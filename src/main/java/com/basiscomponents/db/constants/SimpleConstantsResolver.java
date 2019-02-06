package com.basiscomponents.db.constants;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The SimpleConstantResolver provides methods to define Constants([[CONSTANT_NAME]])
 * with values. The class also has a method to replace those constants from a String with the defined values.
 */
public class SimpleConstantsResolver implements ConstantsResolver{

	private Map<String, String> constants;
	
	/**
	 * Initializes the SimpleConstantResolver.
	 */
	public SimpleConstantsResolver() {
		this.constants = new HashMap<>();
	}
	
	/**
	 * Adds the given constant with the given value to the list of constants to replace.
	 * 
	 * @param con The name of the constant.
	 * @param val The value of the constant.
	 */
	public void put(String con, String val){
		this.constants.put(con, val);
	}
	
	/**
	 * Removes the given constant name from the list of constant names.
	 * 
	 * @param con The name of the constant to remove.
	 */
	public void remove(String con){
		this.constants.remove(con);
	}
	
	@Override
	public String resolveConstants(final String in) {
		if (in.matches("(?s).*\\[\\[.*\\]\\].*")){
			String out = in;
			
			for (Entry<String, String> e : this.constants.entrySet()) {
				out = out.replaceAll("\\[\\["+e.getKey()+"\\]\\]"  , e.getValue());
			}
			return out;
		}
		else 
			return in;
	}
}
