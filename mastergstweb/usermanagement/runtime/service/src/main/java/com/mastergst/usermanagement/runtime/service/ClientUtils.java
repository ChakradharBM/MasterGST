package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.common.MasterGSTConstants.CDNUR;
import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.SendMsgsSummary;
import com.mastergst.usermanagement.runtime.dao.Gstr1Dao;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.ClientStatus;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.Estimates;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.GSTRB2CSA;
import com.mastergst.usermanagement.runtime.domain.GSTRItemDetails;
import com.mastergst.usermanagement.runtime.domain.GroupDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.ProformaInvoices;
import com.mastergst.usermanagement.runtime.domain.PurchaseOrder;
import com.mastergst.usermanagement.runtime.domain.PurchaseRegister;
import com.mastergst.usermanagement.runtime.domain.Reminders;
import com.mastergst.usermanagement.runtime.domain.SupplierStatus;
import com.mastergst.usermanagement.runtime.repository.GSTR1Repository;
import com.mastergst.usermanagement.runtime.repository.EstimatesRepository;
import com.mastergst.usermanagement.runtime.repository.GroupDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.SupplierStatusRepository;
import com.mastergst.usermanagement.runtime.response.Response;


@Component
public class ClientUtils {
	private static final Logger logger = LogManager.getLogger(ClientUtils.class.getName());
	private static final String CLASSNAME = "ClientStatusIhubUtil::";
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	@Autowired
	private ClientService clientService;
	@Autowired
	SupplierStatusRepository supplierStatusRepository;
	@Autowired
	GroupDetailsRepository groupDetailsRepository;
	@Autowired
	private IHubConsumerService iHubConsumerService;
	private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	@Autowired GSTR1Repository gstr1Repository;
	@Autowired	private Gstr1Dao gstr1Dao;
	
	public Response publicRettrack(String clientid,	String fy) {
		logger.debug(CLASSNAME + "publicRettrack:: Begin");
		logger.debug(CLASSNAME + "publicRettrack:: clientid {}", clientid);
		logger.debug(CLASSNAME + "publicRettrack:: fy {}", fy);
		Client client=clientService.findById(clientid);
		Response response = iHubConsumerService.publicRettrack(client.getGstnnumber(),fy);
		if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
				&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
			//CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
			//future.thenAcceptAsync(responseObj -> {
				response.getData().getStatusList().forEach(status->{
					try {
						//ClientStatus clientStatus = clientService.getClientStatus(clientid, status.getRtntype(), status.getRetPeriod());
						List<ClientStatus> clientStatusList = clientService.getClientReturnStatus(clientid, status.getRtntype(), status.getRetPeriod());
						
						if(isEmpty(clientStatusList)) {
							ClientStatus clientStatus = new ClientStatus();
							clientStatus.setClientId(clientid);
							if(isNotEmpty(status.getRetPeriod())) {
								clientStatus.setReturnPeriod(status.getRetPeriod());
							}
							if(isNotEmpty(status.getRtntype())) {
								clientStatus.setReturnType(status.getRtntype());
							}
							if(isNotEmpty(status.getStatus())) {
								clientStatus.setStatus(status.getStatus());
							}
							if(isNotEmpty(status.getArn())) {
								clientStatus.setArn(status.getArn());
							}
							if(isNotEmpty(status.getMof())) {
								clientStatus.setMof(status.getMof());
							}
							//SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
							if(isNotEmpty(status.getDof())) {
								clientStatus.setDof(dateFormat.parse(status.getDof()));								
							}
							clientService.saveClientStatus(clientStatus);
						}else {
							for(ClientStatus clnstatus : clientStatusList) {
									if(isNotEmpty(status.getArn())) {
										clnstatus.setArn(status.getArn());
									}
								if(isNotEmpty(status.getDof())) {
									clnstatus.setDof(dateFormat.parse(status.getDof()));								
								}
								if(isNotEmpty(status.getStatus())) {
									clnstatus.setStatus(status.getStatus());
								}
								clientService.saveClientStatus(clnstatus);
							}	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			//});
		}
		return response;
	}
	
	public Response supplierRettrack(String clientid, String supplierid, String suppliername,String gstin, String fy) {
		logger.debug(CLASSNAME + "rettrack:: Begin");
		logger.debug(CLASSNAME + "rettrack:: fy {}", fy);
		logger.debug(CLASSNAME + "rettrack:: gstin {}", gstin);
		logger.debug(CLASSNAME + "rettrack:: clientid {}", clientid);
		logger.debug(CLASSNAME + "rettrack:: supplierid {}", supplierid);
		logger.debug(CLASSNAME + "rettrack:: suppliername {}", suppliername);
		Response response = iHubConsumerService.publicRettrack(gstin,fy);
		if (isNotEmpty(response) && isNotEmpty(response.getData()) && isNotEmpty(response.getStatuscd())
				&& response.getStatuscd().equals(MasterGSTConstants.SUCCESS_CODE)) {
			CompletableFuture<Response> future = CompletableFuture.supplyAsync(() -> response);
			future.thenAcceptAsync(responseObj -> {
				responseObj.getData().getStatusList().forEach(status->{
					try {
						//SupplierStatus supplier=clientService.getSupplierStatusByMonthwise(supplierid, status.getRetPeriod(),status.getRtntype());
						
						List<SupplierStatus> supplierStatusList = clientService.getSupplierStatusByMonthwise(supplierid, status.getRetPeriod(),status.getRtntype());
						if(isEmpty(supplierStatusList)){
							SupplierStatus supplierStatus=new SupplierStatus();
							supplierStatus.setGstin(gstin);
							supplierStatus.setClientid(clientid);
							supplierStatus.setSupplierid(supplierid);
							supplierStatus.setSuppliername(suppliername);
							
							if(isNotEmpty(status.getRetPeriod())) {
								supplierStatus.setReturnperiod(status.getRetPeriod());
							}
							if(isNotEmpty(status.getRtntype())) {
								supplierStatus.setReturntype(status.getRtntype());
							}
							if(isNotEmpty(status.getStatus())) {
								supplierStatus.setStatus(status.getStatus());
							}
							if(isNotEmpty(status.getArn())) {
								supplierStatus.setArn(status.getArn());
							}
							if(isNotEmpty(status.getMof())) {
								supplierStatus.setMof(status.getMof());
							}
							if(isNotEmpty(status.getDof())) {
								SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
								supplierStatus.setDof(dateFormat.parse(status.getDof()));								
							}
							supplierStatusRepository.save(supplierStatus);							
						}else {
							for(SupplierStatus suppstatus : supplierStatusList) {
								if(isEmpty(suppstatus.getArn())) {
									if(isNotEmpty(status.getArn())) {
										suppstatus.setArn(status.getArn());
									}
								}
								if(isNotEmpty(status.getStatus())) {
									suppstatus.setStatus(status.getStatus());
								}
								supplierStatusRepository.save(suppstatus);	
							}	
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			});
		}
		return response;
	}
	
	public Map<String, String> getTotals(InvoiceParent invoice, String returntype, Reminders mailData) {
		Map<String,String> invDetails = Maps.newHashMap();
		String invNumberText="";String invDateText="";
		if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1) || returntype.equalsIgnoreCase("SalesRegister")) {
			invoice = (GSTR1)invoice;
			invNumberText = "Invoice No";
			invDateText = "Invoice Date";
			invDetails.put("irnNo", "");
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.DELIVERYCHALLANS)) {
			invoice = (DeliveryChallan)invoice;
			invNumberText = "Delivery Challan No";
			invDateText = "Delivery Challan Date";
			invDetails.put("irnNo", "");
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.PROFORMAINVOICES)) {
			invoice = (ProformaInvoices)invoice;
			invNumberText = "Proforma Invoice No";
			invDateText = "Proforma Invoice Date";
			invDetails.put("irnNo", "");
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.ESTIMATES)) {
			invoice = (Estimates)invoice;
			invNumberText = "Estimate No";
			invDateText = "Estimate Date";
			invDetails.put("irnNo", "");
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASEORDER)) {
			invoice = (PurchaseOrder)invoice;
			invNumberText = "Purchase Order No";
			invDateText = "Purchase Order Date";
			invDetails.put("irnNo", "");
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)) {
			invoice = (GSTR1)invoice;
			invNumberText = "Invoice No";
			invDateText = "Invoice Date";
			if(isNotEmpty(invoice.getIrnNo())) {
				invDetails.put("irnNo", invoice.getIrnNo());
			}else {
				invDetails.put("irnNo", "");
			}
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
		invDetails.put("invNumberText", invNumberText);
		invDetails.put("invDateText", invDateText);
		invDetails.put("invoiceno", invoice.getInvoiceno());
		invDetails.put("invoicedate", simpleDateFormat.format(invoice.getDateofinvoice()));
		invDetails.put("pos", invoice.getStatename());
		
		if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
			invDetails.put("gstin", invoice.getB2b().get(0).getCtin());
		}
		if(isNotEmpty(mailData)) {
			if(isNotEmpty(mailData.getMessage())) {
				invDetails.put("mailMessage", mailData.getMessage());
			}else {
				invDetails.put("mailMessage", "Here's your invoice! We appreciate your prompt payment.");
			}
			if(isNotEmpty(mailData.getClientName())) {
				invDetails.put("customerName",mailData.getClientName());
			}else {
				invDetails.put("customerName",invoice.getBilledtoname());
			}
		}
		String taxable = String.format("%.2f", invoice.getTotaltaxableamount());  
		invDetails.put("taxableVal",getIndianCurrencyFormat(taxable));
		String totigst = String.format("%.2f", invoice.getTotalIgstAmount());  
		invDetails.put("igstVal", getIndianCurrencyFormat(totigst));
		String totcgst = String.format("%.2f", invoice.getTotalCgstAmount());  
		invDetails.put("cgstVal", getIndianCurrencyFormat(totcgst));
		String totsgst = String.format("%.2f", invoice.getTotalSgstAmount());  
		invDetails.put("sgstVal", getIndianCurrencyFormat(totsgst));
		String totamt = String.format("%.2f", invoice.getTotalamount());  
		invDetails.put("totalVal", getIndianCurrencyFormat(totamt));
		return invDetails;
	}

	public Map<String, List<SendMsgsSummary>> getMailDetails(InvoiceParent invoice, String returntype) {
		Map<String, List<SendMsgsSummary>> invmap = Maps.newHashMap();
		if(returntype.equalsIgnoreCase(MasterGSTConstants.GSTR1)) {
			invoice = (GSTR1)invoice;
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.DELIVERYCHALLANS)) {
			invoice = (DeliveryChallan)invoice;
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.PROFORMAINVOICES)) {
			invoice = (ProformaInvoices)invoice;
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.ESTIMATES)) {
			invoice = (Estimates)invoice;
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.PURCHASEORDER)) {
			invoice = (PurchaseOrder)invoice;
		}
		List<SendMsgsSummary> invsummary=new ArrayList<SendMsgsSummary>();
		for(Item item : invoice.getItems()) {
			SendMsgsSummary ssum= new SendMsgsSummary();
			if(isNotEmpty(item.getItemno())) {
				ssum.setItemNo(item.getItemno());
			}else {
				ssum.setItemNo("");
			}
			if(isNotEmpty(item.getHsn())) {
				ssum.setHsn(item.getHsn());
			}
			if(isNotEmpty(item.getUqc())) {
				ssum.setUqc(item.getUqc());
			}
			if(isNotEmpty(item.getQuantity())) {
				ssum.setQuantity(item.getQuantity());
			}
			if(isNotEmpty(item.getRateperitem())) {
				String s=Double.toString(item.getRateperitem());
				ssum.setsRate(getIndianCurrencyFormat(s));
				ssum.setRate(item.getRateperitem());
			}
			if(isNotEmpty(item.getRate())) {
				ssum.setTaxrate(item.getRate());
			}
			if(isNotEmpty(item.getTaxablevalue())) {
				String ttv = df2.format(item.getTaxablevalue());
				ssum.setsTaxableAmt(getIndianCurrencyFormat(String.format("%.2f", item.getTaxablevalue())));
				ssum.setTaxableAmt(Double.parseDouble(ttv));
			}
			if(isNotEmpty(item.getIgstamount())) {
				String igt = df2.format(item.getIgstamount());
				ssum.setsTotalIGSTAmt(getIndianCurrencyFormat(igt));
				ssum.setTotalIGSTAmt(Double.parseDouble(igt));
			}
			if(isNotEmpty(item.getCgstamount())) {
				String cgt = df2.format(item.getCgstamount());
				ssum.setsTotalCGSTAmt(getIndianCurrencyFormat(cgt));
				ssum.setTotalCGSTAmt(Double.parseDouble(cgt));
			}
			if(isNotEmpty(item.getSgstamount())) {
				String sgt = df2.format(item.getSgstamount());
				ssum.setsTotalSGSTAmt(getIndianCurrencyFormat(sgt));
				ssum.setTotalSGSTAmt(Double.parseDouble(sgt));
			}
			if(isNotEmpty(item.getTotal())) {
				String tot = df2.format(item.getTotal());
				ssum.setsTotalAmt(getIndianCurrencyFormat(tot));
				ssum.setTotalAmt(Double.parseDouble(tot));
			}
			invsummary.add(ssum);
		}
		invmap.put(invoice.getId().toString(), invsummary);
		return invmap;
	}

	public Map<String, String> getClientDetails(Client client) {
		Map<String,String> clientDetails = Maps.newHashMap();
    	if(isNotEmpty(client)) {
    		if(isNotEmpty(client.getBusinessname())) {
    			clientDetails.put("clientName", client.getBusinessname());
    		}
    		if(isNotEmpty(client.getEmail())) {
    			clientDetails.put("clientEmail", client.getEmail());
    		}else {
    			clientDetails.put("clientEmail", "");
    		}
    		if(isNotEmpty(client.getMobilenumber())) {
    			clientDetails.put("clientMobileNumber",client.getMobilenumber());
    		}else {
    			clientDetails.put("clientMobileNumber","");
    		}
    		if(isNotEmpty(client.getGstnnumber())) {
    			clientDetails.put("clientGstin",client.getGstnnumber());
    		}else {
    			clientDetails.put("clientGstin","");
    		}
    		if(isNotEmpty(client.getClientSignature())) {
    			String sign = client.getClientSignature();
    			if(sign.contains("\r\n")) {
    				sign = sign.replace("\r\n", "</br>");
    			}
    			clientDetails.put("clientSignature",sign);
    		}else {
    			clientDetails.put("clientSignature","");
    		}
    	}
		return clientDetails;
	}
	
	public String getIndianCurrencyFormat(String amount) {
		if(isNotEmpty(amount)) {
			String amt = amount;
			if(amount.contains(".")) {
				 amt = amt.substring(0, amt.indexOf("."));
			}
		    StringBuilder stringBuilder = new StringBuilder();
		    char amountArray[] = amt.toCharArray();
		    int a = 0, b = 0;
		    for (int i = amountArray.length - 1; i >= 0; i--) {
		        if (a < 3) {
		            stringBuilder.append(amountArray[i]);
		            a++;
		        } else if (b < 2) {
		            if (b == 0) {
		                stringBuilder.append(",");
		                stringBuilder.append(amountArray[i]);
		                b++;
		            } else {
		                stringBuilder.append(amountArray[i]);
		                b = 0;
		            }
		        }
		    }
		    if(amount.contains(".")) {
		    	String amt1 = amount.substring(amount.indexOf("."));
		    	//logger.info(amt1);
		    	//logger.info(stringBuilder.reverse().toString()+amt1);
		    	return stringBuilder.reverse().toString()+amt1;
		    }else {
		    	//logger.info(stringBuilder.reverse().toString());
		    	return stringBuilder.reverse().toString();
		    }
		}else {
			return "";
		}
	}
	public void unclaimInvoices(String invId, String returntype) {
		InvoiceParent invoice = clientService.getInvoice(invId, returntype);
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getMatchingId())) {
			InvoiceParent gstr2invoice = clientService.getInvoice(invoice.getMatchingId(), "GSTR2");
			List<Item> gstr2itms = Lists.newArrayList();
			if(isNotEmpty(gstr2invoice)) {
				if(gstr2invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
					if(isNotEmpty(gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc())) {
						gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setElg("no");
						if(isNotEmpty(gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getCsTax())) {
							gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setCsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getsTax())) {
							gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getcTax())) {
							gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setcTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getiTax())) {
							gstr2invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setiTax(0d);
						}
					}
				}else if(gstr2invoice.getInvtype().equals(MasterGSTConstants.B2B)) {
					if(isNotEmpty(gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc())) {
						gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setElg("no");
						if(isNotEmpty(gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getCsTax())) {
							gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setCsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getcTax())) {
							gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setcTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getsTax())) {
							gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getiTax())) {
							gstr2invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setiTax(0d);
						}
					}
				}else if(gstr2invoice.getInvtype().equals(MasterGSTConstants.B2BUR)) {
					if(isNotEmpty(((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc())) {
						((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setElg("no");
						if(isNotEmpty(((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getCsTax())) {
							((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setCsTax(0d);
						}
						if(isNotEmpty(((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getcTax())) {
							((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setcTax(0d);
						}
						if(isNotEmpty(((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getsTax())) {
							((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setsTax(0d);
						}
						if(isNotEmpty(((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getiTax())) {
							((GSTR2)gstr2invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setiTax(0d);
						}
					}
				}else if(gstr2invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
					if(isNotEmpty(gstr2invoice.getCdnur().get(0).getItms().get(0).getItc())) {
						gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().setElg("no");
						if(isNotEmpty(gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().getCsTax())) {
							gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().setCsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().getsTax())) {
							gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().setsTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().getcTax())) {
							gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().setcTax(0d);
						}
						if(isNotEmpty(gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().getiTax())) {
							gstr2invoice.getCdnur().get(0).getItms().get(0).getItc().setiTax(0d);
						}
					}
				}else if(gstr2invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
					if(isNotEmpty(((GSTR2)gstr2invoice).getImpGoods().get(0).getItms())) {
						((GSTR2)gstr2invoice).getImpGoods().get(0).getItms().get(0).setElg("no");
						if(isNotEmpty(((GSTR2)gstr2invoice).getImpGoods().get(0).getItms().get(0).getCsTax())) {
							((GSTR2)gstr2invoice).getImpGoods().get(0).getItms().get(0).setCsTax(0d);
						}
						if(isNotEmpty(((GSTR2)gstr2invoice).getImpGoods().get(0).getItms().get(0).getiTax())) {
							((GSTR2)gstr2invoice).getImpGoods().get(0).getItms().get(0).setiTax(0d);
						}
					}
					
				}else if(gstr2invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
					if(isNotEmpty(((GSTR2)gstr2invoice).getImpServices().get(0).getItms())) {
						((GSTR2)gstr2invoice).getImpServices().get(0).getItms().get(0).setElg("no");
						if(isNotEmpty(((GSTR2)gstr2invoice).getImpServices().get(0).getItms().get(0).getCsTax())) {
							((GSTR2)gstr2invoice).getImpServices().get(0).getItms().get(0).setCsTax(0d);
						}
						if(isNotEmpty(((GSTR2)gstr2invoice).getImpServices().get(0).getItms().get(0).getiTax())) {
							((GSTR2)gstr2invoice).getImpServices().get(0).getItms().get(0).setiTax(0d);
						}
					}
					
				}
			}
			if(isNotEmpty(gstr2invoice) && isNotEmpty(gstr2invoice.getItems())) {
				for(Item item : gstr2invoice.getItems()) {
					Item items = new Item();
					if(isNotEmpty(item.getItemno())) {
						items.setItemno(item.getItemno());
					}
					if(isNotEmpty(item.getCategory())) {
						items.setCategory(item.getCategory());
					}
					if(isNotEmpty(item.getHsn())) {
						items.setHsn(item.getHsn());
					}
					if(isNotEmpty(item.getUqc())) {
						items.setUqc(item.getUqc());
					}
					if(isNotEmpty(item.getQuantity())) {
						items.setQuantity(item.getQuantity());
					}
					if(isNotEmpty(item.getRateperitem())) {
						items.setRateperitem(item.getRateperitem());
					}
					if(isNotEmpty(item.getTotal())) {
						items.setTotal(item.getTotal());
					}
					if(isNotEmpty(item.getDiscount())) {
						items.setDiscount(item.getDiscount());
					}
					if(isNotEmpty(item.getTaxablevalue())) {
						items.setTaxablevalue(item.getTaxablevalue());
					}
					if(isNotEmpty(item.getRate())) {
						items.setRate(item.getRate());
					}
					if(isNotEmpty(item.getCgstamount())) {
						items.setCgstamount(item.getCgstamount());
					}
					if(isNotEmpty(item.getSgstamount())) {
						items.setSgstamount(item.getSgstamount());
					}
					if(isNotEmpty(item.getIgstamount())) {
						items.setIgstamount(item.getIgstamount());
					}
					if(isNotEmpty(item.getCessrate())) {
						items.setCessrate(item.getCessrate());
					}
					if(isNotEmpty(item.getCessamount())) {
						items.setCessamount(item.getCessamount());
					}
					gstr2itms.add(items);
				}
				gstr2invoice.setItems(gstr2itms);
				Calendar cal = Calendar.getInstance();
				gstr2invoice.setDateofitcCleared(cal.getTime());
				gstr2invoice.setTotalitc(0d);
				List<InvoiceParent> list = Lists.newArrayList();
				list.add(gstr2invoice);
				clientService.saveInvoices(list, "GSTR2");
			}
		}
		List<Item> itms = Lists.newArrayList();
		if(isNotEmpty(invoice)) {
			if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
				if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc())) {
					invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setElg("no");
					if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getCsTax())) {
						invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setCsTax(0d);
					}
					if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getsTax())) {
						invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setsTax(0d);
					}
					if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getcTax())) {
						invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setcTax(0d);
					}
					if(isNotEmpty(invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().getiTax())) {
						invoice.getCdn().get(0).getNt().get(0).getItms().get(0).getItc().setiTax(0d);
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2B)) {
				if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc())) {
					invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setElg("no");
					if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getCsTax())) {
						invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setCsTax(0d);
					}
					if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getcTax())) {
						invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setcTax(0d);
					}
					if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getsTax())) {
						invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setsTax(0d);
					}
					if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().getiTax())) {
						invoice.getB2b().get(0).getInv().get(0).getItms().get(0).getItc().setiTax(0d);
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2BUR)) {
				if(isNotEmpty(((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc())) {
					((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setElg("no");
					if(isNotEmpty(((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getCsTax())) {
						((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setCsTax(0d);
					}
					if(isNotEmpty(((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getcTax())) {
						((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setcTax(0d);
					}
					if(isNotEmpty(((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getsTax())) {
						((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setsTax(0d);
					}
					if(isNotEmpty(((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().getiTax())) {
						((PurchaseRegister)invoice).getB2bur().get(0).getInv().get(0).getItms().get(0).getItc().setiTax(0d);
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)) {
				if(isNotEmpty(invoice.getCdnur().get(0).getItms().get(0).getItc())) {
					invoice.getCdnur().get(0).getItms().get(0).getItc().setElg("no");
					if(isNotEmpty(invoice.getCdnur().get(0).getItms().get(0).getItc().getCsTax())) {
						invoice.getCdnur().get(0).getItms().get(0).getItc().setCsTax(0d);
					}
					if(isNotEmpty(invoice.getCdnur().get(0).getItms().get(0).getItc().getsTax())) {
						invoice.getCdnur().get(0).getItms().get(0).getItc().setsTax(0d);
					}
					if(isNotEmpty(invoice.getCdnur().get(0).getItms().get(0).getItc().getcTax())) {
						invoice.getCdnur().get(0).getItms().get(0).getItc().setcTax(0d);
					}
					if(isNotEmpty(invoice.getCdnur().get(0).getItms().get(0).getItc().getiTax())) {
						invoice.getCdnur().get(0).getItms().get(0).getItc().setiTax(0d);
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_GOODS)) {
				if(isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getItms())) {
					((PurchaseRegister)invoice).getImpGoods().get(0).getItms().get(0).setElg("no");
					if(isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getItms().get(0).getCsTax())) {
						((PurchaseRegister)invoice).getImpGoods().get(0).getItms().get(0).setCsTax(0d);
					}
					if(isNotEmpty(((PurchaseRegister)invoice).getImpGoods().get(0).getItms().get(0).getiTax())) {
						((PurchaseRegister)invoice).getImpGoods().get(0).getItms().get(0).setiTax(0d);
					}
				}
				
			}else if(invoice.getInvtype().equals(MasterGSTConstants.IMP_SERVICES)) {
				if(isNotEmpty(((PurchaseRegister)invoice).getImpServices().get(0).getItms())) {
					((PurchaseRegister)invoice).getImpServices().get(0).getItms().get(0).setElg("no");
					if(isNotEmpty(((PurchaseRegister)invoice).getImpServices().get(0).getItms().get(0).getCsTax())) {
						((PurchaseRegister)invoice).getImpServices().get(0).getItms().get(0).setCsTax(0d);
					}
					if(isNotEmpty(((PurchaseRegister)invoice).getImpServices().get(0).getItms().get(0).getiTax())) {
						((PurchaseRegister)invoice).getImpServices().get(0).getItms().get(0).setiTax(0d);
					}
				}
				
			}
		}
		
		if(isNotEmpty(invoice) && isNotEmpty(invoice.getItems())) {
			
			for(Item item : invoice.getItems()) {
				Item items = new Item();
				if(isNotEmpty(item.getItemno())) {
					items.setItemno(item.getItemno());
				}
				if(isNotEmpty(item.getCategory())) {
					items.setCategory(item.getCategory());
				}
				if(isNotEmpty(item.getHsn())) {
					items.setHsn(item.getHsn());
				}
				if(isNotEmpty(item.getUqc())) {
					items.setUqc(item.getUqc());
				}
				if(isNotEmpty(item.getQuantity())) {
					items.setQuantity(item.getQuantity());
				}
				if(isNotEmpty(item.getRateperitem())) {
					items.setRateperitem(item.getRateperitem());
				}
				if(isNotEmpty(item.getTotal())) {
					items.setTotal(item.getTotal());
				}
				if(isNotEmpty(item.getDiscount())) {
					items.setDiscount(item.getDiscount());
				}
				if(isNotEmpty(item.getTaxablevalue())) {
					items.setTaxablevalue(item.getTaxablevalue());
				}
				if(isNotEmpty(item.getRate())) {
					items.setRate(item.getRate());
				}
				if(isNotEmpty(item.getCgstamount())) {
					items.setCgstamount(item.getCgstamount());
				}
				if(isNotEmpty(item.getSgstamount())) {
					items.setSgstamount(item.getSgstamount());
				}
				if(isNotEmpty(item.getIgstamount())) {
					items.setIgstamount(item.getIgstamount());
				}
				if(isNotEmpty(item.getCessrate())) {
					items.setCessrate(item.getCessrate());
				}
				if(isNotEmpty(item.getCessamount())) {
					items.setCessamount(item.getCessamount());
				}
				itms.add(items);
			}
			invoice.setItems(itms);
			Calendar cal = Calendar.getInstance();
			invoice.setDateofitcCleared(cal.getTime());
			invoice.setTotalitc(0d);
			List<InvoiceParent> list = Lists.newArrayList();
			list.add(invoice);
			clientService.saveInvoices(list, returntype);
		}
	}
	
	public void saveDefaultGroupDetails(String clientid) {
		List<GroupDetails> groups = Lists.newArrayList();
		GroupDetails grpdetails = new GroupDetails();
		grpdetails.setClientid(clientid);
		grpdetails.setGroupname("Expenditure");
		grpdetails.setHeadname("Expenditure");
		grpdetails.setPath("Expenditure/Expenditure");
		groups.add(grpdetails);
		
		GroupDetails bnkCharges = new GroupDetails();
		bnkCharges.setClientid(clientid);
		bnkCharges.setGroupname("Bank Fee and Charges");
		bnkCharges.setHeadname("Expenditure");
		bnkCharges.setPath("Expenditure/Bank Fee and Charges");
		groups.add(bnkCharges);
		
		GroupDetails empsal = new GroupDetails();
		empsal.setClientid(clientid);
		empsal.setGroupname("Employee Salaries & Advances");
		empsal.setHeadname("Expenditure");
		empsal.setPath("Expenditure/Employee Salaries & Advances");
		groups.add(empsal);
		
		GroupDetails printStationary = new GroupDetails();
		printStationary.setClientid(clientid);
		printStationary.setGroupname("Printing and Stationery");
		printStationary.setHeadname("Expenditure");
		printStationary.setPath("Expenditure/Printing and Stationery");
		groups.add(printStationary);
		
		GroupDetails rawMaterial = new GroupDetails();
		rawMaterial.setClientid(clientid);
		rawMaterial.setGroupname("Raw Material");
		rawMaterial.setHeadname("Expenditure");
		rawMaterial.setPath("Expenditure/Raw Material");
		groups.add(rawMaterial);
		
		GroupDetails rentExpense = new GroupDetails();
		rentExpense.setClientid(clientid);
		rentExpense.setGroupname("Rent Expense");
		rentExpense.setHeadname("Expenditure");
		rentExpense.setPath("Expenditure/Rent Expense");
		groups.add(rentExpense);
		
		GroupDetails repair = new GroupDetails();
		repair.setClientid(clientid);
		repair.setGroupname("Repair & Maintenance");
		repair.setHeadname("Expenditure");
		repair.setPath("Expenditure/Repair & Maintenance");
		groups.add(repair);
		
		GroupDetails teleandinternet = new GroupDetails();
		teleandinternet.setClientid(clientid);
		teleandinternet.setGroupname("Telephone & Internet Expense");
		teleandinternet.setHeadname("Expenditure");
		teleandinternet.setPath("Expenditure/Telephone & Internet Expense");
		groups.add(teleandinternet);
		
		GroupDetails transport = new GroupDetails();
		transport.setClientid(clientid);
		transport.setGroupname("Transportation & Travel Expense");
		transport.setHeadname("Expenditure");
		transport.setPath("Expenditure/Transportation & Travel Expense");
		groups.add(transport);
		
		groupDetailsRepository.save(groups);
		
	}
	public void populateAmendmentDetails(InvoiceParent invoice, InvoiceParent oldinvoice) {
		String oldNum="";
		String oldDate="";
		
		if(isNotEmpty(oldinvoice)) {
			//if old-invoice is not empty means b2b invoice found. invoices is amendment.  
			invoice.setId(null);
		}
		
		if(isEmpty(oldinvoice)) {
			//if old-invoice is not found, to check by invoice object id
			oldinvoice = gstr1Repository.findOne(invoice.getId().toString());
			if(isNotEmpty(oldinvoice)) {
				if(isNotEmpty(oldinvoice.getAmendmentRefId())) {
					
					oldinvoice = gstr1Repository.findByIdIn(oldinvoice.getAmendmentRefId());
				}
			}else {
				oldinvoice = null;
			}
		}
		if(isNotEmpty(oldinvoice)) {
			invoice.setAmendmentRefId(Arrays.asList(oldinvoice.getId().toString()));							
		}
		
			if(invoice.getInvtype().equals(MasterGSTConstants.B2BA)) {
				if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
					if(isEmpty(((GSTR1) invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
						invoice.setGstStatus("");
						
						if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
							((GSTR1) invoice).getB2ba().get(0).setCtin(invoice.getB2b().get(0).getCtin());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv())) {
							if(isNotEmpty(oldinvoice)) {
								oldNum=oldinvoice.getB2b().get(0).getInv().get(0).getInum();
								oldDate=oldinvoice.getB2b().get(0).getInv().get(0).getIdt();
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOinum(oldNum);
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOidt(oldDate);
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getIdt())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setIdt(invoice.getB2b().get(0).getInv().get(0).getIdt());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getVal())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setVal(invoice.getB2b().get(0).getInv().get(0).getVal());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setPos(invoice.getB2b().get(0).getInv().get(0).getPos());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getRchrg())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setRchrg(invoice.getB2b().get(0).getInv().get(0).getRchrg());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
							}
							if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getDiffPercent())) {
								((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setDiffPercent(invoice.getB2b().get(0).getInv().get(0).getDiffPercent());
							}
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setItms(invoice.getB2b().get(0).getInv().get(0).getItms());
						}
						//BeanUtils.copyProperties(b2baList, invoice.getB2b());
						
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2CLA)) {
				if(isNotEmpty(invoice.getB2cl()) && isNotEmpty(invoice.getB2cl().get(0).getInv())) {
					//if(isEmpty(((GSTR1) invoice).getB2cla()) && isEmpty(((GSTR1) invoice).getB2cla().get(0).getInv()) && isEmpty(((GSTR1) invoice).getB2cla().get(0).getInv().get(0).getOinum())) {
						//invoice.setGstStatus("");
						((GSTR1) invoice).setB2cla(invoice.getB2cl());
						if(isNotEmpty(oldinvoice)) {
							((GSTR1) invoice).getB2cla().get(0).getInv().get(0).setOinum(oldinvoice.getB2cl().get(0).getInv().get(0).getInum());
							((GSTR1) invoice).getB2cla().get(0).getInv().get(0).setOidt(oldinvoice.getB2cl().get(0).getInv().get(0).getIdt());
						}
					//}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				if(isNotEmpty(((GSTR1)invoice).getB2csa()) && isNotEmpty(((GSTR1) invoice).getB2csa().get(0))) {
					if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getFp())) {
						((GSTR1) invoice).getB2csa().get(0).setOmon(oldinvoice.getFp());
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
				if(isNotEmpty(((GSTR1) invoice).getAt())) {
					//if(isEmpty(((GSTR1) invoice).getAt().get(0).getOmon())) {
						//invoice.setGstStatus("");
						((GSTR1) invoice).setAta(((GSTR1) invoice).getAt());
						if(isNotEmpty(oldinvoice.getFp())) {
							((GSTR1) invoice).getAta().get(0).setOmon(oldinvoice.getFp());
						}
					//}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				if(isNotEmpty(invoice.getTxpd())) {
						invoice.setGstStatus("");
						
						invoice.setTxpda(invoice.getTxpd());
						if(isNotEmpty(oldinvoice.getFp())) {
							invoice.getTxpda().get(0).setOmon(oldinvoice.getFp());
						}
						if(isNotEmpty(invoice.getTxpda()) && isNotEmpty(invoice.getTxpda().get(0))) {
							if(isEmpty(invoice.getTxpda().get(0).getPos())) {
								List<Item> items = invoice.getItems();
								String pos = null;
								if(isNotEmpty(items)) {
									for(Item item : items) {
										if(isNotEmpty(item.getAdvStateName())) {
											pos = item.getAdvStateName();
											break;
										}
									}
								}
								invoice.getTxpda().get(0).setPos(pos);
							}
							
						}
				  }
			}else if(invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
				if(isNotEmpty(invoice.getExp())) {
					invoice.setGstStatus("");
					
					((GSTR1) invoice).setExpa(invoice.getExp());
					if(isNotEmpty(oldinvoice)) {
						if(isNotEmpty(oldinvoice.getExp()) && isNotEmpty(oldinvoice.getExp().get(0).getInv())) {
							((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOinum(oldinvoice.getExp().get(0).getInv().get(0).getInum());
							((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOidt(oldinvoice.getExp().get(0).getInv().get(0).getIdt());
						}
					}
			  }
		}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
			if(isNotEmpty(invoice.getCdnur())) {
				invoice.setGstStatus("");
				
				((GSTR1) invoice).setCdnura(invoice.getCdnur());
				if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getCdnur())) {
					((GSTR1) invoice).getCdnura().get(0).setOntNum(oldinvoice.getCdnur().get(0).getNtNum());
					((GSTR1) invoice).getCdnura().get(0).setOntDt(oldinvoice.getCdnur().get(0).getNtDt());
				}
		  }
	   }else if(invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {
			if(isNotEmpty(((GSTR1) invoice).getCdnr())) {
				invoice.setGstStatus("");
				
				((GSTR1) invoice).setCdnra(((GSTR1) invoice).getCdnr());
				if(isNotEmpty(oldinvoice) && isNotEmpty(((GSTR1) oldinvoice).getCdnr())) {
					((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntNum(((GSTR1) oldinvoice).getCdnr().get(0).getNt().get(0).getNtNum());
					((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntDt(((GSTR1) oldinvoice).getCdnr().get(0).getNt().get(0).getNtDt());
				}
		  }
	   }
	}
	
	public void populateImportAmendmentDetails(InvoiceParent invoice, String oinum, String fp, String invtype) {
		String oldNum="";
		String oldDate="";
		
		InvoiceParent oldinvoice = null; 
		if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA) || invoice.getInvtype().equals(MasterGSTConstants.ATA) || invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
			String pos = null, suplytyp = null;
			if(isNotEmpty(invoice.getB2cs()) && isNotEmpty(invoice.getB2cs().get(0))) {
				if(isNotEmpty(invoice.getB2cs().get(0).getPos())) {
					pos = invoice.getB2cs().get(0).getPos();
				}
				if(isNotEmpty(invoice.getB2cs().get(0).getSplyTy())) {
					suplytyp = invoice.getB2cs().get(0).getSplyTy();
				}
			}
			List<GSTR1> inv = null; 
			if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				inv = gstr1Repository.findByClientidAndFpAndB2cs_PosAndB2cs_splyTyAndInvtype(invoice.getClientid(), fp, pos, suplytyp,MasterGSTConstants.B2C);
			}
			if(invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				inv = gstr1Repository.findByClientidAndFpAndTxpd_PosAndTxpd_splyTyAndInvtype(invoice.getClientid(), fp, pos, suplytyp,MasterGSTConstants.ATPAID);
			}
			if(invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
				inv = gstr1Repository.findByClientidAndFpAndAt_PosAndAt_splyTyAndInvtype(invoice.getClientid(), fp, pos, suplytyp,MasterGSTConstants.ADVANCES);
			}
			if(isNotEmpty(inv)) {
				List<String> refids = Lists.newArrayList();
				inv.stream().forEach(invv -> refids.add(invv.getId().toString()));
				if(isNotEmpty(refids)) {
					invoice.setAmendmentRefId(refids);
				}
			}
		}else {
			fp = fp.replaceAll("-", "/");
			oldinvoice = gstr1Dao.findByInvoicenoAndClientidAndInvtypeAndDateofinvoice_str(oinum, invoice.getClientid(), invtype, fp);
			//oldinvoice = gstr1Repository.findByClientidAndInvtypeAndInvoicenoAndFp(invoice.getClientid(), oinum, fp, invtype);
		}
			
		if(isNotEmpty(oldinvoice)) {
			invoice.setAmendmentRefId(Arrays.asList(oldinvoice.getId().toString()));							
		}
		
		if(invoice.getInvtype().equals(MasterGSTConstants.B2BA)) {
			invoice.setExp(Lists.newArrayList());
			invoice.setTxpd(Lists.newArrayList());
			invoice.setCdn(Lists.newArrayList());
			invoice.setCdnur(Lists.newArrayList());
			invoice.setNil(null);
			if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0).getInv())) {
				if(isEmpty(((GSTR1) invoice).getB2ba().get(0).getInv().get(0).getOinum())) {
					invoice.setGstStatus("");
					if(isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						((GSTR1) invoice).getB2ba().get(0).setCtin(invoice.getB2b().get(0).getCtin());
					}
					if(isNotEmpty(invoice.getB2b().get(0).getInv())) {
						if(isNotEmpty(oldinvoice)) {
							oldNum=oldinvoice.getB2b().get(0).getInv().get(0).getInum();
							oldDate=oldinvoice.getB2b().get(0).getInv().get(0).getIdt();
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOinum(oldNum);
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOidt(oldDate);
						}else {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOinum(oinum);
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOidt(invoice.getStrOdate());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInum())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setInum(invoice.getB2b().get(0).getInv().get(0).getInum());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getIdt())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setIdt(invoice.getB2b().get(0).getInv().get(0).getIdt());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getVal())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setVal(invoice.getB2b().get(0).getInv().get(0).getVal());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getPos())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setPos(invoice.getB2b().get(0).getInv().get(0).getPos());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getRchrg())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setRchrg(invoice.getB2b().get(0).getInv().get(0).getRchrg());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getEtin())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setEtin(invoice.getB2b().get(0).getInv().get(0).getEtin());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setInvTyp(invoice.getB2b().get(0).getInv().get(0).getInvTyp());
						}
						if(isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getDiffPercent())) {
							((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setDiffPercent(invoice.getB2b().get(0).getInv().get(0).getDiffPercent());
						}
						((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setItms(invoice.getB2b().get(0).getInv().get(0).getItms());
					}
					//BeanUtils.copyProperties(b2baList, invoice.getB2b());
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2CLA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if(isNotEmpty(invoice.getB2cl()) && isNotEmpty(invoice.getB2cl().get(0).getInv())) {
					((GSTR1) invoice).setB2cla(invoice.getB2cl());
					if(isNotEmpty(oldinvoice)) {
						((GSTR1) invoice).getB2cla().get(0).getInv().get(0).setOinum(oldinvoice.getB2cl().get(0).getInv().get(0).getInum());
						((GSTR1) invoice).getB2cla().get(0).getInv().get(0).setOidt(oldinvoice.getB2cl().get(0).getInv().get(0).getIdt());
					}else {
						((GSTR1) invoice).getB2ba().get(0).getInv().get(0).setOinum(oinum);
						if(isNotEmpty(invoice.getStrOdate())) {
							((GSTR1) invoice).getB2cla().get(0).getInv().get(0).setOidt(invoice.getStrOdate());
						}
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.B2CSA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if(isNotEmpty(oldinvoice)) {
					if(isNotEmpty(((GSTR1)invoice).getB2csa()) && isNotEmpty(((GSTR1) invoice).getB2csa().get(0))) {
						if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getFp())) {
							((GSTR1) invoice).getB2csa().get(0).setOmon(oldinvoice.getFp());
						}
					}					
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.ATA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if(isNotEmpty(((GSTR1) invoice).getAt())) {
					((GSTR1) invoice).setAta(((GSTR1) invoice).getAt());
					if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getFp())) {
						((GSTR1) invoice).getAta().get(0).setOmon(oldinvoice.getFp());
					}
				}
			}else if(invoice.getInvtype().equals(MasterGSTConstants.TXPA)) {
				invoice.setExp(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if(isNotEmpty(invoice.getTxpd())) {
						invoice.setGstStatus("");
						
						invoice.setTxpda(invoice.getTxpd());
						if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getFp())) {
							invoice.getTxpda().get(0).setOmon(oldinvoice.getFp());
						}
						if(isNotEmpty(invoice.getTxpda()) && isNotEmpty(invoice.getTxpda().get(0))) {
							if(isEmpty(invoice.getTxpda().get(0).getPos())) {
								List<Item> items = invoice.getItems();
								String pos = null;
								if(isNotEmpty(items)) {
									for(Item item : items) {
										if(isNotEmpty(item.getAdvStateName())) {
											pos = item.getAdvStateName();
											break;
										}
									}
								}
								invoice.getTxpda().get(0).setPos(pos);
							}
						}
				  }
			}else if(invoice.getInvtype().equals(MasterGSTConstants.EXPA)) {
				invoice.setTxpd(Lists.newArrayList());
				invoice.setCdn(Lists.newArrayList());
				invoice.setCdnur(Lists.newArrayList());
				invoice.setNil(null);
				if(isNotEmpty(invoice.getExp())) {
					invoice.setGstStatus("");
					
					((GSTR1) invoice).setExpa(invoice.getExp());
					if(isNotEmpty(oldinvoice)) {
						if(isNotEmpty(oldinvoice.getExp()) && isNotEmpty(oldinvoice.getExp().get(0).getInv())) {
							((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOinum(oldinvoice.getExp().get(0).getInv().get(0).getInum());
							((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOidt(oldinvoice.getExp().get(0).getInv().get(0).getIdt());
						}
					}else {
						if(isNotEmpty(invoice.getStrOdate())) {
							((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOinum(oinum);
							try {
								((GSTR1) invoice).getExpa().get(0).getInv().get(0).setOidt(dateFormat.parse(invoice.getStrOdate()));
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
			  }
		}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNURA)) {
			invoice.setExp(Lists.newArrayList());
			invoice.setTxpd(Lists.newArrayList());
			invoice.setCdn(Lists.newArrayList());
			invoice.setNil(null);
			if(isNotEmpty(invoice.getCdnur())) {
				invoice.setGstStatus("");
				
				((GSTR1) invoice).setCdnura(invoice.getCdnur());
				if(isNotEmpty(oldinvoice) && isNotEmpty(oldinvoice.getCdnur())) {
					((GSTR1) invoice).getCdnura().get(0).setOntNum(oldinvoice.getCdnur().get(0).getNtNum());
					((GSTR1) invoice).getCdnura().get(0).setOntDt(oldinvoice.getCdnur().get(0).getNtDt());
				}else {
					((GSTR1) invoice).getCdnura().get(0).setOntNum(oinum);
					if(isNotEmpty(invoice.getStrOdate())) {
						try {
							((GSTR1) invoice).getCdnura().get(0).setOntDt(dateFormat.parse(invoice.getStrOdate()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
		  }
	   }else if(invoice.getInvtype().equals(MasterGSTConstants.CDNA)) {
		   invoice.setExp(Lists.newArrayList());
			invoice.setTxpd(Lists.newArrayList());
			invoice.setCdnur(Lists.newArrayList());
			invoice.setNil(null);
			if(isNotEmpty(((GSTR1) invoice).getCdnr())) {
				invoice.setGstStatus("");
				
				((GSTR1) invoice).setCdnra(((GSTR1) invoice).getCdnr());
				if(isNotEmpty(oldinvoice) && isNotEmpty(((GSTR1) oldinvoice).getCdnr())) {
					((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntNum(((GSTR1) oldinvoice).getCdnr().get(0).getNt().get(0).getNtNum());
					((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntDt(((GSTR1) oldinvoice).getCdnr().get(0).getNt().get(0).getNtDt());
				}else {
					if(isNotEmpty(oinum)) {
						((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntNum(oinum);						
					}
					if(isNotEmpty(invoice.getStrOdate())) {
						try {
							((GSTR1) invoice).getCdnra().get(0).getNt().get(0).setOntDt(dateFormat.parse(invoice.getStrOdate()));
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
		  }
	   }
	}
	
	public void consolidatedB2csaItemRate(GSTRB2CSA invoiceDetails) {
		if(isNotEmpty(invoiceDetails) && isNotEmpty(invoiceDetails.getItms())) {
			Map<Double, GSTRItemDetails> itemMap=Maps.newHashMap();
			for(GSTRItemDetails item : invoiceDetails.getItms()) {
				if(isNotEmpty(item.getRt())) {
					if(itemMap.keySet().contains(item.getRt())) {
						if(isNotEmpty(item.getTxval())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getTxval())) {
								itemMap.get(item.getRt()).setTxval(item.getTxval()
										+ itemMap.get(item.getRt()).getTxval());
							} else {
								itemMap.get(item.getRt()).setTxval(item.getTxval());
							}
						}
						if(isNotEmpty(item.getIamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getIamt())) {
								itemMap.get(item.getRt()).setIamt(item.getIamt()
										+ itemMap.get(item.getRt()).getIamt());
							} else {
								itemMap.get(item.getRt()).setIamt(item.getIamt());
							}
						}
						if(isNotEmpty(item.getCamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCamt())) {
								itemMap.get(item.getRt()).setCamt(item.getCamt()
										+ itemMap.get(item.getRt()).getCamt());
							} else {
								itemMap.get(item.getRt()).setCamt(item.getCamt());
							}
						}
						if(isNotEmpty(item.getSamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getSamt())) {
								itemMap.get(item.getRt()).setSamt(item.getSamt()
										+ itemMap.get(item.getRt()).getSamt());
							} else {
								itemMap.get(item.getRt()).setSamt(item.getSamt());
							}
						}
						if(isNotEmpty(item.getCsamt())) {
							if(isNotEmpty(itemMap.get(item.getRt()).getCsamt())) {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt()
										+ itemMap.get(item.getRt()).getCsamt());
							} else {
								itemMap.get(item.getRt()).setCsamt(item.getCsamt());
							}
						}
					} else {
						itemMap.put(item.getRt(), item);
					}
				}
			}
			List<GSTRItemDetails> gstrItems = Lists.newArrayList();
			int index = 1;
			for(GSTRItemDetails item : itemMap.values()) {
				//item.setNum(index++);
				gstrItems.add(item);
			}
			invoiceDetails.setItms(gstrItems);
		}
	}
}
