package com.basiscomponents.db.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.model.Attribute;
import com.fasterxml.jackson.core.JsonGenerator;

public class MetaDataJsonMapper {

	public static final String ATTRIBUTES = "_attributes";

	private MetaDataJsonMapper() {
	}

	public static void writeDataRowAttributes(HashMap<String, String> attributes, JsonGenerator jsonGenerator)
			throws IOException {
		jsonGenerator.writeFieldName(ATTRIBUTES);
		jsonGenerator.writeStartObject();
		for (Entry<String, String> entry : attributes.entrySet()) {
			jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
		}
		jsonGenerator.writeEndObject();
	}

	public static void writeResultSetMetaData(ResultSet rs, String indexColumn, JsonGenerator jsonGenerator, DataRow dr)
			throws IOException {

			jsonGenerator.writeFieldName("meta");

			jsonGenerator.writeStartObject();

			if (indexColumn != null) {
				jsonGenerator.writeFieldName(indexColumn);
				jsonGenerator.writeStartObject();
				jsonGenerator.writeStringField("ColumnType", "12");
				jsonGenerator.writeEndObject();
			}

		for (HashMap<String, Object> hm : rs.getMetaData()) {

				String c = (String) hm.get("ColumnName");
				if (c != null) {
					jsonGenerator.writeFieldName(c);
					jsonGenerator.writeStartObject();

					final Map<String, Attribute> atr = getFieldAttributes(dr, c).orElseGet(HashMap::new);

					for (Entry<String, Object> entry : hm.entrySet()) {
						if ("ColumnTypeName".equals(entry.getKey()) || "ColumnName".equals(entry.getKey())
								|| atr.containsKey(entry.getKey())) {
							continue;
						}
						String value = null;
						if (entry.getValue() != null)
							value = entry.getValue().toString();

						jsonGenerator.writeStringField(entry.getKey(), value);
					}

				if (atr != null && !atr.isEmpty()) {
						for (Entry<String, Attribute> entry : atr.entrySet()) {
							String k = entry.getKey();
							Attribute v = entry.getValue();
							if (v.getType() == String.class) {
								jsonGenerator.writeStringField(k, v.getValue());
							} else if (v.getType() == int.class) {
								jsonGenerator.writeNumber(v.getIntValue());
							} else if (v.getType() == double.class) {
								jsonGenerator.writeNumber(v.getDoubleValue());
							} else if (v.getType() == boolean.class) {
								jsonGenerator.writeBoolean(v.getBooleanValue());
							}
						}
					}
					jsonGenerator.writeEndObject();
				}
			}
		jsonGenerator.writeEndObject();
	}
		
	public static void writeDataRowMetaData(DataRow dr, JsonGenerator jsonGenerator) {
		
		try {
			BBArrayList<String> fields = dr.getFieldNames();
			boolean mWritten = false;
			for (String fieldname : fields) {
				if (dr.getField(fieldname, true) != null) {
					Map<String, String> fieldAttributes = dr.getFieldAttributes(fieldname);
					if (!fieldAttributes.isEmpty()) {
						if (!mWritten) {
							jsonGenerator.writeFieldName("meta");
							jsonGenerator.writeStartObject();
							mWritten = true;
						}
						jsonGenerator.writeFieldName(fieldname);
						jsonGenerator.writeStartObject();
						for (String itk : fieldAttributes.keySet()) {
							jsonGenerator.writeStringField(itk, fieldAttributes.get(itk));
						}
						jsonGenerator.writeEndObject();
					}
				}
			}
			if (mWritten) {
				jsonGenerator.writeEndObject();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @param dr
	 * @param c
	 * @return
	 */
	private static Optional<Map<String, Attribute>> getFieldAttributes(DataRow dr, String c) {
		Map<String, Attribute> atr;
		try {
			atr = dr.getFieldAttributes2(c);
		} catch (Exception e) {
			e.printStackTrace();
			atr = null;
		}
		return Optional.of(atr);
	}
}
