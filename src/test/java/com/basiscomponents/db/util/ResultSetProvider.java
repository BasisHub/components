package com.basiscomponents.db.util;

import com.basiscomponents.db.ResultSet;

public class ResultSetProvider {

  public static ResultSet createDefaultResultSet() throws Exception {
    ResultSet result = new ResultSet();
    result.add(DataRowProvider.buildSampleDataRow(false));
    return result;
  }
  
  public static ResultSet createMultipleDataRowResultSet() throws Exception {
	    ResultSet result = new ResultSet();
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    result.add(DataRowProvider.buildSampleDataRow(false));
	    return result;
	  }

  public static ResultSet createDefaultResultSetMinMax() throws Exception {
	    ResultSet result = new ResultSet();
	    result.add(DataRowProvider.buildSampleDataRowMinMax());
	    return result;
	  }
  
}
