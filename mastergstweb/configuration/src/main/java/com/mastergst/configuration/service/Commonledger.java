package com.mastergst.configuration.service;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "commonledger")
public class Commonledger {
	
	@Id 
	private ObjectId id;
	private String ledgerName;
	private String grpsubgrpName;
	private String ledgerpath;
	
	public Commonledger() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
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

}
