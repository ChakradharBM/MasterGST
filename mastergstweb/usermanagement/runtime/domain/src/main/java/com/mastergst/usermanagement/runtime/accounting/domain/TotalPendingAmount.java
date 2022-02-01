package com.mastergst.usermanagement.runtime.accounting.domain;

import java.math.BigDecimal;

public class TotalPendingAmount {

	private String _id;
    private int totalTransactions;
    private BigDecimal pendingAmount = new BigDecimal(0.0);
    
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
	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}
	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
    
    
    
}
