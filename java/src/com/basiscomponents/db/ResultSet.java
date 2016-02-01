package com.basiscomponents.db;

import java.io.StringWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;

import com.basiscomponents.json.ComponentsCharacterEscapes;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang.StringEscapeUtils;

public class ResultSet implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private ArrayList<HashMap<String, Object>> MetaData = new ArrayList<HashMap<String, Object>>();
	@Expose
	private ArrayList<String> ColumnNames = new ArrayList<String>();
	@Expose
	private ArrayList<DataRow> DataRows = new ArrayList<DataRow>();

	private int currentRow = -1;
	private DataRow currentDataRow;

	public static HashMap<Integer, String> SQLTypeNameMap = new HashMap<Integer, String>();

	public ResultSet() {
		if (SQLTypeNameMap.isEmpty())
			setSQLTypeNameMap();
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
	public void populate(java.sql.ResultSet rs, Boolean defaultMetaData)
			throws Exception {
		java.sql.ResultSetMetaData rsmd = rs.getMetaData();
		int cc = rsmd.getColumnCount();
		String name;
		int type;
		ArrayList<Integer> types = new ArrayList<Integer>();
		int column = 0;
		if (defaultMetaData == true) {
			while (column < cc) {
				column++;
				HashMap<String, Object> colMap = new HashMap<String, Object>();
				colMap.put("CatalogName", rsmd.getCatalogName(column));
				colMap.put("ColumnClassName", rsmd.getColumnClassName(column));
				colMap.put("ColumnDisplaySize",
						rsmd.getColumnDisplaySize(column));
				colMap.put("ColumnLabel", rsmd.getColumnLabel(column));
				name = rsmd.getColumnName(column);
				colMap.put("ColumnName", name);
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
				colMap.put("DefinitelyWritable",
						rsmd.isDefinitelyWritable(column));
				colMap.put("Nullable", rsmd.isNullable(column));
				colMap.put("ReadOnly", rsmd.isReadOnly(column));
				colMap.put("Searchable", rsmd.isSearchable(column));
				colMap.put("Signed", rsmd.isSigned(column));
				colMap.put("Writable", rsmd.isWritable(column));
				this.MetaData.add(colMap);
			}
		}

		try {
			rs.beforeFirst();
		} catch (Exception e) {
			// do nothing
		}

		while (rs.next()) {
			DataRow dr = DataRow.newInstance(this);
			column = 0;
			while (column < cc) {
				column++;
				DataField field = new DataField(rs.getObject(column));
				if (defaultMetaData == true)
					type = types.get(column - 1);
				else
					type = getColumnType(column - 1);
				dr.addDataField(this.ColumnNames.get(column - 1), type, field);
			}
			this.DataRows.add(dr);
		}
	}

	private void mergeDataRowFields(DataRow dr) {
		
		ArrayList<String> fn = dr.getFieldNames();
		
		
		Iterator<String> it = fn.iterator();
		while (it.hasNext()){
			String f=it.next();
			if (!this.ColumnNames.contains(f)){
				int c = this.addColumn(f);
				try {
					this.setColumnType(c, dr.getFieldType(f));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					HashMap<String, String> hm = dr.getFieldAttributes(f);
					Iterator<String> it2 = hm.keySet().iterator();
					while (it2.hasNext()){
						String key = it2.next();
						this.setAttribute(c, key, (String) hm.get(key));
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
		if (this.DataRows.isEmpty() || row < 0
				|| row > this.DataRows.size() - 1)
			return false;
		else {
			this.currentRow = row;
			this.currentDataRow = this.DataRows.get(this.currentRow);
			return true;
		}
	}

	public Boolean next() {
		if (this.DataRows.isEmpty()
				|| this.currentRow > this.DataRows.size() - 2)
			return false;
		else {
			this.currentRow = this.currentRow + 1;
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
		Integer size = (Integer) this.MetaData.get(column).get(
				"ColumnDisplaySize");
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
		Boolean flag = (Boolean) this.MetaData.get(column).get(
				"DefinitelyWritable");
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
			throw new Exception("Invalid nullable value"
					+ String.valueOf(nullable));
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
		return (String) field.getString();
	}

	public String getNString(int column) {
		DataField field = getField(column);
		return (String) field.getString();
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

	// getRowId(int column)
	// getAsciiStream(int column)
	// getBinaryStream(int column)
	// getCharacterStream(int column)
	// getNCharacterStream(int column)
	// getUnicodeStream(int column)

	public String toJson() throws Exception {

		JsonFactory jf = new JsonFactory();
		jf.setCharacterEscapes(new ComponentsCharacterEscapes());
		StringWriter w = new StringWriter();
		JsonGenerator g = jf.createJsonGenerator(w);
//		g.useDefaultPrettyPrinter();

		g.writeStartArray();
		
		Boolean meta_done = false;
		Iterator<DataRow> it = this.DataRows.iterator();
		while (it.hasNext()){
			DataRow dr = it.next();
			ArrayList<String> f = dr.getFieldNames();
			Iterator<String> itf = f.iterator();
			
			g.writeStartObject();
			
			while (itf.hasNext()){
				String fn = itf.next();
				int t=dr.getFieldType(fn);
				switch (t){
						case java.sql.Types.CHAR                    : 
						case java.sql.Types.VARCHAR                 : 
						case java.sql.Types.NVARCHAR                : 
						case java.sql.Types.NCHAR                   :
						case java.sql.Types.LONGVARCHAR             : 
						case java.sql.Types.LONGNVARCHAR            : 
							String s =  dr.getField(fn).getString();
//							s=StringEscapeUtils.escapeJavaScript(s);
							g.writeStringField(fn,s);
							break;
						case java.sql.Types.BIGINT                  : 
						case java.sql.Types.TINYINT                 : 
						case java.sql.Types.INTEGER                 : 
						case java.sql.Types.SMALLINT                :
							g.writeNumberField(fn, dr.getField(fn).getInt());
							break;							
							
						case java.sql.Types.NUMERIC                 : 
						case java.sql.Types.DOUBLE                  : 
						case java.sql.Types.FLOAT                   : 
						case java.sql.Types.DECIMAL                 : 
						case java.sql.Types.REAL                    :
							g.writeNumberField(fn, dr.getField(fn).getDouble());
							break;							
							
						case java.sql.Types.BOOLEAN                 : 
						case java.sql.Types.BIT                     : 
							g.writeBooleanField(fn, dr.getField(fn).getBoolean());
							break;							

						case java.sql.Types.TIMESTAMP               : 
						case java.sql.Types.TIMESTAMP_WITH_TIMEZONE : 
						case (int) 11                               :
							
							DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
							DateFormat df2 = new SimpleDateFormat("HH:mm:ss");
							String fd = df1.format(dr.getField(fn).getTimestamp())+"T"+df2.format(dr.getField(fn).getTimestamp())+".000Z";							
							g.writeStringField(fn, fd);
							break;
						case java.sql.Types.DATE                    : 
						case (int) 9                                :
							 df1 = new SimpleDateFormat("yyyy-MM-dd");
							 df2 = new SimpleDateFormat("HH:mm:ss");
							 fd = df1.format(dr.getField(fn).getDate())+"T"+df2.format(dr.getField(fn).getDate())+".000Z";							
							g.writeStringField(fn, fd);
							break;
							
						case java.sql.Types.ARRAY                   : 
						case java.sql.Types.BINARY                  : 
						case java.sql.Types.BLOB                    : 
						case java.sql.Types.CLOB                    : 
						case java.sql.Types.DATALINK                : 

						case java.sql.Types.DISTINCT                : 
						case java.sql.Types.JAVA_OBJECT             : 
						case java.sql.Types.LONGVARBINARY           : 
						case java.sql.Types.NCLOB                   : 
						case java.sql.Types.NULL                    : 
						case java.sql.Types.OTHER                   : 
						case java.sql.Types.REF                     : 
						case java.sql.Types.REF_CURSOR              : 
						case java.sql.Types.ROWID                   : 
						case java.sql.Types.SQLXML                  : 
						case java.sql.Types.STRUCT                  : 
						case java.sql.Types.TIME                    : 
						case java.sql.Types.TIME_WITH_TIMEZONE      : 
						case java.sql.Types.VARBINARY               : 


						default:
							//this is a noop - TODO
							break;
				
				}//switch
				
			}//while on fields
			
			if (!meta_done){
				
				g.writeFieldName("meta");
				
				
				
				g.writeStartObject();
				
				Iterator<HashMap<String, Object>> i = this.MetaData.iterator();
				while (i.hasNext())  {
					HashMap<String, Object> hm = i.next();
					String c = (String) hm.get("ColumnName");
					if (c!=null){
						g.writeFieldName(c);
						g.writeStartObject();
						
						Set<String> ks = hm.keySet();
						Iterator<String> its = ks.iterator(); 
						while (its.hasNext()){
							String key = its.next();
							if (key.equals("ColumnTypeName") || key.equals("ColumnName"))
								continue;
							String value = hm.get(key).toString();
							g.writeStringField(key, value);
						}
						g.writeEndObject();
					}
				}
				
				g.writeEndObject();
				
				
				
				meta_done=true;
			}
			
			g.writeEndObject();
			
		}//while on rows
		
		g.writeEndArray();
		g.close(); 
		
		return(w.toString()); 
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
		SQLTypeNameMap.put(java.sql.Types.TIME_WITH_TIMEZONE,
				"TIME_WITH_TIMEZONE");
		SQLTypeNameMap.put(java.sql.Types.TIMESTAMP, "TIMESTAMP");
		SQLTypeNameMap.put(java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
				"TIMESTAMP_WITH_TIMEZONE");
		SQLTypeNameMap.put(java.sql.Types.TINYINT, "TINYINT");
		SQLTypeNameMap.put(java.sql.Types.VARBINARY, "VARBINARY");
		SQLTypeNameMap.put(java.sql.Types.VARCHAR, "VARCHAR");
		SQLTypeNameMap.put((int) 9, "BASIS DATE");
		SQLTypeNameMap.put((int) 11, "BASIS TIMESTAMP");
	}

	public static String getSQLTypeName(int type) {
		return (String) SQLTypeNameMap.get(type);
	}

	public static ResultSet fromJson(String js) {
		// TODO Auto-generated method stub
		return null;
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


public String toString() {
	String s = "[";
		s+="Metadata:";
		s+= this.MetaData.toString();
		s+=";";
		s+="Columns:";
		s+= this.ColumnNames.toString();
		s+=";";
		s+="Records:[";
		Iterator<DataRow> it = this.DataRows.iterator();
		while (it.hasNext()){
			s+=it.next().toString();
			s+=";";
		}
		s+="]]";
		return s;
}

}