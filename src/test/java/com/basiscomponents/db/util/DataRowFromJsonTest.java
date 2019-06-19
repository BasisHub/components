package com.basiscomponents.db.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.fasterxml.jackson.core.JsonParseException;

public class DataRowFromJsonTest {

	private String json;
	private DataRow dr;
	private DataRow dr1;

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
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
		assertEquals("New York", dr.getFieldValue("city"));
	}

	/**
	 * Checking some special cases considering the conversion from Json to DataRow.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonSpecialCasesTest() throws IOException, ParseException {

		// An empty String results in a empty DataRow
		dr = DataRowFromJsonProvider.fromJson("", new DataRow());
		assertTrue(dr.isEmpty());

		// An empty Json String
		dr1 = DataRowFromJsonProvider.fromJson("{}", new DataRow());
		assertTrue(dr1.isEmpty());

		// Violating the format should result in a JsonParseException
		assertThrows(JsonParseException.class, () -> DataRowFromJsonProvider.fromJson("Hi", new DataRow()));
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
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
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
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
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
	public void dataRowFromJsonNestedDataRowTest() throws IOException, ParseException {

		// Without MetaData
		json = "{\"name\":\"John\", \"city\":\"New York\", \"datarow\":{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}}";
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));
		assertEquals("New York", dr.getFieldValue("city"));

		// Checking the nested DataRow
		DataRow drNested = (DataRow) dr.getFieldValue("datarow");
		assertEquals("John", drNested.getFieldValue("name"));
		assertEquals(42.1337, drNested.getFieldValue("double"));
		assertEquals(false, drNested.getFieldValue("truth"));

		// With MetaData
		json = "{\"name\":\"John\", \"datarow\":{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}, \"meta\":{\"name\":{\"ColumnType\":\""
				+ java.sql.Types.VARCHAR + "\"}, \"datarow\":{\"ColumnType\":\"-974\"}}}";
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr1.getFieldValue("name"));

		// Checking the nested DataRow
		DataRow drNested1 = (DataRow) dr1.getFieldValue("datarow");
		assertEquals("John", drNested1.getFieldValue("name"));
		assertEquals(42.1337, drNested1.getFieldValue("double"));
		assertEquals(false, drNested1.getFieldValue("truth"));
	}

	/**
	 * A conversion of a Json-String to a nested ResultSet. A version with and
	 * without MetaData is tested.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonNestedResultSetTest() throws IOException, ParseException {

		// Without MetaData
		json = "{\"name\":\"John\", \"resultset\":[{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}]}";
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr.getFieldValue("name"));

		// Checking the nested ResultSet
		ResultSet rsNested = (ResultSet) dr.getFieldValue("resultset");
		assertEquals("John", rsNested.get(0).getFieldValue("name"));
		assertEquals(42.1337, rsNested.get(0).getFieldValue("double"));
		assertEquals(false, rsNested.get(0).getFieldValue("truth"));

		// With MetaData
		json = "{\"name\":\"John\", \"resultset\":[{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}], \"meta\":{\"name\":{\"ColumnType\":\""
				+ java.sql.Types.VARCHAR + "\"}, \"resultset\":{\"ColumnType\":\"-975\"}}}";
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr1.getFieldValue("name"));

		// Checking the nested ResultSet
		ResultSet rsNested1 = (ResultSet) dr1.getFieldValue("resultset");
		assertEquals("John", rsNested1.get(0).getFieldValue("name"));
		assertEquals(42.1337, rsNested1.get(0).getFieldValue("double"));
		assertEquals(false, rsNested1.get(0).getFieldValue("truth"));
	}

	/**
	 * A conversion of a Json-String to a DataRow with many types.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonManyTypesMetaTest() throws IOException, ParseException {
//		meta.setFieldValue("", value);
		json = "{\"truth\": true, \"age\": 31, \"Decimal\": 23456.434, \"city\":\"New York\", \"Number\": 42.0, \"meta\":{\"truth\":{\"ColumnType\":\""
				+ java.sql.Types.BOOLEAN + "\"},\"age\":{\"ColumnType\":\"" + java.sql.Types.INTEGER
				+ "\"}, \"Decimal\":{\"ColumnType\":\"" + java.sql.Types.NUMERIC + "\"}}}";
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(true, dr.getFieldValue("truth"));
		assertEquals(31, dr.getFieldValue("age"));
		assertEquals("New York", dr.getFieldValue("city"));
		assertEquals(42.0, dr.getFieldValue("Number"));
		assertEquals(new BigDecimal("23456.434"), dr.getFieldValue("Decimal"));

		// Date
//		Long l = new Long("925941599999");
//		assertEquals(new Date(l), dr.getFieldValue("date"));
	}
}
