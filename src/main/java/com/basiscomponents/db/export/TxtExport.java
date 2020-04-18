package com.basiscomponents.db.export;

import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class TxtExport {

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
	
}
