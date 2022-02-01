package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.SmtpDetails;

public interface SmtpDetailsRepository extends MongoRepository<SmtpDetails, String> {
	public SmtpDetails findByUserIdAndClientId(String userId, String clientId);
	public List<SmtpDetails> findByUserId(String userId);
	public SmtpDetails findByClientId(String clientId);
}
