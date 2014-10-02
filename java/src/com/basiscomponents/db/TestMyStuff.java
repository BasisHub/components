package com.basiscomponents.db;

import java.util.Calendar;

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

		r.setFieldValue("DT",new java.sql.Date(now.getTime()));
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("DT"));

		r.setFieldValue("TIM",new java.sql.Time(now.getTime()));
		System.out.println(r);
		System.out.println(r.getFieldAsNumber("TIM"));
		System.out.println(r.getFieldAsString("TIM"));

		
	}

}
