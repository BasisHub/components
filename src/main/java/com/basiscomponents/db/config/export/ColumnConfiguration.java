package com.basiscomponents.db.config.export;

/**
 * Provides the attributes and methods needed to manage a particular column in a
 * spread sheet.
 */
public class ColumnConfiguration {
	private String header = null;
	private int width = -1; // Negative value to indicate default column width

	public ColumnConfiguration(String header, int width) {
		this.header = header;
		this.width = width;
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
		return header;
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
}
