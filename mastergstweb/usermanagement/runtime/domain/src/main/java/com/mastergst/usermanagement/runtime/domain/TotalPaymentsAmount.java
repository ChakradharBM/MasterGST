package com.mastergst.usermanagement.runtime.domain;

import java.math.BigDecimal;

public class TotalPaymentsAmount {
	private String _id;
	private int totalTransactions;
	private BigDecimal totalPaidAmount = new BigDecimal(0.0);
	private BigDecimal totalInvoiceAmount = new BigDecimal(0.0);
	private BigDecimal totalPendingAmount = new BigDecimal(0.0);

	private BigDecimal totalCashAmount = new BigDecimal(0.0);
	private BigDecimal totalBankAmount = new BigDecimal(0.0);
	private BigDecimal totalTdsItAmount = new BigDecimal(0.0);
	private BigDecimal totalTdsGstAmount = new BigDecimal(0.0);
	private BigDecimal totalDiscountAmount = new BigDecimal(0.0);
	private BigDecimal totalOthersAmount = new BigDecimal(0.0);

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

	public BigDecimal getTotalPaidAmount() {
		return totalPaidAmount;
	}

	public void setTotalPaidAmount(BigDecimal totalPaidAmount) {
		this.totalPaidAmount = totalPaidAmount;
	}

	public BigDecimal getTotalInvoiceAmount() {
		return totalInvoiceAmount;
	}

	public void setTotalInvoiceAmount(BigDecimal totalInvoiceAmount) {
		this.totalInvoiceAmount = totalInvoiceAmount;
	}

	public BigDecimal getTotalPendingAmount() {
		return totalPendingAmount;
	}

	public void setTotalPendingAmount(BigDecimal totalPendingAmount) {
		this.totalPendingAmount = totalPendingAmount;
	}

	public BigDecimal getTotalCashAmount() {
		return totalCashAmount;
	}

	public void setTotalCashAmount(BigDecimal totalCashAmount) {
		this.totalCashAmount = totalCashAmount;
	}

	public BigDecimal getTotalBankAmount() {
		return totalBankAmount;
	}

	public void setTotalBankAmount(BigDecimal totalBankAmount) {
		this.totalBankAmount = totalBankAmount;
	}

	public BigDecimal getTotalTdsItAmount() {
		return totalTdsItAmount;
	}

	public void setTotalTdsItAmount(BigDecimal totalTdsItAmount) {
		this.totalTdsItAmount = totalTdsItAmount;
	}

	public BigDecimal getTotalTdsGstAmount() {
		return totalTdsGstAmount;
	}

	public void setTotalTdsGstAmount(BigDecimal totalTdsGstAmount) {
		this.totalTdsGstAmount = totalTdsGstAmount;
	}

	public BigDecimal getTotalDiscountAmount() {
		return totalDiscountAmount;
	}

	public void setTotalDiscountAmount(BigDecimal totalDiscountAmount) {
		this.totalDiscountAmount = totalDiscountAmount;
	}

	public BigDecimal getTotalOthersAmount() {
		return totalOthersAmount;
	}

	public void setTotalOthersAmount(BigDecimal totalOthersAmount) {
		this.totalOthersAmount = totalOthersAmount;
	}

}
