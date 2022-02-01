/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Message Configuration information.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Document(collection = "messages")
public class Message extends Base {

	private String message;
	private String msgId;
	private String subject;
	private List<String> usertype;
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public List<String> getUsertype() {
		return usertype;
	}
	public void setUsertype(List<String> usertype) {
		this.usertype = usertype;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getMsgId() {
		return msgId;
	}
	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}
