package com.basiscomponents.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetJoiner {

	private ResultSetJoiner() {
	}

	/**
	 * @See @MainLeftJoinMethod
	 */
	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName) throws ParseException {
		return ResultSetJoiner.leftJoin(left, right, joinFieldName, null, null);
	}

	/**
	 * @See @MainLeftJoinMethod
	 */
	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName,
			List<String> targetFieldNames) throws ParseException {
		return ResultSetJoiner.leftJoin(left, right, joinFieldName, targetFieldNames, null);
	}

	/**
	 * Performs a Left-Join on two ResultSets.
	 * 
	 * @MainLeftJoinMethod
	 * @param left:             left ResultSet of the join
	 * @param right:            right ResultSet of the join
	 * @param joinFieldName:    FieldName which should be used to join
	 * @param targetFieldNames: FieldNames of the right ResultSet which should be
	 *                          joined, if null all fields are joined
	 * @param writeEmptyFields: if not null, empty fields which didn't find a match
	 *                          to join are written with this String if null, these
	 *                          fields are not written
	 * 
	 * @return The left-joined ResultSet as an result of the two input ResultSets
	 * @throws ParseException
	 */
	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName,
			List<String> targetFieldNames, String writeEmptyFields)
			throws ParseException {
		
		ResultSet result = left.clone();

		// If the targeFieldNames are not specified, all fields will be joined
		if (targetFieldNames == null || targetFieldNames.isEmpty()) {
			targetFieldNames = right.getColumnNames();
			targetFieldNames.remove(joinFieldName);
		}

		// Building an HashMap out of the right ResultSet and the targetNames
		Map<Object, List<Object>> targetData = new HashMap<>();
		List<Object> leftData = new ArrayList<>();
		// The leftData list is used to build a dynamic HasMap which only contains the
		// data of the right ResultSet which is actually needed
		for (DataRow dr : left) {
			leftData.add(dr.getFieldValue(joinFieldName));
		}
		for (DataRow dr : right) {
			if (leftData.contains(dr.getFieldValue(joinFieldName))) {
			targetData.put(dr.getFieldValue(joinFieldName),
					getObjectValuesInOrderFromTargetFields(dr, targetFieldNames));
			}
		}

		// Joining the left ResultSet with the data from the right ResultSet
		performJoin(result, targetData, joinFieldName, writeEmptyFields, targetFieldNames);

		return result;
	}

	private static void performJoin(ResultSet result, Map<Object, List<Object>> targetData,
			String joinFieldName, String writeEmptyFields,
			List<String> targetFieldNames) throws ParseException {

		List<Object> currentJoinItems;

		// Joining the left ResultSet with the data from the right ResultSet
		for (DataRow dr : result) {
			currentJoinItems = targetData.get(dr.getFieldValue(joinFieldName));
			if (currentJoinItems == null) {
				if (writeEmptyFields == null) {
					continue;
				} else {
					for (int i = 0; i < targetFieldNames.size(); i++) {
						currentJoinItems = new ArrayList<>();
						currentJoinItems.add(writeEmptyFields);
					}
				}
			}
			for (int i = 0; i < currentJoinItems.size(); i++) {
				dr.setFieldValue(targetFieldNames.get(i), currentJoinItems.get(i));
			}
		}

	}

	private static List<Object> getObjectValuesInOrderFromTargetFields(DataRow dr, List<String> targetFieldNames) {

		List<Object> targetObjects = new ArrayList<>();
		for (int i = 0; i < targetFieldNames.size(); i++) {
			targetObjects.add(dr.getFieldValue(targetFieldNames.get(i)));
		}

		return targetObjects;

	}

}
