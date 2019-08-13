package com.basiscomponents.db;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.exception.DataFieldNotFoundException;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetJoinerTest {

	/**
	 * A simple call of the leftJoin method from the ResultSetJoiner. The right
	 * ResultSet is joined into the left ResultSet with all fields after the join
	 * argument "PLZ".
	 * 
	 * @throws Exception
	 */
	@Test
	public void mostBasicOneResultSetJoinerTest() throws Exception {

		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null, null);

		assertTrue(rs.get(0).getFieldNames().size() == 5);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(4).getFieldValue("Ort"));

		assertEquals("Elias", rs.get(0).getFieldValue("Buergermeister"));
		assertEquals("Sascha", rs.get(1).getFieldValue("Buergermeister"));
		assertEquals("Dude", rs.get(2).getFieldValue("Buergermeister"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Buergermeister"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(4).getFieldValue("Buergermeister"));

	}

	/**
	 * A simple call of the leftJoin method from the ResultSetJoiner. The right
	 * ResultSet is joined into the left ResultSet with the fields, which are
	 * specified in the HashMap parameter, after the join argument "PLZ".
	 * 
	 * @throws Exception
	 */
	@Test
	public void notAllFieldsResultSetJoinerTest() throws Exception {

		List<String> myList = new ArrayList<String>();
		myList.add("Ort");
		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ",
				myList, null);
		assertTrue(rs.get(0).getFieldNames().size() == 4);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
	}

	/**
	 * There is one DataRow on the left side of the join which can't find a join
	 * partner. So this DataRow isn't changed.
	 * 
	 * @throws Exception
	 */
	@Test
	public void unperfectJoinResultSetJoinerTest() throws Exception {

		DataRow dr = new DataRow();
		dr.setFieldValue("Name", "Hans");
		dr.setFieldValue("Alter", 45);
		dr.setFieldValue("PLZ", "66654");
		ResultSet left = ResultSetProvider.createLeftResultSetForLeftJoinTesting();
		left.add(dr);
		ResultSet rs = ResultSetJoiner.leftJoin(left,
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null, null);
		assertTrue(rs.get(0).getFieldNames().size() == 5);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Ort"));
		
		assertEquals("Elias", rs.get(0).getFieldValue("Buergermeister"));
		assertEquals("Sascha", rs.get(1).getFieldValue("Buergermeister"));
		assertEquals("Dude", rs.get(2).getFieldValue("Buergermeister"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Buergermeister"));
	}


	/**
	 * One ResultSets is joined into the left sided one. One entry cannot be matched
	 * with the right ResultSet, but the empty fields are written with a test
	 * string.
	 * 
	 * @throws Exception
	 */
	@Test
	public void emptyFieldsResultSetJoinerTest() throws Exception {

		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null, "test");
		assertTrue(rs.get(0).getFieldNames().size() == 5);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertEquals("test", rs.get(3).getFieldValue("Ort"));
		assertEquals("test", rs.get(3).getFieldValue("Buergermeister"));

	}

	/**
	 * One ResultSets is joined into the left sided one. One entry cannot be matched
	 * with the right ResultSet, but the empty fields are written with a test
	 * string.
	 * 
	 * @throws Exception
	 */
	@Test
	public void partlyEmptyFieldsWithDefaultResultSetJoinerTest() throws Exception {

		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null, "test");
		assertTrue(rs.get(0).getFieldNames().size() == 5);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertEquals("test", rs.get(3).getFieldValue("Ort"));
		assertEquals("test", rs.get(3).getFieldValue("Buergermeister"));
		assertEquals("Dillingen", rs.get(4).getFieldValue("Ort"));
		assertEquals("test", rs.get(4).getFieldValue("Buergermeister"));

	}

	/**
	 * One ResultSets is joined into the left sided one. One entry cannot be matched
	 * with the right ResultSet, but the empty fields are written with a test
	 * string.
	 * 
	 * @throws Exception
	 */
	@Test
	public void partlyEmptyFieldsWithoutDefaultResultSetJoinerTest() throws Exception {

		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null, null);
		assertTrue(rs.get(0).getFieldNames().size() == 5);

		assertEquals("Saarbruecken", rs.get(0).getFieldValue("Ort"));
		assertEquals("St. Wendel", rs.get(1).getFieldValue("Ort"));
		assertEquals("Dillingen", rs.get(2).getFieldValue("Ort"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Ort"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Buergermeister"));
		assertEquals("Dillingen", rs.get(4).getFieldValue("Ort"));
		assertThrows(DataFieldNotFoundException.class, () -> rs.get(3).getFieldValue("Buergermeister"));

	}

}
