package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "apis_version")
public class ApisVesrionConfig extends Base {

	private String docid;
	private String api;
	private String type;
	private String name;
	private String method;
	private String sandboxVersion;
	private String productionVersion;
	private boolean webimpl;

	public String getDocid() {
		return docid;
	}

	public void setDocid(String docid) {
		this.docid = docid;
	}

	public String getApi() {
		return api;
	}

	public void setApi(String api) {
		this.api = api;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getSandboxVersion() {
		return sandboxVersion;
	}

	public void setSandboxVersion(String sandboxVersion) {
		this.sandboxVersion = sandboxVersion;
	}

	public String getProductionVersion() {
		return productionVersion;
	}

	public void setProductionVersion(String productionVersion) {
		this.productionVersion = productionVersion;
	}

	public boolean isWebimpl() {
		return webimpl;
	}

	public void setWebimpl(boolean webimpl) {
		this.webimpl = webimpl;
	}
}
