package com.mastergst.usermanagement.runtime.response.gstr6;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class ITCDetails {
private String des;
@JsonSerialize(using = CustomDoubleSerializer.class)
private Double iamt;
@JsonSerialize(using = CustomDoubleSerializer.class)
private Double camt;
@JsonSerialize(using = CustomDoubleSerializer.class)
private Double samt;
@JsonSerialize(using = CustomDoubleSerializer.class)
private Double csamt;
public String getDes() {
	return des;
}
public void setDes(String des) {
	this.des = des;
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
public Double getCsamt() {
	return csamt;
}
public void setCsamt(Double csamt) {
	this.csamt = csamt;
}

}
