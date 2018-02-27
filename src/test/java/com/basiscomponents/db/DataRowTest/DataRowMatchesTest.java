package com.basiscomponents.db.DataRowTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.junit.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataRowProvider;

public class DataRowMatchesTest {
	@Test
	public void testPositive() throws ParseException {
		DataRow dataRow = DataRowProvider.buildSampleDataRow(false);
		DataRow filter = new DataRow();
		filter.setFieldValue(DataRowProvider.STRINGFIELD, "Value");
		assertTrue(dataRow.matches(filter));
	}

	@Test
	public void testNegative() throws ParseException {
		DataRow dataRow = DataRowProvider.buildSampleDataRow(false);
		DataRow filter = new DataRow();
		filter.setFieldValue(DataRowProvider.STRINGFIELD, "This is not the value, you are looking for");
		assertFalse(dataRow.matches(filter));
	}

	@Test
	public void testNonExistingFields() throws ParseException {
		DataRow dataRow = DataRowProvider.buildSampleDataRow(false);
		DataRow filter = new DataRow();
		filter.setFieldValue("Not a field in existance", "should not be compared");
		assertTrue(dataRow.matches(filter));// not sure if it should behave like this
	}
}
