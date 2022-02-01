package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AdminUsers;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;

public interface AdminUserRepository extends MongoRepository<AdminUsers, String>{
	
	public AdminUsers findById(String id);
		
	public AdminUsers findByAdminUserIdAndAdminUserEmail(String id,String email);
	
	public void deleteByAdminUserEmail(String email);

	public AdminUsers findByAdminUserEmail(String email);
}
