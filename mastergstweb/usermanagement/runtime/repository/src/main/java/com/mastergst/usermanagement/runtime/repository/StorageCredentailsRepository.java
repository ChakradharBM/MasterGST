package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.StorageCredentials;

public interface StorageCredentailsRepository extends MongoRepository<StorageCredentials, String>{

}
