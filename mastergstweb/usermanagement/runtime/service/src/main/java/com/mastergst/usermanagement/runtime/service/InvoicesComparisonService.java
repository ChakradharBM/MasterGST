package com.mastergst.usermanagement.runtime.service;

import java.text.ParseException;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.usermanagement.runtime.domain.GSTR2B_vs_GSTR2;

public interface InvoicesComparisonService {
	
	public Map<String, Object> getGSTR2B_vs_GSTR2Report(String clientid, int month, int year, int start, int length, String searchVal, boolean b) throws MasterGSTException, ParseException;

}
