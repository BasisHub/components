package com.basiscomponents.bridge;

import com.basiscomponents.db.DataRow;

class SessionCreateClassEntity extends SessionExecuteEntity{
	private String var,classname;
	
	private SessionCreateClassEntity(){};
	
	public SessionCreateClassEntity(String var, String classname){

		this.var=var;
		this.classname=classname;
		
	}
	
	@Override
	public String toJson() {
		String json = "{\"op\":\"create\",\"var\":\""+var+"\",\"class\":\""+classname+"\"}";
		return json;
	}
	
}
