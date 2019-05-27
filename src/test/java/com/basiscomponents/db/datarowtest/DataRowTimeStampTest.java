package com.basiscomponents.db.DataRowTest;

import com.basiscomponents.db.DataRow;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;

public class DataRowTimeStampTest {
    @Test
    public void testUSTimeStamp() throws IOException, ParseException {
        String response = "[{\"TIME\":\"2019-05-27 03:15:19.587-06:00\",\"meta\":{\"TIME\":{\"ColumnType\":\"91\"}}}]";
        DataRow dr = DataRow.fromJson(response);

    }
}
