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

@Document(collection="leads_comments")
public class LeadsComments extends Base implements Serializable,Comparable<LeadsComments>{

	private static final long serialVersionUID = 1L;
	
	private String userid;
	private String inviteid;
	private String leadscomments;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private Date commentDate;
	private String addedby;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getInviteid() {
		return inviteid;
	}

	public void setInviteid(String inviteid) {
		this.inviteid = inviteid;
	}
	public String getLeadscomments() {
		return leadscomments;
	}
	public void setLeadscomments(String leadscomments) {
		this.leadscomments = leadscomments;
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
	public int compareTo(LeadsComments cmnt) {
		
		return this.getCreatedDate().compareTo(cmnt.getCreatedDate());
	}
	
	
}
