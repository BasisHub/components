package com.basiscomponents.bc;

import static com.basiscomponents.db.ExpressionMatcher.getPreparedWhereClauseValues;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.basiscomponents.bc.util.CloseableWrapper;
import com.basiscomponents.bc.util.SqlConnectionHelper;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.DataRowRegexMatcher;
import com.basiscomponents.util.KeyValuePair;

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

  private static final String AND = " AND ";

  private static final String WHERE = " WHERE ";

  private static final String ERROR = "ERROR";

  private static final String BASIS_DBMS = "BASIS DBMS";
  
  private static final String MYSQL_DBMS = "MYSQL";

  private static final String COLUMN_NAME = "COLUMN_NAME";

	private final SqlConnectionHelper connectionHelper;

  private String table;
  private String scope = "";
  private HashMap<String, ArrayList<String>> scopes;
  private ArrayList<String> primaryKeys;
  private ArrayList<String> autoIncrementKeys;
  private DataRow attributesRecord;
  private DataRow allowedFilter;
  private DataRow metaData;
  private String retrieveSql;
  private String sqlStatement;
  private DataRow retrieveParams;
  private Map<String, String> mapping = new HashMap<>();

	private DataRow filter = new DataRow();
  private DataRow fieldSelection;

  private String dbType;
  private String dbQuoteString = "";

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
   * @param driver the name/package of the JDBC driver.
   * @param url database URL to the database.
   * @param user the name of the database user.
   * @param password the password of the database user.
   * @throws ClassNotFoundException thrown if the JDBC driver could not be found.
   */
  public SqlTableBC(String driver, String url, String user, String password)
      throws ClassNotFoundException {
    this.connectionHelper = new SqlConnectionHelper(url, user, password, driver);
  }

  /**
   * Constructor.
   * <p>
   * Creates a new SqlTableBC object using a database connection.
   * <p>
   * <b>NOTE</b>: SqlTableBC will not close the connection (after reading or writing).
   *
   * @param con the database connection to the database.
   * @throws SQLException thrown if the connection status (con.isCloses()) cannot be determined
   */
  public SqlTableBC(Connection con) throws SQLException {
    connectionHelper = new SqlConnectionHelper(con);
  }

  /**
   * Returns a database connection.
   * <p>
   * If {@link #SqlTableBC(Connection con)} was used, the connection passed in the constructor will
   * be returned.<br>
   * Otherwise a new connection, using the database URL, will be created.
   *
   * @return a {@link java.sql.Connection} to the database.
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
   * <b>NOTE</b>: if a custom retrieve SQL statement was set, then this will be used for the
   * metadata generation.
   *
   * @param table the table for reading and writing data.
   * @see #getAttributesRecord()
   * @see #setRetrieveSql(String)
   * @see #setRetrieveSql(String, DataRow)
   */
  public void setTable(String table) {
    this.table = table;
    this.primaryKeys = new ArrayList<>();
    this.autoIncrementKeys = new ArrayList<>();
    this.attributesRecord = new DataRow();
    createAttributesRecord();
  }

  /**
   * Sets a custom retrieve SQL statement.
   * <p>
   * A custom retrieve SQL statement can be set when a more complex select statement is needed (f.g.
   * to retrieve foreign key values). The SQL statement can be a prepared statement. Parameters can
   * be set with {@link #setRetrieveParameters(DataRow)}.
   * <p>
   * <b>NOTE</b>: if a custom retrieve SQL statement is used, a table name need to be set (using
   * {@link #setTable(String)}) for writing/removing data from the main table.
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
   * A custom retrieve SQL statement can be set when a more complex select statement is needed (f.g.
   * to retrieve foreign key values).<br>
   * <p>
   * Prepared values (question marks in the SQL statement) can be passed within a DataRow.<br>
   * The field count and field type in the DataRow should match the count and the type of the
   * question mark in the prepared select statement.
   * <p>
   * <b>NOTE</b>: if a custom retrieve SQL statement is used, a table name need to be set (using
   * {@link #setTable(String)}) for writing/removing data from the main table.
   *
   * @param sql the custom retrieve SQL statement.
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
   * <b>NOTE</b>: the quote string character is returned from the JDBC driver and it is determined
   * in the setTable(String) method.
   *
   * @return the database specific quote string character.
   */
  public String getDBQuoteString() {
    return dbQuoteString;
  }

  /**
   * Returns a previously set filter. May be null.
   *
   * @return a DataRow with filter fields.
   * @see #setFilter(DataRow)
   */
	public DataRow getFilter() {
		regexes.forEach((k, v) -> filter.addDataField(k, v));
		return this.filter;
	}

  /**
   * {@inheritDoc}
   */
	public void setFilter(final DataRow filter) {
		if (filter == null) {
			this.filter = new DataRow();
		} else {
			DataRow f = filter.clone();
			Map<String, DataField> rgxs = f.getFieldNames().stream()
					.map(x -> new KeyValuePair<String, DataField>(x, filter.getField(x)))
					.filter(x -> x.getValue().getString().startsWith("regex:"))
					// .peek(x -> System.out.println(x.getValue().getString()))
					.collect(Collectors.toMap(KeyValuePair::getKey, KeyValuePair::getValue));

			this.regexes = rgxs;
			rgxs.forEach((k, v) -> f.removeField(k));
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
  public void setFieldSelection(DataRow fieldSelection) {
    this.fieldSelection = fieldSelection;
  }

  /**
   * {@inheritDoc}
   */
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
   * The scope definition is a HashMap with the scope name as key and an ArrayList with field names
   * as value.
   *
   * @param scopes the HashMap with the scope definitions
   */
  public void setScopeDef(HashMap<String, ArrayList<String>> scopes) {
    this.scopes = scopes;
  }

  /**
   * {@inheritDoc}
   */
  public ResultSet retrieve() throws Exception {
    return retrieve(-1, -1);
  }

  /**
   * {@inheritDoc}
   */
  // TODO Still too complex
	public ResultSet retrieve(int first, int last) throws Exception {
		if (first >= 0 && last < first) {
			throw new IllegalArgumentException("Invalid range: last could not be lower than first!");
		}

		if (filter.contains("%SEARCH")) {
			throw new UnsupportedOperationException("Full text search not implemented yet!");
		}

		DataRow filterRow = filter.clone();
		List<DataRowRegexMatcher> regexmatchers = null;
		if (regexes != null) {
		regexmatchers = regexes.entrySet().stream()
				.map(x -> new DataRowRegexMatcher(x.getKey(), x.getValue().getString()))
				.collect(Collectors.toList());
		}
		
		ResultSet retrs = null;

		try (CloseableWrapper<Connection> connw = getConnection()) {
			Connection conn = connw.getCloseable();
			StringBuilder sql;

			LinkedHashSet<String> fields = new LinkedHashSet<>();
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
					sqlfields.append("," + dbQuoteString + field + dbQuoteString);
				}
				sqlfields = new StringBuilder(sqlfields.substring(1));
			}

			if (customStatementUsed) {
				sql = new StringBuilder("SELECT " + sqlfields + " FROM (" + retrieveSql + ") ");
				if (MYSQL_DBMS.equals(dbType))
					sql.append(" as s ");
			} else
				sql = new StringBuilder("SELECT " + sqlfields + " FROM " + dbQuoteString + table + dbQuoteString);

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
							wh.append(AND + dbQuoteString + ff + dbQuoteString + " IS NULL");
							filterRow.removeField(f);
						} else
							wh.append(AND + dbQuoteString + ff + dbQuoteString + "=?");
					}
				}
				if (wh.length() > 0)
					sql.append(WHERE + wh.substring(5));
			}

			if (first >= 0 && last >= first) {
				switch (dbType) {
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
					}
					else {
						throw new UnsupportedOperationException(
								"Pagination is not supported or not implemented with the " + dbType + " (version "
										+ dbVersion + ") database.");}
					break;
				default:
					throw new UnsupportedOperationException(
							"Pagination is not supported or not implemented with the " + dbType + " database.");
				}
			}

			sqlStatement = sql.toString();

			try (PreparedStatement prep = conn.prepareStatement(sqlStatement)) {
				DataRow params = new DataRow();
				if (retrieveParams != null && retrieveParams.getColumnCount() > 0) {
					params = retrieveParams.clone();
				}
				if (filterRow != null && filterRow.getColumnCount() > 0) {
					params.mergeRecord(filterRow);
				}
				if (params.getColumnCount() > 0) {
					setSqlParams(prep, params, params.getFieldNames(), BASIS_DBMS.equals(dbType));
				}

				try (java.sql.ResultSet rs = prep.executeQuery()) {
					retrs = new ResultSet();
					retrs.populate(rs, true);
				}
			}
		}

		// Set the generated meta attributes to the first record
		if (retrs.size() > 0) {
			Iterator<DataRow> iterator = retrs.iterator();
			if (regexmatchers != null && !regexmatchers.isEmpty()) {
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

  /**
   * {@inheritDoc}
   */
  public ResultSet validateWrite(DataRow dr) {

		if (dr == null || dr.getColumnCount() == 0) {
			return new ResultSet();
		}
		ResultSet rs = new ResultSet();
    for (String fieldName : dr.getFieldNames()) {
      fieldName = getMapping(fieldName);
      if (!metaData.contains(fieldName) || primaryKeys.contains(fieldName))
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
  // TODO reduce CC of this Method
  public DataRow write(DataRow r) throws Exception {
    ResultSet errors = validateWrite(r);
    checkWriteError(errors);

    boolean pkPresent = isPrimaryKeyPresent(r);

    try (CloseableWrapper<Connection> connw = getConnection()) {
      Connection conn = connw.getCloseable();
      // Read table field names from the table (not from getAttributesRecord()).
      // The field may differ if a custom retrieve sql statement is used.
      DatabaseMetaData meta = conn.getMetaData();
      java.sql.ResultSet rs = meta.getColumns(null, null, table, null);
      List<String> tableFields = new ArrayList<>();
      while (rs.next()) {
        tableFields.add(rs.getString(COLUMN_NAME));
      }

      StringBuilder sql = new StringBuilder();
      int affectedRows = 0;
      DataRow ret = r.clone();
      PreparedStatement prep;

      // update (Try an update an check affected rows. If there are no (0) affected
      // rows, then make an
      // insert.)
      if (pkPresent) {
        sql = new StringBuilder("UPDATE " + dbQuoteString + table + dbQuoteString + " SET ");

        List<String> fields = new ArrayList<>();

        StringBuilder update = new StringBuilder();
        for (String field : r.getFieldNames()) {
          String field2 = getMapping(field);
          if (primaryKeys.contains(field))
            continue;
          if (tableFields.contains(field2)) {
            fields.add(field);
            update.append("," + dbQuoteString + field2 + dbQuoteString + "=?");
          }
        }

        if (update.length() > 0) {
          // if the fields are _only_ fields that are part of the primary key
          // (e.g. a table with PK being a compount, not having fields outside the PK)
          // then update would be "" and this portion would fail

          sql.append(update.substring(1));

          StringBuilder wh = new StringBuilder();
          for (String pkfield : primaryKeys) {
            wh.append(AND + dbQuoteString + getMapping(pkfield) + dbQuoteString + "=?");
            fields.add(pkfield);
          }
          sql.append(WHERE + wh.substring(5));

          prep = conn.prepareStatement(sql.toString());
          setSqlParams(prep, r, fields, BASIS_DBMS.equals(dbType));

          affectedRows = prep.executeUpdate();
          prep.close();
        } else {
          /// so now we have to do a SELECT to see if the record is there, as we can't
          /// check with
          /// update
          sql = new StringBuilder(
              "SELECT COUNT(*) AS C FROM " + dbQuoteString + table + dbQuoteString);
          StringBuilder wh = new StringBuilder("");
          for (String pkfield : primaryKeys) {
            wh.append(AND + dbQuoteString + getMapping(pkfield) + dbQuoteString + "=?");
            fields.add(pkfield);
          }
          if (wh.length() > 0) {
            sql.append(WHERE + wh.substring(5));
          }
          prep = conn.prepareStatement(sql.toString());
          setSqlParams(prep, r, fields, BASIS_DBMS.equals(dbType));
          java.sql.ResultSet jrs = prep.executeQuery();
          ResultSet retrs = new ResultSet();
          retrs.populate(jrs, true);
          affectedRows = retrs.get(0).getFieldAsNumber("C").intValue();
          prep.close();
        }
      }

      // insert
      Boolean inserted = false;
      if (!pkPresent || affectedRows == 0) {
        sql = new StringBuilder("INSERT INTO " + dbQuoteString + table + dbQuoteString + " (");

        List<String> fields = new ArrayList<>();
        StringBuilder keys = new StringBuilder("");
        StringBuilder values = new StringBuilder("");
        for (String field : r.getFieldNames()) {
          String field2 = getMapping(field);
          if (tableFields.contains(field2)) {
            fields.add(field);
            keys.append("," + dbQuoteString + field2 + dbQuoteString);
            values.append(",?");
          }
        }
        sql.append(keys.substring(1) + ") VALUES(" + values.substring(1) + ")");

        prep = conn.prepareStatement(sql.toString(), PreparedStatement.RETURN_GENERATED_KEYS);
        setSqlParams(prep, r, fields, BASIS_DBMS.equals(dbType));

        affectedRows = prep.executeUpdate();
        inserted = affectedRows > 0;

        // get generated keys
        if (affectedRows > 0) {
          try (java.sql.ResultSet gkeys = prep.getGeneratedKeys()) {
            if (gkeys.next()) {
              for (int i = 0; i < gkeys.getMetaData().getColumnCount(); i++) {
                String name = autoIncrementKeys.get(i);
                ret.setFieldValue(name, gkeys.getObject(i + 1));
              }
              pkPresent = isPkPresent(pkPresent, ret);
            }
          }
        }

        prep.close();
      }

      sqlStatement = sql.toString();

      // reload from the database
      if (pkPresent) {
        DataRow oldfilter = this.filter;
        DataRow filterRow = new DataRow();
        for (String f : primaryKeys) {
          filterRow.setFieldValue(f, ret.getFieldType(f), ret.getField(f).getValue());
        }
        this.setFilter(filterRow);
        ResultSet retrs = this.retrieve(0, 1);
        if (retrs.size() == 1)
          ret = retrs.getItem(0);
        else {
          System.err.println("could not read written record - got more than 1 record back!");
          System.err.println("filter = " + filterRow);
        }

        this.setFilter(oldfilter);
      }

      if (inserted) {
        ret.setAttribute("CREATED", inserted.toString());
      }
      return ret;

    }
  }

  private boolean isPkPresent(boolean pkPresent, DataRow ret) {
    if (primaryKeys != null && !primaryKeys.isEmpty()) {
      ArrayList<String> list = new ArrayList<>();
      for (String field : ret.getFieldNames()) {
        list.add(getMapping(field));
      }
      pkPresent = list.containsAll(primaryKeys);
    }
    return pkPresent;
  }

  private void checkWriteError(ResultSet errors) throws SQLException {
    if (errors.size() > 0) {
      StringBuilder errMsg = new StringBuilder();
      for (int i = 0; i < errors.size(); i++) {
        DataRow dr = errors.get(i);
        if (dr.getFieldAsString("TYPE").equals(ERROR))
          errMsg.append("\n" + dr.getFieldAsString("TYPE") + " on column "
              + dr.getFieldAsString("FIELD_NAME") + ": " + dr.getFieldAsString("MESSAGE"));
      }
      if (errMsg.length() > 0) {
        throw new SQLException(errMsg.substring(1));
      }
    }
  }

  /**
   * @param r
   * @return
   */
  private boolean isPrimaryKeyPresent(DataRow r) {
    boolean pkPresent = false;
    pkPresent = isPkPresent(pkPresent, r);
    return pkPresent;
  }

  /**
   * {@inheritDoc}
   */
  public ResultSet validateRemove(DataRow dr) {
    return new ResultSet();
  }

  /**
   * {@inheritDoc}
   */
  public void remove(DataRow r) throws Exception {
    ResultSet errors = validateRemove(r);
    if (errors.size() > 0) {
      StringBuilder errMsg = new StringBuilder();
      for (int i = 0; i < errors.size(); i++) {
        DataRow dr = errors.get(i);
        if (dr.getFieldAsString("TYPE").equals(ERROR))
          errMsg.append("\n" + dr.getFieldAsString("TYPE") + " on column "
              + dr.getFieldAsString("FIELD_NAME") + ": " + dr.getFieldAsString("MESSAGE"));
      }
      if (errMsg.length() > 0) {
        throw new SQLException(errMsg.substring(1));
      }
    }

    if (primaryKeys == null || primaryKeys.isEmpty()) {
      throw new SQLException("No primary key definition for table \"" + table + "\"");
    } else if (!r.getFieldNames().containsAll(primaryKeys)) {
      throw new SQLException("Missing primary column for table \"" + table + "\"");
    }

    StringBuilder sql =
        new StringBuilder("DELETE FROM " + dbQuoteString + table + dbQuoteString + " ");

    StringBuilder wh = new StringBuilder("");
    for (String pkfieldname : primaryKeys) {
      wh.append(AND + dbQuoteString + getMapping(pkfieldname) + dbQuoteString + "=?");
    }
    sql.append(WHERE + wh.substring(5));
    sqlStatement = sql.toString();
    try (CloseableWrapper<Connection> connw = getConnection();
        PreparedStatement prep = connw.getCloseable().prepareStatement(sqlStatement)) {
      setSqlParams(prep, r, primaryKeys, BASIS_DBMS.equals(dbType));
      prep.execute();
    }

  }

  /**
   * {@inheritDoc}
   */
  public DataRow getAttributesRecord() {
    return attributesRecord.clone();
  }

  /**
   * {@inheritDoc}
   */
  public DataRow getNewObjectTemplate(DataRow conditions) {
    return getAttributesRecord();
  }

  /**
   * Executes a SQL query statement and returns the result as a {@link ResultSet}.
   *
   * @param sql the query statement.
   * @param params if not null or empty the values from this DataRow will be used to set prepared
   *        parameters in the sql statement.
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
   * Adds a field mapping. If a custom retrieve SQL statement with alias names is used, a field
   * mapping can be added.<br>
   * The field mapping is internally used when the where clause (filters) are created (on retrieve)
   * and on insert/update and deleting data.
   *
   * @param bcFieldName the alias name
   * @param dbFieldName the table field name
   */
  public void addMapping(String bcFieldName, String dbFieldName) {
    mapping.put(bcFieldName, dbFieldName);
  }

  /**
   * Returns all defined field mappings.
   * <p>
   * <p>
   * TODO APICHANGE to Map instead of HasMap
   *
   * @return a HashMap with field mappings. Where key is the alias name and value is the table field
   *         name.
   */
  public HashMap<String, String> getMappings() {
    return new HashMap<>(mapping);
  }

  /**
   * Gets the mapping for a field. If no mapping exists, then the alias field name (bcFieldName) is
   * returned.
   *
   * @param bcFieldName the alias field name.
   * @return the table field name.
   */
  public String getMapping(String bcFieldName) {
    String dbFieldName = mapping.get(bcFieldName);
    if (dbFieldName == null)
      return bcFieldName;
    return dbFieldName;
  }

  /**
   * Sets if values of character fields should be automatically truncated on validation to the field
   * length defined in the table.
   * <p>
   * <b>NOTE</b>: only character fields that are not primary keys will be truncated!
   *
   * @param truncate <code>true</code> - all character fields (except primary keys) will be
   *        truncated in the {@link #validateWrite(DataRow)} method. <code>false</code> - don't
   *        truncate character fields.
   */
  public void setTruncateFieldValues(boolean truncate) {
    truncateFieldValues = truncate;
  }

  /**
   * Returns the last executed sql statement. The last executed sql statement is set after a
   * retrieve, write or delete.
   * <p>
   *
   * @return the last executed sql statement.
   */
  public String getLastSqlStatement() {
    return sqlStatement;
  }

  /**
   * Returns a DataRow with fields (the values are not used) which are allowed for filtering. This
   * method returns a clone of the attributes record plus additionally added fields. Additional
   * fields can be added using the registerFilterField method.
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
      this.dbType = meta.getDatabaseProductName().toUpperCase();
      this.dbQuoteString = meta.getIdentifierQuoteString();
      this.metaData = new DataRow();
      java.sql.ResultSet sqlMetaData = meta.getPrimaryKeys(null, null, table);

      while (sqlMetaData.next()) {
        primaryKeys.add(sqlMetaData.getString(COLUMN_NAME));
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
          if (metaData.getFieldAttributes(field).containsKey("REMARKS"))
            attributesRecord.setFieldAttribute(field, "REMARKS",
                metaData.getFieldAttribute(field, "REMARKS"));
          if (primaryKeys.contains(getMapping(field)))
            attributesRecord.setFieldAttribute(field, "EDITABLE", "2");
        } catch (Exception ex) {
          // do nothing
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
          autoIncrementKeys.add(name);
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
  private static void setSqlParams(PreparedStatement prep, DataRow dr, List<String> fields,
      boolean isBasisDBMS) throws Exception {
    if (prep == null || dr == null) {
      return;
    }

    if (fields == null) {
      fields = dr.getFieldNames();
    }

    int index = 1;
    for (String field : fields) {
      Integer type;
      DataField o;

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
        if (isBasisDBMS && type == Types.CHAR) {
          prep.setString(index, "");
        } else {
          prep.setNull(index, type);
        }
        index++;
        continue;
      } else if (o.getValue() instanceof String && ((String) o.getValue()).startsWith("cond:")) {
        DataRow drv = getPreparedWhereClauseValues(((String) o.getValue()).substring(5), type);
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

  private static void setPreparedStatementType(PreparedStatement prep, boolean isBasisDBMS,
      int index, Integer type, DataField o) throws SQLException {
    switch (type) {
      case Types.NUMERIC:
        if (isBasisDBMS && o.getValue() == null)
          prep.setBigDecimal(index, new BigDecimal(0));
        else
          prep.setBigDecimal(index, o.getBigDecimal());
        break;
      case Types.INTEGER:
        if (isBasisDBMS && o.getValue() == null)
          prep.setInt(index, 0);
        else
          prep.setInt(index, o.getInt());
        break;
      case Types.DOUBLE:
        prep.setDouble(index, o.getDouble());
        break;
      case Types.LONGNVARCHAR:
      case Types.CHAR:
      case Types.VARCHAR:
        if (isBasisDBMS && o.getValue() == null)
          prep.setString(index, "");
        else
          prep.setString(index, o.getString());
        break;
      case Types.BIT:
      case Types.BOOLEAN:
        prep.setBoolean(index, o.getBoolean());
        break;
      case Types.DATE:
        prep.setDate(index, o.getDate());
        break;
      case Types.TIMESTAMP:
        prep.setTimestamp(index, o.getTimestamp());
        break;
      case Types.OTHER:
        /// this is an auto-generated key. set as string and hope for the best
        if (isBasisDBMS && o.getValue() == null)
          prep.setString(index, "");
        else
          prep.setString(index, o.getString());
        break;
      default:
        System.err.println(
            "WARNING: using prep.setObject(object) will fail if there is no equivalent SQL type for the given object");
        prep.setObject(index, o.getValue());
    }

  }

  private PreparedStatement createMetadataStatement(Connection conn) throws SQLException {
    PreparedStatement stmt;

    if (retrieveSql != null && !retrieveSql.equals("")) {
    	
      switch (dbType) {
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
          setSqlParams(stmt, retrieveParams, null, BASIS_DBMS.equals(dbType));
        } catch (Exception e) {
          // do nothing
        }
      }
    } else {
      if (BASIS_DBMS.equals(dbType)) {
        stmt =
            conn.prepareStatement("SELECT TOP 1 * FROM " + dbQuoteString + table + dbQuoteString);
      } else {
        stmt = conn.prepareStatement(
            "SELECT * FROM " + dbQuoteString + table + dbQuoteString + " WHERE 1=0");
      }
    }
    return stmt;
  }

  private void setParameters(DataRow params, PreparedStatement prep) {
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

  private static boolean isChartype(int type) {
    return type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR
        || type == Types.NCHAR || type == Types.NVARCHAR || type == Types.LONGNVARCHAR
        || type == Types.CLOB;
  }
}
