package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;
import java.math.BigDecimal;

public class InvoiceTypeSummery implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String _id;
	private BigDecimal totalAmount = new BigDecimal(0.0);
    private BigDecimal totalTaxableAmount = new BigDecimal(0.0);
    private BigDecimal totalTaxAmount = new BigDecimal(0.0); 
    private int noOfInvoices;
    private String value;
    private int order;
    private int totalInvoices;
    private int totalPending;
    private int totalUploaded;
    private int totalFailed;
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
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
	public BigDecimal getTotalTaxAmount() {
		return totalTaxAmount;
	}
	public void setTotalTaxAmount(BigDecimal totalTaxAmount) {
		this.totalTaxAmount = totalTaxAmount;
	}
	public int getNoOfInvoices() {
		return noOfInvoices;
	}
	public void setNoOfInvoices(int noOfInvoices) {
		this.noOfInvoices = noOfInvoices;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	public int getTotalInvoices() {
		return totalInvoices;
	}
	public void setTotalInvoices(int totalInvoices) {
		this.totalInvoices = totalInvoices;
	}
	public int getTotalPending() {
		return totalPending;
	}
	public void setTotalPending(int totalPending) {
		this.totalPending = totalPending;
	}
	public int getTotalUploaded() {
		return totalUploaded;
	}
	public void setTotalUploaded(int totalUploaded) {
		this.totalUploaded = totalUploaded;
	}
	public int getTotalFailed() {
		return totalFailed;
	}
	public void setTotalFailed(int totalFailed) {
		this.totalFailed = totalFailed;
	}
	
}
