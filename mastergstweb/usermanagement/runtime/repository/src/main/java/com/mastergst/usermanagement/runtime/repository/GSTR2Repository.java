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

import com.mastergst.usermanagement.runtime.domain.GSTR2;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR2Repository extends MongoRepository<GSTR2, String> {

	List<GSTR2> findByClientid(final String clientid);
	Page<GSTR2> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	Page<GSTR2> findByClientidAndMatchingStatusIsNotNullAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusIsNotNullAndDateofinvoiceBetween(final String clientid, List<String> invtypes, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndFp(final String clientid,List<String> invtypes, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusIsNullAndFp(final String clientid,List<String> invtypes, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndIsAmendmentAndMatchingStatusIsNullAndFpIn(final String clientid,List<String> invtypes,boolean isAmendment, final List<String> fp, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusAndFp(final String clientid,List<String> invtypes,final String matchingStatus, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusInAndFp(final String clientid,List<String> invtypes,List<String> matchingStatus, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusInAndIsAmendmentAndFp(final String clientid,List<String> invtypes,List<String> matchingStatus, boolean isAmendment, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndFp(final String clientid, final String fp, Pageable pageable);
	Page<GSTR2> findByClientidAndFpAndIsAmendment(final String clientid, final String fp,boolean isAmendment, Pageable pageable);
	Page<GSTR2> findByClientidAndFpAndIsAmendmentAndInvtypeIn(final String clientid, final String fp,boolean isAmendment,List<String> invtypes, Pageable pageable);
	List<GSTR2> findByClientidAndFpAndIsAmendmentAndInvtypeIn(final String clientid, final String fp,boolean isAmendment,List<String> invtypes);
	List<GSTR2> findByClientidAndFpAndIsAmendmentAndInvtype(final String clientid, final String fp,boolean isAmendment,String invtype);
	List<GSTR2> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	Page<GSTR2> findByClientidAndIsAmendmentAndDateofinvoiceBetween(final String clientid, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndIsAmendmentAndFpInAndInvtypeIn(final String clientid, boolean isAmendment, List<String> fps, List<String> invtypes, Pageable pageable);
	List<GSTR2> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<GSTR2> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<GSTR2> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	GSTR2 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	List<GSTR2> findByGstinAndFp(final String gstin, final String fp);
	List<GSTR2> findByGstinAndFpAndInvtype(final String gstin, final String fp, final String invtype);
	List<GSTR2> findByClientidAndFpAndInvtype(final String clientid, final String fp, final String invtype);
	List<GSTR2> findByClientidAndFpAndInvtypeAndIsAmendment(final String clientid, final String fp, final String invtype,boolean isAmendment);
	Page<GSTR2> findByClientidAndFpAndInvtypeAndIsAmendmentAndMatchingStatusIsNull(final String clientid, final String fp, final String invtype,boolean isAmendment, Pageable pageable);
	Page<GSTR2> findByClientidAndInvoicenoIn(final String clientid, List<String> invoiceNos, Pageable pageable);
	
	Page<GSTR2> findByClientidAndFpInAndInvoicenoIn(final String clientid, List<String> fp,List<String> invoiceNos, Pageable pageable);
	
	Page<GSTR2> findByClientidAndInvtypeAndFpInAndInvoicenoIn(final String clientid,final String invtype, List<String> fp,List<String> invoiceNos, Pageable pageable);
	
	List<GSTR2> findByClientidAndFpInAndInvtypeAndIsAmendment(final String clientid, final List<String> fp, final String invtype,boolean isAmendment);
	Page<GSTR2> findByClientidAndFpInAndInvtypeAndIsAmendmentAndMatchingStatusIsNull(final String clientid, final List<String> fp, final String invtype,boolean isAmendment,Pageable pageable);
	long countByClientidAndFpInAndInvtypeAndIsAmendmentAndMatchingStatusIsNull(final String clientid, final List<String> fp, final String invtype,boolean isAmendment);
	Page<GSTR2> findByClientidAndFpInAndInvtypeInAndIsAmendmentAndMatchingStatusIsNull(final String clientid, final List<String> fp, final List<String> invtype,boolean isAmendment,Pageable pageable);
	Page<GSTR2> findByClientidAndFpInAndInvtypeInAndIsAmendmentAndMatchingStatus(final String clientid, final List<String> fp, final List<String> invtype,boolean isAmendment,final String matchingStatus,Pageable pageable);
	Page<GSTR2> findByClientidAndFpInAndInvtypeInAndIsAmendmentAndMatchingStatusIn(final String clientid, final List<String> fp, final List<String> invtype,boolean isAmendment,List<String> matchingStatus,Pageable pageable);
	List<GSTR2> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR2> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	Page<GSTR2> findByClientidAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid, List<String> invtypes, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid, List<String> invtypes,boolean isAmendment, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIn(final String clientid, List<String> invtypes,boolean isAmendment, Date d1, Date d2,List<String> matchingStatus, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, List<String> invtypes, Date d1, Date d2, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetween(final String clientid, List<String> invtypes, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	
	List<GSTR2> findByInvoicenoAndClientidAndDateofinvoiceBetween(final String invoiceNumber,final String clientid, Date d1, Date d2);
	
	Page<GSTR2> findByClientidAndIsAmendmentAndFpIn(final String clientid, boolean isAmendment,List<String> fp, Pageable pageable);

	Page<GSTR2> findByClientidInAndIsAmendmentAndFpInAndInvtypeIn(final List<String> clientids, boolean isAmendment, List<String> fps, List<String> invtypes, Pageable pageable);
	
	Page<GSTR2> findByClientidInAndIsAmendmentAndInvtypeInAndDateofinvoiceBetween(List<String> clientids, boolean isAmendment, List<String> invTypes, Date stDate, Date endDate, Pageable pageable);
	Page<GSTR2> findByMatchingIdAndMatchingStatus(String matchingId,String matchingStatus, Pageable pageable);
	
	
	Page<GSTR2> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatus(final String clientid, List<String> invtypes, Date d1, Date d2, final String matchingStatus, Pageable pageable);
	Page<GSTR2> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusAndIsAmendment(final String clientid, List<String> invtypes, Date d1, Date d2, final String matchingStatus,boolean isAmendment, Pageable pageable);
	
	Page<GSTR2> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusInAndIsAmendment(final String clientid, List<String> invtypes, Date d1, Date d2, List<String> matchingStatus,boolean isAmendment, Pageable pageable);
	
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusInAndIsAmendmentAndFpIn(final String clientid,List<String> invtypes,List<String> matchingStatus, boolean isAmendment, final List<String> fp, Pageable pageable);
	
	Page<GSTR2> findByClientidAndInvtypeInAndMatchingStatusInAndMannualMatchInvoicesAndIsAmendmentAndFpIn(final String clientid,List<String> invtypes,List<String> matchingStatus, final String mannualMatchInvoices, boolean isAmendment, final List<String> fp, Pageable pageable);
	List<GSTR2> findByMatchingStatus(final String matchingStatus);
	
	
	public List<GSTR2> findByClientidAndMatchingIdIn(String clientid, List<String> matchingids);
	public GSTR2 findByClientidAndMatchingId(String clientid, String matchingid);
	public List<GSTR2> findByMatchingId(String matchingid);
	
	//public List<GSTR2> findByClientidAndMatchingIdIn(String clientid, List<String> matchingid);
}
