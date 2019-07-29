package com.basiscomponents.db;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.config.export.SheetConfiguration;
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
		SheetConfiguration sheetConfig = new SheetConfiguration();
		sheetConfig.addColumn("LISTFIELD", 300, true);
		ResultSetExporter.writeXLSX(rs, out, true, false, "myLabel", null, sheetConfig);
	}
}
