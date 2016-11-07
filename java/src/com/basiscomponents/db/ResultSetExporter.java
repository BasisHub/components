package com.basiscomponents.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ResultSetExporter {

	public static void writeXML(ResultSet rs, String rootTagName, String entityName, Writer wr) throws Exception{
		
		wr.write("<"+rootTagName+">\n");
		
		Iterator<DataRow> it = rs.iterator();
		List fieldnames = rs.getColumnNames();
		
		while (it.hasNext()){
			
			wr.write("   <"+entityName+">\n");
			
			DataRow r = it.next();
			Iterator<String> rit = fieldnames.iterator();
			while (rit.hasNext()){
				String f = rit.next();
				if (f.contains("%")) continue; //TODO filter out all field names that are invalid tag names 
				String fv ="";
				try {
				fv = r.getFieldAsString(f).trim();
				wr.write("      <"+f+">");
				wr.write(fv);
				wr.write("</"+f+">\n");
				
				}
				catch (Exception e){
				wr.write("      <"+f+" null=\"true\" />\n");
				}
				
			}
			
			wr.write("   </"+entityName+">\n");
			
		}
		
		wr.write("</"+rootTagName+">\n");
	
	}
	

	
	public static void writeHTML(ResultSet rs, Writer wr) throws Exception{
		ResultSetExporter.writeHTML(rs, wr, null);
	}
	
	/**
	 * 
	 * @param rs: the ResultSet to export
	 * @param wr: a java.io.Writer Instance
	 * @param links: a Hashmap with fieldname / url pattern pairs, like http://xyz/a/{key}/gy
	 * @throws Exception
	 */
	public static void writeHTML(ResultSet rs, Writer wr, HashMap<String,String> links) throws Exception{
		
		wr.write("<table>\n");
		
		if (rs.size()>0){
		
		List<String> fieldnames = rs.getColumnNames();
		
		Iterator<DataRow> it = rs.iterator();
			DataRow r = rs.get(0);
			
			
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
					String fv = "";
					try {
					
					String link=null;
					if (links != null) link=links.get(f);
					
						
					fv = r.getFieldAsString(f).trim();
					wr.write("<td>");
					if (link != null){
						link=link.replace("{key}", fv);
							

						Iterator<String> inner_rit = fieldnames.iterator();
						while (inner_rit.hasNext()){
							String inner_f = (String) inner_rit.next();
							String inner_fv = "";
							try {
								inner_fv = r.getFieldAsString(inner_f).trim();
								
							}
							catch (Exception e) {}
							link=link.replace("{"+inner_f+"}", inner_fv);
						}
						wr.write("<a href='"+link+"'>");
						
					}
					wr.write(fv);
					if (link != null)
						wr.write("</a>");
					wr.write("</td>");
					}
					catch (Exception e) {
						wr.write("<td class='missing'>");
						wr.write("");
						wr.write("</td>");						
					}
					
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

		List<String> fieldnames = rs.getColumnNames();
		Iterator<DataRow> it = rs.iterator();
		
			DataRow r = rs.get(0);
			
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
					String fv = "";
					try {
					fv = r.getFieldAsString(f).trim();
					} catch (Exception e)
					{}
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
	
    public static void writeXLSX(ResultSet rs, File outputFile, boolean writeHeader) throws Exception{
    	if(!rs.isEmpty()){
    		if(outputFile != null && outputFile.canWrite()){
    			String sheetName = "Sheet1";//name of sheet
                
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet(sheetName) ;
                
                List<String> fieldnames = rs.getColumnNames();
        		Iterator<DataRow> it = rs.iterator();
        		Iterator<String> fieldNameIterator;
                
        		XSSFRow row;
        		XSSFCell cell;
        		int rowIndex = 0;
        		int cellIndex = 0;
        		
                if(writeHeader){
                	fieldNameIterator = fieldnames.iterator();
                	row = sheet.createRow(rowIndex);
                	rowIndex++;
                	
            		while(fieldNameIterator.hasNext()){
            			cell = row.createCell(cellIndex);
            			cell.setCellValue(fieldNameIterator.next());
            			cellIndex++;
            		}
                }
                
                DataRow currentRow;
                String currentFieldName;
                while(it.hasNext()){
                	fieldNameIterator = fieldnames.iterator();
                	
                	row = sheet.createRow(rowIndex);
                	rowIndex++;
                	
                	cellIndex = 0;
                	currentRow = it.next();
                	int columnType;
                	while(fieldNameIterator.hasNext()){
                		currentFieldName = fieldNameIterator.next();
                		cell = row.createCell(cellIndex);
                		columnType = rs.getColumnType(rs.getColumnIndex(currentFieldName));
            			if(columnType == java.sql.Types.NUMERIC || columnType == java.sql.Types.DOUBLE){
            				cell.setCellType(XSSFCell.CELL_TYPE_NUMERIC);
            				cell.setCellValue(currentRow.getFieldAsNumber(currentFieldName));
            			}else{
            				cell.setCellValue(currentRow.getFieldAsString(currentFieldName));
            			}
            			cellIndex++;
                	}
                }
                
                FileOutputStream os = new FileOutputStream(outputFile);
                wb.write(os);
                wb.close();
                
                os.flush();
                os.close();
        	}
    	}
    }
	
//  sample code:
	
//	public static void main(String[] args) throws Exception {
//		StringWriter w = new StringWriter();
//		ResultSet rs = new ResultSet();
//		DataRow dr = new DataRow();
//		dr.setFieldValue("feld1","TEST1");
//		dr.setFieldValue("feld2","TEST2");
//		rs.add(dr);
//
//		dr = new DataRow();
//		dr.setFieldValue("feld1","TEST1");
//		dr.setFieldValue("feld3","TEST3");
//		rs.add(dr);
//
//		dr = new DataRow();
//		dr.setFieldValue("feld1","TEST1");
//		dr.setFieldValue("feld2","TEST2");
//		dr.setFieldValue("feld3","TEST3");
//		rs.add(dr);
//		
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
//	    HashMap<String, String> links = new HashMap<>();
//	    links.put("feld1","/rest/test/{feld1}");
//	    links.put("feld2","/rest/test/{feld1}/{feld2}");
//		fw = new FileWriter("D:/test.html");
//		ResultSetExporter.writeHTML(rs, fw, links);
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
