
package com.fileutils.plugin.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.bo.ZtByteArrayOutputStream;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.Constants;
import com.fileutils.util.ConvertFiles;
import com.fileutils.util.FileSystemTypeConstants;
import com.sshtools.net.SocketTransport;
import com.sshtools.sftp.SftpClient;
import com.sshtools.sftp.SftpFile;
import com.sshtools.sftp.SftpStatusException;
import com.sshtools.ssh.ChannelOpenException;
import com.sshtools.ssh.PasswordAuthentication;
import com.sshtools.ssh.SshAuthentication;
import com.sshtools.ssh.SshClient;
import com.sshtools.ssh.SshConnector;
import com.sshtools.ssh.SshException;
import com.sshtools.ssh2.Ssh2Client;
import com.sshtools.ssh2.Ssh2Context;

import ch.qos.logback.classic.Logger;

public class SFTPUtil implements FileSystemUtilsPlugin{
	
	/** The logger. */
	static Logger logger = (Logger) LoggerFactory.getLogger(ConvertFiles.class);
	
	/** The sample data. */
	private String sampleData;
	
	/** The host. */
	private String host 	= null;	  	
	
	/** The username. */
	private String username	= null;	
	
	/** The password. */
	private String password	= null;
	
	/** The remotefile path. */
	private String remotefilePath = null;	
	
	/** The local file path. */
	private String localFilePath = null;		
	
	
	
	/** The read time out. */
	private int readTimeOut = 0;
	
	/** The port. */
	private int port;
	
	/** The ssh. */
	private SshClient ssh 	= null;
	
	/** The sftp. */
	private SftpClient sftp = null;
	
	private SshClient sshC = null;
	
		
	/** The bundle. */
	//private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE);

	/** The mutex. */
	private String mutex = "";
	
		
	/**
	 * Instantiates a new SFTP util.
	 *
	 * @param configBean the config bean
	 * @throws FileSystemUtilException the file system util exception
	 */
	public SFTPUtil()  {
		//logger.setResourceBundle(bundle);
	}
	

	/**
	 * 
	 * Method Name 	: setConfig
	 * Description 		: The Method "setConfig" is used for 
	 * Date    			: May 20, 2016, 2:54:20 PM
	 * @param context
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":connect()");
		host = context.getHostname();
		username = context.getUsername();
		password = context.getPassword();
		port = context.getPort();
		if (port == 0) {
			port = Constants.SFTP_PORT;
		}
		remotefilePath = context.getRemoteFilePath();
		localFilePath = context.getLocalFilePath();
		readTimeOut = context.getReadTimeOut();
		logger.debug("End: " + getClass().getName() + ":connect()");

	}

	/**
	 * 
	 * Method Name 	: connect
	 * Description 		: The Method "connect" is used for 
	 * Date    			: May 20, 2016, 2:54:23 PM
	 * @param isUIModule
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void connect() throws FileSystemUtilException {
		//SshConnectionProperties properties = null;
		// based on this varible value check the password athentication
		int result;
		try {
			/*properties = new SshConnectionProperties();
			ssh = new SshClient();
			logger.debug("Connecting to " + host + " on port " + port);
			properties.setHost(host);
			properties.setPort(port);
			ssh.setSocketTimeout(readTimeOut);
			ssh.connect(properties, new IgnoreHostKeyVerification());*/
			
			//ADDED JAN-19-2017.. Sreekhar.K
			SshConnector con = SshConnector.createInstance();
	        con.getContext().setPreferredPublicKey(Ssh2Context.PUBLIC_KEY_SSHDSS);
	        SocketTransport t = new SocketTransport(host, port);
	        t.setTcpNoDelay(true);
	        sshC = con.connect(t, username, true);
	        ssh = (Ssh2Client) sshC;
		} catch (SshException ioe)
		{
			logger.error(new Date()+":: Unable to Connect with parameters " +
					", username= "+username+" , remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+localFilePath +" , host= "+host+" and port ="+port);
		   
		    throw new FileSystemUtilException("ET0019",null,Level.ERROR,ioe);		
		   
		} catch (SocketException e) {
			logger.error(new Date()+":: Unable to Connect with parameters " +
					", username= "+username+" , remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+localFilePath +" , host= "+host+" and port ="+port);
		   
		     throw new FileSystemUtilException("ET0019",null,Level.ERROR,e);		
		   
		}catch (IOException ioe) {
			logger.error(ioe.getMessage(),ioe);
			logger.info(new Date() + ":: Unable to Connect with parameters " + ", username= " + username
					+ " , remotefilePath = " + remotefilePath + ", " + "localFilePath = "
					+ localFilePath + " , host= " + host + " and port =" + port);
			throw new FileSystemUtilException("ET0019", null, Level.ERROR, ioe);
		}
		try {
			/*PasswordAuthenticationClient pwd = new PasswordAuthenticationClient();
			pwd.setUsername(username);
			pwd.setPassword(password);
			result = ssh.authenticate(pwd);
			if ((result == AuthenticationProtocolState.FAILED) || (result == AuthenticationProtocolState.PARTIAL)
					|| (result == AuthenticationProtocolState.CANCELLED)) {
				logger.info(new Date() + ":: Unable to Connect with parameters " + ", username= " + username
						+ " , remotefilePath = " + remotefilePath + ", "
						+ "localFilePath = " + localFilePath + " , host= " + host + " and port =" + port);
				throw new FileSystemUtilException("ET0021", null, Level.ERROR, null);
			}*/
			
			//ADDED JAN-19-2017.. Sreekhar.K
			PasswordAuthentication pwd = new PasswordAuthentication();
	        pwd.setPassword(password);
	        result =  ssh.authenticate(pwd);
	        if ((result == SshAuthentication.FAILED) || (result == SshAuthentication.FURTHER_AUTHENTICATION_REQUIRED) || (result == SshAuthentication.CANCELLED))
			{
				logger.info(new Date()+":: Unable to Connect with parameters " +
						", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
						"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
				throw new FileSystemUtilException("ET0021",null,Level.ERROR,null);
			}
		} catch (SshException ioe) {
			logger.info(new Date() + ":: Unable to Connect with parameters " + ", username= " + username
					+ " , remotefilePath = " + remotefilePath + ", " + "localFilePath = "
					+ localFilePath + " , host= " + host + " and port =" + port);
			throw new FileSystemUtilException("ET0021", null, Level.ERROR, ioe);
		}

	}

	/**
	 * 
	 * Method Name 	: disconnect
	 * Description 		: The Method "disconnect" is used for 
	 * Date    			: May 20, 2016, 2:54:28 PM
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void disconnect() throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":disconnect()");
		try {
			if (sftp != null) {
				sftp.quit();
			}
		}
		// no need to error out if not disconnected just placing warning
		catch (SshException ioe) {
			String errMessage = ioe.getMessage();
			logger.info(new Date() + ":: Unable to disconnect with parameters " + ", username= " + username
					+ " , remotefilePath = " + remotefilePath + ", " + "localFilePath = "
					+ localFilePath + " , host= " + host + " and port =" + port + " , errormessage=" + errMessage);

			if (errMessage == null) {
				errMessage = "";
			}
			logger.error(errMessage,ioe);
		} finally {
			try {
				if (ssh != null) {
					ssh.disconnect();
					ssh = null;
				}
				if (sftp != null) {
					sftp = null;
				}
			} catch (Exception e) {
				String errMessage = e.getMessage();
				logger.error(new Date() + ":: Unable to disconnect with parameters " + ", username= " + username
						+ " , remotefilePath = " + remotefilePath + ", "
						+ "localFilePath = " + localFilePath + " , host= " + host + " and port =" + port
						+ " , errormessage=" + errMessage);

				if (errMessage == null) {
					errMessage = "";
				}
				logger.error(errMessage,e);
			}
		}

	}

	/**
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 20, 2016, 2:54:32 PM
	 * @param is
	 * @param targetLocation
	 * @param fileName
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void putFile(InputStream is, String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":renameFile()");
		try
		{
			connect();				
			//sftp =  ssh.openSftpClient();
			sftp=new SftpClient(ssh);//ADDED JAN-19-2017.. Sreekhar.K
			sftp.put(is,filePath);
		}catch (Exception e){
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}		
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":renameFiles()");
		
	}

	/**
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 20, 2016, 2:54:34 PM
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
	 * Date    			: May 20, 2016, 2:54:57 PM
	 * @param filepath
	 * @param fileName
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
	 * Date    			: May 20, 2016, 2:55:06 PM
	 * @param filePath
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
	 * Method Name 	: renameFile
	 * Description 		: The Method "renameFile" is used for 
	 * Date    			: May 20, 2016, 2:55:16 PM.
	 *
	 * @param fileName the file name
	 * @param newFileName the new file name
	 * @return 		:
	 * @throws FileSystemUtilException the file system util exception
	 */
	@Override
	public boolean renameFile(String oldFileName, String newFileName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":renameFile()");
		try
		{
			connect();				
//			sftp =  ssh.openSftpClient();
			sftp = new SftpClient(ssh); 						
			sftp.rename(oldFileName,newFileName);
		}catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}		
				
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":renameFiles()");
		return true;
	}

	
	
	/**
	 * 
	 * Method Name 	: createFolder
	 * Description 		: The Method "createFolder" is used for 
	 * Date    			: May 20, 2016, 2:55:20 PM
	 * @param filePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean createFolder(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":createFolder()");
		try
		{
			String userHome = getUserHome();
			//if given file path doesnot start with user home
			if(filePath == null || filePath.trim().length() < 1 || !filePath.startsWith(userHome)){
				logger.error("File path is not matched with userhome File path :: "+filePath+"  :: user home ::" +userHome +" ");
				throw new FileSystemUtilException("EL0701");
			}
			connect();				
			//sftp =  ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
			sftp.mkdirs(filePath);
		}catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}		
		/*catch (IOException e) 
		{				
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}*/		
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":createFolder()");
		return true;
		
		
	}

	

	/**
	 * 
	 * Method Name 	: removeFolder
	 * Description 		: The Method "removeFolder" is used for 
	 * Date    			: May 20, 2016, 2:55:24 PM
	 * @param path
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
    @Override
	public boolean removeFolder(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":createFolder()");
		try
		{
			connect();				
			//sftp =  ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
			logger.debug("File path to remove:"+filePath);
			sftp.rm(filePath);
		}	catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}/*	
		catch (IOException e) 
		{				
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}*/		
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":removeFolder()");
		return true;
	}

	/**
	 * 
	 * Method Name 	: copyFolder
	 * Description 		: The Method "copyFolder" is used for 
	 * Date    			: May 20, 2016, 2:55:30 PM
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
	 * Date    			: May 20, 2016, 2:55:40 PM
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
			logger.error("Exception :: "+e.getMessage(),e);
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
			logger.error("Exception ::"+e.getMessage(),e);
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
			logger.error("Exception::"+e.getMessage(),e);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,e);	
		}
	}


	/**
	 * 
	 * Method Name 	: downloadFile
	 * Description 		: The Method "downloadFile" is used for 
	 * Date    			: May 20, 2016, 2:53:14 PM
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
		logger.debug(new Date()+":: parameters " +
				", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
				"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
		List ls = null;
		long fileSize = -1;
		int downloadRetryCount = 3;
		boolean downloadCompleted = false;
		try
		{
			//Bug Fix Start #14446 ZET #1990 - List is errored with "TransportProtocolException", when we use the same SFTP server for File source and Merge filter source.
			synchronized (this.mutex) {
				connect();			
				//sftp =  ssh.openSftpClient();
				sftp = new SftpClient(ssh);//ADDED JAN-19-2017.. Sreekhar.K
				//ZET#1950 Start - Campaigns got errored out with the error description as EL1185 : Publishing Campaign failed. One or more of the scheduled List(s) got errored or stopped."
				//To get the file size from the SFTP location and compare with the file size after downloding the file to the location
				int lastindex = -1;
				// One View #2680 fix starts here 
				//Import task is getting error out with the IO error exception. (Support #2680)
				String dirPath = null;
				String fileName = null;
				if( remotefilePath.lastIndexOf("/") == 0){
					dirPath= remotefilePath.substring(0, 1);
					lastindex=remotefilePath.lastIndexOf("/");
					fileName = remotefilePath.substring(lastindex+1, remotefilePath.length());
				}
				// OneView #2680 fix ends here.
				if( remotefilePath.lastIndexOf("/") != -1){
					lastindex = remotefilePath.lastIndexOf("/");
				}else if( remotefilePath.lastIndexOf("\\") != -1){
					lastindex = remotefilePath.lastIndexOf("\\");
				}
				
				if(lastindex>0){
					dirPath= remotefilePath.substring(0, lastindex);
					fileName = remotefilePath.substring(lastindex+1, remotefilePath.length());
				}
				logger.debug("dirPath ------------>:"+dirPath+" : fileName ----------->:"+fileName);
				if(dirPath!=null && fileName!=null  && dirPath.trim().length()>0 && fileName.trim().length()>0 )
				{
					//ls= sftp.ls(dirPath);
					ls=Arrays.asList(sftp.ls(dirPath));  //ADDED JAN-19-2017.. Sreekhar.K
					//This loop is used to list out the files from the specifid path
					for(int i=0;i < ls.size();i++)
					{
						SftpFile sftpFile = (SftpFile)ls.get(i);
						if(!sftpFile.isDirectory())
						if(fileName.equalsIgnoreCase(sftpFile.getFilename()))
						{
							String fsize = sftpFile.getAttributes().getSize().toString();
							fileSize =Long.parseLong(fsize);
							logger.debug("fileSize ----------------------->"+fileSize);
							break;
						}
					}
				}
				//ZET#1950 End - Campaigns got errored out with the error description as EL1185 : Publishing Campaign failed. One or more of the scheduled List(s) got errored or stopped."
				for(int i=1; i<=downloadRetryCount;i++){
					sftp.get(remotefilePath,localFilePath );
					/*
					 * Bug Fix Start:11293::  UnZip is not happened when we use sftp protocol
					 */
					//ZipFileUtil zu = new ZipFileUtil();
					FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
					File sourceFile = new File(localFilePath);
					if(zu.isZip(localFilePath))
					{
						logger.debug("SFTPUtipl:download:Unzipping the downloaded file");
						extractFile=zu.unZip(localFilePath,true);
						localFilePath=extractFile;
					}
					zu=null;
					if(fileSize == sourceFile.length()){
						downloadCompleted = true;
						break;
					}
				}
				logger.debug("downloadCompleted status ---------->"+downloadCompleted);
				if(!downloadCompleted || fileSize==-1 )
					throw new FileSystemUtilException("ET0617");
				/*
				 * Bug Fix End:11293::  UnZip is not happened when we use sftp protocol
				 */
			}
			//Bug Fix End #14446 ZET #1990 - List is errored with "TransportProtocolException", when we use the same SFTP server for File source and Merge filter source.
		}
		catch(IOException ie)
		{
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
			logger.error(ie.getMessage(),ie);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ie);
		}catch(Exception e)
		{
			logger.info(new Date()+":: Unable to download with parameters " +
					", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}
		finally
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				logger.info(new Date()+":: Unable to disconnect with parameters " +
						", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
						"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
				
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
		}
		logger.debug("End: "+getClass().getName()+":download()");
		return true;
		
		
	}

	/**
	 * 
	 * Method Name 	: isZip
	 * Description 		: The Method "isZip" is used for 
	 * Date    			: May 20, 2016, 2:53:05 PM
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
	 * Date    			: May 20, 2016, 2:53:02 PM
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
	 * Date    			: May 20, 2016, 2:52:56 PM
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":upload()");
		try
		{
			connect();				
		/*	sftp =  ssh.openSftpClient();						
			sftp.cd(remotefilePath);			
			sftp.put(localFilePath+fileName);	*/
			
			//ADDED JAN-19-2017.. Sreekhar.K
			sftp = new SftpClient(ssh);
	        sftp.cd(remotefilePath);			
			sftp.put(localFilePath+fileName);
		}		
		catch (IOException e) 
		{	
			logger.info(new Date()+":: Unable to upload with parameters " +
					", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}catch (Exception e) 
		{	
			logger.info(new Date()+":: Unable to upload with parameters " +
					", username= "+ username +" ,  remotefilePath = "+remotefilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ host +" and port ="+ port);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}			
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":upload()");
		return true;
		
	}
	
	/**
	 * 
	 * Method Name 	: getSampleData
	 * Description 		: The Method "getSampleData" is used for 
	 * Date    			: May 20, 2016, 2:52:51 PM
	 * @param dataLength
	 * @param charset
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException 
	{
		logger.debug("Begin: "+getClass().getName()+":getSampleData(DataLength,charset)");	
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		ZtByteArrayOutputStream bos = null;
		try
		{  			
			connect();		
			//sftp = ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
			bos = new ZtByteArrayOutputStream(1024*10,sftp);
	        sftp.get(remotefilePath,bos);
	        sampleData=bos.getSampleData(charset,zu.isZip(remotefilePath));	
		}
		catch(UnsupportedEncodingException use)
		{
			logger.error(use.getMessage(),use);
			throw new FileSystemUtilException("ET0111");
		}	
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			if(ex instanceof SshException){
				SshException exception = (SshException)ex;
				if(exception.getReason() == 6){
					try {
						return bos.getSampleData(charset,zu.isZip(remotefilePath));
					} catch (UnsupportedEncodingException e) {
						logger.error("SFTP FileUtil exception due to filesize is > expected file data");
					}
				}
			}else{
				throw new FileSystemUtilException("F00002");
			}
		}			
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":getSampleData(DataLength,charset)");	
		return sampleData;		
	}
	
	/**
	 * This method is used to get list of directories in the given path.
	 *
	 * @return the folders
	 * @throws PrePublisherException the pre publisher exception
	 */
	public String[] listFolders() throws FileSystemUtilException
	{
		return listFiles(true);			
	}
	
	/**
	 * this method return the list of files in the specified path.
	 *
	 * @return the string[]
	 * @throws PrePublisherException the pre publisher exception
	 */
	public String[] listFiles() throws FileSystemUtilException
	{
		return listFiles(false);
	}
	
	/**
	 * 	 
	 * This method is used to get list of directories in the given path   
	 * check list using the ls method
	 * check output of ls using with isDirectory
	 * if isFile ,store and return.
	 *
	 * @param isFoldersRequired the is folders required
	 * @return the string[]
	 * @throws PrePublisherException the pre publisher exception
	 */
	private String[] listFiles(boolean isFoldersRequired) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		String  fileslist[]= null;		
		Vector<String> list = null;
		List ls = null;
		try 
		{			
				connect();  
				//sftp = ssh.openSftpClient();
				sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
				sftp.cd(remotefilePath);	
//				ls= sftp.ls();
				ls= Arrays.asList(sftp.ls());
				list = new Vector<String>();
				//This loop is used to list out the files from the specifid path
				for(int i=0;i < ls.size();i++)
				{
					SftpFile sftpFile = (SftpFile)ls.get(i);
					if(sftpFile.isDirectory())
					{	
						//ignore if the hidden dir and files
						if(!sftpFile.getFilename().startsWith("."))
							list.add(sftpFile.getFilename()+"/");												
					}
					else
					{
						if(!isFoldersRequired)
						{
							//ignore if the hidden dir and files
							if(!sftpFile.getFilename().startsWith("."))
								list.add(sftpFile.getFilename());
						}						
					}
				}
				fileslist = new String[list.size()];
				//This loop is used to get the list of files from vector and add to String array
				for(int i = 0;i < fileslist.length ; i++)
				{						
					fileslist[i] = list.get(i).toString();
				}				
		}		
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("EL0802");
		}
		finally
		{	
			try
			{
			disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			if(list != null)
			{
				list.clear();
				list = null;
			}
			}
		logger.debug("End: "+getClass().getName()+":listFiles()");
		return fileslist;
	}
	
	/**
	 * This method move the file in the remote computer 
	 * from old path to new path.
	 *
	 * @param oldPath the old path
	 * @param newPath the new path
	 * @return true, if successful
	 * @throws PrePublisherException the pre publisher exception
	 */
	@Override
	public boolean moveFile(String oldPath, String newPath) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":moveFile()");
		try
		{
			connect();
			//sftp =  ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
			//To fix the issue Import relational task with sftp is getting errored timestamp is appended to the new file name 
			java.util.Date date=new java.util.Date();
        	DateFormat dateFormatter =new SimpleDateFormat("yyyyMMddHHmmss");
        	String timeStamp  = dateFormatter.format(date);
			String actualPath = newPath+timeStamp;
			//BUG fix end
			sftp.rename(oldPath, actualPath);
		}catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} //ADDED JAN-19-2017.. Sreekhar.K	
		/*catch (IOException e) 
		{				
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}*/		
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("End: "+getClass().getName()+":moveFile()");
		return true;
	}
	
	/**
	 * 	 
	 * This method is used to get list of files and directories in created date order   
	 * check list using the ls method
	 * check output of ls using with isDirectory
	 * if isFile ,store and return.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @return the string[]
	 * @throws PrePublisherException the pre publisher exception
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
	 * @throws FileSystemUtilException the file system util exception
	 */
	public String[] listFiles(boolean isFoldersRequired, boolean isSort, boolean isFilesOnly) throws FileSystemUtilException
	{

		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		List ls = null;
		HashMap<String, Vector<String>> map = new HashMap<String, Vector<String>>();
		String[] filenames = null;
		List<String> filesList = new ArrayList<String>();
		if(!isSort){
			listFiles(isFoldersRequired);
		}
		try 
		{			
				connect(); 
				//sftp = ssh.openSftpClient();
				sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
				sftp.cd(remotefilePath);			
				ls= Arrays.asList(sftp.ls());
				//This loop is used to list out the files from the specifid path
				for(int i=0;i < ls.size();i++)
				{
					SftpFile sftpFile = (SftpFile)ls.get(i);
					if(sftpFile.isDirectory())
					{	
						if(isFilesOnly){
							continue;
						}
						//ignore if the hidden dir and files
						if(!sftpFile.getFilename().startsWith(".")){
							Vector<String> file = new Vector<String>();
							String fileName = sftpFile.getFilename();
							long val = Long.parseLong(sftpFile.getAttributes().getModifiedTime().toString());							
							if(!map.containsKey(val+"")){
								file.add(fileName+"/");
								map.put(val+"", file);
							}else{
								file = (Vector)map.get(val+"");
								file.add(fileName+"/");
								map.put(val+"", file);
							}
						}
					}
					else
					{
						if(!isFoldersRequired)
						{
							if(!sftpFile.getFilename().startsWith(".")){
								Vector<String> file = new Vector();
								String fileName = sftpFile.getFilename();
								long val = Long.parseLong(sftpFile.getAttributes().getModifiedTime().toString());							
								if(!map.containsKey(val+"")){
									file.add(fileName);
									map.put(val+"", file);
								}else{
									file = (Vector)map.get(val+"");
									file.add(fileName);
									map.put(val+"", file);
								}
							}
						}
					}
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
							filesList.add(values.get(w).toString());
						}
				}
				filenames = new String[filesList.size()];
				for(int i=0;i<filesList.size();i++){
					filenames[i] = filesList.get(i).toString();
				}
		}catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("EL0802");
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("EL0802");
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("EL0802");
		}//ADDED JAN-19-2017.. Sreekhar.K
		
		catch(Exception e)
		{
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("EL0802");
		}
		finally
		{	
			try
			{
			disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			if(filesList != null)
			{
				filesList.clear();
				filesList = null;
			}
			}
		logger.debug("End: "+getClass().getName()+":listFiles()");
		return filenames;
	}
	
	/**
	 * 
	 * Method Name 	: removeFile
	 * Description 		: The Method "removeFile" is used for 
	 * Date    			: May 20, 2016, 2:52:30 PM
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
		logger.debug("Begin: "+getClass().getName()+":deleteFile()");
		try
		{
			connect();				
			//sftp =  ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
			sftp.rm(remotefilePath);			
		}	catch (SftpStatusException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (SshException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		} catch (ChannelOpenException e) {
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}	//ADDED JAN-19-2017.. Sreekhar.K	
		finally 
		{
			try 
			{
				disconnect();
			}
			catch (Exception e) 
			{
				String errMessage = e.getMessage();
				if(errMessage == null)
				{
					errMessage = "";
				}
				logger.warn(errMessage,e);
			}
			
		}		
		logger.debug("Begin: "+getClass().getName()+":deleteFile()");
		return true;
	}

	/**
	 *  
	 *
	 * @return the user home
	 * @throws PrePublisherException the pre publisher exception
	 * @see com.zt.ebiz.util.ImportExportUtil#getUserHome()
	 * This method return the user home directory
	 */
	@Override
	public String getUserHome() throws FileSystemUtilException 
	{
		logger.debug("Begin: "+getClass().getName()+":getUserHome()");
		String userHome = "";
		connect();  
		try
		{
			//sftp = ssh.openSftpClient();
			sftp = new SftpClient(ssh);//ADDED JAN-19-2017.. Sreekhar.K
			userHome = sftp.pwd();			
		}
		catch (Exception e) 
		{		
			logger.error(e.getMessage(),e);
			throw new FileSystemUtilException("ET0022");
		}
		logger.debug("End: "+getClass().getName()+":getUserHome()");		
		return userHome;	
	}

		
	/**
	 * This method is used to upload the content to the remote location and create the directory structure if not exists.
	 *
	 * @param is the is
	 * @param targetLocation the target location
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws PrePublisherException the pre publisher exception
	 */
	@Override
	public boolean uploadFiles(InputStream is,String targetLocation,String fileName) throws FileSystemUtilException{
		logger.debug("Begin: "+getClass().getName()+":uploadFiles()");	
		try	
		{	connect();
			//sftp = ssh.openSftpClient();
			sftp = new SftpClient(ssh); //ADDED JAN-19-2017.. Sreekhar.K
		    if(targetLocation!=null && targetLocation.trim().length() > 0)
		    {
		    	if(targetLocation.startsWith("/"))
				{
					targetLocation=targetLocation.substring(1,targetLocation.length());
				}
		    	sftp.mkdirs(targetLocation);
		    	sftp.cd(targetLocation);
		    }
		   sftp.put(is,fileName);		  
		}
		/*catch(UnsupportedEncodingException use)
		{
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch(IOException ioe) 
		{	
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ioe);			
		} */
		catch(Exception ex)
		{	
			logger.error(ex.getMessage(),ex);
			throw new FileSystemUtilException("F00002",null,Level.ERROR,ex);				
		}finally{
			disconnect();
		}
		logger.debug("End: "+getClass().getName()+":uploadFiles()");	
		return true;
	}

	/**
	 * 
	 * Method Name 	: unZip
	 * Description 		: The Method "unZip" is used for 
	 * Date    			: May 20, 2016, 2:52:14 PM
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
	 * Date    			: May 20, 2016, 2:52:18 PM
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
	


}
