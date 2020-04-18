package com.basiscomponents.db.fieldconverter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class StringTimestamp implements IConversionRule {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.S");

	public StringTimestamp() {
	}

	@Override
	public DataField serialize(DataField field) {
		return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deserialize(DataField field) {
		
		String tmp = (String) field.getObject(); // example: 2019-08-10 18:01:15.0

		try {
			Timestamp value = java.sql.Timestamp.valueOf(tmp);
			return new DataField(value);
		} catch (java.lang.IllegalArgumentException e) {
			System.err.println("Invalid timestamp string: " + field.getValue().toString());
		}

		return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.TIMESTAMP;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
