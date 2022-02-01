package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

public class GSTR4IMPS {
	private String pos;
	private List<GSTR4ImpsItem> itms;

	public String getPos() {
		return pos;
	}

	public void setPos(String pos) {
		this.pos = pos;
	}

	public List<GSTR4ImpsItem> getItms() {
		return itms;
	}

	public void setItms(List<GSTR4ImpsItem> itms) {
		this.itms = itms;
	}

}
