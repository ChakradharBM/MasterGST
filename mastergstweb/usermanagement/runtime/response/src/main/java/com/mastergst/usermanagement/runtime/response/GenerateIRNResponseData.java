package com.mastergst.usermanagement.runtime.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenerateIRNResponseData {

	@JsonProperty("AckNo")
	private Number ackNo;
	@JsonProperty("AckDt")
	private String ackDt;
	@JsonProperty("Irn")
	private String irn;
	@JsonProperty("SignedInvoice")
	private String signedInvoice;
	@JsonProperty("SignedQRCode")
	private String signedQRCode;
	@JsonProperty("Status")
	private String status;
	public Number getAckNo() {
		return ackNo;
	}
	public void setAckNo(Number ackNo) {
		this.ackNo = ackNo;
	}
	public String getAckDt() {
		return ackDt;
	}
	public void setAckDt(String ackDt) {
		this.ackDt = ackDt;
	}
	public String getIrn() {
		return irn;
	}
	public void setIrn(String irn) {
		this.irn = irn;
	}
	public String getSignedInvoice() {
		return signedInvoice;
	}
	public void setSignedInvoice(String signedInvoice) {
		this.signedInvoice = signedInvoice;
	}
	public String getSignedQRCode() {
		return signedQRCode;
	}
	public void setSignedQRCode(String signedQRCode) {
		this.signedQRCode = signedQRCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	@Override
	public String toString() {
		return "GenerateIRNResponseData [ackNo=" + ackNo + ", ackDt=" + ackDt + ", irn=" + irn + ", signedInvoice="
				+ signedInvoice + ", signedQRCode=" + signedQRCode + ", status=" + status + ", getAckNo()=" + getAckNo()
				+ ", getAckDt()=" + getAckDt() + ", getIrn()=" + getIrn() + ", getSignedInvoice()=" + getSignedInvoice()
				+ ", getSignedQRCode()=" + getSignedQRCode() + ", getStatus()=" + getStatus() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	
}
