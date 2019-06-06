package com.basiscomponents.db.util;

import com.basiscomponents.db.DataField;
import org.junit.jupiter.api.Test;

import java.sql.Types;

public class DataFieldConverterTest {
    @Test
    public void testConvertTimeStamp(){

        String timestamp="2019-05-28 07:08:23.61-06:00";
        java.sql.Timestamp ts = (java.sql.Timestamp) DataField.convertType(timestamp, Types.TIMESTAMP);

    }
}
