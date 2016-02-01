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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class DataField implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private Object Value;

	private HashMap<String, String> Attributes = new HashMap<String, String>();

	public DataField(Object value) {
		setValue(value);
	}

	public Object getValue() {
		return this.Value;
	}

	public void setValue(Object value) {
		this.Value = value;
	}

	public String getClassName() {
		return this.Value.getClass().getCanonicalName();
	}

	public Object getObject() {
		return this.Value;
	}

	public String getString() {
		return (String) this.Value.toString();
	}

	public Integer getInt() {
		return (Integer) this.Value;
	}

	public Byte getByte() {
		return (Byte) this.Value;
	}

	public Short getShort() {
		return (Short) this.Value;
	}

	public Long getLong() {
		return (Long) this.Value;
	}

	public BigDecimal getBigDecimal() {
		return (BigDecimal) this.Value;
	}

	public Double getDouble() {
		return (Double) this.Value;
	}

	public Float getFloat() {
		return (Float) this.Value;
	}

	public Date getDate() {
		return (Date) this.Value;
	}

	public Time getTime() {
		return (Time) this.Value;
	}

	public Timestamp getTimestamp() {
		return (Timestamp) this.Value;
	}

	public byte[] getBytes() {
		return (byte[]) this.Value;
	}

	public Array getArray() {
		return (Array) this.Value;
	}

	public Blob getBlob() {
		return (Blob) this.Value;
	}

	public Clob getClob() {
		return (Clob) this.Value;
	}

	public NClob getNClob() {
		return (NClob) this.Value;
	}

	public Boolean getBoolean() {
		return (Boolean) this.Value;
	}

	public Ref getRef() {
		return (Ref) this.Value;
	}

	public URL getURL() {
		return (URL) this.Value;
	}

	public SQLXML getSQLXML() {
		return (SQLXML) this.Value;
	}

	public void setAttribute(String name, String value) {
		this.Attributes.put(name, value);
	}

	public String getAttribute(String name) {
		return this.Attributes.get(name);
	}

	@SuppressWarnings("unchecked")
	public HashMap<String, String> getAttributes() {
		return (HashMap<String, String>) this.Attributes.clone();
	}

	public void removeAttribute(String name) {
		this.Attributes.remove(name);
	}

	public DataField clone() {
		DataField f = null;
		try {
			f = new DataField(this.Value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (f != null) {
			Set<String> ks = this.Attributes.keySet();
			Iterator<String> it = ks.iterator();
			while (it.hasNext()) {
				String k = it.next();
				String v = this.Attributes.get(k);
				f.setAttribute(k, v);
			}
		}
		return f;
	}

	public String toString() {
		return this.Value.toString();
	}

}
