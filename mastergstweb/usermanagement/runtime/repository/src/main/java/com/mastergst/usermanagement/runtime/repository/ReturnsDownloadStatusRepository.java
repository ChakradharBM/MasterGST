package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;

public interface ReturnsDownloadStatusRepository extends MongoRepository<ReturnsDownloadStatus, String>{

	public List<ReturnsDownloadStatus> findByClientidAndReturntypeAndReturnperiodIn(String clientid, String returntype, List<String> fp);

	public List<ReturnsDownloadStatus> findByClientidAndReturnperiod(String clientid, String returnperiod);

	public List<ReturnsDownloadStatus> findByClientidAndReturntypeAndReturnperiod(String clientid, String returntype, String returnperiod);

	

}