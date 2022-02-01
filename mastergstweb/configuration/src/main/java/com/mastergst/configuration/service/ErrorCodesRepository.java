package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorCodesRepository extends MongoRepository<ErrorCodeConfig, String> {
	ErrorCodeConfig findByErrorCode(final String error);
}
