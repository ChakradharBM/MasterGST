package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR2AIMPG;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRImportItems;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BB2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BCDN;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPG;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPGSEZ;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BItems;
import com.mastergst.usermanagement.runtime.repository.ClientRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BSupportRepository;
import com.mongodb.WriteResult;

@RestController
public class Gstr2BMigrateController {
	private static final Logger logger = LogManager.getLogger(Gstr2BMigrateController.class.getName());
	private static final String CLASSNAME = "Gstr2BMigrateController::";

	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private ClientRepository clientRepository;
	
	@Autowired
	private GSTR2BSupportRepository gstr2bSupportRepository;
	
	@Autowired
	private GSTR2BRepository gstr2bRepository;
	
	@RequestMapping(value = "/migrate2b", method = RequestMethod.GET)
	public String migrate() {
		List<String> clientLst = new ArrayList<String>();
		Query query = new Query();
		int length = 5000;
		for(int i=0;i< Integer.MAX_VALUE;i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<GSTR2B> gstr2b = mongoTemplate.find(query, GSTR2B.class);
			if(NullUtil.isEmpty(gstr2b)) {
				break;
			}
			
			for(GSTR2B invoice : gstr2b) {
				String docKey = UUID.randomUUID().toString();
				invoice.setDocKey(docKey);
				
				Client client = clientRepository.findOne(invoice.getClientid());
				if(isNotEmpty(client)) {
					
					if(clientLst.contains(client.getId().toString()+"_"+invoice.getFp())) {
						logger.info("Gstr2b Records deleted ::"+invoice.getId().toString() +" clientid ::"+invoice.getClientid());
						gstr2bRepository.delete(invoice);
					}else {
						clientLst.add(client.getId().toString()+"_"+invoice.getFp());
						logger.info("Client info ::"+client.getId());
						gst2bSupportDocuments(invoice, docKey, invoice.getFullname(), client, invoice.getUserid(), invoice.getFp());
						System.out.println("doc affected ---- "+ invoice.getId());
						gstr2bRepository.save(invoice);
					}
				}
			}
		}	
		
		return "{\"status\":\"success\"}";
	}

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
						//GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), b2b.getSupprd(), userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
						GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
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
						GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
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
						GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
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
						GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
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
					
					//GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, invoice.getFp(), userid, client.getId().toString(), invType);
					GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, returnPeriod, userid, client.getId().toString(), invType);
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
						GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, returnPeriod, userid, client.getId().toString(), invType);
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
		
		@RequestMapping(value = "/migrate2bCridetDebitNotes", method = RequestMethod.GET)
		public String migrateCreditDebitNotes() {
			
			Query query = new Query();
			query.fields().include("invtype");
			query.fields().include("cdn");
			query.fields().include("cdna");
			int length = 5000;
			for(int i=0;i< Integer.MAX_VALUE;i++) {
				Pageable pageable = new PageRequest(i, length);
				query.with(pageable);
				List<GSTR2BSupport> gstr2b = mongoTemplate.find(query, GSTR2BSupport.class);
				if(NullUtil.isEmpty(gstr2b)) {
					break;
				}
				
				for(GSTR2BSupport invoice : gstr2b) {
					
					Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
					Update update = new Update();
					int sftr = 1;
					int csftr = 1;
					if(MasterGSTConstants.CREDIT_DEBIT_NOTES.equals(invoice.getInvtype())){
						
						if(isNotEmpty(invoice.getCdn())) {
							if(isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt())) {
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									String ntty = invoice.getCdn().get(0).getNt().get(0).getNtty();
									if("C".equalsIgnoreCase(ntty)) {
										sftr = -1;
									}
								}
							}
						}
					}else if(MasterGSTConstants.CDNA.equals(invoice.getInvtype())) {
						if(isNotEmpty(invoice.getCdn())) {
							if(isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt())) {
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									String ntty = invoice.getCdn().get(0).getNt().get(0).getNtty();
									if("C".equalsIgnoreCase(ntty)) {
										sftr = -1;
									}
								}
							}
						}else if(isNotEmpty(invoice.getCdna())) {
							if(isNotEmpty(invoice.getCdna().get(0)) && isNotEmpty(invoice.getCdna().get(0).getNt())) {
								if(isNotEmpty(invoice.getCdna().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdna().get(0).getNt().get(0).getNtty())) {
									String ntty = invoice.getCdna().get(0).getNt().get(0).getNtty();
									if("C".equalsIgnoreCase(ntty)) {
										sftr = -1;
									}
								}
							}
						}
					}
					update.set("sftr", sftr);
					update.set("csftr",csftr);
					WriteResult result = mongoTemplate.updateFirst(queryTmp, update, GSTR2BSupport.class);
					System.out.println(result.getN()+"doc affected ---- "+ invoice.getId());
				}
			}
			
			return "{\"status\":\"success\"}";
		}
	

}
