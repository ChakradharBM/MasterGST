package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "attachments")
public class Attachments extends Base {

	private String userid;
	private String clientid;

	private List<S3Attachments> s3files;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public List<S3Attachments> getS3files() {
		return s3files;
	}

	public void setS3files(List<S3Attachments> s3files) {
		this.s3files = s3files;
	}
}
