package com.basiscomponents.db.fieldconverter;

import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class StringDate_YYYYMMDD implements IConversionRule {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public StringDate_YYYYMMDD() {
	}

	@Override
	public DataField serialize(DataField field) {
		return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deserialize(DataField field) {
		String tmp = (String) field.getObject();

		if (tmp.trim().length() == 8) {
			tmp = tmp.substring(0, 4) + "-" + tmp.substring(4, 6) + "-" + tmp.substring(6, 8);
			try {
				java.sql.Date value = java.sql.Date.valueOf(tmp);
				return new DataField(value);
			} catch (java.lang.IllegalArgumentException e) {
				System.err.println("Invalid YYYYMMDD date string: " + field.getValue().toString());
			}
		}

		return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.DATE;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
