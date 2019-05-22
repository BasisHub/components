package com.basiscomponents.db.datarowtest;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataRowProvider;
import org.junit.jupiter.api.Test;

import static com.basiscomponents.db.util.DataRowProvider.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetFieldsHavingTest {


  @Test
  public void testStandardCaseWithEmpty() throws Exception {
    DataRow dr = DataRowProvider.buildSampleDataRow(false);
    DataRow dr2 = dr.getFieldsHavingAttribute("TWO", true);
    BBArrayList<String> v = dr2.getFieldNames();
    assertEquals(2, v.size());
    assertTrue(v.contains("INTFIELD"));
    assertTrue(v.contains("DOUBLEFIELD"));
  }

  @Test
  public void testStandardCaseWithoutEmpty() throws Exception {
    DataRow dr = DataRowProvider.buildSampleDataRow(false);
    DataRow dr2 = dr.getFieldsHavingAttribute("TWO", false);
    BBArrayList<String> v = dr2.getFieldNames();
    assertEquals(0, v.size());
  }

  @Test
  public void testFieldSubset() throws Exception {
    DataRow dr = DataRowProvider.buildSampleDataRow(false);

    DataRow subset = new DataRow();
    subset.setFieldValue(STRINGFIELD, "");
    subset.setFieldValue(DOUBLEFIELD, "");
    subset.setFieldValue(DATEFIELD, "");
    subset.setFieldValue(TIMESTAMPFIELD, "");

    DataRow dr2 = dr.getFieldsHavingAttribute("THREE", true, subset);
    BBArrayList<String> v = dr2.getFieldNames();
    assertEquals(2, v.size());
  }


}
