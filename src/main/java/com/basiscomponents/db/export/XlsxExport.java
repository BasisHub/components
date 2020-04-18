package com.basiscomponents.db.export;

import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.SqlTypeNames;

public class XlsxExport {

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
	
}
