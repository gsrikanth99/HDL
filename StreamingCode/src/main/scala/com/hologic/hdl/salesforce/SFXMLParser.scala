package com.hologic.hdl.salesforce

import scala.collection.mutable.LinkedHashMap
import scala.xml._

import java.util.Calendar

import com.hologic.hdl.util._

/**-----------------------------------------------------------------------------------
 *This custom object handles parsing xml string and returning a 
 * complex object which holds the values
 *Input 					: XML String
 *Output 					: Return complex object which contains the values in XML
 *project Name 		: Hologic Data Lake
 *File Name 			: SFXMLParser.java
 *Author					: Arun Srinivasan
 *Description			: Custom XML Parser for parsing data from salesforce 
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

class SFXMLParser {

  /**
   * Parse one to one relation tags and defines the relation in a map.
   * Runs recursively till the leaf node and returns a tuple
   * @param node : Node
   * @param xmlMap : LinkedHashMap[String,String]
   * @return Tuple[String,String]
   */
  def parseOneToOneTags(node: Node, label: String, xmlMap: LinkedHashMap[String,String]) : (String, String) = {
    var xmlTuple = ("","")
    for (n <- node.child) {
      if (n.toString.contains("</")) {
        val lb = if(label.nonEmpty) label + "_" +n.label else n.label
        xmlMap += parseOneToOneTags(n,lb,xmlMap)
      }
      else
        xmlTuple = (label, n.text)
    }
    xmlTuple
  }
  
   /**
   * Parse one to many relation tags and defines the relation in a map
   * where key is a string of parent node and value is list of relations.
   * @param node : Node
   * @return Unit
   */
  def parseOneToManyTags(node: Node, xmlMap: LinkedHashMap[String,List[LinkedHashMap[String,String]]]) : Unit = {
    for(n <- node){
      val label = n.label
      var nodeItemList = List[LinkedHashMap[String,String]]()
      for(m <- n.child){
        var nodeMap = LinkedHashMap[String,String]()
        parseOneToOneTags(m,"",nodeMap)
        nodeItemList ::= nodeMap
      }
      xmlMap += (label -> nodeItemList)
    }
  }
  
  /**
	 * Function to parse the loaded xml String and return an object which holds all the values 
	 * @param xmlString : Elem
	 * @param processId : String
	 * @return ((CaseRecord, AuditRecord),(List[NoteRecord], List[TaskRecord], List[ChargeRecord], List[RMARecord]))
	 */
	def parseCaseDataXML(xmlString: Elem, processId: String) : ((CaseRecord, AuditRecord),(List[NoteRecord], List[TaskRecord], List[ChargeRecord], List[RMARecord])) = {
	  //LinkedHashMap to store One To One Relation Tags
    var OneToOneMap = LinkedHashMap[String,String]()
  
    //LinkedHashMap to store One To Many Relation Tags
    var OneToManyMap = LinkedHashMap[String,List[LinkedHashMap[String,String]]]()  
  
	  val parseXMLLevel = xmlString.child.collect{
	    case el:Elem => (el.label, el)
	  }
	  for (firstLevel <- parseXMLLevel){
	    if(firstLevel._1.equals("ServiceRequestNotes") || firstLevel._1.equals("ServiceRequestTasks")) 
	      parseOneToManyTags(firstLevel._2, OneToManyMap) 
	    else if(firstLevel._1.equals("ServiceRequestLine")) {
	      for(secondLevel <- firstLevel._2.child)
          parseOneToManyTags(secondLevel, OneToManyMap)
	    }
	    else
	      parseOneToOneTags(firstLevel._2,"",OneToOneMap)
	  }
	  val caseObjBuilder = new SFCaseObjectBuilder
	  val caseObj = caseObjBuilder.createCaseObject(OneToOneMap, processId)
	  val SRObj = caseObjBuilder.createServiceRequestOneToManyObject(OneToManyMap)
	  (caseObj,SRObj)
	}
	
}