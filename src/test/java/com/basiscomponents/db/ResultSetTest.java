package com.basiscomponents.db;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;

import static com.basiscomponents.db.util.ResultSetProvider.createDefaultResultSet;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
	@Test
	public void testOmittedAttributes() throws Exception {
		ResultSet rs = createDefaultResultSet();
		rs.get(0).setAttribute("BGCOLOR","Black");
		HashMap<String, String> attributes = rs.get(0).getAttributes();
		assertTrue(attributes.containsKey("BGCOLOR"));
		System.out.println(rs.toJson());
	}

}
