package com.basiscomponents.db.util;

import java.util.regex.Pattern;

import com.basiscomponents.db.DataRow;

public class DataRowRegexMatcher implements DataRowMatcher {
	private static final int STRINGTYPE = 12;
	private String criteria;
	private String fieldName;
	public DataRowRegexMatcher(String fieldname, String criteria) {
		this.fieldName = fieldname;
		this.criteria = criteria;
	}

	@Override
	public boolean matches(DataRow dr) {
		if (dr.getFieldType(fieldName) == STRINGTYPE && criteria.startsWith("regex:")) {
			return Pattern.matches(criteria.substring(6), dr.getField(fieldName).getString());
		}
		return false;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	String getCriteria() {
		return criteria;
	}

}
