package com.basiscomponents.bridge;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

class SessionVarEntity {
	private String name,type;
	
	private Object val;
	
	private SessionVarEntity(){};
	
	public SessionVarEntity(String name, String val){
		this.name=name;
		this.type="str";
		this.val=val;
	}

	public SessionVarEntity(String name, Number val){
		this.name=name;
		this.type="num";
		this.val=val;

	}

	public SessionVarEntity(String name, DataRow val){
		this.name=name;
		this.type="dr";
		this.val=val;
	}

	public SessionVarEntity(String name, ResultSet val) {
		this.name=name;
		this.type="rs";
		this.val=val;
	}

	public String toJson() {
		String json = "{\"n\":\""+name+"\",\"t\":\""+type+"\",\"v\":";
		switch (type)
		{
			case "str":
				json +="\""+val+"\"";
				break;
			case "num":
				json +=val.toString();
				break;
			case "dr":
				json += ((DataRow)val).toJsonElement();
				break;	
			case "rs":
				json += ((ResultSet)val).toJsonElement();
				break;	
		}
				json +="}";
		return json;
	}

	
}
