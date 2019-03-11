package com.basiscomponents.db.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.model.Attribute;
import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ResultSetJsonMapper {
	private ResultSetJsonMapper() {
	}
	public static String toJson(List<DataRow> dataRows, List<HashMap<String, Object>> metaData, boolean meta,
			String addIndexColumn, boolean f_trimStrings)
			throws IOException {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter w = new StringWriter();
		try (JsonGenerator g = jf.createGenerator(w)) {
		g.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		// g.useDefaultPrettyPrinter();

		g.writeStartArray();

		boolean metaDone = !meta;
		
		for (DataRow dr : dataRows) {
			g.writeStartObject();

			if (addIndexColumn!= null) {
				
				g.writeStringField(addIndexColumn,dr.getRowKey());
			}
			
			for (String fn : dr.getFieldNames()) {

				if (dr.getField(fn).getValue() == null) {
					g.writeNullField(fn);
					continue;
				}
				int t = dr.getFieldType(fn);
				switch (t) {

				//a nested ResultSet
				case -974:{
					DataRow drj = (DataRow) dr.getField(fn).getObject();
					try {
						g.writeFieldName(fn);
						String jstr = drj.toJson(meta);
						if (jstr.startsWith("[")) {
							jstr = jstr.substring(1,jstr.length()-1);
						}
						g.writeRawValue(jstr);
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
						g.writeFieldName(fn);
						g.writeRawValue(rs.toJson(meta, addIndexColumn));
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
						g.writeFieldName(fn);
						String s = dr.getField(fn).getString().trim();
						if (s.isEmpty()) {
							s = "{}";
						}
						g.writeRawValue(s);
					} else {
						String s = dr.getField(fn).getString();
						if (f_trimStrings)
							s=s.trim();
						g.writeStringField(fn, s);
					}
					break;
				case java.sql.Types.BIGINT:
					if (dr.getField(fn) == null || dr.getField(fn).getLong() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getLong().longValue());
					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
					if (dr.getField(fn) == null || dr.getField(fn).getInt() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getInt().intValue());
					break;

				case java.sql.Types.NUMERIC:
					g.writeNumberField(fn, dr.getField(fn).getBigDecimal().stripTrailingZeros());
					break;

				case java.sql.Types.DECIMAL:
					if (dr.getField(fn) == null || dr.getField(fn).getBigDecimal() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getBigDecimal());
					break;

				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
					if (dr.getField(fn) == null || dr.getField(fn).getDouble() == null)
						g.writeNumberField(fn, 0.0);
					else
						g.writeNumberField(fn, dr.getField(fn).getDouble().doubleValue());
					break;

				case java.sql.Types.REAL:
					if (dr.getField(fn) == null || dr.getField(fn).getFloat() == null)
						g.writeNumberField(fn, 0.0);
					else
						g.writeNumberField(fn, dr.getField(fn).getFloat().floatValue());
					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					if (dr.getField(fn) == null || dr.getField(fn).getBoolean() == null)
						g.writeStringField(fn, "");
					else
						g.writeBooleanField(fn, dr.getField(fn).getBoolean());

					break;

				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
				case 11:
					if (dr.getField(fn) == null || dr.getField(fn).getTimestamp() == null)
						g.writeStringField(fn, "");
					else {
						
						String str_ts = dr.getField(fn).getTimestamp().toString().replaceFirst(" ", "T");
						
						// calculate the offset of the timezone
						// TODO: finish according to https://github.com/BBj-Plugins/BBjGridExWidget/issues/38
						// potentially externalize into a static utility method and add unit tests.
						
						TimeZone tz= java.util.TimeZone.getDefault();
						
						int offset = tz.getRawOffset();

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
						
						g.writeStringField(fn, str_ts);
					}
					break;
				case java.sql.Types.DATE:
				case 9:
					if (dr.getField(fn) == null || dr.getField(fn).getDate() == null)
						g.writeStringField(fn, "");
					else {
						g.writeStringField(fn, dr.getField(fn).getDate().toString()+"T00:00:00");
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
					g.writeFieldName("meta");

					g.writeStartObject();

					if (addIndexColumn!= null) {
						
						{
							g.writeFieldName(addIndexColumn);
							g.writeStartObject();
							g.writeStringField("ColumnType","12");
							g.writeEndObject();
						}
						
						
					}
					
					for (HashMap<String, Object> hm : metaData) {

						String c = (String) hm.get("ColumnName");
						if (c != null) {
							g.writeFieldName(c);
							g.writeStartObject();

							final Map<String, Attribute> atr = getFieldAttributes(dr, c);
							

							for (Entry<String, Object> entry : hm.entrySet()) {
								if ("ColumnTypeName".equals(entry.getKey()) || "ColumnName".equals(entry.getKey())
										|| (atr != null && atr.containsKey(entry.getKey()))) {
									continue;
								}
								String value = null;
								if(entry.getValue()!=null)
									value= entry.getValue().toString();
								
								g.writeStringField(entry.getKey(), value);
							}

							if (atr != null && !atr.isEmpty()) {
								for(Entry<String,Attribute> entry:atr.entrySet()) {
									String k = entry.getKey();
									Attribute v = entry.getValue();
									if (v.getType() == String.class) {
										g.writeStringField(k, v.getValue());
									} else if (v.getType() == int.class) {
										g.writeNumber(v.getIntValue());
									} else if (v.getType() == double.class) {
										g.writeNumber(v.getDoubleValue());
									} else if (v.getType() == boolean.class) {
										g.writeBoolean(v.getBooleanValue());
									}
								}
							}
							g.writeEndObject();
						}
					}
					g.writeEndObject();

					metaDone = true;
				} else {
					BBArrayList<String> fields = dr.getFieldNames();
					boolean mWritten = false;
					for (String fieldname : fields) {
						Map<String, String> l = dr.getFieldAttributes(fieldname);
						if (!l.isEmpty()) {
							if (!mWritten) {
								g.writeFieldName("meta");
								g.writeStartObject();
								mWritten = true;
							}
							g.writeFieldName(fieldname);
							g.writeStartObject();
							Iterator<String> itks = l.keySet().iterator();
							while (itks.hasNext()) {
								String itk = itks.next();
								g.writeStringField(itk, l.get(itk));
							}
							g.writeEndObject();
						}
					}
					if (mWritten)
						g.writeEndObject();
				}
			}

			g.writeEndObject();

		} // while on rows

		g.writeEndArray();
		g.close();
		return w.toString();
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
