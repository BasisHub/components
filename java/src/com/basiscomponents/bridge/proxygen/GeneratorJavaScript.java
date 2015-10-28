package com.basiscomponents.bridge.proxygen;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class GeneratorJavaScript {

	private static final String AllowedTypes = "DataRow ResultSet BBjNumber BBjString void";

	public static void createJavaScriptProxyClasses(
			HashMap<String, ParseEntity> classes, 
			String outputfolder, String classfileprefix, String classfilesuffix)
			throws FileNotFoundException, UnsupportedEncodingException {

		

		Set<String> ks = classes.keySet();
		Iterator<String> itfiles = ks.iterator();
		while (itfiles.hasNext()) {
			String classname = itfiles.next();
			HashSet<String> overloadedMethods = new HashSet<>();
			ParseEntity pe = classes.get(classname);
			String filename = outputfolder + classname + ".js";
			PrintWriter writer = new PrintWriter(filename, "UTF-8");

			writer.println("function "+classname+"(){"); 
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
				
				if (m.getIsOverloaded()){
					overloadedMethods.add(m.getName());
					writer.println("//overloaded method of "+m.getName());
					writer.print(classname+".prototype."+ m.getName()
							+ getMethodSignatureString(m)
							+ " = function("); 
				}
				else
				{
					writer.print(classname+".prototype."+ m.getName()
							+ " = function("); 
				}

				it2 = m.getParams().iterator();
				Boolean second = false;
				while (it2.hasNext()) {
					MethodParameter mp = it2.next();
					if (second)
						writer.print(",");
					else
						second = true;
					if (!m.getIsOverloaded())
						writer.print(mp.getName());
					else
						writer.print(mp.getType()+"_"+mp.getName());
				}

				writer.println(") { ");

				// prepare parameters into session
				if(!m.getParams().isEmpty()){
					writer.println("	     var args = new Array();");
				}
					

					it2 = m.getParams().iterator();
					while (it2.hasNext()) {
						MethodParameter mp = it2.next();
						if (!m.getIsOverloaded())
							writer.println("	     args.push("+mp.getName()+");");
						else
							writer.println("	     args.push("+mp.getType()+"_"+mp.getName()+");");							
					}
				
//				// create return variable name
				String rv_name;	
				if (m.getReturnType().equals("void"))
					rv_name="'void'";
				else {
					rv_name="'r_"+classname+"_"+m.getName()+"_retvar'";
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

			//now generate the parent function to dispatch all the overloaded versions
			Iterator<String> it = overloadedMethods.iterator();
			while (it.hasNext())
			{
				String methodname = it.next();
				writer.println("//parent for overloaded function "+methodname);
				writer.println(classname+".prototype."+ methodname+ " = function(){"); 

				Iterator<Method> itm = pe.getMethods().iterator();
				while (itm.hasNext()){
					Method m = itm.next();

					if (m.getName().equals(methodname)){
						Integer i=0;
						writer.print  ("     if(");
						
						Iterator<MethodParameter> it2 = m.getParams().iterator();
						while (it2.hasNext()) {
							MethodParameter mp = it2.next();
								writer.print("ses.getTypeof(arguments["+i.toString()+"])=='");
								switch (mp.getType()){
									case "String":
										writer.print("str");
										break;
									case "Number":
										writer.print("num");
										break;
									default:
										writer.print("dr");
										break;										
								}
								writer.print("' && ");
								i++;
						}						
						
						writer.println("typeof(arguments["+i.toString()+"])=='undefined'){");
						writer.println("        this."+methodname+getMethodSignatureString(m)+".apply(this,arguments);");
						writer.println("     };");
					}
				}
				writer.println("}");
			}
			
			writer.println("");


			writer.close();
		}

	}

	private static String getMethodSignatureString(Method m) {
		String sig = "";
		Iterator<MethodParameter> it2 = m.getParams().iterator();
		while (it2.hasNext()) {
			String t = it2.next().getType();
			switch (t){
				case "String":
					sig += 'S';
					break;
				case "Number":
					sig += 'N';
					break;
				default:
					sig += 'O';
				
				}//switch
		}//while
	
		if (sig.isEmpty())
			sig="VOID";
		return sig;
	}

}
