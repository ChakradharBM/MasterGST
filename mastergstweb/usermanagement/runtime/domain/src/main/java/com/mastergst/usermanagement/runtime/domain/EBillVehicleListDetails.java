/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EBillVehicleListDetails {
	@Id
	private ObjectId id;
	
	@JsonProperty("updMode")
	private String updMode;
	@JsonProperty("vehicleNo")
	private String vehicleNo;
	@JsonProperty("fromPlace")
	private String fromPlace;
	@JsonProperty("fromState")
	private Number fromState;
	@JsonProperty("tripshtNo")
	private Number tripshtNo;
	@JsonProperty("userGSTINTransin")
	private String userGSTINTransin;
	@JsonProperty("enteredDate")
	private String enteredDate;
	@JsonProperty("transMode")
	private String transMode;
	@JsonProperty("transDocNo")
	private String transDocNo;
	@JsonProperty("transDocDate")
	private String transDocDate;
	@JsonProperty("groupNo")
	private String groupNo;
	
	
	@JsonProperty("reasonCode")
	private String reasonCode;
	@JsonProperty("reasonRem")
	private String reasonRem;
	@JsonProperty("vehicleType")
	private String vehicleType;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public String getUpdMode() {
		return updMode;
	}
	public void setUpdMode(String updMode) {
		this.updMode = updMode;
	}
	public String getVehicleNo() {
		return vehicleNo;
	}
	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}
	public String getFromPlace() {
		return fromPlace;
	}
	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}
	public Number getFromState() {
		return fromState;
	}
	public void setFromState(Number fromState) {
		this.fromState = fromState;
	}
	public Number getTripshtNo() {
		return tripshtNo;
	}
	public void setTripshtNo(Number tripshtNo) {
		this.tripshtNo = tripshtNo;
	}
	public String getUserGSTINTransin() {
		return userGSTINTransin;
	}
	public void setUserGSTINTransin(String userGSTINTransin) {
		this.userGSTINTransin = userGSTINTransin;
	}
	public String getEnteredDate() {
		return enteredDate;
	}
	public void setEnteredDate(String enteredDate) {
		this.enteredDate = enteredDate;
	}
	public String getTransMode() {
		return transMode;
	}
	public void setTransMode(String transMode) {
		this.transMode = transMode;
	}
	public String getTransDocNo() {
		return transDocNo;
	}
	public void setTransDocNo(String transDocNo) {
		this.transDocNo = transDocNo;
	}
	public String getTransDocDate() {
		return transDocDate;
	}
	public void setTransDocDate(String transDocDate) {
		this.transDocDate = transDocDate;
	}
	public String getGroupNo() {
		return groupNo;
	}
	public void setGroupNo(String groupNo) {
		this.groupNo = groupNo;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getReasonRem() {
		return reasonRem;
	}
	public void setReasonRem(String reasonRem) {
		this.reasonRem = reasonRem;
	}
	public String getVehicleType() {
		return vehicleType;
	}
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	
	
	
	
}
