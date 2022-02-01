package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.YES;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.AdminUsers;
import com.mastergst.usermanagement.runtime.domain.Permission;
import com.mastergst.usermanagement.runtime.service.AdminUserService;

@Controller
public class AdminUserController {
	
	private static final Logger logger = LogManager.getLogger(AdminUserController.class.getName());
	private static final String CLASSNAME = "AdminUserController::";
	
	private String[] arrPermissions = {"REPORTS","ERROR_LOG","MESSAGES","PAYMENTS","DETAILS","GST_PRODUCTION","EWAY_BILL_PRODUCTION","USER_TYPE_CHANGE","PARTNER_LINK","OTP_NOT_VERIFIED_USERS","PAID_EXPIRED_USERS","ALL_EXPIRED_USERS","RENEWAL","MONTHLY_INV_USAGE","CUSTOM_MONTHLY_API","ACTIVE_USERS","DEMO_USERS","SUBSCRIPTION_SUMMARY","MONTHLY_TAX_ITCSUM","GST_FILING_STATUS","GST_FILING_STATUS_SUM","HEADER_KEYS"};
	
	private String[] arrReports = {"PAID_EXPIRED_USERS","ALL_EXPIRED_USERS","RENEWAL","PARTNER_PAYMENTS","INVOICES_LIMIT_EXCEEDS_USERS","MONTHLY_INV_USAGE","CUSTOM_MONTHLY_API","API_VERSION_DOCUMENT","ACTIVE_USERS","DEMO_USERS","SUBSCRIPTION_SUMMARY","USER_SUMMARY"};
	
	private String[] arrMenus = {"USERS","REPORTS","ERROR_LOG","MESSAGES"};
	
	private String[] arrGeneral = {"PAYMENTS","DETAILS","GST_PRODUCTION","EWAY_BILL_PRODUCTION","USER_TYPE_CHANGE","PARTNER_LINK"};
	
	private String[] arrActions = {"EDIT","DELETE"};
	
	private String[] arrMessages = {"CREATE_MESSAGES","LATESTUPDATES","LATESTNEWS"};
	
	private String[] arrTabs = {"ALL","ASP","CAANDCMA","SMALLANDMEDIUM","ENTERPRISE","PARTNERS","SUVIDHAKENDRA","SUBUSERS","SUBCENTERS","TESTACCOUNTS","OTPNOTVERIFIED"};
	
	
	
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private UserService userService;
		
	@RequestMapping(value = "/superadminpage", method = RequestMethod.GET)
	public String superAdminPage(@RequestParam(value = "id", required = true) String id,
			@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String userType, ModelMap model) throws Exception {
		final String method = "superAdminPage::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		List<String> permissions=Arrays.asList(arrPermissions);	
		model.addAttribute("permissionsList", permissions);
		
		List<String> arrReportslst=Arrays.asList(arrReports);	
		model.addAttribute("arrReports", arrReportslst);
		List<String> menupermissions=Arrays.asList(arrMenus);	
		model.addAttribute("menupermissionsList", menupermissions);
		List<String> tabpermissions=Arrays.asList(arrTabs);	
		model.addAttribute("tabpermissionsList", tabpermissions);
		List<String> actpermissions=Arrays.asList(arrActions);	
		model.addAttribute("actpermissionsList", actpermissions);
		List<String> generalpermissions=Arrays.asList(arrGeneral);	
		model.addAttribute("genpermissionsList", generalpermissions);
		List<String> msgpermissions=Arrays.asList(arrMessages);	
		model.addAttribute("msgpermissionsList", msgpermissions);
		
		List<AdminUsers> adminUsersList=adminUserService.getAllAdminUsers();
		model.addAttribute("adminUsersList", adminUsersList);	
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", userType);
		logger.debug(CLASSNAME + method + END);
		
		return "admin/admin";
	}
	
	@RequestMapping(value="/saveAdminUserDetails",method=RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public String saveAdminUsersData(@ModelAttribute("adminUsers")AdminUsers adminUsers,
			@RequestParam(value = "id", required = true) String id,@RequestParam(value = "fullname", required = true) String fullname,
			@RequestParam(value = "usertype", required = true) String usertype,ModelMap map) throws UnsupportedEncodingException {
		
		final String method = "saveAdminUsersData::saveAdminUsersData::";
		logger.debug(CLASSNAME + method + BEGIN);
				
		//AdminUsers adminusers=adminUserService.getAdminUserData(adminUsers.getAdminUserId().toString());
		List<AdminUsers> adminUsersList=null;
		String emailid=adminUsers.getAdminUserEmail();
		
		AdminUsers adminusers=adminUserService.getAdminUserByAdminUserIdAndEmail(id, emailid);
			
		/*if(NullUtil.isNotEmpty(usr)) {
			System.out.println("user cannot create");
			
			logger.info("user cannot create emailid already exists");
			logger.debug("user cannot create emailid already exists");
			map.addAttribute("adminusererrormsg", "user unable to create");
		}*/
			//userService.createUser();
			
			if(NullUtil.isNotEmpty(adminusers)) {
				//update admin_user
				User usr =userService.findByEmail(emailid);
				usr.setEmail(usr.getEmail());
				usr.setFullname(adminUsers.getAdminUserName());				
				usr.setPassword(Base64.getEncoder().encodeToString(adminUsers.getAdminUserPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				usr.setParentid(id);
				usr.setType("subadmin");
				adminusers.setAdminUserId(id);
				adminusers.setAdminUserName(adminUsers.getAdminUserName());
				adminusers.setAdminUserEmail(adminusers.getAdminUserEmail());
				adminusers.setAdminUserPassword(Base64.getEncoder().encodeToString(adminUsers.getAdminUserPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				
				if (isNotEmpty(adminUsers.getPermissions())) {
					Map<String, List<Permission>> permissions = Maps.newHashMap();
					for (String feature : adminUsers.getPermissions().keySet()) {
						List<Permission> permissionList = Lists.newArrayList();
						for (Permission permission : adminUsers.getPermissions().get(feature)) {
							if (isNotEmpty(permission.getName())) {
								permission.setStatus(YES);
								permissionList.add(permission);
							}
						}
						permissions.put(feature, permissionList);
					}
					adminusers.setPermissions(permissions);
				}else {
					Map<String, List<Permission>> permissions = Maps.newHashMap();
					adminusers.setPermissions(permissions);
				}
				userService.createUser(usr);
				adminUsersList=adminUserService.saveAdminUsers(adminusers);
			}else {
				User user=new User();
				user.setFullname(adminUsers.getAdminUserName());
				user.setEmail(emailid);
				user.setType("subadmin");
				user.setPassword(Base64.getEncoder().encodeToString(adminUsers.getAdminUserPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				user.setParentid(id);
				
				AdminUsers admin_user = new AdminUsers();
				admin_user.setAdminUserEmail(adminUsers.getAdminUserEmail());
				admin_user.setAdminUserName(adminUsers.getAdminUserName());
				admin_user.setAdminUserPassword(Base64.getEncoder().encodeToString(adminUsers.getAdminUserPassword().getBytes(MasterGSTConstants.PASSWORD_ENCODE_FORMAT)));
				admin_user.setAdminUserId(id);
				if (isNotEmpty(adminUsers.getPermissions())) {
					Map<String, List<Permission>> permissions = Maps.newHashMap();
					for (String feature : adminUsers.getPermissions().keySet()) {
						List<Permission> permissionList = Lists.newArrayList();
						for (Permission permission : adminUsers.getPermissions().get(feature)) {
							if (isNotEmpty(permission.getName())) {
								permission.setStatus(YES);
								permissionList.add(permission);
							}
						}
						permissions.put(feature, permissionList);
					}
					admin_user.setPermissions(permissions);
				}
				adminUserService.createAdminUser(admin_user);
				adminUsersList=adminUserService.getAllAdminUsers();
				userService.createUser(user);
			}	
		map.addAttribute("adminUsersList", adminUsersList);
		logger.debug(CLASSNAME + method + END);
		return "redirect:/superadminpage?id="+id+"&fullname="+fullname+"&usertype="+usertype;
	}
	
	@RequestMapping("/getadminuserdetails")
	public @ResponseBody AdminUsers getAdminUserDetails(@RequestParam("id")String id) throws UnsupportedEncodingException {
		final String method = "getAdminUserDetails::getAdminUserDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		AdminUsers adminUsers=adminUserService.getAdminUserData(id);
		adminUsers.setAdminUserPassword(new String(Base64.getDecoder().decode(adminUsers.getAdminUserPassword()),MasterGSTConstants.PASSWORD_ENCODE_FORMAT));
		
		logger.debug(CLASSNAME + method + END);
		if(NullUtil.isNotEmpty(adminUsers)) {
			
			return adminUsers;
		}else {
			return  null;
		}
	}
	
	@RequestMapping("/deleteadminuserdetails")
	public @ResponseBody void deleteAdminUserDetails(@RequestParam("adminuserid")String adminuserid) {
		final String method = "deleteAdminUserDetails::deleteAdminUserDetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		AdminUsers adminusers= adminUserService.getAdminUserData(adminuserid);
		userService.deleteUser(adminusers.getAdminUserEmail());
		adminUserService.deleteAdminUserData(adminuserid);
		logger.debug(CLASSNAME + method + END);
	}
	@RequestMapping("/emailidexits")
	public @ResponseBody String getEmailExits(@RequestParam("email")String email) {
		final String method = "getEmailExits::getEmailExits::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		User user =userService.findByEmail(email);
	
		logger.debug(CLASSNAME + method + END);
		if (NullUtil.isNotEmpty(user)) {
			return "success";
		}else {
			return "failed";
		}
	}
	
	@RequestMapping("/adminuserprms")
	public @ResponseBody Set<String> adminUserPermissions(@RequestParam("id")String id) {
		final String method = "adminUserPermissions::adminUserPermissions::";
		logger.debug(CLASSNAME + method + BEGIN);
		Set<String> permissions=adminUserService.getAdminUserPermissions(id);
				
		logger.debug(CLASSNAME + method + END);
		return permissions;
	}
}
