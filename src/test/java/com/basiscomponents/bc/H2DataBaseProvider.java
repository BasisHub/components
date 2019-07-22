package com.basiscomponents.bc;

import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_FILTER_SCOPE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_NORMAL_RETRIEVE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_SQL_RETRIEVE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_WRITE_REMOVE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.USERNAME_PASSWORD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.basiscomponents.constants.SpecialCharacterConstants;
import com.basiscomponents.constants.TestDataBaseConstants;

public class H2DataBaseProvider {

	private static ArrayList<String> sql = new ArrayList<String>();

	/**
	 * Creates a H2DataBase with the sql statements listed in the sql ArrayList of
	 * this class.
	 * 
	 * @param stmt The statement of the DataBase to fill.
	 * @throws SQLException
	 */
	private static void createDataBase(Statement stmt) throws SQLException {
		try {
		for (int i = 0; i < sql.size(); i++) {
			stmt.executeUpdate(sql.get(i));
		}
		sql.clear();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("WARNING: Tables are dropped through exception in creating!");
			dropAllTestTables();
		}
	}

	/**
	 * Drops all tables of the test DataBases.
	 * 
	 */
	public static void dropAllTestTables() throws SQLException {
		ArrayList<String> connections = TestDataBaseConstants.getAllTestConnections();
		for (int currentDataBase = 0; currentDataBase < connections.size(); currentDataBase++) {
			try (Connection con = DriverManager.getConnection(
					connections.get(currentDataBase),
					USERNAME_PASSWORD, USERNAME_PASSWORD);
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

	public static void createTestDataBaseForSQLRetrieve() throws SQLException {
		try (
				Connection con = DriverManager.getConnection(CON_TO_SQL_RETRIEVE_DB, USERNAME_PASSWORD,
						USERNAME_PASSWORD);
		Statement stmt = con.createStatement();
		) {
			sql.add("CREATE TABLE IF NOT EXISTS REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into REGISTRATION VALUES ('Alfred', 62, 1)");
			sql.add("insert into REGISTRATION VALUES ('Simpson', 42, 0)");
			sql.add("CREATE TABLE IF NOT EXISTS REGISTRATION2 (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into REGISTRATION2 VALUES ('Jasper', 33, 1)");
			sql.add("insert into REGISTRATION2 VALUES ('Alfred', 62, 1)");

			sql.add("CREATE TABLE IF NOT EXISTS FULLREGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER, employeeID INTEGER)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Simpson', 42, 0, 2)");
			sql.add("insert into FULLREGISTRATION VALUES ('Jasper', 33, 1, 3)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1, 4)");

			sql.add("CREATE TABLE IF NOT EXISTS TREES (name CHAR(5), rings INT, height DOUBLE)");
			sql.add("insert into TREES VALUES ('tree1', 155, 144.32)");
			sql.add("insert into TREES VALUES ('tree2', 132, 1004.53)");

			sql.add("CREATE TABLE IF NOT EXISTS CUSTOMERS (name CHAR(50), customerID INTEGER, country VARCHAR(255), employeeID INTEGER, PRIMARY KEY(customerID))");
			sql.add("insert into CUSTOMERS VALUES ('Freeman', 1, 'USA', 1)");
			sql.add("insert into CUSTOMERS VALUES ('Jasper', 2, 'England', 1)");
			sql.add("insert into CUSTOMERS VALUES ('Simpson', 3, 'USA', 1)");
			sql.add("insert into CUSTOMERS VALUES ('Alfred', 4, 'England', 1)");
			sql.add("insert into CUSTOMERS VALUES ('Jenkins', 5, 'Australia', 2)");
			sql.add("insert into CUSTOMERS VALUES ('Caesar', 6, 'England', 2)");
			sql.add("insert into CUSTOMERS VALUES ('Peter', 7, 'USA', 2)");
			sql.add("insert into CUSTOMERS VALUES ('Flint', 8, 'USA', 3)");

			createDataBase(stmt);
		}
	}

	public static void createTestDataBaseForNormalRetrieve() throws SQLException {
		try (Connection con = DriverManager.getConnection(CON_TO_NORMAL_RETRIEVE_DB, USERNAME_PASSWORD,
				USERNAME_PASSWORD);
				Statement stmt = con.createStatement();) {

			sql.add("CREATE TABLE IF NOT EXISTS PRIMARYKEY_REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER, PRIMARY KEY (customerID))");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Alfred', 62, 0)");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Simpson', 42, 1)");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Freeman', 22, 2)");

			sql.add("CREATE TABLE IF NOT EXISTS SPECIALREGISTRATION (first VARCHAR(255))");
			sql.add("insert into SPECIALREGISTRATION VALUES ('" + SpecialCharacterConstants.GERMAN_SPECIAL_CHARACTERS
					+ "')");
			sql.add("insert into SPECIALREGISTRATION VALUES ('" + SpecialCharacterConstants.STANDARD_SPECIAL_CHARACTERS
					+ "')");
			sql.add("insert into SPECIALREGISTRATION VALUES ('"
					+ SpecialCharacterConstants.MATHEMATICAL_SPECIAL_CHARACTERS + "')");
			sql.add("insert into SPECIALREGISTRATION VALUES ('" + SpecialCharacterConstants.FRENCH_SPECIAL_CHARACTERS
					+ "')");

			sql.add("CREATE TABLE IF NOT EXISTS BOOLTABLE (boolType BOOL, booleanType BOOLEAN, bitType BIT)");
			sql.add("insert into BOOLTABLE VALUES (true, false, true)");

			sql.add("CREATE TABLE IF NOT EXISTS INTTABLE (intType INT, integerType INTEGER, mediumintType MEDIUMINT, int4Type INT4, signedType SIGNED)");
			sql.add("insert into INTTABLE VALUES (1, -1, 0, -2147483648, 2147483647)");

			sql.add("CREATE TABLE IF NOT EXISTS SPECIALINTTABLE (tinyintType TINYINT, smallintType SMALLINT, bigintType BIGINT)");
			sql.add("insert into SPECIALINTTABLE VALUES (127, 32767, 9223372036854775807)");

			sql.add("CREATE TABLE IF NOT EXISTS DOUBLETABLE (doubleType DOUBLE, floatAsDoubleType FLOAT, realType REAL, floatAsFloatType FLOAT(20))");
			sql.add("insert into DOUBLETABLE VALUES (127.43324344, 32767.534344, 1.23, 1.23)");

			sql.add("CREATE TABLE IF NOT EXISTS BIGDECIMALTABLE (bigDecimalType NUMERIC)");
			sql.add("insert into BIGDECIMALTABLE VALUES (64543)");

			createDataBase(stmt);
		}
	}

	public static void createTestDataBaseForWriteRemove() throws SQLException {
		try (Connection con = DriverManager.getConnection(CON_TO_WRITE_REMOVE_DB, USERNAME_PASSWORD, USERNAME_PASSWORD);
				Statement stmt = con.createStatement();) {

			sql.add("CREATE TABLE IF NOT EXISTS PRIMARYKEY_REGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER, PRIMARY KEY (customerID))");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Alfred', 62, 0)");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Simpson', 42, 1)");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Simpson', 42, 2)");
			sql.add("insert into PRIMARYKEY_REGISTRATION VALUES ('Freeman', 22, 3)");

			sql.add("CREATE TABLE IF NOT EXISTS FULLREGISTRATION (first VARCHAR(10), age INTEGER, customerID INTEGER)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Simpson', 42, 0)");
			sql.add("insert into FULLREGISTRATION VALUES ('Jasper', 33, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 1)");

			sql.add("CREATE TABLE IF NOT EXISTS BOOLTABLE (boolType BOOL, booleanType BOOLEAN, bitType BIT)");
			sql.add("insert into BOOLTABLE VALUES (true, false, true)");

			sql.add("CREATE TABLE IF NOT EXISTS TIMETABLE (dateType DATE, timeStampType TIMESTAMP)");
			sql.add("insert into TIMETABLE VALUES ('1999-05-05',  '1999-05-05 01:23:45.67')");

			sql.add("CREATE TABLE IF NOT EXISTS SPECIALINTTABLE (smallintType SMALLINT, bigintType BIGINT, bigDecimalType NUMERIC)");
			sql.add("insert into SPECIALINTTABLE VALUES (34, 35543543435, 64543)");

			sql.add("CREATE TABLE IF NOT EXISTS DOUBLETABLE (doubleType DOUBLE, floatAsDoubleType FLOAT, realType REAL, floatAsFloatType FLOAT(20))");
			sql.add("insert into DOUBLETABLE VALUES (127.43324344, 32767.534344, 1.23, 1.23)");

			sql.add("CREATE TABLE IF NOT EXISTS TREES (name CHAR(5), rings INT, height DOUBLE)");
			sql.add("insert into TREES VALUES ('tree1', 155, 144.32)");
			sql.add("insert into TREES VALUES ('tree2', 132, 1004.53)");

			createDataBase(stmt);
		}
	}

	public static void createTestDataBaseForFilteringScoping() throws SQLException {
		try (Connection con = DriverManager.getConnection(CON_TO_FILTER_SCOPE_DB, USERNAME_PASSWORD, USERNAME_PASSWORD);
				Statement stmt = con.createStatement();) {

			sql.add("CREATE TABLE IF NOT EXISTS FULLREGISTRATION (first VARCHAR(255), age INTEGER, customerID INTEGER)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 0)");
			sql.add("insert into FULLREGISTRATION VALUES ('Simpson', 42, 1)");
			sql.add("insert into FULLREGISTRATION VALUES ('Jasper', 33, 2)");
			sql.add("insert into FULLREGISTRATION VALUES ('Alfred', 62, 3)");
			sql.add("insert into FULLREGISTRATION VALUES ('Freeman', 18, 4)");
			sql.add("insert into FULLREGISTRATION VALUES ('Freeman', 18, 5)");
			sql.add("insert into FULLREGISTRATION VALUES ('Caesaaar', 18, 6)");
			sql.add("insert into FULLREGISTRATION VALUES ('Peter', 18, 3)");

			sql.add("CREATE TABLE IF NOT EXISTS TREES (name CHAR(50), rings INT, height DOUBLE, high BOOL)");
			sql.add("insert into TREES VALUES ('tree1', 155, 144.32, true)");
			sql.add("insert into TREES VALUES ('tree2', -10, 5434535464.53, false)");
			sql.add("insert into TREES VALUES ('Manfred', 0, 544504.53, true)");
			sql.add("insert into TREES VALUES ('Dirk', -3455, 1454.53, false)");
			sql.add("insert into TREES VALUES ('Heinz', 0, 10.53, true)");
			sql.add("insert into TREES VALUES ('Otto', 135432, 1004.53, false)");
			sql.add("insert into TREES VALUES ('Wilhelm', 13452, 5004.53, true)");
			sql.add("insert into TREES VALUES ('Eduard', 13259, 100004.53, false)");

			sql.add("CREATE TABLE IF NOT EXISTS CUSTOMERS (name CHAR(50), customerID INTEGER, country VARCHAR(255), age INTEGER, PRIMARY KEY(customerID))");
			sql.add("insert into CUSTOMERS VALUES ('Freeman', 1, 'USA', 62)");
			sql.add("insert into CUSTOMERS VALUES ('Jasper', 2, 'England', 63)");
			sql.add("insert into CUSTOMERS VALUES ('Simpson', 3, 'USA', 64)");
			sql.add("insert into CUSTOMERS VALUES ('Alfred', 4, 'England', 65)");
			sql.add("insert into CUSTOMERS VALUES ('Jenkins', 5, 'Australia', 66)");
			sql.add("insert into CUSTOMERS VALUES ('Caesar', 6, 'England', 67)");
			sql.add("insert into CUSTOMERS VALUES ('Peter', 7, 'USA', 68)");
			sql.add("insert into CUSTOMERS VALUES ('Flint', 8, 'USA', 69)");
			sql.add("insert into CUSTOMERS VALUES ('Jasper', 9, 'England', 63)");

			createDataBase(stmt);
		}
	}
}
