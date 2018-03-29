package com.basiscomponents.db.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class ResultSetJsonMapper {
	public static String toJson(List<DataRow> dataRows, List<HashMap<String, Object>> metaData, boolean meta)
			throws IOException {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter w = new StringWriter();
		JsonGenerator g = jf.createGenerator(w);
		g.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		// g.useDefaultPrettyPrinter();

		g.writeStartArray();

		Boolean meta_done = !meta;

		Iterator<DataRow> it = dataRows.iterator();
		while (it.hasNext()) {
			DataRow dr = it.next();
			BBArrayList<String> f = dr.getFieldNames();
			Iterator<String> itf = f.iterator();

			g.writeStartObject();

			while (itf.hasNext()) {
				String fn = itf.next();
				if (dr.getField(fn).getValue() == null) {
					g.writeNullField(fn);
					continue;
				}
				int t = dr.getFieldType(fn);
				switch (t) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					String tmp = dr.getField(fn).getAttribute("StringFormat");
					if (tmp != null && tmp.toUpperCase().equals("JSON")) {
						g.writeFieldName(fn);
						String s = dr.getField(fn).getString().trim();
						if (s.isEmpty()) {
							s = "{}";
						}
						g.writeRawValue(s);
					} else {
						String s = dr.getField(fn).getString().trim();
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
						g.writeStringField(fn, dr.getField(fn).getTimestamp().toString().replaceFirst(" ", "T"));
					}
					break;
				case java.sql.Types.DATE:
				case 9:
					if (dr.getField(fn) == null || dr.getField(fn).getDate() == null)
						g.writeStringField(fn, "");
					else {
						g.writeStringField(fn, dr.getField(fn).getDate().toString());
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
					break;

				}// switch

			} // while on fields

			if (meta) {
				if (!meta_done) {
					g.writeFieldName("meta");

					g.writeStartObject();

					Iterator<HashMap<String, Object>> i = metaData.iterator();
					while (i.hasNext()) {
						HashMap<String, Object> hm = i.next();
						String c = (String) hm.get("ColumnName");
						if (c != null) {
							g.writeFieldName(c);
							g.writeStartObject();

							HashMap<String, String> atr;
							try {
								atr = dr.getFieldAttributes(c);
							} catch (Exception e) {
								atr = null;
							}

							Set<String> ks = hm.keySet();
							Iterator<String> its = ks.iterator();
							while (its.hasNext()) {
								String key = its.next();
								if (key.equals("ColumnTypeName") || key.equals("ColumnName"))
									continue;

								if (atr != null && atr.containsKey(key))
									continue;

								String value = null;
								if (hm.get(key) != null)
									value = hm.get(key).toString();
								g.writeStringField(key, value);
							}

							if (atr != null && !atr.isEmpty()) {
								Iterator<String> itks = atr.keySet().iterator();
								while (itks.hasNext()) {
									String itk = itks.next();
									g.writeStringField(itk, atr.get(itk));
								}
							}

							g.writeEndObject();
						}
					}

					g.writeEndObject();

					meta_done = true;
				} else {
					BBArrayList<String> fields = dr.getFieldNames();
					itf = fields.iterator();
					boolean m_written = false;
					while (itf.hasNext()) {
						String fieldname = itf.next();
						HashMap<String, String> l = dr.getFieldAttributes(fieldname);
						if (!l.isEmpty()) {
							if (!m_written) {
								g.writeFieldName("meta");
								g.writeStartObject();
								m_written = true;
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
					if (m_written)
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
