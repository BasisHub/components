package com.basiscomponents.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
	public void wrongMetaDataFieldFromJsonTest() throws Exception {
		// The ANGUAGE in meta lacks a L, exception?
		String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		ResultSet rs = ResultSet.fromJson(response);
	}
	
	@Test
	public void wrongFormatFromJsonTest() throws Exception {
		// \" is missing after LANGUAGE
		String response = "[{\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		try {
		ResultSet rs = ResultSet.fromJson(response);
		assertTrue(false);
		} catch (JsonSyntaxException e) {
			// Caught correct exception
		}
	}
	
	@Test
	public void wrongFormatFromJsonTest2() throws Exception {
		// { is missing as second character
		String response = "[\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		try {
		ResultSet rs = ResultSet.fromJson(response);
		assertTrue(false);
		} catch (JsonSyntaxException e) {
			// Caught correct exception
		}
	}
	
	@Test
	public void wrongFormatFromJsonTest3() throws Exception {
		// : is missing after LANGUAGE
		String response = "[{\"LANGUAGE\"\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
		try {
		ResultSet rs = ResultSet.fromJson(response);
		assertTrue(false);
		} catch (JsonSyntaxException e) {
			// Caught correct exception
		}
	}
	
}
