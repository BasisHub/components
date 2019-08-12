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
	public static ResultSet leftJoin(ResultSet left, ResultSet right, String joinFieldName, String writeEmptyFields)
			throws ParseException {
		Map<ResultSet, List<String>> temp = new HashMap<>();
		temp.put(right, null);
		return ResultSetJoiner.leftJoin(left, temp, joinFieldName, writeEmptyFields);
	}

	/**
	 * @See @MainLeftJoinMethod
	 */
	public static ResultSet leftJoin(ResultSet left, List<ResultSet> rights, String joinFieldName,
			String writeEmptyFields)
			throws ParseException {
		Map<ResultSet, List<String>> temp = new HashMap<>();
		for (ResultSet rs: rights) {
			temp.put(rs, null);
		}
		return ResultSetJoiner.leftJoin(left, temp, joinFieldName, writeEmptyFields);
	}

	/**
	 * Performs a Left-Join on two or more ResultSets.
	 * 
	 * @MainLeftJoinMethod
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
			final String joinFieldName, final String writeEmptyFields)
			throws ParseException {
		
		ResultSet result = left.clone();
		List<String> targetFieldNames;

		// Iterating over the right sided ResultSets of the join
		for (ResultSet currentRight : rights.keySet()) {

			targetFieldNames = rights.get(currentRight);

			// If the targeFieldNames are not specified, all fields will be joined
			if (targetFieldNames == null || targetFieldNames.isEmpty()) {
				targetFieldNames = currentRight.getColumnNames();
				targetFieldNames.remove(joinFieldName);
			}

			// Building an HashMap out of the right ResultSet and the targetNames
			Map<Object, List<Object>> targetData = new HashMap<>();
			for (DataRow dr : currentRight) {
				targetData.put(dr.getFieldValue(joinFieldName),
						getObjectValuesInOrderFromTargetFields(dr, targetFieldNames));
			}

			// Joining the left ResultSet with the data from the right ResultSet
			performJoin(result, targetData, joinFieldName, writeEmptyFields, targetFieldNames);

		}

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

//	/**
//	 * Performs a left join. @See @MainLeftJoinMethod This method only differs from
//	 * the main one, considering the use of the JoinConfiguration here.
//	 * 
//	 * 
//	 * @param left:    The left part of the join
//	 * @param rights:  The right side of the join represented as a Map, which maps
//	 *                 the right-sided ResultSets to the fieldNames which should be
//	 *                 joined
//	 * @param jConfig: A ConfigurationClass with has to be filled with the main
//	 *                 joinField and a function which decides how to perform the
//	 *                 join in detail.
//	 * @return
//	 * @throws ParseException
//	 */
//	public static ResultSet leftJoin(final ResultSet left, final Map<ResultSet, List<String>> rights,
//			final JoinConfiguration jConfig) throws ParseException {
//
//		ResultSet result = left.clone();
//		List<String> targetFieldNames;
//		List<Object> currentJoinItems;
//		String joinFieldName = jConfig.getJoinFieldName();
//
//		// Iterating over the right sided ResultSets of the join
//		for (ResultSet currentRight : rights.keySet()) {
//
//			targetFieldNames = rights.get(currentRight);
//
//			// If the targeFieldNames are not specified, all fields will be joined
//			if (targetFieldNames == null || targetFieldNames.isEmpty()) {
//				targetFieldNames = currentRight.get(0).getFieldNames();
//				targetFieldNames.remove(joinFieldName);
//			}
//
//			// Building an HashMap out of the right ResultSet and the targetNames
//			Map<Object, List<Object>> targetData = new HashMap<>();
//			for (DataRow dr : currentRight) {
//				targetData.put(dr.getFieldValue(joinFieldName),
//						getObjectValuesInOrderFromTargetFields(dr, targetFieldNames));
//			}
//
//			// Joining the left ResultSet with the data from the right ResultSet
//			for (DataRow dr : result) {
//				currentJoinItems = targetData.get(dr.getFieldValue(joinFieldName));
//				jConfig.applyJoinFunction(dr, currentJoinItems, targetFieldNames);
//			}
//
//		} // Iterating over the right sided ResultSets of the join
//
//		return result;
//	}

}
