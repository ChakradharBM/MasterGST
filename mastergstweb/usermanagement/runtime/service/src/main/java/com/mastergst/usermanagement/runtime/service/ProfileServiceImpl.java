/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CUSTOMERS;
import static com.mastergst.core.common.MasterGSTConstants.ITEMS;
import static com.mastergst.core.common.MasterGSTConstants.PRODUCTS;
import static com.mastergst.core.common.MasterGSTConstants.SERVICES;
import static com.mastergst.core.common.MasterGSTConstants.SUPPLIERS;
import static com.mastergst.core.common.MasterGSTConstants.YES;
import static com.mastergst.core.common.MasterGSTConstants.NO;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.Groups;
import com.mastergst.configuration.service.GroupsRepository;
import com.mastergst.configuration.service.Role;
import com.mastergst.configuration.service.RoleRepository;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.accounting.dao.AccountingDao;
import com.mastergst.usermanagement.runtime.dao.ClientDao;
import com.mastergst.usermanagement.runtime.dao.CustomersDao;
import com.mastergst.usermanagement.runtime.dao.MultiGSTNDao;
import com.mastergst.usermanagement.runtime.dao.ProfileDao;
import com.mastergst.usermanagement.runtime.dao.ProfileItemDao;
import com.mastergst.usermanagement.runtime.dao.StorageCredentialsDao;
import com.mastergst.usermanagement.runtime.dao.SuppliersDao;
import com.mastergst.usermanagement.runtime.domain.AcknowledgementPermission;
import com.mastergst.usermanagement.runtime.domain.AdminUsers;
import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;
import com.mastergst.usermanagement.runtime.domain.Branch;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
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
import com.mastergst.usermanagement.runtime.domain.CostCenter;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DashboardRoles;
import com.mastergst.usermanagement.runtime.domain.DeletedCompanyUser;
import com.mastergst.usermanagement.runtime.domain.DeletedUsers;
import com.mastergst.usermanagement.runtime.domain.EcommerceOpeartor;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.domain.PartnerBankDetails;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.RolePermissionMapping;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.StorageCredentials;
import com.mastergst.usermanagement.runtime.domain.Subgroups;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.UserSequenceIdGenerator;
import com.mastergst.usermanagement.runtime.domain.Vertical;
import com.mastergst.usermanagement.runtime.repository.AccountingHeadsRepository;
import com.mastergst.usermanagement.runtime.repository.AllGSTnoTempRepository;
import com.mastergst.usermanagement.runtime.repository.ClientAddlInfoRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCentersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyGroupRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyInvoicesRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyItemsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyProductsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyRoleRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyServicesRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.repository.DeletedCompanyUserRepository;
import com.mastergst.usermanagement.runtime.repository.DeletedUserRepository;
import com.mastergst.usermanagement.runtime.repository.EcommerceOperatorRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.MultiGSTnRepository;
import com.mastergst.usermanagement.runtime.repository.PartnerBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.PrintConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.RolePermissionMappingRepository;
import com.mastergst.usermanagement.runtime.repository.StockAdjustmentRepository;
import com.mastergst.usermanagement.runtime.repository.UserSequenceIdGeneratorRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mastergst.usermanagement.runtime.domain.AccountingHeads;

/**
 * Service interface for Profile to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {
	
	private static final Logger logger = LogManager.getLogger(ProfileServiceImpl.class.getName());
	private static final String CLASSNAME = "ProfileServiceImpl::";
	@Autowired StockAdjustmentRepository stockAdjustmentRepository;
	@Autowired	CustomFieldsRepository customFieldsRepository;
	@Autowired	private EcommerceOperatorRepository ecommerceOperatorRepository;
	@Autowired	private LedgerRepository ledgerRepository;
	@Autowired	private GroupsRepository groupsRepository;
	@Autowired	GroupDetailsRepository groupDetailsRepository;
	@Autowired	CompanyProductsRepository companyProductsRepository;
	@Autowired	CompanyServicesRepository companyServicesRepository;
	@Autowired	CompanyItemsRepository companyItemsRepository;
	@Autowired	CompanyCustomersRepository companyCustomersRepository;
	@Autowired	AllGSTnoTempRepository allgstnoTempRepository;
	@Autowired	private CompanySuppliersRepository companySuppliersRepository;
	@Autowired	private CompanyCentersRepository companyCentersRepository;
	@Autowired	CompanyInvoicesRepository invoicesRepository;
	@Autowired	CompanyUserRepository companyUserRepository;
	@Autowired	CompanyRoleRepository companyRoleRepository;
	@Autowired	CompanyGroupRepository groupRepository;
	@Autowired	ClientUserMappingRepository clientUserMappingRepository;
	@Autowired	CompanyBankDetailsRepository companyBankDetailsRepository;
	@Autowired	PartnerBankDetailsRepository partnerBankDetailsRepository;
	@Autowired	ClientRepository clientRepository;
	@Autowired	GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired	RolePermissionMappingRepository permissionMappingRepository;
	@Autowired	RoleRepository roleRepository;
	@Autowired	ClientAddlInfoRepository clientAddlInfoRepository;
	@Autowired	MongoTemplate mongoTemplate;
	@Autowired	UserService userService;
	@Autowired	EmailService emailService;
	@Autowired	private SubscriptionService subscriptionService;
	@Autowired	ClientService clientService;
	@Autowired	ConfigService configService;
	@Autowired	IHubConsumerService iHubConsumerService;
	@Autowired	PrintConfigurationRepository printConfigurationRepository;
	@Autowired	UserRepository userRepository;
	@Autowired	DeletedUserRepository deletedUserRepository;
	@Autowired	DeletedCompanyUserRepository deletedCompanyUserRepository;
	@Autowired	private UserSequenceIdGeneratorRepository userSequenceIdGeneratorRepository;
	@Autowired	private InventoryService inventoryService;
	@Autowired	private StorageCredentialsDao storageCredentialsDao;
	@Autowired	ProfileDao profileDao;
	@Autowired	ProfileItemDao profileItemDao;
	@Autowired	AccountingDao accountingDao;
	@Autowired	private ClientDao clientDao;
	@Autowired	private CustomersDao customersDao;
	@Autowired	private SuppliersDao suppliersDao;
	@Autowired	private MultiGSTNDao multiGSTNDao;
	@Autowired	private MultiGSTnRepository multiGSTnRepository;
	@Autowired	GridFsOperations gridOperations;
	@Autowired	private AccountingHeadsRepository accountingHeadsRepository;
	
	@Override
	@Transactional
	public List<CompanyUser> getGlobalUsers(final String userid) {
		logger.debug(CLASSNAME + "getGlobalUsers : Begin");
		List<CompanyUser> users = companyUserRepository.findByUseridAndIsglobal(userid, "true");
		if(isNotEmpty(users)) {
			for(CompanyUser companyUser : users) {
				List<String> clientIds = Lists.newArrayList();
				User user=userService.findByEmail(companyUser.getEmail());
				if(isNotEmpty(user) && isNotEmpty(user.getNumberofclients()) && isNotEmpty(user.getParentid())) {
					List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(user.getId().toString());
					if(isNotEmpty(clientUserMappings)) {
						for(ClientUserMapping clientUserMapping : clientUserMappings) {
							clientIds.add(clientUserMapping.getClientid());
						}
					}
					/* else if(isNotEmpty(user) && isNotEmpty(user.getIsglobal()) && user.getIsglobal().equals("true") && isNotEmpty(user.getParentid())) {
						clientUserMappings = clientUserMappingRepository.findByUserid(user.getParentid());
						if(isNotEmpty(clientUserMappings)) {
							for(ClientUserMapping clientUserMapping : clientUserMappings) {
								clientIds.add(clientUserMapping.getClientid());
							}
						}
					} */
				}
				/*
				 * if(isNotEmpty(user.isAccessAcknowledgement()) &&
				 * user.isAccessAcknowledgement()) {
				 * companyUser.setAccessAcknowledgement(user.isAccessAcknowledgement()); }else {
				 * companyUser.setAccessAcknowledgement(false);
				 * 
				 * }
				 */
				companyUser.setAddedclients(clientIds.size());
			}
		}
		return users;
	}
	
	@Override
	@Transactional
	public List<CompanyUser> getUsers(final String userid) {
		logger.debug(CLASSNAME + "getUsers : Begin");
		return companyUserRepository.findByUserid(userid);
	}
	
	@Override
	@Transactional
	public List<CompanyUser> getUsers(final String userid, final String clientid) {
		logger.debug(CLASSNAME + "getUsers : Begin");
		List<CompanyUser> users = Lists.newArrayList();
		if(isNotEmpty(clientid)) {
			List<String> clientIds=Lists.newArrayList(clientid);
			List<CompanyUser> clientUsers = companyUserRepository.findByUseridAndCompanyIn(userid, clientIds);
			if(isNotEmpty(clientUsers)) {
				for(CompanyUser clientUser : clientUsers) {
					if(isEmpty(users)){
						users.add(clientUser);
					}else{
						if(!users.contains(clientUser)) {
							users.add(clientUser);
						}
					}
				}
			}
		}
		if(isNotEmpty(users)) {
			for(CompanyUser companyUser : users) {
				List<String> clientIds = Lists.newArrayList();
				User user=userService.findByEmail(companyUser.getEmail());
				if(isNotEmpty(user) && isNotEmpty(user.getNumberofclients()) && isNotEmpty(user.getParentid())) {
					if(isNotEmpty(user.getId()) && isNotEmpty(user.getId().toString())){
						List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(user.getId().toString());
						if(isNotEmpty(clientUserMappings)) {
							for(ClientUserMapping clientUserMapping : clientUserMappings) {
								clientIds.add(clientUserMapping.getClientid());
							}
						} else if(isNotEmpty(user) && isNotEmpty(user.getIsglobal()) && user.getIsglobal().equals("true") && isNotEmpty(user.getParentid())) {
							clientUserMappings = clientUserMappingRepository.findByUserid(user.getParentid());
							if(isNotEmpty(clientUserMappings)) {
								for(ClientUserMapping clientUserMapping : clientUserMappings) {
									clientIds.add(clientUserMapping.getClientid());
								}
							}
						}
					}
				}
				companyUser.setAddedclients(clientIds.size());
			}
		}
		return users;
	}
	
	
	@Override
	@Transactional
	public List<CompanyUser> getUsersByClientId(final String clientid) {
		logger.debug(CLASSNAME + "getUsers : Begin");
		List<CompanyUser> users = Lists.newArrayList();
		if(isNotEmpty(clientid)) {
			List<String> clientIds=Lists.newArrayList(clientid);
			List<CompanyUser> clientUsers = companyUserRepository.findByCompanyIn(clientIds);
			if(isNotEmpty(clientUsers)) {
				for(CompanyUser clientUser : clientUsers) {
					if(isEmpty(users)){
						users.add(clientUser);
					}else{
						if(!users.contains(clientUser)) {
							users.add(clientUser);
						}
					}
				}
			}
		}
		if(isNotEmpty(users)) {
			for(CompanyUser companyUser : users) {
				List<String> clientIds = Lists.newArrayList();
				User user=userService.findByEmail(companyUser.getEmail());
				if(isNotEmpty(user) && isNotEmpty(user.getNumberofclients()) && isNotEmpty(user.getParentid())) {
					if(isNotEmpty(user.getId()) && isNotEmpty(user.getId().toString())){
						List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(user.getId().toString());
						if(isNotEmpty(clientUserMappings)) {
							for(ClientUserMapping clientUserMapping : clientUserMappings) {
								clientIds.add(clientUserMapping.getClientid());
							}
						} else if(isNotEmpty(user) && isNotEmpty(user.getIsglobal()) && user.getIsglobal().equals("true") && isNotEmpty(user.getParentid())) {
							clientUserMappings = clientUserMappingRepository.findByUserid(user.getParentid());
							if(isNotEmpty(clientUserMappings)) {
								for(ClientUserMapping clientUserMapping : clientUserMappings) {
									clientIds.add(clientUserMapping.getClientid());
								}
							}
						}
					}
				}
				companyUser.setAddedclients(clientIds.size());
			}
		}
		return users;
	}
	
	@Override
	@Transactional
	public boolean checkEmailExists(final String email) {
		logger.debug(CLASSNAME + "checkEmailExists : Begin");
		User user = userService.findByEmail(email);
		if(isNotEmpty(user)) {
			return true;
		}
		CompanyUser companyUser = getCompanyUser(email);
		if(isNotEmpty(companyUser)) {
			return true;
		}
		logger.debug(CLASSNAME + "checkEmailExists : End");
		return false;
	}
	
	@Override
	@Transactional(readOnly = true)
	//@Cacheable(value="compUserCache", key="#email")
	public CompanyUser getCompanyUser(final String email) {
		logger.debug(CLASSNAME + "getCompanyUser : Begin");
		return companyUserRepository.findByEmail(email);
	}
	
	@Override
	@Transactional(readOnly = true)
	public CompanyUser getCompanyUser(final String email, final String clientid) {
		logger.debug(CLASSNAME + "getCompanyUser : Begin");
		List<String> clientIds=Lists.newArrayList(clientid);
		CompanyUser cUser = companyUserRepository.findByEmailAndCompanyIn(email, clientIds);
		//CompanyUser cUser = getCompanyUser(email);
		
		if(isNotEmpty(cUser) && isNotEmpty(cUser.getId()) && isNotEmpty(cUser.getRole())) {
			RolePermissionMapping userRoleMapping = permissionMappingRepository.findByClientidAndUseridAndRoleIsNull(clientid, cUser.getId().toString());
			if(isNotEmpty(userRoleMapping) && isNotEmpty(userRoleMapping.getRoleid())) {
				cUser.setRole(userRoleMapping.getRoleid());
			}
		}
		return cUser;
	}
	
	@Override
	@Transactional
	//@CacheEvict(value="compUserCache", key="#user.email")
	public CompanyUser saveUser(CompanyUser user, final String clientid) {
		logger.debug(CLASSNAME + "saveUser : Begin {}", user);
		/*if(isNotEmpty(user.getPermissions())) {
			Map<String, List<Permission>> permissions = Maps.newHashMap();
			for(String feature : user.getPermissions().keySet()) {
				List<Permission> permissionList = Lists.newArrayList();
				for(Permission permission : user.getPermissions().get(feature)) {
					if(isNotEmpty(permission.getName())) {
						permission.setStatus(YES);
						permissionList.add(permission);
					}
				}
				permissions.put(feature, permissionList);
			}
			user.setPermissions(permissions);
		}*/
		CompanyUser dbUser = companyUserRepository.save(user);
		if(isNotEmpty(user.getRole()) && isNotEmpty(clientid) && isNotEmpty(dbUser)) {
			RolePermissionMapping userRoleMapping = permissionMappingRepository.findByClientidAndUseridAndRoleIsNull(clientid, dbUser.getId().toString());
			if(isEmpty(userRoleMapping)) {
				userRoleMapping = new RolePermissionMapping();
				userRoleMapping.setClientid(clientid);
				userRoleMapping.setUserid(dbUser.getId().toString());
			}
			userRoleMapping.setRoleid(dbUser.getRole());
			permissionMappingRepository.save(userRoleMapping);
		}else if(isEmpty(user.getRole()) && isNotEmpty(clientid) && isNotEmpty(dbUser)) {
			RolePermissionMapping userRoleMapping = permissionMappingRepository.findByClientidAndUseridAndRoleIsNull(clientid, dbUser.getId().toString());
			if(isNotEmpty(userRoleMapping)) {
				permissionMappingRepository.delete(userRoleMapping.getId().toString());
			}
		}
		return dbUser;
	}
	
	@Override
	@Transactional
	public List<CompanyRole> getRoles(final String userid) {
		logger.debug(CLASSNAME + "getRoles : Begin");
		return companyRoleRepository.findByUserid(userid);
	}
	
	@Override
	@Transactional
	public List<CompanyRole> getRoles(final String userid, final String clientid) {
		logger.debug(CLASSNAME + "getRoles : Begin");
		if(NullUtil.isNotEmpty(clientid)) {
			return companyRoleRepository.findByUseridAndClientid(userid, clientid);
		} else {
			return companyRoleRepository.findByUseridAndClientidIsNull(userid);
		}
	}
	
	@Override
	@Transactional
	public CompanyRole saveRole(CompanyRole role) {
		logger.debug(CLASSNAME + "saveRole : Begin");
		if(isNotEmpty(role.getPermissions())) {
			Map<String, List<Permission>> permissions = Maps.newHashMap();
			for(String feature : role.getPermissions().keySet()) {
				if(!feature.contains("-")) {
					List<Permission> permissionList = Lists.newArrayList();
					List<String> permissionName = Lists.newArrayList();
					for(Permission permission : role.getPermissions().get(feature)) {
						if(isNotEmpty(permission.getName())) {
							if(permission.getName().contains(MasterGSTConstants.COMMA)) {
								for(String strPerm : permission.getName().split(MasterGSTConstants.COMMA)) {
									Permission tPerm = new Permission();
									tPerm.setName(strPerm.trim());
									tPerm.setStatus(YES);
									permissionList.add(tPerm);
									permissionName.add(strPerm.trim());
								}
							} else {
								permission.setStatus(YES);
								permissionList.add(permission);
								permissionName.add(permission.getName());
							}
						}
					}
					List<Role> rolesBycategory = roleRepository.findByCategory(feature);
					for(Role categoryRole : rolesBycategory) {
						if(!permissionName.contains(categoryRole.getName())) {
							Permission tPerm = new Permission();
							tPerm.setName(categoryRole.getName());
							tPerm.setStatus(NO);
							permissionList.add(tPerm);
						}
					}
					permissions.put(feature, permissionList);
				} else {
					List<Permission> permissionList = Lists.newArrayList();
					String[] roleArr = feature.split("-");
					for(Permission permission : role.getPermissions().get(feature)) {
						if(isNotEmpty(permission.getName())) {
							if(roleArr.length < 3) {
								Permission tPerm = new Permission();
								tPerm.setName(roleArr[1]);
								tPerm.setStatus(permission.getName()+"-"+YES);
								permissionList.add(tPerm);
							} else {
								permission.setName(roleArr[1]);
								permission.setStatus(roleArr[2]+"-"+YES);
								permissionList.add(permission);
							}
						}
					}
					if(permissions.containsKey(roleArr[0])) {
						permissions.get(roleArr[0]).addAll(permissionList);
					} else {
						permissions.put(roleArr[0], permissionList);
					}
				}
			}
			List<String> categoryList = Arrays.asList("Invoices","Settings");
			for(String category : categoryList) {
				List<Role> rolesBycategory = roleRepository.findByCategory(category);
				List<Permission> roles = permissions.get(category);
				List<String> permissionName = Lists.newArrayList();
				if(isNotEmpty(roles)) {
					for(Permission permission : roles) {
						permissionName.add(permission.getName()+"-"+permission.getStatus());
					}
					for(Role categoryRole : rolesBycategory) {
						Set<String> privileges = categoryRole.getPrivileges();
						for(String privilege : privileges) {
							if(!permissionName.contains(categoryRole.getName()+"-"+privilege+"-"+YES)) {
								Permission tPerm = new Permission();
								tPerm.setName(categoryRole.getName());
								tPerm.setStatus(privilege+"-"+NO);
								roles.add(tPerm);
							}
						}
					}
				}else {
					roles = Lists.newArrayList();
					for(Role categoryRole : rolesBycategory) {
						Set<String> privileges = categoryRole.getPrivileges();
						for(String privilege : privileges) {
								Permission tPerm = new Permission();
								tPerm.setName(categoryRole.getName());
								tPerm.setStatus(privilege+"-"+NO);
								roles.add(tPerm);
						}
					}
				}
			}
			role.setPermissions(permissions);
		}
		return companyRoleRepository.save(role);
	}
	
	@Override
	@Transactional
	public List<CompanyGroup> getGroups(final String userid, final String clientid) {
		logger.debug(CLASSNAME + "getGroups : Begin");
		if(NullUtil.isNotEmpty(clientid)) {
			return groupRepository.findByUseridAndClientid(userid, clientid);
		} else {
			return groupRepository.findByUseridAndClientidIsNull(userid);
		}
	}
	
	@Override
	@Transactional
	public CompanyGroup saveGroup(CompanyGroup group) {
		logger.debug(CLASSNAME + "saveGroup : Begin");
		if(isNotEmpty(group.getPermissions())) {
			Map<String, List<Permission>> permissions = Maps.newHashMap();
			for(String feature : group.getPermissions().keySet()) {
				List<Permission> permissionList = Lists.newArrayList();
				for(Permission permission : group.getPermissions().get(feature)) {
					if(isNotEmpty(permission.getName())) {
						permission.setStatus(YES);
						permissionList.add(permission);
					}
				}
				permissions.put(feature, permissionList);
			}
			group.setPermissions(permissions);
		}
		return groupRepository.save(group);
	}
	
	@Override
	@Transactional
	public List<CompanyInvoices> getInvoiceSubmission(final String userid, final String clientid) {
		logger.debug(CLASSNAME + "getInvoiceSubmission : Begin");
		return invoicesRepository.findByUseridAndClientid(userid, clientid);
	}
	
	@Override
	@Transactional
	public List<CompanyInvoices> getInvoiceSubmissionDetails(final String userid, final String clientid, final String year) {
		logger.debug(CLASSNAME + "getInvoiceSubmissionDetails : Begin");
		return invoicesRepository.findByUseridAndClientidAndYear(userid,clientid, year);
	}
	
	@Override
	@Transactional
	public List<CompanyInvoices> getUserInvoiceSubmissionDetails(final String clientid, final String year) {
		logger.debug(CLASSNAME + "getInvoiceSubmissionDetails : Begin");
		return invoicesRepository.findByClientidAndYear(clientid, year);
	}
	
	
	@Override
	@Transactional(readOnly=true)
	public CompanyInvoices getInvoiceSubmissionDetails(final String userid, final String clientid, final String year, final String invoiceType) {
		logger.debug(CLASSNAME + "getInvoiceSubmissionDetails : Begin");
		CompanyInvoices invoiceData = null;
		if(isNotEmpty(invoiceType)) {
			invoiceData = invoicesRepository.findByUseridAndClientidAndYearAndInvoiceType(userid, clientid, year, invoiceType); 
		}
		if(isEmpty(invoiceData) || isEmpty(invoiceData.getUserid())) {
			invoiceData = invoicesRepository.findByUseridAndClientidAndYearAndInvoiceType(userid, clientid, year, "ALL");
		}
		return invoiceData;
	}
	
	@Override
	@Transactional(readOnly=true)
	public CompanyInvoices getUserInvoiceSubmissionDetails(final String clientid, final String year, final String invoiceType) {
		logger.debug(CLASSNAME + "getInvoiceSubmissionDetails : Begin");
		CompanyInvoices invoiceData = null;
		if(isNotEmpty(invoiceType)) {
			invoiceData = invoicesRepository.findByClientidAndYearAndInvoiceType(clientid, year, invoiceType); 
		}
		if(isEmpty(invoiceData) || isEmpty(invoiceData.getUserid())) {
			invoiceData = invoicesRepository.findByClientidAndYearAndInvoiceType(clientid, year, "ALL");
		}
		return invoiceData;
	}
	
	@Override
	@Transactional
	public CompanyInvoices saveInvoiceSubmission(CompanyInvoices invoice) {
		logger.debug(CLASSNAME + "saveInvoiceSubmission : Begin {}", invoice);
		return invoicesRepository.save(invoice);
	}
	
	@Override
	@Transactional
	public void deleteInvoiceSubmission(final String invoiceId) {
		logger.debug(CLASSNAME + "deleteInvoiceSubmission : Begin {}", invoiceId);
		CompanyInvoices invoiceConfigureDetails = invoicesRepository.findById(invoiceId);
		clientAddlInfoRepository.deleteByClientIdAndInvoiceTypeAndReturnTypeAndFinancialYear(invoiceConfigureDetails.getClientid(), invoiceConfigureDetails.getInvoiceType(),MasterGSTConstants.GSTR1,  invoiceConfigureDetails.getYear());
		invoicesRepository.delete(invoiceId);
	}
	
	@Override
	@Transactional
	public List<CompanyProducts> getProducts(String clientid) {
		logger.debug(CLASSNAME + "getProducts : Begin");
		return companyProductsRepository.findByClientid(clientid);
	}

	@Override
	@Transactional
	public CompanyProducts saveProduct(CompanyProducts product) {
		logger.debug(CLASSNAME + "saveProduct : Begin {}", product);
		return companyProductsRepository.save(product);
	}
	
	@Override
	@Transactional
	public List<CompanyCustomers> getCustomers(String clientid) {
		logger.debug(CLASSNAME + "getCustomers : Begin");
		return companyCustomersRepository.findByClientid(clientid);
	}

	@Override
	@Transactional
	public CompanyCustomers saveCustomer(CompanyCustomers customer) {
		logger.debug(CLASSNAME + "saveCustomer : Begin {}", customer);
		if(isNotEmpty(customer.getGstnnumber())) {
			customer.setGstnnumber(customer.getGstnnumber().toUpperCase());
			GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(customer.getGstnnumber().toUpperCase().trim());
			if(isNotEmpty(gstndata)) {
				if(isNotEmpty(gstndata.getDty())) {
					customer.setDealerType(gstndata.getDty());
				}
			}else {
				Response response = iHubConsumerService.publicSearch(customer.getGstnnumber().toUpperCase().trim());
				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					ResponseData data = response.getData(); 
					if(isNotEmpty(data.getDty())) {
						customer.setDealerType(data.getDty());
					}
					CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
					future.thenAcceptAsync(responseObj -> {
						try {
							gstinPublicDataRepository.deleteByGstinAndUseridIsNull(customer.getGstnnumber().toUpperCase().trim());
						} catch(Exception e) {}
						GSTINPublicData publicData = new GSTINPublicData();
						BeanUtils.copyProperties(responseObj.getData(), publicData);
						publicData.setCreatedDate(new Date());
						if(isNotEmpty(customer.getUserid())) {
							publicData.setUserid(customer.getUserid());
						}
						try {
							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gstinPublicDataRepository.save(publicData);
					});
				}
			}
		}
		return companyCustomersRepository.save(customer);
	}
	
	@Override
	@Transactional
	public List<CompanyCenters> getCenters(String userid) {
		logger.debug(CLASSNAME + "getCenters : Begin");
		return companyCentersRepository.findByUserid(userid);
	}

	@Override
	@Transactional
	public CompanyCenters saveCenter(CompanyCenters center, final String clientid) {
		logger.debug(CLASSNAME + "saveCenter : Begin {}", center);
		if(isNotEmpty(center.getPassword())) {
			try {
				center.setPassword(Base64.getEncoder()
					.encodeToString(center.getPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
			} catch (UnsupportedEncodingException e) {
				logger.error(CLASSNAME + "saveCenter : ERROR", e);
			}
		}
		CompanyCenters centers = null;
		if(isNotEmpty(center.getId())){
			centers = companyCentersRepository.findOne(center.getId().toString());
		}
		center = companyCentersRepository.save(center);
		if(isNotEmpty(center.getPassword())) {
			User superUser = null;
			User appUser = null;
			if(NullUtil.isNotEmpty(centers)){
				appUser = userService.findByEmail(centers.getEmail());
			}else{
				appUser = userService.findByEmail(center.getEmail());
			}
			if(isEmpty(appUser)) {
				appUser = new User();
					appUser.setCreatedDate(new Date());
			}
			appUser.setFullname(center.getContactperson());
			appUser.setPassword(center.getPassword());
			appUser.setEmail(center.getEmail());
			appUser.setMobilenumber(center.getMobilenumber());
			appUser.setParentid(center.getUserid());
			if(isNotEmpty(center.getUserid())) {
				superUser = userService.findById(center.getUserid());
			}
			if(isNotEmpty(superUser)) {
				appUser.setType(superUser.getType());
			}
			appUser = userService.createUser(appUser);
			if(isNotEmpty(center.getUserid())) {
				SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(center.getUserid());
				if(isNotEmpty(subscriptionDetails)) {
					subscriptionDetails.setId(new ObjectId());
					subscriptionDetails.setUserid(appUser.getId().toString());
					subscriptionService.updateSubscriptionData(subscriptionDetails);
				}
			}
			CompanyUser companyUser = new CompanyUser();
			companyUser.setName(center.getContactperson());
			companyUser.setPassword(center.getPassword());
			companyUser.setEmail(center.getEmail());
			companyUser.setMobile(center.getMobilenumber());
			companyUser.setUserid(center.getUserid());
			companyUser.setIsglobal("true");
			companyUser.setNumberofclients("0");
			List<String> centerList=Lists.newArrayList();
			centerList.add(center.getId().toString());
			companyUser.setCenters(centerList);
			saveUser(companyUser, clientid);
			byte[] base64decodedBytes = Base64.getDecoder().decode(appUser.getPassword());
	         String password = null;
	         try {
				password = new String(base64decodedBytes, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			try {
				emailService.sendEnrollEmail(center.getEmail(),
						VmUtil.velocityTemplate("email_enrollment.vm", appUser.getFullname(), appUser.getEmail(),
								password),
						MasterGSTConstants.CREATEUSER_EMAILSUBJECT);
			} catch (MasterGSTException e) {
				logger.error(CLASSNAME + "saveCenter : Email ERROR", e);
			}
		}
		logger.debug(CLASSNAME + "saveCenter : End");
		return center;
	}
	
	@Override
	@Transactional
	public List<CompanySuppliers> getSuppliers(String clientid) {
		logger.debug(CLASSNAME + "getSuppliers : Begin");
		return companySuppliersRepository.findByClientid(clientid);
	}

	@Override
	@Transactional
	public CompanySuppliers saveSupplier(CompanySuppliers supplier) {
		logger.debug(CLASSNAME + "saveSupplier : Begin {}", supplier);
		if(isNotEmpty(supplier.getGstnnumber())) {
			supplier.setGstnnumber(supplier.getGstnnumber().toUpperCase().trim());
			GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(supplier.getGstnnumber().toUpperCase().trim());
			if(isNotEmpty(gstndata)) {
				if(isNotEmpty(gstndata.getDty())) {
					supplier.setDealerType(gstndata.getDty());
				}
			}else {
				Response response = iHubConsumerService.publicSearch(supplier.getGstnnumber().toUpperCase().trim());
				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
						&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					ResponseData data = response.getData(); 
					if(isNotEmpty(data.getDty())) {
						supplier.setDealerType(data.getDty());
					}
					CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
					future.thenAcceptAsync(responseObj -> {
						try {
							gstinPublicDataRepository.deleteByGstinAndUseridIsNull(supplier.getGstnnumber().toUpperCase().trim());
						} catch(Exception e) {}
						GSTINPublicData publicData = new GSTINPublicData();
						BeanUtils.copyProperties(responseObj.getData(), publicData);
						publicData.setCreatedDate(new Date());
						if(isNotEmpty(supplier.getUserid())) {
							publicData.setUserid(supplier.getUserid());
						}
						try {
							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						gstinPublicDataRepository.save(publicData);
					});
				}
			}
		}
		return companySuppliersRepository.save(supplier);
	}

	@Override
	@Transactional
	public List<CompanyServices> getServices(String clientid) {
		logger.debug(CLASSNAME + "getServices : Begin");
		return companyServicesRepository.findByClientid(clientid);
	}

	@Override
	@Transactional
	public CompanyServices saveService(CompanyServices service) {
		logger.debug(CLASSNAME + "saveService : Begin {}", service);
		return companyServicesRepository.save(service);
	}

	@Override
	@Transactional
	public List<CompanyItems> getItems(String clientid) {
		logger.debug(CLASSNAME + "getItems : Begin");
		return companyItemsRepository.findByClientid(clientid);
	}

	@Override
	@Transactional
	public CompanyItems saveItem(CompanyItems item) {
		logger.debug(CLASSNAME + "saveItem : Begin {}", item);
		Double purchasePrice = 0d;Double taxRate=0d;
		Double purchaseTax = 0d;
		if(isEmpty(item.getCurrentStock())) {
			if(isNotEmpty(item.getOpeningStock())) {
				item.setCurrentStock(item.getOpeningStock());
			}
		}
		
		if(isNotEmpty(item.getPurchaseGstType())) {
			if(isNotEmpty(item.getPurchasePrice())) {
				purchasePrice = item.getPurchasePrice();
			}
			if(isNotEmpty(item.getTaxrate())) {
				taxRate = Double.parseDouble(item.getTaxrate());
			}
			if(item.getPurchaseGstType().equals("Without GST")) {
				purchaseTax = ((purchasePrice*taxRate)/100);
			}else if(item.getPurchaseGstType().equals("With GST")) {
				purchaseTax = (purchasePrice/(100+taxRate)*(taxRate));
			}
		}
		if(isNotEmpty(item.getSalePrice())) {
			item.setSalePriceTxt(item.getSalePrice().toString());
		}
		if(isNotEmpty(item.getPurchasePrice())) {
			item.setPurchasePriceTxt(item.getPurchasePrice().toString());
		}
		item.setPurchaseTaxAmt(purchaseTax);
		item.setPurchaseTotAmt(purchasePrice + purchaseTax);
		Date dt = null;
		dt = (Date)item.getAsOnDate();
		if(isNotEmpty(dt)) {
			int month = dt.getMonth();
			int year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			
			item.setMthCd(""+month);
			item.setYrCd(""+yearCode);
			item.setQrtCd(""+quarter);
			item.setSftr(1);
		}
		return companyItemsRepository.save(item);
	}
	
	@Override
	@Transactional
	//@CacheEvict(value="compUserCache", key="#user.email")
	public void createUserForClient(CompanyUser user, final String userid) {
		logger.debug(CLASSNAME + "createUserForClient : Begin {}", user);
		try {
			User superUser = null;
			User appUser = userService.findByEmail(user.getEmail());
			if(isEmpty(appUser)) {
				appUser = new User();
				appUser.setCreatedDate(new Date());
				appUser.setOtpVerified("false");
				appUser.setCategory(MasterGSTConstants.USER_CLIENT);
				appUser.setPassword(user.getPassword());
				UserSequenceIdGenerator sequencegenerator=userSequenceIdGeneratorRepository.findBySequenceIdName(MasterGSTConstants.SEQUENCE_GENERATOR);
			if(isNotEmpty(sequencegenerator) && isNotEmpty(sequencegenerator.getUserSequenceId())) {
				appUser.setUserSequenceid(sequencegenerator.getUserSequenceId());
				sequencegenerator.setUserSequenceId(sequencegenerator.getUserSequenceId()+1);
				userSequenceIdGeneratorRepository.save(sequencegenerator);
			}
			}else{
				appUser.setPassword(appUser.getPassword());
			}
			appUser.setFullname(user.getName());
			
			appUser.setEmail(user.getEmail());
			appUser.setMobilenumber(user.getMobile());
			if(isNotEmpty(userid)) {
				superUser = userService.findById(userid);
				
				if(NullUtil.isNotEmpty(superUser)) {
					appUser.setAccessEinvoice(superUser.isAccessEinvoice());
					if(isNotEmpty(superUser.getDisable())) {
						appUser.setDisable(superUser.getDisable()); 
					}else {
						appUser.setDisable("false");
					}
				}
				appUser.setParentid(userid);
				if(isNotEmpty(superUser.getDisable())) {
					appUser.setDisable(superUser.getDisable()); 
				}
			}
			if(isNotEmpty(superUser)) {
				appUser.setType(superUser.getType());
			}
			if(isNotEmpty(user.getIsglobal())) {
				appUser.setIsglobal(user.getIsglobal());
			} else {
				appUser.setIsglobal("false");
			}
			/*
			 * if(isNotEmpty(user.getDisable())) { appUser.setDisable(user.getDisable()); }
			 * else { appUser.setDisable("false"); }
			 */
			if(isNotEmpty(user.getNumberofclients())) {
				appUser.setNumberofclients(user.getNumberofclients());
			}
			
			appUser = userService.createUser(appUser);
			List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(appUser.getId().toString());
			List<String> clientIds = Lists.newArrayList();
			if(isNotEmpty(clientUserMappings)) {
				List<ClientUserMapping> filterMappings = Lists.newArrayList();
				for(ClientUserMapping clientUserMapping : clientUserMappings) {
					if(isNotEmpty(clientUserMapping.getCreatedBy()) 
							&& isNotEmpty(clientUserMapping.getUserid())
							&& clientUserMapping.getUserid().equals(clientUserMapping.getCreatedBy())) {
						/*filterMappings.add(clientUserMapping);*/
					} else {
						clientIds.add(clientUserMapping.getClientid());
					}
				}
				clientUserMappingRepository.delete(filterMappings);
			}
			if(isEmpty(user.getIsglobal()) || !user.getIsglobal().equals("true")) {
				if(isNotEmpty(user.getClientid())) {
					ClientUserMapping clientUserMapping = clientUserMappingRepository.findByUseridAndClientid(appUser.getId().toString(), user.getClientid());
					if(isEmpty(clientUserMapping)) {
						clientUserMapping = new ClientUserMapping();
						clientUserMapping.setUserid(appUser.getId().toString());
						clientUserMapping.setClientid(user.getClientid());
						clientUserMapping.setAddSubUser(user.getAddsubuser());
						clientUserMapping.setRole(user.getRole());
						clientUserMappingRepository.save(clientUserMapping);
						clientIds.add(user.getClientid());
					}else {
						clientUserMapping.setAddSubUser(user.getAddsubuser());
						clientUserMapping.setRole(user.getRole());
						clientUserMappingRepository.save(clientUserMapping);
					}
				}
			}
			if(isNotEmpty(user.getCompany())) {
				for(String cId : user.getCompany()) {
					if(!clientIds.contains(cId)) {
						ClientUserMapping clientUserMapping = new ClientUserMapping();
						clientUserMapping.setUserid(appUser.getId().toString());
						clientUserMapping.setClientid(cId);
						clientUserMapping.setAddSubUser(user.getAddsubuser());
						clientUserMapping.setRole(user.getRole());
						clientUserMappingRepository.save(clientUserMapping);
					}
				}
			}
			byte[] base64decodedBytes = Base64.getDecoder().decode(user.getPassword());
			String password = null;
			try {
				password = new String(base64decodedBytes,MasterGSTConstants.PASSWORD_ENCODE_FORMAT);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String userguide = "<a href=http://mastergst.com/blog/how-to-file-gst-returns-using-mastergst/>Click here for Product User guide:</a>";
			emailService.sendEnrollEmail(user.getEmail(),
					VmUtil.velocityTemplate("email_enrollment.vm", userguide,user.getFullname(), user.getEmail(),
							password),
					MasterGSTConstants.CREATEUSER_EMAILSUBJECT);

		} catch (MasterGSTException e) {
			logger.error(CLASSNAME + "createUserForClient : ERROR", e);
		}
		logger.debug(CLASSNAME + "createUserForClient : End");
	}
	
	private String userGuide(String type) throws Exception {
		String userGuide = "";
		switch (type) {
		case MasterGSTConstants.PARTNER:
			userGuide = "<a href=http://www.mastergst.com/blog/user-guide-to-business-partners/>Click here User Guide for Business Partner:</a>";
			break;
		default:
			userGuide = "<a href=http://mastergst.com/blog/how-to-file-gst-returns-using-mastergst/>Click here for Product User guide:</a>";
			break;
		}
		return userGuide;

	}

	
	@Override
	@Transactional
	public void updateExcelData(final MultipartFile file, final String category, final String id, 
			final String fullname, final String clientId) throws IllegalArgumentException, IOException, ParseException {
		logger.debug(CLASSNAME + "updateExcelData : Begin");
		if(!file.isEmpty()) {
			ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
	        Workbook workbook;
	        if (file.getOriginalFilename().endsWith("xls")) {
                workbook = new HSSFWorkbook(bis);
            } else if (file.getOriginalFilename().endsWith("xlsx")) {
                workbook = new XSSFWorkbook(bis);
            } else {
                throw new IllegalArgumentException("Received file does not have a standard excel extension.");
            }
	        
	        if(isNotEmpty(workbook) && workbook.getNumberOfSheets() > 0) {
	        	int lastRowIndex=workbook.getSheetAt(0).getLastRowNum();
        		if(lastRowIndex > 0) {
            		int startRowIndex=workbook.getSheetAt(0).getFirstRowNum();
            		Row firstRow = workbook.getSheetAt(0).getRow(startRowIndex);
            		int columns=firstRow.getLastCellNum()-firstRow.getFirstCellNum();
            		logger.debug(CLASSNAME + "updateExcelData : No of Columns {}", columns);
            		if(isNotEmpty(category) && category.equals(PRODUCTS)) {
            			if(columns >= 4) {
            				List<CompanyProducts> products = Lists.newArrayList();
                			for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    			Row row = workbook.getSheetAt(0).getRow(rowIndex);
                				CompanyProducts product = new CompanyProducts();
                    			product.setUserid(id);
                    			product.setFullname(fullname);
                    			product.setClientid(clientId);
                    			if(row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING) {
                    				product.setSerialno(Long.parseLong(row.getCell(row.getFirstCellNum()).getStringCellValue()));
                    			} else {
                    				product.setSerialno(new Double(row.getCell(row.getFirstCellNum()).getNumericCellValue()).longValue());
                    			}
                    			product.setName(row.getCell(row.getFirstCellNum()+1).getStringCellValue());
                    			product.setDescription(row.getCell(row.getFirstCellNum()+2).getStringCellValue());
                    			if(row.getCell(row.getFirstCellNum()+3).getCellType() == Cell.CELL_TYPE_STRING) {
	                    			product.setHsn(row.getCell(row.getFirstCellNum()+3).getStringCellValue());
                    			} else {
                    				product.setHsn(new Double(row.getCell(row.getFirstCellNum()+3).getNumericCellValue()).longValue()+"");
                    			}
                    			product.setUnit(row.getCell(row.getFirstCellNum()+4).getStringCellValue());
                    			if(row.getCell(row.getFirstCellNum()+5).getCellType() == Cell.CELL_TYPE_STRING) {
	                    			product.setCost(row.getCell(row.getFirstCellNum()+5).getStringCellValue());
                    			} else {
                    				product.setCost(new Double(row.getCell(row.getFirstCellNum()+5).getNumericCellValue()).longValue()+"");
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+6))){
                    				product.setTaxrate(new Double(row.getCell(row.getFirstCellNum()+6).getNumericCellValue()));
                    			}
                    			products.add(product);
                    			logger.debug(CLASSNAME + "updateExcelData : Product {}", product);
                    		}
            				if(isNotEmpty(products)) {
            					companyProductsRepository.save(products);
            				}
            			}
            		} else if(isNotEmpty(category) && category.equals(SERVICES)) {
            			if(columns >= 4) {
            				List<CompanyServices> services = Lists.newArrayList();
                			for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    			Row row = workbook.getSheetAt(0).getRow(rowIndex);
                				CompanyServices service = new CompanyServices();
                    			service.setUserid(id);
                    			service.setFullname(fullname);
                    			service.setClientid(clientId);
                    			if(row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING) {
                    				service.setSerialno(Long.parseLong(row.getCell(row.getFirstCellNum()).getStringCellValue()));
                    			} else {
                    				service.setSerialno(new Double(row.getCell(row.getFirstCellNum()).getNumericCellValue()).longValue());
                    			}
                    			service.setName(row.getCell(row.getFirstCellNum()+1).getStringCellValue());
                    			service.setDescription(row.getCell(row.getFirstCellNum()+2).getStringCellValue());
                    			service.setUnit(row.getCell(row.getFirstCellNum()+3).getStringCellValue());
                    			if(row.getCell(row.getFirstCellNum()+4).getCellType() == Cell.CELL_TYPE_STRING) {
                    				service.setCost(row.getCell(row.getFirstCellNum()+4).getStringCellValue());
                    			}
                    			else {
                    				service.setCost(new Double(row.getCell(row.getFirstCellNum()+4).getNumericCellValue()).longValue()+"");
                    			}
                    			if(row.getCell(row.getFirstCellNum()+5).getCellType() == Cell.CELL_TYPE_STRING) {
                    				service.setSac(row.getCell(row.getFirstCellNum()+5).getStringCellValue());
                    			} else {
                    				service.setSac(new Double(row.getCell(row.getFirstCellNum()+5).getNumericCellValue()).longValue()+"");
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+6))){
                    				service.setTaxrate(new Double(row.getCell(row.getFirstCellNum()+6).getNumericCellValue()));
                    			}
                    			services.add(service);
                    			logger.debug(CLASSNAME + "updateExcelData : Service {}", service);
                    		}
            				if(isNotEmpty(services)) {
            					companyServicesRepository.save(services);
            				}
            			}
            		} else if(isNotEmpty(category) && category.equals(ITEMS)) {
            			if(columns >= 6) {
            				List<CompanyItems> items = Lists.newArrayList();
                			for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    			Row row = workbook.getSheetAt(0).getRow(rowIndex);
                				CompanyItems item = new CompanyItems();
                    			item.setUserid(id);
                    			item.setFullname(fullname);
                    			item.setClientid(clientId);
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()))){
                    				if(row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING) {
                        				item.setItemType(row.getCell(row.getFirstCellNum()).getStringCellValue());
                        			} else {
                        				item.setItemType(new Double(row.getCell(row.getFirstCellNum()).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+1))){
	                    			if(row.getCell(row.getFirstCellNum()+1).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setItemno(row.getCell(row.getFirstCellNum()+1).getStringCellValue());
	                    			} else {
	                    				item.setItemno(new Double(row.getCell(row.getFirstCellNum()+1).getNumericCellValue()).longValue()+"");
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+2))){
                    				item.setDescription(row.getCell(row.getFirstCellNum()+2).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+3))){
                    				if(row.getCell(row.getFirstCellNum()+3).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setItemDescription(row.getCell(row.getFirstCellNum()+3).getStringCellValue());
	                    			} else {
	                    				item.setItemDescription(new Double(row.getCell(row.getFirstCellNum()+3).getNumericCellValue()).longValue()+"");
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+4))){
	                    			if(row.getCell(row.getFirstCellNum()+4).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setItemgroupno(row.getCell(row.getFirstCellNum()+4).getStringCellValue());
	                    			} else {
	                    				item.setItemgroupno(new Double(row.getCell(row.getFirstCellNum()+4).getNumericCellValue()).longValue()+"");
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+5))){
                    				if(row.getCell(row.getFirstCellNum()+5).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setGroupdescription(row.getCell(row.getFirstCellNum()+5).getStringCellValue());
	                    			} else {
	                    				item.setGroupdescription(new Double(row.getCell(row.getFirstCellNum()+5).getNumericCellValue()).longValue()+"");
	                    			}
                    			}
                    			
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+6))){
                    				if(row.getCell(row.getFirstCellNum()+6).getCellType() == Cell.CELL_TYPE_STRING) {
                    					item.setUnit(row.getCell(row.getFirstCellNum()+6).getStringCellValue());
                    				}else {
                    					item.setUnit(new Double(row.getCell(row.getFirstCellNum()+6).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+7))){
                    				if(row.getCell(row.getFirstCellNum()+7).getCellType() == Cell.CELL_TYPE_STRING) {
                    					item.setCode(row.getCell(row.getFirstCellNum()+7).getStringCellValue());
                    				}else {
                    					item.setCode(new Double(row.getCell(row.getFirstCellNum()+7).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+8))){
	                    			if(row.getCell(row.getFirstCellNum()+8).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setSalePrice(Double.parseDouble(row.getCell(row.getFirstCellNum()+8).getStringCellValue()));
	                    			} else {
	                    				item.setSalePrice(row.getCell(row.getFirstCellNum()+8).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+9))){
	                    			if(row.getCell(row.getFirstCellNum()+9).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setPurchasePrice(Double.parseDouble(row.getCell(row.getFirstCellNum()+9).getStringCellValue()));
	                    			} else {
	                    				item.setPurchasePrice(row.getCell(row.getFirstCellNum()+9).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+10))){
	                    			if(row.getCell(row.getFirstCellNum()+10).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setWholeSalePrice(Double.parseDouble(row.getCell(row.getFirstCellNum()+10).getStringCellValue()));
	                    			} else {
	                    				item.setWholeSalePrice(row.getCell(row.getFirstCellNum()+10).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+11))){
	                    			if(row.getCell(row.getFirstCellNum()+11).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setMrpPrice(Double.parseDouble(row.getCell(row.getFirstCellNum()+11).getStringCellValue()));
	                    			} else {
	                    				item.setMrpPrice(row.getCell(row.getFirstCellNum()+11).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+12))){
	                    			if(row.getCell(row.getFirstCellNum()+12).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setOpeningStock(Double.parseDouble(row.getCell(row.getFirstCellNum()+12).getStringCellValue()));
	                    				item.setCurrentStock(Double.parseDouble(row.getCell(row.getFirstCellNum()+12).getStringCellValue()));
	                    			} else {
	                    				item.setOpeningStock(row.getCell(row.getFirstCellNum()+12).getNumericCellValue());
	                    				item.setCurrentStock(row.getCell(row.getFirstCellNum()+12).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+13))){
	                    			if(row.getCell(row.getFirstCellNum()+13).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setClosingStock(Double.parseDouble(row.getCell(row.getFirstCellNum()+13).getStringCellValue()));
	                    			} else {
	                    				item.setClosingStock(row.getCell(row.getFirstCellNum()+13).getNumericCellValue());
	                    			}
                    			}

                    			
                    			SimpleDateFormat asdate=new SimpleDateFormat("dd/MM/yyyy");  
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+14))){
	                    			if(row.getCell(row.getFirstCellNum()+14).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setAsOnDate(asdate.parse(row.getCell(row.getFirstCellNum()+14).getStringCellValue()));
	                    			}else {
	                    				item.setAsOnDate(row.getCell(row.getFirstCellNum()+14).getDateCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+15))){
	                    			if(row.getCell(row.getFirstCellNum()+15).getCellType() == Cell.CELL_TYPE_STRING) {
	                    			      item.setStocklevel(row.getCell(row.getFirstCellNum()+15).getStringCellValue());
	                    			} else {
	                    				item.setStocklevel(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
	                    			}
                				}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+16))){
                    				if(row.getCell(row.getFirstCellNum()+16).getCellType() == Cell.CELL_TYPE_STRING) {
                        				item.setTaxrate(row.getCell(row.getFirstCellNum()+16).getStringCellValue());
                        			} else {
                        				item.setTaxrate(new Double(row.getCell(row.getFirstCellNum()+16).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+17))){
	                    			if(row.getCell(row.getFirstCellNum()+17).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setExmepted(Double.parseDouble(row.getCell(row.getFirstCellNum()+17).getStringCellValue()));
	                    			} else {
	                    				item.setExmepted(row.getCell(row.getFirstCellNum()+17).getNumericCellValue());
	                    			}
	                    			item.setIsExempted(true);
                    			}else {
                    				item.setIsExempted(false);
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+18))){
                    				if(row.getCell(row.getFirstCellNum()+18).getCellType() == Cell.CELL_TYPE_STRING) {
                        				item.setExemptedType(row.getCell(row.getFirstCellNum()+18).getStringCellValue());
                        			} else {
                        				item.setExemptedType(new Double(row.getCell(row.getFirstCellNum()+18).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+19))){
	                    			if(row.getCell(row.getFirstCellNum()+19).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				item.setDiscount(Double.parseDouble(row.getCell(row.getFirstCellNum()+19).getStringCellValue()));
	                    			} else {
	                    				item.setDiscount(row.getCell(row.getFirstCellNum()+19).getNumericCellValue());
	                    			}
	                    			item.setIsDiscount(true);
                    			}else {
                    				item.setIsDiscount(false);
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+20))){
                    				if(row.getCell(row.getFirstCellNum()+20).getCellType() == Cell.CELL_TYPE_STRING) {
                        				item.setDiscountType(row.getCell(row.getFirstCellNum()+20).getStringCellValue());
                        			} else {
                        				item.setDiscountType(new Double(row.getCell(row.getFirstCellNum()+20).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+21))){
                    				if(row.getCell(row.getFirstCellNum()+21).getCellType() == Cell.CELL_TYPE_STRING) {
                        				item.setReOrderType(row.getCell(row.getFirstCellNum()+21).getStringCellValue());
                        			} else {
                        				item.setReOrderType(new Double(row.getCell(row.getFirstCellNum()+21).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			saveDefaultStocks(id, item);
                    			Date dt = null;
                    			dt = (Date)item.getAsOnDate();
                    			if(isNotEmpty(dt)) {
                    				int month = dt.getMonth();
                    				int year = dt.getYear()+1900;
                    				int quarter = month/3;
                    				quarter = quarter == 0 ? 4 : quarter;
                    				String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
                    				month++;
                    				
                    				item.setMthCd(""+month);
                    				item.setYrCd(""+yearCode);
                    				item.setQrtCd(""+quarter);
                    				item.setSftr(1);
                    			}
                    			items.add(item);
                    			logger.debug(CLASSNAME + "updateExcelData : Service {}", item);
                			}
            				if(isNotEmpty(items)) {
            					companyItemsRepository.save(items);
            				}
            			}
            		}else if(isNotEmpty(category) && category.equals(CUSTOMERS)) {
            			if(columns >= 6) {
            				List<CompanyCustomers> customers = Lists.newArrayList();
            				List<CompanyBankDetails> bankDetails = Lists.newArrayList();
            				List<ProfileLedger> ledgers = Lists.newArrayList();
            				List<String> bankAccountNumbers = Lists.newArrayList();
            				List<String> ledgerNames = Lists.newArrayList();
            				boolean bankExist = false;
            				boolean ledgerExist = false;
            				Calendar cal = Calendar.getInstance();
        					int month = cal.get(Calendar.MONTH);
        					int year = cal.get(Calendar.YEAR);
        					int day = cal.get(Calendar.DATE);
        					cal.set(year, month, day, 0, 0, 0);
        					Date startDate = new java.util.Date(cal.getTimeInMillis());
        					cal = Calendar.getInstance();
        					cal.set(year, month-1, day, 0, 0, 0);
        					Date endDate = new java.util.Date(cal.getTimeInMillis());
                			for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    			Row row = workbook.getSheetAt(0).getRow(rowIndex);
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum())) && NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()).getStringCellValue())){
                    			CompanyCustomers customer = new CompanyCustomers();
                    			customer.setUserid(id);
                    			customer.setFullname(fullname);
                    			customer.setClientid(clientId);
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()))){
                    				customer.setType(row.getCell(row.getFirstCellNum()).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+1))){
                    				if(row.getCell(row.getFirstCellNum()+1).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setName(row.getCell(row.getFirstCellNum()+1).getStringCellValue());
                    				}else {
                    					customer.setName(new Double(row.getCell(row.getFirstCellNum()+1).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+2))){
                    				if(row.getCell(row.getFirstCellNum()+2).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCustomerLedgerName(row.getCell(row.getFirstCellNum()+2).getStringCellValue());
                    				}else {
                    					customer.setCustomerLedgerName(new Double(row.getCell(row.getFirstCellNum()+2).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+3))){
                    				if(row.getCell(row.getFirstCellNum()+3).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setContactperson(row.getCell(row.getFirstCellNum()+3).getStringCellValue());
                    				}else {
                    					customer.setContactperson(new Double(row.getCell(row.getFirstCellNum()+3).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+4))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+4).getCellType() == Cell.CELL_TYPE_STRING) {
                        				customer.setCustomerId(row.getCell(row.getFirstCellNum()+4).getStringCellValue());
                    				} else {
                        				customer.setCustomerId(new Double(row.getCell(row.getFirstCellNum()+4).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+5))){
	                    			if(row.getCell(row.getFirstCellNum()+5).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				customer.setCreditPeriod(row.getCell(row.getFirstCellNum()+5).getStringCellValue());
	                    			} else {
	                    				customer.setCreditPeriod(new Double(row.getCell(row.getFirstCellNum()+5).getNumericCellValue()).longValue()+"");
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+6))){
	                    			if(row.getCell(row.getFirstCellNum()+6).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				customer.setCreditAmount(Double.parseDouble(row.getCell(row.getFirstCellNum()+6).getStringCellValue()));
	                    			} else {
	                    				customer.setCreditAmount(row.getCell(row.getFirstCellNum()+6).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+7))){
                    				if(row.getCell(row.getFirstCellNum()+7).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCustomerPanNumber(row.getCell(row.getFirstCellNum()+7).getStringCellValue());
                    				}else {
                    					customer.setCustomerPanNumber(new Double(row.getCell(row.getFirstCellNum()+7).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+8))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+8).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCustomerTanPanNumber(row.getCell(row.getFirstCellNum()+8).getStringCellValue());
                    				}else {
                    					customer.setCustomerTanPanNumber(new Double(row.getCell(row.getFirstCellNum()+8).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+9))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+9).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCustomerTanNumber(row.getCell(row.getFirstCellNum()+9).getStringCellValue());
                    				}else {
                    					customer.setCustomerTanNumber(new Double(row.getCell(row.getFirstCellNum()+9).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			
                    			                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+10))){
                    				customer.setGstnnumber(row.getCell(row.getFirstCellNum()+10).getStringCellValue());
                    				GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(row.getCell(row.getFirstCellNum()+10).getStringCellValue());
                    				if(isNotEmpty(gstndata)) {
                    					if(isNotEmpty(gstndata.getDty())) {
                    						customer.setDealerType(gstndata.getDty());
                    					}
                    				}else {
	                    				Response response = iHubConsumerService.publicSearch(row.getCell(row.getFirstCellNum()+10).getStringCellValue().toUpperCase());
	                    				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
	                    						&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
	                    					ResponseData data = response.getData(); 
	                    					if(isNotEmpty(data.getDty())) {
	                    						customer.setDealerType(data.getDty());
	                    					}
	                    					CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
	                    					future.thenAcceptAsync(responseObj -> {
	                    						try {
	                    							gstinPublicDataRepository.deleteByGstinAndUseridIsNull(row.getCell(row.getFirstCellNum()+10).getStringCellValue().toUpperCase());
	                    						} catch(Exception e) {}
	                    						GSTINPublicData publicData = new GSTINPublicData();
	                    						BeanUtils.copyProperties(responseObj.getData(), publicData);
	                    						publicData.setCreatedDate(new Date());
	                    						if(isNotEmpty(customer.getUserid())) {
	                    							publicData.setUserid(customer.getUserid());
	                    						}
	                    						try {
	                    							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
	                    						} catch (UnknownHostException e) {
	                    							// TODO Auto-generated catch block
	                    							e.printStackTrace();
	                    						}
	                    						gstinPublicDataRepository.save(publicData);
	                    					});
	                    				}
	                    			}
                    				
                    			}
                    			
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+11))){
                    				if(row.getCell(row.getFirstCellNum()+11).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setMobilenumber(row.getCell(row.getFirstCellNum()+11).getStringCellValue());
                        			} else {
                        				customer.setMobilenumber(new Double(row.getCell(row.getFirstCellNum()+11).getNumericCellValue()).longValue()+"");
                        			}	
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+12))){
                    				if(row.getCell(row.getFirstCellNum()+12).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCountry(row.getCell(row.getFirstCellNum()+12).getStringCellValue());
                        			} else {
                        				customer.setCountry(new Double(row.getCell(row.getFirstCellNum()+12).getNumericCellValue()).longValue()+"");
                        			}
                    				
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+13))){
                    				if(row.getCell(row.getFirstCellNum()+13).getCellType() == Cell.CELL_TYPE_STRING) {
                    					String stateName = row.getCell(row.getFirstCellNum()+13).getStringCellValue();
                        				for(StateConfig stateConfig : configService.getStates()) {
                        					String state = stateConfig.getName();
                        					String st = state.substring(state.indexOf("-")+1,state.length()).trim().replaceAll("\\s", "");
    										if(st.equalsIgnoreCase(stateName.replaceAll("\\s", ""))) {
    											customer.setState(state);
    										}
    									}
                    				}else {
                    					String stateName = new Double(row.getCell(row.getFirstCellNum()+13).getNumericCellValue()).longValue()+"";
                        				for (StateConfig stateConfig : configService.getStates()) {
                        					int len = stateName.length();
                        					String state = stateConfig.getName();
                        					if(len < 2) {
                        						stateName = "0"+stateName;
                        						if (stateConfig.getTin().equals(stateName)) {
                        							customer.setState(state);
                            						break;
                            					}
                        					}else {
                        						if (stateConfig.getTin().equals(Integer.parseInt(stateName))) {
                        							customer.setState(state);
                            						break;
                            					}
                        					}
                        					
                        				}
                        				
                    				}
                    				
                    				
                    				
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+14))){
                    				customer.setAddress(row.getCell(row.getFirstCellNum()+14).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+15))){
                    				if(row.getCell(row.getFirstCellNum()+15).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setPincode(row.getCell(row.getFirstCellNum()+15).getStringCellValue());
                        			} else {
                        				customer.setPincode(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+16))){
                    				if(row.getCell(row.getFirstCellNum()+16).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setCity(row.getCell(row.getFirstCellNum()+16).getStringCellValue());
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+17))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+17).getCellType() == Cell.CELL_TYPE_STRING) {
                    					customer.setEmail(row.getCell(row.getFirstCellNum()+17).getStringCellValue());
                        			} else {
                        				customer.setEmail(new Double(row.getCell(row.getFirstCellNum()+17).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+18))){
                    			
                    				if(row.getCell(row.getFirstCellNum()+18).getCellType() == Cell.CELL_TYPE_STRING) {
                    					//customer.setLandline(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
                        				customer.setLandline(row.getCell(row.getFirstCellNum()+18).getStringCellValue());
                        			} else {
                        				customer.setLandline(new Double(row.getCell(row.getFirstCellNum()+18).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+19))){
	                    			if(row.getCell(row.getFirstCellNum()+19).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				customer.setOpeningbalance(Double.parseDouble(row.getCell(row.getFirstCellNum()+19).getStringCellValue()));
	                    			} else {
	                    				customer.setOpeningbalance(row.getCell(row.getFirstCellNum()+19).getNumericCellValue());
	                    			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+20))){
                    				customer.setCustomerBankName(row.getCell(row.getFirstCellNum()+20).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+21))){
                    			
                    				if(row.getCell(row.getFirstCellNum()+21).getCellType() == Cell.CELL_TYPE_STRING) {
                    					//customer.setLandline(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
                        				customer.setCustomerAccountNumber(row.getCell(row.getFirstCellNum()+21).getStringCellValue());
                        			} else {
                        				customer.setCustomerAccountNumber(new Double(row.getCell(row.getFirstCellNum()+21).getNumericCellValue()).longValue()+"");
                        				//customer.setCustomerAccountNumber(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+22))){
                    				customer.setCustomerAccountName(row.getCell(row.getFirstCellNum()+22).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+23))){
                    				customer.setCustomerBranchName(row.getCell(row.getFirstCellNum()+23).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+24))){
                    				customer.setCustomerBankIfscCode(row.getCell(row.getFirstCellNum()+24).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+21))){
                    				String bankAccountNumber = "";
                    				if(row.getCell(row.getFirstCellNum()+21).getCellType() == Cell.CELL_TYPE_STRING) {
                    					bankAccountNumber = row.getCell(row.getFirstCellNum()+21).getStringCellValue();
                        			} else {
                        				bankAccountNumber = new Double(row.getCell(row.getFirstCellNum()+21).getNumericCellValue()).longValue()+"";
                        			}
                    				if(isEmpty(bankAccountNumbers)){
                    					bankAccountNumbers.add(bankAccountNumber);
                    					bankExist = true;
                    				}else{
                    					if(!bankAccountNumbers.contains(bankAccountNumber)){
                    						bankAccountNumbers.add(bankAccountNumber);
                    						bankExist = true;
                    					}else{
                    						bankExist = false;
                    					}
                    				}
                    				CompanyBankDetails comBankDetail = companyBankDetailsRepository.findByClientidAndAccountnumber(clientId, bankAccountNumber);
                    				if(isEmpty(comBankDetail) && bankExist){
		                    			CompanyBankDetails bankDetail = new CompanyBankDetails();
		                    			bankDetail.setClientid(clientId);
		                    			bankDetail.setUserid(id);
		                    			bankDetail.setFullname(fullname);
		                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+20))){
		                    				bankDetail.setBankname(row.getCell(row.getFirstCellNum()+20).getStringCellValue());
		                    			}
		                    			//bankDetail.setAccountnumber(new Double(row.getCell(row.getFirstCellNum()+17).getNumericCellValue()).longValue()+"");
		                    			
		                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+21))){
		                        			
		                    				if(row.getCell(row.getFirstCellNum()+21).getCellType() == Cell.CELL_TYPE_STRING) {
		                    					//customer.setLandline(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
		                    					bankDetail.setAccountnumber(row.getCell(row.getFirstCellNum()+21).getStringCellValue());
		                        			} else {
		                        				bankDetail.setAccountnumber(new Double(row.getCell(row.getFirstCellNum()+21).getNumericCellValue()).longValue()+"");
		                        				//customer.setCustomerAccountNumber(new Double(row.getCell(row.getFirstCellNum()+15).getNumericCellValue()).longValue()+"");
		                        			}
		                    			}
		                    				                    			
		                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+23))){
		                    				bankDetail.setBranchname(row.getCell(row.getFirstCellNum()+23).getStringCellValue());
		                    			}
		                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+24))){
		                    				bankDetail.setIfsccode(row.getCell(row.getFirstCellNum()+24).getStringCellValue());
		                    			}
		                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+22))){
		                    				bankDetail.setAccountName(row.getCell(row.getFirstCellNum()+22).getStringCellValue());
		                    			}
		                    			bankDetails.add(bankDetail);
                    				}
                    			}
                    			String ledgerName = "";
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+2))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+2).getCellType() == Cell.CELL_TYPE_STRING) {
                    					ledgerName = row.getCell(row.getFirstCellNum()+2).getStringCellValue();
                    				}else {
                    					ledgerName =  new Double(row.getCell(row.getFirstCellNum()+2).getNumericCellValue()).longValue()+"";
                    				}
                    			}else if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+1))) {
                    				ledgerName = row.getCell(row.getFirstCellNum()+1).getStringCellValue();
                    			}
                    			
                    			if(isEmpty(ledgerNames)){
                    				ledgerNames.add(ledgerName);
                    				ledgerExist = true;
                				}else{
                					if(!ledgerNames.contains(ledgerName)){
                						ledgerNames.add(ledgerName);
                						ledgerExist = true;
                					}else{
                						ledgerExist = false;
                					}
                				}
                    			
                    			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientId,ledgerName);
                    			if(isEmpty(ldgr) && ledgerExist){
                    				ProfileLedger lgrdr = new ProfileLedger();
                    				lgrdr.setClientid(clientId);
                    				lgrdr.setLedgerName(ledgerName);
                    				lgrdr.setGrpsubgrpName("Sundry Debtors");
                    				lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
                    				lgrdr.setLedgerOpeningBalance(0);
                    				ledgers.add(lgrdr);
                    			}
                    			
                    			customers.add(customer);
                    			logger.debug(CLASSNAME + "updateExcelData : Customer {}", customer);
                			}
                			}
            				if(isNotEmpty(customers)) {
            					companyCustomersRepository.save(customers);
            				}
            				if(isNotEmpty(bankDetails)) {
            					companyBankDetailsRepository.save(bankDetails);
            				}
            				if(isNotEmpty(ledgers)) {
            					ledgerRepository.save(ledgers);
            				}
            			}
            		}else if(isNotEmpty(category) && category.equals(SUPPLIERS)) {
            			if(columns >= 6) {
            				List<CompanySuppliers> suppliers = Lists.newArrayList();
            				List<ProfileLedger> ledgers = Lists.newArrayList();
            				List<String> ledgerNames = Lists.newArrayList();
            				boolean ledgerExist = false;
            				Calendar cal = Calendar.getInstance();
        					int month = cal.get(Calendar.MONTH);
        					int year = cal.get(Calendar.YEAR);
        					int day = cal.get(Calendar.DATE);
        					cal.set(year, month, day, 0, 0, 0);
        					Date startDate = new java.util.Date(cal.getTimeInMillis());
        					cal = Calendar.getInstance();
        					cal.set(year, month-1, day, 0, 0, 0);
        					Date endDate = new java.util.Date(cal.getTimeInMillis());
                			for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    			Row row = workbook.getSheetAt(0).getRow(rowIndex);
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum())) && NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()).getStringCellValue())){
                    			CompanySuppliers supplier = new CompanySuppliers();
                    			supplier.setUserid(id);
                    			supplier.setFullname(fullname);
                    			supplier.setClientid(clientId);
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()))){
                    				supplier.setType(row.getCell(row.getFirstCellNum()).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+1))){
                    				supplier.setName(row.getCell(row.getFirstCellNum()+1).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+2))){
                    				if(row.getCell(row.getFirstCellNum()+2).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setSupplierLedgerName(row.getCell(row.getFirstCellNum()+2).getStringCellValue());
                    				}else {
                    					supplier.setSupplierLedgerName(new Double(row.getCell(row.getFirstCellNum()+2).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+3))){
                    				if(row.getCell(row.getFirstCellNum()+3).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setContactperson(row.getCell(row.getFirstCellNum()+3).getStringCellValue());
                    				}else {
                    					supplier.setContactperson(new Double(row.getCell(row.getFirstCellNum()+3).getNumericCellValue()).longValue()+"");
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+4))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+4).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setSupplierCustomerId(row.getCell(row.getFirstCellNum()+4).getStringCellValue());

                        			} else {
                        				supplier.setSupplierCustomerId(new Double(row.getCell(row.getFirstCellNum()+4).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+5))){                    				
                    				if(row.getCell(row.getFirstCellNum()+5).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setSupplierPanNumber(row.getCell(row.getFirstCellNum()+5).getStringCellValue());

                        			} else {
                        				supplier.setSupplierPanNumber(new Double(row.getCell(row.getFirstCellNum()+5).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+6))){				
                    				if(row.getCell(row.getFirstCellNum()+6).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setSupplierTanPanNumber(row.getCell(row.getFirstCellNum()+6).getStringCellValue());
                        			} else {
                        				supplier.setSupplierTanPanNumber(new Double(row.getCell(row.getFirstCellNum()+6).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+7))){
                    				if(row.getCell(row.getFirstCellNum()+7).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setSupplierTanNumber(row.getCell(row.getFirstCellNum()+7).getStringCellValue());
                        			} else {
                        				supplier.setSupplierTanNumber(new Double(row.getCell(row.getFirstCellNum()+7).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+8))){
                    				if(row.getCell(row.getFirstCellNum()+8).getCellType() == Cell.CELL_TYPE_STRING) {
	                    				supplier.setGstnnumber(row.getCell(row.getFirstCellNum()+8).getStringCellValue());
	                    				GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(row.getCell(row.getFirstCellNum()+8).getStringCellValue());
	                    				if(isNotEmpty(gstndata)) {
	                    					if(isNotEmpty(gstndata.getDty())) {
	                    						supplier.setDealerType(gstndata.getDty());
	                    					}
	                    				}else {
		                    				Response response = iHubConsumerService.publicSearch(row.getCell(row.getFirstCellNum()+8).getStringCellValue().toUpperCase());
		                    				if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
		                    						&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
		                    					ResponseData data = response.getData(); 
		                    					if(isNotEmpty(data.getDty())) {
		                    						supplier.setDealerType(data.getDty());
		                    					}
		                    					CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
		                    					future.thenAcceptAsync(responseObj -> {
		                    						try {
		                    							gstinPublicDataRepository.deleteByGstinAndUseridIsNull(row.getCell(row.getFirstCellNum()+8).getStringCellValue().toUpperCase());
		                    						} catch(Exception e) {}
		                    						GSTINPublicData publicData = new GSTINPublicData();
		                    						BeanUtils.copyProperties(responseObj.getData(), publicData);
		                    						publicData.setCreatedDate(new Date());
		                    						if(isNotEmpty(supplier.getUserid())) {
		                    							publicData.setUserid(supplier.getUserid());
		                    						}
		                    						try {
		                    							publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
		                    						} catch (UnknownHostException e) {
		                    							// TODO Auto-generated catch block
		                    							e.printStackTrace();
		                    						}
		                    						gstinPublicDataRepository.save(publicData);
		                    					});
		                    				}
	                    				}
                    				}
                    			}	
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+9))){
                    				if(row.getCell(row.getFirstCellNum()+9).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setMobilenumber(row.getCell(row.getFirstCellNum()+9).getStringCellValue());
                        			} else {
                        				supplier.setMobilenumber(new Double(row.getCell(row.getFirstCellNum()+9).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+10))){
                    				if(row.getCell(row.getFirstCellNum()+10).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setCountry(row.getCell(row.getFirstCellNum()+10).getStringCellValue());
                        			} else {
                        				supplier.setCountry(new Double(row.getCell(row.getFirstCellNum()+10).getNumericCellValue()).longValue()+"");
                        			}
                    				
                    			}
                    			
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+11))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+11).getCellType() == Cell.CELL_TYPE_STRING) {
                    					String stateName = row.getCell(row.getFirstCellNum()+11).getStringCellValue();
                        				for(StateConfig stateConfig : configService.getStates()) {
                        					String state = stateConfig.getName();
                        					String st = state.substring(state.indexOf("-")+1,state.length()).trim().replaceAll("\\s", "");
    										if(st.equalsIgnoreCase(stateName.replaceAll("\\s", ""))) {
    											supplier.setState(state);
    										}
    									}
                    				}else {
                    					String stateName = new Double(row.getCell(row.getFirstCellNum()+11).getNumericCellValue()).longValue()+"";
                        				for (StateConfig stateConfig : configService.getStates()) {
                        					int len = stateName.length();
                        					String state = stateConfig.getName();
                        					if(len < 2) {
                        						stateName = "0"+stateName;
                        						if (stateConfig.getTin().equals(stateName)) {
                            						supplier.setState(state);
                            						break;
                            					}
                        					}else {
                        						if (stateConfig.getTin().equals(Integer.parseInt(stateName))) {
                            						supplier.setState(state);
                            						break;
                            					}
                        					}
                        					
                        				}
                        				
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+12))){
                    				supplier.setAddress(row.getCell(row.getFirstCellNum()+12).getStringCellValue());
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+13))){
                    				if(row.getCell(row.getFirstCellNum()+13).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setPincode(row.getCell(row.getFirstCellNum()+13).getStringCellValue());
                        			} else {
                        				supplier.setPincode(new Double(row.getCell(row.getFirstCellNum()+13).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+14))){
                    				if(row.getCell(row.getFirstCellNum()+14).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setCity(row.getCell(row.getFirstCellNum()+14).getStringCellValue());
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+15))){
                    				if(row.getCell(row.getFirstCellNum()+15).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setEmail(row.getCell(row.getFirstCellNum()+15).getStringCellValue());
                    				}
                    			}
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+16))){
                    				if(row.getCell(row.getFirstCellNum()+16).getCellType() == Cell.CELL_TYPE_STRING) {
                    					supplier.setLandline(row.getCell(row.getFirstCellNum()+16).getStringCellValue());
                        			} else {
                        				supplier.setLandline(new Double(row.getCell(row.getFirstCellNum()+16).getNumericCellValue()).longValue()+"");
                        			}
                    			}
                    			String ledgerName = "";
                    			if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+2))){
                    				
                    				if(row.getCell(row.getFirstCellNum()+2).getCellType() == Cell.CELL_TYPE_STRING) {
                    					ledgerName = row.getCell(row.getFirstCellNum()+2).getStringCellValue();
                    				}else {
                    					ledgerName =  new Double(row.getCell(row.getFirstCellNum()+2).getNumericCellValue()).longValue()+"";
                    				}
                    			}else if(NullUtil.isNotEmpty(row.getCell(row.getFirstCellNum()+1))) {
                    				ledgerName = row.getCell(row.getFirstCellNum()+1).getStringCellValue();
                    			}
                    			
                    			if(isEmpty(ledgerNames)){
                    				ledgerNames.add(ledgerName);
                    				ledgerExist = true;
                				}else{
                					if(!ledgerNames.contains(ledgerName)){
                						ledgerNames.add(ledgerName);
                						ledgerExist = true;
                					}else{
                						ledgerExist = false;
                					}
                				}
                    			
                    			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientId,ledgerName);
                    			if(isEmpty(ldgr) && ledgerExist){
                    				ProfileLedger lgrdr = new ProfileLedger();
                    				lgrdr.setClientid(clientId);
                    				lgrdr.setLedgerName(ledgerName);
                    				lgrdr.setGrpsubgrpName("Sundry Creditors");
                    				lgrdr.setLedgerpath("Laibilities/Current liabilities/Sundry Creditors");
                    				ledgers.add(lgrdr);
                    			}
                    			suppliers.add(supplier);
                    			logger.debug(CLASSNAME + "updateExcelData : Supplier {}", supplier);
                			}
            			}
            				if(isNotEmpty(suppliers)) {
            					companySuppliersRepository.save(suppliers);
            				}
            				if(isNotEmpty(ledgers)) {
            					ledgerRepository.save(ledgers);
            				}
            			}
            		}
            	}
            }
		}
		logger.debug(CLASSNAME + "updateExcelData : End");
	}
	
	@Override
	@Transactional
	public List<CompanyCustomers> getSearchedCustomers(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedCustomers : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				new Criteria().orOperator(Criteria.where("name").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),Criteria.where("customerId").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)))
			));
		
		
		return mongoTemplate.find(query, CompanyCustomers.class, "companycustomers");
	}
	
	@Override
	@Transactional
	public List<CompanySuppliers> getSearchedSuppliers(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedSuppliers : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
			Criteria.where("clientid").is(clientid),
			new Criteria().orOperator(Criteria.where("name").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),Criteria.where("supplierCustomerId").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)))
		));
		
		return mongoTemplate.find(query, CompanySuppliers.class, "company_suppliers");
	}
	
	
	
	@Override
	public List<CompanyItems> getSearchedItemsList(String clientid, final String searchQuery) {
		List<CompanyItems> cmpyitemslist=getSearchedItems(clientid, searchQuery);
		cmpyitemslist.stream().forEach(item->{
			item.setItemnoAndDescription(item.getItemno()+"-"+item.getDescription());
			item.setUserid(item.getId().toString());
		});
		return cmpyitemslist;
	}
	
	@Override
	@Transactional
	public List<CompanyItems> getSearchedItems(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedItems : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				new Criteria().orOperator(Criteria.where("description").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),Criteria.where("itemno").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)))
				));
		return mongoTemplate.find(query, CompanyItems.class, "companyitems");
	}
	
	public List<CompanyProducts> getSearchedProducts(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedProducts : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				Criteria.where("description").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, CompanyProducts.class, "companyproducts");
	}
	
	public List<CompanyServices> getSearchedServices(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedServices : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				Criteria.where("description").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, CompanyServices.class, "companyservices");
	}
	
	@Override
	@Transactional
	public CommonBO getCommonList(final String clientid, final String searchQuery) {
		final String method = "getCommonList::";
		logger.debug(CLASSNAME + method + BEGIN);
		CommonBO commonBO = new CommonBO();
		commonBO.setItems(getSearchedItemsList(clientid, searchQuery));
		//commonBO.setProducts(getSearchedProducts(clientid, searchQuery));
		//commonBO.setServices(getSearchedServices(clientid, searchQuery));
		return commonBO;
	}
	
	@Override
	@Transactional
	public Map<String, List<Permission>> getPermissions(final String userid) {
		logger.debug(CLASSNAME + "getPermissions : Begin");
		User user = userService.getUser(userid);
		if(isNotEmpty(user) && "Child".equalsIgnoreCase(user.getCategory())) {
			List<Role> roles = roleRepository.findAll();
			CompanyUser companyUser = getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getRole())) {
				CompanyRole role = companyRoleRepository.findOne(companyUser.getRole());
				if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
					logger.debug(CLASSNAME + "getPermissions : is not empty");
					for(Role dbRole : roles) {
						if(isNotEmpty(dbRole.getPrivileges())) {
							if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
								Permission dummy = new Permission();
								dummy.setName(dbRole.getName());
								List<Permission> dummies = Lists.newArrayList();
								dummies.add(dummy);
								role.getPermissions().put(dbRole.getCategory(), dummies);
							} else {
								boolean present = false;
								for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
									if(perm.getName().equals(dbRole.getName())) {
										present = true;
										break;
									}
								}
								if(!present) {
									Permission dummy = new Permission();
									dummy.setName(dbRole.getName());
									role.getPermissions().get(dbRole.getCategory()).add(dummy);
								}
							}
						}
					}
					return role.getPermissions();
				}else{
					logger.debug(CLASSNAME + "getPermissions : is empty");
					 return null;
				}
			} else if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getUserid())) {
				User superUser = userService.getUser(companyUser.getUserid());
				if(isNotEmpty(superUser)) {
					CompanyUser companyParent = getCompanyUser(superUser.getEmail());
					if(isNotEmpty(companyParent) && isNotEmpty(companyParent.getRole())) {
						CompanyRole role = companyRoleRepository.findOne(companyParent.getRole());
						if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
							logger.debug(CLASSNAME + "getPermissions : is not empty");
							for(Role dbRole : roles) {
								if(isNotEmpty(dbRole.getPrivileges())) {
									if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
										Permission dummy = new Permission();
										dummy.setName(dbRole.getName());
										List<Permission> dummies = Lists.newArrayList();
										dummies.add(dummy);
										role.getPermissions().put(dbRole.getCategory(), dummies);
									} else {
										boolean present = false;
										for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
											if(perm.getName().equals(dbRole.getName())) {
												present = true;
												break;
											}
										}
										if(!present) {
											Permission dummy = new Permission();
											dummy.setName(dbRole.getName());
											role.getPermissions().get(dbRole.getCategory()).add(dummy);
										}
									}
								}
							}
							return role.getPermissions();
						} else {
							logger.debug(CLASSNAME + "getPermissions : is empty");
							 return null;
						}
					}
				}
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public Map<String, List<Permission>> getUserClientPermissions(final String userid, final String clientid) {
		logger.debug(CLASSNAME + "getUserClientPermissions : Begin");
		User user = userRepository.findById(userid);
		if(isNotEmpty(user)) {
			List<Role> roles = roleRepository.findAll();
			CompanyUser companyUser = getCompanyUser(user.getEmail(), clientid);
			String cUrole = "";
			if(isNotEmpty(companyUser)) {
				ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(user.getId().toString(), clientid);
				if(isNotEmpty(clientuserMapping) && isNotEmpty(clientuserMapping.getRole())) {
					cUrole =  clientuserMapping.getRole();
				}
			}
			if(isNotEmpty(cUrole)) {				
				CompanyRole role = companyRoleRepository.findOne(cUrole);
				if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
					logger.debug(CLASSNAME + "getUserClientPermissions : is not empty");
					for(Role dbRole : roles) {
						if(isNotEmpty(dbRole.getPrivileges())) {
							if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
								Set<String> privileges = dbRole.getPrivileges();
								List<Permission> dummies = Lists.newArrayList();
								for(String privilege : privileges) {
									Permission dummy = new Permission();
									dummy.setName(dbRole.getName());
									dummy.setStatus(privilege+"-No");
									dummies.add(dummy);
								}
								role.getPermissions().put(dbRole.getCategory(), dummies);
							} else {
								boolean present = false;
								for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
									if(perm.getName().equals(dbRole.getName())) {
										present = true;
										break;
									}
								}
								if(!present) {
									Set<String> privileges = dbRole.getPrivileges();
									List<Permission> dummies = Lists.newArrayList();
									for(String privilege : privileges) {
										Permission dummy = new Permission();
										dummy.setName(dbRole.getName());
										dummy.setStatus(privilege+"-No");
										dummies.add(dummy);
									}
									role.getPermissions().get(dbRole.getCategory()).addAll(dummies);
								}
							}
						}else {
							if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
								List<Permission> dummies = Lists.newArrayList();
								Permission dummy = new Permission();
								dummy.setName(dbRole.getName());
								dummy.setStatus("No");
								dummies.add(dummy);
								role.getPermissions().put(dbRole.getCategory(), dummies);
							}else {
								boolean present = false;
								for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
									if(perm.getName().equals(dbRole.getName())) {
										present = true;
										break;
									}
								}
								if(!present) {
									Permission dummy = new Permission();
									dummy.setName(dbRole.getName());
									dummy.setStatus("No");
									role.getPermissions().get(dbRole.getCategory()).add(dummy);
								}
							}
						}
					}
					return role.getPermissions();
				}else{
					logger.debug(CLASSNAME + "getUserClientPermissions : is empty");
					 return null;
				}
			} else if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getUserid())) {
				User superUser = userService.getUser(companyUser.getUserid());
				if(isNotEmpty(superUser)) {
					CompanyUser companyParent = getCompanyUser(user.getEmail(), clientid);
					String cPUrole = "";
					if(isNotEmpty(companyParent)) {
						ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(superUser.getId().toString(), clientid);
						if(isNotEmpty(clientuserMapping) && isNotEmpty(clientuserMapping.getRole())) {
							cPUrole =  clientuserMapping.getRole();
						}
					}
					if(isNotEmpty(cPUrole)) {
						CompanyRole role = companyRoleRepository.findOne(cPUrole);
						if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
							logger.debug(CLASSNAME + "getPermissions : is not empty");
							for(Role dbRole : roles) {
								if(isNotEmpty(dbRole.getPrivileges())) {
									if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
										Set<String> privileges = dbRole.getPrivileges();
										List<Permission> dummies = Lists.newArrayList();
										for(String privilege : privileges) {
											Permission dummy = new Permission();
											dummy.setName(dbRole.getName());
											dummy.setStatus(privilege+"-No");
											dummies.add(dummy);
										}
										role.getPermissions().put(dbRole.getCategory(), dummies);
									} else {
										boolean present = false;
										for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
											if(perm.getName().equals(dbRole.getName())) {
												present = true;
												break;
											}
										}
										if(!present) {
											Set<String> privileges = dbRole.getPrivileges();
											List<Permission> dummies = Lists.newArrayList();
											for(String privilege : privileges) {
												Permission dummy = new Permission();
												dummy.setName(dbRole.getName());
												dummy.setStatus(privilege+"-No");
												dummies.add(dummy);
											}
											role.getPermissions().get(dbRole.getCategory()).addAll(dummies);
										}
									}
								}else {
									if(!role.getPermissions().keySet().contains(dbRole.getCategory())) {
										List<Permission> dummies = Lists.newArrayList();
										Permission dummy = new Permission();
										dummy.setName(dbRole.getName());
										dummy.setStatus("No");
										dummies.add(dummy);
										role.getPermissions().put(dbRole.getCategory(), dummies);
									}else {
										boolean present = false;
										for(Permission perm : role.getPermissions().get(dbRole.getCategory())) {
											if(perm.getName().equals(dbRole.getName())) {
												present = true;
												break;
											}
										}
										if(!present) {
											Permission dummy = new Permission();
											dummy.setName(dbRole.getName());
											dummy.setStatus("No");
											role.getPermissions().get(dbRole.getCategory()).add(dummy);
										}
									}
								}
							}
							return role.getPermissions();
						} else {
							logger.debug(CLASSNAME + "getPermissions : is empty");
							 return null;
						}
					}
				}
			}
		}
		return null;
	}
	
	@Override
	@Transactional
	public Set<String> getClientPermissions(final String clientid) {
		logger.debug(CLASSNAME + "getClientPermissions : Begin");
		CompanyRole role = companyRoleRepository.findByClientidAndUseridIsNull(clientid);
		if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
			logger.debug(CLASSNAME + "getClientPermissions : is not empty");
			return role.getPermissions().keySet();
		} else {
			logger.debug(CLASSNAME + "getClientPermissions : is empty");
			return null;
		}
	}
	
	@Override
	@Transactional
	public void saveClientPermissions(final String clientid, final Set<String> permissions) {
		logger.debug(CLASSNAME + "saveClientPermissions : Begin");
		Map<String, List<Permission>> permissionMap = Maps.newHashMap();
		for(String permission : permissions) {
			Permission pObj = new Permission();
			pObj.setName(permission);
			pObj.setStatus(MasterGSTConstants.YES);
			List<Permission> pList = Lists.newArrayList();
			pList.add(pObj);
			permissionMap.put(permission, pList);
		}
		CompanyRole role = companyRoleRepository.findByClientidAndUseridIsNull(clientid);
		if(isNotEmpty(role)) {
			role.setPermissions(permissionMap);
		} else {
			role = new CompanyRole();
			role.setClientid(clientid);
			role.setPermissions(permissionMap);
		}
		companyRoleRepository.save(role);
		logger.debug(CLASSNAME + "saveClientPermissions : End");
	}
	
	@Override
	@Transactional
	public List<CompanyInvoices> getInvoices(final String userid) {
		logger.debug(CLASSNAME + "getInvoiceSubmission : Begin");
		return invoicesRepository.findByUserid(userid);
	}
	
	@Override
	@Transactional
	public CompanyBankDetails getBankDetails(String clientid) {
		logger.debug(CLASSNAME + "getBankDetails : Begin");
		return companyBankDetailsRepository.findByClientid(clientid);
	}
	
	@Override
	@Transactional
	public CompanyBankDetails saveBankDetails(CompanyBankDetails bankdetails) {
		logger.debug(CLASSNAME + "saveBankDetails : Begin {}", bankdetails);
		return companyBankDetailsRepository.save(bankdetails);
	}

	@Override
	public PartnerBankDetails getPBankDetails(String id) {
		logger.debug(CLASSNAME + "getBankDetailsById : Begin");
		return partnerBankDetailsRepository.findByUserid(id);
	}

	@Override
	public PartnerBankDetails savePBankDetails(PartnerBankDetails bankdetails) {
		logger.debug(CLASSNAME + "saveBankDetails : Begin {}", bankdetails);
		return partnerBankDetailsRepository.save(bankdetails);
	}

	@Override
	public void deleteProduct(String productId) {
		companyProductsRepository.delete(productId);
	}
	
	@Override
	public void deleteCustomer(String customerId) {
		companyCustomersRepository.delete(customerId);
	}
	
	@Override
	public void deleteSupplier(String supplierId) {
		companySuppliersRepository.delete(supplierId);
	}
	
	@Override
	public void deleteItem(String itemId) {
		inventoryService.deleteStocks(itemId);
		companyItemsRepository.delete(itemId);
	}
	
	@Override
	public void deleteBankDetail(String banknameid) {
		companyBankDetailsRepository.delete(banknameid);
	}	
	@Override
	public void deleteService(String serviceId) {
		companyServicesRepository.delete(serviceId);
	}

	@Override
	public List<Client> getSearchedGroupName(String id, final String searchQuery) {
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("groupName").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE))));
		return mongoTemplate.find(query, Client.class, "client");
	}

	@Override
	public List<CompanyBankDetails> getCompanyBankDetails(String userid,String clientid) {
		logger.debug(CLASSNAME + "getCompanyBankDetails : Begin");
		return companyBankDetailsRepository.findByUseridAndClientid(userid, clientid);
	}

	@Override
	public CompanyInvoices getInvoiceSubmissionDetails(String userid,
			String clientid, String year, String invoiceType, String returnType) {
		logger.debug(CLASSNAME + "getInvoiceSubmissionDetails : Begin");
		CompanyInvoices invoiceData = null;
		if(isNotEmpty(invoiceType)) {
			invoiceData = invoicesRepository.findByUseridAndClientidAndYearAndInvoiceTypeAndReturnType(userid, clientid, year, invoiceType,returnType); 
		}
		if(isEmpty(invoiceData) || isEmpty(invoiceData.getUserid())) {
			invoiceData = invoicesRepository.findByUseridAndClientidAndYearAndInvoiceTypeAndReturnType(userid, clientid, year, "ALL",returnType);
		}
		return invoiceData;
	}
	
	@Override
	public CompanyInvoices getInvoiceConfigDetails(String clientid, String year, String invoiceType, String returnType) {
		logger.debug(CLASSNAME + "getInvoiceConfigDetails : Begin");
		CompanyInvoices invoiceData = null;
		if(isNotEmpty(invoiceType)) {
			invoiceData = invoicesRepository.findByClientidAndYearAndInvoiceType(clientid, year, invoiceType); 
		}
		if(isEmpty(invoiceData)) {
			invoiceData = invoicesRepository.findByClientidAndYearAndInvoiceType(clientid, year, "ALL");
		}
		return invoiceData;
	}

	@Override
	public List<CompanyCustomers> getCustomers(String id, String clientid) {
		return companyCustomersRepository.findByUseridAndClientid(id,clientid);
	}

	@Override
	public List<CompanySuppliers> getSuppliers(String id, String clientid) {
		return companySuppliersRepository.findByUseridAndClientid(id, clientid);
	}

	@Override
	public void deleteBranch(String branchid, String clientid) {
		Client client = clientService.findById(clientid);
		List<Branch> branches = client.getBranches();
		for(int i=0; i<branches.size();i++){
			Branch branch = branches.get(i);
			if(branchid.equals(branch.getId().toString())){
				branches.remove(branch);
			}
		}
		client.setBranches(branches);
		clientRepository.save(client);
	}
	@Override
	public void deleteCostcenter(String branchid, String clientid) {
		Client client = clientService.findById(clientid);
		List<CostCenter> costcenters = client.getCostCenter();
		for(int i=0; i<costcenters.size();i++){
			CostCenter costcenter = costcenters.get(i);
			if(branchid.equals(costcenter.getId().toString())){
				costcenters.remove(costcenter);
			}
		}
		client.setCostCenter(costcenters);
		clientRepository.save(client);
	}
	
	
	@Override
	public void deleteVertical(String verticalid, String clientid) {
		Client client = clientService.findById(clientid);
		List<Vertical> verticals = client.getVerticals();
		for(int i=0; i<verticals.size();i++){
			Vertical vertical = verticals.get(i);
			if(verticalid.equals(vertical.getId().toString())){
				verticals.remove(vertical);
			}
		}
		client.setVerticals(verticals);
		clientRepository.save(client);
	}

	@Override
	public void deleteSubVertical(String subVerticalid, String verticalid, String clientid) {
		Client client = clientService.findById(clientid);
		List<Vertical> verticals = client.getVerticals();
		for(int i=0; i<verticals.size();i++){
			Vertical vertical = verticals.get(i);
			if(verticalid.equals(vertical.getId().toString())){
				List<Vertical> subVerticals = vertical.getSubverticals();
				for(int j=0; j<subVerticals.size();j++){
					Vertical subVertical = subVerticals.get(j);
					if(subVerticalid.equals(subVertical.getId().toString())){
						subVerticals.remove(subVertical);
					}
				}
			}
		}
		client.setVerticals(verticals);
		clientRepository.save(client);
	}
	
	@Override
	public void deleteSubBranch(String subBranchid, String branchid, String clientid) {
		Client client = clientService.findById(clientid);
		List<Branch> branchs = client.getBranches();
		for(int i=0; i<branchs.size();i++){
			Branch branch = branchs.get(i);
			if(branchid.equals(branch.getId().toString())){
				List<Branch> subBranchs = branch.getSubbranches();
				for(int j=0; j<subBranchs.size();j++){
					Branch subBranch = subBranchs.get(j);
					if(subBranchid.equals(subBranch.getId().toString())){
						subBranchs.remove(subBranch);
					}
				}
			}
		}
		client.setBranches(branchs);
		clientRepository.save(client);
	}
	@Override
	public void deleteSubCostcenter(String subBranchid, String branchid, String clientid) {
		Client client = clientService.findById(clientid);
		List<CostCenter> costcenters = client.getCostCenter();
		for(int i=0; i<costcenters.size();i++){
			CostCenter costcenter = costcenters.get(i);
			if(branchid.equals(costcenter.getId().toString())){
				List<CostCenter> subCostcenter = costcenter.getSubcostcenter();
				for(int j=0; j<subCostcenter.size();j++){
					CostCenter subBranch = subCostcenter.get(j);
					if(subBranchid.equals(subBranch.getId().toString())){
						subCostcenter.remove(subBranch);
					}
				}
			}
		}
		client.setCostCenter(costcenters);
		clientRepository.save(client);
	}
	
	@Override
	public CompanyCenters saveCenterSuvidhaSignup(CompanyCenters center) {
		
		return companyCentersRepository.save(center);
	}
	
	@Override
	public Page<CompanyCenters> getAllSubCenters(int start, int length, String fieldName, String order, String searchVal) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		query.addCriteria(criteria.where("email").exists(true));
		
		
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("fullname").regex(searchVal,"i"), Criteria.where("email").regex(searchVal,"i"), Criteria.where("mobilenumber").regex(searchVal)));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, CompanyCenters.class, "company_centers");
		if (total == 0) {
			return new PageImpl<CompanyCenters>(Collections.<CompanyCenters> emptyList());
		}
		return new PageImpl<CompanyCenters>(mongoTemplate.find(query, CompanyCenters.class, "company_centers"), pageable, total);

	}
	
	@Override
	public Page<AdminUsers> getAllSubAdmins(int start, int length, String fieldName, String order, String searchVal) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		
		query.addCriteria(criteria.where("email").exists(true));
		
		
		if(NullUtil.isNotEmpty(searchVal)){
			criteria = new Criteria().andOperator(criteria, new Criteria().orOperator(Criteria.where("fullname").regex(searchVal,"i"), Criteria.where("email").regex(searchVal,"i"), Criteria.where("mobilenumber").regex(searchVal)));
		}
		query.addCriteria(criteria);
		Sort sort = null;
		if(NullUtil.isNotEmpty(fieldName) && NullUtil.isNotEmpty(order)){	
			sort = new Sort(new Order("asc".equalsIgnoreCase(order) ? Direction.ASC : Direction.DESC, fieldName));
		}
		Pageable pageable = new PageRequest(start, length, sort);
		query.with(pageable);
		long total = mongoTemplate.count(query, User.class, "users");
		if (total == 0) {
			return new PageImpl<AdminUsers>(Collections.<AdminUsers> emptyList());
		}
		return new PageImpl<AdminUsers>(mongoTemplate.find(query, AdminUsers.class, "admin_user"), pageable, total);
	}

	@Override
	@Transactional
	public PrintConfiguration savePrintConfiguration(PrintConfiguration printform) {
		logger.debug(CLASSNAME + "savePrintConfiguration : Begin");
		printConfigurationRepository.deleteByClientid(printform.getClientid());
		if(isNotEmpty(printform.getPermissions())) {
			Map<String, List<Permission>> permissions = Maps.newHashMap();
			for(String feature : printform.getPermissions().keySet()) {
				List<Permission> permissionList = Lists.newArrayList();
				for(Permission permission : printform.getPermissions().get(feature)) {
					if(isNotEmpty(permission.getName())) {
						permission.setStatus(YES);
						permissionList.add(permission);
					}
				}
				permissions.put(feature, permissionList);
			}
			printform.setPermissions(permissions);
		}
		logger.debug(CLASSNAME + "savePrintConfiguration : End");
		return printConfigurationRepository.save(printform);
	}
	
	@Override
	@Transactional(readOnly = true)
	public PrintConfiguration getPrintConfig(String clientId) {
		logger.debug(CLASSNAME + "getPrintConfig : Begin");
		return printConfigurationRepository.findByClientid(clientId);
	}
	
	@Override
	@Transactional
	public void updateClientPermissions(String clientId, String role, List<String> permissions) {
		logger.debug(CLASSNAME + "updateClientPermissions : Begin");
		permissionMappingRepository.deleteByClientidAndRole(clientId, role);
		if(isNotEmpty(permissions)) {
			Set<Permission> pList = Sets.newHashSet();
			for(String permission : permissions) {
				Permission pObj = new Permission();
				pObj.setName(permission);
				pObj.setStatus(YES);
				pList.add(pObj);
			}
			RolePermissionMapping mObj = new RolePermissionMapping();
			mObj.setClientid(clientId);
			mObj.setRole(role);
			mObj.setPermissions(pList);
			permissionMappingRepository.save(mObj);
		}
		logger.debug(CLASSNAME + "updateClientPermissions : End");
	}
	
	@Override
	@Transactional(readOnly=true)
	public Map<String, Map<String, Set<Permission>>> getPermissionMap(String clientId, String roleid) {
		logger.debug(CLASSNAME + "getPermissionMap : Begin");
		Map<String, Map<String, Set<Permission>>> permissionMap = Maps.newTreeMap();
		List<RolePermissionMapping> permissionMappings = null;
		if(isNotEmpty(clientId)) {
			if(isNotEmpty(roleid)) {
				permissionMappings = permissionMappingRepository.findByClientidAndRoleidAndUseridIsNull(clientId, roleid);
			} else {
				permissionMappings = permissionMappingRepository.findByClientidAndRoleidIsNull(clientId);
			}
		} else if(isNotEmpty(roleid)) {
			permissionMappings = permissionMappingRepository.findByRoleidAndClientidIsNull(roleid);
		}
		List<Role> roles = null;
		if(isNotEmpty(permissionMappings)) {
			List<String> names = Lists.newArrayList();
			permissionMappings.forEach(permissionMapping -> names.add(permissionMapping.getRole()));
			roles = roleRepository.findByNameIn(names);
		} else {
			roles = roleRepository.findAll();
		}
		if(isNotEmpty(roles)) {
			for(Role role : roles) {
				if(permissionMap.keySet().contains(role.getCategory())) {
					if(isNotEmpty(permissionMappings)) {
						for(RolePermissionMapping permissionMapping : permissionMappings) {
							if(role.getName().equals(permissionMapping.getRole())) {
								if(isNotEmpty(role.getPrivileges())) {
									permissionMap.get(role.getCategory()).put(role.getName(), permissionMapping.getPermissions());
								} else {
									permissionMap.get(role.getCategory()).put(role.getName(), Sets.newHashSet());
								}
							}
						}
					} else {
						Set<Permission> pList = Sets.newHashSet();
						if(isNotEmpty(role.getPrivileges())) {
							for(String permission : role.getPrivileges()) {
								Permission pObj = new Permission();
								pObj.setName(permission);
								pObj.setStatus(YES);
								pList.add(pObj);
							}
						}
						permissionMap.get(role.getCategory()).put(role.getName(), pList);
					}
				} else {
					Map<String, Set<Permission>> roleMap = Maps.newHashMap();
					if(isNotEmpty(permissionMappings)) {
						for(RolePermissionMapping permissionMapping : permissionMappings) {
							if(role.getName().equals(permissionMapping.getRole())) {
								if(isNotEmpty(role.getPrivileges())) {
									roleMap.put(role.getName(), permissionMapping.getPermissions());
								} else {
									roleMap.put(role.getName(), Sets.newHashSet());
								}
							}
						}
					} else {
						Set<Permission> pList = Sets.newHashSet();
						if(isNotEmpty(role.getPrivileges())) {
							for(String permission : role.getPrivileges()) {
								Permission pObj = new Permission();
								pObj.setName(permission);
								pObj.setStatus(YES);
								pList.add(pObj);
							}
						}
						roleMap.put(role.getName(), pList);
					}
					if(isNotEmpty(roleMap)) {
						permissionMap.put(role.getCategory(), roleMap);
					}
				}
			}
		}
		logger.debug(CLASSNAME + "getPermissionMap : End");
		return permissionMap;
	}
	
	@Override
	public CompanyCustomers getCompanyCustomerData(String clientid, String customerid) {
		return companyCustomersRepository.findByClientidAndCustomerId(clientid, customerid);
	}
	
	@Override
	public CompanySuppliers getCompanySupplierData(String clientid, String suppliername) {
		return companySuppliersRepository.findByNameAndClientid(suppliername,clientid);
	}

	@Override
	public void delinkUser(String userId, String email, String clientid) {
		CompanyUser companyUser = companyUserRepository.findByEmail(email);
		if(isNotEmpty(companyUser)){
			List<String> userCompanies = companyUser.getCompany(); 
			if(isNotEmpty(userCompanies)){
				if(userCompanies.contains(clientid)){
					userCompanies.remove(clientid);
				}
			}
			companyUser.setCompany(userCompanies);
			companyUserRepository.save(companyUser);
		}
		
		User usr = userRepository.findByEmail(email);
		clientUserMappingRepository.deleteByUseridAndClientid(usr.getId().toString(), clientid);
		
	}

	@Override
	public List<CompanyBankDetails> getCompanyBankDetails(String clientid) {
		logger.debug(CLASSNAME + "getCompanyBankDetails : Begin");
		return companyBankDetailsRepository.findByClientidAndUseridNotNull(clientid);
	}
	
	@Override
	public List<CompanyUser> getAllUsersByUserid(String userid) {
		return companyUserRepository.findAllByUserid(userid);
	}
	@Override
	@Transactional
	public GroupDetails saveGroupDetails(GroupDetails groupdetails) {
		logger.debug(CLASSNAME + "saveGroupDetails : Begin {}", groupdetails);
		
		return groupDetailsRepository.save(groupdetails);
	}
	
	@Override
	@Transactional
	public List<GroupDetails> getGroupDetails(String clientid) {
		logger.debug(CLASSNAME + "getCustomers : Begin");
		return groupDetailsRepository.findByClientid(clientid);
	}
	
	@Override
	public void deleteGroup(String id) {
		
		GroupDetails group = groupDetailsRepository.findOne(id);
		
		List<String> grpnameLst=new ArrayList<String>();
		grpnameLst.add(group.getGroupname());
		if(isNotEmpty(group.getSubgroups())){
			
			for(Subgroups subgrps:group.getSubgroups()) {
				grpnameLst.add(subgrps.getGroupname());
			}
		}
		List<ProfileLedger> ledgersLst= ledgerRepository.findByClientidAndGrpsubgrpNameIn(group.getClientid(), grpnameLst);
		
		ledgerRepository.delete(ledgersLst);
		
		groupDetailsRepository.delete(id);
	}

	@Override
	public List<Groups> getGroupsList() {
		
		return configService.getGroups();
	}
	@Override
	public GroupDetails groupnameExists(String clientid, String groupname) {

		return groupDetailsRepository.findByClientidAndGroupnameIgnoreCase(clientid, groupname);
	}
	
	@Override
	public GroupDetails subgroupnameExists(String clientid, String groupname) {

		return groupDetailsRepository.findByClientidAndSubgroups_GroupnameIgnoreCase(clientid, groupname);
	}
	@Override
	public List<GroupDetails> addGroupInfo(String clientid, String groupname, String headname, String parentId,String subgroupid,String pathname) {
		
		List<GroupDetails> details=groupDetailsRepository.findByClientid(clientid);
		//Groups grps = groupsRepository.findByName(headname);
		
		boolean isgroup=false;
		if (isNotEmpty(parentId)) {
			  if (isNotEmpty(details)) { 
				  ObjectId objId = new ObjectId(parentId);
			 if(isEmpty(subgroupid)) {
				 subgroupid = parentId;
			 }
			  for (GroupDetails groupDtls : details) { 
				  if(groupDtls.getId().equals(objId)) {
					  Subgroups subgroup = new Subgroups(); 
					  subgroup.setGroupname(groupname);
					  subgroup.setHeadname(headname); 
					  subgroup.setClientid(clientid);
					  subgroup.setSubgroupid(subgroupid);
					  subgroup.setPath(pathname);
					  //subgroup.setPath(groups.getHeadname());
					  groupDtls.getSubgroups().add(subgroup); 
					 
					  }
				  }
			 
			  }
			  }else { 
				 
				  GroupDetails group = new GroupDetails();
				  group.setGroupname(groupname); 
				  group.setHeadname(headname);
				  group.setClientid(clientid);
////////// add groups pathset
			AccountingHeads accHead = accountingHeadsRepository.findByHeads_name(headname);
			boolean flag = false;
			if(isNotEmpty(accHead) && isNotEmpty(headname)) {
				for(AccountingHeads head : accHead.getHeads()) {
					if(headname.equals(head.getName())) {
						group.setPath(head.getPath()+"/"+groupname);							
						flag = true;
						break;
					}
				}
			}
			if(!flag) {
				group.setPath(headname+"/"+groupname);
			}

				  details.add(group);
				  
				  }
			
		
		return groupDetailsRepository.save(details);
	}

	@Override
	public List<GroupDetails> updateGroupInfo(String clientid, String id, String groupname, String headname,
			String parentId) {
		List<GroupDetails> groupDetails=groupDetailsRepository.findByClientid(clientid);
		ObjectId objId = new ObjectId(id);

		if (isNotEmpty(parentId)) {
			if (isNotEmpty(groupDetails)) {
				ObjectId objParentId = new ObjectId(parentId);
				List<String> grpsugrpnames=new ArrayList<String>();
				for (GroupDetails grpDtls : groupDetails) {
					if (grpDtls.getId().equals(objParentId) && isNotEmpty(grpDtls.getSubgroups())) {
						
						String oldsubgrpname=null;
						String oldheadname=null;
						grpsugrpnames.add(grpDtls.getGroupname());
						
						for (Subgroups subgrp : grpDtls.getSubgroups()) {
							
							grpsugrpnames.add(subgrp.getGroupname());
							
							if (subgrp.getId().equals(objId)) {
								String oldpath=subgrp.getPath();
								oldsubgrpname=subgrp.getGroupname();
								oldheadname=subgrp.getHeadname();
								String newpath=oldpath.replaceAll(subgrp.getGroupname(), groupname);
								newpath=newpath.replaceAll(subgrp.getHeadname(), headname);
								subgrp.setGroupname(groupname);
								subgrp.setHeadname(headname);
								subgrp.setPath(newpath);
							}
						}
						if(isNotEmpty(oldsubgrpname)) {
							for (Subgroups subgrp : grpDtls.getSubgroups()) {
								if (!subgrp.getId().equals(objId)) {
									String subgrpHoldpath=subgrp.getPath();
									String path=null;
									if(subgrpHoldpath.contains(oldheadname)) {
										path=subgrpHoldpath.replaceAll(oldheadname, headname);
									}
									if(subgrpHoldpath.contains(oldsubgrpname)) {
										if(isNotEmpty(path)) {
											String newpath=path.replaceAll(oldsubgrpname, groupname);
											subgrp.setPath(newpath);
										}else {
											String newpath=subgrpHoldpath.replaceAll(oldsubgrpname, groupname);
											subgrp.setPath(newpath);
										}
									}								
								}
							}
						}
						
						List<ProfileLedger> ledgersLst=ledgerRepository.findByClientidAndGrpsubgrpNameIn(clientid, grpsugrpnames);
						
						if(isNotEmpty(ledgersLst)){
							List<ProfileLedger> ldgrLst=new ArrayList<ProfileLedger>();
							for(ProfileLedger ledgr:ledgersLst) {
								String ledgerHoldpath=ledgr.getLedgerpath();
								String path=null;
								if(ledgerHoldpath.contains(oldheadname)) {
									path=ledgerHoldpath.replaceAll(oldheadname, headname);
								}
								if(ledgerHoldpath.contains(oldsubgrpname)) {
									if(isNotEmpty(path)) {
										String newpath=path.replaceAll(oldsubgrpname, groupname);
										ledgr.setLedgerpath(newpath);
									}else {
										String newpath=ledgerHoldpath.replaceAll(oldsubgrpname, groupname);
										ledgr.setLedgerpath(newpath);
									}
								}	
								ldgrLst.add(ledgr);
							}
							ledgerRepository.save(ldgrLst);
						}
					}
				}
			}
		} else if (isNotEmpty(groupDetails)) {
			String oldgrpname=null;
			
			for (GroupDetails group : groupDetails) {
				
				List<String> grpsugrpnames=new ArrayList<String>();
				if (group.getId().equals(objId)) {
					
					oldgrpname=group.getGroupname();
					grpsugrpnames.add(groupname);
					
					group.setGroupname(groupname);
					group.setHeadname(headname);
					
					String oldpath=group.getPath();
					String newpath=headname + "/" + groupname;
					
					AccountingHeads accHead = accountingHeadsRepository.findByHeads_name(headname);
					boolean flag = false;
					if(isNotEmpty(accHead) && isNotEmpty(headname)) {
						for(AccountingHeads head : accHead.getHeads()) {
							if(headname.equals(head.getName())) {
								group.setPath(head.getPath()+"/"+groupname);							
								flag = true;
								break;
							}
						}
					}
					if(!flag) {
						group.setPath(newpath);
					}
					
					////////// groups edit pathset
					
					List<Subgroups> subgrp = group.getSubgroups();
					List<Subgroups> subgrplst = new ArrayList<Subgroups>();
					if (isNotEmpty(subgrp)) {
						for (Subgroups subgroup : subgrp) {
							String replacepath=subgroup.getPath().replaceAll(oldpath, newpath);
							subgroup.setPath(replacepath);
							if(subgroup.getHeadname().equalsIgnoreCase(oldgrpname)) {
								subgroup.setHeadname(groupname);
							}
							
							grpsugrpnames.add(subgroup.getGroupname());
							
							subgrplst.add(subgroup);							
						}
						group.setSubgroups(subgrplst);
					}
					
					List<ProfileLedger> ledgersLst=ledgerRepository.findByClientidAndGrpsubgrpNameIn(clientid, grpsugrpnames);
					
					if(isNotEmpty(ledgersLst)){
						List<ProfileLedger> ldgrLst=new ArrayList<ProfileLedger>();
						for(ProfileLedger ledgr:ledgersLst) {
							String replacepath=ledgr.getLedgerpath().replaceAll(oldpath, newpath);
							ledgr.setLedgerpath(replacepath);
							
							if(ledgr.getGrpsubgrpName().equalsIgnoreCase(oldgrpname)) {
								ledgr.setGrpsubgrpName(groupname);
							}
							
							ldgrLst.add(ledgr);
						}
						ledgerRepository.save(ldgrLst);
					}
					
				}
			}
		}

		return groupDetailsRepository.save(groupDetails);
	}

	@Override
	public void deleteSubGroup(String subGroupid, String Groupid, String clientid) {
		List<GroupDetails> groups = groupDetailsRepository.findAll();
		GroupDetails group = groupDetailsRepository.findOne(Groupid);

		List<Subgroups> subgroups = group.getSubgroups();
		
		List<String> grpnameLst=new ArrayList<String>();
		String path=null;
		if(isNotEmpty(subgroups)) {
			for(Subgroups subgrp:subgroups) {
				if (subGroupid.equals(subgrp.getId().toString())) {
					path=subgrp.getPath();
				}	
			}			
		}
		
		Iterator<Subgroups> it = subgroups.iterator();

		while (it.hasNext()) {
			Subgroups sub = it.next();
			if (sub.getPath().contains(path)) {
				grpnameLst.add(sub.getGroupname());
				it.remove();
			}
		}
		List<ProfileLedger> ledgersLst= ledgerRepository.findByClientidAndGrpsubgrpNameIn(group.getClientid(), grpnameLst);
		
		ledgerRepository.delete(ledgersLst);

		group.setSubgroups(subgroups);
		groupDetailsRepository.save(group);
	}

	@Override
	public ProfileLedger ledgerNameExists(String clientid, String ledgername) {
		
		return ledgerRepository.findByClientidAndLedgerNameIgnoreCase(clientid, ledgername);
	}

	
	@Override
	public ProfileLedger saveLegerDetails(ProfileLedger ledger) {
		return ledgerRepository.save(ledger);
	}

	@Override
	public List<ProfileLedger> getLedgerDetails(String clientid) {
		
		return ledgerRepository.findByClientid(clientid);
	}
	
	@Override
	public List<ProfileLedger> getLedgerDetails(String clientid,List<String> subgroupNames) {
		
		return ledgerRepository.findByClientidAndGrpsubgrpNameIn(clientid,subgroupNames);
	}

	@Override
	public void deleteLedger(String ledgerid) {
		ledgerRepository.delete(ledgerid);
		
	}

	@Override
	public EcommerceOpeartor saveOperator(EcommerceOpeartor operator) {
		
		return ecommerceOperatorRepository.save(operator);
	}

	@Override
	public List<EcommerceOpeartor> getOperators(String clientid) {
		
		return ecommerceOperatorRepository.findByClientid(clientid);
	}
	
	@Override
	public void deleteOperator(String operatorid) {
		
		ecommerceOperatorRepository.delete(operatorid);
	}
	
	@Override
	@Transactional
	public List<EcommerceOpeartor> getSearchedOperators(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedOperators : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "gstnnumber"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				new Criteria().orOperator(Criteria.where("gstnnumber").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)))
			));
		
		
		return mongoTemplate.find(query, EcommerceOpeartor.class, "ecommerceoperator");
	}
	
	@Override
	public void deleteAdminUser(String id) {
		
		CompanyUser companyUser = companyUserRepository.findOne(id);
		if(isNotEmpty(companyUser)) {
			DeletedCompanyUser deleteCompanyUser = new DeletedCompanyUser();
			if(isNotEmpty(companyUser.getUserid())) {
				deleteCompanyUser.setUserid(companyUser.getUserid());
			}
			if(isNotEmpty(companyUser.getFullname())) {
				deleteCompanyUser.setFullname(companyUser.getFullname());
			}
			if(isNotEmpty(companyUser.getClientid())) {
				deleteCompanyUser.setClientid(companyUser.getClientid());
			}
			if(isNotEmpty(companyUser.getTeamuserid())) {
				deleteCompanyUser.setTeamuserid(companyUser.getTeamuserid());
			}
			if(isNotEmpty(companyUser.getName())) {
				deleteCompanyUser.setName(companyUser.getName());
			}
			if(isNotEmpty(companyUser.getPassword())) {
				deleteCompanyUser.setPassword(companyUser.getPassword());
			}
			if(isNotEmpty(companyUser.getCompany())) {
				deleteCompanyUser.setCompany(companyUser.getCompany());
			}
			if(isNotEmpty(companyUser.getCenters())) {
				deleteCompanyUser.setCenters(companyUser.getCenters());
			}
			if(isNotEmpty(companyUser.getBranch())) {
				deleteCompanyUser.setBranch(companyUser.getBranch());
			}
			if(isNotEmpty(companyUser.getVertical())) {
				deleteCompanyUser.setVertical(companyUser.getVertical());
			}
			if(isNotEmpty(companyUser.getRole())) {
				deleteCompanyUser.setRole(companyUser.getRole());
			}
			if(isNotEmpty(companyUser.getGroup())) {
				deleteCompanyUser.setGroup(companyUser.getGroup());
			}
			if(isNotEmpty(companyUser.getEmail())) {
				deleteCompanyUser.setEmail(companyUser.getEmail());
			}
			if(isNotEmpty(companyUser.getNumberofclients())) {
				deleteCompanyUser.setNumberofclients(companyUser.getNumberofclients());
			}
			if(isNotEmpty(companyUser.getAddedclients())) {
				deleteCompanyUser.setAddedclients(companyUser.getAddedclients());
			}
			if(isNotEmpty(companyUser.getMobile())) {
				deleteCompanyUser.setMobile(companyUser.getMobile());
			}
			if(isNotEmpty(companyUser.getIsglobal())) {
				deleteCompanyUser.setIsglobal(companyUser.getIsglobal());
			}
			if(isNotEmpty(companyUser.getDisable())) {
				deleteCompanyUser.setDisable(companyUser.getDisable());
			}
			if(isNotEmpty(companyUser.getAddsubuser())) {
				deleteCompanyUser.setAddsubuser(companyUser.getAddsubuser());
			}
			deletedCompanyUserRepository.save(deleteCompanyUser);
			User usr = userService.findByEmail(companyUser.getEmail());
			if(isNotEmpty(usr)) {
				DeletedUsers deletedUser = new DeletedUsers(); 
				if(isNotEmpty(usr.getFullname())) {
					deletedUser.setFullname(usr.getFullname());
				}
				if(isNotEmpty(usr.getPassword())) {
					deletedUser.setPassword(usr.getPassword());
				}
				if(isNotEmpty(usr.getEmail())) {
					deletedUser.setEmail(usr.getEmail());
				}
				if(isNotEmpty(usr.getRefid())) {
					deletedUser.setRefid(usr.getRefid());
				}
				if(isNotEmpty(usr.getMobilenumber())) {
					deletedUser.setMobilenumber(usr.getMobilenumber());
				}
				if(isNotEmpty(usr.getType())) {
					deletedUser.setType(usr.getType());
				}
				if(isNotEmpty(usr.getIsglobal())) {
					deletedUser.setIsglobal(usr.getIsglobal());
				}
				if(isNotEmpty(usr.getDisable())) {
					deletedUser.setDisable(usr.getDisable());
				}
				if(isNotEmpty(usr.getParentid())) {
					deletedUser.setParentid(usr.getParentid());
				}
				if(isNotEmpty(usr.getLastLoggedIn())) {
					deletedUser.setLastLoggedIn(usr.getLastLoggedIn());
				}
				if(isNotEmpty(usr.isAccessDrive())) {
					deletedUser.setAccessDrive(usr.isAccessDrive());
				}
				if(isNotEmpty(usr.isAccessImports())) {
					deletedUser.setAccessImports(usr.isAccessImports());
				}
				if(isNotEmpty(usr.isAccessReports())) {
					deletedUser.setAccessReports(usr.isAccessReports());
				}
				if(isNotEmpty(usr.isAccessJournalExcel())) {
					deletedUser.setAccessJournalExcel(usr.isAccessJournalExcel());
				}
				if(isNotEmpty(usr.getUserSequenceid())) {
					deletedUser.setUserSequenceid(usr.getUserSequenceid());
				}
				if(isNotEmpty(usr.getOtpVerified())) {
					deletedUser.setOtpVerified(usr.getOtpVerified());
				}
				if(isNotEmpty(usr.getCategory())) {
					deletedUser.setCategory(usr.getCategory());
				}
				deletedUserRepository.save(deletedUser);
			}
		}
		if(isNotEmpty(companyUser)) {
			userService.deleteUser(companyUser.getEmail());
		}
		companyUserRepository.delete(id);
	}
	
	@Override
	public AllGSTINTempData saveGstno(AllGSTINTempData gstno) {
		return allgstnoTempRepository.save(gstno);
	}
	
	@Override
	public List<AllGSTINTempData> getGstnum(final String userid) {
		logger.debug(CLASSNAME + "getGstno : Begin");
		return allgstnoTempRepository.findByUserid(userid);
	}
	
	@Override
	public List<GSTINPublicData> getpublicgstnum(final String userid) {
		logger.debug(CLASSNAME + "getGstno : Begin");
		List<MultiGSTNData> multigstndata = multiGSTnRepository.findByUserid(userid);
		List<String> gstnids = Lists.newArrayList();
		if(isNotEmpty(multigstndata)) {
			for(MultiGSTNData multigstdata : multigstndata) {
				if(isNotEmpty(multigstdata.getGstnid())) {
					gstnids.add(multigstdata.getGstnid());
				}
			}
		}
		List<GSTINPublicData> datalst=Lists.newArrayList(gstinPublicDataRepository.findAll(gstnids));
		 datalst.stream().forEach(data->{
			 data.setGstinpublicdataid(data.getId().toString());
		 });
		return datalst;
	}

	@Transactional
	public void updateMultiGstnoExcelData(final MultipartFile file, final String id, 
			final String fullname) throws IllegalArgumentException, IOException {
		logger.debug(CLASSNAME + "updateMultiGstnoExcelData : Begin");
		
		User user=userService.findById(id);
		
		String parentid=null;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())) {
			parentid=user.getParentid();
		}
		
		if(!file.isEmpty()) {
			ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
	        Workbook workbook;
	        if (file.getOriginalFilename().endsWith("xls") || file.getOriginalFilename().endsWith("XLS")) {
                workbook = new HSSFWorkbook(bis);
            } else if (file.getOriginalFilename().endsWith("xlsx") || file.getOriginalFilename().endsWith("XLSX")) {
                workbook = new XSSFWorkbook(bis);
            } else {
                throw new IllegalArgumentException("Received file does not have a standard excel extension.");
            }
	        
	        if(isNotEmpty(workbook) && workbook.getNumberOfSheets() > 0) {
	        	int lastRowIndex=workbook.getSheetAt(0).getLastRowNum();
        		if(lastRowIndex > 0) {
            		int startRowIndex=workbook.getSheetAt(0).getFirstRowNum();
            		Row firstRow = workbook.getSheetAt(0).getRow(startRowIndex);
            		int columns=firstRow.getLastCellNum()-firstRow.getFirstCellNum();
            		logger.debug(CLASSNAME + "updateExcelData : No of Columns {}", columns);
            		
            		List<AllGSTINTempData> gstnum = Lists.newArrayList();
            		if(columns >= 0) {
                		for(int rowIndex=startRowIndex+1;rowIndex<=lastRowIndex;rowIndex++) {
                    		Row row = workbook.getSheetAt(0).getRow(rowIndex);
                    		if(isNotEmpty(row)) {
	                    		AllGSTINTempData gstnumx = new AllGSTINTempData();
	                    		gstnumx.setUserid(id);
	                    		gstnumx.setParentid(parentid);
	                    		String gstno = "";
	                    		if(row.getCell(row.getFirstCellNum()).getCellType() == Cell.CELL_TYPE_STRING) {
	                    			gstnumx.setGstno(row.getCell(row.getFirstCellNum()).getStringCellValue());
	                    			gstno = row.getCell(row.getFirstCellNum()).getStringCellValue();
	                    		} else {
	                    			gstnumx.setGstno(row.getCell(row.getFirstCellNum()).getNumericCellValue()+"");
	                    			gstno = row.getCell(row.getFirstCellNum()).getNumericCellValue()+"";
	                    		}
                    			AllGSTINTempData existGSTN = allgstnoTempRepository.findByUseridAndGstnoAndStatusNotNull(id,gstno);
                    			AllGSTINTempData tempgst = null;
                				if(NullUtil.isNotEmpty(existGSTN)) {
                					allgstnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstno);
                					tempgst = allgstnoTempRepository.save(existGSTN);
                				}else {
                					allgstnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstno);
                					tempgst = allgstnoTempRepository.save(gstnumx);
                				}
                    			if(NullUtil.isNotEmpty(tempgst.getGstno())) {
                    				multiGSTnRepository.deleteByUseridAndGstin(id,tempgst.getGstno());
                    				MultiGSTNData mgstndata = new MultiGSTNData();
                    				mgstndata.setGstin(tempgst.getGstno());
                    				mgstndata.setUserid(id);
                    				mgstndata.setGstnid(tempgst.getId().toString());
                    				multiGSTnRepository.save(mgstndata);
                    			}
                    		}
                		}
            		}
            	}
            }
            
		}
		logger.debug(CLASSNAME + "updateMultiGstnoExcelData : End");
	}
	@Override
	public String getAcknowledgementPermissions(String email){
		logger.debug(CLASSNAME + "getAcknowledgementPermissions : Begin");
		logger.debug(CLASSNAME + "getAcknowledgementPermissions Email\t:"+email);
	
		String status=null;
		
		CompanyUser cmpyUser= companyUserRepository.findByEmail(email);
		
		if(isNotEmpty(cmpyUser) && isNotEmpty(cmpyUser.getRole())) {
			
			CompanyRole companyRole= companyRoleRepository.findByUseridAndId(cmpyUser.getUserid(), cmpyUser.getRole());
			
			if(isNotEmpty(companyRole)) {
				
				if(isNotEmpty(companyRole.getPermissions()) && isNotEmpty(companyRole.getPermissions().get("Acknowledgement")) && companyRole.getPermissions().get("Acknowledgement").size() > 0) {
					
					for(Permission permission :companyRole.getPermissions().get("Acknowledgement")) {
						
						if(isNotEmpty(permission) && isNotEmpty(permission.getName())) {
							
							if(isNotEmpty(permission.getStatus())) {
								//Acknowledgement User (On selecting this User Can View Only Acknowledgement Window)
								//Acknowledgements (On Selecting this User Can View Acknowledgement feature along with MasterGST Features)
								
								if("Acknowledgement User".equalsIgnoreCase(permission.getName()) && "Yes".equalsIgnoreCase(permission.getStatus())) {
									status ="Acknowledgement User";
								}
								if("Acknowledgements".equalsIgnoreCase(permission.getName()) && "Yes".equalsIgnoreCase(permission.getStatus())) {
									status ="Acknowledgements";
								}
							}	
						}
					}
				}
			}
		}
		logger.debug(CLASSNAME + "getAcknowledgementPermissions : End");
		return status;
	}
	
	@Override
	public String getAcknowledgementRoles(String roleid) {
		String status=null;
		CompanyRole companyRole= companyRoleRepository.findById(roleid);
		
		if(isNotEmpty(companyRole)) {
			if(isNotEmpty(companyRole.getPermissions()) && isNotEmpty(companyRole.getPermissions().get("Acknowledgement")) && companyRole.getPermissions().get("Acknowledgement").size() > 0) {
				for(Permission permission :companyRole.getPermissions().get("Acknowledgement")) {
					if(isNotEmpty(permission) && isNotEmpty(permission.getName())) {
						
						if(isNotEmpty(permission.getStatus())) {
							//Acknowledgement User (On selecting this User Can View Only Acknowledgement Window)
							//Acknowledgements (On Selecting this User Can View Acknowledgement feature along with MasterGST Features)
							
							if("Acknowledgement User".equalsIgnoreCase(permission.getName()) && "Yes".equalsIgnoreCase(permission.getStatus())) {
								status ="Acknowledgement User";
								break;
							}
						}	
					}
				}
			}
		}
		
		return status;
	}
	
	/* public boolean isStorageCredentialsEntered(String userId){
		List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(userId);
		if(clientUserMappings == null){
			return false;
		}
		ClientUserMapping clientUserMapping = clientUserMappings.get(0);//>0--true
		return isStorageCredentialsEnteredForClient(clientUserMapping.getClientid());
	} */
	
	public boolean isStorageCredentialsEntered(String userId){
		boolean iscredentials= false;
		List<StorageCredentials> scredentials = storageCredentialsDao.getStorageCredentialsBasedOnId(userId);
		if(scredentials.size() > 0){
			iscredentials = true;
		}else{
			iscredentials = false;
		}
		return iscredentials;
	}

	public boolean isStorageCredentialsEnteredForClient(String clientId){
		StorageCredentials storageCredentials = storageCredentialsDao.getStorageCredentials(clientId);
		return storageCredentials != null;
	}

	@Override
	public Page<? extends CompanyCustomers> getCustomersDetails(Pageable pageable, String clientid, int month, int year,
			int start, int length, String searchVal) {
		Page<? extends CompanyCustomers> customers = null;
		customers = profileDao.findByClientid(clientid,start, length, searchVal);
		return customers;
	}
	
	@Override
	public Page<? extends CompanySuppliers> getSupplierDetails(Pageable pageable, String clientid, int month, int year,
			int start, int length, String searchVal) {
		Page<? extends CompanySuppliers> suppliers = null;
		suppliers = profileDao.findByid(clientid,start, length, searchVal);
		return suppliers;
	}

	
	
	@Override
	public Page<Client> getRemindersClients(String id, int start, int length, String searchVal){
		
		List<String> clientids = clientService.fetchClientIds(id);
		
		return clientDao.getClients(clientids, start, length, searchVal);
	}
	
	@Override
	public Page<CompanyCustomers> getRemindersCustomers(String id, String[] clientids, int start, int length, String searchVal){
		List<String> clientidsLst=null;
		if(clientids !=null) {
			clientidsLst=Arrays.asList(clientids);
		}else {
			clientidsLst= clientService.fetchClientIds(id);
		}
		return customersDao.getCUstomers(clientidsLst, start, length, searchVal);
	}
	
	@Override
	public Page<CompanySuppliers> getRemindersSuppliers(String id, String[] clientids, int start, int length, String searchVal){
		List<String> clientidsLst=null;
		if(clientids !=null) {
			clientidsLst=Arrays.asList(clientids);
		}else {
			clientidsLst= clientService.fetchClientIds(id);
		}
		return suppliersDao.getSuppiers(clientidsLst, start, length, searchVal);
	}
	
	@Override
	public CompanySuppliers getCompanySuppliers(String id){
		
		return suppliersDao.getSuppiersById(id);
	}
	@Override
	public CompanyCustomers getCompanyCustomers(String id){
		
		return customersDao.getCustomersById(id);
	}
	
	@Override
	public Page<? extends MultiGSTNData> getmultiGstnDetails(Pageable pageable, String userid, int month, int year,
			int start, int length, String searchVal) {
		Page<? extends MultiGSTNData> gstndetails = null;
		gstndetails = multiGSTNDao.findByUserid(userid,start, length, searchVal);
		return gstndetails;
	}
	
	public Map<String, List<String>> dbRolesPrivilegesMap(){
		Map<String, List<String>> dbRolesMap = new HashMap<String, List<String>>();

		dbRolesMap.put("Invoices", Arrays.asList("Sales","Purchase"));
		//dbRolesMap.put("GSTR1", Arrays.asList("Sales"));
		dbRolesMap.put("GSTR2", Arrays.asList("GSTR2", "MisMatched", "GSTR2A"));
		dbRolesMap.put("GSTR3B", Arrays.asList("GSTR3B"));
		dbRolesMap.put("GSTR4", Arrays.asList("GSTR4"));
		dbRolesMap.put("GSTR5", Arrays.asList("GSTR5"));
		dbRolesMap.put("GSTR6", Arrays.asList("GSTR6"));

		dbRolesMap.put("General", Arrays.asList("Bulk Imports", "Import Templates"));
		dbRolesMap.put("New Returns", Arrays.asList("Ewaybill", "E-invoice"));
		
		return dbRolesMap;
	}
	
	
	@Override
	@Transactional
	public List<DashboardRoles> getUserDashboardClientPermissions(final String userid, final List<String> clientids) {
		logger.debug(CLASSNAME + "getUserDashboardClientPermissions : Begin");
		User user = userRepository.findById(userid);
		List<DashboardRoles> cdbRoles = Lists.newArrayList();
				
		if(isNotEmpty(user) && isNotEmpty(user.getCategory()) && "Child".equalsIgnoreCase(user.getCategory())) {
			for(String clientid: clientids) {
				CompanyUser companyUser = getCompanyUser(user.getEmail(), clientid);
				
				List<String> rList = Lists.newArrayList();
				Map<String, List<String>> dbRolesMap = dbRolesPrivilegesMap();
				String cUrole = "";
				if(isNotEmpty(companyUser)) {
					ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(user.getId().toString(), clientid);
					if(isNotEmpty(clientuserMapping) && isNotEmpty(clientuserMapping.getRole())) {
						cUrole =  clientuserMapping.getRole();
					}
				}
				if(isNotEmpty(cUrole)) {
					CompanyRole role = companyRoleRepository.findOne(cUrole);
					if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
						logger.debug(CLASSNAME + "getUserDashboardClientPermissions : is not empty");
						//System.out.println(clientid +" ::"+role.getClientid()+" ::"+role.getId().toString());
						Map<String, List<Permission>> roleMap = role.getPermissions();
						
						if(roleMap.size() > 0){
							roleMap.forEach((key, val) ->{
								if(dbRolesMap.containsKey(key)){
									val.forEach( per -> {
										
										List<String> dbrper = dbRolesMap.get(key);
										if(dbrper.contains(per.getName())){
											if("No".equalsIgnoreCase(per.getStatus()) || "View-No".equalsIgnoreCase(per.getStatus())){
												rList.add(per.getName());
											}										
										}
										
										
									});
									dbRolesMap.remove(key);
								}
							});
						}
						if(dbRolesMap.size() > 0) {
							dbRolesMap.forEach((key, val) ->{
								rList.addAll(val);
							});
						}
						//System.out.println(rList.toString());
						if(isNotEmpty(rList) && rList.size()>0) {
							DashboardRoles drole = new DashboardRoles();
							drole.setClientid(clientid);
							drole.setUserid(userid);
							drole.setRoles(rList);
							cdbRoles.add(drole);
						}
						//return cdbRoles;
					}else{
						logger.debug(CLASSNAME + "getUserDashboardClientPermissions : is empty");
						//return Lists.newArrayList();
					}
					//return cdbRoles;
				} else if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getUserid())) {
					User superUser = userService.getUser(companyUser.getUserid());
					if(isNotEmpty(superUser)) {
						CompanyUser companyParent = getCompanyUser(user.getEmail(), clientid);
						String cPUrole = "";
						if(isNotEmpty(companyParent)) {
							ClientUserMapping clientuserMapping = clientUserMappingRepository.findByUseridAndClientid(superUser.getId().toString(), clientid);
							if(isNotEmpty(clientuserMapping) && isNotEmpty(clientuserMapping.getRole())) {
								cPUrole =  clientuserMapping.getRole();
							}
						}
						if(isNotEmpty(cPUrole)) {
							CompanyRole role = companyRoleRepository.findOne(cPUrole);
							if(isNotEmpty(role) && isNotEmpty(role.getPermissions())) {
								
								Map<String, List<Permission>> roleMap = role.getPermissions();
								
								if(roleMap.size() > 0){
									roleMap.forEach((key, val) ->{
										if(dbRolesMap.containsKey(key)){
											val.forEach( per -> {
												
												List<String> dbrper = dbRolesMap.get(key);
												if(dbrper.contains(per.getName())){
													if("No".equalsIgnoreCase(per.getStatus()) || "View-No".equalsIgnoreCase(per.getStatus())){
														rList.add(per.getName());
													}										
												}
											});
											dbRolesMap.remove(key);
										}
									});
								}
								if(dbRolesMap.size() > 0) {
									dbRolesMap.forEach((key, val) ->{
										rList.addAll(val);
									});
								}
								if(isNotEmpty(rList) && rList.size()>0) {
									DashboardRoles drole = new DashboardRoles();
									drole.setClientid(clientid);
									drole.setUserid(userid);
									drole.setRoles(rList);
									cdbRoles.add(drole);
								}
								//return cdbRoles;
							} else {
								logger.debug(CLASSNAME + "getUserDashboardClientPermissions : is empty");
								//return Lists.newArrayList();
							}
						}
					}
				}
			}
		}
		return cdbRoles;
		//return Lists.newArrayList();
	}

	@Override
	public CompanyBankDetails saveBankDetailsQRCode(CompanyBankDetails bankdetails,MultipartFile file) {
		logger.debug(CLASSNAME + "saveBankDetails : Begin {}", bankdetails);
		
		CompanyBankDetails bankdetailss = companyBankDetailsRepository.save(bankdetails);
		String ledgerName="";
		ProfileLedger lgrdr = null;
		if(isNotEmpty(bankdetails.getBankname())) {
			ledgerName = bankdetails.getBankname();
		}
		if(isNotEmpty(bankdetails.getAccountnumber())) {
			String accno = bankdetails.getAccountnumber().substring(bankdetails.getAccountnumber().length() - 4);
			ledgerName = ledgerName+"-"+accno;
		}
		
		ProfileLedger oldDetails = ledgerRepository.findByClientidAndBankId(bankdetails.getClientid(), bankdetailss.getId().toString());
		if(isNotEmpty(oldDetails)) {
			oldDetails.setClientid(bankdetails.getClientid());	
			oldDetails.setLedgerName(ledgerName);
			oldDetails.setGrpsubgrpName(AccountConstants.BANK_ACCOUNTS);
			oldDetails.setLedgerpath(AccountConstants.BANK_DEFAULT_PATH);
			oldDetails.setBankId(bankdetailss.getId().toString());
			oldDetails.setLedgerOpeningBalance(0);
			ledgerRepository.save(oldDetails);
		}else {
			lgrdr = new ProfileLedger();
			lgrdr.setClientid(bankdetails.getClientid());	
			lgrdr.setLedgerName(ledgerName);
			lgrdr.setGrpsubgrpName(AccountConstants.BANK_ACCOUNTS);
			lgrdr.setLedgerpath(AccountConstants.BANK_DEFAULT_PATH);
			lgrdr.setBankId(bankdetailss.getId().toString());
			lgrdr.setLedgerOpeningBalance(0);
			ledgerRepository.save(lgrdr);
		}
		
		
		if(file != null && isNotEmpty(file.getOriginalFilename())) {
			DBObject metaData = new BasicDBObject();
			metaData.put("clientid", bankdetailss.getId().toString());
			String contentType = file.getContentType();
			if(isEmpty(contentType)) {
				contentType = "image/png";
			}
			if(isNotEmpty(bankdetailss.getQrcodeid())) {
				gridOperations.delete(new Query(Criteria.where("_id").is(bankdetailss.getQrcodeid())));
			}
			try {
				String imageId = gridOperations.store(file.getInputStream(), file.getName(), contentType, metaData).getId().toString();
				bankdetailss.setQrcodeid(imageId);
			} catch (IOException e) {
			}
		}
		
		return companyBankDetailsRepository.save(bankdetailss);
	}
	
	@Override
	public void updateCompanyUsers(List<CompanyUser> companyUsers) {
		
		
		
		companyUserRepository.save(companyUsers);
	}
@Override
	public String saveCustomFieldsData(String rParam, String userid,String clientid,CustomFields oldCustomData, CustomData customdetails) {
		String message = null;
		List<CustomData> salesList = null;
		List<CustomData> purList = null;
		List<CustomData> ewaybillList = null;
		List<CustomData> einvList = null;
		List<CustomData> itemsList = null;
		CustomFields fields = null;
		if(isNotEmpty(customdetails)) {
			if(isNotEmpty(customdetails.getCustomFieldType()) && "input".equals(customdetails.getCustomFieldType())) {
				customdetails.setTypeData(new ArrayList<>());
			}
			if(isNotEmpty(oldCustomData)) {
				fields = oldCustomData;
			}else {
				fields = new CustomFields();
			}
			
			fields.setUserid(userid);
			fields.setClientid(clientid);
			if(isNotEmpty(rParam)) {
				if(rParam.equals("Sales")) {
					if(isNotEmpty(oldCustomData) && isNotEmpty(oldCustomData.getSales())) {
						salesList = oldCustomData.getSales();
					}else {
						salesList = Lists.newArrayList();
					}
					salesList.add(customdetails);
					fields.setSales(salesList);
				}else if(rParam.equals("Purchase")) {
					if(isNotEmpty(oldCustomData) && isNotEmpty(oldCustomData.getPurchase())) {
						purList = oldCustomData.getPurchase();
					}else {
						purList = Lists.newArrayList();
					}
					purList.add(customdetails);
					fields.setPurchase(purList);
				}else if(rParam.equals("Ewaybill")) {
					if(isNotEmpty(oldCustomData) && isNotEmpty(oldCustomData.getEwaybill())) {
						ewaybillList = oldCustomData.getEwaybill();
					}else {
						ewaybillList = Lists.newArrayList();
					}
					ewaybillList.add(customdetails);
					fields.setEwaybill(ewaybillList);
				}else if(rParam.equals("E-invoice")) {
					if(isNotEmpty(oldCustomData) && isNotEmpty(oldCustomData.getEinvoice())) {
						einvList = oldCustomData.getEinvoice();
					}else {
						einvList = Lists.newArrayList();
					}
					einvList.add(customdetails);
					fields.setEinvoice(einvList);
				}else if(rParam.equals("Items")) {
					if(isNotEmpty(oldCustomData) && isNotEmpty(oldCustomData.getItems())) {
						itemsList = oldCustomData.getItems();
					}else {
						itemsList = Lists.newArrayList();
					}
					itemsList.add(customdetails);
					fields.setItems(itemsList);
				}
			}
			
		}
		if((isNotEmpty(salesList) && salesList.size() > 4) || (isNotEmpty(purList) && purList.size() > 4)  || (isNotEmpty(ewaybillList) && ewaybillList.size() > 4)
				  || (isNotEmpty(einvList) && einvList.size() > 4)  || (isNotEmpty(itemsList) && itemsList.size() > 4)) {
			message = "You Can not add more than 4 "+rParam+" custom fields";
		}else {
			message = "";
			customFieldsRepository.save(fields);
		}
		return message;
	}
	@Override
	public String editCustomFieldsData(String rParam, String userid, String clientid, CustomFields beforeCustomData,CustomData customdetails,String oldCustomName,String oldCustomtype) {
		String message = null;
		List<CustomData> salesList = null;
		List<CustomData> purList = null;
		List<CustomData> ewaybillList = null;
		List<CustomData> einvList = null;
		List<CustomData> itemList = null;
		if(isNotEmpty(customdetails)) {
			if(isNotEmpty(customdetails.getCustomFieldType()) && "input".equals(customdetails.getCustomFieldType())) {
				customdetails.setTypeData(new ArrayList<>());
			}
			CustomFields fields = null;
			if(isNotEmpty(beforeCustomData)) {
				fields = beforeCustomData;
			}else {
				fields = new CustomFields();
			}
			
			fields.setUserid(userid);
			fields.setClientid(clientid);
			if(isNotEmpty(rParam)) {
				if(rParam.equals("Sales")) {
					if(isNotEmpty(fields) && isNotEmpty(fields.getSales())) {
						salesList = fields.getSales();
					}else {
						salesList = Lists.newArrayList();
					}
					boolean flag = false;
					if(isNotEmpty(salesList)) {
						for(CustomData data : salesList) {
							if(oldCustomName.equalsIgnoreCase(data.getCustomFieldName()) && oldCustomtype.equalsIgnoreCase(data.getCustomFieldType())) {
								data.setCustomFieldName(customdetails.getCustomFieldName());
								data.setCustomFieldType(customdetails.getCustomFieldType());
								data.setDisplayInPrint(customdetails.getDisplayInPrint());
								data.setIsMandatory(customdetails.getIsMandatory());
								data.setDisplayInFilters(customdetails.getDisplayInFilters());
								if(!data.getCustomFieldType().equalsIgnoreCase("input")) {
									data.setTypeData(customdetails.getTypeData());
								}else {
									data.setTypeData(new ArrayList<>());
								}
								flag = true;
							}
						}
					}	
					if(!flag) {
						salesList.add(customdetails);	
					}
					fields.setSales(salesList);
				}else if(rParam.equals("Purchase")) {
					if(isNotEmpty(fields) && isNotEmpty(fields.getPurchase())) {
						purList = fields.getPurchase();
					}else {
						purList = Lists.newArrayList();
					}
					boolean flag = false;
					if(isNotEmpty(purList)) {
						for(CustomData data : purList) {
							if(oldCustomName.equalsIgnoreCase(data.getCustomFieldName()) && oldCustomtype.equalsIgnoreCase(data.getCustomFieldType())) {
								data.setCustomFieldName(customdetails.getCustomFieldName());
								data.setCustomFieldType(customdetails.getCustomFieldType());
								data.setDisplayInPrint(customdetails.getDisplayInPrint());
								data.setIsMandatory(customdetails.getIsMandatory());
								data.setDisplayInFilters(customdetails.getDisplayInFilters());
								if(!data.getCustomFieldType().equalsIgnoreCase("input")) {
									data.setTypeData(customdetails.getTypeData());
								}else {
									data.setTypeData(new ArrayList<>());
								}
								flag = true;
							}
						}
					}	
					if(!flag) {
						purList.add(customdetails);	
					}
					fields.setPurchase(purList);
				}else if(rParam.equals("Ewaybill")) {
					if(isNotEmpty(fields) && isNotEmpty(fields.getEwaybill())) {
						ewaybillList = fields.getEwaybill();
					}else {
						ewaybillList = Lists.newArrayList();
					}
					boolean flag = false;
					if(isNotEmpty(ewaybillList)) {
						for(CustomData data : ewaybillList) {
							if(oldCustomName.equalsIgnoreCase(data.getCustomFieldName()) && oldCustomtype.equalsIgnoreCase(data.getCustomFieldType())) {
								data.setCustomFieldName(customdetails.getCustomFieldName());
								data.setCustomFieldType(customdetails.getCustomFieldType());
								data.setDisplayInPrint(customdetails.getDisplayInPrint());
								data.setIsMandatory(customdetails.getIsMandatory());
								data.setDisplayInFilters(customdetails.getDisplayInFilters());
								if(!data.getCustomFieldType().equalsIgnoreCase("input")) {
									data.setTypeData(customdetails.getTypeData());
								}else {
									data.setTypeData(new ArrayList<>());
								}
								flag = true;
							}
						}
					}	
					if(!flag) {
						ewaybillList.add(customdetails);	
					}
					fields.setEwaybill(ewaybillList);
				}else if(rParam.equals("E-invoice")) {
					if(isNotEmpty(fields) && isNotEmpty(fields.getEinvoice())) {
						einvList = fields.getEinvoice();
					}else {
						einvList = Lists.newArrayList();
					}
					boolean flag = false;
					if(isNotEmpty(einvList)) {
						for(CustomData data : einvList) {
							if(oldCustomName.equalsIgnoreCase(data.getCustomFieldName()) && oldCustomtype.equalsIgnoreCase(data.getCustomFieldType())) {
								data.setCustomFieldName(customdetails.getCustomFieldName());
								data.setCustomFieldType(customdetails.getCustomFieldType());
								data.setDisplayInPrint(customdetails.getDisplayInPrint());
								data.setIsMandatory(customdetails.getIsMandatory());
								data.setDisplayInFilters(customdetails.getDisplayInFilters());
								if(!data.getCustomFieldType().equalsIgnoreCase("input")) {
									data.setTypeData(customdetails.getTypeData());
								}else {
									data.setTypeData(new ArrayList<>());
								}
								flag = true;
							}
						}
					}	
					if(!flag) {
						einvList.add(customdetails);	
					}
					fields.setEinvoice(einvList);
				}else if(rParam.equals("Items")) {
					if(isNotEmpty(fields) && isNotEmpty(fields.getItems())) {
						itemList = fields.getItems();
					}else {
						itemList = Lists.newArrayList();
					}
					boolean flag = false;
					if(isNotEmpty(itemList)) {
						for(CustomData data : itemList) {
							if(oldCustomName.equalsIgnoreCase(data.getCustomFieldName()) && oldCustomtype.equalsIgnoreCase(data.getCustomFieldType())) {
								data.setCustomFieldName(customdetails.getCustomFieldName());
								data.setCustomFieldType(customdetails.getCustomFieldType());
								data.setDisplayInPrint(customdetails.getDisplayInPrint());
								data.setIsMandatory(customdetails.getIsMandatory());
								data.setDisplayInFilters(customdetails.getDisplayInFilters());
								if(!data.getCustomFieldType().equalsIgnoreCase("input")) {
									data.setTypeData(customdetails.getTypeData());
								}else {
									data.setTypeData(new ArrayList<>());
								}
								flag = true;
							}
						}
					}	
					if(!flag) {
						itemList.add(customdetails);	
					}
					fields.setItems(itemList);
				}
			}
			customFieldsRepository.save(fields);
		}
		if((isNotEmpty(salesList) && salesList.size() > 4) || (isNotEmpty(purList) && purList.size() > 4)  || (isNotEmpty(ewaybillList) && ewaybillList.size() > 4)
				  || (isNotEmpty(einvList) && einvList.size() > 4)  || (isNotEmpty(itemList) && itemList.size() > 4)) {
			message = "You Can not add more than 4 "+rParam+" custom fields";
		}else {
			message = "";
		}
		return message;
	}
	@Override
	public void deleteCustomFields(String id, String typeid, String clientid, String type) {
		CustomFields customData = customFieldsRepository.findOne(id);
		if(type.equalsIgnoreCase("Sales")) {
			List<CustomData> salesData = customData.getSales();
			if(isNotEmpty(salesData)) {
				Iterator<CustomData> it = salesData.iterator();
				while (it.hasNext()) {
					CustomData sub = it.next();
					if (typeid.equals(sub.getId().toString())) {
						it.remove();
					}
				}
			}
		}else if(type.equalsIgnoreCase("Purchase")) {
			List<CustomData> purchaseData = customData.getPurchase();
			if(isNotEmpty(purchaseData)) {
				Iterator<CustomData> it = purchaseData.iterator();
				while (it.hasNext()) {
					CustomData sub = it.next();
					if (typeid.equals(sub.getId().toString())) {
						it.remove();
					}
				}
			}
		}else if(type.equalsIgnoreCase("Einvoice")) {
			List<CustomData> einvoiceData = customData.getEinvoice();
			if(isNotEmpty(einvoiceData)) {
				Iterator<CustomData> it = einvoiceData.iterator();
				while (it.hasNext()) {
					CustomData sub = it.next();
					if (typeid.equals(sub.getId().toString())) {
						it.remove();
					}
				}
			}
		}else if(type.equalsIgnoreCase("Ewaybill")) {
			List<CustomData> ewaybillData = customData.getEwaybill();
			if(isNotEmpty(ewaybillData)) {
				Iterator<CustomData> it = ewaybillData.iterator();
				while (it.hasNext()) {
					CustomData sub = it.next();
					if (typeid.equals(sub.getId().toString())) {
						it.remove();
					}
				}
			}
		}
		
		customFieldsRepository.save(customData);
	}
	public void saveDefaultStocks(String id, CompanyItems item) {
		StockAdjustments stock = new StockAdjustments();
		stock.setClientid(item.getClientid());
		stock.setUserid(id);
		if(isNotEmpty(item)) {
			StockAdjustments dStock = stockAdjustmentRepository.findByClientidAndTransactionTypeAndItemId(item.getClientid(), "Opening Stock", item.getId());
			stock.setItemId(item.getId());
			if(isNotEmpty(item.getItemno())) {
				stock.setStockItemNo(item.getItemno());
			}
			if(isNotEmpty(item.getItemDescription())) {
				stock.setStockItemName(item.getItemDescription());
			}
			if(isNotEmpty(item.getItemgroupno())) {
				stock.setStockItemGrpCode(item.getItemgroupno());
			}
			if(isNotEmpty(item.getPurchasePrice())) {
				stock.setStockPurchaseCost(item.getPurchasePrice());
			}
			if(isNotEmpty(item.getGroupdescription())) {
				stock.setStockItemGrpName(item.getGroupdescription());
			}
			if(isNotEmpty(item.getUnit())) {
				stock.setStockUnit(item.getUnit());
			}
			if(isNotEmpty(item.getAsOnDate())) {
				stock.setDateOfMovement(item.getAsOnDate());
			}
			if(isNotEmpty(item.getOpeningStock())) {
				stock.setStockItemQty(item.getOpeningStock());
				stock.setCurrentStock(item.getOpeningStock());
			}
			stock.setStockMovement("In");
			Date dt = null;
			dt = (Date)item.getAsOnDate();
			if(isNotEmpty(dt)) {
				int month = dt.getMonth();
				int year = dt.getYear()+1900;
				int quarter = month/3;
				quarter = quarter == 0 ? 4 : quarter;
				String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
				month++;
				
				stock.setMthCd(""+month);
				stock.setYrCd(""+yearCode);
				stock.setQrtCd(""+quarter);
				stock.setSftr(1);
			}
			stock.setTransactionType("Opening Stock");
			if(isEmpty(dStock)) {
				stockAdjustmentRepository.save(stock);
			}
		}
	}

	@Override
	public Page<? extends CompanyItems> getItemDetails(Pageable pageable, String clientid, String type, int month, int year,
			int start, int length, String searchVal) {
		Page<? extends CompanyItems> items = null;
		items = profileItemDao.findByClientid(clientid, type, start, length, searchVal);
		return items;
	}

	@Override
	public List<String> ledgerNamesForBankCash(String clientid) {
		return accountingDao.getLedgerForBankCash(clientid);
	}
	
	@Override
	@Transactional
	public List<AccountingHeads> getHeadDetails(String clientid) {
		logger.debug(CLASSNAME + "getCustomers : Begin");
		return accountingHeadsRepository.findAll();
	}
} 
