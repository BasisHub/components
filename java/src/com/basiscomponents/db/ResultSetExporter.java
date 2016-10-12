package com.basiscomponents.db;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

public class ResultSetExporter {

	public static void writeXML(ResultSet rs, String rootTagName, String entityName, Writer wr) throws Exception{
		
		wr.write("<"+rootTagName+">\n");
		
		Iterator<DataRow> it = rs.iterator();
		while (it.hasNext()){
			
			wr.write("   <"+entityName+">\n");
			
			DataRow r = it.next();
			Iterator<String> rit = r.getFieldNames().iterator();
			while (rit.hasNext()){
				String f = rit.next();
				String fv = r.getFieldAsString(f);
				wr.write("      <"+f+">");
				wr.write(fv);
				wr.write("</"+f+">\n");
				
			}
			
			wr.write("   </"+entityName+">\n");
			
		}
		
		wr.write("</"+rootTagName+">\n");
	
	}
	
	public static void writeHTML(ResultSet rs, Writer wr) throws Exception{
		
		wr.write("<table>\n");
		
		if (rs.size()>0){
		
		Iterator<DataRow> it = rs.iterator();
			DataRow r = rs.get(0);
			List fieldnames = r.getFieldNames();
			
			wr.write("<thead>");
			
			Iterator<String> rit = fieldnames.iterator();
			while (rit.hasNext()){
				String f = rit.next();
				wr.write("<th>");
				wr.write(f);
				wr.write("</th>");
				
			}
			
			wr.write("</thead>\n");			
			
			
			while (it.hasNext()){
				
				wr.write("<tr>");
				
				r = it.next();
				rit = fieldnames.iterator();
				while (rit.hasNext()){
					String f = rit.next();
					String fv = r.getFieldAsString(f);
					wr.write("<td>");
					wr.write(fv);
					wr.write("</td>");
					
				}
				
				wr.write("</tr>\n");
				
			}
		}
		wr.write("</table>\n");
	
	}	
	

	public static void writeTXT(ResultSet rs, Writer wr) throws Exception{
		
		//TODO maybe do a variation where this can be set:
		String delim = "\t";
		Boolean writeHeader = true;
		
		
		if (rs.size()>0){
			
		Iterator<DataRow> it = rs.iterator();
		
			DataRow r = rs.get(0);
			List fieldnames = r.getFieldNames();
			
			Iterator<String> rit = fieldnames.iterator();
			if (writeHeader){
			
				
				while (rit.hasNext()){
					String f = rit.next();
					wr.write(f);
					wr.write(delim);
					
				}
				wr.write("\n");
			}
			
			
			while (it.hasNext()){
				
				r = it.next();
				rit = fieldnames.iterator();
				while (rit.hasNext()){
					String f = rit.next();
					String fv = r.getFieldAsString(f);
					wr.write(fv);
					wr.write(delim);
					
				}
				
				wr.write("\n");
				
			}
		}
	
	}		
	
	public static void writeJSON(ResultSet rs, Writer wr) throws Exception{
	
		wr.write(rs.toJson());
	
	}	
	
	
//  sample code:	
//	public static void main(String[] args) throws Exception {
//		StringWriter w = new StringWriter();
//		ResultSet rs = new ResultSet();
//		DataRow dr = new DataRow();
//		dr.setFieldValue("feld1","TEST1");
//		dr.setFieldValue("feld2","TEST2");
//		rs.add(dr);
//		ResultSetExporter.writeXML(rs, "articles","article", w);
//	    w.flush();
//	    w.close();			
//		
//		System.out.println(w.toString());
//
//		FileWriter fw = new FileWriter("D:/test.xml");
//		ResultSetExporter.writeXML(rs, "articles","article", fw);
//	    fw.flush();
//	    fw.close();	
//	    
//		fw = new FileWriter("D:/test.html");
//		ResultSetExporter.writeHTML(rs, fw);
//	    fw.flush();
//	    fw.close();		    
//
//		fw = new FileWriter("D:/test.json");
//		ResultSetExporter.writeJSON(rs, fw);
//	    fw.flush();
//	    fw.close();			    
//
//		fw = new FileWriter("D:/test.txt");
//		ResultSetExporter.writeTXT(rs, fw);
//	    fw.flush();
//	    fw.close();			    
//	    
//	    
//	}

}
