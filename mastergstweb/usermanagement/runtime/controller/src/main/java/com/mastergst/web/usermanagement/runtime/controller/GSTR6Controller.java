package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.math.BigDecimal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6Details;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6DocDetails;
import com.mastergst.usermanagement.runtime.domain.gstr6.GSTR6ISD;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.GSTR6Utils;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;
import com.mastergst.usermanagement.runtime.service.ProfileService;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
@Controller
public class GSTR6Controller {
	private static final Logger logger = LogManager.getLogger(GSTR6Controller.class.getName());
	private static final String CLASSNAME = "GSTR6Controller::";
	@Autowired	private UserService userService;
	@Autowired	private ClientService clientService;
	@Autowired	private ProfileService profileService;
	@Autowired	private SubscriptionService subscriptionService;
	@Autowired	private OtpExpiryService otpExpiryService;
	@Autowired	private UserRepository userRepository;
	@Autowired	private GSTR6Utils gstr6Utils;
	@Autowired	ClientUserMappingRepository clientUserMappingRepository;
	
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	@RequestMapping(value = "/addGSTR6invoice/{id}/{name}/{usertype}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public String addGSTR6AnnualInvoice(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "addGSTR6AnnaulInvoice::";
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
		String strMonth =  month<10 ? "0"+month : month+"";
		String retPeriod = strMonth+year;
		model.addAttribute("returntype", GSTR6);
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		ClientStatus clientStatus = clientService.getClientStatus(clientid, GSTR6, retPeriod);
		if (isNotEmpty(clientStatus)) {
			client.setStatus(clientStatus.getStatus());
		}
		
		TotalInvoiceAmount b2b = null;
		TotalInvoiceAmount b2ba = null;
		TotalInvoiceAmount cdnr = null;
		TotalInvoiceAmount cdnra= null;
		TotalInvoiceAmount isdElg = null;
		TotalInvoiceAmount isdInElg = null;
		TotalInvoiceAmount isdaElg= null;
		TotalInvoiceAmount isdaInElg= null;
		GSTR6Details gstr6Details = gstr6Utils.getGSTR6Invoice(clientid, retPeriod);
		if(isNotEmpty(gstr6Details)) {
			if(isNotEmpty(gstr6Details.getB2b())) {
				b2b = new TotalInvoiceAmount();
				List<GSTRB2B> b2binv = gstr6Details.getB2b();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(b2binv)) {
					transactions = b2binv.size();
					for(GSTRB2B inv : b2binv) {
						for(GSTRInvoiceDetails invdetails : inv.getInv()) {
							for(GSTRItems item : invdetails.getItms()) {
								if(isNotEmpty(item.getItem())) {
									totalTaxableAmount += item.getItem().getTxval();
									if(isNotEmpty(item.getItem().getIamt())) {
										totalIGSTAmount += item.getItem().getIamt();
									}
									if(isNotEmpty(item.getItem().getCamt())) {
										totalCGSTAmount += item.getItem().getCamt();
									}
									if(isNotEmpty(item.getItem().getSamt())) {
										totalSGSTAmount += item.getItem().getSamt();
									}
									if(isNotEmpty(item.getItem().getCsamt())) {
										totalCESSAmount += item.getItem().getCsamt();	
									}
								}
							}
					   }
					}
				}
				b2b.setTotalTransactions(transactions);
				b2b.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				b2b.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2b.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2b.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2b.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			if(isNotEmpty(gstr6Details.getB2ba())) {
				b2ba = new TotalInvoiceAmount();
				List<GSTRB2B> b2bainv = gstr6Details.getB2ba();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(b2bainv)) {
					transactions = b2bainv.size();
					for(GSTRB2B inv : b2bainv) {
						for(GSTRInvoiceDetails invdetails : inv.getInv()) {
							for(GSTRItems item : invdetails.getItms()) {
								if(isNotEmpty(item.getItem())) {
									totalTaxableAmount += item.getItem().getTxval();
									if(isNotEmpty(item.getItem().getIamt())) {
										totalIGSTAmount += item.getItem().getIamt();
									}
									if(isNotEmpty(item.getItem().getCamt())) {
										totalCGSTAmount += item.getItem().getCamt();
									}
									if(isNotEmpty(item.getItem().getSamt())) {
										totalSGSTAmount += item.getItem().getSamt();
									}
									if(isNotEmpty(item.getItem().getCsamt())) {
										totalCESSAmount += item.getItem().getCsamt();	
									}
								}
							}
					   }
					}
				}
				b2ba.setTotalTransactions(transactions);
				b2ba.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				b2ba.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2ba.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2ba.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				b2ba.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			if(isNotEmpty(gstr6Details.getCdn())) {
				cdnr = new TotalInvoiceAmount();
				List<GSTRCreditDebitNotes> cdninv = gstr6Details.getCdn();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(cdninv)) {
					transactions = cdninv.size();
					for(GSTRCreditDebitNotes inv : cdninv) {
						for(GSTRInvoiceDetails invdetails : inv.getNt()) {
							for(GSTRItems item : invdetails.getItms()) {
								if(isNotEmpty(item.getItem())) {
									totalTaxableAmount += item.getItem().getTxval();
									if(isNotEmpty(item.getItem().getIamt())) {
										totalIGSTAmount += item.getItem().getIamt();
									}
									if(isNotEmpty(item.getItem().getCamt())) {
										totalCGSTAmount += item.getItem().getCamt();
									}
									if(isNotEmpty(item.getItem().getSamt())) {
										totalSGSTAmount += item.getItem().getSamt();
									}
									if(isNotEmpty(item.getItem().getCsamt())) {
										totalCESSAmount += item.getItem().getCsamt();	
									}
								}
							}
					   }
					}
				}
				cdnr.setTotalTransactions(transactions);
				cdnr.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				cdnr.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnr.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnr.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnr.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			if(isNotEmpty(gstr6Details.getCdna())) {
				cdnra = new TotalInvoiceAmount();
				List<GSTRCreditDebitNotes> cdnainv = gstr6Details.getCdna();
				int transactions = 0;
				Double totalAmount = 0d, totalTaxableAmount = 0d, totalTaxAmount = 0d, totalIGSTAmount = 0d;
				Double totalCGSTAmount = 0d, totalSGSTAmount = 0d, totalCESSAmount = 0d;
				
				if(isNotEmpty(cdnainv)) {
					transactions = cdnainv.size();
					for(GSTRCreditDebitNotes inv : cdnainv) {
						for(GSTRInvoiceDetails invdetails : inv.getNt()) {
							for(GSTRItems item : invdetails.getItms()) {
								if(isNotEmpty(item.getItem())) {
									totalTaxableAmount += item.getItem().getTxval();
									if(isNotEmpty(item.getItem().getIamt())) {
										totalIGSTAmount += item.getItem().getIamt();
									}
									if(isNotEmpty(item.getItem().getCamt())) {
										totalCGSTAmount += item.getItem().getCamt();
									}
									if(isNotEmpty(item.getItem().getSamt())) {
										totalSGSTAmount += item.getItem().getSamt();
									}
									if(isNotEmpty(item.getItem().getCsamt())) {
										totalCESSAmount += item.getItem().getCsamt();	
									}
								}
							}
					   }
					}
				}
				cdnra.setTotalTransactions(transactions);
				cdnra.setTotalTaxableAmount(BigDecimal.valueOf(totalTaxableAmount));
				cdnra.setTotalIGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnra.setTotalCGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnra.setTotalSGSTAmount(BigDecimal.valueOf(totalIGSTAmount));
				cdnra.setTotalCESSAmount(BigDecimal.valueOf(totalCESSAmount));
			}
			if(isNotEmpty(gstr6Details.getIsd())) {
				isdElg = new TotalInvoiceAmount();
				isdInElg = new TotalInvoiceAmount();
				List<GSTR6ISD> isdinv = gstr6Details.getIsd();
				int transactions = 0;int inelgtransaction = 0;
				Double totalElgCGSTAmount = 0d, totalElgSGSTAmount = 0d, totalElgCESSAmount = 0d,totalElgIGSTAmount = 0d;
				Double totalInElgCGSTAmount = 0d, totalInElgSGSTAmount = 0d, totalInElgCESSAmount = 0d,totalInElgIGSTAmount = 0d;
				
				if(isNotEmpty(isdinv)) {
					//transactions = isdinv.size();
					for(GSTR6ISD inv : isdinv) {
							for(GSTR6DocDetails item : inv.getElglst().getDoclst()) {
								transactions = inv.getElglst().getDoclst().size();
								if(isNotEmpty(item.getIamti())) {
									totalElgIGSTAmount += item.getIamti();
								}
								if(isNotEmpty(item.getSamti())) {
									totalElgIGSTAmount += item.getSamti();
								}
								if(isNotEmpty(item.getCamti())) {
									totalElgIGSTAmount += item.getCamti();
								}
								if(isNotEmpty(item.getIamtc())) {
									totalElgCGSTAmount += item.getIamtc();
								}
								if(isNotEmpty(item.getCamtc())) {
									totalElgCGSTAmount += item.getCamtc();
								}
								if(isNotEmpty(item.getIamts())) {
									totalElgSGSTAmount += item.getIamts();
								}
								if(isNotEmpty(item.getSamts())) {
									totalElgSGSTAmount += item.getSamts();
								}
								if(isNotEmpty(item.getCsamt())) {
									totalElgCESSAmount += item.getCsamt();	
								}
							}
							for(GSTR6DocDetails item : inv.getInelglst().getDoclst()) {
								inelgtransaction = inv.getElglst().getDoclst().size();
								if(isNotEmpty(item.getIamti())) {
									totalInElgIGSTAmount += item.getIamti();
								}
								if(isNotEmpty(item.getSamti())) {
									totalInElgIGSTAmount += item.getSamti();
								}
								if(isNotEmpty(item.getCamti())) {
									totalInElgIGSTAmount += item.getCamti();
								}
								if(isNotEmpty(item.getIamtc())) {
									totalInElgCGSTAmount += item.getIamtc();
								}
								if(isNotEmpty(item.getCamtc())) {
									totalInElgCGSTAmount += item.getCamtc();
								}
								if(isNotEmpty(item.getIamts())) {
									totalInElgSGSTAmount += item.getIamts();
								}
								if(isNotEmpty(item.getSamts())) {
									totalInElgSGSTAmount += item.getSamts();
								}
								if(isNotEmpty(item.getCsamt())) {
									totalInElgCESSAmount += item.getCsamt();	
								}
							}
					   
					}
				}
				isdElg.setTotalTransactions(transactions);
				isdElg.setTotalIGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdElg.setTotalCGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdElg.setTotalSGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdElg.setTotalCESSAmount(BigDecimal.valueOf(totalElgCESSAmount));
				
				isdInElg.setTotalTransactions(inelgtransaction);
				isdInElg.setTotalIGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdInElg.setTotalCGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdInElg.setTotalSGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdInElg.setTotalCESSAmount(BigDecimal.valueOf(totalInElgCESSAmount));
			}
			if(isNotEmpty(gstr6Details.getIsda())) {
				isdaElg = new TotalInvoiceAmount();
				isdaInElg = new TotalInvoiceAmount();
				List<GSTR6ISD> isdinv = gstr6Details.getIsda();
				int transactions = 0;int inelgtransactions = 0;
				Double totalElgCGSTAmount = 0d, totalElgSGSTAmount = 0d, totalElgCESSAmount = 0d,totalElgIGSTAmount = 0d;
				Double totalInElgCGSTAmount = 0d, totalInElgSGSTAmount = 0d, totalInElgCESSAmount = 0d,totalInElgIGSTAmount = 0d;
				
				if(isNotEmpty(isdinv)) {
					//transactions = isdinv.size();
					for(GSTR6ISD inv : isdinv) {
							for(GSTR6DocDetails item : inv.getElglst().getDoclst()) {
								transactions = inv.getElglst().getDoclst().size();
								if(isNotEmpty(item.getIamti())) {
									totalElgIGSTAmount += item.getIamti();
								}
								if(isNotEmpty(item.getSamti())) {
									totalElgIGSTAmount += item.getSamti();
								}
								if(isNotEmpty(item.getCamti())) {
									totalElgIGSTAmount += item.getCamti();
								}
								if(isNotEmpty(item.getIamtc())) {
									totalElgCGSTAmount += item.getIamtc();
								}
								if(isNotEmpty(item.getCamtc())) {
									totalElgCGSTAmount += item.getCamtc();
								}
								if(isNotEmpty(item.getIamts())) {
									totalElgSGSTAmount += item.getIamts();
								}
								if(isNotEmpty(item.getSamts())) {
									totalElgSGSTAmount += item.getSamts();
								}
								if(isNotEmpty(item.getCsamt())) {
									totalElgCESSAmount += item.getCsamt();	
								}
							}
							for(GSTR6DocDetails item : inv.getInelglst().getDoclst()) {
								inelgtransactions = inv.getElglst().getDoclst().size();
								if(isNotEmpty(item.getIamti())) {
									totalInElgIGSTAmount += item.getIamti();
								}
								if(isNotEmpty(item.getSamti())) {
									totalInElgIGSTAmount += item.getSamti();
								}
								if(isNotEmpty(item.getCamti())) {
									totalInElgIGSTAmount += item.getCamti();
								}
								if(isNotEmpty(item.getIamtc())) {
									totalInElgCGSTAmount += item.getIamtc();
								}
								if(isNotEmpty(item.getCamtc())) {
									totalInElgCGSTAmount += item.getCamtc();
								}
								if(isNotEmpty(item.getIamts())) {
									totalInElgSGSTAmount += item.getIamts();
								}
								if(isNotEmpty(item.getSamts())) {
									totalInElgSGSTAmount += item.getSamts();
								}
								if(isNotEmpty(item.getCsamt())) {
									totalInElgCESSAmount += item.getCsamt();	
								}
							}
					   
					}
				}
				isdaElg.setTotalTransactions(transactions);
				isdaElg.setTotalIGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdaElg.setTotalCGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdaElg.setTotalSGSTAmount(BigDecimal.valueOf(totalElgIGSTAmount));
				isdaElg.setTotalCESSAmount(BigDecimal.valueOf(totalElgCESSAmount));
				
				isdaInElg.setTotalTransactions(inelgtransactions);
				isdaInElg.setTotalIGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdaInElg.setTotalCGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdaInElg.setTotalSGSTAmount(BigDecimal.valueOf(totalInElgIGSTAmount));
				isdaInElg.setTotalCESSAmount(BigDecimal.valueOf(totalInElgCESSAmount));
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
		InvoiceParent syncData = gstr6Utils.syncGstr6(user, client, retPeriod,month,year);
		model.addAttribute("syncData", syncData);
		model.addAttribute("b2bAmts", b2b);
		model.addAttribute("b2baAmts", b2ba);
		model.addAttribute("cdnAmts", cdnr);
		model.addAttribute("cdnaAmts", cdnra);
		model.addAttribute("isdAmts", isdElg);
		model.addAttribute("isdInElgAmts", isdInElg);
		model.addAttribute("isdaAmts", isdaElg);
		model.addAttribute("isdaInElgAmts", isdaInElg);
		logger.debug(CLASSNAME + method + END);
		//System.out.println(gstr6);
		return "client/add_gstr6_invoice";
	}
	@RequestMapping(value = "/getpopulategstr6data/{id}/{clientid}/{returntype}/{usertype}/{month}/{year}", method = RequestMethod.GET)
	public String autoPopulateGstr6Data(@PathVariable("id") String id, @PathVariable("returntype") String returntype,@PathVariable("usertype") String usertype,  
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, ModelMap model) throws Exception {
		final String method = "autoPopulateGstr6Data::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + id);		
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		User user = userService.findById(id);
		String fullname="";
		if(isNotEmpty(user) && isNotEmpty(user.getFullname())) {
			fullname = user.getFullname();
		}
			Client client = clientService.findById(clientid);
			String strMonth =  month<10 ? "0"+month : month+"";
			String retPeriod = strMonth+year;
			gstr6Utils.deleteGSTR6Data(clientid,retPeriod);
			
			ClientStatus clientStatus = clientService.getClientStatus(clientid, GSTR6, retPeriod);
			if (isNotEmpty(clientStatus)) {
				client.setStatus(clientStatus.getStatus());
			}
			GSTR6Details details = new GSTR6Details();
			details.setClientid(clientid);
			details.setUserid(id);
			details.setFullname(fullname);
			details.setGstin(client.getGstnnumber());
			details.setFp(retPeriod);
			gstr6Utils.autoCalculateData(details, client, month,year);
			
			InvoiceParent syncData = gstr6Utils.syncGstr6(user, client, retPeriod,month,year);
			model.addAttribute("syncData", syncData);
		
		logger.debug(CLASSNAME + method + END);
		return "redirect:/addGSTR6invoice/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/" + month + "/" + year;
	}
	@RequestMapping(value = "/populateInvoiceData/{id}/{clientid}/{month}/{year}")
	public @ResponseBody GSTR6Details populateInvoiceWiseData(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid,@PathVariable("month") int month, @PathVariable("year") int year,@RequestParam("type")String type, ModelMap model) throws Exception {
		final String method = "populateInvoiceWiseData::";
		logger.debug(CLASSNAME + method + BEGIN);
		String strMonth =  month<10 ? "0"+month : month+"";
		String retPeriod = strMonth+year;
		GSTR6Details gstr6Details = gstr6Utils.getGSTR6Invoice(clientid, retPeriod);
		if(isNotEmpty(gstr6Details)) {
			return gstr6Details;
		}
		logger.debug(CLASSNAME + method + END);
		return null;
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
