package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mastergst.core.domain.Base;

@Document(collection = "gstr4_annual")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({ "id", "userid", "fullname", "clientid" })
public class GSTR4Annual extends Base {
	private String userid;
	private String fullname;
	private String clientid;

	private String gstin;
	private Double agg_turnover;
	private String fp;
	private String isnil;
	private String isreset;
	private List<GSTR4B2B> b2bor;
	private List<GSTR4B2B> b2br;
	private List<GSTR4B2C> b2bur;
	private List<GSTR4IMPS> imps;
	private List<OutwardSupply> outsupply;
	
	private GSTR4CMPData cmpdata;

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

	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public Double getAgg_turnover() {
		return agg_turnover;
	}

	public void setAgg_turnover(Double agg_turnover) {
		this.agg_turnover = agg_turnover;
	}

	public String getFp() {
		return fp;
	}

	public void setFp(String fp) {
		this.fp = fp;
	}

	public String getIsnil() {
		return isnil;
	}

	public void setIsnil(String isnil) {
		this.isnil = isnil;
	}

	public String getIsreset() {
		return isreset;
	}

	public void setIsreset(String isreset) {
		this.isreset = isreset;
	}

	public List<GSTR4B2B> getB2bor() {
		return b2bor;
	}

	public void setB2bor(List<GSTR4B2B> b2bor) {
		this.b2bor = b2bor;
	}

	public List<GSTR4B2B> getB2br() {
		return b2br;
	}

	public void setB2br(List<GSTR4B2B> b2br) {
		this.b2br = b2br;
	}

	public List<GSTR4B2C> getB2bur() {
		return b2bur;
	}

	public void setB2bur(List<GSTR4B2C> b2bur) {
		this.b2bur = b2bur;
	}

	public List<GSTR4IMPS> getImps() {
		return imps;
	}

	public void setImps(List<GSTR4IMPS> imps) {
		this.imps = imps;
	}

	public List<OutwardSupply> getOutsupply() {
		return outsupply;
	}

	public void setOutsupply(List<OutwardSupply> outsupply) {
		this.outsupply = outsupply;
	}

	public GSTR4CMPData getCmpdata() {
		return cmpdata;
	}

	public void setCmpdata(GSTR4CMPData cmpdata) {
		this.cmpdata = cmpdata;
	}
	
}
