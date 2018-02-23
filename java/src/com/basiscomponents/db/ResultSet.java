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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;

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

/**
 * The ResultSet class is a container class for DataRow objects.
 */
public class ResultSet implements java.io.Serializable, Iterable<DataRow> {

	private static final long serialVersionUID = 1L;

	public static final int NO_SORT = 0;
	public static final int SORT_ON_GROUPFIELD = 1;
	public static final int SORT_ON_GROUPLABEL = 2;
	public static final int SORT_ON_RESULT = 3;
	public static final int SORT_ON_GROUPFIELD_DESC = 11;
	public static final int SORT_ON_GROUPLABEL_DESC = 12;
	public static final int SORT_ON_RESULT_DESC = 13;

	@Expose
	private ArrayList<HashMap<String, Object>> MetaData = new ArrayList<>();
	@Expose
	private ArrayList<String> ColumnNames = new ArrayList<>();
	@Expose
	private ArrayList<DataRow> DataRows = new ArrayList<>();
	private ArrayList<String> FieldSelection;

	private ArrayList<String> KeyColumns = new ArrayList<>();
	private String KeyTemplateString = "";
	private Template KeyTemplate = null;

	private int currentRow = -1;
	private DataRow currentDataRow;

	public static HashMap<Integer, String> SQLTypeNameMap = new HashMap<>();

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


	/**
	 * re-orders a result set according to an ORDER BY clause like in SQL ORDER BY:
	 * e.g. (ORDER BY) NAME, FIRST_NAME DESC, ZIP
	 * NOTE: Only ASC and DESC are allowed!
	 * @param orderByClause: order by clause
	 * @return the ordered result set
	 * @throws Exception
	 */
	public ResultSet orderBy(String orderByClause) throws Exception {
		ResultSet rs = this.clone();
		rs.orderByColumn(new DataRowMultifieldComparator(orderByClause));
		return rs;
	}

	/**
	 * Re-orders the ResultSet based on the given field name and the given sort direction.
	 * The sort direction can only be ASC(=Ascending) or DESC(=Descending). If any value is passed which is not "ASC" or "DESC",
	 * the methods defaults to the ascending sorting.
	 * 
	 * @param fieldName The field name on which to sort the ResultSet
	 * @param direction The sort direction("ASC" or "DESC")
	 */
	public void orderByColumn(String fieldName, String direction) {
		if (!"ASC".equalsIgnoreCase(direction) && !"DESC".equalsIgnoreCase(direction))
			direction = "ASC";

		java.util.Comparator<DataRow> comparator = new DataRowComparator(fieldName);
		if ("DESC".equalsIgnoreCase(direction))
			comparator = comparator.reversed();

		DataRows.sort(comparator);
	}

	/**
	 * Re-order the ResultSet using the given Comparator object.
	 * 
	 * @param comparator The comparator used to re-order the ResultSet.
	 */
	public void orderByColumn(java.util.Comparator<DataRow> comparator) {
		DataRows.sort(comparator);
	}

	/**
	 * Re-orders the ResultSet based on the Row ID's.
	 * This methods can be used to revert the ResultSet's order back to its default.
	 * <br><br>
	 * <b>Note: </b>The method does nothing in case the ResultSet hasn't been reordered.
	 */
	public void orderByRowID() {
		DataRows.sort(java.util.Comparator.comparing(DataRow::getRowID));
	}

	/**
	 * Applies the queryClause to a ResultSet and returns a new ResultSet that only contains
	 * records that match the clause. The clause syntax is similar to an SQL WHERE clause.
	 * 
	 * CAUTION: this method is experimental!!
	 * 
	 * @param QueryClause: the query
	 * @return ResultSet: the records that match the query clause
	 * @throws Exception
	 */
	public ResultSet filterBy(String QueryClause) throws Exception{
		return filterBy(QueryClause, true, false); // this was the default behavior
	}

	/**
	 * Applies the queryClause to a ResultSet and returns a new ResultSet that only contains
	 * records that match the clause. The clause syntax is similar to an SQL WHERE clause.
	 * 
	 * CAUTION: this method is experimental!!
	 * 
	 * @param QueryClause: the query
	 * @param caseSensitive whether the filter query should be case sensitive or not
	 * @param trimmed {@code true} if the query paramters should get trimmed
	 * @return ResultSet: the records that match the query clause
	 * @throws Exception
	 */
	public ResultSet filterBy(String QueryClause, final boolean caseSensitive, final boolean trimmed) throws Exception{
		System.out.println("WARNING: using experimental method implementation filterBy clause on ResultSet");
		ResultSet r = new ResultSet(this.MetaData, this.ColumnNames, this.KeyColumns);
		Iterator<DataRow> it = this.iterator();
		while (it.hasNext()) {
			DataRow dr = it.next();
			if (DataRowQueryMatcher.matches(QueryClause, dr, caseSensitive, trimmed))
				r.add(dr);
		}
		return r;
	}

	/**
	 * Search for DataRow(s) containing a specific value(s)
	 * Returns a new ResultSet with the DataRow(s) found in the current instance.
	 * 
	 * If the type of field in simpleFilterCondition is 12 (String) and the value starts with "regex:" then a regular search will be applied
	 * 
	 * @param simpleFilterCondition A DataRow with the vlaues to search for.
	 * 
	 * @return a ResultSet with the DataRows found in the current instance.
	 */
	public ResultSet filterBy(DataRow simpleFilterCondition) throws Exception {
		ResultSet resultSet = new ResultSet(this.MetaData, this.ColumnNames, this.KeyColumns);
		if (size() == 0) {
			return resultSet;
		}
		// Check for "cond:" filters. If found, then create new ExpressionMatcher object's per field.
		HashMap<String, Comparator<DataRow>> comparatorMap = new HashMap<>();
		HashMap<String, ExpressionMatcher> matcherMap = new HashMap<>();
		BBArrayList<String> filterFields = simpleFilterCondition.getFieldNames();
		DataRow dataRow = get(0);
		for (String filterFieldName : filterFields) {
			if (dataRow.contains(filterFieldName)) {
				DataField filterField = simpleFilterCondition.getField(filterFieldName);
				if (filterField.getValue() != null && filterField.getString().startsWith("cond:")) {
					comparatorMap.put(filterFieldName, 
							new DataRowComparator(filterFieldName));
					matcherMap.put(filterFieldName, 
							new ExpressionMatcher(
									filterField.getString().substring(5), // after "cond:"
									dataRow.getFieldType(filterFieldName), 
									filterFieldName));
				}
			}
		}

		Iterator<DataRow> dataRowIterator = this.iterator();
		while (dataRowIterator.hasNext()) {
			DataRow dataRow = dataRowIterator.next();
			Iterator<String> filterFieldsIterator = filterFields.iterator();
			boolean match = true;
			while (match && filterFieldsIterator.hasNext()) {
				String filterFieldKey = filterFieldsIterator.next();
				DataField cond = simpleFilterCondition.getField(filterFieldKey);
				match = matchesCondition(comparatorMap, matcherMap, dataRow, filterFieldKey, cond);
			}
			if (match) {
				resultSet.add(dataRow);
			}
		}
		return resultSet;
	}

	private boolean matchesCondition(HashMap<String, Comparator<DataRow>> comparatorMap,
			HashMap<String, ExpressionMatcher> matcherMap, DataRow dataRow, String filterFieldKey, DataField cond)
			throws Exception {
		boolean match = true;
		if (dataRow.getFieldType(filterFieldKey) == 12 && cond.getString().startsWith("regex:")) {
			match = dataRow.getFieldAsString(filterFieldKey).matches(cond.getString().substring(6));
		} else if (matcherMap.containsKey(filterFieldKey)) {
			Comparator<DataRow> comparator = comparatorMap.get(filterFieldKey);
			ExpressionMatcher matcher = matcherMap.get(filterFieldKey);
			match = matcher.match(comparator, dataRow, filterFieldKey);
		} else {
			DataField comp = dataRow.getField(filterFieldKey);
			match = cond.equals(comp);
		}
		return match;
	}

	/**
	 * Initializes the ResultSet using the metadata and Data from the given java.sql.ResultSet.
	 * 
	 * @param rs The java.sql.ResultSet used to initialize the ResultSet object.
	 */
	public ResultSet(java.sql.ResultSet rs) {
		this();
		try {
			populate(rs, true);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Iterates over the given java.sql.Result object and adds a DataRow object for each record
	 * of it to this ResultSet.
	 * 
	 * If the defaultMetaData flag is set to true, the ResultSet will use the metadata from the given java.sql.ResultSet object.
	 * If it is set to false, no metadata will be created. If the resultSet already has some metadata defined, it will not be removed.
	 * 
	 * In case the field selection list has been set, this method will only create DataRow object's with the
	 * fields from the field selection list.
	 * 
	 * @param rs
	 * @param defaultMetaData
	 * @throws Exception
	 */
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

		column = 0;
		HashMap<String, HashMap<String, String>> fieldAttributes = new HashMap<String, HashMap<String, String>>();
		for (HashMap.Entry<Integer, String> entry : columns.entrySet()) {
			name = entry.getValue();
			HashMap<String, String> attribute = new HashMap<String, String>();

			if (this.MetaData.get(column).get("ColumnTypeName").equals("JSON"))
				attribute.put("StringFormat", "JSON");

			if (KeyColumns != null && KeyColumns.contains(name)) {
				attribute.put("EDITABLE", "2");
			}
			else {
				if (this.isAutoIncrement(column) || !this.isWritable(column) || !this.isDefinitelyWritable(column))
					attribute.put("EDITABLE", "0");
				else
					attribute.put("EDITABLE", "1");
			}

			fieldAttributes.put(name, attribute);

			column++;
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
				type = defaultMetaData? types.get(column - 1) : getColumnType(column - 1);
//				field.setAttributes(new HashMap<String, String>(fieldAttributes.get(name)));
				dr.addDataField(name, type, field);
			}

			if (KeyColumns != null && KeyColumns.size() > 0) {
				try {
					buildRowKey(rs, dr);
				} catch (Exception e) {}
			}

			dr.setRowID(rowId);
			this.DataRows.add(dr);
			rowId++;
		}

		// Add meta data to the first row only
		if (DataRows.size() > 0 && fieldAttributes.size() > 0) {
			DataRow dr = DataRows.get(0);
			Iterator<String> it = dr.getFieldNames().iterator();
			while (it.hasNext()) {
				String fieldName = it.next();
				dr.setFieldAttributes(fieldName, new HashMap<String, String>(fieldAttributes.get(fieldName)));
			}
		}
	}

	/**
	 * Merges the fields of the given DataRow to all DataRows present in this ResultSet object.
	 * 
	 * @param dr The DataRow whose field will be merged into the DataRows from this ResultSet.
	 */
	private void mergeDataRowFields(DataRow dr) {
		BBArrayList<String> names = dr.getFieldNames();
		Iterator<String> it = names.iterator();
		while (it.hasNext()) {
			String name = it.next();
			if (!this.ColumnNames.contains(name)) {
				int column = this.addColumn(name);
				try {
					this.setColumnType(column, dr.getFieldType(name));
				} catch (Exception e) {
					// Auto-generated catch block
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
					// Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Adds the given DataRow object to the ResultSet.
	 * 
	 * @param dr The DataRow to add.
	 */
	public void add(DataRow dr) {
		this.DataRows.add(dr);
		this.mergeDataRowFields(dr);
	}

	/**
	 * Inserts the given DataRow object at the specified index in the ResultSet.
	 * 
	 * @param row The index were to insert the DataRow.
	 * @param dr The DataRow to insert.
	 */
	public void add(int row, DataRow dr) {
		this.DataRows.add(row, dr);
		this.mergeDataRowFields(dr);
	}

	/**
	 * Adds the given DataRow object to the ResultSet.
	 * 
	 * @param dr The DataRow to add.
	 */
	public void addItem(DataRow dr) {
		add(dr);
		this.mergeDataRowFields(dr);
	}

	/**
	 * Inserts the given DataRow object at the specified index in the ResultSet.
	 * 
	 * @param row The index were to insert the DataRow.
	 * @param dr The DataRow to insert.
	 */
	public void insertItem(int row, DataRow dr) {
		add(row, dr);
		this.mergeDataRowFields(dr);
	}

	/**
	 * Adds a column with the specified name to the list of columns.
	 * 
	 * @param name The name of the column.
	 * 
	 * @return the index of the newly added column.
	 */
	public int addColumn(String name) {
		HashMap<String, Object> colMap = new HashMap<String, Object>();
		colMap.put("ColumnName", name);
		this.ColumnNames.add(name);
		this.MetaData.add(colMap);
		return this.MetaData.size() - 1;
	}

	/**
	 * Adds a column with the given name and the given metadata to the list of columns.
	 * 
	 * @param name The name of the column.
	 * @param colMap The metadata for the column.
	 * 
	 * @return the index of the newly added column.
	 */
	public int addColumn(String name, HashMap<String, Object> colMap) {
		colMap.put("ColumnName", name);
		this.ColumnNames.add(name);
		this.MetaData.add(colMap);
		return this.MetaData.size() - 1;
	}

	/**
	 * Sets the given metadata to the column with the specified name.
	 * 
	 * @param name The name of the column.
	 * @param colMap The metadata to set.
	 */
	public void setColumnMetaData(String name, HashMap<String, Object> colMap) {
		int column = getColumnIndex(name);
		if (column != -1)
			this.MetaData.set(column, colMap);
	}

	/**
	 * Returns the metadata of the column with the specified name.
	 * 
	 * @param name The name of the column.
	 * 
	 * @return the column's metadata.
	 */
	public HashMap<String, Object> getColumnMetaData(String name) {
		int column = getColumnIndex(name);
		if (column != -1)
			return this.MetaData.get(column);
		else
			return null;
	}

	/**
	 * Returns the index of the column with the specified name, or -1
	 * in case no column exists for the given name.
	 * 
	 * @param name The name of the column.
	 * 
	 * @return the column's index or -1 if the column doesn't exist.
	 */
	public int getColumnIndex(String name) {
		return this.ColumnNames.indexOf(name);
	}

	/**
	 * Returns a list with all column names.
	 * 
	 * @return the list with all column names.
	 */
	public ArrayList<String> getColumnNames() {
		return this.ColumnNames;
	}

	/**
	 * Returns a list with all key columns.
	 * 
	 * @return the list with the key columns.
	 */
	public ArrayList<String> getKeyColumns() {
		return this.KeyColumns;
	}

	/**
	 * @param keyColumns the keyColumns to set
	 */
	public void setKeyColumns(ArrayList<String> keyColumns) {
		this.KeyColumns = keyColumns;
	}

	/**
	 * Sets the given column name as key column.
	 * 
	 * @param name the name of the key column to add.
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
	 * Returns the row key of the current DataRow object.
	 * 
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

	/**
	 * Clears the ResultSet by removing all DataRow objects.
	 */
	public void clear() {
		this.DataRows.clear();
	}

	/**
	 * Returns the DataRow at the given index.
	 * 
	 * @param row The index of the DataRow.
	 * 
	 * @return The DataRow at the specified index.
	 */
	public DataRow get(int row) {
		return this.DataRows.get(row);
	}

	/**
	 * Returns the DataRow at the given index.
	 * 
	 * @param row The index of the DataRow.
	 * 
	 * @return The DataRow at the specified index.
	 */
	public DataRow getItem(int row) {
		return get(row);
	}

	/**
	 * Sets the DataRow at the given index.
	 * 
	 * @param row The index of the DataRow.
	 * @param dr The DataRow to set.
	 */
	public void set(int row, DataRow dr) {
		this.DataRows.set(row, dr);
	}

	/**
	 * Sets the DataRow at the given index.
	 * 
	 * @param row The index of the DataRow.
	 * @param dr The DataRow to set.
	 */
	public void setItem(int row, DataRow dr) {
		set(row, dr);
	}

	/**
	 * Returns the current DataRow.
	 * 
	 * @return the current DataRow object.
	 */
	public int getRow() {
		return this.currentRow;
	}

	/**
	 * Returns true if the ResultSet doesn't contain any DataRow, false otherwise.
	 * 
	 * @return true if the ResultSet doesn't have any DataRows, false otherwise.
	 */
	public Boolean isEmpty() {
		return this.DataRows.isEmpty();
	}

	/**
	 * Removes the DataRow at the specified index, and shifts any
	 * subsequent DataRows to the left.
	 * 
	 * @param row The index of the DataRow to remove.
	 * 
	 * @return The DataRow that was removed.
	 */
	public DataRow remove(int row) {
		if (this.currentRow == row) {
			this.currentRow -= 1;
			this.currentDataRow = null;
		}
		return this.DataRows.remove(row);
	}

	/**
	 * Removes the DataRow at the specified index, and shifts any
	 * subsequent DataRows to the left.
	 * 
	 * @param row The index of the DataRow to remove.
	 * 
	 * @return The DataRow that was removed.
	 */
	public DataRow removeItem(int row) {
		return remove(row);
	}

	/**
	 * Removes the column at the given index.
	 * 
	 * @param column The column index
	 */
	public void removeColumn(int column) {
		this.ColumnNames.remove(column);
		this.MetaData.remove(column);
	}

	/**
	 * Returns the number of DataRows stored in the ResultSet.
	 * 
	 * @return The number of DataRow objects stored in the ResultSet.
	 */
	public int size() {
		return this.DataRows.size();
	}

	/**
	 * Moves the cursor before the first DataRow element.
	 */
	// Navigation methods (0-based)
	public void beforeFirst() {
		this.currentRow = -1;
		this.currentDataRow = null;
	}

	/**
	 * Moves the cursor after the last DataRow element.
	 */
	public void afterLast() {
		this.currentRow = this.DataRows.size();
		this.currentDataRow = null;
	}

	/**
	 * Moves the cursor to the first DataRow element of this ResultSet.
	 * Returns true if the cursor was successfully moved at the first element, false otherwise.
	 * 
	 * @return true if the cursor was successfully moved to the first element, false otherwise.
	 */
	public Boolean first() {
		if (this.DataRows.isEmpty())
			return false;
		else {
			this.currentRow = 0;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	/**
	 * Moves the cursor to the last DataRow element of this ResultSet.
	 * Returns true if the cursor was successfully moved at the last element, false otherwise.
	 * 
	 * @return true if the cursor was successfully moved to the last element, false otherwise.
	 */
	public Boolean last() {
		if (this.DataRows.isEmpty())
			return false;
		else {
			this.currentRow = this.DataRows.size() - 1;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	/**
	 * Moves the cursor to the DataRow element at the specified index.
	 * Returns true if the cursor was successfully moved to the specified index, false otherwise.
	 * 
	 * @return true if the cursor was successfully moved to the specified index, false otherwise.
	 */
	public Boolean absolute(int row) {
		if (this.DataRows.isEmpty() || row < 0 || row > this.DataRows.size() - 1)
			return false;
		else {
			this.currentRow = row;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	/**
	 * Moves the cursor to the next DataRow element. Returns true
	 * if the cursor was moved successfully, false otherwise.
	 * 
	 * @return true if the cursor was moved successfully, false otherwise.
	 */
	public Boolean next() {
		if (this.DataRows.isEmpty() || this.currentRow > this.DataRows.size() - 2)
			return false;
		else {
			this.currentRow += 1;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	/**
	 * Returns the number of columns.
	 * 
	 * @return the number of columns.
	 */
	// MetaData get methods (0-based)
	public int getColumnCount() {
		return this.MetaData.size();
	}

	/**
	 * Returns the value of the CatalogName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the CatalogName property of the column.
	 */
	public String getCatalogName(int column) {
		String name = (String) this.MetaData.get(column).get("CatalogName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the ColumnClassName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnClassName property of the column.
	 */
	public String getColumnClassName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnClassName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the ColumnDisplaySize property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnDisplaySize property of the column.
	 */
	public int getColumnDisplaySize(int column) {
		Integer size = (Integer) this.MetaData.get(column).get("ColumnDisplaySize");
		if (size == null)
			size = 0;
		return size.intValue();
	}

	/**
	 * Returns the value of the ColumnLabel property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnLabel property of the column.
	 */
	public String getColumnLabel(int column) {
		String label = (String) this.MetaData.get(column).get("ColumnLabel");
		if (label == null)
			label = "";
		return label;
	}

	/**
	 * Returns the value of the ColumnName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnName property of the column.
	 */
	public String getColumnName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the ColumnType property from the ResultSet's metadata,
	 * for the column at the specified index. Returns 0 in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnType property of the column.
	 */
	public int getColumnType(int column) {
		Integer type = (Integer) this.MetaData.get(column).get("ColumnType");
		if (type == null)
			type = 0;
		return type.intValue();
	}

	/**
	 * Returns the value of the ColumnTypeName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ColumnTypeName property of the column.
	 */
	public String getColumnTypeName(int column) {
		String name = (String) this.MetaData.get(column).get("ColumnTypeName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the Precision property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Precision property of the column.
	 */
	public int getPrecision(int column) {
		Integer prec = (Integer) this.MetaData.get(column).get("Precision");
		if (prec == null)
			prec = 0;
		return prec.intValue();
	}

	/**
	 * Returns the value of the Scale property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Scale property of the column.
	 */
	public int getScale(int column) {
		Integer scale = (Integer) this.MetaData.get(column).get("Scale");
		if (scale == null)
			scale = 0;
		return scale.intValue();
	}

	/**
	 * Returns the value of the SchemaName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the SchemaName property of the column.
	 */
	public String getSchemaName(int column) {
		String name = (String) this.MetaData.get(column).get("SchemaName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the TableName property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the TableName property of the column.
	 */
	public String getTableName(int column) {
		String name = (String) this.MetaData.get(column).get("TableName");
		if (name == null)
			name = "";
		return name;
	}

	/**
	 * Returns the value of the AutoIncrement property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the AutoIncrement property of the column.
	 */
	public Boolean isAutoIncrement(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("AutoIncrement");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the CaseSensitive property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the CaseSensitive property of the column.
	 */
	public Boolean isCaseSensitive(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("CaseSensitive");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the Currency property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Currency property of the column.
	 */
	public Boolean isCurrency(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Currency");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the DefinitelyWritable property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the DefinitelyWritable property of the column.
	 */
	public Boolean isDefinitelyWritable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("DefinitelyWritable");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the Nullable property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Nullable property of the column.
	 */
	public int isNullable(int column) {
		Integer nullable = (Integer) this.MetaData.get(column).get("Nullable");
		if (nullable == null)
			nullable = java.sql.ResultSetMetaData.columnNullableUnknown;
		return nullable.intValue();
	}

	/**
	 * Returns the value of the ReadOnly property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the ReadOnly property of the column.
	 */
	public Boolean isReadOnly(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("ReadOnly");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the Searchable property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Searchable property of the column.
	 */
	public Boolean isSearchable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Searchable");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the Signed property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Signed property of the column.
	 */
	public Boolean isSigned(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Signed");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the value of the Writable property from the ResultSet's metadata,
	 * for the column at the specified index. Returns an empty String in case the property isn't set.
	 * 
	 * @param column The column index.
	 * 
	 * @return The value of the Writable property of the column.
	 */
	public Boolean isWritable(int column) {
		Boolean flag = (Boolean) this.MetaData.get(column).get("Writable");
		if (flag == null)
			flag = false;
		return flag;
	}

	/**
	 * Returns the attribute with the given name, for the column at the specified index.
	 * Returns an empty String in case the specified attribute name doesn't exist.
	 * 
	 * @param column The column's index
	 * @param name The attribute's name
	 * 
	 * @return The attribute's value, or empty String in case the attribute doesn't exist.
	 */
	public String getAttribute(int column, String name) {
		String value = (String) this.MetaData.get(column).get(name);
		if (value == null)
			value = "";
		return value;
	}

	/**
	 * Sets the value of the CatalogName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param catalogName The value of the CatalogName property to set.
	 */
	// MetaData set methods (0-based)
	public void setCatalogName(int column, String catalogName) {
		this.MetaData.get(column).put("CatalogName", catalogName);
	}

	/**
	 * Sets the value of the ColumnClassName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param className The value of the ClassName property to set.
	 */
	public void setColumnClassName(int column, String className) {
		this.MetaData.get(column).put("ColumnClassName", className);
	}

	/**
	 * Sets the value of the ColumnDisplaySize property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param displaySize The value of the ColumnDisplaySize property to set.
	 */
	public void setColumnDisplaySize(int column, int displaySize) {
		this.MetaData.get(column).put("ColumnDisplaySize", displaySize);
	}

	/**
	 * Sets the value of the ColumnLabel property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param label The value of the ColumnLabel property to set.
	 */
	public void setColumnLabel(int column, String label) {
		this.MetaData.get(column).put("ColumnLabel", label);
	}

	/**
	 * Sets the value of the ColumnName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param name The value of the ColumnName property to set.
	 */
	public void setColumnName(int column, String name) throws Exception {
		if (name.isEmpty())
			throw new Exception("Column name may not be empty");
		this.MetaData.get(column).put("ColumnName", name);
	}

	/**
	 * Sets the value of the ColumnType property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param type The value of the ColumnType property to set.
	 */
	public void setColumnType(int column, int type) throws Exception {
		String typeName = getSQLTypeName(type);
		if (typeName == null)
			throw new Exception("Unknown column type" + String.valueOf(type));
		else
			setColumnTypeName(column, typeName);
		this.MetaData.get(column).put("ColumnType", type);
	}

	/**
	 * Sets the value of the ColumnTypeName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param typeName The value of the ColumnTypeName property to set.
	 */
	public void setColumnTypeName(int column, String typeName) {
		this.MetaData.get(column).put("ColumnTypeName", typeName);
	}

	/**
	 * Sets the value of the Precision property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param precision The value of the Precision property to set.
	 */
	public void setPrecision(int column, int precision) throws Exception {
		if (precision < getScale(column))
			throw new Exception("Precision must be >= scale");
		this.MetaData.get(column).put("Precision", precision);
	}

	/**
	 * Sets the value of the Scale property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param scale The value of the Scale property to set.
	 */
	public void setScale(int column, int scale) throws Exception {
		if (scale > getPrecision(column))
			throw new Exception("Scale must be <= precision");
		this.MetaData.get(column).put("Scale", scale);
	}

	/**
	 * Sets the value of the SchemaName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param schemaName The value of the SchemaName property to set.
	 */
	public void setSchemaName(int column, String schemaName) {
		this.MetaData.get(column).put("SchemaName", schemaName);
	}

	/**
	 * Sets the value of the TableName property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param name The value of the TableName property to set.
	 */
	public void setTableName(int column, String tableName) {
		this.MetaData.get(column).put("TableName", tableName);
	}

	/**
	 * Sets the value of the AutoIncrement property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the AutoIncrement property to set.
	 */
	public void setAutoIncrement(int column, Boolean flag) {
		this.MetaData.get(column).put("AutoIncrement", flag);
	}

	/**
	 * Sets the value of the CaseInsensitive property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the CaseInsensitive property to set.
	 */
	public void setCaseSensitive(int column, Boolean flag) {
		this.MetaData.get(column).put("CaseSensitive", flag);
	}

	/**
	 * Sets the value of the Currency property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the Currency property to set.
	 */
	public void setCurrency(int column, Boolean flag) {
		this.MetaData.get(column).put("Currency", flag);
	}

	/**
	 * Sets the value of the DefinitelyWritable property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the DefinitelyWritable property to set.
	 */
	public void setDefinitelyWritable(int column, Boolean flag) {
		this.MetaData.get(column).put("DefinitelyWritable", flag);
	}

	/**
	 * Sets the value of the DefinitelyWritable property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * <br><br>
	 * <b>Note: </b>The Nullable flag can only be one of the following values, else an Exception is thrown.
	 * <ul>
	 *     <li>{@link java.sql.ResultSetMetaData#columnNoNulls java.sql.ResultSetMetaData.columnNoNulls}</li>
	 *     <li>{@link java.sql.ResultSetMetaData#columnNullable java.sql.ResultSetMetaData.columnNullable}</li>
	 *     <li>{@link java.sql.ResultSetMetaData#columnNullableUnknown java.sql.ResultSetMetaData.columnNullableUnknown}</li>
	 * </ul>
	 * 
	 * @param column The column index.
	 * @param flag The value of the Nullable property to set..
	 * 
	 * @throws Exception Gets thrown in case the Nullable flag is not equal any of the above mentioned types.
	 */
	public void setNullable(int column, int nullable) throws Exception {
		if (nullable != java.sql.ResultSetMetaData.columnNoNulls
				&& nullable != java.sql.ResultSetMetaData.columnNullable
				&& nullable != java.sql.ResultSetMetaData.columnNullableUnknown)
			throw new Exception("Invalid nullable value" + String.valueOf(nullable));
		this.MetaData.get(column).put("Nullable", nullable);
	}

	/**
	 * Sets the value of the ReadOnly property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the ReadOnly property to set.
	 */
	public void setReadOnly(int column, Boolean flag) {
		this.MetaData.get(column).put("ReadOnly", flag);
	}

	/**
	 * Sets the value of the Searchable property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the Searchable property to set.
	 */
	public void setSearchable(int column, Boolean flag) {
		this.MetaData.get(column).put("Searchable", flag);
	}

	/**
	 * Sets the value of the Signed property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the Signed property to set.
	 */
	public void setSigned(int column, Boolean flag) {
		this.MetaData.get(column).put("Signed", flag);
	}

	/**
	 * Sets the value of the Writable property of the ResultSet's metadata to the given value,
	 * for the column at the specified index.
	 * 
	 * @param column The column index.
	 * @param flag The value of the Writable property to set.
	 */
	public void setWritable(int column, Boolean flag) {
		this.MetaData.get(column).put("Writable", flag);
	}

	/**
	 * Sets the attribute with the specified name and value to the column at the specified index.
	 * Overwrites the value of the attribute with the given one in case an attribute with the given name does already exist.
	 * 
	 * @param column The column's index.
	 * @param name The name of the attribute.
	 * @param value The value of the attribute.
	 */
	public void setAttribute(int column, String name, String value) {
		this.MetaData.get(column).put(name, value);
	}

	/**
	 * Returns the DataField object of the current DataRow for the specified column index.
	 * The method converts the column index to the field name and calls the {@link DataRow#getField(String) DataRow#getField(String)} method)
	 * 
	 * @param column The column's index.
	 * 
	 * @return the DataField object of the current DataRow for the given column index.
	 */
	public DataField getField(int column) {
		String name = "";
		//TODO this check is breaking code where distinct DataRows in a ResultSet do not contain one or more of the fields.
		// Then the column index will be skewed anyway so the column number may not match the column names order in other data rows
		// disabling the check to fix a problem in the QV Grid. This needs to be discussed again (SW)
		try {
			//name = this.currentDataRow.getColumnName(column);
			name = this.getColumnName(column);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
		return this.currentDataRow.getDataField(name);
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as String.
	 * 
	 * @see com.basiscomponents.db.DataField#getString()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as String.
	 */
	// column get methods (0-based)
	public String getString(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getString();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as NString.
	 * 
	 * @see com.basiscomponents.db.DataField#getString()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as String.
	 */
	public String getNString(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getString();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Integer.
	 * 
	 * @see com.basiscomponents.db.DataField#getInt()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Integer.
	 */
	public Integer getInt(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getInt();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Byte.
	 * 
	 * @see com.basiscomponents.db.DataField#getByte()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Byte.
	 */
	public Byte getByte(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getByte();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Short.
	 * 
	 * @see com.basiscomponents.db.DataField#getShort()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Short.
	 */
	public Short getShort(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getShort();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Long.
	 * 
	 * @see com.basiscomponents.db.DataField#getLong()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Long.
	 */
	public Long getLong(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getLong();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as BigDecimal.
	 * 
	 * @see com.basiscomponents.db.DataField#getBigDecimal()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as BigDecimal.
	 */
	public BigDecimal getBigDecimal(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getBigDecimal();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Double.
	 * 
	 * @see com.basiscomponents.db.DataField#getDouble()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Double.
	 */
	public Double getDouble(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getDouble();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Float.
	 * 
	 * @see com.basiscomponents.db.DataField#getFloat()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Float.
	 */
	public Float getFloat(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getFloat();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Boolean.
	 * 
	 * @see com.basiscomponents.db.DataField#getBoolean()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Boolean.
	 */
	public Boolean getBoolean(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getBoolean();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Date.
	 * 
	 * @see com.basiscomponents.db.DataField#getDate()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Date.
	 */
	public Date getDate(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getDate();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Time.
	 * 
	 * @see com.basiscomponents.db.DataField#getTime()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Time.
	 */
	public Time getTime(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getTime();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Timestamp.
	 * 
	 * @see com.basiscomponents.db.DataField#getTimestamp()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Timestamp.
	 */
	public Timestamp getTimestamp(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getTimestamp();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Byte array.
	 * 
	 * @see com.basiscomponents.db.DataField#getBytes()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Byte array.
	 */
	public byte[] getBytes(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getBytes();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Array.
	 * 
	 * @see com.basiscomponents.db.DataField#getArray()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Array.
	 */
	public Array getArray(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getArray();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Blob.
	 * 
	 * @see com.basiscomponents.db.DataField#getBlob()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Blob.
	 */
	public Blob getBlob(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getBlob();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Clob.
	 * 
	 * @see com.basiscomponents.db.DataField#getClob()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Clob.
	 */
	public Clob getClob(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getClob();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as NClob.
	 * 
	 * @see com.basiscomponents.db.DataField#getNClob()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as NClob.
	 */
	public Clob getNClob(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getNClob();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object.
	 * 
	 * @see com.basiscomponents.db.DataField#getObject()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value object.
	 */
	public Object getObject(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getObject();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as Ref.
	 * 
	 * @see com.basiscomponents.db.DataField#getRef()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as Ref.
	 */
	public Ref getRef(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getRef();
	}

	/**
	 * Retrieves the DataField object of the current DataRow for the specified column index,
	 * and returns the DataField's value object as URL.
	 * 
	 * @see com.basiscomponents.db.DataField#getURL()
	 * 
	 * @param column The column's index.
	 * 
	 * @return The DataField's value as URL.
	 */
	public URL getURL(int column) {
		DataField field = getField(column);
		if (field==null)
			return null;
		return field.getURL();
	}

	/**
	 * Sets the list of field names to be imported into the ResultSet when calling the
	 * {@link #populate(java.sql.ResultSet, Boolean) populate(java.sql.ResultSet, Boolean)}
	 * method. The populate method will only import the field names from the given list into the ResultSet.
	 * 
	 * @param fieldSelection The list of column names to import into the ResultSet when calling the populate method.
	 */
	public void setFieldSelection(ArrayList<String> fieldSelection) {
		this.FieldSelection = fieldSelection;
	}

	// getRowId(int column)
	// getAsciiStream(int column)
	// getBinaryStream(int column)
	// getCharacterStream(int column)
	// getNCharacterStream(int column)
	// getUnicodeStream(int column)

	/**
	 * Returns a JSON String with the ResultSet's content.
	 * 
	 * @return The JSON String with the ResultSet's content.
	 * 
	 * @throws Exception Gets thrown in case the JSON String could not be created.
	 */
	public String toJson() throws Exception {
			return toJson(true);
	}

	/**
	 * Returns a JSON String with the ResultSet's content.
	 * 
	 * @return The JSON String with the ResultSet's content.
	 * 
	 * @throws Exception Gets thrown in case the JSON String could not be created.
	 */
	public String toJson(Boolean f_meta) throws Exception {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter w = new StringWriter();
		JsonGenerator g = jf.createGenerator(w);
		g.configure(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
		// g.useDefaultPrettyPrinter();

		g.writeStartArray();

		Boolean meta_done = !f_meta;

		Iterator<DataRow> it = this.DataRows.iterator();
		while (it.hasNext()) {
			DataRow dr = it.next();
			BBArrayList<String> f = dr.getFieldNames();
			Iterator<String> itf = f.iterator();

			g.writeStartObject();

			while (itf.hasNext()) {
				String fn = itf.next();
				if (dr.getField(fn).getValue() == null) {
					g.writeNullField(fn);
					continue;
				}
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
					g.writeNumberField(fn, dr.getField(fn).getBigDecimal().stripTrailingZeros());
					break;

				case java.sql.Types.DECIMAL:
					if (dr.getField(fn) == null || dr.getField(fn).getBigDecimal() == null)
						g.writeNumberField(fn, 0);
					else
						g.writeNumberField(fn, dr.getField(fn).getBigDecimal());
					break;

				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
					if (dr.getField(fn) == null || dr.getField(fn).getDouble() == null)
						g.writeNumberField(fn, 0.0);
					else
						g.writeNumberField(fn, dr.getField(fn).getDouble().doubleValue());
					break;

				case java.sql.Types.REAL:
					if (dr.getField(fn) == null || dr.getField(fn).getFloat() == null)
						g.writeNumberField(fn, 0.0);
					else
						g.writeNumberField(fn, dr.getField(fn).getFloat().floatValue());
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
						g.writeStringField(fn, dr.getField(fn).getTimestamp().toString().replaceFirst(" ", "T"));
					}
					break;
				case java.sql.Types.DATE:
				case 9:
					if (dr.getField(fn) == null || dr.getField(fn).getDate() == null)
						g.writeStringField(fn, "");
					else {
						g.writeStringField(fn, dr.getField(fn).getDate().toString());
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

			if (f_meta) {
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

							HashMap<String, String> atr;
							try {
								atr = dr.getFieldAttributes(c);
							} catch (Exception e) {
								atr = null;
							}

							Set<String> ks = hm.keySet();
							Iterator<String> its = ks.iterator();
							while (its.hasNext()) {
								String key = its.next();
								if (key.equals("ColumnTypeName") || key.equals("ColumnName"))
									continue;

								if (atr != null && atr.containsKey(key))
									continue;

								String value = null;
								if (hm.get(key) != null)
									value = hm.get(key).toString();
								g.writeStringField(key, value);
							}

							if (atr != null && !atr.isEmpty()){
								Iterator<String> itks = atr.keySet().iterator();
								while (itks.hasNext()){
									String itk = itks.next();
									g.writeStringField(itk,atr.get(itk));
								}
							}

							g.writeEndObject();
						}
					}

					g.writeEndObject();

					meta_done = true;
				}
				else
				{
					BBArrayList<String> fields = dr.getFieldNames();
					itf = fields.iterator();
					boolean m_written = false;
					while (itf.hasNext()) {
						String fieldname = itf.next();
						HashMap<String, String> l = dr.getFieldAttributes(fieldname);
						if (!l.isEmpty()) {
							if (!m_written) {
								g.writeFieldName("meta");
								g.writeStartObject();
								m_written=true;
							}
							g.writeFieldName(fieldname);
							g.writeStartObject();
							Iterator<String> itks = l.keySet().iterator();
							while (itks.hasNext()) {
								String itk = itks.next();
								g.writeStringField(itk,l.get(itk));
							}
							g.writeEndObject();
						}
					}
					if (m_written)
						g.writeEndObject();
				}
			}

			g.writeEndObject();

		} // while on rows

		g.writeEndArray();
		g.close();

		return (w.toString());
	}

	/**
	 * Adds the java.sql.Types and the matching names to the SQLTypeNameMap.
	 */
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

	/**
	 * Returns the name of the given java.sql.Type value, or null in case the given type is unknown.
	 * 
	 * @param sqlType The SQL Type value.
	 * 
	 * @return sqlTypeName The name of the given SQL Type value.
	 */
	public static String getSQLTypeName(int sqlType) {
		return SQLTypeNameMap.get(sqlType);
	}

	/**
	 * Returns a ResultSet object created by parsing the given JSON String.
	 * 
	 * @param js The JSON String used to create the ResultSet object.
	 * 
	 * @return The ResultSet object created from the values provided in the given JSON String.
	 * @throws Exception throws an exception if can not parse the json string to a DataRow.
	 */
	public static ResultSet fromJson(String js) throws Exception {

		ResultSet rs = new ResultSet();
		com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
		com.google.gson.JsonArray o = new com.google.gson.JsonArray();
		try {
			o = parser.parse(js).getAsJsonArray();
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		//Check if first row contains meta data. If so then use it as template row.
		JsonObject meta=null;
		DataRow metaRow=null;
		if (o.size() > 0) {
			meta = o.get(0).getAsJsonObject().getAsJsonObject("meta");
			try {
				metaRow = DataRow.fromJson(o.get(0).toString());
				metaRow.clear();
			} catch(Exception ex) {}
		}
		if (meta == null)
			System.err.println("error parsing - meta data missing");

		Iterator<JsonElement> it = o.iterator();
		while (it.hasNext()) {
			JsonElement el = it.next();
			if (el.getAsJsonObject().getAsJsonObject("meta") == null)
				el.getAsJsonObject().add("meta", meta);

			rs.add(DataRow.fromJson(el.toString(), metaRow));
		}
		return rs;
	}

	/**
	 * Returns the java.sql.ResultSet object of this com.basiscomponents.db.ResultSet object.
	 * 
	 * @return The java.sql.ResultSet object.
	 */
	public java.sql.ResultSet getSQLResultSet() {
		if (sqlResultSet == null) {
			sqlResultSet = new SQLResultSet(this);
		}
		return sqlResultSet;
	}

	/**
	 * Returns this ResultSet as com.google.gson.JsonArray object.
	 * 
	 * @return This ResultSet as com.google.gson.JsonArray object.
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
		String s=new String("ResultSet ");
		s += this.DataRows.size();
		s += " entries @";
		s += System.identityHashCode(this);
		return s;
	}

	@Override
	public Iterator<DataRow> iterator() {
		// Auto-generated method stub
		return new ResultSetIterator(this.DataRows);
	}

	// ---------- scalar functions on one field in the resultset ----------

	/**
	 * Returns the number of DataRows stored in this ResultSet.
	 * 
	 * @see com.basiscomponents.db.ResultSet#size()
	 * 
	 * @return The number of DataRow objects in this ResultSet.
	 */
	public int count() {
		return this.DataRows.size();
	}

	/**
	 * Returns the sum of all fields with the specified name, by iterating over all DataRow objects of this ResultSet.
	 * 
	 * @param fieldname The name of the DataRow field whose value to sum
	 * 
	 * @return the sum of all numeric fields with the specified name.
	 * 
	 * @throws Exception Gets thrown in case the specified field value can't be retrieved as number
	 */
	public Double sum(String fieldname) throws Exception {
		Iterator<DataRow> it = iterator();
		Double s = 0.0;
		while (it.hasNext()) {
			DataRow dr = it.next();
			s += dr.getFieldAsNumber(fieldname);
		}
		return s;
	};

	/**
	 * Returns the min value of all fields with the specified name, by iterating over all DataRow objects of this ResultSet.
	 * 
	 * @param fieldname The name of the DataRow field.
	 * 
	 * @return the min value of all numeric fields with the specified name.
	 * 
	 * @throws Exception Gets thrown in case the specified field value can't be retrieved as number
	 */
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

	/**
	 * Returns the max value of all fields with the specified name, by iterating over all DataRow objects of this ResultSet.
	 * 
	 * @param fieldname The name of the DataRow field.
	 * 
	 * @return the max value of all numeric fields with the specified name.
	 * 
	 * @throws Exception Gets thrown in case the specified field value can't be retrieved as number
	 */
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

	/**
	 * Returns the average value of all fields with the specified name, by iterating over all DataRow objects of this ResultSet.
	 * 
	 * @param fieldname The name of the DataRow field.
	 * 
	 * @return the average value of all numeric fields with the specified name.
	 * 
	 * @throws Exception Gets thrown in case the specified field value can't be retrieved as number
	 */
	public Double avg(String fieldname) throws Exception {
		return sum(fieldname) / count();
	};

	/**
	 * Returns the median value of all fields with the specified name, by iterating over all DataRow objects of this ResultSet.
	 * 
	 * @param fieldname The name of the DataRow field.
	 * 
	 * @return the median value of all numeric fields with the specified name.
	 * 
	 * @throws Exception Gets thrown in case the specified field value can't be retrieved as number
	 */
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

	/**
	 * Returns a DataRow which contains all field values of the DataRows defined in this ResultSet for the given field name.
	 * The number of field occurrences in all DataRows of this ResultSet will be set as field value in the returned DataRow.
	 * In addition, while iterating over the DataRow objects, the method retrieves the field value for the given label name and sets it
	 * as attribute of the returned DataRow object.

	 * @param fieldname The name of the field.
	 * @param labelname The label name.
	 * 
	 * @see #countByGroup(String)
	 * @see #countByGroup(String, String, int, int)
	 *
	 * @return A DataRow object with the field values from the DataRows defined in this ResultSet as field names and with the number
	 * 		   of occurrences as field values.
	 * 
	 * @throws Exception
	 */
	public DataRow countByGroup(String fieldname, String labelname) throws Exception {
		return this.countByGroup(fieldname, labelname, NO_SORT, 0);
	}

	/**
	 * Returns a DataRow which contains all field values of the DataRows defined in this ResultSet for the given field name.
	 * The number of field occurrences in all DataRows of this ResultSet will be set as field value in the returned DataRow.
	 * 
	 * @see #countByGroup(String, String)
	 * @see #countByGroup(String, String, int, int)
	 * 
	 * @param fieldname The name of the field.
	 *
	 * @return A DataRow object with the field values from the DataRows defined in this ResultSet as field names and with the number
	 * 		   of occurrences as field values.
	 * 
	 * @throws Exception
	 */
	public DataRow countByGroup(String fieldname) throws Exception {
		return this.countByGroup(fieldname, fieldname, NO_SORT, 0);
	}



	/**
	 * Returns a DataRow which contains all field values of the DataRows defined in this ResultSet for the given field name.
	 * The number of field occurrences in all DataRows of this ResultSet will be set as field value in the returned DataRow.
	 * In addition, while iterating over the DataRow objects, the method retrieves the field value for the given label name and sets it
	 * as attribute of the returned DataRow object.
	 * <br><br>
	 * The sort parameter can be used to sort the created DataRow's fields. The sort parameter can only have predefined values, see below.
	 * The top parameter is used to limit the field names of the returned DataRow to the given value.
	 * <br><br>
	 * The only allowed values for the sorting are:
	 * <ul>
	 *     <li>ResultSet.NO_SORT (default)</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL</li>
	 *     <li>ResultSet.SORT_ON_RESULT</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD_DESC</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL_DESC</li>
	 *     <li>ResultSet.SORT_ON_RESULT_DESC</li>
	 * </ul>
	 * 
	 * @see #countByGroup(String)
	 * @see #countByGroup(String, String)
	 * 
	 * @param fieldname The name of the field.
	 * @param labelname The label name.
	 * @param sort The sorting code.
	 * @param top The maximal number of field's of the new DataRow object.
	 *
	 * @return A DataRow object with the field values from the DataRows defined in this ResultSet as field names and with the number
	 * 		   of occurrences as field values.
	 * 
	 * @throws Exception
	 */
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
			BBArrayList<String> map = dr.getFieldNames();
			while (map.size() > top) {
				int i = map.size() - 1;
				String f = (String) map.get(i);
				dr.removeField(f);
				map.remove(i);
			}
		}

		return dr;
	}

	/**
	 * Sorts the given DataRow object based on the sort code.
	 * The valid sort codes are:
	 * <ul>
	 *     <li>ResultSet.NO_SORT (default)</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL</li>
	 *     <li>ResultSet.SORT_ON_RESULT</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD_DESC</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL_DESC</li>
	 *     <li>ResultSet.SORT_ON_RESULT_DESC</li>
	 * </ul>
	 * 
	 * @param dr The DataRow whose values to sort.
	 * @param sort The sort code defining how to sort the fields.
	 * 
	 * @return The re-ordered DataRow object.
	 * 
	 * @throws Exception
	 */
	private DataRow sortDataRow(DataRow dr, int sort) throws Exception {
		// TODO make a generic implementation of this in the DataRow itself,
		// that can sort on the value, the name or any attribute

		DataRow drn = new DataRow();
		BBArrayList<String> f = dr.getFieldNames();
		Iterator<String> it = f.iterator();
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

	/**
	 * Returns a String with the inverted bytes of the given input String.
	 * 
	 * @param in The String whose bytes will be inverted.
	 * 
	 * @return A String with the inverted bytes of the given input String.
	 */
	private String invert(String in) {
		String out = "";
		byte[] b = in.getBytes();
		for (int i = 0; i < b.length; i++) {
			out += 255 - b[i];
		}
		return out;
	}

	/**
	 * Returns a DataRow which contains all field values of the DataRows defined in this ResultSet for the given field name.
	 * Sums the value of the field with the given field name(sumfieldname) and sets it as field value to the DataRow.
	 * In addition, while iterating over the DataRow objects, the method retrieves the field value for the given label name and sets it
	 * as attribute of the returned DataRow object.
	 * 
	 * @see #sumByGroup(String, String, String, int, int)
	 * 
	 * @param fieldname The name of the field.
	 * @param labelname The label name.
	 * @param sumfieldname The field name whose value should be summed
	 *
	 * @return A DataRow object with the field values from the DataRows defined in this ResultSet as field names and with the sum
	 * 		   of the values of the field with the given name as field values.
	 * 
	 * @throws Exception
	 */
	public DataRow sumByGroup(String fieldname, String sumfieldname) throws Exception {
		return this.sumByGroup(fieldname, fieldname, sumfieldname, NO_SORT, 0);
	}

	/**
	 * Returns a DataRow which contains all field values of the DataRows defined in this ResultSet for the given field name.
	 * Sums the value of the field with the given field name(sumfieldname) and sets it as field value to the DataRow.
	 * In addition, while iterating over the DataRow objects, the method retrieves the field value for the given label name and sets it
	 * as attribute of the returned DataRow object.
	 * <br><br>
	 * The sort parameter can be used to sort the created DataRow's fields. The sort parameter can only have predefined values, see below.
	 * The top parameter is used to limit the field names of the returned DataRow to the given value.
	 * <br><br>
	 * The only allowed values for the sorting are:
	 * <ul>
	 *     <li>ResultSet.NO_SORT (default)</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL</li>
	 *     <li>ResultSet.SORT_ON_RESULT</li>
	 *     <li>ResultSet.SORT_ON_GROUPFIELD_DESC</li>
	 *     <li>ResultSet.SORT_ON_GROUPLABEL_DESC</li>
	 *     <li>ResultSet.SORT_ON_RESULT_DESC</li>
	 * </ul>
	 * 
	 * @see #sumByGroup(String, String)
	 * 
	 * @param fieldname The name of the field.
	 * @param labelname The label name.
	 * @param sumfieldname The field name whose value should be summed
	 * @param sort The sort code.
	 * @param top The maximal number of field's of the new DataRow object.
	 *
	 * @return A DataRow object with the field values from the DataRows defined in this ResultSet as field names and with the sum
	 * 		   of the values of the field with the given name as field values.
	 * 
	 * @throws Exception
	 */
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
			BBArrayList<String> map = dr.getFieldNames();
			while (map.size() > top) {
				int i = map.size() - 1;
				String f = map.get(i);
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

	/**
	 * Prints the ResultSet's content to the standard output stream.
	 * If this method is called from a BBj context, the ResultSet's content
	 * will be printed in the Debug.log file.
	 */
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