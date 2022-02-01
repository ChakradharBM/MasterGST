/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * OTP information.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
@Document(collection = "otp")
public class OTP extends Base {

	private String userid;
	private String otp;
	private String otp1;
	private String otp2;
	private String otp3;
	private String otp4;
	private String status;

	public String getOtp1() {
		return otp1;
	}

	public void setOtp1(String otp1) {
		this.otp1 = otp1;
	}

	public String getOtp2() {
		return otp2;
	}

	public void setOtp2(String otp2) {
		this.otp2 = otp2;
	}

	public String getOtp3() {
		return otp3;
	}

	public void setOtp3(String otp3) {
		this.otp3 = otp3;
	}

	public String getOtp4() {
		return otp4;
	}

	public void setOtp4(String otp4) {
		this.otp4 = otp4;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
