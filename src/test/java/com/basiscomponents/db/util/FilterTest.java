package com.basiscomponents.db.util;

import com.basiscomponents.bc.SqlTableBC;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Disabled
public class FilterTest {
	private Connection con;
	private SqlTableBC stb;

	@BeforeEach
	public void setup() throws ClassNotFoundException, SQLException {
		Class.forName("com.basis.jdbc.BasisDriver");

		String url = "jdbc:basis:localhost?DATABASE=ChileCompany&SSL=false";
		con = DriverManager.getConnection(url, "admin", "admin123");
		stb = new SqlTableBC(con);
		stb.setTable("CUSTOMER");
	}

	@AfterEach
	public void cleanup() throws SQLException {
		con.close();
	}

	@Test
	public void testNonConditionalFilter() throws Exception {
		DataRow dr = new DataRow();

		dr.setFieldValue("POST_CODE", "87111");
		stb.setFilter(dr);
		ResultSet retrieve = stb.retrieve();
		assertEquals(3, retrieve.getDataRows().size());
	}

	@Test
	public void testUnequalParanthesis() throws Exception {
		DataRow dr = new DataRow();
		dr.setFieldValue("POST_CODE", "cond:<>87111");
		stb.setFilter(dr);

		ResultSet retrieve = stb.retrieve();
		retrieve = stb.retrieve();
		assertEquals(65, retrieve.getDataRows().size());
	}

	@Test

	public void testUnequalExclamationMark() throws Exception {
		DataRow dr = new DataRow();
		dr.setFieldValue("POST_CODE", "cond:!87111");
		stb.setFilter(dr);
		ResultSet retrieve = stb.retrieve();
		assertEquals(65, retrieve.getDataRows().size());


		dr = new DataRow();
		dr.setFieldValue("BILL_ADDR1", "regex:[0-9]\\w+[\\s,\\w]*");
		stb.setFilter(null);
		retrieve = stb.retrieve();
		retrieve.getDataRows().forEach(System.out::println);
		System.out.println(retrieve.getDataRows().size());
		dr.setFieldValue("LICENSE", "cond:>DD-57-DD");
		dr.setFieldValue("LICENSE", "cond:<DD-57-DD");
		dr.setFieldValue("LICENSE", "cond:DD-57-DD");
		con.close();

		dr.addDataField("FIELD", new DataField("Hello"));
		DataRowRegexMatcher matcher = new DataRowRegexMatcher("FIELD", "regex:[Helo]\\w*");
		assertTrue(matcher.matches(dr));
	}

}
