
package com.fileutils.bo;


public class FileUtilContext {

	/** The hostname. */
	private String hostname;

	/** The username. */
	private String username;

	/** The password. */
	private String password;

	/** The port. */
	private int port;

	/** The session timeout. */
	private int readTimeOut;

	/** The urlString */
	private String urlString;

	/** The localFilePath */
	private String localFilePath;
	/** The remoteFilePath */
	private String remoteFilePath;

	/**
	 * Instantiates a new file system util bean.
	 *
	 * @param hostname
	 *            the hostname
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 * @param port
	 *            the port
	 * @param sessionTimeout
	 *            the session timeout
	 */
	public FileUtilContext(String hostname, String username, String password, int port, int sessionTimeout) {
		super();
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.port = port;
		this.readTimeOut = sessionTimeout;
	}

	/**
	 * Gets the hostname.
	 *
	 * @return the hostname
	 */
	public String getHostname() {
		return hostname;
	}

	/**
	 * Sets the hostname.
	 *
	 * @param hostname
	 *            the new hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	/**
	 * Gets the username.
	 *
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 *
	 * @param username
	 *            the new username
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 *
	 * @param password
	 *            the new password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Sets the port.
	 *
	 * @param port
	 *            the new port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Gets the session timeout.
	 *
	 * @return the session timeout
	 */
	public int getReadTimeOut() {
		return readTimeOut;
	}

	/**
	 * Sets the readTimeOut .
	 *
	 * @param readTimeOut
	 *            the new readTimeOut
	 */
	public void setReadTimeOut(int readTimeOut) {
		this.readTimeOut = readTimeOut;
	}

	/**
	 * Gets the url string.
	 *
	 * @return the url string
	 */
	public String getUrlString() {
		return urlString;
	}

	/**
	 * Sets the url string.
	 *
	 * @param urlString
	 *            the new url string
	 */
	public void setUrlString(String urlString) {
		this.urlString = urlString;
	}

	/**
	 * Gets the local file path.
	 *
	 * @return the local file path
	 */
	public String getLocalFilePath() {
		return localFilePath;
	}

	/**
	 * Sets the local file path.
	 *
	 * @param localFilePath
	 *            the new local file path
	 */
	public void setLocalFilePath(String localFilePath) {
		this.localFilePath = localFilePath;
	}

	/**
	 * Gets the remote file path.
	 *
	 * @return the remote file path
	 */
	public String getRemoteFilePath() {
		return remoteFilePath;
	}

	/**
	 * Sets the remote file path.
	 *
	 * @param remoteFilePath the new remote file path
	 */
	public void setRemoteFilePath(String remoteFilePath) {
		this.remoteFilePath = remoteFilePath;
	}

}
