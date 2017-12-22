package com.fileutils.plugin;

import java.io.InputStream;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
public interface FileSystemUtilsPlugin {

	/**
	 * Sets the context.
	 *
	 * @param fileSystemUtilBean
	 *            the new context
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void setContext(FileUtilContext fileSystemUtilBean) throws FileSystemUtilException;

	/**
	 * Connect.
	 *
	 * 
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void connect() throws FileSystemUtilException;

	/**
	 * Disconnect.
	 *
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void disconnect() throws FileSystemUtilException;

	/**
	 * Put file.
	 *
	 * @param is
	 *            the is
	 * @param targetLocation
	 *            the target location
	 * @param fileName
	 *            the file name
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void putFile(InputStream is, String targetLocation) throws FileSystemUtilException;

	/**
	 * Put file.
	 *
	 * @param input
	 *            the input
	 * @param targetLocation
	 *            the target location
	 * @param fileName
	 *            the file name
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void putFile(String input, String targetLocation) throws FileSystemUtilException;

	/**
	 * Gets the file.
	 *
	 * @param filepath
	 *            the filepath
	 * @param fileName
	 *            the file name
	 * @return the file
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public InputStream getFile(String filepath) throws FileSystemUtilException;

	/**
	 * List files.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public String[] listFiles() throws FileSystemUtilException;
	
	/**
	 * List files.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @param isFilesOnly the is files only
	 * @return the string[]
	 * @throws FileSystemUtilException the file system util exception
	 */
	public String[] listFiles(boolean isFoldersRequired, boolean isSort, boolean isFilesOnly) throws FileSystemUtilException;

	/**
	 * Removes the file.
	 *
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean removeFile(String fileName) throws FileSystemUtilException;

	/**
	 * Truncate file.
	 *
	 * @param filePath
	 *            the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean truncateFile(String filePath) throws FileSystemUtilException;

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
	public boolean moveFile(String srcfilePath, String destfilePath) throws FileSystemUtilException;

	/**
	 * Rename file.
	 *
	 * @param oldFileName
	 *            the old file name
	 * @param newFileName
	 *            the new file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean renameFile(String oldFileName, String newFileName) throws FileSystemUtilException;

	/**
	 * Upload files.
	 *
	 * @param is
	 *            the is
	 * @param targetLocation
	 *            the target location
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean uploadFiles(InputStream is, String targetLocation, String fileName) throws FileSystemUtilException;

	/**
	 * Creates the folder.
	 *
	 * @param dirName
	 *            the dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean createFolder(String dirName) throws FileSystemUtilException;

	/**
	 * List folders.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public String[] listFolders() throws FileSystemUtilException;

	/**
	 * Removes the folder.
	 *
	 * @param path
	 *            the path
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean removeFolder(String path) throws FileSystemUtilException;

	/**
	 * Copy folder.
	 *
	 * @param path
	 *            the path
	 * @param dirName
	 *            the dir name
	 * @param newDirName
	 *            the new dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean copyFolder(String path, String dirName, String newDirName) throws FileSystemUtilException;

	/**
	 * Rename folder.
	 *
	 * @param path
	 *            the path
	 * @param dirName
	 *            the dir name
	 * @param newDirName
	 *            the new dir name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean renameFolder(String oldDirName, String newDirName) throws FileSystemUtilException;

	/**
	 * Convert unix file to dos.
	 *
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean convertUnixFileToDOS(String fileName) throws FileSystemUtilException;

	/**
	 * Convert dos file to unix.
	 *
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean convertDOSFileToUnix(String fileName) throws FileSystemUtilException;

	/**
	 * Convert file to utf format.
	 *
	 * @param fileName
	 *            the file name
	 * @param encodeFormat
	 *            the encode format
	 * @param newFileName
	 *            the new file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean convertFileToUTFFormat(String fileName, String encodeFormat, String newFileName)
			throws FileSystemUtilException;

	/**
	 * Upload file.
	 *
	 * @param fileName
	 *            the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean uploadFile(String fileName) throws FileSystemUtilException;

	/**
	 * Download file.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean downloadFile() throws FileSystemUtilException;

	/**
	 * Checks if is zip.
	 *
	 * @param remoteFilePath
	 *            the remote file path
	 * @return true, if is zip
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public boolean isZip(String remoteFilePath) throws FileSystemUtilException;

	/**
	 * Zip file.
	 *
	 * @param zipFileDetails
	 *            the zip file details
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public void zipFile(ZipFileDetails zipFileDetails) throws FileSystemUtilException;

	/**
	 * Un zip.
	 *
	 * @param localFilePath
	 *            the local file path
	 * @param remLocal
	 *            the rem local
	 * @return the string
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public String unZip(String localFilePath, boolean remLocal) throws FileSystemUtilException;

	/**
	 * Stream un zipper.
	 *
	 * @param sampleBytes
	 *            the sample bytes
	 * @return the byte[]
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public byte[] streamUnZipper(byte[] sampleBytes) throws FileSystemUtilException;

	/**
	 * Gets the sample data.
	 *
	 * @param dataLength
	 *            the data length
	 * @param charset
	 *            the charset
	 * @return the sample data
	 * @throws FileSystemUtilException
	 *             the file system util exception
	 */
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException;
    
	/**
	 * Gets the user home.
	 *
	 * @return the user home
	 * @throws FileSystemUtilException the file system util exception
	 */
	public String getUserHome() throws FileSystemUtilException;

}
