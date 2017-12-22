
package com.fileutils.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.bo.ZipFileDetails;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.impl.ZipFileUtil;
import com.fileutils.util.Constants;


public class ZipFileUtilTest {
	static ZipFileUtil zipFileUtil = null;

	private String filePath = Constants.EBIZHOME + "ziptest\\sample.zip";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		FileUtilContext context = null;

		zipFileUtil = new ZipFileUtil();
		zipFileUtil.setContext(context);
	}

	@Test
	public void testUnZipByteArray() throws Exception {
		File file = new File(filePath);

		byte[] bFile = new byte[(int) file.length()];

		HashMap<String, ByteArrayOutputStream> hashMap = zipFileUtil.unZip(bFile);
		// loop a Map
		for (Map.Entry<String, ByteArrayOutputStream> entry : hashMap.entrySet()) {
			System.out.println("Key : " + entry.getKey() + " Value : " + entry.getValue());
		}

		byte[] output = zipFileUtil.streamUnZipper(bFile);
		assertNotNull(output);

	}

	@Test
	public void atestIsZip() {
		System.out.println(System.getProperty("EBIZHOME"));
		assertTrue(zipFileUtil.isZip(filePath));
	}

	@Test
	public void testUnZipStringBoolean() throws FileSystemUtilException {

		String test = zipFileUtil.unZip(filePath, false);
		System.out.println(test);
		assertNotNull(test);
	}

	@Test
	public void testZipFile() throws FileSystemUtilException {
		ZipFileDetails zipFileDetails = new ZipFileDetails();
		zipFileDetails.setLocationfolder("ziptest");
		zipFileDetails.setFilename("sample");
		zipFileDetails.setCharset("UTF-8");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("File1", "File1 Sample Text");
		map.put("File2", "File2 Sample Text");
		map.put("File3", "File3 Sample Text");
		zipFileDetails.setZipContentList(map);
		zipFileUtil.zipFile(zipFileDetails);
	}

}
