package com.basiscomponents.db.fieldconverter;

import com.basis.util.BasisDate;
import com.basiscomponents.db.DataField;

public class StringDate_Julian implements IConversionRule {

	public StringDate_Julian() {
	}

	@Override
	public DataField serialize(DataField field) {
		return new DataField(BasisDate.jul((java.util.Date) field.getValue()));
	}

	@Override
	public DataField deserialize(DataField field) {
		int tmp = Integer.valueOf((String) field.getObject().toString());

		try {
			java.sql.Date o1 = new java.sql.Date(BasisDate.date(tmp).getTime());
			return new DataField(o1);
		} catch (java.lang.IllegalArgumentException e) {
			System.err.println("Invalid Julian date string: " + field.getValue().toString());
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
