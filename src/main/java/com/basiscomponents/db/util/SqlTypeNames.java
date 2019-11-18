package com.basiscomponents.db.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTypeNames {
	private SqlTypeNames() {
	}
	private static final Map<Integer, String> typeNameMap = new HashMap<>();
	private static final List<Integer> numericTypeCodeList = new ArrayList<>();

	static {
		numericTypeCodeList.add(java.sql.Types.BIGINT);
		numericTypeCodeList.add(java.sql.Types.TINYINT);
		numericTypeCodeList.add(java.sql.Types.INTEGER);
		numericTypeCodeList.add(java.sql.Types.SMALLINT);
		numericTypeCodeList.add(java.sql.Types.NUMERIC);
		numericTypeCodeList.add(java.sql.Types.DOUBLE);
		numericTypeCodeList.add(java.sql.Types.FLOAT);
		numericTypeCodeList.add(java.sql.Types.DECIMAL);
		numericTypeCodeList.add(java.sql.Types.REAL);

		typeNameMap.put(java.sql.Types.ARRAY, "ARRAY");
		typeNameMap.put(java.sql.Types.BIGINT, "BIGINT");
		typeNameMap.put(java.sql.Types.BINARY, "BINARY");
		typeNameMap.put(java.sql.Types.BIT, "BIT");
		typeNameMap.put(java.sql.Types.BLOB, "BLOB");
		typeNameMap.put(java.sql.Types.BOOLEAN, "BOOLEAN");
		typeNameMap.put(java.sql.Types.CHAR, "CHAR");
		typeNameMap.put(java.sql.Types.CLOB, "CLOB");
		typeNameMap.put(java.sql.Types.DATALINK, "DATALINK");
		typeNameMap.put(java.sql.Types.DATE, "DATE");
		typeNameMap.put(java.sql.Types.DECIMAL, "DECIMAL");
		typeNameMap.put(java.sql.Types.DISTINCT, "DISTINCT");
		typeNameMap.put(java.sql.Types.DOUBLE, "DOUBLE");
		typeNameMap.put(java.sql.Types.FLOAT, "FLOAT");
		typeNameMap.put(java.sql.Types.INTEGER, "INTEGER");
		typeNameMap.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
		typeNameMap.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
		typeNameMap.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
		typeNameMap.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
		typeNameMap.put(java.sql.Types.NCHAR, "NCHAR");
		typeNameMap.put(java.sql.Types.NCLOB, "NCLOB");
		typeNameMap.put(java.sql.Types.NULL, "NULL");
		typeNameMap.put(java.sql.Types.NUMERIC, "NUMERIC");
		typeNameMap.put(java.sql.Types.NVARCHAR, "NVARCHAR");
		typeNameMap.put(java.sql.Types.OTHER, "OTHER");
		typeNameMap.put(java.sql.Types.REAL, "REAL");
		typeNameMap.put(java.sql.Types.REF, "REF");
		typeNameMap.put(java.sql.Types.REF_CURSOR, "REF_CURSOR");
		typeNameMap.put(java.sql.Types.ROWID, "ROWID");
		typeNameMap.put(java.sql.Types.SMALLINT, "SMALLINT");
		typeNameMap.put(java.sql.Types.SQLXML, "SQLXML");
		typeNameMap.put(java.sql.Types.STRUCT, "STRUCT");
		typeNameMap.put(java.sql.Types.TIME, "TIME");
		typeNameMap.put(java.sql.Types.TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE");
		typeNameMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
		typeNameMap.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE");
		typeNameMap.put(java.sql.Types.TINYINT, "TINYINT");
		typeNameMap.put(java.sql.Types.VARBINARY, "VARBINARY");
		typeNameMap.put(java.sql.Types.VARCHAR, "VARCHAR");
		typeNameMap.put(9, "BASIS DATE");
		typeNameMap.put(11, "BASIS TIMESTAMP");
	}


	public static String get(int sqlType) {
		return typeNameMap.get(sqlType);
	}

	public static boolean isNumericType(int type){
		return numericTypeCodeList.contains(type);
	}
}
