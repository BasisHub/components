package com.basiscomponents.bc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
	
//	@BeforeEach
//	void setUp() throws Exception {
//		initBC();
//	}
//
//	private void initBC() {
////		if (this.bc != null) {
////			return;
////		}
//		this.bc = new ConfigurationsBC();
//		ClassLoader classLoader = getClass().getClassLoader();
//		
//        URL resource = classLoader.getResource("testconfigs");
//		this.bc.setConfigFile(resource.getFile());
//		this.bc.setUserName(this.bc.getAdminUser());
//	}
//	
//	@Test
//	void testConfigurationsBC() {
//		assertNotNull(bc);
//	}
//
//	@Test
//	void testSetConfigFile() {
//		assertNotNull(bc.getConfigFile());
//	}
//	
//
//
//	@Test
//	void testGetAttributesRecord() {
//		DataRow attrRec = bc.getAttributesRecord();
//		assertNotNull(attrRec);
//		assertFalse(attrRec.isEmpty());
//	}
//
//
//	@Test
//	void testSetFieldSelectionDataRow() throws ParseException {
//		DataRow fieldSelection = new DataRow();
//		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "");
//		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
//		fieldSelection.setFieldValue(ConfigurationsBC.FIELD_NAME_CREA_USER, "");
//		bc.setFieldSelection(fieldSelection);
//		DataRow fieldSel = bc.getFieldSelection();
//		assertNotNull(fieldSel);
//		assertTrue(fieldSel.getColumnCount() == 3);
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CONFIG));
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_USERID));
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CREA_USER));
//	}
//	
//	@Test
//	void testSetFieldSelectionCollectionOfString() {
//		ArrayList<String> al = new ArrayList<String>();
//		al.add(ConfigurationsBC.FIELD_NAME_CONFIG);
//		al.add(ConfigurationsBC.FIELD_NAME_USERID);
//		al.add(ConfigurationsBC.FIELD_NAME_CREA_USER);
//		bc.setFieldSelection(al);
//		DataRow fieldSel = bc.getFieldSelection();
//		assertNotNull(fieldSel);
//		assertTrue(fieldSel.getColumnCount() == 3);
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CONFIG));
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_USERID));
//		assertTrue(fieldSel.contains(ConfigurationsBC.FIELD_NAME_CREA_USER));
//	}
//
//	@Test
//	void testSetScope() {
//		bc.setScope("B");
//		assertNotNull(bc.getScope());
//		assertFalse(bc.getScope().isEmpty());
//	}
//
//	@Test
//	void testRetrieve() throws Exception {
//		ResultSet rs = bc.retrieve();
//		assertNotNull(rs);
//		assertFalse(rs.isEmpty());
//	}
//	
//	@Test
//	void testRetrieveIntInt() throws Exception {
//		ResultSet rs = bc.retrieve(1,2);
//		assertNotNull(rs);
//		assertFalse(rs.isEmpty());
//	}
//	
//	@Test
//	void testValidateWrite() throws Exception {
//		DataRow writeDr = new DataRow();
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
//		ResultSet rs = bc.validateWrite(writeDr);
//		assertNotNull(rs);
//		assertTrue(rs.isEmpty());//must be empty because no errors are in the dr
//		writeDr = new DataRow();
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
//		rs = bc.validateWrite(writeDr);
//		assertNotNull(rs);
//		assertFalse(rs.isEmpty());//must contain error because 2 Key fields are missing
//	}
//
//	@Test
//	void testWrite() throws Exception {
//		DataRow writeDr = new DataRow();
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
//		writeDr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
//		DataRow dr = bc.write(writeDr);
//		assertNotNull(dr);
//		assertFalse(dr.isEmpty());
//	}
//
//	@Test
//	void testValidateRemove() throws Exception {
//		DataRow dr = new DataRow();
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
//		bc.write(dr);
//		ResultSet validation = bc.validateRemove(dr);
//		assertNotNull(validation);
//		assertTrue(validation.isEmpty(), "validateRemove should have been successful for data row: " + dr.toJson(false));
//		
//		dr = new DataRow();
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
//		validation = bc.validateRemove(dr);
//		assertNotNull(validation);
//		assertFalse(validation.isEmpty(), "validateRemove should NOT have been successful (missing SETTING key) for data row: " + dr.toJson(false));
//		
//	}
//
//	@Test
//	void testRemove() throws Exception {
//		DataRow dr = new DataRow();
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"quite an unpopular view");
//		bc.write(dr);
//		bc.remove(dr);
//		boolean exceptionCatched = false;
//		try {
//			bc.remove(dr);//second removal should fail
//		} catch (Exception e) {
//			exceptionCatched = true;
//			assertNotNull(e.getCause());
//			assertTrue(e.getCause() instanceof com.basis.filesystem.FilesystemKeyException);
//		}
//		assertTrue(exceptionCatched, "");
//	}
//
//	@Test
//	void testGetNewObjectTemplate() throws ParseException {
//		DataRow dr = new DataRow();
//		DataRow objectTemplate;
//		DataRow attributesRecord = bc.getAttributesRecord();
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test1");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "JE");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"quite an unpopular view");
//		dr.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG,"test");
//		objectTemplate = bc.getNewObjectTemplate(dr);
//		assertNotNull(objectTemplate);
//		assertTrue(objectTemplate.getColumnCount() == attributesRecord.getColumnCount(), "template column count does not match attributes record column count.");
//		BBArrayList<String> fieldNamesAttributesRecord = attributesRecord.getFieldNames();
//		for (String fieldName : fieldNamesAttributesRecord) {
//			assertTrue(objectTemplate.contains(fieldName));
//			HashMap<String, String> attributes = attributesRecord.getFieldAttributes(fieldName);
//			HashMap<String, String> templateAttributes = objectTemplate.getFieldAttributes(fieldName);
//			
//			Set<String> keys = attributes.keySet();
//			for (String key : keys) {
//				assertTrue(templateAttributes.containsKey(key), "Template does not have field '" + key + "' from Attributes record.");
//				String attributeValue 		  = attributes.get(key);
//				String templateAttributeValue = templateAttributes.get(key);
//				assertTrue(templateAttributeValue.contentEquals(attributeValue), "Attribute '" + key + "' in template differs from attribute record. Should have been '" + attributeValue + "' but is actually '" + templateAttributeValue + "'.");
//			}
//		}
//	}
//
//	@Test
//	void testGetEffectiveConfiguration() throws Exception {
//		DataRow defaultAll 			= new DataRow();
//		DataRow defaultSetting 		= new DataRow();
//		DataRow customizedSetting	= new DataRow();
//		DataRow test1 				= new DataRow();
//		DataRow test2 				= new DataRow();
//		DataRow test3 				= new DataRow();
//		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
//		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
//		defaultAll.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "default all");
//		
//		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
//		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"");
//		defaultSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "default setting");
//		
//		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
//		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
//		customizedSetting.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG, "customized setting");
////		bc.write(writeDr);
//		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
//		test1.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"my setting");
//		
//		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "AA");
//		test2.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"weird setting");
//		
//		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  "DBO");
//		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   "Test");
//		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "XX");
//		test3.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,"weird setting");
//		try {
//			bc.remove(test1);
//		} catch (Exception e) {
//		}
//		try {
//			bc.remove(test2);
//		} catch (Exception e) {
//		}
//		try {
//			bc.remove(test3);
//		} catch (Exception e) {
//		}
//		bc.write(defaultAll);
//		bc.write(defaultSetting);
//		bc.write(customizedSetting);
//		
//		ResultSet effectiveConfiguration;
//		DataRow configRow;
//		
//		bc.setFilter(test1);
//		effectiveConfiguration = bc.getEffectiveConfiguration();
//		assertNotNull(effectiveConfiguration);
//		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
//		configRow = effectiveConfiguration.get(0);
//		assertNotNull(configRow);
//		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, customizedSetting), "effective configuration returned the wrong data!");
//		
//		bc.setFilter(test2);
//		effectiveConfiguration = bc.getEffectiveConfiguration();
//		assertNotNull(effectiveConfiguration);
//		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
//		configRow = effectiveConfiguration.get(0);
//		assertNotNull(configRow);
//		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, defaultSetting), "effective configuration returned the wrong data!");
//		
//		bc.setFilter(test3);
//		effectiveConfiguration = bc.getEffectiveConfiguration();
//		assertNotNull(effectiveConfiguration);
//		assertFalse(effectiveConfiguration.isEmpty(), "effective Configuration is empty");
//		configRow = effectiveConfiguration.get(0);
//		assertNotNull(configRow);
//		assertTrue(checkIfRsContainsKeys(effectiveConfiguration, defaultAll), "effective configuration returned the wrong data!");
//	}
//	
//	@Test
//	void testGetAvaillableConfigurations() throws Exception {
//		DataRow test1 = getDummyDR("DBO", "Test", "AA", "my setting 1", 	null);
//		DataRow test2 = getDummyDR("DBO", "Test", "AA", "my setting 2", 	null);
//		DataRow test3 = getDummyDR("DBO", "Test", "XX", "weird setting", 	null);
//		DataRow test4 = getDummyDR("DBO", "Test", "AA", "",					null);
//		DataRow test5 = getDummyDR("DBO", "Test",  "",  "",					null);
//		DataRow test6 = getDummyDR("DBO", "Test",  "",  "default setting 1",null);
//		bc.write(test1);
//		bc.write(test2);
//		bc.write(test3);
//		bc.write(test4);
//		bc.write(test5);
//		bc.write(test6);
//		bc.setFilter(test1);
//		ResultSet availlableConfigurations = bc.getAvaillableConfigurations();
//		assertNotNull(availlableConfigurations);
//		assertTrue(availlableConfigurations.size() >= 4);
//		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test1), "DataRow missing in availlable config: " + test1.toJson(false));
//		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test2), "DataRow missing in availlable config: " + test2.toJson(false));
//		assertFalse(checkIfRsContainsKeys(availlableConfigurations, test3), "Wrong DataRow included in availlable config: " + test3.toJson(false));//must not be in it; wrong user
//		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test4), "DataRow missing in availlable config: " + test4.toJson(false));
//		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test5), "DataRow missing in availlable config: " + test5.toJson(false));
//		assertTrue(checkIfRsContainsKeys(availlableConfigurations, test6), "DataRow missing in availlable config: " + test6.toJson(false));
//		
//	}
//	
//	/**
//	 * checks if a datarow is in a resultset only considering keys (and ignoring other fields)
//	 * @param rs
//	 * @param keys
//	 * @return
//	 */
//	private boolean checkIfRsContainsKeys(ResultSet rs, DataRow keys) {
//		if (rs.isEmpty()) {
//			return false;
//		}
//		try {
//			String realm   = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_REALM).trim();
//			String key     = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_KEYX).trim();
//			String userid  = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID).trim();
//			String setting = keys.getFieldAsString(ConfigurationsBC.FIELD_NAME_SETTING).trim();
//			for (DataRow dataRow : rs) {
//				String currentRealm   = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_REALM).trim();
//				String currentKey     = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_KEYX).trim();
//				String currentUserid  = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID).trim();
//				String currentSetting = dataRow.getFieldAsString(ConfigurationsBC.FIELD_NAME_SETTING).trim();
//				if (
//					realm	.contentEquals(currentRealm) 	&&
//					key		.contentEquals(currentKey) 			&&
//					userid	.contentEquals(currentUserid) 	&&
//					setting	.contentEquals(currentSetting))
//				{
//					return true;
//				}
//			}
//		}catch (Exception e) {
//		}
//		return false;
//	}
//	
//	private DataRow getDummyDR(String realm, String key, String userID, String setting, String config) throws ParseException {
//		DataRow dummy = new DataRow();
//		if (realm != null) {
//			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM,  realm);
//		}
//		if (key != null) {
//			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX,   key);
//		}
//		if (userID != null) {
//			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, userID);
//		}
//		if (setting != null) {
//			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_SETTING,setting);
//		}
//		if (config != null) {
//			dummy.setFieldValue(ConfigurationsBC.FIELD_NAME_CONFIG,config);
//		}
//		dummy = bc.fillWithSpacesToMatchMinLength(dummy);
//		return dummy;
//	}
//	

//	@Test
//	void testGetFilter() {
//		fail("Not yet implemented");
//	}

//	@Test
//	void testGetFieldSelection() {
//		fail("Not yet implemented");
//	}

}
