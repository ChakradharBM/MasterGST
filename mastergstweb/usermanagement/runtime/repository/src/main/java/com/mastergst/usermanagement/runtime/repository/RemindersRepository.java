package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;


import com.mastergst.usermanagement.runtime.domain.Reminders;

public interface RemindersRepository extends MongoRepository<Reminders, String> {

	List<Reminders> findByClientid(String clientid);
	

}
