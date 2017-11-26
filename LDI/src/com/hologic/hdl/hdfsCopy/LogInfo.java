package com.hologic.hdl.hdfsCopy;

/**
 * This object holds the log Information
 *
 */
public class LogInfo {
	
	//Stores correlation ID
	private String correlationID;
	
	//Stores name of the application
	private String applicationName;
	
	//stores name of the company
	private String company;
	
	//stores country detail
	private String country;
	
	//stores division detail
	private String division = "";
	
	//stores group detail
	private String group = "";
	
	//stores location detail
	private String location;
	
	//stores processed folder date
	private String processedDate;
	
	//stores hologic ID
	private String hologicID;
	
	//stores name of the model
	private String modelName;
	
	//stores name of the device
	private String deviceName;

	//stores name of the file
	private String file;
	
	//stores file received timestamp
	private String fileReceivedTimestamp;
	
	//stores process ID
	private String processID;
	
	//stores process status
	private int processSuccessful;
	
	//stores name of the consumer
	private String consumerUserName;
	
	//stores IP of the consumer
	private String consumerIP;
	
	//stores time stamp of the process
	private String createdTime;

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getProcessedDate() {
		return processedDate;
	}

	public void setProcessedDate(String processedDate) {
		this.processedDate = processedDate;
	}

	public String getCorrelationID() {
		return correlationID;
	}

	public void setCorrelationID(String correlationID) {
		this.correlationID = correlationID;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getHologicID() {
		return hologicID;
	}

	public void setHologicID(String hologicID) {
		this.hologicID = hologicID;
	}

	public String getFileReceivedTimestamp() {
		return fileReceivedTimestamp;
	}

	public void setFileReceivedTimestamp(String fileReceivedTimestamp) {
		this.fileReceivedTimestamp = fileReceivedTimestamp;
	}

	public String getProcessID() {
		return processID;
	}

	public void setProcessID(String processID) {
		this.processID = processID;
	}

	public int getProcessSuccessful() {
		return processSuccessful;
	}

	public void setProcessSuccessful(int processSuccessful) {
		this.processSuccessful = processSuccessful;
	}

	public String getConsumerUserName() {
		return consumerUserName;
	}

	public void setConsumerUserName(String consumerUserName) {
		this.consumerUserName = consumerUserName;
	}

	public String getConsumerIP() {
		return consumerIP;
	}

	public void setConsumerIP(String consumerIP) {
		this.consumerIP = consumerIP;
	}

	public String getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public String toString() {
		return correlationID + "," + applicationName + ","
				+ company + "," + country + "," + division + "," + group + ","
				+ location + "," + processedDate + "," + hologicID + ","
				+ modelName + "," + deviceName + "," + file + ","
				+ fileReceivedTimestamp + "," + processID + "," + processSuccessful
				+ "," + consumerUserName + "," + consumerIP + ","
				+ createdTime;
	}

}
