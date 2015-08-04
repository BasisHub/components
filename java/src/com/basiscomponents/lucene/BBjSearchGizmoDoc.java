package com.basiscomponents.lucene;

import java.io.IOException;
import java.util.ArrayList;

public class BBjSearchGizmoDoc {

	private String id;
	private ArrayList<BBjSearchGizmoDocField> fields = new ArrayList<>();

	public String getId() {
		try {
			return new String(new sun.misc.BASE64Decoder().decodeBuffer(id));
		} catch (IOException e) {
			System.err.println("Cant decode!");
		}
		return id;
	}

	public String getInternalId() {
		return id;
	}

	public ArrayList<BBjSearchGizmoDocField> getFields() {
		return fields;
	}

	public BBjSearchGizmoDoc(String id) {
		this.id = new sun.misc.BASE64Encoder().encode(id.getBytes());
	}

	public void addField(String name, String content, float boost) {
		this.addField(name, content, boost, false);

	}

	public void addField(String name, String content) {
		this.addField(name, content, 1, false);
	}

	public void addField(String name, String content, float boost,
			boolean isfacet) {
		BBjSearchGizmoDocField f = new BBjSearchGizmoDocField(name, content,
				boost, isfacet);
		fields.add(f);
	}

}
