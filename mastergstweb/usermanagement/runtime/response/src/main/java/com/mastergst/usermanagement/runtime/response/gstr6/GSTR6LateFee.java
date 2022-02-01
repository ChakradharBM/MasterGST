package com.mastergst.usermanagement.runtime.response.gstr6;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR6LateFee {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double cLamt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double sLamt;
	private String debitId;
	private String date;
	private GSTR6Offset foroffset;
	public Double getcLamt() {
		return cLamt;
	}
	public void setcLamt(Double cLamt) {
		this.cLamt = cLamt;
	}
	public Double getsLamt() {
		return sLamt;
	}
	public void setsLamt(Double sLamt) {
		this.sLamt = sLamt;
	}
	public String getDebitId() {
		return debitId;
	}
	public void setDebitId(String debitId) {
		this.debitId = debitId;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public GSTR6Offset getForoffset() {
		return foroffset;
	}
	public void setForoffset(GSTR6Offset foroffset) {
		this.foroffset = foroffset;
	}
	
}
