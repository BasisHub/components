package com.basiscomponents.db.fieldconverter;

import com.basiscomponents.db.DataField;

public class StringYMD implements IConversionRule {

	String mode;
	String fmt;
	String sep;

	@SuppressWarnings("unused")
	private StringYMD() {
	}

	/**
	 *
	 * @param mode Date mode (YM, MD)
	 * @param fmt Date format (MDY, DMY, YMD)
	 */
	public StringYMD(String mode, String fmt) {
		this.mode = mode;
		this.fmt = fmt;
	}

	/**
	 * Convert from external (target) type value to internal (source) type value
	 * "going down".
	 */
	@Override
	public DataField serialize(DataField field) {
		String val = field.getString().trim();

		// YYYYMM (on disk)
		if (mode == "YM" && val.length() == 6) {
			if (fmt == "MDY" || fmt == "DMY") {
				val =  val.substring(0, 4) + val.substring(4, 6);
			}
		} else {
			// YYMM (on disk)
			if (mode == "YM" && val.length() == 4) {
				if (fmt == "MDY" || fmt == "DMY") {
					val =  val.substring(0, 2) + val.substring(2, 4);
				}
			} else {
				// MMDD (on disk)
				if (mode == "MD" && val.length() == 4) {
					if (fmt == "DMY") {
						val = val.substring(0, 2) + val.substring(2, 4);
					}
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

		// YYYYMM (on disk)
		if (mode == "YM" && val.length() == 6) {
			if (fmt == "MDY" || fmt == "DMY") {
				val = val.substring(4, 6) + val.substring(0, 4);
			}
		} else {
			// YYMM (on disk)
			if (mode == "YM" && val.length() == 4) {
				if (fmt == "MDY" || fmt == "DMY") {
					val = val.substring(2, 4) + val.substring(0, 2);
				}
			} else {
				// MMDD (on disk)
				if (mode == "MD" && val.length() == 4) {
					if (fmt == "DMY") {
						val = val.substring(2, 4) + val.substring(0, 2);
					}
				}
			}
		}
		return new DataField(val);
	}

	@Override
	public int getTargetFieldType() {
		return java.sql.Types.VARCHAR;
	}

	@Override
	public int getSourceFieldType() {
		return java.sql.Types.VARCHAR;
	}

}
