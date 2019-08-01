package com.basiscomponents.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import com.basiscomponents.db.ResultSet;
import com.basiscomponents.db.util.ResultSetJsonMapper;

public class RestClient {
	
	private String endpoint;
	private String user;
	private String password;
		
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
		
	}

	public void setUsername(String user) {
		this.user = user;
		
	}

	public void setPassword(String password) {
		this.password = password;
		
	}

	public ResultSet get(String path, String request_parms) throws Exception {
		
		String u = this.endpoint + path;
		if (request_parms != null && request_parms.length()>0)
				u=u+"?"+request_parms;
		
		URL url = new URL(u);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		String encoding = Base64.getEncoder().encodeToString((this.user + ":" + this.password).getBytes());
		
		conn.setRequestProperty("Authorization", "Basic " + encoding);		
		conn.setRequestProperty("Accept", "application/json");


		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
			(conn.getInputStream())));
		
		StringBuilder sb = new StringBuilder();
		String output;
		while ((output = br.readLine()) != null) {
			sb.append(output);
		}

		conn.disconnect();
		ResultSet rs = ResultSetJsonMapper.fromJson(sb.toString());
		
		return rs;
	}

}
