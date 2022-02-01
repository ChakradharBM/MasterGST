package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beust.jcommander.internal.Lists;
import com.google.api.client.util.Maps;
import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.Gstr2Dao;
import com.mastergst.usermanagement.runtime.dao.InvoicesMappingDao;
import com.mastergst.usermanagement.runtime.dao.PurchageRegisterDao;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceMap;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.ClientConfigRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.support.ReconcileUtility;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
public class InvoicesMappingServiceImpl implements InvoicesMappingService {
	private static final Logger logger = LogManager.getLogger(InvoicesMappingServiceImpl.class.getName());
	private static final String CLASSNAME = "InvoicesMappingServiceImpl::";
		
	@Autowired
	private InvoicesMappingDao invoicesMappingDao;
	@Autowired ClientConfigRepository clientConfigRepository;
	@Autowired Gstr2Dao gstr2Dao;
	@Autowired GSTR2Repository gstr2Repository;
	@Autowired PurchaseRegisterRepository purchaseRepository;
	@Autowired PurchageRegisterDao purchageRegisterDao;
	@Autowired ReconcileTempRepository reconcileTempRepository;
	@Autowired private ClientService clientService;
	@Autowired private ReconcileUtility reconcileUtility;
	
	@Autowired
	private MongoTemplate mongoTemplate;
		
	@Override
	public Map<String, Object> getInvoicesSupport(String clientid, int month, int year, boolean isMonthly, boolean isTransactionDate) {
		String yearCd = null;
		if(isMonthly) {
			yearCd = Utility.getYearCode(month, year);						
		}else {
			month = 0;
			yearCd = Utility.getYearCode(4, year);			
		}
		Map<String, Object> supportObj = new HashMap<>();
		List<String> billedtoNames = invoicesMappingDao.getBillToNames(clientid, month, yearCd, isMonthly,isTransactionDate);
		
		supportObj.put("billedtoNames", billedtoNames);
		return supportObj;
	}
	
	@Override
	public Map<String, Object> getInvoices(String clientid, int month, int year, boolean isMonthly, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate){
		String yearCd = null;
		if(isMonthly) {
			yearCd = Utility.getYearCode(month, year);						
		}else {
			month = 0;
			yearCd = Utility.getYearCode(4, year);
		}
		Page<PurchaseRegister> invoices = invoicesMappingDao.getInvoices(clientid, month, yearCd, start, length, searchVal, filter,isTransactiondate,isMonthly);
		long total = invoices.getTotalElements();
		
		Map<String, Object> invMap = Maps.newHashMap();
		List<InvoiceMap> mappedInvs = Lists.newArrayList();
		if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
			List<String> matchedIds = Lists.newArrayList();
			if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
				for(PurchaseRegister prInv : invoices.getContent()) {
					prInv.setUserid(prInv.getId().toString());
					if(NullUtil.isNotEmpty(prInv.getMatchingStatus())) {
						matchedIds.add(prInv.getMatchingId());
					}
				}
			}
			List<GSTR2> gstr2aInvs = invoicesMappingDao.getGstr2aInvoices(matchedIds);
			if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
				if(NullUtil.isNotEmpty(gstr2aInvs)) {
					for(GSTR2 gstr2 : gstr2aInvs) {
						gstr2.setUserid(gstr2.getId().toString());
					}
				}
			}
			if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
				for(PurchaseRegister prinv : invoices.getContent()) {
					InvoiceMap invice = new InvoiceMap();
					invice.setOrigin(MasterGSTConstants.PURCHASE_REGISTER);
					
					if(NullUtil.isNotEmpty(prinv.getMatchingStatus()) && NullUtil.isNotEmpty(prinv.getMatchingId())) {
						if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
							for(GSTR2 gstr2 : gstr2aInvs) {
								if(gstr2.getId().toString().equals(prinv.getMatchingId())) {
									gstr2.setUserid(gstr2.getId().toString());
									invice.setGstr2(gstr2);
								}
							}
						}
					}
					invice.setPurchaseRegister(prinv);
					mappedInvs.add(invice);
				}
			}
		}
		Map<String, Object> map = customPageable(total, invoices, clientid, month, yearCd, start, length, searchVal, filter,isTransactiondate,isMonthly);
		
		mappedInvs.addAll((List) map.get("gstr2aInvoices"));
		invMap.put("total", map.get("total"));
		invMap.put("mappedInvs", mappedInvs);
		
		TotalInvoiceAmount invAmounts = getInvoiceTotals(clientid, month, year, yearCd, searchVal, filter,isTransactiondate,isMonthly);
		invMap.put("amts", invAmounts);
		return invMap;
	}
	
	private Map<String, Object> customPageable(long total ,Page<PurchaseRegister> invoices, String clientid, int month, String yearCd, int start, int length, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear){
		Map<String, Object> invMap = Maps.newHashMap();
		long gstr2aInvsCount = invoicesMappingDao.getGstr2aInvoicesCount(clientid, month, yearCd, start, length, searchVal, filter,isTransactiondate,isYear);
		int size = (int) invoices.getTotalElements();
		int val = length - invoices.getContent().size();
		if(invoices.getContent().size() == 0) {
			val = start - size;
		}
		int i = 0;
		List<InvoiceMap> mappedInvs = Lists.newArrayList();
		if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
			int st = 0;
			Page<GSTR2> gstr2aInvs = invoicesMappingDao.getGstr2aInvoiceswithpagination(clientid, month, yearCd, st, length, searchVal, filter,isTransactiondate,isYear);
			if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(gstr2aInvs.getContent())) {
				for(GSTR2 gstr2 : gstr2aInvs.getContent()) {
					if(NullUtil.isNotEmpty(gstr2)) {
						if(i < val) {
							gstr2.setUserid(gstr2.getId().toString());
							InvoiceMap invoice = new InvoiceMap();
							invoice.setGstr2(gstr2);
							invoice.setOrigin(MasterGSTConstants.GSTR2A);
							mappedInvs.add(invoice);
							i++;
						}if(i > val) {
							break;
						}
					}
				}
			}
		}
		
		
		int j = 0, k = 0;
		if(NullUtil.isEmpty(invoices.getContent())) {
			Page<GSTR2> gstr2aInvs = null;
			
			int pageLength = 100;
			/*
			 int pageSize = (int) (gstr2bInvsCount / length);
			 int remainder = (int) (gstr2bInvsCount % length);
			 */
			 int pageSize = (int) (gstr2aInvsCount / pageLength);
			 int remainder = (int) (gstr2aInvsCount % pageLength);
			if(remainder > 0) {
				pageSize += 1;
			}
			boolean flag = false;
			
			
			/*
				int numbers = (int) (gstr2aInvsCount / 100);
				int remainder = (int) (gstr2aInvsCount % 100);
				if(remainder > 0) {
					numbers =+ 1;
				}
				boolean flag = false;
			 */
			for(int st = 0; st < pageSize; st++) {
				int n = 0; 
				if(st != 0) {
					//n = st * length;
					n = st * pageLength;
				}
			//for(int st = 0; st <= numbers; st++) {
				//gstr2aInvs = invoicesMappingDao.getGstr2aInvoiceswithpagination(clientid, month, yearCd, st, length, searchVal, filter,isTransactiondate,isYear);
				if(flag) {
					break;
				}
				gstr2aInvs = invoicesMappingDao.getGstr2aInvoiceswithpagination(clientid, month, yearCd, n, pageLength, searchVal, filter,isTransactiondate,isYear);
				
				if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(gstr2aInvs.getContent())) {	
					for(GSTR2 gstr2 : gstr2aInvs) {
						if(j < val) {
							j++;
						}else {
							if(NullUtil.isNotEmpty(gstr2)) {
								if(k < length) {
									gstr2.setUserid(gstr2.getId().toString());
									InvoiceMap invoice = new InvoiceMap();
									invoice.setGstr2(gstr2);
									invoice.setOrigin(MasterGSTConstants.GSTR2A);
									mappedInvs.add(invoice);
									//k++;
									++k;
								}
								if(k == gstr2aInvsCount || k >= length) {
									flag = true;
									break;
								}
							}	
						}
					}
				}
			}
		}
		long lstSize = gstr2aInvsCount;
		if(lstSize == 0) {
			lstSize = i;
		}
		total = total + lstSize;
		invMap.put("gstr2aInvoices", mappedInvs);
		invMap.put("total", total);
		return invMap;
	}
	
	public TotalInvoiceAmount getInvoiceTotals(String clientid, int month, int year, String yearCd, String searchVal, InvoiceFilter filter,boolean isTransactiondate,boolean isYear) {
		
		TotalInvoiceAmount prAmounts = invoicesMappingDao.getPurchaseTotalInvoicesAmounts(clientid, month, yearCd, searchVal, filter,isTransactiondate,isYear);
		TotalInvoiceAmount gstr2Amounts = invoicesMappingDao.getGstr2aTotalInvoicesAmounts(clientid, month, yearCd, searchVal, filter,isTransactiondate,isYear);
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(prAmounts != null && gstr2Amounts != null) {
			totalInvAmount.setTotalTransactions(prAmounts.getTotalTransactions()+gstr2Amounts.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(prAmounts.getTotalTaxableAmount().add(gstr2Amounts.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(prAmounts.getTotalExemptedAmount().add(gstr2Amounts.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(prAmounts.getTotalTaxAmount().add(gstr2Amounts.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(prAmounts.getTotalAmount().add(gstr2Amounts.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(prAmounts.getTotalIGSTAmount().add(gstr2Amounts.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(prAmounts.getTotalCGSTAmount().add(gstr2Amounts.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(prAmounts.getTotalSGSTAmount().add(gstr2Amounts.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(prAmounts.getTotalCESSAmount().add(gstr2Amounts.getTotalCESSAmount()));
			return totalInvAmount;
		}else if(gstr2Amounts != null){
			return gstr2Amounts;
		}else {
			return prAmounts;
		}
	}
	
	@Override
	public Map<String, Object> getReconcileSummary(String clientid, int month, int year, boolean isMonthly,boolean isTransactiondate) {
		String yearCd = null;
		if(isMonthly) {
			yearCd = Utility.getYearCode(month, year);						
		}else {
			month = 0;
			yearCd = Utility.getYearCode(4, year);			
		}
		Map<String, Object> invMap = Maps.newHashMap();
		invMap.put("MATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("MATCHED_IN_OTHER_MONTHS", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MATCHED_IN_OTHER_MONTHS.getName(),isMonthly,isTransactiondate));
		invMap.put("ROUNDOFF_MATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.ROUNDOFF_MATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("PROBABLE_MATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.PROBABLE_MATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MISMATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("INVOICE_NO_MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_NO_MISMATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("TAX_MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.TAX_MISMATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("INVOICE_VALUE_MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_VALUE_MISMATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("GST_NO_MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.GST_NO_MISMATCHED.getName(),isMonthly,isTransactiondate));
		invMap.put("INVOICE_DATE_MISMATCHED", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_DATE_MISMATCHED.getName(),isMonthly,isTransactiondate));
		
		long prMannualMatchCounts = invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MANUAL_MATCHED.getName(),isMonthly,isTransactiondate);
		invMap.put("NOT_IN_PURCHASES", invoicesMappingDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.NOT_IN_GSTR2A.getName(),isMonthly,isTransactiondate));
		invMap.put("NOT_IN_GSTR2A", invoicesMappingDao.getGstr2aReconcileSummary(clientid, month, yearCd, Reconsile.NOT_IN_PURCHASES.getName()));
		long gstr2aMannualMatchCounts = invoicesMappingDao.getGstr2aReconcileSummary(clientid, month, yearCd, Reconsile.MANUAL_MATCHED.getName());
		invMap.put("MANUAL_MATCHED", prMannualMatchCounts + gstr2aMannualMatchCounts);
		return invMap;
	}

	public enum Reconsile {
		MATCHED("Matched"),
		MATCHED_IN_OTHER_MONTHS("Matched In Other Months"),
		ROUNDOFF_MATCHED("Round Off Matched"),
		PROBABLE_MATCHED("Probable Matched"),
		NOT_IN_GSTR2A("Not In GSTR 2A"),
		NOT_IN_PURCHASES("Not In Purchases"),
		MISMATCHED("Mismatched"),
		INVOICE_NO_MISMATCHED("Invoice No Mismatched"),
		TAX_MISMATCHED("Tax Mismatched"),
		INVOICE_VALUE_MISMATCHED("Invoice Value Mismatched"),
		GST_NO_MISMATCHED("GST No Mismatched"),
		INVOICE_DATE_MISMATCHED("Invoice Date Mismatched"),
		MANUAL_MATCHED("Manual Matched"), NOT_IN_GSTR2B("Not In GSTR2B");
		
		private String name;
		private Reconsile(String name){
			this.name = name;
		}
		
		public String getName(){
			return name;
		}
		
		public static Reconsile get(String name){
			for( Reconsile val : values()){
				if(val.getName().equals(name)){
					return val;
				}
			}
			return null;
		}
	}
	
	@Override
	@Transactional
	public ClientConfig getClientConfig(String clientId) {
		logger.debug(CLASSNAME + "updateClientConfig : Begin");
		return clientConfigRepository.findByClientId(clientId);
	}
	
	public String removeLeadingZeros(String str){
		if (str == null){
			return null;
		}
		char[] chars = str.toCharArray();
		int index = 0;
		for (; index < str.length();index++) {
			if (chars[index] != '0'){
				break;
			}
		}
		return (index == 0) ? str :str.substring(index);
	}
	
	 public List<Character> convertStringToCharList(String str){ 
	  
		 List<Character> chars = new ArrayList<>();  
	     for (char ch : str.toCharArray()) { 
	    	 chars.add(ch); 
	     }  
	     return chars; 
	  }
		
	@Override
	@Transactional
	@Async("reconcileTaskExecutor")
	public void updateReconsileStatus(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2List,List<String> rtarray,List<String> matchingstatus,
			final String invType, final String gstn, final String fp, final String monthlyOrYearly, boolean istransactionDate) {
		logger.debug(CLASSNAME + "updateMismatchStatus : Begin");
		Double allowedDiff = 0d;
		Double allowedDays = 0d;
		boolean ignoreHyphen = true;
		boolean ignoreSlash = true;
		boolean ignoreZeroOrO = true;
		boolean ignoreCapitalI = true;
		boolean ignorel = true;
		boolean ignoreInvoiceMatch = true;
		ClientConfig clientConfig = getClientConfig(clientId);
		if(isNotEmpty(clientConfig)) {
			if(isNotEmpty(clientConfig.getReconcileDiff())) {
				allowedDiff = clientConfig.getReconcileDiff();
			}
			if(isNotEmpty(clientConfig.getAllowedDays())) {
				allowedDays = clientConfig.getAllowedDays();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreHyphen())) {
				ignoreHyphen = clientConfig.isEnableIgnoreHyphen();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreSlash())) {
				ignoreSlash = clientConfig.isEnableIgnoreSlash();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreZero())) {
				ignoreZeroOrO = clientConfig.isEnableIgnoreZero();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreI())) {
				ignoreCapitalI = clientConfig.isEnableIgnoreI();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreL())) {
				ignorel = clientConfig.isEnableIgnoreL();
			}
			if(isNotEmpty(clientConfig.isEnableInvoiceMatch())) {
				ignoreInvoiceMatch = clientConfig.isEnableInvoiceMatch();
			}
		}else {
			ignoreHyphen = true;
			ignoreSlash = true;
			ignoreZeroOrO = true;
			ignoreCapitalI = true;
			ignorel = true;
			ignoreInvoiceMatch = true;
		}
		if(gstr2List == null) {
			List<String> reconsileMatchingIds = Lists.newArrayList();
			Criteria criteria = Criteria.where("clientid").in(clientId)
					.and("fp").in(rtarray).and("isAmendment").is(true).and("invtype").is(invType).and("matchingStatus").in(matchingstatus);
			Query query = new Query();
			query.addCriteria(criteria);
			int length = 100;
			for(int i=0;i< Integer.MAX_VALUE;i--) {
				if(i<0) {
					i=0;
				}
				Pageable pageable = new PageRequest(i, length);
				query.with(pageable);
				gstr2List = mongoTemplate.find(query, GSTR2.class);
				if(isEmpty(gstr2List)) {
					gstr2List = null;
					break;
				}else {
					ReconcileTemp reconciletemp = reconcileTempRepository.findByMonthlyoryearlyAndClientid(monthlyOrYearly,clientId);
					if(isNotEmpty(reconciletemp) && isNotEmpty(reconciletemp.getProcessedgstr2ainvoices())) {
					reconciletemp.setProcessedgstr2ainvoices(reconciletemp.getProcessedgstr2ainvoices()+gstr2List.size());
					}else {
						reconciletemp.setProcessedgstr2ainvoices((long)gstr2List.size());
					}
					reconcileTempRepository.save(reconciletemp);
				}
				List<PurchaseRegister> savePRList = Lists.newArrayList();
				List<PurchaseRegister> savePRProbableList = Lists.newArrayList();
				List<GSTR2> saveGSTR2List = Lists.newArrayList();
				List<String> matchingid = Lists.newArrayList();
			if (isNotEmpty(gstr2List)) {
				for (GSTR2 gstr2 : gstr2List) {
				if (isEmpty(gstr2.getMatchingStatus()) || (isNotEmpty(gstr2.getMatchingStatus()) && !gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) && !reconsileMatchingIds.contains(gstr2.getId().toString())) {
					gstr2.setMatchingStatus("Not In Purchases");
					gstr2 = gstr2Repository.save(gstr2);
					boolean mstatus = false;
					List<PurchaseRegister> savePRGList = Lists.newArrayList();
					List<PurchaseRegister> savePRRList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVDATEList = Lists.newArrayList();
					List<PurchaseRegister> savePRGGSTNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGTAXList = Lists.newArrayList();
					List<PurchaseRegister> savePRPList = Lists.newArrayList();
					for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if(!mstatus) {
						if (isEmpty(purchaseRegister.getMatchingStatus()) || (isNotEmpty(purchaseRegister.getMatchingStatus()) && !purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
							if (isNotEmpty(gstr2.getB2b())) {
								for (GSTRB2B gstrb2b : gstr2.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
											if(isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
										if (isNotEmpty(purchaseRegister.getB2b())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getCtin())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getInum())) {
											SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
											String dateBeforeString = gstrInvoiceDetails.getIdt();
											String dateAfterString = purchaseRegister.getB2b().get(0).getInv().get(0).getIdt();
											float daysBetween = 0f;
											double daysBetweenInvoiceDate = 0d;
											 try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
											 } catch (Exception e) {
											       e.printStackTrace();
											 }
											String purchaseregisterInvoiceNo = (purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim();
											String gstr2InvoiceNo = (gstrInvoiceDetails.getInum()).trim();
											 if(ignoreHyphen) {
												 if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
												 }
											 }
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												if (gstr2InvoiceNo.contains("/")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
													if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
												&& (gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())
												&& gstrInvoiceDetails.getPos().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getPos())) {
													if(isNotEmpty(gstrb2b.getCfs())) {
														purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
													}
													if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																	if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																		if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
															saveGSTR2List.add(gstr2);
															matchingid.add(gstr2.getId().toString());
															savePRRList.add((PurchaseRegister) purchaseRegister);
															mstatus = true;
														} else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePRList.add((PurchaseRegister) purchaseRegister);
															savePRGList.add((PurchaseRegister) purchaseRegister);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															saveGSTR2List.add(gstr2);
														}
											} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
														&& (gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())) {
													if (isNotEmpty(gstrb2b.getCfs())) {
														purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
													}
														if (daysBetweenInvoiceDate <= allowedDays) {
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																			if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																				if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				}
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																		matchingid.add(gstr2.getId().toString());
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		mstatus = true;
																	} else {
																		if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																			if(isNotEmpty(savePRGTAXList) && savePRGTAXList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																			}
																		}else {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	}
														} else {
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																		if(isNotEmpty(savePRGINVDATEList) && savePRGINVDATEList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																		}
																	}else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRGList.add((PurchaseRegister) purchaseRegister);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}
														}
													} else if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
															&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
															if (isNotEmpty(gstrb2b.getCfs())) {
																purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																if (ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					matchingid.add(gstr2.getId().toString());
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}else {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	} else {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				matchingid.add(gstr2.getId().toString());
																				savePRPList.add((PurchaseRegister) purchaseRegister);
																				savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			}
																		}	
																	}else{
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			}
																		}
																	}
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
															} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		matchingid.add(gstr2.getId().toString());
																		savePRPList.add((PurchaseRegister) purchaseRegister);
																		savePRProbableList.add((PurchaseRegister) purchaseRegister);
																	}
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
															} else {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																	}
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
															}
														}
													} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
															&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
														if (isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
														Double alldDiff = 0d;
														if (allowedDiff == 0d) {
															alldDiff = 1d;
														} else {
															alldDiff = allowedDiff;
														}
														if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																	savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																}
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
															saveGSTR2List.add(gstr2);
															}
														}
													}
												}
											}
										}
									}
								} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2.getCdn()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
											if (isNotEmpty(gstrInvoiceDetails.getNtNum()) && isNotEmpty(gstrInvoiceDetails.getNtDt())) {
												if (isNotEmpty(purchaseRegister.getCdn().get(0).getCtin())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
													String dateAfterString = myFormat.format(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt());
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
														Date dateBefore = myFormat.parse(dateBeforeString);
														Date dateAfter = myFormat.parse(dateAfterString);
														long difference = dateAfter.getTime() - dateBefore.getTime();
														daysBetween = (difference / (1000 * 60 * 60 * 24));
														daysBetweenInvoiceDate = Math.abs((double) daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim();
													String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
													if (ignoreHyphen) {
														if (purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
														}
														if (gstr2InvoiceNo.contains("-")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
														}
													}
													if (ignoreSlash) {
														if (purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
														}
														if (gstr2InvoiceNo.contains("/")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
														}
													}
													if (ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
														}
														if (gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
														}
													}
													if (ignoreCapitalI) {
														if (purchaseregisterInvoiceNo.contains("I")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
														}
														if (gstr2InvoiceNo.contains("I")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														}
													}
													if (ignorel) {
														if (purchaseregisterInvoiceNo.contains("l")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
														}
														if (gstr2InvoiceNo.contains("l")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														}
													}
													gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
													if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))
															&& (gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
														if(isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														List<Double> pTxValues = Lists.newArrayList();
														if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getItms())) {
															for (GSTRItems gstrItem : purchaseRegister.getCdn().get(0).getNt().get(0).getItms()) {
																pTxValues.add(gstrItem.getItem().getTxval());
															}
														}
													if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePRList.add((PurchaseRegister) purchaseRegister);
														saveGSTR2List.add(gstr2);
														matchingid.add(gstr2.getId().toString());
														savePRRList.add((PurchaseRegister) purchaseRegister);
														mstatus = true;
													} else {
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePRList.add((PurchaseRegister) purchaseRegister);
														savePRGList.add((PurchaseRegister) purchaseRegister);
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														saveGSTR2List.add(gstr2);
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& (gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														if (daysBetweenInvoiceDate <= allowedDays) {
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				mstatus = true;
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		} else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																	} else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																	matchingid.add(gstr2.getId().toString());
																	savePRRList.add((PurchaseRegister) purchaseRegister);
																	mstatus = true;
																} else {
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																		if(isNotEmpty(savePRGTAXList) && savePRGTAXList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																		}
																	}else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRGList.add((PurchaseRegister) purchaseRegister);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}
															} else {
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																			if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																				if(isNotEmpty(savePRGINVDATEList) && savePRGINVDATEList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																					savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																				}
																			}else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																			}
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2List.add(gstr2);
																}
															}
														} else if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin()).trim())&& gstrInvoiceDetails.getNtDt().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt())) {
															if(isNotEmpty(gstrcdn.getCfs())) {
																purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				matchingid.add(gstr2.getId().toString());
																				savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			}
																		}else {
																			
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				matchingid.add(gstr2.getId().toString());
																				savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			}	
																		}else{
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		matchingid.add(gstr2.getId().toString());
																		
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		savePRProbableList.add((PurchaseRegister) purchaseRegister);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}else {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}
															}
														} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
																&& gstrInvoiceDetails.getNtDt().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt())) {
															if(isNotEmpty(gstrcdn.getCfs())) {
																purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																	
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																	}
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
																}
															}
												}
											}
										}
									}
								}
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2.getImpGoods()) {
										if(isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
											if(isNotEmpty(purchaseRegister.getImpGoods()) && isNotEmpty(purchaseRegister.getImpGoods().get(0)) && isNotEmpty(purchaseRegister.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(purchaseRegister.getDateofinvoice());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												 try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												 } catch (Exception e) {
												       e.printStackTrace();
												 }
												String purchaseregisterInvoiceNo = (purchaseRegister.getImpGoods().get(0).getBoeNum().toString()).trim();
												String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
												 if(ignoreHyphen) {
													 if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													 }
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												 }
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													if (gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
													 }
													 if(gstr2InvoiceNo.contains("I")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
													 }
													 if(gstr2InvoiceNo.contains("l")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
													 }
												 }
												 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												 
												 if(isEmpty(gstrimpg.getStin())) {
													 gstrimpg.setStin(" ");
												 }
												 if(isEmpty(purchaseRegister) || isEmpty(purchaseRegister.getImpGoods()) || isEmpty(purchaseRegister.getImpGoods().get(0)) || isEmpty(purchaseRegister.getImpGoods().get(0).getStin())) {
													 purchaseRegister.getImpGoods().get(0).setStin(" ");
												 }
												 
												 if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
															&& ((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getImpGoods().get(0).getBoeNum().toString()).trim().toLowerCase())
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						reconsileMatchingIds.add(purchaseRegister.getId().toString());
																						mstatus = true;
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																		matchingid.add(gstr2.getId().toString());
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		mstatus = true;
																	} else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		savePRGList.add((PurchaseRegister) purchaseRegister);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2List.add(gstr2);
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							reconsileMatchingIds.add(purchaseRegister.getId().toString());
																							mstatus = true;
																						}else {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				saveGSTR2List.add(gstr2);
																				matchingid.add(gstr2.getId().toString());
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				mstatus = true;
																			} else {
																				if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																					if(isNotEmpty(savePRGTAXList) && savePRGTAXList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																					}
																				}else {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					savePRGList.add((PurchaseRegister) purchaseRegister);
																				}
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				saveGSTR2List.add(gstr2);
																			}
																} else {
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																		
																		if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(isNotEmpty(savePRGINVDATEList) && savePRGINVDATEList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																			}
																		}else {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																		}
																}
															} else if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																	Double alldDiff = 0d;
																	if (allowedDiff == 0d) {
																		alldDiff = 1d;
																	} else {
																		alldDiff = allowedDiff;
																	}
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						matchingid.add(gstr2.getId().toString());
																					}
																				}else {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																							purchaseRegister.setMatchingId(gstr2.getId().toString());
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																						}
																					}
																				}
																			} else {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																				}else {	
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					matchingid.add(gstr2.getId().toString());
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																				}
																			}else{
																				if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																				}else {
																					if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					}
																				}
																			}
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			matchingid.add(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRProbableList.add((PurchaseRegister) purchaseRegister);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			}
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	}
																}
															} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																Double alldDiff = 0d;
																if (allowedDiff == 0d) {
																	alldDiff = 1d;
																} else {
																	alldDiff = allowedDiff;
																}
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}
								if(isNotEmpty(savePRRList) && savePRRList.size() > 0) {
									purchaseRegisters.removeAll(savePRRList);
								}
								if(isNotEmpty(savePRGList) && savePRGList.size() > 1) {
									savePRGList.remove(0);
									removematchingid(savePRGList);
								}
							}
						}
					purchaseRepository.save(savePRList);
					gstr2Repository.save(saveGSTR2List);
					removegstr2id(matchingid,clientId);
					
					if(isNotEmpty(savePRProbableList) && savePRProbableList.size() > 0) {
						for(PurchaseRegister pr : savePRProbableList) {
							if(isNotEmpty(pr.getMatchingId()) && isNotEmpty(pr.getMatchingStatus()) && MasterGSTConstants.GST_STATUS_PROBABLEMATCHED.equalsIgnoreCase(pr.getMatchingStatus())) {
								GSTR2 gstr2 = gstr2Repository.findOne(pr.getMatchingId());
								if(isNotEmpty(gstr2)) {
									if(isNotEmpty(gstr2.getMatchingStatus()) && MasterGSTConstants.GST_STATUS_MISMATCHED.equalsIgnoreCase(gstr2.getMatchingStatus())) {
										gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
										gstr2Repository.save(gstr2);
									}
								}
							}
						}
					}
				}
			}
		}
		int month = Integer.parseInt(fp.substring(0, 2));
		int year = Integer.parseInt(fp.substring(2));
		Calendar cal = Calendar.getInstance();
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);
		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;

		Date ystDate = null;
		Date yendDate = null;
		if(month < 10) {
			cal.set(year-1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
			yendDate = new java.util.Date(cal.getTimeInMillis());
			}
		}else {
			cal.set(year - 1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
			yendDate = new java.util.Date(cal.getTimeInMillis());
		}
		}
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		Pageable pageableAll = new PageRequest(0, Integer.MAX_VALUE);
		Page<? extends InvoiceParent> gstr2aInvoices = gstr2Repository.findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIsNull(clientId,invTypes, true, ystDate, yendDate, pageableAll);
		Page<? extends InvoiceParent> gstr2aInvoicess = gstr2Repository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusAndIsAmendment(clientId,invTypes,ystDate, yendDate,"Not In Purchases",true,pageableAll);
		List<GSTR2> gstr2aInvoic = Lists.newArrayList();
		List<GSTR2> npur = (List<GSTR2>) gstr2aInvoices.getContent();
		List<GSTR2> gstr2aInvoice = (List<GSTR2>) gstr2aInvoicess.getContent();
		if(isNotEmpty(npur)) {
			gstr2aInvoic.addAll(npur);
		}
		if(isNotEmpty(gstr2aInvoice)) {
			gstr2aInvoic.addAll(gstr2aInvoice);
		}
		long notinpurchases = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoicescount(clientId, invType, ystDate, yendDate,istransactionDate);
		ReconcileTemp reconciletemppr = reconcileTempRepository.findByMonthlyoryearlyAndClientid(monthlyOrYearly,clientId);
		if(isNotEmpty(reconciletemppr) && isNotEmpty(reconciletemppr.getTotalpurchaseinvoices())) {
			reconciletemppr.setTotalpurchaseinvoices(reconciletemppr.getTotalpurchaseinvoices()+(Long)notinpurchases);
		}else {
			reconciletemppr.setTotalpurchaseinvoices((Long)notinpurchases);
		}
		reconcileTempRepository.save(reconciletemppr);
		Page<PurchaseRegister> notInGstr2aInvs = null;
		int length = 100;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			if("Yearly".equalsIgnoreCase(monthlyOrYearly)) {
				notInGstr2aInvs = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoices(clientId, invType, ystDate, yendDate, pageable,istransactionDate);
			}else {
				if(!istransactionDate) {
					notInGstr2aInvs = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoices(clientId, invType, ystDate, yendDate, pageable,istransactionDate);
				}else {
					notInGstr2aInvs = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoices(clientId, invType, ystDate, yendDate, pageable,istransactionDate);
				}
			}
			if(isEmpty(notInGstr2aInvs.getContent())) {
				notInGstr2aInvs = null;
				break;
			}else {
				ReconcileTemp reconciletemp = reconcileTempRepository.findByMonthlyoryearlyAndClientid(monthlyOrYearly,clientId);
				if(isNotEmpty(reconciletemp) && isNotEmpty(reconciletemp.getProcessedpurchaseinvoices())) {
				reconciletemp.setProcessedpurchaseinvoices(reconciletemp.getProcessedpurchaseinvoices()+notInGstr2aInvs.getContent().size());
				}else {
					reconciletemp.setProcessedpurchaseinvoices((long)notInGstr2aInvs.getContent().size());
				}
				reconcileTempRepository.save(reconciletemp);
			}
		List<PurchaseRegister> savePPRList = Lists.newArrayList();
		List<GSTR2> savePGSTR2List = Lists.newArrayList();
		List<String> statusMatchingIds = Lists.newArrayList();
		List<PurchaseRegister> savePPRProbableList = Lists.newArrayList();
		List<String> pmatchingid = Lists.newArrayList();
		if (isNotEmpty(gstr2aInvoic)) {
			for (PurchaseRegister purchaseRegister : notInGstr2aInvs.getContent()) {
				boolean mstatus = false;
				List<GSTR2> savePPRRList = Lists.newArrayList();
				List<GSTR2> savePRGINVNOList = Lists.newArrayList();
				List<GSTR2> savePRGINVDATEList = Lists.newArrayList();
				List<GSTR2> savePRGGSTNOList = Lists.newArrayList();
				List<GSTR2> savePRGTAXList = Lists.newArrayList();
				List<GSTR2> savePRPList = Lists.newArrayList();
				List<PurchaseRegister> savePPRGList = Lists.newArrayList();
				for (GSTR2 gstr2 : gstr2aInvoic) {
					if(!mstatus) {
					if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
							&& purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getB2b())) {
								for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
										if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
											if (isNotEmpty(gstr2.getB2b()) && isNotEmpty(gstr2.getB2b().get(0).getCtin())
												&& isNotEmpty(gstr2.getB2b().get(0).getInv())
												&& isNotEmpty(gstr2.getB2b().get(0).getInv().get(0).getInum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = gstrInvoiceDetails.getIdt();
												String dateAfterString = gstr2.getB2b().get(0).getInv().get(0).getIdt();
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = gstr2.getB2b().get(0).getInv().get(0).getInum().trim();
												String gstr2InvoiceNo = gstrInvoiceDetails.getInum().trim();
												if(ignoreHyphen) {
													if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													}
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
											if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
												if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														savePPRList.add(purchaseRegister);
														statusMatchingIds.add(gstr2.getId().toString());
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
														
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
														savePPRGList.add((PurchaseRegister) purchaseRegister);
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& (gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
													}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														savePPRList.add(purchaseRegister);
														statusMatchingIds.add(gstr2.getId().toString());
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
													} else {
														
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
														}else {
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());														
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
														}else {
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
															savePGSTR2List.add((GSTR2) gstr2);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getB2b().get(0).getInv().get(0).getInum().trim()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		pmatchingid.add(gstr2.getId().toString());
																		savePRPList.add((GSTR2) gstr2);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																	}
																}
															}else {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		savePRGINVNOList.add((GSTR2) gstr2);
																	}
																}
															}
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	pmatchingid.add(gstr2.getId().toString());
																	savePRPList.add((GSTR2) gstr2);
																	savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																}
															}
															savePGSTR2List.add((GSTR2) gstr2);
															savePPRList.add(purchaseRegister);
														}else {
															
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	savePRGINVNOList.add((GSTR2) gstr2);
																}
															}
															savePGSTR2List.add(gstr2);
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}
												}
											} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
													}else {
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
														savePGSTR2List.add(gstr2);
													}
													}
												}
											}
										}
									}
								}
							}
					} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getCdn())) {
								for (GSTRCreditDebitNotes gstrcdn : purchaseRegister.getCdn()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
										if (isNotEmpty(gstrInvoiceDetails.getNtNum())&& isNotEmpty(gstrInvoiceDetails.getNtDt())) {
											if (isNotEmpty(gstr2.getCdn()) && isNotEmpty(gstr2.getCdn().get(0)) && isNotEmpty(gstr2.getCdn().get(0).getCtin())
												&& isNotEmpty(gstr2.getCdn().get(0).getNt())
												&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getNtDt())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
												String dateAfterString = myFormat.format(gstr2.getCdn().get(0).getNt().get(0).getNtDt());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim();
												String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
												if(ignoreHyphen) {
												 if(purchaseregisterInvoiceNo.contains("-")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
												 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
												 if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
												if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												List<Double> pTxValues = Lists.newArrayList();
												if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getItms())) {
													for (GSTRItems gstrItem : gstr2.getCdn().get(0).getNt().get(0).getItms()) {
														pTxValues.add(gstrItem.getItem().getTxval());
													}
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add((PurchaseRegister)purchaseRegister);
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
												}
											} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& (gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))) {
												if (isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
															|| ((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add((PurchaseRegister) purchaseRegister);
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
													} else {
														
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
															if(isNotEmpty(savePRGTAXList) && savePRGTAXList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																savePRGTAXList.add((GSTR2) gstr2);
															}
														}else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
														savePGSTR2List.add(gstr2);
																												
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
															|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
															if(isNotEmpty(savePRGINVDATEList) && savePRGINVDATEList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																savePRGINVDATEList.add((GSTR2) gstr2);
															}
														}else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
																savePGSTR2List.add((GSTR2) gstr2);
																savePPRList.add(purchaseRegister);
														}
												}
											} else if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getNtDt().equals(gstr2.getCdn().get(0).getNt().get(0).getNtDt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
													&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
													|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
													if(ignoreInvoiceMatch) {
													List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
													List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()));
														if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	pmatchingid.add(gstr2.getId().toString());
																	savePRPList.add((GSTR2) gstr2);
																	savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																}
															}
														}else {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	savePRGINVNOList.add((GSTR2) gstr2);
																}
															}
															
														}
														savePGSTR2List.add((GSTR2) gstr2);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																pmatchingid.add(gstr2.getId().toString());
																savePRPList.add((GSTR2) gstr2);
																savePPRProbableList.add((PurchaseRegister) purchaseRegister);
															}
														}
														savePGSTR2List.add((GSTR2) gstr2);
														savePPRList.add(purchaseRegister);
													}else {
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																savePRGINVNOList.add((GSTR2) gstr2);
															}
														}
														savePGSTR2List.add(gstr2);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}
											} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
													&& gstrInvoiceDetails.getNtDt().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt())) {
												if(isNotEmpty(gstrcdn.getCfs())) {
													purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
												}
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
													&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
													|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
													&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
													|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
														
													if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
													}else {
														if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
															savePRGGSTNOList.add((GSTR2) gstr2);
														}
													}
													savePPRList.add((PurchaseRegister) purchaseRegister);
													savePGSTR2List.add(gstr2);
													}
												}
										}
									}
								}
							}
						}
					}else if (invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getImpGoods())) {
								for (GSTRImportDetails gstrimpg : purchaseRegister.getImpGoods()) {
										if (isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
											if (isNotEmpty(gstr2.getImpGoods()) && isNotEmpty(gstr2.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(gstr2.getImpGoods().get(0).getBoeDt());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = (gstr2.getImpGoods().get(0).getBoeNum().toString()).trim();
												String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
												if(ignoreHyphen) {
													if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													}
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
											if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											 if(isEmpty(gstrimpg.getStin())) {
												 gstrimpg.setStin(" ");
											 }
											 if(isEmpty(gstr2) || isEmpty(gstr2.getImpGoods()) || isEmpty(gstr2.getImpGoods().get(0)) || isEmpty(gstr2.getImpGoods().get(0).getStin())) {
												 gstr2.getImpGoods().get(0).setStin(" ");
											 }
											
											
											if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
												&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())
													&& (gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin()).trim())){
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
														pmatchingid.add(gstr2.getId().toString());
														savePPRRList.add((GSTR2) gstr2);
														mstatus = true;
													} else {
														if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
															if(isNotEmpty(savePRGTAXList) && savePRGTAXList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																savePRGTAXList.add((GSTR2) gstr2);
															}
														}else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
														savePGSTR2List.add(gstr2);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															
														if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
															if(isNotEmpty(savePRGINVDATEList) && savePRGINVDATEList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																savePRGINVDATEList.add((GSTR2) gstr2);
															}
														}else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRGList.add((PurchaseRegister) purchaseRegister);
														}
															savePGSTR2List.add((GSTR2) gstr2);
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
													&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			pmatchingid.add(gstr2.getId().toString());
																			savePRPList.add((GSTR2) gstr2);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																}else {

																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((GSTR2) gstr2);
																		}
																	}
																}
															}else {
																
																if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {	
																		if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			pmatchingid.add(gstr2.getId().toString());
																			savePRPList.add((GSTR2) gstr2);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																}else{
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((GSTR2)gstr2);
																		}
																	}
																}
															}
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																pmatchingid.add(gstr2.getId().toString());
																savePRPList.add((GSTR2) gstr2);
																savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																}
															}
															savePGSTR2List.add((GSTR2) gstr2);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													
													if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
													}else {
														if(isNotEmpty(savePRGINVNOList) && savePRGINVNOList.size() < 1) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
															savePRGGSTNOList.add((GSTR2) gstr2);
														}
														savePPRList.add((PurchaseRegister) purchaseRegister);
														savePGSTR2List.add(gstr2);
													}
													}
												}
											}
										}
									}
								}
							}
						}
					
					}
				if(isNotEmpty(savePPRRList) && savePPRRList.size() > 0) {
					gstr2aInvoic.removeAll(savePPRRList);
				}
				if(isNotEmpty(savePPRGList) && savePPRGList.size() > 1) {
					savePPRGList.remove(0);
					removematchingid(savePPRGList);
				}
				
				}
			}
			purchaseRepository.save(savePPRList);
			gstr2Repository.save(savePGSTR2List);
			removegstr2id(pmatchingid,clientId);
			if(isNotEmpty(savePPRProbableList) && savePPRProbableList.size() > 0) {
				for(PurchaseRegister pr : savePPRProbableList) {
					if(isNotEmpty(pr.getMatchingId()) && isNotEmpty(pr.getMatchingStatus()) && MasterGSTConstants.GST_STATUS_PROBABLEMATCHED.equalsIgnoreCase(pr.getMatchingStatus())) {
						GSTR2 gstr2 = gstr2Repository.findOne(pr.getMatchingId());
						if(isNotEmpty(gstr2)) {
							if(isNotEmpty(gstr2.getMatchingStatus()) && MasterGSTConstants.GST_STATUS_MISMATCHED.equalsIgnoreCase(gstr2.getMatchingStatus())) {
								gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
								gstr2Repository.save(gstr2);
							}
						}
					}
				}
			}
		}
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
	}
	
	@Override
	@Async("reconcileTaskExecutor")
	@Transactional
	public void updateReconsileStatusMatchingStatusIsNull(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2> gstr2List,
			String invType, String gstnnumber, String fp, String monthlyOrYearly) {
		logger.debug(CLASSNAME + "updateReconsileStatusMatchingStatusIsNull : Begin");
		logger.info("start invtype ::"+invType);
		Double allowedDiff = 0d;
		Double allowedDays = 0d;
		boolean ignoreHyphen = true;
		boolean ignoreSlash = true;
		boolean ignoreZeroOrO = true;
		boolean ignoreCapitalI = true;
		boolean ignorel = true;
		boolean ignoreInvoiceMatch = true;
		ClientConfig clientConfig = getClientConfig(clientId);
		if(isNotEmpty(clientConfig)) {
			if(isNotEmpty(clientConfig.getReconcileDiff())) {
				allowedDiff = clientConfig.getReconcileDiff();
			}
			if(isNotEmpty(clientConfig.getAllowedDays())) {
				allowedDays = clientConfig.getAllowedDays();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreHyphen())) {
				ignoreHyphen = clientConfig.isEnableIgnoreHyphen();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreSlash())) {
				ignoreSlash = clientConfig.isEnableIgnoreSlash();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreZero())) {
				ignoreZeroOrO = clientConfig.isEnableIgnoreZero();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreI())) {
				ignoreCapitalI = clientConfig.isEnableIgnoreI();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreL())) {
				ignorel = clientConfig.isEnableIgnoreL();
			}
			if(isNotEmpty(clientConfig.isEnableInvoiceMatch())) {
				ignoreInvoiceMatch = clientConfig.isEnableInvoiceMatch();
			}
		}else {
			ignoreHyphen = true;
			ignoreSlash = true;
			ignoreZeroOrO = true;
			ignoreCapitalI = true;
			ignorel = true;
			ignoreInvoiceMatch = true;
		}
		
		ReconcileTemp reconcileTemp = new ReconcileTemp(clientId, 0l, invType);
		boolean flag = false;
		if(gstr2List == null) {
			List<String> reconsileMatchingIds = Lists.newArrayList();
			String year = fp.substring(2);
			int yr = Integer.parseInt(year);
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			int presentYear = calendar.get(Calendar.YEAR);
			List<String> rtarray = Lists.newArrayList();
			for (int i = yr; i <= presentYear; i++) {
				for (int j = 4; j <= 12; j++) {
					String strMonth = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonth + (i));
				}
				for (int k = 1; k <= 3; k++) {
					String strMonth = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonth + (i + 1));
				}
			}
			
			int length = 100;
			
			for(int i=0;i< Integer.MAX_VALUE;i++) {
				Pageable pageable = new PageRequest(i, length);
				gstr2List = invoicesMappingDao.findByClientidAndFpInAndInvtypeAndIsAmendmentAndMactingStatusIsNull(clientId, rtarray, invType, true, pageable).getContent();
				
				if(isEmpty(gstr2List)) {
					gstr2List = null;
					break;
				}
				List<PurchaseRegister> savePRList = Lists.newArrayList();
				List<GSTR2> saveGSTR2List = Lists.newArrayList();
			if (isNotEmpty(gstr2List)) {
			for (GSTR2 gstr2 : gstr2List) {
				
				if (isEmpty(gstr2.getMatchingStatus()) || (isNotEmpty(gstr2.getMatchingStatus()) && !gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) && !reconsileMatchingIds.contains(gstr2.getId().toString())) {
					gstr2.setMatchingStatus("Not In Purchases");
					gstr2 = gstr2Repository.save(gstr2);
					boolean mstatus = false;
					for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if(mstatus) {
							break;
						}
						if(!mstatus) {
						if (isEmpty(purchaseRegister.getMatchingStatus()) || (isNotEmpty(purchaseRegister.getMatchingStatus()) && !purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
							if (isNotEmpty(gstr2.getB2b())) {
								for (GSTRB2B gstrb2b : gstr2.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
											if(isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
										if (isNotEmpty(purchaseRegister.getB2b())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getCtin())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv())
												&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getInum())) {
											SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
											String dateBeforeString = gstrInvoiceDetails.getIdt();
											String dateAfterString = purchaseRegister.getB2b().get(0).getInv().get(0).getIdt();
											float daysBetween = 0f;
											double daysBetweenInvoiceDate = 0d;
											 try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
											 } catch (Exception e) {
											       e.printStackTrace();
											 }
											String purchaseregisterInvoiceNo = (purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim();
											String gstr2InvoiceNo = (gstrInvoiceDetails.getInum()).trim();
											 if(ignoreHyphen) {
												 if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
												 }
											 }
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												if (gstr2InvoiceNo.contains("/")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
													if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
												&& (gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())
												&& gstrInvoiceDetails.getPos().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getPos())) {
													if(isNotEmpty(gstrb2b.getCfs())) {
														purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
													}
													if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
															
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																	if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																		if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			mstatus = true;
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
															saveGSTR2List.add(gstr2);
														} else {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
															
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePRList.add((PurchaseRegister) purchaseRegister);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															saveGSTR2List.add(gstr2);
														}
											} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
														&& (gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())) {
													if (isNotEmpty(gstrb2b.getCfs())) {
														purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
													}
														if (daysBetweenInvoiceDate <= allowedDays) {
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																			if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																				if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				}
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2List.add(gstr2);
																	}
														} else {
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	
																	//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																	
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}
														}
													} else if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
															&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
															if (isNotEmpty(gstrb2b.getCfs())) {
																purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																if (ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			}
																		}else if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	} else {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	}else if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	}
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
															} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2List.add(gstr2);
															} else {
																if (isEmpty(purchaseRegister.getMatchingStatus())|| purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2List.add(gstr2);
																}
															}
														}
													} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
															&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
														if (isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
														Double alldDiff = 0d;
														if (allowedDiff == 0d) {
															alldDiff = 1d;
														} else {
															alldDiff = allowedDiff;
														}
														if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	saveGSTR2List.add(gstr2);
																}
															}
														}
													}
												}
											}
										}
									}
								} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2.getCdn()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
											if (isNotEmpty(gstrInvoiceDetails.getNtNum()) && isNotEmpty(gstrInvoiceDetails.getNtDt())) {
												if (isNotEmpty(purchaseRegister.getCdn().get(0).getCtin())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
													String dateAfterString = myFormat.format(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt());
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
														Date dateBefore = myFormat.parse(dateBeforeString);
														Date dateAfter = myFormat.parse(dateAfterString);
														long difference = dateAfter.getTime() - dateBefore.getTime();
														daysBetween = (difference / (1000 * 60 * 60 * 24));
														daysBetweenInvoiceDate = Math.abs((double) daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim();
													String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
													if (ignoreHyphen) {
														if (purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
														}
														if (gstr2InvoiceNo.contains("-")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
														}
													}
													if (ignoreSlash) {
														if (purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
														}
														if (gstr2InvoiceNo.contains("/")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
														}
													}
													if (ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
														}
														if (gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
														}
													}
													if (ignoreCapitalI) {
														if (purchaseregisterInvoiceNo.contains("I")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
														}
														if (gstr2InvoiceNo.contains("I")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														}
													}
													if (ignorel) {
														if (purchaseregisterInvoiceNo.contains("l")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
														}
														if (gstr2InvoiceNo.contains("l")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														}
													}
													gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
													if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))
															&& (gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
														if(isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														List<Double> pTxValues = Lists.newArrayList();
														if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getItms())) {
															for (GSTRItems gstrItem : purchaseRegister.getCdn().get(0).getNt().get(0).getItms()) {
																pTxValues.add(gstrItem.getItem().getTxval());
															}
														}
													if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePRList.add((PurchaseRegister) purchaseRegister);
														saveGSTR2List.add(gstr2);
													} else {
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePRList.add((PurchaseRegister) purchaseRegister);
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														saveGSTR2List.add(gstr2);
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& (gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														if (daysBetweenInvoiceDate <= allowedDays) {
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																	if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				mstatus = true;
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		} else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																	} else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																} else {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	saveGSTR2List.add(gstr2);
																}
															} else {
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2List.add(gstr2);
																}
															}
														} else if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin()).trim())&& gstrInvoiceDetails.getNtDt().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt())) {
															if(isNotEmpty(gstrcdn.getCfs())) {
																purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			}
																		}else if (isEmpty(purchaseRegister.getMatchingStatus())|| purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}else if (isEmpty(purchaseRegister.getMatchingStatus())|| purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2List.add(gstr2);
																}else {
																	if (isEmpty(purchaseRegister.getMatchingStatus())|| purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2List.add(gstr2);
																	}
																}
															}
														}
												}
											}
										}
									}
								}
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								
								
								if (isNotEmpty(gstr2.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2.getImpGoods()) {
										if(isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
											if(isNotEmpty(purchaseRegister.getImpGoods()) && isNotEmpty(purchaseRegister.getImpGoods().get(0)) && isNotEmpty(purchaseRegister.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(purchaseRegister.getDateofinvoice());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												 try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												 } catch (Exception e) {
												       e.printStackTrace();
												 }
												String purchaseregisterInvoiceNo = (purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim();
												String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
												 if(ignoreHyphen) {
													 if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													 }
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												 }
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													if (gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
													 }
													 if(gstr2InvoiceNo.contains("I")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
													 }
													 if(gstr2InvoiceNo.contains("l")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
													 }
												 }
												 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												 
												 if(isEmpty(gstrimpg.getStin())) {
													 gstrimpg.setStin(" ");
												 }
												 if(isEmpty(purchaseRegister) || isEmpty(purchaseRegister.getImpGoods()) || isEmpty(purchaseRegister.getImpGoods().get(0)) || isEmpty(purchaseRegister.getImpGoods().get(0).getStin())) {
													 purchaseRegister.getImpGoods().get(0).setStin(" ");
												 }
												 
												 if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
															&& ((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getImpGoods().get(0).getBoeNum().toString()).trim().toLowerCase())
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						reconsileMatchingIds.add(purchaseRegister.getId().toString());
																						mstatus = true;
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2List.add(gstr2);
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							reconsileMatchingIds.add(purchaseRegister.getId().toString());
																							mstatus = true;
																						}else {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				saveGSTR2List.add(gstr2);
																			} else {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2List.add(gstr2);
																			}
																} else {
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2List.add(gstr2);
																		}
																}
															} else if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																	Double alldDiff = 0d;
																	if (allowedDiff == 0d) {
																		alldDiff = 1d;
																	} else {
																		alldDiff = allowedDiff;
																	}
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					}
																				}else if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				}
																			} else {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}else if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2List.add(gstr2);
																	} else {
																		if (isEmpty(purchaseRegister.getMatchingStatus())|| purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2List.add(gstr2);
																		}
																	}
																}
															} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																
																Double alldDiff = 0d;
																if (allowedDiff == 0d) {
																	alldDiff = 1d;
																} else {
																	alldDiff = allowedDiff;
																}
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																		if (isEmpty(purchaseRegister.getMatchingStatus()) || purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			saveGSTR2List.add(gstr2);
																		}
																	}
																}
															}
														}
													}
												}
											}
										}
									}
								}//mstatus brace;
							}
						}
					purchaseRepository.save(savePRList);
					gstr2Repository.save(saveGSTR2List);
					if(flag) {
						ReconcileTemp temp = reconcileTempRepository.findByClientidAndInvtype(clientId, invType);
						temp.setProcessedinvoices(temp.getProcessedinvoices()+saveGSTR2List.size());
						reconcileTempRepository.save(temp);
					}else {
						long processSize = reconcileTemp.getProcessedinvoices() + saveGSTR2List.size();
						reconcileTemp.setProcessedinvoices(processSize);
						reconcileTempRepository.save(reconcileTemp);
						flag = true;
					}
				}
			}
		}
		int month = Integer.parseInt(fp.substring(0, 2));
		int year = Integer.parseInt(fp.substring(2));
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);

		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;

		Date ystDate = null;
		Date yendDate = null;
		if(month < 10) {
			cal.set(year-1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
			yendDate = new java.util.Date(cal.getTimeInMillis());
			}
		}else {
			cal.set(year - 1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
			yendDate = new java.util.Date(cal.getTimeInMillis());
		}
		}
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		Pageable pageableAll = new PageRequest(0, Integer.MAX_VALUE);
		/*Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		Page<? extends InvoiceParent> notInGstr2Ainvoices = purchaseRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusIsNull(clientId, invTypes, stDate,endDate, pageable);
		Page<? extends InvoiceParent> notInGstr2Ainvoicess = purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusAndDateofinvoiceBetween(clientId, invTypes,"Not In GSTR 2A",stDate,endDate, pageable);
		*/
		
		
		
		List<PurchaseRegister> notingstr2a = Lists.newArrayList();
		//List<PurchaseRegister> ngstr2a = (List<PurchaseRegister>) notInGstr2Ainvoices.getContent();
		//List<PurchaseRegister> notingstr2aa = (List<PurchaseRegister>) notInGstr2Ainvoicess.getContent();
		//if(isNotEmpty(ngstr2a)) {
		//	notingstr2a.addAll(ngstr2a);
		//}
		//if(isNotEmpty(notingstr2aa)) {
		//	notingstr2a.addAll(notingstr2aa);
		//}
		Page<? extends InvoiceParent> gstr2aInvoices = gstr2Repository.findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIsNull(clientId,invTypes, true, ystDate, yendDate, pageableAll);
		Page<? extends InvoiceParent> gstr2aInvoicess = gstr2Repository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusAndIsAmendment(clientId,invTypes,ystDate, yendDate,"Not In Purchases",true,pageableAll);
		List<GSTR2> gstr2aInvoic = Lists.newArrayList();
		List<GSTR2> npur = (List<GSTR2>) gstr2aInvoices.getContent();
		List<GSTR2> gstr2aInvoice = (List<GSTR2>) gstr2aInvoicess.getContent();
		if(isNotEmpty(npur)) {
			gstr2aInvoic.addAll(npur);
		}
		if(isNotEmpty(gstr2aInvoice)) {
			gstr2aInvoic.addAll(gstr2aInvoice);
		}
		Page<PurchaseRegister> notInGstr2aInvs = null;
		
		int length = 100;
		List<String> statusMatchingIds = Lists.newArrayList();
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			notInGstr2aInvs = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoices(clientId, invType, ystDate, yendDate, pageable,false);				
			if(isEmpty(notInGstr2aInvs.getContent())) {
				notInGstr2aInvs = null;
				break;
			}
		
		List<PurchaseRegister> savePPRList = Lists.newArrayList();
		List<GSTR2> savePGSTR2List = Lists.newArrayList();
		if (isNotEmpty(notingstr2a)) {
			for (PurchaseRegister purchaseRegister : notInGstr2aInvs.getContent()) {
				for (GSTR2 gstr2 : gstr2aInvoic) {
					if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
							&& purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getB2b())) {
								for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
										if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
											if (isNotEmpty(gstr2.getB2b()) && isNotEmpty(gstr2.getB2b().get(0).getCtin())
												&& isNotEmpty(gstr2.getB2b().get(0).getInv())
												&& isNotEmpty(gstr2.getB2b().get(0).getInv().get(0).getInum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = gstrInvoiceDetails.getIdt();
												String dateAfterString = gstr2.getB2b().get(0).getInv().get(0).getIdt();
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = gstr2.getB2b().get(0).getInv().get(0).getInum().trim();
												String gstr2InvoiceNo = gstrInvoiceDetails.getInum().trim();
												if(ignoreHyphen) {
													if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													}
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
											if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
												if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& (gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
													}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2) gstr2);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getB2b().get(0).getInv().get(0).getInum().trim()));
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
															}
															savePGSTR2List.add((GSTR2) gstr2);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
														}
													}
												}
											}
										}
									}
								}
							}
					} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getCdn())) {
								for (GSTRCreditDebitNotes gstrcdn : purchaseRegister.getCdn()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
										if (isNotEmpty(gstrInvoiceDetails.getNtNum())&& isNotEmpty(gstrInvoiceDetails.getNtDt())) {
											if (isNotEmpty(gstr2.getCdn()) && isNotEmpty(gstr2.getCdn().get(0)) && isNotEmpty(gstr2.getCdn().get(0).getCtin())
												&& isNotEmpty(gstr2.getCdn().get(0).getNt())
												&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getNtDt())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
												String dateAfterString = myFormat.format(gstr2.getCdn().get(0).getNt().get(0).getNtDt());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim();
												String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
												if(ignoreHyphen) {
												 if(purchaseregisterInvoiceNo.contains("-")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
												 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
												 if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
												if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												List<Double> pTxValues = Lists.newArrayList();
												if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getItms())) {
													for (GSTRItems gstrItem : gstr2.getCdn().get(0).getNt().get(0).getItms()) {
														pTxValues.add(gstrItem.getItem().getTxval());
													}
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add((PurchaseRegister)purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
												}
											} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& (gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))) {
												if (isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
															|| ((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add((PurchaseRegister) purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister)purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
															|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}
												}
											} else if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getNtDt().equals(gstr2.getCdn().get(0).getNt().get(0).getNtDt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
													&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
													|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
													if(ignoreInvoiceMatch) {
													List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
													List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()));
														if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																
															}
														}else {
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
														}
														
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
														}
														savePGSTR2List.add((GSTR2) gstr2);
														savePPRList.add(purchaseRegister);
													}else {
														if (isEmpty(gstr2.getMatchingStatus())|| gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePPRList.add((PurchaseRegister)purchaseRegister);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}else if (invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getImpGoods())) {
								for (GSTRImportDetails gstrimpg : purchaseRegister.getImpGoods()) {
										if (isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
											if (isNotEmpty(gstr2.getImpGoods()) && isNotEmpty(gstr2.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(gstr2.getImpGoods().get(0).getBoeDt());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												try {
											       Date dateBefore = myFormat.parse(dateBeforeString);
											       Date dateAfter = myFormat.parse(dateAfterString);
											       long difference = dateAfter.getTime() - dateBefore.getTime();
											       daysBetween = (difference / (1000*60*60*24));
											       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												} catch (Exception e) {
													e.printStackTrace();
												}
												String purchaseregisterInvoiceNo = (gstr2.getImpGoods().get(0).getBoeNum().toString()).trim();
												String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
												if(ignoreHyphen) {
													if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													}
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												}
											 if(ignoreSlash) {
												 if(purchaseregisterInvoiceNo.contains("/")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
												 }
												 if(gstr2InvoiceNo.contains("-")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
												 }
											 }
											 if(ignoreZeroOrO) {
											if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
												 }
												 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
												 }
											 }
											 if(ignoreCapitalI) {
												 if(purchaseregisterInvoiceNo.contains("I")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
												 }
												 if(gstr2InvoiceNo.contains("I")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
												 }
											 }
											 if(ignorel) {
												 if(purchaseregisterInvoiceNo.contains("l")) {
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
												 }
												 if(gstr2InvoiceNo.contains("l")) {
													 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
												 }
											 }
											gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
											purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
											 if(isEmpty(gstrimpg.getStin())) {
												 gstrimpg.setStin(" ");
											 }
											 if(isEmpty(gstr2) || isEmpty(gstr2.getImpGoods()) || isEmpty(gstr2.getImpGoods().get(0)) || isEmpty(gstr2.getImpGoods().get(0).getStin())) {
												 gstr2.getImpGoods().get(0).setStin(" ");
											 }
											
											
											if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
												&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())
													&& (gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin()).trim())){
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
															|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
															|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2) gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														savePPRList.add(purchaseRegister);
													} else {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2) gstr2);
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
													&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()));
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																
															}
															savePGSTR2List.add((GSTR2) gstr2);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2);
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
													|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
													|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2);
														purchaseRegister.setMatchingId(gstr2.getId().toString());
														
														purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
														}
													}
												}
											}
										}
									}
								}
							}
					}
				}
			}
			purchaseRepository.save(savePPRList);
			gstr2Repository.save(savePGSTR2List);
			if(flag) {
				ReconcileTemp temp = reconcileTempRepository.findByClientidAndInvtype(clientId, invType);
				temp.setProcessedinvoices(temp.getProcessedinvoices() + savePGSTR2List.size());
				reconcileTempRepository.save(temp);
			}else {
				long processSize = reconcileTemp.getProcessedinvoices() + savePGSTR2List.size();
				reconcileTemp.setProcessedinvoices(processSize);
				reconcileTempRepository.save(reconcileTemp);
				flag = true;
			}
		}
		/*List<ReconcileTemp> temp = reconcileTempRepository.findByClientid(clientId);
		if(isNotEmpty(temp)) {
			reconcileTempRepository.delete(temp);
			
		}*/
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
	}

	@Override
	@Transactional
	//@Async("reconcileTaskExecutor")
	public void removegstr2id(List<String> gstr2id,String clientid) {
		if(isNotEmpty(gstr2id)) {
			List<String> status = Lists.newArrayList();
			status.add(MasterGSTConstants.GST_STATUS_MATCHED);
			status.add(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
			status.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			status.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
			status.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
			List<PurchaseRegister> pr = purchaseRepository.findByClientidAndMatchingIdInAndMatchingStatus(clientid, gstr2id,MasterGSTConstants.GST_STATUS_MISMATCHED);
			List<PurchaseRegister> savePPRList = Lists.newArrayList();
			for(PurchaseRegister purchaseregister: pr) {
				purchaseregister.setMatchingId(null);
				purchaseregister.setMatchingStatus(null);
				savePPRList.add(purchaseregister);
			}
			purchaseRepository.save(savePPRList);
		}
	}

	@Override
	@Transactional
	//@Async("reconcileTaskExecutor")
	public void removematchingid(List<PurchaseRegister> purchaseRegisters) {
		List<PurchaseRegister> savePPRList = Lists.newArrayList();
		for(PurchaseRegister purchaseregister: purchaseRegisters) {
			purchaseregister.setMatchingId(null);
			purchaseregister.setMatchingStatus(null);
			savePPRList.add(purchaseregister);
		}
		purchaseRepository.save(savePPRList);
		
	}
	
	
	@Transactional(readOnly=true)
	public Page<PurchaseRegister> getPageablePurchaseRegisters(final String invType, final String clientid, final int month,
			final int year, boolean isYear, Pageable pageable) {
		Date stDate = null;
		Date endDate = null;
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);

		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;
		Calendar cal = Calendar.getInstance();
		if (isYear) {
			if (month < 10) {
				cal.set(year - 1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}

			} else {
				cal.set(year - 1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		} else {
			cal.set(year, 3, 1, 0, 0, 0);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year + 1, 3, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}
		return purchaseRepository.findByClientidAndInvtypeAndDateofinvoiceBetweenAndMatchingStatusIsNull(clientid, invType, stDate, endDate, pageable);
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<PurchaseRegister> getPurchaseRegisters(final String invType, final String clientid, final int month,
			final int year, boolean isYear) {
		Date stDate = null;
		Date endDate = null;
		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);

		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;
		Calendar cal = Calendar.getInstance();
		if (isYear) {
		if(month < 10) {
			cal.set(year-1, 3, 1, 0, 0, 0);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}

			} else {
				cal.set(year - 1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if (presentYear != year) {
					cal.set(year, 12, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				} else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		}else {
			cal.set(year, 3, 1, 0, 0, 0);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year + 1, 3, 0, 23, 59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}
		
		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add("Not In GSTR2A");
		matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH);
		matchingstatus.add("");
		matchingstatus.add(null);
		return purchaseRepository.findByClientidAndInvtypeAndMatchingStatusInAndDateofinvoiceBetween(clientid, invType,matchingstatus, stDate, endDate);
	}
	
	//---------------------------------Gstra Reconcile performance changes----------------------------------------------
	
	@Transactional
	@Override
	@Async("reconcileTaskExecutor")
	public void updateMismatchStatus(String clientid, final String invType, final String gstn, final String fp, int month, int year, final String monthlyOrYearly,final boolean isBilledDate) {
		final String method ="updateMismatchStatus ::";
		logger.debug(CLASSNAME + "updateMismatchStatus : Begin");
		ClientConfig clientConfig = getClientConfig(clientid);
		int defaultSize = 1000;
		//int prdefaultSize = 10000;
		int prdefaultSize = 2000;
		ReconcileTemp recon  = reconcileTempRepository.findByClientid(clientid);
		Pageable invPageable = new PageRequest(0, 10000, Sort.Direction.ASC, "invoiceno");
		
		List<String> reconlist = Lists.newArrayList();
		reconlist.add(MasterGSTConstants.GST_STATUS_MATCHED);
		reconlist.add(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
		reconlist.add("Matched In Other Months");
		reconlist.add("Round Off Matched");
		reconlist.add("Round Off Matched In Other Months");
		reconlist.add("Probable Matched");
		reconlist.add("Mismatched");
		reconlist.add("Tax Mismatched");
		reconlist.add("Invoice Value Mismatched");
		reconlist.add("GST No Mismatched");
		reconlist.add("Invoice No Mismatched");
		reconlist.add("Invoice Date Mismatched");
		reconlist.add("Manual Matched");
		reconlist.add(MasterGSTConstants.GST_STATUS_NOTINPURCHASES);
		Page<GSTR2> gstr2List = null;
		List<String> fps = Lists.newArrayList();
		List<String> rtarray = Lists.newArrayList();
		if ("monthly".equalsIgnoreCase(monthlyOrYearly)) {
			fps.add(fp);
		}else {
			String fpYear = fp.substring(2);
			int yr = Integer.parseInt(fpYear);
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			int presentYear = calendar.get(Calendar.YEAR);
			//for (int m = yr; m <= presentYear; m++) {
				for (int j = 4; j <= 12; j++) {
					String strMonth = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonth + (yr-1));
				}
				for (int k = 1; k <= 3; k++) {
					String strMonth = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonth + (yr));
				}
			//}
		}
		boolean hasMore = true;
		while(hasMore) {
			List<String> gstr2InvoiceNoList = Lists.newArrayList();
			List<GSTR2> gstr2Content = Lists.newArrayList();
			Pageable pageable = new PageRequest(0, defaultSize, Sort.Direction.ASC, "invoiceno");
			Page<PurchaseRegister> purchaseRegisters = null;
			List<GSTR2> matched2A = Lists.newArrayList();
			List<GSTR2> invoceNoMisMatched2A = Lists.newArrayList();
			List<GSTR2> gstNoMisMatched2A = Lists.newArrayList();
			
			if ("monthly".equalsIgnoreCase(monthlyOrYearly)) {
				Criteria criteria = Criteria.where("clientid").in(clientid)
						.and("fp").is(fp).and("isAmendment").is(true).and("invtype").is(invType).and("matchingStatus").nin(reconlist);
				Query query = new Query();
				query.addCriteria(criteria);
				query.fields().include("invoiceno");
				query.with(invPageable);
				List<GSTR2> gstr2InvoiceList = mongoTemplate.find(query, GSTR2.class);
				if(isEmpty(gstr2InvoiceList) || gstr2InvoiceList.size() == 0) {
					hasMore = false;
					break;
				}
				for(GSTR2 gstr2 : gstr2InvoiceList) {
					gstr2InvoiceNoList.add(gstr2.getInvoiceno());
				}
				if(gstr2InvoiceNoList.size() < 10000) {
					hasMore = false;
				}
				
				//gstr2List = gstr2Repository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,fps, gstr2InvoiceNoList, pageable);
				gstr2List = gstr2Dao.findInvoicesForReconcile(clientid, invType, fps, gstr2InvoiceNoList, pageable);
				gstr2Content.addAll(gstr2List.getContent());
				while(isNotEmpty(gstr2List) && gstr2List.hasNext()) {
					gstr2List = gstr2Dao.findInvoicesForReconcile(clientid, invType, fps, gstr2InvoiceNoList, gstr2List.nextPageable());
					//gstr2List = gstr2Repository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,fps, gstr2InvoiceNoList, gstr2List.nextPageable());
					gstr2Content.addAll(gstr2List.getContent());
				}
				List<String> matchedids = Lists.newArrayList();
				List<PurchaseRegister> matchedPR = Lists.newArrayList();
				List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> purchaseRegisterContent = Lists.newArrayList();
				purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2InvoiceNoList, pageable);
				if(isEmpty(purchaseRegisters) || purchaseRegisters.getContent().size() == 0) {
					break;
				}
				purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				while(isNotEmpty(purchaseRegisters) && purchaseRegisters.hasNext()) {
					purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2InvoiceNoList, purchaseRegisters.nextPageable());
					purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				}
				updateMismatchedStatusForPageable(purchaseRegisterContent, gstr2Content, invType, clientConfig, clientid,matchedids,matchedPR,matched2A,invoceNoMisMatchedPR,invoceNoMisMatched2A,gstNoMisMatchedPR,gstNoMisMatched2A);
				if(isNotEmpty(gstr2Content)) {
					if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ainvoices())) {
						recon.setProcessedgstr2ainvoices(recon.getProcessedgstr2ainvoices()+gstr2Content.size());
					}else {
						recon.setProcessedgstr2ainvoices(new Long(gstr2Content.size()));
					}
					if(invType.equals(MasterGSTConstants.B2B)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ab2binvoices())) {
							recon.setProcessedgstr2ab2binvoices(recon.getProcessedgstr2ab2binvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2ab2binvoices(new Long(gstr2Content.size()));
						}
					}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2acreditinvoices())) {
							recon.setProcessedgstr2acreditinvoices(recon.getProcessedgstr2acreditinvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2acreditinvoices(new Long(gstr2Content.size()));
						}
					}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2aimpginvoices())) {
							recon.setProcessedgstr2aimpginvoices(recon.getProcessedgstr2aimpginvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2aimpginvoices(new Long(gstr2Content.size()));
						}
					}
				}
				recon = reconcileTempRepository.save(recon);
				matchedPR.addAll(invoceNoMisMatchedPR);
				matchedPR.addAll(gstNoMisMatchedPR);
				saveBulkPR(matchedPR);
				matched2A.addAll(invoceNoMisMatched2A);
				matched2A.addAll(gstNoMisMatched2A);
				saveBulkGSTR2(matched2A);
				gstr2Content.removeAll(matched2A);
				if(isNotEmpty(gstr2Content)) {
					List<GSTR2> gstr2NotMatchedList = Lists.newArrayList();
					for (GSTR2 gstr2 : gstr2Content) {
							gstr2.setMatchingStatus("Not In Purchases");
							gstr2NotMatchedList.add(gstr2);
					}
					saveBulkGSTR2(gstr2NotMatchedList);
				}
			}else {
				//yearly
				Criteria criteria = Criteria.where("clientid").in(clientid).and("fp").in(rtarray).and("isAmendment").is(true).and("invtype").is(invType).and("matchingStatus").nin(reconlist);
				Query query = new Query();
				query.addCriteria(criteria);
				query.fields().include("invoiceno");
				query.with(invPageable);
				List<GSTR2> gstr2InvoiceList = mongoTemplate.find(query, GSTR2.class);
				if(isEmpty(gstr2InvoiceList) || gstr2InvoiceList.size() == 0) {
					hasMore = false;
					break;
				}
				for(GSTR2 gstr2 : gstr2InvoiceList) {
					gstr2InvoiceNoList.add(gstr2.getInvoiceno());
				}
				if(gstr2InvoiceNoList.size() < 10000) {
					hasMore = false;
				}
				
				//gstr2List = gstr2Repository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,rtarray, gstr2InvoiceNoList, pageable);
				gstr2List = gstr2Dao.findInvoicesForReconcile(clientid, invType, rtarray, gstr2InvoiceNoList, pageable);
				gstr2Content.addAll(gstr2List.getContent());
				while(isNotEmpty(gstr2List) && gstr2List.hasNext()) {
					gstr2List = gstr2Dao.findInvoicesForReconcile(clientid, invType, rtarray, gstr2InvoiceNoList, gstr2List.nextPageable());
					//gstr2List = gstr2Repository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,rtarray, gstr2InvoiceNoList, gstr2List.nextPageable());
					gstr2Content.addAll(gstr2List.getContent());
				}
				List<String> matchedids = Lists.newArrayList();
				List<PurchaseRegister> matchedPR = Lists.newArrayList();
				List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> purchaseRegisterContent = Lists.newArrayList();
				purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2InvoiceNoList, pageable);
				if(isEmpty(purchaseRegisters) || purchaseRegisters.getContent().size() == 0) {
					break;
				}
				purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				while(isNotEmpty(purchaseRegisters) && purchaseRegisters.hasNext()) {
					purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2InvoiceNoList, purchaseRegisters.nextPageable());
					purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				}
				updateMismatchedStatusForPageable(purchaseRegisterContent, gstr2Content, invType, clientConfig, clientid,matchedids,matchedPR,matched2A,invoceNoMisMatchedPR,invoceNoMisMatched2A,gstNoMisMatchedPR,gstNoMisMatched2A);
				if(isNotEmpty(gstr2Content)) {
					if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ainvoices())) {
						recon.setProcessedgstr2ainvoices(recon.getProcessedgstr2ainvoices()+gstr2Content.size());
					}else {
						recon.setProcessedgstr2ainvoices(new Long(gstr2Content.size()));
					}
					if(invType.equals(MasterGSTConstants.B2B)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ab2binvoices())) {
							recon.setProcessedgstr2ab2binvoices(recon.getProcessedgstr2ab2binvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2ab2binvoices(new Long(gstr2Content.size()));
						}
					}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2acreditinvoices())) {
							recon.setProcessedgstr2acreditinvoices(recon.getProcessedgstr2acreditinvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2acreditinvoices(new Long(gstr2Content.size()));
						}
					}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2aimpginvoices())) {
							recon.setProcessedgstr2aimpginvoices(recon.getProcessedgstr2aimpginvoices()+gstr2Content.size());
						}else {
							recon.setProcessedgstr2aimpginvoices(new Long(gstr2Content.size()));
						}
					}
				}
				recon = reconcileTempRepository.save(recon);
				matchedPR.addAll(invoceNoMisMatchedPR);
				matchedPR.addAll(gstNoMisMatchedPR);
				saveBulkPR(matchedPR);
				matched2A.addAll(invoceNoMisMatched2A);
				matched2A.addAll(gstNoMisMatched2A);
				saveBulkGSTR2(matched2A);
				gstr2Content.removeAll(matched2A);
				if(isNotEmpty(gstr2Content)) {
					List<GSTR2> gstr2NotMatchedList = Lists.newArrayList();
					for (GSTR2 gstr2 : gstr2Content) {
							gstr2.setMatchingStatus("Not In Purchases");
							gstr2NotMatchedList.add(gstr2);
					}
					saveBulkGSTR2(gstr2NotMatchedList);
				}
				
			}
		}
		
		reconcileMissingInvoices(clientid, invType, fp, clientConfig, isBilledDate,recon,monthlyOrYearly);
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
	}
	
	
	private void reconcileMissingInvoices(String clientId, final String invType, final String fp, ClientConfig clientConfig,boolean billdate,ReconcileTemp recon,final String monthlyOrYearly) {
	//	logger.info(CLASSNAME + " reconcileMissingInvoices method calling...");
		int month = Integer.parseInt(fp.substring(0, 2));
		int year = Integer.parseInt(fp.substring(2));
		Calendar cal = Calendar.getInstance();

		Date presentDate = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(presentDate);

		int presentYear = calendar.get(Calendar.YEAR);
		int presentMonth = calendar.get(Calendar.MONTH) + 1;

		Date ystDate = null;
		Date yendDate = null;
		if (month < 10) {
			cal.set(year - 1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			}
		} else {
			cal.set(year - 1, 3, 1, 0, 0, 0);
			ystDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			if (presentYear != year) {
				cal.set(year, 9, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, presentMonth, 0, 23, 59, 59);
				yendDate = new java.util.Date(cal.getTimeInMillis());
			}
		}
		long notinpurchases = 0l;
		String yrcd = Utility.getYearCode(month, year);
		String strMonth = month+""; //month < 10 ? "0" + month : month + "";
		
		if("monthly".equalsIgnoreCase(monthlyOrYearly)) {
			
			notinpurchases = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoicesscount(clientId, invType, strMonth, yrcd,billdate);
		}else {
			notinpurchases = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2aInvoicescount(clientId, invType, ystDate, yendDate,billdate);
		}
		if(isNotEmpty(recon) && isNotEmpty(recon.getTotalpurchaseinvoices())) {
			recon.setTotalpurchaseinvoices(recon.getTotalpurchaseinvoices()+notinpurchases);
		}else {
			recon.setTotalpurchaseinvoices(notinpurchases);
		}
		recon = reconcileTempRepository.save(recon);
		reconcileMissingPageable(clientId, invType, ystDate, yendDate, clientConfig, billdate, monthlyOrYearly, strMonth, yrcd);	
	}
	
	private void reconcileMissingPageable(String clientid, final String invType, Date ystDate, Date yendDate,ClientConfig clientConfig ,boolean billdate,final String monthlyOrYearly, String mthcd, String yrcd) {
		final String method = "reconcileMissingPageable ::";
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		List<String> statusIsNull = Lists.newArrayList("", null,MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		List<ObjectId> ids = Lists.newArrayList();
		int defaultSize = 5000;
		Page<? extends InvoiceParent> notInGstr2Ainvoices = null;
		Calendar calendar = Calendar.getInstance();
		boolean hasMore = true;
		while(hasMore) {
			Pageable pageable = new PageRequest(0, defaultSize, Sort.Direction.ASC, "dateofinvoice");
			List<PurchaseRegister> notInGstr2aList = Lists.newArrayList();
			List<PurchaseRegister> matchedPR = Lists.newArrayList();
			List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
			List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
			if("monthly".equalsIgnoreCase(monthlyOrYearly)) {
				if(billdate) {
					//notInGstr2Ainvoices = purchaseRepository.findByClientidAndInvtypeInAndMthCdAndYrCdAndMatchingStatusInAndIdNotIn(clientid, invTypes, mthcd, yrcd, statusIsNull,ids, pageable);
					notInGstr2Ainvoices = purchageRegisterDao.findByClientidAndInvtypeInAndMthCdAndYrCdAndMatchingStatusInAndIdNotIn(clientid, invTypes, mthcd, yrcd, statusIsNull,ids, pageable);
				} else {
					notInGstr2Ainvoices = purchageRegisterDao.findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndMatchingStatusInAndIdNotIn(clientid, invTypes, mthcd, yrcd, statusIsNull,ids, pageable);
					//notInGstr2Ainvoices = purchaseRepository.findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndMatchingStatusInAndIdNotIn(clientid, invTypes, mthcd, yrcd, statusIsNull,ids, pageable);
				}
			}else {
				notInGstr2Ainvoices = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusInAndIdNotIn(clientid, invTypes, ystDate, yendDate, statusIsNull,ids, pageable);
				//notInGstr2Ainvoices = purchaseRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndMatchingStatusInAndIdNotIn(clientid, invTypes, ystDate, yendDate, statusIsNull,ids, pageable);
			}
			if(isEmpty(notInGstr2Ainvoices) || isEmpty(notInGstr2Ainvoices.getContent()) || notInGstr2Ainvoices.getContent().size() == 0) {
				hasMore = false;
				break;
			}
			if(notInGstr2Ainvoices.getContent().size() < defaultSize) {
				hasMore = false;
			}
			notInGstr2aList.addAll((Collection<? extends PurchaseRegister>) notInGstr2Ainvoices.getContent());
			List<String> matchedids = Lists.newArrayList();
			List<GSTR2> matched2A = Lists.newArrayList();
			List<GSTR2> invoceNoMisMatched2A = Lists.newArrayList();
			List<GSTR2> gstNoMisMatched2A = Lists.newArrayList();
			List<String> gstr2status = Arrays.asList("", null, "Not In Purchases");
			Date pstdate = null;
			Date penddate = null;
				if(isNotEmpty(notInGstr2aList)  && isNotEmpty(notInGstr2aList.get(0)) && isNotEmpty(notInGstr2aList.get(0).getDateofinvoice())) {
					calendar = Calendar.getInstance();
					Date stdate = notInGstr2aList.get(0).getDateofinvoice();
					calendar.set(stdate.getYear()+1900, stdate.getMonth(), stdate.getDate()-1, 0, 0, 0);
					pstdate = new java.util.Date(calendar.getTimeInMillis());
				}
				if(isNotEmpty(notInGstr2aList)  && isNotEmpty(notInGstr2aList.get(notInGstr2aList.size()-1)) && isNotEmpty(notInGstr2aList.get(notInGstr2aList.size()-1).getDateofinvoice())) {
					calendar = Calendar.getInstance();
					Date enddate = notInGstr2aList.get(notInGstr2aList.size()-1).getDateofinvoice();
					calendar.set(enddate.getYear()+1900, enddate.getMonth(), enddate.getDate(), 23, 59, 59);
					penddate = new java.util.Date(calendar.getTimeInMillis());
				}
			List<GSTR2> gstr2aInvoiceList = Lists.newArrayList();
			//Page<? extends InvoiceParent> gstr2aInvoices = gstr2Repository.findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIn(clientid,invTypes, true, pstdate, penddate, gstr2status, pageable);
			Page<? extends InvoiceParent> gstr2aInvoices = gstr2Dao.findInvoicesForReconcile(clientid, invTypes, pstdate, penddate, gstr2status, pageable);
			if(isEmpty(gstr2aInvoices) || gstr2aInvoices.getContent().size() == 0) {
				break;
			}
			gstr2aInvoiceList.addAll((List<GSTR2>) gstr2aInvoices.getContent());
			while(isNotEmpty(gstr2aInvoices) && gstr2aInvoices.hasNext()) {
				//gstr2aInvoices = gstr2Repository.findByClientidAndInvtypeInAndIsAmendmentAndDateofinvoiceBetweenAndMatchingStatusIn(clientid,invTypes, true, pstdate, penddate, gstr2status, gstr2aInvoices.nextPageable());
				gstr2aInvoices = gstr2Dao.findInvoicesForReconcile(clientid, invTypes, pstdate, penddate, gstr2status, gstr2aInvoices.nextPageable());
				gstr2aInvoiceList.addAll((List<GSTR2>) gstr2aInvoices.getContent());
			}
			reconcileRemainingRecords(clientid, invType, clientConfig, notInGstr2aList, gstr2aInvoiceList,matchedids,matchedPR,matched2A,invoceNoMisMatchedPR,invoceNoMisMatched2A,gstNoMisMatchedPR,gstNoMisMatched2A);
			ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
			if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseinvoices())) {
				recon.setProcessedpurchaseinvoices(recon.getProcessedpurchaseinvoices()+notInGstr2aList.size());
			}else {
				recon.setProcessedpurchaseinvoices(new Long(notInGstr2aList.size()));
			}
			if(invType.equals(MasterGSTConstants.B2B)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseb2binvoices())) {
					recon.setProcessedpurchaseb2binvoices(recon.getProcessedpurchaseb2binvoices()+notInGstr2aList.size());
				}else {
					recon.setProcessedpurchaseb2binvoices(new Long(notInGstr2aList.size()));
				}
			}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchasecreditinvoices())) {
					recon.setProcessedpurchasecreditinvoices(recon.getProcessedpurchasecreditinvoices()+notInGstr2aList.size());
				}else {
					recon.setProcessedpurchasecreditinvoices(new Long(notInGstr2aList.size()));
				}
			}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseimpginvoices())) {
					recon.setProcessedpurchaseimpginvoices(recon.getProcessedpurchaseimpginvoices()+notInGstr2aList.size());
				}else {
					recon.setProcessedpurchaseimpginvoices(new Long(notInGstr2aList.size()));
				}
			}
			recon = reconcileTempRepository.save(recon);
			matchedPR.addAll(invoceNoMisMatchedPR);
			matchedPR.addAll(gstNoMisMatchedPR);
			saveBulkPR(matchedPR);
			matched2A.addAll(invoceNoMisMatched2A);
			matched2A.addAll(gstNoMisMatched2A);
			saveBulkGSTR2(matched2A);
			notInGstr2aList.removeAll(matchedPR);
			if(isNotEmpty(notInGstr2aList)) {
				List<PurchaseRegister> gstr2NotMatchedList = Lists.newArrayList();
				for (PurchaseRegister gstr2 : notInGstr2aList) {
						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
						gstr2NotMatchedList.add(gstr2);
						ids.add(gstr2.getId());
				}
				if(isNotEmpty(gstr2NotMatchedList)) {
					saveBulkPR(gstr2NotMatchedList);
				}
			}
		}
	}

	private void reconcileRemainingRecords(String clientId, final String invType, ClientConfig clientConfig, List<PurchaseRegister> notingstr2a, List<GSTR2> gstr2aInvoic,List<String> matchedids,List<PurchaseRegister> matchedPR, List<GSTR2> matched2A,List<PurchaseRegister> invoiceNoMisMatchedPR, List<GSTR2> invoiceNoMisMatched2A,List<PurchaseRegister> gstNoMisMatchedPR, List<GSTR2> gstNoMisMatched2A) {
		final String method = "reconcileRemainingRecords";
		logger.debug(CLASSNAME + method + "GSTR2 Size="+notingstr2a.size());
		logger.debug(CLASSNAME + method + "GSTR2 Size="+gstr2aInvoic.size());
		Double allowedDiff = 0d;
		Double allowedDays = 0d;
		boolean ignoreHyphen = true;
		boolean ignoreSlash = true;
		boolean ignoreZeroOrO = true;
		boolean ignoreCapitalI = true;
		boolean ignorel = true;
		boolean ignoreInvoiceMatch = true;
		if(isNotEmpty(clientConfig)) {
			if(isNotEmpty(clientConfig.getReconcileDiff())) {
				allowedDiff = clientConfig.getReconcileDiff();
			}
			if(isNotEmpty(clientConfig.getAllowedDays())) {
				allowedDays = clientConfig.getAllowedDays();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreHyphen())) {
				ignoreHyphen = clientConfig.isEnableIgnoreHyphen();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreSlash())) {
				ignoreSlash = clientConfig.isEnableIgnoreSlash();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreZero())) {
				ignoreZeroOrO = clientConfig.isEnableIgnoreZero();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreI())) {
				ignoreCapitalI = clientConfig.isEnableIgnoreI();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreL())) {
				ignorel = clientConfig.isEnableIgnoreL();
			}
			if(isNotEmpty(clientConfig.isEnableInvoiceMatch())) {
				ignoreInvoiceMatch = clientConfig.isEnableInvoiceMatch();
			}
		}else {
			ignoreHyphen = true;
			ignoreSlash = true;
			ignoreZeroOrO = true;
			ignoreCapitalI = true;
			ignorel = true;
			ignoreInvoiceMatch = true;
		}
		
		//List<PurchaseRegister> savePPRList = Lists.newArrayList();
		//List<GSTR2> savePGSTR2List = Lists.newArrayList();
		List<PurchaseRegister> savePPRProbableList = Lists.newArrayList();
		//List<String> pmatchingid = Lists.newArrayList();
		if (isNotEmpty(notingstr2a)) {
			for (PurchaseRegister purchaseRegister : notingstr2a) {
				if ((isEmpty(purchaseRegister.getMatchingStatus()) || (isNotEmpty(purchaseRegister.getMatchingStatus()) && !purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) && !matchedids.contains(purchaseRegister.getId().toString())) {
				boolean mstatus = false;
				List<GSTR2> savePRGList = Lists.newArrayList();
				List<GSTR2> savePPRRList = Lists.newArrayList();
				List<GSTR2> savePRGINVNOList = Lists.newArrayList();
				List<GSTR2> savePRGINVDATEList = Lists.newArrayList();
				List<GSTR2> savePRGGSTNOList = Lists.newArrayList();
				List<GSTR2> savePRGTAXList = Lists.newArrayList();
				List<GSTR2> savePRGINVVALUEList = Lists.newArrayList();
				List<GSTR2> savePRPList = Lists.newArrayList();
				List<PurchaseRegister> savePPRGList = Lists.newArrayList();
				//logger.info(CLASSNAME + " reconcileRemainingRecords purchaseRegister macting status checking");
				for (GSTR2 gstr2 : gstr2aInvoic) {
					if (!mstatus) {
						if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
								&& purchaseRegister.getInvtype().equals(invType)) {
								if (isNotEmpty(purchaseRegister.getB2b())) {
									for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
											if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
												if (isNotEmpty(gstr2.getB2b()) && isNotEmpty(gstr2.getB2b().get(0).getCtin())
													&& isNotEmpty(gstr2.getB2b().get(0).getInv())
													&& isNotEmpty(gstr2.getB2b().get(0).getInv().get(0).getInum())
													&& isNotEmpty(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = gstrInvoiceDetails.getIdt();
													String dateAfterString = gstr2.getB2b().get(0).getInv().get(0).getIdt();
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = gstr2.getB2b().get(0).getInv().get(0).getInum().trim();
													String gstr2InvoiceNo = gstrInvoiceDetails.getInum().trim();
													if(ignoreHyphen) {
														if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
														}
														 if(gstr2InvoiceNo.contains("-")) {
															 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
														 }
													}
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													 if(gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
												if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
												 }
												gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
													&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
													if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
																|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															if(gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add(gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))
														&& (gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
															gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
														}
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotalamount())&& isNotEmpty(gstr2.getTotalamount())
															&& ((((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
															&& (purchaseRegister.getTotalamount()- gstr2.getTotalamount()) >= 0) || (((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
															&& (gstr2.getTotalamount()- purchaseRegister.getTotalamount()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
																|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
																|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))
																&& (((purchaseRegister.getTotalamount() - gstr2.getTotalamount()) == 0) 
																|| ((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) == 0))) {
																if (gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add(gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
																|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0))) {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add(gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
															}
													}
											} else if ((gstrb2b.getCtin().trim()).equals((gstr2.getB2b().get(0).getCtin().trim()))
														&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
													if(isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0))) {
															if(ignoreInvoiceMatch) {
																List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
																List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getB2b().get(0).getInv().get(0).getInum().trim()));
																if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRPList.add((GSTR2) gstr2);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(purchaseRegister.getId().toString());
																			matched2A.add((GSTR2)gstr2);
																			matchedPR.add(purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																				invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																			}
																			if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																				gstNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																				gstNoMisMatched2A.remove((GSTR2)gstr2);
																			}
																			savePPRRList.add((GSTR2) gstr2);
																			mstatus = true;
																		}
																	}
																}else {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				savePRPList.add((GSTR2) gstr2);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2A.add((GSTR2)gstr2);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																					invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																					gstNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				savePPRRList.add((GSTR2) gstr2);
																				mstatus = true;
																			}
																		}
																	}else {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((GSTR2) gstr2);
																				invoiceNoMisMatched2A.add((GSTR2) gstr2);
																				invoiceNoMisMatchedPR.add(purchaseRegister);
																			}
																		}
																	}
																}
															}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		savePRPList.add((GSTR2) gstr2);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2A.add((GSTR2)gstr2);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																			invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																			gstNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		savePPRRList.add((GSTR2) gstr2);
																		mstatus = true;
																	}
																}
															}else {
																
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRGINVNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		savePRGINVNOList.add((GSTR2) gstr2);
																		invoiceNoMisMatched2A.add((GSTR2) gstr2);
																		invoiceNoMisMatchedPR.add(purchaseRegister);
																	}
																}
															}
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2.getB2b().get(0).getInv().get(0).getIdt())) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if (isNotEmpty(gstr2.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0))) {
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add(gstr2);
																gstNoMisMatched2A.add((GSTR2)gstr2);
																gstNoMisMatchedPR.add(purchaseRegister);
															}
														}
														}
													}
												}
											}
										}
									}
								}
						} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
								if (isNotEmpty(purchaseRegister.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : purchaseRegister.getCdn()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
											if (isNotEmpty(gstrInvoiceDetails.getNtNum())&& isNotEmpty(gstrInvoiceDetails.getNtDt())) {
												if (isNotEmpty(gstr2.getCdn()) && isNotEmpty(gstr2.getCdn().get(0)) && isNotEmpty(gstr2.getCdn().get(0).getCtin())
													&& isNotEmpty(gstr2.getCdn().get(0).getNt())
													&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getNtDt())
													&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getNtNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
													String dateAfterString = myFormat.format(gstr2.getCdn().get(0).getNt().get(0).getNtDt());
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim();
													String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
													if(ignoreHyphen) {
													 if(purchaseregisterInvoiceNo.contains("-")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													 }
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
													}
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													 if(gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
													 if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
												 }
												 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
													&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
														&& daysBetweenInvoiceDate <= allowedDays
														&& gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
													if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
													}
													List<Double> pTxValues = Lists.newArrayList();
													if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2.getCdn().get(0).getNt().get(0).getItms())) {
														for (GSTRItems gstrItem : gstr2.getCdn().get(0).getNt().get(0).getItms()) {
															pTxValues.add(gstrItem.getItem().getTxval());
														}
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
																|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
																|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (dateBeforeString.equals(dateAfterString)) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															if(gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()))
														&& (gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
													}
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
															|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
															&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
															&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
															&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
															|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
															&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
																|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)
																|| ((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotalamount())&& isNotEmpty(gstr2.getTotalamount())
																		&& (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) == 0)
																		|| ((gstr2.getTotalamount()- purchaseRegister.getTotalamount()) == 0)))) {
																if (dateBeforeString.equals(dateAfterString)) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
																|| (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
																&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
																|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													}
												} else if ((gstrcdn.getCtin().trim()).equals((gstr2.getCdn().get(0).getCtin().trim()))
														&& dateBeforeString.equals(dateAfterString)) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if(isNotEmpty(gstr2.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2.getCdn().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
														if(ignoreInvoiceMatch) {
														List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
														List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		savePRPList.add((GSTR2) gstr2);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2A.add((GSTR2)gstr2);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																			invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																			gstNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		savePPRRList.add((GSTR2) gstr2);
																		mstatus = true;
																	}
																}
															}else {
																if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			savePRPList.add((GSTR2) gstr2);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(purchaseRegister.getId().toString());
																			matched2A.add((GSTR2)gstr2);
																			matchedPR.add(purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																				invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																			}
																			if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																				gstNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																				gstNoMisMatched2A.remove((GSTR2)gstr2);
																			}
																			savePPRRList.add((GSTR2) gstr2);
																			mstatus = true;
																		}
																	}
																}else {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((GSTR2) gstr2);
																			invoiceNoMisMatched2A.add((GSTR2) gstr2);
																			invoiceNoMisMatchedPR.add(purchaseRegister);
																		}
																	}
																}
															}
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	savePRPList.add((GSTR2) gstr2);
																	savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}else {
															if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
															}else {
																if(savePRGINVNOList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	savePRGINVNOList.add((GSTR2) gstr2);
																	invoiceNoMisMatched2A.add((GSTR2) gstr2);
																	invoiceNoMisMatchedPR.add(purchaseRegister);
																}
															}
														}
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((gstr2.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
														&& dateBeforeString.equals(dateAfterString)) {
													if(isNotEmpty(gstrcdn.getCfs())) {
														purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
													}
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
														&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
															
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add((GSTR2) gstr2);
																gstNoMisMatched2A.add((GSTR2)gstr2);
																gstNoMisMatchedPR.add(purchaseRegister);
															}
														}
														}
													}
											}
										}
									}
								}
							}
						}else if (invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(purchaseRegister.getInvtype()) && purchaseRegister.getInvtype().equals(invType)) {
								if (isNotEmpty(purchaseRegister.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : purchaseRegister.getImpGoods()) {
											if (isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
												if (isNotEmpty(gstr2.getImpGoods()) && isNotEmpty(gstr2.getImpGoods().get(0).getBoeNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
													String dateAfterString = myFormat.format(gstr2.getImpGoods().get(0).getBoeDt());
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = (gstr2.getImpGoods().get(0).getBoeNum().toString()).trim();
													String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
													if(ignoreHyphen) {
														if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
														}
														 if(gstr2InvoiceNo.contains("-")) {
															 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
														 }
													}
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													 if(gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
												if (purchaseregisterInvoiceNo.contains("o") || purchaseregisterInvoiceNo.contains("O")) {
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
														purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
												 }
												gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												 if(isEmpty(gstrimpg.getStin())) {
													 gstrimpg.setStin(" ");
												 }
												 if(isEmpty(gstr2) || isEmpty(gstr2.getImpGoods()) || isEmpty(gstr2.getImpGoods().get(0)) || isEmpty(gstr2.getImpGoods().get(0).getStin())) {
													 gstr2.getImpGoods().get(0).setStin(" ");
												 }
												
												
												if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
													&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
																|| ((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															if(gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())
														&& (gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin()).trim())){
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
															|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)
																|| ((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax())
																&& (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) == 0) 
																|| ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))
																&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
																	if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															savePPRRList.add((GSTR2) gstr2);
															matchedids.add(purchaseRegister.getId().toString());
															matched2A.add((GSTR2)gstr2);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																invoiceNoMisMatched2A.remove((GSTR2)gstr2);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																gstNoMisMatched2A.remove((GSTR2)gstr2);
															}
															mstatus = true;
														} else {
															if(gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)
																|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)	|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																
															if(gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else if(!gstrimpg.getBoeVal().equals(gstr2.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2) gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2)gstr2);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2A.add((GSTR2)gstr2);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																		invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																		gstNoMisMatched2A.remove((GSTR2)gstr2);
																	}
																	savePPRRList.add((GSTR2) gstr2);
																	mstatus = true;
																}
															}
														}
													}
											} else if ((gstrimpg.getStin().trim()).equals((gstr2.getImpGoods().get(0).getStin().trim()))
														&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
															if(ignoreInvoiceMatch) {
																List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
																List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim()));
																if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRPList.add((GSTR2) gstr2);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2A.add((GSTR2)gstr2);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																					invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																					gstNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				savePPRRList.add((GSTR2) gstr2);
																				mstatus = true;
																			}
																		}
																	}else {

																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((GSTR2) gstr2);
																				invoiceNoMisMatched2A.add((GSTR2) gstr2);
																				invoiceNoMisMatchedPR.add(purchaseRegister);
																			}
																		}
																	}
																}else {
																	
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {	
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRPList.add((GSTR2) gstr2);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2A.add((GSTR2)gstr2);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																					invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																					gstNoMisMatched2A.remove((GSTR2)gstr2);
																				}
																				savePPRRList.add((GSTR2) gstr2);
																				mstatus = true;
																			}
																		}
																	}else{
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((GSTR2)gstr2);
																				invoiceNoMisMatched2A.add((GSTR2) gstr2);
																				invoiceNoMisMatchedPR.add(purchaseRegister);
																			}
																		}
																	}
																}
															}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		savePRPList.add((GSTR2) gstr2);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2A.add((GSTR2)gstr2);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																			invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																			gstNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		savePPRRList.add((GSTR2) gstr2);
																		mstatus = true;
																	}
																}
															}else {
																if (isEmpty(gstr2.getMatchingStatus()) || gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																	if(savePRGList.size()<1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePPRGList.add((PurchaseRegister) purchaseRegister);
																		savePRGList.add((GSTR2)gstr2);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2A.add((GSTR2)gstr2);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains((GSTR2)gstr2)) {
																			invoiceNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains((GSTR2)gstr2)) {
																			gstNoMisMatched2A.remove((GSTR2)gstr2);
																		}
																		savePPRRList.add((GSTR2) gstr2);
																		mstatus = true;
																	}
																}
															}
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2.getDateofinvoice()))) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)
														|| (((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)
														|| (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
														
														if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add((GSTR2) gstr2);
																gstNoMisMatched2A.add((GSTR2)gstr2);
																gstNoMisMatchedPR.add(purchaseRegister);
															}
														}
														}
													}
												}
											}
										}
									}
								}
							}else{
								//logger.info(CLASSNAME + " reconcileRemainingRecords for loop break");
								break;
							}
						
						}
					if(isNotEmpty(savePPRRList) && savePPRRList.size() > 0) {
						gstr2aInvoic.removeAll(savePPRRList);
					}
					
				}
			}
		}
	}
	
	void saveBulkPR(List<PurchaseRegister> savePRList) {
		int batchCount = 2000;
		if(savePRList.size() > batchCount) {
			int index = 0;
			while((savePRList.size()-index) > batchCount) {
				List<PurchaseRegister> subList = savePRList.subList(index, index+batchCount);
				purchaseRepository.save(subList);
				index=index+batchCount;
			}
			purchaseRepository.save(savePRList.subList(index, savePRList.size()));
		} else {
			purchaseRepository.save(savePRList);
		}
	}
	
	void saveBulkGSTR2(List<GSTR2> saveGSTRList) {
		int batchCount = 2000;
		if(saveGSTRList.size() > batchCount) {
			int index = 0;
			while((saveGSTRList.size()-index) > batchCount) {
				List<GSTR2> subList = saveGSTRList.subList(index, index+batchCount);
				gstr2Repository.save(subList);
				index=index+batchCount;
			}
			gstr2Repository.save(saveGSTRList.subList(index, saveGSTRList.size()));
		} else {
			gstr2Repository.save(saveGSTRList);
		}
	}
	
	
	private void updateMismatchedStatusForPageable(List<PurchaseRegister> prr, List<GSTR2> gstr2List, final String invType, ClientConfig clientConfig, String clientid,List<String> matchedids,List<PurchaseRegister> matchedPR, List<GSTR2> matched2A,List<PurchaseRegister> invoiceNoMisMatchedPR, List<GSTR2> invoiceNoMisMatched2A,List<PurchaseRegister> gstNoMisMatchedPR, List<GSTR2> gstNoMisMatched2A) {
		final String method = "updateMismatchedStatusForPageable ::";
		logger.debug(CLASSNAME + method + "Purchase Content Size="+prr.size());
		logger.debug(CLASSNAME + method + "GSTR2 Size="+gstr2List.size());
		Double allowedDiff = 0d;
		Double allowedDays = 0d;
		boolean ignoreHyphen = true;
		boolean ignoreSlash = true;
		boolean ignoreZeroOrO = true;
		boolean ignoreCapitalI = true;
		boolean ignorel = true;
		boolean ignoreInvoiceMatch = true;
		if(isNotEmpty(clientConfig)) {
			if(isNotEmpty(clientConfig.getReconcileDiff())) {
				allowedDiff = clientConfig.getReconcileDiff();
			}
			if(isNotEmpty(clientConfig.getAllowedDays())) {
				allowedDays = clientConfig.getAllowedDays();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreHyphen())) {
				ignoreHyphen = clientConfig.isEnableIgnoreHyphen();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreSlash())) {
				ignoreSlash = clientConfig.isEnableIgnoreSlash();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreZero())) {
				ignoreZeroOrO = clientConfig.isEnableIgnoreZero();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreI())) {
				ignoreCapitalI = clientConfig.isEnableIgnoreI();
			}
			if(isNotEmpty(clientConfig.isEnableIgnoreL())) {
				ignorel = clientConfig.isEnableIgnoreL();
			}
			if(isNotEmpty(clientConfig.isEnableInvoiceMatch())) {
				ignoreInvoiceMatch = clientConfig.isEnableInvoiceMatch();
			}
		}else {
			ignoreHyphen = true;
			ignoreSlash = true;
			ignoreZeroOrO = true;
			ignoreCapitalI = true;
			ignorel = true;
			ignoreInvoiceMatch = true;
		}
		List<PurchaseRegister> purchaseRegisters = Lists.newArrayList();
		purchaseRegisters.addAll(prr);
		//List<GSTR2> saveGSTR2List = Lists.newArrayList();
		List<PurchaseRegister> savePRProbableList = Lists.newArrayList();
		if (isNotEmpty(gstr2List)) {
			for (GSTR2 gstr2 : gstr2List) {
				if ((isEmpty(gstr2.getMatchingStatus()) || (isNotEmpty(gstr2.getMatchingStatus()) && !gstr2.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) && !matchedids.contains(gstr2.getId().toString())) {
					//gstr2.setMatchingStatus("Not In Purchases");
					//gstr2 = gstr2Repository.save(gstr2);
					boolean mstatus = false;
					List<PurchaseRegister> savePRGList = Lists.newArrayList();
					List<PurchaseRegister> savePRRList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVDATEList = Lists.newArrayList();
					List<PurchaseRegister> savePRGGSTNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGTAXList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVVALUEList = Lists.newArrayList();
					List<PurchaseRegister> savePRPList = Lists.newArrayList();
					//logger.info("purchaseRegister matching status checking ::"+gstr2.getId().toString());
					for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if(!mstatus) {
						if (isEmpty(purchaseRegister.getMatchingStatus()) || (isNotEmpty(purchaseRegister.getMatchingStatus()) && !purchaseRegister.getMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getB2b())) {
									for (GSTRB2B gstrb2b : gstr2.getB2b()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
											if(isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
											if (isNotEmpty(purchaseRegister.getB2b())
													&& isNotEmpty(purchaseRegister.getB2b().get(0).getCtin())
													&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv())
													&& isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = gstrInvoiceDetails.getIdt();
												String dateAfterString = purchaseRegister.getB2b().get(0).getInv().get(0).getIdt();
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												 try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												 } catch (Exception e) {
												       e.printStackTrace();
												 }
												String purchaseregisterInvoiceNo = (purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim();
												String gstr2InvoiceNo = (gstrInvoiceDetails.getInum()).trim();
												 if(ignoreHyphen) {
													 if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													 }
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												 }
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													if (gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
												 }
												 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
													&& (gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())
													&& gstrInvoiceDetails.getPos().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getPos())) {
														if(isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
														if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																	|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																	&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																	|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																		if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																			if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																}else {
																	purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
																matchedids.add(gstr2.getId().toString());
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matched2A.add(gstr2);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2A.contains(gstr2)) {
																	invoiceNoMisMatched2A.remove(gstr2);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2A.contains(gstr2)) {
																	gstNoMisMatched2A.remove(gstr2);
																}
																mstatus = true;
															} else {
																if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																	if(savePRGTAXList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																		savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(gstr2.getId().toString());
																		matched2A.add(gstr2);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains(gstr2)) {
																			invoiceNoMisMatched2A.remove(gstr2);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains(gstr2)) {
																			gstNoMisMatched2A.remove(gstr2);
																		}
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		mstatus = true;
																	}
																}else {
																	if(savePRGList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRGList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(gstr2.getId().toString());
																		matched2A.add(gstr2);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains(gstr2)) {
																			invoiceNoMisMatched2A.remove(gstr2);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains(gstr2)) {
																			gstNoMisMatched2A.remove(gstr2);
																		}
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		mstatus = true;
																	}
																}
															}
												} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
															&& (gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())) {
														if (isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
															if (daysBetweenInvoiceDate <= allowedDays) {
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																				&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																				|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																				&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																				&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																				|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))
																				&& (((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																				|| ((purchaseRegister.getTotalamount()	- gstr2.getTotalamount()) == 0))) {
																					if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																						if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			}
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		} else {
																			if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																				if(savePRGTAXList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																					savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}else if(((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0) && ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0) && !gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																				if(savePRGINVVALUEList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																					savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}else {
																				if(savePRGList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					savePRGList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}
																		}
															} else {
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																		if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																			if(savePRGINVDATEList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}else if(!gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																			if(savePRGINVVALUEList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}
																	}
															}
														} else if ((gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())
																&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																if (isNotEmpty(gstrb2b.getCfs())) {
																	purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
																}
																Double alldDiff = 0d;
																if (allowedDiff == 0d) {
																	alldDiff = 1d;
																} else {
																	alldDiff = allowedDiff;
																}
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																	&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																	&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																	|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																	&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0))) {
																	if (ignoreInvoiceMatch) {
																		List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																		List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																		if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																				}else {
																					if(savePRPList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						savePRPList.add((PurchaseRegister) purchaseRegister);
																						savePRProbableList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2.getId().toString());
																						matched2A.add(gstr2);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2A.contains(gstr2)) {
																							invoiceNoMisMatched2A.remove(gstr2);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2A.contains(gstr2)) {
																							gstNoMisMatched2A.remove(gstr2);
																						}
																						mstatus = true;
																					}
																				}
																			}else {
																				if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																				}else {
																					if(savePRGINVNOList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																						invoiceNoMisMatched2A.add(gstr2);
																						invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																					}
																				}
																			}
																		} else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}	
																		}else{
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					invoiceNoMisMatched2A.add(gstr2);
																					invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRPList.add((PurchaseRegister) purchaseRegister);
																			savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}
																} else {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			invoiceNoMisMatched2A.add(gstr2);
																			invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																}
															}
														} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
																&& gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
															if (isNotEmpty(gstrb2b.getCfs())) {
																purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0))) {
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRGGSTNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																		gstNoMisMatched2A.add(gstr2);
																		gstNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																	}
																}
																}
															}
														}
													}
												}
											}
										}
							} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2.getCdn()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
											if (isNotEmpty(gstrInvoiceDetails.getNtNum()) && isNotEmpty(gstrInvoiceDetails.getNtDt())) {
												if (isNotEmpty(purchaseRegister.getCdn().get(0).getCtin())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt())
														&& isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum()) && isNotEmpty(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
													String dateAfterString = myFormat.format(purchaseRegister.getCdn().get(0).getNt().get(0).getNtDt());
													float daysBetween = 0f;
													double daysBetweenInvoiceDate = 0d;
													try {
														Date dateBefore = myFormat.parse(dateBeforeString);
														Date dateAfter = myFormat.parse(dateAfterString);
														long difference = dateAfter.getTime() - dateBefore.getTime();
														daysBetween = (difference / (1000 * 60 * 60 * 24));
														daysBetweenInvoiceDate = Math.abs((double) daysBetween);
													} catch (Exception e) {
														e.printStackTrace();
													}
													String purchaseregisterInvoiceNo = purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim();
													String gstr2InvoiceNo = gstrInvoiceDetails.getNtNum().trim();
													if (ignoreHyphen) {
														if (purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
														}
														if (gstr2InvoiceNo.contains("-")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
														}
													}
													if (ignoreSlash) {
														if (purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
														}
														if (gstr2InvoiceNo.contains("/")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
														}
													}
													if (ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
														}
														if (gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
															gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
														}
													}
													if (ignoreCapitalI) {
														if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
													}
													if (ignorel) {
														if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
													}
													gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
													purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
													if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))
															&& (gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
														if(isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
													if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
														 && (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
														&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
														&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
															purchaseRegister.setMatchingId(gstr2.getId().toString());
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePRRList.add((PurchaseRegister) purchaseRegister);
														matchedids.add(gstr2.getId().toString());
														matched2A.add(gstr2);
														matchedPR.add((PurchaseRegister) purchaseRegister);
														if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
															invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
														}
														if(invoiceNoMisMatched2A.contains(gstr2)) {
															invoiceNoMisMatched2A.remove(gstr2);
														}
														if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
															gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
														}
														if(gstNoMisMatched2A.contains(gstr2)) {
															gstNoMisMatched2A.remove(gstr2);
														}
														mstatus = true;
													} else {
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
															if(savePRGTAXList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matchedids.add(gstr2.getId().toString());
																matched2A.add(gstr2);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2A.contains(gstr2)) {
																	invoiceNoMisMatched2A.remove(gstr2);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2A.contains(gstr2)) {
																	gstNoMisMatched2A.remove(gstr2);
																}
																mstatus = true;
															}
														}else {
															if(savePRGList.size() < 1) {
																purchaseRegister.setMatchingId(gstr2.getId().toString());
																purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePRGList.add((PurchaseRegister) purchaseRegister);
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matchedids.add(gstr2.getId().toString());
																matched2A.add(gstr2);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2A.contains(gstr2)) {
																	invoiceNoMisMatched2A.remove(gstr2);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2A.contains(gstr2)) {
																	gstNoMisMatched2A.remove(gstr2);
																}
																mstatus = true;
															}
														}
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& (gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														if (daysBetweenInvoiceDate <= allowedDays) {
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																	purchaseRegister.setMatchingId(gstr2.getId().toString());
																	if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) == 0)))
																		&& (((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																		|| ((purchaseRegister.getTotalamount()	- gstr2.getTotalamount()) == 0))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				mstatus = true;
																			}else {
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		} else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																	} else {
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
																	savePRRList.add((PurchaseRegister) purchaseRegister);
																	matchedids.add(gstr2.getId().toString());
																	if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																	}
																	if(invoiceNoMisMatched2A.contains(gstr2)) {
																		invoiceNoMisMatched2A.remove(gstr2);
																	}
																	if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																		gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																	}
																	if(gstNoMisMatched2A.contains(gstr2)) {
																		gstNoMisMatched2A.remove(gstr2);
																	}
																	mstatus = true;
																} else {
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																		if(savePRGTAXList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																			savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}else if(((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0) && ((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0) &&!gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																		if(savePRGINVVALUEList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}else {
																		if(savePRGList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}
																}
															} else {
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																		
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																		if(savePRGINVDATEList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}else if(!gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																		if(savePRGINVVALUEList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}else {
																		if(savePRGList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}
																}
															}
														} else if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin()).trim())&& dateBeforeString.equals(dateAfterString)) {
															if(isNotEmpty(gstrcdn.getCfs())) {
																purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																	&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																	&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																	|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																	&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}
																		}else {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					invoiceNoMisMatched2A.add(gstr2);
																					invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2.getId().toString());
																					matched2A.add(gstr2);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2A.contains(gstr2)) {
																						invoiceNoMisMatched2A.remove(gstr2);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2A.contains(gstr2)) {
																						gstNoMisMatched2A.remove(gstr2);
																					}
																					mstatus = true;
																				}
																			}	
																		}else{
																			if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setMatchingId(gstr2.getId().toString());
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					invoiceNoMisMatched2A.add(gstr2);
																					invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			savePRPList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2.getId().toString());
																			matched2A.add(gstr2);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2A.contains(gstr2)) {
																				invoiceNoMisMatched2A.remove(gstr2);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2A.contains(gstr2)) {
																				gstNoMisMatched2A.remove(gstr2);
																			}
																			mstatus = true;
																		}
																	}
																}else {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			invoiceNoMisMatched2A.add(gstr2);
																			invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																}
															}
														} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
																&& dateBeforeString.equals(dateAfterString)) {
															if(isNotEmpty(gstrcdn.getCfs())) {
																purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
															}
															Double alldDiff = 0d;
															if (allowedDiff == 0d) {
																alldDiff = 1d;
															} else {
																alldDiff = allowedDiff;
															}
															if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																	
																if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																}else {
																	if(savePRGGSTNOList.size() < 1) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																		gstNoMisMatched2A.add(gstr2);
																		gstNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																	}
																}
																}
															}
												}
											}
										}
									}
								}
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2.getInvtype()) && gstr2.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2.getImpGoods()) {
										if(isNotEmpty(gstrimpg.getBoeNum()) && isNotEmpty(gstrimpg.getBoeDt())) {
											if(isNotEmpty(purchaseRegister.getImpGoods()) && isNotEmpty(purchaseRegister.getImpGoods().get(0)) && isNotEmpty(purchaseRegister.getImpGoods().get(0).getBoeNum()) && isNotEmpty(purchaseRegister.getDateofinvoice())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(purchaseRegister.getDateofinvoice());
												float daysBetween = 0f;
												double daysBetweenInvoiceDate = 0d;
												 try {
												       Date dateBefore = myFormat.parse(dateBeforeString);
												       Date dateAfter = myFormat.parse(dateAfterString);
												       long difference = dateAfter.getTime() - dateBefore.getTime();
												       daysBetween = (difference / (1000*60*60*24));
												       daysBetweenInvoiceDate = Math.abs((double)daysBetween);
												 } catch (Exception e) {
												       e.printStackTrace();
												 }
												String purchaseregisterInvoiceNo = (purchaseRegister.getImpGoods().get(0).getBoeNum().toString()).trim();
												String gstr2InvoiceNo = (gstrimpg.getBoeNum().toString()).trim();
												 if(ignoreHyphen) {
													 if(purchaseregisterInvoiceNo.contains("-")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("-", "");
													 }
													 if(gstr2InvoiceNo.contains("-")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("-", "");
													 }
												 }
												 if(ignoreSlash) {
													 if(purchaseregisterInvoiceNo.contains("/")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("/", "");
													 }
													if (gstr2InvoiceNo.contains("/")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("/", "");
													 }
												 }
												 if(ignoreZeroOrO) {
														if (purchaseregisterInvoiceNo.contains("o")	|| purchaseregisterInvoiceNo.contains("O")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("o", "0");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("O", "0");
													 }
													 if(gstr2InvoiceNo.contains("o") || gstr2InvoiceNo.contains("O")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("o", "0");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("O", "0");
													 }
												 }
												 if(ignoreCapitalI) {
													 if(purchaseregisterInvoiceNo.contains("I") || purchaseregisterInvoiceNo.contains("i")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("I", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("i", "1");
													 }
													 if(gstr2InvoiceNo.contains("I") || gstr2InvoiceNo.contains("i")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("I", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("i", "1");
													 }
												 }
												 if(ignorel) {
													 if(purchaseregisterInvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("l", "1");
															purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.replaceAll("L", "1");
													 }
													 if(gstr2InvoiceNo.contains("l") || purchaseregisterInvoiceNo.contains("L")) {
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("l", "1");
														 gstr2InvoiceNo = gstr2InvoiceNo.replaceAll("L", "1");
													 }
												 }
												 gstr2InvoiceNo = gstr2InvoiceNo.toLowerCase();
												 purchaseregisterInvoiceNo = purchaseregisterInvoiceNo.toLowerCase();
												 
												 if(isEmpty(gstrimpg.getStin())) {
													 gstrimpg.setStin(" ");
												 }
												 if(isEmpty(purchaseRegister) || isEmpty(purchaseRegister.getImpGoods()) || isEmpty(purchaseRegister.getImpGoods().get(0)) || isEmpty(purchaseRegister.getImpGoods().get(0).getStin())) {
													 purchaseRegister.getImpGoods().get(0).setStin(" ");
												 }
												 
												 if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
															&& ((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getImpGoods().get(0).getBoeNum().toString()).trim().toLowerCase())
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																
																if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))) {
																		purchaseRegister.setMatchingId(gstr2.getId().toString());
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						mstatus = true;
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		matchedids.add(gstr2.getId().toString());
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		matched2A.add(gstr2);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2A.contains(gstr2)) {
																			invoiceNoMisMatched2A.remove(gstr2);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2A.contains(gstr2)) {
																			gstNoMisMatched2A.remove(gstr2);
																		}
																		mstatus = true;
																	} else {
																		if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGTAXList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																					&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																					&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																					|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																					&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString());
																				if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2.getTotaltax()) == 0)))
																					&& (((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																					|| ((purchaseRegister.getTotalamount()	- gstr2.getTotalamount()) == 0))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			} else {
																				if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																					if(savePRGTAXList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2.getId().toString());
																						matched2A.add(gstr2);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2A.contains(gstr2)) {
																							invoiceNoMisMatched2A.remove(gstr2);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2A.contains(gstr2)) {
																							gstNoMisMatched2A.remove(gstr2);
																						}
																						mstatus = true;
																					}
																				}else if(!gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																					if(savePRGINVVALUEList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2.getId().toString());
																						matched2A.add(gstr2);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2A.contains(gstr2)) {
																							invoiceNoMisMatched2A.remove(gstr2);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2A.contains(gstr2)) {
																							gstNoMisMatched2A.remove(gstr2);
																						}
																						mstatus = true;
																					}
																				}else {
																					if(savePRGList.size() < 1) {
																						purchaseRegister.setMatchingId(gstr2.getId().toString());
																						purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																						gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																						savePRGList.add((PurchaseRegister) purchaseRegister);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2.getId().toString());
																						matched2A.add(gstr2);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2A.contains(gstr2)) {
																							invoiceNoMisMatched2A.remove(gstr2);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2A.contains(gstr2)) {
																							gstNoMisMatched2A.remove(gstr2);
																						}
																						mstatus = true;
																					}
																				}
																			}
																} else {
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																			&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																					&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																					&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																					|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= allowedDiff)
																					&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																		
																		if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGINVDATEList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}else if(!gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGINVVALUEList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}
																		}
																}
															} else if ((gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()).trim())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																	Double alldDiff = 0d;
																	if (allowedDiff == 0d) {
																		alldDiff = 1d;
																	} else {
																		alldDiff = allowedDiff;
																	}
																	if ((isNotEmpty(gstr2.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																				&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																				&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																				|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																				&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						if(savePRPList.size() < 1) {
																							purchaseRegister.setMatchingId(gstr2.getId().toString());
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							savePRPList.add((PurchaseRegister) purchaseRegister);
																							savePRProbableList.add((PurchaseRegister) purchaseRegister);
																							savePRRList.add((PurchaseRegister) purchaseRegister);
																							matchedids.add(gstr2.getId().toString());
																							matched2A.add(gstr2);
																							matchedPR.add((PurchaseRegister) purchaseRegister);
																							if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(invoiceNoMisMatched2A.contains(gstr2)) {
																								invoiceNoMisMatched2A.remove(gstr2);
																							}
																							if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(gstNoMisMatched2A.contains(gstr2)) {
																								gstNoMisMatched2A.remove(gstr2);
																							}
																							mstatus = true;
																						}
																					}
																				}else {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						if(savePRGINVNOList.size() < 1) {
																							purchaseRegister.setMatchingId(gstr2.getId().toString());
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																							invoiceNoMisMatched2A.add(gstr2);
																							invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																						}
																					}
																				}
																			}else {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						if(savePRPList.size() < 1) {
																							purchaseRegister.setMatchingId(gstr2.getId().toString());
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							savePRPList.add((PurchaseRegister) purchaseRegister);
																							savePRProbableList.add((PurchaseRegister) purchaseRegister);
																							savePRRList.add((PurchaseRegister) purchaseRegister);
																							matchedids.add(gstr2.getId().toString());
																							matched2A.add(gstr2);
																							matchedPR.add((PurchaseRegister) purchaseRegister);
																							if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(invoiceNoMisMatched2A.contains(gstr2)) {
																								invoiceNoMisMatched2A.remove(gstr2);
																							}
																							if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(gstNoMisMatched2A.contains(gstr2)) {
																								gstNoMisMatched2A.remove(gstr2);
																							}
																							mstatus = true;
																						}
																					}	
																				}else{
																					if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																					}else {
																						if(savePRGINVNOList.size() < 1) {
																							purchaseRegister.setMatchingId(gstr2.getId().toString());
																							purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																							invoiceNoMisMatched2A.add(gstr2);
																							invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																						}
																					}
																				}
																			}
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRProbableList.add((PurchaseRegister) purchaseRegister);
																				savePRPList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2.getId().toString());
																				matched2A.add(gstr2);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2A.contains(gstr2)) {
																					invoiceNoMisMatched2A.remove(gstr2);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2A.contains(gstr2)) {
																					gstNoMisMatched2A.remove(gstr2);
																				}
																				mstatus = true;
																			}
																		}
																	} else {
																		if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setMatchingId(gstr2.getId().toString());
																				purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																				invoiceNoMisMatched2A.add(gstr2);
																				invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																			}
																		}
																	}
																}
															} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																	&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																Double alldDiff = 0d;
																if (allowedDiff == 0d) {
																	alldDiff = 1d;
																} else {
																	alldDiff = allowedDiff;
																}
																if ((isNotEmpty(gstr2.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2.getTotaltax()) >= 0)))
																	&& (isNotEmpty(gstr2.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																			&& ((((gstr2.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																			&& (gstr2.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																			|| (((purchaseRegister.getTotalamount()- gstr2.getTotalamount()) <= alldDiff)
																			&& (purchaseRegister.getTotalamount() - gstr2.getTotalamount()) >= 0)))) {
																	if(isNotEmpty(purchaseRegister.getMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getMatchingStatus())) {
																	}else {
																		if(savePRGGSTNOList.size() < 1) {
																			purchaseRegister.setMatchingId(gstr2.getId().toString());
																			purchaseRegister.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																			gstr2.setMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																			savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																			gstNoMisMatched2A.add(gstr2);
																			gstNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																		}
																	}
																	}
																}
															}
														}
													}
												}
											}
						}
					}else{
						break;
					}
					}
					if(isNotEmpty(savePRRList) && savePRRList.size() > 0) {
						if(isNotEmpty(purchaseRegisters)) {
							purchaseRegisters.removeAll(savePRRList);
						}
					}
				}
			}
		}
	}
	
	
	
}
