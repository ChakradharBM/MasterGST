package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.google.common.collect.Lists;
import com.mastergst.core.domain.Base;

@Document(collection = "expenses")
public class Expenses extends Base{
	private String userid;
	private String clientid;
	private String paymentMode;
	private String ledgerName;
	private String branchName;
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date paymentDate;
	private String comments;
	private Double totalAmount;
	String mthCd;
	String qrtCd;
	String yrCd;
	int sftr;
	private List<ExpenseDetails> expenses = Lists.newArrayList();
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public Double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
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
	public int getSftr() {
		return sftr;
	}
	public void setSftr(int sftr) {
		this.sftr = sftr;
	}
	public List<ExpenseDetails> getExpenses() {
		return expenses;
	}
	public void setExpenses(List<ExpenseDetails> expenses) {
		this.expenses = expenses;
	}
	public String getPaymentMode() {
		return paymentMode;
	}
	public void setPaymentMode(String paymentMode) {
		this.paymentMode = paymentMode;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public String getBranchName() {
		return branchName;
	}
	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	
}
