
package com.fileutils.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.plugin.impl.FTPUtil;
import com.fileutils.util.Constants;


public class FTPUtilTest {

	static FTPUtil ftpUtil = null;
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String hostname = Constants.HOSTNAME;
		String username = Constants.USER_NAME;
		String password = Constants.PASSWORD;
		int readTimeOut = 100;
		int port = 22;
		FileUtilContext context = new FileUtilContext(hostname, username, password, port, readTimeOut);
		context.setPort(port);		
		ftpUtil = new FTPUtil();
		ftpUtil.setContext(context);
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
	public void testGetFolders() throws Exception{
		assertNotNull(ftpUtil.listFiles(true,true));
	}

	
}
