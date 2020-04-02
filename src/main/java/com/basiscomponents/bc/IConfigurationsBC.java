package com.basiscomponents.bc;
import java.util.Collection;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
public interface IConfigurationsBC {

public String getConfigFile()                                                 ;
public String getTemplateConfigFile()                                         ;
public void setConfigFile(String path)                                        ;
public void setUserName(String userName)                                      ;
public String getUserName()                                                   ;
public void setFieldsMinLengthAutoConvert(boolean fieldsMinLengthAutoConvert) ;
public void setTemplateConfigFile(String template)                            ;
public DataRow getAttributesRecord()                                          ;
public DataRow fillWithSpacesToMatchMinLength(DataRow dr)                     ;
public void setFilter(DataRow filter)                                         ;
public String getAdminUser()                                                  ;
public void setAdminUser(String adminUser)                                    ;
public void setFieldSelection(DataRow fieldSelection)                         ;
public void setFieldSelection(Collection<String> fieldSelection)              ;
public void setScope(String scope)                                            ;
public ResultSet retrieve() throws Exception                                  ;
public ResultSet retrieve(int first, int last) throws Exception               ;
public ResultSet validateWrite(DataRow dr)                                    ;
public DataRow write(DataRow dr) throws Exception                             ;
public ResultSet validateRemove(DataRow dr)                                   ;
public Boolean canModify(DataRow dr)                                          ;
public void remove(DataRow dr) throws Exception                               ;
public DataRow getNewObjectTemplate(DataRow conditions)                       ;
public ResultSet getEffectiveConfiguration()                                  ;
public DataRow getFilter()                                                    ;
public DataRow getFieldSelection()                                            ;
public ResultSet getAvaillableConfigurations() throws Exception               ;
public DataRow getScope()                                                     ;
public String getScopeDefault();
public String getScopeNoscope();
public String getFieldNameRealm();

public String getFieldNameKeyx();
public String getFieldNameUserid() ;
public String getFieldNameSetting();

public String getFieldNameCreaUser();

public String getFieldNameCreaDt();

public String getFieldNameModUser();

public String getFieldNameModDt();

public String getFieldNameConfig();
}
