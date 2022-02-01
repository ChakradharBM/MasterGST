/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Header keys
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
@Document(collection = "headerkeys")
public class HeaderKeys extends Base {

	private String userid;
	private String username;
	private String statecd;
	private String appkey;
	private String appkeyencoded;
	private String authtoken;
	private String sek;
	private String clientid;
	private String clientsecret;
	private String userclientid;
	private String userclientsecret;
	private String contenttype;
	private String ipusr;
	private String txn;

	private String email;
	private String gstusername;
	private long starttime;
	private long endtime;
	private int expiry;

	private String headerid;
	private String headerKeysType;
	
	private String einvoiceTokenExpiry;
	private String refreshtokeneror;
	
	public HeaderKeys(String userid, String username, String statecd, String appkey, String appkeyencoded,
			String authtoken, String sek, String clientid, String clientsecret, String userclientid,
			String userclientsecret, String contenttype, String ipusr, String txn, String email, String gstusername,
			long starttime, long endtime, int expiry) {
		this.userid = userid;
		this.username = username;
		this.statecd = statecd;
		this.appkey = appkey;
		this.appkeyencoded = appkeyencoded;
		this.authtoken = authtoken;
		this.sek = sek;
		this.clientid = clientid;
		this.clientsecret = clientsecret;
		this.userclientid = userclientid;
		this.userclientsecret = userclientsecret;
		this.contenttype = contenttype;
		this.ipusr = ipusr;
		this.txn = txn;
		this.email = email;
		this.gstusername = gstusername;
		this.starttime = starttime;
		this.endtime = endtime;
		this.expiry = expiry;
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

	public String getAppkey() {
		return appkey;
	}

	public void setAppkey(String appkey) {
		this.appkey = appkey;
	}

	public String getAppkeyencoded() {
		return appkeyencoded;
	}

	public void setAppkeyencoded(String appkeyencoded) {
		this.appkeyencoded = appkeyencoded;
	}

	public String getAuthtoken() {
		return authtoken;
	}

	public void setAuthtoken(String authtoken) {
		this.authtoken = authtoken;
	}

	public String getSek() {
		return sek;
	}

	public void setSek(String sek) {
		this.sek = sek;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getClientsecret() {
		return clientsecret;
	}

	public void setClientsecret(String clientsecret) {
		this.clientsecret = clientsecret;
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

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
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

	public String getContenttype() {
		return contenttype;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGstusername() {
		return gstusername;
	}

	public void setGstusername(String gstusername) {
		this.gstusername = gstusername;
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

	public int getExpiry() {
		return expiry;
	}

	public void setExpiry(int expiry) {
		this.expiry = expiry;
	}

	public String getHeaderid() {
		return headerid;
	}

	public void setHeaderid(String headerid) {
		this.headerid = headerid;
	}

	public String getHeaderKeysType() {
		return headerKeysType;
	}

	public void setHeaderKeysType(String headerKeysType) {
		this.headerKeysType = headerKeysType;
	}

	public String getEinvoiceTokenExpiry() {
		return einvoiceTokenExpiry;
	}

	public void setEinvoiceTokenExpiry(String einvoiceTokenExpiry) {
		this.einvoiceTokenExpiry = einvoiceTokenExpiry;
	}

	public String getRefreshtokeneror() {
		return refreshtokeneror;
	}

	public void setRefreshtokeneror(String refreshtokeneror) {
		this.refreshtokeneror = refreshtokeneror;
	}
	
	
}
