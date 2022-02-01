package com.mastergst.usermanagement.runtime.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "proformainvoices")
@JsonFilter("gstr1Filter")
@JsonIgnoreProperties({"id", "userid", "fullname", "clientid", "createdDate", "createdBy", "updatedDate", "updatedBy", 
	"totaltax", "totalamount", "totaltaxableamount", "totalitc", "dateofinvoice", "statename", "invtype", "revchargetype", "billedtoname", 
	"isbilledto", "consigneename", "consigneepos", "consigneeaddress", "ecomoperatorid", "invoiceno", "gstStatus", "gstRefId","matchingId", 
	"matchingStatus", "errorMsg", "items","bankDetails","strDate","referenceNumber","ewayBillNumber","branch","vertical","notes","terms",
	"strAmendment","isAmendment","amendment"})
public class ProformaInvoices extends InvoiceParent {
	
	List<PreformaInvoice> pi=LazyList.decorate(new ArrayList<PreformaInvoice>(), 
			FactoryUtils.instantiateFactory(PreformaInvoice.class));
	
	public List<PreformaInvoice> getPi() {
		return pi;
	}

	public void setPi(List<PreformaInvoice> pi) {
		this.pi = pi;
	}

}
