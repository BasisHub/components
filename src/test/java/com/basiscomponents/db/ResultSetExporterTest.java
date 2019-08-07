package com.basiscomponents.db;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.config.export.SheetConfiguration;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetExporterTest {

	/**
	 * Simple test of the ResultSetExporter's Excel writing method
	 * 
	 * @throws Exception
	 */
	@Test
	public void simpleResultSetExporterExcelTest() throws Exception {
		ResultSet rs = ResultSetProvider.createDefaultResultSet();
		OutputStream out = new FileOutputStream("./src/test/testExports/test1.xlsx");
		SheetConfiguration sheetConfig = new SheetConfiguration();
		sheetConfig.addColumn("LISTFIELD", 300, true);
		ResultSetExporter.writeXLSX(rs, out, true, false, "myLabel", null, sheetConfig);
	}

	/**
	 * Simple test of the ResultSetExporter's Html writing method
	 * 
	 * @throws Exception
	 */
	@Test
	public void simpleResultSetExporterHtmlTest() throws Exception {

		ResultSet rs = new ResultSet();

		DataRow dr = new DataRow();
		dr.setFieldValue("feld1", "TEST1");
		dr.setFieldValue("feld2", "TEST2");
		rs.add(dr);

		dr = new DataRow();
		dr.setFieldValue("feld1", "TEST1");
		dr.setFieldValue("feld3", "TEST3");
		rs.add(dr);

		HashMap<String, String> links = new HashMap<>();
		links.put("feld1", "/rest/test/{feld1}");
		links.put("feld2", "/rest/test/{feld1}/{feld2}");

		FileWriter fw = new FileWriter("./src/test/testExports/test.html");
		ResultSetExporter.writeHTML(rs, fw, links);
		fw.flush();
		fw.close();
	}

	/**
	 * Simple test of the ResultSetExporter's Excel writing method
	 * 
	 * @throws Exception
	 */
	@Test
	public void simpleResultSetExporterTextFileTest() throws Exception {
		ResultSet rs = ResultSetProvider.createDefaultResultSet();
		FileWriter fw = new FileWriter("./src/test/testExports/test.txt");
		ResultSetExporter.writeTXT(rs, fw);
		fw.flush();
		fw.close();
	}

}
