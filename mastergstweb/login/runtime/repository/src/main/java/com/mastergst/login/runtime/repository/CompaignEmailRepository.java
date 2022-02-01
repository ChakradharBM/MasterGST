package com.mastergst.login.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.login.runtime.domain.CompaignEmail;

public interface CompaignEmailRepository extends MongoRepository<CompaignEmail, String>{

}
