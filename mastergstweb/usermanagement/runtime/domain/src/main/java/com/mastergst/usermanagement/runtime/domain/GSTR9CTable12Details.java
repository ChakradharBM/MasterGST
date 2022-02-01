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
public class GSTR9CTable12Details {
	@Id
	private ObjectId id;
	
	@JsonProperty("itc_avail")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcAvail;
	
	@JsonProperty("itc_book_earl")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcBookEarl;
	
	@JsonProperty("itc_book_curr")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcBookCurr;
	
	@JsonProperty("itc_avail_audited")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcAvailAudited;
	
	@JsonProperty("itc_claim")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double itcClaim;
	
	@JsonProperty("unrec_itc")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double unrecItc;
	
	public GSTR9CTable12Details() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getItcAvail() {
		return itcAvail;
	}

	public void setItcAvail(Double itcAvail) {
		this.itcAvail = itcAvail;
	}

	public Double getItcBookEarl() {
		return itcBookEarl;
	}

	public void setItcBookEarl(Double itcBookEarl) {
		this.itcBookEarl = itcBookEarl;
	}

	public Double getItcBookCurr() {
		return itcBookCurr;
	}

	public void setItcBookCurr(Double itcBookCurr) {
		this.itcBookCurr = itcBookCurr;
	}

	public Double getItcAvailAudited() {
		return itcAvailAudited;
	}

	public void setItcAvailAudited(Double itcAvailAudited) {
		this.itcAvailAudited = itcAvailAudited;
	}

	public Double getItcClaim() {
		return itcClaim;
	}

	public void setItcClaim(Double itcClaim) {
		this.itcClaim = itcClaim;
	}

	public Double getUnrecItc() {
		return unrecItc;
	}

	public void setUnrecItc(Double unrecItc) {
		this.unrecItc = unrecItc;
	}
	
	
}
