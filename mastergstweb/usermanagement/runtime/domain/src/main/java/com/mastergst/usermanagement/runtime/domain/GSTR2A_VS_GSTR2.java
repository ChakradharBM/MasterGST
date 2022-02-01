package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

public class GSTR2A_VS_GSTR2 {

	private String gstin;
	private String fullname;
	private String invtype;
	private String invoiceno;
	private Date invoicedate;

	/**
	 * Cann't remove this document ,if remove take problems ,if any changes read it
	 * carefully. GSTR2A --> PurchaseRegister GSTR2 --> GSTR2
	 */

	private Double gstr2AInvoiceValue;
	private Double gstr2ATaxValue;
	private Double gstr2AIGSTValue;
	private Double gstr2ACGSTValue;
	private Double gstr2ASGSTValue;

	private Double gstr2InvoiceValue;
	private Double gstr2TaxValue;
	private Double gstr2IGSTValue;
	private Double gstr2CGSTValue;
	private Double gstr2SGSTValue;

	private Double diffInvoiceValue;
	private Double diffTaxValue;
	private Double diffIGSTValue;
	private Double diffCGSTValue;
	private Double diffSGSTValue;

	public GSTR2A_VS_GSTR2() {
		super();
	}

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

	public Double getGstr2AInvoiceValue() {
		return gstr2AInvoiceValue;
	}

	public void setGstr2AInvoiceValue(Double gstr2aInvoiceValue) {
		gstr2AInvoiceValue = gstr2aInvoiceValue;
	}

	public Double getGstr2ATaxValue() {
		return gstr2ATaxValue;
	}

	public void setGstr2ATaxValue(Double gstr2aTaxValue) {
		gstr2ATaxValue = gstr2aTaxValue;
	}

	public Double getGstr2AIGSTValue() {
		return gstr2AIGSTValue;
	}

	public void setGstr2AIGSTValue(Double gstr2aigstValue) {
		gstr2AIGSTValue = gstr2aigstValue;
	}

	public Double getGstr2ACGSTValue() {
		return gstr2ACGSTValue;
	}

	public void setGstr2ACGSTValue(Double gstr2acgstValue) {
		gstr2ACGSTValue = gstr2acgstValue;
	}

	public Double getGstr2ASGSTValue() {
		return gstr2ASGSTValue;
	}

	public void setGstr2ASGSTValue(Double gstr2asgstValue) {
		gstr2ASGSTValue = gstr2asgstValue;
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
		return "GSTR2A_VS_GSTR2 [gstin=" + gstin + ", fullname=" + fullname + ", invtype=" + invtype + ", invoiceno=" + invoiceno
				+ ",  invoicedate=" + invoicedate + ", gstr2AInvoiceValue=" + gstr2AInvoiceValue + ", gstr2ATaxValue=" + gstr2ATaxValue
				+ ", gstr2AIGSTValue=" + gstr2AIGSTValue + ", gstr2ACGSTValue=" + gstr2ACGSTValue + ", gstr2ASGSTValue="
				+ gstr2ASGSTValue + ", gstr2InvoiceValue=" + gstr2InvoiceValue + ", gstr2TaxValue=" + gstr2TaxValue
				+ ", gstr2IGSTValue=" + gstr2IGSTValue + ", gstr2CGSTValue=" + gstr2CGSTValue + ", gstr2SGSTValue="
				+ gstr2SGSTValue + ", diffInvoiceValue=" + diffInvoiceValue + ", diffTaxValue=" + diffTaxValue
				+ ", diffIGSTValue=" + diffIGSTValue + ", diffCGSTValue=" + diffCGSTValue + ", diffSGSTValue="
				+ diffSGSTValue + "]";
	}

}
