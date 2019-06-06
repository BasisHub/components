package com.basiscomponents.bc;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class SqlTableBCFilterTest {

	private ResultSet rs;
	private DataRow filter;

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
	 * The filter DataRow has to stay the same DataRow as before, after inserting it
	 * into the SqlTableBC.
	 * 
	 */
	@Test
	public void sqlTableBCFilterStaysTest() {
		filter = new DataRow();
		filter.addDataField("Regex", new DataField("regex:[A-Z]"));
		filter.addDataField("Age", new DataField("25"));
		SqlTableBC tableBC = new SqlTableBC("");
		tableBC.setFilter(filter);
		DataRow filter2 = tableBC.getFilter();
		assertTrue(filter2.equals(filter), "Filter must stay the same");
	}

	/**
	 * A filter is created and used for the retrieve().
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void sqlTableBCFilterSimpleTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test4", "sa", "sa");
		) {
			SqlTableBC tableBC = new SqlTableBC(con);
			tableBC.setTable("CUSTOMERS");

			// Setting the filter
			filter = new DataRow();
			filter.addDataField("COUNTRY", new DataField("England"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(4, rs.size());
			assertEquals("England", rs.get(0).getFieldValue("COUNTRY"));
			assertEquals("England", rs.get(1).getFieldValue("COUNTRY"));
			assertEquals("England", rs.get(2).getFieldValue("COUNTRY"));
			assertEquals("England", rs.get(3).getFieldValue("COUNTRY"));
		}
	}

	/**
	 * A filter with multiple fields is created and used for the retrieve().
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void sqlTableBCFilterWithMoreValuesTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test4", "sa", "sa");
		) {
			SqlTableBC tableBC = new SqlTableBC(con);
			tableBC.setTable("CUSTOMERS");

			// Setting the filter
			filter = new DataRow();
			filter.addDataField("COUNTRY", new DataField("England"));
			filter.addDataField("NAME", new DataField("Jasper"));
			filter.addDataField("AGE", new DataField(63));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(2, rs.size());
			assertEquals("England", rs.get(0).getFieldValue("COUNTRY"));
			assertEquals("England", rs.get(1).getFieldValue("COUNTRY"));
			assertEquals("Jasper", rs.get(0).getFieldValue("NAME"));
			assertEquals("Jasper", rs.get(1).getFieldValue("NAME"));
		}
	}

	/**
	 * A filter with conditions is created and used for retrieve().
	 * 
	 * @throws Exception
	 * 
	 */
	@Test
	public void sqlTableBCFilterCompareTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test4", "sa", "sa");
		) {
			SqlTableBC tableBC = new SqlTableBC(con);
			tableBC.setTable("CUSTOMERS");

			// Setting the filter to >63 and use it
			filter = new DataRow();
			filter.addDataField("AGE", new DataField("cond:>63"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(6, rs.size());
			assertEquals(3, rs.get(0).getFieldValue("CUSTOMERID"));
			assertEquals(4, rs.get(1).getFieldValue("CUSTOMERID"));
			assertEquals(5, rs.get(2).getFieldValue("CUSTOMERID"));
			assertEquals(6, rs.get(3).getFieldValue("CUSTOMERID"));
			assertEquals(7, rs.get(4).getFieldValue("CUSTOMERID"));
			assertEquals(8, rs.get(5).getFieldValue("CUSTOMERID"));

			// Setting the filter to <63 and use it
			filter = new DataRow();
			filter.addDataField("AGE", new DataField("cond:<63"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(1, rs.size());
			assertEquals(1, rs.get(0).getFieldValue("CUSTOMERID"));

			// Setting the filter to >=63 and use it
			filter = new DataRow();
			filter.addDataField("AGE", new DataField("cond:>=63"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(8, rs.size());
			assertEquals(2, rs.get(0).getFieldValue("CUSTOMERID"));
			assertEquals(3, rs.get(1).getFieldValue("CUSTOMERID"));
			assertEquals(4, rs.get(2).getFieldValue("CUSTOMERID"));
			assertEquals(5, rs.get(3).getFieldValue("CUSTOMERID"));
			assertEquals(6, rs.get(4).getFieldValue("CUSTOMERID"));
			assertEquals(7, rs.get(5).getFieldValue("CUSTOMERID"));
			assertEquals(8, rs.get(6).getFieldValue("CUSTOMERID"));
			assertEquals(9, rs.get(7).getFieldValue("CUSTOMERID"));

			// Setting the filter to <=63 and use it
			filter = new DataRow();
			filter.addDataField("AGE", new DataField("cond:<=63"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(3, rs.size());
			assertEquals(1, rs.get(0).getFieldValue("CUSTOMERID"));
			assertEquals(2, rs.get(1).getFieldValue("CUSTOMERID"));
			assertEquals(9, rs.get(2).getFieldValue("CUSTOMERID"));

		}
	}

	/**
	 * A filter with regexes is created and used for the retrieve(). XXX
	 * 
	 * @throws Exception
	 * 
	 */
//	@Test
	public void sqlTableBCRegexSimpleTest() throws Exception {
		try (Connection con = DriverManager.getConnection("jdbc:h2:./src/test/testH2DataBases/test4", "sa", "sa");
		) {
			SqlTableBC tableBC = new SqlTableBC(con);
			tableBC.setTable("CUSTOMERS");

			// Setting the filter
			filter = new DataRow();
			filter.addDataField("NAME", new DataField("regex:(J)([a-z]*)"));
			tableBC.setFilter(filter);

			rs = tableBC.retrieve();
			assertEquals(2, rs.size());
			assertEquals("Jasper", rs.get(0).getFieldValue("NAME"));
			assertEquals("Jasper", rs.get(1).getFieldValue("NAME"));
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
