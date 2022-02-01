package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class GSTR4B2B {
	private String ctin;
	private String pos;
	private List<GSTR4Items> itms;

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public List<GSTR4Items> getItms() {
		return itms;
	}

	public void setItms(List<GSTR4Items> itms) {
		this.itms = itms;
	}

}
