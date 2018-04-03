package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public class SimpleDataRowMatcher implements DataRowMatcher {

	private Object criteria;
	private String fieldName;
	SimpleDataRowMatcher(final String fieldname, final Object criteria) {
		this.fieldName = fieldname;
		this.criteria = criteria;
	}

	@Override
	public boolean matches(DataRow dr) {
		return dr.getField(fieldName, true).getString().equals(criteria);
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

}
