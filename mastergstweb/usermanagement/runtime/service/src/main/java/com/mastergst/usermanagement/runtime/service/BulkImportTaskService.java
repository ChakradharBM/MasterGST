package com.mastergst.usermanagement.runtime.service;

import java.util.List;

import com.mastergst.usermanagement.runtime.domain.BulkImportTask;
import com.mastergst.usermanagement.runtime.domain.Item;

public interface BulkImportTaskService {
	
	BulkImportTask createBulkImportTask(BulkImportTask bulkImportTask);
	
	List<BulkImportTask> getBulkImportTask(String userid,String clientid);
	
	public Item changeInvoiceAmounts(Item item);
}
