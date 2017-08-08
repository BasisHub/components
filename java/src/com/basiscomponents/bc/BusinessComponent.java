package com.basiscomponents.bc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public interface BusinessComponent {

	public DataRow getAttributesRecord();

	public String getDBQuoteString();

	public DataRow getFilter();

	public void setFilter(DataRow filter);

	public DataRow getFieldSelection();

	public void setFieldSelection(DataRow fieldSelection);

	public void setFieldSelection(Collection<String> fieldSelection);

	public String getScope();

	public void setScope(String scope);

	public HashMap<String, ArrayList<String>> getScopeDef();

	public void setScopeDef(HashMap<String, ArrayList<String>> scopes);

	public ResultSet retrieve() throws Exception;

	public ResultSet retrieve(int first, int last) throws Exception; 

	public Collection<String> validateWrite(DataRow dr);

	public DataRow write(DataRow row) throws Exception;

	public Collection<String> validateRemove(DataRow dr);

	public void remove(DataRow row) throws Exception;

	public DataRow getNewObjectTemplate(DataRow conditions);

}