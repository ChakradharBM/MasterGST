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
public class GSTR9CTable9Details {
	@Id
	private ObjectId id;
	
	private List<GSTR9CRateDetails> rate=LazyList.decorate(new ArrayList<GSTR9CRateDetails>(), 
			FactoryUtils.instantiateFactory(GSTR9CRateDetails.class));
	
	private GSTR9CItemRateDetails inter = new GSTR9CItemRateDetails();
	
	@JsonProperty("late_fee")
	private GSTR9CItemRateDetails lateFee = new GSTR9CItemRateDetails();
	private GSTR9CItemRateDetails pen = new GSTR9CItemRateDetails();
	private GSTR9CItemRateDetails oth = new GSTR9CItemRateDetails();
	@JsonProperty("tot_amt_payable")
	private GSTR9CItemRateDetails totAmtPayable =  new GSTR9CItemRateDetails();
	@JsonProperty("tot_amt_paid")
	private GSTR9CItemRateDetails  totAmtPaid=  new GSTR9CItemRateDetails();
	@JsonProperty("unrec_amt")
	private GSTR9CItemRateDetails  unrecAmt=  new GSTR9CItemRateDetails();
	
	public GSTR9CTable9Details() {
		this.id = ObjectId.get();
	}
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public List<GSTR9CRateDetails> getRate() {
		return rate;
	}
	public void setRate(List<GSTR9CRateDetails> rate) {
		this.rate = rate;
	}
	public GSTR9CItemRateDetails getInter() {
		return inter;
	}
	public void setInter(GSTR9CItemRateDetails inter) {
		this.inter = inter;
	}
	public GSTR9CItemRateDetails getLateFee() {
		return lateFee;
	}
	public void setLateFee(GSTR9CItemRateDetails lateFee) {
		this.lateFee = lateFee;
	}
	public GSTR9CItemRateDetails getPen() {
		return pen;
	}
	public void setPen(GSTR9CItemRateDetails pen) {
		this.pen = pen;
	}
	public GSTR9CItemRateDetails getOth() {
		return oth;
	}
	public void setOth(GSTR9CItemRateDetails oth) {
		this.oth = oth;
	}
	public GSTR9CItemRateDetails getTotAmtPayable() {
		return totAmtPayable;
	}
	public void setTotAmtPayable(GSTR9CItemRateDetails totAmtPayable) {
		this.totAmtPayable = totAmtPayable;
	}
	public GSTR9CItemRateDetails getTotAmtPaid() {
		return totAmtPaid;
	}
	public void setTotAmtPaid(GSTR9CItemRateDetails totAmtPaid) {
		this.totAmtPaid = totAmtPaid;
	}
	public GSTR9CItemRateDetails getUnrecAmt() {
		return unrecAmt;
	}
	public void setUnrecAmt(GSTR9CItemRateDetails unrecAmt) {
		this.unrecAmt = unrecAmt;
	}
	
	
}
