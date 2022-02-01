/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import com.mastergst.configuration.service.SubscriptionPlan;
import com.mastergst.login.runtime.domain.ApiExceedsUsers;
import com.mastergst.login.runtime.domain.SubscriptionUsers;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PaymentLink;
import com.mastergst.usermanagement.runtime.domain.PaymentRequestDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;

/**
 * Service interface for Subscription logic to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
public interface SubscriptionService {

	List<SubscriptionPlan> getSubscriptions();
	
	List<SubscriptionPlan> getSubscriptions(final String category);

	SubscriptionPlan getPlan(String id);
	
	boolean allowAddClient(final String userId, final Integer addedClients);
	
	boolean allowAddCenters(final String userId, final Integer addedCenters);
	
	boolean allowUploadInvoices(final String userId, final Long invoices);
	
	boolean allowUploadInvoices(final String userId, final String clientid, final Long invoices);
	
	SubscriptionDetails getSubscriptionData(final String userId);
	
	SubscriptionDetails getSubscriptionData(final String userId, final String apiType);
	
	SubscriptionDetails getUserSubscriptionDetails(final String userId);
	
	SubscriptionDetails updateSubscriptionData(final SubscriptionDetails subscriptionDetails);
	
	List<SubscriptionDetails> getAllSubscriptions();
	
	List<SubscriptionDetails> getSubscriptionDetailsByDates(final Date startDate, final Date endDate);
	
	void saveSubscriptionDetails(List<SubscriptionDetails> subscriptionDetails);
	
	Map<String, String> processPaymentResponse(PaymentRequestDetails paymentRequestDetails, String userid, 
			String encryptedResponse, String response, SubscriptionPlan plan);
	
	Map<String, String> processPaymentLinkResponse(PaymentRequestDetails paymentRequestDetails, String userid, 
			String encryptedResponse, String response, PaymentLink plan);
	
	void updateOfflinePaymentDetails(final String id, final String usertype, final Double amount, 
			final Integer invoices, final Integer clients, final Integer centers, final Double rate, 
			final String paymentMode, final String referenceNo, final String stage, final String statename,final String planid, final String subscriptionType,final String subscriptionStartDate,final String subscriptionExpiryDate);
	
	List<PaymentDetails> getPaymentDetails(final String userId);
	
	List<PaymentDetails> getPaymentDetails(final String userId,final String apiType);
	List<PaymentDetails> getPaymentDetails(final String userId,final String apiType,final List<String> subscriptionstartdate,final List<String> subscriptionenddate);
	
	SubscriptionPlan getPlanByName(String name);
	SubscriptionPlan getPlanByNameForBussiness(String name);
	
	public SubscriptionDetails getSubscriptionPaymentDetails(final String id,final String apiType);
	public SubscriptionDetails getSubscriptionPaymentDetails(final String id);
	
	public List<SubscriptionDetails> getAllSubscriptionDetailsByUser(final String userid);
	public List<SubscriptionDetails> getEwaybillAPIsSubscriptionDetailsApiTye(final String userid);
	
	//Page<SubscriptionDetails> getBeforeFourtyFiveDaysExpiryUsers();
	//Page<SubscriptionDetails> getExpiredUsers();
	
	SubscriptionDetails getRetriveSubscriptionDetailsByObjectId(String id);

	List<User> getPaidUsersAndActiveUsers();
	
	List<User> getDemoUsers();

	Double getByRevenueSubscriptionDataUsersidIn(List<String> userids);
	Double getByRevenueSubscriptionDataUsersidInAndApitype(List<String> userids,String apitype);
	
	List<SubscriptionDetails> getASPUsersidsIn(List<String> userids);

	Double getByRevenueSubscriptionDataUsersidInAndNotInDemo(List<String> aspuserids);
	
	public SubscriptionDetails getSubscriptionDataByDelete(final String userId);
	public void deletesubscriptiondata(final String subscriptionid);
	
	List<SubscriptionDetails> getSubscriptionsByuserid(String userids);

	public void subscriptiondataDeleteByUserid(List<SubscriptionDetails> subscriptiondata);

	public Page<ApiExceedsUsers> getExceedUsageApisInfo(String apis,int start, int length, String searchVal);

	Page<SubscriptionUsers> getExpiredUsers(String type, int start, int length, String searchVal);

	public List<PaymentDetails> getPartnerPaymentDetails(String userids, int month, String yearCode);
}
