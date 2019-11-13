package com.basiscomponents.db.resultSetJsonTest;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.Regression;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

public class MissingFieldTest {
	@Test
	@Regression(issue = "159")
	public void testMissingField() throws Exception {
		ResultSet rs = new ResultSet();

		DataRow dr = new DataRow();
		dr.setFieldValue("TEST1","test1");
		dr.setFieldValue("TEST2","test2");
		rs.add(dr);

		dr = new DataRow();
		dr.setFieldValue("TEST1","x1");
		dr.setFieldValue("TEST2","x2");
		dr.setFieldValue("TEST3","x3");
		rs.add(dr);

		System.out.println(rs.toJson(true));

		Assert.assertNotNull(rs.toJson(true));
	}
}
