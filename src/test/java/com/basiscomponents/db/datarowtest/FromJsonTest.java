package com.basiscomponents.db.datarowtest;

import java.io.IOException;
import java.text.ParseException;

import org.junit.jupiter.api.Test;

import com.basiscomponents.db.DataRow;
import com.basiscomponents.db.ResultSet;

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
	
	@Test
	public void testIssue181() throws Exception {
		//issue was that cr / lf / tab charactes in the structure led to issues parsing JSon
		String json = "[\r\n" + 
				"	{\r\n\t" + 
				"        \"CDNUMBER\": \"000001\",\r\n" + 
				"        \"TITLE\": \"Mississippi Blues\",\r\n" + 
				"        \"ARTIST\": \"John Hurt & The Ramblers\",\r\n" + 
				"        \"LABEL\": \"Blind Pig\",\r\n" + 
				"        \"PLAYINGTIME\": \"72\",\r\n" + 
				"        \"RECORDINGTYPE\": \"AAD\",\r\n" + 
				"        \"MUSICTYPE\": \"Blues\",\r\n" + 
				"        \"BINLOCATION\": \"N123\",\r\n" + 
				"        \"NUMBEROFTRACKS\": 12,\r\n" + 
				"        \"ONHAND\": 55,\r\n" + 
				"        \"COST\": 5.99,\r\n" + 
				"        \"RETAIL\": 12.99,\r\n" + 
				"        \"meta\": {\r\n" + 
				"            \"CDNUMBER\": {\r\n" + 
				"                \"ReadOnly\": \"false\",\r\n" + 
				"                \"ColumnType\": \"12\",\r\n" + 
				"                \"Signed\": \"false\",\r\n" + 
				"                \"AutoIncrement\": \"false\",\r\n" + 
				"                \"Writable\": \"true\",\r\n" + 
				"                \"ColumnLabel\": \"CDNUMBER\",\r\n" + 
				"                \"Scale\": \"0\",\r\n" + 
				"                \"StringFormat\": \"\",\r\n" + 
				"                \"TableName\": \"CDINVENTORY\",\r\n" + 
				"                \"ColumnClassName\": \"java.lang.String\",\r\n" + 
				"                \"ColumnDisplaySize\": \"6\",\r\n" + 
				"                \"Precision\": \"6\",\r\n" + 
				"                \"Currency\": \"false\",\r\n" + 
				"                \"CaseSensitive\": \"true\",\r\n" + 
				"                \"CatalogName\": \"CDStore\",\r\n" + 
				"                \"Searchable\": \"true\",\r\n" + 
				"                \"DefinitelyWritable\": \"true\",\r\n" + 
				"                \"SchemaName\": \"nobody\",\r\n" + 
				"                \"Nullable\": \"1\",\r\n" + 
				"                \"EDITABLE\": \"1\"\r\n" + 
				"            },\r\n" + 
				"            \"TITLE\": {\r\n" + 
				"                \"ReadOnly\": \"false\",\r\n" + 
				"                \"ColumnType\": \"12\",\r\n" + 
				"                \"Signed\": \"false\",\r\n" + 
				"                \"AutoIncrement\": \"false\",\r\n" + 
				"                \"Writable\": \"true\",\r\n" + 
				"                \"ColumnLabel\": \"TITLE\",\r\n" + 
				"                \"Scale\": \"0\",\r\n" + 
				"                \"StringFormat\": \"\",\r\n" + 
				"                \"TableName\": \"CDINVENTORY\",\r\n" + 
				"                \"ColumnClassName\": \"java.lang.String\",\r\n" + 
				"                \"ColumnDisplaySize\": \"50\",\r\n" + 
				"                \"Precision\": \"50\",\r\n" + 
				"                \"Currency\": \"false\",\r\n" + 
				"                \"CaseSensitive\": \"true\",\r\n" + 
				"                \"CatalogName\": \"CDStore\",\r\n" + 
				"                \"Searchable\": \"true\",\r\n" + 
				"                \"DefinitelyWritable\": \"true\",\r\n" + 
				"                \"SchemaName\": \"nobody\",\r\n" + 
				"                \"Nullable\": \"1\",\r\n" + 
				"                \"EDITABLE\": \"1\"\r\n" + 
				"            }\r\n"+ 
				"        }\r\n" + 
				"    },\r\n" + 
				"    {\r\n" + 
				"        \"CDNUMBER\": \"000002\",\r\n" + 
				"        \"TITLE\": \"Gold - Greatest Hits\",\r\n" + 
				"        \"ARTIST\": \"ABBA\",\r\n" + 
				"        \"LABEL\": \"Universal\",\r\n" + 
				"        \"PLAYINGTIME\": \"76\",\r\n" + 
				"        \"RECORDINGTYPE\": \"ADD\",\r\n" + 
				"        \"MUSICTYPE\": \"Pop\",\r\n" + 
				"        \"BINLOCATION\": \"ND02\",\r\n" + 
				"        \"NUMBEROFTRACKS\": 19,\r\n" + 
				"        \"ONHAND\": 13,\r\n" + 
				"        \"COST\": 8.73,\r\n" + 
				"        \"RETAIL\": 12.99\r\n" + 
				" 	 }\r\n" + 
				"]";
		ResultSet rs = ResultSet.fromJson(json);
	}

}
