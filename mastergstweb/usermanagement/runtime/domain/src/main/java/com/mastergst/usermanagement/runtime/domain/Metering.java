/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Metering keys
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "metering")
public class Metering extends Base {
	
	
	
	@Override
	public String toString() {
		return "Metering [status=" + status + ", email=" + email + ", id=" + id + "]";
	}

	private String userid;
	private String username;
	private String statecd;
	private String ipusr;
	private String txn;
	private String servicename;
	private String userclientid;
	private String userclientsecret;
	private long starttime;
	private long endtime;
	private String status;
	private String type;
	private String email;
	private String errorMessage;
	private long size;
	private String stage;

	private String gstnusername;
	private String statecode;
	
	private String meterid;
	private String cost;
	
	public Metering() {
	}

	// TODO REMOVE
	public Metering(String userid, String username, String statecd, String ipusr, String txn, String servicename,
			String userclientid, String userclientsecret, long starttime, long endtime, String status, String type) {
		this.userid = userid;
		this.username = username;
		this.statecd = statecd;
		this.ipusr = ipusr;
		this.txn = txn;
		this.servicename = servicename;
		this.userclientid = userclientid;
		this.userclientsecret = userclientsecret;
		this.starttime = starttime;
		this.endtime = endtime;
		this.status = status;
		this.type = type;
	}
	
	public Metering(String userid, String username, String email, String userclientid, String userclientsecret,
			String ipusr, String txn, String gstnusername, String statecode, String servicename, long starttime,
			long endtime, String status, String type, String errorMessage) {
		this.userid = userid;
		this.username = username;
		this.email = email;
		this.userclientid = userclientid;
		this.userclientsecret = userclientsecret;
		this.ipusr = ipusr;
		this.txn = txn;
		this.gstnusername = gstnusername;
		this.statecode = statecode;
		this.servicename = servicename;
		this.starttime = starttime;
		this.endtime = endtime;
		this.status = status;
		this.type = type;
		this.errorMessage = errorMessage;
	}

	public Metering(String userid, String username, String email, String userclientid, String userclientsecret,
			String ipusr, String txn, String gstnusername, String statecode, String servicename, long starttime,
			long endtime, String status, String type, long size) {
		this.userid = userid;
		this.username = username;
		this.email = email;
		this.userclientid = userclientid;
		this.userclientsecret = userclientsecret;
		this.ipusr = ipusr;
		this.txn = txn;
		this.gstnusername = gstnusername;
		this.statecode = statecode;
		this.servicename = servicename;
		this.starttime = starttime;
		this.endtime = endtime;
		this.status = status;
		this.type = type;
		this.size = size;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatecd() {
		return statecd;
	}

	public void setStatecd(String statecd) {
		this.statecd = statecd;
	}

	public String getIpusr() {
		return ipusr;
	}

	public void setIpusr(String ipusr) {
		this.ipusr = ipusr;
	}

	public String getTxn() {
		return txn;
	}

	public void setTxn(String txn) {
		this.txn = txn;
	}

	public String getServicename() {
		return servicename;
	}

	public void setServicename(String servicename) {
		this.servicename = servicename;
	}

	public long getStarttime() {
		return starttime;
	}

	public void setStarttime(long starttime) {
		this.starttime = starttime;
	}

	public long getEndtime() {
		return endtime;
	}

	public void setEndtime(long endtime) {
		this.endtime = endtime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserclientid() {
		return userclientid;
	}

	public void setUserclientid(String userclientid) {
		this.userclientid = userclientid;
	}

	public String getUserclientsecret() {
		return userclientsecret;
	}

	public void setUserclientsecret(String userclientsecret) {
		this.userclientsecret = userclientsecret;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
	}

	public String getGstnusername() {
		return gstnusername;
	}

	public void setGstnusername(String gstnusername) {
		this.gstnusername = gstnusername;
	}

	public String getStatecode() {
		return statecode;
	}

	public void setStatecode(String statecode) {
		this.statecode = statecode;
	}

	public String getMeterid() {
		return meterid;
	}

	public void setMeterid(String meterid) {
		this.meterid = meterid;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

}
