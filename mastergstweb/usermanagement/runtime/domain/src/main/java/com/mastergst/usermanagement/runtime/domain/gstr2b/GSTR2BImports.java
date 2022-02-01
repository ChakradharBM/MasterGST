package com.mastergst.usermanagement.runtime.domain.gstr2b;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BImports {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	private GSTR2BTaxAmounts impg;
	private GSTR2BTaxAmounts impgsez;
	private GSTR2BTaxAmounts impga;
	private GSTR2BTaxAmounts impgasez;
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
	public GSTR2BTaxAmounts getImpg() {
		return impg;
	}
	public void setImpg(GSTR2BTaxAmounts impg) {
		this.impg = impg;
	}
	public GSTR2BTaxAmounts getImpgsez() {
		return impgsez;
	}
	public void setImpgsez(GSTR2BTaxAmounts impgsez) {
		this.impgsez = impgsez;
	}
	public GSTR2BTaxAmounts getImpga() {
		return impga;
	}
	public void setImpga(GSTR2BTaxAmounts impga) {
		this.impga = impga;
	}
	public GSTR2BTaxAmounts getImpgasez() {
		return impgasez;
	}
	public void setImpgasez(GSTR2BTaxAmounts impgasez) {
		this.impgasez = impgasez;
	}
}
