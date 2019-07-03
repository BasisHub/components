package com.basiscomponents.db;

import java.security.InvalidAlgorithmParameterException;

public class DataRowQueryMatcher {


	private static String resolveTerm(String term, final DataRow dataRow, final boolean caseSensitive,
	                                  final boolean trimmed) throws InvalidAlgorithmParameterException {
		if (term.trim().isEmpty())
			return "";

		switch (term.trim().toUpperCase()) {
			case "AND":
				return "&";

			case "OR":
				return "|";

			case "NOT":
				return "!";

			case "(":
				return "(";

			case ")":
				return ")";
			default:
				break;
		}

		String[] exp_parts = term.split("=");
		if (exp_parts.length != 2)
			throw new InvalidAlgorithmParameterException("invalid expression: " + term);
		DataField dataField = dataRow.getField(exp_parts[0]);
		if (dataField.equals(exp_parts[1], caseSensitive, trimmed))
			return "1";
		else
			return "0";
	}

	public static Boolean matches(String statement, DataRow datarow) throws Exception {
		return matches(statement, datarow, true, false);
	}

	/**
	 * @param stmt: the statement, syntax as in a WHERE clause of an SQL statement
	 * @param dr:   the DataRow to examine against the query clause
	 * @return Boolean if the datarow matches the statement
	 * @throws Exception
	 */
	public static Boolean matches(String statement, DataRow datarow, final boolean caseSensitive, final boolean trimmed) throws InvalidAlgorithmParameterException {

		statement = statement.replaceAll("\\s", " ");
		while (statement.contains("  "))
			statement = statement.replace("  ", " ");
		statement += " ";

		Boolean status = false; //false=term true=punctuation

		StringBuilder booleanExpression = new StringBuilder();

		StringBuilder curterm = new StringBuilder();
		for (final char c : statement.toCharArray()) {
			switch (c) {
				case ' ':
					booleanExpression.append(resolveTerm(curterm.toString(), datarow, caseSensitive, trimmed));
					status = !status;
					curterm = new StringBuilder(); //reset curterm
					break;
				case '(':
				case ')':
				case '!':
					booleanExpression.append(resolveTerm(curterm.toString(), datarow, caseSensitive, trimmed));
					curterm = new StringBuilder(); //reset curterm
					String tmp = "" + c;
					booleanExpression.append(resolveTerm(tmp, datarow, caseSensitive, trimmed));

					status = false;
					break;
				default:
					curterm.append(c);
					break;
			}
		}
		return resolveBooleanExp(booleanExpression.toString());
	}

	private static Boolean resolveBooleanExp(String bexp) {

		while (bexp.contains("(")) {
			int first_par = bexp.indexOf('(');
			int last_par = first_par;
			int more_opens = 0;
			//find matching parenthesis
			while (true) {
				last_par++;
				if (bexp.charAt(last_par) == '(') {
					more_opens++;
					continue;
				}
				if (bexp.charAt(last_par) == ')' && more_opens > 0) {
					more_opens--;
					continue;
				}
				if (bexp.charAt(last_par) == ')' && more_opens <= 0)
					break;
			}

			String exp = bexp.substring(first_par + 1, last_par);


			if (resolveBooleanExp(exp))
				bexp = bexp.substring(0, first_par) + '1' + bexp.substring(last_par + 1);
			else
				bexp = bexp.substring(0, first_par) + '0' + bexp.substring(last_par + 1);
		}


		bexp = bexp.replaceAll("!1", "0");
		bexp = bexp.replaceAll("!0", "1");

		String[] or_terms = bexp.split("\\|");
		for (int i = 0; i < or_terms.length; i++) {
			if (or_terms[i].contains("&")) {
				Boolean tmp_res = true;
				String[] and_terms = bexp.split("\\&");
				for (int j = 0; j < and_terms.length; j++) {
					if (and_terms[j].equals("0")) {
						tmp_res = false;
						break;
					}
				}

				if (tmp_res) {
					return true;
				} else {
					continue;
				}

			}
			if (or_terms[i].equals("1")) {
				return true;
			}
		}


		return false;
	}


}
