/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;

/**
 * Repository interface for Purchase Register Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface PurchaseRegisterRepository extends MongoRepository<PurchaseRegister, String> {

	List<PurchaseRegister> findByClientid(final String clientid);
	List<PurchaseRegister> findByClientidAndInvoicenoIn(String clientid, List<String> invoicenos);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid,List<String> invttpes, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndBillDateBetweenAndMatchingStatusIsNull(final String clientid,List<String> invttpes, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndBillDateBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndBillDateBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	List<PurchaseRegister> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	Page<PurchaseRegister> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	PurchaseRegister findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	PurchaseRegister findByClientidAndInvtypeAndInvoicenoAndFp(final String clientid, final String invtype, final String invoiceno,final String fp);
	List<PurchaseRegister> findByGstinAndFp(final String gstin, final String fp);
	List<PurchaseRegister> findByGstinAndFpAndInvtype(final String gstin, final String fp, final String invtype);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndItems_ElgIsNull(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndItems_ElgIsNullAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	List<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndInvtypeNotInAndItems_ElgIsNull(final String clientid, Date d1, Date d2,List<String> invtype);
	Page<PurchaseRegister> findByClientidAndDateofinvoiceBetweenAndInvtypeNotInAndItems_ElgIsNull(final String clientid, Date d1, Date d2,List<String> invtype,Pageable pageable);
	Page<PurchaseRegister> findByUseridAndClientidAndGstStatusNotAndDateofinvoiceBetween(final String userid, final String clientid, final String gstStatus, Date d1, Date d2, Pageable pageable);
	List<PurchaseRegister> findByClientidAndGstStatusNotAndDateofinvoiceBetween(final String clientid, final String gstStatus, Date d1, Date d2);
	List<PurchaseRegister> findByGstinAndInvtypeAndDateofinvoiceBetween(final String gstin, final String invtype, Date d1, Date d2);
	List<PurchaseRegister> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<PurchaseRegister> findByClientidAndInvtypeAndMatchingStatusInAndDateofinvoiceBetween(final String clientid, final String invtype,List<String> matchingstatus, Date d1, Date d2);
	Page<PurchaseRegister> findByClientidAndInvtypeAndDateofinvoiceBetweenAndMatchingStatusIsNull(final String clientid, final String invtype, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvoicenoInAndMatchingStatusIn(final String clientid, List<String> invoiceNos, List<String> statusList, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeAndInvoicenoInAndMatchingStatusIn(final String clientid, final String invtype, List<String> invoiceNos, List<String> statusList, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeAndDateofinvoiceBetweenAndMatchingStatusIn(final String clientid, final String invtype, Date d1, Date d2, List<String> status, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeAndBillDateBetweenAndMatchingStatusIsNull(final String clientid, final String invtype, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeAndBillDateBetweenAndMatchingStatusIn(final String clientid, final String invtype, Date d1, Date d2,List<String> status, Pageable pageable);
	List<PurchaseRegister> findByClientidAndInvtypeAndBillDateBetween(final String clientid, final String invtype, Date d1, Date d2);
	
	List<PurchaseRegister> findByClientidAndInvoicenoAndCompanyDBIdAndDateofinvoiceBetween(String clientid,String invoiceid,String companyDBID, Date d1, Date d2);
	List<PurchaseRegister> findByClientidAndInvoicenoAndDateofinvoiceBetween(String clientid,String invoiceid,Date d1, Date d2);
	List<PurchaseRegister> findByClientidAndInvoicenoAndB2b_CtinAndDateofinvoiceBetween(String clientid,String invoiceid, String gstin,Date d1, Date d2);
	PurchaseRegister findByClientidAndInvtypeAndInvoicenoAndB2b_CtinIgnoreCaseAndFp(String clientid,final String invtype,String invoiceno, String gstin,String fp);
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	Page<PurchaseRegister> findByClientidInAndDateofinvoiceBetween(List<String> clientids, Date d1, Date d2, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndMatchingStatusIsNotNullAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusIsNotNullAndDateofinvoiceBetween(final String clientid,List<String> invTypes, Date d1, Date d2, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusIsNullAndDateofinvoiceBetween(final String clientid,List<String> invTypes, Date d1, Date d2, Pageable pageable);
	
	List<PurchaseRegister> findByInvoicenoAndClientidAndDateofinvoiceBetween(final String invoiceNumber,final String clientid, Date d1, Date d2);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingIdIn(final String clientid,List<String> invttpes, List<String> matchingids, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusAndDateofinvoiceBetween(final String clientid,List<String> invttpes, final String matchingStatus, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusAndBillDateBetween(final String clientid,List<String> invttpes, final String matchingStatus, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid,List<String> invtype, Date d1, Date d2, Pageable pageable);

	Page<PurchaseRegister> findByClientidAndFp(final String clientid, final String fp, Pageable pageable);


	Page<PurchaseRegister> findByClientidAndDateofitcClaimedBetween(Pageable pageable, String clientid, Date startdate, Date enddate);
	
	Page<PurchaseRegister> findByClientidAndDateofitcClaimedBetweenAndInvtypeNotIn(Pageable pageable, String clientid, Date startdate, Date enddate,List<String> invtype);
	Page<PurchaseRegister> findByClientidAndDateofitcClaimedBetweenAndInvtypeNotInAndItems_ElgIsNotNull(Pageable pageable, String clientid, Date startdate, Date enddate,List<String> invtype);
	
	PurchaseRegister findByClientidAndInvtypeAndFpAndInvoiceno(final String clientid, final String invtype,final String fp, final String invoiceno);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusInAndDateofinvoiceBetween(final String clientid,List<String> invTypes, List<String> matchingStatus, Date d1, Date d2, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusInAndMannualMatchInvoicesAndDateofinvoiceBetween(final String clientid,List<String> invTypes, List<String> matchingStatus, final String mannualMatchInvoices, Date d1, Date d2, Pageable pageable);
	
	Page<PurchaseRegister> findByMatchingIdAndMatchingStatus(final String matchingId,final String matchingStatus, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMannualMatchInvoicesAndMatchingIdIn(final String clientid,List<String> invttpes, final String mannualMatchInvoices, List<String> matchingids, Pageable pageable);
	List<PurchaseRegister> findByMatchingStatus(final String matchingStatus);

	
	List<PurchaseRegister> findByClientidAndMatchingId(final String clientid, final String matchingStatus);
	List<PurchaseRegister> findByClientidAndMatchingIdInAndMatchingStatus(final String clientid, final List<String> matchingId, final String matchingStatus);
	List<PurchaseRegister> findByClientidAndMatchingIdInAndMatchingStatusNotIn(final String clientid, final List<String> matchingId, final List<String> matchingStatus);
	
	List<PurchaseRegister> findByClientidAndRevchargetypeAndDateofinvoiceBetween(final String clientid, final String revCharge, Date d1, Date d2);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndYrCdAndMatchingStatusIsNull(final String clientid,List<String> invTypes, final String yrCd, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusInAndYrCd(final String clientid,List<String> invTypes, List<String> matchingStatus, final String yrCd, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndMthCdAndYrCd(String clientid, String mthcd, String yrcd, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndYrCd(String clientid,String yrcd, Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusIsNullAndBillDateBetween(final String clientid,List<String> invTypes, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusInAndMannualMatchInvoicesAndBillDateBetween(final String clientid,List<String> invTypes, List<String> matchingStatus, final String mannualMatchInvoices, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMatchingStatusInAndBillDateBetween(final String clientid,List<String> invTypes, List<String> matchingStatus, Date d1, Date d2, Pageable pageable);
	
	public Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIsNull(String clientId, List<String> invTypes, Date ystDate, Date yendDate, Pageable pageable);
	public Page<PurchaseRegister> findByClientidAndInvtypeInAndGstr2bMatchingStatusAndDateofinvoiceBetween(String clientId, List<String> invTypes, String matchStatus, Date ystDate, Date yendDate,  Pageable pageable);
	
	public Page<PurchaseRegister> findByClientidAndInvtypeInAndBillDateBetweenAndGstr2bMatchingStatusIsNull(String clientId, List<String> invTypes, Date ystDate, Date yendDate, Pageable pageable);
	public Page<PurchaseRegister> findByClientidAndInvtypeInAndGstr2bMatchingStatusAndBillDateBetween(String clientId, List<String> invTypes, String matchStatus, Date ystDate, Date yendDate,  Pageable pageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2, Pageable pageable);
	Page<PurchaseRegister> findByMatchingIdIn(List<String> matchingIds,  Pageable pageable);
	List<PurchaseRegister> findByGstr2bMatchingIdIn(List<String> matchingIds,  Pageable pageable);
	
	//optional methods
	Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusIn(String clientId, List<String> invTypes, Date ystDate, Date yendDate, List<String> statusIsNull, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIn(String clientId, List<String> invTypes, Date ystDate, Date yendDate, List<String> statusIsNull, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndBillDateBetweenAndMatchingStatusIn(String clientId, List<String> invTypes, Date ystDate, Date yendDate, List<String> statusIsNull, Pageable nextPageable);
	
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMthCdAndYrCdAndMatchingStatusIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMthCdAndYrCdAndGstr2bMatchingStatusIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndGstr2bMatchingStatusIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull, Pageable nextPageable);
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndMatchingStatusIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeAndInvoicenoInAndGstr2bMatchingStatusIn(String clientid, String invtype, List<String> invoiceNos, List<String> matchingstatuspr, Pageable pageable);
	
	
	Page<PurchaseRegister> findByClientidAndInvtypeInAndMthCdAndYrCdAndMatchingStatusInAndIdNotIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull,List<ObjectId> objectsIds, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndMatchingStatusInAndIdNotIn(String clientId, List<String> invTypes, String mthcd, String yrcd, List<String> statusIsNull,List<ObjectId> objectsIds, Pageable nextPageable);
	Page<PurchaseRegister> findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusInAndIdNotIn(String clientId, List<String> invTypes, Date ystDate, Date yendDate, List<String> statusIsNull,List<ObjectId> objectsIds, Pageable nextPageable);
	
	
}
