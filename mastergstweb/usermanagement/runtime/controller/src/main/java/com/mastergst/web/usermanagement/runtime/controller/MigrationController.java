package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;
import static com.mastergst.core.util.NullUtil.isEmpty;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.util.Lists;
import com.ibm.icu.text.SimpleDateFormat;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.Metering;
import com.mastergst.usermanagement.runtime.domain.PartnerPayments;
import com.mastergst.usermanagement.runtime.domain.PaymentDetails;
import com.mastergst.usermanagement.runtime.domain.PaymentItems;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.SubscriptionDetails;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.repository.MeteringRepository;
import com.mastergst.usermanagement.runtime.repository.SubscriptionDetailsRepository;
import com.mastergst.usermanagement.runtime.service.SubscriptionService;
import com.mongodb.WriteResult;

@RestController
public class MigrationController {

	private static final Logger logger = LogManager.getLogger(MigrationController.class.getName());
	private static final String CLASSNAME = "MigrationController::";

	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private MeteringRepository meteringRepository;
	@Autowired
	private SubscriptionService subscriptionService;
	@Autowired
	private SubscriptionDetailsRepository subscriptionDetailsRepository;
	@Autowired
	private GroupDetailsRepository groupDetailsRepository;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	
	@Autowired
	private LedgerRepository ledgerRepository;

	private Map<String, PartnerPayments> partnerPaymentsMap = new HashMap<String, PartnerPayments>();

	@CrossOrigin
	@GetMapping("/mirageReceiptsPayments")
	public String mirgateReceiptsAndPayment() {

		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("paymentitems");
		query.fields().include("paymentDate");
		query.fields().include("createdDate");
		query.fields().include("updatedDate");
		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<Payments> payments = mongoTemplate.find(query, Payments.class);

			if (NullUtil.isEmpty(payments)) {
				break;
			}
			System.out.println(payments.size());
			if (NullUtil.isNotEmpty(payments)) {
				for (Payments payment : payments) {

					Query queryTmp = Query.query(Criteria.where("_id").is(payment.getId()));
					Update update = new Update();
					int month = -1, year = -1;

					Date dt = null;
					if (NullUtil.isNotEmpty(payment.getPaymentDate())) {
						try {
							dt = sdf.parse(payment.getPaymentDate());
						} catch (ParseException e) {
							if (NullUtil.isNotEmpty(payment.getCreatedDate())) {
								dt = (Date) payment.getCreatedDate();
							} else if (NullUtil.isNotEmpty(payment.getUpdatedDate())) {
								dt = (Date) payment.getUpdatedDate();
							}
						}
					} else if (NullUtil.isNotEmpty(payment.getCreatedDate())) {
						dt = (Date) payment.getCreatedDate();
					} else if (NullUtil.isNotEmpty(payment.getUpdatedDate())) {
						dt = (Date) payment.getUpdatedDate();
					}

					if (dt != null) {
						month = dt.getMonth();
						year = dt.getYear() + 1900;

						Calendar cal = Calendar.getInstance();
						cal.setTime(dt);
					}

					int quarter = month / 3;
					quarter = quarter == 0 ? 4 : quarter;
					String yearCode = quarter == 4 ? (year - 1) + "-" + year : (year) + "-" + (year + 1);
					month++;
					update.set("mthCd", "" + month);
					update.set("yrCd", "" + yearCode);

					Double paidAmount = 0d;
					Double cashAmount = 0d;
					Double bankAmount = 0d;
					Double tdsItAmount = 0d;
					Double tdsGstAmount = 0d;
					Double discountAmount = 0d;
					Double othersAmount = 0d;

					if (NullUtil.isNotEmpty(payment.getPaymentitems())) {
						for (PaymentItems paymentDetails : payment.getPaymentitems()) {
							paidAmount += paymentDetails.getAmount();
							if ("Cash".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								cashAmount += paymentDetails.getAmount();
							} else if ("Bank".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								bankAmount += paymentDetails.getAmount();
							} else if ("Discount".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								discountAmount += paymentDetails.getAmount();
							} else if ("TDS-IT".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								tdsItAmount += paymentDetails.getAmount();
							} else if ("TDS-GST".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								tdsGstAmount += paymentDetails.getAmount();
							} else if ("Others".equalsIgnoreCase(paymentDetails.getModeOfPayment())) {
								othersAmount += paymentDetails.getAmount();
							}
						}
					}

					update.set("paidAmount", paidAmount);
					update.set("cashAmount", cashAmount);
					update.set("bankAmount", bankAmount);
					update.set("tdsItAmount", tdsItAmount);
					update.set("tdsGstAmount", tdsGstAmount);
					update.set("discountAmount", discountAmount);
					update.set("othersAmount", othersAmount);

					WriteResult result = mongoTemplate.updateFirst(queryTmp, update, Payments.class);
					System.out.println(result.getN() + "doc affected ---- " + payment.getId());
				}
			}
		}
		return "{\"status\":\"success\"}";
	}

	@CrossOrigin
	@GetMapping("/mirageEinvoiceSubscription")
	public String mirgateUsersubscriptionEinvoice() {

		Query query = new Query();
		query.fields().include("_id");
		Criteria criteria = Criteria.where("type").is("aspdeveloper");
		query.addCriteria(criteria);
		int length = 1000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<User> users = mongoTemplate.find(query, User.class);

			if (NullUtil.isEmpty(users)) {
				break;
			}
			System.out.println(users.size());
			if (NullUtil.isNotEmpty(users)) {
				for (User user : users) {
					List<Metering> einvoicemetering = meteringRepository.findByUseridAndStage(user.getId().toString(),
							"EInvoiceProduction");
					SubscriptionDetails subscriptionDetailsEinv = subscriptionService
							.getSubscriptionData(user.getId().toString(), "E-INVOICEAPI");
					if (NullUtil.isNotEmpty(einvoicemetering) && NullUtil.isNotEmpty(subscriptionDetailsEinv)) {
						subscriptionDetailsEinv.setProcessedInvoices(einvoicemetering.size());
						subscriptionDetailsRepository.save(subscriptionDetailsEinv);
					}
				}
			}
		}
		return "{\"status\":\"success\"}";
	}

	@CrossOrigin
	@GetMapping("/mirageEinvoiceSandboxSubscription")
	public String mirgateUsersubscriptionEinvoiceSandbox() {

		Query query = new Query();
		query.fields().include("_id");
		Criteria criteria = Criteria.where("type").is("aspdeveloper");
		query.addCriteria(criteria);
		int length = 1000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<User> users = mongoTemplate.find(query, User.class);

			if (NullUtil.isEmpty(users)) {
				break;
			}
			System.out.println(users.size());
			if (NullUtil.isNotEmpty(users)) {
				for (User user : users) {
					List<Metering> einvoicemetering = meteringRepository.findByUseridAndStage(user.getId().toString(),
							"EInvoiceSandBox");
					SubscriptionDetails subscriptionDetailsEinv = subscriptionService
							.getSubscriptionData(user.getId().toString(), "E-INVOICESANDBOXAPI");
					if (NullUtil.isNotEmpty(einvoicemetering) && NullUtil.isNotEmpty(subscriptionDetailsEinv)) {
						subscriptionDetailsEinv.setProcessedSandboxInvoices(einvoicemetering.size());
						subscriptionDetailsRepository.save(subscriptionDetailsEinv);
					}
				}
			}
		}
		return "{\"status\":\"success\"}";
	}

	@CrossOrigin(origins = "*")
	@RequestMapping("/updateSubscriptionDetails")
	public @ResponseBody String subscriptionDetailsUpdate() {

		final String method = "subscriptionDetailsUpdate::";
		logger.debug(CLASSNAME + method + BEGIN);

		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("createdDate");
		query.fields().include("updatedDate");
		query.fields().include("userid");

		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<SubscriptionDetails> invoices = mongoTemplate.find(query, SubscriptionDetails.class);
			if (NullUtil.isEmpty(invoices)) {
				break;
			}
			System.out.println(invoices.size());
			for (SubscriptionDetails invoice : invoices) {
				Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
				Update update = new Update();

				int month = -1, year = -1;
				Date dt = null;

				if (NullUtil.isNotEmpty(invoice.getCreatedDate())) {
					dt = (Date) invoice.getCreatedDate();
				} else if (NullUtil.isNotEmpty(invoice.getUpdatedDate())) {
					dt = (Date) invoice.getUpdatedDate();
				}

				if (dt != null) {
					month = dt.getMonth();
					year = dt.getYear() + 1900;
					Calendar cal = Calendar.getInstance();
					cal.setTime(dt);
				}

				int quarter = month / 3;
				quarter = quarter == 0 ? 4 : quarter;
				String yearCode = quarter == 4 ? (year - 1) + "-" + year : (year) + "-" + (year + 1);
				month++;

				update.set("mthCd", "" + month);
				update.set("qrtCd", "" + quarter);
				update.set("yrCd", "" + yearCode);
				update.set("userDocid", new ObjectId(invoice.getUserid()));

				WriteResult result = mongoTemplate.updateFirst(queryTmp, update, SubscriptionDetails.class);
				System.out.println(result.getN() + "doc affected ---- " + invoice.getId());
				// break;
			}
		}

		logger.debug(CLASSNAME + method + END);
		return "Users Subscription data update successfully...!";
	}

	@CrossOrigin
	@GetMapping("/mirgatetcstdspayable")
	public String mirgateTcsTdsPayable() {
		Query query = new Query();
		Criteria criteria = Criteria.where("status").exists(false);
		
		List<String> ledgername = Lists.newArrayList();
		ledgername.add(AccountConstants.TDS_PAYABLE);
		ledgername.add(AccountConstants.TCS_PAYABLE);
		
		List<Criteria> criterias = new ArrayList<Criteria>();
		criterias.add(new Criteria().orOperator(
				Criteria.where("crEntrie").elemMatch(Criteria.where("name").in(ledgername)),
				Criteria.where("drEntrie").elemMatch(Criteria.where("name").in(ledgername))
			));
		criteria = new Criteria().andOperator(criteria,new Criteria().orOperator(criterias.toArray(new Criteria[criterias.size()])));
		query.fields().include("_id");
		query.fields().include("invoiceId");
		query.fields().include("returnType");
		query.fields().include("crEntrie");
		query.fields().include("drEntrie");
		query.addCriteria(criteria);
		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<AccountingJournal> journals = mongoTemplate.find(query, AccountingJournal.class);

			if (NullUtil.isEmpty(journals)) {
				break;
			}
			System.out.println(journals.size());
			if (NullUtil.isNotEmpty(journals)) {
				for (AccountingJournal journal : journals) {
					double totalamount = 0.0;
					if(NullUtil.isNotEmpty(journal) && NullUtil.isNotEmpty(journal.getReturnType()) && NullUtil.isNotEmpty(journal.getInvoiceId())) {
						if(journal.getReturnType().equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							GSTR1 gstr1 = mongoTemplate.findById(new ObjectId(journal.getInvoiceId()), GSTR1.class);
							if(NullUtil.isNotEmpty(gstr1) && NullUtil.isNotEmpty(gstr1.getTotalamount())) {
								totalamount = gstr1.getTotalamount();
							}
						}else if(journal.getReturnType().equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							PurchaseRegister purchaseregister = mongoTemplate.findById(new ObjectId(journal.getInvoiceId()), PurchaseRegister.class);
							if(NullUtil.isNotEmpty(purchaseregister) && NullUtil.isNotEmpty(purchaseregister.getTotalamount())) {
								totalamount = purchaseregister.getTotalamount();
							}
						}
					}
					Query queryTmp = Query.query(Criteria.where("_id").is(journal.getId()));
					Update update = new Update();
					double totaltcspayable = 0.0;
					double totaltdspayable = 0.0;
					if(NullUtil.isNotEmpty(journal) && NullUtil.isNotEmpty(journal.getCrEntrie())) {
						for(JournalEntrie creditentrie : journal.getCrEntrie()) {
							if(creditentrie.getName().equalsIgnoreCase(AccountConstants.TCS_PAYABLE)) {
								totaltcspayable += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
							}
							if(creditentrie.getName().equalsIgnoreCase(AccountConstants.TDS_PAYABLE)) {
								totaltdspayable += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
							}
						}
					}
					if(NullUtil.isNotEmpty(journal) && NullUtil.isNotEmpty(journal.getDrEntrie())) {
						for(JournalEntrie debitentrie : journal.getDrEntrie()) {
							if(debitentrie.getName().equalsIgnoreCase(AccountConstants.TCS_PAYABLE)) {
								totaltcspayable += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
							}
							if(debitentrie.getName().equalsIgnoreCase(AccountConstants.TDS_PAYABLE)) {
								totaltdspayable += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
							}
						}
					}
					update.set("tcs_payable", totaltcspayable);
					update.set("tds_payable", totaltdspayable);
					update.set("invoiceamount",totalamount);
					WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
					System.out.println(result.getN() + "doc affected ---- " + journal.getId());
				}
			}
		}
		return "{\"status\":\"success\"}";
	}
	
	@CrossOrigin
	@GetMapping("/mirgatejournals")
	public String mirgatejournals() {
		Query query = new Query();
		List<String> returntype = Lists.newArrayList();
		returntype.add(MasterGSTConstants.GSTR1);
		returntype.add(MasterGSTConstants.GSTR2);
		Criteria criteria = Criteria.where("status").exists(false).and("returnType").in(returntype);
		query.fields().include("_id");
		query.fields().include("invoiceId");
		query.fields().include("returnType");
		query.addCriteria(criteria);
		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<AccountingJournal> journals = mongoTemplate.find(query, AccountingJournal.class);

			if (NullUtil.isEmpty(journals)) {
				break;
			}
			System.out.println(journals.size());
			if (NullUtil.isNotEmpty(journals)) {
				for (AccountingJournal journal : journals) {
					Query query1 = Query.query(Criteria.where("_id").is(new ObjectId(journal.getInvoiceId())));
					// query.fields().include("_id");
					// query.fields().include("partnerEmail");
					List<User> user = mongoTemplate.find(query, User.class);
					
					
					Query queryTmp = Query.query(Criteria.where("_id").is(journal.getId()));
					Update update = new Update();
					double totaltcspayable = 0.0;
					double totaltdspayable = 0.0;
					
					for(JournalEntrie creditentrie : journal.getCrEntrie()) {
						if(creditentrie.getName().equalsIgnoreCase(AccountConstants.TCS_PAYABLE)) {
							totaltcspayable += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
						}
						if(creditentrie.getName().equalsIgnoreCase(AccountConstants.TDS_PAYABLE)) {
							totaltdspayable += creditentrie.getValue() == null ? 0.0 : creditentrie.getValue();
						}
					}
					for(JournalEntrie debitentrie : journal.getDrEntrie()) {
						if(debitentrie.getName().equalsIgnoreCase(AccountConstants.TCS_PAYABLE)) {
							totaltcspayable += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
						}
						if(debitentrie.getName().equalsIgnoreCase(AccountConstants.TDS_PAYABLE)) {
							totaltdspayable += debitentrie.getValue() == null ? 0.0 : debitentrie.getValue();
						}
					}
					update.set("tcs_payable", totaltcspayable);
					update.set("tds_payable", totaltdspayable);

					WriteResult result = mongoTemplate.updateFirst(queryTmp, update, AccountingJournal.class);
					System.out.println(result.getN() + "doc affected ---- " + journal.getId());
				}
			}
		}
		return "{\"status\":\"success\"}";
	}

	public void updatePartnerPayments() {

		Collection<PartnerPayments> payments = partnerPaymentsMap.values();
		if (payments.size() > 0) {
			for (PartnerPayments payment : payments) {
				mongoTemplate.save(payment, "partner_payments");

			}
		}
	}

	public void findPartnerPayments(String userid, Double amount, int month, String yearCode, int quarter) {
		Query query = Query.query(Criteria.where("_id").is(new ObjectId(userid)));
		// query.fields().include("_id");
		// query.fields().include("partnerEmail");
		List<User> user = mongoTemplate.find(query, User.class);
		if (NullUtil.isEmpty(user)) {
			System.out.println("user empty...");
		}

		if (user.size() == 1 && isNotEmpty(user.get(0)) && isNotEmpty(user.get(0).getPartnerEmail())) {
			Query qryTmp = Query.query(Criteria.where("email").is(user.get(0).getPartnerEmail()));

			List<User> partnerUser = mongoTemplate.find(qryTmp, User.class);

			if (partnerUser.size() == 1 && isNotEmpty(partnerUser.get(0))
					&& isNotEmpty(partnerUser.get(0).getId().toString())) {

				Double subscriptionamts = 0d;
				Double percentage = 0d;

				String key = partnerUser.get(0).getId().toString() + "_" + month + "_" + yearCode;

				PartnerPayments partnerPayment = partnerPaymentsMap.get(key);
				if (NullUtil.isEmpty(partnerPayment)) {

					partnerPayment = new PartnerPayments();
					partnerPayment.setUserid(partnerUser.get(0).getId().toString());
					partnerPayment.setFullname(partnerUser.get(0).getFullname());
					partnerPayment.setPaidamount(0d);
					subscriptionamts += amount;
					partnerPayment.setPartnerPayment(MasterGSTConstants.PENDING);
					partnerPayment.setMthCd(month + "");
					partnerPayment.setYrCd(yearCode);
					partnerPayment.setQrtCd(quarter + "");
					if (isNotEmpty(partnerUser.get(0).getPartnerPercentage())) {
						percentage = Double.parseDouble(partnerUser.get(0).getPartnerPercentage());
					} else {
						percentage = 25d;
					}
					partnerPayment.setPercentage(percentage);
				} else {
					subscriptionamts += partnerPayment.getSubscriptionamount() + amount;
					percentage = partnerPayment.getPercentage();
				}
				partnerPayment.setSubscriptionamount(subscriptionamts);

				Double partneramt = (subscriptionamts * percentage) / 100;
				partnerPayment.setPartneramt(partneramt);

				partnerPaymentsMap.put(key, partnerPayment);
			}
		}
	}

	@CrossOrigin
	@GetMapping("/migratenewledgers")
	public String defaultLedgersMigrate() {

		
		final String method = "defaultLedgersMigrate::";
		logger.debug(CLASSNAME + method + BEGIN);
		Query query = new Query();
		Criteria criteria = Criteria.where("isCommonLedgerExit").exists(true);
		query.addCriteria(criteria);
		List<String> ledgerNames = Lists.newArrayList();
		ledgerNames.add("Discount Allowed");
		ledgerNames.add("Discount Received");
		ledgerNames.add("Discount");
		ledgerNames.add("HO-ISD");
		ledgerNames.add("Input ITC Reversal");
		ledgerNames.add("Input Ineligible ISD");
		
		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			
			List<Client> clients = mongoTemplate.find(query, Client.class);
			
			if (NullUtil.isEmpty(clients)) {
				break;
			}

			if (NullUtil.isNotEmpty(clients)) {
				for(Client client : clients) {
					
					System.out.println("clientid ::"+client.getId().toString());
					List<ProfileLedger> ledgers = ledgerRepository.findByClientidAndLedgerNameIn(client.getId().toString(), ledgerNames);
					
					if(isNotEmpty(ledgers)) {
						System.out.println("duplicate ledger docs deleted ::" + client.getId().toString());
						ledgerRepository.delete(ledgers);
					}
					List<ProfileLedger> profileLst = Lists.newArrayList(); 
						
					ProfileLedger ledger1 = new ProfileLedger();
					ledger1.setClientid(client.getId().toString());
					ledger1.setLedgerName("Discount Allowed");
					ledger1.setGrpsubgrpName("Discount Allowed");
					ledger1.setLedgerpath("Expenses/Indirect expenses/Other expenses");
					
					ProfileLedger ledger2 = new ProfileLedger();
					ledger2.setClientid(client.getId().toString());
					ledger2.setLedgerName("Discount Received");
					ledger2.setGrpsubgrpName("Discount Received");
					ledger2.setLedgerpath("Income/Indirect Income/Other Income");
					
					ProfileLedger ledger3 = new ProfileLedger();
					ledger3.setClientid(client.getId().toString());
					ledger3.setLedgerName("HO-ISD");
					ledger3.setGrpsubgrpName("Sundry Creditors");
					ledger3.setLedgerpath("Assets/Current Assets/Sundry Creditors");
					
					ProfileLedger ledger4 = new ProfileLedger();
					ledger4.setClientid(client.getId().toString());
					ledger4.setLedgerName("Input ITC Reversal");
					ledger4.setGrpsubgrpName("Purchases");
					ledger4.setLedgerpath("Expenses/Direct expenses/Purchases");
					
					ProfileLedger ledger5 = new ProfileLedger();
					ledger5.setClientid(client.getId().toString());
					ledger5.setLedgerName("Input Ineligible ISD");
					ledger5.setGrpsubgrpName("Purchases");
					ledger5.setLedgerpath("Expenses/Direct expenses/Purchases");
					
					profileLst.add(ledger1);
					profileLst.add(ledger2);
					profileLst.add(ledger3);
					profileLst.add(ledger4);
					profileLst.add(ledger5);
					System.out.println("doc affected ---- " + client.getId().toString());
					ledgerRepository.save(profileLst);
				}
			}
		}
		
		Query gquery = new Query();
		Criteria gcriteria = Criteria.where("groupname").is("Other expenses");
		
		gquery.addCriteria(gcriteria);
		
		List<GroupDetails> groupdetails = mongoTemplate.find(gquery, GroupDetails.class);
		
		groupdetails.stream().forEach(ele-> {
			ele.setPath("Expenses/Indirect expenses/Other expenses");
		});
		
		groupDetailsRepository.save(groupdetails);
		
		
		
		logger.debug(CLASSNAME + method + END);
		return "{\"status\":\"success\"}";
	}
	
	@CrossOrigin(origins = "*")
	@RequestMapping("/updaterecordpayments")
	public @ResponseBody String recordpaymentUpdate() {

		final String method = "recordpaymentUpdate::";
		logger.debug(CLASSNAME + method + BEGIN);

		Query query = new Query();
		query.fields().include("_id");
		query.fields().include("invoiceid");

		int length = 5000;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			Pageable pageable = new PageRequest(i, length);
			query.with(pageable);
			List<Payments> invoices = mongoTemplate.find(query, Payments.class);
			if (NullUtil.isEmpty(invoices)) {
				break;
			}
			System.out.println(invoices.size());
			for (Payments invoice : invoices) {
				if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvoiceid())) {
					Query queryTmp = Query.query(Criteria.where("_id").is(invoice.getId()));
					Update update = new Update();
					update.set("invoiceDocId", new ObjectId(invoice.getInvoiceid()));
					WriteResult result = mongoTemplate.updateFirst(queryTmp, update, Payments.class);
					System.out.println(result.getN() + "doc affected ---- " + invoice.getId());
				}
				// break;
			}
		}

		logger.debug(CLASSNAME + method + END);
		return "Payment data update successfully...!";
	}
}
