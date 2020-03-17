package com.basiscomponents.db.datarowtest;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;

public class FromJsonTest {
	
	@Test
	public void testIssue165() throws Exception {
		String json="{\"id\":103263057,\"name\":\"BBjGridExWidget\",\"project_url\":\"https://github.com/BBj-Plugins/BBjGridExWidget\",\"installation_date\":\"Tue, 10 Dec 2019 10:13:00\",\"tag\":{\"bbj_dependency\":\"18.10\",\"tag_name\":\"0.100.2\",\"last_change\":\"Wed, 25 Sep 2019 20:00:08\",\"plugin_dependencies\":[{\"plugin_name\":\"BBjWidget\",\"project_url\":\"https://github.com/BBj-Plugins/BBjWidget\",\"plugin_version\":\"master\"}]}}";
		DataRow dr = DataRow.fromJson(json);
		System.out.println(dr);
//		if (dr.contains("tag"))
//				json!=str(dr!.getField("tag"))
//				dr!=DataRow.fromJson(json!)
//				if ok then
//				if dr!.contains("tag_name")
//					version!=str(dr!.getField("tag_name"))
//				?version!
		
		
		json = "[{\"orgHierarchy\":[\"Erica Rogers\",\"Malcolm Barrett\",\"Esther Baker\",\"Brittany Hanson\"],\"jobTitle\":\"Fleet Coordinator\",\"employmentType\":\"Permanent\"}]";
		dr = DataRow.fromJson(json);
		System.out.println(dr.getField("orgHierarchy").getObject().getClass());
		
		System.out.println(dr.toJson());
		
		
	}

}
