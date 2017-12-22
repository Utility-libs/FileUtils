package com.fileutils.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fileutils.bo.FileUtilContext;
import com.fileutils.exception.FileSystemUtilException;
import com.fileutils.plugin.impl.LFSUtil;
import com.fileutils.util.Constants;

public class LFSUtilTest {

	static LFSUtil lfsUtil = null;
	private String filePath = Constants.EBIZHOME + "LFS\\";

	@BeforeClass
	public static void setUpClass() throws FileSystemUtilException {
		// Initialize stuff once for ALL tests (run once)
		FileUtilContext context = null;
		lfsUtil = new LFSUtil();
	}

	@Test
	public void testPutFileInputStreamStringString() throws FileSystemUtilException {

		lfsUtil.putFile("Test String", filePath + "SampleFile.txt");

		String files[] = lfsUtil.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[0].equals("SampleFile.txt"))
				assertTrue(true);
			else
				assertFalse(false);
		}

	}

	@Test
	public void testGetFile() {

	}

	@Test
	public void testListFiles() throws FileSystemUtilException {
		String files[] = lfsUtil.listFiles();
		assertNotNull(files);
	}

	@Test
	public void testRemoveFile() throws FileSystemUtilException {
		testPutFileInputStreamStringString();
		assertTrue(lfsUtil.removeFile(filePath + "SampleFile.txt"));
	}

	@Test
	public void testTruncateFile() throws FileSystemUtilException {
		testPutFileInputStreamStringString();
		assertTrue(lfsUtil.truncateFile(filePath + "SampleFile.txt"));
	}

	@Test
	public void testCreateDirectory() throws FileSystemUtilException {
		assertTrue(lfsUtil.createFolder(filePath + "TestFolder\\"));
	}

	@Test
	public void testMoveFile() throws FileSystemUtilException {
		testPutFileInputStreamStringString();
		assertTrue(lfsUtil.moveFile(filePath + "SampleFile.txt", filePath + "TestFolder\\"));
	}

	@Test
	public void testRenameFile() throws FileSystemUtilException {
		testPutFileInputStreamStringString();
		assertTrue(lfsUtil.renameFile(filePath + "SampleFile.txt", filePath + "TestFolder\\"));
	}

	@Test
	public void testListDirectories() throws FileSystemUtilException {
		List<String> dirList = lfsUtil.listFolders(filePath);
		assertNotNull(dirList);
	}

	@Test
	public void testRemoveDirectory() throws FileSystemUtilException {
		assertTrue(lfsUtil.removeFolder(filePath + "TestFolder\\"));
	}

}
