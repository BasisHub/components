package com.basiscomponents.db;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.DataFieldConverter;

public class DataFieldConverterTests {

	private DataField df;

	@Test
	public void DataFieldConverterIntegerTest() {
		df = new DataField(5);

		DataFieldConverter.convertType(df, java.sql.Types.INTEGER);
		assertEquals(5, df.getValue());
		DataFieldConverter.convertType(df, java.sql.Types.TINYINT);
		assertEquals(5, df.getValue());
		DataFieldConverter.convertType(df, java.sql.Types.SMALLINT);
		assertEquals(5, df.getValue());
		DataFieldConverter.convertType(df, java.sql.Types.BIGINT);
		assertEquals(5, df.getValue());

		df = new DataField(1);
		DataFieldConverter.convertType(df, java.sql.Types.BOOLEAN);
		assertEquals(true, DataFieldConverter.convertType(df, java.sql.Types.BOOLEAN));

	}

	@Test
	public void DataFieldConverterBooleanTest() {
		DataField df = new DataField(true);

		DataFieldConverter.convertType(df, java.sql.Types.BOOLEAN);

		assertEquals(true, df.getValue());
	}

	@Test
	public void DataFieldConverterDoubleTest() {
		DataField df = new DataField(5.2);

		DataFieldConverter.convertType(df, java.sql.Types.DOUBLE);

		assertEquals(5.2, df.getValue());
	}

	@Test
	public void DataFieldConverterStringTest() {
		DataField df = new DataField("Hi");

		DataFieldConverter.convertType(df, java.sql.Types.VARCHAR);

		assertEquals("Hi", df.getValue());
	}

	@Test
	public void DataFieldConverterDateTest() {
		Date date = new Date(System.currentTimeMillis());
		DataField df = new DataField(date);

		DataFieldConverter.convertType(df, java.sql.Types.DATE);

		assertEquals(date, df.getValue());
	}
}
