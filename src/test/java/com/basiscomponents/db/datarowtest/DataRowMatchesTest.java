package com.basiscomponents.db.datarowtest;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataRowProvider;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

	@Test
	public void testRegex() throws ParseException {
		DataRow dataRow = DataRowProvider.buildSampleDataRow(false);
		DataRow filter = new DataRow();
		filter.setFieldValue(DataRowProvider.STRINGFIELD, "regex:[A-Z][a-z]*");
		assertTrue(dataRow.matches(filter));
	}
}
