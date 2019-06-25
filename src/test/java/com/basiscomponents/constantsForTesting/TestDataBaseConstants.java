package com.basiscomponents.constantsForTesting;

import java.util.ArrayList;

public class TestDataBaseConstants {

	// WARNING: If a new connection is added here, it has to be listed in the
	// getAllTestConnections() method in order to be cleared correctly

	public static final String CON_TO_SQL_RETRIEVE_DB = "jdbc:h2:./src/test/testH2DataBases/test1";

	public static final String CON_TO_NORMAL_RETRIEVE_DB = "jdbc:h2:./src/test/testH2DataBases/test2";

	public static final String CON_TO_WRITE_REMOVE_DB = "jdbc:h2:./src/test/testH2DataBases/test3";

	public static final String CON_TO_FILTER_SCOPE_DB = "jdbc:h2:./src/test/testH2DataBases/test4";

	public static final String USERNAME_PASSWORD = "sa";

	private TestDataBaseConstants() {
	}

	/**
	 * Adds all the connection constants to an ArrayList, to gather the information
	 * to drop them all.
	 * 
	 * @return
	 */
	public static ArrayList<String> getAllTestConnections() {
		ArrayList<String> al = new ArrayList<String>();
		al.add(CON_TO_SQL_RETRIEVE_DB);
		al.add(CON_TO_NORMAL_RETRIEVE_DB);
		al.add(CON_TO_WRITE_REMOVE_DB);
		al.add(CON_TO_FILTER_SCOPE_DB);
		return al;
	}
}
