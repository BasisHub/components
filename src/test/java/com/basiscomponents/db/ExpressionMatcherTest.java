package com.basiscomponents.db;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExpressionMatcherTest {

	@Test
	public void testGeneratePreparedWhereClause() throws Exception {
		String wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "!123");
		assertEquals("LICENSE != ?", wh);
		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "<>123");
		// assertEquals("LICENSE != ?", wh); FIXME this still needs to be fixed

		System.out.println(wh);
		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", "<123");
		assertEquals("LICENSE < ?", wh);
		System.out.println(wh);
		wh = ExpressionMatcher.generatePreparedWhereClause("LICENSE", ">123");
		assertEquals("LICENSE > ?", wh);

	}

}
