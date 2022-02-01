/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR1 Invoice information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
//@Document(collection = "einvoice")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({ "id", "sftr", "tdstcsenable","tdsenable","ebillValidator","bankDetails","advTaxableType","docuploads", "nil", "amendment","csftr","dispatchpin","shipmentpin","samebilladdress","pendingAmount","receivedAmount","convertedtoinv" })
public class EINVOICE extends InvoiceParent {
	@JsonProperty("DocDtls")
	DocDetails edocDtls = new DocDetails();
	@JsonProperty("TranDtls")
	TransportDetails etranDtls = new TransportDetails();
	@JsonProperty("SellerDtls")
	SellerDetails esellerDtls = new SellerDetails();
	@JsonProperty("BuyerDtls")
	BuyerDetails ebuyerDtls = new BuyerDetails();
	
	 @JsonProperty("DispatcherDtls") 
	 DispatcherDetails edispatcherDtls;//= new DispatcherDetails();
	
	  @JsonProperty("ShipmentDtls")
	  ShipmentDetails eshipmentDtls;// = new  ShipmentDetails();
	
	@JsonProperty("ValDtls")
	ValueDetails evalDtls = new ValueDetails();
	//@JsonProperty("BchDtls")
	//BatchDetails ebchDtls;// = new BatchDetails();
	@JsonProperty("PayDtls")
	PayeeDetails epayDtls;// = new PayeeDetails();
	@JsonProperty("RefDtls")
	ReferenceDetails reefDtls;// = new ReferenceDetails();
	@JsonProperty("AddlDocDtls")
	List<AddlDocDetails> eaddlDocDtls;// = new AddlDocDetails();
	@JsonProperty("ExpDtls")
	ExportDetails eexpDtls ;//= new ExportDetails();
	@JsonProperty("EwbDtls")
	EwayBillDetails eewbDtls;// = new EwayBillDetails();
	@JsonProperty("ItemList")
	List<EinvoiceItem> eitemList = LazyList.decorate(new ArrayList<EinvoiceItem>(),
			FactoryUtils.instantiateFactory(EinvoiceItem.class));
	public DocDetails getEdocDtls() {
		return edocDtls;
	}
	public void setEdocDtls(DocDetails edocDtls) {
		this.edocDtls = edocDtls;
	}
	public TransportDetails getEtranDtls() {
		return etranDtls;
	}
	public void setEtranDtls(TransportDetails etranDtls) {
		this.etranDtls = etranDtls;
	}
	public SellerDetails getEsellerDtls() {
		return esellerDtls;
	}
	public void setEsellerDtls(SellerDetails esellerDtls) {
		this.esellerDtls = esellerDtls;
	}
	public BuyerDetails getEbuyerDtls() {
		return ebuyerDtls;
	}
	public void setEbuyerDtls(BuyerDetails ebuyerDtls) {
		this.ebuyerDtls = ebuyerDtls;
	}
	public ValueDetails getEvalDtls() {
		return evalDtls;
	}
	public void setEvalDtls(ValueDetails evalDtls) {
		this.evalDtls = evalDtls;
	}
	
	public PayeeDetails getEpayDtls() {
		return epayDtls;
	}
	public void setEpayDtls(PayeeDetails epayDtls) {
		this.epayDtls = epayDtls;
	}
	public ReferenceDetails getReefDtls() {
		return reefDtls;
	}
	public void setReefDtls(ReferenceDetails reefDtls) {
		this.reefDtls = reefDtls;
	}
	public List<AddlDocDetails> getEaddlDocDtls() {
		return eaddlDocDtls;
	}
	public void setEaddlDocDtls(List<AddlDocDetails> eaddlDocDtls) {
		this.eaddlDocDtls = eaddlDocDtls;
	}
	public ExportDetails getEexpDtls() {
		return eexpDtls;
	}
	public void setEexpDtls(ExportDetails eexpDtls) {
		this.eexpDtls = eexpDtls;
	}
	public EwayBillDetails getEewbDtls() {
		return eewbDtls;
	}
	public void setEewbDtls(EwayBillDetails eewbDtls) {
		this.eewbDtls = eewbDtls;
	}
	public List<EinvoiceItem> getEitemList() {
		return eitemList;
	}
	public void setEitemList(List<EinvoiceItem> eitemList) {
		this.eitemList = eitemList;
	}
	public DispatcherDetails getEdispatcherDtls() {
		return edispatcherDtls;
	}
	public void setEdispatcherDtls(DispatcherDetails edispatcherDtls) {
		this.edispatcherDtls = edispatcherDtls;
	}
	public ShipmentDetails getEshipmentDtls() {
		return eshipmentDtls;
	}
	public void setEshipmentDtls(ShipmentDetails eshipmentDtls) {
		this.eshipmentDtls = eshipmentDtls;
	}
}
