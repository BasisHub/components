package com.basiscomponents.db;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static com.basiscomponents.db.util.ResultSetProvider.createDefaultResultSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResultSetTest {


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
		ResultSet rs = createDefaultResultSet(false);
		rs.get(0).setAttribute("BGCOLOR","Black");
		HashMap<String, String> attributes = rs.get(0).getAttributes();
		assertTrue(attributes.containsKey("BGCOLOR"));
	}
	
	@Test
	public void testRowKey() throws Exception {
		
		ResultSet rs = new ResultSet();
		
		
		for (int i=100; i<300; i++) {
			DataRow dr = new DataRow();
			String el = Integer.toString(i);

			if (i != 201 && i!=203 && i!=204)
				dr.setFieldValue("K1",el.substring(0, 1)+" "+el.substring(1,2));
			
			if (i != 202 && i!=203 && i!=204)
				dr.setFieldValue("K2", Integer.valueOf(el.substring(2,3)));
			
			dr.setFieldValue("DATA", "Value is "+el);
			rs.add(dr);
		}
		
		ArrayList<String> keys=new ArrayList<>();
		keys.add("K1");
		keys.add("K2");
		rs.setKeyColumns(keys);
		rs.createIndex();


		// check if row key is built as expected
		assertEquals("1 0\0002", rs.get(2).getRowKey());

		assertEquals("<NULL>\0001", rs.get(101).getRowKey());
		
		assertEquals("2 0\000<NULL>", rs.get(102).getRowKey());
		
		assertEquals("<NULL>\000<NULL>", rs.get(103).getRowKey());
		
		assertEquals("<NULL>\000<NULL>-104", rs.get(104).getRowKey());
		
		//try to find an element by the expected RowKey
		assertEquals("Value is 219", rs.get("2 1\0009").getFieldAsString("DATA"));
		
	}

}
