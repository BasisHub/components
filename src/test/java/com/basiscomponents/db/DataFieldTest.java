package com.basiscomponents.db;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DataFieldTest {


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
