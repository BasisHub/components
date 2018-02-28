package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public class SimpleDataRowMatcher extends DataRowMatcher {

	private String criteria;

	SimpleDataRowMatcher(final String fieldname, final String criteria) {
		super(fieldname);
		this.criteria = criteria;
	}

	@Override
	public boolean matches(DataRow dr) {
		return dr.getField(fieldName, true).equals(criteria);
	}

}
