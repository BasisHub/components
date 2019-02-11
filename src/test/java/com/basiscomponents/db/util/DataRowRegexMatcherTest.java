package com.basiscomponents.db.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;


public class DataRowRegexMatcherTest {

	@Test
	public void test() {
		DataRow dr = new DataRow();
		dr.addDataField("FIELD", new DataField("Hello"));
		DataRowRegexMatcher matcher = new DataRowRegexMatcher("FIELD", "regex:[Helo]\\w*");
		assertTrue(matcher.matches(dr));
	}

}
