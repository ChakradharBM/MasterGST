package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2BA;
import static com.mastergst.core.common.MasterGSTConstants.CDNA;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.Gstr2Dao;
import com.mastergst.usermanagement.runtime.dao.Gstr2bDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.FilingStatusReportsVO;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR2AIMPG;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRDocListDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRISD;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRImportItems;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BB2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BCDN;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocList;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPG;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPGSEZ;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BISD;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BISDSupplies;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BImports;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BItems;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BNonRevSup;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BOthrSupplies;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BRevSupplies;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BSupportRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.response.GSTR2BResponse;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.response.gstr2b.GSTR2BVO;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
public class InvoiceServiceImpl implements InvoiceService{
	private static final Logger logger = LogManager.getLogger(InvoiceServiceImpl.class.getName());
	private static final String CLASSNAME = "InvoiceServiceImpl::";
	
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private UserService userService;
	@Autowired
	private Gstr2Dao gstr2Dao;
	@Autowired
	private Gstr2bDao gstr2bDao;
	
	@Autowired
	private GSTR2BSupportRepository  gstr2bSupportRepository;
	@Autowired private PurchaseRegisterRepository purchaseRepository;
	@Autowired private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired private ReturnsDownloadStatusService returnsDownloadStatusService;
	
	@Autowired
	private GSTR2BRepository gstr2bRepository;
	private static DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	private static String DOUBLE_FORMAT  = "%.2f";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	@Override
	public GSTR2B syncGstr2bInvoices(Client client, String userid, String returntype, String returnPeriod, boolean initial) {
		logger.debug(CLASSNAME + "syncInvoiceData : Begin");
		logger.debug(CLASSNAME + "syncInvoiceData : returnType {}", returntype);
		logger.debug(CLASSNAME + "syncInvoiceData : returnPeriod {}", returnPeriod);
		GSTR2B invoice = null;	
		if (isNotEmpty(client)) {
			try {
			 invoice = iHubConsumerService.getGstr2bInvoices(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,0);
				if(isNotEmpty(invoice) && isNotEmpty(invoice.getData()) && isNotEmpty(invoice.getData().getFc())) {
					if(invoice.getData().getFc() > 0) {
						processGstr2bData(invoice, client, userid, returntype, returnPeriod);
						for(int i = 1;i<= invoice.getData().getFc(); i++) {
							GSTR2B invoicebyfilenum = iHubConsumerService.getGstr2bInvoices(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,i);
							processGstr2bDataByFileNum(invoicebyfilenum, client, userid, returntype, returnPeriod);
						}
					}else {
						processGstr2bData(invoice, client, userid, returntype, returnPeriod);
					}
				}else {
					processGstr2bData(invoice, client, userid, returntype, returnPeriod);
				}
			} catch(Exception e) {
				logger.debug(CLASSNAME + "syncInvoiceData : ERROR", e);
			}
		}
		return invoice;
	}
	

	public void processGstr2bData(GSTR2B invoice, final Client client, String userid, final String returntype, final String returnPeriod) throws ParseException {
		logger.debug(CLASSNAME + "processGstr2bData : Begin");
		invoice.setClientid(client.getId().toString());
		invoice.setUserid(userid);
		invoice.setFp(returnPeriod);
		User user = userService.findById(userid);
		if(isNotEmpty(user) && isNotEmpty(user.getFullname())) {
			invoice.setFullname(user.getFullname());
		}
		String docKey = UUID.randomUUID().toString();
		invoice.setDocKey(docKey);
		
		List<GSTR2B> gstr2b = gstr2bRepository.findByFpAndClientid(returnPeriod, client.getId().toString());
		logger.info("GSTR2B Content size() ::"+gstr2b.size());
		for(GSTR2B inv : gstr2b) {
			if(isNotEmpty(inv)) {
				String key = inv.getDocKey();
				List<GSTR2BSupport> g2bSupport = gstr2bSupportRepository.findByClientidAndDocKey(client.getId().toString(), key);
				gstr2bRepository.delete(inv);
				if(isNotEmpty(g2bSupport) && g2bSupport.size() > 0) {
					logger.info("GSTR2B Support Content size() ::"+g2bSupport.size());
					List<String> matchingIds = Lists.newArrayList();
					g2bSupport.forEach(gst2bInv -> {
						//String matchingStatus = gst2bInv.getGstr2bMatchingStatus();
						if(isNotEmpty(gst2bInv.getGstr2bMatchingStatus())){
							if(!MasterGSTConstants.GST_STATUS_NOTINPURCHASES.equalsIgnoreCase(gst2bInv.getGstr2bMatchingStatus())) {
								matchingIds.add(gst2bInv.getId().toString());
							}
						}
					});
					if(isNotEmpty(matchingIds)) {
						logger.info("GSTR2B matchingIds ::"+matchingIds.size());
						delinkPurchaseRegisterGstr2bMatchingIds(matchingIds);
					}
					if(isNotEmpty(g2bSupport) && g2bSupport.size() > 0) {
						logger.info("GSTR2B Support Content size() ::"+g2bSupport.size());
						gstr2bSupportRepository.delete(g2bSupport);
					}
				}
			}
		}
		gstr2bRepository.save(invoice);
		gst2bSupportDocuments(invoice, docKey, user.getFullname(), client, userid, returnPeriod);
		logger.debug(CLASSNAME + "processGstr2bData : End");
	}
	
	public void processGstr2bDataByFileNum(GSTR2B invoice, final Client client, String userid, final String returntype, final String returnPeriod) throws ParseException {
		logger.info(CLASSNAME + "processGstr2bDataByFileNum : Begin");
		invoice.setClientid(client.getId().toString());
		invoice.setUserid(userid);
		invoice.setFp(returnPeriod);
		User user = userService.findById(userid);
		if(isNotEmpty(user) && isNotEmpty(user.getFullname())) {
			invoice.setFullname(user.getFullname());
		}
		
		GSTR2B gstr2binv = gstr2bRepository.findByClientidAndFp(client.getId().toString(), returnPeriod);
		
		if(isNotEmpty(gstr2binv) && isNotEmpty(gstr2binv.getData()) && isNotEmpty(gstr2binv.getData().getDocdata())) {
			if(isNotEmpty(invoice) && isNotEmpty(invoice.getData()) && isNotEmpty(invoice.getData().getDocdata())) {
				if(isNotEmpty(invoice.getData().getDocdata().getB2b()) && isNotEmpty(gstr2binv.getData().getDocdata().getB2b())) {
					List<GSTR2BB2B> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getB2b());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getB2b());
					gstr2binv.getData().getDocdata().setB2b(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getB2b()) && isEmpty(gstr2binv.getData().getDocdata().getB2b())) {
					gstr2binv.getData().getDocdata().setB2b(invoice.getData().getDocdata().getB2b());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getB2ba()) && isNotEmpty(gstr2binv.getData().getDocdata().getB2ba())) {
					List<GSTR2BB2B> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getB2ba());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getB2ba());
					gstr2binv.getData().getDocdata().setB2ba(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getB2ba()) && isEmpty(gstr2binv.getData().getDocdata().getB2ba())) {
					gstr2binv.getData().getDocdata().setB2ba(invoice.getData().getDocdata().getB2b());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getCdnr()) && isNotEmpty(gstr2binv.getData().getDocdata().getCdnr())) {
					List<GSTR2BCDN> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getCdnr());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getCdnr());
					gstr2binv.getData().getDocdata().setCdnr(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getCdnr()) && isEmpty(gstr2binv.getData().getDocdata().getCdnr())) {
					gstr2binv.getData().getDocdata().setCdnr(invoice.getData().getDocdata().getCdnr());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getCdnra()) && isNotEmpty(gstr2binv.getData().getDocdata().getCdnra())) {
					List<GSTR2BCDN> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getCdnra());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getCdnra());
					gstr2binv.getData().getDocdata().setCdnra(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getCdnra()) && isEmpty(gstr2binv.getData().getDocdata().getCdnra())) {
					gstr2binv.getData().getDocdata().setCdnra(invoice.getData().getDocdata().getCdnra());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getIsd()) && isNotEmpty(gstr2binv.getData().getDocdata().getIsd())) {
					List<GSTR2BISD> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getIsd());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getIsd());
					gstr2binv.getData().getDocdata().setIsd(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getIsd()) && isEmpty(gstr2binv.getData().getDocdata().getIsd())) {
					gstr2binv.getData().getDocdata().setIsd(invoice.getData().getDocdata().getIsd());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getIsda()) && isNotEmpty(gstr2binv.getData().getDocdata().getIsda())) {
					List<GSTR2BISD> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getIsda());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getIsda());
					gstr2binv.getData().getDocdata().setIsda(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getIsda()) && isEmpty(gstr2binv.getData().getDocdata().getIsda())) {
					gstr2binv.getData().getDocdata().setIsda(invoice.getData().getDocdata().getIsda());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getImpg()) && isNotEmpty(gstr2binv.getData().getDocdata().getImpg())) {
					List<GSTR2BIMPG> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getImpg());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getImpg());
					gstr2binv.getData().getDocdata().setImpg(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getImpg()) && isEmpty(gstr2binv.getData().getDocdata().getImpg())) {
					gstr2binv.getData().getDocdata().setImpg(invoice.getData().getDocdata().getImpg());
				}
				
				if(isNotEmpty(invoice.getData().getDocdata().getImpgsez()) && isNotEmpty(gstr2binv.getData().getDocdata().getImpgsez())) {
					List<GSTR2BIMPGSEZ> gstr2bb2b = Lists.newArrayList();
					gstr2bb2b.addAll(gstr2binv.getData().getDocdata().getImpgsez());
					gstr2bb2b.addAll(invoice.getData().getDocdata().getImpgsez());
					gstr2binv.getData().getDocdata().setImpgsez(gstr2bb2b);
				}else if(isNotEmpty(invoice.getData().getDocdata().getImpgsez()) && isEmpty(gstr2binv.getData().getDocdata().getImpgsez())) {
					gstr2binv.getData().getDocdata().setImpgsez(invoice.getData().getDocdata().getImpgsez());
				}
				
			}
		}else if(isNotEmpty(gstr2binv) && isNotEmpty(gstr2binv.getData()) && isEmpty(gstr2binv.getData().getDocdata())){
			gstr2binv.getData().setDocdata(invoice.getData().getDocdata());
		}
		gstr2bRepository.save(gstr2binv);
		gst2bSupportDocuments(invoice, gstr2binv.getDocKey(), user.getFullname(), client, userid, returnPeriod);
		logger.debug(CLASSNAME + "processGstr2bData : End");
	}
	
	private void delinkPurchaseRegisterGstr2bMatchingIds(List<String> matchingIds) {
		if(isNotEmpty(matchingIds)) {
			int length = 500;
			for(int i = 0; i < Integer.MAX_VALUE ; i++) {
				Pageable pageable = new PageRequest(i, length);
				List<PurchaseRegister> invoices = purchaseRepository.findByGstr2bMatchingIdIn(matchingIds, pageable);
				if(isEmpty(invoices)) {
					break;
				}
				for(PurchaseRegister inv : invoices) {
					inv.setGstr2bMatchingId(null);
					inv.setGstr2bMatchingStatus(null);
					inv.setGstr2bMatchingRsn(null);
					purchaseRepository.save(inv);
				}
			}
		}
	}
		
	public List<GSTR2BVO> getGSTR2BData(String clientid, String returnPeriod,GSTR2B gstr2bdata) {
		List<GSTR2BVO > gstr2bLst= new ArrayList<GSTR2BVO >();
		GSTR2BVO  mainpart= new GSTR2BVO();
		mainpart.setHeading("Credit which may be availed under FORM GSTR-3B");
		GSTR2BVO  parta= new GSTR2BVO();
		parta.setHeading("PART-A) ITC Available - Credit may be claimed in relevant headings in GSTR-3B");
		GSTR2BVO  nonrevsupO= new GSTR2BVO();
		nonrevsupO.setHeading("I) All other ITC - Supplies from registered persons other than reverse charge");
		nonrevsupO.setTableValue("4(A)(5)");
		nonrevsupO.setAdvisory("If this is positive, credit may be availed under Table 4(A)(5) of FORM GSTR-3B.");
		GSTR2BVO  nonrevsupb2b= new GSTR2BVO();
		nonrevsupb2b.setHeading("B2B - Invoices");
		GSTR2BVO  nonrevsupcdnr= new GSTR2BVO();
		nonrevsupcdnr.setHeading("B2B - Debit notes");
		GSTR2BVO nonrevsupb2ba= new GSTR2BVO();
		nonrevsupb2ba.setHeading("B2B - Invoices (Amendment)");
		GSTR2BVO nonrevsupcdnra= new GSTR2BVO();
		nonrevsupcdnra.setHeading("B2B - Debit notes (Amendment)");
		GSTR2BVO isdsupO= new GSTR2BVO();
		isdsupO.setHeading("II) Inward Supplies from ISD");
		isdsupO.setTableValue("4(A)(4)");
		isdsupO.setAdvisory("If this is positive, credit may be availed under Table 4(A)(4) of FORM GSTR-3B.");
		GSTR2BVO isdsupISD = new GSTR2BVO();
		isdsupISD.setHeading("ISD - Invoices");
		GSTR2BVO isdsupISDA= new GSTR2BVO();
		isdsupISDA.setHeading("ISD - Invoices (Amendment)");
		GSTR2BVO  revsupO= new GSTR2BVO();
		revsupO.setHeading("III) Inward Supplies liable for reverse charge");
		revsupO.setTableValue("3.1(d) 4(A)(3)");
		revsupO.setAdvisory("These supplies shall be declared in Table 3.1(d) of FORM GSTR-3B for payment of tax.");
		GSTR2BVO revsupb2b= new GSTR2BVO();
		revsupb2b.setHeading("B2B - Invoices");
		GSTR2BVO revsupcdnr= new GSTR2BVO();
		revsupcdnr.setHeading("B2B - Debit notes");
		GSTR2BVO revsupb2ba= new GSTR2BVO();
		revsupb2ba.setHeading("B2B - Invoices (Amendment)");
		GSTR2BVO revsupcdnra= new GSTR2BVO();
		revsupcdnra.setHeading("B2B - Debit notes (Amendment)");
		GSTR2BVO  itcavlimp= new GSTR2BVO();
		itcavlimp.setHeading("IV) Import of Goods");
		itcavlimp.setTableValue("4(A)(1)");
		itcavlimp.setAdvisory("If this is positive, credit may be availed under Table 4(A)(1) of FORM GSTR-3B.");
		GSTR2BVO  itcavlimpg= new GSTR2BVO();
		itcavlimpg.setHeading("IMPG - Import of goods from overseas");
		GSTR2BVO  itcavlimpga= new GSTR2BVO();
		itcavlimpga.setHeading("IMPG (Amendment)");
		GSTR2BVO  itcavlimpgsez= new GSTR2BVO();
		itcavlimpgsez.setHeading("IMPGSEZ - Import of goods from SEZ");
		GSTR2BVO  itcavlimpgseza= new GSTR2BVO();
		itcavlimpgseza.setHeading("IMPGSEZ (Amendment)");
		GSTR2BVO  partb= new GSTR2BVO();
		partb.setHeading("PART-B) ITC Reversal - Credit should be reversed in relevant headings in GSTR-3B");
		GSTR2BVO  others= new GSTR2BVO();
		others.setHeading("I) Others");
		others.setTableValue("4(B)(2)");
		others.setAdvisory("If this is negative, then credit may be reclaimed subject to reversal of the same on an earlier instance.");
		GSTR2BVO  othersb2bcdn = new GSTR2BVO();
		othersb2bcdn.setHeading("B2B - Credit notes");
		GSTR2BVO  othersb2bcdna = new GSTR2BVO();
		othersb2bcdna.setHeading("B2B - Credit notes (Amendment)");
		GSTR2BVO  othersb2bcdnr = new GSTR2BVO();
		othersb2bcdnr.setHeading("B2B - Credit notes (Reverse charge)");
		GSTR2BVO  othersb2bcdnra = new GSTR2BVO();
		othersb2bcdnra.setHeading("B2B - Credit notes (Reverse charge)(Amendment)");
		GSTR2BVO  othersisdcdn = new GSTR2BVO();
		othersisdcdn.setHeading("ISD - Credit notes");
		GSTR2BVO  othersisdcdna = new GSTR2BVO();
		othersisdcdna.setHeading("ISD - Credit notes (Amendment)");
		if(isNotEmpty(gstr2bdata) && isNotEmpty(gstr2bdata.getData()) && isNotEmpty(gstr2bdata.getData().getItcsumm())) {
			if(isNotEmpty(gstr2bdata.getData().getItcsumm().getItcavl())) {
				if(isNotEmpty(gstr2bdata.getData().getItcsumm().getItcavl().getNonrevsup())) {
					GSTR2BNonRevSup nonrevsup = gstr2bdata.getData().getItcsumm().getItcavl().getNonrevsup();
					if(isNotEmpty(nonrevsup)) {
					if(isNotEmpty(nonrevsup.getIgst())) {
						nonrevsupO.setIgst(nonrevsup.getIgst());
					}
					if(isNotEmpty(nonrevsup.getIgst())) {
						nonrevsupO.setCgst(nonrevsup.getCgst());					
					}
					if(isNotEmpty(nonrevsup.getIgst())) {
						nonrevsupO.setSgst(nonrevsup.getSgst());
					}
					if(isNotEmpty(nonrevsup.getIgst())) {
						nonrevsupO.setCess(nonrevsup.getCess());
					}
					if(isNotEmpty(nonrevsup.getB2b())) {
						if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
							nonrevsupb2b.setIgst(nonrevsup.getB2b().getIgst());
						}
						if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
							nonrevsupb2b.setCgst(nonrevsup.getB2b().getCgst());					
						}
						if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
							nonrevsupb2b.setSgst(nonrevsup.getB2b().getSgst());
						}
						if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
							nonrevsupb2b.setCess(nonrevsup.getB2b().getCess());
						}
						
					}
					if(isNotEmpty(nonrevsup.getB2ba())) {
						if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
							nonrevsupb2ba.setIgst(nonrevsup.getB2ba().getIgst());
						}
						if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
							nonrevsupb2ba.setCgst(nonrevsup.getB2ba().getCgst());					
						}
						if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
							nonrevsupb2ba.setSgst(nonrevsup.getB2ba().getSgst());
						}
						if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
							nonrevsupb2ba.setCess(nonrevsup.getB2ba().getCess());
						}
					}
					if(isNotEmpty(nonrevsup.getCdnr())) {
						if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
							nonrevsupcdnr.setIgst(nonrevsup.getCdnr().getIgst());
						}
						if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
							nonrevsupcdnr.setCgst(nonrevsup.getCdnr().getCgst());					
						}
						if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
							nonrevsupcdnr.setSgst(nonrevsup.getCdnr().getSgst());
						}
						if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
							nonrevsupcdnr.setCess(nonrevsup.getCdnr().getCess());
						}
					}
					if(isNotEmpty(nonrevsup.getCdnra())) {
						if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
							nonrevsupcdnra.setIgst(nonrevsup.getCdnra().getIgst());
						}
						if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
							nonrevsupcdnra.setCgst(nonrevsup.getCdnra().getCgst());					
						}
						if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
							nonrevsupcdnra.setSgst(nonrevsup.getCdnra().getSgst());
						}
						if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
							nonrevsupcdnra.setCess(nonrevsup.getCdnra().getCess());
						}
					}
				}
				}
					GSTR2BISDSupplies isdsup = gstr2bdata.getData().getItcsumm().getItcavl().getIsdsup();
					if(isNotEmpty(isdsup)) {
						if(isNotEmpty(isdsup.getIgst())) {
							isdsupO.setIgst(isdsup.getIgst());
						}
						if(isNotEmpty(isdsup.getCgst())) {
							isdsupO.setCgst(isdsup.getCgst());
						}
						if(isNotEmpty(isdsup.getSgst())) {
							isdsupO.setSgst(isdsup.getSgst());
						}
						if(isNotEmpty(isdsup.getCess())) {
							isdsupO.setCess(isdsup.getCess());
						}
						if(isNotEmpty(isdsup.getIsd())) {
							if(isNotEmpty(isdsup.getIsd().getIgst())) {
								isdsupISD.setIgst(isdsup.getIsd().getIgst());
							}
							if(isNotEmpty(isdsup.getIsd().getCgst())) {
								isdsupISD.setCgst(isdsup.getIsd().getCgst());
							}
							if(isNotEmpty(isdsup.getIsd().getSgst())) {
								isdsupISD.setSgst(isdsup.getIsd().getSgst());
							}
							if(isNotEmpty(isdsup.getIsd().getCess())) {
								isdsupISD.setCess(isdsup.getIsd().getCess());
							}
						}
						if(isNotEmpty(isdsup.getIsda())) {
							if(isNotEmpty(isdsup.getIsda().getIgst())) {
								isdsupISDA.setIgst(isdsup.getIsda().getIgst());
							}
							if(isNotEmpty(isdsup.getIsda().getCgst())) {
								isdsupISDA.setCgst(isdsup.getIsda().getCgst());
							}
							if(isNotEmpty(isdsup.getIsda().getSgst())) {
								isdsupISDA.setSgst(isdsup.getIsda().getSgst());
							}
							if(isNotEmpty(isdsup.getIsda().getCess())) {
								isdsupISDA.setCess(isdsup.getIsda().getCess());
							}
						}
					}
					
					GSTR2BRevSupplies revsup = gstr2bdata.getData().getItcsumm().getItcavl().getRevsup();
					if(isNotEmpty(revsup)) {
						if(isNotEmpty(revsup.getIgst())) {
							revsupO.setIgst(revsup.getIgst());
						}
						if(isNotEmpty(revsup.getCgst())) {
							revsupO.setCgst(revsup.getCgst());
						}
						if(isNotEmpty(revsup.getSgst())) {
							revsupO.setSgst(revsup.getSgst());
						}
						if(isNotEmpty(revsup.getCess())) {
							revsupO.setCess(revsup.getCess());
						}
						if(isNotEmpty(revsup.getB2b())) {
							if(isNotEmpty(revsup.getB2b().getIgst())) {
								revsupb2b.setIgst(revsup.getB2b().getIgst());
							}
							if(isNotEmpty(revsup.getB2b().getCgst())) {
								revsupb2b.setCgst(revsup.getB2b().getCgst());
							}
							if(isNotEmpty(revsup.getB2b().getSgst())) {
								revsupb2b.setSgst(revsup.getB2b().getSgst());
							}
							if(isNotEmpty(revsup.getB2b().getCess())) {
								revsupb2b.setCess(revsup.getB2b().getCess());
							}
						}
						if(isNotEmpty(revsup.getB2ba())) {
							if(isNotEmpty(revsup.getB2ba().getIgst())) {
								revsupb2ba.setIgst(revsup.getB2ba().getIgst());
							}
							if(isNotEmpty(revsup.getB2ba().getCgst())) {
								revsupb2ba.setCgst(revsup.getB2ba().getCgst());
							}
							if(isNotEmpty(revsup.getB2ba().getSgst())) {
								revsupb2ba.setSgst(revsup.getB2ba().getSgst());
							}
							if(isNotEmpty(revsup.getB2ba().getCess())) {
								revsupb2ba.setCess(revsup.getB2ba().getCess());
							}
						}
						if(isNotEmpty(revsup.getCdnr())) {
							if(isNotEmpty(revsup.getCdnr().getIgst())) {
								revsupcdnr.setIgst(revsup.getCdnr().getIgst());
							}
							if(isNotEmpty(revsup.getCdnr().getCgst())) {
								revsupcdnr.setCgst(revsup.getCdnr().getCgst());
							}
							if(isNotEmpty(revsup.getCdnr().getSgst())) {
								revsupcdnr.setSgst(revsup.getCdnr().getSgst());
							}
							if(isNotEmpty(revsup.getCdnr().getCess())) {
								revsupcdnr.setCess(revsup.getCdnr().getCess());
							}
						}
						if(isNotEmpty(revsup.getCdnra())) {
							if(isNotEmpty(revsup.getCdnra().getIgst())) {
								revsupcdnra.setIgst(revsup.getCdnra().getIgst());
							}
							if(isNotEmpty(revsup.getCdnra().getCgst())) {
								revsupcdnra.setCgst(revsup.getCdnra().getCgst());
							}
							if(isNotEmpty(revsup.getCdnra().getSgst())) {
								revsupcdnra.setSgst(revsup.getCdnra().getSgst());
							}
							if(isNotEmpty(revsup.getCdnra().getCess())) {
								revsupcdnra.setCess(revsup.getCdnra().getCess());
							}
						}
					}
					
					GSTR2BImports avlimp = gstr2bdata.getData().getItcsumm().getItcavl().getImports();
					if(isNotEmpty(avlimp)) {
						if(isNotEmpty(avlimp.getIgst())) {
							itcavlimp.setIgst(avlimp.getIgst());
						}
						if(isNotEmpty(avlimp.getCgst())) {
							itcavlimp.setCgst(avlimp.getCgst());
						}
						if(isNotEmpty(avlimp.getSgst())) {
							itcavlimp.setSgst(avlimp.getSgst());
						}
						if(isNotEmpty(avlimp.getCess())) {
							itcavlimp.setCess(avlimp.getCess());
						}
						if(isNotEmpty(avlimp.getImpg())) {
							if(isNotEmpty(avlimp.getImpg().getIgst())) {
								itcavlimpg.setIgst(avlimp.getImpg().getIgst());
							}
							if(isNotEmpty(avlimp.getImpg().getCgst())) {
								itcavlimpg.setCgst(avlimp.getImpg().getCgst());
							}
							if(isNotEmpty(avlimp.getImpg().getSgst())) {
								itcavlimpg.setSgst(avlimp.getImpg().getSgst());
							}
							if(isNotEmpty(avlimp.getImpg().getCess())) {
								itcavlimpg.setCess(avlimp.getImpg().getCess());
							}
						}
						if(isNotEmpty(avlimp.getImpga())) {
							if(isNotEmpty(avlimp.getImpga().getIgst())) {
								itcavlimpga.setIgst(avlimp.getImpga().getIgst());
							}
							if(isNotEmpty(avlimp.getImpga().getCgst())) {
								itcavlimpga.setCgst(avlimp.getImpga().getCgst());
							}
							if(isNotEmpty(avlimp.getImpga().getSgst())) {
								itcavlimpga.setSgst(avlimp.getImpga().getSgst());
							}
							if(isNotEmpty(avlimp.getImpga().getCess())) {
								itcavlimpga.setCess(avlimp.getImpga().getCess());
							}
						}
						if(isNotEmpty(avlimp.getImpgsez())) {
							if(isNotEmpty(avlimp.getImpgsez().getIgst())) {
								itcavlimpgsez.setIgst(avlimp.getImpgsez().getIgst());
							}
							if(isNotEmpty(avlimp.getImpgsez().getCgst())) {
								itcavlimpgsez.setCgst(avlimp.getImpgsez().getCgst());
							}
							if(isNotEmpty(avlimp.getImpgsez().getSgst())) {
								itcavlimpgsez.setSgst(avlimp.getImpgsez().getSgst());
							}
							if(isNotEmpty(avlimp.getImpgsez().getCess())) {
								itcavlimpgsez.setCess(avlimp.getImpgsez().getCess());
							}
						}
						if(isNotEmpty(avlimp.getImpgasez())) {
							if(isNotEmpty(avlimp.getImpgasez().getIgst())) {
								itcavlimpgseza.setIgst(avlimp.getImpgasez().getIgst());
							}
							if(isNotEmpty(avlimp.getImpgasez().getCgst())) {
								itcavlimpgseza.setCgst(avlimp.getImpgasez().getCgst());
							}
							if(isNotEmpty(avlimp.getImpgasez().getSgst())) {
								itcavlimpgseza.setSgst(avlimp.getImpgasez().getSgst());
							}
							if(isNotEmpty(avlimp.getImpgasez().getCess())) {
								itcavlimpgseza.setCess(avlimp.getImpgasez().getCess());
							}
						}
					}
					
					GSTR2BOthrSupplies avlothers = gstr2bdata.getData().getItcsumm().getItcavl().getOthersup();
					if(isNotEmpty(avlothers)) {
						if(isNotEmpty(avlothers.getIgst())) {
							others.setIgst(avlothers.getIgst());
						}
						if(isNotEmpty(avlothers.getCgst())) {
							others.setCgst(avlothers.getCgst());
						}
						if(isNotEmpty(avlothers.getSgst())) {
							others.setSgst(avlothers.getSgst());
						}
						if(isNotEmpty(avlothers.getCess())) {
							others.setCess(avlothers.getCess());
						}
						if(isNotEmpty(avlothers.getCdnr())) {
							if(isNotEmpty(avlothers.getCdnr().getIgst())) {
								othersb2bcdn.setIgst(avlothers.getCdnr().getIgst());
							}
							if(isNotEmpty(avlothers.getCdnr().getCgst())) {
								othersb2bcdn.setCgst(avlothers.getCdnr().getCgst());
							}
							if(isNotEmpty(avlothers.getCdnr().getSgst())) {
								othersb2bcdn.setSgst(avlothers.getCdnr().getSgst());
							}
							if(isNotEmpty(avlothers.getCdnr().getCess())) {
								othersb2bcdn.setCess(avlothers.getCdnr().getCess());
							}
						}
						if(isNotEmpty(avlothers.getCdnra())) {
							if(isNotEmpty(avlothers.getCdnra().getIgst())) {
								othersb2bcdna.setIgst(avlothers.getCdnra().getIgst());
							}
							if(isNotEmpty(avlothers.getCdnra().getCgst())) {
								othersb2bcdna.setCgst(avlothers.getCdnra().getCgst());
							}
							if(isNotEmpty(avlothers.getCdnra().getSgst())) {
								othersb2bcdna.setSgst(avlothers.getCdnra().getSgst());
							}
							if(isNotEmpty(avlothers.getCdnra().getCess())) {
								othersb2bcdna.setCess(avlothers.getCdnra().getCess());
							}
						}
						if(isNotEmpty(avlothers.getCdnrrev())) {
							if(isNotEmpty(avlothers.getCdnrrev().getIgst())) {
								othersb2bcdnr.setIgst(avlothers.getCdnrrev().getIgst());
							}
							if(isNotEmpty(avlothers.getCdnrrev().getCgst())) {
								othersb2bcdnr.setCgst(avlothers.getCdnrrev().getCgst());
							}
							if(isNotEmpty(avlothers.getCdnrrev().getSgst())) {
								othersb2bcdnr.setSgst(avlothers.getCdnrrev().getSgst());
							}
							if(isNotEmpty(avlothers.getCdnrrev().getCess())) {
								othersb2bcdnr.setCess(avlothers.getCdnrrev().getCess());
							}
						}
						if(isNotEmpty(avlothers.getCdnrarev())) {
							if(isNotEmpty(avlothers.getCdnrarev().getIgst())) {
								othersb2bcdnra.setIgst(avlothers.getCdnrarev().getIgst());
							}
							if(isNotEmpty(avlothers.getCdnrarev().getCgst())) {
								othersb2bcdnra.setCgst(avlothers.getCdnrarev().getCgst());
							}
							if(isNotEmpty(avlothers.getCdnrarev().getSgst())) {
								othersb2bcdnra.setSgst(avlothers.getCdnrarev().getSgst());
							}
							if(isNotEmpty(avlothers.getCdnrarev().getCess())) {
								othersb2bcdnra.setCess(avlothers.getCdnrarev().getCess());
							}
						}
						if(isNotEmpty(avlothers.getIsd())) {
							if(isNotEmpty(avlothers.getIsd().getIgst())) {
								othersisdcdn.setIgst(avlothers.getIsd().getIgst());
							}
							if(isNotEmpty(avlothers.getIsd().getCgst())) {
								othersisdcdn.setCgst(avlothers.getIsd().getCgst());
							}
							if(isNotEmpty(avlothers.getIsd().getSgst())) {
								othersisdcdn.setSgst(avlothers.getIsd().getSgst());
							}
							if(isNotEmpty(avlothers.getIsd().getCess())) {
								othersisdcdn.setCess(avlothers.getIsd().getCess());
							}
						}
						if(isNotEmpty(avlothers.getIsda())) {
							if(isNotEmpty(avlothers.getIsda().getIgst())) {
								othersisdcdna.setIgst(avlothers.getIsda().getIgst());
							}
							if(isNotEmpty(avlothers.getIsda().getCgst())) {
								othersisdcdna.setCgst(avlothers.getIsda().getCgst());
							}
							if(isNotEmpty(avlothers.getIsda().getSgst())) {
								othersisdcdna.setSgst(avlothers.getIsda().getSgst());
							}
							if(isNotEmpty(avlothers.getIsda().getCess())) {
								othersisdcdna.setCess(avlothers.getIsda().getCess());
							}
						}
					}
					
				
				gstr2bLst.add(mainpart);
				gstr2bLst.add(parta);
				gstr2bLst.add(nonrevsupO);
				gstr2bLst.add(nonrevsupb2b);
				gstr2bLst.add(nonrevsupb2ba);
				gstr2bLst.add(nonrevsupcdnr);
				gstr2bLst.add(nonrevsupcdnra);
				gstr2bLst.add(isdsupO);
				gstr2bLst.add(isdsupISD);
				gstr2bLst.add(isdsupISDA);
				gstr2bLst.add(revsupO);
				gstr2bLst.add(revsupb2b);
				gstr2bLst.add(revsupb2ba);
				gstr2bLst.add(revsupcdnr);
				gstr2bLst.add(revsupcdnra);
				gstr2bLst.add(itcavlimp);
				gstr2bLst.add(itcavlimpg);
				gstr2bLst.add(itcavlimpga);
				gstr2bLst.add(itcavlimpgsez);
				gstr2bLst.add(itcavlimpgseza);
				gstr2bLst.add(partb);
				gstr2bLst.add(others);
				gstr2bLst.add(othersb2bcdn);
				gstr2bLst.add(othersb2bcdna);
				gstr2bLst.add(othersb2bcdnr);
				gstr2bLst.add(othersb2bcdnra);
				gstr2bLst.add(othersisdcdn);
				gstr2bLst.add(othersisdcdna);
				return gstr2bLst;
				
				
			}
			}
		return null;
		
	}
	
@Override
	public List<GSTR2BVO> getGSTR2BITCunavlData(String clientid, String returnPeriod, GSTR2B gstr2bdata) {
	List<GSTR2BVO > gstr2bLst= new ArrayList<GSTR2BVO >();
	GSTR2BVO  mainpart= new GSTR2BVO();
	mainpart.setHeading("Credit which may not be availed under FORM GSTR-3B");
	GSTR2BVO  parta= new GSTR2BVO();
	parta.setHeading("PART-A) ITC Not Available");
	GSTR2BVO  nonrevsupO= new GSTR2BVO();
	nonrevsupO.setHeading("I) All other ITC - Supplies from registered persons other than reverse charge");
	nonrevsupO.setTableValue("NA");
	nonrevsupO.setAdvisory("Such credit shall not be taken in FORM GSTR-3B.");
	GSTR2BVO  nonrevsupb2b= new GSTR2BVO();
	nonrevsupb2b.setHeading("B2B - Invoices");
	GSTR2BVO  nonrevsupcdnr= new GSTR2BVO();
	nonrevsupcdnr.setHeading("B2B - Debit notes");
	GSTR2BVO nonrevsupb2ba= new GSTR2BVO();
	nonrevsupb2ba.setHeading("B2B - Invoices (Amendment)");
	GSTR2BVO nonrevsupcdnra= new GSTR2BVO();
	nonrevsupcdnra.setHeading("B2B - Debit notes (Amendment)");
	GSTR2BVO isdsupO= new GSTR2BVO();
	isdsupO.setHeading("II) Inward Supplies from ISD");
	isdsupO.setTableValue("NA");
	isdsupO.setAdvisory("Such credit shall not be taken in FORM GSTR-3B.");
	GSTR2BVO isdsupISD = new GSTR2BVO();
	isdsupISD.setHeading("ISD - Invoices");
	GSTR2BVO isdsupISDA= new GSTR2BVO();
	isdsupISDA.setHeading("ISD - Invoices (Amendment)");
	GSTR2BVO  revsupO= new GSTR2BVO();
	revsupO.setHeading("III) Inward Supplies liable for reverse charge");
	revsupO.setTableValue("3.1(d)");
	revsupO.setAdvisory("These supplies shall be declared in Table 3.1(d) of FORM GSTR-3B for payment of tax.");
	GSTR2BVO revsupb2b= new GSTR2BVO();
	revsupb2b.setHeading("B2B - Invoices");
	GSTR2BVO revsupcdnr= new GSTR2BVO();
	revsupcdnr.setHeading("B2B - Debit notes");
	GSTR2BVO revsupb2ba= new GSTR2BVO();
	revsupb2ba.setHeading("B2B - Invoices (Amendment)");
	GSTR2BVO revsupcdnra= new GSTR2BVO();
	revsupcdnra.setHeading("B2B - Debit notes (Amendment)");
	
	GSTR2BVO  partb= new GSTR2BVO();
	partb.setHeading("PART-B) ITC Reversal");
	GSTR2BVO  others= new GSTR2BVO();
	others.setHeading("I) Others");
	others.setTableValue("4(B)(2)");
	others.setAdvisory("Credit shall be reversed under Table 4(B)(2) of FORM GSTR-3B.");
	GSTR2BVO  othersb2bcdn = new GSTR2BVO();
	othersb2bcdn.setHeading("B2B - Credit notes");
	GSTR2BVO  othersb2bcdna = new GSTR2BVO();
	othersb2bcdna.setHeading("B2B - Credit notes (Amendment)");
	GSTR2BVO  othersb2bcdnr = new GSTR2BVO();
	othersb2bcdnr.setHeading("B2B - Credit notes (Reverse charge)");
	GSTR2BVO  othersb2bcdnra = new GSTR2BVO();
	othersb2bcdnra.setHeading("B2B - Credit notes (Reverse charge)(Amendment)");
	GSTR2BVO  othersisdcdn = new GSTR2BVO();
	othersisdcdn.setHeading("ISD - Credit notes");
	GSTR2BVO  othersisdcdna = new GSTR2BVO();
	othersisdcdna.setHeading("ISD - Credit notes (Amendment)");
	if(isNotEmpty(gstr2bdata) && isNotEmpty(gstr2bdata.getData()) && isNotEmpty(gstr2bdata.getData().getItcsumm())) {
		if(isNotEmpty(gstr2bdata.getData().getItcsumm().getItcunavl())) {
			if(isNotEmpty(gstr2bdata.getData().getItcsumm().getItcunavl().getNonrevsup())) {
				GSTR2BNonRevSup nonrevsup = gstr2bdata.getData().getItcsumm().getItcunavl().getNonrevsup();
				if(isNotEmpty(nonrevsup.getIgst())) {
					nonrevsupO.setIgst(nonrevsup.getIgst());
				}
				if(isNotEmpty(nonrevsup.getIgst())) {
					nonrevsupO.setCgst(nonrevsup.getCgst());					
				}
				if(isNotEmpty(nonrevsup.getIgst())) {
					nonrevsupO.setSgst(nonrevsup.getSgst());
				}
				if(isNotEmpty(nonrevsup.getIgst())) {
					nonrevsupO.setCess(nonrevsup.getCess());
				}
				if(isNotEmpty(nonrevsup.getB2b())) {
					if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
						nonrevsupb2b.setIgst(nonrevsup.getB2b().getIgst());
					}
					if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
						nonrevsupb2b.setCgst(nonrevsup.getB2b().getCgst());					
					}
					if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
						nonrevsupb2b.setSgst(nonrevsup.getB2b().getSgst());
					}
					if(isNotEmpty(nonrevsup.getB2b().getIgst())) {
						nonrevsupb2b.setCess(nonrevsup.getB2b().getCess());
					}
					
				}
				if(isNotEmpty(nonrevsup.getB2ba())) {
					if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
						nonrevsupb2ba.setIgst(nonrevsup.getB2ba().getIgst());
					}
					if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
						nonrevsupb2ba.setCgst(nonrevsup.getB2ba().getCgst());					
					}
					if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
						nonrevsupb2ba.setSgst(nonrevsup.getB2ba().getSgst());
					}
					if(isNotEmpty(nonrevsup.getB2ba().getIgst())) {
						nonrevsupb2ba.setCess(nonrevsup.getB2ba().getCess());
					}
				}
				if(isNotEmpty(nonrevsup.getCdnr())) {
					if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
						nonrevsupcdnr.setIgst(nonrevsup.getCdnr().getIgst());
					}
					if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
						nonrevsupcdnr.setCgst(nonrevsup.getCdnr().getCgst());					
					}
					if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
						nonrevsupcdnr.setSgst(nonrevsup.getCdnr().getSgst());
					}
					if(isNotEmpty(nonrevsup.getCdnr().getIgst())) {
						nonrevsupcdnr.setCess(nonrevsup.getCdnr().getCess());
					}
				}
				if(isNotEmpty(nonrevsup.getCdnra())) {
					if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
						nonrevsupcdnra.setIgst(nonrevsup.getCdnra().getIgst());
					}
					if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
						nonrevsupcdnra.setCgst(nonrevsup.getCdnra().getCgst());					
					}
					if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
						nonrevsupcdnra.setSgst(nonrevsup.getCdnra().getSgst());
					}
					if(isNotEmpty(nonrevsup.getCdnra().getIgst())) {
						nonrevsupcdnra.setCess(nonrevsup.getCdnra().getCess());
					}
				}
			}
				GSTR2BISDSupplies isdsup = gstr2bdata.getData().getItcsumm().getItcunavl().getIsdsup();
				if(isNotEmpty(isdsup)) {
					if(isNotEmpty(isdsup.getIgst())) {
						isdsupO.setIgst(isdsup.getIgst());
					}
					if(isNotEmpty(isdsup.getCgst())) {
						isdsupO.setCgst(isdsup.getCgst());
					}
					if(isNotEmpty(isdsup.getSgst())) {
						isdsupO.setSgst(isdsup.getSgst());
					}
					if(isNotEmpty(isdsup.getCess())) {
						isdsupO.setCess(isdsup.getCess());
					}
					
					if(isNotEmpty(isdsup.getIsd())) {
						if(isNotEmpty(isdsup.getIsd().getIgst())) {
							isdsupISD.setIgst(isdsup.getIsd().getIgst());
						}
						if(isNotEmpty(isdsup.getIsd().getCgst())) {
							isdsupISD.setCgst(isdsup.getIsd().getCgst());
						}
						if(isNotEmpty(isdsup.getIsd().getSgst())) {
							isdsupISD.setSgst(isdsup.getIsd().getSgst());
						}
						if(isNotEmpty(isdsup.getIsd().getCess())) {
							isdsupISD.setCess(isdsup.getIsd().getCess());
						}
					}
					if(isNotEmpty(isdsup.getIsda())) {
						if(isNotEmpty(isdsup.getIsda().getIgst())) {
							isdsupISDA.setIgst(isdsup.getIsda().getIgst());
						}
						if(isNotEmpty(isdsup.getIsda().getCgst())) {
							isdsupISDA.setCgst(isdsup.getIsda().getCgst());
						}
						if(isNotEmpty(isdsup.getIsda().getSgst())) {
							isdsupISDA.setSgst(isdsup.getIsda().getSgst());
						}
						if(isNotEmpty(isdsup.getIsda().getCess())) {
							isdsupISDA.setCess(isdsup.getIsda().getCess());
						}
					}
				}
				
				GSTR2BRevSupplies revsup = gstr2bdata.getData().getItcsumm().getItcunavl().getRevsup();
				if(isNotEmpty(revsup)) {
					if(isNotEmpty(revsup.getIgst())) {
						revsupO.setIgst(revsup.getIgst());
					}
					if(isNotEmpty(revsup.getCgst())) {
						revsupO.setCgst(revsup.getCgst());
					}
					if(isNotEmpty(revsup.getSgst())) {
						revsupO.setSgst(revsup.getSgst());
					}
					if(isNotEmpty(revsup.getCess())) {
						revsupO.setCess(revsup.getCess());
					}
					if(isNotEmpty(revsup.getB2b())) {
						if(isNotEmpty(revsup.getB2b().getIgst())) {
							revsupb2b.setIgst(revsup.getB2b().getIgst());
						}
						if(isNotEmpty(revsup.getB2b().getCgst())) {
							revsupb2b.setCgst(revsup.getB2b().getCgst());
						}
						if(isNotEmpty(revsup.getB2b().getSgst())) {
							revsupb2b.setSgst(revsup.getB2b().getSgst());
						}
						if(isNotEmpty(revsup.getB2b().getCess())) {
							revsupb2b.setCess(revsup.getB2b().getCess());
						}
					}
					if(isNotEmpty(revsup.getB2ba())) {
						if(isNotEmpty(revsup.getB2ba().getIgst())) {
							revsupb2ba.setIgst(revsup.getB2ba().getIgst());
						}
						if(isNotEmpty(revsup.getB2ba().getCgst())) {
							revsupb2ba.setCgst(revsup.getB2ba().getCgst());
						}
						if(isNotEmpty(revsup.getB2ba().getSgst())) {
							revsupb2ba.setSgst(revsup.getB2ba().getSgst());
						}
						if(isNotEmpty(revsup.getB2ba().getCess())) {
							revsupb2ba.setCess(revsup.getB2ba().getCess());
						}
					}
					if(isNotEmpty(revsup.getCdnr())) {
						if(isNotEmpty(revsup.getCdnr().getIgst())) {
							revsupcdnr.setIgst(revsup.getCdnr().getIgst());
						}
						if(isNotEmpty(revsup.getCdnr().getCgst())) {
							revsupcdnr.setCgst(revsup.getCdnr().getCgst());
						}
						if(isNotEmpty(revsup.getCdnr().getSgst())) {
							revsupcdnr.setSgst(revsup.getCdnr().getSgst());
						}
						if(isNotEmpty(revsup.getCdnr().getCess())) {
							revsupcdnr.setCess(revsup.getCdnr().getCess());
						}
					}
					if(isNotEmpty(revsup.getCdnra())) {
						if(isNotEmpty(revsup.getCdnra().getIgst())) {
							revsupcdnra.setIgst(revsup.getCdnra().getIgst());
						}
						if(isNotEmpty(revsup.getCdnra().getCgst())) {
							revsupcdnra.setCgst(revsup.getCdnra().getCgst());
						}
						if(isNotEmpty(revsup.getCdnra().getSgst())) {
							revsupcdnra.setSgst(revsup.getCdnra().getSgst());
						}
						if(isNotEmpty(revsup.getCdnra().getCess())) {
							revsupcdnra.setCess(revsup.getCdnra().getCess());
						}
					}
				}
				
				
				GSTR2BOthrSupplies avlothers = gstr2bdata.getData().getItcsumm().getItcunavl().getOthersup();
				if(isNotEmpty(avlothers)) {
					if(isNotEmpty(avlothers.getIgst())) {
						others.setIgst(avlothers.getIgst());
					}
					if(isNotEmpty(avlothers.getCgst())) {
						others.setCgst(avlothers.getCgst());
					}
					if(isNotEmpty(avlothers.getSgst())) {
						others.setSgst(avlothers.getSgst());
					}
					if(isNotEmpty(avlothers.getCess())) {
						others.setCess(avlothers.getCess());
					}
					if(isNotEmpty(avlothers.getCdnr())) {
						if(isNotEmpty(avlothers.getCdnr().getIgst())) {
							othersb2bcdn.setIgst(avlothers.getCdnr().getIgst());
						}
						if(isNotEmpty(avlothers.getCdnr().getCgst())) {
							othersb2bcdn.setCgst(avlothers.getCdnr().getCgst());
						}
						if(isNotEmpty(avlothers.getCdnr().getSgst())) {
							othersb2bcdn.setSgst(avlothers.getCdnr().getSgst());
						}
						if(isNotEmpty(avlothers.getCdnr().getCess())) {
							othersb2bcdn.setCess(avlothers.getCdnr().getCess());
						}
					}
					if(isNotEmpty(avlothers.getCdnra())) {
						if(isNotEmpty(avlothers.getCdnra().getIgst())) {
							othersb2bcdna.setIgst(avlothers.getCdnra().getIgst());
						}
						if(isNotEmpty(avlothers.getCdnra().getCgst())) {
							othersb2bcdna.setCgst(avlothers.getCdnra().getCgst());
						}
						if(isNotEmpty(avlothers.getCdnra().getSgst())) {
							othersb2bcdna.setSgst(avlothers.getCdnra().getSgst());
						}
						if(isNotEmpty(avlothers.getCdnra().getCess())) {
							othersb2bcdna.setCess(avlothers.getCdnra().getCess());
						}
					}
					if(isNotEmpty(avlothers.getCdnrrev())) {
						if(isNotEmpty(avlothers.getCdnrrev().getIgst())) {
							othersb2bcdnr.setIgst(avlothers.getCdnrrev().getIgst());
						}
						if(isNotEmpty(avlothers.getCdnrrev().getCgst())) {
							othersb2bcdnr.setCgst(avlothers.getCdnrrev().getCgst());
						}
						if(isNotEmpty(avlothers.getCdnrrev().getSgst())) {
							othersb2bcdnr.setSgst(avlothers.getCdnrrev().getSgst());
						}
						if(isNotEmpty(avlothers.getCdnrrev().getCess())) {
							othersb2bcdnr.setCess(avlothers.getCdnrrev().getCess());
						}
					}
					if(isNotEmpty(avlothers.getCdnrarev())) {
						if(isNotEmpty(avlothers.getCdnrarev().getIgst())) {
							othersb2bcdnra.setIgst(avlothers.getCdnrarev().getIgst());
						}
						if(isNotEmpty(avlothers.getCdnrarev().getCgst())) {
							othersb2bcdnra.setCgst(avlothers.getCdnrarev().getCgst());
						}
						if(isNotEmpty(avlothers.getCdnrarev().getSgst())) {
							othersb2bcdnra.setSgst(avlothers.getCdnrarev().getSgst());
						}
						if(isNotEmpty(avlothers.getCdnrarev().getCess())) {
							othersb2bcdnra.setCess(avlothers.getCdnrarev().getCess());
						}
					}
					if(isNotEmpty(avlothers.getIsd())) {
						if(isNotEmpty(avlothers.getIsd().getIgst())) {
							othersisdcdn.setIgst(avlothers.getIsd().getIgst());
						}
						if(isNotEmpty(avlothers.getIsd().getCgst())) {
							othersisdcdn.setCgst(avlothers.getIsd().getCgst());
						}
						if(isNotEmpty(avlothers.getIsd().getSgst())) {
							othersisdcdn.setSgst(avlothers.getIsd().getSgst());
						}
						if(isNotEmpty(avlothers.getIsd().getCess())) {
							othersisdcdn.setCess(avlothers.getIsd().getCess());
						}
					}
					if(isNotEmpty(avlothers.getIsda())) {
						if(isNotEmpty(avlothers.getIsda().getIgst())) {
							othersisdcdna.setIgst(avlothers.getIsda().getIgst());
						}
						if(isNotEmpty(avlothers.getIsda().getCgst())) {
							othersisdcdna.setCgst(avlothers.getIsda().getCgst());
						}
						if(isNotEmpty(avlothers.getIsda().getSgst())) {
							othersisdcdna.setSgst(avlothers.getIsda().getSgst());
						}
						if(isNotEmpty(avlothers.getIsda().getCess())) {
							othersisdcdna.setCess(avlothers.getIsda().getCess());
						}
					}
				}
		}
		}
	gstr2bLst.add(mainpart);
	gstr2bLst.add(parta);
	gstr2bLst.add(nonrevsupO);
	gstr2bLst.add(nonrevsupb2b);
	gstr2bLst.add(nonrevsupb2ba);
	gstr2bLst.add(nonrevsupcdnr);
	gstr2bLst.add(nonrevsupcdnra);
	gstr2bLst.add(isdsupO);
	gstr2bLst.add(isdsupISD);
	gstr2bLst.add(isdsupISDA);
	gstr2bLst.add(revsupO);
	gstr2bLst.add(revsupb2b);
	gstr2bLst.add(revsupb2ba);
	gstr2bLst.add(revsupcdnr);
	gstr2bLst.add(revsupcdnra);
	
	gstr2bLst.add(partb);
	gstr2bLst.add(others);
	gstr2bLst.add(othersb2bcdn);
	gstr2bLst.add(othersb2bcdna);
	gstr2bLst.add(othersb2bcdnr);
	gstr2bLst.add(othersb2bcdnra);
	gstr2bLst.add(othersisdcdn);
	gstr2bLst.add(othersisdcdna);
	return gstr2bLst;
	}

	public ByteArrayInputStream itcToExcel(List<GSTR2BVO> gstr2bdat,List<GSTR2BVO> gstr2bUnAvlList,List<InvoiceParent> b2bInvoicesList,List<InvoiceParent> b2baInvoicesList,List<InvoiceParent> cdnInvoicesList,List<InvoiceParent> cdnaInvoicesList,List<InvoiceParent> isdInvoicesList,List<InvoiceParent> isdaInvoicesList,List<InvoiceParent> impgInvoicesList,List<InvoiceParent> impgsezInvoicesList,String type) throws IOException {
	    String[] columns = {"Heading","GSTR-3B Table","IGST Amount","CGST Amount","SGST Amount","CESS Amount","Advisory"};
	    String[] b2bcolumns = {"Invoice Number","GSTIN of Supplier","Trade/Legal Name","Invoice Date","Place Of Supply","Supply Attract Reverse Charge","Invoice Value","Taxable Value","IGST","CGST","SGST","CESS","GSTR-1/5 Period","GSTR-1/5 Filing Date","ITC Availability","Reason","Applicable % of Tax Rate"};
	    String[] b2bacolumns = {"Invoice Number","GSTIN of Supplier","Trade/Legal Name","Invoice Date","Original Invoice Number","Original Invoice Date","Place Of Supply","Supply Attract Reverse Charge","Invoice Value","Taxable Value","IGST","CGST","SGST","CESS","GSTR-1/5 Period","GSTR-1/5 Filing Date","ITC Availability","Reason","Applicable % of Tax Rate"};
	    String[] cdncolumns = {"Document Type","Note Number","GSTIN of Supplier","Trade/Legal Name","Note Date","Place Of Supply","Supply Attract Reverse Charge","Note Value","Taxable Value","IGST","CGST","SGST","CESS","GSTR-1/5 Period","GSTR-1/5 Filing Date","ITC Availability","Reason","Applicable % of Tax Rate"};
	    String[] cdnacolumns = {"Document Type","Note Number","GSTIN of Supplier","Trade/Legal Name","Note Date","Original Note Number","Original Note Date","Place Of Supply","Supply Attract Reverse Charge","Note Value","Taxable Value","IGST","CGST","SGST","CESS","GSTR-1/5 Period","GSTR-1/5 Filing Date","ITC Availability","Reason","Applicable % of Tax Rate"};
	    
	    String[] isdcolumns = {"ISD Document type","ISD Document number","GSTIN of ISD","Trade/Legal name","ISD Document date","Original Invoice Number","Original Invoice date","IGST","CGST","SGST","CESS","ISD GSTR-6 Period","ISD GSTR-6 Filing Date","Eligibility of ITC"};
	    String[] isdacolumns = {"ISD Document type","ISD Document number","GSTIN of ISD","Trade/Legal name","ISD Document date","Original ISD Document type","Original ISD Document number","Original ISD Document date","Original Invoice Number","Original Invoice date","IGST","CGST","SGST","CESS","ISD GSTR-6 Period","ISD GSTR-6 Filing Date","Eligibility of ITC"};
	    
	    String[] impgcolumns = {"Icegate Reference Date","Received Date in the GST System","Port Code","Number","Date","Taxable Value","IGST","CGST","SGST","CESS","Amended (Yes)"};
	    String[] impgsezcolumns = {"GSTIN of Supplier","Trade/Legal Name","Icegate Reference Date","Received Date in the GST System","Port Code","Number","Date","Taxable Value","IGST","CGST","SGST","CESS","Amended (Yes)"};
	    
	    try(
	        Workbook workbook = new XSSFWorkbook();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ){
	      CreationHelper createHelper = workbook.getCreationHelper();
	      Sheet sheet = null;Sheet sheet1 = null;
	      if(isNotEmpty(type) && type.equalsIgnoreCase("gstr2b")) {
	    	  sheet = workbook.createSheet("ITC Available");
	    	  sheet1 = workbook.createSheet("ITC not available");
	      }
	      Sheet sheet2 = workbook.createSheet("B2B");
	      Sheet sheet3 = workbook.createSheet("B2BA");
	      Sheet sheet4 = workbook.createSheet("B2B-CDNR");
	      Sheet sheet5 = workbook.createSheet("B2B-CDNRA");
	      
	      Sheet sheet6 = workbook.createSheet("ISD");
	      Sheet sheet7 = workbook.createSheet("ISDA");
	      
	      Sheet sheet8 = workbook.createSheet("IMPG");
	      Sheet sheet9 = workbook.createSheet("IMPGSEZ");
	      Font headerFont = workbook.createFont();
	      headerFont.setBold(true);
	      headerFont.setColor(IndexedColors.BLUE.getIndex());
	      
	      CellStyle headerCellStyle = workbook.createCellStyle();
	      headerCellStyle.setFont(headerFont);
	      headerCellStyle.setAlignment(headerCellStyle.ALIGN_CENTER);
	      
	      // Row for Header
	      Row headerRow = null;Row headerRow1 = null;
	      if(isNotEmpty(sheet)) {
	    	  headerRow = sheet.createRow(0);
	      }
	      if(isNotEmpty(sheet1)) {
	    	  headerRow1 = sheet1.createRow(0);
	      }
	      
	      Row headerRow2 = sheet2.createRow(0);
	      Row headerRow3 = sheet3.createRow(0);
	      Row headerRow4 = sheet4.createRow(0);
	      Row headerRow5 = sheet5.createRow(0);
	      Row headerRow6 = sheet6.createRow(0);
	      Row headerRow7 = sheet7.createRow(0);
	      Row headerRow8 = sheet8.createRow(0);
	      Row headerRow9 = sheet9.createRow(0);
	      // Header
	      for (int col = 0; col < columns.length; col++) {
	    	  if(isNotEmpty(headerRow)) { 
		        Cell cell = headerRow.createCell(col);
		        cell.setCellValue(columns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet.autoSizeColumn(col);
	    	  }
	        if(isNotEmpty(headerRow1)) {
		        Cell cell1 = headerRow1.createCell(col);
		        cell1.setCellValue(columns[col]);
		        cell1.setCellStyle(headerCellStyle);
		        sheet1.autoSizeColumn(col);
	        }
	      }
	      for (int col = 0; col < b2bcolumns.length; col++) {
		        Cell cell = headerRow2.createCell(col);
		        cell.setCellValue(b2bcolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet2.autoSizeColumn(col);
	      }
	      for (int col = 0; col < b2bacolumns.length; col++) {
		        Cell cell = headerRow3.createCell(col);
		        cell.setCellValue(b2bacolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet3.autoSizeColumn(col);
	      }
	      for (int col = 0; col < cdncolumns.length; col++) {
		        Cell cell = headerRow4.createCell(col);
		        cell.setCellValue(cdncolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet4.autoSizeColumn(col);
	      }
	      for (int col = 0; col < cdnacolumns.length; col++) {
		        Cell cell = headerRow5.createCell(col);
		        cell.setCellValue(cdnacolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet5.autoSizeColumn(col);
	      }
	      
	      for (int col = 0; col < isdcolumns.length; col++) {
		        Cell cell = headerRow6.createCell(col);
		        cell.setCellValue(isdcolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet6.autoSizeColumn(col);
	      }
	      for (int col = 0; col < isdacolumns.length; col++) {
		        Cell cell = headerRow7.createCell(col);
		        cell.setCellValue(isdacolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet7.autoSizeColumn(col);
	      }
	      for (int col = 0; col < impgcolumns.length; col++) {
		        Cell cell = headerRow8.createCell(col);
		        cell.setCellValue(impgcolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet8.autoSizeColumn(col);
	      }
	      for (int col = 0; col < impgsezcolumns.length; col++) {
		        Cell cell = headerRow9.createCell(col);
		        cell.setCellValue(impgsezcolumns[col]);
		        cell.setCellStyle(headerCellStyle);
		        sheet9.autoSizeColumn(col);
	      }
	      
	      
	      int rowIdx = 1;
	      if(isNotEmpty(gstr2bdat)) {
		      for (GSTR2BVO gstr2b : gstr2bdat) {
		        Row row = sheet.createRow(rowIdx++);
		        row.createCell(0).setCellValue(gstr2b.getHeading());
		        row.createCell(1).setCellValue(gstr2b.getTableValue());
		        row.createCell(2).setCellValue(gstr2b.getIgst());
		        row.createCell(3).setCellValue(gstr2b.getCgst());
		        row.createCell(4).setCellValue(gstr2b.getSgst());
		        row.createCell(5).setCellValue(gstr2b.getCess());
		        row.createCell(6).setCellValue(gstr2b.getAdvisory());
		      }
	      }
	      int rowIdx1 = 1;
	      if(isNotEmpty(gstr2bUnAvlList)) {
		      for (GSTR2BVO gstr2b : gstr2bUnAvlList) {
		        Row row1 = sheet1.createRow(rowIdx1++);
			    row1.createCell(0).setCellValue(gstr2b.getHeading());
			    row1.createCell(1).setCellValue(gstr2b.getTableValue());
			    row1.createCell(2).setCellValue(gstr2b.getIgst());
			    row1.createCell(3).setCellValue(gstr2b.getCgst());
			    row1.createCell(4).setCellValue(gstr2b.getSgst());
			    row1.createCell(5).setCellValue(gstr2b.getCess());
			    row1.createCell(6).setCellValue(gstr2b.getAdvisory());
		      }
	      }
	      int b2bIdx = 1;
	      if(isNotEmpty(b2bInvoicesList)) {
		      for (InvoiceParent invoice : b2bInvoicesList) {
		        Row row1 = sheet2.createRow(b2bIdx++);
			    row1.createCell(0).setCellValue(invoice.getInvoiceno());
			    row1.createCell(1).setCellValue(invoice.getGstin());
			    row1.createCell(2).setCellValue(invoice.getBilledtoname());
			    row1.createCell(3).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(4).setCellValue(invoice.getStatename());
			    row1.createCell(5).setCellValue(invoice.getRevchargetype());
			    row1.createCell(6).setCellValue(invoice.getTotalamount());
			    row1.createCell(7).setCellValue(invoice.getTotaltaxableamount());
			    row1.createCell(8).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(9).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(10).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(11).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(12).setCellValue(invoice.getCustomField2());
			    row1.createCell(13).setCellValue(invoice.getCustomField1());
			    row1.createCell(14).setCellValue(invoice.getItcavl());
			    row1.createCell(15).setCellValue(invoice.getCustomField3());
			    row1.createCell(16).setCellValue(invoice.getCustomField4());
		      }
	      }
	      int b2baIdx = 1;
	      if(isNotEmpty(b2baInvoicesList)) {
		      for (InvoiceParent invoice : b2baInvoicesList) {
		        Row row1 = sheet3.createRow(b2baIdx++);
			    row1.createCell(0).setCellValue(invoice.getInvoiceno());
			    row1.createCell(1).setCellValue(invoice.getGstin());
			    row1.createCell(2).setCellValue(invoice.getBilledtoname());
			    row1.createCell(3).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(4).setCellValue(invoice.getFromGstin());
			    row1.createCell(5).setCellValue(invoice.getToGstin());
			    row1.createCell(6).setCellValue(invoice.getStatename());
			    row1.createCell(7).setCellValue(invoice.getRevchargetype());
			    row1.createCell(8).setCellValue(invoice.getTotalamount());
			    row1.createCell(9).setCellValue(invoice.getTotaltaxableamount());
			    row1.createCell(10).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(11).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(12).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(13).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(14).setCellValue(invoice.getCustomField2());
			    row1.createCell(15).setCellValue(invoice.getCustomField1());
			    row1.createCell(16).setCellValue(invoice.getItcavl());
			    row1.createCell(17).setCellValue(invoice.getCustomField3());
			    row1.createCell(18).setCellValue(invoice.getCustomField4());
		      }
	      }
	      int cdnIdx = 1;
	      if(isNotEmpty(cdnInvoicesList)) {
		      for (InvoiceParent invoice : cdnInvoicesList) {
		        Row row1 = sheet4.createRow(cdnIdx++);
		        row1.createCell(0).setCellValue(invoice.getInvtype());
			    row1.createCell(1).setCellValue(invoice.getInvoiceno());
			    row1.createCell(2).setCellValue(invoice.getGstin());
			    row1.createCell(3).setCellValue(invoice.getBilledtoname());
			    row1.createCell(4).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(5).setCellValue(invoice.getStatename());
			    row1.createCell(6).setCellValue(invoice.getRevchargetype());
			    row1.createCell(7).setCellValue(invoice.getTotalamount());
			    row1.createCell(8).setCellValue(invoice.getTotaltaxableamount());
			    row1.createCell(9).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(10).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(11).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(12).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(13).setCellValue(invoice.getCustomField2());
			    row1.createCell(14).setCellValue(invoice.getCustomField1());
			    row1.createCell(15).setCellValue(invoice.getItcavl());
			    row1.createCell(16).setCellValue(invoice.getCustomField3());
			    row1.createCell(17).setCellValue(invoice.getCustomField4());
		      }
	      }
	      int cdnaIdx = 1;
	      if(isNotEmpty(cdnaInvoicesList)) {
		      for (InvoiceParent invoice : cdnaInvoicesList) {
		        Row row1 = sheet5.createRow(cdnaIdx++);
		        row1.createCell(0).setCellValue(invoice.getInvtype());
			    row1.createCell(1).setCellValue(invoice.getInvoiceno());
			    row1.createCell(2).setCellValue(invoice.getGstin());
			    row1.createCell(3).setCellValue(invoice.getBilledtoname());
			    row1.createCell(4).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(5).setCellValue(invoice.getFromGstin());
			    row1.createCell(6).setCellValue(invoice.getToGstin());
			    row1.createCell(7).setCellValue(invoice.getStatename());
			    row1.createCell(8).setCellValue(invoice.getRevchargetype());
			    row1.createCell(9).setCellValue(invoice.getTotalamount());
			    row1.createCell(10).setCellValue(invoice.getTotaltaxableamount());
			    row1.createCell(11).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(12).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(13).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(14).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(15).setCellValue(invoice.getCustomField2());
			    row1.createCell(16).setCellValue(invoice.getCustomField1());
			    row1.createCell(17).setCellValue(invoice.getItcavl());
			    row1.createCell(18).setCellValue(invoice.getCustomField3());
			    row1.createCell(19).setCellValue(invoice.getCustomField4());
		      }
	      }
	      
	      int isdIdx = 1;
	      if(isNotEmpty(isdInvoicesList)) {
	    	  for (InvoiceParent invoice : isdInvoicesList) {
		        Row row1 = sheet6.createRow(isdIdx++);
		        row1.createCell(0).setCellValue(invoice.getDocType());
			    row1.createCell(1).setCellValue(invoice.getInvoiceno());
			    row1.createCell(2).setCellValue(invoice.getGstin());
			    row1.createCell(3).setCellValue(invoice.getBilledtoname());
			    row1.createCell(4).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(5).setCellValue(invoice.getCustomField3());
			    row1.createCell(6).setCellValue(invoice.getCustomField4());
			    row1.createCell(7).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(8).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(9).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(10).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(11).setCellValue(invoice.getCustomField2());
			    row1.createCell(12).setCellValue(invoice.getCustomField1());
			    row1.createCell(13).setCellValue(invoice.getItcavl());
		      }
	      }
		      int isdaIdx = 1;
		  if(isNotEmpty(isdaInvoicesList)) {
		      for (InvoiceParent invoice : isdaInvoicesList) {
		        Row row1 = sheet7.createRow(isdaIdx++);
		        row1.createCell(0).setCellValue(invoice.getDocType());
			    row1.createCell(1).setCellValue(invoice.getInvoiceno());
			    row1.createCell(2).setCellValue(invoice.getGstin());
			    row1.createCell(3).setCellValue(invoice.getBilledtoname());
			    row1.createCell(4).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
			    row1.createCell(5).setCellValue(invoice.getFromGstin());
			    row1.createCell(6).setCellValue(invoice.getToGstin());
			    row1.createCell(7).setCellValue(invoice.getAckDt());
			    row1.createCell(8).setCellValue(invoice.getCustomField3());
			    row1.createCell(9).setCellValue(invoice.getCustomField4());
			    row1.createCell(10).setCellValue(invoice.getTotalIgstAmount());
			    row1.createCell(11).setCellValue(invoice.getTotalCgstAmount());
			    row1.createCell(12).setCellValue(invoice.getTotalSgstAmount());
			    row1.createCell(13).setCellValue(invoice.getTotalCessAmount());
			    row1.createCell(14).setCellValue(invoice.getCustomField2());
			    row1.createCell(15).setCellValue(invoice.getCustomField1());
			    row1.createCell(16).setCellValue(invoice.getItcavl());
		      }
		  }
		      int impgIdx = 1;
		      if(isNotEmpty(impgInvoicesList)) {
			      for (InvoiceParent invoice : impgInvoicesList) {
			        Row row1 = sheet8.createRow(impgIdx++);
				    row1.createCell(0).setCellValue(invoice.getCustomField4());
				    row1.createCell(1).setCellValue(invoice.getCustomField2());
				    row1.createCell(2).setCellValue(invoice.getCustomField1());
				    row1.createCell(3).setCellValue(invoice.getInvoiceno());
				    row1.createCell(4).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
				    row1.createCell(5).setCellValue(invoice.getTotaltaxableamount());
				    row1.createCell(6).setCellValue(invoice.getTotalIgstAmount());
				    row1.createCell(7).setCellValue(invoice.getTotalCgstAmount());
				    row1.createCell(8).setCellValue(invoice.getTotalSgstAmount());
				    row1.createCell(9).setCellValue(invoice.getTotalCessAmount());
				    row1.createCell(10).setCellValue(invoice.getCustomField3());
			      }
		      }
		      int impgsezIdx = 1;
		      if(isNotEmpty(impgsezInvoicesList)) {
			      for (InvoiceParent invoice : impgsezInvoicesList) {
			        Row row1 = sheet9.createRow(impgsezIdx++);
			        row1.createCell(0).setCellValue(invoice.getGstin());
				    row1.createCell(1).setCellValue(invoice.getBilledtoname());
			        row1.createCell(2).setCellValue(invoice.getCustomField4());
				    row1.createCell(3).setCellValue(invoice.getCustomField2());
				    row1.createCell(4).setCellValue(invoice.getCustomField1());
				    row1.createCell(5).setCellValue(invoice.getInvoiceno());
				    row1.createCell(6).setCellValue(Utility.getFormatedDateStr(invoice.getDateofinvoice()));
				    row1.createCell(7).setCellValue(invoice.getTotaltaxableamount());
				    row1.createCell(8).setCellValue(invoice.getTotalIgstAmount());
				    row1.createCell(9).setCellValue(invoice.getTotalCgstAmount());
				    row1.createCell(10).setCellValue(invoice.getTotalSgstAmount());
				    row1.createCell(11).setCellValue(invoice.getTotalCessAmount());
				    row1.createCell(12).setCellValue(invoice.getCustomField3());
			      }
		      }
	      
	      //Setting Auto Column Width
		      if(isNotEmpty(sheet)) {
		    	  sheet.autoSizeColumn(0);
		    	  sheet.setColumnWidth(0,50 * 256);
		    	  sheet.autoSizeColumn(6);
		           sheet.setColumnWidth(6,50 * 256);
		      }
		      if(isNotEmpty(sheet1)) {
		    	  sheet1.autoSizeColumn(0);
		    	  sheet1.setColumnWidth(0,50 * 256);
		    	  sheet1.autoSizeColumn(6);
		           sheet1.setColumnWidth(6,50 * 256);
		      }
           sheet2.autoSizeColumn(0);
           sheet2.setColumnWidth(0,50 * 256);
           
           sheet2.autoSizeColumn(6);
           sheet2.setColumnWidth(6,50 * 256);
	      workbook.write(out);
	      return new ByteArrayInputStream(out.toByteArray());
	    }
	  }
	
	@Override
	public Map<String, Object> getITCReportParams(Client client, List<GSTR2BVO> gstr2bavlList,List<GSTR2BVO> gstr2bunavlList) {
		Map<String, Object> params = Maps.newHashMap();
		if(isNotEmpty(client.getGstnnumber())) {
			params.put("clientgstno", client.getGstnnumber());
		}else {
			params.put("clientgstno", "");
		}
		if(isNotEmpty(client.getBusinessname())) {
			params.put("clientname", client.getBusinessname());
		}else {
			params.put("clientname", client.getBusinessname());
		}
		params.put("generationDate", "");
		return params;
	}
	
	@Override
	public GSTR2BResponse syncGstr2bInvoice(Client client, String userid, String returntype, String returnPeriod, boolean initial) {
		logger.debug(CLASSNAME + "syncInvoiceData : Begin");
		logger.debug(CLASSNAME + "syncInvoiceData : returnType {}", returntype);
		logger.debug(CLASSNAME + "syncInvoiceData : returnPeriod {}", returnPeriod);
		GSTR2B invoice = null;	
		GSTR2BResponse gstr2bresponse = null;
		if (isNotEmpty(client)) {
			try {
				String dwndstatus = "NODATA_FOUND";
				gstr2bresponse = iHubConsumerService.getGstr2bInvoice(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,0);
				if(isNotEmpty(gstr2bresponse) && isNotEmpty(gstr2bresponse.getGstr2b())) {
					invoice = gstr2bresponse.getGstr2b();
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getData()) && isNotEmpty(invoice.getData().getFc())) {
						if(invoice.getData().getFc() > 0) {
							processGstr2bData(invoice, client, userid, returntype, returnPeriod);
							for(int i = 1;i<= invoice.getData().getFc(); i++) {
								GSTR2BResponse gstr2bInvoicebyfilenum = iHubConsumerService.getGstr2bInvoice(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,i);
								if(isNotEmpty(gstr2bInvoicebyfilenum) && isNotEmpty(gstr2bInvoicebyfilenum.getGstr2b())) {
									processGstr2bDataByFileNum(gstr2bInvoicebyfilenum.getGstr2b(), client, userid, returntype, returnPeriod);
								}
							}
						}else {
							processGstr2bData(invoice, client, userid, returntype, returnPeriod);
						}
					}else {
						processGstr2bData(invoice, client, userid, returntype, returnPeriod);
					}
					//processGstr2bData(invoice, client, userid, returntype, returnPeriod);
					dwndstatus = "NODATA_FOUND";
				}
				String strMonth = returnPeriod.substring(0,2);
				String strYear = returnPeriod.substring(2);
				List<ReturnsDownloadStatus> downloadStatus = returnsDownloadStatusService.getReturnsDownloadStatus(userid, client.getId().toString(), returntype, returnPeriod);
				
				returnsDownloadStatusService.deleteReturnDownloadStatus(downloadStatus);
				
				ReturnsDownloadStatus dwnldstatus = new ReturnsDownloadStatus();
				
				dwnldstatus.setUserid(userid);
				dwnldstatus.setClientid(client.getId().toString());
				dwnldstatus.setReturnperiod(returnPeriod);
				dwnldstatus.setStatus("DOWNLOAD");
				dwnldstatus.setFinancialyear(strYear);
				dwnldstatus.setCurrrentmonth(strMonth);
				dwnldstatus.setReturntype(MasterGSTConstants.GSTR2B);
				if("DATA_FOUND".equals(dwndstatus)) {
					dwnldstatus.setInvoicedata("invoices available");
				}else {
					dwnldstatus.setInvoicedata("invoices not available");
				}
				returnsDownloadStatusService.savedownloadstatus(dwnldstatus);
			} catch(Exception e) {
				logger.debug(CLASSNAME + "syncInvoiceData : ERROR", e);
			}
		}
		return gstr2bresponse;
	}
	
	@Override
	public ReturnsDownloadStatus downloadGstr2bInvoice(Client client, String userid, String returntype, String returnPeriod, boolean initial) {
		logger.debug(CLASSNAME + "syncInvoiceData : Begin");
		logger.debug(CLASSNAME + "syncInvoiceData : returnType {}", returntype);
		logger.debug(CLASSNAME + "syncInvoiceData : returnPeriod {}", returnPeriod);
		GSTR2B invoice = null;	
		GSTR2BResponse gstr2bresponse = null;
		ReturnsDownloadStatus dwnldstatus = new ReturnsDownloadStatus();
		
		String strMonth = returnPeriod.substring(0,2);
		String strYear = returnPeriod.substring(2);
		dwnldstatus.setUserid(userid);
		dwnldstatus.setClientid(client.getId().toString());
		dwnldstatus.setReturnperiod(returnPeriod);
		dwnldstatus.setStatus("DOWNLOAD");
		dwnldstatus.setFinancialyear(strYear);
		dwnldstatus.setCurrrentmonth(strMonth);
		dwnldstatus.setReturntype(MasterGSTConstants.GSTR2B);
		if (isNotEmpty(client)) {
			try {
				String dwndstatus = "NODATA_FOUND";
				gstr2bresponse = iHubConsumerService.getGstr2bInvoice(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,0);
				if(isNotEmpty(gstr2bresponse) && isNotEmpty(gstr2bresponse.getGstr2b())) {
					invoice = gstr2bresponse.getGstr2b();
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getData()) && isNotEmpty(invoice.getData().getFc())) {
						if(invoice.getData().getFc() > 0) {
							processGstr2bData(invoice, client, userid, returntype, returnPeriod);
							for(int i = 1;i<= invoice.getData().getFc(); i++) {
								GSTR2BResponse gstr2bInvoicebyfilenum = iHubConsumerService.getGstr2bInvoice(client, client.getGstnnumber(), userid, returnPeriod, returntype, false,i);
								if(isNotEmpty(gstr2bInvoicebyfilenum) && isNotEmpty(gstr2bInvoicebyfilenum.getGstr2b())) {
										processGstr2bDataByFileNum(gstr2bInvoicebyfilenum.getGstr2b(), client, userid, returntype, returnPeriod);
								}
							}
						}else {
							processGstr2bData(invoice, client, userid, returntype, returnPeriod);
						}
					}else {
						processGstr2bData(invoice, client, userid, returntype, returnPeriod);
					}
					//processGstr2bData(invoice, client, userid, returntype, returnPeriod);
					dwndstatus = "DATA_FOUND";
				}
				List<ReturnsDownloadStatus> downloadStatus = returnsDownloadStatusService.getReturnsDownloadStatus(userid, client.getId().toString(), returntype, returnPeriod);
				
				returnsDownloadStatusService.deleteReturnDownloadStatus(downloadStatus);
				
				if("DATA_FOUND".equals(dwndstatus)) {
					dwnldstatus.setInvoicedata("invoices available");
				}else {
					dwnldstatus.setInvoicedata("invoices not available");
				}
				return returnsDownloadStatusService.savedownloadstatus(dwnldstatus);
			} catch(Exception e) {
				logger.debug(CLASSNAME + "syncInvoiceData : ERROR", e);
			}
		}
		
		return dwnldstatus;
	}
	public ByteArrayInputStream gstr2aToExcel(List<InvoiceVO> InvoicesList,List<InvoiceVO> ammendedInvoicesList,List<String> headers,String dwnldxlsyearlytype) throws IOException {
	    
	    try(
	        Workbook workbook = new XSSFWorkbook();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ){
	      CreationHelper createHelper = workbook.getCreationHelper();
	      Sheet sheet = workbook.createSheet("Invoices");
	      Sheet sheet1 = workbook.createSheet("Ammended Invoices");
	      
	      Font headerFont = workbook.createFont();
	      headerFont.setBold(true);
	      headerFont.setColor(IndexedColors.BLUE.getIndex());
	      
	      CellStyle headerCellStyle = workbook.createCellStyle();
	      headerCellStyle.setFont(headerFont);
	      headerCellStyle.setAlignment(headerCellStyle.ALIGN_CENTER);
	      
	      // Row for Header
	      Row headerRow = sheet.createRow(0);
	      Row headerRow1 = sheet1.createRow(0);
	      
	      // Header
	      for (int col = 0; col < headers.size(); col++) {
	        Cell cell = headerRow.createCell(col);
	        cell.setCellValue(headers.get(col));
	        cell.setCellStyle(headerCellStyle);
	        sheet.autoSizeColumn(col);
	        
	        Cell cell1 = headerRow1.createCell(col);
	        cell1.setCellValue(headers.get(col));
	        cell1.setCellStyle(headerCellStyle);
	        sheet1.autoSizeColumn(col);
	      }
	       if("invoicewise".equalsIgnoreCase(dwnldxlsyearlytype)) {
	    	   int b2bIdx = 1;
	    	   if(isNotEmpty(InvoicesList)) {
	 		      for (InvoiceVO invoice : InvoicesList) {
	 		    	 Row row1 = sheet.createRow(b2bIdx++);
	 		    	row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
				    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
				    row1.createCell(2).setCellValue(invoice.getCustomerGSTIN());
				    row1.createCell(3).setCellValue(invoice.getPlaceOfSupply());
				    row1.createCell(4).setCellValue(invoice.getCustomerName());
				    row1.createCell(5).setCellValue(invoice.getCompanyGSTIN());
				    row1.createCell(6).setCellValue(invoice.getCompanyStatename());
				    row1.createCell(7).setCellValue(invoice.getCounterFilingStatus());
				    row1.createCell(8).setCellValue(invoice.getOriginalInvoiceNo());
				    row1.createCell(9).setCellValue(invoice.getOriginalInvoiceDate());
				    row1.createCell(10).setCellValue(invoice.getReturnPeriod());
				    row1.createCell(11).setCellValue(invoice.getRecharge());
				    row1.createCell(12).setCellValue(invoice.getType());
				    row1.createCell(13).setCellValue(invoice.getState());
				    row1.createCell(14).setCellValue(invoice.getBranch());
				    if(isNotEmpty(invoice.getTaxableValue())) {
				    	row1.createCell(15).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
				    }else {
				    	row1.createCell(15).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getIgstAmount())) {
				    	row1.createCell(16).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
				    }else {
				    	row1.createCell(16).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getCgstAmount())) {
				    	row1.createCell(17).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
				    }else {
				    	row1.createCell(17).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getSgstAmount())) {
				    	row1.createCell(18).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
				    }else {
				    	row1.createCell(18).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getCessAmount())) {
				    	row1.createCell(19).setCellValue(decimalFormat.format(invoice.getCessAmount()));
				    }else {
				    	row1.createCell(19).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getTotalItc())) {
				    	row1.createCell(20).setCellValue(decimalFormat.format(invoice.getTotalItc()));
				    }else {
				    	row1.createCell(20).setCellValue("");
				    }
				    if(isNotEmpty(invoice.getTotaltax())) {
				    	row1.createCell(21).setCellValue(decimalFormat.format(invoice.getTotaltax()));
				    }else {
				    	row1.createCell(21).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getTotalValue())) {
				    	row1.createCell(22).setCellValue(decimalFormat.format(invoice.getTotalValue()));
				    }else {
				    	row1.createCell(22).setCellValue(decimalFormat.format(0d));
				    }
	 		      }
	    	   } 
	    	   int ammendedIdx = 1;
	 	      if(isNotEmpty(ammendedInvoicesList)) {
	 		      for (InvoiceVO invoice : ammendedInvoicesList) {
	 		    	 Row row1 = sheet1.createRow(ammendedIdx++);
	 		    	row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
				    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
				    row1.createCell(2).setCellValue(invoice.getCustomerGSTIN());
				    row1.createCell(3).setCellValue(invoice.getPlaceOfSupply());
				    row1.createCell(4).setCellValue(invoice.getCustomerName());
				    row1.createCell(5).setCellValue(invoice.getCompanyGSTIN());
				    row1.createCell(6).setCellValue(invoice.getCompanyStatename());
				    row1.createCell(7).setCellValue(invoice.getCounterFilingStatus());
				    row1.createCell(8).setCellValue(invoice.getOriginalInvoiceNo());
				    row1.createCell(9).setCellValue(invoice.getOriginalInvoiceDate());
				    row1.createCell(10).setCellValue(invoice.getReturnPeriod());
				    row1.createCell(11).setCellValue(invoice.getRecharge());
				    row1.createCell(12).setCellValue(invoice.getType());
				    row1.createCell(13).setCellValue(invoice.getState());
				    row1.createCell(14).setCellValue(invoice.getBranch());
				    if(isNotEmpty(invoice.getTaxableValue())) {
				    	row1.createCell(15).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
				    }else {
				    	row1.createCell(15).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getIgstAmount())) {
				    	row1.createCell(16).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
				    }else {
				    	row1.createCell(16).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getCgstAmount())) {
				    	row1.createCell(17).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
				    }else {
				    	row1.createCell(17).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getSgstAmount())) {
				    	row1.createCell(18).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
				    }else {
				    	row1.createCell(18).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getCessAmount())) {
				    	row1.createCell(19).setCellValue(decimalFormat.format(invoice.getCessAmount()));
				    }else {
				    	row1.createCell(19).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getTotalItc())) {
				    	row1.createCell(20).setCellValue(decimalFormat.format(invoice.getTotalItc()));
				    }else {
				    	row1.createCell(20).setCellValue("");
				    }
				    if(isNotEmpty(invoice.getTotaltax())) {
				    	row1.createCell(21).setCellValue(decimalFormat.format(invoice.getTotaltax()));
				    }else {
				    	row1.createCell(21).setCellValue(decimalFormat.format(0d));
				    }
				    if(isNotEmpty(invoice.getTotalValue())) {
				    	row1.createCell(22).setCellValue(decimalFormat.format(invoice.getTotalValue()));
				    }else {
				    	row1.createCell(22).setCellValue(decimalFormat.format(0d));
				    }
	 		      }
	    	   }
	       }else if("itemwise".equalsIgnoreCase(dwnldxlsyearlytype)) {
	      int b2bIdx = 1;
	      if(isNotEmpty(InvoicesList)) {
		      for (InvoiceVO invoice : InvoicesList) {
		        Row row1 = sheet.createRow(b2bIdx++);
			    row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
			    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
			    row1.createCell(2).setCellValue(invoice.getCustomerGSTIN());
			    row1.createCell(3).setCellValue(invoice.getPlaceOfSupply());
			    row1.createCell(4).setCellValue(invoice.getCustomerName());
			    row1.createCell(5).setCellValue(invoice.getCompanyGSTIN());
			    row1.createCell(6).setCellValue(invoice.getCompanyStatename());
			    row1.createCell(7).setCellValue(invoice.getCounterFilingStatus());
			    row1.createCell(8).setCellValue(invoice.getOriginalInvoiceNo());
			    row1.createCell(9).setCellValue(invoice.getOriginalInvoiceDate());
			    row1.createCell(10).setCellValue(invoice.getReturnPeriod());
			    row1.createCell(11).setCellValue(invoice.getRecharge());
			    row1.createCell(12).setCellValue(invoice.getType());
			    row1.createCell(13).setCellValue(invoice.getState());
			    row1.createCell(14).setCellValue(invoice.getItemname());
			    row1.createCell(15).setCellValue(invoice.getItemNotesComments());
			    row1.createCell(16).setCellValue(invoice.getHsnCode());
			    row1.createCell(17).setCellValue(invoice.getUqc());
			    if(isNotEmpty(invoice.getQuantity())) {
			    	row1.createCell(18).setCellValue(decimalFormat.format(invoice.getQuantity()));
			    }else {
			    	row1.createCell(18).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getRateperitem())) {
			    	row1.createCell(19).setCellValue(decimalFormat.format(invoice.getRateperitem()));
			    }else {
			    	row1.createCell(19).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getTaxableValue())) {
			    	row1.createCell(20).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
			    }else {
			    	row1.createCell(20).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getIgstRate())) {
			    	row1.createCell(21).setCellValue(decimalFormat.format(invoice.getIgstRate()));
			    }else {
			    	row1.createCell(21).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getIgstAmount())) {
			    	row1.createCell(22).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
			    }else {
			    	row1.createCell(22).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCgstRate())) {
			    	row1.createCell(23).setCellValue(decimalFormat.format(invoice.getCgstRate()));
			    }else {
			    	row1.createCell(23).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCgstAmount())) {
			    	row1.createCell(24).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
			    }else {
			    	row1.createCell(24).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getSgstRate())) {
			    	row1.createCell(25).setCellValue(decimalFormat.format(invoice.getSgstRate()));
			    }else {
			    	row1.createCell(25).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getSgstAmount())) {
			    	row1.createCell(26).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
			    }else {
			    	row1.createCell(26).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCessRate())) {
			    	row1.createCell(27).setCellValue(decimalFormat.format(invoice.getCessRate()));
			    }else {
			    	row1.createCell(27).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCessAmount())) {
			    	row1.createCell(28).setCellValue(decimalFormat.format(invoice.getCessAmount()));
			    }else {
			    	row1.createCell(28).setCellValue(decimalFormat.format(0d));
			    }
			    row1.createCell(29).setCellValue(invoice.getItcType());
			    if(isNotEmpty(invoice.getTotalItc())) {
			    	row1.createCell(30).setCellValue(decimalFormat.format(invoice.getTotalItc()));
			    }else {
			    	row1.createCell(30).setCellValue("");
			    }
			    if(isNotEmpty(invoice.getTotaltax())) {
			    	row1.createCell(31).setCellValue(decimalFormat.format(invoice.getTotaltax()));
			    }else {
			    	row1.createCell(31).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getTotalValue())) {
			    	row1.createCell(32).setCellValue(decimalFormat.format(invoice.getTotalValue()));
			    }else {
			    	row1.createCell(32).setCellValue(decimalFormat.format(0d));
			    }
		      }
	      }
	      
	      
	      int ammendedIdx = 1;
	      if(isNotEmpty(ammendedInvoicesList)) {
		      for (InvoiceVO invoice : ammendedInvoicesList) {
		        Row row1 = sheet1.createRow(ammendedIdx++);
			    row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
			    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
			    row1.createCell(2).setCellValue(invoice.getCustomerGSTIN());
			    row1.createCell(3).setCellValue(invoice.getPlaceOfSupply());
			    row1.createCell(4).setCellValue(invoice.getCustomerName());
			    row1.createCell(5).setCellValue(invoice.getCompanyGSTIN());
			    row1.createCell(6).setCellValue(invoice.getCompanyStatename());
			    row1.createCell(7).setCellValue(invoice.getCounterFilingStatus());
			    row1.createCell(8).setCellValue(invoice.getOriginalInvoiceNo());
			    row1.createCell(9).setCellValue(invoice.getOriginalInvoiceDate());
			    row1.createCell(10).setCellValue(invoice.getReturnPeriod());
			    row1.createCell(11).setCellValue(invoice.getRecharge());
			    row1.createCell(12).setCellValue(invoice.getType());
			    row1.createCell(13).setCellValue(invoice.getState());
			    row1.createCell(14).setCellValue(invoice.getItemname());
			    row1.createCell(15).setCellValue(invoice.getItemNotesComments());
			    row1.createCell(16).setCellValue(invoice.getHsnCode());
			    row1.createCell(17).setCellValue(invoice.getUqc());
			    if(isNotEmpty(invoice.getQuantity())) {
			    	row1.createCell(18).setCellValue(decimalFormat.format(invoice.getQuantity()));
			    }else {
			    	row1.createCell(18).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getRateperitem())) {
			    	row1.createCell(19).setCellValue(decimalFormat.format(invoice.getRateperitem()));
			    }else {
			    	row1.createCell(19).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getTaxableValue())) {
			    	row1.createCell(20).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
			    }else {
			    	row1.createCell(20).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getIgstRate())) {
			    	row1.createCell(21).setCellValue(decimalFormat.format(invoice.getIgstRate()));
			    }else {
			    	row1.createCell(21).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getIgstAmount())) {
			    	row1.createCell(22).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
			    }else {
			    	row1.createCell(22).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCgstRate())) {
			    	row1.createCell(23).setCellValue(decimalFormat.format(invoice.getCgstRate()));
			    }else {
			    	row1.createCell(23).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCgstAmount())) {
			    	row1.createCell(24).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
			    }else {
			    	row1.createCell(24).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getSgstRate())) {
			    	row1.createCell(25).setCellValue(decimalFormat.format(invoice.getSgstRate()));
			    }else {
			    	row1.createCell(25).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getSgstAmount())) {
			    	row1.createCell(26).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
			    }else {
			    	row1.createCell(26).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCessRate())) {
			    	row1.createCell(27).setCellValue(decimalFormat.format(invoice.getCessRate()));
			    }else {
			    	row1.createCell(27).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getCessAmount())) {
			    	row1.createCell(28).setCellValue(decimalFormat.format(invoice.getCessAmount()));
			    }else {
			    	row1.createCell(28).setCellValue(decimalFormat.format(0d));
			    }
			    row1.createCell(29).setCellValue(invoice.getItcType());
			    if(isNotEmpty(invoice.getTotalItc())) {
			    	row1.createCell(30).setCellValue(decimalFormat.format(invoice.getTotalItc()));
			    }else {
			    	row1.createCell(30).setCellValue("");
			    }
			    if(isNotEmpty(invoice.getTotaltax())) {
			    	row1.createCell(31).setCellValue(decimalFormat.format(invoice.getTotaltax()));
			    }else {
			    	row1.createCell(31).setCellValue(decimalFormat.format(0d));
			    }
			    if(isNotEmpty(invoice.getTotalValue())) {
			    	row1.createCell(32).setCellValue(decimalFormat.format(invoice.getTotalValue()));
			    }else {
			    	row1.createCell(32).setCellValue(decimalFormat.format(0d));
			    }
		      }
	      }
	       }else {
	    	   int b2bIdx = 1;
	 	      if(isNotEmpty(InvoicesList)) {
	 		      for (InvoiceVO invoice : InvoicesList) {
	 		        Row row1 = sheet.createRow(b2bIdx++);
	 			    row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
	 			    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
	 			    row1.createCell(2).setCellValue(invoice.getType());
	 			    row1.createCell(3).setCellValue(invoice.getReturnPeriod());
	 			    row1.createCell(4).setCellValue(invoice.getRecharge());
	 			    row1.createCell(5).setCellValue(invoice.getCustomerGSTIN());
	 			    row1.createCell(6).setCellValue(invoice.getPlaceOfSupply());
	 			    row1.createCell(7).setCellValue(invoice.getCustomerName());
	 			    row1.createCell(8).setCellValue(invoice.getCompanyStatename());
	 			    row1.createCell(9).setCellValue(invoice.getCounterFilingStatus());
	 			    row1.createCell(10).setCellValue(invoice.getEcommerceGSTIN());
	 			    row1.createCell(11).setCellValue(invoice.getBillingAddress());
	 			    row1.createCell(12).setCellValue(invoice.getShipingAddress());
	 			    row1.createCell(13).setCellValue(invoice.getOriginalInvoiceNo());
		 			row1.createCell(14).setCellValue(invoice.getOriginalInvoiceDate());
		 			row1.createCell(15).setCellValue(invoice.getEwayBillNumber());
		 			row1.createCell(16).setCellValue(invoice.getLedgerName());
		 			row1.createCell(17).setCellValue(invoice.getState());
		 			row1.createCell(18).setCellValue(invoice.getReverseCharge());
		 			row1.createCell(19).setCellValue(invoice.getReference());
		 			row1.createCell(20).setCellValue(invoice.getBranch());
		 			row1.createCell(21).setCellValue(invoice.getVertical());
		 			row1.createCell(22).setCellValue(invoice.getDifferentialPercentage());
		 			row1.createCell(23).setCellValue(invoice.getAddTCS());
		 			row1.createCell(24).setCellValue(invoice.getTcsSection());
		 			row1.createCell(25).setCellValue(invoice.getTcsPercentage());
		 			row1.createCell(26).setCellValue(invoice.getBankName());
		 			row1.createCell(27).setCellValue(invoice.getAccountNumber());
		 			row1.createCell(28).setCellValue(invoice.getAccountName());
		 			row1.createCell(29).setCellValue(invoice.getBranchName());
		 			row1.createCell(30).setCellValue(invoice.getIfsccode());
		 			row1.createCell(31).setCellValue(invoice.getCustomerNotes());
		 			row1.createCell(32).setCellValue(invoice.getTermsAndConditions());
		 			row1.createCell(33).setCellValue(invoice.getCustomField1());
		 			row1.createCell(34).setCellValue(invoice.getCustomField2());
		 			row1.createCell(35).setCellValue(invoice.getCustomField3());
		 			row1.createCell(36).setCellValue(invoice.getCustomField4());
		 			row1.createCell(37).setCellValue(invoice.getItemno());
	 			    row1.createCell(38).setCellValue(invoice.getItemNotesComments());
	 			    row1.createCell(39).setCellValue(invoice.getHsnCode());
	 			    row1.createCell(40).setCellValue(invoice.getUqc());
	 			    if(isNotEmpty(invoice.getQuantity())) {
	 			    	row1.createCell(41).setCellValue(decimalFormat.format(invoice.getQuantity()));
	 			    }else {
	 			    	row1.createCell(41).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getRateperitem())) {
	 			    	row1.createCell(42).setCellValue(decimalFormat.format(invoice.getRateperitem()));
	 			    }else {
	 			    	row1.createCell(42).setCellValue(decimalFormat.format(0d));
	 			    }
   	 			    if(isNotEmpty(invoice.getItemDiscount())) {
	 			    	row1.createCell(43).setCellValue(decimalFormat.format(invoice.getItemDiscount()));
	 			    }else {
	 			    	row1.createCell(43).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTaxableValue())) {
	 			    	row1.createCell(44).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
	 			    }else {
	 			    	row1.createCell(44).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getIgstRate())) {
	 			    	row1.createCell(45).setCellValue(decimalFormat.format(invoice.getIgstRate()));
	 			    }else {
	 			    	row1.createCell(45).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getIgstAmount())) {
	 			    	row1.createCell(46).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
	 			    }else {
	 			    	row1.createCell(46).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCgstRate())) {
	 			    	row1.createCell(47).setCellValue(decimalFormat.format(invoice.getCgstRate()));
	 			    }else {
	 			    	row1.createCell(47).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCgstAmount())) {
	 			    	row1.createCell(48).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
	 			    }else {
	 			    	row1.createCell(48).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getSgstRate())) {
	 			    	row1.createCell(49).setCellValue(decimalFormat.format(invoice.getSgstRate()));
	 			    }else {
	 			    	row1.createCell(49).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getSgstAmount())) {
	 			    	row1.createCell(50).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
	 			    }else {
	 			    	row1.createCell(50).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCessRate())) {
	 			    	row1.createCell(51).setCellValue(decimalFormat.format(invoice.getCessRate()));
	 			    }else {
	 			    	row1.createCell(51).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCessAmount())) {
	 			    	row1.createCell(52).setCellValue(decimalFormat.format(invoice.getCessAmount()));
	 			    }else {
	 			    	row1.createCell(52).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    row1.createCell(53).setCellValue(invoice.getItcType());
	 			    if(isNotEmpty(invoice.getTotalItc())) {
	 			    	row1.createCell(54).setCellValue(decimalFormat.format(invoice.getTotalItc()));
	 			    }else {
	 			    	row1.createCell(54).setCellValue("");
	 			    }
	 			    if(isNotEmpty(invoice.getTotaltax())) {
	 			    	row1.createCell(55).setCellValue(decimalFormat.format(invoice.getTotaltax()));
	 			    }else {
	 			    	row1.createCell(55).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTotalValue())) {
	 			    	row1.createCell(56).setCellValue(decimalFormat.format(invoice.getTotalValue()));
	 			    }else {
	 			    	row1.createCell(56).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTcsAmount())) {
	 			    	row1.createCell(57).setCellValue(decimalFormat.format(invoice.getTcsAmount()));
	 			    }else {
	 			    	row1.createCell(57).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTcsNetAmount())) {
	 			    	row1.createCell(58).setCellValue(decimalFormat.format(invoice.getTcsNetAmount()));
	 			    }else {
	 			    	row1.createCell(58).setCellValue(decimalFormat.format(0d));
	 			    }
	 		      }
	 	      }
	 	      int ammendedIdx = 1;
	 	     if(isNotEmpty(ammendedInvoicesList)) {
	 		      for (InvoiceVO invoice : ammendedInvoicesList) {
	 		        Row row1 = sheet1.createRow(ammendedIdx++);
	 			    row1.createCell(0).setCellValue(Utility.getFormatedDateStr(invoice.getInvoiceDate()));
	 			    row1.createCell(1).setCellValue(invoice.getInvoiceNo());
	 			    row1.createCell(2).setCellValue(invoice.getType());
	 			    row1.createCell(3).setCellValue(invoice.getReturnPeriod());
	 			    row1.createCell(4).setCellValue(invoice.getRecharge());
	 			    row1.createCell(5).setCellValue(invoice.getCustomerGSTIN());
	 			    row1.createCell(6).setCellValue(invoice.getPlaceOfSupply());
	 			    row1.createCell(7).setCellValue(invoice.getCustomerName());
	 			    row1.createCell(8).setCellValue(invoice.getCompanyStatename());
	 			    row1.createCell(9).setCellValue(invoice.getCounterFilingStatus());
	 			    row1.createCell(10).setCellValue(invoice.getEcommerceGSTIN());
	 			    row1.createCell(11).setCellValue(invoice.getBillingAddress());
	 			    row1.createCell(12).setCellValue(invoice.getShipingAddress());
	 			    row1.createCell(13).setCellValue(invoice.getOriginalInvoiceNo());
		 			row1.createCell(14).setCellValue(invoice.getOriginalInvoiceDate());
		 			row1.createCell(15).setCellValue(invoice.getEwayBillNumber());
		 			row1.createCell(16).setCellValue(invoice.getLedgerName());
		 			row1.createCell(17).setCellValue(invoice.getState());
		 			row1.createCell(18).setCellValue(invoice.getReverseCharge());
		 			row1.createCell(19).setCellValue(invoice.getReference());
		 			row1.createCell(20).setCellValue(invoice.getBranch());
		 			row1.createCell(21).setCellValue(invoice.getVertical());
		 			row1.createCell(22).setCellValue(invoice.getDifferentialPercentage());
		 			row1.createCell(23).setCellValue(invoice.getAddTCS());
		 			row1.createCell(24).setCellValue(invoice.getTcsSection());
		 			row1.createCell(25).setCellValue(invoice.getTcsPercentage());
		 			row1.createCell(26).setCellValue(invoice.getBankName());
		 			row1.createCell(27).setCellValue(invoice.getAccountNumber());
		 			row1.createCell(28).setCellValue(invoice.getAccountName());
		 			row1.createCell(29).setCellValue(invoice.getBranchName());
		 			row1.createCell(30).setCellValue(invoice.getIfsccode());
		 			row1.createCell(31).setCellValue(invoice.getCustomerNotes());
		 			row1.createCell(32).setCellValue(invoice.getTermsAndConditions());
		 			row1.createCell(33).setCellValue(invoice.getCustomField1());
		 			row1.createCell(34).setCellValue(invoice.getCustomField2());
		 			row1.createCell(35).setCellValue(invoice.getCustomField3());
		 			row1.createCell(36).setCellValue(invoice.getCustomField4());
		 			row1.createCell(37).setCellValue(invoice.getItemno());
	 			    row1.createCell(38).setCellValue(invoice.getItemNotesComments());
	 			    row1.createCell(39).setCellValue(invoice.getHsnCode());
	 			    row1.createCell(40).setCellValue(invoice.getUqc());
	 			    if(isNotEmpty(invoice.getQuantity())) {
	 			    	row1.createCell(41).setCellValue(decimalFormat.format(invoice.getQuantity()));
	 			    }else {
	 			    	row1.createCell(41).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getRateperitem())) {
	 			    	row1.createCell(42).setCellValue(decimalFormat.format(invoice.getRateperitem()));
	 			    }else {
	 			    	row1.createCell(42).setCellValue(decimalFormat.format(0d));
	 			    }
  	 			    if(isNotEmpty(invoice.getItemDiscount())) {
	 			    	row1.createCell(43).setCellValue(decimalFormat.format(invoice.getItemDiscount()));
	 			    }else {
	 			    	row1.createCell(43).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTaxableValue())) {
	 			    	row1.createCell(44).setCellValue(decimalFormat.format(invoice.getTaxableValue()));
	 			    }else {
	 			    	row1.createCell(44).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getIgstRate())) {
	 			    	row1.createCell(45).setCellValue(decimalFormat.format(invoice.getIgstRate()));
	 			    }else {
	 			    	row1.createCell(45).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getIgstAmount())) {
	 			    	row1.createCell(46).setCellValue(decimalFormat.format(invoice.getIgstAmount()));
	 			    }else {
	 			    	row1.createCell(46).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCgstRate())) {
	 			    	row1.createCell(47).setCellValue(decimalFormat.format(invoice.getCgstRate()));
	 			    }else {
	 			    	row1.createCell(47).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCgstAmount())) {
	 			    	row1.createCell(48).setCellValue(decimalFormat.format(invoice.getCgstAmount()));
	 			    }else {
	 			    	row1.createCell(48).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getSgstRate())) {
	 			    	row1.createCell(49).setCellValue(decimalFormat.format(invoice.getSgstRate()));
	 			    }else {
	 			    	row1.createCell(49).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getSgstAmount())) {
	 			    	row1.createCell(50).setCellValue(decimalFormat.format(invoice.getSgstAmount()));
	 			    }else {
	 			    	row1.createCell(50).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCessRate())) {
	 			    	row1.createCell(51).setCellValue(decimalFormat.format(invoice.getCessRate()));
	 			    }else {
	 			    	row1.createCell(51).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getCessAmount())) {
	 			    	row1.createCell(52).setCellValue(decimalFormat.format(invoice.getCessAmount()));
	 			    }else {
	 			    	row1.createCell(52).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    row1.createCell(53).setCellValue(invoice.getItcType());
	 			    if(isNotEmpty(invoice.getTotalItc())) {
	 			    	row1.createCell(54).setCellValue(decimalFormat.format(invoice.getTotalItc()));
	 			    }else {
	 			    	row1.createCell(54).setCellValue("");
	 			    }
	 			    if(isNotEmpty(invoice.getTotaltax())) {
	 			    	row1.createCell(55).setCellValue(decimalFormat.format(invoice.getTotaltax()));
	 			    }else {
	 			    	row1.createCell(55).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTotalValue())) {
	 			    	row1.createCell(56).setCellValue(decimalFormat.format(invoice.getTotalValue()));
	 			    }else {
	 			    	row1.createCell(56).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTcsAmount())) {
	 			    	row1.createCell(57).setCellValue(decimalFormat.format(invoice.getTcsAmount()));
	 			    }else {
	 			    	row1.createCell(57).setCellValue(decimalFormat.format(0d));
	 			    }
	 			    if(isNotEmpty(invoice.getTcsNetAmount())) {
	 			    	row1.createCell(58).setCellValue(decimalFormat.format(invoice.getTcsNetAmount()));
	 			    }else {
	 			    	row1.createCell(58).setCellValue(decimalFormat.format(0d));
	 			    }
	 		      }
	 	      }
	       }
	      //Setting Auto Column Width
           sheet.autoSizeColumn(0);
           sheet.setColumnWidth(0,50 * 256);
           sheet1.autoSizeColumn(0);
           sheet1.setColumnWidth(0,50 * 256);
	      workbook.write(out);
	      return new ByteArrayInputStream(out.toByteArray());
	    }
	  }


	@Override
	public List<InvoiceVO> invoiceListItems(Page<? extends InvoiceParent> invoices, String returntype,List<StateConfig> states,Client client){
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;
		Double ctotCessNonAdvol = 0d;
		if(isNotEmpty(invoices)) {
			for (InvoiceParent invoice : invoices) {
				if (isNotEmpty(invoice.getItems())) {
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
							if(isNotEmpty(invoice.getRevchargeNo())) {
								invo.setRevChargeNo(invoice.getRevchargeNo());
							}
						}
						if(isNotEmpty(invoice.getBillDate())) {
							invo.setTransactionDate(invoice.getBillDate());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setRecharge("No");
							}else {
								invo.setRecharge("Yes");
							}
						}
						if(isNotEmpty(invoice.getGstStatus())) {
							if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
								invo.setGstStatus("Uploaded");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
								invo.setGstStatus("Submitted");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
								invo.setGstStatus("Filed");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								invo.setGstStatus("Cancelled");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
								invo.setGstStatus("Failed");
							}else {
								invo.setGstStatus("Pending");
							}
						}else {
							invo.setGstStatus("Pending");
						}
						if(returntype.equals(MasterGSTConstants.EINVOICE)) {
							String docType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
								if(("INV").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "INVOICE";
								}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "CREDIT NOTE";
								}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "DEBIT NOTE";
								}else {
									docType =  "INVOICE";
								}
							}
							if(isNotEmpty(invoice.getTyp())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getIrnNo())) {
								invo.setIrnNo(invoice.getIrnNo());
							}
							if(isNotEmpty(invoice.getIrnStatus())) {
								invo.setIrnStatus(invoice.getIrnStatus());
							}else {
								invo.setIrnStatus("Not Generated");
							}
						}
						if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
							String supplyType="";
							String subSupplyType="";
							String docType="";
							String vehicleType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSupplyType())) {
								if(("I").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Inward";
								}else if(("O").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Outward";
								}
							}
							String sText[] = {"Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"};
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSubSupplyType())) {
								subSupplyType = sText[Integer.parseInt(invoice.getSubSupplyType().trim())];
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getDocType())) {
								if(("INV").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Tax Invoice";
								}else if(("CHN").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Delivery Challan";
								}else if(("BIL").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Supply";
								}else if(("BOE").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Entry";
								}else if(("CNT").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Credit Note";
								}else if(("OTH").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Others";
								}
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getVehicleType())) {
								if(("R").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Regular";
								}else if(("O").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Over Dimensional Cargo";
								}
							}
							if(isNotEmpty(invoice.getEwayBillNumber())) {
								invo.setEwayBillNo(invoice.getEwayBillNumber());
							}
							if(isNotEmpty(invoice.geteBillDate())) {
								invo.setEwayBillDate(invoice.geteBillDate());
							}
							if(isNotEmpty(invoice.getSupplyType())) {
								invo.setSupplyType(supplyType);
							}
							if(isNotEmpty(invoice.getSubSupplyType())) {
								invo.setSubSupplyType(subSupplyType);
							}
							if(isNotEmpty(invoice.getDocType())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getFromGstin())) {
								invo.setFromGstin(invoice.getFromGstin());
							}
							if(isNotEmpty(invoice.getFromTrdName())) {
								invo.setFromTrdName(invoice.getFromTrdName());
							}
							if(isNotEmpty(invoice.getFromAddr1())) {
								invo.setFromAddr1(invoice.getFromAddr1());
							}
							if(isNotEmpty(invoice.getFromAddr2())) {
								invo.setFromAddr2(invoice.getFromAddr2());
							}
							if(isNotEmpty(invoice.getFromPlace())) {
								invo.setFromPlace(invoice.getFromPlace());
							}
							if(isNotEmpty(invoice.getFromPincode())) {
								invo.setFromPincode(invoice.getFromPincode());
							}
							if(isNotEmpty(invoice.getFromStateCode())) {
								invo.setFromStateCode(invoice.getFromStateCode());
							}
							if(isNotEmpty(invoice.getToGstin())) {
								invo.setToGstin(invoice.getToGstin());
							}
							if(isNotEmpty(invoice.getToTrdName())) {
								invo.setToTrdName(invoice.getToTrdName());
							}
							if(isNotEmpty(invoice.getToAddr1())) {
								invo.setToAddr1(invoice.getToAddr1());
							}
							if(isNotEmpty(invoice.getToAddr2())) {
								invo.setToAddr2(invoice.getToAddr2());
							}
							if(isNotEmpty(invoice.getToPincode())) {
								invo.setToPincode(invoice.getToPincode());
							}
							if(isNotEmpty(invoice.getToPlace())) {
								invo.setToPlace(invoice.getToPlace());
							}
							if(isNotEmpty(invoice.getToStateCode())) {
								invo.setToStateCode(invoice.getToStateCode());
							}
							if(isNotEmpty(invoice.getTransporterId())) {
								invo.setTransporterId(invoice.getTransporterId());
							}
							if(isNotEmpty(invoice.getTransporterName())) {
								invo.setTransporterName(invoice.getTransporterName());
							}
							if(isNotEmpty(invoice.getStatus())) {
								invo.setStatus(invoice.getStatus());
							}
							if(isNotEmpty(invoice.getActualDist())) {
								invo.setActualDist(invoice.getActualDist());
							}
							if(isNotEmpty(invoice.getNoValidDays())) {
								invo.setNoValidDays(invoice.getNoValidDays());
							}
							if(isNotEmpty(invoice.getValidUpto())) {
								invo.setValidUpto(invoice.getValidUpto());
							}
							if(isNotEmpty(invoice.getExtendedTimes())) {
								invo.setExtendedTimes(invoice.getExtendedTimes());
							}
							if(isNotEmpty(invoice.getRejectStatus())) {
								invo.setRejectStatus(invoice.getRejectStatus());
							}
							if(isNotEmpty(vehicleType)) {
								invo.setVehicleType(vehicleType);
							}
							if(isNotEmpty(invoice.getTransactionType())) {
								invo.setTransactionType(invoice.getTransactionType());
							}
							if(isNotEmpty(invoice.getOtherValue())) {
								invo.setOtherValue(invoice.getOtherValue());
							}
							if(isNotEmpty(invoice.getCessNonAdvolValue())) {
								invo.setCessNonAdvolValue(invoice.getCessNonAdvolValue());
							}
							
						
						}
						
						
						if(isNotEmpty(item.getItemno())) {
							invo.setItemname(item.getItemno());
						}
						if(isNotEmpty(item.getItemNotescomments())) {
							invo.setItemNotesComments(item.getItemNotescomments());
						}
						if(isNotEmpty(client.getGstnnumber())) {
							invo.setCompanyGSTIN(client.getGstnnumber());
						}
						if(isNotEmpty(client.getStatename())) {
							invo.setCompanyStatename(client.getStatename());
						}
						if (isNotEmpty(invoice.getBilledtoname())) {
							invo.setCustomerName(invoice.getBilledtoname());
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							invo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)  || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
									invo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
									String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
									for (StateConfig state : states) {
										if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
											invo.setPlaceOfSupply(state.getName());
											break;
										}
									}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
								String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
						}
						if (isNotEmpty(item.getIgstrate())) {
							invo.setIgstRate(item.getIgstrate());
						}
						if(isEmpty(item.getExmepted())) {
							invo.setExemptedVal(0d);
						}
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							
							invo.setItcType(itcType);
						}else {
							//Nill Supplies itc type not applicable, but end-users asks don't empty keep Ineligible.
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL) && (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER))) {
								invo.setItcType("Ineligible");
							}else {
								invo.setItcType("");								
							}
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(-item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(item.getExmepted());
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}
							}
							if(isNotEmpty(item.getFreeQty())) {
								invo.setFreeQty(item.getFreeQty());
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}
							}
							invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}
						}
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
						}
						if (isNotEmpty(item.getCgstrate())) {
							invo.setCgstRate(item.getCgstrate());
						}
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
						}
						if (isNotEmpty(item.getSgstrate())) {
							invo.setSgstRate(item.getSgstrate());
						}
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
						}
						if (isNotEmpty(item.getCessrate())) {
							invo.setCessRate(item.getCessrate());
						}
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
						}
						if(isNotEmpty(item.getQuantity())){
							invo.setQuantity(item.getQuantity());
						}
						if(isNotEmpty(item.getUqc())){
							invo.setUqc(item.getUqc());
						}
						if(isNotEmpty(item.getHsn())){
							invo.setHsnCode(item.getHsn());
						}
						if(isNotEmpty(item.getRateperitem())){
							invo.setRateperitem(item.getRateperitem());
						}
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						if (isNotEmpty(invoice.getStatename())) {
							invo.setState(invoice.getStatename());
						}
						if (isNotEmpty(invoice.getInvoiceno())) {
							invo.setInvoiceNo(invoice.getInvoiceno());
						}
						invo.setInvoiceDate(invoice.getDateofinvoice());
						invo.setReturnPeriod(invoice.getFp());
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
							String docType = "";
							if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
								if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
											docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
									}
								}
							}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
											docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
									}
								}
							}
							if("C".equals(docType)) {
								invo.setType("Credit Note");
							}else if("D".equals(docType)) {
								invo.setType("Debit Note");
							}else {
								invo.setType(invoice.getInvtype());
							}
						}else {
							invo.setType(invoice.getInvtype());
						}	
						
						invo.setTotalItc(invoice.getTotalitc());
						if(invo.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVOCancelledList.add(invo);
						}else {
							invoiceVOList.add(invo);
						}
					}
				}
					if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else {
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							ctottax = ctottax+invoice.getTotaltax();
						}else {
							tottax = tottax+invoice.getTotaltax();
						}
					}
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		if(isNotEmpty(totStateCess)) {
			totalinvo.setStateCess(totStateCess);
		}
		if(isNotEmpty(totCessNonAdvol)) {
			totalinvo.setCessnonAdvol(totCessNonAdvol);
		}
		totalinvo.setTotalValue(tottotal);
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO emptyinvo = new InvoiceVO();
			InvoiceVO emptyinvo1 = new InvoiceVO();
			emptyinvo1.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(emptyinvo);
			invoiceVOList.add(emptyinvo1);
			invoiceVOList.addAll(invoiceVOCancelledList);
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			invoiceVOList.add(ctotalinvo);
		}
		return invoiceVOList;
	}


	@Override
	public List<InvoiceVO> getInvoice_Wise_List(Page<? extends InvoiceParent> invoices, String returntype,List<StateConfig> states,Client client){
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;
		Double ctotCessNonAdvol = 0d;
		if(isNotEmpty(invoices)) {
			for (InvoiceParent invoice : invoices) {
				String clientid = invoice.getClientid();
				
				if (isNotEmpty(invoice.getItems())) {
					
					InvoiceVO invoiceVo = new InvoiceVO();
					if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
						if(isNotEmpty(invoice.getRevchargeNo())) {
							invoiceVo.setRevChargeNo(invoice.getRevchargeNo());
						}
					}
					if(isNotEmpty(invoice.getBillDate())) {
						invoiceVo.setTransactionDate(invoice.getBillDate());
					}
					if(isNotEmpty(invoice.getRevchargetype())) {
						if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
							invoiceVo.setRecharge("No");
						}else {
							invoiceVo.setRecharge("Yes");
						}
					}
					if(isNotEmpty(invoice.getGstStatus())) {
						if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
							invoiceVo.setGstStatus("Uploaded");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
							invoiceVo.setGstStatus("Submitted");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
							invoiceVo.setGstStatus("Filed");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVo.setGstStatus("Cancelled");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
							invoiceVo.setGstStatus("Failed");
						}else {
							invoiceVo.setGstStatus("Pending");
						}
					}else {
						invoiceVo.setGstStatus("Pending");
					}
					if(isNotEmpty(client.getGstnnumber())) {
						invoiceVo.setCompanyGSTIN(client.getGstnnumber());
					}
					if(isNotEmpty(client.getStatename())) {
						invoiceVo.setCompanyStatename(client.getStatename());
					}
					if (isNotEmpty(invoice.getBilledtoname())) {
						invoiceVo.setCustomerName(invoice.getBilledtoname());
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						invoiceVo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
					}
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
								String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invoiceVo.setPlaceOfSupply(state.getName());
										break;
									}
								}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
							String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
							String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
							String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
					}
					if (isNotEmpty(invoice.getInvoiceno())) {
						invoiceVo.setInvoiceNo(invoice.getInvoiceno());
					}
					if (isNotEmpty(invoice.getStatename())) {
						invoiceVo.setState(invoice.getStatename());
					}
					if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						String docType="";
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
							if(("INV").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "INVOICE";
							}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "CREDIT NOTE";
							}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "DEBIT NOTE";
							}else {
								docType =  "INVOICE";
							}
						}
						if(isNotEmpty(invoice.getTyp())) {
							invoiceVo.setDocType(docType);
						}
						if(isNotEmpty(invoice.getIrnNo())) {
							invoiceVo.setIrnNo(invoice.getIrnNo());
						}
						if(isNotEmpty(invoice.getIrnStatus())) {
							invoiceVo.setIrnStatus(invoice.getIrnStatus());
						}else {
							invoiceVo.setIrnStatus("Not Generated");
						}
					}
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
							if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
							if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
							if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
							if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
								invoiceVo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
							}
							if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
								invoiceVo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
							if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
							if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
								invoiceVo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
							}
							if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
								invoiceVo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
							}
						}
					}
					Double quantity=0d;
					Double freeqty=0d;
					Double assAmt=0d;
					Double stateCess=0d;
					Double cessnonAdvol=0d;
					Double taxableValue=0d;
					Double igstRate=0d;
					Double igstAmount=0d;
					Double igstTax=0d;
					Double cgstRate=0d;
					Double cgstAmount=0d;
					Double cgstTax=0d;
					Double sgstRate=0d;
					Double sgstAmount=0d;
					Double sgstTax=0d;
					Double cessRate=0d;
					Double cessAmount=0d;
					Double cessTax=0d;
					Double rateperitem=0d;
					Double totaltax=0d;
					Double totalValue=0d;
					Double totalExemptedValue=0d;
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							
							invo.setItcType(itcType);
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue = totalExemptedValue-((item.getQuantity())*(item.getExmepted()));
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted = ctotExempted-((item.getQuantity())*(item.getExmepted()));
										}else {
											totExempted = totExempted-((item.getQuantity())*(item.getExmepted()));
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue += (item.getQuantity())*(item.getExmepted());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted += (item.getQuantity())*(item.getExmepted());
										}else {
											totExempted += (item.getQuantity())*(item.getExmepted());
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue += (item.getQuantity())*(item.getExmepted());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted += (item.getQuantity())*(item.getExmepted());
										}else {
											totExempted += (item.getQuantity())*(item.getExmepted());
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
									totalExemptedValue += (item.getQuantity())*(item.getExmepted());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotExempted += (item.getQuantity())*(item.getExmepted());
									}else {
										totExempted += (item.getQuantity())*(item.getExmepted());
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}					
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}
							}
							invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
						}
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
						}
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
						}
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
						}
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
						}
						
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						
						if(isNotEmpty(invo.getTotaltax())) {
							totaltax+=invo.getTotaltax();
						}
						if(isNotEmpty(item.getTotal())){
							totalValue+=invo.getTotalValue();
						}
						
						if(isNotEmpty(invo.getQuantity())) {
							quantity+=invo.getQuantity();
						}
						if(isNotEmpty(invo.getTaxableValue())) {
							taxableValue+=invo.getTaxableValue();
						}
						if(isNotEmpty(invo.getIgstAmount())) {
							igstAmount+=invo.getIgstAmount();
						}
						
						if(isNotEmpty(invo.getIgstTax())) {
							igstTax+=invo.getIgstTax();
						}
						if(isNotEmpty(invo.getCgstAmount())) {
							cgstAmount+=invo.getCgstAmount();
						}
						
						if(isNotEmpty(invo.getCgstTax())) {
							cgstTax+=invo.getCgstTax();
						
						}
						if(isNotEmpty(invo.getSgstAmount())) {
							sgstAmount+=invo.getSgstAmount();
						}
						
						if(isNotEmpty(invo.getSgstTax())) {
							sgstTax+=invo.getSgstTax();
						}
					
						if(isNotEmpty(invo.getCessAmount())) {
							cessAmount+=invo.getCessAmount();
						}
						if(isNotEmpty(invo.getCessTax())) {
							cessTax+=invo.getCessTax();
						}
					}
					invoiceVo.setExemptedVal(totalExemptedValue);
					invoiceVo.setInvoiceDate(invoice.getDateofinvoice());
					invoiceVo.setReturnPeriod(invoice.getFp());
					if(isNotEmpty(invoice.getBranch())) {
						invoiceVo.setBranch(invoice.getBranch());
					}else {
						invoiceVo.setBranch("");
					}
					if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
						String docType = "";
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
							}
						}
						if("C".equals(docType)) {
							invoiceVo.setType("Credit Note");
						}else if("D".equals(docType)) {
							invoiceVo.setType("Debit Note");
						}else {
							invoiceVo.setType(invoice.getInvtype());
						}
					}else {
						invoiceVo.setType(invoice.getInvtype());
					}
					invoiceVo.setTotalItc(invoice.getTotalitc());
					if(isNotEmpty(totAss)) {
						invoiceVo.setAssAmt(totAss);
					}
					invoiceVo.setTaxableValue(taxableValue);
					invoiceVo.setIgstAmount(igstAmount);
					
					invoiceVo.setIgstTax(igstTax);
					invoiceVo.setCgstAmount(cgstAmount);
					
					invoiceVo.setCgstTax(cgstTax);
					invoiceVo.setSgstAmount(sgstAmount);
					
					invoiceVo.setSgstTax(sgstTax);
					
					invoiceVo.setCessAmount(cessAmount);
					
					invoiceVo.setCessTax(cessTax);
					
					invoiceVo.setTotaltax(totaltax);
					invoiceVo.setTotalValue(totalValue);
					if(isNotEmpty(invoice.getDateofitcClaimed())) {
						invoiceVo.setDateOfItcClaimed(invoice.getDateofitcClaimed());
					}
					if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
						invoiceVOCancelledList.add(invoiceVo);
					}else {
						invoiceVOList.add(invoiceVo);
					}
				}
				if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}
				}else {
					if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
						ctottax = ctottax+invoice.getTotaltax();
					}else {
						tottax = tottax+invoice.getTotaltax();
					}
				}
				
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		totalinvo.setTotalValue(tottotal);
		totalinvo.setExemptedVal(totExempted);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO cancelinvo = new InvoiceVO();
			InvoiceVO cancel1invo = new InvoiceVO();
			cancel1invo.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(cancelinvo);
			invoiceVOList.add(cancel1invo);
			invoiceVOList.addAll(invoiceVOCancelledList);
			
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			ctotalinvo.setExemptedVal(ctotExempted);
			invoiceVOList.add(ctotalinvo);
		}
		
		return invoiceVOList;
	}
	
	@Override
	public Page<? extends InvoiceParent> getDaoInvoices(Client client, String returntype, int month, int year, String reporttype,List<String> invTypes,InvoiceFilter filter) {
		logger.debug(CLASSNAME + "getDaoInvoices : Begin");
		String yearCode = Utility.getYearCode(month, year);
		if(MasterGSTConstants.GSTR2B.equals(returntype)) {
			return gstr2bDao.getMultimonthInvoices(client.getId().toString(), invTypes, year,  0, 0, null, filter);
		}

		return gstr2Dao.getMultimonthInvoices(client.getId().toString(), invTypes, yearCode,  0, 0, null, filter);
	}
	
	@Override
	public  Map<String, Map<String, String>> getConsolidatedSummeryForYearReports(final Client client, String returntype, String yearCd, boolean checkQuarterly, String reportType, InvoiceFilter invoiceFilter){
		
		List<TotalInvoiceAmount> gstrInvoiceAmounts = null;
		Map<String, TotalInvoiceAmount> summerySlsData = new HashMap<String, TotalInvoiceAmount>();
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		
			List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES,MasterGSTConstants.IMP_GOODS,MasterGSTConstants.IMP_SERVICES,
					MasterGSTConstants.ISD, MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
			if(returntype.equals(MasterGSTConstants.GSTR2B)) {
				int year = Integer.parseInt(yearCd.split("-")[1]);
				gstrInvoiceAmounts = gstr2bDao.getConsolidatedMultimonthSummeryForYearMonthwise(client, invTypes, year, invoiceFilter);				
				List<String> fps = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
				for(TotalInvoiceAmount gstr1InvoiceAmount : gstrInvoiceAmounts){
					String code = gstr1InvoiceAmount.get_id();
					summerySlsData.put(code, gstr1InvoiceAmount);
				}
				for(String fp : fps) {
					//String cd = Integer.toString(i);
					int totalTransactions = 0;
					Double totalAmount = 0d, itcAmount =0d;
					Double salesAmt = 0d, taxAmt = 0d, salesTax = 0d, igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,tds = 0d;
					TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(fp);
					Map<String, String> reportMap = new HashMap<String, String>();
					summeryReturnData.put(Integer.parseInt(fp.substring(0, 2))+"", reportMap);
					if(invoiceAmountSls != null){
						totalTransactions = invoiceAmountSls.getTotalTransactions();
						salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
						taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
						salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
						igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
						cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
						sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
						
						if(GSTR1.equalsIgnoreCase(returntype)) {
							exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();					
							tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
						}else {
							itcAmount= invoiceAmountSls.getTotalITCAvailable().doubleValue();
							tds = invoiceAmountSls.getTcsTdsAmount().doubleValue();
						}
						
						cess=invoiceAmountSls.getTotalCESSAmount().doubleValue();
						//totalAmount = salesAmt + igst+ cgst + sgst;
						totalAmount = invoiceAmountSls.getTotalAmount().doubleValue();
					}
					reportMap.put("Sales", decimalFormat.format(salesAmt));
					reportMap.put("Tax", decimalFormat.format(taxAmt));
					reportMap.put("SalesTax", decimalFormat.format(salesTax));
					reportMap.put("igst", decimalFormat.format(igst));
					reportMap.put("cgst", decimalFormat.format(cgst));
					reportMap.put("sgst", decimalFormat.format(sgst));
					reportMap.put("cess", decimalFormat.format(cess));
					reportMap.put("exempted", decimalFormat.format(exempted));
					reportMap.put("tcsamount", decimalFormat.format(tcs));
					reportMap.put("tdsamount", decimalFormat.format(tds));
					reportMap.put("totalTransactions", String.valueOf(totalTransactions));
					reportMap.put("totalamt", decimalFormat.format(totalAmount));
					reportMap.put("itc", decimalFormat.format(itcAmount));
				}
				return summeryReturnData;
				
			}else {
				gstrInvoiceAmounts = gstr2Dao.getConsolidatedMultimonthSummeryForYearMonthwise(client, invTypes, yearCd, invoiceFilter);				
			}
		
		
		for(TotalInvoiceAmount gstr1InvoiceAmount : gstrInvoiceAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		List<TotalInvoiceAmount> invs = new ArrayList<>();
		int ct = 12;
		if(checkQuarterly){
			ct = 4;
		}
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, itcAmount =0d;
			Double salesAmt = 0d, taxAmt = 0d, salesTax = 0d, igst = 0d, cgst = 0d, sgst = 0d , cess = 0d, exempted = 0d,tcs = 0d,tds = 0d;
			TotalInvoiceAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				salesAmt = invoiceAmountSls.getTotalTaxableAmount().doubleValue();
				taxAmt = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				salesTax = invoiceAmountSls.getTotalTaxAmount().doubleValue();
				igst = invoiceAmountSls.getTotalIGSTAmount().doubleValue();
				cgst = invoiceAmountSls.getTotalCGSTAmount().doubleValue();
				sgst = invoiceAmountSls.getTotalSGSTAmount().doubleValue();
				
				if(GSTR1.equalsIgnoreCase(returntype)) {
					exempted = invoiceAmountSls.getTotalExemptedAmount().doubleValue();					
					tcs = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}else {
					itcAmount= invoiceAmountSls.getTotalITCAvailable().doubleValue();
					tds = invoiceAmountSls.getTcsTdsAmount().doubleValue();
				}
				
				cess=invoiceAmountSls.getTotalCESSAmount().doubleValue();
				//totalAmount = salesAmt + igst+ cgst + sgst;
				totalAmount = invoiceAmountSls.getTotalAmount().doubleValue();
			}
			reportMap.put("Sales", decimalFormat.format(salesAmt));
			reportMap.put("Tax", decimalFormat.format(taxAmt));
			reportMap.put("SalesTax", decimalFormat.format(salesTax));
			reportMap.put("igst", decimalFormat.format(igst));
			reportMap.put("cgst", decimalFormat.format(cgst));
			reportMap.put("sgst", decimalFormat.format(sgst));
			reportMap.put("cess", decimalFormat.format(cess));
			reportMap.put("exempted", decimalFormat.format(exempted));
			reportMap.put("tcsamount", decimalFormat.format(tcs));
			reportMap.put("tdsamount", decimalFormat.format(tds));
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalamt", decimalFormat.format(totalAmount));
			reportMap.put("itc", decimalFormat.format(itcAmount));
		}
		return summeryReturnData;
	}
	
	private void gst2bSupportDocuments(GSTR2B invoice, String key, String fullname, Client client, String userid, String returnPeriod) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Pageable pageable = new PageRequest(0, 2, Sort.Direction.DESC, "createdDate");
		GSTR2BDocData  doc =invoice.getData().getDocdata();
		Map<String, ResponseData> gstnMap = Maps.newHashMap();
		if(isNotEmpty(doc) && isNotEmpty(doc.getB2b())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BB2B b2b : doc.getB2b()) {
				if(isNotEmpty(b2b.getCtin()) && !gstnMap.containsKey(b2b.getCtin())) {
					Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(b2b.getCtin(), pageable);
					if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
						GSTINPublicData publicData = page.getContent().get(0);
						ResponseData data = new ResponseData();
						if(isNotEmpty(publicData.getTradeNam())) {
							data.setTradeNam(publicData.getTradeNam());
						}else {
							data.setTradeNam(publicData.getLgnm());
						}
						data.setPradr(publicData.getPradr());
						gstnMap.put(b2b.getCtin(), data);
					} else {
						Response response = iHubConsumerService.publicSearch(b2b.getCtin());
						if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							ResponseData data = response.getData();
							if(isNotEmpty(data.getTradeNam())) {
								data.setTradeNam(data.getTradeNam());
							}else {
								data.setTradeNam(data.getLgnm());
							}
							gstnMap.put(b2b.getCtin(), data);
						}
					}
				}
				String invType = MasterGSTConstants.B2B;			 
				List<GSTR2BInvoiceDetails> inv = b2b.getInv();
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRB2B b2bb = new GSTRB2B();
					b2bb.setCtin(b2b.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					BeanUtils.copyProperties(ginv, gstInvDetails);
					b2bb.setInv(Arrays.asList(gstInvDetails));	
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getRev())) {
						gstInvDetails.setRchrg(ginv.getRev());						
					}
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
					if(isNotEmpty(ginv.getVal())) {
						gstInvDetails.setVal(Double.parseDouble(ginv.getVal()));
					}else {
						gstInvDetails.setVal(totval);
					}
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setDocKey(key);
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(1);
					if(isNotEmpty(b2b.getCtin()) && gstnMap.containsKey(b2b.getCtin())) {
						individualInvoice.setBilledtoname(gstnMap.get(b2b.getCtin()).getTradeNam());
					}
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
				if(isNotEmpty(b2b.getCtin()) && !gstnMap.containsKey(b2b.getCtin())) {
					Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(b2b.getCtin(), pageable);
					if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
						GSTINPublicData publicData = page.getContent().get(0);
						ResponseData data = new ResponseData();
						if(isNotEmpty(publicData.getTradeNam())) {
							data.setTradeNam(publicData.getTradeNam());
						}else {
							data.setTradeNam(publicData.getLgnm());
						}
						data.setPradr(publicData.getPradr());
						gstnMap.put(b2b.getCtin(), data);
					} else {
						Response response = iHubConsumerService.publicSearch(b2b.getCtin());
						if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							ResponseData data = response.getData();
							if(isNotEmpty(data.getTradeNam())) {
								data.setTradeNam(data.getTradeNam());
							}else {
								data.setTradeNam(data.getLgnm());
							}
							gstnMap.put(b2b.getCtin(), data);
						}
					}
				}
				String invType = MasterGSTConstants.B2BA;			 
				List<GSTR2BInvoiceDetails> inv = b2b.getInv();
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRB2B b2bb = new GSTRB2B();
					b2bb.setCtin(b2b.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					BeanUtils.copyProperties(ginv, gstInvDetails);
					b2bb.setInv(Arrays.asList(gstInvDetails));	
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getRev())) {
						gstInvDetails.setRchrg(ginv.getRev());						
					}
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
					if(isNotEmpty(ginv.getVal())) {
						gstInvDetails.setVal(Double.parseDouble(ginv.getVal()));
					}else {
						gstInvDetails.setVal(totval);
					}
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, b2b.getCtin(), null , null, b2b.getSupfildt(), b2b.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(1);
					if(isNotEmpty(b2b.getCtin()) && gstnMap.containsKey(b2b.getCtin())) {
						individualInvoice.setBilledtoname(gstnMap.get(b2b.getCtin()).getTradeNam());
					}
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
				if(isNotEmpty(cdnr.getCtin()) && !gstnMap.containsKey(cdnr.getCtin())) {
					Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(cdnr.getCtin(), pageable);
					if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
						GSTINPublicData publicData = page.getContent().get(0);
						ResponseData data = new ResponseData();
						if(isNotEmpty(publicData.getTradeNam())) {
							data.setTradeNam(publicData.getTradeNam());
						}else {
							data.setTradeNam(publicData.getLgnm());
						}
						data.setPradr(publicData.getPradr());
						gstnMap.put(cdnr.getCtin(), data);
					} else {
						Response response = iHubConsumerService.publicSearch(cdnr.getCtin());
						if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
								&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							ResponseData data = response.getData();
							if(isNotEmpty(data.getTradeNam())) {
								data.setTradeNam(data.getTradeNam());
							}else {
								data.setTradeNam(data.getLgnm());
							}
							gstnMap.put(cdnr.getCtin(), data);
						}
					}
				}
				String invType = MasterGSTConstants.CREDIT_DEBIT_NOTES;			 
				List<GSTR2BInvoiceDetails> inv = cdnr.getNt();
				int sumFactor = 1;
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRCreditDebitNotes cdn = new GSTRCreditDebitNotes();
					cdn.setCtin(cdnr.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					
					BeanUtils.copyProperties(ginv, gstInvDetails);
					    
	                cdn.setNt(Arrays.asList(gstInvDetails));
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getRev())) {
						gstInvDetails.setRchrg(ginv.getRev());						
					}
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
					
					if(isNotEmpty(ginv.getTyp())) {
						if("C".equalsIgnoreCase(ginv.getTyp())) {
							sumFactor = -1;
						}
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
					if(isNotEmpty(ginv.getVal())) {
						gstInvDetails.setVal(Double.parseDouble(ginv.getVal()));
					}else {
						gstInvDetails.setVal(totval);
					}
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(sumFactor);
					if(isNotEmpty(cdnr.getCtin()) && gstnMap.containsKey(cdnr.getCtin())) {
						individualInvoice.setBilledtoname(gstnMap.get(cdnr.getCtin()).getTradeNam());
					}
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
				if(isNotEmpty(cdnr.getCtin()) && !gstnMap.containsKey(cdnr.getCtin())) {
					Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(cdnr.getCtin(), pageable);
					if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
						GSTINPublicData publicData = page.getContent().get(0);
						ResponseData data = new ResponseData();
						if(isNotEmpty(publicData.getTradeNam())) {
							data.setTradeNam(publicData.getTradeNam());
						}else {
							data.setTradeNam(publicData.getLgnm());
						}
						data.setPradr(publicData.getPradr());
						gstnMap.put(cdnr.getCtin(), data);
					} else {
						Response response = iHubConsumerService.publicSearch(cdnr.getCtin());
						if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
								&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							ResponseData data = response.getData();
							if(isNotEmpty(data.getTradeNam())) {
								data.setTradeNam(data.getTradeNam());
							}else {
								data.setTradeNam(data.getLgnm());
							}
							gstnMap.put(cdnr.getCtin(), data);
						}
					}
				}
				String invType = MasterGSTConstants.CDNA;			 
				List<GSTR2BInvoiceDetails> inv = cdnr.getNt();
				int sumFactor = 1;
				for(GSTR2BInvoiceDetails ginv : inv) {
					GSTRCreditDebitNotes cdn = new GSTRCreditDebitNotes();
					cdn.setCtin(cdnr.getCtin());
					GSTRInvoiceDetails  gstInvDetails = new GSTRInvoiceDetails();
					
					BeanUtils.copyProperties(ginv, gstInvDetails);
					    
	                cdn.setNt(Arrays.asList(gstInvDetails));
					gstInvDetails.setIdt(ginv.getDt());
					if(isNotEmpty(ginv.getRev())) {
						gstInvDetails.setRchrg(ginv.getRev());						
					}
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
					if(isNotEmpty(ginv.getTyp())) {
						if("C".equalsIgnoreCase(ginv.getTyp())) {
							sumFactor = -1;
						}
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
					if(isNotEmpty(ginv.getVal())) {
						gstInvDetails.setVal(Double.parseDouble(ginv.getVal()));
					}else {
						gstInvDetails.setVal(totval);
					}
					gstInvDetails.setItms(items);
					GSTR2BSupport individualInvoice = populateGSTR2BSupport(gstInvDetails, client.getGstnnumber(), returnPeriod, userid, client.getId().toString(), invType, cdnr.getCtin(), null , null, cdnr.getSupfildt(), cdnr.getSupprd(), null);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(sumFactor);
					if(isNotEmpty(cdnr.getCtin()) && gstnMap.containsKey(cdnr.getCtin())) {
						individualInvoice.setBilledtoname(gstnMap.get(cdnr.getCtin()).getTradeNam());
					}
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
				GSTR2BSupport individualInvoice = populateGSTR2IMPG(gstrImportDetails, returnPeriod, userid, client.getId().toString(), invType);
				individualInvoice.setDocKey(key);
				individualInvoice.setFullname(fullname);
				individualInvoice.setBranch("Main Branch");
				individualInvoice.setCsftr(1);
				individualInvoice.setSftr(1);
				invLst.add(individualInvoice);
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getImpgsez())) {
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			for(GSTR2BIMPGSEZ impgsez : doc.getImpgsez()) {
				if(isNotEmpty(impgsez.getCtin()) && !gstnMap.containsKey(impgsez.getCtin())) {
					Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(impgsez.getCtin(), pageable);
					if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
						GSTINPublicData publicData = page.getContent().get(0);
						ResponseData data = new ResponseData();
						if(isNotEmpty(publicData.getTradeNam())) {
							data.setTradeNam(publicData.getTradeNam());
						}else {
							data.setTradeNam(publicData.getLgnm());
						}
						data.setPradr(publicData.getPradr());
						gstnMap.put(impgsez.getCtin(), data);
					} else {
						Response response = iHubConsumerService.publicSearch(impgsez.getCtin());
						if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
								&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							ResponseData data = response.getData();
							if(isNotEmpty(data.getTradeNam())) {
								data.setTradeNam(data.getTradeNam());
							}else {
								data.setTradeNam(data.getLgnm());
							}
							gstnMap.put(impgsez.getCtin(), data);
						}
					}
				}
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
					GSTRB2B indGstrb2b = new GSTRB2B();
					if (isNotEmpty(impgsez.getCtin())) {
						indGstrb2b.setCtin(impgsez.getCtin());
					}
					individualInvoice.getB2b().add(indGstrb2b);
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					if(isNotEmpty(impgsez.getCtin()) && gstnMap.containsKey(impgsez.getCtin())) {
						individualInvoice.setBilledtoname(gstnMap.get(impgsez.getCtin()).getTradeNam());
					}
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(1);
					invLst.add(individualInvoice);
				}
			}
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		} 
		if(isNotEmpty(doc) && isNotEmpty(doc.getIsd())) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			List<GSTRDocListDetails> docList = Lists.newArrayList();
			List<GSTRISD> gstrisdList = Lists.newArrayList();
			List<Item> itemsList = Lists.newArrayList();
			Double totalTaxVal = 0d;
			Double totalIgstVal = 0d;
			Double totalCgstVal = 0d;
			Double totalSgstVal = 0d;
			Double totalCessVal = 0d;
			for(GSTR2BISD isd : doc.getIsd()) {
				
				for(GSTR2BDocList docs : isd.getDoclist()) {
					GSTRISD gstrisd = new GSTRISD();
					GSTR2BSupport individualInvoice = new GSTR2BSupport();
					GSTRB2B indGstrb2b = new GSTRB2B();
					if (isNotEmpty(isd.getCtin())) {
						indGstrb2b.setCtin(isd.getCtin());
					}
					individualInvoice.getB2b().add(indGstrb2b);
					gstrisd.setCtin(isd.getCtin());
					individualInvoice.setInvoiceno(docs.getDocnum());
					GSTRDocListDetails docData = new GSTRDocListDetails();
					docData.setDocnum(docs.getDocnum());
					if(isNotEmpty(docs.getDocdt())) {
						docData.setDocdt(simpleDateFormat.parse(docs.getDocdt()));
						individualInvoice.setDateofinvoice(simpleDateFormat.parse(docs.getDocdt()));
						individualInvoice.setDateofinvoice_str(dateFormatOnlyDate.format(simpleDateFormat.parse(docs.getDocdt())));
					}
					docData.setIsdDocty(docs.getDoctyp());
					docData.setItcElg(docs.getItcelg());
					if(isNotEmpty(docs.getIgst())) {
						docData.setIamt(docs.getIgst());
						totalTaxVal += docs.getIgst();
						totalIgstVal += docs.getIgst();
					}else {
						docData.setIamt(0d);
					}
					if(isNotEmpty(docs.getCgst())) {
						docData.setCamt(docs.getCgst());
						totalTaxVal += docs.getCgst();
						totalCgstVal += docs.getCgst();
					}else {
						docData.setCamt(0d);
					}
					if(isNotEmpty(docs.getSgst())) {
						docData.setSamt(docs.getSgst());
						totalTaxVal += docs.getSgst();
						totalSgstVal += docs.getSgst();
					}else {
						docData.setSamt(0d);
					}
					
					if(isNotEmpty(docs.getCess())) {
						docData.setCess(docs.getCess());
						totalTaxVal += docs.getCess();
						totalCessVal += docs.getCess();
					}else {
						docData.setCess(0d);
					}
					docList.add(docData);
					Item item = populateGSTR2ISD(docData);
					itemsList.add(item);
					gstrisd.setDoclist(docList);
					gstrisdList.add(gstrisd);
					individualInvoice.setItems(itemsList);
					individualInvoice.setFp(returnPeriod);
					individualInvoice.setInvtype(MasterGSTConstants.ISD);
					individualInvoice.setBilledtoname(isd.getTrdnm());
					individualInvoice.setUserid(userid);
					individualInvoice.setClientid(client.getId().toString());
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setTotalamount(totalTaxVal);
					individualInvoice.setTotalamount_str(String.format(DOUBLE_FORMAT, totalTaxVal));
					individualInvoice.setTotaltax(totalTaxVal);
					individualInvoice.setTotaltax_str(String.format(DOUBLE_FORMAT, totalTaxVal));
					individualInvoice.setTotalIgstAmount(totalIgstVal);
					individualInvoice.setTotalCgstAmount(totalCgstVal);
					individualInvoice.setTotalSgstAmount(totalSgstVal);
					individualInvoice.setTotalCessAmount(totalCessVal);
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(1);
					individualInvoice.setIsd(gstrisdList);
					invLst.add(individualInvoice);
				}
				
			}
			
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
		if(isNotEmpty(doc) && isNotEmpty(doc.getIsda())) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			List<GSTR2BSupport> invLst = Lists.newArrayList();
			List<GSTRDocListDetails> docList = Lists.newArrayList();
			List<GSTRISD> gstrisdList = Lists.newArrayList();
			List<Item> itemsList = Lists.newArrayList();
			Double totalTaxVal = 0d;
			Double totalIgstVal = 0d;
			Double totalCgstVal = 0d;
			Double totalSgstVal = 0d;
			Double totalCessVal = 0d;
			for(GSTR2BISD isd : doc.getIsda()) {
				
				for(GSTR2BDocList docs : isd.getDoclist()) {
					GSTRISD gstrisd = new GSTRISD();
					GSTR2BSupport individualInvoice = new GSTR2BSupport();
					individualInvoice.setInvoiceno(docs.getDocnum());
					
					GSTRB2B indGstrb2b = new GSTRB2B();
					if (isNotEmpty(isd.getCtin())) {
						indGstrb2b.setCtin(isd.getCtin());
					}
					individualInvoice.getB2b().add(indGstrb2b);
					gstrisd.setCtin(isd.getCtin());
					GSTRDocListDetails docData = new GSTRDocListDetails();
					docData.setDocnum(docs.getDocnum());
					if(isNotEmpty(docs.getDocdt())) {
						docData.setDocdt(simpleDateFormat.parse(docs.getDocdt()));
						individualInvoice.setDateofinvoice(simpleDateFormat.parse(docs.getDocdt()));
						individualInvoice.setDateofinvoice_str(dateFormatOnlyDate.format(simpleDateFormat.parse(docs.getDocdt())));
					}
					docData.setIsdDocty(docs.getDoctyp());
					docData.setItcElg(docs.getItcelg());
					if(isNotEmpty(docs.getOdocnum())) {
						docData.setOdocnum(docs.getOdocnum());
					}
					if(isNotEmpty(docs.getOdocdt())) {
						docData.setOdocdt(simpleDateFormat.parse(docs.getOdocdt()));
					}
					if(isNotEmpty(docs.getIgst())) {
						docData.setIamt(docs.getIgst());
						totalTaxVal += docs.getIgst();
						totalIgstVal += docs.getIgst();
					}else {
						docData.setIamt(0d);
					}
					if(isNotEmpty(docs.getCgst())) {
						docData.setCamt(docs.getCgst());
						totalTaxVal += docs.getCgst();
						totalCgstVal += docs.getCgst();
					}else {
						docData.setCamt(0d);
					}
					if(isNotEmpty(docs.getSgst())) {
						docData.setSamt(docs.getSgst());
						totalTaxVal += docs.getSgst();
						totalSgstVal += docs.getSgst();
					}else {
						docData.setSamt(0d);
					}
					
					if(isNotEmpty(docs.getCess())) {
						docData.setCess(docs.getCess());
						totalTaxVal += docs.getCess();
						totalCessVal += docs.getCess();
					}else {
						docData.setCess(0d);
					}
					docList.add(docData);
					Item item = populateGSTR2ISD(docData);
					itemsList.add(item);
					gstrisd.setDoclist(docList);
					gstrisdList.add(gstrisd);
					individualInvoice.setItems(itemsList);
					individualInvoice.setFp(returnPeriod);
					individualInvoice.setInvtype(MasterGSTConstants.ISDA);
					individualInvoice.setBilledtoname(isd.getTrdnm());
					individualInvoice.setUserid(userid);
					individualInvoice.setClientid(client.getId().toString());
					individualInvoice.setDocKey(key);
					individualInvoice.setFullname(fullname);
					individualInvoice.setBranch("Main Branch");
					individualInvoice.setTotalamount(totalTaxVal);
					individualInvoice.setTotalamount_str(String.format(DOUBLE_FORMAT, totalTaxVal));
					individualInvoice.setTotaltax(totalTaxVal);
					individualInvoice.setTotaltax_str(String.format(DOUBLE_FORMAT, totalTaxVal));
					individualInvoice.setTotalIgstAmount(totalIgstVal);
					individualInvoice.setTotalCgstAmount(totalCgstVal);
					individualInvoice.setTotalSgstAmount(totalSgstVal);
					individualInvoice.setTotalCessAmount(totalCessVal);
					individualInvoice.setCsftr(1);
					individualInvoice.setSftr(1);
					individualInvoice.setIsda(gstrisdList);
					invLst.add(individualInvoice);
				}
				
			}
			
			if(invLst.size() > 0) {
				gstr2bSupportRepository.save(invLst);
			}
		}
	}
	private Item populateGSTR2ISD(GSTRDocListDetails docData) {
		Item item = new Item();
		Double totalVal = 0d;
		item.setIgstamount(docData.getIamt());
		item.setCgstamount(docData.getCamt());
		item.setSgstamount(docData.getSamt());
		item.setCessamount(docData.getCess());
		totalVal += docData.getIamt();
		totalVal += docData.getCamt();
		totalVal += docData.getSamt();
		totalVal += docData.getCess();
		item.setTotal(totalVal);
		item.setLedgerName("PURCHASES");
		if(docData.getItcElg().equals("Y")) {
			item.setIsdType("Eligible - Credit distributed");
		}else {
			item.setIsdType("Ineligible - Credit distributed");
		}
		return item;
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
					individualInvoice.setDateofinvoice_str(dateFormatOnlyDate.format(gstrInvoiceDetails.getNtDt()));
				}
			}else {
				if(isNotEmpty(gstrInvoiceDetails.getIdt())) {
					individualInvoice.setDateofinvoice(simpleDateFormat.parse(gstrInvoiceDetails.getIdt()));
					individualInvoice.setDateofinvoice_str(dateFormatOnlyDate.format(simpleDateFormat.parse(gstrInvoiceDetails.getIdt())));
				}
			}
		} catch (ParseException e) {
			logger.error(CLASSNAME + "populateGSTR2B :: ERROR in invoice date", e);
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
			if (isNotEmpty(gstrInvoiceDetails) && isNotEmpty(gstrInvoiceDetails.getRchrg())) {
				if ("Y".equalsIgnoreCase(gstrInvoiceDetails.getRchrg())) {
					individualInvoice.setRevchargetype("Reverse");
				} else {
					individualInvoice.setRevchargetype("Regular");
				}
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
		String totalAmtStr = String.format(DOUBLE_FORMAT,gstrInvoiceDetails.getVal());
		individualInvoice.setTotalamount_str(totalAmtStr);
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
		individualInvoice.setTotaltaxableamount_str(String.format(DOUBLE_FORMAT,totalTaxVal));
		individualInvoice.setTotaltax(totalTax);
		individualInvoice.setTotaltax_str(String.format(DOUBLE_FORMAT,totalTax));
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
					individualInvoice.setDateofinvoice_str(dateFormatOnlyDate.format(simpleDateFormat.parse(gstrImportDetailss.getBedt())));
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
			individualInvoice.setTotalamount_str(String.format(DOUBLE_FORMAT,totalval));
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
		individualInvoice.setTotaltaxableamount_str(String.format(DOUBLE_FORMAT,totalTaxVal));
		individualInvoice.setTotaltax(totalTax);
		individualInvoice.setTotaltax_str(String.format(DOUBLE_FORMAT,totalTax));
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
	
public ByteArrayInputStream supplierFilingStatusToExcel(List<FilingStatusReportsVO> InvoicesList,List<String> headers) throws IOException {
	    
	    try(
	        Workbook workbook = new XSSFWorkbook();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ){
	      CreationHelper createHelper = workbook.getCreationHelper();
	      Sheet sheet = workbook.createSheet("Supplier_Filing_Status");
	      
	      Font headerFont = workbook.createFont();
	      headerFont.setBold(true);
	      headerFont.setColor(IndexedColors.BLUE.getIndex());
	      
	      CellStyle headerCellStyle = workbook.createCellStyle();
	      headerCellStyle.setFont(headerFont);
	      headerCellStyle.setAlignment(headerCellStyle.ALIGN_CENTER);
	      
	      // Row for Header
	      Row headerRow = sheet.createRow(0);
	      
	      // Header
	      for (int col = 0; col < headers.size(); col++) {
	        Cell cell = headerRow.createCell(col);
	        cell.setCellValue(headers.get(col));
	        cell.setCellStyle(headerCellStyle);
	        sheet.autoSizeColumn(col);
	      }
	    	   int b2bIdx = 1;
	    	   if(isNotEmpty(InvoicesList)) {
	 		      for (FilingStatusReportsVO invoice : InvoicesList) {
	 		    	 Row row1 = sheet.createRow(b2bIdx++);
	 		    	row1.createCell(0).setCellValue(invoice.getSupplierName());
				    row1.createCell(1).setCellValue(invoice.getArnNo());
				    row1.createCell(2).setCellValue(invoice.getGstin());
				    row1.createCell(3).setCellValue(invoice.getModeOfFiling());
				    row1.createCell(4).setCellValue(invoice.getDateOfFiling());
				    row1.createCell(5).setCellValue(invoice.getReturnType());
				    row1.createCell(6).setCellValue(invoice.getRetPeriod());
				    row1.createCell(7).setCellValue(invoice.getStatus());
	 		      }
	    	   } 
	      //Setting Auto Column Width
           sheet.autoSizeColumn(0);
           sheet.setColumnWidth(0,50 * 256);
	      workbook.write(out);
	      return new ByteArrayInputStream(out.toByteArray());
	    }
	  }
public ByteArrayInputStream supplierFilingStatusToMultipleExcel(List<List<FilingStatusReportsVO>> InvoicesLists,List<String> headers,HttpServletResponse response) throws IOException {
	OutputStream nout = null;
	ZipOutputStream zipOutputStream = null;
	nout = response.getOutputStream();
	String fileName = "demo.zip";
	response.setHeader("Content-Disposition", "attachment; filename="+fileName);	
	response.setContentType("application/octet-stream; charset=utf-8");
	zipOutputStream = new ZipOutputStream(nout);
	int i =1;
	for(List<FilingStatusReportsVO> InvoicesList: InvoicesLists) {
	    try(
	        Workbook workbook = new XSSFWorkbook();
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ){
	      CreationHelper createHelper = workbook.getCreationHelper();
	      Sheet sheet = workbook.createSheet("Supplier_Filing_Status");
	      
	      Font headerFont = workbook.createFont();
	      headerFont.setBold(true);
	      headerFont.setColor(IndexedColors.BLUE.getIndex());
	      
	      CellStyle headerCellStyle = workbook.createCellStyle();
	      headerCellStyle.setFont(headerFont);
	      headerCellStyle.setAlignment(headerCellStyle.ALIGN_CENTER);
	      
	      // Row for Header
	      Row headerRow = sheet.createRow(0);
	      
	      // Header
	      for (int col = 0; col < headers.size(); col++) {
	        Cell cell = headerRow.createCell(col);
	        cell.setCellValue(headers.get(col));
	        cell.setCellStyle(headerCellStyle);
	        sheet.autoSizeColumn(col);
	      }
	    	   int b2bIdx = 1;
	    	   if(isNotEmpty(InvoicesList)) {
	 		      for (FilingStatusReportsVO invoice : InvoicesList) {
	 		    	 Row row1 = sheet.createRow(b2bIdx++);
	 		    	row1.createCell(0).setCellValue(invoice.getSupplierName());
				    row1.createCell(1).setCellValue(invoice.getArnNo());
				    row1.createCell(2).setCellValue(invoice.getGstin());
				    row1.createCell(3).setCellValue(invoice.getModeOfFiling());
				    row1.createCell(4).setCellValue(invoice.getDateOfFiling());
				    row1.createCell(5).setCellValue(invoice.getReturnType());
				    row1.createCell(6).setCellValue(invoice.getRetPeriod());
				    row1.createCell(7).setCellValue(invoice.getStatus());
	 		      }
	    	   } 
	      //Setting Auto Column Width
	       sheet.autoSizeColumn(0);
	       sheet.setColumnWidth(0,50 * 256);
	      workbook.write(out);
	      ZipEntry entry = new ZipEntry("demo"+(i+1)+ ".xlsx");
			zipOutputStream.putNextEntry(entry);
			out.writeTo(zipOutputStream);
			zipOutputStream.closeEntry();
			out.close();
			workbook.close();
			i++;

    }
      //return new ByteArrayInputStream(out.toByteArray());
      
    }
	nout.flush();  
    zipOutputStream.flush();
	return null;
  }
	
}
