/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * Client config
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "client_config")
public class ClientConfig extends Base {

	private String clientId;
	private String userId;
	private Double reconcileDiff = 0d;
	/* private String invoiceMatch; */
	private boolean enableInvoiceMatch; 
	private Map<String, String> filingOption;
	private Double allowedDays;
	
	private boolean enableIgnoreSlash;
	private boolean enableIgnoreHyphen;
	private boolean enableIgnoreZero;
	private boolean enableIgnoreI;
	private boolean enableIgnoreL;
	
	/*
	 * private boolean enableInvoiceFirstFiveCharMatch; private boolean
	 * enableInvoiceLastFiveCharMatch;
	 */
	
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Double getReconcileDiff() {
		return reconcileDiff;
	}
	public void setReconcileDiff(Double reconcileDiff) {
		this.reconcileDiff = reconcileDiff;
	}
	public Map<String, String> getFilingOption() {
		return filingOption;
	}
	public void setFilingOption(Map<String, String> filingOption) {
		this.filingOption = filingOption;
	}
	public Double getAllowedDays() {
		return allowedDays;
	}
	public void setAllowedDays(Double allowedDays) {
		this.allowedDays = allowedDays;
	}

	
	public boolean isEnableIgnoreSlash() {
		return enableIgnoreSlash;
	}
	public void setEnableIgnoreSlash(boolean enableIgnoreSlash) {
		this.enableIgnoreSlash = enableIgnoreSlash;
	}
	public boolean isEnableIgnoreHyphen() {
		return enableIgnoreHyphen;
	}
	public void setEnableIgnoreHyphen(boolean enableIgnoreHyphen) {
		this.enableIgnoreHyphen = enableIgnoreHyphen;
	}
	public boolean isEnableIgnoreZero() {
		return enableIgnoreZero;
	}
	public void setEnableIgnoreZero(boolean enableIgnoreZero) {
		this.enableIgnoreZero = enableIgnoreZero;
	}
	public boolean isEnableIgnoreI() {
		return enableIgnoreI;
	}
	public void setEnableIgnoreI(boolean enableIgnoreI) {
		this.enableIgnoreI = enableIgnoreI;
	}
	public boolean isEnableIgnoreL() {
		return enableIgnoreL;
	}
	public void setEnableIgnoreL(boolean enableIgnoreL) {
		this.enableIgnoreL = enableIgnoreL;
	}
	public boolean isEnableInvoiceMatch() {
		return enableInvoiceMatch;
	}
	public void setEnableInvoiceMatch(boolean enableInvoiceMatch) {
		this.enableInvoiceMatch = enableInvoiceMatch;
	}
	/*
	 * public boolean isEnableInvoiceFirstFiveCharMatch() { return
	 * enableInvoiceFirstFiveCharMatch; } public void
	 * setEnableInvoiceFirstFiveCharMatch(boolean enableInvoiceFirstFiveCharMatch) {
	 * this.enableInvoiceFirstFiveCharMatch = enableInvoiceFirstFiveCharMatch; }
	 * public boolean isEnableInvoiceLastFiveCharMatch() { return
	 * enableInvoiceLastFiveCharMatch; } public void
	 * setEnableInvoiceLastFiveCharMatch(boolean enableInvoiceLastFiveCharMatch) {
	 * this.enableInvoiceLastFiveCharMatch = enableInvoiceLastFiveCharMatch; }
	 */
	
	
}
