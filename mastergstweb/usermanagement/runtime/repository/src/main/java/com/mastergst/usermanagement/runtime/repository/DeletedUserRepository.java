package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.DeletedUsers;

public interface DeletedUserRepository extends MongoRepository<DeletedUsers, String> {
	
}
