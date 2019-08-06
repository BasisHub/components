package com.basiscomponents.db.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public class ListImporterTest {

	private ArrayList<Object> myList = new ArrayList<Object>();
	private DataRow attrRecord = new DataRow();
	private ResultSet result;

	/**
	 * A basic test for the ListImporter. Every number of the list will be inserted
	 * in one DataRow.
	 * 
	 * @throws Exception
	 */
	@Test
	public void listImporterSimpleTest() throws Exception {

		// adding data to the list and setting up the structure of the DataRows of the
		// new ResultSet
		myList.add(5);
		myList.add(6);
		myList.add(7);
		DataRow attrRecord = new DataRow();
		attrRecord.setFieldValue("FieldName", "FieldValue");
		result = ListImporter.ResultSetFromVector(myList, attrRecord);

		// checking the data of the ResultSet
		assertEquals("5", result.get(0).getFieldValue("FieldName"));
		assertEquals("6", result.get(1).getFieldValue("FieldName"));
		assertEquals("7", result.get(2).getFieldValue("FieldName"));

		attrRecord.clear();
		myList.clear();
	}

	@Test
	public void listImporterMoreComplexPatternTest() throws Exception {

		// adding data to the list and setting up the structure of the DataRows of the
		// new ResultSet
		myList.add(1);
		myList.add(10);
		myList.add(2);
		myList.add(20);
		myList.add(3);
		myList.add(30);
		DataRow attrRecord = new DataRow();
		attrRecord.setFieldValue("FirstNumber", "FirstValue");
		attrRecord.setFieldValue("SecondNumber", "SecondValue");
		result = ListImporter.ResultSetFromVector(myList, attrRecord);

		// checking the data of the ResultSet
		assertEquals("1", result.get(0).getFieldValue("FirstNumber"));
		assertEquals("10", result.get(0).getFieldValue("SecondNumber"));
		assertEquals("2", result.get(1).getFieldValue("FirstNumber"));
		assertEquals("20", result.get(1).getFieldValue("SecondNumber"));
		assertEquals("3", result.get(2).getFieldValue("FirstNumber"));
		assertEquals("30", result.get(2).getFieldValue("SecondNumber"));

		attrRecord.clear();
		myList.clear();
	}

	/**
	 * All content of the list will  be imported to one DataRow.
	 * 
	 * @throws Exception
	 */
	@Test
	public void listImporterOneDataRowTest() throws Exception {

		// adding data to the list and setting up the structure of the DataRows of the
		// new ResultSet
		myList.add(5);
		myList.add(6);
		myList.add(7);
		myList.add(8);
		myList.add(9);
		DataRow attrRecord = new DataRow();
		attrRecord.setFieldValue("FieldName", "FieldValue");
		attrRecord.setFieldValue("SampleName", "SampleValue");
		attrRecord.setFieldValue("TestName", "TestValue");
		attrRecord.setFieldValue("SomeName", "SomeValue");
		attrRecord.setFieldValue("AnotherName", "AnotherValue");
		result = ListImporter.ResultSetFromVector(myList, attrRecord);

		// checking the data of the ResultSet
		assertEquals("5", result.get(0).getFieldValue("FieldName"));
		assertEquals("6", result.get(0).getFieldValue("SampleName"));
		assertEquals("7", result.get(0).getFieldValue("TestName"));
		assertEquals("8", result.get(0).getFieldValue("SomeName"));
		assertEquals("9", result.get(0).getFieldValue("AnotherName"));

		attrRecord.clear();
		myList.clear();
	}

}
