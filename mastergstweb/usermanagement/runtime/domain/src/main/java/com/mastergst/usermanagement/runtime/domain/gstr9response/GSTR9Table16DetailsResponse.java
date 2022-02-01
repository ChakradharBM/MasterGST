package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
public class GSTR9Table16DetailsResponse {

	@Id
	private ObjectId id;
	String flag;
	@JsonProperty("comp_supp")
	GSTR9Table16CompositionSuppliesDetailsResponse compSupp = new GSTR9Table16CompositionSuppliesDetailsResponse();
	@JsonProperty("deemed_supp")
	GSTR9Table16DeemedAndNotReturnedDetailsResponse deemedSupp = new GSTR9Table16DeemedAndNotReturnedDetailsResponse();
	@JsonProperty("not_returned")
	GSTR9Table16DeemedAndNotReturnedDetailsResponse notReturned = new GSTR9Table16DeemedAndNotReturnedDetailsResponse();
	
	public GSTR9Table16DetailsResponse() {
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

	public GSTR9Table16CompositionSuppliesDetailsResponse getCompSupp() {
		return compSupp;
	}

	public void setCompSupp(GSTR9Table16CompositionSuppliesDetailsResponse compSupp) {
		this.compSupp = compSupp;
	}

	public GSTR9Table16DeemedAndNotReturnedDetailsResponse getDeemedSupp() {
		return deemedSupp;
	}

	public void setDeemedSupp(GSTR9Table16DeemedAndNotReturnedDetailsResponse deemedSupp) {
		this.deemedSupp = deemedSupp;
	}

	public GSTR9Table16DeemedAndNotReturnedDetailsResponse getNotReturned() {
		return notReturned;
	}

	public void setNotReturned(GSTR9Table16DeemedAndNotReturnedDetailsResponse notReturned) {
		this.notReturned = notReturned;
	}
}
