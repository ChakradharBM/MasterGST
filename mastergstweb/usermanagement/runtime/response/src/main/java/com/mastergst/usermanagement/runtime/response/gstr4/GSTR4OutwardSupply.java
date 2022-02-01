package com.mastergst.usermanagement.runtime.response.gstr4;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR4OutwardSupply {
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double tax_val;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samt;
	
	public Double getTax_val() {
		return tax_val;
	}
	public void setTax_val(Double tax_val) {
		this.tax_val = tax_val;
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
	
}
