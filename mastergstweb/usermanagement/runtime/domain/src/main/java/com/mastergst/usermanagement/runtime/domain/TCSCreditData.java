package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GSTR2X TCS Credit Data invoices information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class TCSCreditData {

	@Id
	private ObjectId id;
	
	private String ctin;
	private String chksum;
	private Double amt;
	private Double supR;
	private Double retsupR;
	private Double supU;
	private Double retsupU;
	private Double iamt;
	private Double camt;
	private Double samt;
	private String flag;
	@DateTimeFormat(pattern = "MMYYYY")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MMYYYY")
	private Date month;
	
	public TCSCreditData() {
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
	public Double getAmt() {
		return amt;
	}
	public void setAmt(Double amt) {
		this.amt = amt;
	}
	public Double getSupR() {
		return supR;
	}
	public void setSupR(Double supR) {
		this.supR = supR;
	}
	public Double getRetsupR() {
		return retsupR;
	}
	public void setRetsupR(Double retsupR) {
		this.retsupR = retsupR;
	}
	public Double getSupU() {
		return supU;
	}
	public void setSupU(Double supU) {
		this.supU = supU;
	}
	public Double getRetsupU() {
		return retsupU;
	}
	public void setRetsupU(Double retsupU) {
		this.retsupU = retsupU;
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
