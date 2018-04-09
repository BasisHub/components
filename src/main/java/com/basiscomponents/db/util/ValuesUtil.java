package com.basiscomponents.db.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.basis.bbj.client.datatypes.BBjVector;
import com.basiscomponents.db.ResultSet;

public class ValuesUtil {
	private ValuesUtil() {
	}

	public static List<String> fieldValuesAsList(ResultSet rs, String columnName){
		return fieldValuesAsList(rs, columnName, false, true);
	}

	public static List<String> fieldValuesAsList(ResultSet rs, String columnName, boolean addEmptyRow,
			boolean unique) {
		Stream<String> values = rs.getDataRows().stream().map(dr -> dr.getFieldAsString(columnName).trim());
		values = values.sorted();
		if (addEmptyRow) {
			Stream.concat(values, Stream.of(""));
		}
		if (unique) {
			values = values.distinct();
		}
		List<String> result = new ArrayList<>();
		result.addAll(values.collect(Collectors.toList()));
		return result;
	}

	public static BBjVector fieldValuesAsVector(ResultSet rs, String columnName) {
		return new BBjVector(fieldValuesAsList(rs, columnName));
	}

	public static BBjVector fieldValuesAsVector(ResultSet rs, String columnName, boolean addEmptyRow, boolean unique) {
		return new BBjVector(fieldValuesAsList(rs, columnName, addEmptyRow, unique));
	}

	public static String listAsString(List<String> list) {
		return listAsString(list, "\n");
	}

	public static String listAsString(List<String> list, String delimiter) {
		return list.stream().collect(Collectors.joining(delimiter));
	}
}
