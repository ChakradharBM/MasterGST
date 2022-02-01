package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.google.common.collect.Lists;

public class ExpenseDetails {
	@Id
	private ObjectId id;
	private String category;
	private Double totalAmt;
	private List<ExpenseItem> expensesList = Lists.newArrayList();
	
	public ExpenseDetails() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public Double getTotalAmt() {
		return totalAmt;
	}
	public void setTotalAmt(Double totalAmt) {
		this.totalAmt = totalAmt;
	}
	public List<ExpenseItem> getExpensesList() {
		return expensesList;
	}
	public void setExpensesList(List<ExpenseItem> expensesList) {
		this.expensesList = expensesList;
	}
}
