package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "partner_payments")
public class PartnerPayments extends Base {
	private String docId;
	private String invoiceno;
	private String invoicedate;
	private String fullname;
	private String userid;
	private Double subscriptionamount;
	private Double percentage;
	private Double paidamount;
	private Double partneramt;
	private Double tdsamt;
	private String partnerPayment;

	private String mthCd;
	private String yrCd;
	private String qrtCd;

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public Double getSubscriptionamount() {
		return subscriptionamount;
	}

	public void setSubscriptionamount(Double subscriptionamount) {
		this.subscriptionamount = subscriptionamount;
	}

	public Double getPercentage() {
		return percentage;
	}

	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}

	public Double getPaidamount() {
		return paidamount;
	}

	public void setPaidamount(Double paidamount) {
		this.paidamount = paidamount;
	}

	public Double getPartneramt() {
		return partneramt;
	}

	public void setPartneramt(Double partneramt) {
		this.partneramt = partneramt;
	}

	public String getPartnerPayment() {
		return partnerPayment;
	}

	public void setPartnerPayment(String partnerPayment) {
		this.partnerPayment = partnerPayment;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	public String getQrtCd() {
		return qrtCd;
	}

	public void setQrtCd(String qrtCd) {
		this.qrtCd = qrtCd;
	}

	public String getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public String getInvoicedate() {
		return invoicedate;
	}

	public void setInvoicedate(String invoicedate) {
		this.invoicedate = invoicedate;
	}

	public Double getTdsamt() {
		return tdsamt;
	}

	public void setTdsamt(Double tdsamt) {
		this.tdsamt = tdsamt;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
}
