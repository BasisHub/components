package com.basiscomponents.db.config.export;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.export.CsvExport;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvExportTest {
	
	@Test
	public void testExportToFile() {
		File file = new File("test");
		CsvExport.exportToFile(file, "myString");
		
		boolean b = file.exists();
		assertEquals(b, true);
		file.delete();
	}
	
	@Test
	public void testBuildExportString() throws Exception {
		ResultSet rs = new ResultSet();
		DataRow dr = new DataRow();
		dr.setFieldValue("String", "myString");
		dr.setFieldValue("int", 4);
		rs.add(dr);
		dr = new DataRow();
		dr.setFieldValue("String", "myString_2");
		dr.setFieldValue("int", 5);
		rs.add(dr);
		
		String s = CsvExport.buildExportString(rs);
		
		assertEquals(s, "\"String\",\"int\"\r\n\"myString\",\"4\"\r\n\"myString_2\",\"5\"\r\n");
	}

}
