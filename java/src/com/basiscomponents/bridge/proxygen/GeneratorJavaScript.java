package com.basiscomponents.bridge.proxygen;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class GeneratorJavaScript {

	private static final String AllowedTypes = "DataRow ResultSet BBjNumber BBjString void";

	public static void createJavaScriptProxyClasses(
			HashMap<String, ParseEntity> classes, 
			String outputfolder, String classfileprefix, String classfilesuffix)
			throws FileNotFoundException, UnsupportedEncodingException {

		Set<String> ks = classes.keySet();
		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String classname = it.next();
			System.out.println("------------------------\n" + classname);

			ParseEntity pe = classes.get(classname);
			String filename = outputfolder + classname + ".js";
			PrintWriter writer = new PrintWriter(filename, "UTF-8");

			writer.println("function RelationsBC(){"); 
			writer.println("	this.id = guid(); ");
			writer.println("	ses.create(this.id,\"::" + classfileprefix
					+ pe.getClassname() + classfilesuffix + "::"
					+ pe.getClassname() + "\"); ");
			writer.println("} ");

			

			// methods

			Iterator<Method> it1 = pe.getMethods().iterator();
			while (it1.hasNext()) {

				Method m = it1.next();
				if (m.getIsConstructor())
					continue;

				// first check if all concerned data types are valid
				Boolean OK = true;
				if (!AllowedTypes.contains(m.getReturnType()))
					OK = false;

				Iterator<MethodParameter> it2 = m.getParams().iterator();
				while (it2.hasNext()) {
					if (!AllowedTypes.contains(it2.next().getType())) {
						OK = false;
						break;
					}

				}

				if (!OK)
					continue;
				
	
				writer.print(classname+".prototype."+ m.getName()
						+ " = function("); 


				it2 = m.getParams().iterator();
				Boolean second = false;
				while (it2.hasNext()) {
					MethodParameter mp = it2.next();
					if (second)
						writer.print(",");
					else
						second = true;
					writer.print(mp.getName());
				}

				writer.println(") { ");

				// prepare parameters into session
				if(!m.getParams().isEmpty()){
					writer.println("	     var args = new Array();");
				}
					

					it2 = m.getParams().iterator();
					while (it2.hasNext()) {
						MethodParameter mp = it2.next();
						writer.println("	     args.push("+mp.getName()+");");
					}
				
//				// create return variable name
				String rv_name;	
				if (m.getReturnType().equals("void"))
					rv_name="'void'";
				else {
					
					rv_name="'r_"+classname+"_"+m.getName()+"_retvar'";
					rv_name="'r_"+m.getName()+"_retvar'";
				}


				// now invoke
				writer.print("	     ses.invoke(this.id,"+rv_name+",'" 
						+ m.getName()+"'");
				if(!m.getParams().isEmpty()){
					writer.print(",args");
				}
				writer.println(");");

				// return value
				String tmp = m.getReturnType();
				if (!tmp.equals("void")) {
					writer.println("	     ses.pushRet("+rv_name+");");
				} ;

				writer.println("	} ");
				writer.println("");
			}

			writer.println("");
			writer.println("}		 ");
			writer.println();

			writer.close();
		}

	}

}
