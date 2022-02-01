package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GSTR3BLiabilities {
	@Id
	private ObjectId id;
	private String gstin;
	@JsonProperty("ret_period")
	private String retPeriod;
	@JsonProperty("sup_details")
	private GSTR3BAutoLiabilitySupplyDetails supplyDetails = new GSTR3BAutoLiabilitySupplyDetails();
	@JsonProperty("inter_sup")
	private GSTR3BAutoLiabilitySupplyDetails interSupplyDetails = new GSTR3BAutoLiabilitySupplyDetails();
	private GSTR3BAutoLiabilitySupplyDetails elgitc = new GSTR3BAutoLiabilitySupplyDetails();
	
	public GSTR3BLiabilities() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getRetPeriod() {
		return retPeriod;
	}

	public void setRetPeriod(String retPeriod) {
		this.retPeriod = retPeriod;
	}

	public GSTR3BAutoLiabilitySupplyDetails getSupplyDetails() {
		return supplyDetails;
	}

	public void setSupplyDetails(GSTR3BAutoLiabilitySupplyDetails supplyDetails) {
		this.supplyDetails = supplyDetails;
	}

	public GSTR3BAutoLiabilitySupplyDetails getInterSupplyDetails() {
		return interSupplyDetails;
	}

	public void setInterSupplyDetails(GSTR3BAutoLiabilitySupplyDetails interSupplyDetails) {
		this.interSupplyDetails = interSupplyDetails;
	}

	public GSTR3BAutoLiabilitySupplyDetails getElgitc() {
		return elgitc;
	}

	public void setElgitc(GSTR3BAutoLiabilitySupplyDetails elgitc) {
		this.elgitc = elgitc;
	}
	
}
