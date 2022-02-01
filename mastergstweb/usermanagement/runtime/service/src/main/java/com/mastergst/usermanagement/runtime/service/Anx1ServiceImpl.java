package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR5;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.UQCConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.domain.Base;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.NewAnx1Dao;
import com.mastergst.usermanagement.runtime.domain.Anx1InvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.Anx2Details;
import com.mastergst.usermanagement.runtime.domain.Anx2Docs;
import com.mastergst.usermanagement.runtime.domain.AnxBoe;
import com.mastergst.usermanagement.runtime.domain.AnxDetails;
import com.mastergst.usermanagement.runtime.domain.AnxDoc;
import com.mastergst.usermanagement.runtime.domain.AnxDocs;
import com.mastergst.usermanagement.runtime.domain.AnxEcom;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.AnxItems;
import com.mastergst.usermanagement.runtime.domain.AnxSb;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CL;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CS;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRExportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceParentSupport;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.NewAnx1;
import com.mastergst.usermanagement.runtime.domain.NewAnx2;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.NewAnx1Repository;
import com.mastergst.usermanagement.runtime.repository.NewAnx2Repository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
@Transactional(readOnly = true)
public class Anx1ServiceImpl implements Anx1Service{
	
	@Autowired
	private NewAnx1Repository anx1Repository;
	@Autowired
	private NewAnx2Repository anx2Repository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private ConfigService configService;
	@Autowired 
	private UserService userService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired
	private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired
	NewAnx1Dao newAnx1Dao;
	
	private static final Logger logger = LogManager.getLogger(ClientServiceImpl.class.getName());
	private static final String CLASSNAME = "Anx1ServiceImpl::";
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	private static String DOUBLE_FORMAT  = "%.2f";
	private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Override
	public void saveAnx1Invoice(InvoiceParent invoiceForAnx1, String clientid, String returntype,boolean isIntraState) {
		final String method = "saveAnx1Invoice ::";
		logger.debug(CLASSNAME + method + BEGIN);
		NewAnx1 anx1 = anx1Repository.findByClientidAndFpAndInvoiceno(clientid, invoiceForAnx1.getFp(), invoiceForAnx1.getInvoiceno());
		if(NullUtil.isEmpty(anx1)) {
			anx1 = new NewAnx1();
			anx1.setId(new ObjectId());
		}
		String id = anx1.getId().toString();
		try {
			BeanUtils.copyProperties(anx1, invoiceForAnx1);
			anx1.setId(new ObjectId(id));
			if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
				if(NullUtil.isNotEmpty(invoiceForAnx1.getExp()) && NullUtil.isNotEmpty(invoiceForAnx1.getExp().get(0)) && NullUtil.isNotEmpty(invoiceForAnx1.getExp().get(0).getInv())) {
					AnxDetails anxdetails = new AnxDetails();
					GSTRExportDetails gstrinvoiceDetails = invoiceForAnx1.getExp().get(0).getInv().get(0);
					anxdetails.setDoctyp("I");
					AnxDoc anxdoc = new AnxDoc();
					anxdoc.setNum(gstrinvoiceDetails.getInum());
					anxdoc.setDt(simpleDateFormat.format(gstrinvoiceDetails.getIdt()));
					anxdoc.setVal(gstrinvoiceDetails.getVal());
					anxdetails.setDoc(anxdoc);
					
					AnxSb anxsb = new AnxSb();
					if(isNotEmpty(gstrinvoiceDetails.getSbnum())) {
						anxsb.setNum(gstrinvoiceDetails.getSbnum());
					}
					if(isNotEmpty(gstrinvoiceDetails.getSbpcode())) {
						anxsb.setPcode(gstrinvoiceDetails.getSbpcode());
					}
					if(isNotEmpty(gstrinvoiceDetails.getSbdt())) {
						anxsb.setDt(simpleDateFormat.format(gstrinvoiceDetails.getSbdt()));
					}
					anxdetails.setSb(anxsb);
					anxdetails.setItems(Lists.newArrayList());
					for (Item item : invoiceForAnx1.getItems()) {
						AnxItems anxItem = new AnxItems();
						String code = "";
						if (item.getHsn().contains(" : ")) {
							String hsncode[] = item.getHsn().split(" : ");
							code = hsncode[0];
						} else {
							code = item.getHsn();
						}
						anxItem.setHsn(code);
						anxItem.setRate(item.getRate());
						anxItem.setTxval(item.getTaxablevalue());
						anxdetails.getItems().add(anxItem);
					}
					List<AnxDetails> expAnxDetails = Lists.newArrayList();
					expAnxDetails.add(anxdetails);
					if("WPAY".equalsIgnoreCase(invoiceForAnx1.getExp().get(0).getExpTyp())) {
						anx1.setExpwp(expAnxDetails);
						anx1.setInvtype("EXPWP");
					}else {
						anx1.setInvtype("EXPWOP");
						anx1.setExpwop(expAnxDetails);
					}
					
				}
			}else if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
				List<AnxDocs> listanxdocs = anxDetails(invoiceForAnx1,isIntraState);
				anx1.setAnxb2b(listanxdocs);
			}else if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2C) || invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2CL)){
				List<AnxDetails> b2cAnxDetails = b2cAnxdetails(invoiceForAnx1,isIntraState);
				anx1.setB2c(b2cAnxDetails);
			}else if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
				List<AnxDetails> b2cAnxDetails = b2cAnxdetails(invoiceForAnx1,isIntraState);
				anx1.setB2c(b2cAnxDetails);
			}else if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)){
				List<AnxDocs> listanxdocs = anxImpgDetails(invoiceForAnx1,isIntraState);
				anx1.setImpg(listanxdocs);
			}else if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)){
				List<AnxDetails> listanxdetails = anxImpsDetails(invoiceForAnx1,isIntraState);
				anx1.setImps(listanxdetails);
			}else {
				if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b()) && NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0))) {
					if(invoiceForAnx1.getRevchargetype().equalsIgnoreCase("Regular") || invoiceForAnx1.getRevchargetype().equalsIgnoreCase("No")) {
						if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0)) && "R".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getInvTyp()) && "N".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getRchrg())) {
							List<AnxDocs> listanxdocs = anxDetails(invoiceForAnx1,isIntraState);
							anx1.setAnxb2b(listanxdocs);
							anx1.setDe(Lists.newArrayList());
							anx1.setSezwop(Lists.newArrayList());
							anx1.setSezwp(Lists.newArrayList());
						}else if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0)) && "DE".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getInvTyp()) && "N".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getRchrg())) {
							List<AnxDocs> listanxdocs = anxDetails(invoiceForAnx1,isIntraState);
							anx1.setInvtype("DE");
							anx1.setDe(listanxdocs);
							anx1.setAnxb2b(Lists.newArrayList());
							anx1.setSezwop(Lists.newArrayList());
							anx1.setSezwp(Lists.newArrayList());
						}else if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0)) && "SEWP".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getInvTyp()) && "N".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getRchrg())) {
							List<AnxDocs> listanxdocs = anxDetails(invoiceForAnx1,isIntraState);
							anx1.setInvtype("SEWP");
							anx1.setSezwp(listanxdocs);
							anx1.setDe(Lists.newArrayList());
							anx1.setSezwop(Lists.newArrayList());
							anx1.setAnxb2b(Lists.newArrayList());
						}else if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0)) && "SEWOP".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getInvTyp()) && "N".equalsIgnoreCase(invoiceForAnx1.getB2b().get(0).getInv().get(0).getRchrg())) {
							AnxDocs anxdocs = new AnxDocs();
							anxdocs.setDocs(Lists.newArrayList());
							if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getCtin())) {
								anxdocs.setCtin(invoiceForAnx1.getB2b().get(0).getCtin());
							}
							AnxDetails anxdetails = new AnxDetails();
							AnxDoc anxdoc = new AnxDoc();
							anxdetails.setDoctyp("I");
						
							anxdetails.setItems(Lists.newArrayList());
							if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0))) {
								GSTRInvoiceDetails gstrinvoiceDetails = invoiceForAnx1.getB2b().get(0).getInv().get(0);
								anxdetails.setPos(gstrinvoiceDetails.getPos());
								
								anxdoc.setNum(gstrinvoiceDetails.getInum());
								anxdoc.setDt(gstrinvoiceDetails.getIdt());
								anxdoc.setVal(gstrinvoiceDetails.getVal());
								anxdetails.setDoc(anxdoc);
								for (Item item : invoiceForAnx1.getItems()) {
									AnxItems anxItem = new AnxItems();
									String code = "";
									if (item.getHsn().contains(" : ")) {
										String hsncode[] = item.getHsn().split(" : ");
										code = hsncode[0];
									} else {
										code = item.getHsn();
									}
									anxItem.setHsn(code);
									anxItem.setRate(item.getRate());
									anxItem.setTxval(item.getTaxablevalue());
									anxdetails.getItems().add(anxItem);
								}
								
							}
							anxdocs.getDocs().add(anxdetails);
							List<AnxDocs> listanxdocs = Lists.newArrayList();
							listanxdocs.add(anxdocs);
							anx1.setInvtype("SEWOP");
							anx1.setSezwop(listanxdocs);
							anx1.setDe(Lists.newArrayList());
							anx1.setSezwp(Lists.newArrayList());
							anx1.setAnxb2b(Lists.newArrayList());
						}
					}
				}else {
					List<AnxDetails> revAnxDetails = revAnxdetails(invoiceForAnx1,isIntraState);
					anx1.setRev(revAnxDetails);
				}
			}
			anx1Repository.save(anx1);
		}catch (IllegalAccessException | InvocationTargetException e) {
			logger.error(CLASSNAME + method + "ERROR in BeanUtils copy", e);
		}
		
	}
	
	public List<AnxDetails> b2cAnxdetails(InvoiceParent invoiceForAnx1,boolean isIntraState){
		AnxDetails anxdetails = new AnxDetails();
		String pos="";
		Double diffpercent = 0d;
		String docType="I";
		if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype()) && invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2C)) {
			docType = "I";
			GSTRB2CS gstrinvDetails = invoiceForAnx1.getB2cs().get(0);
			if(NullUtil.isNotEmpty(gstrinvDetails.getPos())) {
				pos = gstrinvDetails.getPos();
			}
			
			if(NullUtil.isNotEmpty(gstrinvDetails.getDiffPercent())) {
				diffpercent = gstrinvDetails.getDiffPercent();
			}
		}else if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype()) && invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2CL)){
			docType = "I";
			GSTRB2CL gstrinvDetails = invoiceForAnx1.getB2cl().get(0);
			if(NullUtil.isNotEmpty(gstrinvDetails.getPos())) {
				pos = gstrinvDetails.getPos();
			}
			
			if(NullUtil.isNotEmpty(gstrinvDetails.getDiffPercent())) {
				diffpercent = gstrinvDetails.getDiffPercent();
			}
		}else if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype()) && invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
			GSTRInvoiceDetails gstrinvDetails = invoiceForAnx1.getCdnur().get(0);
			if(NullUtil.isNotEmpty(gstrinvDetails)) {
				if("C".equalsIgnoreCase(gstrinvDetails.getNtty())) {
					docType = "C";
				}else if("D".equalsIgnoreCase(gstrinvDetails.getNtty())) {
					docType = "D";
				}
				if(NullUtil.isNotEmpty(gstrinvDetails.getDiffPercent())) {
					diffpercent = gstrinvDetails.getDiffPercent();
				}
			}
			if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0))) {
				GSTRInvoiceDetails gstrinvoiceDetails = invoiceForAnx1.getB2b().get(0).getInv().get(0);
				pos = gstrinvoiceDetails.getPos();
			}
			
		}
		
		anxdetails.setDoctyp(docType);
		if(NullUtil.isNotEmpty(pos)) {
			anxdetails.setPos(pos);
		}
		
		if(NullUtil.isNotEmpty(diffpercent) && diffpercent > 0) {
			anxdetails.setDiffprcnt(diffpercent);
		}
		
		if(isIntraState) {
			anxdetails.setSec7act("N");
		}else {
			anxdetails.setSec7act("Y");
		}
		anxdetails.setItems(Lists.newArrayList());
		for (Item item : invoiceForAnx1.getItems()) {
			AnxItems anxItem = new AnxItems();
			if(isIntraState) {
				anxItem.setCgst(item.getCgstamount());
				anxItem.setSgst(item.getSgstamount());
			}else {
				anxItem.setIgst(item.getIgstamount());
			}
			anxItem.setCess(item.getCessamount());
			anxItem.setRate(item.getRate());
			anxItem.setTxval(item.getTaxablevalue());
			anxdetails.getItems().add(anxItem);
		}
		List<AnxDetails> b2cAnxDetails = Lists.newArrayList();
		b2cAnxDetails.add(anxdetails);
		return b2cAnxDetails;
	}
	
	public List<AnxDocs> anxDetails(InvoiceParent invoiceForAnx1,boolean isIntraState){
		AnxDocs anxdocs = new AnxDocs();
		anxdocs.setDocs(Lists.newArrayList());
		if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getCtin())) {
			anxdocs.setCtin(invoiceForAnx1.getB2b().get(0).getCtin());
		}
		AnxDetails anxdetails = new AnxDetails();
		AnxDoc anxdoc = new AnxDoc();
		if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
			List<GSTRCreditDebitNotes> notes = ((GSTR1) invoiceForAnx1).getCdnr();
			if(NullUtil.isNotEmpty(notes) && NullUtil.isNotEmpty(notes.get(0)) && NullUtil.isNotEmpty(notes.get(0).getNt()) && NullUtil.isNotEmpty(notes.get(0).getNt().get(0)) && NullUtil.isNotEmpty(notes.get(0).getNt()) && NullUtil.isNotEmpty(notes.get(0).getNt().get(0).getNtty())) {
				String doctype = notes.get(0).getNt().get(0).getNtty();
				if("C".equalsIgnoreCase(doctype)) {
					anxdetails.setDoctyp("C");
				}else if("D".equalsIgnoreCase(doctype)) {
					anxdetails.setDoctyp("D");
				}
			}
		}else {
			anxdetails.setDoctyp("I");
		}
		if(isIntraState) {
			anxdetails.setSec7act("N");
		}else {
			anxdetails.setSec7act("Y");
		}
		anxdetails.setItems(Lists.newArrayList());
		if(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
			if(NullUtil.isNotEmpty(((GSTR1) invoiceForAnx1).getCdnr().get(0))) {
				GSTRCreditDebitNotes gstrinvoiceDetails = ((GSTR1) invoiceForAnx1).getCdnr().get(0);
				GSTRInvoiceDetails gstrinvdetails = gstrinvoiceDetails.getNt().get(0);
				String pos = "";
				if(NullUtil.isNotEmpty(gstrinvoiceDetails.getCtin())) {
					pos = gstrinvoiceDetails.getCtin().substring(0, 2);
				}
				anxdetails.setPos(pos);
				if(NullUtil.isNotEmpty(gstrinvdetails.getDiffPercent())) {
					anxdetails.setDiffprcnt(gstrinvdetails.getDiffPercent());
				}
				anxdoc.setNum(gstrinvdetails.getInum());
				anxdoc.setDt(gstrinvdetails.getIdt());
				anxdoc.setVal(gstrinvdetails.getVal());
				anxdetails.setDoc(anxdoc);
				for (Item item : invoiceForAnx1.getItems()) {
					AnxItems anxItem = new AnxItems();
					if(isIntraState) {
						anxItem.setCgst(item.getCgstamount());
						anxItem.setSgst(item.getSgstamount());
					}else {
						anxItem.setIgst(item.getIgstamount());
					}
					anxItem.setCess(item.getCessamount());
					String code = "";
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
					} else {
						code = item.getHsn();
					}
					anxItem.setHsn(code);
					anxItem.setRate(item.getRate());
					anxItem.setTxval(item.getTaxablevalue());
					anxdetails.getItems().add(anxItem);
				}
				
			}
		}else {
			if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0))) {
				GSTRInvoiceDetails gstrinvoiceDetails = invoiceForAnx1.getB2b().get(0).getInv().get(0);
				anxdetails.setPos(gstrinvoiceDetails.getPos());
				if(NullUtil.isNotEmpty(gstrinvoiceDetails.getDiffPercent())) {
					anxdetails.setDiffprcnt(gstrinvoiceDetails.getDiffPercent());
				}
				anxdoc.setNum(gstrinvoiceDetails.getInum());
				anxdoc.setDt(gstrinvoiceDetails.getIdt());
				anxdoc.setVal(gstrinvoiceDetails.getVal());
				anxdetails.setDoc(anxdoc);
				for (Item item : invoiceForAnx1.getItems()) {
					AnxItems anxItem = new AnxItems();
					if(isIntraState) {
						anxItem.setCgst(item.getCgstamount());
						anxItem.setSgst(item.getSgstamount());
					}else {
						anxItem.setIgst(item.getIgstamount());
					}
					anxItem.setCess(item.getCessamount());
					String code = "";
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
					} else {
						code = item.getHsn();
					}
					anxItem.setHsn(code);
					anxItem.setRate(item.getRate());
					anxItem.setTxval(item.getTaxablevalue());
					anxdetails.getItems().add(anxItem);
				}
				
			}
		}
		anxdocs.getDocs().add(anxdetails);
		List<AnxDocs> listanxdocs = Lists.newArrayList();
		listanxdocs.add(anxdocs);
		return listanxdocs;
		
	}
	
	public List<AnxDetails> revAnxdetails(InvoiceParent invoiceForAnx1,boolean isIntraState){
		AnxDetails anxdetails = new AnxDetails();
		String pos="";
		Double diffpercent = 0d;
		if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2C))) {
			GSTRB2CS gstrinvDetails = invoiceForAnx1.getB2cs().get(0);
			if(NullUtil.isNotEmpty(gstrinvDetails.getPos())) {
				pos = gstrinvDetails.getPos();
			}
			
			if(NullUtil.isNotEmpty(gstrinvDetails.getDiffPercent())) {
				diffpercent = gstrinvDetails.getDiffPercent();
			}
		}else if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2CL))){
			GSTRB2CL gstrinvDetails = invoiceForAnx1.getB2cl().get(0);
			if(NullUtil.isNotEmpty(gstrinvDetails.getPos())) {
				pos = gstrinvDetails.getPos();
			}
			
			if(NullUtil.isNotEmpty(gstrinvDetails.getDiffPercent())) {
				diffpercent = gstrinvDetails.getDiffPercent();
			}
		}else if(NullUtil.isNotEmpty(invoiceForAnx1.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B))) {
			
			if(NullUtil.isNotEmpty(invoiceForAnx1.getB2b().get(0).getInv().get(0))) {
				GSTRInvoiceDetails gstrinvoiceDetails = invoiceForAnx1.getB2b().get(0).getInv().get(0);
				pos = gstrinvoiceDetails.getPos();
				if(NullUtil.isNotEmpty(gstrinvoiceDetails.getDiffPercent())) {
					diffpercent = gstrinvoiceDetails.getDiffPercent();
				}
			}
			
		}
		
		if(NullUtil.isNotEmpty(pos)) {
			anxdetails.setPos(pos);
		}
		
		if(NullUtil.isNotEmpty(diffpercent) && diffpercent > 0) {
			anxdetails.setDiffprcnt(diffpercent);
		}
		
		if(isIntraState) {
			anxdetails.setSec7act("N");
		}else {
			anxdetails.setSec7act("Y");
		}
		anxdetails.setItems(Lists.newArrayList());
		for (Item item : invoiceForAnx1.getItems()) {
			AnxItems anxItem = new AnxItems();
			if(isIntraState) {
				anxItem.setCgst(item.getCgstamount());
				anxItem.setSgst(item.getSgstamount());
			}else {
				anxItem.setIgst(item.getIgstamount());
			}
			anxItem.setCess(item.getCessamount());
			anxItem.setRate(item.getRate());
			anxItem.setTxval(item.getTaxablevalue());
			anxdetails.getItems().add(anxItem);
		}
		List<AnxDetails> revAnxDetails = Lists.newArrayList();
		revAnxDetails.add(anxdetails);
		return revAnxDetails;
	}
	
	private Date[] caliculateStEndDates(final Client client, final String returnType, final String reports,int month, int year){
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		if(returnType.equals("Unclaimed")){
			if(year <= 2018) {
				cal.set(2017, 6, 0, 0, 0, 0);
			} else {
				if(month > 3) {
					cal.set(year, 3, 0, 0, 0, 0);
				} else {
					cal.set(year-1, 3, 0, 0, 0, 0);
				}	
			}
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(year, month, 0, 0, 0, 0);
			endDate = new java.util.Date(cal.getTimeInMillis());
		}else{
			if(isNotEmpty(client.getFilingOption()) && client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_YEARLY)) {
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year-1, 3, 0, 0, 0, 0);
				} else {
					cal.set(year, 3, 0, 0, 0, 0);
				}
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year, 3, 0, 0, 0, 0);
				} else {
					cal.set(year+1, 3, 0, 0, 0, 0);
				}
				endDate = new java.util.Date(cal.getTimeInMillis());
			} else if((!returnType.equals(MasterGSTConstants.GSTR2) && !returnType.equals(MasterGSTConstants.PURCHASE_REGISTER)) && isNotEmpty(client.getFilingOption()) && client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_QUARTERLY)) {
				if("reports".equals(reports)) {
					cal.set(year, month - 1, 0, 0, 0, 0);
					stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					cal.set(year, month, 0, 0, 0, 0);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					if(month == 1 || month == 2 || month == 3) {
						cal.set(year, 0, 0, 0, 0, 0);
					} else if(month == 4 || month == 5 || month == 6) {
						cal.set(year, 3, 0, 0, 0, 0);
					} else if(month == 7 || month == 8 || month == 9) {
						cal.set(year, 6, 0, 0, 0, 0);
					} else if(month == 10 || month == 11 || month == 12) {
						cal.set(year, 9, 0, 0, 0, 0);
					}
					stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					if(month == 1 || month == 2 || month == 3) {
						cal.set(year, 3, 0, 0, 0, 0);
					} else if(month == 4 || month == 5 || month == 6) {
						cal.set(year, 6, 0, 0, 0, 0);
					} else if(month == 7 || month == 8 || month == 9) {
						cal.set(year, 9, 0, 0, 0, 0);
					} else if(month == 10 || month == 11 || month == 12) {
						cal.set(year+1, 0, 0, 0, 0, 0);
					}
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			} else {
				cal.set(year, month - 1, 0, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, month, 0, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
		}
		return new Date[]{stDate, endDate};
	}
	
	public Map getInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired, String booksOrReturns){
		logger.debug(CLASSNAME + "getInvoices : Begin");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends AnxInvoiceParent> invoices = null;
		TotalInvoiceAmount totalInvoiceAmount = null;
		if (isNotEmpty(returnType)) {
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				//String yr=year+"-"+(year+1);
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						client.setFilingOption(options.getOption());			
					}
				});
			}
			Date[] dates = caliculateStEndDates(client, returnType, reports, month, year);
			Date stDate = dates[0];
			Date endDate = dates[1];
			String yearCode = Utility.getYearCode(month, year);
			
				String strMonth =  month<10 ? "0"+month : month+"";
				String retPeriod = strMonth+year;
				ClientStatus clientStatus = clientService.getClientStatus(client.getId().toString(), returnType, retPeriod);
				String filingstatus = "";
				if (isNotEmpty(clientStatus)) {
					filingstatus = clientStatus.getStatus();
				}
				Page<NewAnx1> gstr1 = null;
				Boolean prevPendinginv = false;
				if(isEmpty(client.getPrevPendingInv())) {
					prevPendinginv = false;
				}else if("previousMonthsPendingInv".equalsIgnoreCase(client.getPrevPendingInv())){
					prevPendinginv = true;
				}else {
					prevPendinginv = false;
				}
				if(filingstatus.equalsIgnoreCase(MasterGSTConstants.STATUS_FILED) || filingstatus.equalsIgnoreCase(MasterGSTConstants.STATUS_SUBMITTED)) {
					prevPendinginv = false;
				}
				OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(client.getId().toString());
				Boolean fpidate = false;
				if(isNotEmpty(otherconfig)){
					fpidate = otherconfig.isEnableinvoiceview();
				}
				if("reports".equals(reports)) {
					gstr1 = newAnx1Dao.findByClientidAndMonthAndYear(client.getId().toString(), month, yearCode, start, length, searchVal, filter);
					totalInvoiceAmount = newAnx1Dao.getTotalInvoicesAmountsForMonth(client.getId().toString(), month, yearCode, searchVal, filter);
				}else {
					if(isEmpty(otherconfig) || !fpidate) {
						if(prevPendinginv && isEmpty(booksOrReturns)) {
							
							boolean contains = false;
							if(isNotEmpty(filter) && isNotEmpty(filter.getPaymentStatus()) && filter.getPaymentStatus().length>0) {
								contains = Arrays.asList(filter.getPaymentStatus()).contains("Pending");
								if(!contains) {
									filter.getPaymentStatus()[filter.getPaymentStatus().length-1] = "Pending";
								}
							}else {
								 String[] tempArray = new String[ 1 ];
								 tempArray[0] ="Pending";
								filter.setPaymentStatus(tempArray);
							}
							
							gstr1 = newAnx1Dao.findByClientidAndMonthAndYear(client.getId().toString(), 0, yearCode, start, length, searchVal, filter);
							totalInvoiceAmount = newAnx1Dao.getTotalInvoicesAmountsForMonth(client.getId().toString(), 0, yearCode, searchVal, filter);
						}else {
							gstr1 = newAnx1Dao.findByClientidAndMonthAndYear(client.getId().toString(), month, yearCode, start, length, searchVal, filter);
							totalInvoiceAmount = newAnx1Dao.getTotalInvoicesAmountsForMonth(client.getId().toString(), month, yearCode, searchVal, filter);
						}
						
					} else {
						/*gstr1 = gstr1Dao.findByClientidAndInvtypeInAndFp(client.getId().toString(),invTypes, retPeriod, start, length, searchVal);
						totalInvoiceAmount = gstr1Dao.getTotalInvoicesAmounts(client.getId().toString(), invTypes, retPeriod, searchVal);*/
						if(prevPendinginv && isEmpty(booksOrReturns)) {
							
							boolean contains = false;
							if(isNotEmpty(filter) && isNotEmpty(filter.getPaymentStatus()) && filter.getPaymentStatus().length>0) {
								contains = Arrays.asList(filter.getPaymentStatus()).contains("Pending");
								if(!contains) {
									filter.getPaymentStatus()[filter.getPaymentStatus().length-1] = "Pending";
								}
							}else {
								 String[] tempArray = new String[ 1 ];
								 tempArray[0] ="Pending";
								filter.setPaymentStatus(tempArray);
							}
							
							gstr1 = newAnx1Dao.findByClientidAndMonthAndYear(client.getId().toString(), 0, yearCode, start, length, searchVal, filter);
							totalInvoiceAmount = newAnx1Dao.getTotalInvoicesAmountsForMonth(client.getId().toString(), 0, yearCode, searchVal, filter);
						}else {
							gstr1 = newAnx1Dao.findByClientidAndFp(client.getId().toString(), retPeriod, start, length, searchVal, filter);
							totalInvoiceAmount = newAnx1Dao.getTotalInvoicesAmountsForFp(client.getId().toString(), retPeriod, searchVal, filter);
						}
					}
				}
				
				
				invoices = gstr1;
		
			invoicesMap.put("invoices",invoices);
			invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		}
		return invoicesMap;
	}
	
	@Override
	@Transactional
	public AnxInvoiceParent getGSTRReturnInvoice(List<? extends AnxInvoiceParent> invoices, final Client client,
			final String returntype, final int iMonth, final int iYear) {
		final String method = "getGSTRReturnInvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		int tYear = iYear-1;
		
		String strYear="";
		if(iMonth < 4) {
			if(tYear == 2016 || tYear == 2017) {
				if(iMonth < 4) {
					strYear="APR2017-JUN2017";
				}else {
					strYear="2017-2018";
				}
			}else {
				tYear = tYear-1;
				strYear=tYear+"-"+(tYear+1);
			}
		}else {
			strYear=tYear+"-"+(tYear+1);
		}
		
		String month = iMonth < 10 ? "0" + iMonth : iMonth + "";
		Map<String,List<AnxDetails>> b2csmap = Maps.newHashMap();
		AnxInvoiceParent invoice = new AnxInvoiceParent();
		if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX1)) {	
			invoice = new NewAnx1();
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			invoice = new NewAnx2();
		}	
		invoice.setGstin(client.getGstnnumber());
		for (AnxInvoiceParent invoiceParent : invoices) {
		if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX1)) {	
			if (isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(B2B)) {
				//((NewAnx1) invoice).setAnxb2b(((NewAnx1) invoiceParent).getAnxb2b());
				((NewAnx1) invoice).setAnxb2b(Lists.newArrayList());
				for(AnxDocs anxdoc : ((NewAnx1) invoiceParent).getAnxb2b()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (AnxDocs eInvs : ((NewAnx1)invoice).getAnxb2b()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx1)invoice).getAnxb2b().add(anxdoc);
					}
				}
				
				
				
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals("DE")) {
				//((NewAnx1) invoice).setDe(((NewAnx1) invoiceParent).getDe());
				((NewAnx1) invoice).setDe(Lists.newArrayList());
				for(AnxDocs anxdoc : ((NewAnx1) invoiceParent).getDe()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (AnxDocs eInvs : ((NewAnx1)invoice).getDe()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx1)invoice).getDe().add(anxdoc);
					}
				}
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals("SEWP")) {
				//((NewAnx1) invoice).setSezwp(((NewAnx1) invoiceParent).getSezwp());
				((NewAnx1) invoice).setSezwp(Lists.newArrayList());
				for(AnxDocs anxdoc : ((NewAnx1) invoiceParent).getSezwp()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (AnxDocs eInvs : ((NewAnx1)invoice).getSezwp()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx1)invoice).getSezwp().add(anxdoc);
					}
				}
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals("SEWOP")) {
				//((NewAnx1) invoice).setSezwop(((NewAnx1) invoiceParent).getSezwop());
				((NewAnx1) invoice).setSezwop(Lists.newArrayList());
				for(AnxDocs anxdoc : ((NewAnx1) invoiceParent).getSezwop()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (AnxDocs eInvs : ((NewAnx1)invoice).getSezwop()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx1)invoice).getSezwop().add(anxdoc);
					}
				}
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals("EXPWP")) {
				((NewAnx1) invoice).setExpwp(((NewAnx1) invoiceParent).getExpwp());
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals("EXPWOP")) {
				((NewAnx1) invoice).setExpwop(((NewAnx1) invoiceParent).getExpwop());
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(MasterGSTConstants.B2C)) {
				((NewAnx1) invoice).setB2c(((NewAnx1) invoiceParent).getB2c());
			}else if (isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(B2C)) {
				if (isNotEmpty(((NewAnx1)invoiceParent).getB2c())) {
					
					if(isNotEmpty(((NewAnx1)invoice).getB2c())) {
						List<AnxDetails> newInvs = Lists.newArrayList();
						String b2cskey="";
						for(AnxDetails eb2cs : ((NewAnx1)invoice).getB2c()) {
							for(AnxDetails b2cs : ((NewAnx1)invoiceParent).getB2c()) {
								if(isNotEmpty(b2cs.getPos())) {
									b2cskey = b2cs.getPos();
								if (isNotEmpty(eb2cs.getPos()) && isNotEmpty(b2cs.getPos()) && eb2cs.getPos().equals(b2cs.getPos())) {
									
									eb2cs.getItems().addAll(b2cs.getItems());
									
								} else {
									if(!b2csmap.containsKey(b2cskey)) {
									newInvs.add(b2cs);
										b2csmap.put(b2cskey, newInvs);
									}
								}
							}
							}
						}
						((NewAnx1)invoice).getB2c().addAll(newInvs);
					} else {
						String b2cskey="";
						((NewAnx1)invoice).setB2c(Lists.newArrayList());
						List<AnxDetails> newInvs = Lists.newArrayList();
						for (AnxDetails b2cs : ((NewAnx1)invoiceParent).getB2c()) {
							boolean present = false;
							b2cskey = b2cs.getPos();
							for (AnxDetails eb2cs : newInvs) {
								if (isNotEmpty(eb2cs.getPos()) && isNotEmpty(b2cs.getPos()) && eb2cs.getPos().equals(b2cs.getPos())) {
									present = true;
									eb2cs.getItems().addAll(b2cs.getItems());
								} else {
									newInvs.add(b2cs);
									present = true;
								}
							}
							if(!present) {
								newInvs.add(b2cs);
							}
						}
						b2csmap.put(b2cskey, newInvs);
						((NewAnx1)invoice).setB2c(newInvs);
					}
				}
			}
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			invoice.setGstin(client.getGstnnumber());
			if (isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(B2B)) {
				((NewAnx2) invoice).setAnx2b2b(Lists.newArrayList());
				
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((NewAnx2) invoiceParent).getAnx2b2b()) {
					for(Anx2Details anx2dtls : anx2docs.getDocs()) {
						Anx2Details anx2Details=new Anx2Details();
						anx2Details.setDoctyp(anx2dtls.getDoctyp());
						anx2Details.setChksum(anx2dtls.getChksum());
						anx2Details.setAction(anx2dtls.getAction());
						anx2Details.setItcent(anx2dtls.getItcent());
						anxDoc.setNum(anx2dtls.getDoc().getNum());
						anxDoc.setDt(anx2dtls.getDoc().getDt());
						anx2Details.setDoc(anxDoc);
						anx2DetailsList.add(anx2Details);
						
						
						anx2Docs.setCtin(anx2docs.getCtin());
					}
				}
				
				anx2Docs.setDocs(anx2DetailsList);
				anx2DocsList.add(anx2Docs);
				
				((NewAnx2)invoice).setAnx2b2b(anx2DocsList);
				//((NewAnx2)invoice).getAnx2b2b().add((Anx2Docs) anx2doc);
				
				
			/* 	for(Anx2Docs anxdoc : ((NewAnx2) invoiceParent).getAnx2b2b()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (Anx2Docs eInvs : ((NewAnx2)invoice).getAnx2b2b()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx2)invoice).getAnx2b2b().add(anxdoc);
					}
				} */
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(MasterGSTConstants.DE)) {
				//((NewAnx1) invoice).setDe(((NewAnx1) invoiceParent).getDe());
				((NewAnx2) invoice).setDe(Lists.newArrayList());
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((NewAnx2) invoiceParent).getDe()) {
					for(Anx2Details anx2dtls : anx2docs.getDocs()) {
						Anx2Details anx2Details=new Anx2Details();
						
						anx2Details.setDoctyp(anx2dtls.getDoctyp());
						anx2Details.setChksum(anx2dtls.getChksum());
						anx2Details.setAction(anx2dtls.getAction());
						anx2Details.setItcent(anx2dtls.getItcent());
						anxDoc.setNum(anx2dtls.getDoc().getNum());
						anxDoc.setDt(anx2dtls.getDoc().getDt());
						anx2Details.setDoc(anxDoc);
						anx2DetailsList.add(anx2Details);
						anx2Docs.setCtin(anx2docs.getCtin());
					}
				}
				
				anx2Docs.setDocs(anx2DetailsList);
				anx2DocsList.add(anx2Docs);
				
				((NewAnx2)invoice).setDe(anx2DocsList);
				/* for(Anx2Docs anxdoc : ((NewAnx2) invoiceParent).getDe()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (Anx2Docs eInvs : ((NewAnx2)invoice).getDe()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx2)invoice).getDe().add(anxdoc);
					}
				} */
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(MasterGSTConstants.SEZWP)) {
				//((NewAnx1) invoice).setSezwp(((NewAnx1) invoiceParent).getSezwp());
				((NewAnx2) invoice).setSezwp(Lists.newArrayList());
				for(Anx2Docs anxdoc : ((NewAnx2) invoiceParent).getSezwp()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (Anx2Docs eInvs : ((NewAnx2)invoice).getSezwp()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx2)invoice).getSezwp().add(anxdoc);
					}
				}
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(MasterGSTConstants.SEZWOP)) {
				//((NewAnx1) invoice).setSezwop(((NewAnx1) invoiceParent).getSezwop());
				((NewAnx2) invoice).setSezwop(Lists.newArrayList());
				for(Anx2Docs anxdoc : ((NewAnx2) invoiceParent).getSezwop()) {
					String ctin = anxdoc.getCtin();
					boolean present = false;
					
					for (Anx2Docs eInvs : ((NewAnx2)invoice).getSezwop()) {
						if (isNotEmpty(anxdoc.getCtin()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getDocs().addAll(anxdoc.getDocs());
						}
					}
					if (!present) {
						// gstrb2bInvoice.setErrorMsg("");
						((NewAnx2)invoice).getSezwop().add(anxdoc);
					}
				}
		}
		}
		}
		logger.debug(CLASSNAME + method + " invoices {}", invoices);
		
		logger.debug(CLASSNAME + method + END);
		return invoice;
	}
	
	@Override
	@Transactional
	public Response fetchUploadStatus(final String userid, final String usertype, String clientid, String returntype,
			int month, int year, List<String> invoiceList) {
		final String method = "fetchUploadStatus::";
		logger.debug(CLASSNAME + method + BEGIN);

		if (year <= 0) {
			Calendar cal = Calendar.getInstance();
			year = cal.get(Calendar.YEAR);
			month = cal.get(Calendar.MONTH);
		}
		Client client = clientService.findById(clientid);
		String gstn = client.getGstnnumber();
		String clientPos = getStateCode(client.getStatename());
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth + year;
		Base gstInvoice = null;
		List<? extends AnxInvoiceParent> invoices = Lists.newArrayList();
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				//String yr=year+"-"+(year+1);
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						client.setFilingOption(options.getOption());			
					}
				});
			}
			if (isNotEmpty(client.getFilingOption())
					&& client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_QUARTERLY)) {
				if(month == 1 || month == 2 || month == 3) {
					strMonth = "03";
				} else if(month == 4 || month == 5 || month == 6) {
					strMonth = "06";
				} else if(month == 7 || month == 8 || month == 9) {
					strMonth = "09";
				} else if(month == 10 || month == 11 || month == 12) {
					strMonth = "12";
				}
				retPeriod = strMonth + year;
			}
			
				logger.debug(CLASSNAME + method + " Selected Invoices {}", invoiceList);
				if(NullUtil.isEmpty(invoiceList)) {
					invoices = getInvoices(null, client, returntype, month, year, MasterGSTConstants.GST_STATUS_SUCCESS);;
				}else {
				invoices = getSelectedInvoices(invoiceList, returntype);
				}
				logger.debug(CLASSNAME + method + " invoices {}", invoices);
			
			if (isNotEmpty(invoices)) {
				gstInvoice = getGSTRReturnInvoice(invoices, client, returntype, month, year);
				
				logger.debug(CLASSNAME + method + " gstInvoice {}", gstInvoice);
				if (returntype.equals(ANX1)) {
					NewAnx1 anx1 = ((NewAnx1) gstInvoice);
					anx1.setNil(null);
					anx1.setRtnprd(retPeriod);
				/*
				 * if (isNotEmpty(anx1.getAnxb2b())) { retPeriod = B2B; } else if
				 * (isNotEmpty(anx1.getB2c())) { retPeriod = B2C; }
				 */
				}else if (returntype.equals(MasterGSTConstants.ANX2)) {
					NewAnx2 anx2 = ((NewAnx2) gstInvoice);
					anx2.setNil(null);
					anx2.setRtnprd(retPeriod);
				}
			}
		
		if (isNotEmpty(gstInvoice)) {
			try {
				Response saveResponse = iHubConsumerService.saveReturns(gstInvoice, client.getStatename(),client.getGstname(), gstn, retPeriod, returntype, true);
				if (isNotEmpty(saveResponse.getStatuscd())	&& saveResponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					String refId = saveResponse.getData().getRefId();
					Response gstnResponse = iHubConsumerService.newReturnStatus(refId, client.getStatename(),	client.getGstname(), gstn, retPeriod,returntype, true);
					int i = 0;
					while ((i < 5) && (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_IP))) {
						gstnResponse = iHubConsumerService.newReturnStatus(refId, client.getStatename(),
								client.getGstname(), gstn, retPeriod,returntype, false);
						i++;
					}
					List<String> invList = Lists.newArrayList();
					for (AnxInvoiceParent invItem : invoices) {
						invList.add(invItem.getId().toString());
					}
					invoices = getSelectedInvoices(invList, returntype);
					if (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_PE)
							&& isNotEmpty(gstnResponse.getData().getErrorReport())) {
							InvoiceParentSupport retInvoice = gstnResponse.getData().getErrorReport();
							//saveGSTErrors(invoices, retInvoice, returntype);
						
					} else if (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_P)) {
						
							for (AnxInvoiceParent invoice : invoices) {
								invoice.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
								invoice.setGstStatus(MasterGSTConstants.GST_STATUS_SUCCESS);
								invoice.setGstRefId(refId);
							}
							saveInvoices(invoices, returntype);
						
					} else if (isNotEmpty(gstnResponse)) {
						String message = "";
						if (isNotEmpty(gstnResponse.getData()) && isNotEmpty(gstnResponse.getData().getErrorReport())
								&& isNotEmpty(gstnResponse.getData().getErrorReport().getErrorMsg())) {
							message = gstnResponse.getData().getErrorReport().getErrorMsg();
						} else if (isNotEmpty(gstnResponse.getError())
								&& isNotEmpty(gstnResponse.getError().getMessage())) {
							message = gstnResponse.getError().getMessage();
						} else if (isNotEmpty(gstnResponse.getStatusdesc())) {
							message = gstnResponse.getStatusdesc();
							if (isNotEmpty(gstnResponse.getData().getStatusCd()) && gstnResponse.getData().getStatusCd()
									.equals(MasterGSTConstants.GST_RETURN_CODE_IP)) {
								message = "In Progress";
							}
						}
						
							for (AnxInvoiceParent invoice : invoices) {
								invoice.setGstStatus(message);
								if(message.equalsIgnoreCase("In Progress")) {
									invoice.setGstRefId(refId);
								}
							}
							saveInvoices(invoices, returntype);
						
					}
					String usrid = userid(userid,clientid);
					SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(usrid);
					if(isNotEmpty(subscriptionDetails)) {
						if(isNotEmpty(usertype) && usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS) 
								&& isNotEmpty(subscriptionDetails.getUserid())
								&& !userid.equals(subscriptionDetails.getUserid())) {
							subscriptionDetails.setId(new ObjectId());
							subscriptionDetails.setUserid(userid);
						}
						if(isNotEmpty(subscriptionDetails.getProcessedInvoices())) {
							subscriptionDetails
									.setProcessedInvoices(subscriptionDetails.getProcessedInvoices() + invoices.size());
						} else {
							subscriptionDetails.setProcessedInvoices(invoices.size());
						}
						subscriptionService.updateSubscriptionData(subscriptionDetails);
					}
					return gstnResponse;
				}
				return saveResponse;
			} catch(Exception e) {
				Response response = new Response();
				response.setStatuscd("0");
				response.setStatusdesc(e.getMessage());
				return response;
			}
		}
		Response emptyResponse = new Response();
		emptyResponse.setStatuscd("1");
		ResponseData responseData = new ResponseData();
		responseData.setStatusCd(MasterGSTConstants.GST_RETURN_CODE_P);
		emptyResponse.setData(responseData);
		return emptyResponse;
	}
	
	private String getStateCode(String strState) {
		List<StateConfig> states = configService.getStates();
		if (isNotEmpty(strState)) {
			for (StateConfig state : states) {
				if (state.getName().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getCode().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getTin().toString().equals(strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				} else if (state.getName().equals(state.getTin() + "-" + strState)) {
					Integer tin = state.getTin();
					return (tin < 10) ? ("0" + tin) : (tin + "");
				}
			}
			if (strState.contains("-")) {
				strState = strState.substring(0, strState.indexOf("-")).trim();
				return (strState.length() < 2) ? ("0" + strState) : strState;
			}
			if (strState.equals(",")) {
				return "";
			}
		}
		return strState;
	}
	
	@Override
	@Transactional(readOnly=true)
	public List<? extends AnxInvoiceParent> getSelectedInvoices(final List<String> invoiceList, final String returnType) {
		logger.debug(CLASSNAME + "getSelectedInvoices : Begin");
		if (isNotEmpty(returnType)) {
			if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
				Iterable<? extends AnxInvoiceParent> invIterable = null;
					invIterable = anx1Repository.findAll(invoiceList);
				if (isNotEmpty(invIterable)) {
					List<AnxInvoiceParent> list=Lists.newArrayList();
					CollectionUtils.addAll(list, invIterable);
					return list;
				}
			}else if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
				Iterable<? extends AnxInvoiceParent> invIterable = null;
				invIterable = anx2Repository.findAll(invoiceList);
				if (isNotEmpty(invIterable)) {
					List<AnxInvoiceParent> list=Lists.newArrayList();
					CollectionUtils.addAll(list, invIterable);
					return list;
				}
			}
		}
		return Lists.newArrayList();
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
					ClientUserMapping clntusermapping = clientUserMappingRepository
							.findByClientidAndCreatedByIsNotNull(clientid);
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


	@Override
	@Transactional
	public void saveInvoices(Iterable<? extends AnxInvoiceParent> invoices, final String returnType) {
		logger.debug(CLASSNAME + "saveInvoices : Begin");
		anx1Repository.save((Iterable<NewAnx1>) invoices);
		logger.debug(CLASSNAME + "saveInvoices : End");
	}
	
	
	@Override
	@Transactional
	public List<? extends AnxInvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType,
			int month, int year, final String status) {
		logger.debug(CLASSNAME + "getInvoices : Begin");
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		if (isNotEmpty(returnType)) {
			if(isNotEmpty(client.getFilingoptions())) {
				String yr;
				if(month == 1 || month == 2 || month == 3) {
					yr = (year-1)+"-"+(year);
				}else {
					yr = year+"-"+(year+1);
				}
				//String yr=year+"-"+(year+1);
				client.getFilingoptions().forEach(options->{
					if(options.getYear().equalsIgnoreCase(yr)){
						client.setFilingOption(options.getOption());			
					}
				});
			}
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			if (isNotEmpty(client.getFilingOption())
					&& client.getFilingOption().equals(MasterGSTConstants.FILING_OPTION_QUARTERLY)) {
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year, 0, 0, 23, 59, 59);
				} else if(month == 4 || month == 5 || month == 6) {
					cal.set(year, 3, 0, 23, 59, 59);
				} else if(month == 7 || month == 8 || month == 9) {
					cal.set(year, 6, 0, 23, 59, 59);
				} else if(month == 10 || month == 11 || month == 12) {
					cal.set(year, 9, 0, 23, 59, 59);
				}
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(month == 1 || month == 2 || month == 3) {
					cal.set(year, 3, 0, 23, 59, 59);
				} else if(month == 4 || month == 5 || month == 6) {
					cal.set(year, 6, 0, 23, 59, 59);
				} else if(month == 7 || month == 8 || month == 9) {
					cal.set(year, 9, 0, 23, 59, 59);
				} else if(month == 10 || month == 11 || month == 12) {
					cal.set(year + 1, 0, 0, 23, 59, 59);
				}
				endDate = new java.util.Date(cal.getTimeInMillis());
			} else {
				cal.set(year, month - 1, 0, 23, 59, 59);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, month, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
			if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
			Page<? extends AnxInvoiceParent> invoices = anx1Repository.findByClientidAndDateofinvoiceBetween(client.getId().toString(), stDate, endDate, pageable);
				List<AnxInvoiceParent> filteredInvoices = Lists.newArrayList();
				if (isNotEmpty(invoices)) {
					for (AnxInvoiceParent invoiceParent : invoices) {
						if (isEmpty(invoiceParent.getGstStatus()) || !invoiceParent.getGstStatus().equals("CANCELLED")) {
							filteredInvoices.add(invoiceParent);
						}
					}
				}
				return filteredInvoices;
			}else if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			Page<? extends AnxInvoiceParent> invoices = anx2Repository.findByClientidAndDateofinvoiceBetween(client.getId().toString(), stDate, endDate, pageable);
				List<AnxInvoiceParent> filteredInvoices = Lists.newArrayList();
				if (isNotEmpty(invoices)) {
					for (AnxInvoiceParent invoiceParent : invoices) {
						if (isEmpty(invoiceParent.getGstStatus()) || !invoiceParent.getGstStatus().equals("CANCELLED")) {
							filteredInvoices.add(invoiceParent);
						}
					}
				}
				return filteredInvoices;
			}
		}
		return Lists.newArrayList();
	}

	@Override
	public void performDownload(Client client, String invType, String string, String clientid, String id, int month, int year) {
		Anx1InvoiceSupport anx1 = null;
		try {
			anx1 = iHubConsumerService.getANX1Invoices(client, client.getGstnnumber(), month, year, MasterGSTConstants.ANX1, invType, null, id, true);
		} catch (MasterGSTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			logger.info(CLASSNAME + "performAnx2Reconcile : anx2\t" + anx1);
			if(isNotEmpty(anx1)) {
				processAnx1Data(anx1, client, MasterGSTConstants.ANX1, invType, id, month, year);
			}
	}

	public void processAnx1Data(Anx1InvoiceSupport invoice, final Client client, String returnType,
			String invType, String userid, final int month, final int year) {
		logger.debug(CLASSNAME + "processAnx1Data : Begin");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		List<String> receivedInvIds = Lists.newArrayList();
		List<String> receivedInvIdss = Lists.newArrayList();
		String strMonth = month < 10 ? "0" + month : month + "";
	
			if (invType.equals(MasterGSTConstants.B2B)) {
				if (isNotEmpty(invoice) && isNotEmpty(invoice.getAnxb2b())) {
					for (AnxDocs anxb2b : invoice.getAnxb2b()) {
							if(isNotEmpty(anxb2b.getDocs())) {
								for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
									String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									
										if(isNotEmpty(anxb2b.getCtin())) {
											String ctin = anxb2b.getCtin();
											invNoCtin = invNoCtin+ctin;
										}
										if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
											//String idate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
											invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
										}
									receivedInvIdss.add(invNoCtin);
									receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
								}
							}
				}
			} 
		}else if(invType.equals(MasterGSTConstants.DE)) {
				if (isNotEmpty(invoice) && isNotEmpty(invoice.getDe())) {
					for (AnxDocs anxb2b : invoice.getDe()) {
							if(isNotEmpty(anxb2b.getDocs())) {
								for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
									String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
										if(isNotEmpty(anxb2b.getCtin())) {
											String ctin = anxb2b.getCtin();
											invNoCtin = invNoCtin+ctin;
										}
										if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
											invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
										}
									receivedInvIdss.add(invNoCtin);
									receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
								}
							}
				}
			} 
		}else if(invType.equals(MasterGSTConstants.SEZWP)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getSezwp())) {
				for (AnxDocs anxb2b : invoice.getSezwp()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
								
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										//String idate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			}
		} 
	}else if(invType.equals(MasterGSTConstants.SEZWOP)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getSezwp())) {
				for (AnxDocs anxb2b : invoice.getSezwp()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
								
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										//String idate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			}
		 } 
	}else if(invType.equals(MasterGSTConstants.B2BA)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getB2ba())) {
				for (AnxDocs anxb2b : invoice.getB2ba()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			}
		} 
		}else if(invType.equals(MasterGSTConstants.IMPG)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getImpg())) {
				for (AnxDocs anxb2b : invoice.getImpg()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getBoe().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getBoe().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getBoe().getNum());
							}
						}
			}
		} 
		}else if(invType.equals(MasterGSTConstants.IMPGSEZ)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getImpgsez())) {
				for (AnxDocs anxb2b : invoice.getImpgsez()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getBoe().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getBoe().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getBoe().getNum());
							}
						}
			}
		} 
		}else if(invType.equals(MasterGSTConstants.MIS)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getMis())) {
				for (AnxDocs anxb2b : invoice.getMis()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			}
		} 
		}else if(invType.equals(MasterGSTConstants.SEZWPA)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getSezwpa())) {
				for (AnxDocs anxb2b : invoice.getSezwpa()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			}
		} 
		}else if(invType.equals(MasterGSTConstants.SEZWOPA)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getSezwopa())) {
				for (AnxDocs anxb2b : invoice.getSezwopa()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			  }
		   } 
		}else if(invType.equals(MasterGSTConstants.DEA)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getDea())) {
				for (AnxDocs anxb2b : invoice.getDea()) {
						if(isNotEmpty(anxb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : anxb2b.getDocs()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									}
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
						}
			  }
		   } 
		}else if (invType.equals(MasterGSTConstants.B2C)){
			if (isNotEmpty(invoice.getB2cs())){
				for(AnxDetails anxb2c : invoice.getB2c()){
					if (isNotEmpty(anxb2c.getPos())){
						receivedInvIdss.add(anxb2c.getPos());
						receivedInvIds.add(anxb2c.getPos());
					}
				}
			}
		}else if(invType.equals(MasterGSTConstants.EXPWP)) {
				if (isNotEmpty(invoice) && isNotEmpty(invoice.getExpwp())) {
							for (AnxDetails gstrInvoiceDetails : invoice.getExpwp()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
									/* if(isNotEmpty(anxb2b.getCtin())) {
										String ctin = anxb2b.getCtin();
										invNoCtin = invNoCtin+ctin;
									} */
									if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
										invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
									}
								receivedInvIdss.add(invNoCtin);
								receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
							}
				} 
		}else if(invType.equals(MasterGSTConstants.EXPWOP)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getExpwop())) {
				for (AnxDetails gstrInvoiceDetails : invoice.getExpwop()) {
					String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
						/* if(isNotEmpty(anxb2b.getCtin())) {
							String ctin = anxb2b.getCtin();
							invNoCtin = invNoCtin+ctin;
						} */
						if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
							invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
						}
					receivedInvIdss.add(invNoCtin);
					receivedInvIds.add(gstrInvoiceDetails.getDoc().getNum());
				}
			} 
		}else if(invType.equals(MasterGSTConstants.ECOM)) {
			if (isNotEmpty(invoice) && isNotEmpty(invoice.getEcom())) {
				for (AnxEcom anxecom : invoice.getEcom()) {
					if(isNotEmpty(anxecom.getEtin())) {
						receivedInvIdss.add(anxecom.getEtin());
						receivedInvIds.add(anxecom.getEtin());
					}
			  }
		   } 
		}else if (invType.equals(MasterGSTConstants.IMPS)){
			if (isNotEmpty(invoice.getImps())){
				for(AnxDetails anximps : invoice.getImps()){
					if (isNotEmpty(anximps.getPos())){
						receivedInvIdss.add(anximps.getPos());
						receivedInvIds.add(anximps.getPos());
					}
				}
			}
		}
	Calendar cal = Calendar.getInstance();
		if(isNotEmpty(receivedInvIds)) {
			List<NewAnx1> existingRecords = anx1Repository.findByClientidAndInvoicenoIn(client.getId().toString(),receivedInvIds);
			if(isNotEmpty(existingRecords)) {
				List<String> availableInvoices = Lists.newArrayList();
				List<String> availableInvoicess = Lists.newArrayList();
				for( NewAnx1 anx1 : existingRecords) {
					if (anx1.getInvtype().equals(B2B)) {
						if (isNotEmpty(anx1.getAnxb2b())) {
							for (AnxDocs gstrb2b : anx1.getAnxb2b()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
										
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												//String idate1 = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
						
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.DE)) {
						if (isNotEmpty(anx1.getDe())) {
							for (AnxDocs gstrb2b : anx1.getDe()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
										
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												//String idate1 = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.SEZWP)) {
						if (isNotEmpty(anx1.getSezwp())) {
							for (AnxDocs gstrb2b : anx1.getSezwp()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
										
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												//String idate1 = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.SEZWOP)) {
						if (isNotEmpty(anx1.getSezwop())) {
							for (AnxDocs gstrb2b : anx1.getSezwop()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
										
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												//String idate1 = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.B2BA)) {
						if (isNotEmpty(anx1.getB2ba())) {
							for (AnxDocs gstrb2b : anx1.getB2ba()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.IMPG)) {
						if (isNotEmpty(anx1.getImpg())) {
							for (AnxDocs gstrb2b : anx1.getImpg()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getBoe().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getBoe().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.IMPGSEZ)) {
						if (isNotEmpty(anx1.getImpgsez())) {
							for (AnxDocs gstrb2b : anx1.getImpgsez()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getBoe().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getBoe().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
							
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.MIS)) {
						if (isNotEmpty(anx1.getMis())) {
							for (AnxDocs gstrb2b : anx1.getMis()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.SEZWPA)) {
						if (isNotEmpty(anx1.getSezwpa())) {
							for (AnxDocs gstrb2b : anx1.getSezwpa()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
						}
					} else if (anx1.getInvtype().equals(MasterGSTConstants.SEZWOPA)) {
						if (isNotEmpty(anx1.getSezwopa())) {
							for (AnxDocs gstrb2b : anx1.getSezwopa()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.DEA)) {
						if (isNotEmpty(anx1.getDea())) {
							for (AnxDocs gstrb2b : anx1.getDea()) {
								if(isNotEmpty(gstrb2b.getDocs())) {
									for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
										String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
											if(isNotEmpty(gstrb2b.getCtin())) {
												String ctin = gstrb2b.getCtin();
												invNoCtin = invNoCtin+ctin;
											}
											if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
												invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
											}
											availableInvoicess.add(invNoCtin);
									}
								}
								availableInvoices.add(anx1.getInvoiceno());
							}
						}
					}else if (anx1.getInvtype().equals(MasterGSTConstants.EXPWP)) {
						if (isNotEmpty(anx1.getExpwp())) {
							for (AnxDetails gstrInvoiceDetails : anx1.getExpwp()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
								/* 	if(isNotEmpty(gstrb2b.getCtin())) {
								String ctin = gstrb2b.getCtin();
								invNoCtin = invNoCtin+ctin;
								} */
							   if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								availableInvoicess.add(invNoCtin);
							}
							availableInvoices.add(anx1.getInvoiceno());
						}
					} else if (anx1.getInvtype().equals(MasterGSTConstants.EXPWOP)) {
						if (isNotEmpty(anx1.getExpwop())) {
							for (AnxDetails gstrInvoiceDetails : anx1.getExpwop()) {
								String invNoCtin = gstrInvoiceDetails.getDoc().getNum();
								/* 	if(isNotEmpty(gstrb2b.getCtin())) {
								String ctin = gstrb2b.getCtin();
								invNoCtin = invNoCtin+ctin;
								} */
							   if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									invNoCtin = invNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								availableInvoicess.add(invNoCtin);
							}
							availableInvoices.add(anx1.getInvoiceno());
						}
					}                                                
				}
				anx1Repository.save(existingRecords);
				receivedInvIdss.removeAll(availableInvoicess);
				receivedInvIds.removeAll(availableInvoices);
			}
			
			String fp = strMonth + year;
			if(invType.equals(MasterGSTConstants.B2C)) {
				existingRecords = anx1Repository.findByClientidAndFpAndInvtype(client.getId().toString(), fp, MasterGSTConstants.B2C);
				if(isNotEmpty(existingRecords)) {
					List<String> availableInvoices = Lists.newArrayList();
					List<String> availableInvoicess = Lists.newArrayList();
					for(NewAnx1 anx1 : existingRecords) {
						anx1.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
						
						if(isEmpty(anx1.getDateofinvoice())) {
							if(isNotEmpty(anx1.getFp())) {
								try {
									int iMonth = Integer.parseInt(anx1.getFp().substring(0, 2));
									int iYear = Integer.parseInt(anx1.getFp().substring(2));
									cal.set(iYear, iMonth-1, 1);
									anx1.setDateofinvoice(cal.getTime());
								} catch (Exception e) {
								}
							} else {
								cal.set(year, month-1, 1);
								anx1.setDateofinvoice(cal.getTime());
							}
						}
						if (isNotEmpty(anx1.getB2c())) {
							for(AnxDetails gstrb2cs : anx1.getB2c()) {
								if (isNotEmpty(gstrb2cs.getPos())) {
									availableInvoices.add(gstrb2cs.getPos());
									availableInvoicess.add(gstrb2cs.getPos());
								}
							}
						}
					}
					anx1Repository.save(existingRecords);
					receivedInvIds.removeAll(availableInvoices);
					receivedInvIdss.removeAll(availableInvoicess);
				}
			}
			
			if(invType.equals(MasterGSTConstants.ECOM)) {
				existingRecords = anx1Repository.findByClientidAndFpAndInvtype(client.getId().toString(), fp, MasterGSTConstants.ECOM);
				if(isNotEmpty(existingRecords)) {
					List<String> availableInvoices = Lists.newArrayList();
					List<String> availableInvoicess = Lists.newArrayList();
					for(NewAnx1 anx1 : existingRecords) {
						anx1.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
						
						if(isEmpty(anx1.getDateofinvoice())) {
							if(isNotEmpty(anx1.getFp())) {
								try {
									int iMonth = Integer.parseInt(anx1.getFp().substring(0, 2));
									int iYear = Integer.parseInt(anx1.getFp().substring(2));
									cal.set(iYear, iMonth-1, 1);
									anx1.setDateofinvoice(cal.getTime());
								} catch (Exception e) {
								}
							} else {
								cal.set(year, month-1, 1);
								anx1.setDateofinvoice(cal.getTime());
							}
						}
						if (isNotEmpty(anx1.getEcom())) {
							for(AnxEcom anxecomm : anx1.getEcom()) {
								if (isNotEmpty(anxecomm.getEtin())) {
									availableInvoices.add(anxecomm.getEtin());
									availableInvoicess.add(anxecomm.getEtin());
								}
							}
						}
					}
					anx1Repository.save(existingRecords);
					receivedInvIds.removeAll(availableInvoices);
					receivedInvIdss.removeAll(availableInvoicess);
				}
			}
			
			if(invType.equals(MasterGSTConstants.IMPS)) {
				existingRecords = anx1Repository.findByClientidAndFpAndInvtype(client.getId().toString(), fp, MasterGSTConstants.IMPS);
				if(isNotEmpty(existingRecords)) {
					List<String> availableInvoices = Lists.newArrayList();
					List<String> availableInvoicess = Lists.newArrayList();
					for(NewAnx1 anx1 : existingRecords) {
						anx1.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
						
						if(isEmpty(anx1.getDateofinvoice())) {
							if(isNotEmpty(anx1.getFp())) {
								try {
									int iMonth = Integer.parseInt(anx1.getFp().substring(0, 2));
									int iYear = Integer.parseInt(anx1.getFp().substring(2));
									cal.set(iYear, iMonth-1, 1);
									anx1.setDateofinvoice(cal.getTime());
								} catch (Exception e) {
								}
							} else {
								cal.set(year, month-1, 1);
								anx1.setDateofinvoice(cal.getTime());
							}
						}
						if (isNotEmpty(anx1.getImps())) {
							for(AnxDetails gstrb2cs : anx1.getImps()) {
								if (isNotEmpty(gstrb2cs.getPos())) {
									availableInvoices.add(gstrb2cs.getPos());
									availableInvoicess.add(gstrb2cs.getPos());
								}
							}
						}
					}
					anx1Repository.save(existingRecords);
					receivedInvIds.removeAll(availableInvoices);
					receivedInvIdss.removeAll(availableInvoicess);
				}
			}
		}
		if(isNotEmpty(receivedInvIdss)) {
		List<NewAnx1> newRecords = Lists.newArrayList(); 
		List<GSTR2> newRecords1 = Lists.newArrayList(); 
		if(isNotEmpty(invoice)) {
			 if (isNotEmpty(invoice.getAnxb2b()) && invType.equals(MasterGSTConstants.B2B)) {
				for (AnxDocs gstrb2b : invoice.getAnxb2b()) {
					if(isNotEmpty(gstrb2b.getDocs())) {
						Map<String, ResponseData> gstnMap = Maps.newHashMap();
						for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
							String dwnldInvNoCtin = "";
							if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
								dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
							}
							// && gstnMap.containsKey(gstrb2b.getCtin()
							if(isNotEmpty(gstrb2b.getCtin())) {
								String dwnldctin = gstrb2b.getCtin();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
							}
							if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
								//String dwnlddate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
								dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
							}
							
						
								NewAnx1 newInvoice = null;
								try {
									newInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.B2B, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									logger.info("ParseException----"+e.getMessage());
									e.printStackTrace();
								}
								//GSTR2 newInvoice1 = populateGSTR2(gstrInvoiceDetails, null, null, userid, client.getId().toString(), invType, gstrb2b.getCtin(), null);
									
							
								if(isNotEmpty(gstrb2b.getCtin())){
									String gstnno = gstrb2b.getCtin();
									gstnno = gstnno.substring(0,2);
									for(StateConfig stateConfig : configService.getStates()) {
										if (gstnno.equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
											if(isNotEmpty(stateConfig.getName())) {
												//newInvoice.setStatename(stateConfig.getName());
											}
										}
									}
								}
								if (isNotEmpty(gstrb2b.getCtin()) && gstnMap.containsKey(gstrb2b.getCtin())) {
									if(isNotEmpty(gstnMap.get(gstrb2b.getCtin()).getTradeNam())) {
										//newInvoice.setBilledtoname(gstnMap.get(gstrb2b.getCtin()).getTradeNam());
									}
								}
								newRecords.add(newInvoice);
								//newRecords1.add(newInvoice1);
							}
						}
					}
				}else  if (isNotEmpty(invoice.getDe()) && invType.equals(MasterGSTConstants.DE)) {
					for (AnxDocs gstrb2b : invoice.getDe()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								// && gstnMap.containsKey(gstrb2b.getCtin()
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									//String dwnlddate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.DE, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								
								newRecords.add(deInvoice);
							}	
							
							
						}
					}
				}else  if (isNotEmpty(invoice.getSezwp()) && invType.equals(MasterGSTConstants.SEZWP)) {
					for (AnxDocs gstrb2b : invoice.getSezwp()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								// && gstnMap.containsKey(gstrb2b.getCtin()
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									//String dwnlddate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.SEZWP, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								
								newRecords.add(deInvoice);
							}	
							
							
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.SEZWOP)) {
					for (AnxDocs gstrb2b : invoice.getSezwop()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								// && gstnMap.containsKey(gstrb2b.getCtin()
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									//String dwnlddate = simpleDateFormat.format(gstrInvoiceDetails.getDoc().getDt());
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.SEZWOP, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								
								newRecords.add(deInvoice);
							}	
							
							
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.B2BA)) {
					for (AnxDocs gstrb2b : invoice.getB2ba()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.B2BA, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.IMPG)) {
					for (AnxDocs gstrb2b : invoice.getImpg()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getBoe().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getBoe().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getBoe().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.IMPG, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.IMPGSEZ)) {
					for (AnxDocs gstrb2b : invoice.getImpgsez()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getBoe().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getBoe().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getBoe().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getBoe().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.IMPGSEZ, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.MIS)) {
					for (AnxDocs gstrb2b : invoice.getMis()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.MIS, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.SEZWPA)) {
					for (AnxDocs gstrb2b : invoice.getSezwpa()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.SEZWPA, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getSezwop()) && invType.equals(MasterGSTConstants.SEZWOPA)) {
					for (AnxDocs gstrb2b : invoice.getSezwopa()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.SEZWOPA, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getDea()) && invType.equals(MasterGSTConstants.DEA)) {
					for (AnxDocs gstrb2b : invoice.getDea()) {
						if(isNotEmpty(gstrb2b.getDocs())) {
							for (AnxDetails gstrInvoiceDetails : gstrb2b.getDocs()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								}
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.DEA, gstrb2b.getCtin(), null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
						}
					}
				}else  if (isNotEmpty(invoice.getExpwp()) && invType.equals(MasterGSTConstants.EXPWP)) {
							for (AnxDetails gstrInvoiceDetails : invoice.getExpwp()) {
								String dwnldInvNoCtin = "";
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
									dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
								}
								/* if(isNotEmpty(gstrb2b.getCtin())) {
									String dwnldctin = gstrb2b.getCtin();
									dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
								} */
								if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
									dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
								}
								
								NewAnx1 deInvoice = null;
								try {
									deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.EXPWP, null, null);
								} catch (ParseException e) {
									e.printStackTrace();
								}
								newRecords.add(deInvoice);
							}	
				}else  if (isNotEmpty(invoice.getExpwp()) && invType.equals(MasterGSTConstants.EXPWOP)) {
					for (AnxDetails gstrInvoiceDetails : invoice.getExpwop()) {
						String dwnldInvNoCtin = "";
						if(isNotEmpty(gstrInvoiceDetails.getDoc().getNum())){
							dwnldInvNoCtin = gstrInvoiceDetails.getDoc().getNum();
						}
						/* if(isNotEmpty(gstrb2b.getCtin())) {
							String dwnldctin = gstrb2b.getCtin();
							dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
						} */
						if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
							dwnldInvNoCtin = dwnldInvNoCtin+gstrInvoiceDetails.getDoc().getDt();
						}
						
						NewAnx1 deInvoice = null;
						try {
							deInvoice = populateAnx1(gstrInvoiceDetails, null, null, userid, client.getId().toString(), MasterGSTConstants.EXPWOP, null, null);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						newRecords.add(deInvoice);
					}	
		} else if (isNotEmpty(invoice.getB2c()) && invType.equals(MasterGSTConstants.B2C)) {
					for (AnxDetails anxb2c : invoice.getB2c()) {
						if (isNotEmpty(anxb2c.getPos()) && receivedInvIdss.contains(anxb2c.getPos())) {
							NewAnx1 newInvoice = null;
							try {
								newInvoice = populateAnx1(anxb2c, null, null, userid, client.getId().toString(), MasterGSTConstants.B2C, null, null);
							} catch (ParseException e1) {
								logger.info("ANX1 B2C download-------"+e1.getMessage());
								e1.printStackTrace();
							}
							newInvoice.getB2c().add(anxb2c);
							if(isNotEmpty(anxb2c.getPos())){
								String pos = anxb2c.getPos();
								for(StateConfig stateConfig : configService.getStates()) {
									if (pos.equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
										newInvoice.setStatename(stateConfig.getName());
									}
								}
							}
							
							if(isEmpty(newInvoice.getDateofinvoice())) {
								if(isNotEmpty(newInvoice.getFp())) {
									try {
										int iMonth = Integer.parseInt(newInvoice.getFp().substring(0, 2));
										int iYear = Integer.parseInt(newInvoice.getFp().substring(2));
										cal.set(iYear, iMonth-1, 1);
										newInvoice.setDateofinvoice(cal.getTime());
									} catch (Exception e) {
									}
								} else {
									cal.set(year, month-1, 1);
									newInvoice.setDateofinvoice(cal.getTime());
								}
							}
							
							newRecords.add(newInvoice);
						}
					}
				} else if (isNotEmpty(invoice.getEcom()) && invType.equals(MasterGSTConstants.ECOM)) {
					NewAnx1 newInvoice = null;
					for (AnxEcom anxecom : invoice.getEcom()) {
						if (isNotEmpty(anxecom.getEtin()) && receivedInvIdss.contains(anxecom.getEtin())) {
							AnxEcom ecom = new AnxEcom();
							List<AnxEcom> anxecomList = Lists.newArrayList();
								if(isNotEmpty(anxecom.getEtin())){
									ecom.setEtin(anxecom.getEtin());
								}
								if(isNotEmpty(anxecom.getChksum())) {
									ecom.setChksum(anxecom.getChksum());
								}
								if(isNotEmpty(anxecom.getFlag())) {
									ecom.setFlag(anxecom.getFlag());
								}
								if(isNotEmpty(anxecom.getSup())) {
									ecom.setSup(anxecom.getSup());
								}
								if(isNotEmpty(anxecom.getSupr())) {
									ecom.setSupr(anxecom.getSupr());
								}
								if(isNotEmpty(anxecom.getNsup())) {
									ecom.setNsup(anxecom.getNsup());
								}
								if(isNotEmpty(anxecom.getIgst())) {
									ecom.setIgst(anxecom.getIgst());
								}
								if(isNotEmpty(anxecom.getCgst())) {
									ecom.setCgst(anxecom.getCgst());
								}
								if(isNotEmpty(anxecom.getSgst())) {
									ecom.setSgst(anxecom.getSgst());
								}
								if(isNotEmpty(anxecom.getCess())) {
									ecom.setCess(anxecom.getCess());;
								}
								
								anxecomList.add(ecom);
							
							newInvoice.setEcom(anxecomList);
							if(isEmpty(newInvoice.getDateofinvoice())) {
								if(isNotEmpty(newInvoice.getFp())) {
									try {
										int iMonth = Integer.parseInt(newInvoice.getFp().substring(0, 2));
										int iYear = Integer.parseInt(newInvoice.getFp().substring(2));
										cal.set(iYear, iMonth-1, 1);
										newInvoice.setDateofinvoice(cal.getTime());
									} catch (Exception e) {
									}
								} else {
									cal.set(year, month-1, 1);
									newInvoice.setDateofinvoice(cal.getTime());
								}
							}
							
							newRecords.add(newInvoice);
						}
					}
				}else if (isNotEmpty(invoice.getImps()) && invType.equals(MasterGSTConstants.IMPS)) {
					for (AnxDetails anximps : invoice.getImps()) {
						if (isNotEmpty(anximps.getPos()) && receivedInvIdss.contains(anximps.getPos())) {
							NewAnx1 newInvoice = null;
							try {
								newInvoice = populateAnx1(anximps, null, null, userid, client.getId().toString(), MasterGSTConstants.IMPS, null, null);
							} catch (ParseException e1) {
								logger.info("ANX1 IMPS download-------"+e1.getMessage());
								e1.printStackTrace();
							}
							newInvoice.getImps().add(anximps);
							if(isNotEmpty(anximps.getPos())){
								String pos = anximps.getPos();
								for(StateConfig stateConfig : configService.getStates()) {
									if (pos.equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
										newInvoice.setStatename(stateConfig.getName());
									}
								}
							}
							
							if(isEmpty(newInvoice.getDateofinvoice())) {
								if(isNotEmpty(newInvoice.getFp())) {
									try {
										int iMonth = Integer.parseInt(newInvoice.getFp().substring(0, 2));
										int iYear = Integer.parseInt(newInvoice.getFp().substring(2));
										cal.set(iYear, iMonth-1, 1);
										newInvoice.setDateofinvoice(cal.getTime());
									} catch (Exception e) {
									}
								} else {
									cal.set(year, month-1, 1);
									newInvoice.setDateofinvoice(cal.getTime());
								}
							}
							
							newRecords.add(newInvoice);
						}
					}
				}
			 
			}
	//logger.info("newRecords-------"+newRecords.get(0).getAnx2b2b().get(0).getDocs().get(0));
		if(isNotEmpty(newRecords)) {
			anx1Repository.save(newRecords);
		}
		//gstr2Repository.save(newRecords1);
	}
}
	
	private NewAnx1 populateAnx1(AnxDetails gstrInvoiceDetails, final String gstn, final String fp,
			final String userid, final String clientid, final String invType, final String ctin,final String cfs) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		NewAnx1 individualInvoice = new NewAnx1();
		individualInvoice.setGstin(gstn);
		individualInvoice.setFp(fp);
		individualInvoice.setInvtype(invType);
		individualInvoice.setUserid(userid);
		individualInvoice.setClientid(clientid);
		individualInvoice.setInvoiceno(gstrInvoiceDetails.getDoc().getNum());
		
		if(isNotEmpty(gstrInvoiceDetails.getDoc().getDt())) {
			Date dt1=new SimpleDateFormat("dd-MM-yyyy").parse(gstrInvoiceDetails.getDoc().getDt());
			individualInvoice.setDateofinvoice(dt1);
		}
	
		AnxDocs indGstrb2b = new AnxDocs();
		AnxBoe anxboe = new AnxBoe();
		List<AnxDetails> anxDetailsList = Lists.newArrayList();
		List<AnxDocs> anxB2BList = Lists.newArrayList();
		AnxDetails anxdetails = new AnxDetails();
		
		if (isNotEmpty(ctin)) {
			indGstrb2b.setCtin(ctin);
		}
		anxdetails.setId(new ObjectId());
		if(isNotEmpty(gstrInvoiceDetails.getPos())) {
			anxdetails.setPos(gstrInvoiceDetails.getPos());
		}
		if(isNotEmpty(gstrInvoiceDetails.getOctin())) {
			anxdetails.setOctin(gstrInvoiceDetails.getOctin());
		}
		if(isNotEmpty(gstrInvoiceDetails.getDoctyp())) {
			anxdetails.setDoctyp(gstrInvoiceDetails.getDoctyp());
		}
		if(isNotEmpty(gstrInvoiceDetails.getOdoctyp())) {
			anxdetails.setOdoctyp(gstrInvoiceDetails.getOdoctyp());
		}
		if(isNotEmpty(gstrInvoiceDetails.getInvalid())) {
			anxdetails.setInvalid(gstrInvoiceDetails.getInvalid());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAmended())) {
			anxdetails.setAmended(gstrInvoiceDetails.getAmended());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAmdtyp())) {
			anxdetails.setAmdtyp(gstrInvoiceDetails.getAmdtyp());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAprd())) {
			anxdetails.setAprd(gstrInvoiceDetails.getAprd());
		}
		if(isNotEmpty(gstrInvoiceDetails.getOdoc())) {
			anxdetails.setOdoc(gstrInvoiceDetails.getOdoc());
		}
		if(isNotEmpty(gstrInvoiceDetails.getSec7act())) {
			anxdetails.setSec7act(gstrInvoiceDetails.getSec7act());
		}
		if(isNotEmpty(gstrInvoiceDetails.getDiffprcnt())) {
			anxdetails.setDiffprcnt(gstrInvoiceDetails.getDiffprcnt());
		}
		if(isNotEmpty(gstrInvoiceDetails.getRfndelg())) {
			anxdetails.setRfndelg(gstrInvoiceDetails.getRfndelg());
		}
		
		if(isNotEmpty(gstrInvoiceDetails.getAction())) {
			anxdetails.setAction(gstrInvoiceDetails.getAction());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAmdtyp())) {
			anxdetails.setAmdtyp(gstrInvoiceDetails.getAmdtyp());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAprd())) {
			anxdetails.setAprd(gstrInvoiceDetails.getAprd());
		}
		if(isNotEmpty(gstrInvoiceDetails.getAmended())) {
			anxdetails.setAmended(gstrInvoiceDetails.getAmended());
		}
		if(isNotEmpty(gstrInvoiceDetails.getChksum())) {
			anxdetails.setChksum(gstrInvoiceDetails.getChksum());
		}
		if(isNotEmpty(gstrInvoiceDetails.getClmrfnd())) {
			anxdetails.setClmrfnd(gstrInvoiceDetails.getClmrfnd());
		}
		if(isNotEmpty(gstrInvoiceDetails.getDoc())) {
			anxdetails.setDoc(gstrInvoiceDetails.getDoc());
		}
		if(isNotEmpty(gstrInvoiceDetails.getSb())) {
			anxdetails.setSb(gstrInvoiceDetails.getSb());
		}
		if(isNotEmpty(gstrInvoiceDetails.getOdoc())) {
			anxdetails.setOdoc(gstrInvoiceDetails.getOdoc());
		}
		if(isNotEmpty(gstrInvoiceDetails.getBoe())) {
			anxdetails.setBoe(gstrInvoiceDetails.getBoe());
		}
		if(isNotEmpty(gstrInvoiceDetails.getItems())) {
			anxdetails.setItems(gstrInvoiceDetails.getItems());
		}
		anxDetailsList.add(anxdetails);
		indGstrb2b.setDocs(anxDetailsList);
		anxB2BList.add(indGstrb2b);
		//individualInvoice.setAnx2b2b(anx2B2BList);
		//individualInvoice.setDe(anx2B2BList);
		if(invType.equalsIgnoreCase(MasterGSTConstants.B2B)) {
			individualInvoice.setAnxb2b(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.DE)) {
			individualInvoice.setDe(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.SEZWP)) {
			individualInvoice.setSezwp(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.SEZWOP)) {
			individualInvoice.setSezwop(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.B2BA)) {
			individualInvoice.setB2ba(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.IMPG)) {
			individualInvoice.setImpg(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.IMPGSEZ)) {
			individualInvoice.setImpgsez(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.MIS)) {
			individualInvoice.setMis(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.SEZWPA)) {
			individualInvoice.setSezwpa(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.SEZWOPA)) {
			individualInvoice.setSezwopa(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.DEA)) {
			individualInvoice.setDea(anxB2BList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.B2C)) {
			individualInvoice.setB2c(anxDetailsList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.EXPWP)) {
			individualInvoice.setExpwp(anxDetailsList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.EXPWOP)) {
			individualInvoice.setExpwop(anxDetailsList);
		}else if(invType.equalsIgnoreCase(MasterGSTConstants.IMPS)) {
			individualInvoice.setImps(anxDetailsList);
		}    
		individualInvoice.setTotalamount(gstrInvoiceDetails.getDoc().getVal());
		Double totalTaxVal = 0d;
		Double totalTax = 0d;
		
		List<Item> items = Lists.newArrayList();
		for (AnxItems gstrItem : gstrInvoiceDetails.getItems()) {
			Item item = new Item();
			item.setTaxablevalue(gstrItem.getTxval());
			if (gstrInvoiceDetails.getItems().size() == 1) { //Fix for gstr2a invoice edit as purchase register 
				item.setTotal(gstrInvoiceDetails.getDoc().getVal());
			}
			totalTaxVal += gstrItem.getTxval();
			item.setRateperitem(gstrItem.getTxval());
			item.setQuantity(Double.parseDouble("1"));
			if (isNotEmpty(gstrItem.getRate())) {
				item.setRate(gstrItem.getRate());
			}
			if (isNotEmpty(gstrItem.getIgst())) {
				item.setIgstamount(gstrItem.getIgst());
				item.setIgstrate(gstrItem.getRate());
				totalTax += gstrItem.getIgst();
			}
			if (isNotEmpty(gstrItem.getCgst())) {
				item.setCgstamount(gstrItem.getCgst());
				item.setCgstrate(gstrItem.getRate() / 2);
				totalTax += gstrItem.getCgst();
			}
			if (isNotEmpty(gstrItem.getSgst())) {
				item.setCgstamount(gstrItem.getSgst());
				item.setCgstrate(gstrItem.getRate() / 2);
				totalTax += gstrItem.getSgst();
			}
			if (isNotEmpty(gstrItem.getCess())) {
				item.setCessamount(gstrItem.getCess());
			}
			
			items.add(item);
		}
		individualInvoice.setItems(items);
		individualInvoice.setTotaltaxableamount(totalTaxVal);
		individualInvoice.setTotaltax(totalTax);
		
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
		individualInvoice = extrafields(individualInvoice,"Anx1");
		return individualInvoice;
	}	
	public NewAnx1 extrafields(NewAnx1 individualInvoice,String returntype) {
		int mnths = -1,yrs=-1;
		if("Advances".equals(individualInvoice.getInvtype()) || "Nil Supplies".equals(individualInvoice.getInvtype()) || "Advance Adjusted Detail".equals(individualInvoice.getInvtype())){
			String fp = individualInvoice.getFp();
			int sumFactor = 1;
			if(fp != null){
				try {
					mnths = Integer.parseInt(fp.substring(0,2));
					mnths--;
					yrs = Integer.parseInt(fp.substring(2));
				} catch (NumberFormatException e) {
					
				}
			}
			int quarter = mnths/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (yrs-1)+"-"+yrs : (yrs)+"-"+(yrs+1);
			mnths++;
			
			individualInvoice.setMthCd(""+mnths);
			individualInvoice.setYrCd(""+yearCode);
			individualInvoice.setQrtCd(""+quarter);
			individualInvoice.setSftr(sumFactor);
		}else {
			
		String invType = individualInvoice.getInvtype();
		boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
		boolean isCDNA = "CDNA".equals(invType);
		boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
		boolean isCreditNote = "Credit Note".equals(invType);
		boolean isDebitNote = "Debit Note".equals(invType);
		boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
		boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
		boolean isCdnNtNttyExists = individualInvoice.getCdn() != null && individualInvoice.getCdn().size() > 0 && individualInvoice.getCdn().get(0).getNt() != null && individualInvoice.getCdn().get(0).getNt().size() > 0;
		String cdnNtNtty = null;
		if(isCdnNtNttyExists){
			cdnNtNtty = individualInvoice.getCdn().get(0).getNt().get(0).getNtty();
		}
		boolean isCdnurNttyExists = individualInvoice.getCdnur() != null && individualInvoice.getCdnur().size() > 0;
		String cdnurNtty = null;
		if(isCdnurNttyExists){
			cdnurNtty = individualInvoice.getCdnur().get(0).getNtty();
		}
		int sumFactor = 1;
		
		
		if(!"CANCELLED".equals(individualInvoice.getGstStatus())){
			if((isDebitCreditNotes || isCDNA )){
				if(isCdnNtNttyExists){
					if(!"D".equals(cdnNtNtty)){
						sumFactor = -1;
					}
				}
			}else if(isCDNUR && isCdnurNttyExists){
				if("C".equals(cdnurNtty)){
					sumFactor = -1;
				}
			}
			
			if(isCreditNote || isDebitNote || isCDNA){
				if(isCdnNtNttyExists){
					if(!"D".equals(cdnNtNtty)){
						sumFactor = -1;
					}
				}
			}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
				if("C".equals(cdnurNtty)){
					sumFactor = -1;
				}
			}
		}else{
			sumFactor = 0;
		}
		individualInvoice.setSftr(sumFactor);
		Date dt = null;
		if (returntype.equals(MasterGSTConstants.EWAYBILL)) {
			dt = (Date)individualInvoice.geteBillDate();
		}else {
			dt = (Date)individualInvoice.getDateofinvoice();
		}
		if(returntype.equals(MasterGSTConstants.GSTR2)) {
			String fp = individualInvoice.getFp();
			if(fp != null){
				try {
					mnths = Integer.parseInt(fp.substring(0,2));
					mnths--;
					yrs = Integer.parseInt(fp.substring(2));
				} catch (NumberFormatException e) {
					
				}
			}
			int quarter = mnths/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (yrs-1)+"-"+yrs : (yrs)+"-"+(yrs+1);
			mnths++;
			
			individualInvoice.setMthCd(""+mnths);
			individualInvoice.setYrCd(""+yearCode);
			individualInvoice.setQrtCd(""+quarter);
		}else {
			if(isNotEmpty(dt)) {
				int month = dt.getMonth();
				int year = dt.getYear()+1900;
				int quarter = month/3;
				quarter = quarter == 0 ? 4 : quarter;
				String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
				month++;
				
				individualInvoice.setMthCd(""+month);
				individualInvoice.setYrCd(""+yearCode);
				individualInvoice.setQrtCd(""+quarter);
			}
		}
		}
		
		
		Date ewayBillDate = null;
		Double totalTaxableAmt = individualInvoice.getTotaltaxableamount();
		Double totalTax = individualInvoice.getTotaltax();
		Double totalAmt = individualInvoice.getTotalamount();
		Date dateOfInvoice = individualInvoice.getDateofinvoice();
		if(isNotEmpty(individualInvoice.geteBillDate())) {
			ewayBillDate = individualInvoice.geteBillDate();
		}
		String totalTaxableAmtStr = String.format(DOUBLE_FORMAT,totalTaxableAmt);
		String totalTaxStr = String.format(DOUBLE_FORMAT,totalTax);
		String totalAmtStr = String.format(DOUBLE_FORMAT,totalAmt);
		String dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
		String ewayBillDateStr = "";
		if(isNotEmpty(ewayBillDate)) {
			ewayBillDateStr = dateFormatOnlyDate.format(ewayBillDate);
		}
		individualInvoice.setTotaltaxableamount_str(totalTaxableAmtStr);
		individualInvoice.setTotaltax_str(totalTaxStr);
		individualInvoice.setTotalamount_str(totalAmtStr);
		individualInvoice.setDateofinvoice_str(dateOfInvoiceStr);
		if(isNotEmpty(ewayBillDateStr)) {
			individualInvoice.setEwayBillDate_str(ewayBillDateStr);
		}
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
	
	public List<AnxDocs> anxImpgDetails(InvoiceParent invoiceForAnx1,boolean isIntraState){
		AnxDocs anxdocs = new AnxDocs();
		anxdocs.setDocs(Lists.newArrayList());
		AnxDetails anxdetails = new AnxDetails();
		String stateTin="";
		if(NullUtil.isNotEmpty(invoiceForAnx1.getStatename())) {
			stateTin = getStateCode(invoiceForAnx1.getStatename());
		}
		String sez = ((PurchaseRegister) invoiceForAnx1).getImpGoods().get(0).getIsSez();
		if("Y".equalsIgnoreCase(sez)) {
			anxdocs.setCtin(((PurchaseRegister) invoiceForAnx1).getImpGoods().get(0).getStin());
			anxdetails.setPos(stateTin);
			anxdetails.setRfndelg("Y");
		}else {
			anxdocs.setPos(stateTin);
		}
		
		anxdetails.setDoctyp("B");
		AnxBoe anxboe = new AnxBoe();
		anxboe.setNum(invoiceForAnx1.getInvoiceno());
		anxboe.setDt(simpleDateFormat.format(invoiceForAnx1.getDateofinvoice()));
		anxboe.setVal(invoiceForAnx1.getTotalamount());
		anxboe.setPcode(((PurchaseRegister) invoiceForAnx1).getImpGoods().get(0).getPortCode());
		anxdetails.setBoe(anxboe);
		anxdetails.setItems(Lists.newArrayList());
				for (Item item : invoiceForAnx1.getItems()) {
					AnxItems anxItem = new AnxItems();
					anxItem.setIgst(item.getIgstamount());
					anxItem.setCess(item.getCessamount());
					String code = "";
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
					} else {
						code = item.getHsn();
					}
					anxItem.setHsn(code);
					anxItem.setRate(item.getRate());
					anxItem.setTxval(item.getTaxablevalue());
					anxdetails.getItems().add(anxItem);
				}
		
		anxdocs.getDocs().add(anxdetails);
		List<AnxDocs> listanxdocs = Lists.newArrayList();
		listanxdocs.add(anxdocs);
		return listanxdocs;
		
	}
	
	public List<AnxDetails> anxImpsDetails(InvoiceParent invoiceForAnx1,boolean isIntraState){
		AnxDetails anxdetails = new AnxDetails();
		String stateTin="";
		if(NullUtil.isNotEmpty(invoiceForAnx1.getStatename())) {
			stateTin = getStateCode(invoiceForAnx1.getStatename());
		}
			anxdetails.setPos(stateTin);
			anxdetails.setRfndelg("Y");
		
		anxdetails.setItems(Lists.newArrayList());
				for (Item item : invoiceForAnx1.getItems()) {
					AnxItems anxItem = new AnxItems();
					anxItem.setIgst(item.getIgstamount());
					anxItem.setCess(item.getCessamount());
					String code = "";
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
					} else {
						code = item.getHsn();
					}
					anxItem.setHsn(code);
					anxItem.setRate(item.getRate());
					anxItem.setTxval(item.getTaxablevalue());
					anxdetails.getItems().add(anxItem);
				}
		List<AnxDetails> listanxdetails = Lists.newArrayList();
		listanxdetails.add(anxdetails);
		return listanxdetails;
		
	}
	
	@Override
	@Transactional(readOnly = true)
	public AnxInvoiceParent getInvoice(final String invoiceId, final String returnType) {
		return anx1Repository.findOne(invoiceId);
	}
}
