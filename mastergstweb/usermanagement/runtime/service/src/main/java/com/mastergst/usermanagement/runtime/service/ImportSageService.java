package com.mastergst.usermanagement.runtime.service;

import java.util.Date;
import java.util.Map;

import com.mastergst.configuration.service.StateConfig;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

public interface ImportSageService {
public InvoiceParent getInvoice(Client client,InvoiceParent invoice,String[] patterns,int year,int month,String returntype,Map<String,StateConfig> statetin,String imptype);
public Date invdate(String invdate,String[] patterns,int year,int month);
public int month(String mnth);
public String beantype(String invtype);
}
