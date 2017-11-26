package com.hologic.hdl.util

/**
 * This class defines Alarm dataset
 */
  /**-----------------------------------------------------------------------------------
 *This  Case object holding values for parsed from XML
 *Input 					: 
 *Output 					: 
 *project Name 		: Hologic Data Lake
 *File Name 			: AlarmRecord.scala
 *Author					: Arun Srinivasan
 *Description			: AlarmRecord case class  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           November 08 2017  1.1     added fields for UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */
case class AlarmRecord (
  correlationid : String,
  hologicid : String,
  transactionid : Long,
  tenantglobal: Boolean, 
  tenantid : Long, 
  tenant : String, 
  id : Long,
  serialnumber : String, 
  name : String,
  pingrate : Long, 
  missing : Boolean,
  offline : Boolean, 
  gatewaydeviceoffline : String,
  invalidtoken : Boolean,
  connector : Long, 
  dateregistered : java.sql.Timestamp,
  timezoneid : String, 
  tokenrequired : Boolean,
  customerid : Long, 
  locationid : Long,
  modelid : Long, 
  conditionid : Long,
  isbackupenabled : Boolean, 
  family : String,
  networkaccesibleflg : Boolean, 
  protocolid : String,
  categorycode : String, 
  retrycount : String,
  retryinterval : String, 
  muted : Boolean,
  notesmodified : String,
  almid : Long, 
  associationid : Long, 
  almname : String, 
  date : java.sql.Timestamp, 
  active :Boolean, 
  storehistory : String, 
  severity : Int, 
  assignedtoIdtype : String, 
  notesupdatedby : String, 
  notesupdateddate : java.sql.Timestamp, 
  alarmid : Long, 
  alarmstate : String, 
  sourcetype : String, 
  sourcedetails : String,
  dateRegistered_UT : String,
  alarmdate_UT  :  String,
  year : Int,
  month : Int,
  day : Int
)
