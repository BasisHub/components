package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;

public interface IConversionRule {

	public DataField serialize(DataField field);

	public DataField deserialize(DataField field);

	public int getTargetFieldType();

	public int getSourceFieldType();

}
