package com.mastergst.usermanagement.runtime.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mastergst.usermanagement.runtime.domain.CommonBO;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.StockAgingVO;
import com.mastergst.usermanagement.runtime.domain.StockItems;

public interface InventoryService {

	CommonBO getItemsList(String clientid, String query);

	List<PurchaseOrder> getAllPOData(String id, String clientid, int month, int year, String query);

	Double getInvoiceItemsData(String clientid, String itemname);

	List<ProformaInvoices> getAllProformaData(String id, String clientid, int month, int year, String query);

	void saveStocks(StockAdjustments stockItem, ObjectId objectId);

	void updateStockAdjustments(InvoiceParent invoice, String returntype);

	Page<? extends StockAdjustments> getStockDetails(Pageable pageable, String clientid, String itemname, int month, int year, int start, int length, String searchVal);

	Page<? extends StockAdjustments> getCustomStockDetails(Pageable pageable, String clientid, String itemname, String fromtime, String totime, int start, int length, String searchVal);

	Page<StockItems> getStockLedgerDetails(Pageable pageable, String clientid, String itemname, int month, int year, int start, int length, String searchVal);

	Page<StockItems> getCustomStockLedgerDetails(Pageable pageable, String clientid, String itemname, String fromtime, String totime, int start, int length, String searchVal);

	Map<String, Object> getStockSalesSummaryDetails(Pageable pageable, String clientid, int month, int year, int start, int length, String searchVal);

	void changeQuantityInMasters(InvoiceParent invoice, String returntype);

	Page<? extends StockItems> getCustomerWiseItemDetails(Pageable pageable, String clientid, String custName,
			int month, int year, int start, int length, String searchVal);

	Map<String, Object> getLowStockItemDetails(Pageable pageable, String clientid, int month, int year,
			int start, int length, String searchVal);

	Map<String, Object> getStockAgingDetails(Pageable pageable, String clientid, int intervalValue, int month, int year, int start,
			int length, String searchVal);
	void deleteStocks(String itemid);
	List<CompanyItems> getReportSummaryItems(List<CompanyItems> itm);

	List<StockAdjustments> getStockReportDetail(List<StockAdjustments> stock);

}
