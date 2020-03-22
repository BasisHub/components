package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;

public interface IConversionRule {
	
	public int getTargetFieldType();
	
	public DataField serialize(DataField field);
	
	public DataField deSerialize(DataField field);

}
