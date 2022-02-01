/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.login.runtime.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

/**
 * User information.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
@Document(collection = "users")
public class User extends Base implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fullname;

	private String password;

	private String email;
	
	private String refid;

	private String mobilenumber;

	private String address;
	
	private String branches;
	
	private String ewaybillApi;
	
	private String einvoiceApi;
	private String gstApi;
	
	private String notes;

	private String anyerp;

	private String type;
	
	private String isglobal;
	
	private String disable;
	
	private String parentid;

	private String numberofclients;
	
	private String registrationnumber;
	
	private String userId;
	
	private String subscriptionType;
	
	private String paidAmount;

	private List<UserKeys> userkeys;
	
	private String totalClients;
	
	private String totalInvoices;
	
	private String totalInvoicesUsed;
	
	private Date lastLoggedIn;
	private String usrLastLoggedIn;

	private String totalInvitedClients;
	
	private String totalPendingClients;
	
	private String totalJoinedClients;
	
	private String totalSubscribedClients;
	
	private String totalCenters;

	private boolean accessDrive = false;

	private boolean accessImports = false;

	private boolean accessReports = false;

	private boolean accessJournalExcel = false;
	
	private boolean accessGstr9 = false;
	
	private boolean accessMultiGSTNSearch = false;
	
	private boolean accessReminders = false;
	
	private boolean accessANX1 = false;
	private boolean accessANX2 = false;
	
	private boolean accessDwnldewabillinv = false;
	private boolean accessEinvoice = false;
	private boolean accessCustomFieldTemplate = false;
	private boolean accessAcknowledgement = false;
	private boolean accessEntertainmentEinvoiceTemplate = false;
	private boolean accessTravel = false;
	
	//private boolean accessGstr4Annual = false;
	//private boolean accessGstr6 = false;
	private boolean accessGstr8 = false;

	private String parentName;
	
	private String parentEmailId;
	
	private String partnerPercentage;
	
	private long userSequenceid;
	private String needFollowupdate;
	
	private String partnerEmail;
	private String partnerType;
	private Integer gstAPIUsageCountInvoices;
	private Integer gstSanboxUsageCountInvoices;
	private Integer ewaybillAPIUsageCountInvoices;
	private Integer ewaybillSanboxUsageCountInvoices;
	private Integer einvAPIUsageCountInvoices;
	private Integer einvSanboxUsageCountInvoices;

	private Integer gstAPIAllowedInvoices;
	private Integer gstSanboxAllowedCountInvoices;
	private Integer ewaybillAPIAllowedInvoices;
	private Integer ewaybillSanboxAllowedInvoices;
	private Integer einvAPIAllowedInvoices;
	private Integer einvSanboxAllowedInvoices;

	private String subscriptionStartDate;
	private String subscriptionExpiryDate;
	
	private String gstSubscriptionStartDate;
	private String gstSubscriptionExpiryDate;

	private String gstSandboxSubscriptionStartDate;
	private String gstSandboxSubscriptionExpiryDate;

	private String ewaybillSubscriptionStartDate;
	private String ewaybillSubscriptionExpiryDate;

	private String ewaybillSandboxSubscriptionStartDate;
	private String ewaybillSandboxSubscriptionExpiryDate;

	private String einvSubscriptionStartDate;
	private String einvSubscriptionExpiryDate;

	private String einvSandboxSubscriptionStartDate;
	private String einvSandboxSubscriptionExpiryDate;

	private String needToFollowUp;
	private String agreementStatus;
	private String needToFollowUpComment;

	/* comments class variables */
	private String comments;
	private String commentDate;
	private String addedby;
	
	private String gstAPIStatus;
	private String gstSandboxAPIStatus;
	private String ewaybillAPIStatus;
	private String ewaybillSandboxAPIStatus;
	private String einvAPIStatus;
	private String einvSandboxAPIStatus;
	
	private String sandboxApplied;
	private String quotationSent;
	
	private String otpVerified;

	private String gstin;
	private String pan;
	private String authorisedSignatory;
	private String authorisedPANNumber;
	private String businessName;
	private String dealerType;
	private String stateName;
	private String category;
	private String needFollowup;
	private String apiType;
	private String userSignDate;
	private String patmentStatus;
	private String userSignatureDetails;
	private String isRemindersEnabled;
	private String reminderEmail;
	
	private String subscriptionamount;
	private String paymentStatus;
	private String invoiceFromPartner;
	private String mthCd;
	private String yrCd;
	public User() {
		this.id = ObjectId.get();
	}

	public String getUsrLastLoggedIn() {
		return usrLastLoggedIn;
	}

	public void setUsrLastLoggedIn(String usrLastLoggedIn) {
		this.usrLastLoggedIn = usrLastLoggedIn;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getRefid() {
		return refid;
	}
	
	public void setRefid(String refid) {
		this.refid = refid;
	}

	public String getMobilenumber() {
		return mobilenumber;
	}

	public void setMobilenumber(String mobilenumber) {
		this.mobilenumber = mobilenumber;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<UserKeys> getUserkeys() {
		return userkeys;
	}

	public void setUserkeys(List<UserKeys> userkeys) {
		this.userkeys = userkeys;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIsglobal() {
		return isglobal;
	}

	public void setIsglobal(String isglobal) {
		this.isglobal = isglobal;
	}

	public String getDisable() {
		return disable;
	}

	public void setDisable(String disable) {
		this.disable = disable;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getNumberofclients() {
		return numberofclients;
	}

	public void setNumberofclients(String numberofclients) {
		this.numberofclients = numberofclients;
	}

	public String getRegistrationnumber() {
		return registrationnumber;
	}

	public void setRegistrationnumber(String registrationnumber) {
		this.registrationnumber = registrationnumber;
	}

	public String getAnyerp() {
		return anyerp;
	}

	public void setAnyerp(String anyerp) {
		this.anyerp = anyerp;
	}

	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}
	
	public String getTotalClients() {
		return totalClients;
	}
	
	public void setTotalClients(String totalClients) {
		this.totalClients = totalClients;
	}
	
	public String getTotalInvoices() {
		return totalInvoices;
	}
	
	public void setTotalInvoices(String totalInvoices) {
		this.totalInvoices = totalInvoices;
	}

	public String getTotalCenters() {
		return totalCenters;
	}

	public void setTotalCenters(String totalCenters) {
		this.totalCenters = totalCenters;
	}
	
	public Date getLastLoggedIn() {
		return lastLoggedIn;
	}

	public void setLastLoggedIn(Date lastLoggedIn) {
		this.lastLoggedIn = lastLoggedIn;
	}
	
	public String getTotalInvitedClients() {
		return totalInvitedClients;
	}
	
	public void setTotalInvitedClients(String totalInvitedClients) {
		this.totalInvitedClients = totalInvitedClients;
	}
	
	public String getTotalPendingClients() {
		return totalPendingClients;
	}
	
	public void setTotalPendingClients(String totalPendingClients) {
		this.totalPendingClients = totalPendingClients;
	}
	
	public String getTotalJoinedClients() {
		return totalJoinedClients;
	}
	
	public void setTotalJoinedClients(String totalJoinedClients) {
		this.totalJoinedClients = totalJoinedClients;
	}
	
	public String getTotalSubscribedClients() {
		return totalSubscribedClients;
	}
	
	public void setTotalSubscribedClients(String totalSubscribedClients) {
		this.totalSubscribedClients = totalSubscribedClients;
	}

	public String getTotalInvoicesUsed() {
		return totalInvoicesUsed;
	}

	public void setTotalInvoicesUsed(String totalInvoicesUsed) {
		this.totalInvoicesUsed = totalInvoicesUsed;
	}

	public String getEwaybillApi() {
		return ewaybillApi;
	}

	public void setEwaybillApi(String ewaybillApi) {
		this.ewaybillApi = ewaybillApi;
	}

	public String getGstApi() {
		return gstApi;
	}

	public void setGstApi(String gstApi) {
		this.gstApi = gstApi;
	}

	public boolean isAccessDrive() {
		return accessDrive;
	}

	public void setAccessDrive(boolean accessDrive) {
		this.accessDrive = accessDrive;
	}
	
	public boolean isAccessImports() {
		return accessImports;
	}

	public void setAccessImports(boolean accessImports) {
		this.accessImports = accessImports;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentEmailId() {
		return parentEmailId;
	}

	public void setParentEmailId(String parentEmailId) {
		this.parentEmailId = parentEmailId;
	}

	public String getPartnerPercentage() {
		return partnerPercentage;
	}

	public void setPartnerPercentage(String partnerPercentage) {
		this.partnerPercentage = partnerPercentage;
	}

	public boolean isAccessReports() {
		return accessReports;
	}

	public void setAccessReports(boolean accessReports) {
		this.accessReports = accessReports;
	}

	public Integer getGstAPIAllowedInvoices() {
		return gstAPIAllowedInvoices;
	}

	public void setGstAPIAllowedInvoices(Integer gstAPIAllowedInvoices) {
		this.gstAPIAllowedInvoices = gstAPIAllowedInvoices;
	}

	public Integer getGstSanboxAllowedCountInvoices() {
		return gstSanboxAllowedCountInvoices;
	}

	public void setGstSanboxAllowedCountInvoices(Integer gstSanboxAllowedCountInvoices) {
		this.gstSanboxAllowedCountInvoices = gstSanboxAllowedCountInvoices;
	}

	public Integer getEwaybillAPIAllowedInvoices() {
		return ewaybillAPIAllowedInvoices;
	}

	public void setEwaybillAPIAllowedInvoices(Integer ewaybillAPIAllowedInvoices) {
		this.ewaybillAPIAllowedInvoices = ewaybillAPIAllowedInvoices;
	}

	public Integer getEwaybillSanboxAllowedInvoices() {
		return ewaybillSanboxAllowedInvoices;
	}

	public void setEwaybillSanboxAllowedInvoices(Integer ewaybillSanboxAllowedInvoices) {
		this.ewaybillSanboxAllowedInvoices = ewaybillSanboxAllowedInvoices;
	}

	public Integer getGstAPIUsageCountInvoices() {
		return gstAPIUsageCountInvoices;
	}

	public void setGstAPIUsageCountInvoices(Integer gstAPIUsageCountInvoices) {
		this.gstAPIUsageCountInvoices = gstAPIUsageCountInvoices;
	}

	public Integer getGstSanboxUsageCountInvoices() {
		return gstSanboxUsageCountInvoices;
	}

	public void setGstSanboxUsageCountInvoices(Integer gstSanboxUsageCountInvoices) {
		this.gstSanboxUsageCountInvoices = gstSanboxUsageCountInvoices;
	}

	public Integer getEwaybillAPIUsageCountInvoices() {
		return ewaybillAPIUsageCountInvoices;
	}

	public void setEwaybillAPIUsageCountInvoices(Integer ewaybillAPIUsageCountInvoices) {
		this.ewaybillAPIUsageCountInvoices = ewaybillAPIUsageCountInvoices;
	}

	public Integer getEwaybillSanboxUsageCountInvoices() {
		return ewaybillSanboxUsageCountInvoices;
	}

	public void setEwaybillSanboxUsageCountInvoices(Integer ewaybillSanboxUsageCountInvoices) {
		this.ewaybillSanboxUsageCountInvoices = ewaybillSanboxUsageCountInvoices;
	}

	public String getNeedToFollowUp() {
		return needToFollowUp;
	}

	public void setNeedToFollowUp(String needToFollowUp) {
		this.needToFollowUp = needToFollowUp;
	}

	public String getAgreementStatus() {
		return agreementStatus;
	}

	public void setAgreementStatus(String agreementStatus) {
		this.agreementStatus = agreementStatus;
	}

	public String getNeedToFollowUpComment() {
		return needToFollowUpComment;
	}

	public void setNeedToFollowUpComment(String needToFollowUpComment) {
		this.needToFollowUpComment = needToFollowUpComment;
	}

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

	public void setAddedby(String addedby) {
		this.addedby = addedby;
	}

	public String getSubscriptionStartDate() {
		return subscriptionStartDate;
	}

	public void setSubscriptionStartDate(String subscriptionStartDate) {
		this.subscriptionStartDate = subscriptionStartDate;
	}

	public String getSubscriptionExpiryDate() {
		return subscriptionExpiryDate;
	}

	public void setSubscriptionExpiryDate(String subscriptionExpiryDate) {
		this.subscriptionExpiryDate = subscriptionExpiryDate;
	}

	public String getGstSubscriptionStartDate() {
		return gstSubscriptionStartDate;
	}

	public void setGstSubscriptionStartDate(String gstSubscriptionStartDate) {
		this.gstSubscriptionStartDate = gstSubscriptionStartDate;
	}

	public String getGstSubscriptionExpiryDate() {
		return gstSubscriptionExpiryDate;
	}

	public void setGstSubscriptionExpiryDate(String gstSubscriptionExpiryDate) {
		this.gstSubscriptionExpiryDate = gstSubscriptionExpiryDate;
	}

	public String getGstSandboxSubscriptionStartDate() {
		return gstSandboxSubscriptionStartDate;
	}

	public void setGstSandboxSubscriptionStartDate(String gstSandboxSubscriptionStartDate) {
		this.gstSandboxSubscriptionStartDate = gstSandboxSubscriptionStartDate;
	}

	public String getGstSandboxSubscriptionExpiryDate() {
		return gstSandboxSubscriptionExpiryDate;
	}

	public void setGstSandboxSubscriptionExpiryDate(String gstSandboxSubscriptionExpiryDate) {
		this.gstSandboxSubscriptionExpiryDate = gstSandboxSubscriptionExpiryDate;
	}

	public String getEwaybillSubscriptionStartDate() {
		return ewaybillSubscriptionStartDate;
	}

	public void setEwaybillSubscriptionStartDate(String ewaybillSubscriptionStartDate) {
		this.ewaybillSubscriptionStartDate = ewaybillSubscriptionStartDate;
	}

	public String getEwaybillSubscriptionExpiryDate() {
		return ewaybillSubscriptionExpiryDate;
	}

	public void setEwaybillSubscriptionExpiryDate(String ewaybillSubscriptionExpiryDate) {
		this.ewaybillSubscriptionExpiryDate = ewaybillSubscriptionExpiryDate;
	}

	public String getEwaybillSandboxSubscriptionStartDate() {
		return ewaybillSandboxSubscriptionStartDate;
	}

	public void setEwaybillSandboxSubscriptionStartDate(String ewaybillSandboxSubscriptionStartDate) {
		this.ewaybillSandboxSubscriptionStartDate = ewaybillSandboxSubscriptionStartDate;
	}

	public String getEwaybillSandboxSubscriptionExpiryDate() {
		return ewaybillSandboxSubscriptionExpiryDate;
	}

	public void setEwaybillSandboxSubscriptionExpiryDate(String ewaybillSandboxSubscriptionExpiryDate) {
		this.ewaybillSandboxSubscriptionExpiryDate = ewaybillSandboxSubscriptionExpiryDate;
	}

	public String getGstAPIStatus() {
		return gstAPIStatus;
	}

	public void setGstAPIStatus(String gstAPIStatus) {
		this.gstAPIStatus = gstAPIStatus;
	}

	public String getGstSandboxAPIStatus() {
		return gstSandboxAPIStatus;
	}

	public void setGstSandboxAPIStatus(String gstSandboxAPIStatus) {
		this.gstSandboxAPIStatus = gstSandboxAPIStatus;
	}

	public String getEwaybillAPIStatus() {
		return ewaybillAPIStatus;
	}

	public void setEwaybillAPIStatus(String ewaybillAPIStatus) {
		this.ewaybillAPIStatus = ewaybillAPIStatus;
	}

	public String getEwaybillSandboxAPIStatus() {
		return ewaybillSandboxAPIStatus;
	}

	public void setEwaybillSandboxAPIStatus(String ewaybillSandboxAPIStatus) {
		this.ewaybillSandboxAPIStatus = ewaybillSandboxAPIStatus;
	}

	public String getSandboxApplied() {
		return sandboxApplied;
	}

	public void setSandboxApplied(String sandboxApplied) {
		this.sandboxApplied = sandboxApplied;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getAuthorisedSignatory() {
		return authorisedSignatory;
	}

	public void setAuthorisedSignatory(String authorisedSignatory) {
		this.authorisedSignatory = authorisedSignatory;
	}

	public String getAuthorisedPANNumber() {
		return authorisedPANNumber;
	}

	public void setAuthorisedPANNumber(String authorisedPANNumber) {
		this.authorisedPANNumber = authorisedPANNumber;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getDealerType() {
		return dealerType;
	}

	public void setDealerType(String dealerType) {
		this.dealerType = dealerType;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getOtpVerified() {
		return otpVerified;
	}

	public void setOtpVerified(String otpVerified) {
		this.otpVerified = otpVerified;
	}

	public String getQuotationSent() {
		return quotationSent;
	}

	public void setQuotationSent(String quotationSent) {
		this.quotationSent = quotationSent;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getNeedFollowup() {
		return needFollowup;
	}

	public void setNeedFollowup(String needFollowup) {
		this.needFollowup = needFollowup;
	}
	
	public long getUserSequenceid() {
		return userSequenceid;
	}

	public void setUserSequenceid(long userSequenceid) {
		this.userSequenceid = userSequenceid;
	}
	
	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}

	public String getUserSignDate() {
		return userSignDate;
	}

	public void setUserSignDate(String userSignDate) {
		this.userSignDate = userSignDate;
	}

	public String getPatmentStatus() {
		return patmentStatus;
	}

	public void setPatmentStatus(String patmentStatus) {
		this.patmentStatus = patmentStatus;
	}

	public boolean isAccessJournalExcel() {
		return accessJournalExcel;
	}

	public void setAccessJournalExcel(boolean accessJournalExcel) {
		this.accessJournalExcel = accessJournalExcel;
	}

	public String getNeedFollowupdate() {
		return needFollowupdate;
	}

	public void setNeedFollowupdate(String needFollowupdate) {
		this.needFollowupdate = needFollowupdate;
	}

	public String getPartnerEmail() {
		return partnerEmail;
	}

	public void setPartnerEmail(String partnerEmail) {
		this.partnerEmail = partnerEmail;
	}
	
	public boolean isAccessGstr9() {
		return accessGstr9;
	}

	public void setAccessGstr9(boolean accessGstr9) {
		this.accessGstr9 = accessGstr9;
	}

	
	public boolean isAccessMultiGSTNSearch() {
		return accessMultiGSTNSearch;
	}

	public void setAccessMultiGSTNSearch(boolean accessMultiGSTNSearch) {
		this.accessMultiGSTNSearch = accessMultiGSTNSearch;
	}
	
	public boolean isAccessDwnldewabillinv() {
		return accessDwnldewabillinv;
	}

	public void setAccessDwnldewabillinv(boolean accessDwnldewabillinv) {
		this.accessDwnldewabillinv = accessDwnldewabillinv;
	}

	public boolean isAccessReminders() {
		return accessReminders;
	}

	public void setAccessReminders(boolean accessReminders) {
		this.accessReminders = accessReminders;
	}
	
	public String getUserSignatureDetails() {
		return userSignatureDetails;
	}

	public void setUserSignatureDetails(String userSignatureDetails) {
		this.userSignatureDetails = userSignatureDetails;
	}
	
		public String getEinvoiceApi() {
		return einvoiceApi;
	}

	public void setEinvoiceApi(String einvoiceApi) {
		this.einvoiceApi = einvoiceApi;
	}
	public boolean isAccessEinvoice() {
		return accessEinvoice;
	}
	public void setAccessEinvoice(boolean accessEinvoice) {
		this.accessEinvoice = accessEinvoice;
	}
	
	public boolean isAccessCustomFieldTemplate() {
		return accessCustomFieldTemplate;
	}
	public void setAccessCustomFieldTemplate(boolean accessCustomFieldTemplate) {
		this.accessCustomFieldTemplate = accessCustomFieldTemplate;
	}
	public boolean isAccessEntertainmentEinvoiceTemplate() {
		return accessEntertainmentEinvoiceTemplate;
	}
	public void setAccessEntertainmentEinvoiceTemplate(boolean accessEntertainmentEinvoiceTemplate) {
		this.accessEntertainmentEinvoiceTemplate = accessEntertainmentEinvoiceTemplate;
	}

	public boolean isAccessAcknowledgement() {
		return accessAcknowledgement;
	}
	public void setAccessAcknowledgement(boolean accessAcknowledgement) {
		this.accessAcknowledgement = accessAcknowledgement;
	}
	
	public boolean isAccessTravel() {
		return accessTravel;
	}

	public void setAccessTravel(boolean accessTravel) {
		this.accessTravel = accessTravel;
	}

	public String getIsRemindersEnabled() {
		return isRemindersEnabled;
	}

	public void setIsRemindersEnabled(String isRemindersEnabled) {
		this.isRemindersEnabled = isRemindersEnabled;
	}

	public String getReminderEmail() {
		return reminderEmail;
	}

	public void setReminderEmail(String reminderEmail) {
		this.reminderEmail = reminderEmail;
	}
	public boolean isAccessANX1() {
		return accessANX1;
	}

	public void setAccessANX1(boolean accessANX1) {
		this.accessANX1 = accessANX1;
	}

	public boolean isAccessANX2() {
		return accessANX2;
	}

	public void setAccessANX2(boolean accessANX2) {
		this.accessANX2 = accessANX2;
	}

	public String getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(String partnerType) {
		this.partnerType = partnerType;
	}
	
	public Integer getEinvAPIUsageCountInvoices() {
		return einvAPIUsageCountInvoices;
	}

	public void setEinvAPIUsageCountInvoices(Integer einvAPIUsageCountInvoices) {
		this.einvAPIUsageCountInvoices = einvAPIUsageCountInvoices;
	}

	public Integer getEinvSanboxUsageCountInvoices() {
		return einvSanboxUsageCountInvoices;
	}

	public void setEinvSanboxUsageCountInvoices(Integer einvSanboxUsageCountInvoices) {
		this.einvSanboxUsageCountInvoices = einvSanboxUsageCountInvoices;
	}

	public Integer getEinvAPIAllowedInvoices() {
		return einvAPIAllowedInvoices;
	}

	public void setEinvAPIAllowedInvoices(Integer einvAPIAllowedInvoices) {
		this.einvAPIAllowedInvoices = einvAPIAllowedInvoices;
	}

	public Integer getEinvSanboxAllowedInvoices() {
		return einvSanboxAllowedInvoices;
	}

	public void setEinvSanboxAllowedInvoices(Integer einvSanboxAllowedInvoices) {
		this.einvSanboxAllowedInvoices = einvSanboxAllowedInvoices;
	}

	public String getEinvSubscriptionStartDate() {
		return einvSubscriptionStartDate;
	}

	public void setEinvSubscriptionStartDate(String einvSubscriptionStartDate) {
		this.einvSubscriptionStartDate = einvSubscriptionStartDate;
	}

	public String getEinvSubscriptionExpiryDate() {
		return einvSubscriptionExpiryDate;
	}

	public void setEinvSubscriptionExpiryDate(String einvSubscriptionExpiryDate) {
		this.einvSubscriptionExpiryDate = einvSubscriptionExpiryDate;
	}

	public String getEinvSandboxSubscriptionStartDate() {
		return einvSandboxSubscriptionStartDate;
	}

	public void setEinvSandboxSubscriptionStartDate(String einvSandboxSubscriptionStartDate) {
		this.einvSandboxSubscriptionStartDate = einvSandboxSubscriptionStartDate;
	}

	public String getEinvSandboxSubscriptionExpiryDate() {
		return einvSandboxSubscriptionExpiryDate;
	}

	public void setEinvSandboxSubscriptionExpiryDate(String einvSandboxSubscriptionExpiryDate) {
		this.einvSandboxSubscriptionExpiryDate = einvSandboxSubscriptionExpiryDate;
	}

	public String getEinvAPIStatus() {
		return einvAPIStatus;
	}

	public void setEinvAPIStatus(String einvAPIStatus) {
		this.einvAPIStatus = einvAPIStatus;
	}

	public String getEinvSandboxAPIStatus() {
		return einvSandboxAPIStatus;
	}

	public void setEinvSandboxAPIStatus(String einvSandboxAPIStatus) {
		this.einvSandboxAPIStatus = einvSandboxAPIStatus;
	}

	public String getSubscriptionamount() {
		return subscriptionamount;
	}

	public void setSubscriptionamount(String subscriptionamount) {
		this.subscriptionamount = subscriptionamount;
	}

	public String getPaymentStatus() {
		return paymentStatus;
	}

	public void setPaymentStatus(String paymentStatus) {
		this.paymentStatus = paymentStatus;
	}

	public String getInvoiceFromPartner() {
		return invoiceFromPartner;
	}

	public void setInvoiceFromPartner(String invoiceFromPartner) {
		this.invoiceFromPartner = invoiceFromPartner;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	/*public boolean isAccessGstr4Annual() {
		return accessGstr4Annual;
	}

	public void setAccessGstr4Annual(boolean accessGstr4Annual) {
		this.accessGstr4Annual = accessGstr4Annual;
	}

	public boolean isAccessGstr6() {
		return accessGstr6;
	}

	public void setAccessGstr6(boolean accessGstr6) {
		this.accessGstr6 = accessGstr6;
	}*/

	public boolean isAccessGstr8() {
		return accessGstr8;
	}

	public void setAccessGstr8(boolean accessGstr8) {
		this.accessGstr8 = accessGstr8;
	}
	
	
	
}
