package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.LeadsComments;
public interface LeadsCommentsRepository extends MongoRepository<LeadsComments, String> {
	List<LeadsComments> findByInviteid(String inviteid);
}
