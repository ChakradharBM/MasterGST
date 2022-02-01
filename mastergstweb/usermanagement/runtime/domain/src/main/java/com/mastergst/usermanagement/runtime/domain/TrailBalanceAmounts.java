package com.mastergst.usermanagement.runtime.domain;

public class TrailBalanceAmounts {

	private double openingamt;
	private double debitamt;
	private double creditamt;
	private double closingamt;

	public TrailBalanceAmounts() {
		super();
	}

	public TrailBalanceAmounts(double openingamt, double debitamt, double creditamt, double closingamt) {
		super();
		this.openingamt = openingamt;
		this.debitamt = debitamt;
		this.creditamt = creditamt;
		this.closingamt = closingamt;
	}

	public double getOpeningamt() {
		return openingamt;
	}

	public void setOpeningamt(double openingamt) {
		this.openingamt = openingamt;
	}

	public double getDebitamt() {
		return debitamt;
	}

	public void setDebitamt(double debitamt) {
		this.debitamt = debitamt;
	}

	public double getCreditamt() {
		return creditamt;
	}

	public void setCreditamt(double creditamt) {
		this.creditamt = creditamt;
	}

	public double getClosingamt() {
		return closingamt;
	}

	public void setClosingamt(double closingamt) {
		this.closingamt = closingamt;
	}

	@Override
	public String toString() {
		return "TrailBalanceAmounts [openingamt=" + openingamt + ", debitamt=" + debitamt + ", creditamt=" + creditamt
				+ ", closingamt=" + closingamt + "]";
	}
}
