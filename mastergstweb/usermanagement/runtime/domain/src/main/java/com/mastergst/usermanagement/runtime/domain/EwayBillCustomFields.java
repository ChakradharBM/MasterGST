package com.mastergst.usermanagement.runtime.domain;

import com.mastergst.core.domain.Base;

public class EwayBillCustomFields extends Base{
	private boolean displayInEwayBill;
	private boolean displayInPrint;
	private String nameInEwayBill;
	public boolean isDisplayInEwayBill() {
		return displayInEwayBill;
	}
	public void setDisplayInEwayBill(boolean displayInEwayBill) {
		this.displayInEwayBill = displayInEwayBill;
	}
	public boolean isDisplayInPrint() {
		return displayInPrint;
	}
	public void setDisplayInPrint(boolean displayInPrint) {
		this.displayInPrint = displayInPrint;
	}
	public String getNameInEwayBill() {
		return nameInEwayBill;
	}
	public void setNameInEwayBill(String nameInEwayBill) {
		this.nameInEwayBill = nameInEwayBill;
	}
}
