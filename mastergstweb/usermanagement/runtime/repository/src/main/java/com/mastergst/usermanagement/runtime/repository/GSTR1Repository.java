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
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface GSTR1Repository extends MongoRepository<GSTR1, String> {

	List<GSTR1> findByClientid(final String clientid);
	Page<GSTR1> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR1> findByClientidAndIsAmendmentAndDateofinvoiceBetween(final String clientid, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	List<GSTR1> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<GSTR1> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2, final List<String> branch);
	Page<GSTR1> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	GSTR1 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	GSTR1 findByClientidAndInvtypeAndInvoicenoAndFp(final String clientid, final String invtype, final String invoiceno, final String fp);
	
	List<GSTR1> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR1> findByClientidAndFpAndGstStatusIn(final String clientid, final String fp, final List<String> gstStatus);
	List<GSTR1> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	List<GSTR1> findByClientidAndGstStatusInAndFpIn(final String clientid, final List<String> gstStatus, final List<String> fps);
	List<GSTR1> findByClientidAndInvoicenoIn(final String clientid, final List<String> invoicenos);
	List<GSTR1> findByClientidAndInvoicenoInAndInvtype(final String clientid, final List<String> invoicenos,String invtype);
	List<GSTR1> findByClientidAndFpAndInvoicenoIn(final String clientid, final String fp, final List<String> invoicenos);
	List<GSTR1> findByClientidAndInvoicenoInAndDateofinvoiceBetween(final String clientid, final List<String> invoicenos, Date d1, Date d2);
	List<GSTR1> findByClientidAndFpAndInvtype(final String clientid, final String fp, final String invtype);
	List<GSTR1> findByClientidAndInvtypeAndGstStatusAndDateofinvoiceBetween(final String clientid, final String invtype, final String gstStatus, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvtypeAndGstStatusInAndDateofinvoiceBetween(final String clientid, final String invtype, final List<String> gstStatus, Date d1, Date d2);
	
	List<GSTR1> findByClientidAndInvtypeInAndGstStatusInAndCdnnilsuppliesAndDateofinvoiceBetween(final String clientid, final List<String> invtype, final List<String> gstStatus,final String cdnnilsupplies, Date d1, Date d2);
	
	List<GSTR1> findByClientidAndInvtypeAndCdnur_TypAndGstStatusInAndDateofinvoiceBetween(final String clientid, final String invtype,final String type, final List<String> gstStatus, Date d1, Date d2);
	
	
	List<GSTR1> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR1> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvtypeInAndBranchInAndDateofinvoiceBetween(final String clientid, final List<String> invtype,final List<String> branch, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvtypeInAndFp(final String clientid, final List<String> invtype, final String fp);
	List<GSTR1> findByClientidAndInvtypeInAndFpAndBranchIn(final String clientid, final List<String> invtype, final String fp,final List<String> branch);
	List<GSTR1> findByClientidAndInvtypeInAndFpIn(final String clientid, final List<String> invtype, final List<String> fps);
	Page<GSTR1> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2, Pageable pageable);
	List<GSTR1> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<GSTR1> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	//GSTR1 findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
	List<GSTR1> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
	
	List<GSTR1> findByDateofinvoiceBetween(Date d1, Date d2);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	
	Page<GSTR1> findByClientidInAndDateofinvoiceBetween(List<String> clientids, Date d1, Date d2, Pageable pageable);
	List<GSTR1> findByClientidInAndInvtypeInAndDateofinvoiceBetween(List<String> clientids, final List<String> invtype, Date d1, Date d2);
	List<GSTR1> findByClientidInAndInvtypeInAndFp(List<String> clientids, final List<String> invtype, final String fp);
	List<GSTR1> findByClientidInAndInvtypeInAndFpIn(List<String> clientids, final List<String> invtype, final List<String> fps);
	
	List<GSTR1> findByInvoicenoAndClientidAndDateofinvoiceBetween(final String invoiceNumber,final String clientid, Date d1, Date d2);
	List<GSTR1> findByInvoicenoAndClientidAndInvtypeInAndDateofinvoiceBetween(final String invoiceNumber,final String clientid,List<String> invtype, Date d1, Date d2);

	Page<GSTR1> findByClientidAndFp(String clientid,String fp, Pageable pageable);
	
	GSTR1 findByClientidAndInvtypeAndFpAndInvoiceno(final String clientid, final String invtype,final String fp, final String invoiceno);

	List<GSTR1> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGovtInvoiceStatusIgnoreCase(final String clientid, final List<String> invtype, Date d1, Date d2, String govtinvstatus);
	List<GSTR1> findByClientidAndInvtypeInAndFpInAndGovtInvoiceStatusIgnoreCase(final String clientid, final List<String> invtype, final List<String> fps, String govtinvstatus);

	List<GSTR1> findByClientidInAndInvtypeInAndDateofinvoiceBetweenAndGovtInvoiceStatusIsNotNull(List<String> clientids, final List<String> invtype, Date d1, Date d2);
	List<GSTR1> findByClientidInAndInvtypeInAndFpAndGovtInvoiceStatusIsNotNull(List<String> clientids, final List<String> invtype, final String fp);

	List<GSTR1> findByClientidInAndInvtypeInAndFpInAndGovtInvoiceStatusIsNotNull(List<String> clientids, final List<String> invtype, final List<String> fps);
	Page<GSTR1> findByClientidInAndDateofinvoiceBetweenAndGovtInvoiceStatusIgnoreCase(List<String> clientids, Date d1, Date d2, Pageable pageable, String govtinvstatus);
	public Page<GSTR1> findByClientidAndFpIn(String clientid, List<String> fp, Pageable pageable);
	Page<? extends InvoiceParent> findByClientidAndDateofinvoiceBetweenAndGstr1orEinvoice(String string, Date stDate, Date endDate, String string2, Pageable pageable);
	
	Page<GSTR1> findByClientidAndMthCdAndYrCd(String clientid, String mthcd, String yrcd, Pageable pageable);
	Page<GSTR1> findByClientidAndYrCd(String clientid,String yrcd, Pageable pageable);
	
	List<GSTR1> findByClientidAndInvtypeInAndFpAndIrnNoIsNull(String clientid, final List<String> invtype, final String fp);
	public InvoiceParent findByAmendmentRefId(String amendmentRefId);
	public List<GSTR1> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndIdNotIn(String clientid, List<String> invTypes, Date stDate, Date endDate, List<String> amendmentRefids);
	public List<GSTR1> findByClientidAndInvtypeInAndFpAndIdNotIn(String clientid, List<String> otherinvTypes, String retPeriod, List<String> amendmentRefids);
	GSTR1 findByInvoicenoAndClientidAndInvtype(String invoiceno, String clientid, String invtype);
	InvoiceParent findByIdIn(List<String> amendmentRefId);
	List<GSTR1> findByClientidAndFpAndB2cs_PosAndB2cs_splyTyAndInvtype(String clientid, String fp, String pos, String splyTy,String invtype);
	List<GSTR1> findByClientidAndFpAndTxpd_PosAndTxpd_splyTyAndInvtype(String clientid, String fp, String pos, String splyTy, String invtype);
	
	List<GSTR1> findByClientidAndFpAndAt_PosAndAt_splyTyAndInvtype(String clientid, String fp, String pos, String splyTy,String invtype);
	//InvoiceParent findByClientidAndInvtypeAndInvoicenoAndFp(String clientid, String invoiceno, String fp, String invtype);
}
