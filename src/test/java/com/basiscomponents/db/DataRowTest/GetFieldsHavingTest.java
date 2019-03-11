package com.basiscomponents.db.datarowtest;

import static com.basiscomponents.db.util.DataRowProvider.DATEFIELD;
import static com.basiscomponents.db.util.DataRowProvider.DOUBLEFIELD;
import static com.basiscomponents.db.util.DataRowProvider.STRINGFIELD;
import static com.basiscomponents.db.util.DataRowProvider.TIMESTAMPFIELD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.DataRowProvider;

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
