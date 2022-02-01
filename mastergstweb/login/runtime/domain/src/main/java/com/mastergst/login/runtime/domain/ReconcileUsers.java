package com.mastergst.login.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;

public class ReconcileUsers {

	private ObjectId docId;
	private String clientid;
	private ObjectId userid;
	private Long processedinvoices;
	private Date createdDate;
	private User user;
	
	private Long totalpurchaseinvoices;
	private Long totalinvoices;
	
	private Long processedpurchaseinvoices;
	private Long processedgstr2ainvoices;

	public ObjectId getDocId() {
		return docId;
	}

	public void setDocId(ObjectId docId) {
		this.docId = docId;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public ObjectId getUserid() {
		return userid;
	}

	public void setUserid(ObjectId userid) {
		this.userid = userid;
	}

	public Long getProcessedinvoices() {
		return processedinvoices;
	}

	public void setProcessedinvoices(Long processedinvoices) {
		this.processedinvoices = processedinvoices;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getProcessedpurchaseinvoices() {
		return processedpurchaseinvoices;
	}

	public void setProcessedpurchaseinvoices(Long processedpurchaseinvoices) {
		this.processedpurchaseinvoices = processedpurchaseinvoices;
	}

	public Long getProcessedgstr2ainvoices() {
		return processedgstr2ainvoices;
	}

	public void setProcessedgstr2ainvoices(Long processedgstr2ainvoices) {
		this.processedgstr2ainvoices = processedgstr2ainvoices;
	}

	public Long getTotalpurchaseinvoices() {
		return totalpurchaseinvoices;
	}

	public void setTotalpurchaseinvoices(Long totalpurchaseinvoices) {
		this.totalpurchaseinvoices = totalpurchaseinvoices;
	}

	public Long getTotalinvoices() {
		return totalinvoices;
	}

	public void setTotalinvoices(Long totalinvoices) {
		this.totalinvoices = totalinvoices;
	}
	
}