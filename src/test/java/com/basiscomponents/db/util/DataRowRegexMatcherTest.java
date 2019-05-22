package com.basiscomponents.db.util;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DataRowRegexMatcherTest {

	@Test
	public void test() {
		DataRow dr = new DataRow();
		dr.addDataField("FIELD", new DataField("Hello"));
		DataRowRegexMatcher matcher = new DataRowRegexMatcher("FIELD", "regex:[Helo]\\w*");
		assertTrue(matcher.matches(dr));
	}

}
