/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companyinvoices")
public class CompanyInvoices extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private String invoiceType;
	private String allowMonth;
	private String allowYear;
	private String year;
	private String prefix;
	private Integer startInvoiceNo;
	private Integer endInvoiceNo;
	private String returnType;
	private Date submittedDate;
	private String formatMonth;
	private String formatYear;
	private String sampleInvNo;
	private String invoicenumbercutoff;
	
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
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public Integer getStartInvoiceNo() {
		return startInvoiceNo;
	}
	public void setStartInvoiceNo(Integer startInvoiceNo) {
		this.startInvoiceNo = startInvoiceNo;
	}
	public Integer getEndInvoiceNo() {
		return endInvoiceNo;
	}
	public void setEndInvoiceNo(Integer endInvoiceNo) {
		this.endInvoiceNo = endInvoiceNo;
	}
	public Date getSubmittedDate() {
		return submittedDate;
	}
	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}
	public String getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}
	public String getAllowMonth() {
		return allowMonth;
	}
	public void setAllowMonth(String allowMonth) {
		this.allowMonth = allowMonth;
	}
	public String getAllowYear() {
		return allowYear;
	}
	public void setAllowYear(String allowYear) {
		this.allowYear = allowYear;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	public String getFormatYear() {
		return formatYear;
	}
	public void setFormatYear(String formatYear) {
		this.formatYear = formatYear;
	}
	public String getFormatMonth() {
		return formatMonth;
	}
	public void setFormatMonth(String formatMonth) {
		this.formatMonth = formatMonth;
	}
	public String getSampleInvNo() {
		return sampleInvNo;
	}
	public void setSampleInvNo(String sampleInvNo) {
		this.sampleInvNo = sampleInvNo;
	}
	public String getInvoicenumbercutoff() {
		return invoicenumbercutoff;
	}
	public void setInvoicenumbercutoff(String invoicenumbercutoff) {
		this.invoicenumbercutoff = invoicenumbercutoff;
	}
	
}
