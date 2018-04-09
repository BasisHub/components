package com.basiscomponents.db;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.basiscomponents.db.model.Attribute;
import com.google.gson.annotations.Expose;

/**
 * The DataField class is an object container class which provides multiple cast methods 
 * to retrieve the initially stored object in different formats / types.
 */
public class DataField implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private Object Value;

	private Map<String, Attribute> attributes = new HashMap<>();

	/**
	 * Creates the DataField object with the given object as the DataField's value
	 * 
	 * @param value The value object of the DataField to be created 
	 */
	public DataField(Object value) {
		setValue(value);
	}

	/**
	 * Returns the DataField's value.
	 * 
	 * @return value the DataField's value
	 */
	public Object getValue() {
		return this.Value;
	}

	/**
	 * Returns <code>true</code> in case the given DataField object equals the current DataField object, <code>false</code> otherwise.
	 * The method does only compare the value object of both DataField objects, not the attributes.
	 * 
	 * @param dataField The DataField object to compare with.
	 * @return equal The boolean value indicating whether the given DataField object equals this DataField object.
	 */
	public Boolean equals(DataField dataField) {
		return this.getString().equals(dataField.getString());
	}

	/**
	 * Returns <code>true</code> in case the given DataField object equals the current DataField object, <code>false</code> otherwise.
	 * The method does only compare the value object of both DataField objects, not the attributes.
	 * 
	 * @param dataField The DataField object to compare with.
	 * @return equal The boolean value indicating whether the given DataField object equals this DataField object.
	 */
	public Boolean equals(String pattern) {
		return equals(pattern, false, true);
	}
	/**
	 * Returns <code>true</code> in case the given DataField object equals the current DataField object, <code>false</code> otherwise.
	 * The method does only compare the value object of both DataField objects, not the attributes.
	 * 
	 * @param dataField The DataField object to compare with.
	 * @return equal The boolean value indicating whether the given DataField object equals this DataField object.
	 */
	public Boolean equals(String pattern,final boolean caseSensitive, final boolean trimmed) {

		if (pattern.startsWith("'") && pattern.endsWith("'"))
		{
			pattern=pattern.substring(1, pattern.length()-1);
		}

		String cn = getClassName();
		switch (cn){
		case "java.lang.Integer":
				return getInt().equals(Integer.parseInt(pattern));
		case "java.lang.String":
				return compareString(getString(), pattern, caseSensitive, trimmed);
		default:
			System.err.println("unimplemented: field type "+cn);
			return false;
		}

	}
	private static boolean compareString(final String dataRowString, final String pattern, boolean caseSensitive, boolean trimmed) {
		String compareString = dataRowString;
		if(trimmed) {
			 compareString = compareString.trim();
		}
		if(caseSensitive) {
			return compareString.equals(pattern);
		}else {
			return compareString.equalsIgnoreCase(pattern);
		}
	}
	/**
	 * Sets the given object as the DataField's value
	 * 
	 * @param value The object to set as the DataField's value
	 */
	public void setValue(Object value) {
		this.Value = value;
	}

	/**
	 * Returns the class name of the DataField's value object.
	 * 
	 * @return className The DataField value's class name.
	 */
	public String getClassName() {
		return this.Value.getClass().getCanonicalName();
	}

	/**
	 * Returns the DataField's value.
	 * 
	 * @return value The DataField's value.
	 */
	public Object getObject() {
		return this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.String</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.String</code> object.
	 */
	public String getString() {
		
		try {
			return (String) convertType(this.Value, java.sql.Types.VARCHAR);
			} catch (Exception e) {
			return "";
		}
		
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Integer</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Integer</code> object.
	 */
	public Integer getInt() {
		return (Integer) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Byte</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Byte</code> object.
	 */
	public Byte getByte() {
		return (Byte) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Short</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Short</code> object.
	 */
	public Short getShort() {
		return (Short) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Long</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Long</code> object.
	 */
	public Long getLong() {
		if (this.Value != null && getClassName() == "java.lang.Integer") {
			// make this work the same as STR(Boolean.TRUE) in BBj
			// for compatibility reasons.
			// If it's a problem, we might introduce a COMPAT flag later.
			return Integer.toUnsignedLong((Integer)this.Value);
		}
		return (Long) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.math.BigDecimal</code> object.
	 * 
	 * @return value The DataField's value as <code>java.math.BigDecimal</code> object.
	 */
	public BigDecimal getBigDecimal() {
		if (this.Value != null && getClassName() == "java.lang.Double") {
			// make this work the same as STR(Boolean.TRUE) in BBj
			// for compatibility reasons.
			// If it's a problem, we might introduce a COMPAT flag later.
			return new BigDecimal((Double)this.Value);
		}		
		return (BigDecimal) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Double</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Double</code> object.
	 */
	public Double getDouble() {
		return (Double) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Float</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Float</code> object.
	 */
	public Float getFloat() {
		if (this.Value != null && getClassName() == "java.lang.Double") {
			// make this work the same as STR(Boolean.TRUE) in BBj
			// for compatibility reasons.
			// If it's a problem, we might introduce a COMPAT flag later.
			return new Float((Double)this.Value);
		}

		return (Float) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Date</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Date</code> object.
	 */
	public Date getDate() {
		if (this.Value != null){
			if (getClassName() == "java.sql.Timestamp") {
				long ms = ((java.sql.Timestamp) this.Value).getTime();
				return new java.sql.Date(ms);
			}
			if (getClassName() == "java.lang.Integer") {
				java.util.Date d = (java.util.Date) com.basis.util.BasisDate.date((Integer)this.Value);
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (getClassName() == "java.lang.Double") {
				java.util.Date d = (java.util.Date) com.basis.util.BasisDate.date(((Double)this.Value).intValue());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (getClassName() == "java.lang.String") {
				String s = (String)this.Value;
				if (s.isEmpty() || s.equals("-1"))
					return null;
			}
		}
		return (Date) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Time</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Time</code> object.
	 */
	public Time getTime() {
		return (Time) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Timestamp</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Timestamp</code> object.
	 */
	public Timestamp getTimestamp() {
		if (this.Value != null && getClassName() == "java.sql.Date") {
			long ms = ((java.sql.Date) this.Value).getTime();
			return new java.sql.Timestamp(ms);
		}
		if (this.Value != null && getClassName() == "java.lang.String") {
			String s = (String)this.Value;
			if (s.isEmpty())
				return null;
		}		
		return (Timestamp) this.Value;
	}

	/**
	 * Returns the DataField's value as byte array.
	 * 
	 * @return value The DataField's value as byte array.
	 */
	public byte[] getBytes() {
		return (byte[]) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Array</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Array</code> object.
	 */
	public Array getArray() {
		return (Array) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Blob</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Blob</code> object.
	 */
	public Blob getBlob() {
		return (Blob) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Clob</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Clob</code> object.
	 */
	public Clob getClob() {
		return (Clob) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.NClob</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.NClob</code> object.
	 */
	public NClob getNClob() {
		return (NClob) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.lang.Boolean</code> object.
	 * 
	 * @return value The DataField's value as <code>java.lang.Boolean</code> object.
	 */
	public Boolean getBoolean() {
		
		if (this.Value!=null){
			if (this.Value.getClass().equals(java.lang.String.class)){
				return ("trueTRUE1".indexOf((String)this.Value) > 0);
			};
			if (this.Value.getClass().equals(java.lang.Integer.class)){
				return ((Integer)this.Value > 0);
			};	
			if (this.Value.getClass().equals(java.lang.Double.class)){
				return ((Double)this.Value > 0);
			};
		}
		return (Boolean) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.Ref</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.Ref</code> object.
	 */
	public Ref getRef() {
		return (Ref) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.net.URL</code> object.
	 * 
	 * @return value The DataField's value as <code>java.net.URL</code> object.
	 */
	public URL getURL() {
		return (URL) this.Value;
	}

	/**
	 * Returns the DataField's value as <code>java.sql.SQLXML</code> object.
	 * 
	 * @return value The DataField's value as <code>java.sql.SQLXML</code> object.
	 */
	public SQLXML getSQLXML() {
		return (SQLXML) this.Value;
	}

	/**
	 * Sets the value of the attribute with the given name.
	 * Creates the attribute if it doesn't exist.
	 * 
	 * @param attributeName The attribute's name.
	 * @param attributeValue The attribute's value.
	 */
	public void setAttribute(String attributeName, String attributeValue) {
		this.attributes.put(attributeName, Attribute.createString(attributeValue));
	}

	public void setAttribute(String attributeName, Attribute attributeValue) {
		this.attributes.put(attributeName, attributeValue);
	}

	/**
	 * Returns the value for the given attribute name.
	 * <code>null</code> is returned if no attribute matches the given name.
	 * 
	 * @param attributeName The attribute's name.
	 * @return attributeValue The attribute's value.
	 */
	public String getAttribute(String attributeName) {
		return this.attributes.get(attributeName).getValue();
	}

	/**
	 * Returns a <code>java.util.HashMap</code> object with the DataField's 
	 * attributes and their corresponding value.
	 * 
	 * @return attributesMap The <code>java.util.HashMap</code> object containing the DataField's attributes and their values.
	 */
	public Map<String, String> getAttributes() {
		return this.attributes.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> e.getValue().getValue()));

	}

	/**
	 * Sets the attributes for this DataField.
	 * 
	 * @param attributes The attribute's <code>java.util.HashMap</code> object with the attributes and their corresponding values.
	 */
	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, e -> Attribute.createString(e.getValue())));
	}

	/**
	 * Removes the attribute with the given name. 
	 * Nothing happens in case the name doesn't exist.
	 * 
	 * @param attributeName The name of the attribute to remove.
	 */
	public void removeAttribute(String attributeName) {
		this.attributes.remove(attributeName);
	}

	@Override
	public DataField clone() {
		DataField f = null;
		try {
			f = new DataField(this.Value);
		} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

		if (f != null) {
			for (Entry<String, Attribute> e : attributes.entrySet()) {
				f.setAttribute(e.getKey(), e.getValue());
			}
		}
		return f;
	}

	@Override
	public String toString() {
		if (this.Value == null)
			return null;
		return this.Value.toString();
	}

	/**
	 * Sets the DataField's value to <code>null</code>
	 */
	public void clear() {
		setValue(null);
	}

	public static Object convertType(Object o, int targetType) throws ParseException {

		if (o == null) return null;

		String classname = o.getClass().getName();
		String tmpstr = o.toString();

		switch (targetType){
		// NOOP types first

		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			if(!classname.equals("java.lang.String")){
				return o.toString().getBytes();
			}
			return ((String)o).getBytes();
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
			//make Boolean special, for compatibility with BBj IF statements
			//want true as "1" and false as "0"
			if (classname.equals("java.lang.Boolean"))
				if ((Boolean)o)
					return "1";
				else
					return "0";
			if (classname.equals("java.math.BigDecimal"))
				return ((BigDecimal)o).stripTrailingZeros().toPlainString();
			if (!classname.equals("java.lang.String"))
				return o.toString();
			else
				return o;
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			if (classname.equals("java.lang.Boolean"))
				return o;
			if (classname.equals("java.lang.String")){
				String c = o.toString().toLowerCase();
				if (c.equals("true") || c.equals(".t.") || c.equals("1"))
					return true;
				else
					return false;
			}
			if (classname.equals("java.lang.Integer"))
				return (Integer)o > 0;
			if (classname.equals("java.lang.Double"))
				return (Double)o >0;
		    if (classname.contains("BBjNumber")||classname.contains("BBjInt"))
				return Double.parseDouble(o.toString())>0;
			break;

		case java.sql.Types.TINYINT:
		case java.sql.Types.BIGINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.INTEGER:
			if (classname.equals("java.lang.Integer"))
				return o;
			if (classname.equals("java.lang.Boolean"))
				return (Boolean)o ? 1:0;
			if (classname.equals("java.lang.Double"))
				return ((Double)o).intValue();
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return (Integer.parseInt(tmpstr));

		case java.sql.Types.DECIMAL:
			if (classname.equals("java.lang.Double"))
				return new BigDecimal((double) o);
			if (classname.equals("java.lang.Integer"))
				return new BigDecimal((int) o);
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return new BigDecimal(tmpstr);

		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
			if (classname.equals("java.lang.Double"))
				return o;
			if (classname.equals("java.lang.Boolean"))
				return (Boolean)o ? 1.0:0.0;
			if (tmpstr.isEmpty())
				tmpstr = "0.0";
			return (Double.parseDouble(tmpstr));
		case java.sql.Types.NUMERIC:
			if (tmpstr.isEmpty())
				tmpstr = "0.0";
			return new java.math.BigDecimal(tmpstr);
		case java.sql.Types.DATE:
			if (classname.equals("java.sql.Date"))
				return o;
			if (classname.contains("com.basis.util.common.BasisNumber") || classname.contains("com.basis.util.common.BBjNumber")) {
				com.basis.util.common.BasisNumber val = com.basis.util.common.BasisNumber.getBasisNumber((com.basis.util.common.BBjNumber)o);
				java.util.Date d = com.basis.util.BasisDate.date(val.intValueExact());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Integer")) {
				java.util.Date d = com.basis.util.BasisDate.date((Integer)o);
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Double")) {
				java.util.Date d = com.basis.util.BasisDate.date(((Double)o).intValue());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Long")) {
				return new java.sql.Date((long)o);
			}
			if (classname.equals("java.lang.String"))
				if (tmpstr.isEmpty())
					return null;
				else {
					if (tmpstr.indexOf('.') != -1)
						return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").parse((String)o).getTime());
					else if (tmpstr.indexOf(':') != -1)
						return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String)o).getTime());
					else
						return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(tmpstr).getTime());
				}
			break;

		case java.sql.Types.TIME:
			if(classname.equals("java.sql.Time"))
				return o;
			if(classname.equals("java.lang.String"))
				return new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse((String)o).getTime());
			break;
		case java.sql.Types.TIMESTAMP:
			if (classname.equals("java.sql.Timestamp"))
				return o;
			if (classname.equals("java.lang.Integer"))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date((Integer)o).getTime());
			if (classname.equals("java.lang.Double"))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date(((Double)o).intValue()).getTime());
			if (classname.equals("java.lang.String")){
				String p = ((String)o).replaceFirst("T", " ");
				if (tmpstr.isEmpty())
					return null;
				return java.sql.Timestamp.valueOf(p);
			}

			break;
		}

		if (classname.contains("DataField")){
			throw new IllegalArgumentException("Setting a DataField into a DataField is not supported");
		}

		String typeName = ResultSet.getSQLTypeName(targetType);
		if(typeName != null){
			System.out.println("warning: unclear type conversion for type " + targetType + "(" + typeName + ") and class " + classname);
		}else{
			System.out.println("warning: unclear type conversion for type " + targetType + " and class "+ classname);
		}

		return o;
	}

	public Map<String, Attribute> getAttributes2() {
		return new HashMap<>(this.attributes);
	}

}
