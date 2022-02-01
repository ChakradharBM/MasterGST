/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

/**
 * This class MasterGST MisMatch POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
public class MisMatchVO {

	private String purchaseId;
	private String gstrId;
	
	public MisMatchVO() {
	}
	
	public MisMatchVO(String purchaseId, String gstrId) {
		this.purchaseId = purchaseId;
		this.gstrId = gstrId;
	}
	
	public String getPurchaseId() {
		return purchaseId;
	}
	public void setPurchaseId(String purchaseId) {
		this.purchaseId = purchaseId;
	}
	public String getGstrId() {
		return gstrId;
	}
	public void setGstrId(String gstrId) {
		this.gstrId = gstrId;
	}
	
}
