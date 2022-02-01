package com.mastergst.usermanagement.runtime.domain;

import com.mastergst.core.domain.Base;

public class EinvoiceCustomFields extends Base{
	private boolean displayInEinvoice;
	private boolean displayInPrint;
	private String nameInEinvoice;
	
	public boolean isDisplayInEinvoice() {
		return displayInEinvoice;
	}
	public void setDisplayInEinvoice(boolean displayInEinvoice) {
		this.displayInEinvoice = displayInEinvoice;
	}
	public String getNameInEinvoice() {
		return nameInEinvoice;
	}
	public void setNameInEinvoice(String nameInEinvoice) {
		this.nameInEinvoice = nameInEinvoice;
	}
	public boolean isDisplayInPrint() {
		return displayInPrint;
	}
	public void setDisplayInPrint(boolean displayInPrint) {
		this.displayInPrint = displayInPrint;
	}
}