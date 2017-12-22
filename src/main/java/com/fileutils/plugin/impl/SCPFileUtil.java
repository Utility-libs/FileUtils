package com.fileutils.plugin.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.util.ConvertFiles;
import com.fileutils.util.FileSystemTypeConstants;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import ch.qos.logback.classic.Logger;

public class SCPFileUtil implements FileSystemUtilsPlugin {

	/** The logger. */
	static Logger logger = (Logger) LoggerFactory.getLogger(ConvertFiles.class);
	
	/** The hostname. */
	private String hostname;
	
	/** The username. */
	private String username;
	
	/** The password. */
	private String password;
	
	/** The port. */
	private int port;

	/** The stop. */
	private boolean stop = false;

	/** The remote file path. */
	private String remoteFilePath;

	/** The local file path. */
	private String localFilePath;

	/** The inputstream. */
	private InputStream inputstream;
	
	/** The outputstream. */
	private OutputStream outputstream;
	
	/** The session. */
	private Session session;
	
	/** The channel. */
	private Channel channel;
	
	/** The sample data. */
	private String sampleData;
	
	/** The is sample. */
	private boolean isSample=false;
	/* BUG-FIX thread hang due to network outage */
	/** The read time out. */
	//initializing with 0 means no timeout(wait forever)
	private int readTimeOut = 0;
	
	/** The is imguplaod. */
	private char isImguplaod ='N';
	/** The locale. */
	private String locale = null;
	
	/** The output. */
	private String output;

	/** The bundle. */
	//private java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle(Constants.RESOURCE_BUNDLE);
	

	/**
	 * Instantiates a new SCP file util.
	 *
	 * @param connBean the conn bean
	 * @throws FileSystemUtilException the file system util exception
	 */
	public SCPFileUtil()  {
		//logger.setResourceBundle(bundle);
	}
	
	/**
	 * 
	 * Method Name 	: setConfig
	 * Description 		: The Method "setConfig" is used for 
	 * Date    			: May 20, 2016, 2:37:37 PM
	 * @param context
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void setContext(FileUtilContext context) throws FileSystemUtilException {
		this.hostname = context.getHostname();
		this.username = context.getUsername();
		this.port = context.getPort();
		this.readTimeOut = context.getReadTimeOut();
		this.password = context.getPassword();
		this.remoteFilePath = context.getRemoteFilePath();
		this.localFilePath = context.getLocalFilePath();
	}
	

	/**
	 * 
	 * Method Name 	: connect
	 * Description 		: The Method "connect" is used for 
	 * Date    			: May 20, 2016, 2:37:41 PM
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
			JSch jsch = new JSch();
			logger.debug("Connecting to " + hostname + " on port " + port);
			session = jsch.getSession(username, hostname,
					port);
			logger.debug("Sucessfully connected");
			UserInfo ui = new MyUserInfo();
			session.setPassword(password);
			session.setUserInfo(ui);
			logger.debug("logging in with username '" + username + "' and password");
			// BUG-FIX thread hang due to network outage
			session.setTimeout(readTimeOut);

			session.connect();
			logger.debug("Succesfully logged in");
			
		} catch (JSchException e) {
			disconnect();
			String errMessage = e.getMessage();
			logger.info(new Date() + ":: Unable to connect with parameters " + " username= "
					+ username 
					+ " , remotefilePath = " + remoteFilePath + ", " + "localFilePath = " + localFilePath + " , host= "
					+ hostname + " and port =" + port);

			if (errMessage == null) {
				errMessage = "";
			}
			logger.info("\n errormessage=" + errMessage);
			//logger.l7dlog(Level.WARN, "", null, e);
			if (errMessage.contains("UnknownHostException")) {
				throw new FileSystemUtilException("ET0019", null, Level.ERROR, e);
			} else if (errMessage.contains("ConnectException")) {
				throw new FileSystemUtilException("ET0019", null, Level.ERROR, e);

			} else if (errMessage.contains("Auth cancel")) {
				throw new FileSystemUtilException("ET0021", e);

			}
			// Bug #23451 start
			// Error message is not clear when a hostname is starting with 1.
			// this exception occurs,when host name contains 9 characters and
			// starts with 1 and the host name is invalid
			// and while trying to connect to the remote location, if we remove
			// the network cable
			else if (errMessage.contains("SocketException")) {

				throw new FileSystemUtilException("ET0019", null, Level.ERROR, e);

			}
			// Bug #23451 End
			else {

				throw new FileSystemUtilException("EL0362", null, Level.ERROR, e);

			}
		} catch (ArrayIndexOutOfBoundsException ae) {
			disconnect();
			logger.info(new Date() + ":: Unable to connect with parameters " + " username= "
					+ username 
					+ " , remotefilePath = " + remoteFilePath + ", " + "localFilePath = " + localFilePath + " , host= "
					+ hostname + " and port =" + port);

			throw new FileSystemUtilException("ET0019", null, Level.ERROR, ae);

		} catch (Exception e) {
			disconnect();
			logger.info(new Date() + ":: Unable to connect with parameters " + " username= "
					+ username
					+ " , remotefilePath = " + remoteFilePath + ", " + "localFilePath = " + localFilePath + " , host= "
					+ hostname + " and port =" + port);

			throw new FileSystemUtilException("EL0362", null, Level.ERROR, e);

		}
		logger.debug("End: " + getClass().getName() + ":connect()");
	}

	
	/**
	 * 
	 * Method Name 	: disconnect
	 * Description 		: The Method "disconnect" is used for 
	 * Date    			: May 20, 2016, 2:37:48 PM
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void disconnect() throws FileSystemUtilException {
		 logger.debug("Begin: "+getClass().getName()+":disconnect()");
		 try 
		 {
		 	logger.debug("Disconnecting from server");
		 	if(inputstream != null)
		 	{
		 		inputstream.close();
		 		inputstream = null;
		 	}
		 	if(outputstream != null)
		 	{
		 		outputstream.flush();
		 		outputstream.close();
		 		outputstream = null;
		 	}	
		 	if(channel != null)
		 	{    
		 		channel.disconnect();
		 	}
		 	if(session!=null)
		 	{    
		 		session.disconnect();
		 	}   
		 	logger.debug("Disconnected successfully");
		 }
		 catch (Throwable e) 
		 {
		 	String errMessage = e.getMessage();
		 	logger.info(new Date()+":: Unable to disconnect with parameters, " +
					" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
			if(errMessage == null)
			{
				errMessage = "";
			}
			logger.warn(errMessage,e);
		 }
		 logger.debug("End: "+getClass().getName()+":disconnect()");

	}
	
	
	/**
	 * This method executes the command and returns the object array.
	 *
	 * @param command command to be executed
	 * @return object array
	 * @throws FileSystemUtilException 
	 */
	private Object[] execCommand(String command) throws FileSystemUtilException
	{
		OutputStream out;
		InputStream in;
		
		logger.debug("Begin: "+getClass().getName()+":execCommand()");
		try 
		{			
			channel = session.openChannel("exec");
			logger.debug("channel is opened");
		} 
		catch (JSchException e) 
		{			
			logger.debug("JSchException :: excepion Reaseon ::",e);
			throw new FileSystemUtilException("EL0005",null,Level.ERROR,e);			
		}

		((ChannelExec) channel).setCommand(command);
		//get I/O streams for remote scp
		try 
		{
			out = channel.getOutputStream();
			logger.debug("execCommand :: Output stream ::"+out);
		} 
		catch (IOException ie) 
		{
			logger.debug("IOException :: excepion $#### Reaseon ::",ie);
			throw new FileSystemUtilException("EL0006",null,Level.ERROR,ie);
		}
		
		try 
		{
			in = channel.getInputStream();
		} 
		catch (IOException ie) 
		{
			logger.debug("IOException :: excepion reassss Reaseon ::",ie);
			throw new FileSystemUtilException("EL0007",null,Level.ERROR,ie);
		}
		try 
		{
			logger.debug("excuting command "+command);
			channel.connect();
			logger.debug("executed command "+command);
		} 
		catch (JSchException jse) 
		{
			logger.debug("JSchException :: excepion reassss Reaseon ::",jse);
			throw new FileSystemUtilException("EL0008",null,Level.ERROR,jse);
		}

		Object iostreams[]={out,in};
		logger.debug("End: "+getClass().getName()+":execCommand()");
		return iostreams;		
	}

	/**
	 * 
	 * Method Name 	: putFile
	 * Description 		: The Method "putFile" is used for 
	 * Date    			: May 20, 2016, 2:40:05 PM
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
	 * Date    			: May 20, 2016, 2:40:08 PM
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
	 * Date    			: May 20, 2016, 2:40:11 PM
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
	 * List files.
	 *
	 * @param isFoldersRequired the is folders required
	 * @return the string[]
	 * @throws FileSystemUtilException 
	 */
	public String[] listFiles(boolean isFoldersRequired) throws FileSystemUtilException
	{
		return listFiles(isFoldersRequired, false, false); 
	}
	
	/**
	 * List files.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @return the string[]
	 * @throws FileSystemUtilException the file system util exception
	 */
	
	public String[] listFiles(boolean isFoldersRequired, boolean isSort) throws FileSystemUtilException
	{
		return listFiles(isFoldersRequired, isSort, false); 
	}
	
	/**
	 * This method downloads the specified amount of data or 
	 * the complete file to the localfile from the httpserver.
	 *
	 * @param dLength the d length
	 * @param charset the charset
	 * @return the file data
	 * @throws FileSystemUtilException 
	 */
	public void getFileData(int dLength,String charset) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":getFileData()");
		FileOutputStream fOut=null;
		ByteArrayOutputStream bOut = null;
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		long fileLen = 0;
		long dataLength = dLength;
		if(isSample)
		{
			bOut = new ByteArrayOutputStream();
		}
		else
		{
			try 
			{
				logger.debug("Opening output stream to file "+localFilePath);
				logger.debug("localFilePath:"+localFilePath);
				fOut=new FileOutputStream("temp.txt");
				logger.debug("Output stream to file "+localFilePath+" is opened");
			} 
			catch(FileNotFoundException fnfEx) 
			{   fnfEx.printStackTrace();
				 logger.info(new Date()+":: File not found on connecting with parameters, " +
							" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
							"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
				throw new FileSystemUtilException("ET0028",null,Level.ERROR,fnfEx);
			}				
		}
		String command = "scp -f "+remoteFilePath;
		Object[] ios= execCommand(command);
		outputstream=(OutputStream)ios[0];
		inputstream=(InputStream)ios[1];
		byte[] buf = new byte[1024];
		// send '\0'
		buf[0] = 0;
		try 
		{
			//returning acknowledgement
			outputstream.write(buf, 0, 1);		
			outputstream.flush();
			checkAcknowledgement(inputstream);
			//getting filesize
			// read '0644 '
			inputstream.read(buf, 0, 5);
			while (true) 
			{
				inputstream.read(buf, 0, 1);
				if (buf[0] == ' ')
					break;
				fileLen = fileLen * 10 + (buf[0] - '0');					
			}
			String file = null;
			for (int i = 0;; i++) 
			{
				inputstream.read(buf, i, 1);
				if (buf[i] == (byte) 0x0a) 
				{
					file = new String(buf, 0, i);
					break;
				}
			}
			logger.debug("filesize="+fileLen+", file="+file);
			if(dataLength == 0)
			{
				dataLength = fileLen;
			}
			else if(dataLength >= fileLen)
			{
				dataLength = fileLen;
			}else if(fileLen > dataLength * 10){
				dataLength = 1024 * 10;
			}
			// send '\0'
			buf[0] = 0;
			outputstream.write(buf, 0, 1);
			outputstream.flush();
			long b=0;
			int l=0;
			int len=10240;
			if(len >= dataLength)
			{
				len = (int)dataLength;
			}
			byte[] barray = new byte[len];
			boolean noData = false;
			long startTime = System.currentTimeMillis();
			while( b < dataLength)
			{
				l=inputstream.read(barray,0,len);
				if(l != -1)
				{
				    noData = false;
					b=b+l;
					if(isSample)
					{
						bOut.write(barray,0,l);
					}
					else
					{
						//check whether the data is crossed fileLength
						if(b > dataLength)
						{
							l = l - (int)(b - dataLength);	
						}						
						fOut.write(barray,0,l);
					}					
				}
				else
				{
					/* BUG-FIX thread hang due to network outage */
				    //implementing readTImeout at client side.
				    if(noData == false)
				    {
				        //this is first iteration with out data 
				        startTime = System.currentTimeMillis();
				        noData = true;
				    }
				    else
				    {
				        //there is no data on prev iteration too
				        if((System.currentTimeMillis()-startTime) >= this.readTimeOut)
				        {
				            throw new Exception("SCP fileDownload failed. readTimeout occured");
				        }
				    }
				}
                    //check stop flag, if true throw FileSystemUtilException with errorcode 999
                if(stop)
                {
                   	throw new FileSystemUtilException("999");
                }
			}	
			// send '\0'
			buf[0] = 0;
			outputstream.write(buf, 0, 1);
			outputstream.flush();
			if(isSample)
			{
				String s=null;
			    if(zu.isZip(remoteFilePath))
			    {
			    	byte [] stUnZip=bOut.toByteArray(); 
			    	byte [] sample = zu.streamUnZipper(stUnZip);
			    	bOut.reset();
			    	bOut.write(sample);
			       //create a byte array stream with bOut
			        //unzip the stream here and use that stream
			    }
			    if(true)/*if(!"cp1252".equalsIgnoreCase(charset))*/
			    {
			    	sampleData = new String(bOut.toByteArray(),charset);
			    }
			    else
			    {
			    	sampleData = new String(bOut.toByteArray());
			    }
			    
				logger.debug("Sample data is : "+sampleData);
			}else{
				sampleData = new String(barray,charset);
			}
		} 
		catch(UnsupportedEncodingException use)
		{
			 logger.info(new Date()+":: Unable to get data on connecting with parameters, " +
						" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
						"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
			throw new FileSystemUtilException("ET0564",null,Level.ERROR,use);
		}	
		catch (IOException e) 
		{
			logger.info(new Date()+":: Unable to get data on connecting with parameters, " +
					" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
			throw new FileSystemUtilException("EL0004",null,Level.ERROR,e);
		}
		catch(FileSystemUtilException ebizEx)
		{
            //suppress if errorcode is 999
			
            if(!ebizEx.getErrorCode().equals("999"))
			{
            	logger.info(new Date()+":: Unable to get data on connecting with parameters, " +
    					" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
    					"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
				throw ebizEx;
            }
		}
		catch(Exception ex)
		{
			logger.info(new Date()+":: Unable to get data on connecting with parameters, " +
					" username= "+ username +"  , remotefilePath = "+remoteFilePath +", " +
					"localFilePath = "+ localFilePath +" , host= "+ hostname +" and port ="+ port );
			throw new FileSystemUtilException("EL0360",null,Level.ERROR,ex);				
		}
		finally 
		{
			try
			{
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
			}		 
		}
		logger.debug("End: "+getClass().getName()+":getFileData()");
	}
	
	/**
	 * This method used to list all the files and directories in a directory based on the condition.
	 *
	 * @param isFoldersRequired the is folders required
	 * @param isSort the is sort
	 * @param isFilesOnly the is files only
	 * @return the string[]
	 * @throws FileSystemUtilException 
	 */
	
	public String[] listFiles(boolean isFoldersRequired, boolean isSort, boolean isFilesOnly) throws FileSystemUtilException 
	{

		String remoteDir = null;
		String remoteFileName = null;
		String command = null;
		int signal = -1;
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		try
		{
			connect(); 
			
			int lastSeperatorIndex = -1;
	
			if(remoteFilePath.lastIndexOf("/") !=  -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("/");
			}else if(remoteFilePath.lastIndexOf("\\") !=  -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("\\");
			}
			if(lastSeperatorIndex == remoteFilePath.length()-1)
			{
				remoteDir = remoteFilePath;
				remoteFileName = "";
				logger.debug("file path ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				command = "cd "+remoteDir;			
				signal = executeSimpleCommand(command);
	
				if(signal != 0)
				{
					//invlid directory/file
					throw new FileSystemUtilException("ET0017");
				}
			}
			else
			{
				remoteDir = remoteFilePath.substring(0,lastSeperatorIndex+1);
				remoteFileName = remoteFilePath.substring(lastSeperatorIndex+1,remoteFilePath.length());
				logger.debug("file path not ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				command = "cd "+remoteDir;
				signal = executeSimpleCommand(command);
				if(signal != 0)
				{
					//invlid directory/file
					throw new FileSystemUtilException("ET0017");
				}
							
				if(!remoteFileName.startsWith("*.") && !remoteFileName.endsWith(".*"))
				{
					logger.debug("file/directory name "+remoteFileName+" not started with *. or ends wirh .*");
					command = "cd "+remoteDir+remoteFileName;
					signal = executeSimpleCommand(command);
							
					if(signal != 0)
					{
						boolean isFile = false;
						command = "ls "+remoteDir;
						executeSimpleCommand(command);
						String[] tempFileNames = getFileNamesArray(output);
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
							throw new FileSystemUtilException("ET0017");
						}
						logger.debug(remoteFileName+" is a file");
					}
					else
					{
						logger.debug(remoteFileName+" is a directory");
						remoteDir = remoteDir+remoteFileName;
						remoteFileName = "";
					}				
				}
			}
			logger.debug("Before getting list of files : current dir "+remoteDir+" getting files list for "+remoteFileName);
			if(!isSort)
				command = "cd "+remoteDir+";ls -F "+remoteFileName;
			if(isSort)
				command = "cd "+remoteDir+";ls -Ftr "+remoteFileName;
			signal = executeSimpleCommand(command);
			disconnect();
			if(signal != 0)
			{			    
				throw new FileSystemUtilException("ET0017");
			}
			String[] filesList=null;
			if(!isFoldersRequired && isFilesOnly)
			{//it displays only files
				filesList = getFileNames(output,isFoldersRequired);	
			}
			else if(isFoldersRequired && !isFilesOnly)
			{//it displays only folders
				filesList = getFileNames(output,isFoldersRequired);	
			}
			else
			{//it is used when  files and folders required required
				filesList = getFileNamesArray(output);		
			}
			logger.debug("End: "+getClass().getName()+":listFiles()");
			return filesList;
		}
		
		catch(FileSystemUtilException e)
		{
		    disconnect();
			throw new FileSystemUtilException(e.getErrorCode(),e.getException());
		}
	}
	/**
	 * Gets the file names.
	 *
	 * @param fileNames the file names
	 * @param isFoldersRequired the is folders required
	 * @return the file names
	 * @throws FileSystemUtilException 
	 */
	public String[] getFileNames(String fileNames,boolean isFoldersRequired) throws FileSystemUtilException
	{		
		Vector<String> files = new Vector<String>();
		String[] filesList = null;
		String fileName = null;
		logger.debug("Begin: "+getClass().getName()+":getFileNamesArray()");
		
		logger.debug("filenames are "+fileNames);
		BufferedReader reader = new BufferedReader(new StringReader(fileNames));
		try 	
		{//it is used when only folders required
			if(isFoldersRequired)
			{
				while((fileName = reader.readLine()) != null )
				{
					if(fileName.endsWith("/") && isValidString(fileName))
						files.add(fileName);
				}				
			}
			else if(!isFoldersRequired){//it is used when only files required
			while((fileName = reader.readLine()) != null)
			{
				if(!fileName.endsWith("/") && isValidString(fileName))
					files.add(fileName);
			}
			}
			logger.debug("files are "+files);
			if(files.size() != 0)
			{
				filesList = new String[files.size()];
				for(int i = 0; i < files.size(); i++)
				{
					String tempFname = (String) files.get(i);
					if(tempFname.endsWith("*"))
					{
						tempFname = tempFname.substring(0,tempFname.length()-1);
						
					}	
					filesList[i]= tempFname;
					logger.debug("file name at "+i+" is  "+filesList[i]);
				}
			}
			else
			{
				filesList = new String[0];
			}			
		}		
		catch (IOException e) 
		{
			throw new FileSystemUtilException("ET0021",e);			
		}
		logger.debug("length of fileslist is "+filesList.length);
		logger.debug("End: "+getClass().getName()+":getFileNamesArray()");
		return filesList;
	}
	

	
	
	/**
	 * This method is used to list of folders in the given path.
	 *
	 * @return String foldersList
	 * @throws FileSystemUtilException 
	 */
	public String[] getFolders() throws FileSystemUtilException
	{
		//getting all the files and folders in the given file path
		String[] files = listFiles();
		List<String> foldersList = new ArrayList<String>();
		for(int i=0;i<files.length;i++){
			//all the folder are ends with '/'
			if(files[i].endsWith("/") && isValidString(files[i])){
				foldersList.add(files[i]);
			}
		}
		String[] folders = new String[foldersList.size()];
		//getting only folders from returned array of files and folders
		for(int i=0;i<foldersList.size();i++){
			folders[i] = foldersList.get(i).toString();
		}
		return folders;
	}
	
	
	
	/**
	 * This method is used to list the files.
	 *
	 * @return the string[]
	 * @throws FileSystemUtilException 
	 */
	public String[] listFiles() throws FileSystemUtilException 
	{
		String remoteDir = null;
		String remoteFileName = null;
		String command = null;
		int signal = -1;
		logger.debug("Begin: "+getClass().getName()+":listFiles()");
		
		try
		{
			connect(); 
			
			int lastSeperatorIndex = -1;
	
			if(remoteFilePath.lastIndexOf("/") !=  -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("/");
			}else if(remoteFilePath.lastIndexOf("\\") !=  -1){
				lastSeperatorIndex = remoteFilePath.lastIndexOf("\\");
			}
			if(lastSeperatorIndex == remoteFilePath.length()-1)
			{
				remoteDir = remoteFilePath;
				remoteFileName = "";
				logger.debug("file path ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				command = "cd "+remoteDir;			
				signal = executeSimpleCommand(command);
	
				if(signal != 0)
				{
					//invlid directory/file
					throw new FileSystemUtilException("ET0017");
				}
			}
			else
			{
				remoteDir = remoteFilePath.substring(0,lastSeperatorIndex+1);
				remoteFileName = remoteFilePath.substring(lastSeperatorIndex+1,remoteFilePath.length());
				logger.debug("file path not ends with / - directory is "+remoteDir+" , filename is "+remoteFileName);
				command = "cd "+remoteDir;
				signal = executeSimpleCommand(command);
				if(signal != 0)
				{
					//invlid directory/file
					throw new FileSystemUtilException("ET0017");
				}
							
				if(!remoteFileName.startsWith("*.") && !remoteFileName.endsWith(".*"))
				{
					logger.debug("file/directory name "+remoteFileName+" not started with *. or ends wirh .*");
					command = "cd "+remoteDir+remoteFileName;
					signal = executeSimpleCommand(command);
							
					if(signal != 0)
					{
						boolean isFile = false;
						command = "ls "+remoteDir;
						executeSimpleCommand(command);
						String[] tempFileNames = getFileNamesArray(output);
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
							throw new FileSystemUtilException("ET0017");
						}
						logger.debug(remoteFileName+" is a file");
					}
					else
					{
						logger.debug(remoteFileName+" is a directory");
						remoteDir = remoteDir+remoteFileName;
						remoteFileName = "";
					}				
				}
			}
			logger.debug("Before getting list of files : current dir "+remoteDir+" getting files list for "+remoteFileName);
			command = "cd "+remoteDir+";ls -F "+remoteFileName;
			signal = executeSimpleCommand(command);
			disconnect();
			if(signal != 0)
			{			    
				throw new FileSystemUtilException("ET0017");
			}
			String[] filesList = getFileNamesArray(output);		
			logger.debug("End: "+getClass().getName()+":listFiles()");
			return filesList;
		}
		
		catch(FileSystemUtilException e)
		{
		    disconnect();
		    if(e.getMessage() != null && e.getMessage().equals("FL0071")){
		    	throw new FileSystemUtilException("FL0071");
		    }
			throw new FileSystemUtilException(e.getErrorCode(),e.getException());
		}
		
	}
	
	/**
	 * Gets the file names array.
	 *
	 * @param fileNames the file names
	 * @return the file names array
	 * @throws FileSystemUtilException 
	 */
	private String[] getFileNamesArray(String fileNames) throws FileSystemUtilException
	{		
		Vector<String> files = new Vector<String>();
		String[] filesList = null;
		String fileName = null;
		logger.debug("Begin: "+getClass().getName()+":getFileNamesArray()");
		
		logger.debug("filenames are "+fileNames);
		BufferedReader reader = new BufferedReader(new StringReader(fileNames));
		try 
		{
			while((fileName = reader.readLine()) != null)
			{
				if(isValidString(fileName))
					files.add(fileName);
			}
			logger.debug("files are "+files);
			if(files.size() != 0)
			{
				filesList = new String[files.size()];
				for(int i = 0; i < files.size(); i++)
				{
					String tempFname = (String) files.get(i);
					if(tempFname.endsWith("*"))
					{
						tempFname = tempFname.substring(0,tempFname.length()-1);
					}
					filesList[i]= tempFname;
					logger.debug("file name at "+i+" is  "+filesList[i]);
				}
			}
			else
			{
				filesList = new String[0];
			}			
		}		
		catch (IOException e) 
		{
			throw new FileSystemUtilException("ET0021",e);			
		}
		logger.debug("length of fileslist is "+filesList.length);
		logger.debug("End: "+getClass().getName()+":getFileNamesArray()");
		return filesList;
	}
	

	/**
	 * 
	 * Method Name 	: removeFile
	 * Description 		: The Method "removeFile" is used for 
	 * Date    			: May 22, 2016, 2:06:27 PM
	 * @param filePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean removeFile(String filePath) throws FileSystemUtilException {
		logger.debug("Begin: "+getClass().getName()+":deleteFile()");	
		try
		{
			connect(); 
			String command = "rm -f "+remoteFilePath;
			 int results = executeSimpleCommand(command);
			disconnect();
			if(stop)
	        {
	           	throw new FileSystemUtilException("999");
	        }			
			logger.debug("End: "+getClass().getName()+":deleteFile()");	
			if(results == 0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(FileSystemUtilException e)
		{			
		    disconnect();
		    throw e;
		}	
	}

	/**
	 * 
	 * Method Name 	: truncateFile
	 * Description 		: The Method "truncateFile" is used for 
	 * Date    			: May 20, 2016, 2:57:57 PM
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
	 * 
	 * Method Name 	: moveFile
	 * Description 		: The Method "moveFile" is used for 
	 * Date    			: May 22, 2016, 2:06:34 PM
	 * @param srcfilePath
	 * @param destfilePath
	 * @param fileName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean moveFile(String srcfilePath, String destfilePath) throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":moveFile()");
		try {
			connect(); 
			String command = "mv " + srcfilePath + " " + destfilePath;
			int results = executeSimpleCommand(command);
			disconnect();
			logger.debug("End: " + getClass().getName() + ":moveFile()");
			if (results == 0) {
				return true;
			} else {
				return false;
			}

		} catch (FileSystemUtilException e) {
			disconnect();
			throw e;
		}

	}
	
	/**
	 * This method used to upload the data using scp protocol.
	 *
	 * @param fileName the file name
	 * @return true, if successful
	 * @throws FileSystemUtilException 
	 */
	@Override
	public boolean uploadFile(String fileName) throws FileSystemUtilException {
		logger.debug("Begin:" + getClass().getName() + ".upload()");
		FileInputStream fis = null;
		String tempLocalFilePath = null;
		try {
			connect(); 
			tempLocalFilePath = localFilePath;
			String command = "scp -C -p -t " + remoteFilePath;
			Object[] ios = execCommand(command);
			outputstream = (OutputStream) ios[0];
			inputstream = (InputStream) ios[1];
			tempLocalFilePath = tempLocalFilePath + fileName;
			if (checkAck(inputstream) != 0) {
				logger.info("10117 : checking if file exists");
				throw new FileSystemUtilException("File " + tempLocalFilePath + "already exists in the remoteSystem");
			}
			long filesize = (new File(tempLocalFilePath)).length();
			command = "C0644 " + filesize + " ";
			if (tempLocalFilePath.lastIndexOf('/') > 0) {
				command += tempLocalFilePath.substring(tempLocalFilePath.lastIndexOf('/') + 1);
				logger.info("uploadfile in scp tempLocalFilePath if:: "+tempLocalFilePath);
			} else {
				logger.info("uploadfile in scp tempLocalFilePath else:: "+tempLocalFilePath);
				command += tempLocalFilePath;
			}
			command += "\n";
			outputstream.write(command.getBytes());
			outputstream.flush();
			/*if (checkAck(inputstream) != 0) {
				logger.info("10118 : writing failed");
				throw new FileSystemUtilException("Cannot write into outputstream");
			}*/
			fis = new FileInputStream(tempLocalFilePath);
			byte[] buf = new byte[1024];
			while (true) {
				int len = fis.read(buf, 0, buf.length);
				if (len <= 0)
					break;
				outputstream.write(buf, 0, len); // out.flush();
			}
			fis.close();
			fis = null;
			// send '\0'
			buf[0] = 0;
			outputstream.write(buf, 0, 1);
			outputstream.flush();
			if (checkAck(inputstream) != 0) {
				logger.info("10119 : after flushing the stream ");
				throw new FileSystemUtilException("files flushing failed");
			}
			// outputstream.close();
		} catch (Exception e) {
			logger.error("uploading of file failed :",e);
			throw new FileSystemUtilException("uploading of file failed :",e);

		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (Exception ee) {
				logger.error("uploading of file failed :",ee);
				throw new FileSystemUtilException("input stream not closed :",ee);
			}
			disconnect();
		}
		logger.debug("End:" + getClass().getName() + ".upload()");
		return true;
	}
	
	/**
	 *  
	 * This method is used to download a file from the scpserver using run mehod.
	 *
	 * @return true, if successful
	 * @throws FileSystemUtilException 
	 */
	@Override
	public boolean downloadFile() throws FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":download()");
		try {
			connect();
			getFileData(0, "");
			disconnect();
			if (stop) {
				throw new FileSystemUtilException("999");
			}
			FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
			if (zu.isZip(localFilePath)) {

				// unzip the localfilepath (a common method at import util level
				// can be used)
				// rename the extracted file to localfilepath+".txt
				// delete the localfilepath
			}
			zu = null;
			logger.debug("End: " + getClass().getName() + ":download()");
			return true;
		} catch (FileSystemUtilException e) {
			disconnect();
			logger.info(new Date() + ":: Unable to download with parameters " + ", username= " + username
					+ " , password = " + password + " , remotefilePath = " + remoteFilePath + ", " + "localFilePath = "
					+ localFilePath + " , host= " + hostname + " and port =" + port);
			throw e;
		}
	}
	
	/**
	 * 
	 * Method Name 	: renameFile
	 * Description 		: The Method "renameFile" is used for 
	 * Date    			: May 28, 2016, 6:17:47 PM
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
		throw new FileSystemUtilException("000000",null,Level.ERROR,null);

	}

	
	
	/**
	 * This method is used to get the userhome directory.
	 *
	 * @return the user home
	 * @throws FileSystemUtilException 
	 */
	@Override
	public String getUserHome() throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":test()");	
		try
		{
			connect();   
			String command = "pwd";			
			int signal = executeSimpleCommand(command);
	
			if(signal != 0)
			{			    
				throw new FileSystemUtilException("ET0030");
			}
			disconnect();		
			String[] dirName = getFileNamesArray(output);
			logger.debug("UserHome is : "+dirName[0]);
			logger.debug("End: "+getClass().getName()+":test()");
			return dirName[0];
		}
		
		catch(FileSystemUtilException ee)
		{
		    disconnect();	
			throw new FileSystemUtilException(ee.getErrorCode(),ee.getException());
		}
	}
	
	
	/**
	 * Execute simple command.
	 *
	 * @param command the command
	 * @return the int
	 * @throws FileSystemUtilException 
	 */
	private int executeSimpleCommand(String command) throws FileSystemUtilException
	{
		logger.debug("Begin: "+getClass().getName()+":executeSimpleCommand()");
		try 
		{
			logger.debug("excuting command "+command);			
			Object[] ios= execCommand(command);
			outputstream=(OutputStream)ios[0];
			inputstream=(InputStream)ios[1];			
			loadOutput(inputstream);
			//Waiting for the channel to close the connection
			logger.debug("Before sleep :: excuting command ::");
			if((isImguplaod+"").equalsIgnoreCase("Y")){
				logger.debug("Inside sleep :: excuting command :: "+channel.isConnected());
				if(channel.isConnected())
				{
					logger.debug("Inside sleep :: excuting command ::");
					Thread.sleep(2000);
				}
			}
			logger.debug("After sleep :: excuting command ::");
			//Begin for #4841
			//As per the new SCP jar file system should verify whether the channel is closed or not, other JSCH will return wrong results.
			int exitSignal = 0;
			if(channel.isClosed()){
				exitSignal = channel.getExitStatus();	
			}
			//End for #4841
			logger.debug("Command executed with return value "+exitSignal);
			channel.disconnect();
			//channel.finalize();
			
			logger.debug("End: "+getClass().getName()+":executeSimpleCommand()");
			return exitSignal;
		} 
		catch(IOException ie) 
		{
			// log and throw ebiz exception
			logger.debug("IOEXRROR :: REASONE ::",ie);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,ie);
		} 
		catch (Error e) {
			logger.error("End: "+getClass().getName()+":Error :: Reason ::",e);
			throw new FileSystemUtilException("ET0022",null,Level.ERROR,e);
		}
		catch(Throwable e) 
		{
			// log and throw ebiz exception
			logger.error("End: "+getClass().getName()+":Throwable :: Reason ::",e);
			throw new FileSystemUtilException("EC0000",null,Level.ERROR,e);
		}	
	}
	
	/**
	 * This method sets the list of files given the input stream.
	 *
	 * @param in InputStream object
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void loadOutput(InputStream in) throws IOException
	{
		logger.debug("Begin: "+getClass().getName()+":loadOutput()");	
		StringBuffer sb=new StringBuffer();
		int c;
		while((c=in.read())!= -1) 
		{		  
			sb.append((char)c);
		}	      
		output=sb.toString();		
		logger.debug("output is : "+output);
		logger.debug("Begin: "+getClass().getName()+":loadOutput()");	
	}

	

	/**
	 * 
	 * Method Name 	: removeFolder
	 * Description 		: The Method "removeFolder" is used for 
	 * Date    			: May 20, 2016, 2:57:26 PM
	 * @param path
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean removeFolder(String path) throws FileSystemUtilException {
		try{
			
			connect();
			String rmDirCmd = "rm -fr "+path;			
			int x = executeSimpleCommand(rmDirCmd);
			if(x!=0)
			{
				logger.info("Folder remove failed, may be file path is not correct or privileges are not there");
				throw new FileSystemUtilException("EL0699");
			}
			return true;
		}catch(FileSystemUtilException e)
		{
		    disconnect();
			throw new FileSystemUtilException(e.getErrorCode(),e.getException());
		}catch(Exception e)
		{
			throw new FileSystemUtilException("F00002",e);
		}

	}

	/**
	 * 
	 * Method Name 	: copyFolder
	 * Description 		: The Method "copyFolder" is used for 
	 * Date    			: May 20, 2016, 2:57:16 PM
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
	 * Date    			: May 20, 2016, 2:57:04 PM
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
 	 * This method checks the acknowledgement given the inputstream.
 	 *
 	 * @param in InputSteam object
 	 * @return success,error or fatalerror as integer indicating status
 	 * @throws IOException Signals that an I/O exception has occurred.
 	 * @throws FileSystemUtilException 
 	 */
	private int checkAcknowledgement(InputStream in) throws IOException,FileSystemUtilException
	{
		int b=in.read();
		// b may be 0 for success,
		//          1 for error,
		//          2 for fatal error,
		//          -1
		if(b==0)
		{
			return b;
		}
		else if(b==-1)
		{
			return b;
		}
		else if(b==1 || b==2)
		{
			StringBuffer sb=new StringBuffer();
			int c;
			do 
			{
				c=in.read();
				sb.append((char)c);
			}
			while(c!='\n');
			
				
			String errMessage = sb.toString();
			if(errMessage.contains("No such file"))
			{
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,null);
			}
			else if(errMessage.contains("not a regular file"))
			{
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,null);
			}				
			else if(errMessage.contains("Permission denied"))
			{
				throw new FileSystemUtilException("ET0101",null,Level.ERROR,null);
			}
			else
			{
				throw new FileSystemUtilException("EL0363",null,Level.ERROR,null);
			}
		} 
		else
		{
			return b;
		}
	}
	
	/**
	 * Check ack.
	 *
	 * @param in the in
	 * @return the int
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	static int checkAck(InputStream in) throws IOException {
		int b = in.read();
		if (b == 0)
			return b;
		if (b == -1)
			return b;

		if (b == 1 || b == 2) {
			StringBuffer sb = new StringBuffer();
			int c;
			do {
				c = in.read();
				sb.append((char) c);
			} while (c != '\n');
			if (b == 1) { // error
				logger.debug(sb.toString());
			}
			if (b == 2) { // fatal error
				logger.debug(sb.toString());
			}
		}
		return b;
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
	 * 
	 * Method Name 	: isZip
	 * Description 		: The Method "isZip" is used for 
	 * Date    			: May 22, 2016, 4:43:25 PM
	 * @param remoteFilePath
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean isZip(String remoteFilePath) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name 	: zipFile
	 * Description 		: The Method "zipFile" is used for 
	 * Date    			: May 22, 2016, 4:43:23 PM
	 * @param zipFileDetails
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public void zipFile(ZipFileDetails zipFileDetails) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name 	: uploadFiles
	 * Description 		: The Method "uploadFiles" is used for 
	 * Date    			: May 22, 2016, 4:43:15 PM
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
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name 	: createFolder
	 * Description 		: The Method "createFolder" is used for 
	 * Date    			: May 22, 2016, 4:43:12 PM
	 * @param dirName
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public boolean createFolder(String filePath) throws FileSystemUtilException {
		try{
			String userHome = getUserHome();
			//if given file path doesnot start with user home
			if(filePath == null && filePath.trim().length() < 1 && !filePath.startsWith(userHome)){
				throw new FileSystemUtilException("EL0701");
			}
			connect();
			String mkDirCmd = "mkdir -p "+filePath;			
			int x = executeSimpleCommand(mkDirCmd);
			if(x!=0)
			{
				logger.info("Folder creation failed, may be file path is not correct or privileges are not there");
				throw new FileSystemUtilException("EL0699");
			}
			return true;
		}catch(FileSystemUtilException e)
		{
		    disconnect();
			throw new FileSystemUtilException(e.getErrorCode(),e.getException());
		}catch(Exception e)
		{
			throw new FileSystemUtilException("F00002",e);
		}

	}

	/**
	 * 
	 * Method Name 	: listFolders
	 * Description 		: The Method "listFolders" is used for 
	 * Date    			: May 22, 2016, 4:43:09 PM
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String[] listFolders() throws FileSystemUtilException {
		// getting all the files and folders in the given file path
		String[] files = listFiles();
		List foldersList = new ArrayList();
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
		return folders;

	}

	/**
	 * 
	 * Method Name 	: getSampleData
	 * Description 		: The Method "getSampleData" is used for 
	 * Date    			: May 22, 2016, 4:43:06 PM
	 * @param dataLength
	 * @param charset
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public String getSampleData(int dataLength, String charset) throws FileSystemUtilException {
		try
		{
			connect();
			try
			{
			    getFileData(dataLength,charset);
			}
			catch(Exception ee)
			{
			    throw ee;
			}
			
		}catch(FileSystemUtilException ee){
			if(ee.getErrorCode() != null && ee.getErrorCode().equals("ET0101")){
				throw new FileSystemUtilException("FL0070");
			}
		}catch(Exception ex)
		{
			throw new FileSystemUtilException(ex);
		}
		finally
		{
		    disconnect();
		}
		
		return sampleData;

	}

	/**
	 * 
	 * Method Name 	: unZip
	 * Description 		: The Method "unZip" is used for 
	 * Date    			: May 22, 2016, 4:43:03 PM
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
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}

	/**
	 * 
	 * Method Name 	: streamUnZipper
	 * Description 		: The Method "streamUnZipper" is used for 
	 * Date    			: May 22, 2016, 4:43:00 PM
	 * @param sampleBytes
	 * @return
	 * @throws FileSystemUtilException
	 * @param  		:
	 * @return 		: 
	 * @throws 		: 
	 */
	
	@Override
	public byte[] streamUnZipper(byte[] sampleBytes) throws FileSystemUtilException {
		throw new FileSystemUtilException("000000", null, Level.ERROR, null);

	}
	
	/**
	 * The Class MyUserInfo.
	 */
	public static class MyUserInfo implements UserInfo 
	{
		
		/** The passwd. */
		String passwd;
		
		/* (non-Javadoc)
		 * @see com.jcraft.jsch.UserInfo#getPassword()
		 */
		public String getPassword() 
		{
			return passwd;
		}

		/**
		 * This method is used to prompt the authentication.
		 *
		 * @param str the str
		 * @return true on success
		 */
		public boolean promptYesNo(String str)
		{
			return true;
		}
		
		/**
		 * This method gets the passphrase.
		 *
		 * @return the passphrase
		 */
		public String getPassphrase() 
		{
			return null;
		}

		/**
		 * This method prompts for the passphrase.
		 *
		 * @param message the message
		 * @return true, if successful
		 */
		public boolean promptPassphrase(String message)
		{
			return false;
		}

		/**
		 * This method prompts for the password.
		 *
		 * @param message password
		 * @return true on success
		 */
		public boolean promptPassword(String message) 
		{
			return false;
		}

		/**
		 * This method displays the message.
		 *
		 * @param message the message
		 */
		public void showMessage(String message) 
		{			
		}
	}
	
	
	public String executeSpamCommand(String command) throws FileSystemUtilException, InterruptedException, IOException{
		String output="";
		try {
			connect();
			logger.debug("excuting command "+command);			
			Object[] ios= execCommand(command);
			outputstream=(OutputStream)ios[0];
			inputstream=(InputStream)ios[1];			
			loadOutput(inputstream);
			output=this.output;
			//Waiting for the channel to close the connection
			logger.debug("Before sleep :: excuting command ::");
			if((isImguplaod+"").equalsIgnoreCase("Y")){
				logger.debug("Inside sleep :: excuting command :: "+channel.isConnected());
				if(channel.isConnected())
				{
					logger.debug("Inside sleep :: excuting command ::");
					Thread.sleep(2000);
				}
			}
			logger.debug("After sleep :: excuting command ::");
			//Begin for #4841
			//As per the new SCP jar file system should verify whether the channel is closed or not, other JSCH will return wrong results.
			int exitSignal = 0;
			if(channel.isClosed()){
				exitSignal = channel.getExitStatus();	
			}
			logger.debug("Command executed with return value "+exitSignal);
			
			 disconnect();
			return output;
		} catch (FileSystemUtilException e) {
			throw e;
		}
		
	}


}
