package com.basiscomponents.db.util;

import com.basiscomponents.db.ResultSet;

/**
 * protected inner util class
 * 
 * @author damore
 *
 */
public class BBTemplateProvider {
	private static final String PE_10 = "+=10)";
	private static final String C_1 = "C(1*)";

	public static String createBBTemplate(ResultSet resultSet, boolean extendedInfo) {
		StringBuilder s = new StringBuilder();
		int cols = resultSet.getColumnCount();
		if (cols > 0) {
			for (int col = 0; col < cols; col++) {
				if (col > 0)
					s.append(",");
				s.append(createBBTemplateColumn(resultSet, col, cols, extendedInfo));
			}
		}
		return s.toString();
	}
	/**
	 * Creates and returns BB template definition for indexed column used by
	 * {@link ResultSet#getBBTemplateColumn}
	 * 
	 * @param col
	 *            Zero-based index of affected column
	 * @param cols
	 *            Total columns (used in checking if at end of record)
	 * @param extendedInfo
	 *            Adds more information to template if true
	 * @return String Column template definition
	 */
	// TODO Urgent reduce the complexity of this Method
	public static String createBBTemplateColumn(final ResultSet rs, final int col, final int cols,
			final boolean extendedInfo) {
		StringBuilder s = new StringBuilder();
		String colName = rs.getColumnName(col);
		s.append(colName).append(":");
		Integer prec = rs.getPrecision(col);
		Integer scale = java.lang.Math.max(0, rs.getScale(col));
		boolean isNum = false;
		int colType = rs.getColumnType(col);
		String colTypeName = rs.getColumnTypeName(col);
		if (colTypeName == null)
			colTypeName = "";
		switch (colType) {
		case java.sql.Types.NULL:
			s.append("C(1)");
			if (extendedInfo)
				s.append(":sqltype=NULL");
			break;
		case java.sql.Types.CHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(")");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=CHAR");
			break;
		case java.sql.Types.VARCHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("*)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=VARCHAR");
			break;
		case java.sql.Types.LONGVARCHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("*)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=LONGVARCHAR");
			break;
		case java.sql.Types.NCHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(")");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NCHAR");
			break;
		case java.sql.Types.NVARCHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(PE_10);
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NVARCHAR");
			break;
		case java.sql.Types.LONGNVARCHAR:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(PE_10);
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=LONGNVARCHAR");
			break;
		case java.sql.Types.INTEGER:
			if (rs.isSigned(col)) {
				s.append("I(4)");
			} else {
				s.append("U(4)");
			}
			if (extendedInfo)
				s.append(":sqltype=INTEGER");
			break;
		case java.sql.Types.TINYINT:
			if (rs.isSigned(col)) {
				s.append("I(1)");
			} else {
				s.append("U(1)");
			}
			if (extendedInfo)
				s.append(":sqltype=TINYINT");
			break;
		case java.sql.Types.SMALLINT:
			if (rs.isSigned(col)) {
				s.append("I(2)");
			} else {
				s.append("U(2)");
			}
			if (extendedInfo)
				s.append(":sqltype=SMALLINT");
			break;
		case java.sql.Types.BIGINT:
			if (rs.isSigned(col)) {
				s.append("I(8)");
			} else {
				s.append("U(8)");
			}
			if (extendedInfo)
				s.append(":sqltype=BIGINT");
			break;
		case java.sql.Types.BIT:
			s.append("N(1)");
			if (extendedInfo)
				s.append(":sqltype=BIT");
			break;
		case java.sql.Types.BOOLEAN:
			s.append("N(1)");
			if (extendedInfo)
				s.append(":sqltype=BOOLEAN");
			break;
		case java.sql.Types.DECIMAL:
			s.append("N(" + rs.getColumnDisplaySize(col) + ((col == cols - 1) ? "*=)" : "*)"));
			if (extendedInfo)
				s.append(":sqltype=DECIMAL size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.NUMERIC:
			s.append("N(" + rs.getColumnDisplaySize(col) + ((col == cols - 1) ? "*=)" : "*)"));
			if (extendedInfo)
				s.append(":sqltype=NUMERIC size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.DOUBLE:
			s.append("Y");
			if (extendedInfo)
				s.append(":sqltype=DOUBLE size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.FLOAT:
			s.append("F");
			if (extendedInfo)
				s.append(":sqltype=FLOAT size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.REAL:
			s.append("B");
			if (extendedInfo)
				s.append(":sqltype=REAL size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.DATE:
			s.append("I(4)");
			if (extendedInfo)
				s.append(":sqltype=DATE");
			break;
		case java.sql.Types.TIME:
			s.append("C(23)");
			if (extendedInfo)
				s.append(":sqltype=TIME");
			break;
		case java.sql.Types.TIMESTAMP:
			s.append("C(23)");
			if (extendedInfo)
				s.append(":sqltype=TIMESTAMP");
			break;
		case java.sql.Types.BINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=BINARY");
			break;
		case java.sql.Types.VARBINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=VARBINARY");
			break;
		case java.sql.Types.LONGVARBINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=LONGVARBINARY");
			break;
		case java.sql.Types.BLOB:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=BLOB");
			break;
		case java.sql.Types.CLOB:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(PE_10);
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=CLOB");
			break;
		case java.sql.Types.NCLOB:
			if (prec <= 0) {
				s.append(C_1);
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(PE_10);
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NCLOB");
			break;
		case 9: // ODBC Date
			s.append("C(10)");
			if (extendedInfo)
				s.append(":sqltype=ODBC_DATE");
			break;
		case 11: // ODBC Timestamp
			s.append("C(19)");
			if (extendedInfo)
				s.append(":sqltype=ODBC_TIMESTAMP");
			break;
		case java.sql.Types.ARRAY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=ARRAY");
			break;
		case java.sql.Types.JAVA_OBJECT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=JAVA_OBJECT");
			break;
		case java.sql.Types.OTHER:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=OTHER");
			break;
		case java.sql.Types.REF:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=REF");
			break;
		case java.sql.Types.DATALINK: // (think URL)
			s.append("C(").append(prec.toString()).append("*)");
			if (extendedInfo)
				s.append(":sqltype=DATALINK");
			break;
		case java.sql.Types.DISTINCT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=DISTINCT");
			break;
		case java.sql.Types.STRUCT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=STRUCT");
			break;
		case java.sql.Types.ROWID:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=ROWID");
			break;
		case java.sql.Types.SQLXML:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=SQLXML");
			break;
		default:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=UNKNOWN");
			break;
		}
		if (extendedInfo) {
			if (colTypeName != "") {
				s.append(" dbtype=").append(colTypeName);
			}
			if (rs.isAutoIncrement(col)) {
				s.append(" auto_increment=1");
			}
			if (rs.isReadOnly(col)) {
				s.append(" read_only=1");
			}
			if (rs.isCaseSensitive(col)) {
				s.append(" case_sensitive=1");
			}
			if (rs.isSigned(col) && isNum) {
				s.append(" signed=1");
			}
			if (rs.isNullable(col) == java.sql.ResultSetMetaData.columnNoNulls) {
				s.append(" required=1");
			}
			s.append(":");
		}
		return s.toString();
	}

}
