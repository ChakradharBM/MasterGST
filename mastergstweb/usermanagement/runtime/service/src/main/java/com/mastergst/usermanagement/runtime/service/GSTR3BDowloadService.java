package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.GSTR1DownloadStatus;
import com.mastergst.usermanagement.runtime.domain.GSTR3BDownloadStatus;

public interface GSTR3BDowloadService {
	
	public void savedownloadstatus(GSTR3BDownloadStatus gSTR3BDownloadStatus);
	public List<GSTR3BDownloadStatus> getGSTR3BDownloadStatus(String userid,String clientid,String returnperiod);
	public List<GSTR3BDownloadStatus> findAllData(String userid,String clientid,String finacialyear);
	public void deleteGSTR3BDownloadStatus(List<GSTR3BDownloadStatus> gstr2ADownloadStatus);
}
