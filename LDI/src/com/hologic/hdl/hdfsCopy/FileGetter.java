package com.hologic.hdl.hdfsCopy;

import java.io.BufferedWriter;

/**-----------------------------------------------------------------------------------
*This custom object / java script was written as part of HDL project for Large file ingestion into data lake 
*This job runs everyday by collecting previous day data from local file system to HDFS
*Input 					: Data from local file system
*Output 				: Data loaded to HDFS and logged in a hive table
*project Name 			: Hologic Data Lake
*File Name 				: FileGetter.java
*Author					: Arun Srinivasan
*Description			: Java code to push data from local to HDFS
*Modification History	:
*-----------------------------------------------------------------------------------
*-----------------------------------------------------------------------------------
*Modified by 				Date 		 		Version.No. 		Description
*Arun Srinivasan   		July 05, 2017  			   0.1 				Initial Release
*Ankit Shah             August 30, 2017            0.2              Release Phase II code changed 
*                                                                   for Large Data Ingestion production Issue
*-----------------------------------------------------------------------------------
*-----------------------------------------------------------------------------------
*/

import java.nio.file.Files;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;

import java.nio.file.LinkOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ArrayList;
import org.apache.hadoop.security.UserGroupInformation;

import org.apache.commons.io.FileUtils;

public class FileGetter {
	DateFormat dateFormat;
	DateFormat timeStampFormat;
	private static Properties props;
	BufferedWriter writer = null;
	List<String> lstfoldername = new ArrayList<String>();

	public FileGetter() {
		super();
		props = new Properties();	
		
	}

	/**
	 * Checks whether a file exists
	 * @param dirPath
	 * @return true/false
	 */
	public boolean checkFileExists(String dirPath){
		File dir = new File(dirPath);
		if(dir.exists()){
			return true;
		}
		return false;
	}
	
	/**
     * List all the folder under a directory
     * @param directoryName 
     * @param datetobeprocessed
     */
	  
   
	 public void listFolders(String directoryName,String dateToBeProcessed)
	 {
		    System.out.println("directoryName : " + directoryName);
	        File directory = new File(directoryName);
	        long folddatetime = 0;
	   	    //get all the files from a directory
	        File[] fList = directory.listFiles();
	        for (File foldername : fList){
	            if (foldername.isDirectory())
	            {
	            	 // gets lastmodified datetime of directory
	            	 folddatetime = foldername.lastModified(); 
					 Date folddt = new Date(folddatetime);
					 SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					 String formattedDateString = formatter.format(folddt);
					 // compare foldername with datetobeprocessed
					 if(formattedDateString.equalsIgnoreCase(dateToBeProcessed))
					 {	
						 //add foldername to List for moving to HDFS
						 lstfoldername.add(foldername.getName());
					 }
			    }
  	        }
	    }
	
		
	/**
	 * Pushes the files from shared NFS to HDFS
	 */
	private void pushFilesFromLocalToHDFS() {
		// TODO Auto-generated method stub
		try {		
			//loading property file
			props.load(new FileInputStream("properties/" + Constants.PROPERTIES_FILENAME));
			dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
			timeStampFormat = new SimpleDateFormat(Constants.TIMESTAMP_FORMAT);
			
			System.out.println("Loaded property file.");	
			
			//getting properties
			String hdfsDefaultFS = props.getProperty("defaultFS").trim();
			String hdfsPath = props.getProperty("hdfsPath");		
			String localPath = props.getProperty("localPath");
			String hdfsLogPath = props.getProperty("hdfsLogPath"); 
			
			final Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, -2);
			
			//storing date to be processed
			String dayBeforeYesterdayDate = dateFormat.format(cal.getTime());
			// Gets list of directories to be pushed to HDFS
			listFolders(localPath,dayBeforeYesterdayDate);
			for (String foldname:lstfoldername)
			{
			String localDirPath =  localPath + "/" + foldname;	
					
			System.out.println("Looking for path: " + localDirPath);

			if(checkFileExists(localDirPath)){
				int status = 0;
				System.out.println(localDirPath + " exists");
				UploadFileToHDFS uploadFile = new UploadFileToHDFS(hdfsDefaultFS);
				
				//pushes file from shared NFS to HDFS
				if(uploadFile.pushFromLocalToHDFS(localDirPath, hdfsPath)){
					System.out.println("Loaded to HDFS");
					status = 1;
				}
				//creates log files in the local file system
				File logPath = createLogFile(new File(localDirPath),dayBeforeYesterdayDate,status);
				
				//uploads log file to hive external table path
				uploadFile.pushFromLocalToHDFS(logPath.getAbsolutePath(), hdfsLogPath);
				logPath.delete();
				System.out.println("Created log file");
			}	
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Creates log file in the local file system
	 * Holds information such as device details and the time file received
	 * @param src
	 * @param processDate
	 * @param status
	 * @return Log File
	 */
	public File createLogFile(File src, String processDate, int status) {
		File logFilePath = null;
		try {
			logFilePath = new File(Constants.LOG_FILE_PATH);			
			if(!logFilePath.exists()){
				logFilePath.mkdirs();
			}
			logFilePath = new File(Constants.LOG_FILE_PATH + "/" +processDate + ".log");
			writer = new BufferedWriter(new FileWriter(logFilePath));
			for(File modelName: src.listFiles()){
				for(File deviceName: modelName.listFiles()){	
					for(File fileEntry: deviceName.listFiles()){
						String uuid = java.util.UUID.randomUUID().toString();
						LogInfo log = new LogInfo();
						log.setCorrelationID(uuid);
						log.setApplicationName("AXEDA");
						log.setCompany("Hologic");
						log.setCountry("USA");
						log.setLocation("Marlborough");
						log.setProcessedDate(processDate);
						
						log.setHologicID(uuid);
						log.setModelName(modelName.getName());
						log.setDeviceName(deviceName.getName());
						log.setFile(fileEntry.getName());
						log.setFileReceivedTimestamp(timeStampFormat.format(fileEntry.lastModified()));
						log.setProcessID("HDL-AXEDA-LDI-"+uuid+"-"+processDate);
						log.setProcessSuccessful(status);
						
						InetAddress ipAddr = InetAddress.getLocalHost();
						log.setConsumerUserName(ipAddr.getHostName());
						log.setConsumerIP(ipAddr.getHostAddress());
						log.setCreatedTime(timeStampFormat.format(Calendar.getInstance().getTime().getTime()));
						writer.write(log.toString());
						writer.newLine();

					}
				}
			}
			if(status == 1)
				//FileUtils.forceDelete(src);
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logFilePath;
	}

	public static void main(String arg[]){
		FileGetter fg = new FileGetter();
		fg.pushFilesFromLocalToHDFS();		
	}
}
