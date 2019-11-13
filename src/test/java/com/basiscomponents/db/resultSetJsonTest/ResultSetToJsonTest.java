package com.basiscomponents.db.resultSetJsonTest;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.ResultSetProvider;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ResultSetToJsonTest {

	public ResultSetToJsonTest() throws ParseException {
	}

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

	@Test
	public void toJsonNestedObjects() throws Exception {
		ResultSet rs = new ResultSet();
		ResultSet customers = new ResultSet();
			for(int i =0; i<10;i++) {
				DataRow dr = new DataRow();
				dr.addDataField("CUSTOMER", new DataField(i));
				dr.addDataField("ONLINE", new DataField(i%2 == 1?"TRUE":"FALSE"));
				customers.add(dr);
			}


		DataRow dr = new DataRow();
		dr.addDataField("$type", new DataField("Non existent"));
		dr.addDataField("Store", new DataField("Bielefeld"));
		dr.addDataField("customers", new DataField(customers));
		rs.add(dr);
		String result = rs.toJson();
		Assert.assertNotNull(result);
		Assert.assertTrue(result.contains("customers"));
	}
}
