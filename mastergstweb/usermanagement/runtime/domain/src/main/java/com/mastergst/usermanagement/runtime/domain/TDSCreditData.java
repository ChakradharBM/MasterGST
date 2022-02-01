package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

/**
 * GSTR2X TDS Credit Data invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class TDSCreditData {
	@Id
	private ObjectId id;
	
	private String ctin;
	private String chksum;
	@JsonProperty("amt_ded")
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double amtDed;
	private Double iamt;
	private Double camt;
	private Double samt;
	private String flag;
	@DateTimeFormat(pattern = "MMYYYY")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMYYYY")
	private Date month;
	
	public TDSCreditData() {
		this.id = ObjectId.get();
	}
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getCtin() {
		return ctin;
	}
	public void setCtin(String ctin) {
		this.ctin = ctin;
	}
	public String getChksum() {
		return chksum;
	}
	public void setChksum(String chksum) {
		this.chksum = chksum;
	}
	public Double getAmtDed() {
		return amtDed;
	}
	public void setAmtDed(Double amtDed) {
		this.amtDed = amtDed;
	}
	public Double getIamt() {
		return iamt;
	}
	public void setIamt(Double iamt) {
		this.iamt = iamt;
	}
	public Double getCamt() {
		return camt;
	}
	public void setCamt(Double camt) {
		this.camt = camt;
	}
	public Double getSamt() {
		return samt;
	}
	public void setSamt(Double samt) {
		this.samt = samt;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Date getMonth() {
		return month;
	}
	public void setMonth(Date month) {
		this.month = month;
	}
}
