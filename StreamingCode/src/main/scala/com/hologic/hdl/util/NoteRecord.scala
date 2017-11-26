package com.hologic.hdl.util

import java.sql.Timestamp
/**
 * This class defines Case Notes node dataset
 */
  /**-----------------------------------------------------------------------------------
 *This  Case object holding values for parsed from XML
 *Input 					: 
 *Output 					: 
 *project Name 		: Hologic Data Lake
 *File Name 			: NoteRecord.scala
 *Author					: Arun Srinivasan
 *Description			: NoteRecord case class  
 *Modification History	:
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 *Modified by 				Date 					 		Version.No. 		Description
 *Arun Srinivasan   	July 11, 2017  			 1.0 				Initial Release
 * Chinmaya R           November 08 2017  1.1     added fields for UTC columns
 *-----------------------------------------------------------------------------------
 *-----------------------------------------------------------------------------------
 */
case class NoteRecord (
  Header_src_obj_id : String,
  Note_Identification_Id: String,
	Note_Identification_SourceObjectId: String,
	Note_Identification_SourceParentObjectId: String,
	Note_Identification_Number: String,
	Note_Identification_Revision: String,
	Note_Identification_QueueRecordId: String,
	Note_Identification_CommonId: String,
	Note_ParentObjectId: String,
	Note_UserName: String,
	Note_CreationDate: Timestamp,
	Note_NoteStatusMeaning: String,
	Note_CreatedBy: String,
	Note_CreatedByName: String,
	Note_isPublished: String,
	Note_isDeleted: String,
	Note_CreatorFullPhotoUrl: String,
	Note_CreatorSmallPhotoUrl: String,
	Note_NoteTypeMeaning: String,
	Note_Note: String,
	Note_LastUpdateByName: String,
	Note_LastUpdateById: String,
	Note_LastUpdateDate: String,
	hdl_created_by : String,
        hdl_created_time : Timestamp,
        CreationDate_UT : String,
        LastUpdateDate_UT : String,
  hdl_year : Int,
  hdl_month : Int,
  hdl_day : Int
)
