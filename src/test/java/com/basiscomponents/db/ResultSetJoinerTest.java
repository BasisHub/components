package com.basiscomponents.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetJoinerTest {

	@Test
	public void mostBasicOneResultSetJoinerTest() throws Exception {
		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null);
		assertTrue(rs.get(0).getFieldNames().size() == 5);
		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertEquals("Elias", rs.get(0).getFieldValue("Buergermeister"));
		assertEquals("Sascha", rs.get(1).getFieldValue("Buergermeister"));
		assertEquals("Dude", rs.get(2).getFieldValue("Buergermeister"));
	}

	@Test
	public void notAllFieldsResultSetJoinerTest() throws Exception {
		List<String> myList = new ArrayList<String>();
		myList.add("Ort");
		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", myList);
		assertTrue(rs.get(0).getFieldNames().size() == 4);
		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
	}
}
