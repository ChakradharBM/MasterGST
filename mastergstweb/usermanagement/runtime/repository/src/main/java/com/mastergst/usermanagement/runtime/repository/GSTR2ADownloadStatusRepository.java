package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR2ADownloadStatus;

public interface GSTR2ADownloadStatusRepository extends MongoRepository<GSTR2ADownloadStatus, String>{
	
	public GSTR2ADownloadStatus findByUseridAndClientidAndReturnperiod(String userid,String clientid,String returnperiod);
	public List<GSTR2ADownloadStatus> findByUseridAndClientidAndFinancialyear(String userid,String clientid,String finacialyear);
	public List<GSTR2ADownloadStatus> findByUseridAndClientidAndReturnperiodIn(String userid,String clientid,List<String> returnperiod);

	//public GSTR2ADownloadStatus findByClientidAndReturnperiod(String clientid,String returnperiod);
	public List<GSTR2ADownloadStatus> findByClientidAndReturnperiod(String clientid,String returnperiod);
	public List<GSTR2ADownloadStatus> findByClientidAndFinancialyear(String clientid,String finacialyear);
	public List<GSTR2ADownloadStatus> findByClientidAndReturnperiodIn(String clientid,List<String> returnperiod);
}