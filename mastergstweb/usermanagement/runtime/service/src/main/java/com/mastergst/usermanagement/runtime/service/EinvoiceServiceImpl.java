package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.ADVANCES;
import static com.mastergst.core.common.MasterGSTConstants.ANX1;
import static com.mastergst.core.common.MasterGSTConstants.ATPAID;
import static com.mastergst.core.common.MasterGSTConstants.B2B;
import static com.mastergst.core.common.MasterGSTConstants.B2C;
import static com.mastergst.core.common.MasterGSTConstants.B2CL;
import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.common.MasterGSTConstants.CREDIT_DEBIT_NOTES;
import static com.mastergst.core.common.MasterGSTConstants.EXCEL_TEMPLATE;
import static com.mastergst.core.common.MasterGSTConstants.EXPORTS;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.GSTR4;
import static com.mastergst.core.common.MasterGSTConstants.GSTR5;
import static com.mastergst.core.common.MasterGSTConstants.GSTR6;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.reader.ReaderBuilder;
import org.jxls.reader.ReaderConfig;
import org.jxls.reader.XLSReadStatus;
import org.jxls.reader.XLSReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.UQCConfig;
import com.mastergst.configuration.service.UQCRepository;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.HeaderKeys;
import com.mastergst.login.runtime.repository.HeaderKeysRepository;
import com.mastergst.usermanagement.runtime.dao.EinvoiceDao;
import com.mastergst.usermanagement.runtime.dao.Gstr1Dao;
import com.mastergst.usermanagement.runtime.domain.AddlDocDetails;
import com.mastergst.usermanagement.runtime.domain.BatchDetails;
import com.mastergst.usermanagement.runtime.domain.BuyerDetails;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientAddlInfo;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanyInvoices;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.DispatcherDetails;
import com.mastergst.usermanagement.runtime.domain.DocDetails;
import com.mastergst.usermanagement.runtime.domain.EINVOICE;
import com.mastergst.usermanagement.runtime.domain.EinvoiceConfigurations;
import com.mastergst.usermanagement.runtime.domain.EinvoiceItem;
import com.mastergst.usermanagement.runtime.domain.EwayBillDetails;
import com.mastergst.usermanagement.runtime.domain.ExportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTINPublicData;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.GSTR6;
import com.mastergst.usermanagement.runtime.domain.GSTRB2B;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CL;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CS;
import com.mastergst.usermanagement.runtime.domain.GSTRCreditDebitNotes;
import com.mastergst.usermanagement.runtime.domain.GSTRExportDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRExports;
import com.mastergst.usermanagement.runtime.domain.GSTRITCDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRInvoiceDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GSTRItems;
import com.mastergst.usermanagement.runtime.domain.ImportResponse;
import com.mastergst.usermanagement.runtime.domain.ImportSummary;
import com.mastergst.usermanagement.runtime.domain.InvoiceFilter;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PayeeDetails;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.ReferenceDetails;
import com.mastergst.usermanagement.runtime.domain.SellerDetails;
import com.mastergst.usermanagement.runtime.domain.ShipmentDetails;
import com.mastergst.usermanagement.runtime.domain.TemplateMapper;
import com.mastergst.usermanagement.runtime.domain.TemplateMapperDoc;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.domain.TransportDetails;
import com.mastergst.usermanagement.runtime.domain.ValueDetails;
import com.mastergst.usermanagement.runtime.repository.ClientAddlInfoRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.EinvoiceConfigurationRepository;
import com.mastergst.usermanagement.runtime.repository.GSTINPublicDataRepository;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.response.GenerateIRNResponse;
import com.mastergst.usermanagement.runtime.response.Response;
import com.mastergst.usermanagement.runtime.response.ResponseData;
import com.mastergst.usermanagement.runtime.support.Utility;
import com.mastergst.usermanagement.runtime.support.WorkbookUtility;

@Service
@Transactional(readOnly = true)
public class EinvoiceServiceImpl  implements EinvoiceService {

	private static final Logger logger = LogManager.getLogger(EinvoiceServiceImpl.class.getName());
	private static final String CLASSNAME = "EinvoiceServiceImpl::";
	
	private static SimpleDateFormat dateFormatOnlyDate = new SimpleDateFormat("dd/MM/yyyy");
	private static String DOUBLE_FORMAT  = "%.2f";
	private String gstnFormat = "[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}|[0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1}|[0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1}|[0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1}|[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}|[9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1}";
	 private static DecimalFormat df2 = new DecimalFormat("#.##");
	private static final String INVOICENO_REGEX ="^[0-9a-zA-Z/-]{1,16}$";
	@Value("${ihub.email}")
	private String iHubEmail;
	@Value("${einvoice.version.prod}")
	private String einvoiceversion;
	@Autowired
	private NewimportService newimportService;
	@Autowired
	private InventoryService inventoryService;
	@Autowired
	IHubConsumerService iHubConsumerService;
	@Autowired
	private BulkImportTaskService bulkImportTaskService; 
	@Autowired
	private ImportInvoiceService importInvoiceService;
	@Autowired
	ClientAddlInfoRepository clientAddlInfoRepository;
	@Autowired
	private EinvoiceConfigurationRepository einvoiceConfigurationRepository;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	@Autowired 
	private EinvoiceDao einvoiceDao;
	
	@Autowired 
	private Gstr1Dao gstr1Dao;
	@Autowired
	private ClientService clientService;
	@Autowired 
	private CompanyCustomersRepository companyCustomersRepository;
	
	@Autowired 
	private UQCRepository uqcRepository;
	@Autowired
	ConfigService configService;
	@Autowired
	GSTR1Repository gstr1Repository;
	@Autowired
	CompanySuppliersRepository companySuppliersRepository;
	@Autowired
	LedgerRepository ledgerRepository;
	@Autowired
	private ProfileService profileService;
	@Autowired
	private GSTINPublicDataRepository gstinPublicDataRepository;
	@Autowired
	HeaderKeysRepository headerKeysRepository;
	
	private  boolean isScientificNotation(String numberString) {
	    // Validate number
	    try {
	        new BigDecimal(numberString);
	    } catch (NumberFormatException e) {
	        return false;
	    }
	    // Check for scientific notation
	    return numberString.toUpperCase().contains("E") && numberString.charAt(1)=='.';   
	}
	
	@Override
	public EinvoiceConfigurations saveEinvoiceConfigurations(EinvoiceConfigurations einvform) {
		einvoiceConfigurationRepository.deleteByClientid(einvform.getClientid());

		return einvoiceConfigurationRepository.save(einvform);
	}

	@Override
	public EinvoiceConfigurations getEinvoiceConfig(String clientId) {
		
		return einvoiceConfigurationRepository.findByClientid(clientId);
	}

	
	public Map getEInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal, InvoiceFilter filter, boolean isTotalRequired){
		logger.debug(CLASSNAME + "getInvoices : Begin");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		invTypes.add(MasterGSTConstants.B2C);
		Page<? extends InvoiceParent> invoices = null;
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
	
					String yearCode = Utility.getYearCode(month, year);
					invoices = gstr1Dao.findByClientidAndInvtypeInAndMonthAndYear(client.getId().toString(),invTypes, month, yearCode, start, length, searchVal,filter);
					totalInvoiceAmount = gstr1Dao.getEinvoiceTotalInvoicesAmountsForMonth(client.getId().toString(),invTypes, month, yearCode, searchVal,filter,"gstrOrEinvoice");
			invoicesMap.put("invoices",invoices);
			invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		}
		return invoicesMap;
	}

	public Map getEInvoices(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean isTotalRequired){
		logger.debug(CLASSNAME + "getInvoices : Begin");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		invTypes.add(MasterGSTConstants.B2C);
		Page<? extends InvoiceParent> invoices = null;
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
	
					String yearCode = Utility.getYearCode(month, year);
					invoices = gstr1Dao.findByClientidAndInvtypeInAndMonthAndYearWithSort(client.getId().toString(),invTypes, month, yearCode, start, length, searchVal,fieldName,order,filter);
					totalInvoiceAmount = gstr1Dao.getEinvoiceTotalInvoicesAmountsForMonth(client.getId().toString(),invTypes, month, yearCode, searchVal,filter,"gstrOrEinvoice");
			invoicesMap.put("invoices",invoices);
			invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		}
		return invoicesMap;
	}

	@Override
	public InvoiceParent populateEinvoiceDetails(InvoiceParent einvoice, Client client) {
		CompanyCustomers customer = null;
		if(isNotEmpty(einvoice.getB2b()) && isNotEmpty(einvoice.getB2b().get(0)) && isNotEmpty(einvoice.getB2b().get(0).getCtin())) {
			String gstnnumber =einvoice.getB2b().get(0).getCtin().trim();
			customer = companyCustomersRepository.findByGstnnumberAndClientid(gstnnumber, einvoice.getClientid().toString());
		}
		Map<String, String> hsnMap = configService.getHSNMap();
		Map<String, String> sacMap = configService.getSACMap();
		List<StateConfig> states = configService.getStates();
		DocDetails docdtls = new DocDetails();
		TransportDetails transDtls = new TransportDetails();
		SellerDetails sellerDetails = new SellerDetails();
		BuyerDetails buyerDetails = new BuyerDetails();
		ShipmentDetails shipDetails = new ShipmentDetails();
		DispatcherDetails dispatchDetails = new DispatcherDetails();
		ValueDetails valDetails = new ValueDetails();
		
		BatchDetails bchDtls = new BatchDetails();
		PayeeDetails payDtls= new PayeeDetails();
		ReferenceDetails refDtls = new ReferenceDetails();
		AddlDocDetails addlDocDtls= new AddlDocDetails();
		ExportDetails expDtls = new ExportDetails();
		EwayBillDetails ewbDtls = new EwayBillDetails();
		List<EinvoiceItem> itemList = Lists.newArrayList();
		
		Date date = einvoice.getDateofinvoice();  
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
        String einvDate = dateFormat.format(date);  
        if(isNotEmpty(einvoice.getInvtype())) {
        	if(("B2B").equalsIgnoreCase(einvoice.getInvtype()) || ("B2C").equalsIgnoreCase(einvoice.getInvtype()) ||  ("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
        		docdtls.setTyp("INV");
        	}else {
        		docdtls.setTyp(einvoice.getTyp());
        	}
        }
		
		docdtls.setNo(einvoice.getB2b().get(0).getInv().get(0).getInum());
		docdtls.setDt(einvDate);
		
		transDtls.setTaxSch("GST");
		if(isNotEmpty(einvoice.getEinvCategory())) {
			transDtls.setSupTyp(einvoice.getEinvCategory());
		}else {
			transDtls.setSupTyp("B2B");
		}
		if(("Regular").equalsIgnoreCase(einvoice.getRevchargetype())) {
			transDtls.setRegRev("N");
		}else if(("Reverse").equalsIgnoreCase(einvoice.getRevchargetype())) {
			transDtls.setRegRev("Y");
		}else {
			transDtls.setRegRev(einvoice.getRevchargetype());
		}
		if(isNotEmpty(einvoice.getIgstOnIntra())) {
			transDtls.setIgstOnIntra(einvoice.getIgstOnIntra());
		}
		if(isNotEmpty(einvoice.getB2b().get(0).getInv().get(0).getEtin())){
			transDtls.setEcmGstin(einvoice.getB2b().get(0).getInv().get(0).getEtin());
		} else {
			transDtls.setEcmGstin(null);
		}
		
		if(("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
			if(isNotEmpty(einvoice.getExp()) && isNotEmpty(einvoice.getExp().get(0)) && isNotEmpty(einvoice.getExp().get(0).getInv()) && isNotEmpty(einvoice.getExp().get(0).getInv().get(0))) {
				if(isNotEmpty(einvoice.getExp().get(0).getInv().get(0).getSbpcode())) {
					expDtls.setPort(einvoice.getExp().get(0).getInv().get(0).getSbpcode());
				}
				if(isNotEmpty(einvoice.getExp().get(0).getInv().get(0).getSbnum())) {
					expDtls.setShipBNo(einvoice.getExp().get(0).getInv().get(0).getSbnum());
				}
				if(isNotEmpty(einvoice.getExp().get(0).getInv().get(0).getSbdt())) {
					expDtls.setShipBDt(dateFormat.format(einvoice.getExp().get(0).getInv().get(0).getSbdt()));
				}
			}
			if(isNotEmpty(einvoice.getCountryCode())) {
				expDtls.setCntCode(einvoice.getCountryCode());
			}
			if(isNotEmpty(einvoice.getAddcurrencyCode())) {
				expDtls.setForCur(einvoice.getAddcurrencyCode());
			}
		}
		
		if(isNotEmpty(client)) {
			if(isNotEmpty(client.getGstnnumber())) {
				sellerDetails.setGstin(client.getGstnnumber());
			}
			if(isNotEmpty(client.getBusinessname())) {
				String businessname = client.getBusinessname();
				if(businessname.length() > 50) {
					businessname = businessname.substring(0, 49);
				}
				sellerDetails.setLglNm(businessname);
				sellerDetails.setTrdNm(businessname);
			}
			if(isNotEmpty(client.getStatename())) {
				String stateCode = client.getStatename();
				String[] sCode = stateCode.split("-");
				sellerDetails.setLoc(sCode[1]);
				sellerDetails.setState(sCode[0]);
			}
			if(isNotEmpty(client.getAddress())) {
				String address = client.getAddress();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				sellerDetails.setAddr1(address);
				sellerDetails.setAddr2(null);
			}else {
				if(isNotEmpty(client.getStatename())) {
					sellerDetails.setAddr1(client.getStatename());
					sellerDetails.setAddr2(null);
				}
			}
			if(isNotEmpty(client.getPincode())) {
				sellerDetails.setPin(client.getPincode());
			}
			if(isNotEmpty(client.getMobilenumber())) {
				sellerDetails.setPh(client.getMobilenumber());
			}
			if(isNotEmpty(client.getEmail())) {
				sellerDetails.setEm(client.getEmail());
			}
		}
		
	if(isEmpty(einvoice.getBuyerDtls())) {
		if(isNotEmpty(customer)) {
			if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
				if(isNotEmpty(customer.getGstnnumber())) {
					buyerDetails.setGstin(customer.getGstnnumber());
				}
			}else {
				buyerDetails.setGstin("URP");
			}
			if(isNotEmpty(customer.getName())) {
				String name = customer.getName();
				if(name.length() > 50) {
					name = name.substring(0, 49);
				}
				buyerDetails.setLglNm(name);
				buyerDetails.setTrdNm(name);
			}
			if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
				if(isNotEmpty(customer.getState())) {
					String stateCode = customer.getState();
					String[] sCode = stateCode.split("-");
					buyerDetails.setLoc(sCode[1]);
					buyerDetails.setPos(sCode[0]);
					buyerDetails.setState(sCode[0]);
				}
			}else {
				buyerDetails.setState("96");
				buyerDetails.setPos("96");
				buyerDetails.setLoc("Other Teritory");
			}
			if(isNotEmpty(customer.getAddress())) {
				String address = customer.getAddress();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				buyerDetails.setAddr1(address);
				buyerDetails.setAddr2(address);
			}else {
				if(isNotEmpty(customer.getState())) {
					buyerDetails.setAddr1(customer.getState());
					buyerDetails.setAddr2(customer.getState());
				}
			}
			if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
				if(isNotEmpty(customer.getPincode())) {
					buyerDetails.setPin(Integer.parseInt(customer.getPincode()));
				}
			}else {
				buyerDetails.setPin(Integer.parseInt("999999"));
			}
			if(isNotEmpty(customer.getMobilenumber()) && customer.getMobilenumber().length() > 5) {
				buyerDetails.setPh(customer.getMobilenumber());
			}
			if(isNotEmpty(customer.getEmail())) {
				buyerDetails.setEm(customer.getEmail());
			}
		} 
	}else {
		buyerDetails = einvoice.getBuyerDtls();
		if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
			if(("Credit/Debit Notes").equalsIgnoreCase(einvoice.getInvtype())) {
				if(isNotEmpty(buyerDetails.getGstin())) {
					buyerDetails.setGstin(buyerDetails.getGstin());
				}else {
					if(isNotEmpty(einvoice.getEinvCategory())) {
						if(("EXPWP").equalsIgnoreCase(einvoice.getEinvCategory()) || ("EXPWOP").equalsIgnoreCase(einvoice.getEinvCategory())) {
							buyerDetails.setGstin("URP");
						}
					}
				}
			}else {
				if(isNotEmpty(buyerDetails.getGstin())) {
					buyerDetails.setGstin(buyerDetails.getGstin());
				}
			}
		}else {
			buyerDetails.setGstin("URP");
		}
		if(isNotEmpty(buyerDetails.getLglNm())) {
			String name = buyerDetails.getLglNm();
			if(name.length() > 50) {
				name = name.substring(0, 49);
			}
			buyerDetails.setLglNm(name);
		}
		if(isNotEmpty(buyerDetails.getTrdNm())) {
			String name = buyerDetails.getTrdNm();
			if(name.length() > 50) {
				name = name.substring(0, 49);
			}
			buyerDetails.setTrdNm(name);
		}
		
		if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
			if (isNotEmpty(buyerDetails.getState())) {
				String statename = buyerDetails.getState();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				if(statename.contains("-")) {
					String[] nm = statename.split("-");
					statename = nm[1].replaceAll("\\s", "");
				}
				Integer pos = null;
				for (StateConfig state : states) {
					String name = state.getName();
					String[] nm = state.getName().split("-");
					if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}
				}
				if(("Credit/Debit Notes").equalsIgnoreCase(einvoice.getInvtype())) {
						if(isNotEmpty(einvoice.getEinvCategory())) {
							if(("EXPWP").equalsIgnoreCase(einvoice.getEinvCategory()) || ("EXPWOP").equalsIgnoreCase(einvoice.getEinvCategory())) {
								buyerDetails.setState("96");
							}else if(isNotEmpty(pos)) {
								String strPos = pos < 10 ? "0"+pos : pos.toString();
								buyerDetails.setState(strPos);
							}
						}
				}else {
					if(isNotEmpty(pos)) {
						String strPos = pos < 10 ? "0"+pos : pos.toString();
						buyerDetails.setState(strPos);
					}
				}
				if(isEmpty(buyerDetails.getLoc())) {
					buyerDetails.setLoc(statename);
				}
			}
			if(isNotEmpty(einvoice.getStatename())) {
				String statename = einvoice.getStatename();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				if(statename.contains("-")) {
					String[] nm = statename.split("-");
					statename = nm[1].replaceAll("\\s", "");
				}
				Integer pos = null;
				for (StateConfig state : states) {
					String name = state.getName();
					String[] nm = state.getName().split("-");
					if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}
				}
				if(("Credit/Debit Notes").equalsIgnoreCase(einvoice.getInvtype())) {
					if(isNotEmpty(einvoice.getEinvCategory())) {
						if(("EXPWP").equalsIgnoreCase(einvoice.getEinvCategory()) || ("EXPWOP").equalsIgnoreCase(einvoice.getEinvCategory())) {
							buyerDetails.setPos("96");
						}else if(isNotEmpty(pos)) {
							String strPos = pos < 10 ? "0"+pos : pos.toString();
							buyerDetails.setPos(strPos);
						}
					}
				}else {
					if(isNotEmpty(pos)) {
						String strPos = pos < 10 ? "0"+pos : pos.toString();
						buyerDetails.setPos(strPos);
					}
				}
			}else if (isNotEmpty(buyerDetails.getPos())) {
				String statename = buyerDetails.getPos();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				if(statename.contains("-")) {
					String[] nm = statename.split("-");
					statename = nm[1].replaceAll("\\s", "");
				}
				Integer pos = null;
				for (StateConfig state : states) {
					String name = state.getName();
					String[] nm = state.getName().split("-");
					if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}
				}
				if(("Credit/Debit Notes").equalsIgnoreCase(einvoice.getInvtype())) {
					if(isNotEmpty(einvoice.getEinvCategory())) {
						if(("EXPWP").equalsIgnoreCase(einvoice.getEinvCategory()) || ("EXPWOP").equalsIgnoreCase(einvoice.getEinvCategory())) {
							buyerDetails.setPos("96");
						}else if(isNotEmpty(pos)) {
							String strPos = pos < 10 ? "0"+pos : pos.toString();
							buyerDetails.setPos(strPos);
						}
					}
				}else {
					if(isNotEmpty(pos)) {
						String strPos = pos < 10 ? "0"+pos : pos.toString();
						buyerDetails.setPos(strPos);
					}
				}
			}
		}else {
			buyerDetails.setState("96");
			buyerDetails.setPos("96");
			if(isEmpty(buyerDetails.getLoc())) {
				buyerDetails.setLoc("Other Teritory");
			}
		}
		if(isNotEmpty(buyerDetails.getAddr1())) {
			String address = buyerDetails.getAddr1();
			if(address.length() > 100) {
				address = address.substring(0, 99);
			}
			buyerDetails.setAddr1(address);
		}else {
			if(isNotEmpty(customer) && isNotEmpty(customer.getState())) {
				buyerDetails.setAddr1(customer.getState());
			}
		}
		if(isNotEmpty(buyerDetails.getAddr2())) {
			String address = buyerDetails.getAddr2();
			if(address.length() > 100) {
				address = address.substring(0, 99);
			}
			buyerDetails.setAddr2(address);
		}else if(isNotEmpty(customer) && isNotEmpty(customer.getState())) {
				buyerDetails.setAddr2(customer.getState());
		}else {
			buyerDetails.setAddr2(null);
		}
		if(!("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
			if(isNotEmpty(buyerDetails.getPin())) {
				buyerDetails.setPin(buyerDetails.getPin());
			}
		}else {
			buyerDetails.setPin(Integer.parseInt("999999"));
		}
		if(isNotEmpty(customer)) {
			if(isNotEmpty(customer.getMobilenumber())  && customer.getMobilenumber().length() > 5) {
				buyerDetails.setPh(customer.getMobilenumber());
			}
			if(isNotEmpty(customer.getEmail())) {
				buyerDetails.setEm(customer.getEmail());
			}
		}
		if(isNotEmpty(buyerDetails.getAddr1())) {
			String address = buyerDetails.getAddr1();
			if(address.length() > 100) {
				address = address.substring(0, 99);
			}
			buyerDetails.setAddr1(address);
		}
		if(isNotEmpty(buyerDetails.getAddr2())) {
			String address = buyerDetails.getAddr2();
			if(address.length() > 100) {
				address = address.substring(0, 99);
			}
			buyerDetails.setAddr2(address);
		}
		
	}
		if(isNotEmpty(einvoice.getDispatcherDtls())) {
			boolean flag=false;
			if(isNotEmpty(einvoice.getDispatcherDtls().getNm())) {
				dispatchDetails.setNm(einvoice.getDispatcherDtls().getNm());flag=true;
			}
			if(isNotEmpty(einvoice.getDispatcherDtls().getAddr1())) {
				String address = einvoice.getDispatcherDtls().getAddr1();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				dispatchDetails.setAddr1(address);flag=true;
			}
			if(isNotEmpty(einvoice.getDispatcherDtls().getAddr2())) {
				String address = einvoice.getDispatcherDtls().getAddr1();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				dispatchDetails.setAddr2(address);flag=true;
			}
			if(isNotEmpty(einvoice.getDispatcherDtls().getLoc())) {
				dispatchDetails.setLoc(einvoice.getDispatcherDtls().getLoc());flag=true;
			}
			if(isNotEmpty(einvoice.getDispatcherDtls().getPin())) {
				dispatchDetails.setPin(einvoice.getDispatcherDtls().getPin());flag=true;
			}
			if (isNotEmpty(einvoice.getDispatcherDtls().getStcd())) {
				String statename = einvoice.getDispatcherDtls().getStcd();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				if(statename.contains("-")) {
					String[] nm = statename.split("-");
					statename = nm[1].replaceAll("\\s", "");
				}
				Integer pos = null;
				for (StateConfig state : states) {
					String name = state.getName();
					String[] nm = state.getName().split("-");
					if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}
				}
				if(isNotEmpty(pos)) {
					String strPos = pos < 10 ? "0"+pos : pos.toString();
					dispatchDetails.setStcd(strPos);
				}
				flag=true;
			}
			if(!flag) {
				dispatchDetails=null;
			}
		}
		if(isNotEmpty(einvoice.getShipmentDtls())) {
			boolean flag=false;
			if(isNotEmpty(einvoice.getShipmentDtls().getGstin())) {
				shipDetails.setGstin(einvoice.getShipmentDtls().getGstin());flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getLglNm())) {
				String lgnm = einvoice.getShipmentDtls().getLglNm();
				if(lgnm.length() > 50) {
					lgnm = lgnm.substring(0,49);
				}
				shipDetails.setLglNm(lgnm);flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getTrdNm())) {
				String tdnm = einvoice.getShipmentDtls().getTrdNm();
				if(tdnm.length() > 50) {
					tdnm = tdnm.substring(0,49);
				}
				shipDetails.setTrdNm(tdnm);flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getAddr1())) {
				String address = einvoice.getShipmentDtls().getAddr1();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				shipDetails.setAddr1(address);flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getAddr2())) {
				String address = einvoice.getShipmentDtls().getAddr2();
				if(address.length() > 100) {
					address = address.substring(0, 99);
				}
				shipDetails.setAddr2(address);flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getLoc())) {
				shipDetails.setLoc(einvoice.getShipmentDtls().getLoc());flag=true;
			}
			if(isNotEmpty(einvoice.getShipmentDtls().getPin())) {
				shipDetails.setPin(einvoice.getShipmentDtls().getPin());flag=true;
			}
			if (isNotEmpty(einvoice.getShipmentDtls().getStcd())) {
				String statename = einvoice.getShipmentDtls().getStcd();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = invStatename(statename);
				}
				if(statename.contains("-")) {
					String[] nm = statename.split("-");
					statename = nm[1].replaceAll("\\s", "");
				}
				Integer pos = null;
				for (StateConfig state : states) {
					String name = state.getName();
					String[] nm = state.getName().split("-");
					if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
						statename = name;
						pos = state.getTin();
						break;
					}
				}
				if(isNotEmpty(pos)) {
					String strPos = pos < 10 ? "0"+pos : pos.toString();
					shipDetails.setStcd(strPos);
				}
				flag=true;
			}
			if(!flag) {
				shipDetails=null;
			}
		}
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		Double totalAssAmt = 0d;
		Double totalCess = 0d;
		Double totalStateCessAmt = 0d;
		Double totalCessNonAdVal = 0d;
		Double totalInvVal = 0d;
		int i = 1;
		for (Item evlist : einvoice.getItems()) {
			EinvoiceItem eitem = new EinvoiceItem();
			eitem.setSlNo(Integer.toString(i));
			String description = null;
			String code = evlist.getHsn();
			if (isNotEmpty(code)) {
				if(code.contains(" : ")) {
					String hsncode[]= code.split(" : ");
					code = hsncode[0];
					description = hsncode[1];
				} 
				if (hsnMap.containsKey(code)) {
					eitem.setIsServc("N");
					description = hsnMap.get(code);
				} else if (hsnMap.containsValue(code)) {
					for (String key : hsnMap.keySet()) {
						if (hsnMap.get(key).equals(code)) {
							code = key;
							description = hsnMap.get(key);
							eitem.setIsServc("N");
							break;
						}
					}
				} else if (sacMap.containsKey(code)) {
					eitem.setIsServc("Y");
					description = sacMap.get(code);
				} else if (sacMap.containsValue(code)) {
					for (String key : sacMap.keySet()) {
						if (sacMap.get(key).equals(code)) {
							code = key;
							description = sacMap.get(key);
							eitem.setIsServc("Y");
							break;
						}
					}
				}
				if (isEmpty(description)) {
					for (String key : hsnMap.keySet()) {
						if (hsnMap.get(key).endsWith(" : " + code)) {
							code = key;
							description = hsnMap.get(key);
							eitem.setIsServc("N");
							break;
						}
					}
				}
				if (isEmpty(description)) {
					for (String key : sacMap.keySet()) {
						if (sacMap.get(key).endsWith(" : " + code)) {
							code = key;
							description = sacMap.get(key);
							eitem.setIsServc("Y");
							break;
						}
					}
				}
			}
			String hcode = null;
			String hdescription = null;
			if (isNotEmpty(evlist.getHsn())) {
				if (evlist.getHsn().contains(" : ")) {
					String hsncode[] = evlist.getHsn().split(" : ");
					hcode = hsncode[0];
					hdescription = hsncode[1];
				} else {
					hcode = evlist.getHsn();
				}
			}
			if(isNotEmpty(evlist.getItemno())) {
				String itemno = evlist.getItemno();
				if(itemno.length() > 300) {
					itemno = itemno.substring(0, 299);
				}
				eitem.setPrdDesc(itemno);
			}
			if(isNotEmpty(hcode)) {
				eitem.setHsnCd(hcode);
			}
			if(isNotEmpty(evlist.getBarCode())) {
				eitem.setBarcde(evlist.getBarCode());
			}
			if(isNotEmpty(evlist.getQuantity())) {
				eitem.setQty(evlist.getQuantity());
			}
			if(isNotEmpty(evlist.getFreeQty())) {
				try {
					eitem.setFreeQty(NumberFormat.getInstance().parse(evlist.getFreeQty()));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			if(isNotEmpty(evlist.getUqc())) {
				UQCConfig uqc = uqcRepository.findByNameIgnoreCase(evlist.getUqc().trim());
				if(isNotEmpty(uqc)) {
					eitem.setUnit(uqc.getCode());
				}else {
					UQCConfig uqccode = uqcRepository.findByCodeIgnoreCase(evlist.getUqc().trim());
					if(isNotEmpty(uqccode)) {
						eitem.setUnit(uqccode.getCode());
					}
				}
			}
			if(isNotEmpty(evlist.getRateperitem())) {
				eitem.setUnitPrice(evlist.getRateperitem());
			}
			if(isNotEmpty(evlist.getQuantity()) && isNotEmpty(evlist.getRateperitem())) {
				if(isNotEmpty(evlist.getQuantity())) {
					eitem.setTotAmt(evlist.getQuantity()*evlist.getRateperitem());	
				}else {
					eitem.setTotAmt(0*evlist.getRateperitem());					
				}
			}else {
				if(isNotEmpty(evlist.getTaxablevalue())) {
					eitem.setTotAmt(evlist.getTaxablevalue());
				}
			}
			if(isNotEmpty(evlist.getTaxablevalue())) {
				evlist.setAssAmt(evlist.getTaxablevalue());
				totalAssAmt += evlist.getTaxablevalue();
			}
			if(isNotEmpty(evlist.getDiscount())) {
				eitem.setDiscount(evlist.getDiscount());
			}
			if(isNotEmpty(evlist.getOthrCharges())) {
				eitem.setOthChrg(evlist.getOthrCharges());
			}
			if(isNotEmpty(evlist.getTaxablevalue())) {
				eitem.setAssAmt(evlist.getTaxablevalue());
			}
			if(isNotEmpty(evlist.getRate())) {
				eitem.setGstRt(evlist.getRate());
			}else {
				if(isNotEmpty(evlist.getIgstrate())) {
					eitem.setGstRt(evlist.getIgstrate());
				}else if(isNotEmpty(evlist.getCgstrate()) && isNotEmpty(evlist.getSgstrate())) {
					eitem.setGstRt(evlist.getCgstrate() + evlist.getSgstrate());
				}else {
					eitem.setGstRt(0d);
				}
			}
			if(isNotEmpty(einvoice.getIgstOnIntra()) && einvoice.getIgstOnIntra().equalsIgnoreCase("Y")) {
				if(isNotEmpty(evlist.getIgstamount())) {
					eitem.setIgstAmt(evlist.getIgstamount());
					totalIGST += evlist.getIgstamount();
				}else {
					if(isNotEmpty(evlist.getSgstamount()) && isNotEmpty(evlist.getCgstamount())) {
						eitem.setIgstAmt(evlist.getSgstamount() + evlist.getCgstamount());
						totalIGST += evlist.getSgstamount() + evlist.getCgstamount();
					}
				}
			}else {
				if(isNotEmpty(evlist.getSgstamount())) {
					eitem.setSgstAmt(evlist.getSgstamount());
					totalSGST += evlist.getSgstamount();
				}
				if(isNotEmpty(evlist.getCgstamount())) {
					eitem.setCgstAmt(evlist.getCgstamount());
					totalCGST += evlist.getCgstamount();
				}
				if(isNotEmpty(evlist.getIgstamount())) {
					eitem.setIgstAmt(evlist.getIgstamount());
					totalIGST += evlist.getIgstamount();
				}
			}
			if(isNotEmpty(evlist.getCessrate())) {
				eitem.setCesRt(evlist.getCessrate());
			}
			if(isNotEmpty(evlist.getCessamount())) {
				eitem.setCesAmt(evlist.getCessamount());
				totalCess += evlist.getCessamount();
			}
			if(isNotEmpty(evlist.getTotal())) {
				eitem.setTotItemVal(evlist.getTotal());
				totalInvVal += evlist.getTotal();
			}
			if (isNotEmpty(evlist.getExmepted())) {
				totalExempted += evlist.getExmepted();
			}
			if(isNotEmpty(evlist.getStateCess())) {
				totalStateCessAmt += evlist.getStateCess();
			}
			
			if(isNotEmpty(evlist.getCessNonAdvol())) {
				totalCessNonAdVal += evlist.getCessNonAdvol();
			}
			itemList.add(eitem);
			i++;
		}
		einvoice.setTotalIgstAmount(totalIGST);
		einvoice.setTotalCgstAmount(totalCGST);
		einvoice.setTotalSgstAmount(totalSGST);
		einvoice.setTotalExemptedAmount(totalExempted);
		einvoice.setTotalamount(totalInvVal);
		einvoice.setTotalCessAmount(totalCess);
		if(isNotEmpty(totalAssAmt)) {
			einvoice.setTotalAssAmount(totalAssAmt);
		}
		if(isNotEmpty(totalCessNonAdVal)) {
			einvoice.setTotalCessNonAdVal(totalCessNonAdVal);
		}
		if(isNotEmpty(totalStateCessAmt)) {
			einvoice.setTotalStateCessAmount(totalStateCessAmt);
		}
		
		einvoice.setItemList(itemList);
		
		OtherConfigurations configdetails = otherConfigurationRepository.findByClientid(einvoice.getClientid());
		boolean tcstds = false;
		if(isNotEmpty(configdetails)) {
			if(isNotEmpty(configdetails.isEnableTCS()) && configdetails.isEnableTCS()) {
				tcstds = true;
			}
		}
		
		/*Double taxableortotalamt = null;
		Double tcstdsamnt = null;
		if(tcstds) {
			if(isNotEmpty(einvoice.getTotaltaxableamount())) {
				taxableortotalamt = einvoice.getTotaltaxableamount();
			}
		}else {
			if(isNotEmpty(einvoice.getTotalamount())) {
				taxableortotalamt = einvoice.getTotalamount();
			}
		}
		
		if(isNotEmpty(taxableortotalamt) && isNotEmpty(einvoice.getTcstdspercentage())) {
			tcstdsamnt = (taxableortotalamt) * einvoice.getTcstdspercentage() / 100;
		}*/
		if(isNotEmpty(einvoice.getTotalamount())) {
			double totalvalue = einvoice.getTotalamount();
			/*if(isNotEmpty(tcstdsamnt)) {
				totalvalue += tcstdsamnt;
			}*/
			valDetails.setTotInvVal(totalvalue);
		}else {
			valDetails.setTotInvVal(null);
		}
		if(isNotEmpty(einvoice.getTotalIgstAmount())) {
			valDetails.setIgstVal(einvoice.getTotalIgstAmount());
		}else {
			valDetails.setIgstVal(null);
		}
		if(isNotEmpty(einvoice.getTotalCgstAmount())) {
			valDetails.setCgstVal(einvoice.getTotalCgstAmount());
		}else {
			valDetails.setCgstVal(null);
		}
		if(isNotEmpty(einvoice.getTotalSgstAmount())) {
			valDetails.setSgstVal(einvoice.getTotalSgstAmount());
		}else {
			valDetails.setSgstVal(null);
		}
		if(isNotEmpty(einvoice.getTotalCessAmount())) {
			valDetails.setCesVal(einvoice.getTotalCessAmount());
		}else {
			valDetails.setCesVal(null);
		}
		if(isNotEmpty(einvoice.getTotalAssAmount())) {
			valDetails.setAssVal(einvoice.getTotalAssAmount());
		}else {
			valDetails.setAssVal(null);
		}
		if(isNotEmpty(einvoice.getTotalStateCessAmount())) {
			valDetails.setStCesVal(einvoice.getTotalStateCessAmount());
		}else {
			valDetails.setStCesVal(null);
		}
		if(isNotEmpty(einvoice.getRoundOffAmount())) {
			valDetails.setRndOffAmt(einvoice.getRoundOffAmount());
		}else {
			valDetails.setRndOffAmt(null);
		}
		if(isNotEmpty(einvoice.getTotalDiscountAmount())) {
			valDetails.setDiscount(einvoice.getTotalDiscountAmount());
		}else {
			valDetails.setDiscount(null);
		}
		/*if(isNotEmpty(tcstdsamnt)) {
			valDetails.setOthChrg(tcstdsamnt);
		}else {
			valDetails.setOthChrg(null);
		}*/
		if(isNotEmpty(einvoice.getTotalOthrChrgeAmount())) {
			valDetails.setOthChrg(einvoice.getTotalOthrChrgeAmount());
		}else {
			valDetails.setOthChrg(null);
		}
		einvoice.setDocDtls(docdtls);
		einvoice.setTranDtls(transDtls);
		einvoice.setSellerDtls(sellerDetails);
		einvoice.setBuyerDtls(buyerDetails);
		einvoice.setShipmentDtls(shipDetails);
		einvoice.setDispatcherDtls(dispatchDetails);
		einvoice.setValDtls(valDetails);
		if(("Exports").equalsIgnoreCase(einvoice.getInvtype())) {
			einvoice.setExpDtls(expDtls);
		}
		return einvoice;
	}

	@Override
	public InvoiceParent arrangeJsonDataForIRN(InvoiceParent respData) {
		EINVOICE einvoice = new EINVOICE();
		einvoice.setVersion(einvoiceversion);
		einvoice.setEtranDtls(respData.getTranDtls());
		einvoice.setEdocDtls(respData.getDocDtls());
		einvoice.setEsellerDtls(respData.getSellerDtls());
		einvoice.setEbuyerDtls(respData.getBuyerDtls());
		einvoice.setEvalDtls(respData.getValDtls());
		einvoice.setEitemList(respData.getItemList());
		if(isNotEmpty(respData.getShipmentDtls())) {
			einvoice.setEshipmentDtls(respData.getShipmentDtls());
		}else {
			einvoice.setEshipmentDtls(null);
		}
		if(isNotEmpty(respData.getDispatcherDtls())) {
			einvoice.setEdispatcherDtls(respData.getDispatcherDtls());
		}else {
			einvoice.setEdispatcherDtls(null);
		}
	if(isNotEmpty(respData.getPayDtls())){
		einvoice.setEpayDtls(respData.getPayDtls());
	}else {
		einvoice.setEpayDtls(null);
	}
	if(isNotEmpty(respData.getRefDtls())){
		einvoice.setReefDtls(respData.getRefDtls());
	}else {
		einvoice.setReefDtls(null);
	}
	if(isNotEmpty(respData.getAddlDocDtls())){
		einvoice.setEaddlDocDtls(respData.getAddlDocDtls());
	}else {
		einvoice.setEaddlDocDtls(null);
	}
	if(isNotEmpty(respData.getExpDtls())){
		einvoice.setEexpDtls(respData.getExpDtls());
	}else {
		einvoice.setEexpDtls(null);
	}
	if(isNotEmpty(respData.getEwbDtls())){
		einvoice.setEewbDtls(respData.getEwbDtls());
	}else {
		einvoice.setEewbDtls(null);
	}
		return einvoice;
	}

	@Override
	public InvoiceParent populateEinvType(InvoiceParent invoice) {
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype()) && isNotEmpty(invoice.getEinvCategory())) {
			if(("B2B").equalsIgnoreCase(invoice.getInvtype())) {
				if(("DEXP").equalsIgnoreCase(invoice.getEinvCategory())){
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
				}else if(("B2B").equalsIgnoreCase(invoice.getEinvCategory())) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				}else if(("SEZWP").equalsIgnoreCase(invoice.getEinvCategory())) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
				}else if(("SEZWOP").equalsIgnoreCase(invoice.getEinvCategory())) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
				}else {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				}
			}else if(("Exports").equalsIgnoreCase(invoice.getInvtype())) {
				if(("EXPWP").equalsIgnoreCase(invoice.getEinvCategory())){
					invoice.getExp().get(0).setExpTyp("WPAY");
				}else if(("EXPWOP").equalsIgnoreCase(invoice.getEinvCategory())) {
					invoice.getExp().get(0).setExpTyp("WOPAY");
				}
			}
		}
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype()) && isNotEmpty(invoice.getTyp())) {
		if(("Credit/Debit Notes").equalsIgnoreCase(invoice.getInvtype())) {
			if(("CRN").equalsIgnoreCase(invoice.getTyp())){
				invoice.getCdn().get(0).getNt().get(0).setNtty("C");
			}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
				invoice.getCdn().get(0).getNt().get(0).setNtty("D");
			}
		}else if(("Credit/Debit Note for Unregistered Taxpayers").equalsIgnoreCase(invoice.getInvtype())) {
			if(("CRN").equalsIgnoreCase(invoice.getTyp())){
				invoice.getCdnur().get(0).setNtty("C");
			}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
				invoice.getCdnur().get(0).setNtty("D");
			}
		}
		}
		return invoice;
	}

	@Override
	@Transactional(readOnly=true)
	public Map<String, Object> getCustomInvoices(Pageable pageable,Client client,String id,String retType,String type,String fromtime,String totime,int start,int length,String searchVal, InvoiceFilter filter, boolean flag,String booksOrReturns) {
		logger.debug(CLASSNAME + "getCustomInvoices : Begin");
		
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		Page<? extends InvoiceParent> invoices = null;
		//TotalInvoiceAmount totalInvoiceAmount = null;
		List<TotalInvoiceAmount> totalInvoiceAmount = null;
		if (isNotEmpty(retType)) {
			Date stDate = null;
			Date endDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
			stDate = new java.util.Date(cal.getTimeInMillis());
			cal = Calendar.getInstance();
			cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
			endDate = new java.util.Date(cal.getTimeInMillis());
			List<String> invTypes = new ArrayList<String>();
			invTypes.add(MasterGSTConstants.B2B);
			invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
			invTypes.add(MasterGSTConstants.EXPORTS);
			
				invoices = gstr1Dao.findByClientidAndFromtimeAndTotimeEinvoice(client.getId().toString(),invTypes, stDate, endDate, start, length, searchVal, filter);
				totalInvoiceAmount =gstr1Dao.getTotalEinvoicesAmountsForCustom(client.getId().toString(),invTypes, stDate, endDate, searchVal, filter);				
		}
		
		invoicesMap.put("invoices",invoices);
		invoicesMap.put("invoicesAmount",totalInvoiceAmount);
		return invoicesMap;
	}

	public String invStatename(String statename) {
		String stname = "";
		if(isNotEmpty(statename) && statename.contains("&")) {
			stname = statename;
			stname = stname.replace("&", "and");
		}
		return stname;
	}
	
	@Override
	@Transactional
	public InvoiceParent saveSalesInvoice(InvoiceParent invoice, final boolean isIntraState)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(CLASSNAME + "saveSalesInvoice : Begin");
		invoice = populateInvoiceInfo(invoice, GSTR1, isIntraState);
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice()) && isEmpty(invoice.getDueDate())) {
			invoice.setDueDate(invoice.getDateofinvoice());
			invoice.setTermDays("0");
		}
		if(isEmpty(invoice.getPendingAmount()) && isNotEmpty(invoice.getTotalamount())) {
			invoice.setPendingAmount(invoice.getTotalamount());
			invoice.setReceivedAmount(0d);
		}
		gstr1Repository.save((GSTR1) invoice);
		inventoryService.updateStockAdjustments(invoice, GSTR1);
		return invoice;
	}
	
	@Override
	public InvoiceParent populateInvoiceInfo(InvoiceParent invoice, final String returntype, final boolean isIntraState) {

		if (isNotEmpty(invoice.getBilledtoname())) {
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(invoice.getClientid(),invoice.getBilledtoname());
			if (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER)
					|| returntype.equals("PurchaseRegister")) {
				CompanySuppliers companysupplier = companySuppliersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
				if (isEmpty(companysupplier)) {
					invoice.setVendorName(invoice.getBilledtoname());
					companysupplier = new CompanySuppliers();
					companysupplier.setName(invoice.getBilledtoname());
					companysupplier.setSupplierLedgerName(invoice.getBilledtoname());
					companysupplier.setClientid(invoice.getClientid());
					companysupplier.setUserid(invoice.getUserid());
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0))) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
								String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
								if (address.length() == 1 && address.equals(",")) {
								} else {
									companysupplier.setAddress(address);
								}
							}
						}
						if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							companysupplier.setGstnnumber(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
							String statename = invoice.getB2b().get(0).getCtin().toUpperCase().trim().substring(0, 2);
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if (nm[0].equalsIgnoreCase(statename)) {
									statename = name;
									break;
								}
							}
							companysupplier.setState(statename);
						}
					}
					profileService.saveSupplier(companysupplier);
				}
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getBilledtoname())) {
						lgrdr.setLedgerName(invoice.getBilledtoname());
					}
					lgrdr.setGrpsubgrpName("Sundry Creditors");
					lgrdr.setLedgerpath("Liabilities/Current liabilities/Sundry Creditors");
					ledgerRepository.save(lgrdr);
				}
			} else {
				CompanyCustomers companycustomer = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), invoice.getClientid());
				if (isEmpty(companycustomer)) {
					invoice.setVendorName(invoice.getBilledtoname());
					companycustomer = new CompanyCustomers();
					companycustomer.setName(invoice.getBilledtoname());
					companycustomer.setCustomerLedgerName(invoice.getBilledtoname());
					companycustomer.setClientid(invoice.getClientid());
					companycustomer.setUserid(invoice.getUserid());
					if(isNotEmpty(invoice.getBuyerPincode())) {
						companycustomer.setPincode(invoice.getBuyerPincode().toString());	
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv())
								&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0))) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
								String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
								if (address.length() == 1 && address.equals(",")) {
								} else {
									companycustomer.setAddress(address);
								}
							}
						}
						if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							companycustomer.setGstnnumber(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
							String statename = invoice.getB2b().get(0).getCtin().toUpperCase().trim().substring(0, 2);
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if (nm[0].equalsIgnoreCase(statename)) {
									statename = name;
									break;
								}
							}
							companycustomer.setState(statename);
						}
					}
					profileService.saveCustomer(companycustomer);
				}
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getBilledtoname())) {
						lgrdr.setLedgerName(invoice.getBilledtoname());
					}
					lgrdr.setGrpsubgrpName(AccountConstants.SUNDRY_DEBTORS);
					lgrdr.setLedgerpath("Assets/Current Assets/Sundry Debtors");
					lgrdr.setLedgerOpeningBalance(0);
					ledgerRepository.save(lgrdr);
				}
			}
		}

		if (isNotEmpty(invoice.getLedgerName())) {
			ProfileLedger ldgr = ledgerRepository.findByClientidAndLedgerNameIgnoreCase(invoice.getClientid(), invoice.getLedgerName());
			if (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER) || returntype.equals("PurchaseRegister")) {
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getLedgerName())) {
						lgrdr.setLedgerName(invoice.getLedgerName());
					}
					if (MasterGSTConstants.ADVANCES.equalsIgnoreCase(invoice.getInvtype())) {
						lgrdr.setGrpsubgrpName("Cash in hand'");
						lgrdr.setLedgerpath("Assets/Current Assets/Cash in hand");
					} else if (MasterGSTConstants.ISD.equalsIgnoreCase(invoice.getInvtype())) {
						lgrdr.setGrpsubgrpName("Output Tax");
						lgrdr.setLedgerpath("Liabilities/Current liabilities/Duties & Taxes - Liabilities/Output Tax");
					} else {
						lgrdr.setGrpsubgrpName("Expenditure");
						lgrdr.setLedgerpath("Expenditure/Expenditure");
					}
					ledgerRepository.save(lgrdr);
				}
			} else {
				if (isEmpty(ldgr)) {
					ProfileLedger lgrdr = new ProfileLedger();
					lgrdr.setClientid(invoice.getClientid());
					if (isNotEmpty(invoice.getLedgerName())) {
						lgrdr.setLedgerName(invoice.getLedgerName());
					}
					/*if (MasterGSTConstants.ADVANCES.equalsIgnoreCase(invoice.getInvtype())) {
						lgrdr.setGrpsubgrpName("Cash in hand'");
						lgrdr.setLedgerpath("Assets/Current Assets/Cash in hand");
					} else {
						lgrdr.setGrpsubgrpName(AccountConstants.SALES);
						lgrdr.setLedgerpath(AccountConstants.SALES_DEFAULT_PATH);
					}*/
					lgrdr.setGrpsubgrpName(AccountConstants.SALES);
					lgrdr.setLedgerpath(AccountConstants.SALES_DEFAULT_PATH);
					lgrdr.setLedgerOpeningBalance(0);
					ledgerRepository.save(lgrdr);
				}
			}
		}

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Double totalIGST = 0d;
		Double totalCGST = 0d;
		Double totalSGST = 0d;
		Double totalExempted = 0d;
		Double totalAssAmt = 0d;
		Double totalStateCessAmt = 0d;
		Double totalCessNonAdVal = 0d;
		Double totalCessAmount = 0d;
		Double toralDiscAmount = 0d;
		Double totalOthrChrgAmount  = 0d;
		if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
			invoice.getB2b().get(0).setCtin(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
			  if (isNotEmpty(invoice.getDealerType())) { 
				  GSTINPublicData gstndata = gstinPublicDataRepository.findByGstin(invoice.getB2b().get(0).getCtin().toUpperCase().trim());
				  if(isNotEmpty(gstndata)) { 
					  if(isNotEmpty(gstndata.getDty())) {
						  invoice.setDealerType(gstndata.getDty()); 
					  }
				  }else { 
					  Response response = iHubConsumerService.publicSearch(invoice.getB2b().get(0).getCtin().toUpperCase().trim()); 
					  if (isNotEmpty(response) && isNotEmpty(response.getStatuscd()) && response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
						  ResponseData data = response.getData(); 
						  if (isNotEmpty(data.getDty())) {
							  invoice.setDealerType(data.getDty()); 
						  } 
					  }
				  } 
			  }
			 
		}
		String ecomtin = "";
		if (isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0)) && isNotEmpty(invoice.getB2cs().get(0).getEtin())) {
			ecomtin = invoice.getB2cs().get(0).getEtin();
			invoice.setInvoiceEcomGSTIN(ecomtin.trim());
		}
		if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))	&& isNotEmpty(invoice.getB2b().get(0).getInv())	&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
			String address = invoice.getB2b().get(0).getInv().get(0).getAddress();
			if (address.length() == 1 && address.equals(",")) {
				invoice.getB2b().get(0).getInv().get(0).setAddress("");
			}
		}
		//if (!returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0))
					&& isNotEmpty(invoice.getB2b().get(0).getInv())
					&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())
					&& isNotEmpty(invoice.getDateofinvoice())) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(invoice.getDateofinvoice());
				int month = cal.get(Calendar.MONTH) + 1;
				int year = cal.get(Calendar.YEAR);
				String submissionYear = (year - 1) + "-" + (year);
				if (month > 3) {
					submissionYear = year + "-" + (year + 1);
				}
				String invType = "";
				if (invoice.getInvtype().equals(MasterGSTConstants.B2B)
						|| invoice.getInvtype().equals(MasterGSTConstants.B2C)
						|| invoice.getInvtype().equals(MasterGSTConstants.B2CL)) {
					invType = MasterGSTConstants.B2B;
					if(returntype.equals("Purchase Register")){
						invType = "PurchaseReverseChargeNo";
					}
				} else if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
					if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())){
						if(invoice.getCdn().get(0).getNt().get(0).getNtty().equalsIgnoreCase("D")){
							invType = "Debit Note";
						}else if(invoice.getCdn().get(0).getNt().get(0).getNtty().equalsIgnoreCase("C")){
							invType = "Credit Note";
						}
					}
				} else if(returntype.equals("Purchase Register")){
					invType = "PurchaseReverseChargeNo";
				}else {
					invType = invoice.getInvtype();
				}
				CompanyInvoices invoiceConfig = profileService.getInvoiceConfigDetails(invoice.getClientid(), submissionYear, invType, returntype);
				String revNo="";
				if(returntype.equals("Purchase Register")) {
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
						revNo = invoice.getRevchargeNo();
					}
				}else {
					revNo = invoice.getB2b().get(0).getInv().get(0).getInum();
				}
				if (isNotEmpty(invoiceConfig) && (isEmpty(invoiceConfig.getPrefix()) || (isNotEmpty(invoiceConfig.getPrefix()) && revNo.startsWith(invoiceConfig.getPrefix())))) {
					String startInvNo = "";
					String endInvNo = "";
					String invPrefix = "";
					String actualNo = "";
					if(returntype.equals("Purchase Register")) {
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
							actualNo = invoice.getRevchargeNo();
						}
					}else {
						actualNo = invoice.getB2b().get(0).getInv().get(0).getInum();
					}
					if (isNotEmpty(invoiceConfig.getPrefix())) {
						startInvNo += invoiceConfig.getPrefix().toUpperCase();
						endInvNo += invoiceConfig.getPrefix().toUpperCase();
						invPrefix += invoiceConfig.getPrefix().toUpperCase();
						actualNo = actualNo.replaceFirst(invoiceConfig.getPrefix(), "");
					}
					if (isNotEmpty(invoiceConfig.getAllowMonth()) && invoiceConfig.getAllowMonth().equals("true")) {

						if (isNotEmpty(invoiceConfig.getFormatMonth())) {
							if (invoiceConfig.getFormatMonth().equals("02")) {
								invPrefix += (month < 10 ? "0" + month : month);
								startInvNo += (month < 10 ? "0" + month : month);
								endInvNo += (month < 10 ? "0" + month : month);
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()), "");
							} else if (invoiceConfig.getFormatMonth().equals("02/")) {
								invPrefix += (month < 10 ? "0" + month + "/" : month + "/");
								startInvNo += (month < 10 ? "0" + month + "/" : month + "/");
								endInvNo += (month < 10 ? "0" + month + "/" : month + "/");
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()) + "/", "");
							} else if (invoiceConfig.getFormatMonth().equals("02-")) {
								invPrefix += (month < 10 ? "0" + month + "-" : month + "-");
								startInvNo += (month < 10 ? "0" + month + "-" : month + "-");
								endInvNo += (month < 10 ? "0" + month + "-" : month + "-");
								actualNo = actualNo.replaceFirst(
										(month < 10 ? "0" + month : Integer.valueOf(month).toString()) + "-", "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB")) {
								String monthString = getMonthName(month);
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB/")) {
								String monthString = getMonthName(month) + "/";
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							} else if (invoiceConfig.getFormatMonth().equals("FEB-")) {
								String monthString = getMonthName(month) + "-";
								invPrefix += monthString;
								startInvNo += monthString;
								endInvNo += monthString;
								actualNo = actualNo.replaceFirst(monthString, "");
							}
						} else {
							invPrefix += (month < 10 ? "0" + month : month);
							startInvNo += (month < 10 ? "0" + month : month);
							endInvNo += (month < 10 ? "0" + month : month);
							actualNo = actualNo
									.replaceFirst((month < 10 ? "0" + month : Integer.valueOf(month).toString()), "");
						}
					}
					if (isNotEmpty(invoiceConfig.getAllowYear()) && invoiceConfig.getAllowYear().equals("true")) {

						if (isNotEmpty(invoiceConfig.getFormatYear())) {
							if (invoiceConfig.getFormatYear().equals("19")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2));
								startInvNo += (Integer.valueOf(year).toString().substring(2));
								endInvNo += (Integer.valueOf(year).toString().substring(2));
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)), "");
							} else if (invoiceConfig.getFormatYear().equals("19/")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2)) + "/";
								startInvNo += (Integer.valueOf(year).toString().substring(2)) + "/";
								endInvNo += (Integer.valueOf(year).toString().substring(2)) + "/";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)) + "/",
										"");
							} else if (invoiceConfig.getFormatYear().equals("19-")) {
								invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-";
								startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-";
								endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2)) + "-",
										"");
							} else if (invoiceConfig.getFormatYear().equals("2019")) {
								invPrefix += (Integer.valueOf(year).toString());
								startInvNo += (Integer.valueOf(year).toString());
								endInvNo += (Integer.valueOf(year).toString());
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()), "");
							} else if (invoiceConfig.getFormatYear().equals("2019/")) {
								invPrefix += (Integer.valueOf(year).toString()) + "/";
								startInvNo += (Integer.valueOf(year).toString()) + "/";
								endInvNo += (Integer.valueOf(year).toString()) + "/";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "/", "");
							} else if (invoiceConfig.getFormatYear().equals("2019-")) {
								invPrefix += (Integer.valueOf(year).toString()) + "-";
								startInvNo += (Integer.valueOf(year).toString()) + "-";
								endInvNo += (Integer.valueOf(year).toString()) + "-";
								actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-", "");
							} else if (invoiceConfig.getFormatYear().equals("19-20")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("19-20-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("19-20/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2)) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ "-" + (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString().substring(2)) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ "-" + (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("1920/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString().substring(2))
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString().substring(2))
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("2019-20/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString()) + "-"
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()) + "-"
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)), "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2));
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)), "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920-")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "-", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "-", "");
								}
							} else if (invoiceConfig.getFormatYear().equals("201920/")) {
								if (month <= 3) {
									invPrefix += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year - 1).toString())
											+ (Integer.valueOf(year).toString().substring(2)) + "/", "");
								} else {
									invPrefix += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									startInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									endInvNo += (Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/";
									actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString())
											+ (Integer.valueOf(year + 1).toString().substring(2)) + "/", "");
								}
							}
						} else {
							invPrefix += (Integer.valueOf(year).toString());
							startInvNo += (Integer.valueOf(year).toString());
							endInvNo += (Integer.valueOf(year).toString());
							actualNo = actualNo.replaceFirst((Integer.valueOf(year).toString()), "");
						}
					}
					String ssinvno = startInvNo;
					if (isNotEmpty(invoiceConfig.getStartInvoiceNo())) {
						startInvNo += invoiceConfig.getStartInvoiceNo();
					}
					if (isNotEmpty(invoiceConfig.getEndInvoiceNo())) {
						endInvNo += invoiceConfig.getEndInvoiceNo();
					}
					String invOrRevNo = "";
					if(returntype.equals("Purchase Register")) {
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getRevchargeNo())) {
							invOrRevNo = invoice.getRevchargeNo();
						}
					}else {
						invOrRevNo = invoice.getB2b().get(0).getInv().get(0).getInum();
					}
					if (invOrRevNo.startsWith(ssinvno)) {
						ClientAddlInfo clientInfo = clientAddlInfoRepository.findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYearAndMonth(invoice.getClientid(), returntype, invType, submissionYear, month);
						if (isEmpty(clientInfo)) {
							clientInfo = clientAddlInfoRepository.findByClientIdAndReturnTypeAndInvoiceTypeAndFinancialYearAndMonth(invoice.getClientid(), returntype, "ALL", submissionYear, month);
						}
						if (isEmpty(clientInfo)) {
							clientInfo = new ClientAddlInfo();
							clientInfo.setClientId(invoice.getClientid());
							clientInfo.setReturnType(returntype);
							clientInfo.setFinancialYear(submissionYear);
							clientInfo.setMonth(month);
							try {
								Integer invNo = Integer.parseInt(actualNo);
								invNo++;
								clientInfo.setInvoiceNo(invPrefix + invNo);
								if (isNotEmpty(invoiceConfig.getInvoiceType()) && !invoiceConfig.equals("ALL")) {
									clientInfo.setInvoiceType(invoiceConfig.getInvoiceType());
								}
								clientAddlInfoRepository.save(clientInfo);
							} catch (Exception e) {
							}
						} else {
							String invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
							if("PurchaseReverseChargeNo".equals(invType)) {
								if(isNotEmpty(invoice.getRevchargeNo())) {
									invnum = invoice.getRevchargeNo();
								}
							}
							
							if (clientInfo.getInvoiceNo().compareTo(invnum) <= 0) {
								try {
									Integer invNo = Integer.parseInt(actualNo);
									invNo++;
									clientInfo.setInvoiceNo(invPrefix + invNo);
									if (isNotEmpty(invoiceConfig.getInvoiceType()) && !invoiceConfig.equals("ALL")) {
										clientInfo.setInvoiceType(invoiceConfig.getInvoiceType());
									}
									clientAddlInfoRepository.save(clientInfo);
								} catch (Exception e) {
								}
							}
						}
					}
				}
			}
		//}
		List<GSTRItems> gstrItems = Lists.newArrayList();
		String stateTin = getStateCode(invoice.getStatename());
		if (isNotEmpty(invoice.getItems())) {
			if (isEmpty(invoice.getInvtype())) {
				List<Item> fItems = Lists.newArrayList();
				for (Item item : invoice.getItems()) {
					if (isEmpty(item.getQuantity())) {
						item.setQuantity(1d);
					}
					if (isEmpty(item.getRateperitem())) {
						if (isNotEmpty(item.getTaxablevalue())) {
							item.setRateperitem(item.getTaxablevalue());
						}
					}
					if(isNotEmpty(invoice.getGstr1orEinvoice()) && "Einvoice".equalsIgnoreCase(invoice.getGstr1orEinvoice())) {
						if(isNotEmpty(item.getTaxablevalue())) {
							item.setAssAmt(item.getTaxablevalue());
						}
					}
					if (isEmpty(item.getCategory())) {
						if (isNotEmpty(item.getHsn()) && item.getHsn().contains(" : ")) {
							item.setCategory(MasterGSTConstants.GOODS);
						} else {
							item.setCategory(MasterGSTConstants.SERVICES);
						}
					}
					if ((isNotEmpty(item.getIgstrate()) || isNotEmpty(item.getCgstrate()))
							|| (isNotEmpty(item.getHsn()) || isNotEmpty(item.getSac()) || isNotEmpty(item.getRate()))) {
						if (isNotEmpty(item.getRate()) && item.getRate() > 0) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0
									&& isEmpty(item.getIgstrate())) {
								item.setIgstrate(item.getRate());
							} else if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0
									&& isNotEmpty(item.getSgstamount()) && isEmpty(item.getCgstrate())
									&& isEmpty(item.getSgstrate())) {
								item.setCgstrate(item.getRate() / 2);
								item.setSgstrate(item.getRate() / 2);
							}
						}
						fItems.add(item);
					}
				}
				invoice.setItems(fItems);
			}
			if (!invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
				gstrItems = populateItemData(invoice, isIntraState, returntype);
			}
		}
		if (isNotEmpty(invoice.getRevchargetype()) && !invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_TCS)) {
			if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
				invoice.getB2b().get(0).getInv().get(0).setEtin("");
				invoice.setEcomoperatorid("");
			}
		}
		if (isEmpty(invoice.getInvoiceno()) && isNotEmpty(invoice.getB2b())
				&& isNotEmpty(invoice.getB2b().get(0).getInv())
				&& isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
			invoice.setInvoiceno(invoice.getB2b().get(0).getInv().get(0).getInum());
		}
		if (isEmpty(invoice.getInvtype())) {
			invoice.setExp(Lists.newArrayList());
			invoice.setTxpd(Lists.newArrayList());
			invoice.setCdn(Lists.newArrayList());
			invoice.setCdnur(Lists.newArrayList());
			if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
				if (invoice.getTotalamount() > 250000 && !isIntraState) {
					invoice.setInvtype(B2CL);
					if (isEmpty(invoice.getB2cl())) {
						List<GSTRB2CL> b2cl = Lists.newArrayList();
						GSTRB2CL gstrb2clInvoice = new GSTRB2CL();
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						inv.add(gstrInvoiceDetail);
						gstrb2clInvoice.setInv(inv);
						b2cl.add(gstrb2clInvoice);
						invoice.setB2cl(b2cl);
					} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						inv.add(gstrInvoiceDetail);
						invoice.getB2cl().get(0).setInv(inv);
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setInum(invoice.getB2b().get(0).getInv().get(0).getInum().toUpperCase());
						}
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
					}
					if (isNotEmpty(ecomtin)) {
						invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
					}
					invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						invoice.getB2cl().get(0).setPos(stateTin);
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
					}
					if (isNotEmpty(gstrItems)) {
						for (GSTRItems gstrItem : gstrItems) {
							if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
								gstrItem.getItem().setAdvAmt(null);
							}
						}
					}
					invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
				} else {
					invoice.setInvtype(B2C);
					if (isEmpty(invoice.getB2cs())) {
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						invoice.setB2cs(b2cs);
					}
					List<GSTRB2CS> b2cs = Lists.newArrayList();
					for (Item item : invoice.getItems()) {
						GSTRB2CS gstrb2csDetail = new GSTRB2CS();
						if (isNotEmpty(item.getIgstrate())) {
							gstrb2csDetail.setRt(item.getIgstrate());
						} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
							gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
						}
						if (!isIntraState && isNotEmpty(item.getIgstamount())) {
							gstrb2csDetail.setIamt(item.getIgstamount());
							// gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
							gstrb2csDetail.setCamt(item.getCgstamount());
							gstrb2csDetail.setSamt(item.getSgstamount());
						}
						if (isIntraState) {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}

						gstrb2csDetail.setCsamt(item.getCessamount());
						gstrb2csDetail.setTxval(item.getTaxablevalue());
						if (isNotEmpty(invoice.getStatename())) {
							gstrb2csDetail.setPos(stateTin);
						}
						String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
						gstrb2csDetail.setTyp("OE");
						if (isNotEmpty(ecomtin)) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(ecomtin.toUpperCase());
						} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrb2csDetail.setDiffPercent(0.65);
						}
						b2cs.add(gstrb2csDetail);
					}
					invoice.setB2cs(b2cs);
				}
			} else {
				invoice.setInvtype(B2B);
				if (isEmpty(invoice.getB2b().get(0).getInv())) {
					List<GSTRInvoiceDetails> inv = Lists.newArrayList();
					GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
					List<GSTRItems> itms = Lists.newArrayList();
					GSTRItems gstrItem = new GSTRItems();
					gstrItem.setNum(1);
					itms.add(gstrItem);
					gstrInvoiceDetail.setItms(itms);
					String diffper = "";
					diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						gstrInvoiceDetail.setDiffPercent(0.65);
					}
					inv.add(gstrInvoiceDetail);
					invoice.getB2b().get(0).setInv(inv);
				} else if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getItms())) {
					List<GSTRItems> itms = Lists.newArrayList();
					GSTRItems gstrItem = new GSTRItems();
					gstrItem.setNum(1);
					itms.add(gstrItem);
					invoice.getB2b().get(0).getInv().get(0).setItms(itms);
				}
				if (isNotEmpty(ecomtin)) {
					invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
				}
				String diffper = "";
				diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					invoice.getB2b().get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
				}
				if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				} else {
					String invTyp = invoice.getB2b().get(0).getInv().get(0).getInvTyp();
					if ("Regular".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else if ("Deemed Exports".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
					} else if ("Supplies to SEZ with payment".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
					} else if ("Supplies to SEZ without payment".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
					} else if ("Sale from Bonded Warehouse".equals(invTyp)) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
					} else {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
					}
				}
				invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
				}
				if ((isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE))
						|| (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("YES")) || (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("Y"))) {
					invoice.setRevchargetype(MasterGSTConstants.TYPE_REVERSE);
					invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
				} else {
					invoice.setRevchargetype("Regular");
					invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
				}
				if (isNotEmpty(gstrItems)) {
					for (GSTRItems gstrItem : gstrItems) {
						if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
							gstrItem.getItem().setAdvAmt(null);
						}
					}
				}
				invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
			}
		} else {
			if (invoice.getInvtype().equals(B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.setB2cs(Lists.newArrayList());
					invoice.setB2cl(Lists.newArrayList());
					invoice.setInvtype(B2B);
					if (isEmpty(invoice.getB2b().get(0).getInv())) {
						List<GSTRInvoiceDetails> inv = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						gstrInvoiceDetail.setItms(itms);
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrInvoiceDetail.setDiffPercent(0.65);
						}
						inv.add(gstrInvoiceDetail);
						invoice.getB2b().get(0).setInv(inv);
					} else if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getItms())) {
						List<GSTRItems> itms = Lists.newArrayList();
						GSTRItems gstrItem = new GSTRItems();
						gstrItem.setNum(1);
						itms.add(gstrItem);
						invoice.getB2b().get(0).getInv().get(0).setItms(itms);
					}
					String diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						invoice.getB2b().get(0).getInv().get(0)
								.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
					}
					if (isEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					} else {
						invoice.getB2b().get(0).getInv().get(0)
								.setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					}
					invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
					if (isNotEmpty(invoice.getStatename())) {
						invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
					}
					if ((isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) || (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("Y"))) {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					if (isNotEmpty(gstrItems)) {
						for (GSTRItems gstrItem : gstrItems) {
							if (isNotEmpty(gstrItem.getItem()) && isNotEmpty(gstrItem.getItem().getAdvAmt())) {
								gstrItem.getItem().setAdvAmt(null);
							}
						}
					}
					invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
					if (isNotEmpty(ecomtin)) {
						invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
					}
				} else {

					String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
					List<GSTRB2CS> b2cs = Lists.newArrayList();
					for (Item item : invoice.getItems()) {
						GSTRB2CS gstrb2csDetail = new GSTRB2CS();
						if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
							gstrb2csDetail.setRt(item.getIgstrate());
						} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
							gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
						}
						int retval = 0;
						if (isNotEmpty(item.getIgstamount())) {
							retval = Double.compare(0d, item.getIgstamount());
						}
						if (!isIntraState && isNotEmpty(item.getIgstamount()) && (retval < 0 || retval > 0)) {
							gstrb2csDetail.setIamt(item.getIgstamount());
						} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
							gstrb2csDetail.setCamt(item.getCgstamount());
							gstrb2csDetail.setSamt(item.getSgstamount());
						}

						if (isIntraState) {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
						gstrb2csDetail.setCsamt(item.getCessamount());
						gstrb2csDetail.setTxval(item.getTaxablevalue());
						if (isNotEmpty(invoice.getStatename())) {
							gstrb2csDetail.setPos(stateTin);
						}
						gstrb2csDetail.setTyp("OE");
						if (isNotEmpty(ecomtin)) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(ecomtin.toUpperCase());
						} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							gstrb2csDetail.setTyp("E");
							gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							gstrb2csDetail.setDiffPercent(0.65);
						}
						b2cs.add(gstrb2csDetail);
					}
					invoice.setB2cs(b2cs);
				}
			}else if (invoice.getInvtype().equals(EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				if (invoice.getExp() == null) {
					List<GSTRExports> expList = Lists.newArrayList();
					GSTRExports gstrExport = new GSTRExports();
					List<GSTRExportDetails> inv = Lists.newArrayList();
					GSTRExportDetails gstrExportDetail = new GSTRExportDetails();
					inv.add(gstrExportDetail);
					gstrExport.setInv(inv);
					expList.add(gstrExport);
					invoice.setExp(expList);
				} else if (isEmpty(invoice.getExp().get(0).getInv())) {
					List<GSTRExportDetails> inv = Lists.newArrayList();
					GSTRExportDetails gstrExportDetail = new GSTRExportDetails();
					inv.add(gstrExportDetail);
					invoice.getExp().get(0).setInv(inv);
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getExp().get(0).getInv().get(0)
								.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
				}
				String diffper = "";
				diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getExp().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
					ntdt = ntdt+"T18:30:000Z";
					SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
					try {
						invoice.getExp().get(0).getInv().get(0).setIdt(simpleDateFormat1.parse(ntdt));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				invoice.getExp().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				List<GSTRItemDetails> gstrItemDetails = Lists.newArrayList();
				for (GSTRItems gstrItem : gstrItems) {
					gstrItemDetails.add(gstrItem.getItem());
				}
				invoice.getExp().get(0).getInv().get(0).setItms(gstrItemDetails);
			} else if (invoice.getInvtype().equals(CREDIT_DEBIT_NOTES)
					|| invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {

				if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.setInvtype(CDNUR);
					if (isEmpty(invoice.getCdnur())) {
						List<GSTRInvoiceDetails> cdnrList = Lists.newArrayList();
						GSTRInvoiceDetails gstrCreditDebitNote = new GSTRInvoiceDetails();
						cdnrList.add(gstrCreditDebitNote);
						invoice.setCdnur(cdnrList);
					}
					String type = "";
					type = invoice.getCdnur().get(0).getTyp();
					String invType = invoice.getCdnur().get(0).getInvTyp();
					if ((isEmpty(invoice.getCdnur().get(0).getNtNum()) && isEmpty(invoice.getCdnur().get(0).getNtDt()))
							&& (isNotEmpty(invoice.getCdn().get(0).getNt())
									&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
						invoice.setCdnur(invoice.getCdn().get(0).getNt());
						if (returntype.equals(PURCHASE_REGISTER)) {
							invoice.getCdnur().get(0).setInvTyp(invType);
						} else {
							invoice.getCdnur().get(0).setTyp(type);
							if("B2CL".equalsIgnoreCase(type)) {
								invoice.getCdnur().get(0).setPos(stateTin);
							}
						}
					}
					if ((isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty()))) {
						invoice.getCdnur().get(0).setNtty(invoice.getCdn().get(0).getNt().get(0).getNtty());
					}
					invoice.setB2cs(Lists.newArrayList());
					invoice.setExp(Lists.newArrayList());
					invoice.setTxpd(Lists.newArrayList());
					invoice.setCdn(Lists.newArrayList());
					if (isNotEmpty(invoice.getCdnur().get(0).getNtNum())) {
						invoice.getCdnur().get(0).setInum(invoice.getCdnur().get(0).getNtNum());
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							invoice.getCdnur().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum());
						}
					}
					if (isNotEmpty(invoice.getCdnur().get(0).getNtDt())) {
						invoice.getCdnur().get(0).setIdt(simpleDateFormat.format(invoice.getCdnur().get(0).getNtDt()));
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
						ntdt = ntdt+"T18:30:000Z";
						SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
						try {
							invoice.getCdnur().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						invoice.getCdnur().get(0).setRtin(invoice.getB2b().get(0).getCtin().toUpperCase());
					}
					if (returntype.equals(GSTR4)) {
						if (isIntraState) {
							invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
					}
					if (isNotEmpty(invoice.getRevchargetype())
							&& invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) {
						invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					
					String diffper = "";
					diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						invoice.getCdnur().get(0).setDiffPercent(0.65);
					}
					invoice.getCdnur().get(0).setVal(invoice.getTotalamount());
					invoice.getCdnur().get(0).setItms(gstrItems);
				} else {
					invoice.setB2cs(Lists.newArrayList());
					invoice.setExp(Lists.newArrayList());
					invoice.setTxpd(Lists.newArrayList());
					invoice.setCdnur(Lists.newArrayList());
					List<GSTRCreditDebitNotes> notes = null;
					if (returntype.equals(GSTR1)) {
						notes = ((GSTR1) invoice).getCdnr();
					} else if (returntype.equals(GSTR2)) {
						notes = ((GSTR2) invoice).getCdn();
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						notes = ((PurchaseRegister) invoice).getCdn();
					} else if (returntype.equals(GSTR4)) {
						notes = ((GSTR4) invoice).getCdnr();
					} else if (returntype.equals(GSTR6)) {
						notes = ((GSTR6) invoice).getCdn();
					} else if (returntype.equals(GSTR5)) {
						notes = ((GSTR5) invoice).getCdn();
					}
					if ((returntype.equals(GSTR1) || returntype.equals(GSTR4) || returntype.equals(GSTR5))
							&& isNotEmpty(notes)) {
						if ((isEmpty(notes.get(0).getNt()) || (isEmpty(notes.get(0).getNt().get(0).getNtNum())
								&& isEmpty(notes.get(0).getNt().get(0).getNtDt())))
								&& (isNotEmpty(invoice.getCdn().get(0).getNt())
										&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
							notes.get(0).setNt(invoice.getCdn().get(0).getNt());
						}
					} else if ((returntype.equals(GSTR1) || returntype.equals(GSTR4) || returntype.equals(GSTR5))
							&& isEmpty(notes)) {
						notes = Lists.newArrayList();
						notes.add(invoice.getCdn().get(0));
					}
					invoice.setCdn(Lists.newArrayList());
					if (isEmpty(notes)) {
						List<GSTRCreditDebitNotes> cdnrList = Lists.newArrayList();
						GSTRCreditDebitNotes gstrCreditDebitNote = new GSTRCreditDebitNotes();
						List<GSTRInvoiceDetails> nt = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						nt.add(gstrInvoiceDetail);
						gstrCreditDebitNote.setNt(nt);
						cdnrList.add(gstrCreditDebitNote);
						if (returntype.equals(GSTR1)) {
							((GSTR1) invoice).setCdnr(cdnrList);
						} else if (returntype.equals(GSTR2)) {
							((GSTR2) invoice).setCdn(cdnrList);
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							((PurchaseRegister) invoice).setCdn(cdnrList);
						} else if (returntype.equals(GSTR4)) {
							((GSTR4) invoice).setCdnr(cdnrList);
						} else if (returntype.equals(GSTR6)) {
							((GSTR6) invoice).setCdn(cdnrList);
						} else if (returntype.equals(GSTR5)) {
							((GSTR5) invoice).setCdn(cdnrList);
						}
					} else if (isEmpty(notes.get(0).getNt())) {
						List<GSTRInvoiceDetails> nt = Lists.newArrayList();
						GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
						nt.add(gstrInvoiceDetail);
						notes.get(0).setNt(nt);
					}
					if (isNotEmpty(notes.get(0).getNt().get(0).getNtNum())) {
						notes.get(0).getNt().get(0).setInum(notes.get(0).getNt().get(0).getNtNum());
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							notes.get(0).getNt().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum());
						}
					}
					if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						notes.get(0).setCtin(invoice.getB2b().get(0).getCtin().toUpperCase());
					}
					if (isNotEmpty(notes.get(0).getNt().get(0).getNtDt())) {
						notes.get(0).getNt().get(0).setIdt(simpleDateFormat.format(notes.get(0).getNt().get(0).getNtDt()));
					}
					if (isNotEmpty(invoice.getDateofinvoice())) {
						String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
						ntdt = ntdt+"T18:30:000Z";
						SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
						try {
							notes.get(0).getNt().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					if ((isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) || (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("Y"))) {
						notes.get(0).getNt().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
					} else {
						notes.get(0).getNt().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
					}
					if(isNotEmpty(invoice.getStatename())) {
						notes.get(0).getNt().get(0).setPos(stateTin);
					}
					if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						notes.get(0).getNt().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
					}
					String diffper = "";
					diffper = invoice.getDiffPercent();
					if ("Yes".equals(diffper)) {
						notes.get(0).getNt().get(0).setDiffPercent(0.65);
					}
					if (returntype.equals(GSTR4)) {
						if (isIntraState) {
							notes.get(0).getNt().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
						} else {
							notes.get(0).getNt().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
						}
					}
					notes.get(0).getNt().get(0).setVal(invoice.getTotalamount());
					notes.get(0).getNt().get(0).setItms(gstrItems);
					if (returntype.equals(GSTR1)) {
						((GSTR1) invoice).setCdnr(notes);
					} else if (returntype.equals(GSTR2)) {
						((GSTR2) invoice).setCdn(notes);
					} else if (returntype.equals(PURCHASE_REGISTER)) {
						((PurchaseRegister) invoice).setCdn(notes);
					} else if (returntype.equals(GSTR4)) {
						((GSTR4) invoice).setCdnr(notes);
					} else if (returntype.equals(GSTR6)) {
						((GSTR6) invoice).setCdn(notes);
					} else if (returntype.equals(GSTR5)) {
						((GSTR5) invoice).setCdn(notes);
					}
				}

			} else if (invoice.getInvtype().equals(CDNUR) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {

				if (isEmpty(invoice.getCdnur())) {
					List<GSTRInvoiceDetails> cdnrList = Lists.newArrayList();
					GSTRInvoiceDetails gstrCreditDebitNote = new GSTRInvoiceDetails();
					cdnrList.add(gstrCreditDebitNote);
					invoice.setCdnur(cdnrList);
				}
				String type = "";
				type = invoice.getCdnur().get(0).getTyp();
				String invType = invoice.getCdnur().get(0).getInvTyp();
				if ((isEmpty(invoice.getCdnur().get(0).getNtNum()) && isEmpty(invoice.getCdnur().get(0).getNtDt()))
						&& (isNotEmpty(invoice.getCdn().get(0).getNt())
								&& isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtNum()))) {
					invoice.setCdnur(invoice.getCdn().get(0).getNt());
					if (returntype.equals(PURCHASE_REGISTER)) {
						invoice.getCdnur().get(0).setInvTyp(invType);
					} else {
						invoice.getCdnur().get(0).setTyp(type);
					}
				}
				if ((isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty()))) {
					invoice.getCdnur().get(0).setNtty(invoice.getCdn().get(0).getNt().get(0).getNtty());
				}
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				
				if (isNotEmpty(invoice.getCdnur().get(0).getNtNum())) {
					invoice.getCdnur().get(0).setInum(invoice.getCdnur().get(0).getNtNum());
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getCdnur().get(0).setNtNum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
				}
				if (isNotEmpty(invoice.getCdnur().get(0).getNtDt())) {
					invoice.getCdnur().get(0).setIdt(simpleDateFormat.format(invoice.getCdnur().get(0).getNtDt()));
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					String ntdt = simpleDateFormat.format(invoice.getDateofinvoice());
					ntdt = ntdt+"T18:30:000Z";
					SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:sss'Z'");
					try {
						invoice.getCdnur().get(0).setNtDt(simpleDateFormat1.parse(ntdt));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
					invoice.getCdnur().get(0).setRtin(invoice.getB2b().get(0).getCtin().toUpperCase());
				}
				if (returntype.equals(GSTR4)) {
					if (isIntraState) {
						invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
					} else {
						invoice.getCdnur().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
					}
				}
				if ((isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) || (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("Y"))) {
					invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
				} else {
					invoice.getCdnur().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getCdnur().get(0).setDiffPercent(0.65);
				}
				invoice.getCdnur().get(0).setVal(invoice.getTotalamount());
				invoice.getCdnur().get(0).setItms(gstrItems);

			} else if (invoice.getInvtype().equals(B2CL) || invoice.getInvtype().equals(MasterGSTConstants.B2CLA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isEmpty(invoice.getB2cl())) {
					GSTRB2CL gstrb2cl = new GSTRB2CL();
					gstrb2cl.getInv().add(new GSTRInvoiceDetails());
					invoice.getB2cl().add(gstrb2cl);
				} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
					invoice.getB2cl().get(0).getInv().add(new GSTRInvoiceDetails());
				}
				if (isNotEmpty(invoice.getDateofinvoice())) {
					invoice.getB2cl().get(0).getInv().get(0)
							.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
				}
				if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
						invoice.getB2cl().get(0).getInv().get(0)
								.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
					}
				}
				invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2cl().get(0).setPos(stateTin);
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(ecomtin)) {
					invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
				}
				invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
			} else if (invoice.getInvtype().equals(B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2BA)) {
				invoice.setB2cs(Lists.newArrayList());
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if ("GSTR1".equals(returntype)
						&& (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin()))) {
					if (invoice.getTotalamount() > 250000 && !isIntraState) {
						invoice.setInvtype(B2CL);
						if (isEmpty(invoice.getB2cl())) {
							List<GSTRB2CL> b2cl = Lists.newArrayList();
							GSTRB2CL gstrb2clInvoice = new GSTRB2CL();
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrInvoiceDetail.setDiffPercent(0.65);
							}
							List<GSTRItems> itms = Lists.newArrayList();
							GSTRItems gstrItem = new GSTRItems();
							gstrItem.setNum(1);
							itms.add(gstrItem);
							gstrInvoiceDetail.setItms(itms);
							inv.add(gstrInvoiceDetail);
							gstrb2clInvoice.setInv(inv);
							b2cl.add(gstrb2clInvoice);
							invoice.setB2cl(b2cl);
							if (isNotEmpty(ecomtin)) {
								invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
							}
						} else if (isEmpty(invoice.getB2cl().get(0).getInv())) {
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrInvoiceDetail.setDiffPercent(0.65);
							}
							List<GSTRItems> itms = Lists.newArrayList();
							GSTRItems gstrItem = new GSTRItems();
							gstrItem.setNum(1);
							itms.add(gstrItem);
							gstrInvoiceDetail.setItms(itms);
							inv.add(gstrInvoiceDetail);
							invoice.getB2cl().get(0).setInv(inv);
						}
						if (isNotEmpty(invoice.getDateofinvoice())) {
							invoice.getB2cl().get(0).getInv().get(0)
									.setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								invoice.getB2cl().get(0).getInv().get(0)
										.setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
							}
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								invoice.getB2cl().get(0).getInv().get(0)
										.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin());
							}
						}
						invoice.getB2cl().get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							invoice.getB2cl().get(0).setPos(stateTin);
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							invoice.getB2cl().get(0).getInv().get(0).setDiffPercent(0.65);
						}
						if (isNotEmpty(ecomtin)) {
							invoice.getB2cl().get(0).getInv().get(0).setEtin(ecomtin);
						}
						invoice.getB2cl().get(0).getInv().get(0).setItms(gstrItems);
					} else {
						invoice.setInvtype(B2C);
						if (isEmpty(invoice.getB2cs())) {
							List<GSTRB2CS> b2cs = Lists.newArrayList();
							invoice.setB2cs(b2cs);
						}
						List<GSTRB2CS> b2cs = Lists.newArrayList();
						for (Item item : invoice.getItems()) {
							GSTRB2CS gstrb2csDetail = new GSTRB2CS();
							if (isNotEmpty(item.getIgstrate())) {
								gstrb2csDetail.setRt(item.getIgstrate());
							} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())) {
								gstrb2csDetail.setRt(item.getCgstrate() + item.getSgstrate());
							}
							if (!isIntraState && isNotEmpty(item.getIgstamount())) {
								gstrb2csDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrb2csDetail.setCamt(item.getCgstamount());
								gstrb2csDetail.setSamt(item.getSgstamount());
							}
							if (isIntraState) {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTRA);
							} else {
								gstrb2csDetail.setSplyTy(MasterGSTConstants.SUPPLY_TYPE_INTER);
							}
							gstrb2csDetail.setCsamt(item.getCessamount());
							gstrb2csDetail.setTxval(item.getTaxablevalue());
							if (isNotEmpty(invoice.getStatename())) {
								gstrb2csDetail.setPos(stateTin);
							}
							String ecomGSTIN = null;
							if (!invoice.getB2cs().isEmpty()) {
								ecomGSTIN = invoice.getB2cs().get(0).getEtin();
							}
							gstrb2csDetail.setTyp("OE");
							if (isNotEmpty(ecomtin)) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(ecomtin.toUpperCase());
							} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								gstrb2csDetail.setTyp("E");
								gstrb2csDetail.setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
							}
							String diffper = invoice.getDiffPercent();
							if ("Yes".equals(diffper)) {
								gstrb2csDetail.setDiffPercent(0.65);
							}
							b2cs.add(gstrb2csDetail);
						}
						invoice.setB2cs(b2cs);
					}
				} else if (!returntype.equals(ANX1)) {

					if (isEmpty(invoice.getB2b()) || isEmpty(invoice.getB2b().get(0).getCtin())) {
						invoice.setB2cs(Lists.newArrayList());
						invoice.setExp(Lists.newArrayList());
						invoice.setTxpd(Lists.newArrayList());
						invoice.setCdn(Lists.newArrayList());
						invoice.setCdnur(Lists.newArrayList());
						invoice.setInvtype(MasterGSTConstants.B2BUR);
						List<GSTRB2B> b2bur = null;
						if (returntype.equals(GSTR2)) {
							b2bur = ((GSTR2) invoice).getB2bur();
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							b2bur = ((PurchaseRegister) invoice).getB2bur();
						} else if (returntype.equals(GSTR4)) {
							b2bur = ((GSTR4) invoice).getB2bur();
						}
						if (isEmpty(b2bur)) {
							b2bur = Lists.newArrayList();
							GSTRB2B gstrb2b = new GSTRB2B();
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							inv.add(gstrInvoiceDetail);
							gstrb2b.setInv(inv);
							b2bur.add(gstrb2b);
						} else if (isEmpty(b2bur.get(0).getInv())) {
							List<GSTRInvoiceDetails> inv = Lists.newArrayList();
							GSTRInvoiceDetails gstrInvoiceDetail = new GSTRInvoiceDetails();
							inv.add(gstrInvoiceDetail);
							b2bur.get(0).setInv(inv);
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
							if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								b2bur.get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
							}
						}
						if (isNotEmpty(invoice.getDateofinvoice())) {
							b2bur.get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						if (returntype.equals(GSTR4)) {
							if (isIntraState) {
								b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTRA);
							} else {
								b2bur.get(0).getInv().get(0).setSplyType(MasterGSTConstants.SUPPLY_TYPE_INTER);
							}
						}
						b2bur.get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							b2bur.get(0).getInv().get(0).setPos(stateTin);
						}
						b2bur.get(0).getInv().get(0).setItms(gstrItems);
						if (returntype.equals(GSTR2)) {
							((GSTR2) invoice).setB2bur(b2bur);
						} else if (returntype.equals(PURCHASE_REGISTER)) {
							((PurchaseRegister) invoice).setB2bur(b2bur);
						} else if (returntype.equals(GSTR4)) {
							((GSTR4) invoice).setB2bur(b2bur);
						}

					} else {

						if (isNotEmpty(invoice.getDateofinvoice())) {
							invoice.getB2b().get(0).getInv().get(0).setIdt(simpleDateFormat.format(invoice.getDateofinvoice()));
						}
						invoice.getB2b().get(0).getInv().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
						invoice.getB2b().get(0).getInv().get(0).setVal(invoice.getTotalamount());
						if (isNotEmpty(invoice.getStatename())) {
							invoice.getB2b().get(0).getInv().get(0).setPos(stateTin);
						}
						if ((isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals(MasterGSTConstants.TYPE_REVERSE)) || (isNotEmpty(invoice.getRevchargetype()) && invoice.getRevchargetype().equals("Y"))) {
							invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_YES);
						} else {
							invoice.getB2b().get(0).getInv().get(0).setRchrg(MasterGSTConstants.REVERSE_CHARGE_NO);
						}
						String diffper = invoice.getDiffPercent();
						if ("Yes".equals(diffper)) {
							invoice.getB2b().get(0).getInv().get(0).setDiffPercent(0.65);
						}
						invoice.getB2b().get(0).getInv().get(0).setItms(gstrItems);
						if (isNotEmpty(ecomtin)) {
							invoice.getB2b().get(0).getInv().get(0).setEtin(ecomtin);
						}
					}
				}
			} else if (invoice.getInvtype().equals(B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());

				if (isIntraState && isNotEmpty(invoice.getItems().get(0).getCgstrate())
						&& isNotEmpty(invoice.getItems().get(0).getSgstrate())) {
					invoice.getB2cs().get(0)
							.setRt(invoice.getItems().get(0).getCgstrate() + invoice.getItems().get(0).getSgstrate());
					invoice.getB2cs().get(0).setCamt(invoice.getItems().get(0).getCgstamount());
					invoice.getB2cs().get(0).setSamt(invoice.getItems().get(0).getSgstamount());
				} else if (isNotEmpty(invoice.getItems().get(0).getIgstrate())) {
					invoice.getB2cs().get(0).setRt(invoice.getItems().get(0).getIgstrate());
					invoice.getB2cs().get(0).setIamt(invoice.getItems().get(0).getIgstamount());
				}
				if (isNotEmpty(invoice.getItems().get(0).getCessamount())) {
					invoice.getB2cs().get(0).setCsamt(invoice.getItems().get(0).getCessamount());
				}
				if (isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
					invoice.getB2cs().get(0).setTxval(invoice.getItems().get(0).getTaxablevalue());
				}
				String ecomGSTIN = invoice.getB2cs().get(0).getEtin();
				invoice.getB2cs().get(0).setTyp("OE");
				if (isNotEmpty(ecomtin)) {
					invoice.getB2cs().get(0).setTyp("E");
					invoice.getB2cs().get(0).setEtin(ecomtin.toUpperCase());
				} else if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
					invoice.getB2cs().get(0).setTyp("E");
					invoice.getB2cs().get(0).setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin().toUpperCase());
				}
				String diffper = invoice.getDiffPercent();
				if ("Yes".equals(diffper)) {
					invoice.getB2cs().get(0).setDiffPercent(0.65);
				}
				if (isNotEmpty(invoice.getStatename())) {
					invoice.getB2cs().get(0).setPos(stateTin);
				}
			}

		}
		if (isEmpty(invoice.getDateofinvoice())) {
			invoice.setDateofinvoice(new Date());
		}
		int mnths = -1,yrs=-1;
		if("Advances".equals(invoice.getInvtype()) || "Nil Supplies".equals(invoice.getInvtype()) || "Advance Adjusted Detail".equals(invoice.getInvtype())){
			String fp = invoice.getFp();
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
			
			invoice.setMthCd(""+mnths);
			invoice.setYrCd(""+yearCode);
			invoice.setQrtCd(""+quarter);
			invoice.setSftr(sumFactor);
		}else {
			
		String invType = invoice.getInvtype();
		boolean isDebitCreditNotes = "Credit/Debit Notes".equals(invType);
		boolean isCDNA = "CDNA".equals(invType);
		boolean isCDNUR = "Credit/Debit Note for Unregistered Taxpayers".equals(invType);
		boolean isCreditNote = "Credit Note".equals(invType);
		boolean isDebitNote = "Debit Note".equals(invType);
		boolean isCreditNoteUr = "Credit Note(UR)".equals(invType);
		boolean isDebitNoteUr = "Debit Note(UR)".equals(invType);
		boolean isCdnNtNttyExists = invoice.getCdn() != null && invoice.getCdn().size() > 0 && invoice.getCdn().get(0).getNt() != null && invoice.getCdn().get(0).getNt().size() > 0;
		String cdnNtNtty = null;
		if(isCdnNtNttyExists){
			cdnNtNtty = invoice.getCdn().get(0).getNt().get(0).getNtty();
		}
		boolean isCdnurNttyExists = invoice.getCdnur() != null && invoice.getCdnur().size() > 0;
		String cdnurNtty = null;
		if(isCdnurNttyExists){
			cdnurNtty = invoice.getCdnur().get(0).getNtty();
		}
		int sumFactor = 1;
		if(!"CANCELLED".equals(invoice.getGstStatus())){
			if(returntype.equals("GSTR1")) {
				boolean isCdnrNtNttyExists = ((GSTR1)invoice).getCdnr() != null && ((GSTR1)invoice).getCdnr().size() > 0 && ((GSTR1)invoice).getCdnr().get(0).getNt() != null && ((GSTR1)invoice).getCdnr().get(0).getNt().size() > 0;
				String cdnrNtNtty = null;
				if(isCdnrNtNttyExists){
				cdnrNtNtty = ((GSTR1)invoice).getCdnr().get(0).getNt().get(0).getNtty();
				}
				if((isDebitCreditNotes || isCDNA )){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
						sumFactor = -1;
						}
					}else if(isCdnrNtNttyExists){
						if("C".equals(cdnrNtNtty)){
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
					}else if(isCdnrNtNttyExists){
						if("C".equals(cdnrNtNtty)){
						sumFactor = -1;
						}
					}
				}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
					sumFactor = -1;
					}
				}
			}else {
				if((isDebitCreditNotes || isCDNA )){
					if(isCdnNtNttyExists){
						if(!"D".equals(cdnNtNtty)){
							sumFactor = -1;
						}
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
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
					}/*else if(isCdnrNtNttyExists){
		if("C".equals(cdnrNtNtty)){
		sumFactor = -1;
		}
		}*/
				}else if((isCreditNoteUr || isDebitNoteUr || isCDNUR) && isCdnurNttyExists){
					if("C".equals(cdnurNtty)){
						sumFactor = -1;
					}
				}
			}
			
		}else{
			sumFactor = 0;
		}
		invoice.setSftr(sumFactor);
		Date dt = null;
			dt = (Date)invoice.getDateofinvoice();
		if(isNotEmpty(dt)) {
			int month = dt.getMonth();
			int year = dt.getYear()+1900;
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			
			invoice.setMthCd(""+month);
			invoice.setYrCd(""+yearCode);
			invoice.setQrtCd(""+quarter);
		}
		}
		Double totalTaxableAmt = invoice.getTotaltaxableamount();
		Double totalTax = invoice.getTotaltax();
		Double totalAmt = invoice.getTotalamount();
		Date dateOfInvoice = invoice.getDateofinvoice();
		String totalTaxableAmtStr = String.format(DOUBLE_FORMAT,totalTaxableAmt);
		String totalTaxStr = String.format(DOUBLE_FORMAT,totalTax);
		String totalAmtStr = String.format(DOUBLE_FORMAT,totalAmt);
		String dateOfInvoiceStr = dateFormatOnlyDate.format(dateOfInvoice);
		
		invoice.setTotaltaxableamount_str(totalTaxableAmtStr);
		invoice.setTotaltax_str(totalTaxStr);
		invoice.setTotalamount_str(totalAmtStr);
		invoice.setDateofinvoice_str(dateOfInvoiceStr);
		
		for (Item item : invoice.getItems()) {
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
			if(isNotEmpty(item.getStateCess())) {
				totalStateCessAmt += item.getStateCess();
			}
			if(isNotEmpty(item.getAssAmt())) {
				totalAssAmt += item.getAssAmt();
			}
			if(isNotEmpty(item.getCessNonAdvol())) {
				totalCessNonAdVal += item.getCessNonAdvol();
			}
			if(isNotEmpty(item.getCessamount())) {
				totalCessAmount+=item.getCessamount();
			}
			if(isNotEmpty(item.getDiscount())) {
				toralDiscAmount+=item.getDiscount();
			}
			if(isNotEmpty(item.getOthrCharges())) {
				totalOthrChrgAmount+=item.getOthrCharges();
			}
		}
		invoice.setTotalIgstAmount(totalIGST);
		invoice.setTotalCgstAmount(totalCGST);
		invoice.setTotalSgstAmount(totalSGST);
		invoice.setTotalExemptedAmount(totalExempted);
		invoice.setTotalCessAmount(totalCessAmount);
		if(isNotEmpty(totalAssAmt)) {
			invoice.setTotalAssAmount(totalAssAmt);
		}
		if(isNotEmpty(totalCessNonAdVal)) {
			invoice.setTotalCessNonAdVal(totalCessNonAdVal);
		}
		if(isNotEmpty(totalStateCessAmt)) {
			invoice.setTotalStateCessAmount(totalStateCessAmt);
		}
		if(isNotEmpty(toralDiscAmount)) {
			invoice.setTotalDiscountAmount(toralDiscAmount);
		}
		if(isNotEmpty(totalOthrChrgAmount)) {
			invoice.setTotalOthrChrgeAmount(totalOthrChrgAmount);
		}
			Client client = clientService.findById(invoice.getClientid());
			invoice = populateEinvoiceDetails(invoice, client);
			OtherConfigurations configdetails = otherConfigurationRepository.findByClientid(invoice.getClientid());
			boolean tcstds = false;
			if(isNotEmpty(configdetails)) {
				if(isNotEmpty(configdetails.isEnableTCS()) && configdetails.isEnableTCS()) {
					tcstds = true;
				}
			}
			
			/*Double taxableortotalamt = null;
			Double tcstdsamnt = null;
			if(tcstds) {
				if(isNotEmpty(invoice.getTotaltaxableamount())) {
					taxableortotalamt = invoice.getTotaltaxableamount();
				}
			}else {
				if(isNotEmpty(invoice.getTotalamount())) {
					taxableortotalamt = invoice.getTotalamount();
				}
			}
			
			if(isNotEmpty(taxableortotalamt) && isNotEmpty(invoice.getTcstdspercentage())) {
				tcstdsamnt = (taxableortotalamt) * invoice.getTcstdspercentage() / 100;
			}
			*/
			Double tcstdsamnt = 0d;
			if(isNotEmpty(invoice.getTcstdsAmount())) {
				tcstdsamnt = invoice.getTcstdsAmount();
			}
			
			if (returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister")) {
					if (isNotEmpty(tcstdsamnt) && isNotEmpty(invoice.getTotalamount()) && isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTcstdspercentage())) {
						invoice.setTcstdsAmount(tcstdsamnt);
						invoice.setNetAmount(invoice.getTotalamount() + tcstdsamnt);
					}
			}
		
		invoice.setTotalamount(totalAmt);
		invoice.setCsftr(1);
		return invoice;
	}
	
	public String getMonthName(int month) {
		String monthString;
		switch (month) {
		case 1:
			monthString = "JAN";
			break;
		case 2:
			monthString = "FEB";
			break;
		case 3:
			monthString = "MAR";
			break;
		case 4:
			monthString = "APR";
			break;
		case 5:
			monthString = "MAY";
			break;
		case 6:
			monthString = "JUN";
			break;
		case 7:
			monthString = "JUL";
			break;
		case 8:
			monthString = "AUG";
			break;
		case 9:
			monthString = "SEP";
			break;
		case 10:
			monthString = "OCT";
			break;
		case 11:
			monthString = "NOV";
			break;
		case 12:
			monthString = "DEC";
			break;
		default:
			monthString = "Invalid month";
			break;
		}
		return monthString;

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
	
	private List<GSTRItems> populateItemData(InvoiceParent invoice, boolean isIntraState, String returntype) {
		Double totalAmount = 0d;
		Double totalTax = 0d;
		Double totalTaxableAmt = 0d;
		Double totalITC = 0d;
		Double totalCurrencyAmount = 0d;
		String invType = invoice.getInvtype();
		List<GSTRItems> gstrItems = Lists.newArrayList();
		for (Item item : invoice.getItems()) {
			Double txvalue = 0d;
			if (invType.equals(MasterGSTConstants.ATPAID)) {
				if (isNotEmpty(item.getAdvadjustedAmount())) {
					txvalue = item.getAdvadjustedAmount();
				}
				GSTR1 inv = gstr1Repository.findByClientidAndInvtypeAndInvoiceno(invoice.getClientid(),
						MasterGSTConstants.ADVANCES, item.getAdvReceiptNo());
				Double advRemainingAmount = item.getAdvAdjustableAmount() - item.getAdvadjustedAmount();
				inv.setAdvRemainingAmount(advRemainingAmount);
				gstr1Repository.save((GSTR1) inv);
			} else {
				if (isNotEmpty(item.getTaxablevalue())) {
					txvalue = item.getTaxablevalue();
				}
			}
			if (isNotEmpty(txvalue)) {
				GSTRItems gstrItem = new GSTRItems();
				GSTRItemDetails gstrItemDetail = new GSTRItemDetails();
				GSTRITCDetails gstrITCDetail = new GSTRITCDetails();
				Map<String, String> hsnMap = configService.getHSNMap();
				Map<String, String> sacMap = configService.getSACMap();

				if (isNotEmpty(item.getHsn())) {
					String code = null;
					String description = null;
					if (item.getHsn().contains(" : ")) {
						String hsncode[] = item.getHsn().split(" : ");
						code = hsncode[0];
						description = hsncode[1];
					} else {
						code = item.getHsn();
					}

					if (hsnMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					} else if (hsnMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.GOODS);
					}
					if (isEmpty(description)) {
						for (String key : hsnMap.keySet()) {
							if (hsnMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.GOODS);
								break;
							}
						}
					}
					if (isEmpty(item.getUqc())) {
						item.setUqc("");
					}

					if (isEmpty(item.getRateperitem())) {
						item.setRateperitem(0d);
					}

					if (isEmpty(item.getDiscount())) {
						item.setDiscount(0d);
					}

					if (isEmpty(item.getQuantity())) {
						item.setQuantity(0d);
					}
					if (MasterGSTConstants.ADVANCES.equals(invoice.getInvtype())) {
						if (isNotEmpty(item.getTotal())) {
							item.setAdvreceived(item.getTotal());
						}
					}
					if (sacMap.containsKey(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					} else if (sacMap.containsValue(code)) {
						item.setCategory(MasterGSTConstants.SERVICES);
					}
					if (isEmpty(description)) {
						for (String key : sacMap.keySet()) {
							if (sacMap.get(key).endsWith(" : " + code)) {
								item.setCategory(MasterGSTConstants.SERVICES);
								break;
							}
						}
					}
				}
				if (isIntraState) {
					if (isNotEmpty(item.getCgstrate()) && item.getCgstrate() > 0 && isNotEmpty(item.getSgstrate()) && item.getSgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					} else if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					} else {
						gstrItemDetail.setRt(0d);
						if(isIntraState) {
							item.setCgstrate(0d);
							item.setSgstrate(0d);
						}else {
							item.setIgstrate(0d);
						}
					}

					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setCamt(item.getIgstamount() / 2);
							gstrItemDetail.setSamt(item.getIgstamount() / 2);
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setCamt(item.getCgstamount());
							gstrItemDetail.setSamt(item.getSgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() >= 0 && isNotEmpty(item.getSgstamount()) && item.getSgstamount() >= 0) {
								logger.debug(invoice.getInvtype());
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}else {
							if (isNotEmpty(item.getCgstamount())  && isNotEmpty(item.getSgstamount())) {
								logger.debug(invoice.getInvtype());
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							} else if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							}
						}
					}

					if (isNotEmpty(item.getCgstavltax())) {
						gstrITCDetail.setcTax(item.getCgstavltax());
					}
					if (isNotEmpty(item.getSgstavltax())) {
						gstrITCDetail.setsTax(item.getSgstavltax());
					}
				} else {
					if (isNotEmpty(item.getIgstrate()) && item.getIgstrate() > 0) {
						gstrItemDetail.setRt(item.getIgstrate());
					} else if (isNotEmpty(item.getCgstrate()) && isNotEmpty(item.getSgstrate())
							&& item.getCgstrate() > 0) {
						gstrItemDetail.setRt(item.getCgstrate() + item.getSgstrate());
					} else if (isEmpty(gstrItemDetail.getRt()) && isNotEmpty(item.getRate()) && item.getRate() > 0) {
						gstrItemDetail.setRt(item.getRate());
						if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
							item.setIgstrate(item.getRate());
						} else {
							item.setCgstrate(item.getRate() / 2);
							item.setSgstrate(item.getRate() / 2);
						}
					} else {
						gstrItemDetail.setRt(0d);
						if(isIntraState) {
							item.setCgstrate(0d);
							item.setSgstrate(0d);
						}else {
							item.setIgstrate(0d);
						}
					}
					if (isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())	&& !"R".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
						if ("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else if ("CWB".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							gstrItemDetail.setIamt(item.getIgstamount());
						} else {
							gstrItemDetail.setIamt(item.getIgstamount());
						}
					} else {
						if (!invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)) {
							if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() >= 0) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								if (invoice.getInvtype().equals(EXPORTS)) {
									gstrItemDetail.setIamt(0d);
								} else {
									gstrItemDetail.setCamt(item.getCgstamount());
									gstrItemDetail.setSamt(item.getSgstamount());
								}
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								gstrItemDetail.setIamt(item.getIgstamount());
							} else if (isNotEmpty(item.getCgstamount()) && isNotEmpty(item.getSgstamount())) {
								gstrItemDetail.setCamt(item.getCgstamount());
								gstrItemDetail.setSamt(item.getSgstamount());
								
							}
						}
					}
					if (isNotEmpty(item.getIgstavltax())) {
						gstrITCDetail.setiTax(item.getIgstavltax());
					}
				}
				if (isNotEmpty(item.getCessamount())) {
					gstrItemDetail.setCsamt(item.getCessamount());
				}
				if (isNotEmpty(item.getCessavltax())) {
					gstrITCDetail.setCsTax(item.getCessavltax());
				}
				if (isNotEmpty(item.getElg())) {
					if (item.getElg().startsWith("Input")) {
						item.setElg("ip");
					} else if (item.getElg().startsWith("Capital")) {
						item.setElg("cp");
					} else if (item.getElg().startsWith("In")) {
						item.setElg("is");
					} else if (item.getElg().startsWith("Not")) {
						item.setElg("no");
					}
					gstrITCDetail.setElg(item.getElg());
				}
				if (isNotEmpty(item.getTaxablevalue())) {
					gstrItemDetail.setTxval(item.getTaxablevalue());
				}
				if (isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(ATPAID) || invoice.getInvtype().equals(ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATA) || invoice.getInvtype().equals(MasterGSTConstants.TXPA))) {
						if (isNotEmpty(item.getTaxablevalue())) {
							gstrItemDetail.setAdvAmt(item.getTaxablevalue());
						}
					
				}
				gstrItem.setItem(gstrItemDetail);
				if (returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
					if (isNotEmpty(gstrITCDetail.getElg())) {
						gstrItem.setItc(gstrITCDetail);
					}
				}
				gstrItem.setNum(gstrItems.size() + 1);
				gstrItems.add(gstrItem);
				if (isNotEmpty(item.getCgstamount())) {
					totalTax += item.getCgstamount();
				}
				if (isNotEmpty(item.getIgstamount())) {
					totalTax += item.getIgstamount();
				}
				if (isNotEmpty(item.getSgstamount())) {
					totalTax += item.getSgstamount();
				}
				if (isNotEmpty(item.getCessamount())) {
					totalTax += item.getCessamount();
				}
				if (isNotEmpty(gstrITCDetail.getiTax())) {
					totalITC += gstrITCDetail.getiTax();
				}
				if (isNotEmpty(gstrITCDetail.getcTax())) {
					totalITC += gstrITCDetail.getcTax();
				}
				if (isNotEmpty(gstrITCDetail.getsTax())) {
					totalITC += gstrITCDetail.getsTax();
				}
				if (isNotEmpty(gstrITCDetail.getCsTax())) {
					totalITC += gstrITCDetail.getCsTax();
				}
				if (isNotEmpty(item.getTotal())) {
					totalAmount += item.getTotal();
				}
				if (isNotEmpty(item.getCurrencytotalAmount())) {
					totalCurrencyAmount += item.getCurrencytotalAmount();
				}
				if (isNotEmpty(item.getTaxablevalue())) {
					totalTaxableAmt += item.getTaxablevalue();
				}
				if (isNotEmpty(item.getAdditionalchargevalue())) {
					totalTaxableAmt += item.getAdditionalchargevalue();
				}
			}
		}

		if (isNotEmpty(invoice.getRoundOffAmount())) {
			invoice.setTotalamount(totalAmount + invoice.getRoundOffAmount());
		} else {
			invoice.setRoundOffAmount(0.0);
			invoice.setTotalamount(totalAmount);
		}
		if(isNotEmpty(totalCurrencyAmount)) {
			invoice.setTotalCurrencyAmount(totalCurrencyAmount);
		}
		
		invoice.setTotaltax(totalTax);
		invoice.setTotaltaxableamount(totalTaxableAmt);
		invoice.setTotalitc(totalITC);
		return gstrItems;
	}

	public Map<String, List<InvoiceParent>> getEinvExcelSheetMap() {
		Map<String, List<InvoiceParent>> sheetMap = Maps.newHashMap();
		List<InvoiceParent> creditList = Lists.newArrayList();
		List<InvoiceParent> exportList = Lists.newArrayList();
		List<InvoiceParent> b2bList = Lists.newArrayList();
		List<InvoiceParent> invoiceList = Lists.newArrayList();
		List<InvoiceParent> cdnurList = Lists.newArrayList();
		sheetMap.put("creditList", creditList);
		sheetMap.put("cdnurList", cdnurList);
		sheetMap.put("exportList", exportList);
		sheetMap.put("b2bList", b2bList);
		sheetMap.put("invoiceList", invoiceList);
		return sheetMap;
	}
	@Override
	public ImportResponse importEinvMapperSimplified(String id, String clientid, String templateName,
			MultipartFile file, String returntype, int month, int year, List<String> list, String fullname,
			TemplateMapper templateMapper, TemplateMapperDoc templateMapperDoc,OtherConfigurations otherconfig,String branch, String vertical,
			HttpServletRequest request) {
		final String method = " importMapperSimplified::";
		logger.debug(CLASSNAME + method + BEGIN);
		Client client = clientService.findById(clientid);
		String stateName = client.getStatename();
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		int skipRows = 1;
		if (isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();

		if (NullUtil.isNotEmpty(templateMapper.getMapperConfig())) {
			for (String key : templateMapper.getMapperConfig().keySet()) {
				if (NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String) templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			try {
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				String xmlPath1 = templateMapperDoc.getXmlDoc();
				InputStream in = IOUtils.toInputStream(xmlPath1, "UTF-8");
				XLSReader xlsReader = ReaderBuilder.buildFromXML(in);
				Map<String, List<InvoiceParent>> sheetMap = getEinvExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				if (status.isStatusOK()) {
					Map<String, String> statesMap = newimportService.getStateMap();
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
					String errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xls";
					if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
						workbookError = new XSSFWorkbook();
						errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xlsx";
					}
					File convFile = new File(errorfilename);
					convFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(convFile);
					for(int i=0;i<workbook.getNumberOfSheets(); i++){
						String sheetName = workbook.getSheetName(i);
						workbookError.createSheet(sheetName);
					}
					for (String key : beans.keySet()) {
						if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
							ImportSummary summary = new ImportSummary();
							String dis = "amount";
							if (key.equals("invoiceList")) {
								summary.setName(mapperConfig.get("Invoices"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Invoices")) {
									dis = templateMapper.getDiscountConfig().get("Invoices");
								}
							} else if (key.equals("creditList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Notes"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Notes")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Notes");
								}
							}else if (key.equals("cdnurList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
								}
							} else if (key.equals("exportList")) {
								summary.setName(mapperConfig.get("Exports"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Exports")) {
									dis = templateMapper.getDiscountConfig().get("Exports");
								}
							}else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
						    Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
							int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
							for(int i=0;i<skipRows;i++) {
								if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, i);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, i);
								}
							}
							
							long failed = 0;
							String strMonth = month < 10 ? "0" + month : month + "";
							String cFp = strMonth + year;
							long invsucess = 0;
							Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
							long totalinvs = 0;
							Map<String,InvoiceParent> tinvoicesmap = new HashMap<String, InvoiceParent>();
							List<InvoiceParent> invoices = beans.get(key);
							List<InvoiceParent> filteredList = Lists.newArrayList();
							if (isNotEmpty(invoices)) {
								int index = skipRows;
								if (skipRows == 1) {
									summary.setTotal(beans.get(key).size());
								} else {
									summary.setTotal(beans.get(key).size() - 1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								for (InvoiceParent invoice : invoices) {
									if(index != skipRows || skipRows == 1) {
										int vindex = index;
										if (skipRows == 1) {
											vindex = index;
										}else {
											vindex = index-1;
										}
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(isNotEmpty(tinvoicesmap)) {
											InvoiceParent exstngInv = tinvoicesmap.get(invnum);
											if(isEmpty(exstngInv)) {
												totalinvs++;
												tinvoicesmap.put(invnum, invoice);
											}
										}else {
											totalinvs++;
											tinvoicesmap.put(invnum, invoice);
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
											failed++;
											if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
												if(vindex != skipRows - 1) {
													Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
													gstincell.setCellValue("Invalid Invoice Number Format");
													gstincell.setCellStyle(style);
													errorIndxes.add(vindex);
												}
											}
										
									}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
										failed++;
										if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
											if(vindex != skipRows - 1) {
												Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
												gstincell.setCellValue("Invalid GSTIN Number");
												gstincell.setCellStyle(style);
												errorIndxes.add(vindex);
											}
										}
									} else {
											if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate()) || (key.equals("creditList")
													&& (isEmpty(((GSTR1) invoice).getCdnr())
															|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
															|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())
															|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())))
															|| (key.equals("cdnurList") && (isEmpty(((GSTR1) invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
																	|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())
																	|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))

															|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) && invoice.getItems().get(0).getRate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = importInvoiceService.einvErrorMsg(key,invoice,returntype,false,"custom");
														Cell advAdjustError = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														advAdjustError.setCellValue(errorVal);
														advAdjustError.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList")&& !key.equals("itrvslList")
													&& ((((returntype.equals(MasterGSTConstants.GSTR1)
															|| returntype.equals("SalesRegister"))
															&& isEmpty(invoice.getStatename()))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) 
															&& (isEmpty(invoice.getItems().get(0).getHsn())
															|| (isNotEmpty(invoice.getItems().get(0).getRate()) && invoice.getItems().get(0).getRate() < 0)))))
															&& key.equals("invoiceList")&& (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
												failed++;
												if (isNotEmpty(datatypeSheet)&& ((vindex) <= datatypeSheet.getLastRowNum())&& isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = "";
														if(isEmpty(invoice.getStatename())) {
															errorVal += "Please Enter Statename,";
														}
														if(isEmpty(invoice.getStrDate())) {
															errorVal += "Please Enter Invoice Date,";
														}
														if(isEmpty(invoice.getItems().get(0).getHsn())) {
															errorVal += "Please Enter HSN,";
														}
														if(isEmpty(invoice.getItems().get(0).getQuantity())) {
															errorVal += "Please Enter Quantity,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getRate())	&& invoice.getItems().get(0).getRate() < 0)) {
															errorVal += "Please Enter valid Rate";
														}
														Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errors.setCellValue(errorVal);
														errors.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else {
												invoice = newimportService.importEInvoicesSimplified(invoice, client,returntype,branch,vertical,month,year,patterns,statesMap);
												if(key.equals("invoiceList") || key.equals("exportList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																invoice.getItems().get(0).setQuantity(1d);
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		invoice.getItems().get(0).setQuantity(1d);
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}
														}
													}
												}
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												filteredList.add(invoice);
												String fp = cFp;
												if(isNotEmpty(invoice)) {
													if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
														Calendar cal = Calendar.getInstance();
														cal.setTime(invoice.getDateofinvoice());
														int iMonth = cal.get(Calendar.MONTH) + 1;
														int iYear = cal.get(Calendar.YEAR);
														fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
													}
													String mapkey = "";
													if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
														mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
													}
													mapkey = mapkey+key.trim();
													mapkey = mapkey+fp.trim();
													if(isNotEmpty(invoicesmap)) {
														InvoiceParent exstngInv = invoicesmap.get(mapkey);
														if(isEmpty(exstngInv)) {
															invsucess++;
															invoicesmap.put(mapkey, invoice);
														}
													}else {
														invsucess++;
														invoicesmap.put(mapkey, invoice);
													}
												}
											}
									}
								}
									index++;
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {	
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}

								int eC = skipRows;
								for (int i = skipRows; i <= datatypeSheet.getLastRowNum(); i++) {
									if (errorIndxes.contains(i)) {
										if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
											WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, eC++);
										}else {
											WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, eC++);
										}
									}
								}
								beans.put(key, filteredList);
							}
							summary.setFailed(failed);
							summary.setTotalinvs(totalinvs);
							summary.setInvsuccess(invsucess);
							summary.setInvfailed(totalinvs - invsucess);
							summaryList.add(summary);
						}
					}
					try {
						workbookError.write(fos);
						fos.flush();
						fos.close();
						// workbook.close();
					} catch (IOException e) {
						logger.debug(CLASSNAME + method + "XLS Write ERROR {}", e);
						convFile = null;
					}
					String strMonth = month < 10 ? "0" + month : month + "";
					String cFp = strMonth + year;
					Map<Integer, Integer> fpMap = Maps.newHashMap();
					for (String key : beans.keySet()) {
						logger.debug(CLASSNAME + method + "{} : {}", key, beans.get(key));
						String fp = cFp;
						if (beans.get(key) instanceof InvoiceParent) {
							List<InvoiceParent> beanList = Lists.newArrayList();
							InvoiceParent invoiceParent = (InvoiceParent) beans.get(key);
							beanList.add(invoiceParent);
							if (isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getDateofinvoice())) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(invoiceParent.getDateofinvoice());
								int iMonth = cal.get(Calendar.MONTH) + 1;
								int iYear = cal.get(Calendar.YEAR);
								fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
								fpMap.put(iMonth, iYear);
							}
							beans.put(key, beanList);
						}
						List<InvoiceParent> invoices = beans.get(key);
						if (isNotEmpty(invoices)) {
							for (InvoiceParent invoice : invoices) {
								if (isNotEmpty(invoice)){
									if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(invoice.getDateofinvoice());
										int iMonth = cal.get(Calendar.MONTH) + 1;
										int iYear = cal.get(Calendar.YEAR);
										fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
										fpMap.put(iMonth, iYear);
									}
									invoice.setFp(fp);
									invoice.setGstr1orEinvoice("Einvoice");
									if (isNotEmpty(invoice.getB2b())) {
										if(isNotEmpty(invoice.getB2b().get(0).getCtin())){
											invoice.getBuyerDtls().setGstin(invoice.getB2b().get(0).getCtin());
										}
										if(isNotEmpty(invoice.getBilledtoname())){
											invoice.getBuyerDtls().setLglNm(invoice.getBilledtoname());
										}
										if(key.equalsIgnoreCase("exportList") && isNotEmpty(invoice.getB2b())) {
											if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
												invoice.getBuyerDtls().setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
											}
										}
										if((key.equalsIgnoreCase("creditList") || key.equalsIgnoreCase("cdnurList")) && isNotEmpty(invoice.getB2b())) {
											if (key.equalsIgnoreCase("cdnurList") && isNotEmpty(invoice.getCdnur())
													&& isNotEmpty(invoice.getCdnur().get(0)) && isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
												String docType =  invoice.getCdnur().get(0).getNtty();
												if ("INVOICE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("R");
													invoice.setTyp("INV");
												} else if ("CREDIT NOTE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("C");
													invoice.setTyp("CRN");
												} else if ("DEBIT NOTE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("D");
													invoice.setTyp("DBN");
												}
											}
											if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
												invoice.getBuyerDtls().setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
											}
										}
									}
								}
							}
						}
					}
					if (isNotEmpty(fpMap.keySet())) {
						for (Integer m : fpMap.keySet()) {
							if (fpMap.keySet().size() == 1
									&& (!m.equals(new Integer(month)) || !fpMap.get(m).equals(new Integer(year)))) {
								month = m;
								year = fpMap.get(m);
							}
						}
					}
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getFailed() > 0) {
								failedCount += summary.getFailed();
							}
						}
						if (failedCount > 0 && isNotEmpty(convFile)) {
							HttpSession session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("error.xls", convFile);
								response.setShowLink(true);
							}
						} else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					String invReturnType = returntype;
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					response.setError(
							"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
				}
			} catch (Exception e) {
				response.setError(
						"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		return response;
	}
	@Override
	public ImportResponse importEinvMapperDetailed(String id, String clientid, String templateName, MultipartFile file,
			String returntype, int month, int year, List<String> list, String fullname, TemplateMapper templateMapper,
			TemplateMapperDoc templateMapperDoc, OtherConfigurations otherconfig,String branch, String vertical, HttpServletRequest request) {
		final String method = "importEinvMapperDetailed::";
		Client client = clientService.findById(clientid);
		BeanUtilsBean.getInstance().getConvertUtils().register(false, false, 0);
		ImportResponse response = new ImportResponse();
		List<ImportSummary> summaryList = Lists.newArrayList();
		int skipRows = 1;
		if (isNotEmpty(templateMapper.getSkipRows())) {
			skipRows = Integer.parseInt(templateMapper.getSkipRows());
		}
		Map<String, String> mapperConfig = Maps.newHashMap();
		if (NullUtil.isNotEmpty(templateMapper.getMapperConfig())) {
			for (String key : templateMapper.getMapperConfig().keySet()) {

				if (NullUtil.isNotEmpty(templateMapper.getMapperConfig().get(key))) {
					String page = (String) templateMapper.getMapperConfig().get(key).get("page");
					mapperConfig.put(key, page);
				}
			}
		}
		if (!file.isEmpty()) {
			try {
				DateConverter converter = new DateConverter();
				String[] patterns = { "dd-MM-yyyy", "dd/MM/yyyy", "dd-MMM-yyyy", "yyyy-MM-dd" };
				converter.setPatterns(patterns);
				ConvertUtils.register(converter, Date.class);
				ReaderConfig.getInstance().setSkipErrors(true);
				String xmlPath1 = templateMapperDoc.getXmlDoc();
				InputStream in = IOUtils.toInputStream(xmlPath1, "UTF-8");
				XLSReader xlsReader = ReaderBuilder.buildFromXML(in);
				Map<String, List<InvoiceParent>> sheetMap = getEinvExcelSheetMap();
				Map<String, List<InvoiceParent>> beans = Maps.newHashMap();
				beans.put("cdnurList", sheetMap.get("cdnurList"));
				beans.put("creditList", sheetMap.get("creditList"));
				beans.put("exportList", sheetMap.get("exportList"));
				beans.put("invoiceList", sheetMap.get("invoiceList"));
				XLSReadStatus status = xlsReader.read(file.getInputStream(), beans);
				if (status.isStatusOK()) {
					String filename = file.getOriginalFilename();
					Workbook workbook = WorkbookFactory.create(file.getInputStream());
					Workbook workbookError = new HSSFWorkbook();
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
					String errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xls";
					if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
						workbookError = new XSSFWorkbook();
						errorfilename = "MasterGST_Import_Errors_" + dateFormat.format(new Date()) + ".xlsx";
					}
					File convFile = new File(errorfilename);
					convFile.createNewFile();
					FileOutputStream fos = new FileOutputStream(convFile);
					for(int i=0;i<workbook.getNumberOfSheets(); i++){
						String sheetName = workbook.getSheetName(i);
						workbookError.createSheet(sheetName);
					}
					Map<String, String> statesMap = newimportService.getStateMap();
					for (String key : beans.keySet()) {
						if (isNotEmpty(sheetMap.get(key)) && sheetMap.get(key).size() > 0) {
							ImportSummary summary = new ImportSummary();
							String dis = "amount";
							if (key.equals("invoiceList")) {
								summary.setName(mapperConfig.get("Invoices"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Invoices")) {
									dis = templateMapper.getDiscountConfig().get("Invoices");
								}
							} else if (key.equals("creditList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Notes"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Notes")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Notes");
								}
							} else if (key.equals("exportList")) {
								summary.setName(mapperConfig.get("Exports"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Exports")) {
									dis = templateMapper.getDiscountConfig().get("Exports");
								}
							}else if (key.equals("cdnurList")) {
								summary.setName(mapperConfig.get("Credit_Debit_Note_Unregistered"));
								if(isNotEmpty(templateMapper.getDiscountConfig()) && templateMapper.getDiscountConfig().containsKey("Credit_Debit_Note_Unregistered")) {
									dis = templateMapper.getDiscountConfig().get("Credit_Debit_Note_Unregistered");
								}
							} else {
								summary.setName(key);
							}
							Sheet datatypeSheet = workbook.getSheet(summary.getName());
							Sheet datatypeSheetError = workbookError.getSheet(summary.getName());
							CellStyle style = workbook.createCellStyle();
						    Font font = workbook.createFont();
				            font.setColor(IndexedColors.RED.getIndex());
				            style.setFont(font);
						    Cell cell = datatypeSheet.getRow(skipRows-1).createCell(datatypeSheet.getRow(skipRows-1).getLastCellNum());
						    cell.setCellValue("Errors");
						    cell.setCellStyle(style);
						    int lastcolumn = datatypeSheet.getRow(skipRows-1).getLastCellNum()-1;
						    for(int i=0;i<skipRows;i++) {
						    	if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
									WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, i);
								}else {
									WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, i);
								}
						    }
							long failed = 0;
							
							String strMonth = month < 10 ? "0" + month : month + "";
							String cFp = strMonth + year;
							long invsucess = 0;
							Map<String,InvoiceParent> invoicesmap = new HashMap<String, InvoiceParent>();
							long totalinvs = 0;
							Map<String,InvoiceParent> tinvoicesmap = new HashMap<String, InvoiceParent>();
							
							
							List<InvoiceParent> invoices = beans.get(key);
							List<InvoiceParent> filteredList = Lists.newArrayList();
							if (isNotEmpty(invoices)) {
								int index = skipRows;
								if (skipRows == 1) {
									summary.setTotal(beans.get(key).size());
								} else {
									summary.setTotal(beans.get(key).size() - 1);
								}
								//List<Row> errorRows = Lists.newArrayList();
								List<Integer> errorIndxes = Lists.newArrayList();
								for (InvoiceParent invoice : invoices) {
									if(index != skipRows || skipRows == 1) {
										int vindex = index;
										if (skipRows == 1) {
											vindex = index;
										}else {
											vindex = index-1;
										}
										String invnum = "";
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
											if(isScientificNotation(invoice.getB2b().get(0).getInv().get(0).getInum())) {
												invnum = new BigDecimal(invoice.getB2b().get(0).getInv().get(0).getInum().trim()).toPlainString();
											}else {
												invnum = invoice.getB2b().get(0).getInv().get(0).getInum();
											}
										}
										if(isNotEmpty(tinvoicesmap)) {
											InvoiceParent exstngInv = tinvoicesmap.get(invnum);
											if(isEmpty(exstngInv)) {
												totalinvs++;
												tinvoicesmap.put(invnum, invoice);
											}
										}else {
											totalinvs++;
											tinvoicesmap.put(invnum, invoice);
										}
										if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()) && isNotEmpty(invnum.trim()) && !Pattern.matches(INVOICENO_REGEX, invnum.trim())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid Invoice Number Format");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
										}else if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())
											&& !Pattern.matches(gstnFormat, invoice.getB2b().get(0).getCtin().trim().toUpperCase())) {
												failed++;
												if (isNotEmpty(datatypeSheet) && ((vindex) <= datatypeSheet.getLastRowNum()) && isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														Cell gstincell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														gstincell.setCellValue("Invalid GSTIN Number");
														gstincell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else {
												if (returntype.equals(MasterGSTConstants.GSTR1)
													&& (key.equals("creditList") || key.equals("cdnurList") || key.equals("exportList"))
													&& (isEmpty(invoice.getStatename()) || isEmpty(invoice.getStrDate())
													|| (key.equals("creditList") && (isEmpty(((GSTR1) invoice).getCdnr())
																			|| isEmpty(((GSTR1) invoice).getB2b().get(0).getCtin())
																			|| isEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())
																			|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())))
													|| (key.equals("cdnurList")	&& (isEmpty(((GSTR1) invoice).getCdnur()) || "Credit/Debit Note Date*".equals(invoice.getStrDate())
															|| isEmpty(((GSTR1) invoice).getB2b().get(0).getInv().get(0).getInum())
															|| isEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())))
													|| (key.equals("exportList") && (isEmpty(invoice.getEinvCategory())))
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| isEmpty(invoice.getItems().get(0).getHsn())
															|| (isNotEmpty(invoice.getItems().get(0).getRateperitem()) && invoice.getItems().get(0).getRateperitem() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getIgstrate()) && invoice.getItems().get(0).getIgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCgstrate()) && invoice.getItems().get(0).getCgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getSgstrate()) && invoice.getItems().get(0).getSgstrate() < 0)
															|| (isNotEmpty(invoice.getItems().get(0).getCessrate()) && invoice.getItems().get(0).getCessrate() < 0))) {
												failed++;
												if (isNotEmpty(datatypeSheet)&& ((vindex) <= datatypeSheet.getLastRowNum())&& isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = newimportService.einvErrorMsg(key,invoice,returntype,false,"mgst");
														Cell errorcell = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errorcell.setCellValue(errorVal);
														errorcell.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else if (!key.equals("advReceiptList") && !key.equals("advAdjustedList") && !key.equals("itrvslList")
													&& ((isEmpty(invoice.getStatename())
															|| isEmpty(invoice.getStrDate())
															|| isEmpty(invoice.getItems())
															|| (isNotEmpty(invoice.getItems()) && (isEmpty(invoice.getItems().get(0).getHsn())
																	|| isEmpty(invoice.getItems().get(0).getQuantity())|| (invoice.getItems().get(0).getQuantity() <= 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getRateperitem())&& invoice.getItems().get(0).getRateperitem() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getIgstrate())&& invoice.getItems().get(0).getIgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCgstrate())&& invoice.getItems().get(0).getCgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getSgstrate())&& invoice.getItems().get(0).getSgstrate() < 0)
																	|| (isNotEmpty(invoice.getItems().get(0).getCessrate())&& invoice.getItems().get(0).getCessrate() < 0)))))
															&& key.equals("invoiceList")&& (isEmpty(invoice.getB2b().get(0).getCtin()) || isEmpty(invoice.getB2b().get(0).getInv().get(0).getInum()))) {
												failed++;
												if (isNotEmpty(datatypeSheet)&& ((vindex) < datatypeSheet.getLastRowNum())&& isNotEmpty(datatypeSheet.getRow(vindex))) {
													if(vindex != skipRows - 1) {
														String errorVal = "";
														if(isEmpty(invoice.getStatename())) {
															errorVal += "Please Enter Statename,";
														}
														if(isEmpty(invoice.getStrDate())) {
															errorVal += "Please Enter Invoice Date,";
														}
														if(isEmpty(invoice.getItems().get(0).getHsn())) {
															errorVal += "Please Enter HSN,";
														}
	
														if(isEmpty(invoice.getItems().get(0).getQuantity())) {
															errorVal += "Please Enter Quantity,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getIgstrate())	&& invoice.getItems().get(0).getIgstrate() < 0)) {
															errorVal += "Please Enter valid Igst Rate,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getCgstrate())	&& invoice.getItems().get(0).getCgstrate() < 0)) {
															errorVal += "Please Enter valid Cgst Rate,";
														}
														if((isNotEmpty(invoice.getItems().get(0).getSgstrate())	&& invoice.getItems().get(0).getSgstrate() < 0)) {
															errorVal += "Please Enter valid Sgst Rate,";
																}
														if((isNotEmpty(invoice.getItems().get(0).getCessrate())	&& invoice.getItems().get(0).getCessrate() < 0)) {
															errorVal += "Please Enter valid Cess Rate";
														}
														Cell errors = datatypeSheet.getRow(vindex).createCell(lastcolumn);
														errors.setCellValue(errorVal);
														errors.setCellStyle(style);
														errorIndxes.add(vindex);
													}
												}
											} else {
												invoice = newimportService.importEInvoicesHsnQtyRateMandatory(invoice,returntype,branch,vertical,month,year,patterns, statesMap);
												if(key.equals("invoiceList") || key.equals("exportList")) {
													if(isNotEmpty(invoice.getItems().get(0).getDiscount()) && invoice.getItems().get(0).getDiscount() > 0) {
														double discount = invoice.getItems().get(0).getDiscount();
														if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
															if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																if("percentage".equalsIgnoreCase(dis)) {
																	invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																	invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																}else {
																	invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																}
															}
														}else {
															if(isNotEmpty(invoice.getItems().get(0).getQuantity()) && invoice.getItems().get(0).getQuantity() > 0) {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}else {
																if(invoice.getItems().get(0).getRateperitem() > 0) {
																	invoice.getItems().get(0).setQuantity(1d);
																	if("percentage".equalsIgnoreCase(dis)) {
																		invoice.getItems().get(0).setDiscount((invoice.getItems().get(0).getRateperitem()*invoice.getItems().get(0).getQuantity())*(discount/100));
																	}
																}else {
																	if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue()) && invoice.getItems().get(0).getTaxablevalue() > 0) {
																		double taxablevalue = invoice.getItems().get(0).getTaxablevalue(); 
																		if("percentage".equalsIgnoreCase(dis)) {
																			invoice.getItems().get(0).setRateperitem((taxablevalue*100)/(100-discount));
																			invoice.getItems().get(0).setDiscount(invoice.getItems().get(0).getRateperitem() - taxablevalue);
																		}else {
																			invoice.getItems().get(0).setRateperitem(taxablevalue + discount);
																		}
																	}

																}
															}
														}
													}
												}
												if(isNotEmpty(invoice.getItems().get(0))) {
													Item item = invoice.getItems().get(0); 
													item = bulkImportTaskService.changeInvoiceAmounts(item);
													List<Item> itms = Lists.newArrayList();
													itms.add(item);
													invoice.setItems(itms);
												}
												filteredList.add(invoice);
												String fp = cFp;
												if(isNotEmpty(invoice)) {
													if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
														Calendar cal = Calendar.getInstance();
														cal.setTime(invoice.getDateofinvoice());
														int iMonth = cal.get(Calendar.MONTH) + 1;
														int iYear = cal.get(Calendar.YEAR);
														fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
													}
													String mapkey = "";
													if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
														mapkey = mapkey+invoice.getB2b().get(0).getInv().get(0).getInum().trim();
													}
													mapkey = mapkey+key.trim();
													mapkey = mapkey+fp.trim();
													if(isNotEmpty(invoicesmap)) {
														InvoiceParent exstngInv = invoicesmap.get(mapkey);
														if(isEmpty(exstngInv)) {
															invsucess++;
															invoicesmap.put(mapkey, invoice);
														}
													}else {
														invsucess++;
														invoicesmap.put(mapkey, invoice);
													}
												}
											}

										}
									}
									index++;
								}
								if (skipRows == 1) {
									summary.setSuccess((beans.get(key).size()) - failed);
								} else {
									summary.setSuccess((beans.get(key).size() - 1) - failed);
								}
								int eC = skipRows;
								for (int i = skipRows; i <= datatypeSheet.getLastRowNum(); i++) {
									if (errorIndxes.contains(i)) {
										if(filename.endsWith(".xlsx") || filename.endsWith(".XLSX")) {
											WorkbookUtility.copyXssFRow(datatypeSheet, datatypeSheetError, i, eC++);
										}else {
											WorkbookUtility.copyRow(datatypeSheet, datatypeSheetError, i, eC++);
										}
									}
								}
								beans.put(key, filteredList);
							}
							summary.setFailed(failed);
							summary.setTotalinvs(totalinvs);
							summary.setInvsuccess(invsucess);
							summary.setInvfailed(totalinvs - invsucess);
							summaryList.add(summary);
						}
					}
					try {
						workbookError.write(fos);
						fos.flush();
						fos.close();
						// workbook.close();
					} catch (IOException e) {
						logger.debug(CLASSNAME + method + "XLS Write ERROR {}", e);
						convFile = null;
					}
					String strMonth = month < 10 ? "0" + month : month + "";
					String cFp = strMonth + year;
					Map<Integer, Integer> fpMap = Maps.newHashMap();
					for (String key : beans.keySet()) {
						logger.debug(CLASSNAME + method + "{} : {}", key, beans.get(key));
						String fp = cFp;
						if (beans.get(key) instanceof InvoiceParent) {
							List<InvoiceParent> beanList = Lists.newArrayList();
							InvoiceParent invoiceParent = (InvoiceParent) beans.get(key);
							beanList.add(invoiceParent);
							if (isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getDateofinvoice())) {
								Calendar cal = Calendar.getInstance();
								cal.setTime(invoiceParent.getDateofinvoice());
								int iMonth = cal.get(Calendar.MONTH) + 1;
								int iYear = cal.get(Calendar.YEAR);
								fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
								fpMap.put(iMonth, iYear);
							}
							beans.put(key, beanList);
						}
						List<InvoiceParent> invoices = beans.get(key);
						if (isNotEmpty(invoices)) {
							for (InvoiceParent invoice : invoices) {
								if(isNotEmpty(invoice)) {
									if (isNotEmpty(invoice) && isNotEmpty(invoice.getDateofinvoice())) {
										Calendar cal = Calendar.getInstance();
										cal.setTime(invoice.getDateofinvoice());
										int iMonth = cal.get(Calendar.MONTH) + 1;
										int iYear = cal.get(Calendar.YEAR);
										fp = (iMonth < 10 ? "0" + iMonth : iMonth + "") + iYear;
										fpMap.put(iMonth, iYear);
									}
									invoice.setFp(fp);
									invoice.setGstr1orEinvoice("Einvoice");
									if (isNotEmpty(invoice.getB2b())) {
										if(isNotEmpty(invoice.getB2b().get(0).getCtin())){
											invoice.getBuyerDtls().setGstin(invoice.getB2b().get(0).getCtin());
										}
										if(isNotEmpty(invoice.getBilledtoname())){
											invoice.getBuyerDtls().setLglNm(invoice.getBilledtoname());
										}
										if(key.equalsIgnoreCase("exportList") && isNotEmpty(invoice.getB2b())) {
											if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
												invoice.getBuyerDtls().setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
											}
										}
										if((key.equalsIgnoreCase("creditList") || key.equalsIgnoreCase("cdnurList")) && isNotEmpty(invoice.getB2b())) {
											if (key.equalsIgnoreCase("cdnurList") && isNotEmpty(invoice.getCdnur())
													&& isNotEmpty(invoice.getCdnur().get(0)) && isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
												String docType =  invoice.getCdnur().get(0).getNtty();
												if ("INVOICE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("R");
													invoice.setTyp("INV");
												} else if ("CREDIT NOTE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("C");
													invoice.setTyp("CRN");
												} else if ("DEBIT NOTE".equalsIgnoreCase(docType)) {
													((GSTR1) invoice).getCdnur().get(0).setNtty("D");
													invoice.setTyp("DBN");
												}
											}
											if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
												invoice.getBuyerDtls().setAddr1(invoice.getB2b().get(0).getInv().get(0).getAddress());
											}
										}
									}
								}
							}
						}
					}
					if (isNotEmpty(fpMap.keySet())) {
						for (Integer m : fpMap.keySet()) {
							if (fpMap.keySet().size() == 1
									&& (!m.equals(new Integer(month)) || !fpMap.get(m).equals(new Integer(year)))) {
								month = m;
								year = fpMap.get(m);
							}
						}
					}
					response.setMonth(month);
					response.setYear(year);
					response.setSummaryList(summaryList);
					if (isNotEmpty(summaryList)) {
						int failedCount = 0;
						for (ImportSummary summary : summaryList) {
							if (summary.getFailed() > 0) {
								failedCount += summary.getFailed();
							}
						}
						if (failedCount > 0 && isNotEmpty(convFile)) {
							HttpSession session = request.getSession(false);
							if (isNotEmpty(session)) {
								session.setAttribute("error.xls", convFile);
								response.setShowLink(true);
							}
						} else {
							try {
								convFile.delete();
							} catch (Exception e) {
							}
						}
					}
					String invReturnType = returntype;
					if (returntype.equals(MasterGSTConstants.GSTR2)) {
						invReturnType = MasterGSTConstants.PURCHASE_REGISTER;
					}
					newimportService.updateExcelData(beans, list, invReturnType, id, fullname, clientid, EXCEL_TEMPLATE,otherconfig);
				} else {
					response.setError(
							"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
				}
			} catch (Exception e) {
				response.setError(
						"Sorry..!!! We have noticed changes in the Template Parameters, please download latest template and re-import invoices.");
				logger.error(CLASSNAME + method + " ERROR", e);
			}
		}
		return response;
	}

	@Override
	public void updateAlreadyGeneratedIRNByOtherSystem(Client client, String returnType, String userid, String usertype, int month, int year) throws Exception {
		EinvoiceConfigurations configDetails = einvoiceConfigurationRepository.findByClientid(client.getId().toString());
		String uname="";
		if(isNotEmpty(configDetails)) {
			uname=configDetails.getUserName();
		}
		String yearCode = Utility.getYearCode(month, year);
		HeaderKeys headerKeys = headerKeysRepository.findByGstusernameAndEmail(uname,iHubEmail);
		String strMonth = month < 10 ? "0" + month : month + "";
		String returnPeriod = strMonth + year;
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		//List<GSTR1> gstr1invoices = gstr1Repository.findByClientidAndInvtypeInAndFpAndIrnNoIsNull(client.getId().toString(),invTypes,returnPeriod);
		Page<? extends InvoiceParent> invoices = gstr1Dao.findByClientidAndInvtypeInAndMonthAndYearEINV(client.getId().toString(),invTypes, month, yearCode);
		if(isNotEmpty(invoices)) {
			for(InvoiceParent gstr1 : invoices) {
				if(isNotEmpty(gstr1.getInvoiceno()) && isNotEmpty(gstr1.getDateofinvoice_str())) {
					String doctype = "";
					gstr1.setGovtInvoiceStatus(MasterGSTConstants.SUCCESS);
					if(isNotEmpty(gstr1.getInvtype())) {
						String invtype = gstr1.getInvtype();
						if(invtype.equalsIgnoreCase(MasterGSTConstants.B2B) || invtype.equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
							doctype = "INV";
						}else if(invtype.equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							if(isNotEmpty(((GSTR1)gstr1).getCdnr()) && isNotEmpty(((GSTR1)gstr1).getCdnr().get(0)) && isNotEmpty(((GSTR1)gstr1).getCdnr().get(0).getNt()) && isNotEmpty(((GSTR1)gstr1).getCdnr().get(0).getNt().get(0)) && isNotEmpty(((GSTR1)gstr1).getCdnr().get(0).getNt().get(0).getNtty())) {
								String crdrtype = ((GSTR1)gstr1).getCdnr().get(0).getNt().get(0).getNtty();
								if(isNotEmpty(crdrtype)) {
									if(crdrtype.equalsIgnoreCase("C")) {
										doctype = "CRN";
									}else if(crdrtype.equalsIgnoreCase("D")) {
										doctype = "DBN";
									}
								}
							}
						}else if(invtype.equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
							if(isNotEmpty(gstr1.getCdnur()) && isNotEmpty(gstr1.getCdnur().get(0)) && isNotEmpty(gstr1.getCdnur().get(0).getNtty())) {
								String crdrtype = gstr1.getCdnur().get(0).getNtty();
								if(isNotEmpty(crdrtype)) {
									if(crdrtype.equalsIgnoreCase("C")) {
										doctype = "CRN";
									}else if(crdrtype.equalsIgnoreCase("D")) {
										doctype = "DBN";
									}
								}
							}
						}
					}
					
					if(isNotEmpty(doctype)) {
						GenerateIRNResponse eresponse = null;
						eresponse = iHubConsumerService.getIRNByDocDetails(client.getGstnnumber(), InetAddress.getLocalHost().getHostAddress(),configDetails,headerKeys,gstr1.getInvoiceno().trim(),gstr1.getDateofinvoice_str(),doctype);
						if(isNotEmpty(eresponse) && isNotEmpty(eresponse.getData()) && isNotEmpty(eresponse.getData().getIrn()) && isNotEmpty(eresponse.getData().getSignedQRCode()) && isNotEmpty(eresponse.getData().getSignedInvoice()) ) {
							gstr1.setIrnNo(eresponse.getData().getIrn());
							
							gstr1.setGstr1orEinvoice("Einvoice");
							gstr1.setSignedQrCode(eresponse.getData().getSignedQRCode());
							gstr1.setSignedInvoice(eresponse.getData().getSignedInvoice());
							if(isNotEmpty(eresponse.getData().getAckNo())) {
								gstr1.setAckNo(eresponse.getData().getAckNo().toString());
							}
							if(isNotEmpty(eresponse.getData().getAckDt())) {
								gstr1.setAckDt(eresponse.getData().getAckDt());
							}
							if(isNotEmpty(eresponse.getData().getStatus())) {
								gstr1.setEinvStatus(eresponse.getData().getStatus());
								if(eresponse.getData().getStatus().equalsIgnoreCase("ACT")) {
									gstr1.setIrnStatus("Generated");
								}else if(eresponse.getData().getStatus().equalsIgnoreCase("CNL")){
									gstr1.setIrnStatus("Cancelled");
								}
							}
							gstr1.setSrctype("GSTAPI");
							gstr1Repository.save((GSTR1)gstr1);
						}
					}
					
				}
			}
		}
	}
	
	public Map getEInvoicesForDelete(Pageable pageable, final Client client, final String userid, final String returnType,final String reports, int month,
			int year, int start, int length, String searchVal,String fieldName, String order, InvoiceFilter filter, boolean isTotalRequired){
		logger.debug(CLASSNAME + "getInvoices : Begin");
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		if(pageable == null) {
			pageable = new PageRequest(0, Integer.MAX_VALUE);
		}
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.CDNUR);
		invTypes.add(MasterGSTConstants.EXPORTS);
		invTypes.add(MasterGSTConstants.B2C);
		Page<? extends InvoiceParent> invoices = null;
		if (isNotEmpty(returnType)) {
			String yearCode = Utility.getYearCode(month, year);
			invoices = gstr1Dao.findByClientidAndInvtypeInAndMonthAndYearDeleteEinv(client.getId().toString(),invTypes, month, yearCode, start, length, searchVal,fieldName,order,filter);
			invoicesMap.put("invoices",invoices);
		}
		return invoicesMap;
	}
	
}
