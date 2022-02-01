package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;

@Document(collection = "accounting_journal")
public class AccountingJournal extends Base {
	private String accountingJournalId;
	private String version;

	// cr=by
	// dr=to
	private List<JournalEntrie> crEntrie;
	private List<JournalEntrie> drEntrie;

	private String clientId;
	private String userId;
	private String invoiceId;
	private String ledgerName;
	private String returnType;
	private String invoiceType;

	/* start ledger report purpose */
	private Double output_igst;
	private Double output_cgst;
	private Double output_sgst;
	private Double output_cess;

	private Double input_igst;
	private Double input_cgst;
	private Double input_sgst;
	private Double input_cess;

	private Double output_igst_rcm;
	private Double output_cgst_rcm;
	private Double output_sgst_rcm;
	private Double output_cess_rcm;

	private Double input_igst_rcm;
	private Double input_cgst_rcm;
	private Double input_sgst_rcm;
	private Double input_cess_rcm;

	private Double tax_on_advance;

	private Double tcs_payable;
	private Double tcs_receivable;
	private Double tds_payable;
	private Double tds_receivable;

	private Double igst_refund_receivable;
	private Double cess_refund_receivable;
	/* start ledger report purpose */

	private Double igstamount;
	private Double sgstamount;
	private Double cgstamount;
	private Double exemptedamount;
	private Double icsgstamountrcm;
	private Double toicsgstamountrcm;
	private Double tdsamount;
	private Double customerorSupplierAccount;
	private Double salesorPurchases;
	private String rcmorinterorintra;
	private String interorintra;
	private String invoiceNumber;
	private String journalNumber;
	private String itcinelg;
	private String vendorName;
	private String status;
	private Double rigstamount;
	private Double rsgstamount;
	private Double rcgstamount;
	private Double rcessamount;
	private Double cessAmount;
	private Double isdineligiblecredit;
	private Double paymentReceivedAmount;
	private int noofpayments;
	private Double tdstcsAmount;

	private String voucherNumber;
	private String voucherDate;
	private String vouchernotes;
	private String vouchertype;
	private Double totdramount;
	private Double totcramount;

	private String contraNumber;
	private String contraDate;
	private String contranotes;
	private String creditDebitNoteType;

	private String invType;

	private Double creditTotal;
	private Double debitTotal;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	Date dateofinvoice;

	String invoiceMonth;
	private Double roundOffAmount;
	String mthCd;
	String qrtCd;
	String yrCd;
	
	private String ctin;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dueDate;
	private String tcspercentage;
	private String tcssection;
	private String tdspercentage;
	private String tdssection;
	private String pancategory;
	private Double invoiceamount;

	List<AccountingJournalPaymentItems> paymentitems = LazyList.decorate(new ArrayList<AccountingJournalPaymentItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalPaymentItems.class));

	List<AccountingJournalVoucherItems> voucheritems = LazyList.decorate(new ArrayList<AccountingJournalVoucherItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalVoucherItems.class));

	List<AccountingJournalVoucherItems> journalvoucheritems = LazyList.decorate(
			new ArrayList<AccountingJournalVoucherItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalVoucherItems.class));

	List<AccountingJournalVoucherItems> contraitems = LazyList.decorate(new ArrayList<AccountingJournalVoucherItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalVoucherItems.class));

	List<AccountingJournalVoucherItems> journalcontraitems = LazyList.decorate(
			new ArrayList<AccountingJournalVoucherItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalVoucherItems.class));

	List<AccountingJournalExpensesItems> journalexpensesitems = LazyList.decorate(
			new ArrayList<AccountingJournalExpensesItems>(),
			FactoryUtils.instantiateFactory(AccountingJournalExpensesItems.class));

	List<Item> items = LazyList.decorate(new ArrayList<Item>(), FactoryUtils.instantiateFactory(Item.class));

	// below variable download excel purpose
	private String ledgerDate;
	private String trnName;
	private String accNumber;
	private String bglDescription;
	private Double amount;

	public AccountingJournal() {
		super();
		version = "v1.0";
	}

	public AccountingJournal(String ledgerDate, String trnName, String journalNumber, String accNumber,
			String bglDescription, Double amount, String invoiceNumber) {
		super();
		this.ledgerDate = ledgerDate;
		this.trnName = trnName;
		this.journalNumber = journalNumber;
		this.amount = amount;
		this.invoiceNumber = invoiceNumber;
		this.accNumber = accNumber;
		this.bglDescription = bglDescription;
	}

	// above variable download excel purpose
	public String getItcinelg() {
		return itcinelg;
	}

	public void setItcinelg(String itcinelg) {
		this.itcinelg = itcinelg;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getLedgerName() {
		return ledgerName;
	}

	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public Double getIgstamount() {
		return igstamount;
	}

	public void setIgstamount(Double igstamount) {
		this.igstamount = igstamount;
	}

	public Double getSgstamount() {
		return sgstamount;
	}

	public void setSgstamount(Double sgstamount) {
		this.sgstamount = sgstamount;
	}

	public Double getCgstamount() {
		return cgstamount;
	}

	public void setCgstamount(Double cgstamount) {
		this.cgstamount = cgstamount;
	}

	public Double getCustomerorSupplierAccount() {
		return customerorSupplierAccount;
	}

	public void setCustomerorSupplierAccount(Double customerorSupplierAccount) {
		this.customerorSupplierAccount = customerorSupplierAccount;
	}

	public Double getSalesorPurchases() {
		return salesorPurchases;
	}

	public void setSalesorPurchases(Double salesorPurchases) {
		this.salesorPurchases = salesorPurchases;
	}

	public Double getIcsgstamountrcm() {
		return icsgstamountrcm;
	}

	public void setIcsgstamountrcm(Double icsgstamountrcm) {
		this.icsgstamountrcm = icsgstamountrcm;
	}

	public Double getToicsgstamountrcm() {
		return toicsgstamountrcm;
	}

	public void setToicsgstamountrcm(Double toicsgstamountrcm) {
		this.toicsgstamountrcm = toicsgstamountrcm;
	}

	public Double getTdsamount() {
		return tdsamount;
	}

	public void setTdsamount(Double tdsamount) {
		this.tdsamount = tdsamount;
	}

	public String getRcmorinterorintra() {
		return rcmorinterorintra;
	}

	public void setRcmorinterorintra(String rcmorinterorintra) {
		this.rcmorinterorintra = rcmorinterorintra;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	public String getJournalNumber() {
		return journalNumber;
	}

	public void setJournalNumber(String journalNumber) {
		this.journalNumber = journalNumber;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getInterorintra() {
		return interorintra;
	}

	public void setInterorintra(String interorintra) {
		this.interorintra = interorintra;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Double getRigstamount() {
		return rigstamount;
	}

	public void setRigstamount(Double rigstamount) {
		this.rigstamount = rigstamount;
	}

	public Double getRsgstamount() {
		return rsgstamount;
	}

	public void setRsgstamount(Double rsgstamount) {
		this.rsgstamount = rsgstamount;
	}

	public Double getRcgstamount() {
		return rcgstamount;
	}

	public void setRcgstamount(Double rcgstamount) {
		this.rcgstamount = rcgstamount;
	}

	public Double getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(Double cessAmount) {
		this.cessAmount = cessAmount;
	}

	public Double getIsdineligiblecredit() {
		return isdineligiblecredit;
	}

	public void setIsdineligiblecredit(Double isdineligiblecredit) {
		this.isdineligiblecredit = isdineligiblecredit;
	}

	public List<AccountingJournalPaymentItems> getPaymentitems() {
		return paymentitems;
	}

	public void setPaymentitems(List<AccountingJournalPaymentItems> paymentitems) {
		this.paymentitems = paymentitems;
	}

	public Double getPaymentReceivedAmount() {
		return paymentReceivedAmount;
	}

	public void setPaymentReceivedAmount(Double paymentReceivedAmount) {
		this.paymentReceivedAmount = paymentReceivedAmount;
	}

	public int getNoofpayments() {
		return noofpayments;
	}

	public void setNoofpayments(int noofpayments) {
		this.noofpayments = noofpayments;
	}

	public Double getTdstcsAmount() {
		return tdstcsAmount;
	}

	public void setTdstcsAmount(Double tdstcsAmount) {
		this.tdstcsAmount = tdstcsAmount;
	}

	public String getLedgerDate() {
		return ledgerDate;
	}

	public void setLedgerDate(String ledgerDate) {
		this.ledgerDate = ledgerDate;
	}

	public String getTrnName() {
		return trnName;
	}

	public void setTrnName(String trnName) {
		this.trnName = trnName;
	}

	public String getAccNumber() {
		return accNumber;
	}

	public void setAccNumber(String accNumber) {
		this.accNumber = accNumber;
	}

	public String getBglDescription() {
		return bglDescription;
	}

	public void setBglDescription(String bglDescription) {
		this.bglDescription = bglDescription;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public List<AccountingJournalVoucherItems> getVoucheritems() {
		return voucheritems;
	}

	public void setVoucheritems(List<AccountingJournalVoucherItems> voucheritems) {
		this.voucheritems = voucheritems;
	}

	public String getVoucherNumber() {
		return voucherNumber;
	}

	public void setVoucherNumber(String voucherNumber) {
		this.voucherNumber = voucherNumber;
	}

	public String getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(String voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getVouchernotes() {
		return vouchernotes;
	}

	public void setVouchernotes(String vouchernotes) {
		this.vouchernotes = vouchernotes;
	}

	public Double getTotdramount() {
		return totdramount;
	}

	public void setTotdramount(Double totdramount) {
		this.totdramount = totdramount;
	}

	public Double getTotcramount() {
		return totcramount;
	}

	public void setTotcramount(Double totcramount) {
		this.totcramount = totcramount;
	}

	public List<AccountingJournalVoucherItems> getJournalvoucheritems() {
		return journalvoucheritems;
	}

	public void setJournalvoucheritems(List<AccountingJournalVoucherItems> journalvoucheritems) {
		this.journalvoucheritems = journalvoucheritems;
	}

	public List<AccountingJournalVoucherItems> getContraitems() {
		return contraitems;
	}

	public void setContraitems(List<AccountingJournalVoucherItems> contraitems) {
		this.contraitems = contraitems;
	}

	public List<AccountingJournalVoucherItems> getJournalcontraitems() {
		return journalcontraitems;
	}

	public void setJournalcontraitems(List<AccountingJournalVoucherItems> journalcontraitems) {
		this.journalcontraitems = journalcontraitems;
	}

	public String getContraNumber() {
		return contraNumber;
	}

	public void setContraNumber(String contraNumber) {
		this.contraNumber = contraNumber;
	}

	public String getContraDate() {
		return contraDate;
	}

	public void setContraDate(String contraDate) {
		this.contraDate = contraDate;
	}

	public String getContranotes() {
		return contranotes;
	}

	public void setContranotes(String contranotes) {
		this.contranotes = contranotes;
	}

	public Date getDateofinvoice() {
		return dateofinvoice;
	}

	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}

	public String getInvoiceMonth() {
		return invoiceMonth;
	}

	public void setInvoiceMonth(String invoiceMonth) {
		this.invoiceMonth = invoiceMonth;
	}

	public String getCreditDebitNoteType() {
		return creditDebitNoteType;
	}

	public void setCreditDebitNoteType(String creditDebitNoteType) {
		this.creditDebitNoteType = creditDebitNoteType;
	}

	public String getAccountingJournalId() {
		return accountingJournalId;
	}

	public void setAccountingJournalId(String accountingJournalId) {
		this.accountingJournalId = accountingJournalId;
	}

	public Double getRoundOffAmount() {
		return roundOffAmount;
	}

	public void setRoundOffAmount(Double roundOffAmount) {
		this.roundOffAmount = roundOffAmount;
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

	public Double getCreditTotal() {
		return creditTotal;
	}

	public void setCreditTotal(Double creditTotal) {
		this.creditTotal = creditTotal;
	}

	public Double getDebitTotal() {
		return debitTotal;
	}

	public void setDebitTotal(Double debitTotal) {
		this.debitTotal = debitTotal;
	}

	public Double getRcessamount() {
		return rcessamount;
	}

	public void setRcessamount(Double rcessamount) {
		this.rcessamount = rcessamount;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}

	public Double getExemptedamount() {
		return exemptedamount;
	}

	public void setExemptedamount(Double exemptedamount) {
		this.exemptedamount = exemptedamount;
	}

	public String getInvType() {
		return invType;
	}

	public void setInvType(String invType) {
		this.invType = invType;
	}

	public String getVouchertype() {
		return vouchertype;
	}

	public void setVouchertype(String vouchertype) {
		this.vouchertype = vouchertype;
	}

	public List<AccountingJournalExpensesItems> getJournalexpensesitems() {
		return journalexpensesitems;
	}

	public void setJournalexpensesitems(List<AccountingJournalExpensesItems> journalexpensesitems) {
		this.journalexpensesitems = journalexpensesitems;
	}

	public Double getOutput_igst() {
		return output_igst;
	}

	public void setOutput_igst(Double output_igst) {
		this.output_igst = output_igst;
	}

	public Double getOutput_cgst() {
		return output_cgst;
	}

	public void setOutput_cgst(Double output_cgst) {
		this.output_cgst = output_cgst;
	}

	public Double getOutput_sgst() {
		return output_sgst;
	}

	public void setOutput_sgst(Double output_sgst) {
		this.output_sgst = output_sgst;
	}

	public Double getOutput_cess() {
		return output_cess;
	}

	public void setOutput_cess(Double output_cess) {
		this.output_cess = output_cess;
	}

	public Double getInput_igst() {
		return input_igst;
	}

	public void setInput_igst(Double input_igst) {
		this.input_igst = input_igst;
	}

	public Double getInput_cgst() {
		return input_cgst;
	}

	public void setInput_cgst(Double input_cgst) {
		this.input_cgst = input_cgst;
	}

	public Double getInput_sgst() {
		return input_sgst;
	}

	public void setInput_sgst(Double input_sgst) {
		this.input_sgst = input_sgst;
	}

	public Double getInput_cess() {
		return input_cess;
	}

	public void setInput_cess(Double input_cess) {
		this.input_cess = input_cess;
	}

	public Double getOutput_igst_rcm() {
		return output_igst_rcm;
	}

	public void setOutput_igst_rcm(Double output_igst_rcm) {
		this.output_igst_rcm = output_igst_rcm;
	}

	public Double getOutput_cgst_rcm() {
		return output_cgst_rcm;
	}

	public void setOutput_cgst_rcm(Double output_cgst_rcm) {
		this.output_cgst_rcm = output_cgst_rcm;
	}

	public Double getOutput_sgst_rcm() {
		return output_sgst_rcm;
	}

	public void setOutput_sgst_rcm(Double output_sgst_rcm) {
		this.output_sgst_rcm = output_sgst_rcm;
	}

	public Double getOutput_cess_rcm() {
		return output_cess_rcm;
	}

	public void setOutput_cess_rcm(Double output_cess_rcm) {
		this.output_cess_rcm = output_cess_rcm;
	}

	public Double getInput_igst_rcm() {
		return input_igst_rcm;
	}

	public void setInput_igst_rcm(Double input_igst_rcm) {
		this.input_igst_rcm = input_igst_rcm;
	}

	public Double getInput_cgst_rcm() {
		return input_cgst_rcm;
	}

	public void setInput_cgst_rcm(Double input_cgst_rcm) {
		this.input_cgst_rcm = input_cgst_rcm;
	}

	public Double getInput_sgst_rcm() {
		return input_sgst_rcm;
	}

	public void setInput_sgst_rcm(Double input_sgst_rcm) {
		this.input_sgst_rcm = input_sgst_rcm;
	}

	public Double getInput_cess_rcm() {
		return input_cess_rcm;
	}

	public void setInput_cess_rcm(Double input_cess_rcm) {
		this.input_cess_rcm = input_cess_rcm;
	}

	public Double getTax_on_advance() {
		return tax_on_advance;
	}

	public void setTax_on_advance(Double tax_on_advance) {
		this.tax_on_advance = tax_on_advance;
	}

	public Double getTcs_payable() {
		return tcs_payable;
	}

	public void setTcs_payable(Double tcs_payable) {
		this.tcs_payable = tcs_payable;
	}

	public Double getTcs_receivable() {
		return tcs_receivable;
	}

	public void setTcs_receivable(Double tcs_receivable) {
		this.tcs_receivable = tcs_receivable;
	}

	public Double getTds_payable() {
		return tds_payable;
	}

	public void setTds_payable(Double tds_payable) {
		this.tds_payable = tds_payable;
	}

	public Double getTds_receivable() {
		return tds_receivable;
	}

	public void setTds_receivable(Double tds_receivable) {
		this.tds_receivable = tds_receivable;
	}

	public Double getIgst_refund_receivable() {
		return igst_refund_receivable;
	}

	public void setIgst_refund_receivable(Double igst_refund_receivable) {
		this.igst_refund_receivable = igst_refund_receivable;
	}

	public Double getCess_refund_receivable() {
		return cess_refund_receivable;
	}

	public void setCess_refund_receivable(Double cess_refund_receivable) {
		this.cess_refund_receivable = cess_refund_receivable;
	}

	public List<JournalEntrie> getCrEntrie() {
		return crEntrie;
	}

	public void setCrEntrie(List<JournalEntrie> crEntrie) {
		this.crEntrie = crEntrie;
	}

	public List<JournalEntrie> getDrEntrie() {
		return drEntrie;
	}

	public void setDrEntrie(List<JournalEntrie> drEntrie) {
		this.drEntrie = drEntrie;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public String getTcspercentage() {
		return tcspercentage;
	}

	public void setTcspercentage(String tcspercentage) {
		this.tcspercentage = tcspercentage;
	}

	public String getTcssection() {
		return tcssection;
	}

	public void setTcssection(String tcssection) {
		this.tcssection = tcssection;
	}

	public String getTdspercentage() {
		return tdspercentage;
	}

	public void setTdspercentage(String tdspercentage) {
		this.tdspercentage = tdspercentage;
	}

	public String getTdssection() {
		return tdssection;
	}

	public void setTdssection(String tdssection) {
		this.tdssection = tdssection;
	}

	public String getPancategory() {
		return pancategory;
	}

	public void setPancategory(String pancategory) {
		this.pancategory = pancategory;
	}

	public Double getInvoiceamount() {
		return invoiceamount;
	}

	public void setInvoiceamount(Double invoiceamount) {
		this.invoiceamount = invoiceamount;
	}
	
	
}
