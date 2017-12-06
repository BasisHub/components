package com.basiscomponents.db.DataRowTest;

import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;

public class GetFieldsHavingTest {

	private DataRow buildSampleDataRow(boolean f_nullAllFields) throws Exception{
		DataRow dr = new DataRow();
		dr.setFieldValue("STRINGFIELD", "Value");
		dr.setFieldValue("INTFIELD", 1);
		dr.setFieldValue("DOUBLEFIELD", 1.1);
		dr.setFieldValue("BOOLFIELD", true);
		dr.setFieldValue("DATEFIELD", new java.sql.Date(System.currentTimeMillis()));
		dr.setFieldValue("TIMESTAMPFIELD", new java.sql.Timestamp(System.currentTimeMillis()));

		if (f_nullAllFields){
			dr.setFieldValue("STRINGFIELD", 	null);
			dr.setFieldValue("INTFIELD", 		null);
			dr.setFieldValue("DOUBLEFIELD", 	null);
			dr.setFieldValue("BOOLFIELD", 		null);
			dr.setFieldValue("DATEFIELD", 		null);
			dr.setFieldValue("TIMESTAMPFIELD", 	null);
		}
		
		dr.setFieldAttribute("STRINGFIELD", 	"ISNUMERIC", "0");
		dr.setFieldAttribute("INTFIELD", 		"ISNUMERIC", "1");
		dr.setFieldAttribute("DOUBLEFIELD", 	"ISNUMERIC", "1");
		dr.setFieldAttribute("BOOLFIELD", 		"ISNUMERIC", "0");
		dr.setFieldAttribute("DATEFIELD", 		"ISNUMERIC", "0");
		dr.setFieldAttribute("TIMESTAMPFIELD", 	"ISNUMERIC", "0");

		
		dr.setFieldAttribute("STRINGFIELD", 	"TYPE", "TEXT");
		dr.setFieldAttribute("INTFIELD", 		"TYPE", "NUMBER");
		dr.setFieldAttribute("DOUBLEFIELD", 	"TYPE", "NUMBER");
		dr.setFieldAttribute("BOOLFIELD", 		"TYPE", "TRUTH");
		dr.setFieldAttribute("DATEFIELD", 		"TYPE", "MOMENT");
		dr.setFieldAttribute("TIMESTAMPFIELD", 	"TYPE", "MOMENT");		
		
		dr.setFieldAttribute("STRINGFIELD", 	"ONE", "");
		dr.setFieldAttribute("INTFIELD", 		"TWO", "");
		dr.setFieldAttribute("DOUBLEFIELD", 	"TWO", "");
		dr.setFieldAttribute("BOOLFIELD", 		"THREE", "");
		dr.setFieldAttribute("DATEFIELD", 		"THREE", "");
		dr.setFieldAttribute("TIMESTAMPFIELD", 	"THREE", "");	
		
		return dr;
	}
	
	@Test
	public void testStandardCaseWithEmpty() throws Exception {
		DataRow dr = this.buildSampleDataRow(false);
		DataRow dr2 = dr.getFieldsHavingAttribute("TWO",true);
		BBArrayList<String> v = dr2.getFieldNames();
		Assert.assertEquals(2, v.size());
		Assert.assertTrue(v.contains("INTFIELD"));
		Assert.assertTrue(v.contains("DOUBLEFIELD"));
	}

	@Test
	public void testStandardCaseWithoutEmpty() throws Exception {
		DataRow dr = this.buildSampleDataRow(false);
		DataRow dr2 = dr.getFieldsHavingAttribute("TWO",false);
		BBArrayList<String> v = dr2.getFieldNames();
		Assert.assertEquals(0, v.size());
	}

	@Test
	public void testFieldSubset() throws Exception {
		DataRow dr = this.buildSampleDataRow(false);
		
		DataRow subset = new DataRow();
		subset.setFieldValue("STRINGFIELD", "");
		subset.setFieldValue("DOUBLEFIELD", "");
		subset.setFieldValue("DATEFIELD", "");
		subset.setFieldValue("TIMESTAMPFIELD","");
		
		DataRow dr2 = dr.getFieldsHavingAttribute("THREE",true,subset);
		System.out.println(dr2);
		BBArrayList<String> v = dr2.getFieldNames();
		Assert.assertEquals(2, v.size());
	}

	
}
