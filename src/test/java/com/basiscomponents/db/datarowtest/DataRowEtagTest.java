package com.basiscomponents.db.datarowtest;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataRowProvider;

public class DataRowEtagTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {

		DataRow dataRow = DataRowProvider.buildSampleDataRow(true);
		assertEquals("418EEACDA00D2FCD37E29D9A7480D9DC", dataRow.getEtag());
	}

}
