package com.basiscomponents.db.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Calendar;

import org.apache.commons.lang3.time.DateUtils;

import com.basiscomponents.db.DataRow;

public class DataRowProvider {
	public static final String STRINGFIELD = "STRINGFIELD";
	public static final String SCD_STRINGFIELD = "SCD_STRINGFIELD";
	public static final String TRD_STRINGFIELD = "TRD_STRINGFIELD";
	public static final String FRT_STRINGFIELD = "FRT_STRINGFIELD";
	public static final String FTH_STRINGFIELD = "FTH_STRINGFIELD";
	public static final String INTFIELD = "INTFIELD";
	public static final String SCD_INTFIELD = "SCD_INTFIELD";
	public static final String DOUBLEFIELD = "DOUBLEFIELD";
	public static final String SCD_DOUBLEFIELD = "SCD_DOUBLEFIELD";
	public static final String BOOLFIELD = "BOOLFIELD";
	public static final String DATEFIELD = "DATEFIELD";
	public static final String TIMESTAMPFIELD = "TIMESTAMPFIELD";
//	public static final String BYTEFIELD = "BYTEFIELD";
//	public static final String SCD_BYTEFIELD = "SCD_BYTEFIELD";
//	public static final String SHORTFIELD = "SHORTFIELD";
//	public static final String SCD_SHORTFIELD = "SCD_SHORTFIELD";
//	public static final String LONGFIELD = "LONGFIELD";
//	public static final String SCD_LONGFIELD = "SCD_LONGFIELD";
//	public static final String BIGDECIMALFIELD = "BIGDECIMALFIELD";
//	public static final String SCD_BIGDECIMALFIELD = "SCD_BIGDECIMALFIELD";
	public static final String TIMEFIELD = "TIMEFIELD";

	public static DataRow buildSampleDataRow(boolean nullAllFields) {
		DataRow dr = new DataRow();
		try {
			int myInt = 1;
			dr.setFieldValue(INTFIELD, myInt);
			Double myDouble = 1.0;
			dr.setFieldValue(DOUBLEFIELD, myDouble);
			
			// The date has to be rounded to the first milliseconds of the day
			// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(System.currentTimeMillis()));
			cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
			dr.setFieldValue(DATEFIELD, new Date(cal.getTimeInMillis()));
			
			dr.setFieldValue(BOOLFIELD, true);
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(System.currentTimeMillis()));
			
//			dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()));
//			There are types which can't be converted yet, see ResultSetJsonMapper Line 259

			if (nullAllFields) {
				dr.setFieldValue(INTFIELD, null);
//				dr.setFieldValue(BYTEFIELD, null);
//				dr.setFieldValue(SHORTFIELD, null);
//				dr.setFieldValue(LONGFIELD, null);
//				dr.setFieldValue(BIGDECIMALFIELD, null);
				dr.setFieldValue(DOUBLEFIELD, null);
				dr.setFieldValue(BOOLFIELD, null);
				dr.setFieldValue(DATEFIELD, null);
				dr.setFieldValue(TIMEFIELD, null);
				dr.setFieldValue(TIMESTAMPFIELD, null);
			}

			dr.setFieldAttribute(INTFIELD, "ISNUMERIC", "1");
			dr.setFieldAttribute(DOUBLEFIELD, "ISNUMERIC", "1");
			dr.setFieldAttribute(BOOLFIELD, "ISNUMERIC", "0");
			dr.setFieldAttribute(DATEFIELD, "ISNUMERIC", "0");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ISNUMERIC", "0");

			dr.setFieldAttribute(INTFIELD, "TYPE", "NUMBER");
			dr.setFieldAttribute(DOUBLEFIELD, "TYPE", "NUMBER");
			dr.setFieldAttribute(BOOLFIELD, "TYPE", "TRUTH");
			dr.setFieldAttribute(DATEFIELD, "TYPE", "MOMENT");
			dr.setFieldAttribute(TIMESTAMPFIELD, "TYPE", "MOMENT");

			dr.setFieldAttribute(INTFIELD, "TWO", "");
			dr.setFieldAttribute(DOUBLEFIELD, "TWO", "");
			dr.setFieldAttribute(BOOLFIELD, "THREE", "");
			dr.setFieldAttribute(DATEFIELD, "THREE", "");
			dr.setFieldAttribute(TIMESTAMPFIELD, "THREE", "");
			
			dr.setFieldAttribute(DOUBLEFIELD, "ColumnType", "8");
//			dr.setFieldAttribute(BYTEFIELD, "ColumnType", "4");
//			dr.setFieldAttribute(SHORTFIELD, "ColumnType", "4");
			dr.setFieldAttribute(INTFIELD, "ColumnType", "4");
//			dr.setFieldAttribute(LONGFIELD, "ColumnType", "4");
//			dr.setFieldAttribute(BIGDECIMALFIELD, "ColumnType", "4");
			dr.setFieldAttribute(BOOLFIELD, "ColumnType", "16");
			dr.setFieldAttribute(DATEFIELD, "ColumnType", "91");
//			dr.setFieldAttribute(TIMEFIELD, "ColumnType", "92");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ColumnType", "93");
			
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
	public static DataRow buildSampleDataRowMinMax() {
		DataRow dr = new DataRow();
		try {
			
			dr.setFieldValue(INTFIELD, Integer.MAX_VALUE);
			dr.setFieldValue(SCD_INTFIELD, Integer.MIN_VALUE);
			dr.setFieldValue(DOUBLEFIELD, Double.MAX_VALUE);
			dr.setFieldValue(SCD_DOUBLEFIELD, Double.MIN_VALUE);
//			dr.setFieldValue(BYTEFIELD, Byte.MAX_VALUE);
//			dr.setFieldValue(SCD_BYTEFIELD, Byte.MIN_VALUE);
//			dr.setFieldValue(SHORTFIELD, Short.MAX_VALUE);
//			dr.setFieldValue(SCD_SHORTFIELD, Short.MIN_VALUE);
//			dr.setFieldValue(LONGFIELD, Integer.MAX_VALUE);
//			dr.setFieldValue(SCD_LONGFIELD, Integer.MIN_VALUE);
//			dr.setFieldValue(BIGDECIMALFIELD, new BigDecimal("1238126387123"));
//			dr.setFieldValue(SCD_BIGDECIMALFIELD, new BigDecimal("-1238126387123"));
			
			// The date has to be rounded to the first milliseconds of the day
			// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(System.currentTimeMillis()));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dr.setFieldValue(DATEFIELD, new Date(cal.getTimeInMillis()));
			
			dr.setFieldValue(BOOLFIELD, false);
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(System.currentTimeMillis()));
			
//			dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()));
//			There are types which can't be converted yet, see ResultSetJsonMapper Line 259

			dr.setFieldAttribute(INTFIELD, "ISDIFFERENT", "1");
			dr.setFieldAttribute(DOUBLEFIELD, "ISDIFFERENT", "1");
			dr.setFieldAttribute(BOOLFIELD, "ISDIFFERENT", "0");
			dr.setFieldAttribute(DATEFIELD, "ISDIFFERENT", "0");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ISDIFFERENT", "0");

			dr.setFieldAttribute(INTFIELD, "SOMETYPE", "NUMBER");
			dr.setFieldAttribute(DOUBLEFIELD, "SOMETYPE", "NUMBER");
			dr.setFieldAttribute(BOOLFIELD, "SOMETYPE", "TRUTH");
			dr.setFieldAttribute(DATEFIELD, "SOMETYPE", "MOMENT");
			dr.setFieldAttribute(TIMESTAMPFIELD, "SOMETYPE", "MOMENT");

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
	
	public static DataRow buildStringOnlyDataRow() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(STRINGFIELD, "1337");
			dr.setFieldValue(SCD_STRINGFIELD, "StringValue");
			dr.setFieldValue(TRD_STRINGFIELD, "4StringValue2");
			dr.setFieldValue(FRT_STRINGFIELD, "");
			dr.setFieldValue(FTH_STRINGFIELD, null);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
}
