package com.mastergst.usermanagement.runtime.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.GSTR2ADownloadStatus;
import com.mastergst.usermanagement.runtime.repository.GSTR2ADownloadStatusRepository;

@Service
public class GSTR2ADowloadServiceImpl implements GSTR2ADowloadService {
	
	
	@Autowired
	private GSTR2ADownloadStatusRepository gstr2ADownloadStatusRepository;

	@Override
	public void savedownloadstatus(GSTR2ADownloadStatus gstr2aDownloadStatus) {
		
		gstr2ADownloadStatusRepository.save(gstr2aDownloadStatus);
	}

	@Override
	public List<GSTR2ADownloadStatus> getGSTR2ADownloadStatus(String userid, String clientid, String returnperiod) {
		
		//return gstr2ADownloadStatusRepository.findByUseridAndClientidAndReturnperiod(userid, clientid, returnperiod);
		return gstr2ADownloadStatusRepository.findByClientidAndReturnperiod(clientid, returnperiod);
	}

	@Override
	public List<GSTR2ADownloadStatus> findAllData(String userid, String clientid, String finacialyear) {
		
		String rtStart = "04"+finacialyear;
		String rtEnd = "03"+(Integer.parseInt(finacialyear)+1);
		List<String> fpLst=Arrays.asList(rtStart, "05"+finacialyear, "06"+finacialyear, "07"+finacialyear, "08"+finacialyear, "09"+finacialyear, "10"+finacialyear, "11"+finacialyear, "12"+finacialyear, "01"+(Integer.parseInt(finacialyear)+1), "02"+(Integer.parseInt(finacialyear)+1), rtEnd);
		
		
		//return gstr2ADownloadStatusRepository.findByUseridAndClientidAndReturnperiodIn(userid, clientid, fpLst);
		
		return gstr2ADownloadStatusRepository.findByClientidAndReturnperiodIn(clientid, fpLst);
	}

	@Override
	public void deleteGSTR2ADownloadStatus(List<GSTR2ADownloadStatus> gstr2aDownloadStatus) {
		// TODO Auto-generated method stub
		for(GSTR2ADownloadStatus gSTR2aDownloadStatus : gstr2aDownloadStatus) {
			gstr2ADownloadStatusRepository.delete(gSTR2aDownloadStatus);
		}
		
	}
}
