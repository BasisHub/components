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

	public static void exportToCSV(File outputFile, ResultSet rs) {
		if (rs == null || rs.isEmpty()) {
			return;
		}
		
		String exportString = "";
		
		DataRow dr = rs.get(0);
		BBArrayList<String> fieldNames = dr.getFieldNames();
		
		for (int i = 0 ; i < fieldNames.size() - 1 ; i++) {
			String header;
			String fieldName = fieldNames.get(i);
			DataField df = dr.getField(fieldName);
			header = df.getAttribute("LABEL");
			if (header == null) {
				header = fieldName;
			}
			exportString += "\"" + header.trim() + "\",";
		}
		exportString += "\r\n";
		
		for (int i = 0 ; i < rs.size() - 1 ; i++) {
			dr = rs.get(i);
			for (int j = 0 ; j < fieldNames.size() - 1 ; j++) {
				String fieldName = fieldNames.get(j);
				String val = dr.getFieldAsString(fieldName);
				exportString += "\"" + val.trim() + "\",";
			}
			exportString = exportString.substring(0, exportString.length()-1);
			exportString += "\r\n";
		}
		
		if (outputFile != null) {
			try (FileOutputStream os = new FileOutputStream(outputFile)) {

				os.write(exportString.getBytes("UTF-8"));

				os.flush();
				os.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
