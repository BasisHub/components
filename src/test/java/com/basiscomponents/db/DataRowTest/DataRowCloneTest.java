package com.basiscomponents.db.DataRowTest;

import org.junit.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class DataRowCloneTest {


	@Test
	public void test() {
		ResultSet rs = new ResultSet();
		
		DataRow dr = new DataRow();
		DataField df = new DataField("sth");
		DataField df2 = new DataField("sth2");
		
		dr.addDataField("firstField", df);
		dr.addDataField("secondField", df2);
		rs.add(dr);
		dr = new DataRow(rs);
		dr.addDataField("firstField", df);
		rs.add(dr);
		rs.forEach(x -> x.clone()); // here an exception was thrown

	}

}
