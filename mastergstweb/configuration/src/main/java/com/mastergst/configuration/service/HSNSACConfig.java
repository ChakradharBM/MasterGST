/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */

package com.mastergst.configuration.service;

import java.util.List;

import com.google.common.collect.Lists;

public class HSNSACConfig {
	private List<HSNConfig> hsnConfig = Lists.newArrayList();
	private List<ServiceConfig> sacConfig = Lists.newArrayList();

	public List<HSNConfig> getHsnConfig() {
		return hsnConfig;
	}

	public void setHsnConfig(List<HSNConfig> hsnConfig) {
		this.hsnConfig = hsnConfig;
	}

	public List<ServiceConfig> getSacConfig() {
		return sacConfig;
	}

	public void setSacConfig(List<ServiceConfig> sacConfig) {
		this.sacConfig = sacConfig;
	}

}
