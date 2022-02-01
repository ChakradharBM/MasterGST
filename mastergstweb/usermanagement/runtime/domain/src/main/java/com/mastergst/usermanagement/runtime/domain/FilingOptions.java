package com.mastergst.usermanagement.runtime.domain;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class FilingOptions {

	@Id
	private ObjectId id;
	private String year;
	private String option;
	
	public FilingOptions(ObjectId id, String year, String option) {
		super();
		this.id = id;
		this.year = year;
		this.option = option;
	}

	public FilingOptions() {
		super();
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}
}
