package com.basiscomponents.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.DataFieldConverter;

public class DataFieldConverterTests {

	private DataField df = new DataField(0);

	/**
	 * Tries to convert Integer into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterIntegerTest() {
		df.setValue(5);

		// Int to Int
		assertEquals(5, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(5, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(5, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(5, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

		// Int to Bool
		df.setValue(1);
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		df.setValue(0);
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));

		// Int to Double
		df.setValue(5);
		assertEquals(5.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(5.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		// Int to String
		assertEquals("5", DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));
		assertEquals("5", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("5", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		// Int to Decimal
		assertEquals(new BigDecimal(5), DataFieldConverter.convertType(df.getValue(), java.sql.Types.DECIMAL));

		// Int to Date
//		assertEquals(new Date(5), DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE));

		// Int to Timestamp

	}

	/**
	 * Tries to convert Boolean into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterBooleanTest() {
		df = new DataField(true);
		assertEquals(true, DataFieldConverter.convertType(df, java.sql.Types.BOOLEAN));
	}

	/**
	 * Tries to convert Double into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterDoubleTest() {
		df = new DataField(5.2);
		assertEquals(5.2, DataFieldConverter.convertType(df, java.sql.Types.DOUBLE));
	}

	/**
	 * Tries to convert String into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterStringTest() {
		df = new DataField("Hi");
		assertEquals("Hi", DataFieldConverter.convertType(df, java.sql.Types.VARCHAR));
	}

	/**
	 * Tries to convert Date into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterDateTest() {
		Date date = new Date(System.currentTimeMillis());
		df = new DataField(date);
		assertEquals(date, DataFieldConverter.convertType(df, java.sql.Types.DATE));
	}
}
