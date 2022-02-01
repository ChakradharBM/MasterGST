package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.GSTR2;
import static com.mastergst.core.common.MasterGSTConstants.PURCHASE_REGISTER;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.configuration.service.StateRepository;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CompanySuppliers;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTR4;
import com.mastergst.usermanagement.runtime.domain.GSTR5;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.OtherConfigurations;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CompanySuppliersRepository;
import com.mastergst.usermanagement.runtime.repository.OtherConfigurationRepository;
import com.mastergst.usermanagement.runtime.response.InvoiceVO;
import com.mastergst.usermanagement.runtime.support.Utility;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

@Component
public class ClientReportsUtil {

	@Autowired
	private ClientService clientService;
	@Autowired
	CompanyCustomersRepository companyCustomersRepository;
	@Autowired
	CompanySuppliersRepository companySuppliersRepository;
	@Autowired
	StateRepository stateRepository;
	@Autowired
	MongoTemplate mongoTemplate;
	@Autowired
	private ConfigService configService;
	@Autowired
	OtherConfigurationRepository otherConfigurationRepository;
	
	public List<InvoiceVO> invoiceListItems(Page<? extends InvoiceParent> invoices, String returntype){
		if(returntype.equals("Unclaimed")) {
			returntype = MasterGSTConstants.PURCHASE_REGISTER;
		}
		List<StateConfig> states = configService.getStates();
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totDiscount = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		Double tottcsamount = 0d;
		Double tottcsnetamount = 0d;
		
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotDiscount = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;                                    
		Double ctotCessNonAdvol = 0d;
		Double ctottcsamount = 0d;
		Double ctottcsnetamount = 0d;
		if(isNotEmpty(invoices)) {
			List<String> refids = Lists.newArrayList();
			if(isNotEmpty(invoices) && isNotEmpty(invoices.getContent())) {
				invoices.getContent().stream().forEach(inv -> {if(isNotEmpty(inv.getAmendmentRefId())){
					refids.addAll(inv.getAmendmentRefId());
				}});
			}
			
			for (InvoiceParent invoice : invoices) {
				String clientid = "";
				if(isNotEmpty(invoice.getClientid())) {
					clientid = invoice.getClientid();
				}
				Client client = clientService.findById(clientid);
				OtherConfigurations configdetails = otherConfigurationRepository.findByClientid(clientid);
				if (isNotEmpty(invoice.getItems())) {
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
							if(isNotEmpty(invoice.getRevchargeNo())) {
								invo.setRevChargeNo(invoice.getRevchargeNo());
							}
						}
						if(isNotEmpty(invoice.getBillDate())) {
							invo.setTransactionDate(invoice.getBillDate());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setRecharge("No");
							}else {
								invo.setRecharge("Yes");
							}
						}
						if(isNotEmpty(invoice.getAckNo())) {
							invo.setAckno(invoice.getAckNo());
						}
						if(isNotEmpty(invoice.getEinvStatus())) {
							invo.setEinvstatus(invoice.getEinvStatus());
						}
						if(isNotEmpty(invoice.getAckDt())) {
							SimpleDateFormat idt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							SimpleDateFormat idt2 = new SimpleDateFormat("dd-MM-yyyy");
							Date irnDate =null;
							try {
								irnDate = idt1.parse(invoice.getAckDt());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							invo.setAckdt(invoice.getAckDt());
							if(isNotEmpty(irnDate)){
								invo.setIrndt(idt2.format(irnDate));
							}
						}
						if(isNotEmpty(invoice.getGstStatus())) {
							if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
								invo.setGstStatus("Uploaded");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
								invo.setGstStatus("Submitted");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
								invo.setGstStatus("Filed");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								invo.setGstStatus("Cancelled");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
								invo.setGstStatus("Failed");
							}else {
								invo.setGstStatus("Pending");
							}
						}else {
							invo.setGstStatus("Pending");
						}
						if(returntype.equals(MasterGSTConstants.EINVOICE)) {
							String docType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
								if(("INV").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "INVOICE";
								}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "CREDIT NOTE";
								}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "DEBIT NOTE";
								}else {
									docType =  "INVOICE";
								}
							}else {
								docType =  "INVOICE";
							}
							if(isNotEmpty(invoice.getTyp())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getIrnNo())) {
								invo.setIrnNo(invoice.getIrnNo());
							}
							if(isNotEmpty(invoice.getIrnStatus())) {
								invo.setIrnStatus(invoice.getIrnStatus());
							}else {
								invo.setIrnStatus("Not Generated");
							}
							if(isNotEmpty(invoice.getSignedQrCode())) {
								invo.setQrcode(invoice.getSignedQrCode());
							}
						}
						if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
							String supplyType="";
							String subSupplyType="";
							String docType="";
							String vehicleType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSupplyType())) {
								if(("I").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Inward";
								}else if(("O").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Outward";
								}
							}
							String sText[] = {"Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"};
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSubSupplyType())) {
								subSupplyType = sText[Integer.parseInt(invoice.getSubSupplyType().trim())-1];
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getDocType())) {
								if(("INV").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Tax Invoice";
								}else if(("CHN").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Delivery Challan";
								}else if(("BIL").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Supply";
								}else if(("BOE").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Entry";
								}else if(("CNT").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Credit Note";
								}else if(("OTH").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Others";
								}
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getVehicleType())) {
								if(("R").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Regular";
								}else if(("O").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Over Dimensional Cargo";
								}
							}
							if(isNotEmpty(invoice.getEwayBillNumber())) {
								invo.setEwayBillNo(invoice.getEwayBillNumber());
							}
							if(isNotEmpty(invoice.geteBillDate())) {
								invo.setEwayBillDate(invoice.geteBillDate());
							}
							if(isNotEmpty(invoice.getSupplyType())) {
								invo.setSupplyType(supplyType);
							}
							if(isNotEmpty(invoice.getSubSupplyType())) {
								invo.setSubSupplyType(subSupplyType);
							}
							if(isNotEmpty(invoice.getDocType())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getFromGstin())) {
								invo.setFromGstin(invoice.getFromGstin());
							}
							if(isNotEmpty(invoice.getFromTrdName())) {
								invo.setFromTrdName(invoice.getFromTrdName());
							}
							if(isNotEmpty(invoice.getFromAddr1())) {
								invo.setFromAddr1(invoice.getFromAddr1());
							}
							if(isNotEmpty(invoice.getFromAddr2())) {
								invo.setFromAddr2(invoice.getFromAddr2());
							}
							if(isNotEmpty(invoice.getFromPlace())) {
								invo.setFromPlace(invoice.getFromPlace());
							}
							if(isNotEmpty(invoice.getFromPincode())) {
								invo.setFromPincode(invoice.getFromPincode());
							}
							if(isNotEmpty(invoice.getFromStateCode())) {
								invo.setFromStateCode(invoice.getFromStateCode());
							}
							if(isNotEmpty(invoice.getToGstin())) {
								invo.setToGstin(invoice.getToGstin());
							}
							if(isNotEmpty(invoice.getToTrdName())) {
								invo.setToTrdName(invoice.getToTrdName());
							}
							if(isNotEmpty(invoice.getToAddr1())) {
								invo.setToAddr1(invoice.getToAddr1());
							}
							if(isNotEmpty(invoice.getToAddr2())) {
								invo.setToAddr2(invoice.getToAddr2());
							}
							if(isNotEmpty(invoice.getToPincode())) {
								invo.setToPincode(invoice.getToPincode());
							}
							if(isNotEmpty(invoice.getToPlace())) {
								invo.setToPlace(invoice.getToPlace());
							}
							if(isNotEmpty(invoice.getToStateCode())) {
								invo.setToStateCode(invoice.getToStateCode());
							}
							if(isNotEmpty(invoice.getTransporterId())) {
								invo.setTransporterId(invoice.getTransporterId());
							}
							if(isNotEmpty(invoice.getTransporterName())) {
								invo.setTransporterName(invoice.getTransporterName());
							}
							if(isNotEmpty(invoice.getStatus())) {
								invo.setStatus(invoice.getStatus());
							}
							if(isNotEmpty(invoice.getActualDist())) {
								invo.setActualDist(invoice.getActualDist());
							}
							if(isNotEmpty(invoice.getNoValidDays())) {
								invo.setNoValidDays(invoice.getNoValidDays());
							}
							if(isNotEmpty(invoice.getValidUpto())) {
								invo.setValidUpto(invoice.getValidUpto());
							}
							if(isNotEmpty(invoice.getExtendedTimes())) {
								invo.setExtendedTimes(invoice.getExtendedTimes());
							}
							if(isNotEmpty(invoice.getRejectStatus())) {
								invo.setRejectStatus(invoice.getRejectStatus());
							}
							if(isNotEmpty(vehicleType)) {
								invo.setVehicleType(vehicleType);
							}
							if(isNotEmpty(invoice.getTransactionType())) {
								invo.setTransactionType(invoice.getTransactionType());
							}
							if(isNotEmpty(invoice.getOtherValue())) {
								invo.setOtherValue(invoice.getOtherValue());
							}
							if(isNotEmpty(invoice.getCessNonAdvolValue())) {
								invo.setCessNonAdvolValue(invoice.getCessNonAdvolValue());
							}
						}
						if(MasterGSTConstants.GSTR1.equalsIgnoreCase(returntype) || MasterGSTConstants.EINVOICE.equalsIgnoreCase(returntype)) {
							CompanyCustomers customers = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), clientid);
							if(isNotEmpty(customers)) {
								if(isNotEmpty(invoice.getBilledtoname())) {
									invo.setCustomerPAN(customers.getCustomerPanNumber());
									invo.setCustomerTAN(customers.getCustomerTanNumber());
									invo.setCustomerTANPAN(customers.getCustomerTanPanNumber());
									if(isNotEmpty(customers.getCustomerLedgerName())) {
										invo.setCustomerLedgerName(customers.getCustomerLedgerName());
									}
								}
							}
						}else {
							CompanySuppliers suppliers = companySuppliersRepository.findByNameAndClientid(invoice.getBilledtoname(), clientid);
							if(isNotEmpty(suppliers)) {
								if(isNotEmpty(invoice.getBilledtoname())) {
									invo.setCustomerPAN(suppliers.getSupplierPanNumber());
									invo.setCustomerTAN(suppliers.getSupplierTanNumber());
									invo.setCustomerTANPAN(suppliers.getSupplierTanPanNumber());
								}
							}
						}
					if(isNotEmpty(invoice.getInvoiceCustomerId())) {
						invo.setCustomerID(invoice.getInvoiceCustomerId());
					}
						
						if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
							invo.setCompanyGSTIN(client.getGstnnumber());
						}
						if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
							invo.setCompanyStatename(client.getStatename());
						}
						if (isNotEmpty(invoice.getBilledtoname())) {
							invo.setCustomerName(invoice.getBilledtoname());
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("Unclaimed")) {
							if(invoice.getInvtype().equals(MasterGSTConstants.B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CL)
									|| invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.NIL)) {
								invo.setDocType("INV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
								invo.setDocType("ADV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
								invo.setDocType("IMPG");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
								invo.setDocType("IMPS");
							}
						}
						if ((returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((GSTR2)invoice).getImpGoods().get(0).getStin());
						}else if ((returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
						}else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								invo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
								String portCode = "";
								String shipNo = "";/* Date shipDate = null; */
								if(isNotEmpty(invoice.getAddcurrencyCode())) {
									invo.setAdditionalCurrencyCode(invoice.getAddcurrencyCode());
								}
								if(isNotEmpty(invoice.getExchangeRate())) {
									invo.setExchangeRate(invoice.getExchangeRate());
								}
								if(isNotEmpty(invoice.getTotalCurrencyAmount())) {
									invo.setCurrencyTotal(invoice.getTotalCurrencyAmount());
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbpcode())) {
									portCode = invoice.getExp().get(0).getInv().get(0).getSbpcode();
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbnum())) {
									shipNo = invoice.getExp().get(0).getInv().get(0).getSbnum();
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbdt())) {
									invo.setShipBillDate(invoice.getExp().get(0).getInv().get(0).getSbdt());
								}
								invo.setPortCode(portCode);
								invo.setShipBillNo(shipNo);
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)  || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
									invo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
									String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
									for (StateConfig state : states) {
										if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
											invo.setPlaceOfSupply(state.getName());
											break;
										}
									}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
								String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
								String gstinNumber = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim();
								gstinNumber = gstinNumber.substring(0,2);
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
							invo.setBillingAddress(invoice.getB2b().get(0).getInv().get(0).getAddress());
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getConsigneeaddress())) {
							invo.setShipingAddress(invoice.getConsigneeaddress());
						}
						if (isNotEmpty(invoice.getLedgerName())) {
							invo.setLedgerName(invoice.getLedgerName());
						}
						if(isNotEmpty(invoice.getEwayBillNumber())) {
							invo.setEwayBillNumber(invoice.getEwayBillNumber());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setReverseCharge("Yes");								
							}else {
								invo.setReverseCharge("No");
							}
						}else {
							invo.setReverseCharge("No");
						}
						if(isNotEmpty(invoice.getVertical())) {
							invo.setVertical(invoice.getVertical());
						}else {
							invo.setVertical("");
						}
						if(isNotEmpty(invoice.getBranch())) {
							invo.setBranch(invoice.getBranch());
						}else {
							invo.setBranch("");
						}
						if(isNotEmpty(invoice.getDiffPercent())) {
							invo.setDifferentialPercentage(invoice.getDiffPercent());
						}else {
							invo.setDifferentialPercentage("No");
						}
						if(isNotEmpty(invoice.getReferenceNumber())) {
							invo.setReference(invoice.getReferenceNumber());
						}
						if(isNotEmpty(invoice.isTdstcsenable())) {
							if(invoice.isTdstcsenable()) {
								invo.setAddTCS("Yes");
								boolean tcstds = false;
								if(isNotEmpty(configdetails)) {
									if(isNotEmpty(configdetails.isEnableTCS()) && configdetails.isEnableTCS()) {
										tcstds = true;
									}
								}
								Double taxableortotalamt = null;
								if(isNotEmpty(invoice.getSection())) {
									invo.setTcsSection(invoice.getSection());
								}
								if(isNotEmpty(invoice.getTcstdspercentage())) {
									invo.setTcsPercentage(invoice.getTcstdspercentage().toString());
									
									if(tcstds) {
										if(isNotEmpty(item.getTaxablevalue())) {
											taxableortotalamt = item.getTaxablevalue();
										}
									}else {
										if(isNotEmpty(item.getTotal())) {
											taxableortotalamt = item.getTotal();
										}
									}
									
									invo.setTcsAmount((taxableortotalamt*invoice.getTcstdspercentage())/100);
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottcsamount+=invo.getTcsAmount();
									}else {
										tottcsamount+=invo.getTcsAmount();
									}
										if(MasterGSTConstants.GSTR1.equalsIgnoreCase(returntype) || returntype.equals(MasterGSTConstants.EINVOICE)) {
											if(isNotEmpty(item.getTotal())) {
												if(isNotEmpty(invo.getTcsAmount())) {
													invo.setTcsNetAmount(item.getTotal()+invo.getTcsAmount());
													if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
														ctottcsnetamount+=invo.getTcsNetAmount();
													}else {
														tottcsnetamount+=invo.getTcsNetAmount();
													}
												}
											}
										}else {
											if(isNotEmpty(item.getTotal())) {
												if(isNotEmpty(invo.getTcsAmount())) {
													if(isNotEmpty(invoice.getTcsorTdsType()) && invoice.getTcsorTdsType().equalsIgnoreCase("tcs")) {
														invo.setTcsNetAmount(item.getTotal()+invo.getTcsAmount());
													}else {
														invo.setTcsNetAmount(item.getTotal()-invo.getTcsAmount());
													}
													if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
														ctottcsnetamount+=invo.getTcsNetAmount();
													}else {
														tottcsnetamount+=invo.getTcsNetAmount();
													}
												}
											}
										}
								}
							}else {
								invo.setAddTCS("No");
								if(isNotEmpty(item.getTotal())) {
									invo.setTcsNetAmount(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottcsnetamount+=invo.getTcsNetAmount();
									}else {
										tottcsnetamount+=invo.getTcsNetAmount();
									}
								}
							}
						}else {
							invo.setAddTCS("No");
							if(isNotEmpty(item.getTotal())) {
								invo.setTcsNetAmount(item.getTotal());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottcsnetamount+=invo.getTcsNetAmount();
								}else {
									tottcsnetamount+=invo.getTcsNetAmount();
								}
							}
						}
						if(isNotEmpty(invoice.getBankDetails())) {
							if(isNotEmpty(invoice.getBankDetails().getBankname())) {
								invo.setBankName(invoice.getBankDetails().getBankname());
							}
							if(isNotEmpty(invoice.getBankDetails().getAccountnumber())) {
								invo.setAccountNumber(invoice.getBankDetails().getAccountnumber());
							}
							if(isNotEmpty(invoice.getBankDetails().getAccountName())) {
								invo.setAccountName(invoice.getBankDetails().getAccountName());
							}
							if(isNotEmpty(invoice.getBankDetails().getBranchname())) {
								invo.setBranchName(invoice.getBankDetails().getAccountName());
							}
							if(isNotEmpty(invoice.getBankDetails().getIfsccode())) {
								invo.setIfsccode(invoice.getBankDetails().getIfsccode());
							}
						}
						if(isNotEmpty(invoice.getNotes())) {
							invo.setCustomerNotes(invoice.getNotes());
						}
						if(isNotEmpty(invoice.getTerms())) {
							invo.setTermsAndConditions(invoice.getTerms());
						}
						if(isNotEmpty(item.getItemno())) {
							invo.setItemno(item.getItemno());
						}
						if(isNotEmpty(item.getItemNotescomments())) {
							invo.setItemNotescomments(item.getItemNotescomments());
						}
						if(isNotEmpty(item.getDiscount())) {
							invo.setItemDiscount(item.getDiscount());
							if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctotDiscount+=item.getDiscount();
							}else {
								totDiscount+=item.getDiscount();
							}}
						} else{ 
							invo.setItemDiscount(0d);
						}
						if(isNotEmpty(item.getExmepted())) {
							invo.setItemExmepted(item.getExmepted());
							if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctotExempted+=item.getExmepted();
							}else {
								totExempted+=item.getExmepted();
							}}
						}
						if (isNotEmpty(item.getIgstrate())) {
							invo.setIgstRate(item.getIgstrate());
						}
						if(isEmpty(item.getExmepted())) {
							invo.setExemptedVal(0d);
						}
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if(isNotEmpty(item.getElgpercent())) {
							invo.setEligiblePercentage(item.getElgpercent()+"");
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							invo.setItcType(itcType);
						}else {
							//Nill Supplies itc type not applicable, but end-users asks don't excel column empty keep it Ineligible.
							if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)){
									invo.setItcType("Ineligible");
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1))
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(-item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}}
									}
									
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1))
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
										totTax = totTax+item.getIgstamount();
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
										totTax = totTax+item.getIgstamount();
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}}
									}
									invo.setTotaltax(totTax);
								}
							}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(-item.getIgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt-item.getIgstamount();
									}else {
										totisgt = totisgt-item.getIgstamount();
									}}
									totTax = totTax+item.getIgstamount();
									//System.out.println("C --->"+totisgt);
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(-item.getExmepted());
								}
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									invo.setTaxableValue(-item.getAdvadjustedAmount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable-item.getAdvadjustedAmount();
									}else {
										tottaxable = tottaxable-item.getAdvadjustedAmount();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(-item.getCgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt-item.getCgstamount();
									}else {
										totcsgt = totcsgt-item.getCgstamount();
									}}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(-item.getSgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt-item.getSgstamount();
									}else {
										totssgt = totssgt-item.getSgstamount();
									}}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(-item.getCessamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess-item.getCessamount();
									}else {
										totcess = totcess-item.getCessamount();
									}}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(-item.getTotal());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal-item.getTotal();
									}else {
										tottotal = tottotal-item.getTotal();
									}}
								}
								
								invo.setTotaltax(-totTax);
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										totisgt = totisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}}
									totTax = totTax+item.getIgstamount();
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(item.getExmepted());
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}}
								}
								
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR2) invoice).getCdna().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(totTax);
							}
						}else {
							Double totTax = 0d;
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}}
								totTax = totTax+item.getIgstamount();
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}}
							}
							if(isNotEmpty(item.getFreeQty())) {
								invo.setFreeQty(item.getFreeQty());
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}}
								totTax = totTax+item.getCgstamount();
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}}
								totTax = totTax+item.getSgstamount();
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}}
								totTax = totTax+item.getCessamount();
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}}
							}
							invo.setTotaltax(totTax);
						}
						double totalitc = 0d;
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
							totalitc += item.getIgstavltax();
						}
						if (isNotEmpty(item.getCgstrate())) {
							invo.setCgstRate(item.getCgstrate());
						}
						
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
							totalitc += item.getCgstavltax();
						}
						if (isNotEmpty(item.getSgstrate())) {
							invo.setSgstRate(item.getSgstrate());
						}
					
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
							totalitc += item.getSgstavltax();
						}
						if (isNotEmpty(item.getCessrate())) {
							invo.setCessRate(item.getCessrate());
						}
						
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
							totalitc += item.getCessavltax();
						}
						if(isNotEmpty(item.getQuantity())){
							invo.setQuantity(item.getQuantity());
						}
						if(isNotEmpty(item.getUqc())){
							invo.setUqc(item.getUqc());
						}
						if(isNotEmpty(item.getHsn())){
							invo.setHsnCode(item.getHsn());
						}
						if(isNotEmpty(item.getRateperitem())){
							invo.setRateperitem(item.getRateperitem());
						}
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						
						  if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) ||  returntype.equalsIgnoreCase("Purchase Register") ||  returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  invo.setState(gstcode.getName()); 
								  } 
							  } 
						  }else {
						
							if (isNotEmpty(invoice.getStatename())) {
								invo.setState(invoice.getStatename());
							}
						}
						if (isNotEmpty(invoice.getInvoiceno())) {
							invo.setInvoiceNo(invoice.getInvoiceno());
						}
						if(isNotEmpty(invoice.getCustomField1())) {
							invo.setCustomField1(invoice.getCustomField1());
						}
						if(isNotEmpty(invoice.getCustomField2())) {
							invo.setCustomField2(invoice.getCustomField2());
						}
						if(isNotEmpty(invoice.getCustomField3())) {
							invo.setCustomField3(invoice.getCustomField3());
						}
						if(isNotEmpty(invoice.getCustomField4())) {
							invo.setCustomField4(invoice.getCustomField4());
						}
						if(isNotEmpty(invoice.getDateofinvoice())) {
							invo.setInvoiceDate(invoice.getDateofinvoice());
						}
						if(isNotEmpty(invoice.getFp())) {
							invo.setReturnPeriod(invoice.getFp());
						}
						if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR))
								|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)){
							if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if("C".equals(docType)) {
										invo.setType("Credit Note");
									}else if("D".equals(docType)) {
										invo.setType("Debit Note");
									}else {
										invo.setType(invoice.getInvtype());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if("C".equals(docType)) {
										invo.setType("Credit Note(UR)");
									}else if("D".equals(docType)) {
										invo.setType("Debit Note(UR)");
									}else {
										invo.setType(invoice.getInvtype());
									}
								}
								
							}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
									String docType = "";
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
											docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note");
										}else {
											invo.setType(invoice.getInvtype());
										}
										if("C".equals(docType)) {
											invo.setTotalItc(-totalitc);
										}else {
											invo.setTotalItc(totalitc);
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note(UR)");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note(UR)");
										}else {
											invo.setType(invoice.getInvtype());
										}
										if("C".equals(docType)) {
											invo.setTotalItc(-totalitc);
										}else {
											invo.setTotalItc(totalitc);
										}
									}
									
								}
							}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
									String docType = "";
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
											docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note");
										}else {
											invo.setType(invoice.getInvtype());
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note(UR)");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note(UR)");
										}else {
											invo.setType(invoice.getInvtype());
										}
									}
								}
							}
						}else {
							if(isNotEmpty(invoice.getInvtype())) {
								invo.setType(invoice.getInvtype());
							}
							invo.setTotalItc(totalitc);
						}
						
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVOCancelledList.add(invo);
						}else {
							invoiceVOList.add(invo);
						}
					}
				}
				if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR))
						|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)){
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
								|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
								if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}}
								}
							}
						}
					}
				}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
						if(isNotEmpty(invoice.getTotaltax())) {
							if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}
						}
					}else {
						if(isNotEmpty(invoice.getTotaltax())) {
							if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax+invoice.getTotaltax();
							}else {
								tottax = tottax+invoice.getTotaltax();
							}
						}}
					}
				}else {
					if(isNotEmpty(invoice.getTotaltax())) {
						if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax+invoice.getTotaltax();
							}else {
								tottax = tottax+invoice.getTotaltax();
							}
						}
					}
				}
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		totalinvo.setTotalValue(tottotal);
		totalinvo.setTcsAmount(tottcsamount);
		totalinvo.setTcsNetAmount(tottcsnetamount);
		totalinvo.setItemDiscount(totDiscount);
		totalinvo.setExemptedVal(totExempted);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		if(isNotEmpty(totStateCess)) {
			totalinvo.setStateCess(totStateCess);
		}
		if(isNotEmpty(totCessNonAdvol)) {
			totalinvo.setCessnonAdvol(totCessNonAdvol);
		}
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO cancelinvo = new InvoiceVO();
			InvoiceVO cancelinvo1 = new InvoiceVO();
			cancelinvo1.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(cancelinvo);
			invoiceVOList.add(cancelinvo1);
			invoiceVOList.addAll(invoiceVOCancelledList);
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			ctotalinvo.setTcsAmount(ctottcsamount);
			ctotalinvo.setTcsNetAmount(ctottcsnetamount);
			ctotalinvo.setItemDiscount(ctotDiscount);
			ctotalinvo.setExemptedVal(ctotExempted);
			
			invoiceVOList.add(ctotalinvo);
		}
		
		return invoiceVOList;
	}
	
	public List<InvoiceVO> invoiceListItems(List<InvoiceParent> invoices, String returntype){
		if(returntype.equals("Unclaimed")) {
			returntype = MasterGSTConstants.PURCHASE_REGISTER;
		}
		List<StateConfig> states = configService.getStates();
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totDiscount = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		Double tottcsamount = 0d;
		Double tottcsnetamount = 0d;
		
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotDiscount = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;                                    
		Double ctotCessNonAdvol = 0d;
		Double ctottcsamount = 0d;
		Double ctottcsnetamount = 0d;
		if(isNotEmpty(invoices)) {
			for (InvoiceParent invoice : invoices) {
				String clientid = "";
				if(isNotEmpty(invoice.getClientid())) {
					clientid = invoice.getClientid();
				}
				Client client = clientService.findById(clientid);
				OtherConfigurations configdetails = otherConfigurationRepository.findByClientid(clientid);
				if (isNotEmpty(invoice.getItems())) {
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
							if(isNotEmpty(invoice.getRevchargeNo())) {
								invo.setRevChargeNo(invoice.getRevchargeNo());
							}
						}
						if(isNotEmpty(invoice.getBillDate())) {
							invo.setTransactionDate(invoice.getBillDate());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setRecharge("No");
							}else {
								invo.setRecharge("Yes");
							}
						}
						if(isNotEmpty(invoice.getAckNo())) {
							invo.setAckno(invoice.getAckNo());
						}
						if(isNotEmpty(invoice.getEinvStatus())) {
							invo.setEinvstatus(invoice.getEinvStatus());
						}
						if(isNotEmpty(invoice.getAckDt())) {
							SimpleDateFormat idt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							SimpleDateFormat idt2 = new SimpleDateFormat("dd-MM-yyyy");
							Date irnDate =null;
							try {
								irnDate = idt1.parse(invoice.getAckDt());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							invo.setAckdt(invoice.getAckDt());
							if(isNotEmpty(irnDate)){
								invo.setIrndt(idt2.format(irnDate));
							}
						}
						if(isNotEmpty(invoice.getGstStatus())) {
							if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
								invo.setGstStatus("Uploaded");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
								invo.setGstStatus("Submitted");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
								invo.setGstStatus("Filed");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								invo.setGstStatus("Cancelled");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
								invo.setGstStatus("Failed");
							}else {
								invo.setGstStatus("Pending");
							}
						}else {
							invo.setGstStatus("Pending");
						}
						if(returntype.equals(MasterGSTConstants.EINVOICE)) {
							String docType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
								if(("INV").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "INVOICE";
								}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "CREDIT NOTE";
								}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "DEBIT NOTE";
								}else {
									docType =  "INVOICE";
								}
							}else {
								docType =  "INVOICE";
							}
							if(isNotEmpty(invoice.getTyp())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getIrnNo())) {
								invo.setIrnNo(invoice.getIrnNo());
							}
							if(isNotEmpty(invoice.getIrnStatus())) {
								invo.setIrnStatus(invoice.getIrnStatus());
							}else {
								invo.setIrnStatus("Not Generated");
							}
							if(isNotEmpty(invoice.getSignedQrCode())) {
								invo.setQrcode(invoice.getSignedQrCode());
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("Unclaimed")) {
							if(invoice.getInvtype().equals(MasterGSTConstants.B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CL)
									|| invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.NIL)) {
								invo.setDocType("INV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
								invo.setDocType("ADV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
								invo.setDocType("IMPG");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
								invo.setDocType("IMPS");
							}
						}
						if(MasterGSTConstants.GSTR1.equalsIgnoreCase(returntype) || MasterGSTConstants.EINVOICE.equalsIgnoreCase(returntype)) {
							CompanyCustomers customers = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), clientid);
							if(isNotEmpty(customers)) {
								if(isNotEmpty(invoice.getBilledtoname())) {
									invo.setCustomerPAN(customers.getCustomerPanNumber());
									invo.setCustomerTAN(customers.getCustomerTanNumber());
									invo.setCustomerTANPAN(customers.getCustomerTanPanNumber());
									if(isNotEmpty(customers.getCustomerLedgerName())) {
										invo.setCustomerLedgerName(customers.getCustomerLedgerName());
									}
								}
							}
						}else {
							CompanySuppliers suppliers = companySuppliersRepository.findByNameAndClientid(invoice.getBilledtoname(), clientid);
							if(isNotEmpty(suppliers)) {
								if(isNotEmpty(invoice.getBilledtoname())) {
									invo.setCustomerPAN(suppliers.getSupplierPanNumber());
									invo.setCustomerTAN(suppliers.getSupplierTanNumber());
									invo.setCustomerTANPAN(suppliers.getSupplierTanPanNumber());
								}
							}
						}
					if(isNotEmpty(invoice.getInvoiceCustomerId())) {
						invo.setCustomerID(invoice.getInvoiceCustomerId());
					}
						
						if(isNotEmpty(client) && isNotEmpty(client.getGstnnumber())) {
							invo.setCompanyGSTIN(client.getGstnnumber());
						}
						if(isNotEmpty(client) && isNotEmpty(client.getStatename())) {
							invo.setCompanyStatename(client.getStatename());
						}
						if (isNotEmpty(invoice.getBilledtoname())) {
							invo.setCustomerName(invoice.getBilledtoname());
						}
						if ((returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((GSTR2)invoice).getImpGoods().get(0).getStin());
						}else if ((returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
						}else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								invo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
								String portCode = "";
								String shipNo = "";/* Date shipDate = null; */
								if(isNotEmpty(invoice.getAddcurrencyCode())) {
									invo.setAdditionalCurrencyCode(invoice.getAddcurrencyCode());
								}
								if(isNotEmpty(invoice.getExchangeRate())) {
									invo.setExchangeRate(invoice.getExchangeRate());
								}
								if(isNotEmpty(invoice.getTotalCurrencyAmount())) {
									invo.setCurrencyTotal(invoice.getTotalCurrencyAmount());
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbpcode())) {
									portCode = invoice.getExp().get(0).getInv().get(0).getSbpcode();
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbnum())) {
									shipNo = invoice.getExp().get(0).getInv().get(0).getSbnum();
								}
								if(isNotEmpty(invoice.getExp()) && isNotEmpty(invoice.getExp().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv()) && isNotEmpty(invoice.getExp().get(0).getInv().get(0)) && isNotEmpty(invoice.getExp().get(0).getInv().get(0).getSbdt())) {
									invo.setShipBillDate(invoice.getExp().get(0).getInv().get(0).getSbdt());
								}
								invo.setPortCode(portCode);
								invo.setShipBillNo(shipNo);
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)  || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
									invo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
									String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
									for (StateConfig state : states) {
										if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
											invo.setPlaceOfSupply(state.getName());
											break;
										}
									}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
								String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
								String gstinNumber = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim();
								gstinNumber = gstinNumber.substring(0,2);
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
							invo.setBillingAddress(invoice.getB2b().get(0).getInv().get(0).getAddress());
						}
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getConsigneeaddress())) {
							invo.setShipingAddress(invoice.getConsigneeaddress());
						}
						if (isNotEmpty(invoice.getLedgerName())) {
							invo.setLedgerName(invoice.getLedgerName());
						}
						if(isNotEmpty(invoice.getEwayBillNumber())) {
							invo.setEwayBillNumber(invoice.getEwayBillNumber());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setReverseCharge("Yes");								
							}else {
								invo.setReverseCharge("No");
							}
						}else {
							invo.setReverseCharge("No");
						}
						if(isNotEmpty(invoice.getVertical())) {
							invo.setVertical(invoice.getVertical());
						}else {
							invo.setVertical("");
						}
						if(isNotEmpty(invoice.getBranch())) {
							invo.setBranch(invoice.getBranch());
						}else {
							invo.setBranch("");
						}
						if(isNotEmpty(invoice.getDiffPercent())) {
							invo.setDifferentialPercentage(invoice.getDiffPercent());
						}else {
							invo.setDifferentialPercentage("No");
						}
						if(isNotEmpty(invoice.getReferenceNumber())) {
							invo.setReference(invoice.getReferenceNumber());
						}
						if(isNotEmpty(invoice.isTdstcsenable())) {
							if(invoice.isTdstcsenable()) {
								invo.setAddTCS("Yes");
								boolean tcstds = false;
								if(isNotEmpty(configdetails)) {
									if(isNotEmpty(configdetails.isEnableTCS()) && configdetails.isEnableTCS()) {
										tcstds = true;
									}
								}
								Double taxableortotalamt = null;
								if(isNotEmpty(invoice.getSection())) {
									invo.setTcsSection(invoice.getSection());
								}
								if(isNotEmpty(invoice.getTcstdspercentage())) {
									invo.setTcsPercentage(invoice.getTcstdspercentage().toString());
									
									if(tcstds) {
										if(isNotEmpty(item.getTaxablevalue())) {
											taxableortotalamt = item.getTaxablevalue();
										}
									}else {
										if(isNotEmpty(item.getTotal())) {
											taxableortotalamt = item.getTotal();
										}
									}
									
									invo.setTcsAmount((taxableortotalamt*invoice.getTcstdspercentage())/100);
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottcsamount+=invo.getTcsAmount();
									}else {
										tottcsamount+=invo.getTcsAmount();
									}
										if(MasterGSTConstants.GSTR1.equalsIgnoreCase(returntype) || returntype.equals(MasterGSTConstants.EINVOICE)) {
											if(isNotEmpty(item.getTotal())) {
												if(isNotEmpty(invo.getTcsAmount())) {
													invo.setTcsNetAmount(item.getTotal()+invo.getTcsAmount());
													if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
														ctottcsnetamount+=invo.getTcsNetAmount();
													}else {
														tottcsnetamount+=invo.getTcsNetAmount();
													}
												}
											}
										}else {
											if(isNotEmpty(item.getTotal())) {
												if(isNotEmpty(invo.getTcsAmount())) {
													if(isNotEmpty(invoice.getTcsorTdsType()) && invoice.getTcsorTdsType().equalsIgnoreCase("tcs")) {
														invo.setTcsNetAmount(item.getTotal()+invo.getTcsAmount());
													}else {
														invo.setTcsNetAmount(item.getTotal()-invo.getTcsAmount());
													}
													if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
														ctottcsnetamount+=invo.getTcsNetAmount();
													}else {
														tottcsnetamount+=invo.getTcsNetAmount();
													}
												}
											}
										}
								}
							}else {
								invo.setAddTCS("No");
								if(isNotEmpty(item.getTotal())) {
									invo.setTcsNetAmount(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottcsnetamount+=invo.getTcsNetAmount();
									}else {
										tottcsnetamount+=invo.getTcsNetAmount();
									}
								}
							}
						}else {
							invo.setAddTCS("No");
							if(isNotEmpty(item.getTotal())) {
								invo.setTcsNetAmount(item.getTotal());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottcsnetamount+=invo.getTcsNetAmount();
								}else {
									tottcsnetamount+=invo.getTcsNetAmount();
								}
							}
						}
						if(isNotEmpty(invoice.getBankDetails())) {
							if(isNotEmpty(invoice.getBankDetails().getBankname())) {
								invo.setBankName(invoice.getBankDetails().getBankname());
							}
							if(isNotEmpty(invoice.getBankDetails().getAccountnumber())) {
								invo.setAccountNumber(invoice.getBankDetails().getAccountnumber());
							}
							if(isNotEmpty(invoice.getBankDetails().getAccountName())) {
								invo.setAccountName(invoice.getBankDetails().getAccountName());
							}
							if(isNotEmpty(invoice.getBankDetails().getBranchname())) {
								invo.setBranchName(invoice.getBankDetails().getAccountName());
							}
							if(isNotEmpty(invoice.getBankDetails().getIfsccode())) {
								invo.setIfsccode(invoice.getBankDetails().getIfsccode());
							}
						}
						if(isNotEmpty(invoice.getNotes())) {
							invo.setCustomerNotes(invoice.getNotes());
						}
						if(isNotEmpty(invoice.getTerms())) {
							invo.setTermsAndConditions(invoice.getTerms());
						}
						if(isNotEmpty(item.getItemno())) {
							invo.setItemno(item.getItemno());
						}
						if(isNotEmpty(item.getItemNotescomments())) {
							invo.setItemNotescomments(item.getItemNotescomments());
						}
						if(isNotEmpty(item.getDiscount())) {
							invo.setItemDiscount(item.getDiscount());
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctotDiscount+=item.getDiscount();
							}else {
								totDiscount+=item.getDiscount();
							}
						} else{ 
							invo.setItemDiscount(0d);
						}
						if(isNotEmpty(item.getExmepted())) {
							invo.setItemExmepted(item.getExmepted());
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctotExempted+=item.getExmepted();
							}else {
								totExempted+=item.getExmepted();
							}
						}
						if (isNotEmpty(item.getIgstrate())) {
							invo.setIgstRate(item.getIgstrate());
						}
						if(isEmpty(item.getExmepted())) {
							invo.setExemptedVal(0d);
						}
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if(isNotEmpty(item.getElgpercent())) {
							invo.setEligiblePercentage(item.getElgpercent()+"");
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							invo.setItcType(itcType);
						}else {
							//Nill Supplies itc type not applicable, but end-users asks don't excel column empty keep it Ineligible.
							if(returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL)){
									invo.setItcType("Ineligible");
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2) invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1))
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(-item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1))
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}
							}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(-item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt-item.getIgstamount();
									}else {
										totisgt = totisgt-item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
									//System.out.println("C --->"+totisgt);
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(-item.getExmepted());
								}
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									invo.setTaxableValue(-item.getAdvadjustedAmount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable-item.getAdvadjustedAmount();
									}else {
										tottaxable = tottaxable-item.getAdvadjustedAmount();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(-item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt-item.getCgstamount();
									}else {
										totcsgt = totcsgt-item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(-item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt-item.getSgstamount();
									}else {
										totssgt = totssgt-item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(-item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess-item.getCessamount();
									}else {
										totcess = totcess-item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(-item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal-item.getTotal();
									}else {
										tottotal = tottotal-item.getTotal();
									}
								}
								
								invo.setTotaltax(-totTax);
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										totisgt = totisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(item.getExmepted());
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR2) invoice).getCdna().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR2) invoice).getCdna().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(totTax);
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-totTax);
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}else {
									Double totTax = 0d;
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										totTax = totTax+item.getIgstamount();
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
										totTax = totTax+item.getCgstamount();
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
										totTax = totTax+item.getSgstamount();
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
										totTax = totTax+item.getCessamount();
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(totTax);
								}
							}else {
								Double totTax = 0d;
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
									totTax = totTax+item.getIgstamount();
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
									totTax = totTax+item.getCgstamount();
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
									totTax = totTax+item.getSgstamount();
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
									totTax = totTax+item.getCessamount();
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(totTax);
							}
						}else {
							Double totTax = 0d;
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}
								totTax = totTax+item.getIgstamount();
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}
							}
							if(isNotEmpty(item.getFreeQty())) {
								invo.setFreeQty(item.getFreeQty());
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}
								totTax = totTax+item.getCgstamount();
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}
								totTax = totTax+item.getSgstamount();
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}
								totTax = totTax+item.getCessamount();
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}
							}
							invo.setTotaltax(totTax);
						}
						double totalitc = 0d;
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
							totalitc += item.getIgstavltax();
						}
						if (isNotEmpty(item.getCgstrate())) {
							invo.setCgstRate(item.getCgstrate());
						}
						
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
							totalitc += item.getCgstavltax();
						}
						if (isNotEmpty(item.getSgstrate())) {
							invo.setSgstRate(item.getSgstrate());
						}
					
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
							totalitc += item.getSgstavltax();
						}
						if (isNotEmpty(item.getCessrate())) {
							invo.setCessRate(item.getCessrate());
						}
						
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
							totalitc += item.getCessavltax();
						}
						double ineligibleitc = 0d;
						double totalitcavailble = item.getIgstamount()+item.getCgstamount()+item.getSgstamount()+item.getCessamount();
						ineligibleitc = totalitcavailble - totalitc;
						if(isNotEmpty(item.getQuantity())){
							invo.setQuantity(item.getQuantity());
						}
						if(isNotEmpty(item.getUqc())){
							invo.setUqc(item.getUqc());
						}
						if(isNotEmpty(item.getHsn())){
							invo.setHsnCode(item.getHsn());
						}
						if(isNotEmpty(item.getRateperitem())){
							invo.setRateperitem(item.getRateperitem());
						}
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						
						  if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A) ||  returntype.equalsIgnoreCase("Purchase Register") ||  returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							  if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								  String gstno = invoice.getB2b().get(0).getCtin().substring(0, 2); 
								  StateConfig gstcode =  null;
								  if(Integer.parseInt(gstno) < 10) {
									  String tagName = gstno;

									  Query query = new Query();
									  query.limit(37);
									  query.addCriteria(Criteria.where("name").regex(tagName));
									  List<StateConfig> state = mongoTemplate.find(query, StateConfig.class, "state");
									  if(isNotEmpty(state)) {
										gstcode = state.get(0);  
									  }
									  
								  }else {
									  gstcode =  stateRepository.findByTin(Integer.parseInt(gstno));
								  }
								   
								  if(isNotEmpty(gstcode)) {
									  invo.setState(gstcode.getName()); 
								  } 
							  } 
						  }else {
						
							if (isNotEmpty(invoice.getStatename())) {
								invo.setState(invoice.getStatename());
							}
						}
						if (isNotEmpty(invoice.getInvoiceno())) {
							invo.setInvoiceNo(invoice.getInvoiceno());
						}
						if(isNotEmpty(invoice.getCustomField1())) {
							invo.setCustomField1(invoice.getCustomField1());
						}
						if(isNotEmpty(invoice.getCustomField2())) {
							invo.setCustomField2(invoice.getCustomField2());
						}
						if(isNotEmpty(invoice.getCustomField3())) {
							invo.setCustomField3(invoice.getCustomField3());
						}
						if(isNotEmpty(invoice.getCustomField4())) {
							invo.setCustomField4(invoice.getCustomField4());
						}
						if(isNotEmpty(invoice.getDateofinvoice())) {
							invo.setInvoiceDate(invoice.getDateofinvoice());
						}
						if(isNotEmpty(invoice.getFp())) {
							invo.setReturnPeriod(invoice.getFp());
						}
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
							if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if("C".equals(docType)) {
										invo.setType("Credit Note");
									}else if("D".equals(docType)) {
										invo.setType("Debit Note");
									}else {
										invo.setType(invoice.getInvtype());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if("C".equals(docType)) {
										invo.setType("Credit Note(UR)");
									}else if("D".equals(docType)) {
										invo.setType("Debit Note(UR)");
									}else {
										invo.setType(invoice.getInvtype());
									}
								}
								
							}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
									String docType = "";
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
											docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note");
										}else {
											invo.setType(invoice.getInvtype());
										}
										if("C".equals(docType)) {
											invo.setTotalItc(-totalitc);
											invo.setTotalinItc(-ineligibleitc);
										}else {
											invo.setTotalItc(totalitc);
											invo.setTotalinItc(ineligibleitc);
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
										if("C".equals(docType)) {
											invo.setType("Credit Note(UR)");
										}else if("D".equals(docType)) {
											invo.setType("Debit Note(UR)");
										}else {
											invo.setType(invoice.getInvtype());
										}
										if("C".equals(docType)) {
											invo.setTotalItc(-totalitc);
											invo.setTotalinItc(-ineligibleitc);
										}else {
											invo.setTotalItc(totalitc);
											invo.setTotalinItc(ineligibleitc);
										}
									}
									
								}
							}
						}else {
							if(isNotEmpty(invoice.getInvtype())) {
								invo.setType(invoice.getInvtype());
							}
							invo.setTotalItc(totalitc);
							invo.setTotalinItc(ineligibleitc);
						}
						
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVOCancelledList.add(invo);
						}else {
							invoiceVOList.add(invo);
						}
					}
				}
				if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equals(MasterGSTConstants.EINVOICE)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(isNotEmpty(invoice.getTotaltax())) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}
				}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
						if(isNotEmpty(invoice.getTotaltax())) {
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax-invoice.getTotaltax();
							}else {
								tottax = tottax-invoice.getTotaltax();
							}
						}
					}else {
						if(isNotEmpty(invoice.getTotaltax())) {
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax+invoice.getTotaltax();
							}else {
								tottax = tottax+invoice.getTotaltax();
							}
						}
					}
				}else {
					if(isNotEmpty(invoice.getTotaltax())) {
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							ctottax = ctottax+invoice.getTotaltax();
						}else {
							tottax = tottax+invoice.getTotaltax();
						}
					}
				}
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		totalinvo.setTotalValue(tottotal);
		totalinvo.setTcsAmount(tottcsamount);
		totalinvo.setTcsNetAmount(tottcsnetamount);
		totalinvo.setItemDiscount(totDiscount);
		totalinvo.setExemptedVal(totExempted);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		if(isNotEmpty(totStateCess)) {
			totalinvo.setStateCess(totStateCess);
		}
		if(isNotEmpty(totCessNonAdvol)) {
			totalinvo.setCessnonAdvol(totCessNonAdvol);
		}
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO cancelinvo = new InvoiceVO();
			InvoiceVO cancelinvo1 = new InvoiceVO();
			cancelinvo1.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(cancelinvo);
			invoiceVOList.add(cancelinvo1);
			invoiceVOList.addAll(invoiceVOCancelledList);
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			ctotalinvo.setTcsAmount(ctottcsamount);
			ctotalinvo.setTcsNetAmount(ctottcsnetamount);
			ctotalinvo.setItemDiscount(ctotDiscount);
			ctotalinvo.setExemptedVal(ctotExempted);
			
			invoiceVOList.add(ctotalinvo);
		}
		
		return invoiceVOList;
	}
	
	public List<InvoiceVO> getInvoice_Wise_List(List<InvoiceParent> invoices, String returntype){
		if(returntype.equals("Unclaimed")) {
			returntype = MasterGSTConstants.PURCHASE_REGISTER;
		}
		List<StateConfig> states = configService.getStates();
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;
		Double ctotCessNonAdvol = 0d;
		if(isNotEmpty(invoices)) {
			for (InvoiceParent invoice : invoices) {
				String clientid = invoice.getClientid();
				Client client = clientService.findById(clientid);
				
				List<String> refids = Lists.newArrayList();
				invoices.stream().forEach(inv -> {if(isNotEmpty(inv.getAmendmentRefId())){
					refids.addAll(inv.getAmendmentRefId());
				}});
				
				if (isNotEmpty(invoice.getItems())) {
					
					InvoiceVO invoiceVo = new InvoiceVO();
					if(isNotEmpty(invoice.getCustomField1())) {
						invoiceVo.setCustomField1(invoice.getCustomField1());
					}
					if(isNotEmpty(invoice.getCustomField2())) {
						invoiceVo.setCustomField2(invoice.getCustomField2());
					}
					if(isNotEmpty(invoice.getCustomField3())) {
						invoiceVo.setCustomField3(invoice.getCustomField3());
					}
					if(isNotEmpty(invoice.getCustomField4())) {
						invoiceVo.setCustomField4(invoice.getCustomField4());
					}
					if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
						if(isNotEmpty(invoice.getRevchargeNo())) {
							invoiceVo.setRevChargeNo(invoice.getRevchargeNo());
						}
					}
					if(isNotEmpty(invoice.getBillDate())) {
						invoiceVo.setTransactionDate(invoice.getBillDate());
					}
					if(isNotEmpty(invoice.getRevchargetype())) {
						if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
							invoiceVo.setRecharge("No");
						}else {
							invoiceVo.setRecharge("Yes");
						}
					}
					if(isNotEmpty(invoice.getGstStatus())) {
						if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
							invoiceVo.setGstStatus("Uploaded");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
							invoiceVo.setGstStatus("Submitted");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
							invoiceVo.setGstStatus("Filed");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVo.setGstStatus("Cancelled");
						}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
							invoiceVo.setGstStatus("Failed");
						}else {
							invoiceVo.setGstStatus("Pending");
						}
					}else {
						invoiceVo.setGstStatus("Pending");
					}
					if(isNotEmpty(client.getGstnnumber())) {
						invoiceVo.setCompanyGSTIN(client.getGstnnumber());
					}
					if(isNotEmpty(client.getStatename())) {
						invoiceVo.setCompanyStatename(client.getStatename());
					}
					if (isNotEmpty(invoice.getBilledtoname())) {
						invoiceVo.setCustomerName(invoice.getBilledtoname());
					}
					if(isNotEmpty(invoice.getAckNo())) {
						invoiceVo.setAckno(invoice.getAckNo());
					}
					if(isNotEmpty(invoice.getEinvStatus())) {
						invoiceVo.setEinvstatus(invoice.getEinvStatus());
					}
					if(isNotEmpty(invoice.getBranch())) {
						invoiceVo.setBranch(invoice.getBranch());
					}else {
						invoiceVo.setBranch("");
					}
					
					if(isNotEmpty(invoice.getAckDt())) {
						SimpleDateFormat idt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
						SimpleDateFormat idt2 = new SimpleDateFormat("dd-MM-yyyy");
						Date irnDate =null;
						try {
							irnDate = idt1.parse(invoice.getAckDt());
						} catch (ParseException e) {
							e.printStackTrace();
						}
						invoiceVo.setAckdt(invoice.getAckDt());
						if(isNotEmpty(irnDate)){
							invoiceVo.setIrndt(idt2.format(irnDate));
						}
					}
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("Unclaimed")) {
						if(invoice.getInvtype().equals(MasterGSTConstants.B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CL)
								|| invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.NIL)) {
							invoiceVo.setDocType("INV");
						}else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
							invoiceVo.setDocType("ADV");
						}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
							invoiceVo.setDocType("IMPG");
						}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
							invoiceVo.setDocType("IMPS");
						}
					}
					if ((returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							invoiceVo.setCustomerGSTIN(((GSTR2)invoice).getImpGoods().get(0).getStin());
						}
					}else if ((returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS)) {
						if(isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
							invoiceVo.setCustomerGSTIN(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
						}
					}else {
						if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							invoiceVo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
						}
					}
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
								String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invoiceVo.setPlaceOfSupply(state.getName());
										break;
									}
								}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
							String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
							String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
							String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						
						if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							String gstinNumber = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim();
							gstinNumber = gstinNumber.substring(0,2);
							for (StateConfig state : states) {
								if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
									invoiceVo.setPlaceOfSupply(state.getName());
									break;
								}
							}
						}
						
					}
					if (isNotEmpty(invoice.getInvoiceno())) {
						invoiceVo.setInvoiceNo(invoice.getInvoiceno());
					}
					if (isNotEmpty(invoice.getStatename())) {
						invoiceVo.setState(invoice.getStatename());
					}
					if(returntype.equals(MasterGSTConstants.EINVOICE)) {
						String docType="INVOICE";
						if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
							if(("INV").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "INVOICE";
							}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "CREDIT NOTE";
							}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
								docType =  "DEBIT NOTE";
							}else {
								docType =  "INVOICE";
							}
						}
						if(isNotEmpty(invoice.getTyp())) {
							invoiceVo.setDocType(docType);
						}else {
							invoiceVo.setDocType(docType);
						}
						if(isNotEmpty(invoice.getIrnNo())) {
							invoiceVo.setIrnNo(invoice.getIrnNo());
						}
						if(isNotEmpty(invoice.getIrnStatus())) {
							invoiceVo.setIrnStatus(invoice.getIrnStatus());
						}else {
							invoiceVo.setIrnStatus("Not Generated");
						}
						if(isNotEmpty(invoice.getSignedQrCode())) {
							invoiceVo.setQrcode(invoice.getSignedQrCode());
						}
					}
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
							if(isNotEmpty(invoice.getCdn().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
							if(isNotEmpty(invoice.getB2b().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
							if(isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
							if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
								invoiceVo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
							}
							if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
								invoiceVo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
							}
						}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
							if(isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
								if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									invoiceVo.setCounterFilingStatus("Filed");
								}else {
									invoiceVo.setCounterFilingStatus("Pending");
								}
							}else {
								invoiceVo.setCounterFilingStatus("Pending");
							}
							if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
								invoiceVo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
							}
							if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
								invoiceVo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
							}
						}
					}
					Double quantity=0d;
					Double freeqty=0d;
					Double assAmt=0d;
					Double stateCess=0d;
					Double cessnonAdvol=0d;
					Double taxableValue=0d;
					Double igstRate=0d;
					Double igstAmount=0d;
					Double igstTax=0d;
					Double cgstRate=0d;
					Double cgstAmount=0d;
					Double cgstTax=0d;
					Double sgstRate=0d;
					Double sgstAmount=0d;
					Double sgstTax=0d;
					Double cessRate=0d;
					Double cessAmount=0d;
					Double cessTax=0d;
					Double rateperitem=0d;
					Double totaltax=0d;
					Double totalValue=0d;
					Double totalExemptedValue=0d;
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							
							invo.setItcType(itcType);
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR))
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invoiceVo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue = totalExemptedValue-((item.getQuantity())*(item.getExmepted()));

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted = ctotExempted-((item.getQuantity())*(item.getExmepted()));
										}else {
											totExempted = totExempted-((item.getQuantity())*(item.getExmepted()));
										}}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invoiceVo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue += (item.getQuantity())*(item.getExmepted());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted += (item.getQuantity())*(item.getExmepted());
										}else {
											totExempted += (item.getQuantity())*(item.getExmepted());
										}}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}}
									}
									if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
										totalExemptedValue += (item.getQuantity())*(item.getExmepted());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotExempted += (item.getQuantity())*(item.getExmepted());
										}else {
											totExempted += (item.getQuantity())*(item.getExmepted());
										}}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(-item.getIgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt-item.getIgstamount();
									}else {
										totisgt = totisgt-item.getIgstamount();
									}}
									//System.out.println("C --->"+totisgt);
								}
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									invo.setTaxableValue(-item.getAdvadjustedAmount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable-item.getAdvadjustedAmount();
									}else {
										tottaxable = tottaxable-item.getAdvadjustedAmount();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(-item.getCgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt-item.getCgstamount();
									}else {
										totcsgt = totcsgt-item.getCgstamount();
									}}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(-item.getSgstamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt-item.getSgstamount();
									}else {
										totssgt = totssgt-item.getSgstamount();
									}}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(-item.getCessamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess-item.getCessamount();
									}else {
										totcess = totcess-item.getCessamount();
									}}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(-item.getTotal());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal-item.getTotal();
									}else {
										tottotal = tottotal-item.getTotal();
									}}
								}
								if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
									totalExemptedValue = totalExemptedValue-((item.getQuantity())*(item.getExmepted()));
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotExempted = ctotExempted-((item.getQuantity())*(item.getExmepted()));
									}else {
										totExempted = totExempted-((item.getQuantity())*(item.getExmepted()));
									}}
								}
								invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}}
								}
								if(isNotEmpty(item.getExmepted()) && isNotEmpty(item.getQuantity())) {
									totalExemptedValue += (item.getQuantity())*(item.getExmepted());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotExempted += (item.getQuantity())*(item.getExmepted());
									}else {
										totExempted += (item.getQuantity())*(item.getExmepted());
									}}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invoiceVo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invoiceVo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}					
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invoiceVo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invoiceVo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invoiceVo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invoiceVo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									invoiceVo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invoiceVo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}}
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}}
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}}
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}}
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}}
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}}
							}
							invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
						}
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
						}
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
						}
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
						}
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
						}
						
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						
						if(isNotEmpty(invo.getTotaltax())) {
							totaltax+=invo.getTotaltax();
						}
						if(isNotEmpty(item.getTotal())){
							totalValue+=invo.getTotalValue();
						}
						
						if(isNotEmpty(invo.getQuantity())) {
							quantity+=invo.getQuantity();
						}
						if(isNotEmpty(invo.getTaxableValue())) {
							taxableValue+=invo.getTaxableValue();
						}
						if(isNotEmpty(invo.getIgstAmount())) {
							igstAmount+=invo.getIgstAmount();
						}
						
						if(isNotEmpty(invo.getIgstTax())) {
							igstTax+=invo.getIgstTax();
						}
						if(isNotEmpty(invo.getCgstAmount())) {
							cgstAmount+=invo.getCgstAmount();
						}
						
						if(isNotEmpty(invo.getCgstTax())) {
							cgstTax+=invo.getCgstTax();
						
						}
						if(isNotEmpty(invo.getSgstAmount())) {
							sgstAmount+=invo.getSgstAmount();
						}
						
						if(isNotEmpty(invo.getSgstTax())) {
							sgstTax+=invo.getSgstTax();
						}
					
						if(isNotEmpty(invo.getCessAmount())) {
							cessAmount+=invo.getCessAmount();
						}
						if(isNotEmpty(invo.getCessTax())) {
							cessTax+=invo.getCessTax();
						}
					}
					invoiceVo.setExemptedVal(totalExemptedValue);
					invoiceVo.setInvoiceDate(invoice.getDateofinvoice());
					invoiceVo.setReturnPeriod(invoice.getFp());
					if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR))
							|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)){
						String docType = "";
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR))
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
							}
						}
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) 
								|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
							if("C".equals(docType)) {
								invoiceVo.setType("Credit Note");
								invoiceVo.setTotalItc(-invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(-ineligibleitc);
								}
							}else if("D".equals(docType)) {
								invoiceVo.setType("Debit Note");
								invoiceVo.setTotalItc(invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(ineligibleitc);
								}
							}else {
								invoiceVo.setType(invoice.getInvtype());
								invoiceVo.setTotalItc(invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(ineligibleitc);
								}
							}
						}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
								|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
							if("C".equals(docType)) {
								invoiceVo.setType("Credit Note(UR)");
								invoiceVo.setTotalItc(-invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(-ineligibleitc);
								}
							}else if("D".equals(docType)) {
								invoiceVo.setType("Debit Note(UR)");
								invoiceVo.setTotalItc(invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(ineligibleitc);
								}
							}else {
								invoiceVo.setType(invoice.getInvtype());
								invoiceVo.setTotalItc(invoice.getTotalitc());
								if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
									Double ineligibleitc = 0d;
									ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
									invoiceVo.setTotalinItc(ineligibleitc);
								}
							}
						}
					}else {
						invoiceVo.setType(invoice.getInvtype());
						invoiceVo.setTotalItc(invoice.getTotalitc());
						if(isNotEmpty(invoice.getTotaltax()) && isNotEmpty(invoice.getTotalitc())) {
							Double ineligibleitc = 0d;
							ineligibleitc = invoice.getTotaltax() - invoice.getTotalitc();
							invoiceVo.setTotalinItc(ineligibleitc);
						}
					}
					
					if(isNotEmpty(totAss)) {
						invoiceVo.setAssAmt(totAss);
					}
					invoiceVo.setTaxableValue(taxableValue);
					invoiceVo.setIgstAmount(igstAmount);
					
					invoiceVo.setIgstTax(igstTax);
					invoiceVo.setCgstAmount(cgstAmount);
					
					invoiceVo.setCgstTax(cgstTax);
					invoiceVo.setSgstAmount(sgstAmount);
					
					invoiceVo.setSgstTax(sgstTax);
					
					invoiceVo.setCessAmount(cessAmount);
					
					invoiceVo.setCessTax(cessTax);
					
					invoiceVo.setTotaltax(totaltax);
					invoiceVo.setTotalValue(totalValue);
					if(isNotEmpty(invoice.getDateofitcClaimed())) {
						invoiceVo.setDateOfItcClaimed(invoice.getDateofitcClaimed());
					}
					if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
						invoiceVOCancelledList.add(invoiceVo);
					}else {
						invoiceVOList.add(invoiceVo);
					}
				}
				if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR))
						|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)){
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
						if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
								|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA))){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}
							}else {
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
						if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
									docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
									docType = invoice.getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
						if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
							String docType = "";
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
								}
							}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
									docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
								}
							}
							if("C".equals(docType)) {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax-invoice.getTotaltax();
								}else {
									tottax = tottax-invoice.getTotaltax();
								}
							}else {
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottax = ctottax+invoice.getTotaltax();
								}else {
									tottax = tottax+invoice.getTotaltax();
								}
							}
						}
					}
				}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID)  || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA)){
					if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
						if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax-invoice.getTotaltax();
							}else {
								tottax = tottax-invoice.getTotaltax();
							}
						}
					}else {
						if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							ctottax = ctottax+invoice.getTotaltax();
						}else {
							tottax = tottax+invoice.getTotaltax();
						}
						}
					}
				}else {
					if(!refids.contains(invoice.getId().toString())){
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							ctottax = ctottax+invoice.getTotaltax();
						}else {
							tottax = tottax+invoice.getTotaltax();
						}
					}
				}
				
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		totalinvo.setTotalValue(tottotal);
		totalinvo.setExemptedVal(totExempted);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO cancelinvo = new InvoiceVO();
			InvoiceVO cancel1invo = new InvoiceVO();
			cancel1invo.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(cancelinvo);
			invoiceVOList.add(cancel1invo);
			invoiceVOList.addAll(invoiceVOCancelledList);
			
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			ctotalinvo.setExemptedVal(ctotExempted);
			invoiceVOList.add(ctotalinvo);
		}
		
		return invoiceVOList;
	}
	
	
	public List<InvoiceVO> invoiceListItemwise(List<InvoiceParent> invoices, String returntype){
		if(returntype.equals("Unclaimed")) {
			returntype = MasterGSTConstants.PURCHASE_REGISTER;
		}
		List<StateConfig> states = configService.getStates();
		List<InvoiceVO> invoiceVOList = Lists.newArrayList();
		List<InvoiceVO> invoiceVOCancelledList = Lists.newArrayList();
		Double totisgt = 0d;
		Double totcsgt = 0d;
		Double totssgt = 0d;
		Double totcess = 0d;
		Double tottaxable = 0d;
		Double tottax = 0d;
		Double tottotal = 0d;
		Double totExempted = 0d;
		Double totAss = 0d;
		Double totStateCess = 0d;
		Double totCessNonAdvol = 0d;
		
		Double ctotisgt = 0d;
		Double ctotcsgt = 0d;
		Double ctotssgt = 0d;
		Double ctotcess = 0d;
		Double ctottaxable = 0d;
		Double ctottax = 0d;
		Double ctottotal = 0d;
		Double ctotExempted = 0d;
		Double ctotAss = 0d;
		Double ctotStateCess = 0d;
		Double ctotCessNonAdvol = 0d;
		if(isNotEmpty(invoices)) {
			List<String> refids = Lists.newArrayList();
			invoices.stream().forEach(inv -> {if(isNotEmpty(inv.getAmendmentRefId())){
				refids.addAll(inv.getAmendmentRefId());
			}});
			
			
			for (InvoiceParent invoice : invoices) {
				String clientid = invoice.getClientid();
				Client client = clientService.findById(clientid);
				if (isNotEmpty(invoice.getItems())) {
					for (Item item : invoice.getItems()) {
						InvoiceVO invo = new InvoiceVO();
						if(isNotEmpty(invoice.getCustomField1())) {
							invo.setCustomField1(invoice.getCustomField1());
						}
						if(isNotEmpty(invoice.getCustomField2())) {
							invo.setCustomField2(invoice.getCustomField2());
						}
						if(isNotEmpty(invoice.getCustomField3())) {
							invo.setCustomField3(invoice.getCustomField3());
						}
						if(isNotEmpty(invoice.getCustomField4())) {
							invo.setCustomField4(invoice.getCustomField4());
						}
						if("Reverse".equalsIgnoreCase(invoice.getRevchargetype())) {
							if(isNotEmpty(invoice.getRevchargeNo())) {
								invo.setRevChargeNo(invoice.getRevchargeNo());
							}
						}
						if(isNotEmpty(invoice.getBillDate())) {
							invo.setTransactionDate(invoice.getBillDate());
						}
						if(isNotEmpty(invoice.getRevchargetype())) {
							if("Regular".equalsIgnoreCase(invoice.getRevchargetype()) || "N".equalsIgnoreCase(invoice.getRevchargetype())) {
								invo.setRecharge("No");
							}else {
								invo.setRecharge("Yes");
							}
						}
						if(isNotEmpty(invoice.getAckNo())) {
							invo.setAckno(invoice.getAckNo());
						}
						if(isNotEmpty(invoice.getEinvStatus())) {
							invo.setEinvstatus(invoice.getEinvStatus());
						}
						if(isNotEmpty(invoice.getAckDt())) {
							SimpleDateFormat idt1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
							SimpleDateFormat idt2 = new SimpleDateFormat("dd-MM-yyyy");
							Date irnDate =null;
							try {
								irnDate = idt1.parse(invoice.getAckDt());
							} catch (ParseException e) {
								e.printStackTrace();
							}
							invo.setAckdt(invoice.getAckDt());
							if(isNotEmpty(irnDate)){
								invo.setIrndt(idt2.format(irnDate));
							}
						}
						if(isNotEmpty(invoice.getGstStatus())) {
							if(invoice.getGstStatus().equalsIgnoreCase("Uploaded") || invoice.getGstStatus().equalsIgnoreCase("SUCCESS")) {
								invo.setGstStatus("Uploaded");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Submitted")) {
								invo.setGstStatus("Submitted");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Filed")) {
								invo.setGstStatus("Filed");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								invo.setGstStatus("Cancelled");
							}else if(invoice.getGstStatus().equalsIgnoreCase("Failed")) {
								invo.setGstStatus("Failed");
							}else {
								invo.setGstStatus("Pending");
							}
						}else {
							invo.setGstStatus("Pending");
						}
						if(returntype.equals(MasterGSTConstants.EINVOICE)) {
							String docType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getTyp())) {
								if(("INV").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "INVOICE";
								}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "CREDIT NOTE";
								}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
									docType =  "DEBIT NOTE";
								}else {
									docType =  "INVOICE";
								}
							}
							if(isNotEmpty(invoice.getTyp())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getIrnNo())) {
								invo.setIrnNo(invoice.getIrnNo());
							}
							if(isNotEmpty(invoice.getIrnStatus())) {
								invo.setIrnStatus(invoice.getIrnStatus());
							}else {
								invo.setIrnStatus("Not Generated");
							}
						}
						if(returntype.equals(MasterGSTConstants.EWAYBILL)) {
							String supplyType="";
							String subSupplyType="";
							String docType="";
							String vehicleType="";
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSupplyType())) {
								if(("I").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Inward";
								}else if(("O").equalsIgnoreCase(invoice.getSupplyType())) {
									supplyType =  "Outward";
								}
							}
							String sText[] = {"Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"};
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getSubSupplyType())) {
								subSupplyType = sText[Integer.parseInt(invoice.getSubSupplyType().trim())-1];
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getDocType())) {
								if(("INV").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Tax Invoice";
								}else if(("CHN").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Delivery Challan";
								}else if(("BIL").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Supply";
								}else if(("BOE").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Bill of Entry";
								}else if(("CNT").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Credit Note";
								}else if(("OTH").equalsIgnoreCase(invoice.getDocType())) {
									docType =  "Others";
								}
							}
							if(isNotEmpty(invoice) && isNotEmpty(invoice.getVehicleType())) {
								if(("R").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Regular";
								}else if(("O").equalsIgnoreCase(invoice.getVehicleType())) {
									vehicleType =  "Over Dimensional Cargo";
								}
							}
							if(isNotEmpty(invoice.getEwayBillNumber())) {
								invo.setEwayBillNo(invoice.getEwayBillNumber());
							}
							if(isNotEmpty(invoice.geteBillDate())) {
								invo.setEwayBillDate(invoice.geteBillDate());
							}
							if(isNotEmpty(invoice.getSupplyType())) {
								invo.setSupplyType(supplyType);
							}
							if(isNotEmpty(invoice.getSubSupplyType())) {
								invo.setSubSupplyType(subSupplyType);
							}
							if(isNotEmpty(invoice.getDocType())) {
								invo.setDocType(docType);
							}
							if(isNotEmpty(invoice.getFromGstin())) {
								invo.setFromGstin(invoice.getFromGstin());
							}
							if(isNotEmpty(invoice.getFromTrdName())) {
								invo.setFromTrdName(invoice.getFromTrdName());
							}
							if(isNotEmpty(invoice.getFromAddr1())) {
								invo.setFromAddr1(invoice.getFromAddr1());
							}
							if(isNotEmpty(invoice.getFromAddr2())) {
								invo.setFromAddr2(invoice.getFromAddr2());
							}
							if(isNotEmpty(invoice.getFromPlace())) {
								invo.setFromPlace(invoice.getFromPlace());
							}
							if(isNotEmpty(invoice.getFromPincode())) {
								invo.setFromPincode(invoice.getFromPincode());
							}
							if(isNotEmpty(invoice.getFromStateCode())) {
								invo.setFromStateCode(invoice.getFromStateCode());
							}
							if(isNotEmpty(invoice.getToGstin())) {
								invo.setToGstin(invoice.getToGstin());
							}
							if(isNotEmpty(invoice.getToTrdName())) {
								invo.setToTrdName(invoice.getToTrdName());
							}
							if(isNotEmpty(invoice.getToAddr1())) {
								invo.setToAddr1(invoice.getToAddr1());
							}
							if(isNotEmpty(invoice.getToAddr2())) {
								invo.setToAddr2(invoice.getToAddr2());
							}
							if(isNotEmpty(invoice.getToPincode())) {
								invo.setToPincode(invoice.getToPincode());
							}
							if(isNotEmpty(invoice.getToPlace())) {
								invo.setToPlace(invoice.getToPlace());
							}
							if(isNotEmpty(invoice.getToStateCode())) {
								invo.setToStateCode(invoice.getToStateCode());
							}
							if(isNotEmpty(invoice.getTransporterId())) {
								invo.setTransporterId(invoice.getTransporterId());
							}
							if(isNotEmpty(invoice.getTransporterName())) {
								invo.setTransporterName(invoice.getTransporterName());
							}
							if(isNotEmpty(invoice.getStatus())) {
								invo.setStatus(invoice.getStatus());
							}
							if(isNotEmpty(invoice.getActualDist())) {
								invo.setActualDist(invoice.getActualDist());
							}
							if(isNotEmpty(invoice.getNoValidDays())) {
								invo.setNoValidDays(invoice.getNoValidDays());
							}
							if(isNotEmpty(invoice.getValidUpto())) {
								invo.setValidUpto(invoice.getValidUpto());
							}
							if(isNotEmpty(invoice.getExtendedTimes())) {
								invo.setExtendedTimes(invoice.getExtendedTimes());
							}
							if(isNotEmpty(invoice.getRejectStatus())) {
								invo.setRejectStatus(invoice.getRejectStatus());
							}
							if(isNotEmpty(vehicleType)) {
								invo.setVehicleType(vehicleType);
							}
							if(isNotEmpty(invoice.getTransactionType())) {
								invo.setTransactionType(invoice.getTransactionType());
							}
							if(isNotEmpty(invoice.getOtherValue())) {
								invo.setOtherValue(invoice.getOtherValue());
							}
							if(isNotEmpty(invoice.getCessNonAdvolValue())) {
								invo.setCessNonAdvolValue(invoice.getCessNonAdvolValue());
							}
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase("Unclaimed")) {
							if(invoice.getInvtype().equals(MasterGSTConstants.B2B) || invoice.getInvtype().equals(MasterGSTConstants.B2C) || invoice.getInvtype().equals(MasterGSTConstants.B2CL)
									|| invoice.getInvtype().equals(MasterGSTConstants.EXPORTS) || invoice.getInvtype().equals(MasterGSTConstants.NIL)) {
								invo.setDocType("INV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES) || invoice.getInvtype().equals(MasterGSTConstants.ATPAID)) {
								invo.setDocType("ADV");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
								invo.setDocType("IMPG");
							}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
								invo.setDocType("IMPS");
							}
						}
						if(isNotEmpty(item.getItemno())) {
							invo.setItemname(item.getItemno());
						}
						if(isNotEmpty(item.getItemNotescomments())) {
							invo.setItemNotesComments(item.getItemNotescomments());
						}
						if(isNotEmpty(client.getGstnnumber())) {
							invo.setCompanyGSTIN(client.getGstnnumber());
						}
						if(isNotEmpty(client.getStatename())) {
							invo.setCompanyStatename(client.getStatename());
						}
						if (isNotEmpty(invoice.getBilledtoname())) {
							invo.setCustomerName(invoice.getBilledtoname());
						}
						if ((returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((GSTR2)invoice).getImpGoods().get(0).getStin());
						}else if ((returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER)) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods()) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0)) && isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getStin())) {
							invo.setCustomerGSTIN(((PurchaseRegister)invoice).getImpGoods().get(0).getStin());
						}else {
							if (isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
								invo.setCustomerGSTIN(invoice.getB2b().get(0).getCtin());
							}
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)  || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getCdna().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntNum());
								}
								if(isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt())) {
									invo.setOriginalInvoiceDate(Utility.getFormatedDateStr(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getOntDt()));
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA)) {
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(((GSTR2)invoice).getB2ba().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
									invo.setOriginalInvoiceNo(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOinum());
								}
								if(isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt())) {
									invo.setOriginalInvoiceDate(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getOidt());
								}
							}
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2A)) {
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B) && isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv()) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
									String gstinNumber = invoice.getB2b().get(0).getInv().get(0).getPos();
									for (StateConfig state : states) {
										if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
											invo.setPlaceOfSupply(state.getName());
											break;
										}
									}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2BA) && isNotEmpty(((GSTR2)invoice).getB2ba()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv()) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0)) && isNotEmpty(((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getB2ba().get(0).getInv().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) && isNotEmpty(invoice.getCdn()) && isNotEmpty(invoice.getCdn().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt()) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0)) && isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getPos())) {
								String gstinNumber = invoice.getCdn().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) && isNotEmpty(((GSTR2)invoice).getCdna()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt()) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0)) && isNotEmpty(((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos())) {
								String gstinNumber = ((GSTR2)invoice).getCdna().get(0).getNt().get(0).getPos();
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
							
							if (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) && isNotEmpty(((GSTR2)invoice).getImpGoods()) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0)) && isNotEmpty(((GSTR2)invoice).getImpGoods().get(0).getStin())) {
								String gstinNumber = ((GSTR2)invoice).getImpGoods().get(0).getStin().trim();
								gstinNumber = gstinNumber.substring(0,2);
								for (StateConfig state : states) {
									if (state.getTin().equals(Integer.parseInt(gstinNumber))) {
										invo.setPlaceOfSupply(state.getName());
										break;
									}
								}
							}
						}
						if (isNotEmpty(item.getIgstrate())) {
							invo.setIgstRate(item.getIgstrate());
						}
						if(isEmpty(item.getExmepted())) {
							invo.setExemptedVal(0d);
						}
						if(isEmpty(item.getIgstamount())){
							item.setIgstamount(0d);
						}
						if(isEmpty(item.getSgstamount())){
							item.setSgstamount(0d);
						}
						if(isEmpty(item.getCgstamount())){
							item.setCgstamount(0d);
						}
						if(isEmpty(item.getCessamount())){
							item.setCessamount(0d);
						}
						if(isNotEmpty(item.getElgpercent())) {
							invo.setEligiblePercentage(item.getElgpercent()+"");
						}
						if (isNotEmpty(item.getElg())) {
							String itcType = "";
							String elg = item.getElg();
							if("cp".equals(elg)){
								itcType = "Capital Good";
							}else if("ip".equals(elg)){
								itcType = "Inputs";
							}else if("is".equals(elg)){
								itcType = "Input Service";
							}else if("no".equals(elg)){
								itcType = "Ineligible";
							}else if("pending".equals(elg)){
								itcType = "Not Selected";
							}
							
							invo.setItcType(itcType);
						}else {
							//Nill Supplies itc type not applicable, but end-users asks don't empty keep Ineligible.
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.NIL) && (returntype.equals(GSTR2) || returntype.equals(PURCHASE_REGISTER))) {
								invo.setItcType("Ineligible");
							}else {
								invo.setItcType("");								
							}
						}
						
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR1) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR1) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}}
										//System.out.println("C --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(-item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}}
									}
									
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
										//System.out.println("D --->"+totisgt);
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}}
									}
									if(isNotEmpty(item.getExmepted())) {
										invo.setExemptedVal(item.getExmepted());
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());

										if(!refids.contains(invoice.getId().toString())){
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA))){
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(-item.getIgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt-item.getIgstamount();
									}else {
										totisgt = totisgt-item.getIgstamount();
									}}
									//System.out.println("C --->"+totisgt);
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(-item.getExmepted());
								}
								if (isNotEmpty(item.getAdvadjustedAmount())) {
									invo.setTaxableValue(-item.getAdvadjustedAmount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable-item.getAdvadjustedAmount();
									}else {
										tottaxable = tottaxable-item.getAdvadjustedAmount();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(-item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt-item.getCgstamount();
									}else {
										totcsgt = totcsgt-item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(-item.getSgstamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt-item.getSgstamount();
									}else {
										totssgt = totssgt-item.getSgstamount();
									}}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(-item.getCessamount());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess-item.getCessamount();
									}else {
										totcess = totcess-item.getCessamount();
									}}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(-item.getTotal());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal-item.getTotal();
									}else {
										tottotal = tottotal-item.getTotal();
									}}
								}
								
								invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}}
								}
								if(isNotEmpty(item.getExmepted())) {
									invo.setExemptedVal(item.getExmepted());
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());

									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdn().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdn().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(invoice.getCdnur().get(0).getInum());
									}
									if(isNotEmpty(invoice.getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(invoice.getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR4) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR4) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR4) invoice).getCdnur().get(0).getIdt());
									}
								}
								
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnr().get(0).getNt().get(0).getIdt());
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getInum())) {
										invo.setOriginalInvoiceNo(((GSTR5) invoice).getCdnur().get(0).getInum());
									}
									if(isNotEmpty(((GSTR5) invoice).getCdnur().get(0).getIdt())) {
										invo.setOriginalInvoiceDate(((GSTR5) invoice).getCdnur().get(0).getIdt());
									}
								}
								if(docType.equalsIgnoreCase("C")) {
									invo.setDocType("CDN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(-item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt-item.getIgstamount();
										}else {
											totisgt = totisgt-item.getIgstamount();
										}
										//System.out.println("C --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(-item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable-item.getTaxablevalue();
										}else {
											tottaxable = tottaxable-item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(-item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt-item.getCgstamount();
										}else {
											totcsgt = totcsgt-item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(-item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt-item.getSgstamount();
										}else {
											totssgt = totssgt-item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(-item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess-item.getCessamount();
										}else {
											totcess = totcess-item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(-item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal-item.getTotal();
										}else {
											tottotal = tottotal-item.getTotal();
										}
									}
									invo.setTotaltax(-item.getIgstamount() - item.getSgstamount() - item.getCgstamount() - item.getCessamount());
								}else if(docType.equalsIgnoreCase("D")) {
									invo.setDocType("DBN");
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
										//System.out.println("D --->"+totisgt);
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}else {
									if (isNotEmpty(item.getIgstamount())) {
										invo.setIgstAmount(item.getIgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotisgt = ctotisgt+item.getIgstamount();
										}else {
											totisgt = totisgt+item.getIgstamount();
										}
									}
									if (isNotEmpty(item.getTaxablevalue())) {
										invo.setTaxableValue(item.getTaxablevalue());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottaxable = ctottaxable+item.getTaxablevalue();
										}else {
											tottaxable = tottaxable+item.getTaxablevalue();
										}
									}
									if (isNotEmpty(item.getCgstamount())) {
										invo.setCgstAmount(item.getCgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcsgt = ctotcsgt+item.getCgstamount();
										}else {
											totcsgt = totcsgt+item.getCgstamount();
										}
									}
									if (isNotEmpty(item.getSgstamount())) {
										invo.setSgstAmount(item.getSgstamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotssgt = ctotssgt+item.getSgstamount();
										}else {
											totssgt = totssgt+item.getSgstamount();
										}
									}
									if (isNotEmpty(item.getCessamount())) {
										invo.setCessAmount(item.getCessamount());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctotcess = ctotcess+item.getCessamount();
										}else {
											totcess = totcess+item.getCessamount();
										}
									}
									if(isNotEmpty(item.getTotal())){
										invo.setTotalValue(item.getTotal());
										if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
											ctottotal = ctottotal+item.getTotal();
										}else {
											tottotal = tottotal+item.getTotal();
										}
									}
									invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
								}
							}else {
								if (isNotEmpty(item.getIgstamount())) {
									invo.setIgstAmount(item.getIgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotisgt = ctotisgt+item.getIgstamount();
									}else {
										totisgt = totisgt+item.getIgstamount();
									}
								}
								if (isNotEmpty(item.getTaxablevalue())) {
									invo.setTaxableValue(item.getTaxablevalue());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottaxable = ctottaxable+item.getTaxablevalue();
									}else {
										tottaxable = tottaxable+item.getTaxablevalue();
									}
								}
								if (isNotEmpty(item.getCgstamount())) {
									invo.setCgstAmount(item.getCgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcsgt = ctotcsgt+item.getCgstamount();
									}else {
										totcsgt = totcsgt+item.getCgstamount();
									}
								}
								if (isNotEmpty(item.getSgstamount())) {
									invo.setSgstAmount(item.getSgstamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotssgt = ctotssgt+item.getSgstamount();
									}else {
										totssgt = totssgt+item.getSgstamount();
									}
								}
								if (isNotEmpty(item.getCessamount())) {
									invo.setCessAmount(item.getCessamount());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctotcess = ctotcess+item.getCessamount();
									}else {
										totcess = totcess+item.getCessamount();
									}
								}
								if(isNotEmpty(item.getTotal())){
									invo.setTotalValue(item.getTotal());
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottotal = ctottotal+item.getTotal();
									}else {
										tottotal = tottotal+item.getTotal();
									}
								}
								invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
							}
						}else {
							if (isNotEmpty(item.getIgstamount())) {
								invo.setIgstAmount(item.getIgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotisgt = ctotisgt+item.getIgstamount();
								}else {
									totisgt = totisgt+item.getIgstamount();
								}}
							}
							if (isNotEmpty(item.getTaxablevalue())) {
								invo.setTaxableValue(item.getTaxablevalue());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottaxable = ctottaxable+item.getTaxablevalue();
								}else {
									tottaxable = tottaxable+item.getTaxablevalue();
								}}
							}
							if(isNotEmpty(item.getFreeQty())) {
								invo.setFreeQty(item.getFreeQty());
							}
							if(isNotEmpty(item.getAssAmt())) {
								invo.setAssAmt(item.getAssAmt());
								totAss  = totAss+item.getAssAmt();
							}
							if(isNotEmpty(item.getStateCess())) {
								invo.setStateCess(item.getStateCess());
								totStateCess  = totStateCess + item.getStateCess();
							}
							if(isNotEmpty(item.getCessNonAdvol())) {
								invo.setCessnonAdvol(item.getCessNonAdvol());
								totCessNonAdvol  = totCessNonAdvol + item.getCessNonAdvol();
							}
							if (isNotEmpty(item.getCgstamount())) {
								invo.setCgstAmount(item.getCgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcsgt = ctotcsgt+item.getCgstamount();
								}else {
									totcsgt = totcsgt+item.getCgstamount();
								}}
							}
							if (isNotEmpty(item.getSgstamount())) {
								invo.setSgstAmount(item.getSgstamount());
								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotssgt = ctotssgt+item.getSgstamount();
								}else {
									totssgt = totssgt+item.getSgstamount();
								}}
							}
							if (isNotEmpty(item.getCessamount())) {
								invo.setCessAmount(item.getCessamount());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctotcess = ctotcess+item.getCessamount();
								}else {
									totcess = totcess+item.getCessamount();
								}}
							}
							if(isNotEmpty(item.getTotal())){
								invo.setTotalValue(item.getTotal());

								if(!refids.contains(invoice.getId().toString())){
								if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
									ctottotal = ctottotal+item.getTotal();
								}else {
									tottotal = tottotal+item.getTotal();
								}}
							}
							invo.setTotaltax(item.getIgstamount() + item.getSgstamount() + item.getCgstamount() + item.getCessamount());
						}
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
								if(isNotEmpty(invoice.getCdn().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getCdn().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}else if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.B2B)) {
								if(isNotEmpty(invoice.getB2b().get(0).getCfs())) {
									if("Y".equalsIgnoreCase(invoice.getB2b().get(0).getCfs())) {
										invo.setCounterFilingStatus("Filed");
									}else {
										invo.setCounterFilingStatus("Pending");
									}
								}else {
									invo.setCounterFilingStatus("Pending");
								}
							}
						}
						double totalitc = 0d;
						if (isNotEmpty(item.getIgstavltax())) {
							invo.setIgstTax(item.getIgstavltax());
							totalitc += item.getIgstavltax();
						}
						if (isNotEmpty(item.getCgstrate())) {
							invo.setCgstRate(item.getCgstrate());
						}
						if (isNotEmpty(item.getCgstavltax())) {
							invo.setCgstTax(item.getCgstavltax());
							totalitc += item.getCgstavltax();
						}
						if (isNotEmpty(item.getSgstrate())) {
							invo.setSgstRate(item.getSgstrate());
						}
						if (isNotEmpty(item.getSgstavltax())) {
							invo.setSgstTax(item.getSgstavltax());
							totalitc += item.getSgstavltax();
						}
						if (isNotEmpty(item.getCessrate())) {
							invo.setCessRate(item.getCessrate());
						}
						if (isNotEmpty(item.getCessavltax())) {
							invo.setCessTax(item.getCessavltax());
							totalitc += item.getCessavltax();
						}
						
						double ineligibleitc = 0d;
						double totalitcavailble = item.getIgstamount()+item.getCgstamount()+item.getSgstamount()+item.getCessamount();
						ineligibleitc = totalitcavailble - totalitc; 
						if(isNotEmpty(item.getQuantity())){
							invo.setQuantity(item.getQuantity());
						}
						if(isNotEmpty(item.getUqc())){
							invo.setUqc(item.getUqc());
						}
						if(isNotEmpty(item.getHsn())){
							invo.setHsnCode(item.getHsn());
						}
						if(isNotEmpty(item.getRateperitem())){
							invo.setRateperitem(item.getRateperitem());
						}
						if(isNotEmpty(item.getCategory())){
							invo.setCategory(item.getCategory());
						}
						if (isNotEmpty(invoice.getStatename())) {
							invo.setState(invoice.getStatename());
						}
						if (isNotEmpty(invoice.getInvoiceno())) {
							invo.setInvoiceNo(invoice.getInvoiceno());
						}
						invo.setInvoiceDate(invoice.getDateofinvoice());
						invo.setReturnPeriod(invoice.getFp());
						if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR))
								|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA)){
							String docType = "";
							if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
								if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR))
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)
											|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
										if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
											docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
											|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
										if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
									}
								}
							}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
									if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
										if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
											docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
										}
									}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
										if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
											docType = invoice.getCdnur().get(0).getNtty();
										}
									}
								}
							}
							if("C".equals(docType)) {
								invo.setType("Credit Note");
								invo.setTotalItc(-totalitc);
								invo.setTotalinItc(-ineligibleitc);
							}else if("D".equals(docType)) {
								invo.setType("Debit Note");
								invo.setTotalItc(totalitc);
								invo.setTotalinItc(ineligibleitc);
							}else {
								invo.setType(invoice.getInvtype());
								invo.setTotalItc(totalitc);
								invo.setTotalinItc(ineligibleitc);
							}
						}else {
							invo.setType(invoice.getInvtype());
							invo.setTotalItc(totalitc);
							invo.setTotalinItc(ineligibleitc);
						}	
						
						
						if(invo.getGstStatus().equalsIgnoreCase("Cancelled")) {
							invoiceVOCancelledList.add(invo);
						}else {
							invoiceVOList.add(invo);
						}
					}
				}
					if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equals(MasterGSTConstants.CDNUR)
							|| invoice.getInvtype().equals(MasterGSTConstants.CDNA) || invoice.getInvtype().equals(MasterGSTConstants.CDNURA))) {
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(isNotEmpty(invoice.getInvtype()) && (invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR))
									|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) 
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)
										|| invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNURA)){
									if(isNotEmpty(((GSTR1) invoice).getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}}
								}else {
									if(!refids.contains(invoice.getId().toString())){
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR2) || returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equalsIgnoreCase(MasterGSTConstants.GSTR6)) {
							if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)) {
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getNtty())) {
										docType = invoice.getCdn().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(invoice.getCdnur().get(0).getNtty())) {
										docType = invoice.getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR4)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR4)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR4)invoice).getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}else if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR5)) {
							if(isNotEmpty(invoice.getInvtype()) && invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
								String docType = "";
								if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
									if(isNotEmpty(((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnr().get(0).getNt().get(0).getNtty();
									}
								}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CDNUR)){
									if(isNotEmpty(((GSTR5)invoice).getCdnur().get(0).getNtty())) {
										docType = ((GSTR5)invoice).getCdnur().get(0).getNtty();
									}
								}
								if("C".equals(docType)) {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax-invoice.getTotaltax();
									}else {
										tottax = tottax-invoice.getTotaltax();
									}
								}else {
									if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
										ctottax = ctottax+invoice.getTotaltax();
									}else {
										tottax = tottax+invoice.getTotaltax();
									}
								}
							}
						}
					}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.ATPAID) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.TXPA)){
						if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
							if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax-invoice.getTotaltax();
							}else {
								tottax = tottax-invoice.getTotaltax();
							}}
						}else {
							if(!refids.contains(invoice.getId().toString())){
							if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
								ctottax = ctottax+invoice.getTotaltax();
							}else {
								tottax = tottax+invoice.getTotaltax();
							}}
						}
					}else {
						if(!refids.contains(invoice.getId().toString())){
						if(isNotEmpty(invoice.getGstStatus()) && invoice.getGstStatus().equalsIgnoreCase("Cancelled")) {
							ctottax = ctottax+invoice.getTotaltax();
						}else {
							tottax = tottax+invoice.getTotaltax();
						}}
					}
			}
		}
		InvoiceVO totalinvo = new InvoiceVO();
		totalinvo.setIgstAmount(totisgt);
		totalinvo.setCgstAmount(totcsgt);
		totalinvo.setSgstAmount(totssgt);
		totalinvo.setCessAmount(totcess);
		totalinvo.setTotaltax(tottax);
		totalinvo.setTaxableValue(tottaxable);
		if(isNotEmpty(totAss)) {
			totalinvo.setAssAmt(totAss);
		}
		if(isNotEmpty(totStateCess)) {
			totalinvo.setStateCess(totStateCess);
		}
		if(isNotEmpty(totCessNonAdvol)) {
			totalinvo.setCessnonAdvol(totCessNonAdvol);
		}
		totalinvo.setTotalValue(tottotal);
		invoiceVOList.add(totalinvo);
		if(invoiceVOCancelledList.size() > 0) {
			InvoiceVO emptyinvo = new InvoiceVO();
			InvoiceVO emptyinvo1 = new InvoiceVO();
			emptyinvo1.setCompanyStatename("Cancelled Invoices");
			invoiceVOList.add(emptyinvo);
			invoiceVOList.add(emptyinvo1);
			invoiceVOList.addAll(invoiceVOCancelledList);
			InvoiceVO ctotalinvo = new InvoiceVO();
			ctotalinvo.setIgstAmount(ctotisgt);
			ctotalinvo.setCgstAmount(ctotcsgt);
			ctotalinvo.setSgstAmount(ctotssgt);
			ctotalinvo.setCessAmount(ctotcess);
			ctotalinvo.setTotaltax(ctottax);
			ctotalinvo.setTaxableValue(ctottaxable);
			ctotalinvo.setTotalValue(ctottotal);
			invoiceVOList.add(ctotalinvo);
		}
		return invoiceVOList;
	}
	
}
