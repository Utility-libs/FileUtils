package com.fileutils.bo;

import java.util.HashMap;

public class ZipFileDetails {

	/** The filename. */
	// FileName to export the file
	private String filename = null;

	/** The locationfolder. */
	// loca to export the file
	private String locationfolder = null;

	/** The zip content list. */
	// list of contents
	private HashMap<String, String> zipContentList = null;

	/** The charset. */
	// charset to export the file
	private String charset = null;

	/**
	 * Gets the locationfolder.
	 *
	 * @return the locationfolder
	 */
	public String getLocationfolder() {
		return locationfolder;
	}

	/**
	 * Sets the locationfolder.
	 *
	 * @param locationfolder the new locationfolder
	 */
	public void setLocationfolder(String locationfolder) {
		this.locationfolder = locationfolder;
	}

	/**
	 * Gets the charset.
	 *
	 * @return the charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * Sets the charset.
	 *
	 * @param charset the new charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * Gets the zip content list.
	 *
	 * @return the zip content list
	 */
	public HashMap<String, String> getZipContentList() {
		return zipContentList;
	}

	/**
	 * Sets the zip content list.
	 *
	 * @param zipContentList the zip content list
	 */
	public void setZipContentList(HashMap<String, String> zipContentList) {
		this.zipContentList = zipContentList;
	}

	/**
	 * Gets the filename.
	 *
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Sets the filename.
	 *
	 * @param filename the new filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

}
