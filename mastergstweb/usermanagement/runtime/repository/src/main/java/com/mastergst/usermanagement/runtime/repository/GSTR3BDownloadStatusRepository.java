package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR3BDownloadStatus;

public interface GSTR3BDownloadStatusRepository extends MongoRepository<GSTR3BDownloadStatus, String>{
	
	public GSTR3BDownloadStatus findByUseridAndClientidAndReturnperiod(String userid,String clientid,String returnperiod);
	public List<GSTR3BDownloadStatus> findByUseridAndClientidAndFinancialyear(String userid,String clientid,String finacialyear);
	public List<GSTR3BDownloadStatus> findByUseridAndClientidAndReturnperiodIn(String userid,String clientid,List<String> returnperiod);

	public List<GSTR3BDownloadStatus> findByClientidAndReturnperiod(String clientid,String returnperiod);
	public List<GSTR3BDownloadStatus> findByClientidAndFinancialyear(String clientid,String finacialyear);
	public List<GSTR3BDownloadStatus> findByClientidAndReturnperiodIn(String clientid,List<String> returnperiod);
}
