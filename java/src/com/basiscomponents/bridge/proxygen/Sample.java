package com.basiscomponents.bridge.proxygen;

import java.io.IOException;
import java.util.HashMap;

public class Sample {

	public static void main(String[] args) throws IOException {
		
		String filename="MyClass.bbj";
		String content = FileHandler.readFile("D:/JsOverloading/"+filename);
		ParseEntity pe = Parser.parse(content);
		
		HashMap<String,ParseEntity> classes = new HashMap<>();
		classes.put("MyClass",pe);
		
		GeneratorJavaScript.createJavaScriptProxyClasses(classes, "D:/JsOverloading/", "api/", ".bbj");

	}

}
