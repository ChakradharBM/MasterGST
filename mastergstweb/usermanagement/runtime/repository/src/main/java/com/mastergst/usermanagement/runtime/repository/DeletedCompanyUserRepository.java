package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.DeletedCompanyUser;

public interface DeletedCompanyUserRepository extends MongoRepository<DeletedCompanyUser, String> {
	
}
