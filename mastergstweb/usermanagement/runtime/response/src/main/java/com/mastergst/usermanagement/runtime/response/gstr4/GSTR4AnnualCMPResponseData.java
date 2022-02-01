package com.mastergst.usermanagement.runtime.response.gstr4;
import java.util.List;

//@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class GSTR4AnnualCMPResponseData {
	private String gstin;
	private String fp;
	private GSTR4CMPSummary cmpsmry;
	private List<GSTR4TdsTcsSummary> tdstcs;
	
	public String getGstin() {
		return gstin;
	}
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	public String getFp() {
		return fp;
	}
	public void setFp(String fp) {
		this.fp = fp;
	}
	public GSTR4CMPSummary getCmpsmry() {
		return cmpsmry;
	}
	public void setCmpsmry(GSTR4CMPSummary cmpsmry) {
		this.cmpsmry = cmpsmry;
	}
	public List<GSTR4TdsTcsSummary> getTdstcs() {
		return tdstcs;
	}
	public void setTdstcs(List<GSTR4TdsTcsSummary> tdstcs) {
		this.tdstcs = tdstcs;
	}
	
	
}
