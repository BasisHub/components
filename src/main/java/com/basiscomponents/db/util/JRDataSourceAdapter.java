package com.basiscomponents.db.util;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

/**
 * 
 * JRDataSourceResultSet 
 * represents a ResultSet as a JRDataSource
 * for processing with JasperReports
 * @see com.basiscomponents.db.ResultSet
 * 
 */
public class JRDataSourceAdapter implements JRDataSource {

	ResultSet rs;
	Iterator<DataRow> it;
	DataRow currRow;
	
	/**
	 * public constructor
	 * @param com.basiscomponents.db.ResultSet rs - the resultset to be represented for Jasper
	 */
	public JRDataSourceAdapter(ResultSet rs) {
		it = rs.iterator();
	}
	
	private JRDataSourceAdapter() {
	}
	
	/**
	 * implementation of the getFieldValue method of the interface
	 * @param JRField field - the field on the report
	 * @return Object o - the plain object from the DataRow with the field name
	 */
	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object o = null; 
		try {
			o=currRow.getField(field.getName()).getObject();
			
			if (o.getClass().equals(com.basiscomponents.db.ResultSet.class)) {
				o=new JRDataSourceAdapter((ResultSet) o);
			}
			
		}
		catch (Exception e) {
			
		}
		finally {}
		return o;
	}
	
	/**
	 * implementation of the next() method 
	 * @return true if there is a next record , false if not
	 */
	@Override
	public boolean next() throws JRException {
		if (it.hasNext()) {
			currRow = it.next();
			return true;
		}
		return false;
	}


}
