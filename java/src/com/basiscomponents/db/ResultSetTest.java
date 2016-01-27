package com.basiscomponents.db;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ResultSetTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void toJsonTest() throws Exception {
		DataRow r1 = new DataRow();
		r1.setFieldValue("F1", "123");
		r1.setFieldValue("F2", 456);
		r1.setFieldValue("F3", Boolean.TRUE);
		
		DataRow r2 = new DataRow();
		r2.setFieldValue("X1", 99.99);
		r2.setFieldValue("X2", "ABC");
		r2.setFieldValue("X3", Boolean.FALSE);
		
		ResultSet r = new ResultSet();
		r.add(r1);
		r.add(r2);
		System.out.println(r.toJson());
		String js = "{\"resultset\":[{\"datarow\":[{\"Name\":\"F1\",\"StringValue\":\"123\",\"Type\":\"C\"},{\"Name\":\"F2\",\"IntegerValue\":456,\"Type\":\"I\"},{\"Name\":\"F3\",\"BooleanValue\":true,\"Type\":\"B\"}]},{\"datarow\":[{\"Name\":\"X1\",\"DoubleValue\":99.99,\"Type\":\"N\"},{\"Name\":\"X2\",\"StringValue\":\"ABC\",\"Type\":\"C\"},{\"Name\":\"X3\",\"BooleanValue\":false,\"Type\":\"B\"}]}]}";
		assertEquals(js, r.toJson());
	}

	@Test
	public void fromJsonTest() throws Exception {
		
		String js = "{\"resultset\":[{\"datarow\":[{\"Name\":\"F1\",\"StringValue\":\"123\",\"Type\":\"C\"},{\"Name\":\"F2\",\"IntegerValue\":456,\"Type\":\"I\"},{\"Name\":\"F3\",\"BooleanValue\":true,\"Type\":\"B\"}]},{\"datarow\":[{\"Name\":\"X1\",\"DoubleValue\":99.99,\"Type\":\"N\"},{\"Name\":\"X2\",\"StringValue\":\"ABC\",\"Type\":\"C\"},{\"Name\":\"X3\",\"BooleanValue\":false,\"Type\":\"B\"}]}]}";
		ResultSet r = ResultSet.fromJson(js);
		
		DataRow r1 = (DataRow) r.get(0);
		assertEquals("123",r1.getFieldAsString("F1"));
		assertEquals((Double)456.0,r1.getFieldAsNumber("F2"));
		assertEquals(Boolean.TRUE,r1.getField("F3"));
		
		DataRow r2 = (DataRow) r.get(1);

		assertEquals((Double)99.99,r2.getFieldAsNumber("X1"));
		assertEquals("ABC",r2.getFieldAsString("X2"));
		assertEquals(Boolean.FALSE,r2.getField("X3"));
		
		
	}	

}
