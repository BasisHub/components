package com.basiscomponents.bc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ExpressionMatcher;
import com.basiscomponents.db.ResultSet;

/**
 * <h1>SqlTableBC represents a table in a database.</h1>
 * <p>
 * Data can be read/written from/into the database using a JDBC driver.<br>
 * {@link #setTable(String)} sets the table, which should be used for reading/writing data.
 * <p>
 * SqlTableBC is using {@link DataRow} for a single data row representation and {@link ResultSet}
 * for a set of data rows.
 * <p>
 * The developer has to work with DataRow's and ResultSet's only. No SQL knowledge is required.
 * <p>
 * SqlTableBC is using the JDBC metadata to determine the table information (such as column names,
 * column types, field lengths, and so on).
 * 
 * @author vkolarov
 *
 */
public class SqlTableBC implements BusinessComponent {

  private static final String COLUMN_NAME = "COLUMN_NAME";
  private String url;
  private String user;
  private String password;
  private String table;
  private String scope = "";
  private HashMap<String, ArrayList<String>> scopes;
  private ArrayList<String> primaryKeys;
  private ArrayList<String> autoIncrementKeys;
  private DataRow attributesRecord;
  private DataRow allowedFilter;
  private DataRow metaData;
  private Connection connection;
  private String retrieveSql;
  private String sqlStatement;
  private DataRow retrieveParams;
  private HashMap<String, String> mapping = new HashMap<>();

  private DataRow Filter;
  private DataRow fieldSelection;

  private String dbType;
  private String dbQuoteString = "";

  private Boolean truncateFieldValues = false;

  /**
   * Constructor.
   * <p>
   * Creates a new SqlTableBC object using a database URL.
   * 
   * @param url database URL to the database.
   */
  public SqlTableBC(String url) {
    this.url = url;
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
    this.url = url;
    this.user = user;
    this.password = password;

    Class.forName(driver);
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
    if (con != null && !con.isClosed()) {
      connection = con;
    }
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
  private Connection getConnection() throws SQLException {
    if (connection != null)
      return connection;

    if (user == null || password == null)
      return DriverManager.getConnection(url);
    else
      return DriverManager.getConnection(url, user, password);
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

    Connection conn = null;
    try {
      conn = getConnection();

      DatabaseMetaData meta = conn.getMetaData();
      dbType = meta.getDatabaseProductName().toUpperCase();
      dbQuoteString = meta.getIdentifierQuoteString();
      metaData = new DataRow();
      java.sql.ResultSet rs = meta.getPrimaryKeys(null, null, table);

      while (rs.next()) {
        String primaryKey = rs.getString(COLUMN_NAME);
        primaryKeys.add(primaryKey);
      }

      rs = meta.getColumns(null, null, table, null);
      while (rs.next()) {
        if (rs.getMetaData().getColumnCount() > 22) { // IS_AUTOINCREMENT=23 (BBj doesn't support
                                                      // the IS_AUTOINCREMENT property)
          String name = rs.getString(COLUMN_NAME);
          String autoIncrement = rs.getString("IS_AUTOINCREMENT");
          if ("YES".equals(autoIncrement))
            autoIncrementKeys.add(name);
        }

        String columnName = rs.getString(COLUMN_NAME);
        try {
          metaData.setFieldValue(columnName, rs.getInt("DATA_TYPE"), null);
        } catch (Exception e1) {
          continue;
        }

        for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
          if (rs.getString(i) != null) {
            try {
              metaData.setFieldAttribute(columnName, rs.getMetaData().getColumnName(i),
                  rs.getString(i));
            } catch (Exception e) {
              // do nothing
            }
          }
        }
      }

      // read attributes (for getAttributesRecord() method)
      PreparedStatement stmt;
      ResultSet ar;
      if (retrieveSql != null && !retrieveSql.equals("")) {
        stmt = conn.prepareStatement("SELECT * FROM (" + retrieveSql + ") WHERE 1=0");
        if (retrieveParams != null && retrieveParams.getColumnCount() > 0) {
          try {
            setSqlParams(stmt, retrieveParams, null);
          } catch (Exception e) {
          }
        }
      } else {
        stmt = conn.prepareStatement(
            "SELECT * FROM " + dbQuoteString + table + dbQuoteString + " WHERE 1=0");
      }
      ar = new ResultSet(stmt.executeQuery());
      for (String field : ar.getColumnNames()) {
		Map<String, Object> attrmap = ar.getColumnMetaData(field);
        try {
          int type = (int) attrmap.get("ColumnType");
          attributesRecord.addDataField(field, type, new DataField(null));
          for (String attr : attrmap.keySet()) {
            String value = null;
            if (attrmap.get(attr) != null)
              value = attrmap.get(attr).toString();
            attributesRecord.setFieldAttribute(field, attr, value);
          }
          if (metaData.getFieldAttributes(field).containsKey("REMARKS"))
            attributesRecord.setFieldAttribute(field, "REMARKS",
                metaData.getFieldAttribute(field, "REMARKS"));
          if (primaryKeys.contains(getMapping(field)))
            attributesRecord.setFieldAttribute(field, "EDITABLE", "2");
        } catch (Exception ex) {

        }
				stmt.close();
      }

      // set default scope
      scopes = new HashMap<>();
      scopes.put("*", attributesRecord.getFieldNames());
    } catch (SQLException e) {
      // TODO real logging preferred
      e.printStackTrace();
    } finally {
      if (connection == null && conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }
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
    return this.Filter;
  }


  /**
   * {@inheritDoc}
   */
  public void setFilter(DataRow filter) {
    this.Filter = filter;
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
  // FIXME reduce complexity of this method. See:
  // https://blog.sonarsource.com/cognitive-complexity-because-testability-understandability
  public ResultSet retrieve(int first, int last) throws Exception {
    if (first >= 0 && last < first) {
      // TODO introduce more specific Exception
      throw new Exception("Invalid range: last could not be lower than first!");
    }

    if (Filter != null && Filter.contains("%SEARCH")) {
      // TODO introduce more specific Exception
      throw new Exception("Full text search not implemented yet!");
    }

    DataRow filter = Filter;
    if (filter != null)
      filter = filter.clone();

    ResultSet result = null;
    Connection conn = null;
    try {
      conn = getConnection();
      String sql;

      LinkedHashSet<String> fields = new LinkedHashSet<String>();
      if ((this.fieldSelection == null || this.fieldSelection.getFieldNames().size() == 0)
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

      if (customStatementUsed)
        sql = "SELECT " + sqlfields + " FROM (" + retrieveSql + ")";
      else
        sql = "SELECT " + sqlfields + " FROM " + dbQuoteString + table + dbQuoteString;

      if (filter != null && filter.getFieldNames().size() > 0) {
        StringBuilder wh = new StringBuilder("");
        for (String f : filter.getFieldNames()) {
          if (filter.getFieldAsString(f).startsWith("cond:")) {
            wh.append(" AND (" + com.basiscomponents.db.ExpressionMatcher
                .generatePreparedWhereClause(f, filter.getFieldAsString(f).substring(5)) + ")");
          } else {
            String ff = f;
            if (!customStatementUsed)
              ff = getMapping(f);
            if (filter.getField(f).getValue() == null) {
              wh.append(" AND " + dbQuoteString + ff + dbQuoteString + " IS NULL");
              filter.removeField(f);
            } else
              wh.append(" AND " + dbQuoteString + ff + dbQuoteString + "=?");
          }
        }
        if (wh.length() > 0)
          sql += " WHERE " + wh.substring(5);
      }

      if (first >= 0 && last >= first) {
        switch (dbType) {
          case "BASIS DBMS":
            sql += " LIMIT " + (first + 1) + "," + (last - first + 1);
            break;
          case "MYSQL":
            sql += " LIMIT " + first + "," + (last - first + 1);
            break;
          case "MICROSOFT SQL SERVER":
            // OFFSET is available since MS SQL Server 2012 (version 11)
            int dbVersion = Integer.parseInt(
                conn.getMetaData().getDatabaseProductVersion().replaceAll("(\\d+)\\..*", "$1"));
            if (dbVersion >= 11)
              sql = "SELECT * FROM (" + sql + ") T ORDER BY (SELECT NULL) OFFSET " + first
                  + " ROWS FETCH NEXT " + (last - first + 1) + " ROWS ONLY";
            else
              throw new Exception("Pagination is not supported or not implemented with the "
                  + dbType + " (version " + dbVersion + ") database.");
            break;
          default:
            throw new Exception(
                "Pagination is not supported or not implemented with the " + dbType + " database.");
        }
      }

			sqlStatement = sql;
			try (PreparedStatement prep = conn.prepareStatement(sql)) {

				DataRow params = new DataRow();
				if (retrieveParams != null && retrieveParams.getColumnCount() > 0)
					params = retrieveParams.clone();
				if (filter != null && filter.getColumnCount() > 0)
					params.mergeRecord(filter);
				if (params.getColumnCount() > 0)
					setSqlParams(prep, params, params.getFieldNames());

				java.sql.ResultSet rs = prep.executeQuery();
				result = new ResultSet();
				result.populate(rs, true);
			}
    } catch (SQLException ex) {
      throw ex;
    } finally {
      if (connection == null && conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }


    // Set the generated meta attributes to the first record
    if (result.size() > 0) {
      DataRow dr = result.get(0);
      for (String field : attributesRecord.getFieldNames()) {
        if (dr.contains(field))
          dr.setFieldAttributes(field, attributesRecord.getFieldAttributes(field));
      }
    }

    return result;
  }


  /**
   * {@inheritDoc}
   */
  public ResultSet validateWrite(DataRow dr) {
    ResultSet rs = new ResultSet();

    if (dr == null || dr.getColumnCount() == 0)
      return rs;

    for (String fieldName : dr.getFieldNames()) {
      fieldName = getMapping(fieldName);
      if (!metaData.contains(fieldName) || primaryKeys.contains(fieldName))
        continue;

      try {
        int type = metaData.getFieldType(fieldName);
        if (type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR
            || type == Types.NCHAR || type == Types.NVARCHAR || type == Types.LONGNVARCHAR
            || type == Types.CLOB) {
          String sSize = metaData.getFieldAttribute(fieldName, "COLUMN_SIZE");
          if (!"".equals(sSize)) {
            int size = Integer.parseInt(sSize);
            if (dr.getFieldAsString(fieldName).length() > size) {
              String errType = "ERROR";
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
        // continue;
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
    if (errors.size() > 0) {
      StringBuilder errMsg = new StringBuilder();
      for (int i = 0; i < errors.size(); i++) {
        DataRow dr = errors.get(i);
        if (dr.getFieldAsString("TYPE").equals("ERROR"))
          errMsg.append("\n" + dr.getFieldAsString("TYPE") + " on column "
              + dr.getFieldAsString("FIELD_NAME") + ": " + dr.getFieldAsString("MESSAGE"));
      }
      if (errMsg.length() > 0)
        throw new Exception(errMsg.substring(1));
    }

    boolean pkPresent = false;
    if (primaryKeys != null && !primaryKeys.isEmpty()) {
      // Use mapped field names to check if r contains the primary key field(s)
      ArrayList<String> list = new ArrayList<>();
      for (String field : r.getFieldNames()) {
        list.add(getMapping(field));
      }
      pkPresent = list.containsAll(primaryKeys);
    }

    Connection conn = getConnection();

    // Read table field names from the table (not from getAttributesRecord()).
    // The field may differ if a custom retrieve sql statement is used.
    DatabaseMetaData meta = conn.getMetaData();
    java.sql.ResultSet rs = meta.getColumns(null, null, table, null);
    ArrayList<String> tableFields = new ArrayList<>();
    while (rs.next()) {
      tableFields.add(rs.getString(COLUMN_NAME));
    }

    String sql = "";
    int affectedRows = 0;
    DataRow ret = r.clone();
    PreparedStatement statement;


    // update (Try an update an check affected rows. If there are no (0) affected rows, then make an
    // insert.)
    if (pkPresent) {
      sql = "UPDATE " + dbQuoteString + table + dbQuoteString + " SET ";

      ArrayList<String> fields = new ArrayList<String>();

      StringBuffer update = new StringBuffer("");
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

        sql += update.substring(1);

        StringBuilder wh = new StringBuilder("");
        for (String pkfield : primaryKeys) {
          wh.append(" AND " + dbQuoteString + getMapping(pkfield) + dbQuoteString + "=?");
          fields.add(pkfield);
        }
        sql += " WHERE " + wh.substring(5);

        statement = conn.prepareStatement(sql);
        setSqlParams(statement, r, fields);

        affectedRows = statement.executeUpdate();
        statement.close();
      } else {
        /// so now we have to do a SELECT to see if the record is there, as we can't check with
        /// update
        sql = "SELECT COUNT(*) AS C FROM " + dbQuoteString + table + dbQuoteString;
        StringBuilder wh = new StringBuilder("");
        for (String pkfield : primaryKeys) {
          wh.append(" AND " + dbQuoteString + getMapping(pkfield) + dbQuoteString + "=?");
          fields.add(pkfield);
        }
        if (wh.length() > 0)
          sql += " WHERE " + wh.substring(5);
        statement = conn.prepareStatement(sql);
        setSqlParams(statement, r, fields);
        java.sql.ResultSet jrs = statement.executeQuery();
        ResultSet retrs = new ResultSet();
        retrs.populate(jrs, true);
        affectedRows = retrs.get(0).getFieldAsNumber("C").intValue();
        statement.close();
      }
    }

    // insert
    Boolean inserted = false;
    if (!pkPresent || affectedRows == 0) {
      sql = "INSERT INTO " + dbQuoteString + table + dbQuoteString + " (";

      ArrayList<String> fields = new ArrayList<String>();
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
      sql += keys.substring(1) + ") VALUES(" + values.substring(1) + ")";

      statement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
      setSqlParams(statement, r, fields);

      affectedRows = statement.executeUpdate();
      inserted = affectedRows > 0;

      // get generated keys
      if (affectedRows > 0) {
        java.sql.ResultSet gkeys = statement.getGeneratedKeys();
        if (gkeys.next()) {
          for (int i = 0; i < gkeys.getMetaData().getColumnCount(); i++) {
            String name = autoIncrementKeys.get(i);
            ret.setFieldValue(name, gkeys.getObject(i + 1));
          }
          if (primaryKeys != null && primaryKeys.size() > 0) {
            ArrayList<String> list = new ArrayList<String>();
            for (String field : ret.getFieldNames()) {
              list.add(getMapping(field));
            }
            pkPresent = list.containsAll(primaryKeys);
          }
        }
      }

      statement.close();
    }

    sqlStatement = sql;

    // reload from the database
    if (pkPresent) {
      DataRow oldfilter = this.Filter;
      DataRow filter = new DataRow();
      for (String f : primaryKeys) {
        filter.setFieldValue(f, ret.getFieldType(f), ret.getField(f).getValue());
      }
      this.setFilter(filter);
      ResultSet retrs = this.retrieve(0, 1);
      if (retrs.size() == 1)
        ret = retrs.getItem(0);
      else {
        System.err.println("could not read written record - got more than 1 record back!");
        System.err.println("filter = " + filter);
      }

      this.setFilter(oldfilter);
    }


    if (connection == null && conn != null) {
      try {
        conn.close();
      } catch (SQLException ex) {
      }
    }

    if (inserted)
      ret.setAttribute("CREATED", inserted.toString());

    return ret;
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
        if (dr.getFieldAsString("TYPE").equals("ERROR"))
          errMsg.append("\n" + dr.getFieldAsString("TYPE") + " on column "
              + dr.getFieldAsString("FIELD_NAME") + ": " + dr.getFieldAsString("MESSAGE"));
      }
      if (errMsg.length() > 0)
        throw new Exception(errMsg.substring(1));
    }

    if (primaryKeys == null || primaryKeys.size() == 0) {
      throw new Exception("No primary key definition for table \"" + table + "\"");
    } else if (!r.getFieldNames().containsAll(primaryKeys)) {
      throw new Exception("Missing primary column for table \"" + table + "\"");
    }

    String sql = "DELETE FROM " + dbQuoteString + table + dbQuoteString + " ";

    StringBuilder wh = new StringBuilder("");
    for (String pkfieldname : primaryKeys) {
      wh.append(" AND " + dbQuoteString + getMapping(pkfieldname) + dbQuoteString + "=?");
    }
    sql += " WHERE " + wh.substring(5);

    Connection conn = getConnection();
    PreparedStatement prep = conn.prepareStatement(sql);
    sqlStatement = sql;

    setSqlParams(prep, r, primaryKeys);

    prep.execute();

    if (connection == null && conn != null && !conn.isClosed()) {
      conn.close();
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
   * Sets values in a prepared statement using a DataRow.
   * 
   * @param prep the prepared statement.
   * @param dr a DataRow containing the values for the prepared statement.
   * @param fields an ArrayList with field names. If not null and not empty, this list will be used
   *        to get a portion of values from the DataRow dr. Otherwise all fields from dr will be set
   *        in the prepared statement.
   * @throws SQLException is thrown when a value cannot be set.
   */
  private void setSqlParams(PreparedStatement prep, DataRow dr, ArrayList<String> fields)
      throws Exception {
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
        // Should never occur. This loop iterates over the DataRow fields. This ensures that all
        // fields exists in the DataRow.
        e.printStackTrace();
        index++;
        continue;
      }

      if (o.getValue() == null) {
        if (dbType.equals("BASIS DBMS") && type == Types.CHAR)
          prep.setString(index, "");
        else
          prep.setNull(index, type);
        index++;
        continue;
      } else if (o.getValue() instanceof String && ((String) o.getValue()).startsWith("cond:")) {
        DataRow drv = ExpressionMatcher
            .getPreparedWhereClauseValues(((String) o.getValue()).substring(5), type);
        for (String expField : drv.getFieldNames()) {
          prep.setObject(index, drv.getFieldValue(expField));
          index++;
        }
        continue;
      }
      switch (type) {
        case Types.NUMERIC:
          if (dbType.equals("BASIS DBMS") && o.getValue() == null)
            prep.setBigDecimal(index, new BigDecimal(0));
          else
            prep.setBigDecimal(index, o.getBigDecimal());
          break;
        case Types.INTEGER:
          if (dbType.equals("BASIS DBMS") && o.getValue() == null)
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
          if (dbType.equals("BASIS DBMS") && o.getValue() == null)
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
          if (dbType.equals("BASIS DBMS") && o.getValue() == null)
            prep.setString(index, "");
          else
            prep.setString(index, o.getString());
          break;
        default:
          System.err.println(
              "WARNING: using prep.setObject(object) will fail if there is no equivalent SQL type for the given object");
          prep.setObject(index, o.getValue());
      }
      index++;
    }
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
    ResultSet brs = null;
    Connection conn = null;

		try {
			conn = getConnection();
			try (PreparedStatement prep = conn.prepareStatement(sql)) {

      // Set params if there are any
      if (params != null) {
        int i = 1;
        for (String p : params.getFieldNames()) {
          try {
            prep.setObject(i, params.getFieldValue(p));
          } catch (Exception e) {
            // Should never occur. This loop iterates over the DataRow fields. This ensures that all
            // fields exists in the DataRow.
          }
          i++;
        }
      }

      brs = new ResultSet(prep.executeQuery());
			}
    } catch (SQLException e1) {
      e1.printStackTrace();
    } finally {
      if (connection == null && conn != null) {
        try {
          conn.close();
        } catch (SQLException e) {
          e.printStackTrace();
        }
      }
    }

    return brs;
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
    }
  }
}
