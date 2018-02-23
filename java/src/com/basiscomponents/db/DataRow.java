package com.basiscomponents.db;

import java.sql.Time;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.basis.bbj.datatypes.TemplatedString;
import com.basis.util.common.BasisNumber;
import com.basis.util.common.TemplateInfo;
import com.basiscomponents.db.constants.ConstantsResolver;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A DataRow is a container object with key/value pairs.
 * Each key being a String and each value being a com.basiscomponents.db.DataField object.
 */
public class DataRow implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private HashMap<String, DataField> DataFields = new HashMap<String, DataField>();

	private HashMap<String, String> Attributes = new HashMap<String, String>();

	private com.basiscomponents.db.ResultSet ResultSet; // containing this row

	private byte[] RowKey = new byte[0];

	private int RowID;

	/**
	 * Instantiates a new DataRow object.
	 */
	public DataRow() {
		this.ResultSet = new com.basiscomponents.db.ResultSet();
	}

	/**
	 * Instantiates a new DataRow object and sets the column metadata to
	 * the given ResultSet object's column metadata.
	 *
	 * @param resultSet The ResultSet whose column metadata will be used for this new DataRow
	 */
	public DataRow(com.basiscomponents.db.ResultSet resultSet) {
		this.ResultSet = resultSet;
	}

	/**
	 * Returns a new instance of a DataRow object.
	 *
	 * @return dataRow A newly instantiated DataRow object.
	 */
	public static DataRow newInstance() {
		return new DataRow();
	}

	/**
	 * Returns a new instance of a DataRow object which has been initialized with the column
	 * metadata from the given ResultSet object.
	 *
	 * @param resultSet The ResultSet object whose metadata will be used for the new DataRow.
	 *
	 * @return dataRow The newly instantiated DataRow object.
	 */
	public static DataRow newInstance(com.basiscomponents.db.ResultSet resultSet) {
		return new DataRow(resultSet);
	}

	/**
	 * Creates a new DataRow object by parsing the specified HashMap object
	 * and DataFields for each of the HashMap's entries.
	 *
	 * @param map HashMap containing key/value pairs used to initialize the DataRow object
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
	 * Sets the row ID of this DataRow object to the given value.
	 *
	 * @param rowId The new row ID value of this DataRow object
	 */
	public void setRowID(int rowId) {
		RowID = rowId;
	}

	/**
	 * Returns a {@link com.basiscomponents.db.BBArrayList BBArrayList} object with all field names defined in this DataRow object.
	 *
	 * @return list The BBArrayList<String> containing all field names defined in this DataRow object
	 */
	public BBArrayList<String> getFieldNames() {
		return new BBArrayList<String>(this.ResultSet.getColumnNames());
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
	 * Sets the attribute with the specified name and value for this DataRow object.
	 *
	 * @param name The attribute's name
	 * @param value The attribute's value
	 */
	public void setAttribute(String name, String value) {
		this.Attributes.put(name, value);
	}

	/**
	 * Returns the attribute with the specified name or
	 * null if no attribute with this name exists.
	 *
	 * @param name The attribute's name
	 * @return attribute The attribute or null if no attribute with the given name exists
	 */
	public String getAttribute(String name) {
		return this.Attributes.get(name);
	}

	/**
	 * Returns a clone of this DataRow's attributes.
	 *
	 * @return attributesMap The java.util.HashMap with this DataRow's attributes
	 */
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

	@Override
	public String toString() {
		String x = "";
		Iterator<String> it = this.ResultSet.getColumnNames().iterator();
		while (it.hasNext()) {
			String k = it.next();
			String f = "";
			try {
				f = this.getFieldAsString(k);
			} catch (Exception e) {
				// Auto-generated catch block
				//e.printStackTrace();
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
	 * Sets the specified value for the field with the specified name.
	 * In case no field with the specified name exists, then the field will be created.
	 *
	 * @param name The name of the field
	 * @param value The value of the field
	 * @throws Exception Thrown when field already exists and the new value cannot be casted to the current field type.
	 */
	public void setFieldValue(String name, Object value) throws Exception  {

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
		if (field != null){
			value = DataField.convertType(value, getFieldType(name));
			field.setValue(value);
		}
		else {
			if (value == null) {
				field = new DataField("");
				try {
					addDataField(name, field);
				} catch (Exception e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
				field.setValue(null);
			} else {
				field = new DataField(value);
				try {
					addDataField(name, field);
				} catch (Exception e) {
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Sets the specified value for the field with the specified name.
	 * In case no field with the specified name exists, then the field will be created with the specified SQL type.
	 *
	 * @param name The name of the field
	 * @param type The SQL type of the field
	 * @param value The value of the field
	 */
	public void setFieldValue(String name, int type, Object value) throws Exception {
		DataField field = null;

		String c = "";

		value = DataField.convertType(value, type);
		if (value != null) c = value.getClass().getCanonicalName();

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
	 * Returns the DataField object for the specified field name.<br>
	 * <br>
	 * Throws an Exception if no field with the specified name exists.
	 *
	 * @param name The field's name
	 * @return dataField The DataField object
	 *
	 * @throws Exception No field with the specified name exists
	 */
	public DataField getField(String name) throws Exception {
		return getField(name, false);
	}

	/**
	 * Returns the DataField object for the specified field name.
	 *
	 * In case no field with the specified field name exists, depending on the specified boolean value,
	 * either an Exception is thrown or not. In fact, this boolean value indicates whether the Exception
	 * should be suppressed or not.
	 *
	 * @param name The field name
	 * @param silent Boolean value indicating whether the eventual exception should be suppressed or not
	 * @return dataField The DataField
	 *
	 * @throws Exception No field with the specified name exists and the specified boolean value is false
	 */
	public DataField getField(String name, Boolean silent) throws Exception {
		DataField field = this.DataFields.get(name);
		if (field == null && !(silent))
			throw new Exception("Field " + name + " does not exist");
		return field;
	}

	/**
	 * Returns the value of the field with the specified name as a java.lang.Object.
	 *
	 * @param name The name of the field
	 * @return value The field's value as an Object
	 *
	 * @throws Exception No field with the specified name exists.
	 */
	public Object getFieldValue(String name) throws Exception {
		DataField field = getField(name);
		return field.getValue();
	}

	/**
	 * Returns the string value of the field matching the given field name
	 * or an empty string in case the field's value has not been set or is null.<br>
	 * <br>
	 * Throws an Exception if the given field name does not exist.
	 *
	 * @param name The name of the field.
	 * @return fieldValue The String representation of the field's value.
	 *
	 * @throws Exception No field with the specified name exists.
	 */
	public String getFieldAsString(String name) throws Exception {
		if (isFieldNull(name))
			return "";
		DataField field = getField(name);
		return field.getString();
	}

	/**
	 * Returns true if the DataRow has no fields, otherwise false.<br>
	 *
	 * @return isEmpty True if the DataRow has no fields, false otherwise.
	 */
	public boolean isEmpty() {
		return this.ResultSet.getColumnNames().isEmpty();
	}

	/**
	 * Returns true if the value of the field with the given name is null, false otherwise.<br>
	 * <br>
	 * Throws an Exception in case the field with the given name doesn't exist.
	 *
	 * @param name The name of the field
	 * @return isNull True in case the value of the field is null, false otherwise.
	 *
	 * @throws Exception No field with the specified name exists.
	 */
	public Boolean isFieldNull(String name) throws Exception {
		DataField field = getField(name);
		return (field.getValue() == null);
	}

	/**
	 * Returns the value of the field with the given name as a String for usage in SQL statements.
	 * The field's value will be returned in single quotes if it has a value, otherwise NULL will be returned.<br>
	 * <br>
	 *
	 * @param name The name of the field
	 * @return fieldValue The field's value as a String for usage in SQL statements
	 *
	 * @throws Exception No field with the specified name exists.
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

	/**
	 * Returns the value of the field with given name as a Double.
	 *
	 * @param fieldName The name of the field whose value should be returned as Double
	 *
	 * @return value The field's value as a Double
	 *
	 * @throws Exception No field exists with the given name
	 */
	@SuppressWarnings("deprecation")
	public Double getFieldAsNumber(String fieldName) throws Exception {
		DataField field = getField(fieldName);
		if (ResultSet == null)
			throw new Exception("ResultSet does not exist");

		int column = this.ResultSet.getColumnIndex(fieldName);
		int type = this.ResultSet.getColumnType(column);

		if (field.getValue() == null) {
			if (type == java.sql.Types.DATE || type == java.sql.Types.TIMESTAMP || type == java.sql.Types.TIMESTAMP_WITH_TIMEZONE)
				return -1d;
			else
				return 0.0;
		}

		Double ret = 0.0;

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
			/* 
			 * Columns with an unsigned numeric type in MySQL are treated as the next 'larger' Java type that the signed variant of the MySQL:
			 * http://www.mysqlab.net/knowledge/kb/detail/topic/java/id/4929
			 * 
			 * In the populate method, the value of an unsigned integer is stored as java.lang.Long in the DataField although its
			 * type remains java.sql.Types.INTEGER in the Column metadata. Calling the getInt() method will then result in an Exception.
			 * This checks prevents this Exception.
			 */
			if(!this.ResultSet.isSigned(column)){
				ret = field.getLong().doubleValue();
			}else{
				ret = field.getInt().doubleValue();
			}
			break;
		case java.sql.Types.BIGINT:
			/* 
			 * Columns with an unsigned numeric type in MySQL are treated as the next 'larger' Java type that the signed variant of the MySQL:
			 * http://www.mysqlab.net/knowledge/kb/detail/topic/java/id/4929
			 * 
			 * In the populate method, the value of an unsigned big integer is stored as java.math.BigInteger in the DataField although its
			 * type remains java.sql.Types.BIGINT in the Column metadata. Calling the getLong() method will then result in an Exception.
			 * This checks prevents this Exception.
			 */
			if(!this.ResultSet.isSigned(column)){
				ret = ((java.math.BigInteger) field.getValue()).doubleValue();
			}else{
				ret = field.getLong().doubleValue();
			}
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
			ret = field.getInt().doubleValue();
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
	 * @throws Exception The specified column name doesn't exist
	 */
	public int getColumnIndex(String name) throws Exception {
		return getColumnIndex(name, false);
	}

	/**
	 * Returns the index of the column with the specified name.<br>
	 * <br>
	 * In case the specified column name doesn't exist, depending on the specified boolean value,
	 * either an Exception is thrown or not. In fact, this boolean value indicates whether the Exception
	 * should be suppressed or not.
	 *
	 * @param name The name of the column
	 * @param silent Boolean value indicating whether an eventual Exception should be suppressed or not
	 * @return index The column's index
	 *
	 * @throws Exception The specified column name doesn't exist and the silent boolean value is false
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
	 * @throws Exception The specified column index doesn't exist
	 */
	public String getColumnName(int column) throws Exception {
		return getColumnName(column, false);
	}

	/**
	 * Returns the column name for the specified column index.<br>
	 * <br>
	 * In case the specified column index doesn't exist, depending on the specified boolean value,
	 * either an Exception is thrown, or not. In fact, this boolean value indicates whether the Exception
	 * should be suppressed or not.
	 *
	 * @param column The column index
	 * @param silent Boolean value indicating whether an eventual Exception should be suppressed or not
	 * @return name The column's name
	 *
	 * @throws Exception The specified column index doesn't exist and the silent boolean value is false
	 */
	public String getColumnName(int column, Boolean silent) throws Exception {
		if ((this.ResultSet.getColumnNames().isEmpty() || column < 0
				|| column >= this.ResultSet.getColumnNames().size()) && !(silent))
			throw new Exception("Column " + String.valueOf(column) + " does not exist");
		return this.ResultSet.getColumnNames().get(column);
	}

	/**
	 * Returns the value of the ColumnType property from the metadata for the field with the given name.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the ColumnType property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public int getFieldType(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnType(column);
	}

	/**
	 * Returns the value of the ColumnTypeName property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the ColumnTypeName property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldTypeName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnTypeName(column);
	}

	/**
	 * Returns the value of the DisplaySize property from the metadata for the field with the given name.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the DisplaySize property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public int getFieldDisplaySize(String fieldName) throws Exception {
		int column = getColumnIndex(fieldName);
		return this.ResultSet.getColumnDisplaySize(column);
	}

	/**
	 * Returns the value of the CatalogName property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the CatalogName property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldCatalogName(String fieldName) throws Exception {
		int column = getColumnIndex(fieldName);
		return this.ResultSet.getCatalogName(column);
	}

	/**
	 * Returns the value of the ClassName property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the ClassName property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldClassName(String fieldName) throws Exception {
		int column = getColumnIndex(fieldName);
		return this.ResultSet.getColumnClassName(column);
	}

	/**
	 * Returns the number of columns defined for this DataRow object.
	 *
	 * @return columnCount The number of columns defined for this DataRow object.
	 *
	 * @throws Exception
	 */
	public int getColumnCount() {
		return this.ResultSet.getColumnCount();
	}

	/**
	 * Returns the value of the Label property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the Label property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldLabel(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getColumnLabel(column);
	}

	/**
	 * Returns the value of the Precision property from the metadata for the field with the given name.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the Precision property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public int getFieldPrecision(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getPrecision(column);
	}

	/**
	 * Returns the value of the SchemaName property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the SchemaName property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldSchemaName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getSchemaName(column);
	}

	/**
	 * Returns the value of the TableName property from the metadata for the field with the given name
	 * or an empty string in case the property isn't set.
	 *
	 * @param name The name of the field.
	 *
	 * @return The value of the TableName property for the field name.
	 *
	 * @throws Exception The specified column name doesn't exist
	 */
	public String getFieldTableName(String name) throws Exception {
		int column = getColumnIndex(name);
		return this.ResultSet.getTableName(column);
	}

	/**
	 * Sets the given attribute name and value for the field with the given name.
	 * Overwrites the attribute's value with the specified one in case the attribute already exists.
	 *
	 * @param name The field's name.
	 * @param attrname The name of the attribute to set.
	 * @param value The value of the attribute to set.
	 * @throws Exception No field exists with the given name.
	 */
	public void setFieldAttribute(String name, String attrname, String value) throws Exception {
		DataField field = getField(name);
		field.setAttribute(attrname, value);
	}

	/**
	 * Returns the value of the attribute with the given name
	 * for the field with the given name.
	 *
	 * @param name The field name.
	 * @param attrname The name of the attribute.
	 *
	 * @return The attribute's name.
	 *
	 * @throws Exception The field name doesn't exist.
	 */
	public String getFieldAttribute(String name, String attrname) throws Exception {
		DataField field = getField(name);
		String attr = field.getAttribute(attrname);
		if (attr == null)
			attr = "";
		return attr;
	}

	/**
	 * Returns a HashMap with the attributes of the field with the given name.
	 * The HashMap keys will be the attribute names and the HashMap values will be
	 * the attribute values.
	 *
	 * @param name The name of the field.
	 *
	 * @return A HashMap with the field's attributesas key/value pairs.
	 *
	 * @throws Exception No field exists with the given name
	 */
	public HashMap<String, String> getFieldAttributes(String name) throws Exception {
		DataField field = getField(name);
		return field.getAttributes();
	}

	/**
	 * Removes the attribute with the given name from the field's attributes.
	 *
	 * @param name The name of the field.
	 * @param attrname The name of the attribute.
	 *
	 * @throws Exception No field exists with the given name.
	 */
	public void removeFieldAttribute(String name, String attrname) throws Exception {
		DataField field = getField(name);
		field.removeAttribute(attrname);
	}

	/**
	 * Removes the field with the given name from the list of fields.
	 *
	 * @param fieldName The name of the field to remove.
	 *
	 * @throws Exception The field name doesn't exist.
	 */
	public void removeField(String fieldName) throws Exception {

		int column = getColumnIndex(fieldName,true);
		if (column>-1)
			this.ResultSet.removeColumn(column);

		this.DataFields.remove(fieldName);

	}

	/**
	 * Returns a list containing all the attribute values for the given attribute name
	 * for each field defined in this DataRow.
	 * <br><br>
	 * <b>Note: </b> The method also includes null values in case the attribute is not defined for a field.
	 *
	 * @see #getAttributeForFields(String, Boolean)
	 *
	 * @param attrname The name of the attribute
	 *
	 * @return A list with the attribute values for the given attribute name.
	 */
	public ArrayList<String> getAttributeForFields(String attrname) {
		return this.getAttributeForFields(attrname, false);
	}

	/**
	 * Returns a list containing all the attribute values for the given attribute name,
	 * for each field defined in this DataRow.
	 * <br><br>
	 * If the defaultToFieldname value is true, the method doesn't add <code>null</code> nor empty Strings
	 * to the list, instead the field's name will be added.
	 *
	 * @param attrname The name of the attribute.
	 *
	 * @param defaultToFieldname The boolean value indicating whether to add <code>null</code> or empty Strings
	 * 							 to the list or not.
	 *
	 * @return The list with the attribute values of each field defined in this DataRow for the given attribute name.
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

	/**
	 * Compares the given DataRow object to the current one, and returns <code>true</code>
	 * in case they are equal, <code>false</code> otherwise.
	 *
	 * @param dataRow The DataRow object to compare with
	 *
	 * @return equal The boolean value indicating whether the DataRows are equal or not
	 */
	public Boolean equals(DataRow dataRow) {
		if (dataRow == null)
			return false;

		Boolean eq = true;
		BBArrayList<String> fields = dataRow.getFieldNames();
		if (fields.size() != this.DataFields.size())
			eq = false;
		else {
			Iterator<String> it = fields.iterator();
			while (it.hasNext()) {
				String name = it.next();
				DataField f = this.DataFields.get(name);
				try {
					if (f == null || !dataRow.getFieldAsString(name).equals(this.getFieldAsString(name))) {
						eq = false;
						break;
					}
				}
				catch (Exception ex) {
					return false;
				}
			}
		}
		return eq;
	}

	/**
	 * Adds a field with the given name and the given value(DataField) to the list of
	 * fields. The methods analyzes the DataField's value in order to determine its
	 * SQL Type.
	 * <br>
	 * <br>
	 * <b>Note: </b> In case a field with the same name does already exist, it will be overwritten.
	 *
	 * @see #addDataField(String fieldName, int sqlType, DataField dataField)
	 *
	 * @param fieldName The name of the field to add
	 * @param dataField The field's value
	 *
	 * @throws Exception
	 */
	public void addDataField(String fieldName, DataField dataField) throws Exception {
		Object o = dataField.getObject(); // default
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
		addDataField(fieldName, type, dataField);
	}

	/**
	 * Adds a field with the given name, the given SQL Type and the given value(DataField) to the list of
	 * fields.
	 * <br>
	 * <br>
	 * <b>Note: </b> In case a field with the same name does already exist, it will be overwritten.
	 *
	 * @param fieldName The name of the field to add
	 * @param sqlType The SQL Type of the field
	 * @param dataField The field's value
	 *
	 * @throws Exception
	 */
	public void addDataField(String fieldName, int sqlType, DataField dataField) throws Exception {

  	  	// deal unknown types as String
//  	  	if (sqlType==-1)
//  	  		sqlType=12;
// -1 is LONGVARCHAR in sql types!

  	  	if (this.ResultSet.getColumnIndex(fieldName) == -1) {
			int column = this.ResultSet.addColumn(fieldName);
			this.ResultSet.setColumnType(column, sqlType);
		}
		this.DataFields.put(fieldName, dataField);
	}

	/**
	 * Returns the DataField object for the given field name.
	 * Returns <code>null</code> in case no field exists with the given name.
	 *
	 * @param fieldName The name of the field7
	 *
	 * @return dataField The DataField value for the given field name.
	 */
	public DataField getDataField(String fieldName) {
		return this.DataFields.get(fieldName);
	}

	@Override
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
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		return dr;
	}

	/**
	 * Returns a SQL Insert statement based on the DataRow fields.
	 * The method does only return the part to add after the VALUES in the statement.
	 *
	 * @return insertStatement The insert statement created based on the DataRow fields.
	 */
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
				// Auto-generated catch block
				e.printStackTrace();
			}
			f1 = f1 + "," + k;
			try {
				v = v + "," + getFieldForSQL(k);
			} catch (Exception e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		sql = "(" + f1.substring(1) + ") VALUES (" + v.substring(1) + ")";
		return sql;
	}

	/**
	 * Returns a SQL Update statement based on the DataRow fields.
	 * The method does only return the part to add after the SET in the statement.
	 *
	 * @return updateStatement The update statement created based on the DataRow fields.
	 */
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
				// Auto-generated catch block
				e.printStackTrace();
			}
			try {
				sql = sql + ", " + k + "=" + getFieldForSQL(k);
			} catch (Exception e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (sql.length() > 0)
			sql = sql.substring(1);
		return sql;
	}

	/**
	 * Returns a <code>java.util.HashMap&lt;String,Object&gt;</code> object with the DataRow's field's
	 * as key value pairs.
	 *
	 * @return fieldMap The DataRow's fields as <code>java.util.HashMap&lt;String,Object&gt;</code> object.
	 */
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
				// Auto-generated catch block
				e.printStackTrace();
			}
			try {
				hm.put(k, getField(k));
			} catch (Exception e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		return hm;
	}

	/**
	 * Merges this DataRow object with the given one by adding all fields
	 * of the given DataRow object to the current one. <br>
	 * <br>
	 * <b>Note:</b> The DataRow's field values will be overwritten
	 * by the values of the passed DataRow in case the field's exist in both.
	 *
	 * @param dataRow The DataRow object to merge with
	 */
	public void mergeRecord(DataRow dataRow) {
		BBArrayList<String> names = dataRow.getFieldNames();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String f = it.next();
			try {
				this.addDataField(f, dataRow.getFieldType(f), dataRow.getDataField(f));
			} catch (Exception e) {
				// Auto-generated catch block
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
	 * @param in the URL formatted code
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

	/**
	 * Returns the DataRow and all of its fields as a JSON String.
	 *
	 * @return The DataRow as JSON String
	 * @throws Exception
	 */
	public String toJson() throws Exception {
		return toJson(true);
	}

	/**
	 * Returns the DataRow and all of its fields as a JSON String.
	 *
	 * @return The DataRow as JSON String
	 * @throws Exception
	 */
	public String toJson(Boolean f_meta) throws Exception {
		ResultSet rs = new ResultSet();
		rs.add(this);
		return rs.toJson(f_meta);
	}

	/**
	 * Initializes and returns a DataRow object based on the values provided in the given JSON String.
	 *
	 * @param in The JSON String
	 * @return the DataRow object created based on the JSOn String's content
	 *
	 * @throws Exception Gets thrown in case the JSON could not be parsed / is invalid
	 */
	public static DataRow fromJson(String j) throws Exception {
		return fromJson(j,null);
	}

	/**
	 * Initializes and returns a DataRow object based on the values provided in the given JSON String.
	 *
	 * @param in The JSON String
	 * @param ar A DataRow that will be used to determine the field types if not given in the meta section of the JSON String
	 * @return the DataRow object created based on the JSOn String's content
	 *
	 * @throws Exception Gets thrown in case the JSON could not be parsed / is invalid
	 */
	@SuppressWarnings("rawtypes")
	public static DataRow fromJson(String in, DataRow ar) throws Exception {

		if (in.length() <2 )
			return new DataRow();

		if (ar == null)
			ar = new DataRow();
		else
			ar = ar.clone();

		// convert characters below chr(32) to \\uxxxx notation
		int i=0;
		while (i<in.length()){
			if (in.charAt(i) <31){
				String hex = String.format("%04x", (int) in.charAt(i));
				in=in.substring(0,i)+"\\u"+hex+in.substring(i+1);
			}
			i++;
		}


		if (in.startsWith("{\"datarow\":[") && in.endsWith("]}")) {
			in = in.substring(11, in.length() - 1);
		}
		String intmp = in;
		JsonNode root = new ObjectMapper().readTree(intmp);

		if (in.startsWith("{") && in.endsWith("}")){
			in = "[" + in + "]";
		}
		JsonFactory f = new JsonFactory();
		JsonParser jp = f.createParser(in);
		jp.nextToken();
		ObjectMapper objectMapper = new ObjectMapper();

		List navigation = objectMapper.readValue(jp,
				objectMapper.getTypeFactory().constructCollectionType(List.class, Object.class));


		if (navigation.size()==0)
			return new DataRow();

		HashMap<?, ?> hm = (HashMap<?, ?>) navigation.get(0);

		DataRow dr = new DataRow();

		if (hm.containsKey("meta")) {
			// new format
			HashMap meta = (HashMap) hm.get("meta");
			Iterator<?> it = hm.keySet().iterator();
			while (it.hasNext()){
				String fieldName = (String) it.next();
				@SuppressWarnings("unchecked")
				HashMap<String, ?> fieldMeta = ((HashMap) meta.get(fieldName));
				if (!ar.contains(fieldName)  && !fieldName.equals("meta")){
					String s = "12";

					if (fieldMeta == null){
						fieldMeta = new HashMap<>();
					}

					if (fieldMeta.get("ColumnType")!=null)
						s = (String)fieldMeta.get("ColumnType");
					if (s!=null){
						ar.addDataField(fieldName, Integer.parseInt(s), new DataField(null));
						Set<String> ks = fieldMeta.keySet();
						if (ks.size() > 1) {
							Iterator<String> itm = ks.iterator();
							while (itm.hasNext()) {
								String k = (String) itm.next();
								if (k.equals("ColumnType"))
									continue;
								ar.setFieldAttribute((String) fieldName, k, (String) fieldMeta.get(k));
							}
						}
					}//if s!=null
				}
			}
		}

		// add all fields to the attributes record that were not part of it before
		Iterator it2 = hm.keySet().iterator();
		while (it2.hasNext()){
			String fieldName = (String) it2.next();
			if (!ar.contains(fieldName) && !fieldName.equals("meta") && root.get(fieldName) != null){
				switch (root.get(fieldName).getNodeType().toString()){
				case "NUMBER":
					ar.addDataField(fieldName, java.sql.Types.DOUBLE, new DataField(null));
					break;
				case "BOOLEAN":
					ar.addDataField(fieldName, java.sql.Types.BOOLEAN, new DataField(null));
					break;
				default:
					ar.addDataField(fieldName, java.sql.Types.VARCHAR, new DataField(null));
					break;

				}
			}
		}

		if (!ar.isEmpty()){
			BBArrayList<String> names = ar.getFieldNames();

			Iterator it = names.iterator();

			while (it.hasNext()) {
				String fieldName = (String) it.next();

				Object fieldObj = hm.get(fieldName);
				int fieldType = ar.getFieldType(fieldName);
				if (fieldObj == null) {
					dr.addDataField(fieldName, fieldType, new DataField(null));
					dr.setFieldAttributes(fieldName, ar.getFieldAttributes(fieldName));
					continue;
				}
				switch (fieldType) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					//got a JSON object - save it as a JSON String
					if (fieldObj.getClass().equals(java.util.LinkedHashMap.class)) {
						dr.addDataField(fieldName, fieldType, new DataField(root.get(fieldName).toString()));
						dr.setFieldAttribute(fieldName, "StringFormat", "JSON");
					}
					else
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
					dr.addDataField(fieldName, fieldType, new DataField(new java.math.BigDecimal(fieldObj.toString())));
					break;
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
					java.sql.Timestamp ts = (java.sql.Timestamp)DataField.convertType(tss, fieldType);
					dr.addDataField(fieldName, fieldType, new DataField(ts));
					break;
				case java.sql.Types.DATE:
				case (int) 9:
					tss = fieldObj.toString();
					dr.addDataField(fieldName, fieldType, new DataField((java.sql.Date)DataField.convertType(tss, fieldType)));
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

				HashMap<String, String> attr = ar.getFieldAttributes(fieldName);
				@SuppressWarnings("unchecked")
				HashMap<String, HashMap> m = (HashMap<String, HashMap>)hm.get("meta");
				if (m != null && m.containsKey(fieldName)){
					attr.putAll((HashMap<String, String>)m.get(fieldName));
					dr.setFieldAttributes(fieldName, attr);
				}
			}
		} else {
			// old format - deprecated
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
				case "D":
					Object d = field.get("DateValue");
					if (d == null) {
						dr.setFieldValue(name, Types.DATE, null);
					}
					else {
						dr.setFieldValue(name, Types.DATE, d.toString());
					}
					break;
				case "T":
					Object t = field.get("TimestampValue");
					if (t == null)
						dr.setFieldValue(name, Types.TIMESTAMP, null);
					else
						dr.setFieldValue(name, Types.TIMESTAMP, t.toString());
					break;
				default:
					break;
				}
			}
		}
		return dr;
	}

	public void setFieldAttributes(String fieldName, HashMap<String, String> attr) throws Exception {

		Iterator<String> it = attr.keySet().iterator();
		while (it.hasNext()){
			String k = it.next();
			setFieldAttribute(fieldName, k, attr.get(k));
		}
	}

	/**
	 * Returns the DataRow as {@link com.google.gson.JsonArray JsonArray} object.
	 *
	 * @return jsonArray The DataRow as {@link com.google.gson.JsonArray JsonArray} object.
	 */
	public Object toJsonElement() {
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		try {
			com.google.gson.JsonArray o = parser.parse(this.toJson()).getAsJsonArray();
			return o;
		} catch (Exception e) {
			// Auto-generated catch block
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
					// Auto-generated catch block
					e.printStackTrace();
				}
		}
		return n;
	}


	/**
	 * Iterates over each DataField object, and calls the {@link DataField#clear() clear()}
	 * method which sets the DataRow's value to <code>null</code>
	 */
	public void clear() {

		Iterator<String> it = this.getFieldNames().iterator();
		while (it.hasNext()){
			try {
				this.getField(it.next()).clear();
			} catch (Exception e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * Returns a DataRow object with all fields defined in this DataRow which have the given attribute name.
	 * The fields where the attribute is defined but the attribute value is empty or null, will be ignored by this method.
	 *
	 * @see #getFieldsHavingAttribute(String, boolean)
	 * @see #getFieldsHavingAttribute(String, boolean, DataRow)
	 *
	 * @param attributeName The name of the attribute.
	 *
	 * @return The DataRow with the fields where the attribute with the given name is defined.
	 */
	public DataRow getFieldsHavingAttribute(String attributeName){
		return getFieldsHavingAttribute(attributeName, false, null);
	}

	/**
	 * Returns a DataRow object with all fields defined in this DataRow which have the given attribute name.
	 * The given boolean value defines whether to include the fields where the attribute is defined but the attribute value
	 * is empty (null or Empty String). If the value is set to true, these fields are included. If it is set to false, those fields
	 * will be ignored by the method.
	 *
	 * @see #getFieldsHavingAttribute(String)
	 * @see #getFieldsHavingAttribute(String, boolean, DataRow)
	 *
	 * @param attributeName The name of the attribute.
	 * @param includeEmptyValues The boolean value indicating whether to include fields with empty attribute values
	 *
	 * @return The DataRow with the fields where the attribute with the given name is defined.
	 */
	public DataRow getFieldsHavingAttribute(String attributeName, boolean includeEmptyValues){
		return getFieldsHavingAttribute(attributeName, includeEmptyValues, null);
	}

	/**
	 * Returns all fields of this DataRow object being defined in the given DataRow object and also having the given attribute name set.
	 * The given boolean value defines whether to include the fields where the attribute is defined but the attribute value
	 * is empty (null or Empty String). If the value is set to true, these fields are included. If it is set to false, those fields
	 * will be ignored by the method.
	 * 
	 * @see #getFieldsHavingAttribute(String)
	 * @see #getFieldsHavingAttribute(String, boolean)
	 *
	 * @param attributeName The name of the attribute.
	 * @param includeEmptyValues The boolean value indicating whether to include fields with empty attribute values
	 * @param dr The DataRow object's whose fields will be iterated over.
	 *
	 * @return The DataRow with the fields where the attribute with the given name is defined.
	 */
	public DataRow getFieldsHavingAttribute(String attributeName, boolean includeEmptyValues, DataRow dr){
		DataRow dataRow = new DataRow();

		DataField fieldValue;

		if (dr == null) dr = this;
		Iterator<String> it = dr.getFieldNames().iterator();
		while(it.hasNext()){
			String fieldName = it.next();
			if(!this.contains(fieldName)) continue;

			try {
				fieldValue = this.getField(fieldName);
			} catch (Exception e) {
				continue;
			}

			if(fieldValue.getAttributes().containsKey(attributeName)){
				if(!includeEmptyValues){
					if(fieldValue.getAttribute(attributeName) == null || fieldValue.getAttribute(attributeName).isEmpty()){
						continue;
					}
				}

				try {
					dataRow.setFieldValue(fieldName, fieldValue.clone());
				} catch (Exception e) {}
			}
		}

		return dataRow;
	}

	/**
	 * Returns all fields of this DataRow, having the specified attribute name and attribute value.
	 *
	 * @param attributeName The name of the attribute.
	 * @param attributeValue The value of the attribute.
	 *
	 * @return The DataRow with the fields where the attribute with the given name and value is defined.
	 */
	public DataRow getFieldsHavingAttributeValue(String attributeName, String attributeValue){
		return getFieldsHavingAttributeValue(attributeName, attributeValue, null);
	}

	/**
	 * Returns all fields of this DataRow, having the specified attribute name and attribute value.
	 *
	 * @param attributeName The name of the attribute.
	 * @param attributeValue The value of the attribute.
	 * @param dr The DataRow object's whose fields will be iterated over.
	 *
	 * @return The DataRow with the fields where the attribute with the given name and value is defined.
	 */
	public DataRow getFieldsHavingAttributeValue(String attributeName, String attributeValue, DataRow dr){
		DataRow dataRow = new DataRow();

		DataField fieldValue;

		if(dr == null) dr = this;
		Iterator<String> it = dr.getFieldNames().iterator();
		while(it.hasNext()){
			String fieldName = it.next();
			if(!this.contains(fieldName)) continue;

			try {
				fieldValue = this.getField(fieldName);
			} catch (Exception e) {
				continue;
			}

			String value = fieldValue.getAttribute(attributeName);
			if(value != null && value.equals(attributeValue)){
				try {
					dataRow.setFieldValue(fieldName, fieldValue.clone());
				} catch (Exception e) {}
			}
		}

		return dataRow;
	}

	/**
	 * Returns a BBj String Template based on the values defined in this DataRow
	 * 
	 * @see #getString()
	 * 
	 * @return a BBj String Template with the values defined in this DataRow.
	 */
	public String getTemplate(){
		ArrayList<Integer> numericTypeCodeList = new ArrayList<Integer>();
		numericTypeCodeList.add(java.sql.Types.BIGINT);
		numericTypeCodeList.add(java.sql.Types.TINYINT);
		numericTypeCodeList.add(java.sql.Types.INTEGER);
		numericTypeCodeList.add(java.sql.Types.SMALLINT);
		numericTypeCodeList.add(java.sql.Types.NUMERIC);
		numericTypeCodeList.add(java.sql.Types.DOUBLE);
		numericTypeCodeList.add(java.sql.Types.FLOAT);
		numericTypeCodeList.add(java.sql.Types.DECIMAL);
		numericTypeCodeList.add(java.sql.Types.REAL);
		numericTypeCodeList.add(java.sql.Types.BOOLEAN);
		numericTypeCodeList.add(java.sql.Types.BIT);
		numericTypeCodeList.add(java.sql.Types.DATE);
		numericTypeCodeList.add(9); // Basis DATE

		StringBuilder templatedString = new StringBuilder();

		int sqlType;
		int precision;

		int size = this.ResultSet.getColumnCount();
		for(int index=0; index<size ; index++){
			sqlType = ResultSet.getColumnType(index);
			precision = ResultSet.getPrecision(index);

			templatedString.append(ResultSet.getColumnName(index) + ":");

			if(!numericTypeCodeList.contains(sqlType)){
				templatedString.append("C");

				if(sqlType == java.sql.Types.TIMESTAMP || sqlType == java.sql.Types.TIMESTAMP_WITH_TIMEZONE || sqlType == 11){
					precision = 21;
				}
			}else{
				templatedString.append("N");

				if(sqlType == java.sql.Types.BOOLEAN){
					precision = 1;
				}else if(sqlType == 9 || sqlType == java.sql.Types.DATE){
					precision = 9;
				}
			}

			templatedString.append("(");

			if(precision == 0){
				// Using the backspace character as delimiter ($08$)
				templatedString.append("1*=8");
			}else{
				templatedString.append(precision);
			}

			templatedString.append(")");

			if(index +1 < size){
				templatedString.append(",");
			}
		}
		return templatedString.toString();
	}

	/**
	 * Returns the record String to initialize a BBj Templated String based on the values
	 * of this DataRow object.
	 * 
	 * @see #getTemplate()
	 * 
	 * @return the record to initialize the BBj Templated String.
	 * 
	 * @throws Exception
	 */
	public String getString() throws Exception{
		ArrayList<Integer> numericTypeCodeList = new ArrayList<Integer>();
		numericTypeCodeList.add(java.sql.Types.BIGINT);
		numericTypeCodeList.add(java.sql.Types.TINYINT);
		numericTypeCodeList.add(java.sql.Types.INTEGER);
		numericTypeCodeList.add(java.sql.Types.SMALLINT);
		numericTypeCodeList.add(java.sql.Types.NUMERIC);
		numericTypeCodeList.add(java.sql.Types.DOUBLE);
		numericTypeCodeList.add(java.sql.Types.FLOAT);
		numericTypeCodeList.add(java.sql.Types.DECIMAL);
		numericTypeCodeList.add(java.sql.Types.REAL);
		numericTypeCodeList.add(java.sql.Types.BOOLEAN);
		numericTypeCodeList.add(java.sql.Types.BIT);
		numericTypeCodeList.add(java.sql.Types.DATE);
		numericTypeCodeList.add(9); // Basis DATE

		String template = getTemplate();
		TemplatedString stringTemplate = new TemplatedString(template);

		int fieldType;
		Entry<String, DataField> entry;
		Iterator<Entry<String, DataField>> it = this.DataFields.entrySet().iterator();
		while(it.hasNext()){
			entry = it.next();
			fieldType = getFieldType(entry.getKey());
			String fieldName = entry.getKey();
			DataField field = entry.getValue();

			if(numericTypeCodeList.contains(fieldType)){
				if(field.getValue()==null) {
					if (fieldType == java.sql.Types.DATE)
						stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(-1));
					else
						stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(0));
				}else{
					if(fieldType == java.sql.Types.BOOLEAN){
						stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(field.getBoolean()? 1: 0));
					}else if(fieldType == 9){
						stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(field.getInt()));
					}else if(fieldType == java.sql.Types.DATE){
						Integer ret2 = com.basis.util.BasisDate.jul(new java.util.Date(field.getDate().getTime()));
						stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(ret2.doubleValue()));
					}else{
						if(fieldType == java.sql.Types.BIGINT || fieldType == java.sql.Types.TINYINT || fieldType == java.sql.Types.INTEGER || fieldType == java.sql.Types.SMALLINT){
							stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(field.getInt()));
						}else if(fieldType == java.sql.Types.DOUBLE){
							stringTemplate.setFieldValue(fieldName, BasisNumber.valueOf(field.getDouble()));
						}else{
							stringTemplate.setFieldValue(fieldName, new BasisNumber(field.getValue().toString()));
						}
					}
				}
			}else{
				if (field.getValue()!=null) stringTemplate.setFieldValue(fieldName, field.toString());
			}
		}

		return stringTemplate.getString().toString();
	}

	/**
	 * Converts and returns the given String Template as DataRow object
	 * containing the default field values.
	 * 
	 * @see #fromTemplate(String, String)
	 * 
	 * @param template The String Template
	 * 
	 * @return a DataRow object created based on the given String Template
	 * 
	 * @throws Exception
	 */
	public static DataRow fromTemplate(String template) throws Exception{
		return fromTemplate(template, "");
	}

	/**
	 * Converts and returns the given String Template as DataRow object
	 * containing the values of the given record String.
	 * 
	 * @see #fromTemplate(String)
	 * 
	 * @param template The String Template
	 * @param record The record to set
	 * 
	 * @return a DataRow object created based on the given String Template
	 * 
	 * @throws Exception
	 */
	public static DataRow fromTemplate(String template, String record) throws Exception{
		TemplatedString stringTemplate = new TemplatedString(template);
		stringTemplate.setBytes(record.getBytes());

		String fieldName;
		byte fieldType;
		int fieldSize;
		String dType = "";

		int value;
		int sqlType = java.sql.Types.VARCHAR;
		DataField df = null;

		DataRow row = new DataRow();

		int fieldCount = stringTemplate.getNumFields();
		for(int i=0; i<fieldCount; i++){
			fieldName = stringTemplate.getFieldName(i).toString();
			fieldType = stringTemplate.getFieldType(i);
			fieldSize = stringTemplate.getFieldSize(i);

			switch(fieldType){
				case TemplateInfo.BLOB: sqlType = java.sql.Types.BLOB;
										try{
											df = new DataField(stringTemplate.getFieldAsString(i));
										}catch(Exception e){
											df = new DataField("");
										}
										break;

				case TemplateInfo.INTEGER: sqlType = java.sql.Types.INTEGER;
			     						   try{
			     							   df = new DataField(stringTemplate.getFieldAsNumber(i).toBigInteger());
			     						   }catch(Exception e){
			     							   df = new DataField(0);
			     						   }
			     						   break;

				case TemplateInfo.CHARACTER: sqlType = java.sql.Types.CHAR;
											 try{
											 	 df = new DataField(stringTemplate.getFieldAsString(i));
											 }catch(Exception e){
												 df = new DataField("");
											 }
					 						 break;

				case TemplateInfo.RESIDENT_FLOAT: sqlType = java.sql.Types.FLOAT;
		           								  try{
												 	  df = new DataField(stringTemplate.getFloat(i));
												  }catch(Exception e){
													  df = new DataField(0f);
												  }
		           								  break;

				case TemplateInfo.RESIDENT_DOUBLE: sqlType = java.sql.Types.DOUBLE;
					  							   try{
												 	   df = new DataField(stringTemplate.getDouble(i));
												   }catch(Exception e){
													   df = new DataField(0d);
												   }
					  							   break;

				case TemplateInfo.BUS:
				case TemplateInfo.NUMERIC:
				case TemplateInfo.ADJN_BUS:
				case TemplateInfo.BCD_FLOAT:
				case TemplateInfo.IEEE_FLOAT:
				case TemplateInfo.ORDERED_NUMERIC:
				case TemplateInfo.UNSIGNED_INTEGER: sqlType = java.sql.Types.NUMERIC;
										            try{
										            	df = new DataField(stringTemplate.getFieldAsNumber(i).toBigDecimal());
													}catch(Exception e){
														df = new DataField(new java.math.BigDecimal(0));
													}
										            break;

				default: sqlType = java.sql.Types.VARCHAR;
						 try{
						     df = new DataField(stringTemplate.getFieldAsString(i));
						 }catch(Exception e){
							df = new DataField("");
						 }
				   	     break;
			}

			if(sqlType == java.sql.Types.NUMERIC){
				try{
					dType = stringTemplate.getAttribute(fieldName, "DTYPE");

					if(dType.equals("D") && fieldSize == 8){
						// Date
						sqlType = 9; // BASIS Date
						value = stringTemplate.getFieldAsNumber(i).intValue();
						df = new DataField(value);
					}else if(dType.equals("N") && fieldSize == 1){
						// Boolean
						sqlType = java.sql.Types.BOOLEAN;
						if(stringTemplate.getFieldAsNumber(i).intValue() == 0){
							df = new DataField(false);
						}else{
							df = new DataField(true);
						}
					}
				}catch(Exception e){
					//e.printStackTrace();
					// ignoring because not all fields have an attribute DTYPE
				}
			}

			row.addDataField(fieldName, sqlType, df);
		}

		return row;
	}


	/**
	 * Adds the values from a BBj templated string to the current DataRow object.
	 * If the current DataRow object has already field definitions, then they will be used to cast/convert the value to the required field type.
	 * Otherwise the type from the templated string will be used.
	 * 
	 * @param template The String Template
	 * @param record The record to set
	 * 
	 * @return a DataRow object created based on the given String Template
	 * 
	 * @throws Exception Thrown when one of the templated string values doesn't match the field type of the DataRow.
	 */
	public void setString(String template, String record) throws Exception {
		TemplatedString tmpl = new TemplatedString(template);
		tmpl.setBytes(record.getBytes());

		for (int i=0; i<tmpl.getNumFields(); i++) {
			String name = tmpl.getJavaFieldName(i);
			byte type = tmpl.getFieldType(i);
			switch (type) {
			case TemplateInfo.ADJN_BUS:
			case TemplateInfo.BCD_FLOAT:
			case TemplateInfo.BUS:
			case TemplateInfo.IEEE_FLOAT:
			case TemplateInfo.INTEGER:
			case TemplateInfo.NUMERIC:
			case TemplateInfo.ORDERED_NUMERIC:
			case TemplateInfo.RESIDENT_DOUBLE:
			case TemplateInfo.RESIDENT_FLOAT:
			case TemplateInfo.UNSIGNED_INTEGER:
				this.setFieldValue(name, tmpl.getFieldAsNumber(name));
				break;

			default:
				this.setFieldValue(name, tmpl.getFieldAsString(name));
				break;
			}
		}
	}

}
