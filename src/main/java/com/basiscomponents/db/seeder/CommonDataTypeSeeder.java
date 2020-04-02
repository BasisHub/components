package com.basiscomponents.db.seeder;

import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.DataRow;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
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
	
	/**
	 * Instantiates a new CommonDataTypeSeeder object
	 */
	private CommonDataTypeSeeder() {
		this.loadNames();
	}
	
	/**
	 * Retuns the CommonDataTypeSeeder instance
	 * 
	 * @return the CommonDataTypeSeeder instance
	 */
	public static CommonDataTypeSeeder getInstance() {
		return (CommonDataTypeSeeder.instance != null) ? CommonDataTypeSeeder.instance : new CommonDataTypeSeeder();
	}
	
	/**
	 * Creates a ResultSet, filled with a specific number of data rows which are filled with common data types data
	 * 
	 * @param resultSetSize	size of the ResultSet
	 * @return returns the creates ResultSet
	 */
	public ResultSet retrieve(int resultSetSize) {
		ResultSet rs = new ResultSet();
		int i = 0;
		while(i < resultSetSize) {
			System.out.println("durchlauf: " + i);
			DataRow dr = createDataRow(i);
			rs.add(dr);
			i++;
		}
		return rs;
	}
	
	/**
	 * Loads all names that can be found in the external JSON file
	 */
	private void loadNames() {
		InputStream stream = this.getClass().getResourceAsStream("/first_names.json");
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
	
	/**
	 * Creates a DataRow with the common data types
	 * 
	 * @param count		The row index
	 * @return	returns the created DataRow
	 */
	private DataRow createDataRow(int count) {
		DataRow dr = new DataRow();
		DataField df;
		df = new DataField(count);
		df.setAttribute("LABEL", "Integer");
		dr.addDataField("INTEGER_COLUMN", java.sql.Types.INTEGER, df);
		
		df = createVarcharDataField();
		df.setAttribute("LABEL", "VarChar");
		dr.addDataField("VARCHAR_COLUMN", java.sql.Types.VARCHAR, df);
		
		double d = Double.valueOf(count);
		df = new DataField(d);
		df.setAttribute("LABEL", "Double");
		dr.addDataField("DOUBLE_COLUMN", java.sql.Types.DOUBLE, df);
		
		df = createDateDataField();
		df.setAttribute("LABEL", "Date");
		dr.addDataField("DATE_COLUMN", java.sql.Types.DATE, df);
		
		df = createTimestampDataField();
		df.setAttribute("LABEL", "Timestamp");
		dr.addDataField("TIMESTAMP_COLUMN", java.sql.Types.TIMESTAMP, df);
		
		boolean bool = (count % 2) == 0;
		df = new DataField(bool);
		df.setAttribute("LABEL", "Boolean");
		dr.addDataField("BOOLEAN_COLUMN", java.sql.Types.BOOLEAN, df);
		return dr;
	}

	/**
	 * Creates a DataField of the VarChar type. The data is randomly chosen of the loaded JSON file.
	 * 
	 * @return	returns the created DataField
	 */
	private DataField createVarcharDataField() {
		int rnd = (int) (Math.round(Math.random() * (this.jsonObjList.size() - 1)));
		String s = this.jsonObjList.get(rnd).toString();
		return new DataField(s);
	}

	/**
	 * Creates a DataField of the Date type. The date is randomly generated.
	 * 
	 * @return	returns the created DataField
	 */
	private DataField createDateDataField() {
	    LocalDate randomDate = createRandomDate();
	    java.sql.Date date = java.sql.Date.valueOf(randomDate);
		return new DataField(date);
	}
	
	/**
	 * Creates a random date.
	 * 
	 * @return	returns the created date
	 */
	private LocalDate createRandomDate() {
		long minDay = LocalDate.of(1970, 1, 1).toEpochDay();
	    long maxDay = LocalDate.of(2020, 1, 31).toEpochDay();
	    long randomDay = ThreadLocalRandom.current().nextLong(minDay, maxDay);
	    return LocalDate.ofEpochDay(randomDay);
	}
	
	/**
	 * Creates a DataField of the Timestamp type. The timestamp is randomly generated.
	 * 
	 * @return	returns the created DataField
	 */
	private DataField createTimestampDataField() {
		Timestamp timestamp = createRandomTimestamp();
		return new DataField(timestamp);
	}
	
	/**
	 * Creates a random timestamp
	 * 
	 * @return	returns the created timestamp
	 */
	private Timestamp createRandomTimestamp() {
		long offset = Timestamp.valueOf("1970-01-01 00:00:00").getTime();
		long end = Timestamp.valueOf("2020-01-31 00:00:00").getTime();
		long diff = end - offset + 1;
		return new Timestamp(offset + (long)(Math.random() * diff));
	}
}
