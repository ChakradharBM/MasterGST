package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table16 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table16GetDetails {

	@Id
	private ObjectId id;
	String flag;
	String chksum;
	@JsonProperty("comp_supp")
	GSTR9Table16CompositionSuppliesDetails compSupp = new GSTR9Table16CompositionSuppliesDetails();
	@JsonProperty("deemed_supp")
	GSTR9Table16DeemedAndNotReturnedDetails deemedSupp = new GSTR9Table16DeemedAndNotReturnedDetails();
	@JsonProperty("not_returned")
	GSTR9Table16DeemedAndNotReturnedDetails notReturned = new GSTR9Table16DeemedAndNotReturnedDetails();
	
	public GSTR9Table16GetDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public GSTR9Table16CompositionSuppliesDetails getCompSupp() {
		return compSupp;
	}

	public void setCompSupp(GSTR9Table16CompositionSuppliesDetails compSupp) {
		this.compSupp = compSupp;
	}

	public GSTR9Table16DeemedAndNotReturnedDetails getDeemedSupp() {
		return deemedSupp;
	}

	public void setDeemedSupp(GSTR9Table16DeemedAndNotReturnedDetails deemedSupp) {
		this.deemedSupp = deemedSupp;
	}

	public GSTR9Table16DeemedAndNotReturnedDetails getNotReturned() {
		return notReturned;
	}

	public void setNotReturned(GSTR9Table16DeemedAndNotReturnedDetails notReturned) {
		this.notReturned = notReturned;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	
}
