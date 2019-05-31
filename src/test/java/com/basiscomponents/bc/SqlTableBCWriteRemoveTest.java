package com.basiscomponents.bc;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
	private int dataRowCount;

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
		H2DataBaseProvider.createTestDataBaseForWriteRemove();
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. This DataRow is removed from
	 * the SqlTableBC.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTableBCRemoveSimpleTest()
			throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("PRIMARYKEY_REGISTRATION");
			rs = sqlTable.retrieve();
			dataRowCount = rs.size();

			DataRow dr = rs.get(0);
			sqlTable.remove(dr);

			rs = sqlTable.retrieve();
			assertEquals(dataRowCount - 1, rs.size());
			assertEquals("Simpson", rs.get(0).getFieldValue("FIRST"));
			assertEquals("Freeman", rs.get(1).getFieldValue("FIRST"));
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. This DataRow is modified, so
	 * it does not exists in the SqlTableBC anymore. This DataRow cannot be removed
	 * from the table PRIMARYKEY_REGISTRATION.
	 * 
	 * @throws Exception
	 */
//	@Test
	// validateRemove is not implemented yet
	public void SqlTableBCRemoveNegativeTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("PRIMARYKEY_REGISTRATION");
			rs = sqlTable.retrieve();
			dataRowCount = rs.size();

			DataRow dr = rs.get(0);
			dr.setFieldValue("FIRST", "Hans");
//			dr.setFieldValue("AGE", "13");
//			dr.setFieldValue("CUSTOMERID", "130");

			assertThrows(Exception.class, () -> {
				sqlTable.remove(dr);
			});

			rs = sqlTable.retrieve();
			assertEquals(dataRowCount, rs.size());
			assertEquals("Simpson", rs.get(0).getFieldValue("FIRST"));
			assertEquals("Freeman", rs.get(1).getFieldValue("FIRST"));
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. An empty DataRow and
	 * a NULL DataRow are created. These cannot be removed from the SqlTableBC.
	 * 
	 * @throws Exception
	 */
	@Test
	// validateRemove is not implemented yet
	public void SqlTableBCRemoveNegativeTest2() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("PRIMARYKEY_REGISTRATION");
			rs = sqlTable.retrieve();
			dataRowCount = rs.size();

			DataRow dr = new DataRow();
			assertThrows(Exception.class, () -> {
				sqlTable.remove(dr);
			});

			DataRow dr2 = null;
			assertThrows(Exception.class, () -> {
				sqlTable.remove(dr2);
			});

			rs = sqlTable.retrieve();
			assertEquals(dataRowCount, rs.size());
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
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("FULLREGISTRATION");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			dataRowCount = rs.size();

			sqlTable.write(dr);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
			assertEquals("Alfred", rs.get(0).getFieldValue("FIRST"));
			assertEquals("Simpson", rs.get(1).getFieldValue("FIRST"));
			assertEquals("Jasper", rs.get(2).getFieldValue("FIRST"));
			assertEquals("Alfred", rs.get(3).getFieldValue("FIRST"));
			assertEquals("Alfred", rs.get(4).getFieldValue("FIRST"));

			sqlTable.setTable("BOOLTABLE");
			rs = sqlTable.retrieve();
			DataRow dr1 = rs.get(0);
			dataRowCount = rs.size();

			sqlTable.write(dr1);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
			assertEquals(true, rs.get(1).getFieldValue("BOOLTYPE"));
			assertEquals(false, rs.get(1).getFieldValue("BOOLEANTYPE"));
			assertEquals(true, rs.get(1).getFieldValue("BITTYPE"));
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
	public void SqlTableBCWriteManyTypesTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("BOOLTABLE");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			dataRowCount = rs.size();

			sqlTable.write(dr);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
			assertEquals(true, rs.get(1).getFieldValue("BOOLTYPE"));
			assertEquals(false, rs.get(1).getFieldValue("BOOLEANTYPE"));
			assertEquals(true, rs.get(1).getFieldValue("BITTYPE"));

			sqlTable.setTable("TIMETABLE");
			rs = sqlTable.retrieve();
			DataRow dr1 = rs.get(0);
			dataRowCount = rs.size();

			sqlTable.write(dr1);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
			assertEquals(rs.get(0).getFieldValue("DATETYPE"), rs.get(1).getFieldValue("DATETYPE"));
			assertEquals(rs.get(0).getFieldValue("TIMESTAMPTYPE"), rs.get(1).getFieldValue("TIMESTAMPTYPE"));

			sqlTable.setTable("BIGDECIMALTABLE");
			rs = sqlTable.retrieve();
			DataRow dr2 = rs.get(0);
			dataRowCount = rs.size();

			sqlTable.write(dr2);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
			assertEquals(rs.get(0).getFieldValue("BIGDECIMALTYPE"), rs.get(1).getFieldValue("BIGDECIMALTYPE"));
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. An empty DataRow and
	 * a NULL DataRow are created. These cannot be written into the SqlTableBC.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTableBCWriteNegativeTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("FULLREGISTRATION");
			rs = sqlTable.retrieve();
			dataRowCount = rs.size();
			DataRow dr = new DataRow();
			
			assertThrows(Exception.class, () -> {
				sqlTable.write(dr);
			});
			
			DataRow dr2 = null;
			assertThrows(Exception.class, () -> {
				sqlTable.write(dr2);
			});

			rs = sqlTable.retrieve();
			assertEquals(dataRowCount, rs.size());
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. The PRIMARYKEY is changed and
	 * it is written into the SqlTableBC.
	 * 
	 * @throws Exception
	 */
//	@Test
	// This test cannot work at the moment, because pagination is required
	// for writing into the a SqlTableBc with a PrimaryKey,
	// which is not implemented yet for the H2DataBases.
	public void SqlTableBCWriteWithPrimaryKeyTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("PRIMARYKEY_REGISTRATION");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			dataRowCount = rs.size();

			dr.setFieldValue("CUSTOMERID", 40);
			sqlTable.write(dr);
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount + 1, rs.size());
		}
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A ResultSet is
	 * created with retrieve() and a DataRow is taken. Afterwards a column of the
	 * same table is renamed. The DataRow is written into the SqlTableBC, but the
	 * column, which does not match is dropped.
	 * 
	 * @throws Exception
	 */
	@Test
	public void SqlTableBCWriteChangeColumnsTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");
				Statement stmt = con.createStatement();) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("TREES");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			dataRowCount = rs.size();

			// The table is modified
			sql = "ALTER TABLE TREES RENAME COLUMN NAME TO NAMESPACE";
			stmt.executeUpdate(sql);

			// The unmodified DataRow is written into the SqlTableBC and the results are
			// checked
			sqlTable.write(dr);
			rs = sqlTable.retrieve();
			assertTrue(rs.getColumnCount() == 3);
			assertTrue(rs.getColumnNames().contains("NAMESPACE"));
			assertTrue(rs.getColumnNames().contains("RINGS"));
			assertTrue(rs.getColumnNames().contains("HEIGHT"));
			assertEquals(dataRowCount + 1, rs.size());
			
			assertEquals("tree1", rs.get(0).getFieldValue("NAMESPACE"));
			assertEquals("tree2", rs.get(1).getFieldValue("NAMESPACE"));
			assertEquals(null, rs.get(2).getFieldValue("NAMESPACE"));
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
	public void SqlTableBCWriteIntTooBigTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("FULLREGISTRATION");
			rs = sqlTable.retrieve();
			DataRow dr = rs.get(0);
			dataRowCount = rs.size();

//			dr.setFieldValue("CUSTOMERID", Integer.MAX_VALUE);
//			sqlTable.write(dr);
//			rs = sqlTable.retrieve();

			dr.setFieldValue("FIRST",
					"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
			assertThrows(SQLException.class, () -> {
				sqlTable.write(dr);
			});
			rs = sqlTable.retrieve();
			assertEquals(dataRowCount, rs.size());
		}
	}

	/**
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");
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
