package com.mastergst.usermanagement.runtime.accounting.domain;

import java.math.BigDecimal;

public class TotalJournalAmount {
	private String _id;
    private int totalTransactions;
    private BigDecimal invoiceamount = new BigDecimal(0.0);
    private BigDecimal tcs_payable = new BigDecimal(0.0);
    private BigDecimal tds_payable = new BigDecimal(0.0);
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public int getTotalTransactions() {
		return totalTransactions;
	}
	public void setTotalTransactions(int totalTransactions) {
		this.totalTransactions = totalTransactions;
	}
	public BigDecimal getInvoiceamount() {
		return invoiceamount;
	}
	public void setInvoiceamount(BigDecimal invoiceamount) {
		this.invoiceamount = invoiceamount;
	}
	public BigDecimal getTcs_payable() {
		return tcs_payable;
	}
	public void setTcs_payable(BigDecimal tcs_payable) {
		this.tcs_payable = tcs_payable;
	}
	public BigDecimal getTds_payable() {
		return tds_payable;
	}
	public void setTds_payable(BigDecimal tds_payable) {
		this.tds_payable = tds_payable;
	}
    
    
}
