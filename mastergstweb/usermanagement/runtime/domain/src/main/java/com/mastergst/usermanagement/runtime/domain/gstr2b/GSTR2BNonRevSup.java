package com.mastergst.usermanagement.runtime.domain.gstr2b;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR2BNonRevSup {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double igst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sgst;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cess;
	private GSTR2BTaxAmounts b2b;
	private GSTR2BTaxAmounts b2ba;
	private GSTR2BTaxAmounts cdnr;
	private GSTR2BTaxAmounts cdnra;

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

	public GSTR2BTaxAmounts getB2b() {
		return b2b;
	}

	public void setB2b(GSTR2BTaxAmounts b2b) {
		this.b2b = b2b;
	}

	public GSTR2BTaxAmounts getB2ba() {
		return b2ba;
	}

	public void setB2ba(GSTR2BTaxAmounts b2ba) {
		this.b2ba = b2ba;
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

}
