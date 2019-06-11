package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import com.basiscomponents.bc.util.SQLHelper;
import com.basiscomponents.db.DataRow;

public class SqlHelperMockTests {

	/*
	 * These tests check the functionality of the listed methods:
	 * 
	 * setSqlParams,setPreparedStatementType
	 * 
	 */

	/**
	 * Checks the behavior of the method "setSqlParams" if certain values are null.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperNullTest() throws Exception {

		// Check the method if dr is null
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);
		java.util.List fields = mock(java.util.List.class);
		
		SQLHelper.setSqlParams(ps, null, fields, false);
		verifyZeroInteractions(ps);
		verifyZeroInteractions(fields);

		// Check the method if ps is null
		com.basiscomponents.db.DataRow dr = mock(com.basiscomponents.db.DataRow.class);
		fields = mock(java.util.List.class);

		SQLHelper.setSqlParams(null, dr, fields, false);
		verifyZeroInteractions(dr);
		verifyZeroInteractions(fields);
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order. One of them is a NullField.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperNullFieldTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		dr.setFieldValue("NullField", null);
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perfrom the method
		SQLHelper.setSqlParams(ps, dr, null, false);

		// Verify the Mock
		verify(ps, times(1)).setBoolean(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setInt(ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(3), ArgumentMatchers.eq("Hi"));
		verify(ps, times(1)).setNull(anyInt(), anyInt());
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperSimpleTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);
		
		// Perform the method
		SQLHelper.setSqlParams(ps, dr, null, false);
		
		// Verify the Mock
		verify(ps, times(1)).setBoolean(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setInt(ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(3), ArgumentMatchers.eq("Hi"));
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperComplexTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		dr.setFieldValue("DoubleField", 1.0);
		dr.setFieldValue("BigDecimalField", new BigDecimal("543544434355"));
		dr.setFieldValue("CharField", 'v');
		Date date = new Date(System.currentTimeMillis());
		dr.setFieldValue("DateField", date);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		dr.setFieldValue("TimeStampField", timestamp);
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setSqlParams(ps, dr, null, false);

		// Verify the Mock
		verify(ps, times(1)).setBoolean(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setInt(ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(3), ArgumentMatchers.eq("Hi"));
		verify(ps, times(1)).setDouble(ArgumentMatchers.eq(4), ArgumentMatchers.eq(1.0));
		verify(ps, times(1)).setBigDecimal(ArgumentMatchers.eq(5), ArgumentMatchers.eq(new BigDecimal("543544434355")));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(6), ArgumentMatchers.eq("v"));
		verify(ps, times(1)).setDate(ArgumentMatchers.eq(7), ArgumentMatchers.eq(date));
		verify(ps, times(1)).setTimestamp(ArgumentMatchers.eq(8),
				ArgumentMatchers.eq(timestamp));
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order. One Field is filled with a condition.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperConditionTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		dr.setFieldValue("ConditionField", "cond:<63");
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setSqlParams(ps, dr, null, false);

		// Verify the Mock
		verify(ps, times(1)).setBoolean(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setInt(ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(3), ArgumentMatchers.eq("Hi"));
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(4), anyString());
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperOtherTypesTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("ByteField", Byte.MAX_VALUE);
		dr.setFieldValue("ShortField", Short.MAX_VALUE);
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setSqlParams(ps, dr, null, false);

		// Verify the Mock
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(1), ArgumentMatchers.eq(Byte.MAX_VALUE));
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(2), ArgumentMatchers.eq(Short.MAX_VALUE));
	}

	/**
	 * Tests the use of the variable "fields" which should limit the
	 * PreparedStatement to the DataFields listed in it.
	 * 
	 * @throws Exception
	 */
	@Test
	public void sqlHelperFieldsTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		List<String> fields = new ArrayList<String>();
		fields.add("BoolField");
		fields.add("StringField");
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setSqlParams(ps, dr, fields, false);

		// Verify the Mock
		verify(ps, times(1)).setBoolean(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(2), ArgumentMatchers.eq("Hi"));
	}

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order.
	 * 
	 * @throws Exception
	 */
//	@Test
	public void sqlHelperIsBasisDataBaseTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("IntField", null);
		dr.setFieldAttribute("IntField", "ColumnType", "4");
		String s = null;
		dr.setFieldValue("StringField", s);
		BigDecimal bd = null;
		dr.setFieldValue("BigDecimalField", bd);
//		dr.setFieldValue("NullField", null);
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setSqlParams(ps, dr, null, true);

		// Verify the Mock
		verify(ps, times(1)).setInt(ArgumentMatchers.eq(1), ArgumentMatchers.eq(0));
		verify(ps, times(1)).setString(ArgumentMatchers.eq(2), ArgumentMatchers.eq(""));
		verify(ps, times(1)).setBigDecimal(ArgumentMatchers.eq(3), ArgumentMatchers.eq(new java.math.BigDecimal(0)));
//		verify(ps, times(1)).setString(ArgumentMatchers.eq(4), ArgumentMatchers.eq(""));
	}

	/*
	 * These tests check the functionality of the listed methods:
	 * 
	 * setParameters, isCharType
	 * 
	 */

	/**
	 * Checks the PreparedStatement to be filled with the correct values in the
	 * right order.
	 * 
	 * @throws Exception
	 */
	@Test
	public void setParametersTest() throws Exception {

		// Preparing objects for the method "setSqlParams"
		DataRow dr = new DataRow();
		dr.setFieldValue("BoolField", true);
		dr.setFieldValue("IntField", 3);
		dr.setFieldValue("StringField", "Hi");
		java.sql.PreparedStatement ps = mock(java.sql.PreparedStatement.class);

		// Perform the method
		SQLHelper.setParameters(dr, ps);

		// Verify the Mock
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(1), ArgumentMatchers.eq(true));
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(2), ArgumentMatchers.eq(3));
		verify(ps, times(1)).setObject(ArgumentMatchers.eq(3), ArgumentMatchers.eq("Hi"));
	}

	/**
	 * Checks the simple functionality of isCharType
	 * 
	 */
	@Test
	public void isChartypeTest() {
		boolean test = true;
		test = SQLHelper.isChartype(java.sql.Types.CHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.VARCHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.LONGVARCHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.NCHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.NVARCHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.NVARCHAR) && test;
		test = SQLHelper.isChartype(java.sql.Types.CLOB) && test;

		assertTrue(test);

		test = SQLHelper.isChartype(java.sql.Types.INTEGER) && test;

		assertFalse(test);
	}

}
