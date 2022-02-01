package com.mastergst.usermanagement.runtime.domain;

public class MgstResponse {

	public static final String SUCCESS = "success";
	public static final String FAIL = "fail";
	private String status;
	private Object body;
	
	public MgstResponse(){}

	public MgstResponse(String status){
		this.status = status;
	}
	public MgstResponse(Object body, String status){
		this.body = body;
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
}
