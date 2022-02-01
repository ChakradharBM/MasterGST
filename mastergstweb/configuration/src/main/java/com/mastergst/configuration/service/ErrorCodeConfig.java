package com.mastergst.configuration.service;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "errorcodes")
public class ErrorCodeConfig {
private String errorCode;
private String errorDesc;
public String getErrorCode() {
	return errorCode;
}
public void setErrorCode(String errorCode) {
	this.errorCode = errorCode;
}
public String getErrorDesc() {
	return errorDesc;
}
public void setErrorDesc(String errorDesc) {
	this.errorDesc = errorDesc;
}

}
