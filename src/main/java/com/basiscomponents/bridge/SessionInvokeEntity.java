package com.basiscomponents.bridge;

class SessionInvokeEntity extends SessionExecuteEntity {
	private String var, retvar, method;
	private String[] args;

	@SuppressWarnings("unused")
	private SessionInvokeEntity() {
	}

	public SessionInvokeEntity(String var, String retvar, String method,
			String... args) {

		this.var = var;
		this.retvar = retvar;
		this.method = method;
		this.args = args;
	}

	@Override
	public String toJson() {

		String json = "{\"op\":\"invoke\",\"var\":\"" + var
				+ "\",\"retvar\":\"" + retvar + "\",\"method\":\"" + method
				+ "\"";

		if (args.length > 0) {
			json += ",\"args\":[";
			Boolean first = true;
			for (int i = 0; i < args.length; i++) {
				if (!first)
					json += ',';
				else
					first = false;
				json += "\"" + args[i] + "\"";
			}
			json += "]";
		}
		json += "}";

		return json;
	}

}
