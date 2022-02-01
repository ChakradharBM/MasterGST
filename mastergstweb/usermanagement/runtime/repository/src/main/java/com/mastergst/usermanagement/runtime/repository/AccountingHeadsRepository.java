package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AccountingHeads;

public interface AccountingHeadsRepository  extends MongoRepository<AccountingHeads, String>{

	public AccountingHeads findByHeads_name(String headname);
	
}
