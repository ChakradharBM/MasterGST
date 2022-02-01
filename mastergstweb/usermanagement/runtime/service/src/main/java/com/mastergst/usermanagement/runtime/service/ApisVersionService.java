package com.mastergst.usermanagement.runtime.service;

import org.springframework.data.domain.Page;

import com.mastergst.usermanagement.runtime.domain.ApisVersionFilter;
import com.mastergst.usermanagement.runtime.domain.ApisVesrion;

public interface ApisVersionService {

	public Page<ApisVesrion> getApisVersions(String apiType, int start, int length, String searchVal, ApisVersionFilter filter);

	public ApisVesrion getApisVersionInfo(String docid);

	public void updateApiVersion(ApisVesrion apivesrion);

}
