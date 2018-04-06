package com.basiscomponents.db.util;

import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.datatypes.TemplatedString;
import com.basis.util.common.TemplateInfo;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.exception.TemplateParseException;

public class TemplateParser {

	public static DataRow dataRowfromTemplate(String template, String record) {
		try {
			TemplatedString stringTemplate;
			stringTemplate = new TemplatedString(template);

			stringTemplate.setBytes(record.getBytes());

			String fieldName;
			byte fieldType;
			int fieldSize;
			String dType = "";

			int value;
			int sqlType;
			DataField df = null;

			DataRow row = new DataRow();

			int fieldCount = stringTemplate.getNumFields();
			for (int i = 0; i < fieldCount; i++) {
				fieldName = stringTemplate.getFieldName(i).toString();
				fieldType = stringTemplate.getFieldType(i);
				fieldSize = stringTemplate.getFieldSize(i);

				switch (fieldType) {
				case TemplateInfo.BLOB:
					sqlType = java.sql.Types.BLOB;
					try {
						df = new DataField(stringTemplate.getFieldAsString(i));
					} catch (Exception e) {
						df = new DataField("");
					}
					break;

				case TemplateInfo.INTEGER:
					sqlType = java.sql.Types.INTEGER;
					try {
						df = new DataField(stringTemplate.getFieldAsNumber(i).toBigInteger());
					} catch (Exception e) {
						df = new DataField(0);
					}
					break;

				case TemplateInfo.CHARACTER:
					sqlType = java.sql.Types.CHAR;
					try {
						df = new DataField(stringTemplate.getFieldAsString(i));
					} catch (Exception e) {
						df = new DataField("");
					}
					break;

				case TemplateInfo.RESIDENT_FLOAT:
					sqlType = java.sql.Types.FLOAT;
					try {
						df = new DataField(stringTemplate.getFloat(i));
					} catch (Exception e) {
						df = new DataField(0f);
					}
					break;

				case TemplateInfo.RESIDENT_DOUBLE:
					sqlType = java.sql.Types.DOUBLE;
					try {
						df = new DataField(stringTemplate.getDouble(i));
					} catch (Exception e) {
						df = new DataField(0d);
					}
					break;

				case TemplateInfo.BUS:
				case TemplateInfo.NUMERIC:
				case TemplateInfo.ADJN_BUS:
				case TemplateInfo.BCD_FLOAT:
				case TemplateInfo.IEEE_FLOAT:
				case TemplateInfo.ORDERED_NUMERIC:
				case TemplateInfo.UNSIGNED_INTEGER:
					sqlType = java.sql.Types.NUMERIC;
					try {
						df = new DataField(stringTemplate.getFieldAsNumber(i).toBigDecimal());
					} catch (Exception e) {
						df = new DataField(new java.math.BigDecimal(0));
					}
					break;

				default:
					sqlType = java.sql.Types.VARCHAR;
					try {
						df = new DataField(stringTemplate.getFieldAsString(i));
					} catch (Exception e) {
						df = new DataField("");
					}
					break;
				}

				if (sqlType == java.sql.Types.NUMERIC) {
					try {
						dType = stringTemplate.getAttribute(fieldName, "DTYPE");

						if (dType.equals("D") && fieldSize == 8) {
							// Date
							sqlType = 9; // BASIS Date
							value = stringTemplate.getFieldAsNumber(i).intValue();
							df = new DataField(value);
						} else if (dType.equals("N") && fieldSize == 1) {
							// Boolean
							sqlType = java.sql.Types.BOOLEAN;
							if (stringTemplate.getFieldAsNumber(i).intValue() == 0) {
								df = new DataField(false);
							} else {
								df = new DataField(true);
							}
						}
					} catch (Exception e) {
						// ignoring because not all fields have an attribute DTYPE
					}
				}
				row.addDataField(fieldName, sqlType, df);
			}
			return row;
		} catch (BBjException | NoSuchFieldException | IndexOutOfBoundsException e1) {
			throw new TemplateParseException("Template could not get parsed:" + template + "]", e1);
		}
	}

	private TemplateParser() {
	}
}
