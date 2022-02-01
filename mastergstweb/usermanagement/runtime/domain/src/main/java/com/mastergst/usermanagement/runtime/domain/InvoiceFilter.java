package com.mastergst.usermanagement.runtime.domain;

public class InvoiceFilter {
	
	private String[] paymentStatus;
	private String[] invoiceType;
	private String[] user;
	private String[] vendor;
	private String[] branch;
	private String[] vertical;
	private String[] reverseCharge;
	private String[] supplyType;
	private String[] documentType;
	private String[] subSupplyType;
	private String booksOrReturns;
	private String[] irnStatus;
	private String[] status;
	/*Mannual Match*/
	private String[] gstno;
	private String[] dateofInvoice;
	private String[] invoiceno;
	/* GSTR2A Filing Status*/
	private String[] gstr2aFilingStatus;
	private String[] reconStatus;
	/* Custom Fields*/
	private String[] customFieldText1;
	private String[] customFieldText2;
	private String[] customFieldText3;
	private String[] customFieldText4;
	
	private String fromtime;
	private String totime;
	public String[] getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(String[] paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus == null ? null : paymentStatus.split(",");
	}
	public String[] getInvoiceType() {
		return invoiceType;
	}
	public void setInvoiceType(String[] invoiceType) {
		this.invoiceType = invoiceType;
	}
	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType == null ? null : invoiceType.split(",");
	}
	public String[] getUser() {
		return user;
	}
	public void setUser(String[] user) {
		this.user = user;
	}
	public void setUser(String user) {
		this.user = user == null ? null : user.split(",");
	}
	public String[] getVendor() {
		return vendor;
	}
	public void setVendor(String[] vendor) {
		this.vendor = vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor == null ? null : vendor.split(",");
	}
	public String[] getBranch() {
		return branch;
	}
	public void setBranch(String[] branch) {
		this.branch = branch;
	}
	public void setBranch(String branch) {
		this.branch = branch == null ? null : branch.split(",");
	}
	public String[] getVertical() {
		return vertical;
	}
	public void setVertical(String[] vertical) {
		this.vertical = vertical;
	}
	public void setVertical(String vertical) {
		this.vertical = vertical == null ? null : vertical.split(",");
	}
	public String[] getReverseCharge() {
		return reverseCharge;
	}
	public void setReverseCharge(String[] reverseCharge) {
		this.reverseCharge = reverseCharge;
	}
	public void setReverseCharge(String reverseCharge) {
		this.reverseCharge = reverseCharge == null ? null : reverseCharge.split(",");
	}
	public String getBooksOrReturns() {
		return booksOrReturns;
	}
	public void setBooksOrReturns(String booksOrReturns) {
		this.booksOrReturns = booksOrReturns;
	}
	public String[] getSupplyType() {
		return supplyType;
	}
	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType == null ? null : supplyType.split(",");
	}
	public String[] getDocumentType() {
		return documentType;
	}
	public void setDocumentType(String documentType) {
		this.documentType = documentType == null ? null : documentType.split(",");
	}
	public String[] getIrnStatus() {
		return irnStatus;
	}
	public void setIrnStatus(String irnStatus) {
		this.irnStatus = irnStatus == null ? null : irnStatus.split(",");
	}
	public String[] getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status == null ? null : status.split(",");
	}
	public String[] getGstno() {
		return gstno;
	}
	public void setGstno(String gstno) {
		this.gstno = gstno == null ? null : gstno.split(",");
	}
	public String[] getDateofInvoice() {
		return dateofInvoice;
	}
	public void setDateofInvoice(String dateofInvoice) {
		this.dateofInvoice = dateofInvoice == null ? null : dateofInvoice.split(",");
	}
	public String[] getInvoiceno() {
		return invoiceno;
	}
	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno == null ? null : invoiceno.split(",");
	}
	public String[] getGstr2aFilingStatus() {
		return gstr2aFilingStatus;
	}
	public void setGstr2aFilingStatus(String gstr2aFilingStatus) {
		this.gstr2aFilingStatus = gstr2aFilingStatus == null ? null : gstr2aFilingStatus.split(",");
	}
	
	public String[] getReconStatus() {
		return reconStatus;
	}
	public void setReconStatus(String reconStatus) {
		this.reconStatus = reconStatus == null ? null : reconStatus.split(",");
	}
	public String[] getSubSupplyType() {
		return subSupplyType;
	}
	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType == null ? null : subSupplyType.split(",");
	}
	public String[] getCustomFieldText1() {
		return customFieldText1;
	}
	public void setCustomFieldText1(String customFieldText1) {
		this.customFieldText1 = customFieldText1 == null ? null : customFieldText1.split(",");
	}
	public String[] getCustomFieldText2() {
		return customFieldText2;
	}
	public void setCustomFieldText2(String customFieldText2) {
		this.customFieldText2 = customFieldText2 == null ? null : customFieldText2.split(",");
	}
	public String[] getCustomFieldText3() {
		return customFieldText3;
	}
	public void setCustomFieldText3(String customFieldText3) {
		this.customFieldText3 = customFieldText3 == null ? null : customFieldText3.split(",");
	}
	public String[] getCustomFieldText4() {
		return customFieldText4;
	}
	public void setCustomFieldText4(String customFieldText4) {
		this.customFieldText4 = customFieldText4 == null ? null : customFieldText4.split(",");
	}
	public String getFromtime() {
		return fromtime;
	}
	public void setFromtime(String fromtime) {
		this.fromtime = fromtime;
	}
	public String getTotime() {
		return totime;
	}
	public void setTotime(String totime) {
		this.totime = totime;
	}
	
}
