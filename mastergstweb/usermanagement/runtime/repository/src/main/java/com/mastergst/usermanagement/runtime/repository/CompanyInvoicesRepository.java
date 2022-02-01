/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;

/**
 * Repository interface for Invoices to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface CompanyInvoicesRepository extends MongoRepository<CompanyInvoices, String> {
	CompanyInvoices findById(String id);
	List<CompanyInvoices> findByUserid(final String userid);
	List<CompanyInvoices> findByUseridAndClientid(final String userid, final String clientid);
	List<CompanyInvoices> findByUseridAndClientidAndYear(final String userid, final String clientid, final String year);
	CompanyInvoices findByClientidAndYearAndInvoiceType(final String clientid, final String year, final String invoiceType);
	CompanyInvoices findByUseridAndClientidAndYearAndInvoiceType(final String userid, final String clientid, final String year, final String invoiceType);
	CompanyInvoices findByUseridAndClientidAndYearAndInvoiceTypeAndReturnType(final String userid, final String clientid, final String year, final String invoiceType, final String returnType);
	CompanyInvoices findByClientidAndYearAndInvoiceTypeAndReturnType(final String clientid, final String year, final String invoiceType, final String returnType);
	List<CompanyInvoices> findByClientid(final String clientid);
	List<CompanyInvoices> findByClientidAndYear(final String clientid, final String year);
}
