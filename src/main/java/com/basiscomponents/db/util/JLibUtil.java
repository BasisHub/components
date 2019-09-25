package com.basiscomponents.db.util;

import com.basis.bbj.datatypes.TemplatedString;
import com.basiscomponents.db.DataField;
import com.basiscomponents.db.DataRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

public class JLibUtil {
	private static class DFWrapper{
		final int size;
		final DataField dataField;
		final byte type;


		public DFWrapper(final DataField dataField, final byte type, final int size) {
			this.dataField= dataField;
			this.type = type;
			this.size = size;
		}
	}

	private static final Collector<DFWrapper,StringBuilder,byte[]> collector = new Collector<DFWrapper, StringBuilder, byte[]>(){

		@Override
		public Supplier<StringBuilder> supplier() {
			return StringBuilder::new;
		}

		@Override
		public BiConsumer<StringBuilder, DFWrapper> accumulator() {
			return (builder,b)->{
				String value;
				int padding=0;
				if(isNumericType(b.type)){

					value =b.dataField.getDouble().toString();
					if (value.endsWith(".0")){
						value = value.substring(0,value.length()-2);
					}
					padding = b.size-value.length()-1;

				}else{
					value = b.dataField.getString();
				}
				builder.append(value);
				for (int i = 0; i <padding; i++) {
					builder.append(' ');
				}
				if (!isNumericType(b.type)){
					builder.append('\n');
				}
			};
		}

		@Override
		public BinaryOperator<StringBuilder> combiner() {
			return (x,y)-> {
				x.append(y.toString());
				return x;
			};
		}

		@Override
		public Function<StringBuilder, byte[]> finisher() {
			return x->x.toString().substring(0,x.length()-1).getBytes();
		}

		@Override
		public Set<Characteristics> characteristics() {
			return new HashSet<>();
		}
	};
	public static boolean isNumericType(final int type) {
		return type == 'B' || type == 'D' || type == 'F' || type == 'N' || type == 'X' || type == 'Y';
	}
	/**
	 * Returns true if the given byte Array is empty, false otherwise.
	 *
	 * @param buffer
	 *            The byte Array
	 *
	 * @return True if the byte Array is empty, false otherwise.
	 */
	public static boolean isRecordEmpty(byte[] buffer) {
		for (byte b : buffer) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}
	/**
	 * Parses the given Templated String using the given field names(Map) and
	 * returns a list of indexes with the field's defined as Numeric fields in the
	 * templated String.
	 *
	 * @param templatedString
	 *            The Templated String to parse.
	 * @param fieldMap
	 *            The name of the fields
	 *
	 * @return A list with the field indexes which are defined as Numeric fields.
	 *
	 * @throws IndexOutOfBoundsException
	 * @throws NoSuchFieldException
	 */

	public static List<Integer> getNumericFieldsIndeces(final TemplatedString templatedString, final Map<Integer, String> fieldMap)
			throws NoSuchFieldException {
		ArrayList<Integer> indexList = new ArrayList<>();

		int index = 0;
		int type;

		for (Integer key:fieldMap.keySet()) {
			type = templatedString.getFieldType(key);
			if (isNumericType(type)) {
				indexList.add(index);
			}

			index++;
		}

		return indexList;
	}
	public static byte[] toByteArray(DataRow dr, final TemplatedString bbjtemplate) {
		List<String> templatedStringFieldNameList = Arrays
				.asList(bbjtemplate.getFieldNames().toString().split("\n"));

		final Stream<DFWrapper> stream = templatedStringFieldNameList.stream().map(fieldname -> {
			try {
				return new DFWrapper(dr.getField(fieldname),bbjtemplate.getFieldType(fieldname),bbjtemplate.getFieldSize(fieldname));
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}
			return new DFWrapper(null, (byte) 'X',0);
		});
		return stream.collect(JLibUtil.collector);
	}
}
