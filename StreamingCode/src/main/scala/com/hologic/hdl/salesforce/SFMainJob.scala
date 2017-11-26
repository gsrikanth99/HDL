package com.hologic.hdl.salesforce

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.hive._
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.hadoop.conf._
import org.apache.hadoop.fs._
import org.apache.log4j.Logger
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat
import org.apache.hadoop.io.{LongWritable, Text}
import scala.annotation.switch
import java.util.Calendar
import java.text.ParseException
import com.hologic.hdl.util._
/**-----------------------------------------------------------------------------------
 *This custom object / java script was written as part of HDL project for parsing xml 
 * data and push it into hive table. 
 *This is a spark streaming job which collects files from HDFS, parses it and loads
 * into hive table 
 *Input 					: XML files from HDFS
 *Output 					: Data loaded and logged in a hive table
 *project Name 		: Hologic Data Lake
 *File Name 			: SFMainJob.java
 *Author					: Arun Srinivasan
 *Description			: Spark Streaming code to parse xml files present on 
 * 									HDFS and load into hive table
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           August 30, 2017                  1.1                            Production Issue -- Salesforce Spark job failing # Issue No : 68
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

object SFMainJob {

  /**
   * Driver method that controls the job
   * @param typeOfData : String
   * @return Unit
   */
  def runJob(typeOfData: String){
 
    //Reads job properties
    val propReader = new PropertyFileReader()
	  val jobProp = propReader.createProperty("salesforce-job.properties")

	  val logger = Logger.getLogger(getClass.getName)

	  //Creates sparkConfiguration and Spark Context object
	  val sparkConf = new SparkConf().setMaster(jobProp("deploymentMaster")).setAppName("SalesforceXMLParser")
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
	   //Parse xml and push data into hive
	   (typeOfData: @switch) match {
	    case "case" => {
	      xmlDStream.foreachRDD( rdd => {
			  	val parsedXMLRDD = rdd.map(xmlLine => {
			    //Loading xml String as Elem object
			  	val xmlString = scala.xml.XML.loadString(xmlLine)
		  		//Generating Process ID
	  			val processId = processIdfrmt + java.util.UUID.randomUUID.toString + "-" + Calendar.getInstance().getTimeInMillis
	  			//Parse Case XML Data
	  			val xmlParser = new SFXMLParser
	  			xmlParser.parseCaseDataXML(xmlString,processId)
	  		})
	  		
		  	parsedXMLRDD.map(x => x._1._1).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputTable"))
	
	  		parsedXMLRDD.map(x => x._2._1).flatMap(x => x).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputNotesTable"))
	  	
	  		parsedXMLRDD.map(x => x._2._2).flatMap(x => x).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputTasksTable"))
	  
	  		parsedXMLRDD.map(x => x._2._3).flatMap(x => x).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputChargeTable"))
	  		
	  		parsedXMLRDD.map(x => x._2._4).flatMap(x => x).toDF.write.mode("append").insertInto(jobProp(typeOfData+"OutputRMATable"))
	  		
		  	parsedXMLRDD.map(x => x._1._2).toDF.write.mode("append").insertInto(jobProp("logTable"))
	  	})
	    }        
     }
	     ssc.start()              // Start the computation
	     ssc.awaitTermination()   // Wait for the computation to terminate
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
  def main(args: Array[String]) {
	  val typeOfData = if (args.length < 1) "case" else args(0).toLowerCase 
			runJob(typeOfData);
  }
}
