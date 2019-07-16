package com.basiscomponents.db;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetExporterTest {

	/**
	 * Simple test of the ResultSetExporter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void simpleResultSetExporterTest() throws Exception {
		ResultSet rs = ResultSetProvider.createDefaultResultSet();
		OutputStream out = new FileOutputStream("./src/test/testExcelExports/test1.xlsx");
		ResultSetExporter.writeXLSX(rs, out, false, false, "myLabel", null);
	}
}
