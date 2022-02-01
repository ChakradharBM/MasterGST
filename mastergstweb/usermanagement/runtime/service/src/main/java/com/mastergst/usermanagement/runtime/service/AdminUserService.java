package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Set;

import com.mastergst.usermanagement.runtime.domain.AdminUsers;

public interface AdminUserService {

	public List<AdminUsers> saveAdminUsers(AdminUsers adminUsers);
	
	public List<AdminUsers> getAllAdminUsers();
	
	AdminUsers createAdminUser(AdminUsers adminUsers);
	
	public AdminUsers getAdminUserData(String id);
	
	public AdminUsers getAdminUserByAdminUserIdAndEmail(String id,String email);
	
	public void deleteAdminUserData(String id);
	
	public void deleteAdminUser(String id);
	
	public Set<String> getAdminUserPermissions(String id);
}
