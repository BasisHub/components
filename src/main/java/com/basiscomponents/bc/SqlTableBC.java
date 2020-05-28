package com.basiscomponents.bc;

import com.basiscomponents.bc.config.DatabaseConfiguration;
import com.basiscomponents.bc.util.CloseableWrapper;
import com.basiscomponents.bc.util.DataRowWriter;
import com.basiscomponents.bc.util.SqlConnectionHelper;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.DataRowRegexMatcher;
import com.basiscomponents.util.KeyValuePair;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.basiscomponents.bc.util.Constants.*;
import static com.basiscomponents.bc.util.SQLHelper.*;

/**
 * <h1>SqlTableBC represents a table in a database.</h1>
 * <p>
 * Data can be read/written from/into the database using a JDBC driver.<br>
 * {@link #setTable(String)} sets the table, which should be used for
 * reading/writing data.
 * <p>
 * SqlTableBC is using {@link DataRow} for a single data row representation and
 * {@link ResultSet} for a set of data rows.
 * <p>
 * The developer has to work with DataRow's and ResultSet's only. No SQL
 * knowledge is required.
 * <p>
 * SqlTableBC is using the JDBC metadata to determine the table information
 * (such as column names, column types, field lengths, and so on).
 *
 * @author vkolarov, damore
 */
public class SqlTableBC implements BusinessComponent {

	private final SqlConnectionHelper connectionHelper;

	private String table;
	private String scope = "";
	private HashMap<String, ArrayList<String>> scopes;

	private DataRow attributesRecord;
	private DataRow allowedFilter;
	private DataRow metaData;
	private String retrieveSql;

	private DataRow retrieveParams;

	private DataRow filter = new DataRow();
	private DataRow fieldSelection;

	private DatabaseConfiguration dbconfig = new DatabaseConfiguration();
	private Boolean truncateFieldValues = false;

	private Map<String, DataField> regexes;

	/**
	 * Constructor.
	 * <p>
	 * Creates a new SqlTableBC object using a database URL.
	 *
	 * @param url database URL to the database.
	 */
	public SqlTableBC(String url) {
		this.connectionHelper = new SqlConnectionHelper(url);
	}

	/**
	 * Constructor.
	 * <p>
	 * Creates a new SqlTableBC object using driver, URL, user and password.
	 *
	 * @param driver   the name/package of the JDBC driver.
	 * @param url      database URL to the database.
	 * @param user     the name of the database user.
	 * @param password the password of the database user.
	 * @throws ClassNotFoundException thrown if the JDBC driver could not be found.
	 */
	public SqlTableBC(String driver, String url, String user, String password) throws ClassNotFoundException {
		this.connectionHelper = new SqlConnectionHelper(url, user, password, driver);
	}

	/**
	 * Constructor.
	 * <p>
	 * Creates a new SqlTableBC object using a database connection.
	 * <p>
	 * <b>NOTE</b>: SqlTableBC will not close the connection (after reading or
	 * writing).
	 *
	 * @param con the database connection to the database.
	 * @throws SQLException thrown if the connection status (con.isCloses()) cannot
	 *                      be determined
	 */
	public SqlTableBC(Connection con) throws SQLException {
		connectionHelper = new SqlConnectionHelper(con);
	}

	/**
	 * Returns a database connection.
	 * <p>
	 * If {@link #SqlTableBC(Connection con)} was used, the connection passed in the
	 * constructor will be returned.<br>
	 * Otherwise a new connection, using the database URL, will be created.
	 *
	 * @return a {@link Connection} to the database.
	 * @throws SQLException thrown if connection cannot be established.
	 */
	private CloseableWrapper<Connection> getConnection() throws SQLException {
		return connectionHelper.getConnection();
	}

	/**
	 * Sets the name of the table for reading and writing data.
	 * <p>
	 * This method reads the JDBC metadata for the table.<br>
	 * The metadata can be retrieved bye the {@link #getAttributesRecord()} method.
	 * <p>
	 * <b>NOTE</b>: if a custom retrieve SQL statement was set, then this will be
	 * used for the metadata generation.
	 *
	 * @param table the table for reading and writing data.
	 * @see #getAttributesRecord()
	 * @see #setRetrieveSql(String)
	 * @see #setRetrieveSql(String, DataRow)
	 */
	public void setTable(String table) {
		this.table = table;
		this.attributesRecord = new DataRow();
		createAttributesRecord();
	}

	/**
	 * Sets a custom retrieve SQL statement.
	 * <p>
	 * A custom retrieve SQL statement can be set when a more complex select
	 * statement is needed (f.g. to retrieve foreign key values). The SQL statement
	 * can be a prepared statement. Parameters can be set with
	 * {@link #setRetrieveParameters(DataRow)}.
	 * <p>
	 * <b>NOTE</b>: if a custom retrieve SQL statement is used, a table name need to
	 * be set (using {@link #setTable(String)}) for writing/removing data from the
	 * main table.
	 *
	 * @param sql the custom retrieve SQL statement.
	 * @see #setRetrieveParameters(DataRow)
	 */
	public void setRetrieveSql(String sql) {
		retrieveSql = sql;
	}

	/**
	 * Sets a custom retrieve SQL prepared statement.
	 * <p>
	 * A custom retrieve SQL statement can be set when a more complex select
	 * statement is needed (f.g. to retrieve foreign key values).<br>
	 * <p>
	 * Prepared values (question marks in the SQL statement) can be passed within a
	 * DataRow.<br>
	 * The field count and field type in the DataRow should match the count and the
	 * type of the question mark in the prepared select statement.
	 * <p>
	 * <b>NOTE</b>: if a custom retrieve SQL statement is used, a table name need to
	 * be set (using {@link #setTable(String)}) for writing/removing data from the
	 * main table.
	 *
	 * @param sql        the custom retrieve SQL statement.
	 * @param retrieveDr the data row with values for the prepared statement.
	 */
	public void setRetrieveSql(String sql, DataRow retrieveDr) {
		retrieveSql = sql;
		retrieveParams = retrieveDr;
	}

	/**
	 * Sets prepared parameters when a custom retrieve SQL statement is used.
	 *
	 * @param retrieveDr the data row with values for the prepared statement.
	 */
	public void setRetrieveParameters(DataRow retrieveDr) {
		retrieveParams = retrieveDr;
	}

	/**
	 * Get the database specific quote string character.<br>
	 * <b>NOTE</b>: the quote string character is returned from the JDBC driver and
	 * it is determined in the setTable(String) method.
	 *
	 * @return the database specific quote string character.
	 */
	public String getDbQuoteString() {
		return dbconfig.getDbQuoteString();
	}

	/**
	 * Returns a previously set filter. May be null.
	 *
	 * @return a DataRow with filter fields.
	 * @see #setFilter(DataRow)
	 */
	public DataRow getFilter() {
		if (regexes != null)
			regexes.forEach((k, v) -> filter.addDataField(k, v));
		return this.filter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFilter(final DataRow filter) {
		if (filter == null) {
			this.filter = new DataRow();
		} else {
			DataRow f = filter.clone();
			try {
				Map<String, DataField> rgxs = f.getFieldNames().stream()
						.map(x -> new KeyValuePair<>(x, filter.getField(x)))
						.filter(x -> x.getValue().getString().startsWith("regex:"))
						// .peek(x -> System.out.println(x.getValue().getString()))
						.collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue));

				this.regexes = rgxs;
				rgxs.forEach((k, v) -> f.removeField(k));
			} catch (Exception e) {
			}
			this.filter = f;
		}
	}

	/**
	 * Returns a previously set field selection. May be null.
	 *
	 * @return a DataRow with field names for selection.
	 */
	public DataRow getFieldSelection() {
		return this.fieldSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFieldSelection(DataRow fieldSelection) {
		this.fieldSelection = fieldSelection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFieldSelection(Collection<String> fieldSelection) {
		if (fieldSelection == null) {
			this.fieldSelection = null;
		} else {
			DataRow fields = new DataRow();
			for (String field : fieldSelection) {
				try {
					fields.setFieldValue(field, "");
				} catch (Exception e) {
					// do nothing?
				}
			}
			this.fieldSelection = fields;
		}
	}

	/**
	 * Get current scope (if scope is set).
	 *
	 * @return the selected scope.
	 */
	public String getScope() {
		return this.scope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * Get a HashMap with all defined scopes (A-Z).
	 *
	 * @return a HashMap with scope name as key and field names as ArrayList.
	 */
	public HashMap<String, ArrayList<String>> getScopeDef() {
		return this.scopes;
	}

	public boolean isFieldInScope(final String fieldName) {
		return isFieldInScope(this.scope, fieldName);
	}

	public boolean isFieldInScope(final String scope, final String fieldName) {
		return this.scopes.get(scope).stream().anyMatch(x -> x.equalsIgnoreCase(fieldName));
	}

	/**
	 * Sets the scope definition.<br>
	 * The scope definition is a HashMap with the scope name as key and an ArrayList
	 * with field names as value.
	 *
	 * @param scopes the HashMap with the scope definitions
	 */
	public void setScopeDef(final HashMap<String, ArrayList<String>> scopes) {
		this.scopes = new HashMap<>(scopes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet retrieve() throws Exception {
		return retrieve(-1, -1);
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO Still too complex
	@Override
	public ResultSet retrieve(final int first, final int last) throws Exception {
		checkIndeces(first, last);
		checkFilter(filter);

		DataRow filterRow = filter.clone();
		List<DataRowRegexMatcher> regexmatchers = Optional.ofNullable(regexes).orElseGet(HashMap::new).entrySet()
				.stream().map(x -> new DataRowRegexMatcher(x.getKey(), x.getValue().getString()))
				.collect(Collectors.toList());

		final ResultSet retrs;

		try (CloseableWrapper<Connection> connw = getConnection()) {
			retrs = retrieveDataRows(first, last, filterRow, connw);
		}

		// Set the generated meta attributes to the first record
		if (retrs.size() > 0) {
			Iterator<DataRow> iterator = retrs.iterator();
			if (!regexmatchers.isEmpty()) {
				while (iterator.hasNext()) {
					DataRow dr = iterator.next();
					if (!regexmatchers.stream().allMatch(x -> x.matches(dr))) {
						retrs.remove(dr.getRowID());
					}
				}
			}
			DataRow dr = retrs.get(0);
			for (String field : attributesRecord.getFieldNames()) {
				if (dr.contains(field))
					dr.setFieldAttributes(field, attributesRecord.getFieldAttributes(field));
			}
		}

		return retrs;
	}

	private ResultSet retrieveDataRows(int first, int last, final DataRow filterRow,
			final CloseableWrapper<Connection> connw) throws Exception {
		ResultSet retrs;
		Connection conn = connw.getCloseable();
		StringBuilder sql;

		java.util.LinkedHashSet<String> fields = new java.util.LinkedHashSet<>();
		if ((this.fieldSelection == null || this.fieldSelection.getFieldNames().isEmpty())
				&& (scope == null || scope.equals(""))) {
			fields.add("*");
		}

		if (scope != null) {
			for (char s : scope.toCharArray()) {
				String localScope = String.valueOf(s);
				if (scopes.containsKey(localScope))
					fields.addAll(scopes.get(localScope));
			}
		}

		if (fieldSelection != null) {
			fields.addAll(fieldSelection.getFieldNames());
		}

		boolean customStatementUsed = (retrieveSql != null && !retrieveSql.equals(""));

		StringBuilder sqlfields = new StringBuilder("");
		if (fields.contains("*"))
			sqlfields.append("*");
		else {
			for (String field : fields) {
				sqlfields.append("," + dbconfig.getDbQuoteString() + field + dbconfig.getDbQuoteString());
			}
			sqlfields = new StringBuilder(sqlfields.substring(1));
		}

		if (customStatementUsed) {
			sql = new StringBuilder("SELECT " + sqlfields + " FROM (" + retrieveSql + ") ");
			if (com.basiscomponents.bc.util.Constants.MYSQL_DBMS.equals(dbconfig.getDbType()))
				sql.append(" as s ");
		} else
			sql = new StringBuilder("SELECT " + sqlfields + " FROM " + dbconfig.getDbQuoteString() + table
					+ dbconfig.getDbQuoteString());

		if (filterRow != null && !filterRow.getFieldNames().isEmpty()) {
			StringBuilder wh = new StringBuilder("");
			for (String f : filterRow.getFieldNames()) {

				if (filterRow.getFieldAsString(f).startsWith("cond:")) {
					wh.append(" AND (" + com.basiscomponents.db.ExpressionMatcher.generatePreparedWhereClause(f,
							filterRow.getFieldAsString(f).substring(5)) + ")");
				} else {
					String ff = f;
					if (!customStatementUsed)
						ff = getMapping(f);
					if (filterRow.getField(f).getValue() == null) {
						wh.append(AND + dbconfig.getDbQuoteString() + ff + dbconfig.getDbQuoteString() + " IS NULL");
						filterRow.removeField(f);
					} else
						wh.append(AND + dbconfig.getDbQuoteString() + ff + dbconfig.getDbQuoteString() + "=?");
				}
			}
			if (wh.length() > 0)
				sql.append(WHERE + wh.substring(5));
		}

		if (first >= 0 && last >= first) {
			switch (dbconfig.getDbType()) {
			case BASIS_DBMS:
				sql.append(" LIMIT " + (first + 1) + "," + (last - first + 1));
				break;
			case MYSQL_DBMS:
				sql.append(" LIMIT " + first + "," + (last - first + 1));
				break;
			case "MICROSOFT SQL SERVER":
				// OFFSET is available since MS SQL Server 2012 (version 11)
				int dbVersion = Integer
						.parseInt(conn.getMetaData().getDatabaseProductVersion().replaceAll("(\\d+)\\..*", "$1"));
				if (dbVersion >= 11) {
					sql = new StringBuilder("SELECT * FROM (" + sql + ") T ORDER BY (SELECT NULL) OFFSET " + first
							+ " ROWS FETCH NEXT " + (last - first + 1) + " ROWS ONLY");
				} else {
					throw new UnsupportedOperationException("Pagination is not supported or not implemented with the "
							+ dbconfig.getDbType() + " (version " + dbVersion + ") database.");
				}
				break;
			default:
				throw new UnsupportedOperationException("Pagination is not supported or not implemented with the "
						+ dbconfig.getDbType() + " database.");
			}
		}
		dbconfig.setSqlStatement(sql.toString());
		try (java.sql.PreparedStatement prep = conn.prepareStatement(dbconfig.getSqlStatement())) {
			DataRow params = new DataRow();
			if (retrieveParams != null && retrieveParams.getColumnCount() > 0) {
				params = retrieveParams.clone();
			}
			if (filterRow != null && filterRow.getColumnCount() > 0) {
				params.mergeRecord(filterRow);
			}
			if (params.getColumnCount() > 0) {
				setSqlParams(prep, params, params.getFieldNames(), BASIS_DBMS.equals(dbconfig.getDbType()));
			}

			try (java.sql.ResultSet rs = prep.executeQuery()) {
				retrs = new ResultSet();
				retrs.populate(rs, true);
			}
		}
		return retrs;
	}

	private static void checkFilter(DataRow filter) {
		if (filter.contains("%SEARCH")) {
			throw new UnsupportedOperationException("Full text search not implemented yet!");
		}
	}

	private static void checkIndeces(int first, int last) {
		if (first >= 0 && last < first) {
			throw new IllegalArgumentException("Invalid range: last could not be lower than first!");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResultSet validateWrite(DataRow dr) {

		if (dr == null || dr.getColumnCount() == 0) {
			return new ResultSet();
		}
		ResultSet rs = new ResultSet();
		for (String fieldName : dr.getFieldNames()) {
			fieldName = getMapping(fieldName);
			if (!metaData.contains(fieldName) || dbconfig.containsPrimaryKey(fieldName))
				continue;

			try {
				int type = metaData.getFieldType(fieldName);
				if (isChartype(type)) {
					String sSize = metaData.getFieldAttribute(fieldName, "COLUMN_SIZE");
					if (!"".equals(sSize)) {
						int size = Integer.parseInt(sSize);
						if (dr.getFieldAsString(fieldName).length() > size) {
							String errType = ERROR;
							String errMsg = "String is too long for column " + fieldName + ".";
							if (truncateFieldValues) {
								dr.setFieldValue(fieldName, dr.getFieldAsString(fieldName).substring(0, size));
								errType = "INFO";
								errMsg = "\"" + fieldName + "\" was truncated to " + size + " characters.";
							}
							DataRow de = new DataRow();
							de.setFieldValue("FIELD_NAME", Types.VARCHAR, fieldName);
							de.setFieldValue("TYPE", Types.VARCHAR, errType);
							de.setFieldValue("MESSAGE", Types.VARCHAR, errMsg);
							rs.add(de);
						}
					}
				}
			} catch (Exception e1) {
				// do nothing
			}
		}

		return rs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataRow write(DataRow dr) throws Exception {
		ResultSet errors = validateWrite(dr);
		checkErrors(errors);
		return DataRowWriter.write(dr, table, dbconfig, connectionHelper, this::reRetrieve);
	}

	private DataRow reRetrieve(DataRow dr) {
		try {
			DataRow oldfilter = this.getFilter();
			DataRow filterRow = new DataRow();
			for (String f : dbconfig.getPrimaryKeys()) {
				filterRow.setFieldValue(f, dr.getFieldType(f), dr.getField(f).getValue());
			}
			this.setFilter(filterRow);
			ResultSet retrs = this.retrieve(0, 1);
			if (retrs.size() == 1)
				dr = retrs.getItem(0);
			else {
				System.err.println("could not read written record - got more than 1 record back!");
				System.err.println("filter = " + filterRow);
			}

			this.setFilter(oldfilter);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dr;
	}

	// TODO reduce CC of this Method

	private static void checkErrors(ResultSet errors) throws SQLException {
		if (errors.size() > 0) {
			StringBuilder errMsg = new StringBuilder();
			for (int i = 0; i < errors.size(); i++) {
				DataRow dr = errors.get(i);
				if (dr.getFieldAsString("TYPE").equals(ERROR))
					errMsg.append("\n" + dr.getFieldAsString("TYPE") + " on column " + dr.getFieldAsString("FIELD_NAME")
							+ ": " + dr.getFieldAsString("MESSAGE"));
			}
			if (errMsg.length() > 0) {
				throw new SQLException(errMsg.substring(1));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// TODO To be implemented
	@Override
	public ResultSet validateRemove(DataRow dr) {
		return new ResultSet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(DataRow r) throws Exception {
		ResultSet errors = validateRemove(r);
		checkErrors(errors);

		if (dbconfig.isPrimaryKeysEmpty()) {
			throw new SQLException("No primary key definition for table \"" + table + "\"");
		} else if (!r.getFieldNames().containsAll(dbconfig.getPrimaryKeys())) {
			throw new SQLException("Missing primary column for table \"" + table + "\"");
		}

		StringBuilder sql = new StringBuilder(
				"DELETE FROM " + dbconfig.getDbQuoteString() + table + dbconfig.getDbQuoteString() + " ");

		StringBuilder wh = new StringBuilder("");
		for (String pkfieldname : dbconfig.getPrimaryKeys()) {
			wh.append(AND + dbconfig.getDbQuoteString() + getMapping(pkfieldname) + dbconfig.getDbQuoteString() + "=?");
		}
		sql.append(WHERE + wh.substring(5));
		dbconfig.setSqlStatement(sql.toString());
		try (CloseableWrapper<Connection> connw = getConnection();
				PreparedStatement prep = connw.getCloseable().prepareStatement(dbconfig.getSqlStatement())) {
			setSqlParams(prep, r, dbconfig.getPrimaryKeys(), BASIS_DBMS.equals(dbconfig.getDbType()));
			prep.execute();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataRow getAttributesRecord() {
		return attributesRecord.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataRow getNewObjectTemplate(DataRow conditions) {
		return getAttributesRecord();
	}

	/**
	 * Executes a SQL query statement and returns the result as a {@link ResultSet}.
	 *
	 * @param sql    the query statement.
	 * @param params if not null or empty the values from this DataRow will be used
	 *               to set prepared parameters in the sql statement.
	 * @return the query result as {@link ResultSet}.
	 */
	public ResultSet retrieve(String sql, DataRow params) {

		try (CloseableWrapper<Connection> connw = getConnection();
				PreparedStatement prep = connw.getCloseable().prepareStatement(sql)) {
			// Set params if there are any
			if (params != null) {
				setParameters(params, prep);
			}

			return new ResultSet(prep.executeQuery());

		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return null;
	}

	/**
	 * Adds a field mapping. If a custom retrieve SQL statement with alias names is
	 * used, a field mapping can be added.<br>
	 * The field mapping is internally used when the where clause (filters) are
	 * created (on retrieve) and on insert/update and deleting data.
	 *
	 * @param bcFieldName the alias name
	 * @param dbFieldName the table field name
	 */
	public void addMapping(String bcFieldName, String dbFieldName) {
		dbconfig.addMapping(bcFieldName, dbFieldName);
	}

	/**
	 * Returns all defined field mappings.
	 * <p>
	 * <p>
	 * TODO APICHANGE to Map instead of HasMap
	 *
	 * @return a HashMap with field mappings. Where key is the alias name and value
	 *         is the table field name.
	 */
	public Map<String, String> getMappings() {
		return dbconfig.getMappings();
	}

	/**
	 * Gets the mapping for a field. If no mapping exists, then the alias field name
	 * (bcFieldName) is returned.
	 *
	 * @param bcFieldName the alias field name.
	 * @return the table field name.
	 */
	public String getMapping(String bcFieldName) {
		return dbconfig.getMapping(bcFieldName);
	}

	/**
	 * Sets if values of character fields should be automatically truncated on
	 * validation to the field length defined in the table.
	 * <p>
	 * <b>NOTE</b>: only character fields that are not primary keys will be
	 * truncated!
	 *
	 * @param truncate <code>true</code> - all character fields (except primary
	 *                 keys) will be truncated in the
	 *                 {@link #validateWrite(DataRow)} method. <code>false</code> -
	 *                 don't truncate character fields.
	 */
	public void setTruncateFieldValues(boolean truncate) {
		truncateFieldValues = truncate;
	}

	/**
	 * Returns the last executed sql statement. The last executed sql statement is
	 * set after a retrieve, write or delete.
	 * <p>
	 *
	 * @return the last executed sql statement.
	 */
	public String getLastSqlStatement() {
		return dbconfig.getSqlStatement();
	}

	/**
	 * Returns a DataRow with fields (the values are not used) which are allowed for
	 * filtering. This method returns a clone of the attributes record plus
	 * additionally added fields. Additional fields can be added using the
	 * registerFilterField method.
	 *
	 * @return a DataRow with fields used for filtering.
	 * @see #registerFilterField(String fieldName)
	 */
	@Override
	public DataRow getAllowedFilter() {
		if (allowedFilter == null) {
			allowedFilter = getAttributesRecord();
		}

		return allowedFilter;
	}

	/**
	 * Add a field to the allowed filter DataRow.
	 *
	 * @param fieldName
	 * @see #getAllowedFilter()
	 */
	public void registerFilterField(String fieldName) {
		if (allowedFilter == null) {
			allowedFilter = getAttributesRecord();
		}
		try {
			allowedFilter.setFieldValue(fieldName, Types.VARCHAR, null);
		} catch (Exception e) {
			// Do nothing
		}
	}

	private void createAttributesRecord() {
		try (CloseableWrapper<Connection> connw = getConnection()) {
			Connection conn = connw.getCloseable();
			DatabaseMetaData meta = conn.getMetaData();
			this.dbconfig.setDbType(meta.getDatabaseProductName().toUpperCase());
			this.dbconfig.setDbQuoteString(meta.getIdentifierQuoteString());
			this.metaData = new DataRow();
			java.sql.ResultSet sqlMetaData = meta.getPrimaryKeys(null, null, table);

			while (sqlMetaData.next()) {
				dbconfig.addPrimaryKey(sqlMetaData.getString(COLUMN_NAME));
			}

			prepareMetadata(meta);
			sqlMetaData.close();
			// read attributes (for getAttributesRecord() method)

			ResultSet ar;
			try (PreparedStatement stmt = createMetadataStatement(conn)) {
				ar = new ResultSet(stmt.executeQuery());
			}

			for (String field : ar.getColumnNames()) {
				Map<String, Object> attrmap = ar.getColumnMetaData(field);
				try {
					int type = (int) attrmap.get("ColumnType");
					attributesRecord.addDataField(field, type, new DataField(null));
					for (Entry<String, Object> entry : attrmap.entrySet()) {
						String value = null;
						if (entry.getValue() != null) {
							value = entry.getValue().toString();
						}
						attributesRecord.setFieldAttribute(field, entry.getKey(), value);
					}
					
					//check if field is contained in JDBC table meta data
					if (!metaData.contains(field)) {
						//field is not in JDBC meta data for the table, so it's probably a function or JOINed field
						attributesRecord.setFieldAttribute(field, "EDITABLE", "0");
						continue;
					}
					
					if (metaData.getFieldAttributes(field).containsKey("REMARKS"))
						attributesRecord.setFieldAttribute(field, "REMARKS",
								metaData.getFieldAttribute(field, "REMARKS"));
					
					if (dbconfig.containsPrimaryKey(getMapping(field))) {
						attributesRecord.setFieldAttribute(field, "EDITABLE", "2");
					} else {
						attributesRecord.setFieldAttribute(field, "EDITABLE", "1");
					}
						
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			// set default scope
			scopes = new HashMap<>();
			scopes.put("*", attributesRecord.getFieldNames());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void prepareMetadata(DatabaseMetaData meta) throws SQLException {
		java.sql.ResultSet metaColumns = meta.getColumns(null, null, table, null);
		while (metaColumns.next()) {
			if (metaColumns.getMetaData().getColumnCount() > 22) { // IS_AUTOINCREMENT=23 (BBj doesn't
				// support
				// the IS_AUTOINCREMENT property)
				String name = metaColumns.getString(COLUMN_NAME);
				String autoIncrement = metaColumns.getString("IS_AUTOINCREMENT");
				if ("YES".equals(autoIncrement))
					dbconfig.addAutoIncrementKey(name);
			}

			String columnName = metaColumns.getString(COLUMN_NAME);
			try {
				metaData.setFieldValue(columnName, metaColumns.getInt("DATA_TYPE"), null);
			} catch (Exception e1) {
				continue;
			}

			for (int i = 1; i <= metaColumns.getMetaData().getColumnCount(); i++) {
				try {
					if (metaColumns.getString(i) != null) {
						metaData.setFieldAttribute(columnName, metaColumns.getMetaData().getColumnName(i),
								metaColumns.getString(i));
					}
				} catch (Exception e) {
					// do nothing
				}
			}
		}
	}

	private PreparedStatement createMetadataStatement(Connection conn) throws SQLException {
		PreparedStatement stmt;
		if (retrieveSql != null && !retrieveSql.equals("")) {

			switch (dbconfig.getDbType()) {
			case BASIS_DBMS:
				stmt = conn.prepareStatement("SELECT TOP 1 * FROM (" + retrieveSql + ")");
				break;
			case MYSQL_DBMS:
				stmt = conn.prepareStatement(retrieveSql + " LIMIT 0");
				break;
			default:
				stmt = conn.prepareStatement("SELECT * FROM (" + retrieveSql + ") WHERE 1=0");
				break;

			}
			if (retrieveParams != null && retrieveParams.getColumnCount() > 0) {
				try {
					setSqlParams(stmt, retrieveParams, null, BASIS_DBMS.equals(dbconfig.getDbType()));
				} catch (Exception e) {
					// do nothing
				}
			}
		} else {
			if (BASIS_DBMS.equals(dbconfig.getDbType())) {
				stmt = conn.prepareStatement(
						"SELECT TOP 1 * FROM " + dbconfig.getDbQuoteString() + table + dbconfig.getDbQuoteString());
			} else {
				stmt = conn.prepareStatement("SELECT * FROM " + dbconfig.getDbQuoteString() + table
						+ dbconfig.getDbQuoteString() + " WHERE 1=0");
			}
		}
		return stmt;
	}

	@Override
	public BusinessComponent getLookup(String fieldName, DataRow dr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getLookupData(String fieldName, DataRow dr) {
		// TODO Auto-generated method stub
		return null;
	}

}
