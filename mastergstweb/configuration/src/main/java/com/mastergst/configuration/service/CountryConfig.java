package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Mahendra Gadiparthi Country Configuration information.
 * 
 * @version 1.0
 */
@Document(collection = "country")
public class CountryConfig {

	private String code;
	private String name;

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

	@Override
	public String toString() {
		return "CountryConfig [code=" + code + ", name=" + name + "]";
	}
}
