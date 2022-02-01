package com.mastergst.usermanagement.runtime.domain.gstr9response;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GSTR9 Table5 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9Table5DetailsResponse {
	
	@Id
	private ObjectId id;
	
	String chksum;
	@JsonProperty("zero_rtd")
	GSTR9Table5ItemDetailsResponse zeroRtd = new GSTR9Table5ItemDetailsResponse();
	GSTR9Table5ItemDetailsResponse sez = new GSTR9Table5ItemDetailsResponse();
	GSTR9Table5ItemDetailsResponse rchrg = new GSTR9Table5ItemDetailsResponse();
	GSTR9Table5ItemDetailsResponse exmt = new GSTR9Table5ItemDetailsResponse();
	GSTR9Table5ItemDetailsResponse nil = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("non_gst")
	GSTR9Table5ItemDetailsResponse nonGst = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("cr_nt")
	GSTR9Table5ItemDetailsResponse crNt = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("dr_nt")
	GSTR9Table5ItemDetailsResponse drNt = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("amd_pos")
	GSTR9Table5ItemDetailsResponse amdPos = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("amd_neg")
	GSTR9Table5ItemDetailsResponse amdNeg = new GSTR9Table5ItemDetailsResponse();
	
	@JsonProperty("sub_totalAF")
	GSTR9Table5ItemDetailsResponse subTotalAF = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("sub_totalHK")
	GSTR9Table5ItemDetailsResponse subTotalHK = new GSTR9Table5ItemDetailsResponse();
	@JsonProperty("tover_tax_np")
	GSTR9Table5ItemDetailsResponse toverTaxNp = new GSTR9Table5ItemDetailsResponse();
	
	@JsonProperty("total_tover")
	GSTR9Table5ItemDetailsTotalToverResponse totalTover = new GSTR9Table5ItemDetailsTotalToverResponse();
	
	public GSTR9Table5ItemDetailsResponse getSubTotalAF() {
		return subTotalAF;
	}

	public void setSubTotalAF(GSTR9Table5ItemDetailsResponse subTotalAF) {
		this.subTotalAF = subTotalAF;
	}

	public GSTR9Table5ItemDetailsResponse getSubTotalHK() {
		return subTotalHK;
	}

	public void setSubTotalHK(GSTR9Table5ItemDetailsResponse subTotalHK) {
		this.subTotalHK = subTotalHK;
	}

	public GSTR9Table5ItemDetailsResponse getToverTaxNp() {
		return toverTaxNp;
	}

	public void setToverTaxNp(GSTR9Table5ItemDetailsResponse toverTaxNp) {
		this.toverTaxNp = toverTaxNp;
	}

	public GSTR9Table5DetailsResponse() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getChksum() {
		return chksum;
	}

	public void setChksum(String chksum) {
		this.chksum = chksum;
	}

	public GSTR9Table5ItemDetailsResponse getZeroRtd() {
		return zeroRtd;
	}

	public void setZeroRtd(GSTR9Table5ItemDetailsResponse zeroRtd) {
		this.zeroRtd = zeroRtd;
	}

	public GSTR9Table5ItemDetailsResponse getSez() {
		return sez;
	}

	public void setSez(GSTR9Table5ItemDetailsResponse sez) {
		this.sez = sez;
	}

	public GSTR9Table5ItemDetailsResponse getRchrg() {
		return rchrg;
	}

	public void setRchrg(GSTR9Table5ItemDetailsResponse rchrg) {
		this.rchrg = rchrg;
	}

	public GSTR9Table5ItemDetailsResponse getExmt() {
		return exmt;
	}

	public void setExmt(GSTR9Table5ItemDetailsResponse exmt) {
		this.exmt = exmt;
	}

	public GSTR9Table5ItemDetailsResponse getNil() {
		return nil;
	}

	public void setNil(GSTR9Table5ItemDetailsResponse nil) {
		this.nil = nil;
	}

	public GSTR9Table5ItemDetailsResponse getNonGst() {
		return nonGst;
	}

	public void setNonGst(GSTR9Table5ItemDetailsResponse nonGst) {
		this.nonGst = nonGst;
	}

	public GSTR9Table5ItemDetailsResponse getCrNt() {
		return crNt;
	}

	public void setCrNt(GSTR9Table5ItemDetailsResponse crNt) {
		this.crNt = crNt;
	}

	public GSTR9Table5ItemDetailsResponse getDrNt() {
		return drNt;
	}

	public void setDrNt(GSTR9Table5ItemDetailsResponse drNt) {
		this.drNt = drNt;
	}

	public GSTR9Table5ItemDetailsResponse getAmdPos() {
		return amdPos;
	}

	public void setAmdPos(GSTR9Table5ItemDetailsResponse amdPos) {
		this.amdPos = amdPos;
	}

	public GSTR9Table5ItemDetailsResponse getAmdNeg() {
		return amdNeg;
	}

	public void setAmdNeg(GSTR9Table5ItemDetailsResponse amdNeg) {
		this.amdNeg = amdNeg;
	}
	
	
}
