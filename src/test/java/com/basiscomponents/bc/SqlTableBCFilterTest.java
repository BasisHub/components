package com.basiscomponents.bc;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

public class SqlTableBCFilterTest {

	@Test
	public void test() {
		DataRow filter = new DataRow();
		filter.addDataField("Regex", new DataField("regex:[A-Z]"));
		SqlTableBC bc = new SqlTableBC("");
		bc.setFilter(filter);
		DataRow filter2 = bc.getFilter();
		assertTrue("Filter must stay the same", filter2.equals(filter));
	}

}
