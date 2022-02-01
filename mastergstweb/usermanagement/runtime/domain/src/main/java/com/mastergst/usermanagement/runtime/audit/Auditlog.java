package com.mastergst.usermanagement.runtime.audit;


import org.springframework.data.mongodb.core.mapping.Document;
import com.mastergst.core.domain.Base;


@Document(collection="auditlog")
public class Auditlog extends Base{
	private String parentid;
	private String userid;
	private String clientid;
	private String clientname;
	private String username;
	private String useremail;
	private String action;
	private String ipAddress;
	private String gstn;
	private String invoicenumber;
	private String description;
	private String returntype;
	private String mthCd;
	private String yrCd;
	private String qtCd;
	private String createddate_str;
	
	private AuditingFields auditingFields;
	
	
	public Auditlog(String parentid,String userid,String clientid,String clientname,String gstn,String username,String useremail,String action, String ipAddress,String invoicenumber,String description,String returntype,String mthCd,String yrCd,String qtCd,String createddate_str,AuditingFields auditingFields) {
		this.parentid = parentid;
		this.userid = userid;
		this.clientid = clientid;
		this.clientname = clientname;
		this.gstn = gstn;
		this.username = username;
		this.useremail = useremail;
		this.action = action;
		this.ipAddress = ipAddress;
		this.invoicenumber = invoicenumber;
		this.description = description;
		this.returntype = returntype;
		this.mthCd = mthCd;
		this.yrCd = yrCd;
		this.qtCd = qtCd;
		this.createddate_str = createddate_str;
		this.auditingFields = auditingFields;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public String getClientname() {
		return clientname;
	}

	public void setClientname(String clientname) {
		this.clientname = clientname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getGstn() {
		return gstn;
	}

	public void setGstn(String gstn) {
		this.gstn = gstn;
	}

	public String getInvoicenumber() {
		return invoicenumber;
	}

	public void setInvoicenumber(String invoicenumber) {
		this.invoicenumber = invoicenumber;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getReturntype() {
		return returntype;
	}

	public void setReturntype(String returntype) {
		this.returntype = returntype;
	}

	public String getMthCd() {
		return mthCd;
	}

	public void setMthCd(String mthCd) {
		this.mthCd = mthCd;
	}

	public String getYrCd() {
		return yrCd;
	}

	public void setYrCd(String yrCd) {
		this.yrCd = yrCd;
	}

	public String getQtCd() {
		return qtCd;
	}

	public void setQtCd(String qtCd) {
		this.qtCd = qtCd;
	}

	public String getCreateddate_str() {
		return createddate_str;
	}

	public void setCreateddate_str(String createddate_str) {
		this.createddate_str = createddate_str;
	}

	public AuditingFields getAuditingFields() {
		return auditingFields;
	}

	public void setAuditingFields(AuditingFields auditingFields) {
		this.auditingFields = auditingFields;
	}
	
}
