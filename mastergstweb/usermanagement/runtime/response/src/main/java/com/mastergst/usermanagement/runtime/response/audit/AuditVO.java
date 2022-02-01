package com.mastergst.usermanagement.runtime.response.audit;

public class AuditVO {
	private String clientname;
	private String username;
	private String useremail;
	private String action;
	private String gstn;
	private String invoicenumber;
	private String description;
	private String returntype;
	private String createddate;
	
	/* -- Old/Previous Values --*/
	private String oinvoiceNumber;
	private String oinvoiceDate;
	private String ogstin;
	private Double ototaltaxableamount;
	private Double ototalamount;
	private Double ototalIGSTAmount;
	private Double ototalCGSTAmount;
	private Double ototalSGSTAmount;
	private Double ototalCESSAmount;
	
	/* -- New Values -- */
	private String ninvoiceNumber;
	private String ninvoiceDate;
	private String ngstin;
	private Double ntotaltaxableamount;
	private Double ntotalamount;
	private Double ntotalIGSTAmount;
	private Double ntotalCGSTAmount;
	private Double ntotalSGSTAmount;
	private Double ntotalCESSAmount;
	
	public String getClientname() {
		return clientname;
	}
	public void setClientname(String clientname) {
		this.clientname = clientname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getGstn() {
		return gstn;
	}
	public void setGstn(String gstn) {
		this.gstn = gstn;
	}
	public String getInvoicenumber() {
		return invoicenumber;
	}
	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getReturntype() {
		return returntype;
	}
	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}
	public String getCreateddate() {
		return createddate;
	}
	public void setCreateddate(String createddate) {
		this.createddate = createddate;
	}
	public String getOinvoiceNumber() {
		return oinvoiceNumber;
	}
	public void setOinvoiceNumber(String oinvoiceNumber) {
		this.oinvoiceNumber = oinvoiceNumber;
	}
	public String getOinvoiceDate() {
		return oinvoiceDate;
	}
	public void setOinvoiceDate(String oinvoiceDate) {
		this.oinvoiceDate = oinvoiceDate;
	}
	public String getOgstin() {
		return ogstin;
	}
	public void setOgstin(String ogstin) {
		this.ogstin = ogstin;
	}
	public Double getOtotaltaxableamount() {
		return ototaltaxableamount;
	}
	public void setOtotaltaxableamount(Double ototaltaxableamount) {
		this.ototaltaxableamount = ototaltaxableamount;
	}
	public Double getOtotalamount() {
		return ototalamount;
	}
	public void setOtotalamount(Double ototalamount) {
		this.ototalamount = ototalamount;
	}
	public Double getOtotalIGSTAmount() {
		return ototalIGSTAmount;
	}
	public void setOtotalIGSTAmount(Double ototalIGSTAmount) {
		this.ototalIGSTAmount = ototalIGSTAmount;
	}
	public Double getOtotalCGSTAmount() {
		return ototalCGSTAmount;
	}
	public void setOtotalCGSTAmount(Double ototalCGSTAmount) {
		this.ototalCGSTAmount = ototalCGSTAmount;
	}
	public Double getOtotalSGSTAmount() {
		return ototalSGSTAmount;
	}
	public void setOtotalSGSTAmount(Double ototalSGSTAmount) {
		this.ototalSGSTAmount = ototalSGSTAmount;
	}
	public Double getOtotalCESSAmount() {
		return ototalCESSAmount;
	}
	public void setOtotalCESSAmount(Double ototalCESSAmount) {
		this.ototalCESSAmount = ototalCESSAmount;
	}
	public String getNinvoiceNumber() {
		return ninvoiceNumber;
	}
	public void setNinvoiceNumber(String ninvoiceNumber) {
		this.ninvoiceNumber = ninvoiceNumber;
	}
	public String getNinvoiceDate() {
		return ninvoiceDate;
	}
	public void setNinvoiceDate(String ninvoiceDate) {
		this.ninvoiceDate = ninvoiceDate;
	}
	public String getNgstin() {
		return ngstin;
	}
	public void setNgstin(String ngstin) {
		this.ngstin = ngstin;
	}
	public Double getNtotaltaxableamount() {
		return ntotaltaxableamount;
	}
	public void setNtotaltaxableamount(Double ntotaltaxableamount) {
		this.ntotaltaxableamount = ntotaltaxableamount;
	}
	public Double getNtotalamount() {
		return ntotalamount;
	}
	public void setNtotalamount(Double ntotalamount) {
		this.ntotalamount = ntotalamount;
	}
	public Double getNtotalIGSTAmount() {
		return ntotalIGSTAmount;
	}
	public void setNtotalIGSTAmount(Double ntotalIGSTAmount) {
		this.ntotalIGSTAmount = ntotalIGSTAmount;
	}
	public Double getNtotalCGSTAmount() {
		return ntotalCGSTAmount;
	}
	public void setNtotalCGSTAmount(Double ntotalCGSTAmount) {
		this.ntotalCGSTAmount = ntotalCGSTAmount;
	}
	public Double getNtotalSGSTAmount() {
		return ntotalSGSTAmount;
	}
	public void setNtotalSGSTAmount(Double ntotalSGSTAmount) {
		this.ntotalSGSTAmount = ntotalSGSTAmount;
	}
	public Double getNtotalCESSAmount() {
		return ntotalCESSAmount;
	}
	public void setNtotalCESSAmount(Double ntotalCESSAmount) {
		this.ntotalCESSAmount = ntotalCESSAmount;
	}
}
