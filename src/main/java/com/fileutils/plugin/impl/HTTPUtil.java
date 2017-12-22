
package com.fileutils.plugin.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.SSLHandshakeException;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.FileSystemTypeConstants;

import ch.qos.logback.classic.Logger;


public class HTTPUtil implements FileSystemUtilsPlugin{
	
	/** The logger. */
	Logger logger = (Logger)LoggerFactory.getLogger(getClass());
	
	
	/** The url. */
	private String url="";
	
	/** The local file path. */
	private String localFilePath="";
	
	/** The url con. */
	URLConnection urlCon = null;
	
	/** The remote file. */
	URL remoteFile = null;
	
	/** The is sample. */
	private boolean isSample=false;
	
	/** The sample data. */
	private String sampleData;
	
	/** The stop. */
	private boolean stop = false;
	
	/** The read file data. */
	public boolean readFileData = false; // Used to return the remote file data
	
	/** The file data. */
	private StringBuffer fileData = new StringBuffer(); // Used to return the remote file data
		
	/* BUG-FIX thread hang due to network outage */
	/** The read time out. */
	//initializing with 0 means no timeout(wait forever)
	private int readTimeOut = 0;
	
	
	
	/** The bundle. */
	//private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE);
	
	
	/**
	 * Instantiates a new HTTP util.
	 *
	 * @param configBean
	 *            the config bean
	 * @throws FileSystemUtilException
	 */
	public HTTPUtil() {
	//	logger.setResourceBundle(bundle);
	}
	
	/**
	 * 
	 * Method Name 	: setConfig
	 * Description 		: The Method "setConfig" is used for 
	 * Date    			: May 20, 2016, 2:25:31 PM
	 * @param fileSystemUtilBean
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":setParams()");
		url=context.getUrlString(); //setting the url string.
		if(url == null)
		{
			url = "";
		}
		localFilePath =context.getLocalFilePath(); //setting the localfilepath
		if(localFilePath == null )
		{
			localFilePath = "";
		}
		logger.debug("Params set are : url - '"+url+"' localfilepath - '"+localFilePath+"'");
		logger.debug("End: "+getClass().getName()+":setParams()");
	}
	

	/**
	 * 
	 * Method Name 	: connect
	 * Description 		: The Method "connect" is used for 
	 * Date    			: May 20, 2016, 2:22:48 PM
	 * @param isUIModule
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public void connect() throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":connect()");
		try {
			remoteFile = new URL(url);
		} catch (MalformedURLException e) {

			throw new FileSystemUtilException("ET0023", null, Level.ERROR, e);
		}
		try {
			urlCon = remoteFile.openConnection();
			/* BUG-FIX thread hang due to network outage */
			urlCon.setReadTimeout(readTimeOut);
			urlCon.connect();
		} catch (ConnectException ce) {

			throw new FileSystemUtilException("ET0024", null, Level.ERROR, ce);

		} catch (SSLHandshakeException she) {

			throw new FileSystemUtilException("ET0111", null, Level.ERROR, she);

		} catch (IOException ie) {

			throw new FileSystemUtilException("ET0024", null, Level.ERROR, ie);

		} catch (IllegalArgumentException e) {

			throw new FileSystemUtilException("ET0026", null, Level.ERROR, e);

		} catch (Exception ue) {

			throw new FileSystemUtilException("WC0000", null, Level.ERROR, ue);

		}
		logger.debug("End: " + getClass().getName() + ":connect()");

	}

	/**
	 * 
	 * Method Name 	: disconnect
	 * Description 		: The Method "disconnect" is used for 
	 * Date    			: May 20, 2016, 2:22:57 PM
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public void disconnect() throws FileSystemUtilException {
		urlCon = null;
	}

	/**
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 20, 2016, 2:23:00 PM
	 * @param is
	 * @param targetLocation
	 * @param fileName
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void putFile(InputStream is, String targetLocation) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 20, 2016, 2:23:20 PM.
	 *
	 * @param input the input
	 * @param targetLocation the target location
	 * @return 		:
	 * @throws FileSystemUtilException the file system util exception
	 */
	
	@Override
	public void putFile(String input, String targetLocation) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: getFile
	 * Description 		: The Method "getFile" is used for 
	 * Date    			: May 20, 2016, 2:23:28 PM
	 * @param filepath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public InputStream getFile(String filepath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	

	

	/**
	 * 
	 * Method Name 	: truncateFile
	 * Description 		: The Method "truncateFile" is used for 
	 * Date    			: May 20, 2016, 2:23:41 PM
	 * @param filePath
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean truncateFile(String filePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}
	
	/**
	 *  
	 * This method is used to download the sample data.
	 *
	 * @param dataLength the data length
	 * @param charset the charset
	 * @return the sample data
	 * @throws PrePublisherException the pre publisher exception
	 */
	@Override
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":getSampleData()");	
		isSample=true;
		getFileData(dataLength,charset);
		logger.debug("End: "+getClass().getName()+":getSampleData()");	
		return sampleData;
	}
	
	/**
	 * 
	 * Method Name 	: downloadFile
	 * Description 		: The Method "downloadFile" is used for 
	 * Date    			: May 20, 2016, 2:23:49 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean downloadFile() throws FileSystemUtilException {
		String extractFile=null;
		logger.debug("Begin: "+getClass().getName()+":download()");	
		isSample=false;		
		getFileData(0,"");
		if(stop)
        {
           	throw new FileSystemUtilException("999");
        }
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		if(zu.isZip(localFilePath))
		{
			extractFile=zu.unZip(localFilePath,true);
			localFilePath=extractFile;
		    //unsip the localfilepath (a common method at import util level can be used)
		    //rename the extracted file to localfilepath+".txt
		    //delete the localfilepath
		}
		zu=null;
		logger.debug("End: "+getClass().getName()+":download()");	
		return true;
	}

	
	/**
	 * 
	 * Method Name 	: renameFile
	 * Description 		: The Method "renameFile" is used for 
	 * Date    			: May 20, 2016, 2:24:04 PM
	 * @param filePath
	 * @param fileName
	 * @param newFileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean renameFile(String fileName, String newFileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
		
	}

	
	/**
	 * 
	 * Method Name 	: removeFolder
	 * Description 		: The Method "removeFolder" is used for 
	 * Date    			: May 20, 2016, 2:24:18 PM
	 * @param path
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public boolean removeFolder(String path) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: copyFolder
	 * Description 		: The Method "copyFolder" is used for 
	 * Date    			: May 20, 2016, 2:24:23 PM
	 * @param path
	 * @param dirName
	 * @param newDirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean copyFolder(String path, String dirName, String newDirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: renameFolder
	 * Description 		: The Method "renameFolder" is used for 
	 * Date    			: May 20, 2016, 2:24:32 PM
	 * @param path
	 * @param dirName
	 * @param newDirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public boolean renameFolder(String oldDirName, String newDirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: convertUnixFileToDOS
	 * Description 		: The Method "convertUnixFileToDOS" is used for 
	 * Date    			: May 20, 2016, 2:24:37 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public boolean convertUnixFileToDOS(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: convertDOSFileToUnix
	 * Description 		: The Method "convertDOSFileToUnix" is used for 
	 * Date    			: May 20, 2016, 2:24:39 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean convertDOSFileToUnix(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: convertFileToUTFFormat
	 * Description 		: The Method "convertFileToUTFFormat" is used for 
	 * Date    			: May 20, 2016, 2:24:44 PM
	 * @param fileName
	 * @param encodeFormat
	 * @param newFileName
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean convertFileToUTFFormat(String fileName, String encodeFormat, String newFileName)
			throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}
	
	
	
	
	/**
	 * This method downloads the specified amount of data or 
	 * the complete file to the localfile from the httpserver.
	 *
	 * @param dLength the d length
	 * @param charset the charset
	 * @return the file data
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	private void getFileData(int dLength,String charset) throws FileSystemUtilException
	{
		InputStream urlIn = null;
		FileOutputStream fOut=null;
		ByteArrayOutputStream bOut = null;
		long fileLen = 0;
		long dataLength = dLength;
		logger.debug("Begin: "+getClass().getName()+":getFileData()");
		connect();   
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		try
		{	
			logger.debug("Opening input stream from url connection");			
			try 
			{
				// Open the input streams for the remote file 
				urlIn=urlCon.getInputStream();
				logger.debug("Input stream from url connection is opened");				
			}
			catch(FileNotFoundException fnfEx) 
			{					
				if(isSample)
				{
				    throw new FileSystemUtilException("ET0025");
				}
				else if(readFileData)
				{
					throw new FileSystemUtilException("WC0574");
				}
				else
				{
				    throw new FileSystemUtilException("ET0025",null,Level.ERROR,fnfEx);
				}
			}
			if(readFileData)
			{
				fileData.delete(0, fileData.length());
			}
			//Open the output streams for saving this file on disk
			if(isSample || readFileData)
			{
				bOut = new ByteArrayOutputStream();				
			}
			else
			{
				try 
				{
					if(!readFileData){
						logger.debug("Opening output stream to file "+localFilePath);
						fOut=new FileOutputStream(localFilePath);
						logger.debug("Output stream to file "+localFilePath+" is opened");
					}
				} 
				catch(FileNotFoundException fnfEx) 
				{
				    if(isSample)
					{
					    throw new FileSystemUtilException("ET0028");
					}
					else
					{
					    throw new FileSystemUtilException("ET0028",null,Level.ERROR,fnfEx);
					}
				}				
			}
			// Read the remote file
			int b=0;
			int l=0;
			int len=10240;
			fileLen = urlCon.getContentLength();
			if(fileLen != -1)
			{
				logger.debug("dataLength is : "+dataLength+" - fileLength is : "+fileLen+" - bufferLength is : "+len);
				if(dataLength == 0)
				{
					dataLength = fileLen;
				}
				else if(dataLength >= fileLen)
				{
					dataLength = fileLen;
				}
				
				if(len >= dataLength)
				{
					len = (int)dataLength;
				}			
				logger.debug("dataLength is : "+dataLength+" - fileLength is : "+fileLen+" - bufferLength is : "+len);

				byte[] barray = new byte[len];
				logger.debug("Reading data from url connection");
				while( b < dataLength)
				{
					l=urlIn.read(barray,0,len);
					if(l != -1)
					{
						b=b+l;
						if(isSample || readFileData)
						{
							bOut.write(barray,0,l);
						}
						else
						{			
							fOut.write(barray,0,l);
						}
					}
					//check stop flag, if true throw FileSystemUtilException with errorcode 999
                    if(stop)
                    {
                    	throw new FileSystemUtilException("999");
                    }
				}	
			}
			else
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(urlIn));
				String inputLine = null;
				byte[] inputBytes = null;
				int dataRead = 0;
				while ((inputLine = in.readLine()) != null)
				{
					//ZET #1770 start-- list from remote "http protocol"
					inputBytes = (inputLine+"\n").getBytes();
					//ZET #1770 end
					//inputBytes = inputLine.getBytes();
					dataRead = dataRead+inputBytes.length;
					if(isSample || readFileData)
					{
						bOut.write(inputBytes,0,inputBytes.length);
					}
					else
					{	
						fOut.write(inputBytes,0,inputBytes.length);
					}
					//ZET #1770 start-- list from remote "http protocol"
					if(dataLength > 0 && dataLength <= dataRead)
					//ZET #1770 end
					{
						break;
					}
				    //check stop flag, if true throw FileSystemUtilException with errorcode 999
                    if(stop)
                    {
                    	throw new FileSystemUtilException("999");
                    }
				}
				in.close();
				in = null;
			}
			logger.debug("Reading data from url connection completed");
			if(isSample)
			{
				if(zu.isZip(url))
			    {
			    	byte [] stUnZip=bOut.toByteArray(); 
			    	byte [] sample = zu.streamUnZipper(stUnZip);
			    	bOut.reset();
			    	bOut.write(sample);
			        //create a byte array stream with bOut
			        //unzip the stream here and use that stream
			    }
				sampleData = new String(bOut.toByteArray(),charset);
				logger.debug("Sample data is : "+sampleData);
			}
			if(readFileData)
			{
				fileData.append(bOut.toString(charset));
			}
		}	
		catch(UnsupportedEncodingException use)
		{
		    if(isSample)
			{
			    throw new FileSystemUtilException("ET0028");
			}
			else
			{
			    throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
			}
		}	
		catch(IOException ioe)
		{
		    if(isSample)
			{
			    throw new FileSystemUtilException("ET0028");
			}
			else
			{
			    throw new FileSystemUtilException("ET0021",null,Level.ERROR,ioe);
			}
		}
		catch(FileSystemUtilException ebizEx)
		{
			if(!ebizEx.getErrorCode().equals("999"))
			{
				throw ebizEx;
            }
		}
		
		catch(Exception e)
		{
		    if(isSample)
			{
			    throw new FileSystemUtilException("WC0000");
			}
			else
			{
			    throw new FileSystemUtilException("WC0000",null,Level.ERROR,e);
			}
		}	
		finally 
		{
			try
			{
				if(urlIn != null)
				{
					urlIn.close();
					urlIn = null;
				}
				if(fOut != null)
				{
					fOut.flush(); 
					fOut.close();
					fOut = null;
				}
				if(bOut != null)
				{
					bOut.flush(); 
					bOut.close();
					bOut = null;
				}
			} 
			catch(Exception e)
			{
				//log warning				
				//logger.l7dlog(Level.WARN,"WT0003",e);
				logger.warn("Warn::",e);
			}		 
		}
		logger.debug("End: "+getClass().getName()+":getFileData()");
	}


	/**
	 * 
	 * Method Name 	: isZip
	 * Description 		: The Method "isZip" is used for 
	 * Date    			: May 20, 2016, 2:24:59 PM
	 * @param remoteFilePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean isZip(String remoteFilePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}


	/**
	 * 
	 * Method Name 	: zipFile
	 * Description 		: The Method "zipFile" is used for 
	 * Date    			: May 20, 2016, 2:25:04 PM
	 * @param zipFileDetails
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void zipFile(ZipFileDetails zipFileDetails) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: uploadFile
	 * Description 		: The Method "uploadFile" is used for 
	 * Date    			: May 20, 2016, 2:25:12 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	

	/**
	 * 
	 * Method Name 	: listFiles
	 * Description 		: The Method "listFiles" is used for 
	 * Date    			: May 20, 2016, 2:25:43 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String[] listFiles() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
		
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
	 * Method Name 	: removeFile
	 * Description 		: The Method "removeFile" is used for 
	 * Date    			: May 20, 2016, 2:25:50 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean removeFile(String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
		
	}

	/**
	 * 
	 * Method Name 	: moveFile
	 * Description 		: The Method "moveFile" is used for 
	 * Date    			: May 20, 2016, 2:25:55 PM
	 * @param srcfilePath
	 * @param destfilePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean moveFile(String srcfilePath, String destfilePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
		
	}

	/**
	 * 
	 * Method Name 	: uploadFiles
	 * Description 		: The Method "uploadFiles" is used for 
	 * Date    			: May 20, 2016, 2:25:59 PM
	 * @param is
	 * @param targetLocation
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean uploadFiles(InputStream is, String targetLocation, String fileName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: createFolder
	 * Description 		: The Method "createFolder" is used for 
	 * Date    			: May 20, 2016, 2:26:05 PM
	 * @param dirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean createFolder(String dirName) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: listFolders
	 * Description 		: The Method "listFolders" is used for 
	 * Date    			: May 20, 2016, 2:26:10 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String[] listFolders() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}


	/**
	 * 
	 * Method Name 	: unZip
	 * Description 		: The Method "unZip" is used for 
	 * Date    			: May 20, 2016, 2:26:15 PM
	 * @param localFilePath
	 * @param remLocal
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String unZip(String localFilePath, boolean remLocal) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}


	/**
	 * 
	 * Method Name 	: streamUnZipper
	 * Description 		: The Method "streamUnZipper" is used for 
	 * Date    			: May 20, 2016, 2:26:19 PM
	 * @param sampleBytes
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public byte[] streamUnZipper(byte[] sampleBytes) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: getUserHome
	 * Description 		: The Method "getUserHome" is used for 
	 * Date    			: Jul 21, 2016, 8:07:15 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String getUserHome() throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

}
