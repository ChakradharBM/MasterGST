/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain.gstr6;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;



/**
 * GSTR6 Eligible Or InEligible Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6EligibleOrNonEligibleDetails {
	
	@Id
	private ObjectId id;
	
	private String typ;
	private String cpty;
	private String statecd;
	private List<GSTR6DocListDetails> doclst=LazyList.decorate(new ArrayList<GSTR6DocListDetails>(), 
			FactoryUtils.instantiateFactory(GSTR6DocListDetails.class)); 
	private String flag;
	
	public GSTR6EligibleOrNonEligibleDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getTyp() {
		return typ;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

	public String getCpty() {
		return cpty;
	}

	public void setCpty(String cpty) {
		this.cpty = cpty;
	}

	public String getStatecd() {
		return statecd;
	}

	public void setStatecd(String statecd) {
		this.statecd = statecd;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public List<GSTR6DocListDetails> getDoclst() {
		return doclst;
	}

	public void setDoclst(List<GSTR6DocListDetails> doclst) {
		this.doclst = doclst;
	}
	
}
