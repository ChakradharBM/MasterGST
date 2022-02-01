package com.mastergst.usermanagement.runtime.domain;

import java.math.BigDecimal;

public class TotalStocksAmounts {
	private String _id;
	private int totalTransactions;
	private BigDecimal stockItemQty = new BigDecimal(0.0);

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

	public BigDecimal getStockItemQty() {
		return stockItemQty;
	}

	public void setStockItemQty(BigDecimal stockItemQty) {
		this.stockItemQty = stockItemQty;
	}
}
