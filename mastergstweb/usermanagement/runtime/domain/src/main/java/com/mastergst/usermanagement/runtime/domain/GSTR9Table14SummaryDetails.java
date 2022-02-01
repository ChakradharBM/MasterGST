package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR9 Table14 Summary information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table14SummaryDetails {
	@Id
	private ObjectId id;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpyble;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	Double txpaid;
	
	public GSTR9Table14SummaryDetails() {
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

	public Double getTxpaid() {
		return txpaid;
	}

	public void setTxpaid(Double txpaid) {
		this.txpaid = txpaid;
	}
	
	
}
