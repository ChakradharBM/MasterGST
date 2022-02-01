package com.mastergst.usermanagement.runtime.domain.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

@Document(collection = "gstr6a")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy"})
public class GSTR6A extends InvoiceParent{
	List<GSTR6ISD> isd=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
	List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdna=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTR6ISD> isda=LazyList.decorate(new ArrayList<GSTR6ISD>(), 
			FactoryUtils.instantiateFactory(GSTR6ISD.class));
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
	
}
