package com.fileutils.test;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.impl.SFTPUtil;
import com.fileutils.util.Constants;


public class SFTPUtilTest {

	static SFTPUtil sftpUtil = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String hostname = Constants.HOSTNAME;
		String username = Constants.USER_NAME;
		String password = Constants.PASSWORD;
		int readTimeOut = 100;
		int port = 22;
		FileUtilContext context = new FileUtilContext(hostname, username, password, port, readTimeOut);
		context.setPort(port);
		context.setRemoteFilePath("/home/zh15dev69/product");
		context.setLocalFilePath("E:\\EBIZHOME\\");
		sftpUtil = new SFTPUtil();
		sftpUtil.setContext(context);
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
	public void testCreateFolderString() throws FileSystemUtilException {
		String filePath = "/home/zh15dev69/product/testFolder";
		assertTrue(sftpUtil.createFolder(filePath));
	}

	@Test
	public void testUploadFile() throws FileSystemUtilException {

		assertTrue(sftpUtil.uploadFile("sample2.txt"));
	}

}
