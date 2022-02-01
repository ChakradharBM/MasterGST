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

import com.mastergst.usermanagement.runtime.domain.GSTR1A;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR1ARepository extends MongoRepository<GSTR1A, String> {

	List<GSTR1A> findByClientid(final String clientid);
	Page<GSTR1A> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	List<GSTR1A> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	GSTR1A findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	List<GSTR1A> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR1A> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	List<GSTR1A> findByClientidAndInvoicenoIn(final String clientid, final List<String> invoicenos);
	List<GSTR1A> findByClientidAndFpAndInvoicenoIn(final String clientid, final String fp, final List<String> invoicenos);
	List<GSTR1A> findByClientidAndFpAndInvtype(final String clientid, final String fp, final String invtype);
	List<GSTR1A> findByClientidAndInvoicenoInAndDateofinvoiceBetween(final String clientid, final List<String> invoicenos, Date d1, Date d2);
}
