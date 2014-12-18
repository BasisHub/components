package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.sql.*;
import java.sql.Types.*;

public class TestMyStuff {

	
	public static void main(String[] args) throws Exception 
	{
		// TODO Auto-generated method stub

		
		DataRow r = new DataRow();
//		r.setFieldValue("VISTADRZIP", "112     ");
//		String k = r.replaceFields("        </tr>   pan=3 class=\"infoSubTitle\">$F{VISTADRZIP} $F{VISTADRCITY}</td>");
//		System.out.println(k);
		
		Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp t = new java.sql.Timestamp(now.getTime());
		System.out.println(t);
		r.setFieldValue("TS",t);
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("TS"));

		System.out.println("-------------------------");
		r.setFieldValue("DT",new java.sql.Date(now.getTime()));
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("DT"));
		
		r.setFieldValue("DT",-1);
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("DT"));
		System.out.println("-------------------------");
		
		r.setFieldValue("TIM",new java.sql.Time(now.getTime()));
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("TIM"));
		System.out.println(r.getFieldAsString("TIM"));
//		
//
//		Class.forName("com.basis.jdbc.BasisDriver");
//
//		String url = "jdbc:basis:beff8?DATABASE=CC&SSL=false";
//		Connection con = DriverManager.getConnection(url, "admin", "admin123");
//		Statement stmt = con.createStatement();
//		ResultSet rs = stmt.executeQuery("SELECT * FROM CLBASE WHERE CLBREF='1433012'");
//
//        		
//		
//        long start=System.currentTimeMillis();
//        
//		while (rs.next())
//		{
//			DataRow row = new DataRow(rs);
////			System.out.println(row);
//            
//		}
//		start = System.currentTimeMillis()-start;
//		System.out.print("duration: ");
//		System.out.println(start);
//		rs.close();
//		stmt.close();
//		con.close();
//		
		
		

		
	}

}
