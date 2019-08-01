package com.basiscomponents.bc;

import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_NORMAL_RETRIEVE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.CON_TO_SQL_RETRIEVE_DB;
import static com.basiscomponents.constants.TestDataBaseConstants.USERNAME_PASSWORD;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.configuration.TracingConfiguration;
import com.basiscomponents.db.ResultSet;

public class SqlQueryBCTest {

	private String sql;
	private ResultSet rs;
	private static Connection conToSQLRetrieve;
	private static Connection conToNormalRetrieve;

	/**
	 * Loading the h2-Driver and creating the test databases.
	 * 
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	@BeforeAll
	public static void initialize()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("org.h2.Driver").newInstance();
		H2DataBaseProvider.createTestDataBaseForSQLRetrieve();
		H2DataBaseProvider.createTestDataBaseForNormalRetrieve();
		conToSQLRetrieve = DriverManager.getConnection(CON_TO_SQL_RETRIEVE_DB, USERNAME_PASSWORD, USERNAME_PASSWORD);
		conToNormalRetrieve = DriverManager.getConnection(CON_TO_NORMAL_RETRIEVE_DB, USERNAME_PASSWORD,
				USERNAME_PASSWORD);
	}

	/**
	 * The SqlQueryBC is queried with a simple SQL-Statement. The ResultSet is
	 * checked to contain the correct data.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void sqlQueryBCSimpleTest() throws SQLException {

		SqlQueryBC sqlQuery = new SqlQueryBC(conToSQLRetrieve);

		// Collect data from the SqlQueryBC
		sql = "SELECT * FROM REGISTRATION";
		rs = sqlQuery.retrieve(sql, null);

		// Checking the ColumnNames and results
		assertTrue(rs.getColumnCount() == 3);
		assertTrue(rs.getColumnNames().contains("FIRST"));
		assertTrue(rs.getColumnNames().contains("AGE"));
		assertTrue(rs.getColumnNames().contains("CUSTOMERID"));

		assertEquals("Alfred", rs.get(0).getFieldValue("FIRST"));
		assertEquals(62, rs.get(0).getFieldValue("AGE"));
		assertEquals(1, rs.get(0).getFieldValue("CUSTOMERID"));
	}

	/**
	 * The last executed "retrieve-method" statement is checked to be correct.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void sqlQueryBCGetLastRetrieveStatement() throws SQLException {

		SqlQueryBC sqlQuery = new SqlQueryBC(conToSQLRetrieve);

		// A statement is executed with retrieve
		sql = "SELECT * FROM REGISTRATION";
		TracingConfiguration.setTraceLastRetrieve(true);
		rs = sqlQuery.retrieve(sql, null);

		assertEquals("SELECT * FROM REGISTRATION", sqlQuery.getLastRetrieve().substring(7));
	}

	/**
	 * The last executed "execute-method" statement is checked to be correct.
	 * 
	 * @throws SQLException
	 */
	@Test
	public void sqlQueryBCGetLastExecuteStatement() throws SQLException {

		SqlQueryBC sqlQuery = new SqlQueryBC(conToSQLRetrieve);

		// A statement is executed with execute
		sql = "SELECT * FROM REGISTRATION";
		TracingConfiguration.setTraceLastExecute(true);
		sqlQuery.execute(sql);

		assertEquals("SELECT * FROM REGISTRATION", sqlQuery.getLastExecute().substring(7));
	}

	@Test
	public void sqlQueryBCGetLastSQLStatement() throws SQLException {

		SqlQueryBC sqlQuery = new SqlQueryBC(conToSQLRetrieve);

		// A statement is executed with execute, but the last statement is tracked with
		// the getLastSql method
		sql = "SELECT * FROM REGISTRATION";
		TracingConfiguration.setTraceLastExecute(true);
		TracingConfiguration.setTraceSql(true);
		sqlQuery.execute(sql);

		assertEquals("SELECT * FROM REGISTRATION", sqlQuery.getLastSql().substring(7));
	}


	/**
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		conToSQLRetrieve.close();
		conToNormalRetrieve.close();
		assertTrue(conToSQLRetrieve.isClosed());
		assertTrue(conToNormalRetrieve.isClosed());
		H2DataBaseProvider.dropAllTestTables();
	}

}
