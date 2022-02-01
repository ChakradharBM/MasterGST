package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "custom_fields")
public class CustomFields  extends Base{
	private String userid;
	private String clientid;
	private List<CustomData> sales;
	private List<CustomData> purchase;
	private List<CustomData> ewaybill;
	private List<CustomData> einvoice;
	private List<CustomData> items;

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

	public List<CustomData> getSales() {
		return sales;
	}

	public void setSales(List<CustomData> sales) {
		this.sales = sales;
	}

	public List<CustomData> getPurchase() {
		return purchase;
	}

	public void setPurchase(List<CustomData> purchase) {
		this.purchase = purchase;
	}

	public List<CustomData> getEwaybill() {
		return ewaybill;
	}

	public void setEwaybill(List<CustomData> ewaybill) {
		this.ewaybill = ewaybill;
	}

	public List<CustomData> getEinvoice() {
		return einvoice;
	}

	public void setEinvoice(List<CustomData> einvoice) {
		this.einvoice = einvoice;
	}

	public List<CustomData> getItems() {
		return items;
	}

	public void setItems(List<CustomData> items) {
		this.items = items;
	}

}
