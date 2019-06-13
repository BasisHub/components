package com.basiscomponents.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.sql.Date;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.DataFieldConverter;

public class DataFieldConverterTest {

	private DataField df = new DataField(0);

	@Test
	public void dataFieldConverterSpecialCasesTest() {

		// Value is null, the targetType can be any number
		df.setValue(null);
		assertEquals(null, DataFieldConverter.convertType(df.getValue(), 42));

		// Value is a DataField
//		df.setValue(5);
//		assertEquals(5, DataFieldConverter.convertType(df, java.sql.Types.INTEGER));
		
		// TargetType does not exist
		df.setValue(5);
		assertEquals(5, DataFieldConverter.convertType(df.getValue(), 42));

	}

	/**
	 * Tries to convert Integer into every other supported type.
	 * 
	 */
	@Test
	public void dataFieldConverterIntegerTest() {

		// Int to Int
		df.setValue(5);
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
	public void dataFieldConverterBooleanTest() {

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
	public void dataFieldConverterDoubleTest() {

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

		// Double to String
		assertEquals("4.2", DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));
		assertEquals("4.2", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("4.2", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		// Double to Decimal
		assertEquals(new BigDecimal("4.2"), DataFieldConverter.convertType(df.getValue(), java.sql.Types.DECIMAL));

		// Double to Date

		// Double to Time

		// Double to Timestamp

	}

	/**
	 * Tries to convert String into every other supported type.
	 * 
	 */
	@Test
	public void dataFieldConverterStringTest() {

		// String to Int
		df.setValue("Hi");
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

		df.setValue("42");
		assertEquals(42, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(42, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(42, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(42, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

		// String to bool
		df.setValue("true");
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));
		df.setValue(".t.");
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));
		df.setValue("1");
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(true, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));

		df.setValue("false");
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));
		df.setValue("anythingElse");
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BOOLEAN));
		assertEquals(false, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIT));

		// String to Double
		df.setValue("Hi");
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertThrows(NumberFormatException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		df.setValue("42.1337");
		assertEquals(42.1337, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(42.1337, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		// String to String
		df.setValue("Hi");
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		// String to Decimal
		df.setValue("5465643.5443");
		assertEquals(new BigDecimal("5465643.5443"),
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.DECIMAL));

	}

	/**
	 * Tries to convert Date into every other supported type.
	 * 
	 */
	@Test
	public void dataFieldConverterDateTest() {
		Date date = new Date(System.currentTimeMillis());
		df.setValue(date);

		// Date to String
		assertEquals(date.toString(), DataFieldConverter.convertType(df.getValue(), java.sql.Types.CHAR));
		assertEquals(date.toString(), DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals(date.toString(), DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		// Date to Date
		assertEquals(date, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE));
	}

//	@Test
//	public void fieldToNumberTest() {
//			
//		df.setValue("5");
//		
//		System.out.println(DataFieldConverter.fieldToNumber(null, df, 1, java.sql.Types.VARCHAR));
//	}
}
