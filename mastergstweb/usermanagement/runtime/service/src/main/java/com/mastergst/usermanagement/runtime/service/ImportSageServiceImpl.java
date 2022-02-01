package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.ss.usermodel.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;

@Service
public class ImportSageServiceImpl implements ImportSageService{
	@Autowired
	private ImportInvoiceService importInvoiceService; 
	
	@Override
	public InvoiceParent getInvoice(Client client, InvoiceParent invoice,String[] patterns,int year,int month,String returntype,Map<String,StateConfig> statetin,String imptype) {
		String stateName = client.getStatename();
		
		
		boolean interStateFlag = false;
		if (returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER)) {
			if(isEmpty(invoice.getStatename())) {
				invoice.setStatename(client.getStatename());
			}
		}else {
			if (isNotEmpty(invoice.getStatename())) {
				String statename = invoice.getStatename();
				if(isNotEmpty(statename) && statename.contains("&")) {
					statename = importInvoiceService.invStatename(statename);
				}
				StateConfig state = statetin.get(statename);
				if(isNotEmpty(state)) {
					statename = state.getName();
				}
				invoice.setStatename(statename);
			}
		}
		if (isNotEmpty(invoice.getRevchargetype())) {
			String revChargeType = invoice.getRevchargetype();
			if ("YES".equalsIgnoreCase(revChargeType) || "Y".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Reverse");
			} else if ("NO".equalsIgnoreCase(revChargeType) || "N".equalsIgnoreCase(revChargeType)) {
				invoice.setRevchargetype("Regular");
			}
		}else {
			invoice.setRevchargetype("Regular");
		}
		String invTyps = "";
		if(!MasterGSTConstants.NIL.equalsIgnoreCase(invoice.getInvtype())) {
			if (isNotEmpty(invoice.getCategorytype())) {
				String invTyp = invoice.getCategorytype();
				if ("Regular".equalsIgnoreCase(invTyp) || "R".equalsIgnoreCase(invTyp)) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
					invTyps = "R";
				} else if ("Deemed Exports".equalsIgnoreCase(invTyp) || "DE".equalsIgnoreCase(invTyp)) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("DE");
					invTyps = "DE";
				} else if ("Supplies to SEZ with payment".equalsIgnoreCase(invTyp) || "SEWP".equalsIgnoreCase(invTyp)) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWP");
					invTyps = "SEWP";
				} else if ("Supplies to SEZ without payment".equalsIgnoreCase(invTyp) || "SEWOP".equalsIgnoreCase(invTyp)) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("SEWOP");
					invTyps = "SEWOP";
				} else if ("Sale from Bonded Warehouse".equalsIgnoreCase(invTyp) || "CBW".equalsIgnoreCase(invTyp)) {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp("CBW");
					invTyps = "CBW";
				} else {
					invoice.getB2b().get(0).getInv().get(0).setInvTyp(invTyp);
					invTyps = invTyp;
				}
			} else {
				invoice.getB2b().get(0).getInv().get(0).setInvTyp("R");
				invTyps = "R";
			}
		}
		if ("GSTR1".equals(returntype) || "Sales Register".equals(returntype) || "SalesRegister".equals(returntype)) {
			if (stateName.equals(invoice.getStatename())) {
				interStateFlag = false;
			} else {
				interStateFlag = true;
			}
		}else {
			String statenameFromGstin = "";
			String pos = "";
			if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin()) && isNotEmpty(invoice.getB2b().get(0).getCtin().trim())) {
				statenameFromGstin = (invoice.getB2b().get(0).getCtin().trim()).substring(0, 2);
			}
			if(isNotEmpty(stateName)) {
				String[] nm = (stateName).split("-");
				pos = nm[0];
			}
			if(isNotEmpty(statenameFromGstin) && isNotEmpty(pos)) {
				if (statenameFromGstin.equals(pos)) {
					interStateFlag = false;
				} else {
					interStateFlag = true;
				}
				
			}else {
				interStateFlag = true;
			}
		}
			
		if("WPAY".equalsIgnoreCase(invTyps) || "SEWP".equals(invTyps) ) {
			interStateFlag = true;
		}
		invoice = invdetails(invoice,patterns,year,month,returntype,interStateFlag,imptype);
		return invoice;
	}

	
	public InvoiceParent invdetails(InvoiceParent invoice,String[] patterns,int year,int month,String returntype,boolean interStateFlag,String imptype) {
		if (isNotEmpty(invoice.getStrDate())) {
			invoice.setDateofinvoice(invdate(invoice.getStrDate(),patterns,year,month));
		}
		if (isNotEmpty(invoice.getTransactionDate())) {
			invoice.setBillDate(invdate(invoice.getTransactionDate(),patterns,year,month));
		}else {
			if(isNotEmpty(invoice.getDateofinvoice())) {
				invoice.setBillDate(invoice.getDateofinvoice());
			}
		}
		if (isNotEmpty(invoice.getItcClaimedDate())) {
			invoice.setDateofitcClaimed(invdate(invoice.getItcClaimedDate(),patterns,year,month));
		}
		if(isNotEmpty(invoice.getItems().get(0).getUgstamount()) && Math.abs(invoice.getItems().get(0).getUgstamount()) > 0) {
			invoice.getItems().get(0).setSgstamount(Math.abs(invoice.getItems().get(0).getUgstamount()));
			invoice.getItems().get(0).setUgstamount(Math.abs(invoice.getItems().get(0).getUgstamount()));
		}
		if(isNotEmpty(invoice.getItems().get(0).getUgstrate()) && invoice.getItems().get(0).getUgstrate() > 0) {
			invoice.getItems().get(0).setSgstrate(invoice.getItems().get(0).getUgstrate());
		}
		
		if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
			invoice.getItems().get(0).setTaxablevalue(Math.abs(invoice.getItems().get(0).getTaxablevalue()));
			if(isEmpty(invoice.getItems().get(0).getRateperitem())) {
				invoice.getItems().get(0).setRateperitem(invoice.getItems().get(0).getTaxablevalue());
			}
			if(isEmpty(invoice.getItems().get(0).getQuantity())) {
				invoice.getItems().get(0).setQuantity(1d);
			}
		}

		if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
			invoice.getItems().get(0).setIgstamount(Math.abs(invoice.getItems().get(0).getIgstamount()));
		}
		if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
			invoice.getItems().get(0).setCgstamount(Math.abs(invoice.getItems().get(0).getCgstamount()));
		}
		if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
			invoice.getItems().get(0).setSgstamount(Math.abs(invoice.getItems().get(0).getSgstamount()));
		}
		if (isNotEmpty(invoice.getItems().get(0).getElg())) {
			String elgType = invoice.getItems().get(0).getElg();
			if ("Capital Good".equalsIgnoreCase(elgType) || "Capital Goods".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("cp");
			} else if ("Inputs".equalsIgnoreCase(elgType) || "Finished Goods".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("ip");
			} else if ("Input Service".equalsIgnoreCase(elgType) || "Service".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("is");
			} else if ("Ineligible".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("no");
			} else if ("Not Selected".equalsIgnoreCase(elgType)) {
				invoice.getItems().get(0).setElg("pending");
			}
		}
		if(imptype.equalsIgnoreCase("sage")) {
			if("GSTR2".equalsIgnoreCase(returntype) || "Purchase Register".equalsIgnoreCase(returntype) || "PurchaseRegister".equalsIgnoreCase(returntype)) {
				if(isEmpty(invoice.getItems().get(0).getElg())) {
					invoice.getItems().get(0).setElg("ip");
				}
				Double tax = 0d;
				Double taxAvail = 0d;
				
				if(isEmpty(invoice.getItems().get(0).getElgpercent())) {
					if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
						if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
							tax += invoice.getItems().get(0).getIgstamount();
						}
						
						if(isNotEmpty(invoice.getItems().get(0).getIgstavltax())) {
							taxAvail += invoice.getItems().get(0).getIgstavltax();
						}
					}else {
						if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
							tax += invoice.getItems().get(0).getIgstamount();
						}else {
							if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
								tax += invoice.getItems().get(0).getCgstamount();
							}
							if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
								tax += invoice.getItems().get(0).getSgstamount();
							}
						}
						
						if(isNotEmpty(invoice.getItems().get(0).getIgstavltax())) {
							taxAvail += invoice.getItems().get(0).getIgstavltax();
						}else {
							if(isNotEmpty(invoice.getItems().get(0).getCgstavltax())) {
								taxAvail += invoice.getItems().get(0).getCgstavltax();
							}
							if(isNotEmpty(invoice.getItems().get(0).getSgstavltax())) {
								taxAvail += invoice.getItems().get(0).getSgstavltax();
							}
						}
					}
					if(tax-taxAvail == 0d) {
						if("no".equalsIgnoreCase(invoice.getItems().get(0).getElg())) {
							invoice.getItems().get(0).setElgpercent(0d);
						}else {
							invoice.getItems().get(0).setElgpercent(100d);
						}
					}else {
						Double elgpercent = (tax-taxAvail)/100;
						invoice.getItems().get(0).setElgpercent(elgpercent);
					}
				}else {
					if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
						if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
							if(isNotEmpty(invoice.getItems().get(0).getElgpercent())) {
								Double igstAmount = invoice.getItems().get(0).getIgstamount();
								Double elgPercent = invoice.getItems().get(0).getElgpercent();
								Double igstAmountavlTax = igstAmount * elgPercent / 100;
								invoice.getItems().get(0).setIgstavltax(Math.round(igstAmountavlTax * 100.0) / 100.0);
							}
						}
					}else {
						if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
							if(isNotEmpty(invoice.getItems().get(0).getElgpercent())) {
								Double igstAmount = invoice.getItems().get(0).getIgstamount();
								Double elgPercent = invoice.getItems().get(0).getElgpercent();
								Double igstAmountavlTax = igstAmount * elgPercent / 100;
								invoice.getItems().get(0).setIgstavltax(Math.round(igstAmountavlTax * 100.0) / 100.0);
							}
						}else {
							if(isNotEmpty(invoice.getItems().get(0).getElgpercent())) {
								if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
									Double csgstAmount = invoice.getItems().get(0).getCgstamount();
									Double elgPercent = invoice.getItems().get(0).getElgpercent();
									Double csgstAmountavlTax = csgstAmount * elgPercent / 100;
									invoice.getItems().get(0).setCgstavltax(Math.round(csgstAmountavlTax * 100.0) / 100.0);
									invoice.getItems().get(0).setSgstavltax(Math.round(csgstAmountavlTax * 100.0) / 100.0);
								}
							}
						}
					}
				}
				
			}
		}
		if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_GOODS) || invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.IMP_SERVICES)) {
			if(isNotEmpty(invoice.getItems().get(0).getIgstrate())) {
				invoice.getItems().get(0).setRate(invoice.getItems().get(0).getIgstrate());
			}
		
		
		if(isEmpty(invoice.getItems().get(0).getTotal())) {
			Double total = 0d;
			if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
				total += invoice.getItems().get(0).getTaxablevalue();
			}
			if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
				total += invoice.getItems().get(0).getIgstamount();
			}
			invoice.getItems().get(0).setTotal(total);
			
		}
		}else {
			if (interStateFlag) {
				if(isNotEmpty(invoice.getItems().get(0).getIgstrate())) {
					invoice.getItems().get(0).setRate(invoice.getItems().get(0).getIgstrate());
				}
			}else {
				Double rate = 0d;
				if(isNotEmpty(invoice.getItems().get(0).getSgstrate())) {
					rate += invoice.getItems().get(0).getSgstrate();
				}
				if(isNotEmpty(invoice.getItems().get(0).getCgstrate())) {
					rate += invoice.getItems().get(0).getCgstrate();
				}
				invoice.getItems().get(0).setRate(rate);
			}
			
			if(isEmpty(invoice.getItems().get(0).getTotal())) {
				Double total = 0d;
				if(isNotEmpty(invoice.getItems().get(0).getTaxablevalue())) {
					total += invoice.getItems().get(0).getTaxablevalue();
				}
				if(isNotEmpty(invoice.getItems().get(0).getIgstamount())) {
					total += invoice.getItems().get(0).getIgstamount();
				}else {
					if(isNotEmpty(invoice.getItems().get(0).getCgstamount())) {
						total += invoice.getItems().get(0).getCgstamount();
					}
					if(isNotEmpty(invoice.getItems().get(0).getSgstamount())) {
						total += invoice.getItems().get(0).getSgstamount();
					}
				}
				invoice.getItems().get(0).setTotal(total);
				
			}
		}
		
		
		return invoice;
	}
	
	
	@Override
	public Date invdate(String invdate, String[] patterns, int year, int month) {
		Date invoiceDate =new Date();
		try {
			if(invdate.contains("-") || invdate.contains("/")) {
				try {
					double dblDate = Double.parseDouble(invdate);
					invoiceDate = DateUtil.getJavaDate(dblDate);
				} catch (NumberFormatException exp) {
					Calendar cal = Calendar.getInstance();
					
					if(invdate.contains("-")) {
						
						String date[] = invdate.split("-");
						if(date.length >= 3) {
						int mnths = 1;
						int years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						if(date[1].length()>2) {
							String mnth = date[1].toUpperCase().replaceAll("\\s", "");
							
							mnths = month(mnth);
						}else {
							mnths = Integer.parseInt(date[1].replaceAll("\\s", ""));
						}
						if(date[2].length()<=2) {
							years = Integer.parseInt("20"+date[2].replaceAll("\\s", ""));
						}else {
							years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						}
						
						
						cal.set(years, mnths - 1, Integer.parseInt(date[0].replaceAll("\\s", "")));
						invoiceDate = cal.getTime();
						}
						
					}else if(invdate.contains("/")) {
						String date[] = invdate.split("/");
						if(date.length >= 3) {
						int mnths = 1;
						if(date[1].length()>2) {
							String mnth = date[1].toUpperCase().replaceAll("\\s", "");
							
							mnths = month(mnth);
						}else {
							mnths = Integer.parseInt(date[1].replaceAll("\\s", ""));
						}
						int years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						if(date[2].length()<=2) {
							years = Integer.parseInt("20"+date[2].replaceAll("\\s", ""));
						}else {
							years = Integer.parseInt(date[2].replaceAll("\\s", ""));
						}
						
						cal.set(years, mnths - 1, Integer.parseInt(date[0].replaceAll("\\s", "")));
						invoiceDate = cal.getTime();
						}
					}
				}
				
			}else {
				invoiceDate = DateUtils.parseDate(invdate,patterns);
			
			}
		} catch (java.text.ParseException e) {
			
			try {
				double dblDate = Double.parseDouble(invdate);
				invoiceDate = DateUtil.getJavaDate(dblDate);
				
			} catch (NumberFormatException exp) {
				Calendar cal = Calendar.getInstance();
				cal.set(year, month - 1, 1);
				invoiceDate = cal.getTime();
			}
		}
		return invoiceDate;
	}

	@Override
	public int month(String mnth) {
		int mnths = 1;
		switch(mnth) {
		case "JAN": mnths = 1;
		break;
		case "FEB": mnths = 2;
		break;
		case "MAR": mnths = 3;
		break;
		case "APR": mnths = 4;
		break;
		case "MAY": mnths = 5;
		break;
		case "JUN": mnths = 6;
		break;
		case "JUL": mnths = 7;
		break;
		case "AUG": mnths = 8;
		break;
		case "SEP": mnths = 9;
		break;
		case "OCT": mnths = 10;
		break;
		case "NOV": mnths = 11;
		break;
		case "DEC": mnths = 12;
		break;
		default : mnths = 1;
		break;
		}
		return mnths;
	}
	
	public String beantype(String invtype) {
		String beantype = "invoiceList";
		if("B2B".equalsIgnoreCase(invtype)) {
			beantype = "invoiceList";
		}else if("Credit/Debit Notes".equalsIgnoreCase(invtype)) {
			beantype = "creditList";
		}else if("Credit/Debit Note for Unregistered Taxpayers".equalsIgnoreCase(invtype)) {
			beantype = "cdnurList";
		}else if("B2B Unregistered".equalsIgnoreCase(invtype)) {
			beantype = "b2buList";
		}else if("Import Goods".equalsIgnoreCase(invtype)) {
			beantype = "impgList";
		}else if("Import Services".equalsIgnoreCase(invtype)) {
			beantype = "impsList";
		}else if("Nil Supplies".equalsIgnoreCase(invtype)) {
			beantype = "nilList";
		}else if("ITC Reversal".equalsIgnoreCase(invtype)) {
			beantype = "itrvslList";
		}else if("Exports".equalsIgnoreCase(invtype)) {
			beantype = "exportList";
		}
		return beantype;
	}

}
