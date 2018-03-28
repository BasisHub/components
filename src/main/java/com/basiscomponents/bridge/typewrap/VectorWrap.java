package com.basiscomponents.bridge.typewrap;

import java.util.Iterator;
import java.util.List;

import com.basis.bbj.client.datatypes.BBjVector;
import org.apache.commons.lang3.StringEscapeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class VectorWrap extends BBjVector {

	private static final long serialVersionUID = 2683282001767043130L;
	
	public VectorWrap() {
	}
	
	@SuppressWarnings("rawtypes")
	public static VectorWrap fromBBjVector(BBjVector v){
		VectorWrap vw = new VectorWrap();
		
		Iterator it = v.iterator();
		while (it.hasNext())
			vw.addItem(it.next());
		
		return vw;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static VectorWrap fromJson(String json) {

		Gson gson = new Gson();
		JsonElement jelement = new JsonParser().parse(json);

		JsonArray jarray = jelement.getAsJsonArray();
		List<Object> records = (List<Object>) gson.fromJson(jarray, java.lang.Object.class);
		
		VectorWrap vw = new VectorWrap();

		if (records != null){
			Iterator it = records.iterator();
			while (it.hasNext()) {
				vw.addItem(it.next());

			}
		}

		return vw;
	}	

	@SuppressWarnings("rawtypes")
	public String toJson() {
		// TODO Auto-generated method stub

		String ret = new String("[");
		Iterator it = this.iterator();
		Boolean first = true;
		while (it.hasNext())
		{
				
				Object o = it.next();
				String tmp = o.toString();
				tmp = StringEscapeUtils.escapeEcmaScript(tmp);
				Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
						.create();
				String json = gson.toJson(o);
				json = json.replace("\\\\u", "\\u");

				if (first)
					first = false;
				else
					ret +=",";
				
				
				ret+=json;
			
		}
			ret += "]";
		
		return ret;
	}
	
	@SuppressWarnings("rawtypes")
	public JsonElement toJsonElement() {

		JsonArray jsonarray = new JsonArray();

		Iterator it = iterator();
		while (it.hasNext()) {
			Object o = it.next();
			Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation()
					.create();
			
			jsonarray.add( gson.toJsonTree(o)); 
		}

		return jsonarray;
	}

}
