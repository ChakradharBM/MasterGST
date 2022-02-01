/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Email Configuration information.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
@Document(collection = "email")
public class EmailConfig extends Base {

	private String host;
	private String port;
	private String auth;
	private String from;
	private String username;
	private String password;
	private String subject;
	private String template;
	private String awsEnabled;
	private String awsUsername;
	private String awsPassword;
	private String awsFrom;
	private String awsHost;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public String getAwsEnabled() {
		return awsEnabled;
	}

	public void setAwsEnabled(String awsEnabled) {
		this.awsEnabled = awsEnabled;
	}

	public String getAwsUsername() {
		return awsUsername;
	}

	public void setAwsUsername(String awsUsername) {
		this.awsUsername = awsUsername;
	}

	public String getAwsPassword() {
		return awsPassword;
	}

	public void setAwsPassword(String awsPassword) {
		this.awsPassword = awsPassword;
	}

	public String getAwsFrom() {
		return awsFrom;
	}

	public void setAwsFrom(String awsFrom) {
		this.awsFrom = awsFrom;
	}

	public String getAwsHost() {
		return awsHost;
	}

	public void setAwsHost(String awsHost) {
		this.awsHost = awsHost;
	}

}
