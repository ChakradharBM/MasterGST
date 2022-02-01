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
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR6Repository extends MongoRepository<GSTR6, String> {

	List<GSTR6> findByClientid(final String clientid);
	Page<GSTR6> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR6> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	Page<GSTR6> findByClientidAndIsAmendmentAndDateofinvoiceBetween(final String clientid, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	List<GSTR6> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<GSTR6> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<GSTR6> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	GSTR6 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	List<GSTR6> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR6> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	List<GSTR6> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<GSTR6> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR6> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<GSTR6> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR6> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR6> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	
	
	List<GSTR6> findByClientidAndInvoicenoAndDateofinvoiceBetween(String clientid,String invoiceid,Date d1, Date d2);
	
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	GSTR6 findByClientidAndFp(String clientId, String fp);
}
