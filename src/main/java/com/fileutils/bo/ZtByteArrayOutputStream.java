package com.fileutils.bo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

import com.enterprisedt.net.ftp.FTPClient;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.FileSystemUtilsPlugin;
import com.fileutils.plugin.impl.FileSystemUtilPluginFactory;
import com.fileutils.util.FileSystemTypeConstants;
import com.sshtools.sftp.SftpClient;

public class ZtByteArrayOutputStream extends ByteArrayOutputStream {
	
	/** The maxlimit. */
	private int maxlimit = 1024;
	
	/** The bytecount. */
	private int bytecount = 0;
	
	/** The sample data bytes. */
	private byte[] sampleDataBytes;
	
	/** The super write. */
	private boolean superWrite = true;
	
	private Object client;
	
	/** The logger. */
	Logger logger = (Logger)LoggerFactory.getLogger(getClass());

	/**
	 * Default constructor that calls the superclass constructor
	 */
	public ZtByteArrayOutputStream() {
		super();
	}

	/**
	 * Constructor that calls superclass method and sets maxlimit value
	 * 
	 * @param limit
	 *            The number of bytes to read from the file
	 */
	public ZtByteArrayOutputStream(int limit) {
		super();
		this.maxlimit = limit;
	}
	
	public ZtByteArrayOutputStream(int limit,Object client) {
		super();
		this.maxlimit = limit;
		this.client = client;
	}
	/**
	 * superclass overloaded write method that enables to read specified number
	 * of bytes from the file
	 * 
	 * @see ByteArrayOutputStream.write()
	 */
	public void write(byte[] b, int off, int len) {
		//logger.debug("Begin: " + getClass().getName() + ":write()");
		bytecount = bytecount + len;
		// bugfix getting sample for zip files with size > 10k failed - START
		// checking based on boolean variable and write to BAOS
		if (superWrite) {
			super.write(b, off, len);
		} else {
			try {
				this.close();
				if(client != null){
					if(client instanceof FTPClient){
						FTPClient ftpClient = (FTPClient)client;
						ftpClient.cancelTransfer();
					}else if(client instanceof SftpClient){
						SftpClient sftpClient = (SftpClient)client;
						try {
							sftpClient.quit();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		if (bytecount > maxlimit) {
			superWrite = false;
		}
		// bugfix getting sample for zip files with size > 10k failed - END
		sampleDataBytes = this.toByteArray();
		logger.debug("Ends: " + getClass().getName() + ":write()");
	}

	/**
	 * superclass overloaded write method that enables to read specified number
	 * of bytes from the file
	 * 
	 * @see ByteArrayOutputStream.write()
	 */
	public void write(int b) {
		//logger.debug("Begin: " + getClass().getName() + ":write()");
		bytecount++;
		// bugfix getting sample for zip files with size > 10k failed - START
		if (superWrite) {
			super.write(b);
		} else {
			try {
				this.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
		// if reach bytecount set false
		if (bytecount > maxlimit) {
			superWrite = false;
		}
		// bugfix getting sample for zip files with size > 10k failed - END
		sampleDataBytes = this.toByteArray();
		logger.debug("Ends: " + getClass().getName() + ":write()");
	}

	/**
	 * @param charset
	 * @return Returns the sampleData.
	 * @throws UnsupportedEncodingException
	 * @throws PrePublisherException
	 */
	public String getSampleData(String charset, boolean isZip)
			throws UnsupportedEncodingException, FileSystemUtilException {
		logger.debug("Begin: " + getClass().getName() + ":getSampleData()");
		String sample;
		FileSystemUtilsPlugin zu = new FileSystemUtilPluginFactory().getFileUtils(FileSystemTypeConstants.ZIP);
		logger.debug("Before :com.zt.ebiz.util.FTPUtilByteArrayOutputStream:getSampleData");
		sample = "";
		if (sampleDataBytes != null) {
			if (isZip) {
				sampleDataBytes = zu.streamUnZipper(sampleDataBytes);
				// create a byte array stream with sampledatabytes
				// unzip the stream here and use that stream
			}
			if (true)/* if(!"cp1252".equalsIgnoreCase(charset)) */
			{
				sample = new String(sampleDataBytes, charset);
			} else {
				sample = new String(sampleDataBytes);
			}
		}
		logger.debug("Ends: " + getClass().getName() + ":getSampleData()");
		return sample;
	}

}
