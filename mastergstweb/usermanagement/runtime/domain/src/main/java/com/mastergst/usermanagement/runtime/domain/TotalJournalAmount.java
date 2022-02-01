package com.mastergst.usermanagement.runtime.domain;

import java.math.BigDecimal;

public class TotalJournalAmount {
	private String _id;
    private int totalTransactions;
    private BigDecimal totalCredit = new BigDecimal(0.0);
    private BigDecimal totalDebit = new BigDecimal(0.0);
    
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
	public BigDecimal getTotalCredit() {
		return totalCredit;
	}
	public void setTotalCredit(BigDecimal totalCredit) {
		this.totalCredit = totalCredit;
	}
	public BigDecimal getTotalDebit() {
		return totalDebit;
	}
	public void setTotalDebit(BigDecimal totalDebit) {
		this.totalDebit = totalDebit;
	}
    

}
