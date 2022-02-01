/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.EINVOICE;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;


/**
 * Repository interface for Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
public interface EinvoiceRepository extends MongoRepository<EINVOICE, String> {

	Page<? extends InvoiceParent> findByClientidAndDateofinvoiceBetween(String string, Date stDate, Date endDate, Pageable pageable);
	
	
}
