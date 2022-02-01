/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.domain;
import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * User keys
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
@Document(collection = "userkeys")
public class UserKeys extends Base implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String keyname;
	private String stage;
	private String clientid;
	private String clientsecret;
	private String createdate;
	private boolean isenabled;
	private String partnerid;
	private String gstusername1;
	private String gstusername2;
	private String gstusername3;
	private String gstusername4;
	private String gstinno1;
	private String gstinno2;
	private String gstinno3;
	private String gstinno4;
	
	private String gsttdsusername1;
	private String gsttdsusername2;
	private String gsttdsusername3;
	private String gsttdsusername4;
	private String tdsgstinno1;
	private String tdsgstinno2;
	private String tdsgstinno3;
	private String tdsgstinno4;
	
	private String gsttcsusername1;
	private String gsttcsusername2;
	private String gsttcsusername3;
	private String gsttcsusername4;
	private String tcsgstinno1;
	private String tcsgstinno2;
	private String tcsgstinno3;
	private String tcsgstinno4;
	
	private String gstisdusername1;
	private String gstisdusername2;
	private String gstisdusername3;
	private String gstisdusername4;
	private String isdgstinno1;
	private String isdgstinno2;
	private String isdgstinno3;
	private String isdgstinno4;
	
	private String gstcompusername1;
	private String gstcompusername2;
	private String gstcompusername3;
	private String gstcompusername4;
	private String compgstinno1;
	private String compgstinno2;
	private String compgstinno3;
	private String compgstinno4;

	private String username;
	private String password;
	
	private String gstclientid;
	private String gstclientsecret;
	
	
	public UserKeys() {
		this.id = ObjectId.get();
	}

	public String getKeyname() {
		return keyname;
	}

	public void setKeyname(String keyname) {
		this.keyname = keyname;
	}

	public String getStage() {
		return stage;
	}

	public void setStage(String stage) {
		this.stage = stage;
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

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public boolean isIsenabled() {
		return isenabled;
	}

	public void setIsenabled(boolean isenabled) {
		this.isenabled = isenabled;
	}

	public String getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}
	
	public String getGstusername1() {
		return gstusername1;
	}
	
	public void setGstusername1(String gstusername1) {
		this.gstusername1 = gstusername1;
	}
	public String getGstusername2() {
		return gstusername2;
	}
	
	public void setGstusername2(String gstusername2) {
		this.gstusername2 = gstusername2;
	}
	public String getGstusername3() {
		return gstusername3;
	}
	
	public void setGstusername3(String gstusername3) {
		this.gstusername3 = gstusername3;
	}
	public String getGstusername4() {
		return gstusername4;
	}
	
	public void setGstusername4(String gstusername4) {
		this.gstusername4 = gstusername4;
	}
	public String getGstinno1() {
		return gstinno1;
	}
	
	public void setGstinno1(String gstinno1) {
		this.gstinno1 = gstinno1;
	}
	public String getGstinno2() {
		return gstinno2;
	}
	
	public void setGstinno2(String gstinno2) {
		this.gstinno2 = gstinno2;
	}
	public String getGstinno3() {
		return gstinno3;
	}
	
	public void setGstinno3(String gstinno3) {
		this.gstinno3 = gstinno3;
	}
	public String getGstinno4() {
		return gstinno4;
	}
	
	public void setGstinno4(String gstinno4) {
		this.gstinno4 = gstinno4;
	}

	public String getGsttdsusername1() {
		return gsttdsusername1;
	}

	public void setGsttdsusername1(String gsttdsusername1) {
		this.gsttdsusername1 = gsttdsusername1;
	}

	public String getGsttdsusername2() {
		return gsttdsusername2;
	}

	public void setGsttdsusername2(String gsttdsusername2) {
		this.gsttdsusername2 = gsttdsusername2;
	}

	public String getGsttdsusername3() {
		return gsttdsusername3;
	}

	public void setGsttdsusername3(String gsttdsusername3) {
		this.gsttdsusername3 = gsttdsusername3;
	}

	public String getGsttdsusername4() {
		return gsttdsusername4;
	}

	public void setGsttdsusername4(String gsttdsusername4) {
		this.gsttdsusername4 = gsttdsusername4;
	}

	public String getTdsgstinno1() {
		return tdsgstinno1;
	}

	public void setTdsgstinno1(String tdsgstinno1) {
		this.tdsgstinno1 = tdsgstinno1;
	}

	public String getTdsgstinno2() {
		return tdsgstinno2;
	}

	public void setTdsgstinno2(String tdsgstinno2) {
		this.tdsgstinno2 = tdsgstinno2;
	}

	public String getTdsgstinno3() {
		return tdsgstinno3;
	}

	public void setTdsgstinno3(String tdsgstinno3) {
		this.tdsgstinno3 = tdsgstinno3;
	}

	public String getTdsgstinno4() {
		return tdsgstinno4;
	}

	public void setTdsgstinno4(String tdsgstinno4) {
		this.tdsgstinno4 = tdsgstinno4;
	}

	public String getGsttcsusername1() {
		return gsttcsusername1;
	}

	public void setGsttcsusername1(String gsttcsusername1) {
		this.gsttcsusername1 = gsttcsusername1;
	}

	public String getGsttcsusername2() {
		return gsttcsusername2;
	}

	public void setGsttcsusername2(String gsttcsusername2) {
		this.gsttcsusername2 = gsttcsusername2;
	}

	public String getGsttcsusername3() {
		return gsttcsusername3;
	}

	public void setGsttcsusername3(String gsttcsusername3) {
		this.gsttcsusername3 = gsttcsusername3;
	}

	public String getGsttcsusername4() {
		return gsttcsusername4;
	}

	public void setGsttcsusername4(String gsttcsusername4) {
		this.gsttcsusername4 = gsttcsusername4;
	}

	public String getTcsgstinno1() {
		return tcsgstinno1;
	}

	public void setTcsgstinno1(String tcsgstinno1) {
		this.tcsgstinno1 = tcsgstinno1;
	}

	public String getTcsgstinno2() {
		return tcsgstinno2;
	}

	public void setTcsgstinno2(String tcsgstinno2) {
		this.tcsgstinno2 = tcsgstinno2;
	}

	public String getTcsgstinno3() {
		return tcsgstinno3;
	}

	public void setTcsgstinno3(String tcsgstinno3) {
		this.tcsgstinno3 = tcsgstinno3;
	}

	public String getTcsgstinno4() {
		return tcsgstinno4;
	}

	public void setTcsgstinno4(String tcsgstinno4) {
		this.tcsgstinno4 = tcsgstinno4;
	}

	public String getGstisdusername1() {
		return gstisdusername1;
	}

	public void setGstisdusername1(String gstisdusername1) {
		this.gstisdusername1 = gstisdusername1;
	}

	public String getGstisdusername2() {
		return gstisdusername2;
	}

	public void setGstisdusername2(String gstisdusername2) {
		this.gstisdusername2 = gstisdusername2;
	}

	public String getGstisdusername3() {
		return gstisdusername3;
	}

	public void setGstisdusername3(String gstisdusername3) {
		this.gstisdusername3 = gstisdusername3;
	}

	public String getGstisdusername4() {
		return gstisdusername4;
	}

	public void setGstisdusername4(String gstisdusername4) {
		this.gstisdusername4 = gstisdusername4;
	}

	public String getIsdgstinno1() {
		return isdgstinno1;
	}

	public void setIsdgstinno1(String isdgstinno1) {
		this.isdgstinno1 = isdgstinno1;
	}

	public String getIsdgstinno2() {
		return isdgstinno2;
	}

	public void setIsdgstinno2(String isdgstinno2) {
		this.isdgstinno2 = isdgstinno2;
	}

	public String getIsdgstinno3() {
		return isdgstinno3;
	}

	public void setIsdgstinno3(String isdgstinno3) {
		this.isdgstinno3 = isdgstinno3;
	}

	public String getIsdgstinno4() {
		return isdgstinno4;
	}

	public void setIsdgstinno4(String isdgstinno4) {
		this.isdgstinno4 = isdgstinno4;
	}

	public String getGstcompusername1() {
		return gstcompusername1;
	}

	public void setGstcompusername1(String gstcompusername1) {
		this.gstcompusername1 = gstcompusername1;
	}

	public String getGstcompusername2() {
		return gstcompusername2;
	}

	public void setGstcompusername2(String gstcompusername2) {
		this.gstcompusername2 = gstcompusername2;
	}

	public String getGstcompusername3() {
		return gstcompusername3;
	}

	public void setGstcompusername3(String gstcompusername3) {
		this.gstcompusername3 = gstcompusername3;
	}

	public String getGstcompusername4() {
		return gstcompusername4;
	}

	public void setGstcompusername4(String gstcompusername4) {
		this.gstcompusername4 = gstcompusername4;
	}

	public String getCompgstinno1() {
		return compgstinno1;
	}

	public void setCompgstinno1(String compgstinno1) {
		this.compgstinno1 = compgstinno1;
	}

	public String getCompgstinno2() {
		return compgstinno2;
	}

	public void setCompgstinno2(String compgstinno2) {
		this.compgstinno2 = compgstinno2;
	}

	public String getCompgstinno3() {
		return compgstinno3;
	}

	public void setCompgstinno3(String compgstinno3) {
		this.compgstinno3 = compgstinno3;
	}

	public String getCompgstinno4() {
		return compgstinno4;
	}

	public void setCompgstinno4(String compgstinno4) {
		this.compgstinno4 = compgstinno4;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getGstclientid() {
		return gstclientid;
	}

	public void setGstclientid(String gstclientid) {
		this.gstclientid = gstclientid;
	}

	public String getGstclientsecret() {
		return gstclientsecret;
	}

	public void setGstclientsecret(String gstclientsecret) {
		this.gstclientsecret = gstclientsecret;
	}
	
	
}