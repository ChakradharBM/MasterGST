package com.mastergst.configuration.service;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupsRepository extends MongoRepository<Groups, String> {
	
	Groups findByName(final String name);
}
