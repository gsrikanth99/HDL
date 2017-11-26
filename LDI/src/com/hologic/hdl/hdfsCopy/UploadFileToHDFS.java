package com.hologic.hdl.hdfsCopy;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;

import java.io.IOException;

/**
 * This custom object handles loading data from
 * local filesystem to HDFS
 *
 */
public class UploadFileToHDFS {
	
	private static Configuration conf = null;
	
	/**
	 * This constructor is invoked and sets 
	 * hadoop related configurations
	 * @param defaultFS
	 */
	public UploadFileToHDFS(String defaultFS) {
		super();
		// setting up hadoop configuration
		conf = new Configuration();
		conf.set("fs.hdfs.impl", 
        org.apache.hadoop.hdfs.DistributedFileSystem.class.getName());
		conf.set("fs.defaultFS", defaultFS);
	}
	
	/**
	 * This method copies the source file
	 * to destination path
	 * @param src
	 * @param dst
	 * @return true/false
	 */
	public boolean pushFromLocalToHDFS(String src, String dst){
		
		FileSystem fs = null;
		try {
			fs = FileSystem.get(conf);
			Path srcPath = new Path(src);
			Path dstPath = new Path(dst);
			fs.copyFromLocalFile(srcPath,dstPath);
			fs.close();
			return true;
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		return false;
	}	
}
