package com.basiscomponents.db.fieldconverter;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.basiscomponents.db.DataField;

public class FieldConverter_StringDate_Julian implements IConversionRule {
	
	String trueString, falseString;
	public FieldConverter_StringDate_Julian() {
	}

	@Override
	public DataField serialize(DataField field) {
			return new DataField(com.basis.util.BasisDate.jul((Date) field.getValue()));
	}

	@Override
	public DataField deSerialize(DataField field) {

		int  tmp = Integer.valueOf((String)field.getObject().toString());
		try {
			 java.sql.Date o1 = new java.sql.Date(com.basis.util.BasisDate.date(tmp).getTime());
			return new DataField(o1);
		} catch (java.lang.IllegalArgumentException e) {
			System.err.println("Invalid Date String not in Julian form: "+field.getValue().toString());
		}
				
	    return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.DATE;
	}
	

}
