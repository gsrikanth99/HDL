package com.hologic.hdl.salesforce

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
 *This custom object create Case object holding values parsed from XML
 *Input 					: Linked Hash Map
 *Output 					: Return complex object which contains the values in Map
 *project Name 		: Hologic Data Lake
 *File Name 			: SFCaseObjectBuilder.java
 *Author					: Arun Srinivasan
 *Description			: Custom Object Builder  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           November 08 2017                 1.2                            Changed the date format and added UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */

class SFCaseObjectBuilder {
  
  val logger = Logger.getLogger(getClass.getName)
  //Date format as per XML data
	val dtformat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'")
	
	//Storing username
	var hdl_created_by = System.getProperty("user.name")
	val hologicID = java.util.UUID.randomUUID.toString
	var srcObjID = ""
	
	//Storing year, month and day for partitioning
	//val hdl_year = Calendar.getInstance().get(Calendar.YEAR)
	//val hdl_month = Calendar.getInstance().get(Calendar.MONTH) + 1
	//val hdl_day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
	
	/**
	 * Create complex case object by getting values from OneToOne Map
	 * @param xmlMap : LinkedHashMap[String,String]
	 * @param processId : String
	 * @return (CaseRecord, AuditRecord)
	 */
  def createCaseObject(xmlMap: LinkedHashMap[String,String], processId: String) : (CaseRecord, AuditRecord) = {
	  val caseObjRecord = createCaseRecordObject(xmlMap)
    val auditInfoRecord = createAuditInfoObj(xmlMap,1,processId)
	  (caseObjRecord, auditInfoRecord)
	}
	
	/**
	 * Create complex case object by getting values from OneToMany Map
	 * @param xmlMap : LinkedHashMap[String,List[LinkedHashMap[String,String]]]
	 * @return (List[NoteRecord], List[TaskRecord], List[ChargeRecord], List[RMARecord])
	 */
	def createServiceRequestOneToManyObject(xmlMap: LinkedHashMap[String,List[LinkedHashMap[String,String]]]) : (List[NoteRecord], List[TaskRecord], List[ChargeRecord], List[RMARecord]) = {
	  var noteRecordList = List[NoteRecord]()
	  var taskRecordList = List[TaskRecord]()
	  var chargeRecordList = List[ChargeRecord]()
	  var rmaRecordList = List[RMARecord]()
	  try {
	    //Custom object for SRNotes Tag
		  if(xmlMap.contains("ServiceRequestNotes") && xmlMap("ServiceRequestNotes").size > 0){
			  val noteTagList = xmlMap("ServiceRequestNotes").filter(x => x.size !=0)
					  noteRecordList = noteTagList.map( x => createNoteObject(x) )
		  }
		  //Custom object for SRTasks Tag
		  if(xmlMap.contains("ServiceRequestTasks") && xmlMap("ServiceRequestTasks").size > 0){
			  val taskTagList = xmlMap("ServiceRequestTasks").filter(x => x.size !=0)
					  taskRecordList = taskTagList.map( x => createTaskObject(x) )
		  }
		  //Custom object for SRCharge Tag
		  if(xmlMap.contains("ServiceRequestCharge") && xmlMap("ServiceRequestCharge").size > 0){
			  val chargeTagList = xmlMap("ServiceRequestCharge").filter(x => x.size !=0)
					  chargeRecordList = chargeTagList.map( x => createChargeObject(x) )
		  }
		  //Custom object for SRRMA Tag
		  if(xmlMap.contains("ServiceRequestRMA") && xmlMap("ServiceRequestRMA").size > 0){
			  val rmaTagList = xmlMap("ServiceRequestRMA").filter(x => x.size !=0)
					  rmaRecordList = rmaTagList.map( x => createRMAObject(x) )
		  }
	  }
	  catch{
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
	  (noteRecordList, taskRecordList, chargeRecordList, rmaRecordList)
	}
	
	/**
	 * Create case class object for Case SRHeader from LinkedHashMap 
	 * @param xmlMap: LinkedHashMap[String,String]
	 * @return CaseRecord
	 */
	def createCaseRecordObject(xmlMap: LinkedHashMap[String,String]): CaseRecord = {
	  var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
	  srcObjID = checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId")
	  CaseRecord(
	      hologicID,
			  checkKeyExistAndGetValue(xmlMap, "Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestId"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestNumber"),
			  checkKeyExistAndGetValue(xmlMap, "CallerFirstName"),
			  checkKeyExistAndGetValue(xmlMap, "CallerLastName"),
			  checkKeyExistAndGetValue(xmlMap, "CallerCentralIdentity"),
			  checkKeyExistAndGetValue(xmlMap, "CallerPhone"),
			  checkKeyExistAndGetValue(xmlMap, "CallerPhoneExt"),
			  checkKeyExistAndGetValue(xmlMap, "CallerEmail"),
			  checkKeyExistAndGetValue(xmlMap, "Status"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestFirstName"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestLastName"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestEmail"),
			  checkKeyExistAndGetValue(xmlMap, "OracleServiceRequestPhoneNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ProblemCode"),
			  checkKeyExistAndGetValue(xmlMap, "ProblemSummary"),
			  checkKeyExistAndGetValue(xmlMap, "Category"),
			  checkKeyExistAndGetValue(xmlMap, "SubCategory"),
			  checkKeyExistAndGetValue(xmlMap, "Type"),
			  checkKeyExistAndGetValue(xmlMap, "Severity"),
			  checkKeyExistAndGetValue(xmlMap, "SRNumber"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "ReportedDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "IncidentDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "ClosedOn")),
			  parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "InstrumentInstallDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")),
			  checkKeyExistAndGetValue(xmlMap, "SROwner"),
			  checkKeyExistAndGetValue(xmlMap, "AssignedTo"),
			  checkKeyExistAndGetValue(xmlMap, "LoggedBy"),
			  checkKeyExistAndGetValue(xmlMap, "LoggedByName"),
			  checkKeyExistAndGetValue(xmlMap, "CreatedByName"),
			  checkKeyExistAndGetValue(xmlMap, "SRChannel"),
			  checkKeyExistAndGetValue(xmlMap, "BillToPartyName"),
			  checkKeyExistAndGetValue(xmlMap, "BillToPartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Type"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_PartyName"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_PartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_SiteNumber"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_AddressLine1"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_AddressLine2"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_AddressLine3"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_AddressLine4"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_City"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_County"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_State"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_PostalCode"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_PostalCodeExt"),
			  checkKeyExistAndGetValue(xmlMap, "BillToLocation_Location_Location_Country"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToPartyName"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToPartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Type"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_PartyName"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_PartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_SiteNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_AddressLine1"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_AddressLine2"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_AddressLine3"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_AddressLine4"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_City"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_County"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_State"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_PostalCode"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_PostalCodeExt"),
			  checkKeyExistAndGetValue(xmlMap, "ShipToLocation_Location_Location_Country"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtPartyName"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtPartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Type"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_PartyName"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_PartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_SiteNumber"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_AddressLine1"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_AddressLine2"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_AddressLine3"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_AddressLine4"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_City"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_County"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_State"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_PostalCode"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_PostalCodeExt"),
			  checkKeyExistAndGetValue(xmlMap, "InstalledAtLocation_Location_Location_Country"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedPartyName"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByPartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Type"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_PartyName"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_PartyNumber"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_SiteNumber"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_AddressLine1"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_AddressLine2"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_AddressLine3"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_AddressLine4"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_City"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_County"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_State"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_PostalCode"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_PostalCodeExt"),
			  checkKeyExistAndGetValue(xmlMap, "OwnedByLocation_Location_Location_Country"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_ItemInstanceNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_ItemCategory"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_SerialNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_ItemDescription"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceItem_ServiceItem_ItemCode"),
			  checkKeyExistAndGetValue(xmlMap, "ComplaintFlag"),
			  checkKeyExistAndGetValue(xmlMap, "ErrorCode"),
			  checkKeyExistAndGetValue(xmlMap, "Urgency"),
			  checkKeyExistAndGetValue(xmlMap, "SeverityCategory"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "CustomerAccount_CaseCustomer_CustomerAccountName"),
			  checkKeyExistAndGetValue(xmlMap, "ContactId"),
			  checkKeyExistAndGetValue(xmlMap, "EntitlementId"),
			  checkKeyExistAndGetValue(xmlMap, "HasCommentsUnreadByOwner"),
			  checkKeyExistAndGetValue(xmlMap, "HasSelfServiceComments"),
			  checkKeyExistAndGetValue(xmlMap, "IsEscalated"),
			  checkKeyExistAndGetValue(xmlMap, "EscalationLevel"),
			  checkKeyExistAndGetValue(xmlMap, "IsDeleted"),
			  checkKeyExistAndGetValue(xmlMap, "IsSelfServiceClosed"),
			  checkKeyExistAndGetValue(xmlMap, "IsStopped"),
			  checkKeyExistAndGetValue(xmlMap, "IsVisibleInSelfService"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "LastReferencedDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "LastViewedDate")),
			  checkKeyExistAndGetValue(xmlMap, "MilestoneStatus"),
			  checkKeyExistAndGetValue(xmlMap, "Origin"),
			  checkKeyExistAndGetValue(xmlMap, "ParentId"),
			  checkKeyExistAndGetValue(xmlMap, "Priority"),
			  checkKeyExistAndGetValue(xmlMap, "QuestionId"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "SlaExitDate")),
			  checkKeyExistAndGetValue(xmlMap, "SlaStartDate"),
			  checkKeyExistAndGetValue(xmlMap, "StopStartDate"),
			  checkKeyExistAndGetValue(xmlMap, "SuppliedCompany"),
			  checkKeyExistAndGetValue(xmlMap, "SuppliedEmail"),
			  checkKeyExistAndGetValue(xmlMap, "SuppliedName"),
			  checkKeyExistAndGetValue(xmlMap, "SuppliedPhone"),
			  checkKeyExistAndGetValue(xmlMap, "SystemModstamp"),
			  checkKeyExistAndGetValue(xmlMap, "ContractId"),
			  checkKeyExistAndGetValue(xmlMap, "ContractDocId"),
			  checkKeyExistAndGetValue(xmlMap, "ContractDocNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ContractNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ContractLineId"),
			  checkKeyExistAndGetValue(xmlMap, "ContractLineNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceLineNumber"),
			  checkKeyExistAndGetValue(xmlMap, "ServiceName"),
			  checkKeyExistAndGetValue(xmlMap, "ResolutionCode"),
			  checkKeyExistAndGetValue(xmlMap, "ResolutionSummary"),
			  checkKeyExistAndGetValue(xmlMap, "UpdatedBy"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "UpdatedDate")),
			  checkKeyExistAndGetValue(xmlMap, "PatientImpact"),
			  checkKeyExistAndGetValue(xmlMap, "Investigation"),			  
			  checkKeyExistAndGetValue(xmlMap, "OperationCodeLevel1"),
			  checkKeyExistAndGetValue(xmlMap, "OperationCodeLevel2"),
			  checkKeyExistAndGetValue(xmlMap, "OperationCodeLevel3"),
			  hdl_created_by, 
				hdl_created_time,
		          parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "InstrumentInstallDate")),
			  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "SlaStartDate")),
			  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "StopStartDate")),
			  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "UpdatedDate")),
		          parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
			  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
			//	hdl_year, 
			//	hdl_month,
       // hdl_day
	      )			  
	}
	
  /**
	 * Create case class object for Case SRCharge from LinkedHashMap 
	 * @param xmlMap: LinkedHashMap[String,String]
	 * @return ChargeRecord
	 */
	def createChargeObject(xmlMap: LinkedHashMap[String,String]) : ChargeRecord = {
	  var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
		ChargeRecord(
		    srcObjID,
				checkKeyExistAndGetValue(xmlMap, "Identification_Id"),
				checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId"),
				checkKeyExistAndGetValue(xmlMap, "Identification_SourceParentObjectId"),
				checkKeyExistAndGetValue(xmlMap, "Identification_Number"),
				checkKeyExistAndGetValue(xmlMap, "Identification_Revision"),
				checkKeyExistAndGetValue(xmlMap, "Identification_QueueRecordId"),
				checkKeyExistAndGetValue(xmlMap, "Identification_CommonId"),
				checkKeyExistAndGetValue(xmlMap, "OrderNumber"),
				checkKeyExistAndGetValue(xmlMap, "Item"),
				checkKeyExistAndGetValue(xmlMap, "ItemDescription"),
				parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Quantity")),
				checkKeyExistAndGetValue(xmlMap, "ProdcutName"),
				parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "OrderItemNumber")),
				checkKeyExistAndGetValue(xmlMap, "OrderStatus"),
				checkKeyExistAndGetValue(xmlMap, "OrderProductStatus"),
				parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "EffectiveDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")),
				parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "RMAReceivedDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")),
				checkKeyExistAndGetValue(xmlMap, "RMASiteReceived"),
				checkKeyExistAndGetValue(xmlMap, "ProductDescription"),
				checkKeyExistAndGetValue(xmlMap, "ReturnReasonCode"),
				checkKeyExistAndGetValue(xmlMap, "OwnerIdOperatingUnit"),
				checkKeyExistAndGetValue(xmlMap, "AssetSerialNumber"),
				checkKeyExistAndGetValue(xmlMap, "RMANumber"),
				checkKeyExistAndGetValue(xmlMap, "IntlCallNumber"),
				checkKeyExistAndGetValue(xmlMap, "BillingType"),
				checkKeyExistAndGetValue(xmlMap, "OrderRecordType"),
				checkKeyExistAndGetValue(xmlMap, "OrderLineType"),
				checkKeyExistAndGetValue(xmlMap, "RmaFlag"),
				checkKeyExistAndGetValue(xmlMap, "PartFlag"), 
				hdl_created_by, 
				hdl_created_time,
				//hdl_year, 
			//	hdl_month,
      //  hdl_day
       parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "EffectiveDate")),
	  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "RMAReceivedDate")),
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
			)
	}
	
	/**
	 * Create case class object for Case SRNotes from LinkedHashMap
	 * @param xmlMap: LinkedHashMap[String,String]
	 * @return NoteRecord
	 */
	def createNoteObject(xmlMap: LinkedHashMap[String,String]) : NoteRecord = {
	  var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
	  NoteRecord(
	      srcObjID,
	      checkKeyExistAndGetValue(xmlMap, "Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "ParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "UserName"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "CreationDate")),
			  checkKeyExistAndGetValue(xmlMap, "NoteStatusMeaning"),
			  checkKeyExistAndGetValue(xmlMap, "CreatedBy"),
			  checkKeyExistAndGetValue(xmlMap, "CreatedByName"),
			  checkKeyExistAndGetValue(xmlMap, "isPublished"),
			  checkKeyExistAndGetValue(xmlMap, "isDeleted"),
			  checkKeyExistAndGetValue(xmlMap, "CreatorFullPhotoUrl"),
			  checkKeyExistAndGetValue(xmlMap, "CreatorSmallPhotoUrl"),
			  checkKeyExistAndGetValue(xmlMap, "NoteTypeMeaning"),
			  checkKeyExistAndGetValue(xmlMap, "Note"),
			  checkKeyExistAndGetValue(xmlMap, "LastUpdateByName"),
			  checkKeyExistAndGetValue(xmlMap, "LastUpdateById"),
			  checkKeyExistAndGetValue(xmlMap, "LastUpdateDate"),
			  hdl_created_by, 
				hdl_created_time,
				parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "CreationDate")),
			  parseAndConvertToUtc(xmlMap, "LastUpdateDate"),
			   parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
			//	hdl_year, 
			//	hdl_month,
       // hdl_day
			  )
	}
	
	/**
	 * Create case class object for Case SRTasks from LinkedHashMap
	 * @param xmlMap: LinkedHashMap[String,String]
	 * @return TaskRecord
	 */
	def createTaskObject(xmlMap: LinkedHashMap[String,String]) : TaskRecord = {
	  var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
	  TaskRecord(
	      srcObjID,
	      checkKeyExistAndGetValue(xmlMap, "Identification_Id"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_SourceParentObjectId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Number"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_Revision"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_QueueRecordId"),
			  checkKeyExistAndGetValue(xmlMap, "Identification_CommonId"),
			  checkKeyExistAndGetValue(xmlMap, "TaskName"),
			  checkKeyExistAndGetValue(xmlMap, "TaskStatus"),
			  checkKeyExistAndGetValue(xmlMap, "TaskPriority"),
			  checkKeyExistAndGetValue(xmlMap, "TaskAssignmentStatus"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "TaskAssignmentId")),
			  checkKeyExistAndGetValue(xmlMap, "TaskType"),
			  checkKeyExistAndGetValue(xmlMap, "TaskDescription"),
			  checkKeyExistAndGetValue(xmlMap, "TaskComment"),
			  checkKeyExistAndGetValue(xmlMap, "TaskAddress"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "IncidentId")),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "ResourceId")),
			  checkKeyExistAndGetValue(xmlMap, "Assignee"),
			  checkKeyExistAndGetValue(xmlMap, "ResourceTypeCode"),
			  checkKeyExistAndGetValue(xmlMap, "AssigneeType"),
			  checkKeyExistAndGetValue(xmlMap, "OwnerId"),
			  checkKeyExistAndGetValue(xmlMap, "OwnerType"),
			  checkKeyExistAndGetValue(xmlMap, "OwnerTypeCode"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskPlannedStartDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskPlannedEndDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskScheduleStartDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskScheduleEndDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskActualStartDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "TaskActualEndDate")),
			  checkKeyExistAndGetValue(xmlMap, "DebriefNumber"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "DebriefHeaderId")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "DebriefDate")),
			  checkKeyExistAndGetValue(xmlMap, "DebriefStatus"),
			  checkKeyExistAndGetValue(xmlMap, "DebriefSerialNumber"),
			  checkKeyExistAndGetValue(xmlMap, "DebriefCategory"),
			  checkKeyExistAndGetValue(xmlMap, "PrivateFlag"),
			  checkKeyExistAndGetValue(xmlMap, "PublishFlag"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "PlannedEffort")),
			  checkKeyExistAndGetValue(xmlMap, "PlannedEffortUOM"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "ActualEffort")),
			  checkKeyExistAndGetValue(xmlMap, "ActualEffortUOM"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Duration")),
			  checkKeyExistAndGetValue(xmlMap, "DurationUOM"),
			  checkKeyExistAndGetValue(xmlMap, "DebriefProcessedFlag"),
			  checkKeyExistAndGetValue(xmlMap, "LaborEndingMileage"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourActivityCode"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourActivityDesc"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourItem"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourItemDescription"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourDuration"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourUOM"),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourServiceDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourStartDate")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourEndDate")),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourStartMileage"),
			  checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourEndMileage"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialActivityCode"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialUsed"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialUsedDescription"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialUOM"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialQuantity")),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialSubInventory")),
			  parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialServiceDate")),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialLocator"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialSerialNumber"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialLotNumber"),
			  checkKeyExistAndGetValue(xmlMap, "Material_Material_MaterialInstanceNumber"),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseActivityCode"),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseItem"),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseItemDescription"),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseUOM"),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseQuantity")),
			  parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseItemAmount")),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseCurrencyCode"),
			  checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseServiceDate"),
			  hdl_created_by, 
				hdl_created_time,
				parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourServiceDate")),
			  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourStartDate")),
			  parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "Labour_Labour_LabourEndDate")),
			   parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "Expense_Expense_ExpenseServiceDate")),
			   	  			   parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
				//hdl_year, 
				//hdl_month,
       // hdl_day
       
	      )
	}
	
	/**
	 * Create case class object for Case SRRMA from LinkedHashMap
	 * @param xmlMap: LinkedHashMap[String,String]
	 * @return RMARecord
	 */
	def createRMAObject(xmlMap: LinkedHashMap[String,String]) : RMARecord = {
	  
	  var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
			RMARecord(
			    srcObjID,
					checkKeyExistAndGetValue(xmlMap, "Identification_Id"),
					checkKeyExistAndGetValue(xmlMap, "Identification_SourceObjectId"), 
					checkKeyExistAndGetValue(xmlMap, "Identification_SourceParentObjectId"), 
					checkKeyExistAndGetValue(xmlMap, "Identification_Number"), 
					checkKeyExistAndGetValue(xmlMap, "Identification_Revision"), 
					checkKeyExistAndGetValue(xmlMap, "Identification_QueueRecordId"), 
					checkKeyExistAndGetValue(xmlMap, "Identification_CommonId"), 
					checkKeyExistAndGetValue(xmlMap, "OrderNumber"), 
					checkKeyExistAndGetValue(xmlMap, "OrderReferenceNumber"), 
					checkKeyExistAndGetValue(xmlMap, "Item"), 
					checkKeyExistAndGetValue(xmlMap, "ItemDescription"), 
					parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "Quantity")), 
					checkKeyExistAndGetValue(xmlMap, "ProdcutName"), 
					parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "OrderItemNumber")), 
					checkKeyExistAndGetValue(xmlMap, "OrderStatus"), 
					checkKeyExistAndGetValue(xmlMap, "OrderProductStatus"), 
					parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "EffectiveDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")), 
					parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "RMAReceivedDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")), 
					parseAndConvertToTimestamp(checkKeyExistAndGetValue(xmlMap, "RMAUpdateDate")), 
					checkKeyExistAndGetValue(xmlMap, "RMASiteReceived"), 
					checkKeyExistAndGetValue(xmlMap, "ProductDescription"), 
					checkKeyExistAndGetValue(xmlMap, "ReturnReason"), 
					checkKeyExistAndGetValue(xmlMap, "OwnerIdOperatingUnit"), 
					checkKeyExistAndGetValue(xmlMap, "AssetSerialNumber"), 
					checkKeyExistAndGetValue(xmlMap, "RMANumber"), 
					parseAndConvertToDouble(checkKeyExistAndGetValue(xmlMap, "RMALineNumber")), 
					checkKeyExistAndGetValue(xmlMap, "IncidentNumber"), 
					checkKeyExistAndGetValue(xmlMap, "InvoiceNumber"), 
					checkKeyExistAndGetValue(xmlMap, "IntlCallNumber"), 
					checkKeyExistAndGetValue(xmlMap, "BillingType"), 
					checkKeyExistAndGetValue(xmlMap, "OrderRecordType"), 
					checkKeyExistAndGetValue(xmlMap, "OrderLineType"), 
					checkKeyExistAndGetValue(xmlMap, "OperatingUnitCode"), 
					checkKeyExistAndGetValue(xmlMap, "OperatingUnitName"), 
					parseAndConvertToTimestampDT(checkKeyExistAndGetValue(xmlMap, "OrderDate"),new java.text.SimpleDateFormat("yyyy-mm-dd")),
					checkKeyExistAndGetValue(xmlMap, "serviceActivity"), 
					checkKeyExistAndGetValue(xmlMap, "RmaFlag"), 
					checkKeyExistAndGetValue(xmlMap, "PartFlag"), 
					checkKeyExistAndGetValue(xmlMap, "CreatedByFederationId"), 
					checkKeyExistAndGetValue(xmlMap, "CreatedBySystemAlias"), 
					checkKeyExistAndGetValue(xmlMap, "CreatedByName"), 
					checkKeyExistAndGetValue(xmlMap, "UpdatedByFederationId"), 
					checkKeyExistAndGetValue(xmlMap, "UpdatedBySystemAlias"), 
					checkKeyExistAndGetValue(xmlMap, "UpdatedByName"), 
					hdl_created_by, 
					hdl_created_time,
					parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "EffectiveDate")), 
					parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "RMAReceivedDate")), 
					parseAndConvertToUtc(checkKeyExistAndGetValue(xmlMap, "RMAUpdateDate")), 
					parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getYear()+1900,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getMonth()+1,
	  parseAndConvertToDate((checkKeyExistAndGetValue(xmlMap, "timeRecevied"))).getDate()
				//	hdl_year, 
				//	hdl_month,
        //  hdl_day
				)
	}

	
	/**
	 * Function to parse audit information from the xml.Function to parse audit data
	 * @param xmlMap : LinkedHashMap[String,String]
	 * @param processflg: Int
	 * @param processId : String
	 * @return AuditRecord
	 */
	def createAuditInfoObj(xmlMap: LinkedHashMap[String,String], processflg: Int, processId: String): AuditRecord = {
	  val dtformat = new java.text.SimpleDateFormat("yyyy-mm-dd hh:mm:ss.SSS z")
	  
	  val ax_correlationid = hologicID
	  val ax_applicationname = "SF Case"
	  val ax_company = "Hologic"
	  val ax_country = "Marlborough"
	  val ax_division = "Hologic"
		val ax_group = "Hologic"
		val ax_location = "Marlborough"
		val ax_domain = "SF_DATA"
		val ax_userid = ""
		val ax_timestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
		val ax_hologicid = hologicID
		val ax_srcip = ""
		val ax_srcrefid = ""
		val ax_transid = Calendar.getInstance().getTime().getTime()

		val ax_errid = 0
		val ax_errlocation = ""
		val ax_errmsg = ""
		val ax_errdesc = ""
		val ax_errstack = ""
		val ax_errtimestamp = null

		val hdl_process_id = processId
		val hdl_process_successful = processflg
		val hdl_timeReceived = checkKeyExistAndGetValue(xmlMap, "timeRecevied")
		val hdl_timerecd = if(hdl_timeReceived.nonEmpty) new java.sql.Timestamp(dtformat.parse(hdl_timeReceived).getTime) else null
		val hdl_consumerip = checkKeyExistAndGetValue(xmlMap, "consumerIp")
		val hdl_consumeruserid = checkKeyExistAndGetValue(xmlMap, "consumerUserId")
		var hdl_created_time = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())
		
		AuditRecord(ax_correlationid, ax_applicationname,ax_company,
									ax_country, ax_division, ax_group, ax_location, ax_domain, ax_userid,
									ax_timestamp, ax_hologicid, ax_srcip, ax_srcrefid, ax_transid,
									ax_errid, ax_errlocation, ax_errmsg, ax_errdesc, ax_errstack,
									ax_errtimestamp, hdl_process_id, hdl_process_successful, hdl_timerecd,
									hdl_consumerip, hdl_consumeruserid, hdl_created_by, hdl_created_time,
									hdl_year, hdl_month, hdl_day)
	}
	
//Method for converting date to UTC
def parseAndConvertToUtc(x: String) : String ={
val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z");
formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
val format =  if (x.nonEmpty) {new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS z").parse(x.trim) } else null;
val dateUtc = formatter.format(format);
dateUtc;
}
//Method for converting to date
    def parseAndConvertToDate(x: String)= if (x.nonEmpty) new java.text.SimpleDateFormat("yyyy-MM-dd").parse(x.trim) else null
	def parseAndConvertToTimestamp(x: String) : Timestamp = if (x.nonEmpty) new java.sql.Timestamp(dtformat.parse(x.trim).getTime()) else null
	def parseAndConvertToTimestampDT(x: String, dtfrmt: java.text.SimpleDateFormat) : Timestamp = if (x.nonEmpty) new java.sql.Timestamp(dtfrmt.parse(x.trim).getTime()) else null
	def parseAndConvertToDouble(x: String) : Double = if (x.nonEmpty) x.trim.toDouble else 0
	/*def parseAndConvertToLong(x: String) : Long =  if (x.nonEmpty) x.trim.toLong else 0
  def parseAndConvertToBoolean(x: String) : Boolean =  if (x.nonEmpty) x.trim.toBoolean else false
  def parseAndConvertToInt(x: String) : Int =  if (x.nonEmpty) x.trim.toInt else 0*/
	def checkKeyExistAndGetValue(xmlMap: LinkedHashMap[String,String], key : String): String = if(xmlMap.contains(key)) xmlMap(key) else ""
		
}
