package com.basiscomponents.db.util;

import java.util.Arrays;
import java.util.List;

public class NumericTypeCodeList {
	private static final List<Integer> typecodes = Arrays.asList(
			java.sql.Types.BIGINT,
			java.sql.Types.TINYINT,
			java.sql.Types.INTEGER,
			java.sql.Types.SMALLINT,
			java.sql.Types.NUMERIC,
			java.sql.Types.DOUBLE,
			java.sql.Types.FLOAT,
			java.sql.Types.DECIMAL,
			java.sql.Types.REAL,
			java.sql.Types.BOOLEAN,
			java.sql.Types.BIT,
			java.sql.Types.DATE,
			9);

	public static boolean isTypeCode(int code) {
		return typecodes.contains(code);
	}
}
