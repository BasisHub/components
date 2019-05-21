package com.basiscomponents.db.datarowtest;

import com.basiscomponents.db.DataRow;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetFieldValueTest {

  @Test
  public void RedefineFieldContentsTest() throws Exception {
    DataRow dr = new DataRow();
    dr.setFieldValue("TEST", "ONE");
    dr.setFieldValue("TEST", "TWO");
    assertEquals("TWO", dr.getFieldAsString("TEST"));
  }

}
