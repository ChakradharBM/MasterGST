package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.NewAnx1;

/**
 * Repository interface for ANX1 Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

public interface NewAnx1Repository extends MongoRepository<NewAnx1, String>{
	
	List<NewAnx1> findByClientid(final String clientid);
	Page<NewAnx1> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2, Pageable pageable);
	Page<NewAnx1> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2, Pageable pageable,List<String> branch);
	List<NewAnx1> findByClientidAndGstStatusNotInAndDateofinvoiceBetween(final String clientid, List<String> list, 
			Date d1, Date d2);
	List<NewAnx1> findByClientidAndGstStatusNotInAndDateofinvoiceBetweenAndBranchIn(final String clientid, List<String> list, 
			Date d1, Date d2,List<String> branch);
	Page<NewAnx1> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2, Pageable pageable);
	NewAnx1 findByClientidAndInvtypeAndInvoiceno(final String clientid, final String invtype, final String invoiceno);
	NewAnx1 findByClientidAndFpAndInvoiceno(final String clientid, final String fp, final String invoiceno);
	List<NewAnx1> findByClientidAndRtnprdAndGstStatus(final String clientid, final String fp, final String gstStatus);
	List<NewAnx1> findByClientidAndGstStatusAndRtnprdIn(final String clientid, final String gstStatus, final List<String> fps);
	List<NewAnx1> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2);
	List<NewAnx1> findByClientidAndInvtypeAndDateofinvoiceBetween(final String clientid, final String invtype, Date d1, Date d2);
	List<NewAnx1> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<NewAnx1> findByUseridAndClientidAndInvtypeAndDateofinvoiceBetween(final String userid, final String clientid, final String invtype, Date d1, Date d2);
	List<NewAnx1> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<NewAnx1> findByClientidAndInvtypeInAndDateofinvoiceBetween(final String clientid, final List<String> invtype, Date d1, Date d2);
	List<NewAnx1> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
	
	@Transactional
	@Modifying
	void deleteByClientid(final String clientid);
	List<NewAnx1> findByClientidAndInvoicenoIn(String string, List<String> receivedInvIds);
	List<NewAnx1> findByClientidAndFpAndInvtype(String string, String fp, String b2c);
}
