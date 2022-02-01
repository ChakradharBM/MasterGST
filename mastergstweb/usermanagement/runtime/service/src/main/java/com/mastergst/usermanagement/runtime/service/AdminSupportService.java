package com.mastergst.usermanagement.runtime.service;

import org.springframework.data.domain.Page;

import com.mastergst.login.runtime.domain.CompaignEmail;
import com.mastergst.login.runtime.domain.ReconcileUsers;

public interface AdminSupportService {
	
	public Page<ReconcileUsers> getReconcileInfo(int start ,int length, String searchVal);
	void compaignEmails(CompaignEmail compaignEmail, String type);

}
