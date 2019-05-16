package com.basiscomponents.db;

import static org.junit.Assert.*;

import org.junit.Test;

import com.basiscomponents.db.util.DataRowProvider;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetToJSonTest {
	
	@Test
	public void toJSonFieldAsStringNullTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(true);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// The convertion of "Time" and other types are not implemented yet
		
		assertEquals(s +"\n TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +"\n DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +"\n BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +"\n DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +"\n INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +"\n STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +"\n BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +"\n SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +"\n LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +"\n BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
//		assertEquals(s +"\n TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +"\n FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		
	}
	
	@Test
	public void toJSonStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createStringOnlyResultSet();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// Strings containing only numbers can be converted, otherwise there will be a NumberFormatException
		
		assertEquals(s +" STRINGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.STRINGFIELD));
		try {
			dr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD);
			assertTrue(false);
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			newDr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD);
			assertTrue(false);
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			dr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
			assertTrue(false);
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
		try {
			newDr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
			assertTrue(false);
		} catch (NumberFormatException e){
		// Caught correct exception	
		}
	}
	
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
		
		assertEquals(s +"\n TIMESTAMPFIELD attributes differ",dr0.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes(), newDr0.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes());
		assertEquals(s +"\n DATEFIELD attributes differ",dr0.getField(DataRowProvider.DATEFIELD).getAttributes(), newDr0.getField(DataRowProvider.DATEFIELD).getAttributes());
		assertEquals(s +"\n BOOLFIELD attributes differ",dr0.getField(DataRowProvider.BOOLFIELD).getAttributes(), newDr0.getField(DataRowProvider.BOOLFIELD).getAttributes());
		assertEquals(s +"\n DOUBLEFIELD attributes differ",dr0.getField(DataRowProvider.DOUBLEFIELD).getAttributes(), newDr0.getField(DataRowProvider.DOUBLEFIELD).getAttributes());
		assertEquals(s +"\n INTFIELD attributes differ",dr0.getField(DataRowProvider.INTFIELD).getAttributes(), newDr0.getField(DataRowProvider.INTFIELD).getAttributes());
		assertEquals(s +"\n STRINGFIELD attributes differ",dr0.getField(DataRowProvider.STRINGFIELD).getAttributes(), newDr0.getField(DataRowProvider.STRINGFIELD).getAttributes());
		assertEquals(s +"\n BYTEFIELD attributes differ",dr0.getField(DataRowProvider.BYTEFIELD).getAttributes(), newDr0.getField(DataRowProvider.BYTEFIELD).getAttributes());
		assertEquals(s +"\n SHORTFIELD attributes differ",dr0.getField(DataRowProvider.SHORTFIELD).getAttributes(), newDr0.getField(DataRowProvider.SHORTFIELD).getAttributes());
		assertEquals(s +"\n LONGFIELD attributes differ",dr0.getField(DataRowProvider.LONGFIELD).getAttributes(), newDr0.getField(DataRowProvider.LONGFIELD).getAttributes());
		assertEquals(s +"\n BIGDECIMALFIELD attributes differ",dr0.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes(), newDr0.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes());
//		assertEquals(s +"\n TIMEFIELD attributes differ",dr0.getField(DataRowProvider.TIMEFIELD).getAttributes(), newDr0.getField(DataRowProvider.TIMEFIELD).getAttributes());
		assertEquals(s +"\n FLOATFIELD attributes differ",dr0.getField(DataRowProvider.FLOATFIELD).getAttributes(), newDr0.getField(DataRowProvider.FLOATFIELD).getAttributes());
		
	}
	
	@Test
	public void toJSonMultipleDataRowAttributesTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
		DataRow dr0 = rs0.get(0);
		DataRow dr1 = rs0.get(1);
		DataRow dr2 = rs0.get(2);
		System.out.println(dr0.getField(DataRowProvider.TIMEFIELD).toString());
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		DataRow newDr1 = newRs0.get(1);
		DataRow newDr2 = newRs0.get(2);
		
		// Checking the values of the converted ResultSet
		// The initial ResultSet must have DataRows with DataFields with the attribute "ColumnType", otherwise the fromJSon will add it
		
		assertEquals(s +"\n TIMESTAMPFIELD attributes differ",dr0.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes(), newDr0.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes());
		assertEquals(s +"\n DATEFIELD attributes differ",dr0.getField(DataRowProvider.DATEFIELD).getAttributes(), newDr0.getField(DataRowProvider.DATEFIELD).getAttributes());
		assertEquals(s +"\n BOOLFIELD attributes differ",dr0.getField(DataRowProvider.BOOLFIELD).getAttributes(), newDr0.getField(DataRowProvider.BOOLFIELD).getAttributes());
		assertEquals(s +"\n DOUBLEFIELD attributes differ",dr0.getField(DataRowProvider.DOUBLEFIELD).getAttributes(), newDr0.getField(DataRowProvider.DOUBLEFIELD).getAttributes());
		assertEquals(s +"\n INTFIELD attributes differ",dr0.getField(DataRowProvider.INTFIELD).getAttributes(), newDr0.getField(DataRowProvider.INTFIELD).getAttributes());
		assertEquals(s +"\n STRINGFIELD attributes differ",dr0.getField(DataRowProvider.STRINGFIELD).getAttributes(), newDr0.getField(DataRowProvider.STRINGFIELD).getAttributes());
		assertEquals(s +"\n BYTEFIELD attributes differ",dr0.getField(DataRowProvider.BYTEFIELD).getAttributes(), newDr0.getField(DataRowProvider.BYTEFIELD).getAttributes());
		assertEquals(s +"\n SHORTFIELD attributes differ",dr0.getField(DataRowProvider.SHORTFIELD).getAttributes(), newDr0.getField(DataRowProvider.SHORTFIELD).getAttributes());
		assertEquals(s +"\n LONGFIELD attributes differ",dr0.getField(DataRowProvider.LONGFIELD).getAttributes(), newDr0.getField(DataRowProvider.LONGFIELD).getAttributes());
		assertEquals(s +"\n BIGDECIMALFIELD attributes differ",dr0.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes(), newDr0.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes());
//		assertEquals(s +"\n TIMEFIELD attributes differ",dr0.getField(DataRowProvider.TIMEFIELD).getAttributes(), newDr0.getField(DataRowProvider.TIMEFIELD).getAttributes());
		assertEquals(s +"\n FLOATFIELD attributes differ",dr0.getField(DataRowProvider.FLOATFIELD).getAttributes(), newDr0.getField(DataRowProvider.FLOATFIELD).getAttributes());
		
		assertEquals(s +"\n TIMESTAMPFIELD attributes differ",dr1.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes(), newDr1.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes());
		assertEquals(s +"\n DATEFIELD attributes differ",dr1.getField(DataRowProvider.DATEFIELD).getAttributes(), newDr1.getField(DataRowProvider.DATEFIELD).getAttributes());
		assertEquals(s +"\n BOOLFIELD attributes differ",dr1.getField(DataRowProvider.BOOLFIELD).getAttributes(), newDr1.getField(DataRowProvider.BOOLFIELD).getAttributes());
		assertEquals(s +"\n DOUBLEFIELD attributes differ",dr1.getField(DataRowProvider.DOUBLEFIELD).getAttributes(), newDr1.getField(DataRowProvider.DOUBLEFIELD).getAttributes());
		assertEquals(s +"\n INTFIELD attributes differ",dr1.getField(DataRowProvider.INTFIELD).getAttributes(), newDr1.getField(DataRowProvider.INTFIELD).getAttributes());
		assertEquals(s +"\n STRINGFIELD attributes differ",dr1.getField(DataRowProvider.STRINGFIELD).getAttributes(), newDr1.getField(DataRowProvider.STRINGFIELD).getAttributes());
		assertEquals(s +"\n BYTEFIELD attributes differ",dr1.getField(DataRowProvider.BYTEFIELD).getAttributes(), newDr1.getField(DataRowProvider.BYTEFIELD).getAttributes());
		assertEquals(s +"\n SHORTFIELD attributes differ",dr1.getField(DataRowProvider.SHORTFIELD).getAttributes(), newDr1.getField(DataRowProvider.SHORTFIELD).getAttributes());
		assertEquals(s +"\n LONGFIELD attributes differ",dr1.getField(DataRowProvider.LONGFIELD).getAttributes(), newDr1.getField(DataRowProvider.LONGFIELD).getAttributes());
		assertEquals(s +"\n BIGDECIMALFIELD attributes differ",dr1.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes(), newDr1.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes());
//		assertEquals(s +"\n TIMEFIELD attributes differ",dr1.getField(DataRowProvider.TIMEFIELD).getAttributes(), newDr1.getField(DataRowProvider.TIMEFIELD).getAttributes());
		assertEquals(s +"\n FLOATFIELD attributes differ",dr1.getField(DataRowProvider.FLOATFIELD).getAttributes(), newDr1.getField(DataRowProvider.FLOATFIELD).getAttributes());
		
		assertEquals(s +"\n TIMESTAMPFIELD attributes differ",dr2.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes(), newDr2.getField(DataRowProvider.TIMESTAMPFIELD).getAttributes());
		assertEquals(s +"\n DATEFIELD attributes differ",dr2.getField(DataRowProvider.DATEFIELD).getAttributes(), newDr2.getField(DataRowProvider.DATEFIELD).getAttributes());
		assertEquals(s +"\n BOOLFIELD attributes differ",dr2.getField(DataRowProvider.BOOLFIELD).getAttributes(), newDr2.getField(DataRowProvider.BOOLFIELD).getAttributes());
		assertEquals(s +"\n DOUBLEFIELD attributes differ",dr2.getField(DataRowProvider.DOUBLEFIELD).getAttributes(), newDr2.getField(DataRowProvider.DOUBLEFIELD).getAttributes());
		assertEquals(s +"\n INTFIELD attributes differ",dr2.getField(DataRowProvider.INTFIELD).getAttributes(), newDr2.getField(DataRowProvider.INTFIELD).getAttributes());
		assertEquals(s +"\n STRINGFIELD attributes differ",dr2.getField(DataRowProvider.STRINGFIELD).getAttributes(), newDr2.getField(DataRowProvider.STRINGFIELD).getAttributes());
		assertEquals(s +"\n BYTEFIELD attributes differ",dr2.getField(DataRowProvider.BYTEFIELD).getAttributes(), newDr2.getField(DataRowProvider.BYTEFIELD).getAttributes());
		assertEquals(s +"\n SHORTFIELD attributes differ",dr2.getField(DataRowProvider.SHORTFIELD).getAttributes(), newDr2.getField(DataRowProvider.SHORTFIELD).getAttributes());
		assertEquals(s +"\n LONGFIELD attributes differ",dr2.getField(DataRowProvider.LONGFIELD).getAttributes(), newDr2.getField(DataRowProvider.LONGFIELD).getAttributes());
		assertEquals(s +"\n BIGDECIMALFIELD attributes differ",dr2.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes(), newDr2.getField(DataRowProvider.BIGDECIMALFIELD).getAttributes());
//		assertEquals(s +"\n TIMEFIELD attributes differ",dr2.getField(DataRowProvider.TIMEFIELD).getAttributes(), newDr2.getField(DataRowProvider.TIMEFIELD).getAttributes());
		assertEquals(s +"\n FLOATFIELD attributes differ",dr2.getField(DataRowProvider.FLOATFIELD).getAttributes(), newDr2.getField(DataRowProvider.FLOATFIELD).getAttributes());
		
	}
	
	@Test
	public void toJSonFieldAsStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
		
		// Checking the values of the converted ResultSet
		// The convertion of "Time" and other types are not implemented yet
		
		assertEquals(s +"\n TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +"\n DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +"\n BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +"\n DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +"\n INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +"\n STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +"\n BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +"\n SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +"\n LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +"\n BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
//		assertEquals(s +"\n TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +"\n FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		
	}
	
	@Test
	public void toJSonFieldAsStringTestMinMax() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSetMinMax();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s);
		DataRow newDr0 = newRs0.get(0);
	
		// Checking the values of the converted ResultSet
		// The convertion of "Time" and other types are not implemented yet
		// BYTEFIELD,SHORTFIELD can only be converted if their values are between [Byte.MAX_VALUE-1,Byte.MIN_VALUE+1] or [Short.MAX_VALUE-1,Short.MIN_VALUE+1] otherwise there will be a ClassCastException
		// LONGFIELD cannot take the Long.MAX_VALUE (NumberFormatException)
	
		assertEquals(s +"\n TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +"\n DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +"\n BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +"\n DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +"\n SCD_DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_DOUBLEFIELD));
		assertEquals(s +"\n INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +"\n SCD_INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_INTFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_INTFIELD));
		assertEquals(s +"\n STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +"\n BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +"\n SCD_BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_BYTEFIELD));
		assertEquals(s +"\n SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +"\n SCD_SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_SHORTFIELD));
		assertEquals(s +"\n LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +"\n SCD_LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_LONGFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_LONGFIELD));
		assertEquals(s +"\n BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
		assertEquals(s +"\n SCD_BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_BIGDECIMALFIELD));
//		assertEquals(s +"\n TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +"\n FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		assertEquals(s +"\n SCD_FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_FLOATFIELD));
	}
	
	@Test
	public void toJSonFieldAsNumberTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Checking the values of the converted ResultSet
		// The convertion of "Time" and other types are not implemented yet
		
		assertEquals(s +" TIMESTAMPFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsNumber(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +" DATEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.DATEFIELD), newDr0.getFieldAsNumber(DataRowProvider.DATEFIELD));
		assertEquals(s +" BOOLFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BOOLFIELD), newDr0.getFieldAsNumber(DataRowProvider.BOOLFIELD));
		assertEquals(s +" DOUBLEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsNumber(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +" INTFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.INTFIELD), newDr0.getFieldAsNumber(DataRowProvider.INTFIELD));
		assertEquals(s +" STRINGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.STRINGFIELD));
		assertEquals(s +" BYTEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BYTEFIELD), newDr0.getFieldAsNumber(DataRowProvider.BYTEFIELD));
		assertEquals(s +" SHORTFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.SHORTFIELD), newDr0.getFieldAsNumber(DataRowProvider.SHORTFIELD));
		assertEquals(s +" LONGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.LONGFIELD), newDr0.getFieldAsNumber(DataRowProvider.LONGFIELD));
		assertEquals(s +" BIGDECIMALFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsNumber(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals(s +" TIMEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.TIMEFIELD), newDr0.getFieldAsNumber(DataRowProvider.TIMEFIELD));
		assertEquals(s +" FLOATFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.FLOATFIELD), newDr0.getFieldAsNumber(DataRowProvider.FLOATFIELD));
	}
	
	@Test
	public void toJSonFieldValueTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Checking the values of the converted ResultSet
		// The convertion of "Time" and other types are not implemented yet
		// The equals method of "Date" doesn't seem to work fine
		System.out.println(dr0.getFieldValue(DataRowProvider.DATEFIELD));
		System.out.println(newDr0.getFieldValue(DataRowProvider.DATEFIELD));
		
		assertEquals(s +" TIMESTAMPFIELD values differ",dr0.getFieldValue(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldValue(DataRowProvider.TIMESTAMPFIELD));
//		assertEquals(s +" DATEFIELD values differ",dr0.getFieldValue(DataRowProvider.DATEFIELD), newDr0.getFieldValue(DataRowProvider.DATEFIELD));
		assertEquals(s +" BOOLFIELD values differ",dr0.getFieldValue(DataRowProvider.BOOLFIELD), newDr0.getFieldValue(DataRowProvider.BOOLFIELD));
		assertEquals(s +" DOUBLEFIELD values differ",dr0.getFieldValue(DataRowProvider.DOUBLEFIELD), newDr0.getFieldValue(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +" INTFIELD values differ",dr0.getFieldValue(DataRowProvider.INTFIELD), newDr0.getFieldValue(DataRowProvider.INTFIELD));
		assertEquals(s +" STRINGFIELD values differ",dr0.getFieldValue(DataRowProvider.STRINGFIELD), newDr0.getFieldValue(DataRowProvider.STRINGFIELD));
		assertEquals(s +" BYTEFIELD values differ",dr0.getFieldValue(DataRowProvider.BYTEFIELD), newDr0.getFieldValue(DataRowProvider.BYTEFIELD));
		assertEquals(s +" SHORTFIELD values differ",dr0.getFieldValue(DataRowProvider.SHORTFIELD), newDr0.getFieldValue(DataRowProvider.SHORTFIELD));
		assertEquals(s +" LONGFIELD values differ",dr0.getFieldValue(DataRowProvider.LONGFIELD), newDr0.getFieldValue(DataRowProvider.LONGFIELD));
		assertEquals(s +" BIGDECIMALFIELD values differ",dr0.getFieldValue(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldValue(DataRowProvider.BIGDECIMALFIELD));
//		assertEquals(s +" TIMEFIELD values differ",dr0.getFieldValue(DataRowProvider.TIMEFIELD), newDr0.getFieldValue(DataRowProvider.TIMEFIELD));
		assertEquals(s +" FLOATFIELD values differ",dr0.getFieldValue(DataRowProvider.FLOATFIELD), newDr0.getFieldValue(DataRowProvider.FLOATFIELD));
	}
	
	@Test
	public void multipleDataRowsToJSonTest() throws Exception {
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
		// The convertion of "Time" and other types are not implemented yet
		
		assertEquals(s +" TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +" DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +" BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +" DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +" INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +" STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +" BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +" SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +" LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +" BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals(s +" TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +" FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		
		assertEquals(s +" TIMESTAMPFIELD values differ",dr1.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr1.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +" DATEFIELD values differ",dr1.getFieldAsString(DataRowProvider.DATEFIELD), newDr1.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +" BOOLFIELD values differ",dr1.getFieldAsString(DataRowProvider.BOOLFIELD), newDr1.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +" DOUBLEFIELD values differ",dr1.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr1.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +" INTFIELD values differ",dr1.getFieldAsString(DataRowProvider.INTFIELD), newDr1.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +" STRINGFIELD values differ",dr1.getFieldAsString(DataRowProvider.STRINGFIELD), newDr1.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +" BYTEFIELD values differ",dr1.getFieldAsString(DataRowProvider.BYTEFIELD), newDr1.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +" SHORTFIELD values differ",dr1.getFieldAsString(DataRowProvider.SHORTFIELD), newDr1.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +" LONGFIELD values differ",dr1.getFieldAsString(DataRowProvider.LONGFIELD), newDr1.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +" BIGDECIMALFIELD values differ",dr1.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr1.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals(s +" TIMEFIELD values differ",dr1.getFieldAsString(DataRowProvider.TIMEFIELD), newDr1.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +" FLOATFIELD values differ",dr1.getFieldAsString(DataRowProvider.FLOATFIELD), newDr1.getFieldAsString(DataRowProvider.FLOATFIELD));
		
		assertEquals(s +" TIMESTAMPFIELD values differ",dr2.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr2.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals(s +" DATEFIELD values differ",dr2.getFieldAsString(DataRowProvider.DATEFIELD), newDr2.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals(s +" BOOLFIELD values differ",dr2.getFieldAsString(DataRowProvider.BOOLFIELD), newDr2.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals(s +" DOUBLEFIELD values differ",dr2.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr2.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals(s +" INTFIELD values differ",dr2.getFieldAsString(DataRowProvider.INTFIELD), newDr2.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals(s +" STRINGFIELD values differ",dr2.getFieldAsString(DataRowProvider.STRINGFIELD), newDr2.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals(s +" BYTEFIELD values differ",dr2.getFieldAsString(DataRowProvider.BYTEFIELD), newDr2.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals(s +" SHORTFIELD values differ",dr2.getFieldAsString(DataRowProvider.SHORTFIELD), newDr2.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals(s +" LONGFIELD values differ",dr2.getFieldAsString(DataRowProvider.LONGFIELD), newDr2.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals(s +" BIGDECIMALFIELD values differ",dr2.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr2.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals(s +" TIMEFIELD values differ",dr2.getFieldAsString(DataRowProvider.TIMEFIELD), newDr2.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals(s +"\n FLOATFIELD values differ",dr2.getFieldAsString(DataRowProvider.FLOATFIELD), newDr2.getFieldAsString(DataRowProvider.FLOATFIELD));
		
	}
	
	
}
