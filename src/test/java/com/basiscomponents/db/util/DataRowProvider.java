package com.basiscomponents.db.util;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;

import com.basiscomponents.db.DataRow;

public class DataRowProvider {
	public static final String STRINGFIELD = "STRINGFIELD";
	public static final String INTFIELD = "INTFIELD";
	public static final String SCD_INTFIELD = "SCD_INTFIELD";
	public static final String DOUBLEFIELD = "DOUBLEFIELD";
	public static final String SCD_DOUBLEFIELD = "SCD_DOUBLEFIELD";
	public static final String FLOATFIELD = "FLOATFIELD";
	public static final String SCD_FLOATFIELD = "SCD_FLOATFIELD";
	public static final String BOOLFIELD = "BOOLFIELD";
	public static final String DATEFIELD = "DATEFIELD";
	public static final String TIMESTAMPFIELD = "TIMESTAMPFIELD";
	public static final String BYTEFIELD = "BYTEFIELD";
	public static final String SCD_BYTEFIELD = "SCD_BYTEFIELD";
	public static final String SHORTFIELD = "SHORTFIELD";
	public static final String SCD_SHORTFIELD = "SCD_SHORTFIELD";
	public static final String LONGFIELD = "LONGFIELD";
	public static final String SCD_LONGFIELD = "SCD_LONGFIELD";
	public static final String BIGDECIMALFIELD = "BIGDECIMALFIELD";
	public static final String SCD_BIGDECIMALFIELD = "SCD_BIGDECIMALFIELD";
	public static final String TIMEFIELD = "TIMEFIELD";

	public static DataRow buildSampleDataRow(boolean nullAllFields) {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(STRINGFIELD, "Value");
			
			dr.setFieldValue(INTFIELD, 1);
			dr.setFieldValue(DOUBLEFIELD, 1.1);
			dr.setFieldValue(FLOATFIELD, 2.1);
			dr.setFieldValue(BYTEFIELD, 2);
			dr.setFieldValue(SHORTFIELD, 3);
			dr.setFieldValue(LONGFIELD, 4);
			dr.setFieldValue(BIGDECIMALFIELD, 5);
			
			dr.setFieldValue(BOOLFIELD, true);
			dr.setFieldValue(DATEFIELD, new Date(System.currentTimeMillis()));
			dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()));
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(System.currentTimeMillis()));

			if (nullAllFields) {
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
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
	public static DataRow buildSampleDataRowMinMax() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(STRINGFIELD, "StringValue");
			
			dr.setFieldValue(INTFIELD, Integer.MAX_VALUE);
			dr.setFieldValue(SCD_INTFIELD, Integer.MIN_VALUE);
			dr.setFieldValue(FLOATFIELD, Float.MAX_VALUE);
			dr.setFieldValue(SCD_FLOATFIELD, Float.MIN_VALUE);
			dr.setFieldValue(DOUBLEFIELD, Double.MAX_VALUE);
			dr.setFieldValue(SCD_DOUBLEFIELD, Double.MIN_VALUE);
			dr.setFieldValue(BYTEFIELD, Byte.MAX_VALUE);
			dr.setFieldValue(SCD_BYTEFIELD, Byte.MIN_VALUE);
			dr.setFieldValue(SHORTFIELD, Short.MAX_VALUE);
			dr.setFieldValue(SCD_SHORTFIELD, Short.MIN_VALUE);
			dr.setFieldValue(LONGFIELD, Long.MAX_VALUE);
			dr.setFieldValue(SCD_LONGFIELD, Long.MIN_VALUE);
			dr.setFieldValue(BIGDECIMALFIELD, Double.MAX_VALUE);
			dr.setFieldValue(SCD_BIGDECIMALFIELD, Double.MIN_VALUE);
			
			dr.setFieldValue(BOOLFIELD, false);
			dr.setFieldValue(DATEFIELD, new Date(System.currentTimeMillis()));
			dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()) );
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(System.currentTimeMillis()));

			dr.setFieldAttribute(STRINGFIELD, "ISDIFFERENT", "0");
			dr.setFieldAttribute(INTFIELD, "ISDIFFERENT", "1");
			dr.setFieldAttribute(DOUBLEFIELD, "ISDIFFERENT", "1");
			dr.setFieldAttribute(BOOLFIELD, "ISDIFFERENT", "0");
			dr.setFieldAttribute(DATEFIELD, "ISDIFFERENT", "0");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ISDIFFERENT", "0");

			dr.setFieldAttribute(STRINGFIELD, "SOMETYPE", "TEXT");
			dr.setFieldAttribute(INTFIELD, "SOMETYPE", "NUMBER");
			dr.setFieldAttribute(DOUBLEFIELD, "SOMETYPE", "NUMBER");
			dr.setFieldAttribute(BOOLFIELD, "SOMETYPE", "TRUTH");
			dr.setFieldAttribute(DATEFIELD, "SOMETYPE", "MOMENT");
			dr.setFieldAttribute(TIMESTAMPFIELD, "SOMETYPE", "MOMENT");

			dr.setFieldAttribute(STRINGFIELD, "ONE", "");
			dr.setFieldAttribute(INTFIELD, "TWO", "");
			dr.setFieldAttribute(DOUBLEFIELD, "TWO", "");
			dr.setFieldAttribute(BOOLFIELD, "THREE", "");
			dr.setFieldAttribute(DATEFIELD, "THREE", "");
			dr.setFieldAttribute(TIMESTAMPFIELD, "THREE", "");
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
}
