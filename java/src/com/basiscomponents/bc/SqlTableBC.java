package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.DataField;

public class SqlTableBC implements BusinessComponent {

	private String Url;
	private String User;
	private String Password;
	private String Table;
	private String Scope = "";
	private HashMap<String,ArrayList<String>> Scopes;
	private ArrayList<String> PrimaryKeys;
	private ArrayList<String> AutoIncrementKeys;
	private DataRow AttributesRecord;
	private Connection Conn;
	private String retrieveSql;

	private DataRow   Filter;
	private DataRow   FieldSelection;

	private String DBType;
	private String DBQuoteString = "";


	public SqlTableBC(String Url) {
		this.Url      = Url;
	}


	public SqlTableBC(String Driver, String Url, String User, String Password) throws ClassNotFoundException {
		this.Url      = Url;
		this.User     = User;
		this.Password = Password;

		Class.forName(Driver);
	}


	public SqlTableBC(Connection con) throws SQLException {
		if (con != null && !con.isClosed()) {
			Conn = con;
		}
	}


	private Connection getConnection() throws SQLException {
		if (Conn != null) return Conn;

		if (User == null || Password == null)
			return DriverManager.getConnection(Url);
		else
			return DriverManager.getConnection(Url, User, Password);
	}


	public void setTable(String Table) {
		this.Table = Table;
		this.PrimaryKeys = new ArrayList<>();
		this.AutoIncrementKeys = new ArrayList<>();
		this.AttributesRecord = new DataRow();

		Connection conn = null;
		try {
			conn = getConnection();

			DatabaseMetaData meta = conn.getMetaData();
			DBType = meta.getDatabaseProductName().toUpperCase();
			DBQuoteString = meta.getIdentifierQuoteString();
			java.sql.ResultSet rs = meta.getPrimaryKeys(null, null, Table);

			while (rs.next()) {
				String primaryKey = rs.getString("COLUMN_NAME");
				PrimaryKeys.add(primaryKey);
			}

			rs = meta.getColumns(null, null, Table, null);
			if (rs.getMetaData().getColumnCount() > 22) { //IS_AUTOINCREMENT=23 (BBj doesn't support the IS_AUTOINCREMENT property)
				while (rs.next()) {
					String name = rs.getString("COLUMN_NAME");
					String autoIncrement = rs.getString("IS_AUTOINCREMENT");
					if ("YES".equals(autoIncrement))
						AutoIncrementKeys.add(name);
				}
			}

			//read attributes (for getAttributesRecord() method)
			Statement stmt = conn.createStatement();
			ResultSet ar ;
			if (retrieveSql != null && !retrieveSql.equals(""))
				ar = new ResultSet(stmt.executeQuery("SELECT * FROM ("+retrieveSql+") WHERE 1=0"));
			else
				ar = new ResultSet(stmt.executeQuery("SELECT * FROM "+DBQuoteString+Table+DBQuoteString+" WHERE 1=0"));
			for (String field : ar.getColumnNames()) {
				HashMap<String, Object> attrmap = ar.getColumnMetaData(field);
				try {
					int type = (int)attrmap.get("ColumnType");
					AttributesRecord.addDataField(field, type, new DataField(null));
					for (String attr : attrmap.keySet()) {
						AttributesRecord.setFieldAttribute(field, attr, attrmap.get(attr).toString());
					}
					if (PrimaryKeys.contains(field)) AttributesRecord.setFieldAttribute(field, "EDITABLE", "2");
				}
				catch (Exception ex) { }
			}

			//set default scope
			Scopes = new HashMap<String,ArrayList<String>>();
			Scopes.put("*", AttributesRecord.getFieldNames());
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		finally {
			if (Conn == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void setRetrieveSql(String sql) {
		retrieveSql = sql;
	}

	public String getDBQuoteString() {
		return DBQuoteString;
	}


	public DataRow getFilter() {
		return this.Filter;
	}


	public void setFilter(DataRow filter) {
		this.Filter = filter;
	}


	public DataRow getFieldSelection() {
		return this.FieldSelection;
	}


	public void setFieldSelection(DataRow FieldSelection) {
		this.FieldSelection = FieldSelection;
	}


	public void setFieldSelection(Collection<String> FieldSelection) {
		if (FieldSelection == null) {
			this.FieldSelection = null;
		}
		else {
			DataRow fields = new DataRow();
			for (String field : FieldSelection) {
				fields.setFieldValue(field, "");
			}
			this.FieldSelection = fields;
		}
	}


	/**
	 * Get current scope (if scope is set).
	 * return  String scope  the selected scope.
	 */
	public String getScope() {
		return this.Scope;
	}


	/**
	 * Set a field selection scope (A-Z).
	 * If no or a wrong scope is set then the default scope will be used.
	 * @param  String scope  the scope to set.
	 */
	public void setScope(String scope) {
		this.Scope = scope;
	}


	public HashMap<String,ArrayList<String>> getScopeDef() {
		return this.Scopes;
	}


	public void setScopeDef(HashMap<String,ArrayList<String>> scopes) {
		this.Scopes = scopes;
	}


	public ResultSet retrieve() throws Exception {
		return retrieve(-1,-1);
	}


	public ResultSet retrieve(int first, int last) throws Exception {
		if (first >= 0 && last < first) {
			throw new Exception("Invalid range: last could not be lower than first!");
		}

		if (Filter != null && Filter.contains("%SEARCH")) {
			throw new Exception("Full text search not implemented yet!");
		}

		ResultSet retrs = null;
		Connection conn = null;
		try {
			conn = getConnection();
			String sql;

			LinkedHashSet<String> fields = new LinkedHashSet<String>();
			if ((this.FieldSelection == null || this.FieldSelection.getFieldNames().size() == 0) && (Scope == null || Scope.equals(""))) {
				fields.add("*");
			}

			if (Scope != null) {
				for (char s : Scope.toCharArray()) {
					String scope = String.valueOf(s);
					if (Scopes.containsKey(scope))
						fields.addAll(Scopes.get(scope));
				}
			}

			if (FieldSelection != null) {
				fields.addAll(FieldSelection.getFieldNames());
			}

			StringBuffer sqlfields = new StringBuffer("");
			if (fields.contains("*"))
				sqlfields.append("*");
			else {
				for (String field : fields) {
					sqlfields.append(","+DBQuoteString+field+DBQuoteString);
				}
				sqlfields = new StringBuffer(sqlfields.substring(1));
			}

			if (retrieveSql != null && !retrieveSql.equals(""))
				sql = "SELECT "+sqlfields+" FROM ("+retrieveSql+")";
			else
				sql = "SELECT "+sqlfields+" FROM "+DBQuoteString+Table+DBQuoteString;

			if (Filter != null && Filter.getFieldNames().size() > 0) {
				StringBuffer wh = new StringBuffer("");
				for (String f : Filter.getFieldNames()) {
					wh.append(" AND "+DBQuoteString+f+DBQuoteString+"=?");
				}
				if (wh.length()>0) sql+=" WHERE "+wh.substring(5);
			}

			if (first >= 0 && last >= first) {
				switch(DBType) {
					case "BASIS DBMS":
						sql+=" LIMIT "+(first+1)+","+(last - first + 1);
						break;
					case "MYSQL":
						sql+=" LIMIT "+first+","+(last - first + 1);
						break;
					case "MICROSOFT SQL SERVER":
						//OFFSET is available since MS SQL Server 2012 (version 11)
						int dbVersion = Integer.valueOf(conn.getMetaData().getDatabaseProductVersion().replaceAll("(\\d+)\\..*", "$1"));
						if (dbVersion >= 11)
							sql="SELECT * FROM ("+sql+") T ORDER BY (SELECT NULL) OFFSET "+first+" ROWS FETCH NEXT "+(last - first + 1)+" ROWS ONLY";
						else
							throw new Exception("Pagination is not supported or not implemented with the "+DBType+" (version "+dbVersion+") database.");
						break;
					default:
						throw new Exception("Pagination is not supported or not implemented with the "+DBType+" database.");
				}
			}

			PreparedStatement prep = conn.prepareStatement(sql);

			if (Filter != null) {
				setSqlParams(prep, Filter, Filter.getFieldNames());
			}

			java.sql.ResultSet rs = prep.executeQuery();
			retrs = new ResultSet();
			retrs.populate(rs, true);
		}
		catch (SQLException ex) {
			throw ex;
		}
		finally {
			if (Conn == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}


		// now set the EDITABLE flag for primary key fields in the result set to "2"
		ArrayList<String> fields = retrs.getColumnNames();
		for (DataRow dr : retrs) {
			for (String field : PrimaryKeys) {
				if (fields.contains(field)) dr.setFieldAttribute(field, "EDITABLE", "2");
			}
		}

		return retrs;
	}


	public Collection<String> validateWrite(DataRow dr) {
		return new ArrayList<String>();
	}


	public DataRow write(DataRow r) throws Exception {
		Boolean pk_present=false;
		if (PrimaryKeys != null && PrimaryKeys.size() > 0) {
			pk_present=r.getFieldNames().containsAll(PrimaryKeys);
		}

		Connection conn = getConnection();

		// Read table field names from the table (not from getAttributesRecord()).
		// The field may differ if a custom retrieve sql statement is used.
		DatabaseMetaData meta = conn.getMetaData();
		java.sql.ResultSet rs = meta.getColumns(null, null, Table, null);
		ArrayList<String> tableFields = new ArrayList<String>();
		while (rs.next()) {
			tableFields.add(rs.getString("COLUMN_NAME"));
		}

		String sql="";
		int affectedRows = 0;
		DataRow ret = r.clone();
		PreparedStatement prep;


		// update (Try an update an check affected rows. If there are no (0) affected rows, then make an insert.)
		if (pk_present) {
			sql="UPDATE "+DBQuoteString+Table+DBQuoteString+" SET ";

			ArrayList<String> fields = new ArrayList<String>();

			StringBuffer update = new StringBuffer("");
			for (String field : tableFields) {
				if (PrimaryKeys.contains(field)) continue;
				if (r.getFieldNames().contains(field)) {
					fields.add(field);
					update.append(","+DBQuoteString+field+DBQuoteString+"=?");
				}
			}

			
			if (update.length()>0){
				// if the fields are _only_ fields that are part of the primary key 
				// (e.g. a table with PK being a compount, not having fields outside the PK)
				// then update would be "" and this portion would fail

				sql+=update.substring(1);

				StringBuffer wh = new StringBuffer("");
				for (String pkfield : PrimaryKeys) {
					wh.append(" AND "+DBQuoteString+pkfield+DBQuoteString+"=?");
					fields.add(pkfield);
				}
				sql+=" WHERE "+wh.substring(5);

				prep = conn.prepareStatement(sql);
				setSqlParams(prep, r, fields);

				affectedRows = prep.executeUpdate();
				
				
			}
			else {
				///so now we have to do a SELECT to see if the record is there, as we can't check with update
				sql="SELECT COUNT(*) AS C FROM "+DBQuoteString+Table+DBQuoteString+"  ";
				StringBuffer wh = new StringBuffer("");
				for (String pkfield : PrimaryKeys) {
					wh.append(" AND "+DBQuoteString+pkfield+DBQuoteString+"=?");
				}
				if (wh.length()>0) sql+=" WHERE "+wh.substring(5);
				fields.addAll(PrimaryKeys);
				prep = conn.prepareStatement(sql);
				setSqlParams(prep, r, fields);
				java.sql.ResultSet jrs = prep.executeQuery();
				ResultSet retrs = new ResultSet();
				retrs.populate(jrs, true);
				affectedRows = retrs.get(0).getFieldAsNumber("C").intValue();
			}

		}

		// insert
		if (!pk_present || affectedRows == 0) {
			sql="INSERT INTO "+DBQuoteString+Table+DBQuoteString+" (";

			ArrayList<String> fields = new ArrayList<String>();
			StringBuffer keys = new StringBuffer("");
			StringBuffer values = new StringBuffer("");
			for (String field : tableFields) {
				if (r.contains(field)) {
					fields.add(field);
					keys.append(","+DBQuoteString+field+DBQuoteString);
					values.append(",?");
				}
			}
			sql+=keys.substring(1)+") VALUES("+values.substring(1)+")";

			prep = conn.prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
			setSqlParams(prep, r, fields);

			affectedRows = prep.executeUpdate();

			//get generated keys
			if (affectedRows > 0) {
				java.sql.ResultSet gkeys = prep.getGeneratedKeys();
				if (gkeys.next()) {
					for (int i=0; i < gkeys.getMetaData().getColumnCount(); i++) {
						String name = AutoIncrementKeys.get(i);
						ret.setFieldValue(name, gkeys.getObject(i+1));
					}
					if (PrimaryKeys != null && PrimaryKeys.size() > 0) {
						pk_present=ret.getFieldNames().containsAll(PrimaryKeys);
					}
				}
			}
		}


		//reload from the database
		if (pk_present) {
			DataRow oldfilter = this.Filter;
			DataRow filter = new DataRow();
			for (String f : PrimaryKeys) {
				filter.setFieldValue(f, ret.getField(f));
			}
			this.setFilter(filter);
			ResultSet retrs = this.retrieve(0,2);
			if (retrs.size() == 1)
				ret=retrs.getItem(0);
			else {
				System.err.println("could not read written record - got more than 1 record back!");
				System.err.println("filter = "+filter);
			}

			this.setFilter(oldfilter);
		}


		if (Conn == null && conn != null) {
			try {
				conn.close();
			}
			catch (SQLException ex) {}
		}

		return ret;
	}


	public Collection<String> validateRemove(DataRow dr) {
		return new ArrayList<String>();
	}


	public void remove(DataRow r) throws Exception {
		if (PrimaryKeys == null || PrimaryKeys.size() == 0) {
			throw new Exception("No primary key definition for table \""+Table+"\"");
		}
		else if (!r.getFieldNames().containsAll(PrimaryKeys)) {
			throw new Exception("Missing primary column for table \""+Table+"\"");
		}

		String sql = "DELETE FROM "+DBQuoteString+Table+DBQuoteString+" ";

		StringBuilder wh = new StringBuilder("");
		for (String pkfieldname : PrimaryKeys) {
			wh.append(" AND "+DBQuoteString+pkfieldname+DBQuoteString+"=?");
		}
		sql+=" WHERE "+wh.substring(5);

		Connection conn = getConnection();
		PreparedStatement prep = conn.prepareStatement(sql);

		setSqlParams(prep, r, PrimaryKeys);

		prep.execute();

		if (Conn == null && conn != null && !conn.isClosed()) {
			conn.close();
		}
	}


	public DataRow getAttributesRecord() {
		return AttributesRecord;
	}


	public DataRow getNewObjectTemplate(DataRow conditions) {
		return getAttributesRecord();
	}


	private void setSqlParams(PreparedStatement prep, DataRow dr, ArrayList<String> fields) throws Exception {
		if (prep == null || dr == null) {
			return;
		}

		if (fields == null) {
			fields = dr.getFieldNames();
		}

		int index = 1;
		for (String field : fields) {
			Integer type = dr.getFieldType(field);
			DataField o = dr.getField(field);
			switch (type) {
				case 4:
					prep.setInt(index, o.getInt());
					break;
				case java.sql.Types.DOUBLE:
					prep.setDouble(index, o.getDouble());
					break;
				case -1:
				case 1:
				case 12:
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
				case 1111:
					///this is an auto-generated key. set as string and hope for the best
					prep.setString(index, o.getString());
					break;
				default:
					System.err.println("WARNING: using prep.setObject(object) will fail if there is no equivalent SQL type for the given object");
					prep.setObject(index, o.getValue());
			}
			index++;
		}
	}


	public ResultSet retrieve(String sql, DataRow params) throws Exception {
		ResultSet brs = null;
		Connection conn = null;

		try {
			conn = getConnection();
			PreparedStatement prep = conn.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (String p : params.getFieldNames()) {
					prep.setObject(i, params.getFieldValue(p));
					i++;
				}
			}

			brs = new ResultSet(prep.executeQuery());
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		finally {
			if (Conn == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

		return brs;
	}

}
