package com.mastergst.usermanagement.runtime.domain.gstr9response;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.core.domain.Base;

/**
 * GSTR9 information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "gstr9_dummy_response")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy"})
//@JsonFilter("gstr9Filter")
public class GSTR9DummyResponse extends Base{
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
	GSTR9Table4DetailsResponse table4 = new GSTR9Table4DetailsResponse();
	GSTR9Table5DetailsResponse table5 = new GSTR9Table5DetailsResponse();
	GSTR9Table6DetailsResponse table6 = new GSTR9Table6DetailsResponse();
	GSTR9Table7DetailsResponse table7 = new GSTR9Table7DetailsResponse();
	GSTR9Table8DetailsResponse table8 = new GSTR9Table8DetailsResponse();
	GSTR9Table9DetailsResponse table9 = new GSTR9Table9DetailsResponse();
	GSTR9Table10DetailsResponse table10 = new GSTR9Table10DetailsResponse();
	GSTR9Table14DetailsResponse table14 = new GSTR9Table14DetailsResponse();
	GSTR9Table15DetailsResponse table15 = new GSTR9Table15DetailsResponse();
	GSTR9Table16DetailsResponse table16 = new GSTR9Table16DetailsResponse();
	GSTR9HSNDataResponse table17 = new GSTR9HSNDataResponse();
	GSTR9HSNDataResponse table18 = new GSTR9HSNDataResponse();


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

	public GSTR9Table4DetailsResponse getTable4() {
		return table4;
	}

	public void setTable4(GSTR9Table4DetailsResponse table4) {
		this.table4 = table4;
	}

	public GSTR9Table5DetailsResponse getTable5() {
		return table5;
	}

	public void setTable5(GSTR9Table5DetailsResponse table5) {
		this.table5 = table5;
	}

	public GSTR9Table6DetailsResponse getTable6() {
		return table6;
	}

	public void setTable6(GSTR9Table6DetailsResponse table6) {
		this.table6 = table6;
	}

	public GSTR9Table7DetailsResponse getTable7() {
		return table7;
	}

	public void setTable7(GSTR9Table7DetailsResponse table7) {
		this.table7 = table7;
	}

	public GSTR9Table8DetailsResponse getTable8() {
		return table8;
	}

	public void setTable8(GSTR9Table8DetailsResponse table8) {
		this.table8 = table8;
	}

	public GSTR9Table9DetailsResponse getTable9() {
		return table9;
	}

	public void setTable9(GSTR9Table9DetailsResponse table9) {
		this.table9 = table9;
	}

	public GSTR9Table10DetailsResponse getTable10() {
		return table10;
	}

	public void setTable10(GSTR9Table10DetailsResponse table10) {
		this.table10 = table10;
	}

	public GSTR9Table14DetailsResponse getTable14() {
		return table14;
	}

	public void setTable14(GSTR9Table14DetailsResponse table14) {
		this.table14 = table14;
	}

	public GSTR9Table15DetailsResponse getTable15() {
		return table15;
	}

	public void setTable15(GSTR9Table15DetailsResponse table15) {
		this.table15 = table15;
	}

	public GSTR9Table16DetailsResponse getTable16() {
		return table16;
	}

	public void setTable16(GSTR9Table16DetailsResponse table16) {
		this.table16 = table16;
	}

	public GSTR9HSNDataResponse getTable17() {
		return table17;
	}

	public void setTable17(GSTR9HSNDataResponse table17) {
		this.table17 = table17;
	}

	public GSTR9HSNDataResponse getTable18() {
		return table18;
	}

	public void setTable18(GSTR9HSNDataResponse table18) {
		this.table18 = table18;
	}
	
	
}
