package com.basiscomponents.db;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.basiscomponents.db.config.export.ColumnConfiguration;
import com.basiscomponents.db.config.export.ReportDetails;
import com.basiscomponents.db.config.export.SheetConfiguration;
import com.basiscomponents.db.util.SqlTypeNames;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

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
	
	private static final int PDF_WIDTH_LANDSCAPE = 802;
	private static final int PDF_WIDTH_PORTRAIT = 555;
	private static final int PDF_OFFSET = 5;
	private static final String DOCUMENTS_PATH = System.getProperty("user.home") + "/documents/";
	private static final String PDF_FIELD_VALUE_ALIGNTMENT_LEFT = "Left";
	private static final String PDF_FIELD_VALUE_ALIGNTMENT_RIGHT = "Right";
	private static final int PDF_EXPORT_NO_FIT = 0;
	private static final int PDF_EXPORT_FIT_TO_WIDTH = 1;
	private static final int PDF_EXPORT_FIT_TO_HEIGHT = 2;
	private static final int PDF_EXPORT_FIT_TO_PAGE = 3;
	
	
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
	 * @param resultSet The ResultSet object whose content will be written as HTML
	 *                  &lt;table&gt;
	 * @param writer    The Writer object used to write the HTML
	 * 
	 * @throws Exception Gets thrown in case the HTML could not be written
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
	 * @param resultSet The ResultSet to export.
	 * @param writer    A java.io.Writer Instance.
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
	 * @param resultSet The ResultSet object to write as JSON.
	 * @param writer    The Writer used to write the JSON String.
	 * @throws Exception Gets thrown in case the ResultSet could not be converted to
	 *                   a JSON String.
	 */
	public static void writeJSON(ResultSet resultSet, Writer writer) throws Exception {
		writer.write(resultSet.toJson());
	}

	/**
	 * 
	 * @param resultSet The ResultSet object to write as JSON.
	 * @param writer    The Writer used to write the JSON String.
	 * @param fMeta     A Boolean indicating whether to write out the meta data
	 *                  object
	 * @throws Exception Gets thrown in case the ResultSet could not be converted to
	 *                   a JSON String.
	 */
	public static void writeJSON(ResultSet resultSet, Writer writer, Boolean fMeta) throws Exception {
		writer.write(resultSet.toJson(fMeta));
	}

	/**
	 * Writes the content of the given ResultSet as XLSX into the specified File.
	 * 
	 * @param resultSet   The ResultSet to export.
	 * @param outputFile  The File in which to write the ResultSet's content.
	 * @param writeHeader The boolean value indicating whether writing the column
	 *                    headers or not.
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be converted to
	 *                   a XLSX File
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
	 * @param resultSet   The ResultSet to export.
	 * @param outputFile  The File in which to write the ResultSet's content.
	 * @param writeHeader The boolean value indicating whether writing the column
	 *                    headers or not.
	 * @param sheetConfig Object containing customized configurations
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be converted to
	 *                   a XLSX File
	 */
	public static void writeXLSX(ResultSet rs, File outputFile, boolean writeHeader, SheetConfiguration sheetConfig)
			throws Exception {
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
	 * @param resultSet         The ResultSet to export.
	 * @param outputFile        The File in which to write the ResultSet's content.
	 * @param writeHeader       The boolean value indicating whether writing the
	 *                          column headers or not.
	 * @param useLabelIfPresent if the attributes record contains labels, use the
	 *                          label instead of the field name.
	 * @param sheetName         A custom name for the sheet
	 * @param DataRow           attributesRecord the attributes record for
	 *                          formatting
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be converted to
	 *                   a XLSX File
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
	 * @param resultSet   The ResultSet to export.
	 * @param out         The output stream in which to write the ResultSet's
	 *                    content.
	 * @param writeHeader The boolean value indicating whether writing the column
	 *                    headers or not.
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be read or
	 *                   output stream can not be written
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
	 * @param resultSet         The ResultSet to export.
	 * @param out               The output stream in which to write the ResultSet's
	 *                          content.
	 * @param writeHeader       The boolean value indicating whether writing the
	 *                          column headers or not.
	 * @param useLabelIfPresent if the attributes record contains labels, use the
	 *                          label instead of the field name.
	 * @param sheetName         A custom name for the sheet
	 * @param attributesRecord  attributesRecord the attributes record for
	 *                          formatting
	 * @param sheetConfig       Object containing customized configurations
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be read or
	 *                   output stream can not be written
	 */
	public static void writeXLSX(ResultSet rs, OutputStream out, boolean writeHeader, boolean useLabelIfPresent,
			String sheetName, DataRow attributesRecord) throws Exception {

		writeXLSX(rs, out, writeHeader, useLabelIfPresent, sheetName, attributesRecord, null);
	}

	/**
	 * Writes the content of the given ResultSet into an output stream.
	 * 
	 * @param resultSet         The ResultSet to export.
	 * @param out               The output stream in which to write the ResultSet's
	 *                          content.
	 * @param writeHeader       The boolean value indicating whether writing the
	 *                          column headers or not.
	 * @param useLabelIfPresent if the attributes record contains labels, use the
	 *                          label instead of the field name.
	 * @param sheetName         A custom name for the sheet
	 * @param DataRow           attributesRecord the attributes record for
	 *                          formatting
	 * 
	 * @throws Exception Gets thrown in case the ResultSet could not be read or
	 *                   output stream can not be written
	 */
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
			} else {
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
						if (SqlTypeNames.isNumericType(columnType)) {
							cell.setCellType(CellType.NUMERIC);
							cell.setCellValue(currentRow.getFieldAsNumber(currentFieldName));
						} else if (columnType == java.sql.Types.BOOLEAN || columnType == java.sql.Types.BIT) {
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

	/**
	 * Exports the content of the given ResultSet from a BBjGridExWidget
	 * 
	 * @param outputFileName    The name of the PDF file
	 * @param resultSet         The ResultSet to export.
	 * @param writeHeader       The sheet configuration for the report
	 * @param baristaMode		indicator if the Barista header should be displayed or not
	 * @param sheetName         content fitting indicator
	 * @param landscapeMode     indicator if the PDF should be created in landscape mode or else in portrait mode
	 * 
	 * @return The final exported PDF file
	 */
	public static File exportToPDF(String outputFileName, ResultSet rs, SheetConfiguration sheetConfig, boolean baristaMode, int fitTo, boolean landscapeMode) {
		int sheetWidth;
		if (landscapeMode) {
			sheetWidth = PDF_WIDTH_LANDSCAPE;
		}else {
			sheetWidth = PDF_WIDTH_PORTRAIT;
		}
		
		ArrayList<SheetConfiguration> sheetConfigList;
		switch(fitTo) {
		// splitting the original sheetConfiguration in multiple ones since there are possibly too many columns to fit on just one page.
		case PDF_EXPORT_NO_FIT:
			sheetConfigList = fitNormal(sheetConfig, sheetWidth);
			break;
		// editing the columnConfiguration; fitting all columns on one page.
		case PDF_EXPORT_FIT_TO_WIDTH:
		case PDF_EXPORT_FIT_TO_PAGE:
			sheetConfigList = fitToWidth(sheetConfig, sheetWidth);
			break;
		default:
			sheetConfigList = fitNormal(sheetConfig, sheetWidth);
			break;
		}
		
		// getting the column field types
		DataRow fieldTypes = getFieldTypes(sheetConfig, rs);
		
		// creating a list of temporary jrxml files
		ArrayList<File> tempJRXMLList = new ArrayList<File>();
		File tempJRXML;
		if(sheetConfigList.size() <= 0) {
			// creating a jrxml file (a report; placing columns and so on)
			tempJRXML = ResultSetExporter.writeTempJRXML(sheetConfig, fieldTypes, rs, landscapeMode, 0, fitTo, baristaMode);
			tempJRXMLList.add(tempJRXML);
		}else {
			int i = 0;
			while(i < sheetConfigList.size()) {
				tempJRXML = ResultSetExporter.writeTempJRXML(sheetConfigList.get(i), fieldTypes, rs, landscapeMode, i, fitTo, baristaMode);
				tempJRXMLList.add(tempJRXML);
				i++;
			}
		}
		
		// creating the PDF files out of the tempJRXML files
		ArrayList<File> tempFileList = writePDFs(tempJRXMLList, outputFileName, rs);
		
		// merging the created pdf files to one file
		return mergePDFFiles(tempFileList, outputFileName);
	}
	
	/**
	 * Compiles a list of jasper reports, fills them with the result set data and exports them as PDF files.
	 * 
	 * @param tempJRXMLList     List of temporary jrxml files
	 * @param outputFileName    The name of the PDF file
	 * @param resultSet         The ResultSet to export
	 * 
	 * @return List of exported PDF files
	 */
	private static ArrayList<File> writePDFs(ArrayList<File> tempJRXMLList, String outputFileName, ResultSet rs) {
		int i = 0;
		File file = null;
		ArrayList<File> tempFileList = new ArrayList<File>();
		while(i < tempJRXMLList.size()) {
			file = new File(DOCUMENTS_PATH + outputFileName + "__" + i + ".pdf");
			try {
				file.createNewFile();
				String filePath = tempJRXMLList.get(i).getAbsolutePath();
				JasperReport report = JasperCompileManager.compileReport(filePath);
				JRDataSource jrDataSource = rs.toJRDataSource();
				JasperPrint jasperPrint = JasperFillManager.fillReport(report, new HashMap(), jrDataSource);
				JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
				tempJRXMLList.get(i).delete();
				tempFileList.add(file);
			} catch (JRException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		return tempFileList;
	}
	
	/**
	 * Merges a list of PDF files to one file.
	 * 
	 * @param tempFileList      List of temporary PDF files
	 * @param outputFileName    The name of the output PDF file
	 * 
	 * @return Merged PDF file
	 */
	private static File mergePDFFiles(ArrayList<File> tempFileList, String outputFileName){
		int i = 0;
		File file;
		ArrayList<PDDocument> pdDocumentsList = new ArrayList<PDDocument>();
		while(i < tempFileList.size()) {
			file = tempFileList.get(i);
			PDDocument tempDoc;
			try {
				tempDoc = PDDocument.load(file);
				pdDocumentsList.add(tempDoc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		
		// create new document - all first pages, all second pages, ...
		PDDocument document = new PDDocument();
		int page = 0;
		int pages = pdDocumentsList.get(0).getNumberOfPages();
		while(page < pages) {
			i = 0;
			while(i < pdDocumentsList.size()) {
				PDDocument pdDocument = pdDocumentsList.get(i);
				document.addPage(pdDocument.getPage(page));
				i++;
			}
			page++;
		}

		// save document
		try {
			document.save(DOCUMENTS_PATH + outputFileName + ".pdf");
			document.close();  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  

		// close all PD-Documents
		i = 0;
		while(i < pdDocumentsList.size()){
			try {
				pdDocumentsList.get(i).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
		
		// delete all temporary pdfs
		i = 0;
		while(i < tempFileList.size()) {
			tempFileList.get(i).delete();
			i++;
		}
		
		// return the new build pdf
		file = new File(DOCUMENTS_PATH + outputFileName + ".pdf");
		return file;
	}
	
	/**
	 * Checks if the amount of needed columns can be placed on one report. If there is not enough space, the sheet configuration
	 * will be split into multiple ones.
	 * 
	 * @param sheetConfig       The sheet configuration which consists of all the data needed for a report.
	 * @param outputFileName    The name of the output PDF file
	 * 
	 * @return List of sheet configurations
	 */
	private static ArrayList<SheetConfiguration> fitNormal(SheetConfiguration sheetConfig, int sheetWidth){
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
		Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();
		
		ArrayList<SheetConfiguration> sheetConfigList = new ArrayList<SheetConfiguration>();
		SheetConfiguration tempSheetConfig = new SheetConfiguration();
		tempSheetConfig.setReportDetails(sheetConfig.getReportDetails());
		tempSheetConfig.setFontSize(sheetConfig.getFontSize());
		int allFieldsWidth = 0;
		while(iterator.hasNext()) {
			ColumnConfiguration cc = iterator.next();
			String fieldName = cc.getFieldName();
			int width = cc.getWidth();
			String header = cc.getHeader();

			if ((allFieldsWidth += width + PDF_OFFSET) > sheetWidth) {
				sheetConfigList.add(tempSheetConfig);
				tempSheetConfig = new SheetConfiguration();
				tempSheetConfig.setReportDetails(sheetConfig.getReportDetails());
				tempSheetConfig.setFontSize(sheetConfig.getFontSize());
				allFieldsWidth = 0;
			}
			
			try {
				tempSheetConfig.addColumn(fieldName, width, header);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sheetConfigList.add(tempSheetConfig);
		return sheetConfigList;
	}
	
	/**
	 * Edits the column configuration so all columns fit on one page.
	 * 
	 * @param sheetConfig       The sheet configuration which consists of all the data needed for a report.
	 * @param outputFileName    The name of the output PDF file
	 * 
	 * @return List of sheet configurations
	 */
	private static ArrayList<SheetConfiguration> fitToWidth(SheetConfiguration sheetConfig, int sheetWidth){
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
		Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();
		
		ArrayList<SheetConfiguration> sheetConfigList = new ArrayList<SheetConfiguration>();
		SheetConfiguration tempSheetConfig = new SheetConfiguration();
		tempSheetConfig.setReportDetails(sheetConfig.getReportDetails());
		tempSheetConfig.setFontSize(sheetConfig.getFontSize());
		int allFieldsWidth = 0;
		
		while(iterator.hasNext()) {
			ColumnConfiguration cc = iterator.next();
			int width = cc.getWidth();
			allFieldsWidth += width + PDF_OFFSET;
		}
		
		double scaling = (double) sheetWidth / allFieldsWidth;
		
		if (scaling < 1) {
			
			iterator = columnConfigList.iterator();
			
			while(iterator.hasNext()) {
				ColumnConfiguration cc = iterator.next();
				int width = cc.getWidth();
				
				double tempWidth = (width - PDF_OFFSET) * scaling;
				cc.setWidth((int) Math.ceil(tempWidth));
			}
		}
		sheetConfigList.add(sheetConfig);
		return sheetConfigList;
	}
	
	private static File writeTempJRXML(SheetConfiguration sheetConfig, DataRow fieldTypes, ResultSet rs, boolean landscapeMode, int index, int fitTo, boolean baristaMode) {
		InputStream stream;
		if (landscapeMode) {
			stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baseReport_landscape.jrxml");
		}else {
			stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baseReport_portrait.jrxml");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String report = "";
		String line;
        try {
			while((line = reader.readLine()) != null) {
			   report += line;
			}
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		} 

        String header = "";
        ReportDetails reportDetails = sheetConfig.getReportDetails();
        if(baristaMode) {
    		stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baristaReport_header.txt");
    		reader = new BufferedReader(new InputStreamReader(stream));
        	
            try {
    			while((line = reader.readLine()) != null) {
    			   header += line;
    			}
    			stream.close();
    		} catch (IOException e1) {
    			e1.printStackTrace();
    			return null;
    		} 
	        if(reportDetails != null) {
	        	header = header.replace("[[ID]]", reportDetails.getReportID());
	        	header = header.replace("[[DATE]]", reportDetails.getFormattedQueryTimestamp());
	        	header = header.replace("[[PROCESSDATE]]", reportDetails.getFormattedProcessDate());
	        	header = header.replace("[[REPORTNAME]]", reportDetails.getReportName());
	        	header = header.replace("[[TABLENAME]]", reportDetails.getTableName());
	        }
        }
        report = report.replace("[[header]]", header);
        
        List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
        Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();
		
        String fieldNames = "";
		while(iterator.hasNext()) {
			String fieldName = iterator.next().getFieldName();
			fieldNames += "<field name=\"" + fieldName + "\" class=\"";
			int fieldType = fieldTypes.getFieldAsNumber(fieldName).intValue();
			switch(fieldType) {
				case java.sql.Types.CHAR:
					fieldNames += "java.lang.String\"/>\n";
					break;
				case java.sql.Types.NUMERIC:
					fieldNames += "java.lang.Number\"/>\n";
					break;
				case java.sql.Types.INTEGER:
					fieldNames += "java.lang.Integer\"/>\n";
					break;
				case java.sql.Types.VARCHAR:
					fieldNames += "java.lang.String\"/>\n";
					break;
				case java.sql.Types.DOUBLE:
					fieldNames += "java.lang.Double\"/>\n";
					break;
				case java.sql.Types.DATE:
					fieldNames += "java.sql.Date\"/>\n";
					break;
				case java.sql.Types.TIMESTAMP:
					fieldNames += "java.sql.Timestamp\"/>\n";
					break;
				case java.sql.Types.BOOLEAN:
					fieldNames += "java.lang.Boolean\"/>\n";
					break;
				default:
					fieldNames += "java.lang.String\"/>\n";
					break;
			}
		}

		int pos = 0;
		iterator = columnConfigList.iterator();
		float fontSize = sheetConfig.getFontSize();
		
		if (fitTo == PDF_EXPORT_FIT_TO_HEIGHT || fitTo == PDF_EXPORT_FIT_TO_PAGE) {
			float tempFontSize = fitToHeightFontSize(rs.size());
			if (tempFontSize < fontSize) {
				if (tempFontSize < 3.5f) {
					fontSize = 3.5f;
				}else {
					fontSize = tempFontSize;
				}
			}
			System.out.println(tempFontSize);
		}
		
		int bandHeight = (int) (fontSize + 8);
		int fieldHeight = (int) (fontSize + 5);
		String columnHeaders = "<band height=\"" + bandHeight + "\" splitType=\"Stretch\">\n";
		while(iterator.hasNext()) {
			ColumnConfiguration columnConfig = iterator.next();
			String columnHeader = "<staticText>\n"
					+		"<reportElement x=\"" + pos + "\" y=\"0\" width=\"" + columnConfig.getWidth() + "\" height=\"" + fieldHeight + "\"/>\n"
					+           "<textElement  textAlignment=\"" + PDF_FIELD_VALUE_ALIGNTMENT_LEFT + "\">\n"
					+ 				"<font fontName=\"DejaVu Sans\" size=\"" + fontSize + "\" isBold=\"true\"/>\n"
					+ 			"</textElement>\n"
					+ 			"<text><![CDATA[" + columnConfig.getHeader() + "]]></text>\n"
					+	"</staticText>\n";
			columnHeaders += columnHeader;		
			pos += columnConfig.getWidth() + PDF_OFFSET;
		}
		
		pos = 0;
		iterator = columnConfigList.iterator();
//		fontSize = sheetConfig.getFontSize();
		bandHeight = (int) (fontSize + 2);
		fieldHeight = (int) (fontSize + 2);
		String alignment;
		String columnDetails = "<band height=\"" + bandHeight + "\" splitType=\"Stretch\">\n";
		while(iterator.hasNext()) {
			ColumnConfiguration columnConfig = iterator.next();
			int fieldType = fieldTypes.getFieldAsNumber(columnConfig.getFieldName()).intValue();
			if(fieldType == java.sql.Types.VARCHAR || fieldType == java.sql.Types.CHAR || fieldType == java.sql.Types.DATE || fieldType == java.sql.Types.TIMESTAMP || fieldType == java.sql.Types.BOOLEAN) {
				alignment = PDF_FIELD_VALUE_ALIGNTMENT_LEFT;
			}else {
				alignment = PDF_FIELD_VALUE_ALIGNTMENT_RIGHT;
			}
			String columnDetail = "<textField isBlankWhenNull=\"true\">\n"
					+		"<reportElement x=\"" + pos + "\" y=\"0\" width=\"" + columnConfig.getWidth() + "\" height=\"" + fieldHeight + "\">\n"
				  //+			"<printWhenExpression><![CDATA[$F{" + columnConfig.getFieldName() + "} !=null]]></printWhenExpression>"
					+		"</reportElement>\n"
					+ 		"<textElement textAlignment=\"" + alignment + "\">\n"
					+			"<font fontName=\"DejaVu Sans\" size=\"" + fontSize + "\"/>\n"
					+		"</textElement>\n";
			
			switch(fieldType) {
				case java.sql.Types.DATE:
					columnDetail +=	"<textFieldExpression><![CDATA[new java.text.SimpleDateFormat(\"" + reportDetails.getDateMask() + "\").format($F{" + columnConfig.getFieldName() + "})]]></textFieldExpression>\n"
							+	"</textField>\n";
					break;
				case java.sql.Types.TIMESTAMP:
					columnDetail +=	"<textFieldExpression><![CDATA[new java.text.SimpleDateFormat(\"" + reportDetails.getTimestampMask() + "\").format($F{" + columnConfig.getFieldName() + "})]]></textFieldExpression>\n"
							+	"</textField>\n";
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.NUMERIC:
					columnDetail +=	"<textFieldExpression><![CDATA[((java.util.concurrent.Callable<String>)() -> {\r\n" + 
							"						java.text.DecimalFormat df = new DecimalFormat(\"#,###,###,##0.0#\");\r\n" + 
							"						java.text.DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();\r\n" + 
							"	   				    dfs.setDecimalSeparator('" + reportDetails.getNumberDecimalSeparator() + "');\r\n" + 
							"	   				    dfs.setGroupingSeparator('" + reportDetails.getNumberGroupSeparator() + "');\r\n" + 
							"					    df.setDecimalFormatSymbols(dfs);\r\n" + 
							"					    return df.format($F{" + columnConfig.getFieldName() + "});\r\n" + 
							"					}).call()]]></textFieldExpression>\n"
							+	"</textField>\n";
					break;
				case java.sql.Types.BOOLEAN:
					columnDetail += "<textFieldExpression><![CDATA[$F{" + columnConfig.getFieldName() + "} ? \"\u2713\" : \"\u2717\"]]></textFieldExpression>\r\n"
							+	"</textField>\n";
					break;
				default:
					columnDetail +=	"<textFieldExpression><![CDATA[$F{" + columnConfig.getFieldName() + "}]]></textFieldExpression>\n"
							+	"</textField>\n";
					break;
			}
			
			columnDetails += columnDetail;
			pos += columnConfig.getWidth() + PDF_OFFSET;
		}
		
		report = report.replace("[[a]]", fieldNames);
		report = report.replace("[[b]]", columnHeaders);
		report = report.replace("[[c]]", columnDetails);
		
		File tempJRXML = new File(DOCUMENTS_PATH + "tempJRXML__" + index + ".jrxml");
		try {
			tempJRXML.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempJRXML.getAbsolutePath()), "UTF8"));
			out.append(report).append("\r\n");
		    out.flush();
		    out.close();
	    } catch (IOException e)
	    {
	       System.out.println(e.getMessage());
	    } catch (Exception e)
	    {
	       System.out.println(e.getMessage());
	    }
		return tempJRXML;
	}
	
	private static float fitToHeightFontSize(int rows){
		float scale = 50 / (float) rows;
		float fontSize = scale * 7;
		return fontSize;
	}
	
	private static DataRow getFieldTypes(SheetConfiguration sheetConfig, ResultSet rs) {
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
        Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();
		
        DataRow fieldTypesDR = new DataRow();
        
		while(iterator.hasNext()) {
			String fieldName = iterator.next().getFieldName();
			DataRow dr = rs.get(0);
			int fieldType = dr.getFieldType(fieldName);
			try {
				fieldTypesDR.setFieldValue(fieldName, fieldType);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return fieldTypesDR;
	}

}
