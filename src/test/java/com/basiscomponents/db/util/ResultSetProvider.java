package com.basiscomponents.db.util;

import com.basiscomponents.db.ResultSet;

public class ResultSetProvider {

  public static ResultSet createDefaultResultSet() throws Exception {
    ResultSet result = new ResultSet();
    result.add(DataRowProvider.buildSampleDataRow(false));
    return result;
  }

}
