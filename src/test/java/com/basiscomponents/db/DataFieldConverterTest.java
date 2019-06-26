package com.basiscomponents.db;

import static com.basiscomponents.constants.SpecialCharacterConstants.FRENCH_SPECIAL_CHARACTERS;
import static com.basiscomponents.constants.SpecialCharacterConstants.GERMAN_SPECIAL_CHARACTERS;
import static com.basiscomponents.constants.SpecialCharacterConstants.MATHEMATICAL_SPECIAL_CHARACTERS;
import static com.basiscomponents.constants.SpecialCharacterConstants.STANDARD_SPECIAL_CHARACTERS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.DataFieldConverter;

public class DataFieldConverterTest {

	private DataField df = new DataField(0);
	
	/*
	 * This problem is similar to the Timestamp-Problem. The Date is rounded to the
	 * flat Date.
	 * 
	 * @See equalTimestamp
	 */
	private boolean equalDate(Date d1, Date d2) {
		return d1.toString().equals(d2.toString());
	}

	/*
	 * Through the conversion to BasisDate, the Timestamps to compare can differ,
	 * which causes random fails in the tests. To minimize the chance of failure the
	 * Timestamps are rounded before comparison.
	 * 
	 */
	private boolean equalTimestamp(Timestamp t1, Timestamp t2) {
		t1.setNanos(0);
		t2.setNanos(0);
		t1.setSeconds(0);
		t2.setSeconds(0);
		return t1.toString().equals(t2.toString());
	}

	/**
	 * Tests special cases considering the DataFieldConverter.
	 * 
	 */
	@Test
	public void dataFieldConverterSpecialCasesTest() {

		// Time to time
		try {
			Time t = new Time(new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime());
			df.setValue(t);
			assertEquals(t, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIME));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		// Long to Date
		Long l = (long) 5435435;
		df.setValue(l);
		assertTrue(equalDate(new Date(l), (Date) DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE)));

		// Value is null, the targetType can be any number
		df.setValue(null);
		assertEquals(null, DataFieldConverter.convertType(df.getValue(), 42));

		// Value is a DataField
		df.setValue(5);
		assertThrows(IllegalArgumentException.class, () -> DataFieldConverter.convertType(df, java.sql.Types.INTEGER));
		
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

		// Questionable things happen here
//		// Int to Date
//		assertTrue(equalDate(new Date(com.basis.util.BasisDate.date(5).getTime()),
//				(Date) DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE)));
//
//		// Int to Timestamp
//		assertEquals(new Timestamp(com.basis.util.BasisDate.date(5).getTime()),
//				DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIMESTAMP));

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

		// Questionable things happen here
//		// Double to Date
//		Double d = 4.2;
//		assertTrue(equalDate(new Date(com.basis.util.BasisDate.date(d.intValue()).getTime()),
//				(Date) DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE)));
//
//		// Double to Timestamp
//		assertTrue(equalTimestamp(new Timestamp(com.basis.util.BasisDate.date(d.intValue()).getTime()),
//				(Timestamp) DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIMESTAMP)));

	}

	/**
	 * Tries to convert String into every other supported type.
	 * 
	 */
	@Test
	public void dataFieldConverterStringTest() {

		// String to Int
		df.setValue("");
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.TINYINT));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.SMALLINT));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.INTEGER));
		assertEquals(0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.BIGINT));

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

		// String to Bool
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

		df.setValue("");
		assertEquals(0.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(0.0, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		df.setValue("42.1337");
		assertEquals(42.1337, DataFieldConverter.convertType(df.getValue(), java.sql.Types.REAL));
		assertEquals(42.1337, DataFieldConverter.convertType(df.getValue(), java.sql.Types.DOUBLE));

		// String to String
		df.setValue("Hi");
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("Hi", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));

		df.setValue(GERMAN_SPECIAL_CHARACTERS);
		assertEquals(GERMAN_SPECIAL_CHARACTERS,
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		df.setValue(FRENCH_SPECIAL_CHARACTERS);
		assertEquals(FRENCH_SPECIAL_CHARACTERS,
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		df.setValue(MATHEMATICAL_SPECIAL_CHARACTERS);
		assertEquals(MATHEMATICAL_SPECIAL_CHARACTERS,
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		df.setValue(STANDARD_SPECIAL_CHARACTERS);
		assertEquals(STANDARD_SPECIAL_CHARACTERS,
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));

		// String to Decimal
		df.setValue("");
		assertEquals(new BigDecimal("0"), DataFieldConverter.convertType(df.getValue(), java.sql.Types.DECIMAL));

		df.setValue("5465643.5443");
		assertEquals(new BigDecimal("5465643.5443"),
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.DECIMAL));

		// String to Date
		df.setValue("1999-05-05 23:59:59.999");
		Long l = new Long("925941599999");
		assertEquals(new Date(l), DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE));

		df.setValue("Hi");
		assertThrows(IllegalStateException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.DATE));

		// String to Time
		df.setValue("23:59:59");
		try {
		assertEquals(new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime()),
				DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIME));
		} catch (ParseException e) {
			fail("This will never occur");
		}
		
		df.setValue("Hi");
		assertThrows(IllegalStateException.class,
				() -> DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIME));

		// String to Timestamp
		df.setValue("1999-05-05 23:59:59");
		assertTrue(equalTimestamp(Timestamp.valueOf("1999-05-05 23:59:59"),
				(Timestamp) DataFieldConverter.convertType(df.getValue(), java.sql.Types.TIMESTAMP)));
	} 

	@Test
	public void dataFieldConverterDecimal() {
		df.setValue(new BigDecimal("43432432"));
		
		// Decimal to String
		assertEquals("43432432", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("43432432", DataFieldConverter.convertType(df.getValue(), java.sql.Types.VARCHAR));
		assertEquals("43432432", DataFieldConverter.convertType(df.getValue(), java.sql.Types.LONGVARCHAR));
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

	@Test
	public void fieldToNumberTest() {

		ResultSet rs = mock(ResultSet.class);

		// FieldValue is null
		df.setValue(null);
		assertEquals(Double.valueOf(0.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.VARCHAR));
		assertEquals(Double.valueOf(-1d), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.DATE));
		assertEquals(Double.valueOf(-1d), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.TIMESTAMP));
		assertEquals(Double.valueOf(-1d),
				DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.TIMESTAMP_WITH_TIMEZONE));

		when(rs.isSigned(1)).thenReturn(false);

		// String
		df.setValue("5");
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.VARCHAR));
		df.setValue("");
		assertEquals(Double.valueOf(0.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.VARCHAR));

		// Numbers
		df.setValue(5);
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.INTEGER));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.TINYINT));
		df.setValue(new Short("5"));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.SMALLINT));
		df.setValue(5.0);
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.DOUBLE));
		df.setValue(new Float(5));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.REAL));
		df.setValue(new BigDecimal("5"));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.DECIMAL));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.NUMERIC));
		df.setValue(new BigInteger("5"));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BIGINT));

		// Time
		Time t;
		try {
			t = new Time(new SimpleDateFormat("HH:mm:ss").parse("23:59:59").getTime());
			df.setValue(t);
			assertEquals(Double.valueOf(23.999722222222225),
					DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.TIME));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Boolean
		df.setValue(true);
		assertEquals(Double.valueOf(1.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BIT));
		assertEquals(Double.valueOf(1.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BOOLEAN));
		df.setValue(false);
		assertEquals(Double.valueOf(0.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BIT));
		assertEquals(Double.valueOf(0.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BOOLEAN));

		// Special cases
		when(rs.isSigned(1)).thenReturn(true);
		df.setValue(5);
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.INTEGER));
		assertEquals(null, DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.OTHER));
		df.setValue(new Long("5"));
		assertEquals(Double.valueOf(5.0), DataFieldConverter.fieldToNumber(rs, df, 1, java.sql.Types.BIGINT));
		DataField dfMock = mock(DataField.class);
		when(dfMock.getDate()).thenReturn(null);
		when(dfMock.getValue()).thenReturn(5);
		assertEquals(Double.valueOf(-1.0), DataFieldConverter.fieldToNumber(rs, dfMock, 1, java.sql.Types.TIMESTAMP));

	}
}
