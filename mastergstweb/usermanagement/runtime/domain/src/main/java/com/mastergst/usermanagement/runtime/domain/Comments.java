/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.io.Serializable;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.mastergst.core.domain.Base;

@Document(collection="comments")
public class Comments extends Base implements Serializable,Comparable<Comments>{

	private static final long serialVersionUID = 1L;
	
	private String userid;
	private String comments;
	@DateTimeFormat(pattern="dd/MM/yyyy")
	private String commentDate;
	private String addedby;
	private String stage;
	
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}
	public String getAddedby() {
		return addedby;
	}
	public void setAddedby(String addby) {
		this.addedby = addby;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	@Override
	public String toString() {
		return "Comments [userid=" + userid + ", comments=" + comments + ", commentDate=" + commentDate + ", addedby="
				+ addedby + "]";
	}
	@Override
	public int compareTo(Comments cmnt) {
		
		return this.getCreatedDate().compareTo(cmnt.getCreatedDate());
	}
	public String getStage() {
		return stage;
	}
	public void setStage(String stage) {
		this.stage = stage;
	}	
}
