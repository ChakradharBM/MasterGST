package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.GSTR8;
import com.mastergst.usermanagement.runtime.domain.GSTR8TCS;
import com.mastergst.usermanagement.runtime.repository.GSTR8Repository;
import com.mastergst.usermanagement.runtime.response.GSTR8GetResponse;

@Service
public class NewClientServiceImpl implements INewClientService {

	private static final Logger logger = LogManager.getLogger(NewClientServiceImpl.class.getName());
	private static final String CLASSNAME = "NewClientServiceImpl::";
		
	@Autowired
	private GSTR8Repository gstr8Repository;
	@Autowired
	private ClientService clientService;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	
	@Override
	@Transactional
	public GSTR8 saveEcomInvoice(GSTR8 gstr8, final String returnType) {
		
		logger.debug(CLASSNAME + "saveSuppliesInvoice : Begin");
		if (isEmpty(gstr8.getFp())) {
			Calendar cal = Calendar.getInstance();
			String strMonth = (cal.get(Calendar.MONTH) + 1) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1)
					: (cal.get(Calendar.MONTH) + 1) + "";
			String retPeriod = strMonth + cal.get(Calendar.YEAR);
			gstr8.setFp(retPeriod);
		}
		GSTR8 invoice = gstr8Repository.findByClientidAndFp(gstr8.getClientid(), gstr8.getFp());
		
		if(isEmpty(invoice)) {
			invoice = new GSTR8();
			Client client = clientService.findById(gstr8.getClientid());
			invoice.setFp(gstr8.getFp());
			invoice.setClientid(gstr8.getClientid());
			invoice.setUserid(gstr8.getUserid());
			invoice.setFullname(gstr8.getFullname());
			invoice.setGstin(client.getGstnnumber());
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsR())) {
			invoice.setTcsR(gstr8.getTcsR());
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsU())) {
			invoice.setTcsU(gstr8.getTcsU());
		}
		getTcs(gstr8, invoice);
		return gstr8Repository.save(invoice);
	}
	
	@Override
	@Transactional
	public GSTR8 saveEcomAInvoice(GSTR8 gstr8, final String returnType) {
		
		logger.debug(CLASSNAME + "saveEcomAInvoice : Begin");
		if (isEmpty(gstr8.getFp())) {
			Calendar cal = Calendar.getInstance();
			String strMonth = (cal.get(Calendar.MONTH) + 1) < 10 ? "0" + (cal.get(Calendar.MONTH) + 1)
					: (cal.get(Calendar.MONTH) + 1) + "";
			String retPeriod = strMonth + cal.get(Calendar.YEAR);
			gstr8.setFp(retPeriod);
		}
		GSTR8 invoice = gstr8Repository.findByClientidAndFp(gstr8.getClientid(), gstr8.getFp());
			
		if(isEmpty(invoice)) {
			invoice = new GSTR8();
			Client client = clientService.findById(gstr8.getClientid());
			invoice.setFp(gstr8.getFp());
			invoice.setClientid(gstr8.getClientid());
			invoice.setUserid(gstr8.getUserid());
			invoice.setFullname(gstr8.getFullname());
			invoice.setGstin(client.getGstnnumber());
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsaR())) {
			invoice.setTcsaR(gstr8.getTcsaR());
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsaU())) {
	
			invoice.setTcsaU(gstr8.getTcsaU());
		}
		getTcsA(gstr8, invoice);
		return gstr8Repository.save(invoice);
	}
	
	@Override
	public GSTR8 getGstr8(String clientid, String retPeriod) {
		
		return gstr8Repository.findByClientidAndFp(clientid, retPeriod);
	}
	public void getTcs(GSTR8 gstr8, GSTR8 target) {
		
		List<GSTR8TCS> tcsList = null;
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsR())) {
			
			Map<String, GSTR8TCS> tcsRMap = new HashMap<>();
			
			for(GSTR8TCS tcsr : gstr8.getTcsR()) {
				
				if(tcsRMap.containsKey(tcsr.getStin())) {
					Double supR = 0d, retsupR = 0d;
					Double supU = 0d, retsupU = 0d;
					Double amt = 0d, iamt = 0d, samt = 0d, camt = 0d;
					
					GSTR8TCS tcs = tcsRMap.get(tcsr.getStin());
					
					if(isNotEmpty(tcsr.getSupR())) {
						supR += tcsr.getSupR();					
					}
					if(isNotEmpty(tcsr.getRetsupR())) {
						retsupR += tcsr.getRetsupR();
					}
					if(isNotEmpty(tcsr.getAmt())) {
						amt += tcsr.getAmt();
					}
					if(isNotEmpty(tcsr.getIamt())) {
						iamt += tcsr.getIamt();
					}
					if(isNotEmpty(tcsr.getCamt())) {
						camt += tcsr.getCamt();
					}
					if(isNotEmpty(tcsr.getSamt())) {
						samt += tcsr.getSamt();
					}
					tcs.setSupR(tcs.getSupR() + supR);
					tcs.setRetsupR(tcs.getRetsupR() + retsupR);
					tcs.setSupU(supU);
					tcs.setRetsupU(retsupU);
					tcs.setAmt(tcs.getAmt() + amt);
					tcs.setIamt(tcs.getIamt() + iamt);
					tcs.setCamt(tcs.getCamt() + camt);
					tcs.setSamt(tcs.getSamt() + samt);
					tcsRMap.put(tcsr.getStin(), tcs);
				}else {
					tcsr.setSupU(0d);
					tcsr.setRetsupU(0d);
					if(isEmpty(tcsr.getRetsupR())) {
						tcsr.setRetsupR(0d);
					}
					tcsRMap.put(tcsr.getStin(), tcsr);
				}
			}
			
			tcsList = tcsRMap.values().stream().collect(Collectors.toList());
			
		}
		if(isEmpty(tcsList)) {
			tcsList = new ArrayList<>();
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsU()) && gstr8.getTcsU().size() > 0) {
			
			Double supR = 0d, retsupR = 0d;
			Double supU = 0d, retsupU = 0d;
			Double amt = 0d, iamt = 0d, samt = 0d, camt = 0d;
			for(GSTR8TCS tcsu : gstr8.getTcsU()) {
				if(isNotEmpty(tcsu.getSupU())) {
					supU += tcsu.getSupU();					
				}
				if(isNotEmpty(tcsu.getRetsupU())) {
					retsupU += tcsu.getRetsupU();
				}
				if(isNotEmpty(tcsu.getAmt())) {
					amt += tcsu.getAmt();
				}
				if(isNotEmpty(tcsu.getIamt())) {
					iamt += tcsu.getIamt();
				}
				if(isNotEmpty(tcsu.getCamt())) {
					camt += tcsu.getCamt();
				}
				if(isNotEmpty(tcsu.getSamt())) {
					samt += tcsu.getSamt();
				}
			}
			GSTR8TCS tcs = new GSTR8TCS();
			tcs.setSupR(supR);
			tcs.setRetsupR(retsupR);
			tcs.setSupU(supU);
			tcs.setRetsupU(+ retsupU);
			tcs.setAmt(+ amt);
			tcs.setIamt(iamt);
			tcs.setCamt(camt);
			tcs.setSamt(samt);
			tcsList.add(tcs);
		}
		
		target.setTcs(tcsList);
	}
	
	public void getTcsA(GSTR8 gstr8, GSTR8 target) {
		
		List<GSTR8TCS> tcsaList = null;
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsaR())) {
			
			Map<String, GSTR8TCS> tcsaRMap = new HashMap<>();
			
			for(GSTR8TCS tcsar : gstr8.getTcsaR()) {
				
				if(tcsaRMap.containsKey(tcsar.getStin())) {
					Double supR = 0d, retsupR = 0d;
					Double supU = 0d, retsupU = 0d;
					Double amt = 0d, iamt = 0d, samt = 0d, camt = 0d;
					
					GSTR8TCS tcs = tcsaRMap.get(tcsar.getStin());
					
					if(isNotEmpty(tcsar.getSupR())) {
						supU += tcsar.getSupR();					
					}
					if(isNotEmpty(tcsar.getRetsupR())) {
						retsupU += tcsar.getRetsupR();
					}
					if(isNotEmpty(tcsar.getAmt())) {
						amt += tcsar.getAmt();
					}
					if(isNotEmpty(tcsar.getIamt())) {
						iamt += tcsar.getIamt();
					}
					if(isNotEmpty(tcsar.getCamt())) {
						camt += tcsar.getCamt();
					}
					if(isNotEmpty(tcsar.getSamt())) {
						samt += tcsar.getSamt();
					}

					tcs.setSupR(tcs.getSupR() + supR);
					tcs.setRetsupR(tcs.getRetsupR() + retsupR);
					tcs.setSupU(supU);
					tcs.setRetsupU(retsupU);
					tcs.setAmt(tcs.getAmt() + amt);
					tcs.setIamt(tcs.getIamt() + iamt);
					tcs.setCamt(tcs.getCamt() + camt);
					tcs.setSamt(tcs.getSamt() + samt);
					tcsaRMap.put(tcsar.getStin(), tcs);
				}else {
					tcsar.setSupU(0d);
					tcsar.setRetsupU(0d);
					tcsaRMap.put(tcsar.getStin(), tcsar);
				}
			}
			
			tcsaList = tcsaRMap.values().stream().collect(Collectors.toList());	
		}
		if(isEmpty(tcsaList)) {
			tcsaList = new ArrayList<>();
		}
		if(isNotEmpty(gstr8) && isNotEmpty(gstr8.getTcsaU()) && gstr8.getTcsaU().size() > 0) {
			
			Double supR = 0d, retsupR = 0d;
			Double supU = 0d, retsupU = 0d;
			Double amt = 0d, iamt = 0d, samt = 0d, camt = 0d;
			for(GSTR8TCS tcsau : gstr8.getTcsaU()) {
					
				if(isNotEmpty(tcsau.getSupU())) {
					supU += tcsau.getSupU();					
				}
				if(isNotEmpty(tcsau.getRetsupU())) {
					retsupU += tcsau.getRetsupU();
				}
				if(isNotEmpty(tcsau.getAmt())) {
					amt += tcsau.getAmt();
				}
				if(isNotEmpty(tcsau.getIamt())) {
					iamt += tcsau.getIamt();
				}
				if(isNotEmpty(tcsau.getCamt())) {
					camt += tcsau.getCamt();
				}
				if(isNotEmpty(tcsau.getSamt())) {
					samt += tcsau.getSamt();
				}
			}
			GSTR8TCS tcs = new GSTR8TCS();
			tcs.setSupR(supR);
			tcs.setRetsupR(retsupR);
			tcs.setSupU(supU);
			tcs.setRetsupU(+ retsupU);
			tcs.setAmt(+ amt);
			tcs.setIamt(iamt);
			tcs.setCamt(camt);
			tcs.setSamt(samt);
			tcsaList.add(tcs);
		}
		
		target.setTcsa(tcsaList);
	}
	
	@Override
	public GSTR8GetResponse syncEcomInvoices(Client client, String userid, String returntype, int month, int year) {
	
		String strMonth = month < 10 ? "0" + month : month + "";
		String retPeriod = strMonth+year;
		GSTR8GetResponse response = null; 
		try {
			response = iHubConsumerService.getGSTR8Details(client, MasterGSTConstants.GSTR8, client.getGstnnumber(), retPeriod); 
			return response; 
		} catch (Exception e) {
			  
			response = new GSTR8GetResponse();
			response.setStatuscd("0");
			response.setStatusdesc(e.getMessage()); 
			return response; 
		}
	}
}
