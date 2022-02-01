package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation.ExpressionProjectionOperationBuilder;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.SubscriptionPlan;
import com.mastergst.configuration.service.SubscriptionPlanRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.ApiExceedsUsers;
import com.mastergst.login.runtime.domain.SubscriptionUsers;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.repository.UserRepository;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;
import com.mastergst.usermanagement.runtime.domain.CompanyItems;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PartnerClient;
import com.mastergst.usermanagement.runtime.domain.PartnerPayments;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PaymentLink;
import com.mastergst.usermanagement.runtime.domain.PaymentRequestDetails;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.CompanyBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyItemsRepository;
import com.mastergst.usermanagement.runtime.repository.PartnerClientRepository;
import com.mastergst.usermanagement.runtime.repository.PartnerPaymentsRepository;
import com.mastergst.usermanagement.runtime.repository.PaymentDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.PaymentLinkRepository;
import com.mastergst.usermanagement.runtime.repository.SubscriptionDetailsRepository;
import com.mastergst.usermanagement.runtime.support.AdminUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * Service Impl class for Subscription services to perform CRUD operation.
 * 
 * @author BVM Consultancy Services(www.bvmcs.com)
 * @version 1.0
 */
@Service
@Transactional(readOnly = true)
public class SubscriptionServiceImpl implements SubscriptionService {
	
	private static final Logger logger = LogManager.getLogger(SubscriptionServiceImpl.class.getName());
	
	private static final String CLASSNAME = "SubscriptionServiceImpl::";
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	private static final String[] FIELDS_ARRAY = {"userid", "paidAmount", "allowedInvoices", "apiType",  "registeredDate", "expiryDate","processedInvoices", "mobilenumber", "address", "type", "userId","user", "subscriptionType", "totalInvoices"};

	private static final String[] USER_FIELDS_ARRAY = {"user", "subscription", "userid", "paidAmount", "allowedInvoices","processedInvoices", "mobilenumber", "email", "fullname", "address", "type", "userId", "subscriptionType", "totalInvoices"};

	
	@Autowired
	private ConfigService configService;
	@Autowired
	private SubscriptionPlanRepository subscriptionPlanRepository;
	@Autowired
	private SubscriptionDetailsRepository subscriptionDetailsRepository;
	@Autowired
	private PaymentDetailsRepository paymentDetailsRepository;
	@Autowired
	private CompanyItemsRepository companyItemsRepository;
	@Autowired
	private CompanyBankDetailsRepository companyBankDetailsRepository;
	
	@Autowired
	private UserService userService;
	@Autowired
	private ClientService clientService;
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PartnerClientRepository partnerClientRepository;
	
	@Autowired
	private AdminUtils adminUtils;
	
	@Autowired
	private PartnerPaymentsRepository partnerPaymentsRepository;
	@Autowired
	private	PaymentLinkRepository paymentLinkRepository;
	
	
	@Value("${mastergst.clientid}")
	private String mastergstClientid;
	@Value("${mastergst.userid}")
	private String mastergstuserid;
	@Value("${bank.accountnumber}")
	private String bankAccountNumber;
	
	

	@Override
	public List<SubscriptionPlan> getSubscriptions() {
		logger.debug(CLASSNAME + "getSubscriptions : Begin");
		return configService.getPlans();
	}
	
	@Override
	public List<SubscriptionPlan> getSubscriptions(final String category) {
		logger.debug(CLASSNAME + "getSubscriptions : Begin");
		List<SubscriptionPlan> plans = subscriptionPlanRepository.findByCategory(category);
		plans.sort(Comparator.comparing(SubscriptionPlan::getPrice));
		return plans;
	}

	@Override
	public SubscriptionPlan getPlan(String id) {
		logger.debug(CLASSNAME + "getPlan : Begin");
		return subscriptionPlanRepository.findOne(id);
	}
	
	@Override
	public boolean allowAddClient(final String userId, final Integer addedClients) {
		logger.debug(CLASSNAME + "allowAddClient : Begin");
		SubscriptionDetails subscriptionDetails = getSubscriptionData(userId);
		if(NullUtil.isEmpty(subscriptionDetails)) {
			subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setUserid(userId);
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			subscriptionDetails.setPaidAmount(0d);
			subscriptionDetails.setRegisteredDate(today);
			subscriptionDetails.setAllowedClients(25);
			subscriptionDetails.setAllowedInvoices(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        try {
				Date expiryDate = sdf.parse("2018-01-07");
				//subscriptionDetails.setExpiryDate(expiryDate);
				subscriptionDetails.setUserDocid(new ObjectId(userId));
				subscriptionDetailsRepository.save(subscriptionDetails);
				/*if(today.compareTo(expiryDate) <= 0) { // Allow Add client without depedency on subscription expiry
					return true;
				} else {
					return false;
				}*/
			} catch (ParseException e) {
				logger.error(CLASSNAME + "allowAddClient : ERROR {}", e);
			}
			//return false;
	        return true;
		} else {
			//Date currentDate = new Date();
			//currentDate.compareTo(subscriptionDetails.getExpiryDate()) <= 0
			if((NullUtil.isEmpty(subscriptionDetails.getAllowedClients()))
							|| (subscriptionDetails.getAllowedClients() == 0)
							|| (subscriptionDetails.getAllowedClients() > addedClients)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean allowAddCenters(final String userId, final Integer addedCenters) {
		logger.debug(CLASSNAME + "allowAddCenters : Begin");
		SubscriptionDetails subscriptionDetails = getSubscriptionData(userId);
		if(NullUtil.isEmpty(subscriptionDetails)) {
			subscriptionDetails = new SubscriptionDetails();
			subscriptionDetails.setUserid(userId);
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			subscriptionDetails.setPaidAmount(0d);
			subscriptionDetails.setRegisteredDate(today);
			subscriptionDetails.setAllowedClients(25);
			subscriptionDetails.setAllowedCenters(25);
			subscriptionDetails.setAllowedInvoices(0);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	        try {
				Date expiryDate = sdf.parse("2018-01-07");
				//subscriptionDetails.setExpiryDate(expiryDate);
				subscriptionDetails.setUserDocid(new ObjectId(userId));
				subscriptionDetailsRepository.save(subscriptionDetails);
				/*if(today.compareTo(expiryDate) <= 0) { // Allow Add centers without dependency on subscription expiry
					return true;
				} else {
					return false;
				}*/
			} catch (ParseException e) {
				logger.error(CLASSNAME + "allowAddCenters : ERROR {}", e);
			}
			//return false;
	        return true;
		} else {
			if((NullUtil.isEmpty(subscriptionDetails.getAllowedCenters())
							|| subscriptionDetails.getAllowedCenters() == 0
							|| subscriptionDetails.getAllowedCenters() > addedCenters)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean allowUploadInvoices(final String userId, final Long invoices) {
		logger.debug(CLASSNAME + "allowUploadInvoices : Begin");
		SubscriptionDetails subscriptionDetails = getSubscriptionData(userId);
		if(NullUtil.isEmpty(subscriptionDetails) || NullUtil.isEmpty(subscriptionDetails.getAllowedInvoices())) {
			return false;
		} else {
			Date currentDate = new Date();
			if((NullUtil.isEmpty(subscriptionDetails.getExpiryDate()) 
					|| currentDate.compareTo(subscriptionDetails.getExpiryDate()) <= 0)
					&& (subscriptionDetails.getAllowedInvoices() >= invoices)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public boolean allowUploadInvoices(final String userId, final String clientid, final Long invoices) {
		logger.debug(CLASSNAME + "allowUploadInvoices : Begin");
		SubscriptionDetails subscriptionDetails = getSubscriptionData(userId);
		if(NullUtil.isEmpty(subscriptionDetails) || NullUtil.isEmpty(subscriptionDetails.getAllowedInvoices())) {
			return false;
		} else {
			Date currentDate = new Date();
			if((NullUtil.isEmpty(subscriptionDetails.getExpiryDate()) 
					|| currentDate.compareTo(subscriptionDetails.getExpiryDate()) <= 0)
					&& (subscriptionDetails.getAllowedInvoices() >= invoices)) {
				return true;
			} else {
				return false;
			}
		}
	}
	
	public SubscriptionDetails getSubscriptionData(final String userId) {
		logger.debug(CLASSNAME + "getSubscriptionData : Begin");
		SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUserid(userId);
		if(NullUtil.isNotEmpty(subscriptionDetails)) {
			return subscriptionDetails;
		} else {
			User user = userService.getUser(userId);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())) {
				return getSubscriptionData(user.getParentid());
			}
		}
		return null;
	}
	
	public SubscriptionDetails getSubscriptionData(final String userId, final String apiType) {
		logger.debug(CLASSNAME + "getSubscriptionData : Begin");
		SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUseridAndApiType(userId, apiType);
		if(NullUtil.isNotEmpty(subscriptionDetails)) {
			return subscriptionDetails;
		} else {
			User user = userService.getUser(userId);
			if(NullUtil.isNotEmpty(user) && NullUtil.isNotEmpty(user.getParentid())) {
				return getSubscriptionData(user.getParentid(), apiType);
			}
		}
		return null;
	}
	
	public SubscriptionDetails getUserSubscriptionDetails(final String userId) {
		logger.debug(CLASSNAME + "getUserSubscriptionDetails : Begin");
		return subscriptionDetailsRepository.findByUserid(userId);
	}
	
	public SubscriptionDetails updateSubscriptionData(final SubscriptionDetails subscriptionDetails) {
		logger.debug(CLASSNAME + "updateSubscriptionData : Begin");
		subscriptionDetails.setUserDocid(new ObjectId(subscriptionDetails.getUserid()));
		return subscriptionDetailsRepository.save(subscriptionDetails);
	}
	
	public void updateOfflinePaymentDetails(final String id, final String usertype, final Double amount, 
			final Integer invoices, final Integer clients, final Integer centers, final Double rate, 
			final String paymentMode, final String referenceNo, final String apiType, final String statename, final String planid,final String subscriptionType,final String subscriptionStartDate,final String subscriptionExpiryDate) {
		logger.debug(CLASSNAME + "updateOfflinePaymentDetails : Begin");
		User dbUser = userService.findById(id);
		
		if(NullUtil.isNotEmpty(dbUser)) {
			dbUser.setDisable("false");
			userService.updateUser(dbUser);
		}
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		int month=-1,year=-1;
		
		month = today.getMonth();
		year = today.getYear()+1900;
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		SubscriptionDetails findSubscriptionApiType=subscriptionDetailsRepository.findByUseridAndApiType(id, apiType);
		if(NullUtil.isNotEmpty(findSubscriptionApiType)) {
			if(NullUtil.isNotEmpty(id) && findSubscriptionApiType.getApiType().equalsIgnoreCase(apiType)) {
				try {
					updateOfflinePaymentDetailsTOPUPORRENWEAL(id, usertype, amount, invoices, clients,  centers,  rate, 
							paymentMode, referenceNo, apiType, statename, planid, subscriptionType, subscriptionStartDate, subscriptionExpiryDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}else {
			PaymentDetails paymentDetails=new PaymentDetails();
			paymentDetails.setUserid(id);
			paymentDetails.setPlanid(planid);
			paymentDetails.setAmount(amount);
			paymentDetails.setResponse(paymentMode+" - "+referenceNo);
			paymentDetails.setPaymentDate(today);
			paymentDetails.setStatus("Success");
			paymentDetails.setStatename(statename);
			paymentDetails.setSubscriptionType(subscriptionType);
			paymentDetails.setSubscriptionStartDate(subscriptionStartDate);
			paymentDetails.setSubscriptionExpiryDate(subscriptionExpiryDate);
			month++;
			paymentDetails.setMthCd(""+month);
			paymentDetails.setYrCd(""+yearCode);
			paymentDetails.setPartnerPayment(MasterGSTConstants.PENDING);
			
			if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				paymentDetails.setApiType(apiType);
			}
			SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUserid(id);
			if(!usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				if(NullUtil.isEmpty(subscriptionDetails)) {
					subscriptionDetails = new SubscriptionDetails();
					subscriptionDetails.setUserid(id);
				}
			}else{
				subscriptionDetails = new SubscriptionDetails();
				subscriptionDetails.setUserid(id);
				subscriptionDetails.setProcessedInvoices(0);
			}
			if(NullUtil.isNotEmpty(subscriptionDetails)) {
				if(NullUtil.isEmpty(subscriptionDetails.getAllowedInvoices())) {
					subscriptionDetails.setAllowedInvoices(invoices);
				}else {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+invoices);
				}
				if(NullUtil.isEmpty(subscriptionDetails.getAllowedCenters())) {
					subscriptionDetails.setAllowedCenters(centers != -1 ? centers : 0);
				}else {
					subscriptionDetails.setAllowedCenters(centers != -1 ? subscriptionDetails.getAllowedCenters()+centers : 0);
				}
			}else {
				subscriptionDetails.setAllowedInvoices(invoices);
			}
			subscriptionDetails.setPlanid(planid);
			subscriptionDetails.setAllowedClients(clients != -1 ? clients : 0);
			subscriptionDetails.setPaidAmount(amount);
			subscriptionDetails.setInvoiceCost(rate);
			if(isNotEmpty(subscriptionStartDate)) {
				DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date startDate;
				try {
					startDate = sdf.parse(subscriptionStartDate);
					subscriptionDetails.setRegisteredDate(startDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}else {
				subscriptionDetails.setRegisteredDate(today);
			}
			subscriptionDetails.setSubscriptionType(subscriptionType);
			if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				subscriptionDetails.setApiType(apiType);
			}
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DATE, -1);
			Date nextYear = cal.getTime();
			if("UNLIMITED".equalsIgnoreCase(subscriptionType)) {
			} else if("DEMO".equalsIgnoreCase(subscriptionType)) {
				DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date expryDate;
				try {
					expryDate = sdf.parse(subscriptionExpiryDate);
					subscriptionDetails.setExpiryDate(expryDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}				
			} else {
				if(isNotEmpty(subscriptionExpiryDate)) {
					DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date expryDate;
					try {
						expryDate = sdf.parse(subscriptionExpiryDate);
						subscriptionDetails.setExpiryDate(expryDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}else {
					subscriptionDetails.setExpiryDate(nextYear);
				}
			}
			SubscriptionDetails subscriptionDetails2=subscriptionDetailsRepository.findOne(id);
			if(subscriptionDetails2!=null) {
				subscriptionDetails2.getApiType();
			}
			Date dt = new Date();
			month=-1;
			year=-1;
			month = dt.getMonth();
			year = dt.getYear()+1900;
			quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			subscriptionDetails.setMthCd(month+"");
			subscriptionDetails.setYrCd(yearCode);
			subscriptionDetails.setQrtCd(quarter+"");
			subscriptionDetails.setUserDocid(new ObjectId(subscriptionDetails.getUserid()));
			subscriptionDetailsRepository.save(subscriptionDetails);
			paymentDetailsRepository.save(paymentDetails);
		}
		PaymentLink paymentLink = paymentLinkRepository.findByUserid(id);
		if(isNotEmpty(paymentLink)) {
			paymentLinkRepository.delete(paymentLink);
		}
		if(isNotEmpty(dbUser) && isNotEmpty(dbUser.getPartnerEmail())){
			updatePartnerClient(dbUser.getId().toString(), dbUser.getEmail(), amount, month, yearCode, quarter);
		}
		
		logger.debug(CLASSNAME + "updateOfflinePaymentDetails : End");
	}
	
	public Map<String, String> processPaymentResponse(PaymentRequestDetails paymentRequestDetails, String userid, 
			String encryptedResponse, String response, SubscriptionPlan plan) {
		logger.debug(CLASSNAME + "processPaymentResponse : Begin");
		Pattern pattern = Pattern.compile("(\\w+)=?([^&]+)?");
		Matcher matcher = pattern.matcher(response);
		Map<String, String> responseMap = Maps.newHashMap();
		while (matcher.find()) {
			responseMap.put(matcher.group(1), matcher.group(2));
		}
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		PaymentDetails paymentDetails=new PaymentDetails();
		paymentDetails.setUserid(userid);
		paymentDetails.setPlanid(plan.getId().toString());
		paymentDetails.setPymtrequestid(paymentRequestDetails.getId().toString());
		paymentDetails.setAmount(plan.getPrice()*1d);
		paymentDetails.setResponse(encryptedResponse);
		paymentDetails.setPaymentDate(today);
		paymentDetails.setCustomerName(paymentRequestDetails.getCustomerName());
		paymentDetails.setCustomerAddress(paymentRequestDetails.getCustomerAddress());
		paymentDetails.setCustomerGSTN(paymentRequestDetails.getCustomerGSTN());
		paymentDetails.setCustomerPOS(paymentRequestDetails.getCustomerPOS());
		int month=-1,year=-1;
		
		month = today.getMonth();
		year = today.getYear()+1900;
						
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		paymentDetails.setMthCd(""+month);
		paymentDetails.setYrCd(""+yearCode);
		paymentDetails.setPartnerPayment(MasterGSTConstants.PENDING);
		
		paymentDetails.setCouponCode(paymentRequestDetails.getCouponCode());
		paymentDetails.setTotalAmount(paymentRequestDetails.getAmount());
		if(NullUtil.isNotEmpty(paymentRequestDetails.getDiscountAmount())) {
			paymentDetails.setDiscountAmount(paymentRequestDetails.getDiscountAmount());
		}
		paymentDetails.setRate(18d);
		String customerState = paymentRequestDetails.getCustomerPOS();
		boolean isIntraState = true;
		if (customerState.contains("-")) {
			customerState = customerState.substring(0, customerState.indexOf("-")).trim();
			if(Integer.parseInt(customerState) == 36) {
				isIntraState = false;
				paymentDetails.setCgstrate(18d/2);
				paymentDetails.setSgstrate(18d/2);
				paymentDetails.setCgstamount(paymentRequestDetails.getCgstAmount());
				paymentDetails.setSgstamount(paymentRequestDetails.getSgstAmount());
			}else {
				paymentDetails.setIgstrate(18d);
				paymentDetails.setIgstamount(paymentRequestDetails.getIgstAmount());
			}
		}else {
			if(customerState.equalsIgnoreCase("Telangana")) {
				isIntraState = false;
				paymentDetails.setCgstrate(18d/2);
				paymentDetails.setSgstrate(18d/2);
				paymentDetails.setCgstamount(paymentRequestDetails.getCgstAmount());
				paymentDetails.setSgstamount(paymentRequestDetails.getSgstAmount());
			}else {
				paymentDetails.setIgstrate(18d);
				paymentDetails.setIgstamount(paymentRequestDetails.getIgstAmount());
			}
		}
		
		String status = null;
		if(NullUtil.isNotEmpty(responseMap)) {
			if(responseMap.containsKey("order_status")) {
				status = responseMap.get("order_status");
				paymentDetails.setStatus(status);
			}
			if(NullUtil.isNotEmpty(status) && status.equals("Success")) {
				User dbUser = userService.findById(userid);
				if(NullUtil.isNotEmpty(dbUser)) {
					dbUser.setNeedToFollowUp("Closed");
					userService.updateUser(dbUser);
				}
				if(responseMap.containsKey("order_id")) {
					paymentDetails.setOrderId(responseMap.get("order_id"));
				}
				if(responseMap.containsKey("tracking_id")) {
					paymentDetails.setTrackingId(responseMap.get("tracking_id"));
				}
				SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUserid(userid);
				if(NullUtil.isEmpty(subscriptionDetails)) {
					subscriptionDetails = new SubscriptionDetails();
					subscriptionDetails.setUserid(userid);
				}
				if(plan.getCategory().equals("AddOn")) {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+plan.getInvoice());
				} else {
					subscriptionDetails.setPlanid(plan.getId().toString());
					subscriptionDetails.setAllowedClients(plan.getClient());
					subscriptionDetails.setAllowedInvoices(plan.getInvoice());
					subscriptionDetails.setPaidAmount(plan.getPrice()*1d);
					subscriptionDetails.setRegisteredDate(today);
					if(plan.getDuration() == 12) {
						cal.add(Calendar.YEAR, 1);
						cal.add(Calendar.DATE, -1);
						Date nextYear = cal.getTime();
						subscriptionDetails.setExpiryDate(nextYear);
					}
				}
				subscriptionDetails.setUserDocid(new ObjectId(subscriptionDetails.getUserid()));
				subscriptionDetailsRepository.save(subscriptionDetails);
				
				GSTR1 invoice = new GSTR1();
				Calendar cals = Calendar.getInstance();
				cals.setTime(today);
				month = cals.get(Calendar.MONTH) + 1;
				year = cals.get(Calendar.YEAR);
				String strMonth = month < 10 ? "0" + month : month + "";
				invoice.setFp(strMonth + year);
				String submissionYear = (year - 1) + "-" + (year);
				if (month > 3) {
					submissionYear = year + "-" + (year + 1);
				}
				Map<String,Object> nextInvoiceNumber = clientService.getDefaultInvoiceNo(mastergstClientid,mastergstuserid, MasterGSTConstants.GSTR1, MasterGSTConstants.B2B, submissionYear,month);
				String invoiceno = nextInvoiceNumber.get("InvoiceNumber").toString();
				invoice.setClientid(mastergstClientid);
				invoice.setUserid(mastergstuserid);
				invoice.setFullname("Mallikharjuna Rao Chittabathina");
				invoice.setInvoiceno(invoiceno);
				invoice.setDateofinvoice(today);
				if (!paymentRequestDetails.getCustomerPOS().contains("-")) {
					String statename = paymentRequestDetails.getCustomerPOS();
					List<StateConfig> states = configService.getStates();
					for (StateConfig state : states) {
						String name = state.getName();
						String[] nm = state.getName().split("-");
						if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
							statename = name;
							break;
						}
					}
					invoice.setStatename(statename);
				}else {
					invoice.setStatename(paymentRequestDetails.getCustomerPOS());
				}
				invoice.setBilledtoname(paymentRequestDetails.getCustomerName());
				invoice.getB2b().get(0).setCtin(paymentRequestDetails.getCustomerGSTN());
				invoice.getB2b().get(0).getInv().get(0).setAddress(paymentRequestDetails.getCustomerAddress());
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invoice.getB2b().get(0).getInv().get(0).setInum(invoiceno);
				invoice.setRevchargetype("Regular");
				String usertype = paymentRequestDetails.getUsertype();
				Item invItem = new Item();
				String itemname = "GST Software for CA and Tax Pro Basic";
				if(usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
					itemname = "GST Suvidha Center";
				}else if(usertype.equals(MasterGSTConstants.ENTERPRISE)) {
					itemname = "GST Enterprise Web Solution";
				}else if(usertype.equals(MasterGSTConstants.CAS) || usertype.equals(MasterGSTConstants.TAXPRACTITIONERS)) {
					itemname = "GST Software for CA and Tax Pro Basic";
				}else if(usertype.equals(MasterGSTConstants.BUSINESS)) {
					itemname = "GST Software Small Business Basic";
				}
				CompanyItems item= companyItemsRepository.findByClientidAndDescription(mastergstClientid,itemname);
				if(NullUtil.isNotEmpty(item)) {
					invItem.setItemno(item.getDescription());
					invItem.setHsn(item.getCode());
					invItem.setUqc(item.getUnit());
				}
					invItem.setQuantity(1d);
					if(NullUtil.isNotEmpty(paymentRequestDetails.getDiscountAmount())) {
						invItem.setDiscount(paymentRequestDetails.getDiscountAmount());
						invItem.setTaxablevalue(paymentRequestDetails.getActualAmount() - paymentRequestDetails.getDiscountAmount());
					}else {
						invItem.setTaxablevalue(paymentRequestDetails.getActualAmount());
					}
					invItem.setRateperitem(paymentRequestDetails.getActualAmount());
					invItem.setRate(18d);
					if(NullUtil.isNotEmpty(paymentRequestDetails.getIgstAmount())) {
						invItem.setIgstamount(paymentRequestDetails.getIgstAmount());
					}else{
						invItem.setSgstamount(paymentRequestDetails.getSgstAmount());
						invItem.setCgstamount(paymentRequestDetails.getCgstAmount());
					}
					invItem.setTotal(paymentRequestDetails.getAmount());
					List<Item> items = Lists.newArrayList();
					items.add(invItem);
					invoice.setItems(items);
					invoice.setInvtype(MasterGSTConstants.B2B);
					CompanyBankDetails bankdetails=  companyBankDetailsRepository.findByClientidAndAccountnumber(mastergstClientid,bankAccountNumber);
					if(NullUtil.isNotEmpty(bankdetails)) {
						CompanyBankDetails bankDetails = new CompanyBankDetails();
						bankDetails.setAccountName(bankdetails.getAccountName());
						bankDetails.setAccountnumber(bankdetails.getAccountnumber());
						bankDetails.setBankname(bankdetails.getBankname());
						bankDetails.setIfsccode(bankdetails.getIfsccode());
						bankDetails.setBranchname(bankdetails.getBranchname());
						invoice.setBankDetails(bankDetails);
					}
					InvoiceParent invoiceForJournal;
					try {
						invoiceForJournal = clientService.saveSalesInvoice(invoice,null, isIntraState);
						clientService.saveJournalInvoice(invoiceForJournal,mastergstClientid,MasterGSTConstants.GSTR1,isIntraState);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			} else if(responseMap.containsKey("failure_message")) {
				paymentDetails.setFailureMessage(responseMap.get("failure_message"));
			}
		}
		paymentDetailsRepository.save(paymentDetails);
		logger.debug(CLASSNAME + "processPaymentResponse : End");
		return responseMap;
	}
	
	public List<PaymentDetails> getPaymentDetails(final String userId) {
		logger.debug(CLASSNAME + "getPaymentDetails : Begin");
		return paymentDetailsRepository.findByUserid(userId);
	}

	@Override
	public SubscriptionPlan getPlanByName(String name) {
		logger.debug(CLASSNAME + "getPlan : Begin");
		SubscriptionPlan plan;
		if("Test".equals(name)){
			plan = subscriptionPlanRepository.findByNameAndCategory(name,"Test");
		}else{
			plan = subscriptionPlanRepository.findByNameAndCategory(name,"ASP");
		}
		return plan;
	}
	
	@Override
	public SubscriptionPlan getPlanByNameForBussiness(String name) {
		logger.debug(CLASSNAME + "getPlan : Begin");
		SubscriptionPlan plan;
		if("Test".equals(name)){
			plan = subscriptionPlanRepository.findByNameAndCategory(name,"Test");
		}else{
			plan = subscriptionPlanRepository.findByNameAndCategory(name,"Business");
		}
		return plan;
	}

	@Override
	public SubscriptionDetails getSubscriptionPaymentDetails(String userid,String apiType) {
	
		logger.debug(CLASSNAME + "getSubscriptionPaymentDetails : Begin");
			
		return subscriptionDetailsRepository.findByUseridAndApiType(userid,apiType);
				
	}
	
	@Override
	public SubscriptionDetails getSubscriptionPaymentDetails(String userid) {
	
		logger.debug(CLASSNAME + "getSubscriptionPaymentDetails : Begin");
			
		return subscriptionDetailsRepository.findByUserid(userid);
				
	}
	
	
	public void updateOfflinePaymentDetailsTOPUPORRENWEAL(final String id, final String usertype, final Double amount, 
			final Integer invoices, final Integer clients, final Integer centers, final Double rate, 
			final String paymentMode, final String referenceNo, final String apiType, final String statename, final String planid,final String subscriptionType,final String subscriptionStartDate,final String subscriptionExpiryDate) throws ParseException {
			logger.debug(CLASSNAME + "updateOfflinePaymentDetails : Begin");
		User dbUser = userService.findById(id);
		if(NullUtil.isNotEmpty(dbUser)) {
			dbUser.setDisable("false");
			userService.updateUser(dbUser);
		}
			Calendar cal = Calendar.getInstance();
			Date today = cal.getTime();
			PaymentDetails paymentDetails=new PaymentDetails();
			paymentDetails.setUserid(id);
			paymentDetails.setPlanid(planid);
			paymentDetails.setAmount(amount);
			paymentDetails.setResponse(paymentMode+" - "+referenceNo);
			paymentDetails.setPaymentDate(today);
			paymentDetails.setStatus("Success");
			paymentDetails.setStatename(statename);
			paymentDetails.setSubscriptionType(subscriptionType);
			paymentDetails.setSubscriptionStartDate(subscriptionStartDate);
			paymentDetails.setSubscriptionExpiryDate(subscriptionExpiryDate);
			
			int month=-1,year=-1;
			
			month = today.getMonth();
			year = today.getYear()+1900;
							
			int quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			paymentDetails.setMthCd(""+month);
			paymentDetails.setYrCd(""+yearCode);
			paymentDetails.setPartnerPayment(MasterGSTConstants.PENDING);
			
			SubscriptionDetails subscriptionDetails =null;
			if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				paymentDetails.setApiType(apiType);
				subscriptionDetails=subscriptionDetailsRepository.findByUseridAndApiType(id, apiType);
			}else {
				subscriptionDetails=subscriptionDetailsRepository.findByUserid(id);
			}
			if(!usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				if(NullUtil.isEmpty(subscriptionDetails)) {
					subscriptionDetails = new SubscriptionDetails();
					subscriptionDetails.setUserid(id);
				}
			}else{
				if(NullUtil.isEmpty(subscriptionDetails)) {
				subscriptionDetails = new SubscriptionDetails();
				subscriptionDetails.setUserid(id);
				}
			}
			subscriptionDetails.setPlanid(subscriptionDetails.getPlanid());
			subscriptionDetails.setAllowedClients(clients != -1 ? clients : 0);
			subscriptionDetails.setAllowedCenters(centers != -1 ? centers : 0);
			if("EWAYBILLSANDBOXAPI".equalsIgnoreCase(subscriptionDetails.getApiType()) || "GSTSANDBOXAPI".equalsIgnoreCase(subscriptionDetails.getApiType()) || "E-INVOICESANDBOXAPI".equalsIgnoreCase(subscriptionDetails.getApiType())) {
				if(NullUtil.isEmpty(subscriptionDetails.getProcessedSandboxInvoices())) {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+invoices);
				}else {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+invoices);
					subscriptionDetails.setProcessedSandboxInvoices(subscriptionDetails.getProcessedSandboxInvoices());
				}
			}else {
				if("RENEWALAPI".equalsIgnoreCase(subscriptionType)) {
					if(NullUtil.isEmpty(subscriptionDetails.getProcessedInvoices())) {
						subscriptionDetails.setAllowedInvoices(invoices);
					}else {
						subscriptionDetails.setAllowedInvoices(invoices);
						subscriptionDetails.setProcessedInvoices(0);
					}
				}else {
				if(NullUtil.isEmpty(subscriptionDetails.getProcessedInvoices())) {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+invoices);
				}else {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+invoices);
					subscriptionDetails.setProcessedInvoices(subscriptionDetails.getProcessedInvoices());
					}					
				}
			}
			Date dt = new Date();
			month=-1;
			year=-1;
			month = dt.getMonth();
			year = dt.getYear()+1900;
			quarter = month/3;
			quarter = quarter == 0 ? 4 : quarter;
			yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
			month++;
			subscriptionDetails.setMthCd(month+"");
			subscriptionDetails.setYrCd(yearCode);
			subscriptionDetails.setQrtCd(quarter+"");
			subscriptionDetails.setPaidAmount(amount);				
			subscriptionDetails.setInvoiceCost(rate);
			if("TOPUPAPI".equalsIgnoreCase(subscriptionType)){
				subscriptionDetails.setRegisteredDate(subscriptionDetails.getRegisteredDate());
				subscriptionDetails.setExpiryDate(subscriptionDetails.getExpiryDate());
			}else if("DEMO".equalsIgnoreCase(subscriptionType)) {
				subscriptionDetails.setRegisteredDate(today);
				DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
				Date expryDate=sdf.parse(subscriptionExpiryDate);
				subscriptionDetails.setExpiryDate(expryDate);
			}else if("UNLIMITED".equalsIgnoreCase(subscriptionType)) {
				if(isNotEmpty(subscriptionStartDate)) {
					DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date startDate=sdf.parse(subscriptionStartDate);
					subscriptionDetails.setRegisteredDate(startDate);
				}else {
					subscriptionDetails.setRegisteredDate(today);
				}
			}else{
				if(isNotEmpty(subscriptionStartDate)) {
					DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date startDate=sdf.parse(subscriptionStartDate);
					subscriptionDetails.setRegisteredDate(startDate);
				}else {
					subscriptionDetails.setRegisteredDate(today);
				}
				if(isNotEmpty(subscriptionExpiryDate)) {
					DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
					sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
					Date expryDate=sdf.parse(subscriptionExpiryDate);
					subscriptionDetails.setExpiryDate(expryDate);
				}else {
					cal.add(Calendar.YEAR, 1);
					cal.add(Calendar.DATE, -1);
					Date nextYear = cal.getTime();
					subscriptionDetails.setExpiryDate(nextYear);
				}
			}
			subscriptionDetails.setSubscriptionType(subscriptionType);
			subscriptionDetails.setSubscriptionTopupDate(today);
			if(usertype.equals(MasterGSTConstants.ASPDEVELOPER)){
				subscriptionDetails.setApiType(apiType);
			}
			subscriptionDetails.setUserDocid(new ObjectId(subscriptionDetails.getUserid()));
			subscriptionDetailsRepository.save(subscriptionDetails);
			paymentDetailsRepository.save(paymentDetails);
		logger.debug(CLASSNAME + "updateOfflinePaymentDetails : End");
	}

	@Override
	public List<SubscriptionDetails> getAllSubscriptionDetailsByUser(String userid) {
		Query query = new Query();
		Criteria criteria = new Criteria();
		query.addCriteria(criteria.where("userid").is(userid));
		query.addCriteria(criteria);
		return mongoTemplate.find(query, SubscriptionDetails.class, "subscription_data");
	}
	
	@Override
	public List<SubscriptionDetails> getEwaybillAPIsSubscriptionDetailsApiTye(String userid) {
				
		List<String> list = new ArrayList<String>();
		list.add("EWAYAPI");
		list.add("EWAYBILLSANDBOXAPI");
		
		return subscriptionDetailsRepository.findByUseridAndApiTypeIn(userid, list);
	}
	
	@Override
	public List<PaymentDetails> getPaymentDetails(String userid, String apiType) {
		
		return paymentDetailsRepository.findByUseridAndApiType(userid, apiType);
	}
	
	@Override
	public List<PaymentDetails> getPaymentDetails(String userid, String apiType,List<String> subscriptionstartdate,final List<String> subscriptionenddate) {
		
		return paymentDetailsRepository.findByUseridAndApiTypeAndSubscriptionStartDateInAndSubscriptionExpiryDateIn(userid, apiType,subscriptionstartdate,subscriptionenddate);
	}
	
	@Override
	@Transactional
	public List<SubscriptionDetails> getAllSubscriptions() {
		return (List<SubscriptionDetails>) subscriptionDetailsRepository.findAll();
	}
	
	@Override
	@Transactional
	public void saveSubscriptionDetails(List<SubscriptionDetails> subscriptionDetails) {
		subscriptionDetailsRepository.save(subscriptionDetails);
	}
	
	@Override
	@Transactional
	public List<SubscriptionDetails> getSubscriptionDetailsByDates(final Date startDate, final Date endDate) {
		
		return subscriptionDetailsRepository.findByCreatedDateBetween(startDate, endDate);
	}
	
	/*@Override
	@Transactional
	public Page<SubscriptionDetails> getBeforeFourtyFiveDaysExpiryUsers() {
		
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		cal.add(Calendar.DATE, 45);
		Date getNext45Days = cal.getTime();
		Query query = new Query();
		//Criteria criteria = new Criteria();
		//List<SubscriptionDetails> subscriptionDetails=subscriptionDetailsRepository.findAll();	
		query.addCriteria(Criteria.where("expiryDate").gte(today).lte(getNext45Days));
		return new PageImpl<SubscriptionDetails>(mongoTemplate.find(query, SubscriptionDetails.class, "subscription_data"));
	}
	
	@Override
	public Page<SubscriptionDetails> getExpiredUsers() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		Query query = new Query();
		//Criteria criteria = new Criteria();
		//List<SubscriptionDetails> subscriptionDetails=subscriptionDetailsRepository.findAll();	
		query.addCriteria(Criteria.where("expiryDate").lte(today));
		return new PageImpl<SubscriptionDetails>(mongoTemplate.find(query, SubscriptionDetails.class, "subscription_data"));
	}*/
	@Override
	public Page<SubscriptionUsers> getExpiredUsers(String type, int start, int length, String searchVal) {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		Pageable pageable = new PageRequest((start/length), length);
	    Aggregation aggregation = null;
	    Aggregation aggCount = null;
	    
	    Criteria criteria = Criteria.where("apiType").nin("GSTSANDBOXAPI", "EWAYBILLSANDBOXAPI", "E-INVOICESANDBOXAPI");
		//.andOperator(Criteria.where("user").ne(null));
	    if(type.equals("ALL_EXPIRED_USERS")) {
	    	criteria.and("expiryDate").lte(today).and("user.needToFollowUp").ne("Test Account");
	    }else if(type.equals("PAID_EXPIRED_USERS")) {
	    	criteria.and("expiryDate").lte(today).and("user.needToFollowUp").is("Closed");
	    }else if(type.equals("EXPIRED_IN_NEXT_45")){
	    	cal.add(Calendar.DATE, 45);
			Date getNext45Days = cal.getTime();
			criteria.and("expiryDate").gte(today).lte(getNext45Days).and("user.needToFollowUp").ne("Test Account");
	    }
	    if(StringUtils.hasLength(searchVal)){
	    	criteria = new Criteria().andOperator(criteria, adminUtils.getExpiredUsersSearchValueCriteria(searchVal));
	    	aggregation = Aggregation.newAggregation(
	    				Aggregation.lookup("users", "userDocid", "_id", "user"),
	    				Aggregation.match(criteria),
	    				Aggregation.unwind("user", false),
	    				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       				Aggregation.limit(pageable.getPageSize())
	       			);
	    	aggCount = Aggregation.newAggregation(
    				Aggregation.lookup("users", "userDocid", "_id", "user"),
    				Aggregation.match(criteria),
    				Aggregation.unwind("user", false)
        		);
	    }else {
	       	aggregation = Aggregation.newAggregation(
	       				Aggregation.lookup("users", "userDocid", "_id", "user"),
	       				Aggregation.match(criteria),
	       				Aggregation.unwind("user", false),
	       				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
	       				Aggregation.limit(pageable.getPageSize())
	       			);
	       	aggCount = Aggregation.newAggregation(
        			Aggregation.lookup("users", "userDocid", "_id", "user"),
        			Aggregation.match(criteria),
        			Aggregation.unwind("user", false)
        		);
	    }
	    
	    long total =  mongoTemplate.aggregate(aggCount, "subscription_data", SubscriptionUsers.class).getMappedResults().size();
	    
	    if (total == 0) {
			return new PageImpl<SubscriptionUsers>(Collections.<SubscriptionUsers> emptyList());
		}
	    
	    List<SubscriptionUsers> aggregationResult = mongoTemplate.aggregate(aggregation, "subscription_data", SubscriptionUsers.class).getMappedResults();
        return new PageImpl<SubscriptionUsers>(aggregationResult, pageable, total);
	}
	
	@Override
	@Transactional
	public SubscriptionDetails getRetriveSubscriptionDetailsByObjectId(String id) {
		
		return subscriptionDetailsRepository.findById(id);
	}
	
	
	@Override
	public List<User> getPaidUsersAndActiveUsers() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		
		List<String> userTypes=Arrays.asList(MasterGSTConstants.CAS,MasterGSTConstants.ENTERPRISE,MasterGSTConstants.BUSINESS,MasterGSTConstants.SUVIDHA_CENTERS,MasterGSTConstants.PARTNER);
		
		List<SubscriptionDetails> subscriptionUsers=subscriptionDetailsRepository.findAll();
				//findByUserPaymentType(MasterGSTConstants.PAID_USER);
		
		List<String> paidUserIds=subscriptionUsers.stream().map(subscriptioDetails->subscriptioDetails.getUserid()).collect(Collectors.toList());
		
		Page<User> usrs=userRepository.findByIdInAndDisableAndNeedToFollowUpAndTypeIn(paidUserIds, "false", "Closed",userTypes, new PageRequest(0, Integer.MAX_VALUE)); 
		
		Map<String,User> userMap=usrs.getContent().stream().collect(Collectors.toMap(usr->usr.getId().toString(), Function.identity()));
		
		List<User> usrLst=new ArrayList<User>();
		
		for(SubscriptionDetails details:subscriptionUsers) {
			
			User user=userMap.get(details.getUserid());
			if(NullUtil.isNotEmpty(user)) {
				user.setUserId(user.getId().toString());
				user.setUserSignDate(dateFormat.format(user.getCreatedDate()));
				user.setSubscriptionStartDate(dateFormat.format(details.getRegisteredDate()));
				user.setSubscriptionExpiryDate(dateFormat.format(details.getExpiryDate()));
				user.setPatmentStatus("paid");
				usrLst.add(user);
			}
		}
		List<String> apislist = Arrays.asList(MasterGSTConstants.GST_API,MasterGSTConstants.EWAYBILL_API,MasterGSTConstants.EWAYBILLPRODUCTION,MasterGSTConstants.GSTSANDBOX_API);
		List<SubscriptionDetails> subscription=subscriptionDetailsRepository.findByApiTypeIn(apislist);
		
		
		for(SubscriptionDetails details:subscription) {
			User ussr=userRepository.findById(details.getUserid());
			
			if(NullUtil.isNotEmpty(ussr)) {
				if((details.getApiType().equalsIgnoreCase(MasterGSTConstants.EWAYBILLSANDBOX) || details.getApiType().equalsIgnoreCase(MasterGSTConstants.GSTSANDBOX_API)) ) {
					if(NullUtil.isNotEmpty(details.getSubscriptionType())) {
						if(!"DEMO".equalsIgnoreCase(details.getSubscriptionType())) {
							ussr.setUserId(ussr.getId().toString());
							ussr.setUserSignDate(dateFormat.format(ussr.getCreatedDate()));
							if(NullUtil.isNotEmpty(details.getRegisteredDate())) {
								ussr.setSubscriptionStartDate(dateFormat.format(details.getRegisteredDate()));
							}
							if(NullUtil.isNotEmpty(details.getApiType())) {
								ussr.setApiType(details.getApiType());
							}
							ussr.setPatmentStatus("paid");
							usrLst.add(ussr);	
						}
					}
				}else {
					ussr.setUserId(ussr.getId().toString());
					ussr.setUserSignDate(dateFormat.format(ussr.getCreatedDate()));
					if(NullUtil.isNotEmpty(details.getRegisteredDate())) {
						ussr.setSubscriptionStartDate(dateFormat.format(details.getRegisteredDate()));
					}
					if(NullUtil.isNotEmpty(details.getExpiryDate())) {
						ussr.setSubscriptionExpiryDate(dateFormat.format(details.getExpiryDate()));
					}
					if(NullUtil.isNotEmpty(details.getApiType())) {
						ussr.setApiType(details.getApiType());
					}
					ussr.setPatmentStatus("paid");
					usrLst.add(ussr);					
				}
			}
		}
		
		
		
		
		return usrLst;
	}
	
	@Override
	public List<User> getDemoUsers() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		List<String> followuptypes=Arrays.asList("Call Not Lift","Duplicate","Not Required","Ready to Go","Ready to Pay","Yet to Take Decision","Pricing Issue","");
		List<String> userTypes=Arrays.asList(MasterGSTConstants.CAS,MasterGSTConstants.ENTERPRISE,MasterGSTConstants.BUSINESS,MasterGSTConstants.SUVIDHA_CENTERS,MasterGSTConstants.PARTNER);
		
		List<SubscriptionDetails> subscriptionUsers=subscriptionDetailsRepository.findAll();
		
		List<String> paidUserIds=subscriptionUsers.stream().map(subscriptioDetails->subscriptioDetails.getUserid()).collect(Collectors.toList());
		
		Page<User> usrs=userRepository.findByIdInAndDisable(paidUserIds, "false", new PageRequest(0, Integer.MAX_VALUE)); 
		
		Map<String,User> userMap=usrs.getContent().stream().collect(Collectors.toMap(usr->usr.getId().toString(), Function.identity()));
		
		List<User> usrLst=new ArrayList<User>();
		
		for(SubscriptionDetails details:subscriptionUsers) {
			
			User user=userMap.get(details.getUserid());
			if(NullUtil.isNotEmpty(user)) {
				if(userTypes.contains(user.getType()) && followuptypes.contains(user.getNeedToFollowUp())){
					user.setUserId(user.getId().toString());
					user.setUserSignDate(dateFormat.format(user.getCreatedDate()));
					if(NullUtil.isNotEmpty(details.getRegisteredDate())) {
						user.setSubscriptionStartDate(dateFormat.format(details.getRegisteredDate()));
					}
					if(NullUtil.isNotEmpty(details.getExpiryDate())) {
						user.setSubscriptionExpiryDate(dateFormat.format(details.getExpiryDate()));
					}
					
					user.setPatmentStatus("demo user");
					usrLst.add(user);
				}
			}
		}
		
		List<String> apislist = Arrays.asList(MasterGSTConstants.EWAYBILLPRODUCTION,MasterGSTConstants.GSTSANDBOX_API);
		List<SubscriptionDetails> subscription=subscriptionDetailsRepository.findByApiTypeIn(apislist);
		
		
		for(SubscriptionDetails details:subscription) {
			User ussr=userRepository.findById(details.getUserid());
			
			if(NullUtil.isNotEmpty(ussr)) {
				ussr.setUserId(ussr.getId().toString());
				
				ussr.setUserSignDate(dateFormat.format(ussr.getCreatedDate()));
				if(NullUtil.isNotEmpty(details.getRegisteredDate())) {
					ussr.setSubscriptionStartDate(dateFormat.format(details.getRegisteredDate()));
				}
				if(NullUtil.isNotEmpty(details.getExpiryDate())) {
					ussr.setSubscriptionExpiryDate(dateFormat.format(details.getExpiryDate()));					
				}
				if(NullUtil.isNotEmpty(details.getApiType())) {
					ussr.setApiType(details.getApiType());
				}
				ussr.setPatmentStatus("demo user");
				usrLst.add(ussr);
			}
		}
		
		return usrLst;
	}
	
	@Override
	public Double getByRevenueSubscriptionDataUsersidIn(List<String> userids) {
		
		List<PaymentDetails> revenue=paymentDetailsRepository.findByUseridIn(userids);
		DoubleSummaryStatistics revenue_total=revenue.stream().collect(Collectors.summarizingDouble(PaymentDetails::getAmount));
		return revenue_total.getSum();
	}
	
	@Override
	public List<SubscriptionDetails> getASPUsersidsIn(List<String> userids) {
		
		return subscriptionDetailsRepository.findByUseridIn(userids);
	}
	
	@Override
	public Double getByRevenueSubscriptionDataUsersidInAndApitype(List<String> userids, String apitype) {
		List<PaymentDetails> revenue=paymentDetailsRepository.findByUseridInAndApiType(userids,apitype);
		List<PaymentDetails> revenue_notdemo=revenue.stream().filter(data->!"Demo".equalsIgnoreCase(data.getSubscriptionType())).collect(Collectors.toList());
		DoubleSummaryStatistics revenue_total=revenue_notdemo.stream().collect(Collectors.summarizingDouble(PaymentDetails::getAmount));
		//System.out.println("lambda total ->"+revenue_total.getSum());
		return revenue_total.getSum();
	}
	
	@Override
	public Double getByRevenueSubscriptionDataUsersidInAndNotInDemo(List<String> aspuserids) {
		
		List<PaymentDetails> revenue=paymentDetailsRepository.findByUseridIn(aspuserids);
		List<PaymentDetails> revenue_notdemo=revenue.stream().filter(data->!"Demo".equalsIgnoreCase(data.getSubscriptionType())).collect(Collectors.toList());
		DoubleSummaryStatistics revenue_total=revenue_notdemo.stream().collect(Collectors.summarizingDouble(PaymentDetails::getAmount));
		
		return revenue_total.getSum();
	}
	
	@Override
	public SubscriptionDetails getSubscriptionDataByDelete(String userId) {
	logger.debug(CLASSNAME + "getSubscriptionDataByDelete : Begin");
	SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUserid(userId);

	return subscriptionDetails;
	}

	@Override
	public void deletesubscriptiondata(String subscriptionid) {
	subscriptionDetailsRepository.delete(subscriptionid);
	}
	
	@Override
	public List<SubscriptionDetails> getSubscriptionsByuserid(String userid) {
		return subscriptionDetailsRepository.findByUseridIn(Arrays.asList(userid));
	}
	
	@Override
	public void subscriptiondataDeleteByUserid(List<SubscriptionDetails> subscriptiondata) {
		if(NullUtil.isNotEmpty(subscriptiondata)) {
			for(SubscriptionDetails data:subscriptiondata) {
				if(NullUtil.isNotEmpty(data) && NullUtil.isNotEmpty(data.getUserid())) {
					subscriptionDetailsRepository.delete(data.getId().toString());
				}
			}
		}
	}
	
	@Override
	public Page<ApiExceedsUsers> getExceedUsageApisInfo(String apis, int start, int length, String searchVal) {
		Criteria criteria = new Criteria() {
            @Override
            public DBObject getCriteriaObject() {
                DBObject obj = new BasicDBObject();
               if(apis.equalsIgnoreCase("APIUSERS")) {
                	 obj.put("apiType", new BasicDBObject("$in", Arrays.asList("GSTAPI", "EWAYAPI", "E-INVOICEAPI")));
                }else {
                	 obj.put("apiType", new BasicDBObject("$exists", false));
                }
                obj.put("$where", "this.processedInvoices > this.allowedInvoices");
				return obj;
            }
        };
		Query query = Query.query(criteria);
		long total = mongoTemplate.count(query, ApiExceedsUsers.class, "subscription_data");
		if (total == 0) {
			return new PageImpl<ApiExceedsUsers>(Collections.<ApiExceedsUsers> emptyList());
		}
        
        Pageable pageable = new PageRequest((start/length), length);
        Aggregation aggregation = null;
        if(apis.equalsIgnoreCase("APIUSERS")) {
        	if(StringUtils.hasLength(searchVal)){
        		aggregation = Aggregation.newAggregation(
        				Aggregation.match(Criteria.where("apiType").in("GSTAPI", "EWAYAPI", "E-INVOICEAPI")),
        				Aggregation.lookup("users", "userDocid", "_id", "user"),
        				Aggregation.match(Criteria.where("user.needToFollowUp").ne("Test Account")),
						Aggregation.project(FIELDS_ARRAY).andExpression("processedInvoices - allowedInvoices").as("exceedInvoices"),
        				Aggregation.match(Criteria.where("exceedInvoices").gt(0).andOperator(getSearchValueCriteria(searchVal))),
        				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
        				Aggregation.limit(pageable.getPageSize())
        				);
        	}else {
        		aggregation = Aggregation.newAggregation(
        				Aggregation.match(Criteria.where("apiType").in("GSTAPI", "EWAYAPI", "E-INVOICEAPI")),
        				Aggregation.lookup("users", "userDocid", "_id", "user"),
        				Aggregation.match(Criteria.where("user.needToFollowUp").ne("Test Account")),
						Aggregation.project(FIELDS_ARRAY).andExpression("processedInvoices - allowedInvoices").as("exceedInvoices"),
        				Aggregation.match(Criteria.where("exceedInvoices").gt(0)),
        				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
        				Aggregation.limit(pageable.getPageSize())
        				);        		
        	}
        }else {
        	if(StringUtils.hasLength(searchVal)){
        		aggregation = Aggregation.newAggregation(
		        		Aggregation.match(Criteria.where("apiType").exists(false)),
						Aggregation.lookup("users", "userDocid", "_id", "user"),
						Aggregation.match(Criteria.where("user.needToFollowUp").ne("Test Account")),
						Aggregation.project(FIELDS_ARRAY).andExpression("processedInvoices - allowedInvoices").as("exceedInvoices"),
						Aggregation.match(Criteria.where("exceedInvoices").gt(0).andOperator(getSearchValueCriteria(searchVal))),
						Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
		        		Aggregation.limit(pageable.getPageSize())
					);
        	}else {
        		aggregation = Aggregation.newAggregation(
        				Aggregation.match(Criteria.where("apiType").exists(false)),
        				Aggregation.lookup("users", "userDocid", "_id", "user"),
        				Aggregation.match(Criteria.where("user.needToFollowUp").ne("Test Account")),
						Aggregation.project(FIELDS_ARRAY).andExpression("processedInvoices - allowedInvoices").as("exceedInvoices"),
        				Aggregation.match(Criteria.where("exceedInvoices").gt(0)),
        				Aggregation.skip(pageable.getPageNumber() * pageable.getPageSize()),
        				Aggregation.limit(pageable.getPageSize())
        				);        		
        	}
        }
        List<ApiExceedsUsers> aggregationResult = mongoTemplate.aggregate(aggregation, "subscription_data", ApiExceedsUsers.class).getMappedResults();
        return new PageImpl<ApiExceedsUsers>(aggregationResult, pageable, total);
        
	}
	
	private Criteria getSearchValueCriteria(String searchVal) {
		List<Criteria> criterias = new ArrayList<Criteria>();
	 	criterias.add(Criteria.where("user.fullname").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.email").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.mobilenumber").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("user.type").regex(searchVal, "i"));
	 	criterias.add(Criteria.where("apiType").regex(searchVal, "i"));
		
	 	String serForNum = searchVal;
		boolean isNumber = NumberUtils.isNumber(serForNum);
		if(!isNumber && serForNum.indexOf(",") != -1){
			serForNum = serForNum.replaceAll(",", "");
			isNumber = NumberUtils.isNumber(serForNum);
		}
		if(isNumber){
			criterias.add(Criteria.where("processedInvoices").regex(serForNum, "i"));
			criterias.add(Criteria.where("allowedInvoices").regex(serForNum, "i"));
		}
	 	
	 	return new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()]));
	}
	
	public void updatePartnerClient(String clientid, String email, Double amount, int month, String yearCode, int quarter) {
		
		PartnerClient partnerClient = partnerClientRepository.findByClientid(clientid);
		if(isNotEmpty(partnerClient)) {
			
			partnerClient.setStatus(MasterGSTConstants.SUBSCRIBED);
			partnerClientRepository.save(partnerClient);
		}
		
		User user = userRepository.findByEmail(email);
		Criteria criteria = Criteria.where("userid").is(user.getId().toString())
				.and("mthCd").is(month+"").and("yrCd").is(yearCode);
		
		Query query = Query.query(criteria);
		PartnerPayments payment = mongoTemplate.findOne(query, PartnerPayments.class);
		
		Double subscriptionamts = 0d;
		Double percentage = 0d;
		if(NullUtil.isEmpty(payment)) {
			payment = new PartnerPayments();
			payment.setUserid(user.getId().toString());
			payment.setFullname(user.getFullname());
			payment.setPaidamount(0d);
			subscriptionamts += amount;
			payment.setPartnerPayment(MasterGSTConstants.PENDING);
			payment.setMthCd(month+"");
			payment.setYrCd(yearCode);
			payment.setQrtCd(quarter+"");
			if(isNotEmpty(user.getPartnerPercentage())) {
				percentage = Double.parseDouble(user.getPartnerPercentage());
			}else {
				percentage = 25d;
			}
			subscriptionamts += amount;
		}else {
			subscriptionamts += payment.getSubscriptionamount()+amount;
			percentage = payment.getPercentage(); 
		}
		payment.setSubscriptionamount(subscriptionamts);
		
		Double partneramt = (subscriptionamts * percentage) / 100;
		payment.setPartneramt(partneramt);
		partnerPaymentsRepository.save(payment);
	}
	
	@Override
	public List<PaymentDetails> getPartnerPaymentDetails(String userid, int month, String yearCode){
		Criteria criteria = Criteria.where("userid").is(userid)
				.and("mthCd").is(month+"").and("yrCd").is(yearCode);
		
		Query query = Query.query(criteria);
		
		List<PaymentDetails> payments =  mongoTemplate.find(query, PaymentDetails.class, "payment_data");
		return payments;
	}

	@Override
	public Map<String, String> processPaymentLinkResponse(PaymentRequestDetails paymentRequestDetails, String userid,
			String encryptedResponse, String response, PaymentLink plan) {
		logger.debug(CLASSNAME + "processPaymentResponse : Begin");
		Pattern pattern = Pattern.compile("(\\w+)=?([^&]+)?");
		Matcher matcher = pattern.matcher(response);
		Map<String, String> responseMap = Maps.newHashMap();
		while (matcher.find()) {
			responseMap.put(matcher.group(1), matcher.group(2));
		}
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		PaymentDetails paymentDetails=new PaymentDetails();
		paymentDetails.setUserid(userid);
		paymentDetails.setPlanid(plan.getId().toString());
		paymentDetails.setPymtrequestid(paymentRequestDetails.getId().toString());
		paymentDetails.setAmount(plan.getPaidAmount());
		paymentDetails.setResponse(encryptedResponse);
		paymentDetails.setPaymentDate(today);
		paymentDetails.setCustomerName(paymentRequestDetails.getCustomerName());
		paymentDetails.setCustomerAddress(paymentRequestDetails.getCustomerAddress());
		paymentDetails.setCustomerGSTN(paymentRequestDetails.getCustomerGSTN());
		paymentDetails.setCustomerPOS(paymentRequestDetails.getCustomerPOS());
		int month=-1,year=-1;
		
		month = today.getMonth();
		year = today.getYear()+1900;
						
		int quarter = month/3;
		quarter = quarter == 0 ? 4 : quarter;
		String yearCode = quarter == 4 ? (year-1)+"-"+year : (year)+"-"+(year+1);
		month++;
		paymentDetails.setMthCd(""+month);
		paymentDetails.setYrCd(""+yearCode);
		paymentDetails.setPartnerPayment(MasterGSTConstants.PENDING);
		
		paymentDetails.setCouponCode(paymentRequestDetails.getCouponCode());
		paymentDetails.setTotalAmount(paymentRequestDetails.getAmount());
		if(NullUtil.isNotEmpty(paymentRequestDetails.getDiscountAmount())) {
			paymentDetails.setDiscountAmount(paymentRequestDetails.getDiscountAmount());
		}
		paymentDetails.setRate(18d);
		String customerState = paymentRequestDetails.getCustomerPOS();
		boolean isIntraState = true;
		if (customerState.contains("-")) {
			customerState = customerState.substring(0, customerState.indexOf("-")).trim();
			if(Integer.parseInt(customerState) == 36) {
				isIntraState = false;
				paymentDetails.setCgstrate(18d/2);
				paymentDetails.setSgstrate(18d/2);
				paymentDetails.setCgstamount(paymentRequestDetails.getCgstAmount());
				paymentDetails.setSgstamount(paymentRequestDetails.getSgstAmount());
			}else {
				paymentDetails.setIgstrate(18d);
				paymentDetails.setIgstamount(paymentRequestDetails.getIgstAmount());
			}
		}else {
			if(customerState.equalsIgnoreCase("Telangana")) {
				isIntraState = false;
				paymentDetails.setCgstrate(18d/2);
				paymentDetails.setSgstrate(18d/2);
				paymentDetails.setCgstamount(paymentRequestDetails.getCgstAmount());
				paymentDetails.setSgstamount(paymentRequestDetails.getSgstAmount());
			}else {
				paymentDetails.setIgstrate(18d);
				paymentDetails.setIgstamount(paymentRequestDetails.getIgstAmount());
			}
		}
		
		String status = null;
		if(NullUtil.isNotEmpty(responseMap)) {
			if(responseMap.containsKey("order_status")) {
				status = responseMap.get("order_status");
				paymentDetails.setStatus(status);
			}
			if(NullUtil.isNotEmpty(status) && status.equals("Success")) {
				User dbUser = userService.findById(userid);
				if(NullUtil.isNotEmpty(dbUser)) {
					dbUser.setNeedToFollowUp("Closed");
					userService.updateUser(dbUser);
				}
				if(responseMap.containsKey("order_id")) {
					paymentDetails.setOrderId(responseMap.get("order_id"));
				}
				if(responseMap.containsKey("tracking_id")) {
					paymentDetails.setTrackingId(responseMap.get("tracking_id"));
				}
				SubscriptionDetails subscriptionDetails = subscriptionDetailsRepository.findByUserid(userid);
				if(NullUtil.isEmpty(subscriptionDetails)) {
					subscriptionDetails = new SubscriptionDetails();
					subscriptionDetails.setUserid(userid);
				}
				
				subscriptionDetails.setAllowedInvoices(plan.getAllowedInvoices());
				subscriptionDetails.setPaidAmount(plan.getPaidAmount());
				subscriptionDetails.setRegisteredDate(today);
				if(plan.getCategory().equals("AddOn")) {
					subscriptionDetails.setAllowedInvoices(subscriptionDetails.getAllowedInvoices()+plan.getAllowedInvoices());
				} else {
					cal.add(Calendar.YEAR, 1);
					cal.add(Calendar.DATE, -1);
					Date nextYear = cal.getTime();
					subscriptionDetails.setExpiryDate(nextYear);
				}
				subscriptionDetails.setUserDocid(new ObjectId(subscriptionDetails.getUserid()));
				subscriptionDetailsRepository.save(subscriptionDetails);
				
				GSTR1 invoice = new GSTR1();
				Calendar cals = Calendar.getInstance();
				cals.setTime(today);
				month = cals.get(Calendar.MONTH) + 1;
				year = cals.get(Calendar.YEAR);
				String strMonth = month < 10 ? "0" + month : month + "";
				invoice.setFp(strMonth + year);
				String submissionYear = (year - 1) + "-" + (year);
				if (month > 3) {
					submissionYear = year + "-" + (year + 1);
				}
				Map<String,Object> nextInvoiceNumber = clientService.getDefaultInvoiceNo(mastergstClientid,mastergstuserid, MasterGSTConstants.GSTR1, MasterGSTConstants.B2B, submissionYear,month);
				String invoiceno = nextInvoiceNumber.get("InvoiceNumber").toString();
				invoice.setClientid(mastergstClientid);
				invoice.setUserid(mastergstuserid);
				invoice.setFullname("Mallikharjuna Rao Chittabathina");
				invoice.setInvoiceno(invoiceno);
				invoice.setDateofinvoice(today);
				if (!paymentRequestDetails.getCustomerPOS().contains("-")) {
					String statename = paymentRequestDetails.getCustomerPOS();
					List<StateConfig> states = configService.getStates();
					for (StateConfig state : states) {
						String name = state.getName();
						String[] nm = state.getName().split("-");
						if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
							statename = name;
							break;
						}
					}
					invoice.setStatename(statename);
				}else {
					invoice.setStatename(paymentRequestDetails.getCustomerPOS());
				}
				invoice.setBilledtoname(paymentRequestDetails.getCustomerName());
				invoice.getB2b().get(0).setCtin(paymentRequestDetails.getCustomerGSTN());
				invoice.getB2b().get(0).getInv().get(0).setAddress(paymentRequestDetails.getCustomerAddress());
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invoice.getB2b().get(0).getInv().get(0).setInum(invoiceno);
				invoice.setRevchargetype("Regular");
				if(plan.getRateofinclusivetax().equalsIgnoreCase("true")) {
					invoice.setIncludetax("Yes");
				}
				String usertype = paymentRequestDetails.getUsertype();
				Item invItem = new Item();
				String itemname = "GST Software for CA and Tax Pro Basic";
				if(usertype.equals(MasterGSTConstants.SUVIDHA_CENTERS)) {
					itemname = "GST Suvidha Center";
				}else if(usertype.equals(MasterGSTConstants.ENTERPRISE)) {
					itemname = "GST Enterprise Web Solution";
				}else if(usertype.equals(MasterGSTConstants.CAS) || usertype.equals(MasterGSTConstants.TAXPRACTITIONERS)) {
					itemname = "GST Software for CA and Tax Pro Basic";
				}else if(usertype.equals(MasterGSTConstants.BUSINESS)) {
					itemname = "GST Software Small Business Basic";
				}
				CompanyItems item= companyItemsRepository.findByClientidAndDescription(mastergstClientid,itemname);
				if(NullUtil.isNotEmpty(item)) {
					invItem.setItemno(item.getDescription());
					invItem.setHsn(item.getCode());
					invItem.setUqc(item.getUnit());
				}
					invItem.setQuantity(1d);
					if(NullUtil.isNotEmpty(paymentRequestDetails.getDiscountAmount())) {
						invItem.setDiscount(Double.parseDouble(df2.format(paymentRequestDetails.getDiscountAmount())));
						if(plan.getRateofinclusivetax().equalsIgnoreCase("true")) {
							double taxableamt = (paymentRequestDetails.getActualAmount() - paymentRequestDetails.getDiscountAmount())*100/118;
							invItem.setTaxablevalue(Double.parseDouble(df2.format(taxableamt)));
						}else {
							invItem.setTaxablevalue(Double.parseDouble(df2.format(paymentRequestDetails.getActualAmount() - paymentRequestDetails.getDiscountAmount())));
						}
					}else {
						if(plan.getRateofinclusivetax().equalsIgnoreCase("true")) {
							double taxableamt = paymentRequestDetails.getActualAmount()*100/118;
							invItem.setTaxablevalue(Double.parseDouble(df2.format(taxableamt)));
						}else {
							invItem.setTaxablevalue(Double.parseDouble(df2.format(paymentRequestDetails.getActualAmount())));
						}
					}
					invItem.setRateperitem(Double.parseDouble(df2.format(paymentRequestDetails.getActualAmount())));
					invItem.setRate(18d);
					if(NullUtil.isNotEmpty(paymentRequestDetails.getIgstAmount())) {
						invItem.setIgstamount(Double.parseDouble(df2.format(paymentRequestDetails.getIgstAmount())));
					}else{
						invItem.setSgstamount(Double.parseDouble(df2.format(paymentRequestDetails.getSgstAmount())));
						invItem.setCgstamount(Double.parseDouble(df2.format(paymentRequestDetails.getCgstAmount())));
					}
					invItem.setTotal(Double.parseDouble(df2.format(paymentRequestDetails.getAmount())));
					List<Item> items = Lists.newArrayList();
					items.add(invItem);
					invoice.setItems(items);
					invoice.setInvtype(MasterGSTConstants.B2B);
					CompanyBankDetails bankdetails=  companyBankDetailsRepository.findByClientidAndAccountnumber(mastergstClientid,bankAccountNumber);
					if(NullUtil.isNotEmpty(bankdetails)) {
						CompanyBankDetails bankDetails = new CompanyBankDetails();
						bankDetails.setAccountName(bankdetails.getAccountName());
						bankDetails.setAccountnumber(bankdetails.getAccountnumber());
						bankDetails.setBankname(bankdetails.getBankname());
						bankDetails.setIfsccode(bankdetails.getIfsccode());
						bankDetails.setBranchname(bankdetails.getBranchname());
						invoice.setBankDetails(bankDetails);
					}
					InvoiceParent invoiceForJournal;
					try {
						invoiceForJournal = clientService.saveSalesInvoice(invoice,null, isIntraState);
						clientService.saveJournalInvoice(invoiceForJournal,mastergstClientid,MasterGSTConstants.GSTR1,isIntraState);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					PaymentLink paymentLink = paymentLinkRepository.findByUserid(userid);
					if(isNotEmpty(paymentLink)) {
						paymentLinkRepository.delete(paymentLink);
					}
			} else if(responseMap.containsKey("failure_message")) {
				paymentDetails.setFailureMessage(responseMap.get("failure_message"));
			}
		}
		paymentDetailsRepository.save(paymentDetails);
		logger.debug(CLASSNAME + "processPaymentResponse : End");
		return responseMap;
	}
}
