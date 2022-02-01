/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This class Error POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Error {

	@JsonProperty("message")
	private String message;

	@JsonProperty("error_cd")
	private String errorcd;
	
	@JsonProperty("code")
	private String code;

	@JsonProperty("desc")
	private String desc;

	public Error() {

	}

	public Error(String message, String errorcd) {
		this.message = message;
		this.errorcd = errorcd;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getErrorcd() {
		return errorcd;
	}

	public void setErrorcd(String errorcd) {
		this.errorcd = errorcd;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "Error [" + (message != null ? "message=" + message + ", " : "")
				+ (errorcd != null ? "errorcd=" + errorcd + ", " : "") + (code != null ? "code=" + code + ", " : "")
				+ (desc != null ? "desc=" + desc : "") + "]";
	}
}
