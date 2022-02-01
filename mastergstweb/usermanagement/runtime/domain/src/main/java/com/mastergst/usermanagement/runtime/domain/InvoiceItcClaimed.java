package com.mastergst.usermanagement.runtime.domain;

public class InvoiceItcClaimed {

	private String docId;
	private String elg;
	private Double elgpercent;
	private String dateofitcClaimed;

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getElg() {
		return elg;
	}

	public void setElg(String elg) {
		this.elg = elg;
	}

	public Double getElgpercent() {
		return elgpercent;
	}

	public void setElgpercent(Double elgpercent) {
		this.elgpercent = elgpercent;
	}

	public String getDateofitcClaimed() {
		return dateofitcClaimed;
	}

	public void setDateofitcClaimed(String dateofitcClaimed) {
		this.dateofitcClaimed = dateofitcClaimed;
	}
}
