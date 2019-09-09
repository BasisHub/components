package com.basiscomponents.bc;

import com.basis.startup.type.BBjException;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.importer.JLibResultSetImporter;

import java.util.Collection;

public class BBjTableBC implements BusinessComponent{

	JLibResultSetImporter importer;

	public BBjTableBC(String filePath, String template){
		importer = new com.basiscomponents.db.importer.JLibResultSetImporter();
		try {
			importer.setFile(filePath,template);
		} catch (BBjException e) {
			throw new RuntimeException("Could not load File ["+filePath+"] with template ["+template+"]", e);
		}
	}
	@Override
	public DataRow getAttributesRecord() {
		return null;
	}

	@Override
	public void setFilter(DataRow filter) {

	}

	@Override
	public void setFieldSelection(DataRow fieldSelection) {

	}

	@Override
	public void setFieldSelection(Collection<String> fieldSelection) {
		importer.setFieldSelection(fieldSelection);
	}
	//TODO
	@Override
	public void setScope(String scope) {

	}

	@Override
	public ResultSet retrieve() throws Exception {
		return importer.retrieve();
	}
	//TODO
	@Override
	public ResultSet retrieve(int first, int last) throws Exception {
		return importer.retrieve();
	}

	@Override
	public ResultSet validateWrite(DataRow dr) {
		return null;
	}

	@Override
	public DataRow write(DataRow row) throws Exception {
		return null;
	}

	@Override
	public ResultSet validateRemove(DataRow dr) {
		return null;
	}

	@Override
	public void remove(DataRow row) throws Exception {


	}

	@Override
	public DataRow getNewObjectTemplate(DataRow conditions) {

		return null;
	}
}
