package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import org.springframework.data.domain.Page;
public interface EstimatesRepository extends MongoRepository<Estimates, String> {
	Page<Estimates> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2,Pageable pageable);
	Page<Estimates> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2,Pageable pageable,final List<String> branch);
	List<Estimates> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<Estimates> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<Estimates> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
}
