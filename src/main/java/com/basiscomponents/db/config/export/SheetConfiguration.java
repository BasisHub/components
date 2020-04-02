package com.basiscomponents.db.config.export;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Sheet;

/**
 * Encapsulates the configurable attributes for an XLSX export
 */
public class SheetConfiguration {
	List<ColumnConfiguration> columnConfigs;

	//Sheet name, like for Excel
	String SheetName="Output";


	public SheetConfiguration() {
		this.columnConfigs = new ArrayList<ColumnConfiguration>();
	}

	/**
	 * Appends a new column to the back of the list of columns
	 * 
	 * @param header: label of the new column to be added
	 * @throws Exception in case any parameter found invalid
	 */
	public void addColumn(String header) throws Exception {
		ColumnConfigurationBuilder colConfigBuilder = new ColumnConfigurationBuilder().setHeader(header);
		insertNewColumn(header, colConfigBuilder, columnConfigs.size());
	}

	/**
	 * Appends a new column to the back of the list of columns
	 * 
	 * @param header: label of the new column to be added
	 * @param width:  width of the new column in number of characters
	 * @throws Exception in case any parameter found invalid
	 */
	public void addColumn(String header, int width) throws Exception {
		ColumnConfigurationBuilder colConfigBuilder = new ColumnConfigurationBuilder().setHeader(header)
				.setWidth(width);
		insertNewColumn(header, colConfigBuilder, columnConfigs.size());
	}

	/**
	 * Appends a new column to the back of the list of columns
	 * 
	 * @param header:           label of the new column to be added
	 * @param colConfigBuilder: ColumnConfigurationBuilder object containing column
	 *                          customizations
	 * @throws Exception in case any parameter found invalid
	 */
	public void addColumn(String header, ColumnConfigurationBuilder colConfigBuilder) throws Exception {
		insertNewColumn(header, colConfigBuilder, columnConfigs.size());
	}

	/**
	 * Inserts a new column in position to the list of columns
	 * 
	 * @param header: label of the new column to be added
	 * @param index:  position of the new column with 0-based indexing
	 * @throws Exception in case any parameter found invalid
	 */
	public void insertColumn(String header, int index) throws Exception {
		ColumnConfigurationBuilder colConfigBuilder = new ColumnConfigurationBuilder().setHeader(header);
		insertNewColumn(header, colConfigBuilder, index);
	}

	/**
	 * Inserts a new column in position to the list of columns
	 * 
	 * @param header: label of the new column to be added
	 * @param index:  position of the new column with 0-based indexing
	 * @param width:  width of the new column in number of characters
	 * @throws Exception in case any parameter found invalid
	 */
	public void insertColumn(String header, int width, int index) throws Exception {
		ColumnConfigurationBuilder colConfigBuilder = new ColumnConfigurationBuilder().setHeader(header)
				.setWidth(width);
		insertNewColumn(header, colConfigBuilder, index);
	}

	/**
	 * Inserts a new column in position to the list of columns
	 * 
	 * @param header:           label of the new column to be added
	 * @param colConfigBuilder: ColumnConfigurationBuilder object containing column
	 *                          customizations
	 * @param index:            position of the new column with 0-based indexing
	 * @throws Exception in case of invalid index or any parameter found invalid
	 */
	public void insertColumn(String header, ColumnConfigurationBuilder colConfigBuilder, int index) throws Exception {
		insertNewColumn(header, colConfigBuilder, index);
	}

	/**
	 * Removes the column at the given position
	 * 
	 * @param index: position of the new column with 0-based indexing
	 * @throws IndexOutOfBoundsException in case of invalid index
	 */
	public void removeColumn(int index) throws IndexOutOfBoundsException {
		if (!validateIndex(index))
			throw new IndexOutOfBoundsException("Invalid index provided");

		columnConfigs.remove(index);
	}

	/**
	 * Removes all columns
	 */
	public void clearColumns() {
		columnConfigs.clear();
	}

	/**
	 * Retrieves the column at the given position
	 * 
	 * @param index: position of the new column with 0-based indexing
	 * @throws IndexOutOfBoundsException in case of invalid index
	 * @return ColumnConfiguration
	 */
	public ColumnConfiguration getColumn(int index) throws IndexOutOfBoundsException {
		if (!validateIndex(index))
			throw new IndexOutOfBoundsException("Invalid index provided");

		return columnConfigs.get(index);
	}

	/**
	 * Gets the list of column names maintaining intended order
	 * 
	 * @return List of String containing column names
	 */
	public List<String> getColumnNamesOrdered() {
		List<String> colNames = new ArrayList<String>();
		for (ColumnConfiguration colConfig : columnConfigs) {
			colNames.add(colConfig.getHeader());
		}
		return colNames;
	}

	/**
	 * Applies configurations to sheet
	 * 
	 * @param sheet: provided Sheet object to configure
	 * @return configured Sheet object
	 */
	public Sheet getConfiguredSheet(Sheet sheet) {
		for (int index = 0; index < columnConfigs.size(); index++) {
			int width = columnConfigs.get(index).getWidth();
			if (width < 0)
				continue; // Keeping default column width in case of negative width

			sheet.setColumnWidth(index, width);
		}

		return sheet;
	}

	/**
	 * Creates a new column and inserts it into given position abstracted away from
	 * exposed methods
	 * 
	 * @param header:           label of the new column to be added
	 * @param colConfigBuilder: ColumnConfigurationBuilder object containing column
	 *                          customizations
	 * @param index:            position of the new column with 0-based indexing
	 * @throws Exception in case of invalid index or any parameter found invalid
	 */
	private void insertNewColumn(String header, ColumnConfigurationBuilder colConfigBuilder, int index)
			throws Exception {

		if (!validateIndex(index))
			throw new IndexOutOfBoundsException("Invalid index provided");
		ColumnConfiguration colConfig = colConfigBuilder.setHeader(header).build();

		columnConfigs.add(index, colConfig);
	}

	/**
	 * Checks if the index provided as parameter is valid
	 * 
	 * @param index: provided index
	 * @return boolean whether the requested index is within valid range
	 */
	private boolean validateIndex(int index) {
		return index >= 0 && index <= columnConfigs.size();
	}
	
	
	/**
	 * 
	 * returns the sheet name configured for excel sheets
	 * 
	 * @return sheet name
	 * 
	 */
	public String getSheetName() {
		return SheetName;
	}

	/**
	 * 
	 * sets the Sheet Name for Excel Sheet Pages
	 * 
	 * @param sheetName: the name
	 */
	public void setSheetName(String sheetName) {
		SheetName = sheetName;
	}
}
