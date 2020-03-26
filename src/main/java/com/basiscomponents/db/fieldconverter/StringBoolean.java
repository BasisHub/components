package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;

public class StringBoolean implements IConversionRule {

	String trueString, falseString;

	@SuppressWarnings("unused")
	private StringBoolean() {
	}

	public StringBoolean(String trueString, String falseString) {
		this.trueString = trueString;
		this.falseString = falseString;
	}

	@Override
	public DataField serialize(DataField field) {
		if (field.getBoolean())
			return new DataField(trueString);
		else
			return new DataField(falseString);
	}

	@Override
	public DataField deserialize(DataField field) {
		if (field.getObject().equals(trueString))
			return new DataField(Boolean.TRUE);
		else
			return new DataField(Boolean.FALSE);
	}

	@Override
	public int getTargetFieldType() {
		return 16; // java.sql.Types.BOOLEAN
	}

	@Override
	public int getSourceFieldType() {
		return 12; // java.sql.Types.VARCHAR
	}

}
