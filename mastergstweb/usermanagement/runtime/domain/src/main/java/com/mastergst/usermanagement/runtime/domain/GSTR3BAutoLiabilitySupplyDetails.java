package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GSTR3BAutoLiabilitySupplyDetails {
	@Id
	private ObjectId id;
	@JsonProperty("osup_3_1a")
	private GSTR3BAutoLiabilityDetails liability31a;
	@JsonProperty("osup_3_1b")
	private GSTR3BAutoLiabilityDetails liability31b;
	@JsonProperty("osup_3_1c")
	private GSTR3BAutoLiabilityDetails liability31c;
	@JsonProperty("osup_3_1d")
	private GSTR3BAutoLiabilityDetails liability31d;
	@JsonProperty("osup_3_1e")
	private GSTR3BAutoLiabilityDetails liability31e;

	@JsonProperty("osup_comp_3_2")
	private GSTR3BInterAutoLiabilityDetails comp32;
	@JsonProperty("osup_unreg_3_2")
	private GSTR3BInterAutoLiabilityDetails unreg32;
	@JsonProperty("osup_uin_3_2")
	private GSTR3BInterAutoLiabilityDetails uin32;

	private GSTR3BAutoLiabilityDetails itc4a1;
	private GSTR3BAutoLiabilityDetails itc4a3;
	private GSTR3BAutoLiabilityDetails itc4a4;
	private GSTR3BAutoLiabilityDetails itc4a5;
	private GSTR3BAutoLiabilityDetails itc4b2;

	public GSTR3BAutoLiabilitySupplyDetails() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR3BAutoLiabilityDetails getLiability31a() {
		return liability31a;
	}

	public void setLiability31a(GSTR3BAutoLiabilityDetails liability31a) {
		this.liability31a = liability31a;
	}

	public GSTR3BAutoLiabilityDetails getLiability31b() {
		return liability31b;
	}

	public void setLiability31b(GSTR3BAutoLiabilityDetails liability31b) {
		this.liability31b = liability31b;
	}

	public GSTR3BAutoLiabilityDetails getLiability31c() {
		return liability31c;
	}

	public void setLiability31c(GSTR3BAutoLiabilityDetails liability31c) {
		this.liability31c = liability31c;
	}

	public GSTR3BAutoLiabilityDetails getLiability31d() {
		return liability31d;
	}

	public void setLiability31d(GSTR3BAutoLiabilityDetails liability31d) {
		this.liability31d = liability31d;
	}

	public GSTR3BAutoLiabilityDetails getLiability31e() {
		return liability31e;
	}

	public void setLiability31e(GSTR3BAutoLiabilityDetails liability31e) {
		this.liability31e = liability31e;
	}

	public GSTR3BInterAutoLiabilityDetails getComp32() {
		return comp32;
	}

	public void setComp32(GSTR3BInterAutoLiabilityDetails comp32) {
		this.comp32 = comp32;
	}

	public GSTR3BInterAutoLiabilityDetails getUnreg32() {
		return unreg32;
	}

	public void setUnreg32(GSTR3BInterAutoLiabilityDetails unreg32) {
		this.unreg32 = unreg32;
	}

	public GSTR3BInterAutoLiabilityDetails getUin32() {
		return uin32;
	}

	public void setUin32(GSTR3BInterAutoLiabilityDetails uin32) {
		this.uin32 = uin32;
	}

	public GSTR3BAutoLiabilityDetails getItc4a1() {
		return itc4a1;
	}

	public void setItc4a1(GSTR3BAutoLiabilityDetails itc4a1) {
		this.itc4a1 = itc4a1;
	}

	public GSTR3BAutoLiabilityDetails getItc4a3() {
		return itc4a3;
	}

	public void setItc4a3(GSTR3BAutoLiabilityDetails itc4a3) {
		this.itc4a3 = itc4a3;
	}

	public GSTR3BAutoLiabilityDetails getItc4a4() {
		return itc4a4;
	}

	public void setItc4a4(GSTR3BAutoLiabilityDetails itc4a4) {
		this.itc4a4 = itc4a4;
	}

	public GSTR3BAutoLiabilityDetails getItc4a5() {
		return itc4a5;
	}

	public void setItc4a5(GSTR3BAutoLiabilityDetails itc4a5) {
		this.itc4a5 = itc4a5;
	}

	public GSTR3BAutoLiabilityDetails getItc4b2() {
		return itc4b2;
	}

	public void setItc4b2(GSTR3BAutoLiabilityDetails itc4b2) {
		this.itc4b2 = itc4b2;
	}

}
