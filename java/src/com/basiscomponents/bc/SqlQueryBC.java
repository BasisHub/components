package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.basiscomponents.db.ResultSet;


public class SqlQueryBC {

	private String url;
	private String user;
	private String password;
	private Connection connection;


	public SqlQueryBC(String Url) {
		this.url      = Url;
	}


	public SqlQueryBC(String driver, String url, String user, String password) throws ClassNotFoundException {
		this.url        = url;
		this.user       = user;
		this.password   = password;

		Class.forName(driver);
	}


	public SqlQueryBC(Connection con) throws SQLException {
		if (con != null && !con.isClosed()) {
			connection = con;
		}
	}


	private Connection getConnection() throws SQLException {
		if (connection != null) return connection;

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
		Connection conn = null;

		try {
			conn = getConnection();
			PreparedStatement prep = conn.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}

			brs = new ResultSet(prep.executeQuery());
		} catch (SQLException e1) {
			throw e1;
		}
		finally {
			if (connection == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}

		return brs;
	}

	public Boolean execute(String sql) throws SQLException {
		return execute(sql,null);
	}

	public Boolean execute(String sql, List<Object> params) throws SQLException {
		Connection conn = null;
		Boolean b = false;

		try {
			conn = getConnection();
			PreparedStatement prep = conn.prepareStatement(sql);

			// Set params if there are any
			if (params != null) {
				int i = 1;
				for (Object p : params) {
					prep.setObject(i, p);
					i++;
				}
			}

			b = prep.execute() || prep.getUpdateCount() > 0;
		} catch (SQLException e1) {
			throw e1;
		}
		finally {
			if (connection == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}

		return b;
	}

}
