package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.AdminUsers;
import com.mastergst.usermanagement.runtime.repository.AdminUserRepository;

@Service
public class AdminUserServiceImpl implements AdminUserService {

	private static final Logger logger = LogManager.getLogger(AdminUserServiceImpl.class.getName());
	private static final String CLASSNAME = "AdminUserServiceImpl::";

	@Autowired
	private AdminUserRepository adminUserRepository; 
	@Autowired
	private UserService userService;

	@Override
	@Transactional
	public List<AdminUsers> saveAdminUsers(AdminUsers adminUsers) {

		logger.debug(CLASSNAME + "saveAdminUsers : Begin");
		
		adminUserRepository.save(adminUsers);
		return adminUserRepository.findAll();
	}

	@Override
	public List<AdminUsers> getAllAdminUsers() {
		logger.debug(CLASSNAME + "getAllAdminUsers : Begin");
		return adminUserRepository.findAll();
	}

	@Override
	public AdminUsers getAdminUserData(String id) {
		return adminUserRepository.findById(id);
	}

	public void deleteAdminUserData(String id) {
		adminUserRepository.delete(id);
	}
	
	
	@Override
	public AdminUsers getAdminUserByAdminUserIdAndEmail(String id, String email) {
		return adminUserRepository.findByAdminUserIdAndAdminUserEmail(id, email);
	}
	
	@Override
	public void deleteAdminUser(String id) {
		adminUserRepository.delete(id);
	}

	@Override
	public AdminUsers createAdminUser(AdminUsers adminUsers) {
		
		return adminUserRepository.save(adminUsers);
	}
	
	@Override
	public Set<String> getAdminUserPermissions(String id) {
	
		User user=userService.getUserById(id);
		if(isNotEmpty(user)) {
			AdminUsers admin_user = adminUserRepository.findByAdminUserEmail(user.getEmail());
				if(isNotEmpty(admin_user) && isNotEmpty(admin_user.getPermissions())) {
						if(isNotEmpty(admin_user) && isNotEmpty(admin_user.getPermissions())) {
							logger.debug(CLASSNAME + "getPermissions : isnot empty");
							return admin_user.getPermissions().keySet();
							}else{
							logger.debug(CLASSNAME + "getPermissions : is empty");
							 return Sets.newHashSet();
						}
				} else if( isNotEmpty(user.getEmail())) {
					User superUser = userService.findByEmail(user.getEmail());
					if(isNotEmpty(superUser)) {
						AdminUsers admuser = adminUserRepository.findByAdminUserEmail(superUser.getEmail());
						if(isNotEmpty(admuser) && isNotEmpty(admuser.getPermissions())) 
							return admuser.getPermissions().keySet();
								//logger.debug(CLASSNAME + "getPermissions : isnot empty");
						} else {
								logger.debug(CLASSNAME + "getPermissions : is empty");
								 return Sets.newHashSet();
						}
					}
			}
		
		return null;
	}
}
