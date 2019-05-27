package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class H2DataBaseProvider {

	private static String sql;

	public static void createTestDataBaseForSQLRetrieve() throws SQLException {
		try (
		Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
		Statement stmt = con.createStatement();
		) {
			sql = "CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION VALUES ('Alfred', 62, 1)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION VALUES ('Simpson', 42, 0)";
			stmt.executeUpdate(sql);
			sql = "CREATE TABLE IF NOT EXISTS REGISTRATION2 (first VARCHAR(255), age INTEGER, customerID INTEGER)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION2 VALUES ('Jasper', 33, 1)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION2 VALUES ('Alfred', 62, 1)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS FULLREGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)";
			stmt.executeUpdate(sql);
			sql = "insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)";
			stmt.executeUpdate(sql);
			sql = "insert into FULLREGISTRATION VALUES ('Simpson', 42, 0)";
			stmt.executeUpdate(sql);
			sql = "insert into FULLREGISTRATION VALUES ('Jasper', 33, 1)";
			stmt.executeUpdate(sql);
			sql = "insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS TREES (name CHAR(5), rings INT, height DOUBLE)";
			stmt.executeUpdate(sql);
			sql = "insert into TREES VALUES ('tree1', 155, 144.32)";
			stmt.executeUpdate(sql);
			sql = "insert into TREES VALUES ('tree2', 132, 1004.53)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS CUSTOMERS (name CHAR(50), customerID INTEGER, country VARCHAR(255))";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Freeman', 1, 'USA')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Jasper', 2, 'England')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Simpson', 3, 'USA')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Alfred', 4, 'England')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Jenkins', 5, 'Australia')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Caesar', 6, 'England')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Peter', 7, 'USA')";
			stmt.executeUpdate(sql);
			sql = "insert into CUSTOMERS VALUES ('Flint', 8, 'USA')";
			stmt.executeUpdate(sql);
		}
	}

	public static void createTestDataBaseForNormalRetrieve() throws SQLException {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test2", "sa", "sa");
				Statement stmt = con.createStatement();) {

			sql = "CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION VALUES ('Alfred', 62, 1)";
			stmt.executeUpdate(sql);
			sql = "insert into REGISTRATION VALUES ('Simpson', 42, 0)";
			stmt.executeUpdate(sql);

			sql = "CREATE TABLE IF NOT EXISTS BOOLTABLE (boolType BOOL, booleanType BOOLEAN, bitType BIT)";
			stmt.executeUpdate(sql);
			sql = "insert into BOOLTABLE VALUES (true, false, true)";
			stmt.executeUpdate(sql);
		}
	}
}
