package com.basiscomponents.bc.util;

import java.sql.SQLException;

import static com.basiscomponents.db.ExpressionMatcher.getPreparedWhereClauseValues;

public class SQLHelper {
	/**
	 * Sets values in a prepared statement using a DataRow.
	 *
	 * @param prep the prepared statement.
	 * @param dr a DataRow containing the values for the prepared statement.
	 * @param fields an ArrayList with field names. If not null and not empty, this list will be used
	 *        to get a portion of values from the DataRow dr. Otherwise all fields from dr will be set
	 *        in the prepared statement.
	 * @throws SQLException is thrown when a value cannot be set.
	 */
	public static void setSqlParams(java.sql.PreparedStatement prep, com.basiscomponents.db.DataRow dr, java.util.List<String> fields,
	                                 boolean isBasisDBMS) throws java.sql.SQLException, java.text.ParseException {
		if (prep == null || dr == null) {
			return;
		}

		if (fields == null) {
			fields = dr.getFieldNames();
		}

		int index = 1;
		for (String field : fields) {
			int type;
			com.basiscomponents.db.DataField o;

			try {
				type = dr.getFieldType(field);
				o = dr.getField(field);
			} catch (Exception e) {
				// Should never occur. This loop iterates over the DataRow fields. This ensures
				// that all
				// fields exists in the DataRow.
				e.printStackTrace();
				index++;
				continue;
			}

			if (o.getValue() == null) {
				// If the o.getValue() is null you will never get to setPreparedStatement
				if (isBasisDBMS && type == java.sql.Types.CHAR) {
					prep.setString(index, "");
				} else {
					prep.setNull(index, type);
				}
				index++;
				continue;
			} else if (o.getValue() instanceof String && ((String) o.getValue()).startsWith("cond:")) {
				com.basiscomponents.db.DataRow drv = getPreparedWhereClauseValues(((String) o.getValue()).substring(5), type);
				for (String expField : drv.getFieldNames()) {
					prep.setObject(index, drv.getFieldValue(expField));
					index++;
				}
				continue;
			}
			setPreparedStatementType(prep, isBasisDBMS, index, type, o);
			index++;
		}
	}
	private static void setPreparedStatementType(java.sql.PreparedStatement prep, boolean isBasisDBMS,
	                                             int index, Integer type, com.basiscomponents.db.DataField o) throws SQLException {
		switch (type) {
			case java.sql.Types.NUMERIC:
				if (isBasisDBMS && o.getValue() == null)
					prep.setBigDecimal(index, new java.math.BigDecimal(0));
				else
					prep.setBigDecimal(index, o.getBigDecimal());
				break;
			case java.sql.Types.INTEGER:
				if (isBasisDBMS && o.getValue() == null)
					prep.setInt(index, 0);
			else
					prep.setInt(index, o.getInt());
				break;
			case java.sql.Types.DOUBLE:
				prep.setDouble(index, o.getDouble());
			break;
			case java.sql.Types.LONGNVARCHAR:
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARBINARY:
				if (isBasisDBMS && o.getValue() == null)
					prep.setString(index, "");
				else
					prep.setString(index, o.getString());
				break;
			case java.sql.Types.BIT:
			case java.sql.Types.BOOLEAN:
				prep.setBoolean(index, o.getBoolean());
				break;
			case java.sql.Types.DATE:
				prep.setDate(index, o.getDate());
				break;
			case java.sql.Types.TIMESTAMP:
				prep.setTimestamp(index, o.getTimestamp());
				break;
			case java.sql.Types.TIME:
				prep.setTime(index, o.getTime());
				break;
			case java.sql.Types.OTHER:
				/// this is an auto-generated key. set as string and hope for the best
				if (isBasisDBMS && o.getValue() == null)
					prep.setString(index, "");
				else
					prep.setString(index, o.getString());
				break;
			//TODO: add all other known types here for which we do not have an explicit handler
			case java.sql.Types.BIGINT:
				prep.setObject(index, o.getValue());
				break;
			default:
				System.err.println(
						"WARNING: using prep.setObject(object) will fail if there is no equivalent SQL type for the given object - type is "+type);
				prep.setObject(index, o.getValue());
		}

	}
	public static void setParameters(com.basiscomponents.db.DataRow params, java.sql.PreparedStatement prep) {
		int i = 1;
		for (String p : params.getFieldNames()) {
			try {
				prep.setObject(i, params.getFieldValue(p));
			} catch (SQLException e) {
				// Should never occur. This loop iterates over the DataRow fields. This ensures
				// that all
				// fields exists in the DataRow.
			}
			i++;
		}
	}
	public static boolean isChartype(int type) {
		switch (type){
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.NCHAR:
			case java.sql.Types.NVARCHAR:
			case java.sql.Types.LONGNVARCHAR:
			case java.sql.Types.CLOB:
				return true;
			default:
				return false;
		}
	}
}
