package com.basiscomponents.db.importer;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

/**
 * 
 * Importer Class to import from a List to a Resultset
 *
 */
public class ListImporter {

	/**
	 * Create a ResultSet from a List (like BBjVector)
	 * 
	 * @param List              li: a List (also BBjVector) containing the cell
	 *                          contents
	 * @param AttributesRecord: an empty predefined record that is used as a
	 *                          template
	 * @return
	 * @throws ParseException
	 */
	public static ResultSet ResultSetFromVector(List li, DataRow AttributesRecord) throws ParseException {

		ResultSet rs = new ResultSet();
		DataRow row;
		List<String> fields = AttributesRecord.getFieldNames();
		Iterator it = li.iterator();
		Iterator<String> itf = null;
		while (it.hasNext()) {

			row = AttributesRecord.clone();
			itf = fields.iterator();
				
			while (itf.hasNext() && it.hasNext()) {
				String name = itf.next();
				Object value = it.next();
				row.setFieldValue(name, value);
			}
			rs.add(row);
		}
		return rs;
		
	}
	
}
