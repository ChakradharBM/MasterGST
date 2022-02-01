package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR9 Table9 Summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table9SummaryDetails {
	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpyble;
	
	
	public GSTR9Table9SummaryDetails() {
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

	
	
}
