package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;

public interface DeliveryChallanRepository extends MongoRepository<DeliveryChallan, String> {
	Page<DeliveryChallan> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2,Pageable pageable);
	Page<DeliveryChallan> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2,Pageable pageable,final List<String> branch);
	List<DeliveryChallan> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<DeliveryChallan> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<DeliveryChallan> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
}
