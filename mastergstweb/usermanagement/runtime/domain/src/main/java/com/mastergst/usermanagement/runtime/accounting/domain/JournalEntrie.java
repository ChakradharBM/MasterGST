package com.mastergst.usermanagement.runtime.accounting.domain;

public class JournalEntrie {

	private String name;
	private Double value;
	
	public JournalEntrie() {
		super();
	}
	
	public JournalEntrie(String name, Double value) {
		super();
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	/*
	 * private List<ToEnterie> toEnterie; private List<ByEnterie> byEnterie;
	 * 
	 * public List<ToEnterie> getToEnterie() { return toEnterie; }
	 * 
	 * public void setToEnterie(List<ToEnterie> toEnterie) { this.toEnterie =
	 * toEnterie; }
	 * 
	 * public List<ByEnterie> getByEnterie() { return byEnterie; }
	 * 
	 * public void setByEnterie(List<ByEnterie> byEnterie) { this.byEnterie =
	 * byEnterie; }
	 */
}