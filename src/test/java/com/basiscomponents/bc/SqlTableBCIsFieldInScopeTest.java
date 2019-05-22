package com.basiscomponents.bc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SqlTableBCIsFieldInScopeTest {

	private SqlTableBC sqlTablebc;
	private HashMap<String, ArrayList<String>> scopes;

	@BeforeAll
	public  void setUp() throws Exception {
		sqlTablebc = new SqlTableBC("");
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
		sqlTablebc.setScopeDef(scopes);
		sqlTablebc.setScope("D");
	}


	@Test
	public void testIsPresent() {

		assertTrue(sqlTablebc.isFieldInScope("FIELD_1"));
		assertTrue(sqlTablebc.isFieldInScope("FIELD_2"));
		assertTrue(sqlTablebc.isFieldInScope("FIELD_3"));
		assertTrue(sqlTablebc.isFieldInScope("FIELD_4"));
		assertFalse(sqlTablebc.isFieldInScope("FIELD_5"));

		sqlTablebc.setScope("F");
		assertTrue(sqlTablebc.isFieldInScope("FIELD_5"));
		sqlTablebc.setScope("D");
	}

	@Test
	public void testIsNotPresent() {
		assertFalse(sqlTablebc.isFieldInScope("FIELD_5"));
		assertFalse(sqlTablebc.isFieldInScope("FIELD_6"));
		assertFalse(sqlTablebc.isFieldInScope("FIELD_7"));
		assertFalse(sqlTablebc.isFieldInScope(null));

	}

}
