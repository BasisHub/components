package com.basiscomponents.bc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class H2DataBaseProvider {

	private static ArrayList<String> sql = new ArrayList<String>();

	private static void createDataBase(Statement stmt) throws SQLException {
		for (int i = 0; i < sql.size(); i++) {
			stmt.executeUpdate(sql.get(i));
		}
		sql.clear();
	}

	public static void createTestDataBaseForSQLRetrieve() throws SQLException {
		try (
		Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
		Statement stmt = con.createStatement();
		) {
			sql.add("CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into REGISTRATION VALUES ('Alfred', 62, 1)");
			sql.add("insert into REGISTRATION VALUES ('Simpson', 42, 0)");
			sql.add("CREATE TABLE IF NOT EXISTS REGISTRATION2 (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into REGISTRATION2 VALUES ('Jasper', 33, 1)");
			sql.add("insert into REGISTRATION2 VALUES ('Alfred', 62, 1)");

			sql.add("CREATE TABLE IF NOT EXISTS FULLREGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Simpson', 42, 0)");
			sql.add("insert into FULLREGISTRATION VALUES ('Jasper', 33, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)");

			sql.add("CREATE TABLE IF NOT EXISTS TREES (name CHAR(5), rings INT, height DOUBLE)");
			sql.add("insert into TREES VALUES ('tree1', 155, 144.32)");
			sql.add("insert into TREES VALUES ('tree2', 132, 1004.53)");

			sql.add("CREATE TABLE IF NOT EXISTS CUSTOMERS (name CHAR(50), customerID INTEGER, country VARCHAR(255))");
			sql.add("insert into CUSTOMERS VALUES ('Freeman', 1, 'USA')");
			sql.add("insert into CUSTOMERS VALUES ('Jasper', 2, 'England')");
			sql.add("insert into CUSTOMERS VALUES ('Simpson', 3, 'USA')");
			sql.add("insert into CUSTOMERS VALUES ('Alfred', 4, 'England')");
			sql.add("insert into CUSTOMERS VALUES ('Jenkins', 5, 'Australia')");
			sql.add("insert into CUSTOMERS VALUES ('Caesar', 6, 'England')");
			sql.add("insert into CUSTOMERS VALUES ('Peter', 7, 'USA')");
			sql.add("insert into CUSTOMERS VALUES ('Flint', 8, 'USA')");

			createDataBase(stmt);

		} catch (Exception e) {
			e.printStackTrace();
			try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
					Statement stmt = con.createStatement();) {

				// Get tableNames
				String sql = "SHOW TABLES;";
				stmt.execute(sql);
				java.sql.ResultSet rs = stmt.getResultSet();
				ArrayList<String> tableNames = new ArrayList<String>();
				while (rs.next()) {
					tableNames.add(rs.getString("TABLE_NAME"));
				}

				// Drop all tables
				for (int i = 0; i < tableNames.size(); i++) {
					sql = "DROP TABLE " + tableNames.get(i);
					stmt.executeUpdate(sql);
				}
			}
		}
	}

	public static void createTestDataBaseForNormalRetrieve() throws SQLException {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test2", "sa", "sa");
				Statement stmt = con.createStatement();) {

			sql.add("CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into REGISTRATION VALUES ('Alfred', 62, 1)");
			sql.add("insert into REGISTRATION VALUES ('Simpson', 42, 0)");

			sql.add("CREATE TABLE IF NOT EXISTS BOOLTABLE (boolType BOOL, booleanType BOOLEAN, bitType BIT)");
			sql.add("insert into BOOLTABLE VALUES (true, false, true)");

			sql.add("CREATE TABLE IF NOT EXISTS INTTABLE (intType INT, integerType INTEGER, mediumintType MEDIUMINT, int4Type INT4, signedType SIGNED)");
			sql.add("insert into INTTABLE VALUES (1, -1, 0, -2147483648, 2147483647)");

			sql.add("CREATE TABLE IF NOT EXISTS SPECIALINTTABLE (tinyintType TINYINT, smallintType SMALLINT, bigintType BIGINT)");
			sql.add("insert into SPECIALINTTABLE VALUES (127, 32767, 9223372036854775807)");

			sql.add("CREATE TABLE IF NOT EXISTS DOUBLETABLE (doubleType DOUBLE, floatAsDoubleType FLOAT, realType REAL, floatAsFloatType FLOAT(20))");
			sql.add("insert into DOUBLETABLE VALUES (127.43324344, 32767.534344, 1.23, 1.23)");

			createDataBase(stmt);

		} catch (Exception e) {
			e.printStackTrace();
			try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test2", "sa", "sa");
					Statement stmt = con.createStatement();) {

				// Get tableNames
				String sql = "SHOW TABLES;";
				stmt.execute(sql);
				java.sql.ResultSet rs = stmt.getResultSet();
				ArrayList<String> tableNames = new ArrayList<String>();
				while (rs.next()) {
					tableNames.add(rs.getString("TABLE_NAME"));
				}

				// Drop all tables
				for (int i = 0; i < tableNames.size(); i++) {
					sql = "DROP TABLE " + tableNames.get(i);
					stmt.executeUpdate(sql);
				}
			}
		}
	}
}
