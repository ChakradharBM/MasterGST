package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2A;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Anx1InvoiceSupport;
import com.mastergst.usermanagement.runtime.domain.AnxInvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanyUser;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.service.Anx1Service;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.ImportMapperService;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

@Controller
public class Anx1Controller {
	private static final Logger logger = LogManager.getLogger(Anx1Controller.class.getName());
	private static final String CLASSNAME = "Anx1Controller::";
	
	@Autowired
	private ClientService clientService;
	@Autowired
	private Anx1Service anx1Service;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private UserService userService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	private OtpExpiryService otpExpiryService;
	@Autowired
	private ImportMapperService importMapperService;
	@Autowired
	ClientUserMappingRepository clientUserMappingRepository;
	
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}

	
	@RequestMapping(value = "/getAnx1Invs/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String getAnx1Invoices(@PathVariable("id") String id, @PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("booksOrReturns")String booksOrReturns,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		InvoiceFilter filter = new InvoiceFilter();
		filter.setBooksOrReturns(request.getParameter("booksorReturns"));
		filter.setPaymentStatus(request.getParameter("paymentStatus"));
		filter.setInvoiceType(request.getParameter("invoiceType"));
		filter.setUser(request.getParameter("user"));
		filter.setVendor(request.getParameter("vendor"));
		filter.setBranch(request.getParameter("branch"));
		filter.setVertical(request.getParameter("vertical"));
		filter.setReverseCharge(request.getParameter("reverseCharge"));
		filter.setSupplyType(request.getParameter("supplyType"));
		filter.setDocumentType(request.getParameter("documentType"));
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		
		Client client = clientService.findById(clientid);
		String retType = returntype;
		Pageable pageable = null;
		Map<String, Object> invoicesMap = anx1Service.getInvoices(pageable, client, id, retType, "notreports", month, year, start, length, searchVal, filter, true,booksOrReturns);
		Page<? extends AnxInvoiceParent> invoices = (Page<? extends AnxInvoiceParent>)invoicesMap.get("invoices");
		if(isNotEmpty(invoices)) {
			for(AnxInvoiceParent invoiceParent : invoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		} 
		
		/*Map<String, Object> invoiceData = new HashMap<>();
		invoiceData.put("data", invoices);
		invoiceData.put("recordsFiltered", invoices.getTotalElements());
		invoiceData.put("recordsTotal", invoices.getTotalElements());
		invoiceData.put("draw", request.getParameter("draw"));*/
		
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
	
	@RequestMapping(value = "/dwnldAnx1inv/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String downloadInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("returntype") String returntype, @PathVariable("month") int month,
			@PathVariable("year") int year, ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "downloadInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);

		Client client = clientService.findById(clientid);
		String strMonth = month < 10 ? "0" + month : month + "";
		ClientStatus clientStatus = clientService.getClientStatus(clientid, returntype, strMonth + year);
		if (isNotEmpty(clientStatus)) {
			client.setStatus(clientStatus.getStatus());
		}
		//String tokenError = (String) request.getSession().getAttribute("tokenError");
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
			}else{
				model.addAttribute("error", "Your subscription has expired. Kindly subscribe to proceed further!");
			}
		} else { 
			String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
			if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
				List<String> invTypes=Lists.newArrayList();
			
					invTypes.add(MasterGSTConstants.B2B);
					invTypes.add(MasterGSTConstants.B2BA);
					invTypes.add(MasterGSTConstants.B2C);
					invTypes.add(MasterGSTConstants.DE);
					invTypes.add(MasterGSTConstants.DEA);
					invTypes.add(MasterGSTConstants.ECOM);
					invTypes.add(MasterGSTConstants.EXPWOP);
					invTypes.add(MasterGSTConstants.EXPWP);
					invTypes.add(MasterGSTConstants.IMPGSEZ);
					invTypes.add(MasterGSTConstants.IMPG);
					invTypes.add(MasterGSTConstants.IMPS);
					invTypes.add(MasterGSTConstants.MIS);
					invTypes.add(MasterGSTConstants.RJDOC);
					invTypes.add(MasterGSTConstants.REV);
					invTypes.add(MasterGSTConstants.SEZWOP);
					invTypes.add(MasterGSTConstants.SEZWOPA);
					invTypes.add(MasterGSTConstants.SEZWP);
					invTypes.add(MasterGSTConstants.SEZWPA);
				for (String invType : invTypes) {
					if(returntype.equalsIgnoreCase(MasterGSTConstants.ANX1)) {
						anx1Service.performDownload(client, invType, strMonth + year, clientid, id, month, year);
						
					}
				}
			}
		}
		updateModel(model, id, fullname, usertype, month, year);
		model.addAttribute("type", "dwnldgstr2a");

		model.addAttribute("client", client);
		if(returntype.equals(GSTR2A)){
			returntype = GSTR2;
		}
		model.addAttribute("returntype", returntype);
		
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		List<TemplateMapperDoc> mappers = importMapperService.getMapperDocs(clientid);
		model.addAttribute("mappers", mappers);
		if (returntype.equals(GSTR1)) {
			String submissionYear = year + "-" + (year + 1);
			if (month > 3) {
				submissionYear = (year + 1) + "-" + (year + 2);
			}
			CompanyInvoices invoiceSubmissionData = profileService.getUserInvoiceSubmissionDetails(client.getId().toString(), submissionYear, null);
			if (isNotEmpty(invoiceSubmissionData)) {
				model.addAttribute("invoiceSubmissionData", invoiceSubmissionData);
			}
		}

		logger.debug(CLASSNAME + method + END);
		return "client/all_invoice_view";
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
}
