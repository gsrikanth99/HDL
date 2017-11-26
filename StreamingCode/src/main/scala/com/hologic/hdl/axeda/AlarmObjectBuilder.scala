package com.hologic.hdl.axeda

import scala.collection.mutable.LinkedHashMap
import scala.xml._
import java.text.ParseException
import java.sql.Timestamp
import java.util.Calendar
import org.apache.log4j.Logger
import com.hologic.hdl.util._
import java.text.SimpleDateFormat;
import java.util.{TimeZone, Date};

/**-----------------------------------------------------------------------------------
 *This custom object create Alarm object holding values parsed from XML
 *Input 			: Linked Hash Map
 *Output 			: Return complex object which contains the values in Map
 *project Name 		: Hologic Data Lake
 *File Name 		: AlarmObjectBuilder.java
 *Author			: Arun Srinivasan
 *Description		: Custom Object Builder  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 24, 2017  			 1.1 				Custom object for alarm data
 * Chinmaya R           November 08 2017                 1.2                            Changed the date format and added UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

class AlarmObjectBuilder {
  val logger = Logger.getLogger(getClass.getName)
  //Date format as per XML data
	val dtformat = new java.text.SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS z")
  	//Storing username
	var hdl_created_by = System.getProperty("user.name")
	
	//Storing year, month and day for partitioning
	//val hdl_year = Calendar.getInstance().get(Calendar.YEAR)
	//val hdl_month = Calendar.getInstance().get(Calendar.MONTH) + 1
	//val hdl_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	
	/**
	 * Create case class object for AssetAlarm from LinkedHashMap and List
	 * @param xmlMap : LinkedHashMap[String,String]
	 * @param xmlList : List[LinkedHashMap[String,String]]
	 * @param processId : String
	 * @return (AlarmRecord, AuditRecord)
	 */
	def createAlarmObject(xmlMap: LinkedHashMap[String,String], xmlList: List[LinkedHashMap[String,String]],processId : String) : (AlarmRecord, AuditRecord) ={
    (AlarmRecord(
        checkKeyExistAndGetValue(xmlMap, "correlationId"), 
        checkKeyExistAndGetValue(xmlMap, "hologicId"),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "transactionId")),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_tenantGlobal")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_tenantId")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_tenant"),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_id_value")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_serialNumber"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_name"),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_pingRate")),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_missing")),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_offline")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_gatewayDeviceOffline"),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_invalidToken")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_connector")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_dateRegistered")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_timeZoneId"),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_tokenRequired")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_customerId")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_locationId")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_modelId")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_conditionId")),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_isBackupEnabled")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_family"),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_networkAccessible")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_protocolId"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_categoryCode"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_retryCount"),
        checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_retryInterval"),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_muted")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_Device_device_notesModified"),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_id_value")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_associationId_value")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_name"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_date")),
			  parseAndConvertToBoolean(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_active")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_storeHistory"),
			  parseAndConvertToInt(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_severity")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_assignedToIdType"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_notesUpdatedBy"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_notesUpdatedDate")),
			  parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_alarmId_value")),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_alarmState"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_sourceType"),
			  checkKeyExistAndGetValue(xmlMap, "LinkedList_AlarmImpl_alarm_sourceDetails"),
			  //Calendar.getInstance().get(Calendar.YEAR),
			  //Calendar.getInstance().get(Calendar.MONTH) + 1,
			  //Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
        ),
     AuditRecord(
        checkKeyExistAndGetValue(xmlMap, "correlationId"), 
        checkKeyExistAndGetValue(xmlMap, "applicationName"),
			  checkKeyExistAndGetValue(xmlMap, "company"),
			  checkKeyExistAndGetValue(xmlMap, "country"),
			  checkKeyExistAndGetValue(xmlMap, "division"),
        checkKeyExistAndGetValue(xmlMap, "group"),
        checkKeyExistAndGetValue(xmlMap, "location"), 
        checkKeyExistAndGetValue(xmlMap, "domain"),
			  checkKeyExistAndGetValue(xmlMap, "userId"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "timeStamp")),
			  checkKeyExistAndGetValue(xmlMap, "hologicId"),
        checkKeyExistAndGetValue(xmlMap, "sourceIp"),
        checkKeyExistAndGetValue(xmlMap, "sourceReferenceId"),
        parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "transactionId")),
        parseAndConvertToLong(checkKeyExistAndGetValue(xmlMap, "errorId")), 
        checkKeyExistAndGetValue(xmlMap, "errorLocation"),
			  checkKeyExistAndGetValue(xmlMap, "errorMessage"),
			  checkKeyExistAndGetValue(xmlMap, "errorDescription"),
			  checkKeyExistAndGetValue(xmlMap, "errorStack"),
        parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "errorTimeStamp")),
        processId, 
        1,
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "timeRecevied")),
			  checkKeyExistAndGetValue(xmlMap, "consumerIp"),
			  checkKeyExistAndGetValue(xmlMap, "consumerUserId"),
        System.getProperty("user.name"),
        new java.sql.Timestamp(Calendar.getInstance().getTime().getTime()),
       / Calendar.getInstance().get(Calendar.YEAR),
			//  Calendar.getInstance().get(Calendar.MONTH) + 1,
			//  Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
		          parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
         )
     )
  }
//Method for date UTC conversion
def parseAndConvertToUtc(x: String) : String ={
val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
val format =  if (x.nonEmpty) {new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").parse(x.trim) } else null;
val dateUtc = formatter.format(format);
dateUtc;
}
  def parseAndConvertToDate(x: String)= if (x.nonEmpty) new java.text.SimpleDateFormat("yyyy-MM-dd").parse(x.trim) else null
  def parseAndConvertToTimestamp(x: String) : Timestamp = if (x.nonEmpty) new java.sql.Timestamp(dtformat.parse(x.trim).getTime()) else null
	def parseAndConvertToTimestampDT(x: String, dtfrmt: java.text.SimpleDateFormat) : Timestamp = if (x.nonEmpty) new java.sql.Timestamp(dtfrmt.parse(x.trim).getTime()) else null
	def parseAndConvertToDouble(x: String) : Double = if (x.nonEmpty) x.trim.toDouble else 0
	def parseAndConvertToLong(x: String) : Long =  if (x.nonEmpty) x.trim.toLong else 0
  def parseAndConvertToBoolean(x: String) : Boolean =  if (x.nonEmpty) x.trim.toBoolean else false
  def parseAndConvertToInt(x: String) : Int =  if (x.nonEmpty) x.trim.toInt else 0
	def checkKeyExistAndGetValue(xmlMap: LinkedHashMap[String,String], key : String): String = if(xmlMap.contains(key)) xmlMap(key) else ""
}
