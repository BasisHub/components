package com.basiscomponents.constants;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TestDataBaseConstants {

	// WARNING: If a new connection is added here, it has to contain a "jdbc" string
	// to be tracked by the
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
	 * to drop them all. The connections strings have to contain an "jdbc" in order
	 * to be tracked.
	 * 
	 * @See above
	 * 
	 * @return The connection strings of this class
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static ArrayList<String> getAllTestConnections() {

		Field[] fieldArray = TestDataBaseConstants.class.getDeclaredFields();
		ArrayList<String> conList = new ArrayList<String>();
		for (int i = 0; i < fieldArray.length; i++) {
			try {
				if (((String) fieldArray[i].get(null)).contains("jdbc")) {
					conList.add((String) fieldArray[i].get(null));
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return conList;
	}

}
