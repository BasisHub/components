/**
 * 
 */
package com.basiscomponents.db;

import static org.junit.Assert.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author beff
 *
 */
public class DataRowTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void fromHashMapTest() {
		 HashMap hm = new HashMap();
		 hm.put("A","AA");
		 hm.put("BB", 12);
		 hm.put("CC", 12.3);
		 hm.put("DD",Boolean.TRUE);
		 DataRow rh;
		 try {
			rh = new DataRow(hm);
			assertEquals(rh.toString(),"[BB=12,CC=12.3,DD=1,A=AA]");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			fail(e.getStackTrace().toString());
		}
		 
	}

	
	@Test
	public void toJsonTest() throws Exception {
		DataRow r = new DataRow();

		java.util.Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp t = new java.sql.Timestamp(now.getTime());
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
		String fd = df1.format(now)+"T"+df2.format(now)+".000Z";
		
		r.setFieldValue("MYTIMESTAMP", t);		
		
		r.setFieldValue("MYBOOLEAN",Boolean.TRUE);
		
		r.setFieldValue("MYSTRING","Lorem ipsum dolor sit amet");
		
		r.setFieldValue("MYNUMBER",123.45);
		
		Date d = java.sql.Date.valueOf(com.basis.util.BasisDate.date(2457049,0.0, "%Yl-%Mz-%Dz"));	
		r.setFieldValue("MYDATE", d);

		String js="{\"datarow\":[{\"Name\":\"MYSTRING\",\"StringValue\":\"Lorem ipsum dolor sit amet\",\"Type\":\"C\"},{\"Name\":\"MYDATE\",\"DateValue\":\"2015-01-26T00:00:00.000Z\",\"Type\":\"D\"},{\"Name\":\"MYNUMBER\",\"DoubleValue\":123.45,\"Type\":\"N\"},{\"Name\":\"MYBOOLEAN\",\"BooleanValue\":true,\"Type\":\"B\"},{\"Name\":\"MYTIMESTAMP\",\"TimestampValue\":\""+fd+"\",\"Type\":\"X\"}]}";
		assertEquals(r.toJson(),js);
	}

	@Test
	public void fomJsonTest() throws Exception {
		java.util.Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp t = new java.sql.Timestamp(now.getTime());
		DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
		String fd = df1.format(now)+"T"+df2.format(now)+".000Z";
		
		String js="{\"datarow\":[{\"Name\":\"MYSTRING\",\"StringValue\":\"Lorem ipsum dolor sit amet\",\"Type\":\"C\"},{\"Name\":\"MYDATE\",\"DateValue\":\"2015-01-26T00:00:00.000Z\",\"Type\":\"D\"},{\"Name\":\"MYNUMBER\",\"DoubleValue\":123.45,\"Type\":\"N\"},{\"Name\":\"MYBOOLEAN\",\"BooleanValue\":true,\"Type\":\"B\"},{\"Name\":\"MYTIMESTAMP\",\"TimestampValue\":\""+fd+"\",\"Type\":\"X\"}]}";
		DataRow r = DataRow.fromJson(js);

		
		String e="[MYSTRING=Lorem ipsum dolor sit amet,MYDATE=2015-01-26,MYNUMBER=123.45,MYBOOLEAN=1,MYTIMESTAMP="+now.toString()+"]";

		assertEquals(r.getFieldAsString("MYSTRING"),"Lorem ipsum dolor sit amet");
		assertEquals(r.getFieldAsString("MYDATE"),"2015-01-26");
		assertTrue(r.getFieldAsNumber("MYNUMBER")==123.45);
		assertTrue(r.getFieldAsString("MYBOOLEAN")=="1");

		Timestamp t1 = (Timestamp)r.getField("MYTIMESTAMP");
		assertEquals(t1.toGMTString(),now.toGMTString());

	}	

	@Test
	public void testUmlauteJson() throws Exception {
		DataRow r=new DataRow();
		r.setFieldValue("MYSTRING","M\u00E4rz \u20AC");
		r.setFieldValue("UMLAUTE","\u00C4\u00D6\u00DC\u00E4\u00F6\u00FC\u00DF");
		
		String e = "{\"datarow\":[{\"Name\":\"MYSTRING\",\"StringValue\":\"M\\u00E4rz \\u20AC\",\"Type\":\"C\"},{\"Name\":\"UMLAUTE\",\"StringValue\":\"\\u00C4\\u00D6\\u00DC\\u00E4\\u00F6\\u00FC\\u00DF\",\"Type\":\"C\"}]}";
		String x=r.toJson();
		System.out.println(x);
		System.out.println(e);
		assertEquals(x,e);
	}
	
	@Test
	public void test() {
		//fail("Not yet implemented");
	}

}

