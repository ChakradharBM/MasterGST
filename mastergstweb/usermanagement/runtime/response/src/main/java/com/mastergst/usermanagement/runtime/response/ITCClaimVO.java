/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.Date;
import java.util.List;

/**
 * This class MasterGST MisMatch POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
public class ITCClaimVO {

	
	private String invId;
	private String itctype;
	private String retType;
	private Double itcamt;
	private Date claimeddate;
	private List<String> invIds;
	
	public ITCClaimVO() {
	}

	public String getInvId() {
		return invId;
	}

	public void setInvId(String invId) {
		this.invId = invId;
	}

	public String getItctype() {
		return itctype;
	}

	public void setItctype(String itctype) {
		this.itctype = itctype;
	}

	public String getRetType() {
		return retType;
	}

	public void setRetType(String retType) {
		this.retType = retType;
	}

	public Double getItcamt() {
		return itcamt;
	}

	public void setItcamt(Double itcamt) {
		this.itcamt = itcamt;
	}

	public Date getClaimeddate() {
		return claimeddate;
	}

	public void setClaimeddate(Date claimeddate) {
		this.claimeddate = claimeddate;
	}

	public List<String> getInvIds() {
		return invIds;
	}

	public void setInvIds(List<String> invIds) {
		this.invIds = invIds;
	}
	
	
}
