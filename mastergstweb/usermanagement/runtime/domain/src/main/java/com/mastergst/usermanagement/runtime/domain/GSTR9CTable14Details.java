package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CTable14Details {
	@Id
	private ObjectId id;
	
	private List<GSTR9CTable14ItemDeatils> items=LazyList.decorate(new ArrayList<GSTR9CTable14ItemDeatils>(), 
			FactoryUtils.instantiateFactory(GSTR9CTable14ItemDeatils.class));
	@JsonProperty("tot_elig_itc")
	private GSTR9CTable14OtherDetails totEligItc = new GSTR9CTable14OtherDetails();
	@JsonProperty("itc_claim")
	private GSTR9CTable14OtherDetails itcClaim = new GSTR9CTable14OtherDetails();
	@JsonProperty("unrec_itc")
	private GSTR9CTable14OtherDetails unrecItc = new GSTR9CTable14OtherDetails();
	
	public GSTR9CTable14Details() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<GSTR9CTable14ItemDeatils> getItems() {
		return items;
	}
	public void setItems(List<GSTR9CTable14ItemDeatils> items) {
		this.items = items;
	}
	public GSTR9CTable14OtherDetails getTotEligItc() {
		return totEligItc;
	}
	public void setTotEligItc(GSTR9CTable14OtherDetails totEligItc) {
		this.totEligItc = totEligItc;
	}
	public GSTR9CTable14OtherDetails getItcClaim() {
		return itcClaim;
	}
	public void setItcClaim(GSTR9CTable14OtherDetails itcClaim) {
		this.itcClaim = itcClaim;
	}
	public GSTR9CTable14OtherDetails getUnrecItc() {
		return unrecItc;
	}
	public void setUnrecItc(GSTR9CTable14OtherDetails unrecItc) {
		this.unrecItc = unrecItc;
	}
	
	

}
