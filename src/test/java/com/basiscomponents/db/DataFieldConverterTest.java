package com.basiscomponents.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.sql.Date;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.DataFieldConverter;

public class DataFieldConverterTest {

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

		// Bool to Int
		df.setValue(true);
		assertEquals(1, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(1, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(1, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(1, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));
		df.setValue(false);
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

		// Bool to Bool
		df.setValue(true);
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		df.setValue(false);
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));

		// Bool to Double
		df.setValue(true);
		assertEquals(1.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(1.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));
		df.setValue(false);
		assertEquals(0.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(0.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		// Bool to String
		df.setValue(true);
		assertEquals("1", DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));
		assertEquals("1", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("1", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));
		df.setValue(false);
		assertEquals("0", DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));
		assertEquals("0", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("0", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		// Bool to Decimal
//		Boolean b = true;
//		System.out.println(b.toString());
//		df.setValue(true);
//		assertEquals(new BigDecimal("1"), DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));

	}

	/**
	 * Tries to convert Double into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterDoubleTest() {

		// Double to Int
		df.setValue(4.2);
		assertEquals(4, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(4, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(4, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(4, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

		// Double to Bool
		df.setValue(1.0);
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));
		df.setValue(0.0);
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));

		// Double to Double
		df.setValue(4.2);
		assertEquals(4.2, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(4.2, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));
	}

	/**
	 * Tries to convert String into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterStringTest() {
		df.setValue("Hi");
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
	}

	/**
	 * Tries to convert Date into every other supported type.
	 * 
	 */
	@Test
	public void DataFieldConverterDateTest() {
		Date date = new Date(System.currentTimeMillis());
		df.setValue(date);
		assertEquals(date, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE));
	}
}
