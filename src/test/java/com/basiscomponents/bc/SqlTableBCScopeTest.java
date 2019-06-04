package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlTableBCScopeTest {

	private SqlTableBC tableBc;
	private HashMap<String, ArrayList<String>> scopes;

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
	 * Cleans up the databases.
	 * 
	 * @throws Exception
	 */
	@AfterAll
	public static void cleanUp() throws Exception {
		H2DataBaseProvider.dropAllTables();
	}
}
