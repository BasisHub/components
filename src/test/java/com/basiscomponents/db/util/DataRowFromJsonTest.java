package com.basiscomponents.db.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.constantsForTesting.SpecialCharacterConstants;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

public class DataRowFromJsonTest {

	private String json;
	private DataRow dr;
	private DataRow dr1;
	private StringBuilder sb;

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
	 * Violating the format to cause the right exceptions.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonFromatViolationTest() throws IOException, ParseException {

		// Violating the format should result in a JsonParseException
		assertThrows(JsonParseException.class, () -> DataRowFromJsonProvider.fromJson("Hi", new DataRow()));

		// Missing { brackets
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson("{\"name\":\"John\", \"city\":\"New York\"", new DataRow()));
		assertThrows(MismatchedInputException.class,
				() -> DataRowFromJsonProvider.fromJson("\"name\":\"John\", \"city\":\"New York\"}", new DataRow()));
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson(
						"{\"age\": 31, \"meta\":{\"age\":{\"ColumnType\":\"4\", \"dataBase\":\"myDataBase\"}}",
						new DataRow()));

		// Missing meta annotation
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson(
						"{\"age\": 31, :{\"age\":{\"ColumnType\":\"4\", \"dataBase\":\"myDataBase\"}}}",
						new DataRow()));

		// Missing : colon
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson(
						"{\"age\": 31, meta{\"age\":{\"ColumnType\":\"4\", \"dataBase\":\"myDataBase\"}}}",
						new DataRow()));

		// Missing " quote
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson(
						"{\"age\": 31, meta:{\"age:{\"ColumnType\":\"4\", \"dataBase\":\"myDataBase\"}}}",
						new DataRow()));

		// Missing data
		assertThrows(JsonParseException.class,
				() -> DataRowFromJsonProvider.fromJson(
						"{\"age\": , meta:{\"age\":{\"ColumnType\":\"4\", \"dataBase\":\"myDataBase\"}}}",
						new DataRow()));
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

		// Empty String as an Integer
		sb = new StringBuilder("");
		sb.append("{\"int\": \"\", \"meta\":{\"int\":{\"ColumnType\":\"" + java.sql.Types.INTEGER + "\"}}}");
		json = sb.toString();
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(0, dr1.getFieldValue("int"));

		// Nulling a DataField
		sb = new StringBuilder("");
		sb.append("{\"int\": null, \"meta\":{\"int\":{\"ColumnType\":\"" + java.sql.Types.INTEGER + "\"}}}");
		json = sb.toString();
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(null, dr1.getFieldValue("int"));

		// Convert characters below chr(32)
		char c = 27;
		String s = "" + c;
		json = "{\"char\":\"" + c + "\"}";
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals(s, dr.getFieldValue("char"));
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
	 * A conversion of a Json-String with a nested DataRow. A version with and
	 * without MetaData is tested.
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
		sb = new StringBuilder("");
		sb.append("{\"name\":\"John\", \"datarow\":{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}, \"meta\":{\"name\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR);
		sb.append("\"}, \"datarow\":{\"ColumnType\":\"-974\"}}}");
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr1.getFieldValue("name"));

		// Checking the nested DataRow
		DataRow drNested1 = (DataRow) dr1.getFieldValue("datarow");
		assertEquals("John", drNested1.getFieldValue("name"));
		assertEquals(42.1337, drNested1.getFieldValue("double"));
		assertEquals(false, drNested1.getFieldValue("truth"));
	}

	/**
	 * A conversion of a Json-String with a nested ResultSet. A version with and
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
		sb = new StringBuilder("");
		sb.append("{\"name\":\"John\", \"resultset\":[{\"name\":\"John\", \"double\": 42.1337, \"truth\": false}], \"meta\":{\"name\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR);
		sb.append("\"}, \"resultset\":{\"ColumnType\":\"-975\"}}}");
		json = sb.toString();
		dr1 = DataRowFromJsonProvider.fromJson(json, new DataRow());
		assertEquals("John", dr1.getFieldValue("name"));

		// Checking the nested ResultSet
		ResultSet rsNested1 = (ResultSet) dr1.getFieldValue("resultset");
		assertEquals("John", rsNested1.get(0).getFieldValue("name"));
		assertEquals(42.1337, rsNested1.get(0).getFieldValue("double"));
		assertEquals(false, rsNested1.get(0).getFieldValue("truth"));
	}

	/**
	 * A conversion of a Json-String to a DataRow with many special characters.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonSpecialCharactersTest() throws IOException, ParseException {
		
		sb = new StringBuilder("");
		sb.append("{\"name1\":\"");
		sb.append(SpecialCharacterConstants.FRENCH_SPECIAL_CHARACTERS);
		sb.append("\", \"name2\":\"");
		sb.append(SpecialCharacterConstants.GERMAN_SPECIAL_CHARACTERS);
		sb.append("\", \"name3\":\"");
		sb.append(SpecialCharacterConstants.MATHEMATICAL_SPECIAL_CHARACTERS);
		sb.append("\", \"name4\":\"");
		sb.append(SpecialCharacterConstants.STANDARD_SPECIAL_CHARACTERS);
		sb.append("\", \"meta\":{\"name1\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR);
		sb.append("\"}, \"name2\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR);
		sb.append("\"}, \"name3\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR);
		sb.append("\"}, \"name4\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.VARCHAR + "\"}}}");
		json = sb.toString();
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());

		assertEquals(SpecialCharacterConstants.FRENCH_SPECIAL_CHARACTERS, dr.getFieldValue("name1"));
		assertEquals(SpecialCharacterConstants.GERMAN_SPECIAL_CHARACTERS, dr.getFieldValue("name2"));
		assertEquals(SpecialCharacterConstants.MATHEMATICAL_SPECIAL_CHARACTERS, dr.getFieldValue("name3"));
		assertEquals(SpecialCharacterConstants.STANDARD_SPECIAL_CHARACTERS, dr.getFieldValue("name4"));
	}

	/**
	 * A conversion of a Json-String to a DataRow with many types.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@Test
	public void dataRowFromJsonManyTypesMetaTest() throws IOException, ParseException {

		sb = new StringBuilder("");
		sb.append(
				"{\"truth\": true, \"age\": 31, \"Decimal\": 23456.434, \"city\":\"New York\", \"Number\": 42.0, \"date\":\"1999-05-05 23:59:59.999\", \"timestamp1\":\"1999-05-05T23:59:59.999\", \"timestamp2\":\"1999-05-05\",");
		sb.append(" \"meta\":{\"truth\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.BOOLEAN);
		sb.append("\"},\"age\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.INTEGER);
		sb.append("\"}, \"Decimal\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.NUMERIC);
		sb.append("\"}, \"timestamp1\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.TIMESTAMP);
		sb.append("\"}, \"timestamp2\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.TIMESTAMP);
		sb.append("\"}, \"date\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.DATE + "\"}}}");
		json = sb.toString();
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());

		assertEquals(true, dr.getFieldValue("truth"));
		assertEquals(31, dr.getFieldValue("age"));
		assertEquals("New York", dr.getFieldValue("city"));
		assertEquals(42.0, dr.getFieldValue("Number"));
		assertEquals(new BigDecimal("23456.434"), dr.getFieldValue("Decimal"));

		// Date and Time
		Long l = new Long("925941599999");
		assertEquals(new Date(l), dr.getFieldValue("date"));
		assertEquals(new Timestamp(l), dr.getFieldValue("timestamp1"));
		l = new Long("925855200000");
		assertEquals(new Timestamp(l), dr.getFieldValue("timestamp2"));
	}

	/**
	 * A conversion of a Json-String to a DataRow. The age DataField has more than
	 * one attribute.
	 * 
	 * @throws Exception
	 */
	@Test
	public void dataRowFromJsonAttributesTest() throws Exception {

		sb = new StringBuilder("");
		sb.append("{\"age\": 31, \"meta\":{\"age\":{\"ColumnType\":\"");
		sb.append(java.sql.Types.INTEGER + "\", \"dataBase\":\"myDataBase\"}}}");
		json = sb.toString();
		dr = DataRowFromJsonProvider.fromJson(json, new DataRow());

		String s = String.format("%d", java.sql.Types.INTEGER);
		assertEquals(s, dr.getFieldAttribute("age", "ColumnType"));
		assertEquals("myDataBase", dr.getFieldAttribute("age", "dataBase"));
	}
}
