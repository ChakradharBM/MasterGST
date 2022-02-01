package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9CAddLiabilities {

	@Id
	private ObjectId id;
	
	@JsonProperty("tax_pay")
	private List<GSTR9CTaxpayDetails> taxPay=LazyList.decorate(new ArrayList<GSTR9CTaxpayDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9CTaxpayDetails.class));
	
	private String place;
	private String signatory;
	@JsonProperty("mem_no")
	private String memNo;
	private String date;
	@JsonProperty("audit_addr")
	private GSTR9CAuditorAddressDetails auditAddr = new GSTR9CAuditorAddressDetails();
	@JsonProperty("pan_no")
	private String panNo;
	
	public GSTR9CAddLiabilities() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<GSTR9CTaxpayDetails> getTaxPay() {
		return taxPay;
	}
	public void setTaxPay(List<GSTR9CTaxpayDetails> taxPay) {
		this.taxPay = taxPay;
	}
	public String getPlace() {
		return place;
	}
	public void setPlace(String place) {
		this.place = place;
	}
	public String getSignatory() {
		return signatory;
	}
	public void setSignatory(String signatory) {
		this.signatory = signatory;
	}
	public String getMemNo() {
		return memNo;
	}
	public void setMemNo(String memNo) {
		this.memNo = memNo;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public GSTR9CAuditorAddressDetails getAuditAddr() {
		return auditAddr;
	}
	public void setAuditAddr(GSTR9CAuditorAddressDetails auditAddr) {
		this.auditAddr = auditAddr;
	}
	public String getPanNo() {
		return panNo;
	}
	public void setPanNo(String panNo) {
		this.panNo = panNo;
	}
	
	
}
