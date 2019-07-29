package com.basiscomponents.db.config.export;

import javax.naming.directory.InvalidAttributeValueException;

/**
 * Builder pattern implementation for class ColumnConfiguration
 */
public class ColumnConfigurationBuilder {
	private ColumnConfiguration columnConfiguration;

	/**
	 * Initializes ColumnConfigurationBuilder
	 */
	public ColumnConfigurationBuilder() {
		this.columnConfiguration = new ColumnConfiguration();
	}

	/**
	 * Sets header
	 * 
	 * @param header: Label String of the column
	 * @return ColumnConfigurationBuilder object to allow chaining
	 */
	public ColumnConfigurationBuilder setHeader(String header) {
		this.columnConfiguration.setHeader(header);
		return this;
	}

	/**
	 * Sets width
	 * 
	 * @param width: width of the column in unit: 1/256th of character width
	 * @return ColumnConfigurationBuilder object to allow chaining
	 */
	public ColumnConfigurationBuilder setWidth(int width) {
		this.columnConfiguration.setWidth(width);
		return this;
	}

	/**
	 * Sets hidden status
	 * 
	 * @param hidden: if the column should be hidden or not
	 * @return ColumnConfigurationBuilder object to allow chaining
	 */
	public ColumnConfigurationBuilder setIsHidden(boolean hidden) {
		this.columnConfiguration.setIsHidden(hidden);
		return this;
	}

	/**
	 * Builds the ColumnConfiguration object
	 * 
	 * @return ColumnConfiguration constructed
	 * @throws Exception when build() is called without setting a mandatory
	 *                   parameter
	 */
	public ColumnConfiguration build() throws InvalidAttributeValueException {
		if (columnConfiguration.getHeader() == null) {
			throw new InvalidAttributeValueException("Header found null. Builder must set attribute header");
		}
		return columnConfiguration;
	}
}
