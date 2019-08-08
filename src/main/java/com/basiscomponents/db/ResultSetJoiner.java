package com.basiscomponents.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResultSetJoiner {

	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName) throws ParseException {
		return ResultSetJoiner.leftJoin(left, right, joinFieldName, null);
	}

	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName,
			List<String> targetFieldNames)
			throws ParseException {
		
		if (targetFieldNames == null || targetFieldNames.isEmpty()) {
			targetFieldNames = right.get(0).getFieldNames();
			targetFieldNames.remove(joinFieldName);
		}

		// Building an HashMap out of the right ResultSet and the targetNames
		HashMap<Object, List<Object>> targetData = new HashMap<Object, List<Object>>();
		for( DataRow dr: right) {
			targetData.put(dr.getFieldValue(joinFieldName),
					getObjectValuesInOrderFromTargetFields(dr, targetFieldNames));
		}
		
		// Joining the left ResultSet with the data from the right ResultSet
		for( DataRow dr: left) {
			List<Object> currentJoinItems = targetData.get(dr.getFieldValue(joinFieldName));
			for (int i = 0; i < currentJoinItems.size(); i++) {
				dr.setFieldValue(targetFieldNames.get(i), currentJoinItems.get(i));
			}
		}
		
		return left;
	}

	private static List<Object> getObjectValuesInOrderFromTargetFields(DataRow dr, List<String> targetFieldNames) {

		List<Object> targetObjects = new ArrayList<Object>();
		for (int i = 0; i < targetFieldNames.size(); i++) {
			targetObjects.add(dr.getFieldValue(targetFieldNames.get(i)));
		}

		return targetObjects;

	}
}
