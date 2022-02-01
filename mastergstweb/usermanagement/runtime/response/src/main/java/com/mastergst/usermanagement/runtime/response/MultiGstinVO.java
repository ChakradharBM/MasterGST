package com.mastergst.usermanagement.runtime.response;

import java.util.List;


public class MultiGstinVO {
	
	List<String> gstnid;
	List<String> multigstin;
	
	public List<String> getGstnid() {
		return gstnid;
	}
	public void setGstnid(List<String> gstnid) {
		this.gstnid = gstnid;
	}
	public List<String> getMultigstin() {
		return multigstin;
	}
	public void setMultigstin(List<String> multigstin) {
		this.multigstin = multigstin;
	}
	
	
	
}
