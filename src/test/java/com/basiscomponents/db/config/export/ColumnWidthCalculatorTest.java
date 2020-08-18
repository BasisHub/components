package com.basiscomponents.db.config.export;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.export.ColumnWidthCalculator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.ParseException;

public class ColumnWidthCalculatorTest {
	
	@Test
	public void testCalculateColumnWidth() throws Exception{
		ResultSet rs = new ResultSet();
		DataRow dr = new DataRow();
		dr.setFieldValue("String", "myString");
		rs.add(dr);
		dr.setFieldValue("String", "myString_2");
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 7, 2, true, false);
		double d = dr.getFieldAsNumber("String");
		assertEquals(d, 46.0);

		dr = new DataRow();
		dr.setFieldValue("String", null);
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 9, 3, true, false);
		d = dr.getFieldAsNumber("String");
		assertEquals(d, 59.0);
		
		dr = new DataRow();
		dr.setFieldValue("String", "ebnlsdkfbns");
		rs.add(dr);
		dr = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 12, 4, true, false);
		d = dr.getFieldAsNumber("String");
		assertEquals(d, 86.0);
	}
	
	@Test
	public void testCalculateColumnWidthWithMalformedResultSet() throws Exception{
		ResultSet rs = buildMalformedResultSet();
		DataRow dr  = ColumnWidthCalculator.calculateColumnWidths(rs, ColumnWidthCalculator.BASIS_BASE_FONT_RESOURCE_PATH, 12, 4, true, false);
		assertTrue(!dr.isEmpty());
	}
	
	private ResultSet buildCorrectResultSet() {
		ResultSet rs = new ResultSet();
		
		for(int i = 0 ; i < 100 ; i++) {
			DataRow dr = new DataRow();
			for(int j = 0 ; j < 10 ; j++) {
				try {
					dr.setFieldValue("String"+j, "myString");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rs.add(dr);
			}
		}
		
		return rs;
	}
	
	private ResultSet buildMalformedResultSet() {
		ResultSet rs = new ResultSet();
				
		for(int i = 0 ; i < 100 ; i++) {
			int mod = (int) (Math.random() * 10) + 1;
			DataRow dr = new DataRow();
			for(int j = 0 ; j < 10 ; j+=mod) {
				try {
					dr.setFieldValue("String"+j, "myString");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				rs.add(dr);
			}
		}
		
		return rs;
	}

}
