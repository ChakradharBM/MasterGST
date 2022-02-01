package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR9TaxPayableSection {
	@Id
	private ObjectId id;
	
	@JsonProperty("liab_id")
	private Integer liabId;
	
	private Integer trancd;
	
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date trandate;
	
	GSTR9MinorHeads cgst = new GSTR9MinorHeads();
	GSTR9MinorHeads sgst = new GSTR9MinorHeads();
	
	public GSTR9TaxPayableSection() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Integer getLiabId() {
		return liabId;
	}

	public void setLiabId(Integer liabId) {
		this.liabId = liabId;
	}

	public Integer getTrancd() {
		return trancd;
	}

	public void setTrancd(Integer trancd) {
		this.trancd = trancd;
	}

	public Date getTrandate() {
		return trandate;
	}

	public void setTrandate(Date trandate) {
		this.trandate = trandate;
	}

	public GSTR9MinorHeads getCgst() {
		return cgst;
	}

	public void setCgst(GSTR9MinorHeads cgst) {
		this.cgst = cgst;
	}

	public GSTR9MinorHeads getSgst() {
		return sgst;
	}

	public void setSgst(GSTR9MinorHeads sgst) {
		this.sgst = sgst;
	}
	
	
	
}
