package com.basiscomponents.bridge.proxygen;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class GeneratorJava {

	private static final String AllowedTypes = "DataRow ResultSet BBjNumber BBjString void Boolean";

	public static void createJavaProxyClasses(
			HashMap<String, ParseEntity> classes, String packagename,
			String outputfolder, String classfileprefix, String classfilesuffix)
			throws FileNotFoundException, UnsupportedEncodingException {

		Set<String> ks = classes.keySet();
		Iterator<String> it = ks.iterator();
		while (it.hasNext()) {
			String classname = it.next();
			System.out.println("------------------------\n" + classname);

			ParseEntity pe = classes.get(classname);
			String filename = outputfolder + classname + ".java";
			PrintWriter writer = new PrintWriter(filename, "UTF-8");
			writer.println("package " + packagename + ";");
			writer.println();
			writer.println("");
			writer.println("import com.basiscomponents.db.ResultSet;");
			writer.println("import com.basiscomponents.bridge.*; ");
			writer.println("import com.basiscomponents.db.DataRow; ");
			writer.println("import java.util.UUID;");
			writer.println(" ");
			writer.println("public class " + pe.getClassname() + " { ");
			writer.println(" ");
			writer.println("	private Session ses; ");
			writer.println("	private String classid; ");
			writer.println("	 ");
			writer.println("	public " + pe.getClassname() + "(Session ses){ ");
			writer.println("		this.ses = ses; ");
			writer.println("		this.classid = \"X_\"+UUID.randomUUID().toString().replaceAll(\"-\", \"\"); ");
			writer.println("		ses.create(classid,\"::" + classfileprefix
					+ pe.getClassname() + classfilesuffix + "::"
					+ pe.getClassname() + "\"); ");
			writer.println("	} ");
			writer.println("");

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
				writer.print("	public "
						+ (m.getReturnType() != null ? m.getReturnType()
								: "void") + " " + m.getName() + "(");

				it2 = m.getParams().iterator();
				Boolean second = false;
				while (it2.hasNext()) {
					MethodParameter mp = it2.next();
					if (second)
						writer.print(",");
					else
						second = true;
					writer.print(mp.getType() + " " + mp.getName());
				}

				writer.println("){ ");

				// prepare parameters into session
				String invokeargs = "";
				it2 = m.getParams().iterator();
				while (it2.hasNext()) {
					MethodParameter mp = it2.next();
					writer.println("");
					writer.println("	     //variable for parameter "
							+ mp.getName());
					writer.println("	     String p_" + mp.getName()
							+ "=  classid+\"_" + mp.getName() + "\";");
					writer.println("	     ses.pushVar(p_" + mp.getName() + ", "
							+ mp.getName() + ");");
					writer.println("");

					invokeargs += ",p_" + mp.getName();
				}

				// create return variable name
				writer.println("");
				writer.println("	     //prepare a name for the return value");
				if (m.getReturnType().equals("void"))
					writer.println("	     String rv = \"dummy\";");
				else {
					writer.println("	     String rv = classid+\"_"
							+ m.getName() + "_retvar\";");
					writer.println("	     ses.pushRet(rv);");
				}
				writer.println("");

				// now invoke
				writer.println("");
				writer.println("	     //the invoke itself");
				writer.println("	     ses.invoke(classid,rv,\"" + m.getName()
						+ "\"" + invokeargs + ");");
				writer.println("");

				// return value
				String tmp = m.getReturnType();
				if (!tmp.equals("void")) {
					writer.println("	     //the method is not void, so execute right away");
					writer.println("	     ses.exec();");
					if (m.getReturnType().equals("DataRow")){
						//DataRow is always wrapped in a ResultSet
						writer.println("	     return (DataRow)(((ResultSet) ses.getResult(rv)).getItem(0));");						
					}
					else{
					writer.println("	     return (" + m.getReturnType()
							+ ")ses.getResult(rv);");
					}
				} else {
					writer.println("	     //the method void, so we do not execute right away but wait");
				}

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
