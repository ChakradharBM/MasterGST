/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.EWAYBILL;
import com.mastergst.usermanagement.runtime.domain.GSTR1;


/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface EwayBillRepository extends MongoRepository<EWAYBILL, String> {
	Page<EWAYBILL> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<EWAYBILL> findByClientidAndEBillDateBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<EWAYBILL> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,final List<String> branch);
	Page<EWAYBILL> findByClientidAndGenerateModeAndEBillDateBetween(final String clientid, String genMode, Date d1, Date d2, Pageable pageable);
	Page<EWAYBILL> findByClientidAndGenerateModeInAndEBillDateBetween(final String clientid, List<String> genMode, Date d1, Date d2, Pageable pageable);
	Page<EWAYBILL> findByClientidAndGenerateModeAndEBillDateBetweenAndBranchIn(final String clientid, String genMode, Date d1, Date d2, Pageable pageable,final List<String> branch);
	
	Page<EWAYBILL> findByClientidAndGenerateModeInAndEBillDateBetweenAndBranchIn(final String clientid, List<String> genMode, Date d1, Date d2, Pageable pageable,final List<String> branch);
	
	EWAYBILL findByClientidAndFpAndEwayBillNumber(final String clientid, String fp, String ewayBillNumber);
	
	EWAYBILL findByClientidAndFpAndDateofinvoiceBetween(final String clientid, String fp, Date d1, Date d2);
	EWAYBILL findById(final String id);
	List<EBillVehicleListDetails> findByClientidAndEwayBillNumber(String clientid, String ewayBillNumber);
	EWAYBILL findByClientidAndInvtypeAndInvoicenoAndFp(final String clientid, final String invtype, final String invoiceno, final String fp);
}
