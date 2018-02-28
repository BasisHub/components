package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public class DataRowRegexMatcher extends DataRowMatcher {
	private String criteria;

	public DataRowRegexMatcher(String fieldname, String criteria) {
		super(fieldname);
		this.criteria = criteria;
	}

	@Override
	public boolean matches(DataRow dr) {
		// TODO Auto-generated method stub
		return false;
	}

}
