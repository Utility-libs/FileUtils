
package com.fileutils.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.impl.SCPFileUtil;
import com.fileutils.util.Constants;


public class SCPUtilTest {
	static SCPFileUtil scpFileUtil = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String hostname = Constants.HOSTNAME;
		String username = Constants.USER_NAME;
		String password = Constants.PASSWORD;
		String localFilepath = Constants.EBIZHOME;
		int readTimeOut = 100;
		int port = 22;
		FileUtilContext context = new FileUtilContext(hostname, username, password, port, readTimeOut);
		context.setRemoteFilePath("/home/zh15dev69/product");
		context.setLocalFilePath(localFilepath+"SCP\\");
		scpFileUtil = new SCPFileUtil();
		scpFileUtil.setContext(context);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {

	}

	@Test
	public void testGetFile() throws FileSystemUtilException {
		String folders[] = scpFileUtil.getFolders();

		assertNotNull(folders);

	}

	@Test
	public void testGetFileNames() throws FileSystemUtilException {
		String folders[] = scpFileUtil.getFileNames("sample2.txt", true);

		assertNotNull(folders);

	}

	@Test
	public void testListFilesBoolean() throws FileSystemUtilException {
		String files[] = scpFileUtil.listFiles(true);
		assertNotNull(files);
	}

	@Test
	public void testListFilesBooleanBooleanBoolean() throws FileSystemUtilException {
		String files[] = scpFileUtil.listFiles();
		assertNotNull(files);
	}

	@Test
	public void testUploadFile() throws FileSystemUtilException {
		assertTrue(scpFileUtil.uploadFile("sample3"));
	}

	@Test
	public void testDownloadFile() throws FileSystemUtilException {
		assertFalse(scpFileUtil.downloadFile());
	}

}
