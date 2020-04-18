package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

class ConfigurationsBCTest {
	
	ConfigurationsBC bc;
	
	@BeforeEach
	void setUp() throws Exception {
		initBC();
	}

	private void initBC() {
//		if (this.bc != null) {
//			return;
//		}
		this.bc = new ConfigurationsBC();
		ClassLoader classLoader = getClass().getClassLoader();
		
        URL resource = classLoader.getResource("testconfigs");
		this.bc.setConfigFile(resource.getFile());
		this.bc.setUserName(this.bc.getAdminUser());
	}
	
	@Test
	void testConfigurationsBC() {
		assertNotNull(bc);
	}

	@Test
	void testSetConfigFile() {
		assertNotNull(bc.getConfigFile());
	}
	


	@Test
	void testGetAttributesRecord() {
		DataRow attrRec = bc.getAttributesRecord();
		assertNotNull(attrRec);
		assertFalse(attrRec.isEmpty());
	}


	@Test
	void testSetFieldSelectionDataRow() throws Exception {
		DataRow fieldSelection = new DataRow();
		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "");
		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_CREA_USER, "");
		bc.setFieldSelection(fieldSelection);
		DataRow fieldSel = bc.getFieldSelection();
		assertNotNull(fieldSel);
		assertTrue(fieldSel.getColumnCount() == 3);
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CONFIG));
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_USERID));
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CREA_USER));
		//check if field selection was applied right on retrieve
		ResultSet retrieved = bc.retrieve();
		//column count must match
		assertEquals(retrieved.getColumnCount(), fieldSel.getColumnCount());
		ArrayList<String> columnNamesRetrieved = retrieved.getColumnNames();
		//column names must be contained
		for (String columnName : columnNamesRetrieved) {
			assertTrue(fieldSelection.contains(columnName));
		}
	}
	
	@Test
	void testSetFieldSelectionCollectionOfString() throws Exception {
		ArrayList<String> al = new ArrayList<String>();
		al.add(ConfigurationsBC.FIELD_NAME_CONFIG);
		al.add(ConfigurationsBC.FIELD_NAME_USERID);
		al.add(ConfigurationsBC.FIELD_NAME_CREA_USER);
		bc.setFieldSelection(al);
		DataRow fieldSel = bc.getFieldSelection();
		assertNotNull(fieldSel);
		assertTrue(fieldSel.getColumnCount() == 3);
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CONFIG));
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_USERID));
		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CREA_USER));
		ResultSet retrieved = bc.retrieve();
		//column count must match
		assertEquals(retrieved.getColumnCount(), fieldSel.getColumnCount());
		ArrayList<String> columnNamesRetrieved = retrieved.getColumnNames();
		//column names must be contained
		for (String columnName : columnNamesRetrieved) {
			assertTrue(fieldSel.contains(columnName));
		}
	}

	@Test
	void testSetScope() throws Exception {
		bc.setScope("B");
		assertNotNull(bc.getScope());
		DataRow scope = bc.getScope();
		assertFalse(scope.isEmpty());
		ResultSet retrieved = bc.retrieve();
		assertTrue(scope.getColumnCount() == retrieved.getColumnCount(), "scope not applied correctly. retrieval has " + retrieved.getColumnCount() + " columns but should have " + scope.getColumnCount());
		for (String columnName : retrieved.getColumnNames()) {
			assertTrue(scope.contains(columnName),"field " + columnName + " not in scope B");
		}
	}

	@Test
	void testRetrieve() throws Exception {
		ResultSet rs = bc.retrieve();
		assertNotNull(rs);
		assertFalse(rs.isEmpty());
	}
	
	@Test
	void testRetrieveIntInt() throws Exception {
		ResultSet rs = bc.retrieve(1,2);
		assertNotNull(rs);
		assertFalse(rs.isEmpty());
	}
	
	@Test
	void testValidateWrite() throws Exception {
		DataRow writeDr = new DataRow();
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
		ResultSet rs = bc.validateWrite(writeDr);
		assertNotNull(rs);
		assertTrue(rs.isEmpty());//must be empty because no errors are in the dr
		writeDr = new DataRow();
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
		rs = bc.validateWrite(writeDr);
		assertNotNull(rs);
		assertFalse(rs.isEmpty());//must contain error because 2 Key fields are missing
	}

	@Test
	void testWrite() throws Exception {
		DataRow writeDr = new DataRow();
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
		DataRow dr = bc.write(writeDr);
		assertNotNull(dr);
		assertFalse(dr.isEmpty());
		//test if row has been written
		bc.setFilter(writeDr);
		ResultSet rs = bc.retrieve();
		assertTrue(rs.size() == 1, "Write did not work for data row " + writeDr.toJson());
	}

	@Test
	void testValidateRemove() throws Exception {
		DataRow dr = new DataRow();
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
		bc.write(dr);
		ResultSet validation = bc.validateRemove(dr);
		assertNotNull(validation);
		assertTrue(validation.isEmpty(), "validateRemove should have been successful for data row: " + dr.toJson(false));
		
		dr = new DataRow();
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
		validation = bc.validateRemove(dr);
		assertNotNull(validation);
		assertFalse(validation.isEmpty(), "validateRemove should NOT have been successful (missing SETTING key) for data row: " + dr.toJson(false));
		
	}

	@Test
	void testRemove() throws Exception {
		DataRow dr = new DataRow();
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"quite an unpopular view");
		bc.write(dr);
		bc.remove(dr);
		//test if remove has actually deleted the entry.
		bc.setFilter(dr);
		ResultSet retrieval = bc.retrieve();
		assertEquals(retrieval.size(), 0, "removal failed. data row is still in the storage.");
		
//		Assertions.assertThrows(com.basis.filesystem.FilesystemKeyException.class, () ->{
//			bc.remove(dr);
//		});
		
		try {
			bc.remove(dr);//second removal should fail
		} catch (Exception e) {
			assertNotNull(e.getCause());
			assertTrue(e.getCause() instanceof com.basis.filesystem.FilesystemKeyException);
			return;
		}
		fail("Failed to catch FilesystemKeyException");
	}

	@Test
	void testGetNewObjectTemplate() throws ParseException {
		DataRow dr = new DataRow();
		DataRow objectTemplate;
		DataRow attributesRecord = bc.getAttributesRecord();
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"quite an unpopular view");
		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG,"test");
		objectTemplate = bc.getNewObjectTemplate(dr);
		assertNotNull(objectTemplate);
		assertTrue(objectTemplate.getColumnCount() == attributesRecord.getColumnCount(), "template column count does not match attributes record column count.");
		BBArrayList<String> fieldNamesAttributesRecord = attributesRecord.getFieldNames();
		//check if all fields from the attributesrecord are included and are valid in the template
		for (String fieldName : fieldNamesAttributesRecord) {
			assertTrue(objectTemplate.contains(fieldName));
			HashMap<String, String> attributes = attributesRecord.getFieldAttributes(fieldName);
			HashMap<String, String> templateAttributes = objectTemplate.getFieldAttributes(fieldName);
			
			Set<String> keys = attributes.keySet();
			//check if key is contained and key value is also valid
			for (String key : keys) {
				assertTrue(templateAttributes.containsKey(key), "Template does not have field '" + key + "' from Attributes record.");
				String attributeValue 		  = attributes.get(key);
				String templateAttributeValue = templateAttributes.get(key);
				assertTrue(templateAttributeValue.contentEquals(attributeValue), "Attribute '" + key + "' in template differs from attribute record. Should have been '" + attributeValue + "' but is actually '" + templateAttributeValue + "'.");
			}
		}
	}

	@Test
	void testGetEffectiveConfiguration() throws Exception {
		// default userID and default setting
		DataRow defaultAll 			= new DataRow();
		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "default all");
		
		// set userid and default setting
		DataRow defaultSetting 		= new DataRow();
		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "default setting");
		
		// set userid and set setting
		DataRow customizedSetting	= new DataRow();
		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "customized setting");
		//define test cases
		DataRow test1 				= new DataRow();
		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
		
		DataRow test2 				= new DataRow();
		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"weird setting");
		
		DataRow test3 				= new DataRow();
		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "XX");
		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"weird setting");
		//removing the test configs
		try {
			bc.remove(test1);
		} catch (Exception e) {
		}
		try {
			bc.remove(test2);
		} catch (Exception e) {
		}
		try {
			bc.remove(test3);
		} catch (Exception e) {
		}
		//writing the cases
		bc.write(defaultAll);
		bc.write(defaultSetting);
		bc.write(customizedSetting);
		
		ResultSet effectiveConfiguration;
		DataRow configRow;
		
		//effective configuration for test case 1 must contain the customized setting
		bc.setFilter(test1);
		effectiveConfiguration = bc.getEffectiveConfiguration();
		assertNotNull(effectiveConfiguration);
		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
		configRow = effectiveConfiguration.get(0);
		assertNotNull(configRow);
		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, customizedSetting), "effective configuration returned the wrong data for test1: " + test1.toJson(false));
		
		//effective configuration for test case 2 must contain the default setting, because the customized setting is not applicable to this test case 
		//(setting name differs).
		bc.setFilter(test2);
		effectiveConfiguration = bc.getEffectiveConfiguration();
		assertNotNull(effectiveConfiguration);
		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
		configRow = effectiveConfiguration.get(0);
		assertNotNull(configRow);
		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, defaultSetting), "effective configuration returned the wrong data for test2: " + test2.toJson(false));
		
		//effective configuration for test case 3 must contain the default all, because neither the customized setting nor the default setting is not applicable to this test case.
		//(setting name and userid differs).
		bc.setFilter(test3);
		effectiveConfiguration = bc.getEffectiveConfiguration();
		assertNotNull(effectiveConfiguration);
		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
		configRow = effectiveConfiguration.get(0);
		assertNotNull(configRow);
		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, defaultAll), "effective configuration returned the wrong data for test3: " + test3.toJson(false));
	}
	
	@Test
	void testGetAvailableConfigurations() throws Exception {
		//write test configs
		DataRow test1 = getDummyDR("DBO", "Test", "AA", "my setting 1", 	null);
		DataRow test2 = getDummyDR("DBO", "Test", "AA", "my setting 2", 	null);
		DataRow test3 = getDummyDR("DBO", "Test", "XX", "weird setting", 	null);
		DataRow test4 = getDummyDR("DBO", "Test", "AA", "",					null);
		DataRow test5 = getDummyDR("DBO", "Test",  "",  "",					null);
		DataRow test6 = getDummyDR("DBO", "Test",  "",  "default setting 1",null);
		bc.write(test1);
		bc.write(test2);
		bc.write(test3);
		bc.write(test4);
		bc.write(test5);
		bc.write(test6);
		bc.setFilter(test1);
		ResultSet availlableConfigurations = bc.getAvaillableConfigurations();
		
		// Testing if the write was successful 
		assertNotNull(availlableConfigurations);
		// must have at least the 5 right configurations that were added above (might contain more from other test methods)
		assertTrue(availlableConfigurations.size() >= 5);
		// these must be included because they are user related configuration
		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test1), "DataRow missing in availlable config: " + test1.toJson(false));
		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test2), "DataRow missing in availlable config: " + test2.toJson(false));
		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test4), "DataRow missing in availlable config: " + test4.toJson(false));
		// these must be included because they are default configuration
		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test5), "DataRow missing in availlable config: " + test5.toJson(false));
		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test6), "DataRow missing in availlable config: " + test6.toJson(false));
		
		// The expected response is false, because the record to write has a different user name which should not be allowed
		assertFalse(checkIfRsContainsKeys(availlableConfigurations, test3), "Wrong DataRow included in availlable config: " + test3.toJson(false));//must not be in it; wrong user
	}
	
	/**
	 * checks if a datarow is in a resultset only considering the config file keys(REALM, KEYX, USERID, SETTING) and ignoring other fields
	 * @param rs
	 * @param keys
	 * @return
	 */
	private boolean checkIfRsContainsKeys(ResultSet rs, DataRow keys) {
		if (rs.isEmpty()) {
			return false;
		}
		try {
			String realm   = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_REALM).trim();
			String key     = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_KEYX).trim();
			String userid  = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID).trim();
			String setting = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_SETTING).trim();
			for (DataRow dataRow : rs) {
				String currentRealm   = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_REALM).trim();
				String currentKey     = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_KEYX).trim();
				String currentUserid  = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID).trim();
				String currentSetting = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_SETTING).trim();
				if (
					realm	.contentEquals(currentRealm) 	&&
					key		.contentEquals(currentKey) 		&&
					userid	.contentEquals(currentUserid) 	&&
					setting	.contentEquals(currentSetting))
				{
					return true;
				}
			}
		}catch (Exception e) {
		}
		return false;
	}
	
	/**
	 * returns a new configuration data row with the 4 keyfields and the configuration field.
	 * Does not modify the config file.
	 * @param realm
	 * @param key
	 * @param userID
	 * @param setting
	 * @param config
	 * @return
	 * @throws ParseException
	 */
	private DataRow getDummyDR(String realm, String key, String userID, String setting, String config) throws ParseException {
		DataRow dummy = new DataRow();
		if (realm != null) {
			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  realm);
		}
		if (key != null) {
			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   key);
		}
		if (userID != null) {
			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, userID);
		}
		if (setting != null) {
			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,setting);
		}
		if (config != null) {
			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG,config);
		}
		dummy = bc.fillWithSpacesToMatchMinLength(dummy);
		return dummy;
	}
	

//	@Test
//	void testGetFilter() {
//		fail("Not yet implemented");
//	}

//	@Test
//	void testGetFieldSelection() {
//		fail("Not yet implemented");
//	}

}
