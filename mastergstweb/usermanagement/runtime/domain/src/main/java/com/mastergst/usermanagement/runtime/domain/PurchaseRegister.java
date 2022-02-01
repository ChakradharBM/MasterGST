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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Purchase Register Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"errorMsg", "bankDetails","strDate","ledgerName"})
@Document(collection = "purchaseregister")
public class PurchaseRegister extends InvoiceParent {
	List<GSTRB2B> b2bur=LazyList.decorate(new ArrayList<GSTRB2B>(), 
			FactoryUtils.instantiateFactory(GSTRB2B.class));
	List<GSTRAdvanceTax> txi=LazyList.decorate(new ArrayList<GSTRAdvanceTax>(), 
			FactoryUtils.instantiateFactory(GSTRAdvanceTax.class));
	@JsonProperty("imp_g")
	List<GSTRImportDetails> impGoods=LazyList.decorate(new ArrayList<GSTRImportDetails>(), 
			FactoryUtils.instantiateFactory(GSTRImportDetails.class));
	@JsonProperty("imp_s")
	List<GSTRImportDetails> impServices=LazyList.decorate(new ArrayList<GSTRImportDetails>(), 
			FactoryUtils.instantiateFactory(GSTRImportDetails.class));
	@JsonProperty("nil_supplies")
	private GSTRNilSupplies nilSupplies;
	@JsonProperty("itc_rvsl")
	private GSTRITCReversals itcRvsl;
	List<GSTRISD> isd=LazyList.decorate(new ArrayList<GSTRISD>(), 
			FactoryUtils.instantiateFactory(GSTRISD.class));
	
	public List<GSTRB2B> getB2bur() {
		return b2bur;
	}
	public void setB2bur(List<GSTRB2B> b2bur) {
		this.b2bur = b2bur;
	}
	public List<GSTRAdvanceTax> getTxi() {
		return txi;
	}
	public void setTxi(List<GSTRAdvanceTax> txi) {
		this.txi = txi;
	}
	public List<GSTRCreditDebitNotes> getCdn() {
		return cdn;
	}
	public void setCdn(List<GSTRCreditDebitNotes> cdn) {
		this.cdn = cdn;
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
	public GSTRNilSupplies getNilSupplies() {
		return nilSupplies;
	}
	public void setNilSupplies(GSTRNilSupplies nilSupplies) {
		this.nilSupplies = nilSupplies;
	}
	public GSTRITCReversals getItcRvsl() {
		return itcRvsl;
	}
	public void setItcRvsl(GSTRITCReversals itcRvsl) {
		this.itcRvsl = itcRvsl;
	}
	public List<GSTRISD> getIsd() {
		return isd;
	}
	public void setIsd(List<GSTRISD> isd) {
		this.isd = isd;
	}
	
}
