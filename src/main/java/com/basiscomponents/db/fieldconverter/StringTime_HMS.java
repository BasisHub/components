package com.basiscomponents.db.fieldconverter;

import java.sql.Time;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.DataField;

public class StringTime_HMS implements IConversionRule {

	SimpleDateFormat sdf = new SimpleDateFormat("kkmmss"); // kk=midnight is 24:00, HH=midnight is 00:00

	String mode;

	@SuppressWarnings("unused")
	private StringTime_HMS() {
	}

	/**
	 *
	 * @param mode Time mode (HMS, HM, MS)
	 */
	public StringTime_HMS(String mode) {
		this.mode = mode;
	}

	/**
	 * Convert from external (target) type value to internal (source) type value
	 * "going down".
	 */
	@Override
	public DataField serialize(DataField field) {
		String val;
		if (field.getValue() == null) {
			val = "";
		} else {
			val = sdf.format(field.getValue());
			if (mode == "HM") {
				val = val.substring(0, 4);
			} else {
				if (mode == "MS") {
					val = val.substring(2, 6);
				}
			}
		}
		return new DataField(val);
	}

	/**
	 * Convert from internal (source) type value to external (target) type value
	 * "going up".
	 */
	@Override
	public DataField deserialize(DataField field) {
		String val = field.getString().trim();

		if (mode == "HMS" && val.length() == 6) {
			val = val.substring(0, 2) + ":" + val.substring(2, 4) + ":" + val.substring(4, 6);
		} else {
			if (mode == "HM" && val.length() == 4) {
				val = val.substring(0, 2) + ":" + val.substring(2, 4) + ":00";
			} else {
				if (mode == "MS" && val.length() == 4) {
					val = "00:" + val.substring(2, 4) + ":" + val.substring(4, 6);
				}
			}
		}

		try {
			Time tim = Time.valueOf(val);
			return new DataField(tim);
		} catch (java.lang.IllegalArgumentException e) {
			System.err.println("Invalid HMS time string: " + field.getValue().toString());
		}

		return null;
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.TIME;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
