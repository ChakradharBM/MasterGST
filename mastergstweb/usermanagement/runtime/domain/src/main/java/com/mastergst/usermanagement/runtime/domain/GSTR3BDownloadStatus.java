package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "GSTR3BDownloadStatus")
public class GSTR3BDownloadStatus extends Base {

	private String userid;
	private String clientid;
	private String returnperiod;
	private String status;
	private String financialyear;
	private String currrentmonth;
	private String invoicedata;
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getReturnperiod() {
		return returnperiod;
	}

	public void setReturnperiod(String returnperiod) {
		this.returnperiod = returnperiod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public GSTR3BDownloadStatus() {

	}

	public String getFinancialyear() {
		return financialyear;
	}

	public void setFinancialyear(String financialyear) {
		this.financialyear = financialyear;
	}

	public String getCurrrentmonth() {
		return currrentmonth;
	}

	public void setCurrrentmonth(String currrentmonth) {
		this.currrentmonth = currrentmonth;
	}
	public String getInvoicedata() {
		return invoicedata;
	}

	public void setInvoicedata(String invoicedata) {
		this.invoicedata = invoicedata;
	}
	@Override
	public String toString() {
		return "GSTR3BDownloadStatus [userid=" + userid + ", clientid=" + clientid + ", returnperiod=" + returnperiod
				+ ", status=" + status + ", financialyear=" + financialyear + ", currrentmonth=" + currrentmonth
				+ ", invoicedata=" + invoicedata + "]";
	}

	
}
