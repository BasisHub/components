package com.basiscomponents.db.util;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.model.Attribute;
import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.Map.Entry;


public class ResultSetJsonMapper {
	private ResultSetJsonMapper() {
	}
	public static String toJson(List<DataRow> dataRows, List<HashMap<String, Object>> metaData, boolean meta,
			String addIndexColumn, boolean f_trimStrings)
			throws IOException {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter writer = new StringWriter();
		try (JsonGenerator jsonGenerator = jf.createGenerator(writer)) {
		jsonGenerator.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		// g.useDefaultPrettyPrinter();

		jsonGenerator.writeStartArray();

		boolean metaDone = !meta;
		
		for (DataRow dr : dataRows) {
			jsonGenerator.writeStartObject();

			if (addIndexColumn!= null) {
				jsonGenerator.writeStringField(addIndexColumn,dr.getRowKey());
			}
			
			for (String fn : dr.getFieldNames()) {
				if (dr.getField(fn,true) == null || dr.getField(fn,true).getValue() == null) {
					jsonGenerator.writeNullField(fn);
					continue;
				}
				int t = dr.getFieldType(fn);
				switch (t) {

				//an ArrayList
				case -973:{
					try {
						
						jsonGenerator.writeArrayFieldStart(fn);
						List l = (List) dr.getField(fn).getObject();
						Iterator it = l.iterator();
						while (it.hasNext())
							jsonGenerator.writeObject(it.next());
						jsonGenerator.writeEndArray();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				break;
				//a nested DataRow
				case -974:{
					DataRow drj = (DataRow) dr.getField(fn).getObject();
					try {
						jsonGenerator.writeFieldName(fn);
						String jstr = drj.toJson(meta);
						if (jstr.startsWith("[")) {
							jstr = jstr.substring(1,jstr.length()-1);
						}
						jsonGenerator.writeRawValue(jstr);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
				//a nested ResultSet
				case -975:{
					ResultSet rs = (ResultSet) dr.getField(fn).getObject();
					try {
						jsonGenerator.writeFieldName(fn);
						jsonGenerator.writeRawValue(rs.toJson(meta, addIndexColumn));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					String tmp = dr.getField(fn).getAttribute("StringFormat");
					if (tmp != null && tmp.equalsIgnoreCase("JSON")) {
						jsonGenerator.writeFieldName(fn);
						String s = dr.getField(fn).getString().trim();
						if (s.isEmpty()) {
							s = "{}";
						}
						jsonGenerator.writeRawValue(s);
					} else {
						String s = dr.getField(fn).getString();
						if (f_trimStrings)
							s=s.trim();
						jsonGenerator.writeStringField(fn, s);
					}
					break;
				case java.sql.Types.BIGINT:
					if (dr.getField(fn) == null || dr.getField(fn).getLong() == null)
						jsonGenerator.writeNumberField(fn, 0);
					else
						jsonGenerator.writeNumberField(fn, dr.getField(fn).getLong().longValue());
					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
					if (dr.getField(fn) == null || dr.getField(fn).getInt() == null)
						jsonGenerator.writeNumberField(fn, 0);
					else
						jsonGenerator.writeNumberField(fn, dr.getField(fn).getInt().intValue());
					break;

				case java.sql.Types.NUMERIC:
					jsonGenerator.writeNumberField(fn, dr.getField(fn).getBigDecimal().stripTrailingZeros());
					break;

				case java.sql.Types.DECIMAL:
					if (dr.getField(fn) == null || dr.getField(fn).getBigDecimal() == null)
						jsonGenerator.writeNumberField(fn, 0);
					else
						jsonGenerator.writeNumberField(fn, dr.getField(fn).getBigDecimal());
					break;

				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
					if (dr.getField(fn) == null || dr.getField(fn).getDouble() == null)
						jsonGenerator.writeNumberField(fn, 0.0);
					else
						jsonGenerator.writeNumberField(fn, dr.getField(fn).getDouble().doubleValue());
					break;

				case java.sql.Types.REAL:
					if (dr.getField(fn) == null || dr.getField(fn).getFloat() == null)
						jsonGenerator.writeNumberField(fn, 0.0);
					else
						jsonGenerator.writeNumberField(fn, dr.getField(fn).getFloat().floatValue());
					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					if (dr.getField(fn) == null || dr.getField(fn).getBoolean() == null)
						jsonGenerator.writeStringField(fn, "");
					else
						jsonGenerator.writeBooleanField(fn, dr.getField(fn).getBoolean());

					break;

				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
				case 11:
					if (dr.getField(fn) == null || dr.getField(fn).getTimestamp() == null)
						jsonGenerator.writeStringField(fn, "");
					else {
						
						java.sql.Timestamp ts = dr.getField(fn).getTimestamp();
						String str_ts = ts.toString().replaceFirst(" ", "T");
						
						// calculate the offset of the timezone
						// TODO: finish according to https://github.com/BBj-Plugins/BBjGridExWidget/issues/38
						// potentially externalize into a static utility method and add unit tests.
						
						TimeZone tz= java.util.TimeZone.getDefault();
						
						int offset = tz.getRawOffset();

						// add daylight saving time
						if (tz.useDaylightTime() && tz.inDaylightTime(new java.util.Date(ts.getTime()))) {
							offset = offset+tz.getDSTSavings();
						}
						
						int h = (offset/3600000);
						int m = Math.abs(offset) - Math.abs(h*3600000);
						
						
						StringBuilder sb = new StringBuilder();
						if (h>=0) 
							sb.append('+');
						else {
							sb.append('-');
							h*=-1;
						}
						
						if (h<10)
							sb.append('0');
						
						sb.append(h);
						sb.append(':');
						if (m==0)
							sb.append("00");
						else {
							if (m<10)
								sb.append('0');
							sb.append(m);
						}
						str_ts+=sb.toString();

						//-------------end timezone handling -------------------
						
						jsonGenerator.writeStringField(fn, str_ts);
					}
					break;
				case java.sql.Types.DATE:
				case 9:
					if (dr.getField(fn) == null || dr.getField(fn).getDate() == null)
						jsonGenerator.writeStringField(fn, "");
					else {
						jsonGenerator.writeStringField(fn, dr.getField(fn).getDate().toString()+"T00:00:00");
						// adding T00:00:00 for JavaScript to understand the correct order of day and month
						// see https://github.com/BBj-Plugins/BBjGridExWidget/issues/89
					}
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
					// this is a noop - TODO
					System.err.println("unknown column type: "+t);
					break;

				}// switch

			} // while on fields

			if (meta) {
				if (!metaDone) {
					jsonGenerator.writeFieldName("meta");

					jsonGenerator.writeStartObject();

					if (addIndexColumn!= null) {
						
						{
							jsonGenerator.writeFieldName(addIndexColumn);
							jsonGenerator.writeStartObject();
							jsonGenerator.writeStringField("ColumnType","12");
							jsonGenerator.writeEndObject();
						}
						
						
					}
					
					for (HashMap<String, Object> hm : metaData) {

						String c = (String) hm.get("ColumnName");
						if (c != null) {
							jsonGenerator.writeFieldName(c);
							jsonGenerator.writeStartObject();

							final Map<String, Attribute> atr = getFieldAttributes(dr, c);
							

							for (Entry<String, Object> entry : hm.entrySet()) {
								if ("ColumnTypeName".equals(entry.getKey()) || "ColumnName".equals(entry.getKey())
										|| (atr != null && atr.containsKey(entry.getKey()))) {
									continue;
								}
								String value = null;
								if(entry.getValue()!=null)
									value= entry.getValue().toString();
								
								jsonGenerator.writeStringField(entry.getKey(), value);
							}

							if (atr != null && !atr.isEmpty()) {
								for(Entry<String,Attribute> entry:atr.entrySet()) {
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

					metaDone = true;
				} else {
					BBArrayList<String> fields = dr.getFieldNames();
					boolean mWritten = false;
					for (String fieldname : fields) {
						if (dr.getField(fieldname,true) != null ) {
							Map<String, String> l = dr.getFieldAttributes(fieldname);
							if (!l.isEmpty()) {
								if (!mWritten) {
									jsonGenerator.writeFieldName("meta");
									jsonGenerator.writeStartObject();
									mWritten = true;
								}
								jsonGenerator.writeFieldName(fieldname);
								jsonGenerator.writeStartObject();
								Iterator<String> itks = l.keySet().iterator();
								while (itks.hasNext()) {
									String itk = itks.next();
									jsonGenerator.writeStringField(itk, l.get(itk));
								}
								jsonGenerator.writeEndObject();
							}
						}
					}
					if (mWritten)
						jsonGenerator.writeEndObject();
				}
			}

			jsonGenerator.writeEndObject();

		} // while on rows

		jsonGenerator.writeEndArray();
		jsonGenerator.close();
		return writer.toString();
		}
	}
	/**
	 * @param dr
	 * @param c
	 * @return
	 */
	private static Map<String, Attribute> getFieldAttributes(DataRow dr, String c) {
		Map<String, Attribute> atr;
		try {
			atr = dr.getFieldAttributes2(c);
		} catch (Exception e) {
			atr = null;
		}
		return atr;
	}


}
