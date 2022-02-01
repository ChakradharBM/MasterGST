package com.mastergst.usermanagement.runtime.domain;

public class AcknowledgementPermission {

	private String category;
	private String name;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "AcknowledgementPermission [category=" + category + ", name=" + name + "]";
	}
}
