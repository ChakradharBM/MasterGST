package com.mastergst.configuration.service;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReconcileTempRepository extends MongoRepository<ReconcileTemp, String>{

	public ReconcileTemp findByClientidAndInvtype(String clientId, String invType);
	public ReconcileTemp findByClientidAndInvtypeAndMonthlyoryearly(String clientId, String invType,String monthlyoryearly);
	
	public ReconcileTemp findByMonthlyoryearlyAndClientid(String monthlyoryearly,String clientId);
	
	public ReconcileTemp findByClientid(String clientid);
	public List<ReconcileTemp> findByClientidAndMonthlyoryearly(String clientid,String monthlyoryearly);
}

