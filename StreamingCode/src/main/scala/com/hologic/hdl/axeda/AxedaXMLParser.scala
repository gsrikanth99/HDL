package com.hologic.hdl.axeda
import scala.collection.mutable.LinkedHashMap
import scala.collection.mutable.ListBuffer
import scala.xml._
import java.util.Calendar
import com.hologic.hdl.util._

/**-----------------------------------------------------------------------------------
 *This custom object handles parsing xml string and returning a 
 * complex object which holds the values
 *Input 					: XML String
 *Output 					: Return complex object which contains the values in XML
 *project Name 		: Hologic Data Lake
 *File Name 			: AxedaXMLParser.java
 *Author					: Arun Srinivasan
 *Description			: Custom XML Parser for parsing data from salesforce 
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	June 21, 2017  			 1.0 				Initial Release
 *Arun Srinivasan			July 24, 2017				 1.1				Changed XML parsing logic
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

class AxedaXMLParser {
  /**
   * Parse one to one relation tags and defines the relation in a map.
   * Runs recursively till the leaf node and returns a tuple
   * @param node : Node
   * @param xmlMap : LinkedHashMap[String,String]
   * @param xmlList : ListBuffer[LinkedHashMap[String,String]]
   * @return Tuple[String,String]
   */
	def parseOneToOneTags(node: Node, label: String, xmlMap: LinkedHashMap[String,String],xmlList: ListBuffer[LinkedHashMap[String,String]]) : (String, String) = {
			var xmlTuple = ("","")
			for (n <- node.child) {
				if (n.toString.contains("</")) {
					if (!label.equals("LinkedList_DataItemMessage_dataItemsList")) {
						val lb = if(label.nonEmpty) label + "_" +n.label else n.label
						xmlMap += parseOneToOneTags(n,lb,xmlMap,xmlList)
					}
					else {
  					var dataItemMap = LinkedHashMap[String,String]()
							for( m <- n.child){
								dataItemMap+= parseOneToOneTags(m,m.label,dataItemMap,xmlList)
							}
							dataItemMap.remove("")
							xmlList += dataItemMap
						}
					}
					else
						xmlTuple = (label -> n.text)
					}		
	  	xmlTuple
	}
  
	/**
	 * Function to parse the loaded xml String and return an object which holds all the values 
	 * @param xmlString : Elem
	 * @param processId : String
	 * @param processStatus : Int
	 * @return  (AssetRecord, AuditRecord)
	 */
	def parseAssetDataXML(xmlString: Elem, processId: String, processStatus: Int) : (AssetRecord, AuditRecord) = {
	  //LinkedHashMap to store One To One Relation Tags
    var oneToOneMap = LinkedHashMap[String,String]()
  
    //ListBuffer to store One To Many Relation Tags
    var dataItemList = new ListBuffer[LinkedHashMap[String,String]]()  
  
    //Creating a tuple for child node with label as key
	  val parseXMLLevel = xmlString.child.collect{
	    case el:Elem => (el.label, el)
	  }
    
	  for (firstLevel <- parseXMLLevel){
      parseOneToOneTags(firstLevel._2,"",oneToOneMap, dataItemList)
    }
	  val assetObjBuilder = new AssetObjectBuilder
	  val assetObj = assetObjBuilder.createAssetObject(oneToOneMap, dataItemList.toList, processId)
	  assetObj
	}
	
	/**
	 * Function to parse the loaded xml String and return an object which holds all the values 
	 * @param xmlString : Elem
	 * @param processId : String
	 * @param processStatus : Int
	 * @return  (AlarmRecord, AuditRecord)
	 */
	def parseAlarmDataXML(xmlString: Elem, processId: String, processStatus: Int) : (AlarmRecord, AuditRecord) = {
	  //LinkedHashMap to store One To One Relation Tags
    var oneToOneMap = LinkedHashMap[String,String]()
  
    //ListBuffer to store One To Many Relation Tags
    var dataItemList = new ListBuffer[LinkedHashMap[String,String]]()  
  
    //Creating a tuple for child node with label as key
	  val parseXMLLevel = xmlString.child.collect{
	    case el:Elem => (el.label, el)
	  }
    
    //Creating a LinkedHashMap and ListBuffer that holds xml tag data
	  for (firstLevel <- parseXMLLevel){
      parseOneToOneTags(firstLevel._2,"",oneToOneMap, dataItemList)
    }
	  val alarmObjBuilder = new AlarmObjectBuilder
	  val alarmObj = alarmObjBuilder.createAlarmObject(oneToOneMap, dataItemList.toList, processId)
	  alarmObj
	}
	
	/**
	 * Function to parse the loaded xml String and return an object which holds all the values 
	 * @param xmlString : Elem
	 * @param processId : String
	 * @param processStatus : Int
	 * @return  (RegistrationRecord, AuditRecord)
	 */
	def parseRegistrationDataXML(xmlString: Elem, processId: String, processStatus: Int) : (RegistrationRecord, AuditRecord) = {
	  //LinkedHashMap to store One To One Relation Tags
    var oneToOneMap = LinkedHashMap[String,String]()
  
    //ListBuffer to store One To Many Relation Tags
    var dataItemList = new ListBuffer[LinkedHashMap[String,String]]()  
    
    //Creating a tuple for child node with label as key
	  val parseXMLLevel = xmlString.child.collect{
	    case el:Elem => (el.label, el)
	  }
    
    //Creating a LinkedHashMap and ListBuffer that holds xml tag data
	  for (firstLevel <- parseXMLLevel){
      parseOneToOneTags(firstLevel._2,"",oneToOneMap, dataItemList)
    }
	  val registrationObjBuilder = new RegistrationObjectBuilder
	  val regObj = registrationObjBuilder.createRegistrationObject(oneToOneMap, dataItemList.toList, processId)
	  regObj
	}
	
	/**
	 * Function to parse the loaded xml String and return an object which holds all the values 
	 * @param xmlString : Elem
	 * @param processId : String
	 * @param processStatus : Int
	 * @return  (RegistrationRecord, AuditRecord)
	 */
	def getErrorAuditRecord(processId: String): AuditRecord = {
	  val hdl_created_by = System.getProperty("user.name")
	  val hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
	  val hdl_year = Calendar.getInstance().get(Calendar.YEAR)
	  val hdl_month = Calendar.getInstance().get(Calendar.MONTH) + 1
	  val hdl_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	   AuditRecord("", "", "", "", "", "", "", "", "",
	     null, "", "", "", 0, 0, "", "", "", "",
	     null, processId, 0, null, "", "", hdl_created_by, hdl_created_time,
	     hdl_year, hdl_month, hdl_day)
	}
}