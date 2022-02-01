package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "industryType")
public class IndustryType {
private String name;

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

}
