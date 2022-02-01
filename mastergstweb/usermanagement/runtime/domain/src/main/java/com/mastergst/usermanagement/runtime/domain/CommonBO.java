/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import com.google.api.client.util.Lists;

public class CommonBO {

	private List<CompanyItems> items = Lists.newArrayList();
	private List<CompanyProducts> products = Lists.newArrayList();
	private List<CompanyServices> services = Lists.newArrayList();
	
	public List<CompanyItems> getItems() {
		return items;
	}
	public void setItems(List<CompanyItems> items) {
		this.items = items;
	}
	public List<CompanyProducts> getProducts() {
		return products;
	}
	public void setProducts(List<CompanyProducts> products) {
		this.products = products;
	}
	public List<CompanyServices> getServices() {
		return services;
	}
	public void setServices(List<CompanyServices> services) {
		this.services = services;
	}
	
}
