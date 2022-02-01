package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class AccountingJournalVoucherItems {
	@Id
	private ObjectId id;
	private String modeOfVoucher;
	private String ledger;
	private Double dramount;
	private Double cramount;
	
	public AccountingJournalVoucherItems() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getModeOfVoucher() {
		return modeOfVoucher;
	}
	public void setModeOfVoucher(String modeOfVoucher) {
		this.modeOfVoucher = modeOfVoucher;
	}
	public String getLedger() {
		return ledger;
	}
	public void setLedger(String ledger) {
		this.ledger = ledger;
	}
	public Double getDramount() {
		return dramount;
	}
	public void setDramount(Double dramount) {
		this.dramount = dramount;
	}
	public Double getCramount() {
		return cramount;
	}
	public void setCramount(Double cramount) {
		this.cramount = cramount;
	}
	
	

}
