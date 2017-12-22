
package com.fileutils.test;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.impl.HTTPUtil;
import com.fileutils.util.Constants;


public class HTTPUtilTest {

	static HTTPUtil httpUtil = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String hostname = null;
		String username = null;
		String password = null;
		int readTimeOut = 1000;
		int port = 22;
		String filePath = Constants.EBIZHOME;
		FileUtilContext context = new FileUtilContext(hostname, username, password, port, readTimeOut);
		
		context.setUrlString("http://www.gnu.org/licenses/gpl.txt");
		context.setLocalFilePath(filePath+"sample.txt");
		// configBean.setRemoteFilePath("licenses/gpl.txt");
		httpUtil = new HTTPUtil();
		httpUtil.setContext(context);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		httpUtil.disconnect();
	}

	@Test
	public void testDownloadFile() throws FileSystemUtilException {
		httpUtil.downloadFile();
	}

}
