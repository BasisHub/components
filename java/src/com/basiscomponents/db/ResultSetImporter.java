package com.basiscomponents.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.basiscomponents.bc.SqlTableBC;

/**
 * This class provides static methods to create a com.basiscomponents.db.ResultSet object
 * from different formats. 
 */
public class ResultSetImporter {
	
	/**
	 * Parses the given Excel file and returns a com.basiscomponents.db.ResultSet with the 
	 * data defined the the Excel file.
	 * <br/>
	 * <br/>
	 * <b>Note:</b> The first row of the Excel file must contain the column names, else an Exception gets thrown.
	 * 
	 * @see #readExcel(File, DataRow)
	 * @see #readExcel(File, DataRow, List, Boolean)
	 * 
	 * @param excelFile The Excel File to parse.
	 * 
	 * @return ResultSet with the data defined in the given Excel file.
	 * 
	 * @throws Exception
	 */
	public static ResultSet readExcel(File excelFile) throws Exception{		
		return readExcel(excelFile, null, null, false);
	}
	
	/**
	 * Parses the given Excel file and returns a com.basiscomponents.db.ResultSet with the 
	 * data defined the the Excel file. The method uses the given DataRow(Attributes record) 
	 * to determine and set the correct field types in the ResultSet.
	 * <br/>
	 * <br/>
	 * <b>Note:</b> The first row of the Excel file must contain the column names, else an Exception gets thrown.
	 * 
	 * @see #readExcel(File)
	 * @see #readExcel(File, DataRow, List, Boolean)
	 * 
	 * @param excelFile The Excel File to parse.
	 * @param attributesRecord The DataRow used to set the correct data types for the fields defined in the Excel file.
	 * 
	 * @return ResultSet with the data defined in the given Excel file.
	 * 
	 * @throws Exception
	 */
	public static ResultSet readExcel(File excelFile, DataRow attributesRecord) throws Exception{
		return readExcel(excelFile, attributesRecord, null, false);
	}

	/**
	 * Parses the given Excel file and returns a com.basiscomponents.db.ResultSet with the 
	 * data defined the the Excel file. The method uses the given DataRow(Attributes record) 
	 * to determine and set the correct field types in the ResultSet. The given boolean value determines whether the 
	 * first line contains the column headers or not. If it is set to true, the first row will be treated as normal 
	 * row with field values and the given List will then be used to initialize the field names. If it is set to false,
	 * the first row will be used to initialize the field names.
	 * 
	 * @see #readExcel(File)
	 * @see #readExcel(File, DataRow)
	 * 
	 * @param excelFile The Excel File to parse.
	 * @param attributesRecord he DataRow used to set the correct data types for the fields defined in the Excel file.
	 * @param columnNames The column names to use(Only required if the firstRowHasData boolean is set to true).
	 * @param firstRowHasData The boolean value used to determine if the first row contains the column headers or not.
	 * 
	 * @return ResultSet with the data defined in the given Excel file.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static ResultSet readExcel(File excelFile, DataRow attributesRecord, List<String> columnNames, Boolean firstRowHasData) throws Exception{
		XSSFWorkbook wb = new XSSFWorkbook(excelFile);
	    XSSFSheet sheet = wb.getSheetAt(0);
	    
	    XSSFRow row;
	    XSSFCell cell;
	    XSSFRow firstRow;
	    
	    String columnName;
	    int columnIndex = 0;
	    
	    ResultSet rs = new ResultSet();
	    
	    Iterator<Cell> cellIterator;
	    Iterator<Row> rowIterator = sheet.rowIterator();
	    
	    if(!firstRowHasData){
	    	// Reading the first line to initialize the columns
		    firstRow = (XSSFRow) rowIterator.next();
		    cellIterator = firstRow.cellIterator();
		    while(cellIterator.hasNext()){
		    	cell = (XSSFCell) cellIterator.next();
		    	columnName = cell.getStringCellValue();
		    	
		    	rs.addColumn(columnName);
		    	
		    	if(attributesRecord != null){
		    		rs.setColumnType(columnIndex, attributesRecord.getFieldType(columnName));
		    	}
		    	
		    	columnIndex++;
		    }
	    }else{
	    	Iterator<String> fieldNameIterator = columnNames.iterator();
	    	while(fieldNameIterator.hasNext()){
	    		columnName = fieldNameIterator.next();
	    		rs.addColumn(columnName);
	    		
	    		if(attributesRecord != null && attributesRecord.contains(columnName)){
	    			rs.setColumnType(columnIndex, attributesRecord.getFieldType(columnName));
	    		}
	    		columnIndex++;
	    	}
	    }
	    
	    int colType;
	    int cellIndex;
	    String colName;
	    CellType cellType;
	    
	    DataRow dataRow;
	    while(rowIterator.hasNext()){
	    	row = (XSSFRow) rowIterator.next();
	    	
	    	dataRow = new DataRow();
	    	cellIterator = row.cellIterator();
		    while(cellIterator.hasNext()){
		    	cell = (XSSFCell) cellIterator.next();
		    	
		    	cellIndex = cell.getColumnIndex();
		    	colName = rs.getColumnName(cellIndex);
		    	colType = rs.getColumnType(cellIndex);
		    	cellType = cell.getCellTypeEnum();
		    	
		    	if(colType != 0){
		    		if(cellType.equals(CellType.STRING)){
			    		dataRow.setFieldValue(colName, colType, cell.getStringCellValue());
			    	}else if(cellType.equals(CellType.NUMERIC)){
			    		dataRow.setFieldValue(colName, colType, cell.getNumericCellValue());
			    	}else if(cellType.equals(CellType.BOOLEAN)){
			    		dataRow.setFieldValue(colName, colType, cell.getBooleanCellValue());
			    	}else if(cellType.equals(CellType.BLANK)){
			    		dataRow.setFieldValue(colName, colType, "");
			    	}
		    	}else{
		    		if(cellType.equals(CellType.STRING)){
			    		dataRow.setFieldValue(colName, cell.getStringCellValue());
			    	}else if(cellType.equals(CellType.NUMERIC)){
			    		dataRow.setFieldValue(colName, cell.getNumericCellValue());
			    	}else if(cellType.equals(CellType.BOOLEAN)){
			    		dataRow.setFieldValue(colName, cell.getBooleanCellValue());
			    	}else if(cellType.equals(CellType.BLANK)){
			    		dataRow.setFieldValue(colName, "");
			    	}
		    	}
		    	
		    }
		    rs.add(dataRow);
	    }
		
	    wb.close();
		
		return rs;
	}
}
