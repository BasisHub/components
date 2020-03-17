package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;

import org.apache.maven.surefire.shade.org.apache.commons.io.Charsets;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;

class SpecialCharacterEncodingTest {

	@Test
	void testEuroSign() {
		DataRow dr = new DataRow();
		String json="";
		try {
			dr.setFieldValue("EUROSIGN", new String("\200".getBytes("ISO_8859_1")));
			dr.setFieldValue("SMALLADIARESIS", "\344");
			dr.setFieldValue("SMALLUDIARESIS", "ü");

			json = dr.toJson();
		} catch (Exception e) {
			e.printStackTrace();
		}
		assertEquals("[{\"EUROSIGN\":\"\\u20AC\",\"SMALLADIARESIS\":\"\\u00E4\",\"SMALLUDIARESIS\":\"\\u00FC\",\"meta\":{\"EUROSIGN\":{\"ColumnType\":\"12\"},\"SMALLADIARESIS\":{\"ColumnType\":\"12\"},\"SMALLUDIARESIS\":{\"ColumnType\":\"12\"}}}]",json);
		
	}

}
