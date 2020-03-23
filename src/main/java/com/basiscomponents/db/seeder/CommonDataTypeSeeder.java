package com.basiscomponents.db.seeder;

import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.DataRow;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import com.basiscomponents.db.DataField;

public class CommonDataTypeSeeder {
	
	private static CommonDataTypeSeeder instance;
	private ArrayList jsonObjList;
	
	private CommonDataTypeSeeder() {
		this.loadNames();
	}
	
	public static CommonDataTypeSeeder getInstance() {
		return (CommonDataTypeSeeder.instance != null) ? CommonDataTypeSeeder.instance : new CommonDataTypeSeeder();
	}
	
	public ResultSet retrieve(int resultSetSize) {
		ResultSet rs = new ResultSet();
		int i = 0;
		while(i < resultSetSize) {
			DataRow dr = createDataRow(i);
			rs.add(dr);
			i++;
		}
		return rs;
	}
	
	private void loadNames() {
		InputStream stream = this.getClass().getResourceAsStream("/first_names.json");
		// File jsonFile = new File("./json/first_names.json");
		JsonParser jsonParser = new JsonParser();
		try {
			JsonArray jsonArray = jsonParser.parse(new InputStreamReader(stream)).getAsJsonArray();
			this.jsonObjList = new Gson().fromJson(jsonArray, ArrayList.class);
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonSyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	private DataRow createDataRow(int count) {
		DataRow dr = new DataRow();
		dr.addDataField("INTEGER_COLUMN", java.sql.Types.INTEGER, new DataField(count));
		dr.addDataField("VARCHAR_COLUMN", java.sql.Types.VARCHAR, createVarcharDataField());
		double d = Double.valueOf(count);
		dr.addDataField("DOUBLE_COLUMN", java.sql.Types.DOUBLE, new DataField(d));
		dr.addDataField("DATE_COLUMN", java.sql.Types.DATE, createDateDataField());
		dr.addDataField("TIMESTAMP_COLUMN", java.sql.Types.TIMESTAMP, createTimestampDataField());
		boolean bool = (count % 2) == 0;
		dr.addDataField("BOOLEAN_COLUMN", java.sql.Types.BOOLEAN, new DataField(bool));
		return dr;
	}

	private DataField createVarcharDataField() {
		int rnd = (int) (Math.round(Math.random() * (this.jsonObjList.size()) - 1));
		String s = this.jsonObjList.get(rnd).toString();
		return new DataField(s);
	}

	private DataField createDateDataField() {
	    LocalDate randomDate = createRandomDate();
	    java.sql.Date date = java.sql.Date.valueOf(randomDate);
		return new DataField(date);
	}
	
	private LocalDate createRandomDate() {
		long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
	    long maxDay = LocalDate.of(2020, 1, 31).toEpochDay();
	    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
	    return LocalDate.ofEpochDay(randomDay);
	}
	
	private DataField createTimestampDataField() {
		Timestamp timestamp = createRandomTimestamp();
		return new DataField(timestamp);
	}
	
	private Timestamp createRandomTimestamp() {
		long offset = Timestamp.valueOf("1970-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2020-01-31 00:00:00").getTime();
		long diff = end - offset + 1;
		return new Timestamp(offset + (long)(Math.random() * diff));
	}
}
