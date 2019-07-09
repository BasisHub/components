package com.basiscomponents.db.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

public class DataFieldJsonMapper {

	private DataFieldJsonMapper() {
	}

	public static String dataFieldtoJson(DataField value, String fieldName, int fieldType) {
		
		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter writer = new StringWriter();

		try (JsonGenerator jsonGenerator = jf.createGenerator(writer)) {
			jsonGenerator.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
			
			jsonGenerator.writeStartObject();

			// The DataField is written
			dataFieldToJson(value, fieldName, fieldType, null, false, jsonGenerator);
			
			jsonGenerator.writeEndObject();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return writer.toString();
	}

	public static void dataFieldToJson(DataField value, String fieldName, int fieldType, String indexColumn,
			boolean f_trimStrings, JsonGenerator jg) throws IOException {

			if (value == null || value.getValue() == null) {
			jg.writeNullField(fieldName);
			return;
			}

			switch (fieldType) {

				//an ArrayList
				case -973:
					try {
						jg.writeArrayFieldStart(fieldName);
						List l = (List) value.getObject();
						Iterator it = l.iterator();
						while (it.hasNext()) {
							jg.writeObject(it.next());
						}
						jg.writeEndArray();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
					
				//a nested DataRow
				case -974:
					com.basiscomponents.db.DataRow drj = (com.basiscomponents.db.DataRow) value.getObject();
					try {
						jg.writeFieldName(fieldName);
						ResultSet rs = new ResultSet();
						rs.add(drj);
						drj.setIsFirstRow(true);
						String jstr = drj.toJson(rs, true, null, f_trimStrings, false);
						jg.writeRawValue(jstr);
					} catch (Exception e) {
				e.printStackTrace();
					}
					break;
					
				//a nested ResultSet
				case -975:
					ResultSet rs = (ResultSet) value.getObject();
					try {
						jg.writeFieldName(fieldName);
						jg.writeRawValue(rs.toJson(true, indexColumn));
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;

				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					String tmp = value.getAttribute("StringFormat");
					if (tmp != null && tmp.equalsIgnoreCase("JSON")) {
						jg.writeFieldName(fieldName);
						String s = value.getString().trim();
						if (s.isEmpty()) {
							s = "{}";
						}
						jg.writeRawValue(s);
					} else {
						String s = value.getString();
						if (f_trimStrings)
							s = s.trim();
						jg.writeStringField(fieldName, s);
					}
					break;
				case java.sql.Types.BIGINT:
					jg.writeNumberField(fieldName, value.getLong().longValue());
					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
					jg.writeNumberField(fieldName, value.getInt().intValue());
					break;

				case java.sql.Types.NUMERIC:
					jg.writeNumberField(fieldName, value.getBigDecimal().stripTrailingZeros());
					break;

				case java.sql.Types.DECIMAL:
					jg.writeNumberField(fieldName, value.getBigDecimal());
					break;

				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
					jg.writeNumberField(fieldName, value.getDouble().doubleValue());
					break;

				case java.sql.Types.REAL:
					jg.writeNumberField(fieldName, value.getFloat().floatValue());
					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					jg.writeBooleanField(fieldName, value.getBoolean());
					break;

				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
				case 11:

					java.sql.Timestamp ts = value.getTimestamp();
					String str_ts = ts.toString().replaceFirst(" ", "T");

					// calculate the offset of the timezone
					// TODO: finish according to https://github.com/BBj-Plugins/BBjGridExWidget/issues/38
					// potentially externalize into a static utility method and add unit tests.

					TimeZone tz = TimeZone.getDefault();

					int offset = tz.getRawOffset();

					// add daylight saving time
					if (tz.useDaylightTime() && tz.inDaylightTime(new Date(ts.getTime()))) {
						offset = offset + tz.getDSTSavings();
					}

					int h = (offset / 3600000);
					int m = Math.abs(offset) - Math.abs(h * 3600000);


					StringBuilder sb = new StringBuilder();
					if (h >= 0)
						sb.append('+');
					else {
						sb.append('-');
						h *= -1;
					}

					if (h < 10)
						sb.append('0');

					sb.append(h);
					sb.append(':');
					if (m == 0)
						sb.append("00");
					else {
						if (m < 10)
							sb.append('0');
						sb.append(m);
					}
					str_ts += sb.toString();

					//-------------end timezone handling -------------------

					jg.writeStringField(fieldName, str_ts);
					break;

				case java.sql.Types.DATE:
				case 9:
					jg.writeStringField(fieldName, value.getDate().toString() + "T00:00:00");
						// adding T00:00:00 for JavaScript to understand the correct order of day and month
						// see https://github.com/BBj-Plugins/BBjGridExWidget/issues/89
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
					System.err.println("unknown column type: " + fieldType);
					break;

			}// switch
	}
}