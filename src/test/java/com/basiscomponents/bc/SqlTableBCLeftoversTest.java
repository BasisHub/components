package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class SqlTableBCLeftoversTest {

	private ResultSet rs;
	private SqlTableBC tableBC;

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
	 * Creates a SqlTableBC with a connection to a h2-DataBase. A write, remove and
	 * retrieve statement are executed. Their sql statements are queried and checked
	 * to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
//	There is something weird with the write Statement, it does not ever fill types in VALUES (So does Remove)
	public void sqlTableBCGetLastSQLStatementTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test3", "sa", "sa");) {

			// Set table and get its data with normal retrieve()
			tableBC = new SqlTableBC(con);
			tableBC.setTable("FULLREGISTRATION");
			rs = tableBC.retrieve();
			DataRow dr = rs.get(0);

			// Testing write
			tableBC.write(dr);
			assertEquals("INSERT INTO \"FULLREGISTRATION\" (\"FIRST\",\"AGE\",\"CUSTOMERID\") VALUES(?,?,?)",
					tableBC.getLastSqlStatement());

			// Testing retrieve
			rs = tableBC.retrieve();
			assertEquals("SELECT * FROM \"FULLREGISTRATION\"", tableBC.getLastSqlStatement());

			// Testing remove
			tableBC.setTable("PRIMARYKEY_REGISTRATION");
			rs = tableBC.retrieve();
			DataRow dr1 = rs.get(0);
			tableBC.remove(dr1);
			assertEquals("DELETE FROM \"PRIMARYKEY_REGISTRATION\"  WHERE \"CUSTOMERID\"=?",
					tableBC.getLastSqlStatement());
		}
	}

	/**
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		H2DataBaseProvider.dropAllTestTables();
	}
}
