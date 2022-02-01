package com.mastergst.usermanagement.runtime.domain;

import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "supplier_status")
public class SupplierStatus extends Base {

	private String arn;

	private String returnperiod;
	private String mof;
	private Date dof;
	private String returntype;
	private String status;
	private String gstin;
	private String clientid;
	private String supplierid;
	private String suppliername;

	public String getSuppliername() {
		return suppliername;
	}

	public void setSuppliername(String suppliername) {
		this.suppliername = suppliername;
	}

	public String getArn() {
		return arn;
	}

	public void setArn(String arn) {
		this.arn = arn;
	}

	public String getReturnperiod() {
		return returnperiod;
	}

	public void setReturnperiod(String returnperiod) {
		this.returnperiod = returnperiod;
	}

	public String getMof() {
		return mof;
	}

	public void setMof(String mof) {
		this.mof = mof;
	}

	public Date getDof() {
		return dof;
	}

	public void setDof(Date dof) {
		this.dof = dof;
	}

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
	public String getGstin() {
		return gstin;
	}

	public void setGstin(String gstin) {
		this.gstin = gstin;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(String supplierid) {
		this.supplierid = supplierid;
	}

	@Override
	public String toString() {
		return "SupplierStatus [arn=" + arn + ", returnperiod=" + returnperiod + ", mof=" + mof + ", dof=" + dof
				+ ", returntype=" + returntype + ", status=" + status + ", gstin=" + gstin
				+ ", clientid=" + clientid + ", supplierid=" + supplierid + ", suppliername=" + suppliername + "]";
	}
}
