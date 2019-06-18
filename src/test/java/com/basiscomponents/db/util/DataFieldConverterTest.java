package com.basiscomponents.db.util;

import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DataFieldConverterTest {
    @Test
    public void testThreeMillisecondTimeStamp() throws java.io.IOException, java.text.ParseException {
        java.sql.Timestamp ts=(java.sql.Timestamp) DataFieldConverter.convertType("2019-06-18T00:31:42.845-06:00", java.sql.Types.TIMESTAMP);
        java.sql.Timestamp ts2 = java.sql.Timestamp.valueOf("2019-06-18 00:31:42.845");
        assertEquals( ts2,ts);
    }
    @Test
    public void testConvertTimeStamp(){

        String timestamp="2019-05-28 07:08:23.61-06:00";
        java.sql.Timestamp ts = (java.sql.Timestamp) DataFieldConverter.convertType(timestamp, Types.TIMESTAMP);

    }
}
