package com.hologic.hdl.util

import java.sql.Timestamp
/**
 * This class defines Case Charge Node dataset
 */
  /**-----------------------------------------------------------------------------------
 *This  Case object holding values for parsed from XML
 *Input 					: 
 *Output 					: 
 *project Name 		: Hologic Data Lake
 *File Name 			: ChargeRecord.scala
 *Author					: Arun Srinivasan
 *Description			: ChargeRecord case class  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           November 08 2017  1.1     added fields for UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */
case class ChargeRecord (
  Header_src_obj_id : String,
  Identification_Id :String, 
  Identification_SourceObjectId :String, 
  Identification_SourceParentObjectId:String, 
  Identification_Number :String, 
  Identification_Revision :String, 
  Identification_QueueRecordId :String, 
  Identification_CommonId :String,  
  OrderNumber :String, 
  Item :String, 
  ItemDescription:String, 
  Quantity :Double, 
  ProdcutName :String, 
  OrderItemNumber :Double, 
  OrderStatus :String, 
  OrderProductStatus :String, 
  EffectiveDate :Timestamp, 
  RMAReceivedDate :Timestamp, 
  RMASiteReceived :String, 
  ProductDescription :String, 
  ReturnReasonCode :String, 
  OwnerIdOperatingUnit :String, 
  AssetSerialNumber :String, 
  RMANumber :String, 
  IntlCallNumber :String, 
  BillingType :String, 
  OrderRecordType :String, 
  OrderLineType :String, 
  RmaFlag :String, 
  PartFlag :String,
  hdl_created_by : String,
  hdl_created_time : Timestamp,
  EffectiveDate_UT : String,
	RMAReceivedDate_UT : String,
  hdl_year : Int,
  hdl_month : Int,
  hdl_day : Int
)
