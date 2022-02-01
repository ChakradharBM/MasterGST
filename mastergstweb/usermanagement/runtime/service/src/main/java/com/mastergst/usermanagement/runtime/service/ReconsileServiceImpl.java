package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.collect.Lists;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.repository.GSTR2Repository;
import com.mastergst.usermanagement.runtime.repository.PurchaseRegisterRepository;

@Service
@Transactional(readOnly = true)
public class ReconsileServiceImpl implements ReconsileService {
	
	@Autowired
	private PurchaseRegisterRepository purchaseRepository;
	@Autowired
	private ClientService clientService;
	@Autowired
	GSTR2Repository gstr2Repository;
	
	@Override
	public String getMatchedAndPresentMonthPurchasesInvoices(String clientid, int month, String yearCode, String fp,int year) throws Exception{
		Pageable pageable = null;
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> invoices = clientService.getPresentMonthfyinvsMatchingIdisNull(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> minvoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MatchingStatusisNotNull(clientid,rtArray);
		
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
				matchingids.add(inv.getId().toString());
				}else {
					matchingids.add(inv.getId().toString());
				}
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}
	
	@Override
	public String getMannualMatchedAndPresentMonthPurchasesInvoices(String clientid, int month, String yearCode, String fp,int year) throws Exception {
Pageable pageable = null;
		
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> minvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();

		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(monthlyinvoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				matchingids.add(inv.getId().toString());
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsMannualInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public String getMatchedAndPresentMonthGSTR2AInvoices(String clientid, int month, String yearCode, String fp,int year) throws Exception {
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		Pageable pageable = null;
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> invoices = clientService.getPresentretperiodgstr2MatchingStatusisNull(clientid,rtArray);
		
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, mstDate,mendDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent gstr2 : monthlyInvoicess) {
				if(isNotEmpty(gstr2.isAmendment()) && gstr2.isAmendment()) {
					matchingstatus.add(gstr2);
				}
			}
			
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
		
	}

	@Override
	public String getMannualMatchedAndPresentMonthGSTR2AInvoices(String clientid, int month,String yearCode, String fp,int year) throws Exception {
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		Pageable pageable = null;
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, mstDate,mendDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			matchingstatus.addAll(monthlyInvoicess);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public String getMatchedAndPresentMonthFYInvoices(String clientid, int year) throws Exception {
		Client client = clientService.findById(clientid);
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);;
		String yrcd = year+"-"+(year+1);
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		List<String> invTypes = Arrays.asList(MasterGSTConstants.B2B, MasterGSTConstants.CREDIT_DEBIT_NOTES, MasterGSTConstants.ISD, 
				MasterGSTConstants.B2BA, MasterGSTConstants.CDNA);
		
		 //2016/11/16 12:08:43
		Page<? extends InvoiceParent> invoices = getPresentMonthfyinvsMatchingIdisNull(clientid, yrcd);
		
		
		Page<? extends InvoiceParent> minvoices = getPresentMonthfyinvsMatchingIdisNotNull(clientid, yrcd);
		Page<? extends InvoiceParent> monthlyinvoices = gstr2Repository.findByClientidAndIsAmendmentAndFpInAndInvtypeIn(client.getId().toString(),true, rtArray, invTypes, pageable);
		
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}else {
					matchingstatus.add(purchaseRepository.findOne(inv.getId().toString()));
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
					matchingids.add(inv.getId().toString());
				}
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public String getgstr2Matchedfyinvs(String clientid, int year) throws Exception {
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);

		Page<? extends InvoiceParent> invoices = clientService.getPresentretperiodgstr2MatchingStatusisNull(clientid,rtArray);
		
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMatchingIdisNotNull(clientid, stDate,endDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					if(isNotEmpty(inv.getMatchingId())) {
						matchingids.add(inv.getMatchingId());
					}
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent gstr2 : monthlyInvoicess) {
				if(isNotEmpty(gstr2.isAmendment()) && gstr2.isAmendment()) {
					matchingstatus.add(gstr2);
				}
			}
			
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public String getgstr2MannualMatchedfyinvs(String clientid, int year) throws Exception {
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate,endDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			matchingstatus.addAll(monthlyInvoicess);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public String getMannualMatchedAndPresentMonthFYInvoices(String clientid, int year) throws Exception {
		Date stDate = null;
		Date endDate = null;
		Calendar cals = Calendar.getInstance();
		cals.set(year, 3, 1, 0, 0, 0);
		stDate = new java.util.Date(cals.getTimeInMillis());
		cals = Calendar.getInstance();
		cals.set(year+1, 3, 0, 23, 59, 59);
		endDate = new java.util.Date(cals.getTimeInMillis());
		
		String rtStart = "04"+year;
		String rtEnd = "03"+(year+1);		
		List<String> rtArray=Arrays.asList(rtStart, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, 
				"10"+year, "11"+year, "12"+year, "01"+(year+1), "02"+(year+1), rtEnd);
		Page<? extends InvoiceParent> minvoices = clientService.getPresentMonthfyinvsMannualMatchingIdisNotNull(clientid, stDate, endDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();

		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(monthlyinvoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				matchingids.add(inv.getId().toString());
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsMannualInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}

	@Override
	public Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNull(String clientid, String yrcd) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		
		return purchaseRepository.findByClientidAndInvtypeInAndYrCdAndMatchingStatusIsNull(clientid, invTypes, yrcd, pageable);
	}

	@Override
	public Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNotNull(String clientid, String yrcd) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		
		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add("Not In GSTR2A");
		matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH);

		return purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndYrCd(clientid,
				invTypes, matchingstatus, yrcd, pageable);
	}
	
	@Override
	public String getMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(String clientid, int month, String yearCode, String fp,int year) throws Exception{
		Pageable pageable = null;
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> invoices = getPresentMonthfyinvsMatchingIdisNullByTransactionDate(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> minvoices = getPresentMonthfyinvsMatchingIdisNotNullByTransactionDate(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MatchingStatusisNotNull(clientid,rtArray);
		
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				if(isNotEmpty(inv.getMatchingStatus()) && !inv.getMatchingStatus().equalsIgnoreCase(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED)) {
				matchingids.add(inv.getId().toString());
				}else {
					matchingids.add(inv.getId().toString());
				}
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}
	
	@Override
	public String getMannualMatchedAndPresentMonthPurchasesInvoicesByTransactionDate(String clientid, int month, String yearCode, String fp,int year) throws Exception {
		Pageable pageable = null;
		
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> minvoices = getPresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(clientid, mstDate, mendDate);
		Page<? extends InvoiceParent> monthlyinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		List<InvoiceParent> mmatchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();

		
		if(isNotEmpty(minvoices)) {
			mmatchingstatusInvoices = (List<InvoiceParent>) minvoices.getContent();
		}
		
		List<String> mmatchingids = Lists.newArrayList();
		if(isNotEmpty(mmatchingstatusInvoices) && mmatchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : mmatchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					mmatchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList();
		if(isNotEmpty(monthlyinvoices)) {
			monthlyInvoicess = (List<InvoiceParent>) monthlyinvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent inv : monthlyInvoicess) {
				matchingids.add(inv.getId().toString());
			}
		}
		matchingids.addAll(mmatchingids);
		Page<? extends InvoiceParent> matchedinvoices = clientService.getgstr2MatchingIdsMannualInvoices(clientid,matchingids);
		List<InvoiceParent> matchingInvoices = Lists.newArrayList();
		if(isNotEmpty(matchedinvoices)) {
			matchingInvoices = (List<InvoiceParent>) matchedinvoices.getContent();
		}
		if(isNotEmpty(matchingInvoices) && matchingInvoices.size() > 0) {
			matchingstatus.addAll(matchingInvoices);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}
	
	@Override
	public String getMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(String clientid, int month, String yearCode, String fp,int year) throws Exception {
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		Pageable pageable = null;
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> invoices = clientService.getPresentretperiodgstr2MatchingStatusisNull(clientid,rtArray);
		
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = getPresentMonthfyinvsMatchingIdisNotNullByTransactionDate(clientid, mstDate,mendDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(invoices)) {
			matchingstatusInvoices = (List<InvoiceParent>) invoices.getContent();
		}
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			for(InvoiceParent gstr2 : monthlyInvoicess) {
				if(isNotEmpty(gstr2.isAmendment()) && gstr2.isAmendment()) {
					matchingstatus.add(gstr2);
				}
			}
			
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
		
	}
	
	@Override
	public String getMannualMatchedAndPresentMonthGSTR2AInvoicesByTransactionDate(String clientid, int month,String yearCode, String fp,int year) throws Exception {
		Date mstDate = null;
		Date mendDate = null;
		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, 0, 23, 59, 59);
		mstDate = new java.util.Date(cal.getTimeInMillis());
		cal = Calendar.getInstance();
		cal.set(year, month, 0, 23, 59, 59);
		mendDate = new java.util.Date(cal.getTimeInMillis());
		
		Pageable pageable = null;
		List<String> rtArray=Arrays.asList(fp);
		Page<? extends InvoiceParent> notinpurchasesinvoices = clientService.getPresentretperiodgstr2MannualMatchingStatus(clientid,rtArray);
		
		Page<? extends InvoiceParent> monthlypurchaseInvoices = getPresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(clientid, mstDate,mendDate);
		
		List<InvoiceParent> monthlypurchasematchingstatusInvoices = Lists.newArrayList();
		if(isNotEmpty(monthlypurchaseInvoices)) {
			monthlypurchasematchingstatusInvoices = (List<InvoiceParent>) monthlypurchaseInvoices.getContent();
		}
		List<String> matchingids = Lists.newArrayList();
		if(isNotEmpty(monthlypurchasematchingstatusInvoices) && monthlypurchasematchingstatusInvoices.size() > 0) {
			for(InvoiceParent inv : monthlypurchasematchingstatusInvoices) {
				if(isNotEmpty(inv.getMatchingId())) {
					matchingids.add(inv.getMatchingId());
				}
			}
		}
		List<InvoiceParent> monthlyInvoicess = Lists.newArrayList(gstr2Repository.findAll(matchingids));
		List<InvoiceParent> matchingstatusInvoices = Lists.newArrayList();
		List<InvoiceParent> notInPurchasesInvoices = Lists.newArrayList();
		List<InvoiceParent> matchingstatus = Lists.newArrayList();
		if(isNotEmpty(notinpurchasesinvoices)) {
			notInPurchasesInvoices = (List<InvoiceParent>) notinpurchasesinvoices.getContent();
		}
		if(isNotEmpty(matchingstatusInvoices) && matchingstatusInvoices.size() > 0) {
			matchingstatus.addAll(matchingstatusInvoices);
		}
		if(isNotEmpty(notInPurchasesInvoices) && notInPurchasesInvoices.size() > 0) {
			matchingstatus.addAll(notInPurchasesInvoices);
		}
		if(isNotEmpty(monthlyInvoicess) && monthlyInvoicess.size() > 0) {
			matchingstatus.addAll(monthlyInvoicess);
		} 
		Page<? extends InvoiceParent> allinvoices = new PageImpl<InvoiceParent>(matchingstatus);
		if(isNotEmpty(allinvoices)) {
			for(InvoiceParent invoiceParent : allinvoices) {
				invoiceParent.setUserid(invoiceParent.getId().toString());
			}
		}
	
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(MapperFeature.USE_ANNOTATIONS);
		ObjectWriter writer=null;
		writer=mapper.writer();
		return writer.writeValueAsString(allinvoices);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNullByTransactionDate(String clientid,Date d1, Date d2) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		
		return purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusIsNullAndBillDateBetween(clientid, invTypes, d1, d2, pageable);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Page<? extends InvoiceParent> getPresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(String clientid, Date d1,Date d2) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);

		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);

		return purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndMannualMatchInvoicesAndBillDateBetween(clientid, invTypes, matchingstatus,"Single", d1, d2, pageable);
	}
	
	@Override
	@Transactional(readOnly=true)
	public Page<? extends InvoiceParent> getPresentMonthfyinvsMatchingIdisNotNullByTransactionDate(String clientid,Date d1, Date d2) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);
		
		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add("Not In GSTR2A");
		//matchingstatus.add(MasterGSTConstants.GST_STATUS_NOTINGSTR2A);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MATCHED_PREVIOUS_MONTH);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_PROBABLEMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_ROUNDOFFMATCHED_PREVIOUS_MONTH);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_GST_NO_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_INVOICE_DATE_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_INVOICE_VALUE_MISMATCHED);
		matchingstatus.add(MasterGSTConstants.GST_STATUS_INVOICENO_MISMATCHED);

		return purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndBillDateBetween(clientid,invTypes, matchingstatus, d1, d2, pageable);
	}
	
	@Override
	public Page<? extends InvoiceParent> getReport_PresentMonthfyinvsMannualMatchingIdisNotNullByTransactionDate(String clientid, Date d1, Date d2) {
		Pageable pageable = null;
		pageable = new PageRequest(0, Integer.MAX_VALUE);
		List<String> invTypes = new ArrayList<String>();
		invTypes.add(MasterGSTConstants.B2B);
		invTypes.add(MasterGSTConstants.CREDIT_DEBIT_NOTES);
		invTypes.add(MasterGSTConstants.B2BA);
		invTypes.add(MasterGSTConstants.CDNA);
		invTypes.add(MasterGSTConstants.ISD);

		List<String> matchingstatus = new ArrayList<String>();
		matchingstatus.add(MasterGSTConstants.GST_STATUS_MANNUAL_MATCHED);

		return purchaseRepository.findByClientidAndInvtypeInAndMatchingStatusInAndBillDateBetween(clientid, invTypes, matchingstatus, d1, d2, pageable);
	}

}
