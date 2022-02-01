/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.ClientPaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PartnerClientInfo;
import com.mastergst.usermanagement.runtime.domain.PartnerFilter;
import com.mastergst.usermanagement.runtime.domain.PartnerPayments;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;

/**
 * Service interface for Partner's Client to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PartnerService {

	PartnerClient createClient(PartnerClient partnerClient);
	List<PartnerClient> getPartnerClients(final String userid);
	List<PartnerClient> getPartnerJoinedClients(String userid);
	List<PartnerClient> getPartnerBilledClients(String userid);
	void updatePartnerDetails(final String inviteId, final User user);
	void updatePartnerDetailsFromAdmin(final String inviteId, final User user);
	
	public List<PartnerClient> getPartnerReferenceClients(String userid);

	public Page<PartnerClient> getPartnerClients(int month,String year,int start, int  length, String sortParam, String sortOrder,String  searchVal);
	public Page<PartnerClient> getPartnerClients(int month,String year,int start, int  length, String sortParam, String sortOrder,String  searchVal, PartnerFilter filter,List<String> userids);

	public List<PartnerClient> getAllPartnerClients();

	public void updatePartnerClientInfo(List<PartnerClient> filteredLst);
	
	PartnerClient findById(String id);
	Page<? extends PartnerClient> getPartners(Pageable pageable, String id, int start, int length, String searchVal);
	Page<User> getPartners(String tabName, int start, int length, String sortParam, String sortOrder,String searchVal);
	Page<User> getPartners(String tabName);
	Page<User> getPartners(String tabName, PartnerFilter filter);
	Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids,int month,int year, int start, int length,String sortParam, String sortOrder, String searchVal);
	
	Page<PartnerClient> getSalesTeamPartners(String tabName, List<String> userids,int month,int year, int start, int length,String sortParam, String sortOrder, String searchVal, PartnerFilter filter);
	
	Map<String, Map<String, String>> getConsolidatedSummeryForDays(String yearCode);
	Map<String, Map<String, String>> getConsolidatedSummeryForDaysInMonth(String yearCode,int mthcd,String dayWeek,String tabName,List<String> userids);
	Map<String, Object> getPartnerSupport (String yearCode,int mthcd,String dayWeek,String tabName,List<String> userids);
	Map<String, Map<String, String>> getConsolidatedSummeryForDaysInMonth(String yearCode,int mthcd,String dayWeek,String tabName,List<String> userids, PartnerFilter filter);

	public PartnerPayments findByPartnerPaymentsUserid(String userid, int month, int year);
	public PartnerPayments savePartnerPayments(PartnerPayments payments, int month, int year);
	
	public Page<User> findByPartnerUsers(int month, String yearCode, int start, int length, String searchVal);
	public Page<User> findByPartnerUsers(Date stDate, Date endDate, int start, int length, String searchVal);
	public Page<PartnerClientInfo> getPartnerReferenceClients(String userid, int start, int length, String sortParam, String sortOrder, String searchVal);
	public Page<ClientPaymentDetails> getPartnerClientPayments(String userid, String payment, int month, int year,
												int start, int length, String sortParam, String sortOrder, String searchVal);
	public Page<PartnerPayments> getPartnerPayments(String userid, String payment, int month, int year, int start, int length, String sortParam, String sortOrder, String searchVal);
	public PartnerPayments findByPartnerPaymentsDocIdAndMonthYearCode(String docid, int month, String yearCode);
	public PartnerPayments savePpartnerPayments(PartnerPayments payments, int month, String yearCode);
	public Page<PartnerClient> getPartnerJoinedClients(String id, String type, int start, int length, String sortParam, String sortOrder, String searchVal);

}
