package com.basiscomponents.db.util;

import com.basiscomponents.db.DataField;
import com.basiscomponents.db.ResultSet;

import java.math.BigDecimal;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DataFieldConverter {
	private static final String JAVA_MATH_BIG_DECIMAL = "java.math.BigDecimal";
	private static final String BBJ_INT = "BBjInt";
	private static final String BBJ_NUMBER = "BBjNumber";
	private static final String JAVA_LANG_DOUBLE = "java.lang.Double";
	private static final String JAVA_LANG_INTEGER = "java.lang.Integer";
	private static final String JAVA_LANG_BOOLEAN = "java.lang.Boolean";
	private static final String JAVA_LANG_STRING = "java.lang.String";

	private DataFieldConverter() {
	}
	public static Object convertType(Object o, int targetType) {

		if (o == null)
			return null;

		String classname = o.getClass().getName();
		String tmpstr = o.toString();

		switch (targetType) {
		// NOOP types first

		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			if (!classname.equals(JAVA_LANG_STRING)) {
				return o.toString().getBytes();
			}
			return ((String) o).getBytes();
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
			// make Boolean special, for compatibility with BBj IF statements
			// want true as "1" and false as "0"
			if (classname.equals(JAVA_LANG_BOOLEAN))
				return (Boolean) o ? "1" : "0";
			if (classname.equals(JAVA_MATH_BIG_DECIMAL))
				return ((BigDecimal) o).stripTrailingZeros().toPlainString();
			if (!classname.equals(JAVA_LANG_STRING))
				return o.toString();
			else
				return o;
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			if (classname.equals(JAVA_LANG_BOOLEAN))
				return o;
			if (classname.equals(JAVA_LANG_STRING)) {
				String c = o.toString().toLowerCase();
				return c.equals("true") || c.equals(".t.") || c.equals("1");
			}
			if (classname.equals(JAVA_LANG_INTEGER))
				return (Integer) o > 0;
			if (classname.equals(JAVA_LANG_DOUBLE))
				return (Double) o > 0;
			if (classname.contains(BBJ_NUMBER) || classname.contains(BBJ_INT))
				return Double.parseDouble(o.toString()) > 0;
			break;

		case java.sql.Types.TINYINT:
		case java.sql.Types.BIGINT:
		case java.sql.Types.SMALLINT:
		case java.sql.Types.INTEGER:
			if (classname.equals(JAVA_LANG_INTEGER))
				return o;
			if (classname.equals(JAVA_LANG_BOOLEAN))
				return (Boolean) o ? 1 : 0;
			if (classname.equals(JAVA_LANG_DOUBLE))
				return ((Double) o).intValue();
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return (Integer.parseInt(tmpstr));

		case java.sql.Types.DECIMAL:
			if (classname.equals(JAVA_LANG_DOUBLE))
				return BigDecimal.valueOf((double) o);
			if (classname.equals(JAVA_LANG_INTEGER))
				return new BigDecimal((int) o);
			if (tmpstr.isEmpty())
				tmpstr = "0";
			return new BigDecimal(tmpstr);

		case java.sql.Types.REAL:
		case java.sql.Types.DOUBLE:
			if (classname.equals(JAVA_LANG_DOUBLE))
				return o;
			if (classname.equals(JAVA_LANG_BOOLEAN))
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
					|| classname.contains("com.basis.startup.type.BBjNumber")) {
				com.basis.util.common.BasisNumber val = com.basis.util.common.BasisNumber
						.getBasisNumber((com.basis.startup.type.BBjNumber) o);
				java.util.Date d = com.basis.util.BasisDate.date(val.intValueExact());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals(JAVA_LANG_INTEGER)) {
				java.util.Date d = com.basis.util.BasisDate.date((Integer) o);
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals(JAVA_LANG_DOUBLE)) {
				java.util.Date d = com.basis.util.BasisDate.date(((Double) o).intValue());
				if (d != null)
					return new java.sql.Date(d.getTime());
				else
					return null;
			}
			if (classname.equals("java.lang.Long")) {
				return new java.sql.Date((long) o);
			}
			if (classname.equals(JAVA_LANG_STRING))
				if (tmpstr.isEmpty())
					return null;
				else {
					try {
						StringBuilder format;
						format=new StringBuilder("yyyy-MM-dd");
						if(tmpstr.contains("T")){
							format.append("'T'");
						}else{
							format.append(' ');
						}
						if(tmpstr.contains(":")){
							format.append("HH:mm:ss");
						}
						if (tmpstr.contains(".")){
							format.append(".SSS");
						}
						if(tmpstr.matches("\\d{4}-\\d{2}-\\d{2}[T\\s]+.*-\\d{2}:\\d{2}")){
							format.append('X');
						}
						return new java.sql.Date(new SimpleDateFormat(format.toString()).parse(tmpstr).getTime());

					} catch (ParseException e) {
						throw new IllegalStateException("Date [" + o + "] could not be parsed", e);
					}
				}
			break;

		case java.sql.Types.TIME:
			if (classname.equals("java.sql.Time"))
				return o;
			if (classname.equals(JAVA_LANG_STRING))
				try {
					return new java.sql.Time(new SimpleDateFormat("HH:mm:ss").parse((String) o).getTime());
				} catch (ParseException e) {
					throw new IllegalStateException("Time [" + o + "] could not be parsed", e);
				}
			break;
		case java.sql.Types.TIMESTAMP:
			if (classname.equals("java.sql.Timestamp"))
				return o;
			if (classname.equals(JAVA_LANG_INTEGER))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date((Integer) o).getTime());
			if (classname.equals(JAVA_LANG_DOUBLE))
				return new java.sql.Timestamp(com.basis.util.BasisDate.date(((Double) o).intValue()).getTime());
			if (classname.equals(JAVA_LANG_STRING)) {
				String p = ((String) o).replaceFirst("T", " ");
				if (tmpstr.isEmpty())
					return null;
				
				try {
					return java.sql.Timestamp.valueOf(p);	
				}
				catch (Exception e){
				}

				if (p.contains("+")) {
					String p1 = p.substring(0, p.indexOf('+'));
					try {
						return java.sql.Timestamp.valueOf(p1);	
					}
					catch (Exception e){
					}

				}
				if (p.contains("-")) {
					String p1 = p.substring(0, p.lastIndexOf('-'));
					try {
						return java.sql.Timestamp.valueOf(p1);
					}
					catch (Exception e){
					}

				}
				System.err.println("ERROR PARSING DATE "+p);
			}

			break;
		default:
			if (classname.contains("DataField")) {
				throw new IllegalArgumentException("Setting a DataField into a DataField is not supported");
			}

			String typeName = ResultSet.getSQLTypeName(targetType);
			if (typeName != null) {
				System.out.println("warning: unclear type conversion for type " + targetType + "(" + typeName
						+ ") and class " + classname);
			} else {
				System.out
						.println("warning: unclear type conversion for type " + targetType + " and class " + classname);
			}

		}
		return o;
	}

	public static Double fieldToNumber(ResultSet resultSet, DataField field, int column, int type) {
		if (field.getValue() == null) {
			if (type == java.sql.Types.DATE || type == java.sql.Types.TIMESTAMP
					|| type == java.sql.Types.TIMESTAMP_WITH_TIMEZONE)
				return -1d;
			else
				return 0.0;
		}

		Double ret;

		// TODO maybe: make this use reflection and skip the field for the
		// column type, to honor dynamic type changes??
		switch (type) {
		case java.sql.Types.CHAR:
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
		case java.sql.Types.NCHAR:
		case java.sql.Types.NVARCHAR:
		case java.sql.Types.LONGNVARCHAR:
			String tmp = field.getString();
			if (tmp.isEmpty())
				tmp = "0.0";
			ret = Double.valueOf(tmp);
			break;
		case java.sql.Types.INTEGER:
		case java.sql.Types.SMALLINT:
			/*
			 * Columns with an unsigned numeric type in MySQL are treated as the next
			 * 'larger' Java type that the signed variant of the MySQL:
			 * http://www.mysqlab.net/knowledge/kb/detail/topic/java/id/4929
			 * 
			 * In the populate method, the value of an unsigned integer is stored as
			 * java.lang.Long in the DataField although its type remains
			 * java.sql.Types.INTEGER in the Column metadata. Calling the getInt() method
			 * will then result in an Exception. This checks prevents this Exception.
			 */
			if (field.getClassName().equals("java.lang.Short"))
				ret=field.getShort().doubleValue();
			else
				if (!resultSet.isSigned(column)) {
					ret = field.getLong().doubleValue();
				} else {
					ret = field.getInt().doubleValue();
				}
			break;
		case java.sql.Types.BIGINT:
			/*
			 * Columns with an unsigned numeric type in MySQL are treated as the next
			 * 'larger' Java type that the signed variant of the MySQL:
			 * http://www.mysqlab.net/knowledge/kb/detail/topic/java/id/4929
			 * 
			 * In the populate method, the value of an unsigned big integer is stored as
			 * java.math.BigInteger in the DataField although its type remains
			 * java.sql.Types.BIGINT in the Column metadata. Calling the getLong() method
			 * will then result in an Exception. This checks prevents this Exception.
			 */
			if (!resultSet.isSigned(column)) {
				ret = ((java.math.BigInteger) field.getValue()).doubleValue();
			} else {
				ret = field.getLong().doubleValue();
			}
			break;
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			ret = field.getBigDecimal().doubleValue();
			break;
		case java.sql.Types.DOUBLE:
		case java.sql.Types.FLOAT:
			ret = field.getDouble();
			break;
		case java.sql.Types.REAL:
			ret = field.getFloat().doubleValue();
			break;
		case java.sql.Types.DATE:
		case java.sql.Types.TIMESTAMP:
		case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
			if (field.getDate() == null)
				ret = -1.0;
			else {
				Integer ret2 = com.basis.util.BasisDate.jul(new java.util.Date(field.getDate().getTime()));
				ret = ret2.doubleValue();
			}
			break;
		case java.sql.Types.TIME:
		case java.sql.Types.TIME_WITH_TIMEZONE:
			Time t = field.getTime();
			Double d = (double) t.getHours();
			Double d1 = (double) t.getMinutes();
			d1 = d1 / 60;
			d += d1;
			d1 = (double) t.getSeconds();
			d1 = d1 / 3600;
			d += d1;
			ret = d;
			break;
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			if (field.getBoolean())
				ret = 1.0;
			else
				ret = 0.0;
			break;
		case java.sql.Types.TINYINT:
			ret = field.getInt().doubleValue();
			break;
		default:
			ret = null;
			break;
		}
		return ret;
	}
}
