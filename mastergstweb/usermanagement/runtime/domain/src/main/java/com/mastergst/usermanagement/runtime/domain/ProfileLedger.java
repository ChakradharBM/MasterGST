package com.mastergst.usermanagement.runtime.domain;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "ledger")
public class ProfileLedger{
	
	@Id 
	private ObjectId id;
	private String clientid;
	private String ledgerName;
	private String grpsubgrpName;
	private String ledgerpath;
	private String ledgerOpeningBalanceType;
	private double ledgerOpeningBalance;
	private String ledgerOpeningBalanceMonth;
	private String bankId;
	private Amounts amounts;

	public ProfileLedger() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public String getGrpsubgrpName() {
		return grpsubgrpName;
	}
	public void setGrpsubgrpName(String grpsubgrpName) {
		this.grpsubgrpName = grpsubgrpName;
	}
	
	public String getLedgerpath() {
		return ledgerpath;
	}

	public void setLedgerpath(String ledgerpath) {
		this.ledgerpath = ledgerpath;
	}

	public Amounts getAmounts() {
		return amounts;
	}

	public void setAmounts(Amounts amounts) {
		this.amounts = amounts;
	}


	public String getLedgerOpeningBalanceType() {
		return ledgerOpeningBalanceType;
	}

	public void setLedgerOpeningBalanceType(String ledgerOpeningBalanceType) {
		this.ledgerOpeningBalanceType = ledgerOpeningBalanceType;
	}

	public double getLedgerOpeningBalance() {
		return ledgerOpeningBalance;
	}

	public void setLedgerOpeningBalance(double ledgerOpeningBalance) {
		this.ledgerOpeningBalance = ledgerOpeningBalance;
	}

	public String getLedgerOpeningBalanceMonth() {
		return ledgerOpeningBalanceMonth;
	}

	public void setLedgerOpeningBalanceMonth(String ledgerOpeningBalanceMonth) {
		this.ledgerOpeningBalanceMonth = ledgerOpeningBalanceMonth;
	}
	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}
}