package com.basiscomponents.db.util;

import com.basiscomponents.db.DataRow;

public class DataRowProvider {
  public static final String STRINGFIELD = "STRINGFIELD";
  public static final String INTFIELD = "INTFIELD";
  public static final String DOUBLEFIELD = "DOUBLEFIELD";
  public static final String BOOLFIELD = "BOOLFIELD";
  public static final String DATEFIELD = "DATEFIELD";
  public static final String TIMESTAMPFIELD = "TIMESTAMPFIELD";

  public static DataRow buildSampleDataRow(boolean f_nullAllFields) throws Exception {
    DataRow dr = new DataRow();
    dr.setFieldValue(STRINGFIELD, "Value");
    dr.setFieldValue(INTFIELD, 1);
    dr.setFieldValue(DOUBLEFIELD, 1.1);
    dr.setFieldValue(BOOLFIELD, true);
    dr.setFieldValue(DATEFIELD, new java.sql.Date(System.currentTimeMillis()));
    dr.setFieldValue(TIMESTAMPFIELD, new java.sql.Timestamp(System.currentTimeMillis()));

    if (f_nullAllFields) {
      dr.setFieldValue(STRINGFIELD, null);
      dr.setFieldValue(INTFIELD, null);
      dr.setFieldValue(DOUBLEFIELD, null);
      dr.setFieldValue(BOOLFIELD, null);
      dr.setFieldValue(DATEFIELD, null);
      dr.setFieldValue(TIMESTAMPFIELD, null);
    }

    dr.setFieldAttribute(STRINGFIELD, "ISNUMERIC", "0");
    dr.setFieldAttribute(INTFIELD, "ISNUMERIC", "1");
    dr.setFieldAttribute(DOUBLEFIELD, "ISNUMERIC", "1");
    dr.setFieldAttribute(BOOLFIELD, "ISNUMERIC", "0");
    dr.setFieldAttribute(DATEFIELD, "ISNUMERIC", "0");
    dr.setFieldAttribute(TIMESTAMPFIELD, "ISNUMERIC", "0");


    dr.setFieldAttribute(STRINGFIELD, "TYPE", "TEXT");
    dr.setFieldAttribute(INTFIELD, "TYPE", "NUMBER");
    dr.setFieldAttribute(DOUBLEFIELD, "TYPE", "NUMBER");
    dr.setFieldAttribute(BOOLFIELD, "TYPE", "TRUTH");
    dr.setFieldAttribute(DATEFIELD, "TYPE", "MOMENT");
    dr.setFieldAttribute(TIMESTAMPFIELD, "TYPE", "MOMENT");

    dr.setFieldAttribute(STRINGFIELD, "ONE", "");
    dr.setFieldAttribute(INTFIELD, "TWO", "");
    dr.setFieldAttribute(DOUBLEFIELD, "TWO", "");
    dr.setFieldAttribute(BOOLFIELD, "THREE", "");
    dr.setFieldAttribute(DATEFIELD, "THREE", "");
    dr.setFieldAttribute(TIMESTAMPFIELD, "THREE", "");

    return dr;
  }
}
