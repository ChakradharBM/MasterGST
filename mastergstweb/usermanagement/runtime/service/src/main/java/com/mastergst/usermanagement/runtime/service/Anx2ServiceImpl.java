package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.domain.Base;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Anx2;
import com.mastergst.usermanagement.runtime.domain.Anx2Details;
import com.mastergst.usermanagement.runtime.domain.Anx2Docs;
import com.mastergst.usermanagement.runtime.domain.AnxDetails;
import com.mastergst.usermanagement.runtime.domain.AnxDoc;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.Anx2Repository;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.response.ANX2Response;
import com.mastergst.usermanagement.runtime.response.ANX2ResponseData;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;

@Service
@Transactional(readOnly = true)
public class Anx2ServiceImpl implements Anx2Service{
	private static final Logger logger = LogManager.getLogger(Anx2ServiceImpl.class.getName());
	private static final String CLASSNAME = "Anx2ServiceImpl::";
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private ConfigService configService;
	@Autowired
	UserService userService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	ClientUserMappingRepository clientUserMappingRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private Anx2Repository anx2Repository;
	
	@Override
	@Transactional
	public ANX2Response fetchANX2UploadStatus(final String userid, final String usertype, String clientid, String returntype,int month, int year, List<String> invoiceList) {
		final String method = "fetchANX2UploadStatus::";
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
		List<? extends InvoiceParent> invoices = Lists.newArrayList();
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
				gstInvoice = getANXReturnInvoice((List<? extends Anx2>) invoices, client, returntype, month, year);
				
				logger.debug(CLASSNAME + method + " gstInvoice {}", gstInvoice);
				logger.info(CLASSNAME + method + " gstInvoice {}", returntype);
				 if (returntype.equals(MasterGSTConstants.ANX2)) {
					Anx2 anx2 = ((Anx2) gstInvoice);
					anx2.setNil(null);
					anx2.setRtnprd(retPeriod);
					anx2.setFp(retPeriod);
				}
			}
		
		if (isNotEmpty(gstInvoice)) {
			try {
				ANX2Response saveResponse = iHubConsumerService.saveANX2Returns(gstInvoice, client.getStatename(),client.getGstname(), gstn, retPeriod, returntype, true);
				if (isNotEmpty(saveResponse.getStatuscd())	&& saveResponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
					String refId = saveResponse.getData().getRefId();
					ANX2Response gstnResponse = iHubConsumerService.newANX2ReturnStatus(refId, client.getStatename(),	client.getGstname(), gstn, retPeriod,returntype, true);
					int i = 0;
					while ((i < 5) && (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_IP))) {
						gstnResponse = iHubConsumerService.newANX2ReturnStatus(refId, client.getStatename(),
								client.getGstname(), gstn, retPeriod,returntype, false);
						i++;
					}
					List<String> invList = Lists.newArrayList();
					for (InvoiceParent invItem : invoices) {
						invList.add(invItem.getId().toString());
					}
					invoices = getSelectedInvoices(invList, returntype);
					if (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_PE)
							&& isNotEmpty(gstnResponse.getData().getErrorReport())) {
						AnxInvoiceSupport retInvoice = gstnResponse.getData().getErrorReport();
						saveANXErrors(invoices, retInvoice, returntype);
						
					} else if (isNotEmpty(gstnResponse) && isNotEmpty(gstnResponse.getData())
							&& isNotEmpty(gstnResponse.getData().getStatusCd())
							&& gstnResponse.getData().getStatusCd().equals(MasterGSTConstants.GST_RETURN_CODE_P)) {
						
							for (InvoiceParent invoice : invoices) {
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
						
							for (InvoiceParent invoice : invoices) {
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
				ANX2Response response = new ANX2Response();
				response.setStatuscd("0");
				response.setStatusdesc(e.getMessage());
				return response;
			}
		}
		ANX2Response emptyResponse = new ANX2Response();
		emptyResponse.setStatuscd("1");
		ANX2ResponseData responseData = new ANX2ResponseData();
		responseData.setStatusCd(MasterGSTConstants.GST_RETURN_CODE_P);
		emptyResponse.setData(responseData);
		return emptyResponse;
	}
	
	@Transactional
	public Anx2 getANXReturnInvoice(List<? extends Anx2> invoices, final Client client,
			final String returntype, final int iMonth, final int iYear) {
		final String method = "getANXReturnInvoice::";
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
		Anx2 invoice = new Anx2();
		
		
		invoice.setGstin(client.getGstnnumber());
		for (Anx2 invoiceParent : invoices) {
		if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			invoice = new Anx2();
			invoice.setGstin(client.getGstnnumber());
			if (isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(B2B)) {
				((Anx2) invoice).setAnx2b2b(Lists.newArrayList());
				
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((Anx2) invoiceParent).getAnx2b2b()) {
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
				
				((Anx2)invoice).setAnx2b2b(anx2DocsList);
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
				((Anx2) invoice).setDe(Lists.newArrayList());
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((Anx2) invoiceParent).getDe()) {
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
				
				((Anx2)invoice).setDe(anx2DocsList);
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
				((Anx2) invoice).setSezwp(Lists.newArrayList());
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((Anx2) invoiceParent).getSezwp()) {
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
				
				((Anx2)invoice).setSezwp(anx2DocsList);
			}else if(isNotEmpty(invoiceParent.getInvtype()) && invoiceParent.getInvtype().equals(MasterGSTConstants.SEZWOP)) {
				//((NewAnx1) invoice).setSezwop(((NewAnx1) invoiceParent).getSezwop());
				((Anx2) invoice).setSezwop(Lists.newArrayList());
				List<Anx2Docs> anx2DocsList = Lists.newArrayList();
				List<Anx2Details> anx2DetailsList= Lists.newArrayList();
				Anx2Docs anx2Docs = new Anx2Docs();
				AnxDoc anxDoc = new AnxDoc();
				for(Anx2Docs anx2docs : ((Anx2) invoiceParent).getSezwop()) {
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
				
				((Anx2)invoice).setSezwop(anx2DocsList);
		}
	}
}
		logger.info(CLASSNAME + method + " invoices {}", invoices);
		
		
		logger.debug(CLASSNAME + method + END);
		return invoice;
	}
	
	
	@Transactional
	public List<? extends InvoiceParent> getInvoices(Pageable pageable, final Client client, final String returnType,
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
			if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
			Page<? extends InvoiceParent> invoices = anx2Repository.findByClientidAndDateofinvoiceBetween(client.getId().toString(), stDate, endDate, pageable);
				List<InvoiceParent> filteredInvoices = Lists.newArrayList();
				if (isNotEmpty(invoices)) {
					for (InvoiceParent invoiceParent : invoices) {
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
	@Transactional(readOnly=true)
	public List<? extends InvoiceParent> getSelectedInvoices(final List<String> invoiceList, final String returnType) {
		logger.debug(CLASSNAME + "getSelectedInvoices : Begin");
		if (isNotEmpty(returnType)) {
			if(returnType.equalsIgnoreCase(MasterGSTConstants.ANX2)) {
				Iterable<? extends InvoiceParent> invIterable = null;
				invIterable = anx2Repository.findAll(invoiceList);
				if (isNotEmpty(invIterable)) {
					List<InvoiceParent> list=Lists.newArrayList();
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
	
	@Transactional
	public void saveInvoices(Iterable<? extends InvoiceParent> invoices, final String returnType) {
		logger.debug(CLASSNAME + "saveInvoices : Begin");
		if (returnType.equals(MasterGSTConstants.ANX2)) {
			//anx2Repository.save((Iterable<ANX2>) invoices);
		} 
	}
	
	@Transactional
	public void saveANXErrors(List<? extends InvoiceParent> invoices, final AnxInvoiceSupport retInvoice, final String returnType) {
		logger.debug(CLASSNAME + "saveANXErrors : Begin");
		if (isNotEmpty(retInvoice.getAnx2b2b())) {
			for (Anx2Docs gstItem : retInvoice.getAnx2b2b()) {
				if (isNotEmpty(gstItem.getErrorMsg())) {
					for (InvoiceParent invoice : invoices) {
						if (isNotEmpty(((Anx2) invoice).getAnx2b2b())) {
							for (Anx2Docs dbItem : ((Anx2) invoice).getAnx2b2b()) {
								if (isNotEmpty(dbItem.getCtin()) && isNotEmpty(gstItem.getCtin())
										&& dbItem.getCtin().equals(gstItem.getCtin())) {
									if (isNotEmpty(dbItem.getDocs())) {
										for (Anx2Details invoiceDetails : dbItem.getDocs()) {
											for (Anx2Details gstrInvoiceDetails : gstItem.getDocs()) {
												if (invoiceDetails.getDoc().getNum().equals(gstrInvoiceDetails.getDoc().getNum())) {
													// dbItem.setErrorMsg(gstItem.getErrorMsg());
													invoice.setGstStatus(gstItem.getErrorMsg());
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
		
		if(invoices.size() == 1) {
			if (isEmpty(invoices.get(0).getGstStatus())) {
				invoices.get(0).setGstStatus(MasterGSTConstants.GST_STATUS_ERROR);
			}
		} else {
		for (InvoiceParent invoice : invoices) {
			if (isEmpty(invoice.getGstStatus())) {
				invoice.setGstStatus(MasterGSTConstants.GST_STATUS_SUCCESS);
				}
			}
		}
		if (returnType.equals(MasterGSTConstants.ANX2)) {
			anx2Repository.save((List<Anx2>) invoices);
		} 
		logger.debug(CLASSNAME + "saveANXErrors : End");
	}
	
}
