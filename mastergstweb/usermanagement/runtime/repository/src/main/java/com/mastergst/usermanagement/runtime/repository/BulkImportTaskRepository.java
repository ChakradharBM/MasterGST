package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.BulkImportTask;

public interface BulkImportTaskRepository extends MongoRepository<BulkImportTask, String>{
	
	List<BulkImportTask> findByUseridAndClientid(String userid,String clientid);
}
