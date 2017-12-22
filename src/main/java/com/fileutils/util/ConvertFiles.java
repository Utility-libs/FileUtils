package com.fileutils.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.util.FileUtil;

public class ConvertFiles {

	static Logger logger = (Logger) LoggerFactory.getLogger(ConvertFiles.class);

	public String DOS_TERM = "\r\n";
	public String UNIX_TERM = "\r";

	public boolean unixToDos(String filePath) throws Exception {
		return convertLineTerminator(filePath, DOS_TERM);
	}

	public boolean dosToUnix(String filePath) throws Exception {
		return convertLineTerminator(filePath, UNIX_TERM);
	}

	/**
	 * Convert line terminator.
	 *
	 * @param filePath
	 *            the file path
	 * @param terminator
	 *            the terminator
	 * @return true, if successful
	 * @throws Exception
	 *             the exception
	 */
	private boolean convertLineTerminator(String filePath, String terminator) throws Exception {
		File inFile = new File(filePath);
		File outFile = new File(filePath + ".conv.tmp");
		BufferedReader inReader = new BufferedReader(new FileReader(inFile));
		PrintWriter outWriter = new PrintWriter(new FileWriter(outFile));
		try {
			String line = "";
			while ((line = inReader.readLine()) != null) {
				outWriter.print(line + terminator);
			}
			outWriter.flush();
			outWriter.close();
			logger.debug("Converted file : " + outFile.getPath());
			inReader.close();
			logger.debug("deleting source file : " + inFile.getPath());
			inFile.delete();
			logger.debug("Renaming file to : " + inFile.getPath());
			outFile.renameTo(inFile);
			logger.debug("Completed file conversion: " + inFile.getPath());
		} finally {
			if (outWriter != null)
				outWriter.close();
			if (inReader != null)
				inReader.close();
		}
		return true;
	}

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
	 * @throws Exception
	 *             the exception
	 */
	public boolean convertFileToUTFFormat(String fileName, String encodeFormat, String newFileName) throws Exception {
		boolean result = true;
		logger.debug("utf 16 convertion started : fileName ="+fileName+" , encodeFormat = "+encodeFormat+" , newFileName = "+newFileName);
		if(encodeFormat != null && encodeFormat.equalsIgnoreCase(Constants.utf8))
			encodeFormat = Constants.UTF_8;
		else if(encodeFormat != null && encodeFormat.equalsIgnoreCase(Constants.utf16))
			encodeFormat = Constants.UTF_16LE;
		else if(encodeFormat != null && encodeFormat.equalsIgnoreCase(Constants.UTF_16))
			encodeFormat = Constants.UTF_16LE;
		
		File inFile = new File(fileName);
		File outFile = new File(newFileName);
		
		if(inFile.getName().equalsIgnoreCase(outFile.getName())){
			inFile = new File(fileName);
			outFile = new File(fileName + ".utf16.tmp");
		}
		
		BufferedReader inReader = null;
		PrintWriter outWriter = null;
		try {
			inReader = new BufferedReader(new FileReader(inFile));
			outWriter = new PrintWriter(outFile,encodeFormat);
			String line = null;
			while ((line = inReader.readLine()) != null) {
				outWriter.println(line);
			}
			outWriter.flush();
			outWriter.close();
			logger.debug("Converted file : " + outFile.getPath());
			inReader.close();
			logger.debug("deleting source file : " + inFile.getPath());
			if(inFile.delete()){
			logger.debug("Renaming file to : " + inFile.getPath());
			outFile.renameTo(inFile);
			}
			logger.debug("Completed file conversion: " + inFile.getPath());
		} finally {
			if (outWriter != null)
				outWriter.close();
			if (inReader != null)
				inReader.close();
		}
		return result;
	}
	
}
