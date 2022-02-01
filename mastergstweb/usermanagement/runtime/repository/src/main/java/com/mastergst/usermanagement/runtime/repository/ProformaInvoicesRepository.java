package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;

public interface ProformaInvoicesRepository extends MongoRepository<ProformaInvoices, String> {
	Page<ProformaInvoices> findByClientidAndDateofinvoiceBetween(final String clientid, Date d1, Date d2,Pageable pageable);
	Page<ProformaInvoices> findByClientidAndDateofinvoiceBetweenAndBranchIn(final String clientid, Date d1, Date d2,Pageable pageable,final List<String> branch);
	List<ProformaInvoices> findByUseridAndClientidAndDateofinvoiceBetween(final String userid, final String clientid, Date d1, Date d2);
	List<ProformaInvoices> findByUseridAndClientidAndInvtypeInAndDateofinvoiceBetween(final String userid,final String clientid, final List<String> invtype, Date d1, Date d2);
	List<ProformaInvoices> findByClientidAndInvoicenoAndDateofinvoiceBetween(final String clientid, final String invoiceno, Date d1, Date d2);
}
