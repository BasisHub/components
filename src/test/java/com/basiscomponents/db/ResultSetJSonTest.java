package com.basiscomponents.db;

import static org.junit.Assert.*;

import org.junit.Test;

import com.basiscomponents.db.util.DataRowProvider;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetJSonTest {
	
	@Test
	public void toJSonFieldAsStringTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet();
		DataRow dr0 = rs0.get(0);
		String s0 = rs0.toJson();
		assertFalse(s0.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s0);
		DataRow newDr0 = newRs0.get(0);
		
		// Pruefung der Werte des neuen ResultSets
		assertEquals("TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals("STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals("TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		
	}
	
	@Test
	public void toJSonFieldAsStringTestMinMax() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSetMinMax();
		DataRow dr0 = rs0.get(0);
		String s0 = rs0.toJson();
		assertFalse(s0.isEmpty());
		ResultSet newRs0 = ResultSet.fromJson(s0);
		DataRow newDr0 = newRs0.get(0);
	
		// Pruefung der Werte des neuen ResultSets
	
		assertEquals("TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals("SCD_DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals("SCD_INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_INTFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_INTFIELD));
		assertEquals("STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals("SCD_BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals("SCD_SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals("SCD_LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_LONGFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
		assertEquals("SCD_BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_BIGDECIMALFIELD));
		assertEquals("TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		assertEquals("SCD_FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.SCD_FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.SCD_FLOATFIELD));
	}
	
	@Test
	public void toJSonFieldAsNumberTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Pruefung der Werte des neuen ResultSets
		assertEquals("TIMESTAMPFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsNumber(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.DATEFIELD), newDr0.getFieldAsNumber(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BOOLFIELD), newDr0.getFieldAsNumber(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsNumber(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.INTFIELD), newDr0.getFieldAsNumber(DataRowProvider.INTFIELD));
	//	assertEquals("STRINGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BYTEFIELD), newDr0.getFieldAsNumber(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.SHORTFIELD), newDr0.getFieldAsNumber(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.LONGFIELD), newDr0.getFieldAsNumber(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsNumber(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals("TIMEFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.TIMEFIELD), newDr0.getFieldAsNumber(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr0.getFieldAsNumber(DataRowProvider.FLOATFIELD), newDr0.getFieldAsNumber(DataRowProvider.FLOATFIELD));
	}
	
	@Test
	public void toJSonFieldValueTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet();
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();
		assertFalse(s.isEmpty());
		ResultSet rs1 = ResultSet.fromJson(s);
		DataRow newDr0 = rs1.get(0);
		
		// Pruefung der Werte des neuen ResultSets
		assertEquals("TIMESTAMPFIELD values differ",dr0.getFieldValue(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldValue(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr0.getFieldValue(DataRowProvider.DATEFIELD), newDr0.getFieldValue(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr0.getFieldValue(DataRowProvider.BOOLFIELD), newDr0.getFieldValue(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr0.getFieldValue(DataRowProvider.DOUBLEFIELD), newDr0.getFieldValue(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr0.getFieldValue(DataRowProvider.INTFIELD), newDr0.getFieldValue(DataRowProvider.INTFIELD));
		assertEquals("STRINGFIELD values differ",dr0.getFieldValue(DataRowProvider.STRINGFIELD), newDr0.getFieldValue(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr0.getFieldValue(DataRowProvider.BYTEFIELD), newDr0.getFieldValue(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr0.getFieldValue(DataRowProvider.SHORTFIELD), newDr0.getFieldValue(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr0.getFieldValue(DataRowProvider.LONGFIELD), newDr0.getFieldValue(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr0.getFieldValue(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldValue(DataRowProvider.BIGDECIMALFIELD));
		assertEquals("TIMEFIELD values differ",dr0.getFieldValue(DataRowProvider.TIMEFIELD), newDr0.getFieldValue(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr0.getFieldValue(DataRowProvider.FLOATFIELD), newDr0.getFieldValue(DataRowProvider.FLOATFIELD));
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
		
		// Pruefung der Werte des neuen ResultSets
		assertEquals("TIMESTAMPFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr0.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DATEFIELD), newDr0.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr0.getFieldAsString(DataRowProvider.BOOLFIELD), newDr0.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr0.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr0.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr0.getFieldAsString(DataRowProvider.INTFIELD), newDr0.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals("STRINGFIELD values differ",dr0.getFieldAsString(DataRowProvider.STRINGFIELD), newDr0.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr0.getFieldAsString(DataRowProvider.BYTEFIELD), newDr0.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr0.getFieldAsString(DataRowProvider.SHORTFIELD), newDr0.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr0.getFieldAsString(DataRowProvider.LONGFIELD), newDr0.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr0.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals("TIMEFIELD values differ",dr0.getFieldAsString(DataRowProvider.TIMEFIELD), newDr0.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr0.getFieldAsString(DataRowProvider.FLOATFIELD), newDr0.getFieldAsString(DataRowProvider.FLOATFIELD));
		
		assertEquals("TIMESTAMPFIELD values differ",dr1.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr1.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr1.getFieldAsString(DataRowProvider.DATEFIELD), newDr1.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr1.getFieldAsString(DataRowProvider.BOOLFIELD), newDr1.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr1.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr1.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr1.getFieldAsString(DataRowProvider.INTFIELD), newDr1.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals("STRINGFIELD values differ",dr1.getFieldAsString(DataRowProvider.STRINGFIELD), newDr1.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr1.getFieldAsString(DataRowProvider.BYTEFIELD), newDr1.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr1.getFieldAsString(DataRowProvider.SHORTFIELD), newDr1.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr1.getFieldAsString(DataRowProvider.LONGFIELD), newDr1.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr1.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr1.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals("TIMEFIELD values differ",dr1.getFieldAsString(DataRowProvider.TIMEFIELD), newDr1.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr1.getFieldAsString(DataRowProvider.FLOATFIELD), newDr1.getFieldAsString(DataRowProvider.FLOATFIELD));
		
		assertEquals("TIMESTAMPFIELD values differ",dr2.getFieldAsString(DataRowProvider.TIMESTAMPFIELD), newDr2.getFieldAsString(DataRowProvider.TIMESTAMPFIELD));
		assertEquals("DATEFIELD values differ",dr2.getFieldAsString(DataRowProvider.DATEFIELD), newDr2.getFieldAsString(DataRowProvider.DATEFIELD));
		assertEquals("BOOLFIELD values differ",dr2.getFieldAsString(DataRowProvider.BOOLFIELD), newDr2.getFieldAsString(DataRowProvider.BOOLFIELD));
		assertEquals("DOUBLEFIELD values differ",dr2.getFieldAsString(DataRowProvider.DOUBLEFIELD), newDr2.getFieldAsString(DataRowProvider.DOUBLEFIELD));
		assertEquals("INTFIELD values differ",dr2.getFieldAsString(DataRowProvider.INTFIELD), newDr2.getFieldAsString(DataRowProvider.INTFIELD));
		assertEquals("STRINGFIELD values differ",dr2.getFieldAsString(DataRowProvider.STRINGFIELD), newDr2.getFieldAsString(DataRowProvider.STRINGFIELD));
		assertEquals("BYTEFIELD values differ",dr2.getFieldAsString(DataRowProvider.BYTEFIELD), newDr2.getFieldAsString(DataRowProvider.BYTEFIELD));
		assertEquals("SHORTFIELD values differ",dr2.getFieldAsString(DataRowProvider.SHORTFIELD), newDr2.getFieldAsString(DataRowProvider.SHORTFIELD));
		assertEquals("LONGFIELD values differ",dr2.getFieldAsString(DataRowProvider.LONGFIELD), newDr2.getFieldAsString(DataRowProvider.LONGFIELD));
		assertEquals("BIGDECIMALFIELD values differ",dr2.getFieldAsString(DataRowProvider.BIGDECIMALFIELD), newDr2.getFieldAsString(DataRowProvider.BIGDECIMALFIELD));
	//	assertEquals("TIMEFIELD values differ",dr2.getFieldAsString(DataRowProvider.TIMEFIELD), newDr2.getFieldAsString(DataRowProvider.TIMEFIELD));
		assertEquals("FLOATFIELD values differ",dr2.getFieldAsString(DataRowProvider.FLOATFIELD), newDr2.getFieldAsString(DataRowProvider.FLOATFIELD));
		
	}
	
	
}
