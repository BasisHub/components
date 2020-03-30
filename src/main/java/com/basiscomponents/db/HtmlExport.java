package com.basiscomponents.db;

import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HtmlExport {
	
	/**
	 * Uses the given Writer object to write the ResultSet's content as HTML
	 * &lt;table&gt; element to the output stream. <br>
	 * <br>
	 * Example Usage with an FileWriter object:<br>
	 * 
	 * <pre>
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
	 * HashMap<String, String> links = new HashMap<>();
	 * links.put("feld1", "/rest/test/{feld1}");
	 * links.put("feld2", "/rest/test/{feld1}/{feld2}");
	 * 
	 * FileWriter fw = new FileWriter("D:/test.html");
	 * ResultSetExporter.writeHTML(rs, fw, links);
	 * fw.flush();
	 * fw.close();
	 * </pre>
	 * 
	 * @param resultSet The ResultSet to export.
	 * @param writer    A java.io.Writer Instance.
	 * @param links     A HashMap with field name / URL pattern pairs, like
	 *                  http://xyz/a/{key}/gy.
	 * @throws Exception
	 */
	public static void writeHTML(ResultSet resultSet, Writer writer, HashMap<String, String> links) throws Exception {

		writer.write("<table>\n");

		if (resultSet.size() > 0) {

			List<String> fieldnames = resultSet.getColumnNames();

			Iterator<DataRow> it = resultSet.iterator();
			DataRow r = resultSet.get(0);

			writer.write("<thead>");

			Iterator<String> rit = fieldnames.iterator();
			while (rit.hasNext()) {
				String f = rit.next();
				writer.write("<th>");
				writer.write(f);
				writer.write("</th>");

			}

			writer.write("</thead>\n");

			while (it.hasNext()) {

				writer.write("<tr>");

				r = it.next();
				rit = fieldnames.iterator();
				while (rit.hasNext()) {
					String f = rit.next();
					String fv = "";
					try {

						String link = null;
						if (links != null)
							link = links.get(f);

						fv = r.getFieldAsString(f).trim();
						writer.write("<td>");
						if (link != null) {
							link = link.replace("{key}", fv);

							Iterator<String> inner_rit = fieldnames.iterator();
							while (inner_rit.hasNext()) {
								String inner_f = (String) inner_rit.next();
								String inner_fv = "";
								try {
									inner_fv = r.getFieldAsString(inner_f).trim();

								} catch (Exception e) {
								}
								link = link.replace("{" + inner_f + "}", inner_fv);
							}
							writer.write("<a href='" + link + "'>");

						}
						writer.write(fv);
						if (link != null)
							writer.write("</a>");
						writer.write("</td>");
					} catch (Exception e) {
						writer.write("<td class='missing'>");
						writer.write("");
						writer.write("</td>");
					}

				}

				writer.write("</tr>\n");

			}
		}
		writer.write("</table>\n");

	}

}
