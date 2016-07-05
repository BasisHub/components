package com.basiscomponents.db.constants;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class SimpleConstantsResolver implements ConstantsResolver{

	private HashMap<String,String> Constants;
	
	public SimpleConstantsResolver() {
		this.Constants = new HashMap<String, String>();
	}
	
	public void put(String con, String val){
		this.Constants.put(con, val);
	}
	
	public void remove(String con){
		this.Constants.remove(con);
	}
	
	@Override
	public String resolveConstants(String in) {
		if (in.matches(".*\\[\\[.*\\]\\].*")){
			String out = in;
			
			Iterator<Entry<String, String>> it = this.Constants.entrySet().iterator();
			while (it.hasNext()){
				Entry<String, String> e = it.next();
				out = out.replaceAll("\\[\\["+e.getKey()+"\\]\\]"  , e.getValue());
			}
			
			return out;
		}
		else 
			return in;
	}

}
