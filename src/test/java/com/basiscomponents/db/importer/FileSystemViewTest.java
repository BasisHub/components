package com.basiscomponents.db.importer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.ResultSet;

public class FileSystemViewTest {

	private ResultSet data;
	private String path;
	private long size;

	/**
	 * There is a test directory which was specially created for this test. The
	 * FileSystemScanner will create a ResultSet out of this. The ResultSet is
	 * checked to contain the correct data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fileSystemViewComplexTest() throws Exception {

		data = FileSystemScanner.getDataSystemAsResultSet("./src/test/testDirForFileSystemScanner/Tests 'n' Secrets",
				"");

		path = (String) data.get(0).getFieldValue("filepath");
		assertTrue(path.endsWith("SecretDoc.txt"));
		size = (long) data.get(0).getFieldValue("size");
		assertEquals(3288, size);

		path = (String) data.get(1).getFieldValue("filepath");
		assertTrue(path.endsWith("TopSecretDoc.txt"));
		size = (long) data.get(1).getFieldValue("size");
		assertEquals(12, size);

		path = (String) data.get(2).getFieldValue("filepath");
		assertTrue(path.endsWith("VerySecretDoc.txt"));
		size = (long) data.get(2).getFieldValue("size");
		assertEquals(0, size);

		path = (String) data.get(3).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc1.txt"));
		size = (long) data.get(3).getFieldValue("size");
		assertEquals(0, size);

		path = (String) data.get(4).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc2.txt"));
		size = (long) data.get(4).getFieldValue("size");
		assertEquals(33, size);

		path = (String) data.get(5).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc3.txt"));
		size = (long) data.get(5).getFieldValue("size");
		assertEquals(22220, size);
	}

	/**
	 * This test filters the test directory for data which contains test or secret
	 * in their name.
	 * 
	 * @throws Exception
	 */
	@Test
	public void fileSystemScannerFilterTest() throws Exception {

		data = FileSystemScanner.getDataSystemAsResultSet("./src/test/testDirForFileSystemScanner/Tests 'n' Secrets",
				"test");

		path = (String) data.get(0).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc1.txt"));
		size = (long) data.get(0).getFieldValue("size");
		assertEquals(0, size);

		path = (String) data.get(1).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc2.txt"));
		size = (long) data.get(1).getFieldValue("size");
		assertEquals(33, size);

		path = (String) data.get(2).getFieldValue("filepath");
		assertTrue(path.endsWith("testDoc3.txt"));
		size = (long) data.get(2).getFieldValue("size");
		assertEquals(22220, size);

		data = FileSystemScanner.getDataSystemAsResultSet("./src/test/testDirForFileSystemScanner/Tests 'n' Secrets",
				"Secret");

		path = (String) data.get(0).getFieldValue("filepath");
		assertTrue(path.endsWith("SecretDoc.txt"));
		size = (long) data.get(0).getFieldValue("size");
		assertEquals(3288, size);

		path = (String) data.get(1).getFieldValue("filepath");
		assertTrue(path.endsWith("TopSecretDoc.txt"));
		size = (long) data.get(1).getFieldValue("size");
		assertEquals(12, size);

		path = (String) data.get(2).getFieldValue("filepath");
		assertTrue(path.endsWith("VerySecretDoc.txt"));
		size = (long) data.get(2).getFieldValue("size");
		assertEquals(0, size);
	}

	@Test
	public void fileSystemViewEmptyDirTest() throws Exception {

		data = FileSystemScanner.getDataSystemAsResultSet("./src/test/testDirForFileSystemScanner/Empty Dirs", "");

		path = (String) data.get(0).getFieldValue("filepath");
		assertTrue(path.endsWith("Empty Dir 1"));
		size = (long) data.get(0).getFieldValue("size");
		assertEquals(0, size);

		path = (String) data.get(1).getFieldValue("filepath");
		assertTrue(path.endsWith("Empty Dir 2"));
		size = (long) data.get(1).getFieldValue("size");
		assertEquals(0, size);
	}

}
