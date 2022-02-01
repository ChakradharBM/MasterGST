/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR4;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR4Repository extends MongoRepository<GSTR4, String> {

	List<GSTR4> findByClientid(final String clientid);
	Page<GSTR4> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR4> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	Page<GSTR4> findByClientidAndIsAmendmentAndDateofinvoiceBetween(final String clientid, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	List<GSTR4> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<GSTR4> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<GSTR4> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	GSTR4 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	List<GSTR4> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR4> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	List<GSTR4> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<GSTR4> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR4> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<GSTR4> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR4> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR4> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR4> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);

	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
}
