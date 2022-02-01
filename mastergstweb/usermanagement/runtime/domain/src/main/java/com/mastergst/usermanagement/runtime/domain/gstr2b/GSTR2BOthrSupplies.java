package com.mastergst.usermanagement.runtime.domain.gstr2b;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BOthrSupplies {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	private GSTR2BTaxAmounts cdnr;
	private GSTR2BTaxAmounts cdnra;
	private GSTR2BTaxAmounts cdnrrev;
	private GSTR2BTaxAmounts cdnrarev;
	private GSTR2BTaxAmounts isd;
	private GSTR2BTaxAmounts isda;
	public Double getIgst() {
		return igst;
	}
	public void setIgst(Double igst) {
		this.igst = igst;
	}
	public Double getCgst() {
		return cgst;
	}
	public void setCgst(Double cgst) {
		this.cgst = cgst;
	}
	public Double getSgst() {
		return sgst;
	}
	public void setSgst(Double sgst) {
		this.sgst = sgst;
	}
	public Double getCess() {
		return cess;
	}
	public void setCess(Double cess) {
		this.cess = cess;
	}
	public GSTR2BTaxAmounts getCdnr() {
		return cdnr;
	}
	public void setCdnr(GSTR2BTaxAmounts cdnr) {
		this.cdnr = cdnr;
	}
	public GSTR2BTaxAmounts getCdnra() {
		return cdnra;
	}
	public void setCdnra(GSTR2BTaxAmounts cdnra) {
		this.cdnra = cdnra;
	}
	public GSTR2BTaxAmounts getCdnrrev() {
		return cdnrrev;
	}
	public void setCdnrrev(GSTR2BTaxAmounts cdnrrev) {
		this.cdnrrev = cdnrrev;
	}
	public GSTR2BTaxAmounts getCdnrarev() {
		return cdnrarev;
	}
	public void setCdnrarev(GSTR2BTaxAmounts cdnrarev) {
		this.cdnrarev = cdnrarev;
	}
	public GSTR2BTaxAmounts getIsd() {
		return isd;
	}
	public void setIsd(GSTR2BTaxAmounts isd) {
		this.isd = isd;
	}
	public GSTR2BTaxAmounts getIsda() {
		return isda;
	}
	public void setIsda(GSTR2BTaxAmounts isda) {
		this.isda = isda;
	}
	
}
