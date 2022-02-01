package com.mastergst.usermanagement.runtime.domain;

public class ReconsileGstr2bMap {
	private String origin;
	private PurchaseRegister purchaseRegister;
	private GSTR2BSupport gstr2b;

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public PurchaseRegister getPurchaseRegister() {
		return purchaseRegister;
	}

	public void setPurchaseRegister(PurchaseRegister purchaseRegister) {
		this.purchaseRegister = purchaseRegister;
	}

	public GSTR2BSupport getGstr2b() {
		return gstr2b;
	}

	public void setGstr2b(GSTR2BSupport gstr2b) {
		this.gstr2b = gstr2b;
	}
}
