package com.basiscomponents.lucene;
import java.util.ArrayList;

public class BBjSearchGizmoDoc 
{
	
	String id;
	ArrayList<BBjSearchGizmoDocField> fields = new ArrayList<>();
	
	public String getId() 
	{
		return id;
	}

	public ArrayList<BBjSearchGizmoDocField> getFields()
	{
		return fields;
	}

	
	public BBjSearchGizmoDoc(String id)
	{
		this.id = id;
	}
	
	public void addField(String name, String content, float boost)
	{
		this.addField(name, content, boost, false);
		
	}
	
	public void addField(String name, String content)
	{
		this.addField(name, content, 1, false);
	}	
	
	public void addField(String name, String content, float boost, boolean isfacet){
		BBjSearchGizmoDocField f = new BBjSearchGizmoDocField(name, content, boost, isfacet);
		fields.add(f);
	}

}
