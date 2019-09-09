package com.basiscomponents.db.importer;

import com.basis.bbj.datatypes.TemplatedString;
import com.basis.filesystem.ConnectionMgr;
import com.basis.filesystem.FilePosition;
import com.basis.filesystem.Filesystem;
import com.basis.filesystem.FilesystemEOFException;
import com.basis.filesystem.FilesystemException;
import com.basis.filesystem.util.KeyDescription;
import com.basis.startup.type.BBjException;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Imports the data of a Data File into a com.basiscomponents.db.ResultSet
 * object using the BASIS JLib.
 */
public class JLibResultSetImporter {

	public static final String FILTER_KNUM = "FILTER_KNUM";
	public static final String FILTER_RANGE_FROM = "FILTER_RANGE_FROM";
	public static final String FILTER_RANGE_TO = "FILTER_RANGE_TO";
	public static final String FILTER_VALUE = "FILTER_VALUE";

	private String stringTemplate = null;
	private TemplatedString templatedString = null;
	private String fileName;
	private DataRow filter;

	private Map<Integer, String> fieldNameMap;

	private int offsetStart = -1;
	private int offsetCount = -1;

	public JLibResultSetImporter() {
	}

	/**
	 * Sets the path of the Data file and its Templated String.
	 * 
	 * @param filePath
	 *            The path of the Data file
	 * @param template
	 *            The Templated String matching the Data file
	 * @throws BBjException
	 */
	public void setFile(String filePath, String template) throws BBjException {
		this.fileName = filePath;
		this.stringTemplate = template;
		this.templatedString = new TemplatedString(template);
		this.fieldNameMap = initFieldNameMap(templatedString);
	}

	/**
	 * Parses the Templated String and initializes the field name map with the field
	 * name's defined in it.
	 * 
	 * @param templatedStr
	 *            The Templated String whose field names will be used to initialize
	 *            the field name map.
	 * 
	 * @return The field name map
	 */
	private HashMap<Integer, String> initFieldNameMap(TemplatedString templatedStr) {
		HashMap<Integer, String> indexList = new HashMap<>();

		List<String> fieldNameList = Arrays.asList(templatedStr.getFieldNames().toString().split("\n"));
		int counter = 0;

		for (String fieldName:fieldNameList) {
			indexList.put(counter, fieldName);
			counter++;
		}

		return indexList;
	}

	/**
	 * Sets the offset and the record index to start from when retrieving the Data
	 * from the Data file.
	 * 
	 * @param record
	 *            The record index to start from
	 * @param count
	 *            The number of records to read from the offset.
	 */
	public void setOffset(int record, int count) {
		this.offsetStart = record;
		this.offsetCount = count;
	}

	/**
	 * Sets the filter to apply while retrieving the records from the Data file.
	 * Currently the filter can be used to specify:
	 * <ul>
	 * <li>a range of values(from-to)</li>
	 * <li>an exact primary key value</li>
	 * <li>the KNUM to use</li>
	 * </ul>
	 * <br>
	 * <br>
	 * Use the following the field names:
	 * <ul>
	 * <li>JLibResultSetImporter.FILTER_KNUM</li>
	 * <li>JLibResultSetImporter.FILTER_RANGE_FROM</li>
	 * <li>JLibResultSetImporter.FILTER_RANGE_TO</li>
	 * <li>JLibResultSetImporter.FILTER_VALUE</li>
	 * </ul>
	 * 
	 * @param filter
	 *            The filter to set.
	 */
	public void setFilter(DataRow filter) {
		this.filter = filter;
	}

	/**
	 * Sets the field names to be retrieved from the Data file to the field name's
	 * of the given DataRow object.
	 * 
	 * @param fieldSelection
	 *            The DataRow whose field names will be set as field selection.
	 */
	public void setFieldSelection(DataRow fieldSelection) {
		if (templatedString == null || fieldSelection == null) {
			return;
		}

		fieldNameMap = new HashMap<>();
		List<String> fieldNameList = fieldSelection.getFieldNames();
		setFieldSelection(fieldNameList);
	}

	public void setFieldSelection(Collection<String> fieldSelection) {

		List<String> templatedStringFieldNameList = Arrays
				.asList(templatedString.getFieldNames().toString().split("\n"));
		int currentFieldIndex = 0;

		for (String currentFieldName:templatedStringFieldNameList) {
			for (String currentRequestedFieldName :fieldSelection) {
				if (currentRequestedFieldName.equalsIgnoreCase(currentFieldName)) {
					fieldNameMap.put(currentFieldIndex, currentFieldName);
				}
			}
			currentFieldIndex++;
		}
	}
	/**
	 * Returns a ResultSet object with the content of the Data file based on the
	 * specified filter, offset, field selection.
	 * 
	 * @return A ResultSet object with the records of the data file.
	 * 
	 * @throws IndexOutOfBoundsException
	 * @throws NoSuchFieldException
	 * @throws Exception
	 */
	public ResultSet retrieve() throws Exception {
		if (fieldNameMap == null || fieldNameMap.isEmpty() || templatedString == null) {
			return null;
		}

		// retrieving the connection manager object
		ConnectionMgr connectionManager = getConnectionMgr();

		// Opening the file & checking out a license
		FilePosition pos = connectionManager.open(fileName, true, true); // params: filePath, readOnly, useRemote


		FilePosition endPos = null;
		ResultSet rs = new ResultSet();

		TemplatedString templatedStr = templatedString;
		List<Integer> numericFieldIndeces = new ArrayList<>();
		if (fieldNameMap != null && !fieldNameMap.isEmpty()) {
			numericFieldIndeces = initNumericFieldsIndeces(templatedStr, fieldNameMap);
		}

		Set<Entry<Integer, String>> entrySet = fieldNameMap.entrySet();
		initColumnMetadata(rs, templatedStr, fieldNameMap);

		byte[] record = null;
		byte[] endRecord = null;

		// Check for record size and allocate buffer.
		if (pos.getRecordSize() > 0) {
			record = new byte[pos.getRecordSize()];
			endRecord = new byte[pos.getRecordSize()];
		} else {
			record = new byte[100];
			endRecord = new byte[100];
		}

		// Array with the keys for that FilePosition in the first dimension
		// and a byte array with the key length in the second dimension
		byte[][] keys = getKeys(pos);
		byte[][] lastRecordKeys = getKeys(pos);

		DataRow currentDataRow;
		int knum = 0;
		try {
			if (filter != null) {
				if (filter.contains(FILTER_KNUM)) {
					int filterKnum;

					try {
						filterKnum = Integer.valueOf(filter.getFieldAsString(FILTER_KNUM));
						if (0 <= filterKnum && filterKnum < keys.length) {
							knum = filterKnum;
						}
					} catch (NumberFormatException e) {
						// Do nothing in case the given KNUM value is not an integer value
					}
				}

				// trying to read the record with the given key
				if (filter.contains(FILTER_VALUE)) {
					byte[] value = filter.getFieldAsString(FILTER_VALUE).getBytes();

					try {
						pos.readByKey(record, 0, record.length, value, 0, value.length, knum, 0, 0);
					} catch (Exception e) {
					}

					if (!isRecordEmpty(record)) {
						templatedStr.setValue(record);

						currentDataRow = getDataRowFromRecord(entrySet, templatedStr, numericFieldIndeces);
						rs.add(currentDataRow);

						// Closing the open file connection
						pos.close();

						// Checking the license back in
						connectionManager.clear();

						return rs;
					}
					return null;
				}

				if (filter.contains(FILTER_RANGE_FROM) && filter.contains(FILTER_RANGE_TO)) {
					byte[] startKey = filter.getFieldAsString(FILTER_RANGE_FROM).getBytes();

					if (startKey != null) {
						try {
							pos.readByKey(record, 0, record.length, startKey, 0, startKey.length, knum, 0, 0);
						} catch (Exception e) {
							// The record could not be read
						}
					}

					byte[] endKey = filter.getFieldAsString(FILTER_RANGE_TO).getBytes();
					if (endKey != null) {
						endPos = connectionManager.open(fileName, true, true);
						try {
							endPos.readByKey(endRecord, 0, endRecord.length, endKey, 0, endKey.length, knum, 0, 0);
						} catch (Exception e) {

						}
					}
				}
			}

			// boolean value indicating whether the last record should be saved in the
			// ResultSet or not
			boolean includeLastRecord = true;
			if (endPos != null) {
				endPos.getNumKey(lastRecordKeys[knum], lastRecordKeys[knum].length, knum);
				if (isRecordEmpty(endRecord)) {
					includeLastRecord = false;
				}
			}

			boolean complete = false;
			boolean readPerOffset = false;

			if (offsetStart >= 0) {
				readPerOffset = true;

				// move the file pointer to the offset position
				pos.read(record, record.length, offsetStart - 1L, 5, false);
			}

			while (!complete) {

				if (endPos != null) {
					pos.getNumKey(keys[knum], keys[knum].length, knum);

					if (Arrays.equals(keys[knum], lastRecordKeys[knum])) {
						complete = true;
						if (!includeLastRecord) {
							break;
						}
					}
				}

				// read(byte[] p_buffer, int record_length, long p_move, int p_timeout, boolean
				// p_find)
				pos.read(record, record.length, 1, 5, false);
				templatedStr.setValue(record);

				currentDataRow = getDataRowFromRecord(entrySet, templatedStr, numericFieldIndeces);
				rs.add(currentDataRow);

				if (readPerOffset) {
					// read the records while the offset count is not reached
					if (rs.size() >= offsetCount) {
						break;
					}
				}
			}

			if (rs.size() > 0) {
				rs.get(0).setFieldValue("X-Total-Count", pos.getRecordCount());
			}

		} catch (FilesystemEOFException ex) {
			// End of iteration...
		}

		// closing the open file connection
		pos.close();

		if (endPos != null) {
			endPos.close();
		}

		// checking back in the checked-out license
		connectionManager.clear();
		return rs;
	}

	/**
	 * Returns a DataRow object with the values defined in the given Set, using the
	 * given Templated String and the numericFieldIndeces list to determine the
	 * exact type of the field in order to cast the value.
	 * 
	 * Parses the given Templated String for the field values defined in the given
	 * Set, and returns a DataRow with those values.
	 * 
	 * @param entrySet
	 *            The Entry with the field names and field values.
	 * @param templatedStr
	 *            The templated String used to retrieve the field type.
	 * @param numericFieldIndeces
	 *            The list of field indexes which are defined as numeric fields.
	 * 
	 * @return A DataRow object with the key-value pairs of the given Set.
	 * 
	 * @throws IndexOutOfBoundsException
	 * @throws NoSuchFieldException
	 * @throws BBjException
	 */
	private DataRow getDataRowFromRecord(final Set<Entry<Integer, String>> entrySet, TemplatedString templatedStr,
			List<Integer> numericFieldIndeces) throws NoSuchFieldException, BBjException {

		DataRow dr = new DataRow();

		boolean containsNumericValues = numericFieldIndeces != null && !numericFieldIndeces.isEmpty();

		for (Entry<Integer, String> entry : entrySet) {
			int key = entry.getKey();
			String value = entry.getValue();
			try {
				if (containsNumericValues) {
					if (numericFieldIndeces.contains(key)) {
						if ('X'==templatedStr.getFieldType(key)) {
							dr.setFieldValue(value, templatedStr.getFloat(key));
						} else {
							dr.setFieldValue(value, templatedStr.getDouble(key));
						}
					} else {
						dr.setFieldValue(value, templatedStr.getFieldAsString(key).toString());
					}
				} else {
					dr.setFieldValue(value, templatedStr.getFieldAsString(key).toString());
				}
			} catch (Exception e) {
				// do nothing
			}
		}
		return dr;
	}

	/**
	 * Parses the given Templated String using the given field names(Map) and
	 * returns a list of indexes with the field's defined as Numeric fields in the
	 * templated String.
	 * 
	 * @param templatedString
	 *            The Templated String to parse.
	 * @param fieldMap
	 *            The name of the fields
	 * 
	 * @return A list with the field indexes which are defined as Numeric fields.
	 * 
	 * @throws IndexOutOfBoundsException
	 * @throws NoSuchFieldException
	 */
	private List<Integer> initNumericFieldsIndeces(final TemplatedString templatedString, final Map<Integer, String> fieldMap)
			throws NoSuchFieldException {
		ArrayList<Integer> indexList = new ArrayList<>();

		int index = 0;
		int type;

		for (Integer key:fieldMap.keySet()) {
			type = templatedString.getFieldType(key);
			if (isNumericType(type)) {
				indexList.add(index);
			}

			index++;
		}

		return indexList;
	}

	private static boolean isNumericType(final int type) {
		return type == 'B' || type == 'D' || type == 'F' || type == 'N' || type == 'X' || type == 'Y';
	}

	/**
	 * Initializes the given ResultSet object's metadata(Column types) based on the
	 * defined values of the given Templated String.
	 * 
	 * @param rs
	 *            The ResultSet whose metadata will be set
	 * @param templatedStr
	 *            The Templated String to parse.
	 * @param fieldMap
	 *            The map with the field names of the Templated String
	 * 
	 * @throws Exception
	 */
	private void initColumnMetadata(ResultSet rs, TemplatedString templatedStr, Map<Integer, String> fieldMap) throws Exception {
		int columnIndex;
		String columnName;
		char type;
		for (Entry<Integer, String> entry:fieldMap.entrySet()) {

			columnName = entry.getValue();
			columnIndex = rs.addColumn(columnName);

			type = (char) templatedStr.getFieldType(entry.getKey());
			switch (type) {
			case 'B':
				rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
				break;

			case 'C':
				rs.setColumnType(columnIndex, java.sql.Types.VARCHAR);
				break;
			case 'D':
				rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
				break;

			case 'F':
				rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
				break;

			case 'I':
				rs.setColumnType(columnIndex, java.sql.Types.BINARY);
				break;

			case 'N':
				rs.setColumnType(columnIndex, java.sql.Types.NUMERIC);
				break;

			case 'U':
				rs.setColumnType(columnIndex, java.sql.Types.BINARY);
				break;

			case 'X':
				rs.setColumnType(columnIndex, java.sql.Types.FLOAT);
				break;

			case 'Y':
				rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
				break;

			default:
				rs.setColumnType(columnIndex, java.sql.Types.VARCHAR);
				break;
			}

			rs.setPrecision(columnIndex, templatedStr.getFieldSize(columnName));
		}
	}

	/**
	 * Returns true if the given byte Array is empty, false otherwise.
	 * 
	 * @param buffer
	 *            The byte Array
	 * 
	 * @return True if the byte Array is empty, false otherwise.
	 */
	private Boolean isRecordEmpty(byte[] buffer) {
		for (byte b : buffer) {
			if (b != 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns a byte Array with the keys from the given FilePosition object as
	 * first dimension and the second dimension a byte Array with the size of the
	 * key. The second value will mostly be used as buffer where to write the key
	 * content.
	 * 
	 * @param pos
	 *            the FilePosition.
	 * 
	 * @return The byte Array with the keys and the key sizes.
	 * 
	 * @throws FilesystemException
	 */
	private byte[][] getKeys(FilePosition pos) throws FilesystemException {
		byte[][] keys = null;

		if (pos.getKeySize() > 0) {
			int size = pos.getKeySize();
			keys = new byte[1][size];
		} else if (((pos.getFileType() & Filesystem.TYPEMASK) == Filesystem.MKEYED_FILE)
				|| ((pos.getFileType() & Filesystem.TYPEMASK) == Filesystem.XKEYED_FILE)) {
			List<KeyDescription> keyDesc = pos.getKeyDescriptions();
			keys = new byte[keyDesc.size()][];
			KeyDescription desc;

			for (int i = 0; i < keys.length; i++) {
				desc = keyDesc.get(i);
				keys[i] = new byte[desc.getKeySize()];
			}
		}

		return keys;
	}

	/**
	 * Returns the Templated String.
	 * 
	 * @return The Templated String.
	 */
	@SuppressWarnings("squid:S4275")
	public String getTemplatedString() {
		return this.stringTemplate;
	}

	/**
	 * Returns a ConnectionMgr object which takes care of the BLM Connection and
	 * which is used to open files using the JLIB.
	 *
	 * @return The ConnectionMgr object.
	 * 
	 * @throws FilesystemException
	 */
	private ConnectionMgr getConnectionMgr() throws FilesystemException {
		ConnectionMgr connectionMgr = Filesystem.getConnectionMgr();
		connectionMgr.setUser(System.getProperty("user.name"));

		// Initialize the license connection for the ConnectionMgr.
		// This must be done before any calls are made.
		connectionMgr.initLocalConnection();

		return connectionMgr;
	}

}
