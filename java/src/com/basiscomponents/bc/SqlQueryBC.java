package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.basiscomponents.db.ResultSet;


public class SqlQueryBC {

	private String url;
	private String user;
	private String password;
	private Connection conn;

	private final List<String> queryStrings;
	private final boolean tracing;
	private String lastRetrieve;
	private String lastExecute;
 
	public SqlQueryBC(String url) {
		this(url, false);
	}

	public SqlQueryBC(String url, boolean tracing) {
		this.url = url;
		this.tracing = tracing;
		this.queryStrings = tracing ? new ArrayList<>(1024) : null;
	}

	public SqlQueryBC(String driver, String url, String user, String password) throws ClassNotFoundException {
		this(driver, url, user, password, false);
	}

	public SqlQueryBC(String driver, String url, String user, String password, boolean tracing)
			throws ClassNotFoundException {
		this.url = url;
		this.user = user;
		this.password = password;
		this.tracing = tracing;
		this.queryStrings = tracing ? new ArrayList<>(1024) : null;
		Class.forName(driver);
	}


	public SqlQueryBC(Connection con) throws SQLException {
		this(con, false);
	}

	public SqlQueryBC(Connection con, boolean tracing) throws SQLException {
		if (con != null && !con.isClosed()) {
			conn = con;
		}
		this.tracing = tracing;
		this.queryStrings = tracing ? new ArrayList<>(1024) : null;

	}

	private Connection getConnection() throws SQLException {
		if (conn != null) return conn;

		if (user == null || password == null)
			return DriverManager.getConnection(url);
		else
			return DriverManager.getConnection(url, user, password);
	}


	public ResultSet retrieve(String sql) throws SQLException {
		return retrieve(sql,null);
	}


	public ResultSet retrieve(String sql, List<Object> params) throws SQLException {
		ResultSet brs = null;
		Connection connection = null;

		try {
			connection = getConnection();
			PreparedStatement prep = connection.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}
			if (tracing) {
				String query = prep.toString();
				lastRetrieve = query;
				if (queryStrings.size() >= 1024) {
					queryStrings.remove(0);
				}
				queryStrings.add(query);
			}
			brs = new ResultSet(prep.executeQuery());
		} finally {
			if (this.conn == null && connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}

		return brs;
	}

	public Boolean execute(String sql) throws SQLException {
		return execute(sql,null);
	}

	public Boolean execute(String sql, List<Object> params) throws SQLException {
		Connection connection = null;
		Boolean b = false;

		try {
			connection = getConnection();
			PreparedStatement prep = connection.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}
			if (tracing) {
				String query = prep.toString();
				lastExecute = query;
				if (queryStrings.size() >= 1024) {
					queryStrings.remove(0);
				}
				queryStrings.add(query);
			}
			b = prep.execute() || prep.getUpdateCount() > 0;
		} finally {
			if (this.conn == null && connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {}
			}
		}
		return b;
	}

	public String getLastRetrieve() {
		if (!tracing) {
			throw new UnsupportedOperationException("No tracing enabled");
		}
		return lastRetrieve;
	}

	public String getLastExecute() {
		if (!tracing) {
			throw new UnsupportedOperationException("No tracing enabled");
		}
		return lastExecute;
	}

	public List<String> getQueryStrings() {
		if (!tracing) {
			throw new UnsupportedOperationException("No tracing enabled");
		}
		List<String> result = new ArrayList<>(1024);
		Collections.copy(result, queryStrings);
		return result;
	}

}
