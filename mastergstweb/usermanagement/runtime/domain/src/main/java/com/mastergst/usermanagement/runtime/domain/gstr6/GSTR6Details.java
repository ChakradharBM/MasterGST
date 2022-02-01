package com.mastergst.usermanagement.runtime.domain.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.core.domain.Base;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;

@Document(collection = "gstr6details")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid"})
public class GSTR6Details extends Base{
	private String userid;
	private String fullname;
	private String clientid;
	private String gstin;
	private String fp;
	List<GSTR6ISD> isd=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
	List<GSTRB2B> b2b=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdn=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTRCreditDebitNotes> cdna=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTR6ISD> isda=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
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
	public List<GSTRB2B> getB2b() {
		return b2b;
	}
	public void setB2b(List<GSTRB2B> b2b) {
		this.b2b = b2b;
	}
	public List<GSTRCreditDebitNotes> getCdn() {
		return cdn;
	}
	public void setCdn(List<GSTRCreditDebitNotes> cdn) {
		this.cdn = cdn;
	}
	public List<GSTR6ISD> getIsd() {
		return isd;
	}
	public void setIsd(List<GSTR6ISD> isd) {
		this.isd = isd;
	}
	public List<GSTRB2B> getB2ba() {
		return b2ba;
	}
	public void setB2ba(List<GSTRB2B> b2ba) {
		this.b2ba = b2ba;
	}
	public List<GSTRCreditDebitNotes> getCdna() {
		return cdna;
	}
	public void setCdna(List<GSTRCreditDebitNotes> cdna) {
		this.cdna = cdna;
	}
	public List<GSTR6ISD> getIsda() {
		return isda;
	}
	public void setIsda(List<GSTR6ISD> isda) {
		this.isda = isda;
	}
	@Override
	public String toString() {
		return "GSTR6Details [gstin=" + gstin + ", fp=" + fp + ", isd=" + isd + ", b2b=" + b2b + ", b2ba=" + b2ba
				+ ", cdn=" + cdn + ", cdna=" + cdna + ", isda=" + isda + "]";
	}
	
}
