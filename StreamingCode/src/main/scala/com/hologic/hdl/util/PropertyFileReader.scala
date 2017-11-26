package com.hologic.hdl.util
import scala.io.Source
import scala.collection.mutable.LinkedHashMap
import org.apache.log4j.Logger
/**-----------------------------------------------------------------------------------
 *This custom object reads the property file and coverts into LinkedHashMap
 *Input 					: Property File
 *Output 					: Return LinkedHashMap holding properties
 *project Name 		: Hologic Data Lake
 *File Name 			: PropertyFileReader.java
 *Author					: Arun Srinivasan
 *Description			: Custom Property File Reader  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	June 21, 2017  			 1.0 				Initial Release
 *Arun Srinivasan			July 24, 2017				 1.1				Changed XML parsing logic
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */
class PropertyFileReader() { 
   
  val logger = Logger.getLogger(getClass.getName)
  
  def createProperty(propFile: String) : LinkedHashMap[String,String] = {
     val lmap = LinkedHashMap[String,String]()
      try { 
        val lines = Source.fromFile("properties/" + propFile).getLines.toArray
        lines.map( x => {
          val m = x.split(":")
          (m(0) -> m(1))}).foreach(x => lmap += x)    
      }
      catch {
       case unknown: Throwable  => {
		    logger.error("General exception occured while reading property files: "+ unknown.getMessage)
		   }
      }
      lmap
   }
}