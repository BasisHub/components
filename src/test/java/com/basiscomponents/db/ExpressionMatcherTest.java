package com.basiscomponents.db;


import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExpressionMatcherTest {

	@Test
	public void testGeneratePreparedWhereClause() throws Exception {
		String wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "!123");
		assertEquals("LICENSE != ?", wh);

		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "<>123");
		assertEquals("LICENSE != ?", wh);

		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "<123");
		assertEquals("LICENSE < ?", wh);

		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", ">123");
		assertEquals("LICENSE > ?", wh);

	}

	@Test
	public void testGetPreparedWhereClauseValues() throws Exception {
		DataRow preparedWhereClauseValues = ExpressionMatcher.getPreparedWhereClauseValues("<123", Types.VARCHAR);
		assertEquals("123", preparedWhereClauseValues.getDataField("VALUE1").getString());
		preparedWhereClauseValues = ExpressionMatcher.getPreparedWhereClauseValues("<>123", Types.VARCHAR);
		assertEquals("123", preparedWhereClauseValues.getDataField("VALUE1").getString());
		preparedWhereClauseValues = ExpressionMatcher.getPreparedWhereClauseValues("!123", Types.VARCHAR);
		assertEquals("123", preparedWhereClauseValues.getDataField("VALUE1").getString());
		preparedWhereClauseValues = ExpressionMatcher.getPreparedWhereClauseValues(">123", Types.VARCHAR);
		assertEquals("123", preparedWhereClauseValues.getDataField("VALUE1").getString());

	}

}
