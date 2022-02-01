package com.mastergst.usermanagement.runtime.domain;

import org.springframework.stereotype.Component;

@Component
public class AdminSummary {

	private String app;
	private String usertype;
	private Object singup;
	private Object notrequired;
	private Object democompleted;
	private Object readytogo;
	private Object paid;
	private Object revenue;
	private Object totalRevenue;

	public AdminSummary() {
		super();
	}

	public AdminSummary(String app, String object, Object singup, Object notrequired, Object democompleted,
			Object readytogo, Object paid, Object revenue,Object totalRevenue) {
		super();
		this.app = app;
		this.usertype = object;
		this.singup = singup;
		this.notrequired = notrequired;
		this.democompleted = democompleted;
		this.readytogo = readytogo;
		this.paid = paid;
		this.revenue = revenue;
		this.totalRevenue = totalRevenue;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public Object getSingup() {
		return singup;
	}

	public void setSingup(Object singup) {
		this.singup = singup;
	}

	public Object getNotrequired() {
		return notrequired;
	}

	public void setNotrequired(Object notrequired) {
		this.notrequired = notrequired;
	}

	public Object getDemocompleted() {
		return democompleted;
	}

	public void setDemocompleted(Object democompleted) {
		this.democompleted = democompleted;
	}

	public Object getReadytogo() {
		return readytogo;
	}

	public void setReadytogo(Object readytogo) {
		this.readytogo = readytogo;
	}

	public Object getPaid() {
		return paid;
	}

	public void setPaid(Object paid) {
		this.paid = paid;
	}

	public Object getRevenue() {
		return revenue;
	}

	public void setRevenue(Object revenue) {
		this.revenue = revenue;
	}

	public Object getTotalRevenue() {
		return totalRevenue;
	}

	public void setTotalRevenue(Object totalRevenue) {
		this.totalRevenue = totalRevenue;
	}

	@Override
	public String toString() {
		return "AdminSummary [app=" + app + ", usertype=" + usertype + ", singup=" + singup + ", notrequired="
				+ notrequired + ", democompleted=" + democompleted + ", readytogo=" + readytogo + ", paid=" + paid
				+ ", revenue=" + revenue + ", totalRevenue=" + totalRevenue + "]";
	}
}
