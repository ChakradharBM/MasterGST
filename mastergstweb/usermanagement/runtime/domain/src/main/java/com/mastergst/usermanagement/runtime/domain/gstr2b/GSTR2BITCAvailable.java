package com.mastergst.usermanagement.runtime.domain.gstr2b;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class GSTR2BITCAvailable {
	@Id
	private ObjectId id;
	private GSTR2BNonRevSup nonrevsup;
	private GSTR2BISDSupplies isdsup;
	private GSTR2BRevSupplies revsup;
	private GSTR2BImports imports;
	private GSTR2BOthrSupplies othersup;

	public GSTR2BITCAvailable() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public GSTR2BNonRevSup getNonrevsup() {
		return nonrevsup;
	}

	public void setNonrevsup(GSTR2BNonRevSup nonrevsup) {
		this.nonrevsup = nonrevsup;
	}

	public GSTR2BISDSupplies getIsdsup() {
		return isdsup;
	}

	public void setIsdsup(GSTR2BISDSupplies isdsup) {
		this.isdsup = isdsup;
	}

	public GSTR2BRevSupplies getRevsup() {
		return revsup;
	}

	public void setRevsup(GSTR2BRevSupplies revsup) {
		this.revsup = revsup;
	}

	public GSTR2BImports getImports() {
		return imports;
	}

	public void setImports(GSTR2BImports imports) {
		this.imports = imports;
	}

	public GSTR2BOthrSupplies getOthersup() {
		return othersup;
	}

	public void setOthersup(GSTR2BOthrSupplies othersup) {
		this.othersup = othersup;
	}

}
