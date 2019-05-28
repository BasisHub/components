package com.basiscomponents.db.util;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.sql.Types;
import java.text.ParseException;
import java.util.*;

public class DataRowFromJsonProvider {

	private static final String COLUMN_TYPE = "ColumnType";
	public static DataRow fromJson(final String in, final DataRow meta) throws IOException, ParseException {
		return fromJson(in,meta,null);
	}
	public static DataRow fromJson(final String in, final DataRow meta, JsonElement attributes)
			throws IOException, ParseException {
		String input = in;
		if (input.length() < 2) {
			return new DataRow();
		}

		input = convertCharsBelowChr32(input);
		input = removeLeadingDataRow(input);
		JsonNode root = buildJsonRoot(input);
		input = wrapInJsonArray(input);

		JsonFactory f = new JsonFactory();
		try (JsonParser jsonParser = f.createParser(input)) {
			jsonParser.nextToken();

			ObjectMapper objectMapper = new ObjectMapper();

			List<?> navigation = objectMapper.readValue(jsonParser,
					objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));
			if (navigation.isEmpty()) {
				return new DataRow();
			}

			DataRow metaRow;
			if (meta == null) {
				metaRow = new DataRow();
			} else {
				metaRow = meta.clone();
			}
			HashMap<?, ?> navigationMap = (HashMap<?, ?>) navigation.get(0);

			createMetaData(metaRow, navigationMap);

			createNonExistingAttributes(root, metaRow, navigationMap);

			DataRow dr = new DataRow();
			if (!metaRow.isEmpty()) {
				createDataFields(root, metaRow, navigationMap, dr);
			} else {
				handleOldFormat(navigation, dr);
			}
			if (attributes!=null){
				attributes.getAsJsonObject().entrySet().stream().forEach(entry->dr.setAttribute(entry.getKey(),entry.getValue().getAsString()));
			}
			return dr;
		}
	}

	private static void createDataFields(JsonNode root, DataRow attributes, HashMap<?, ?> hm, DataRow dr)
			throws ParseException, JsonParseException, IOException {
		
		JsonNode root2;
		if (root.isArray())
			root2 = root.get(0);
		else
			root2 = root;
		for (String fieldName : attributes.getFieldNames()) {
			Object fieldObj = hm.get(fieldName);
			int fieldType = attributes.getFieldType(fieldName);
			if (fieldObj == null) {
				dr.addDataField(fieldName, fieldType, new DataField(null));
				dr.setFieldAttributes(fieldName, attributes.getFieldAttributes(fieldName));
				continue;
			}
			switch (fieldType) {
			case -973:
				// nested ArrayList or BBjVector
				JsonNode x=root.get(0).get(fieldName);
				ObjectMapper mapper = new ObjectMapper();
				ObjectReader reader = mapper.readerFor(new TypeReference<List<Object>>() {});
				List<String> list = reader.readValue(x);
				dr.setFieldValue(fieldName, list);
				break;

			case -974:
				String nestedJson = "";
				nestedJson =root2.get(fieldName).toString();
				dr.setFieldValue(fieldName, DataRow.fromJson(nestedJson));
				break;
			case -975:
				String nestedJson1 = "";
				nestedJson1 = root2.get(fieldName).toString();
				dr.setFieldValue(fieldName, ResultSet.fromJson(nestedJson1));
				break;

			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
			case java.sql.Types.NVARCHAR:
			case java.sql.Types.NCHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.LONGNVARCHAR:
				// got a JSON object - save it as a JSON String
				if (fieldObj.getClass().equals(java.util.LinkedHashMap.class)) {
					dr.addDataField(fieldName, fieldType, new DataField(root2.get(fieldName).toString()));
					dr.setFieldAttribute(fieldName, "StringFormat", "JSON");
				} else
					dr.addDataField(fieldName, fieldType, new DataField(fieldObj));
				break;
			case java.sql.Types.BIGINT:
			case java.sql.Types.TINYINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
				String tmp = fieldObj.toString();
				if (tmp.isEmpty())
					tmp = "0";
				dr.addDataField(fieldName, fieldType, new DataField(Integer.parseInt(tmp)));
				break;
			case java.sql.Types.NUMERIC:
				dr.addDataField(fieldName, fieldType, new DataField(new java.math.BigDecimal(fieldObj.toString())));
				break;
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.DECIMAL:
			case java.sql.Types.REAL:
				dr.addDataField(fieldName, fieldType, new DataField(Double.parseDouble(fieldObj.toString())));
				break;
			case java.sql.Types.BOOLEAN:
			case java.sql.Types.BIT:
				dr.addDataField(fieldName, fieldType, new DataField(fieldObj));
				break;
			case java.sql.Types.TIMESTAMP:
			case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
			case (int) 11:
				String tss = fieldObj.toString();
				if (!tss.contains("T")) {
					tss += "T00:00:00.0";
				}
				java.sql.Timestamp ts = (java.sql.Timestamp) DataField.convertType(tss, 11);
				dr.addDataField(fieldName, fieldType, new DataField(ts));
				break;
			case java.sql.Types.DATE:
			case (int) 9:
				tss = fieldObj.toString();
				dr.addDataField(fieldName, fieldType,
						new DataField((java.sql.Date) DataField.convertType(tss, fieldType)));
				break;
			case java.sql.Types.ARRAY:
			case java.sql.Types.BINARY:
			case java.sql.Types.BLOB:
			case java.sql.Types.CLOB:
			case java.sql.Types.DATALINK:
			case java.sql.Types.DISTINCT:
			case java.sql.Types.JAVA_OBJECT:
			case java.sql.Types.LONGVARBINARY:
			case java.sql.Types.NCLOB:
			case java.sql.Types.NULL:
			case java.sql.Types.OTHER:
			case java.sql.Types.REF:
			case java.sql.Types.REF_CURSOR:
			case java.sql.Types.ROWID:
			case java.sql.Types.SQLXML:
			case java.sql.Types.STRUCT:
			case java.sql.Types.TIME:
			case java.sql.Types.TIME_WITH_TIMEZONE:
			case java.sql.Types.VARBINARY:
			default:
				break;

			}// switch

			Map<String, String> attr = attributes.getFieldAttributes(fieldName);

			@SuppressWarnings("unchecked")
			HashMap<String, HashMap> m = (HashMap<String, HashMap>) hm.get("meta");
			if (m != null && m.containsKey(fieldName)) {
				attr.putAll((HashMap<String, String>) m.get(fieldName));

				//remove the ColumnType as extra Attribute
				//attr.remove("ColumnType");
				
				dr.setFieldAttributes(fieldName, attr);
			}
		}
	}

	private static void handleOldFormat(List<?> navigation, DataRow dr) throws ParseException {
		// old format - deprecated
		@SuppressWarnings("unchecked")
		Iterator<?> it = navigation.iterator();
		while (it.hasNext()) {

			HashMap<String, ?> field = (HashMap<String, ?>) it.next();
			String name = (String) field.get("Name");
			String type = (String) field.get("Type");
			if (type == null)
				continue;
			switch (type) {
			case "C":
				String strval = (String) field.get("StringValue");
				if (strval == null)
					strval = "";
				dr.setFieldValue(name, strval);
				break;
			case "N":
				Object o = field.get("NumericValue");
				Double numval;
				if (o == null)
					numval = 0.0;
				else
					numval = Double.parseDouble(o.toString());
				dr.setFieldValue(name, numval);
				break;
			case "D":
				Object d = field.get("DateValue");
				if (d == null) {
					dr.setFieldValue(name, Types.DATE, null);
				} else {
					dr.setFieldValue(name, Types.DATE, d.toString());
				}
				break;
			case "T":
				Object t = field.get("TimestampValue");
				if (t == null)
					dr.setFieldValue(name, Types.TIMESTAMP, null);
				else
					dr.setFieldValue(name, Types.TIMESTAMP, t.toString());
				break;
			default:
				break;
			}
		}
	}

	private static void createNonExistingAttributes(JsonNode root, DataRow attributes, HashMap<?, ?> hm) {
		// add all fields to the attributes record that were not part of it before
		JsonNode root2;
		if (root.isArray())
			root2=root.get(0);
		else
			root2=root;
		
		Iterator<?> it2 = hm.keySet().iterator();
		while (it2.hasNext()) {
			String fieldName = (String) it2.next();
			if (!attributes.contains(fieldName) && !fieldName.equals("meta") && root2.get(fieldName) != null) {
				String type=root2.get(fieldName).getNodeType().toString();
				switch (type) {
				case "NUMBER":
					attributes.addDataField(fieldName, java.sql.Types.DOUBLE, new DataField(null));
					break;
				case "BOOLEAN":
					attributes.addDataField(fieldName, java.sql.Types.BOOLEAN, new DataField(null));
					break;
				case "OBJECT":
					//a nested DataRow
					attributes.addDataField(fieldName, -974, new DataField(null));
					break;
				case "ARRAY":
					//a nested DataRow or ArrayList / BBjVector
					String subtype=root2.get(fieldName).get(0).getNodeType().toString();
					if (subtype == "OBJECT")
						attributes.addDataField(fieldName, -975, new DataField(null));
					else
						attributes.addDataField(fieldName, -973, new DataField(null));
					break;
				default:
					attributes.addDataField(fieldName, java.sql.Types.VARCHAR, new DataField(null));
					break;

				}
			}
		}
	}

	private static void createMetaData(DataRow attributes, HashMap<?, ?> hm) {
		if (hm.containsKey("meta")) {
			// new format
			HashMap<?, ?> meta = (HashMap<?, ?>) hm.get("meta");
			if (meta == null)
				meta = new HashMap();

			Iterator<?> it = hm.keySet().iterator();
			while (it.hasNext()) {
				String fieldName = (String) it.next();

				@SuppressWarnings("unchecked")
				HashMap<String, ?> fieldMeta = ((HashMap) meta.get(fieldName));
				if (!attributes.contains(fieldName) && !fieldName.equals("meta")) {
					String fieldType = "12";

					if (fieldMeta == null) {
						fieldMeta = new HashMap<>();
					}

					if (fieldMeta.get(COLUMN_TYPE) != null)
						fieldType = (String) fieldMeta.get(COLUMN_TYPE);
					if (fieldType != null) {
						attributes.addDataField(fieldName, Integer.parseInt(fieldType), new DataField(null));

						Set<String> ks = fieldMeta.keySet();
						if (ks.size() > 1) {
							Iterator<String> itm = ks.iterator();
							while (itm.hasNext()) {
								String k = itm.next();
								if (k.equals(COLUMN_TYPE))
									continue;
								attributes.setFieldAttribute((String) fieldName, k, (String) fieldMeta.get(k));
							}
						}
					} // if s!=null
				}
			}
		}
	}

	private static String wrapInJsonArray(String input) {
		if (input.startsWith("{") && input.endsWith("}")) {
			return "[" + input + "]";
		}
		return input;
	}

	private static JsonNode buildJsonRoot(String input) throws IOException {
		String intmp = input;
		JsonNode root = new ObjectMapper().readTree(intmp);
		return root;
	}

	private static String removeLeadingDataRow(String input) {
		if (input.startsWith("{\"datarow\":[") && input.endsWith("]}")) {
			input = input.substring(11, input.length() - 1);
		}
		return input;
	}

	// convert characters below chr(32) to \\uxxxx notation
	private static String convertCharsBelowChr32(String input) {
		int i = 0;

		while (i < input.length()) {
			if (input.charAt(i) < 31) {
				String hex = String.format("%04x", (int) input.charAt(i));
				input = input.substring(0, i) + "\\u" + hex + input.substring(i + 1);
			}
			i++;
		}
		return input;
	}
}
