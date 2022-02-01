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
public class GSTR9CTable7Details {

	@Id
	private ObjectId id;
	
	@JsonProperty("annul_turn_adj")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double annulTurnAdj;
	
	@JsonProperty("othr_turnovr")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double othrTurnovr;
	
	@JsonProperty("zero_sup")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double zeroSup;
	
	@JsonProperty("rev_sup")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double revSup;
	
	@JsonProperty("tax_turn_adj")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxTurnAdj;
	
	@JsonProperty("tax_turn_annul")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double taxTurnAnnul;
	
	@JsonProperty("unrec_tax_turn")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unrecTaxTurn;

	public GSTR9CTable7Details() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getAnnulTurnAdj() {
		return annulTurnAdj;
	}

	public void setAnnulTurnAdj(Double annulTurnAdj) {
		this.annulTurnAdj = annulTurnAdj;
	}

	public Double getOthrTurnovr() {
		return othrTurnovr;
	}

	public void setOthrTurnovr(Double othrTurnovr) {
		this.othrTurnovr = othrTurnovr;
	}

	public Double getZeroSup() {
		return zeroSup;
	}

	public void setZeroSup(Double zeroSup) {
		this.zeroSup = zeroSup;
	}

	public Double getRevSup() {
		return revSup;
	}

	public void setRevSup(Double revSup) {
		this.revSup = revSup;
	}

	public Double getTaxTurnAdj() {
		return taxTurnAdj;
	}

	public void setTaxTurnAdj(Double taxTurnAdj) {
		this.taxTurnAdj = taxTurnAdj;
	}

	public Double getTaxTurnAnnul() {
		return taxTurnAnnul;
	}

	public void setTaxTurnAnnul(Double taxTurnAnnul) {
		this.taxTurnAnnul = taxTurnAnnul;
	}

	public Double getUnrecTaxTurn() {
		return unrecTaxTurn;
	}

	public void setUnrecTaxTurn(Double unrecTaxTurn) {
		this.unrecTaxTurn = unrecTaxTurn;
	}
	
	
}
