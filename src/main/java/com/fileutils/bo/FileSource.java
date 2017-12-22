package com.fileutils.bo;

public class FileSource 
{
	/**
	 * This constant is for ftp protocol
	 */
	public static final String PROTOCOL_FTP = "ftp";
	
	public static final String PROTOCOL_SFTP = "sftp";
	/**
	 * This constant is for http protocol
	 */
	public static final String PROTOCOL_HTTP = "http";
	/**
	 * This constant is for https protocol
	 */
	public static final String PROTOCOL_HTTPS = "https";
	/**
	 * This constant is for scp protocol
	 */
	public static final String PROTOCOL_SCP = "scp";
	
	public static final int DEFAULT_FTP_PORT = 21;
	public static final int DEFAULT_SCP_PORT = 22;
	public static final int DEFAULT_SFTP_PORT = 22;
	
	public static final long serialVersionUID = 4098L;
	
	public String protocol;
	public String path;
	
	/**
	 * Returns the file path.
	 * @return path
	 */
	public String getPath()
	{
		return path;
	}
	/** 
	 * Sets the file path
	 * @param currentFilePath The path to set.
	 */
    public void setPath(String currentFilePath)
    {
    	this.path= currentFilePath;
    }
	
	/**
	 * Returns the protocol.
	 * @return protocol
	 */
	public String getProtocol() {
		return protocol;
	}
	/**
	 * Sets the protocol.
	 * @param protocol The protocol to set.
	 */
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
