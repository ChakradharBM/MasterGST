/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.List;

/**
 * This class MasterGST MisMatch POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
public class SendMessageVO {

	private String invId;
	
	public SendMessageVO() {
	}

	public SendMessageVO(String invId) {
		this.invId = invId;
	}

	public String getInvId() {
		return invId;
	}

	public void setInvId(String invId) {
		this.invId = invId;
	}


	
	
}
