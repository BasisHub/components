package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.basiscomponents.db.ResultSet;


public class SqlQueryBC {

	private String Url;
	private String User;
	private String Password;
	private Connection Conn;


	public SqlQueryBC(String Url) {
		this.Url      = Url;
	}


	public SqlQueryBC(String Driver, String Url, String User, String Password) throws ClassNotFoundException {
		this.Url        = Url;
		this.User       = User;
		this.Password   = Password;

		Class.forName(Driver);
	}


	public SqlQueryBC(Connection con) throws SQLException {
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
			if (Conn == null && conn != null) {
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
			if (Conn == null && conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}

		return b;
	}

}
