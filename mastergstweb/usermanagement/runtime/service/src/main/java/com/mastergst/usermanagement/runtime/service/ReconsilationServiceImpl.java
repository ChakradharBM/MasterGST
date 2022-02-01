package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.PurchageRegisterDao;
import com.mastergst.usermanagement.runtime.dao.ReconsilationDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.GSTR2AIMPG;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRImportItems;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.ReconsileGstr2bMap;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BB2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BCDN;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPG;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPGSEZ;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BItems;
import com.mastergst.usermanagement.runtime.repository.ClientConfigRepository;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BSupportRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.response.MisMatchVO;
import com.mastergst.usermanagement.runtime.service.InvoicesMappingServiceImpl.Reconsile;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class ReconsilationServiceImpl implements ReconsilationService {
	private static final Logger logger = LogManager.getLogger(ReconsilationServiceImpl.class.getName());
	private static final String CLASSNAME = "ReconsilationServiceImpl::";
	
	@Autowired
	private ReconsilationDao reconsilationDao;
	
	@Autowired
	private ClientRepository clientRepository;
	@Autowired
	private PurchaseRegisterRepository purchaseRepository;
	@Autowired
	private  GSTR2BSupportRepository  gstr2bSupportRepository;
	@Autowired private ClientConfigRepository clientConfigRepository;
	@Autowired private ReconcileTempRepository reconcileTempRepository;
	@Autowired private ClientService clientService;
	@Autowired private MongoTemplate mongoTemplate;
	@Autowired private PurchageRegisterDao purchageRegisterDao;
	
	@Override
	public Page<? extends InvoiceParent> getPurchaseInvoices(String clientid,int start, int length, int month, int year, String matchingId, String matchingStatus ,String yearcode, Boolean billdate, boolean isMonthly) throws Exception{
		final String method = "saveClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Page<? extends InvoiceParent> invoices = reconsilationDao.getPurchaseInvoices(clientid, 0, -1, month, year, matchingId, matchingStatus,yearcode,billdate, isMonthly);
		if(isNotEmpty(invoices.getContent())){
			invoices.getContent().forEach(inv -> inv.setDocId(inv.getId().toString()));
		}
		return invoices;
	}
	
	@Override
	public Page<? extends InvoiceParent> getGstr2BSupportInvoices(String clientid,int start, int length, int month, int year, String matchingId, String matchingStatus ,boolean isMonthly) throws Exception{
		final String method = "saveClient::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Page<? extends InvoiceParent> invoices = reconsilationDao.getGstr2BSupportInvoices(clientid, 0, -1, month, year, matchingId, matchingStatus, isMonthly);
		if(isNotEmpty(invoices.getContent())){
			invoices.getContent().forEach(inv -> inv.setDocId(inv.getId().toString()));
		}
		return invoices;
	}
	
	
	
	@Override
	public Map<String, String> getGstr2bData(String id, String clientid, int month, int year) {
		
		GSTR2B invoice = reconsilationDao.getGstr2bInvoices(clientid, month, year);
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getData())) {
			GSTR2BDocData docData = invoice.getData().getDocdata();
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			try {
				Client client = clientRepository.findOne(clientid);
				
				gst2bSupportDocuments(invoice, UUID.randomUUID().toString(), invoice.getFullname(), client, null, strMonth);
				return null;
				//convertGstr2bToInvoices(invoice.getFullname(), clientid, fp, docData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public Page<? extends InvoiceParent> getPurchaseInvoices(String clientid, String matchingId, String matchingStatus, int month, int year) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<InvoiceParent> searchGstr2bInvoicess(List<String> ids, String clientid, String fp){
		
	
		
		return null;
	}
	
	@Async
	private void gst2bSupportDocuments(GSTR2B invoice, String key, String fullname, Client client, String userid, String returnPeriod) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		GSTR2BDocData  doc =invoice.getData().getDocdata();
		if(isNotEmpty(doc) && isNotEmpty(doc.getB2b())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BB2B b2b : doc.getB2b()) {
				String invType = MasterGSTConstants.B2B;			 
				List<GSTR2BInvoiceDetails> inv = b2b.getInv();
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRB2B b2bb = new GSTRB2B();
					b2bb.setCtin(b2b.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					BeanUtils.copyProperties(ginv, gstInvDetails);
					b2bb.setInv(Arrays.asList(gstInvDetails));	
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getDiffprcnt())) {
						gstInvDetails.setDiffPercent(ginv.getDiffprcnt().doubleValue());						
					}
					List<GSTRItems> items = Lists.newArrayList();
					Double totval = 0d;
					for(GSTR2BItems itm : ginv.getItems()) {
						GSTRItems gitem = new GSTRItems();
						GSTRItemDetails item = new GSTRItemDetails();
						
						Double txval = 0d, igst = 0d, cgst = 0d, sgst = 0d, cess = 0d, rt = 0d; 
						if(isNotEmpty(itm.getTxval())) {
							txval += itm.getTxval();
							totval += itm.getTxval();
						}
						if(isNotEmpty(itm.getIgst())) {
							igst += itm.getIgst();
							totval += itm.getIgst();
						}
						if(isNotEmpty(itm.getCgst())) {
							cgst += itm.getCgst();
							totval += itm.getCgst();
						}
						if(isNotEmpty(itm.getSgst())) {
							sgst += itm.getSgst();
							totval += itm.getSgst();
						}
						if(isNotEmpty(itm.getCess())) {
							cess += itm.getCess();
							totval += itm.getCess();
						}
						if(isNotEmpty(itm.getRt())) {
							rt += itm.getRt();
						}
						item.setTxval(txval);
						item.setIamt(igst);
						item.setCamt(cgst);
						item.setSamt(sgst);
						item.setCsamt(cess);
						item.setRt(rt);
						gitem.setItem(item);
						items.add(gitem);
					}
					gstInvDetails.setVal(totval);
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), b2b.getSupprd(), userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setDocKey(key);
					invLst.add(individualInvoice);
				} 
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getB2ba())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BB2B b2b : doc.getB2ba()) {
				String invType = MasterGSTConstants.B2BA;			 
				List<GSTR2BInvoiceDetails> inv = b2b.getInv();
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRB2B b2bb = new GSTRB2B();
					b2bb.setCtin(b2b.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					BeanUtils.copyProperties(ginv, gstInvDetails);
					b2bb.setInv(Arrays.asList(gstInvDetails));	
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getDiffprcnt())) {
						gstInvDetails.setDiffPercent(ginv.getDiffprcnt().doubleValue());						
					}
					List<GSTRItems> items = Lists.newArrayList();
					Double totval = 0d;
					for(GSTR2BItems itm : ginv.getItems()) {
						GSTRItems gitem = new GSTRItems();
						GSTRItemDetails item = new GSTRItemDetails();
						
						Double txval = 0d, igst = 0d, cgst = 0d, sgst = 0d, cess = 0d, rt = 0d; 
						if(isNotEmpty(itm.getTxval())) {
							txval += itm.getTxval();
							totval += itm.getTxval();
						}
						if(isNotEmpty(itm.getIgst())) {
							igst += itm.getIgst();
							totval += itm.getIgst();
						}
						if(isNotEmpty(itm.getCgst())) {
							cgst += itm.getCgst();
							totval += itm.getCgst();
						}
						if(isNotEmpty(itm.getSgst())) {
							sgst += itm.getSgst();
							totval += itm.getSgst();
						}
						if(isNotEmpty(itm.getCess())) {
							cess += itm.getCess();
							totval += itm.getCess();
						}
						if(isNotEmpty(itm.getRt())) {
							rt += itm.getRt();
						}
						item.setTxval(txval);
						item.setIamt(igst);
						item.setCamt(cgst);
						item.setSamt(sgst);
						item.setCsamt(cess);
						item.setRt(rt);
						gitem.setItem(item);
						items.add(gitem);
					}
					gstInvDetails.setVal(totval);
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), b2b.getSupprd(), userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					invLst.add(individualInvoice);
				}
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getCdnr())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BCDN cdnr : doc.getCdnr()) {
				String invType = MasterGSTConstants.CREDIT_DEBIT_NOTES;			 
				List<GSTR2BInvoiceDetails> inv = cdnr.getNt();
				
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRCreditDebitNotes cdn = new GSTRCreditDebitNotes();
					cdn.setCtin(cdnr.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					
					BeanUtils.copyProperties(ginv, gstInvDetails);
					    
	                cdn.setNt(Arrays.asList(gstInvDetails));
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getDiffprcnt())) {
						gstInvDetails.setDiffPercent(ginv.getDiffprcnt().doubleValue());						
					}
					gstInvDetails.setNtNum(ginv.getNtnum());
					try {
						gstInvDetails.setNtDt(sdf.parse(ginv.getDt()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					gstInvDetails.setVal(Double.valueOf(ginv.getVal()));
					gstInvDetails.setRchrg(ginv.getRev());
					
					gstInvDetails.setNtty(ginv.getTyp());
					
					List<GSTRItems> items = Lists.newArrayList();
					Double totval = 0d;
					for(GSTR2BItems itm : ginv.getItems()) {
						GSTRItems gitem = new GSTRItems();
						GSTRItemDetails item = new GSTRItemDetails();
						
						Double txval = 0d, igst = 0d, cgst = 0d, sgst = 0d, cess = 0d, rt = 0d; 
						if(isNotEmpty(itm.getTxval())) {
							txval += itm.getTxval();
							totval += itm.getTxval();
						}
						if(isNotEmpty(itm.getIgst())) {
							igst += itm.getIgst();
							totval += itm.getIgst();
						}
						if(isNotEmpty(itm.getCgst())) {
							cgst += itm.getCgst();
							totval += itm.getCgst();
						}
						if(isNotEmpty(itm.getSgst())) {
							sgst += itm.getSgst();
							totval += itm.getSgst();
						}
						if(isNotEmpty(itm.getCess())) {
							cess += itm.getCess();
							totval += itm.getCess();
						}
						if(isNotEmpty(itm.getRt())) {
							rt += itm.getRt();
						}
						item.setTxval(txval);
						item.setIamt(igst);
						item.setCamt(cgst);
						item.setSamt(sgst);
						item.setCsamt(cess);
						item.setRt(rt);
						gitem.setItem(item);
						items.add(gitem);
					}
					gstInvDetails.setVal(totval);
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), cdnr.getSupprd(), userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					invLst.add(individualInvoice);
				} 
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getCdnra())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BCDN cdnr : doc.getCdnra()) {
				String invType = MasterGSTConstants.CDNA;			 
				List<GSTR2BInvoiceDetails> inv = cdnr.getNt();
				
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRCreditDebitNotes cdn = new GSTRCreditDebitNotes();
					cdn.setCtin(cdnr.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					
					BeanUtils.copyProperties(ginv, gstInvDetails);
					    
	                cdn.setNt(Arrays.asList(gstInvDetails));
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getDiffprcnt())) {
						gstInvDetails.setDiffPercent(ginv.getDiffprcnt().doubleValue());						
					}
					gstInvDetails.setNtNum(ginv.getNtnum());
					gstInvDetails.setOntNum(ginv.getOntnum());
					try {
						gstInvDetails.setNtDt(sdf.parse(ginv.getDt()));
						gstInvDetails.setOntDt(sdf.parse(ginv.getOntdt()));
					} catch (ParseException e) {
						e.printStackTrace();
					}
					
					gstInvDetails.setVal(Double.valueOf(ginv.getVal()));
					gstInvDetails.setRchrg(ginv.getRev());
					gstInvDetails.setNtty(ginv.getTyp());
					
					List<GSTRItems> items = Lists.newArrayList();
					Double totval = 0d;
					for(GSTR2BItems itm : ginv.getItems()) {
						GSTRItems gitem = new GSTRItems();
						GSTRItemDetails item = new GSTRItemDetails();
						
						Double txval = 0d, igst = 0d, cgst = 0d, sgst = 0d, cess = 0d, rt = 0d; 
						if(isNotEmpty(itm.getTxval())) {
							txval += itm.getTxval();
							totval += itm.getTxval();
						}
						if(isNotEmpty(itm.getIgst())) {
							igst += itm.getIgst();
							totval += itm.getIgst();
						}
						if(isNotEmpty(itm.getCgst())) {
							cgst += itm.getCgst();
							totval += itm.getCgst();
						}
						if(isNotEmpty(itm.getSgst())) {
							sgst += itm.getSgst();
							totval += itm.getSgst();
						}
						if(isNotEmpty(itm.getCess())) {
							cess += itm.getCess();
							totval += itm.getCess();
						}
						if(isNotEmpty(itm.getRt())) {
							rt += itm.getRt();
						}
						item.setTxval(txval);
						item.setIamt(igst);
						item.setCamt(cgst);
						item.setSamt(sgst);
						item.setCsamt(cess);
						item.setRt(rt);
						gitem.setItem(item);
						items.add(gitem);
					}
					gstInvDetails.setVal(totval);
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), cdnr.getSupprd(), userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					invLst.add(individualInvoice);
				} 
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getImpg())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BIMPG impg : doc.getImpg()) {
				String invType = MasterGSTConstants.IMPG;
				
				GSTR2AIMPG gstrImportDetails = new GSTR2AIMPG();
				
				gstrImportDetails.setRefdt(impg.getRefdt());
				gstrImportDetails.setPortcd(impg.getPortcode());
				gstrImportDetails.setBenum(Integer.parseInt(impg.getBoenum()));
				gstrImportDetails.setBedt(impg.getBoedt());
				gstrImportDetails.setCsamt(impg.getCess());
				gstrImportDetails.setTxval(impg.getTxval());
				gstrImportDetails.setIamt(impg.getIgst());
				gstrImportDetails.setAmd(impg.getIsamd());
				
				GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, invoice.getFp(), userid, client.getId().toString(), invType);
				individualInvoice.setDocKey(key);
				individualInvoice.setFullname(fullname);
				individualInvoice.setBranch("Main Branch");
				invLst.add(individualInvoice);
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getImpgsez())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BIMPGSEZ impgsez : doc.getImpgsez()) {
				String invType = MasterGSTConstants.IMPGSEZ;
				
				for(GSTR2BIMPG boe : impgsez.getBoe()) {
					
					GSTR2AIMPG gstrImportDetails = new GSTR2AIMPG();
					gstrImportDetails.setSgstin(impgsez.getCtin());
					gstrImportDetails.setRefdt(boe.getRefdt());
					gstrImportDetails.setPortcd(boe.getPortcode());
					gstrImportDetails.setBenum(Integer.parseInt(boe.getBoenum()));
					gstrImportDetails.setBedt(boe.getBoedt());
					gstrImportDetails.setCsamt(boe.getCess());
					gstrImportDetails.setTxval(boe.getTxval());
					gstrImportDetails.setIamt(boe.getIgst());
					gstrImportDetails.setAmd(boe.getIsamd());
					GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, invoice.getFp(), userid, client.getId().toString(), invType);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					invLst.add(individualInvoice);
				}
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		} 
	}
	
	private GSTR2BSupport populateGSTR2BSupport(GSTRInvoiceDetails gstrInvoiceDetails, final String gstn, final String fp,
			final String userid, final String clientid, final String invType, final String ctin,final String cfs,final String cfs3b,final String fldtr1,final String flprdr1,final String dtcancel) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		GSTR2BSupport individualInvoice = new GSTR2BSupport();
		individualInvoice.setGstin(gstn);
		individualInvoice.setFp(fp);
		individualInvoice.setInvtype(invType);
		individualInvoice.setUserid(userid);
		individualInvoice.setClientid(clientid);
		if (invType.equals(MasterGSTConstants.CDNA) || invType.equals(MasterGSTConstants.CDNUR)
				|| invType.equals(MasterGSTConstants.CDNURA) || invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
			individualInvoice.setInvoiceno(gstrInvoiceDetails.getNtNum());
		}else {
			individualInvoice.setInvoiceno(gstrInvoiceDetails.getInum());
		}
		try {
			if (invType.equals(MasterGSTConstants.CDNA) || invType.equals(MasterGSTConstants.CDNUR)
					|| invType.equals(MasterGSTConstants.CDNURA)
					|| invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(gstrInvoiceDetails.getNtDt())) {
					individualInvoice.setDateofinvoice(gstrInvoiceDetails.getNtDt());
				}
			}else {
				if(isNotEmpty(gstrInvoiceDetails.getIdt())) {
					individualInvoice.setDateofinvoice(simpleDateFormat.parse(gstrInvoiceDetails.getIdt()));
				}
			}
		} catch (ParseException e) {
			logger.error(CLASSNAME + "populateGSTR2 :: ERROR in invoice date", e);
		}
		GSTRB2B indGstrb2b = new GSTRB2B();
		if (isNotEmpty(ctin)) {
			indGstrb2b.setCtin(ctin);
		}
		
		if (invType.equals(B2B) || invType.equals(B2BA)) {
			if(isNotEmpty(cfs)) {
				indGstrb2b.setCfs(cfs);
			}
			if(isNotEmpty(cfs3b)) {
				indGstrb2b.setCfs3b(cfs3b);
			}
			if(isNotEmpty(fldtr1)) {
				indGstrb2b.setFldtr1(fldtr1);
			}
			if(isNotEmpty(flprdr1)) {
				indGstrb2b.setFlprdr1(flprdr1);
			}
			if(isNotEmpty(dtcancel)) {
				indGstrb2b.setDtcancel(dtcancel);
			}
			indGstrb2b.getInv().add(gstrInvoiceDetails);
			if (isNotEmpty(gstrInvoiceDetails) && isNotEmpty(gstrInvoiceDetails.getRchrg())) {
				if ("Y".equalsIgnoreCase(gstrInvoiceDetails.getRchrg())) {
					individualInvoice.setRevchargetype("Reverse");
				} else {
					individualInvoice.setRevchargetype("Regular");
				}
			}
		} else if (invType.equals(CREDIT_DEBIT_NOTES) || invType.equals(CDNA)) {
			GSTRCreditDebitNotes note = new GSTRCreditDebitNotes();
			if (isNotEmpty(ctin)) {
				note.setCtin(ctin);
			}
			if(isNotEmpty(cfs)) {
				note.setCfs(cfs);
			}
			if(isNotEmpty(cfs3b)) {
				note.setCfs3b(cfs3b);
			}
			if(isNotEmpty(fldtr1)) {
				note.setFldtr1(fldtr1);
			}
			if(isNotEmpty(flprdr1)) {
				note.setFlprdr1(flprdr1);
			}
			if(isNotEmpty(dtcancel)) {
				note.setDtcancel(dtcancel);
			}
			if(isNotEmpty(gstrInvoiceDetails.getAddress())){
				indGstrb2b.getInv().get(0).setAddress(gstrInvoiceDetails.getAddress());
			}
			note.getNt().add(gstrInvoiceDetails);
			
			if(invType.equals(CDNA)) {
				individualInvoice.getCdna().add(note);
			}else {
				individualInvoice.getCdn().add(note);				
			}
		}
		if(invType.equals(B2BA)) {
			individualInvoice.getB2ba().add(indGstrb2b);
			GSTRB2B indGstrb2ba = new GSTRB2B();
			if (isNotEmpty(ctin)) {
				indGstrb2ba.setCtin(ctin);
				individualInvoice.getB2b().add(indGstrb2ba);
			}
		}else {
			
			individualInvoice.getB2b().add(indGstrb2b);			
		}
		individualInvoice.setTotalamount(gstrInvoiceDetails.getVal());
		Double totalTaxVal = 0d;
		Double totalTax = 0d;
		Double totalITC = 0d;
		List<Item> items = Lists.newArrayList();
		for (GSTRItems gstrItem : gstrInvoiceDetails.getItms()) {
			Item item = new Item();
			Double itemTotal = 0d;
			item.setTaxablevalue(gstrItem.getItem().getTxval());
			
			totalTaxVal += gstrItem.getItem().getTxval();
			if(isNotEmpty(gstrItem.getItem().getTxval())) {
				itemTotal += gstrItem.getItem().getTxval(); 
			}
			item.setRateperitem(gstrItem.getItem().getTxval());
			item.setQuantity(Double.parseDouble("1"));
			if (isNotEmpty(gstrItem.getItem().getRt())) {
				item.setRate(gstrItem.getItem().getRt());
			}
			if (isNotEmpty(gstrItem.getItem().getIamt())) {
				item.setIgstamount(gstrItem.getItem().getIamt());
				item.setIgstrate(gstrItem.getItem().getRt());
				totalTax += gstrItem.getItem().getIamt();
				itemTotal += gstrItem.getItem().getIamt(); 
			}
			if (isNotEmpty(gstrItem.getItem().getCamt())) {
				item.setCgstamount(gstrItem.getItem().getCamt());
				item.setCgstrate(gstrItem.getItem().getRt() / 2);
				totalTax += gstrItem.getItem().getCamt();
				itemTotal += gstrItem.getItem().getCamt(); 
			}
			if (isNotEmpty(gstrItem.getItem().getSamt())) {
				item.setSgstamount(gstrItem.getItem().getSamt());
				item.setSgstrate(gstrItem.getItem().getRt() / 2);
				totalTax += gstrItem.getItem().getSamt();
				itemTotal += gstrItem.getItem().getSamt(); 
			}
			if (isNotEmpty(gstrItem.getItem().getCsamt())) {
				item.setCessamount(gstrItem.getItem().getCsamt());
				totalTax += gstrItem.getItem().getCsamt();
				itemTotal += gstrItem.getItem().getCsamt(); 
			}
			if (gstrInvoiceDetails.getItms().size() == 1) { //Fix for gstr2a invoice edit as purchase register 
				item.setTotal(gstrInvoiceDetails.getVal());
			}else {
				item.setTotal(itemTotal);
			}
			if (isNotEmpty(gstrItem.getItc())) {
				if (isNotEmpty(gstrItem.getItc().getiTax())) {
					totalITC += gstrItem.getItc().getiTax();
				}
				if (isNotEmpty(gstrItem.getItc().getcTax())) {
					totalITC += gstrItem.getItc().getcTax();
				}
				if (isNotEmpty(gstrItem.getItc().getsTax())) {
					totalITC += gstrItem.getItc().getsTax();
				}
				if (isNotEmpty(gstrItem.getItc().getCsTax())) {
					totalITC += gstrItem.getItc().getCsTax();
				}
			}
			items.add(item);
		}
		individualInvoice.setItems(items);
		individualInvoice.setTotaltaxableamount(totalTaxVal);
		individualInvoice.setTotaltax(totalTax);
		individualInvoice.setTotalitc(totalITC);
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		for (Item item : individualInvoice.getItems()) {
			if (isNotEmpty(item.getIgstamount())) {
				totalIGST += item.getIgstamount();
			}
			if (isNotEmpty(item.getCgstamount())) {
				totalCGST += item.getCgstamount();
			}
			if (isNotEmpty(item.getSgstamount())) {
				totalSGST += item.getSgstamount();
			}
			if (isNotEmpty(item.getExmepted())) {
				totalExempted += item.getExmepted();
			}
		}
		individualInvoice.setTotalIgstAmount(totalIGST);
		individualInvoice.setTotalCgstAmount(totalCGST);
		individualInvoice.setTotalSgstAmount(totalSGST);
		individualInvoice.setTotalExemptedAmount(totalExempted);
		
		return individualInvoice;
	}
	
	private GSTR2BSupport populateGSTR2IMPG(GSTR2AIMPG gstrImportDetailss, final String fp,	final String userid, final String clientid, final String invType) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		GSTR2BSupport individualInvoice = new GSTR2BSupport();
		individualInvoice.setFp(fp);
		individualInvoice.setInvtype(MasterGSTConstants.IMP_GOODS);
		individualInvoice.setUserid(userid);
		individualInvoice.setClientid(clientid);
		
		
		List<GSTRImportDetails> impGoods = null;
			impGoods = Lists.newArrayList();
			GSTRImportDetails gstrImportDetails = new GSTRImportDetails();
			try {
				if(isNotEmpty(gstrImportDetailss.getBedt())) {
					gstrImportDetails.setBoeDt(simpleDateFormat.parse(gstrImportDetailss.getBedt()));
					individualInvoice.setDateofinvoice(simpleDateFormat.parse(gstrImportDetailss.getBedt()));
				}
				if(isNotEmpty(gstrImportDetailss.getRefdt())) {
					individualInvoice.setBillDate(simpleDateFormat.parse(gstrImportDetailss.getRefdt()));
				}
			} catch (ParseException e) {
				logger.error(CLASSNAME + "populateGSTR2 :: ERROR in invoice date", e);
			}
			if(isNotEmpty(gstrImportDetailss.getBenum())) {
				gstrImportDetails.setBoeNum(gstrImportDetailss.getBenum());
				individualInvoice.setInvoiceno(gstrImportDetailss.getBenum().toString());
			}
			
			if(invType.equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				gstrImportDetails.setIsSez("N");
				gstrImportDetails.setStin(" ");
			}else {
				gstrImportDetails.setIsSez("Y");
				if(isNotEmpty(gstrImportDetailss.getSgstin())) {
					gstrImportDetails.setStin(gstrImportDetailss.getSgstin());
				}else {
					gstrImportDetails.setStin(" ");
				}
				if(isNotEmpty(gstrImportDetailss.getTdname())) {
					individualInvoice.setBilledtoname(gstrImportDetailss.getTdname());
				}
			}
			if(isNotEmpty(gstrImportDetailss.getPortcd())) {
				gstrImportDetails.setPortCode(gstrImportDetailss.getPortcd());
			}
			Double totalval = 0d;
			totalval += gstrImportDetailss.getTxval() == null ? 0.0 : gstrImportDetailss.getTxval();
			totalval += gstrImportDetailss.getIamt() == null ? 0.0 : gstrImportDetailss.getIamt();
			totalval += gstrImportDetailss.getCsamt() == null ? 0.0 : gstrImportDetailss.getCsamt();
			gstrImportDetails.setBoeVal(totalval);
			
			List<GSTRImportItems> itms = Lists.newArrayList();
			GSTRImportItems importItem = new GSTRImportItems();
			importItem.setNum(1);
			if(isNotEmpty(gstrImportDetailss.getTxval())) {
				importItem.setTxval(gstrImportDetailss.getTxval());
			}
			if(isNotEmpty(gstrImportDetailss.getIamt())) {
				importItem.setIamt(gstrImportDetailss.getIamt());
			}
			if(isNotEmpty(gstrImportDetailss.getCsamt())) {
				importItem.setCsamt(gstrImportDetailss.getCsamt());
			}
			itms.add(importItem);
			gstrImportDetails.setItms(itms);
			impGoods.add(gstrImportDetails);
		
			individualInvoice.setImpGoods(impGoods);
			individualInvoice.setTotalamount(totalval);
		Double totalTaxVal = 0d;
		Double totalTax = 0d;
		Double totalITC = 0d;
		List<Item> items = Lists.newArrayList();
		for (GSTRImportItems gstrItem : gstrImportDetails.getItms()) {
			Item item = new Item();
			Double itemTotal = 0d;
			
			if(isNotEmpty(gstrItem.getTxval())) {
				item.setTaxablevalue(gstrItem.getTxval());
				totalTaxVal += gstrItem.getTxval();
				itemTotal += gstrItem.getTxval();
				item.setRateperitem(gstrItem.getTxval());
			}
			
			item.setQuantity(Double.parseDouble("1"));
			if (isNotEmpty(gstrItem.getIamt())) {
				item.setIgstamount(gstrItem.getIamt());
				totalTax += gstrItem.getIamt();
				itemTotal += gstrItem.getIamt(); 
			}
			if (isNotEmpty(gstrItem.getCsamt())) {
				item.setCessamount(gstrItem.getCsamt());
				totalTax += gstrItem.getCsamt();
				itemTotal += gstrItem.getCsamt(); 
			}
			item.setTotal(itemTotal);
			items.add(item);
		}
		individualInvoice.setItems(items);
		individualInvoice.setTotaltaxableamount(totalTaxVal);
		individualInvoice.setTotaltax(totalTax);
		individualInvoice.setTotalitc(totalITC);
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		for (Item item : individualInvoice.getItems()) {
			if (isNotEmpty(item.getIgstamount())) {
				totalIGST += item.getIgstamount();
			}
			if (isNotEmpty(item.getCgstamount())) {
				totalCGST += item.getCgstamount();
			}
			if (isNotEmpty(item.getSgstamount())) {
				totalSGST += item.getSgstamount();
			}
			if (isNotEmpty(item.getExmepted())) {
				totalExempted += item.getExmepted();
			}
		}
		individualInvoice.setTotalIgstAmount(totalIGST);
		individualInvoice.setTotalCgstAmount(totalCGST);
		individualInvoice.setTotalSgstAmount(totalSGST);
		individualInvoice.setTotalExemptedAmount(totalExempted);
		
		return individualInvoice;
	}
	
	@Override
	public void updateMannualRecords(List<MisMatchVO> records, String returntype, String invoiceid) {
		final String method = "updateMismatchRecords ::";
		
		if (returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(records)) {
				PurchaseRegister purchaseRegister = purchaseRepository.findOne(invoiceid);
				List<String> ids = Lists.newArrayList();
				String rsn = records.size() > 1 ? "G2B-Multiple" : "G2B-Single";
				if (isNotEmpty(purchaseRegister)) {
					for (MisMatchVO record : records) {
						if (isNotEmpty(record.getPurchaseId())) {
							GSTR2BSupport gstr2 = gstr2bSupportRepository.findOne(record.getPurchaseId());
							gstr2.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
							gstr2.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
							gstr2.setGstr2bMatchingRsn(rsn);
							GSTR2BSupport gstr2b = gstr2bSupportRepository.save(gstr2);
							ids.add(gstr2b.getId().toString());
						}
					}
					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
					purchaseRegister.setGstr2bMatchingId(ids);
					purchaseRegister.setGstr2bMatchingRsn(rsn);
					purchaseRepository.save(purchaseRegister);
				}
			}
		} else {
			if (isNotEmpty(records)) {
				String rsn = records.size() > 1 ? "PR-Multiple" : "PR-Single";
				List<String> ids = Lists.newArrayList();
				GSTR2BSupport gstr2 = gstr2bSupportRepository.findOne(invoiceid);
				if (isNotEmpty(gstr2)) {
					for (MisMatchVO record : records) {
						PurchaseRegister purchaseRegister = purchaseRepository.findOne(record.getGstrId());
						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2.getId().toString()));
						purchaseRegister.setGstr2bMatchingRsn(rsn);
						PurchaseRegister prInv = purchaseRepository.save(purchaseRegister);
						ids.add(prInv.getId().toString());
					}
					gstr2.setGstr2bMatchingId(ids);
					gstr2.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);
					gstr2.setGstr2bMatchingRsn(rsn);
					gstr2bSupportRepository.save(gstr2);
				}
			}
		}

		logger.debug(CLASSNAME + method + END);
	}
	
	
	@Override
	public Page<? extends InvoiceParent> findByMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable) {
		InvoiceParent purinv = purchaseRepository.findOne(invoiceid);
		List<String> ids = Lists.newArrayList();
		if(isNotEmpty(purinv) && isNotEmpty(purinv.getGstr2bMatchingId()) && purinv.getGstr2bMatchingId().size()>0) {
			List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2bSupportRepository.findAll(purinv.getGstr2bMatchingId()));
			Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(monthlyInvoicess);
			return allinvoices;
		}
		return null; 
	}
	
	@Override
	public Page<? extends InvoiceParent> findByPrMatchingIdAndMatchingStatus(String invoiceid, String mannualMatched, Pageable pageable) {
		InvoiceParent purinv = gstr2bSupportRepository.findOne(invoiceid);
		List<String> ids = Lists.newArrayList();
		if(isNotEmpty(purinv) && isNotEmpty(purinv.getGstr2bMatchingId()) && purinv.getGstr2bMatchingId().size()>0) {
			List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(purchaseRepository.findAll(purinv.getGstr2bMatchingId()));
			Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(monthlyInvoicess);
			return allinvoices;
		}
		return null;
	}
	
	@Override
	@Transactional
	public void updateGstr2bMismatchStatus(String clientId, List<PurchaseRegister> purchaseRegisters, List<GSTR2BSupport> gstr2bList,
			final String invType, final String gstn, final String fp, final String monthlyOrYearly) {
		logger.debug(CLASSNAME + "updateMismatchStatus : Begin");
		if(gstr2bList == null) {
			if ("monthly".equalsIgnoreCase(monthlyOrYearly)) {
				gstr2bList = gstr2bSupportRepository.findByClientidAndFpAndInvtype(clientId, fp, invType);
			} else {
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
				gstr2bList = gstr2bSupportRepository.findByClientidAndFpInAndInvtype(clientId, rtarray, invType);
			}
		}
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
		List<PurchaseRegister> savePRList = Lists.newArrayList();
		List<GSTR2BSupport> saveGSTR2bList = Lists.newArrayList();
		if (isNotEmpty(gstr2bList)) {
			for (GSTR2BSupport gstr2b : gstr2bList) {
				if (NullUtil.isEmpty(gstr2b.getGstr2bMatchingStatus()) || (isNotEmpty(gstr2b.getGstr2bMatchingStatus()) && !gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
				gstr2b.setGstr2bMatchingStatus("Not In Purchases");
				gstr2b = gstr2bSupportRepository.save(gstr2b);
				for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if (NullUtil.isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || (isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && !purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getB2b())) {
									for (GSTRB2B gstrb2b : gstr2b.getB2b()) {
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
														if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																	|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																	|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																		if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																}else {
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2bList.add(gstr2b);
															} else {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePRList.add((PurchaseRegister) purchaseRegister);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																saveGSTR2bList.add(gstr2b);
															}
												} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
															&& (gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())) {
														if (isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
															if (daysBetweenInvoiceDate <= allowedDays) {
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																				&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																				|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																				&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																				&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																				|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																				if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																					if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					}else {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				}
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
																		} else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			saveGSTR2bList.add(gstr2b);
																		}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																	if (ignoreInvoiceMatch) {
																		List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																		List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																		if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				}
																			}else if (NullUtil.isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		} else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			saveGSTR2bList.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		saveGSTR2bList.add(gstr2b);
																	}
																}
															}
														}
													}
												}
											}
										}
								} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2b.getCdn()) {
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
													if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
														}
														savePRList.add((PurchaseRegister) purchaseRegister);
														saveGSTR2bList.add(gstr2b);
													} else {
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePRList.add((PurchaseRegister) purchaseRegister);
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														saveGSTR2bList.add(gstr2b);
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& (gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														if (daysBetweenInvoiceDate <= allowedDays) {
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		} else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	} else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	saveGSTR2bList.add(gstr2b);
																}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			}
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																}else {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2bList.add(gstr2b);
																	}
																}
															}
														}
												}
											}
										}
									}
								}
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								
								
								if (isNotEmpty(gstr2b.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2b.getImpGoods()) {
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
																
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2bList.add(gstr2b);
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				saveGSTR2bList.add(gstr2b);
																			} else {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2bList.add(gstr2b);
																			}
																} else {
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
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
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					}
																				}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				}
																			} else {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else {
																		if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2bList.add(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																		if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			saveGSTR2bList.add(gstr2b);
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
							}
						}
		purchaseRepository.save(savePRList);
		gstr2bSupportRepository.save(saveGSTR2bList);
		
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
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		Page<? extends InvoiceParent> notInGstr2binvoices = purchaseRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIsNull(clientId, invTypes, stDate,endDate, pageable);
		Page<? extends InvoiceParent> notInGstr2Binvoicess = purchaseRepository.findByClientidAndInvtypeInAndGstr2bMatchingStatusAndDateofinvoiceBetween(clientId, invTypes, "Not In GSTR2B",stDate,endDate, pageable);
		List<PurchaseRegister> notingstr2b = Lists.newArrayList();
		List<PurchaseRegister> ngstr2b = (List<PurchaseRegister>) notInGstr2binvoices.getContent();
		List<PurchaseRegister> notingstr2bb = (List<PurchaseRegister>) notInGstr2Binvoicess.getContent();
		if(isNotEmpty(ngstr2b)) {
			notingstr2b.addAll(ngstr2b);
		}
		if(isNotEmpty(notingstr2bb)) {
			notingstr2b.addAll(notingstr2bb);
		}
		Page<GSTR2BSupport> gstr2bInvoices = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIsNull(clientId, invTypes, ystDate, yendDate, pageable);
		Page<GSTR2BSupport> gstr2bInvoicess = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatus(clientId, invTypes, ystDate, yendDate, "Not In Purchases", pageable);
		List<GSTR2BSupport> gstr2bInvoic = Lists.newArrayList();
		List<GSTR2BSupport> npur = gstr2bInvoices.getContent();
		List<GSTR2BSupport> gstr2bInvoice = gstr2bInvoicess.getContent();
		if(isNotEmpty(npur)) {
			gstr2bInvoic.addAll(npur);
		}
		if(isNotEmpty(gstr2bInvoice)) {
			gstr2bInvoic.addAll(gstr2bInvoice);
		}
		List<PurchaseRegister> savePPRList = Lists.newArrayList();
		List<GSTR2BSupport> savePGSTR2List = Lists.newArrayList();
		if (isNotEmpty(notingstr2b)) {
			for (PurchaseRegister purchaseRegister : notingstr2b) {
				for (GSTR2BSupport gstr2b : gstr2bInvoic) {
					if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
							&& purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getB2b())) {
								for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
										if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
											if (isNotEmpty(gstr2b.getB2b()) && isNotEmpty(gstr2b.getB2b().get(0).getCtin())
												&& isNotEmpty(gstr2b.getB2b().get(0).getInv())
												&& isNotEmpty(gstr2b.getB2b().get(0).getInv().get(0).getInum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = gstrInvoiceDetails.getIdt();
												String dateAfterString = gstr2b.getB2b().get(0).getInv().get(0).getIdt();
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
												String purchaseregisterInvoiceNo = gstr2b.getB2b().get(0).getInv().get(0).getInum().trim();
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
											if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
												if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& (gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
													}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()));
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2b);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
											if (isNotEmpty(gstr2b.getCdn()) && isNotEmpty(gstr2b.getCdn().get(0)) && isNotEmpty(gstr2b.getCdn().get(0).getCtin())
												&& isNotEmpty(gstr2b.getCdn().get(0).getNt())
												&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getNtDt())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
												String dateAfterString = myFormat.format(gstr2b.getCdn().get(0).getNt().get(0).getNtDt());
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
												String purchaseregisterInvoiceNo = gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim();
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
											if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
												if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												List<Double> pTxValues = Lists.newArrayList();
												if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getItms())) {
													for (GSTRItems gstrItem : gstr2b.getCdn().get(0).getNt().get(0).getItms()) {
														pTxValues.add(gstrItem.getItem().getTxval());
													}
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add((PurchaseRegister)purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
												}
											} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& (gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))) {
												if (isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
															|| ((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add((PurchaseRegister) purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister)purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
															|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}
												}
											} else if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getNtDt().equals(gstr2b.getCdn().get(0).getNt().get(0).getNtDt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
													&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
													|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
													if(ignoreInvoiceMatch) {
													List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
													List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()));
														if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
														}else {
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														}
														
														savePRList.add((PurchaseRegister) purchaseRegister);
													}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
														if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														savePPRList.add(purchaseRegister);
													}else {
														if (isEmpty(gstr2b.getGstr2bMatchingStatus())|| gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
											if (isNotEmpty(gstr2b.getImpGoods()) && isNotEmpty(gstr2b.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(gstr2b.getImpGoods().get(0).getBoeDt());
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
												String purchaseregisterInvoiceNo = (gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim();
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
											 if(isEmpty(gstr2b) || isEmpty(gstr2b.getImpGoods()) || isEmpty(gstr2b.getImpGoods().get(0)) || isEmpty(gstr2b.getImpGoods().get(0).getStin())) {
												 gstr2b.getImpGoods().get(0).setStin(" ");
											 }
											
											
											if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
												&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())
													&& (gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin()).trim())){
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
													&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()));
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2b);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
		gstr2bSupportRepository.save(savePGSTR2List);
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
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
	public void updateGstr2bMismatchStatusTrDate(String clientId, List<PurchaseRegister> purchaseRegisters,
			List<GSTR2BSupport> gstr2bList, String invType, String gstn, String fp, String monthlyOrYearly) {
		logger.debug(CLASSNAME + "updateMismatchStatus : Begin");
		if(gstr2bList == null) {
			if ("monthly".equalsIgnoreCase(monthlyOrYearly)) {
				gstr2bList = gstr2bSupportRepository.findByClientidAndFpAndInvtype(clientId, fp, invType);
			} else {
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
				gstr2bList = gstr2bSupportRepository.findByClientidAndFpInAndInvtype(clientId, rtarray, invType);
			}
		}
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
		List<PurchaseRegister> savePRList = Lists.newArrayList();
		List<GSTR2BSupport> saveGSTR2bList = Lists.newArrayList();
		if (isNotEmpty(gstr2bList)) {
			for (GSTR2BSupport gstr2b : gstr2bList) {
				if (NullUtil.isEmpty(gstr2b.getGstr2bMatchingStatus()) || (isNotEmpty(gstr2b.getGstr2bMatchingStatus()) && !gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
				gstr2b.setGstr2bMatchingStatus("Not In Purchases");
				gstr2b = gstr2bSupportRepository.save(gstr2b);
				for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if (NullUtil.isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || (isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && !purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getB2b())) {
									for (GSTRB2B gstrb2b : gstr2b.getB2b()) {
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
														if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																	|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																	|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																		if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																}else {
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																}
																savePRList.add((PurchaseRegister) purchaseRegister);
																saveGSTR2bList.add(gstr2b);
															} else {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePRList.add((PurchaseRegister) purchaseRegister);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																saveGSTR2bList.add(gstr2b);
															}
												} else if ((gstrInvoiceDetails.getInum().trim().toLowerCase()).equals((purchaseRegister.getB2b().get(0).getInv().get(0).getInum()).trim().toLowerCase())
															&& (gstrb2b.getCtin().trim()).equals((purchaseRegister.getB2b().get(0).getCtin()).trim())) {
														if (isNotEmpty(gstrb2b.getCfs())) {
															purchaseRegister.getB2b().get(0).setCfs(gstrb2b.getCfs());
														}
															if (daysBetweenInvoiceDate <= allowedDays) {
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																				&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																				|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																				&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																				&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																				|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																				if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																					if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					}else {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				}
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
																		} else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			saveGSTR2bList.add(gstr2b);
																		}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																	if (ignoreInvoiceMatch) {
																		List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																		List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																		if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				}
																			}else if (NullUtil.isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		} else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			saveGSTR2bList.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		saveGSTR2bList.add(gstr2b);
																	}
																}
															}
														}
													}
												}
											}
										}
								} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2b.getCdn()) {
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
													if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
														}
														savePRList.add((PurchaseRegister) purchaseRegister);
														saveGSTR2bList.add(gstr2b);
													} else {
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePRList.add((PurchaseRegister) purchaseRegister);
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														saveGSTR2bList.add(gstr2b);
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& (gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
														if (daysBetweenInvoiceDate <= allowedDays) {
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			}
																		} else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		}
																	} else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	saveGSTR2bList.add(gstr2b);
																}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			}
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		}
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	}
																	savePRList.add((PurchaseRegister) purchaseRegister);
																	saveGSTR2bList.add(gstr2b);
																}else {
																	if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2bList.add(gstr2b);
																	}
																}
															}
														}
												}
											}
										}
									}
								}
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								
								
								if (isNotEmpty(gstr2b.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2b.getImpGoods()) {
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
																
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		saveGSTR2bList.add(gstr2b);
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				saveGSTR2bList.add(gstr2b);
																			} else {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2bList.add(gstr2b);
																			}
																} else {
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			saveGSTR2bList.add(gstr2b);
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
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					}
																				}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				}
																			} else {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}else if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			}
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		}
																		savePRList.add((PurchaseRegister) purchaseRegister);
																		saveGSTR2bList.add(gstr2b);
																	} else {
																		if (isEmpty(purchaseRegister.getGstr2bMatchingStatus())|| purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRList.add((PurchaseRegister) purchaseRegister);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				saveGSTR2bList.add(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																		if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingId(Arrays.asList(purchaseRegister.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRList.add((PurchaseRegister) purchaseRegister);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			saveGSTR2bList.add(gstr2b);
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
							}
						}
		purchaseRepository.save(savePRList);
		gstr2bSupportRepository.save(saveGSTR2bList);
		
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
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		Page<? extends InvoiceParent> notInGstr2binvoices = purchaseRepository.findByClientidAndInvtypeInAndBillDateBetweenAndGstr2bMatchingStatusIsNull(clientId, invTypes, stDate,endDate, pageable);
		Page<? extends InvoiceParent> notInGstr2Binvoicess = purchaseRepository.findByClientidAndInvtypeInAndGstr2bMatchingStatusAndBillDateBetween(clientId, invTypes, "Not In GSTR2B",stDate,endDate, pageable);
		List<PurchaseRegister> notingstr2b = Lists.newArrayList();
		List<PurchaseRegister> ngstr2b = (List<PurchaseRegister>) notInGstr2binvoices.getContent();
		List<PurchaseRegister> notingstr2bb = (List<PurchaseRegister>) notInGstr2Binvoicess.getContent();
		if(isNotEmpty(ngstr2b)) {
			notingstr2b.addAll(ngstr2b);
		}
		if(isNotEmpty(notingstr2bb)) {
			notingstr2b.addAll(notingstr2bb);
		}
		Page<GSTR2BSupport> gstr2bInvoices = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIsNull(clientId, invTypes, ystDate, yendDate, pageable);
		Page<GSTR2BSupport> gstr2bInvoicess = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatus(clientId, invTypes, ystDate, yendDate, "Not In Purchases", pageable);
		List<GSTR2BSupport> gstr2bInvoic = Lists.newArrayList();
		List<GSTR2BSupport> npur = gstr2bInvoices.getContent();
		List<GSTR2BSupport> gstr2bInvoice = gstr2bInvoicess.getContent();
		if(isNotEmpty(npur)) {
			gstr2bInvoic.addAll(npur);
		}
		if(isNotEmpty(gstr2bInvoice)) {
			gstr2bInvoic.addAll(gstr2bInvoice);
		}
		List<PurchaseRegister> savePPRList = Lists.newArrayList();
		List<GSTR2BSupport> savePGSTR2List = Lists.newArrayList();
		if (isNotEmpty(notingstr2b)) {
			for (PurchaseRegister purchaseRegister : notingstr2b) {
				for (GSTR2BSupport gstr2b : gstr2bInvoic) {
					if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
							&& purchaseRegister.getInvtype().equals(invType)) {
							if (isNotEmpty(purchaseRegister.getB2b())) {
								for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
									for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
										if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
											if (isNotEmpty(gstr2b.getB2b()) && isNotEmpty(gstr2b.getB2b().get(0).getCtin())
												&& isNotEmpty(gstr2b.getB2b().get(0).getInv())
												&& isNotEmpty(gstr2b.getB2b().get(0).getInv().get(0).getInum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = gstrInvoiceDetails.getIdt();
												String dateAfterString = gstr2b.getB2b().get(0).getInv().get(0).getIdt();
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
												String purchaseregisterInvoiceNo = gstr2b.getB2b().get(0).getInv().get(0).getInum().trim();
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
											if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
												if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& (gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
													}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()));
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2b);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
													gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
											if (isNotEmpty(gstr2b.getCdn()) && isNotEmpty(gstr2b.getCdn().get(0)) && isNotEmpty(gstr2b.getCdn().get(0).getCtin())
												&& isNotEmpty(gstr2b.getCdn().get(0).getNt())
												&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getNtDt())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
												String dateAfterString = myFormat.format(gstr2b.getCdn().get(0).getNt().get(0).getNtDt());
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
												String purchaseregisterInvoiceNo = gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim();
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
											if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
												&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
												if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												List<Double> pTxValues = Lists.newArrayList();
												if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getItms())) {
													for (GSTRItems gstrItem : gstr2b.getCdn().get(0).getNt().get(0).getItms()) {
														pTxValues.add(gstrItem.getItem().getTxval());
													}
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add((PurchaseRegister)purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
												}
											} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
													&& (gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))) {
												if (isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
															|| ((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))) {
															if (dateBeforeString.equals(dateAfterString)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add((PurchaseRegister) purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister)purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
															|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add((PurchaseRegister) purchaseRegister);
														}
												}
											} else if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
													&& gstrInvoiceDetails.getNtDt().equals(gstr2b.getCdn().get(0).getNt().get(0).getNtDt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
													gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
													&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
													|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
													if(ignoreInvoiceMatch) {
													List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
													List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()));
														if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
														}else {
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														}
														
														savePRList.add((PurchaseRegister) purchaseRegister);
													}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
														if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														savePPRList.add(purchaseRegister);
													}else {
														if (isEmpty(gstr2b.getGstr2bMatchingStatus())|| gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add(gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
											if (isNotEmpty(gstr2b.getImpGoods()) && isNotEmpty(gstr2b.getImpGoods().get(0).getBoeNum())) {
												SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
												String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
												String dateAfterString = myFormat.format(gstr2b.getImpGoods().get(0).getBoeDt());
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
												String purchaseregisterInvoiceNo = (gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim();
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
											 if(isEmpty(gstr2b) || isEmpty(gstr2b.getImpGoods()) || isEmpty(gstr2b.getImpGoods().get(0)) || isEmpty(gstr2b.getImpGoods().get(0).getStin())) {
												 gstr2b.getImpGoods().get(0).setStin(" ");
											 }
											
											
											if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
												&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()))
												&& daysBetweenInvoiceDate <= allowedDays
												&& gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														}else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())
													&& (gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin()).trim())){
												if (daysBetweenInvoiceDate <= allowedDays) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
														&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
															|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
															&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
															|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
															if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
														} else {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePGSTR2List.add((GSTR2BSupport) gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														savePPRList.add(purchaseRegister);
													} else {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePPRList.add((PurchaseRegister) purchaseRegister);
													}
												}else {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRList.add(purchaseRegister);
														}
												}
										} else if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
													&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
											Double alldDiff = 0d;
											if (allowedDiff == 0d) {
												alldDiff = 1d;
											} else {
												alldDiff = allowedDiff;
											}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
														if(ignoreInvoiceMatch) {
															List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
															List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()));
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
															}
															savePRList.add((PurchaseRegister) purchaseRegister);
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															}
															savePGSTR2List.add((GSTR2BSupport) gstr2b);
															savePPRList.add(purchaseRegister);
														}else {
															if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePGSTR2List.add(gstr2b);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																savePPRList.add((PurchaseRegister) purchaseRegister);
															}
														}
												}
											} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
												if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
													&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
													&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
													|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
													&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
													&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
													&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
													&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
													|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
													&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
													if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
														gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
														savePGSTR2List.add(gstr2b);
														purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
														purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
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
		gstr2bSupportRepository.save(savePGSTR2List);
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
		
	}
	
	//Performances Changes code
	
	@Override
	public Map<String, Object> getInvoicesSupport(String clientid, int month, int year, boolean isTransactionDate, boolean isMonthly) {
		String yearCd = Utility.getYearCode(month, year);						
		
		Map<String, Object> supportObj = new HashMap<>();
		List<String> billedtoNames = reconsilationDao.getBillToNames(clientid, month, yearCd, isTransactionDate, isMonthly);
		
		supportObj.put("billedtoNames", billedtoNames);
		return supportObj;
	}
	
	@Override
	public Map<String, Object> getGstr2bReconcileInvoices(String id, String clientid, int month, int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTransactionDate, boolean isMonthly) {
		String yearCd = Utility.getYearCode(month, year);
		Page<PurchaseRegister> purchaseInvs = reconsilationDao.getReconcilePurchaseInvoices(id, clientid, month, year, yearCd, start, length, searchVal, filter, isTransactionDate, isMonthly);
		
		List<String> matchedIds = Lists.newArrayList();
		if(NullUtil.isNotEmpty(purchaseInvs) && NullUtil.isNotEmpty(purchaseInvs.getContent())) {
			for(PurchaseRegister prInv : purchaseInvs.getContent()) {
				prInv.setDocId(prInv.getId().toString());
				if(NullUtil.isNotEmpty(prInv.getGstr2bMatchingStatus())) {
					if(isNotEmpty(prInv.getGstr2bMatchingId())) {
						matchedIds.addAll(prInv.getGstr2bMatchingId());						
					}
				}
			}
		}
		long total = purchaseInvs.getTotalElements();
		Map<String, Object> invMap = Maps.newHashMap();
		List<GSTR2BSupport> gstr2bInvs = reconsilationDao.getMatchingIdInvoices(matchedIds);
		if(NullUtil.isNotEmpty(purchaseInvs) && NullUtil.isNotEmpty(purchaseInvs.getContent())) {
			if(NullUtil.isNotEmpty(gstr2bInvs)) {
				for(GSTR2BSupport gstr2b : gstr2bInvs) {
					gstr2b.setDocId(gstr2b.getId().toString());
				}
			}
		}
		List<ReconsileGstr2bMap> mappedInvs = Lists.newArrayList();
		if(NullUtil.isNotEmpty(purchaseInvs) && NullUtil.isNotEmpty(purchaseInvs.getContent())) {
			for(PurchaseRegister purchaseInv : purchaseInvs.getContent()) {
				ReconsileGstr2bMap invice = new ReconsileGstr2bMap();
				invice.setOrigin(MasterGSTConstants.PURCHASE_REGISTER);
				
				if(NullUtil.isNotEmpty(purchaseInv.getGstr2bMatchingStatus()) && NullUtil.isNotEmpty(purchaseInv.getGstr2bMatchingId())) {
					if(purchaseInv.getGstr2bMatchingId().size() >0) {
						for(GSTR2BSupport gstr2b : gstr2bInvs) {
							/*if(isNotEmpty(gstr2b.getGstr2bMatchingId()) && gstr2b.getGstr2bMatchingId().size() > 0) {
								if(gstr2b.getGstr2bMatchingId().contains(purchaseInv.getId().toString())) {
									invice.setGstr2b(gstr2b);
									break;
								}
							}*/
							
							if(purchaseInv.getGstr2bMatchingId().contains(gstr2b.getId().toString())) {
								invice.setGstr2b(gstr2b);
								break;
							}
						}
					}
				}
				invice.setPurchaseRegister(purchaseInv);
				mappedInvs.add(invice);
			}
		}
		
		Map<String, Object> map = customPageable(total, purchaseInvs, clientid, month, year, start, length, searchVal, filter, isMonthly);
		
		mappedInvs.addAll((List) map.get("gstr2bInvoices"));
		invMap.put("total", map.get("total"));
		invMap.put("mappedInvs", mappedInvs);
		
		TotalInvoiceAmount invAmounts = getGstr2bReconcileInvoiceTotals(clientid, month, year, yearCd, searchVal, filter, isTransactionDate, isMonthly);
		invMap.put("amts", invAmounts);
		return invMap;
	}
	
	private Map<String, Object> customPageable(long total ,Page<PurchaseRegister> invoices, String clientid, int month, int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isMonthly){
		List<String> fps = null;
		if(isMonthly) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			fps = Arrays.asList(fp);
		}else {
			fps = Arrays.asList("03"+year, "04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "03"+(year+1), "03"+(year+1));
		}
		Map<String, Object> invMap = Maps.newHashMap();
		
		long gstr2bInvsCount = reconsilationDao.getGstr2bInvoicesCount(clientid, fps, start, length, searchVal, filter);
		int size = (int) invoices.getTotalElements();
		int val = length - invoices.getContent().size();
		if(invoices.getContent().size() == 0) {
			val = start - size;
		}
		int i = 0;
		List<ReconsileGstr2bMap> mappedInvs = Lists.newArrayList();
		if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(invoices.getContent())) {
			int st = 0;
			Page<GSTR2BSupport> gstr2bInvs = reconsilationDao.getGstr2bInvoiceswithPagination(clientid, fps, st, length, searchVal, filter);
			if(NullUtil.isNotEmpty(invoices) && NullUtil.isNotEmpty(gstr2bInvs.getContent())) {
				for(GSTR2BSupport gstr2 : gstr2bInvs.getContent()) {
					if(NullUtil.isNotEmpty(gstr2)) {
						if(i <= val) {
							gstr2.setDocId(gstr2.getId().toString());
							ReconsileGstr2bMap invoice = new ReconsileGstr2bMap();
							invoice.setGstr2b(gstr2);
							invoice.setOrigin(MasterGSTConstants.GSTR2B);
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
			Page<GSTR2BSupport> gstr2bInvs = null;
			int pageLength = 100;
			/*
			 int pageSize = (int) (gstr2bInvsCount / length);
			 int remainder = (int) (gstr2bInvsCount % length);
			 */
			 int pageSize = (int) (gstr2bInvsCount / pageLength);
			 int remainder = (int) (gstr2bInvsCount % pageLength);
			if(remainder > 0) {
				pageSize += 1;
			}
			boolean flag = false;
			for(int st = 0; st < pageSize; st++) {
				int n = 0; 
				if(st != 0) {
					//n = st * length;
					n = st * pageLength;
				}
				gstr2bInvs = reconsilationDao.getGstr2bInvoiceswithPagination(clientid, fps, n, pageLength, searchVal, filter);
				if(flag) {
					break;
				}
				if(NullUtil.isNotEmpty(gstr2bInvs) && NullUtil.isNotEmpty(gstr2bInvs.getContent())) {	
					for(GSTR2BSupport gstr2 : gstr2bInvs) {
						if(j < val) {
							j++;
						}else {
							if(NullUtil.isNotEmpty(gstr2)) {
								if(k < length) {
									gstr2.setDocId(gstr2.getId().toString());
									ReconsileGstr2bMap invoice = new ReconsileGstr2bMap();
									invoice.setGstr2b(gstr2);
									invoice.setOrigin(MasterGSTConstants.GSTR2B);
									mappedInvs.add(invoice);
									++k;
								}
								if(k == gstr2bInvsCount || k >= length) {
									flag = true;
									break;
								}
							}	
						}
					}
				}
			}
		}
		long lstSize = gstr2bInvsCount;
		if(lstSize == 0) {
			lstSize = i;
		}
		total = total + lstSize;
		invMap.put("gstr2bInvoices", mappedInvs);
		invMap.put("total", total);
		return invMap;
	}
	
	private TotalInvoiceAmount getGstr2bReconcileInvoiceTotals(String clientid, int month, int year, String yearCd, String searchVal, InvoiceFilter filter, boolean isTransactionDate, boolean isMonthly) {
		List<String> fps = null;
		if(isMonthly) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			fps = Arrays.asList(fp);
		}else {
			fps = Arrays.asList("03"+year, "04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "03"+(year+1), "03"+(year+1));
		}
		TotalInvoiceAmount prAmounts = reconsilationDao.getPurchaseTotalInvoicesAmounts(clientid, month, yearCd, searchVal, filter, isTransactionDate, isMonthly);
		TotalInvoiceAmount gstr2bmounts = reconsilationDao.getGstr2bTotalInvoicesAmounts(clientid, fps, searchVal, filter);
		TotalInvoiceAmount totalInvAmount = new TotalInvoiceAmount();
		if(prAmounts != null && gstr2bmounts != null) {
			totalInvAmount.setTotalTransactions(prAmounts.getTotalTransactions()+gstr2bmounts.getTotalTransactions());
			totalInvAmount.setTotalTaxableAmount(prAmounts.getTotalTaxableAmount().add(gstr2bmounts.getTotalTaxableAmount()));
			totalInvAmount.setTotalExemptedAmount(prAmounts.getTotalExemptedAmount().add(gstr2bmounts.getTotalExemptedAmount()));
			totalInvAmount.setTotalTaxAmount(prAmounts.getTotalTaxAmount().add(gstr2bmounts.getTotalTaxAmount()));
			totalInvAmount.setTotalAmount(prAmounts.getTotalAmount().add(gstr2bmounts.getTotalAmount()));
			totalInvAmount.setTotalIGSTAmount(prAmounts.getTotalIGSTAmount().add(gstr2bmounts.getTotalIGSTAmount()));
			totalInvAmount.setTotalCGSTAmount(prAmounts.getTotalCGSTAmount().add(gstr2bmounts.getTotalCGSTAmount()));
			totalInvAmount.setTotalSGSTAmount(prAmounts.getTotalSGSTAmount().add(gstr2bmounts.getTotalSGSTAmount()));
			totalInvAmount.setTotalCESSAmount(prAmounts.getTotalCESSAmount().add(gstr2bmounts.getTotalCESSAmount()));
			return totalInvAmount;
		}else if(gstr2bmounts != null){
			return gstr2bmounts;
		}else {
			return prAmounts;
		}
	}
	
	@Override
	public Map<String, Object> getGstr2bReconcileSummary(String clientid, int month, int year, boolean isTransactionDate, boolean isMonthly) {
		String yearCd = Utility.getYearCode(month, year);						
		List<String> fps = null;
		if(isMonthly) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String fp = strMonth + year;
			fps = Arrays.asList(fp);
		}else {
			fps = Arrays.asList("03"+year, "04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "03"+(year+1), "03"+(year+1));
		}
		Map<String, Object> invMap = Maps.newHashMap();
		invMap.put("MATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("MATCHED_IN_OTHER_MONTHS", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MATCHED_IN_OTHER_MONTHS.getName(),isTransactionDate, isMonthly));
		invMap.put("ROUNDOFF_MATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.ROUNDOFF_MATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("PROBABLE_MATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.PROBABLE_MATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MISMATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("INVOICE_NO_MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_NO_MISMATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("TAX_MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.TAX_MISMATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("INVOICE_VALUE_MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_VALUE_MISMATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("GST_NO_MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.GST_NO_MISMATCHED.getName(),isTransactionDate, isMonthly));
		invMap.put("INVOICE_DATE_MISMATCHED", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.INVOICE_DATE_MISMATCHED.getName(),isTransactionDate, isMonthly));
			
		long prMannualMatchCounts = reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.MANUAL_MATCHED.getName(), isTransactionDate, isMonthly);
		invMap.put("NOT_IN_PURCHASES", reconsilationDao.getPurchaseReconcileSummary(clientid, month, yearCd, Reconsile.NOT_IN_GSTR2B.getName(),isTransactionDate, isMonthly));
		invMap.put("NOT_IN_GSTR2B", reconsilationDao.getGstr2bReconcileSummary(clientid, fps, Reconsile.NOT_IN_PURCHASES.getName()));
		long gstr2aMannualMatchCounts = reconsilationDao.getGstr2bReconcileSummary(clientid, fps, Reconsile.MANUAL_MATCHED.getName());
		invMap.put("MANUAL_MATCHED", prMannualMatchCounts + gstr2aMannualMatchCounts);
		return invMap;
	}
	
	//Reconcile Performances Changes
	
	@Transactional
	@Override
	@Async("reconcileTaskExecutor")
	public void updateGstr2bMismatchStatus(String clientid, final String invType, final String gstn, final String fp, int month, int year, final boolean isMonthly, final boolean isBilledDate) {
		final String method ="updateMismatchStatus ::";
		logger.debug(CLASSNAME + "updateMismatchStatus : Begin");
		ClientConfig clientConfig = getClientConfig(clientid);
		int defaultSize = 1000;
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
		Page<GSTR2BSupport> gstr2bList = null;
		List<String> fps = Lists.newArrayList();
		List<String> rtarray = Lists.newArrayList();
		if (isMonthly) {
			fps.add(fp);
		}else {
			String fpYear = fp.substring(2);
			int yr = Integer.parseInt(fpYear);
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			for (int j = 4; j <= 12; j++) {
				String strMonth = j < 10 ? "0" + j : j + "";
				rtarray.add(strMonth + (yr-1));
			}
			for (int k = 1; k <= 3; k++) {
				String strMonth = k < 10 ? "0" + k : k + "";
				rtarray.add(strMonth + (yr));
			}
		}
		
		
		boolean hasMore = true;
		while(hasMore) {
			List<String> gstr2bInvoiceNoList = Lists.newArrayList();
			List<GSTR2BSupport> gstr2bContent = Lists.newArrayList();
			Pageable pageable = new PageRequest(0, defaultSize, Sort.Direction.ASC, "invoiceno");
			Page<PurchaseRegister> purchaseRegisters = null;
			List<GSTR2BSupport> matched2B = Lists.newArrayList();
			List<GSTR2BSupport> invoceNoMisMatched2B = Lists.newArrayList();
			List<GSTR2BSupport> gstNoMisMatched2B = Lists.newArrayList();
			
			if (isMonthly) {
				Criteria criteria = Criteria.where("clientid").in(clientid)
						.and("fp").is(fp).and("invtype").is(invType).and("gstr2bMatchingStatus").nin(reconlist);
				Query query = new Query();
				query.addCriteria(criteria);
				query.fields().include("invoiceno");
				query.with(invPageable);
				List<GSTR2BSupport> gstr2bInvoiceList = mongoTemplate.find(query, GSTR2BSupport.class);
				if(isEmpty(gstr2bInvoiceList) || gstr2bInvoiceList.size() == 0) {
					hasMore = false;
					break;
				}
				for(GSTR2BSupport gstr2b : gstr2bInvoiceList) {
					gstr2bInvoiceNoList.add(gstr2b.getInvoiceno());
				}				
				if(gstr2bInvoiceNoList.size() < 10000) {
					hasMore = false;
				}
				
				gstr2bList = gstr2bSupportRepository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,fps, gstr2bInvoiceNoList, pageable);
				gstr2bContent.addAll(gstr2bList.getContent());
				while(isNotEmpty(gstr2bList) && gstr2bList.hasNext()) {
					gstr2bList = gstr2bSupportRepository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,fps, gstr2bInvoiceNoList, gstr2bList.nextPageable());
					gstr2bContent.addAll(gstr2bList.getContent());
				}
				List<String> matchedids = Lists.newArrayList();
				List<PurchaseRegister> matchedPR = Lists.newArrayList();
				List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> purchaseRegisterContent = Lists.newArrayList();
				purchaseRegisters = clientService.getPurchaseRegistersByGstr2bMatchingStatusInvoiceNos(clientid,invType, gstr2bInvoiceNoList, pageable);
				if(isEmpty(purchaseRegisters) || purchaseRegisters.getContent().size() == 0) {
					break;
				}
				purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				while(isNotEmpty(purchaseRegisters) && purchaseRegisters.hasNext()) {
					purchaseRegisters = clientService.getPurchaseRegistersByGstr2bMatchingStatusInvoiceNos(clientid,invType, gstr2bInvoiceNoList, purchaseRegisters.nextPageable());
					purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				}
				updateMismatchedStatusForPageable(purchaseRegisterContent, gstr2bContent, invType, clientConfig, clientid, matchedids, matchedPR, matched2B, invoceNoMisMatchedPR, invoceNoMisMatched2B, gstNoMisMatchedPR, gstNoMisMatched2B);
				
				//Step-1
				if(isNotEmpty(gstr2bContent)) {
					if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ainvoices())) {
						recon.setProcessedgstr2ainvoices(recon.getProcessedgstr2ainvoices()+gstr2bContent.size());
					}else {
						recon.setProcessedgstr2ainvoices(new Long(gstr2bContent.size()));
					}
					if(invType.equals(MasterGSTConstants.B2B)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ab2binvoices())) {
							recon.setProcessedgstr2ab2binvoices(recon.getProcessedgstr2ab2binvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2ab2binvoices(new Long(gstr2bContent.size()));
						}
					}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2acreditinvoices())) {
							recon.setProcessedgstr2acreditinvoices(recon.getProcessedgstr2acreditinvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2acreditinvoices(new Long(gstr2bContent.size()));
						}
					}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2aimpginvoices())) {
							recon.setProcessedgstr2aimpginvoices(recon.getProcessedgstr2aimpginvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2aimpginvoices(new Long(gstr2bContent.size()));
						}
					}
				}
				recon = reconcileTempRepository.save(recon);
				matchedPR.addAll(invoceNoMisMatchedPR);
				matchedPR.addAll(gstNoMisMatchedPR);
				saveBulkPR(matchedPR);
				matched2B.addAll(invoceNoMisMatched2B);
				matched2B.addAll(gstNoMisMatched2B);
				saveBulkGSTR2(matched2B);
				gstr2bContent.removeAll(matched2B);
				if(isNotEmpty(gstr2bContent)) {
					List<GSTR2BSupport> gstr2NotMatchedList = Lists.newArrayList();
					for (GSTR2BSupport gstr2 : gstr2bContent) {
						gstr2.setGstr2bMatchingStatus("Not In Purchases");
						gstr2NotMatchedList.add(gstr2);
					}
					saveBulkGSTR2(gstr2NotMatchedList);
				}
			}else {
				//yearly
				Criteria criteria = Criteria.where("clientid").in(clientid)
						.and("fp").in(rtarray).and("invtype").is(invType).and("gstr2bMatchingStatus").nin(reconlist);
				Query query = new Query();
				query.addCriteria(criteria);
				query.fields().include("invoiceno");
				query.with(invPageable);
				List<GSTR2BSupport> gstr2InvoiceList = mongoTemplate.find(query, GSTR2BSupport.class);
				if(isEmpty(gstr2InvoiceList) || gstr2InvoiceList.size() == 0) {
					hasMore = false;
					break;
				}
				for(GSTR2BSupport gstr2 : gstr2InvoiceList) {
					gstr2bInvoiceNoList.add(gstr2.getInvoiceno());
				}
				if(gstr2bInvoiceNoList.size() < 10000) {
					hasMore = false;
				}
				
				gstr2bList = gstr2bSupportRepository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,rtarray, gstr2bInvoiceNoList, pageable);
				gstr2bContent.addAll(gstr2bList.getContent());
				while(isNotEmpty(gstr2bList) && gstr2bList.hasNext()) {
					gstr2bList = gstr2bSupportRepository.findByClientidAndInvtypeAndFpInAndInvoicenoIn(clientid,invType,rtarray, gstr2bInvoiceNoList, gstr2bList.nextPageable());
					gstr2bContent.addAll(gstr2bList.getContent());
				}
				List<String> matchedids = Lists.newArrayList();
				List<PurchaseRegister> matchedPR = Lists.newArrayList();
				List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
				List<PurchaseRegister> purchaseRegisterContent = Lists.newArrayList();
				purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2bInvoiceNoList, pageable);
				if(isEmpty(purchaseRegisters) || purchaseRegisters.getContent().size() == 0) {
					break;
				}
				purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				while(isNotEmpty(purchaseRegisters) && purchaseRegisters.hasNext()) {
					purchaseRegisters = clientService.getPurchaseRegistersByInvoiceNos(clientid,invType, gstr2bInvoiceNoList, purchaseRegisters.nextPageable());
					purchaseRegisterContent.addAll(purchaseRegisters.getContent());
				}
				updateMismatchedStatusForPageable(purchaseRegisterContent, gstr2bContent, invType, clientConfig, clientid,matchedids,matchedPR,matched2B,invoceNoMisMatchedPR,invoceNoMisMatched2B,gstNoMisMatchedPR,gstNoMisMatched2B);
				if(isNotEmpty(gstr2bContent)) {
					if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ainvoices())) {
						recon.setProcessedgstr2ainvoices(recon.getProcessedgstr2ainvoices()+gstr2bContent.size());
					}else {
						recon.setProcessedgstr2ainvoices(new Long(gstr2bContent.size()));
					}
					if(invType.equals(MasterGSTConstants.B2B)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ab2binvoices())) {
							recon.setProcessedgstr2ab2binvoices(recon.getProcessedgstr2ab2binvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2ab2binvoices(new Long(gstr2bContent.size()));
						}
					}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2acreditinvoices())) {
							recon.setProcessedgstr2acreditinvoices(recon.getProcessedgstr2acreditinvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2acreditinvoices(new Long(gstr2bContent.size()));
						}
					}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2aimpginvoices())) {
							recon.setProcessedgstr2aimpginvoices(recon.getProcessedgstr2aimpginvoices()+gstr2bContent.size());
						}else {
							recon.setProcessedgstr2aimpginvoices(new Long(gstr2bContent.size()));
						}
					}
				}
				recon = reconcileTempRepository.save(recon);
				matchedPR.addAll(invoceNoMisMatchedPR);
				matchedPR.addAll(gstNoMisMatchedPR);
				saveBulkPR(matchedPR);
				matched2B.addAll(invoceNoMisMatched2B);
				matched2B.addAll(gstNoMisMatched2B);
				saveBulkGSTR2(matched2B);
				gstr2bContent.removeAll(matched2B);
				if(isNotEmpty(gstr2bContent)) {
					List<GSTR2BSupport> gstr2NotMatchedList = Lists.newArrayList();
					for (GSTR2BSupport gstr2b : gstr2bContent) {
							gstr2b.setGstr2bMatchingStatus("Not In Purchases");
							gstr2NotMatchedList.add(gstr2b);
					}
					saveBulkGSTR2(gstr2NotMatchedList);
				}
				
			}
		}
		
		reconcileMissingInvoices(clientid, invType, fp, clientConfig, isBilledDate,recon, isMonthly);
		logger.debug(CLASSNAME + "updateMismatchStatus : End");
	}
	
	
	private void reconcileMissingInvoices(String clientId, final String invType, final String fp, ClientConfig clientConfig, boolean isBilledDate, ReconcileTemp recon, final boolean isMonthly) {
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
		
		if(isMonthly) {
			
			notinpurchases = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2bInvoicescount(clientId, invType, strMonth, yrcd,isBilledDate);
		}else {
			notinpurchases = purchageRegisterDao.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndNotInGstr2bInvoicescount(clientId, invType, ystDate, yendDate,isBilledDate);
		}
		if(isNotEmpty(recon) && isNotEmpty(recon.getTotalpurchaseinvoices())) {
			recon.setTotalpurchaseinvoices(recon.getTotalpurchaseinvoices()+notinpurchases);
		}else {
			recon.setTotalpurchaseinvoices(notinpurchases);
		}
		recon = reconcileTempRepository.save(recon);
		reconcileMissingPageable(clientId, invType, ystDate, yendDate, clientConfig, isBilledDate, isMonthly, strMonth, yrcd);	
	}
	
	private void reconcileMissingPageable(String clientid, final String invType, Date ystDate, Date yendDate,ClientConfig clientConfig ,boolean billdate,final boolean isMonthly, String mthcd, String yrcd) {
		final String method = "reconcileMissingPageable ::";
		//logger.info(CLASSNAME + " reconcileMissingPageable method calling...");

		List<String> invTypes = new ArrayList<String>();
		invTypes.add(invType);
		List<String> statusIsNull = Lists.newArrayList("", null);
		Calendar calendar = Calendar.getInstance();		
		int defaultSize = 5000;
		
		Page<? extends InvoiceParent> notInGstr2Binvoices = null;
		boolean hasMore = true;
		while(hasMore) {
			List<PurchaseRegister> notInGstr2bList = Lists.newArrayList();
			
			Pageable pageable = new PageRequest(0, defaultSize, Sort.Direction.ASC, "dateofinvoice");
			List<PurchaseRegister> matchedPR = Lists.newArrayList();
			List<PurchaseRegister> invoceNoMisMatchedPR = Lists.newArrayList();
			List<PurchaseRegister> gstNoMisMatchedPR = Lists.newArrayList();
			if(isMonthly) {
				if(billdate) {
					notInGstr2Binvoices = purchaseRepository.findByClientidAndInvtypeInAndMthCdAndYrCdAndGstr2bMatchingStatusIn(clientid, invTypes, mthcd, yrcd, statusIsNull, pageable);
				} else {
					notInGstr2Binvoices = purchaseRepository.findByClientidAndInvtypeInAndTrDatemthCdAndTrDateyrCdAndGstr2bMatchingStatusIn(clientid, invTypes, mthcd, yrcd, statusIsNull, pageable);
				}
			}else {
				notInGstr2Binvoices = purchaseRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIn(clientid, invTypes, ystDate, yendDate, statusIsNull, pageable);
			}
			if(isEmpty(notInGstr2Binvoices) || isEmpty(notInGstr2Binvoices.getContent()) || notInGstr2Binvoices.getContent().size() == 0) {
				hasMore = false;
				break;
			}
			if(notInGstr2Binvoices.getContent().size() < defaultSize) {
				hasMore = false;
			}
			//logger.info(CLASSNAME + method + "PurchaseRegister Content="+notInGstr2Ainvoices.getContent().size() +" Batch No ::"+i);
			notInGstr2bList.addAll((Collection<? extends PurchaseRegister>) notInGstr2Binvoices.getContent());
			List<String> matchedids = Lists.newArrayList();
			List<GSTR2BSupport> matched2B = Lists.newArrayList();
			List<GSTR2BSupport> invoceNoMisMatched2B = Lists.newArrayList();
			List<GSTR2BSupport> gstNoMisMatched2B = Lists.newArrayList();
			List<String> gstr2status = Arrays.asList("", null, "Not In Purchases");
			Date pstdate = null;
			Date penddate = null;	
			if(isNotEmpty(notInGstr2bList)  && isNotEmpty(notInGstr2bList.get(0)) && isNotEmpty(notInGstr2bList.get(0).getDateofinvoice())) {
					calendar = Calendar.getInstance();
					Date stdate = notInGstr2bList.get(0).getDateofinvoice();
					calendar.set(stdate.getYear()+1900, stdate.getMonth(), stdate.getDate()-1, 0, 0, 0);
					pstdate = new java.util.Date(calendar.getTimeInMillis());
				}
			if(isNotEmpty(notInGstr2bList)  && isNotEmpty(notInGstr2bList.get(notInGstr2bList.size()-1)) && isNotEmpty(notInGstr2bList.get(notInGstr2bList.size()-1).getDateofinvoice())) {
					calendar = Calendar.getInstance();
					Date enddate = notInGstr2bList.get(notInGstr2bList.size()-1).getDateofinvoice();
					calendar.set(enddate.getYear()+1900, enddate.getMonth(), enddate.getDate(), 23, 59, 59);
					penddate = new java.util.Date(calendar.getTimeInMillis());
				}
			
			
			List<GSTR2BSupport> gstr2bInvoiceList = Lists.newArrayList();
			Page<? extends InvoiceParent> gstr2bInvoices = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIn(clientid, invTypes, pstdate, yendDate, gstr2status, pageable);
			if(isEmpty(gstr2bInvoices) || gstr2bInvoices.getContent().size() == 0) {
				break;
			}
			gstr2bInvoiceList.addAll((List<GSTR2BSupport>) gstr2bInvoices.getContent());
			while(isNotEmpty(gstr2bInvoices) && gstr2bInvoices.hasNext()) {
				gstr2bInvoices = gstr2bSupportRepository.findByClientidAndInvtypeInAndDateofinvoiceBetweenAndGstr2bMatchingStatusIn(clientid,invTypes, pstdate, penddate, gstr2status, gstr2bInvoices.nextPageable());
				gstr2bInvoiceList.addAll((List<GSTR2BSupport>) gstr2bInvoices.getContent());
			}
			reconcileRemainingRecords(clientid, invType, clientConfig, notInGstr2bList, gstr2bInvoiceList,matchedids,matchedPR,matched2B,invoceNoMisMatchedPR,invoceNoMisMatched2B,gstNoMisMatchedPR,gstNoMisMatched2B);
			ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
			if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseinvoices())) {
				recon.setProcessedpurchaseinvoices(recon.getProcessedpurchaseinvoices()+notInGstr2bList.size());
			}else {
				recon.setProcessedpurchaseinvoices(new Long(notInGstr2bList.size()));
			}
			if(invType.equals(MasterGSTConstants.B2B)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseb2binvoices())) {
					recon.setProcessedpurchaseb2binvoices(recon.getProcessedpurchaseb2binvoices()+notInGstr2bList.size());
				}else {
					recon.setProcessedpurchaseb2binvoices(new Long(notInGstr2bList.size()));
				}
			}else if(invType.equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchasecreditinvoices())) {
					recon.setProcessedpurchasecreditinvoices(recon.getProcessedpurchasecreditinvoices()+notInGstr2bList.size());
				}else {
					recon.setProcessedpurchasecreditinvoices(new Long(notInGstr2bList.size()));
				}
			}else if(invType.equals(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedpurchaseimpginvoices())) {
					recon.setProcessedpurchaseimpginvoices(recon.getProcessedpurchaseimpginvoices()+notInGstr2bList.size());
				}else {
					recon.setProcessedpurchaseimpginvoices(new Long(notInGstr2bList.size()));
				}
			}
			recon = reconcileTempRepository.save(recon);
			matchedPR.addAll(invoceNoMisMatchedPR);
			matchedPR.addAll(gstNoMisMatchedPR);
			saveBulkPR(matchedPR);
			matched2B.addAll(invoceNoMisMatched2B);
			matched2B.addAll(gstNoMisMatched2B);
			saveBulkGSTR2(matched2B);
			notInGstr2bList.removeAll(matchedPR);

			if(isNotEmpty(notInGstr2bList)) {
				List<PurchaseRegister> gstr2NotMatchedList = Lists.newArrayList();
				for (PurchaseRegister gstr2 : notInGstr2bList) {
						gstr2.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_NOTINGSTR2B);
						gstr2NotMatchedList.add(gstr2);
				}
				if(isNotEmpty(gstr2NotMatchedList)) {
					saveBulkPR(gstr2NotMatchedList);
				}
			}
			
		}
		
	}

	private void reconcileRemainingRecords(String clientId, final String invType, ClientConfig clientConfig, List<PurchaseRegister> notingstr2b, List<GSTR2BSupport> gstr2bInvoic,List<String> matchedids,List<PurchaseRegister> matchedPR, List<GSTR2BSupport> matched2B,List<PurchaseRegister> invoiceNoMisMatchedPR, List<GSTR2BSupport> invoiceNoMisMatched2B,List<PurchaseRegister> gstNoMisMatchedPR, List<GSTR2BSupport> gstNoMisMatched2B) {
		final String method = "reconcileRemainingRecords";
		logger.debug(CLASSNAME + method + "GSTR2 Size="+notingstr2b.size());
		logger.debug(CLASSNAME + method + "GSTR2 Size="+gstr2bInvoic.size());
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
		List<PurchaseRegister> savePPRProbableList = Lists.newArrayList();
		
		if (isNotEmpty(notingstr2b)) {
			for (PurchaseRegister purchaseRegister : notingstr2b) {
				if ((isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || (isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && !purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) && !matchedids.contains(purchaseRegister.getId().toString())) {
				boolean mstatus = false;
				List<GSTR2BSupport> savePRGList = Lists.newArrayList();
				List<GSTR2BSupport> savePPRRList = Lists.newArrayList();
				List<GSTR2BSupport> savePRGINVNOList = Lists.newArrayList();
				List<GSTR2BSupport> savePRGINVDATEList = Lists.newArrayList();
				List<GSTR2BSupport> savePRGGSTNOList = Lists.newArrayList();
				List<GSTR2BSupport> savePRGTAXList = Lists.newArrayList();
				List<GSTR2BSupport> savePRGINVVALUEList = Lists.newArrayList();
				List<GSTR2BSupport> savePRPList = Lists.newArrayList();
				List<PurchaseRegister> savePPRGList = Lists.newArrayList();
				//logger.info(CLASSNAME + " reconcileRemainingRecords purchaseRegister macting status checking");
				for (GSTR2BSupport gstr2b : gstr2bInvoic) {
					if (!mstatus) {
						if (invType.equals(B2B) && isNotEmpty(purchaseRegister.getInvtype())
								&& purchaseRegister.getInvtype().equals(invType)) {
								if (isNotEmpty(purchaseRegister.getB2b())) {
									for (GSTRB2B gstrb2b : purchaseRegister.getB2b()) {
										for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
											if (isNotEmpty(gstrInvoiceDetails.getInum()) && isNotEmpty(gstrInvoiceDetails.getIdt())) {
												if (isNotEmpty(gstr2b.getB2b()) && isNotEmpty(gstr2b.getB2b().get(0).getCtin())
													&& isNotEmpty(gstr2b.getB2b().get(0).getInv())
													&& isNotEmpty(gstr2b.getB2b().get(0).getInv().get(0).getInum())
													&& isNotEmpty(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = gstrInvoiceDetails.getIdt();
													String dateAfterString = gstr2b.getB2b().get(0).getInv().get(0).getIdt();
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
													String purchaseregisterInvoiceNo = gstr2b.getB2b().get(0).getInv().get(0).getInum().trim();
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
												if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
													&& (gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
													if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
																|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add(gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))
														&& (gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))) {
														if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
															gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
														}
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotalamount())&& isNotEmpty(gstr2b.getTotalamount())
															&& ((((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
															&& (purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) >= 0) || (((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
															&& (gstr2b.getTotalamount()- purchaseRegister.getTotalamount()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
																|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
																|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))
																&& (((purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) == 0) 
																|| ((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) == 0))) {
																if (gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add(gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
																|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0))) {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add(gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2b.getB2b().get(0).getInv().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
															}
													}
											} else if ((gstrb2b.getCtin().trim()).equals((gstr2b.getB2b().get(0).getCtin().trim()))
														&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
													if(isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0))) {
															if(ignoreInvoiceMatch) {
																List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getInum().trim()));
																List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()));
																if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRPList.add((GSTR2BSupport) gstr2b);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(purchaseRegister.getId().toString());
																			matched2B.add((GSTR2BSupport)gstr2b);
																			matchedPR.add(purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																				invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																				gstNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																				gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																			}
																			savePPRRList.add((GSTR2BSupport) gstr2b);
																			mstatus = true;
																		}
																	}
																}else {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				savePRPList.add((GSTR2BSupport) gstr2b);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2B.add((GSTR2BSupport)gstr2b);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				savePPRRList.add((GSTR2BSupport) gstr2b);
																				mstatus = true;
																			}
																		}
																	}else {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((GSTR2BSupport) gstr2b);
																				invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																				invoiceNoMisMatchedPR.add(purchaseRegister);
																			}
																		}
																	}
																}
															}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		savePRPList.add((GSTR2BSupport) gstr2b);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2B.add((GSTR2BSupport)gstr2b);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		savePPRRList.add((GSTR2BSupport) gstr2b);
																		mstatus = true;
																	}
																}
															}else {
																
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRGINVNOList.size() < 1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																		savePRGINVNOList.add((GSTR2BSupport) gstr2b);
																		invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																		invoiceNoMisMatchedPR.add(purchaseRegister);
																	}
																}
															}
													}
												} else if ((gstrInvoiceDetails.getInum().trim()).equals((gstr2b.getB2b().get(0).getInv().get(0).getInum().trim()))&& gstrInvoiceDetails.getIdt().equals(gstr2b.getB2b().get(0).getInv().get(0).getIdt())) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if (isNotEmpty(gstr2b.getB2b().get(0).getCfs())) {
														gstrb2b.setCfs(gstr2b.getB2b().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0))) {
														if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add(gstr2b);
																gstNoMisMatched2B.add((GSTR2BSupport)gstr2b);
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
												if (isNotEmpty(gstr2b.getCdn()) && isNotEmpty(gstr2b.getCdn().get(0)) && isNotEmpty(gstr2b.getCdn().get(0).getCtin())
													&& isNotEmpty(gstr2b.getCdn().get(0).getNt())
													&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getNtDt())
													&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getNtNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrInvoiceDetails.getNtDt());
													String dateAfterString = myFormat.format(gstr2b.getCdn().get(0).getNt().get(0).getNtDt());
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
													String purchaseregisterInvoiceNo = gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim();
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
												if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
													&& (gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
														&& daysBetweenInvoiceDate <= allowedDays
														&& gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
													if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
													}
													List<Double> pTxValues = Lists.newArrayList();
													if (isNotEmpty(gstrInvoiceDetails.getItms())&& isNotEmpty(gstr2b.getCdn().get(0).getNt().get(0).getItms())) {
														for (GSTRItems gstrItem : gstr2b.getCdn().get(0).getNt().get(0).getItms()) {
															pTxValues.add(gstrItem.getItem().getTxval());
														}
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
																|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
																|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (dateBeforeString.equals(dateAfterString)) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()))
														&& (gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))) {
													if (isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
													}
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
															&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
															|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
															&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
															&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
															&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
															|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
															&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
																|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)
																|| ((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotalamount())&& isNotEmpty(gstr2b.getTotalamount())
																		&& (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) == 0)
																		|| ((gstr2b.getTotalamount()- purchaseRegister.getTotalamount()) == 0)))) {
																if (dateBeforeString.equals(dateAfterString)) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
																|| (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
																&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
																|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
															
															if(gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrInvoiceDetails.getVal().equals(gstr2b.getCdn().get(0).getNt().get(0).getVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													}
												} else if ((gstrcdn.getCtin().trim()).equals((gstr2b.getCdn().get(0).getCtin().trim()))
														&& dateBeforeString.equals(dateAfterString)) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if(isNotEmpty(gstr2b.getCdn().get(0).getCfs())) {
														gstrcdn.setCfs(gstr2b.getCdn().get(0).getCfs());
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
														if(ignoreInvoiceMatch) {
														List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstrInvoiceDetails.getNtNum().trim()));
														List<Character> purinvd = convertStringToCharList(removeLeadingZeros(gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim()));
															if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		savePRPList.add((GSTR2BSupport) gstr2b);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2B.add((GSTR2BSupport)gstr2b);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		savePPRRList.add((GSTR2BSupport) gstr2b);
																		mstatus = true;
																	}
																}
															}else {
																if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			savePRPList.add((GSTR2BSupport) gstr2b);
																			savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(purchaseRegister.getId().toString());
																			matched2B.add((GSTR2BSupport)gstr2b);
																			matchedPR.add(purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																				invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																				gstNoMisMatchedPR.remove(purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																				gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																			}
																			savePPRRList.add((GSTR2BSupport) gstr2b);
																			mstatus = true;
																		}
																	}
																}else {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((GSTR2BSupport) gstr2b);
																			invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																			invoiceNoMisMatchedPR.add(purchaseRegister);
																		}
																	}
																}
															}
														}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																if(isNotEmpty(savePRPList) && savePRPList.size() < 1) {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	savePRPList.add((GSTR2BSupport) gstr2b);
																	savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}else {
															if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
															}else {
																if(savePRGINVNOList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																	savePRGINVNOList.add((GSTR2BSupport) gstr2b);
																	invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																	invoiceNoMisMatchedPR.add(purchaseRegister);
																}
															}
														}
													}
												} else if ((gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((gstr2b.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
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
													if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
														&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
														&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
														&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
															
														if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add((GSTR2BSupport) gstr2b);
																gstNoMisMatched2B.add((GSTR2BSupport)gstr2b);
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
												if (isNotEmpty(gstr2b.getImpGoods()) && isNotEmpty(gstr2b.getImpGoods().get(0).getBoeNum())) {
													SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
													String dateBeforeString = myFormat.format(gstrimpg.getBoeDt());
													String dateAfterString = myFormat.format(gstr2b.getImpGoods().get(0).getBoeDt());
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
													String purchaseregisterInvoiceNo = (gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim();
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
												 if(isEmpty(gstr2b) || isEmpty(gstr2b.getImpGoods()) || isEmpty(gstr2b.getImpGoods().get(0)) || isEmpty(gstr2b.getImpGoods().get(0).getStin())) {
													 gstr2b.getImpGoods().get(0).setStin(" ");
												 }
												
												
												if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
													&& ((gstrimpg.getBoeNum().toString()).trim()).equals(((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()))
													&& daysBetweenInvoiceDate <= allowedDays
													&& gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
																|| ((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))) {
																if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																}else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															}else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															if(gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())
														&& (gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin()).trim())){
													if (daysBetweenInvoiceDate <= allowedDays) {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
															&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
															|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
															&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))) {
															if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)
																|| ((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax())
																&& (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) == 0) 
																|| ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)))
																&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
																	if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																	}
																} else {
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
															} else {
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
															}
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															savePPRRList.add((GSTR2BSupport) gstr2b);
															matchedids.add(purchaseRegister.getId().toString());
															matched2B.add((GSTR2BSupport)gstr2b);
															matchedPR.add(purchaseRegister);
															if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																invoiceNoMisMatchedPR.remove(purchaseRegister);
															}
															if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																gstNoMisMatchedPR.remove(purchaseRegister);
															}
															if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
															}
															mstatus = true;
														} else {
															if(gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
																if(savePRGTAXList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																	savePRGTAXList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													}else {
														if ((isNotEmpty(purchaseRegister.getTotaltaxableamount()) && isNotEmpty(gstr2b.getTotaltaxableamount())
																&& ((((purchaseRegister.getTotaltaxableamount()	- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)
																|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0))) 
																&& (isNotEmpty(purchaseRegister.getTotaltax()) && isNotEmpty(gstr2b.getTotaltax()) && ((((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)	|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																
															if(gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVDATEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																	savePRGINVDATEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else if(!gstrimpg.getBoeVal().equals(gstr2b.getImpGoods().get(0).getBoeVal())) {
																if(savePRGINVVALUEList.size() < 1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																	savePRGINVVALUEList.add((GSTR2BSupport) gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}else {
																if(savePRGList.size()<1) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																	savePPRGList.add((PurchaseRegister) purchaseRegister);
																	savePRGList.add((GSTR2BSupport)gstr2b);
																	matchedids.add(purchaseRegister.getId().toString());
																	matched2B.add((GSTR2BSupport)gstr2b);
																	matchedPR.add(purchaseRegister);
																	if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																		gstNoMisMatchedPR.remove(purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																		gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																	}
																	savePPRRList.add((GSTR2BSupport) gstr2b);
																	mstatus = true;
																}
															}
														}
													}
											} else if ((gstrimpg.getStin().trim()).equals((gstr2b.getImpGoods().get(0).getStin().trim()))
														&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
												Double alldDiff = 0d;
												if (allowedDiff == 0d) {
													alldDiff = 1d;
												} else {
													alldDiff = allowedDiff;
												}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
															if(ignoreInvoiceMatch) {
																List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros((gstrimpg.getBoeNum().toString()).trim()));
																List<Character> purinvd = convertStringToCharList(removeLeadingZeros((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim()));
																if (purinvd.containsAll(gstrinvd)|| gstrinvd.containsAll(purinvd)) {
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRPList.add((GSTR2BSupport) gstr2b);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2B.add((GSTR2BSupport)gstr2b);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				savePPRRList.add((GSTR2BSupport) gstr2b);
																				mstatus = true;
																			}
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					savePRPList.add((GSTR2BSupport) gstr2b);
																					savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(purchaseRegister.getId().toString());
																					matched2B.add((GSTR2BSupport)gstr2b);
																					matchedPR.add(purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove(purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																						invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																						gstNoMisMatchedPR.remove(purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																						gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																					}
																					savePPRRList.add((GSTR2BSupport) gstr2b);
																					mstatus = true;
																				}
																			}
																		}else {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((GSTR2BSupport) gstr2b);
																					invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																					invoiceNoMisMatchedPR.add(purchaseRegister);
																				}
																			}
																		}
																	}
																}else {
																	
																	if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {	
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRPList.add((GSTR2BSupport) gstr2b);
																				savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(purchaseRegister.getId().toString());
																				matched2B.add((GSTR2BSupport)gstr2b);
																				matchedPR.add(purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																					gstNoMisMatchedPR.remove(purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																					gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																				}
																				savePPRRList.add((GSTR2BSupport) gstr2b);
																				mstatus = true;
																			}
																		}
																	}else{
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((GSTR2BSupport)gstr2b);
																				invoiceNoMisMatched2B.add((GSTR2BSupport) gstr2b);
																				invoiceNoMisMatchedPR.add(purchaseRegister);
																			}
																		}
																	}
																}
															}else if(gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRPList.size() < 1) {
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		savePRPList.add((GSTR2BSupport) gstr2b);
																		savePPRProbableList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2B.add((GSTR2BSupport)gstr2b);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		savePPRRList.add((GSTR2BSupport) gstr2b);
																		mstatus = true;
																	}
																}
															}else {
																if (isEmpty(gstr2b.getGstr2bMatchingStatus()) || gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MISMATCHED)) {
																	if(savePRGList.size()<1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePPRGList.add((PurchaseRegister) purchaseRegister);
																		savePRGList.add((GSTR2BSupport)gstr2b);
																		matchedids.add(purchaseRegister.getId().toString());
																		matched2B.add((GSTR2BSupport)gstr2b);
																		matchedPR.add(purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains(purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			invoiceNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains(purchaseRegister)) {
																			gstNoMisMatchedPR.remove(purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains((GSTR2BSupport)gstr2b)) {
																			gstNoMisMatched2B.remove((GSTR2BSupport)gstr2b);
																		}
																		savePPRRList.add((GSTR2BSupport) gstr2b);
																		mstatus = true;
																	}
																}
															}
													}
												} else if (((gstrimpg.getBoeNum().toString()).trim()).equals((gstr2b.getImpGoods().get(0).getBoeNum().toString()).trim())&& myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(gstr2b.getDateofinvoice()))) {
													Double alldDiff = 0d;
													if (allowedDiff == 0d) {
														alldDiff = 1d;
													} else {
														alldDiff = allowedDiff;
													}
													if ((isNotEmpty(purchaseRegister.getTotaltaxableamount())&& isNotEmpty(gstr2b.getTotaltaxableamount())
														&& ((((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
														&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)
														|| (((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
														&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)))
														&& (isNotEmpty(purchaseRegister.getTotaltax())&& isNotEmpty(gstr2b.getTotaltax())
														&& ((((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
														&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)
														|| (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
														&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)))
														&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
														
														if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
														}else {
															if(savePRGGSTNOList.size() < 1) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																savePRGGSTNOList.add((GSTR2BSupport) gstr2b);
																gstNoMisMatched2B.add((GSTR2BSupport)gstr2b);
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
						gstr2bInvoic.removeAll(savePPRRList);
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
	
	void saveBulkGSTR2(List<GSTR2BSupport> saveGSTRList) {
		int batchCount = 2000;
		if(saveGSTRList.size() > batchCount) {
			int index = 0;
			while((saveGSTRList.size()-index) > batchCount) {
				List<GSTR2BSupport> subList = saveGSTRList.subList(index, index+batchCount);
				gstr2bSupportRepository.save(subList);
				index=index+batchCount;
			}
			gstr2bSupportRepository.save(saveGSTRList.subList(index, saveGSTRList.size()));
		} else {
			gstr2bSupportRepository.save(saveGSTRList);
		}
	}
	
	
	private void updateMismatchedStatusForPageable(List<PurchaseRegister> prr, List<GSTR2BSupport> gstr2bList, final String invType, ClientConfig clientConfig, String clientid,List<String> matchedids,List<PurchaseRegister> matchedPR, List<GSTR2BSupport> matched2B,List<PurchaseRegister> invoiceNoMisMatchedPR, List<GSTR2BSupport> invoiceNoMisMatched2B, List<PurchaseRegister> gstNoMisMatchedPR, List<GSTR2BSupport> gstNoMisMatched2B) {
		final String method = "updateMismatchedStatusForPageable ::";
		logger.debug(CLASSNAME + method + "Purchase Content Size="+prr.size());
		logger.debug(CLASSNAME + method + "GSTR2B Size="+gstr2bList.size());
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
		List<PurchaseRegister> savePRProbableList = Lists.newArrayList();
		if (isNotEmpty(gstr2bList)) {
			for (GSTR2BSupport gstr2b : gstr2bList) {
				if ((isEmpty(gstr2b.getGstr2bMatchingStatus()) || (isNotEmpty(gstr2b.getGstr2bMatchingStatus()) && !gstr2b.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) && !matchedids.contains(gstr2b.getId().toString())) {
					boolean mstatus = false;
					List<PurchaseRegister> savePRGList = Lists.newArrayList();
					List<PurchaseRegister> savePRRList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVDATEList = Lists.newArrayList();
					List<PurchaseRegister> savePRGGSTNOList = Lists.newArrayList();
					List<PurchaseRegister> savePRGTAXList = Lists.newArrayList();
					List<PurchaseRegister> savePRGINVVALUEList = Lists.newArrayList();
					List<PurchaseRegister> savePRPList = Lists.newArrayList();
					
					for (PurchaseRegister purchaseRegister : purchaseRegisters) {
						if(!mstatus) {
						if (isEmpty(purchaseRegister.getGstr2bMatchingStatus()) || (isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && !purchaseRegister.getGstr2bMatchingStatus().equals(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED))) {
							if (invType.equals(B2B) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getB2b())) {
									for (GSTRB2B gstrb2b : gstr2b.getB2b()) {
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
														if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
															&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
															&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
															|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
															&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
															&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
															&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
															&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
															|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
															&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																	|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																	|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																		if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																}else {
																	purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																}
																matchedids.add(gstr2b.getId().toString());
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matched2B.add(gstr2b);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2B.contains(gstr2b)) {
																	invoiceNoMisMatched2B.remove(gstr2b);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2B.contains(gstr2b)) {
																	gstNoMisMatched2B.remove(gstr2b);
																}
																mstatus = true;
															} else {
																if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																	if(savePRGTAXList.size() < 1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																		savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(gstr2b.getId().toString());
																		matched2B.add(gstr2b);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains(gstr2b)) {
																			invoiceNoMisMatched2B.remove(gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains(gstr2b)) {
																			gstNoMisMatched2B.remove(gstr2b);
																		}
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		mstatus = true;
																	}
																}else {
																	if(savePRGList.size() < 1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																		savePRGList.add((PurchaseRegister) purchaseRegister);
																		matchedids.add(gstr2b.getId().toString());
																		matched2B.add(gstr2b);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains(gstr2b)) {
																			invoiceNoMisMatched2B.remove(gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains(gstr2b)) {
																			gstNoMisMatched2B.remove(gstr2b);
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
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																				&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																				|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																				&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																				&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																				|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))
																				&& (((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																				|| ((purchaseRegister.getTotalamount()	- gstr2b.getTotalamount()) == 0))) {
																					if (gstrInvoiceDetails.getIdt().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getIdt())) {
																						if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			}
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		} else {
																			if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																				if(savePRGTAXList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																					savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}else if(((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0) && ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0) && !gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																				if(savePRGINVVALUEList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																					savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}else {
																				if(savePRGList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																					savePRGList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}
																		}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																		if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																			if(savePRGINVDATEList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}else if(!gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																			if(savePRGINVVALUEList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																	&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																	&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																	|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																	&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0))) {
																	if (ignoreInvoiceMatch) {
																		List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																		List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																		if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo) || purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																				}else {
																					if(savePRPList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						savePRPList.add((PurchaseRegister) purchaseRegister);
																						savePRProbableList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2b.getId().toString());
																						matched2B.add(gstr2b);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2B.contains(gstr2b)) {
																							invoiceNoMisMatched2B.remove(gstr2b);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2B.contains(gstr2b)) {
																							gstNoMisMatched2B.remove(gstr2b);
																						}
																						mstatus = true;
																					}
																				}
																			}else {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						if(savePRPList.size() < 1) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							savePRPList.add((PurchaseRegister) purchaseRegister);
																							savePRProbableList.add((PurchaseRegister) purchaseRegister);
																							savePRRList.add((PurchaseRegister) purchaseRegister);
																							matchedids.add(gstr2b.getId().toString());
																							matched2B.add(gstr2b);
																							matchedPR.add((PurchaseRegister) purchaseRegister);
																							if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(invoiceNoMisMatched2B.contains(gstr2b)) {
																								invoiceNoMisMatched2B.remove(gstr2b);
																							}
																							if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(gstNoMisMatched2B.contains(gstr2b)) {
																								gstNoMisMatched2B.remove(gstr2b);
																							}
																							mstatus = true;
																						}
																					}	
																				}else {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						if(savePRGINVNOList.size() < 1) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																							invoiceNoMisMatched2B.add(gstr2b);
																							invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																						}
																					}
																				}
																			}
																		} else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}	
																		}else{
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					invoiceNoMisMatched2B.add(gstr2b);
																					invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRPList.add((PurchaseRegister) purchaseRegister);
																			savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}
																} else {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			invoiceNoMisMatched2B.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0))) {
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRGGSTNOList.size() < 1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																		gstNoMisMatched2B.add(gstr2b);
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
							} else if (invType.equals(CREDIT_DEBIT_NOTES) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getCdn())) {
									for (GSTRCreditDebitNotes gstrcdn : gstr2b.getCdn()) {
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
													if ((gstrcdn.getCtin().trim()).equals((purchaseRegister.getCdn().get(0).getCtin().trim()))
															&& (gstrInvoiceDetails.getNtNum().trim().toLowerCase()).equals((purchaseRegister.getCdn().get(0).getNt().get(0).getNtNum().trim().toLowerCase()))
															&& daysBetweenInvoiceDate <= allowedDays
															&& gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
														if(isNotEmpty(gstrcdn.getCfs())) {
															purchaseRegister.getCdn().get(0).setCfs(gstrcdn.getCfs());
														}
													if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
														 && ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
														 && (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
														 || (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
														 && (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
														 && (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
														 && ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
														 && (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
														 || (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
														 && (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
														 && (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
														&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
														&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
														|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
														&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
															purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))) {
																	if (dateBeforeString.equals(dateAfterString)) {
																		if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																		}
																	}else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
															}else {
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
														}
														savePRRList.add((PurchaseRegister) purchaseRegister);
														matchedids.add(gstr2b.getId().toString());
														matched2B.add(gstr2b);
														matchedPR.add((PurchaseRegister) purchaseRegister);
														if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
															invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
														}
														if(invoiceNoMisMatched2B.contains(gstr2b)) {
															invoiceNoMisMatched2B.remove(gstr2b);
														}
														if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
															gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
														}
														if(gstNoMisMatched2B.contains(gstr2b)) {
															gstNoMisMatched2B.remove(gstr2b);
														}
														mstatus = true;
													} else {
														if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
															if(savePRGTAXList.size() < 1) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matchedids.add(gstr2b.getId().toString());
																matched2B.add(gstr2b);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2B.contains(gstr2b)) {
																	invoiceNoMisMatched2B.remove(gstr2b);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2B.contains(gstr2b)) {
																	gstNoMisMatched2B.remove(gstr2b);
																}
																mstatus = true;
															}
														}else {
															if(savePRGList.size() < 1) {
																purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																savePRGList.add((PurchaseRegister) purchaseRegister);
																savePRRList.add((PurchaseRegister) purchaseRegister);
																matchedids.add(gstr2b.getId().toString());
																matched2B.add(gstr2b);
																matchedPR.add((PurchaseRegister) purchaseRegister);
																if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(invoiceNoMisMatched2B.contains(gstr2b)) {
																	invoiceNoMisMatched2B.remove(gstr2b);
																}
																if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																	gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																}
																if(gstNoMisMatched2B.contains(gstr2b)) {
																	gstNoMisMatched2B.remove(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																	purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																		|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& (((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) == 0)
																		|| ((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) == 0)))
																		&& (((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																		|| ((purchaseRegister.getTotalamount()	- gstr2b.getTotalamount()) == 0))) {
																		if (dateBeforeString.equals(dateAfterString)) {
																			if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																				mstatus = true;
																			}else {
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																			}
																		} else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																	} else {
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																	}
																	savePRRList.add((PurchaseRegister) purchaseRegister);
																	matchedids.add(gstr2b.getId().toString());
																	if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																		invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																	}
																	if(invoiceNoMisMatched2B.contains(gstr2b)) {
																		invoiceNoMisMatched2B.remove(gstr2b);
																	}
																	if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																		gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																	}
																	if(gstNoMisMatched2B.contains(gstr2b)) {
																		gstNoMisMatched2B.remove(gstr2b);
																	}
																	mstatus = true;
																} else {
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																		if(savePRGTAXList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																			savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}else if(((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) == 0) && ((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0) &&!gstrInvoiceDetails.getVal().equals(purchaseRegister.getCdn().get(0).getNt().get(0).getVal())) {
																		if(savePRGINVVALUEList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}else {
																		if(savePRGList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}
																}
															} else {
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																		&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) <= allowedDiff)
																		&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																		&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																		&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																		&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																		|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																		&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																		
																	if(gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																		if(savePRGINVDATEList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																			savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}else if(!gstrInvoiceDetails.getVal().equals(purchaseRegister.getB2b().get(0).getInv().get(0).getVal())) {
																		if(savePRGINVVALUEList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																			savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}else {
																		if(savePRGList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																			savePRGList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																	&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																	&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																	|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																	&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																if(ignoreInvoiceMatch) {
																	List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																	List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																	if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}
																		}else {
																			
																			if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																				if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																				}else {
																					if(savePRPList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																						savePRPList.add((PurchaseRegister) purchaseRegister);
																						savePRProbableList.add((PurchaseRegister) purchaseRegister);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2b.getId().toString());
																						matched2B.add(gstr2b);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2B.contains(gstr2b)) {
																							invoiceNoMisMatched2B.remove(gstr2b);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2B.contains(gstr2b)) {
																							gstNoMisMatched2B.remove(gstr2b);
																						}
																						mstatus = true;
																					}
																				}	
																			}else {
																				if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																				}else {
																					if(savePRGINVNOList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																						savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																						invoiceNoMisMatched2B.add(gstr2b);
																						invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																					}
																				}
																			}
																		}
																	}else {
																		if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRPList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																					savePRPList.add((PurchaseRegister) purchaseRegister);
																					savePRProbableList.add((PurchaseRegister) purchaseRegister);
																					savePRRList.add((PurchaseRegister) purchaseRegister);
																					matchedids.add(gstr2b.getId().toString());
																					matched2B.add(gstr2b);
																					matchedPR.add((PurchaseRegister) purchaseRegister);
																					if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(invoiceNoMisMatched2B.contains(gstr2b)) {
																						invoiceNoMisMatched2B.remove(gstr2b);
																					}
																					if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																						gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																					}
																					if(gstNoMisMatched2B.contains(gstr2b)) {
																						gstNoMisMatched2B.remove(gstr2b);
																					}
																					mstatus = true;
																				}
																			}	
																		}else{
																			if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																			}else {
																				if(savePRGINVNOList.size() < 1) {
																					purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																					savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																					invoiceNoMisMatched2B.add(gstr2b);
																					invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																				}
																			}
																		}
																	}
																} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRPList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																			savePRProbableList.add((PurchaseRegister) purchaseRegister);
																			savePRPList.add((PurchaseRegister) purchaseRegister);
																			savePRRList.add((PurchaseRegister) purchaseRegister);
																			matchedids.add(gstr2b.getId().toString());
																			matched2B.add(gstr2b);
																			matchedPR.add((PurchaseRegister) purchaseRegister);
																			if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(invoiceNoMisMatched2B.contains(gstr2b)) {
																				invoiceNoMisMatched2B.remove(gstr2b);
																			}
																			if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																				gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																			}
																			if(gstNoMisMatched2B.contains(gstr2b)) {
																				gstNoMisMatched2B.remove(gstr2b);
																			}
																			mstatus = true;
																		}
																	}
																}else {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRGINVNOList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																			savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																			invoiceNoMisMatched2B.add(gstr2b);
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
															if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																	
																if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																}else {
																	if(savePRGGSTNOList.size() < 1) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																		savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																		gstNoMisMatched2B.add(gstr2b);
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
							}else if(invType.equals(MasterGSTConstants.IMP_GOODS) && isNotEmpty(gstr2b.getInvtype()) && gstr2b.getInvtype().equals(invType)) {
								if (isNotEmpty(gstr2b.getImpGoods())) {
									for (GSTRImportDetails gstrimpg : gstr2b.getImpGoods()) {
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
																
																if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))) {
																		purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& (((gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) == 0)
																			|| ((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) == 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax()) && isNotEmpty(purchaseRegister.getTotaltax())
																			&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																			|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))) {
																				if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																					if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						mstatus = true;
																					}else {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																		}else {
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																		}
																		matchedids.add(gstr2b.getId().toString());
																		savePRRList.add((PurchaseRegister) purchaseRegister);
																		matched2B.add(gstr2b);
																		matchedPR.add((PurchaseRegister) purchaseRegister);
																		if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(invoiceNoMisMatched2B.contains(gstr2b)) {
																			invoiceNoMisMatched2B.remove(gstr2b);
																		}
																		if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																			gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																		}
																		if(gstNoMisMatched2B.contains(gstr2b)) {
																			gstNoMisMatched2B.remove(gstr2b);
																		}
																		mstatus = true;
																	} else {
																		if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGTAXList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																				savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}
																	}
														} else if (((gstrimpg.getBoeNum().toString()).trim().toLowerCase()).equals((purchaseRegister.getInvoiceno()).trim().toLowerCase())
																&& (gstrimpg.getStin().trim()).equals((purchaseRegister.getImpGoods().get(0).getStin()))) {
																if (daysBetweenInvoiceDate <= allowedDays) {
																		if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																					&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																					&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																					|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																					&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				//Mahi-reconsileMatchingIds.add(purchaseRegister.getId().toString()));
																				if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																					&& (((gstr2b.getTotaltaxableamount()	- purchaseRegister.getTotaltaxableamount()) == 0)
																					|| ((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) == 0)))
																					&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																					&& (((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) == 0)
																					|| ((purchaseRegister.getTotaltax()	- gstr2b.getTotaltax()) == 0)))
																					&& (((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) == 0)
																					|| ((purchaseRegister.getTotalamount()	- gstr2b.getTotalamount()) == 0))) {
																					if (myFormat.format(gstrimpg.getBoeDt()).equals(myFormat.format(purchaseRegister.getDateofinvoice()))) {
																						if (gstr2b.getFp().equals(purchaseRegister.getFp())) {
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED);
																						}else {
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
																						}
																					}else {
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					}
																				}else {
																					purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																					gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
																				}
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			} else {
																				if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																					if(savePRGTAXList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_TAX_MISMATCHED);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						savePRGTAXList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2b.getId().toString());
																						matched2B.add(gstr2b);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2B.contains(gstr2b)) {
																							invoiceNoMisMatched2B.remove(gstr2b);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2B.contains(gstr2b)) {
																							gstNoMisMatched2B.remove(gstr2b);
																						}
																						mstatus = true;
																					}
																				}else if(!gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																					if(savePRGINVVALUEList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2b.getId().toString());
																						matched2B.add(gstr2b);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2B.contains(gstr2b)) {
																							invoiceNoMisMatched2B.remove(gstr2b);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2B.contains(gstr2b)) {
																							gstNoMisMatched2B.remove(gstr2b);
																						}
																						mstatus = true;
																					}
																				}else {
																					if(savePRGList.size() < 1) {
																						purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																						purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																						gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																						savePRGList.add((PurchaseRegister) purchaseRegister);
																						savePRRList.add((PurchaseRegister) purchaseRegister);
																						matchedids.add(gstr2b.getId().toString());
																						matched2B.add(gstr2b);
																						matchedPR.add((PurchaseRegister) purchaseRegister);
																						if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(invoiceNoMisMatched2B.contains(gstr2b)) {
																							invoiceNoMisMatched2B.remove(gstr2b);
																						}
																						if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																							gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																						}
																						if(gstNoMisMatched2B.contains(gstr2b)) {
																							gstNoMisMatched2B.remove(gstr2b);
																						}
																						mstatus = true;
																					}
																				}
																			}
																} else {
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																			&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= allowedDiff)
																			&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																			|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																			&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= allowedDiff)
																			&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																			|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= allowedDiff)
																			&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																			&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																					&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= allowedDiff)
																					&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																					|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= allowedDiff)
																					&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																		
																		if(gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGINVDATEList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
																				savePRGINVDATEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}else if(!gstrimpg.getBoeVal().equals(purchaseRegister.getImpGoods().get(0).getBoeVal())) {
																			if(savePRGINVVALUEList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
																				savePRGINVVALUEList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}else {
																			if(savePRGList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_MISMATCHED);
																				savePRGList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
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
																	if ((isNotEmpty(gstr2b.getTotaltaxableamount()) && isNotEmpty(purchaseRegister.getTotaltaxableamount())
																		&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																		&& (gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) >= 0)
																		|| (((purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) <= alldDiff)
																		&& (purchaseRegister.getTotaltaxableamount() - gstr2b.getTotaltaxableamount()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotaltax())	&& isNotEmpty(purchaseRegister.getTotaltax())
																		&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																		&& (gstr2b.getTotaltax()	- purchaseRegister.getTotaltax()) >= 0)
																		|| (((purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) <= alldDiff)
																		&& (purchaseRegister.getTotaltax() - gstr2b.getTotaltax()) >= 0)))
																		&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																				&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																				&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																				|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																				&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																		if (ignoreInvoiceMatch) {
																			List<Character> gstrinvd = convertStringToCharList(removeLeadingZeros(gstr2InvoiceNo));
																			List<Character> purinvd = convertStringToCharList(removeLeadingZeros(purchaseregisterInvoiceNo));
																			if (purinvd.containsAll(gstrinvd) || gstrinvd.containsAll(purinvd)) {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						if(savePRPList.size() < 1) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							savePRPList.add((PurchaseRegister) purchaseRegister);
																							savePRProbableList.add((PurchaseRegister) purchaseRegister);
																							savePRRList.add((PurchaseRegister) purchaseRegister);
																							matchedids.add(gstr2b.getId().toString());
																							matched2B.add(gstr2b);
																							matchedPR.add((PurchaseRegister) purchaseRegister);
																							if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(invoiceNoMisMatched2B.contains(gstr2b)) {
																								invoiceNoMisMatched2B.remove(gstr2b);
																							}
																							if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(gstNoMisMatched2B.contains(gstr2b)) {
																								gstNoMisMatched2B.remove(gstr2b);
																							}
																							mstatus = true;
																						}
																					}
																				}else {
																					if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																						if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																						}else {
																							if(savePRPList.size() < 1) {
																								purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																								purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																								gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																								savePRPList.add((PurchaseRegister) purchaseRegister);
																								savePRProbableList.add((PurchaseRegister) purchaseRegister);
																								savePRRList.add((PurchaseRegister) purchaseRegister);
																								matchedids.add(gstr2b.getId().toString());
																								matched2B.add(gstr2b);
																								matchedPR.add((PurchaseRegister) purchaseRegister);
																								if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																									invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																								}
																								if(invoiceNoMisMatched2B.contains(gstr2b)) {
																									invoiceNoMisMatched2B.remove(gstr2b);
																								}
																								if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																									gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																								}
																								if(gstNoMisMatched2B.contains(gstr2b)) {
																									gstNoMisMatched2B.remove(gstr2b);
																								}
																								mstatus = true;
																							}
																						}	
																					}else {
																						if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																						}else {
																							if(savePRGINVNOList.size() < 1) {
																								purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																								purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																								gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																								savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																								invoiceNoMisMatched2B.add(gstr2b);
																								invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																							}
																						}
																					}
																				}
																			}else {
																				if (gstr2InvoiceNo.contains(purchaseregisterInvoiceNo)|| purchaseregisterInvoiceNo.contains(gstr2InvoiceNo)) {
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						if(savePRPList.size() < 1) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																							savePRPList.add((PurchaseRegister) purchaseRegister);
																							savePRProbableList.add((PurchaseRegister) purchaseRegister);
																							savePRRList.add((PurchaseRegister) purchaseRegister);
																							matchedids.add(gstr2b.getId().toString());
																							matched2B.add(gstr2b);
																							matchedPR.add((PurchaseRegister) purchaseRegister);
																							if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(invoiceNoMisMatched2B.contains(gstr2b)) {
																								invoiceNoMisMatched2B.remove(gstr2b);
																							}
																							if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																								gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																							}
																							if(gstNoMisMatched2B.contains(gstr2b)) {
																								gstNoMisMatched2B.remove(gstr2b);
																							}
																							mstatus = true;
																						}
																					}	
																				}else{
																					if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																					}else {
																						if(savePRGINVNOList.size() < 1) {
																							purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																							purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																							savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																							invoiceNoMisMatched2B.add(gstr2b);
																							invoiceNoMisMatchedPR.add((PurchaseRegister) purchaseRegister);
																						}
																					}
																				}
																			}
																	} else if (gstr2InvoiceNo.equals(purchaseregisterInvoiceNo)) {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRPList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
																				savePRProbableList.add((PurchaseRegister) purchaseRegister);
																				savePRPList.add((PurchaseRegister) purchaseRegister);
																				savePRRList.add((PurchaseRegister) purchaseRegister);
																				matchedids.add(gstr2b.getId().toString());
																				matched2B.add(gstr2b);
																				matchedPR.add((PurchaseRegister) purchaseRegister);
																				if(invoiceNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					invoiceNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(invoiceNoMisMatched2B.contains(gstr2b)) {
																					invoiceNoMisMatched2B.remove(gstr2b);
																				}
																				if(gstNoMisMatchedPR.contains((PurchaseRegister) purchaseRegister)) {
																					gstNoMisMatchedPR.remove((PurchaseRegister) purchaseRegister);
																				}
																				if(gstNoMisMatched2B.contains(gstr2b)) {
																					gstNoMisMatched2B.remove(gstr2b);
																				}
																				mstatus = true;
																			}
																		}
																	} else {
																		if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																		}else {
																			if(savePRGINVNOList.size() < 1) {
																				purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																				purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);
																				savePRGINVNOList.add((PurchaseRegister) purchaseRegister);
																				invoiceNoMisMatched2B.add(gstr2b);
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
																if ((isNotEmpty(gstr2b.getTotaltaxableamount())&& isNotEmpty(purchaseRegister.getTotaltaxableamount())
																	&& ((((gstr2b.getTotaltaxableamount() - purchaseRegister.getTotaltaxableamount()) <= alldDiff)
																	&& (gstr2b.getTotaltaxableamount()- purchaseRegister.getTotaltaxableamount()) >= 0)
																	|| (((purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) <= alldDiff)
																	&& (purchaseRegister.getTotaltaxableamount()- gstr2b.getTotaltaxableamount()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotaltax())&& isNotEmpty(purchaseRegister.getTotaltax())
																	&& ((((gstr2b.getTotaltax() - purchaseRegister.getTotaltax()) <= alldDiff)
																	&& (gstr2b.getTotaltax()- purchaseRegister.getTotaltax()) >= 0)
																	|| (((purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) <= alldDiff)
																	&& (purchaseRegister.getTotaltax()- gstr2b.getTotaltax()) >= 0)))
																	&& (isNotEmpty(gstr2b.getTotalamount())	&& isNotEmpty(purchaseRegister.getTotalamount())
																			&& ((((gstr2b.getTotalamount() - purchaseRegister.getTotalamount()) <= alldDiff)
																			&& (gstr2b.getTotalamount()	- purchaseRegister.getTotalamount()) >= 0)
																			|| (((purchaseRegister.getTotalamount()- gstr2b.getTotalamount()) <= alldDiff)
																			&& (purchaseRegister.getTotalamount() - gstr2b.getTotalamount()) >= 0)))) {
																	if(isNotEmpty(purchaseRegister.getGstr2bMatchingStatus()) && "Matched".equalsIgnoreCase(purchaseRegister.getGstr2bMatchingStatus())) {
																	}else {
																		if(savePRGGSTNOList.size() < 1) {
																			purchaseRegister.setGstr2bMatchingId(Arrays.asList(gstr2b.getId().toString()));
																			purchaseRegister.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																			gstr2b.setGstr2bMatchingStatus(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
																			savePRGGSTNOList.add((PurchaseRegister) purchaseRegister);
																			gstNoMisMatched2B.add(gstr2b);
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
