package com.basiscomponents.db;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.basiscomponents.db.constants.ConstantsResolver;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DataRow implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, DataField> DataFields = new HashMap<String, DataField>();

	private HashMap<String, String> Attributes = new HashMap<String, String>();

	private com.basiscomponents.db.ResultSet ResultSet; // containing this row

	private byte[] RowKey = new byte[0];

	private int RowID;

	public DataRow() {
		this.ResultSet = new com.basiscomponents.db.ResultSet();
	}

	public DataRow(com.basiscomponents.db.ResultSet resultset) {
		this.ResultSet = resultset;
	}

	public static DataRow newInstance() {
		return new DataRow();
	}

	public static DataRow newInstance(com.basiscomponents.db.ResultSet resultset) {
		return new DataRow(resultset);
	}

	/**
	 * Creates a new DataRow object by parsing the specified HashMap object 
	 * and creating DataField for each of the HashMap's entries.
	 * 
	 * @param map HashMap containing key value pairs used to initializes the DataRow object
	 */
	public DataRow(java.util.Map<String, Object> map) throws Exception {
		this.ResultSet = new com.basiscomponents.db.ResultSet();
		Set<String> ks = map.keySet();
		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String k = it.next();
			Object o = map.get(k);
			setFieldValue(k, o);
		}
	}

	/**
	 * Returns the row ID of this DataRow object.
	 * 
	 * @return RowID The row ID of this DataRow object  
	 */
	public int getRowID() {
		return RowID;
	}

	/**
	 * Sets the row ID of this DataRow object to the given value
	 * 
	 * @param rowId The new row ID value of this DataRow object
	 */
	public void setRowID(int rowId) {
		RowID = rowId;
	}

	/**
	 * Returns a BBArrayList object with all field names defined in this DataRow object.
	 * 
	 * @return list The BBArrayList containing all field names defined in this DataRow object
	 */
	public BBArrayList getFieldNames() {
		return new BBArrayList(this.ResultSet.getColumnNames());
	}

	/**
	 * Returns true if this DataRow contains a field matching the given field name,
	 * false otherwise.
	 * 
	 * @param name The name of the field.
	 * 
	 * @return contains True if the DataRow contains a field matching the given field name, false otherwise.
	 */
	public Boolean contains(String name) {
		return this.ResultSet.getColumnNames().contains(name);
	}

	/**
	 * Sets the attribute with the specified name and value to this DataRow object.
	 * 
	 * @param name The attribute's name
	 * @param value The attribute's value
	 */
	public void setAttribute(String name, String value) {
		this.Attributes.put(name, value);
	}

	/**
	 * Returns the attribute with the specified name or 
	 * null if no attribute with this name exists
	 * 
	 * @param name The attribute's name
	 * @return attribute The attribute, or null if no attriute with the given name exists
	 */
	public String getAttribute(String name) {
		return this.Attributes.get(name);
	}

	@SuppressWarnings("unchecked")
	public java.util.HashMap<String, String> getAttributes() {
		java.util.HashMap<String, String> clone = (java.util.HashMap<String, String>) this.Attributes.clone();
		return clone;
	}

	/**
	 * Removes the attribute with the specified name from the DataRow's attributes if it exists.
	 * Does nothing if no attribute with the specified name exists.
	 * 
	 * @param name The attributes name
	 */
	public void removeAttribute(String name) {
		this.Attributes.remove(name);
	}

	/**
	 * Returns a String representation of the DataRow object.
	 * 
	 * @return string The String representation of the DataRow object 
	 */
	public String toString() {
		String x = "";
		Iterator<String> it = this.ResultSet.getColumnNames().iterator();
		while (it.hasNext()) {
			String k = it.next();
			String f = "";
			try {
				f = this.getFieldAsString(k);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			x = x + "," + k + "=" + f;
		}
		if (x.length() > 0)
			x = "[" + x.substring(1) + "]";
		else
			x = "(empty)";
		return x;
	}

	/**
	 * Sets the specified value as value to the field with the specified name.
	 * In case no field with the specified name exists, then the field will be created.
	 * 
	 * @param name The name of the field
	 * @param value The value of the field
	 */
	public void setFieldValue(String name, Object value)  {

		if (value != null) {
			String c = value.getClass().getCanonicalName();
			if (c.contains("BBjNumber") | c.contains("BBjInt")) {
				value = Double.parseDouble(value.toString());
			}
		}

		DataField field = null;
		try {
			field = getField(name);
		} catch (Exception e) {
			// do nothing
		}
		if (field != null)
			field.setValue(value);
		else {
			if (value == null) {
				field = new DataField("");
				try {
					addDataField(name, field);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				field.setValue(null);
			} else {
				field = new DataField(value);
				try {
					addDataField(name, field);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * Sets the specified value as value to the field with the specified name.
	 * In case no field with the specified name exists, then the field will be created with the specified SQL type.
	 * 
	 * @param name The name of the field
	 * @param type The SQL type of the field
	 * @param value The value of the field
	 */
	public void setFieldValue(String name, int type, Object value) throws Exception {
		DataField field = null;

		String c = value.getClass().getCanonicalName();
		if (c.contains("BBjNumber") | c.contains("BBjInt")) {
			value = Double.parseDouble(value.toString());
		}

		try {
			field = this.getField(name);
		} catch (Exception e) {
			// do nothing
		}
		if (field != null)
			field.setValue(value);
		else {
			field = new DataField(value);
			addDataField(name, type, field);
		}
	}

	/**
	 * Returns the DataField object with the specified field name.<br>
	 * <br>
	 * Throws an Exception if no field with the specified name exists 
	 * 
	 * @param name The name of the field
	 * @return dataField The DataField object 
	 * 
	 * @throws Exception Gets thrown in case no field with the specified name exists
	 */
	public DataField getField(String name) throws Exception {
		return getField(name, false);
	}

	/**
	 * Returns the DataField object with the specified field name.
	 * 
	 * In case no field with the specified field name exists, depending on the specified boolean value
	 * either an Exception is thrown, or not. In fact, this boolean value indicates whether the Exception 
	 * should be suppressed or not. 
	 * 
	 * @param name The field name
	 * @param silent Boolean value indicating whether the eventual Exception should be suppressed or not
	 * @return dataField The DataField
	 * 
	 * @throws Exception Gets thrown if no field with the specified name exists and the specified boolean value is false
	 */
	public DataField getField(String name, Boolean silent) throws Exception {
		DataField field = this.DataFields.get(name);
		if (field == null && !(silent))
			throw new Exception("Field " + name + " does not exist");
		return field;
	}

	/**
	 * Returns the value of the field with the specified name as java.lang.Object
	 * 
	 * @param name The name of the field
	 * @return value The field's value as Object
	 * 
	 * @throws Exception Gets thrown in case no field with the specified name exists.
	 */
	public Object getFieldValue(String name) throws Exception {
		DataField field = getField(name);
		return field.getValue();
	}

	/**
	 * Returns the string value of the field matching the given field name,
	 * or an empty String in case the field's value has not been set or is null.<br>
	 * <br>
	 * Throws an Exception if the given field name does not exist.
	 * 
	 * @param name The name of the field.
	 * @return fieldValue The String representation of the field's value.
	 * 
	 * @throws Exception Gets thrown in case no field with the specified name exists.
	 */
	public String getFieldAsString(String name) throws Exception {
		if (isFieldNull(name))
			return "";
		DataField field = getField(name);
		return field.getString();
	}

	/**
	 * Returns true if the value of the field with the given name is null, false otherwise.<br>
	 * <br>
	 * Throws an Exception in case the field with the given name doesn't exist.
	 *  
	 * @param name The name of the field
	 * @return isNull True in case the value of the field is null, false otherwise.
	 * 
	 * @throws Exception Gets thrown in case no field with the specified name exists.
	 */
	public Boolean isFieldNull(String name) throws Exception {
		DataField field = getField(name);
		return (field.getValue() == null);
	}

	/**
	 * Returns the value of the field with the given name as String for usage in SQL statements. 
	 * The field's value will be returned in single quotes if it has a value, if not NULL will be returned.<br>
	 * <br>
	 * 
	 * @param name The name of the field
	 * @return fieldValue The field's value as String for usage in SQL statements  
	 * 
	 * @throws Exception Gets thrown in case no field with the specified name exists.
	 */
	public String getFieldForSQL(String name) throws Exception {
		DataField field = getField(name);
		String ret = "";
		if (field.getValue() == null)
			ret = "NULL";
		else
			ret = "'" + field.getString() + "'";
		return ret;
	}

	@SuppressWarnings("deprecation")
	public Double getFieldAsNumber(String name) throws Exception {
		DataField field = getField(name);
		if (ResultSet == null)
			throw new Exception("ResultSet does not exist");
		if (field.getValue() == null)
			return 0.0;

		Double ret = 0.0;

		int column = this.ResultSet.getColumnIndex(name);
		int type = this.ResultSet.getColumnType(column);
		// TODO maybe: make this use reflection and skip the field for the
		// column type, to honor dynamic type changes??
		switch (type) {
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.NCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			String tmp = field.getString();
			if (tmp.isEmpty())
				tmp = "0.0";
			ret = Double.valueOf(tmp);
			break;
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
			ret = field.getInt().doubleValue();
			break;
		case java.sql.Types.BIGINT:
			ret = field.getLong().doubleValue();
			break;
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			ret = field.getBigDecimal().doubleValue();
			break;
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
			ret = field.getDouble();
			break;
		case java.sql.Types.REAL:
			ret = field.getFloat().doubleValue();
			break;
		case java.sql.Types.DATE:
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
			if (field.getDate() == null)
				ret = -1.0;
			else {
				Integer ret2 = com.basis.util.BasisDate.jul(new java.util.Date(field.getDate().getTime()));
				ret = ret2.doubleValue();
			}
			break;
		case java.sql.Types.TIME:
		case java.sql.Types.TIME_WITH_TIMEZONE:
			Time t = field.getTime();
			Double d = (double) t.getHours();
			Double d1 = (double) t.getMinutes();
			d1 = d1 / 60;
			d += d1;
			d1 = (double) t.getSeconds();
			d1 = d1 / 3600;
			d += d1;
			ret = d;
			break;
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			if (field.getBoolean())
				ret = 1.0;
			else
				ret = 0.0;
			break;
		case java.sql.Types.TINYINT:
			ret = ((Integer) Byte.toUnsignedInt(field.getByte())).doubleValue();
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}

	/**
	 * Returns the index of the column with the specified name.<br>
	 * <br>
	 * Throws an Exception if the specified column name doesn't exist. 
	 * 
	 * @param name The name of the column
	 * @return index The column's index
	 * 
	 * @throws Exception Gets thrown if the specified column name doesn't exist
	 */
	public int getColumnIndex(String name) throws Exception {
		return getColumnIndex(name, false);
	}

	/**
	 * Returns the index of the column with the specified name.<br>
	 * <br>
	 * In case the specified column name doesn't exist, depending on the specified boolean value
	 * either an Exception is thrown, or not. In fact, this boolean value indicates whether the Exception 
	 * should be suppressed or not. 
	 * 
	 * @param name The name of the column
	 * @param silent Boolean value indicating whether the eventual Exception should be suppressed or not
	 * @return index The column's index
	 * 
	 * @throws Exception Gets thrown if the specified column name doesn't exist and the silent boolean value is false
	 */
	public int getColumnIndex(String name, Boolean silent) throws Exception {
		int column = this.ResultSet.getColumnIndex(name);
		if (column == -1 && !(silent))
			throw new Exception("Field " + name + " does not exist");
		return column;
	}

	/**
	 * Returns the column name for the specified column index.<br>
	 * <br>
	 * Throws an Exception in case the specified column index doesn't exist.
	 * 
	 * @param column The column index
	 * @return name The column's name 
	 * 
	 * @throws Exception Gets thrown in case the specified column index doesn't exist
	 */
	public String getColumnName(int column) throws Exception {
		return getColumnName(column, false);
	}

	/**
	 * Returns the column name for the specified column index.<br>
	 * <br>
	 * In case the specified column index doesn't exist, depending on the specified boolean value
	 * either an Exception is thrown, or not. In fact, this boolean value indicates whether the Exception 
	 * should be suppressed or not. 
	 * 
	 * @param column The column index
	 * @param silent Boolean value indicating whether eventual Exception should be suppressed or not
	 * @return name The column's name 
	 * 
	 * @throws Exception Gets thrown in case the specified column index doesn't exist and the silent boolean value is false
	 */
	public String getColumnName(int column, Boolean silent) throws Exception {
		if ((this.ResultSet.getColumnNames().isEmpty() || column < 0
				|| column >= this.ResultSet.getColumnNames().size()) && !(silent))
			throw new Exception("Column " + String.valueOf(column) + " does not exist");
		return this.ResultSet.getColumnNames().get(column);
	}

	public int getFieldType(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnType(column);
	}

	public String getFieldTypeName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnTypeName(column);
	}

	public int getFieldDisplaySize(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnDisplaySize(column);
	}

	public String getFieldCatalogName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getCatalogName(column);
	}

	public String getFieldClassName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnClassName(column);
	}

	public int getColumnCount() throws Exception {
		return this.ResultSet.getColumnCount();
	}

	public String getFieldLabel(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnLabel(column);
	}

	public int getFieldPrecision(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getPrecision(column);
	}

	public String getFieldSchemaName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getSchemaName(column);
	}

	public String getFieldTableName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getTableName(column);
	}

	public void setFieldAttribute(String name, String attrname, String value) throws Exception {
		DataField field = getField(name);
		field.setAttribute(attrname, value);
	}

	public String getFieldAttribute(String name, String attrname) throws Exception {
		DataField field = getField(name);
		String attr = field.getAttribute(attrname);
		if (attr == null)
			attr = "";
		return attr;
	}

	public HashMap<String, String> getFieldAttributes(String name) throws Exception {
		DataField field = getField(name);
		return field.getAttributes();
	}

	public void removeFieldAttribute(String name, String attrname) throws Exception {
		DataField field = getField(name);
		field.removeAttribute(attrname);
	}

	public void removeField(String name) throws Exception {
		int column = getColumnIndex(name);
		this.ResultSet.removeColumn(column);
		this.DataFields.remove(name);
	}

	public ArrayList<String> getAttributeForFields(String attrname) {
		return this.getAttributeForFields(attrname, false);
	}

	/**
	 * Method getAttributeForFields: Get all attributes for a specific attribute
	 * name
	 *
	 * @param attrname 
	 * 				the name of the attribute
	 * @param defaultToFieldname
	 * 				if 1, return the field name if attribute
	 *            	value is empty
	 * @return ArrayList list: a list of field attributes for the given
	 *         attribute name
	 */
	public ArrayList<String> getAttributeForFields(String attrname, Boolean defaultToFieldname) {
		DataField field;
		ArrayList<String> ret = new ArrayList<String>();
		if (this.ResultSet.getColumnNames().size() > 0) {
			Iterator<String> it = this.ResultSet.getColumnNames().iterator();
			while (it.hasNext()) {
				String n = it.next();
				field = this.DataFields.get(n);
				String r = field.getAttribute(attrname);
				if (r == "" && defaultToFieldname)
					r = n;
				ret.add(r);
			}
		}
		return ret;
	}

	/**
	 * Method replaceFields: Search and replace all the field names in a given
	 * string (formula) with the field value. The field name should be escaped
	 * as "F{name}".
	 *
	 * @param formula the string with the escaped field names
	 * @return String formula: the replaced string
	 */
	public String replaceFields(String formula) throws Exception {
		return replaceFields(formula, false);
	}

	public String replaceFields(String formula, Boolean fCleanRemainingPlaceholders) throws Exception {
		Set<String> ks = this.DataFields.keySet();
		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String k = it.next();
			String k1 = "$F{" + k + "}";

			formula = formula.replace(k1, this.getFieldAsString(k));
		}
		if (fCleanRemainingPlaceholders) {
			formula = formula.replaceAll("\\$F\\{\\S*\\}", "");
		}
		return formula;
	}

	public Boolean equals(DataRow dr) throws Exception {
		Boolean eq = true;
		BBArrayList fields = dr.getFieldNames();
		if (fields.size() != this.DataFields.size())
			eq = false;
		else {
			@SuppressWarnings("unchecked")
			Iterator<String> it = fields.iterator();
			while (it.hasNext()) {
				String name = it.next();
				DataField f = this.DataFields.get(name);
				if (f == null || !dr.getFieldAsString(name).equals(this.getFieldAsString(name))) {
					eq = false;
					break;
				}
			}
		}
		return eq;
	}

	public void addDataField(String name, DataField field) throws Exception {
		Object o = field.getObject(); // default
		int type;
		String typeName = o.getClass().getCanonicalName();
		if (typeName != null && typeName.startsWith("[")) {
			if (typeName.contains("byte"))
				type = java.sql.Types.ARRAY;
			else
				type = java.sql.Types.VARBINARY;
		} else {
			switch (typeName) {
			case "java.lang.String":
				type = java.sql.Types.VARCHAR;
				break;
			case "byte":
			case "java.lang.Byte":
				type = java.sql.Types.TINYINT;
				break;
			case "long":
			case "java.lang.Long":
			case "java.math.BigInteger":
				type = java.sql.Types.BIGINT;
				break;
			case "short":
			case "java.lang.Short":
				type = java.sql.Types.SMALLINT;
				break;
			case "boolean":
			case "java.lang.Boolean":
				type = java.sql.Types.BOOLEAN;
				break;
			case "char":
			case "java.lang.Character":
				type = java.sql.Types.CHAR;
				break;
			case "double":
			case "java.lang.Double":
				type = java.sql.Types.DOUBLE;
				break;
			case "float":
			case "java.lang.Float":
				type = java.sql.Types.REAL;
				break;
			case "int":
			case "java.lang.Integer":
				type = java.sql.Types.INTEGER;
				break;
			case "java.math.BigDecimal":
				type = java.sql.Types.NUMERIC;
				break;
			case "java.sql.Date":
				type = java.sql.Types.DATE;
				break;
			case "java.sql.Time":
				type = java.sql.Types.TIME;
				break;
			case "java.sql.Timestamp":
				type = java.sql.Types.TIMESTAMP;
				break;
			case "java.sql.Blob":
				type = java.sql.Types.BLOB;
				break;
			case "java.sql.Clob":
				type = java.sql.Types.CLOB;
				break;
			case "java.sql.Array":
				type = java.sql.Types.ARRAY;
				break;
			case "java.sql.Struct":
				type = java.sql.Types.STRUCT;
				break;
			case "java.sql.Ref":
				type = java.sql.Types.REF;
				break;
			default:
				type = java.sql.Types.OTHER;
				break;
			}
		}
		addDataField(name, type, field);
	}

	public void addDataField(String name, int type, DataField field) throws Exception {

		if (this.ResultSet.getColumnIndex(name) == -1) {
			int column = this.ResultSet.addColumn(name);
			this.ResultSet.setColumnType(column, type);
		}
		this.DataFields.put(name, field);
	}

	public DataField getDataField(String name) {
		return this.DataFields.get(name);
	}

	public DataRow clone() {
		DataRow dr = new DataRow();
		Iterator<String> it = this.ResultSet.getColumnNames().iterator();
		while (it.hasNext()) {
			String k = it.next();
			DataField f = this.DataFields.get(k);
			DataField f1 = f.clone();
			try {
				dr.addDataField(k, this.getFieldType(k), f1);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dr;
	}

	public String getInsertStatement() {
		Set<String> ks = this.DataFields.keySet();
		Iterator<String> it = ks.iterator();
		String f1 = "";
		String v = "";
		String sql;
		while (it.hasNext()) {
			String k = it.next();
			try {
				if (isFieldNull(k))
					continue;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			f1 = f1 + "," + k;
			try {
				v = v + "," + getFieldForSQL(k);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sql = "(" + f1.substring(1) + ") VALUES (" + v.substring(1) + ")";
		return sql;
	}

	public String getUpdateStatement() {
		Set<String> ks = this.DataFields.keySet();
		Iterator<String> it = ks.iterator();
		String sql = "";
		while (it.hasNext()) {
			String k = it.next();
			try {
				if (isFieldNull(k))
					continue;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				sql = sql + ", " + k + "=" + getFieldForSQL(k);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (sql.length() > 0)
			sql = sql.substring(1);
		return sql;
	}

	public java.util.HashMap<String, Object> getObjects() {
		HashMap<String, Object> hm = new HashMap<String, Object>();
		Set<String> ks = this.DataFields.keySet();
		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String k = it.next();
			try {
				if (isFieldNull(k))
					continue;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				hm.put(k, getField(k));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hm;
	}

	public void mergeRecord(DataRow dr) {
		BBArrayList names = dr.getFieldNames();
		@SuppressWarnings("unchecked")
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String f = it.next();
			try {
				this.addDataField(f, dr.getFieldType(f), dr.getDataField(f));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates a DataRow from a String in URL format
	 * Sample: field1=value1&field2=value2 
	 * will result in a DataRow with the two fields field1 and field2
	 * holding the according values
	 * 
	 * @param in: the URL formatted code 
	 * @return a new DataRow from the String
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public static DataRow fromURL(String in) throws Exception {
		DataRow r = new DataRow();
		if (in.length() <3 )
			return r;
		
		List<String> Params = Arrays.asList(in.split("&"));
		Iterator<String> it = Params.iterator();
		while (it.hasNext()){
			String arg = it.next();
			String[] pair = arg.split("=");
			if (pair.length > 0){
				String key	 = java.net.URLDecoder.decode(pair[0]);
				String value = new String();
				if (pair.length >1)
					value= java.net.URLDecoder.decode(pair[1]);
				r.setFieldValue(key, value);
			}
		}
		
		return r;
		
	}

	/**
	 * Creates a URL-encoded String out of the DataRow
	 * 
	 * @return the URL encoded String 
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	public String toURL() throws Exception {
		
		StringBuilder ret = new StringBuilder();
		ArrayList<String> fields = this.ResultSet.getColumnNames();
		Iterator<String> it = fields.iterator();
		while (it.hasNext()){
			String f = it.next();
			String v = getFieldAsString(f);
			if (ret.length()>0)
				ret.append("&");

			ret.append(java.net.URLEncoder.encode(f));
			ret.append("=");
			ret.append(java.net.URLEncoder.encode(v));

		}
			
		return ret.toString();
	}
	
	public String toJson() throws Exception {
		ResultSet rs = new ResultSet();
		rs.add(this);
		return rs.toJson();

	}

	public static DataRow fromJson(String in) throws Exception {

		if (in.length() <2 )
			return new DataRow();
		
		// convert characters below chr(32) to \\uxxxx notation
		int i=0;
		while (i<in.length()){
			if (in.charAt(i) <31){
				String hex = String.format("%04x", (int) in.charAt(i));
				in=in.substring(0,i)+"\\u"+hex+in.substring(i+1);
				System.out.println(i+ "-" +hex);
			}
			i++;
		}
		
		
		if (in.startsWith("{\"datarow\":[") && in.endsWith("]}")) {
			in = in.substring(11, in.length() - 1);
		}
		if (in.startsWith("{") && in.endsWith("}"))
			in = "[" + in + "]";
		JsonFactory f = new JsonFactory();
		@SuppressWarnings("deprecation")
		JsonParser jp = f.createJsonParser(in);
		jp.nextToken();
		ObjectMapper objectMapper = new ObjectMapper();
		@SuppressWarnings("rawtypes")
		List navigation = objectMapper.readValue(jp,
				objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));

		if (navigation.size()==0)
			return new DataRow();
		HashMap<?, ?> hm = (HashMap<?, ?>) navigation.get(0);

		DataRow dr = new DataRow();

		if (hm.containsKey("meta")) {
			// new format

			@SuppressWarnings("rawtypes")
			HashMap meta = (HashMap) hm.get("meta");
			@SuppressWarnings("rawtypes")
			Iterator it = meta.keySet().iterator();

			while (it.hasNext()) {
				String fieldName = (String) it.next();
				@SuppressWarnings({ "rawtypes", "unchecked" })
				HashMap<String, ?> fieldMeta = ((HashMap) meta.get(fieldName));
				Object fieldObj = hm.get(fieldName);
				if (fieldObj == null)
					continue;
				int fieldType = Integer.parseInt((String) fieldMeta.get("ColumnType"));
				switch (fieldType) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					dr.addDataField(fieldName, fieldType, new DataField(fieldObj));
					break;

				case java.sql.Types.BIGINT:
				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
					String tmp = fieldObj.toString();
					if (tmp.isEmpty())
						tmp="0";
					dr.addDataField(fieldName, fieldType, new DataField(Integer.parseInt(tmp)));
					break;

				case java.sql.Types.NUMERIC:
				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
				case java.sql.Types.DECIMAL:
				case java.sql.Types.REAL:
					dr.addDataField(fieldName, fieldType, new DataField(Double.parseDouble(fieldObj.toString())));
					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					dr.addDataField(fieldName, fieldType, new DataField(fieldObj));
					break;

				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
				case (int) 11:
					String tss = fieldObj.toString();
					try{
					java.sql.Timestamp ts = java.sql.Timestamp
							.valueOf(tss.substring(0, 10) + " " + tss.substring(11, 19));
					dr.addDataField(fieldName, fieldType, new DataField(ts));
					}
					catch (Exception e) {
						dr.addDataField(fieldName, fieldType, new DataField(new java.sql.Timestamp(0)));
					}
					finally {
						
					}
					break;

				case java.sql.Types.DATE:
				case (int) 9:
					tss = fieldObj.toString();
				java.sql.Date ds=null;
					if (tss.length()>9){
						ds = java.sql.Date.valueOf(tss.substring(0, 10));
					}
					dr.addDataField(fieldName, fieldType, new DataField(ds));
					
					break;

				case java.sql.Types.ARRAY:
				case java.sql.Types.BINARY:
				case java.sql.Types.BLOB:
				case java.sql.Types.CLOB:
				case java.sql.Types.DATALINK:
				case java.sql.Types.DISTINCT:
				case java.sql.Types.JAVA_OBJECT:
				case java.sql.Types.LONGVARBINARY:
				case java.sql.Types.NCLOB:
				case java.sql.Types.NULL:
				case java.sql.Types.OTHER:
				case java.sql.Types.REF:
				case java.sql.Types.REF_CURSOR:
				case java.sql.Types.ROWID:
				case java.sql.Types.SQLXML:
				case java.sql.Types.STRUCT:
				case java.sql.Types.TIME:
				case java.sql.Types.TIME_WITH_TIMEZONE:
				case java.sql.Types.VARBINARY:
				default:
					break;

				}// switch

				Set<String> ks = fieldMeta.keySet();
				if (ks.size() > 1) {
					Iterator<String> itm = ks.iterator();
					while (itm.hasNext()) {
						String k = (String) itm.next();
						if (k.equals("ColumnType"))
							continue;

						dr.setFieldAttribute(fieldName, k, (String) fieldMeta.get(k));
					}
				}

			}

		} else {
			// old format - deprecated
			@SuppressWarnings("rawtypes")
			Iterator it = navigation.iterator();
			while (it.hasNext()) {
				@SuppressWarnings("unchecked")
				HashMap<String, ?> field = (HashMap<String, ?>) it.next();
				String name = (String) field.get("Name");
				String type = (String) field.get("Type");
				if (type==null)
					continue;
				switch (type) {
				case "C":
					String strval = (String) field.get("StringValue");
					if (strval == null)
						strval = "";
					dr.setFieldValue(name, strval);
					break;
				case "N":
					Object o = field.get("NumericValue");
					Double numval;
					if (o == null)
						numval = 0.0;
					else
						numval = Double.parseDouble(o.toString());
					dr.setFieldValue(name, numval);
					break;
				default:
					break;
				}

			}

		}
		return dr;
	}

	public Object toJsonElement() {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		try {
			com.google.gson.JsonArray o = parser.parse(this.toJson()).getAsJsonArray();
			return o;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return null;
	}

	/**
	 * @param keydata
	 *            the key segment data as byte array to append to the row key
	 */
	public void addBytesToRowKey(byte[] keydata) {
		byte[] b = new byte[this.RowKey.length + keydata.length];
		System.arraycopy(this.RowKey, 0, b, 0, this.RowKey.length);
		System.arraycopy(keydata, 0, b, this.RowKey.length, keydata.length);
		this.RowKey = b;
	}

	/**
	 * @param keydata
	 *            the key segment data as string of bytes to append to the row
	 *            key
	 */
	public void addToRowKey(String keydata) {
		addBytesToRowKey(keydata.getBytes());
	}

	/**
	 * @return the row key as string of bytes
	 */
	public String getRowKey() {
		return new String(this.RowKey);
	}

	/**
	 * @param rowKey
	 *            the key value as string of bytes to set
	 */
	public void setRowKey(String rowKey) {
		this.RowKey = rowKey.getBytes();
	}


	/**
	 * Resolve any [[CONSTANT]] type of string inside all String fields
	 * note: if the CONSTANT contains "!CLEAR" the field will be removed from the 
	 * DataRow (like an STBL gets cleared)
	 * @param cr an instance of the ConstantsResolver class that holds the constants 
	 * @return: a new object with resolved String constants
	 */
	public DataRow resolveConstants(ConstantsResolver cr) {
		return resolveConstants(cr, false);
	}


	/**
	 * Resolve any [[CONSTANT]] type of string inside all String fields
	 * note: if the CONSTANT contains "!CLEAR" the field will be removed from the
	 * DataRow (like an STBL gets cleared)
	 * @param cr an instance of the ConstantsResolver class that holds the constants
	 * @param removeUnsetFields if true all fields which could not be resolved will be removed
	 * @return: a new object with resolved String constants
	 */
	public DataRow resolveConstants(ConstantsResolver cr, boolean removeUnsetFields) {
		DataRow n = this.clone();
		@SuppressWarnings("rawtypes")
		Iterator it = n.getFieldNames().iterator();
		while (it.hasNext()){
			String f = (String) it.next();
				try {
					if (n.getFieldType(f) ==12){
						n.setFieldValue(f, cr.resolveConstants(n.getField(f).getString()));
						if ((removeUnsetFields && n.getField(f).getString().matches("^\\[\\[.*\\]\\]$")) || n.getField(f).getString().equals("!CLEAR"))
							n.removeField(f);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return n;
	}


	public void clear() {

		@SuppressWarnings("unchecked")
		Iterator<String> it = this.getFieldNames().iterator();
		while (it.hasNext()){
			try {
				this.getField(it.next()).clear();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
