package com.mastergst.usermanagement.runtime.domain;

public class ApisVersionFilter {
	
	private String[] type;
	private String[] name;
	private String[] method;
	private String[] sandboxVersion;
	private String[] productionVersion;
	private boolean[] webimpl;
	
	
	public String[] getType() {
		return type;
	}
	public void setType(String[] type) {
		this.type = type;
	}
	public void setType(String type) {
		this.type = type == null ? null : type.split(",");
	}
	public String[] getName() {
		return name;
	}
	public void setName(String[] name) {
		this.name = name;
	}
	public void setName(String name) {
		this.name = name == null ? null : name.split(",");
	}
	public String[] getMethod() {
		return method;
	}
	public void setMethod(String[] method) {
		this.method = method;
	}
	public void setMethod(String method) {
		this.method = method == null ? null : method.split(",");
	}
	public String[] getSandboxVersion() {
		return sandboxVersion;
	}
	public void setSandboxVersion(String[] sandboxVersion) {
		this.sandboxVersion = sandboxVersion;
	}
	public void setSandboxVersion(String sandboxVersion) {
		this.sandboxVersion = sandboxVersion == null ? null : sandboxVersion.split(",");
	}
	public String[] getProductionVersion() {
		return productionVersion;
	}
	public void setProductionVersion(String[] productionVersion) {
		this.productionVersion = productionVersion;
	}
	public void setProductionVersion(String productionVersion) {
		this.productionVersion = productionVersion == null ? null : productionVersion.split(",");
	}
	public boolean[] getWebimpl() {
		return webimpl;
	}
	public void setwebimpl(boolean[] webimpl) {
		this.webimpl = webimpl;
	}
}
