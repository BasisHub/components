package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DataBaseProvider {

	private static String sql;

	public static void createTestDataBase() throws SQLException {
		try (
		Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
		Statement stmt = con.createStatement();
		) {
			sql = "CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS TREES (name CHAR(5), rings INT, height DOUBLE)";
			stmt.executeUpdate(sql);
		}
	}
}
