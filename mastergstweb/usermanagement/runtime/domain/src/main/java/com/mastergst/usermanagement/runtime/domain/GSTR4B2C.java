package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class GSTR4B2C {
	private String rchrg;
	private String pos;
	private String trdnm;
	private String sply_ty;
	private String cpan;
	private List<GSTR4Items> itms;

	public String getRchrg() {
		return rchrg;
	}

	public void setRchrg(String rchrg) {
		this.rchrg = rchrg;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public String getTrdnm() {
		return trdnm;
	}

	public void setTrdnm(String trdnm) {
		this.trdnm = trdnm;
	}

	public String getSply_ty() {
		return sply_ty;
	}

	public void setSply_ty(String sply_ty) {
		this.sply_ty = sply_ty;
	}

	public String getCpan() {
		return cpan;
	}

	public void setCpan(String cpan) {
		this.cpan = cpan;
	}

	public List<GSTR4Items> getItms() {
		return itms;
	}

	public void setItms(List<GSTR4Items> itms) {
		this.itms = itms;
	}
}
