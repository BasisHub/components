package com.basiscomponents.db.importer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.sql.Timestamp;
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
	 * Some data is inserted into a list. The attributesRecord is set up and the
	 * data is imported from the list with a special pattern.
	 * 
	 * @throws Exception
	 */
	@Test
	public void listImporterMoreComplexPatternWithManyTypesTest() throws Exception {

		// adding data to the list and setting up the structure of the DataRows of the
		// new ResultSet

		myList.add("Alfred");
		myList.add(true);
		myList.add(42);
		myList.add(5000.0);
		myList.add("Chief");
		myList.add(new Date(Long.valueOf("534654653427")));
		myList.add(new Timestamp(Long.valueOf("879674634")));

		myList.add("Jasper");
		myList.add(true);
		myList.add(39);
		myList.add(3000.0);
		myList.add("Worker1");
		myList.add(new Date(Long.valueOf("534654653427")));
		myList.add(new Timestamp(Long.valueOf("879674634")));

		myList.add("Elizabeth");
		myList.add(false);
		myList.add(56);
		myList.add(2500.0);
		myList.add("Worker2");
		myList.add(new Date(Long.valueOf("534654653427")));
		myList.add(new Timestamp(Long.valueOf("879674634")));

		myList.add("Jimmy");
		myList.add(true);
		myList.add(39);
		myList.add(1500.0);
		myList.add("Caretaker");
		myList.add(new Date(Long.valueOf("534654653427")));
		myList.add(new Timestamp(Long.valueOf("879674634")));

		myList.add("Pieter");
		myList.add(true);
		myList.add(21);
		myList.add(900.0);
		myList.add("Working Student");
		myList.add(new Date(Long.valueOf("534654653427")));
		myList.add(new Timestamp(Long.valueOf("879674634")));

		attrRecord.setFieldValue("Name", "");
		attrRecord.setFieldValue("isMale", "");
		attrRecord.setFieldValue("Age", "");
		attrRecord.setFieldValue("Salary", "");
		attrRecord.setFieldValue("Employment", "");
		attrRecord.setFieldValue("Begin", "");
		attrRecord.setFieldValue("End", "");
		result = ListImporter.ResultSetFromVector(myList, attrRecord);

		// checking the data of the ResultSet
		assertEquals("Alfred", result.get(0).getFieldValue("Name"));
		assertEquals("1", result.get(0).getFieldValue("isMale"));
		assertEquals("42", result.get(0).getFieldValue("Age"));
		assertEquals("5000.0", result.get(0).getFieldValue("Salary"));
		assertEquals("Chief", result.get(0).getFieldValue("Employment"));
		assertEquals("1986-12-11", result.get(0).getFieldValue("Begin"));
		assertEquals("1970-01-11 05:21:14.634", result.get(0).getFieldValue("End"));

		assertEquals("Jasper", result.get(1).getFieldValue("Name"));
		assertEquals("1", result.get(1).getFieldValue("isMale"));
		assertEquals("39", result.get(1).getFieldValue("Age"));
		assertEquals("3000.0", result.get(1).getFieldValue("Salary"));
		assertEquals("Worker1", result.get(1).getFieldValue("Employment"));
		assertEquals("1986-12-11", result.get(1).getFieldValue("Begin"));
		assertEquals("1970-01-11 05:21:14.634", result.get(1).getFieldValue("End"));

		assertEquals("Elizabeth", result.get(2).getFieldValue("Name"));
		assertEquals("0", result.get(2).getFieldValue("isMale"));
		assertEquals("56", result.get(2).getFieldValue("Age"));
		assertEquals("2500.0", result.get(2).getFieldValue("Salary"));
		assertEquals("Worker2", result.get(2).getFieldValue("Employment"));
		assertEquals("1986-12-11", result.get(2).getFieldValue("Begin"));
		assertEquals("1970-01-11 05:21:14.634", result.get(2).getFieldValue("End"));

		assertEquals("Jimmy", result.get(3).getFieldValue("Name"));
		assertEquals("1", result.get(3).getFieldValue("isMale"));
		assertEquals("39", result.get(3).getFieldValue("Age"));
		assertEquals("1500.0", result.get(3).getFieldValue("Salary"));
		assertEquals("Caretaker", result.get(3).getFieldValue("Employment"));
		assertEquals("1986-12-11", result.get(3).getFieldValue("Begin"));
		assertEquals("1970-01-11 05:21:14.634", result.get(3).getFieldValue("End"));

		assertEquals("Pieter", result.get(4).getFieldValue("Name"));
		assertEquals("1", result.get(4).getFieldValue("isMale"));
		assertEquals("21", result.get(4).getFieldValue("Age"));
		assertEquals("900.0", result.get(4).getFieldValue("Salary"));
		assertEquals("Working Student", result.get(4).getFieldValue("Employment"));
		assertEquals("1986-12-11", result.get(4).getFieldValue("Begin"));
		assertEquals("1970-01-11 05:21:14.634", result.get(4).getFieldValue("End"));

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

	/**
	 * The attributes which are written in the attributesRecord are present in the
	 * resulting ResultSet.
	 * 
	 * @throws Exception
	 */
	@Test
	public void listImporterAttributesTest() throws Exception {

		// adding data to the list and setting up the structure of the DataRows of the
		// new ResultSet
		myList.add(5);
		myList.add(6);
		myList.add(7);
		myList.add(8);
		myList.add(9);
		myList.add(10);
		attrRecord.setFieldValue("FieldName", "FieldValue");
		attrRecord.setFieldAttribute("FieldName", "attribute1", "value1");
		attrRecord.setFieldValue("SampleName", "SampleValue");
		attrRecord.setFieldAttribute("SampleName", "attribute2", "value2");
		attrRecord.setFieldValue("TestName", "TestValue");
		attrRecord.setFieldAttribute("TestName", "attribute3", "value3");
		result = ListImporter.ResultSetFromVector(myList, attrRecord);

		// checking the data of the ResultSet
		assertEquals("5", result.get(0).getFieldValue("FieldName"));
		assertEquals("value1", result.get(0).getFieldAttribute("FieldName", "attribute1"));
		assertEquals("6", result.get(0).getFieldValue("SampleName"));
		assertEquals("value2", result.get(0).getFieldAttribute("SampleName", "attribute2"));
		assertEquals("7", result.get(0).getFieldValue("TestName"));
		assertEquals("value3", result.get(0).getFieldAttribute("TestName", "attribute3"));

		assertEquals("8", result.get(1).getFieldValue("FieldName"));
		assertEquals("value1", result.get(1).getFieldAttribute("FieldName", "attribute1"));
		assertEquals("9", result.get(1).getFieldValue("SampleName"));
		assertEquals("value2", result.get(1).getFieldAttribute("SampleName", "attribute2"));
		assertEquals("10", result.get(1).getFieldValue("TestName"));
		assertEquals("value3", result.get(1).getFieldAttribute("TestName", "attribute3"));

		attrRecord.clear();
		myList.clear();
	}

}
