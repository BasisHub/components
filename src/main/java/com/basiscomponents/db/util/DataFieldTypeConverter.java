package com.basiscomponents.db.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.basiscomponents.db.ResultSet;

public class DataFieldTypeConverter {
	private DataFieldTypeConverter() {
		// no instance needed
	}

	public static Object convertType(Object o, int targetType) throws ParseException {

		if (o == null) {
			return null;
		}
		String classname = o.getClass().getName();
		String tmpstr = o.toString();

		switch (targetType) {
		// NOOP types first

		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			if (!classname.equals("java.lang.String")) {
				return o.toString().getBytes();
			}
			return ((String) o).getBytes();
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
			// make Boolean special, for compatibility with BBj IF statements
			// want true as "1" and false as "0"
			if (classname.equals("java.lang.Boolean"))
				if ((Boolean) o)
					return "1";
				else
					return "0";
			if (classname.equals("java.math.BigDecimal"))
				return ((BigDecimal) o).stripTrailingZeros().toPlainString();
			if (!classname.equals("java.lang.String"))
				return o.toString();
			else
				return o;
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			if (classname.equals("java.lang.Boolean"))
				return o;
			if (classname.equals("java.lang.String")) {
				String c = o.toString().toLowerCase();
				if (c.equals("true") || c.equals(".t.") || c.equals("1"))
					return true;
				else
					return false;
			}
			if (classname.equals("java.lang.Integer"))
				return (Integer) o > 0;
			if (classname.equals("java.lang.Double"))
				return (Double) o > 0;
			if (classname.contains("BBjNumber") || classname.contains("BBjInt"))
				return Double.parseDouble(o.toString()) > 0;
			break;

		case java.sql.Types.TINYINT:
		case java.sql.Types.BIGINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.INTEGER:
			if (classname.equals("java.lang.Integer"))
				return o;
			if (classname.equals("java.lang.Boolean"))
				return (Boolean) o ? 1 : 0;
			if (classname.equals("java.lang.Double"))
				return ((Double) o).intValue();
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return (Integer.parseInt(tmpstr));

		case java.sql.Types.DECIMAL:
			if (classname.equals("java.lang.Double"))
				return BigDecimal.valueOf((double) o);
			if (classname.equals("java.lang.Integer"))
				return BigDecimal.valueOf((int) o);
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return new BigDecimal(tmpstr);

		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
			if (classname.equals("java.lang.Double"))
				return o;
			if (classname.equals("java.lang.Boolean"))
				return (Boolean) o ? 1.0 : 0.0;
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
			if (classname.contains("com.basis.util.common.BasisNumber")
					|| classname.contains("com.basis.util.common.BBjNumber")) {
				com.basis.util.common.BasisNumber val = com.basis.util.common.BasisNumber
						.getBasisNumber((com.basis.util.common.BBjNumber) o);
				java.util.Date d = com.basis.util.BasisDate.date(val.intValueExact());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Integer")) {
				java.util.Date d = com.basis.util.BasisDate.date((Integer) o);
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Double")) {
				java.util.Date d = com.basis.util.BasisDate.date(((Double) o).intValue());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Long")) {
				return new java.sql.Date((long) o);
			}
			if (classname.equals("java.lang.String"))
				if (tmpstr.isEmpty())
					return null;
				else {
					if (tmpstr.indexOf('.') != -1)
						return new java.sql.Date(
								new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S").parse((String) o).getTime());
					else if (tmpstr.indexOf(':') != -1)
						return new java.sql.Date(
								new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse((String) o).getTime());
					else
						return new java.sql.Date(new SimpleDateFormat("yyyy-MM-dd").parse(tmpstr).getTime());
				}
			break;

		case java.sql.Types.TIME:
			if (classname.equals("java.sql.Time"))
				return o;
			if (classname.equals("java.lang.String"))
				return new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse((String) o).getTime());
			break;
		case java.sql.Types.TIMESTAMP:
			if (classname.equals("java.sql.Timestamp"))
				return o;
			if (classname.equals("java.lang.Integer"))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date((Integer) o).getTime());
			if (classname.equals("java.lang.Double"))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date(((Double) o).intValue()).getTime());
			if (classname.equals("java.lang.String")) {
				String p = ((String) o).replaceFirst("T", " ");
				if (tmpstr.isEmpty())
					return null;

				// split off timezone
				// TODO: detect timezone by offset and adjust the timestamp accordingly
				// TODO: find a better performing implementation

				String tz_offs = "";
				if (p.contains("+")) {
					tz_offs = tmpstr.substring(p.indexOf('+'));
					p = p.substring(0, p.indexOf('+'));
				}
				p = p.replaceFirst("-", "X");
				p = p.replaceFirst("-", "X");
				if (p.contains("-")) {
					tz_offs = p.substring(p.indexOf('-'));
					p = p.substring(0, p.indexOf('-') - 1);
				}
				p = p.replaceFirst("X", "-");
				p = p.replaceFirst("X", "-");
				return java.sql.Timestamp.valueOf(p);
			}

			break;
		}

		if (classname.contains("DataField")) {
			throw new IllegalArgumentException("Setting a DataField into a DataField is not supported");
		}

		String typeName = ResultSet.getSQLTypeName(targetType);
		if (typeName != null) {
			System.out.println("warning: unclear type conversion for type " + targetType + "(" + typeName
					+ ") and class " + classname);
		} else {
			System.out.println("warning: unclear type conversion for type " + targetType + " and class " + classname);
		}

		return o;
	}
}
