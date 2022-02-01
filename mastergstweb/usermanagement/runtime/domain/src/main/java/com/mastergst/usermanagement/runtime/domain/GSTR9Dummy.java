package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.core.domain.Base;

/**
 * GSTR9 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr9_dummy")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy"})
@JsonFilter("gstr9Filter")
public class GSTR9Dummy{
	private String userid;
	private String fullname;
	private String clientid;
	private String matchingId;
	private String matchingStatus;
	private Date dateofinvoice;
	private String legalName;
	private String tradeName;
	String gstin;
	String fp;
	GSTR9Table4Details table4 = new GSTR9Table4Details();
	GSTR9Table5Details table5 = new GSTR9Table5Details();
	GSTR9Table6Autocalculateddetails table6 = new GSTR9Table6Autocalculateddetails();
	GSTR9Table7Details table7 = new GSTR9Table7Details();
	GSTR9Table8AutoCalculatedDetails table8 = new GSTR9Table8AutoCalculatedDetails();
	GSTR9Table9AutoCalculatedDetails table9 = new GSTR9Table9AutoCalculatedDetails();

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getMatchingId() {
		return matchingId;
	}

	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}

	public String getMatchingStatus() {
		return matchingStatus;
	}

	public void setMatchingStatus(String matchingStatus) {
		this.matchingStatus = matchingStatus;
	}

	public Date getDateofinvoice() {
		return dateofinvoice;
	}

	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public GSTR9Table4Details getTable4() {
		return table4;
	}

	public void setTable4(GSTR9Table4Details table4) {
		this.table4 = table4;
	}

	public GSTR9Table5Details getTable5() {
		return table5;
	}

	public void setTable5(GSTR9Table5Details table5) {
		this.table5 = table5;
	}

	public GSTR9Table6Autocalculateddetails getTable6() {
		return table6;
	}

	public void setTable6(GSTR9Table6Autocalculateddetails table6) {
		this.table6 = table6;
	}

	public GSTR9Table7Details getTable7() {
		return table7;
	}

	public void setTable7(GSTR9Table7Details table7) {
		this.table7 = table7;
	}

	public GSTR9Table8AutoCalculatedDetails getTable8() {
		return table8;
	}

	public void setTable8(GSTR9Table8AutoCalculatedDetails table8) {
		this.table8 = table8;
	}

	public GSTR9Table9AutoCalculatedDetails getTable9() {
		return table9;
	}

	public void setTable9(GSTR9Table9AutoCalculatedDetails table9) {
		this.table9 = table9;
	}
	
}
