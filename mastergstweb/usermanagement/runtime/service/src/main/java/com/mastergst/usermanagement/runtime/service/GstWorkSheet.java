package com.mastergst.usermanagement.runtime.service;

import java.util.ArrayList;
import java.util.List;

public class GstWorkSheet {
	
	private String name;
	private String items;
	private String var;
	private String varType;
	
	public GstWorkSheet(String name){
		this.name = name;
	}
	private List<Field> fields = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getItems() {
		return items;
	}

	public void setItems(String items) {
		this.items = items;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getVarType() {
		return varType;
	}

	public void setVarType(String varType) {
		this.varType = varType;
	}

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	public void addFields(Field field) {
		this.fields.add(field);
	}
	
	
	

}
