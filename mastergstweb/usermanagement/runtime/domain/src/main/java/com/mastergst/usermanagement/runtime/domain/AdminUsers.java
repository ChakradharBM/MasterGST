package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Factory;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.list.LazyList;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "admin_user")
public class AdminUsers extends Base{

	private String adminUserId;
	private String adminUserName;
	private String adminUserEmail;
	private String adminUserPassword;

	private Map<String, List<Permission>> permissions = MapUtils.lazyMap(new HashMap<String, List<Permission>>(),
			new Factory() {
				public Object create() {
					return LazyList.decorate(new ArrayList<Permission>(),
							FactoryUtils.instantiateFactory(Permission.class));
				}
			});

	/*
	 * private String adminUserREPORTS; private String adminUserERROR_LOG; private
	 * String adminUserMESSAGES; private String adminUserPAYMENTS; private String
	 * adminUserDETAILS; private String adminUserGST_PRODUCTION; private String
	 * adminUserEWAY_BILL_PRODUCTION;
	 */

	public AdminUsers() {

	}

	public AdminUsers(String adminUserId) {
		super();
		this.adminUserId = adminUserId;
	}

	public String getAdminUserId() {
		return adminUserId;
	}

	public void setAdminUserId(String adminUserId) {
		this.adminUserId = adminUserId;
	}

	public String getAdminUserName() {
		return adminUserName;
	}

	public void setAdminUserName(String adminUserName) {
		this.adminUserName = adminUserName;
	}

	public String getAdminUserEmail() {
		return adminUserEmail;
	}

	public void setAdminUserEmail(String adminUserEmail) {
		this.adminUserEmail = adminUserEmail;
	}

	public String getAdminUserPassword() {
		return adminUserPassword;
	}

	public void setAdminUserPassword(String adminUserPassword) {
		this.adminUserPassword = adminUserPassword;
	}

	public Map<String, List<Permission>> getPermissions() {
		return permissions;
	}

	public void setPermissions(Map<String, List<Permission>> permissions) {
		this.permissions = permissions;
	}

	@Override
	public String toString() {
		return "AdminUsers [adminUserId=" + adminUserId + ", adminUserName=" + adminUserName + ", adminUserEmail="
				+ adminUserEmail + ", adminUserPassword=" + adminUserPassword + ", permissions=" + permissions + "]";
	}
}
