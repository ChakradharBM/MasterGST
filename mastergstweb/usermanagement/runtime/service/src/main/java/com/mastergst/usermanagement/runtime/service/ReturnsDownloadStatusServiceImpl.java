package com.mastergst.usermanagement.runtime.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;
import com.mastergst.usermanagement.runtime.repository.ReturnsDownloadStatusRepository;

@Service
public class ReturnsDownloadStatusServiceImpl implements ReturnsDownloadStatusService{

	@Autowired private ReturnsDownloadStatusRepository returnsDownloadStatusRepository;
	
	@Override
	public ReturnsDownloadStatus savedownloadstatus(ReturnsDownloadStatus downloadStatus) {
		return returnsDownloadStatusRepository.save(downloadStatus);
	}
	
	@Override
	public List<ReturnsDownloadStatus> getReturnsDownloadStatus(String userid, String clientid, String returntype, String returnperiod) {
		
		return returnsDownloadStatusRepository.findByClientidAndReturntypeAndReturnperiod(clientid, returntype, returnperiod);
	}
	
	@Override
	public void deleteReturnDownloadStatus(List<ReturnsDownloadStatus> downloadStatus) {
		for(ReturnsDownloadStatus status : downloadStatus) {
			returnsDownloadStatusRepository.delete(status);
		}
		
	}
	
	@Override
	public List<ReturnsDownloadStatus> findAllData(String userid, String clientid, String returntype, String financialyear) {
		String rtStart = "04"+financialyear;
		String rtEnd = "03"+(Integer.parseInt(financialyear)+1);
		List<String> fpLst=Arrays.asList(rtStart, "05"+financialyear, "06"+financialyear, "07"+financialyear, "08"+financialyear, "09"+financialyear, "10"+financialyear, "11"+financialyear, "12"+financialyear, "01"+(Integer.parseInt(financialyear)+1), "02"+(Integer.parseInt(financialyear)+1), rtEnd);
		
		return returnsDownloadStatusRepository.findByClientidAndReturntypeAndReturnperiodIn(clientid, returntype, fpLst);
	}

}
