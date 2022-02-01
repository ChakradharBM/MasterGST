/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.response;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;


/**
 * This class is Reference Data POJO.
 * 
 * @author Ashok Samrat
 * @version 1.0
 * @since 1.0
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class EwayBillResponseData {
	
	@JsonProperty("ewbNo")
	private Number ewbillNo;
	@JsonProperty("ewayBillDate")
	private String ewbillDate;
	@JsonProperty("status")
	private String status;
	@JsonProperty("genGstin")
	private String genGSTIN;
	@JsonProperty("docNo")
	private String docNo;
	@JsonProperty("docDate")
	private String docDate;
	@JsonProperty("delPinCode")
	private Number delPinCode;
	@JsonProperty("delStateCode")
	private Number delStateCode;
	@JsonProperty("delPlace")
	private String delPlace;
	@JsonProperty("validUpto")
	private String validUpto;
	@JsonProperty("extendedTimes")
	private Number extendedTimes;
	@JsonProperty("rejectStatus")
	private String rejectStatus;
	
	
	@JsonProperty("genMode")
	private String genMode;
	@JsonProperty("userGstin")
	private String userGstin;
	@JsonProperty("supplyType")
	private String supplyType;
	@JsonProperty("subSupplyType")
	private String subSupplyType;
	@JsonProperty("subSupplyDesc")
	private String subSupplyDesc;
	@JsonProperty("docType")
	private String docType;
	@JsonProperty("fromGstin")
	private String fromGstin;
	@JsonProperty("fromTrdName")
	private String fromTrdName;
	@JsonProperty("fromAddr1")
	private String fromAddr1;
	@JsonProperty("fromAddr2")
	private String fromAddr2;
	@JsonProperty("fromPlace")
	private String fromPlace;
	@JsonProperty("fromPincode")
	private Number fromPincode;
	@JsonProperty("fromStateCode")
	private Number fromStateCode;
	@JsonProperty("toGstin")
	private String toGstin;
	@JsonProperty("toTrdName")
	private String toTrdName;
	@JsonProperty("toAddr1")
	private String toAddr1;
	@JsonProperty("toAddr2")
	private String toAddr2;
	@JsonProperty("toPlace")
	private String toPlace;
	@JsonProperty("toPincode")
	private Number toPincode;
	@JsonProperty("toStateCode")
	private Number toStateCode;
	@JsonProperty("totalValue")
	private Double totalValue;
	@JsonProperty("totInvValue")
	private Double totInvValue;
	@JsonProperty("cgstValue")
	private Double cgstValue;
	@JsonProperty("sgstValue")
	private Double sgstValue;
	@JsonProperty("igstValue")
	private Double igstValue;
	@JsonProperty("cessValue")
	private Double cessValue;
	@JsonProperty("transporterId")
	private String transporterId;
	@JsonProperty("transporterName")
	private String transporterName;
	@JsonProperty("actualDist")
	private Number actualDist;
	@JsonProperty("noValidDays")
	private Number noValidDays;
	@JsonProperty("vehicleType")
	private String vehicleType;
	@JsonProperty("actFromStateCode")
	private Number actFromStateCode;
	@JsonProperty("actToStateCode")
	private Number actToStateCode;
	@JsonProperty("transactionType")
	private Number transactionType;
	@JsonProperty("otherValue")
	private Double otherValue;
	@JsonProperty("cessNonAdvolValue")
	private Double cessNonAdvolValue;
	@JsonProperty("transDistance")
	private String transDistance;
	
	List<EwayBillItems> itemList=LazyList.decorate(new ArrayList<EwayBillItems>(), 
			FactoryUtils.instantiateFactory(EwayBillItems.class));
	@JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
	/* List<EwayBillVehicleListDetails> VehiclListDetails=LazyList.decorate(new ArrayList<EwayBillVehicleListDetails>(), 
			FactoryUtils.instantiateFactory(EwayBillVehicleListDetails.class)); */
	List<EBillVehicleListDetails> VehiclListDetails=LazyList.decorate(new ArrayList<EBillVehicleListDetails>(), 
			FactoryUtils.instantiateFactory(EBillVehicleListDetails.class));
	
	public EwayBillResponseData() {

	}

	public Number getEwbillNo() {
		return ewbillNo;
	}

	public void setEwbillNo(Number ewbillNo) {
		this.ewbillNo = ewbillNo;
	}

	public String getEwbillDate() {
		return ewbillDate;
	}

	public void setEwbillDate(String ewbillDate) {
		this.ewbillDate = ewbillDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getGenGSTIN() {
		return genGSTIN;
	}

	public void setGenGSTIN(String genGSTIN) {
		this.genGSTIN = genGSTIN;
	}

	public String getDocNo() {
		return docNo;
	}

	public void setDocNo(String docNo) {
		this.docNo = docNo;
	}

	public String getDocDate() {
		return docDate;
	}

	public void setDocDate(String docDate) {
		this.docDate = docDate;
	}

	public Number getDelPinCode() {
		return delPinCode;
	}

	public void setDelPinCode(Number delPinCode) {
		this.delPinCode = delPinCode;
	}

	public Number getDelStateCode() {
		return delStateCode;
	}

	public void setDelStateCode(Number delStateCode) {
		this.delStateCode = delStateCode;
	}

	public String getDelPlace() {
		return delPlace;
	}

	public void setDelPlace(String delPlace) {
		this.delPlace = delPlace;
	}

	public String getValidUpto() {
		return validUpto;
	}

	public void setValidUpto(String validUpto) {
		this.validUpto = validUpto;
	}

	public Number getExtendedTimes() {
		return extendedTimes;
	}

	public void setExtendedTimes(Number extendedTimes) {
		this.extendedTimes = extendedTimes;
	}

	public String getRejectStatus() {
		return rejectStatus;
	}

	public void setRejectStatus(String rejectStatus) {
		this.rejectStatus = rejectStatus;
	}

	public String getGenMode() {
		return genMode;
	}

	public void setGenMode(String genMode) {
		this.genMode = genMode;
	}

	public String getUserGstin() {
		return userGstin;
	}

	public void setUserGstin(String userGstin) {
		this.userGstin = userGstin;
	}

	public String getSupplyType() {
		return supplyType;
	}

	public void setSupplyType(String supplyType) {
		this.supplyType = supplyType;
	}

	public String getSubSupplyType() {
		return subSupplyType;
	}

	public void setSubSupplyType(String subSupplyType) {
		this.subSupplyType = subSupplyType;
	}

	public String getDocType() {
		return docType;
	}

	public void setDocType(String docType) {
		this.docType = docType;
	}

	public String getFromGstin() {
		return fromGstin;
	}

	public void setFromGstin(String fromGstin) {
		this.fromGstin = fromGstin;
	}

	public String getFromTrdName() {
		return fromTrdName;
	}

	public void setFromTrdName(String fromTrdName) {
		this.fromTrdName = fromTrdName;
	}

	public String getFromAddr1() {
		return fromAddr1;
	}

	public void setFromAddr1(String fromAddr1) {
		this.fromAddr1 = fromAddr1;
	}

	public String getFromAddr2() {
		return fromAddr2;
	}

	public void setFromAddr2(String fromAddr2) {
		this.fromAddr2 = fromAddr2;
	}

	public String getFromPlace() {
		return fromPlace;
	}

	public void setFromPlace(String fromPlace) {
		this.fromPlace = fromPlace;
	}

	public Number getFromPincode() {
		return fromPincode;
	}

	public void setFromPincode(Number fromPincode) {
		this.fromPincode = fromPincode;
	}

	public Number getFromStateCode() {
		return fromStateCode;
	}

	public void setFromStateCode(Number fromStateCode) {
		this.fromStateCode = fromStateCode;
	}

	public String getToGstin() {
		return toGstin;
	}

	public void setToGstin(String toGstin) {
		this.toGstin = toGstin;
	}

	public String getToTrdName() {
		return toTrdName;
	}

	public void setToTrdName(String toTrdName) {
		this.toTrdName = toTrdName;
	}

	public String getToAddr1() {
		return toAddr1;
	}

	public void setToAddr1(String toAddr1) {
		this.toAddr1 = toAddr1;
	}

	public String getToAddr2() {
		return toAddr2;
	}

	public void setToAddr2(String toAddr2) {
		this.toAddr2 = toAddr2;
	}

	public String getToPlace() {
		return toPlace;
	}

	public void setToPlace(String toPlace) {
		this.toPlace = toPlace;
	}

	public Number getToPincode() {
		return toPincode;
	}

	public void setToPincode(Number toPincode) {
		this.toPincode = toPincode;
	}

	public Number getToStateCode() {
		return toStateCode;
	}

	public void setToStateCode(Number toStateCode) {
		this.toStateCode = toStateCode;
	}

	public Double getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(Double totalValue) {
		this.totalValue = totalValue;
	}

	public Double getCgstValue() {
		return cgstValue;
	}

	public void setCgstValue(Double cgstValue) {
		this.cgstValue = cgstValue;
	}

	public Double getSgstValue() {
		return sgstValue;
	}

	public void setSgstValue(Double sgstValue) {
		this.sgstValue = sgstValue;
	}

	public Double getIgstValue() {
		return igstValue;
	}

	public void setIgstValue(Double igstValue) {
		this.igstValue = igstValue;
	}

	public Double getCessValue() {
		return cessValue;
	}

	public void setCessValue(Double cessValue) {
		this.cessValue = cessValue;
	}

	public String getTransporterId() {
		return transporterId;
	}

	public void setTransporterId(String transporterId) {
		this.transporterId = transporterId;
	}

	public String getTransporterName() {
		return transporterName;
	}

	public void setTransporterName(String transporterName) {
		this.transporterName = transporterName;
	}

	public Number getActualDist() {
		return actualDist;
	}

	public void setActualDist(Number actualDist) {
		this.actualDist = actualDist;
	}

	public Number getNoValidDays() {
		return noValidDays;
	}

	public void setNoValidDays(Number noValidDays) {
		this.noValidDays = noValidDays;
	}

	public String getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}

	public Number getActFromStateCode() {
		return actFromStateCode;
	}

	public void setActFromStateCode(Number actFromStateCode) {
		this.actFromStateCode = actFromStateCode;
	}

	public Number getActToStateCode() {
		return actToStateCode;
	}

	public void setActToStateCode(Number actToStateCode) {
		this.actToStateCode = actToStateCode;
	}

	public Number getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(Number transactionType) {
		this.transactionType = transactionType;
	}

	public Double getOtherValue() {
		return otherValue;
	}

	public void setOtherValue(Double otherValue) {
		this.otherValue = otherValue;
	}

	public Double getCessNonAdvolValue() {
		return cessNonAdvolValue;
	}

	public void setCessNonAdvolValue(Double cessNonAdvolValue) {
		this.cessNonAdvolValue = cessNonAdvolValue;
	}

	
	public List<EwayBillItems> getItemList() {
		return itemList;
	}

	public void setItemList(List<EwayBillItems> itemList) {
		this.itemList = itemList;
	}

	
	public List<EBillVehicleListDetails> getVehiclListDetails() {
		return VehiclListDetails;
	}

	public void setVehiclListDetails(List<EBillVehicleListDetails> vehiclListDetails) {
		VehiclListDetails = vehiclListDetails;
	}

	public Double getTotInvValue() {
		return totInvValue;
	}

	public void setTotInvValue(Double totInvValue) {
		this.totInvValue = totInvValue;
	}
	

	public String getTransDistance() {
		return transDistance;
	}

	public void setTransDistance(String transDistance) {
		this.transDistance = transDistance;
	}
	
	public String getSubSupplyDesc() {
		return subSupplyDesc;
	}

	public void setSubSupplyDesc(String subSupplyDesc) {
		this.subSupplyDesc = subSupplyDesc;
	}

	@Override
	public String toString() {
		return "EwayBillResponseData [ewbillNo=" + ewbillNo + ", ewbillDate=" + ewbillDate + ", status=" + status
				+ ", genGSTIN=" + genGSTIN + ", docNo=" + docNo + ", docDate=" + docDate + ", delPinCode=" + delPinCode
				+ ", delStateCode=" + delStateCode + ", delPlace=" + delPlace + ", validUpto=" + validUpto
				+ ", extendedTimes=" + extendedTimes + ", rejectStatus=" + rejectStatus + ", genMode=" + genMode
				+ ", userGstin=" + userGstin + ", supplyType=" + supplyType + ", subSupplyType=" + subSupplyType
				+ ", docType=" + docType + ", fromGstin=" + fromGstin + ", fromTrdName=" + fromTrdName + ", fromAddr1="
				+ fromAddr1 + ", fromAddr2=" + fromAddr2 + ", fromPlace=" + fromPlace + ", fromPincode=" + fromPincode
				+ ", fromStateCode=" + fromStateCode + ", toGstin=" + toGstin + ", toTrdName=" + toTrdName
				+ ", toAddr1=" + toAddr1 + ", toAddr2=" + toAddr2 + ", toPlace=" + toPlace + ", toPincode=" + toPincode
				+ ", toStateCode=" + toStateCode + ", totalValue=" + totalValue + ", totInvValue=" + totInvValue
				+ ", cgstValue=" + cgstValue + ", sgstValue=" + sgstValue + ", igstValue=" + igstValue + ", cessValue="
				+ cessValue + ", transporterId=" + transporterId + ", transporterName=" + transporterName
				+ ", actualDist=" + actualDist + ", noValidDays=" + noValidDays + ", vehicleType=" + vehicleType
				+ ", actFromStateCode=" + actFromStateCode + ", actToStateCode=" + actToStateCode + ", transactionType="
				+ transactionType + ", otherValue=" + otherValue + ", cessNonAdvolValue=" + cessNonAdvolValue
				+ ", itemList=" + itemList + ", VehiclListDetails=" + VehiclListDetails + "]";
	}

	

	
	
	
}
