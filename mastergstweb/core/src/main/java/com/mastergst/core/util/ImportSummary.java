package com.mastergst.core.util;

public class ImportSummary {

	private String name;
	private long total;
	private long success;
	private long failed;

	private long totalinvs;
	
	private long invsuccess;
	private long invfailed;
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getSuccess() {
		return success;
	}

	public void setSuccess(long success) {
		this.success = success;
	}

	public long getFailed() {
		return failed;
	}

	public void setFailed(long failed) {
		this.failed = failed;
	}

	public long getTotalinvs() {
		return totalinvs;
	}

	public void setTotalinvs(long totalinvs) {
		this.totalinvs = totalinvs;
	}

	public long getInvsuccess() {
		return invsuccess;
	}

	public void setInvsuccess(long invsuccess) {
		this.invsuccess = invsuccess;
	}

	public long getInvfailed() {
		return invfailed;
	}

	public void setInvfailed(long invfailed) {
		this.invfailed = invfailed;
	}
	

}
