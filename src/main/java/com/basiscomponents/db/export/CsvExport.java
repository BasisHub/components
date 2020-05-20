package com.basiscomponents.db.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.BBArrayList;

public class CsvExport {
	
	/**
	 * Method to export a result set to a CSV file
	 * 
	 * @param outputFile	the ouput file
	 * @param rs			the result set that holds the data to export
	 */
	public static void exportToCSV(File outputFile, ResultSet rs) {
		if (rs == null || rs.isEmpty()) {
			return;
		}
		
		String exportString = buildExportString(rs);
		exportToFile(outputFile, exportString);
	}
	
	/**
	 * Method to build the CSV export String
	 * 
	 * @param rs			the result set that holds the data to export
	 */
	public static String buildExportString(ResultSet rs) {
		String exportString = "";
		
		DataRow dr = rs.get(0);
		BBArrayList<String> fieldNames = dr.getFieldNames();
		
		for (int i = 0 ; i < fieldNames.size() ; i++) {
			String header;
			String fieldName = fieldNames.get(i);
			DataField df = dr.getField(fieldName);
			header = df.getAttribute("LABEL");
			if (header == null) {
				header = fieldName;
			}
			exportString += "\"" + header.trim() + "\",";
		}
		exportString = exportString.substring(0, exportString.length()-1);
		exportString += "\r\n";
		
		for (int i = 0 ; i < rs.size() ; i++) {
			dr = rs.get(i);
			for (int j = 0 ; j < fieldNames.size() ; j++) {
				String fieldName = fieldNames.get(j);
				String val = dr.getFieldAsString(fieldName);
				exportString += "\"" + val.trim() + "\",";
			}
			exportString = exportString.substring(0, exportString.length()-1);
			exportString += "\r\n";
		}
		
		return exportString;
	}
	
	/**
	 * Method to export the export string to the given file
	 * 
	 * @param outputFile	the ouput file
	 * @param exportString	the export string
	 */
	public static void exportToFile(File outputFile, String exportString) {
		try (FileOutputStream os = new FileOutputStream(outputFile)) {
			if (outputFile != null) {
				os.write(exportString.getBytes("UTF-8"));
				
				os.flush();
				os.close();
			}else {
				throw new IOException("Given output file is null.");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
