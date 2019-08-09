package com.basiscomponents.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultSetJoiner {

	/**
	 * @See full parameter leftJoin method
	 */
	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName) throws ParseException {
		Map<ResultSet, List<String>> temp = new HashMap<>();
		temp.put(right, null);
		return ResultSetJoiner.leftJoin(left, temp, joinFieldName);
	}

	/**
	 * @See full parameter leftJoin method
	 */
	public static ResultSet leftJoin(ResultSet left, List<ResultSet> rights, String joinFieldName)
			throws ParseException {
		Map<ResultSet, List<String>> temp = new HashMap<>();
		for (ResultSet rs: rights) {
			temp.put(rs, null);
		}
		return ResultSetJoiner.leftJoin(left, temp, joinFieldName);
	}

	/**
	 * Performs a Left-Join on two ResultSet.
	 * 
	 * @param left:          The left part of the join
	 * @param rights:        The right side of the join represented as a Map, which
	 *                       maps the right-sided ResultSets to the fieldNames which
	 *                       should be joined
	 * @param joinFieldName: The FieldName which should be used to join
	 * 
	 * @return The left-joined ResultSet as an Result of the two input ResultSets
	 * @throws ParseException
	 */
	public static ResultSet leftJoin(final ResultSet left, final Map<ResultSet, List<String>> rights,
			final String joinFieldName)
			throws ParseException {
		
		ResultSet result = left.clone();
		List<String> targetFieldNames;
		List<Object> currentJoinItems;

		// Iterating over the right sided ResultSets of the join
		for (ResultSet currentRight : rights.keySet()) {

			targetFieldNames = rights.get(currentRight);

			// If the targeFieldNames are not specified, all fields will be joined
			if (targetFieldNames == null || targetFieldNames.isEmpty()) {
				targetFieldNames = currentRight.get(0).getFieldNames();
				targetFieldNames.remove(joinFieldName);
			}

			// Building an HashMap out of the right ResultSet and the targetNames
			Map<Object, List<Object>> targetData = new HashMap<>();
			for (DataRow dr : currentRight) {
				targetData.put(dr.getFieldValue(joinFieldName),
						getObjectValuesInOrderFromTargetFields(dr, targetFieldNames));
			}

			// Joining the left ResultSet with the data from the right ResultSet
			for (DataRow dr : result) {
				currentJoinItems = targetData.get(dr.getFieldValue(joinFieldName));
				if (currentJoinItems == null) {
					continue;
				}
				for (int i = 0; i < currentJoinItems.size(); i++) {
					dr.setFieldValue(targetFieldNames.get(i), currentJoinItems.get(i));
				}
			}
		
		} // Iterating over the right sided ResultSets of the join

		return result;
	}

	private static List<Object> getObjectValuesInOrderFromTargetFields(DataRow dr, List<String> targetFieldNames) {

		List<Object> targetObjects = new ArrayList<>();
		for (int i = 0; i < targetFieldNames.size(); i++) {
			targetObjects.add(dr.getFieldValue(targetFieldNames.get(i)));
		}

		return targetObjects;

	}
}
