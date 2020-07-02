package com.basiscomponents.bc;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.LinkedList;

import com.basis.bbj.datatypes.TemplatedString;
import com.basis.startup.type.BBjException;
import com.basiscomponents.db.BBArrayList;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.util.JLibDataFileHandler;
import com.google.common.base.Strings;

/**
 * Will handle a VKEYED config file that will store and provide configurations. This is currently used in the configuration widget plugin.
 * It ill handle a config file that will store and provide configurations for different users, setting and programs. The BC will not create the VKEYED config file.
 * the key fields are
 * -REALM: where the configuration is being used
 * -KEYX: program name
 * -USERID: username of the configuration owner
 * -SETTING: name of the configuration. a user can have multiple settings
 * @author jcorea
 *
 */
public class ConfigurationsBC implements BusinessComponent,IConfigurationsBC {
	
	protected static final String DEFAULT_TEMPLATE_CONFIG_FILE 	= "REALM:C(3*=10):DESCRIPTION=Realm:,KEYX:C(40*=10):DESCRIPTION=Key:,USERID:C(64*=10):DESCRIPTION=UserID:,SETTING:C(40*=10):DESCRIPTION=Setting:,CR_MONT:C(10*=10):DESCRIPTION=createduser:,CR_TS_LD:C(14*=10):DESCRIPTION=createdtimestamp:,MOD_MONT:C(10*=10):DESCRIPTION=modificationuser:,MOD_TS_LD:C(14*=10):DESCRIPTION=modificationtimestamp:,CONFIG:C(1*=10):DESCRIPTION=Configurationpayload:";
	
	protected static final String SCOPE_DEFAULT 				= "B";
	protected static final String SCOPE_NOSCOPE 				= "";
	
	public static final String FIELD_NAME_REALM 	            = "REALM";

	public static final String FIELD_NAME_KEYX  	            = "KEYX";
	public static final String FIELD_NAME_USERID                = "USERID";
	public static final String FIELD_NAME_SETTING               = "SETTING";
	public static final String FIELD_NAME_CREA_USER             = "CR_MONT";
	public static final String FIELD_NAME_CREA_DT  	            = "CR_TS_LD";
	public static final String FIELD_NAME_MOD_USER              = "MOD_MONT";
	public static final String FIELD_NAME_MOD_DT 	            = "MOD_TS_LD";
	public static final String FIELD_NAME_CONFIG  	            = "CONFIG";
	
	protected static String templateConfigFile 					= ConfigurationsBC.DEFAULT_TEMPLATE_CONFIG_FILE;
	
	protected String pathConfigFile;
	protected String userName;
	

	protected DataRow scope = new DataRow();
	protected DataRow filter;
	protected DataRow fieldSelection;
	
	protected static DataRow attributes;
	protected JLibDataFileHandler dataFileHandler;
	
	protected boolean fieldsMinLengthAutoConvert = true;
	protected boolean debug = false;
	
	protected String adminUser = "";
	protected String factoryDefaultUser = "_$";
	

	public ConfigurationsBC() {
		this("","");
	}
	
	public ConfigurationsBC(String pathConfigFile) {
		this(pathConfigFile,"");
	}
	
	public ConfigurationsBC(String pathConfigFile, String userName) {
		setScope(SCOPE_DEFAULT);
		this.filter = 			new DataRow();        
		this.fieldSelection = 	new DataRow();
		this.dataFileHandler=   new JLibDataFileHandler();
		setUserName(userName);
		setConfigFile(pathConfigFile);
	}
	
	public String getConfigFile() {
		return pathConfigFile;
	}
	
	public String getTemplateConfigFile() {
		return templateConfigFile;
	}
	
	public String getScopeDefault() {
		return SCOPE_DEFAULT;
	}
	
	public String getScopeNoscope() {
		return SCOPE_NOSCOPE;
	}
	
	public String getFieldNameRealm() {
		return FIELD_NAME_REALM;
	}
	
	public String getFieldNameKeyx() {
		return FIELD_NAME_KEYX;
	}
	
	public String getFieldNameUserid() {
		return FIELD_NAME_USERID;
	}
	
	public String getFieldNameSetting() {
		return FIELD_NAME_SETTING;
	}
	
	public String getFieldNameCreaUser() {
		return FIELD_NAME_CREA_USER;
	}
	
	public String getFieldNameCreaDt() {
		return FIELD_NAME_CREA_DT;
	}
	
	public String getFieldNameModUser() {
		return FIELD_NAME_MOD_USER;
	}
	
	public String getFieldNameModDt() {
		return FIELD_NAME_MOD_DT;
	}
	
	public String getFieldNameConfig() {
		return FIELD_NAME_CONFIG;
	}
	/**
	 * sets 
	 * @param path
	 */
	public void setConfigFile(String path) {
		this.pathConfigFile = path;
		try {
			dataFileHandler.setFile(this.pathConfigFile,  ConfigurationsBC.templateConfigFile);
		} catch (BBjException e) {
			printDebug(e.getMessage());
		}
	}
	
	public void setUserName(String userName) {
		userName = userName.trim();
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	
	public void setFieldsMinLengthAutoConvert(boolean fieldsMinLengthAutoConvert) {
		this.fieldsMinLengthAutoConvert = fieldsMinLengthAutoConvert;
	}
	
	/**
	 * sets the string template for the config file (and resets the attributes record)
	 * @param template new string template
	 */
	public void setTemplateConfigFile(String template) {
		ConfigurationsBC.attributes = null;
		ConfigurationsBC.templateConfigFile = template;
	}
	
	

	@Override
	public DataRow getAttributesRecord() {
		if (ConfigurationsBC.attributes == null) {
			DataRow attributes = DataRow.fromTemplate(ConfigurationsBC.templateConfigFile);
			setFieldLengthAttribute(attributes);
			setFieldLabelAttribute(attributes);
			//Keys
			attributes.setFieldAttribute(FIELD_NAME_REALM,   	"EDITABLE", "2");
			attributes.setFieldAttribute(FIELD_NAME_KEYX,    	"EDITABLE", "2");
			attributes.setFieldAttribute(FIELD_NAME_SETTING, 	"EDITABLE", "2");
			attributes.setFieldAttribute(FIELD_NAME_USERID,  	"EDITABLE", "2");
			//non editable fields
			attributes.setFieldAttribute(FIELD_NAME_CREA_USER,  "EDITABLE", "0");
			attributes.setFieldAttribute(FIELD_NAME_CREA_DT,  	"EDITABLE", "0");
			attributes.setFieldAttribute(FIELD_NAME_MOD_USER,  	"EDITABLE", "0");
			attributes.setFieldAttribute(FIELD_NAME_MOD_DT,  	"EDITABLE", "0");
			//editable non key field
			attributes.setFieldAttribute(FIELD_NAME_CONFIG,		"EDITABLE", "1");
			ConfigurationsBC.attributes = attributes;
		}
		return ConfigurationsBC.attributes;
	}
	
	/**
	 * sets the field label for each attribute in a 'LABEL' attribute. length is defined in the templated string as 'DESCRIPTION'.
	 * @param attributes
	 */
	private void setFieldLabelAttribute(DataRow attributes) {
		try {
			TemplatedString templatedString = new TemplatedString(ConfigurationsBC.templateConfigFile);
			for (String attributesName : attributes.getFieldNames()) {
				attributes.setFieldAttribute(attributesName, "LABEL", templatedString.getAttribute(attributesName, "DESCRIPTION"));
					
			}
		} catch (IndexOutOfBoundsException | BBjException e) {
			printDebug(e.getMessage());
		}
	}

	/**
	 * sets the field length for each attribute in a 'LENGTH' attribute. length is defined in the templated string.
	 * @param attributes
	 */
	protected void setFieldLengthAttribute(DataRow attributes) {
		try {
			TemplatedString templatedString = new TemplatedString(ConfigurationsBC.templateConfigFile);
			for (String attributesName : attributes.getFieldNames()) {
				attributes.setFieldAttribute(attributesName, "LENGTH", Integer.toString(templatedString.getFieldSize(attributesName)));
			}
		} catch (NoSuchFieldException | IndexOutOfBoundsException | BBjException e) {
			printDebug(e.getMessage());
		}
	}
	
	/**
	 * adds spaces to the end of each field value in the given datarow, until it reaches the given minimun length.
	 * No effekt for fields when field value is already at or above minimum length
	 * @param dr
	 * @return
	 */
	public DataRow fillWithSpacesToMatchMinLength(DataRow dr) {
		for (String fieldName : dr.getFieldNames()) {
			try {
				int length = Integer.valueOf(this.getAttributesRecord().getFieldAttribute(fieldName, "LENGTH"));
				dr.setFieldValue(fieldName, Strings.padEnd(dr.getFieldAsString(fieldName),length, ' '));
			} catch (Exception e) {
				printDebug(e.getMessage());
			}
		}
		return dr;
	}

	@Override
	public void setFilter(DataRow filter) {
		if (fieldsMinLengthAutoConvert) {
			filter = fillWithSpacesToMatchMinLength(filter);
		}
		this.filter = filter;
	}
	
	/**
	 * returns admin user. user with permission to edit all entries. other users can only modify their configuration
	 * @return 
	 */
	public String getAdminUser() {
		return adminUser;
	}

	/**
	 * sets admin user. user with permission to edit all entries. other users can only modify their configuration
	 * @param adminUser
	 */
	public void setAdminUser(String adminUser) {
		this.adminUser = adminUser;
	}
	
	public String getFactoryDefaultUser() {
		return this.factoryDefaultUser;
	}
	
	public void setFactoryDefaultUser(String factoryDefaultUser) {
		this.factoryDefaultUser = factoryDefaultUser;
	}

	@Override
	public void setFieldSelection(DataRow fieldSelection) {
		this.fieldSelection = fieldSelection;
	}

	@Override
	public void setFieldSelection(Collection<String> fieldSelection) {
		DataRow fieldSelectionDr = new DataRow();
		for (String field : fieldSelection) {
			try {
				fieldSelectionDr.setFieldValue(field, "");
			} catch (ParseException e) {
				printDebug(e.getMessage());
			}
		}
		this.setFieldSelection(fieldSelectionDr);
	}

	@Override
	public void setScope(String scope) {
		if (scope == null) {
			this.scope = new DataRow();
			return;
		}
		switch (scope) {
		case SCOPE_DEFAULT:
			DataRow dr = new DataRow();
			try {
				dr.setFieldValue("REALM", 	"");
				dr.setFieldValue("KEYX", 	"");
				dr.setFieldValue("SETTING", "");
				dr.setFieldValue("USERID", 	"");
				dr.setFieldValue("CONFIG", 	"");
				this.scope = dr;
			} catch (ParseException e) {
				printDebug(e.getMessage());
			}
			break;
		case SCOPE_NOSCOPE:
		default:
			this.scope = new DataRow();
			return;
		}
	}
	
	/**
	 * returns the modified filter where all key fields are removed and instead 2 fields are added<br>
	 * -FILTER_VALUE<br>
	 * -FILTER_KNUM<br>
	 * FILTER_VALUE is the concatination of the key fields in the knum order, so the DataFileHandler can use it to query the file<br>
	 * All key fields must be provided in the filter, otherwise an unmodified filter is returned.
	 * @param knumIndex
	 * @return
	 * @throws ParseException
	 */
	protected DataRow getKNUMValue(int knumIndex) throws ParseException {
		if (filter==null || filter.isEmpty() || !containsAllKNUMKeys(filter)) {
			return filter;
		}
		filter = filter.clone();
		filter.setFieldValue(JLibDataFileHandler.FILTER_VALUE, getKNUMString(knumIndex, filter));
		filter.setFieldValue(JLibDataFileHandler.FILTER_KNUM, Integer.toString(knumIndex));
		filter.removeField(FIELD_NAME_REALM);
		filter.removeField(FIELD_NAME_KEYX);
		filter.removeField(FIELD_NAME_SETTING);
		filter.removeField(FIELD_NAME_USERID);
		return filter;
	}
	
	/**
	 * concatenates the knum key and puts it in the right order. Used by getKNUMValue().
	 * @param knumIndex
	 * @param dr
	 * @return
	 * @see getKNUMValue()
	 */
	protected String getKNUMString(int knumIndex, DataRow dr) {
		String realm = "";
		String keyx = "";
		String userid = "";
		String setting = "";
		realm = dr.getFieldAsString(FIELD_NAME_REALM);
		realm = Strings.padEnd(realm, 3, ' ');
		keyx = dr.getFieldAsString(FIELD_NAME_KEYX);
		keyx = Strings.padEnd(keyx, 40, ' ');
		userid = dr.getFieldAsString(FIELD_NAME_USERID);
		userid = Strings.padEnd(userid, 64, ' ');
		setting = dr.getFieldAsString(FIELD_NAME_SETTING);
		setting = Strings.padEnd(setting, 40, ' ');
		String knum;
		if (knumIndex == 0) {
			knum = realm + keyx + userid + setting;
		} else {
			knum = userid + realm + keyx + setting;
		}
		return knum;
	}
	
	/**
	 * returns whether or not all key fields are provided in the data row
	 * @param dr
	 * @return
	 */
	protected boolean containsAllKNUMKeys(DataRow dr) {
		return (dr.contains(FIELD_NAME_USERID) && dr.contains(FIELD_NAME_KEYX) && dr.contains(FIELD_NAME_REALM) && dr.contains(FIELD_NAME_SETTING));
	}

	@Override
	public ResultSet retrieve() throws Exception {
		ResultSet rs;
		if (this.getFilter() == null) {this.setFilter(new DataRow());}
		DataRow rsImporterFilter = getKNUMValue(0);
		if (rsImporterFilter != null) {
			dataFileHandler.setFilter(rsImporterFilter);
		}
		if (this.getFieldSelection() != null && !this.getFieldSelection().isEmpty()) {
			dataFileHandler.setFieldSelection(this.getFieldSelection());
		} else if (this.scope != null && !this.scope.isEmpty()) {
			dataFileHandler.setFieldSelection(this.scope);
		} else {
			dataFileHandler.setFieldSelection(getAttributesRecord());
		}
		rs = dataFileHandler.retrieve();
		if (rs==null) {
			rs = new ResultSet();
		}
		return rs;
	}
	

	@Override
	public ResultSet retrieve(int first, int last) throws Exception {
		if (last < first) {
			return new ResultSet();
		}
		ResultSet retrieved = retrieve();
		ResultSet rs = new ResultSet();
		for (int i=first; i <= last && last < retrieved.size(); i++) {
			rs.add(retrieved.get(i));
		}
		return rs;
	}
	
	/**
	 * generates error message for validation methods.
	 * @param fieldName
	 * @param type
	 * @param message
	 * @return
	 */
	protected DataRow validationErrorMessage(String fieldName, String type, String message) {
		DataRow dr = new DataRow();
		try {
			dr.setFieldValue("FIELD_NAME", fieldName);
			dr.setFieldValue("TYPE", type);
			dr.setFieldValue("MESSAGE", message);
		} catch (ParseException e) {
			printDebug(e.getMessage());
		}
		return dr;
	}
	
	/**
	 * checks if the field is contained in the datarow, otherwise an error is added to the resultset
	 * @param dr
	 * @param errorRS
	 * @param fieldName
	 * @see checkEditableFieldsInDataRow()
	 */
	protected void checkDataRowMustContainField(DataRow dr, ResultSet errorRS, String fieldName) {
		if (!dr.contains(fieldName)) {
			errorRS.add(validationErrorMessage(fieldName, "ERROR", "Field '" + fieldName + "' is missing."));
		}
	}
	
	/**
	 * checks if the field is NOT contained in the datarow, otherwise an error is added to the resultset
	 * @param dr
	 * @param errorRS
	 * @param fieldName
	 * @see checkEditableFieldsInDataRow()
	 */
	protected void checkDataRowMustNotContainField(DataRow dr, ResultSet errorRS, String fieldName) {
		if (dr.contains(fieldName)) {
			errorRS.add(validationErrorMessage(fieldName, "ERROR", "Field '" + fieldName + "' is not writable."));
		}
	}
	
	/**
	 * checks a data row if it violates the requirements as defined in the attributesRecord.<br>
	 * For example if a field is a key, the dataRow must contain it.<br>
	 * If it is read only, the dataRow must not contain it (That is, if 'checkKeysOnly' is false.
	 * In case of violation, an error is added in the error result set.
	 * 
	 * @param dr
	 * @param errorRS
	 * @param checkKeysOnly
	 * @throws Exception
	 */
	protected void checkEditableFieldsInDataRow(DataRow dr, ResultSet errorRS, boolean checkKeysOnly) throws Exception {
		DataRow attributes = getAttributesRecord();
		BBArrayList<String> attributesFieldNames = attributes.getFieldNames();
		for (String attrFieldName : attributesFieldNames) {
			String editable = attributes.getFieldAttribute(attrFieldName, "EDITABLE");
			switch (editable) {
			case "0"://Not writable
				if (!checkKeysOnly) {
					checkDataRowMustNotContainField(dr, errorRS, attrFieldName);
				}
				break;
			case "2"://key
				checkDataRowMustContainField(dr, errorRS, attrFieldName);
				break;
			default:
				break;
			}
		}
	}

	/**
     * Validates a DataRow object before it can be written.<br>
     * This method is internally used by the write method.<br>
     * But it can also be called from the frontend to check for required or missing data.
     * @param  dr a DataRow to validated.
     * @return a ResultSet with validation messages (empty ResultSet means no validation errors).<br>
     *         Each DataRow in the ResultSet should have following fields: FIELD_NAME, TYPE and MESSAGE.<br>
     *         FIELD_NAME: the name of the validated field<br>
     *         TYPE: INFO, WARNING or ERROR<br>
     *         MESSAGE: the validation message
     */
	@Override
	public ResultSet validateWrite(DataRow dr) {
		ResultSet errorRS = new ResultSet();
		if (!canModify(dr)) {
			errorRS.add(validationErrorMessage("USERID", "ERROR", "User not allowed to write this configuration"));
		}
		try {
			checkEditableFieldsInDataRow(dr, errorRS, false);
		} catch (Exception e) {
			printDebug(e.getMessage());
		}
		return errorRS;
	}

	/**
	 * throws a validation error if the validation has failed. Does only affect methods where validation is called (e.g. write(), remove() etc.). The validate methods will instead return Boolean.FALSE
	 * @param validation
	 * @throws Exception
	 */
	protected void throwValidation(ResultSet validation) throws Exception {
		if (validation == null || validation.isEmpty()) {
			return;
		}
		StringBuilder sb = new StringBuilder();
		for (DataRow error : validation) {
			String fieldName = error.getFieldAsString("FIELD_NAME");
			String message = error.getFieldAsString("MESSAGE");
			String type = error.getFieldAsString("TYPE");
			sb.append("- ");
			sb.append(type).append(" in field '").append(fieldName).append("': ").append(message);
			sb.append("\n");
		}
		throw new Exception(sb.toString());
	}
	
	@Override
	public DataRow write(DataRow dr) throws Exception {
		ResultSet validation = validateWrite(dr);
		throwValidation(validation);
		dr = dr.clone();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
		LocalDateTime nowLDT = LocalDateTime.now();  
		String now = dtf.format(nowLDT);  
		String key = getKNUMString(0, dr);
		DataRow oldEntry = find(dr);
		//add knum key
		dr.setFieldValue(JLibDataFileHandler.FILTER_VALUE, key);
		dr.setFieldValue(JLibDataFileHandler.FILTER_KNUM,  0);
		//update modification date/user
		dr.setFieldValue(FIELD_NAME_MOD_DT, now);
		dr.setFieldValue(FIELD_NAME_MOD_USER, this.userName);
		if (oldEntry.isEmpty()) {
			//set creation fields to now and this user
			dr.setFieldValue(FIELD_NAME_CREA_DT, now);
			dr.setFieldValue(FIELD_NAME_CREA_USER, this.userName);
		} else {
			//remove keys because already provided via FILTER_VALUE KNUM key
			dr.removeField(FIELD_NAME_REALM);
			dr.removeField(FIELD_NAME_KEYX);
			dr.removeField(FIELD_NAME_SETTING);
			dr.removeField(FIELD_NAME_USERID);
		}
		if (fieldsMinLengthAutoConvert) {
			dr = fillWithSpacesToMatchMinLength(dr);
		}
		DataRow updated = dataFileHandler.write(dr);
		return updated;
	}
	
	/**
	 * 
	 * @param dr dataRow containing knum fields
	 * @return returns empty dataRow if entry was not found, or the entry as dataRow if found
	 * @throws ParseException
	 */
	protected DataRow find(DataRow dr) throws ParseException {
		if (fieldsMinLengthAutoConvert) {
			dr = fillWithSpacesToMatchMinLength(dr);
		}
		DataRow previousFilter = getFilter();
		DataRow previousFieldSelection = getFieldSelection();
		DataRow keys = new DataRow();
		//set keys
		keys.setFieldValue(FIELD_NAME_REALM, dr.getFieldAsString(FIELD_NAME_REALM));
		keys.setFieldValue(FIELD_NAME_KEYX, dr.getFieldAsString(FIELD_NAME_KEYX));
		keys.setFieldValue(FIELD_NAME_SETTING, dr.getFieldAsString(FIELD_NAME_SETTING));
		keys.setFieldValue(FIELD_NAME_USERID, dr.getFieldAsString(FIELD_NAME_USERID));
		setFilter(keys);
		setFieldSelection(new DataRow());
		ResultSet rs = null;
		try {
			rs = retrieve();
		}catch (Exception e) {
			printDebug(e.getMessage());
		}
		//reset filter
		setFilter(previousFilter);
		setFieldSelection(previousFieldSelection);
		if (rs == null || rs.isEmpty()) {
			return new DataRow();
		} else {
			return rs.get(0);
		}
	}
	

	/**
	 * validates datarow before removal.
	 * Does only check if main keys are provided, and NOT if record exists in the file.
	 */
	@Override
	public ResultSet validateRemove(DataRow dr) {
		ResultSet errorRS = new ResultSet();
		if (!canModify(dr)) {
			errorRS.add(validationErrorMessage("USERID", "ERROR", "User not allowed to delete this configuration"));
		}
		try {
			checkEditableFieldsInDataRow(dr, errorRS, true);
		} catch (Exception e) {
			printDebug(e.getMessage());
		}
		return errorRS;
	}
	
	/**
	 * returns true if this datarow can be modified. checks the username which is set with setUserName().
	 * if the username does not equal the userid field in the datarow, this returns false, unless the adminuser is set.
	 * The admin user can modify all configurations. 
	 * @param dr
	 * @return
	 */
	public Boolean canModify(DataRow dr) {
		String userName = null;
		if (dr.contains(FIELD_NAME_USERID)) {
			userName = dr.getFieldAsString(FIELD_NAME_USERID).trim();
		}
		if (this.userName.equals(this.factoryDefaultUser.trim())) {//factory user can edit everything
			return true;
		}
		if (userName != null && this.factoryDefaultUser.equals(userName)) {//bc user is not factoryDefault (checked before) and user in dataRow IS factoryDefault: not allowed
			return false;
		}
		if (this.userName.equals(this.adminUser.trim())) {// nonfactory related: admin can edit anything
			return true;
		}
		if (userName == null) {//if userName is not set and no admin or factory default, then not allowed
			return false;
		}
		if (this.userName.equals(userName)) {//normal user can edit his own entrys and nothing else
			return true;
		}
		return false;
	}

	/**
	 * removes a record from the file
	 * @throws throws an exception when the record could not be found.
	 * @param dr dataRow that contains the key of the record to be removed. should contain following fields:
	 *  - REALM	
	 *  - KEYX
     *  - USERID
     *  - SETTING
     *  fields not part of the key are being ignored.
	 */
	@Override
	public void remove(DataRow dr) throws Exception {
		ResultSet validation = validateRemove(dr);
		throwValidation(validation);
		String key = getKNUMString(0, dr);
		dr.setFieldValue(JLibDataFileHandler.FILTER_VALUE, key);
		dr.setFieldValue(JLibDataFileHandler.FILTER_KNUM,  0);
		try {
			dataFileHandler.remove(dr);
		} catch (Exception e) {
			Exception newExc = new Exception("Record could not be deleted: " + e.getClass() + e.getMessage());
			newExc.initCause(e);
			throw newExc;
		}
	}

	@Override
	public DataRow getNewObjectTemplate(DataRow conditions) {
		DataRow dr = getAttributesRecord().clone();
		for (String fieldName : conditions.getFieldNames()) {
			try {
				dr.setFieldValue(fieldName, conditions.getFieldAsString(fieldName));
			} catch (ParseException e) {
				printDebug(e.getMessage());
			}
		}
		return dr;
	}
	
	/**
	 * returns a DataRow with only the knum key fields from a given dataRow. if not all keys are present, adds them with empty value
	 * @param dr
	 * @return
	 * @throws ParseException
	 */
	protected DataRow getKeyFieldsOnly(DataRow dr) throws ParseException {
		DataRow newDR = new DataRow();
		DataRow attributesKeysOnly = this.getAttributesRecord().getFieldsHavingAttributeValue("EDITABLE", "2");
		for (String knum : attributesKeysOnly.getFieldNames()) {
			if (filter.contains(knum)) {
				newDR.setFieldValue(knum, dr.getFieldAsString(knum));
			} else {
				newDR.setFieldValue(knum, "");
			}
		}
		return newDR;
	}
	
	/**
	 * gets the effective configuration for a user. This means, if a configuration for a 
	 * given user with given settings exist (as specified by the filter), then this configuration
	 * will be returned, otherwise a default configuration will be returned. 
	 * @return ResultSet with the effective Configuration
	 */
	public ResultSet getEffectiveConfiguration() {
		DataRow previousFilter = getFilter();
		DataRow filter = previousFilter.clone();
		ResultSet rs = null;
		try {
			filter = getKeyFieldsOnly(filter);
			setFilter(filter);
			rs = retrieve();
		} catch (Exception e) {
			printDebug(e.getMessage());
		}
		if (rs != null && !rs.isEmpty()) {
			printDebug("found immediately.");
			try {printDebug("filter: " + getFilter().toJson());} catch (Exception e) {}
			setFilter(previousFilter);
			return rs;
		}
		LinkedList<String> filters = new LinkedList<String>();
		//following filter will applied in the order in which they are added to the filters list.
		//the first non-empty resultset that is retrieved will be returned.
		filters.add(FIELD_NAME_SETTING);
		filters.add(FIELD_NAME_USERID );
		for (String fieldDefault : filters) {
			try {
				if (FIELD_NAME_SETTING.equals(fieldDefault)) {
					filter.setFieldValue(fieldDefault, this.adminUser);
				} else {
					filter.setFieldValue(fieldDefault, "");
				}
				setFilter(filter);
				rs = retrieve();
			} catch (Exception e) {
				printDebug(e.getMessage());
			}
			if (rs != null && !rs.isEmpty()) {
				printDebug("found after admin defaulting " + fieldDefault);
				try {printDebug("filter: " + getFilter().toJson());} catch (Exception e) {}
				setFilter(previousFilter);
				return rs;
			}
		}
		
		try {//try factory default with setting from filter
			filter.setFieldValue(FIELD_NAME_USERID, this.factoryDefaultUser);
			if (previousFilter.contains(FIELD_NAME_SETTING)) {
				filter.setFieldValue(FIELD_NAME_SETTING, previousFilter.getFieldAsString(FIELD_NAME_SETTING));
			}
			setFilter(filter);
			rs = retrieve();
		} catch (Exception e) {
			printDebug(e.getMessage());
		}
		if (rs != null && !rs.isEmpty()) {
			printDebug("found after factory defaulting FIELD_NAME_USERID");
			try {printDebug("filter: " + getFilter().toJson());} catch (Exception e) {}
			setFilter(previousFilter);
			return rs;
		}
		
		try {//try factory default with empty setting
			filter.setFieldValue(FIELD_NAME_SETTING, "");
			setFilter(filter);
			rs = retrieve();
		} catch (Exception e) {
			printDebug(e.getMessage());
		}
		if (rs != null && !rs.isEmpty()) {
			printDebug("found after factory defaulting FIELD_NAME_SETTING");
			try {printDebug("filter: " + getFilter().toJson());} catch (Exception e) {}
			setFilter(previousFilter);
			return rs;
		}
		printDebug("not found.");
		try {printDebug("filter: " + getFilter().toJson());} catch (Exception e) {}
		setFilter(previousFilter);
		return new ResultSet();
	}
	

	public DataRow getFilter() {
		return filter;
	}

	public DataRow getFieldSelection() {
		return fieldSelection;
	}
	
	protected void printDebug(String s) {
		if (debug) {
			System.out.println(s);
		}
	}
	
	/**
	 * returns the available configurations the user can access with the current filter.
	 * conditions are:
	 * -only filtered realm
	 * -only filtered keyx
	 * -only when match with set userid OR default userid
	 * -ignore settings filter (all settings should be shown)
	 * @return ResultSet with available Configurations
	 * @throws Exception
	 */
	public ResultSet getAvailableConfigurations() throws Exception {
		DataRow previousFilter 	 = getFilter();
		if (!previousFilter.contains(ConfigurationsBC.FIELD_NAME_REALM) || !previousFilter.contains(ConfigurationsBC.FIELD_NAME_KEYX)) {
			return new ResultSet();//No key and/or realm: no available config to be configured
		}
		DataRow previousFieldSel = getFieldSelection();
		try {
			setFieldSelection(new DataRow());
			DataRow filter = new DataRow();
			String realm = previousFilter.getFieldAsString(ConfigurationsBC.FIELD_NAME_REALM);
			String key = previousFilter.getFieldAsString(ConfigurationsBC.FIELD_NAME_KEYX);
			//set filter for factory default and retrieve
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM, realm);
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX, key);
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, this.factoryDefaultUser);
			setFilter(filter);
			ResultSet factoryDefaultConfig = retrieve();
			//set filter for default user id and retrieve
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_REALM, realm);
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_KEYX, key);
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, "");
			setFilter(filter);
			ResultSet defaultConfig = retrieve();
			defaultConfig = mergeResultSetsOnKeys(factoryDefaultConfig, defaultConfig); // merge factory default with default config
			//if user was admin, we only need the default configuration. return it
			if (!previousFilter.contains(ConfigurationsBC.FIELD_NAME_USERID) || previousFilter.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID).trim().contentEquals(getAdminUser())) {
				setFilter(previousFilter);
				setFieldSelection(previousFieldSel);
				return defaultConfig;
			}
			//set filter for set user id and retrieve
			String userid = previousFilter.getFieldAsString(ConfigurationsBC.FIELD_NAME_USERID);
			filter.setFieldValue(ConfigurationsBC.FIELD_NAME_USERID, userid);
			setFilter(filter);
			ResultSet userConfig = retrieve();
			//merge default and set configuration
			ResultSet availableConfigs = mergeResultSetsOnKeys(userConfig, defaultConfig);
			//reset filter and fied selection
			setFilter(previousFilter);
			setFieldSelection(previousFieldSel);
			//return merged configuration
			return availableConfigs;
		}catch (Exception e) {
			setFilter(previousFilter);
			setFieldSelection(previousFieldSel);
			throw e;
		}
	}
	
	/**
	 * merges 2 resultsets
	 * @param rs1
	 * @param rs2
	 * @return
	 */
	protected ResultSet mergeResultSetsOnKeys(ResultSet rs1, ResultSet rs2) {
		ResultSet largerRS;
		ResultSet smallerRS;
		//determine which is rs smaller
		if (rs1.size() > rs2.size()) {
			largerRS = rs1;
			smallerRS = rs2;
		} else {
			largerRS = rs2;
			smallerRS = rs1;
		}
		//merge smaller into larger rs
		ResultSet mergedRS = largerRS.clone();
		for (DataRow smallerDR : smallerRS) {
			boolean found = false;
			for (DataRow largerDR : largerRS) {
				if (smallerDR.equals(largerDR)) {
					found = true;
					break;
				}
			}
			if (!found) {
				mergedRS.add(smallerDR);
			}
		}
		return mergedRS;
	}

	public static void main(String[] args) {
		try {
			ConfigurationsBC bc = new ConfigurationsBC();
			bc.setConfigFile("C:\\bbj\\cfg\\configurationWidgetConfigs");
			bc.setUserName("");
//			System.out.println(bc.getAttributesRecord().toJson());
			DataRow writeDr = new DataRow();
			writeDr.setFieldValue(FIELD_NAME_REALM,  "DBO");
			writeDr.setFieldValue(FIELD_NAME_KEYX,   "Test2");
			writeDr.setFieldValue(FIELD_NAME_USERID, "JC");
			writeDr.setFieldValue(FIELD_NAME_SETTING,"");
			writeDr.setFieldValue(FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
			bc.write(writeDr);
			bc.remove(writeDr);
			writeDr.setFieldValue(FIELD_NAME_REALM,  "DBO");
			writeDr.setFieldValue(FIELD_NAME_KEYX,   "Test2");
			writeDr.setFieldValue(FIELD_NAME_USERID, "MN");
			writeDr.setFieldValue(FIELD_NAME_SETTING,"");
			writeDr.setFieldValue(FIELD_NAME_CONFIG, "{\"TEST\":\"Default\"}");
			bc.write(writeDr);
			writeDr.setFieldValue(FIELD_NAME_REALM,  "DBO");
			writeDr.setFieldValue(FIELD_NAME_KEYX,   "Test2");
			writeDr.setFieldValue(FIELD_NAME_USERID, "");
			writeDr.setFieldValue(FIELD_NAME_SETTING,"");
			writeDr.setFieldValue(FIELD_NAME_CONFIG, "{\"TEST\":\"Default2\"}");
			bc.write(writeDr);
			bc.remove(writeDr);
			writeDr.setFieldValue(FIELD_NAME_REALM,  "DBO");
			writeDr.setFieldValue(FIELD_NAME_KEYX,   "Test2");
			writeDr.setFieldValue(FIELD_NAME_USERID, "_$");
			writeDr.setFieldValue(FIELD_NAME_SETTING,"");
			writeDr.setFieldValue(FIELD_NAME_CONFIG, "{\"TEST\":\"factoryDefault2\"}");
			bc.setUserName("_$");
			bc.write(writeDr);
			bc.setUserName("");
//			bc.remove(writeDr);
//			writeDr.setFieldVaCdValue(FIELD_NAME_CREA_USER, "");
			DataRow filter = new DataRow();
			filter.setFieldValue(FIELD_NAME_REALM,  "DBO");
			filter.setFieldValue(FIELD_NAME_KEYX,   "Test2");
			filter.setFieldValue(FIELD_NAME_USERID, "JC");
			filter.setFieldValue(FIELD_NAME_SETTING,"asd");
			bc.setFilter(filter);
//			bc.setFieldSelection(fieldSelection);
			ResultSet rs;
//			long millis = System.currentTimeMillis();
//			rs = bc.retrieve();
//			rs = bc.getAvailableConfigurations();
			rs = bc.getEffectiveConfiguration();
//			millis = System.currentTimeMillis() - millis;
//			System.out.println("retrieve execution time: " + millis);
			for (DataRow dataRow : rs) {
				System.out.println(dataRow.toString());
			}
//			bc.remove(filter);
			System.out.println(rs.size());
			
//			System.out.println(bc.getAttributesRecord().toJson());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataRow getScope() {
		return this.scope;
	}

	@Override
	public BusinessComponent getLookup(String fieldName, DataRow dr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ResultSet getLookupData(String fieldName, DataRow dr) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
