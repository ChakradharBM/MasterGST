package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.common.MasterGSTConstants.GSTR1;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.google.api.client.util.Maps;
import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.accounting.dao.AccountingDao;
import com.mastergst.usermanagement.runtime.accounting.dao.AccountingInvoicePaymentDao;
import com.mastergst.usermanagement.runtime.accounting.domain.TotalJournalAmount;
import com.mastergst.usermanagement.runtime.accounting.domain.TotalPendingAmount;
import com.mastergst.usermanagement.runtime.accounting.service.AccountingSupportService;
import com.mastergst.usermanagement.runtime.accounting.util.AccountingDataUtils;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.InvoicePayments;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.domain.Payments;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.Subgroups;
import com.mastergst.usermanagement.runtime.domain.TotalInvoiceAmount;
import com.mastergst.usermanagement.runtime.repository.AccountingHeadsRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
public class AccountingReportsServiceImpl implements AccountingReportsService {
	private static final Logger logger = LogManager.getLogger(AccountingReportsServiceImpl.class.getName());
	private static final String CLASSNAME = "AccountingReportsServiceImpl::";

	@Autowired
	private GroupDetailsRepository groupDetailsRepository;
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private AccountingInvoicePaymentDao accountingInvoicePaymentDao;
	
	@Autowired
	AccountingHeadsRepository accountingHeadsRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	AccountingDataUtils accountingDataUtils;
	@Autowired
	AccountingSupportService accountingSupportService;
	@Autowired
	AccountingDao accountingDao;

	Map<String, Amounts> accJournalsMap = new HashMap<String, Amounts>();
	
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");

	@Override
	public List<GroupDetails> getClientGroupdetails(String clientid) {
		final String method = "getClientGroupdetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);

		return groupDetailsRepository.findByClientid(clientid);
	}

	@Override
	public List<ProfileLedger> getLedgersByClientid(String clientid) {
		final String method = "getLedgersByClientid::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);

		return ledgerRepository.findByClientid(clientid);
	}

	@Override
	public Map<String, Node> getMonthlyTrailBalance(String clientid, int month, int year) {
		final String method = "getMonthlyTrailBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
	
		Client client= clientService.findById(clientid);
		SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
		String invoiceMonth=null;
		if(isNotEmpty(client.getJournalEnteringDate())) {
			String[] fpArr= sdf.format(client.getJournalEnteringDate()).split("-");
			invoiceMonth=fpArr[1]+fpArr[2];
		}else {
			String[] fpArr= sdf.format(client.getCreatedDate()).split("-");
			invoiceMonth=fpArr[1]+fpArr[2];
		}
		String strMonths = month < 10 ? "0" + month : month + "";
		String monthYear = strMonths+year;
		boolean flag=false;
		if(invoiceMonth.equalsIgnoreCase(monthYear)) {
			flag=true;
		}
		
		Map<String, List<Node>> groupMap = new HashMap<String, List<Node>>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		accJournalsMap = accountingDataUtils.openingAndClosingBalanceAdjustment(clientid, month, year);
		
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		Map<String,Double> ledgerBooksOpeningMap=new HashMap<String,Double>();
		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				
				if(flag) {
					if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type=ledger.getLedgerOpeningBalanceType();
						if(AccountConstants.DR.equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
						}else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
						}
					}
				}
				
				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if(amounts != null) {
					ledger.setAmounts(amounts);
					//ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
			}
		}
		
		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {
			for (GroupDetails group : groupsLst) {
				List<Subgroups> subgrpLst=new ArrayList<Subgroups>();
				Amounts grpamts=new Amounts(0d,0d,0d,0d);
				if(isNotEmpty(group.getSubgroups())) {
					for (Subgroups subGroup : group.getSubgroups()) {
						Amounts subgrpamts=new Amounts(0d,0d,0d,0d);
						if(ledgerMap.containsKey(subGroup.getGroupname())){
							
							List<ProfileLedger> ledgrLst=ledgerMap.get(subGroup.getGroupname());
							if(isNotEmpty(ledgrLst)) {
								for(ProfileLedger ledgr:ledgrLst) {
									Subgroups subgrpCreate=new Subgroups();
									subgrpCreate.setId(ledgr.getId());
									subgrpCreate.setGroupname(ledgr.getLedgerName());
									subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
									subgrpCreate.setPath(ledgr.getLedgerpath());
									subgrpCreate.setAmounts(ledgr.getAmounts());
									
									subgrpLst.add(subgrpCreate);
								}
								ledgerMap.remove(subGroup.getGroupname());
							}
						}
						subGroup.setAmounts(subgrpamts);
						subgrpLst.add(subGroup);
					}
				}
				
				if(ledgerMap.containsKey(group.getGroupname())){
					
					List<ProfileLedger> ledgrLst=ledgerMap.get(group.getGroupname());
					if(isNotEmpty(ledgrLst)) {
						
						for(ProfileLedger ledgr:ledgrLst) {
							Subgroups subgrpCreate=new Subgroups();
							subgrpCreate.setId(ledgr.getId());
							subgrpCreate.setGroupname(ledgr.getLedgerName());
							subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
							subgrpCreate.setPath(ledgr.getLedgerpath());
							subgrpCreate.setAmounts(ledgr.getAmounts());
							
							subgrpLst.add(subgrpCreate);
						}
						ledgerMap.remove(group.getGroupname());
					}
				}
				
				group.setAmounts(grpamts);				
				if(isNotEmpty(subgrpLst)) {
					group.setSubgroups(subgrpLst);
				}
				groupsAndLedgerLst.add(group);
			}
		}
		
		if(isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey,ldgrValue)->{
				if(isNotEmpty(ldgrValue)) {
					for(ProfileLedger ledgr:ldgrValue) {
						GroupDetails group=new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}
		
		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getGroupname());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmounts(node);
				updateGroupConsolidatedAmounts(node);
				List<Node> nodes = groupMap.get(details.getHeadname());
				if(nodes == null) {
					nodes = new ArrayList<Node>();
				}
				nodes.add(node);
				groupMap.put(details.getHeadname(), nodes);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return accountingSupportService.createHeads(groupMap);
		//return groupMap;
	}
	
	

	@Override
	public Map<String, Node> getYearlyTrailBalance(String clientid, int year) {
		final String method = "getYearlyTrailBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Map<String, List<Node>> groupMap = new HashMap<String, List<Node>>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);

		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String invoiceMonth = null;
		if (isNotEmpty(client.getJournalEnteringDate())) {
			String[] fpArr = sdf.format(client.getJournalEnteringDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		} else {
			String[] fpArr = sdf.format(client.getCreatedDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		}

		int nextYear = year + 1;
		List<String> invoice_Month = Arrays.asList("04" + year, "05" + year, "06" + year, "07" + year, "08" + year,
				"09" + year, "10" + year, "11" + year, "12" + year, "01" + (nextYear), "02" + (nextYear),
				"03" + (nextYear));

		boolean flag = false;
		if (invoice_Month.contains(invoiceMonth)) {
			flag = true;
		}

		accJournalsMap = accountingDataUtils.openingAndClosingBalanceAdjustment(clientid, 0, year);

		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();
		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				if (flag) {
					if (isNotEmpty(ledger.getLedgerOpeningBalanceType())
							&& isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type = ledger.getLedgerOpeningBalanceType();
						if ("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), ledger.getLedgerOpeningBalance());
						} else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), -ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if (amounts != null) {

					ledger.setAmounts(amounts);
					// ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
			}
		}
		/*
		 * if(isNotEmpty(ledgerBooksOpeningMap) &&
		 * isNotEmpty(ledgerBooksOpeningMap.keySet())) { for(String
		 * ldgrName:ledgerBooksOpeningMap.keySet()) {
		 * if(accJournalsMap.containsKey(ldgrName)) { Amounts
		 * amts=accJournalsMap.get(ldgrName); double
		 * booksAmts=ledgerBooksOpeningMap.get(ldgrName);
		 * 
		 * double openingamt=0,debitamt=0,creditamt=0,closingamt=0;
		 * 
		 * openingamt+=amts.getOpeningamt()+booksAmts; debitamt+=amts.getDebitamt();
		 * creditamt+=amts.getCreditamt(); closingamt=openingamt+debitamt-creditamt;
		 * 
		 * amts.setOpeningamt(openingamt); amts.setClosingamt(closingamt);
		 * accJournalsMap.put(ldgrName,amts); }else { double
		 * booksAmts=ledgerBooksOpeningMap.get(ldgrName);
		 * accJournalsMap.put(ldgrName,new Amounts(booksAmts,0,0,booksAmts)); } } }
		 */
		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {
			for (GroupDetails group : groupsLst) {
				List<Subgroups> subgrpLst = new ArrayList<Subgroups>();
				Amounts grpamts = new Amounts(0d, 0d, 0d, 0d);
				if (isNotEmpty(group.getSubgroups())) {
					for (Subgroups subGroup : group.getSubgroups()) {
						Amounts subgrpamts = new Amounts(0d, 0d, 0d, 0d);
						if (ledgerMap.containsKey(subGroup.getGroupname())) {

							List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
							if (isNotEmpty(ledgrLst)) {
								for (ProfileLedger ledgr : ledgrLst) {
									Subgroups subgrpCreate = new Subgroups();
									subgrpCreate.setId(ledgr.getId());
									subgrpCreate.setGroupname(ledgr.getLedgerName());
									subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
									subgrpCreate.setPath(ledgr.getLedgerpath());
									subgrpCreate.setAmounts(ledgr.getAmounts());

									subgrpLst.add(subgrpCreate);
								}
								ledgerMap.remove(subGroup.getGroupname());
							}
						}
						subGroup.setAmounts(subgrpamts);
						subgrpLst.add(subGroup);
					}
				}

				if (ledgerMap.containsKey(group.getGroupname())) {

					List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
					if (isNotEmpty(ledgrLst)) {

						for (ProfileLedger ledgr : ledgrLst) {
							Subgroups subgrpCreate = new Subgroups();
							subgrpCreate.setId(ledgr.getId());
							subgrpCreate.setGroupname(ledgr.getLedgerName());
							subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
							subgrpCreate.setPath(ledgr.getLedgerpath());
							subgrpCreate.setAmounts(ledgr.getAmounts());

							subgrpLst.add(subgrpCreate);
						}
						ledgerMap.remove(group.getGroupname());
					}
				}

				group.setAmounts(grpamts);
				if (isNotEmpty(subgrpLst)) {
					group.setSubgroups(subgrpLst);
				}
				groupsAndLedgerLst.add(group);
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {

						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getGroupname());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmounts(node);
				updateGroupConsolidatedAmounts(node);
				List<Node> nodes = groupMap.get(details.getHeadname());
				if(nodes == null) {
					nodes = new ArrayList<Node>();
				}
				nodes.add(node);
				groupMap.put(details.getHeadname(), nodes);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return accountingSupportService.createHeads(groupMap);
		//return groupMap;
	}

	@Override
	public Map<String, Node> getCustomTrailBalance(String clientid, String fromtime, String totime) {
		final String method = "getCustomTrailBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "totime\t" + totime);
		logger.debug(CLASSNAME + method + "fromtime\t" + fromtime);

		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String invoiceMonth = null;
		if (isNotEmpty(client.getJournalEnteringDate())) {
			String[] fpArr = sdf.format(client.getJournalEnteringDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		} else {
			String[] fpArr = sdf.format(client.getCreatedDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		}

		String[] fromtimeArr = fromtime.split("-");
		String[] totimeArr = totime.split("-");

		String strMonthsFrom = Integer.parseInt(fromtimeArr[1]) < 10 ? "0" + Integer.parseInt(fromtimeArr[1])
				: Integer.parseInt(fromtimeArr[1]) + "";
		String strMonthsTo = Integer.parseInt(totimeArr[1]) < 10 ? "0" + Integer.parseInt(totimeArr[1])
				: Integer.parseInt(totimeArr[1]) + "";
		String fromMonthYear = strMonthsFrom + fromtimeArr[2];
		String toMonthYear = strMonthsTo + totimeArr[2];
		boolean flag = false;
		if (invoiceMonth.equalsIgnoreCase(fromMonthYear) || invoiceMonth.equalsIgnoreCase(toMonthYear)) {
			flag = true;
		}
		Map<String, List<Node>> groupMap = new HashMap<String, List<Node>>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);

		accJournalsMap = accountingDataUtils.customOpeningAndClosingBalanceAdjustment(clientid, fromtime, totime);

		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();

		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				if (flag) {
					if (isNotEmpty(ledger.getLedgerOpeningBalanceType())
							&& isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type = ledger.getLedgerOpeningBalanceType();
						if ("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), ledger.getLedgerOpeningBalance());
						} else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), -ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if(amounts != null) {
					//ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
					ledger.setAmounts(amounts);
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
				
			}
		}

		if (isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			for (String ldgrName : ledgerBooksOpeningMap.keySet()) {
				if (accJournalsMap.containsKey(ldgrName)) {
					Amounts amts = accJournalsMap.get(ldgrName);
					double booksAmts = amts.getOpeningamt() + ledgerBooksOpeningMap.get(ldgrName);
					amts.setOpeningamt(booksAmts);
					accJournalsMap.put(ldgrName, amts);
				} else {
					accJournalsMap.put(ldgrName, new Amounts(ledgerBooksOpeningMap.get(ldgrName), 0, 0, 0));
				}
			}
		}
		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {
			for (GroupDetails group : groupsLst) {
				List<Subgroups> subgrpLst = new ArrayList<Subgroups>();
				Amounts grpamts = new Amounts(0d, 0d, 0d, 0d);
				if (isNotEmpty(group.getSubgroups())) {
					for (Subgroups subGroup : group.getSubgroups()) {
						Amounts subgrpamts = new Amounts(0d, 0d, 0d, 0d);
						if (ledgerMap.containsKey(subGroup.getGroupname())) {

							List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
							if (isNotEmpty(ledgrLst)) {
								for (ProfileLedger ledgr : ledgrLst) {
									Subgroups subgrpCreate = new Subgroups();
									subgrpCreate.setId(ledgr.getId());
									subgrpCreate.setGroupname(ledgr.getLedgerName());
									subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
									subgrpCreate.setPath(ledgr.getLedgerpath());
									subgrpCreate.setAmounts(ledgr.getAmounts());

									subgrpLst.add(subgrpCreate);
								}
								ledgerMap.remove(subGroup.getGroupname());
							}
						}
						subGroup.setAmounts(subgrpamts);
						subgrpLst.add(subGroup);
					}
				}

				if (ledgerMap.containsKey(group.getGroupname())) {
					List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
					if (isNotEmpty(ledgrLst)) {

						for (ProfileLedger ledgr : ledgrLst) {
							Subgroups subgrpCreate = new Subgroups();
							subgrpCreate.setId(ledgr.getId());
							subgrpCreate.setGroupname(ledgr.getLedgerName());
							subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
							subgrpCreate.setPath(ledgr.getLedgerpath());
							subgrpCreate.setAmounts(ledgr.getAmounts());

							subgrpLst.add(subgrpCreate);
						}
						ledgerMap.remove(group.getGroupname());
					}
				}
				group.setAmounts(grpamts);
				if (isNotEmpty(subgrpLst)) {
					group.setSubgroups(subgrpLst);
				}
				groupsAndLedgerLst.add(group);
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {

						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getGroupname());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmounts(node);
				updateGroupConsolidatedAmounts(node);
				List<Node> nodes = groupMap.get(details.getHeadname());
				if(nodes == null) {
					nodes = new ArrayList<Node>();
				}
				nodes.add(node);
				groupMap.put(details.getHeadname(), nodes);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return accountingSupportService.createHeads(groupMap);
		//return groupMap;
	}

	@Override
	public Map<String, Node> getMonthlyPandLReport(String clientid, int month, int year) {
		final String method = "getMonthlyPandLReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		List<AccountingJournal> monthlyAmounts = accountingDataUtils.getMonthlyAccountingJournalClosingBalance(clientid, month, year);

		accJournalsMap = accountingDataUtils.calculateLedgersAmounts(monthlyAmounts, null);

		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();

		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {

				String[] path = ledger.getLedgerpath().split("/");

				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenses".equalsIgnoreCase(path[0].trim())) {
					
					Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
					if (amounts != null) {
						ledger.setAmounts(amounts);
						List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
						if (existLedgerLst == null) {
							existLedgerLst = new ArrayList<ProfileLedger>();
							ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
						}
						existLedgerLst.add(ledger);
					}

				}
			}
		}
		

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");
				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenses".equalsIgnoreCase(path[0].trim())) {
					List<Subgroups> subgrpLst = new ArrayList<Subgroups>();

					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
					amounts.setPreviousyearclosingamt(0);

					if (isNotEmpty(group.getSubgroups())) {
						for (Subgroups subGroup : group.getSubgroups()) {
							if (ledgerMap.containsKey(subGroup.getGroupname())) {

								List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
								if (isNotEmpty(ledgrLst)) {

									for (ProfileLedger ledgr : ledgrLst) {
										Subgroups subgrpCreate = new Subgroups();
										subgrpCreate.setId(ledgr.getId());
										subgrpCreate.setGroupname(ledgr.getLedgerName());
										subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
										subgrpCreate.setPath(ledgr.getLedgerpath());
										subgrpCreate.setAmounts(ledgr.getAmounts());

										subgrpLst.add(subgrpCreate);
									}
									ledgerMap.remove(subGroup.getGroupname());
								}
							}
							subGroup.setAmounts(amounts);
							subgrpLst.add(subGroup);
						}
					}
					if (ledgerMap.containsKey(group.getGroupname())) {

						List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
						if (isNotEmpty(ledgrLst)) {

							for (ProfileLedger ledgr : ledgrLst) {
								Subgroups subgrpCreate = new Subgroups();
								subgrpCreate.setId(ledgr.getId());
								subgrpCreate.setGroupname(ledgr.getLedgerName());
								subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
								subgrpCreate.setPath(ledgr.getLedgerpath());
								subgrpCreate.setAmounts(ledgr.getAmounts());

								subgrpLst.add(subgrpCreate);
							}
							ledgerMap.remove(group.getGroupname());
						}
					}

					group.setAmounts(amounts);
					if (isNotEmpty(subgrpLst)) {
						group.setSubgroups(subgrpLst);
					}
					groupsAndLedgerLst.add(group);
				}
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {

						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getPath());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmountsPandL(node);
				updateGroupConsolidatedAmountsPandL(node);
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	@Override
	public Map<String, Node> getYearlyPandLReport(String clientid, int year) {
		final String method = "getYearlyPandLReport::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Map<String, Node> groupMap = new HashMap<String,Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		List<AccountingJournal> accJournalLstCurrentYear = accountingDataUtils.getYearlyAccountingJournalClosingBalance(clientid, 0, year);
		List<AccountingJournal> accJournalLstPreviousYear = accountingDataUtils.getYearlyAccountingJournalOpeningBalance(clientid, 0, year);

		accJournalsMap = accountingDataUtils.calculateLedgersAmounts(accJournalLstCurrentYear, null);

		Map<String, Amounts> accJournalsPrevousMap = accountingDataUtils.calculateLedgersAmounts(accJournalLstPreviousYear, null);

		for (String ledgername : accJournalsPrevousMap.keySet()) {

			if (accJournalsMap.containsKey(ledgername)) {
				Amounts prevsAmts = accJournalsPrevousMap.get(ledgername);
				Amounts amts = accJournalsMap.get(ledgername);
				amts.setPreviousyearclosingamt(prevsAmts.getClosingamt());
				accJournalsMap.put(ledgername, amts);
			} else {
				Amounts prevsAmts = accJournalsPrevousMap.get(ledgername);
				prevsAmts.setPreviousyearclosingamt(prevsAmts.getClosingamt());
				prevsAmts.setClosingamt(0);
				accJournalsMap.put(ledgername, prevsAmts);
			}
		}

		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();

		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				String[] path = ledger.getLedgerpath().split("/");
				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenses".equalsIgnoreCase(path[0].trim())) {
					if (isNotEmpty(ledger.getLedgerOpeningBalanceType())
							&& isNotEmpty(ledger.getLedgerOpeningBalance())) {
						//String type = ledger.getLedgerOpeningBalanceType();
						// ledgerBooksOpeningMap.put(ledger.getLedgerName(),addLedgersOpeningBalance(ledger));
					}
				}

				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if (amounts != null) {
					ledger.setAmounts(amounts);
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
			}
		}

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");

				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenses".equalsIgnoreCase(path[0].trim())) {
					List<Subgroups> subgrpLst = new ArrayList<Subgroups>();

					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
					amounts.setPreviousyearclosingamt(0);
					if (isNotEmpty(group.getSubgroups())) {
						for (Subgroups subGroup : group.getSubgroups()) {

							if (ledgerMap.containsKey(subGroup.getGroupname())) {
								List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
								if (isNotEmpty(ledgrLst)) {

									for (ProfileLedger ledgr : ledgrLst) {
										Subgroups subgrpCreate = new Subgroups();
										subgrpCreate.setId(ledgr.getId());
										subgrpCreate.setGroupname(ledgr.getLedgerName());
										subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
										subgrpCreate.setPath(ledgr.getLedgerpath());
										subgrpCreate.setAmounts(ledgr.getAmounts());

										subgrpLst.add(subgrpCreate);
									}
									ledgerMap.remove(subGroup.getGroupname());
								}
							}
							subGroup.setAmounts(amounts);
							subgrpLst.add(subGroup);
						}
					}

					if (ledgerMap.containsKey(group.getGroupname())) {
						List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
						if (isNotEmpty(ledgrLst)) {

							for (ProfileLedger ledgr : ledgrLst) {
								Subgroups subgrpCreate = new Subgroups();
								subgrpCreate.setId(ledgr.getId());
								subgrpCreate.setGroupname(ledgr.getLedgerName());
								subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
								subgrpCreate.setPath(ledgr.getLedgerpath());
								subgrpCreate.setAmounts(ledgr.getAmounts());

								subgrpLst.add(subgrpCreate);
							}
							ledgerMap.remove(group.getGroupname());
						}
					}
					group.setAmounts(amounts);
					if (isNotEmpty(subgrpLst)) {
						group.setSubgroups(subgrpLst);
					}
					groupsAndLedgerLst.add(group);
				}
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {

						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getPath());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmountsPandL(node);
				updateGroupConsolidatedAmountsPandL(node);
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	@Override
	public Map<String, Node> getMonthlyBalanceSheet(String clientid, int month, int year) {
		final String method = "getMonthlyBalanceSheet::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgersLst = getLedgersByClientid(clientid);

		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String invoiceMonth = null;
		if (isNotEmpty(client.getJournalEnteringDate())) {
			String[] fpArr = sdf.format(client.getJournalEnteringDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		} else {
			String[] fpArr = sdf.format(client.getCreatedDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		}
		String strMonths = month < 10 ? "0" + month : month + "";
		String monthYear = strMonths + year;
		boolean flag = false;
		if (invoiceMonth.equalsIgnoreCase(monthYear)) {
			flag = true;
		}

		accJournalsMap = accountingDataUtils.openingAndClosingBalanceAdjustment(clientid, month, year);
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		if (isNotEmpty(ledgersLst)) {
			for (ProfileLedger ledger : ledgersLst) {

				//String[] path = ledger.getLedgerpath().split("/");
				if (flag) {
					if (isNotEmpty(ledger.getLedgerOpeningBalanceType())
							&& isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type = ledger.getLedgerOpeningBalanceType();
						if ("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), ledger.getLedgerOpeningBalance());
						} else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), -ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if (amounts != null) {
					amounts.setPreviousyearclosingamt(0);
					ledger.setAmounts(amounts);
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
			}
		}

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				List<Subgroups> subgrpLst = new ArrayList<Subgroups>();
				Amounts amounts = new Amounts();
				amounts.setOpeningamt(0);
				amounts.setClosingamt(0);
				amounts.setDebitamt(0);
				amounts.setCreditamt(0);
				amounts.setPreviousyearclosingamt(0);
				if (isNotEmpty(group.getSubgroups())) {
					for (Subgroups subGroup : group.getSubgroups()) {
						if (ledgerMap.containsKey(subGroup.getGroupname())) {
							List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
							if (isNotEmpty(ledgrLst)) {
								for (ProfileLedger ledgr : ledgrLst) {
									Subgroups subgrpCreate = new Subgroups();
									subgrpCreate.setId(ledgr.getId());
									subgrpCreate.setGroupname(ledgr.getLedgerName());
									subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
									subgrpCreate.setPath(ledgr.getLedgerpath());
									subgrpCreate.setAmounts(ledgr.getAmounts());
									subgrpLst.add(subgrpCreate);
								}
								ledgerMap.remove(subGroup.getGroupname());
							}
						}
						subGroup.setAmounts(amounts);
						subgrpLst.add(subGroup);
					}
				}

				if (ledgerMap.containsKey(group.getGroupname())) {
					List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
					if (isNotEmpty(ledgrLst)) {
						for (ProfileLedger ledgr : ledgrLst) {
							Subgroups subgrpCreate = new Subgroups();
							subgrpCreate.setId(ledgr.getId());
							subgrpCreate.setGroupname(ledgr.getLedgerName());
							subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
							subgrpCreate.setPath(ledgr.getLedgerpath());
							subgrpCreate.setAmounts(ledgr.getAmounts());
							subgrpLst.add(subgrpCreate);
						}
						ledgerMap.remove(group.getGroupname());
					}
				}
				group.setAmounts(amounts);
				if (isNotEmpty(subgrpLst)) {
					group.setSubgroups(subgrpLst);
				}
				groupsAndLedgerLst.add(group);
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {
						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getPath());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmounts(node);
				updateGroupConsolidatedAmounts(node);
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	@Override
	public Map<String, Node> getYearlyBalanceSheet(String clientid, int year) {
		final String method = "getMonthlyBalanceSheet::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);

		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgersLst = getLedgersByClientid(clientid);

		Client client = clientService.findById(clientid);
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String invoiceMonth = null;
		if (isNotEmpty(client.getJournalEnteringDate())) {
			String[] fpArr = sdf.format(client.getJournalEnteringDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		} else {
			String[] fpArr = sdf.format(client.getCreatedDate()).split("-");
			invoiceMonth = fpArr[1] + fpArr[2];
		}

		int nextYear = year + 1;
		List<String> invoice_Month = Arrays.asList("04" + year, "05" + year, "06" + year, "07" + year, "08" + year,
				"09" + year, "10" + year, "11" + year, "12" + year, "01" + (nextYear), "02" + (nextYear),
				"03" + (nextYear));

		boolean flag = false;
		if (invoice_Month.contains(invoiceMonth)) {
			flag = true;
		}

		accJournalsMap = accountingDataUtils.openingAndClosingBalanceAdjustment(clientid, 0, year);
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		if (isNotEmpty(ledgersLst)) {
			for (ProfileLedger ledger : ledgersLst) {
				if (flag) {
					if (isNotEmpty(ledger.getLedgerOpeningBalanceType())
							&& isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type = ledger.getLedgerOpeningBalanceType();
						if ("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), ledger.getLedgerOpeningBalance());
						} else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(), -ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = accJournalsMap.get(ledger.getLedgerName());
				if (amounts != null) {
					amounts.setPreviousyearclosingamt(0);
					ledger.setAmounts(amounts);
					List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
					if (existLedgerLst == null) {
						existLedgerLst = new ArrayList<ProfileLedger>();
						ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
					}
					existLedgerLst.add(ledger);
				}
			}
		}

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				List<Subgroups> subgrpLst = new ArrayList<Subgroups>();

				Amounts amounts = new Amounts();
				amounts.setOpeningamt(0);
				amounts.setClosingamt(0);
				amounts.setDebitamt(0);
				amounts.setCreditamt(0);
				amounts.setPreviousyearclosingamt(0);
				if (isNotEmpty(group.getSubgroups())) {
					for (Subgroups subGroup : group.getSubgroups()) {
						if (ledgerMap.containsKey(subGroup.getGroupname())) {
							List<ProfileLedger> ledgrLst = ledgerMap.get(subGroup.getGroupname());
							if (isNotEmpty(ledgrLst)) {
								for (ProfileLedger ledgr : ledgrLst) {
									Subgroups subgrpCreate = new Subgroups();
									subgrpCreate.setId(ledgr.getId());
									subgrpCreate.setGroupname(ledgr.getLedgerName());
									subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
									subgrpCreate.setPath(ledgr.getLedgerpath());
									subgrpCreate.setAmounts(ledgr.getAmounts());
									subgrpLst.add(subgrpCreate);
								}
								ledgerMap.remove(subGroup.getGroupname());
							}
						}
						subGroup.setAmounts(amounts);
						subgrpLst.add(subGroup);
					}
				}
				if (ledgerMap.containsKey(group.getGroupname())) {
					List<ProfileLedger> ledgrLst = ledgerMap.get(group.getGroupname());
					if (isNotEmpty(ledgrLst)) {
						for (ProfileLedger ledgr : ledgrLst) {
							Subgroups subgrpCreate = new Subgroups();
							subgrpCreate.setId(ledgr.getId());
							subgrpCreate.setGroupname(ledgr.getLedgerName());
							subgrpCreate.setHeadname(ledgr.getGrpsubgrpName());
							subgrpCreate.setPath(ledgr.getLedgerpath());
							subgrpCreate.setAmounts(ledgr.getAmounts());
							subgrpLst.add(subgrpCreate);
						}
						ledgerMap.remove(group.getGroupname());
					}
				}
				group.setAmounts(amounts);
				if (isNotEmpty(subgrpLst)) {
					group.setSubgroups(subgrpLst);
				}
				groupsAndLedgerLst.add(group);
			}
		}

		if (isNotEmpty(ledgerMap)) {
			ledgerMap.forEach((ldgrKey, ldgrValue) -> {
				if (isNotEmpty(ldgrValue)) {
					for (ProfileLedger ledgr : ldgrValue) {

						GroupDetails group = new GroupDetails();
						group.setId(ledgr.getId());
						group.setGroupname(ledgr.getLedgerName());
						group.setHeadname(ledgr.getGrpsubgrpName());
						group.setPath(ledgr.getLedgerpath());
						group.setAmounts(ledgr.getAmounts());
						groupsAndLedgerLst.add(group);
					}
				}
			});
		}

		if (isNotEmpty(groupsAndLedgerLst)) {
			for (GroupDetails details : groupsAndLedgerLst) {
				Node node = new Node();
				node.setHeadname(details.getHeadname());
				node.setGroupname(details.getGroupname());
				node.setPath(details.getPath());
				node.setAmount(details.getAmounts());
				List<Subgroups> subGroups = details.getSubgroups();
				List<Node> tempNodes = new ArrayList<Node>();
				for (Subgroups subGroup : subGroups) {
					Node subNode = new Node();
					subNode.setHeadname(subGroup.getHeadname());
					subNode.setGroupname(subGroup.getGroupname());
					subNode.setPath(subGroup.getPath());
					subNode.setAmount(subGroup.getAmounts());
					if (!addNode(node, subNode, tempNodes)) {
						boolean result = false;
						for (Node childNode : tempNodes) {
							if (addNode(childNode, subNode, tempNodes)) {
								result = true;
								break;
							}
						}
						if (!result) {
							tempNodes.add(subNode);
						}
					}
				}
				updateConsolidatedAmounts(node);
				updateGroupConsolidatedAmounts(node);
				
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}
	// -----------------------------------------------------------------------------------

	private void updateGroupConsolidatedAmountsPandL(Node node) {
		double openingamt = 0, debitamnt = 0, creditamt = 0, closingamt = 0, prevclosingamt = 0;
		List<Node> childrens = node.getChildren();
		if (isNotEmpty(childrens)) {
			for (Node child : childrens) {
				Amounts amts = child.getAmount();
				debitamnt += amts.getDebitamt();
				creditamt += amts.getCreditamt();
				closingamt += amts.getClosingamt();
				openingamt += amts.getOpeningamt();
				prevclosingamt += amts.getPreviousyearclosingamt();
			}
		}
		Amounts amts = accJournalsMap.get(node.getGroupname());

		if (isNotEmpty(amts)) {
			amts.setOpeningamt(amts.getOpeningamt() + openingamt);
			amts.setDebitamt(amts.getDebitamt() + debitamnt);
			amts.setCreditamt(amts.getCreditamt() + creditamt);
			amts.setClosingamt(amts.getClosingamt() + closingamt);
			amts.setPreviousyearclosingamt(amts.getPreviousyearclosingamt() + prevclosingamt);
			openingamt += amts.getOpeningamt();
			debitamnt += amts.getDebitamt();
			creditamt += amts.getCreditamt();
			closingamt += amts.getClosingamt();
			prevclosingamt += amts.getPreviousyearclosingamt();
			node.setAmount(amts);
			accJournalsMap.remove(node.getGroupname());
		} else {
			amts = new Amounts();
			amts.setClosingamt(closingamt);
			amts.setDebitamt(debitamnt);
			amts.setCreditamt(creditamt);
			amts.setOpeningamt(openingamt);
			amts.setPreviousyearclosingamt(prevclosingamt);
			node.setAmount(amts);
		}
	}

	private void updateConsolidatedAmountsPandL(Node node) {
		List<Node> childrens = node.getChildren();
		if (isNotEmpty(childrens)) {
			// Amounts amounts = new Amounts(0d, 0d, 0d, 0d);
			double openingamt = 0, debitamnt = 0, creditamt = 0, closingamt = 0, prevclosingamt = 0;
			for (Node child : childrens) {
				openingamt = 0;
				debitamnt = 0;
				creditamt = 0;
				closingamt = 0;
				prevclosingamt = 0;
				if (isNotEmpty(child.getChildren())) {
					updateConsolidatedAmounts(child);
					Amounts amts = child.getAmount();
					debitamnt += amts.getDebitamt();
					creditamt += amts.getCreditamt();
					closingamt += amts.getClosingamt();
					openingamt += amts.getOpeningamt();
					prevclosingamt += amts.getPreviousyearclosingamt();
				}
				Amounts childamts = accJournalsMap.get(child.getGroupname());

				if (isNotEmpty(childamts)) {
					childamts.setOpeningamt(childamts.getOpeningamt() + openingamt);
					childamts.setDebitamt(childamts.getDebitamt() + debitamnt);
					childamts.setCreditamt(childamts.getCreditamt() + creditamt);
					childamts.setClosingamt(childamts.getClosingamt() + closingamt);
					childamts.setPreviousyearclosingamt(childamts.getPreviousyearclosingamt() + prevclosingamt);

					debitamnt += childamts.getDebitamt();
					creditamt += childamts.getCreditamt();
					closingamt += childamts.getClosingamt();
					openingamt += childamts.getOpeningamt();
					prevclosingamt += childamts.getPreviousyearclosingamt();
					child.setAmount(childamts);

					accJournalsMap.remove(child.getGroupname());
				} else {
					childamts = new Amounts();
					childamts.setClosingamt(closingamt);
					childamts.setDebitamt(debitamnt);
					childamts.setCreditamt(creditamt);
					childamts.setOpeningamt(openingamt);
					childamts.setPreviousyearclosingamt(prevclosingamt);
					child.setAmount(childamts);
				}
				node.getAmount().setOpeningamt(node.getAmount().getOpeningamt() + openingamt);
				node.getAmount().setDebitamt(node.getAmount().getDebitamt() + debitamnt);
				node.getAmount().setCreditamt(node.getAmount().getCreditamt() + creditamt);
				node.getAmount().setClosingamt(node.getAmount().getClosingamt() + closingamt);
				node.getAmount().setPreviousyearclosingamt(node.getAmount().getPreviousyearclosingamt() + prevclosingamt);
			}
			// node.setAmount(node);
		}
	}

	// -----------------------------------------------------------------------------------
	private void updateGroupConsolidatedAmounts(Node node) {

		double openingamt = 0, debitamnt = 0, creditamt = 0, closingamt = 0;
		List<Node> childrens = node.getChildren();
		if (isNotEmpty(childrens)) {
			for (Node child : childrens) {
				Amounts amts = child.getAmount();
				debitamnt += amts.getDebitamt();
				creditamt += amts.getCreditamt();
				closingamt += amts.getClosingamt();
				openingamt += amts.getOpeningamt();
			}
		}
		Amounts amts = accJournalsMap.get(node.getGroupname());

		if (isNotEmpty(amts)) {
			amts.setOpeningamt(amts.getOpeningamt() + openingamt);
			amts.setDebitamt(amts.getDebitamt() + debitamnt);
			amts.setCreditamt(amts.getCreditamt() + creditamt);
			amts.setClosingamt(amts.getClosingamt() + closingamt);
			openingamt += amts.getOpeningamt();
			debitamnt += amts.getDebitamt();
			creditamt += amts.getCreditamt();
			closingamt += amts.getClosingamt();

			node.setAmount(amts);
			accJournalsMap.remove(node.getGroupname());
		} else {
			amts = new Amounts();
			amts.setClosingamt(closingamt);
			amts.setDebitamt(debitamnt);
			amts.setCreditamt(creditamt);
			amts.setOpeningamt(openingamt);
			node.setAmount(amts);
		}
	}

	private void updateConsolidatedAmounts(Node node) {
		List<Node> childrens = node.getChildren();
		Map<String, Node> mainnode = Maps.newHashMap();
		mainnode.put(node.getGroupname(), node);
		if (isNotEmpty(childrens)) {
			double openingamt = 0, debitamnt = 0, creditamt = 0, closingamt = 0;
			for (Node child : childrens) {
				openingamt = 0;
				debitamnt = 0;
				creditamt = 0;
				closingamt = 0;
				if (isNotEmpty(child.getChildren())) {
					updateConsolidatedAmounts(child);
					Amounts amts = child.getAmount();
					debitamnt += amts.getDebitamt();
					creditamt += amts.getCreditamt();
					closingamt += amts.getClosingamt();
					openingamt += amts.getOpeningamt();
				}
				Amounts childamts = accJournalsMap.get(child.getGroupname());
				if (isNotEmpty(childamts)) {
					childamts.setOpeningamt(childamts.getOpeningamt() + openingamt);
					childamts.setDebitamt(childamts.getDebitamt() + debitamnt);
					childamts.setCreditamt(childamts.getCreditamt() + creditamt);
					childamts.setClosingamt(childamts.getClosingamt() + closingamt);
					openingamt += childamts.getOpeningamt();
					debitamnt += childamts.getDebitamt();
					creditamt += childamts.getCreditamt();
					closingamt += childamts.getClosingamt();
					child.setAmount(childamts);
					accJournalsMap.remove(child.getGroupname());
				} else {
					childamts = new Amounts();

					childamts.setOpeningamt(openingamt);
					childamts.setDebitamt(debitamnt);
					childamts.setCreditamt(creditamt);
					childamts.setClosingamt(closingamt);
					child.setAmount(childamts);
				}
				Amounts amts = new Amounts();
				Node nd1 = mainnode.get(node.getGroupname());
				amts.setOpeningamt(nd1.getAmount().getOpeningamt() + openingamt);
				amts.setDebitamt(nd1.getAmount().getDebitamt() + debitamnt);
				amts.setCreditamt(nd1.getAmount().getCreditamt() + creditamt);
				amts.setClosingamt(nd1.getAmount().getClosingamt() + closingamt);
				nd1.setAmount(amts);
				
				/*node1.getAmount().setOpeningamt(node1.getAmount().getOpeningamt() + openingamt);
				node1.getAmount().setDebitamt(node1.getAmount().getDebitamt() + debitamnt);
				node1.getAmount().setCreditamt(node1.getAmount().getCreditamt() + creditamt);
				node1.getAmount().setClosingamt(node1.getAmount().getClosingamt() + closingamt);*/
			}
			// node.setAmount(node);
		}
		mainnode.clear();
	}

	// Main Heads-> iterator->

	private boolean addNode(Node currentNode, Node targetNode, List<Node> tempNodes) {
		if (currentNode == null) {
			return false;
		}
		String headName = targetNode.getHeadname();
		String groupName = targetNode.getGroupname();
		// String nodeHeadName = currentNode.getHeadname();
		String nodeGroupName = currentNode.getGroupname();
		boolean result = false;
		if (nodeGroupName.equals(headName)) {
			List<Node> childs = currentNode.getChildren();
			if (childs == null) {
				childs = new ArrayList<Node>();
				currentNode.setChildren(childs);
			}
			childs.add(targetNode);

			Iterator<Node> ite = tempNodes.iterator();
			while (ite.hasNext()) {
				Node pending = ite.next();
				if (groupName.equals(pending.getHeadname())) {
					List<Node> targetChilds = targetNode.getChildren();
					if (targetChilds == null) {
						targetChilds = new ArrayList<Node>();
						targetNode.setChildren(targetChilds);
					}
					targetChilds.add(pending);
					ite.remove();
				}
			}
			result = true;
		} else {
			List<Node> childNodes = currentNode.getChildren();
			if (childNodes != null) {
				for (Node childNode : childNodes) {
					if (addNode(childNode, targetNode, tempNodes)) {
						result = true;
						break;
					}
				}
			}
		}
		return result;
	}
		
	@Override
	public List<AccountingJournal> getLedgersDataNew(String ledgername, String clientid, List<String> invoiceMonth) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
	
		return accountingDataUtils.getLedgersDataNew(ledgername, clientid, invoiceMonth);
	}
	@Override
	public List<AccountingJournal> getCustomLedgersDataNew(String ledgername, String clientid, Date startDate, Date endDate) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		
		return accountingDataUtils.getCustomLedgersDataNew(ledgername, clientid, startDate, endDate);
	}

	public Double addLedgersOpeningBalance(ProfileLedger ledger) {

		if (isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
			String type = ledger.getLedgerOpeningBalanceType();
			return "Cr".equalsIgnoreCase(type) ? ledger.getLedgerOpeningBalance() : -ledger.getLedgerOpeningBalance();
		}
		return 0d;
	}
	
	@Override
	public Map<String, Object> getMontlyAndYearlyDeductionOrCollection(String clientid, int month, int year, boolean isTds, int start, int length, String searchVal) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		
		String yearCode = Utility.getYearCode(month == 0 ? 4 : month, year);
		
		Page<AccountingJournal> journals = accountingDao.getMontlyAndYearlyDeductionOrCollection(clientid, month, yearCode, isTds, start, length, searchVal);
		TotalJournalAmount totaljournalamount = accountingDao.getTotalInvoicesAmountsForMonth(clientid, month, yearCode, isTds, searchVal);
		invoicesMap.put("invoices", journals);
		invoicesMap.put("totaljournalamount", totaljournalamount);
		
		return invoicesMap;
	}
	
	@Override
	public Map<String, Object> getCustomDeductionOrCollection(String clientid, String fromdate, String todate, boolean isTds, int start, int length, String searchVal) {
		
		final String method = "getCustomTdsDeductionOrCollection::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		String[] fromtimes = fromdate.split("-");
		String[] totimes = todate.split("-");
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23, 59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		Map<String, Object> invoicesMap = new HashMap<String, Object>();
		Page<AccountingJournal> journals = accountingDao.getCustomDeductionOrCollection(clientid, stDate, endDate, isTds, start, length, searchVal);
		TotalJournalAmount totaljournalamount = accountingDao.getTotalInvoicesAmountsForCustom(clientid, stDate, endDate, isTds, searchVal);
		invoicesMap.put("invoices", journals);
		invoicesMap.put("totaljournalamount", totaljournalamount);
		return invoicesMap;
	}

	@Override
	public Map<String, Map<String, String>> getConsolidatedMonthSummeryForYearMonthwise(String clientid, int month, int year, boolean isTds) {
		String yearCode = Utility.getYearCode(month == 0 ? 4 : month, year);
		List<TotalJournalAmount> journalAmounts = null;
		journalAmounts = accountingDao.getConsolidatedMultimonthSummeryForYearMonthwise(clientid, yearCode, isTds);
		Map<String, TotalJournalAmount> summerySlsData = new HashMap<String, TotalJournalAmount>();
		for(TotalJournalAmount gstr1InvoiceAmount : journalAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		int ct = 12;
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, tcsAmount =0d,tdsAmount=0d;
			TotalJournalAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				totalAmount = invoiceAmountSls.getInvoiceamount().doubleValue();
				tcsAmount = invoiceAmountSls.getTcs_payable().doubleValue();
				tdsAmount = invoiceAmountSls.getTds_payable().doubleValue();
			}
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalAmount", decimalFormat.format(totalAmount));
			reportMap.put("tcsAmount", decimalFormat.format(tcsAmount));
			reportMap.put("tdsAmount", decimalFormat.format(tdsAmount));
			
		}
		return summeryReturnData;
	}

	@Override
	public Map<String, Map<String, String>> getConsolidatedMonthSummeryForCustomMonthwise(String clientid,String fromdate, String todate, boolean isTds) {
		String[] fromtimes = fromdate.split("-");
		String[] totimes = todate.split("-");
		
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		List<TotalJournalAmount> journalAmounts = null;
		journalAmounts = accountingDao.getConsolidatedMultimonthSummeryForCustomMonthwise(clientid, stDate, endDate,isTds);
		Map<String, TotalJournalAmount> summerySlsData = new HashMap<String, TotalJournalAmount>();
		for(TotalJournalAmount gstr1InvoiceAmount : journalAmounts){
			String code = gstr1InvoiceAmount.get_id();
			summerySlsData.put(code, gstr1InvoiceAmount);
		}
		Map<String, Map<String, String>> summeryReturnData = new HashMap<String, Map<String, String>>();		
		int ct = 12;
		for(int i=1; i<=ct; i++){
			String cd = Integer.toString(i);
			int totalTransactions = 0;
			Double totalAmount = 0d, tcsAmount =0d,tdsAmount=0d;
			TotalJournalAmount invoiceAmountSls = summerySlsData.get(cd);
			Map<String, String> reportMap = new HashMap<String, String>();
			summeryReturnData.put(cd, reportMap);
			if(invoiceAmountSls != null){
				totalTransactions = invoiceAmountSls.getTotalTransactions();
				totalAmount = invoiceAmountSls.getInvoiceamount().doubleValue();
				tcsAmount = invoiceAmountSls.getTcs_payable().doubleValue();
				tdsAmount = invoiceAmountSls.getTds_payable().doubleValue();
			}
			reportMap.put("totalTransactions", String.valueOf(totalTransactions));
			reportMap.put("totalAmount", decimalFormat.format(totalAmount));
			reportMap.put("tcsAmount", decimalFormat.format(tcsAmount));
			reportMap.put("tdsAmount", decimalFormat.format(tdsAmount));
			
		}
		return summeryReturnData;
	}
	
	@Override
	public Map<String, Object> getAllInvoicePayments(String clientid, String yearcode, boolean isPayment, int start, int length, String searchVal) {
		final String method = "getAllInvoicePayments::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		
		Page<InvoicePayments> invoices = accountingInvoicePaymentDao.getAllInvoicePayments(clientid,yearcode, isPayment, start, length, searchVal);
	
		if(isNotEmpty(invoices.getContent())) {
			invoices.getContent().forEach(invoice -> {
				double amount = 0;
				if(isNotEmpty(invoice.getPayments())) {
					for(Payments token :invoice.getPayments()){
						amount += token.getPaidAmount();	
					}
				}
				if(isNotEmpty(invoice.getTotalamount())) {
					amount = invoice.getTotalamount() - amount;
				}
				invoice.setPaidamount(amount);
				
				int days =  Days.daysBetween(
						new LocalDate(invoice.getDueDate() != null ? invoice.getDueDate() : invoice.getDateofinvoice()),
						new LocalDate(new Date())).getDays();
				invoice.setDays(days);
			});
		}
			
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("invoices", invoices);
		
		return data;
	}

	@Override
	public Map<String, Object> getStockAgingReportData(String clientid, int month,int year, boolean isPayment,String sortparam,String order, int start,int length, String searchVal) {
		Page<? extends InvoiceParent> invoices = null;
		String yearCode = Utility.getYearCode(month == 0 ? 4 : month, year);
		if(isPayment) {
			invoices = accountingInvoicePaymentDao.findByClientidAndMonthAndYearPurchaseRegister(clientid,month,yearCode,sortparam, order,start, length, searchVal);
		}else {
			invoices = accountingInvoicePaymentDao.findByClientidAndMonthAndYear(clientid,month,yearCode, sortparam, order,start, length, searchVal);
		}
		//Page<GSTR1> invoices = accountingInvoicePaymentDao.findByClientidAndMonthAndYear(clientid,yearcode, start, length, searchVal);
		if(isNotEmpty(invoices.getContent())) {
			invoices.getContent().forEach(invoice -> {
				int days =  Days.daysBetween(
						new LocalDate(invoice.getDueDate() != null ? invoice.getDueDate() : invoice.getDateofinvoice()),
						new LocalDate(new Date())).getDays();
				invoice.setSftr(days);
			});
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("invoices", invoices);
		TotalPendingAmount daytotals30 = totalpendingamount(clientid,month, yearCode,isPayment,30);
		TotalPendingAmount daytotals60 = totalpendingamount(clientid,month, yearCode,isPayment,60);
		TotalPendingAmount daytotals90 = totalpendingamount(clientid, month,yearCode,isPayment,90);
		TotalPendingAmount daytotals120 = totalpendingamount(clientid, month,yearCode,isPayment,120);
		TotalPendingAmount daytotals180 = totalpendingamount(clientid, month,yearCode,isPayment,180);
		if(NullUtil.isNotEmpty(daytotals30)) {
			data.put("daytotals30", daytotals30);
		}
		if(NullUtil.isNotEmpty(daytotals60)) {
			data.put("daytotals60", daytotals60);
		}
		if(NullUtil.isNotEmpty(daytotals90)) {
			data.put("daytotals90", daytotals90);
		}
		if(NullUtil.isNotEmpty(daytotals120)) {
			data.put("daytotals120", daytotals120);
		}
		if(NullUtil.isNotEmpty(daytotals180)) {
			data.put("daytotals180", daytotals180);
		}
		return data;
	}
	 public TotalPendingAmount totalpendingamount(String clientid, int month,String yearcode,boolean isPayment,int noofdays) {
		 if(noofdays == 30) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 Date enDate30 = new Date();
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsForMonth(clientid,month, yearcode,isPayment, stDate30,enDate30);
		 }else if(noofdays == 60) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -30);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsForMonth(clientid,month, yearcode,isPayment, stDate30,enDate30);
		 }else if(noofdays == 90) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -60);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsForMonth(clientid,month, yearcode,isPayment, stDate30,enDate30);
		 }else if(noofdays == 120) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -90);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsForMonth(clientid,month, yearcode,isPayment, stDate30,enDate30);
		 }else if(noofdays == 180) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -120);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 Date stDate30 = null;
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsForMonth(clientid,month, yearcode,isPayment, stDate30,enDate30);
		 }
		 
		 return null;
	 }

	@Override
	public Map<String, Object> getStockAgingReportCustomData(String clientid, String fromtime, String totime,
			boolean isPayment, String sortparam,String order,int start, int length, String searchVal) {
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 23, 59, 59);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 23,59, 59);
		endDate = new java.util.Date(cal.getTimeInMillis());
		
		Page<? extends InvoiceParent> invoices = null;
		if(isPayment) {
			invoices = accountingInvoicePaymentDao.findByClientidAndFromtimeAndTotimePurchaseRegister(clientid,stDate,endDate, sortparam, order,start, length, searchVal);
		}else {
			invoices = accountingInvoicePaymentDao.findByClientidAndFromtimeAndTotime(clientid,stDate,endDate,sortparam, order, start, length, searchVal);
		}
		
		if(isNotEmpty(invoices.getContent())) {
			invoices.getContent().forEach(invoice -> {
				int days =  Days.daysBetween(
						new LocalDate(invoice.getDueDate() != null ? invoice.getDueDate() : invoice.getDateofinvoice()),
						new LocalDate(new Date())).getDays();
				invoice.setSftr(days);
			});
		}
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("invoices", invoices);
		TotalPendingAmount daytotals30 = totalpendingamountCustom(clientid,stDate,endDate,isPayment,30);
		TotalPendingAmount daytotals60 = totalpendingamountCustom(clientid,stDate,endDate,isPayment,60);
		TotalPendingAmount daytotals90 = totalpendingamountCustom(clientid,stDate,endDate,isPayment,90);
		TotalPendingAmount daytotals120 = totalpendingamountCustom(clientid, stDate,endDate,isPayment,120);
		TotalPendingAmount daytotals180 = totalpendingamountCustom(clientid,stDate,endDate,isPayment,180);
		if(NullUtil.isNotEmpty(daytotals30)) {
			data.put("daytotals30", daytotals30);
		}
		if(NullUtil.isNotEmpty(daytotals60)) {
			data.put("daytotals60", daytotals60);
		}
		if(NullUtil.isNotEmpty(daytotals90)) {
			data.put("daytotals90", daytotals90);
		}
		if(NullUtil.isNotEmpty(daytotals120)) {
			data.put("daytotals120", daytotals120);
		}
		if(NullUtil.isNotEmpty(daytotals180)) {
			data.put("daytotals180", daytotals180);
		}
		return data;
	}
	
	
	 public TotalPendingAmount totalpendingamountCustom(String clientid, Date fromtime,Date endtime,boolean isPayment,int noofdays) {
		 if(noofdays == 30) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 Date enDate30 = new Date();
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsFromTimetoEndtime(clientid,fromtime, endtime,isPayment, stDate30,enDate30);
		 }else if(noofdays == 60) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -30);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsFromTimetoEndtime(clientid,fromtime, endtime,isPayment, stDate30,enDate30);
		 }else if(noofdays == 90) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -60);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsFromTimetoEndtime(clientid,fromtime, endtime,isPayment, stDate30,enDate30);
		 }else if(noofdays == 120) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -90);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -noofdays);
			 Date dt = calendar.getTime();
			 Date stDate30 = null;
			 cal = Calendar.getInstance();
			 cal.set(dt.getYear()+1900, dt.getMonth(), dt.getDate()-1, 23, 59, 59);
			 stDate30 = new java.util.Date(cal.getTimeInMillis());
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsFromTimetoEndtime(clientid,fromtime, endtime,isPayment, stDate30,enDate30);
		 }else if(noofdays == 180) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.add(Calendar.DAY_OF_MONTH, -120);
			 Date edt = calendar.getTime();
			 Date enDate30 = null;
			 Calendar cal = Calendar.getInstance();
			 cal.set(edt.getYear()+1900, edt.getMonth(), edt.getDate()-1, 0, 0, 0);
			 enDate30 = new java.util.Date(cal.getTimeInMillis());
			 
			 Date stDate30 = null;
			 return accountingInvoicePaymentDao.getTotalInvoicesAmountsFromTimetoEndtime(clientid,fromtime, endtime,isPayment, stDate30,enDate30);
		 }
		 
		 return null;
	 }
	
}
