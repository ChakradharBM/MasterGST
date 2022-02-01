package com.mastergst.usermanagement.runtime.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.audit.Auditlog;


public interface AuditLogRepository extends MongoRepository<Auditlog, String>{

}
