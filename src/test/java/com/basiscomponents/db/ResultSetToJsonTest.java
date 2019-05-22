package com.basiscomponents.db;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetToJsonTest {

	/**
	 * A StringOnlyResultSet is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonStringOnlyResultSetTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createStringOnlyResultSet();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"STRINGFIELD\":\"1337\",\"SCD_STRINGFIELD\":\"StringValue\",\"TRD_STRINGFIELD\":\"4StringValue2\",\"FRT_STRINGFIELD\":\"\",\"FTH_STRINGFIELD\":null,\"STH_STRINGFIELD\":\"-1\",\"SVT_STRINGFIELD\":\"1.0\",\"meta\":{\"STRINGFIELD\":{\"ColumnType\":\"12\"},\"SCD_STRINGFIELD\":{\"ColumnType\":\"12\"},\"TRD_STRINGFIELD\":{\"ColumnType\":\"12\"},\"FRT_STRINGFIELD\":{\"ColumnType\":\"12\"},\"FTH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"STH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"SVT_STRINGFIELD\":{\"ColumnType\":\"12\"}}}]",
				s);
	}
}
