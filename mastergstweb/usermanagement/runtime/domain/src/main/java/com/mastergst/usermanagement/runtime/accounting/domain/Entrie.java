package com.mastergst.usermanagement.runtime.accounting.domain;

public class Entrie {

	private String name;
	private Double value;

	public Entrie() {
		super();
	}

	public Entrie(String name, Double value) {
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

	@Override
	public String toString() {
		return "Entrie [name=" + name + ", value=" + value + "]";
	}
}
