package com.mastergst.configuration.service;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "reconciletemp")
public class ReconcileTemp extends Base {
	private ObjectId userid;
	private String clientid;
	private String returntype;
	private Long processedinvoices;
	private String invtype;
	private String monthlyoryearly;
	private Long totalinvoices;
	private Long processedgstr2ainvoices;
	
	private Long totalpurchaseinvoices;
	private Long processedpurchaseinvoices;
	
	private Long totalb2binvoices;
	private Long processedgstr2ab2binvoices;
	
	private Long totalpurchaseb2binvoices;
	private Long processedpurchaseb2binvoices;
	
	private Long totalcreditinvoices;
	private Long processedgstr2acreditinvoices;
	
	private Long totalpurchasecreditinvoices;
	private Long processedpurchasecreditinvoices;
	
	private Long totalimpginvoices;
	private Long processedgstr2aimpginvoices;
	
	private Long totalpurchaseimpginvoices;
	private Long processedpurchaseimpginvoices;
	
	
	private String fullname;
	private String initiateduserid;
	
	public ReconcileTemp(String clientid, long processedinvoices, String invtype) {
		super();
		this.clientid = clientid;
		this.processedinvoices = processedinvoices;
		this.invtype = invtype;
	}
	
	public ReconcileTemp(String clientid, long processedinvoices,String monthlyoryearly,long totalinvoices) {
		super();
		this.clientid = clientid;
		this.processedinvoices = processedinvoices;
		this.monthlyoryearly = monthlyoryearly;
		this.totalinvoices = totalinvoices;
	}
	
	public ReconcileTemp(String clientid, long processedinvoices, String invtype,String monthlyoryearly,long totalinvoices) {
		super();
		this.clientid = clientid;
		this.processedinvoices = processedinvoices;
		this.invtype = invtype;
		this.monthlyoryearly = monthlyoryearly;
		this.totalinvoices = totalinvoices;
	}
	
	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}
	public ReconcileTemp() {
		super();

	}

	public ObjectId getUserid() {
		return userid;
	}

	public void setUserid(ObjectId userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public Long getProcessedinvoices() {
		return processedinvoices;
	}

	public void setProcessedinvoices(Long processedinvoices) {
		this.processedinvoices = processedinvoices;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getMonthlyoryearly() {
		return monthlyoryearly;
	}

	public void setMonthlyoryearly(String monthlyoryearly) {
		this.monthlyoryearly = monthlyoryearly;
	}

	public Long getTotalinvoices() {
		return totalinvoices;
	}

	public void setTotalinvoices(Long totalinvoices) {
		this.totalinvoices = totalinvoices;
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

	public Long getProcessedpurchaseinvoices() {
		return processedpurchaseinvoices;
	}

	public void setProcessedpurchaseinvoices(Long processedpurchaseinvoices) {
		this.processedpurchaseinvoices = processedpurchaseinvoices;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getInitiateduserid() {
		return initiateduserid;
	}

	public void setInitiateduserid(String initiateduserid) {
		this.initiateduserid = initiateduserid;
	}

	public Long getTotalb2binvoices() {
		return totalb2binvoices;
	}

	public void setTotalb2binvoices(Long totalb2binvoices) {
		this.totalb2binvoices = totalb2binvoices;
	}

	public Long getProcessedgstr2ab2binvoices() {
		return processedgstr2ab2binvoices;
	}

	public void setProcessedgstr2ab2binvoices(Long processedgstr2ab2binvoices) {
		this.processedgstr2ab2binvoices = processedgstr2ab2binvoices;
	}

	public Long getTotalpurchaseb2binvoices() {
		return totalpurchaseb2binvoices;
	}

	public void setTotalpurchaseb2binvoices(Long totalpurchaseb2binvoices) {
		this.totalpurchaseb2binvoices = totalpurchaseb2binvoices;
	}

	public Long getProcessedpurchaseb2binvoices() {
		return processedpurchaseb2binvoices;
	}

	public void setProcessedpurchaseb2binvoices(Long processedpurchaseb2binvoices) {
		this.processedpurchaseb2binvoices = processedpurchaseb2binvoices;
	}

	public Long getTotalcreditinvoices() {
		return totalcreditinvoices;
	}

	public void setTotalcreditinvoices(Long totalcreditinvoices) {
		this.totalcreditinvoices = totalcreditinvoices;
	}

	public Long getProcessedgstr2acreditinvoices() {
		return processedgstr2acreditinvoices;
	}

	public void setProcessedgstr2acreditinvoices(Long processedgstr2acreditinvoices) {
		this.processedgstr2acreditinvoices = processedgstr2acreditinvoices;
	}

	public Long getTotalpurchasecreditinvoices() {
		return totalpurchasecreditinvoices;
	}

	public void setTotalpurchasecreditinvoices(Long totalpurchasecreditinvoices) {
		this.totalpurchasecreditinvoices = totalpurchasecreditinvoices;
	}

	public Long getProcessedpurchasecreditinvoices() {
		return processedpurchasecreditinvoices;
	}

	public void setProcessedpurchasecreditinvoices(Long processedpurchasecreditinvoices) {
		this.processedpurchasecreditinvoices = processedpurchasecreditinvoices;
	}

	public Long getTotalimpginvoices() {
		return totalimpginvoices;
	}

	public void setTotalimpginvoices(Long totalimpginvoices) {
		this.totalimpginvoices = totalimpginvoices;
	}

	public Long getProcessedgstr2aimpginvoices() {
		return processedgstr2aimpginvoices;
	}

	public void setProcessedgstr2aimpginvoices(Long processedgstr2aimpginvoices) {
		this.processedgstr2aimpginvoices = processedgstr2aimpginvoices;
	}

	public Long getTotalpurchaseimpginvoices() {
		return totalpurchaseimpginvoices;
	}

	public void setTotalpurchaseimpginvoices(Long totalpurchaseimpginvoices) {
		this.totalpurchaseimpginvoices = totalpurchaseimpginvoices;
	}

	public Long getProcessedpurchaseimpginvoices() {
		return processedpurchaseimpginvoices;
	}

	public void setProcessedpurchaseimpginvoices(Long processedpurchaseimpginvoices) {
		this.processedpurchaseimpginvoices = processedpurchaseimpginvoices;
	}
	
	
}

