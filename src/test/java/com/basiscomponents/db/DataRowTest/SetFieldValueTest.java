package com.basiscomponents.db.datarowtest;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.basiscomponents.db.DataRow;

public class SetFieldValueTest {

  @Test
  public void RedefineFieldContentsTest() throws Exception {
    DataRow dr = new DataRow();
    dr.setFieldValue("TEST", "ONE");
    dr.setFieldValue("TEST", "TWO");
    assertEquals("TWO", dr.getFieldAsString("TEST"));
  }

}
