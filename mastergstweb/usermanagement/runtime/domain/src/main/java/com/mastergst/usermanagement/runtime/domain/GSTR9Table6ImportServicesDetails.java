package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR9 Table6 Import of Services information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table6ImportServicesDetails {

	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double iamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double csamt;
	
	public GSTR9Table6ImportServicesDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Double getIamt() {
		return iamt;
	}

	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}
	
	
	
}
