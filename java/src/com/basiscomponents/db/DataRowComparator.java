package com.basiscomponents.db;

public class DataRowComparator implements java.util.Comparator<DataRow> {

	private String fieldName;

	public DataRowComparator(String fieldName) {
		this.fieldName = fieldName;
	}

	public int compare(DataRow dr1, DataRow dr2) {
		int fieldType;

		Object val1 = null;
		Object val2 = null;

		try {
			val1 = dr1.getDataField(fieldName).getObject();
		} catch(Exception ex) {}

		try {
			val2 = dr2.getDataField(fieldName).getObject();
		} catch (Exception ex) {}

		if (val1 == null && val2 == null)
			return 0;
		else if (val1 == null)
			return -1;
		else if (val2 == null)
			return 1;


		try {
			fieldType = dr1.getFieldType(fieldName);
		} catch (Exception e) {
			fieldType = java.sql.Types.CHAR;
		}

		int returnVal=0;
		DataField f1 = dr1.getDataField(fieldName);
		DataField f2 = dr2.getDataField(fieldName);

		switch(fieldType) {
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.NCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			String s1 = f1.getString();
			String s2 = f2.getString();
			returnVal = s1.compareToIgnoreCase(s2);
			break;
		case java.sql.Types.BIGINT:
			long lng1 = f1.getLong();
			long lng2 = f2.getLong();
			returnVal = Long.compare(lng1, lng2);
			break;
		case java.sql.Types.TINYINT:
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
		case 9:
		case 11:
			int int1 = f1.getInt();
			int int2 = f2.getInt();
			returnVal = Integer.compare(int1, int2);
			break;
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
		case java.sql.Types.REAL:
			double dbl1 = f1.getDouble();
			double dbl2 = f2.getDouble();
			returnVal = Double.compare(dbl1, dbl2);
			break;
		case java.sql.Types.NUMERIC:
		case java.sql.Types.DECIMAL:
			java.math.BigDecimal bd1 = f1.getBigDecimal();
			java.math.BigDecimal bd2 = f2.getBigDecimal();
			returnVal = bd1.compareTo(bd2);
			break;
		case java.sql.Types.BOOLEAN:
		case java.sql.Types.BIT:
			boolean b1 = f1.getBoolean();
			boolean b2 = f2.getBoolean();
			returnVal = Boolean.compare(b1, b2);
			break;
		case java.sql.Types.DATE:
			java.sql.Date d1 = f1.getDate();
			java.sql.Date d2 = f2.getDate();
			returnVal = d1.compareTo(d2);
			break;
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
			java.sql.Timestamp t1 = f1.getTimestamp();
			java.sql.Timestamp t2 = f2.getTimestamp();
			returnVal = t1.compareTo(t2);
			break;
		}

		return returnVal;
	}
}
