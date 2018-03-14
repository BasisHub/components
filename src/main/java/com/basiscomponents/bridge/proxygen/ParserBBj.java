package com.basiscomponents.bridge.proxygen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

public class ParserBBj {

	public static ParseEntity parse(String content) {

		ArrayList<Method> methods = new ArrayList<>();
		HashMap<String,Integer> methodlist = new HashMap<>();
		
		String baseclass = null, classname = null;

		Scanner scanner = new Scanner(content);

		String line;
		String nextLine = null;
		
		// read file line by line
		scanner.useDelimiter(System.getProperty("line.separator"));
		while (scanner.hasNext()) {
			
			if(nextLine != null){
				line = nextLine;
				nextLine = null;
			}else{
				line = scanner.next();
			}

			String lline = line.toLowerCase();
			line = formatLine(line);
			lline = formatLine(lline);

			if (lline.contains("method public") && !(lline.startsWith("rem"))) {
				if(scanner.hasNext()){
					StringBuilder buffer = new StringBuilder();
					while(scanner.hasNext() && (nextLine = scanner.next()).startsWith(":")){
						buffer.append(nextLine);
					}

					if(!buffer.toString().isEmpty()){
						line = formatLine(line + buffer.toString());
						lline = formatLine(lline + buffer.toString().toLowerCase());
					}
				}

				Method m = parseMethodSignature(line.trim());
				if (m != null) {
					methods.add(m);
					if (methodlist.containsKey(m.getName())){
						methodlist.put(m.getName(), 	
										(methodlist.get(m.getName()))+1);
					}
					else
					{
						methodlist.put(m.getName(), 1);
					}
				}
			}


			if (lline.contains("class public") && !lline.startsWith("rem")) {
				//FIXME this is bogus - what if no extends but only implements?
				line = line.substring(lline.indexOf("public") + 7);
				lline = lline.substring(lline.indexOf("public") + 7);
				if (lline.contains("extends")) {
					String lineend = line.substring(lline.indexOf("extends") + 7);
					String llineend = lline.substring(lline.indexOf("extends") + 7);
					if (llineend.contains("implements")) {
						lineend = lineend.substring(0, llineend.indexOf("implements"));
						llineend = llineend.substring(0, llineend.indexOf("implements"));
						baseclass = lineend.trim();
					}

					if (lline.contains("extends")) {
						line = line.substring(0, lline.indexOf("extends"));
					}
				}

				if (lline.contains("implements")) {
					line = line.substring(0, lline.indexOf("implements"));
				}
				classname = line.trim();
			}

		}
		scanner.close();


		//check for overloaded methods
		Iterator<Method> it = methods.iterator();

		while (it.hasNext()){
			Method m = it.next();
			if (methodlist.get(m.getName())>1){
				m.setIsOverloaded(true);
			}
		}

		ParseEntity et = new ParseEntity(classname, baseclass, methods);

		return et;
	}

	private static Method parseMethodSignature(String line) {
		line = line.substring(13).trim();

		Method m = new Method();
		if (line.startsWith("static")) {
			m.setIsStatic(true);
			line = line.substring(7);
		}

		ArrayList<MethodParameter> params = new ArrayList<MethodParameter>();
		String args = line.substring(line.indexOf('(') + 1);
		line = line.substring(0, line.indexOf('('));

		args = args.substring(0, args.indexOf(')'));
		String[] arglist = args.split(",");

		for (String arg : arglist) {
			arg = arg.trim();
			if (arg.isEmpty())
				continue;
			String[] argsplit = arg.split(" ");
			MethodParameter mp = new MethodParameter(argsplit[0].trim(),
					argsplit[1].trim());
			params.add(mp);
		}
		m.setParams(params);

		String[] splitline = line.split(" ");
		if (splitline.length == 1) {
			// this is a constructor
			m.setIsConstructor(true);
			m.setName(splitline[0].trim());
		}
		if (splitline.length == 2) {
			String returntype = splitline[0].trim();
			if (returntype.equals("BBjVector"))
				returntype = "ResultSet";
			if (returntype.equals("BBjString"))
				returntype = "String";
			if (returntype.equals("BBjNumber"))
				returntype = "Number";

			m.setReturnType(returntype);

			m.setName(splitline[1].trim());
		}

		return m;
	}

	private static String formatLine(String line) {

		while (line.contains("\t"))
			line = line.replace('\t', ' ');

		line = line.trim().replaceAll("( )+", " ");

		return line;

	}

}
