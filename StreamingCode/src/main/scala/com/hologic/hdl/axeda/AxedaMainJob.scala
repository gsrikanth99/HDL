package com.hologic.hdl.axeda

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark.sql.hive._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.io.{LongWritable, Text}
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._
import org.apache.log4j.Logger
import java.util.Calendar
import java.text.ParseException
import scala.annotation.switch

import com.hologic.hdl.util._
/**-----------------------------------------------------------------------------------
 *This custom object / java script was written as part of HDL project for parsing xml 
 * data and push it into hive table. 
 *This is a spark streaming job which collects files from HDFS, parses it and loads
 * into hive table 
 *Input 					: XML files from HDFS
 *Output 					: Data loaded and logged in a hive table
 *project Name 		: Hologic Data Lake
 *File Name 			: AxedaMainJob.java
 *Author					: Arun Srinivasan
 *Description			: Spark Streaming code to parse xml files present on 
 * 									HDFS and load into hive table
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	June 21, 2017  			 1.0 				Initial Release
 *Arun Srinivasan			July 24, 2017				 1.1				Changed XML parsing logic
 *Ankit Shah                            August 30,2017                           1.2                          Production Issue -- Axeda Spark job failing # Issue No : 66
 * -----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

object AxedaMainJob {

  /**
   * Driver method that controls the job
   * @param typeOfData : String
   * @return Unit
   */
  def runJob(typeOfData: String){

    val propReader = new PropertyFileReader()
	  val jobProp = propReader.createProperty("axeda-job.properties")
	  val logger = Logger.getLogger(getClass.getName)
	  /*//Reads properties set in applications.properties
	  val appConf = ConfigFactory.load()
	  */

	  //Creates sparkConfiguration and Spark Context object
	  val sparkConf = new SparkConf().setMaster(jobProp("deploymentMaster")).setAppName("AxedaXMLParser")
	  val sc = new SparkContext(sparkConf)

	  //Creates Streaming Context object
	  val ssc = new StreamingContext(sc, Seconds(jobProp("timeInterval").toInt))

	  //Creates Hive Context object
  	val sqlContext = new HiveContext(sc)

	  import sqlContext.implicits._
	  sqlContext.setConf("hive.exec.dynamic.partition", "true")
	  sqlContext.setConf("hive.exec.dynamic.partition.mode", "nonstrict")
	  sqlContext.setConf("hive.enforce.bucketing","true")

	  val processIdfrmt = "HDL-" + typeOfData + "-"

  	//Sets checkpoint directory
	  ssc.checkpoint(jobProp(typeOfData+"CheckpointDir"))
		logger.info("Reading from HDFS location: "+ jobProp(typeOfData+"InputPath"))	
	  //Creating DStream to read from HDFS file path
	  val xmlDStream = ssc.fileStream[LongWritable, Text, TextInputFormat](jobProp(typeOfData+"InputPath"),(path: org.apache.hadoop.fs.Path) => (path.getName.endsWith("xml")),newFilesOnly=true).map(_._2.toString)
	  try {
		  //Parse XML files and push into Hive Table
		  xmlDStream.foreachRDD( rdd => {
			val filteredXMLRDD = rdd.map(xmlLine => {
				val xmlString = scala.xml.XML.loadString(xmlLine)
				val headerNode = xmlString \ "Header"
				val ax_hologicid = (headerNode \ "hologicId" text)
				//filtering data based on presence of hologic id
				if(xmlString.nonEmpty && ax_hologicid.nonEmpty) (xmlString,1) else (xmlString,0)			
			})
			(typeOfData: @switch) match {
			  
			  //processing asset data xml files
				case "asset" => {
					val parsedXMLRDD = filteredXMLRDD.filter(x => (x._2 == 1))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							val xmlString = rec._1
							val xmlParser = new AxedaXMLParser
							val parsedRDD = xmlParser.parseAssetDataXML(xmlString,processId,1)
							(parsedRDD)
						})
					parsedXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputTable"))
					parsedXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
					val erroredXMLRDD = filteredXMLRDD.filter(x => (x._2 == 0))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							processErrorRecord(processId, typeOfData, rec._1.toString)
						})
					erroredXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp("errorTable"))
					erroredXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
				}
				
				//processing alarm data xml files
				case "alarm" => {
					val parsedXMLRDD = filteredXMLRDD.filter(x => (x._2 == 1))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							val xmlString = rec._1
							val xmlParser = new AxedaXMLParser
							val parsedRDD = xmlParser.parseAlarmDataXML(xmlString,processId,1)
							parsedRDD
						})
					parsedXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputTable"))
					parsedXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
					val erroredXMLRDD = filteredXMLRDD.filter(x => (x._2 == 0))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							processErrorRecord(processId, typeOfData, rec._1.toString)
						})
					erroredXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp("errorTable"))
					erroredXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
				}
				
				//processing registration data xml files
				case "registration" =>  {
					val parsedXMLRDD = filteredXMLRDD.filter(x => (x._2 == 1))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							val xmlString = rec._1
							val xmlParser = new AxedaXMLParser
							val parsedRDD = xmlParser.parseRegistrationDataXML(xmlString,processId,1)
							(parsedRDD)
						})
					parsedXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputTable"))
					parsedXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
					val erroredXMLRDD = filteredXMLRDD.filter(x => (x._2 == 0))
						.map( rec => {
							val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
							processErrorRecord(processId, typeOfData, rec._1.toString)
						})
					erroredXMLRDD.map(x => x._1).toDF.write.mode("append").insertInto(jobProp("errorTable"))
					erroredXMLRDD.map(x => x._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
				}
			}
			
		})	 
		ssc.start()              // Start the computation
		ssc.awaitTermination()  // Wait for the computation to terminate
	  }
	  catch {
	    case npEx: NullPointerException => {
	  	  logger.error(npEx.getMessage)
	  	}
		  case pEx: ParseException => {
		  	logger.error(pEx.getMessage)
		  }
		  case unknown: Throwable  => {
		  	logger.error("General exception occured while parsing Case data: "+ unknown.getMessage)
		  }
  	}
	  finally {
	   // ssc.stop(true,true)
	  }
  }
  
  /**
   * Main method where the program is initiated
   * @param args : Array[String]
   * @return Unit
   */
  def processErrorRecord(processId: String,typeOfData: String, xmlString: String): (ErrorRecord, AuditRecord) = {
		val xmlParser = new AxedaXMLParser
		val errorRDD = ErrorRecord(processId, typeOfData, xmlString.toString)
		val auditInfoRDD = xmlParser.getErrorAuditRecord(processId)
		(errorRDD, auditInfoRDD)
  }
  def main(args: Array[String]) {
	  val typeOfData = args(0).toLowerCase 
			runJob(typeOfData);
  }
}
