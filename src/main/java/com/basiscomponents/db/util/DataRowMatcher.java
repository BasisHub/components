package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public interface DataRowMatcher {
	public String getFieldName();
	public abstract boolean matches(DataRow dr);
}
