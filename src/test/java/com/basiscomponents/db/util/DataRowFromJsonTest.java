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
		json = "{\"name\":\"John\", \"age\": 31, \"city\":\"New York\"}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
//		assertEquals(31, dr.getFieldValue("age"));
		assertEquals("New York", dr.getFieldValue("city"));
	}
	
	/**
	 * A simple conversion of a Json-String to a DataRow with many types.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonManyTypesTest() throws IOException, ParseException {
		json = "{\"truth\": true, \"age\": 31.0, \"city\":\"New York\", \"Number\": 42.0}";
		DataRow dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(true, dr.getFieldValue("truth"));
		assertEquals(31.0, dr.getFieldValue("age"));
		assertEquals("New York", dr.getFieldValue("city"));
		assertEquals(42.0, dr.getFieldValue("Number"));
	}
}
