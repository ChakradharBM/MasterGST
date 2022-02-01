/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;


public class TrueCopyResponse {

	private String authstr;
	private String cs;
	private String hash256;
	private String content;
	
	public String getAuthstr() {
		return authstr;
	}
	public void setAuthstr(String authstr) {
		this.authstr = authstr;
	}
	public String getCs() {
		return cs;
	}
	public void setCs(String cs) {
		this.cs = cs;
	}
	public String getHash256() {
		return hash256;
	}
	public void setHash256(String hash256) {
		this.hash256 = hash256;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}
