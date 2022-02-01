package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.dao.ReconsilationDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientConfig;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR2BSupport;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BSupportRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;
import com.mastergst.usermanagement.runtime.response.MisMatchVO;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.ReconsilationService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class ReconsilationController {
	
	private static final Logger logger = LogManager.getLogger(ReconsilationController.class.getName());
	private static final String CLASSNAME = "ReconsilationController::";
	
	@Autowired	private ReconsilationService reconsilationService;
	@Autowired	private GSTR2BSupportRepository gstr2bSupportRepository;
	@Autowired	private PurchaseRegisterRepository purchaseRepository;
	@Autowired	private ReconsilationDao reconsilationDao;
	@Autowired	private ClientService clientService;
	@Autowired	private UserService userService;
	@Autowired	private ProfileService profileService;
	@Autowired	private SubscriptionService subscriptionService;
	@Autowired	private UserRepository userRepository;
	@Autowired	private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired	private ImportMapperService importMapperService;
	@Autowired	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired	ReconcileTempRepository reconcileTempRepository;
	
	@RequestMapping(value = "/getg2breconsilationInvDetails/{docid}/{clientid}/{status}", method = RequestMethod.GET)
	public @ResponseBody Map<String, InvoiceParent> getG2breconsilationInvDetails(@PathVariable(name = "docid") String docid,
			@PathVariable(name = "clientid") String clientid, @PathVariable(name = "status") String status, @RequestParam(required = false) String fp) {
		Map<String, InvoiceParent> map = Maps.newHashMap();
		InvoiceParent invparent = null;
		if(status.equalsIgnoreCase("Not In GSTR2B")) {
			invparent = purchaseRepository.findOne(docid);
			map.put("PurchaseRegisterinvoice",invparent);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods()) && isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((PurchaseRegister)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv.add(gstrinv);
					gstrb2b.setInv(inv);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
		}else if(status.equalsIgnoreCase("Not In Purchases")) {
			invparent = gstr2bSupportRepository.findOne(docid);
			map.put("gstr2binvoice", invparent);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((GSTR2BSupport)invparent).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((GSTR2BSupport)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((GSTR2BSupport)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv1 = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv1.add(gstrinv);
					gstrb2b.setInv(inv1);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
		}else {
			invparent=purchaseRepository.findOne(docid);
			if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods()) && isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0))) {
					GSTRB2B gstrb2b = new GSTRB2B();
					if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0).getStin())) {
						gstrb2b.setCtin(((PurchaseRegister)invparent).getImpGoods().get(0).getStin());
					}else {
						gstrb2b.setCtin(" ");
					}
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
					inv.add(gstrinv);
					gstrb2b.setInv(inv);
					List<GSTRB2B> b2b = Lists.newArrayList();
					b2b.add(gstrb2b);
					invparent.setB2b(b2b);
				}
			}
			map.put("PurchaseRegisterinvoice",invparent);
			if(NullUtil.isEmpty(invparent)) {
				invparent = gstr2bSupportRepository.findOne(docid);
				if(isNotEmpty(invparent) && invparent.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					if(isNotEmpty(((GSTR2BSupport)invparent).getImpGoods()) && isNotEmpty(((GSTR2BSupport)invparent).getImpGoods().get(0))) {
						GSTRB2B gstrb2b = new GSTRB2B();
						if(isNotEmpty(((GSTR2BSupport)invparent).getImpGoods().get(0).getStin())) {
							gstrb2b.setCtin(((GSTR2BSupport)invparent).getImpGoods().get(0).getStin());
						}else {
							gstrb2b.setCtin(" ");
						}
						List<GSTRInvoiceDetails> inv1 = Lists.newArrayList();
						GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
						inv1.add(gstrinv);
						gstrb2b.setInv(inv1);
						List<GSTRB2B> b2b = Lists.newArrayList();
						b2b.add(gstrb2b);
						invparent.setB2b(b2b);
					}
				}
				
				if(isNotEmpty(invparent) && isNotEmpty(invparent.getGstr2bMatchingId())) {
					InvoiceParent inv = invparent = purchaseRepository.findOne(invparent.getGstr2bMatchingId().get(0));
					if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods()) && isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0))) {
						GSTRB2B gstrb2b = new GSTRB2B();
						if(isNotEmpty(((PurchaseRegister)invparent).getImpGoods().get(0).getStin())) {
							gstrb2b.setCtin(((PurchaseRegister)invparent).getImpGoods().get(0).getStin());
						}else {
							gstrb2b.setCtin(" ");
						}
						List<GSTRInvoiceDetails> inv1 = Lists.newArrayList();
						GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
						inv1.add(gstrinv);
						gstrb2b.setInv(inv1);
						List<GSTRB2B> b2b = Lists.newArrayList();
						b2b.add(gstrb2b);
						invparent.setB2b(b2b);
					}
					map.put("gstr2binvoice",inv);
				}
			}else {
				if(isNotEmpty(invparent) && isNotEmpty(invparent.getGstr2bMatchingId())) {
					InvoiceParent inv = invparent = gstr2bSupportRepository.findOne(invparent.getGstr2bMatchingId().get(0));
					if(isNotEmpty(inv) && inv.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(((GSTR2BSupport)inv).getImpGoods()) && isNotEmpty(((GSTR2BSupport)inv).getImpGoods().get(0))) {
							GSTRB2B gstrb2b = new GSTRB2B();
							if(isNotEmpty(((GSTR2BSupport)inv).getImpGoods().get(0).getStin())) {
								gstrb2b.setCtin(((GSTR2BSupport)inv).getImpGoods().get(0).getStin());
							}else {
								gstrb2b.setCtin(" ");
							}
							List<GSTRInvoiceDetails> inv1 = Lists.newArrayList();
							GSTRInvoiceDetails gstrinv = new GSTRInvoiceDetails();
							inv1.add(gstrinv);
							gstrb2b.setInv(inv1);
							List<GSTRB2B> b2b = Lists.newArrayList();
							b2b.add(gstrb2b);
							inv.setB2b(b2b);
						}
					}
					map.put("gstr2binvoice",inv);
				}
			}
		}
			
		return map;
	}
	
	@RequestMapping(value = "/reconsileg2binvs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String,String> getReconsileInvoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		String yearCode = Utility.getYearCode(month, year);
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean billdate = false;
		if(isNotEmpty(otherconfig)){
			billdate = otherconfig.isEnableTransDate();
		}
		//Matching id's is null & Matching status is null
		Page<? extends InvoiceParent> prInvoices = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, null, null,yearCode,billdate, true);
		/*MisMatched
		Page<? extends InvoiceParent> prMisMatched = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, null, MasterGSTConstants.GST_STATUS_MISMATCHED);
		*/
		//Manual Matching status
		Page<? extends InvoiceParent> prManualMatched = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, null, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED,yearCode,billdate, true);
		//Matching id's is not null & Matching status is not null
		Page<? extends InvoiceParent> prMatched = reconsilationService.getPurchaseInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MATCHED, MasterGSTConstants.GST_STATUS_MATCHED,yearCode,billdate, true);
		
				
		//Matching id's is null & Matching status is null
		Page<? extends InvoiceParent> g2bInvoice = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, null, null, true);
		/*MisMatched
		Page<? extends InvoiceParent> g2bMisMatch = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, null, MasterGSTConstants.GST_STATUS_MISMATCHED);
		*/
		//Manual Matching status
		Page<? extends InvoiceParent> g2bManualMatch = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, null, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED, true);
		//Matching id's is not null & Matching status is not null
		Page<? extends InvoiceParent> g2bMatch = reconsilationService.getGstr2BSupportInvoices(clientid, 0, -1, month, year, MasterGSTConstants.GST_STATUS_MATCHED, MasterGSTConstants.GST_STATUS_MATCHED, true);
		List<InvoiceParent> g2b = Lists.newArrayList();
		List<InvoiceParent> g2bf = Lists.newArrayList();
		if(isNotEmpty(g2bMatch)) {
			g2b = (List<InvoiceParent>) g2bMatch.getContent();
		}
		if(isNotEmpty(g2b) && g2b.size() > 0) {
			g2bf.addAll(g2b);
		}
		List<InvoiceParent> prb = Lists.newArrayList();
		List<InvoiceParent> prbf = Lists.newArrayList();
		if(isNotEmpty(prMatched)) {
			prb = (List<InvoiceParent>) prMatched.getContent();
		}
		if(isNotEmpty(prb) && prb.size() > 0) {
			prbf.addAll(prb);
		}
		Map<String, List<String>> idsMap = findReconsilationids(prMatched,g2bMatch);
		if(isNotEmpty(idsMap)) {
			if(isNotEmpty(idsMap.get("2bIds"))) {
				List<InvoiceParent> gstr2binvoices =  Lists.newArrayList(gstr2bSupportRepository.findAll(idsMap.get("2bIds")));
				if(isNotEmpty(gstr2binvoices) && gstr2binvoices.size() > 0) {
					gstr2binvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					g2bf.addAll(gstr2binvoices);
				}
			}
			if(isNotEmpty(idsMap.get("prIds"))) {
				List<InvoiceParent> prinvoices =  Lists.newArrayList(purchaseRepository.findAll(idsMap.get("prIds")));
				if(isNotEmpty(prinvoices) && prinvoices.size() > 0) {
					prinvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					prbf.addAll(prinvoices);
				}
			}
		}
		
		List<InvoiceParent> mg2b = Lists.newArrayList();
		List<InvoiceParent> mg2bf = Lists.newArrayList();
		if(isNotEmpty(g2bManualMatch)) {
			mg2b = (List<InvoiceParent>) g2bManualMatch.getContent();
		}
		if(isNotEmpty(mg2b) && mg2b.size() > 0) {
			mg2bf.addAll(mg2b);
		}
		List<InvoiceParent> mprb = Lists.newArrayList();
		List<InvoiceParent> mprbf = Lists.newArrayList();
		if(isNotEmpty(prManualMatched)) {
			mprb = (List<InvoiceParent>) prManualMatched.getContent();
		}
		if(isNotEmpty(mprb) && mprb.size() > 0) {
			mprbf.addAll(mprb);
		}
		Map<String, List<String>> midsMap = findReconsilationids(prManualMatched,g2bManualMatch);
		if(isNotEmpty(midsMap)) {
			if(isNotEmpty(midsMap.get("2bIds"))) {
				List<InvoiceParent> gstr2binvoices =  Lists.newArrayList(gstr2bSupportRepository.findAll(midsMap.get("2bIds")));
				if(isNotEmpty(gstr2binvoices) && gstr2binvoices.size() > 0) {
					gstr2binvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					mg2bf.addAll(gstr2binvoices);
				}
			}
			if(isNotEmpty(midsMap.get("prIds"))) {
				List<InvoiceParent> prinvoices =  Lists.newArrayList(purchaseRepository.findAll(midsMap.get("prIds")));
				if(isNotEmpty(prinvoices) && prinvoices.size() > 0) {
					prinvoices.forEach(inv -> inv.setDocId(inv.getId().toString()));
					mprbf.addAll(prinvoices);
				}
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		
		Map<String,String> reconsileInvoices =  Maps.newHashMap();
		reconsileInvoices.put("prInvoices", writer.writeValueAsString(prInvoices.getContent()));
		reconsileInvoices.put("prMatched", writer.writeValueAsString(prbf));
		reconsileInvoices.put("prManualMatched", writer.writeValueAsString(mprbf));
		//reconsileInvoices.put("prMisMatched", writer.writeValueAsString(prMisMatched.getContent()));
		
		reconsileInvoices.put("g2bInvoice", writer.writeValueAsString(g2bInvoice.getContent()));
		reconsileInvoices.put("g2bMatch", writer.writeValueAsString(g2bf));
		reconsileInvoices.put("g2bManualMatch", writer.writeValueAsString(mg2bf));
		//reconsileInvoices.put("g2bMisMatch", writer.writeValueAsString(g2bMisMatch.getContent()));
		
		//Map<String,String> gstr2b = reconsilationService.getGstr2bData(id, clientid, month, year);
		//if(isNotEmpty(gstr2b)) {
		//	reconsileInvoices.putAll(gstr2b);			
		//}
		return reconsileInvoices;
	}
	
	public Map<String, List<String>> findReconsilationids(Page<? extends InvoiceParent> prMatched,Page<? extends InvoiceParent> g2bMatch){
		List<String> g2bids = Lists.newArrayList();
		List<String> prids = Lists.newArrayList();
		List<String> g2bbids = Lists.newArrayList();
		List<String> prrids = Lists.newArrayList();
		Map<String, List<String>> idsMap = Maps.newHashMap();
		if(isNotEmpty(prMatched) && isNotEmpty(prMatched.getContent())) {
			for(InvoiceParent inv : prMatched.getContent()) {
				prids.addAll(inv.getGstr2bMatchingId());
				prrids.add(inv.getId().toString());
			}
			
		}
		if(isNotEmpty(g2bMatch) && isNotEmpty(g2bMatch.getContent())) {
			for(InvoiceParent inv : g2bMatch.getContent()) {
				g2bids.addAll(inv.getGstr2bMatchingId());
				g2bbids.add(inv.getId().toString());
			}
		}
		prids.removeAll(g2bbids);
		g2bids.removeAll(prrids);
		idsMap.put("prIds" , g2bids);
		idsMap.put("2bIds" , prids);
				
		return idsMap;		
	}
	
	
	@RequestMapping(value = "/g2bInvoicesForMannualMatch/{clientid}/{invoiceid}/{returntype}/{month}/{year}/{monthlyOryearly}", method = RequestMethod.GET)
	public @ResponseBody String getInvoicesForMannualMatch(@PathVariable("clientid") String clientid, @PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, @PathVariable("month") int month, @PathVariable("year") int year, @PathVariable("monthlyOryearly") String monthlyOryearly, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		
		Pageable pageable = null;
		Date stDate = null;
		Date endDate = null;
		
		String st = request.getParameter("start");
		InvoiceFilter filter = new InvoiceFilter();
		filter.setBooksOrReturns(request.getParameter("booksOrReturns"));
		filter.setPaymentStatus(request.getParameter("paymentStatus"));
		filter.setInvoiceType(request.getParameter("invoiceType"));
		filter.setUser(request.getParameter("user"));
		filter.setVendor(request.getParameter("vendor"));
		filter.setBranch(request.getParameter("branch"));
		filter.setVertical(request.getParameter("vertical"));
		filter.setReverseCharge(request.getParameter("reverseCharge"));
		filter.setSupplyType(request.getParameter("supplyType"));
		filter.setDocumentType(request.getParameter("documentType"));
		filter.setGstno(request.getParameter("gstno"));
		filter.setDateofInvoice(request.getParameter("dateofInvoice"));
		filter.setInvoiceno(request.getParameter("invoiceno"));
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		List<String> rtarray = Lists.newArrayList();
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		/*invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		invTypes.add(MasterGSTConstants.IMP_GOODS);*/
		if("monthly".equalsIgnoreCase(monthlyOryearly)) {
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			
			int presentYear = calendar.get(Calendar.YEAR);
			int presentMonth = calendar.get(Calendar.MONTH)+1;
			Calendar cal = Calendar.getInstance();
			if(month < 10) {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
				
			}else {
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				if(presentYear != year) {
					cal.set(year, 9, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}else {
					cal.set(year, presentMonth, 0, 23, 59, 59);
					endDate = new java.util.Date(cal.getTimeInMillis());
				}
			}
		}else {
			int yr = year;
			if(month < 4) {
				yr = yr-1;
				Calendar cal = Calendar.getInstance();
				cal.set(year-1, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}else {
				Calendar cal = Calendar.getInstance();
				cal.set(year, 3, 1, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(year+1, 3, 0, 23, 59, 59);
				endDate = new java.util.Date(cal.getTimeInMillis());
			}
			//String year = fp.substring(2);
			
			Date presentDate = new Date();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(presentDate);
			int presentYear = calendar.get(Calendar.YEAR);
			for(int i=yr;i<=presentYear;i++) {
				for(int j=4;j<=12;j++) {
					String strMonth = j < 10 ? "0" + j : j + "";
					rtarray.add(strMonth+(i));
				}
				for(int k=1;k<=3;k++) {
					String strMonth = k < 10 ? "0" + k : k + "";
					rtarray.add(strMonth+(i+1));
				}
			}
			
		}
		Page<? extends InvoiceParent> invoices = null;
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add("Not In Purchases");
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			
			if("monthly".equalsIgnoreCase(monthlyOryearly)) {
				invoicesMap.put("invoices",reconsilationDao.findByG2bClientidAndMonthAndYearForMannualMatch(clientid, invTypes, matchingstatus, stDate, endDate, start, length, searchVal, filter));
				invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			}else {
				invoicesMap.put("invoices",reconsilationDao.findByClientidAndFpinForMannualMatch(clientid, invTypes, matchingstatus, rtarray, start, length, searchVal, filter));
				invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			}
		}else {
			List<String> matchingstatus = new ArrayList<String>();
			matchingstatus.add("Not In GSTR2B");
			matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			if("monthly".equalsIgnoreCase(monthlyOryearly)) {
				invoicesMap.put("invoices",reconsilationDao.findByClientidAndMonthAndYearForMannualMatch(clientid, invTypes, matchingstatus, stDate, endDate, start, length, searchVal, filter));
				invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			}else {
				invoicesMap.put("invoices",reconsilationDao.findByClientidAndFpinForMannualMatch(clientid, invTypes, matchingstatus, rtarray, start, length, searchVal, filter));
				invoices = (Page<? extends InvoiceParent>)invoicesMap.get("invoices");
			}
		}
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setDocId(invoiceParent.getId().toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoicesMap);
	}
	
	@RequestMapping(value = "/g2bmannualMatchArray/{returnType}/{invoiceid}", method = RequestMethod.POST)
	public @ResponseBody void performMismatch(@RequestBody List<MisMatchVO> records,@PathVariable("returnType") String returnType,@PathVariable("invoiceid") String invoiceid,ModelMap model) throws Exception {
		final String method = "performMismatch ::";
		
		reconsilationService.updateMannualRecords(records, returnType,invoiceid);
	}
	
	@RequestMapping(value = "/viewMannualG2bMatchingInvoices/{invoiceid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody String getInvoices(@PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		
		Pageable pageable = null;
		Page<? extends InvoiceParent> invoices = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			invoices = reconsilationService.findByMatchingIdAndMatchingStatus(invoiceid, MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED, pageable);
		}else {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
			invoices = reconsilationService.findByPrMatchingIdAndMatchingStatus(invoiceid,MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED,pageable);
			
			
			
		}
		if(isNotEmpty(invoices)) {
			for(InvoiceParent invoiceParent : invoices) {
				invoiceParent.setDocId(invoiceParent.getId().toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		if(returntype.equals(GSTR1)) {
			FilterProvider filters = new SimpleFilterProvider().addFilter("gstr1Filter", SimpleBeanPropertyFilter.serializeAll());
			writer=mapper.writer(filters);
		} else {
			writer=mapper.writer();
		}
		return writer.writeValueAsString(invoices);
	}
	
	@RequestMapping(value = "/g2bMannualMatchingInvoice/{invoiceid}/{returntype}", method = RequestMethod.GET)
	public @ResponseBody InvoiceParent g2bMannualMatchingInvoice(@PathVariable("invoiceid") String invoiceid, @PathVariable("returntype") String returntype, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		
		InvoiceParent invoice = null;
		if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
			invoice = purchaseRepository.findOne(invoiceid);
		}else {
			invoice = gstr2bSupportRepository.findOne(invoiceid);
		}
		if(isNotEmpty(invoice)) {
			invoice.setDocId(invoice.getId().toString());
		}
		return invoice;
	}
	
	/*@RequestMapping(value = "/update2breconcileinv/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String reconcileInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "reconcileInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Client client = clientService.findById(clientid);
		Date reconciledDate = new Date();
		client.setGstr2bReconcileDate(simpleDateFormat.format(reconciledDate));
		clientService.saveClient(client);
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
				if(isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			if(usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				
				model.addAttribute("error", errormsg);
			}else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		} else {
			
			String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
			if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
				String[] invTypes = {MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
						MasterGSTConstants.B2BA, MasterGSTConstants.CDNA,MasterGSTConstants.IMP_GOODS};
				OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
				Boolean billdate = false;
				if(isNotEmpty(otherconfig)){
					billdate = otherconfig.isEnableTransDate();
				}
				for (String invType : invTypes) {
					if(isEmpty(otherconfig) || !billdate) {
						List<PurchaseRegister> purchaseRegisters = clientService.getPurchaseRegisters(invType, clientid, month, year, true);
						logger.debug(CLASSNAME + method + "purchaseRegisters\t" + purchaseRegisters.size());
						if (isNotEmpty(purchaseRegisters)) {
							reconsilationService.updateGstr2bMismatchStatus(clientid, purchaseRegisters, null, invType, client.getGstnnumber(), retPeriod,"monthly");
						}
					}else {
						List<PurchaseRegister> purchaseRegisters = clientService.getPurchaseRegistersByTransactionDate(invType, clientid, month, year, true);
						logger.debug(CLASSNAME + method + "purchaseRegisters\t" + purchaseRegisters.size());
						if (isNotEmpty(purchaseRegisters)) {
							reconsilationService.updateGstr2bMismatchStatusTrDate(clientid, purchaseRegisters, null, invType, client.getGstnnumber(), retPeriod,"monthly");
						}
					}
				}
			}
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("type", "gstr2breconsile");
		ClientConfig clientConfig = clientService.getClientConfig(clientid);
		model.addAttribute("clientConfig", clientConfig);
		model.addAttribute("client", client);
				
		model.addAttribute("returntype", GSTR2);
		
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);

		logger.debug(CLASSNAME + method + END);
		return "client/all_invoice_view";
	}
	*/
	
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
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	
	//Performace Changes
	@RequestMapping(value = "/getGstr2bReconsileInvsSupport/{clientid}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getAdditionalInvoicesSupport(@PathVariable("clientid") String clientid, @PathVariable("year") int year, 
		@PathVariable int month,@RequestParam(defaultValue = MasterGSTConstants.MONTHLY, required = false) String isYearly, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean isBilledate = false;
		Boolean isMonthly = MasterGSTConstants.MONTHLY.equals(isYearly) ? true : false;
		if(isNotEmpty(otherconfig)){
			isBilledate = otherconfig.isEnableTransDate();
		}
		boolean isTransactionDate = false;
		if(isEmpty(otherconfig) || !isBilledate) {
			isTransactionDate = false;
		}else {
			isTransactionDate = true;
		}
		Map<String, Object> invoicesMap = reconsilationService.getInvoicesSupport(clientid, month, year, isTransactionDate, isMonthly);
		return invoicesMap;
	}
	
	@RequestMapping(value = "/reconsilegstr2binvs/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Object> getGstr2bReconcileInvoices(@PathVariable String id, @PathVariable String clientid,
			@PathVariable int month, @PathVariable int year, @RequestParam String returntype, @RequestParam(defaultValue = MasterGSTConstants.MONTHLY, required = false) String isYearly,HttpServletRequest request){
		
		Boolean isMonthly = MasterGSTConstants.MONTHLY.equals(isYearly) ? true : false;
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		Boolean isBilledate = false;
		if(isNotEmpty(otherconfig)){
			isBilledate = otherconfig.isEnableTransDate();
		}
		String st = request.getParameter("start");
		InvoiceFilter filter = invoiceFilter(request);

		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		boolean isTransactionDate = false;
		if(isEmpty(otherconfig) || !isBilledate) {
			isTransactionDate = false;
		}else {
			isTransactionDate = true;
		}
		
		Map<String, Object> dataMap = reconsilationService.getGstr2bReconcileInvoices(id, clientid, month, year, start, length, searchVal, filter, isTransactionDate, isMonthly);
		
		return dataMap;
	}
	
	@RequestMapping(value = "/getGstr2bReconcileSummary/{clientid}/{month}/{year}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<String, Object> getGstr2bReconcileSummary(@PathVariable("clientid") String clientid,
			@PathVariable int month, @PathVariable("year") int year,@RequestParam(defaultValue = MasterGSTConstants.MONTHLY, required = false) String isYearly, HttpServletRequest request) throws Exception {

		Boolean isMonthly = MasterGSTConstants.MONTHLY.equals(isYearly) ? true : false;

		Boolean isBilledDate = false;
		OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
		if(isNotEmpty(otherconfig)){
			isBilledDate = otherconfig.isEnableTransDate();
		}
		boolean isTransactionDate = false;
		if(isEmpty(otherconfig) || !isBilledDate) {
			isTransactionDate = false;
		}else {
			isTransactionDate = true;
		}
		Map<String, Object> summary = reconsilationService.getGstr2bReconcileSummary(clientid, month, year, isTransactionDate, isMonthly);
		
		return summary;
	}
	
	@RequestMapping(value = "/gstr2breconcileinv/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String reconcileInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, 
			@RequestParam(defaultValue = MasterGSTConstants.MONTHLY, required = false) String isYearly, HttpServletRequest request) throws Exception {
		final String method = "reconcileInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		Boolean isMonthly = MasterGSTConstants.MONTHLY.equals(isYearly) ? true : false;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		Client client = clientService.findById(clientid);
		Date reconciledDate = new Date();
		client.setGstr2bReconcileDate(simpleDateFormat.format(reconciledDate));
		clientService.saveClient(client);
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		User user = userService.findById(id);
		ReconcileTemp reconcileTemp = new ReconcileTemp();
		reconcileTemp.setClientid(clientid);
		if(isMonthly) {
			reconcileTemp.setMonthlyoryearly(MasterGSTConstants.MONTHLY);		
		}else {
			reconcileTemp.setMonthlyoryearly(MasterGSTConstants.YEARLY);					
		}
		reconcileTemp.setReturntype(MasterGSTConstants.GSTR2B);
		if(isNotEmpty(user)) {
			reconcileTemp.setUserid(new ObjectId(id));
			reconcileTemp.setInitiateduserid(id);
			if(isNotEmpty(user.getFullname())) {
				reconcileTemp.setFullname(user.getFullname());
			}
		}
		reconcileTempRepository.save(reconcileTemp);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
					}
				}
				if(isNotEmpty(companyUser.getAddclient())) {
					model.addAttribute("addClient", companyUser.getAddclient());
				}else {
					model.addAttribute("addClient", "true");
				}
			}else {
				model.addAttribute("addClient", "true");
			}
		}else {
			model.addAttribute("addClient", "true");
		}
		String userid = userid(id,clientid);
		if (!subscriptionService.allowUploadInvoices(userid, 1l)) {
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid()) && usrid.equals(user.getParentid())){
				User usr = userRepository.findById(userid);
				String errormsg = "";
				if(isNotEmpty(usr)) {
					errormsg = primaryHolderMessage(client,usr);
				}else {
					errormsg = "Your Admin user subscription has expired. Please Contact your Admin User to proceed further!";
				}
				
				model.addAttribute("error", errormsg);
			}else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		} else {
			long gstr2bList = reconsilationDao.getGstr2bInvoicesCount(clientid, retPeriod);
			ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
			recon.setReturntype(MasterGSTConstants.GSTR2B);
			recon.setTotalinvoices((Long)gstr2bList);
			if(isMonthly) {
				recon.setMonthlyoryearly(MasterGSTConstants.MONTHLY);		
			}else {
				recon.setMonthlyoryearly(MasterGSTConstants.YEARLY);					
			}
			recon = reconcileTempRepository.save(recon);
			
			String[] invTypes = {MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.IMP_GOODS};
			OtherConfigurations otherconfig = otherConfigurationRepository.findByClientid(clientid);
			Boolean billdate = false;
			if(isNotEmpty(otherconfig)){
				billdate = otherconfig.isEnableTransDate();
			}
			for (String invType : invTypes) {
				if(isEmpty(otherconfig) || !billdate) {
					reconsilationService.updateGstr2bMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, isMonthly, true);
				}else {
					reconsilationService.updateGstr2bMismatchStatus(clientid, invType, client.getGstnnumber(), retPeriod, month, year, isMonthly, false);
				}
			}
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("type", "gstr2breconcile");
		ClientConfig clientConfig = clientService.getClientConfig(clientid);
		model.addAttribute("clientConfig", clientConfig);
		model.addAttribute("client", client);
		if(returntype.equals(GSTR2A)){
			returntype = GSTR2;
		}
		model.addAttribute("returntype", returntype);
		
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		ReconcileTemp temp = reconcileTempRepository.findByClientid(clientid);
		if(isNotEmpty(temp)) {
			reconcileTempRepository.delete(temp);
			logger.info("reconcile temp deleted...");
			
		}
		logger.debug(CLASSNAME + method + END);
		if(!isMonthly) {
			return "redirect:/reports/" + id + "/" + fullname + "/" + usertype + "/"+ clientid + "/" + month + "/" + year+"?type=yearlyGstr2bRecocileReport";
		}
		
		return "client/all_invoice_view";
	}
	
	public InvoiceFilter invoiceFilter(HttpServletRequest request) {
		InvoiceFilter filter = new InvoiceFilter();
		filter.setDocumentType(request.getParameter("documentType"));
		filter.setInvoiceType(request.getParameter("invoiceType"));
		String user = request.getParameter("user");
		if(isNotEmpty(user)) {
			if(user.contains("-mgst-")) {
				user = user.replaceAll("-mgst-", "&");
			}
		}
		filter.setUser(user);
		String vendor = request.getParameter("vendor");
		if(isNotEmpty(vendor)) {
			if(vendor.contains("-mgst-")) {
				vendor = vendor.replaceAll("-mgst-", "&");
			}
		}
		filter.setVendor(vendor);
		filter.setBranch(request.getParameter("branch"));
		filter.setVertical(request.getParameter("vertical"));
		return filter;	
	}
}
