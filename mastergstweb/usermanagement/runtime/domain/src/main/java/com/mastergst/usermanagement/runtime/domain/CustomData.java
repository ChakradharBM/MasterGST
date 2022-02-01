package com.mastergst.usermanagement.runtime.domain;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

public class CustomData {
	@Id
	private ObjectId id;
	private String customFieldName;
	private String customFieldType;
	private Boolean displayInPrint = false;
	private Boolean displayInFilters = false;
	private Boolean isMandatory = false;
	//private Boolean isDuplicatesAllow = false;
	private List<String> typeData;
	private String typeId;

	public CustomData() {
		this.id = ObjectId.get();
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getCustomFieldName() {
		return customFieldName;
	}

	public void setCustomFieldName(String customFieldName) {
		this.customFieldName = customFieldName;
	}

	public String getCustomFieldType() {
		return customFieldType;
	}

	public void setCustomFieldType(String customFieldType) {
		this.customFieldType = customFieldType;
	}

	public Boolean getDisplayInPrint() {
		return displayInPrint;
	}

	public void setDisplayInPrint(Boolean displayInPrint) {
		this.displayInPrint = displayInPrint;
	}

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public List<String> getTypeData() {
		return typeData;
	}

	public void setTypeData(List<String> typeData) {
		this.typeData = typeData;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	/*public Boolean getIsDuplicatesAllow() {
		return isDuplicatesAllow;
	}

	public void setIsDuplicatesAllow(Boolean isDuplicatesAllow) {
		this.isDuplicatesAllow = isDuplicatesAllow;
	}*/

	public Boolean getDisplayInFilters() {
		return displayInFilters;
	}

	public void setDisplayInFilters(Boolean displayInFilters) {
		this.displayInFilters = displayInFilters;
	}
}
