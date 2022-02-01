package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.ApisVesrionsDao;
import com.mastergst.usermanagement.runtime.domain.ApisVersionFilter;
import com.mastergst.usermanagement.runtime.domain.ApisVesrion;
import com.mastergst.usermanagement.runtime.repository.ApisVesrionRepository;

@Service
public class ApisVersionServiceImpl implements ApisVersionService {
	private static final Logger logger = LogManager.getLogger(ApisVersionServiceImpl.class.getName());
	private static final String CLASSNAME = "APIsVersionServiceImpl::";

	@Autowired
	private ApisVesrionsDao apisVesrionsDao;
	
	@Autowired
	private  ApisVesrionRepository apisVesrionRepository;
	
	@Override
	public Page<ApisVesrion> getApisVersions(String apiType, int start, int length, String searchVal, ApisVersionFilter filter) {
		final String method = "getApisVersions::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		return apisVesrionsDao.getApisVersions(apiType, start, length, searchVal, filter);
	}
	
	@Override
	public ApisVesrion getApisVersionInfo(String docid) {
		final String method = "getApisVersionInfo::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		return apisVesrionsDao.getApisVersionInfo(docid);
	}
	
	@Override
	public void updateApiVersion(ApisVesrion apivesrion) {
		final String method = "updateApiVersion::";
		logger.debug(CLASSNAME + method + BEGIN);
		ApisVesrion api = apisVesrionRepository.findOne(apivesrion.getDocid());
		
		if(NullUtil.isNotEmpty(api)) {
			if(NullUtil.isNotEmpty(apivesrion.getMethod())) {
				api.setMethod(apivesrion.getMethod());
			}
			if(NullUtil.isNotEmpty(apivesrion.getSandboxVersion())) {
				api.setSandboxVersion(apivesrion.getSandboxVersion());
			}
			if(NullUtil.isNotEmpty(apivesrion.getProductionVersion())) {
				api.setProductionVersion(apivesrion.getProductionVersion());
			}
			if(NullUtil.isNotEmpty(apivesrion.isWebimpl())) {
				api.setWebimpl(apivesrion.isWebimpl());
			}
			apisVesrionRepository.save(api);
		}
	}
}
