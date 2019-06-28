package com.basiscomponents.bc.config;

import com.basiscomponents.db.DataRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseConfiguration {

	private String dbQuoteString="";
	private List<String > primaryKeys= new ArrayList<>();
	private Map<String, String> mapping = new HashMap<>();
	private String dbType= "";
	private List<String> autoIncrementKeys = new java.util.ArrayList<>();
	private String sqlStatement;

	public DatabaseConfiguration(){
	}
	public boolean addPrimaryKey(String key){
		return primaryKeys.add(key);
	}
	public boolean containsPrimaryKey(String key){
		return primaryKeys.contains(key);
	}
	public boolean isPrimaryKeysEmpty(){
		return primaryKeys.isEmpty();
	}
	public List<String> getPrimaryKeys(){
		return new java.util.ArrayList<>(primaryKeys);
	}
	public boolean isPrimaryKeyPresent(DataRow r) {
		boolean pkPresent = false;
		pkPresent = isPkPresent(pkPresent, r);
		return pkPresent;
	}
	public boolean isPkPresent(boolean pkPresent, DataRow ret) {
		if (!this.isPrimaryKeysEmpty()) {
			ArrayList<String> list = new ArrayList<>();
			for (String field : ret.getFieldNames()) {
				list.add(getMapping(field));
			}
			pkPresent = list.containsAll(getPrimaryKeys());
		}
		return pkPresent;
	}


	public String getDbQuoteString() {
		return dbQuoteString;
	}
	public void setDbQuoteString(String dbQuoteString) {
		this.dbQuoteString= dbQuoteString;
	}
	public String getMapping(String fieldname){
		return mapping.getOrDefault(fieldname,fieldname);
	}
	public void addMapping(String fieldname, String mapping){
		this.mapping.put(fieldname,mapping);
	}
	public HashMap<String,String> getMappings(){
		return new HashMap<>(mapping);
	}

	public String getDbType() {
		return dbType;
	}

	public void setDbType(String dbType) {
		this.dbType = dbType;
	}

	public List<String> getAutoIncrementKeys() {
		return new java.util.ArrayList<>(autoIncrementKeys);
	}

	public void setAutoIncrementKeys(List<String> autoIncrementKeys) {
		this.autoIncrementKeys = new java.util.ArrayList<>(autoIncrementKeys);
	}
	public String getAutoIncrementKey(int i){
		return  autoIncrementKeys.get(i);
	}
	public boolean addAutoIncrementKey(String key){
		return autoIncrementKeys.add(key);
	}

	public String getSqlStatement() {
		return sqlStatement;
	}

	public void setSqlStatement(String sqlStatement) {
		this.sqlStatement = sqlStatement;
	}
}
