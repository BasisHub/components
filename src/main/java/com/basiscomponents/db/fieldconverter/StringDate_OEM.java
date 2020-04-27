package com.basiscomponents.db.fieldconverter;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.basis.dateformat.LegacyDateFormatBridge;
import com.basis.startup.type.dateformat.BBjDateFormat;
import com.basis.startup.type.dateformat.BBjDateFormatException;
import com.basiscomponents.db.DataField;


public class StringDate_OEM implements IConversionRule, Serializable {

	private static final long serialVersionUID = 1L;

	String dateType;
	int storedLen;
	BBjDateFormat dateFmt;

	@SuppressWarnings("unused")
	private StringDate_OEM() {
	}

	public StringDate_OEM(String dateType, int storedLen) throws BBjDateFormatException {
		this.dateFmt = LegacyDateFormatBridge.getInstance("com.basis.dateformat." + dateType, null);
		this.dateType = dateType;
		this.storedLen = storedLen;
	}

	/**
	 * Convert from external (target) type value to internal (source) type value
	 * "going down"
	 */
	@Override
	public DataField serialize(DataField field) {
		Date date = (Date) field.getValue();
		String val = null;
		if (date != null) {
			Calendar cal = new GregorianCalendar();
			cal.setTime(date);
			byte[] bytes = new byte[storedLen];
			try {
				// byte[] toBytes(Calendar p_source, int p_bufferSize, byte[] p_expectedFormat)
				bytes = dateFmt.toBytes(cal, storedLen, null);
			} catch (BBjDateFormatException e) {
				System.err.println(e.getMessage() + " : " + date);
			}
			val = new String(bytes);
		}
		return new DataField(val);
	}

	/**
	 * Convert from internal (source) type value to external (target) type value
	 * "going up"
	 */
	@Override
	public DataField deserialize(DataField field) {
		String val = field.getObject().toString();
		Date date = null;
		if (val != null && !(val.trim().isEmpty())) {
			Calendar cal = GregorianCalendar.getInstance();
			try {
				// Calendar toCalendar(byte[] p_source, Calendar p_destination)
				cal = dateFmt.toCalendar(val.getBytes(), cal);
			} catch (BBjDateFormatException e) {
				System.err.println(e.getMessage() + " : " + val);
			}
			if (cal != null) date = new java.sql.Date(cal.getTimeInMillis());
		}
		return new DataField(date);
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.DATE;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
