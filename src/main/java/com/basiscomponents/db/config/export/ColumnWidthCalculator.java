package com.basiscomponents.db.config.export;

import java.io.IOException;
import java.text.ParseException;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

public class ColumnWidthCalculator {
	
	public static final String BASIS_BASE_FONT_RESOURCE_PATH 	  = "/DejaVuSansMono.ttf";
	public static final String BASIS_BASE_FONT_BOLD_RESOURCE_PATH = "/DejaVuSansMono-Bold.ttf";
	public static final int BASIS_BASE_FONT_SIZE = 7;

	public static DataRow calculateColumnWidths(ResultSet rs, float fontSize, boolean calculateAllRows) {
		return calculateColumnWidths(rs, "", fontSize, -1, calculateAllRows);
	}
	
	public static DataRow calculateColumnWidths(ResultSet rs, float fontSize) {
		return calculateColumnWidths(rs, "", fontSize, -1, false);
	}
	
	private static BaseFont loadBaseFont(String fontResourcePath) {
		try {
			BaseFont baseFont = BaseFont.createFont(fontResourcePath, BaseFont.IDENTITY_H, false);
			return baseFont;
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	private static BaseFont loadBasisBaseFont() {
		return loadBaseFont(BASIS_BASE_FONT_RESOURCE_PATH);
	}

	public static DataRow calculateColumnWidths(ResultSet rs, String fontResourcePath, float fontSize, int rowsToCheck, boolean calculateAllRows) {
		if(rs.isEmpty()) {
			return new DataRow();
		}
		
		if(fontSize <= 0) {
			fontSize = BASIS_BASE_FONT_SIZE;
		}
		
		if(fontResourcePath.trim().length() == 0) {
			fontResourcePath = BASIS_BASE_FONT_RESOURCE_PATH;
		}
		
		int rowsToCalculate;
		if (calculateAllRows) {
			rowsToCalculate = rs.size();
		}else if(rowsToCheck == -1){
			if (rs.size() >= 100) {
				rowsToCalculate = 100;
			}else {
				rowsToCalculate = rs.size();
			}
		}else {
			rowsToCalculate = rowsToCheck;
		}

		BBArrayList<String> fieldNames = rs.get(0).getFieldNames();
		DataRow dr = new DataRow();
		int i = 0;
		while(i < fieldNames.size()) {
			try {
				dr.setFieldValue(fieldNames.get(i), 0);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		
		BaseFont baseFont = loadBaseFont(fontResourcePath);
		if (baseFont == null) {
			return new DataRow();
			//TODO: throw exception
		}
		
		// check <each> field's length
		dr = calculateFieldWidth(rs, rowsToCalculate, fieldNames, dr, baseFont, fontSize);
		System.out.println(dr.toString());
		
		// check each header's length
		baseFont = loadBaseFont(BASIS_BASE_FONT_BOLD_RESOURCE_PATH);
		dr = calculateHeaderWidth(fieldNames, dr, baseFont, fontSize);
		System.out.println(dr.toString());
		return dr;
	}
	
	private static DataRow calculateFieldWidth(ResultSet rs, int rowsToCalculate, BBArrayList<String> fieldNames, DataRow dr, BaseFont baseFont, float fontSize) {
		int numberOfFieldNames = fieldNames.size();
		int i = 0;
		while(i < numberOfFieldNames) {
			String fieldName = fieldNames.get(i);
			int j = 0;
			while(j < rowsToCalculate) {
				DataRow tempDR = rs.get(j);
				String fieldValue = tempDR.getFieldAsString(fieldName);
				try {
					float valueWidth = baseFont.getWidthPoint(fieldValue.trim(), fontSize);
					if(valueWidth > dr.getFieldAsNumber(fieldName).floatValue()) {
						int val = (int) Math.ceil(valueWidth);
						dr.setFieldValue(fieldName, val);
						System.out.println(tempDR.getFieldAsString(fieldName));
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				j++;	
			}
		i++;
		}
		return dr;
	}
	
	private static DataRow calculateHeaderWidth(BBArrayList<String> fieldNames, DataRow dr, BaseFont baseFont, float fontSize) {
		fontSize += 2; // because the header is BOLD
		int numberOfFieldNames = fieldNames.size();
		int i = 0;
		while(i < numberOfFieldNames) {
			String fieldName = fieldNames.get(i);
			try {
				float valueWidth = baseFont.getWidthPoint(fieldName.trim(), fontSize);
				if(valueWidth > dr.getFieldAsNumber(fieldName).floatValue()) {
					int val = (int) Math.ceil(valueWidth);
					dr.setFieldValue(fieldName, val);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		i++;
		}
		return dr;
	}

}
