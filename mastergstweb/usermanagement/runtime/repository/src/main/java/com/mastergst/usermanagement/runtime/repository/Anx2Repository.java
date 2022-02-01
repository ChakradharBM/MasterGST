
package com.mastergst.usermanagement.runtime.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;


import com.mastergst.usermanagement.runtime.domain.Anx2;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

/**
 * Repository interface for ANX2 Invoice to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

public interface Anx2Repository extends MongoRepository<Anx2, String>{
	Anx2 findByClientidAndFpAndInvoiceno(final String clientid, final String fp, final String invoiceno);
	List<Anx2> findByClientidAndInvoicenoIn(String string, List<String> receivedInvIds);
	Page<? extends InvoiceParent> findByClientidAndDateofinvoiceBetween(String string, Date stDate, Date endDate,Pageable pageable);
	
	
}
