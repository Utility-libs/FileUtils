package com.fileutils.plugin.impl;

import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.FileSystemTypeConstants;

public class FileSystemUtilPluginFactory {
	/**
	 * 
	 * 
	 * Method Name 	: getFileUtils
	 * Description 	: The Method "getFileUtils" is used for 
	 * Date    		: May 13, 2016, 11:10:59 AM
	 * @param constants
	 * @return
	 * @param  		:
	 * @return 		: ZHFileUtils
	 * @throws FileSystemUtilException 
	 * @throws 		:
	 */
	public FileSystemUtilsPlugin getFileUtils(FileSystemTypeConstants constants) throws FileSystemUtilException {
		switch (constants) {
		case LFS:
			return new LFSUtil();
		case SCP:
			return new SCPFileUtil();
		case FTP:
			return new FTPUtil();
		case SFTP:
			return new SFTPUtil();
		case HTTP:
			return new HTTPUtil();
		case ZIP:
			return new ZipFileUtil();
		
		default:
			break;
		}
		return null;
	}
}
