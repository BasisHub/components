package com.basiscomponents.db;

import java.security.InvalidAlgorithmParameterException;

public class DataRowQueryMatcher {
	
	
	private static String resolveTerm(String term,DataRow r) throws Exception{
		if (term.trim().isEmpty())
			return"";
		
		switch (term.trim().toUpperCase()){
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
		}
		
		String[] exp_parts = term.split("=");
		if (exp_parts.length != 2)
			throw new InvalidAlgorithmParameterException("invalid expression: "+term); 
		
		DataField f = r.getField(exp_parts[0]);

//		System.out.println("term: "+term+" resolved to "+b);
		if (f.equals(exp_parts[1]))
			return "1";
		else 
			return "0";
	}	
	
	
	/**
	 * 
	 * @param stmt: the statement, syntax as in a WHERE clause of an SQL statement
	 * @param dr: the DataRow to examine against the query clause 
	 * @return Boolean if the datarow matches the statement
	 * @throws Exception
	 */
	public static Boolean matches(String stmt, DataRow dr) throws Exception {

		
		stmt = stmt.replaceAll("\\s"," ");
		while (stmt.contains("  "))
			stmt = stmt.replace("  ", " ");
//		System.out.println(stmt);
		stmt += " ";
				
		int i=0;
		Boolean status = false; //false=term true=punctuation
		
		String bexp = "";
		
		String curterm="";
		while (i<stmt.length()){
			char c = stmt.charAt(i);
			switch (c) {
				case ' ':

						bexp += resolveTerm(curterm,dr);
					
					status = !status;
					curterm="";
					break;
				case '(':
				case ')':
				case '!':
						bexp += resolveTerm(curterm,dr);
					
					status = !status;
					curterm="";
					String tmp = new String();
					tmp+=c;
					bexp += resolveTerm(tmp,dr);
					
					status=false;
				break;
				default:
					curterm += c;
					break;
			}
			i++;
		
		}
		
//		System.out.println(bexp);
		return resolveBooleanExp(bexp);

		
		
	}

	private static Boolean resolveBooleanExp(String bexp) {

//		System.out.println("resolve: "+bexp);
		while (bexp.contains("(")){
			int first_par = bexp.indexOf('(');
			int last_par =first_par;
			int more_opens=0;
			//find matching parenthesis
			while (true){
				last_par++;
				if (bexp.charAt(last_par) == '(')
				{
					more_opens++;
					continue;
				}
				if (bexp.charAt(last_par) == ')' && more_opens>0)
				{
					more_opens--;
					continue;
				}
				if (bexp.charAt(last_par) == ')' && more_opens<=0)
					break;
			}

			String exp = bexp.substring(first_par+1,last_par);
			
			Boolean b = resolveBooleanExp(exp);
			if (b)
				bexp = bexp.substring(0,first_par)+'1'+bexp.substring(last_par+1);
			else
				bexp = bexp.substring(0,first_par)+'0'+bexp.substring(last_par+1);
		}
		
		
		bexp=bexp.replaceAll("!1", "0");
		bexp=bexp.replaceAll("!0", "1");
		
		String[] or_terms = bexp.split("\\|");
		for (int i=0; i<or_terms.length; i++){
			if (or_terms[i].contains("&")){
				Boolean tmp_res=true;
				String[] and_terms = bexp.split("\\&");
				for (int j=0; j<and_terms.length; j++){
					if (and_terms[j].equals("0")){
						tmp_res=false;
					 	break;
					}
				}
				
				if (tmp_res){
//					System.out.println(bexp+" is true");
					return true;
				}
				else {
//					System.out.println(bexp+" is false");

					continue;
				}
						
			}
			if (or_terms[i].equals("1")){
//				System.out.println(or_terms[i]+" is true");
				return true;
			}
//			else
//				System.out.println(or_terms[i]+" is false");
		}

		
		return false;	
	}
	

}
