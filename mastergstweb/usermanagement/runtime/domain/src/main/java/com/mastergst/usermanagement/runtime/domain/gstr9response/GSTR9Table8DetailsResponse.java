package com.mastergst.usermanagement.runtime.domain.gstr9response;

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
@JsonIgnoreProperties({ "id" })
public class GSTR9Table8DetailsResponse {

	@Id
	private ObjectId id;
	String flag;
	@JsonProperty("itc_inwd_supp")
	GSTR9Table8SummaryDetailsResponse itcInwdSupp = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("itc_nt_avalid")
	GSTR9Table8SummaryDetailsResponse itcNtAvaild = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("itc_nt_eleg")
	GSTR9Table8SummaryDetailsResponse itcNtEleg = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("iog_taxpaid")
	GSTR9Table8SummaryDetailsResponse iogTaxpaid = new GSTR9Table8SummaryDetailsResponse();

	@JsonProperty("itc_2a")
	GSTR9Table8SummaryDetailsResponse itc2a = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("itc_tot")
	GSTR9Table8SummaryDetailsResponse itcTot = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("iog_itc_availd")
	GSTR9Table8SummaryDetailsResponse iogItcAvaild = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("iog_itc_ntavaild")
	GSTR9Table8SummaryDetailsResponse iogItcNtavaild = new GSTR9Table8SummaryDetailsResponse();
	GSTR9Table8SummaryDetailsResponse differenceGH = new GSTR9Table8SummaryDetailsResponse();
	GSTR9Table8SummaryDetailsResponse differenceABC = new GSTR9Table8SummaryDetailsResponse();
	@JsonProperty("tot_itc_lapsed")
	GSTR9Table8SummaryDetailsResponse totItcLapsed = new GSTR9Table8SummaryDetailsResponse();

	public GSTR9Table8SummaryDetailsResponse getItc2a() {
		return itc2a;
	}

	public void setItc2a(GSTR9Table8SummaryDetailsResponse itc2a) {
		this.itc2a = itc2a;
	}

	public GSTR9Table8SummaryDetailsResponse getItcTot() {
		return itcTot;
	}

	public void setItcTot(GSTR9Table8SummaryDetailsResponse itcTot) {
		this.itcTot = itcTot;
	}

	public GSTR9Table8SummaryDetailsResponse getIogItcAvaild() {
		return iogItcAvaild;
	}

	public void setIogItcAvaild(GSTR9Table8SummaryDetailsResponse iogItcAvaild) {
		this.iogItcAvaild = iogItcAvaild;
	}

	public GSTR9Table8SummaryDetailsResponse getIogItcNtavaild() {
		return iogItcNtavaild;
	}

	public void setIogItcNtavaild(GSTR9Table8SummaryDetailsResponse iogItcNtavaild) {
		this.iogItcNtavaild = iogItcNtavaild;
	}

	public GSTR9Table8SummaryDetailsResponse getDifferenceGH() {
		return differenceGH;
	}

	public void setDifferenceGH(GSTR9Table8SummaryDetailsResponse differenceGH) {
		this.differenceGH = differenceGH;
	}

	public GSTR9Table8SummaryDetailsResponse getDifferenceABC() {
		return differenceABC;
	}

	public void setDifferenceABC(GSTR9Table8SummaryDetailsResponse differenceABC) {
		this.differenceABC = differenceABC;
	}

	public GSTR9Table8SummaryDetailsResponse getTotItcLapsed() {
		return totItcLapsed;
	}

	public void setTotItcLapsed(GSTR9Table8SummaryDetailsResponse totItcLapsed) {
		this.totItcLapsed = totItcLapsed;
	}

	public GSTR9Table8DetailsResponse() {
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

	public GSTR9Table8SummaryDetailsResponse getItcInwdSupp() {
		return itcInwdSupp;
	}

	public void setItcInwdSupp(GSTR9Table8SummaryDetailsResponse itcInwdSupp) {
		this.itcInwdSupp = itcInwdSupp;
	}

	public GSTR9Table8SummaryDetailsResponse getItcNtAvaild() {
		return itcNtAvaild;
	}

	public void setItcNtAvaild(GSTR9Table8SummaryDetailsResponse itcNtAvaild) {
		this.itcNtAvaild = itcNtAvaild;
	}

	public GSTR9Table8SummaryDetailsResponse getItcNtEleg() {
		return itcNtEleg;
	}

	public void setItcNtEleg(GSTR9Table8SummaryDetailsResponse itcNtEleg) {
		this.itcNtEleg = itcNtEleg;
	}

	public GSTR9Table8SummaryDetailsResponse getIogTaxpaid() {
		return iogTaxpaid;
	}

	public void setIogTaxpaid(GSTR9Table8SummaryDetailsResponse iogTaxpaid) {
		this.iogTaxpaid = iogTaxpaid;
	}

}
