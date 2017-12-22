
package com.fileutils.plugin.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.enterprisedt.net.ftp.FTPClient;
import com.enterprisedt.net.ftp.FTPException;
import com.enterprisedt.net.ftp.FTPFile;
import com.enterprisedt.net.ftp.FTPTransferType;
import com.fileutils.bo.FileSource;
import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.bo.ZtByteArrayOutputStream;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.ConvertFiles;
import com.fileutils.util.FileSystemTypeConstants;

import ch.qos.logback.classic.Logger;

public class FTPUtil implements FileSystemUtilsPlugin{
	
	/** The host. */
	private String host;
	
	/** The port. */
	private int port;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The remote file path. */
	private String remoteFilePath;
	
	/** The local file path. */
	private String localFilePath;
	
	/** The fc. */
	private FTPClient fc;
	
	/** The sample data. */
	private String sampleData;
	
	/** The buffersize. */
	private final int buffersize=10240;
	
	/** The stop. */
	private boolean stop = false;
	
	/** The read time out. */
	//initializing with 0 means no timeout(wait forever)
	private int readTimeOut = 0;
	
	/** The logger. */
	Logger logger = (Logger)LoggerFactory.getLogger(getClass());
	
	/**
	 * Default constructor.
	 * 
	 * @throws FileSystemUtilException
	 */
	public FTPUtil()  {
	}
	
	/**
	 * 
	 * Method Name 	: setConfig
	 * Description 		: The Method "setConfig" is used for 
	 * Date    			: May 20, 2016, 2:17:06 PM
	 * @param context
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":setParams()");
		host=context.getHostname();
		username=context.getUsername();
		password=context.getPassword();
		port = context.getPort();	
		if(port == 0)
		{
			port = FileSource.DEFAULT_FTP_PORT;
		}
		remoteFilePath=context.getRemoteFilePath();
		localFilePath = context.getLocalFilePath();
		/* BUG-FIX thread hang due to network outage - START */
		//The calls from older versions may not include this param
		readTimeOut = context.getReadTimeOut();
		/* BUG-FIX thread hang due to network outage - END */
		logger.debug("Params set are : host - '"+host+"' username - '"+username+"' - port '"+port+"' remotefilepath - '"+remoteFilePath+"' localfilepath - '"+localFilePath+"'");
		logger.debug("End: "+getClass().getName()+":setParams()");
	}
		
	/**
	 * This method connects to the FTP server and logs in with the given username and password.
	 *
	 * @param fromUI the from ui
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public void connect() throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":connect()");
		fc=new FTPClient();			
		try 
		{
			fc.setRemoteAddr(InetAddress.getByName(host));		
			fc.setControlPort(port);
			/* BUG-FIX thread hang due to network outage */
			fc.setTimeout(readTimeOut);
			logger.debug("Connecting to "+host+" on port "+port);		
			fc.connect();
			logger.debug("Sucessfully connected");		
		}
		catch (IOException ioe) 
		{   
			logger.info(new Date()+":: Unable to connect with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw new FileSystemUtilException("ET0019",null,Level.ERROR,ioe);		
		}
		catch (FTPException ftpe) 
		{   
			
			logger.info(new Date()+":: Unable to connect with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			logger.error(ftpe.getMessage(),ftpe);
			throw new FileSystemUtilException("ET0020",null,Level.ERROR,ftpe);	
		}
		
		try 
		{
			logger.debug("logging in with username '"+username+"' and password");
			fc.login(username,password);
			logger.debug("Succesfully logged in");			
		} 
		catch (FTPException e) 
		{	
			logger.info(new Date()+":: Unable to connect with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			String errMessage = e.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			logger.info("\n errorMessage = "+errMessage);
			logger.warn(errMessage,e);
			if(e.getReplyCode() == 530)
			{
				throw new FileSystemUtilException("ET0021",null,Level.ERROR,e);
			}
			else
			{
				throw new FileSystemUtilException("ET0614",e);
			}
		}
		catch (IOException ie) 
		{   ie.printStackTrace();
			logger.info(new Date()+":: Unable to connect with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ie);		
		} 
		logger.debug("End: "+getClass().getName()+":connect()");
	}
	
	/**
	 * Stop.
	 *
	 * @throws FileSystemUtilException the file system util exception
	 */
	private void stop()throws FileSystemUtilException
    {
			try 
			{	
				if(fc != null)
				{
					fc.cancelTransfer();
				}
				stop=true;
			}
			catch (RuntimeException e) 
			{
				logger.error(e.getMessage(),e);
				throw new FileSystemUtilException("ET0229",null,Level.ERROR,e);	
			}	
    }
	
	/**
	 * This method is used to get list of directories in the given path.
	 *
	 * @return the folders
	 * @throws FileSystemUtilException the pre publisher exception
	 */	
	public String[] getFolders() throws FileSystemUtilException	
	{					
		return listFiles(true);
	}
	
	/**
	 *  
	 * This method gets the list of files in a specified directory from the ftp server.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException the invalid data exception
	 */
	@Override
	public String[] listFiles() throws FileSystemUtilException
	{		
		return listFiles(false);
	}
	
	/**
	 *  
	 * This method gets the list of files in a specified directory from the ftp server
	 * and returns filenames array.
	 * This method gets the list of directories in a specified directory from the ftp server
	 * and returns foldernames array.when this is called from getFolders() mehtod.
	 *
	 * @param isFoldersRequired the is folders required
	 * @return the string[]
	 * @throws FileSystemUtilException the invalid data exception
	 */	
	private String[] listFiles(boolean isFoldersRequired) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		String[] fileslist = null;
		Vector<String> list = null;		
		FTPFile[] ftpfiles = null;
		String remoteDir = null;
		String remoteFileName = null;
		try 
		{	
			connect();			
			int lastSeperatorIndex = -1;
			
			if( remoteFilePath.lastIndexOf("/") != -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("/");
			}else if( remoteFilePath.lastIndexOf("\\") != -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("\\");
			}

			if(lastSeperatorIndex == remoteFilePath.length()-1)
			{
				remoteDir = remoteFilePath;
				remoteFileName = "";
				logger.debug("file path ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				try
				{
					if(!"".equals(remoteDir))
					{
						fc.chdir(remoteDir);
					}
				}
				catch(FTPException e)
				{	
					
					String errMessage = e.getMessage();
					if(errMessage == null)
					{
						errMessage = "";
					}

					if(e.getReplyCode() == 550)
					{
						//invlid directory/file			
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("ET0017");
					}
					else
					{
						//unknown
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("EL0359");
					}	
				}
			}
			else
			{
				remoteDir = remoteFilePath.substring(0,lastSeperatorIndex+1);
				remoteFileName = remoteFilePath.substring(lastSeperatorIndex+1,remoteFilePath.length());
				logger.debug("file path not ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				try
				{
					if(!"".equals(remoteDir))
					{
						fc.chdir(remoteDir);
					}
				}
				catch(FTPException e)
				{
					String errMessage = e.getMessage();
					if(errMessage == null)
					{
						errMessage = "";
					}
					if(e.getReplyCode() == 550)
					{
						//invlid directory/file
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("ET0017");
					}
					else
					{
						//unknown
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("EL0359");
					}					
				}				
				
				if(!remoteFileName.startsWith("*.") && !remoteFileName.endsWith(".*"))
				{
					logger.debug("file/directory name "+remoteFileName+" not started with *. or ends wirh .*");
					try
					{
						fc.chdir(remoteFileName);
						logger.debug(remoteFileName+" is a directory");
						remoteFileName = "";
					}
					catch(FTPException e)
					{
						String errMessage = e.getMessage();
						if(errMessage == null)
						{
							errMessage = "";
						}
						if(e.getReplyCode() != 550)
						{	
							//unknown
							logger.error(e.getMessage(),e);
							throw new FileSystemUtilException("EL0359");
						}
						else
						{
							boolean isFile = false;
							String[] tempFileNames = fc.dir();
							for(int j = 0; j < tempFileNames.length; j++)
							{
								if(remoteFileName.equals(tempFileNames[j]))
								{
									isFile = true;
								}
							}
							if(!isFile)
							{
								//invlid directory/file
								logger.error(e.getMessage(),e);
								throw new FileSystemUtilException("ET0017");
							}
							logger.debug(remoteFileName+" is a file");
						}
					}					
				}
			}
			logger.debug("Before getting list of files : current dir "+fc.pwd()+" getting files list for "+remoteFileName);
			try
			{
				ftpfiles = fc.dirDetails(remoteFileName);
			}
			catch(FTPException e)
			{
				logger.error("Exception ::"+e.getMessage(),e);
				throw new FileSystemUtilException("ET0023");
			}
			catch(ParseException pe)
			{	
				logger.error("Exception::"+pe.getMessage(),pe);
				throw new FileSystemUtilException("ET0023");
			}		
			//below vector used to store fileNames and Dir names
			list = new Vector<String>();
			for(int i = 0; i < ftpfiles.length; i++)
			{				
				FTPFile tempFile = ftpfiles[i];
				String tempFileName = null;				
				if(tempFile.isDir())
				{
					tempFileName = tempFile.getName()+"/";
					if(isValidString(tempFileName))
						list.add(""+tempFileName); 
					logger.debug("directory name "+tempFileName);					
				}
				else
				{
					if(!isFoldersRequired)
					{
						tempFileName = tempFile.getName();
						if(isValidString(tempFileName)) 
							list.add(""+tempFileName);
						logger.debug("file name "+tempFileName);
					}
				}				
				tempFileName = null;
				tempFile = null;
			}
			fileslist = new String[list.size()];
			//loop used to copy folder names to String array
			for(int i = 0; i < fileslist.length; i++)
			{	
				fileslist[i]= list.get(i).toString();				
			}						
			logger.debug("Got files list of size "+fileslist.length);
		} 
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022");			
		} 
		catch(FileSystemUtilException ebizEx)
		{
			throw ebizEx;
		}
		catch(Exception ex)
		{	logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("EL0359");			
		}
		finally
		{
			if(list != null)
			{
				list.clear();
				list = null;
			}
			
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":listFiles()");
		return fileslist;		
	}
	
	/**
	 * This method disconnects from the FTP Server.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public void disconnect() throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":disconnect()");	
		try 
		{
			logger.debug("Disconnecting from server");
			if(fc.connected()){
			fc.quit();
			}
			logger.debug("Disconnected successfully");
		} 
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			String errMessage = ioe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			logger.warn(errMessage);
		} 
		catch(FTPException ftpe) 
		{			
			logger.error(ftpe.getMessage(),ftpe);
			String errMessage = ftpe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			logger.warn(errMessage);					
		}
		logger.debug("End: "+getClass().getName()+":disconnect()");
		
		
	}
	
	/**
	 *  
	 * This method gets the specified amount of sample data from the FTP server and returns it.
	 *
	 * @param dataLength the data length
	 * @param charset the charset
	 * @return the sample data
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException
	{		
		logger.debug("Begin: "+getClass().getName()+":getSampleData()");	
		connect(); 
		ZtByteArrayOutputStream bos = new ZtByteArrayOutputStream(dataLength,fc);
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		try
		{	
			fc.setType(FTPTransferType.BINARY);
			fc.setTransferBufferSize(buffersize);
			fc.get(bos, remoteFilePath);		
			sampleData = bos.getSampleData(charset,zu.isZip(remoteFilePath));
			bos.close();
		}
		catch(UnsupportedEncodingException use)
		{
			logger.error(use.getMessage(),use);
			throw new FileSystemUtilException("ET0111");
		}	
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022");			
		} 
		catch(FTPException ftpe) 
		{
			
			String errMessage = ftpe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			if(ftpe.getReplyCode() == 550)
			{	
				//invalid file name
				logger.error(ftpe.getMessage(),ftpe);
				throw new FileSystemUtilException("FL0070");
			}
			else
			{
				if(errMessage.equals("Transfer was cancelled") || errMessage.equals("Failure writing network stream.")){
					//transfered manually if data size is greaterthan specified data
					try {
						return  bos.getSampleData(charset,zu.isZip(remoteFilePath));
					} catch (UnsupportedEncodingException e) {
						logger.error(e.getMessage(),e);
						logger.error("FTP FileUtil exception due to filesize is > expected file data");
					}
				}else{
					//unknown
					logger.error(ftpe.getMessage(),ftpe);
					throw new FileSystemUtilException("EL0360");
				}
			}				
		}	
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002");				
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":getSampleData()");	
		return sampleData;
	}
	
	
	/**
	 * This method downloads the complete file from the FTP Server to the specified local file.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public boolean downloadFile() throws FileSystemUtilException 
	{
		
		String extractFile=null;
		logger.debug("Begin: "+getClass().getName()+":download()");	
		connect();
		try 
		{
				fc.setType(FTPTransferType.BINARY);
				fc.setTransferBufferSize(buffersize);
				//Get data from the FTP server. Uses the currently set transfer mode.		
				fc.get(localFilePath,remoteFilePath);	
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
		}
		catch(FileSystemUtilException ebize)
		{
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw ebize;
		}
		catch(UnsupportedEncodingException use)
		{
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			String errMessage = ftpe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			logger.info("errMessage ="+errMessage);
			if(ftpe.getReplyCode() == 550)
			{	
				//invalid file name
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,ftpe);
			}
			else
			{
				//unknown
				throw new FileSystemUtilException("EL0361",null,Level.ERROR,ftpe);
			}				
		}	
		catch(Exception ex)
		{	
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port );
			throw new FileSystemUtilException("F00002");				
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":download()");	
		return true;
	}
	
	/**
	 * This method tests the connection and disconnects from FTP Server.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	public boolean test() throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":test()");	
		connect();
		disconnect();
		logger.debug("End: "+getClass().getName()+":test()");	
		return true;
	}
	
	/**
	 * This method is used to get the userhome directory.
	 *
	 * @return the user home
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public String getUserHome() throws FileSystemUtilException
	{
		String userHome = "";
		logger.debug("Begin: "+getClass().getName()+":getUserHome()");	
		connect();
		try
		{
			userHome = fc.pwd();
		}
		catch(IOException ie)
		{
			logger.error("Error occurred at getUserHome ",ie);
			throw new FileSystemUtilException("ET0022");		
		}
		catch(FTPException ie)
		{
			logger.error("Error occurred at getUserHome ",ie);
			throw new FileSystemUtilException("ET0023");
		}
		catch(Exception ie)
		{
			logger.error("Error occurred at getUserHome ",ie);
			throw new FileSystemUtilException("WC0000");
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":getUserHome()");	
		return userHome;
	}
	
	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException
	{
		try	
		{	
			fc.setType(FTPTransferType.BINARY);
			fc.setTransferBufferSize(buffersize);
			if(!fc.existsDirectory(remoteFilePath)){
				boolean isFilePathCreated = false;
				isFilePathCreated = createFolder(remoteFilePath);
				if(isFilePathCreated == false){
					logger.info("Remote FTP FILE Path is not created.remoteFilePath:"+remoteFilePath);	
					throw new FileSystemUtilException("F00002",null,Level.ERROR,new Exception());	
				}
			}
			fc.chdir(remoteFilePath);
			fc.put(localFilePath+"/"+fileName,remoteFilePath+"/"+fileName);
		}		
		catch(UnsupportedEncodingException use)
		{
			logger.error("Error occurred at uploadFile ",use);
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			logger.error("Error occurred at uploadFile ",ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			logger.error("Error occurred at uploadFile ",ftpe);
			String errMessage = ftpe.getMessage();
			ftpe.printStackTrace();
			if(errMessage == null)
			{
				errMessage = "";
			}
			if(ftpe.getReplyCode() == 550)
			{	
				throw new FileSystemUtilException("ET0102",null,Level.ERROR,ftpe);
			}
			else
			{
				throw new FileSystemUtilException("EL0367",null,Level.ERROR,ftpe);
			}				
		}	
		catch(Exception ex)
		{	
			logger.error("Error occurred at uploadFile ",ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}

		return true;
	}
	
	/**
	 * Upload the data to the Image server.
	 *
	 * @param is the is
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @param remoteFilePath1 the remote file path1
	 * @param subDirectories the sub directories
	 * @param isThumb the is thumb
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	public boolean uploadFiles(InputStream is,String targetLocation,String fileName,String remoteFilePath1,String[] subDirectories, boolean isThumb) throws FileSystemUtilException{
		logger.debug("Begin: "+getClass().getName()+":uploadFiles()");	
		connect();		
		StringTokenizer tokens = null;
		String targetDirectories[]  = null;
		try	
		{	
			fc.setType(FTPTransferType.BINARY);
			fc.setTransferBufferSize(buffersize);
			String parentDirOfRemoteFilePath = null;
			
			if(targetLocation!=null && targetLocation.trim().length() > 0){
			 tokens = new StringTokenizer(targetLocation,"/");
			 targetDirectories = new String[tokens.countTokens()];	
			 int dirCount = 0; 
			 while(tokens.hasMoreTokens())
			 {
			 	targetDirectories[dirCount]=tokens.nextToken();
			 	fc.chdir(targetDirectories[dirCount]);
			 	parentDirOfRemoteFilePath = targetDirectories[dirCount];
			 	dirCount++;
			 }
			}
			FTPFile ftpFiles[] = fc.dirDetails(remoteFilePath1);
			if(ftpFiles.length!=0){
				fc.chdir(remoteFilePath1);	
				if(subDirectories != null && subDirectories.length > 0){
						for(int i=0;i< subDirectories.length;i++){
							FTPFile ftpSubs[] = fc.dirDetails(subDirectories[i]);
							if(ftpSubs.length ==0){
								fc.mkdir(subDirectories[i]);
								fc.chdir(subDirectories[i]);
							}else
								fc.chdir(subDirectories[i]);
						}
					}
			}else if(ftpFiles.length==0){
				//Bug 22186: Begin
				//Images are not uploaded using FTP protocol
				fc.cdup();
				FTPFile ftpDirList[] = fc.dirDetails(parentDirOfRemoteFilePath);
				fc.chdir(parentDirOfRemoteFilePath);
				boolean isChangeDir = false;
				if(ftpDirList.length!=0){
					for(int k=0; k < ftpDirList.length;k++){
						if(ftpDirList[k].getName().equalsIgnoreCase(remoteFilePath1)){
							if( ftpDirList[k].isDir()){
								isChangeDir =true ;
							}
						}
					}
				}else if(ftpDirList.length == 0){
					isChangeDir = false;
				}
				if(isChangeDir){
					fc.chdir(remoteFilePath1);
				}else if(!isChangeDir){
					fc.mkdir(remoteFilePath1);
					fc.chdir(remoteFilePath1);
				}
				//Bug 22186: End
				if(subDirectories != null && subDirectories.length > 0){
					for(int i=0;i<subDirectories.length;i++){
						fc.mkdir(subDirectories[i]);
						fc.chdir(subDirectories[i]);
					}
				}
			}
			
			if(isThumb){
				FTPFile thumbFiles[] = fc.dirDetails("thumbs");
				if(thumbFiles.length == 0){
					fc.mkdir("thumbs");
					fc.chdir("thumbs");
				}else
					fc.chdir("thumbs");
			}
			fc.put(is,fileName);
		}
		catch(UnsupportedEncodingException use)
		{
			logger.error("Error occurred at uploadFiles ",use);
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			logger.error("Error occurred at uploadFiles ",ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			logger.error("Error occurred at uploadFiles ",ftpe);
			String errMessage = ftpe.getMessage();
			ftpe.printStackTrace();
			if(errMessage == null)
			{
				errMessage = "";
			}
			if(ftpe.getReplyCode() == 550)
			{	
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,ftpe);
			}
			else
			{
				throw new FileSystemUtilException("EL0367",null,Level.ERROR,ftpe);
			}				
		}	
		catch(Exception ex)
		{	
			logger.error("Error occurred at uploadFiles ",ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		logger.debug("End: "+getClass().getName()+":uploadFiles()");	
		return true;
	}
	
	/**
	 * This method is used to upload the content to the remote location and create the directory structure if not exists.
	 *
	 * @param is the is
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public boolean uploadFiles(InputStream is,String targetLocation,String fileName) throws FileSystemUtilException{
		logger.debug("Begin: "+getClass().getName()+":uploadFiles()");	
		connect();		
		StringTokenizer tokens = null;
		String targetDirectories[]  = null;
		try	
		{	
		    fc.setType(FTPTransferType.BINARY);
		    fc.setTransferBufferSize(buffersize);
		    if(targetLocation!=null && targetLocation.trim().length() > 0){
		    	tokens = new StringTokenizer(targetLocation,"/");
		    	targetDirectories = new String[tokens.countTokens()];	
		    	int dirCount = 0; 
		    	while(tokens.hasMoreTokens())
		    	{
		    		targetDirectories[dirCount]=tokens.nextToken();
		    		if(dirCount==0){
		    			//to indicate that initial directory /home/ should not be created if already exists
		    			//Bug # 23119 Start
						//Showing error page as "FC0403 : Image Save Failed, Image upload failed due to network failures, upload again" while trying to save an image in Content Manager. 
		    			targetDirectories[dirCount]=targetDirectories[dirCount];
		    			//Bug # 23119 End
		    		}
		    		FTPFile ftpDirDtls[] = fc.dirDetails(targetDirectories[dirCount]);
					if(ftpDirDtls.length ==0){
						fc.mkdir(targetDirectories[dirCount]);
						fc.chdir(targetDirectories[dirCount]);
					}else{
						fc.chdir(targetDirectories[dirCount]);
					}
		    		dirCount++;
		    	}
		    }
		    fc.put(is,fileName);
		}
		catch(UnsupportedEncodingException use)
		{
			logger.error("Error occurred at uploadFiles ",use);
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			logger.error("Error occurred at uploadFiles ",ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			logger.error("Error occurred at uploadFiles ",ftpe);
			String errMessage = ftpe.getMessage();
			ftpe.printStackTrace();
			if(errMessage == null)
			{
				errMessage = "";
			}
			if(ftpe.getReplyCode() == 550)
			{	
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,ftpe);
			}
			else
			{
				throw new FileSystemUtilException("EL0367",null,Level.ERROR,ftpe);
			}				
		}	
		catch(Exception ex)
		{	
			logger.error("Error occurred at uploadFiles "+ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		logger.debug("End: "+getClass().getName()+":uploadFiles()");	
		return true;
	}
	
	/**
	 * This method create the folder in the given path
	 * if folder already exists it will not create new folder, otherwise it will create a new folder.
	 *
	 * @param filePath the file path
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	@Override
	public boolean createFolder(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":createFolder()");	
		try	
		{
			FTPFile ftpSubs[] = null;
			String userHome = getUserHome();
			String newFilePath = userHome;
			//if given file path doesnot start with user home
			if(filePath == null && filePath.trim().length() < 1 && !filePath.startsWith(userHome)){
				logger.error("File path is not matched with userhome File path :: "+filePath+"  :: user home ::" +userHome +" ");
				throw new FileSystemUtilException("EL0701");
			}
			//Begin for ZET-2095
			//Fixed by Ramasubrahmanyam T
			//As per the implementation, system is expecting "/" is the home folder, if user did not provide the forwared slash
			//before file path, then we need to add the forward slash to existing file path
			if(filePath != null && !filePath.startsWith("/"))
				filePath = "/" + filePath;
			//End for ZET-2095
			
			String directories = filePath.substring(filePath.indexOf(userHome)+userHome.length(),filePath.length());
			StringTokenizer folders = new StringTokenizer(directories,"/");
			String folder = null;
			connect();
			while(folders.hasMoreTokens()){
				folder = folders.nextToken();
				if(folder != null ){
					//gettting all file and folder in the path
					if(!newFilePath.endsWith("/")){
						newFilePath +="/";
					}
					newFilePath +=folder;
					try{
						ftpSubs = fc.dirDetails(newFilePath);
					}catch(FTPException ftpe) 
					{ /*To avoid the exception from*/ 	}
					//if no files and folders are not there then create the folder
					if(ftpSubs==null || ftpSubs.length ==0){
						try{
							fc.mkdir(folder);
							fc.chdir(folder);
						}catch(FTPException ftpe) 
						{
							//Bug : 23976 Start
							//Description : Unable to process the Import activity from the remote server if the remote path have an empty done directory present.
							//if folder already exists with no files and folders then change the directory
							if(ftpe.getReplyCode() == 550 || ftpe.getReplyCode() == 521)
							{
								//Bug : 23976 End
								try{
									fc.chdir(folder);
								}catch(FTPException e) 
								{
									//if folder is wrong or permissions are not there throw an exception
									throw new FileSystemUtilException("EL0699",null,Level.ERROR,e);
								}
							}
							else
							{
								throw new FileSystemUtilException("EL0699",null,Level.ERROR,ftpe);
							}
						}
					}else
					{
						//if folder is there with some files or folders then change the folder
						fc.chdir(folder);
					}
					ftpSubs = null;
				}
			}
		}
		catch(IOException ioe)
		{
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		}catch(FTPException ftpe) 
		{
			logger.error(ftpe.getMessage(),ftpe);
			throw new FileSystemUtilException("EL0699",null,Level.ERROR,ftpe);
		}
		catch(FileSystemUtilException e){
			throw e;
		}
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		logger.debug("End: "+getClass().getName()+":createFolder()");	
		return true;
	}
	
	/**
	 * This method move the file in the remote computer 
	 * from old path to new path.
	 *
	 * @param oldPath the old path
	 * @param newPath the new path
	 * @return true, if successful
	 * @throws FileSystemUtilException the pre publisher exception
	 */
	public boolean moveFile(String oldPath, String newPath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":moveFile()");	
		connect();
		try
		{	
			fc.rename(oldPath, newPath);
		}		
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			logger.error(ftpe.getMessage(),ftpe);
			String errMessage = ftpe.getMessage();
			ftpe.printStackTrace();
			if(errMessage == null)
			{
				errMessage = "";
			}
		}
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":moveFile()");	
		return true;
	}
	
	/**
	 *  
	 * This method gets the list of files in a specified directory from the ftp server
	 * and returns filenames array.
	 * This method gets the list of directories in a specified directory from the ftp server
	 * and returns foldernames array.when this is called from getFolders() mehtod.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @return the string[]
	 * @throws FileSystemUtilException the invalid data exception
	 */	
	public String[] listFiles(boolean isFoldersRequired, boolean isSort) throws FileSystemUtilException
	{
		return listFiles(isFoldersRequired, isSort, false);
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
	
	public String[] listFiles(boolean isFoldersRequired, boolean isSort, boolean isFilesOnly) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		Vector list = null;		
		FTPFile[] ftpfiles = null;
		String remoteDir = null;
		String remoteFileName = null;
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		String[] filenames = null;
		List<String> filesList = new ArrayList<String>();
		if(!isSort){
			listFiles(isFoldersRequired);
		}
		try 
		{	
			connect();			
			int lastSeperatorIndex = -1;
			
			if( remoteFilePath.lastIndexOf("/") != -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("/");
			}else if( remoteFilePath.lastIndexOf("\\") != -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("\\");
			}

			if(lastSeperatorIndex == remoteFilePath.length()-1)
			{
				remoteDir = remoteFilePath;
				remoteFileName = "";
				logger.debug("file path ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				try
				{
					if(!"".equals(remoteDir))
					{
						fc.chdir(remoteDir);
					}
				}
				catch(FTPException e)
				{	
					String errMessage = e.getMessage();
					if(errMessage == null)
					{
						errMessage = "";
					}

					if(e.getReplyCode() == 550)
					{
						//invlid directory/file		
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("ET0017");
					}
					else
					{
						//unknown
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("EL0359");
					}	
				}
			}
			else
			{
				remoteDir = remoteFilePath.substring(0,lastSeperatorIndex+1);
				remoteFileName = remoteFilePath.substring(lastSeperatorIndex+1,remoteFilePath.length());
				logger.debug("file path not ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				try
				{
					if(!"".equals(remoteDir))
					{
						fc.chdir(remoteDir);
					}
				}
				catch(FTPException e)
				{
					String errMessage = e.getMessage();
					if(errMessage == null)
					{
						errMessage = "";
					}
					if(e.getReplyCode() == 550)
					{
						//invlid directory/file
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("ET0017");
					}
					else
					{
						//unknown
						logger.error(e.getMessage(),e);
						throw new FileSystemUtilException("EL0359");
					}					
				}				
				
				if(!remoteFileName.startsWith("*.") && !remoteFileName.endsWith(".*"))
				{
					logger.debug("file/directory name "+remoteFileName+" not started with *. or ends wirh .*");
					try
					{
						fc.chdir(remoteFileName);
						logger.debug(remoteFileName+" is a directory");
						remoteFileName = "";
					}
					catch(FTPException e)
					{
						String errMessage = e.getMessage();
						if(errMessage == null)
						{
							errMessage = "";
						}
						if(e.getReplyCode() != 550)
						{	
							//unknown
							logger.error(e.getMessage(),e);
							throw new FileSystemUtilException("EL0359");
						}
						else
						{
							boolean isFile = false;
							String[] tempFileNames = fc.dir();
							for(int j = 0; j < tempFileNames.length; j++)
							{
								if(remoteFileName.equals(tempFileNames[j]))
								{
									isFile = true;
								}
							}
							if(!isFile)
							{
								//invlid directory/file
								logger.error(e.getMessage(),e);
								throw new FileSystemUtilException("ET0017");
							}
							logger.debug(remoteFileName+" is a file");
						}
					}					
				}
			}
			logger.debug("Before getting list of files : current dir "+fc.pwd()+" getting files list for "+remoteFileName);
			try
			{
				ftpfiles = fc.dirDetails(remoteFileName);
			}
			catch(FTPException e)
			{
				logger.error(e.getMessage(),e);
				throw new FileSystemUtilException("ET0023");
			}
			catch(ParseException pe)
			{	
				logger.error(pe.getMessage(),pe);
				throw new FileSystemUtilException("ET0023");
			}		
			//below vector used to store fileNames and Dir names
			list = new Vector();
			for(int i = 0; i < ftpfiles.length; i++)
			{				
				FTPFile tempFile = ftpfiles[i];
				String tempFileName = null;	
				if(tempFile.isDir())
				{
					if(isFilesOnly){
						continue;
					}
					tempFileName = tempFile.getName()+"/";				
					
					Vector<String> file = new Vector<String>();
					long val = tempFile.lastModified().getTime();							
					if(!map.containsKey(val+"")){
						if(isValidString(tempFileName)){
							file.add(tempFileName);
							map.put(val+"", file);
						}
					}else{
						file = (Vector<String>)map.get(val+"");
						if(isValidString(tempFileName)){
							file.add(tempFileName+"/");
							map.put(val+"", file);
						}
					}
					logger.debug("directory name "+tempFileName);
				}
				else
				{
					if(!isFoldersRequired)
					{
						tempFileName = tempFile.getName();
						
						Vector<String> file = new Vector<String>();
						long val = tempFile.lastModified().getTime();							
						if(!map.containsKey(val+"")){
							if(isValidString(tempFileName)){
								file.add(tempFileName);
								map.put(val+"", file);
							}
						}else{
							file = (Vector)map.get(val+"");
							if(isValidString(tempFileName)){
								file.add(tempFileName);
								map.put(val+"", file);
							}
						}
						
						logger.debug("file name "+tempFileName);
					}
				}				
				tempFileName = null;
				tempFile = null;
			}
			
			Set keyset = map.keySet();
			Iterator keysIterator = keyset.iterator();
			
			long[] times = new long[map.keySet().size()];
			int timesCount =0;
			while(keysIterator.hasNext()){
				times[timesCount]= Long.parseLong(keysIterator.next().toString());
				timesCount = timesCount+1;
			}
			Arrays.sort(times);
			Vector values = new Vector();
			for(int i=0;i<times.length;i++){
					values = (Vector)map.get(times[i]+"");
					for(int w=0;w<values.size();w++){
						if(isValidString(values.get(w).toString())){
							filesList.add(values.get(w).toString());
						}
					}
			}
			filenames = new String[filesList.size()];
			for(int i=0;i<filesList.size();i++){
				filenames[i] = filesList.get(i).toString();
			}
			logger.debug("Got files list of size "+filenames.length);
		} 
		catch(IOException ioe) 
		{			
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022");			
		} 
		catch(FileSystemUtilException ebizEx)
		{
			
			throw ebizEx;
		}
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("EL0359");			
		}
		finally
		{
			if(list != null)
			{
				list.clear();
				list = null;
			}
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
			
		}
		logger.debug("End: "+getClass().getName()+":listFiles()");
		return filenames;		
	
	}
	
	
	/**
	 * 
	 * Method Name 	: removeFile
	 * Description 		: The Method "removeFile" is used for 
	 * Date    			: May 28, 2016, 6:19:34 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean removeFile(String fileName) throws FileSystemUtilException 
	{
		String extractFile=null;
		logger.debug("Begin: "+getClass().getName()+":deleteFile()");	
		connect();
		try 
		{		
				fc.delete(remoteFilePath);
				disconnect();
				if(stop)
				{
				   	throw new FileSystemUtilException("999");
				}
				FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
				if(zu.isZip(localFilePath))
				{
					extractFile=zu.unZip(localFilePath,true);
					localFilePath=extractFile;
				}
				zu=null;
		}
		catch(FileSystemUtilException ebize)
		{
			throw ebize;
		}
		catch(UnsupportedEncodingException use)
		{
			logger.error(use.getMessage(),use);
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			String errMessage = ftpe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
			if(ftpe.getReplyCode() == 550)
			{	
				logger.error(ftpe.getMessage(),ftpe);
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,ftpe);
			}
			else
			{
				logger.error(ftpe.getMessage(),ftpe);
				throw new FileSystemUtilException("EL0701",null,Level.ERROR,ftpe);
			}				
		}	
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":deleteFile()");	
		return true;
	}

	

	

	/**
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 22, 2016, 2:07:43 PM
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
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 22, 2016, 2:07:46 PM
	 * @param input
	 * @param targetLocation
	 * @param fileName
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void putFile(String input, String targetLocation) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);	
	}

	/**
	 * 
	 * Method Name 	: getFile
	 * Description 		: The Method "getFile" is used for 
	 * Date    			: May 22, 2016, 2:07:49 PM
	 * @param filepath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public InputStream getFile(String filepath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":moveFile()");	
		connect();
		ByteArrayInputStream in = null;
		try
		{	
			byte[] content= fc.get(filepath);
			
			in = new ByteArrayInputStream(content);
		}		
		catch(IOException ioe) 
		{	
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} 
		catch(FTPException ftpe) 
		{
			
			String errMessage = ftpe.getMessage();
			if(errMessage == null)
			{
				errMessage = "";
			}
		}
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}
		finally
		{
			try
			{
				disconnect();
			}
			catch(FileSystemUtilException ebe)
			{
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: "+getClass().getName()+":moveFile()");	
		return in;
		
	}


	

	/**
	 * 
	 * Method Name 	: truncateFile
	 * Description 		: The Method "truncateFile" is used for 
	 * Date    			: May 22, 2016, 2:07:51 PM
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
	 * Method Name 	: renameFile
	 * Description 		: The Method "renameFile" is used for 
	 * Date    			: May 28, 2016, 6:18:38 PM
	 * @param oldFileName
	 * @param newFileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean renameFile(String oldFileName, String newFileName) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":renameFile()");
		try {
			connect();
			fc.rename(oldFileName,newFileName);

		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022", null, Level.ERROR, ioe);
		} catch (FTPException ftpe) {
			String errMessage = ftpe.getMessage();
			logger.error(ftpe.getMessage(),ftpe);
			if (errMessage == null) {
				errMessage = "";
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002", null, Level.ERROR, ex);
		} finally {
			try {
				disconnect();
			} catch (FileSystemUtilException ebe) {
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: " + getClass().getName() + ":renameFile()");
		return true;
	}

	

	/**
	 * 
	 * Method Name : removeFolder Description : The Method "removeFolder" is
	 * used for Date : May 22, 2016, 2:08:03 PM
	 * 
	 * @param path
	 * @return
	 * @throws FileSystemUtilException
	 * @param :
	 * @return :
	 * @throws :
	 */

	@Override
	public boolean removeFolder(String filepath) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":removeFolder()");
		try {
			connect();
			fc.rmdir(filepath);

		} catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			throw new FileSystemUtilException("ET0022", null, Level.ERROR, ioe);
		} catch (FTPException ftpe) {
			
			String errMessage = ftpe.getMessage();
			logger.error(ftpe.getMessage(),ftpe);
			if (errMessage == null) {
				errMessage = "";
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002", null, Level.ERROR, ex);
		} finally {
			try {
				disconnect();
			} catch (FileSystemUtilException ebe) {
				logger.error("Exception::",ebe);
			}
		}
		logger.debug("End: " + getClass().getName() + ":removeFolder()");
		return true;
	}

	/**
	 * 
	 * Method Name 	: copyFolder
	 * Description 		: The Method "copyFolder" is used for 
	 * Date    			: May 22, 2016, 2:08:05 PM
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
	 * Date    			: May 22, 2016, 2:08:15 PM
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
		return renameFile(oldDirName, newDirName);
	}

	/**
	 * Method Name 	: convertUnixFileToDOS
	 * Description 		: The Method "convertUnixFileToDOS" is used for 
	 * Date    			: May 20, 2016, 2:57:01 PM.
	 *
	 * @param filePath the file path
	 * @return 		:
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
	 * Method Name 	: convertDOSFileToUnix
	 * Description 		: The Method "convertDOSFileToUnix" is used for 
	 * Date    			: May 20, 2016, 2:56:53 PM.
	 *
	 * @param filePath the file path
	 * @return 		:
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
	 * 
	 * Method Name 	: convertFileToUTFFormat
	 * Description 		: The Method "convertFileToUTFFormat" is used for 
	 * Date    			: May 22, 2016, 2:08:25 PM
	 * @param fileName
	 * @param encodeFormat
	 * @param newFileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
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
	 * 
	 * Method Name 	: isZip
	 * Description 		: The Method "isZip" is used for 
	 * Date    			: May 22, 2016, 2:08:29 PM
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
	 * Date    			: May 22, 2016, 2:08:36 PM
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
	 * Method Name 	: listFolders
	 * Description 		: The Method "listFolders" is used for 
	 * Date    			: May 22, 2016, 2:08:39 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String[] listFolders() throws FileSystemUtilException {
		return listFiles(true);	
	}

	/**
	 * 
	 * Method Name 	: unZip
	 * Description 		: The Method "unZip" is used for 
	 * Date    			: May 22, 2016, 2:08:42 PM
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
	 * Date    			: May 22, 2016, 2:08:45 PM
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
	
	/**
	 * Gets the download status.
	 *
	 * @return the download status
	 */
	public boolean getDownloadStatus()
	{
		return true;
	}	
	
	
	/**
	 * Gets the connection id.
	 *
	 * @return the connection id
	 */
	public int getConnectionId()
	{
		return -1;
	}
	

}
