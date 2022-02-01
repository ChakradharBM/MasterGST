package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.GSTR4Annual;
import com.mastergst.usermanagement.runtime.domain.GSTR4B2B;
import com.mastergst.usermanagement.runtime.domain.GSTR4B2C;
import com.mastergst.usermanagement.runtime.domain.GSTR4IMPS;
import com.mastergst.usermanagement.runtime.domain.GSTR4ImpsItem;
import com.mastergst.usermanagement.runtime.domain.GSTR4Items;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CS;
import com.mastergst.usermanagement.runtime.domain.InvoiceItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TurnoverOptions;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.Gstr4AnnualService;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mastergst.usermanagement.runtime.support.Utility;

@Controller
public class GSTR4AnnualController {
	
	private static final Logger logger = LogManager.getLogger(GSTR4AnnualController.class.getName());
	private static final String CLASSNAME = "GSTR4AnnualController::";
	@Autowired	private UserService userService;
	@Autowired	private ClientService clientService;
	@Autowired	private ProfileService profileService;
	@Autowired	private Gstr4AnnualService gstr4AnnualService;
	@Autowired	private SubscriptionService subscriptionService;
	@Autowired	private UserRepository userRepository;
	@Autowired	ClientUserMappingRepository clientUserMappingRepository;
	@Autowired	private OtpExpiryService otpExpiryService;
	
	@RequestMapping(value = "/addGSTR4invoice/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addGSTR4invoice(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addGSTR4invoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);
		logger.debug(CLASSNAME + method + "fullname\t" + fullname);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		updateModel(model, id, fullname, usertype, month, year);
		User user = userService.findById(id);
		String usrid = id;
		if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())){
			CompanyUser companyUser = profileService.getCompanyUser(user.getEmail());
			if(isNotEmpty(companyUser)) {
				if(isNotEmpty(companyUser.getCompany())){
					if(companyUser.getCompany().contains(clientid)){
						usrid = user.getParentid();
						model.addAttribute("companyUser", companyUser);
					}
				}
				if(isNotEmpty(companyUser) && isNotEmpty(companyUser.getAddclient())) {
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
		model.addAttribute("returntype", GSTR4);
		Client client = clientService.findById(clientid);
		Double turnover = 0d;
		String financialYear = null;
		if(year == 2017){
			financialYear = "JUL"+year+"-MAR"+(year+1);
		}else {
			financialYear = year+"-"+(year+1);
		}
		
		if(isNotEmpty(client) && isNotEmpty(client.getTurnovergoptions())){
			
			for(TurnoverOptions options : client.getTurnovergoptions()) {
				if(options.getYear().equals(financialYear)){
					turnover = options.getTurnover();
					break;
				}
			}
		}
		String fp = "03"+year;
		ClientStatus clientStatus = clientService.getClientStatus(clientid, GSTR4, fp);
		if (isNotEmpty(clientStatus)) {
			client.setStatus(clientStatus.getStatus());
		}
		
		TotalInvoiceAmount b2bor = null;
		TotalInvoiceAmount b2br = null;
		TotalInvoiceAmount b2c = null;
		TotalInvoiceAmount imps = null;
		
		GSTR4Annual gstr4Annual =gstr4AnnualService.getGstr4AnnualInvoices(client.getId().toString(), fp);
		
		if(isNotEmpty(gstr4Annual)) {
			
			if(isNotEmpty(gstr4Annual.getB2bor())) {
				
				b2bor = new TotalInvoiceAmount();
				List<GSTR4B2B> b2borinv = gstr4Annual.getB2bor();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(b2borinv)) {
					transactions = b2borinv.size();
					for(GSTR4B2B inv : b2borinv) {
						
						for(GSTR4Items item : inv.getItms()) {
							totalTaxableAmount += item.getTxval();
							totalIGSTAmount += item.getIamt();
							totalCGSTAmount += item.getCamt();
							totalSGSTAmount += item.getSamt();
							totalCESSAmount += item.getCsamt();	
						}
					}
	
				}
				b2bor.setTotalTransactions(transactions);
				b2bor.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				b2bor.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2bor.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2bor.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2bor.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			
			if(isNotEmpty(gstr4Annual.getB2br())) {
				
				b2br = new TotalInvoiceAmount();
				List<GSTR4B2B> b2brinv = gstr4Annual.getB2br();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(b2brinv)) {
					transactions = b2brinv.size();
					for(GSTR4B2B inv : b2brinv) {
						
						for(GSTR4Items item : inv.getItms()) {
							totalTaxableAmount += item.getTxval();
							totalIGSTAmount += item.getIamt();
							totalCGSTAmount += item.getCamt();
							totalSGSTAmount += item.getSamt();
							totalCESSAmount += item.getCsamt();	
						}
					}
	
				}
				b2br.setTotalTransactions(transactions);
				b2br.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				b2br.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2br.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2br.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2br.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			
			if(isNotEmpty(gstr4Annual.getB2bur())) {
				
				b2c = new TotalInvoiceAmount();
				List<GSTR4B2C> b2burinv = gstr4Annual.getB2bur();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(b2burinv)) {
					transactions = b2burinv.size();
					for(GSTR4B2C inv : b2burinv) {
						
						for(GSTR4Items item : inv.getItms()) {
							totalTaxableAmount += item.getTxval();
							totalIGSTAmount += item.getIamt();
							totalCGSTAmount += item.getCamt();
							totalSGSTAmount += item.getSamt();
							totalCESSAmount += item.getCsamt();	
						}
					}
	
				}
				b2c.setTotalTransactions(transactions);
				b2c.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				b2c.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2c.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2c.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2c.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			
			if(isNotEmpty(gstr4Annual.getImps())) {
				
				imps = new TotalInvoiceAmount();
				List<GSTR4IMPS> impsinv = gstr4Annual.getImps();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(impsinv)) {
					transactions = impsinv.size();
					for(GSTR4IMPS inv : impsinv) {
						
						for(GSTR4ImpsItem item : inv.getItms()) {
							totalTaxableAmount += item.getTxval();
							totalIGSTAmount += item.getIamt();
							totalCESSAmount += item.getCsamt();	
						}
					}
	
				}
				imps.setTotalTransactions(transactions);
				imps.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				imps.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				imps.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				imps.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				imps.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
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
			} else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		}else {
			String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
			if(!otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
				model.addAttribute("error", "Unauthorized User!");
			}
		}

		model.addAttribute("turnover", turnover);
		model.addAttribute("client", client);
		model.addAttribute("b2borAmts", b2bor);
		model.addAttribute("b2brAmts", b2br);
		model.addAttribute("b2cAmts", b2c);
		model.addAttribute("impsAmts", imps);
		model.addAttribute("gstr4annualinv", gstr4Annual);
		if(isNotEmpty(gstr4Annual)) {
			if(isNotEmpty(gstr4Annual.getCmpdata())) {
				model.addAttribute("gstr4annualcmp", gstr4Annual.getCmpdata());
			}
		}
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		logger.debug(CLASSNAME + method + END);
		return "client/add_gstr4annual_invoice";
	}
	
	@RequestMapping(value = "/populateSupplierData/{id}/{clientid}/{year}")
	public @ResponseBody GSTR4Annual populateSupplierWiseData(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("year") int year,@RequestParam("type")String type, ModelMap model) throws Exception {
		final String method = "populateSupplierWiseData::";
		logger.debug(CLASSNAME + method + BEGIN);
		GSTR4Annual gstr4Annual =gstr4AnnualService.getGstr4AnnualInvoices(clientid, "03"+year);
		if(isNotEmpty(gstr4Annual)) {
			return gstr4Annual;
		}
		logger.debug(CLASSNAME + method + END);
		return null;
	}
	
	@RequestMapping(value = "/saveGstr4Annualinvoice/{returntype}/{usertype}/{month}/{year}", method = RequestMethod.POST)
	public String saveAnnualInvoice(@ModelAttribute("invoice") GSTR4Annual invoice,
			@PathVariable("returntype") String returntype, @PathVariable("usertype") String usertype,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "saveAnnualinvoice::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "invoice\t" + invoice);
		logger.debug(CLASSNAME + method + "id\t" + invoice.getUserid());
		logger.debug(CLASSNAME + method + "fullname\t" + invoice.getFullname());
		logger.debug(CLASSNAME + method + "clientid\t" + invoice.getClientid());
		updateModel(model, invoice.getUserid(), invoice.getFullname(), usertype, month, year);
		Client client = clientService.findById(invoice.getClientid());
		model.addAttribute("client", client);
		model.addAttribute("returntype", returntype);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		String fp = "03"+(year);
		invoice.setClientid(client.getId().toString());
		invoice.setGstin(client.getGstnnumber());
		invoice.setFp(fp);
		invoice.setIsnil("N");
		invoice.setIsreset("N");
		gstr4AnnualService.saveGSTR4AnnualInvoice(invoice,client,month,year);

		logger.debug(CLASSNAME + method + END);
		return "redirect:/alliview/" + invoice.getUserid() + "/" + invoice.getFullname() + "/" + usertype + "/"
				+ invoice.getClientid() + "/" + returntype + "/" + month + "/" + year + "?type=inv";
	}
	
	@RequestMapping(value = "/getpopulategstr4annualdata/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String  autoPopulateGstr4AnnualData(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype,@PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "autoPopulateGstr4AnnualData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		//GSTR4Annual gstr4 = populateGSTR4Detailss(client, month, year);
		User user = userService.findById(id);
		if(isNotEmpty(user) && isNotEmpty(user.getFullname())) {
			fullname = user.getFullname();
		}
		Client client = clientService.findById(clientid);
		if(isNotEmpty(client)) {
			String fp = "03"+year;
			gstr4AnnualService.deleteGstr4AnnualData(clientid, fp);
			
			Double turnover = 0d;
			String financialYear = null;
			if(year == 2017){
				financialYear = "JUL"+year+"-MAR"+(year+1);
			}else {
				financialYear = year+"-"+(year+1);
			}
			
			if(isNotEmpty(client) && isNotEmpty(client.getTurnovergoptions())){
				
				for(TurnoverOptions options : client.getTurnovergoptions()) {
					if(options.getYear().equals(financialYear)){
						turnover = options.getTurnover();
						break;
					}
				}
			}
			
			String retPeriod = "";
			if(month > 3) {
				retPeriod = "03"+(year+1);
			}else {
				retPeriod = "03"+(year);
			}
			ClientStatus clientStatus = clientService.getClientStatus(clientid, GSTR4, retPeriod);
			if (isNotEmpty(clientStatus)) {
				client.setStatus(clientStatus.getStatus());
			}
			GSTR4Annual gstr4Annual = new GSTR4Annual();
			//GSTR4 gstr4 = clientService.getGSTR4Invoice(clientid, retPeriod);
			
			gstr4Annual.setClientid(clientid);
			gstr4Annual.setUserid(id);
			gstr4Annual.setFullname(fullname);
			gstr4Annual.setGstin(client.getGstnnumber());
			gstr4Annual.setFp(fp);
			gstr4Annual.setAgg_turnover(turnover);
			gstr4AnnualService.autoCalculateData(gstr4Annual, client, year);
			gstr4AnnualService.saveInvoices(gstr4Annual, user, client, fp);
			
			gstr4AnnualService.syncGstr4AnnualCmp(user, client, fp);
		}	
		
		logger.debug(CLASSNAME + method + END);
		return "redirect:/addGSTR4invoice/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
	}
	
	/*@RequestMapping(value = "/syncgstr4annualcmp/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public void syncGstr4CmpData(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		Client client = clientService.findById(clientid);
		//GSTR4Annual gstr4 = populateGSTR4Detailss(client, month, year);
		String retPeriod = "";
		if(month > 3) {
			retPeriod = "03"+(year+1);
		}else {
			retPeriod = "03"+(year);
		}
		GSTR4AnnualCMPResponse cmpresponse = iHubConsumerService.getAnnualCmp(client, client.getGstnnumber(), retPeriod);
	}
	
	private GSTR4Annual populateGSTR4Detailss(Client client, int month, int year) throws UnknownHostException {
		String retPeriod = "";
		if(month > 3) {
			retPeriod = "03"+(year+1);
		}else {
			retPeriod = "03"+(year);
		}
		GSTR4Annual gstr4annual  = new GSTR4Annual();
		GSTR4AnnualCMPResponse cmpresponse = iHubConsumerService.getAnnualCmp(client, client.getGstnnumber(), retPeriod);
		 if (isNotEmpty(cmpresponse.getStatuscd()) && cmpresponse.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE) && isNotEmpty(cmpresponse.getData())) {
			 GSTR4AnnualCMPResponseData gstr4resData = cmpresponse.getData();
			 gstr4annual.setGstin(client.getGstnnumber());
			 gstr4annual.setFp(retPeriod);
			 if(isNotEmpty(gstr4resData)) {
				 
			 }
		 }
		return null;
	}*/
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
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
/*

	@RequestMapping(value = "/populateSupplierData/{id}/{clientid}/{year}")
	public @ResponseBody List<InvoiceItems> populateSupplierWiseData(@PathVariable("id") String id, @PathVariable("clientid") String clientid,
			@PathVariable("year") int year,@RequestParam("type")String type, ModelMap model) throws Exception {
		final String method = "populateSupplierWiseData::";
		Map<String,List<GSTRB2CS>> b2csmap = Maps.newHashMap();
		if(type.equalsIgnoreCase("b2bor")) {
			List<? extends InvoiceParent> b2borinvoices = null;//gstr4annualDao.getSupplierWiseInvoices(clientid, 0, Utility.getYearCode(4, year),"B2B","Regular").getContent();
			Map<String, InvoiceItems> dataMap = new HashMap<>();
			for (InvoiceParent invoice : b2borinvoices) {
				
				String ctin = invoice.getB2b().get(0).getCtin();
				String pos = invoice.getB2b().get(0).getInv().get(0).getPos();
				
				InvoiceItems items = null; 
				if(dataMap.containsKey(ctin)) {
					items = dataMap.get(ctin);
					items.setTxval(items.getTxval()+invoice.getTotaltaxableamount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setCgst(items.getCgst()+invoice.getTotalCgstAmount());
					items.setSgst(items.getSgst()+invoice.getTotalSgstAmount());
					items.setCess(items.getCess()+invoice.getTotalCessAmount());
				}else {
					items = new InvoiceItems();
					items.setCtin(ctin);
					items.setPos(pos);
					items.setTxval(invoice.getTotaltaxableamount());
					items.setIgst(invoice.getTotalIgstAmount());
					items.setCgst(invoice.getTotalCgstAmount());
					items.setSgst(invoice.getTotalSgstAmount());
					items.setCess(invoice.getTotalCessAmount());
				}
				dataMap.put(ctin, items);
			}
			return dataMap.values().stream().collect(Collectors.toList());
		}else if(type.equalsIgnoreCase("b2br")) {
			List<? extends InvoiceParent> b2brinvoices = null;//gstr4annualDao.getSupplierWiseInvoices(clientid, 0, Utility.getYearCode(4, year),"B2B","Reverse").getContent();
			Map<String, InvoiceItems> dataMap = new HashMap<>();
			for (InvoiceParent invoice : b2brinvoices) {
					
				String ctin = invoice.getB2b().get(0).getCtin();
				String pos = invoice.getB2b().get(0).getInv().get(0).getPos();
				
				InvoiceItems items = null; 
				if(dataMap.containsKey(ctin)) {
					items = dataMap.get(ctin);
					items.setTxval(items.getTxval()+invoice.getTotaltaxableamount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setCgst(items.getCgst()+invoice.getTotalCgstAmount());
					items.setSgst(items.getSgst()+invoice.getTotalSgstAmount());
					items.setCess(items.getCess()+invoice.getTotalCessAmount());
				}else {
					items = new InvoiceItems();
					items.setCtin(ctin);
					items.setPos(pos);
					items.setTxval(invoice.getTotaltaxableamount());
					items.setIgst(invoice.getTotalIgstAmount());
					items.setCgst(invoice.getTotalCgstAmount());
					items.setSgst(invoice.getTotalSgstAmount());
					items.setCess(invoice.getTotalCessAmount());
				}
				dataMap.put(ctin, items);
			}
			
			return dataMap.values().stream().collect(Collectors.toList());
		}else if(type.equalsIgnoreCase("b2c")) {
			List<? extends InvoiceParent> b2brinvoices = null;//gstr4annualDao.getSupplierWiseInvoices(clientid, 0, Utility.getYearCode(4, year),"B2B Unregistered","").getContent();
			
			Map<String, InvoiceItems> dataMap = new HashMap<>();
			for (InvoiceParent invoice : b2brinvoices) {
					
				//String ctin = invoice.getB2b().get(0).getCtin();
				String pos = ((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getPos();
				
				InvoiceItems items = null; 
				if(dataMap.containsKey(pos)) {
					items = dataMap.get(pos);
					items.setTxval(items.getTxval()+invoice.getTotaltaxableamount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setCgst(items.getCgst()+invoice.getTotalCgstAmount());
					items.setSgst(items.getSgst()+invoice.getTotalSgstAmount());
					items.setCess(items.getCess()+invoice.getTotalCessAmount());
				}else {
					items = new InvoiceItems();
					items.setCtin("");
					items.setPos(pos);
					items.setTxval(invoice.getTotaltaxableamount());
					items.setIgst(invoice.getTotalIgstAmount());
					items.setCgst(invoice.getTotalCgstAmount());
					items.setSgst(invoice.getTotalSgstAmount());
					items.setCess(invoice.getTotalCessAmount());
				}
				dataMap.put(pos, items);
			}
			
			return dataMap.values().stream().collect(Collectors.toList());
			
		}else if(type.equalsIgnoreCase("imps")) {
			List<? extends InvoiceParent> impginvoices = null;//gstr4annualDao.getSupplierWiseInvoices(clientid, 0, Utility.getYearCode(4, year),MasterGSTConstants.IMP_SERVICES,"").getContent();
			Map<String, InvoiceItems> dataMap = new HashMap<>();
			for (InvoiceParent invoice : impginvoices) {
					
				String pos = invoice.getStatename().substring(0, 2);
				
				InvoiceItems items = null; 
				if(dataMap.containsKey(pos)) {
					items = dataMap.get(pos);
					items.setTxval(items.getTxval()+invoice.getTotaltaxableamount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setIgst(items.getIgst()+invoice.getTotalIgstAmount());
					items.setCgst(items.getCgst()+invoice.getTotalCgstAmount());
					items.setSgst(items.getSgst()+invoice.getTotalSgstAmount());
					items.setCess(items.getCess()+invoice.getTotalCessAmount());
				}else {
					items = new InvoiceItems();
					items.setCtin("");
					items.setPos(pos);
					items.setTxval(invoice.getTotaltaxableamount());
					items.setIgst(invoice.getTotalIgstAmount());
					items.setCgst(invoice.getTotalCgstAmount());
					items.setSgst(invoice.getTotalSgstAmount());
					items.setCess(invoice.getTotalCessAmount());
				}
				dataMap.put(pos, items);
			}
			
			return dataMap.values().stream().collect(Collectors.toList());
		}
		return null;
	}
 */
}
