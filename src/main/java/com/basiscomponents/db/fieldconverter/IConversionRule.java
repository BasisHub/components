package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

public interface IConversionRule {

	public DataField serialize(DataField field, DataRow dr);

	public DataField deserialize(DataField field, DataRow dr);

	public int getTargetFieldType();

	public int getSourceFieldType();

}
