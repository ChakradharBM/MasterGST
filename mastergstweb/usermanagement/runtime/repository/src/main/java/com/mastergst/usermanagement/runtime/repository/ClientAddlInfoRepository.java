/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.ClientAddlInfo;

/**
 * Repository interface for Client Addl info to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface ClientAddlInfoRepository extends MongoRepository<ClientAddlInfo, String> {

	List<ClientAddlInfo> findByClientId(final String clientId);
	List<ClientAddlInfo> findByClientIdAndReturnType(final String clientId, final String returnType);
	List<ClientAddlInfo> findByClientIdAndReturnTypeAndMonthAndYear(final String clientId, final String returnType, final Integer month, final Integer year);
	ClientAddlInfo findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYear(final String clientId, final String returnType, String invoiceType, final String financialYear);
	ClientAddlInfo findByClientIdAndReturnTypeAndFinancialYear(final String clientId, final String returnType, final String financialYear);
	ClientAddlInfo findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYearAndMonth(final String clientId, final String returnType, String invoiceType, final String financialYear, final int month);
	ClientAddlInfo findByClientIdAndReturnTypeAndFinancialYearAndMonth(final String clientId, final String returnType, final String financialYear, final int month);
	List<ClientAddlInfo> findByClientIdAndInvoiceTypeAndReturnTypeAndFinancialYear(final String clientId, final String invoicetype, final String returnType, final String financialYear);
	
	@Transactional
	@Modifying
	void deleteByClientIdAndInvoiceTypeAndReturnTypeAndFinancialYear(final String clientId, final String invoicetype, final String returnType, final String financialYear);
}
