package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

public class InvoicePayments {
	private String docId;
	private String invoiceno;
	private String invtype;
	private String billedtoname;
	private String paymentStatus;
	private Double paidamount;
	private Double receivedAmount;
	private Double pendingAmount;

	private Double totalamount;
	private int days;

	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dateofinvoice;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date dueDate;

	private List<Payments> payments = new ArrayList<Payments>();

	public String getInvoiceno() {
		return invoiceno;
	}

	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getBilledtoname() {
		return billedtoname;
	}

	public void setBilledtoname(String billedtoname) {
		this.billedtoname = billedtoname;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public Date getDateofinvoice() {
		return dateofinvoice;
	}

	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public List<Payments> getPayments() {
		return payments;
	}

	public void setPayments(List<Payments> payments) {
		this.payments = payments;
	}

	public Double getPaidamount() {
		return paidamount;
	}

	public void setPaidamount(Double paidamount) {
		this.paidamount = paidamount;
	}

	public Double getTotalamount() {
		return totalamount;
	}

	public void setTotalamount(Double totalamount) {
		this.totalamount = totalamount;
	}

	public int getDays() {
		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public Double getReceivedAmount() {
		return receivedAmount;
	}

	public void setReceivedAmount(Double receivedAmount) {
		this.receivedAmount = receivedAmount;
	}

	public Double getPendingAmount() {
		return pendingAmount;
	}

	public void setPendingAmount(Double pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
}
