package com.mastergst.usermanagement.runtime.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.GSTR1DownloadStatus;
import com.mastergst.usermanagement.runtime.domain.GSTR3BDownloadStatus;
import com.mastergst.usermanagement.runtime.repository.GSTR3BDownloadStatusRepository;

@Service
public class GSTR3BDowloadServiceImpl implements GSTR3BDowloadService {

	@Autowired
	private GSTR3BDownloadStatusRepository gSTR3BDownloadStatusRepository;
	
	
	@Override
	public void savedownloadstatus(GSTR3BDownloadStatus GSTR3BDownloadStatus) {
		
		gSTR3BDownloadStatusRepository.save(GSTR3BDownloadStatus);
	}
	
	@Override
	public List<GSTR3BDownloadStatus> getGSTR3BDownloadStatus(String userid, String clientid, String returnperiod) {
		
		//return gSTR3BDownloadStatusRepository.findByUseridAndClientidAndReturnperiod(userid, clientid, returnperiod);
		return gSTR3BDownloadStatusRepository.findByClientidAndReturnperiod(clientid, returnperiod);
	}
	
	@Override
	public List<GSTR3BDownloadStatus> findAllData(String userid, String clientid, String finacialyear) {
		

		String rtStart = "04"+finacialyear;
		String rtEnd = "03"+(Integer.parseInt(finacialyear)+1);
		List<String> fpLst=Arrays.asList(rtStart, "05"+finacialyear, "06"+finacialyear, "07"+finacialyear, "08"+finacialyear, "09"+finacialyear, "10"+finacialyear, "11"+finacialyear, "12"+finacialyear, "01"+(Integer.parseInt(finacialyear)+1), "02"+(Integer.parseInt(finacialyear)+1), rtEnd);
		
		//return gSTR3BDownloadStatusRepository.findByUseridAndClientidAndReturnperiodIn(userid, clientid, fpLst);
		return gSTR3BDownloadStatusRepository.findByClientidAndReturnperiodIn(clientid, fpLst);
	}

	@Override
	public void deleteGSTR3BDownloadStatus(List<GSTR3BDownloadStatus> gstr3bDownloadStatus) {
		for(GSTR3BDownloadStatus gSTR3bDownloadStatus : gstr3bDownloadStatus) {
			gSTR3BDownloadStatusRepository.delete(gSTR3bDownloadStatus);
		}
		
	}
	
}
