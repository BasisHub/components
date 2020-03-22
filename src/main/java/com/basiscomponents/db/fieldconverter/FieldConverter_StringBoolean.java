package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;

public class FieldConverter_StringBoolean implements IConversionRule {
	
	
	
	String trueString, falseString;
	
	private FieldConverter_StringBoolean() {}
	
	public FieldConverter_StringBoolean(String trueString, String falseString) {
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
	public DataField deSerialize(DataField field) {
		if (field.getObject().equals(trueString))
			return new DataField(Boolean.TRUE);
		else
			return new DataField(Boolean.FALSE);
		
	}

	@Override
	public int getTargetFieldType() {
		return 16;
	}
	

}
