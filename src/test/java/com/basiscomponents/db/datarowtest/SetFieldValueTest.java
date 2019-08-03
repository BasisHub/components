package com.basiscomponents.db.datarowtest;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.util.Regression;
import org.junit.jupiter.api.Test;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetFieldValueTest {

  @Test
  public void RedefineFieldContentsTest() throws Exception {
    DataRow dr = new DataRow();
    dr.setFieldValue("TEST", "ONE");
    dr.setFieldValue("TEST", "TWO");
    assertEquals("TWO", dr.getFieldAsString("TEST"));
  }
  @Test
  @Regression(issue = "#155")
  public void testDateField() throws ParseException {
    DataRow dr = new DataRow();
    dr.setFieldValue("DATE",java.sql.Types.DATE,"2013-02-01");
  }

}
