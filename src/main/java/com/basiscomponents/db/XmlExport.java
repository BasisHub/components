package com.basiscomponents.db;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class XmlExport {

	/**
	 * Uses the given Writer object to write the ResultSet's content as XML to the
	 * output stream. The given root tag name will be used as the XML's root tag,
	 * and the give entity name will be used as surrounding tag for each of the
	 * ResultSet's DataRow objects. <br>
	 * <br>
	 * Usage:<br>
	 * 
	 * <pre>
	 * StringWriter w = new StringWriter();
	 * ResultSet rs = new ResultSet();
	 * 
	 * DataRow dr = new DataRow();
	 * dr.setFieldValue("feld1", "TEST1");
	 * dr.setFieldValue("feld2", "TEST2");
	 * rs.add(dr);
	 * 
	 * dr = new DataRow();
	 * dr.setFieldValue("feld1", "TEST1");
	 * dr.setFieldValue("feld3", "TEST3");
	 * rs.add(dr);
	 * 
	 * ResultSetExporter.writeXML(rs, "articles", "article", w);
	 * w.flush();
	 * w.close();
	 * 
	 * System.out.println(w.toString());
	 * 
	 * FileWriter fw = new FileWriter("D:/test.xml");
	 * ResultSetExporter.writeXML(rs, "articles", "article", fw);
	 * fw.flush();
	 * fw.close();
	 * </pre>
	 * 
	 * @param resultSet   The ResultSet object whose content will be written in XML
	 *                    format.
	 * @param rootTagName The name of the XML Root Tag to use.
	 * @param entityName  The name of the XML tag to use for each DataRow of the
	 *                    ResultSet.
	 * @param writer      The Writer object used to write the XML.
	 * 
	 * @throws Exception Gets thrown in case the XML could not be written.
	 */
	public static void writeXML(ResultSet resultSet, String rootTagName, String entityName, Writer writer)
			throws Exception {
		int indent = 0;

		writer.write("<" + rootTagName + ">\n");
		indent++;

		DataRow row;
		Iterator<String> rit;
		String fieldName, fieldValue;

		List<String> fieldnames = resultSet.getColumnNames();
		Iterator<DataRow> it = resultSet.iterator();
		while (it.hasNext()) {
			writer.write(getIndent(indent) + "<" + entityName + ">\n");
			indent++;

			row = it.next();
			rit = fieldnames.iterator();
			while (rit.hasNext()) {
				fieldName = rit.next();

				if (fieldName.contains("%")) {
					// TODO filter out all field names that are invalid tag names
					continue;
				}

				fieldValue = "";

				try {
					fieldValue = row.getFieldAsString(fieldName).trim();
					writer.write(getIndent(indent) + "<" + fieldName + ">");
					writer.write(escapeHTML(fieldValue));
					writer.write("</" + fieldName + ">\n");
				} catch (Exception e) {
					writer.write(getIndent(indent) + "<" + fieldName + " null=\"true\" />\n");
				}
			}
			indent--;

			writer.write(getIndent(indent) + "</" + entityName + ">\n");

		}
		indent--;

		writer.write("</" + rootTagName + ">\n");
	}

	private static String getIndent(int indent) {
		if (indent <= 0) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < indent; i++) {
			sb.append("    ");
		}

		return sb.toString();
	}

	/**
	 * Replaces all characters from the given String whose ascii decimal value is
	 * >127 by the ascii decimal value preceeded by "&#" and followed by ";"(Without
	 * the quotes).
	 * 
	 * Additionally replaces the following characters using the same mechanism:
	 * ",&,',&lt;,&gt;<br>
	 * <br>
	 * For example, the following String: <br>
	 * <br>
	 * &lt;p&gt;Let's write a "Hello World" program!&lt;/p&gt;<br>
	 * <br>
	 * would become:<br>
	 * <br>
	 * &amp;#60;p&amp;#62;Let&amp;#38;s write a &amp;#34;Hello World&amp;#34;
	 * program!&amp;#60;/p&amp;#62;<br>
	 * <br>
	 * 
	 * @param s The String whose special characters should be replaced
	 * 
	 * @return The String with the escaped characters
	 */
	public static String escapeHTML(String s) {
		StringBuilder out = new StringBuilder(Math.max(16, s.length()));
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c > 127 || c == 34 || c == 38 || c == 39 || c == 60 || c == 62) {
				out.append("&#");
				out.append((int) c);
				out.append(';');
			} else {
				out.append(c);
			}
		}
		return out.toString();
	}
	
}
