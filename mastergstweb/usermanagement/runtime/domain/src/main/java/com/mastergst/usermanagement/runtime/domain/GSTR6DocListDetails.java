/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;



import java.util.Date;

/**
 * GSTR6 Document List Details information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"id"})
public class GSTR6DocListDetails {
	
	@Id
	private ObjectId id;
	
	@JsonProperty("isd_docty")
	private String isdDocty;
	private String docnum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date docdt;
	private String crdnum;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date crddt;
	
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamts;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double iamtc;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samts;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camti;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camtc;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double csamt;
	
	public GSTR6DocListDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public String getIsdDocty() {
		return isdDocty;
	}

	public void setIsdDocty(String isdDocty) {
		this.isdDocty = isdDocty;
	}

	public String getDocnum() {
		return docnum;
	}

	public void setDocnum(String docnum) {
		this.docnum = docnum;
	}

	public Date getDocdt() {
		return docdt;
	}

	public void setDocdt(Date docdt) {
		this.docdt = docdt;
	}

	public String getCrdnum() {
		return crdnum;
	}

	public void setCrdnum(String crdnum) {
		this.crdnum = crdnum;
	}

	public Date getCrddt() {
		return crddt;
	}

	public void setCrddt(Date crddt) {
		this.crddt = crddt;
	}

	public Double getIamti() {
		return iamti;
	}

	public void setIamti(Double iamti) {
		this.iamti = iamti;
	}

	public Double getIamts() {
		return iamts;
	}

	public void setIamts(Double iamts) {
		this.iamts = iamts;
	}

	public Double getIamtc() {
		return iamtc;
	}

	public void setIamtc(Double iamtc) {
		this.iamtc = iamtc;
	}

	public Double getSamts() {
		return samts;
	}

	public void setSamts(Double samts) {
		this.samts = samts;
	}

	public Double getSamti() {
		return samti;
	}

	public void setSamti(Double samti) {
		this.samti = samti;
	}

	public Double getCamti() {
		return camti;
	}

	public void setCamti(Double camti) {
		this.camti = camti;
	}

	public Double getCamtc() {
		return camtc;
	}

	public void setCamtc(Double camtc) {
		this.camtc = camtc;
	}

	public Double getCsamt() {
		return csamt;
	}

	public void setCsamt(Double csamt) {
		this.csamt = csamt;
	}
	
}
