package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.NewAnx1;
import com.mastergst.usermanagement.runtime.domain.NewAnx2;

/**
 * Repository interface for ANX1 Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

public interface NewAnx2Repository extends MongoRepository<NewAnx2, String>{
	
	List<NewAnx2> findByClientid(final String clientid);
	Page<NewAnx2> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<NewAnx2> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	List<NewAnx2> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<NewAnx2> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<NewAnx2> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	NewAnx2 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	NewAnx2 findByClientidAndFpAndInvoiceno(final String clientid, final String fp, final String invoiceno);
	List<NewAnx2> findByClientidAndRtnprdAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<NewAnx2> findByClientidAndGstStatusAndRtnprdIn(final String clientid, final String gstStatus, final List<String> fps);
	List<NewAnx2> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<NewAnx2> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<NewAnx2> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<NewAnx2> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	List<NewAnx2> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<NewAnx2> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	List<NewAnx2> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
}
