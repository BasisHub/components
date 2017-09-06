package com.basiscomponents.db;

import java.util.Comparator;


public class DataRowMultifieldComparator implements Comparator<DataRow> {

	private String[] fields;

	public DataRowMultifieldComparator(String fieldList) throws Exception {
		fieldList = fieldList.trim().toUpperCase();
		this.fields = fieldList.split("\\s*,\\s*");

		for (String field : fields) {
			String[] split = field.split("\\s+");
			if (split.length > 2) {
				throw new Exception("Invalid sort field name: "+field);
			}
			if (split.length == 2) {
				if (!split[1].equals("ASC") && !split[1].equals("DESC"))
					throw new Exception("Unsupported order direction: "+split[1]+". Allowed is ASC or DESC");
				else
					field = split[0]+" "+split[1];
			}
		}
	}

	@Override
	public int compare(DataRow dr1, DataRow dr2) {
		int returnVal=0;

		for(String field : fields) {
			int direction = 1;
			int fieldType;

			String[] split = field.split(" ");
			if (split.length == 2) {
				field = split[0];
				direction = split[1].equals("DESC")? -1 : 1;
			}

			Object val1 = null;
			Object val2 = null;

			try {
				val1 = dr1.getDataField(field).getObject();
			} catch(Exception ex) {}

			try {
				val2 = dr2.getDataField(field).getObject();
			} catch (Exception ex) {}

			if (val1 == null && val2 == null)
				continue;
			else if (val1 == null)
				return -1 * direction;
			else if (val2 == null)
				return 1 * direction;


			try {
				fieldType = dr1.getFieldType(field);
			} catch (Exception e) {
				fieldType = java.sql.Types.CHAR;
			}

			DataField f1 = dr1.getDataField(field);
			DataField f2 = dr2.getDataField(field);

			switch(fieldType) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
				case java.sql.Types.NVARCHAR:
				case java.sql.Types.NCHAR:
				case java.sql.Types.LONGVARCHAR:
				case java.sql.Types.LONGNVARCHAR:
					String s1 = f1.getString();
					String s2 = f2.getString();
					returnVal = s1.compareToIgnoreCase(s2) * direction;
					break;
				case java.sql.Types.BIGINT:
					long lng1 = f1.getLong();
					long lng2 = f2.getLong();
					returnVal = Long.compare(lng1, lng2) * direction;
					break;
				case java.sql.Types.TINYINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.SMALLINT:
				case 9:
				case 11:
					int int1 = f1.getInt();
					int int2 = f2.getInt();
					returnVal = Integer.compare(int1, int2) * direction;
					break;
				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
				case java.sql.Types.REAL:
					double dbl1 = f1.getDouble();
					double dbl2 = f2.getDouble();
					returnVal = Double.compare(dbl1, dbl2) * direction;
					break;
				case java.sql.Types.NUMERIC:
				case java.sql.Types.DECIMAL:
					java.math.BigDecimal bd1 = f1.getBigDecimal();
					java.math.BigDecimal bd2 = f2.getBigDecimal();
					returnVal = bd1.compareTo(bd2) * direction;
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					boolean b1 = f1.getBoolean();
					boolean b2 = f2.getBoolean();
					returnVal = Boolean.compare(b1, b2) * direction;
					break;
				case java.sql.Types.DATE:
					java.sql.Date d1 = f1.getDate();
					java.sql.Date d2 = f2.getDate();
					returnVal = d1.compareTo(d2) * direction;
					break;
				case java.sql.Types.TIMESTAMP:
				case java.sql.Types.TIMESTAMP_WITH_TIMEZONE:
					java.sql.Timestamp t1 = f1.getTimestamp();
					java.sql.Timestamp t2 = f2.getTimestamp();
					returnVal = t1.compareTo(t2) * direction;
					break;
			}

			if (returnVal != 0) return returnVal;
		}

		return returnVal;
	}
}
