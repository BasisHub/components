package com.basiscomponents.db.export;

/**
 * Provides the attributes and methods needed to manage a particular column in a
 * spread sheet.
 */
public class ColumnConfiguration {
	private String header = null;
	private int width = -1; // Negative value to indicate default column width
	private String fieldName;

	public ColumnConfiguration(String header, int width) {
		this.header = header;
		this.width = width;
	}
	
	public ColumnConfiguration(String header, int width, String fieldName, String fieldType) {
		this.header = header;
		this.width = width;
		this.fieldName = fieldName;
	}

	public ColumnConfiguration(String header) {
		this.header = header;
	}

	/**
	 * Protected Constructor to be used by Builder class only
	 */
	protected ColumnConfiguration() {
	}

	public String getHeader() {
		return this.header;
	}

	protected void setHeader(String header) {
		this.header = header;
	}

	public int getWidth() {
		return this.width;
	}

	public void setWidth(int width) {
		this.width = width;
	}
	
	public String getFieldName() {
		return this.fieldName;
	}
	
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
}
