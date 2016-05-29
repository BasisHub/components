package com.basiscomponents.bridge.proxygen;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;


public class ParserJava {
	
	private static boolean isCandidate(Method m){

		//omit all methods inherited from java.lang.Object.
		if (m.toString().contains("java.lang.Object."))
			return false;			
		
		ArrayList<Class> valid = new ArrayList();
		valid.add(java.lang.String.class);
		valid.add(java.lang.Boolean.class);
		valid.add(void.class);
		valid.add(com.basiscomponents.db.DataRow.class);
		valid.add(com.basiscomponents.db.ResultSet.class);
		
		if (!valid.contains(m.getReturnType()))
			return false;
		
		Parameter[] p = m.getParameters();
		for (int i=0; i<p.length; i++){
			if (!valid.contains(p[i].getType()))
				return false;			
		}
		
		
		return true;
	}
	
	
	public static ParseEntity parseJavaClass(Class c){
		
		ArrayList<com.basiscomponents.bridge.proxygen.Method> methods = new ArrayList<>();	
		
		java.lang.reflect.Method[] m = c.getMethods();
		for (int i=0; i<m.length; i++)
		{
			Method myMethod = m[i];
			if (isCandidate(myMethod))
			{
				ArrayList<MethodParameter> params = new ArrayList<>();
				
				com.basiscomponents.bridge.proxygen.Method methoddef = new com.basiscomponents.bridge.proxygen.Method();
				
				methoddef.setName(myMethod.getName());
				methoddef.setReturnType(getProxyVariableType(myMethod.getReturnType()));

				Parameter[] p = myMethod.getParameters();
				for (int j=0; j<p.length; j++){
					params.add(new MethodParameter(getProxyVariableType(p[j].getType()), p[j].getName()));
				}				
				
				methoddef.setParams(params);
				
				methods.add(methoddef);
			}
		}
		return new ParseEntity(c.getName(),"",methods);		
	}
	
	private static String getProxyVariableType(Class type) {
		String t;
		switch (type.getName()){
		case "java.lang.String":
			return "String";
		case "java.lang.Boolean":
			return "Boolean";
		case "com.basiscomponents.db.DataRow":
			return "DataRow";
		case "com.basiscomponents.db.ResultSet":
			return "ResultSet";
		}
		return type.getName();
		
	}
}
