package com.basiscomponents.db.resultSetJoinerConfiguration;

import java.util.ArrayList;
import java.util.List;

import com.basiscomponents.db.DataRow;

public class JoinConfiguration {

	@FunctionalInterface
	public interface JoinFunction<DataRow, List, ArrayList, Void> {
		public void apply(DataRow dataRow, List listJoinItems, List listTagetNames);
	}

	private String joinFieldName;
	private JoinFunction<DataRow, List, ArrayList, Void> joinFunc;

	public JoinConfiguration(String joinFieldName, JoinFunction<DataRow, List, ArrayList, Void> joinFunc) {
		this.joinFieldName = joinFieldName;
		this.joinFunc = joinFunc;
	}

	public void applyJoinFunction(DataRow dr, List<Object> list, List<String> names) {
		this.joinFunc.apply(dr, list, names);
	}

	public String getJoinFieldName() {
		return this.joinFieldName;
	}

	public JoinFunction<DataRow, List, ArrayList, Void> joinFunc() {
		return this.joinFunc;
	}

	public void setJoinFieldName(String joinFieldName) {
		this.joinFieldName = joinFieldName;
	}

	public void setJoinFunction(JoinFunction<DataRow, List, ArrayList, Void> joinFunc) {
		this.joinFunc = joinFunc;
	}

}
