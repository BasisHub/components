package com.basiscomponents.db;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.basiscomponents.db.exportconfig.SheetConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Provides static methods to export a {@link com.basiscomponents.db.ResultSet
 * com.basiscomponents.db.ResultSet} in the following formats:
 * <ul>
 * <li>HTML(&lt;table&gt; tag)</li>
 * <li>JSON</li>
 * <li>TXT</li>
 * <li>XLSX</li>
 * <li>XML</li>
 * </ul>
 */
public class ResultSetExporter {

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
	 * @param resultSet
	 *            The ResultSet object whose content will be written in XML format.
	 * @param rootTagName
	 *            The name of the XML Root Tag to use.
	 * @param entityName
	 *            The name of the XML tag to use for each DataRow of the ResultSet.
	 * @param writer
	 *            The Writer object used to write the XML.
	 * 
	 * @throws Exception
	 *             Gets thrown in case the XML could not be written.
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
	 * @param s
	 *            The String whose special characters should be replaced
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
	 * @param resultSet
	 *            The ResultSet object whose content will be written as HTML
	 *            &lt;table&gt;
	 * @param writer
	 *            The Writer object used to write the HTML
	 * 
	 * @throws Exception
	 *             Gets thrown in case the HTML could not be written
	 */
	public static void writeHTML(ResultSet resultSet, Writer writer) throws Exception {
		ResultSetExporter.writeHTML(resultSet, writer, null);
	}

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
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param writer
	 *            A java.io.Writer Instance.
	 * @param links
	 *            A HashMap with field name / URL pattern pairs, like
	 *            http://xyz/a/{key}/gy.
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

	/**
	 * Uses the given Writer object to write the ResultSet's content as plain text
	 * to the output stream. <br>
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
	 * FileWriter fw = new FileWriter("D:/test.txt");
	 * ResultSetExporter.writeTXT(rs, fw);
	 * fw.flush();
	 * fw.close();
	 * </pre>
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param writer
	 *            A java.io.Writer Instance.
	 * @throws Exception
	 */
	public static void writeTXT(ResultSet resultSet, Writer writer) throws Exception {

		// TODO maybe do a variation where this can be set:
		String delim = "\t";
		Boolean writeHeader = true;

		if (resultSet.size() > 0) {

			List<String> fieldnames = resultSet.getColumnNames();
			Iterator<DataRow> it = resultSet.iterator();

			DataRow r = resultSet.get(0);

			Iterator<String> rit = fieldnames.iterator();
			if (writeHeader) {

				while (rit.hasNext()) {
					String f = rit.next();
					writer.write(f);
					writer.write(delim);

				}
				writer.write("\n");
			}

			while (it.hasNext()) {

				r = it.next();
				rit = fieldnames.iterator();
				while (rit.hasNext()) {
					String f = rit.next();
					String fv = "";
					try {
						fv = r.getFieldAsString(f).trim();
					} catch (Exception e) {
					}
					writer.write(fv);
					writer.write(delim);

				}

				writer.write("\n");

			}
		}

	}

	/**
	 * Converts the given ResultSet object to a JSON String and writes it to the
	 * output stream using the given writer object.
	 * 
	 * <br>
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
	 * FileWriter fw = new FileWriter("D:/test.json");
	 * ResultSetExporter.writeJSON(rs, fw);
	 * fw.flush();
	 * fw.close();
	 * </pre>
	 * 
	 * @param resultSet
	 *            The ResultSet object to write as JSON.
	 * @param writer
	 *            The Writer used to write the JSON String.
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be converted to a
	 *             JSON String.
	 */
	public static void writeJSON(ResultSet resultSet, Writer writer) throws Exception {
		writer.write(resultSet.toJson());
	}

	/**
	 * 
	 * @param resultSet
	 *            The ResultSet object to write as JSON.
	 * @param writer
	 *            The Writer used to write the JSON String.
	 * @param fMeta
	 *            A Boolean indicating whether to write out the meta data object
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be converted to a
	 *             JSON String.
	 */
	public static void writeJSON(ResultSet resultSet, Writer writer, Boolean fMeta) throws Exception {
		writer.write(resultSet.toJson(fMeta));
	}

	/**
	 * Writes the content of the given ResultSet as XLSX into the specified File.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param outputFile
	 *            The File in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be converted to a
	 *             XLSX File
	 */
	public static void writeXLSX(ResultSet rs, File outputFile, boolean writeHeader) throws Exception {
		DataRow ar;
		if (rs.size() > 0)
			ar = rs.get(0);
		else
			ar = new DataRow();
		writeXLSX(rs, outputFile, writeHeader, false, "Sheet", ar);
	}
	
	/**
	 * Writes the content of the given ResultSet as XLSX into the specified File.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param outputFile
	 *            The File in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * @param sheetConfig
	 *            Object containing customized configurations
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be converted to a
	 *             XLSX File
	 */
	public static void writeXLSX(ResultSet rs, File outputFile, boolean writeHeader, SheetConfiguration sheetConfig) throws Exception {
		DataRow ar;
		if (rs.size() > 0)
			ar = rs.get(0);
		else
			ar = new DataRow();
		if (outputFile != null) {
			try (FileOutputStream os = new FileOutputStream(outputFile)) {

				writeXLSX(rs, new FileOutputStream(outputFile), writeHeader, false, "Sheet", ar, sheetConfig);

				os.flush();
			}
		}
	}

	/**
	 * Writes the content of the given ResultSet as XLSX into the specified File.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param outputFile
	 *            The File in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * @param useLabelIfPresent
	 *            if the attributes record contains labels, use the label instead of
	 *            the field name.
	 * @param sheetName
	 *            A custom name for the sheet
	 * @param DataRow
	 *            attributesRecord the attributes record for formatting
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be converted to a
	 *             XLSX File
	 */
	public static void writeXLSX(ResultSet rs, File outputFile, boolean writeHeader, boolean useLabelIfPresent,
			String sheetName, DataRow AttributesRecord) throws Exception {
		if (outputFile != null) {
			try (FileOutputStream os = new FileOutputStream(outputFile)) {

				writeXLSX(rs, new FileOutputStream(outputFile), writeHeader, useLabelIfPresent, sheetName,
						AttributesRecord);

				os.flush();
			}
		}
	}

	/**
	 * Writes the content of the given ResultSet into an output stream.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param out
	 *            The output stream in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be read or output
	 *             stream can not be written
	 */
	public static void writeXLSX(ResultSet rs, OutputStream out, boolean writeHeader) throws Exception {
		DataRow ar;
		if (rs.size() > 0)
			ar = rs.get(0);
		else
			ar = new DataRow();
		writeXLSX(rs, out, writeHeader, false, "Sheet", ar);
	}

	/**
	 * Writes the content of the given ResultSet into an output stream.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param out
	 *            The output stream in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * @param useLabelIfPresent
	 *            if the attributes record contains labels, use the label instead of
	 *            the field name.
	 * @param sheetName
	 *            A custom name for the sheet
	 * @param attributesRecord
	 *            attributesRecord the attributes record for formatting
	 * @param sheetConfig
	 * 			  Object containing customized configurations
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be read or output
	 *             stream can not be written
	 */
	public static void writeXLSX(ResultSet rs, OutputStream out, boolean writeHeader, boolean useLabelIfPresent,
			String sheetName, DataRow attributesRecord) throws Exception {
		
		writeXLSX(rs, out, writeHeader, useLabelIfPresent, sheetName, attributesRecord, null);		
	}

	/**
	 * Writes the content of the given ResultSet into an output stream.
	 * 
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param out
	 *            The output stream in which to write the ResultSet's content.
	 * @param writeHeader
	 *            The boolean value indicating whether writing the column headers or
	 *            not.
	 * @param useLabelIfPresent
	 *            if the attributes record contains labels, use the label instead of
	 *            the field name.
	 * @param sheetName
	 *            A custom name for the sheet
	 * @param DataRow
	 *            attributesRecord the attributes record for formatting
	 * 
	 * @throws Exception
	 *             Gets thrown in case the ResultSet could not be read or output
	 *             stream can not be written
	 */
	@SuppressWarnings("deprecation")
	public static void writeXLSX(ResultSet rs, OutputStream out, boolean writeHeader, boolean useLabelIfPresent,
			String sheetName, DataRow AttributesRecord, SheetConfiguration sheetConfig) throws Exception {

		try (Workbook wb = new SXSSFWorkbook(400)) {
			Sheet sheet = wb.createSheet(sheetName);

			Row row;
			Cell cell;

			if (rs == null || rs.isEmpty()) {
				row = sheet.createRow(0);
				cell = row.createCell(0);

				if (rs == null) {
					cell.setCellValue("null ResultSet");
				} else {
					cell.setCellValue("Empty ResultSet");
				}

				wb.write(out);

				return;
			}

			List<String> fieldnames;
			if (sheetConfig == null) {
				fieldnames = rs.getColumnNames();
			}
			else {
				sheet = sheetConfig.getConfiguredSheet(sheet);
				fieldnames = sheetConfig.getColumnNamesOrdered();
			}
			Iterator<DataRow> it = rs.iterator();
			Iterator<String> fieldNameIterator;

			int rowIndex = 0;
			int cellIndex = 0;

			if (writeHeader) {
				fieldNameIterator = fieldnames.iterator();
				row = sheet.createRow(rowIndex);
				rowIndex++;

				while (fieldNameIterator.hasNext()) {
					cell = row.createCell(cellIndex);
					String fieldname = fieldNameIterator.next();
					String label = fieldname;
					if (useLabelIfPresent) {
						try {
							label = AttributesRecord.getFieldAttribute(fieldname, "LABEL");
						} finally {
						}
					}
					cell.setCellValue(label);
					cellIndex++;
				}
			}

			ArrayList<Integer> numericTypes = new ArrayList<Integer>();
			numericTypes.add(java.sql.Types.BIGINT);
			numericTypes.add(java.sql.Types.DOUBLE);
			numericTypes.add(java.sql.Types.NUMERIC);
			numericTypes.add(java.sql.Types.INTEGER);
			numericTypes.add(java.sql.Types.DECIMAL);
			numericTypes.add(java.sql.Types.FLOAT);
			numericTypes.add(java.sql.Types.REAL);
			numericTypes.add(java.sql.Types.TINYINT);
			numericTypes.add(java.sql.Types.SMALLINT);

			DataRow currentRow;
			String currentFieldName;
			while (it.hasNext()) {
				fieldNameIterator = fieldnames.iterator();

				row = sheet.createRow(rowIndex);
				rowIndex++;

				cellIndex = 0;
				currentRow = it.next();
				int columnType;
				while (fieldNameIterator.hasNext()) {
					currentFieldName = fieldNameIterator.next();
					cell = row.createCell(cellIndex);
					columnType = rs.getColumnType(rs.getColumnIndex(currentFieldName));
					if (currentRow.contains(currentFieldName)) {
						if (numericTypes.contains(columnType)) {
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(currentRow.getFieldAsNumber(currentFieldName));
						} else if (columnType == java.sql.Types.BOOLEAN) {
							cell.setCellType(CellType.BOOLEAN);
							cell.setCellValue(currentRow.getField(currentFieldName).getBoolean());
						} else if (columnType == java.sql.Types.BINARY || columnType == java.sql.Types.LONGVARBINARY
								|| columnType == java.sql.Types.VARBINARY) {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(new String(currentRow.getField(currentFieldName).getBytes()));
						} else {
							cell.setCellType(CellType.STRING);
							cell.setCellValue(currentRow.getFieldAsString(currentFieldName));
						}
					}
					cellIndex++;
				}
			}

			wb.write(out);

		}
	}

}
