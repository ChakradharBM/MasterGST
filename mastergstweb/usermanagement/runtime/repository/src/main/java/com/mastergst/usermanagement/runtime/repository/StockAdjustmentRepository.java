package com.mastergst.usermanagement.runtime.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.bson.types.ObjectId;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.StockAdjustments;

public interface StockAdjustmentRepository extends MongoRepository<StockAdjustments, String> {

	@Transactional
	@Modifying
	void deleteByInvoiceId(final String invoiceid);

	List<StockAdjustments> findByInvoiceId(String string);

	List<StockAdjustments> findByClientid(String clientid);
	
	List<StockAdjustments> findByInvoiceIdAndItemId(String invoiceid, ObjectId id);

	StockAdjustments findByClientidAndTransactionTypeAndItemId(String clientid, String string, ObjectId id);

	List<StockAdjustments> findByItemId(ObjectId objectId);
	
	@Transactional
	@Modifying
	void deleteByItemId(final ObjectId objectId);
	

}
