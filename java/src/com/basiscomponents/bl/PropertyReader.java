package com.basiscomponents.bl;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Collections;

import com.basis.bbj.client.datatypes.BBjVector;

public class PropertyReader {

	private HashMap<String, LinkedHashMap<BBjVector, BBjVector>> finalMap = new HashMap<String, LinkedHashMap<BBjVector, BBjVector>>() ;
	private String filename;
	private String lang;
	
	private static final String PROPERTIES  = ".properties";
	
	/**
	 * Creates the PropertyReader object which contains a HashMap with all the attribute Lists.
	 * 
	 * @param filename - The name of the property file without any language specifications
	 * @param lang - The desired language
	 */
	public PropertyReader(String filename, String lang){
		this.filename = filename;
		this.lang = lang;
		reload();
	}
	

	/**
	 * Returns a properties object for the given filename if the property file was found.
	 * @param filename - The properties filename
	 * @return properties object
	 * @throws IOException 
	 */
	private Properties getProperties(String filename) {
		try {
			FileInputStream fileInput = new FileInputStream(filename);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
			return properties;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Creates the Map which will contain all the attribute lists in the desired language,
	 * and with alphabetically ordered values
	 * 
	 * @param desiredLangMap - The map which contains every key value pairs of any attribute found in the property file
	 */
	@SuppressWarnings("unchecked")
	private void createFinalMap(HashMap<String, HashMap<String,String>> desiredLangMap){
		Iterator<String> it = desiredLangMap.keySet().iterator();
		HashMap<String,String> tmpMap;
		LinkedHashMap<BBjVector, BBjVector> tmpVectorMap;
		
		BBjVector keyVector;
		BBjVector valueVector;
		
		while(it.hasNext()){
			String keyname = it.next();
			tmpMap = desiredLangMap.get(keyname);
			
			keyVector = new BBjVector();
			valueVector = new BBjVector();
			
			ArrayList<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>( tmpMap.entrySet());
	        Collections.sort( list, new Comparator<Map.Entry<String, String>>(){
	            public int compare( Map.Entry<String, String> o1, Map.Entry<String, String> o2 ){
	                return (o1.getValue()).compareToIgnoreCase(o2.getValue());
	            }
	        } );
	       
	        for (Map.Entry<String, String> entry : list){
	        	keyVector.add(entry.getKey());
				valueVector.add(entry.getValue());
	        }
			
			tmpVectorMap = new LinkedHashMap<BBjVector, BBjVector>();
			tmpVectorMap.put(keyVector, valueVector);
			finalMap.put(keyname,tmpVectorMap);
		}
	}
	
	/**
	 * Returns the ID of the given Key. For example : for the key color_1 the method returns 1.
	 * The method returns null if the given name does'nt contain the following character: "_"
	 * @param key
	 * @return id | null
	 */
	private String getKeyID(String key){
		if(key.contains("_")){
			return key.substring(key.lastIndexOf("_") +1);
		}
		return null;
	}
	
	/**
	 * Returns the name of the given Key. For example : for the key color_1 the method returns color.
	 * The method returns null if the given name does'nt contain the following character: "_"
	 * @param key
	 * @return id | null
	 */

	private String getKeyName(String key){
		if(key.contains("_")){
			return key.substring(0, key.lastIndexOf("_"));
		}
		return null;
	}
	
	/**
	 * Returns a BBjVector with the found values sorted alphabetically or null if nothing was found for the given attribute name.
	 * 
	 * @param attrname - The attribute's name 
	 * @return vector which contains all values for the given attribute name or null if nothing was found
	 */
	public BBjVector getAttributes(String attrname){
		LinkedHashMap<BBjVector, BBjVector> map = finalMap.get(attrname);
		if(finalMap.containsKey(attrname)){
			for(Map.Entry<BBjVector, BBjVector> entrySet : map.entrySet()){
				return (BBjVector) entrySet.getValue().clone();
			}
		}
		return null;
	}
	
	/**
	 * Returns the value for the given attribute name and the specified position.
	 * 
	 * @param attrname - The attribute's name 
	 * @param id - The position of the key 
	 * @return key or null if nothing was found
	 * @throws IndexOutOfBoundException
	 */
	public String getAttribute(String attrname, int id){
		LinkedHashMap<BBjVector, BBjVector> map = finalMap.get(attrname);
		if(finalMap.containsKey(attrname)){
			for(Map.Entry<BBjVector, BBjVector> entrySet : map.entrySet()){
				return (String)entrySet.getValue().get(id);
			}
		}
		return null; 
	}
	
	/**
	 * Returns the key for the given attribute name and the specified position.
	 * 
	 * @param attrname - The attribute's name 
	 * @param id - The position of the key 
	 * @return key or null if nothing was found
	 * @throws IndexOutOfBoundException
	 */
	public String getAttributeKey(String attrname, int id){
		LinkedHashMap<BBjVector, BBjVector> map = finalMap.get(attrname);
		if(finalMap.containsKey(attrname)){
			for(Map.Entry<BBjVector, BBjVector> entrySet : map.entrySet()){
				return (String)entrySet.getKey().get(id);
			}
		}
		return null;
	}
	
	/**
	 * Reloads the property file and all its content to rebuild the map with all the attributes
	 */
	public void reload(){
		Properties properties = getProperties(filename + PROPERTIES);
		Properties desiredproperties = null;
		if(!lang.isEmpty()){
			desiredproperties = getProperties(filename + "_" + lang + PROPERTIES);
		}
			
		HashMap<String, HashMap<String,String>> desiredLangMap = new HashMap<String, HashMap<String,String>>() ;
		
		Iterator<Object> it = properties.keySet().iterator();
		HashMap<String, String> tmpMap;
		String key;
		String defaultValue;
		String desiredValue = null;
		while (it.hasNext()) {
			key = (String) it.next();
			
			defaultValue = properties.getProperty(key);
			if(desiredproperties != null){
				desiredValue = desiredproperties.getProperty(key);
			}
			
			String keyName = getKeyName(key);
			String keyID = getKeyID(key);
			if(keyName == null || keyID == null){
				continue;
			}
			
			if(desiredValue == null){
				desiredValue = defaultValue;
			}
			
			if(desiredLangMap.containsKey(keyName)){
				tmpMap = desiredLangMap.get(keyName);
				tmpMap.put(keyID, desiredValue);
			}else{					
				tmpMap = new LinkedHashMap<String, String>();
				tmpMap.put(keyID, desiredValue);
				desiredLangMap.put(keyName, tmpMap);
			}
		}
		createFinalMap(desiredLangMap);
	}
	
}

