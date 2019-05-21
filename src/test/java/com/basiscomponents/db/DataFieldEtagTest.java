package com.basiscomponents.db;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFieldEtagTest {

	@BeforeAll
	public void setUp() throws Exception {
	}

	@AfterAll
	public void tearDown() throws Exception {
	}
	@Ignore
	@Test
	public void test() {
		DataField s1 = new DataField("String1");
		assertEquals("B55673D809600C3A5C40CF0BB7049673", s1.createEtag());
		DataField i1 = new DataField(1);
		assertEquals("6933D47FACCC84A5AA3EB1011EBB2F8B", i1.createEtag());
	}

}
