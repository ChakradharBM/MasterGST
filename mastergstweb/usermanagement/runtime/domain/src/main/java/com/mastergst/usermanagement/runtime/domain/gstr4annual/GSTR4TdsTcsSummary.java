package com.mastergst.usermanagement.runtime.domain.gstr4annual;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mastergst.core.util.CustomDoubleSerializer;

public class GSTR4TdsTcsSummary {
	private String ctin;
	private String gross_val;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double camt;
	@JsonSerialize(using = CustomDoubleSerializer.class)
	private Double samt;

	public String getCtin() {
		return ctin;
	}

	public void setCtin(String ctin) {
		this.ctin = ctin;
	}

	public String getGross_val() {
		return gross_val;
	}

	public void setGross_val(String gross_val) {
		this.gross_val = gross_val;
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
