package com.fileutils.plugin.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.ConvertFiles;

import ch.qos.logback.classic.Logger;

public class LFSUtil implements FileSystemUtilsPlugin {
	
	/** The logger. */
	Logger logger = (Logger)LoggerFactory.getLogger(getClass());
	
	/** The local file path. */
	private String localFilePath = null;		

	/**
	 * Instantiates a new LFS util.
	 *
	 * @param configBean the config bean
	 * @throws FileSystemUtilException the file system util exception
	 */
	public LFSUtil()  {
	}

	/**
	 * Sets the config.
	 *
	 * @param context the new context
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":connect()");
		
		localFilePath = context.getLocalFilePath();
		logger.debug("End: " + getClass().getName() + ":connect()");
	}

	/**
	 * Connect.
	 *
	 * @param isUIModule the is ui module
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void connect() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * Disconnect.
	 *
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void disconnect() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * Put file.
	 *
	 * @param is the is
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void putFile(InputStream is, String targetLocation) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":putFile()");
		File arg0 = new File(targetLocation);
		FileOutputStream outputStream = null;
		try {
			logger.debug("targetLocation:"+targetLocation);
			outputStream = new FileOutputStream(arg0);
			int read = 0;
			byte[] bytes = new byte[1024];
			while ((read = is.read(bytes)) != -1) {
				outputStream.write(bytes, 0, read);
			}
			outputStream.close();
		} catch (IOException e) {
			throw new FileSystemUtilException("F00002",null,Level.ERROR,e);	
		}
		logger.debug("Ends: "+getClass().getName()+":putFile()");
	}

	/**
	 * Gets the file.
	 *
	 * @param filepath the filepath
	 * @return the file
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public InputStream getFile(String filepath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":getFile()");
		try {
			logger.debug("FilePath:"+filepath);
			return new FileInputStream(new File(filepath));
		} catch (FileNotFoundException e) {

			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
	}

	/**
	 * List files.
	 *
	 * @param dirPath the dir path
	 * @return the list
	 * @throws FileSystemUtilException the file system util exception
	 */

	public List<String> listFiles(String dirPath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		List<String> fileList = null;
		try {
			logger.debug("dirPath:"+dirPath);
			File directory = new File(dirPath);
			// get all the files from a directory
			File[] fList = directory.listFiles();
			if (fList.length > 0) {
				fileList = new ArrayList<>();
			}
			for (File file : fList) {
				if (file.isFile())
					fileList.add(file.getName());
			}

		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("Ends: "+getClass().getName()+":listFiles()");
		return fileList;
	}
	
	/**
	 * List files.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @param isFilesOnly the is files only
	 * @return the string[]
	 * @throws FileSystemUtilException the invalid data exception
	 */
	 @Override
	 public String[] listFiles(boolean isFoldersRequired, boolean isSort, boolean isFilesOnly) throws FileSystemUtilException{
		return listFiles();
	 }

	/**
	 * Removes the file.
	 *
	 * @param filePath the file path
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	
	public boolean removeFile(String filePath, String fileName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":removeFile()");
		try {
			logger.debug("FilePath:"+filePath);
			logger.debug("FileName:"+fileName);
			File file = new File(filePath + fileName);
			if (file.exists() && file.isFile()) {
				file.delete();
			}
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":removeFile()");
		return true;
	}

	/**
	 * Truncate file.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean truncateFile(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":truncateFile()");
		try {
			File file = new File(filePath);
			logger.debug("File Path ===="+filePath);
			FileOutputStream fileOutputStream = new FileOutputStream(file, true);
			FileChannel outChan = fileOutputStream.getChannel();
			outChan.truncate(0);
			outChan.close();
			fileOutputStream.close();
			return true;
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
	}

	

	/**
	 * Rename file.
	 *
	 * @param filePath the file path
	 * @param fileName the file name
	 * @param newFileName the new file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean renameFile(String oldFileName,String newFileName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":renameFile()");
		try {
			File afile = new File(oldFileName);
			logger.debug("Renaming file :"+oldFileName+" to :"+newFileName);
			afile.renameTo(new File(newFileName));
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":renameFile()");
		return true;
	}

	/**
	 * Creates the folder.
	 *
	 * @param dirName the dir name
	 * @param path the path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	public boolean createFolder(String dirName, String path) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":createFolder()");
		try {
			logger.debug("Directory Name:"+dirName);
			logger.debug("Directory Path"+path);
			File afile = new File(path + dirName);
			afile.mkdir();
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":createFolder()");
		return true;

	}

	/**
	 * List folders.
	 *
	 * @param dirPath the dir path
	 * @return the list
	 * @throws FileSystemUtilException the file system util exception
	 */
	
	public List<String> listFolders(String dirPath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":listFolders()");
		List<String> fileList = null;
		try {
			File directory = new File(dirPath);
			// get all the files from a directory
			File[] fList = directory.listFiles();
			if (fList.length > 0) {
				fileList = new ArrayList<>();
			}
			for (File file : fList) {
				if (file.isDirectory())
					fileList.add(file.getName());
			}

		} catch (Exception e) {
			throw new FileSystemUtilException("WT0001",null,Level.ERROR,e);
		}
		logger.debug("End: "+getClass().getName()+":listFolders()");
		return fileList;
	}

	/**
	 * Removes the folder.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean removeFolder(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":removeFolder()");
		try {
			logger.debug("File Path:"+filePath);
			File file = new File(filePath);
			if (file.exists() && file.isDirectory()) {
				file.delete();
			}
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":removeFolder()");
		return true;
	}

	/**
	 * Copy folder.
	 *
	 * @param path the path
	 * @param dirName the dir name
	 * @param newDirName the new dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean copyFolder(String path, String dirName, String newDirName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":copyFolder()");
		try {
			logger.debug("Path:"+path);
			logger.debug("Directory Name"+dirName);
			logger.debug("New Directory Name:"+newDirName);
			File afile = new File(path + dirName);
			afile.renameTo(new File(path + newDirName));
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":copyFolder()");
		return true;
	}

	/**
	 * Removes the folder contents.
	 *
	 * @param dirPath the dir path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	public boolean removeFolderContents(String dirPath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":removeFolderContents()");
		try {
			File file = new File(dirPath);
			logger.debug("Folder path:"+dirPath);
			if (file.exists()) {
				do {
					delete(file);
				} while (file.exists());
			} else {
				logger.debug("File or Folder not found : " + dirPath);
			}
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
			
		}
		logger.debug("Ends: "+getClass().getName()+":removeFolderContents()");
		return true;

	}

	/**
	 * Rename folder.
	 *
	 * @param path the path
	 * @param dirName the dir name
	 * @param newDirName the new dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean renameFolder(String oldDirName, String newDirName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":renameFolder()");
		try {
			File afile = new File(oldDirName);
			logger.debug("Old folder name : " + oldDirName + " and new folder name :" + newDirName);
			afile.renameTo(new File(newDirName));

		} catch (Exception e) {
			throw new FileSystemUtilException("FM0008",null,Level.ERROR,e);
		}
		logger.debug("End: "+getClass().getName()+":renameFolder()");
		return true;
	}

	/**
	 * Put file.
	 *
	 * @param input the input
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void putFile(String input, String targetLocation) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":putFile()");
		try {
			FileWriter fileWriter = new FileWriter(targetLocation);
			logger.debug("File path : " + targetLocation );
			fileWriter.write(input);
			fileWriter.close();
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		logger.debug("End: "+getClass().getName()+":putFile()");
	}

	/**
	 * Delete.
	 *
	 * @param file the file
	 */
	private void delete(File file) {
		logger.debug("Begin: "+getClass().getName()+":delete()");
		if (file.isDirectory()) {
			String fileList[] = file.list();
			if (fileList.length == 0) {
				logger.debug("Deleting Directory : " + file.getPath());
				file.delete();
			} else {
				int size = fileList.length;
				for (int i = 0; i < size; i++) {
					String fileName = fileList[i];
					logger.debug("File path : " + file.getPath() + " and name :" + fileName);
					String fullPath = file.getPath() + "/" + fileName;
					File fileOrFolder = new File(fullPath);
					logger.debug("Full Path :" + fileOrFolder.getPath());
					delete(fileOrFolder);
				}
			}
		} else {
			logger.debug("Deleting file : " + file.getPath());
			file.delete();
		}
		logger.debug("Begin: "+getClass().getName()+":delete()");
	}

	

	/**
	 * Removes the file.
	 *
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	@Override
	public boolean removeFile(String fileName) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":removeFile()");
		try {
			File file = new File(fileName);
			if (file.delete()) {
				logger.debug(file.getName() + " is deleted!");
				return true;
			} else {
				logger.debug("Delete operation is failed.");
				return false;
			}
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}

	}

	/**
	 * Move file.
	 *
	 * @param srcfilePath
	 *            the srcfile path
	 * @param destfilePath
	 *            the destfile path
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	@Override
	public boolean moveFile(String srcfilePath, String destfilePath) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":moveFile()");
		try {
			logger.debug("Source File Path:"+srcfilePath);
			File afile = new File(srcfilePath);
			if (afile.renameTo(new File(destfilePath + afile.getName()))) {
				logger.debug("File is moved successful!");
				return true;
			} else {
				logger.debug("File is failed to move!");
				return false;
			}

		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
	}

	/**
	 * Upload files.
	 *
	 * @param is the is
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean uploadFiles(InputStream is, String targetLocation, String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * Creates the folder.
	 *
	 * @param dirName
	 *            the dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	@Override
	public boolean createFolder(String dirName) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":createFolder()");
		boolean folderCreated= false;
		try {
			logger.debug("Directory Name"+dirName);
			File file = new File(dirName);
			if (!file.exists()) {
				if (file.mkdir()) {
					logger.debug("Directory is created!");
					folderCreated= true;
				} else {
					logger.debug("Failed to create directory!");
					folderCreated = false;
				}
			}
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		}
		return folderCreated;
	}

	/**
	 * List folders.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public String[] listFolders() throws FileSystemUtilException {
		logger.info("Info: " + getClass().getName() + ":listFolders()");
		// getting all the files and folders in the given file path
		String[] files = listFiles();
		List<String> foldersList = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			// all the folder are ends with '/'
			if (files[i].endsWith("/") && isValidString(files[i])) {
				foldersList.add(files[i]);
			}
		}
		String[] folders = new String[foldersList.size()];
		// getting only folders from returned array of files and folders
		for (int i = 0; i < foldersList.size(); i++) {
			folders[i] = foldersList.get(i).toString();
		}
		logger.info("Ends: " + getClass().getName() + ":listFolders()");
		return folders;

	}

	/**
	 * Gets the sample data.
	 *
	 * @param dataLength the data length
	 * @param charset the charset
	 * @return the sample data
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * Un zip.
	 *
	 * @param localFilePath the local file path
	 * @param remLocal the rem local
	 * @return the string
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public String unZip(String localFilePath, boolean remLocal) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * Stream un zipper.
	 *
	 * @param sampleBytes the sample bytes
	 * @return the byte[]
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public byte[] streamUnZipper(byte[] sampleBytes) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}
	
	/**
	 * Convert unix file to dos.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean convertUnixFileToDOS(String filePath) throws FileSystemUtilException {
		try {
			return new ConvertFiles().unixToDos(filePath);
		} catch (Exception e) {
			logger.error("Exception::",e);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,e);	
		}

	}

	/**
	 * Convert dos file to unix.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean convertDOSFileToUnix(String filePath) throws FileSystemUtilException {
		try {
			return new ConvertFiles().dosToUnix(filePath);
		} catch (Exception e) {
			logger.error("Exception::",e);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,e);	
		}

	}

	/**
	 * Convert file to utf format.
	 *
	 * @param fileName the file name
	 * @param encodeFormat the encode format
	 * @param newFileName the new file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean convertFileToUTFFormat(String fileName, String encodeFormat, String newFileName)
			throws FileSystemUtilException {
		try {
			return new ConvertFiles().convertFileToUTFFormat(fileName, encodeFormat, newFileName);
		} catch (Exception e) {
			logger.error("Exception::",e);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,e);	
		}

	}

	/**
	 * Download file.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean downloadFile() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * Checks if is zip.
	 *
	 * @param remoteFilePath the remote file path
	 * @return true, if is zip
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean isZip(String remoteFilePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * Zip file.
	 *
	 * @param zipFileDetails the zip file details
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public void zipFile(ZipFileDetails zipFileDetails) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * Upload file.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * List files.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public String[] listFiles() throws FileSystemUtilException {
		// throw new FileSystemUtilException("000000", null, Level.ERROR, null);
		List<String> results = new ArrayList<String>();
		File[] files = new File(localFilePath).listFiles();
		// If this pathname does not denote a directory, then listFiles()
		// returns null.
		for (File file : files) {
			if (file.isFile()) {
				results.add(file.getName());
			}
		}
		return (String[]) results.toArray();

	}

	/**
	 * 
	 * Method Name 	: getUserHome
	 * Description 		: The Method "getUserHome" is used for 
	 * Date    			: Jul 21, 2016, 8:07:28 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String getUserHome() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}
	
	/**
	 * Checks if is valid string.
	 *
	 * @param str the str
	 * @return true, if is valid string
	 */
	private boolean isValidString(String str){
		String allowedChars="! \" # $ % & ' ( ) * + , - . / 0 1 2 3 4 5 6 7 8 9 : ; < = > ? @ A B C D E F G H I J K L M N O P Q R S T U V W X Y Z [ \\ ] ^ _ ` a b c d e f g h i j k l m n o p q r s t u v w x y z { | } ~";
		for(int i=0;i<str.split("").length;i++){
			if(allowedChars.indexOf(str.split("")[i]) == -1){
				return false;
			}
		}
		return true;
	}

}
