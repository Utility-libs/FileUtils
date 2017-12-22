package com.fileutils.plugin.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.Constants;
import com.fileutils.util.ConvertFiles;

import ch.qos.logback.classic.Logger;

public class ZipFileUtil implements FileSystemUtilsPlugin {

	/** The logger. */
	static Logger logger = (Logger) LoggerFactory.getLogger(ConvertFiles.class);

	/** The bundle. */
	//private ResourceBundle bundle = java.util.ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE);

	/**
	 * 
	 * @param configBeanConstructor
	 *            "ZipFileUtil" is used for
	 */
	public ZipFileUtil() {
		//logger.setResourceBundle(bundle);
	}

	/**
	 * 
	 * Method Name : setContext Description : The Method "setContext" is used
	 * for Date : Jun 17, 2016, 11:36:24 AM
	 * 
	 * @param context
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * 
	 * Method Name : connect Description : The Method "connect" is used for Date
	 * : Jun 17, 2016, 11:36:26 AM
	 * 
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public void connect() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * method for checking for a zip file param file name returns true or false.
	 *
	 * @param remoteFilePath
	 *            the remote file path
	 * @return true, if is zip
	 */

	public boolean isZip(String remoteFilePath) {
		logger.debug("Begin: " + getClass().getName() + ":isZip()");
		boolean returnval = false;
		if (remoteFilePath != null) {
			String exts = remoteFilePath.substring(remoteFilePath.lastIndexOf(".") + 1, remoteFilePath.length());
			if (exts.equalsIgnoreCase(Constants.ZIPEXT)) {
				logger.debug("End: " + getClass().getName() + ":isZip()");
				returnval = true;
			} else {
				logger.debug("End: " + getClass().getName() + ":isZip()");
				// get localfilepath and get the xtension and return true if it
				// is zip
				returnval = false;
			}
		}
		return returnval;
	}

	/**
	 * this method is for unzipping the file and gets t he first file and the
	 * renames the extracted file with .zip.txt and returns the file name param
	 * filename which needs to be zipped throws FileSystemUtilException
	 *
	 * @param localFilePath
	 *            the local file path
	 * @param remLocal
	 *            the rem local
	 * @return the string
	 * @throws FileSystemUtilException
	 *             the pre publisher exception
	 */
	@Override
	public String unZip(String localFilePath, boolean remLocal) throws FileSystemUtilException {
		logger.debug("Start: " + getClass().getName() + ":unZip()");
		BufferedOutputStream destination = null;
		FileInputStream finps = null;
		ZipInputStream zipinp = null;
		FileOutputStream fos = null;
		String fileName = null;
		String lFileName = null;
		try {
			String path = localFilePath.substring(0, localFilePath.lastIndexOf(Constants.file_separator));
			finps = new FileInputStream(localFilePath);
			zipinp = new ZipInputStream(new BufferedInputStream(finps));
			ZipEntry entry;
			logger.debug("Begin: " + getClass().getName() + ":File Reading started");
			if ((entry = zipinp.getNextEntry()) != null) {

				int count;
				byte databuff[] = new byte[2048];
				fileName = entry.getName();
				String file1 = path + Constants.file_separator + fileName;
				// write the files
				fos = new FileOutputStream(file1);
				destination = new BufferedOutputStream(fos, 2048);
				while ((count = zipinp.read(databuff, 0, 2048)) != -1) {
					destination.write(databuff, 0, count);
				}
				destination.flush();
				destination.close();
				destination = null;
				fos.flush();
				fos.close();
				fos = null;
			} else {
				// Fixed ZET#3681
				logger.info("EL0705 : Reading a file from a zip file " + localFilePath
						+ " has failed. Check whether valid zip file present or not.");
				throw new FileSystemUtilException("EL0705");
			}
			zipinp.close();
			zipinp = null;
			finps.close();
			finps = null;
			logger.debug("End: " + getClass().getName() + ":File Reading Ended");
			lFileName = localFilePath.trim() + ".txt";
			String s1 = path + Constants.file_separator + fileName;
			logger.debug(getClass().getName() + ":Modified to file" + lFileName);
			if (remLocal) {
				File ff = new File(localFilePath);
				ff.delete();
			}
			logger.debug("End: " + getClass().getName() + ":unZip()");
		} catch (FileNotFoundException e) {
			throw new FileSystemUtilException("EL0635", null, Level.ERROR, e);
		} catch (IOException e) {
			throw new FileSystemUtilException("EL0634", null, Level.ERROR, e);
		} catch (FileSystemUtilException e) {
			throw new FileSystemUtilException("EL0705", null, Level.ERROR, e);
		} catch (Exception ex) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, ex);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
				if (destination != null) {
					destination.close();
				}
				if (zipinp != null) {
					zipinp.close();
				}
				if (finps != null) {
					finps.close();
				}
			} catch (IOException e1) {
				//logger.l7dlog(Level.WARN, "WL0634", e1);
				logger.warn("Warn::",e1);
			}

		}
		return lFileName;
	}

	/**
	 * this method is for stream unzipping retrives the value of the first file
	 * present in the zip file and returs a byte array of that data param a byte
	 * array of data which contains the stream throws FileSystemUtilException.
	 *
	 * @param sampleBytes
	 *            the sample bytes
	 * @return the byte[]
	 * @throws FileSystemUtilException
	 *             the pre publisher exception
	 */
	public byte[] streamUnZipper(byte[] sampleBytes) throws FileSystemUtilException {
		ByteArrayInputStream sampleStream = null;
		ZipInputStream zin = null;
		String outputString = "";
		try {
			sampleStream = new ByteArrayInputStream(sampleBytes);
			byte[] myBytes = new byte[1024];
			zin = new ZipInputStream(sampleStream);
			zin.getNextEntry();
			int byteCount = 0;
			int tempCount = 0;
			int totalLength = sampleBytes.length;
			// bugfix getting sample for zip files with size > 10k failed -
			// START
			// while reading the zip file checking the condition till end of the
			// sampleByetes
			while (byteCount <= totalLength) {
				tempCount = zin.read(myBytes);
				if (tempCount != -1) {
					outputString = outputString + new String(myBytes, 0, tempCount);
				}
				byteCount = byteCount + tempCount;
			}
			// bugfix getting sample for zip files with size > 10k failed - END
		} catch (IOException e) {
			throw new FileSystemUtilException("EL0634", null, Level.ERROR, e);
		} catch (Exception e) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, e);
		} finally {
			try {
				if (sampleStream != null) {
					sampleStream.close();
				}
				if (zin != null) {
					zin.close();
				}
			} catch (IOException e1) {
				//logger.warn(Level.WARN, "WL0634", e1);
				logger.warn("Warn::",e1);
			}
		}
		return outputString.getBytes();
	}

	/**
	 * This method unzips a given Zip file as bytestream and returns an hashmap.
	 *
	 * @param file
	 *            the file
	 * @return hashMap;
	 * @throws Exception
	 *             the exception
	 */
	public HashMap<String, ByteArrayOutputStream> unZip(byte[] file) throws Exception {
		logger.debug("Start: " + getClass().getName() + ":unZip()");
		String fileName = null;
		ByteArrayInputStream byteArrayInpStream = new ByteArrayInputStream(file);
		ZipInputStream zipInp = new ZipInputStream(new BufferedInputStream(byteArrayInpStream));
		ByteArrayOutputStream bos = null;
		ZipEntry entry;
		HashMap<String, ByteArrayOutputStream> filesMap = new HashMap<String, ByteArrayOutputStream>();
		try {
			while ((entry = zipInp.getNextEntry()) != null) {
				int count = 0;
				byte databuff[] = new byte[2048];
				if (!entry.isDirectory()) {
					fileName = entry.getName();

					bos = new ByteArrayOutputStream();
					while ((count = zipInp.read(databuff, 0, 2048)) != -1) {
						bos.write(databuff, 0, count);
					}

					filesMap.put(fileName, bos);
					bos.close();
					bos.flush();
					bos = null;
				}
				logger.debug("End: " + getClass().getName() + ":unZip()");
			}

		} catch (ZipException zipe) {
			throw new FileSystemUtilException("WC0420", zipe);
		} catch (IllegalArgumentException iae) {
			throw new FileSystemUtilException("WC0421", iae);
		} catch (IOException e) {
			throw new FileSystemUtilException("WC0420", e);
		} catch (Exception ex) {
			throw new FileSystemUtilException("F00002", null, Level.ERROR, ex);
		} finally {
			try {
				if (bos != null) {
					bos.close();
				}
				if (zipInp != null) {
					zipInp.close();
				}
			} catch (IOException e1) {
				//logger.l7dlog(Level.WARN, "WL0634", e1);
				logger.warn("Warn::",e1);
			}
		}
		return filesMap;
	}

	/**
	 * This Method is used for to zip the content and zip file will be created
	 * in specified location.
	 *
	 * @param zipFileDetails
	 *            the zip file details
	 * @throws Exception
	 *             the exception
	 */
	public void zipFile(ZipFileDetails zipFileDetails) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":zipFile()");
		ZipOutputStream zipout = null;
		FileOutputStream zipfileobj = null;
		String contentBuffer = null;
		HashMap<String, String> contentHashMap = null;
		String zipFilename = null;
		String charset = "";
		String ebizhome = System.getProperty("EBIZHOME");
		try {
			if (zipFileDetails.getFilename() == null && zipFileDetails.getFilename().length() != 0) {
				throw new FileSystemUtilException("WC0420");
			}
			if (zipFileDetails.getZipContentList() == null && zipFileDetails.getZipContentList().size() != 0) {
				throw new FileSystemUtilException("WC0420");
			}
			if (zipFileDetails.getCharset() != null) {
				charset = zipFileDetails.getCharset();
			}
			if (ebizhome != null) {
				zipFilename = ebizhome + zipFileDetails.getLocationfolder() + Constants.file_separator;
			} else {
				zipFilename = Constants.EBIZHOME + zipFileDetails.getLocationfolder() + Constants.file_separator;
			}
			if (zipFilename != null) {
				new File(zipFilename).mkdirs();
			}
			zipFilename = zipFilename + zipFileDetails.getFilename() + ".zip";
			contentHashMap = zipFileDetails.getZipContentList();
			zipfileobj = new FileOutputStream(zipFilename);
			zipout = new ZipOutputStream(zipfileobj);
			for (String contentFileName : contentHashMap.keySet()) {
				contentBuffer = contentHashMap.get(contentFileName).toString();
				zipout.putNextEntry(new ZipEntry(contentFileName + ".html"));
				byte[] zipbuf = contentBuffer.getBytes(charset);
				zipout.write(zipbuf, 0, zipbuf.length);
				zipout.flush();
			}
			zipout.finish();
			// Complete the ZIP file
		} catch (ZipException zipe) {
			throw new FileSystemUtilException(zipe);
		} catch (FileSystemUtilException ide) {
			throw new FileSystemUtilException(ide);
		} catch (IllegalArgumentException iae) {
			throw new FileSystemUtilException(iae);
		} catch (IOException e) {
			throw new FileSystemUtilException(e);
		} catch (Exception ex) {
			throw new FileSystemUtilException(ex);
		} finally {
			contentBuffer = null;
			contentHashMap = null;
			if (zipout != null) {
				try {
					zipout.close();
				} catch (IOException e) {

					throw new FileSystemUtilException(e);
				}
				zipout = null;
			}
			if (zipfileobj != null) {
				try {
					zipfileobj.close();
				} catch (IOException e) {

					throw new FileSystemUtilException(e);
				}
				zipfileobj = null;
			}
		}
		logger.debug("End: " + getClass().getName() + ":zipFile()");
	}

	/**
	 * 
	 * Method Name : disconnect Description : The Method "disconnect" is used
	 * for Date : May 22, 2016, 4:53:54 PM
	 * 
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public void disconnect() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * 
	 * Method Name : putFile Description : The Method "putFile" is used for Date
	 * : May 22, 2016, 4:53:50 PM
	 * 
	 * @param is
	 * @param targetLocation
	 * @param fileName
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public void putFile(InputStream is, String targetLocation) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : putFile Description : The Method "putFile" is used for Date
	 * : May 22, 2016, 4:53:48 PM
	 * 
	 * @param input
	 * @param targetLocation
	 * @param fileName
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public void putFile(String input, String targetLocation) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * 
	 * Method Name : getFile Description : The Method "getFile" is used for Date
	 * : May 22, 2016, 4:53:45 PM
	 * 
	 * @param filepath
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public InputStream getFile(String filepath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * 
	 * Method Name : truncateFile Description : The Method "truncateFile" is
	 * used for Date : May 22, 2016, 4:53:39 PM
	 * 
	 * @param filePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean truncateFile(String filePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : renameFile Description : The Method "renameFile" is used
	 * for Date : May 28, 2016, 6:21:58 PM
	 * 
	 * @param fileName
	 * @param newFileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */
	@Override
	public boolean renameFile(String fileName, String newFileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : removeFolder Description : The Method "removeFolder" is
	 * used for Date : May 22, 2016, 4:53:31 PM
	 * 
	 * @param path
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean removeFolder(String path) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : copyFolder Description : The Method "copyFolder" is used
	 * for Date : May 22, 2016, 4:53:28 PM
	 * 
	 * @param path
	 * @param dirName
	 * @param newDirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean copyFolder(String path, String dirName, String newDirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : renameFolder Description : The Method "renameFolder" is
	 * used for Date : May 22, 2016, 4:53:23 PM
	 * 
	 * @param path
	 * @param oldDirName
	 * @param newDirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean renameFolder(String oldDirName, String newDirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : convertUnixFileToDOS Description : The Method
	 * "convertUnixFileToDOS" is used for Date : May 22, 2016, 4:53:20 PM
	 * 
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean convertUnixFileToDOS(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : convertDOSFileToUnix Description : The Method
	 * "convertDOSFileToUnix" is used for Date : May 22, 2016, 4:53:17 PM
	 * 
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean convertDOSFileToUnix(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : convertFileToUTFFormat Description : The Method
	 * "convertFileToUTFFormat" is used for Date : May 22, 2016, 4:53:14 PM
	 * 
	 * @param fileName
	 * @param encodeFormat
	 * @param newFileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean convertFileToUTFFormat(String fileName, String encodeFormat, String newFileName)
			throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : downloadFile Description : The Method "downloadFile" is
	 * used for Date : May 22, 2016, 4:53:10 PM
	 * 
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean downloadFile() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : uploadFile Description : The Method "uploadFile" is used
	 * for Date : May 22, 2016, 4:53:07 PM
	 * 
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : listFiles Description : The Method "listFiles" is used for
	 * Date : May 22, 2016, 4:53:04 PM
	 * 
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public String[] listFiles() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

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
	 * 
	 * Method Name : removeFile Description : The Method "removeFile" is used
	 * for Date : May 22, 2016, 4:53:01 PM
	 * 
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean removeFile(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : moveFile Description : The Method "moveFile" is used for
	 * Date : May 22, 2016, 4:52:58 PM
	 * 
	 * @param srcfilePath
	 * @param destfilePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean moveFile(String srcfilePath, String destfilePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);
	}

	/**
	 * 
	 * Method Name : uploadFiles Description : The Method "uploadFiles" is used
	 * for Date : May 22, 2016, 4:52:55 PM
	 * 
	 * @param is
	 * @param targetLocation
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean uploadFiles(InputStream is, String targetLocation, String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : createFolder Description : The Method "createFolder" is
	 * used for Date : May 22, 2016, 4:52:52 PM
	 * 
	 * @param dirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean createFolder(String dirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : listFolders Description : The Method "listFolders" is used
	 * for Date : May 22, 2016, 4:52:49 PM
	 * 
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public String[] listFolders() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name : getSampleData Description : The Method "getSampleData" is
	 * used for Date : May 22, 2016, 4:52:46 PM
	 * 
	 * @param dataLength
	 * @param charset
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name 	: getUserHome
	 * Description 		: The Method "getUserHome" is used for 
	 * Date    			: Jul 21, 2016, 8:06:54 PM
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
}
