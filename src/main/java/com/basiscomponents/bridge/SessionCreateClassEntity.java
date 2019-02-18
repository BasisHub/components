package com.basiscomponents.bridge;

class SessionCreateClassEntity extends SessionExecuteEntity {
	private String var;
	private String classname;

	@SuppressWarnings("unused")
	private SessionCreateClassEntity() {
	}

	public SessionCreateClassEntity(String var, String classname) {

		this.var = var;
		this.classname = classname;

	}

	@Override
	public String toJson() {
		return "{\"op\":\"create\",\"var\":\"" + var + "\",\"class\":\""
				+ classname + "\"}";
	}

}
