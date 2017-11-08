package com.basiscomponents.bc;

import java.util.Collection;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

public interface BusinessComponent {

	/**
	 * Returns an empty DataRow including all field attributes (like field name, editable, label etc.), which the retrieve method could return.
	 * @return an empty DataRow with field attributes.
	 */
	public DataRow getAttributesRecord();

	/**
	 * Set a filter for the search result.<br>
	 * If the filter is set it will be used in the retrieve method.<br>
	 * If no filter is set, the retrieve method will return all data.
	 * @param  filter  a DataRow including field names and values to filter for. Filters are <u>AND</u> combined.
	 * @see    #retrieve()
	 */
	public void setFilter(DataRow filter);

	/**
     * Set a field selection to retrieve a custom set of fields.<br>
     * All fields will be returned if no field selection is set.
     * @param  fieldSelection a DataRow containing the field names for retrieving. Only the field names in the DataRow are used.
     */
	public void setFieldSelection(DataRow fieldSelection);

	/**
     * Set a field selection to retrieve a custom set of fields.<br>
     * The default scope will be used if no field selection is set.
     * @param  fieldSelection a String Collection containing the field names for retrieving.
     */
	public void setFieldSelection(Collection<String> fieldSelection);

	/**
     * Set a field selection scope (A, B, C, etc.).<br>
     * If no or a wrong scope is set then all fields will be returned.
     * @param  scope  the scope to set.
     */
	public void setScope(String scope);

	/**
     * Retrieves a ResultSet with DataRow's.<br>
     * If a filter is set, this will be applied to filter the result.<br>
     * If a scope and/or a field selection is set, it will be used to retrieve the desired fields.
     * @return  a ResultSet with DataRow's (may be empty).
     * @throws  Exception may occur during reading.
     */
	public ResultSet retrieve() throws Exception;

	/**
     * Retrieves a ResultSet containing a subset of DataRow's (for pagination f.g.).<br>
     * If a filter is set, this will be applied to filter the result.<br>
     * If a scope and/or a field selection is set, it will be used to retrieve the desired fields.
     * @param   first the index to start from (0 based).
     * @param   last the index of the last element in the subset.
     * @return  a ResultSet with DataRow's (may be empty).
     * @throws  Exception may occur during reading.
     */
	public ResultSet retrieve(int first, int last) throws Exception; 

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
	public ResultSet validateWrite(DataRow dr);

	/**
     * Write/persist a DataRow.
     * @param  row the DataRow to write.
     * @return the updated DataRow (may contain auto generated values/ID's).
     * @throws Exception when writing failed.
     * @see    #validateWrite(DataRow dr)
     */
	public DataRow write(DataRow row) throws Exception;

	/**
     * Validates a DataRow object before it can be removed.<br>
     * This method is internally used by the remove method.<br>
     * But it can also be called from the frontend to check for dependencies before it can be removed.
     * @param  dr the DataRow that should be removed.
     * @return a ResultSet with validation messages (empty ResultSet means no validation errors).<br>
     *         Each DataRow in the ResultSet should have following fields: FIELD_NAME, TYPE and MESSAGE.<br>
     *         FIELD_NAME: the name of the validated field<br>
     *         TYPE: INFO, WARNING or ERROR<br>
     *         MESSAGE: the validation message
     */
	public ResultSet validateRemove(DataRow dr);

	/**
     * Removes a DataRow.
     * @param  row the DataRow to remove.
     * @throws Exception if deleting failed.
     * @see    #validateRemove(DataRow dr)
     */
	public void remove(DataRow row) throws Exception;

	/**
     * Returns a new (predefined) DataRow including all field attributes (like field name, editable, label etc.).
     * @param  conditions a DataRow with predefined fields.
     * @return a new predefined DataRow with the field attributes.
     */
	public DataRow getNewObjectTemplate(DataRow conditions);

}