package com.basiscomponents.db.seeder;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.ResultSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommonDataTypeSeederTest {
	
	@Test
	public void testRetrieve() throws Exception{
		CommonDataTypeSeeder seeder = CommonDataTypeSeeder.getInstance();
		ResultSet rs = seeder.retrieve(100);
		
		assertEquals(rs.size(), 100);
		assertEquals(rs.get(99).getFieldAsNumber("DOUBLE_COLUMN"), new Double(99.0));
	}

}
