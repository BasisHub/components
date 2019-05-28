package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class SqlTableBCWriteRemoveTest {

	private String sql;
	private ResultSet rs;

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
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. This DataRow is removed from
	 * the SqlTableBC.
	 * 
	 * @throws Exception
	 */
//	@Test
	public void SqlTableBCRemoveSimpleTest()
			throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test2", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("REGISTRATION");
			rs = sqlTable.retrieve();

			DataRow dr = rs.get(0);
			sqlTable.remove(dr);

			rs = sqlTable.retrieve();
			assertEquals(1, rs.size());
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. This DataRow is written into
	 * the SqlTableBC.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTableBCWriteSimpleTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test1", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("REGISTRATION");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			assertEquals(2, rs.size());

			sqlTable.setTable("REGISTRATION2");
			sqlTable.write(dr);
			rs = sqlTable.retrieve();
			assertEquals(3, rs.size());
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
