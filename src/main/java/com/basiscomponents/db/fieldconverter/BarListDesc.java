package com.basiscomponents.db.fieldconverter;

import java.text.ParseException;
import java.util.Arrays;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

public class BarListDesc implements IConversionRule {

	String codeCol, descCol, attr_ldat, attr_opts;

	@SuppressWarnings("unused")
	private BarListDesc() {
	}

	public BarListDesc(String codeCol, String descCol, String attr_ldat, String attr_opts) {
		this.codeCol = codeCol;
		this.descCol = descCol;
		this.attr_ldat = attr_ldat;
		this.attr_opts = attr_opts;
	}

	/**
	 * Convert from external (target) type value to internal (source) type value
	 * "going down". In this case, we don't convert the value at all. We take the
	 * value and build the description column.
	 */
	@Override
	public DataField serialize(DataField field, DataRow dr) {
		setDesc(field, dr);
		return field;
	}

	/**
	 * Convert from internal (source) type value to external (target) type value
	 * "going up". In this case, we don't convert the value at all. We take the
	 * value and build the description column.
	 */
	@Override
	public DataField deserialize(DataField field, DataRow dr) {
		setDesc(field, dr);
		return field;
	}

	@Override
	public int getTargetFieldType() {
		return 12; // java.sql.Types.VARCHAR (aka String)
	}

	@Override
	public int getSourceFieldType() {
		return 12; // java.sql.Types.VARCHAR (aka String)
	}

	/**
	 * Use the code column's value to get and format the description column's value.
	 */
	public void setDesc(DataField field, DataRow dr) {
		String desc = "";

		// System.out.println("BarListDesc::setDesc:codeCol=" + codeCol); // "COST_METHOD"
		// System.out.println("BarListDesc::setDesc:descCol=" + descCol); // "COST_METHOD_DESC"
		// System.out.println("BarListDesc::setDesc:attr_ldat=" + attr_ldat); // "FIFO     ~FIFO  ;LIFO     ~LIFO  ;" (C(64)~C(20);..)
		// System.out.println("BarListDesc::setDesc:attr_opts=" + attr_opts); // ""

		if (!attr_ldat.isEmpty()) {
			String code = dr.getFieldAsString(codeCol).trim();
			// System.out.println("BarListDesc::setDesc:code=" + code); // "FIFO"
			String[] descs = attr_ldat.split(";");
			// System.out.println("BarListDesc::setDesc:descs=" + Arrays.toString(descs)); // [FIFO     ~FIFO  ,LIFO     ~LIFO  ]

			for (String s : descs) {
				// System.out.println("BarListDesc::setDesc:s=" + s); // "FIFO     ~FIFO  " (C(64)~C(20)
				int i = s.lastIndexOf("~");
				// System.out.println("BarListDesc::setDesc:s.lastIndexOf(\"~\")=" + i); // 64
				// System.out.println("BarListDesc::setDesc:s.substring(i + 1).trim()=" + s.substring(i + 1).trim()); // "FIFO"
				if (i != -1 && s.substring(i + 1).trim().equals(code)) {
					desc = s.substring(0, i).trim();
					if (attr_opts.indexOf("o") == -1 && attr_opts.indexOf("k") == -1) {
						desc = desc + " (" + code + ")";
					}
					if (attr_opts.indexOf("o") != -1) {
						desc = code + "-" + desc;
					}
					// "k" means just the desc alone
					break;
				}
			}
		}
		try {
			// System.out.println("BarListDesc::setDesc:desc=" + desc); // "FIFO (FIFO)" (default format)
			dr.setFieldValue(descCol, desc);
		} catch (ParseException e) {
			// do nothing
		}
	}

}
