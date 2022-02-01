package com.mastergst.core.util;

import java.util.List;

import com.google.common.collect.Lists;

public class ImportResponse {

	private int month;
	private int year;
	private String error;
	private List<ImportSummary> summaryList = Lists.newArrayList();
	private boolean showLink = false;

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public List<ImportSummary> getSummaryList() {
		return summaryList;
	}

	public void setSummaryList(List<ImportSummary> summaryList) {
		this.summaryList = summaryList;
	}

	public boolean isShowLink() {
		return showLink;
	}

	public void setShowLink(boolean showLink) {
		this.showLink = showLink;
	}
}
