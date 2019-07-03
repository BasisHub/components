/**
 * 
 */
package com.basiscomponents.db.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
	@BeforeEach
	public void setUp() throws Exception {
		rs = new ResultSet();
		DataRow dr = DataRowProvider.buildSampleDataRow(false);
		DataField df = new DataField(ResultSetProvider.createDefaultResultSet(false));
		dr.addDataField("EMBEDDED_RS", df);
		rs.add(dr);
	}

	@Test
	public void test() throws Exception {
		String js = rs.toJson();
		ResultSet rs2 = ResultSet.fromJson(js);
		assertTrue(rs2.get(0).getDataField("EMBEDDED_RS").getValue() instanceof ResultSet);
		ResultSet rs3 = (ResultSet) rs2.get(0).getDataField("EMBEDDED_RS").getValue();
	}
//	@Regression(issue = "#144")
//	@Test
//	public void testDataRowAttributeToJson() throws Exception {
//		ResultSet rs = ResultSetProvider.createMultipleDataRowResultSet();
//		rs.get(0).setAttribute("ATTRIBUTE", "VALUE");
//		String json= MetaDataJsonMapper.toJson(rs,true,null,true,true);
//		ResultSet rs2 = ResultSet.fromJson(json);
//		assertEquals(rs.getDataRows().size(), rs2.getDataRows().size());
//		assertEquals("VALUE",rs2.get(0).getAttribute("ATTRIBUTE"));
//	}

}
