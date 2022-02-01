package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.GSTR4AnnualDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicAddress;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicAddressData;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR4A;
import com.mastergst.usermanagement.runtime.domain.GSTR4Annual;
import com.mastergst.usermanagement.runtime.domain.GSTR4B2B;
import com.mastergst.usermanagement.runtime.domain.GSTR4B2C;
import com.mastergst.usermanagement.runtime.domain.GSTR4CMPData;
import com.mastergst.usermanagement.runtime.domain.GSTR4IMPS;
import com.mastergst.usermanagement.runtime.domain.GSTR4ImpsItem;
import com.mastergst.usermanagement.runtime.domain.GSTR4Items;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRImportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRImportItems;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6A;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR4ARepository;
import com.mastergst.usermanagement.runtime.repository.GSTR4AnnualRepository;
import com.mastergst.usermanagement.runtime.response.gstr4.GSTR4AnnualCMPResponse;
import com.mastergst.usermanagement.runtime.response.gstr4.GSTR4AnnualCMPResponseData;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
public class Gstr4AnnualServiceImpl implements Gstr4AnnualService {
	
	private static final Logger logger = LogManager.getLogger(Gstr4AnnualServiceImpl.class.getName());
	private static final String CLASSNAME = "Gstr4AnnualServiceImpl::";
	@Autowired
	private ClientService clientService;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	private GSTR4ARepository gstr4aRepository;
	@Autowired
	private GSTR4AnnualRepository gstr4AnnualRepository;
	@Autowired
	private OtpExpiryService otpExpiryService;
	@Autowired
	private IHubConsumerService ihubConsumerService;
	@Autowired
	private GSTR4AnnualDao gstr4annualDao;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ClientUserMappingRepository clientUserMappingRepository;
	@Override
	public GSTR4Annual getGstr4AnnualInvoices(String clientid, String fp) {
		
		GSTR4Annual gstr4Annual =gstr4AnnualRepository.findByClientidAndFp(clientid, fp);
		
		return gstr4Annual;
	}
	
	
	@Override
	public void saveInvoices(GSTR4Annual gstr4Annual, User user, Client clientid, String fp) {
		gstr4AnnualRepository.save(gstr4Annual);		
	}
	
	@Override
	public void deleteGstr4AnnualData(String clientid, String fp) {
		
		GSTR4Annual gstr4Annual =gstr4AnnualRepository.findByClientidAndFp(clientid, fp);
		if(isNotEmpty(gstr4Annual)) {
			gstr4AnnualRepository.delete(gstr4Annual);			
		}
	}
	
	@Override
	public void autoCalculateData(GSTR4Annual gstr4Annual, Client client, int year) {
		
		List<? extends InvoiceParent> b2borinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B","Regular").getContent();
		List<? extends InvoiceParent> b2brinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B","Reverse").getContent();
		List<? extends InvoiceParent> b2burinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B Unregistered","").getContent();
		List<? extends InvoiceParent> impginvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),MasterGSTConstants.IMP_SERVICES,"").getContent();
		
		if(isNotEmpty(b2borinvoices)) {

			Map<String,GSTR4B2B> dataMap = new HashMap<>();
			for(InvoiceParent invoice : b2borinvoices) {
				
				if(isNotEmpty(invoice.getB2b())) {
					GSTR4B2B gstr4B2b = null;
					
					String ctin = invoice.getB2b().get(0).getCtin();
					if(dataMap.containsKey(ctin)) {
						
						gstr4B2b = dataMap.get(ctin);
						List<GSTRItems> gstrItems = invoice.getB2b().get(0).getInv().get(0).getItms();
						Map<Double, GSTR4Items> gstr4ItmMap = gstr4B2b.getItms()
																	.stream()
																	.collect(Collectors.toMap(GSTR4Items::getRt, item -> item));
						for(GSTRItems gstrItem : gstrItems) {
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
								txval = itmDetails.getTxval();
							}
							if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
								iamt = itmDetails.getIamt();									
							}
							if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
								camt = itmDetails.getCamt();
							}
							if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
								samt = itmDetails.getSamt();
							}
							if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
								csamt = itmDetails.getCsamt();
							}
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
																					
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(itmDetails.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values()
												.stream().collect(Collectors.toList());
						gstr4B2b.setItms(gst4Iitems);
						
					}else {
						String pos = invoice.getB2b().get(0).getInv().get(0).getPos();
						
						gstr4B2b = new GSTR4B2B();
						gstr4B2b.setCtin(ctin);
						gstr4B2b.setPos(pos);
						List<GSTRItems> gstrItems = invoice.getB2b().get(0).getInv().get(0).getItms();
						
						Map<Double, GSTR4Items> gstr4ItmMap = new HashMap<>();
						for(GSTRItems gstrItem : gstrItems) {
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;
							
							if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
								txval = itmDetails.getTxval();
							}
							if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
								iamt = itmDetails.getIamt();									
							}
							if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
								camt = itmDetails.getCamt();
							}
							if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
								samt = itmDetails.getSamt();
							}
							if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
								csamt = itmDetails.getCsamt();
							}
														
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
																					
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(itmDetails.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values().stream().collect(Collectors.toList());
						gstr4B2b.setItms(gst4Iitems);
					}
					
					dataMap.put(ctin, gstr4B2b);	
				}
			}
			if(isNotEmpty(dataMap.values())) {
				List<GSTR4B2B> b2bor = dataMap.values()
												.stream().collect(Collectors.toList());
				gstr4Annual.setB2bor(b2bor);				
			}
		}
		if(isNotEmpty(b2brinvoices)){

			Map<String,GSTR4B2B> dataMap = new HashMap<>();
			for(InvoiceParent invoice : b2brinvoices) {
				
				if(isNotEmpty(invoice.getB2b())) {
					GSTR4B2B gstr4B2b = null;
					
					String ctin = invoice.getB2b().get(0).getCtin();
					if(dataMap.containsKey(ctin)) {
						
						gstr4B2b = dataMap.get(ctin);
						List<GSTRItems> gstrItems = invoice.getB2b().get(0).getInv().get(0).getItms();
						Map<Double, GSTR4Items> gstr4ItmMap = gstr4B2b.getItms()
																	.stream()
																	.collect(Collectors.toMap(GSTR4Items::getRt, item -> item));
						for(GSTRItems gstrItem : gstrItems) {
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
								txval = itmDetails.getTxval();
							}
							if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
								iamt = itmDetails.getIamt();									
							}
							if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
								camt = itmDetails.getCamt();
							}
							if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
								samt = itmDetails.getSamt();
							}
							if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
								csamt = itmDetails.getCsamt();
							}
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
																					
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(itmDetails.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values().stream().collect(Collectors.toList());
						gstr4B2b.setItms(gst4Iitems);
					}else {
						String pos = invoice.getB2b().get(0).getInv().get(0).getPos();
						
						gstr4B2b = new GSTR4B2B();
						gstr4B2b.setCtin(ctin);
						gstr4B2b.setPos(pos);
						List<GSTRItems> gstrItems = invoice.getB2b().get(0).getInv().get(0).getItms();
						
						Map<Double, GSTR4Items> gstr4ItmMap = new HashMap<>();
						for(GSTRItems gstrItem : gstrItems) {
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;
							
							if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
								txval = itmDetails.getTxval();
							}
							if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
								iamt = itmDetails.getIamt();									
							}
							if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
								camt = itmDetails.getCamt();
							}
							if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
								samt = itmDetails.getSamt();
							}
							if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
								csamt = itmDetails.getCsamt();
							}
														
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
																					
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(itmDetails.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values().stream().collect(Collectors.toList());
						gstr4B2b.setItms(gst4Iitems);
					}
					
					dataMap.put(ctin, gstr4B2b);	
				}
				
			}
			if(isNotEmpty(dataMap.values())) {
				List<GSTR4B2B> b2br = dataMap.values()
											.stream().collect(Collectors.toList());
				gstr4Annual.setB2br(b2br);				
			}
		}
		if(isNotEmpty(b2burinvoices)) {
			Map<String,GSTR4B2C> dataMap = new HashMap<>();
			for(InvoiceParent invoice : b2burinvoices) {
				
				if(isNotEmpty(invoice.getB2b())) {
					GSTR4B2C gstr4B2c = null;
					
					String pos = ((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getPos();
					if(dataMap.containsKey(pos)) {
						
						gstr4B2c = dataMap.get(pos);
						List<GSTRItems> gstrItems = ((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms();
						Map<Double, GSTR4Items> gstr4ItmMap = gstr4B2c.getItms()
																	.stream()
																	.collect(Collectors.toMap(GSTR4Items::getRt, item -> item));
						for(GSTRItems gstrItem : gstrItems) {
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());

								if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
									txval = itmDetails.getTxval();
								}
								if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
									iamt = itmDetails.getIamt();									
								}
								if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
									camt = itmDetails.getCamt();
								}
								if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
									samt = itmDetails.getSamt();
								}
								if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
									csamt = itmDetails.getCsamt();
								}
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
												
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(itmDetails.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values().stream().collect(Collectors.toList());
						gstr4B2c.setItms(gst4Iitems);
					}else {
						
						gstr4B2c = new GSTR4B2C();
						gstr4B2c.setPos(pos);
						gstr4B2c.setTrdnm(client.getBusinessname());
						String rchrg = "N";
						if(isNotEmpty(invoice.getRevchargetype())) {
							rchrg = invoice.getRevchargetype();
						}
						gstr4B2c.setRchrg(rchrg);;
						
						String splyty = client.getGstnnumber().substring(0, 2).equals(pos) ? "INTRA" : "INTER";
						
						gstr4B2c.setSply_ty(splyty);
						
						List<GSTRItems> gstrItems = ((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms();
						
						Map<Double, GSTR4Items> gstr4ItmMap = new HashMap<>();
						for(GSTRItems gstrItem : gstrItems) {
							GSTR4Items item = null;
							GSTRItemDetails itmDetails = gstrItem.getItem();
							Double txval = 0d, iamt = 0d, camt = 0d, samt = 0d, csamt = 0d;

							if(isNotEmpty(itmDetails.getTxval())  && itmDetails.getTxval() !=null){
								txval = itmDetails.getTxval();
							}
							if(isNotEmpty(itmDetails.getIamt()) && itmDetails.getIamt() !=null) {
								iamt = itmDetails.getIamt();									
							}
							if(isNotEmpty(itmDetails.getCamt()) && itmDetails.getCamt() !=null) {
								camt = itmDetails.getCamt();
							}
							if(isNotEmpty(itmDetails.getSamt())  && itmDetails.getSamt() !=null) {
								samt = itmDetails.getSamt();
							}
							if(isNotEmpty(itmDetails.getCsamt())  && itmDetails.getCsamt() !=null) {
								csamt = itmDetails.getCsamt();
							}
							
							if(gstr4ItmMap.containsKey(itmDetails.getRt())) {
								item = gstr4ItmMap.get(itmDetails.getRt());
								
								item.setTxval(item.getTxval()+txval);
								item.setIamt(item.getIamt()+iamt);
								item.setCamt(item.getCamt()+camt);
								item.setSamt(item.getSamt()+samt);
								item.setCsamt(item.getCsamt()+csamt);
							}else {
								item = new GSTR4Items();
								item.setRt(itmDetails.getRt());
													
								item.setTxval(txval);
								item.setIamt(iamt);
								item.setCamt(camt);
								item.setSamt(samt);
								item.setCsamt(csamt);
							}
							gstr4ItmMap.put(item.getRt(), item);
						}
						List<GSTR4Items> gst4Iitems = gstr4ItmMap.values().stream().collect(Collectors.toList());
						gstr4B2c.setItms(gst4Iitems);
					}
					dataMap.put(pos, gstr4B2c);
				}
			}
			if(isNotEmpty(dataMap.values())) {
				List<GSTR4B2C> b2bc =  dataMap.values()
											.stream().collect(Collectors.toList());
				gstr4Annual.setB2bur(b2bc);				
			}
		}
		if(isNotEmpty(impginvoices)) {
			Map<String, GSTR4IMPS> dataMap = new HashMap<>();
			for (InvoiceParent invoice : impginvoices) {
				GSTRImportDetails invImps = ((PurchaseRegister) invoice).getImpServices().get(0);
				String pos = invoice.getStatename().substring(0, 2);			
				List<GSTRImportItems> invImpsItms = invImps.getItms();
				Map<Double, GSTR4ImpsItem> gstr4ItmMap = null;
				
				if(dataMap.containsKey(pos)) {
					
					GSTR4IMPS existImps = dataMap.get(pos);
					gstr4ItmMap = existImps.getItms()
										.stream()
										.collect(Collectors.toMap(GSTR4ImpsItem::getRt, item -> item));
					for(GSTRImportItems invitem:  invImpsItms) {
						GSTR4ImpsItem item = null;
						Double txval = 0d, iamt = 0d, csamt = 0d;
						if(isNotEmpty(invitem.getTxval()) && invitem.getTxval() !=null) {
							txval = invitem.getTxval();
						}
						if(isNotEmpty(invitem.getIamt())  && invitem.getIamt() !=null) {
							iamt = invitem.getIamt();									
						}
						
						if(isNotEmpty(invitem.getCsamt())  && invitem.getCsamt() !=null) {
							csamt = invitem.getCsamt();
						}
						if(gstr4ItmMap.containsKey(invitem.getRt())) {
							item = gstr4ItmMap.get(invitem.getRt());
							
							item.setTxval(item.getTxval()+txval);
							item.setIamt(item.getIamt()+iamt);
							item.setCsamt(item.getCsamt()+csamt);
							
						}else {
							GSTR4IMPS imps = new GSTR4IMPS();
							item = new GSTR4ImpsItem();
							imps.setPos(pos);
							item.setRt(invitem.getRt());
																								
							item.setTxval(txval);
							item.setIamt(iamt);
							item.setCsamt(csamt);
						}
						gstr4ItmMap.put(invitem.getRt(), item);
					}
					List<GSTR4ImpsItem> impsItems = gstr4ItmMap.values()
													.stream().collect(Collectors.toList());
					existImps.setItms(impsItems);
					dataMap.put(pos, existImps);

				}else {
					gstr4ItmMap = new HashMap<>();
					GSTR4ImpsItem item = null;
					GSTR4IMPS imps = new GSTR4IMPS();
					for(GSTRImportItems invitem:  invImpsItms) {
						Double txval = 0d, iamt = 0d,  csamt = 0d;
						if(isNotEmpty(invitem.getTxval()) && invitem.getTxval() !=null) {
							txval = invitem.getTxval();
						}
						if(isNotEmpty(invitem.getIamt())  && invitem.getIamt() !=null) {
							iamt = invitem.getIamt();									
						}
						
						if(isNotEmpty(invitem.getCsamt())  && invitem.getCsamt() !=null) {
							csamt = invitem.getCsamt();
						}
	
						
						if(gstr4ItmMap.containsKey(invitem.getRt())) {
							item = gstr4ItmMap.get(invitem.getRt());
								
							item.setTxval(item.getTxval()+txval);
							item.setIamt(item.getIamt()+iamt);
							item.setCsamt(item.getCsamt()+csamt);
								
						}else {
								
							item = new GSTR4ImpsItem();
							imps.setPos(pos);
							item.setRt(invitem.getRt());
																									
							item.setTxval(txval);
							item.setIamt(iamt);
							item.setCsamt(csamt);
						}
						gstr4ItmMap.put(invitem.getRt(), item);
					}
					List<GSTR4ImpsItem> impsItems = gstr4ItmMap.values()
														.stream().collect(Collectors.toList());
					imps.setItms(impsItems);				
					dataMap.put(pos, imps);
				}
			}
			if(isNotEmpty(dataMap.values())) {
				List<GSTR4IMPS> imps = dataMap.values()
											.stream().collect(Collectors.toList());;
				gstr4Annual.setImps(imps);			
			}
		}
	}
	
	@Override
	public void syncGstr4AnnualCmp(User user, Client client, String fp) {
		logger.debug(CLASSNAME + "getGstr4AnnualCmp:: Begin");
				
		String usrid = user.getId().toString();
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser.getCompany())){
				if(companyUser.getCompany().contains(client.getId().toString())){
					usrid = user.getParentid();
				}
			}
		}
		
		String userid = userid(user.getId().toString(), client.getId().toString());
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			Response errorResponse = new Response();
			errorResponse.setStatuscd("0");
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				errorResponse.setStatusdesc(errormsg);
			}else{
				errorResponse.setStatusdesc("Your subscription has expired. Kindly subscribe to proceed further!");
			}
		}
		GSTR4AnnualCMPResponse response = null;
		try {
			response = iHubConsumerService.getAnnualCmp(client, client.getGstnnumber(), fp);
			//if (isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals("1")) {
				saveGstr4AnnualCmpResponseInvoices(response.getData(), user, client, fp);
			//}
		} catch (Exception e) {
			logger.debug(CLASSNAME + e);
		}
	}
	
	@Override
	public void saveGstr4AnnualCmpResponseInvoices(GSTR4AnnualCMPResponseData data, User user, Client client, String fp) {
		
		GSTR4Annual gstr4Annual =gstr4AnnualRepository.findByClientidAndFp(client.getId().toString(), fp);
		
		if(isNotEmpty(gstr4Annual)) {
			GSTR4CMPData cmpdata = new GSTR4CMPData();
			try {
				BeanUtils.copyProperties(cmpdata, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			gstr4Annual.setCmpdata(cmpdata);
		}else {
			gstr4Annual = new GSTR4Annual();
			
			gstr4Annual.setUserid(user.getId().toString());
			gstr4Annual.setClientid(client.getId().toString());
			gstr4Annual.setGstin(client.getGstnnumber());
			gstr4Annual.setFullname(user.getFullname());
			gstr4Annual.setFp(fp);
			
			GSTR4CMPData cmpdata = new GSTR4CMPData();
			
			try {
				BeanUtils.copyProperties(cmpdata, data);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			gstr4Annual.setCmpdata(cmpdata);
			
		}
		gstr4AnnualRepository.save(gstr4Annual);
	}
	
	public String userid(String id,String clientid) {
		String userid = id;
		User user = userService.findById(userid);
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(NullUtil.isNotEmpty(companyUser)) {
				List<String> clntids = companyUser.getCompany();
				if(isNotEmpty(clntids) && clntids.size() > 0){
					if(clntids.contains(clientid)){
						ClientUserMapping clntusermapping = clientUserMappingRepository.findByClientidAndCreatedByIsNotNull(clientid);
						if(isNotEmpty(clntusermapping) && isNotEmpty(clntusermapping.getCreatedBy())) {
							userid = clntusermapping.getCreatedBy();
						}else {
							userid = user.getParentid();
						}
					}
				}else {
					userid = user.getParentid();
				}
			}
		}
		return userid;
	}
	
	public String primaryHolderMessage(Client client, User usr) {
		
		String message = "Primary Account Holder of <span style='color:blue;'>"+client.getBusinessname()+"</span> is Subscription is expired, please contact <span style='color:blue'>"+usr.getFullname()+","+usr.getEmail()+" & "+usr.getMobilenumber()+"</span> to renew";
		return message;
	}
	@Override
	@Transactional
	public GSTR4Annual saveGSTR4AnnualInvoice(GSTR4Annual gstr4,Client client,int month,int year) {
		logger.debug(CLASSNAME + "saveGSTR4AnnualInvoice : Begin");
		//String strMonth = month < 10 ? "0" + month : month + "";
		String fp = "03"+(year);
		gstr4AnnualRepository.deleteByClientidAndFp(client.getId().toString(),fp);
		List<? extends InvoiceParent> b2borinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B","Regular").getContent();
		
		//GSTR4Annual invoice = new GSTR4Annual();
		
		List<GSTR4B2B> g4b2blist= Lists.newArrayList();
		if(isNotEmpty(b2borinvoices)) {
			List<GSTRB2B> b2bor=Lists.newArrayList();
			for (InvoiceParent invoiceParent : b2borinvoices) {
				b2bor.addAll( invoiceParent.getB2b());
			}
			if(isNotEmpty(b2bor)) {
				Map<String, List<GSTRInvoiceDetails>> ctinMap =  consolidatedGSTR4CtinMap(b2bor);
				
				for(String ctin: ctinMap.keySet()) {
					List<GSTRInvoiceDetails> invdtls = ctinMap.get(ctin);
					List<GSTR4Items> g4Itms = Lists.newArrayList();
					GSTR4B2B b2b=new GSTR4B2B();
					b2b.setCtin(ctin);
					for(GSTRInvoiceDetails invdt: invdtls) {
						for(GSTRItems gstritm : invdt.getItms()) {
							GSTR4Items gstr4items = new GSTR4Items();
							gstr4items.setRt(gstritm.getItem().getRt());
							gstr4items.setTxval(gstritm.getItem().getTxval());
							gstr4items.setIamt(gstritm.getItem().getIamt());
							gstr4items.setCamt(gstritm.getItem().getCamt());
							gstr4items.setSamt(gstritm.getItem().getSamt());
							gstr4items.setCsamt(gstritm.getItem().getCsamt());
							g4Itms.add(gstr4items);
							GSTR4B2B gstr4b2bor = consolidatedGSTR4ItemRate(g4Itms,b2b);
							g4b2blist.add(gstr4b2bor);
							
							gstr4.setB2bor(g4b2blist);
						}
					}
				}
				//gstr4AnnualRepository.save(gstr4);
			}	
		}
		List<? extends InvoiceParent> b2brinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B","Reverse").getContent();
		List<GSTR4B2B> g4b2brlist= Lists.newArrayList();
		if(isNotEmpty(b2brinvoices)) {
			List<GSTRB2B> b2br=Lists.newArrayList();
			for (InvoiceParent invoiceParent : b2brinvoices) {
				b2br.addAll(invoiceParent.getB2b());
			}
			if(isNotEmpty(b2br)) {
				Map<String, List<GSTRInvoiceDetails>> ctinMap =  consolidatedGSTR4CtinMap(b2br);
				
				for(String ctin: ctinMap.keySet()) {
					List<GSTRInvoiceDetails> invdtls = ctinMap.get(ctin);
					List<GSTR4Items> g4Itms = Lists.newArrayList();
					GSTR4B2B b2b=new GSTR4B2B();
					b2b.setCtin(ctin);
					for(GSTRInvoiceDetails invdt: invdtls) {
						for(GSTRItems gstritm : invdt.getItms()) {
							GSTR4Items gstr4items = new GSTR4Items();
							gstr4items.setRt(gstritm.getItem().getRt());
							gstr4items.setTxval(gstritm.getItem().getTxval());
							gstr4items.setIamt(gstritm.getItem().getIamt());
							gstr4items.setCamt(gstritm.getItem().getCamt());
							gstr4items.setSamt(gstritm.getItem().getSamt());
							gstr4items.setCsamt(gstritm.getItem().getCsamt());
							g4Itms.add(gstr4items);
							GSTR4B2B gstr4b2br = consolidatedGSTR4ItemRate(g4Itms,b2b);
							g4b2brlist.add(gstr4b2br);
							
							gstr4.setB2br(g4b2brlist);
						}
					}
				}
				//gstr4AnnualRepository.save(gstr4);
			}	
		}
	
		List<GSTR4B2C> g4b2clist= Lists.newArrayList();
		List<? extends InvoiceParent> b2burinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),"B2B Unregistered","").getContent();
		if(isNotEmpty(b2burinvoices)) {
			List<GSTRB2B> b2bur=Lists.newArrayList();
			for (InvoiceParent invoiceParent : b2burinvoices) {
				b2bur.addAll(((PurchaseRegister) invoiceParent).getB2bur());
			}
			if(isNotEmpty(b2bur)) {
				Map<String, List<GSTRInvoiceDetails>> posMap =  consolidatedGSTR4PosMap(b2bur);
				
				for(String pos: posMap.keySet()) {
					List<GSTRInvoiceDetails> invdtls = posMap.get(pos);
					List<GSTR4Items> g4Itms = Lists.newArrayList();
					GSTR4B2C b2c=new GSTR4B2C();
					b2c.setPos(pos);
					b2c.setCpan(client.getPannumber());
					b2c.setTrdnm(client.getBusinessname());
					b2c.setSply_ty("INTER");
					for(GSTRInvoiceDetails invdt: invdtls) {
						for(GSTRItems gstritm : invdt.getItms()) {
							GSTR4Items gstr4items = new GSTR4Items();
							gstr4items.setRt(gstritm.getItem().getRt());
							gstr4items.setTxval(gstritm.getItem().getTxval());
							gstr4items.setIamt(gstritm.getItem().getIamt());
							gstr4items.setCamt(gstritm.getItem().getCamt());
							gstr4items.setSamt(gstritm.getItem().getSamt());
							gstr4items.setCsamt(gstritm.getItem().getCsamt());
							g4Itms.add(gstr4items);
							GSTR4B2C gstr4b2br = consolidatedB2CItemDetails(g4Itms,b2c);
							g4b2clist.add(gstr4b2br);
							
							gstr4.setB2bur(g4b2clist);
						}
					}
				}
				
			}
			//gstr4AnnualRepository.save(gstr4);
		}
		
		List<GSTR4IMPS> g4impslist= Lists.newArrayList();
		List<? extends InvoiceParent> impsinvoices = gstr4annualDao.getSupplierWiseInvoices(client.getId().toString(), 0, Utility.getYearCode(4, year),MasterGSTConstants.IMP_SERVICES,"").getContent();
		if(isNotEmpty(impsinvoices)) {
			List<GSTRImportDetails> imps=Lists.newArrayList();
			for (InvoiceParent invoiceParent : impsinvoices) {
				imps.addAll(((PurchaseRegister) invoiceParent).getImpServices());
			}
			if(isNotEmpty(imps)) {
				Map<String, List<GSTRImportDetails>> posMap =  consolidatedGSTR4ImpsPosMap(imps);
				
				for(String pos: posMap.keySet()) {
					List<GSTRImportDetails> invdtls = posMap.get(pos);
					List<GSTR4ImpsItem> g4impsItms = Lists.newArrayList();
					GSTR4IMPS gstr4imps=new GSTR4IMPS();
					gstr4imps.setPos(pos);
					for(GSTRImportDetails invdt: invdtls) {
						for(GSTRImportItems gstritm : invdt.getItms()) {
							GSTR4ImpsItem gstr4items = new GSTR4ImpsItem();
							gstr4items.setRt(gstritm.getRt());
							gstr4items.setTxval(gstritm.getTxval());
							gstr4items.setIamt(gstritm.getIamt());
							gstr4items.setCsamt(gstritm.getCsamt());
							g4impsItms.add(gstr4items);
							GSTR4IMPS gstr4imp = consolidatedIMPSItemDetails(g4impsItms,gstr4imps);
							g4impslist.add(gstr4imp);
							
							gstr4.setImps(g4impslist);
						}
					}
				}
				
			}
			//gstr4AnnualRepository.save(gstr4);
		}
		
		
		return gstr4AnnualRepository.save(gstr4);
	}

	private GSTR4IMPS consolidatedIMPSItemDetails(List<GSTR4ImpsItem> g4impsItms, GSTR4IMPS gstr4imps) {
		if(isNotEmpty(g4impsItms)) {
			Map<Double, GSTR4ImpsItem> itemMap=Maps.newHashMap();
			for(GSTR4ImpsItem item : g4impsItms) {
				if(isNotEmpty(item.getRt())) {
					if(itemMap.keySet().contains(item.getRt())) {
						if(isNotEmpty(item.getTxval())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getTxval())) {
								itemMap.get(item.getRt()).setTxval(item.getTxval()
										+ itemMap.get(item.getRt()).getTxval());
							} else {
								itemMap.get(item.getRt()).setTxval(item.getTxval());
							}
						}
						if(isNotEmpty(item.getIamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getIamt())) {
								itemMap.get(item.getRt()).setIamt(item.getIamt()
										+ itemMap.get(item.getRt()).getIamt());
							} else {
								itemMap.get(item.getRt()).setIamt(item.getIamt());
							}
						}
						
						if(isNotEmpty(item.getCsamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCsamt())) {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt()
										+ itemMap.get(item.getRt()).getCsamt());
							} else {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt());
							}
						}
					} else {
						itemMap.put(item.getRt(), item);
					}
				}
			}
			List<GSTR4ImpsItem> gstrItems = Lists.newArrayList();
			int index = 1;
			for(GSTR4ImpsItem item : itemMap.values()) {
				//item.setNum(index++);
				gstrItems.add(item);
			}
			gstr4imps.setItms(gstrItems);
		}
		return gstr4imps;
	}
	private Map<String, List<GSTRImportDetails>> consolidatedGSTR4ImpsPosMap(List<GSTRImportDetails> imps) {
		Map<String, List<GSTRImportDetails>> posMap =  new HashMap<>();
		for(GSTRImportDetails impdtls : imps) {
				List<GSTRImportDetails> impss=Lists.newArrayList();
				if(posMap.containsKey(impdtls.getPos())){
					impss = posMap.get(impdtls.getPos());
					impss.add(impdtls);
				}else {
					impss.add(impdtls);
				}
				posMap.put(impdtls.getPos(), impss);	
		}
		return posMap;
	}

	private Map<String, List<GSTRInvoiceDetails>> consolidatedGSTR4CtinMap(List<GSTRB2B> b2bor) {
		Map<String, List<GSTRInvoiceDetails>> ctinMap =  new HashMap<>();
		for(GSTRB2B b2b : b2bor) {
			for(GSTRInvoiceDetails invdetails : b2b.getInv()) {
				List<GSTRInvoiceDetails> b2bors=Lists.newArrayList();
				if(ctinMap.containsKey(invdetails.getPos())){
					b2bors = ctinMap.get(invdetails.getPos());
					b2bors.add(invdetails);
				}else {
					b2bors.add(invdetails);
				}
				ctinMap.put(b2b.getCtin(), b2bors);				
			}
		}
		
		return ctinMap;
	}

	

	public Map<String, List<GSTRInvoiceDetails>> consolidatedGSTR4PosMap(List<GSTRB2B> b2bur) {
		Map<String, List<GSTRInvoiceDetails>> posMap =  new HashMap<>();
		for(GSTRB2B b2b : b2bur) {
			for(GSTRInvoiceDetails invdetails : b2b.getInv()) {
				List<GSTRInvoiceDetails> b2burs=Lists.newArrayList();
				if(posMap.containsKey(invdetails.getPos())){
					b2burs = posMap.get(invdetails.getPos());
					b2burs.add(invdetails);
				}else {
					b2burs.add(invdetails);
				}
				posMap.put(invdetails.getPos(), b2burs);				
			}
		}
		
		return posMap;
	}
	
	public GSTR4B2B consolidatedGSTR4ItemRate(List<GSTR4Items> invoiceDetails,GSTR4B2B b2b) {
		if(isNotEmpty(invoiceDetails)) {
			Map<Double, GSTR4Items> itemMap=Maps.newHashMap();
			for(GSTR4Items item : invoiceDetails) {
				if(isNotEmpty(item.getRt())) {
					if(itemMap.keySet().contains(item.getRt())) {
						if(isNotEmpty(item.getTxval())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getTxval())) {
								itemMap.get(item.getRt()).setTxval(item.getTxval()
										+ itemMap.get(item.getRt()).getTxval());
							} else {
								itemMap.get(item.getRt()).setTxval(item.getTxval());
							}
						}
						if(isNotEmpty(item.getIamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getIamt())) {
								itemMap.get(item.getRt()).setIamt(item.getIamt()
										+ itemMap.get(item.getRt()).getIamt());
							} else {
								itemMap.get(item.getRt()).setIamt(item.getIamt());
							}
						}
						if(isNotEmpty(item.getCamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCamt())) {
								itemMap.get(item.getRt()).setCamt(item.getCamt()
										+ itemMap.get(item.getRt()).getCamt());
							} else {
								itemMap.get(item.getRt()).setCamt(item.getCamt());
							}
						}
						if(isNotEmpty(item.getSamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getSamt())) {
								itemMap.get(item.getRt()).setSamt(item.getSamt()
										+ itemMap.get(item.getRt()).getSamt());
							} else {
								itemMap.get(item.getRt()).setSamt(item.getSamt());
							}
						}
						if(isNotEmpty(item.getCsamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCsamt())) {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt()
										+ itemMap.get(item.getRt()).getCsamt());
							} else {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt());
							}
						}
					} else {
						itemMap.put(item.getRt(), item);
					}
				}
			}
			List<GSTR4Items> gstrItems = Lists.newArrayList();
			int index = 1;
			for(GSTR4Items item : itemMap.values()) {
				//item.setNum(index++);
				gstrItems.add(item);
			}
			b2b.setItms(gstrItems);
		}
		return b2b;
	}
	
	public GSTR4B2C consolidatedB2CItemDetails(List<GSTR4Items> invoiceDetails,GSTR4B2C b2c) {
		if(isNotEmpty(invoiceDetails)) {
			Map<Double, GSTR4Items> itemMap=Maps.newHashMap();
			for(GSTR4Items item : invoiceDetails) {
				if(isNotEmpty(item.getRt())) {
					if(itemMap.keySet().contains(item.getRt())) {
						if(isNotEmpty(item.getTxval())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getTxval())) {
								itemMap.get(item.getRt()).setTxval(item.getTxval()
										+ itemMap.get(item.getRt()).getTxval());
							} else {
								itemMap.get(item.getRt()).setTxval(item.getTxval());
							}
						}
						if(isNotEmpty(item.getIamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getIamt())) {
								itemMap.get(item.getRt()).setIamt(item.getIamt()
										+ itemMap.get(item.getRt()).getIamt());
							} else {
								itemMap.get(item.getRt()).setIamt(item.getIamt());
							}
						}
						if(isNotEmpty(item.getCamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCamt())) {
								itemMap.get(item.getRt()).setCamt(item.getCamt()
										+ itemMap.get(item.getRt()).getCamt());
							} else {
								itemMap.get(item.getRt()).setCamt(item.getCamt());
							}
						}
						if(isNotEmpty(item.getSamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getSamt())) {
								itemMap.get(item.getRt()).setSamt(item.getSamt()
										+ itemMap.get(item.getRt()).getSamt());
							} else {
								itemMap.get(item.getRt()).setSamt(item.getSamt());
							}
						}
						if(isNotEmpty(item.getCsamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCsamt())) {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt()
										+ itemMap.get(item.getRt()).getCsamt());
							} else {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt());
							}
						}
					} else {
						itemMap.put(item.getRt(), item);
					}
				}
			}
			List<GSTR4Items> gstrItems = Lists.newArrayList();
			int index = 1;
			for(GSTR4Items item : itemMap.values()) {
				//item.setNum(index++);
				gstrItems.add(item);
			}
			b2c.setItms(gstrItems);
		}
		return b2c;
	}
	@Override
	public void performGSTR4Reconcile(Client client, String invType, String string, String clientid, String userid,int month, int year) {
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth + year;
		String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
		if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
			InvoiceParent gstr4a =null;
			try {
				gstr4a = ihubConsumerService.getGSTRXInvoices(client, client.getGstnnumber(), month, year,MasterGSTConstants.GSTR4A, invType, null, userid, true);
				if (isNotEmpty(gstr4a)) {
					System.out.println(gstr4a);
					List<GSTR4A> gstr4ainvs = gstr4aRepository.findByClientidAndFpAndIsAmendmentAndInvtype(clientid,retPeriod,true,invType);
					gstr4aRepository.delete(gstr4ainvs);
					gstr4a.setUserid(userid);
					gstr4a.setClientid(clientid);
					gstr4a.setAmendment(true);
					updateGSTR4AReturnData(gstr4a, invType, client.getGstnnumber(), clientid, retPeriod, month, year);
				}
			} catch (MasterGSTException e) {
				System.out.println(e.getMessage());
			}
			
		}
		
	}
	private void updateGSTR4AReturnData(InvoiceParent gstr4a, String invType, String gstnnumber, String clientid,String fp, int month, int year) {
		Client client = clientService.findById(clientid);
		List<GSTR4A> gstr4DwnldList = Lists.newArrayList();
		Pageable pageable = new PageRequest(0, 2, Sort.Direction.DESC, "createdDate");
		Map<String, ResponseData> gstnMap = Maps.newHashMap();
		if (invType.equals(B2B)|| invType.equals(MasterGSTConstants.B2BA)) {
			if (isNotEmpty(gstr4a.getB2b())) {
				List<GSTR4A> gstr4List = gstr4aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr4AList = Lists.newArrayList();
				List<String> gstr4Ids = Lists.newArrayList();
				for(GSTR4A g4 : gstr4List) {
					if(isNotEmpty(g4.getInvoiceno())) {
						gstr4Ids.add(g4.getInvoiceno());
						if(g4.isAmendment()) {
							String invNoCtin = g4.getInvoiceno();
							if(isNotEmpty(g4.getB2b())) {
								if(isNotEmpty(g4.getB2b().get(0).getCtin())) {
									String ctin = g4.getB2b().get(0).getCtin();
									invNoCtin = invNoCtin+ctin;
								}
								if(isNotEmpty(g4.getB2b().get(0).getInv().get(0).getIdt())) {
									String idt = g4.getB2b().get(0).getInv().get(0).getIdt();
									invNoCtin = invNoCtin+idt;
								}
							}
							gstr4AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRB2B gstrb2b : gstr4a.getB2b()) {
					if(isNotEmpty(gstrb2b.getCtin()) && !gstnMap.containsKey(gstrb2b.getCtin())) {
						Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(gstrb2b.getCtin(), pageable);
						if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
							GSTINPublicData publicData = page.getContent().get(0);
							ResponseData data = new ResponseData();
							if(isNotEmpty(publicData.getTradeNam())) {
								data.setTradeNam(publicData.getTradeNam());
							}else {
								data.setTradeNam(publicData.getLgnm());
							}
							data.setPradr(publicData.getPradr());
							gstnMap.put(gstrb2b.getCtin(), data);
						} else {
							Response response = ihubConsumerService.publicSearch(gstrb2b.getCtin());
							if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
									&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
								ResponseData data = response.getData();
								if(isNotEmpty(data.getTradeNam())) {
									data.setTradeNam(data.getTradeNam());
								}else {
									data.setTradeNam(data.getLgnm());
								}
								gstnMap.put(gstrb2b.getCtin(), data);
							}
						}
					}
					for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2b.getInv()) {
							String dwnldInvNoCtin = gstrInvoiceDetails.getInum();
							if(isNotEmpty(gstrb2b.getCtin())) {
								String dwnldctin = gstrb2b.getCtin();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
							}
							if(isNotEmpty(gstrInvoiceDetails.getIdt())) {
								String dwnlddate = gstrInvoiceDetails.getIdt();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnlddate;
							}
						
						if(isEmpty(gstr4AList) || !gstr4AList.contains(dwnldInvNoCtin)) {
							if(isNotEmpty(gstrb2b.getCtin()) && gstnMap.containsKey(gstrb2b.getCtin())) {
								String addr;
								GSTINPublicAddress gstinAddress = gstnMap.get(gstrb2b.getCtin()).getPradr();
								if(isNotEmpty(gstinAddress)){
									GSTINPublicAddressData gstinAddressData = gstinAddress.getAddr();
									if(isNotEmpty(gstinAddressData)){
										addr = gstinAddressData.getBnm() + "," + gstinAddressData.getSt() + ","
												+ gstinAddressData.getLoc() + "," + gstinAddressData.getDst() + ","
												+ gstinAddressData.getStcd() + "," + gstinAddressData.getCity() + ","
												+ gstinAddressData.getFlno() + "," + gstinAddressData.getPncd();
										gstrInvoiceDetails.setAddress(addr);
									}
								}
							}
							GSTR4A individualInvoice = populateGSTR4(gstrInvoiceDetails, gstnnumber, fp, gstr4a.getUserid(),
									gstr4a.getClientid(), invType, gstrb2b.getCtin(),gstrb2b.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							
							if(isNotEmpty(gstrb2b.getCtin()) && gstnMap.containsKey(gstrb2b.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrb2b.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr4a.isAmendment());
							gstr4DwnldList.add(individualInvoice);
						}
					}
				}
			}else if (isNotEmpty(((GSTR4A) gstr4a).getB2ba())) {
				List<GSTR4A> gstr4List = gstr4aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr4AList = Lists.newArrayList();
				List<String> gstr4Ids = Lists.newArrayList();
				for(GSTR4A g4 : gstr4List) {
					if(isNotEmpty(g4.getInvoiceno())) {
						gstr4Ids.add(g4.getInvoiceno());
						if(g4.isAmendment()) {
							String invNoCtin = g4.getInvoiceno();
							if(isNotEmpty(((GSTR4A) g4).getB2ba())) {
								if(isNotEmpty(((GSTR4A) g4).getB2ba().get(0).getCtin())) {
									String ctin = ((GSTR4A) g4).getB2ba().get(0).getCtin();
									invNoCtin = invNoCtin+ctin;
								}
								if(isNotEmpty(((GSTR4A) g4).getB2ba().get(0).getInv().get(0).getIdt())) {
									String idt = ((GSTR4A) g4).getB2ba().get(0).getInv().get(0).getIdt();
									invNoCtin = invNoCtin+idt;
								}
							}
							gstr4AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRB2B gstrb2ba : ((GSTR4A)gstr4a).getB2ba()) {
					if(isNotEmpty(gstrb2ba.getCtin()) && !gstnMap.containsKey(gstrb2ba.getCtin())) {
						Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(gstrb2ba.getCtin(), pageable);
						if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
							GSTINPublicData publicData = page.getContent().get(0);
							ResponseData data = new ResponseData();
							if(isNotEmpty(publicData.getTradeNam())) {
								data.setTradeNam(publicData.getTradeNam());
							}else {
								data.setTradeNam(publicData.getLgnm());
							}
							data.setPradr(publicData.getPradr());
							gstnMap.put(gstrb2ba.getCtin(), data);
						} else {
							Response response = ihubConsumerService.publicSearch(gstrb2ba.getCtin());
							if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
								ResponseData data = response.getData();
								if(isNotEmpty(data.getTradeNam())) {
									data.setTradeNam(data.getTradeNam());
								}else {
									data.setTradeNam(data.getLgnm());
								}
								gstnMap.put(gstrb2ba.getCtin(), data);
							}
						}
					}
					for (GSTRInvoiceDetails gstrInvoiceDetails : gstrb2ba.getInv()) {
							String dwnldInvNoCtin = gstrInvoiceDetails.getInum();
							if(isNotEmpty(gstrb2ba.getCtin())) {
								String dwnldctin = gstrb2ba.getCtin();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
							}
							if(isNotEmpty(gstrInvoiceDetails.getIdt())) {
								String dwnlddate = gstrInvoiceDetails.getIdt();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnlddate;
							}
						
						if(isEmpty(gstr4AList) || !gstr4AList.contains(dwnldInvNoCtin)) {
							if(isNotEmpty(gstrb2ba.getCtin()) && gstnMap.containsKey(gstrb2ba.getCtin())) {
								String addr;
								GSTINPublicAddress gstinAddress = gstnMap.get(gstrb2ba.getCtin()).getPradr();
								if(isNotEmpty(gstinAddress)){
									GSTINPublicAddressData gstinAddressData = gstinAddress.getAddr();
									if(isNotEmpty(gstinAddressData)){
										addr = gstinAddressData.getBnm() + "," + gstinAddressData.getSt() + ","
												+ gstinAddressData.getLoc() + "," + gstinAddressData.getDst() + ","
												+ gstinAddressData.getStcd() + "," + gstinAddressData.getCity() + ","
												+ gstinAddressData.getFlno() + "," + gstinAddressData.getPncd();
										gstrInvoiceDetails.setAddress(addr);
									}
								}
							}
							GSTR4A individualInvoice = populateGSTR4(gstrInvoiceDetails, gstnnumber, fp, gstr4a.getUserid(),
									gstr4a.getClientid(), invType, gstrb2ba.getCtin(),gstrb2ba.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							
							if(isNotEmpty(gstrb2ba.getCtin()) && gstnMap.containsKey(gstrb2ba.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrb2ba.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr4a.isAmendment());
							gstr4DwnldList.add(individualInvoice);
							//gstr2Repository.save(individualInvoice);
						}
					}
				}
			}	
	    }else if (invType.equals(CREDIT_DEBIT_NOTES) || invType.equals(MasterGSTConstants.CDNA)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			if (isNotEmpty(gstr4a.getCdn())) {
				List<GSTR4A> gstr4List = gstr4aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr4Ids = Lists.newArrayList();
				List<String> gstr4AList = Lists.newArrayList();
				for(GSTR4A g4 : gstr4List) {
					if(isNotEmpty(g4.getInvoiceno())) {
						gstr4Ids.add(g4.getInvoiceno());
						if(g4.isAmendment()) {
							String invNoCtin = g4.getInvoiceno();
						if(isNotEmpty(g4.getCdn())) {
							if(isNotEmpty(g4.getCdn().get(0).getCtin())) {
								String ctin = g4.getCdn().get(0).getCtin();
								invNoCtin = invNoCtin+ctin;
							}
							if(isNotEmpty(g4.getCdn().get(0).getNt().get(0).getNtDt())) {
								String idt = simpleDateFormat.format(g4.getCdn().get(0).getNt().get(0).getNtDt());
								invNoCtin = invNoCtin+idt;
							}
						}
							gstr4AList.add(invNoCtin);
						}
					}
				}
			
				for (GSTRCreditDebitNotes gstrcdn : gstr4a.getCdn()) {
					if(isNotEmpty(gstrcdn.getCtin()) && !gstnMap.containsKey(gstrcdn.getCtin())) {
						Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(gstrcdn.getCtin(), pageable);
						if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
							GSTINPublicData publicData = page.getContent().get(0);
							ResponseData data = new ResponseData();
							if(isNotEmpty(publicData.getTradeNam())) {
								data.setTradeNam(publicData.getTradeNam());
							}else {
								data.setTradeNam(publicData.getLgnm());
							}
							data.setPradr(publicData.getPradr());
							gstnMap.put(gstrcdn.getCtin(), data);
						} else {
							Response response = ihubConsumerService.publicSearch(gstrcdn.getCtin());
							if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
									&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
								ResponseData data = response.getData();
								if(isNotEmpty(data.getTradeNam())) {
									data.setTradeNam(data.getTradeNam());
								}else {
									data.setTradeNam(data.getLgnm());
								}
								gstnMap.put(gstrcdn.getCtin(), data);
							}
						}
					}
					
					for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdn.getNt()) {
						String dwnldInvNoCtin = gstrInvoiceDetails.getNtNum();
						if(isNotEmpty(gstrcdn.getCtin())) {
							String dwnldctin = gstrcdn.getCtin();
							dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
						}
						
						if(isNotEmpty(gstrInvoiceDetails.getNtDt())) {
							String idt = simpleDateFormat.format(gstrInvoiceDetails.getNtDt());
							dwnldInvNoCtin = dwnldInvNoCtin+idt;
						}
						
						if(isEmpty(gstr4AList) || !gstr4AList.contains(dwnldInvNoCtin)) {
							if(isNotEmpty(gstrcdn.getCtin()) && gstnMap.containsKey(gstrcdn.getCtin())) {
								String addr;
								GSTINPublicAddress gstinAddress = gstnMap.get(gstrcdn.getCtin()).getPradr();
								if(isNotEmpty(gstinAddress)){
									GSTINPublicAddressData gstinAddressData = gstinAddress.getAddr();
									if(isNotEmpty(gstinAddressData)){
										addr = gstinAddressData.getBnm() + "," + gstinAddressData.getSt() + ","
												+ gstinAddressData.getLoc() + "," + gstinAddressData.getDst() + ","
												+ gstinAddressData.getStcd() + "," + gstinAddressData.getCity() + ","
												+ gstinAddressData.getFlno() + "," + gstinAddressData.getPncd();
										gstrInvoiceDetails.setAddress(addr);
									}
								}
							}
							GSTR4A individualInvoice = populateGSTR4(gstrInvoiceDetails, gstnnumber, fp, gstr4a.getUserid(),
									gstr4a.getClientid(), invType, gstrcdn.getCtin(),gstrcdn.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							if(isNotEmpty(gstrcdn.getCtin()) && gstnMap.containsKey(gstrcdn.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrcdn.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr4a.isAmendment());
							gstr4DwnldList.add(individualInvoice);
						}
					}
				}
			}else if (isNotEmpty(((GSTR4A)gstr4a).getCdna())) {
				List<GSTR4A> gstr4List = gstr4aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr4Ids = Lists.newArrayList();
				List<String> gstr4AList = Lists.newArrayList();
				for(GSTR4A g4 : gstr4List) {
					if(isNotEmpty(g4.getInvoiceno())) {
						gstr4Ids.add(g4.getInvoiceno());
						if(g4.isAmendment()) {
							String invNoCtin = g4.getInvoiceno();
						if(isNotEmpty(((GSTR4A)g4).getCdna())) {
							if(isNotEmpty(((GSTR4A)g4).getCdn().get(0).getCtin())) {
								String ctin = ((GSTR4A)g4).getCdn().get(0).getCtin();
								invNoCtin = invNoCtin+ctin;
							}
							if(isNotEmpty(((GSTR4A)g4).getCdn().get(0).getNt().get(0).getNtDt())) {
								String idt = simpleDateFormat.format(((GSTR4A)g4).getCdn().get(0).getNt().get(0).getNtDt());
								invNoCtin = invNoCtin+idt;
							}
						}
							gstr4AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRCreditDebitNotes gstrcdna : ((GSTR4A)gstr4a).getCdna()) {
					if(isNotEmpty(gstrcdna.getCtin()) && !gstnMap.containsKey(gstrcdna.getCtin())) {
						Page<GSTINPublicData> page = gstinPublicDataRepository.findByGstin(gstrcdna.getCtin(), pageable);
						if(isNotEmpty(page) && isNotEmpty(page.getContent())) {
							GSTINPublicData publicData = page.getContent().get(0);
							ResponseData data = new ResponseData();
							if(isNotEmpty(publicData.getTradeNam())) {
								data.setTradeNam(publicData.getTradeNam());
							}else {
								data.setTradeNam(publicData.getLgnm());
							}
							data.setPradr(publicData.getPradr());
							gstnMap.put(gstrcdna.getCtin(), data);
						} else {
							Response response = ihubConsumerService.publicSearch(gstrcdna.getCtin());
							if(isNotEmpty(response) && isNotEmpty(response.getStatuscd()) 
									&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
								ResponseData data = response.getData();
								if(isNotEmpty(data.getTradeNam())) {
									data.setTradeNam(data.getTradeNam());
								}else {
									data.setTradeNam(data.getLgnm());
								}
								gstnMap.put(gstrcdna.getCtin(), data);
							}
						}
					}
					
					for (GSTRInvoiceDetails gstrInvoiceDetails : gstrcdna.getNt()) {
						String dwnldInvNoCtin = gstrInvoiceDetails.getNtNum();
						if(isNotEmpty(gstrcdna.getCtin())) {
							String dwnldctin = gstrcdna.getCtin();
							dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
						}
						
						if(isNotEmpty(gstrInvoiceDetails.getNtDt())) {
							String idt = simpleDateFormat.format(gstrInvoiceDetails.getNtDt());
							dwnldInvNoCtin = dwnldInvNoCtin+idt;
						}
						
						if(isEmpty(gstr4List) || !gstr4AList.contains(dwnldInvNoCtin)) {
							if(isNotEmpty(gstrcdna.getCtin()) && gstnMap.containsKey(gstrcdna.getCtin())) {
								String addr;
								GSTINPublicAddress gstinAddress = gstnMap.get(gstrcdna.getCtin()).getPradr();
								if(isNotEmpty(gstinAddress)){
									GSTINPublicAddressData gstinAddressData = gstinAddress.getAddr();
									if(isNotEmpty(gstinAddressData)){
										addr = gstinAddressData.getBnm() + "," + gstinAddressData.getSt() + ","
												+ gstinAddressData.getLoc() + "," + gstinAddressData.getDst() + ","
												+ gstinAddressData.getStcd() + "," + gstinAddressData.getCity() + ","
												+ gstinAddressData.getFlno() + "," + gstinAddressData.getPncd();
										gstrInvoiceDetails.setAddress(addr);
									}
								}
							}
							GSTR4A individualInvoice = populateGSTR4(gstrInvoiceDetails, gstnnumber, fp, gstr4a.getUserid(),
									gstr4a.getClientid(), invType, gstrcdna.getCtin(),gstrcdna.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							if(isNotEmpty(gstrcdna.getCtin()) && gstnMap.containsKey(gstrcdna.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrcdna.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr4a.isAmendment());
							gstr4DwnldList.add(individualInvoice);
						}
					}
				}
			}
		}
		gstr4aRepository.save(gstr4DwnldList);
	}


	private GSTR4A populateGSTR4(GSTRInvoiceDetails gstrInvoiceDetails, String gstnnumber, String fp, String userid,String clientid, String invType, String ctin, String cfs) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		GSTR4A individualInvoice = new GSTR4A();
		individualInvoice.setGstin(gstnnumber);
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
			//logger.error(CLASSNAME + "populateGSTR2 :: ERROR in invoice date", e);
		}
		GSTRB2B indGstrb2b = new GSTRB2B();
		if (isNotEmpty(ctin)) {
			indGstrb2b.setCtin(ctin);
		}
		
		if (invType.equals(B2B) || invType.equals(MasterGSTConstants.B2BA)) {
			if(isNotEmpty(cfs)) {
				indGstrb2b.setCfs(cfs);
			}

			indGstrb2b.getInv().add(gstrInvoiceDetails);
			if (isNotEmpty(gstrInvoiceDetails) && isNotEmpty(gstrInvoiceDetails.getRchrg())) {
				if ("Y".equalsIgnoreCase(gstrInvoiceDetails.getRchrg())) {
					individualInvoice.setRevchargetype("Reverse");
				} else {
					individualInvoice.setRevchargetype("Regular");
				}
			}
		} else if (invType.equals(CREDIT_DEBIT_NOTES) || invType.equals(MasterGSTConstants.CDNA)) {
			GSTRCreditDebitNotes note = new GSTRCreditDebitNotes();
			if (isNotEmpty(ctin)) {
				note.setCtin(ctin);
			}
			if(isNotEmpty(cfs)) {
				note.setCfs(cfs);
			}
			if(isNotEmpty(gstrInvoiceDetails.getAddress())){
				indGstrb2b.getInv().get(0).setAddress(gstrInvoiceDetails.getAddress());
			}
			note.getNt().add(gstrInvoiceDetails);
			if(invType.equals(MasterGSTConstants.CDNA)) {
				individualInvoice.getCdna().add(note);
			}else {
				individualInvoice.getCdn().add(note);				
			}
		}
		if(invType.equals(MasterGSTConstants.B2BA)) {
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
		individualInvoice = (GSTR4A) clientService.extrafields(individualInvoice,"GSTR4A");
		return individualInvoice;
	}
}
