/**
 * 
 */
package com.basiscomponents.db.util;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

/**
 * @author damore
 *
 */
public class ResultSetJsonMapperTest {

	ResultSet rs;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		rs = new ResultSet();
		DataRow dr = DataRowProvider.buildSampleDataRow(false);
		DataField df = new DataField(ResultSetProvider.createDefaultResultSet());
		dr.addDataField("EMBEDDED_RS", df);
		rs.add(dr);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		rs.print();
		String js = rs.toJson();
		System.out.println(js);
		ResultSet rs2 = ResultSet.fromJson(js);
		rs2.print();
		System.out.println(rs2.getDataRows().get(0).getField("EMBEDDED_RS").getValue().getClass().getName());
		assertTrue(rs2.get(0).getDataField("EMBEDDED_RS").getValue() instanceof ResultSet);
		rs2.get(0).getDataField("EMBEDDED_RS").getAttributes()
				.forEach((x, y) -> System.out.println("[" + x + "]: " + y));
		ResultSet rs3 = (ResultSet) rs2.get(0).getDataField("EMBEDDED_RS").getValue();
		rs3.print();

		// assertEquals(rs, rs2);

	}

}
