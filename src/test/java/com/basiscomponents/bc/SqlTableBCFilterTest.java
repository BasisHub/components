package com.basiscomponents.bc;


import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SqlTableBCFilterTest {

	@Test
	public void test() {
		DataRow filter = new DataRow();
		filter.addDataField("Regex", new DataField("regex:[A-Z]"));
		SqlTableBC bc = new SqlTableBC("");
		bc.setFilter(filter);
		DataRow filter2 = bc.getFilter();
		assertTrue(filter2.equals(filter), "Filter must stay the same" );
	}

}
