/*
 * Copyright (c) 2017, BVM Solutions  and/or its affiliates. All rights reserved.
 *
 */
package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.collect.Maps;
import com.mastergst.core.domain.Base;

/**
 * Role information
 * 
 * @author BVM Consultancy Services(www.bvmcs.com).
 * @version 1.0
 */
@Document(collection = "companyrole")
public class CompanyRole extends Base {
	
	private String userid;
	private String fullname;
	private String clientid;
	private String name;
	private String description;
	private Map<String, List<Permission>> permissions = MapUtils.lazyMap(new HashMap<String,List<Permission>>(), new Factory() {
        public Object create() {
            return LazyList.decorate(new ArrayList<Permission>(), 
                           FactoryUtils.instantiateFactory(Permission.class));
        }
    });
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFullname() {
		return fullname;
	}
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Map<String, List<Permission>> getPermissions() {
		return permissions;
	}
	public void setPermissions(Map<String, List<Permission>> permissions) {
		this.permissions = permissions;
	}
	
}
