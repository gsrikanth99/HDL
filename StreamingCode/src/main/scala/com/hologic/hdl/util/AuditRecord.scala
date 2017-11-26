package com.hologic.hdl.util
/**
 * This class defines Audit Info dataset
 */
case class AuditRecord (
		correlationid : String, 
		applicationname : String,
		company : String,
		country : String,
		division : String, 
		group : String, 
		location : String, 
		domain : String, 
		userid : String,
		timestamp : java.sql.Timestamp, 
		hologicid : String, 
		srcip : String, 
		srcrefid : String, 
		transid : Long,
		errid : Long, 
		errlocation : String, 
		errmsg : String, 
		errdesc : String,
		errstack : String,
		errtimestamp : java.sql.Timestamp, 
		process_id: String,
		process_successful: Int,
		timerecd : java.sql.Timestamp,
		consumerip : String,
		consumeruserid : String,
		created_by : String,
		created_time : java.sql.Timestamp,
		year : Int,
		month : Int,
		day : Int
)