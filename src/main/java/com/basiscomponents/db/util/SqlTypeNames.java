package com.basiscomponents.db.util;

import java.util.HashMap;
import java.util.Map;

public class SqlTypeNames {
	private static final Map<String, Integer> sqlNameMap = new HashMap<>();
	private static final int defaultType = java.sql.Types.OTHER;

	private SqlTypeNames() {
	}

	private static final Map<Integer, String> typeNameMap = new HashMap<>();

	public static String get(int sqlType) {
		return typeNameMap.get(sqlType);
	}

	public static int getSQLType(String typename) {
		return sqlNameMap.getOrDefault(typename, defaultType);
	}
	static {
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

		sqlNameMap.put("java.lang.String", java.sql.Types.VARCHAR);
		sqlNameMap.put("byte", java.sql.Types.TINYINT);
		sqlNameMap.put("java.lang.Byte", java.sql.Types.TINYINT);
		sqlNameMap.put("long", java.sql.Types.BIGINT);
		sqlNameMap.put("java.lang.Long", java.sql.Types.BIGINT);
		sqlNameMap.put("java.math.BigInteger", java.sql.Types.BIGINT);
		sqlNameMap.put("short", java.sql.Types.SMALLINT);
		sqlNameMap.put("java.lang.Short", java.sql.Types.SMALLINT);
		sqlNameMap.put("boolean", java.sql.Types.BOOLEAN);
		sqlNameMap.put("java.lang.Boolean", java.sql.Types.BOOLEAN);
		sqlNameMap.put("char", java.sql.Types.CHAR);
		sqlNameMap.put("java.lang.Character", java.sql.Types.CHAR);
		sqlNameMap.put("double", java.sql.Types.DOUBLE);
		sqlNameMap.put("java.lang.Double", java.sql.Types.DOUBLE);
		sqlNameMap.put("float", java.sql.Types.REAL);
		sqlNameMap.put("java.lang.Float", java.sql.Types.REAL);
		sqlNameMap.put("int", java.sql.Types.INTEGER);
		sqlNameMap.put("java.lang.Integer", java.sql.Types.INTEGER);
		sqlNameMap.put("java.math.BigDecimal", java.sql.Types.NUMERIC);
		sqlNameMap.put("java.sql.Date", java.sql.Types.DATE);
		sqlNameMap.put("java.sql.Time", java.sql.Types.TIME);
		sqlNameMap.put("java.sql.Timestamp", java.sql.Types.TIMESTAMP);
		sqlNameMap.put("java.sql.Blob", java.sql.Types.BLOB);
		sqlNameMap.put("java.sql.Clob", java.sql.Types.CLOB);
		sqlNameMap.put("java.sql.Array", java.sql.Types.ARRAY);
		sqlNameMap.put("java.sql.Struct", java.sql.Types.STRUCT);
		sqlNameMap.put("java.sql.Ref", java.sql.Types.REF);

	}
}
