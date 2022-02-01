package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "gstr3bduedate")
public class Gstr3bDueDateConfig extends Base{

	private String code;
	private String name;
	private Integer tin;
	private Integer duedate;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getTin() {
		return tin;
	}
	public void setTin(Integer tin) {
		this.tin = tin;
	}
	public Integer getDuedate() {
		return duedate;
	}
	public void setDuedate(Integer duedate) {
		this.duedate = duedate;
	}
	
	
}
