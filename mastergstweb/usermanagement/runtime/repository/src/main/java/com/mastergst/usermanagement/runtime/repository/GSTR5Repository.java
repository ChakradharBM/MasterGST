package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.GSTR5;

/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

public interface GSTR5Repository extends MongoRepository<GSTR5, String>{
	
	List<GSTR5> findByClientid(final String clientid);
	Page<GSTR5> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<GSTR5> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	Page<GSTR5> findByClientidAndIsAmendmentAndDateofinvoiceBetween(final String clientid, boolean isAmendment, Date d1, Date d2, Pageable pageable);
	List<GSTR5> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<GSTR5> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<GSTR5> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	GSTR5 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	List<GSTR5> findByClientidAndFpAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<GSTR5> findByClientidAndGstStatusAndFpIn(final String clientid, final String gstStatus, final List<String> fps);
	List<GSTR5> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<GSTR5> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR5> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<GSTR5> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	List<GSTR5> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR5> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	List<GSTR5> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
}
