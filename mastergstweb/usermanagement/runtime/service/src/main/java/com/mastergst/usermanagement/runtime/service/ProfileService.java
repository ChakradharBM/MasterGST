/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;


import com.mastergst.configuration.service.Groups;
import com.mastergst.usermanagement.runtime.domain.AccountingHeads;
import com.mastergst.usermanagement.runtime.domain.AcknowledgementPermission;
import com.mastergst.usermanagement.runtime.domain.AdminUsers;
import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CommonBO;
import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;
import com.mastergst.usermanagement.runtime.domain.CompanyCenters;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyGroup;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.CompanyProducts;
import com.mastergst.usermanagement.runtime.domain.CompanyRole;
import com.mastergst.usermanagement.runtime.domain.CompanyServices;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DashboardRoles;
import com.mastergst.usermanagement.runtime.domain.EcommerceOpeartor;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;
import com.mastergst.usermanagement.runtime.domain.PartnerBankDetails;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.Subgroups;

/**
 * Service interface for Profile to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ProfileService {

	List<CompanyUser> getGlobalUsers(final String userid);
	List<CompanyUser> getUsers(final String userid);
	List<CompanyUser> getUsers(final String userid, final String clientid);
	List<CompanyUser> getUsersByClientId(final String clientid);
	boolean checkEmailExists(final String email);
	CompanyUser getCompanyUser(final String email);
	CompanyUser getCompanyUser(final String email, final String clientid);
	
	CompanyUser saveUser(CompanyUser user, final String clientid);
	List<CompanyRole> getRoles(final String userid);
	List<CompanyRole> getRoles(final String userid, final String clientid);
	CompanyRole saveRole(CompanyRole role);
	List<CompanyGroup> getGroups(final String userid, final String clientid);
	CompanyGroup saveGroup(CompanyGroup group);
	List<CompanyInvoices> getInvoiceSubmission(final String userid, final String clientid);
	List<CompanyInvoices> getInvoiceSubmissionDetails(final String userid, final String clientid, final String year);
	List<CompanyInvoices> getUserInvoiceSubmissionDetails(final String clientid, final String year);
	CompanyInvoices getUserInvoiceSubmissionDetails(final String clientid, final String year, final String invoiceType);
	CompanyInvoices getInvoiceSubmissionDetails(final String userid, final String clientid, final String year, final String invoiceType);
	CompanyInvoices getInvoiceSubmissionDetails(final String userid, final String clientid, final String year, final String invoiceType,final String returnType);
	List<CompanyInvoices> getInvoices(final String userid);
	CompanyInvoices saveInvoiceSubmission(CompanyInvoices invoice);
	List<CompanyProducts> getProducts(final String clientid);
	CompanyProducts saveProduct(CompanyProducts product);
	List<CompanyCustomers> getCustomers(String clientid);
	List<CompanyCustomers> getCustomers(String id,String clientid);
	CompanyCustomers saveCustomer(CompanyCustomers customer);
	List<CompanyCenters> getCenters(String userid);
	CompanyCenters saveCenter(CompanyCenters center, final String clientid);
	CompanyCenters saveCenterSuvidhaSignup(CompanyCenters center);
	List<CompanySuppliers> getSuppliers(String clientid);
	List<CompanySuppliers> getSuppliers(String id,String clientid);
	CompanySuppliers saveSupplier(CompanySuppliers supplier);
	List<CompanyServices> getServices(final String clientid);
	CompanyServices saveService(CompanyServices service);
	List<CompanyItems> getItems(final String clientid);
	CommonBO getCommonList(final String clientid, final String searchQuery);
	CompanyItems saveItem(CompanyItems item);
	CompanyBankDetails saveBankDetails(CompanyBankDetails bankdetails);
	CompanyBankDetails saveBankDetailsQRCode(CompanyBankDetails bankdetails,MultipartFile file);
	
	
	
	
	PartnerBankDetails savePBankDetails(PartnerBankDetails bankdetails);
	CompanyBankDetails getBankDetails(String clientid);
	PartnerBankDetails getPBankDetails(String id);
	List<CompanyBankDetails> getCompanyBankDetails(final String userid,final String clientid);
	List<CompanyBankDetails> getCompanyBankDetails(final String clientid);
	
	void createUserForClient(CompanyUser user, final String userid);
	
	Map<String, List<Permission>> getPermissions(final String userid);
	Map<String, List<Permission>> getUserClientPermissions(final String userid, final String clientid);
	Set<String> getClientPermissions(final String clientid);
	void saveClientPermissions(final String clientid, final Set<String> permissions);

	void updateExcelData(final MultipartFile file, final String category, final String id, 
			final String fullname, final String clientId) throws IllegalArgumentException, IOException, ParseException;
	
	List<CompanyCustomers> getSearchedCustomers(final String clientid, final String searchQuery);
	List<CompanySuppliers> getSearchedSuppliers(final String clientid, final String searchQuery);
	List<CompanyItems> getSearchedItems(final String clientid, final String searchQuery);
	
	List<CompanyItems> getSearchedItemsList(final String clientid,final String searchQuery);
	
	List<Client> getSearchedGroupName(final String id, final String searchQuery);
	
	CompanyInvoices getInvoiceConfigDetails(String clientid, String year, String invoiceType, String returnType);
	
	void deleteProduct(final String productId);
	void deleteCustomer(final String customerId);
	void deleteSupplier(final String supplierId);
	void deleteItem(final String itemId);
	void deleteBankDetail(final String banknameid);
	void deleteService(final String serviceId);
	void deleteInvoiceSubmission(final String invoiceId);
	
	void deleteBranch(final String branchid, final String clientid);
	void deleteCostcenter(final String branchid, final String clientid);
	void deleteVertical(final String verticalid, final String clientid);
	void deleteSubVertical(final String subVerticalid,final String verticalid, final String clientid);
	void deleteSubBranch(final String subBranchid,final String branchid, final String clientid);
	void deleteSubCostcenter(final String subBranchid,final String branchid, final String clientid);
	Page<CompanyCenters> getAllSubCenters(int start, int length,String fieldName, String order, String searchVal);
	
	Page<AdminUsers> getAllSubAdmins(int start, int length,String fieldName, String order, String searchVal);
	
	PrintConfiguration savePrintConfiguration(PrintConfiguration printform);
	PrintConfiguration getPrintConfig(String clientId);
	
	void updateClientPermissions(String clientId, String role, List<String> permissions);
	Map<String, Map<String, Set<Permission>>> getPermissionMap(String clientId, String roleid);
	
	CompanyCustomers getCompanyCustomerData(String clientid, String customerid);
	CompanySuppliers getCompanySupplierData(String clientid, String customerid);
	
	void delinkUser(final String userId,final String email, final String clientid);
	
	List<CompanyUser> getAllUsersByUserid(String userid);
	GroupDetails saveGroupDetails(GroupDetails groupdetails);
	ProfileLedger saveLegerDetails(ProfileLedger ledger);
	
	List<GroupDetails> getGroupDetails(String clientid);
	List<ProfileLedger> getLedgerDetails(String clientid);
	List<ProfileLedger> getLedgerDetails(String clientid,List<String> subgroupNames);
	void deleteGroup(final String id);
	void deleteSubGroup(final String subGroupid,final String Groupid, final String clientid);
	void deleteLedger(final String ledgerid);
	List<Groups> getGroupsList();
	//List<GroupDetails> getGroupDetailsByclientid(final String clientid);
	List<GroupDetails> addGroupInfo(final String clientid, final String groupname, final String headname, final String parentId,final String subgroupid,final String pathname);
	List<GroupDetails> updateGroupInfo(final String clientid, final String id, final String groupname, final String headname, final String parentId);
	
	EcommerceOpeartor saveOperator(EcommerceOpeartor operator);
	List<EcommerceOpeartor> getOperators(String clientid);
	List<EcommerceOpeartor> getSearchedOperators(final String clientid, final String searchQuery);
	void deleteOperator(final String operatorid);
	void deleteAdminUser(final String id);
	
	public AllGSTINTempData saveGstno(AllGSTINTempData gstno);
	List<AllGSTINTempData> getGstnum(final String userid);
	List<GSTINPublicData> getpublicgstnum(final String userid);
	void updateMultiGstnoExcelData(MultipartFile file, String id, String fullname) throws IllegalArgumentException, IOException;
	
	public GroupDetails groupnameExists(String clientid, String groupname);
	public GroupDetails subgroupnameExists(String clientid, String groupname);
	public ProfileLedger ledgerNameExists(String clientid, String ledgername);
	public String getAcknowledgementPermissions(String userid);
	public String getAcknowledgementRoles(String roleid);
	public boolean isStorageCredentialsEntered(String userId);
	public boolean isStorageCredentialsEnteredForClient(String clientId);
	Page<? extends CompanyCustomers> getCustomersDetails(Pageable pageable, String clientid, int month, int year,
			int start, int length, String searchVal);
	Page<? extends CompanySuppliers> getSupplierDetails(Pageable pageable, String clientid, int month, int year,
			int start, int length, String searchVal);
	//Page<? extends CompanyItems> getItemDetails(Pageable pageable, String clientid, int month, int year, int start,
		//	int length, String searchVal);
	Page<Client> getRemindersClients(String id, int start, int length, String searchVal);
	Page<CompanyCustomers> getRemindersCustomers(String id, String[] clientids, int start, int length, String searchVal);
	Page<CompanySuppliers> getRemindersSuppliers(String id, String[] clientids,int start, int length, String searchVal);
	public CompanySuppliers getCompanySuppliers(String id);
	public CompanyCustomers getCompanyCustomers(String id);
	
	Page<? extends MultiGSTNData> getmultiGstnDetails(Pageable pageable, String userid, int month, int year,
			int start, int length, String searchVal);
	List<DashboardRoles> getUserDashboardClientPermissions(String id, List<String> clientIds);
	void updateCompanyUsers(List<CompanyUser> companyUsers);
	String saveCustomFieldsData(String rParam,String userid, String clientid,CustomFields oldCustomData, CustomData customdetails);
	String editCustomFieldsData(String rParam, String userid, String clientid, CustomFields beforeCustomData,CustomData customdetails,String oldCustomName,String oldCustomtype);
	void deleteCustomFields(String id, String typeid, String clientid,String type);
	void saveDefaultStocks(String id, CompanyItems item);
	Page<? extends CompanyItems> getItemDetails(Pageable pageable, String clientid, String type, int month, int year, int start, int length, String searchVal);
	List<String> ledgerNamesForBankCash(String clientid);
	public List<AccountingHeads> getHeadDetails(String clientid);
	
}
