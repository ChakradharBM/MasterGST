package com.mastergst.login.runtime.domain;

import java.util.List;

import com.mastergst.core.domain.Base;

/**
 * @author MasterGST Tech sols
 *
 */
public class CompaignEmail extends Base {

	private String subject;
	private String mailBody;
	private String userType;
	private String senderid;
	private String sender;
	private Long success = 0l;
	private Long failure = 0l;
	private List<String> failureMails;

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMailBody() {
		return mailBody;
	}

	public void setMailBody(String mailBody) {
		this.mailBody = mailBody;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getSenderid() {
		return senderid;
	}

	public void setSenderid(String senderid) {
		this.senderid = senderid;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Long getSuccess() {
		return success;
	}

	public void setSuccess(Long success) {
		this.success = success;
	}

	public Long getFailure() {
		return failure;
	}

	public void setFailure(Long failure) {
		this.failure = failure;
	}

	public List<String> getFailureMails() {
		return failureMails;
	}

	public void setFailureMails(List<String> failureMails) {
		this.failureMails = failureMails;
	}
}
