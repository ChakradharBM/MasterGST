package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR9Table9AllDetails {
	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpyble;
	@JsonProperty("txpaid_cash")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpaidCash;
	@JsonProperty("tax_paid_itc_iamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcIamt;
	@JsonProperty("tax_paid_itc_camt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcCamt;
	@JsonProperty("tax_paid_itc_samt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcSamt;
	@JsonProperty("tax_paid_itc_csamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcCsamt;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public Double getTxpaidCash() {
		return txpaidCash;
	}
	public void setTxpaidCash(Double txpaidCash) {
		this.txpaidCash = txpaidCash;
	}
	public Double getTaxPaidItcIamt() {
		return taxPaidItcIamt;
	}
	public void setTaxPaidItcIamt(Double taxPaidItcIamt) {
		this.taxPaidItcIamt = taxPaidItcIamt;
	}
	public Double getTaxPaidItcCamt() {
		return taxPaidItcCamt;
	}
	public void setTaxPaidItcCamt(Double taxPaidItcCamt) {
		this.taxPaidItcCamt = taxPaidItcCamt;
	}
	public Double getTaxPaidItcSamt() {
		return taxPaidItcSamt;
	}
	public void setTaxPaidItcSamt(Double taxPaidItcSamt) {
		this.taxPaidItcSamt = taxPaidItcSamt;
	}
	public Double getTaxPaidItcCsamt() {
		return taxPaidItcCsamt;
	}
	public void setTaxPaidItcCsamt(Double taxPaidItcCsamt) {
		this.taxPaidItcCsamt = taxPaidItcCsamt;
	}
	public Double getTxpyble() {
		return txpyble;
	}
	public void setTxpyble(Double txpyble) {
		this.txpyble = txpyble;
	}
	
}
