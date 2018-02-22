package com.basiscomponents.db;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ResultSetTest {
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void testFromJson() throws Exception {
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

}
