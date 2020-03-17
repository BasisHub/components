package com.basiscomponents.db.datarowtest;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataFieldConverter;
import com.basiscomponents.util.StringDateTimeGuesser;

class JsonIsoFormatTest {

	final String json_meta_compare = new String("{\"SOMETIMESTAMP\":\"2020-03-17T19:09:36.772+01:00\",\"SOMEDATE\":\"2020-03-17T00:00:00+01:00\",\"SOMETIME\":\"1970-01-01T19:09:36+00:00\",\"meta\":{\"SOMETIMESTAMP\":{\"ColumnType\":\"93\"},\"SOMEDATE\":{\"ColumnType\":\"91\"},\"SOMETIME\":{\"ColumnType\":\"92\"}}}");
	final String json_plain_compare = new String("{\"SOMETIMESTAMP\":\"2020-03-17T19:09:36.772+01:00\",\"SOMEDATE\":\"2020-03-17T00:00:00+01:00\",\"SOMETIME\":\"1970-01-01T19:09:36+00:00\"}");

	final String json_meta_compare2 = new String("{\"SOMETIMESTAMP\":\"x020-03-17T19:09:36.772+01:00\",\"SOMEDATE\":\"x020-03-17T00:00:00+01:00\",\"SOMETIME\":\"x970-01-01T19:09:36+00:00\",\"meta\":{\"SOMETIMESTAMP\":{\"ColumnType\":\"12\"},\"SOMEDATE\":{\"ColumnType\":\"12\"},\"SOMETIME\":{\"ColumnType\":\"12\"}}}");
	final String json_plain_compare2 = new String("{\"SOMETIMESTAMP\":\"x020-03-17T19:09:36.772+01:00\",\"SOMEDATE\":\"x020-03-17T00:00:00+01:00\",\"SOMETIME\":\"x970-01-01T19:09:36+00:00\"}");


	
	final long millis = new Long("1584468576772");
	
	@Test
	void testIsoFormats() throws Exception {
		
		
		DataRow dr = new DataRow();
		
		dr.setFieldValue("SOMETIMESTAMP", new java.sql.Timestamp(millis));
		dr.setFieldValue("SOMEDATE", new java.sql.Date(millis));
		dr.setFieldValue("SOMETIME", new java.sql.Time(millis));
		String json = dr.toJsonObject(true, null, false).toString();
		assertEquals(json_meta_compare, json);
		
		DataRow dr2 = DataRow.fromJson(json);
		String json2 = dr2.toJsonObject(true, null, false).toString();
		assertEquals(json,json2);
			
	}

	@Test
	void testDateTimeGuessing() {
		Object o;
		
		String SOMETIMESTAMP=	"2020-03-17T19:09:36.772+01:00";
		o = StringDateTimeGuesser.guess(SOMETIMESTAMP);
		assertEquals("java.sql.Timestamp",o.getClass().getCanonicalName());
		assertEquals(millis,((java.sql.Timestamp)(o)).getTime());
		
		String SOMEDATE=		"2020-03-17T00:00:00+01:00";
		o = StringDateTimeGuesser.guess(SOMEDATE);
		assertEquals("java.sql.Date",o.getClass().getCanonicalName());
		assertEquals("2020-03-17",o.toString());
		
		String SOMETIME=		"1970-01-01T19:09:36+00:00";
		o = StringDateTimeGuesser.guess(SOMETIME);
		assertEquals("java.sql.Time",o.getClass().getCanonicalName());
		assertEquals("19:09:36",o.toString());
		
		String SOMETIME2=		"19:20:36";
		o = StringDateTimeGuesser.guess(SOMETIME2);
		assertEquals("java.sql.Time",o.getClass().getCanonicalName());
		assertEquals("19:20:36",o.toString());
		
		
		String SOMETIMESTAMP1=	"x020-03-17T19:09:36.772+01:00";
		o = StringDateTimeGuesser.guess(SOMETIMESTAMP1);
		assertEquals("java.lang.String",o.getClass().getCanonicalName());
		assertEquals("x020-03-17T19:09:36.772+01:00",o.toString());

		
		String SOMEDATE1=		"x020-03-17T00:00:00+01:00";
		o = StringDateTimeGuesser.guess(SOMEDATE1);
		assertEquals("java.lang.String",o.getClass().getCanonicalName());
		assertEquals("x020-03-17T00:00:00+01:00",o.toString());
		
		String SOMETIME1=		"x970-01-01T19:09:36+00:00";
		o = StringDateTimeGuesser.guess(SOMETIME1);
		assertEquals("java.lang.String",o.getClass().getCanonicalName());
		assertEquals("x970-01-01T19:09:36+00:00",o.toString());
		
		String SOMETIME21=		"x9:20:36";
		o = StringDateTimeGuesser.guess(SOMETIME21);
		assertEquals("java.lang.String",o.getClass().getCanonicalName());
		assertEquals("x9:20:36",o.toString());
		
	}
	
	@Test
	void testIsoFormatJsonGuessing() throws Exception {
		DataRow dr = DataRow.fromJson(json_plain_compare);
		String json = dr.toJsonObject(true,null,false).toString();
		assertEquals(json_meta_compare, json);
		
		DataRow dr2 = DataRow.fromJson(json_plain_compare2);
		String json2 = dr2.toJsonObject(true,null,false).toString();
		assertEquals(json_meta_compare2, json2);
		
	}
}
