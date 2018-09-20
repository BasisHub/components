package com.basiscomponents.bc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * Helper Class to manage Connections to SQLServers in the SQLTableBC
 * 
 * <b> Do not use this class outside of basiscomponents, because a lot might
 * change here in the future</b>
 *
 */
public class SqlConnectionHelper {
	private String url;
	private String user;
	private String password;
	private Connection connection;

	public SqlConnectionHelper(String url) {
		this.url = url;
	}

	public SqlConnectionHelper(String url, String user, String password, String driver) throws ClassNotFoundException {
		this.url = url;
		this.user = user;
		this.password = password;
		Class.forName(driver);

	}

	public SqlConnectionHelper(Connection con) throws SQLException {
		if (con != null && !con.isClosed()) {
			connection = con;
		}
	}

	public Connection getConnection() throws SQLException {
		if (connection != null) {
			return connection;
		}
		if (user == null || password == null) {
			return DriverManager.getConnection(url);
		} else {
			return DriverManager.getConnection(url, user, password);
		}
	}

	public void closeConnection(Connection conn) {
		try {
			if (connection == null && conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
