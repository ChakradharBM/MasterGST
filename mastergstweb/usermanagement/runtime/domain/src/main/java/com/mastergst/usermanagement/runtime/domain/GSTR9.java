package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR9 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr9")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid","matchingId", "matchingStatus", "dateofinvoice", "legalName", "tradeName", 
	"samt", "totalTurnOver", "fee", "itc_2a", "table4ItoL", "iamt", "intr", "table5AtoF", "turnoverOnTaxNotPaid", "camt", "itc_3b", "table4AtoG", "csamt", 
	"table4HtoM", "table5HtoK",	"createdDate", "createdBy", "updatedDate", "updatedBy","table6BtoH","table6J","table6N","table6O","table7I","table7J",
	"table8B","table8D","table8I","table8H","table8J","table8K","table10111213","pen","other"})
@JsonFilter("gstr9Filter")
public class GSTR9 extends Gstr9NonEditableItems {
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
	GSTR9Table4Details table4 = new GSTR9Table4Details();
	GSTR9Table5Details table5 = new GSTR9Table5Details();
	GSTR9Table6Details table6 = new GSTR9Table6Details();
	GSTR9Table7Details table7 = new GSTR9Table7Details();
	GSTR9Table8Details table8 = new GSTR9Table8Details();
	GSTR9Table9Details table9 = new GSTR9Table9Details();
	GSTR9Table10Details table10 = new GSTR9Table10Details();
	GSTR9Table14Details table14 = new GSTR9Table14Details();
	GSTR9Table15Details table15 = new GSTR9Table15Details();
	GSTR9Table16Details table16 = new GSTR9Table16Details();
	GSTR9HSNSummary table17 = new GSTR9HSNSummary();
	GSTR9HSNSummary table18 = new GSTR9HSNSummary();


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

	public GSTR9Table4Details getTable4() {
		return table4;
	}

	public void setTable4(GSTR9Table4Details table4) {
		this.table4 = table4;
	}

	public GSTR9Table5Details getTable5() {
		return table5;
	}

	public void setTable5(GSTR9Table5Details table5) {
		this.table5 = table5;
	}

	public GSTR9Table6Details getTable6() {
		return table6;
	}

	public void setTable6(GSTR9Table6Details table6) {
		this.table6 = table6;
	}

	public GSTR9Table7Details getTable7() {
		return table7;
	}

	public void setTable7(GSTR9Table7Details table7) {
		this.table7 = table7;
	}

	public GSTR9Table8Details getTable8() {
		return table8;
	}

	public void setTable8(GSTR9Table8Details table8) {
		this.table8 = table8;
	}

	public GSTR9Table9Details getTable9() {
		return table9;
	}

	public void setTable9(GSTR9Table9Details table9) {
		this.table9 = table9;
	}

	public GSTR9Table10Details getTable10() {
		return table10;
	}

	public void setTable10(GSTR9Table10Details table10) {
		this.table10 = table10;
	}

	public GSTR9Table14Details getTable14() {
		return table14;
	}

	public void setTable14(GSTR9Table14Details table14) {
		this.table14 = table14;
	}

	public GSTR9Table15Details getTable15() {
		return table15;
	}

	public void setTable15(GSTR9Table15Details table15) {
		this.table15 = table15;
	}

	public GSTR9Table16Details getTable16() {
		return table16;
	}

	public void setTable16(GSTR9Table16Details table16) {
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

	
	
	
}
