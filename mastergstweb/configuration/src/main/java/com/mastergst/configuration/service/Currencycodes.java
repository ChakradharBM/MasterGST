package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "currencycodes")
public class Currencycodes extends Base {
	
	private String code;
	private String name;
	private String symbolcode;
	private String mainunit;
	private String fractionunit;
	private String countrycode;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSymbolcode() {
		return symbolcode;
	}
	public void setSymbolcode(String symbolcode) {
		this.symbolcode = symbolcode;
	}
	public String getMainunit() {
		return mainunit;
	}
	public void setMainunit(String mainunit) {
		this.mainunit = mainunit;
	}
	public String getFractionunit() {
		return fractionunit;
	}
	public void setFractionunit(String fractionunit) {
		this.fractionunit = fractionunit;
	}
	public String getCountrycode() {
		return countrycode;
	}
	public void setCountrycode(String countrycode) {
		this.countrycode = countrycode;
	}
	
	

}
