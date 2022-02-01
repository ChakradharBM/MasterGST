package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR1DownloadStatus;

public interface GSTR1DownloadStatusRepository extends MongoRepository<GSTR1DownloadStatus, String>{

	public GSTR1DownloadStatus findByUseridAndClientidAndReturnperiod(String userid,String clientid,String returnperiod);
	public List<GSTR1DownloadStatus> findByUseridAndClientidAndFinancialyear(String userid,String clientid,String finacialyear);
	public List<GSTR1DownloadStatus> findByUseridAndClientidAndReturnperiodIn(String userid,String clientid,List<String> returnperiod);

	public List<GSTR1DownloadStatus> findByClientidAndReturnperiod(String clientid,String returnperiod);
	public List<GSTR1DownloadStatus> findByAndClientidAndFinancialyear(String clientid,String finacialyear);
	public List<GSTR1DownloadStatus> findByClientidAndReturnperiodIn(String clientid,List<String> returnperiod);
}
