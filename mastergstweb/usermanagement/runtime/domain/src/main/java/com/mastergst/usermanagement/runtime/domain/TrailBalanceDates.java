package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

public class TrailBalanceDates {

	private Date startdate;
	private Date enddate;

	public TrailBalanceDates() {
		super();
	}

	public TrailBalanceDates(Date startdate, Date enddate) {
		super();
		this.startdate = startdate;
		this.enddate = enddate;
	}

	public Date getStartdate() {
		return startdate;
	}

	public void setStartdate(Date startdate) {
		this.startdate = startdate;
	}

	public Date getEnddate() {
		return enddate;
	}

	public void setEnddate(Date enddate) {
		this.enddate = enddate;
	}

	@Override
	public String toString() {
		return "TrailBalanceDates [startdate=" + startdate + ", enddate=" + enddate + "]";
	}
}
