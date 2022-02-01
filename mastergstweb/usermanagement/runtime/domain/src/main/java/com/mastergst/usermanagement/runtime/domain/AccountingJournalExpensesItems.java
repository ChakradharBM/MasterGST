package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class AccountingJournalExpensesItems {
	
	@Id
	private ObjectId id;
	private String expensecategory;
	private Double totalAmt;
	
	public AccountingJournalExpensesItems() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getExpensecategory() {
		return expensecategory;
	}
	public void setExpensecategory(String expensecategory) {
		this.expensecategory = expensecategory;
	}
	public Double getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}
	
	
	

}
