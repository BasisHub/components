package com.basiscomponents.db.datarowtest;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basis.startup.type.BBjNumber;
import com.basis.util.common.BasisNumber;
import com.basiscomponents.db.DataRow;

import junit.framework.Assert;

public class BBjTemplatesTest {
	
	private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
	
	public static String asHex(String str)
	{
		byte[] buf = str.getBytes();
	    char[] chars = new char[2 * buf.length];
	    for (int i = 0; i < buf.length; ++i)
	    {
	        chars[2 * i] = HEX_CHARS[(buf[i] & 0xF0) >>> 4];
	        chars[2 * i + 1] = HEX_CHARS[buf[i] & 0x0F];
	    }
	    return new String(chars);
	}
	
	@Test
	public void DynTemplate() throws Exception {


		BigDecimal d = new BigDecimal("-123.456");
		DataRow dr = new DataRow();
		dr.setFieldValue("ASTRING", "TEST123456");
		dr.setFieldValue("ANUMBER", d);
		dr.setFieldValue("SOMEINT",17);

		System.out.println(dr.getTemplate());
		System.out.println(dr.getString());
		System.out.println("compares "+dr.getFieldAsNumber("ANUMBER").equals(d));
//		Assert.assertEquals("ASTRING:C(1*),ANUMBER:Y,SOMEINT:U(4)", dr.getTemplate());
//		Assert.assertEquals("544553543132333435360ac028b0a3d70a3d7100000011", asHex(dr.getString()));

		String tpl="ASTRING:C(10*=09),ANUMBER:N(14*=09),SOMEINT:N(3*=09)";
		
		dr.setTemplate(tpl);
//		Assert.assertEquals(tpl,dr.getTemplate());
		System.out.println(dr.getTemplate());
		System.out.println(dr.getString());
		System.out.println("compares "+dr.getFieldAsNumber("ANUMBER").equals(d));
		System.out.println(asHex(dr.getString()));
		System.out.println(asHex(dr.getString()));
		
		
//		DataRow dr2 = DataRow.fromTemplate(tpl, "TESTXXXXXX	-12.3	3");
//		System.out.println(dr2.toJson());		
		
	}

}
