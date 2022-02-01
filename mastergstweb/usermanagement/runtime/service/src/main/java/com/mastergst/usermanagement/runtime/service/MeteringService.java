/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;

import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.domain.MeterCount;
import com.mastergst.usermanagement.runtime.domain.Metering;

/**
 * Service interface for Metering to perform CRUD operation.
 * 
 * @author Ashok Samrat
 * @version 1.0
 */
public interface MeteringService {

	List<Metering> getAllMetering(String userid);
	
	List<Metering> getMeteringData(final String userid, final Date startDate, final Date endDate);
	
	List<Metering> getMeteringData(final String userid, final String sandBox, final String production, final Date startDate, final Date endDate);
	
	List<Metering> getEWayMeteringData(final String userid, final Date startDate, final Date endDate);
	
	List<Metering> getGSTMeteringData(final String userid, final Date startDate, final Date endDate);
	
	List<MeterCount> getMeteringData(final Date startDate, final Date endDate);
	
	List<Metering> getMeteringDataByStage(final String stage, final Date startDate, final Date endDate);
	
	List<Metering> getClientMeteringData(final String gstUsername, final Date startDate, final Date endDate);

	void deleteMetering(String id);
	
	void updateMeteringStage(final Date startDate, final Date endDate);
	
	List<Metering> getMeteringStageCount(final String userid,final String stage);
	Page<Metering> getGSTAllMeterings(String dashboardType,String userid,int page, int length, String sortFieldName, String order, String searchVal,final Date startDate, final Date endDate);
	Page<Metering> getGSTAllMeteringsFoExcel(String dashboardType,String userid,final Date startDate, final Date endDate);
	
	List<Metering> getMeteringDataByuserIdAndStage(final String userid,final String stage, final Date startDate, final Date endDate);

	List<MeterCount> getAspMeteringData(final String userid, final Date startDate, final Date endDate);
	
	Map<String, String> getUserWiseMeteringData(final String userid, final Date startDate, final Date endDate);
	
	Map<String, String> getYearWiseMeteringData(final Date startDate, final Date endDate);
	List<Metering> getEInvMeteringData(String string, Date startDate, Date endDate);
	
}
