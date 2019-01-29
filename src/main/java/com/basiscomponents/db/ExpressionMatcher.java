package com.basiscomponents.db;

import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ExpressionMatcher {

	private String function;
	private String operand;
	private boolean orCombined;
	private ArrayList<ExpressionMatcher> matcherList = new ArrayList<>();
	private DataRow drCompare;


	public ExpressionMatcher(String condition, int type, String fieldName) throws Exception {
		String[] orConditions = condition.split("(?<!\\\\)\\|");
		if (orConditions.length > 1) {
			orCombined = true;
			for (String cond : orConditions) {
				ExpressionMatcher matcher = new ExpressionMatcher(cond, type, fieldName);
				matcherList.add(matcher);
			}
		}
		else {
			orCombined = false;
			String[] andConditions = condition.split("(?<!\\\\)\\&");
			if (andConditions.length > 1) {
				for (String cond : andConditions) {
					ExpressionMatcher matcher = new ExpressionMatcher(cond, type, fieldName);
					matcherList.add(matcher);
				}
			}
			else {
				Pattern p = Pattern.compile("^(!|<=|>=|=<|=>|<|>){0,1}(.*)");
				Matcher m = p.matcher(condition);
				if (m.matches()) {
					function = m.group(1);
					operand = m.group(2);
					drCompare = new DataRow();
					drCompare.setFieldValue(fieldName, type, operand);
					if (function == null) function = "=";
				}
			}
		}
	}


	public boolean match(String value) {
		if (value == null) return false;

		if (matcherList.size() > 0) {
			ExpressionMatcher matcher = matcherList.get(0);
			boolean ret = matcher.match(value);

			for (int i=1; i<matcherList.size(); i++) {
				if (orCombined && ret || !orCombined && !ret) return ret;
				matcher = matcherList.get(i);
				ret = orCombined? ret || matcher.match(value) : ret && matcher.match(value);
			}
			return ret;
		}
		else {
			switch (function) {
			case "!":
				return !value.equals(operand);
			case "<":
				return value.compareTo(operand) < 0;
			case ">":
				return value.compareTo(operand) > 0;
			case "<=":
			case "=<":
				return value.compareTo(operand) <= 0;
			case ">=":
			case "=>":
				return value.compareTo(operand) >= 0;
			default:
				return value.equals(operand);
			}
		}
	}


	public boolean match(Comparator<DataRow> comp, DataRow dr, String fieldName) throws Exception {
		if (matcherList.size() > 0) {
			ExpressionMatcher matcher = matcherList.get(0);
			boolean ret = matcher.match(comp, dr, fieldName);

			for (int i=1; i<matcherList.size(); i++) {
				if (orCombined && ret || !orCombined && !ret) return ret;
				matcher = matcherList.get(i);
				ret = orCombined? ret || matcher.match(comp, dr, fieldName) : ret && matcher.match(comp, dr, fieldName);
			}
			return ret;
		}
		else {
			switch (function) {
			case "!":
				return comp.compare(dr, drCompare) != 0;
			case "<":
				return comp.compare(dr, drCompare) < 0;
			case ">":
				return comp.compare(dr, drCompare) > 0;
			case "<=":
			case "=<":
				return comp.compare(dr, drCompare) <= 0;
			case ">=":
			case "=>":
				return comp.compare(dr, drCompare) >= 0;
			default:
				return comp.compare(dr, drCompare) == 0;
			}
		}
	}


	public static String generateJavaScriptExpression(String expression, int type) throws ParseException {
		String[] expressions = expression.split("(?<!\\\\)[\\&\\|]");
		ArrayList<String> operators = new ArrayList<String>();
		Pattern p = Pattern.compile("(?<!\\\\)[\\&\\|]");
		Matcher m = p.matcher(expression);
		while (m.find()) {
			if (m.group().equals("&"))
				operators.add("&&");
			else
				operators.add("||");
		}

		String function = "var func1 = function(value";
		String ret = "";
		for (int i=0; i<expressions.length; i++) {
			p = Pattern.compile("^(!|<=|>=|=<|=>|<|>){0,1}(.*)");
			m = p.matcher(expressions[i]);
			while (m.find()) {
				String operation = m.group(1);
				String value = m.group(2).replaceAll("\\\\\\&", "&").replaceAll("\\\\\\|", "|");

				if (operation == null)
					operation = "==";
				else if (operation.equals("!"))
					operation = "!=";

//				function += ",v"+(i+1);
//				ret += "value " + operation + " v" + (i+1);

				switch (type) {
				case Types.BIT:
				case Types.INTEGER:
				case Types.BIGINT:
				case Types.DECIMAL:
				case Types.DOUBLE:
				case Types.FLOAT:
				case Types.NUMERIC:
				case Types.REAL:
				case Types.SMALLINT:
				case Types.TINYINT:
					ret += "value " + operation + " " + value;
					break;
				case Types.DATE:
				case Types.TIMESTAMP:
					ret += "value " + operation + " new Date(\"" + value + "\")";
					break;
				default:
					ret += "value " + operation + " \"" + value + "\"";
					break;
				}

				if (i < expressions.length - 1) ret += " " + operators.get(i) + " ";
			}
		}
		function += ") {if (value==null) return false; return " + ret + ";};";

		return function;
	}


	public static String generatePreparedWhereClause(String fieldName, final String condition) {
		String cond = condition.replace("<>", "!");
		String ret = "";
		String[] conditions = cond.split("(?<!\\\\)[\\|&]");
		int i = 0;
		Pattern p = Pattern.compile("(?<!\\\\)([\\|&])");
		Matcher m = p.matcher(condition);
		Pattern p1 = Pattern.compile("^(!|<=|>=|=<|=>|<|>){0,1}(.*)");
		while (m.find()) {
			Matcher m1 = p1.matcher(conditions[i]);
			if (m1.find()) {
				String oper = m1.group(1);
				if (oper == null) oper = "=";
				ret += fieldName + " " + oper + " ?" + (m.group(1).equals("&")? " AND " : " OR ");
			}
			i++;
		}
		Matcher m1 = p1.matcher(conditions[conditions.length-1]);
		if (m1.find()) {
			String oper = m1.group(1);
			if (oper == null) oper = "=";
			if (oper.equals("!"))
				oper = "!=";
			ret += fieldName + " " + oper + " ?";
		}
		return ret;
	}


	public static DataRow getPreparedWhereClauseValues(String condition, int fieldType) throws Exception {
		DataRow dr = new DataRow();
		String[] conditions = condition.split("(?<!\\\\)[\\|&]");
		Pattern p = Pattern.compile("^(!|<=|>=|=<|=>|<|>){0,1}(.*)");
		int i = 1;
		for (String cond : conditions) {
			Matcher m = p.matcher(cond);
			if (m.find()) {
				dr.setFieldValue("VALUE"+i, fieldType, m.group(2).replaceAll("\\\\([\\|&])", "$1"));
				i++;
			}
		}
		return dr;
	}
}
