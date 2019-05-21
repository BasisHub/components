package com.basiscomponents.db;

import static org.junit.Assert.assertFalse;

import org.junit.Test;

import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetToJsonTest {

	/**
	 * A default ResultSet is created 
	 * The equality method will check them to contain the same values (with getFieldAsString) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldAsStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		System.out.println(s);
		
		assertFalse(s.isEmpty());
	}
}
