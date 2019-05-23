package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DataBaseProvider {

	public static void createTestDataBase() throws SQLException {
		try (
		Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
		Statement stmt = con.createStatement();
		) {
		String sql = "CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER)";
		stmt.executeUpdate(sql);
		}
	}
}
