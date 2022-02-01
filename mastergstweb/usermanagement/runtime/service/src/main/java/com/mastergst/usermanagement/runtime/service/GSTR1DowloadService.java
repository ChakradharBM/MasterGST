package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.GSTR1DownloadStatus;
import com.mastergst.usermanagement.runtime.domain.GSTR2ADownloadStatus;

public interface GSTR1DowloadService {

	public GSTR1DownloadStatus savedownloadstatus(GSTR1DownloadStatus gstr1DownloadStatus);
	public List<GSTR1DownloadStatus> getGSTR1DownloadStatus(String userid,String clientid,String returnperiod);
	public List<GSTR1DownloadStatus> findAllData(String userid,String clientid,String finacialyear);
	public void deleteGSTR1DownloadStatus(List<GSTR1DownloadStatus> gstr2ADownloadStatus);
}
