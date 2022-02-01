package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.mastergst.core.common.AccountConstants;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.accounting.dao.AccountingDao;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalPaymentItems;
import com.mastergst.usermanagement.runtime.domain.AccountingJournalVoucherItems;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.Node;
import com.mastergst.usermanagement.runtime.domain.ProfileLedger;
import com.mastergst.usermanagement.runtime.domain.Subgroups;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.LedgerRepository;

@Service
public class AccountingReportsServiceImpl_bkp implements AccountingReportsService_bkp {

	private static final Logger logger = LogManager.getLogger(AccountingReportsServiceImpl_bkp.class.getName());
	private static final String CLASSNAME = "AccountingReportsServiceImpl::";
	
	@Autowired
	private AccountingJournalRepository accountingJournalRepository;
	@Autowired
	private GroupDetailsRepository groupDetailsRepository;	
	@Autowired
	private LedgerRepository ledgerRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private AccountingDao accountingDao;
	
	@Autowired
	MongoTemplate mongoTemplate;
	
	Map<String, Amounts> accJournalsMap = new HashMap<String, Amounts>();

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

	public List<AccountingJournal> getMonthlyAccountingJournalDetailsByClientidAndDate(String clientid, Date todate, Date fromdate) {
		final String method = "getMonthlyAccountingJournalDetailsByClientidAndDate::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "startdate\t" + todate);
		logger.debug(CLASSNAME + method + "enddate\t" + fromdate);

		return accountingJournalRepository.findByClientIdAndCreatedDateBetweenAndStatusNull(clientid, todate, fromdate);
	}

	public List<AccountingJournal> getMonthlyAccountingJournalClosingBalance(String clientid, int month, int year) {
		final String method = "getMonthlyAccountingJournalClosingBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		String strMonths = month < 10 ? "0" + month : month + "";
		String monthYear = strMonths+year;
		List<String> invoiceMonth=Arrays.asList(monthYear);

		Page<AccountingJournal> journals = accountingDao.findByClientidAndMonthAndYear(clientid, invoiceMonth);
		
		if(isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
			return journals.getContent();
		}
		return null;
		//return accountingJournalRepository.findByClientIdAndInvoiceMonthInAndStatusNull(clientid, invoiceMonth);
	}

	public List<AccountingJournal> getMonthlyAccountingJournalOpeningBalance(String clientid, int month, int year) {
		final String method = "getMonthlyAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		List<String> invoiceMonth=new ArrayList<String>();
		if(month == 1 || month == 2 ||month == 3) {
			int lastyear=year-1;
			for(int i=4;i<=12;i++) {
				if(i>9) {
					invoiceMonth.add(i+""+lastyear);					
				}else{
					invoiceMonth.add("0"+i+lastyear+"");
				}
			}
			for(int i=1;i<month;i++) {
				invoiceMonth.add("0"+i+year+"");				
			}
		}else {
			for(int i=4;i<month;i++) {
				if(i>9) {
					invoiceMonth.add(i+""+year);					
				}else{
					invoiceMonth.add("0"+i+year);
				}
			}
		}
		Page<AccountingJournal> journals = accountingDao.findByClientidAndMonthAndYear(clientid, invoiceMonth);
		
		if(isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
			return journals.getContent();
		}
		return null;
		//return accountingJournalRepository.findByClientIdAndInvoiceMonthInAndStatusNull(clientid, invoiceMonth);
	}

	public List<AccountingJournal> getYearlyAccountingJournalClosingBalance(String clientid, int month, int year) {
		final String method = "getYearlyAccountingJournalClosingBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);
		List<String> invoiceMonth = Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		return accountingJournalRepository.findByClientIdAndInvoiceMonthInAndStatusNull(clientid, invoiceMonth);
	}

	public List<AccountingJournal> getYearlyAccountingJournalOpeningBalance(String clientid, int month, int year) {
		final String method = "getYearlyAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		int prevYear=year-1;
		
		String rtStart = "04"+prevYear;
		String rtEnd = "03"+(year);
		List<String> invoiceMonth = Arrays.asList(rtStart, "05"+prevYear, "06"+prevYear, "07"+prevYear, "08"+prevYear, "09"+prevYear, 
				"10"+prevYear, "11"+prevYear, "12"+prevYear, "01"+(year), "02"+(year), rtEnd);
		
		return accountingJournalRepository.findByClientIdAndInvoiceMonthInAndStatusNull(clientid, invoiceMonth);
	}

	public List<AccountingJournal> getCustomAccountingJournalClosingBalance(String clientid, String fromtime,
			String totime) {
		final String method = "getCustomAccountingJournalClosingBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1,
				0, 0, 0);
		stDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(Integer.parseInt(totimes[2]), Integer.parseInt(totimes[1]) - 1, Integer.parseInt(totimes[0]), 0, 0, 0);
		endDate = new java.util.Date(cal.getTimeInMillis());

		return accountingJournalRepository.findByClientIdAndDateofinvoiceBetweenAndStatusNull(clientid, stDate, endDate);
	}

	public List<AccountingJournal> getCustomAccountingJournalOpeningBalance(String clientid, String fromtime, String totime) {
		final String method = "getCustomAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		

		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");
		
		Date stDate = null;
		Date endDate = null;
		
		
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		
		
		if(Integer.parseInt(fromtimes[1]) == 4 && Integer.parseInt(fromtimes[0]) == 1) {
			//no opening balance and closing balance
			return null;
		}else {
			if(Integer.parseInt(fromtimes[2]) == Integer.parseInt(totimes[2])) {
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1])-1, 0, 0, 0, 0);
				stDate=new java.util.Date(cal.getTimeInMillis());
				
				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
						Integer.parseInt(fromtimes[0]) - 1, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());

				return accountingJournalRepository.findByClientIdAndDateofinvoiceBetweenAndStatusNull(clientid, stDate, endDate);
			} else {
				cal.set(Integer.parseInt(fromtimes[2]) - 1, Integer.parseInt(fromtimes[1]) - 1, 0, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());

				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
						Integer.parseInt(fromtimes[0]) - 1, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());

				return accountingJournalRepository.findByClientIdAndDateofinvoiceBetweenAndStatusNull(clientid, stDate, endDate);
			}
		}
	}

	/**
	 * below method works on mothly and yearly
	 */
	public Map<String, Amounts> openingAndClosingBalanceAdjustment(String clientid, int month, int year) {
		final String method = "openingAndClosingBalanceAdjustment::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		List<AccountingJournal> openingbalance = null;
		List<AccountingJournal> closingbalance = null;
		// month != 0 if
		if (month != 0) {
			closingbalance = getMonthlyAccountingJournalClosingBalance(clientid, month, year);
			if (month != 4) {
				openingbalance = getMonthlyAccountingJournalOpeningBalance(clientid, month, year);
			}
		} else {
			closingbalance = getYearlyAccountingJournalClosingBalance(clientid, month, year);
			openingbalance = getYearlyAccountingJournalOpeningBalance(clientid, month, year);
		}

		Map<String, Amounts> accntJournalsMap=newCalculateLedgersAmountstb(closingbalance, openingbalance);
		
		logger.debug(CLASSNAME + method + END);
		return accntJournalsMap;
	}

	public Map<String, Amounts> customOpeningAndClosingBalanceAdjustment(String clientid, String fromtime,
			String totime) {
		final String method = "customOpeningAndClosingBalanceAdjustment::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "totime\t" + totime);
		logger.debug(CLASSNAME + method + "fromtime\t" + fromtime);

		List<AccountingJournal> closingbalance = getCustomAccountingJournalClosingBalance(clientid, fromtime, totime);
		List<AccountingJournal> openingbalance = getCustomAccountingJournalOpeningBalance(clientid, fromtime, totime);

		Map<String, Amounts> accntJournalsMap=calculateLedgersAmounts(closingbalance, openingbalance);
		
		logger.debug(CLASSNAME + method + END);
		return accntJournalsMap;
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
		
		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		accJournalsMap = openingAndClosingBalanceAdjustment(clientid, month, year);

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
				ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
				List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
				if (existLedgerLst == null) {
					existLedgerLst = new ArrayList<ProfileLedger>();
					ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
				}
				existLedgerLst.add(ledger);
			}
		}
		
		if(isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			
			for(String ldgrName:ledgerBooksOpeningMap.keySet()) {
				
				if(accJournalsMap.containsKey(ldgrName)) {
					Amounts amts=accJournalsMap.get(ldgrName);
					double booksAmts=ledgerBooksOpeningMap.get(ldgrName);
					
					double openingamt=0,debitamt=0,creditamt=0,closingamt=0;
					
					openingamt+=amts.getOpeningamt()+booksAmts;
					debitamt+=amts.getDebitamt();
					creditamt+=amts.getCreditamt();
					closingamt=openingamt+debitamt-creditamt;
					
					amts.setOpeningamt(openingamt);
					amts.setClosingamt(closingamt);
					accJournalsMap.put(ldgrName,amts);
				}else {
					double booksAmts=ledgerBooksOpeningMap.get(ldgrName);
					accJournalsMap.put(ldgrName,new Amounts(booksAmts,0,0,booksAmts));
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
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	@Override
	public Map<String, Node> getYearlyTrailBalance(String clientid, int year) {
		final String method = "getYearlyTrailBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "year\t" + year);
		
		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);

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
		
		int nextYear=year+1;
		List<String> invoice_Month = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(nextYear), "02"+(nextYear), "03"+(nextYear));
		
		boolean flag=false;
		if(invoice_Month.contains(invoiceMonth)) {
			flag=true;
		}
		
		accJournalsMap = openingAndClosingBalanceAdjustment(clientid, 0, year);
		
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();
		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				if(flag) {
					if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type=ledger.getLedgerOpeningBalanceType();
						if("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
						}else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
						}
					}					
				}
				ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
				List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
				if (existLedgerLst == null) {
					existLedgerLst = new ArrayList<ProfileLedger>();
					ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
				}
				existLedgerLst.add(ledger);
			}
		}
		if(isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			for(String ldgrName:ledgerBooksOpeningMap.keySet()) {
				if(accJournalsMap.containsKey(ldgrName)) {
					Amounts amts=accJournalsMap.get(ldgrName);
					double booksAmts=ledgerBooksOpeningMap.get(ldgrName);
					
					double openingamt=0,debitamt=0,creditamt=0,closingamt=0;
					
					openingamt+=amts.getOpeningamt()+booksAmts;
					debitamt+=amts.getDebitamt();
					creditamt+=amts.getCreditamt();
					closingamt=openingamt+debitamt-creditamt;
					
					amts.setOpeningamt(openingamt);
					amts.setClosingamt(closingamt);
					accJournalsMap.put(ldgrName,amts);
				}else {
					double booksAmts=ledgerBooksOpeningMap.get(ldgrName);
					accJournalsMap.put(ldgrName,new Amounts(booksAmts,0,0,booksAmts));
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
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	@Override
	public Map<String, Node> getCustomTrailBalance(String clientid, String fromtime, String totime) {
		final String method = "getCustomTrailBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);	
		logger.debug(CLASSNAME + method + "totime\t" + totime);
		logger.debug(CLASSNAME + method + "fromtime\t" + fromtime);

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
		
		String[] fromtimeArr=fromtime.split("-");
		String[] totimeArr=totime.split("-");
		
		String strMonthsFrom = Integer.parseInt(fromtimeArr[1]) < 10 ? "0" + Integer.parseInt(fromtimeArr[1]) : Integer.parseInt(fromtimeArr[1]) + "";
		String strMonthsTo = Integer.parseInt(totimeArr[1]) < 10 ? "0" + Integer.parseInt(totimeArr[1]) : Integer.parseInt(totimeArr[1]) + "";
		String fromMonthYear = strMonthsFrom+fromtimeArr[2];
		String toMonthYear = strMonthsTo+totimeArr[2];
		boolean flag=false;
		if(invoiceMonth.equalsIgnoreCase(fromMonthYear) || invoiceMonth.equalsIgnoreCase(toMonthYear)) {
			flag=true;
		}
		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		
		accJournalsMap =customOpeningAndClosingBalanceAdjustment(clientid, fromtime, totime);
		
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		Map<String, Double> ledgerBooksOpeningMap = new HashMap<String, Double>();

		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				if(flag) {
					if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type=ledger.getLedgerOpeningBalanceType();
						if("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
						}else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
						}
					}
				}
				ledger.setAmounts(new Amounts(0d, 0d, 0d, 0d));
				List<ProfileLedger> existLedgerLst = ledgerMap.get(ledger.getGrpsubgrpName());
				if (existLedgerLst == null) {
					existLedgerLst = new ArrayList<ProfileLedger>();
					ledgerMap.put(ledger.getGrpsubgrpName(), existLedgerLst);
				}
				existLedgerLst.add(ledger);
			}
		}

		if(isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			for(String ldgrName:ledgerBooksOpeningMap.keySet()) {
				if(accJournalsMap.containsKey(ldgrName)) {
					Amounts amts=accJournalsMap.get(ldgrName);
					double booksAmts=amts.getOpeningamt()+ledgerBooksOpeningMap.get(ldgrName);
					amts.setOpeningamt(booksAmts);
					accJournalsMap.put(ldgrName,amts);
				}else {
					accJournalsMap.put(ldgrName,new Amounts(ledgerBooksOpeningMap.get(ldgrName),0,0,0));
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
				groupMap.put(details.getGroupname(), node);
			}
		}
		logger.debug(CLASSNAME + method + END);
		return groupMap;
	}

	public List<GroupDetails> getClientGroupdetailsAndHeadTypeIn(String clientid) {
		final String method = "getClientGroupdetails::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "id\t" + clientid);

		List<String> headTypes = Arrays.asList("Income", "income", "Expenditure");

		return groupDetailsRepository.findByClientidAndHeadnameIn(clientid, headTypes);
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
		List<AccountingJournal> monthlyAmounts=getMonthlyAccountingJournalClosingBalance(clientid,month, year);
		accJournalsMap = calculateLedgersAmounts(monthlyAmounts, null);
		
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
				
		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				
				String[] path = ledger.getLedgerpath().split("/");

				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenditure".equalsIgnoreCase(path[0].trim())) {
					/*if(flag) {
						if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
							String type=ledger.getLedgerOpeningBalanceType();
							if("Dr".equalsIgnoreCase(type)) {
								ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
							}else {
								ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
							}
						}
					}*/
					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
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
		/*if(isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			for(String ldgrName:ledgerBooksOpeningMap.keySet()) {
				if(accJournalsMap.containsKey(ldgrName)) {
					Amounts amts=accJournalsMap.get(ldgrName);
					double booksAmts=amts.getOpeningamt()+ledgerBooksOpeningMap.get(ldgrName);
					amts.setOpeningamt(booksAmts);
					accJournalsMap.put(ldgrName,amts);
				}else {
					accJournalsMap.put(ldgrName,new Amounts(ledgerBooksOpeningMap.get(ldgrName),0,0,0));
				}
			}
		}*/

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {
			
			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");
				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenditure".equalsIgnoreCase(path[0].trim())) {
					List<Subgroups> subgrpLst = new ArrayList<Subgroups>();

					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
					amounts.setPreviousyearclosingamt(0);
					
					if(isNotEmpty(group.getSubgroups())) {
						for (Subgroups subGroup : group.getSubgroups()) {
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
							subGroup.setAmounts(amounts);
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
					
					group.setAmounts(amounts);				
					if(isNotEmpty(subgrpLst)) {
						group.setSubgroups(subgrpLst);
					}
					groupsAndLedgerLst.add(group);
				}
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
		
		Map<String, Node> groupMap = new HashMap<String, Node>();
		List<GroupDetails> groupsLst = getClientGroupdetails(clientid);
		List<ProfileLedger> ledgerLst = getLedgersByClientid(clientid);
		List<AccountingJournal> accJournalLstCurrentYear = getYearlyAccountingJournalClosingBalance(clientid, 0, year);
		List<AccountingJournal> accJournalLstPreviousYear = getYearlyAccountingJournalOpeningBalance(clientid, 0, year);
		
		accJournalsMap = calculateLedgersAmounts(accJournalLstCurrentYear, null);
		
		Map<String, Amounts> accJournalsPrevousMap=calculateLedgersAmounts(accJournalLstPreviousYear, null);
		
		for(String ledgername:accJournalsPrevousMap.keySet()){
			
			if(accJournalsMap.containsKey(ledgername)) {
				
				Amounts prevsAmts=accJournalsPrevousMap.get(ledgername);
				
				Amounts amts=accJournalsMap.get(ledgername);
				
				amts.setPreviousyearclosingamt(prevsAmts.getClosingamt());
				accJournalsMap.put(ledgername,amts);
			}else {
				Amounts prevsAmts=accJournalsPrevousMap.get(ledgername);
				/*
				 	prevsAmts.setOpeningamt(0);
					prevsAmts.setDebitamt(0);
					prevsAmts.setCreditamt(0);
				*/
				prevsAmts.setPreviousyearclosingamt(prevsAmts.getClosingamt());
				prevsAmts.setClosingamt(0);
				accJournalsMap.put(ledgername,prevsAmts);
			}
		}
		
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
				
		if (isNotEmpty(ledgerLst)) {
			for (ProfileLedger ledger : ledgerLst) {
				
				String[] path = ledger.getLedgerpath().split("/");

				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenditure".equalsIgnoreCase(path[0].trim())) {
					/*if(flag) {
						if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
							String type=ledger.getLedgerOpeningBalanceType();
							if("Dr".equalsIgnoreCase(type)) {
								ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
							}else {
								ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
							}
						}
					}*/
					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
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

		/*if(isNotEmpty(ledgerBooksOpeningMap) && isNotEmpty(ledgerBooksOpeningMap.keySet())) {
			for(String ldgrName:ledgerBooksOpeningMap.keySet()) {
				if(accJournalsMap.containsKey(ldgrName)) {
					Amounts amts=accJournalsMap.get(ldgrName);
					double booksAmts=amts.getOpeningamt()+ledgerBooksOpeningMap.get(ldgrName);
					amts.setOpeningamt(booksAmts);
					accJournalsMap.put(ldgrName,amts);
				}else {
					accJournalsMap.put(ldgrName,new Amounts(ledgerBooksOpeningMap.get(ldgrName),0,0,0));
				}
			}
		}*/

		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");

				if ("Income".equalsIgnoreCase(path[0].trim()) || "Expenditure".equalsIgnoreCase(path[0].trim())) {
					List<Subgroups> subgrpLst = new ArrayList<Subgroups>();

					Amounts amounts = new Amounts();
					amounts.setOpeningamt(0);
					amounts.setClosingamt(0);
					amounts.setDebitamt(0);
					amounts.setCreditamt(0);
					amounts.setPreviousyearclosingamt(0);
					if(isNotEmpty(group.getSubgroups())) {
						for (Subgroups subGroup : group.getSubgroups()) {
							
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
							subGroup.setAmounts(amounts);
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
					group.setAmounts(amounts);				
					if(isNotEmpty(subgrpLst)) {
						group.setSubgroups(subgrpLst);
					}
					groupsAndLedgerLst.add(group);
				}
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
		List<GroupDetails> groupsLst=getClientGroupdetails(clientid);
		List<ProfileLedger> ledgersLst=getLedgersByClientid(clientid); 

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
		
		accJournalsMap = openingAndClosingBalanceAdjustment(clientid,month, year);
		Map<String,Double> ledgerBooksOpeningMap=new HashMap<String,Double>();
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		if (isNotEmpty(ledgersLst)) {
			for (ProfileLedger ledger : ledgersLst) {

				String[] path = ledger.getLedgerpath().split("/");
				if(flag) {
					if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type=ledger.getLedgerOpeningBalanceType();
						if("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
						}else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = new Amounts();
				amounts.setOpeningamt(0);
				amounts.setClosingamt(0);
				amounts.setDebitamt(0);
				amounts.setCreditamt(0);
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
		
		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");
				
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
		//System.out.println(groupMap);
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
		List<GroupDetails> groupsLst=getClientGroupdetails(clientid);
		List<ProfileLedger> ledgersLst=getLedgersByClientid(clientid); 

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
		
		int nextYear=year+1;
		List<String> invoice_Month = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(nextYear), "02"+(nextYear), "03"+(nextYear));
		
		boolean flag=false;
		if(invoice_Month.contains(invoiceMonth)) {
			flag=true;
		}
		
		accJournalsMap = openingAndClosingBalanceAdjustment(clientid, 0, year);
		Map<String,Double> ledgerBooksOpeningMap=new HashMap<String,Double>();
		Map<String, List<ProfileLedger>> ledgerMap = new HashMap<String, List<ProfileLedger>>();
		if (isNotEmpty(ledgersLst)) {
			for (ProfileLedger ledger : ledgersLst) {

				String[] path = ledger.getLedgerpath().split("/");
				if(flag) {
					if(isNotEmpty(ledger.getLedgerOpeningBalanceType()) && isNotEmpty(ledger.getLedgerOpeningBalance())) {
						String type=ledger.getLedgerOpeningBalanceType();
						if("Dr".equalsIgnoreCase(type)) {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),ledger.getLedgerOpeningBalance());
						}else {
							ledgerBooksOpeningMap.put(ledger.getLedgerName(),-ledger.getLedgerOpeningBalance());
						}
					}
				}
				Amounts amounts = new Amounts();
				amounts.setOpeningamt(0);
				amounts.setClosingamt(0);
				amounts.setDebitamt(0);
				amounts.setCreditamt(0);
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
		
		List<GroupDetails> groupsAndLedgerLst = new ArrayList<GroupDetails>();
		if (isNotEmpty(groupsLst)) {

			for (GroupDetails group : groupsLst) {
				String[] path = group.getPath().split("/");
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
		//System.out.println(groupMap);
		logger.debug(CLASSNAME + method + END);
		return groupMap;	
	}
	// -----------------------------------------------------------------------------------

	private void updateGroupConsolidatedAmountsPandL(Node node) {
		double openingamt = 0, debitamnt = 0, creditamt = 0, closingamt = 0, prevclosingamt=0;
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
			//Amounts amounts = new Amounts(0d, 0d, 0d, 0d);
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
				Amounts childamts=accJournalsMap.get(child.getGroupname());

				if(isNotEmpty(childamts)) {
					childamts.setOpeningamt(childamts.getOpeningamt()+openingamt);
					childamts.setDebitamt(childamts.getDebitamt()+debitamnt);
					childamts.setCreditamt(childamts.getCreditamt()+creditamt);
					childamts.setClosingamt(childamts.getClosingamt()+closingamt);
					childamts.setPreviousyearclosingamt(childamts.getPreviousyearclosingamt()+prevclosingamt);
					
					debitamnt += childamts.getDebitamt();
					creditamt += childamts.getCreditamt();
					closingamt += childamts.getClosingamt();
					openingamt += childamts.getOpeningamt();
					prevclosingamt+=childamts.getPreviousyearclosingamt();
					child.setAmount(childamts);
					
					accJournalsMap.remove(child.getGroupname());
				}else {
					childamts=new Amounts();
					childamts.setClosingamt(closingamt);
					childamts.setDebitamt(debitamnt);
					childamts.setCreditamt(creditamt);
					childamts.setOpeningamt(openingamt);
					childamts.setOpeningamt(prevclosingamt);
					child.setAmount(childamts);
				}
				node.getAmount().setOpeningamt(node.getAmount().getOpeningamt()+openingamt);
				node.getAmount().setDebitamt(node.getAmount().getDebitamt()+debitamnt);
				node.getAmount().setCreditamt(node.getAmount().getCreditamt()+creditamt);
				node.getAmount().setClosingamt(node.getAmount().getClosingamt()+closingamt);
				node.getAmount().setOpeningamt(node.getAmount().getPreviousyearclosingamt()+prevclosingamt);
			}
			//node.setAmount(node);
		}
	}
	
	//-----------------------------------------------------------------------------------
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
		if (isNotEmpty(childrens)) {
			//Amounts amounts = new Amounts(0d, 0d, 0d, 0d);
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
				if(child.getGroupname().equalsIgnoreCase("Advance From Other Debtors")) {
					logger.info(child.getGroupname());
				}
				Amounts childamts=accJournalsMap.get(child.getGroupname());

				if (isNotEmpty(childamts)) {
					childamts.setOpeningamt(childamts.getOpeningamt() + openingamt);
					childamts.setDebitamt(childamts.getDebitamt() + debitamnt);
					childamts.setCreditamt(childamts.getCreditamt() + creditamt);
					childamts.setClosingamt(childamts.getClosingamt() + closingamt);
					
					openingamt += childamts.getOpeningamt();
					debitamnt += childamts.getDebitamt();
					creditamt += childamts.getCreditamt();
					closingamt += childamts.getClosingamt();
					
					childamts.setOpeningamt(childamts.getOpeningamt());
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
				node.getAmount().setOpeningamt(node.getAmount().getOpeningamt()+openingamt);
				node.getAmount().setDebitamt(node.getAmount().getDebitamt()+debitamnt);
				node.getAmount().setCreditamt(node.getAmount().getCreditamt()+creditamt);
				node.getAmount().setClosingamt(node.getAmount().getClosingamt()+closingamt);				
			}
			//node.setAmount(node);
		}
	}

	private boolean addNode(Node currentNode, Node targetNode, List<Node> tempNodes) {
		if (currentNode == null) {
			return false;
		}
		String headName = targetNode.getHeadname();
		String groupName = targetNode.getGroupname();
		//String nodeHeadName = currentNode.getHeadname();
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
	
	public Map<String, Amounts> calculateLedgersAmounts(List<AccountingJournal> accJournalsClosingLst, List<AccountingJournal> accJournalsOpeningLst) {

		Map<String, Amounts> accntJournalsClosingMap = new HashMap<String, Amounts>();
		Map<String, Amounts> accntJournalsMap = new HashMap<String, Amounts>();
		if(isNotEmpty(accJournalsClosingLst)) {
			for(AccountingJournal accJournals :accJournalsClosingLst) {
				if ("Contra".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getContraitems())) {
						for (AccountingJournalVoucherItems contra : accJournals.getContraitems()) {
							Amounts amts = accntJournalsClosingMap.get(contra.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(contra.getLedger(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
	
								accntJournalsClosingMap.put(contra.getLedger(), amts);
							}
						}
					}
				} else if ("Voucher".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVoucheritems())) {
						for (AccountingJournalVoucherItems voucher : accJournals.getVoucheritems()) {
							Amounts amts = accntJournalsClosingMap.get(voucher.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(voucher.getLedger(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
	
								accntJournalsClosingMap.put(voucher.getLedger(), amts);
							}
						}
					}
				} else if ("Payment Receipt".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt + amts.getOpeningamt());
							amts.setDebitamt(debitamt + amts.getDebitamt());
							amts.setCreditamt(creditamt + amts.getCreditamt());
							amts.setClosingamt(closingamt + amts.getClosingamt());
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						} else {
							amts = new Amounts();
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							}
						}
					}
				} else if ("Payment".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt + amts.getOpeningamt());
							amts.setDebitamt(debitamt + amts.getDebitamt());
							amts.setCreditamt(creditamt + amts.getCreditamt());
							amts.setClosingamt(closingamt + amts.getClosingamt());
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						} else {
							amts = new Amounts();
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							}
						}
					}
				} else if (MasterGSTConstants.GSTR1.equalsIgnoreCase(accJournals.getReturnType())) {
					
					String tdsReceivables = "TDS Receivables";
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String doctype=null;
					
					if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
						doctype=accJournals.getCreditDebitNoteType();
					}
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
							MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
		
						if (isNotEmpty(vendorName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								creditamt += accJournals.getCustomerorSupplierAccount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(vendorName, amts);
						}
		
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getTdsamount())) {
								creditamt += accJournals.getTdsamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tdsReceivables, amts);
						}
		
						String ledgerName ="Sales";
						
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								debitamt += accJournals.getSalesorPurchases();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(ledgerName, amts);
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getIgstamount())) {
								debitamt += accJournals.getIgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getCgstamount())) {
								debitamt += accJournals.getCgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getSgstamount())) {
								debitamt += accJournals.getSgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
					}else {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
		
						if (isNotEmpty(vendorName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								debitamt += accJournals.getCustomerorSupplierAccount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(vendorName, amts);
						}
		
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getTdsamount())) {
								debitamt += accJournals.getTdsamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tdsReceivables, amts);
						}
		
						String ledgerName =null;
						
						if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
							ledgerName ="Cash";
						}else {
							ledgerName ="Sales";
						}
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								creditamt += accJournals.getSalesorPurchases();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(ledgerName, amts);
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getIgstamount())) {
								creditamt += accJournals.getIgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getCgstamount())) {
								creditamt += accJournals.getCgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
		
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getSgstamount())) {
								creditamt += accJournals.getSgstamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
					}
					// GSTR1 end
				} else {
					String tdsPayable = "TDS Payable";
					String igstReceivable_rcm = "Input IGST (RCM)";
					String cgstReceivable_rcm = "Input CGST (RCM)";
					String sgstReceivable_rcm = "Input SGST (RCM)";
	
					String igstReceivable = "Input IGST";
					String cgstReceivable = "Input CGST";
					String sgstReceivable = "Input SGST";
					String cessReceivable = "CESS Receivable";
					
					String igstPayable_rcm = "Output IGST (RCM)";
					String cgstPayable_rcm = "Output CGST (RCM)";
					String sgstPayable_rcm = "Output SGST (RCM)";
	
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String cessPayable = "CESS Payable";
					
					String tcsReceivable= "TCS Receivable";
					
					if("RCM".equalsIgnoreCase(accJournals.getRcmorinterorintra())) {
						if("true".equalsIgnoreCase(accJournals.getItcinelg())) {
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = "Expenses";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getInterorintra())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(cgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getCgstamount())) {
										debitamt += accJournals.getCgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(sgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getSgstamount())) {
										debitamt += accJournals.getSgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstReceivable_rcm, amts);
								}
								
								if (isNotEmpty(accJournals.getRcgstamount()) && accJournals.getRcgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(cgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getRcgstamount())) {
										creditamt += accJournals.getRcgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstPayable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getRsgstamount()) && accJournals.getRsgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(sgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getRsgstamount())) {
										creditamt += accJournals.getRsgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}
						}
					}else {
						if ("Nil Supplies".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = null;
							if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
								ledgerName = "Cash";						
							}else{
								ledgerName = "Expenditure";
							}
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else if ("Import Services".equalsIgnoreCase(accJournals.getInvoiceType())) {
							if("Intra".equalsIgnoreCase(accJournals.getInvoiceType())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}
						}else if("Import Goods".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							//not itcinelg
							if(!"true".equalsIgnoreCase(accJournals.getItcinelg())) {
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable, amts);
								}
							}
							
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = "Expenditure";
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else if("ITC Reversal".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIgstamount())) {
									creditamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCgstamount())) {
									creditamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getSgstamount())) {
									creditamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstReceivable, amts);
							}
							
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCessAmount())) {
									creditamt += accJournals.getCessAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessReceivable, amts);
							}
						}else if("ISD".equalsIgnoreCase(accJournals.getInvoiceType())) {
							String ledgerName = "HO-ISD";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIgstamount())) {
									creditamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCgstamount())) {
									creditamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getSgstamount())) {
									creditamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCessAmount())) {
									creditamt += accJournals.getCessAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessReceivable, amts);
							}
							
							if (isNotEmpty(accJournals.getIsdineligiblecredit()) && accJournals.getIsdineligiblecredit() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get("Ineligible GST Credit Receivable");
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIsdineligiblecredit())) {
									creditamt += accJournals.getIsdineligiblecredit();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put("Ineligible GST Credit Receivable", amts);
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getRcmorinterorintra())){
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												debitamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												creditamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												debitamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
	
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													debitamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													creditamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													debitamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getCgstamount())) {
													creditamt += accJournals.getCgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(cgstReceivable, amts);
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getSgstamount())) {
													creditamt += accJournals.getSgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(sgstReceivable, amts);
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													creditamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													debitamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getTdsamount())) {
													creditamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCgstamount())) {
													debitamt += accJournals.getCgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(cgstReceivable, amts);
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSgstamount())) {
													debitamt += accJournals.getSgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(sgstReceivable, amts);
											}
										}
									}
								}
							}else {
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												debitamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												creditamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												debitamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
	
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
											
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													debitamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													creditamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													debitamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getIgstamount())) {
													creditamt += accJournals.getIgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(igstReceivable, amts);
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													creditamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													debitamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													creditamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getIgstamount())) {
													debitamt += accJournals.getIgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(igstReceivable, amts);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if(isNotEmpty(accJournalsOpeningLst)) {
			for(AccountingJournal accJournals :accJournalsOpeningLst) {
				if ("Contra".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getContraitems())) {
						for (AccountingJournalVoucherItems contra : accJournals.getContraitems()) {
							
							double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(contra.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(contra.getDramount())) {
									opendebitamt+= contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									opencreditamt+= contra.getCramount();
								}
								
								openopeningamt = opendebitamt - opencreditamt;
								
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(contra.getLedger(), amts);
								accntJournalsClosingMap.remove(contra.getLedger());
							} else {
								Amounts amtss = accntJournalsMap.get(contra.getLedger());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								amtss.setClosingamt(openopeningamt+closingamt);

								accntJournalsMap.put(contra.getLedger(), amtss);
							}
						}
					}
				}else if ("Voucher".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVoucheritems())) {
						for (AccountingJournalVoucherItems voucher : accJournals.getVoucheritems()) {
							
							double openopeningamt=0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(voucher.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(voucher.getDramount())) {
									opendebitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									opencreditamt = voucher.getCramount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(opendebitamt + amts.getDebitamt());
								amts.setCreditamt(opencreditamt + amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(voucher.getLedger(), amts);
								accntJournalsClosingMap.remove(voucher.getLedger());
							} else {
								Amounts amtss = accntJournalsMap.get(voucher.getLedger());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								
								amtss.setClosingamt(openopeningamt+closingamt);

								accntJournalsMap.put(voucher.getLedger(), amtss);
							}
						}
					}
				}else if ("Payment Receipt".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						
						double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
						
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								opencreditamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt =opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
							amts.setDebitamt(amts.getDebitamt());
							amts.setCreditamt(amts.getCreditamt());
							amts.setClosingamt(openopeningamt + amts.getClosingamt());
							
							accntJournalsMap.put(accJournals.getVendorName(), amts);
							
							accntJournalsClosingMap.remove(accJournals.getVendorName());
						} else {
							Amounts amtss = accntJournalsMap.get(accJournals.getVendorName());
							if(isNotEmpty(amtss)) {
								amtss.setDebitamt(amtss.getDebitamt());
								amtss.setCreditamt(amtss.getCreditamt());
								openingamt+=amtss.getOpeningamt();
								closingamt+=amtss.getClosingamt();
							}else {
								amtss = new Amounts();
								amtss.setDebitamt(0);
								amtss.setCreditamt(0);
							}
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt =debitamt - creditamt;
							amtss.setOpeningamt(openopeningamt+openingamt);
							
							amtss.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(accJournals.getVendorName(), amtss);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								accntJournalsMap.put(payemtItems.getLedgerName(), amts);
								
								accntJournalsClosingMap.remove(payemtItems.getLedgerName());
							} else {
								Amounts amtss = accntJournalsMap.get(payemtItems.getLedgerName());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								
								amtss.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(payemtItems.getLedgerName(), amtss);
							}
						}
					}
				}else if ("Payment".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						
						double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
						
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt = debitamt - creditamt;
							amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
							amts.setDebitamt(amts.getDebitamt());
							amts.setCreditamt(amts.getCreditamt());
							amts.setClosingamt(openopeningamt + amts.getClosingamt());
							accntJournalsMap.put(accJournals.getVendorName(), amts);
							accntJournalsClosingMap.remove(accJournals.getVendorName());
						} else {
							Amounts amtss = accntJournalsMap.get(accJournals.getVendorName());
							if(isNotEmpty(amtss)) {
								amtss.setDebitamt(amtss.getDebitamt());
								amtss.setCreditamt(amtss.getCreditamt());
								openingamt+=amtss.getOpeningamt();
								closingamt+=amtss.getClosingamt();
							}else {
								amtss = new Amounts();																
								amtss.setDebitamt(0);
								amtss.setCreditamt(0);
							}
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt = debitamt - creditamt;
							amtss.setOpeningamt(openopeningamt+openingamt);
							
							amtss.setClosingamt(openopeningamt+closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amtss);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(payemtItems.getLedgerName(), amts);
								accntJournalsClosingMap.remove(payemtItems.getLedgerName());
							} else {
								Amounts amtss = accntJournalsMap.get(accJournals.getLedgerName());
								if(isEmpty(amtss)) {
									amtss = new Amounts();																
								}
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								openopeningamt =debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt);
								amtss.setDebitamt(debitamt);
								amtss.setCreditamt(creditamt);
								amtss.setClosingamt(openopeningamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amtss);
							}
						}
					}
				}else if (MasterGSTConstants.GSTR1.equalsIgnoreCase(accJournals.getReturnType())) {

					String tdsReceivables = "TDS Receivables";
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String doctype=null;
					if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
						doctype=accJournals.getCreditDebitNoteType();
					}
					
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
							MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							Amounts amts = null;
							 amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(vendorName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								opencreditamt += accJournals.getCustomerorSupplierAccount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							
							accntJournalsMap.put(vendorName, amts);
							
							if(accntJournalsClosingMap.containsKey(vendorName)){
								accntJournalsClosingMap.remove(vendorName);
							}
						}

						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(tdsReceivables);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}

							if (isNotEmpty(accJournals.getTdsamount())) {
								opencreditamt += accJournals.getTdsamount();
							}
							openopeningamt = opendebitamt - openclosingamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(tdsReceivables, amts);
							
							if(accntJournalsClosingMap.containsKey(tdsReceivables)){
								accntJournalsClosingMap.remove(tdsReceivables);
							}
						}

						String ledgerName ="Sales";
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(ledgerName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								opendebitamt += accJournals.getSalesorPurchases();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(ledgerName, amts);
							
							if(accntJournalsClosingMap.containsKey(ledgerName)) {
								accntJournalsClosingMap.remove(ledgerName);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(igstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(igstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}

							if (isNotEmpty(accJournals.getIgstamount())) {
								opendebitamt += accJournals.getIgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(igstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(igstPayable)){
								accntJournalsClosingMap.remove(igstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts =null;
							amts =accntJournalsClosingMap.get(cgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(cgstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
								amts = new Amounts();
							}

							if (isNotEmpty(accJournals.getCgstamount())) {
								opendebitamt += accJournals.getCgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(cgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(cgstPayable)) {
								accntJournalsClosingMap.remove(cgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = null;
							amts =accntJournalsClosingMap.get(sgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(sgstPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}

							if (isNotEmpty(accJournals.getSgstamount())) {
								opendebitamt += accJournals.getSgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(sgstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(sgstPayable)) {
								accntJournalsClosingMap.remove(sgstPayable);
							}
						}
					}else {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						
						if (isNotEmpty(vendorName)) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							Amounts amts = null;
							amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(vendorName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								opendebitamt += accJournals.getCustomerorSupplierAccount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							
							accntJournalsMap.put(vendorName, amts);
							
							if(accntJournalsClosingMap.containsKey(vendorName)){
								accntJournalsClosingMap.remove(vendorName);
							}
						}
						
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(tdsReceivables);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount())) {
								opendebitamt += accJournals.getTdsamount();
							}
							openopeningamt = opendebitamt - openclosingamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(tdsReceivables, amts);
							
							if(accntJournalsClosingMap.containsKey(tdsReceivables)){
								accntJournalsClosingMap.remove(tdsReceivables);
							}
						}
						
						String ledgerName =null;
						
						if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
							ledgerName ="Cash";
						}else {
							ledgerName ="Sales";
						}
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(ledgerName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								opencreditamt += accJournals.getSalesorPurchases();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(ledgerName, amts);
							
							if(accntJournalsClosingMap.containsKey(ledgerName)) {
								accntJournalsClosingMap.remove(ledgerName);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(igstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(igstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							
							if (isNotEmpty(accJournals.getIgstamount())) {
								opencreditamt += accJournals.getIgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(igstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(igstPayable)){
								accntJournalsClosingMap.remove(igstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts =null;
							amts =accntJournalsClosingMap.get(cgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(cgstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
								amts = new Amounts();
							}
							
							if (isNotEmpty(accJournals.getCgstamount())) {
								opencreditamt += accJournals.getCgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(cgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(cgstPayable)) {
								accntJournalsClosingMap.remove(cgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = null;
							amts =accntJournalsClosingMap.get(sgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(sgstPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}
							
							if (isNotEmpty(accJournals.getSgstamount())) {
								opencreditamt += accJournals.getSgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(sgstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(sgstPayable)) {
								accntJournalsClosingMap.remove(sgstPayable);
							}
						}
					}
					// GSTR1 end
				}else {
					String tdsPayable = "TDS Payable";
					String igstReceivable_rcm = "Input IGST (RCM)";
					String cgstReceivable_rcm = "Input CGST (RCM)";
					String sgstReceivable_rcm = "Input SGST (RCM)";

					String igstReceivable = "Input IGST";
					String cgstReceivable = "Input CGST";
					String sgstReceivable = "Input SGST";
					String cessReceivable = "CESS Receivable";
					
					String igstPayable_rcm = "Output IGST (RCM)";
					String cgstPayable_rcm = "Output CGST (RCM)";
					String sgstPayable_rcm = "Output SGST (RCM)";

					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String cessPayable = "CESS Payable";
					
					String tcsReceivable= "TDS Receivables";
					
					if("RCM".equalsIgnoreCase(accJournals.getRcmorinterorintra())) {
						if("true".equalsIgnoreCase(accJournals.getItcinelg())) {
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)){
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getInterorintra())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(cgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(cgstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getCgstamount())) {
										opendebitamt += accJournals.getCgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(cgstReceivable_rcm, amts);
									
									if(accntJournalsClosingMap.containsKey(cgstReceivable_rcm)) {
										accntJournalsClosingMap.remove(cgstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(sgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(sgstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getSgstamount())) {
										opendebitamt += accJournals.getSgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(sgstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(sgstReceivable_rcm)) {
										accntJournalsClosingMap.remove(sgstReceivable_rcm);
									}
								}
								
								if (isNotEmpty(accJournals.getRcgstamount()) && accJournals.getRcgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(cgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(cgstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getRcgstamount())) {
										opencreditamt += accJournals.getRcgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(cgstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(cgstPayable_rcm)) {
										accntJournalsClosingMap.remove(cgstPayable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getRsgstamount()) && accJournals.getRsgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(sgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(sgstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getRsgstamount())) {
										opencreditamt += accJournals.getRsgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(sgstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(sgstPayable_rcm)) {
										accntJournalsClosingMap.remove(sgstPayable_rcm);
									}
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.remove(igstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstPayable_rcm)) {
										accntJournalsClosingMap.remove(igstPayable_rcm);
									}
								}
							}
						}
					}else {
						if ("Nil Supplies".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = null;
							if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
									"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
								ledgerName = "Cash";						
							}else{
								ledgerName = "Expenditure";
							}
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else if ("Import Services".equalsIgnoreCase(accJournals.getInvoiceType())) {
							if("Intra".equalsIgnoreCase(accJournals.getInvoiceType())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.remove(igstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									
									if(accntJournalsClosingMap.containsKey(vendorName)){
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
									
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts= accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts= accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.put(igstReceivable_rcm, amts);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstPayable_rcm)) {
										accntJournalsClosingMap.remove(igstPayable_rcm);
									}
								}
							}
						}else if("Import Goods".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							//not itcinelg
							if(!"true".equalsIgnoreCase(accJournals.getItcinelg())) {
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(igstReceivable, amts);
									
									if(accntJournalsClosingMap.containsKey(igstReceivable)) {
										accntJournalsClosingMap.remove(igstReceivable);
									}
								}
							}
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = "Expenditure";
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else if("ITC Reversal".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIgstamount())) {
									opencreditamt += accJournals.getIgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(igstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(igstReceivable)) {
									accntJournalsClosingMap.remove(igstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCgstamount())) {
									opencreditamt += accJournals.getCgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
									accntJournalsClosingMap.remove(cgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(sgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getSgstamount())) {
									opencreditamt += accJournals.getSgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(sgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
									accntJournalsClosingMap.remove(sgstReceivable);
								}
							}
							
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cessReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCessAmount())) {
									opencreditamt += accJournals.getCessAmount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cessReceivable, amts);
								
								if(accntJournalsClosingMap.containsKey(cessReceivable)) {
									accntJournalsClosingMap.remove(cessReceivable);
								}
							}
						}else if("ISD".equalsIgnoreCase(accJournals.getInvoiceType())) {
							//Always LedgerName Credit
							String ledgerName = "HO-ISD";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opencreditamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIgstamount())) {
									opendebitamt += accJournals.getIgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(igstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(igstReceivable)) {
									accntJournalsClosingMap.remove(igstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCgstamount())) {
									opendebitamt += accJournals.getCgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
									accntJournalsClosingMap.remove(cgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(sgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getSgstamount())) {
									opendebitamt += accJournals.getSgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(sgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
									accntJournalsClosingMap.remove(sgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cessReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCessAmount())) {
									opendebitamt += accJournals.getCessAmount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cessReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cessReceivable)) {
									accntJournalsClosingMap.remove(cessReceivable);
								}
							}
							
							if (isNotEmpty(accJournals.getIsdineligiblecredit()) && accJournals.getIsdineligiblecredit() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get("Isdineligiblecredit Receivable");
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get("Isdineligiblecredit Receivable");
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIsdineligiblecredit())) {
									opendebitamt += accJournals.getIsdineligiblecredit();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put("Isdineligiblecredit Receivable", amts);
								if(accntJournalsClosingMap.containsKey("Isdineligiblecredit Receivable")) {
									accntJournalsClosingMap.remove("Isdineligiblecredit Receivable");
								}
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getRcmorinterorintra())){
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opendebitamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opencreditamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opendebitamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opendebitamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opencreditamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opendebitamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(cgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getCgstamount())) {
													opencreditamt += accJournals.getCgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(cgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
													accntJournalsClosingMap.remove(cgstReceivable);
												}
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(sgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getSgstamount())) {
													opencreditamt += accJournals.getSgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(sgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
													accntJournalsClosingMap.remove(sgstReceivable);
												}
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opencreditamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opendebitamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opencreditamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(cgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getCgstamount())) {
													opendebitamt += accJournals.getCgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(cgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
													accntJournalsClosingMap.remove(cgstReceivable);
												}
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(sgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getSgstamount())) {
													opendebitamt += accJournals.getSgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(sgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
													accntJournalsClosingMap.remove(sgstReceivable);
												}
											}
										}
									}
								}
							}else {
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opendebitamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opencreditamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opendebitamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {										
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {										
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts= accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts= accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opendebitamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opencreditamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName, amts);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opendebitamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts =accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts =accntJournalsMap.get(igstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getIgstamount())) {
													opencreditamt += accJournals.getIgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(igstReceivable, amts);
												
												if(accntJournalsClosingMap.containsKey(igstReceivable)) {
													accntJournalsClosingMap.remove(igstReceivable);
												}
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opencreditamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opendebitamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName, amts);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opencreditamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts =accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts =accntJournalsMap.get(igstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getIgstamount())) {
													opendebitamt += accJournals.getIgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(igstReceivable, amts);
												
												if(accntJournalsClosingMap.containsKey(igstReceivable)) {
													accntJournalsClosingMap.remove(igstReceivable);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (isNotEmpty(accntJournalsClosingMap)) {
			accntJournalsMap.putAll(accntJournalsClosingMap);
		}
		//System.out.println(accntJournalsMap);
		return accntJournalsMap;
	}

	@Override
	public List<AccountingJournal> getLedgersData(String ledgername, String clientid, List<String> invoiceMonth) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
				
		Query query=new  Query();
		Criteria criteria=new Criteria();
		
		query.addCriteria(
				new Criteria().orOperator(
						Criteria.where("ledgerName").is(ledgername),
						Criteria.where("vendorName").is(ledgername)
				)
		);
		query.addCriteria(Criteria.where("returnType").in("GSTR1","GSTR2"));
		query.addCriteria(Criteria.where("invoiceMonth").in(invoiceMonth));
		query.addCriteria(criteria.where("clientId").is(clientid));
		query.addCriteria(criteria.where("status").exists(false));
		
		List<AccountingJournal> invoiceJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");
		
		List<String> returntType=Arrays.asList("GSTR1","GSTR2");
		List<String> voucherJournalsType=Arrays.asList("Voucher");
		List<AccountingJournal> voucherJournals=accountingJournalRepository.findByClientIdAndVoucheritems_LedgerAndInvoiceMonthInAndReturnTypeIn(clientid, ledgername, invoiceMonth, voucherJournalsType);
		
		List<String> contraJournalsType=Arrays.asList("Contra");
		List<AccountingJournal> contraJournals=accountingJournalRepository.findByClientIdAndContraitems_LedgerAndInvoiceMonthInAndReturnTypeIn(clientid, ledgername, invoiceMonth, contraJournalsType);

		List<String> payment_ReceiptsJournalsType=Arrays.asList("Payment Receipt","Payment");
		
		Query query1=new  Query();
		
		query1.addCriteria(
				new Criteria().orOperator(
						Criteria.where("paymentitems_ledgerName").is(ledgername),
						Criteria.where("vendorName").is(ledgername)
				)
		);
		query1.addCriteria(Criteria.where("returnType").in(payment_ReceiptsJournalsType));
		query1.addCriteria(Criteria.where("invoiceMonth").in(invoiceMonth));
		query1.addCriteria(Criteria.where("clientId").is(clientid));
		
		List<AccountingJournal> paymentsAndPaymentsReceiptsJournals = mongoTemplate.find(query1, AccountingJournal.class, "accounting_journal");
		
		List<AccountingJournal> JournalsLst=new ArrayList<>();
		
		if(isNotEmpty(invoiceJournals)) { 
			JournalsLst.addAll(invoiceJournals); 
		}
		if(isNotEmpty(voucherJournals)) {
			JournalsLst.addAll(voucherJournals);			
		}
		if(isNotEmpty(contraJournals)) {
			JournalsLst.addAll(contraJournals);
		}
		if(isNotEmpty(paymentsAndPaymentsReceiptsJournals)) {
			JournalsLst.addAll(paymentsAndPaymentsReceiptsJournals);
		}
		
		return JournalsLst;
	}
	
	@Override
	public List<AccountingJournal> getCustomLedgersData(String ledgername, String clientid, Date stDate, Date endDate) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
				
		Query query=new  Query();
		Criteria criteria=new Criteria();
		
		query.addCriteria(
				new Criteria().orOperator(
						Criteria.where("ledgerName").is(ledgername),
						Criteria.where("vendorName").is(ledgername)
				)
		);
		query.addCriteria(Criteria.where("returnType").in("GSTR1","GSTR2"));
		query.addCriteria(Criteria.where("dateofinvoice").gte(stDate).lt(endDate));
		query.addCriteria(criteria.where("clientId").is(clientid));
		query.addCriteria(criteria.where("status").exists(false));
		
		List<AccountingJournal> invoiceJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");
		
		List<String> voucherJournalsType=Arrays.asList("Voucher");
		List<AccountingJournal> voucherJournals=accountingJournalRepository.findByClientIdAndVoucheritems_LedgerAndReturnTypeInAndDateofinvoiceBetween(clientid, ledgername, voucherJournalsType, stDate, endDate);
		
		List<String> contraJournalsType=Arrays.asList("Contra");
		List<AccountingJournal> contraJournals=accountingJournalRepository.findByClientIdAndContraitems_LedgerAndReturnTypeInAndDateofinvoiceBetween(clientid, ledgername, contraJournalsType, stDate, endDate);

		query=new  Query();
		
		query.addCriteria(
				new Criteria().orOperator(
						Criteria.where("paymentitems_ledgerName").is(ledgername),
						Criteria.where("vendorName").is(ledgername)
				)
		);
		List<String> payment_ReceiptsJournalsType=Arrays.asList("Payment Receipt","Payment");
		query.addCriteria(Criteria.where("returnType").in(payment_ReceiptsJournalsType));
		query.addCriteria(Criteria.where("dateofinvoice").gte(stDate).lt(endDate));
		query.addCriteria(Criteria.where("clientId").is(clientid));
		
		List<AccountingJournal> paymentsAndPaymentsReceiptsJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");
		
		List<AccountingJournal> JournalsLst=new ArrayList<>();
		
		if(isNotEmpty(invoiceJournals)) { 
			JournalsLst.addAll(invoiceJournals); 
		}
		if(isNotEmpty(voucherJournals)) {
			JournalsLst.addAll(voucherJournals);			
		}
		if(isNotEmpty(contraJournals)) {
			JournalsLst.addAll(contraJournals);
		}
		if(isNotEmpty(paymentsAndPaymentsReceiptsJournals)) {
			JournalsLst.addAll(paymentsAndPaymentsReceiptsJournals);
		}
		
		return JournalsLst;
	}
	
	
	public Map<String, Amounts> newCalculateLedgersAmounts(List<AccountingJournal> accJournalsClosingLst, List<AccountingJournal> accJournalsOpeningLst) {

		Map<String, Amounts> accntJournalsClosingMap = new HashMap<String, Amounts>();
		Map<String, Amounts> accntJournalsMap = new HashMap<String, Amounts>();
		if(isNotEmpty(accJournalsClosingLst)) {
			for(AccountingJournal accJournals :accJournalsClosingLst) {
				if ("Contra".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getContraitems())) {
						for (AccountingJournalVoucherItems contra : accJournals.getContraitems()) {
							Amounts amts = accntJournalsClosingMap.get(contra.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(contra.getLedger(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
	
								accntJournalsClosingMap.put(contra.getLedger(), amts);
							}
						}
					}
				} else if ("Voucher".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVoucheritems())) {
						for (AccountingJournalVoucherItems voucher : accJournals.getVoucheritems()) {
							Amounts amts = accntJournalsClosingMap.get(voucher.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(voucher.getLedger(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
	
								accntJournalsClosingMap.put(voucher.getLedger(), amts);
							}
						}
					}
				} else if ("Payment Receipt".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt + amts.getOpeningamt());
							amts.setDebitamt(debitamt + amts.getDebitamt());
							amts.setCreditamt(creditamt + amts.getCreditamt());
							amts.setClosingamt(closingamt + amts.getClosingamt());
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						} else {
							amts = new Amounts();
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							}
						}
					}
				} else if ("Payment".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt + amts.getOpeningamt());
							amts.setDebitamt(debitamt + amts.getDebitamt());
							amts.setCreditamt(creditamt + amts.getCreditamt());
							amts.setClosingamt(closingamt + amts.getClosingamt());
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						} else {
							amts = new Amounts();
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							closingamt =openingamt+ debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amts);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amts);
							}
						}
					}
				} else if (MasterGSTConstants.GSTR1.equalsIgnoreCase(accJournals.getReturnType())) {
					
					String tdsReceivables = "TDS Receivables";
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String doctype=null;
					
					if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
						doctype=accJournals.getCreditDebitNoteType();
					}
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
							MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
		
						if (isNotEmpty(vendorName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								creditamt += accJournals.getCustomerorSupplierAccount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(vendorName, amts);
						}
		
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getTdsamount())) {
								creditamt += accJournals.getTdsamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tdsReceivables, amts);
						}
		
						
						if(isNotEmpty(accJournals.getItems()) && accJournals.getItems().size() > 0) {
							for(Item item : accJournals.getItems()) {
								String ledgerName ="Sales";
								
								if (isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										debitamt += item.getTaxablevalue();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(igstPayable);
				
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									debitamt += item.getIgstamount();
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable, amts);
								}
								if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(cgstPayable);
				
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									debitamt += item.getCgstamount();
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstPayable, amts);
								}
								if (isNotEmpty(item.getSgstamount()) && item.getSgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(sgstPayable);
				
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									debitamt += item.getSgstamount();
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstPayable, amts);
								}
							}
						}else {
						
							String ledgerName ="Sales";
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(igstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getIgstamount())) {
									debitamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cgstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getCgstamount())) {
									debitamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(sgstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getSgstamount())) {
									debitamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstPayable, amts);
							}
						}
						
						
						
					}else {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
		
						if (isNotEmpty(vendorName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								debitamt += accJournals.getCustomerorSupplierAccount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(vendorName, amts);
						}
		
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
		
							if (isNotEmpty(accJournals.getTdsamount())) {
								debitamt += accJournals.getTdsamount();
							}
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tdsReceivables, amts);
						}
						
						
						if(isNotEmpty(accJournals.getItems()) && accJournals.getItems().size() > 0) {
							for(Item item : accJournals.getItems()) {
								String ledgerName =null;
								if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
									ledgerName ="Cash";
								}else {
									ledgerName ="Sales";
								}
								if (isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt += item.getTaxablevalue();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(item.getIgstamount()) && item.getIgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(igstPayable);
				
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									creditamt += accJournals.getIgstamount();
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable, amts);
								}
								if (isNotEmpty(item.getCgstamount()) && item.getCgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(cgstPayable);
				
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
				
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									creditamt += item.getCgstamount();
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstPayable, amts);
								}
								if (isNotEmpty(item.getSgstamount()) && item.getSgstamount() > 0) {
									Amounts amts = accntJournalsClosingMap.get(sgstPayable);
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(item.getSgstamount())) {
										creditamt += item.getSgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstPayable, amts);
								}
							}
						}else {
							String ledgerName =null;
							
							if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
								ledgerName ="Cash";
							}else {
								ledgerName ="Sales";
							}
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									creditamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(igstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getIgstamount())) {
									creditamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cgstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getCgstamount())) {
									creditamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(sgstPayable);
			
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
			
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
			
								if (isNotEmpty(accJournals.getSgstamount())) {
									creditamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstPayable, amts);
							}
						}
						
						
					}
					// GSTR1 end
				} else {
					String tdsPayable = "TDS Payable";
					String igstReceivable_rcm = "Input IGST (RCM)";
					String cgstReceivable_rcm = "Input CGST (RCM)";
					String sgstReceivable_rcm = "Input SGST (RCM)";
	
					String igstReceivable = "Input IGST";
					String cgstReceivable = "Input CGST";
					String sgstReceivable = "Input SGST";
					String cessReceivable = "CESS Receivable";
					
					String igstPayable_rcm = "Output IGST (RCM)";
					String cgstPayable_rcm = "Output CGST (RCM)";
					String sgstPayable_rcm = "Output SGST (RCM)";
	
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String cessPayable = "CESS Payable";
					
					String tcsReceivable= "TCS Receivable";
					
					if("RCM".equalsIgnoreCase(accJournals.getRcmorinterorintra())) {
						if("true".equalsIgnoreCase(accJournals.getItcinelg())) {
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getInterorintra())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(cgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getCgstamount())) {
										debitamt += accJournals.getCgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(sgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getSgstamount())) {
										debitamt += accJournals.getSgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstReceivable_rcm, amts);
								}
								
								if (isNotEmpty(accJournals.getRcgstamount()) && accJournals.getRcgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(cgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getRcgstamount())) {
										creditamt += accJournals.getRcgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(cgstPayable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getRsgstamount()) && accJournals.getRsgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(sgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getRsgstamount())) {
										creditamt += accJournals.getRsgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(sgstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}
						}
					}else {
						if ("Nil Supplies".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = null;
							if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
								ledgerName = "Cash";						
							}else{
								ledgerName = "Expenditure";
							}
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else if ("Import Services".equalsIgnoreCase(accJournals.getInvoiceType())) {
							if("Intra".equalsIgnoreCase(accJournals.getInvoiceType())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getTdsamount())) {
										creditamt += accJournals.getTdsamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(tdsPayable, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable_rcm, amts);
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										creditamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}
						}else if("Import Goods".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							//not itcinelg
							if(!"true".equalsIgnoreCase(accJournals.getItcinelg())) {
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
									Amounts amts = accntJournalsClosingMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();
									}
	
									if (isNotEmpty(accJournals.getIgstamount())) {
										debitamt += accJournals.getIgstamount();
									}
									closingamt = openingamt + debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(igstReceivable, amts);
								}
							}
							
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									creditamt += accJournals.getCustomerorSupplierAccount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
							
							String ledgerName = "Expenditure";
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getTdsamount())) {
									creditamt += accJournals.getTdsamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tdsPayable, amts);
							}
						}else if("ITC Reversal".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIgstamount())) {
									creditamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCgstamount())) {
									creditamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getSgstamount())) {
									creditamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstReceivable, amts);
							}
							
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCessAmount())) {
									creditamt += accJournals.getCessAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessReceivable, amts);
							}
						}else if("ISD".equalsIgnoreCase(accJournals.getInvoiceType())) {
							String ledgerName = "HO-ISD";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(ledgerName, amts);
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIgstamount())) {
									creditamt += accJournals.getIgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCgstamount())) {
									creditamt += accJournals.getCgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getSgstamount())) {
									creditamt += accJournals.getSgstamount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstReceivable, amts);
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getCessAmount())) {
									creditamt += accJournals.getCessAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessReceivable, amts);
							}
							
							if (isNotEmpty(accJournals.getIsdineligiblecredit()) && accJournals.getIsdineligiblecredit() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
								Amounts amts = accntJournalsClosingMap.get("Ineligible GST Credit Receivable");
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
	
								if (isNotEmpty(accJournals.getIsdineligiblecredit())) {
									creditamt += accJournals.getIsdineligiblecredit();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put("Ineligible GST Credit Receivable", amts);
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getRcmorinterorintra())){
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												debitamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												creditamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												debitamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
	
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													debitamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													creditamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													debitamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getCgstamount())) {
													creditamt += accJournals.getCgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(cgstReceivable, amts);
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getSgstamount())) {
													creditamt += accJournals.getSgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(sgstReceivable, amts);
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													creditamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													debitamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getTdsamount())) {
													creditamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCgstamount())) {
													debitamt += accJournals.getCgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(cgstReceivable, amts);
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSgstamount())) {
													debitamt += accJournals.getSgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(sgstReceivable, amts);
											}
										}
									}
								}
							}else {
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												debitamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												creditamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												debitamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
		
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												creditamt += accJournals.getCustomerorSupplierAccount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(vendorName, amts);
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												debitamt += accJournals.getSalesorPurchases();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(ledgerName, amts);
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
	
											Amounts amts = accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts = new Amounts();
											}
	
											if (isNotEmpty(accJournals.getTdsamount())) {
												creditamt += accJournals.getTdsamount();
											}
											closingamt = openingamt + debitamt - creditamt;
											amts.setOpeningamt(openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(closingamt);
											accntJournalsClosingMap.put(tdsPayable, amts);
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
											
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													debitamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													creditamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													debitamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getIgstamount())) {
													creditamt += accJournals.getIgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(igstReceivable, amts);
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													creditamt += accJournals.getCustomerorSupplierAccount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(vendorName, amts);
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													debitamt += accJournals.getSalesorPurchases();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(ledgerName, amts);
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getTdsamount())) {
													creditamt += accJournals.getTdsamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
												Amounts amts = accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();
												}
		
												if (isNotEmpty(accJournals.getIgstamount())) {
													debitamt += accJournals.getIgstamount();
												}
												closingamt = openingamt + debitamt - creditamt;
												amts.setOpeningamt(openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(closingamt);
												accntJournalsClosingMap.put(igstReceivable, amts);
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if(isNotEmpty(accJournalsOpeningLst)) {
			for(AccountingJournal accJournals :accJournalsOpeningLst) {
				if ("Contra".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getContraitems())) {
						for (AccountingJournalVoucherItems contra : accJournals.getContraitems()) {
							
							double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(contra.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(contra.getDramount())) {
									opendebitamt+= contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									opencreditamt+= contra.getCramount();
								}
								
								openopeningamt = opendebitamt - opencreditamt;
								
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(contra.getLedger(), amts);
								accntJournalsClosingMap.remove(contra.getLedger());
							} else {
								Amounts amtss = accntJournalsMap.get(contra.getLedger());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(contra.getDramount())) {
									debitamt = contra.getDramount();
								}
								if (isNotEmpty(contra.getCramount())) {
									creditamt = contra.getCramount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								amtss.setClosingamt(openopeningamt+closingamt);

								accntJournalsMap.put(contra.getLedger(), amtss);
							}
						}
					}
				}else if ("Voucher".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVoucheritems())) {
						for (AccountingJournalVoucherItems voucher : accJournals.getVoucheritems()) {
							
							double openopeningamt=0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(voucher.getLedger());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(voucher.getDramount())) {
									opendebitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									opencreditamt = voucher.getCramount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(opendebitamt + amts.getDebitamt());
								amts.setCreditamt(opencreditamt + amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(voucher.getLedger(), amts);
								accntJournalsClosingMap.remove(voucher.getLedger());
							} else {
								Amounts amtss = accntJournalsMap.get(voucher.getLedger());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(voucher.getDramount())) {
									debitamt = voucher.getDramount();
								}
								if (isNotEmpty(voucher.getCramount())) {
									creditamt = voucher.getCramount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								
								amtss.setClosingamt(openopeningamt+closingamt);

								accntJournalsMap.put(voucher.getLedger(), amtss);
							}
						}
					}
				}else if ("Payment Receipt".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						
						double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
						
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								opencreditamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt =opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
							amts.setDebitamt(amts.getDebitamt());
							amts.setCreditamt(amts.getCreditamt());
							amts.setClosingamt(openopeningamt + amts.getClosingamt());
							
							accntJournalsMap.put(accJournals.getVendorName(), amts);
							
							accntJournalsClosingMap.remove(accJournals.getVendorName());
						} else {
							Amounts amtss = accntJournalsMap.get(accJournals.getVendorName());
							if(isNotEmpty(amtss)) {
								amtss.setDebitamt(amtss.getDebitamt());
								amtss.setCreditamt(amtss.getCreditamt());
								openingamt+=amtss.getOpeningamt();
								closingamt+=amtss.getClosingamt();
							}else {
								amtss = new Amounts();
								amtss.setDebitamt(0);
								amtss.setCreditamt(0);
							}
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								creditamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt =debitamt - creditamt;
							amtss.setOpeningamt(openopeningamt+openingamt);
							
							amtss.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(accJournals.getVendorName(), amtss);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								accntJournalsMap.put(payemtItems.getLedgerName(), amts);
								
								accntJournalsClosingMap.remove(payemtItems.getLedgerName());
							} else {
								Amounts amtss = accntJournalsMap.get(payemtItems.getLedgerName());
								if(isNotEmpty(amtss)) {
									amtss.setDebitamt(amtss.getDebitamt());
									amtss.setCreditamt(amtss.getCreditamt());
									
									openingamt+=amtss.getOpeningamt();
									closingamt+=amtss.getClosingamt();
								}else {
									amtss = new Amounts();																
									amtss.setDebitamt(0);
									amtss.setCreditamt(0);
								}
								if (isNotEmpty(payemtItems.getAmount())) {
									debitamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt+openingamt);
								
								amtss.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(payemtItems.getLedgerName(), amtss);
							}
						}
					}
				}else if ("Payment".equals(accJournals.getReturnType())) {
					if (isNotEmpty(accJournals.getVendorName())) {
						
						double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
						
						Amounts amts = accntJournalsClosingMap.get(accJournals.getVendorName());
						double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
						if (isNotEmpty(amts)) {
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt = debitamt - creditamt;
							amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
							amts.setDebitamt(amts.getDebitamt());
							amts.setCreditamt(amts.getCreditamt());
							amts.setClosingamt(openopeningamt + amts.getClosingamt());
							accntJournalsMap.put(accJournals.getVendorName(), amts);
							accntJournalsClosingMap.remove(accJournals.getVendorName());
						} else {
							Amounts amtss = accntJournalsMap.get(accJournals.getVendorName());
							if(isNotEmpty(amtss)) {
								amtss.setDebitamt(amtss.getDebitamt());
								amtss.setCreditamt(amtss.getCreditamt());
								openingamt+=amtss.getOpeningamt();
								closingamt+=amtss.getClosingamt();
							}else {
								amtss = new Amounts();																
								amtss.setDebitamt(0);
								amtss.setCreditamt(0);
							}
							if (isNotEmpty(accJournals.getPaymentReceivedAmount())) {
								debitamt = accJournals.getPaymentReceivedAmount();
							}
							openopeningamt = debitamt - creditamt;
							amtss.setOpeningamt(openopeningamt+openingamt);
							
							amtss.setClosingamt(openopeningamt+closingamt);
							accntJournalsClosingMap.put(accJournals.getVendorName(), amtss);
						}
					}
					if (isNotEmpty(accJournals.getPaymentitems())) {
						for (AccountingJournalPaymentItems payemtItems : accJournals.getPaymentitems()) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts = accntJournalsClosingMap.get(payemtItems.getLedgerName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								openopeningamt = debitamt - creditamt;
								amts.setOpeningamt(openopeningamt + amts.getOpeningamt());
								amts.setDebitamt(amts.getDebitamt());
								amts.setCreditamt(amts.getCreditamt());
								amts.setClosingamt(openopeningamt + amts.getClosingamt());
								
								accntJournalsMap.put(payemtItems.getLedgerName(), amts);
								accntJournalsClosingMap.remove(payemtItems.getLedgerName());
							} else {
								Amounts amtss = accntJournalsMap.get(accJournals.getLedgerName());
								if(isEmpty(amtss)) {
									amtss = new Amounts();																
								}
								if (isNotEmpty(payemtItems.getAmount())) {
									creditamt = payemtItems.getAmount();
								}
								openopeningamt =debitamt - creditamt;
								amtss.setOpeningamt(openopeningamt);
								amtss.setDebitamt(debitamt);
								amtss.setCreditamt(creditamt);
								amtss.setClosingamt(openopeningamt);
								accntJournalsClosingMap.put(payemtItems.getLedgerName(), amtss);
							}
						}
					}
				}else if (MasterGSTConstants.GSTR1.equalsIgnoreCase(accJournals.getReturnType())) {

					String tdsReceivables = "TDS Receivables";
					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String doctype=null;
					if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
						doctype=accJournals.getCreditDebitNoteType();
					}
					
					if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) ||
							MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && (isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype))) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							Amounts amts = null;
							 amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(vendorName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								opencreditamt += accJournals.getCustomerorSupplierAccount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							
							accntJournalsMap.put(vendorName, amts);
							
							if(accntJournalsClosingMap.containsKey(vendorName)){
								accntJournalsClosingMap.remove(vendorName);
							}
						}

						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(tdsReceivables);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}

							if (isNotEmpty(accJournals.getTdsamount())) {
								opencreditamt += accJournals.getTdsamount();
							}
							openopeningamt = opendebitamt - openclosingamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(tdsReceivables, amts);
							
							if(accntJournalsClosingMap.containsKey(tdsReceivables)){
								accntJournalsClosingMap.remove(tdsReceivables);
							}
						}

						String ledgerName ="Sales";
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(ledgerName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								opendebitamt += accJournals.getSalesorPurchases();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(ledgerName, amts);
							
							if(accntJournalsClosingMap.containsKey(ledgerName)) {
								accntJournalsClosingMap.remove(ledgerName);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(igstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(igstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}

							if (isNotEmpty(accJournals.getIgstamount())) {
								opendebitamt += accJournals.getIgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(igstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(igstPayable)){
								accntJournalsClosingMap.remove(igstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts =null;
							amts =accntJournalsClosingMap.get(cgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(cgstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
								amts = new Amounts();
							}

							if (isNotEmpty(accJournals.getCgstamount())) {
								opendebitamt += accJournals.getCgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(cgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(cgstPayable)) {
								accntJournalsClosingMap.remove(cgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = null;
							amts =accntJournalsClosingMap.get(sgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(sgstPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}

							if (isNotEmpty(accJournals.getSgstamount())) {
								opendebitamt += accJournals.getSgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(sgstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(sgstPayable)) {
								accntJournalsClosingMap.remove(sgstPayable);
							}
						}
					}else {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						
						if (isNotEmpty(vendorName)) {
							
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							Amounts amts = null;
							amts = accntJournalsClosingMap.get(vendorName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(vendorName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
								opendebitamt += accJournals.getCustomerorSupplierAccount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							
							accntJournalsMap.put(vendorName, amts);
							
							if(accntJournalsClosingMap.containsKey(vendorName)){
								accntJournalsClosingMap.remove(vendorName);
							}
						}
						
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(tdsReceivables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(tdsReceivables);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount())) {
								opendebitamt += accJournals.getTdsamount();
							}
							openopeningamt = opendebitamt - openclosingamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(tdsReceivables, amts);
							
							if(accntJournalsClosingMap.containsKey(tdsReceivables)){
								accntJournalsClosingMap.remove(tdsReceivables);
							}
						}
						
						String ledgerName =null;
						
						if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType())) {
							ledgerName ="Cash";
						}else {
							ledgerName ="Sales";
						}
						if (isNotEmpty(accJournals.getLedgerName())) {
							ledgerName = accJournals.getLedgerName();
						}
						
						if (isNotEmpty(ledgerName)) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(ledgerName);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(ledgerName);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							if (isNotEmpty(accJournals.getSalesorPurchases())) {
								opencreditamt += accJournals.getSalesorPurchases();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(ledgerName, amts);
							
							if(accntJournalsClosingMap.containsKey(ledgerName)) {
								accntJournalsClosingMap.remove(ledgerName);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts =null;
							amts=accntJournalsClosingMap.get(igstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts=accntJournalsMap.get(igstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
							}
							
							if (isNotEmpty(accJournals.getIgstamount())) {
								opencreditamt += accJournals.getIgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(igstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(igstPayable)){
								accntJournalsClosingMap.remove(igstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts =null;
							amts =accntJournalsClosingMap.get(cgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(cgstPayable);
								if(isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								}else {
									amts = new Amounts();							
								}
								amts = new Amounts();
							}
							
							if (isNotEmpty(accJournals.getCgstamount())) {
								opencreditamt += accJournals.getCgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(cgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(cgstPayable)) {
								accntJournalsClosingMap.remove(cgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = null;
							amts =accntJournalsClosingMap.get(sgstPayable);
							double openopeningamt =0, opendebitamt = 0, opencreditamt = 0, openclosingamt=0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts =accntJournalsMap.get(sgstPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}
							
							if (isNotEmpty(accJournals.getSgstamount())) {
								opencreditamt += accJournals.getSgstamount();
							}
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt+openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt+closingamt);
							accntJournalsMap.put(sgstPayable, amts);
							
							if(accntJournalsClosingMap.containsKey(sgstPayable)) {
								accntJournalsClosingMap.remove(sgstPayable);
							}
						}
					}
					// GSTR1 end
				}else {
					String tdsPayable = "TDS Payable";
					String igstReceivable_rcm = "Input IGST (RCM)";
					String cgstReceivable_rcm = "Input CGST (RCM)";
					String sgstReceivable_rcm = "Input SGST (RCM)";

					String igstReceivable = "Input IGST";
					String cgstReceivable = "Input CGST";
					String sgstReceivable = "Input SGST";
					String cessReceivable = "Input CESS";
					
					String igstPayable_rcm = "Output IGST (RCM)";
					String cgstPayable_rcm = "Output CGST (RCM)";
					String sgstPayable_rcm = "Output SGST (RCM)";

					String igstPayable = "Output IGST";
					String cgstPayable = "Output CGST";
					String sgstPayable = "Output SGST";
					String cessPayable = "CESS Payable";
					
					String tcsReceivable= "TDS Receivables";
					
					if("RCM".equalsIgnoreCase(accJournals.getRcmorinterorintra())) {
						if("true".equalsIgnoreCase(accJournals.getItcinelg())) {
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)){
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getInterorintra())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(cgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(cgstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getCgstamount())) {
										opendebitamt += accJournals.getCgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(cgstReceivable_rcm, amts);
									
									if(accntJournalsClosingMap.containsKey(cgstReceivable_rcm)) {
										accntJournalsClosingMap.remove(cgstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(sgstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(sgstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getSgstamount())) {
										opendebitamt += accJournals.getSgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(sgstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(sgstReceivable_rcm)) {
										accntJournalsClosingMap.remove(sgstReceivable_rcm);
									}
								}
								
								if (isNotEmpty(accJournals.getRcgstamount()) && accJournals.getRcgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(cgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(cgstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getRcgstamount())) {
										opencreditamt += accJournals.getRcgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(cgstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(cgstPayable_rcm)) {
										accntJournalsClosingMap.remove(cgstPayable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getRsgstamount()) && accJournals.getRsgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(sgstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(sgstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getRsgstamount())) {
										opencreditamt += accJournals.getRsgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(sgstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(sgstPayable_rcm)) {
										accntJournalsClosingMap.remove(sgstPayable_rcm);
									}
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.remove(igstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstPayable_rcm)) {
										accntJournalsClosingMap.remove(igstPayable_rcm);
									}
								}
							}
						}
					}else {
						if ("Nil Supplies".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
								"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = null;
							if ("Advances".equalsIgnoreCase(accJournals.getInvoiceType()) ||
									"Advance Adjusted Detail".equalsIgnoreCase(accJournals.getInvoiceType())) {
								ledgerName = "Cash";						
							}else{
								ledgerName = "Expenditure";
							}
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else if ("Import Services".equalsIgnoreCase(accJournals.getInvoiceType())) {
							if("Intra".equalsIgnoreCase(accJournals.getInvoiceType())) {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									
									if(accntJournalsClosingMap.containsKey(vendorName)) {
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.remove(igstReceivable_rcm);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(igstPayable_rcm, amts);
								}
							}else {
								String vendorName = "Other Creditors";
								if (isNotEmpty(accJournals.getVendorName())) {
									vendorName = accJournals.getVendorName();
								}
								if (isNotEmpty(vendorName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(vendorName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										opencreditamt += accJournals.getCustomerorSupplierAccount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(vendorName, amts);
									
									if(accntJournalsClosingMap.containsKey(vendorName)){
										accntJournalsClosingMap.remove(vendorName);
									}
								}
								
								String ledgerName = "Expenditure";
								
								if (isNotEmpty(accJournals.getLedgerName())) {
									ledgerName = accJournals.getLedgerName();
								}
								if (isNotEmpty(ledgerName)) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(ledgerName);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										opendebitamt += accJournals.getSalesorPurchases();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
									
									if(accntJournalsClosingMap.containsKey(ledgerName)) {
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
								
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(tdsPayable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getTdsamount())) {
										opencreditamt += accJournals.getTdsamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(tdsPayable, amts);
									if(accntJournalsClosingMap.containsKey(tdsPayable)) {
										accntJournalsClosingMap.remove(tdsPayable);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts= accntJournalsClosingMap.get(igstReceivable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts= accntJournalsMap.get(igstReceivable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstReceivable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstReceivable_rcm)) {
										accntJournalsClosingMap.put(igstReceivable_rcm, amts);
									}
								}
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstPayable_rcm);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstPayable_rcm);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opencreditamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsMap.put(igstPayable_rcm, amts);
									if(accntJournalsClosingMap.containsKey(igstPayable_rcm)) {
										accntJournalsClosingMap.remove(igstPayable_rcm);
									}
								}
							}
						}else if("Import Goods".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							//not itcinelg
							if(!"true".equalsIgnoreCase(accJournals.getItcinelg())) {
								if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
									Amounts amts =null;
									amts=accntJournalsClosingMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										creditamt += amts.getCreditamt();
										debitamt += amts.getDebitamt();
										closingamt += amts.getClosingamt();
									} else {
										amts=accntJournalsMap.get(igstReceivable);
										if (isNotEmpty(amts)) {
											openingamt += amts.getOpeningamt();
											debitamt += amts.getDebitamt();
											creditamt += amts.getCreditamt();
											closingamt += amts.getClosingamt();
										} else {
											amts = new Amounts();									
										}
									}

									if (isNotEmpty(accJournals.getIgstamount())) {
										opendebitamt += accJournals.getIgstamount();
									}
									openopeningamt = opendebitamt - opencreditamt;
									amts.setOpeningamt(openopeningamt+openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(openopeningamt+closingamt);
									accntJournalsClosingMap.put(igstReceivable, amts);
									
									if(accntJournalsClosingMap.containsKey(igstReceivable)) {
										accntJournalsClosingMap.remove(igstReceivable);
									}
								}
							}
							
							String vendorName = "Other Creditors";
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(vendorName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(vendorName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									opencreditamt += accJournals.getCustomerorSupplierAccount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(vendorName, amts);
								if(accntJournalsClosingMap.containsKey(vendorName)) {
									accntJournalsClosingMap.remove(vendorName);
								}
							}
							
							String ledgerName = "Expenditure";
							
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(tdsPayable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(tdsPayable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getTdsamount())) {
									opencreditamt += accJournals.getTdsamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(tdsPayable, amts);
								
								if(accntJournalsClosingMap.containsKey(tdsPayable)) {
									accntJournalsClosingMap.remove(tdsPayable);
								}
							}
						}else if("ITC Reversal".equalsIgnoreCase(accJournals.getInvoiceType())) {
							
							String ledgerName = "Expenditure";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opendebitamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIgstamount())) {
									opencreditamt += accJournals.getIgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(igstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(igstReceivable)) {
									accntJournalsClosingMap.remove(igstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCgstamount())) {
									opencreditamt += accJournals.getCgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsClosingMap.put(cgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
									accntJournalsClosingMap.remove(cgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(sgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getSgstamount())) {
									opencreditamt += accJournals.getSgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(sgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
									accntJournalsClosingMap.remove(sgstReceivable);
								}
							}
							
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cessReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCessAmount())) {
									opencreditamt += accJournals.getCessAmount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cessReceivable, amts);
								
								if(accntJournalsClosingMap.containsKey(cessReceivable)) {
									accntJournalsClosingMap.remove(cessReceivable);
								}
							}
						}else if("ISD".equalsIgnoreCase(accJournals.getInvoiceType())) {
							//Always LedgerName Credit
							String ledgerName = "HO-ISD";
							if (isNotEmpty(accJournals.getLedgerName())) {
								ledgerName = accJournals.getLedgerName();
							}
							if (isNotEmpty(ledgerName)) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(ledgerName);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(ledgerName);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									opencreditamt += accJournals.getSalesorPurchases();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(ledgerName, amts);
								if(accntJournalsClosingMap.containsKey(ledgerName)) {
									accntJournalsClosingMap.remove(ledgerName);
								}
							}
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(igstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(igstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIgstamount())) {
									opendebitamt += accJournals.getIgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(igstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(igstReceivable)) {
									accntJournalsClosingMap.remove(igstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCgstamount())) {
									opendebitamt += accJournals.getCgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
									accntJournalsClosingMap.remove(cgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(sgstReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(sgstReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getSgstamount())) {
									opendebitamt += accJournals.getSgstamount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(sgstReceivable, amts);
								if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
									accntJournalsClosingMap.remove(sgstReceivable);
								}
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get(cessReceivable);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get(cessReceivable);
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getCessAmount())) {
									opendebitamt += accJournals.getCessAmount();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put(cessReceivable, amts);
								if(accntJournalsClosingMap.containsKey(cessReceivable)) {
									accntJournalsClosingMap.remove(cessReceivable);
								}
							}
							
							if (isNotEmpty(accJournals.getIsdineligiblecredit()) && accJournals.getIsdineligiblecredit() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
								Amounts amts =null;
								amts=accntJournalsClosingMap.get("Isdineligiblecredit Receivable");
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts=accntJournalsMap.get("Isdineligiblecredit Receivable");
									if (isNotEmpty(amts)) {
										openingamt += amts.getOpeningamt();
										debitamt += amts.getDebitamt();
										creditamt += amts.getCreditamt();
										closingamt += amts.getClosingamt();
									} else {
										amts = new Amounts();									
									}
								}

								if (isNotEmpty(accJournals.getIsdineligiblecredit())) {
									opendebitamt += accJournals.getIsdineligiblecredit();
								}
								openopeningamt = opendebitamt - opencreditamt;
								amts.setOpeningamt(openopeningamt+openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(openopeningamt+closingamt);
								accntJournalsMap.put("Isdineligiblecredit Receivable", amts);
								if(accntJournalsClosingMap.containsKey("Isdineligiblecredit Receivable")) {
									accntJournalsClosingMap.remove("Isdineligiblecredit Receivable");
								}
							}
						}else {
							if("Intra".equalsIgnoreCase(accJournals.getRcmorinterorintra())){
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opendebitamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opencreditamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opendebitamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opendebitamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opencreditamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opendebitamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(cgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getCgstamount())) {
													opencreditamt += accJournals.getCgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(cgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
													accntJournalsClosingMap.remove(cgstReceivable);
												}
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(sgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getSgstamount())) {
													opencreditamt += accJournals.getSgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(sgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
													accntJournalsClosingMap.remove(sgstReceivable);
												}
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opencreditamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opendebitamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opencreditamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(cgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(cgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getCgstamount())) {
													opendebitamt += accJournals.getCgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(cgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(cgstReceivable)) {
													accntJournalsClosingMap.remove(cgstReceivable);
												}
											}
											if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(sgstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(sgstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getSgstamount())) {
													opendebitamt += accJournals.getSgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(sgstReceivable, amts);
												if(accntJournalsClosingMap.containsKey(sgstReceivable)) {
													accntJournalsClosingMap.remove(sgstReceivable);
												}
											}
										}
									}
								}
							}else {
								if("true".equalsIgnoreCase(accJournals.getItcinelg())){
									String doctype=null;
									if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
										doctype=accJournals.getCreditDebitNoteType();
									}
									if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
											MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opendebitamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opencreditamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opendebitamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {										
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {										
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}
								}else {
									if("B2B Unregistered".equalsIgnoreCase(accJournals.getInvoiceType())){
										String vendorName = "Other Creditors";
										if (isNotEmpty(accJournals.getVendorName())) {
											vendorName = accJournals.getVendorName();
										}
										if (isNotEmpty(vendorName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(vendorName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
												opencreditamt += accJournals.getCustomerorSupplierAccount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(vendorName, amts);
											if(accntJournalsClosingMap.containsKey(vendorName)) {
												accntJournalsClosingMap.remove(vendorName);
											}
										}
										
										String ledgerName = "Expenditure";
										
										if (isNotEmpty(accJournals.getLedgerName())) {
											ledgerName = accJournals.getLedgerName();
										}
										if (isNotEmpty(ledgerName)) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts=accntJournalsClosingMap.get(ledgerName);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												debitamt += amts.getDebitamt();
												creditamt += amts.getCreditamt();
												closingamt += amts.getClosingamt();
											} else {
												amts=accntJournalsMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}
											if (isNotEmpty(accJournals.getSalesorPurchases())) {
												opendebitamt += accJournals.getSalesorPurchases();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(ledgerName, amts);
											
											if(accntJournalsClosingMap.containsKey(ledgerName)) {
												accntJournalsClosingMap.remove(ledgerName);
											}
										}
										
										if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
											double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
											double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
											Amounts amts =null;
											amts= accntJournalsClosingMap.get(tdsPayable);
											if (isNotEmpty(amts)) {
												openingamt += amts.getOpeningamt();
												creditamt += amts.getCreditamt();
												debitamt += amts.getDebitamt();
												closingamt += amts.getClosingamt();
											} else {
												amts= accntJournalsMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts = new Amounts();									
												}
											}

											if (isNotEmpty(accJournals.getTdsamount())) {
												opencreditamt += accJournals.getTdsamount();
											}
											openopeningamt = opendebitamt - opencreditamt;
											amts.setOpeningamt(openopeningamt+openingamt);
											amts.setDebitamt(debitamt);
											amts.setCreditamt(creditamt);
											amts.setClosingamt(openopeningamt+closingamt);
											accntJournalsMap.put(tdsPayable, amts);
											
											if(accntJournalsClosingMap.containsKey(tdsPayable)) {
												accntJournalsClosingMap.remove(tdsPayable);
											}
										}
									}else {
										String doctype=null;
										if(isNotEmpty(accJournals.getCreditDebitNoteType())) {
											doctype=accJournals.getCreditDebitNoteType();
										}
										if (MasterGSTConstants.CREDIT_DEBIT_NOTES.equalsIgnoreCase(accJournals.getInvoiceType()) || 
												MasterGSTConstants.CDNUR.equalsIgnoreCase(accJournals.getInvoiceType()) && isNotEmpty(doctype) && "C".equalsIgnoreCase(doctype)) {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opendebitamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opencreditamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName, amts);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opendebitamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts =accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts =accntJournalsMap.get(igstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getIgstamount())) {
													opencreditamt += accJournals.getIgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(igstReceivable, amts);
												
												if(accntJournalsClosingMap.containsKey(igstReceivable)) {
													accntJournalsClosingMap.remove(igstReceivable);
												}
											}
										}else {
											String vendorName = "Other Creditors";
											if (isNotEmpty(accJournals.getVendorName())) {
												vendorName = accJournals.getVendorName();
											}
											if (isNotEmpty(vendorName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(vendorName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(vendorName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
													opencreditamt += accJournals.getCustomerorSupplierAccount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(vendorName, amts);
												
												if(accntJournalsClosingMap.containsKey(vendorName)) {
													accntJournalsClosingMap.remove(vendorName);
												}
											}
											
											String ledgerName = "Expenditure";
											
											if (isNotEmpty(accJournals.getLedgerName())) {
												ledgerName = accJournals.getLedgerName();
											}
											if (isNotEmpty(ledgerName)) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(ledgerName);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													debitamt += amts.getDebitamt();
													creditamt += amts.getCreditamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(ledgerName);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
												if (isNotEmpty(accJournals.getSalesorPurchases())) {
													opendebitamt += accJournals.getSalesorPurchases();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(ledgerName, amts);
												
												if(accntJournalsClosingMap.containsKey(ledgerName)) {
													accntJournalsClosingMap.remove(ledgerName, amts);
												}
											}
											
											if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts=accntJournalsClosingMap.get(tdsPayable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts=accntJournalsMap.get(tdsPayable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getTdsamount())) {
													opencreditamt += accJournals.getTdsamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsClosingMap.put(tdsPayable, amts);
												if(accntJournalsClosingMap.containsKey(tdsPayable)) {
													accntJournalsClosingMap.remove(tdsPayable);
												}
											}
											if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
												double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
												double openopeningamt=0, opendebitamt=0, opencreditamt=0, openclosingamt=0;
												Amounts amts =null;
												amts =accntJournalsClosingMap.get(igstReceivable);
												if (isNotEmpty(amts)) {
													openingamt += amts.getOpeningamt();
													creditamt += amts.getCreditamt();
													debitamt += amts.getDebitamt();
													closingamt += amts.getClosingamt();
												} else {
													amts =accntJournalsMap.get(igstReceivable);
													if (isNotEmpty(amts)) {
														openingamt += amts.getOpeningamt();
														debitamt += amts.getDebitamt();
														creditamt += amts.getCreditamt();
														closingamt += amts.getClosingamt();
													} else {
														amts = new Amounts();									
													}
												}
	
												if (isNotEmpty(accJournals.getIgstamount())) {
													opendebitamt += accJournals.getIgstamount();
												}
												openopeningamt = opendebitamt - opencreditamt;
												amts.setOpeningamt(openopeningamt+openingamt);
												amts.setDebitamt(debitamt);
												amts.setCreditamt(creditamt);
												amts.setClosingamt(openopeningamt+closingamt);
												accntJournalsMap.put(igstReceivable, amts);
												
												if(accntJournalsClosingMap.containsKey(igstReceivable)) {
													accntJournalsClosingMap.remove(igstReceivable);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		if (isNotEmpty(accntJournalsClosingMap)) {
			accntJournalsMap.putAll(accntJournalsClosingMap);
		}
		return accntJournalsMap;
	}
	
	@Override
	public List<AccountingJournal> getLedgersDataNew(String ledgername, String clientid, List<String> invoiceMonth) {
		final String method = "getLedgersData::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		List<String> pr = Arrays.asList("TDS Receivables","Output IGST","Output CGST","Output SGST");
		Query query=new  Query();
		Criteria criteria=new Criteria();
		if(!pr.contains(ledgername)) {
			query.addCriteria(
					new Criteria().orOperator(
							Criteria.where("items_ledgerName").is(ledgername),
							Criteria.where("vendorName").is(ledgername)
					)
			);
		}else {
			List<String> pr1 = Arrays.asList("Output CGST","Output SGST");
			if(pr1.contains(ledgername)) {
				query.addCriteria(criteria.where("rcmorinterorintra").is("Intra"));
			}else {
				query.addCriteria(criteria.where("rcmorinterorintra").is("Inter"));
			}
		}
		query.addCriteria(Criteria.where("returnType").in("GSTR1","GSTR2"));
		query.addCriteria(Criteria.where("invoiceMonth").in(invoiceMonth));
		query.addCriteria(criteria.where("clientId").is(clientid));
		query.addCriteria(criteria.where("status").exists(false));
		
		List<AccountingJournal> invoiceJournals = mongoTemplate.find(query, AccountingJournal.class, "accounting_journal");
		
		List<String> voucherJournalsType=Arrays.asList("Voucher");
		List<AccountingJournal> voucherJournals=accountingJournalRepository.findByClientIdAndVoucheritems_LedgerAndInvoiceMonthInAndReturnTypeIn(clientid, ledgername, invoiceMonth, voucherJournalsType);
		
		List<String> contraJournalsType=Arrays.asList("Contra");
		List<AccountingJournal> contraJournals=accountingJournalRepository.findByClientIdAndContraitems_LedgerAndInvoiceMonthInAndReturnTypeIn(clientid, ledgername, invoiceMonth, contraJournalsType);

		List<String> payment_ReceiptsJournalsType=Arrays.asList("Payment Receipt","Payment");
		
		Query query1=new  Query();
		
		query1.addCriteria(
				new Criteria().orOperator(
						Criteria.where("paymentitems_ledgerName").is(ledgername),
						Criteria.where("vendorName").is(ledgername)
				)
		);
		query1.addCriteria(Criteria.where("returnType").in(payment_ReceiptsJournalsType));
		query1.addCriteria(Criteria.where("invoiceMonth").in(invoiceMonth));
		query1.addCriteria(Criteria.where("clientId").is(clientid));
		
		List<AccountingJournal> paymentsAndPaymentsReceiptsJournals = mongoTemplate.find(query1, AccountingJournal.class, "accounting_journal");
		
		List<AccountingJournal> JournalsLst=new ArrayList<>();
		
		if(isNotEmpty(invoiceJournals)) { 
			JournalsLst.addAll(invoiceJournals); 
		}
		if(isNotEmpty(voucherJournals)) {
			JournalsLst.addAll(voucherJournals);			
		}
		if(isNotEmpty(contraJournals)) {
			JournalsLst.addAll(contraJournals);
		}
		if(isNotEmpty(paymentsAndPaymentsReceiptsJournals)) {
			JournalsLst.addAll(paymentsAndPaymentsReceiptsJournals);
		}
		
		return JournalsLst;
	}
	
	
	
	public Map<String, Amounts> newCalculateLedgersAmountstb(List<AccountingJournal> accJournalsClosingLst, List<AccountingJournal> accJournalsOpeningLst) {

		Map<String, Amounts> accntJournalsClosingMap = new HashMap<String, Amounts>();
		Map<String, Amounts> accntJournalsMap = new HashMap<String, Amounts>();
		
		if(isNotEmpty(accJournalsClosingLst)) {
			for(AccountingJournal accJournals :accJournalsClosingLst) {
				
				if (accJournals.getReturnType().equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
					String igstRefundReceivable = AccountConstants.IGST_REFUND_RECEIVABLE;
					String tcsPayables = AccountConstants.TCS_PAYABLE;
					String igstPayable = AccountConstants.OUTPUT_IGST;
					String cgstPayable = AccountConstants.OUTPUT_CGST;
					String sgstPayable = AccountConstants.OUTPUT_SGST;
					String cessPayable = AccountConstants.OUTPUT_CESS;
					String taxonadv = AccountConstants.TAX_ON_ADVANCE;
					
					if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						Amounts amtstaxonadv = accntJournalsClosingMap.get(taxonadv);
						double openingamtadv = 0, debitamtadv = 0, creditamtadv = 0, closingamtadv = 0;
						if (isNotEmpty(amtstaxonadv)) {
							openingamtadv += amtstaxonadv.getOpeningamt();
							creditamtadv += amtstaxonadv.getCreditamt();
							debitamtadv += amtstaxonadv.getDebitamt();
							closingamtadv += amtstaxonadv.getClosingamt();
						} else {
							amtstaxonadv = new Amounts();
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							debitamtadv += accJournals.getIgstamount();
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							debitamtadv += accJournals.getSgstamount();
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							debitamtadv += accJournals.getCgstamount();
						}
						closingamtadv = openingamtadv + debitamtadv - creditamtadv;
						amtstaxonadv.setOpeningamt(openingamtadv);
						amtstaxonadv.setDebitamt(debitamtadv);
						amtstaxonadv.setCreditamt(creditamtadv);
						amtstaxonadv.setClosingamt(closingamtadv);
						accntJournalsClosingMap.put(taxonadv, amtstaxonadv);
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
							String vendorName = AccountConstants.OTHER_DEBTORS;
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								Amounts amtsv = accntJournalsClosingMap.get(vendorName);
								double openingamtv = 0, debitamtv = 0, creditamtv = 0, closingamtv = 0;
								if (isNotEmpty(amtsv)) {
									debitamtv += accJournals.getTdsamount();
									closingamtv =openingamtv+ debitamtv - creditamtv;
									amtsv.setOpeningamt(openingamtv + amtsv.getOpeningamt());
									amtsv.setDebitamt(debitamtv + amtsv.getDebitamt());
									amtsv.setCreditamt(creditamtv + amtsv.getCreditamt());
									amtsv.setClosingamt(closingamtv + amtsv.getClosingamt());
									accntJournalsClosingMap.put(vendorName, amtsv);
								} else {
									amtsv = new Amounts();
									debitamtv += accJournals.getTdsamount();
									closingamtv =openingamtv+ debitamtv - creditamtv;
									amtsv.setOpeningamt(openingamtv);
									amtsv.setDebitamt(debitamtv);
									amtsv.setCreditamt(creditamtv);
									amtsv.setClosingamt(closingamtv);
									accntJournalsClosingMap.put(vendorName, amtsv);
								}
							}
						}
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							//Amounts amts = accntJournalsClosingMap.get(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName);
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									creditamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								//accntJournalsClosingMap.put(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName, amts);
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									creditamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								//accntJournalsClosingMap.put(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName, amts);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.CASH;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getRateperitem())) {
										debitamt = item.getRateperitem();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getRateperitem())) {
										debitamt = item.getRateperitem();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
						if(accJournals.getCreditDebitNoteType().equalsIgnoreCase("C")) {
							String vendorName = AccountConstants.OTHER_CREDITORS;
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									creditamt += accJournals.getTdsamount()*2;
								}
								if (isNotEmpty(amts)) {
										if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
											creditamt += accJournals.getCustomerorSupplierAccount();
										}
									if (isNotEmpty(accJournals.getRoundOffAmount())) {
										creditamt += accJournals.getRoundOffAmount();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(vendorName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
									if (isNotEmpty(accJournals.getRoundOffAmount())) {
										creditamt += accJournals.getRoundOffAmount();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
							}
							
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(igstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								debitamt += accJournals.getIgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cgstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								debitamt += accJournals.getCgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(sgstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								debitamt += accJournals.getSgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cessPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								debitamt += accJournals.getCessAmount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessPayable, amts);
							}
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								Amounts amts = accntJournalsClosingMap.get(tcsPayables);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								debitamt += accJournals.getTdsamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tcsPayables, amts);
							}
							if (isNotEmpty(accJournals.getItems())) {
								for (Item item : accJournals.getItems()) {
									String ledgerName = AccountConstants.SALES;
									if(isNotEmpty(item.getLedgerName())) {
										ledgerName = item.getLedgerName();
									}
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									if (isNotEmpty(amts)) {
										if (isNotEmpty(item.getTaxablevalue())) {
											debitamt = item.getTaxablevalue();
										}
										closingamt =openingamt+ debitamt - creditamt;
										amts.setOpeningamt(openingamt + amts.getOpeningamt());
										amts.setDebitamt(debitamt + amts.getDebitamt());
										amts.setCreditamt(creditamt + amts.getCreditamt());
										amts.setClosingamt(closingamt + amts.getClosingamt());
										accntJournalsClosingMap.put(ledgerName, amts);
									} else {
										amts = new Amounts();
										if (isNotEmpty(item.getTaxablevalue())) {
											debitamt = item.getTaxablevalue();
										}
										closingamt =openingamt+ debitamt - creditamt;
										amts.setOpeningamt(openingamt);
										amts.setDebitamt(debitamt);
										amts.setCreditamt(creditamt);
										amts.setClosingamt(closingamt);
										accntJournalsClosingMap.put(ledgerName, amts);
									}
								}
							}
						}else {
							String vendorName = AccountConstants.OTHER_DEBTORS;
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								Amounts amts = accntJournalsClosingMap.get(vendorName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
									debitamt += accJournals.getTdsamount()*2;
								}
								if (isNotEmpty(amts)) {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
									if (isNotEmpty(accJournals.getRoundOffAmount())) {
										debitamt += accJournals.getRoundOffAmount();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(vendorName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
									if (isNotEmpty(accJournals.getRoundOffAmount())) {
										debitamt += accJournals.getRoundOffAmount();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(vendorName, amts);
								}
							}
							
							if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(igstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								creditamt += accJournals.getIgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(igstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cgstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								creditamt += accJournals.getCgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(sgstPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								creditamt += accJournals.getSgstamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(sgstPayable, amts);
							}
							if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
								Amounts amts = accntJournalsClosingMap.get(cessPayable);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								creditamt += accJournals.getCessAmount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(cessPayable, amts);
							}
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								Amounts amts = accntJournalsClosingMap.get(tcsPayables);
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									creditamt += amts.getCreditamt();
									debitamt += amts.getDebitamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
								creditamt += accJournals.getTdsamount();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(tcsPayables, amts);
							}
							if (isNotEmpty(accJournals.getItems())) {
								for (Item item : accJournals.getItems()) {
									String ledgerName = AccountConstants.SALES;
									if(isNotEmpty(item.getLedgerName())) {
										ledgerName = item.getLedgerName();
									}
									Amounts amts = accntJournalsClosingMap.get(ledgerName);
									double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
									if (isNotEmpty(amts)) {
										if (isNotEmpty(item.getTaxablevalue())) {
											creditamt = item.getTaxablevalue();
										}
										closingamt =openingamt+ debitamt - creditamt;
										amts.setOpeningamt(openingamt + amts.getOpeningamt());
										amts.setDebitamt(debitamt + amts.getDebitamt());
										amts.setCreditamt(creditamt + amts.getCreditamt());
										amts.setClosingamt(closingamt + amts.getClosingamt());
										accntJournalsClosingMap.put(ledgerName, amts);
									} else {
										amts = new Amounts();
										if (isNotEmpty(item.getTaxablevalue())) {
											creditamt = item.getTaxablevalue();
										}
										closingamt =openingamt+ debitamt - creditamt;
										amts.setOpeningamt(openingamt);
										amts.setDebitamt(debitamt);
										amts.setCreditamt(creditamt);
										amts.setClosingamt(closingamt);
										accntJournalsClosingMap.put(ledgerName, amts);
									}
								}
							}
						
						}
						
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2B) || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2C) || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2CL)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount();
							}
							if (isNotEmpty(amts)) {
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						//IGST Refund Receivable
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstRefundReceivable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstRefundReceivable, amts);
						}
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.NIL)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									debitamt += accJournals.getCustomerorSupplierAccount();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
									debitamt += accJournals.getCustomerorSupplierAccount();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}
				}
			}
		}
		
		
		if(isNotEmpty(accJournalsOpeningLst)) {
			for(AccountingJournal accJournals :accJournalsOpeningLst) {
				if (accJournals.getReturnType().equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
					String igstRefundReceivable = AccountConstants.IGST_REFUND_RECEIVABLE;
					String tcsPayables = AccountConstants.TCS_PAYABLE;
					String igstPayable = AccountConstants.OUTPUT_IGST;
					String cgstPayable = AccountConstants.OUTPUT_CGST;
					String sgstPayable = AccountConstants.OUTPUT_SGST;
					String cessPayable = AccountConstants.OUTPUT_CESS;
					String taxonadv = AccountConstants.TAX_ON_ADVANCE;
					if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.ADVANCES)) {
						Amounts amtstaxonadv = accntJournalsClosingMap.get(taxonadv);
						double openingamtadv = 0, debitamtadv = 0, creditamtadv = 0, closingamtadv = 0;
						if (isNotEmpty(amtstaxonadv)) {
							openingamtadv += amtstaxonadv.getOpeningamt();
							creditamtadv += amtstaxonadv.getCreditamt();
							debitamtadv += amtstaxonadv.getDebitamt();
							closingamtadv += amtstaxonadv.getClosingamt();
						} else {
							amtstaxonadv = new Amounts();
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							debitamtadv += accJournals.getIgstamount();
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							debitamtadv += accJournals.getSgstamount();
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							debitamtadv += accJournals.getCgstamount();
						}
						
						closingamtadv = openingamtadv + debitamtadv - creditamtadv;
						amtstaxonadv.setOpeningamt(openingamtadv);
						amtstaxonadv.setDebitamt(debitamtadv);
						amtstaxonadv.setCreditamt(creditamtadv);
						amtstaxonadv.setClosingamt(closingamtadv);
						accntJournalsMap.put(taxonadv, amtstaxonadv);
						if(accntJournalsClosingMap.containsKey(taxonadv)){
							accntJournalsClosingMap.remove(taxonadv);
						}
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsMap.put(igstPayable, amts);
							if(accntJournalsClosingMap.containsKey(igstPayable)){
								accntJournalsClosingMap.remove(igstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsMap.put(cgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(cgstPayable)){
								accntJournalsClosingMap.remove(cgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsMap.put(sgstPayable, amts);
							if(accntJournalsClosingMap.containsKey(sgstPayable)){
								accntJournalsClosingMap.remove(sgstPayable);
							}
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsMap.put(cessPayable, amts);
							if(accntJournalsClosingMap.containsKey(cessPayable)){
								accntJournalsClosingMap.remove(cessPayable);
							}
						}
						
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
		
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
							
							String vendorName = AccountConstants.OTHER_DEBTORS;
							if (isNotEmpty(accJournals.getVendorName())) {
								vendorName = accJournals.getVendorName();
							}
							if (isNotEmpty(vendorName)) {
								Amounts amtsv = accntJournalsClosingMap.get(vendorName);
								double openingamtv = 0, debitamtv = 0, creditamtv = 0, closingamtv = 0;
								if (isNotEmpty(amtsv)) {
									debitamtv += accJournals.getTdsamount();
									closingamtv =openingamtv+ debitamtv - creditamtv;
									amtsv.setOpeningamt(openingamtv + amtsv.getOpeningamt());
									amtsv.setDebitamt(debitamtv + amtsv.getDebitamt());
									amtsv.setCreditamt(creditamtv + amtsv.getCreditamt());
									amtsv.setClosingamt(closingamtv + amtsv.getClosingamt());
									accntJournalsClosingMap.put(vendorName, amtsv);
								} else {
									amtsv = new Amounts();
									debitamtv += accJournals.getTdsamount();
									closingamtv =openingamtv+ debitamtv - creditamtv;
									amtsv.setOpeningamt(openingamtv);
									amtsv.setDebitamt(debitamtv);
									amtsv.setCreditamt(creditamtv);
									amtsv.setClosingamt(closingamtv);
									accntJournalsClosingMap.put(vendorName, amtsv);
								}
							}
						}
						
						
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									creditamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsMap.put(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName, amts);
								if(accntJournalsClosingMap.containsKey(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName)){
									accntJournalsClosingMap.remove(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName);
								}
							} else {
								amts = new Amounts();
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									creditamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsMap.put(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName, amts);
								if(accntJournalsClosingMap.containsKey(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName)){
									accntJournalsClosingMap.remove(AccountConstants.ADVANCE_FROM + AccountConstants.WHITE_SPACE + vendorName);
								}
							}
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.CASH;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getRateperitem())) {
										debitamt = item.getRateperitem();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)){
										accntJournalsClosingMap.remove(ledgerName);
									}
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getRateperitem())) {
										debitamt = item.getRateperitem();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsMap.put(ledgerName, amts);
									if(accntJournalsClosingMap.containsKey(ledgerName)){
										accntJournalsClosingMap.remove(ledgerName);
									}
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {

						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								creditamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										creditamt += accJournals.getCustomerorSupplierAccount();
									}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										debitamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										debitamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2B) || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)  || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2C)  || accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.B2CL)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if (isNotEmpty(accJournals.getSalesorPurchases())) {
									debitamt += accJournals.getSalesorPurchases();
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						//IGST Refund Receivable
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstRefundReceivable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							debitamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstRefundReceivable, amts);
						}
						if (isNotEmpty(accJournals.getIgstamount()) && accJournals.getIgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(igstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getIgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(igstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCgstamount()) && accJournals.getCgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getSgstamount()) && accJournals.getSgstamount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(sgstPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getSgstamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(sgstPayable, amts);
						}
						if (isNotEmpty(accJournals.getCessAmount()) && accJournals.getCessAmount() > 0) {
							Amounts amts = accntJournalsClosingMap.get(cessPayable);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getCessAmount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(cessPayable, amts);
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}else if(accJournals.getInvType().equalsIgnoreCase(MasterGSTConstants.NIL)) {
						String vendorName = AccountConstants.OTHER_DEBTORS;
						if (isNotEmpty(accJournals.getVendorName())) {
							vendorName = accJournals.getVendorName();
						}
						if (isNotEmpty(vendorName)) {
							Amounts amts = accntJournalsClosingMap.get(vendorName);
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
								debitamt += accJournals.getTdsamount()*2;
							}
							if (isNotEmpty(amts)) {
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(vendorName, amts);
							} else {
								amts = new Amounts();
								if(accJournals.getInvoiceType().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
									if (isNotEmpty(accJournals.getSalesorPurchases())) {
										debitamt += accJournals.getSalesorPurchases();
									}
								}else {
									if (isNotEmpty(accJournals.getCustomerorSupplierAccount())) {
										debitamt += accJournals.getCustomerorSupplierAccount();
									}
								}
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									debitamt += accJournals.getRoundOffAmount();
								}
								closingamt =openingamt+ debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(vendorName, amts);
							}
						}
						if (isNotEmpty(accJournals.getTdsamount()) && accJournals.getTdsamount() > 0) {
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(tcsPayables);
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								creditamt += amts.getCreditamt();
								debitamt += amts.getDebitamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = new Amounts();
							}
							creditamt += accJournals.getTdsamount();
							closingamt = openingamt + debitamt - creditamt;
							amts.setOpeningamt(openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(closingamt);
							accntJournalsClosingMap.put(tcsPayables, amts);
						}	
						
						if (isNotEmpty(accJournals.getItems())) {
							for (Item item : accJournals.getItems()) {
								String ledgerName = AccountConstants.SALES;
								if(isNotEmpty(item.getLedgerName())) {
									ledgerName = item.getLedgerName();
								}
								Amounts amts = accntJournalsClosingMap.get(ledgerName);
								double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
								if (isNotEmpty(amts)) {
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt + amts.getOpeningamt());
									amts.setDebitamt(debitamt + amts.getDebitamt());
									amts.setCreditamt(creditamt + amts.getCreditamt());
									amts.setClosingamt(closingamt + amts.getClosingamt());
									accntJournalsClosingMap.put(ledgerName, amts);
								} else {
									amts = new Amounts();
									if (isNotEmpty(item.getTaxablevalue())) {
										creditamt = item.getTaxablevalue();
									}
									closingamt =openingamt+ debitamt - creditamt;
									amts.setOpeningamt(openingamt);
									amts.setDebitamt(debitamt);
									amts.setCreditamt(creditamt);
									amts.setClosingamt(closingamt);
									accntJournalsClosingMap.put(ledgerName, amts);
								}
							}
						}
					}
				}
			}
		}
		if (isNotEmpty(accntJournalsClosingMap)) {
			accntJournalsMap.putAll(accntJournalsClosingMap);
		}
		return accntJournalsMap;
	}
	
	
	
	
	
}
