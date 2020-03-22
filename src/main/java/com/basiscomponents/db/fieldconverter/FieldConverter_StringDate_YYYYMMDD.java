package com.basiscomponents.db.fieldconverter;

import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class FieldConverter_StringDate_YYYYMMDD implements IConversionRule {
	
	String trueString, falseString;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	public FieldConverter_StringDate_YYYYMMDD() {
	}

	@Override
	public DataField serialize(DataField field) {
			return new DataField(sdf.format(field.getValue()));
	}

	@Override
	public DataField deSerialize(DataField field) {
		String tmp = (String)field.getObject();
		
	    if (tmp.trim().length() == 8) {
	    	tmp = tmp.substring(0,4)+"-"+tmp.substring(4,6)+"-"+tmp.substring(6,8);
	    	System.out.println(tmp);
	    	try {
	    		java.sql.Date value = java.sql.Date.valueOf(tmp);	
	    		return new DataField(value);
	    	} catch (java.lang.IllegalArgumentException e) {
	    		System.err.println("Invalid Date String not it YYYYMMDD form: "+field.getValue().toString());
	    	}
	    }
				
	    return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.DATE;
	}
	

}
