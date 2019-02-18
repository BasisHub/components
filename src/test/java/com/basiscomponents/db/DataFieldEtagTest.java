package com.basiscomponents.db;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataFieldEtagTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		DataField s1 = new DataField("String1");
		assertEquals("B55673D809600C3A5C40CF0BB7049673", s1.createEtag());
		DataField i1 = new DataField(1);
		assertEquals("6933D47FACCC84A5AA3EB1011EBB2F8B", i1.createEtag());
	}

}
