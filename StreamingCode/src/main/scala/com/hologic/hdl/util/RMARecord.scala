package com.hologic.hdl.util

import java.sql.Timestamp
/**
 * This class defines Case RMA Node dataset
 */
case class RMARecord (
  Header_src_obj_id : String,
	Identification_Id : String,
	Identification_SourceObjectId : String,
	Identification_SourceParentObjectId : String,
	Identification_Number : String,
	Identification_Revision : String,
	Identification_QueueRecordId : String,
	Identification_CommonId : String,
	OrderNumber : String,
	OrderReferenceNumber : String,
	Item : String,
	ItemDescription : String,
	Quantity : Double,
	ProdcutName : String,
	OrderItemNumber : Double,
	OrderStatus : String,
	OrderProductStatus : String,
	EffectiveDate : Timestamp,
	RMAReceivedDate : Timestamp,
	RMAUpdateDate : Timestamp,
	RMASiteReceived : String,
	ProductDescription : String,
	ReturnReason : String,
	OwnerIdOperatingUnit : String,
	AssetSerialNumber : String,
	RMANumber : String,
	RMALineNumber : Double,
	IncidentNumber : String,
	InvoiceNumber : String,
	IntlCallNumber : String,
	BillingType : String,
	OrderRecordType : String,
	OrderLineType : String,
	OperatingUnitCode : String,
	OperatingUnitName : String,
	OrderDate : Timestamp,
	serviceActivity : String,
	RmaFlag : String,
	PartFlag : String,
	CreatedByFederationId : String,
	CreatedBySystemAlias : String,
	CreatedByName : String,
	UpdatedByFederationId : String,
	UpdatedBySystemAlias : String,
	UpdatedByName : String,
	hdl_created_by : String,
	hdl_created_time : Timestamp,
	hdl_year : Int,
	hdl_month : Int,
	hdl_day : Int
)