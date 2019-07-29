package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.DataRowProvider;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetToJsonTest {

	@Test
	public void anotherTest() throws Exception {
		ResultSet rs = new ResultSet();
		String s = rs.toJson();
		System.out.println(s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonToJsonOnlyResultSetTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createNumberTypesResultSet();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"LONGFIELD\":66547657568678,\"BIGDECIMALFIELD\":546445464354,\"SCD_BIGDECIMALFIELD\":2345465476565,\"FLOATFIELD\":4.2,\"meta\":{\"LONGFIELD\":{\"ColumnType\":\"-5\"},\"BIGDECIMALFIELD\":{\"ColumnType\":\"3\"},\"SCD_BIGDECIMALFIELD\":{\"ColumnType\":\"3\"},\"FLOATFIELD\":{\"ColumnType\":\"7\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonStringOnlyResultSetTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createStringOnlyResultSet();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"STRINGFIELD\":\"1337\",\"SCD_STRINGFIELD\":\"StringValue\",\"TRD_STRINGFIELD\":\"4StringValue2\",\"FRT_STRINGFIELD\":\"\",\"FTH_STRINGFIELD\":null,\"STH_STRINGFIELD\":\"-1\",\"SVT_STRINGFIELD\":\"1.0\",\"EGT_STRINGFIELD\":\"\\u00C3\\u201E\\u00C3\\u00A4\\u00C3\\u2013\\u00C3\\u00B6\\u00C3\\u0153\\u00C3\\u00BC\\u00C3\\u0178\",\"NTH_STRINGFIELD\":\"\\u00C3\\u2021\\u00C3\\u00A7\\u00C3\\u20AC\\u00C3\\u00A0\\u00C3\\uFFFD\\u00C3\\u00A1\\u00C3\\u201A\\u00C3\\u00A2\\u00C3\\u02C6\\u00C3\\u00A8\\u00C3\\u2030\\u00C3\\u00A9\\u00C3\\u0160\\u00C3\\u00AA\\u00C3\\u201D\\u00C3\\u00B4\\u00C3\\u2122\\u00C3\\u00B9\\u00C3\\u203A\\u00C3\\u00BB\\u00C3\\u017D\\u00C3\\u00AE\\u00C3\\uFFFD\\u00C3\\u00AF\\u00C2\\u0178\\u00C3\\u00BF\",\"TTH_STRINGFIELD\":\"\\u00C2\\u20AC%$\\u00C2\\u00A7#^\\u00C2\\u00B0!?\\u0026{}[]:.;,|\",\"ELT_STRINGFIELD\":\"+-~\\u003C\\u003E=/*\",\"meta\":{\"STRINGFIELD\":{\"ColumnType\":\"12\"},\"SCD_STRINGFIELD\":{\"ColumnType\":\"12\"},\"TRD_STRINGFIELD\":{\"ColumnType\":\"12\"},\"FRT_STRINGFIELD\":{\"ColumnType\":\"12\"},\"FTH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"STH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"SVT_STRINGFIELD\":{\"ColumnType\":\"12\"},\"EGT_STRINGFIELD\":{\"ColumnType\":\"12\"},\"NTH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"TTH_STRINGFIELD\":{\"ColumnType\":\"12\"},\"ELT_STRINGFIELD\":{\"ColumnType\":\"12\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonDefaultResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet();
		String s = rs0.toJson();

//		System.out.println(s);
//		System.out.println(
//				"[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}]");

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonDefaultNullResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSet(true);
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":null,\"DOUBLEFIELD\":null,\"DATEFIELD\":null,\"BOOLFIELD\":null,\"TIMESTAMPFIELD\":null,\"TIMEFIELD\":null,\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMEFIELD\":{\"ColumnType\":\"12\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonMinMaxResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createDefaultResultSetMinMax();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"INTFIELD\":2147483647,\"SCD_INTFIELD\":-2147483648,\"DOUBLEFIELD\":1.7976931348623157E308,\"SCD_DOUBLEFIELD\":4.9E-324,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":false,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"INTFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"4\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_INTFIELD\":{\"ColumnType\":\"4\"},\"DOUBLEFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"8\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_DOUBLEFIELD\":{\"ColumnType\":\"8\"},\"DATEFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"91\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"SOMETYPE\":\"TRUTH\",\"ColumnType\":\"16\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"93\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonMultipleDataRowResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
		String s = rs0.toJson();

//		System.out.println(s);
//		System.out.println(
//				"[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}]");

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonNestedDataRowsResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createNestedDataRowsResultSet();
		String s = rs0.toJson();

//		System.out.println(s);
//		System.out.println(
//				"[{\"NESTEDDATAROW1\":{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},\"NESTEDDATAROW2\":{\"INTFIELD\":2147483647,\"SCD_INTFIELD\":-2147483648,\"DOUBLEFIELD\":1.7976931348623157E308,\"SCD_DOUBLEFIELD\":4.9E-324,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":false,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"INTFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"4\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_INTFIELD\":{\"ColumnType\":\"4\"},\"DOUBLEFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"8\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_DOUBLEFIELD\":{\"ColumnType\":\"8\"},\"DATEFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"91\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"SOMETYPE\":\"TRUTH\",\"ColumnType\":\"16\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"93\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"}}},\"meta\":{\"NESTEDDATAROW1\":{\"ColumnType\":\"-974\"},\"NESTEDDATAROW2\":{\"ColumnType\":\"-974\"}}}]");

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"NESTEDDATAROW1\":{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},\"NESTEDDATAROW2\":{\"INTFIELD\":2147483647,\"SCD_INTFIELD\":-2147483648,\"DOUBLEFIELD\":1.7976931348623157E308,\"SCD_DOUBLEFIELD\":4.9E-324,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":false,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"INTFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"4\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_INTFIELD\":{\"ColumnType\":\"4\"},\"DOUBLEFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"8\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_DOUBLEFIELD\":{\"ColumnType\":\"8\"},\"DATEFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"91\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"SOMETYPE\":\"TRUTH\",\"ColumnType\":\"16\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"93\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"}}},\"meta\":{\"NESTEDDATAROW1\":{\"ColumnType\":\"-974\"},\"NESTEDDATAROW2\":{\"ColumnType\":\"-974\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonNestedResultSetsResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createNestedResultSetsResultSet();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"NESTEDRESULTSET1\":[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}],\"NESTEDRESULTSET2\":[{\"INTFIELD\":2147483647,\"SCD_INTFIELD\":-2147483648,\"DOUBLEFIELD\":1.7976931348623157E308,\"SCD_DOUBLEFIELD\":4.9E-324,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":false,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"INTFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"4\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_INTFIELD\":{\"ColumnType\":\"4\"},\"DOUBLEFIELD\":{\"SOMETYPE\":\"NUMBER\",\"ColumnType\":\"8\",\"ISDIFFERENT\":\"1\",\"TWO\":\"\"},\"SCD_DOUBLEFIELD\":{\"ColumnType\":\"8\"},\"DATEFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"91\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"SOMETYPE\":\"TRUTH\",\"ColumnType\":\"16\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"SOMETYPE\":\"MOMENT\",\"ColumnType\":\"93\",\"ISDIFFERENT\":\"0\",\"THREE\":\"\"}}}],\"meta\":{\"NESTEDRESULTSET1\":{\"ColumnType\":\"-975\"},\"NESTEDRESULTSET2\":{\"ColumnType\":\"-975\"}}}]",
				s);
	}

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonNestedResultSetWithMultipleDataRowsResultSet() throws Exception {
		ResultSet rs0 = ResultSetProvider.createNestedResultSetsWithMultipleDataRowsResultSet();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"NESTEDRESULTSET1\":[{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TYPE\":\"NUMBER\",\"TWO\":\"\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}},{\"LISTFIELD\":[\"hello\",\"world\"],\"INTFIELD\":1,\"DOUBLEFIELD\":1.0,\"DATEFIELD\":\"1970-01-01T00:00:00\",\"BOOLFIELD\":true,\"TIMESTAMPFIELD\":\"1970-01-01T01:00:00.0+01:00\",\"meta\":{\"LISTFIELD\":{\"ColumnType\":\"-973\"},\"INTFIELD\":{\"ColumnType\":\"4\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DOUBLEFIELD\":{\"ColumnType\":\"8\",\"TWO\":\"\",\"TYPE\":\"NUMBER\",\"ISNUMERIC\":\"1\"},\"DATEFIELD\":{\"ColumnType\":\"91\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"BOOLFIELD\":{\"ColumnType\":\"16\",\"TYPE\":\"TRUTH\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"},\"TIMESTAMPFIELD\":{\"ColumnType\":\"93\",\"TYPE\":\"MOMENT\",\"ISNUMERIC\":\"0\",\"THREE\":\"\"}}}],\"meta\":{\"NESTEDRESULTSET1\":{\"ColumnType\":\"-975\"}}}]",
				s);
	}

	/**
	 * This test should evolve with the implementation.
	 * 
	 * @throws Exception
	 */
	@Test
	public void mostBasicOne() throws Exception {
		ResultSet rs = new ResultSet();
		DataRow dr = new DataRow();

		// Values are written
		dr.setFieldValue("hello", "world");
		dr.setFieldValue("bye", "world");
		dr.setFieldValue("number", 5);
		dr.setFieldValue("double", 5.0);
		dr.setFieldValue("hello2", "world2");
		DataRow nested = new DataRow();
		nested.setFieldValue("hey", "im nested");
		nested.setFieldValue("really", "trust me");
		nested.setFieldValue("number", 534);
		dr.setFieldValue("nested", nested);

		// Attributes are written
		dr.setFieldAttribute("hello", "ColumnType", Integer.toString(java.sql.Types.VARCHAR));

		DataRow dr2 = new DataRow();

		// Values are written
		dr2.setFieldValue("hello", "world");
		dr2.setFieldValue("hello2", "world2");

		dr2.setFieldAttribute("hello2", "ColumnType", Integer.toString(java.sql.Types.VARCHAR));

		rs.add(DataRowProvider.buildSampleDataRow(false));
		rs.add(DataRowProvider.buildSampleDataRow(false));
		rs.toJson();
	}

	/**
	 * The types byte, short and long couldn't be converted because of a bug in the
	 * datafield which made wrong casts, leading to a ClassCastExcpetion. This was
	 * corrected now, so this test shouldn't cause any trouble.
	 * 
	 * @throws Exception
	 */
	@Test
	public void numberTypesTest() throws Exception {
		ResultSet rs = new ResultSet();
		DataRow dr = DataRowProvider.buildNumberTypesDataRow2();
		rs.add(dr);

		assertEquals(rs.toJson(),
				"[{\"INTFIELD\":5,\"BYTEFIELD\":5,\"SHORTFIELD\":42,\"LONGFIELD\":66547657568678,\"meta\":{\"INTFIELD\":{\"ColumnType\":\"4\"},\"BYTEFIELD\":{\"ColumnType\":\"-6\"},\"SHORTFIELD\":{\"ColumnType\":\"5\"},\"LONGFIELD\":{\"ColumnType\":\"-5\"}}}]");
	}
}
