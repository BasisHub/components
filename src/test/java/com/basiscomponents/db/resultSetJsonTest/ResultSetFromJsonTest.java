package com.basiscomponents.db.resultSetJsonTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.ResultSet;
import com.google.gson.JsonSyntaxException;

public class ResultSetFromJsonTest {


    /**
     * Checking the column names and DataField values after fromJson
     *
     * @throws Exception
     */
    @Test
    public void getColumnNamesAndValuesFromJsonTest() throws Exception {
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.getColumnCount() == 3);
        assertTrue(rs.getColumnNames().contains("ISO639-1"));
        assertTrue(rs.getColumnNames().contains("LANGUAGE"));
        assertTrue(rs.getColumnNames().contains(""));

		assertEquals("ENG", rs.get(0).getFieldValue("LANGUAGE"));
        assertEquals("en", rs.get(0).getFieldValue("ISO639-1"));
		assertEquals("en", rs.get(0).getFieldValue(""));
    }

    /**
     * Checking the attributes and their values of the DataFields
     * An empty string should be returned if the attribute doesn't exists
     *
     * @throws Exception
     */
    @Test
    public void getAttributeFromJsonTest() throws Exception {
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().size() == 1);
        assertTrue(rs.get(0).getDataField("ISO639-1").getAttributes().size() == 1);

        assertEquals("12", rs.getAttribute("LANGUAGE", "ColumnType"));
        assertEquals("12", rs.getAttribute("ISO639-1", "ColumnType"));
        assertEquals("", rs.getAttribute("LANGUAGE", "NotAnAttribute"));
        assertEquals("", rs.getAttribute("ISO639-1", "NotAnAttribute"));
    }

    /**
     * The spelling mistake in the MetaData for LANGUAGE is detected and the attribute is dropped
     * After the conversion toJson and fromJson the MetaData was corrected
     *
     * @throws Exception
     */
    @Test
    public void wrongMetaDataCutOutFromJsonTest() throws Exception {
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"ANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains("ANGUAGE"));

        String s = rs.toJson();
        rs = ResultSet.fromJson(s);

        assertNotNull(rs);
        assertFalse(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains("ANGUAGE"));
    }

    /**
     * The spelling mistake in the MetaData for LANGUAGE is detected and the attribute is dropped
     * After the conversion toJson and fromJson the MetaData was corrected
     *
     * @throws Exception
     */
    @Test
    public void wrongMetaDataCutOutFromJsonTest2() throws Exception {
        // The E in MetaData is corrected to LANGUAGE after FromJson + ToJson
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"E\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains("E"));

        String s = rs.toJson();
        rs = ResultSet.fromJson(s);

        assertNotNull(rs);
        assertFalse(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains("E"));
    }

    /**
     * The spelling mistake in the MetaData for LANGUAGE is detected and the attribute is dropped
     * After the conversion toJson and fromJson the MetaData was corrected
     *
     * @throws Exception
     */
    @Test
    public void wrongMetaDataCutOutFromJsonTest3() throws Exception {
        // The "" in MetaData is corrected to LANGUAGE after FromJson + ToJson
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains(""));

        String s = rs.toJson();
        rs = ResultSet.fromJson(s);

        assertNotNull(rs);
        assertFalse(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());
        assertFalse(rs.getColumnNames().contains(""));
    }

    /**
     * In the test situation every DataField name in the MetaData is missing, so the DataField get their attributes by order
     *
     * @throws Exception
     */
    @Test
    public void wrongMetaDataCutOutFromJsonTest4() throws Exception {
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());

        String s = rs.toJson();
        assertEquals(
                "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]",
                s);
    }

    /**
     * In the test situation every DataField name in the MetaData is missing, so the DataField get their attributes by order
     * There are more unnamed MetaData fields than DataFields, so the last one is dropped
     *
     * @throws Exception
     */
    @Test
    public void wrongMetaDataCutOutFromJsonTest5() throws Exception {
        // The JSonMapper will drop the last attribute field
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"},\"\":{\"ColumnType\":\"12\"}}}]";
        ResultSet rs = ResultSet.fromJson(response);

        assertNotNull(rs);
        assertTrue(rs.get(0).getDataField("LANGUAGE").getAttributes().isEmpty());

        String s = rs.toJson();
        assertEquals(
                "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]",
                s);
    }

    /**
     * There is a \" missing after LANGUAGE, which should lead to a JsonSyntaxException
     *
     * @throws Exception
     */
    @Test
    public void wrongFormatFromJsonTest() throws Exception {
        String response = "[{\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        assertThrows(JsonSyntaxException.class,()->
         ResultSet.fromJson(response));
    }

    /**
     * There is a { missing after LANGUAGE, which should lead to a JsonSyntaxException
     *
     * @throws Exception
     */
    @Test
    public void wrongFormatFromJsonTest2() throws Exception {
        String response = "[\"LANGUAGE:\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        assertThrows(JsonSyntaxException.class, () ->
                ResultSet.fromJson(response));
    }

    /**
     * There is a : missing after LANGUAGE, which should lead to a JsonSyntaxException
     *
     * @throws Exception
     */
    @Test
    public void wrongFormatFromJsonTest3() throws Exception {
        String response = "[{\"LANGUAGE\"\"ENG\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        assertThrows(JsonSyntaxException.class, () ->
                ResultSet.fromJson(response));
    }

    /**
     * LANGUAGE has no value field, which should lead to a JsonSyntaxException
     *
     * @throws Exception
     */
    @Test
    public void wrongFormatFromJsonTest4() throws Exception {
        String response = "[{\"LANGUAGE\",\"ISO639-1\":\"en\",\"meta\":{\"LANGUAGE\":{\"ColumnType\":\"12\"},\"ISO639-1\":{\"ColumnType\":\"12\"}}}]";
        assertThrows(JsonSyntaxException.class, () ->
                ResultSet.fromJson(response));
    }

    /**
     * The MetaData is missing, which is allowed and shouldn't cause any exceptions
     *
     * @throws Exception
     */
    @Test
    public void metaDataMissingFromJsonTest() throws Exception {
        String response = "[{\"LANGUAGE\":\"ENG\",\"ISO639-1\":\"en\"}]";
        ResultSet rs = ResultSet.fromJson(response);
    }

}
