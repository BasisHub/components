package com.basiscomponents.db.fieldconverter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class FieldConverter_StringTimestamp implements IConversionRule {
	
	String trueString, falseString;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss.S");
	public FieldConverter_StringTimestamp() {
	}

	@Override
	public DataField serialize(DataField field) {
			return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deSerialize(DataField field) {
		//sample: 2019-08-10 18:01:15.0
		String tmp = (String)field.getObject();
	    	try {
	    		Timestamp value = java.sql.Timestamp.valueOf(tmp);	
	    		return new DataField(value);
	    	} catch (java.lang.IllegalArgumentException e) {
	    		System.err.println("Invalid Timestamp String in correct form: "+field.getValue().toString());
	    	}
				
	    return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.TIMESTAMP;
	}
	

}
