package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.AllGSTINTempData;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.MultiGSTNData;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.AllGSTnoTempRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.MultiGSTnRepository;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.service.IHubConsumerService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;

@Controller
public class MultiGSTNController {
	private static final Logger logger = LogManager.getLogger(MultiGSTNController.class.getName());
	private static final String CLASSNAME = "MultiGSTNController::";
	
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	private AllGSTnoTempRepository allGSTnoTempRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	@Autowired
	ProfileService profileService;
	@Autowired
	UserService userService;
	@Autowired
	SubscriptionService subscriptionService;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MultiGSTnRepository multiGSTnRepository;
	
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	
	@RequestMapping(value = "/multigstn/{id}/{month}/{year}/{gstnum}", method = RequestMethod.GET)
	public @ResponseBody Response validateSelectedgstno(@PathVariable("id") String id,
			 @PathVariable("month") int month,	@PathVariable("year") int year, @PathVariable("gstnum") String gstnum, ModelMap model,HttpServletRequest request)
					throws Exception {
		final String method = "validateSelectedgstno::";
		logger.debug(CLASSNAME + method + BEGIN);
		Response gstnResponse = new Response();
		String referer = request.getHeader("referer");
		logger.debug(referer.contains("cp_multiGtstin"));
		User user = userRepository.findOne(id);
		if(isNotEmpty(user) && isNotEmpty(referer) &&  referer.contains("cp_multiGtstin")) {
		
			if (isNotEmpty(gstnum) && Pattern.matches(gstnFormat, gstnum.trim())) {
				
				AllGSTINTempData existGSTN = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNotNull(id,gstnum);
				
				if(NullUtil.isNotEmpty(existGSTN)) {
					
					//List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
					allGSTnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstnum);
					Calendar cal = Calendar.getInstance();
					int pmonth = cal.get(Calendar.MONTH);
					int pyear = cal.get(Calendar.YEAR);
					int tdate = cal.get(Calendar.DATE);
					cal.set(pyear, pmonth, tdate, 0, 0,0);
					Date stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					cal.set(pyear, pmonth, tdate, 23, 59, 59);
					Date endDate = new java.util.Date(cal.getTimeInMillis());
					
					GSTINPublicData gstndata = gstinPublicDataRepository.findByGstinAndCreatedDateBetween(gstnum, stDate, endDate);
					if(isNotEmpty(gstndata)) {
						ResponseData data = new ResponseData();
						BeanUtils.copyProperties(gstndata, data);
						gstnResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
						gstnResponse.setData(data);
						//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
						//if(NullUtil.isNotEmpty(gstndataa)) {
							multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
						//}
						
						MultiGSTNData mgstdata = new MultiGSTNData();
						mgstdata.setUserid(id);
						mgstdata.setGstin(gstndata.getGstin());
						mgstdata.setDty(gstndata.getDty());
						mgstdata.setTradeNam(gstndata.getTradeNam());
						mgstdata.setRgdt(gstndata.getRgdt());
						mgstdata.setLstupdt(gstndata.getLstupdt());
						mgstdata.setStatus("VALID");
						mgstdata.setGstnid(gstndata.getId().toString());
						
						multiGSTnRepository.save(mgstdata);
						allGSTnoTempRepository.delete(existGSTN);
					}else {
						Response response = iHubConsumerService.publicSearch(gstnum);
						gstnResponse = response;
						if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
								&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							
							GSTINPublicData gstndataBasedonUserid = gstinPublicDataRepository.findByGstin(gstnum);
							if(NullUtil.isNotEmpty(gstndataBasedonUserid)) {
								gstinPublicDataRepository.delete(gstndataBasedonUserid.getId().toString());
							}
							//gstinPublicDataRepository.deleteByGstin(gstnum);
								
							GSTINPublicData publicData = new GSTINPublicData();
							BeanUtils.copyProperties(response.getData(), publicData);
							publicData.setCreatedDate(new Date());
							try {
								publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
							} catch (UnknownHostException e) {
									
								e.printStackTrace();
							}
							/*
							 * publicData.setUserid(id); //User user = userService.findById(id); if
							 * (NullUtil.isNotEmpty(user.getParentid())) {
							 * publicData.setParentid(user.getParentid()); } publicData.setStatus("VALID");
							 */
							GSTINPublicData gstpubdata =  gstinPublicDataRepository.save(publicData);
							//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
							//if(NullUtil.isNotEmpty(gstndataa)) {
								multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
							//}
							
							MultiGSTNData mgstdata = new MultiGSTNData();
							mgstdata.setUserid(id);
							mgstdata.setGstin(gstpubdata.getGstin());
							mgstdata.setDty(gstpubdata.getDty());
							mgstdata.setTradeNam(gstpubdata.getTradeNam());
							mgstdata.setRgdt(gstpubdata.getRgdt());
							mgstdata.setLstupdt(gstpubdata.getLstupdt());
							mgstdata.setStatus("VALID");
							mgstdata.setGstnid(gstpubdata.getId().toString());
							
							multiGSTnRepository.save(mgstdata);
							allGSTnoTempRepository.delete(existGSTN);
						}else {
							AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
							if(isNotEmpty(gstde)) {
								gstde.setStatus("Invalid GSTIN");
								gstde = profileService.saveGstno(gstde);

								//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
								//if(NullUtil.isNotEmpty(gstndataa)) {
									multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
								//}
								
								MultiGSTNData mulgstndata = new MultiGSTNData();
								mulgstndata.setGstin(gstnum);
								mulgstndata.setUserid(id);
								mulgstndata.setGstnid(gstde.getId().toString());
								mulgstndata.setStatus("Invalid GSTIN");
								multiGSTnRepository.save(mulgstndata);
							}
							
						}
						
						SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(id);
						if(isNotEmpty(subscriptionDetails)) {
							if(isNotEmpty(subscriptionDetails.getProcessedInvoices())) {
								subscriptionDetails.setProcessedInvoices(
										subscriptionDetails.getProcessedInvoices() + 1);
							} else {
								subscriptionDetails.setProcessedInvoices(1);
							}
							subscriptionService.updateSubscriptionData(subscriptionDetails);
						}
					}
					return gstnResponse;
				}else {
					Calendar cal = Calendar.getInstance();
					int pmonth = cal.get(Calendar.MONTH);
					int pyear = cal.get(Calendar.YEAR);
					int tdate = cal.get(Calendar.DATE);
					cal.set(pyear, pmonth, tdate, 0, 0,0);
					Date stDate = new java.util.Date(cal.getTimeInMillis());
					cal = Calendar.getInstance();
					cal.set(pyear, pmonth, tdate, 23, 59, 59);
					Date endDate = new java.util.Date(cal.getTimeInMillis());
					
					GSTINPublicData gstndata = gstinPublicDataRepository.findByGstinAndCreatedDateBetween(gstnum, stDate, endDate);
					if(isNotEmpty(gstndata)) {
						ResponseData data = new ResponseData();
						BeanUtils.copyProperties(gstndata, data);
						gstnResponse.setStatuscd(MasterGSTConstants.SUCCESS_CODE);
						gstnResponse.setData(data);
						//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
						//if(NullUtil.isNotEmpty(gstndataa)) {
							multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
						//}
						
						MultiGSTNData mgstdata = new MultiGSTNData();
						mgstdata.setUserid(id);
						mgstdata.setGstin(gstndata.getGstin());
						mgstdata.setDty(gstndata.getDty());
						mgstdata.setTradeNam(gstndata.getTradeNam());
						mgstdata.setRgdt(gstndata.getRgdt());
						mgstdata.setLstupdt(gstndata.getLstupdt());
						mgstdata.setStatus("VALID");
						mgstdata.setGstnid(gstndata.getId().toString());
						
						multiGSTnRepository.save(mgstdata);
						//List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
						
						//if(isNotEmpty(dupLst)) {
							allGSTnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstnum);	
						//}
					}else {
						Response response = iHubConsumerService.publicSearch(gstnum);
						gstnResponse = response;
						if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
								&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
							
							GSTINPublicData gstndataBasedonUserid = gstinPublicDataRepository.findByGstin(gstnum);
							if(NullUtil.isNotEmpty(gstndataBasedonUserid)) {
								gstinPublicDataRepository.delete(gstndataBasedonUserid.getId().toString());
							}
							//gstinPublicDataRepository.deleteByGstin(gstnum);
								
							GSTINPublicData publicData = new GSTINPublicData();
							BeanUtils.copyProperties(response.getData(), publicData);
							publicData.setCreatedDate(new Date());
							try {
								publicData.setIpAddress(InetAddress.getLocalHost().getHostAddress());
							} catch (UnknownHostException e) {
									
								e.printStackTrace();
							}
							/*
							 * publicData.setUserid(id); //User user = userService.findById(id); if
							 * (NullUtil.isNotEmpty(user.getParentid())) {
							 * publicData.setParentid(user.getParentid()); } publicData.setStatus("VALID");
							 */
							GSTINPublicData gstpubdata = gstinPublicDataRepository.save(publicData);
							
							//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
							//if(NullUtil.isNotEmpty(gstndataa)) {
								multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
							//}
							
							MultiGSTNData mgstdata = new MultiGSTNData();
							mgstdata.setUserid(id);
							mgstdata.setGstin(gstpubdata.getGstin());
							mgstdata.setDty(gstpubdata.getDty());
							mgstdata.setTradeNam(gstpubdata.getTradeNam());
							mgstdata.setRgdt(gstpubdata.getRgdt());
							mgstdata.setLstupdt(gstpubdata.getLstupdt());
							mgstdata.setStatus("VALID");
							mgstdata.setGstnid(gstpubdata.getId().toString());
							
							multiGSTnRepository.save(mgstdata);
							//AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
							//List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
							
							//if(isNotEmpty(dupLst)) {
								allGSTnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstnum);	
							//}
						}else {
							AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
							if(isNotEmpty(gstde)) {
								gstde.setStatus("Invalid GSTIN");
								gstde = profileService.saveGstno(gstde);
								
								//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
								//if(NullUtil.isNotEmpty(gstndataa)) {
									multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
								//}
								
								MultiGSTNData mulgstndata = new MultiGSTNData();
								mulgstndata.setGstin(gstnum);
								mulgstndata.setUserid(id);
								mulgstndata.setGstnid(gstde.getId().toString());
								mulgstndata.setStatus("Invalid GSTIN");
								multiGSTnRepository.save(mulgstndata);
							}

							
							
							//List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
							
							//if(isNotEmpty(dupLst)) {
								allGSTnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstnum);	
							//}
						}
						
						SubscriptionDetails subscriptionDetails = subscriptionService.getSubscriptionData(id);
						if(isNotEmpty(subscriptionDetails)) {
							if(isNotEmpty(subscriptionDetails.getProcessedInvoices())) {
								subscriptionDetails.setProcessedInvoices(
										subscriptionDetails.getProcessedInvoices() + 1);
							} else {
								subscriptionDetails.setProcessedInvoices(1);
							}
							subscriptionService.updateSubscriptionData(subscriptionDetails);
						}
					}
					return gstnResponse;
				}
			}else {
				AllGSTINTempData gstde = allGSTnoTempRepository.findByUseridAndGstno(id,gstnum);
				if(isNotEmpty(gstde)) {
					gstde.setStatus("Invalid GSTIN");
					gstde = profileService.saveGstno(gstde);
					//List<MultiGSTNData> gstndataa =  multiGSTnRepository.findByUseridAndGstin(id,gstnum);
					//if(NullUtil.isNotEmpty(gstndataa)) {
						multiGSTnRepository.deleteByUseridAndGstin(id,gstnum);
					//}
					
					MultiGSTNData mulgstndata = new MultiGSTNData();
					mulgstndata.setGstin(gstnum);
					mulgstndata.setUserid(id);
					mulgstndata.setGstnid(gstde.getId().toString());
					mulgstndata.setStatus("Invalid GSTIN");
					multiGSTnRepository.save(mulgstndata);
				}
				//List<AllGSTINTempData> dupLst = allGSTnoTempRepository.findByUseridAndGstnoAndStatusNull(id,gstnum);
				allGSTnoTempRepository.deleteByUseridAndGstnoAndStatusNull(id,gstnum);
				gstnResponse.setStatusdesc("Invalid GSTIN");
				return gstnResponse;
			}
	}else {
		gstnResponse.setStatuscd("0");
		return gstnResponse;
	}
	}
	
	@RequestMapping(value = "/getmultigstndetails/{id}/{name}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody String multigstndetails(@PathVariable("id") String id, @PathVariable("name") String fullname, 
			@PathVariable("usertype") String usertype, @PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "multigstndetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		String st = request.getParameter("start");
		int start = StringUtils.isEmpty(st) ? 0 : Integer.parseInt(st);
		String len = request.getParameter("length");
		int length = StringUtils.isEmpty(len) ? 10 :  Integer.parseInt(len);
		String searchVal = request.getParameter("search[value]");
		if(searchVal == null || "".equals(searchVal.trim())){
			searchVal = null;
		}
		Pageable pageable = null;
		Page<? extends MultiGSTNData> multigstnumberdata = profileService.getmultiGstnDetails(pageable,id,month, year, start, length,searchVal);
		if(isNotEmpty(multigstnumberdata)) {
			for(MultiGSTNData invoiceParent : multigstnumberdata) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;

			writer=mapper.writer();
	
		return writer.writeValueAsString(multigstnumberdata);
		
	} 


}
