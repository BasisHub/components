package com.basiscomponents.db.config.export;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.export.ColumnWidthCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnWidthCalculatorTest {
	
	@Test
	public void testCalculateCloumnWidth() throws Exception{
		ResultSet rs = new ResultSet();
		DataRow dr = new DataRow();
		dr.setFieldValue("String", "myString");
		rs.add(dr);
		dr.setFieldValue("String", "myString_2");
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 7, 2, true, false);
		double d = dr.getFieldAsNumber("String");
		assertEquals(d, 45.0);

		dr = new DataRow();
		dr.setFieldValue("String", null);
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 9, 3, true, false);
		d = dr.getFieldAsNumber("String");
		assertEquals(d, 58.0);
		
		dr = new DataRow();
		dr.setFieldValue("String", "ebnlsdkfbns");
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 12, 4, true, false);
		d = dr.getFieldAsNumber("String");
		System.out.println(d);
		assertEquals(d, 85.0);
	}

}
