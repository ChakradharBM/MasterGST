package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "record_payment")
public class Payments extends Base implements Serializable {
	private static final long serialVersionUID = 1L;

	private String docId;
	//private ObjectId invoiceDocId;
	private String userid;
	private String clientid;
	private String voucherNumber;
	private String invoiceNumber;
	private String modeOfPayment;
	private Double amount;
	private String paymentDate;
	private String referenceNumber;
	private Double pendingBalance;
	private String customerName;
	private String gstNumber;
	private String returntype;
	private String receivedAmount;
	private String invoiceid;
	private Double previousPendingBalance;
	
	private Double paidAmount;
	private Double cashAmount;
	private Double bankAmount;
	private Double tdsItAmount;
	private Double tdsGstAmount;
	private Double discountAmount;
	private Double othersAmount;
	
	private String bankname;
	private String accountnumber;
	private String accountName;
	private String branchname;
	private String ifsccode;
	
	private Boolean isadvadjust;
	private String advpmntrecno;
	private String advpmntrecdate;
	private String advpmntpos;
	private Double advpmntrecamt;
	private Double advpmntavailadjust;
	private Double advpmntadjust;
	private Double advpmnttaxrate;
	private Double advpmntigstamt;
	private Double advpmntcgstamt;
	private Double advpmntsgstamt;
	private Double advpmntcessrate;
	private Double advpmntcessamt;
	private Double advpmntremainamt;
	private String invtype;
	private String mthCd;
	private String qrtCd;
	private String yrCd;
	
	private List<PaymentItems> paymentitems = LazyList.decorate(new ArrayList<PaymentItems>(), 
			FactoryUtils.instantiateFactory(PaymentItems.class));

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public Double getPendingBalance() {
		return pendingBalance;
	}

	public void setPendingBalance(Double pendingBalance) {
		this.pendingBalance = pendingBalance;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getGstNumber() {
		return gstNumber;
	}

	public void setGstNumber(String gstNumber) {
		this.gstNumber = gstNumber;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getModeOfPayment() {
		return modeOfPayment;
	}

	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getPaymentDate() {
		return paymentDate;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public void setPaymentDate(String paymentDate) {
		this.paymentDate = paymentDate;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getReceivedAmount() {
		return receivedAmount;
	}

	public void setReceivedAmount(String receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public String getInvoiceid() {
		return invoiceid;
	}

	public void setInvoiceid(String invoiceid) {
		this.invoiceid = invoiceid;
	}

	public List<PaymentItems> getPaymentitems() {
		return paymentitems;
	}

	public void setPaymentitems(List<PaymentItems> paymentitems) {
		this.paymentitems = paymentitems;
	}

	public Double getPreviousPendingBalance() {
		return previousPendingBalance;
	}

	public void setPreviousPendingBalance(Double previousPendingBalance) {
		this.previousPendingBalance = previousPendingBalance;
	}

	public String getBankname() {
		return bankname;
	}

	public void setBankname(String bankname) {
		this.bankname = bankname;
	}

	public String getAccountnumber() {
		return accountnumber;
	}

	public void setAccountnumber(String accountnumber) {
		this.accountnumber = accountnumber;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getBranchname() {
		return branchname;
	}

	public void setBranchname(String branchname) {
		this.branchname = branchname;
	}

	public String getIfsccode() {
		return ifsccode;
	}

	public void setIfsccode(String ifsccode) {
		this.ifsccode = ifsccode;
	}

	public String getAdvpmntrecno() {
		return advpmntrecno;
	}

	public void setAdvpmntrecno(String advpmntrecno) {
		this.advpmntrecno = advpmntrecno;
	}

	public String getAdvpmntpos() {
		return advpmntpos;
	}

	public void setAdvpmntpos(String advpmntpos) {
		this.advpmntpos = advpmntpos;
	}

	public Double getAdvpmntrecamt() {
		return advpmntrecamt;
	}

	public void setAdvpmntrecamt(Double advpmntrecamt) {
		this.advpmntrecamt = advpmntrecamt;
	}

	public Double getAdvpmntavailadjust() {
		return advpmntavailadjust;
	}

	public void setAdvpmntavailadjust(Double advpmntavailadjust) {
		this.advpmntavailadjust = advpmntavailadjust;
	}

	public Double getAdvpmntadjust() {
		return advpmntadjust;
	}

	public void setAdvpmntadjust(Double advpmntadjust) {
		this.advpmntadjust = advpmntadjust;
	}

	public Double getAdvpmnttaxrate() {
		return advpmnttaxrate;
	}

	public void setAdvpmnttaxrate(Double advpmnttaxrate) {
		this.advpmnttaxrate = advpmnttaxrate;
	}



	public Boolean getIsadvadjust() {
		return isadvadjust;
	}

	public void setIsadvadjust(Boolean isadvadjust) {
		this.isadvadjust = isadvadjust;
	}

	public String getAdvpmntrecdate() {
		return advpmntrecdate;
	}

	public void setAdvpmntrecdate(String advpmntrecdate) {
		this.advpmntrecdate = advpmntrecdate;
	}

	public Double getAdvpmntigstamt() {
		return advpmntigstamt;
	}

	public void setAdvpmntigstamt(Double advpmntigstamt) {
		this.advpmntigstamt = advpmntigstamt;
	}

	public Double getAdvpmntcgstamt() {
		return advpmntcgstamt;
	}

	public void setAdvpmntcgstamt(Double advpmntcgstamt) {
		this.advpmntcgstamt = advpmntcgstamt;
	}

	public Double getAdvpmntsgstamt() {
		return advpmntsgstamt;
	}

	public void setAdvpmntsgstamt(Double advpmntsgstamt) {
		this.advpmntsgstamt = advpmntsgstamt;
	}

	public Double getAdvpmntcessrate() {
		return advpmntcessrate;
	}

	public void setAdvpmntcessrate(Double advpmntcessrate) {
		this.advpmntcessrate = advpmntcessrate;
	}

	public Double getAdvpmntcessamt() {
		return advpmntcessamt;
	}

	public void setAdvpmntcessamt(Double advpmntcessamt) {
		this.advpmntcessamt = advpmntcessamt;
	}

	public Double getAdvpmntremainamt() {
		return advpmntremainamt;
	}

	public void setAdvpmntremainamt(Double advpmntremainamt) {
		this.advpmntremainamt = advpmntremainamt;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getQrtCd() {
		return qrtCd;
	}

	public void setQrtCd(String qrtCd) {
		this.qrtCd = qrtCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	public Double getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}

	public Double getBankAmount() {
		return bankAmount;
	}

	public void setBankAmount(Double bankAmount) {
		this.bankAmount = bankAmount;
	}

	public Double getTdsItAmount() {
		return tdsItAmount;
	}

	public void setTdsItAmount(Double tdsItAmount) {
		this.tdsItAmount = tdsItAmount;
	}

	public Double getTdsGstAmount() {
		return tdsGstAmount;
	}

	public void setTdsGstAmount(Double tdsGstAmount) {
		this.tdsGstAmount = tdsGstAmount;
	}

	public Double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(Double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public Double getOthersAmount() {
		return othersAmount;
	}

	public void setOthersAmount(Double othersAmount) {
		this.othersAmount = othersAmount;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public Double getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(Double paidAmount) {
		this.paidAmount = paidAmount;
	}

	/*public ObjectId getInvoiceDocId() {
		return invoiceDocId;
	}

	public void setInvoiceDocId(ObjectId invoiceDocId) {
		this.invoiceDocId = invoiceDocId;
	}*/
}
