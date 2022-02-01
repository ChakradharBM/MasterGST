package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "other_config")
public class OtherConfigurations extends Base {
	
	private String clientid;
	private String itcinput;
	private String itcinputService;
	private String itcCapgood;
	
	private String drcr;
	private String transDate;
	private String ledgerName;
	private String salesFields;
	private String purFields;
	private String invoiceview;
	
	private boolean enableCessQty; 
	private boolean enableDrcr; 
	private boolean enableTransDate; 
	private boolean enableLedgerName;
	private boolean enableSalesFields; 
	private boolean enablePurFields; 
	private boolean enableLedgerSalesField = true; 
	private boolean enableLedgerPurField = true; 
	private boolean enableinvoiceview;
	private boolean enableroundoffSalesField;
	private boolean enableroundoffPurField;
	private boolean enablejournals;
	private boolean enableTCS;
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getItcinput() {
		return itcinput;
	}
	public void setItcinput(String itcinput) {
		this.itcinput = itcinput;
	}
	public String getItcinputService() {
		return itcinputService;
	}
	public void setItcinputService(String itcinputService) {
		this.itcinputService = itcinputService;
	}
	public String getItcCapgood() {
		return itcCapgood;
	}
	public void setItcCapgood(String itcCapgood) {
		this.itcCapgood = itcCapgood;
	}
	public String getDrcr() {
		return drcr;
	}
	public void setDrcr(String drcr) {
		this.drcr = drcr;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getLedgerName() {
		return ledgerName;
	}
	public void setLedgerName(String ledgerName) {
		this.ledgerName = ledgerName;
	}
	public boolean isEnableDrcr() {
		return enableDrcr;
	}
	public void setEnableDrcr(boolean enableDrcr) {
		this.enableDrcr = enableDrcr;
	}
	public boolean isEnableTransDate() {
		return enableTransDate;
	}
	public void setEnableTransDate(boolean enableTransDate) {
		this.enableTransDate = enableTransDate;
	}
	public boolean isEnableLedgerName() {
		return enableLedgerName;
	}
	public void setEnableLedgerName(boolean enableLedgerName) {
		this.enableLedgerName = enableLedgerName;
	}
	public boolean isEnablePurFields() {
		return enablePurFields;
	}
	public void setEnablePurFields(boolean enablePurFields) {
		this.enablePurFields = enablePurFields;
	}
	public String getSalesFields() {
		return salesFields;
	}
	public void setSalesFields(String salesFields) {
		this.salesFields = salesFields;
	}
	public String getPurFields() {
		return purFields;
	}
	public void setPurFields(String purFields) {
		this.purFields = purFields;
	}
	public boolean isEnableSalesFields() {
		return enableSalesFields;
	}
	public void setEnableSalesFields(boolean enableSalesFields) {
		this.enableSalesFields = enableSalesFields;
	}
	public boolean isEnableinvoiceview() {
		return enableinvoiceview;
	}
	public void setEnableinvoiceview(boolean enableinvoiceview) {
		this.enableinvoiceview = enableinvoiceview;
	}
	public String getInvoiceview() {
		return invoiceview;
	}
	public void setInvoiceview(String invoiceview) {
		this.invoiceview = invoiceview;
	}
	public boolean isEnableLedgerSalesField() {
		return enableLedgerSalesField;
	}
	public void setEnableLedgerSalesField(boolean enableLedgerSalesField) {
		this.enableLedgerSalesField = enableLedgerSalesField;
	}
	public boolean isEnableLedgerPurField() {
		return enableLedgerPurField;
	}
	public void setEnableLedgerPurField(boolean enableLedgerPurField) {
		this.enableLedgerPurField = enableLedgerPurField;
	}
	public boolean isEnableroundoffSalesField() {
		return enableroundoffSalesField;
	}
	public void setEnableroundoffSalesField(boolean enableroundoffSalesField) {
		this.enableroundoffSalesField = enableroundoffSalesField;
	}
	public boolean isEnableroundoffPurField() {
		return enableroundoffPurField;
	}
	public void setEnableroundoffPurField(boolean enableroundoffPurField) {
		this.enableroundoffPurField = enableroundoffPurField;
	}
	public boolean isEnablejournals() {
		return enablejournals;
	}
	public void setEnablejournals(boolean enablejournals) {
		this.enablejournals = enablejournals;
	}
	public boolean isEnableTCS() {
		return enableTCS;
	}
	public void setEnableTCS(boolean enableTCS) {
		this.enableTCS = enableTCS;
	}
	public boolean isEnableCessQty() {
		return enableCessQty;
	}
	public void setEnableCessQty(boolean enableCessQty) {
		this.enableCessQty = enableCessQty;
	}
	
	
}