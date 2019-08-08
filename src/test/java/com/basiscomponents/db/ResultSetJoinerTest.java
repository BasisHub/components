package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetJoinerTest {

	@Test
	public void mostBasicOneResultSetJoinerTest() throws Exception {
		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", null);
		System.out.println(rs.toJson());
	}

	@Test
	public void notAllFieldsResultSetJoinerTest() throws Exception {
		List<String> myList = new ArrayList<String>();
		myList.add("Ort");
		ResultSet rs = ResultSetJoiner.leftJoin(ResultSetProvider.createLeftResultSetForLeftJoinTesting(),
				ResultSetProvider.createRightResultSetForLeftJoinTesting(), "PLZ", myList);
		System.out.println(rs.toJson());
	}
}
