package com.basiscomponents.bridge;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.DataRowFromJsonMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Session {

	private List<SessionExecuteEntity> Ex;
	private List<String> Ret;
	private List<SessionVarEntity> Vars;
	private Map<String, Object> Result;
	private String SessionID;
	private String BaseUrl;
	private String lastError;

	public Session(String url) {
		BaseUrl = url;
		Ex = new ArrayList<SessionExecuteEntity>();
		Ret = new ArrayList<String>();
		Vars = new ArrayList<SessionVarEntity>();
		SessionID = new String();
		Result = new HashMap<>();
	}

	public String getSessionID() {
		return SessionID;
	}

	public void pushVar(String name, String val) {
		SessionVarEntity v = new SessionVarEntity(name, val);
		Vars.add(v);
	}

	public void pushVar(String name, Number val) {
		SessionVarEntity v = new SessionVarEntity(name, val);
		Vars.add(v);
	}

	public void pushVar(String name, DataRow val) {
		SessionVarEntity v = new SessionVarEntity(name, val);
		Vars.add(v);
	}

	public void pushVar(String name, ResultSet val) {
		SessionVarEntity v = new SessionVarEntity(name, val);
		Vars.add(v);
	}

	public void pushRet(String name) {
		this.Ret.add(name);
	}

	public void create(String var, String classname) {
		SessionCreateClassEntity ex = new SessionCreateClassEntity(var,
				classname);
		this.Ex.add(ex);
	}

	public void invoke(String var, String retvar, String method, String... args) {
		SessionInvokeEntity in = new SessionInvokeEntity(var, retvar, method,
				args);
		this.Ex.add(in);
	}

	public void reset() {
		Ex = new ArrayList<SessionExecuteEntity>();
		Ret = new ArrayList<String>();
		Vars = new ArrayList<SessionVarEntity>();
		lastError = null;
	}

	private String getJson() {
		String json = "{";

		if (!this.SessionID.isEmpty())
			json += "\"ses\":\"" + this.getSessionID() + "\"";

		if (this.Vars.size() > 0) {
			if (json.length() > 1)
				json += ',';
			json += "\"vars\":[";
			java.util.Iterator<SessionVarEntity> it = Vars.iterator();
			Boolean first = true;
			while (it.hasNext()) {
				if (!first)
					json += ',';
				else
					first = false;
				json += it.next().toJson();
			}
			json += "]";

		}

		if (this.Ex.size() > 0) {
			if (json.length() > 1)
				json += ',';
			json += "\"ex\":[";
			java.util.Iterator<SessionExecuteEntity> it = Ex.iterator();
			Boolean first = true;
			while (it.hasNext()) {
				if (!first)
					json += ',';
				else
					first = false;
				json += it.next().toJson();
			}
			json += "]";

		}

		if (this.Ret.size() > 0) {
			if (json.length() > 1)
				json += ',';
			json += "\"ret\":[";
			java.util.Iterator<String> it = Ret.iterator();
			Boolean first = true;
			while (it.hasNext()) {
				if (!first)
					json += ',';
				else
					first = false;
				json += "\"" + it.next() + "\"";
			}
			json += "]";

		}

		json += "}";

		// replace the special chars
		try {
			// the url encoder is a bit overdoing stuff, resulting in request
			// strings longer than necessary...
			// TODO ponder if we should only pick a few chars like %,& etc to
			// encode
			json = java.net.URLEncoder.encode(json, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return json;
	}

	private String postRequest(String req) {

		{
			URL url;
			HttpURLConnection connection = null;
			req = "ex=" + req;
			try {
				// Create connection
				url = new URL(BaseUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");

				connection.setRequestProperty("Content-Length",
						"" + Integer.toString(req.getBytes().length));
				connection.setRequestProperty("Content-Language", "en-US");

				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);

				// Send request
				DataOutputStream wr = new DataOutputStream(
						connection.getOutputStream());
				wr.writeBytes(req);
				wr.flush();
				wr.close();

				// Get Response
				InputStream is = connection.getInputStream();
				BufferedReader rd = new BufferedReader(
						new InputStreamReader(is));
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				return response.toString();

			} catch (Exception e) {

				e.printStackTrace();
				return null;

			} finally {

				if (connection != null) {
					connection.disconnect();
				}
			}
		}

	}

	public void exec()   {

		lastError = null;
		String req = getJson();

		System.out.println("Request: " + java.net.URLDecoder.decode(req));
		String ret = this.postRequest(req);
		System.out.println("Answer:  " + ret);

		JsonElement jelement = new JsonParser().parse(ret);
		JsonObject jobject = jelement.getAsJsonObject();

		if (jobject.get("err") != null) {
			lastError = jobject.get("err").getAsString();
			System.err.println("received remote error: " + lastError);
			return;
		}

		if (this.SessionID.isEmpty())
		{
			SessionID = jobject.get("ses").getAsString();
//			System.out.println("got session "+this.SessionID);
		}
		
		Set<Entry<String, JsonElement>> es = jobject.entrySet();
		java.util.Iterator<Entry<String, JsonElement>> it = es.iterator();
		while (it.hasNext()) {
			String key = it.next().getKey();
			JsonElement el = jobject.get(key);
			if (el.isJsonArray()) {
				// todo implement this inside the ResultSet implementation!!! 
//				ResultSet rs = ResultSet.fromJson(el.getAsJsonArray().toString());
//				System.err.println(rs.toString());
				ResultSet rs = new ResultSet();
				
				
				JsonArray ar = el.getAsJsonArray();
				java.util.Iterator<JsonElement> ia = ar.iterator();
				String metapart = null;
				JsonElement metaelement = null;
				while (ia.hasNext()) {
					JsonElement nextelement = ia.next();
					JsonElement meta = nextelement.getAsJsonObject().get("meta");
					if (metapart == null && meta != null){
						metapart = meta.toString();
						metaelement = meta;
					}
					
					DataRow dr;
					try {
						if (meta != null && metaelement != null){
							dr = DataRowFromJsonMapper.fromJson(nextelement.toString());
							rs.add(dr);
						}
						else
						{	
							nextelement.getAsJsonObject().add("meta", metaelement);
							dr = DataRowFromJsonMapper.fromJson(nextelement.toString());
							rs.add(dr);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					// TODO: add a true from json object method to DataRow
					// TODO: even better move this section to ResultSet

				}
				this.Result.put(key, rs);
			}
			else{
				this.Result.put(key,el.getAsString());
			}
		}
		this.Vars.clear();
		this.Ex.clear();
		this.Ret.clear();

	}

	public Object getResult(String name) {
		//System.out.println(Result);
		return Result.get(name);
	}

	public String toString(){
		return "Session "+this.SessionID+" to "+this.BaseUrl;
	}
	
	public String getLastError(){
		return lastError;
	}
	
}
