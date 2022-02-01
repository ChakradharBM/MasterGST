package com.mastergst.usermanagement.runtime.domain;

public class Amounts {

	private double openingamt;
	private double debitamt;
	private double creditamt;
	private double closingamt;

	private double previousyearclosingamt;

	public Amounts() {
		super();
	}

	public Amounts(double openingamt, double debitamt, double creditamt, double closingamt) {
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

	public double getPreviousyearclosingamt() {
		return previousyearclosingamt;
	}

	public void setPreviousyearclosingamt(double previousyearclosingamt) {
		this.previousyearclosingamt = previousyearclosingamt;
	}

	@Override
	public String toString() {
		return "Amounts [openingamt=" + openingamt + ", debitamt=" + debitamt + ", creditamt=" + creditamt
				+ ", closingamt=" + closingamt + ", previousyearclosingamt=" + previousyearclosingamt + "]";
	}

}
