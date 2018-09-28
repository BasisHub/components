package com.basiscomponents.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDataSource {

	private String jdbcDriver;
	private String url;
	private String user;
	private String password;

	@SuppressWarnings("unused")
	private JDBCDataSource() {
	}

	public JDBCDataSource(String jdbcDriver, String url, String user, String password) throws ClassNotFoundException {
		this.jdbcDriver = jdbcDriver;
		this.url = url;
		this.user = user;
		this.password = password;

		Class.forName(this.jdbcDriver);
	}

	public ResultSet fetchResultSet(String sql) throws SQLException {
		try (Connection conn = DriverManager.getConnection(url, user, password);
				Statement stmt = conn.createStatement();) {
			java.sql.ResultSet rs = stmt.executeQuery(sql);// this rs is not closed, be carefull
			return new ResultSet(rs);
		}

	}

}
