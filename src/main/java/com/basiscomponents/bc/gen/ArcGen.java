package com.basiscomponents.bc.gen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.Iterator;

import com.basiscomponents.db.DataRow;

public class ArcGen {

    final String LF = "\r\n";
    	
	public String getArc(DataRow ar, String formname, String title) {

		StringWriter writer = new StringWriter();
		
        int colcount = ar.getColumnCount();
        int cid = 100;
        int row = 1;

        
        writer.append("//#charset: windows-1252");
        writer.append(LF);
        writer.append("");
        writer.append(LF);
        writer.append("VERSION \"4.0\"");
        writer.append(LF);
        writer.append("");
        writer.append(LF);
        
		writer.append("CHILD-WINDOW 100 0 0 500 "+((colcount+2)*30));
		writer.append(LF);
        writer.append("BEGIN");
        writer.append(LF);
        writer.append("    BORDERLESS");
        writer.append(LF);
        writer.append("    EVENTMASK 3287287492");
        writer.append(LF);
        writer.append("    HSCROLLBAR");
        writer.append(LF);
        writer.append("    KEYBOARDNAVIGATION");
        writer.append(LF);
        writer.append("    NAME \""+formname+"\"");
        writer.append(LF);
        writer.append("    VSCROLLBAR");
        writer.append(LF);
        writer.append("");
        writer.append(LF);

        Iterator<String> it = ar.getFieldNames().iterator();
        while (it.hasNext()) {
            String col_name = it.next();

            String col_label="";
			try {
				col_label = ar.getFieldAttribute(col_name, "LABEL");
			} catch (Exception e) {
			}
            if  (col_label.isEmpty())
                col_label=col_name;

            int col_type = ar.getFieldType(col_name);
            int col_size=0;
			try {
				col_size = Integer.valueOf(ar.getFieldAttribute(col_name, "ColumnDisplaySize"));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            if (col_size == 0 )
            	col_size=20;
            
            String ctrl="";
            int w=0;
            switch (col_type) {
                case java.sql.Types.BLOB:
                case java.sql.Types.LONGVARBINARY:
                    //TODO these types tend to be blobs
                    //skip here and see later what to do
                    break;

                case java.sql.Types.DECIMAL:
                case java.sql.Types.DOUBLE:
                case java.sql.Types.FLOAT:
                case java.sql.Types.INTEGER:
                case java.sql.Types.SMALLINT:
                case java.sql.Types.TINYINT:
                case java.sql.Types.NUMERIC:
                case java.sql.Types.REAL:
                    ctrl="INPUTN";
                    w=Math.min(Math.max(30,col_size*13),400);
                    break;

                case java.sql.Types.DATE:
                    ctrl="INPUTD";
                    w=100;
                    break;

                case java.sql.Types.BOOLEAN:
                case java.sql.Types.BIT:
                    ctrl="CHECKBOX";
                    w=200;
                    break;

                case java.sql.Types.TIMESTAMP:
                    ctrl="EDIT";
                    w=250;
                    break;

                default:
                    ctrl="INPUTE";
                    w=Math.min(Math.max(30,col_size*13),400);
                    break;
            }
//REM 
//REM                 if rsmd!.isReadOnly(i) or rsmd!.isAutoIncrement(i) then
//REM                     ctrl="STATICTEXT"
//REM                 fi

            if (ctrl.equals("INPUTE") ||  ctrl.equals("INPUTN") ||  ctrl.equals("INPUTD") ||  ctrl.equals("EDIT") ||  ctrl.equals("STATICTEXT")){
                writer.append("    STATICTEXT "+cid+", \""+col_label+":\", 10, "+(8+row*30)+", 130, 20");
                writer.append(LF);
                writer.append("        BEGIN");
                writer.append(LF);
////                rem writer.append("        JUSTIFICATION 32768");
////                rem right justify
                writer.append("            NAME \""+col_name+".label\"");
                writer.append(LF);
                writer.append("        END");
                writer.append(LF);
                writer.append("");
                writer.append(LF);

                cid++;
            }

            writer.append("    "+ctrl+" "+(cid)+", \"\", 150, "+(5+row*30)+", "+(w)+", 20");
            writer.append(LF);
            writer.append("        BEGIN");
            writer.append(LF);

            if (ctrl.equals("INPUTE")) {
            	
            	String fill="";
            	int siz = Math.min(100,col_size);
            	for (int i=0; i<siz; i++)
            		fill += "X";

                writer.append("            MASK \"");
                writer.append(fill);
                writer.append("\"");
                writer.append(LF);
// rem TODO: choose a better control for very large fields, like a CEdit
            }

            writer.append("            NAME \""+col_name+"\"");
            writer.append(LF);

            if (ctrl.equals("INPUTE")) {
                writer.append("            PADCHARACTER 32");
                writer.append(LF);
            }

            writer.append("        END");
            writer.append(LF);
            writer.append("");

            cid++;
            row++;
        }

        writer.append("END");
        writer.append(LF);
		
		return writer.toString();
	}


}
