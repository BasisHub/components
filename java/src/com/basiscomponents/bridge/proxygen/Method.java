package com.basiscomponents.bridge.proxygen;

import java.util.ArrayList;
import java.util.Iterator;

public class Method {
	private String name,returntype;
	private Boolean isStatic=false, isConstructor=false;
	private ArrayList<MethodParameter> params;
	public String getReturnType() {
		return returntype;
	}
	public void setReturnType(String retval) {
		this.returntype = retval;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<MethodParameter> getParams() {
		return params;
	}
	public void setParams(ArrayList<MethodParameter> params) {
		this.params = params;
	}
	public void addParam(MethodParameter param){
		this.params.add(param);
	}

	public Boolean getIsStatic() {
		return isStatic;
	}
	public void setIsStatic(Boolean isStatic) {
		this.isStatic = isStatic;
	}
	public Boolean getIsConstructor() {
		return isConstructor;
	}
	public void setIsConstructor(Boolean isConstructor) {
		this.isConstructor = isConstructor;
	}
	
	public String toString(){
		String ret=(isStatic ? "static ":"");

		if (isConstructor){
			ret +="<constructor>";
		}
		else{
			ret+=this.returntype;
			ret +=" ";
			ret+=this.name;
		}
		ret +="(";
			Iterator<MethodParameter> it = this.params.iterator();
			boolean second=false;
			while (it.hasNext()){
				MethodParameter param = it.next();
				if (second)
					ret +=",";
				else
					second=true;
				
				ret += param.getType();
				ret += " ";
				ret += param.getName();
			}
		ret +=")";
		return ret;
	}

}
