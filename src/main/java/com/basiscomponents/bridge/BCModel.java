package com.basiscomponents.bridge;

import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.DataRow;

public interface BCModel {
	
	ResultSet retrieve();
	void setFilter(DataRow filter);

}
