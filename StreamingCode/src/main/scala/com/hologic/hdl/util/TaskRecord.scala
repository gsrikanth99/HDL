package com.hologic.hdl.util

import java.sql.Timestamp
/**
 * This class defines Case Tasks node dataset
 */
case class TaskRecord (
  Header_src_obj_id : String,
	Task_Identification_Id: String,
	Task_Identification_SourceObjectId: String,
	Task_Identification_SourceParentObjectId: String,
	Task_Identification_Number: String,
	Task_Identification_Revision: String,
	Task_Identification_QueueRecordId: String,
	Task_Identification_CommonId: String,
	Task_TaskName: String,
	Task_TaskStatus: String,
	Task_TaskPriority: String,
	Task_TaskAssignmentStatus: String,
	Task_TaskAssignmentId: Double,
	Task_TaskType: String,
	Task_TaskDescription: String,
	Task_TaskComment: String,
	Task_TaskAddress: String,
	Task_IncidentId: Double,
	Task_ResourceId: Double,
	Task_Assignee: String,
	Task_ResourceTypeCode: String,
	Task_AssigneeType: String,
	Task_OwnerId: String,
	Task_OwnerType: String,
	Task_OwnerTypeCode: String,
	Task_TaskPlannedStartDate: Timestamp,
	Task_TaskPlannedEndDate: Timestamp,
	Task_TaskScheduleStartDate: Timestamp,
	Task_TaskScheduleEndDate: Timestamp,
	Task_TaskActualStartDate: Timestamp,
	Task_TaskActualEndDate: Timestamp,
	Task_DebriefNumber: String,
	Task_DebriefHeaderId: Double,
	Task_DebriefDate: Timestamp,
	Task_DebriefStatus: String,
	Task_DebriefSerialNumber: String,
	Task_DebriefCategory: String,
	Task_PrivateFlag: String,
	Task_PublishFlag: String,
	Task_PlannedEffort: Double,
	Task_PlannedEffortUOM: String,
	Task_ActualEffort: Double,
	Task_ActualEffortUOM: String,
	Task_Duration: Double,
	Task_DurationUOM: String,
	Task_DebriefProcessedFlag: String,
	Task_LaborEndingMileage: String,
	Task_Labour_Labour_LabourActivityCode: String,
	Task_Labour_Labour_LabourActivityDesc: String,
	Task_Labour_Labour_LabourItem: String,
	Task_Labour_Labour_LabourItemDescription: String,
	Task_Labour_Labour_LabourDuration: String,
	Task_Labour_Labour_LabourUOM: String,
	Task_Labour_Labour_LabourServiceDate: Timestamp,
	Task_Labour_Labour_LabourStartDate: Timestamp,
	Task_Labour_Labour_LabourEndDate: Timestamp,
	Task_Labour_Labour_LabourStartMileage: String,
	Task_Labour_Labour_LabourEndMileage: String,
	Task_Material_Material_MaterialActivityCode: String,
	Task_Material_Material_MaterialUsed: String,
	Task_Material_Material_MaterialUsedDescription: String,
	Task_Material_Material_MaterialUOM: String,
	Task_Material_Material_MaterialQuantity: Double,
	Task_Material_Material_MaterialSubInventory: Double,
	Task_Material_Material_MaterialServiceDate: Timestamp,
	Task_Material_Material_MaterialLocator: String,
	Task_Material_Material_MaterialSerialNumber: String,
	Task_Material_Material_MaterialLotNumber: String,
	Task_Material_Material_MaterialInstanceNumber: String,
	Task_Expense_Expense_ExpenseActivityCode: String,
	Task_Expense_Expense_ExpenseItem: String,
	Task_Expense_Expense_ExpenseItemDescription: String,
	Task_Expense_Expense_ExpenseUOM: String,
	Task_Expense_Expense_ExpenseQuantity: Double,
	Task_Expense_Expense_ExpenseItemAmount: Double,
	Task_Expense_Expense_ExpenseCurrencyCode: String,
	Task_Expense_Expense_ExpenseServiceDate: String,
	hdl_created_by : String,
  hdl_created_time : Timestamp,
  hdl_year : Int,
  hdl_month : Int,
  hdl_day : Int
)