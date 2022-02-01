package com.mastergst.usermanagement.runtime.dao;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.core.common.MasterGSTConstants;

@Service
public class InvoiceDaoFactory {

	@Autowired
	private Gstr1Dao gstr1Dao;
	@Autowired
	private Gstr2Dao gstr2Dao;
	@Autowired
	private Gstr4Dao gstr4Dao;
	@Autowired
	private Gstr5Dao gstr5Dao;
	@Autowired
	private Gstr6Dao gstr6Dao;
	@Autowired
	private PurchageRegisterDao purchageRegisterDao;
	@Autowired
	private EwayBillDao ewayBillDao;
	@Autowired
	private DeliveryChallanaDao deliveryChallanDao;
	@Autowired
	private EstimatesDao estimatesDao;
	@Autowired
	private ProformaDao proformaDao;
	@Autowired
	private PurchaseOrderDao purchaseOrderDao;
	
	private Map<String, InvoiceDao> invoiceDaos;
	
    @PostConstruct
    public void init(){
        invoiceDaos = new HashMap<String, InvoiceDao>();
        invoiceDaos.put(MasterGSTConstants.GSTR1, gstr1Dao);
        invoiceDaos.put(MasterGSTConstants.GSTR2, gstr2Dao);
        invoiceDaos.put(MasterGSTConstants.GSTR4, gstr4Dao);
        invoiceDaos.put(MasterGSTConstants.GSTR5, gstr5Dao);
        invoiceDaos.put(MasterGSTConstants.GSTR6, gstr6Dao);
        invoiceDaos.put(MasterGSTConstants.PURCHASE_REGISTER, purchageRegisterDao);
        invoiceDaos.put(MasterGSTConstants.EWAYBILL, ewayBillDao);
        invoiceDaos.put(MasterGSTConstants.DELIVERYCHALLANS, deliveryChallanDao);
        invoiceDaos.put(MasterGSTConstants.ESTIMATES, estimatesDao);
        invoiceDaos.put(MasterGSTConstants.PROFORMAINVOICES, proformaDao);
        invoiceDaos.put(MasterGSTConstants.PURCHASEORDER, purchaseOrderDao);
    }
    
    public InvoiceDao getInvoiceDao(String returnType){
        return invoiceDaos.get(returnType);
    }
}
