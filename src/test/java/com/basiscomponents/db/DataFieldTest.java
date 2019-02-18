package com.basiscomponents.db;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataFieldTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetBoolean() {
		DataField df = new DataField("true");
		assertTrue(df.getBoolean());
		df = new DataField("TRUE");
		assertTrue(df.getBoolean());
		df = new DataField("1");
		assertTrue(df.getBoolean());
		df = new DataField("false");
		assertFalse(df.getBoolean());
	}

}
