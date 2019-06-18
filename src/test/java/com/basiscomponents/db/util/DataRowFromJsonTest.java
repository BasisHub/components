package com.basiscomponents.db.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;

public class DataRowFromJsonTest {

	private String json;

	/**
	 * A simple conversion of a Json-String to a DataRow, by using the full format.
	 * In other test cases this format is shortened by dropping the
	 * "datarow"-statement at the beginning, leaving the resulting Json-string less
	 * complex.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonFullCorrectTest() throws IOException, ParseException {
		json = "{\"datarow\":[{\"name\":\"John\", \"age\": 31, \"city\":\"New York\"}]}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
//		assertEquals(31, dr.getFieldValue("age")); 
		assertEquals("New York", dr.getFieldValue("city"));
	}

	/**
	 * A simple conversion of a Json-String to a DataRow.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonSimpleTest() throws IOException, ParseException {
		json = "{\"name\":\"John\", \"city\":\"New York\"}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
		assertEquals("New York", dr.getFieldValue("city"));
	}
	
	/**
	 * A simple conversion of a Json-String to a DataRow, without MetaData.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonTypesWithoutMetaDataTest() throws IOException, ParseException {
		json = "{\"name\":\"John\", \"age\": 31, \"city\":\"New York\", \"double\": 42.1337, \"truth\": false}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
		assertEquals("New York", dr.getFieldValue("city"));
		assertEquals(false, dr.getFieldValue("truth"));

		// Integer becomes Double
		assertEquals(31.0, dr.getFieldValue("age"));
		assertEquals(42.1337, dr.getFieldValue("double"));
	}

	/**
	 * A conversion of a Json-String to a nested DataRow. A version with and without
	 * MetaData is tested.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonNestedTest() throws IOException, ParseException {
		json = "{\"name\":\"John\", \"city\":\"New York\", \"datarow\":{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
		assertEquals("New York", dr.getFieldValue("city"));

		// Checking the nested DataRow
		DataRow drNested = (DataRow) dr.getFieldValue("datarow");
		assertEquals("John", drNested.getFieldValue("name"));
		assertEquals(42.1337, drNested.getFieldValue("double"));
		assertEquals(false, drNested.getFieldValue("truth"));
	}

	/**
	 * A conversion of a Json-String to a DataRow with many types.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonManyTypesMetaTest() throws IOException, ParseException {
		DataRow meta = new DataRow();
//		meta.setFieldValue("", value);
		json = "{\"truth\": true, \"age\": 31, \"city\":\"New York\", \"Number\": 42.0, \"meta\":{\"truth\":{\"ColumnType\":\""
				+ java.sql.Types.BOOLEAN + "\"},\"age\":{\"ColumnType\":\"" + java.sql.Types.INTEGER + "\"}}}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(true, dr.getFieldValue("truth"));
		assertEquals(31, dr.getFieldValue("age"));
		assertEquals("New York", dr.getFieldValue("city"));
		assertEquals(42.0, dr.getFieldValue("Number"));
//		Long l = new Long("925941599999");
//		assertEquals(new Date(l), dr.getFieldValue("date"));
	}
}
