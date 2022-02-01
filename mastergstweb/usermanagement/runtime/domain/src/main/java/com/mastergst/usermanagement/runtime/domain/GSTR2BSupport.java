/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR2 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gst2b_support")
public class GSTR2BSupport extends InvoiceParent {
	
	List<GSTRB2B> b2ba = LazyList.decorate(new ArrayList<GSTRB2B>(), FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTR2AIMPG> impgsez = LazyList.decorate(new ArrayList<GSTR2AIMPG>(),
			FactoryUtils.instantiateFactory(GSTR2AIMPG.class));
	List<GSTR2AIMPG> impg = LazyList.decorate(new ArrayList<GSTR2AIMPG>(),
			FactoryUtils.instantiateFactory(GSTR2AIMPG.class));
	List<GSTRISD> isd = LazyList.decorate(new ArrayList<GSTRISD>(), FactoryUtils.instantiateFactory(GSTRISD.class));
	List<GSTRISD> isda = LazyList.decorate(new ArrayList<GSTRISD>(), FactoryUtils.instantiateFactory(GSTRISD.class));
	List<GSTRCreditDebitNotes> cdna = LazyList.decorate(new ArrayList<GSTRCreditDebitNotes>(),
			FactoryUtils.instantiateFactory(GSTRCreditDebitNotes.class));

	@JsonProperty("imp_g")
	List<GSTRImportDetails> impGoods = LazyList.decorate(new ArrayList<GSTRImportDetails>(),
			FactoryUtils.instantiateFactory(GSTRImportDetails.class));
	@JsonProperty("imp_s")
	List<GSTRImportDetails> impServices = LazyList.decorate(new ArrayList<GSTRImportDetails>(),
			FactoryUtils.instantiateFactory(GSTRImportDetails.class));

	public List<GSTRISD> getIsd() {
		return isd;
	}

	public void setIsd(List<GSTRISD> isd) {
		this.isd = isd;
	}

	public List<GSTRB2B> getB2ba() {
		return b2ba;
	}

	public void setB2ba(List<GSTRB2B> b2ba) {
		this.b2ba = b2ba;
	}

	public List<GSTRCreditDebitNotes> getCdna() {
		return cdna;
	}

	public void setCdna(List<GSTRCreditDebitNotes> cdna) {
		this.cdna = cdna;
	}

	public List<GSTRISD> getIsda() {
		return isda;
	}

	public void setIsda(List<GSTRISD> isda) {
		this.isda = isda;
	}

	public List<GSTR2AIMPG> getImpgsez() {
		return impgsez;
	}

	public void setImpgsez(List<GSTR2AIMPG> impgsez) {
		this.impgsez = impgsez;
	}

	public List<GSTR2AIMPG> getImpg() {
		return impg;
	}

	public void setImpg(List<GSTR2AIMPG> impg) {
		this.impg = impg;
	}

	public List<GSTRImportDetails> getImpGoods() {
		return impGoods;
	}

	public void setImpGoods(List<GSTRImportDetails> impGoods) {
		this.impGoods = impGoods;
	}

	public List<GSTRImportDetails> getImpServices() {
		return impServices;
	}

	public void setImpServices(List<GSTRImportDetails> impServices) {
		this.impServices = impServices;
	}
}
