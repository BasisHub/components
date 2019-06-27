package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
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
		DataRow dr0 = rs0.get(0);
		String s = rs0.toJson();

		assertFalse(s.isEmpty());
		assertEquals("[{\"LONGFIELD\":66547657568678,\"meta\":{\"LONGFIELD\":{\"ColumnType\":\"-5\"}}}]", s);

	}
}
