package com.basiscomponents.db.util;

public class DataRowMatcherProvider {
	public static DataRowMatcher createMatcher(String fieldname, String criteria) {
		if (criteria.startsWith("regex:")) {
			return new DataRowRegexMatcher(fieldname, criteria);
		}
		return new SimpleDataRowMatcher(fieldname, criteria);
	}
}
