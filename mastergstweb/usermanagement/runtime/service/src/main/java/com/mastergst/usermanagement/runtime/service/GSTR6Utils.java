package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.dao.Gstr6Dao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicAddress;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicAddressData;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6A;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6Details;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6DocDetails;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6ISD;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR6ARepository;
import com.mastergst.usermanagement.runtime.repository.GSTR6DetailsRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.support.Utility;

@Component
public class GSTR6Utils {
	@Autowired
	private Gstr6Dao gstr6Dao;
	@Autowired
	private IHubConsumerService ihubConsumerService;
	@Autowired
	private OtpExpiryService otpExpiryService;
	@Autowired
	private GSTR6ARepository gstr6aRepository;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private GSTR6DetailsRepository gstr6DetailsRepository;
	public Map getInvoices(final Client client, final String userid, final String returnType, int month,int year) throws MasterGSTException{
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		String strMonth =  month<10 ? "0"+month : month+"";
		String retPeriod = strMonth+year;
		List<String> invtypeLst = Lists.newArrayList();
		invtypeLst.add(MasterGSTConstants.B2B);
		invtypeLst.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invtypeLst.add(MasterGSTConstants.B2BA);
		invtypeLst.add(MasterGSTConstants.CDNA);
		List<String> itctypeLst = Lists.newArrayList();
		itctypeLst.add(MasterGSTConstants.ISD);
		itctypeLst.add(MasterGSTConstants.ISDCN);
		itctypeLst.add(MasterGSTConstants.ISDDN);
		itctypeLst.add(MasterGSTConstants.ISDA);
		List<String> amndtypeLst = Arrays.asList("latefee","ITC","b2ba","isda","cdna");
		Page<? extends InvoiceParent> invoices = gstr6Dao.getGSTR6ReturnInvoices(client.getId().toString(),userid,invtypeLst,month,Utility.getYearCode(month, year),0,0);
		Page<? extends InvoiceParent> isdinvoices = gstr6Dao.getGSTR6ReturnInvoices(client.getId().toString(),userid,itctypeLst,month,Utility.getYearCode(month, year),0,0);
		InvoiceParent invoice = null;
		for(String invtype : amndtypeLst) {
			invoice = ihubConsumerService.getGSTRXInvoices(client, client.getGstnnumber(), month,year, returnType, invtype, null, userid, true);
		}
		//LateFee latefee = ihubConsumerService.getGSTR6LateFeeDetails(client, month, year, userid, client.getGstnnumber(), retPeriod, true);
		if(NullUtil.isNotEmpty(invoice)) {
			if(isNotEmpty(((GSTR6)invoice).getTotalItc())){
				invoicesMap.put("totalITC",((GSTR6)invoice).getTotalItc());
			}
			if(isNotEmpty(((GSTR6)invoice).getElgItc())){
				invoicesMap.put("elgITC",((GSTR6)invoice).getElgItc());
			}
			if(isNotEmpty(((GSTR6)invoice).getInelgItc())){
				invoicesMap.put("inelgITC",((GSTR6)invoice).getInelgItc());
			}
			if(isNotEmpty(((GSTR6)invoice).getLatefee())){
				invoicesMap.put("latefee",((GSTR6)invoice).getLatefee());
			}
		}
		invoicesMap.put("invoices",invoices);
		invoicesMap.put("itcinvoices",isdinvoices);
		
		//invoicesMap.put("itcDetails",invoice);
		//invoicesMap.put("latefee",latefee);
		return invoicesMap;
	}
	public void performGSTR6Reconcile(Client client, String invType, String string, String clientid, String userid,int month, int year) {
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth + year;
		String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
		if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
			InvoiceParent gstr6a =null;
			try {
				gstr6a = ihubConsumerService.getGSTRXInvoices(client, client.getGstnnumber(), month, year,MasterGSTConstants.GSTR6A, invType, null, userid, true);
				if (isNotEmpty(gstr6a)) {
					System.out.println(gstr6a);
					List<GSTR6A> gstr6ainvs = gstr6aRepository.findByClientidAndFpAndIsAmendmentAndInvtype(clientid,retPeriod,true,invType);
					gstr6aRepository.delete(gstr6ainvs);
					gstr6a.setUserid(userid);
					gstr6a.setClientid(clientid);
					gstr6a.setAmendment(true);
					updateGSTR6AReturnData(gstr6a, invType, client.getGstnnumber(), clientid, retPeriod, month, year);
				}
			} catch (MasterGSTException e) {
				System.out.println(e.getMessage());
			}
			
		}
		
	}
	private void updateGSTR6AReturnData(InvoiceParent gstr6a, String invType, String gstnnumber, String clientid,String fp, int month, int year) {
		Client client = clientService.findById(clientid);
		List<GSTR6A> gstr6DwnldList = Lists.newArrayList();
		Pageable pageable = new PageRequest(0, 2, Sort.Direction.DESC, "createdDate");
		Map<String, ResponseData> gstnMap = Maps.newHashMap();
		if (invType.equals(B2B)|| invType.equals(MasterGSTConstants.B2BA)) {
			if (isNotEmpty(gstr6a.getB2b())) {
				List<GSTR6A> gstr6List = gstr6aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr6AList = Lists.newArrayList();
				List<String> gstr6Ids = Lists.newArrayList();
				for(GSTR6A g6 : gstr6List) {
					if(isNotEmpty(g6.getInvoiceno())) {
						gstr6Ids.add(g6.getInvoiceno());
						if(g6.isAmendment()) {
							String invNoCtin = g6.getInvoiceno();
							if(isNotEmpty(g6.getB2b())) {
								if(isNotEmpty(g6.getB2b().get(0).getCtin())) {
									String ctin = g6.getB2b().get(0).getCtin();
									invNoCtin = invNoCtin+ctin;
								}
								if(isNotEmpty(g6.getB2b().get(0).getInv().get(0).getIdt())) {
									String idt = g6.getB2b().get(0).getInv().get(0).getIdt();
									invNoCtin = invNoCtin+idt;
								}
							}
							gstr6AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRB2B gstrb2b : gstr6a.getB2b()) {
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
							 //&& gstnMap.containsKey(gstrb2b.getCtin())
							if(isNotEmpty(gstrb2b.getCtin())) {
								String dwnldctin = gstrb2b.getCtin();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnldctin;
							}
							if(isNotEmpty(gstrInvoiceDetails.getIdt())) {
								String dwnlddate = gstrInvoiceDetails.getIdt();
								dwnldInvNoCtin = dwnldInvNoCtin+dwnlddate;
							}
						
						if(isEmpty(gstr6AList) || !gstr6AList.contains(dwnldInvNoCtin)) {
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
							GSTR6A individualInvoice = populateGSTR6(gstrInvoiceDetails, gstnnumber, fp, gstr6a.getUserid(),
									gstr6a.getClientid(), invType, gstrb2b.getCtin(),gstrb2b.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							
							if(isNotEmpty(gstrb2b.getCtin()) && gstnMap.containsKey(gstrb2b.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrb2b.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr6a.isAmendment());
							gstr6DwnldList.add(individualInvoice);
						}
					}
				}
			}else if (isNotEmpty(((GSTR6A) gstr6a).getB2ba())) {
				List<GSTR6A> gstr6List = gstr6aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr6AList = Lists.newArrayList();
				List<String> gstr6Ids = Lists.newArrayList();
				for(GSTR6A g6 : gstr6List) {
					if(isNotEmpty(g6.getInvoiceno())) {
						gstr6Ids.add(g6.getInvoiceno());
						if(g6.isAmendment()) {
							String invNoCtin = g6.getInvoiceno();
							if(isNotEmpty(((GSTR6A) g6).getB2ba())) {
								if(isNotEmpty(((GSTR6A) g6).getB2ba().get(0).getCtin())) {
									String ctin = ((GSTR6A) g6).getB2ba().get(0).getCtin();
									invNoCtin = invNoCtin+ctin;
								}
								if(isNotEmpty(((GSTR6A) g6).getB2ba().get(0).getInv().get(0).getIdt())) {
									String idt = ((GSTR6A) g6).getB2ba().get(0).getInv().get(0).getIdt();
									invNoCtin = invNoCtin+idt;
								}
							}
							gstr6AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRB2B gstrb2ba : ((GSTR6A)gstr6a).getB2ba()) {
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
						
						if(isEmpty(gstr6AList) || !gstr6AList.contains(dwnldInvNoCtin)) {
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
							GSTR6A individualInvoice = populateGSTR6(gstrInvoiceDetails, gstnnumber, fp, gstr6a.getUserid(),
									gstr6a.getClientid(), invType, gstrb2ba.getCtin(),gstrb2ba.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							
							if(isNotEmpty(gstrb2ba.getCtin()) && gstnMap.containsKey(gstrb2ba.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrb2ba.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr6a.isAmendment());
							gstr6DwnldList.add(individualInvoice);
							//gstr2Repository.save(individualInvoice);
						}
					}
				}
			}
		}else if (invType.equals(CREDIT_DEBIT_NOTES) || invType.equals(MasterGSTConstants.CDNA)) {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			if (isNotEmpty(gstr6a.getCdn())) {
				List<GSTR6A> gstr6List = gstr6aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr6Ids = Lists.newArrayList();
				List<String> gstr6AList = Lists.newArrayList();
				for(GSTR6A g6 : gstr6List) {
					if(isNotEmpty(g6.getInvoiceno())) {
						gstr6Ids.add(g6.getInvoiceno());
						if(g6.isAmendment()) {
							String invNoCtin = g6.getInvoiceno();
						if(isNotEmpty(g6.getCdn())) {
							if(isNotEmpty(g6.getCdn().get(0).getCtin())) {
								String ctin = g6.getCdn().get(0).getCtin();
								invNoCtin = invNoCtin+ctin;
							}
							if(isNotEmpty(g6.getCdn().get(0).getNt().get(0).getNtDt())) {
								String idt = simpleDateFormat.format(g6.getCdn().get(0).getNt().get(0).getNtDt());
								invNoCtin = invNoCtin+idt;
							}
						}
							gstr6AList.add(invNoCtin);
						}
					}
				}
			
				for (GSTRCreditDebitNotes gstrcdn : gstr6a.getCdn()) {
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
						
						if(isEmpty(gstr6AList) || !gstr6AList.contains(dwnldInvNoCtin)) {
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
							GSTR6A individualInvoice = populateGSTR6(gstrInvoiceDetails, gstnnumber, fp, gstr6a.getUserid(),
									gstr6a.getClientid(), invType, gstrcdn.getCtin(),gstrcdn.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							if(isNotEmpty(gstrcdn.getCtin()) && gstnMap.containsKey(gstrcdn.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrcdn.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr6a.isAmendment());
							gstr6DwnldList.add(individualInvoice);
						}
					}
				}
			}else if (isNotEmpty(((GSTR6A)gstr6a).getCdna())) {
				List<GSTR6A> gstr6List = gstr6aRepository.findByClientidAndFpAndInvtype(clientid, fp, invType);
				List<String> gstr6Ids = Lists.newArrayList();
				List<String> gstr6AList = Lists.newArrayList();
				for(GSTR6A g6 : gstr6List) {
					if(isNotEmpty(g6.getInvoiceno())) {
						gstr6Ids.add(g6.getInvoiceno());
						if(g6.isAmendment()) {
							String invNoCtin = g6.getInvoiceno();
						if(isNotEmpty(((GSTR6A)g6).getCdna())) {
							if(isNotEmpty(((GSTR6A)g6).getCdn().get(0).getCtin())) {
								String ctin = ((GSTR6A)g6).getCdn().get(0).getCtin();
								invNoCtin = invNoCtin+ctin;
							}
							if(isNotEmpty(((GSTR6A)g6).getCdn().get(0).getNt().get(0).getNtDt())) {
								String idt = simpleDateFormat.format(((GSTR6A)g6).getCdn().get(0).getNt().get(0).getNtDt());
								invNoCtin = invNoCtin+idt;
							}
						}
							gstr6AList.add(invNoCtin);
						}
					}
				}
				//gstr2Repository.delete(gstr2AList);
				for (GSTRCreditDebitNotes gstrcdna : ((GSTR6A)gstr6a).getCdna()) {
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
						
						if(isEmpty(gstr6List) || !gstr6AList.contains(dwnldInvNoCtin)) {
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
							GSTR6A individualInvoice = populateGSTR6(gstrInvoiceDetails, gstnnumber, fp, gstr6a.getUserid(),
									gstr6a.getClientid(), invType, gstrcdna.getCtin(),gstrcdna.getCfs());
							
							if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
								individualInvoice.setStatename(client.getStatename());
							}
							if(isNotEmpty(gstrcdna.getCtin()) && gstnMap.containsKey(gstrcdna.getCtin())) {
								individualInvoice.setBilledtoname(gstnMap.get(gstrcdna.getCtin()).getTradeNam());
							}
							individualInvoice.setAmendment(gstr6a.isAmendment());
							gstr6DwnldList.add(individualInvoice);
						}
					}
				}
			}
		}
		gstr6aRepository.save(gstr6DwnldList);
	}
	private GSTR6A populateGSTR6(GSTRInvoiceDetails gstrInvoiceDetails, final String gstn, final String fp,
			final String userid, final String clientid, final String invType, final String ctin,final String cfs) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		GSTR6A individualInvoice = new GSTR6A();
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
		individualInvoice = (GSTR6A) clientService.extrafields(individualInvoice,"GSTR6A");
		return individualInvoice;
	}
	public GSTR6Details saveGSTR6Invoices(String id,Client client,int month,int year,Page<? extends InvoiceParent> invoices,Page<? extends InvoiceParent> isdinvoices) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth + year;
		gstr6DetailsRepository.deleteByClientidAndFp(client.getId().toString(),retPeriod);
		GSTR6Details details = new GSTR6Details();
		details.setUserid(id);
		details.setClientid(client.getId().toString());
		details.setFp(retPeriod);
		details.setGstin(client.getGstnnumber());
		List<? extends InvoiceParent> invoiceslist= null;
		if(isNotEmpty(invoices)) {
			invoiceslist = invoices.getContent();
		}
		List<? extends InvoiceParent> isdinvoiceslist= null;
		if(isNotEmpty(isdinvoices)) {
			isdinvoiceslist = isdinvoices.getContent();
		} 
		for(InvoiceParent invoiceParent : invoiceslist) {
			if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
				for (GSTRB2B gstrb2bInvoice : invoiceParent.getB2b()) {
					String ctin = gstrb2bInvoice.getCtin();
					boolean present = false;
					for (GSTRB2B eInvs : details.getB2b()) {
						if (isNotEmpty(gstrb2bInvoice.getCtin()) && isNotEmpty(gstrb2bInvoice.getInv()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getInv().addAll(gstrb2bInvoice.getInv());
						}
					}
					if (!present) {
						details.getB2b().add(gstrb2bInvoice);
					}
					for (GSTRB2B eInvs : details.getB2b()) {
						for(GSTRInvoiceDetails invoiceDetails : eInvs.getInv()) {
							clientService.consolidatedItemRate(invoiceDetails);
						}
					}
				}
			}else if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
				for (GSTRB2B gstrb2bInvoice : ((GSTR6) invoiceParent).getB2ba()) {
					String ctin = gstrb2bInvoice.getCtin();
					boolean present = false;
					for (GSTRB2B eInvs : details.getB2ba()) {
						if (isNotEmpty(gstrb2bInvoice.getCtin()) && isNotEmpty(gstrb2bInvoice.getInv()) && eInvs.getCtin().equals(ctin)) {
							present = true;
							eInvs.getInv().addAll(gstrb2bInvoice.getInv());
						}
					}
					if (!present) {
						details.getB2ba().add(gstrb2bInvoice);
					}
					for (GSTRB2B eInvs : details.getB2ba()) {
						for(GSTRInvoiceDetails invoiceDetails : eInvs.getInv()) {
							clientService.consolidatedItemRate(invoiceDetails);
						}
					}
				}
			}else if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				List<GSTRCreditDebitNotes> notes = null;
				List<GSTRCreditDebitNotes> eNotes = null;
					notes = invoiceParent.getCdn();
					eNotes = details.getCdn();
				if (isNotEmpty(notes)) {
					if(isNotEmpty(invoiceParent.getB2b())) {
						invoiceParent.setB2b(Lists.newArrayList());
					}
					for (GSTRCreditDebitNotes gstrcdnrInvoice : notes) {
						String ctin = gstrcdnrInvoice.getCtin();
						boolean present = false;
						if (isNotEmpty(eNotes)) {
							for (GSTRCreditDebitNotes eInvs : eNotes) {
								if (isNotEmpty(ctin) && isNotEmpty(gstrcdnrInvoice.getNt())
										&& isNotEmpty(eInvs.getCtin()) && eInvs.getCtin().equals(ctin)) {
									present = true;
									eInvs.getNt().addAll(gstrcdnrInvoice.getNt());
								}
							}
						}
						if (!present) {
							details.getCdn().add(gstrcdnrInvoice);
						}
					}
				}
			}else if(invoiceParent.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
				List<GSTRCreditDebitNotes> notes = null;
				List<GSTRCreditDebitNotes> eNotes = null;
					notes = ((GSTR6) invoiceParent).getCdna();
					eNotes = details.getCdna();
				if (isNotEmpty(notes)) {
					if(isNotEmpty(invoiceParent.getB2b())) {
						invoiceParent.setB2b(Lists.newArrayList());
					}
					for (GSTRCreditDebitNotes gstrcdnrInvoice : notes) {
						String ctin = gstrcdnrInvoice.getCtin();
						boolean present = false;
						if (isNotEmpty(eNotes)) {
							for (GSTRCreditDebitNotes eInvs : eNotes) {
								if (isNotEmpty(ctin) && isNotEmpty(gstrcdnrInvoice.getNt())
										&& isNotEmpty(eInvs.getCtin()) && eInvs.getCtin().equals(ctin)) {
									present = true;
									eInvs.getNt().addAll(gstrcdnrInvoice.getNt());
								}
							}
						}
						if (!present) {
							details.getCdna().add(gstrcdnrInvoice);
						}
					}
				}
			} 
		}
		for(InvoiceParent isdInvoices : isdinvoiceslist) {
			if(isdInvoices.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
				for (GSTR6ISD gstrisdInvoice : ((GSTR6) isdInvoices).getIsd()) {
					String ctin = gstrisdInvoice.getElglst().getCpty();
					boolean present = false;
					for (GSTR6ISD eInvs : details.getIsd()) {
						if (isNotEmpty(gstrisdInvoice.getElglst().getCpty()) && isNotEmpty(gstrisdInvoice.getElglst()) && eInvs.getElglst().getCpty().equals(ctin)) {
							present = true;
							eInvs.getElglst().getDoclst().addAll(gstrisdInvoice.getElglst().getDoclst());
							if(isNotEmpty(eInvs.getInelglst()) && isNotEmpty(eInvs.getInelglst().getDoclst())) {
								eInvs.getInelglst().getDoclst().addAll(gstrisdInvoice.getInelglst().getDoclst());
							}
						}
					}
					if (!present) {
						details.getIsd().add(gstrisdInvoice);
					}
				}
			}else if(isdInvoices.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDA)) {
				for (GSTR6ISD gstrisdInvoice : ((GSTR6) isdInvoices).getIsda()) {
					String ctin = gstrisdInvoice.getElglst().getCpty();
					String ictin = gstrisdInvoice.getInelglst().getCpty();
					boolean present = false;
					for (GSTR6ISD eInvs : details.getIsd()) {
						if (isNotEmpty(gstrisdInvoice.getElglst().getCpty()) && isNotEmpty(gstrisdInvoice.getElglst()) && eInvs.getElglst().getCpty().equals(ctin)) {
							present = true;
							eInvs.getElglst().getDoclst().addAll(gstrisdInvoice.getElglst().getDoclst());
							if(isNotEmpty(eInvs.getInelglst()) && isNotEmpty(eInvs.getInelglst().getDoclst())) {
								eInvs.getInelglst().getDoclst().addAll(gstrisdInvoice.getInelglst().getDoclst());
							}
						}
					}
					if (!present) {
						details.getIsda().add(gstrisdInvoice);
					}
				}
			}	
		}
		gstr6DetailsRepository.save(details);
		return details;
	}
	
	public List<GSTR6DocDetails> getGstr6IsdEligibleISDItems(InvoiceParent oldinvoice,InvoiceParent invoice) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String isddocno = oldinvoice.getB2b().get(0).getInv().get(0).getInum();
		List<GSTR6DocDetails> doclist = Lists.newArrayList();
		//for (Item item : invoice.getItems()) {
			GSTR6DocDetails isditems = new GSTR6DocDetails();
			if(isNotEmpty(isddocno)) {
				isditems.setRdocnum(isddocno);
			}
			isditems.setRdocdt(dateFormat.format(invoice.getDateofinvoice()));
			if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum())) {
				isditems.setRcrdnum(invoice.getCdn().get(0).getNt().get(0).getNtNum());
			}else {
				isditems.setRcrdnum("");
			}
			if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtDt())) {
				isditems.setRcrddt(dateFormat.format(invoice.getCdn().get(0).getNt().get(0).getNtDt()));
			}else {
				isditems.setRcrddt("");
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocdt())) {
				isditems.setOdocdt(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocdt());	
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocnum())) {
				isditems.setOdocnum(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocnum());	
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocdt())) {
				isditems.setOdocdt(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getDocdt());	
			}
			if(oldinvoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDCN) || oldinvoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDDN)) {
				if(isNotEmpty(oldinvoice.getB2b().get(0).getCtin())) {
					isditems.setIsdDocty("ISDCN");
				}else {
					isditems.setIsdDocty("ISDCNUR");
				}
			}else {
				if(isNotEmpty(oldinvoice.getB2b().get(0).getCtin())) {
					isditems.setIsdDocty("ISD");
				}else {
					isditems.setIsdDocty("ISDUR");
				}
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd()) && isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getCrdnum())) {
				isditems.setOcrdnum(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getCrdnum());
			}else {
				isditems.setOcrdnum("");
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd()) && isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getCrddt())) {
				isditems.setOcrddt(((GSTR6) oldinvoice).getIsd().get(0).getElglst().getDoclst().get(0).getCrddt());
			}else {
				isditems.setOcrddt("");
			}
			if (isNotEmpty(oldinvoice.getItems().get(0).getSgstamount())) {
				isditems.setIamti(oldinvoice.getItems().get(0).getSgstamount());
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getSgstamount())) {
				isditems.setIamts(oldinvoice.getItems().get(1).getSgstamount());
			}else {
				isditems.setIamts(0d);
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getCgstamount())) {
				isditems.setIamtc(oldinvoice.getItems().get(1).getCgstamount());
			}else {
				isditems.setIamtc(0d);
			}
			
			if (isNotEmpty(oldinvoice.getItems().get(0).getCgstamount())) {
				isditems.setCamtc(oldinvoice.getItems().get(0).getCgstamount());
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getIgstamount())) {
				isditems.setCamti(oldinvoice.getItems().get(1).getIgstamount());
				isditems.setSamti(oldinvoice.getItems().get(1).getIgstamount());
			}else {
				isditems.setCamti(0d);
				isditems.setSamti(0d);
			}
			
			if (isNotEmpty(oldinvoice.getItems().get(0).getSgstamount())) {
				isditems.setSamts(oldinvoice.getItems().get(0).getSgstamount());
			}
		
			if (isNotEmpty(oldinvoice.getItems().get(0).getCessamount())) {
				isditems.setCsamt(oldinvoice.getItems().get(0).getIsdcessamount());
			}
			
			doclist.add(isditems);
		//}
		return doclist;
	}
	public List<GSTR6DocDetails> getGstr6IsdInEligibleISDItems(InvoiceParent oldinvoice,InvoiceParent invoice) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		String isddocno = oldinvoice.getB2b().get(0).getInv().get(0).getInum();
		List<GSTR6DocDetails> doclist = Lists.newArrayList();
		//for (Item item : invoice.getItems()) {
			GSTR6DocDetails isditems = new GSTR6DocDetails();
			if(isNotEmpty(isddocno)) {
				isditems.setRdocnum(isddocno);
			}
			isditems.setRdocdt(dateFormat.format(invoice.getDateofinvoice()));
			if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum())) {
				isditems.setRcrdnum(invoice.getCdn().get(0).getNt().get(0).getNtNum());
			}else {
				isditems.setRcrdnum("");
			}
			if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtDt())) {
				isditems.setRcrddt(dateFormat.format(invoice.getCdn().get(0).getNt().get(0).getNtDt()));
			}else {
				isditems.setRcrddt("");
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getDocnum())) {
				isditems.setOdocnum(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getDocnum());	
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getDocdt())) {
				isditems.setOdocdt(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getDocdt());	
			}
			if(oldinvoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDCN) || oldinvoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDDN)) {
				if(isNotEmpty(oldinvoice.getB2b().get(0).getCtin())) {
					isditems.setIsdDocty("ISDCN");
				}else {
					isditems.setIsdDocty("ISDCNUR");
				}
			}else {
				if(isNotEmpty(oldinvoice.getB2b().get(0).getCtin())) {
					isditems.setIsdDocty("ISD");
				}else {
					isditems.setIsdDocty("ISDUR");
				}
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd()) && isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getCrdnum())) {
				isditems.setOcrdnum(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getCrdnum());
			}else {
				isditems.setOcrdnum("");
			}
			if(isNotEmpty(((GSTR6) oldinvoice).getIsd()) && isNotEmpty(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getCrddt())) {
				isditems.setOcrddt(((GSTR6) oldinvoice).getIsd().get(0).getInelglst().getDoclst().get(0).getCrddt());
			}else {
				isditems.setOcrddt("");
			}
			if (isNotEmpty(oldinvoice.getItems().get(0).getSgstamount())) {
				isditems.setIamti(oldinvoice.getItems().get(0).getSgstamount());
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getSgstamount())) {
				isditems.setIamts(oldinvoice.getItems().get(1).getSgstamount());
			}else {
				isditems.setIamts(0d);
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getCgstamount())) {
				isditems.setIamtc(oldinvoice.getItems().get(1).getCgstamount());
			}else {
				isditems.setIamtc(0d);
			}
			
			if (isNotEmpty(oldinvoice.getItems().get(0).getCgstamount())) {
				isditems.setCamtc(oldinvoice.getItems().get(0).getCgstamount());
			}
			if(isNotEmpty(oldinvoice.getItems().get(1).getIgstamount())) {
				isditems.setCamti(oldinvoice.getItems().get(1).getIgstamount());
				isditems.setSamti(oldinvoice.getItems().get(1).getIgstamount());
			}else {
				isditems.setCamti(0d);
				isditems.setSamti(0d);
			}
			
			if (isNotEmpty(oldinvoice.getItems().get(0).getSgstamount())) {
				isditems.setSamts(oldinvoice.getItems().get(0).getSgstamount());
			}
		
			if (isNotEmpty(oldinvoice.getItems().get(0).getCessamount())) {
				isditems.setCsamt(oldinvoice.getItems().get(0).getIsdcessamount());
			}
			
			doclist.add(isditems);
		//}
		return doclist;
	}
	public GSTR6Details getGSTR6Invoice(String clientid, String retPeriod) {
		GSTR6Details details = gstr6DetailsRepository.findByClientidAndFp(clientid,retPeriod);
		return details;
	}
	public void deleteGSTR6Data(String clientid, String retPeriod) {
		GSTR6Details details = gstr6DetailsRepository.findByClientidAndFp(clientid,retPeriod);
		if(isNotEmpty(details)) {
			gstr6DetailsRepository.delete(details);			
		}
	}
	public void autoCalculateData(GSTR6Details details, Client client, int month,int year) {
		List<String> isdTypeList = Arrays.asList("ISD","ISDCN","ISDDN");
		List<? extends InvoiceParent> b2binvoices = gstr6Dao.getGSTR6B2BInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),"B2B").getContent();
		List<? extends InvoiceParent> b2bainvoices = gstr6Dao.getGSTR6B2BInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),"B2BA").getContent();
		List<? extends InvoiceParent> cdninvoices = gstr6Dao.getGSTR6B2BInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),"Credit/Debit Notes").getContent();
		List<? extends InvoiceParent> cdnainvoices = gstr6Dao.getGSTR6B2BInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),"CDNA").getContent();
		List<? extends InvoiceParent> isdinvoices = gstr6Dao.getGSTR6ISDInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),isdTypeList).getContent();
		List<? extends InvoiceParent> isdainvoices = gstr6Dao.getGSTR6B2BInvoices(client.getId().toString(), month, Utility.getYearCode(4, year),"ISDA").getContent();
		if(isNotEmpty(b2binvoices)) {
			for(InvoiceParent invoice : b2binvoices) {
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
					for (GSTRB2B gstrb2bInvoice : invoice.getB2b()) {
						String ctin = gstrb2bInvoice.getCtin();
						boolean present = false;
						for (GSTRB2B eInvs : details.getB2b()) {
							if (isNotEmpty(gstrb2bInvoice.getCtin()) && isNotEmpty(gstrb2bInvoice.getInv()) && eInvs.getCtin().equals(ctin)) {
								present = true;
								eInvs.getInv().addAll(gstrb2bInvoice.getInv());
							}
						}
						if (!present) {
							details.getB2b().add(gstrb2bInvoice);
						}
						for (GSTRB2B eInvs : details.getB2b()) {
							for(GSTRInvoiceDetails invoiceDetails : eInvs.getInv()) {
								clientService.consolidatedItemRate(invoiceDetails);
							}
						}
					}
				}
			}
		}
		if(isNotEmpty(b2bainvoices)) {
			for(InvoiceParent invoice : b2bainvoices) {
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
					for (GSTRB2B gstrb2bInvoice : ((GSTR6) invoice).getB2ba()) {
						String ctin = gstrb2bInvoice.getCtin();
						boolean present = false;
						for (GSTRB2B eInvs : details.getB2ba()) {
							if (isNotEmpty(gstrb2bInvoice.getCtin()) && isNotEmpty(gstrb2bInvoice.getInv()) && eInvs.getCtin().equals(ctin)) {
								present = true;
								eInvs.getInv().addAll(gstrb2bInvoice.getInv());
							}
						}
						if (!present) {
							details.getB2ba().add(gstrb2bInvoice);
						}
						for (GSTRB2B eInvs : details.getB2ba()) {
							for(GSTRInvoiceDetails invoiceDetails : eInvs.getInv()) {
								clientService.consolidatedItemRate(invoiceDetails);
							}
						}
					}
				}
		   }
		}
		if(isNotEmpty(cdninvoices)) {
			for(InvoiceParent invoice : cdninvoices) {
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					for (GSTRCreditDebitNotes gstrcdnInvoice : invoice.getCdn()) {
						String ctin = gstrcdnInvoice.getCtin();
						boolean present = false;
						for (GSTRCreditDebitNotes eInvs : details.getCdn()) {
							if (isNotEmpty(gstrcdnInvoice.getCtin()) && isNotEmpty(gstrcdnInvoice.getNt()) && eInvs.getCtin().equals(ctin)) {
								present = true;
								eInvs.getNt().addAll(gstrcdnInvoice.getNt());
							}
						}
						if (!present) {
							details.getCdn().add(gstrcdnInvoice);
						}
						for (GSTRCreditDebitNotes eInvs : details.getCdn()) {
							for(GSTRInvoiceDetails invoiceDetails : eInvs.getNt()) {
								clientService.consolidatedItemRate(invoiceDetails);
							}
						}
					}
				}
		   }
		}
		if(isNotEmpty(cdnainvoices)) {
			for(InvoiceParent invoice : cdnainvoices) {
				if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)) {
					for (GSTRCreditDebitNotes gstrcdnInvoice : ((GSTR6) invoice).getCdna()) {
						String ctin = gstrcdnInvoice.getCtin();
						boolean present = false;
						for (GSTRCreditDebitNotes eInvs : details.getCdna()) {
							if (isNotEmpty(gstrcdnInvoice.getCtin()) && isNotEmpty(gstrcdnInvoice.getNt()) && eInvs.getCtin().equals(ctin)) {
								present = true;
								eInvs.getNt().addAll(gstrcdnInvoice.getNt());
							}
						}
						if (!present) {
							details.getCdna().add(gstrcdnInvoice);
						}
						for (GSTRCreditDebitNotes eInvs : details.getCdna()) {
							for(GSTRInvoiceDetails invoiceDetails : eInvs.getNt()) {
								clientService.consolidatedItemRate(invoiceDetails);
							}
						}
					}
				}
		   }
		}
		if(isNotEmpty(isdinvoices)) {
			for(InvoiceParent invoice : isdinvoices) {
				for (GSTR6ISD gstrisdInvoice : ((GSTR6) invoice).getIsd()) {
					String ctin = gstrisdInvoice.getElglst().getCpty();
					boolean present = false;
					for (GSTR6ISD eInvs : details.getIsd()) {
						if (isNotEmpty(gstrisdInvoice.getElglst().getCpty()) && isNotEmpty(gstrisdInvoice.getElglst()) && eInvs.getElglst().getCpty().equals(ctin)) {
							present = true;
							eInvs.getElglst().getDoclst().addAll(gstrisdInvoice.getElglst().getDoclst());
							if(isNotEmpty(eInvs.getInelglst()) && isNotEmpty(eInvs.getInelglst().getDoclst())) {
								eInvs.getInelglst().getDoclst().addAll(gstrisdInvoice.getInelglst().getDoclst());
							}
						}
					}
					if (!present) {
						details.getIsd().add(gstrisdInvoice);
					}
				}
			}
		}
		if(isNotEmpty(isdainvoices)) {
			for(InvoiceParent invoice : isdainvoices) {
				for (GSTR6ISD gstrisdInvoice : ((GSTR6) invoice).getIsda()) {
					String ctin = gstrisdInvoice.getElglst().getCpty();
					boolean present = false;
					for (GSTR6ISD eInvs : details.getIsda()) {
						if (isNotEmpty(gstrisdInvoice.getElglst().getCpty()) && isNotEmpty(gstrisdInvoice.getElglst()) && eInvs.getElglst().getCpty().equals(ctin)) {
							present = true;
							eInvs.getElglst().getDoclst().addAll(gstrisdInvoice.getElglst().getDoclst());
							if(isNotEmpty(eInvs.getInelglst()) && isNotEmpty(eInvs.getInelglst().getDoclst())) {
								eInvs.getInelglst().getDoclst().addAll(gstrisdInvoice.getInelglst().getDoclst());
							}
						}
					}
					if (!present) {
						details.getIsda().add(gstrisdInvoice);
					}
				}
			}
		}
		gstr6DetailsRepository.save(details);
	}
	public InvoiceParent syncGstr6(User user, Client client, String retPeriod, int month, int year) {
		InvoiceParent invoice = null;
		List<String> typeLst = Arrays.asList("latefee","ITC");
		for(String invtype : typeLst) {
			try {
				invoice = ihubConsumerService.getGSTRXInvoices(client, client.getGstnnumber(), month,year, GSTR6, invtype, null, user.getId().toString(), true);
				
			} catch (MasterGSTException e) {
				e.printStackTrace();
			}
		}
		return invoice;
		
	}
	
}
