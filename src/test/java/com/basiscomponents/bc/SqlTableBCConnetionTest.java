package com.basiscomponents.bc;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.ResultSet;

public class SqlTableBCConnetionTest {

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
		H2DataBaseProvider.createTestDataBase();
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase.
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@Test
	public void SqlTablBCConnectionSimpleTest()
			throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");) {
			SqlTableBC sqlTable = new SqlTableBC(con);
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. The active table is
	 * switched to REGISTRATION and its values are queried with retrieve(). The
	 * resulting ResultSet is checked to contain the right data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTablBCConnectionSimpleTableTest()
			throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
				Statement stmt = con.createStatement();) {

			// Insert new data
			String sql = "insert into REGISTRATION VALUES ('Freeman', 34)";
			stmt.executeUpdate(sql);

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("REGISTRATION");
			ResultSet rs = sqlTable.retrieve();

			assertTrue(rs.getColumnCount() == 2);
			assertTrue(rs.getColumnNames().contains("FIRST"));
			assertTrue(rs.getColumnNames().contains("AGE"));

			assertEquals("Freeman", rs.get(0).getFieldValue("FIRST"));
			assertEquals(34, rs.get(0).getFieldValue("AGE"));
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A SQL Statement is
	 * created and the SqlTableBC is queried with retrieve(String sql, DataRow dr).
	 * The resulting ResultSet is checked to contain the right data.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTablBCWriteSqlTest()
			throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
				Statement stmt = con.createStatement();) {

			// Insert new data
			String sql = "insert into REGISTRATION VALUES ('Simpson', 42)";
			stmt.executeUpdate(sql);

			// Collect data from the SqlTableBC
			SqlTableBC sqlTable = new SqlTableBC(con);
			sql = "SELECT * FROM REGISTRATION";
			ResultSet rs = sqlTable.retrieve(sql, null);

			assertTrue(rs.getColumnCount() == 2);
			assertTrue(rs.getColumnNames().contains("AGE"));

			assertEquals("Simpson", rs.get(0).getFieldValue("FIRST"));
			assertEquals(42, rs.get(0).getFieldValue("AGE"));
		}
	}

	/**
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");
				Statement stmt = con.createStatement();) {
			String sql = "DROP TABLE REGISTRATION";
			stmt.executeUpdate(sql);
		}
	}

}
