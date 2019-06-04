package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import com.basiscomponents.db.ResultSet;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlTableBCScopeTest {

	private SqlTableBC tableBc;
	private HashMap<String, ArrayList<String>> scopes;
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
		H2DataBaseProvider.createTestDataBaseForFilteringScoping();
	}

	/**
	 * Two scopes are created and inserted into the SqlTableBC. Afterwards the test
	 * checks if the scopes are saved correctly.
	 * 
	 */
	@Test
	public void SqlTableBCScopeIsPresent() {

		tableBc = new SqlTableBC("");
		scopes = new HashMap<>();
		ArrayList<String> scopeD = new ArrayList<>();
		ArrayList<String> scopeF = new ArrayList<>();
		scopeD.add("FIELD_1");
		scopeD.add("FIELD_2");
		scopeD.add("FIELD_3");
		scopeD.add("FIELD_4");
		scopeF.add("FIELD_1");
		scopeF.add("FIELD_2");
		scopeF.add("FIELD_3");
		scopeF.add("FIELD_4");
		scopeF.add("FIELD_5");
		scopeF.add("FIELD_6");

		scopes.put("D", scopeD);
		scopes.put("F", scopeF);
		tableBc.setScopeDef(scopes);

		tableBc.setScope("D");

		assertTrue(tableBc.isFieldInScope("FIELD_1"));
		assertTrue(tableBc.isFieldInScope("FIELD_2"));
		assertTrue(tableBc.isFieldInScope("FIELD_3"));
		assertTrue(tableBc.isFieldInScope("FIELD_4"));
		assertFalse(tableBc.isFieldInScope("FIELD_5"));
		assertFalse(tableBc.isFieldInScope("FIELD_6"));
		assertFalse(tableBc.isFieldInScope("FIELD_7"));
		assertFalse(tableBc.isFieldInScope(null));

		tableBc.setScope("F");

		assertTrue(tableBc.isFieldInScope("FIELD_1"));
		assertTrue(tableBc.isFieldInScope("FIELD_2"));
		assertTrue(tableBc.isFieldInScope("FIELD_3"));
		assertTrue(tableBc.isFieldInScope("FIELD_4"));
		assertTrue(tableBc.isFieldInScope("FIELD_5"));
		assertTrue(tableBc.isFieldInScope("FIELD_6"));
		assertFalse(tableBc.isFieldInScope("FIELD_7"));
		assertFalse(tableBc.isFieldInScope(null));
	}

	/**
	 * Creates a SqlTableBC with a connection to a h2-DataBase. The active table is
	 * switched to REGISTRATION and its values are queried with a scope.
	 * 
	 * @throws Exception
	 */
//	@Test
	public void SqlTableBCScopeSimpleTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test4", "sa", "sa");
				Statement stmt = con.createStatement();) {

			// Set table
			SqlTableBC sqlTable = new SqlTableBC(con);
			sqlTable.setTable("CUSTOMERS");

			// Setting up the scope
			scopes = new HashMap<>();
			ArrayList<String> myScope = new ArrayList<>();
			myScope.add("NAME");
			myScope.add("CUSTOMERID");
			scopes.put("myScope", myScope);
			tableBc.setScopeDef(scopes);
			tableBc.setScope("myScope");

			rs = sqlTable.retrieve();

			// Checking the ColumnNames and results
			assertTrue(rs.getColumnCount() == 4);
			assertTrue(rs.getColumnNames().contains("NAME"));
			assertTrue(rs.getColumnNames().contains("CUSTOMERID"));

			assertEquals("Freeman", rs.get(0).getFieldValue("NAME"));
			assertEquals(1, rs.get(0).getFieldValue("CUSTOMERID"));

		}
	}

	/**
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		H2DataBaseProvider.dropAllTables();
	}
}
