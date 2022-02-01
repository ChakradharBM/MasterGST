/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;

@Document(collection="suppliercomments")
public class SupplierComments extends Base implements Serializable,Comparable<SupplierComments>{

	private static final long serialVersionUID = 1L;
	
	private String userid;
	private String invoiceid;
	private String clientid;
	private String supcomments;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date commentDate;
	private String addedby;
	

	public String getClientid() {
		return clientid;
	}


	public void setClientid(String clientid) {
		this.clientid = clientid;
	}


	public String getUserid() {
		return userid;
	}


	public void setUserid(String userid) {
		this.userid = userid;
	}


	public String getInvoiceid() {
		return invoiceid;
	}


	public void setInvoiceid(String invoiceid) {
		this.invoiceid = invoiceid;
	}


	public String getSupcomments() {
		return supcomments;
	}


	public void setSupcomments(String supcomments) {
		this.supcomments = supcomments;
	}


	public Date getCommentDate() {
		return commentDate;
	}


	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}


	public String getAddedby() {
		return addedby;
	}


	public void setAddedby(String addedby) {
		this.addedby = addedby;
	}


	@Override
	public int compareTo(SupplierComments cmnt) {
		
		return this.getCreatedDate().compareTo(cmnt.getCreatedDate());
	}
	
	
}
