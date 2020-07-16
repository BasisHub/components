package com.basiscomponents.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;

import com.basiscomponents.db.export.CsvExport;
import com.basiscomponents.db.export.HtmlExport;
import com.basiscomponents.db.export.PdfExport;
import com.basiscomponents.db.export.SheetConfiguration;
import com.basiscomponents.db.export.TxtExport;
import com.basiscomponents.db.export.XlsxExport;
import com.basiscomponents.db.export.XmlExport;

import net.sf.jasperreports.engine.JasperPrint;

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
		XmlExport.writeXML(resultSet, rootTagName, entityName, writer, null, false);
	}
	
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
	 * @param baseDR		the data row, that holds the column sequence and the column labels (if available)
	 * @param useLabel		indicates if the label instead of the field name should be used
	 * 
	 * @throws Exception Gets thrown in case the XML could not be written.
	 */
	public static void writeXML(ResultSet resultSet, String rootTagName, String entityName, Writer writer, DataRow baseDR, boolean useLabel)
			throws Exception {
		XmlExport.writeXML(resultSet, rootTagName, entityName, writer, baseDR, useLabel);
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
		ResultSetExporter.writeHTML(resultSet, writer, null, null, false);
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
	 * @param baseDR		the data row, that holds the column sequence and the column labels (if available)
	 * @param useLabel		indicates if the label instead of the field name should be used
	 * @throws Exception
	 */
	public static void writeHTML(ResultSet resultSet, Writer writer, DataRow baseDR, boolean useLabel) throws Exception {
		HtmlExport.writeHTML(resultSet, writer, null, baseDR, useLabel);
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
		ResultSetExporter.writeHTML(resultSet, writer, links, null, false);
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
	 * @param baseDR		the data row, that holds the column sequence and the column labels (if available)
	 * @param useLabel		indicates if the label instead of the field name should be used
	 * @throws Exception
	 */
	public static void writeHTML(ResultSet resultSet, Writer writer, HashMap<String, String> links, DataRow baseDR, boolean useLabel) throws Exception {
		HtmlExport.writeHTML(resultSet, writer, links, baseDR, useLabel);
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
		ResultSetExporter.writeTXT(resultSet, writer, null, false);
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
	 * @param baseDR		the data row, that holds the column sequence and the column labels (if available)
	 * @param useLabel		indicates if the label instead of the field name should be used
	 * @throws Exception
	 */
	public static void writeTXT(ResultSet resultSet, Writer writer, DataRow baseDR, Boolean useLabel) throws Exception {
		TxtExport.writeTXT(resultSet, writer, baseDR, useLabel);
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

				writeXLSX(rs, os, writeHeader, false, sheetConfig.getSheetName(), ar, sheetConfig);

				os.flush();
				os.close();
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

				writeXLSX(rs, os, writeHeader, useLabelIfPresent, sheetName,
						AttributesRecord);

				os.flush();
				os.close();
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
		XlsxExport.writeXLSX(rs, out, writeHeader, useLabelIfPresent, sheetName, AttributesRecord, sheetConfig);
	}

	/**
	 * Exports the content of the given ResultSet from a BBjGridExWidget
	 * 
	 * @param file    			The output file.
	 * @param rs		        The ResultSet to export.
	 * @param sheetConfig       The sheet configuration for the report
	 * @param baristaMode		Indicator if the Barista header should be displayed or not
	 * @param fitTo		        Content fitting indicator
	 * @param landscapeMode     Indicator if the PDF should be created in landscape mode or else in portrait mode
	 * 
	 * @return The final exported PDF file
	 */
	public static File exportToPDF(File file, ResultSet rs, SheetConfiguration sheetConfig, boolean baristaMode, int fitTo, boolean landscapeMode) {
//		return PdfExport.exportToPDF(file, rs, sheetConfig, baristaMode, fitTo, landscapeMode);
		JasperPrint jasperPrint = PdfExport.exportToJasperPrint(file, rs, sheetConfig, baristaMode, fitTo, landscapeMode);
		return PdfExport.writePdf(file, jasperPrint);
	}
	
	/**
	 * Exports the content of the given ResultSet from a BBjGridExWidget
	 * 
	 * @param outputFile		The output file
	 * @param customReport		The custom report file
	 * @param resultSet         The ResultSet to export.
	 * 
	 * @return The final exported PDF file
	 */
	public static File exportToPDFByCustomReport(File outputFile, File customReport, ResultSet rs) {
		return PdfExport.exportToPDF(outputFile, customReport, rs);
	}
	
	public static JasperPrint exportToJasperPrint(File file, ResultSet rs, SheetConfiguration sheetConfig, boolean baristaMode, int fitTo, boolean landscapeMode) {
		return PdfExport.exportToJasperPrint(file, rs, sheetConfig, baristaMode, fitTo, landscapeMode);
	}
	
	public static File exportToPdf(File file, JasperPrint jasperPrint) {
		return PdfExport.writePdf(file, jasperPrint);
	}
	
	
	/**
	 * Exports the content of the given ResultSet from a BBjGridExWidget
	 * 
	 * @param outputFile  		The output file
	 * @param resultSet         The ResultSet to export.
	 * 
	 * @return The final exported CSV file
	 */
	public static void exportToCSV(File outputFile, ResultSet rs) {
		CsvExport.exportToCSV(outputFile, rs);
	}
	
	/**
	 * Method to export a result set to a CSV file
	 * 
	 * @param outputFile	the ouput file
	 * @param rs			the result set that holds the data to export
	 * @param baseDR		the data row, that holds the column sequence and the column labels (if available)
	 * @param useLabel		indicates if the label instead of the field name should be used
	 */
	public static void exportToCSV(File outputFile, ResultSet rs, DataRow baseDR, boolean useLabel) {
		CsvExport.exportToCSV(outputFile, rs, baseDR, useLabel);
	}

}
