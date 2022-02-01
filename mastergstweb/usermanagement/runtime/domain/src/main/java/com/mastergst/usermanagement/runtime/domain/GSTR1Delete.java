package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;

@Document(collection = "gstr1archive")
@JsonFilter("gstr1Filter")
public class GSTR1Delete extends InvoiceParent {
	List<GSTRAdvanceTax> at=LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), 
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	List<GSTRB2B> b2bur=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRCreditDebitNotes> cdnr=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), 
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	List<GSTRB2B> b2ba=LazyList.decorate(new ArrayList<GSTRB2B>(), FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRB2CL> b2cla = LazyList.decorate(new ArrayList<GSTRB2CL>(), FactoryUtils.instantiateFactory(GSTRB2CL.class));
	List<GSTRB2CSA> b2csa=LazyList.decorate(new ArrayList<GSTRB2CSA>(), FactoryUtils.instantiateFactory(GSTRB2CSA.class));
	List<GSTRAdvanceTax> ata = LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	
	
	List<GSTRExports> expa = LazyList.decorate(new ArrayList<GSTRExports>(),FactoryUtils.instantiateFactory(GSTRExports.class));
	List<GSTRInvoiceDetails> cdnura = LazyList.decorate(new ArrayList<GSTRInvoiceDetails>(),FactoryUtils.instantiateFactory(GSTRInvoiceDetails.class));
	List<GSTRCreditDebitNotes> cdnra=LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(), FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));
	@JsonProperty("doc_issue")
	GSTR1DocumentIssue docIssue; 
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date deleteddate;
	
	public List<GSTRAdvanceTax> getAt() {
		return at;
	}
	public void setAt(List<GSTRAdvanceTax> at) {
		this.at = at;
	}
	public List<GSTRB2B> getB2bur() {
		return b2bur;
	}
	public void setB2bur(List<GSTRB2B> b2bur) {
		this.b2bur = b2bur;
	}
	public List<GSTRCreditDebitNotes> getCdnr() {
		return cdnr;
	}
	public void setCdnr(List<GSTRCreditDebitNotes> cdnr) {
		this.cdnr = cdnr;
	}
	public List<GSTRB2B> getB2ba() {
		return b2ba;
	}
	public void setB2ba(List<GSTRB2B> b2ba) {
		this.b2ba = b2ba;
	}
	public List<GSTRB2CL> getB2cla() {
		return b2cla;
	}
		
	public List<GSTRB2CSA> getB2csa() {
		return b2csa;
	}
	public void setB2csa(List<GSTRB2CSA> b2csa) {
		this.b2csa = b2csa;
	}
	public void setB2cla(List<GSTRB2CL> b2cla) {
		this.b2cla = b2cla;
	}
	public List<GSTRAdvanceTax> getAta() {
		return ata;
	}
	public void setAta(List<GSTRAdvanceTax> ata) {
		this.ata = ata;
	}
	
	public List<GSTRExports> getExpa() {
		return expa;
	}
	public void setExpa(List<GSTRExports> expa) {
		this.expa = expa;
	}
	public List<GSTRInvoiceDetails> getCdnura() {
		return cdnura;
	}
	public void setCdnura(List<GSTRInvoiceDetails> cdnura) {
		this.cdnura = cdnura;
	}
	public List<GSTRCreditDebitNotes> getCdnra() {
		return cdnra;
	}
	public void setCdnra(List<GSTRCreditDebitNotes> cdnra) {
		this.cdnra = cdnra;
	}
	public GSTR1DocumentIssue getDocIssue() {
		return docIssue;
	}
	public void setDocIssue(GSTR1DocumentIssue docIssue) {
		this.docIssue = docIssue;
	}
	public Date getDeleteddate() {
		return deleteddate;
	}
	public void setDeleteddate(Date deleteddate) {
		this.deleteddate = deleteddate;
	}
	
}
