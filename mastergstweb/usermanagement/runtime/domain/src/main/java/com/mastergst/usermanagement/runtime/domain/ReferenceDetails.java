/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * ReferenceDetails Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReferenceDetails {
	@JsonProperty("InvRm")
	private String invRm;
	@JsonProperty("DocPerdDtls")
	DocPerDetails docPerdDtls = new DocPerDetails();
	@JsonProperty("PrecDocDtls")
	List<PreceedingDocDetails> precDocDtls = LazyList.decorate(new ArrayList<PreceedingDocDetails>(), 
			FactoryUtils.instantiateFactory(PreceedingDocDetails.class));
	@JsonProperty("ContrDtls")
	ContraDetails contrDtls = new ContraDetails();
	public String getInvRm() {
		return invRm;
	}
	public void setInvRm(String invRm) {
		this.invRm = invRm;
	}
	public DocPerDetails getDocPerdDtls() {
		return docPerdDtls;
	}
	public void setDocPerdDtls(DocPerDetails docPerdDtls) {
		this.docPerdDtls = docPerdDtls;
	}
	public List<PreceedingDocDetails> getPrecDocDtls() {
		return precDocDtls;
	}
	public void setPrecDocDtls(List<PreceedingDocDetails> precDocDtls) {
		this.precDocDtls = precDocDtls;
	}
	public ContraDetails getContrDtls() {
		return contrDtls;
	}
	public void setContrDtls(ContraDetails contrDtls) {
		this.contrDtls = contrDtls;
	}
	

}
