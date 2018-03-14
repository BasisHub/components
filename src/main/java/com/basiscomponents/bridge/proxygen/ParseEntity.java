package com.basiscomponents.bridge.proxygen;

import java.util.ArrayList;
import java.util.Iterator;

public class ParseEntity {

	private String classname, baseclass;
	private ArrayList<Method> methods;

	public ParseEntity(String classname, String baseclass,
			ArrayList<Method> methods) {
		this.classname = classname;
		this.baseclass = baseclass;
		this.methods = methods;
	}

	public String getBaseclass() {
		return baseclass;
	}

	public void setBaseclass(String baseclass) {
		this.baseclass = baseclass;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public ArrayList<Method> getMethods() {
		return methods;
	}

	public void setMethods(ArrayList<Method> methods) {
		this.methods = methods;
	}

	public String toString() {
		String ret = "Class:    " + this.classname;
		ret += "\n";
		if (baseclass != null) {
			ret += "Extends:  " + this.baseclass;
			ret += "\n";
		}
		Iterator<Method> it = methods.iterator();
		while (it.hasNext()) {
			Method m = it.next();
			ret += "Method:   ";
			ret += m.toString();
			ret += "\n";
		}
		return ret;
	}

}
