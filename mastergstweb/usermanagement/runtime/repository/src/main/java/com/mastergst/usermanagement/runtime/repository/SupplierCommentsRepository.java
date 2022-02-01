package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.SupplierComments;
public interface SupplierCommentsRepository extends MongoRepository<SupplierComments, String> {
	List<SupplierComments> findByInvoiceid(String invoiceid);
}
