package com.basiscomponents.bridge.proxygen;

public class MethodParameter {

	private String type, name;

	public MethodParameter(String type, String name) {
		this.setType(type);
		this.setName(name);
	}

	public String getType() {
		return type;
	}

	public final void setType(String type) {
		if (type.trim().equals("BBjVector"))
			type = "ResultSet";
		if (type.trim().equals("BBjString"))
			type = "String";
		if (type.trim().equals("BBjNumber"))
			type = "Number";
		if (type.trim().equals("Boolean"))
			type = "Boolean";		

		this.type = type;
	}

	public String getName() {
		return name;
	}

	public final void setName(String name) {
		if (name.endsWith("!"))
			name = name.substring(0, name.length() - 1);
		if (name.endsWith("$"))
			name = name.substring(0, name.length() - 1);
		if (name.endsWith("%"))
			name = name.substring(0, name.length() - 1);
		this.name = name;
	}
}
