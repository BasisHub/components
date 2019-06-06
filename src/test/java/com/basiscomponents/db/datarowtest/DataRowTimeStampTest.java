package com.basiscomponents.db.datarowtest;

import com.basiscomponents.db.ResultSet;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DataRowTimeStampTest {
    @Test
    public void testUSTimeStamp() throws IOException, ParseException {
        String response = "[{\"TIME\":\"2019-05-27 09:47:12.055-06:00\",\"meta\":{\"TIME\":{\"ColumnType\":\"91\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);
        assertNotNull(rs);

    }
}
