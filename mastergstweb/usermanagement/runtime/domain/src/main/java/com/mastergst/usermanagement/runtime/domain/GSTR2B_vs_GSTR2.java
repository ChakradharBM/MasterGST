package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

public class GSTR2B_vs_GSTR2 {

	private String gstin;
	private String fullname;
	private String invtype;
	private String invoiceno;
	private Date invoicedate;

	private Double gstr2BInvoiceValue = 0d;
	private Double gstr2BTaxValue = 0d;
	private Double gstr2BIGSTValue = 0d;
	private Double gstr2BCGSTValue = 0d;
	private Double gstr2BSGSTValue = 0d;

	private Double gstr2InvoiceValue = 0d;
	private Double gstr2TaxValue = 0d;
	private Double gstr2IGSTValue = 0d;
	private Double gstr2CGSTValue = 0d;
	private Double gstr2SGSTValue = 0d;

	private Double diffInvoiceValue = 0d;
	private Double diffTaxValue = 0d;
	private Double diffIGSTValue = 0d;
	private Double diffCGSTValue = 0d;
	private Double diffSGSTValue = 0d;

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public Date getInvoicedate() {
		return invoicedate;
	}

	public void setInvoicedate(Date invoicedate) {
		this.invoicedate = invoicedate;
	}

	public Double getGstr2BInvoiceValue() {
		return gstr2BInvoiceValue;
	}

	public void setGstr2BInvoiceValue(Double gstr2bInvoiceValue) {
		gstr2BInvoiceValue = gstr2bInvoiceValue;
	}

	public Double getGstr2BTaxValue() {
		return gstr2BTaxValue;
	}

	public void setGstr2BTaxValue(Double gstr2bTaxValue) {
		gstr2BTaxValue = gstr2bTaxValue;
	}

	public Double getGstr2BIGSTValue() {
		return gstr2BIGSTValue;
	}

	public void setGstr2BIGSTValue(Double gstr2bigstValue) {
		gstr2BIGSTValue = gstr2bigstValue;
	}

	public Double getGstr2BCGSTValue() {
		return gstr2BCGSTValue;
	}

	public void setGstr2BCGSTValue(Double gstr2bcgstValue) {
		gstr2BCGSTValue = gstr2bcgstValue;
	}

	public Double getGstr2BSGSTValue() {
		return gstr2BSGSTValue;
	}

	public void setGstr2BSGSTValue(Double gstr2bsgstValue) {
		gstr2BSGSTValue = gstr2bsgstValue;
	}

	public Double getGstr2InvoiceValue() {
		return gstr2InvoiceValue;
	}

	public void setGstr2InvoiceValue(Double gstr2InvoiceValue) {
		this.gstr2InvoiceValue = gstr2InvoiceValue;
	}

	public Double getGstr2TaxValue() {
		return gstr2TaxValue;
	}

	public void setGstr2TaxValue(Double gstr2TaxValue) {
		this.gstr2TaxValue = gstr2TaxValue;
	}

	public Double getGstr2IGSTValue() {
		return gstr2IGSTValue;
	}

	public void setGstr2IGSTValue(Double gstr2igstValue) {
		gstr2IGSTValue = gstr2igstValue;
	}

	public Double getGstr2CGSTValue() {
		return gstr2CGSTValue;
	}

	public void setGstr2CGSTValue(Double gstr2cgstValue) {
		gstr2CGSTValue = gstr2cgstValue;
	}

	public Double getGstr2SGSTValue() {
		return gstr2SGSTValue;
	}

	public void setGstr2SGSTValue(Double gstr2sgstValue) {
		gstr2SGSTValue = gstr2sgstValue;
	}

	public Double getDiffInvoiceValue() {
		return diffInvoiceValue;
	}

	public void setDiffInvoiceValue(Double diffInvoiceValue) {
		this.diffInvoiceValue = diffInvoiceValue;
	}

	public Double getDiffTaxValue() {
		return diffTaxValue;
	}

	public void setDiffTaxValue(Double diffTaxValue) {
		this.diffTaxValue = diffTaxValue;
	}

	public Double getDiffIGSTValue() {
		return diffIGSTValue;
	}

	public void setDiffIGSTValue(Double diffIGSTValue) {
		this.diffIGSTValue = diffIGSTValue;
	}

	public Double getDiffCGSTValue() {
		return diffCGSTValue;
	}

	public void setDiffCGSTValue(Double diffCGSTValue) {
		this.diffCGSTValue = diffCGSTValue;
	}

	public Double getDiffSGSTValue() {
		return diffSGSTValue;
	}

	public void setDiffSGSTValue(Double diffSGSTValue) {
		this.diffSGSTValue = diffSGSTValue;
	}

	@Override
	public String toString() {
		return "GSTR2B_vs_GSTR2 [invoiceno=" + invoiceno + ", gstr2BInvoiceValue=" + gstr2BInvoiceValue
				+ ", gstr2BTaxValue=" + gstr2BTaxValue + ", gstr2BIGSTValue=" + gstr2BIGSTValue + ", gstr2BCGSTValue="
				+ gstr2BCGSTValue + ", gstr2BSGSTValue=" + gstr2BSGSTValue + ", gstr2InvoiceValue=" + gstr2InvoiceValue
				+ ", gstr2TaxValue=" + gstr2TaxValue + ", gstr2IGSTValue=" + gstr2IGSTValue + ", gstr2CGSTValue="
				+ gstr2CGSTValue + ", gstr2SGSTValue=" + gstr2SGSTValue + ", diffInvoiceValue=" + diffInvoiceValue
				+ ", diffTaxValue=" + diffTaxValue + ", diffIGSTValue=" + diffIGSTValue + ", diffCGSTValue="
				+ diffCGSTValue + ", diffSGSTValue=" + diffSGSTValue + ", getGstin()=" + getGstin() + ", getFullname()="
				+ getFullname() + ", getInvtype()=" + getInvtype() + ", getInvoiceno()=" + getInvoiceno()
				+ ", getInvoicedate()=" + getInvoicedate() + ", getGstr2BInvoiceValue()=" + getGstr2BInvoiceValue()
				+ ", getGstr2BTaxValue()=" + getGstr2BTaxValue() + ", getGstr2BIGSTValue()=" + getGstr2BIGSTValue()
				+ ", getGstr2BCGSTValue()=" + getGstr2BCGSTValue() + ", getGstr2BSGSTValue()=" + getGstr2BSGSTValue()
				+ ", getGstr2InvoiceValue()=" + getGstr2InvoiceValue() + ", getGstr2TaxValue()=" + getGstr2TaxValue()
				+ ", getGstr2IGSTValue()=" + getGstr2IGSTValue() + ", getGstr2CGSTValue()=" + getGstr2CGSTValue()
				+ ", getGstr2SGSTValue()=" + getGstr2SGSTValue() + ", getDiffInvoiceValue()=" + getDiffInvoiceValue()
				+ ", getDiffTaxValue()=" + getDiffTaxValue() + ", getDiffIGSTValue()=" + getDiffIGSTValue()
				+ ", getDiffCGSTValue()=" + getDiffCGSTValue() + ", getDiffSGSTValue()=" + getDiffSGSTValue()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}

}
