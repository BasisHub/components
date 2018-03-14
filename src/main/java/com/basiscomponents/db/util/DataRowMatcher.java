package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public abstract class DataRowMatcher {
	String fieldName;

	public String getFieldName() {
		return fieldName;
	}

	DataRowMatcher(String fieldname) {
		this.fieldName = fieldname;
	}
	public abstract boolean matches(DataRow dr);
}
