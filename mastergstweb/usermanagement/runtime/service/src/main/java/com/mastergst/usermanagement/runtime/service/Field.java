package com.mastergst.usermanagement.runtime.service;

import java.util.function.Function;

public class Field {
	
	private String name;
	private String code;
	private String conversion;
	private String attrs;
	
	private Function<Object, Boolean> validator; 
	
	public Field(String name, String code, String conversion, String attrs, Function<Object, Boolean> validator){
		this.name = name;
		this.code = code;
		this.conversion = conversion;
		this.attrs = attrs;
		this.validator = validator;
	}

	public String getName() {
		return name;
	}

	public String getCode() {
		return code;
	}

	public String getConversion() {
		return conversion;
	}

	public String getAttrs() {
		return attrs;
	}
	
	
}
