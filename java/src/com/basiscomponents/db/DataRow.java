package com.basiscomponents.db;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.basis.bbj.client.datatypes.BBjVector;
import com.basis.util.common.BBjNumber;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class DataRow {

	private HashMap<String, DataField> FieldList = new HashMap<>();
	private ArrayList<String> FieldNames = new ArrayList<>();
	private String Tpl;

	public DataRow(String tpl) {
		this.Tpl = tpl;
	}

	public DataRow(String tpl, String rec) throws Exception {
		this(tpl);
		setString(rec);
	}

	public DataRow() {
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("rawtypes")
	public DataRow(java.util.Map map) throws Exception {
		Set ks = map.keySet();
		Iterator it = ks.iterator();
		while (it.hasNext()) {
			String k = (String) it.next();
			Object o = map.get(k);
			
			System.out.println(o.getClass().getCanonicalName());
			switch (o.getClass().getCanonicalName())
			{
				case "java.lang.String":
					setFieldValue(k, (String)o);
					break;
				case "java.lang.Double":
					setFieldValue(k, (Double)o);
					break;					
				case "java.lang.Integer":
					setFieldValue(k, (Integer)o);
					break;					
				case "java.lang.Boolean":
					setFieldValue(k, (Boolean)o);
					break;					
				default:
					System.err.println("Constructor Datarow from Map - can't convert "+o.getClass().getCanonicalName());
					break;
			
			}
			

		}

	}
	

	public DataRow(java.sql.ResultSet rs) throws Exception {

		long t = System.currentTimeMillis();
		int cc = rs.getMetaData().getColumnCount();
		int i = 0;
		while (i < cc) {
			i++;
			String ColName = rs.getMetaData().getColumnName(i);
			String ColLabel = rs.getMetaData().getColumnName(i);
			Integer ColType = rs.getMetaData().getColumnType(i);

			String s = null;

			switch (ColType) {
			case java.sql.Types.VARCHAR:
			case java.sql.Types.CHAR:
			case java.sql.Types.LONGVARCHAR:
			case java.sql.Types.LONGNVARCHAR:
			case java.sql.Types.NCHAR:
				try {
					s = rs.getString(ColName);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setFieldValue(ColName, s);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.NVARCHAR:
				try {
					s = rs.getString(ColName);
				} catch (SQLException e) {

					e.printStackTrace();
				}
				setFieldValue(ColName, s);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.NUMERIC:
			case java.sql.Types.DECIMAL:
				java.math.BigDecimal d = rs.getBigDecimal(ColName);
				try {
					setFieldValue(ColName, d);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.REAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
				java.lang.Double dbl = rs.getDouble(ColName);
				setFieldValue(ColName, dbl);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.INTEGER:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:
				java.lang.Integer inte = null;
				try {
					inte = rs.getInt(ColName);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setFieldValue(ColName, inte);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.DATE:

				java.sql.Date dt = null;

				try {
					dt = rs.getDate(ColName);
				} catch (SQLException e) {
					// e.printStackTrace();
					// System.out.println("object: "+rs.getObject(ColName));
				}

				setFieldValue(ColName, dt);
				setFieldAttribute(ColName, "LABEL", ColLabel);

				break;

			case java.sql.Types.TIMESTAMP:
				java.sql.Timestamp ts = null;
				try {
					ts = rs.getTimestamp(ColName);
				} catch (SQLException e3) {
					// TODO Auto-generated catch block
					e3.printStackTrace();
					System.out.println("object: " + rs.getObject(ColName));
				}
				setFieldValue(ColName, ts);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.TIME:
				java.sql.Time tm = null;
				try {
					tm = rs.getTime(ColName);
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
					System.out.println("object: " + rs.getObject(ColName));
				}
				setFieldValue(ColName, tm);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.BIT:
			case java.sql.Types.BOOLEAN:
				java.lang.Boolean bl = null;
				try {
					bl = rs.getBoolean(ColName);
				} catch (SQLException e1) {
					System.out.println("object: " + rs.getObject(ColName));
					e1.printStackTrace();
				}
				setFieldValue(ColName, bl);
				setFieldAttribute(ColName, "LABEL", ColLabel);
				break;

			case java.sql.Types.BINARY:
			case java.sql.Types.VARBINARY:
			case java.sql.Types.BLOB:
			case java.sql.Types.CLOB:
			case java.sql.Types.LONGVARBINARY:
				// rem these types tend to be blobs
				// rem dunno what to do with these TODO
				// rem simply skip...
				continue;
				// break;

			default:
				try {
					setFieldValue(ColName, rs.getString(ColName));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setFieldAttribute(ColName, "LABEL", "<unkown type>" + ColLabel);
				break;
			}

			setSQLType(ColName, ColType);

		}
		t = System.currentTimeMillis() - t;
		// System.out.println("finished new for "+cc+" columns: "+t+" ms ");
	}

	public static DataRow newInstance() {
		return new DataRow();
	}

	public Boolean contains(String field) {
		return this.FieldList.containsKey(field);
	}

	public void setString(String rec) throws Exception {
		throw new Exception("Not yet implemented");
		// BBjTemplatedString x;
		// x=BBjAPI.makeTemplatedString(this.Tpl.getBytes());
		// x.setString(rec)
		// dim x1:this.Tpl
		// fields=fattr(x1,"")
		//
		// while fields>""
		// p=pos(0a=fields)
		// f=fields(1,p-1)
		// fields=fields(p+1)
		//
		// fieldtype=asc(fattr(x1,f)(1,1))
		//
		// if fieldtype=1 or fieldtype=11 then
		// this.setFieldValue(f,x.getFieldAsString(f))
		// else
		// this.setFieldValue(f,x.getFieldAsNumber(f))
		// fi
		//
		//
		// x=this.Tpl(pos(f+":"=this.Tpl)+len(f)+2)
		// x=x(pos("("=x)+1)
		// x=x(1,pos(")"=x)-1)
		//
		// if pos("*"=x)>0 then
		// x=x(1,pos("*"=x)-1)
		// fi
		//
		// if pos("+"=x)>0 then
		// x=x(1,pos("+"=x)-1)
		// fi
		//
		// cast(DataField,this.FieldList.get(f)).setLength(num(x))
		// wend
		//
	}

	public String toString() {

		String x = "";

		Iterator<String> it = FieldNames.iterator();
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

	public void setFieldValue(String name, String value) throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'C', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, BBjNumber value) throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'N', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.sql.Date value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'D', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.sql.Timestamp value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'X', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.lang.Double value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'N', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.lang.Integer value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'I', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.lang.Boolean value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'B', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.math.BigDecimal value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'Y', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public void setFieldValue(String name, java.sql.Time value)
			throws Exception {
		DataField field = this.FieldList.get(name);
		if (field == null) {
			field = new DataField(name, 'T', value);
			this.FieldList.put(name, field);
			this.FieldNames.add(name);
		} else {
			field.setValue(value);
		}
	}

	public Object getField(String name) throws Exception {

		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return field.getObject();
	}

	public String getFieldAsString(String name) throws Exception {

		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return field.getValueAsString();
	}

	public Double getFieldAsNumber(String name) throws Exception {

		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return field.getValueAsNumber();
	}

	public BBjVector getFieldNames() {
		BBjVector v = new BBjVector();

		Iterator<String> it = this.FieldNames.iterator();
		while (it.hasNext()) {
			v.addItem(it.next());
		}

		return v;
	}

	public String getFieldType(String name) throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return Character.toString(field.getType());

	}

	public int getFieldLength(String name) throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return field.getLength();

	}

	public void setFieldAttribute(String name, String attrname, String value)
			throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		field.setAttribute(attrname, value);

	}

	public String getFieldAttribute(String name, String attrname)
			throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		String attr = field.getAttribute(attrname);
		if (attr == null)
			attr = "";

		return attr;

	}

	public HashMap<String, String> getFieldAttributes(String name)
			throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		return field.getAttributes();

	}

	public void removeFieldAttribute(String name, String attrname)
			throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		field.removeAttribute(attrname);

	}

	public void removeField(String name) {
		this.FieldList.remove(name);
		this.FieldNames.remove(name);
	}

	public BBjVector getAttributeForFields(String attrname) {
		return this.getAttributeForFields(attrname, false);
	}

	// rem /**
	// rem * Method getAttributeForFields:
	// rem * get all attributes, with a specific attribute name, as a BBjVector
	// rem * @param String attrame: the name of the attribute
	// rem * @param BBjNumber defaultToFieldname: if 1 return the field name if
	// attribute value is empty
	// rem * @return BBjVector vec: a BBjVector with the attributes with the
	// given attribute name
	// rem */
	public BBjVector getAttributeForFields(String attrname,
			Boolean defaultToFieldname) {
		DataField field;
		BBjVector ret = new BBjVector();

		if (this.FieldNames.size() > 0) {
			Iterator<String> it = this.FieldNames.iterator();
			while (it.hasNext()) {
				String n = it.next();
				field = this.FieldList.get(n);
				String r = field.getAttribute(attrname);

				if (r == "" && defaultToFieldname)
					r = n;
				ret.addItem(r);
			}
		}
		return ret;
	}

	//
	//
	// rem /**
	// rem * Method replaceFields:
	// rem * search and replace all the field names in a given string (formula)
	// with the field value.
	// rem * The field name should be escaped as "F{name}".
	// rem * @param String formula: the string with the escaped field names
	// rem * @return String formula: the replaced string
	// rem */
	public String replaceFields(String Formula) throws Exception {

		return replaceFields(Formula, false);

	}

	public String replaceFields(String Formula,
			Boolean fCleanRemainingPlaceholders) throws Exception {

		Set<String> ks = this.FieldList.keySet();
		Iterator<String> it = ks.iterator();

		while (it.hasNext()) {
			String k = it.next();
			String k1 = "$F{" + k + "}";

			Formula = Formula.replace(k1, this.getFieldAsString(k));
		}

		if (fCleanRemainingPlaceholders) {
			Formula = Formula.replaceAll("\\$F\\{\\S*\\}", "");
		}

		return Formula;

	}

	public Boolean equals(DataRow r) throws Exception {
		Boolean eq = true;
		BBjVector fields = r.getFieldNames();

		if (fields.size() != this.FieldList.size())
			eq = false;
		else {
			@SuppressWarnings("unchecked")
			Iterator<String> it = fields.iterator();
			while (it.hasNext()) {
				String fieldname = it.next();
				DataField f = this.FieldList.get(fieldname);
				if (f == null
						|| !r.getFieldAsString(fieldname).equals(
								this.getFieldAsString(fieldname))) {
					eq = false;
					break;
				}
			}
		}
		return eq;
	}

	public void addDataField(String name, DataField field) {
		if (FieldList.containsKey(name)) {
			FieldList.remove(name);
			FieldNames.remove(name);
		}
		this.FieldList.put(name, field);
		this.FieldNames.add(name);
	}

	public DataField getDataField(String name) {
		return this.FieldList.get(name);
	}

	public DataRow clone() {

		DataRow dRow = new DataRow();

		Iterator<String> it = this.FieldNames.iterator();
		while (it.hasNext()) {
			String k = it.next();
			DataField f = this.FieldList.get(k);
			DataField f1 = f.clone();
			dRow.addDataField(k, f1);
		}

		return dRow;
	}

	public String getInsertStatement() {

		Set<String> ks = this.FieldList.keySet();
		Iterator<String> it = ks.iterator();
		String f1 = "";
		String v = "";
		String sql;
		while (it.hasNext()) {
			String k = it.next();
			DataField f = this.FieldList.get(k);
			if (f.isNull())
				continue;
			f1 = f1 + "," + k;
			v = v + "," + f.getValueForSQL();
		}

		sql = "(" + f1.substring(1) + ") VALUES (" + v.substring(1) + ")";

		return sql;

	}

	public String getUpdateStatement() {

		Set<String> ks = this.FieldList.keySet();
		Iterator<String> it = ks.iterator();

		String sql = "";
		while (it.hasNext()) {
			String k = it.next();
			DataField f = this.FieldList.get(k);
			if (f.isNull())
				continue;

			sql = sql + ", " + k + "=" + f.getValueForSQL();
		}

		if (sql.length() > 0)
			sql = sql.substring(1);

		return sql;
	}

	public java.util.HashMap<String, Object> getObjects() {

		HashMap<String, Object> hm = new HashMap<String, Object>();

		Set<String> ks = this.FieldList.keySet();
		Iterator<String> it = ks.iterator();

		while (it.hasNext()) {
			String k = it.next();
			DataField f = this.FieldList.get(k);

			if (f.isNull())
				continue;

			hm.put(k, f.getObject());
		}

		return hm;

	}

	public void setSQLType(String name, int SQLType) throws Exception {
		DataField field = this.FieldList.get(name);

		if (field == null)
			throw new Exception("Field " + name + " does not exist");

		field.setSQLType(SQLType);

	}

	@SuppressWarnings("rawtypes")
	public void mergeRecord(DataRow r) throws Exception {
		BBjVector v = r.getFieldNames();
		Iterator it = v.iterator();
		while (it.hasNext()) {

			String f = (String) it.next();
			this.addDataField(f, r.getDataField(f));

		}

	}

	public String toJson() {

		String json = "";
		Iterator<String> it = FieldList.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();
			if (!json.isEmpty()) {
				json += ',';
			}
			json += FieldList.get(k).toJson();

		}

		json = "{\"datarow\":[" + json + "]}";
		return json;
	}

	public JsonElement toJsonElement() {

		JsonArray jsonarray = new JsonArray();

		Iterator<String> it = FieldList.keySet().iterator();
		while (it.hasNext()) {
			String k = it.next();
			jsonarray.add(FieldList.get(k).toJsonElement());
		}

		return jsonarray;
	}

	// public JsonElement toJsonElementOld() {
	//
	// String json=toJson();
	// JsonElement jelement = new JsonParser().parse(json);
	// JsonObject jobject = jelement.getAsJsonObject();
	// return jobject.get("datarow");
	// }

	public static DataRow fromJson(String json) {

		Gson gson = new Gson();
		JsonElement jelement = new JsonParser().parse(json);
		JsonObject jobject = jelement.getAsJsonObject();
		JsonArray jarray = jobject.getAsJsonArray("datarow");
		Type type = new TypeToken<List<DataField>>() {
		}.getType();
		List<DataField> records = gson.fromJson(jarray, type);
		DataRow r = new DataRow();

		if (records != null){
			Iterator<DataField> it = records.iterator();
			while (it.hasNext()) {
				DataField df = it.next();
				r.addDataField(df.getName(), df);
			}
		}

		return r;
	}

}
