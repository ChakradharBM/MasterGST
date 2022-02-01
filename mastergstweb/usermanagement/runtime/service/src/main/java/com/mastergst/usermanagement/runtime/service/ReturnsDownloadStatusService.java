package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;

public interface ReturnsDownloadStatusService {

	public ReturnsDownloadStatus savedownloadstatus(ReturnsDownloadStatus downloadStatus);
	public List<ReturnsDownloadStatus> findAllData(String userid, String clientid, String returntype, String financialyear);
	public List<ReturnsDownloadStatus> getReturnsDownloadStatus(String userid, String clientid, String returntype, String returnperiod);
	public void deleteReturnDownloadStatus(List<ReturnsDownloadStatus> downloadStatus);


}
