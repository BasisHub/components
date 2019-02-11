package com.basiscomponents.db;

import static org.junit.Assert.assertEquals;

import java.sql.Types;

import org.junit.Test;

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
