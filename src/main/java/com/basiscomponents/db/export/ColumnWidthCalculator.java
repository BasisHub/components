package com.basiscomponents.db.export;

import java.io.IOException;
import java.text.ParseException;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;

/**
 * Calculates the column width
 */
public class ColumnWidthCalculator {
	
	public static final String BASIS_BASE_FONT_RESOURCE_PATH 	  = "/pdfexport/DejaVuSansMono.ttf";
	public static final String BASIS_BASE_FONT_BOLD_RESOURCE_PATH = "/pdfexport/DejaVuSansMono-Bold.ttf";
	public static final int BASIS_BASE_FONT_SIZE = 7;

	/**
	 * Calculates the column widths
	 * 
	 * @param rs				the result set which contains representative data rows
	 * @param fontSize			the font size that is used to calculate the column width
	 * @param calculateAllRows	indicator if the perfect column width should be calculated for all rows in the result set
	 * 
	 * @return	returns a data row which contains the width for each column (data field)
	 */
	public static DataRow calculateColumnWidths(ResultSet rs, float fontSize, boolean calculateAllRows) {
		return calculateColumnWidths(rs, "", fontSize, -1, calculateAllRows);
	}
	
	/**
	 * Calculates the column widths
	 * 
	 * @param rs				the result set which contains representative data rows
	 * @param fontSize			the font size that is used to calculate the column width
	 * 
	 * @return	returns a data row which contains the width for each column (data field)
	 */
	public static DataRow calculateColumnWidths(ResultSet rs, float fontSize) {
		return calculateColumnWidths(rs, "", fontSize, -1, false);
	}
	
	/**
	 * Loads the base font
	 * 
	 * @param fontResourcePath	path leading to the font resource
	 * 
	 * @return	return the base font
	 */
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
	
	/** 
	 * Loads the basic base font
	 * 
	 * @return	returns the basic base font
	 */
	private static BaseFont loadBasisBaseFont() {
		return loadBaseFont(BASIS_BASE_FONT_RESOURCE_PATH);
	}

	/** 
	 * Calculates the column widths
	 * 
	 * @param rs				the result set which contains representative data rows
	 * @param fontResourcePath	path leading to the font resource
	 * @param fontSize			the font size that is used to calculate the column width
	 * @param rowsToCheck		the number of rows that should be used to calculate the column width
	 * @param calculateAllRows	indicator if the perfect column width should be calculated for all rows in the result set
	 * 
	 * @return	returns a data row which contains the width for each column (data field)
	 */
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
		
		// check each header's length
		baseFont = loadBaseFont(BASIS_BASE_FONT_BOLD_RESOURCE_PATH);
		dr = calculateHeaderWidth(fieldNames, dr, baseFont, fontSize);
		return dr;
	}
	
	/**
	 * Calculates the field width for each data field in a data row for a defined number of data rows.
	 * 
	 * @param rs				the result set which contains the data rows to calculate
	 * @param rowsToCalculate	the number of rows that should be calculated
	 * @param fieldNames		the array that contains all the field names used in the data rows
	 * @param dr				the data row that is used to fill the calculated field width in
	 * @param baseFont			the base font that is used to calculate the field width
	 * @param fontSize			the font size that is used to calculate the field width
	 * 
	 * @return	returns a data row containing the field widths
	 */
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
	
	/**
	 * Calculates the column header width
	 * 
	 * @param fieldNames		the array that contains all the field names used in the data rows
	 * @param dr				the data row that is used to fill the calculated field width in
	 * @param baseFont			the base font that is used to calculate the field width
	 * @param fontSize			the font size that is used to calculate the field width
	 *
	 * @return	returns a data row containing the field widths
	 */
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
