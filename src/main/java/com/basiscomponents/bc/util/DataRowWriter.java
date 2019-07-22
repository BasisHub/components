package com.basiscomponents.bc.util;

import static com.basiscomponents.bc.util.Constants.*;
import static com.basiscomponents.bc.util.Constants.BASIS_DBMS;
import static com.basiscomponents.bc.util.SQLHelper.setSqlParams;

public class DataRowWriter {

	private DataRowWriter(){}
	public static com.basiscomponents.db.DataRow write(com.basiscomponents.db.DataRow dr, String table, com.basiscomponents.bc.config.DatabaseConfiguration dbconfig, SqlConnectionHelper connectionHelper, java.util.function.Function<com.basiscomponents.db.DataRow, com.basiscomponents.db.DataRow> reRetrieve) throws Exception {

		boolean pkPresent = dbconfig.isPrimaryKeyPresent(dr);

		try (CloseableWrapper<java.sql.Connection> connw = connectionHelper.getConnection()) {
			java.sql.Connection conn = connw.getCloseable();
			// Read table field names from the table (not from getAttributesRecord()).
			// The field may differ if a custom retrieve sql statement is used.
			java.sql.DatabaseMetaData meta = conn.getMetaData();
			java.sql.ResultSet rs = meta.getColumns(null, null, table, null);
			java.util.List<String> tableFields = new java.util.ArrayList<>();
			while (rs.next()) {
				tableFields.add(rs.getString(COLUMN_NAME));
			}

			StringBuilder sql = new StringBuilder();
			int affectedRows = 0;
			com.basiscomponents.db.DataRow ret = dr.clone();
			java.sql.PreparedStatement prep;

			// update (Try an update an check affected rows. If there are no (0) affected
			// rows, then make an
			// insert.)
			if (pkPresent) {
				sql = new StringBuilder("UPDATE " + dbconfig.getDbQuoteString() + table + dbconfig.getDbQuoteString() + " SET ");

				java.util.List<String> fields = new java.util.ArrayList<>();

				StringBuilder update = new StringBuilder();
				for (String field : dr.getFieldNames()) {
					String field2 = dbconfig.getMapping(field);
					if (dbconfig.containsPrimaryKey(field))
						continue;
					if (tableFields.contains(field2)) {
						fields.add(field);
						update.append("," + dbconfig.getDbQuoteString() + field2 + dbconfig.getDbQuoteString() + "=?");
					}
				}

				if (update.length() > 0) {
					// if the fields are _only_ fields that are part of the primary key
					// (e.g. a table with PK being a compount, not having fields outside the PK)
					// then update would be "" and this portion would fail

					sql.append(update.substring(1));

					StringBuilder wh = new StringBuilder();
					for (String pkfield : dbconfig.getPrimaryKeys()) {
						wh.append(AND + dbconfig.getDbQuoteString() + dbconfig.getMapping(pkfield) + dbconfig.getDbQuoteString() + "=?");
						fields.add(pkfield);
					}
					sql.append(WHERE + wh.substring(5));

					prep = conn.prepareStatement(sql.toString());
					setSqlParams(prep, dr, fields, BASIS_DBMS.equals(dbconfig.getDbType()));

					affectedRows = prep.executeUpdate();
					prep.close();
				} else {
					/// so now we have to do a SELECT to see if the record is there, as we can't
					/// check with
					/// update
					sql = new StringBuilder(
							"SELECT COUNT(*) AS C FROM " + dbconfig.getDbQuoteString() + table + dbconfig.getDbQuoteString());
					StringBuilder wh = new StringBuilder("");
					for (String pkfield : dbconfig.getPrimaryKeys()) {
						wh.append(AND + dbconfig.getDbQuoteString() + dbconfig.getMapping(pkfield) + dbconfig.getDbQuoteString() + "=?");
						fields.add(pkfield);
					}
					if (wh.length() > 0) {
						sql.append(WHERE + wh.substring(5));
					}
					prep = conn.prepareStatement(sql.toString());
					setSqlParams(prep, dr, fields, BASIS_DBMS.equals(dbconfig.getDbType()));
					java.sql.ResultSet jrs = prep.executeQuery();
					com.basiscomponents.db.ResultSet retrs = new com.basiscomponents.db.ResultSet();
					retrs.populate(jrs, true);
					affectedRows = retrs.get(0).getFieldAsNumber("C").intValue();
					prep.close();
				}
			}

			// insert
			boolean inserted = false;
			if (!pkPresent || affectedRows == 0) {
				sql = new StringBuilder("INSERT INTO " + dbconfig.getDbQuoteString() + table + dbconfig.getDbQuoteString() + " (");

				java.util.List<String> fields = new java.util.ArrayList<>();
				StringBuilder keys = new StringBuilder("");
				StringBuilder values = new StringBuilder("");
				for (String field : dr.getFieldNames()) {
					String field2 = dbconfig.getMapping(field);
					if (tableFields.contains(field2)) {
						fields.add(field);
						keys.append("," + dbconfig.getDbQuoteString() + field2 + dbconfig.getDbQuoteString());
						values.append(",?");
					}
				}
				sql.append(keys.substring(1) + ") VALUES(" + values.substring(1) + ")");

				prep = conn.prepareStatement(sql.toString(), java.sql.PreparedStatement.RETURN_GENERATED_KEYS);
				setSqlParams(prep, dr, fields, BASIS_DBMS.equals(dbconfig.getDbType()));

				affectedRows = prep.executeUpdate();
				inserted = affectedRows > 0;

				// get generated keys
				if (affectedRows > 0) {
					try (java.sql.ResultSet gkeys = prep.getGeneratedKeys()) {
						if (gkeys.next()) {
							for (int i = 0; i < gkeys.getMetaData().getColumnCount(); i++) {
								String name = dbconfig.getAutoIncrementKey(i);
								ret.setFieldValue(name, gkeys.getObject(i + 1));
							}
							pkPresent = dbconfig.isPkPresent(pkPresent, ret);
						}
					}
				}

				prep.close();
			}

			dbconfig.setSqlStatement(sql.toString());

			// reload from the database
			if (pkPresent) {
				ret = reRetrieve.apply(ret);
			}

			if (inserted) {
				ret.setAttribute("CREATED", "TRUE");
			}
			return ret;

		}
	}

}
