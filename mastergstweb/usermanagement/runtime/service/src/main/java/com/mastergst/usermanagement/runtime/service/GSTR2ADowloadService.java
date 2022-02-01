package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.GSTR2ADownloadStatus;

public interface GSTR2ADowloadService {

	public void savedownloadstatus(GSTR2ADownloadStatus gstr2ADownloadStatus);
	public List<GSTR2ADownloadStatus> getGSTR2ADownloadStatus(String userid,String clientid,String returnperiod);
	public List<GSTR2ADownloadStatus> findAllData(String userid,String clientid,String finacialyear);
	public void deleteGSTR2ADownloadStatus(List<GSTR2ADownloadStatus> gstr2ADownloadStatus);
}
