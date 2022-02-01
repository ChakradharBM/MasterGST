package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isNotEmpty;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.UQCConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTRAdvanceTax;
import com.mastergst.usermanagement.runtime.domain.GSTRExportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.GSTRNilItems;
import com.mastergst.usermanagement.runtime.domain.HSNData;
import com.mastergst.usermanagement.runtime.domain.HSNDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoiceTypeSummery;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.ReturnsDownloadStatus;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BB2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BCDN;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocList;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPG;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPGSEZ;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BISD;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BItems;
import com.mastergst.usermanagement.runtime.response.GSTR2BResponse;
import com.mastergst.usermanagement.runtime.response.gstr2b.GSTR2BVO;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.GSTR2BRepository;
import com.mastergst.usermanagement.runtime.repository.HSNSummaryRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.InvoiceService;
import com.mastergst.usermanagement.runtime.service.OtpExpiryService;
import com.mastergst.usermanagement.runtime.support.Utility;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Controller
public class InvoiceSummeryController {

	private static final Logger logger = LogManager.getLogger(InvoiceSummeryController.class.getName());
	private static final String CLASSNAME = "InvoiceSummeryController::";
	@Autowired	private ClientService clientService;
	@Autowired	private InvoiceService invoiceService;
	@Autowired	private OtpExpiryService otpExpiryService;
	@Autowired	private GSTR2BRepository gstr2bRepository;
	@Autowired	ConfigService configService;
	@Autowired  private ResourceLoader resourceLoader;
	@Autowired  private	HSNSummaryRepository hsnSummaryRepository;
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	
	@RequestMapping(value = "/invoiceSummeryByTypeForMonth/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<InvoiceTypeSummery> invoiceSummeryByTypeForMonth(@PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, @RequestParam("booksOrReturns")String booksOrReturns,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		
		String booksorReturns = request.getParameter("booksOrReturns");
		if(isNotEmpty(booksorReturns)) {
			if(booksorReturns.equals(MasterGSTConstants.PROFORMAINVOICES) || booksorReturns.equals(MasterGSTConstants.DELIVERYCHALLANS) || booksorReturns.equals(MasterGSTConstants.ESTIMATES) || booksorReturns.equals("SalesRegister") || booksorReturns.equals(MasterGSTConstants.PURCHASEORDER) || booksorReturns.equals("PurchaseRegister")){
				booksorReturns = "books";
			}			
		}
		Client client = clientService.findById(clientid);
		if(isNotEmpty(client) && isNotEmpty(client.getInvoiceViewOption())) {
			
			if(client.getInvoiceViewOption().equalsIgnoreCase("Yearly")) {
				if(booksorReturns !="" && booksorReturns !=null && booksorReturns.equalsIgnoreCase("books")) {
					month = 0;
					++year;
				}
			}
		}
		List<InvoiceTypeSummery> summery = clientService.getInvoiceSummeryByTypeForMonth(clientid, returntype, month, year);
		return summery;
	}
	
	@RequestMapping(value = "/invoiceSummeryByGstStatusForMonth/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Integer> getTotalInvoicesByGststatus(@PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year, 
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "getInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		Map<String, Integer> totalInvoiceByStatus =  clientService.getTotalInvoicesByGststatus(clientid, returntype,month, year);
		return totalInvoiceByStatus;
	}
	@RequestMapping(value = "/addgstr2binvoice/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String gstr2bView(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "gstr2bView::";
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		String strMonth = month < 10 ? "0" + month : month + "";
		String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
		if(!otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {	
			model.addAttribute("otperror", "Y");
			model.addAttribute("error", "Unauthorized User!");
		}
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		return "client/add_gstr2B_invoice";
	}
	@RequestMapping(value = "/sync2binvs/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public String syncGstr2bInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "syncGstr2bInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String type = request.getParameter("type");
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		model.addAttribute("client", client);
		model.addAttribute("lGSTReturnsSummury", clientService.getGSTReturnsSummary(client, month, year, false));
		String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
		if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String returnPeriod = strMonth + year;
			GSTR2BResponse gstr2bresponse = invoiceService.syncGstr2bInvoice(client, id, returntype, returnPeriod, false);
			if(NullUtil.isNotEmpty(gstr2bresponse) && NullUtil.isNotEmpty(gstr2bresponse.getError())) {
				if(NullUtil.isNotEmpty(gstr2bresponse.getError().getMessage())) {
					model.addAttribute("error", gstr2bresponse.getError().getMessage());
				}
			}	
		}else{
			model.addAttribute("otperror", "Y");
			model.addAttribute("error", "Unauthorized User!");
		}
		if(isNotEmpty(type) && type.equalsIgnoreCase("gstr2b")) {
			return "redirect:/addgstr2binvoice/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/GSTR2B/" + month + "/" + year + "?type=gstr2b";
		}else {
			return "redirect:/alliview/" + id + "/" + fullname + "/" + usertype + "/" + clientid + "/GSTR2/" + month + "/" + year + "?type=gstr2b";
		}
	}
	
	@RequestMapping(value = "/download2binvs/{id}/{name}/{usertype}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody ReturnsDownloadStatus downloadGstr2bInvoices(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,@PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year, ModelMap model,HttpServletRequest request) throws Exception {
		final String method = "syncGstr2bInvoices::";
		logger.debug(CLASSNAME + method + BEGIN);
		String type = request.getParameter("type");
		updateModel(model, id, fullname, usertype, month, year);
		Client client = clientService.findById(clientid);
		String otpcheck = otpExpiryService.otpexpiry(client.getGstname());
		ReturnsDownloadStatus response = null;
		
		if(otpcheck.equalsIgnoreCase("OTP_VERIFIED")) {
			String strMonth = month < 10 ? "0" + month : month + "";
			String returnPeriod = strMonth + year;
			response = invoiceService.downloadGstr2bInvoice(client, id, returntype, returnPeriod, false);
			
		}else{
			response = new ReturnsDownloadStatus();
			response.setErrormsg("Unauthorized User!");
		}
		
		return response;
	}
	private void updateModel(ModelMap model, String id, String fullname, String usertype, int month, int year) {
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("month", month);
		model.addAttribute("year", year);
	}
	@RequestMapping(value = "/dwnldITCAvailableSummaryxls/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET, produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public @ResponseBody ResponseEntity<InputStreamResource> downloadExcelData(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year,HttpServletRequest request) throws IOException {
		String type = request.getParameter("type");
		Client client = clientService.findById(clientid);
		String gstnumber = "";
		if(NullUtil.isNotEmpty(client)){
			gstnumber = client.getGstnnumber();
		}
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		GSTR2B gstr2bdata = gstr2bRepository.findByClientidAndFp(clientid, returnPeriod);
		List<GSTR2BVO> gstr2bList = Lists.newArrayList();
		List<GSTR2BVO> gstr2bunavlList = Lists.newArrayList();
		List<InvoiceParent> invoices = Lists.newArrayList();
		if(isNotEmpty(type) && type.equalsIgnoreCase("gstr2b")) {
			gstr2bList = invoiceService.getGSTR2BData(clientid,returnPeriod,gstr2bdata);
			gstr2bunavlList = invoiceService.getGSTR2BITCunavlData(clientid, returnPeriod, gstr2bdata);
		}
		if(isNotEmpty(gstr2bdata) && isNotEmpty(gstr2bdata.getData())) {
			GSTR2BDocData docData = gstr2bdata.getData().getDocdata();
			try {
				invoices = convertGstr2bToInvoices(gstr2bdata.getFullname(), clientid, docData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		List<InvoiceParent> b2b = Lists.newArrayList();
		List<InvoiceParent> b2ba = Lists.newArrayList();
		List<InvoiceParent> cdn = Lists.newArrayList();
		List<InvoiceParent> cdna = Lists.newArrayList();
		List<InvoiceParent> isd = Lists.newArrayList();
		List<InvoiceParent> isda = Lists.newArrayList();
		List<InvoiceParent> impg = Lists.newArrayList();
		List<InvoiceParent> impgsez = Lists.newArrayList();
		for(InvoiceParent invoice : invoices) {
			if(isNotEmpty(invoice.getInvtype())) {
				if(invoice.getInvtype().equalsIgnoreCase("B2B")) {
					b2b.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase("B2BA")) {
					b2ba.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase("Credit Note") || invoice.getInvtype().equalsIgnoreCase("Debit Note")) {
					cdn.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase("Credit Note(CDNA)") || invoice.getInvtype().equalsIgnoreCase("Debit Note(CDNA)")) {
					cdna.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISD)) {
					isd.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ISDA)) {
					isda.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMPG)) {
					impg.add(invoice);
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMPGSEZ)) {
					impgsez.add(invoice);
				}
			}
		}
		ByteArrayInputStream in = invoiceService.itcToExcel(gstr2bList,gstr2bunavlList,b2b,b2ba,cdn,cdna,isd,isda,impg,impgsez,type);
	    HttpHeaders headers = new HttpHeaders();
	     headers.add("Content-Disposition", "attachment; filename='MGST_"+returntype+"_"+gstnumber+".xls");
	     return ResponseEntity.ok().headers(headers).body(new InputStreamResource(in));
	    }
	
	@RequestMapping(value = "/getGSTR2BData/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody GSTR2B getGstr2BData(ModelMap model, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("month") int month,@PathVariable("year") int year) {
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		GSTR2B invoice = gstr2bRepository.findByClientidAndFp(clientid,returnPeriod);
		return invoice;
	}
	
	@RequestMapping(value = "/getGstr2bData/{id}/{clientid}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<InvoiceParent> getGstr2bData(ModelMap model, @PathVariable("id") String id,@PathVariable("clientid") String clientid,
			@PathVariable("month") int month,@PathVariable("year") int year) {
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		GSTR2B invoice = gstr2bRepository.findByClientidAndFp(clientid,returnPeriod);
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getData())) {
			GSTR2BDocData docData = invoice.getData().getDocdata();
			try {
				return convertGstr2bToInvoices(invoice.getFullname(), clientid, docData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public List<InvoiceParent> convertGstr2bToInvoices(String fullname, String clientid, GSTR2BDocData docData) throws Exception {
		List<InvoiceParent> invList = null;
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		List<StateConfig> states = configService.getStates();
		InvoiceParent invParent = null;
		if(isNotEmpty(docData)){
			invList = new ArrayList<>();
			if(isNotEmpty(docData.getB2b())){
				for(GSTR2BB2B b2b : docData.getB2b()) {
					for(GSTR2BInvoiceDetails gstrInvoiceDetails : b2b.getInv()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						invParent.setInvtype(MasterGSTConstants.B2B);
						invParent.setGstin(b2b.getCtin());
						invParent.setBilledtoname(b2b.getTrdnm());
						invParent.setCustomField1(b2b.getSupfildt());
						invParent.setCustomField2(b2b.getSupprd());
						invParent.setRevchargetype(gstrInvoiceDetails.getRev());
						invParent.setTyp(gstrInvoiceDetails.getTyp());
						invParent.setItcavl(gstrInvoiceDetails.getItcavl());
						invParent.setInvoiceno(gstrInvoiceDetails.getInum());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDt()));
						if(isNotEmpty(gstrInvoiceDetails.getPos())) {
							for(StateConfig stateConfig : states) {
								if (gstrInvoiceDetails.getPos().equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
									if(isNotEmpty(stateConfig.getName())) {
										invParent.setStatename(stateConfig.getName());
									}
								}
							}
						}
						invParent.setCustomField3(gstrInvoiceDetails.getRsn());
						if(isNotEmpty(gstrInvoiceDetails.getDiffprcnt())) {
							invParent.setCustomField4(gstrInvoiceDetails.getDiffprcnt().toString());
						}
						consilidateInvoices(invParent, gstrInvoiceDetails);
						invList.add(invParent);
					}
				}
			}
			if(isNotEmpty(docData.getB2ba())) {
				for(GSTR2BB2B b2ba : docData.getB2ba()) {
					for(GSTR2BInvoiceDetails gstrInvoiceDetails : b2ba.getInv()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						invParent.setInvtype(MasterGSTConstants.B2BA);
						invParent.setGstin(b2ba.getCtin());
						invParent.setBilledtoname(b2ba.getTrdnm());
						invParent.setCustomField1(b2ba.getSupfildt());
						invParent.setCustomField2(b2ba.getSupprd());
						invParent.setRevchargetype(gstrInvoiceDetails.getRev());
						invParent.setTyp(gstrInvoiceDetails.getTyp());
						invParent.setItcavl(gstrInvoiceDetails.getItcavl());
						invParent.setInvoiceno(gstrInvoiceDetails.getInum());
						invParent.setFromGstin(gstrInvoiceDetails.getOinum());
						invParent.setToGstin(gstrInvoiceDetails.getOidt());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDt()));
						if(isNotEmpty(gstrInvoiceDetails.getPos())) {
							for(StateConfig stateConfig : states) {
								if (gstrInvoiceDetails.getPos().equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
									if(isNotEmpty(stateConfig.getName())) {
										invParent.setStatename(stateConfig.getName());
									}
								}
							}
						}
						invParent.setCustomField3(gstrInvoiceDetails.getRsn());
						if(isNotEmpty(gstrInvoiceDetails.getDiffprcnt())) {
							invParent.setCustomField4(gstrInvoiceDetails.getDiffprcnt().toString());
						}
						consilidateInvoices(invParent, gstrInvoiceDetails);
						invList.add(invParent);
					}
				}
			}
			
			if(isNotEmpty(docData.getCdnr()) ) {
				for(GSTR2BCDN cdn : docData.getCdnr()) {
					for(GSTR2BInvoiceDetails gstrInvoiceDetails : cdn.getNt()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						String type = null; 
						if(isNotEmpty(gstrInvoiceDetails.getTyp()) ) {
							type = gstrInvoiceDetails.getTyp().equals("C") ? "Credit Note" : "Debit Note";
						}
						invParent.setInvtype(type);
						invParent.setGstin(cdn.getCtin());
						invParent.setBilledtoname(cdn.getTrdnm());
						invParent.setCustomField1(cdn.getSupfildt());
						invParent.setCustomField2(cdn.getSupprd());
						invParent.setRevchargetype(gstrInvoiceDetails.getRev());
						invParent.setTyp(gstrInvoiceDetails.getTyp());
						invParent.setItcavl(gstrInvoiceDetails.getItcavl());
						invParent.setInvoiceno(gstrInvoiceDetails.getNtnum());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDt()));
						if(isNotEmpty(gstrInvoiceDetails.getPos())) {
							for(StateConfig stateConfig : states) {
								if (gstrInvoiceDetails.getPos().equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
									if(isNotEmpty(stateConfig.getName())) {
										invParent.setStatename(stateConfig.getName());
									}
								}
							}
						}
						invParent.setCustomField3(gstrInvoiceDetails.getRsn());
						if(isNotEmpty(gstrInvoiceDetails.getDiffprcnt())) {
							invParent.setCustomField4(gstrInvoiceDetails.getDiffprcnt().toString());
						}
						consilidateInvoices(invParent, gstrInvoiceDetails);
						invList.add(invParent);
					}					
				}
			}
			if(isNotEmpty(docData.getCdnra())) {
				for(GSTR2BCDN cdnra : docData.getCdnra()) {
					for(GSTR2BInvoiceDetails gstrInvoiceDetails : cdnra.getNt()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						String type = null; 
						if(isNotEmpty(gstrInvoiceDetails.getTyp()) ) {
							type = gstrInvoiceDetails.getTyp().equals("C") ? "Credit Note(CDNA)" : "Debit Note(CDNA)";
						}
						invParent.setInvtype(type);
						invParent.setGstin(cdnra.getCtin());
						invParent.setBilledtoname(cdnra.getTrdnm());
						invParent.setCustomField1(cdnra.getSupfildt());
						invParent.setCustomField2(cdnra.getSupprd());
						invParent.setRevchargetype(gstrInvoiceDetails.getRev());
						invParent.setTyp(gstrInvoiceDetails.getTyp());
						invParent.setItcavl(gstrInvoiceDetails.getItcavl());
						invParent.setInvoiceno(gstrInvoiceDetails.getNtnum());
						invParent.setFromGstin(gstrInvoiceDetails.getOntnum());
						invParent.setToGstin(gstrInvoiceDetails.getOntdt());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDt()));
						if(isNotEmpty(gstrInvoiceDetails.getPos())) {
							for(StateConfig stateConfig : states) {
								if (gstrInvoiceDetails.getPos().equals(stateConfig.getTin() < 10 ? "0" + stateConfig.getTin() : stateConfig.getTin() + "")) {
									if(isNotEmpty(stateConfig.getName())) {
										invParent.setStatename(stateConfig.getName());
									}
								}
							}
						}
						invParent.setCustomField3(gstrInvoiceDetails.getRsn());
						if(isNotEmpty(gstrInvoiceDetails.getDiffprcnt())) {
							invParent.setCustomField4(gstrInvoiceDetails.getDiffprcnt().toString());
						}
						consilidateInvoices(invParent, gstrInvoiceDetails);
						invList.add(invParent);
					}	
				}
			}
			if(isNotEmpty(docData.getIsd())) {
				for(GSTR2BISD isd : docData.getIsd()) {
					for(GSTR2BDocList gstrInvoiceDetails : isd.getDoclist()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						invParent.setInvtype(MasterGSTConstants.ISD);
						invParent.setGstin(isd.getCtin());
						invParent.setBilledtoname(isd.getTrdnm());
						invParent.setCustomField1(isd.getSupfildt());
						invParent.setCustomField2(isd.getSupprd());
						invParent.setInvoiceno(gstrInvoiceDetails.getDocnum());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDocdt()));
						invParent.setCustomField3(gstrInvoiceDetails.getOinvnum());
						invParent.setCustomField4(gstrInvoiceDetails.getOinvdt());
						invParent.setDocType(gstrInvoiceDetails.getDoctyp());
						Item items = new Item();
						Double igst =0d, cgst =0d, sgst =0d, cess =0d;
						Double totalTaxVal = 0d;
						Double totalTax = 0d;
						Double totalITC = 0d;
						Double totalamount = 0d;
						Double totalIgst =0d, totalCgst=0d, totalSgst=0d, totalCess =0d;
						if(isNotEmpty(gstrInvoiceDetails)) {
							if(isNotEmpty(gstrInvoiceDetails.getIgst())) {
								igst += gstrInvoiceDetails.getIgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getCgst())) {
								cgst += gstrInvoiceDetails.getCgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getSgst())) {
								sgst += gstrInvoiceDetails.getSgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getCess())) {
								cess += gstrInvoiceDetails.getCess();
							}
							items.setElg(gstrInvoiceDetails.getItcelg());
							invParent.setItcavl(gstrInvoiceDetails.getItcelg());
							totalamount = igst+cgst+sgst+cess;
							totalTax = igst+cgst+sgst+cess;
						}
						items.setIgstamount(igst);
						items.setCgstamount(cgst);
						items.setSgstamount(sgst);
						items.setCessamount(cess);
						invParent.setTotalamount(totalamount);
						List<Item> itmLst = new ArrayList<Item>();
						itmLst.add(items);
						invParent.setItems(itmLst);
						invParent.setTotaltaxableamount(totalTaxVal);
						invParent.setTotaltax(totalTax);
						invParent.setTotalitc(totalITC);
						invParent.setTotalIgstAmount(igst);
						invParent.setTotalCgstAmount(cgst);
						invParent.setTotalSgstAmount(sgst);
						invParent.setTotalCessAmount(cess);
						invList.add(invParent);
					}
				}
			}
			if(isNotEmpty(docData.getIsda())) {
				for(GSTR2BISD isda : docData.getIsda()) {
						
					for(GSTR2BDocList gstrInvoiceDetails : isda.getDoclist()) {
						invParent = new InvoiceParent();
						invParent.setFullname(fullname);
						invParent.setClientid(clientid);
						invParent.setInvtype(MasterGSTConstants.ISDA);
						invParent.setGstin(isda.getCtin());
						invParent.setBilledtoname(isda.getTrdnm());
						invParent.setCustomField1(isda.getSupfildt());
						invParent.setCustomField2(isda.getSupprd());
						invParent.setInvoiceno(gstrInvoiceDetails.getDocnum());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getDocdt()));
						invParent.setCustomField3(gstrInvoiceDetails.getOinvnum());
						invParent.setCustomField4(gstrInvoiceDetails.getOinvdt());
						invParent.setDocType(gstrInvoiceDetails.getDoctyp());
						invParent.setFromGstin(gstrInvoiceDetails.getOdocnum());
						invParent.setToGstin(gstrInvoiceDetails.getOdocdt());
						invParent.setAckDt(gstrInvoiceDetails.getOdoctyp());
						Item items = new Item();
						Double igst =0d, cgst =0d, sgst =0d, cess =0d;
						Double totalTaxVal = 0d;
						Double totalTax = 0d;
						Double totalITC = 0d;
						Double totalamount = 0d;
						Double totalIgst =0d, totalCgst=0d, totalSgst=0d, totalCess =0d;
						if(isNotEmpty(gstrInvoiceDetails)) {
							
							if(isNotEmpty(gstrInvoiceDetails.getIgst())) {
								igst += gstrInvoiceDetails.getIgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getCgst())) {
								cgst += gstrInvoiceDetails.getCgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getSgst())) {
								sgst += gstrInvoiceDetails.getSgst();
							}
							if(isNotEmpty(gstrInvoiceDetails.getCess())) {
								cess += gstrInvoiceDetails.getCess();
							}
							items.setElg(gstrInvoiceDetails.getItcelg());
							invParent.setItcavl(gstrInvoiceDetails.getItcelg());
							totalamount = igst+cgst+sgst+cess;
							totalTax = igst+cgst+sgst+cess;
						}
						items.setIgstamount(igst);
						items.setCgstamount(cgst);
						items.setSgstamount(sgst);
						items.setCessamount(cess);
						invParent.setTotalamount(totalamount);
						List<Item> itmLst = new ArrayList<Item>();
						itmLst.add(items);
						invParent.setItems(itmLst);
						invParent.setTotaltaxableamount(totalTaxVal);
						invParent.setTotaltax(totalTax);
						invParent.setTotalitc(totalITC);
						invParent.setTotalIgstAmount(igst);
						invParent.setTotalCgstAmount(cgst);
						invParent.setTotalSgstAmount(sgst);
						invParent.setTotalCessAmount(cess);
						invList.add(invParent);
					}
				}
			}
			
			if(isNotEmpty(docData.getImpg())) {
				for(GSTR2BIMPG impg : docData.getImpg()) {
						
					invParent = new InvoiceParent();
					invParent.setClientid(clientid);
					invParent.setGstin(null);
					invParent.setFullname(fullname);
					invParent.setBilledtoname(null);
					invParent.setInvtype(MasterGSTConstants.IMPG);
					
					invParent.setInvoiceno(impg.getBoenum());
					invParent.setDateofinvoice(sdf.parse(impg.getBoedt()));
					invParent.setCustomField1(impg.getPortcode());
					invParent.setCustomField2(impg.getRecdt());
					invParent.setCustomField3(impg.getIsamd());
					invParent.setCustomField4(impg.getRefdt());
					Item items = new Item();
					Double igst =0d, cgst =0d, sgst =0d, cess =0d, txval =0d;
					Double totalTaxVal = 0d;
					Double totalTax = 0d;
					Double totalITC = 0d;
					Double totalamount = 0d;
					Double totalIgst =0d, totalCgst=0d, totalSgst=0d, totalCess =0d;
					igst += impg.getIgst();
					cess += impg.getCess();
					items.setElg("N");
					totalamount = igst+cgst+sgst+cess;
					totalTax = igst+cgst+sgst+cess;
					totalTaxVal += impg.getTxval();
					items.setIgstamount(igst);
					items.setCgstamount(cgst);
					items.setSgstamount(sgst);
					items.setCessamount(cess);
					invParent.setTotalamount(totalamount);
					List<Item> itmLst = new ArrayList<Item>();
					itmLst.add(items);
					invParent.setItems(itmLst);
					invParent.setTotaltaxableamount(totalTaxVal);
					invParent.setTotaltax(totalTax);
					invParent.setTotalitc(totalITC);
					invParent.setTotalIgstAmount(igst);
					invParent.setTotalCgstAmount(cgst);
					invParent.setTotalSgstAmount(sgst);
					invParent.setTotalCessAmount(cess);
					invList.add(invParent);
				}
			}
			
			if(isNotEmpty(docData.getImpgsez())) {
				for(GSTR2BIMPGSEZ impgsez : docData.getImpgsez()) {
					for(GSTR2BIMPG gstrInvoiceDetails : impgsez.getBoe()) {
						invParent = new InvoiceParent();
						invParent.setClientid(clientid);
						invParent.setFullname(fullname);
						invParent.setGstin(impgsez.getCtin());
						invParent.setBilledtoname(impgsez.getTrdnm());
						invParent.setInvtype(MasterGSTConstants.IMPGSEZ);
						invParent.setInvoiceno(gstrInvoiceDetails.getBoenum());
						invParent.setDateofinvoice(sdf.parse(gstrInvoiceDetails.getBoedt()));
						invParent.setCustomField1(gstrInvoiceDetails.getPortcode());
						invParent.setCustomField2(gstrInvoiceDetails.getRecdt());
						invParent.setCustomField4(gstrInvoiceDetails.getRefdt());
						invParent.setCustomField3(gstrInvoiceDetails.getIsamd());
						Item items = new Item();
						Double igst =0d, cgst =0d, sgst =0d, cess =0d;
						Double totalTaxVal = 0d;
						Double totalTax = 0d;
						Double totalITC = 0d;
						Double totalamount = 0d;
						Double totalIgst =0d, totalCgst=0d, totalSgst=0d, totalCess =0d;
						if(isNotEmpty(gstrInvoiceDetails)) {
							
							igst += gstrInvoiceDetails.getIgst();
							cess += gstrInvoiceDetails.getCess();
							totalTaxVal += gstrInvoiceDetails.getTxval();
							items.setElg("N");
							totalamount = igst+cgst+sgst+cess+totalTaxVal;
							totalTax = igst+cgst+sgst+cess;
						}
						items.setIgstamount(igst);
						items.setCgstamount(cgst);
						items.setSgstamount(sgst);
						items.setCessamount(cess);
						invParent.setTotalamount(totalamount);
						List<Item> itmLst = new ArrayList<Item>();
						itmLst.add(items);
						invParent.setItems(itmLst);
						invParent.setTotaltaxableamount(totalTaxVal);
						invParent.setTotaltax(totalTax);
						invParent.setTotalitc(totalITC);
						invParent.setTotalIgstAmount(igst);
						invParent.setTotalCgstAmount(cgst);
						invParent.setTotalSgstAmount(sgst);
						invParent.setTotalCessAmount(cess);
						invList.add(invParent);
					}
				}
			}
		}
		return invList;
	}
	
	private void consilidateInvoices(InvoiceParent invParent, GSTR2BInvoiceDetails gstrInvoiceDetails) {
		Double totalTaxVal = 0d;
		Double totalTax = 0d;
		Double totalITC = 0d;
		Double totalamount = 0d;
		Double totalIgst =0d, totalCgst=0d, totalSgst=0d, totalCess =0d;
		List<Item> items = Lists.newArrayList();
		if(isNotEmpty(gstrInvoiceDetails) && isNotEmpty(gstrInvoiceDetails.getItems())) {
			totalamount += Double.parseDouble(gstrInvoiceDetails.getVal());
			for (GSTR2BItems gstrItem : gstrInvoiceDetails.getItems()) {
				Item item = new Item();
				item.setTaxablevalue(gstrItem.getTxval());
				item.setRateperitem(gstrItem.getTxval());
				item.setQuantity(Double.parseDouble("1"));
				item.setRate(gstrItem.getRt());
				totalTaxVal += gstrItem.getTxval();
				if (isNotEmpty(gstrItem.getIgst())) {
					item.setIgstamount(gstrItem.getIgst());
					item.setIgstrate(gstrItem.getRt());
					totalTax += gstrItem.getIgst();
					totalIgst += gstrItem.getIgst();
				}
				if (isNotEmpty(gstrItem.getCgst())) {
					item.setCgstamount(gstrItem.getCgst());
					item.setCgstrate(gstrItem.getRt() / 2);
					totalTax += gstrItem.getCgst();
					totalCgst += gstrItem.getCgst();
				}
				if (isNotEmpty(gstrItem.getSgst())) {
					item.setSgstamount(gstrItem.getSgst());
					item.setSgstrate(gstrItem.getRt() / 2);
					totalTax += gstrItem.getSgst();
					totalSgst += gstrItem.getSgst();
				}
				if (isNotEmpty(gstrItem.getCess())) {
					item.setCessamount(gstrItem.getCess());
					totalTax += gstrItem.getCess();
					totalCess += gstrItem.getCess();
				}
				if (gstrInvoiceDetails.getItems().size() == 1) { 
					item.setTotal(Double.parseDouble(gstrInvoiceDetails.getVal()));
				}else {
					item.setTotal(totalTaxVal + totalTax);
				}
				items.add(item);
			}
		}
		invParent.setTotalamount(totalamount);
		invParent.setItems(items);
		invParent.setTotaltaxableamount(totalTaxVal);
		invParent.setTotaltax(totalTax);
		invParent.setTotalitc(totalITC);
		invParent.setTotalIgstAmount(totalIgst);
		invParent.setTotalCgstAmount(totalCgst);
		invParent.setTotalSgstAmount(totalSgst);
		invParent.setTotalCessAmount(totalCess);
	}


	@RequestMapping(value = "/dwnldITCAvailableToPDF/{id}/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET,produces = MediaType.APPLICATION_PDF_VALUE)
	public @ResponseBody HttpEntity<byte[]> downloadPDFData(@PathVariable("id") String id,
			@PathVariable("clientid") String clientid, @PathVariable("returntype") String returntype, 
			@PathVariable("month") int month, @PathVariable("year") int year) throws IOException {
		try {	
			Client client = clientService.findById(clientid);
			String gstnumber = "";
			if(NullUtil.isNotEmpty(client)){
				gstnumber = client.getGstnnumber();
			}
			String filename = "MGST_"+returntype+"_"+gstnumber+".xls";
			String strMonth = month < 10 ? "0" + month : month + "";
			String returnPeriod = strMonth + year;
			GSTR2B gstr2bdata = gstr2bRepository.findByClientidAndFp(clientid, returnPeriod);
			List<GSTR2BVO> gstr2bavlList = invoiceService.getGSTR2BData(clientid,returnPeriod,gstr2bdata);
			List<GSTR2BVO> gstr2bunavlList = invoiceService.getGSTR2BITCunavlData(clientid, returnPeriod, gstr2bdata);
			
			String xmlPath = "classpath:/report/itcpdf.jrxml";
			Map<String, Object> params = invoiceService.getITCReportParams(client, gstr2bavlList,gstr2bunavlList);
			Resource config = resourceLoader.getResource(xmlPath);
			// get report file and then load into jasperDesign
			JasperDesign jasperDesign = JRXmlLoader.load(config.getInputStream());
			// compile the jasperDesign
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			params.put(JRParameter.REPORT_LOCALE, new Locale("en", "IN")); 
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,new JRBeanCollectionDataSource(gstr2bavlList));
			// export to pdf
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			HttpHeaders header = new HttpHeaders();
			header.setContentType(MediaType.APPLICATION_PDF);
			header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+filename+".pdf");
			header.setContentLength(data.length);
			logger.debug("END :: printITCPdf");
			return new HttpEntity<byte[]>(data, header);
		}catch (Exception e) {
			logger.error("printITCPdf :: id "+id, e);
			return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
		}
	}
	@RequestMapping(value = "/getInvoiceHSNSummary/{clientid}/{returntype}/{month}/{year}", method = RequestMethod.GET)
	public @ResponseBody List<HSNData> invoiceHSNSummery(@PathVariable("returntype") String returntype, 
			@PathVariable("clientid") String clientid, @PathVariable("month") int month, @PathVariable("year") int year,
			ModelMap model, HttpServletRequest request) throws Exception {
		final String method = "invoiceHSNSummery::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		List<HSNData> hsnList = getHSNSummaryDetails(client,month,year,returntype);
		return hsnList;
	}

	private List<HSNData> getHSNSummaryDetails(Client client,int iMonth,int iYear,String returntype) {
		List<HSNData> hsnDataList = Lists.newArrayList();
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		List<UQCConfig> uqcList = configService.getUQCs();
		String strMonth = iMonth < 10 ? "0" + iMonth : iMonth + "";
		String fp = strMonth + iYear;
		List<String> yrcode = Lists.newArrayList();
		List<String> monthcode = Lists.newArrayList();
		monthcode.add("04");
		yrcode.add("2021-2022");
		String yearCode=Utility.getYearCode(iMonth, iYear);
		HSNDetails hsndetails = hsnSummaryRepository.findByClientidAndReturnPeriodAndReturnType(client.getId().toString(),fp,returntype);
		if(isNotEmpty(hsndetails) && hsndetails.getHsnData().size()>0) {
			if(yrcode.contains(yearCode) && !monthcode.contains(strMonth)) {
				if(isNotEmpty(hsndetails.getImporttype()) && !"Tally".equalsIgnoreCase(hsndetails.getImporttype())) {
					Map<String,HSNData> data = Maps.newHashMap();
					List<String> hsnlist = Lists.newArrayList();
					for(HSNData hsndata : hsndetails.getHsnData()) {
						if(isNotEmpty(data)) {
							HSNData hsndt = data.get(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt());
							if(isNotEmpty(hsndt)) {
								if(isNotEmpty(hsndata.getTxval())) {
									hsndt.setTxval(hsndt.getTxval()+Double.parseDouble(df2.format(hsndata.getTxval())));
								}
								if(isNotEmpty(hsndata.getVal())) {
									hsndt.setVal(0d);
								}
								if(isNotEmpty(hsndata.getIamt())) {
									hsndt.setIamt(hsndt.getIamt()+Double.parseDouble(df2.format(hsndata.getIamt())));
								}
								if(isNotEmpty(hsndata.getCamt())) {
									hsndt.setCamt(hsndt.getCamt()+Double.parseDouble(df2.format(hsndata.getCamt())));
								}
								if(isNotEmpty(hsndata.getSamt())) {
									hsndt.setSamt(hsndt.getSamt()+Double.parseDouble(df2.format(hsndata.getSamt())));
								}
								if(isNotEmpty(hsndata.getCsamt())) {
									hsndt.setCsamt(hsndt.getCsamt()+Double.parseDouble(df2.format(hsndata.getCsamt())));
								}
								data.put(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt(), hsndt);
							}else {
								if(isNotEmpty(hsndata.getTxval())) {
									hsndata.setTxval(Double.parseDouble(df2.format(hsndata.getTxval())));
								}
								if(isNotEmpty(hsndata.getVal())) {
									hsndata.setVal(0d);
								}
								if(isNotEmpty(hsndata.getIamt())) {
									hsndata.setIamt(Double.parseDouble(df2.format(hsndata.getIamt())));
								}
								if(isNotEmpty(hsndata.getCamt())) {
									hsndata.setCamt(Double.parseDouble(df2.format(hsndata.getCamt())));
								}
								if(isNotEmpty(hsndata.getSamt())) {
									hsndata.setSamt(Double.parseDouble(df2.format(hsndata.getSamt())));
								}
								if(isNotEmpty(hsndata.getCsamt())) {
									hsndata.setCsamt(Double.parseDouble(df2.format(hsndata.getCsamt())));
								}
								hsnlist.add(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt());
								data.put(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt(), hsndata);
							}
						}else {
							if(isNotEmpty(hsndata.getTxval())) {
								hsndata.setTxval(Double.parseDouble(df2.format(hsndata.getTxval())));
							}
							if(isNotEmpty(hsndata.getVal())) {
								hsndata.setVal(0d);
							}
							if(isNotEmpty(hsndata.getIamt())) {
								hsndata.setIamt(Double.parseDouble(df2.format(hsndata.getIamt())));
							}
							if(isNotEmpty(hsndata.getCamt())) {
								hsndata.setCamt(Double.parseDouble(df2.format(hsndata.getCamt())));
							}
							if(isNotEmpty(hsndata.getSamt())) {
								hsndata.setSamt(Double.parseDouble(df2.format(hsndata.getSamt())));
							}
							if(isNotEmpty(hsndata.getCsamt())) {
								hsndata.setCsamt(Double.parseDouble(df2.format(hsndata.getCsamt())));
							}
							hsnlist.add(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt());
							data.put(hsndata.getHsnSc()+hsndata.getUqc()+hsndata.getRt(), hsndata);
						}
					}
					 hsnDataList = Lists.newArrayList();
					for(String hsnkey : hsnlist) {
						hsnDataList.add(data.get(hsnkey));
					}
				}else {
					hsnDataList = Lists.newArrayList();
				}
			}else {
				for(HSNData hsndata : hsndetails.getHsnData()) {
					if(isNotEmpty(hsndata.getTxval())) {
						hsndata.setTxval(Double.parseDouble(df2.format(hsndata.getTxval())));
					}
					if(isNotEmpty(hsndata.getVal())) {
						hsndata.setVal(Double.parseDouble(df2.format(hsndata.getVal())));
					}
					if(isNotEmpty(hsndata.getIamt())) {
						hsndata.setIamt(Double.parseDouble(df2.format(hsndata.getIamt())));
					}
					if(isNotEmpty(hsndata.getCamt())) {
						hsndata.setCamt(Double.parseDouble(df2.format(hsndata.getCamt())));
					}
					if(isNotEmpty(hsndata.getSamt())) {
						hsndata.setSamt(Double.parseDouble(df2.format(hsndata.getSamt())));
					}
					if(isNotEmpty(hsndata.getCsamt())) {
						hsndata.setCsamt(Double.parseDouble(df2.format(hsndata.getCsamt())));
					}
				}
				hsnDataList = hsndetails.getHsnData();
			}
		}else {
			Page<? extends InvoiceParent> savedInvoices = null;
			if (returntype.equals(GSTR1)) {
				savedInvoices = clientService.getInvoiceBasedOnClientidAndFP(null, client.getId().toString(), returntype, fp);
				if(isNotEmpty(savedInvoices.getContent())) {
					for (InvoiceParent invoiceParent : savedInvoices) {
						if(yrcode.contains(yearCode) && !monthcode.contains(strMonth)) {
							clientService.populateNewHSNList(hsnDataList, invoiceParent, hsnMap, sacMap, uqcList, returntype);
						}else {
							clientService.populateHSNList(hsnDataList, invoiceParent, hsnMap, sacMap, uqcList, returntype);
						}
					}
				}
			}
		}
		
		
		
		
		
		return hsnDataList;
    }
	
}
