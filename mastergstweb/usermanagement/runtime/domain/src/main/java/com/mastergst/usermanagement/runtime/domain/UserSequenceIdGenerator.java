package com.mastergst.usermanagement.runtime.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import com.mastergst.core.domain.Base;

@Document(collection = "usersequence_generator")
public class UserSequenceIdGenerator extends Base {

	public Long userSequenceId;

	public String sequenceIdName;

	public Long getUserSequenceId() {
		return userSequenceId;
	}

	public void setUserSequenceId(Long userSequenceId) {
		this.userSequenceId = userSequenceId;
	}

	public String getSequenceIdName() {
		return sequenceIdName;
	}

	public void setSequenceIdName(String sequenceIdName) {
		this.sequenceIdName = sequenceIdName;
	}
}
