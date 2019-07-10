package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataFieldJsonMapper;

public class DataRowAndFieldToJsonTest {

	DataField df = new DataField("");
	String s;

	/**
	 * A simple DataField with a number is created and converted to Json.
	 */
	@Test
	public void dataFieldToJsonNumberTest() {
		df.setValue(5);
		s = DataFieldJsonMapper.dataFieldtoJson(df, "number", java.sql.Types.INTEGER);

		assertEquals("{\"number\":5}", s);

		s = DataFieldJsonMapper.dataFieldtoJson(df, "number", java.sql.Types.VARCHAR);

		assertEquals("{\"number\":\"5\"}", s);
	}

	/**
	 * A simple DataField with a string is created and converted to Json.
	 */
	@Test
	public void dataFieldToJsonStringTest() {
		df.setValue("Hello World");
		s = DataFieldJsonMapper.dataFieldtoJson(df, "string", java.sql.Types.VARCHAR);

		assertEquals("{\"string\":\"Hello World\"}", s);
		
		assertThrows(ClassCastException.class,
				() -> DataFieldJsonMapper.dataFieldtoJson(df, "string", java.sql.Types.INTEGER));
	}

	/**
	 * A simple DataField with a boolean is created and converted to Json.
	 */
	@Test
	public void dataFieldToJsonBooleanTest() {
		df.setValue(true);
		s = DataFieldJsonMapper.dataFieldtoJson(df, "bool", java.sql.Types.BOOLEAN);

		assertEquals("{\"bool\":true}", s);

		s = DataFieldJsonMapper.dataFieldtoJson(df, "bool", java.sql.Types.VARCHAR);

		assertEquals("{\"bool\":\"1\"}", s);

		assertThrows(ClassCastException.class,
				() -> DataFieldJsonMapper.dataFieldtoJson(df, "bool", java.sql.Types.INTEGER));
	}

	/**
	 * A simple DataRow is created and it is converted to Json.
	 */
	@Test
	public void dataRowToJsonSimpleTest() {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue("hello", "world");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		s = dr.toJson();
		assertEquals("{\"hello\":\"world\",\"meta\":{\"hello\":{\"ColumnType\":\"12\"}}}", s);
	}
}
