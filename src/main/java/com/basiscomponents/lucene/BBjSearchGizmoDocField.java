package com.basiscomponents.lucene;

class BBjSearchGizmoDocField {
	private String name;
	private String content;
	private float boost;
	private boolean facet = false;

	@SuppressWarnings("unused")
	private BBjSearchGizmoDocField() {
	}

	public BBjSearchGizmoDocField(String name, String content, float boost,
			boolean faceting) {
		// this(name,content,boost);
		this.setName(name);
		this.setContent(content);
		this.setBoost(boost);
		this.setFacet(faceting);

	}

	public BBjSearchGizmoDocField(String name, String content, float boost) {
		this.setName(name);
		this.setContent(content);
		this.setBoost(boost);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public float getBoost() {
		return boost;
	}

	public void setBoost(float boost) {
		this.boost = boost;
	}

	public boolean isFacet() {
		return facet;
	}

	public void setFacet(boolean facet) {
		this.facet = facet;
	}
}