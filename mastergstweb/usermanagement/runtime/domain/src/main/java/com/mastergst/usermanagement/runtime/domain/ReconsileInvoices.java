package com.mastergst.usermanagement.runtime.domain;

public class ReconsileInvoices {

	private String status;

	private String purchaseStatus;
	private String purchaseInvtype;
	private String purchaseSuppliername;
	private String purchaseGstno;
	private String purchaseInvoiceno;
	private String purchaseInvoicedate;
	private String purchaseBranch;
	private Double purchaseTotaltaxableamount;
	private Double purchaseTotalamount;
	private Double purchaseTotalIgstAmount;
	private Double purchaseTotalCgstAmount;
	private Double purchaseTotalSgstAmount;
	private Double purchaseTotalCessAmount;
	
	private String purchaseReturnPeriod;
	private String purchaseReverseCharge;
	private String purchaseTransactionDate;
	private String purchaseCompanyStateName;
	private String purchaseState;
	private String purchasecustomField1;
	private String purchasecustomField2;
	private String purchasecustomField3;
	private String purchasecustomField4;

	private String gstr2status;
	private String gstr2Gstno;
	private String gstr2Invtype;
	private String gstr2Suppliername;
	private String gstr2Invoiceno;
	private String gstr2Invoicedate;
	private String gstr2Branch;
	private Double gstr2Totaltaxableamount;
	private Double gstr2Totalamount;
	private Double gstr2TotalIgstAmount;
	private Double gstr2TotalCgstAmount;
	private Double gstr2TotalSgstAmount;
	private Double gstr2TotalCessAmount;
	
	private String gstr2ReturnPeriod;
	private String gstr2ReverseCharge;
	private String gstr2TransactionDate;
	private String gstr2CompanyStateName;
	private String gstr2State;
	private String gstr2customField1;
	private String gstr2customField2;
	private String gstr2customField3;
	private String gstr2customField4;
	
	public String getPurchaseStatus() {
		return purchaseStatus;
	}

	public String getPurchaseGstno() {
		return purchaseGstno;
	}

	public void setPurchaseGstno(String purchaseGstno) {
		this.purchaseGstno = purchaseGstno;
	}

	public String getGstr2Gstno() {
		return gstr2Gstno;
	}

	public void setGstr2Gstno(String gstr2Gstno) {
		this.gstr2Gstno = gstr2Gstno;
	}

	public void setPurchaseStatus(String purchaseStatus) {
		this.purchaseStatus = purchaseStatus;
	}

	public String getPurchaseInvtype() {
		return purchaseInvtype;
	}

	public void setPurchaseInvtype(String purchaseInvtype) {
		this.purchaseInvtype = purchaseInvtype;
	}

	public String getPurchaseSuppliername() {
		return purchaseSuppliername;
	}

	public void setPurchaseSuppliername(String purchaseSuppliername) {
		this.purchaseSuppliername = purchaseSuppliername;
	}

	public String getPurchaseInvoiceno() {
		return purchaseInvoiceno;
	}

	public void setPurchaseInvoiceno(String purchaseInvoiceno) {
		this.purchaseInvoiceno = purchaseInvoiceno;
	}

	public String getPurchaseInvoicedate() {
		return purchaseInvoicedate;
	}

	public void setPurchaseInvoicedate(String purchaseInvoicedate) {
		this.purchaseInvoicedate = purchaseInvoicedate;
	}

	public String getPurchaseBranch() {
		return purchaseBranch;
	}

	public void setPurchaseBranch(String purchaseBranch) {
		this.purchaseBranch = purchaseBranch;
	}

	public Double getPurchaseTotaltaxableamount() {
		return purchaseTotaltaxableamount;
	}

	public void setPurchaseTotaltaxableamount(Double purchaseTotaltaxableamount) {
		this.purchaseTotaltaxableamount = purchaseTotaltaxableamount;
	}

	public Double getPurchaseTotalamount() {
		return purchaseTotalamount;
	}

	public void setPurchaseTotalamount(Double purchaseTotalamount) {
		this.purchaseTotalamount = purchaseTotalamount;
	}

	public String getGstr2status() {
		return gstr2status;
	}

	public void setGstr2status(String gstr2status) {
		this.gstr2status = gstr2status;
	}

	public String getGstr2Invtype() {
		return gstr2Invtype;
	}

	public void setGstr2Invtype(String gstr2Invtype) {
		this.gstr2Invtype = gstr2Invtype;
	}

	public String getGstr2Suppliername() {
		return gstr2Suppliername;
	}

	public void setGstr2Suppliername(String gstr2Suppliername) {
		this.gstr2Suppliername = gstr2Suppliername;
	}

	public String getGstr2Invoiceno() {
		return gstr2Invoiceno;
	}

	public void setGstr2Invoiceno(String gstr2Invoiceno) {
		this.gstr2Invoiceno = gstr2Invoiceno;
	}

	public String getGstr2Invoicedate() {
		return gstr2Invoicedate;
	}

	public void setGstr2Invoicedate(String gstr2Invoicedate) {
		this.gstr2Invoicedate = gstr2Invoicedate;
	}

	public String getGstr2Branch() {
		return gstr2Branch;
	}

	public void setGstr2Branch(String gstr2Branch) {
		this.gstr2Branch = gstr2Branch;
	}

	public Double getGstr2Totaltaxableamount() {
		return gstr2Totaltaxableamount;
	}

	public void setGstr2Totaltaxableamount(Double gstr2Totaltaxableamount) {
		this.gstr2Totaltaxableamount = gstr2Totaltaxableamount;
	}

	public Double getGstr2Totalamount() {
		return gstr2Totalamount;
	}

	public void setGstr2Totalamount(Double gstr2Totalamount) {
		this.gstr2Totalamount = gstr2Totalamount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPurchaseReturnPeriod() {
		return purchaseReturnPeriod;
	}

	public void setPurchaseReturnPeriod(String purchaseReturnPeriod) {
		this.purchaseReturnPeriod = purchaseReturnPeriod;
	}

	public String getPurchaseReverseCharge() {
		return purchaseReverseCharge;
	}

	public void setPurchaseReverseCharge(String purchaseReverseCharge) {
		this.purchaseReverseCharge = purchaseReverseCharge;
	}

	public String getPurchaseTransactionDate() {
		return purchaseTransactionDate;
	}

	public void setPurchaseTransactionDate(String purchaseTransactionDate) {
		this.purchaseTransactionDate = purchaseTransactionDate;
	}

	public String getPurchaseCompanyStateName() {
		return purchaseCompanyStateName;
	}

	public void setPurchaseCompanyStateName(String purchaseCompanyStateName) {
		this.purchaseCompanyStateName = purchaseCompanyStateName;
	}

	public String getPurchaseState() {
		return purchaseState;
	}

	public void setPurchaseState(String purchaseState) {
		this.purchaseState = purchaseState;
	}

	public String getPurchasecustomField1() {
		return purchasecustomField1;
	}

	public void setPurchasecustomField1(String purchasecustomField1) {
		this.purchasecustomField1 = purchasecustomField1;
	}

	public String getPurchasecustomField2() {
		return purchasecustomField2;
	}

	public void setPurchasecustomField2(String purchasecustomField2) {
		this.purchasecustomField2 = purchasecustomField2;
	}

	public String getPurchasecustomField3() {
		return purchasecustomField3;
	}

	public void setPurchasecustomField3(String purchasecustomField3) {
		this.purchasecustomField3 = purchasecustomField3;
	}

	public String getPurchasecustomField4() {
		return purchasecustomField4;
	}

	public void setPurchasecustomField4(String purchasecustomField4) {
		this.purchasecustomField4 = purchasecustomField4;
	}

	public String getGstr2ReturnPeriod() {
		return gstr2ReturnPeriod;
	}

	public void setGstr2ReturnPeriod(String gstr2ReturnPeriod) {
		this.gstr2ReturnPeriod = gstr2ReturnPeriod;
	}

	public String getGstr2ReverseCharge() {
		return gstr2ReverseCharge;
	}

	public void setGstr2ReverseCharge(String gstr2ReverseCharge) {
		this.gstr2ReverseCharge = gstr2ReverseCharge;
	}

	public String getGstr2TransactionDate() {
		return gstr2TransactionDate;
	}

	public void setGstr2TransactionDate(String gstr2TransactionDate) {
		this.gstr2TransactionDate = gstr2TransactionDate;
	}

	public String getGstr2CompanyStateName() {
		return gstr2CompanyStateName;
	}

	public void setGstr2CompanyStateName(String gstr2CompanyStateName) {
		this.gstr2CompanyStateName = gstr2CompanyStateName;
	}

	public String getGstr2State() {
		return gstr2State;
	}

	public void setGstr2State(String gstr2State) {
		this.gstr2State = gstr2State;
	}

	public String getGstr2customField1() {
		return gstr2customField1;
	}

	public void setGstr2customField1(String gstr2customField1) {
		this.gstr2customField1 = gstr2customField1;
	}

	public String getGstr2customField2() {
		return gstr2customField2;
	}

	public void setGstr2customField2(String gstr2customField2) {
		this.gstr2customField2 = gstr2customField2;
	}

	public String getGstr2customField3() {
		return gstr2customField3;
	}

	public void setGstr2customField3(String gstr2customField3) {
		this.gstr2customField3 = gstr2customField3;
	}

	public String getGstr2customField4() {
		return gstr2customField4;
	}

	public void setGstr2customField4(String gstr2customField4) {
		this.gstr2customField4 = gstr2customField4;
	}

	public Double getPurchaseTotalIgstAmount() {
		return purchaseTotalIgstAmount;
	}

	public void setPurchaseTotalIgstAmount(Double purchaseTotalIgstAmount) {
		this.purchaseTotalIgstAmount = purchaseTotalIgstAmount;
	}

	public Double getPurchaseTotalCgstAmount() {
		return purchaseTotalCgstAmount;
	}

	public void setPurchaseTotalCgstAmount(Double purchaseTotalCgstAmount) {
		this.purchaseTotalCgstAmount = purchaseTotalCgstAmount;
	}

	public Double getPurchaseTotalSgstAmount() {
		return purchaseTotalSgstAmount;
	}

	public void setPurchaseTotalSgstAmount(Double purchaseTotalSgstAmount) {
		this.purchaseTotalSgstAmount = purchaseTotalSgstAmount;
	}

	public Double getGstr2TotalIgstAmount() {
		return gstr2TotalIgstAmount;
	}

	public void setGstr2TotalIgstAmount(Double gstr2TotalIgstAmount) {
		this.gstr2TotalIgstAmount = gstr2TotalIgstAmount;
	}

	public Double getGstr2TotalCgstAmount() {
		return gstr2TotalCgstAmount;
	}

	public void setGstr2TotalCgstAmount(Double gstr2TotalCgstAmount) {
		this.gstr2TotalCgstAmount = gstr2TotalCgstAmount;
	}

	public Double getGstr2TotalSgstAmount() {
		return gstr2TotalSgstAmount;
	}

	public void setGstr2TotalSgstAmount(Double gstr2TotalSgstAmount) {
		this.gstr2TotalSgstAmount = gstr2TotalSgstAmount;
	}

	public Double getPurchaseTotalCessAmount() {
		return purchaseTotalCessAmount;
	}

	public void setPurchaseTotalCessAmount(Double purchaseTotalCessAmount) {
		this.purchaseTotalCessAmount = purchaseTotalCessAmount;
	}

	public Double getGstr2TotalCessAmount() {
		return gstr2TotalCessAmount;
	}

	public void setGstr2TotalCessAmount(Double gstr2TotalCessAmount) {
		this.gstr2TotalCessAmount = gstr2TotalCessAmount;
	}

	@Override
	public String toString() {
		return "ReconsileInvoices [status=" + status + ", purchaseStatus=" + purchaseStatus + ", purchaseInvtype="
				+ purchaseInvtype + ", purchaseSuppliername=" + purchaseSuppliername + ", purchaseInvoiceno="
				+ purchaseInvoiceno + ", purchaseInvoicedate=" + purchaseInvoicedate + ", purchaseBranch="
				+ purchaseBranch + ", purchaseTotaltaxableamount=" + purchaseTotaltaxableamount
				+ ", purchaseTotalamount=" + purchaseTotalamount + ", gstr2status=" + gstr2status + ", gstr2Invtype="
				+ gstr2Invtype + ", gstr2Suppliername=" + gstr2Suppliername + ", gstr2Invoiceno=" + gstr2Invoiceno
				+ ", gstr2Invoicedate=" + gstr2Invoicedate + ", gstr2Branch=" + gstr2Branch
				+ ", gstr2Totaltaxableamount=" + gstr2Totaltaxableamount + ", gstr2Totalamount=" + gstr2Totalamount
				+ "]";
	}
}
