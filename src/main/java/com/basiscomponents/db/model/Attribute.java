package com.basiscomponents.db.model;

/**
 * Class to wrap Attributes for DataFields, so we can have
 * double/integer/boolean attributes
 * 
 * @author damore
 *
 */
public class Attribute {
	private Class<?> type;
	private Object value;

	public static Attribute createBoolean(boolean value) {
		Attribute attr = new Attribute();
		attr.type = boolean.class;
		attr.value = value;
		return attr;
	}

	public static Attribute createString(String value) {
		Attribute attr = new Attribute();
		attr.type = String.class;
		attr.value = value;
		return attr;
	}

	public static Attribute createInt(int value) {
		Attribute attr = new Attribute();
		attr.type = int.class;
		attr.value = value;
		return attr;
	}

	public static Attribute createDouble(double value) {
		Attribute attr = new Attribute();
		attr.type = double.class;
		attr.value = value;
		return attr;
	}

	public String getValue() {
		return String.valueOf(value);
	}

	public int getIntValue() {
		return (int) value;
	}
	public boolean getBooleanValue() {
		return (boolean) value;
	}

	public double getDoubleValue() {
		return (double) value;
	}

	public Class<?> getType() {
		return type;
	}

}
