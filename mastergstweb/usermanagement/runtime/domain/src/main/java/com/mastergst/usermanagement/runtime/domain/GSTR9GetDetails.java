package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.core.domain.Base;
/**
 * GSTR9 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid","matchingId", "matchingStatus", "dateofinvoice", "legalName", "tradeName"})
//@JsonFilter("gstr9Filter")
public class GSTR9GetDetails extends Base {
	private String userid;
	private String fullname;
	private String clientid;
	private String matchingId;
	private String matchingStatus;
	private Date dateofinvoice;
	private String legalName;
	private String tradeName;
	String gstin;
	String fp;
	GSTR9Table4GetDetails table4 = new GSTR9Table4GetDetails();
	GSTR9Table5GetDetails table5 = new GSTR9Table5GetDetails();
	GSTR9Table6GetDetails table6 = new GSTR9Table6GetDetails();
	GSTR9Table7GetDetails table7 = new GSTR9Table7GetDetails();
	GSTR9Table8GetDetails table8 = new GSTR9Table8GetDetails();
	GSTR9Table9GetDetails table9 = new GSTR9Table9GetDetails();
	GSTR9Table10GetDetails table10 = new GSTR9Table10GetDetails();
	GSTR9Table14GetDetails table14 = new GSTR9Table14GetDetails();
	GSTR9Table15GetDetails table15 = new GSTR9Table15GetDetails();
	GSTR9Table16GetDetails table16 = new GSTR9Table16GetDetails();
	GSTR9HSNSummary table17 = new GSTR9HSNSummary();
	GSTR9HSNSummary table18 = new GSTR9HSNSummary();

	@JsonProperty("tax_pay")		
	List<GSTR9TaxPayableSection> taxPay = LazyList.decorate(new ArrayList<GSTR9TaxPayableSection>(), 
			FactoryUtils.instantiateFactory(GSTR9TaxPayableSection.class));
	@JsonProperty("tax_paid")	
	GSTR9PaidByCash taxPaid = new GSTR9PaidByCash();
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getMatchingId() {
		return matchingId;
	}

	public void setMatchingId(String matchingId) {
		this.matchingId = matchingId;
	}

	public String getMatchingStatus() {
		return matchingStatus;
	}

	public void setMatchingStatus(String matchingStatus) {
		this.matchingStatus = matchingStatus;
	}

	public Date getDateofinvoice() {
		return dateofinvoice;
	}

	public void setDateofinvoice(Date dateofinvoice) {
		this.dateofinvoice = dateofinvoice;
	}

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getTradeName() {
		return tradeName;
	}

	public void setTradeName(String tradeName) {
		this.tradeName = tradeName;
	}

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public GSTR9Table4GetDetails getTable4() {
		return table4;
	}

	public void setTable4(GSTR9Table4GetDetails table4) {
		this.table4 = table4;
	}

	public GSTR9Table5GetDetails getTable5() {
		return table5;
	}

	public void setTable5(GSTR9Table5GetDetails table5) {
		this.table5 = table5;
	}

	public GSTR9Table6GetDetails getTable6() {
		return table6;
	}

	public void setTable6(GSTR9Table6GetDetails table6) {
		this.table6 = table6;
	}

	public GSTR9Table7GetDetails getTable7() {
		return table7;
	}

	public void setTable7(GSTR9Table7GetDetails table7) {
		this.table7 = table7;
	}

	public GSTR9Table8GetDetails getTable8() {
		return table8;
	}

	public void setTable8(GSTR9Table8GetDetails table8) {
		this.table8 = table8;
	}

	public GSTR9Table9GetDetails getTable9() {
		return table9;
	}

	public void setTable9(GSTR9Table9GetDetails table9) {
		this.table9 = table9;
	}

	public GSTR9Table10GetDetails getTable10() {
		return table10;
	}

	public void setTable10(GSTR9Table10GetDetails table10) {
		this.table10 = table10;
	}

	public GSTR9Table14GetDetails getTable14() {
		return table14;
	}

	public void setTable14(GSTR9Table14GetDetails table14) {
		this.table14 = table14;
	}

	public GSTR9Table15GetDetails getTable15() {
		return table15;
	}

	public void setTable15(GSTR9Table15GetDetails table15) {
		this.table15 = table15;
	}

	public GSTR9Table16GetDetails getTable16() {
		return table16;
	}

	public void setTable16(GSTR9Table16GetDetails table16) {
		this.table16 = table16;
	}

	public GSTR9HSNSummary getTable17() {
		return table17;
	}

	public void setTable17(GSTR9HSNSummary table17) {
		this.table17 = table17;
	}

	public GSTR9HSNSummary getTable18() {
		return table18;
	}

	public void setTable18(GSTR9HSNSummary table18) {
		this.table18 = table18;
	}

	public List<GSTR9TaxPayableSection> getTaxPay() {
		return taxPay;
	}

	public void setTaxPay(List<GSTR9TaxPayableSection> taxPay) {
		this.taxPay = taxPay;
	}

	public GSTR9PaidByCash getTaxPaid() {
		return taxPaid;
	}

	public void setTaxPaid(GSTR9PaidByCash taxPaid) {
		this.taxPaid = taxPaid;
	}

	
	
	
}
