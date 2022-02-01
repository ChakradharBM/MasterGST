package com.mastergst.usermanagement.runtime.domain;

public class InvoiceMap {
	private String origin;
	private PurchaseRegister purchaseRegister;
	private GSTR2 gstr2;

	public PurchaseRegister getPurchaseRegister() {
		return purchaseRegister;
	}

	public void setPurchaseRegister(PurchaseRegister purchaseRegister) {
		this.purchaseRegister = purchaseRegister;
	}

	public GSTR2 getGstr2() {
		return gstr2;
	}

	public void setGstr2(GSTR2 gstr2) {
		this.gstr2 = gstr2;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}
}
