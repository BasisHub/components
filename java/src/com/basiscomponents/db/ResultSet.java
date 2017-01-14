package com.basiscomponents.db;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import javax.naming.OperationNotSupportedException;

import com.basis.util.common.BasisNumber;
import com.basis.util.common.Template;
import com.basis.util.common.TemplateInfo;
import com.basiscomponents.db.sql.SQLResultSet;
import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public class ResultSet implements java.io.Serializable, Iterable<DataRow> {

	private static final long serialVersionUID = 1L;

	@Expose
	private ArrayList<HashMap<String, Object>> MetaData = new ArrayList<HashMap<String, Object>>();
	@Expose
	private ArrayList<String> ColumnNames = new ArrayList<String>();
	@Expose
	private ArrayList<DataRow> DataRows = new ArrayList<DataRow>();
	private ArrayList<String> FieldSelection;

	private ArrayList<String> KeyColumns = new ArrayList<String>();
	private String KeyTemplateString = "";
	private Template KeyTemplate = null;

	private int currentRow = -1;
	private DataRow currentDataRow;

	public static HashMap<Integer, String> SQLTypeNameMap = new HashMap<Integer, String>();

	private SQLResultSet sqlResultSet = null;

	public ResultSet() {
		if (SQLTypeNameMap.isEmpty())
			setSQLTypeNameMap();
	}

	@SuppressWarnings("unchecked")
	private ResultSet(ArrayList<HashMap<String, Object>> MetaData, ArrayList<String> ColumnNames,
			ArrayList<DataRow> DataRows, ArrayList<String> KeyColumns) {
		this.MetaData = (ArrayList<HashMap<String, Object>>) MetaData.clone();
		this.ColumnNames = (ArrayList<String>) ColumnNames.clone();
		this.DataRows = (ArrayList<DataRow>) DataRows.clone();
		this.KeyColumns = (ArrayList<String>) KeyColumns.clone();
	}

	@SuppressWarnings("unchecked")
	private ResultSet(ArrayList<HashMap<String, Object>> MetaData, ArrayList<String> ColumnNames,
			ArrayList<String> KeyColumns) {
		this.MetaData = (ArrayList<HashMap<String, Object>>) MetaData.clone();
		this.ColumnNames = (ArrayList<String>) ColumnNames.clone();
		this.KeyColumns = (ArrayList<String>) KeyColumns.clone();
	}

	@Override
	public ResultSet clone() {
		return new ResultSet(MetaData, ColumnNames, DataRows, KeyColumns);
	}

	public ResultSet orderBy(String orderByClause) throws Exception {
		ResultSet r = new ResultSet(this.MetaData, this.ColumnNames, this.KeyColumns);

		String[] fields = orderByClause.split(",");
		for (int i = 0; i < fields.length; i++) {
			fields[i] = fields[i].trim();
			if (fields[i].contains(" ")) {
				fields[i] = fields[i].substring(0, fields[i].indexOf(' '));
				// TODO implement DESCending and ASCending predicates, maybe
				// UCASE() etc., like in an SQL ORDER BY clause
			}
		}

		Iterator<DataRow> it = this.iterator();
		TreeMap<String, DataRow> tm = new TreeMap<>();
		while (it.hasNext()) {
			DataRow dr = it.next();
			String idx = "";
			for (int i = 0; i < fields.length; i++) {
				idx += dr.getFieldAsString(fields[i]);
			}
			tm.put(idx, dr);
		}

		Iterator<String> it2 = tm.keySet().iterator();
		while (it2.hasNext()) {
			String k = it2.next();
			DataRow dr = tm.get(k);
			r.add(dr);
		}

		return r;
	}

	public void orderByColumn(String fieldName, String direction) {
		if (!"ASC".equalsIgnoreCase(direction) && !"DESC".equalsIgnoreCase(direction))
			direction = "ASC";

		java.util.Comparator<DataRow> comparator = new DataRowComparator(fieldName);
		if ("DESC".equalsIgnoreCase(direction))
			comparator = comparator.reversed();

		DataRows.sort(comparator);
	}

	public void orderByColumn(java.util.Comparator<DataRow> comparator) {
		DataRows.sort(comparator);
	}

	public void orderByRowID() {
		DataRows.sort(java.util.Comparator.comparing(DataRow::getRowID));
	}

	public ResultSet filterBy(DataRow simpleFilterCondition) throws Exception {
		ResultSet r = new ResultSet(this.MetaData, this.ColumnNames, this.KeyColumns);

		BBArrayList filterFields = simpleFilterCondition.getFieldNames();

		Iterator<DataRow> it = this.iterator();
		while (it.hasNext()) {
			DataRow dr = it.next();
			@SuppressWarnings("rawtypes")
			Iterator it2 = filterFields.iterator();
			Boolean match = true;
			while (it2.hasNext()) {
				String k = (String) it2.next();
				match = true;
				DataField cond = simpleFilterCondition.getField(k);

				if (dr.getFieldType(k) == 12 && cond.getString().startsWith("regex:")) {
					if (!dr.getFieldAsString(k).matches(cond.getString().substring(6))) {
						match = false;
						break;
					}
				} else {
					DataField comp = dr.getField(k);
					if (!cond.equals(comp)) {
						match = false;
						break;
					}
				}
			}
			if (match) {
				r.add(dr);
			}
		}
		return r;
	}

	public ResultSet(java.sql.ResultSet rs) {
		this();
		try {
			populate(rs, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// NOTE: java.sql.ResultSet is 1-based, ours is 0-based
	public void populate(java.sql.ResultSet rs, Boolean defaultMetaData) throws Exception {
		java.sql.ResultSetMetaData rsmd = rs.getMetaData();
		int cc = rsmd.getColumnCount();
		String name;
		int type;
		ArrayList<Integer> types = new ArrayList<Integer>();
		LinkedHashMap<Integer, String> columns = new LinkedHashMap<Integer, String>();
		int column = 0;
		if (defaultMetaData == true) {
			while (column < cc) {
				column++;
				if (FieldSelection != null && !FieldSelection.contains(rsmd.getColumnName(column))) continue;
				HashMap<String, Object> colMap = new HashMap<String, Object>();
				colMap.put("CatalogName", rsmd.getCatalogName(column));
				colMap.put("ColumnClassName", rsmd.getColumnClassName(column));
				colMap.put("ColumnDisplaySize", rsmd.getColumnDisplaySize(column));
				colMap.put("ColumnLabel", rsmd.getColumnLabel(column));
				name = rsmd.getColumnName(column);
				if (this.ColumnNames.contains(name))
					name = name + "_" + String.valueOf(column); // handle dups
				colMap.put("ColumnName", name);
				columns.put(column, name);
				this.ColumnNames.add(name);
				type = rsmd.getColumnType(column);
				colMap.put("ColumnType", type);
				types.add(type);
				colMap.put("ColumnTypeName", rsmd.getColumnTypeName(column));
				colMap.put("Precision", rsmd.getPrecision(column));
				colMap.put("Scale", rsmd.getScale(column));
				colMap.put("SchemaName", rsmd.getSchemaName(column));
				colMap.put("TableName", rsmd.getTableName(column));
				colMap.put("AutoIncrement", rsmd.isAutoIncrement(column));
				colMap.put("CaseSensitive", rsmd.isCaseSensitive(column));
				colMap.put("Currency", rsmd.isCurrency(column));
				colMap.put("DefinitelyWritable", rsmd.isDefinitelyWritable(column));
				colMap.put("Nullable", rsmd.isNullable(column));
				colMap.put("ReadOnly", rsmd.isReadOnly(column));
				colMap.put("Searchable", rsmd.isSearchable(column));
				colMap.put("Signed", rsmd.isSigned(column));
				colMap.put("Writable", rsmd.isWritable(column));
				colMap.put("StringFormat", "");
				this.MetaData.add(colMap);
			}
		} else {
			Iterator<String> it = ColumnNames.iterator();
			while (it.hasNext()) {
				columns.put(columns.size()+1, it.next());
			}
		}

		if (KeyColumns != null && KeyColumns.size() > 0) {
			KeyTemplate = TemplateInfo.createTemplate(getKeyTemplate());
		}

		try {
			rs.beforeFirst();
		} catch (Exception e) {
			// do nothing
		}
		int rowId = 0;
		while (rs.next()) {
			DataRow dr = DataRow.newInstance(this);
			Iterator<HashMap.Entry<Integer, String>> it = columns.entrySet().iterator();
			column = 0;
			while (it.hasNext()) {
				column++;
				HashMap.Entry<Integer, String> entry = it.next();
				name = entry.getValue();
				DataField field = new DataField(rs.getObject(entry.getKey()));
				if (defaultMetaData == true)
					type = types.get(column - 1);
				else
					type = getColumnType(column - 1);
				dr.addDataField(name, type, field);
				if (this.MetaData.get(column - 1).get("ColumnTypeName").equals("JSON"))
					dr.setFieldAttribute(name, "StringFormat", "JSON");

				if (this.isAutoIncrement(column - 1) || !this.isWritable(column - 1)
						|| !this.isDefinitelyWritable(column - 1))
					dr.setFieldAttribute(name, "EDITABLE", "0");
				else
					dr.setFieldAttribute(name, "EDITABLE", "1");
			}

			if (KeyColumns != null && KeyColumns.size() > 0) {
				try {
					buildRowKey(rs, dr);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}

			dr.setRowID(rowId);
			this.DataRows.add(dr);
			rowId++;
		}
	}

	private void mergeDataRowFields(DataRow dr) {
		BBArrayList names = dr.getFieldNames();
		@SuppressWarnings("unchecked")
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (!this.ColumnNames.contains(name)) {
				int column = this.addColumn(name);
				try {
					this.setColumnType(column, dr.getFieldType(name));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					HashMap<String, String> attrMap = dr.getFieldAttributes(name);
					Iterator<String> it2 = attrMap.keySet().iterator();
					while (it2.hasNext()) {
						String attrKey = it2.next();
						this.setAttribute(column, attrKey, attrMap.get(attrKey));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void add(DataRow dr) {
		this.DataRows.add(dr);
		this.mergeDataRowFields(dr);
	}

	public void add(int row, DataRow dr) {
		this.DataRows.add(row, dr);
		this.mergeDataRowFields(dr);
	}

	public void addItem(DataRow dr) {
		add(dr);
		this.mergeDataRowFields(dr);
	}

	public void insertItem(int row, DataRow dr) {
		add(row, dr);
		this.mergeDataRowFields(dr);
	}

	public int addColumn(String name) {
		HashMap<String, Object> colMap = new HashMap<String, Object>();
		colMap.put("ColumnName", name);
		this.ColumnNames.add(name);
		this.MetaData.add(colMap);
		return this.MetaData.size() - 1;
	}

	public int addColumn(String name, HashMap<String, Object> colMap) {
		colMap.put("ColumnName", name);
		this.ColumnNames.add(name);
		this.MetaData.add(colMap);
		return this.MetaData.size() - 1;
	}

	public void setColumnMetaData(String name, HashMap<String, Object> colMap) {
		int column = getColumnIndex(name);
		if (column != -1)
			this.MetaData.set(column, colMap);
	}

	public HashMap<String, Object> getColumnMetaData(String name) {
		int column = getColumnIndex(name);
		if (column != -1)
			return this.MetaData.get(column);
		else
			return null;
	}

	public int getColumnIndex(String name) {
		return this.ColumnNames.indexOf(name);
	}

	public ArrayList<String> getColumnNames() {
		return this.ColumnNames;
	}

	/**
	 * @return the keyColumns
	 */
	public ArrayList<String> getKeyColumns() {
		return this.KeyColumns;
	}

	/**
	 * @param keyColumns
	 *            the keyColumns to set
	 */
	public void setKeyColumns(ArrayList<String> keyColumns) {
		this.KeyColumns = keyColumns;
	}

	/**
	 * @param name
	 *            the column name
	 */
	public void addKeyColumn(String name) {
		this.KeyColumns.add(name);
	}

	/**
	 * @return the template definition string
	 */
	public String getKeyTemplate() {
		if (this.KeyTemplateString == "")
			this.KeyTemplateString = getBBKeyTemplate();
		return this.KeyTemplateString;
	}

	/**
	 * @param template
	 *            the template definition string
	 */
	public void setKeyTemplate(String template) {
		this.KeyTemplateString = template;
	}

	/**
	 * @return key data as a string for the current row
	 */
	public String getRowKey() {
		return getRowKey(this.currentRow);
	}

	/**
	 * @return key data as a string for a specific row
	 */
	public String getRowKey(int row) {
		DataRow dr = get(row);
		return dr.getRowKey();
	}

	public void clear() {
		this.DataRows.clear();
	}

	public DataRow get(int row) {
		return this.DataRows.get(row);
	}

	public DataRow getItem(int row) {
		return get(row);
	}

	public void set(int row, DataRow dr) {
		this.DataRows.set(row, dr);
	}

	public void setItem(int row, DataRow dr) {
		set(row, dr);
	}

	public int getRow() {
		return this.currentRow;
	}

	public Boolean isEmpty() {
		return this.DataRows.isEmpty();
	}

	public DataRow remove(int row) {
		if (this.currentRow == row) {
			this.currentRow -= 1;
			this.currentDataRow = null;
		}
		return this.DataRows.remove(row);
	}

	public DataRow removeItem(int row) {
		return remove(row);
	}

	public void removeColumn(int column) {
		this.ColumnNames.remove(column);
		this.MetaData.remove(column);
	}

	public int size() {
		return this.DataRows.size();
	}

	// Navigation methods (0-based)
	public void beforeFirst() {
		this.currentRow = -1;
		this.currentDataRow = null;
	}

	public void afterLast() {
		this.currentRow = this.DataRows.size();
		this.currentDataRow = null;
	}

	public Boolean first() {
		if (this.DataRows.isEmpty())
			return false;
		else {
			this.currentRow = 0;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	public Boolean last() {
		if (this.DataRows.isEmpty())
			return false;
		else {
			this.currentRow = this.DataRows.size() - 1;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	public Boolean absolute(int row) {
		if (this.DataRows.isEmpty() || row < 0 || row > this.DataRows.size() - 1)
			return false;
		else {
			this.currentRow = row;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	public Boolean next() {
		if (this.DataRows.isEmpty() || this.currentRow > this.DataRows.size() - 2)
			return false;
		else {
			this.currentRow += 1;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	// MetaData get methods (0-based)
	public int getColumnCount() {
		return this.MetaData.size();
	}

	public String getCatalogName(int column) {
		String name = (String) this.MetaData.get(column).get("CatalogName");
		if (name == null)
			name = "";
		return name;
	}

	public String getColumnClassName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnClassName");
		if (name == null)
			name = "";
		return name;
	}

	public int getColumnDisplaySize(int column) {
		Integer size = (Integer) this.MetaData.get(column).get("ColumnDisplaySize");
		if (size == null)
			size = 0;
		return size.intValue();
	}

	public String getColumnLabel(int column) {
		String label = (String) this.MetaData.get(column).get("ColumnLabel");
		if (label == null)
			label = "";
		return label;
	}

	public String getColumnName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnName");
		if (name == null)
			name = "";
		return name;
	}

	public int getColumnType(int column) {
		Integer type = (Integer) this.MetaData.get(column).get("ColumnType");
		if (type == null)
			type = 0;
		return type.intValue();
	}

	public String getColumnTypeName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnTypeName");
		if (name == null)
			name = "";
		return name;
	}

	public int getPrecision(int column) {
		Integer prec = (Integer) this.MetaData.get(column).get("Precision");
		if (prec == null)
			prec = 0;
		return prec.intValue();
	}

	public int getScale(int column) {
		Integer scale = (Integer) this.MetaData.get(column).get("Scale");
		if (scale == null)
			scale = 0;
		return scale.intValue();
	}

	public String getSchemaName(int column) {
		String name = (String) this.MetaData.get(column).get("SchemaName");
		if (name == null)
			name = "";
		return name;
	}

	public String getTableName(int column) {
		String name = (String) this.MetaData.get(column).get("TableName");
		if (name == null)
			name = "";
		return name;
	}

	public Boolean isAutoIncrement(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("AutoIncrement");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isCaseSensitive(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("CaseSensitive");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isCurrency(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Currency");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isDefinitelyWritable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("DefinitelyWritable");
		if (flag == null)
			flag = false;
		return flag;
	}

	public int isNullable(int column) {
		Integer nullable = (Integer) this.MetaData.get(column).get("Nullable");
		if (nullable == null)
			nullable = java.sql.ResultSetMetaData.columnNullableUnknown;
		return nullable.intValue();
	}

	public Boolean isReadOnly(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("ReadOnly");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isSearchable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Searchable");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isSigned(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Signed");
		if (flag == null)
			flag = false;
		return flag;
	}

	public Boolean isWritable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Writable");
		if (flag == null)
			flag = false;
		return flag;
	}

	public String getAttribute(int column, String name) {
		String value = (String) this.MetaData.get(column).get(name);
		if (value == null)
			value = "";
		return value;
	}

	// MetaData set methods (0-based)
	public void setCatalogName(int column, String catalogName) {
		this.MetaData.get(column).put("CatalogName", catalogName);
	}

	public void setColumnClassName(int column, String className) {
		this.MetaData.get(column).put("ColumnClassName", className);
	}

	public void setColumnDisplaySize(int column, int displaySize) {
		this.MetaData.get(column).put("ColumnDisplaySize", displaySize);
	}

	public void setColumnLabel(int column, String label) {
		this.MetaData.get(column).put("ColumnLabel", label);
	}

	public void setColumnName(int column, String name) throws Exception {
		if (name.isEmpty())
			throw new Exception("Column name may not be empty");
		this.MetaData.get(column).put("ColumnName", name);
	}

	public void setColumnType(int column, int type) throws Exception {
		String typeName = getSQLTypeName(type);
		if (typeName == null)
			throw new Exception("Unknown column type" + String.valueOf(type));
		else
			setColumnTypeName(column, typeName);
		this.MetaData.get(column).put("ColumnType", type);
	}

	public void setColumnTypeName(int column, String typeName) {
		this.MetaData.get(column).put("ColumnTypeName", typeName);
	}

	public void setPrecision(int column, int precision) throws Exception {
		if (precision < getScale(column))
			throw new Exception("Precision must be >= scale");
		this.MetaData.get(column).put("Precision", precision);
	}

	public void setScale(int column, int scale) throws Exception {
		if (scale > getPrecision(column))
			throw new Exception("Scale must be <= precision");
		this.MetaData.get(column).put("Scale", scale);
	}

	public void setSchemaName(int column, String schemaName) {
		this.MetaData.get(column).put("SchemaName", schemaName);
	}

	public void setTableName(int column, String tableName) {
		this.MetaData.get(column).put("TableName", tableName);
	}

	public void setAutoIncrement(int column, Boolean flag) {
		this.MetaData.get(column).put("AutoIncrement", flag);
	}

	public void setCaseSensitive(int column, Boolean flag) {
		this.MetaData.get(column).put("CaseSensitive", flag);
	}

	public void setCurrency(int column, Boolean flag) {
		this.MetaData.get(column).put("Currency", flag);
	}

	public void setDefinitelyWritable(int column, Boolean flag) {
		this.MetaData.get(column).put("DefinitelyWritable", flag);
	}

	public void setNullable(int column, int nullable) throws Exception {
		if (nullable != java.sql.ResultSetMetaData.columnNoNulls
				&& nullable != java.sql.ResultSetMetaData.columnNullable
				&& nullable != java.sql.ResultSetMetaData.columnNullableUnknown)
			throw new Exception("Invalid nullable value" + String.valueOf(nullable));
		this.MetaData.get(column).put("Nullable", nullable);
	}

	public void setReadOnly(int column, Boolean flag) {
		this.MetaData.get(column).put("ReadOnly", flag);
	}

	public void setSearchable(int column, Boolean flag) {
		this.MetaData.get(column).put("Searchable", flag);
	}

	public void setSigned(int column, Boolean flag) {
		this.MetaData.get(column).put("Signed", flag);
	}

	public void setWritable(int column, Boolean flag) {
		this.MetaData.get(column).put("Writable", flag);
	}

	public void setAttribute(int column, String name, String value) {
		this.MetaData.get(column).put(name, value);
	}

	public DataField getField(int column) {
		String name = "";
		try {
			name = this.currentDataRow.getColumnName(column);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return this.currentDataRow.getDataField(name);
	}

	// column get methods (0-based)
	public String getString(int column) {
		DataField field = getField(column);
		return field.getString();
	}

	public String getNString(int column) {
		DataField field = getField(column);
		return field.getString();
	}

	public Integer getInt(int column) {
		DataField field = getField(column);
		return field.getInt();
	}

	public Byte getByte(int column) {
		DataField field = getField(column);
		return field.getByte();
	}

	public Short getShort(int column) {
		DataField field = getField(column);
		return field.getShort();
	}

	public Long getLong(int column) {
		DataField field = getField(column);
		return field.getLong();
	}

	public BigDecimal getBigDecimal(int column) {
		DataField field = getField(column);
		return field.getBigDecimal();
	}

	public Double getDouble(int column) {
		DataField field = getField(column);
		return field.getDouble();
	}

	public Float getFloat(int column) {
		DataField field = getField(column);
		return field.getFloat();
	}

	public Boolean getBoolean(int column) {
		DataField field = getField(column);
		return field.getBoolean();
	}

	public Date getDate(int column) {
		DataField field = getField(column);
		return field.getDate();
	}

	public Time getTime(int column) {
		DataField field = getField(column);
		return field.getTime();
	}

	public Timestamp getTimestamp(int column) {
		DataField field = getField(column);
		return field.getTimestamp();
	}

	public byte[] getBytes(int column) {
		DataField field = getField(column);
		return field.getBytes();
	}

	public Array getArray(int column) {
		DataField field = getField(column);
		return field.getArray();
	}

	public Blob getBlob(int column) {
		DataField field = getField(column);
		return field.getBlob();
	}

	public Clob getClob(int column) {
		DataField field = getField(column);
		return field.getClob();
	}

	public Clob getNClob(int column) {
		DataField field = getField(column);
		return field.getNClob();
	}

	public Object getObject(int column) {
		DataField field = getField(column);
		return field.getObject();
	}

	public Ref getRef(int column) {
		DataField field = getField(column);
		return field.getRef();
	}

	public URL getURL(int column) {
		DataField field = getField(column);
		return field.getURL();
	}

	public void setFieldSelection(ArrayList<String> fieldSelection) {
		this.FieldSelection = fieldSelection;
	}

	// getRowId(int column)
	// getAsciiStream(int column)
	// getBinaryStream(int column)
	// getCharacterStream(int column)
	// getNCharacterStream(int column)
	// getUnicodeStream(int column)

	@SuppressWarnings("deprecation")
	public String toJson() throws Exception {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter w = new StringWriter();
		JsonGenerator g = jf.createJsonGenerator(w);
		// g.useDefaultPrettyPrinter();

		g.writeStartArray();

		Boolean meta_done = false;
		Iterator<DataRow> it = this.DataRows.iterator();
		while (it.hasNext()) {
			DataRow dr = it.next();
			BBArrayList f = dr.getFieldNames();
			@SuppressWarnings("unchecked")
			Iterator<String> itf = f.iterator();

			g.writeStartObject();

			while (itf.hasNext()) {
				String fn = itf.next();
				int t = dr.getFieldType(fn);
				switch (t) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					String tmp = dr.getField(fn).getAttribute("StringFormat");
					if (tmp != null && tmp.toUpperCase().equals("JSON")) {
						g.writeFieldName(fn);
						String s = dr.getField(fn).getString().trim();
						if (s.isEmpty()) {
							s = "{}";
						}
						g.writeRawValue(s);
					} else {
						String s = dr.getField(fn).getString().trim();
						g.writeStringField(fn, s);
					}
					break;
				case java.sql.Types.BIGINT:
					if (dr.getField(fn) == null || dr.getField(fn).getLong() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getLong().longValue());
					break;

				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
					if (dr.getField(fn) == null || dr.getField(fn).getInt() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getInt().intValue());
					break;

				case java.sql.Types.NUMERIC:
					g.writeNumberField(fn, dr.getField(fn).getBigDecimal());
					break;

				case java.sql.Types.DECIMAL:
					if (dr.getField(fn) == null || dr.getField(fn).getBigDecimal() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getBigDecimal());
					break;

				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
				case java.sql.Types.REAL:
					if (dr.getField(fn) == null || dr.getField(fn).getDouble() == null)
						g.writeNumberField(fn, 0.0);
					else
						g.writeNumberField(fn, dr.getField(fn).getDouble().doubleValue());
					break;

				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					if (dr.getField(fn) == null || dr.getField(fn).getBoolean() == null)
						g.writeStringField(fn, "");
					else
						g.writeBooleanField(fn, dr.getField(fn).getBoolean());

					break;

				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
				case 11:
					if (dr.getField(fn) == null || dr.getField(fn).getTimestamp() == null)
						g.writeStringField(fn, "");
					else {
						DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
						DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
						String fd = df1.format(dr.getField(fn).getTimestamp()) + "T"
								+ df2.format(dr.getField(fn).getTimestamp()) + ".000Z";
						g.writeStringField(fn, fd);
					}
					break;
				case java.sql.Types.DATE:
				case 9:
					if (dr.getField(fn) == null || dr.getField(fn).getDate() == null)
						g.writeStringField(fn, "");
					else {
						DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
						DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
						String fd = df1.format(dr.getField(fn).getDate()) + "T" + df2.format(dr.getField(fn).getDate())
								+ ".000Z";
						g.writeStringField(fn, fd);
					}
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
					// this is a noop - TODO
					break;

				}// switch

			} // while on fields

			if (!meta_done) {

				g.writeFieldName("meta");

				g.writeStartObject();

				Iterator<HashMap<String, Object>> i = this.MetaData.iterator();
				while (i.hasNext()) {
					HashMap<String, Object> hm = i.next();
					String c = (String) hm.get("ColumnName");
					if (c != null) {
						g.writeFieldName(c);
						g.writeStartObject();

						Set<String> ks = hm.keySet();
						Iterator<String> its = ks.iterator();
						while (its.hasNext()) {
							String key = its.next();
							if (key.equals("ColumnTypeName") || key.equals("ColumnName"))
								continue;
							String value = null;
							if (hm.get(key) != null)
								value = hm.get(key).toString();
							g.writeStringField(key, value);
						}
						g.writeEndObject();
					}
				}

				g.writeEndObject();

				meta_done = true;
			}

			g.writeEndObject();

		} // while on rows

		g.writeEndArray();
		g.close();

		return (w.toString());
	}

	private static void setSQLTypeNameMap() {
		SQLTypeNameMap.put(java.sql.Types.ARRAY, "ARRAY");
		SQLTypeNameMap.put(java.sql.Types.BIGINT, "BIGINT");
		SQLTypeNameMap.put(java.sql.Types.BINARY, "BINARY");
		SQLTypeNameMap.put(java.sql.Types.BIT, "BIT");
		SQLTypeNameMap.put(java.sql.Types.BLOB, "BLOB");
		SQLTypeNameMap.put(java.sql.Types.BOOLEAN, "BOOLEAN");
		SQLTypeNameMap.put(java.sql.Types.CHAR, "CHAR");
		SQLTypeNameMap.put(java.sql.Types.CLOB, "CLOB");
		SQLTypeNameMap.put(java.sql.Types.DATALINK, "DATALINK");
		SQLTypeNameMap.put(java.sql.Types.DATE, "DATE");
		SQLTypeNameMap.put(java.sql.Types.DECIMAL, "DECIMAL");
		SQLTypeNameMap.put(java.sql.Types.DISTINCT, "DISTINCT");
		SQLTypeNameMap.put(java.sql.Types.DOUBLE, "DOUBLE");
		SQLTypeNameMap.put(java.sql.Types.FLOAT, "FLOAT");
		SQLTypeNameMap.put(java.sql.Types.INTEGER, "INTEGER");
		SQLTypeNameMap.put(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT");
		SQLTypeNameMap.put(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR");
		SQLTypeNameMap.put(java.sql.Types.LONGVARBINARY, "LONGVARBINARY");
		SQLTypeNameMap.put(java.sql.Types.LONGVARCHAR, "LONGVARCHAR");
		SQLTypeNameMap.put(java.sql.Types.NCHAR, "NCHAR");
		SQLTypeNameMap.put(java.sql.Types.NCLOB, "NCLOB");
		SQLTypeNameMap.put(java.sql.Types.NULL, "NULL");
		SQLTypeNameMap.put(java.sql.Types.NUMERIC, "NUMERIC");
		SQLTypeNameMap.put(java.sql.Types.NVARCHAR, "NVARCHAR");
		SQLTypeNameMap.put(java.sql.Types.OTHER, "OTHER");
		SQLTypeNameMap.put(java.sql.Types.REAL, "REAL");
		SQLTypeNameMap.put(java.sql.Types.REF, "REF");
		SQLTypeNameMap.put(java.sql.Types.REF_CURSOR, "REF_CURSOR");
		SQLTypeNameMap.put(java.sql.Types.ROWID, "ROWID");
		SQLTypeNameMap.put(java.sql.Types.SMALLINT, "SMALLINT");
		SQLTypeNameMap.put(java.sql.Types.SQLXML, "SQLXML");
		SQLTypeNameMap.put(java.sql.Types.STRUCT, "STRUCT");
		SQLTypeNameMap.put(java.sql.Types.TIME, "TIME");
		SQLTypeNameMap.put(java.sql.Types.TIME_WITH_TIMEZONE, "TIME_WITH_TIMEZONE");
		SQLTypeNameMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
		SQLTypeNameMap.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE, "TIMESTAMP_WITH_TIMEZONE");
		SQLTypeNameMap.put(java.sql.Types.TINYINT, "TINYINT");
		SQLTypeNameMap.put(java.sql.Types.VARBINARY, "VARBINARY");
		SQLTypeNameMap.put(java.sql.Types.VARCHAR, "VARCHAR");
		SQLTypeNameMap.put(9, "BASIS DATE");
		SQLTypeNameMap.put(11, "BASIS TIMESTAMP");
	}

	public static String getSQLTypeName(int type) {
		return SQLTypeNameMap.get(type);
	}

	public static ResultSet fromJson(String js) {

		ResultSet rs = new ResultSet();
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonArray o = new com.google.gson.JsonArray();
		try {
			o = parser.parse(js).getAsJsonArray();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Iterator<JsonElement> it = o.iterator();
		JsonObject meta=null;
		while (it.hasNext()) {
			JsonElement el = it.next();
			if (meta == null)
				meta = el.getAsJsonObject().getAsJsonObject("meta");
			if (meta == null) 
				System.err.println("error parsing - meta data missing");
			
			DataRow r = null;
			try {
				//System.out.println(el.toString());
				if (el.getAsJsonObject().getAsJsonObject("meta") == null)
					el.getAsJsonObject().add("meta", meta);
				r = DataRow.fromJson(el.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rs.add(r);
		}
		return rs;
	}

	public java.sql.ResultSet getSQLResultSet() {
		if (sqlResultSet == null) {
			sqlResultSet = new SQLResultSet(this);
		}
		return sqlResultSet;
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

	/*
	 * JDBC Types Mapped to Java Object Types CHAR String VARCHAR String
	 * LONGVARCHAR String NUMERIC java.math.BigDecimal DECIMAL
	 * java.math.BigDecimal BIT Boolean BOOLEAN Boolean TINYINT Byte SMALLINT
	 * Short INTEGER Integer BIGINT Long REAL Float FLOAT Double DOUBLE Double
	 * BINARY byte[] VARBINARY byte[] LONGVARBINARY byte[] DATE java.sql.Date
	 * TIME java.sql.Time TIMESTAMP java.sql.Timestamp DISTINCT (Object type of
	 * underlying type) CLOB java.sql.Clob BLOB java.sql.Blob ARRAY
	 * java.sql.Array STRUCT java.sql.Struct or SQLData REF java.sql.Ref
	 * JAVA_OBJECT (Underlying Java class)
	 */
	/*
	 * Java Object Types Mapped to JDBC Types char CHAR(1?) Character CHAR(1?)
	 * String CHAR, VARCHAR, or LONGVARCHAR java.math.BigDecimal NUMERIC or
	 * DECIMAL byte TINYINT Byte TINYINT boolean BOOLEAN Boolean BOOLEAN int
	 * INTEGER Integer INTEGER long BIGINT Long BIGINT java.math.BigInteger
	 * BIGINT short SMALLINT Short SMALLINT float REAL Float REAL double DOUBLE
	 * Double DOUBLE byte[] BINARY, VARBINARY, or LONGVARBINARY java.sql.Date
	 * DATE java.sql.Time TIME java.sql.Timestamp TIMESTAMP java.sql.Clob CLOB
	 * java.sql.Blob BLOB java.sql.Array ARRAY java.sql.Struct STRUCT
	 * java.sql.Ref REF Java class JAVA_OBJECT
	 */

	@Override
	public String toString() {
		String s = "[";
		s += "Metadata:";
		s += this.MetaData.toString();
		s += ";";
		s += "Columns:";
		s += this.ColumnNames.toString();
		s += ";";
		s += "Records:[";
		Iterator<DataRow> it = this.DataRows.iterator();
		while (it.hasNext()) {
			s += it.next().toString();
			s += ";";
		}
		s += "]]";
		return s;
	}

	@Override
	public Iterator<DataRow> iterator() {
		// TODO Auto-generated method stub
		return new ResultSetIterator(this.DataRows);
	}

	public static class ResultSetIterator implements Iterator<DataRow> {

		private ArrayList<DataRow> DataRows = new ArrayList<DataRow>();
		private int current;

		ResultSetIterator(ArrayList<DataRow> DataRows) {
			this.DataRows = DataRows;
			this.current = 0;
		}

		@Override
		public boolean hasNext() {
			return current < DataRows.size();
		}

		@Override
		public DataRow next() {
			if (!hasNext())
				throw new NoSuchElementException();
			return DataRows.get(current++);
		}

		@Override
		public void remove() {
			// Choose exception or implementation:
			try {
				throw new OperationNotSupportedException();
			} catch (OperationNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// or
			// // if (! hasNext()) throw new NoSuchElementException();
			// // if (currrent + 1 < myArray.end) {
			// // System.arraycopy(myArray.arr, current+1, myArray.arr, current,
			// myArray.end - current-1);
			// // }
			// // myArray.end--;
		}
	}

	// ---------- scalar functions on one field in the resultset ----------

	public int count() {
		return this.DataRows.size();
	}

	public Double sum(String fieldname) throws Exception {
		Iterator<DataRow> it = iterator();
		Double s = 0.0;
		while (it.hasNext()) {
			DataRow dr = it.next();
			s += dr.getFieldAsNumber(fieldname);
		}
		return s;
	};

	public Double min(String fieldname) throws Exception {
		Iterator<DataRow> it = iterator();
		Double s = 0.0;
		Boolean first = true;
		while (it.hasNext()) {
			DataRow dr = it.next();
			Double d = dr.getFieldAsNumber(fieldname);
			if (first || d < s) {
				s = d;
				first = false;
			}
		}
		return s;
	};

	public Double max(String fieldname) throws Exception {
		Iterator<DataRow> it = iterator();
		Double s = 0.0;
		Boolean first = true;
		while (it.hasNext()) {
			DataRow dr = it.next();
			Double d = dr.getFieldAsNumber(fieldname);
			if (first || d > s) {
				s = d;
				first = false;
			}
		}
		return s;
	};

	public Double avg(String fieldname) throws Exception {
		return sum(fieldname) / count();
	};

	public Double median(String fieldname) throws Exception {
		double[] m = new double[count()];
		Iterator<DataRow> it = iterator();
		int i = 0;
		while (it.hasNext()) {
			DataRow dr = it.next();
			m[i++] = dr.getFieldAsNumber(fieldname);
		}

		Arrays.sort(m);

		int middle = m.length / 2;
		if (m.length % 2 == 1) {
			return m[middle];
		} else {
			return (m[middle - 1] + m[middle]) / 2.0;
		}
	}

	public DataRow countByGroup(String fieldname, String labelname) throws Exception {
		return this.countByGroup(fieldname, labelname, NO_SORT, 0);
	}

	public DataRow countByGroup(String fieldname) throws Exception {
		return this.countByGroup(fieldname, fieldname, NO_SORT, 0);
	}

	public static final int NO_SORT = 0;
	public static final int SORT_ON_GROUPFIELD = 1;
	public static final int SORT_ON_GROUPLABEL = 2;
	public static final int SORT_ON_RESULT = 3;
	public static final int SORT_ON_GROUPFIELD_DESC = 11;
	public static final int SORT_ON_GROUPLABEL_DESC = 12;
	public static final int SORT_ON_RESULT_DESC = 13;

	public DataRow countByGroup(String fieldname, String labelname, int sort, int top) throws Exception {

		// note: fieldname and labelname are separate as you may have different
		// values in the row to group by (like different IDs),
		// but the human readable values might still be the same (like same name
		// for different IDs).

		Iterator<DataRow> it = this.iterator();
		DataRow dr = new DataRow();
		DataRow d;
		Integer tmp;
		String field, label;
		while (it.hasNext()) {
			d = it.next();
			field = "(-)";
			label = "(-)";
			try {
				field = d.getFieldAsString(fieldname);
				label = d.getFieldAsString(labelname);
			} catch (Exception e) {
			} finally {
			}
			tmp = 0;
			try {
				tmp = dr.getField(field).getInt();
			} catch (Exception e) {
			} finally {
			}
			dr.setFieldValue(field, tmp + 1);
			dr.setFieldAttribute(field, "label", label);
		}

		if (sort > 0) {
			dr = sortDataRow(dr, sort);
		}

		if (top > 0) {
			BBArrayList map = dr.getFieldNames();
			while (map.size() > top) {
				int i = map.size() - 1;
				String f = (String) map.get(i);
				dr.removeField(f);
				map.remove(i);
			}
		}

		return dr;
	}

	private DataRow sortDataRow(DataRow dr, int sort) throws Exception {
		// TODO make a generic implementation of this in the DataRow itself,
		// that can sort on the value, the name or any attribute

		DataRow drn = new DataRow();
		BBArrayList f = dr.getFieldNames();
		@SuppressWarnings("rawtypes")
		Iterator it = f.iterator();
		TreeMap<String, String> tm = new TreeMap<>();
		while (it.hasNext()) {
			String k = (String) it.next();
			String tmp = "";
			switch (sort) {
			case SORT_ON_GROUPFIELD:
				tm.put(k, k);
				break;
			case SORT_ON_GROUPLABEL:
				tm.put(dr.getFieldAttribute(k, "label") + k, k);
				break;
			case SORT_ON_RESULT:
				tmp = dr.getFieldAsNumber(k).toString();
				while (tmp.length() < 30)
					tmp = '0' + tmp;
				tm.put(tmp + k, k);
				// FIXME this is clumsy. Mind the decimals when filling up!
				break;
			case SORT_ON_GROUPFIELD_DESC:
				tm.put(invert(k), k);
				break;
			case SORT_ON_GROUPLABEL_DESC:
				tm.put(invert(dr.getFieldAttribute(k, "label") + k), k);
				break;
			case SORT_ON_RESULT_DESC:
				tmp = dr.getFieldAsNumber(k).toString();
				while (tmp.length() < 30)
					tmp = '0' + tmp;
				tm.put(invert(tmp + k), k);
				break;
			}
		}
		Iterator<String> it2 = tm.keySet().iterator();
		while (it2.hasNext()) {
			String k = tm.get(it2.next());
			drn.setFieldValue(k, dr.getFieldType(k), dr.getFieldValue(k));
			drn.setFieldAttribute(k, "label", dr.getFieldAttribute(k, "label"));
		}

		return drn;

	}

	private String invert(String in) {
		String out = "";
		byte[] b = in.getBytes();
		for (int i = 0; i < b.length; i++) {
			out += 255 - b[i];
		}
		return out;
	}

	public DataRow sumByGroup(String fieldname, String sumfieldname) throws Exception {
		return this.sumByGroup(fieldname, fieldname, sumfieldname, NO_SORT, 0);
	}

	public DataRow sumByGroup(String fieldname, String labelname, String sumfieldname, int sort, int top)
			throws Exception {
		Iterator<DataRow> it = this.iterator();
		DataRow dr = new DataRow();
		DataRow d;
		Double tmp, tmp1;
		String field, label;
		while (it.hasNext()) {
			d = it.next();
			field = "(-)";
			label = "(-)";
			try {
				field = d.getFieldAsString(fieldname);
				label = d.getFieldAsString(labelname);
			} catch (Exception e) {
			} finally {
			}
			tmp = 0.0;
			tmp1 = 0.0;
			try {
				tmp = d.getFieldAsNumber(sumfieldname);
			} catch (Exception e) {
			} finally {
			}

			try {
				tmp1 = dr.getFieldAsNumber(field);
			} catch (Exception e) {
			} finally {
			}
			dr.setFieldValue(field, tmp + tmp1);
			dr.setFieldAttribute(field, "label", label);
		}

		if (sort > 0) {
			dr = sortDataRow(dr, sort);
		}

		if (top > 0) {
			BBArrayList map = dr.getFieldNames();
			while (map.size() > top) {
				int i = map.size() - 1;
				String f = (String) map.get(i);
				dr.removeField(f);
				map.remove(i);
			}
		}

		return dr;
	}

	// load row key bytes in order of key columns
	public void buildRowKey(java.sql.ResultSet rs, DataRow dr)
			throws IllegalArgumentException, IndexOutOfBoundsException, NoSuchFieldException, SQLException {

		String stringVal = "";
		byte[] bytes;

		if (KeyColumns != null && KeyColumns.size() > 0) {
			if (KeyTemplate == null) {
				KeyTemplate = TemplateInfo.createTemplate(getKeyTemplate());
			} else {
				KeyTemplate.clear();
			}
			Iterator<String> it = KeyColumns.iterator();
			while (it.hasNext()) {
				String colName = it.next();
				int col = getColumnIndex(colName);
				Integer colType = getColumnType(col);
				col++; // java.sql.ResultSet 1-based
				switch (colType) {
				case java.sql.Types.NULL: // C(1)
					KeyTemplate.setString(colName, "");
					break;
				case java.sql.Types.CHAR: // C(n)
				case java.sql.Types.VARCHAR: // C(n*)
				case java.sql.Types.LONGVARCHAR: // C(n*)
					stringVal = rs.getString(col);
					if (stringVal != null)
						KeyTemplate.setString(colName, stringVal);
					break;
				case java.sql.Types.NCHAR: // C(n)
				case java.sql.Types.NVARCHAR: // C(n+=10)
				case java.sql.Types.LONGNVARCHAR: // C(n+=10)
					stringVal = rs.getNString(col);
					if (stringVal != null)
						KeyTemplate.setString(colName, stringVal);
					break;
				case java.sql.Types.INTEGER: // I(4)/U(4)
					KeyTemplate.setInt(colName, rs.getInt(col));
					break;
				case java.sql.Types.TINYINT: // I(1)/U(1)
					KeyTemplate.setInt(colName, rs.getInt(col));
					break;
				case java.sql.Types.SMALLINT: // I(2)/U(2)
					KeyTemplate.setInt(colName, rs.getShort(col));
					break;
				case java.sql.Types.BIGINT: // I(8)/U(8)
					KeyTemplate.setLong(colName, rs.getLong(col));
					break;
				case java.sql.Types.DECIMAL: // N(n*)/N(n*=)
				case java.sql.Types.NUMERIC: // N(n*)/N(n*=)
					java.math.BigDecimal decVal = rs.getBigDecimal(col);
					if (decVal != null) {
						decVal = decVal.setScale(15, java.math.BigDecimal.ROUND_HALF_EVEN);
						KeyTemplate.setBasisNumber(colName, new BasisNumber(decVal));
					}
					break;
				case java.sql.Types.DOUBLE: // Y
				case java.sql.Types.FLOAT: // F
					KeyTemplate.setDouble(colName, rs.getDouble(col));
					break;
				case java.sql.Types.REAL: // B
					KeyTemplate.setFloat(colName, rs.getFloat(col));
					break;
				case java.sql.Types.DATE: // I(4) Julian
					java.sql.Date dateVal = rs.getDate(col);
					if (dateVal == null)
						KeyTemplate.setInt(colName, -1);
					else
						KeyTemplate.setInt(colName, com.basis.util.BasisDate.jul(dateVal));
					break;
				case java.sql.Types.TIME: // C(23)
					java.sql.Time time = rs.getTime(col);
					if (time != null)
						KeyTemplate.setString(colName, time.toString());
					break;
				case java.sql.Types.TIMESTAMP: // C(23)
					java.sql.Timestamp timestamp = rs.getTimestamp(col);
					if (timestamp != null)
						KeyTemplate.setString(colName, timestamp.toString());
					break;
				case java.sql.Types.BINARY: // O(n)
				case java.sql.Types.VARBINARY: // O(n)
				case java.sql.Types.LONGVARBINARY: // O(n)
					bytes = rs.getBytes(col);
					if (bytes != null)
						KeyTemplate.setBytes(colName, bytes);
					break;
				case java.sql.Types.BLOB: // O(n)
					Blob blob = rs.getBlob(col);
					if (blob != null) {
						int len = (int) blob.length();
						KeyTemplate.setBytes(colName, blob.getBytes(1, len));
					}
					break;
				case java.sql.Types.BIT: // N(1)
				case java.sql.Types.BOOLEAN: // N(1)
					KeyTemplate.setBasisNumber(colName, rs.getBoolean(col) ? BasisNumber.ONE : BasisNumber.ZERO);
					break;
				case java.sql.Types.CLOB: // C(n+=10)
					Clob clob = rs.getClob(col);
					if (clob != null) {
						int len = (int) clob.length();
						KeyTemplate.setString(colName, clob.getSubString(1, len));
					}
					break;
				case java.sql.Types.NCLOB: // C(n+=10)
					NClob nclob = rs.getNClob(col);
					if (nclob != null) {
						int len = (int) nclob.length();
						KeyTemplate.setString(colName, nclob.getSubString(1, len));
					}
					break;
				case java.sql.Types.DATALINK: // C(n*)
					java.net.URL url = rs.getURL(col);
					if (url != null)
						KeyTemplate.setString(colName, url.toString());
					break;
				case java.sql.Types.ARRAY: // O(n)
					java.sql.Array array = rs.getArray(col);
					if (array != null) {
						// TODO
					}
					break;
				case java.sql.Types.JAVA_OBJECT: // O(n)
				case java.sql.Types.OTHER: // O(n)
					java.lang.Object object = rs.getObject(col);
					if (object != null) {
						// TODO
					}
					break;
				case java.sql.Types.REF: // O(n)
					java.sql.Ref ref = rs.getRef(col);
					if (ref != null) {
						// TODO
					}
					break;
				case java.sql.Types.DISTINCT: // O(n)
				case java.sql.Types.STRUCT: // O(n)
				case java.sql.Types.ROWID: // O(n)
				case java.sql.Types.SQLXML: // O(n)
				default: // O(n)
					bytes = rs.getBytes(col);
					if (bytes != null)
						KeyTemplate.setBytes(colName, bytes);
					break;
				}
			}
			dr.addBytesToRowKey(KeyTemplate.getBytes());
		}
	}

	/**
	 * Creates and returns simplified BB template definition based on the key
	 * columns
	 *
	 * @return String Key template definition
	 */
	private String getBBKeyTemplate() {
		return getBBKeyTemplate(false);
	}

	/**
	 * Creates and returns BB template definition based on the key columns
	 *
	 * @param extendedInfo
	 *            Adds more information to template if true
	 * @return String Key template definition
	 */
	private String getBBKeyTemplate(Boolean extendedInfo) {
		StringBuffer s = new StringBuffer();
		if (KeyColumns != null && KeyColumns.size() > 0) {
			Iterator<String> it = KeyColumns.iterator();
			while (it.hasNext()) {
				String colName = it.next();
				if (s.length() > 0)
					s.append(",");
				String tmplCol = getBBTemplateColumn(colName, -1, extendedInfo);
				// key template segments are always fixed length, so..
				if (!tmplCol.isEmpty() && (tmplCol.contains("*") || tmplCol.contains("+"))) {
					tmplCol = tmplCol.substring(0, java.lang.Math.max(tmplCol.indexOf("*"),tmplCol.indexOf("+")));
					tmplCol = tmplCol.concat(")");
				}
				s.append(tmplCol);
			}
		}
		return s.toString();
	}

	/**
	 * Creates and returns simplified BB template definition based on result set
	 * metadata, analog SQLTMPL().
	 *
	 * @return String Template definition
	 */
	public String getBBTemplate() {
		return getBBTemplate(false);
	}

	/**
	 * Creates and returns BB template definition based on ResultSetMeatData,
	 * analog SQLTMPL().
	 *
	 * @param extendedInfo
	 *            Adds more information to template if true
	 * @return String Template definition
	 */
	public String getBBTemplate(Boolean extendedInfo) {
		StringBuffer s = new StringBuffer();
		int cols = getColumnCount();
		if (cols > 0) {
			for (int col = 0; col < cols; col++) {
				if (col > 0)
					s.append(",");
				s.append(getBBTemplateColumn(col, cols, extendedInfo));
			}
		}
		return s.toString();
	}

	/**
	 * Creates and returns BB template definition for named column
	 *
	 * @param colName
	 *            Name of affected column
	 * @param cols
	 *            Total columns (used in checking if at end of record)
	 * @param extendedInfo
	 *            Adds more information to template if true
	 * @return String Column template definition
	 */
	private String getBBTemplateColumn(String colName, int cols, Boolean extendedInfo) {
		int col = getColumnIndex(colName);
		return getBBTemplateColumn(col, cols, extendedInfo);
	}

	/**
	 * Creates and returns BB template definition for indexed column
	 *
	 * @param col
	 *            Zero-based index of affected column
	 * @param cols
	 *            Total columns (used in checking if at end of record)
	 * @param extendedInfo
	 *            Adds more information to template if true
	 * @return String Column template definition
	 */
	private String getBBTemplateColumn(int col, int cols, Boolean extendedInfo) {
		StringBuffer s = new StringBuffer();
		String colName = getColumnName(col);
		s.append(colName).append(":");
		Integer prec = getPrecision(col);
		Integer scale = java.lang.Math.max(0, getScale(col));
		Boolean isNum = false;
		Integer colType = getColumnType(col);
		String colTypeName = getColumnTypeName(col);
		if (colTypeName == null)
			colTypeName = "";
		switch (colType) {
		case java.sql.Types.NULL:
			s.append("C(1)");
			if (extendedInfo)
				s.append(":sqltype=NULL");
			break;
		case java.sql.Types.CHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(")");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=CHAR");
			break;
		case java.sql.Types.VARCHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("*)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=VARCHAR");
			break;
		case java.sql.Types.LONGVARCHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("*)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=LONGVARCHAR");
			break;
		case java.sql.Types.NCHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append(")");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NCHAR");
			break;
		case java.sql.Types.NVARCHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("+=10)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NVARCHAR");
			break;
		case java.sql.Types.LONGNVARCHAR:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("+=10)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=LONGNVARCHAR");
			break;
		case java.sql.Types.INTEGER:
			if (isSigned(col)) {
				s.append("I(4)");
			} else {
				s.append("U(4)");
			}
			if (extendedInfo)
				s.append(":sqltype=INTEGER");
			break;
		case java.sql.Types.TINYINT:
			if (isSigned(col)) {
				s.append("I(1)");
			} else {
				s.append("U(1)");
			}
			if (extendedInfo)
				s.append(":sqltype=TINYINT");
			break;
		case java.sql.Types.SMALLINT:
			if (isSigned(col)) {
				s.append("I(2)");
			} else {
				s.append("U(2)");
			}
			if (extendedInfo)
				s.append(":sqltype=SMALLINT");
			break;
		case java.sql.Types.BIGINT:
			if (isSigned(col)) {
				s.append("I(8)");
			} else {
				s.append("U(8)");
			}
			if (extendedInfo)
				s.append(":sqltype=BIGINT");
			break;
		case java.sql.Types.BIT:
			s.append("N(1)");
			if (extendedInfo)
				s.append(":sqltype=BIT");
			break;
		case java.sql.Types.BOOLEAN:
			s.append("N(1)");
			if (extendedInfo)
				s.append(":sqltype=BOOLEAN");
			break;
		case java.sql.Types.DECIMAL:
			s.append("N(" + String.valueOf(getColumnDisplaySize(col)) + ((col == cols - 1) ? "*=)" : "*)"));
			if (extendedInfo)
				s.append(":sqltype=DECIMAL size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.NUMERIC:
			s.append("N(" + String.valueOf(getColumnDisplaySize(col)) + ((col == cols - 1) ? "*=)" : "*)"));
			if (extendedInfo)
				s.append(":sqltype=NUMERIC size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.DOUBLE:
			s.append("Y");
			if (extendedInfo)
				s.append(":sqltype=DOUBLE size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.FLOAT:
			s.append("F");
			if (extendedInfo)
				s.append(":sqltype=FLOAT size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.REAL:
			s.append("B");
			if (extendedInfo)
				s.append(":sqltype=REAL size=").append(prec.toString()).append(" scale=" + scale.toString());
			isNum = true;
			break;
		case java.sql.Types.DATE:
			s.append("I(4)");
			if (extendedInfo)
				s.append(":sqltype=DATE");
			break;
		case java.sql.Types.TIME:
			s.append("C(23)");
			if (extendedInfo)
				s.append(":sqltype=TIME");
			break;
		case java.sql.Types.TIMESTAMP:
			s.append("C(23)");
			if (extendedInfo)
				s.append(":sqltype=TIMESTAMP");
			break;
		case java.sql.Types.BINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=BINARY");
			break;
		case java.sql.Types.VARBINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=VARBINARY");
			break;
		case java.sql.Types.LONGVARBINARY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=LONGVARBINARY");
			break;
		case java.sql.Types.BLOB:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=BLOB");
			break;
		case java.sql.Types.CLOB:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("+=10)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=CLOB");
			break;
		case java.sql.Types.NCLOB:
			if (prec <= 0) {
				s.append("C(1*)");
			} else {
				if (prec <= 32767) {
					s.append("C(").append(prec.toString()).append("+=10)");
				} else {
					s.append("C(32767+=10)");
				}
			}
			if (extendedInfo)
				s.append(":sqltype=NCLOB");
			break;
		case 9: // ODBC Date
			s.append("C(10)");
			if (extendedInfo)
				s.append(":sqltype=ODBC_DATE");
			break;
		case 11: // ODBC Timestamp
			s.append("C(19)");
			if (extendedInfo)
				s.append(":sqltype=ODBC_TIMESTAMP");
			break;
		case java.sql.Types.ARRAY:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=ARRAY");
			break;
		case java.sql.Types.JAVA_OBJECT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=JAVA_OBJECT");
			break;
		case java.sql.Types.OTHER:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=OTHER");
			break;
		case java.sql.Types.REF:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=REF");
			break;
		case java.sql.Types.DATALINK: // (think URL)
			s.append("C(").append(prec.toString()).append("*)");
			if (extendedInfo)
				s.append(":sqltype=DATALINK");
			break;
		case java.sql.Types.DISTINCT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=DISTINCT");
			break;
		case java.sql.Types.STRUCT:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=STRUCT");
			break;
		case java.sql.Types.ROWID:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=ROWID");
			break;
		case java.sql.Types.SQLXML:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=SQLXML");
			break;
		default:
			if (prec > 0) {
				prec = java.lang.Math.min(prec, 32767);
			} else {
				prec = 32767;
			}
			s.append("O(").append(prec.toString()).append(")");
			if (extendedInfo)
				s.append(":sqltype=UNKNOWN");
			break;
		}
		if (extendedInfo) {
			if (colTypeName != "") {
				s.append(" dbtype=").append(colTypeName);
			}
			if (isAutoIncrement(col)) {
				s.append(" auto_increment=1");
			}
			if (isReadOnly(col)) {
				s.append(" read_only=1");
			}
			if (isCaseSensitive(col)) {
				s.append(" case_sensitive=1");
			}
			if (isSigned(col) && isNum) {
				s.append(" signed=1");
			}
			if (isNullable(col) == java.sql.ResultSetMetaData.columnNoNulls) {
				s.append(" required=1");
			}
			s.append(":");
		}
		return s.toString();
	}

	public void print() {

		System.out.println("-------------------ResultSet-----------------------------");
		Iterator<DataRow> it = this.iterator();
		while (it.hasNext()) {

			DataRow row = it.next();
			System.out.println(row);
		}

		System.out.println("-------------------ResultSet End-------------------------");

	}

}