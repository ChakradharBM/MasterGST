package com.mastergst.usermanagement.runtime.support;

public class InvoicePendingActions {
	
	private String invNo;
	private String invType;
	private String clientId;
	private String fp;
	private String returnType;
	private boolean needDealerType;
	private boolean needFormatInvoice;
	
	public String getInvNo() {
		return invNo;
	}
	public void setInvNo(String invNo) {
		this.invNo = invNo;
	}
	public String getInvType() {
		return invType;
	}
	public void setInvType(String invType) {
		this.invType = invType;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public boolean isNeedDealerType() {
		return needDealerType;
	}
	public void setNeedDealerType(boolean needDealerType) {
		this.needDealerType = needDealerType;
	}
	public boolean isNeedFormatInvoice() {
		return needFormatInvoice;
	}
	public void setNeedFormatInvoice(boolean needFormatInvoice) {
		this.needFormatInvoice = needFormatInvoice;
	}
	public String getReturnType() {
		return returnType;
	}
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	

}
