package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BData {
	@Id
	private ObjectId id;
	private String gstin;
	private String rtnprd;
	private String version;
	private String gendt;
	private GSTR2BITCSummary itcsumm;
	private GSTR2BCPSummary cpsumm;
	private GSTR2BDocData docdata;
	private int fc;
	
	public GSTR2BData() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRtnprd() {
		return rtnprd;
	}

	public void setRtnprd(String rtnprd) {
		this.rtnprd = rtnprd;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGendt() {
		return gendt;
	}

	public void setGendt(String gendt) {
		this.gendt = gendt;
	}

	public GSTR2BITCSummary getItcsumm() {
		return itcsumm;
	}

	public void setItcsumm(GSTR2BITCSummary itcsumm) {
		this.itcsumm = itcsumm;
	}

	public GSTR2BCPSummary getCpsumm() {
		return cpsumm;
	}

	public void setCpsumm(GSTR2BCPSummary cpsumm) {
		this.cpsumm = cpsumm;
	}

	public GSTR2BDocData getDocdata() {
		return docdata;
	}

	public void setDocdata(GSTR2BDocData docdata) {
		this.docdata = docdata;
	}

	public int getFc() {
		return fc;
	}

	public void setFc(int fc) {
		this.fc = fc;
	}
}
