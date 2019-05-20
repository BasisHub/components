package com.basiscomponents.db;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.basiscomponents.db.util.DataRowProvider;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetJSonConversionTest {
	
	/**
	 * This method takes two DataRows and compares their values with the getFieldAsString method from DataRow to evaluate the process of toJson/fromJson
	 * 
	 * @param oldDR The initial DataRow before the conversion with toJson/fromJson
	 * @param newDR The converted DataRow after the conversion with toJson/fromJson
	 * @param json The Json String converted from the oldDR 
	 */
	public void equalityAsStringDataRowTest(DataRow oldDR, DataRow newDR, String json ) {
		BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
		BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
		assertTrue(json,fieldNamesOld.size() == fieldNamesNew.size());
		for(int i = 0; i < fieldNamesOld.size(); i++) {
			assertEquals(json + "\n" + fieldNamesOld.get(i) + " values differ with function getFieldAsString",oldDR.getFieldAsString(fieldNamesOld.get(i)), newDR.getFieldAsString(fieldNamesOld.get(i)));
		}
	}
	
	/**
	 * This method takes two DataRows and compares their values with the getFieldAsNumber method from DataRow to evaluate the process of toJson/fromJson
	 * 
	 * @param oldDR The initial DataRow before the conversion with toJson/fromJson
	 * @param newDR The converted DataRow after the conversion with toJson/fromJson
	 * @param json The Json String converted from the oldDR 
	 */
	public void equalityAsNumberDataRowTest(DataRow oldDR, DataRow newDR, String json) {
		BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
		BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
		assertTrue(json, fieldNamesOld.size() == fieldNamesNew.size());
		for(int i = 0; i < fieldNamesOld.size(); i++) {
			assertEquals(json + "\n" + fieldNamesOld.get(i) + " values differ with function getFieldAsNumber",oldDR.getFieldAsNumber(fieldNamesOld.get(i)), newDR.getFieldAsNumber(fieldNamesOld.get(i)));
		}
	}
	
	/**
	 * This method takes two DataRows and compares their values with the getFieldValue method from DataRow to evaluate the process of toJson/fromJson
	 * 
	 * @param oldDR The initial DataRow before the conversion with toJson/fromJson
	 * @param newDR The converted DataRow after the conversion with toJson/fromJson
	 * @param json The Json String converted from the oldDR 
	 */
	public void equalityAsValueDataRowTest(DataRow oldDR, DataRow newDR, String json) {
		BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
		BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
		assertTrue(json, fieldNamesOld.size() == fieldNamesNew.size());
		for(int i = 0; i < fieldNamesOld.size(); i++) {
			assertEquals(json + "\n" + fieldNamesOld.get(i) + " values differ with function getFieldValue",oldDR.getFieldValue(fieldNamesOld.get(i)), newDR.getFieldValue(fieldNamesOld.get(i)));
		}
	}
	
	/**
	 * This method takes two DataRows and compares their attributes with the getField and getAttributes methods from DataRow to evaluate the process of toJson/fromJson
	 * 
	 * @param oldDR The initial DataRow before the conversion with toJson/fromJson
	 * @param newDR The converted DataRow after the conversion with toJson/fromJson
	 * @param json The Json String converted from the oldDR 
	 */
	public void equalityAttributesDataRowTest(DataRow oldDR, DataRow newDR, String json) {
		BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
		BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
		assertTrue(json, fieldNamesOld.size() == fieldNamesNew.size());
		for(int i = 0; i < fieldNamesOld.size(); i++) {
			assertEquals(json + "\n" + fieldNamesOld.get(i) + " values differ with function getAttributes",oldDR.getField(fieldNamesOld.get(i)).getAttributes(), newDR.getField(fieldNamesOld.get(i)).getAttributes());
		}
	}
	
	/**
	 * A default result set is created and all values are set to null
	 * The equality methods will check them to contain null before and after the conversion
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldAsStringNullTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(true);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		
		equalityAsStringDataRowTest(dr0, newDr0, s);
		equalityAsNumberDataRowTest(dr0, newDr0, s);
		equalityAsValueDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A string-only result set is created 
	 * Every string is checked to have the right behavior
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createStringOnlyResultSet();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		
		equalityAsStringDataRowTest(dr0, newDr0, s);
		equalityAsValueDataRowTest(dr0, newDr0, s);
		
		// Testing Strings with getFieldAsNumber
		// Strings containing only numbers (or null) can be converted, otherwise there will be a NumberFormatException
		
		assertEquals(s +" STRINGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.STRINGFIELD));
		assertEquals(s +" FRT_STRINGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.FRT_STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.FRT_STRINGFIELD));
		assertEquals(s +" FTH_STRINGField values difer",dr0.getFieldAsNumber(DataRowProvider.FTH_STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.FTH_STRINGFIELD));
		try {
			dr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD);
			fail("This String cannot be converted into a number!");
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			newDr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD);
			fail("This String cannot be converted into a number!");
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			dr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
			fail("This String cannot be converted into a number!");
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			newDr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
			fail("This String cannot be converted into a number!");
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
	}
	
	/**
	 * A default result set is created 
	 * The equality method will check them to contain the same attributes as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonAttributesTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// The initial ResultSet must have DataRows with DataFields with the attribute "ColumnType", otherwise the fromJSon will add it
		
		equalityAttributesDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A default result set is created, containing 3 DataRows 
	 * The equality method will check them to contain the same attributes as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonMultipleDataRowAttributesTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
		DataRow dr0 = rs0.get(0);
		DataRow dr1 = rs0.get(1);
		DataRow dr2 = rs0.get(2);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		DataRow newDr1 = newRs0.get(1);
		DataRow newDr2 = newRs0.get(2);
		
		// Checking the values of the converted ResultSet
		// The initial ResultSet must have DataRows with DataFields with the attribute "ColumnType", otherwise the fromJSon will add it
		
		equalityAttributesDataRowTest(dr0, newDr0, s);
		equalityAttributesDataRowTest(dr1, newDr1, s);
		equalityAttributesDataRowTest(dr2, newDr2, s);
	}
	
	/**
	 * A default ResultSet is created 
	 * The equality method will check them to contain the same values (with getFieldAsString) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldAsStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		
		equalityAsStringDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A MinMaxResultSet is created, containing extreme values for the common data types
	 * The equality method will check them to contain the same values (with getFieldAsString, getFieldAsNumber, getFieldValue) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldAsStringTestMinMax() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSetMinMax();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
	
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		// BYTEFIELD,SHORTFIELD can only be converted if their values are between [Byte.MAX_VALUE-1,Byte.MIN_VALUE+1] or [Short.MAX_VALUE-1,Short.MIN_VALUE+1] otherwise there will be a ClassCastException
		// LONGFIELD cannot take the Long.MAX_VALUE (NumberFormatException)
	
		equalityAsStringDataRowTest(dr0, newDr0, s);
		equalityAsNumberDataRowTest(dr0, newDr0, s);
		equalityAsValueDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A default ResultSet is created 
	 * The equality method will check them to contain the same values (with getFieldAsNumber) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldAsNumberTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		
		equalityAsNumberDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A default ResultSet is created 
	 * The equality method will check them to contain the same values (with getFieldValue) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonFieldValueTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		dr0.getFieldValue(DataRowProvider.DATEFIELD);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		// The date has to be rounded to the first milliseconds of the day
		// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
		
		equalityAsValueDataRowTest(dr0, newDr0, s);
	}
	
	/**
	 * A default ResultSet is created, containing 3 DataRows
	 * The equality method will check them to contain the same values (with getFieldValue) as before
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonmultipleDataRowAsValueTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
		DataRow dr0 = rs0.get(0);
		DataRow dr1 = rs0.get(1);
		DataRow dr2 = rs0.get(2);
		String s = rs0.toJson();
		
		assertFalse(s.isEmpty());
		
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		DataRow newDr1 = newRs0.get(1);
		DataRow newDr2 = newRs0.get(2);
		
		// Checking the values of the converted ResultSet
		// The conversion of "Time" and other types are not implemented yet
		// The date has to be rounded to the first milliseconds of the day
		// Reason: In conversion the hours, minutes, seconds and milliseconds are dropped
		
		equalityAsStringDataRowTest(dr0, newDr0, s);
		equalityAsNumberDataRowTest(dr0, newDr0, s);
		equalityAsValueDataRowTest(dr0, newDr0, s);
		
		equalityAsStringDataRowTest(dr1, newDr1, s);
		equalityAsNumberDataRowTest(dr1, newDr1, s);
		equalityAsValueDataRowTest(dr1, newDr1, s);
		
		equalityAsStringDataRowTest(dr2, newDr2, s);
		equalityAsNumberDataRowTest(dr2, newDr2, s);
		equalityAsValueDataRowTest(dr2, newDr2, s);
	}
	
}
