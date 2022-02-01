package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


import com.mastergst.usermanagement.runtime.domain.Payments;

public interface RecordPaymentsRepository extends MongoRepository<Payments, String> {
	
	List<Payments> findByClientidAndInvoiceNumber(final String clientid,final String invoiceNumber);
	List<Payments> findByClientidAndInvoiceid(final String clientid, final String invoiceid);
	List<Payments> findByClientid(final String clientid);
	List<Payments> findByClientidAndReturntype(final String clientid,final String returntype);
	List<Payments> findByInvoiceid(final String invoiceid);
	
	Page<Payments> findByClientidInAndCreatedDateBetween(List<String> clientids,Date d1,Date d2,Pageable pageable);
	
	
	Page<Payments> findByClientidInAndReturntypeAndCreatedDateBetween(List<String> clientids,String returntype, Date d1,Date d2,Pageable pageable);
	Page<Payments> findByClientidInAndReturntypeInAndCreatedDateBetween(List<String> clientids,final List<String> returntype, Date d1,Date d2,Pageable pageable);
	List<Payments> findByInvoiceidAndReturntype(final String invoiceid,String returnntype);
	List<Payments> findByInvoiceidAndReturntypeIn(final String invoiceid,final List<String> returnntype);
	List<Payments> findByClientidAndReturntypeIn(final String invoiceid,final List<String> returnntype);
	
	Payments findByClientidAndVoucherNumberAndInvoiceNumberAndReturntypeIn(final String clientid, final String voucherNumber, final String invoiceNumber, final List<String> returntype);
	Payments findByClientidAndVoucherNumberAndReturntypeIn(final String clientid,final String voucherNumber,final List<String> returntype);
	
	Payments findById(String docid);
	List<Payments> findByClientidAndReturntypeAndYrCd(final String clientid,final String returntype,final String yearCode);
}
