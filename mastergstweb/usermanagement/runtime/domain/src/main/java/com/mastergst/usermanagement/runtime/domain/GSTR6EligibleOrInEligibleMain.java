/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

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
public class GSTR6EligibleOrInEligibleMain {
	
	@Id
	private ObjectId id;
	
	private List<GSTR6EligibleOrNonEligibleDetails> elglst=LazyList.decorate(new ArrayList<GSTR6EligibleOrNonEligibleDetails>(), 
			FactoryUtils.instantiateFactory(GSTR6EligibleOrNonEligibleDetails.class));
	
	private List<GSTR6EligibleOrNonEligibleDetails> inelglst=LazyList.decorate(new ArrayList<GSTR6EligibleOrNonEligibleDetails>(), 
			FactoryUtils.instantiateFactory(GSTR6EligibleOrNonEligibleDetails.class));

	
	public GSTR6EligibleOrInEligibleMain() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public List<GSTR6EligibleOrNonEligibleDetails> getElglst() {
		return elglst;
	}

	public void setElglst(List<GSTR6EligibleOrNonEligibleDetails> elglst) {
		this.elglst = elglst;
	}

	public List<GSTR6EligibleOrNonEligibleDetails> getInelglst() {
		return inelglst;
	}

	public void setInelglst(List<GSTR6EligibleOrNonEligibleDetails> inelglst) {
		this.inelglst = inelglst;
	}

}
