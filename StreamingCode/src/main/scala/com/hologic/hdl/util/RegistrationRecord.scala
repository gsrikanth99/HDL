package com.hologic.hdl.util

/**
 * This class defines Registration dataset
 */
  /**-----------------------------------------------------------------------------------
 *This  Case object holding values for parsed from XML
 *Input 					: 
 *Output 					: 
 *project Name 		: Hologic Data Lake
 *File Name 			: RegistrationRecord.scala
 *Author					: Arun Srinivasan
 *Description			: RegistrationRecord case class  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           November 08 2017  1.1     added fields for UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */
case class RegistrationRecord(
  correlationid : String,
  hologicid : String,
  transactionid : Long,
  tenantglobal: Boolean, 
  tenantid : Long, 
  tenant : String, 
  id : Long,
  serialnumber : String, 
  name : String, 
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
  modelnumber : String,
  creationtime : String, 
  testmessage : String,
  internal : String, 
  eventsequenceuuid : String, 
  executionstarttime : java.sql.Timestamp, 
  totalexecutiontime : String, 
  queuestarttime : String, 
  queueendtime : String, 
  registrationdate : java.sql.Timestamp, 
  firsttimeregistration : Boolean,
  pingrate : Long, 
  version : Int,
  dateRegistered_UT : String,
  executionStartTime_UT : String,
  registrationDate_UT  : String,
  year : Int,
  month : Int,
  day : Int
)
