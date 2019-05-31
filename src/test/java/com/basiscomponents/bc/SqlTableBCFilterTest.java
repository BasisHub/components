package com.basiscomponents.bc;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class SqlTableBCFilterTest {

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
		H2DataBaseProvider.createTestDataBaseForNormalRetrieve();
	}

	/**
	 * The filter DataRow has to stay the same DataRow as before, after inserting it
	 * into the SqlTableBC.
	 * 
	 */
	@Test
	public void test() {
		DataRow filter = new DataRow();
		filter.addDataField("Regex", new DataField("regex:[A-Z]"));
		SqlTableBC bc = new SqlTableBC("");
		bc.setFilter(filter);
		DataRow filter2 = bc.getFilter();
		assertTrue(filter2.equals(filter), "Filter must stay the same");
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
	}


}
