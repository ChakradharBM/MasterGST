package com.mastergst.usermanagement.runtime.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.util.StringUtil;
import com.mastergst.usermanagement.runtime.dao.SmtpDetailsDao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientUserMapping;
import com.mastergst.usermanagement.runtime.domain.SmtpDetails;
import com.mastergst.usermanagement.runtime.repository.ClientUserMappingRepository;
import com.mastergst.usermanagement.runtime.repository.SmtpDetailsRepository;

@Service
public class SmtpDetailsService {
	
	private static final Logger logger = LogManager.getLogger(SmtpDetailsService.class.getName());
	
	@Autowired
	private ClientUserMappingRepository clientUserMappingRepository;
	@Autowired
	private SmtpDetailsRepository smtpDetailsRepository;
	@Autowired
	private SmtpDetailsDao smtpDetailsDao;
	@Autowired
	private ClientService clientService;
	
	public Map<String, String> saveSmtpDetails(SmtpDetails smtpDetails){
		Map<String, String> results = new HashMap<>();
		String clientId = smtpDetails.getClientId();
		if(smtpDetails.getId() == null && clientId == null){
			List<ClientUserMapping> clientUserMappings = clientUserMappingRepository.findByUserid(smtpDetails.getUserId());
			if(clientUserMappings != null){
				for(ClientUserMapping clientUserMapping : clientUserMappings){
					logger.info(clientUserMapping.getClientid());
					SmtpDetails details = smtpDetailsRepository.findByUserIdAndClientId(smtpDetails.getUserId(), clientUserMapping.getClientid());
					if(details == null){
						details = new SmtpDetails();
						details.setUserId(smtpDetails.getUserId());
						details.setClientId(clientUserMapping.getClientid());
					}
					updateSmtpDetailsWithNew(details, smtpDetails);
					smtpDetailsRepository.save(details);
				}
			}
		}else{
			SmtpDetails details = null;
			if(smtpDetails.getDocId() != null){
				details = smtpDetailsRepository.findOne(smtpDetails.getDocId());
			}
			if(details == null && !StringUtils.isEmpty(clientId)){
				details = smtpDetailsRepository.findByUserIdAndClientId(smtpDetails.getUserId(), clientId);
			}
			if(details == null){
				details = new SmtpDetails();
				details.setUserId(smtpDetails.getUserId());
				details.setClientId(smtpDetails.getClientId());
			}
			smtpDetailsRepository.save(details);
			updateSmtpDetailsWithNew(details, smtpDetails);
		}
		return results;	
	}
	
	private void updateSmtpDetailsWithNew(SmtpDetails details, SmtpDetails smtpDetails){
		
		details.setHost(smtpDetails.getHost());
		details.setPort(smtpDetails.getPort());
		details.setAuth(smtpDetails.getAuth());
		details.setFrom(smtpDetails.getFrom());
		details.setSchedlueExpressionVal(smtpDetails.getSchedlueExpressionVal());
		details.setUsername(smtpDetails.getUsername());
		details.setPassword(smtpDetails.getPassword());
		details.setToAddress(smtpDetails.getToAddress());
		details.setCcAddress(smtpDetails.getCcAddress());
	}
	
	public List<SmtpDetails> getAllSmtpDetails(String userId){
		List<SmtpDetails> smtpDetails = smtpDetailsRepository.findByUserId(userId);
		if(smtpDetails != null && !smtpDetails.isEmpty()){
			List<Client> lClients= clientService.findByUserid(userId);
			if(lClients != null && !lClients.isEmpty()){
				for(SmtpDetails detail : smtpDetails){
					for(Client client : lClients)
					if(client.getId().toString().equals(detail.getClientId())){
						detail.setBussinessName(client.getBusinessname());
						break;
					}
				}
			}
		}
		return smtpDetails;
	}
	
	public SmtpDetails getSmtpDetails(String id){
		return smtpDetailsRepository.findOne(id);
	}
	
	public List<SmtpDetails> getSmtpConfigutedClients(){
		return smtpDetailsDao.getClientsConfiguredSmtpDetails();
	}
	
}
