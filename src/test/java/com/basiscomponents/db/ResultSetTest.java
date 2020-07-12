package com.basiscomponents.db;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;

import static com.basiscomponents.db.util.ResultSetProvider.createDefaultResultSet;
import static org.junit.Assert.assertThrows;
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
		
		assertEquals("<NULL>\000<NULL>-1", rs.get(104).getRowKey());
		
		//try to find an element by the expected RowKey
		assertEquals("Value is 219", rs.get("2 1\0009").getFieldAsString("DATA"));
		
	}
	
	@Test
	public void testRowKeyIndexing() throws Exception {
		ResultSet rs = new ResultSet();
		
		//add one record to a plain vanilla ResultSet
		rs.add(DataRow.fromJson("{\"NAME\":\"Luffy\",\"FIRSTNAME\":\"Monkey D.\",\"Cartoon\":\"One Piece\"}"));

		// now create the Index 
		rs.createIndex();

		//add another record
		rs.add(DataRow.fromJson("{\"NAME\":\"Mouse\",\"FIRSTNAME\":\"Mickey\",\"Cartoon\":\"Mickey Mouse\"}"));

		//now set the key columns to test if the ResultSet is re-creating the row keys of the existing records
		ArrayList<String> keys = new ArrayList<String>();
		keys.add("NAME");		
		rs.setKeyColumns(keys);

		//adding one more record
		rs.add(DataRow.fromJson("{\"NAME\":\"Duck\",\"FIRSTNAME\":\"Donald\",\"Cartoon\":\"Duck Tales\"}"));

		//set the keys again, avoid re-building as it's the same keys again
		rs.setKeyColumns(keys);
		
		rs.add(DataRow.fromJson("{\"NAME\":\"Coyote\",\"FIRSTNAME\":\"Wile E.\",\"Cartoon\":\"Bugs Bunny\"}"));
		
		Assert.assertEquals("Monkey D.",rs.get("Luffy").getFieldAsString("FIRSTNAME"));		
		Assert.assertEquals("Donald",rs.get("Duck").getFieldAsString("FIRSTNAME"));
		Assert.assertEquals("Mickey",rs.get("Mouse").getFieldAsString("FIRSTNAME"));
		Assert.assertEquals("Wile E.",rs.get("Coyote").getFieldAsString("FIRSTNAME"));
		
		
		// now add a duplicate
		//adding one more record
		rs.add(DataRow.fromJson("{\"NAME\":\"Duck\",\"FIRSTNAME\":\"Daisy\",\"Cartoon\":\"Duck Tales\"}"));
		
		//donald should still be indexed as "Duck"
		Assert.assertEquals("Donald",rs.get("Duck").getFieldAsString("FIRSTNAME"));		
		
		//daisy should be counted up
		Assert.assertEquals("Daisy",rs.get("Duck-1").getFieldAsString("FIRSTNAME"));

		//now create a unique index
		keys = new ArrayList<String>();
		keys.add("NAME");
		keys.add("FIRSTNAME");
		rs.setKeyColumns(keys);

		//donald and daisy should now have their proper unique key
		Assert.assertEquals("Donald",rs.get("Duck\0Donald").getFieldAsString("FIRSTNAME"));		
		Assert.assertEquals("Daisy",rs.get("Duck\0Daisy").getFieldAsString("FIRSTNAME"));
		
		//now change a record
		int index_donald = rs.indexOf("Duck\0Donald");
		DataRow rec =rs.get(index_donald).clone();
		
		//change data in the clone and overwrite Donald
		rec.setFieldValue("FIRSTNAME", "Dagobert");
		rs.set(index_donald, rec);
		
		// now Donald should be gone
		assertThrows(Exception.class, () -> {rs.get("Duck\0Donald");});
		
		//but dagobert should be indexed
		Assert.assertEquals("Dagobert",rs.get("Duck\0Dagobert").getFieldAsString("FIRSTNAME"));		
		
		//now the same, but not on a clone
		int index_mickey= rs.indexOf("Mouse\0Mickey");
		rec =rs.get(index_mickey);
		
		rec.setFieldValue("FIRSTNAME","Minnie");
		
		//need to reCreate the index as the DataRow does not communicate to the resultset it's contained in
		rs.reCreateIndex();
		
		//check if minnie can be found now
		Assert.assertEquals("Minnie",rs.get("Mouse\0Minnie").getFieldAsString("FIRSTNAME"));				

		//check if mickey is gone
		assertThrows(Exception.class, () -> {rs.get("Mouse\0Mickey");});	
		
	}

}
