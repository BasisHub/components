package com.basiscomponents.db.util;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataField;
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

    public static final String ATTRIBUTES = "_attributes";

    private ResultSetJsonMapper() {
    }


    public static String toJson(ResultSet rs, boolean meta,
                                String addIndexColumn, boolean f_trimStrings, boolean writeDataRowAttributes)
            throws IOException {

        JsonFactory jf = new JsonFactory();
        jf.setCharacterEscapes(new ComponentsCharacterEscapes());
        StringWriter writer = new StringWriter();
        try (JsonGenerator jsonGenerator = jf.createGenerator(writer)) {
            jsonGenerator.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
            // g.useDefaultPrettyPrinter();

            jsonGenerator.writeStartArray();

            boolean metaDone = !meta;

            for (DataRow dr : rs.getDataRows()) {
                jsonGenerator.writeStartObject();

                if (addIndexColumn != null) {
                    jsonGenerator.writeStringField(addIndexColumn, dr.getRowKey());
                }

                for (String fn : dr.getFieldNames()) {
                    dataFieldToJson(dr.getField(fn, true), fn, dr.getFieldType(fn), meta, addIndexColumn, f_trimStrings, jsonGenerator);

                } // while on fields
                if (writeDataRowAttributes){
                    writeDataRowAttributes(dr.getAttributes(), jsonGenerator);
                }
                if (meta) {
                    metaDone = writeMeta(rs, addIndexColumn, jsonGenerator, metaDone, dr);
                }

                jsonGenerator.writeEndObject();

            } // while on rows

            jsonGenerator.writeEndArray();
            jsonGenerator.close();
            return writer.toString();
        }
    }

    private static void writeDataRowAttributes(HashMap<String, String> attributes, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName(ATTRIBUTES);
        jsonGenerator.writeStartObject();
        for (Entry<String, String> entry : attributes.entrySet()) {
            System.out.println(entry);
            jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
        }
        jsonGenerator.writeEndObject();
    }

    private static void dataFieldToJson(DataField value, String fieldName, int fieldType, boolean meta, String addIndexColumn, boolean f_trimStrings, JsonGenerator jsonGenerator) throws IOException {
        if (value == null || value.getValue() == null) {
            jsonGenerator.writeNullField(fieldName);
            return;
        }

        switch (fieldType) {

            //an ArrayList
            case -973: {
                try {

                    jsonGenerator.writeArrayFieldStart(fieldName);
                    List l = (List) value.getObject();
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
            case -974: {
                DataRow drj = (DataRow) value.getObject();
                try {
                    jsonGenerator.writeFieldName(fieldName);
                    String jstr = drj.toJson(meta);
                    if (jstr.startsWith("[")) {
                        jstr = jstr.substring(1, jstr.length() - 1);
                    }
                    jsonGenerator.writeRawValue(jstr);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            break;
            //a nested ResultSet
            case -975: {
                ResultSet rs = (ResultSet) value.getObject();
                try {
                    jsonGenerator.writeFieldName(fieldName);
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
                String tmp = value.getAttribute("StringFormat");
                if (tmp != null && tmp.equalsIgnoreCase("JSON")) {
                    jsonGenerator.writeFieldName(fieldName);
                    String s = value.getString().trim();
                    if (s.isEmpty()) {
                        s = "{}";

                    }
                    jsonGenerator.writeRawValue(s);
                } else {
                    String s = value.getString();
                    if (f_trimStrings)
                        s = s.trim();
                    jsonGenerator.writeStringField(fieldName, s);
                }
                break;
            case java.sql.Types.BIGINT:
                if (value == null || value.getLong() == null)
                    jsonGenerator.writeNumberField(fieldName, 0);
                else
                    jsonGenerator.writeNumberField(fieldName, value.getLong().longValue());
                break;

            case java.sql.Types.TINYINT:
            case java.sql.Types.INTEGER:
            case java.sql.Types.SMALLINT:
                if (value == null || value.getInt() == null)
                    jsonGenerator.writeNumberField(fieldName, 0);
                else
                    jsonGenerator.writeNumberField(fieldName, value.getInt().intValue());
                break;

            case java.sql.Types.NUMERIC:
                jsonGenerator.writeNumberField(fieldName, value.getBigDecimal().stripTrailingZeros());
                break;

            case java.sql.Types.DECIMAL:
                if (value == null || value.getBigDecimal() == null)
                    jsonGenerator.writeNumberField(fieldName, 0);
                else
                    jsonGenerator.writeNumberField(fieldName, value.getBigDecimal());
                break;

            case java.sql.Types.DOUBLE:
            case java.sql.Types.FLOAT:
                if (value == null || value.getDouble() == null)
                    jsonGenerator.writeNumberField(fieldName, 0.0);
                else
                    jsonGenerator.writeNumberField(fieldName, value.getDouble().doubleValue());
                break;

            case java.sql.Types.REAL:
                if (value == null || value.getFloat() == null)
                    jsonGenerator.writeNumberField(fieldName, 0.0);
                else
                    jsonGenerator.writeNumberField(fieldName, value.getFloat().floatValue());
                break;

            case java.sql.Types.BOOLEAN:
            case java.sql.Types.BIT:
                if (value == null || value.getBoolean() == null)
                    jsonGenerator.writeStringField(fieldName, "");
                else
                    jsonGenerator.writeBooleanField(fieldName, value.getBoolean());

                break;

            case java.sql.Types.TIMESTAMP:
            case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
            case 11:
                if (value == null || value.getTimestamp() == null)
                    jsonGenerator.writeStringField(fieldName, "");
                else {

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

                    jsonGenerator.writeStringField(fieldName, str_ts);
                }
                break;
            case java.sql.Types.DATE:
            case 9:
                if (value == null || value.getDate() == null)
                    jsonGenerator.writeStringField(fieldName, "");
                else {
                    jsonGenerator.writeStringField(fieldName, value.getDate().toString() + "T00:00:00");
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
                System.err.println("unknown column type: " + fieldType);
                break;

        }// switch
    }

    private static boolean writeMeta(ResultSet resultSet, String indexColumn, JsonGenerator jsonGenerator, boolean metaDone, DataRow dr) throws IOException {
        if (!metaDone) {
            jsonGenerator.writeFieldName("meta");

            jsonGenerator.writeStartObject();

            if (indexColumn != null) {
                jsonGenerator.writeFieldName(indexColumn);
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("ColumnType", "12");
                jsonGenerator.writeEndObject();
            }

            for (HashMap<String, Object> hm : resultSet.getMetaData()) {

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

            metaDone = true;
        } else {
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
            if (mWritten)
                jsonGenerator.writeEndObject();
        }
        return metaDone;
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
            atr = null;
        }
        return Optional.of(atr);
    }


}
