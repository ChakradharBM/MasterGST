package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.exception.MasterGSTException;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.dao.InvoicesComparisonDao;
import com.mastergst.usermanagement.runtime.domain.GSTR2B_vs_GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BB2B;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BCDN;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocData;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BDocList;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPG;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BIMPGSEZ;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BISD;
import com.mastergst.usermanagement.runtime.domain.gstr2b.GSTR2BItems;
import com.mastergst.usermanagement.runtime.support.Utility;

@Service
public class InvoicesComparisonServiceImpl implements InvoicesComparisonService{
	
	@Autowired
	private InvoicesComparisonDao invoicesComparisonDao;
	private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
	//Page<GSTR2B_vs_GSTR2>
	@Override
	public Map<String, Object> getGSTR2B_vs_GSTR2Report(String clientid, int month, int year, int start, int length, String searchVal, boolean isxls) throws MasterGSTException, ParseException{
		String yearCode = month == 0 ? yearCode = Utility.getYearCode(4, year) : Utility.getYearCode(month, year);
		List<? extends InvoiceParent> gstr2List = invoicesComparisonDao.getClientidAndMonthAndYear(clientid, month, yearCode, searchVal);
		
		List<GSTR2B> gstr2bList = invoicesComparisonDao.getGstr2BClientidAndMonthAndYear(clientid, returnPeriod(month, year));
		
		List<GSTR2B_vs_GSTR2> cmpLst = getComparison(gstr2List, gstr2bList, searchVal);
		
		Pageable pageable = new PageRequest((start/length), length);
		Map<String, Object> map = new HashMap<>();
		
		int pageSize = pageable.getPageSize();
        int currentPage = pageable.getPageNumber();
        int startItem = currentPage * pageSize;
        
        map.put("totalElements", cmpLst.size());
        if (cmpLst.size() < startItem) {
        	cmpLst = Collections.emptyList();
        } else {
        	if(!isxls) {
        		int toIndex = Math.min(startItem + pageSize, cmpLst.size());
        		cmpLst = cmpLst.subList(startItem, toIndex);        		
        	}
        }
        
        Page<GSTR2B_vs_GSTR2> cmpPage = new PageImpl<GSTR2B_vs_GSTR2>(cmpLst, pageable, cmpLst.size());
        map.put("invoices", cmpPage);
        //PageImpl<GSTR2B_vs_GSTR2>(cmpLst, PageRequest.of(currentPage, pageSize), cmpLst.size());
		return map;
	}
		
	public List<GSTR2B_vs_GSTR2> getComparison(List<? extends InvoiceParent> gstr2List, List<GSTR2B> gstr2bList, String searchVal) throws MasterGSTException, ParseException{
		Map<String, GSTR2B_vs_GSTR2> gstr2bMap = new HashMap<>();
		
		Map<String, GSTR2B_vs_GSTR2> gstr2Map = new HashMap<>();
		boolean searchFalg = false;
		if(StringUtils.hasLength(searchVal)){
			searchFalg = true;
		}
		if(NullUtil.isNotEmpty(gstr2bList)) {
			
			for(GSTR2B gstr2b : gstr2bList) {
				
				if(NullUtil.isNotEmpty(gstr2b) && NullUtil.isNotEmpty(gstr2b.getData())) {
					GSTR2BData data = gstr2b.getData();
					if(NullUtil.isNotEmpty(data) && NullUtil.isNotEmpty(data.getDocdata())) {
						GSTR2BDocData summary = data.getDocdata();
												
						List<GSTR2BB2B> b2bLst = summary.getB2b();
						List<GSTR2BB2B> b2baLst = summary.getB2ba();
						List<GSTR2BCDN> cdnrLst = summary.getCdnr();
						List<GSTR2BCDN> cdnraLst = summary.getCdnra();
						List<GSTR2BISD> isdLst = summary.getIsd();
						List<GSTR2BISD> isdaLst = summary.getIsda();
						List<GSTR2BIMPG> impgLst = summary.getImpg();
						List<GSTR2BIMPGSEZ> impgsezLst = summary.getImpgsez();
						if(NullUtil.isNotEmpty(b2bLst) && b2bLst.size() > 0) {
							for(GSTR2BB2B b2b : b2bLst) {
								boolean flag = false;
								GSTR2B_vs_GSTR2 b = new GSTR2B_vs_GSTR2();
								String invno = b2b.getInv().get(0).getInum();
								String ctin = b2b.getCtin();
								b.setInvoiceno(invno);
								b.setGstin(ctin);
								b.setInvoicedate(sdf.parse(b2b.getInv().get(0).getDt()));
								b.setFullname(b2b.getTrdnm());
								b.setInvtype("B2B");
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(b2b.getInv().get(0).getDt().contains(searchVal)) {
										flag = true;
									}
									if(b2b.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								
								Double val = Double.parseDouble(b2b.getInv().get(0).getVal());
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BItems item : b2b.getInv().get(0).getItems()) {
									if(NullUtil.isNotEmpty(item.getTxval())) {
										tax += item.getTxval();
									}
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								b.setGstr2BInvoiceValue(val);
								b.setGstr2BTaxValue(tax);
								b.setGstr2BIGSTValue(igst);
								b.setGstr2BCGSTValue(cgst);
								b.setGstr2BSGSTValue(sgst);
								
								b.setDiffInvoiceValue(val);
								b.setDiffTaxValue(tax);
								b.setDiffCGSTValue(igst);
								b.setDiffCGSTValue(cgst);
								b.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put("B2B"+ctin+invno, b);									
								}else if(!searchFalg) {
									gstr2bMap.put("B2B"+ctin+invno, b);									
								}
							}
						}
						
						if(NullUtil.isNotEmpty(b2baLst) && b2baLst.size() > 0) {
							for(GSTR2BB2B b2b : b2baLst) {
								GSTR2B_vs_GSTR2 b = new GSTR2B_vs_GSTR2();
								String invno = b2b.getInv().get(0).getInum();
								String ctin = b2b.getCtin();
								b.setInvtype("B2BA");
								b.setInvoiceno(invno);
								b.setGstin(ctin);
								b.setInvoicedate(sdf.parse(b2b.getInv().get(0).getDt()));
								b.setFullname(b2b.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(b2b.getInv().get(0).getDt().contains(searchVal)) {
										flag = true;
									}
									if(b2b.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double val = Double.parseDouble(b2b.getInv().get(0).getVal());
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BItems item : b2b.getInv().get(0).getItems()) {
									if(NullUtil.isNotEmpty(item.getTxval())) {
										tax += item.getTxval();
									}
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								b.setGstr2BInvoiceValue(val);
								b.setGstr2BTaxValue(tax);
								b.setGstr2BIGSTValue(igst);
								b.setGstr2BCGSTValue(cgst);
								b.setGstr2BSGSTValue(sgst);
								
								b.setDiffInvoiceValue(val);
								b.setDiffTaxValue(tax);
								b.setDiffCGSTValue(igst);
								b.setDiffCGSTValue(cgst);
								b.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put("B2BA"+ctin+invno, b);
								}else if(!searchFalg) {
									gstr2bMap.put("B2BA"+ctin+invno, b);
								}
							}
						}
						
						if(NullUtil.isNotEmpty(cdnrLst) && cdnrLst.size() > 0) {
							for(GSTR2BCDN cdnr : cdnrLst) {
								GSTR2B_vs_GSTR2 cdn = new GSTR2B_vs_GSTR2();
								String invno = cdnr.getNt().get(0).getNtnum();
								String ctin = cdnr.getCtin();
								String invtype = "";
								
								if("C".equalsIgnoreCase(cdnr.getNt().get(0).getTyp())){
									invtype = MasterGSTConstants.CREDIT_NOTES;
								}else {
									invtype = MasterGSTConstants.DEBIT_NOTES;
								}
								cdn.setInvtype(invtype);
								cdn.setInvoiceno(invno);
								cdn.setGstin(ctin);
								cdn.setInvoicedate(sdf.parse(cdnr.getNt().get(0).getDt()));
								cdn.setFullname(cdnr.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(cdnr.getNt().get(0).getDt().contains(searchVal)) {
										flag = true;
									}
									if(cdnr.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double val = Double.parseDouble(cdnr.getNt().get(0).getVal());
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BItems item : cdnr.getNt().get(0).getItems()) {
									if(NullUtil.isNotEmpty(item.getTxval())) {
										tax += item.getTxval();
									}
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								cdn.setGstr2BInvoiceValue(val);
								cdn.setGstr2BTaxValue(tax);
								cdn.setGstr2BIGSTValue(igst);
								cdn.setGstr2BCGSTValue(cgst);
								cdn.setGstr2BSGSTValue(sgst);
								
								cdn.setDiffInvoiceValue(val);
								cdn.setDiffTaxValue(tax);
								cdn.setDiffCGSTValue(igst);
								cdn.setDiffCGSTValue(cgst);
								cdn.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put(cdn.getInvtype()+ctin+invno, cdn);
								}else if(!searchFalg) {
									gstr2bMap.put(cdn.getInvtype()+ctin+invno, cdn);
								}
								
							}
						}
						
						if(NullUtil.isNotEmpty(cdnraLst) && cdnraLst.size() > 0) {
							for(GSTR2BCDN cdnra : cdnraLst) {
								GSTR2B_vs_GSTR2 cdn = new GSTR2B_vs_GSTR2();
								String invno = cdnra.getNt().get(0).getNtnum();
								String ctin = cdnra.getCtin();
								String invtype = "";
								
								if("C".equalsIgnoreCase(cdnra.getNt().get(0).getTyp())){
									invtype = MasterGSTConstants.CREDIT_NOTES+"A";
								}else {
									invtype = MasterGSTConstants.DEBIT_NOTES+"A";
								}
								cdn.setInvtype(invtype);
								
								cdn.setInvoiceno(invno);
								cdn.setGstin(ctin);
								cdn.setInvoicedate(sdf.parse(cdnra.getNt().get(0).getDt()));
								cdn.setFullname(cdnra.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(cdnra.getNt().get(0).getDt().contains(searchVal)) {
										flag = true;
									}
									if(cdnra.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double val = Double.parseDouble(cdnra.getNt().get(0).getVal());
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BItems item : cdnra.getNt().get(0).getItems()) {
									if(NullUtil.isNotEmpty(item.getTxval())) {
										tax += item.getTxval();
									}
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								cdn.setGstr2BInvoiceValue(val);
								cdn.setGstr2BTaxValue(tax);
								cdn.setGstr2BIGSTValue(igst);
								cdn.setGstr2BCGSTValue(cgst);
								cdn.setGstr2BSGSTValue(sgst);
								
								cdn.setDiffInvoiceValue(val);
								cdn.setDiffTaxValue(tax);
								cdn.setDiffCGSTValue(igst);
								cdn.setDiffCGSTValue(cgst);
								cdn.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put(cdn.getInvtype()+ctin+invno, cdn);
								}else if(!searchFalg) {
									gstr2bMap.put(cdn.getInvtype()+ctin+invno, cdn);
								}
								
							}
						}
						
						if(NullUtil.isNotEmpty(isdLst) && isdLst.size() > 0) {
							for(GSTR2BISD isd : isdLst) {
								GSTR2B_vs_GSTR2 inv = new GSTR2B_vs_GSTR2();
								String invno = isd.getDoclist().get(0).getDocnum();
								String ctin = isd.getCtin();
								inv.setInvtype("ISD");
								inv.setInvoiceno(invno);
								inv.setGstin(ctin);
								inv.setInvoicedate(sdf.parse(isd.getDoclist().get(0).getDocdt()));
								inv.setFullname(isd.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(isd.getDoclist().get(0).getDocdt().contains(searchVal)) {
										flag = true;
									}
									if(isd.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BDocList item : isd.getDoclist()) {
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								Double val = igst+cgst+sgst;
								inv.setGstr2BInvoiceValue(val);
								inv.setGstr2BTaxValue(tax);
								inv.setGstr2BIGSTValue(igst);
								inv.setGstr2BCGSTValue(cgst);
								inv.setGstr2BSGSTValue(sgst);
								
								inv.setDiffInvoiceValue(val);
								inv.setDiffTaxValue(tax);
								inv.setDiffCGSTValue(igst);
								inv.setDiffCGSTValue(cgst);
								inv.setDiffSGSTValue(sgst);
								
								if(searchFalg && flag) {
									gstr2bMap.put("ISD"+ctin+invno, inv);
								}else if(!searchFalg) {
									gstr2bMap.put("ISD"+ctin+invno, inv);
								}
							}
						}
						
						if(NullUtil.isNotEmpty(isdaLst) && isdaLst.size() > 0) {
							for(GSTR2BISD isd : isdaLst) {
								GSTR2B_vs_GSTR2 inv = new GSTR2B_vs_GSTR2();
								String invno = isd.getDoclist().get(0).getDocnum();
								String ctin = isd.getCtin();
								inv.setInvtype("ISDA");
								inv.setInvoiceno(invno);
								inv.setGstin(ctin);
								inv.setInvoicedate(sdf.parse(isd.getDoclist().get(0).getDocdt()));
								inv.setFullname(isd.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(isd.getDoclist().get(0).getDocdt().contains(searchVal)) {
										flag = true;
									}
									if(isd.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								for(GSTR2BDocList item : isd.getDoclist()) {
									if(NullUtil.isNotEmpty(item.getIgst())) {
										igst += item.getIgst();
									}
									if(NullUtil.isNotEmpty(item.getCgst())) {
										cgst += item.getCgst();
									}
									if(NullUtil.isNotEmpty(item.getSgst())) {
										sgst += item.getSgst();
									}
								}
								Double val = igst+cgst+sgst;
								inv.setGstr2BInvoiceValue(val);
								inv.setGstr2BTaxValue(tax);
								inv.setGstr2BIGSTValue(igst);
								inv.setGstr2BCGSTValue(cgst);
								inv.setGstr2BSGSTValue(sgst);
								
								inv.setDiffInvoiceValue(val);
								inv.setDiffTaxValue(tax);
								inv.setDiffCGSTValue(igst);
								inv.setDiffCGSTValue(cgst);
								inv.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put("ISDA"+ctin+invno, inv);
								}else if(!searchFalg) {
									gstr2bMap.put("ISDA"+ctin+invno, inv);
								}
								
							}
						}
						
						if(NullUtil.isNotEmpty(impgLst) && impgLst.size() > 0) {
							for(GSTR2BIMPG impg : impgLst) {
								GSTR2B_vs_GSTR2 inv = new GSTR2B_vs_GSTR2();
								String invno = impg.getBoenum();
								String ctin = "";
								if(impg.getIsamd().equalsIgnoreCase("Y")) {
									inv.setInvtype("IMPGA");
								}else {
									inv.setInvtype("IMPG");
								}
								inv.setInvoiceno(invno);
								inv.setGstin(ctin);
								inv.setInvoicedate(sdf.parse(impg.getBoedt()));
								inv.setFullname("");
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(impg.getBoedt().contains(searchVal)) {
										flag = true;
									}
								}
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								if(NullUtil.isNotEmpty(impg.getIgst())) {
									igst += impg.getIgst();
								}
								if(NullUtil.isNotEmpty(impg.getTxval())) {
									tax +=impg.getTxval();
								}
								
								Double val = igst+cgst+sgst+tax;
								inv.setGstr2BInvoiceValue(val);
								inv.setGstr2BTaxValue(tax);
								inv.setGstr2BIGSTValue(igst);
								inv.setGstr2BCGSTValue(cgst);
								inv.setGstr2BSGSTValue(sgst);
								
								inv.setDiffInvoiceValue(val);
								inv.setDiffTaxValue(tax);
								inv.setDiffCGSTValue(igst);
								inv.setDiffCGSTValue(cgst);
								inv.setDiffSGSTValue(sgst);
								if(searchFalg && flag) {
									gstr2bMap.put(inv.getInvtype()+ctin+invno, inv);
								}else if(!searchFalg) {
									gstr2bMap.put(inv.getInvtype()+ctin+invno, inv);
								}
							}
						}
						
						if(NullUtil.isNotEmpty(impgsezLst) && impgsezLst.size() > 0) {
							for(GSTR2BIMPGSEZ impgsez : impgsezLst) {
								GSTR2B_vs_GSTR2 inv = new GSTR2B_vs_GSTR2();
								String invno = impgsez.getBoe().get(0).getBoenum();
								String ctin = impgsez.getCtin();
								if(impgsez.getBoe().get(0).getIsamd().equalsIgnoreCase("Y")) {
									inv.setInvtype("IMPGSEZA");
								}else {
									inv.setInvtype("IMPGSEZ");
								}
								inv.setInvoiceno(invno);
								inv.setGstin(ctin);
								inv.setInvoicedate(sdf.parse(impgsez.getBoe().get(0).getBoedt()));
								inv.setFullname(impgsez.getTrdnm());
								boolean flag = false;
								if(searchFalg) {
									if(ctin.contains(searchVal)) {
										flag = true;
									}
									if(invno.contains(searchVal)) {
										flag = true;
									}
									if(impgsez.getBoe().get(0).getBoedt().contains(searchVal)) {
										flag = true;
									}
									if(impgsez.getTrdnm().contains(searchVal)) {
										flag = true;
									}
								}
								Double tax = 0d, igst = 0d, cgst = 0d, sgst = 0d; 
								
								if(NullUtil.isNotEmpty(impgsez.getBoe().get(0).getIgst())) {
									igst += impgsez.getBoe().get(0).getIgst();
								}
								if(NullUtil.isNotEmpty(impgsez.getBoe().get(0).getTxval())) {
									tax +=impgsez.getBoe().get(0).getTxval();
								}
								
								Double val = igst+cgst+sgst+tax;
								inv.setGstr2BInvoiceValue(val);
								inv.setGstr2BTaxValue(tax);
								inv.setGstr2BIGSTValue(igst);
								inv.setGstr2BCGSTValue(cgst);
								inv.setGstr2BSGSTValue(sgst);
								
								inv.setDiffInvoiceValue(val);
								inv.setDiffTaxValue(tax);
								inv.setDiffCGSTValue(igst);
								inv.setDiffCGSTValue(cgst);
								inv.setDiffSGSTValue(sgst);
								
								if(searchFalg && flag) {
									gstr2bMap.put(inv.getInvtype()+ctin+invno, inv);
								}else if(!searchFalg) {
									gstr2bMap.put(inv.getInvtype()+ctin+invno, inv);
								}
							}
						}	
					}
				}
			}
		}
		if(NullUtil.isNotEmpty(gstr2List) && gstr2List.size() > 0) {
			for(InvoiceParent invoice : gstr2List) {
				String invtype = invoice.getInvtype();
				String invno = invoice.getInvoiceno();
				
				if(invtype.equalsIgnoreCase(MasterGSTConstants.B2B) || invtype.equalsIgnoreCase(MasterGSTConstants.B2BA)) {
					String ctin = invoice.getB2b().get(0).getCtin();
					
					GSTR2B_vs_GSTR2 b = new GSTR2B_vs_GSTR2();
					b.setInvtype(invtype);
					b.setInvoiceno(invno);
					b.setGstin(ctin);
					b.setInvoicedate(sdf.parse(invoice.getB2b().get(0).getInv().get(0).getIdt()));
					if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
						b.setFullname(invoice.getBilledtoname());						
					}
					boolean flag = false;
					if(searchFalg) {
						if(ctin.contains(searchVal)) {
							flag = true;
						}
						if(invno.contains(searchVal)) {
							flag = true;
						}
						/*if(invoice.getB2b().get(0).getInv().get(0).getIdt().contains(searchVal)) {
							flag = true;
						}*/
						if(invoice.getBilledtoname().contains(searchVal)) {
							flag = true;
						}
					}
					Double val = Double.parseDouble(invoice.getTotalamount_str());
					Double tax = Double.parseDouble(invoice.getTotaltaxableamount_str());
					Double igst = invoice.getTotalIgstAmount(); 
					Double cgst = invoice.getTotalCgstAmount(); 
					Double sgst = invoice.getTotalSgstAmount(); 
					
					b.setGstr2InvoiceValue(val);
					b.setGstr2TaxValue(tax);
					b.setGstr2IGSTValue(igst);
					b.setGstr2CGSTValue(cgst);
					b.setGstr2SGSTValue(sgst);
					
					b.setDiffInvoiceValue(val);
					b.setDiffTaxValue(tax);
					b.setDiffCGSTValue(igst);
					b.setDiffCGSTValue(cgst);
					b.setDiffSGSTValue(sgst);
					if(searchFalg && flag) {
						gstr2Map.put(invtype+ctin+invno, b);
					}else if(!searchFalg) {
						gstr2Map.put(invtype+ctin+invno, b);
					}
				}
				
				if(invtype.equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					String ctin = invoice.getB2b().get(0).getCtin();
					if(NullUtil.isNotEmpty(ctin)){
						if ("D".equalsIgnoreCase(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
							invtype = MasterGSTConstants.DEBIT_NOTES;
						}else {
							invtype = MasterGSTConstants.CREDIT_NOTES;
						}
						
						GSTR2B_vs_GSTR2 cdn = new GSTR2B_vs_GSTR2();
						cdn.setInvtype(invtype);
						cdn.setInvoiceno(invno);
						cdn.setGstin(ctin);
						cdn.setInvoicedate(invoice.getDateofinvoice());
						if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
							cdn.setFullname(invoice.getBilledtoname());						
						}
						boolean flag = false;
						if(searchFalg) {
							if(ctin.contains(searchVal)) {
								flag = true;
							}
							if(invno.contains(searchVal)) {
								flag = true;
							}
							/*if(invoice.getB2b().get(0).getInv().get(0).getIdt().contains(searchVal)) {
								flag = true;
							}*/
							if(invoice.getBilledtoname().contains(searchVal)) {
								flag = true;
							}
						}
						Double val = Double.parseDouble(invoice.getTotalamount_str());
						Double tax = Double.parseDouble(invoice.getTotaltaxableamount_str());
						Double igst = invoice.getTotalIgstAmount(); 
						Double cgst = invoice.getTotalCgstAmount(); 
						Double sgst = invoice.getTotalSgstAmount(); 
						
						cdn.setGstr2InvoiceValue(val);
						cdn.setGstr2TaxValue(tax);
						cdn.setGstr2IGSTValue(igst);
						cdn.setGstr2CGSTValue(cgst);
						cdn.setGstr2SGSTValue(sgst);
						
						cdn.setDiffInvoiceValue(val);
						cdn.setDiffTaxValue(tax);
						cdn.setDiffCGSTValue(igst);
						cdn.setDiffCGSTValue(cgst);
						cdn.setDiffSGSTValue(sgst);
						if(searchFalg && flag) {
							gstr2Map.put(invtype+ctin+invno, cdn);
						}else if(!searchFalg) {
							gstr2Map.put(invtype+ctin+invno, cdn);
						}
					}
				
				}
						
				if(invtype.equalsIgnoreCase(MasterGSTConstants.ISD)) {
					String ctin = invoice.getB2b().get(0).getCtin();
					if(NullUtil.isNotEmpty(ctin)){
						
						GSTR2B_vs_GSTR2 isd = new GSTR2B_vs_GSTR2();
						isd.setInvtype(invtype);
						isd.setInvoiceno(invno);
						isd.setGstin(ctin);
						isd.setInvoicedate(invoice.getDateofinvoice());
						if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
							isd.setFullname(invoice.getBilledtoname());						
						}
						
						boolean flag = false;
						if(searchFalg) {
							if(ctin.contains(searchVal)) {
								flag = true;
							}
							if(invno.contains(searchVal)) {
								flag = true;
							}
							if(sdf.format(invoice.getDateofinvoice()).contains(searchVal)) {
								flag = true;
							}
							if(invoice.getBilledtoname().contains(searchVal)) {
								flag = true;
							}
						}
						
						Double val = Double.parseDouble(invoice.getTotalamount_str());
						Double tax = Double.parseDouble(invoice.getTotaltaxableamount_str());
						Double igst = invoice.getTotalIgstAmount(); 
						Double cgst = invoice.getTotalCgstAmount(); 
						Double sgst = invoice.getTotalSgstAmount(); 
						
						isd.setGstr2InvoiceValue(val);
						isd.setGstr2TaxValue(tax);
						isd.setGstr2IGSTValue(igst);
						isd.setGstr2CGSTValue(cgst);
						isd.setGstr2SGSTValue(sgst);
						
						isd.setDiffInvoiceValue(val);
						isd.setDiffTaxValue(tax);
						isd.setDiffCGSTValue(igst);
						isd.setDiffCGSTValue(cgst);
						isd.setDiffSGSTValue(sgst);
						if(searchFalg && flag) {
							gstr2Map.put(invtype+ctin+invno, isd);
						}else if(!searchFalg) {
							gstr2Map.put(invtype+ctin+invno, isd);
						}
					}
				}
				
				if(invtype.equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
					String ctin = ((PurchaseRegister) invoice).getImpGoods().get(0).getStin();
					if(NullUtil.isNotEmpty(ctin)){
						
						GSTR2B_vs_GSTR2 impg = new GSTR2B_vs_GSTR2();
						impg.setInvtype(invtype);
						impg.setInvoiceno(invno);
						impg.setGstin(ctin);
						impg.setInvoicedate(invoice.getDateofinvoice());
						if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
							impg.setFullname(invoice.getBilledtoname());						
						}
						boolean flag = false;
						if(searchFalg) {
							if(ctin.contains(searchVal)) {
								flag = true;
							}
							if(invno.contains(searchVal)) {
								flag = true;
							}
							if(sdf.format(invoice.getDateofinvoice()).contains(searchVal)) {
								flag = true;
							}
							if(invoice.getBilledtoname().contains(searchVal)) {
								flag = true;
							}
						}
						Double val = Double.parseDouble(invoice.getTotalamount_str());
						Double tax = Double.parseDouble(invoice.getTotaltaxableamount_str());
						Double igst = invoice.getTotalIgstAmount(); 
						Double cgst = invoice.getTotalCgstAmount(); 
						Double sgst = invoice.getTotalSgstAmount(); 
						
						impg.setGstr2InvoiceValue(val);
						impg.setGstr2TaxValue(tax);
						impg.setGstr2IGSTValue(igst);
						impg.setGstr2CGSTValue(cgst);
						impg.setGstr2SGSTValue(sgst);
						
						impg.setDiffInvoiceValue(val);
						impg.setDiffTaxValue(tax);
						impg.setDiffCGSTValue(igst);
						impg.setDiffCGSTValue(cgst);
						impg.setDiffSGSTValue(sgst);
						if(searchFalg && flag) {
							gstr2Map.put(invtype+ctin+invno, impg);
						}else if(!searchFalg) {
							gstr2Map.put(invtype+ctin+invno, impg);
						}
						}
					}
				}
			}
			List<String> same_invoices_numbers = new ArrayList<String>();
			List<String> pr_invoices_numbers = new ArrayList<String>();
			List<String> gstr2b_invoices_numbers = new ArrayList<String>();

			gstr2Map.forEach((kpr, vpr) -> {
				if (gstr2bMap.containsKey(kpr)) {
					same_invoices_numbers.add(kpr);
				} else {
					pr_invoices_numbers.add(kpr);
				}
			});
			gstr2bMap.forEach((kg, vg) -> {
				if (!gstr2Map.containsKey(kg)) {
					gstr2b_invoices_numbers.add(kg);
				}
			});
			
			List<GSTR2B_vs_GSTR2> cmprLst = new ArrayList<GSTR2B_vs_GSTR2>();

			same_invoices_numbers.forEach(num -> {
				GSTR2B_vs_GSTR2 pr_gst2b = new GSTR2B_vs_GSTR2();
				gstr2bMap.forEach((kpr, vpr) -> {
					if (num.equalsIgnoreCase(kpr)) {
						pr_gst2b.setFullname(vpr.getFullname());
						if (isNotEmpty(vpr.getGstin())) {
							pr_gst2b.setGstin(vpr.getGstin());
						}
						
						pr_gst2b.setInvoiceno(vpr.getInvoiceno());
						pr_gst2b.setInvoicedate(vpr.getInvoicedate());
						
						pr_gst2b.setGstr2BInvoiceValue(vpr.getGstr2BInvoiceValue());
						pr_gst2b.setGstr2BTaxValue(vpr.getGstr2BTaxValue());
						pr_gst2b.setGstr2BIGSTValue(vpr.getGstr2BIGSTValue());
						pr_gst2b.setGstr2BCGSTValue(vpr.getGstr2BCGSTValue());
						pr_gst2b.setGstr2BSGSTValue(vpr.getGstr2BSGSTValue());
					}
				});

				gstr2Map.forEach((kg, vg) -> {
					if (num.equalsIgnoreCase(kg)) {
						pr_gst2b.setGstr2InvoiceValue(vg.getGstr2InvoiceValue());
						pr_gst2b.setGstr2TaxValue(vg.getGstr2TaxValue());
						pr_gst2b.setGstr2IGSTValue(vg.getGstr2IGSTValue());
						pr_gst2b.setGstr2CGSTValue(vg.getGstr2CGSTValue());
						pr_gst2b.setGstr2SGSTValue(vg.getGstr2SGSTValue());
					}
				});

				pr_gst2b.setDiffInvoiceValue(pr_gst2b.getGstr2BInvoiceValue() - pr_gst2b.getGstr2InvoiceValue());
				pr_gst2b.setDiffTaxValue(pr_gst2b.getGstr2BTaxValue() - pr_gst2b.getGstr2TaxValue());
				pr_gst2b.setDiffIGSTValue(pr_gst2b.getGstr2BIGSTValue() - pr_gst2b.getGstr2IGSTValue());
				pr_gst2b.setDiffCGSTValue(pr_gst2b.getGstr2BCGSTValue() - pr_gst2b.getGstr2CGSTValue());
				pr_gst2b.setDiffSGSTValue(pr_gst2b.getGstr2BSGSTValue() - pr_gst2b.getGstr2SGSTValue());

				cmprLst.add(pr_gst2b);
			});
			List<GSTR2B_vs_GSTR2> prLst = new ArrayList<GSTR2B_vs_GSTR2>();
			pr_invoices_numbers.forEach(num -> {
				GSTR2B_vs_GSTR2 pr_gst2b = new GSTR2B_vs_GSTR2();
				gstr2Map.forEach((kp, vp) -> {
					if (kp.equalsIgnoreCase(num)) {
						pr_gst2b.setFullname(vp.getFullname());
						if (isNotEmpty(vp.getGstin())) {
							pr_gst2b.setGstin(vp.getGstin());
						}
						pr_gst2b.setInvoiceno(vp.getInvoiceno());
						pr_gst2b.setInvoicedate(vp.getInvoicedate());
						pr_gst2b.setGstr2InvoiceValue(vp.getGstr2InvoiceValue());
						pr_gst2b.setGstr2TaxValue(vp.getGstr2TaxValue());
						pr_gst2b.setGstr2IGSTValue(vp.getGstr2IGSTValue());
						pr_gst2b.setGstr2CGSTValue(vp.getGstr2CGSTValue());
						pr_gst2b.setGstr2SGSTValue(vp.getGstr2SGSTValue());
						pr_gst2b.setGstr2BInvoiceValue(0.0);
						pr_gst2b.setGstr2BTaxValue(0.0);
						pr_gst2b.setGstr2BIGSTValue(0.0);
						pr_gst2b.setGstr2BCGSTValue(0.0);
						pr_gst2b.setGstr2BSGSTValue(0.0);
						pr_gst2b.setDiffInvoiceValue(vp.getGstr2InvoiceValue());
						pr_gst2b.setDiffTaxValue(vp.getGstr2TaxValue());
						pr_gst2b.setDiffIGSTValue(vp.getGstr2IGSTValue());
						pr_gst2b.setDiffCGSTValue(vp.getGstr2CGSTValue());
						pr_gst2b.setDiffSGSTValue(vp.getGstr2SGSTValue());
					}
				});
				prLst.add(pr_gst2b);
			});

			List<GSTR2B_vs_GSTR2> gstr2Lst = new ArrayList<GSTR2B_vs_GSTR2>();
			gstr2b_invoices_numbers.forEach(num -> {
			GSTR2B_vs_GSTR2 pr_gst2b = new GSTR2B_vs_GSTR2();
			gstr2bMap.forEach((kg, vg) -> {
				if (kg.equalsIgnoreCase(num)) {
					pr_gst2b.setFullname(vg.getFullname());
					if (isNotEmpty(vg.getGstin())) {
						pr_gst2b.setGstin(vg.getGstin());
					}
					
					pr_gst2b.setInvoiceno(vg.getInvoiceno());
					pr_gst2b.setInvoicedate(vg.getInvoicedate());
					
					pr_gst2b.setGstr2InvoiceValue(0.0);
					pr_gst2b.setGstr2TaxValue(0.0);
					pr_gst2b.setGstr2IGSTValue(0.0);
					pr_gst2b.setGstr2CGSTValue(0.0);
					pr_gst2b.setGstr2SGSTValue(0.0);
					
					pr_gst2b.setGstr2BInvoiceValue(vg.getGstr2BInvoiceValue());
					pr_gst2b.setGstr2BTaxValue(vg.getGstr2BTaxValue());
					pr_gst2b.setGstr2BIGSTValue(vg.getGstr2BIGSTValue());
					pr_gst2b.setGstr2BCGSTValue(vg.getGstr2BCGSTValue());
					pr_gst2b.setGstr2BSGSTValue(vg.getGstr2BSGSTValue());
					
					pr_gst2b.setDiffInvoiceValue(-vg.getGstr2BInvoiceValue());
					pr_gst2b.setDiffTaxValue(-vg.getGstr2BTaxValue());
					pr_gst2b.setDiffIGSTValue(-vg.getGstr2BIGSTValue());
					pr_gst2b.setDiffCGSTValue(-vg.getGstr2BCGSTValue());
					pr_gst2b.setDiffSGSTValue(-vg.getGstr2BSGSTValue());
				}
			});
			gstr2Lst.add(pr_gst2b);
		});
		cmprLst.addAll(prLst);
		cmprLst.addAll(gstr2Lst);
		
		return cmprLst;
	}
	
	public List<String> returnPeriod(int month, int year){
		List<String> returnPeriod = null;
		if(month == 0) {
			returnPeriod = Arrays.asList("04"+year, "05"+year, "06"+year, "07"+year, "08"+year, "09"+year, "10"+year, 
					"11"+year, "12"+year, "01"+(year+1), "02"+(year+1), "03"+(year+1));
		}else {
			returnPeriod = new ArrayList<String>();
			String strMonth = month < 10 ? "0" + month : month + "";
			returnPeriod.add(strMonth + year);
		}
		
		return returnPeriod;
	}

}
