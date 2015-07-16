package com.basiscomponents.db;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.sql.*;
import java.sql.Types.*;

import sun.reflect.LangReflectAccess;

import com.basis.util.common.BBjNumber;
import com.basis.util.common.BasisNumber;

public class TestMyStuff {

	
	public static void main(String[] args) throws Exception 
	{
		

		
//		HashMap hm = new HashMap();
//		hm.put("A","AA");
//		hm.put("BB", "BBB");
//		DataRow rh = new DataRow(hm);
//		System.out.println(rh);
		
		
		
		
		DataRow r = new DataRow();
		
		
		
//		r.setFieldValue("VISTADRZIP", "112     ");
//		String k = r.replaceFields("        </tr>   pan=3 class=\"infoSubTitle\">$F{VISTADRZIP} $F{VISTADRCITY}</td>");
//		System.out.println(k);
		
		java.util.Calendar calendar = Calendar.getInstance();
		java.util.Date now = calendar.getTime();
		java.sql.Timestamp t = new java.sql.Timestamp(now.getTime());
		
		
		r.setFieldValue("MYTIMESTAMP",t);
		
		
		Date d = java.sql.Date.valueOf(com.basis.util.BasisDate.date(2457049,0.0,"%Yl-%Mz-%Dz"));
		
		
		r.setFieldValue("MYTIMESTAMP",d);
		r.setFieldValue("Name","Müller");
		r.setFieldValue("Age",35);
		
		System.out.println(r.toJson());
		
		long t1,t2,i;

		
	
		DataRow r2 ;
		r2 = DataRow.fromJson(r.toJson());
		String tj = r.toJson();
		r2 = DataRow.fromJson(tj);
		
		ResultSet rs = new ResultSet();
		rs.addItem(r);
		rs.addItem(r2);

		rs.toJsonElementOld();
		rs.toJsonElement();
		
		t1= System.currentTimeMillis();
		for (i=0; i<1000; i++) rs.toJsonElementOld();
		System.out.println(rs.toJsonElementOld());
		t1 = System.currentTimeMillis()-t1;
		System.out.println(t1);
		
		t2 = System.currentTimeMillis();
		for (i=0; i<1000; i++) rs.toJsonElement();
		System.out.println(rs.toJsonElement());		
		
		t2 = System.currentTimeMillis()-t2;
		System.out.println(t2);
/*				
		
		String rsst = rs.toJson();
		System.out.println(rsst);
		
		ResultSet rs2 = ResultSet.fromJson(rsst);
		System.out.println(rs2);
		
/*	
		System.out.println("----------------------------");
//		r.setFieldValue("TS",t);
//		System.out.println(r);
//		System.out.println(r.getFieldAsNumber("TS"));
//		r.setFieldValue("TS", "");
//
//		System.out.println("-------------------------");
//		r.setFieldValue("DT",new java.sql.Date(now.getTime()));
//		System.out.println(r);
//		System.out.println(r.getFieldAsNumber("DT"));
//		r.setFieldValue("DT", "");
//		
//		r.setFieldValue("DT",-1);
//		System.out.println(r);
//		System.out.println(r.getFieldAsNumber("DT"));
//		System.out.println("-------------------------");
//		
//		r.setFieldValue("TIM",new java.sql.Time(now.getTime()));
//		System.out.println(r);
//		System.out.println(r.getFieldAsNumber("TIM"));
//		System.out.println(r.getFieldAsString("TIM"));
//		
//		r.setFieldValue("INTE",new java.sql.Date( com.basis.util.BasisDate.date(2457019).getTime() ));
//		r.setFieldValue("INTE","");
//		System.out.println(r.getFieldAsString("INTE"));
//		
//		System.out.println(r.getFieldNames());
//		System.out.println(r.clone().getFieldNames());
//		
//		java.sql.Date d;
//		d=null;
////		r.setFieldValue("testdate",d);
//		BBjNumber n ;
//		n= BasisNumber.createBasisNumber(-1);
//		
//		r.setFieldValue("testdate",new java.sql.Date(0));
//		System.out.println(r.getFieldAsNumber("testdate"));
//		System.out.println(r.getField("testdate"));
//		r.setFieldValue("testdate",n);
//		System.out.println(r.getFieldAsNumber("testdate"));
//		System.out.println(r.getField("testdate"));
//
//		r.setFieldValue("STR1","string1"); 
//		
//		DataRow r1 = new DataRow();
//		r1.setFieldValue("STR1","string1a");
//		r1.setFieldValue("STR2","string2a");
//		
//		System.out.println(r);
//		System.out.println(r1);
//		r.mergeRecord(r1);
//		System.out.println(r);
//		
//
//		Class.forName("com.basis.jdbc.BasisDriver");
//
//		String url = "jdbc:basis:beff8?DATABASE=CC&SSL=false";
//		Connection con = DriverManager.getConnection(url, "admin", "admin123");
//		Statement stmt = con.createStatement();
//		
// 	 String sql = "SELECT B.CLBREF,B.CLBDATECREA,B.CLBUSRCREA,B.CLBDATEDERMOD,B.CLBUSRDERMOD,B.CLBCLI,B.CLBDATEACC,B.CLBDATECLO,B.CLBUSRCLO,B.CLBREFCLO,B.CLBUSRPRO,B.CLBREFCLIENT,B.CLBREFLEASCLIENT,B.CLBDATEDECL,B.CLBCIRCONST,B.CLBIMMAT,B.CLBDEGTCORP,B.CLBTEMOIN,B.CLBLIEUACC,B.CLBCONDUCT,B.CLBDATENAISCONDU,B.CLBDESCRACC,B.CLBREM,B.CLBRESP,B.CLBAUTORITE,B.CLBAUTORITELIEU,B.CLBDATEPV,B.CLBNUMPV,B.CLBALCOOLTS,B.CLBPRSSANG,B.CLBSPECIF,B.CLBMAILKEY2,B.CLBMAILKEY3,B.CLBNBRSDS,B.CLBPA1LIBRC,B.CLBPA1LIBFT,B.CLBPA1RUE,B.CLBPA1NUM,B.CLBPA1BTE,B.CLBPA1PAYS,B.CLBPA1POSTE,B.CLBPA1LOCAL,B.CLBPA1TEL,B.CLBPA1LANGUE,B.CLBPA1DATENAIS,B.CLBPA1CIE,B.CLBPA1POL,B.CLBPA1IMMAT,B.CLBPA1REFCIE,B.CLBPA1REM,B.CLBPA2LIBRC,B.CLBPA2LIBFT,B.CLBPA2RUE,B.CLBPA2NUM,B.CLBPA2BTE,B.CLBPA2PAYS,B.CLBPA2POSTE,B.CLBPA2LOCAL,B.CLBPA2TEL,B.CLBPA2LANGUE,B.CLBPA2DATENAIS,B.CLBPA2CIE,B.CLBPA2POL,B.CLBPA2IMMAT,B.CLBPA2REFCIE,B.CLBPA2REM,B.CLBPA3LIBRC,B.CLBPA3LIBFT,B.CLBPA3RUE,B.CLBPA3NUM,B.CLBPA3BTE,B.CLBPA3PAYS,B.CLBPA3POSTE,B.CLBPA3LOCAL,B.CLBPA3TEL,B.CLBPA3LANGUE,B.CLBPA3DATENAIS,B.CLBPA3CIE,B.CLBPA3POL,B.CLBPA3IMMAT,B.CLBPA3REFCIE,B.CLBPA3REM,B.CLBMAILKEY4,B.CLBMAILKEY5,B.CLBMAILKEY61,B.CLBMAILKEY62,B.CLBMAILKEY7,B.CLBMAILKEY8,B.CLBMAILKEY9,B.CLBMAILKEY10,B.CLBDATEREPAIR,B.CLBWAY,B.CLBLOCADAMAGE,B.CLBMEMO,D.ACCIDENT_HOUR,D.VEHICLE_BRAND,D.SENIORITY_WITHIN_COMPANY,D.ACCIDENT_LOCATION,D.ACCIDENT_CAUSE,D.VEHICLE_CARGO,D.TECHNICAL_FAILURE,D.ORGANISATION_FAILURES,D.DRIVER_RESPONSIBLE_FOR_ACCIDENT,D.VHCIMPACT,D.COULD_ACCIDET_BE_AVOIDED,D.OPINION_NEXT_ACCIDENT_DIFFERENCE,D.NOT_RESPECTED_PREVENTION_RULES,D.VIASELF,D.VIAEY,P.POLICE FROM CLBASE B LEFT OUTER JOIN CLWEBDECLA D ON B.CLBREF=D.CLBREF LEFT OUTER JOIN (SELECT TOP 1 POLICE , PLATE from CC23CC) P ON  P.PLATE=B.CLBIMMAT   WHERE  (CLBCLI = '021494') AND ( ( clbmailkey4='EB' ))  AND (B.CLBREF LIKE '14%' or B.CLBREF LIKE '15%' or B.CLBREF LIKE '18%' or B.CLBREF LIKE '28%' or B.CLBREF LIKE '29%' or B.CLBREF LIKE '34%' or B.CLBREF LIKE '35%' or B.CLBREF LIKE '36%' or B.CLBREF LIKE '37%' or B.CLBREF LIKE '69%')";
//		
//		//ResultSet rs = stmt.executeQuery("SELECT TOP 1 * FROM CLBASE ");
// 	 	ResultSet rs = stmt.executeQuery(sql);
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

		ResultSet rs = new ResultSet();
		rs.addItem("blabla"); 
		System.out.println(rs);
		
		System.out.print("***finish***");
*/
		
	}

}
