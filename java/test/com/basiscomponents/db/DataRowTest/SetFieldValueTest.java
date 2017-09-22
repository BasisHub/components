package com.basiscomponents.db.DataRowTest;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.basiscomponents.db.DataRow;

public class SetFieldValueTest {

	@Test
	public void RedefineFieldContentsTest() {
		DataRow dr = new DataRow();
		dr.setFieldValue("TEST", "ONE");
		dr.setFieldValue("TEST", "TWO");
		
		try {
			Assert.assertEquals("TWO",dr.getFieldAsString("TEST"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail ("field TEST missing after (re)assigning");
		}
	}

}
