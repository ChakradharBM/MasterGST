package com.mastergst.usermanagement.runtime.repository;

import java.util.List;
import java.util.Date;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.mastergst.usermanagement.runtime.domain.AccountingJournal;

public interface AccountingJournalRepository extends MongoRepository<AccountingJournal, String>{
	AccountingJournal findByInvoiceId(String invoiceId);
	AccountingJournal findByInvoiceIdAndClientIdAndReturnType(String invoiceId,String clientId,String returnType);
	List<AccountingJournal> findByClientId(String clientId);
	List<AccountingJournal> findByClientIdAndReturnType(String clientId,String returnType);
	List<AccountingJournal> findByClientIdAndReturnTypeInAndStatusNull(String clientId,List<String> returnType);
	List<AccountingJournal> findByClientIdAndStatusNull(String clientId);
	List<AccountingJournal> findByReturnType(String returnType);
	List<AccountingJournal> findByReturnTypeIn(List<String> returnType);
	AccountingJournal findByInvoiceNumberAndClientIdAndReturnType(String invoiceNumber,String clientId,String returnType);
	List<AccountingJournal> findByClientIdAndCreatedDateBetween(String clientid, Date fromDate, Date toDate);
	List<AccountingJournal> findByClientIdAndCreatedDateBetweenAndStatusNull(String clientid, Date fromDate, Date toDate);
	List<AccountingJournal> findByClientIdAndDateofinvoiceBetweenAndStatusNull(String clientid, Date fromDate, Date toDate);
	List<AccountingJournal> findByClientIdAndInvoiceMonthInAndStatusNull(String clientid, List<String> invoiceMonth);
	
	List<AccountingJournal> findByClientIdAndDateofinvoiceBetween(String clientid, Date fromDate, Date toDate);
	public List<AccountingJournal> findByClientIdAndVoucheritems_LedgerAndInvoiceMonthInAndReturnTypeIn(String clientid, String ledgername, List<String> invoiceMonth, List<String> returntType);
	public List<AccountingJournal> findByClientIdAndContraitems_LedgerAndInvoiceMonthInAndReturnTypeIn(String clientid, String ledgername, List<String> invoiceMonth, List<String> returntType);
	public List<AccountingJournal> findByClientIdAndVoucheritems_LedgerAndReturnTypeInAndDateofinvoiceBetween(String clientid, String ledgername, List<String> voucherJournalsType, Date stDate, Date endDate);
	public List<AccountingJournal> findByClientIdAndContraitems_LedgerAndReturnTypeInAndDateofinvoiceBetween(String clientid, String ledgername, List<String> contraJournalsType, Date stDate, Date endDate);
	public void deleteByInvoiceId(String invoiceId);

	//public List<AccountingJournal> findByClientIdAndJournalEntrie_ToEnterie_Entries_NameOrJournalEntrie_ByEnterie_Entries_Name(String entrie);
	//public List<AccountingJournal> findByClientIdAndJournalEntrie_ToEnterie_Entries_Name(String entrie);
	//public List<AccountingJournal> findByClientIdAndJournalEntrie_ByEnterie_Entries_Name(String entrie);
	//List<AccountingJournal> findByClientIdAndJournalEntrie_ToEnterie_Name(String string);
	//List<AccountingJournal> findByClientIdAndJournalEntrieToEnterie_Name(String string);
	//List<AccountingJournal> findByClientIdAndCrJournalEntrie_name(String clientid, String string);
	//List<AccountingJournal> findByClientIdAndCrJournalEntrie_nameOrDrJournalEntrie_name(String clientid, String string);

}
