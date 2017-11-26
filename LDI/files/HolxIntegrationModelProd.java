//-----------------------------------------------------------------------------------
// This custom object / groovy script will be executed by expression rule (DownloadFileOnDevice) when file upload process completes
// After execution of the script file will be downloaded to one target device
// Input 				: Object of file uploaded on any device on Axeda cloud with serial number and model name of that device
// Output 				: Uploaded file will be downloaded to one target device
// project Name 		: Hologic Data Lake
// File Name 			: DownloadFileOnDevice.java
// Author				: Ashish Karnia
// Description			: groovy script to download uploaded file on one target device
// Modification History	:
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------
// Modified by 				Date 		 		Version.No. 		Description
// Ashish Karnia    		June 30, 2017  		0.1 				Initial Release
//-----------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------

import com.axeda.drm.sdk.Context
import com.axeda.drm.sdk.data.*
import com.axeda.drm.sdk.device.*
import com.axeda.drm.sdk.device.Model
import com.axeda.drm.sdk.device.ModelFinder
import com.axeda.drm.sdk.agent.commands.Download
import com.axeda.drm.sdk.agent.commands.CommandStatus
import static com.axeda.sdk.v2.dsl.Bridges.*
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;

def downloadFileModelName = "HolxIntegrationModel"; // model name of device on which uploaded file will be downloaded
def downloadFileDeviceName = "prod-hdl-agent01"; // serial number of device on which uploaded file will be downloaded
def mainFolder = "\\\\USMARISILON01.hologic.corp\\PRDAXD\\HolxIntegrationModel_prod\\"; // path of main folder on device where uploaded file will be downloaded
def uploadedFileModelName = parameters.model; // model name of device on which file is uploaded
def uploadedFileDeviceName = parameters.serial; // serial number of device on which file is uploaded
def customObjectName = "DownloadFileOnDevice";
def date = new Date(System.currentTimeMillis()).format("yyyy-MM-dd");

try 
{  
	logger.info("Execution of custom object " + customObjectName + " started");
	logger.info("Serial number of device on which file is uploaded: " + uploadedFileDeviceName);
	logger.info("Model name of device on which file is uploaded: " + uploadedFileModelName);
	logger.info("Serial number of device on which uploaded file will be downloaded: " + downloadFileDeviceName);
	logger.info("Model name of device on which uploaded file will be downloaded: " + downloadFileModelName);
	
	// Operate in the context of the user calling the service
	def context = Context.getUserContext();
		
	// gets list of uploaded files from agent once file upload process completes
	def uploadedFiles = compressedFile.getFiles();
	
	// Find the model of device on which file will be downloaded
	ModelFinder modelFinder = new ModelFinder(context);
	modelFinder.setName(downloadFileModelName);
    Model model = modelFinder.find();
	
	if(!model)
	{
		throw new Exception("Unable to find model (" + downloadFileModelName + ") of device on which file will be downloaded");
	}
	
	// Finds the device for given model
	DeviceFinder deviceFinder = new DeviceFinder(context);
    deviceFinder.setModel(model);
    deviceFinder.setSerialNumber(downloadFileDeviceName);
    Device device = deviceFinder.find();
	
	if(!device) 
	{
		throw new Exception("Unable to find serial number (" + downloadFileDeviceName + ") of device on which file will be downloaded");
	}

	for (UploadedFile uploadedFile : uploadedFiles)
	{
		// generates file path where file will be downloaded on device
		
		def folderStructure = "";
		
		def filePath = uploadedFile.getName();
		
		String[] paths = filePath.split("/");
		
		if(paths.length > 1){
			for(int i=0; i<paths.length-1; i++){
				if(paths[i] != '' && paths[i] != null && paths[i] != '.'){
				  folderStructure += "\\" + paths[i];
				}
			 }
		}
		
		def fileDownloadPath = mainFolder + date +"\\"+ uploadedFileModelName +"\\"+ uploadedFileDeviceName + folderStructure;
		
		logger.info("file will be downloaded on path: " + fileDownloadPath);
		
		// Creates a new file in the file system with the contents of the uploaded file and returns instance of File that represents the new file.
		File file = uploadedFile.extractFile(); 
		
		// Create a download, specifying the target directory
		Download download= new Download(context, file, fileDownloadPath ,false); 
		
		// Enqueue file download request to the given device.
		CommandStatus cs = download.send(device); 
	}
	
	logger.info("Execution of custom object " + customObjectName + " finished");
}
catch (Exception e)
{
	logger.info("Exception occurred while executing custom object " + customObjectName + " : " + e.message);
}


