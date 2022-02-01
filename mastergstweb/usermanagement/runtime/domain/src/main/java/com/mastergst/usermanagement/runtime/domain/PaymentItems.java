package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class PaymentItems{
	 
	@Id
	private ObjectId id;
	private String modeOfPayment;
	private Double amount;
	private Double pendingBalance;
	private String referenceNumber;
	private String ledger;
	
	public PaymentItems() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getModeOfPayment() {
		return modeOfPayment;
	}
	public void setModeOfPayment(String modeOfPayment) {
		this.modeOfPayment = modeOfPayment;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getReferenceNumber() {
		return referenceNumber;
	}
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public Double getPendingBalance() {
		return pendingBalance;
	}
	public void setPendingBalance(Double pendingBalance) {
		this.pendingBalance = pendingBalance;
	}
	
	
}
