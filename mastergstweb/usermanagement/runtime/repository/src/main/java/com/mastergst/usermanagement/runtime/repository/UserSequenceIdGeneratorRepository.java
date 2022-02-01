package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.UserSequenceIdGenerator;

public interface UserSequenceIdGeneratorRepository extends MongoRepository<UserSequenceIdGenerator,String>{

	UserSequenceIdGenerator findBySequenceIdName(String sequenceGenerator);

}
