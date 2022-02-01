package com.mastergst.usermanagement.runtime.domain;

import java.math.BigDecimal;

public class TotalInvoiceAmount {
	private String _id;
    private int totalTransactions;
    private BigDecimal totalAmount = new BigDecimal(0.0);
    private BigDecimal totalTaxableAmount = new BigDecimal(0.0);
    private BigDecimal totalExemptedAmount = new BigDecimal(0.0);
    private BigDecimal totalTaxAmount = new BigDecimal(0.0);
    private BigDecimal totalIGSTAmount = new BigDecimal(0.0);
    private BigDecimal totalCGSTAmount = new BigDecimal(0.0);
    private BigDecimal totalSGSTAmount = new BigDecimal(0.0);
    private BigDecimal totalCESSAmount = new BigDecimal(0.0);
    private BigDecimal totalITCAvailable = new BigDecimal(0.0);
    private BigDecimal tcsTdsAmount = new BigDecimal(0.0);
    private BigDecimal tdsAmount = new BigDecimal(0.0);
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
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	public BigDecimal getTotalTaxableAmount() {
		return totalTaxableAmount;
	}
	public void setTotalTaxableAmount(BigDecimal totalTaxableAmount) {
		this.totalTaxableAmount = totalTaxableAmount;
	}
	public BigDecimal getTotalExemptedAmount() {
		return totalExemptedAmount;
	}
	public void setTotalExemptedAmount(BigDecimal totalExemptedAmount) {
		this.totalExemptedAmount = totalExemptedAmount;
	}
	public BigDecimal getTotalTaxAmount() {
		return totalTaxAmount;
	}
	public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}
	public BigDecimal getTotalIGSTAmount() {
		return totalIGSTAmount;
	}
	public void setTotalIGSTAmount(BigDecimal totalIGSTAmount) {
		this.totalIGSTAmount = totalIGSTAmount;
	}
	public BigDecimal getTotalCGSTAmount() {
		return totalCGSTAmount;
	}
	public void setTotalCGSTAmount(BigDecimal totalCGSTAmount) {
		this.totalCGSTAmount = totalCGSTAmount;
	}
	public BigDecimal getTotalSGSTAmount() {
		return totalSGSTAmount;
	}
	public void setTotalSGSTAmount(BigDecimal totalSGSTAmount) {
		this.totalSGSTAmount = totalSGSTAmount;
	}
	public BigDecimal getTotalCESSAmount() {
		return totalCESSAmount;
	}
	public void setTotalCESSAmount(BigDecimal totalCESSAmount) {
		this.totalCESSAmount = totalCESSAmount;
	}
	public BigDecimal getTotalITCAvailable() {
		return totalITCAvailable;
	}
	public void setTotalITCAvailable(BigDecimal totalITCAvailable) {
		this.totalITCAvailable = totalITCAvailable;
	}
	public BigDecimal getTcsTdsAmount() {
		return tcsTdsAmount;
	}
	public void setTcsTdsAmount(BigDecimal tcsTdsAmount) {
		this.tcsTdsAmount = tcsTdsAmount;
	}
	public BigDecimal getTdsAmount() {
		return tdsAmount;
	}
	public void setTdsAmount(BigDecimal tdsAmount) {
		this.tdsAmount = tdsAmount;
	}  
	
	
}
