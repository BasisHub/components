package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.ResultSetProvider;

public class ResultSetToJsonTest {

	/**
	 * A result set is created. The toJsonString is checked to be correct.
	 * 
	 * @throws Exception
	 */
	@Test
	public void toJSonStringOnlyResultSetTest() throws Exception {
		ResultSet rs0 = ResultSetProvider.createToJsonOnlyResultSet();
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals(
				"[{\"LONGFIELD\":66547657568678,\"BIGDECIMALFIELD\":546445464354,\"SCD_BIGDECIMALFIELD\":2345465476565,\"FLOATFIELD\":4.2,\"meta\":{\"LONGFIELD\":{\"ColumnType\":\"-5\"},\"BIGDECIMALFIELD\":{\"ColumnType\":\"3\"},\"SCD_BIGDECIMALFIELD\":{\"ColumnType\":\"3\"},\"FLOATFIELD\":{\"ColumnType\":\"7\"}}}]",
				s);

	}
}
