package com.basiscomponents.db.fieldconverter;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

public class StringTime_HHMMSS implements IConversionRule {

	SimpleDateFormat sdf = new SimpleDateFormat("kkmmss"); // kk=midnight is 24:00, HH=midnight is 00:00

	public StringTime_HHMMSS() {
	}

	@Override
	public DataField serialize(DataField field, DataRow dr) {
		return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deserialize(DataField field, DataRow dr) {
		String tmp = (String) field.getObject();

		if (tmp.trim().length() == 6) {
			tmp = tmp.substring(0, 2) + ":" + tmp.substring(2, 4) + ":" + tmp.substring(4, 6);
			try {
				Time value = java.sql.Time.valueOf(tmp);
				return new DataField(value);
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println("Invalid HHMMSS time string: " + field.getValue().toString());
			}
		}

		return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.TIME;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
