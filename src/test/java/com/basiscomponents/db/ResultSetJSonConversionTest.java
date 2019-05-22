package com.basiscomponents.db;


import com.basiscomponents.db.util.DataRowProvider;
import com.basiscomponents.db.util.ResultSetProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ResultSetJSonConversionTest {

    /**
     * This method takes two DataRows and compares their values with the getFieldAsString method from DataRow to evaluate the process of toJson/fromJson
     *
     * @param oldDR The initial DataRow before the conversion with toJson/fromJson
     * @param newDR The converted DataRow after the conversion with toJson/fromJson
     * @param json  The Json String converted from the oldDR
     */
    public void equalityAsStringDataRowTest(DataRow oldDR, DataRow newDR, String json) {
        BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
        BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
        assertTrue(fieldNamesOld.size() == fieldNamesNew.size(), json);
        for (int i = 0; i < fieldNamesOld.size(); i++) {
            String fieldname = fieldNamesOld.get(i);
            assertEquals( oldDR.getFieldAsString(fieldname), newDR.getFieldAsString(fieldname), ()->json + "\n" + fieldname + " values differ with function getFieldAsString");
        }
    }

    /**
     * This method takes two DataRows and compares their values with the getFieldAsNumber method from DataRow to evaluate the process of toJson/fromJson
     *
     * @param oldDR The initial DataRow before the conversion with toJson/fromJson
     * @param newDR The converted DataRow after the conversion with toJson/fromJson
     * @param json  The Json String converted from the oldDR
     */
    public void equalityAsNumberDataRowTest(DataRow oldDR, DataRow newDR, String json) {
        BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
        BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
        assertTrue(fieldNamesOld.size() == fieldNamesNew.size(), json);
        for (int i = 0; i < fieldNamesOld.size(); i++) {
            String fieldName= fieldNamesOld.get(i);
            assertEquals(
                    oldDR.getFieldAsNumber(fieldName),
                    newDR.getFieldAsNumber(fieldName),
                    () -> json + "\n" + fieldName + " values differ with function getFieldAsNumber");
        }
    }

    /**
     * This method takes two DataRows and compares their values with the
     * getFieldValue method from DataRow to evaluate the process of toJson/fromJson
     *
     * @param oldDR  The initial DataRow before the conversion with toJson/fromJson
     * @param newDR  The converted DataRow after the conversion with toJson/fromJson
     * @param json   The Json String converted from the oldDR
     * @param nested There are nested DataRows in the oldDR and newDR
     */
    public void equalityAsValueDataRowTest(DataRow oldDR, DataRow newDR, String json, boolean nested) {
        BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
        BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
        assertEquals(fieldNamesOld.size() ,fieldNamesNew.size(), json);
        if (!nested) {
            for (int i = 0; i < fieldNamesOld.size(); i++) {
                final String fieldName= fieldNamesOld.get(i);
                assertEquals(oldDR.getFieldValue(fieldName),
                        newDR.getFieldValue(fieldNamesOld.get(i)),
                        ()->json + "\n" + fieldName + " values differ with function getFieldValue");
            }
        } else {
            for (int i = 0; i < fieldNamesOld.size(); i++) {
                equalityAsValueDataRowTest((DataRow) oldDR.getFieldValue(fieldNamesOld.get(i)),
                        (DataRow) newDR.getFieldValue(fieldNamesOld.get(i)), json, false);
            }
        }
    }

    /**
     * This method takes two DataRows and compares their attributes with the getField and getAttributes methods from DataRow to evaluate the process of toJson/fromJson
     *
     * @param oldDR The initial DataRow before the conversion with toJson/fromJson
     * @param newDR The converted DataRow after the conversion with toJson/fromJson
     * @param json  The Json String converted from the oldDR
     */
    public void equalityAttributesDataRowTest(DataRow oldDR, DataRow newDR, String json) {
        BBArrayList<String> fieldNamesOld = oldDR.getFieldNames();
        BBArrayList<String> fieldNamesNew = newDR.getFieldNames();
        assertTrue(fieldNamesOld.size() == fieldNamesNew.size(), json);
        for (int i = 0; i < fieldNamesOld.size(); i++) {
            assertEquals(oldDR.getField(fieldNamesOld.get(i)).getAttributes(),
                    newDR.getField(fieldNamesOld.get(i)).getAttributes(),
                    json + "\n" + fieldNamesOld.get(i) + " values differ with function getAttributes");
        }
    }

    /**
     * A default result set is created and all values are set to null
     * The equality methods will check them to contain null before and after the conversion
     *
     * @throws Exception
     */
    @Test
    public void toJSonFieldAsStringNullTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSet(true);
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsNumberDataRowTest(dr0, newDr0, s);
        equalityAsValueDataRowTest(dr0, newDr0, s, false);
    }

    /**
     * A string-only result set is created
     * Every string is checked to have the right behavior
     *
     * @throws Exception
     */
    @Test
    public void toJSonStringTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createStringOnlyResultSet();
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);

        // Checking the values of the converted ResultSet

        equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsValueDataRowTest(dr0, newDr0, s, false);

        // Testing Strings with getFieldAsNumber
        // Strings containing only numbers (or null) can be converted, otherwise there will be a NumberFormatException

        assertEquals( dr0.getFieldAsNumber(DataRowProvider.STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.STRINGFIELD),s + " STRINGFIELD values differ");
        assertEquals( dr0.getFieldAsNumber(DataRowProvider.FRT_STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.FRT_STRINGFIELD),s + " FRT_STRINGFIELD values differ");
        assertEquals( dr0.getFieldAsNumber(DataRowProvider.FTH_STRINGFIELD), newDr0.getFieldAsNumber(DataRowProvider.FTH_STRINGFIELD),s + " FTH_STRINGField values difer");

        //So kann man das mit JUnit 5 schreiben
        assertThrows(NumberFormatException.class, ()->dr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD));

        try {
            newDr0.getFieldAsNumber(DataRowProvider.SCD_STRINGFIELD);
            fail("This String cannot be converted into a number!");
        } catch (NumberFormatException e) {
            // Caught correct exception
        }
        try {
            dr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
            fail("This String cannot be converted into a number!");
        } catch (NumberFormatException e) {
            // Caught correct exception
        }
        try {
            newDr0.getFieldAsNumber(DataRowProvider.TRD_STRINGFIELD);
            fail("This String cannot be converted into a number!");
        } catch (NumberFormatException e) {
            // Caught correct exception
        }
    }

    /**
     * A default result set is created
     * The equality method will check them to contain the same attributes as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonAttributesTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);

        // Checking the values of the converted ResultSet
        // The initial ResultSet must have DataRows with DataFields with the attribute "ColumnType", otherwise the fromJSon method will add it

        equalityAttributesDataRowTest(dr0, newDr0, s);
    }

    /**
     * A default result set is created, containing 3 DataRows
     * The equality method will check them to contain the same attributes as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonMultipleDataRowAttributesTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
        DataRow dr0 = rs0.get(0);
        DataRow dr1 = rs0.get(1);
        DataRow dr2 = rs0.get(2);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);
        DataRow newDr1 = newRs0.get(1);
        DataRow newDr2 = newRs0.get(2);

        // Checking the values of the converted ResultSet
        // The initial ResultSet must have DataRows with DataFields with the attribute "ColumnType", otherwise the fromJSon will add it

        equalityAttributesDataRowTest(dr0, newDr0, s);
        equalityAttributesDataRowTest(dr1, newDr1, s);
        equalityAttributesDataRowTest(dr2, newDr2, s);
    }

    /**
     * A default ResultSet is created
     * The equality method will check them to contain the same values (with getFieldAsString) as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonFieldAsStringTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        equalityAsStringDataRowTest(dr0, newDr0, s);
    }

    /**
     * A MinMaxResultSet is created, containing extreme values for the common data types
     * The equality method will check them to contain the same values (with getFieldAsString, getFieldAsNumber, getFieldValue)
     * and attributes as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonFieldAsStringTestMinMax() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSetMinMax();
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsNumberDataRowTest(dr0, newDr0, s);
        equalityAsValueDataRowTest(dr0, newDr0, s, false);
        equalityAttributesDataRowTest(dr0, newDr0, s);
    }

    /**
     * A default ResultSet is created
     * The equality method will check them to contain the same values (with getFieldAsNumber) as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonFieldAsNumberTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet rs1 = ResultSet.fromJson(s);
        DataRow newDr0 = rs1.get(0);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        equalityAsNumberDataRowTest(dr0, newDr0, s);
    }

    /**
     * A default ResultSet is created
     * The equality method will check them to contain the same values (with getFieldValue) as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonFieldValueTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createDefaultResultSet(false);
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet rs1 = ResultSet.fromJson(s);
        DataRow newDr0 = rs1.get(0);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        // Float will be converted to Double

        // The date has to be rounded to the first milliseconds of the day
        // Reason: In conversion the hours, minutes, seconds and milliseconds are dropped

        equalityAsValueDataRowTest(dr0, newDr0, s, false);
    }

    /**
     * A default ResultSet is created, containing 3 DataRows
     * The equality method will check them to contain the same values (with getFieldValue) as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonmultipleDataRowAsValueTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createMultipleDataRowResultSet();
        DataRow dr0 = rs0.get(0);
        DataRow dr1 = rs0.get(1);
        DataRow dr2 = rs0.get(2);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet newRs0 = ResultSet.fromJson(s);
        DataRow newDr0 = newRs0.get(0);
        DataRow newDr1 = newRs0.get(1);
        DataRow newDr2 = newRs0.get(2);

        // Checking the values of the converted ResultSet
        // The conversion of "Time" and other types are not implemented yet

        // Float will be converted to Double

        // The date has to be rounded to the first milliseconds of the day
        // Reason: In conversion the hours, minutes, seconds and milliseconds are dropped

        equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsNumberDataRowTest(dr0, newDr0, s);
        equalityAsValueDataRowTest(dr0, newDr0, s, false);

        equalityAsStringDataRowTest(dr1, newDr1, s);
        equalityAsNumberDataRowTest(dr1, newDr1, s);
        equalityAsValueDataRowTest(dr1, newDr1, s, false);

        equalityAsStringDataRowTest(dr2, newDr2, s);
        equalityAsNumberDataRowTest(dr2, newDr2, s);
        equalityAsValueDataRowTest(dr2, newDr2, s, false);
    }

    /**
     * A ResultSet with nested DataRows is created. The equality method will check
     * them to contain the same values (with getFieldValue, getFieldAsString,
     * getFieldAsNumber) as before
     *
     * @throws Exception
     */
    @Test
    public void toJSonNestedDataRowWithDataRowsTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createNestedDataRowsResultSet();
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet rs1 = ResultSet.fromJson(s);
        DataRow newDr0 = rs1.get(0);

        equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsNumberDataRowTest(dr0, newDr0, s);
        equalityAsValueDataRowTest(dr0, newDr0, s, true);
    }

    /**
     * A ResultSet with nested ResultSets is created. The nested ResultSets are
     * extracted and their DataRows are checked through the equality methods
     *
     * @throws Exception
     */
    @Test
    public void toJSonNestedDataRowWithResultSetsTest() throws Exception {
        ResultSet rs0 = ResultSetProvider.createNestedResultSetsResultSet();
        DataRow dr0 = rs0.get(0);
        String s = rs0.toJson();

        assertFalse(s.isEmpty());

        ResultSet rs1 = ResultSet.fromJson(s);
        DataRow newDr0 = rs1.get(0);

//	 	Converting the nested DataRow with ResutSets to String might have problems 
//		equalityAsStringDataRowTest(dr0, newDr0, s);
        equalityAsNumberDataRowTest(dr0, newDr0, s);
        BBArrayList<String> fieldNames = dr0.getFieldNames();
        for (int i = 0; i < fieldNames.size(); i++) {
            equalityAsValueDataRowTest(((ResultSet) dr0.getFieldValue(fieldNames.get(i))).get(0),
                    ((ResultSet) newDr0.getFieldValue(fieldNames.get(i))).get(0), s, false);
        }
    }

}
