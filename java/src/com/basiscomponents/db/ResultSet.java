package com.basiscomponents.db;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import com.basis.bbj.client.datatypes.BBjVector;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ResultSet extends BBjVector {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public String toJson(){

		String json="";
		Iterator it = iterator();
		while (it.hasNext())
		{
			DataRow r = (DataRow)it.next();
			if (!json.isEmpty()){
				json +=',';
			}
			json += r.toJson();

			
		}
		
		json = "{\"resultset\":["+json+"]}";
		return json;
		
	}

	@SuppressWarnings("unchecked")
	public JsonElement toJsonElement() {

		JsonArray jsonarray = new JsonArray();
		

		Iterator<DataRow> it = iterator();
		while (it.hasNext())
		{
			DataRow r = (DataRow)it.next();
			JsonElement el = r.toJsonElement();
			JsonObject o = new JsonObject();
			o.add("datarow", el);
			jsonarray.add(o);
		}
		
		return jsonarray;		
	}		

	public JsonElement toJsonElementOld() {
		// TODO Auto-generated method stub
		String json=toJson();
		JsonElement jelement = new JsonParser().parse(json);
	    JsonObject  jobject = jelement.getAsJsonObject();
		return jobject.get("resultset");
	}		
	
	public static ResultSet fromJson(String json){

		ResultSet rs = new ResultSet();
		JsonElement jelement = new JsonParser().parse(json);
	    JsonObject  jobject = jelement.getAsJsonObject();
	    JsonArray jarray = jobject.getAsJsonArray("resultset");
	    Iterator<JsonElement> it = jarray.iterator();
	    while (it.hasNext()){
	    	JsonElement el = it.next();
		    DataRow r = DataRow.fromJson(el.toString());
		    rs.addItem(r);
	    	
	    }
	    

	    return rs;
	}	
	
}
