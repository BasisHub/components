package com.basiscomponents.bc;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

/**
 * This interface has to be implemented by BCs in order to work with the configurationW widget plugin. Implementations will handle a config file that will store and provide configurations
 * for different users, setting and programs.
 * the key fields are
 * -REALM: where the configuration is being used
 * -KEYX: program name
 * -USERID: username of the configuration owner
 * -SETTING: name of the configuration. a user can have multiple settings
 * @author jcorea
 *
 */
public interface IConfigurationsBC extends BusinessComponent{

	/**
	 * @return returns the path of the config file
	 */
	public String getConfigFile();
	
	/**
	 * @return returns the string template of the config file
	 */
	public String getTemplateConfigFile();
	
	/**
	 * sets new config file path
	 * @param path new config file path
	 */
	public void setConfigFile(String path);
	
	/**
	 * set a new username. username will be used to determine the visible configurations and permissions.
	 * empty String "" as username enables admin mode
	 * @param userName
	 */
	public void setUserName(String userName);
	
	/**
	 * returns the current username
	 * @return the current username
	 */
	public String getUserName();
	
	/**
	 * if set to true, all fields will be padded with empty spaces to match the required field length in the attributes record.
	 * automatically applies to all calls to write,remove,retrieve
	 * @see fillWithSpacesToMatchMinLength(DataRow dr) 
	 * @param fieldsMinLengthAutoConvert
	 */
	public void setFieldsMinLengthAutoConvert(boolean fieldsMinLengthAutoConvert);
	
	/**
	 * sets the string template for the config file (and resets the attributes record)
	 * @param template new string template
	 */
	public void setTemplateConfigFile(String template);
	
	/**
	 * all fields will be padded with empty spaces to match the required field length in the attributes record.
	 * @see setFieldsMinLengthAutoConvert(boolean fieldsMinLengthAutoConvert)
	 * @param dr
	 * @return
	 */
	public DataRow fillWithSpacesToMatchMinLength(DataRow dr);
	
	/**
	 * returns the currently set username for the admin user. Default is "".
	 * @return current admin user
	 */
	public String getAdminUser();
	
	/**
	 * sets the new username of the admin. default is empty string ""
	 * @param new admin username
	 */
	public void setAdminUser(String adminUser);
	
	
	/**
	 * returns true if this datarow can be modified. checks the username which is set with setUserName().
	 * if the username does not equal the userid field in the datarow, this returns false, unless the adminuser is set.
	 * The admin user can modify all configurations. 
	 * @param dr
	 * @return
	 */
	public Boolean canModify(DataRow dr);
	
	/**
	 * gets the effective configuration for a user. This means, if a configuration for a 
	 * given user with given settings exist (as specified by the filter), then this configuration
	 * will be returned, otherwise a default configuration will be returned. 
	 * @return ResultSet with the effective Configuration
	 */
	public ResultSet getEffectiveConfiguration();
	
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
	public ResultSet getAvailableConfigurations() throws Exception;
	
	
	/**
	 * returns the default scope
	 * @return
	 */
	public String getScopeDefault();
	
	/**
	 * returns the scope which returns all fields (no scopes applied)
	 * @return
	 */
	public String getScopeNoscope();
	
	/**
	 * return the field name of the realm field
	 * @return
	 */
	public String getFieldNameRealm();
	
	/**
	 * return the field name of the keyx field
	 * @return
	 */
	public String getFieldNameKeyx();
	
	/**
	 * return the field name of the userid field
	 * @return
	 */
	public String getFieldNameUserid();
	
	/**
	 * return the field name of the setting field
	 * @return
	 */
	public String getFieldNameSetting();
	
	/**
	 * return the field name of the creation user field
	 * @return
	 */
	public String getFieldNameCreaUser();
	
	/**
	 * return the field name of the creation date field
	 * @return
	 */
	public String getFieldNameCreaDt();
	
	/**
	 * return the field name of the last modification user field
	 * @return
	 */
	public String getFieldNameModUser();
	
	/**
	 * return the field name of the last modification date field
	 * @return
	 */
	public String getFieldNameModDt();
	
	/**
	 * return the field name of the config field
	 * @return
	 */
	public String getFieldNameConfig();
}
