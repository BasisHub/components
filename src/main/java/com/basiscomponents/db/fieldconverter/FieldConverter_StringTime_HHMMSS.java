package com.basiscomponents.db.fieldconverter;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class FieldConverter_StringTime_HHMMSS implements IConversionRule {
	
	String trueString, falseString;
	SimpleDateFormat sdf = new SimpleDateFormat("kkmmss");
	public FieldConverter_StringTime_HHMMSS() {
	}

	@Override
	public DataField serialize(DataField field) {
			return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deSerialize(DataField field) {
		String tmp = (String)field.getObject();
		
	    if (tmp.trim().length() == 6) {
	    	tmp = tmp.substring(0,2)+":"+tmp.substring(2,4)+":"+tmp.substring(4,6);
	    	System.out.println("Time: "+tmp);
	    	try {
	    		Time value = java.sql.Time.valueOf(tmp);	
	    		return new DataField(value);
	    	} catch (java.lang.IllegalArgumentException e) {
	    		System.err.println("Invalid Time String not it HHMMSS form: "+field.getValue().toString());
	    	}
	    }
				
	    return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.TIME;
	}
	

}
