package com.basiscomponents.db;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.ParseException;

import com.basiscomponents.db.config.export.ReportDetails;
import com.basiscomponents.db.config.export.SheetConfiguration;

import org.junit.jupiter.api.Test;

public class ResultSetExporterTest {
	
	private ResultSet createDummyResultSet() {
		ResultSet rs = new ResultSet();
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue("String", "myString");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rs.add(dr);
		return rs;
	}
	
	@Test
	public boolean pdfFileExportCreationTest() throws Exception {
		ResultSet rs = createDummyResultSet();
		
		ReportDetails reportDetails = new ReportDetails(0, "reportName", "reportTable", "0001");
		SheetConfiguration sheetConfig = new SheetConfiguration(reportDetails, 7);
		
		File file = ResultSetExporter.exportToPDF("myTestFile", "", rs, sheetConfig, false, 0, true);
		
		if (file.length() > 0) {
			return true;
		}else {
			return false;
		}
	}
	
	@Test
	public boolean canFileBeDeletedTest() throws Exception {
		ResultSet rs = createDummyResultSet();
		
		ReportDetails reportDetails = new ReportDetails(0, "reportName", "reportTable", "0001");
		SheetConfiguration sheetConfig = new SheetConfiguration(reportDetails, 7);
		
		File file = ResultSetExporter.exportToPDF("myTestFile", "", rs, sheetConfig, false, 0, true);
		
		FileChannel channel = new RandomAccessFile(file.getAbsolutePath(), "rw").getChannel();

		// if exception is thrown, file is not lockable
		FileLock lock = channel.tryLock();
		lock.release();
		return true;
	}

}
