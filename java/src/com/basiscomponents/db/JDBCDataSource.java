package com.basiscomponents.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCDataSource {

	private String JDBCDriver;
	private String URL;
	private String User;
	private String Password;

	@SuppressWarnings("unused")
	private JDBCDataSource() {
	};

	public JDBCDataSource(String JDBCDriver, String URL, String User, String Password) throws ClassNotFoundException {
		this.JDBCDriver = JDBCDriver;
		this.URL = URL;
		this.User = User;
		this.Password = Password;

		Class.forName(this.JDBCDriver);
	}

	public ResultSet fetchResultSet(String sql) throws SQLException {
		Connection conn = DriverManager.getConnection(URL, User, Password);
		Statement stmt = conn.createStatement();
		java.sql.ResultSet rs = stmt.executeQuery(sql);
		ResultSet r = new ResultSet(rs);
		return r;
	}

}
