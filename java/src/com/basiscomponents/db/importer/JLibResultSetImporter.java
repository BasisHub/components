package com.basiscomponents.db.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.basis.bbj.client.util.BBjException;
import com.basis.bbj.datatypes.TemplatedString;
import com.basis.filesystem.ConnectionMgr;
import com.basis.filesystem.FilePosition;
import com.basis.filesystem.Filesystem;
import com.basis.filesystem.FilesystemEOFException;
import com.basis.filesystem.FilesystemException;
import com.basis.filesystem.util.KeyDescription;
import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

/**
 * Provides methods to import Records from a Data File to a com.basiscomponents.db.ResultSet using the BASIS JLib.
 */
public class JLibResultSetImporter {
	
	public static final String FILTER_KNUM       = "FILTER_KNUM";
	public static final String FILTER_RANGE_FROM = "FILTER_RANGE_FROM";
	public static final String FILTER_RANGE_TO   = "FILTER_RANGE_TO";
	public static final String FILTER_VALUE      = "FILTER_VALUE";
	
	private String stringTemplate = null;
	private TemplatedString templatedString = null;
	private String fileName;
	private DataRow filter;
	
	private HashMap<Integer, String> fieldNameMap;
	
	private int offsetStart = -1;
	private int offsetCount = -1;
	
	public JLibResultSetImporter(){}
	
	public void setFile(String filePath, String template) throws BBjException{
		this.fileName = filePath;
		this.stringTemplate = template;
		this.templatedString = new TemplatedString(template);
		this.fieldNameMap = initFieldNameMap(templatedString);
	}
	
	private HashMap<Integer, String> initFieldNameMap(TemplatedString templatedStr) {
		HashMap<Integer, String> indexList = new HashMap<Integer, String>();
		List<String> fieldNameList = Arrays.asList(templatedStr.getFieldNames().toString().split("\n"));
		Iterator<String> it = fieldNameList.iterator();
		int counter = 0;
		
		while(it.hasNext()){
			indexList.put(counter, it.next());
			counter++;
		}
		
		return indexList;
	}

	public void setOffset(int record, int count){
		this.offsetStart = record;
		this.offsetCount = count;
	}
	
	public void setFilter(DataRow filter){
		this.filter = filter;
	}
	
	public void setFieldSelection(DataRow fieldSelection){
		if(templatedString != null){
			if(fieldSelection != null){
				fieldNameMap = new HashMap<Integer, String>();
				
				@SuppressWarnings("unchecked")
				ArrayList<String> fieldNameList = fieldSelection.getFieldNames();
				
				List<String> templatedStringFieldNameList = Arrays.asList(templatedString.getFieldNames().toString().split("\n"));
				Iterator<String> it = templatedStringFieldNameList.iterator();
				
				Iterator<String> requestedFieldNameIterator;
				
				String currentFieldName;
				String currentRequestedFieldName;
				int currentFieldIndex = 0;
				
				while(it.hasNext()){
					currentFieldName = it.next();
					
					requestedFieldNameIterator = fieldNameList.iterator();
					while(requestedFieldNameIterator.hasNext()){
						currentRequestedFieldName = requestedFieldNameIterator.next();
						
						if(currentRequestedFieldName.equalsIgnoreCase(currentFieldName)){
							fieldNameMap.put(currentFieldIndex, currentFieldName);
						}
					}
					
					currentFieldIndex++;
				}
			}
		}
	}
    
    public ResultSet retrieve() throws IndexOutOfBoundsException, NoSuchFieldException, Exception {
    	if(fieldNameMap != null && !fieldNameMap.isEmpty()){
    		if(templatedString != null){
    			
    			ConnectionMgr connectionManager = getConnectionMgr();
    			FilePosition pos = connectionManager.open(fileName,true,true);
    			
            	FilePosition endPos = null;
            	
            	ResultSet rs = new ResultSet();
            	
            	TemplatedString templatedStr = templatedString;
            	List<Integer> numericFieldIndeces = new ArrayList<Integer>();
            	if(fieldNameMap != null && !fieldNameMap.isEmpty()){
            		numericFieldIndeces = initNumericFieldsIndeces(templatedStr, fieldNameMap);
            	}
            	
            	Set<Entry<Integer, String>> entrySet = fieldNameMap.entrySet();
            	initColumnMetadata(rs, templatedStr, fieldNameMap);
            	
            	try{
            		byte[] record = null;
        			byte[] endRecord = null;
            		
        			// Check for record size and allocate buffer.
        			if (pos.getRecordSize() > 0){
        			    record = new byte[pos.getRecordSize()];
        			    endRecord = new byte[pos.getRecordSize()];
        			}else{
        			    record = new byte[100];
        			    endRecord = new byte[100];
        			}
        			
        			// Array with the keys for that FilePosition in the first dimension
        			// and a byte array with the key length in the second dimension
        			byte[][] keys = getKeys(pos);
        			byte[][] lastRecordKeys = getKeys(pos);
        			
        			DataRow currentDataRow;
        			int knum = 0;
        			
        			if(filter != null){
        				
        				if(filter.contains(FILTER_KNUM)){
        					int filterKnum;
        					
        					try{
        						filterKnum = Integer.valueOf(filter.getFieldAsString(FILTER_KNUM));
        						if(0 <= filterKnum && filterKnum < keys.length){
            						knum = filterKnum;
            					}
        					}catch(NumberFormatException e){
        						// Do nothing in case the given KNUM value is not an integer value
        					}
        				}
        				
        				if(filter.contains(FILTER_VALUE)){
        					byte[] value = filter.getFieldAsString(FILTER_VALUE).getBytes();
        					
        					try{
        						pos.readByKey(record, 0, record.length, value, 0, value.length, knum, 0, 0);
        					}catch(Exception e){
        					}
        					
        					if(!isRecordEmpty(record)){
        						templatedStr.setValue(record);
        						
        						currentDataRow = getDataRowFromRecord(entrySet, templatedStr, numericFieldIndeces);
        						rs.add(currentDataRow);
        						
        						return rs;
        					}
        						
        					return null;
        				}
        				
        				if(filter.contains(FILTER_RANGE_FROM) && filter.contains(FILTER_RANGE_TO)){
        					byte[] startKey = filter.getFieldAsString(FILTER_RANGE_FROM).getBytes();
        					
        					if(startKey != null){
        						try{
        							pos.readByKey(record, 0, record.length, startKey, 0, startKey.length, knum, 0, 0);
        						}catch(Exception e){
        							// The record could not be read
        						}
        					}	
        					
        					byte[] endKey = filter.getFieldAsString(FILTER_RANGE_TO).getBytes();
        					if(endKey != null){
        						endPos = connectionManager.open(fileName,true,true);
        						try{
        							endPos.readByKey(endRecord, 0, endRecord.length, endKey, 0, endKey.length, knum, 0, 0);
        						}catch(Exception e){
        							
        						}
        					}	
        				}
        			}			
        			
        			// boolean value indicating whether the last record should be saved in the ResultSet or not 
        			boolean includeLastRecord = true;
        			if(endPos != null){
        				endPos.getNumKey(lastRecordKeys[knum], lastRecordKeys[knum].length, knum);
        				if(isRecordEmpty(endRecord)){
        					includeLastRecord = false;
        				}
        			}
        			
        			boolean complete = false;
        			boolean readPerOffset = false;
        			
        			if(offsetStart >= 0){
        				readPerOffset = true;
        				
        				// move the file pointer to the offset position
        				pos.read(record, record.length, offsetStart-1, 5, false);
        			}
        			
        			boolean firstRecord = true; 
        			// get the total number of records
        			//pos.getRecordCount());
        			
        			while (!complete){	
        				
        				if(endPos != null){
        					pos.getNumKey(keys[knum], keys[knum].length, knum);
        					
        					if(Arrays.equals(keys[knum],lastRecordKeys[knum])){
        						complete = true;
        						if(!includeLastRecord){
        							break;
        						}
        					}
        				}
        				
        				// read(byte[] p_buffer, int p_size, long p_move, int p_timeout, boolean p_field)
        				pos.read(record,record.length,1,5,false);
        				templatedStr.setValue(record);
        					
        				currentDataRow = getDataRowFromRecord(entrySet, templatedStr, numericFieldIndeces);
        				
        				if(firstRecord){
        					currentDataRow.setFieldValue("X-Total-Count", pos.getRecordCount());
        					firstRecord = false;
        				}
        				
        				rs.add(currentDataRow);
        
        				
        				if(readPerOffset){
        					// read the records while the offset count is not reached
            				if(rs.size() >= offsetCount){
            					break;
            				}
        				}
        			}
        	    }catch (FilesystemEOFException ex){
        	        // End of iteration...
        	    }
            	
            	connectionManager.clear();
            	return rs;
        	}
        	return null;
    	}
    	return null;
	}

    /**
     * Parses the given Templated String for the field values defined in the given Set, and returns a DataRow with those values.
     * 
     * @param entrySet
     * @param templatedStr
     * @param numericFieldIndeces
     * @return
     * @throws IndexOutOfBoundsException
     * @throws NoSuchFieldException
     * @throws BBjException
     */
	private DataRow getDataRowFromRecord(Set<Entry<Integer,String>> entrySet, TemplatedString templatedStr, List<Integer> numericFieldIndeces) throws IndexOutOfBoundsException, NoSuchFieldException, BBjException {
		Iterator<Entry<Integer,String>> it = entrySet.iterator();
		Entry<Integer, String> entry;
		
		DataRow dr = new DataRow();
		
		boolean containsNumericValues = false;
		if(numericFieldIndeces != null && !numericFieldIndeces.isEmpty()){
			containsNumericValues = true;
		}
		
		byte type;
		int key;
		String value;
		
		while(it.hasNext()){
			entry = it.next();
			key = entry.getKey();
			value = entry.getValue();
			
			if(containsNumericValues){
				if(numericFieldIndeces.contains(entry.getKey())){
					type = templatedStr.getFieldType(entry.getKey());
					
					if(type == 'X'){
						dr.setFieldValue(value, templatedStr.getFloat(key));
					}else{
						dr.setFieldValue(value, templatedStr.getDouble(key));
					}
				}else{
					dr.setFieldValue(value, templatedStr.getFieldAsString(key).toString());
				}
			}else{
				dr.setFieldValue(value, templatedStr.getFieldAsString(key).toString());
			}
		}
		
		return dr;
	}

	private List<Integer> initNumericFieldsIndeces(TemplatedString templatedString, HashMap<Integer, String> fieldMap) throws IndexOutOfBoundsException, NoSuchFieldException {
		Iterator<Entry<Integer, String>> it = fieldMap.entrySet().iterator();
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		int index = 0;
		int type;
		Entry<Integer, String> entry;
		
		while(it.hasNext()){
			entry = it.next();
			type = templatedString.getFieldType(entry.getKey());
			
			if(type == 'B' || type == 'D' || type == 'F' || type == 'N' || type == 'X' || type == 'Y'){
				indexList.add(index);
			}
			
			index++;
		}
		
		return indexList;
	}

	private void initColumnMetadata(ResultSet rs, TemplatedString templatedStr, HashMap<Integer, String> fieldMap) throws Exception {
		Iterator<Entry<Integer,String>> it = fieldMap.entrySet().iterator();
    	int columnIndex;
    	String columnName;
    	char type;
    	Entry<Integer, String> entry;
    	
    	while(it.hasNext()){
    		entry = it.next();
    		columnName = entry.getValue();
    		columnIndex = rs.addColumn(columnName);
    		
    		type = (char) templatedStr.getFieldType(entry.getKey());
    		switch(type){
    			case 'B': rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
    					  break;
    				
    			case 'C': rs.setColumnType(columnIndex, java.sql.Types.VARCHAR);
    					  break;
    					  
    			case 'D': rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
				  		  break;
				  		  
    			case 'F': rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
		  		  		  break;
    					  
    			case 'I': rs.setColumnType(columnIndex, java.sql.Types.BINARY);
				  		  break;
    					  
    			case 'N': rs.setColumnType(columnIndex, java.sql.Types.NUMERIC);
    					  break;
    				
    			case 'U': rs.setColumnType(columnIndex, java.sql.Types.BINARY);
				  		  break;
				  		
    			case 'X': rs.setColumnType(columnIndex, java.sql.Types.FLOAT);
				  		  break;
				  		  
    			case 'Y': rs.setColumnType(columnIndex, java.sql.Types.DOUBLE);
		  		  		  break;
		  		  		
    			default:  rs.setColumnType(columnIndex, java.sql.Types.VARCHAR);
    					  break;
    		}
    		
    		rs.setPrecision(columnIndex, templatedStr.getFieldSize(columnName));
    	}
	}

	private Boolean isRecordEmpty(byte[] buffer) {
    	for(byte b : buffer){
			if(b != 0){
				return false;
			}
		}
    	return true;
	}

	/**
     * Returns a byte Array with the keys from the given FilePosition object as first dimension 
     * and the second dimension a byte Array with the size of the key. The second value will mostly be used
     * as buffer where to write the key content
     * 
     * @param pos
     * @return
     * @throws FilesystemException
     */
	private byte[][] getKeys(FilePosition pos) throws FilesystemException {
		byte[][] keys = null;
		
		if (pos.getKeySize() > 0){
            int size = pos.getKeySize();
        	keys = new byte[1][size];    
        }else if (((pos.getFileType() & Filesystem.TYPEMASK) == Filesystem.MKEYED_FILE)|| ((pos.getFileType() & Filesystem.TYPEMASK) == Filesystem.XKEYED_FILE)){
            List<KeyDescription> keyDesc = pos.getKeyDescriptions();
            keys = new byte[keyDesc.size()][];
            KeyDescription desc;
            
            for (int i = 0; i < keys.length; i++){
                desc = (KeyDescription)keyDesc.get(i);
                keys[i] = new byte[desc.getKeySize()];
            }
        }
		
		return keys;
	}
    
    public String getTemplatedString() {
		return this.stringTemplate;
	}
    
    private ConnectionMgr getConnectionMgr() throws FilesystemException{
        ConnectionMgr connectionMgr = Filesystem.getConnectionMgr();
        connectionMgr.setUser(System.getProperty("user.name"));
        
        // Initialize the license connection for the ConnectionMgr. 
    	// This must be done before any calls are made.
        connectionMgr.initLocalConnection();
        
        return connectionMgr;
    }
    
    public static void main(String[] args) throws IndexOutOfBoundsException, NoSuchFieldException, Exception{
    	JLibResultSetImporter importer = new JLibResultSetImporter();
    	importer.setFile("C:/bbj/demos/chiledd/data/Customer", "CUST_NUM:C(6):LABEL=CUST_NUM:,FIRST_NAME:C(20):LABEL=FIRST_NAME:,LAST_NAME:C(30):LABEL=Last:,COMPANY:C(30):LABEL=Company:,BILL_ADDR1:C(30):LABEL=BILL_ADDR1:,BILL_ADDR2:C(30):LABEL=BILL_ADDR2:,CITY:C(20):LABEL=CITY:,STATE:C(2):LABEL=STATE:,COUNTRY:C(20):LABEL=COUNTRY:,POST_CODE:C(12):LABEL=POST_CODE:,PHONE:C(15):LABEL=PHONE:,FAX:C(15):LABEL=FAX:,SALESPERSON:C(3):LABEL=SALESPERSON:,SHIP_ZONE:C(2):LABEL=SHIP_ZONE:,SHIP_METHOD:C(5):LABEL=SHIP_METHOD:,CURRENT_BAL:N(12):LABEL=CURRENT_BAL:,OVER_30:N(12):LABEL=OVER_30:,OVER_60:N(12):LABEL=OVER_60:,OVER_90:N(12):LABEL=OVER_90:,OVER_120:N(12):LABEL=OVER_120:,SALES_MTD:N(12):LABEL=SALES_MTD:,SALES_YTD:N(12):LABEL=SALES_YTD:,SALES_LY:N(12):LABEL=SALES_LY:,LAST_PURCH_DATE:N(7):LABEL=LAST_PURCH_DATE:,LAST_PAY_DATE:N(7):LABEL=LAST_PAY_DATE:,CREDIT_CODE:C(2):LABEL=CREDIT_CODE:");
    	
    	DataRow selection = new DataRow();
    	selection.setFieldValue("CUST_NUM", "");
    	selection.setFieldValue("FIRST_NAME", "");
    	selection.setFieldValue("LAST_NAME", "");
    	importer.setFieldSelection(selection);
    	
    	importer.setOffset(10, 10);
    	
    	//DataRow filter = new DataRow();
    	//filter.setFieldValue(JLibResultSetImporter.FILTER_KNUM, "0");
    	//filter.setFieldValue(JLibResultSetImporter.FILTER_RANGE_FROM, "000020");
    	//filter.setFieldValue(JLibResultSetImporter.FILTER_RANGE_TO, "000040");
    	//importer.setFilter(filter);
    			
    	ResultSet set = importer.retrieve();
    	System.out.println(set.toJson());
    }
    
}
