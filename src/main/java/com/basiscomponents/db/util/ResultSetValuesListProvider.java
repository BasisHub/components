package com.basiscomponents.db.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.basiscomponents.db.ResultSet;

public class ResultSetValuesListProvider {
	private ResultSetValuesListProvider() {
	}
	public static List<String> getFieldValuesAsList(ResultSet rs, String columnName){
		return getFieldValuesAsList(rs, columnName, false, true);
	}

	public static List<String> getFieldValuesAsList(ResultSet rs, String columnName, boolean addEmptyRow,
			boolean unique) {
		Stream<String> values = rs.getDataRows().stream().map(dr -> dr.getFieldAsString(columnName));
		if (unique) {
			values = values.distinct();
		}

		values = values.sorted();
		List<String> result = new ArrayList<>();

		if (!addEmptyRow) {
			result.add("");
		}
		result.addAll(values.collect(Collectors.toList()));
		return result;
	}

	public static String getFieldValuesListAsString(List<String> list, String delimiter) {
		return list.stream().collect(Collectors.joining(delimiter));
	}
}
