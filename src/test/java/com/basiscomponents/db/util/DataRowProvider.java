package com.basiscomponents.db.util;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.basiscomponents.constants.SpecialCharacterConstants;
import com.basiscomponents.db.DataRow;

public class DataRowProvider {
	public static final String STRINGFIELD = "STRINGFIELD";
	public static final String SCD_STRINGFIELD = "SCD_STRINGFIELD";
	public static final String TRD_STRINGFIELD = "TRD_STRINGFIELD";
	public static final String FRT_STRINGFIELD = "FRT_STRINGFIELD";
	public static final String FTH_STRINGFIELD = "FTH_STRINGFIELD";
	public static final String STH_STRINGFIELD = "STH_STRINGFIELD";
	public static final String SVT_STRINGFIELD = "SVT_STRINGFIELD";
	public static final String EGT_STRINGFIELD = "EGT_STRINGFIELD";
	public static final String NTH_STRINGFIELD = "NTH_STRINGFIELD";
	public static final String TTH_STRINGFIELD = "TTH_STRINGFIELD";
	public static final String ELT_STRINGFIELD = "ELT_STRINGFIELD";
	public static final String INTFIELD = "INTFIELD";
	public static final String SCD_INTFIELD = "SCD_INTFIELD";
	public static final String DOUBLEFIELD = "DOUBLEFIELD";
	public static final String SCD_DOUBLEFIELD = "SCD_DOUBLEFIELD";
	public static final String BOOLFIELD = "BOOLFIELD";
	public static final String DATEFIELD = "DATEFIELD";
	public static final String TIMESTAMPFIELD = "TIMESTAMPFIELD";
	public static final String TIMEFIELD = "TIMEFIELD";
	public static final String LISTFIELD = "LISTFIELD";
	public static final String LONGFIELD = "LONGFIELD";
	public static final String SCD_LONGFIELD = "SCD_LONGFIELD";
	public static final String BIGDECIMALFIELD = "BIGDECIMALFIELD";
	public static final String SCD_BIGDECIMALFIELD = "SCD_BIGDECIMALFIELD";
	public static final String FLOATFIELD = "FLOATFIELD";
	public static final String SHORTFIELD = "SHORTFIELD";
	public static final String BYTEFIELD = "BYTEFIELD";
	
	public static final String NESTEDDATAROW1 = "NESTEDDATAROW1";
	public static final String NESTEDDATAROW2 = "NESTEDDATAROW2";
	public static final String NESTEDRESULTSET1 = "NESTEDRESULTSET1";
	public static final String NESTEDRESULTSET2 = "NESTEDRESULTSET2";
	public static final String STRINGFIELD_VALUE ="1337";

	public static DataRow buildSampleDataRow(boolean nullAllFields) {
		DataRow dr = new DataRow();
		try {

			// Adding attributes to the DataRow
			dr.setAttribute("hello", "world");
			dr.setAttribute("bye", "world");

			// Adding a rowkey
			dr.addToRowKey("1fdssdf79g79df");

			// Adding Data into the DataRow
			List l = new ArrayList<String>();
			l.add("hello");
			l.add("world");
			dr.setFieldValue(LISTFIELD, l);

			int myInt = 1;
			dr.setFieldValue(INTFIELD, myInt);
			Double myDouble = 1.0;
			dr.setFieldValue(DOUBLEFIELD, myDouble);
			// Float will be casted to double
			
			// The date has to be rounded to the first millisecond of the day
			// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(0));
			cal.set(Calendar.HOUR_OF_DAY, 0);
		    cal.set(Calendar.MINUTE, 0);
		    cal.set(Calendar.SECOND, 0);
		    cal.set(Calendar.MILLISECOND, 0);
			dr.setFieldValue(DATEFIELD, new Date(cal.getTimeInMillis()));
			
			dr.setFieldValue(BOOLFIELD, true);
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(0));
			
			// dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()));
			// There are types which can't be converted yet, see ResultSetJsonMapper Line 259

			if (nullAllFields) {
				dr.setFieldValue(INTFIELD, null);
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
			dr.setFieldAttribute(INTFIELD, "ColumnType", "4");
			dr.setFieldAttribute(BOOLFIELD, "ColumnType", "16");
			dr.setFieldAttribute(DATEFIELD, "ColumnType", "91");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ColumnType", "93");
			dr.setFieldAttribute(LISTFIELD, "ColumnType", "-973");
			
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
			
			// The date has to be rounded to the first millisecond of the day
			// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(0));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			dr.setFieldValue(DATEFIELD, new Date(cal.getTimeInMillis()));
			
			dr.setFieldValue(BOOLFIELD, false);
			dr.setFieldValue(TIMESTAMPFIELD, new Timestamp(0));
			
			// dr.setFieldValue(TIMEFIELD, new Time(System.currentTimeMillis()));
			// There are types which can't be converted yet, see ResultSetJsonMapper Line 259

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
			
			dr.setFieldAttribute(DOUBLEFIELD, "ColumnType", "8");
			dr.setFieldAttribute(SCD_DOUBLEFIELD, "ColumnType", "8");
			dr.setFieldAttribute(INTFIELD, "ColumnType", "4");
			dr.setFieldAttribute(SCD_INTFIELD, "ColumnType", "4");
			dr.setFieldAttribute(BOOLFIELD, "ColumnType", "16");
			dr.setFieldAttribute(DATEFIELD, "ColumnType", "91");
			dr.setFieldAttribute(TIMESTAMPFIELD, "ColumnType", "93");
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
	public static DataRow buildStringOnlyDataRow() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(STRINGFIELD, STRINGFIELD_VALUE);
			dr.setFieldValue(SCD_STRINGFIELD, "StringValue");
			dr.setFieldValue(TRD_STRINGFIELD, "4StringValue2");
			dr.setFieldValue(FRT_STRINGFIELD, "");
			dr.setFieldValue(FTH_STRINGFIELD, null);
			dr.setFieldValue(STH_STRINGFIELD, "-1");
			dr.setFieldValue(SVT_STRINGFIELD, "1.0");
			dr.setFieldValue(EGT_STRINGFIELD, SpecialCharacterConstants.GERMAN_SPECIAL_CHARACTERS);
			dr.setFieldValue(NTH_STRINGFIELD, SpecialCharacterConstants.FRENCH_SPECIAL_CHARACTERS);
			dr.setFieldValue(TTH_STRINGFIELD, SpecialCharacterConstants.STANDARD_SPECIAL_CHARACTERS);
			dr.setFieldValue(ELT_STRINGFIELD, SpecialCharacterConstants.MATHEMATICAL_SPECIAL_CHARACTERS);
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return dr;
	}
	
	public static DataRow buildNestedDataRowWithDataRows() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(NESTEDDATAROW1, buildSampleDataRow(false));
			dr.setFieldValue(NESTEDDATAROW2, buildSampleDataRowMinMax());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

	public static DataRow buildNestedDataRowWithResultSets() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(NESTEDRESULTSET1, ResultSetProvider.createDefaultResultSet(false));
			dr.setFieldValue(NESTEDRESULTSET2, ResultSetProvider.createDefaultResultSetMinMax());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

	public static DataRow buildNestedDataRowWithMultipleDataRowsResultSet() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue(NESTEDRESULTSET1, ResultSetProvider.createMultipleDataRowResultSet());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

	public static DataRow buildNumberTypesDataRow() {
		DataRow dr = new DataRow();
		try {
			// Values Set
			long myLong = Long.valueOf("66547657568678");
			dr.setFieldValue(LONGFIELD, myLong);
			BigDecimal myDecimal = new BigDecimal("546445464354");
			dr.setFieldValue(BIGDECIMALFIELD, myDecimal);
			BigDecimal myDeci = new BigDecimal("2345465476565");
			dr.setFieldValue(SCD_BIGDECIMALFIELD, myDeci);
			Float myFloat = new Float("4.2");
			dr.setFieldValue(FLOATFIELD, myFloat);
			// Attributes Set
			dr.setFieldAttribute(LONGFIELD, "ColumnType", Integer.toString(java.sql.Types.BIGINT));
			dr.setFieldAttribute(BIGDECIMALFIELD, "ColumnType", Integer.toString(java.sql.Types.DECIMAL));
			dr.setFieldAttribute(SCD_BIGDECIMALFIELD, "ColumnType", Integer.toString(java.sql.Types.DECIMAL));
			dr.setFieldAttribute(FLOATFIELD, "ColumnType", Integer.toString(java.sql.Types.REAL));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

	public static DataRow buildNumberTypesDataRow2() {
		DataRow dr = new DataRow();
		try {
			// Values Set
			int myInt = 5;
			dr.setFieldValue(INTFIELD, myInt);
			byte myByte = Byte.valueOf("5");
			dr.setFieldValue(BYTEFIELD, myByte);
			short myShort = Short.valueOf("42");
			dr.setFieldValue(SHORTFIELD, myShort);
			long myLong = Long.valueOf("66547657568678");
			dr.setFieldValue(LONGFIELD, myLong);
			// Attributes Set
			dr.setFieldAttribute(BYTEFIELD, "ColumnType", Integer.toString(java.sql.Types.TINYINT));
			dr.setFieldAttribute(SHORTFIELD, "ColumnType", Integer.toString(java.sql.Types.SMALLINT));
			dr.setFieldAttribute(INTFIELD, "ColumnType", Integer.toString(java.sql.Types.INTEGER));
			dr.setFieldAttribute(LONGFIELD, "ColumnType", Integer.toString(java.sql.Types.BIGINT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

}
