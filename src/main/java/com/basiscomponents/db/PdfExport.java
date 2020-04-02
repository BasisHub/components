package com.basiscomponents.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import com.basiscomponents.db.config.export.ColumnConfiguration;
import com.basiscomponents.db.config.export.ReportDetails;
import com.basiscomponents.db.config.export.SheetConfiguration;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;

class PdfExport {

	private static final int PDF_WIDTH_LANDSCAPE = 802;
	private static final int PDF_WIDTH_PORTRAIT = 555;
	private static final int PDF_OFFSET = 5;
	private static final String USER_HOME = System.getProperty("user.home")+"/";
	private static final String PDF_FIELD_VALUE_ALIGNTMENT_LEFT = "Left";
	private static final String PDF_FIELD_VALUE_ALIGNTMENT_RIGHT = "Right";
	private static final int PDF_EXPORT_NO_FIT = 0;
	private static final int PDF_EXPORT_FIT_TO_WIDTH = 1;
	private static final int PDF_EXPORT_FIT_TO_HEIGHT = 2;
	private static final int PDF_EXPORT_FIT_TO_PAGE = 3;

	/**
	 * Exports the content of the given ResultSet from a BBjGridExWidget
	 * 
	 * @param outputFileName
	 *            The name of the PDF file
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param sheetConfig
	 *            The sheet configuration for the report
	 * @param baristaMode
	 *            indicator if the Barista header should be displayed or not
	 * @param fitTo
	 *            content fitting indicator
	 * @param landscapeMode
	 *            indicator if the PDF should be created in landscape mode or else
	 *            in portrait mode
	 * 
	 * @return The final exported PDF file
	 */
	public static File exportToPDF(String outputFileName, String filePath, ResultSet rs, SheetConfiguration sheetConfig,
			boolean baristaMode, int fitTo, boolean landscapeMode) {
		int sheetWidth;
		if (landscapeMode) {
			sheetWidth = PDF_WIDTH_LANDSCAPE;
		} else {
			sheetWidth = PDF_WIDTH_PORTRAIT;
		}
		
		if(filePath == null || filePath.trim().isEmpty()) {
			filePath = USER_HOME;
		}

		ArrayList<SheetConfiguration> sheetConfigList;
		switch (fitTo) {
		// splitting the original sheetConfiguration in multiple ones since there are
		// possibly too many columns to fit on just one page.
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
		String report;
		File tempJRXML;
		if (sheetConfigList.size() <= 0) {
			// creating a jrxml file (a report; placing columns and so on)
			report = buildReport(sheetConfig, fieldTypes, rs, landscapeMode, 0, fitTo, baristaMode);
			tempJRXML = writeTempJRXML(report, filePath, 0);
			tempJRXMLList.add(tempJRXML);
		} else {
			int i = 0;
			while (i < sheetConfigList.size()) {
				report = buildReport(sheetConfigList.get(i), fieldTypes, rs, landscapeMode, i, fitTo,
						baristaMode);
				tempJRXML = writeTempJRXML(report, filePath, i);
				tempJRXMLList.add(tempJRXML);
				i++;
			}
		}

		// creating the PDF files out of the tempJRXML files
		ArrayList<File> tempFileList = writePDFs(tempJRXMLList, outputFileName, filePath, rs);

		// merging the created pdf files to one file
		return mergePDFFiles(tempFileList, outputFileName, filePath);
	}

	/**
	 * Compiles a list of jasper reports, fills them with the result set data and
	 * exports them as PDF files.
	 * 
	 * @param tempJRXMLList
	 *            List of temporary jrxml files
	 * @param outputFileName
	 *            The name of the PDF file
	 * @param resultSet
	 *            The ResultSet to export
	 * 
	 * @return List of exported PDF files
	 */
	private static ArrayList<File> writePDFs(ArrayList<File> tempJRXMLList, String outputFileName, String filePath, ResultSet rs) {
		int i = 0;
		File file = null;
		ArrayList<File> tempFileList = new ArrayList<File>();
		while (i < tempJRXMLList.size()) {
			file = new File(filePath + outputFileName + "__" + i + ".pdf");
			try {
				file.createNewFile();
				filePath = tempJRXMLList.get(i).getAbsolutePath();
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
	 * @param tempFileList
	 *            List of temporary PDF files
	 * @param outputFileName
	 *            The name of the output PDF file
	 * 
	 * @return Merged PDF file
	 */
	private static File mergePDFFiles(ArrayList<File> tempFileList, String outputFileName, String filePath) {
		int i = 0;
		File file;
		ArrayList<PDDocument> pdDocumentsList = new ArrayList<PDDocument>();
		while (i < tempFileList.size()) {
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
		while (page < pages) {
			i = 0;
			while (i < pdDocumentsList.size()) {
				PDDocument pdDocument = pdDocumentsList.get(i);
				document.addPage(pdDocument.getPage(page));
				i++;
			}
			page++;
		}

		// save document
		try {
			document.save(filePath + outputFileName + ".pdf");
			document.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// close all PD-Documents
		i = 0;
		while (i < pdDocumentsList.size()) {
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
		while (i < tempFileList.size()) {
			tempFileList.get(i).delete();
			i++;
		}

		// return the new build pdf
		file = new File(filePath + outputFileName + ".pdf");
		return file;
	}

	/**
	 * Checks if the amount of needed columns can be placed on one report. If there
	 * is not enough space, the sheet configuration will be split into multiple
	 * ones.
	 * 
	 * @param sheetConfig
	 *            The sheet configuration which consists of all the data needed for
	 *            a report.
	 * @param outputFileName
	 *            The name of the output PDF file
	 * 
	 * @return List of sheet configurations
	 */
	private static ArrayList<SheetConfiguration> fitNormal(SheetConfiguration sheetConfig, int sheetWidth) {
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
		Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();

		ArrayList<SheetConfiguration> sheetConfigList = new ArrayList<SheetConfiguration>();
		SheetConfiguration tempSheetConfig = new SheetConfiguration();
		tempSheetConfig.setReportDetails(sheetConfig.getReportDetails());
		tempSheetConfig.setFontSize(sheetConfig.getFontSize());
		int allFieldsWidth = 0;
		while (iterator.hasNext()) {
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
				tempSheetConfig.addColumn(header, width, fieldName);
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
	 * @param sheetConfig
	 *            The sheet configuration which consists of all the data needed for
	 *            a report.
	 * @param outputFileName
	 *            The name of the output PDF file
	 * 
	 * @return List of sheet configurations
	 */
	private static ArrayList<SheetConfiguration> fitToWidth(SheetConfiguration sheetConfig, int sheetWidth) {
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
		Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();

		ArrayList<SheetConfiguration> sheetConfigList = new ArrayList<SheetConfiguration>();
		SheetConfiguration tempSheetConfig = new SheetConfiguration();
		tempSheetConfig.setReportDetails(sheetConfig.getReportDetails());
		tempSheetConfig.setFontSize(sheetConfig.getFontSize());
		int allFieldsWidth = 0;

		while (iterator.hasNext()) {
			ColumnConfiguration cc = iterator.next();
			int width = cc.getWidth();
			allFieldsWidth += width + PDF_OFFSET;
		}

		double scaling = (double) sheetWidth / allFieldsWidth;

		if (scaling < 1) {

			iterator = columnConfigList.iterator();

			while (iterator.hasNext()) {
				ColumnConfiguration cc = iterator.next();
				int width = cc.getWidth();

				double tempWidth = (width - PDF_OFFSET) * scaling;
				cc.setWidth((int) Math.ceil(tempWidth));
			}
		}
		sheetConfigList.add(sheetConfig);
		return sheetConfigList;
	}

	/**
	 * Places the columns and their respective headers on the report.
	 * 
	 * @param outputFileName
	 *            The name of the PDF file
	 * @param fieldTypes
	 *            The DataField field types
	 * @param resultSet
	 *            The ResultSet to export.
	 * @param sheetConfig
	 *            The sheet configuration for the report
	 * @param baristaMode
	 *            indicator if the Barista header should be displayed or not
	 * @param fitTo
	 *            content fitting indicator
	 * @param landscapeMode
	 *            indicator if the PDF should be created in landscape mode or else
	 *            in portrait mode
	 * 
	 * @return The build report as string
	 */
	private static String buildReport(SheetConfiguration sheetConfig, DataRow fieldTypes, ResultSet rs,
			boolean landscapeMode, int index, int fitTo, boolean baristaMode) {
		InputStream stream;
		if (landscapeMode) {
			stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baseReport_landscape.jrxml");
		} else {
			stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baseReport_portrait.jrxml");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		String report = "";
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				report += line;
			}
			stream.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}

		String header = "";
		ReportDetails reportDetails = sheetConfig.getReportDetails();
		if (baristaMode) {
			stream = ResultSetExporter.class.getClassLoader().getResourceAsStream("baristaReport_header.txt");
			reader = new BufferedReader(new InputStreamReader(stream));

			try {
				while ((line = reader.readLine()) != null) {
					header += line;
				}
				stream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}

			if (reportDetails != null) {
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
		while (iterator.hasNext()) {
			String fieldName = iterator.next().getFieldName();
			fieldNames += "<field name=\"" + fieldName + "\" class=\"";
			int fieldType = fieldTypes.getFieldAsNumber(fieldName).intValue();
			switch (fieldType) {
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
				} else {
					fontSize = tempFontSize;
				}
			}
		}

		int bandHeight = (int) (fontSize + 8);
		int fieldHeight = (int) (fontSize + 5);
		String columnHeaders = "<band height=\"" + bandHeight + "\" splitType=\"Stretch\">\n";
		while (iterator.hasNext()) {
			ColumnConfiguration columnConfig = iterator.next();
			String columnHeader = "<staticText>\n" + "<reportElement x=\"" + pos + "\" y=\"0\" width=\""
					+ columnConfig.getWidth() + "\" height=\"" + fieldHeight + "\"/>\n"
					+ "<textElement  textAlignment=\"" + PDF_FIELD_VALUE_ALIGNTMENT_LEFT + "\">\n"
					+ "<font fontName=\"DejaVu Sans\" size=\"" + fontSize + "\" isBold=\"true\"/>\n"
					+ "</textElement>\n" + "<text><![CDATA[" + columnConfig.getHeader() + "]]></text>\n"
					+ "</staticText>\n";
			columnHeaders += columnHeader;
			pos += columnConfig.getWidth() + PDF_OFFSET;
		}

		pos = 0;
		iterator = columnConfigList.iterator();
		// fontSize = sheetConfig.getFontSize();
		bandHeight = (int) (fontSize + 2);
		fieldHeight = (int) (fontSize + 2);
		String alignment;
		String columnDetails = "<band height=\"" + bandHeight + "\" splitType=\"Stretch\">\n";
		while (iterator.hasNext()) {
			ColumnConfiguration columnConfig = iterator.next();
			int fieldType = fieldTypes.getFieldAsNumber(columnConfig.getFieldName()).intValue();
			if (fieldType == java.sql.Types.VARCHAR || fieldType == java.sql.Types.CHAR
					|| fieldType == java.sql.Types.DATE || fieldType == java.sql.Types.TIMESTAMP
					|| fieldType == java.sql.Types.BOOLEAN) {
				alignment = PDF_FIELD_VALUE_ALIGNTMENT_LEFT;
			} else {
				alignment = PDF_FIELD_VALUE_ALIGNTMENT_RIGHT;
			}
			String columnDetail = "<textField isBlankWhenNull=\"true\">\n" + "<reportElement x=\"" + pos
					+ "\" y=\"0\" width=\"" + columnConfig.getWidth() + "\" height=\"" + fieldHeight + "\">\n"
					// + "<printWhenExpression><![CDATA[$F{" + columnConfig.getFieldName() + "}
					// !=null]]></printWhenExpression>"
					+ "</reportElement>\n" + "<textElement textAlignment=\"" + alignment + "\">\n"
					+ "<font fontName=\"DejaVu Sans\" size=\"" + fontSize + "\"/>\n" + "</textElement>\n";

			switch (fieldType) {
			case java.sql.Types.DATE:
				columnDetail += "<textFieldExpression><![CDATA[new java.text.SimpleDateFormat(\""
						+ reportDetails.getDateMask() + "\").format($F{" + columnConfig.getFieldName()
						+ "})]]></textFieldExpression>\n" + "</textField>\n";
				break;
			case java.sql.Types.TIMESTAMP:
				columnDetail += "<textFieldExpression><![CDATA[new java.text.SimpleDateFormat(\""
						+ reportDetails.getTimestampMask() + "\").format($F{" + columnConfig.getFieldName()
						+ "})]]></textFieldExpression>\n" + "</textField>\n";
				break;
			case java.sql.Types.DOUBLE:
			case java.sql.Types.NUMERIC:
				columnDetail += "<textFieldExpression><![CDATA[((java.util.concurrent.Callable<String>)() -> {\r\n"
						+ "						java.text.DecimalFormat df = new DecimalFormat(\"#,###,###,##0.0#\");\r\n"
						+ "						java.text.DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();\r\n"
						+ "	   				    dfs.setDecimalSeparator('" + reportDetails.getNumberDecimalSeparator()
						+ "');\r\n" + "	   				    dfs.setGroupingSeparator('"
						+ reportDetails.getNumberGroupSeparator() + "');\r\n"
						+ "					    df.setDecimalFormatSymbols(dfs);\r\n"
						+ "					    return df.format($F{" + columnConfig.getFieldName() + "});\r\n"
						+ "					}).call()]]></textFieldExpression>\n" + "</textField>\n";
				break;
			case java.sql.Types.BOOLEAN:
				columnDetail += "<textFieldExpression><![CDATA[$F{" + columnConfig.getFieldName()
						+ "} ? \"\u2713\" : \"\u2717\"]]></textFieldExpression>\r\n" + "</textField>\n";
				break;
			default:
				columnDetail += "<textFieldExpression><![CDATA[$F{" + columnConfig.getFieldName()
						+ "}]]></textFieldExpression>\n" + "</textField>\n";
				break;
			}

			columnDetails += columnDetail;
			pos += columnConfig.getWidth() + PDF_OFFSET;
		}

		report = report.replace("[[a]]", fieldNames);
		report = report.replace("[[b]]", columnHeaders);
		report = report.replace("[[c]]", columnDetails);
		
		return report;
	}
	
	private static File writeTempJRXML(String report, String filePath, int index) {
		File tempJRXML = new File(filePath + "tempJRXML__" + index + ".jrxml");
		tempJRXML.deleteOnExit();
		try { // try with resources
			tempJRXML.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try (Writer out = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(tempJRXML.getAbsolutePath()), "UTF8"))) {
			out.append(report).append("\r\n");
			out.flush();
			out.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return tempJRXML;
	}
	
	

	/**
	 * Calculates the font size for the report, if the report should fit to height.
	 * 
	 * @param rows
	 *            number of rows in the result set
	 * 
	 * @return returns the calculated font size
	 */
	private static float fitToHeightFontSize(int rows) {
		float scale = 50 / (float) rows;
		float fontSize = scale * 7;
		return fontSize;
	}

	/**
	 * Calculates the font size for the report, if the report should fit to height.
	 * 
	 * @param sheetConfig
	 *            The sheet configuration
	 * @param rs
	 *            The result set
	 * 
	 * @return returns a DataRow with all the field types
	 */
	private static DataRow getFieldTypes(SheetConfiguration sheetConfig, ResultSet rs) {
		List<ColumnConfiguration> columnConfigList = sheetConfig.getColumnConfigurations();
		Iterator<ColumnConfiguration> iterator = columnConfigList.iterator();

		DataRow fieldTypesDR = new DataRow();

		while (iterator.hasNext()) {
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
