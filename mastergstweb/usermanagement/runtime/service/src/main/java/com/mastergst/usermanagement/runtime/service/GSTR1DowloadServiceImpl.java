package com.mastergst.usermanagement.runtime.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.domain.GSTR1DownloadStatus;
import com.mastergst.usermanagement.runtime.domain.GSTR2ADownloadStatus;
import com.mastergst.usermanagement.runtime.repository.GSTR1DownloadStatusRepository;

@Service
public class GSTR1DowloadServiceImpl implements GSTR1DowloadService {

	@Autowired
	private GSTR1DownloadStatusRepository gstr1DownloadStatusRepository;

	@Override
	public GSTR1DownloadStatus savedownloadstatus(GSTR1DownloadStatus gstr1DownloadStatus) {
		return gstr1DownloadStatusRepository.save(gstr1DownloadStatus);
	}

	@Override
	public List<GSTR1DownloadStatus> getGSTR1DownloadStatus(String userid, String clientid, String returnperiod) {
		
		//return gstr1DownloadStatusRepository.findByUseridAndClientidAndReturnperiod(userid, clientid, returnperiod);
		return gstr1DownloadStatusRepository.findByClientidAndReturnperiod(clientid, returnperiod);
	}

	@Override
	public List<GSTR1DownloadStatus> findAllData(String userid, String clientid, String financialyear) {
		
		String rtStart = "04"+financialyear;
		String rtEnd = "03"+(Integer.parseInt(financialyear)+1);
		List<String> fpLst=Arrays.asList(rtStart, "05"+financialyear, "06"+financialyear, "07"+financialyear, "08"+financialyear, "09"+financialyear, "10"+financialyear, "11"+financialyear, "12"+financialyear, "01"+(Integer.parseInt(financialyear)+1), "02"+(Integer.parseInt(financialyear)+1), rtEnd);

		//return gstr1DownloadStatusRepository.findByUseridAndClientidAndReturnperiodIn(userid, clientid, fpLst);
		return gstr1DownloadStatusRepository.findByClientidAndReturnperiodIn(clientid, fpLst);
	}

	@Override
	public void deleteGSTR1DownloadStatus(List<GSTR1DownloadStatus> gstr1DownloadStatus) {
		for(GSTR1DownloadStatus gSTR1DownloadStatus : gstr1DownloadStatus) {
			gstr1DownloadStatusRepository.delete(gSTR1DownloadStatus);
		}
		
	}
}
