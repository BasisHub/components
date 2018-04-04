package com.basiscomponents.db.util;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.basiscomponents.db.ResultSet;

public class ResultSetValuesListProvider {
	private ResultSetValuesListProvider() {
	}
	public static List<String> getFieldValuesAsList(ResultSet rs, String columnName){
		return getFieldValuesAsList(rs, columnName, false);
	}

	public static List<String> getFieldValuesAsList(ResultSet rs, String columnName, boolean addEmptyRow) {
		Stream<String> values = rs.getDataRows().stream().map(dr -> dr.getFieldAsString(columnName));
		if (!addEmptyRow) {
			values = values.filter(Objects::nonNull);
		}
		return values.collect(Collectors.toList());
	}

	public static String getFieldValuesListAsString(List<String> list, String delimiter) {
		return list.stream().collect(Collectors.joining(delimiter));
	}
}
