package com.mastergst.usermanagement.runtime.domain;

import com.mastergst.core.domain.Base;

public class InvoiceCutomFields extends Base{
	private boolean displayInSales;
	private boolean displayInPurchases;
	private boolean displayInPrint;
	private String nameInSales;
	private String nameInPurchase;
	public boolean isDisplayInSales() {
		return displayInSales;
	}
	public void setDisplayInSales(boolean displayInSales) {
		this.displayInSales = displayInSales;
	}
	public boolean isDisplayInPurchases() {
		return displayInPurchases;
	}
	public void setDisplayInPurchases(boolean displayInPurchases) {
		this.displayInPurchases = displayInPurchases;
	}
	public boolean isDisplayInPrint() {
		return displayInPrint;
	}
	public void setDisplayInPrint(boolean displayInPrint) {
		this.displayInPrint = displayInPrint;
	}
	public String getNameInSales() {
		return nameInSales;
	}
	public void setNameInSales(String nameInSales) {
		this.nameInSales = nameInSales;
	}
	public String getNameInPurchase() {
		return nameInPurchase;
	}
	public void setNameInPurchase(String nameInPurchase) {
		this.nameInPurchase = nameInPurchase;
	}
	
	

}
