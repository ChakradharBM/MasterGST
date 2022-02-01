package com.mastergst.usermanagement.runtime.domain.gstr2b;

import java.util.List;

public class GSTR2BCPSummary {
	private List<GSTR2BCPDetails> b2b;
	private List<GSTR2BCPDetails> b2ba;
	private List<GSTR2BCDNCPDetails> cdnr;
	private List<GSTR2BCDNCPDetails> cdna;
	private List<GSTR2BISDCPdetails> isd;
	private List<GSTR2BISDCPdetails> isda;
	private List<GSTR2BIMPGCPdetails> impgsez;

	public List<GSTR2BCPDetails> getB2b() {
		return b2b;
	}

	public void setB2b(List<GSTR2BCPDetails> b2b) {
		this.b2b = b2b;
	}

	public List<GSTR2BCPDetails> getB2ba() {
		return b2ba;
	}

	public void setB2ba(List<GSTR2BCPDetails> b2ba) {
		this.b2ba = b2ba;
	}

	public List<GSTR2BCDNCPDetails> getCdnr() {
		return cdnr;
	}

	public void setCdnr(List<GSTR2BCDNCPDetails> cdnr) {
		this.cdnr = cdnr;
	}

	public List<GSTR2BCDNCPDetails> getCdna() {
		return cdna;
	}

	public void setCdna(List<GSTR2BCDNCPDetails> cdna) {
		this.cdna = cdna;
	}

	public List<GSTR2BISDCPdetails> getIsd() {
		return isd;
	}

	public void setIsd(List<GSTR2BISDCPdetails> isd) {
		this.isd = isd;
	}

	public List<GSTR2BISDCPdetails> getIsda() {
		return isda;
	}

	public void setIsda(List<GSTR2BISDCPdetails> isda) {
		this.isda = isda;
	}

	public List<GSTR2BIMPGCPdetails> getImpgsez() {
		return impgsez;
	}

	public void setImpgsez(List<GSTR2BIMPGCPdetails> impgsez) {
		this.impgsez = impgsez;
	}

}
