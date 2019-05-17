package com.basiscomponents.db;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.basiscomponents.db.exception.DataFieldNotFoundException;
import com.google.gson.JsonSyntaxException;

public class ResultSetFromJsonTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void simpleFromJsonTest() throws Exception {
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		byte[] buffer = new byte[1024];

		for (int i = 0; i < response.length(); i++) {
			buffer[i] = (byte) response.charAt(i);
		}
		String str = new String(buffer);
		ResultSet rs = ResultSet.fromJson(str);
		assertNotNull(rs);
		assertTrue(rs.getColumnNames().contains("ISO639-1"));
		assertTrue(rs.getColumnNames().contains("LANGUAGE"));
	}

	@Test
	public void wrongMetaDataCorrectionFromJsonTest() throws Exception {
		// The ANGUAGE in MetaData is corrected to LANGUAGE
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
		String s = rs.toJson();
		System.out.println(s);
		DataRow dr = rs.get(0);
		thrown.expect(DataFieldNotFoundException.class);
		dr.getField("ANGUAGE");
	}
	
	@Test
	public void wrongMetaDataCorrectionFromJsonTest2() throws Exception {
		// The E in MetaData is corrected to LANGUAGE
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"E\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
		String s = rs.toJson();
		System.out.println(s);
		DataRow dr = rs.get(0);
		thrown.expect(DataFieldNotFoundException.class);
		dr.getField("E");
	}
	
	@Test
	public void wrongMetaDataCorrectionFromJsonTest3() throws Exception {
		// The "" in MetaData is corrected to LANGUAGE
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
		String s = rs.toJson();
		System.out.println(s);
		DataRow dr = rs.get(0);
		thrown.expect(DataFieldNotFoundException.class);
		dr.getField("");
	}
	
	@Test
	public void wrongMetaDataCorrectionFromJsonTest4() throws Exception {
		// The DataField get their attributes by order
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
		String s = rs.toJson();
		assertEquals(s, "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]");
	}
	
	@Test
	public void wrongMetaDataCorrectionFromJsonTest5() throws Exception {
		// The JSonMapper will drop the last attribute field
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
		String s = rs.toJson();
		assertEquals(s, "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]");
	}
	
	@Test
	public void wrongFormatFromJsonTest() throws Exception {
		// \" is missing after LANGUAGE
		String response = "[{\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		thrown.expect(JsonSyntaxException.class);
		ResultSet rs = ResultSet.fromJson(response);
	}
	
	@Test
	public void wrongFormatFromJsonTest2() throws Exception {
		// { is missing as second character
		String response = "[\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		thrown.expect(JsonSyntaxException.class);
		ResultSet rs = ResultSet.fromJson(response);
	}
	
	@Test
	public void wrongFormatFromJsonTest3() throws Exception {
		// : is missing after LANGUAGE
		String response = "[{\"LANGUAGE\"\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		thrown.expect(JsonSyntaxException.class);
		ResultSet rs = ResultSet.fromJson(response);
	}
	
	@Test
	public void metaDataMissingFromJsonTest() throws Exception {
		// MetaData missing
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\"}]";
		try {
		ResultSet rs = ResultSet.fromJson(response);
		assertTrue(false);
		} catch (JsonSyntaxException e) {
			// Caught correct exception
		}
	}
	
}
