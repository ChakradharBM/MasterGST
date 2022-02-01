package com.mastergst.usermanagement.runtime.support;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mastergst.configuration.service.ReconcileTemp;
import com.mastergst.configuration.service.ReconcileTempRepository;

@Component
public class ReconcileUtility {
	
	@Autowired private ReconcileTempRepository reconcileTempRepository;
	
	
	public ReconcileTemp initialUpdateReconcileData(String clientid, String returntype, long contentSize) {
		
		ReconcileTemp recon = reconcileTempRepository.findByClientid(clientid);
		//Long totalProcessed = 0l;
		if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2ab2binvoices())) {
			contentSize += recon.getProcessedgstr2ab2binvoices();
		}
		if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2acreditinvoices())) {
			contentSize += recon.getProcessedgstr2acreditinvoices();
		}
		if(isNotEmpty(recon) && isNotEmpty(recon.getProcessedgstr2aimpginvoices())) {
			contentSize += recon.getProcessedgstr2aimpginvoices();
		}
		recon.setProcessedgstr2ainvoices(contentSize);
		recon = reconcileTempRepository.save(recon);
		return recon;
	}
	
	

}
