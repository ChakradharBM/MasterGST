package com.mastergst.usermanagement.runtime.service;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.jsoup.select.Evaluator.IsEmpty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.beust.jcommander.internal.Maps;
import com.google.api.client.util.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.InventoryDao;
import com.mastergst.usermanagement.runtime.domain.CommonBO;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.StockAdjustments;
import com.mastergst.usermanagement.runtime.domain.StockAgingVO;
import com.mastergst.usermanagement.runtime.domain.StockItems;
import com.mastergst.usermanagement.runtime.domain.TotalStocksAmounts;
import com.mastergst.usermanagement.runtime.repository.CompanyItemsRepository;
import com.mastergst.usermanagement.runtime.repository.StockAdjustmentRepository;
import com.mastergst.usermanagement.runtime.support.CreditDebitNoteType;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService{
	private static final Logger logger = LogManager.getLogger(InventoryServiceImpl.class.getName());
	private static final String CLASSNAME = "InventoryServiceImpl::";
	
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private StockAdjustmentRepository stockAdjustmentRepository;
	@Autowired
	private CompanyItemsRepository companyItemsRepository;
	@Autowired
	private InventoryDao inventoryDao;
	
	/*Addition of inwards and outwards quantity start*/
	Predicate<StockAdjustments> inward = item -> item.getStockMovement().equals("In");
	Predicate<StockAdjustments> outward = item -> item.getStockMovement().equals("Out");
	Function<StockAdjustments, Double> inwardFunction = (item)->{ if(inward.test(item)) { return item.getStockItemQty();} return 0d;};
	Function<StockAdjustments, Double> outwardFunction = (item)->{ if(outward.test(item)) {return item.getStockItemQty();} return 0d;};
	/*Addition of inwards and outwards quantity End*/
	@Override
	public CommonBO getItemsList(String clientid, String query) {
		CommonBO commonBO = new CommonBO();
		commonBO.setItems(getSearchedItemsList(clientid, query));
		return commonBO;
	}
	
	private List<CompanyItems> getSearchedItemsList(String clientid, final String searchQuery) {
	
		List<CompanyItems> cmpyitemslist=getSearchedItems(clientid, searchQuery);
		
		cmpyitemslist.stream().forEach(item->{item.setItemnoAndDescription(item.getItemno());
		item.setUserid(item.getId().toString());
				});
		
		return cmpyitemslist;
	}
	private List<CompanyItems> getSearchedItems(final String clientid, final String searchQuery) {
		logger.debug(CLASSNAME + "getSearchedItems : Begin");
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "name"));
		query.addCriteria(Criteria.where("itemType").is("Product"));
		query.addCriteria(new Criteria().andOperator(
				Criteria.where("clientid").is(clientid),
				new Criteria().orOperator(Criteria.where("description").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)),Criteria.where("itemno").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)))
				));
		return mongoTemplate.find(query, CompanyItems.class, "companyitems");
	}
	
@Override
	public List<PurchaseOrder> getAllPOData(String id, String clientid, int month, int year, String searchQuery) {
	String yearCode = Utility.getYearCode(month, year);
	Query query = new Query();
	query.limit(10);
	query.with(new Sort(Sort.Direction.ASC, "invoiceno"));
	query.addCriteria(Criteria.where("clientid").is(clientid));
	query.addCriteria(Criteria.where("yrCd").is(yearCode));
	query.addCriteria(Criteria.where("invoiceno").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
	return mongoTemplate.find(query, PurchaseOrder.class, "purchaseorder");
	}
	@Override
	public Double getInvoiceItemsData(String clientid, String itemname) {
		Query query = new Query();
		query.addCriteria(Criteria.where("clientid").is(clientid));
		query.addCriteria(Criteria.where("items_itemno").is(itemname));
		List<GSTR1> invoices = mongoTemplate.find(query, GSTR1.class, "gstr1");
		Double qty= 0d;
		for(GSTR1 invoice : invoices) {
			for(Item item : invoice.getItems()) {
				if(isNotEmpty(item.getQuantity())) {
					qty += item.getQuantity();
				}
			}
		}
		return qty;
	}
	@Override
	public List<ProformaInvoices> getAllProformaData(String id, String clientid, int month, int year, String searchQuery) {
		String yearCode = Utility.getYearCode(month, year);
		Query query = new Query();
		query.limit(10);
		query.with(new Sort(Sort.Direction.ASC, "invoiceno"));
		query.addCriteria(Criteria.where("clientid").is(clientid));
		query.addCriteria(Criteria.where("yrCd").is(yearCode));
		query.addCriteria(Criteria.where("invoiceno").regex(Pattern.compile(searchQuery, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
		return mongoTemplate.find(query, ProformaInvoices.class, "proformainvoices");
	}
@Override
	public void saveStocks(StockAdjustments stockItem, ObjectId itemid) {
	if(isNotEmpty(stockItem) && isNotEmpty(stockItem.getStockMovement())) {
		if(stockItem.getStockMovement().equalsIgnoreCase("In")) {
			stockItem.setTransactionType("Add Stock");
		}else if(stockItem.getStockMovement().equalsIgnoreCase("Out")) {
			stockItem.setTransactionType("Reduce Stock");
		}
	}
	Date dt = null;
	dt = (Date)stockItem.getDateOfMovement();
	if(isNotEmpty(dt)) {
		int month = dt.getMonth();
		int year = dt.getYear()+1900;
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		
		stockItem.setMthCd(""+month);
		stockItem.setYrCd(""+yearCode);
		stockItem.setQrtCd(""+quarter);
		stockItem.setSftr(1);
	}
	stockAdjustmentRepository.save(stockItem);	
	}
private void saveMovementDate(Date dt, StockAdjustments stockItem, InvoiceParent invoice, String returntype) {
	int sftr = 1;
	if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
		if(returntype.equals(GSTR1) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
			sftr = "C".equals(CreditDebitNoteType.getNtty(invoice, returntype)) ? -1 : 1;
		}else {
			sftr = "D".equals(CreditDebitNoteType.getNtty(invoice, returntype)) ? 1 : -1;
		}
	}
	if(isNotEmpty(dt)) {
		int month = dt.getMonth();
		int year = dt.getYear()+1900;
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		
		stockItem.setMthCd(""+month);
		stockItem.setYrCd(""+yearCode);
		stockItem.setQrtCd(""+quarter);
		stockItem.setSftr(sftr);
	}
}

	

	@Override
	public void updateStockAdjustments(InvoiceParent invoice, String returntype) {
		String docType="";
		if(returntype.equalsIgnoreCase(GSTR1) || returntype.equals("SalesRegister")) {
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
					docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				}
			}
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
					docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
				}
			}
		}
		if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
			if(isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
				docType = invoice.getCdnur().get(0).getNtty();
			}
		}
		if(isNotEmpty(invoice)) {
			List<StockAdjustments> oldStocks = stockAdjustmentRepository.findByInvoiceId(invoice.getId().toString());
			Double oldtotalQtyVal = 0d;
			List<String> itemid = Lists.newArrayList();
			List<String> itemids = Lists.newArrayList();
			Map<String, Double> totalqty = Maps.newHashMap();
			for(StockAdjustments oldstock : oldStocks) {
				if(isNotEmpty(oldstock.getItemId())) {
					if(!itemid.contains(oldstock.getItemId().toString())) {
						itemid.add(oldstock.getItemId().toString());
						itemids.add(oldstock.getItemId().toString());
					}
				}
			}
			//itemids = itemid;
			Double totalQtyVal = 0d;
			Boolean flag = false;
			for(Item item : invoice.getItems()) {
				if(isNotEmpty(item.getQuantity()) && isNotEmpty(item.getItemId())) {
					if(!totalqty.containsKey(item.getItemId())) {
						totalqty.put(item.getItemId(), item.getQuantity());
					}else {
						totalqty.get(item.getItemId());
						totalqty.put(item.getItemId(), totalqty.get(item.getItemId())+item.getQuantity());
					}
				}
			}
			if(isNotEmpty(invoice.getItems())) {
				for(Item item : invoice.getItems()) {
					CompanyItems citems = null;
					if(isNotEmpty(item.getItemId())) {
						List<StockAdjustments> oldStocksbyitem = Lists.newArrayList();
						oldtotalQtyVal = 0d;
						if(isNotEmpty(item.getItemId()) && itemid.contains(item.getItemId())) {
							flag = false;
							oldStocksbyitem = stockAdjustmentRepository.findByInvoiceIdAndItemId(invoice.getId().toString(),new ObjectId(item.getItemId()));
							for(StockAdjustments oldstock : oldStocksbyitem) {
								if(isNotEmpty(oldstock.getStockItemQty())) {
									oldtotalQtyVal += oldstock.getStockItemQty();
								}
							}
							stockAdjustmentRepository.delete(oldStocksbyitem);
							itemid.remove(item.getItemId());
						}else if(!itemids.contains(item.getItemId())){
							flag = false;
						}
						citems = companyItemsRepository.findOne(item.getItemId());
						StockAdjustments stock = new StockAdjustments();
						stock.setClientid(invoice.getClientid());
						stock.setUserid(invoice.getUserid());
						if(isNotEmpty(invoice.getId())) {
							stock.setInvoiceId(invoice.getId().toString());
						}
						if(isNotEmpty(item.getQuantity())) {
							stock.setStockItemQty(item.getQuantity());
							totalQtyVal += item.getQuantity();
						}
						if(isNotEmpty(invoice.getDateofinvoice())) {
							stock.setDateOfMovement(invoice.getDateofinvoice());
							saveMovementDate(stock.getDateOfMovement(), stock, invoice, returntype);
						}
						if(isNotEmpty(item.getItemno())) {
							stock.setStockItemName(item.getItemno());
						}
						if(isNotEmpty(item.getUqc())) {
							stock.setStockUnit(item.getUqc());
						}
						if(isNotEmpty(item.getRateperitem())) {
							stock.setStockPurchaseCost(item.getRateperitem());
						}
						if(isNotEmpty(citems)) {
							stock.setItemId(citems.getId());
							if(isNotEmpty(citems.getItemno())) {
								stock.setStockItemNo(citems.getItemno());
							}
						}
						/*if(isNotEmpty(invoice.getBilledtoname())) {
							stock.setCustomerName(invoice.getBilledtoname());
						}*/
						if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister") || returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
								if("C".equals(docType)) {
									stock.setTransactionType("Credit Note");
									stock.setStockMovement("In");
								}else {
									if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
										stock.setTransactionType("Ewaybill Invoice");
									}else {
										stock.setTransactionType("Sales Invoice");
									}
									stock.setStockMovement("Out");
							   }
						}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
								if("D".equals(docType)) {
									stock.setTransactionType("Debit Note");
									stock.setStockMovement("Out");
								}else {
								stock.setTransactionType("Purchase Record");
								stock.setStockMovement("In");
							}
						}
						if(NullUtil.isEmpty(oldStocksbyitem)) {
							if(!flag) {
								saveStocks(docType, citems, item, stock, returntype, totalQtyVal);
							}
							if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
								stock.setCurrentStock(citems.getCurrentStock());
							}
						}else {
							if(!flag) {
								if(isNotEmpty(citems.getCurrentStock())) {
									Double cstock = citems.getCurrentStock();
									flag = editStocks(docType, citems, item, stock, returntype, totalqty.get(item.getItemId()),oldtotalQtyVal, flag);
									if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
										if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister") || returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
											if("C".equals(docType)) {
												stock.setCurrentStock(cstock + item.getQuantity() - oldtotalQtyVal);
											}else {
												stock.setCurrentStock(cstock + oldtotalQtyVal - item.getQuantity());
											}
										}else {
											if(docType.equalsIgnoreCase("D")) {
												stock.setCurrentStock(cstock + oldtotalQtyVal - item.getQuantity());
											}else {
												stock.setCurrentStock(cstock - oldtotalQtyVal + item.getQuantity());
											}
										}
									}
								}
							}else {
								if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
									stock.setCurrentStock(citems.getCurrentStock());
								}
							}
						}
						if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
							stockAdjustmentRepository.save(stock);
						}
				   }
				}
			}
		}
		
	}
	private Boolean editStocks(String docType, CompanyItems citems, Item item, StockAdjustments stock, String returntype,Double totalQtyVal,Double oldtotalQtyVal, Boolean flag) {
		Double ostock =0d;
		if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
			if(isNotEmpty(citems.getOpeningStock())) {
				ostock = citems.getOpeningStock();
			}
			if(isNotEmpty(citems.getStockAdjItemQty())) {
				ostock += citems.getStockAdjItemQty();
			}
		}	
		if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister") || returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
			if(docType.equalsIgnoreCase("C")) {
				Double usedqty = citems.getCurrentStock() + totalQtyVal - oldtotalQtyVal;
				citems.setCurrentStock(citems.getCurrentStock() + totalQtyVal - oldtotalQtyVal);
				//citems.setCurrentStock(citems.getOpeningStock() + totalQtyVal);
				citems.setTotalQtyUsage(ostock - usedqty);
			}else {
				Double usedqty = citems.getCurrentStock()+oldtotalQtyVal - totalQtyVal;
				citems.setCurrentStock(citems.getCurrentStock()+oldtotalQtyVal - totalQtyVal);
				//citems.setCurrentStock(citems.getOpeningStock() - totalQtyVal);
				citems.setTotalQtyUsage(ostock - usedqty);
			}
		}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
			if(docType.equalsIgnoreCase("D")) {
				citems.setCurrentStock(citems.getCurrentStock()+oldtotalQtyVal - totalQtyVal);
				//citems.setCurrentStock(citems.getOpeningStock() - totalQtyVal);
			}else {
				citems.setCurrentStock(citems.getCurrentStock()-oldtotalQtyVal + totalQtyVal);
				//citems.setCurrentStock(citems.getOpeningStock() + totalQtyVal);
			}
		}
		/*if(isNotEmpty(stock) && isNotEmpty(stock.getStockMovement()) && stock.getStockMovement().equals("Out")) {
			if(isNotEmpty(totalQtyVal)) {
				//citems.setTotalQtyUsage(citems.getTotalQtyUsage() + totalQtyVal);
				citems.setTotalQtyUsage(totalQtyVal);
			}
		}*/
		flag = true;
		companyItemsRepository.save(citems);
		return flag;
	}
	/*private Boolean editStocks(String docType, CompanyItems citems, Item item, StockAdjustments stock, String returntype, double oldVal, Double totalQtyVal, Boolean flag) {
		if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister") || returntype.equals(MasterGSTConstants.EINVOICE)) {
			if(docType.equalsIgnoreCase("C")) {
				citems.setCurrentStock(citems.getCurrentStock() + oldVal);
			}else {
				//citems.setCurrentStock(citems.getCurrentStock() - stock.getStockItemQty());
				citems.setCurrentStock(citems.getCurrentStock() - oldVal);
			}
		}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
			if(docType.equalsIgnoreCase("D")) {
				citems.setCurrentStock(citems.getCurrentStock() - oldVal);
			}else {
				citems.setCurrentStock(citems.getCurrentStock() + oldVal);
			}
		}
		if(isNotEmpty(stock) && isNotEmpty(stock.getStockMovement()) && stock.getStockMovement().equals("Out")) {
			if(isNotEmpty(totalQtyVal)) {
				if(isNotEmpty(citems.getTotalQtyUsage())) {
					//citems.setTotalQtyUsage(citems.getTotalQtyUsage() + totalQtyVal);
					citems.setTotalQtyUsage(totalQtyVal);
				}
			}
		}
		flag = true;
		companyItemsRepository.save(citems);
		return flag;
	}*/

	private void saveStocks(String docType, CompanyItems citems, Item item, StockAdjustments stockAdj, String returntype, Double totalQtyVal) {
		Double stock =0d;
		Double ostock =0d;
		if(isNotEmpty(citems) && isNotEmpty(citems.getCurrentStock())) {
			stock = citems.getCurrentStock();
			if(isNotEmpty(citems.getOpeningStock())) {
				ostock = citems.getOpeningStock();
			}
			if(isNotEmpty(citems.getStockAdjItemQty())) {
				ostock += citems.getStockAdjItemQty();
			}
			if(returntype.equals(GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
				if(docType.equals("C")) {
					stock += item.getQuantity();
					totalQtyVal = ostock - stock;
				}else {
					stock -= item.getQuantity();
					totalQtyVal = ostock - stock;
				}
				citems.setTotalQtyUsage(totalQtyVal);
			}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
				if(docType.equals("D")) {
					stock -= item.getQuantity();
				}else {
					stock += item.getQuantity();
				}
			}
			citems.setCurrentStock(stock);
			
			/*if(isNotEmpty(stockAdj) && isNotEmpty(stockAdj.getStockMovement()) && stockAdj.getStockMovement().equals("Out")) {
				if(isNotEmpty(totalQtyVal)) {
					citems.setTotalQtyUsage(totalQtyVal);
				}
			}*/
			companyItemsRepository.save(citems);
		}
		
	}

	@Override
	public Page<? extends StockAdjustments> getStockDetails(Pageable pageable, String clientid, String itemname, int month, int year, int start, int length, String searchVal) {
		String yearCode = Utility.getYearCode(month, year);
		Page<? extends StockAdjustments> stocks = inventoryDao.findByClientid(clientid, itemname, month, yearCode, start, length, searchVal, pageable);
		return stocks;
	}
	@Override
	public Page<? extends StockAdjustments> getCustomStockDetails(Pageable pageable, String clientid, String itemname, String fromtime, String totime, int start, int length, String searchVal) {
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		Page<? extends StockAdjustments> stocks = inventoryDao.findByClientidAndFromtimeAndTotime(clientid, itemname, stDate, endDate, start, length, searchVal);
		return stocks;
	}
	
	@Override
	public Page<StockItems> getStockLedgerDetails(Pageable pageable, String clientid, String itemname, int month, int year, int start, int length, String searchVal) {
		String yearCode = Utility.getYearCode(month, year);
		Page<StockItems> stocks = inventoryDao.getStockLedgerDetails(clientid, itemname, month, yearCode, start, length, searchVal);
		stocks.forEach(item->{
			double intotal = 0d;
			double outtotal = 0d;
			for(StockAdjustments adj : item.getStocks()) {
				intotal += inwardFunction.apply(adj);
				outtotal += outwardFunction.apply(adj);
			}
			item.setTotInQty(intotal);
			item.setTotOutQty(outtotal);
			item.setTotBalQty(intotal-outtotal);
		});
		return stocks;
	}
@Override
	public Page<StockItems> getCustomStockLedgerDetails(Pageable pageable, String clientid, String itemname, String fromtime, String totime, int start, int length, String searchVal) {
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		Page<StockItems> stocks = inventoryDao.getCustomStockLedgerDetails(clientid, itemname, stDate, endDate, start, length, searchVal);
		stocks.forEach(item->{
			double intotal = 0d;
			double outtotal = 0d;
			for(StockAdjustments adj : item.getStocks()) {
				intotal += inwardFunction.apply(adj);
				outtotal += outwardFunction.apply(adj);
			}
			item.setTotInQty(intotal);
			item.setTotOutQty(outtotal);
			item.setTotBalQty(intotal-outtotal);
		});
		return stocks;
	}
	@Override
	public Map<String, Object> getStockSalesSummaryDetails(Pageable pageable, String clientid, int month, int year, int start, int length, String searchVal) {
		Map<String, Object> stocksMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends CompanyItems> items = null;
		TotalStocksAmounts totalQty = null;
		String yearCode = Utility.getYearCode(month, year);
		items = inventoryDao.getStockSalesSummaryDetails(clientid, month, yearCode, start, length, searchVal);
		totalQty = inventoryDao.getTotalQuantityAmounts(clientid, month, yearCode, start, length, searchVal);
		stocksMap.put("items", items);
		stocksMap.put("totalQtyAmts", totalQty);
		return stocksMap;
	}
	public void changeQuantityInMasters(InvoiceParent invoice, String returntype) {
		if(isNotEmpty(invoice)) {
			String docType="";
			if(returntype.equalsIgnoreCase(GSTR1) || returntype.equals("SalesRegister")) {
				if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					if(isNotEmpty(((GSTR1) invoice).getCdnr()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt()) && isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
						docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
					}
				}
			}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase("Purchase Register")) {
				if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
						docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
					}
				}
			}
			if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
				if(isNotEmpty(invoice.getCdnur()) && isNotEmpty(invoice.getCdnur().get(0).getNtty()) && isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
					docType = invoice.getCdnur().get(0).getNtty();
				}
			}
			stockAdjustmentRepository.deleteByInvoiceId(invoice.getId().toString());
			for (Item item : invoice.getItems()) {
				if(isNotEmpty(item.getItemId())) {
					CompanyItems citems = null;
					Double stock = 0d;
					Double qty = 0d;
					if(isNotEmpty(item.getItemno())) {
						citems = companyItemsRepository.findOne(item.getItemId());
					}
					if(isNotEmpty(item.getQuantity())) {
						if(isNotEmpty(citems)) {
							if(isNotEmpty(citems.getCurrentStock())) {
								stock = citems.getCurrentStock();
								if(returntype.equals(GSTR1) || returntype.equals("Sales Register")) {
									if(docType.equalsIgnoreCase("C")) {
										stock -= item.getQuantity();
									}else {
										stock += item.getQuantity();
									}
								}else if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals("Purchase Register")) {
									if(docType.equalsIgnoreCase("D")) {
										stock += item.getQuantity();
									}else {
										stock -= item.getQuantity();
									}
								}
								citems.setCurrentStock(stock);
						    }
							if(returntype.equals(GSTR1) || returntype.equals("Sales Register")) {
								if(isNotEmpty(citems.getTotalQtyUsage())) {
									qty = citems.getTotalQtyUsage();
									qty -= item.getQuantity();
									citems.setTotalQtyUsage(qty);
								}
							}
							companyItemsRepository.save(citems);
						}
					}
				}
			}
		}
	}
	
	
@Override
	public Map<String, Object> getLowStockItemDetails(Pageable pageable, String clientid, int month, int year, int start, int length, String searchVal) {
		Map<String, Object> stocksMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends CompanyItems> items = null;
		TotalStocksAmounts totalQty = null;
		String yearCode = Utility.getYearCode(month, year);
		items = inventoryDao.getStockSalesSummaryDetails(clientid, month, yearCode, start, length, searchVal);
		totalQty = inventoryDao.getTotalQuantityAmounts(clientid, month, yearCode, start, length, searchVal);
		stocksMap.put("items", items);
		stocksMap.put("totalQtyAmts", totalQty);
		return stocksMap;
	}
	@Override
	public Map<String, Object> getStockAgingDetails(Pageable pageable, String clientid, int intervalValue, int month, int year, int start, int length, String searchVal) {
		Map<String, Object> stocksMap = new HashMap<String, Object>();
		List<StockAgingVO> astock = Lists.newArrayList();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends StockAdjustments> stocks = null;
		int j = 0;
		if(intervalValue == 30) {
			j = 6;
		}else if(intervalValue == 90) {
			j = 3;
		}else if(intervalValue == 180) {
			j = 2;
		}
		Calendar c = Calendar.getInstance();   
	    c.set(Calendar.DAY_OF_MONTH, 1);
		Date startDate = c.getTime();
		Date endDate = c.getTime();
		for(int i=0;i<j;i++) {
			//System.out.println(startDate+"----"+endDate);
			stocks = inventoryDao.findByClientidAndFromtimeAndTotime(clientid, startDate, endDate, start, length, searchVal);
			c.add(Calendar.DATE, 1); 
			startDate = c.getTime();
			c.add(Calendar.DATE, intervalValue); 
			endDate = c.getTime();
			stocksMap = getStocksBasedOnInterval(stocksMap, stocks, astock, i, intervalValue);
		}
		return stocksMap;
	}
	private Map<String, Object> getStocksBasedOnInterval(Map<String, Object> stocksMap, Page<? extends StockAdjustments> stocks, List<StockAgingVO> astock, int val, int intervalValue) {
		double qty = 0d;
		//stocks.getContent().forEach(stock->{
		
		for(StockAdjustments stock :stocks.getContent()) {
			StockAgingVO stockVO = new StockAgingVO();
			if(isNotEmpty(stock.getStockItemName())){
				stockVO.setItem(stock.getStockItemName());
			}
			if(isNotEmpty(stock.getStockUnit())){
				stockVO.setUqc(stock.getStockUnit());
			}
			if(isNotEmpty(stock.getCurrentStock())){
				stockVO.setCurrentStock(stock.getCurrentStock());
			}
			if(isNotEmpty(stock.getStockItemQty())){
				qty += 	stock.getStockItemQty();
			}
			int column = val * intervalValue;
			if(column == 0) {
				stockVO.setDays0(qty);
			}else if(column == 30) {
				stockVO.setDays30(qty);
			}else if(column == 60) {
				stockVO.setDays60(qty);
			}else if(column == 90) {
				stockVO.setDays90(qty);
			}else if(column == 120) {
				stockVO.setDays120(qty);
			}else if(column == 150) {
				stockVO.setDays150(qty);
			}
			astock.add(stockVO);
		//});
	   }
		stocksMap.put("stocks",new PageImpl(astock));
		return stocksMap;
	}
	@Override
	public List<CompanyItems> getReportSummaryItems(List<CompanyItems> itm) {
		List<CompanyItems> itmList = Lists.newArrayList();
		if(isNotEmpty(itm)) {
			for (CompanyItems item : itm) {
				CompanyItems items = new CompanyItems();
				String qty =  "";
				String usage = "";
				if(isNotEmpty(item.getItemno())) {
					items.setItemno(item.getItemno());
				}
				if(isNotEmpty(item.getDescription())) {
					items.setDescription(item.getDescription());
				}
				if(isNotEmpty(item.getSalePrice())) {
					items.setSalePrice(item.getSalePrice());
				}
				if(isNotEmpty(item.getPurchasePrice())) {
					items.setPurchasePrice(item.getPurchasePrice());
				}
				if(isNotEmpty(item.getMrpPrice())) {
					items.setMrpPrice(item.getMrpPrice());
				}else {
					items.setMrpPrice(0d);
				}
				if(isNotEmpty(item.getCurrentStock())) {
					qty = item.getCurrentStock().toString();
					  if(isNotEmpty(item.getUnit())) {
						qty = qty+" "+item.getUnit();
					  }
				   items.setFullname(qty);
				}
				if(isNotEmpty(item.getCurrentStock()) && isNotEmpty(item.getPurchasePrice())) {
					double price = item.getCurrentStock() * item.getPurchasePrice();
					items.setWholeSalePrice(price);
				}
				if(isNotEmpty(item.getTotalQtyUsage())) {
					usage = item.getTotalQtyUsage().toString();
					if(isNotEmpty(item.getUnit())) {
						usage = usage+" "+item.getUnit();
					  }
				}
				items.setClientid(usage);
				itmList.add(items);
			}
		}
		return itmList;
	}
	@Override
	public List<StockAdjustments> getStockReportDetail(List<StockAdjustments> stocks) {
		List<StockAdjustments> stockList = Lists.newArrayList();
		if(isNotEmpty(stocks)) {
			for (StockAdjustments stock : stocks) {
				String stockqty="";
				StockAdjustments stockadj = new StockAdjustments();
				if(isNotEmpty(stock.getDateOfMovement())) {
					stockadj.setDateOfMovement(stock.getDateOfMovement());
				}
				if(isNotEmpty(stock.getStockItemName())) {
					stockadj.setStockItemName(stock.getStockItemName());
				}
				if(isNotEmpty(stock.getTransactionType())) {
					stockadj.setTransactionType(stock.getTransactionType());
				}
				if(isNotEmpty(stock.getStockMovement()) && "Out".equals(stock.getStockMovement())) {
					stockqty = "-";
				}
				if(isNotEmpty(stock.getStockItemQty())) {
					stockqty += stock.getStockItemQty()+" ";
				}
				if(isNotEmpty(stock.getStockUnit())) {
					stockqty += stock.getStockUnit();
				}
				stockadj.setFullname(stockqty);
				if(isNotEmpty(stock.getCurrentStock())) {
					if(isNotEmpty(stock.getStockUnit())) {
						stockadj.setStockComments(stock.getCurrentStock()+" "+stock.getStockUnit());
					}
				}
				stockList.add(stockadj);
			}
		}
		return stockList;
	}
	@Override
	public Page<? extends StockItems> getCustomerWiseItemDetails(Pageable pageable, String clientid, String custName, int month, int year, int start, int length, String searchVal) {
		
		return null;
	}

	@Override
	public void deleteStocks(String itemid) {
		stockAdjustmentRepository.deleteByItemId(new ObjectId(itemid));
	}
}
