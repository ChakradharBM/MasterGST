/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

public class DigioRequest {

	private String signer, content, format, callback, comment,signature_type,file,email;
	private int expire_in_days;
	private boolean notify_signers;

	public String getSigner() {
		return signer;
	}

	public void setSigner(String signer) {
		this.signer = signer;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getCallback() {
		return callback;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getExpire_in_days() {
		return expire_in_days;
	}

	public void setExpire_in_days(int expire_in_days) {
		this.expire_in_days = expire_in_days;
	}

	public boolean isNotify_signers() {
		return notify_signers;
	}

	public void setNotify_signers(boolean notify_signers) {
		this.notify_signers = notify_signers;
	}

	public String getSignature_type() {
		return signature_type;
	}

	public void setSignature_type(String signature_type) {
		this.signature_type = signature_type;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
