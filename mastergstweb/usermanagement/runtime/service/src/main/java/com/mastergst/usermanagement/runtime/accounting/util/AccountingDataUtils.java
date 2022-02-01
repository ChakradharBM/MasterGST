package com.mastergst.usermanagement.runtime.accounting.util;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mastergst.usermanagement.runtime.accounting.dao.AccountingDao;
import com.mastergst.usermanagement.runtime.accounting.domain.JournalEntrie;
import com.mastergst.usermanagement.runtime.domain.AccountingJournal;
import com.mastergst.usermanagement.runtime.domain.Amounts;
import com.mastergst.usermanagement.runtime.repository.AccountingJournalRepository;

@Service
public class AccountingDataUtils {
	private static final Logger logger = LogManager.getLogger(AccountingDataUtils.class.getName());
	private static final String CLASSNAME = "AccountingDataUtils::";
	
	@Autowired
	private AccountingDao accountingDao;

	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private AccountingJournalRepository accountingJournalRepository;
	
	public List<AccountingJournal> getMonthlyAccountingJournalDetailsByClientidAndDate(String clientid, Date todate,
			Date fromdate) {
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
		String monthYear = strMonths + year;
		List<String> invoiceMonth = Arrays.asList(monthYear);

		Page<AccountingJournal> journals = accountingDao.findByClientidAndMonthAndYear(clientid, invoiceMonth);

		if (isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
			return journals.getContent();
		}
		return null;
	}

	public List<AccountingJournal> getMonthlyAccountingJournalOpeningBalance(String clientid, int month, int year) {
		final String method = "getMonthlyAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		List<String> invoiceMonth = new ArrayList<String>();
		if (month == 1 || month == 2 || month == 3) {
			int lastyear = year - 1;
			for (int i = 4; i <= 12; i++) {
				if (i > 9) {
					invoiceMonth.add(i + "" + lastyear);
				} else {
					invoiceMonth.add("0" + i + lastyear + "");
				}
			}
			for (int i = 1; i < month; i++) {
				invoiceMonth.add("0" + i + year + "");
			}
		} else {
			for (int i = 4; i < month; i++) {
				if (i > 9) {
					invoiceMonth.add(i + "" + year);
				} else {
					invoiceMonth.add("0" + i + year);
				}
			}
		}
		Page<AccountingJournal> journals = accountingDao.findByClientidAndMonthAndYear(clientid, invoiceMonth);

		if (isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
			return journals.getContent();
		}
		return null;
	}

	public List<AccountingJournal> getYearlyAccountingJournalClosingBalance(String clientid, int month, int year) {
		final String method = "getYearlyAccountingJournalClosingBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);
		String rtStart = "04" + year;
		String rtEnd = "03" + (year + 1);
		List<String> invoiceMonth = Arrays.asList(rtStart, "05" + year, "06" + year, "07" + year, "08" + year,
				"09" + year, "10" + year, "11" + year, "12" + year, "01" + (year + 1), "02" + (year + 1), rtEnd);
		return accountingJournalRepository.findByClientIdAndInvoiceMonthInAndStatusNull(clientid, invoiceMonth);
	}

	public List<AccountingJournal> getYearlyAccountingJournalOpeningBalance(String clientid, int month, int year) {
		final String method = "getYearlyAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		logger.debug(CLASSNAME + method + "month\t" + month);
		logger.debug(CLASSNAME + method + "year\t" + year);

		int prevYear = year - 1;

		String rtStart = "04" + prevYear;
		String rtEnd = "03" + (year);
		List<String> invoiceMonth = Arrays.asList(rtStart, "05" + prevYear, "06" + prevYear, "07" + prevYear,
				"08" + prevYear, "09" + prevYear, "10" + prevYear, "11" + prevYear, "12" + prevYear, "01" + (year),
				"02" + (year), rtEnd);

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

		Page<AccountingJournal> journals = accountingDao.findByClientIdAndDateofinvoiceBetween(clientid, stDate,
				endDate);

		if (isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
			return journals.getContent();
		}
		return null;
	}

	public List<AccountingJournal> getCustomAccountingJournalOpeningBalance(String clientid, String fromtime,
			String totime) {
		final String method = "getCustomAccountingJournalOpeningBalance::";
		logger.debug(CLASSNAME + method + BEGIN);

		String[] fromtimes = fromtime.split("-");
		String[] totimes = totime.split("-");

		Date stDate = null;
		Date endDate = null;
		Calendar cal = Calendar.getInstance();
		cal = Calendar.getInstance();
		if (Integer.parseInt(fromtimes[1]) == 4 && Integer.parseInt(fromtimes[0]) == 1) {
			// no opening balance and closing balance
			return null;
		} else {
			if (Integer.parseInt(fromtimes[2]) == Integer.parseInt(totimes[2])) {
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, 0, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());
				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1, Integer.parseInt(fromtimes[0]) - 1, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());
				Page<AccountingJournal> journals = accountingDao.findByClientIdAndDateofinvoiceBetween(clientid, stDate, endDate);
				if (isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
					return journals.getContent();
				}
				return null;
			} else {
				cal.set(Integer.parseInt(fromtimes[2]) - 1, Integer.parseInt(fromtimes[1]) - 1, 0, 0, 0, 0);
				stDate = new java.util.Date(cal.getTimeInMillis());

				cal = Calendar.getInstance();
				cal.set(Integer.parseInt(fromtimes[2]), Integer.parseInt(fromtimes[1]) - 1,
						Integer.parseInt(fromtimes[0]) - 1, 0, 0, 0);
				endDate = new java.util.Date(cal.getTimeInMillis());

				Page<AccountingJournal> journals = accountingDao.findByClientIdAndDateofinvoiceBetween(clientid, stDate, endDate);
				if (isNotEmpty(journals) && isNotEmpty(journals.getContent())) {
					return journals.getContent();
				}
				return null;
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

		Map<String, Amounts> accntJournalsMap = newCalculateLedgersAmountstb(closingbalance, openingbalance);

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

		Map<String, Amounts> accntJournalsMap = calculateLedgersAmounts(closingbalance, openingbalance);

		logger.debug(CLASSNAME + method + END);
		return accntJournalsMap;
	}
	
	public Map<String, Amounts> newCalculateLedgersAmountstb(List<AccountingJournal> accJournalsClosingLst,
			List<AccountingJournal> accJournalsOpeningLst) {

		Map<String, Amounts> accntJournalsClosingMap = new HashMap<String, Amounts>();
		Map<String, Amounts> accntJournalsMap = new HashMap<String, Amounts>();

		if (isNotEmpty(accJournalsClosingLst)) {
			for (AccountingJournal accJournals : accJournalsClosingLst) {

				List<JournalEntrie> drEntrie = accJournals.getDrEntrie();
				List<JournalEntrie> crEntrie = accJournals.getCrEntrie();

				if (isNotEmpty(drEntrie)) {
					drEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								debitamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							} else {
								amts = new Amounts();
								debitamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							}
						}
					});
				}
				if (isNotEmpty(crEntrie)) {
					crEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								creditamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							} else {
								amts = new Amounts();
								creditamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							}
						}
					});
				}
			}
		}

		if (isNotEmpty(accJournalsOpeningLst)) {
			for (AccountingJournal accJournals : accJournalsOpeningLst) {
				List<JournalEntrie> drEntrie = accJournals.getDrEntrie();
				List<JournalEntrie> crEntrie = accJournals.getCrEntrie();

				if (isNotEmpty(drEntrie)) {
					drEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {

							double openopeningamt = 0, opendebitamt = 0, opencreditamt = 0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(jEntrie.getName());
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}
							opendebitamt += jEntrie.getValue();
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt + openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt + closingamt);

							accntJournalsMap.put(jEntrie.getName(), amts);

							if (accntJournalsClosingMap.containsKey(jEntrie.getName())) {
								accntJournalsClosingMap.remove(jEntrie.getName());
							}
						}
					});
				}
				if (isNotEmpty(crEntrie)) {
					crEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {

							double openopeningamt = 0, opendebitamt = 0, opencreditamt = 0;
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							if (isNotEmpty(amts)) {
								openingamt += amts.getOpeningamt();
								debitamt += amts.getDebitamt();
								creditamt += amts.getCreditamt();
								closingamt += amts.getClosingamt();
							} else {
								amts = accntJournalsMap.get(jEntrie.getName());
								if (isNotEmpty(amts)) {
									openingamt += amts.getOpeningamt();
									debitamt += amts.getDebitamt();
									creditamt += amts.getCreditamt();
									closingamt += amts.getClosingamt();
								} else {
									amts = new Amounts();
								}
							}
							opencreditamt += jEntrie.getValue();
							openopeningamt = opendebitamt - opencreditamt;
							amts.setOpeningamt(openopeningamt + openingamt);
							amts.setDebitamt(debitamt);
							amts.setCreditamt(creditamt);
							amts.setClosingamt(openopeningamt + closingamt);

							accntJournalsMap.put(jEntrie.getName(), amts);

							if (accntJournalsClosingMap.containsKey(jEntrie.getName())) {
								accntJournalsClosingMap.remove(jEntrie.getName());
							}
						}
					});
				}
			}
		}

		if (isNotEmpty(accntJournalsClosingMap)) {
			accntJournalsMap.putAll(accntJournalsClosingMap);
		}
		return accntJournalsMap;
	}
	
	public Map<String, Amounts> calculateLedgersAmounts(List<AccountingJournal> accJournalsClosingLst,List<AccountingJournal> accJournalsOpeningLst) {

		Map<String, Amounts> accntJournalsClosingMap = new HashMap<String, Amounts>();
		Map<String, Amounts> accntJournalsMap = new HashMap<String, Amounts>();

		if (isNotEmpty(accJournalsClosingLst)) {
			for (AccountingJournal accJournals : accJournalsClosingLst) {

				List<JournalEntrie> drEntrie = accJournals.getDrEntrie();
				List<JournalEntrie> crEntrie = accJournals.getCrEntrie();

				if (isNotEmpty(drEntrie)) {
					drEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								debitamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							} else {
								amts = new Amounts();
								debitamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							}
						}
					});
				}
				if (isNotEmpty(crEntrie)) {
					crEntrie.forEach(jEntrie -> {
						if (isNotEmpty(jEntrie.getName())) {
							Amounts amts = accntJournalsClosingMap.get(jEntrie.getName());
							double openingamt = 0, debitamt = 0, creditamt = 0, closingamt = 0;

							if (isNotEmpty(amts)) {
								creditamt += jEntrie.getValue();
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt + amts.getOpeningamt());
								amts.setDebitamt(debitamt + amts.getDebitamt());
								amts.setCreditamt(creditamt + amts.getCreditamt());
								amts.setClosingamt(closingamt + amts.getClosingamt());
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							} else {
								amts = new Amounts();
								creditamt += jEntrie.getValue();
								if (isNotEmpty(accJournals.getRoundOffAmount())) {
									creditamt += accJournals.getRoundOffAmount();
								}
								closingamt = openingamt + debitamt - creditamt;
								amts.setOpeningamt(openingamt);
								amts.setDebitamt(debitamt);
								amts.setCreditamt(creditamt);
								amts.setClosingamt(closingamt);
								accntJournalsClosingMap.put(jEntrie.getName(), amts);
							}
						}
					});
				}
			}
		}

		if (isNotEmpty(accntJournalsClosingMap)) {
			accntJournalsMap.putAll(accntJournalsClosingMap);
		}
		// System.out.println(accntJournalsMap);
		return accntJournalsMap;
	}
	
	public List<AccountingJournal> getLedgersDataNew(String ledgername, String clientid, List<String> invoiceMonth) {
		final String method = "getLedgersDataNew::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		
		return accountingDao.getLedgersDataNew(ledgername, clientid, invoiceMonth);
	}

	public List<AccountingJournal> getCustomLedgersDataNew(String ledgername, String clientid, Date startDate, Date endDate) {
		final String method = "getCustomLedgersDataNew::";
		logger.debug(CLASSNAME + method + BEGIN);
		logger.debug(CLASSNAME + method + "clientid\t" + clientid);
		       
		return accountingDao.getCustomLedgersDataNew(ledgername, clientid, startDate, endDate);
	}

}
