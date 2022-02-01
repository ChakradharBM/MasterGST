package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table8 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table8GetDetails {

	@Id
	private ObjectId id;
	String flag;
	String chksum;
	
	@JsonProperty("itc_inwd_supp")
	GSTR9Table8SummaryDetails itcInwdSupp = new GSTR9Table8SummaryDetails();
	@JsonProperty("itc_nt_availd")
	GSTR9Table8SummaryDetails itcNtAvaild = new GSTR9Table8SummaryDetails();
	@JsonProperty("itc_nt_eleg")
	GSTR9Table8SummaryDetails itcNtEleg = new GSTR9Table8SummaryDetails();
	@JsonProperty("iog_taxpaid")
	GSTR9Table8SummaryDetails iogTaxpaid = new GSTR9Table8SummaryDetails();
	
	@JsonProperty("itc_2a")
	GSTR9Table8ITC2A itc2a = new GSTR9Table8ITC2A();
	@JsonProperty("itc_tot")
	GSTR9Table4OtherThanExpSezDetails itcTot = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails differenceABC = new GSTR9Table4OtherThanExpSezDetails();
	GSTR9Table4OtherThanExpSezDetails differenceGH = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("iog_itc_availd")
	GSTR9Table4OtherThanExpSezDetails iogItcAvaild = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("iog_itc_ntavaild")
	GSTR9Table4OtherThanExpSezDetails iogItcNtavaild = new GSTR9Table4OtherThanExpSezDetails();
	@JsonProperty("tot_itc_lapsed")
	GSTR9Table4OtherThanExpSezDetails totItcLapsed = new GSTR9Table4OtherThanExpSezDetails();
	
	public GSTR9Table8GetDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table8SummaryDetails getItcInwdSupp() {
		return itcInwdSupp;
	}

	public void setItcInwdSupp(GSTR9Table8SummaryDetails itcInwdSupp) {
		this.itcInwdSupp = itcInwdSupp;
	}

	public GSTR9Table8SummaryDetails getItcNtAvaild() {
		return itcNtAvaild;
	}

	public void setItcNtAvaild(GSTR9Table8SummaryDetails itcNtAvaild) {
		this.itcNtAvaild = itcNtAvaild;
	}

	public GSTR9Table8SummaryDetails getItcNtEleg() {
		return itcNtEleg;
	}

	public void setItcNtEleg(GSTR9Table8SummaryDetails itcNtEleg) {
		this.itcNtEleg = itcNtEleg;
	}

	public GSTR9Table8SummaryDetails getIogTaxpaid() {
		return iogTaxpaid;
	}

	public void setIogTaxpaid(GSTR9Table8SummaryDetails iogTaxpaid) {
		this.iogTaxpaid = iogTaxpaid;
	}

	public GSTR9Table8ITC2A getItc2a() {
		return itc2a;
	}

	public void setItc2a(GSTR9Table8ITC2A itc2a) {
		this.itc2a = itc2a;
	}

	public GSTR9Table4OtherThanExpSezDetails getItcTot() {
		return itcTot;
	}

	public void setItcTot(GSTR9Table4OtherThanExpSezDetails itcTot) {
		this.itcTot = itcTot;
	}

	public GSTR9Table4OtherThanExpSezDetails getDifferenceABC() {
		return differenceABC;
	}

	public void setDifferenceABC(GSTR9Table4OtherThanExpSezDetails differenceABC) {
		this.differenceABC = differenceABC;
	}

	public GSTR9Table4OtherThanExpSezDetails getDifferenceGH() {
		return differenceGH;
	}

	public void setDifferenceGH(GSTR9Table4OtherThanExpSezDetails differenceGH) {
		this.differenceGH = differenceGH;
	}

	public GSTR9Table4OtherThanExpSezDetails getIogItcAvaild() {
		return iogItcAvaild;
	}

	public void setIogItcAvaild(GSTR9Table4OtherThanExpSezDetails iogItcAvaild) {
		this.iogItcAvaild = iogItcAvaild;
	}

	public GSTR9Table4OtherThanExpSezDetails getIogItcNtavaild() {
		return iogItcNtavaild;
	}

	public void setIogItcNtavaild(GSTR9Table4OtherThanExpSezDetails iogItcNtavaild) {
		this.iogItcNtavaild = iogItcNtavaild;
	}

	public GSTR9Table4OtherThanExpSezDetails getTotItcLapsed() {
		return totItcLapsed;
	}

	public void setTotItcLapsed(GSTR9Table4OtherThanExpSezDetails totItcLapsed) {
		this.totItcLapsed = totItcLapsed;
	}
	
	
}
