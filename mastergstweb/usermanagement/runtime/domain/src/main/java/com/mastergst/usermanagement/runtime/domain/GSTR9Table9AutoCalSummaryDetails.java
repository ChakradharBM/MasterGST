package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table9AutoCalSummaryDetails {
	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpyble;
	
	@JsonProperty("txpaid_cash")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpaidCash;
	
	@JsonProperty("tax_paid_itc_csamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcCsamt;
	
	@JsonProperty("tax_paid_itc_iamt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcIamt;
	
	@JsonProperty("tax_paid_itc_samt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcSamt;
	
	@JsonProperty("tax_paid_itc_camt")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxPaidItcCamt;
	
	public GSTR9Table9AutoCalSummaryDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getTxpyble() {
		return txpyble;
	}

	public void setTxpyble(Double txpyble) {
		this.txpyble = txpyble;
	}

	public Double getTxpaidCash() {
		return txpaidCash;
	}

	public void setTxpaidCash(Double txpaidCash) {
		this.txpaidCash = txpaidCash;
	}

	public Double getTaxPaidItcCsamt() {
		return taxPaidItcCsamt;
	}

	public void setTaxPaidItcCsamt(Double taxPaidItcCsamt) {
		this.taxPaidItcCsamt = taxPaidItcCsamt;
	}

	public Double getTaxPaidItcIamt() {
		return taxPaidItcIamt;
	}

	public void setTaxPaidItcIamt(Double taxPaidItcIamt) {
		this.taxPaidItcIamt = taxPaidItcIamt;
	}

	public Double getTaxPaidItcSamt() {
		return taxPaidItcSamt;
	}

	public void setTaxPaidItcSamt(Double taxPaidItcSamt) {
		this.taxPaidItcSamt = taxPaidItcSamt;
	}

	public Double getTaxPaidItcCamt() {
		return taxPaidItcCamt;
	}

	public void setTaxPaidItcCamt(Double taxPaidItcCamt) {
		this.taxPaidItcCamt = taxPaidItcCamt;
	}
}
