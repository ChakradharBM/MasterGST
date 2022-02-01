package com.mastergst.usermanagement.runtime.service;

import static com.mastergst.core.util.NullUtil.isEmpty;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Currency;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.Currencycodes;
import com.mastergst.configuration.service.CurrencycodesRepository;
import com.mastergst.configuration.service.EmailService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.common.MasterGSTConstants;
import com.mastergst.core.util.MoneyConverterUtil;
import com.mastergst.core.util.NullUtil;
import com.mastergst.core.util.SendMsgsSummary;
import com.mastergst.core.util.VmUtil;
import com.mastergst.login.runtime.domain.User;
import com.mastergst.login.runtime.service.UserService;
import com.mastergst.usermanagement.runtime.domain.Branch;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyBankDetails;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CustomData;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.DeliveryChallan;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.GSTR1;
import com.mastergst.usermanagement.runtime.domain.GSTR2;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.Item;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.domain.Reminders;
import com.mastergst.usermanagement.runtime.repository.CompanyBankDetailsRepository;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mongodb.gridfs.GridFSDBFile;

import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Service
@Transactional(readOnly = true)
public class PrintServiceImpl implements PrintService{
	private static DecimalFormat df2 = new DecimalFormat("#.##");
	@Autowired
	private ProfileService profileService;
	@Autowired
	GridFsOperations gridOperations;
	@Autowired 
	private CompanyCustomersRepository companyCustomersRepository;
	@Autowired
	private CurrencycodesRepository currencycodesRepository;
	@Autowired
	private	CompanyBankDetailsRepository companyBankDetailsRepository;
	@Autowired
	private ConfigService configService;
	@Autowired
	CustomFieldsRepository customFieldsRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ClientService clientService;
	@Autowired
    private ResourceLoader resourceLoader;
	@Autowired
	private PrintService printService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private ClientUtils clientUtils;
	//HttpServletResponse response
	public byte[] createQRImage(InvoiceParent invoice,String returntype) throws WriterException, IOException {

		String qrCodeText = "This is Your Sample E-invoice";
		int size = 500;
		String fileType = "png";
		//System.out.println("DONE");
	
		//ServletOutputStream out = response.getOutputStream();
		if(returntype.equalsIgnoreCase("EWAYBILL")) {
			if(NullUtil.isNotEmpty(invoice.getEwayBillNumber())) {
				qrCodeText = invoice.getEwayBillNumber();
			}
		}else {
			if(NullUtil.isNotEmpty(invoice.getIrnNo())) {
				qrCodeText = invoice.getIrnNo();
			}
		}
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
		// Make the BufferedImage that are to hold the QRCode
		int matrixWidth = byteMatrix.getWidth();
		BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
		image.createGraphics();
	
		Graphics2D graphics = (Graphics2D) image.getGraphics();
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, matrixWidth, matrixWidth);
		// Paint and save the image using the ByteMatrix
		graphics.setColor(Color.BLACK);
	
		for (int i = 0; i < matrixWidth; i++) {
			for (int j = 0; j < matrixWidth; j++) {
				if (byteMatrix.get(i, j)) {
					graphics.fillRect(i, j, 1, 1);
				}
			}
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, fileType, bos );
		byte [] byteArry = bos.toByteArray();
		return byteArry;
		
	}
	 public static byte[] generateQRCodeImage(String barcodeText) throws Exception {
			QRCodeWriter qrCodeWriter = new QRCodeWriter();
			BitMatrix byteMatrix = qrCodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 700, 700);
			BufferedImage image = MatrixToImageWriter.toBufferedImage(byteMatrix);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", bos );
			byte [] byteArry = bos.toByteArray();
			return byteArry;
	 }
	public Map<String, Object> getReportParams(Client client, InvoiceParent invoice,String returntype) throws Exception {
		boolean isIGST = false;
		boolean isCGST = false;
		double totalQuantity = 0d;
		if(NullUtil.isNotEmpty(invoice.getItems())) {
			for(Item item : invoice.getItems()) {
				if(item.getItemno() == null){
					item.setItemno("");
				}
				if(item.getQuantity() == null) {
					item.setQuantity(0d);
				}else {
					totalQuantity += item.getQuantity();
				}
				if(item.getDiscount() == null) {
					item.setDiscount(0d);
				}
				if(item.getIgstrate() == null) {
					item.setIgstrate(0d);
				}
				if(item.getIgstamount() == null) {
					item.setIgstamount(0d);
				} else if(item.getIgstamount() > 0) {
					isIGST = true;
				}
				if(item.getCgstrate() == null) {
					item.setCgstrate(0d);
				}
				if(item.getCgstamount() == null) {
					item.setCgstamount(0d);
				} else if(item.getCgstamount() > 0) {
					isCGST = true;
				}
				if(item.getSgstrate() == null) {
					item.setSgstrate(0d);
				}
				if(item.getSgstamount() == null) {
					item.setSgstamount(0d);
				}
				if(item.getRateperitem() == null) {
					item.setRateperitem(0d);
				}
				if(item.getTotal() == null) {
					item.setTotal(0d);
				}
				if(item.getTaxablevalue() == null) {
					item.setTaxablevalue(0d);
				}
				if(item.getIsdcessamount() == null) {
					item.setIsdcessamount(0d);
				}
				if(NullUtil.isNotEmpty(item.getHsn())){
					if(item.getHsn().contains(":")){
						String hsn[] = item.getHsn().split(":");
						item.setHsn(hsn[0]);
					}
				}else{
					item.setHsn("");
				}
			}
		}
		
		PrintConfiguration pconfig = profileService.getPrintConfig(client.getId().toString());
		Map<String, Object> params = Maps.newHashMap();
		params.put("totalQuantity", totalQuantity);
		params.put("isIGST", isIGST);
		params.put("isCGST", isCGST);
		if(NullUtil.isNotEmpty(invoice.getRevchargetype()) && "Reverse".equals(invoice.getRevchargetype())){
			params.put("reverseCharge", "**Tax to be paid on Reverse Charge");
		}else{
			params.put("reverseCharge", null);
		}
		if(NullUtil.isNotEmpty(pconfig) && NullUtil.isNotEmpty(pconfig.isIsfooternotescheck())) {
			if(NullUtil.isNotEmpty(pconfig.getFooternotes())) {
				params.put("footernotes", pconfig.getFooternotes());
			}else {
				params.put("footernotes", "");
			}
		}
		
		if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			params.put("refNoText", "Reference:");
			String invText="";
			String custAddress = "";
			if(NullUtil.isNotEmpty(invoice)) {
				//CompanyCustomers customers = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), client.getId().toString());
				if(!returntype.equalsIgnoreCase(MasterGSTConstants.EINVOICE)) {
					CompanyCustomers customers = null;
					if(isNotEmpty(invoice.getB2b()) && isNotEmpty(invoice.getB2b().get(0)) && isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						String gstnnumber =invoice.getB2b().get(0).getCtin().trim();
						customers = companyCustomersRepository.findByGstnnumberAndClientid(gstnnumber, client.getId().toString());
					}else {
						customers = companyCustomersRepository.findByNameAndClientid(invoice.getBilledtoname(), client.getId().toString());
					}
					if(isNotEmpty(customers)) {
						
						if(isNotEmpty(customers.getAddress())) {
							custAddress = customers.getAddress();
						}
						if(isNotEmpty(customers.getPincode())) {
							if(isNotEmpty(custAddress)) {
								custAddress = custAddress+','+ customers.getPincode();
							}else {
								custAddress = customers.getPincode();
							}
						}
					}
					if(isNotEmpty(invoice.getStatename())) {
						if(isNotEmpty(custAddress)) {
							custAddress = custAddress+','+ invoice.getStatename().substring(3);
						}else {
							custAddress = invoice.getStatename().substring(3);
						}
						
					}
				}else {
					if(isNotEmpty(invoice.getBuyerDtls())) {
						
						if(isNotEmpty(invoice.getBuyerDtls().getAddr1())) {
							custAddress = invoice.getBuyerDtls().getAddr1();
						}
						if(isNotEmpty(invoice.getEntertaimentprintto())) {
							if(isNotEmpty(invoice.getBuyerDtls().getAddr2())) {
								params.put("theatreName", invoice.getBuyerDtls().getAddr2());
							}else{
								params.put("theatreName", "");
							}
						}else {
							if(isNotEmpty(invoice.getBuyerDtls().getAddr2())) {
								custAddress = custAddress+','+ invoice.getBuyerDtls().getAddr2();
							}
						}
						if(isNotEmpty(invoice.getBuyerDtls().getLoc())) {
							custAddress = custAddress+','+ invoice.getBuyerDtls().getLoc();
						}
					}
					if(isNotEmpty(invoice.getStatename())) {
						if(isNotEmpty(custAddress)) {
							if(invoice.getStatename().contains("-")) {
								custAddress = custAddress+','+ invoice.getStatename().substring(3);
							}else {
								custAddress = custAddress+','+ invoice.getStatename();
							}
						}else {
							if(invoice.getStatename().contains("-")) {
								custAddress = invoice.getStatename().substring(3);
							}else {
								custAddress = invoice.getStatename();
							}
						}
					}
					if(isNotEmpty(invoice.getBuyerDtls())) {
						if(isNotEmpty(invoice.getBuyerDtls().getPin()) && invoice.getBuyerDtls().getPin()>0) {
							if(isNotEmpty(custAddress)) {
								custAddress = custAddress+'-'+ invoice.getBuyerDtls().getPin();
							}else {
								custAddress = invoice.getBuyerDtls().getPin()+"";
							}
						}
					}
				}
				params.put("custAddress", custAddress);
					if(NullUtil.isNotEmpty(invoice.getTyp())) {
						if(("INV").equalsIgnoreCase(invoice.getTyp())) {
							invText =  "TAX INVOICE";
						}else if(("CRN").equalsIgnoreCase(invoice.getTyp())) {
							invText =  "CREDIT NOTE";
						}else if(("DBN").equalsIgnoreCase(invoice.getTyp())) {
							invText =  "DEBIT NOTE";
						}else {
							invText =  "TAX INVOICE";
						}
						
					}else {
						if(isNotEmpty(invoice.getInvtype()) && "Exports".equalsIgnoreCase(invoice.getInvtype())) {
							invText =  "EXPORT INVOICE";
						}else {
							invText =  "TAX INVOICE";
						}
					}
					params.put("einvText", invText);
					
					if(NullUtil.isNotEmpty(invoice.getIrnNo())) {
						params.put("irnNo", invoice.getIrnNo());
					}else {
						params.put("irnNo", "");
					}
					if(NullUtil.isNotEmpty(client.getGstnnumber())) {
						params.put("ct_gstno", client.getGstnnumber());
					}else {
						params.put("ct_gstno", "");
					}
					if(NullUtil.isNotEmpty(client.getPannumber())) {
						params.put("ct_panno", client.getPannumber());
					}else {
						params.put("ct_panno", "");
					}
					if(NullUtil.isNotEmpty(client.getPincode())) {
						params.put("ct_pin", client.getPincode().toString());
					}else {
						params.put("ct_pin", "");
					}
					if(NullUtil.isNotEmpty(invoice.getClientAddress())) {
						params.put("ct_caddr", invoice.getClientAddress());
					}else {
						params.put("ct_caddr", "");
					}
					if(NullUtil.isNotEmpty(client.getStatename())) {
						params.put("ct_state", client.getStatename());
					}else {
						params.put("ct_state", "");
					}
					if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
						params.put("bt_billname", invoice.getBilledtoname());
					}else {
						params.put("bt_billname", "");
					}
					if(NullUtil.isNotEmpty(invoice.getB2b().get(0).getCtin())) {
						params.put("bt_gstin", invoice.getB2b().get(0).getCtin());
					}else {
						params.put("bt_gstin", "");
					}
					if(NullUtil.isNotEmpty(invoice.getStatename())) {
						params.put("bt_state", invoice.getStatename());
					}else {
						params.put("bt_state", "");
					}
					/* if(NullUtil.isNotEmpty(invoice.getBuyerPincode())) {
					params.put("bt_pin", invoice.getBuyerPincode());
				}else {
					params.put("bt_pin", "");
				} */
					
					if(NullUtil.isNotEmpty(invoice.getSignedQrCode())) {
						try {	
							byte[] byteArr=generateQRCodeImage(invoice.getSignedQrCode());
							ByteArrayInputStream bio= new ByteArrayInputStream(byteArr);
							params.put("qrcode", ImageIO.read(bio));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							params.put("qrcode",null);
						} catch (WriterException e) {
							// TODO Auto-generated catch block
							params.put("qrcode",null);
						}
					}
					if(isNotEmpty(invoice.getRoundOffAmount())) {
						params.put("roundOffAmt",invoice.getRoundOffAmount());
					}else {
						params.put("roundOffAmt",0d);
					}
					if(isNotEmpty(invoice.getAckNo())){
						params.put("ackno",invoice.getAckNo());
					}else {
						params.put("ackno","");
					}
					if(isNotEmpty(invoice.getAckDt())){
						params.put("ackdt",invoice.getAckDt());
					}else {
						params.put("ackdt","");
					}
					if(isNotEmpty(invoice.getIgstOnIntra())) {
						if(invoice.getIgstOnIntra().equals("Y")) {
							params.put("igstonintra", "Yes");
						}else {
							params.put("igstonintra", "No");
						}
					}else {
						params.put("igstonintra", "");
					}
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype())) {
						if("B2B".equalsIgnoreCase(invoice.getInvtype())) {
							params.put("supply","B2B");
						}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.CREDIT_DEBIT_NOTES)) {
							params.put("supply","CDN");
						}else if(invoice.getInvtype().equalsIgnoreCase(MasterGSTConstants.EXPORTS)) {
							params.put("supply","EXP");
						}
					}
					params.put("printDate", new SimpleDateFormat("dd-MM-YYY hh:mm:ss a").format(new Date()));
					if(isNotEmpty(invoice) && isNotEmpty(invoice.getInvtype()) && "B2C".equalsIgnoreCase(invoice.getInvtype())) {
						
						String url = "";
						if(isNotEmpty(invoice.getBankDetails()) && isNotEmpty(invoice.getBankDetails().getAccountnumber()) && isNotEmpty(invoice.getBankDetails().getIfsccode()) && isNotEmpty(invoice.getBankDetails().getAccountName())) {
							String invdate = "";
							if(isNotEmpty(invoice.getDateofinvoice())) {
								invdate = new SimpleDateFormat("dd-MM-yyyy").format(invoice.getDateofinvoice());
							}
							url = "upi://pay?pa="+invoice.getBankDetails().getAccountnumber()+"@"+invoice.getBankDetails().getIfsccode()+".ifsc.npci&pn="+invoice.getBankDetails().getAccountName()+"&mc=0000&tr="+invoice.getInvoiceno()+"&am="+invoice.getTotalamount_str()+"&mam="+invoice.getTotalamount_str()+"&cu=INR&mode=01&b2cSellerGstin="+client.getGstnnumber()+"&b2cUPIID="+invoice.getBankDetails().getAccountnumber()+"@"+invoice.getBankDetails().getIfsccode()+"&b2cBankAcNo="+invoice.getBankDetails().getAccountnumber()+"&b2cIFSCCode="+invoice.getBankDetails().getIfsccode()+"&b2cInvNo="+invoice.getInvoiceno()+"&b2cInvDate="+invdate+"&b2cGSTAmount="+invoice.getTotaltax()+"&b2cCGSTAmount="+invoice.getTotalCgstAmount()+"&b2cSGSTAmount="+invoice.getTotalSgstAmount()+"&b2cIGSTAmount="+invoice.getTotalIgstAmount()+"&b2cCESS="+invoice.getTotalCessAmount()+"&size=150";
							try {	
								byte[] byteArr=generateQRCodeImage(url);
								ByteArrayInputStream bio= new ByteArrayInputStream(byteArr);
								params.put("qrcode", ImageIO.read(bio));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								params.put("qrcode",null);
							} catch (WriterException e) {
								// TODO Auto-generated catch block
								params.put("qrcode",null);
							}
						}else {
							if(isNotEmpty(invoice.getBankDetails()) && isNotEmpty(invoice.getBankDetails().getAccountnumber())) {
								CompanyBankDetails bankdetails = companyBankDetailsRepository.findByClientidAndAccountnumber(invoice.getClientid(), invoice.getBankDetails().getAccountnumber());
								if(isNotEmpty(bankdetails) && isNotEmpty(bankdetails.getQrcodeid())) {
										GridFSDBFile imageFile = gridOperations.findOne(new Query(Criteria.where("_id").is(bankdetails.getQrcodeid())));
										try {
											byte[] targetArray = ByteStreams.toByteArray(imageFile.getInputStream());
											params.put("qrcode", ImageIO.read(new ByteArrayInputStream(targetArray)));
										} catch (IOException e) {
											params.put("qrcode",null);
										}
								}else {
									params.put("qrcode",null);
								}
							}
						}
					}
			}
		}
		CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
		if(returntype.equals(MasterGSTConstants.EINVOICE)) {
			if(isNotEmpty(customFields) && isNotEmpty(customFields.getEinvoice())) {
				if(isNotEmpty(invoice.getEntertaimentprintto()) && invoice.getEntertaimentprintto().equalsIgnoreCase("Producer")) {
					if(isNotEmpty(invoice.getCustomFieldText1()) && invoice.getCustomFieldText1().equalsIgnoreCase("Picture Name")) {
						params.put("CustomField1", invoice.getCustomFieldText1()+"  :  ");
						params.put("CustomField1Val", invoice.getCustomField1());
					}else if(isNotEmpty(invoice.getCustomFieldText2()) && invoice.getCustomFieldText2().equalsIgnoreCase("Picture Name")) {
						params.put("CustomField1", invoice.getCustomFieldText2()+"  :  ");
						params.put("CustomField1Val", invoice.getCustomField2());
					}else if(isNotEmpty(invoice.getCustomFieldText3()) && invoice.getCustomFieldText3().equalsIgnoreCase("Picture Name")) {
						params.put("CustomField1", invoice.getCustomFieldText3()+"  :  ");
						params.put("CustomField1Val", invoice.getCustomField3());
					}else if(isNotEmpty(invoice.getCustomFieldText4()) && invoice.getCustomFieldText4().equalsIgnoreCase("Picture Name")) {
						params.put("CustomField1", invoice.getCustomFieldText4()+"  :  ");
						params.put("CustomField1Val", invoice.getCustomField4());
					}
				}else {
					int i=1;
					int j=1;
					for(CustomData customdata : customFields.getEinvoice()){
						if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
							String customField1 = "";
							if(j==1) {
								if(NullUtil.isNotEmpty(invoice.getCustomField1())) {
									customField1 = invoice.getCustomField1();
								}
							}else if(j==2) {
								if(NullUtil.isNotEmpty(invoice.getCustomField2())) {
									customField1 = invoice.getCustomField2();
								}
							}else if(j==3) {
								if(NullUtil.isNotEmpty(invoice.getCustomField3())) {
									customField1 = invoice.getCustomField3();
								}
							}else if(j==4) {
								if(NullUtil.isNotEmpty(invoice.getCustomField4())) {
									customField1 = invoice.getCustomField4();
								}
							}
							params.put("CustomField"+i, customdata.getCustomFieldName()+"  :  ");
							params.put("CustomField"+i+"Val", customField1);
							i++;
						}
						j++;
					}
				}
			}
		}else if(returntype.equalsIgnoreCase(MasterGSTConstants.EWAYBILL)) {
			if(isNotEmpty(customFields) && isNotEmpty(customFields.getEwaybill())) {
				int i=1;
				int j=1;
				for(CustomData customdata : customFields.getEwaybill()){
					if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
						String customField1 = "";
						if(j==1) {
							if(NullUtil.isNotEmpty(invoice.getCustomField1())) {
								customField1 = invoice.getCustomField1();
							}
						}else if(j==2) {
							if(NullUtil.isNotEmpty(invoice.getCustomField2())) {
								customField1 = invoice.getCustomField2();
							}
						}else if(j==3) {
							if(NullUtil.isNotEmpty(invoice.getCustomField3())) {
								customField1 = invoice.getCustomField3();
							}
						}else if(j==4) {
							if(NullUtil.isNotEmpty(invoice.getCustomField4())) {
								customField1 = invoice.getCustomField4();
							}
						}
						params.put("CustomField"+i, customdata.getCustomFieldName()+":  ");
						params.put("CustomField"+i+"Val",customField1);
						i++;
					}
					j++;
				}
			}
		}else {
		if(isNotEmpty(customFields)) {
			if(returntype.equals(MasterGSTConstants.GSTR1) || returntype.equals("SalesRegister")) {
				if(isNotEmpty(customFields.getSales())) {
					int i=1;
					int j=1;
					for(CustomData customdata : customFields.getSales()){
						if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
							String customField1 = "";
							if(j==1) {
								if(NullUtil.isNotEmpty(invoice.getCustomField1())) {
									customField1 = invoice.getCustomField1();
								}
							}else if(j==2) {
								if(NullUtil.isNotEmpty(invoice.getCustomField2())) {
									customField1 = invoice.getCustomField2();
								}
							}else if(j==3) {
								if(NullUtil.isNotEmpty(invoice.getCustomField3())) {
									customField1 = invoice.getCustomField3();
								}
							}else if(j==4) {
								if(NullUtil.isNotEmpty(invoice.getCustomField4())) {
									customField1 = invoice.getCustomField4();
								}
							}
							params.put("CustomField"+i, customdata.getCustomFieldName()+":  ");
							params.put("CustomField"+i+"Val",customField1);
							i++;
						}
						j++;
					}
				}
			}else if(returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals("PurchaseRegister") || returntype.equals(MasterGSTConstants.GSTR2)) {
				if(isNotEmpty(customFields.getPurchase())) {
					int i=1;
					int j=1;
					for(CustomData customdata : customFields.getPurchase()){
						if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
							String customField1 = "";
							if(j==1) {
								if(NullUtil.isNotEmpty(invoice.getCustomField1())) {
									customField1 = invoice.getCustomField1();
								}
							}else if(j==2) {
								if(NullUtil.isNotEmpty(invoice.getCustomField2())) {
									customField1 = invoice.getCustomField2();
								}
							}else if(j==3) {
								if(NullUtil.isNotEmpty(invoice.getCustomField3())) {
									customField1 = invoice.getCustomField3();
								}
							}else if(j==4) {
								if(NullUtil.isNotEmpty(invoice.getCustomField4())) {
									customField1 = invoice.getCustomField4();
								}
							}
							params.put("CustomField"+i, customdata.getCustomFieldName()+":  ");
							params.put("CustomField"+i+"Val",customField1);
							i++;
						}
						j++;
					}
				}
			}
		}
		
	}
		if(NullUtil.isNotEmpty(invoice.getLutNo())) {
			params.put("LUTnotext", "LUT NO              :");
			params.put("LUTnovalue", invoice.getLutNo());
		}else {
			params.put("LUTnotext", "");
			params.put("LUTnovalue", "");
		}
		
		if(NullUtil.isNotEmpty(client.getMsmeNo())) {
			params.put("MSMEnotext", "MSME NO:");
			params.put("MSMEnovalue", client.getMsmeNo());
		}else {
			params.put("MSMEnotext", "");
			params.put("MSMEnovalue", "");
		}
		
		if(returntype.equals(MasterGSTConstants.PROFORMAINVOICES)) {
			params.put("invoiceDisplaytext", "PROFORMA INVOICE");
			params.put("invoiceNumberText", "Proforma Invoice No :");
			params.put("invoiceDateText", "Proforma Invoice Date :");
			params.put("OriginalInvNo", "");
			params.put("OriginalInvDate", "");
		}else if(returntype.equals(MasterGSTConstants.ESTIMATES)) {
			params.put("invoiceDisplaytext", "ESTIMATE");
			params.put("invoiceNumberText", "Estimate No :");
			params.put("invoiceDateText", "Estimate Date :");
			params.put("OriginalInvNo", "");
			params.put("OriginalInvDate", "");
		}else if(returntype.equals(MasterGSTConstants.DELIVERYCHALLANS)) {
			params.put("invoiceDisplaytext", "DELIVERY CHALLAN");
			params.put("invoiceNumberText", "Delivery Challan No :");
			params.put("invoiceDateText", "Delivery Challan Date :");
			params.put("OriginalInvNo", "");
			params.put("OriginalInvDate", "");
		}else if(returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			params.put("invoiceDisplaytext", "PURCHASE ORDER");
			params.put("invoiceNumberText", "Purchase Order No :");
			params.put("invoiceDateText", "Purchase Order Date :");
			params.put("OriginalInvNo", "");
			params.put("OriginalInvDate", "");
		}else {
		if(invoice.getInvtype().equals(MasterGSTConstants.ADVANCES)){
			
			if("GSTR1".equals(returntype)){
				params.put("invoiceDisplaytext", "ADVANCE RECEIPT VOUCHER");
				params.put("invoiceNumberText", "Advance Receipt No :");
				params.put("invoiceDateText", "Advance Receipt Date :");
			}else if("GSTR2".equals(returntype) || "Purchase Register".equals(returntype)){
				params.put("invoiceDisplaytext", "ADVANCE PAYMENT");
				params.put("invoiceNumberText", "Advance Payment No :");
				params.put("invoiceDateText", "Advance Payment Date :");
			}
		}else if(invoice.getInvtype().equals(MasterGSTConstants.NIL)){
			params.put("invoiceNumberText", "Bill No :");
			params.put("invoiceDateText", "Bill Date :");
			params.put("invoiceDisplaytext", "BILL OF SUPPLY");
		}else if(invoice.getInvtype().equals(MasterGSTConstants.CREDIT_DEBIT_NOTES)){
			String docType = "";
			String originalInvNo = ""; 
			String originalInvDate="";
			if("GSTR1".equals(returntype)){
				docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				originalInvNo = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum();
				originalInvDate = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt();
			}else if("GSTR2".equals(returntype) || "Purchase Register".equals(returntype)){
				docType = ((GSTR2) invoice).getCdn().get(0).getNt().get(0).getNtty();
				originalInvNo = ((GSTR2) invoice).getCdn().get(0).getNt().get(0).getInum();
				originalInvDate = ((GSTR2) invoice).getCdn().get(0).getNt().get(0).getIdt();
			}else if("EINVOICE".equals(returntype)) {
				docType = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getNtty();
				originalInvNo = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getInum();
				originalInvDate = ((GSTR1) invoice).getCdnr().get(0).getNt().get(0).getIdt();
			}
			params.put("originalInvNo", originalInvNo);
			params.put("originalInvDate",originalInvDate);
			params.put("OriginalInvNo", "Original Inv.No :");
			params.put("OriginalInvDate", "Original Inv.Date :");
			if(docType.equals("C")){
				params.put("invoiceDisplaytext", "CREDIT NOTE");
				params.put("invoiceNumberText", "Credit Note.No :");
				params.put("invoiceDateText", "Credit Note Date :");
			}else if(docType.equals("D")){
				params.put("invoiceDisplaytext", "DEBIT NOTE");
				params.put("invoiceNumberText", "Debit Note.No :");
				params.put("invoiceDateText", "Debit Note Date :");
			}else if(docType.equals("R")){
				params.put("invoiceDisplaytext", "REFUND NOTE");
				params.put("invoiceNumberText", "Refund Voucher No :");
				params.put("invoiceDateText", "Refund Voucher Date :");
			}else if(isNotEmpty(invoice.getTyp()) && ("CRN").equalsIgnoreCase(invoice.getTyp())){
				params.put("invoiceDisplaytext", "CREDIT NOTE");
				params.put("invoiceNumberText", "Credit Note.No :");
				params.put("invoiceDateText", "Credit Note Date :");
			}else if(isNotEmpty(invoice.getTyp()) &&("DBN").equalsIgnoreCase(invoice.getTyp())){
				params.put("invoiceDisplaytext", "DEBIT NOTE");
				params.put("invoiceNumberText", "Debit Note.No :");
				params.put("invoiceDateText", "Debit Note Date :");
			}
		}else if(invoice.getInvtype().equals(MasterGSTConstants.CDNUR)){
			String docType = invoice.getCdnur().get(0).getNtty();
			String originalInvNo = invoice.getCdnur().get(0).getInum();
			String originalInvDate = invoice.getCdnur().get(0).getIdt();
			params.put("originalInvNo", originalInvNo);
			params.put("originalInvDate",originalInvDate);
			params.put("OriginalInvNo", "Original Inv.No:");
			params.put("OriginalInvDate", "Original Inv.Date:");
			if(docType.equals("C")){
				params.put("invoiceDisplaytext", "CREDIT NOTE");
				params.put("invoiceNumberText", "Credit Note.No:");
				params.put("invoiceDateText", "Credit Note Date:");
			}else if(docType.equals("D")){
				params.put("invoiceDisplaytext", "DEBIT NOTE");
				params.put("invoiceNumberText", "Debit Note.No:");
				params.put("invoiceDateText", "Debit Note Date:");
			}else if(docType.equals("R")){
				params.put("invoiceDisplaytext", "REFUND NOTE");
				params.put("invoiceNumberText", "Refund Voucher No:");
				params.put("invoiceDateText", "Refund Voucher Date:");
			}else if(isNotEmpty(invoice.getTyp()) && ("CRN").equalsIgnoreCase(invoice.getTyp())){
				params.put("invoiceDisplaytext", "CREDIT NOTE");
				params.put("invoiceNumberText", "Credit Note.No :");
				params.put("invoiceDateText", "Credit Note Date :");
			}else if(isNotEmpty(invoice.getTyp()) &&("DBN").equalsIgnoreCase(invoice.getTyp())){
				params.put("invoiceDisplaytext", "DEBIT NOTE");
				params.put("invoiceNumberText", "Debit Note.No :");
				params.put("invoiceDateText", "Debit Note Date :");
			}
		}else{
			params.put("invoiceNumberText", "Invoice Number:");
			params.put("invoiceDateText", "Invoice Date:");
			params.put("refNoText", "Reference:");
			params.put("OriginalInvNo", "");
			params.put("OriginalInvDate", "");
			if(invoice.getInvtype().equals(MasterGSTConstants.EXPORTS)){
				params.put("invoiceDisplaytext", "EXPORT INVOICE");
			}else{
				if(NullUtil.isNotEmpty(pconfig) && NullUtil.isNotEmpty(pconfig.getInvoiceText())){
					params.put("invoiceDisplaytext", pconfig.getInvoiceText());
				}else{
					params.put("invoiceDisplaytext", "TAX INVOICE");
				}
			}
		}
	}
		
		if(NullUtil.isNotEmpty(invoice.getB2b()) && NullUtil.isNotEmpty(invoice.getB2b().get(0)) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv()) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getInvTyp())){
			if("SEWP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp()) || "SEWPC".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())){
				params.put("sez", "**Supply To Sez Unit Or Sez Developer For Authorised Operations On Payment Of Integrated Tax");
				}else if("SEWOP".equals(invoice.getB2b().get(0).getInv().get(0).getInvTyp())){
				params.put("sez", "**Supply To Sez Unit Or Sez Developer For Authorised Operations Under Bond Or Letter Of Undertaking Without Payment of Integrated Tax");
				}else {
					params.put("sez", null);
				}
		}
			if(invoice.getInvtype().equals(MasterGSTConstants.EXPORTS)){
				if(NullUtil.isNotEmpty(invoice.getExp()) && NullUtil.isNotEmpty(invoice.getExp().get(0))){
				if("WPAY".equals(invoice.getExp().get(0).getExpTyp())){
				params.put("sez", "**Supply Meant For Export on Payment Of Integrated Tax");
				}else if("WOPAY".equals(invoice.getExp().get(0).getExpTyp())){
				params.put("sez", "**Supply Meant For Export Under Bond Or Letter Of Undertaking Without Payment Of Integrated Tax");
				}else{
				params.put("sez", null);
				}
			}else{
			params.put("sez", null);
			}
			}
		if(NullUtil.isNotEmpty(client.getLogoid())) {
			GridFSDBFile imageFile = gridOperations.findOne(new Query(Criteria.where("_id").is(client.getLogoid())));
			try {
				byte[] targetArray = ByteStreams.toByteArray(imageFile.getInputStream());
				params.put("logo", ImageIO.read(new ByteArrayInputStream(targetArray)));
			} catch (IOException e) {
				params.put("logo", null);
			}
		} else {
			params.put("logo", null);
		}
		if(client.isEnableAuthorisedSignatory()){
			params.put("computerSignature", "");
			params.put("sname", client.getAuthorisedSignatory());
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getAuthSignText())) {
				params.put("signatureText", pconfig.getAuthSignText());
			}else {
				params.put("signatureText", "Authorised Signature");
			}
			if(NullUtil.isNotEmpty(client.getDesignation())){
				params.put("designation", client.getDesignation());
			}else{
				params.put("designation", null);
			}
		}else{
			params.put("computerSignature", "This is a computer generated invoice and does not need a signature");
			params.put("sname", null);
			params.put("signatureText", null);
			params.put("designation", null);
		}
		
		if(NullUtil.isNotEmpty(client.getSignid()) && client.isDigitalSignOn()) {
			GridFSDBFile imageFile = gridOperations.findOne(new Query(Criteria.where("_id").is(client.getSignid())));
			try {
				byte[] targetArray = ByteStreams.toByteArray(imageFile.getInputStream());
				params.put("signature", ImageIO.read(new ByteArrayInputStream(targetArray)));
			} catch (IOException e) {
				params.put("signature", null);
			}
		} else {
			params.put("signature", null);
		}
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			params.put("c_state", "");
		}else {
			if(NullUtil.isNotEmpty(invoice.getStatename())) {
				if(invoice.getStatename().contains("-")){
					String state[] = invoice.getStatename().split("-");
					params.put("c_state", state[1]);
				}else{
					params.put("c_state", invoice.getStatename());
				}
			}else {
				params.put("c_state", "");
			}
		}
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			if(NullUtil.isNotEmpty(client.getBusinessname())) {
				params.put("c_name", client.getBusinessname());
			}else {
				params.put("c_name", "");
			}
		}else {
			if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
				params.put("c_name", invoice.getBilledtoname());
			}else {
				params.put("c_name", "");
			}
		}
		
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			if(NullUtil.isNotEmpty(client.getGstnnumber())) {	
				params.put("c_gst_no", client.getGstnnumber());
			}else {
				params.put("c_gst_no", "");
			}
		}else {
			if(NullUtil.isNotEmpty(invoice.getB2b()) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getCtin())) {
				params.put("c_gst_no", invoice.getB2b().get(0).getCtin());
			}else {
				params.put("c_gst_no", "");
			}
		}
		
		params.put("invoice_no", invoice.getInvoiceno());
		if(NullUtil.isNotEmpty(invoice.getDateofinvoice())) {
			params.put("invoice_date", new SimpleDateFormat("dd-MM-yyyy").format(invoice.getDateofinvoice()));
		} else {
			params.put("invoice_date", "");
		}
		if(NullUtil.isNotEmpty(invoice.getStatename())){
			String[] state = invoice.getStatename().split("-");
			params.put("state_code", state[0]);
			params.put("bpos", invoice.getStatename());
		}else {
			params.put("bpos", "");
		}
		if(returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
			if(NullUtil.isNotEmpty(invoice.getShipmentDtls()) && NullUtil.isNotEmpty(invoice.getShipmentDtls().getTrdNm())) {
				params.put("c_ship_name", invoice.getShipmentDtls().getTrdNm());
			}else {
				params.put("c_ship_name", "");
			}
		}else {
			if(NullUtil.isNotEmpty(invoice.getConsigneename())) {
				params.put("c_ship_name", invoice.getConsigneename());
			}else {
				params.put("c_ship_name", "");
			}
		}
		if(returntype.equals(MasterGSTConstants.EINVOICE) || returntype.equals(MasterGSTConstants.EWAYBILL)) {
			if(NullUtil.isNotEmpty(invoice.getShipmentDtls()) && NullUtil.isNotEmpty(invoice.getShipmentDtls().getAddr1())) {
				String shipaddr = "";
				shipaddr = invoice.getShipmentDtls().getAddr1();
				if(isNotEmpty(invoice.getShipmentDtls().getAddr2())) {
					shipaddr = shipaddr+','+ invoice.getShipmentDtls().getAddr2();
				}
				if(isNotEmpty(invoice.getShipmentDtls().getLoc())) {
					shipaddr = shipaddr+','+ invoice.getShipmentDtls().getLoc();
				}
				if(NullUtil.isNotEmpty(invoice.getShipmentDtls().getStcd())) {
					if(invoice.getShipmentDtls().getStcd().contains("-")) {
						shipaddr = shipaddr+','+ invoice.getShipmentDtls().getStcd().substring(3);
					}else {
						String statename = invoice.getShipmentDtls().getStcd();
						List<StateConfig> states = configService.getStates();
						for (StateConfig state : states) {
							String name = state.getName();
							String[] nm = state.getName().split("-");
							if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
								statename = name;
								break;
							}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
								statename = name;
								break;
							}
						}
						if(statename.contains("-")) {
							statename = statename.substring(3);
						}
						shipaddr = shipaddr+','+ statename;
					}
				}
				if(NullUtil.isNotEmpty(invoice.getShipmentDtls().getPin())) {
					shipaddr = shipaddr+'-'+ invoice.getShipmentDtls().getPin();
				}
				params.put("c_ship_address", shipaddr);
			}else {
				params.put("c_ship_address", "");
			}
			if(NullUtil.isNotEmpty(invoice.getShipmentDtls()) && NullUtil.isNotEmpty(invoice.getShipmentDtls().getGstin())) {
				params.put("c_ship_gstin", invoice.getShipmentDtls().getGstin());
			}else {
				params.put("c_ship_gstin", "");
			}
			if(NullUtil.isNotEmpty(invoice.getDispatcherDtls()) && NullUtil.isNotEmpty(invoice.getDispatcherDtls().getNm())) {
				params.put("c_disp_name", invoice.getDispatcherDtls().getNm());
			}else {
				params.put("c_disp_name", "");
			}
			if(NullUtil.isNotEmpty(invoice.getDispatcherDtls()) && NullUtil.isNotEmpty(invoice.getDispatcherDtls().getAddr1())) {
				String shipaddr = "";
				shipaddr = invoice.getDispatcherDtls().getAddr1();
				if(isNotEmpty(invoice.getDispatcherDtls().getAddr2())) {
					shipaddr = shipaddr+','+ invoice.getDispatcherDtls().getAddr2();
				}
				if(isNotEmpty(invoice.getDispatcherDtls().getLoc())) {
					shipaddr = shipaddr+','+ invoice.getDispatcherDtls().getLoc();
				}
				if(NullUtil.isNotEmpty(invoice.getDispatcherDtls().getStcd())) {
					if(invoice.getDispatcherDtls().getStcd().contains("-")) {
						shipaddr = shipaddr+','+ invoice.getDispatcherDtls().getStcd().substring(3);
					}else {
						String statename = invoice.getDispatcherDtls().getStcd();
						List<StateConfig> states = configService.getStates();
						for (StateConfig state : states) {
							String name = state.getName();
							String[] nm = state.getName().split("-");
							if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
								statename = name;
								break;
							}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
								statename = name;
								break;
							}
						}
						if(statename.contains("-")) {
							statename = statename.substring(3);
						}
						shipaddr = shipaddr+','+ statename;
					}
				}
				if(NullUtil.isNotEmpty(invoice.getDispatcherDtls().getPin())) {
					shipaddr = shipaddr+'-'+ invoice.getDispatcherDtls().getPin();
				}
				params.put("c_disp_address", shipaddr);
			}else {
				params.put("c_disp_address", "");
			}
		}else {
			if(NullUtil.isNotEmpty(invoice.getConsigneeaddress())) {
				params.put("c_ship_address", invoice.getConsigneeaddress());
			}else {
				params.put("c_ship_address", "");
			}
		}
	if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			if(NullUtil.isNotEmpty(invoice.getBilledtoname())) {
				params.put("bname", invoice.getBilledtoname());
			}else {
				params.put("bname", "");
			}
		}else {
			if(NullUtil.isNotEmpty(client.getBusinessname())) {
				params.put("bname", client.getBusinessname());
			}else {
				params.put("bname", "");
			}
		}
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			if(NullUtil.isNotEmpty(invoice.getB2b()) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getCtin())) {
				params.put("bpan", (invoice.getB2b().get(0).getCtin()).substring(2, 12));
			}else {
				params.put("bpan", "");
			}
		}else {
			params.put("bpan", client.getPannumber());
		}
	if(NullUtil.isNotEmpty(client.getCinNumber())) {
		params.put("bsinno", client.getCinNumber());
	}else {
		params.put("bsinno", "");
	}
		String baddress = "";
		if(NullUtil.isEmpty(invoice.getBranch())) {
			if(NullUtil.isNotEmpty(invoice.getClientAddress())) {
				baddress = invoice.getClientAddress();
			}else {
				baddress = client.getAddress();
			}
		}else {
			List<Branch> branches = client.getBranches();
			if(NullUtil.isNotEmpty(branches)) {
				for(Branch branch : branches) {
					if(invoice.getBranch().equals(branch.getName())) {
						baddress = branch.getAddress();
					}
				}
			}
			
		}
			if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
				
				String baddress1 = "";
				if(NullUtil.isNotEmpty(invoice.getB2b()) && NullUtil.isNotEmpty(invoice.getB2b().get(0)) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv()) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv().get(0)) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
					baddress1 = invoice.getB2b().get(0).getInv().get(0).getAddress();
					params.put("baddress", invoice.getB2b().get(0).getInv().get(0).getAddress());
				}else {
					params.put("baddress", "");
				}
		if(NullUtil.isNotEmpty(invoice.getStatename())) {
			if(invoice.getStatename().contains("-")){
				String state[] = invoice.getStatename().split("-");
				
				baddress1 = baddress1 + state[1] ;
			}else{
				baddress1 = baddress1 + invoice.getStatename();
				
			}
		}
		params.put("baddress", baddress1);
		}else {
			params.put("baddress", baddress);
		}
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			params.put("c_address", baddress);
		}else {
		if(NullUtil.isNotEmpty(invoice.getB2b()) 
				&& NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv())
				&& NullUtil.isNotEmpty(invoice.getB2b().get(0).getInv().get(0).getAddress())) {
			params.put("c_address", invoice.getB2b().get(0).getInv().get(0).getAddress());
		} else {
			params.put("c_address", "");
		}
		}
		if(returntype.equals(MasterGSTConstants.GSTR2) || returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals(MasterGSTConstants.PURCHASEORDER)) {
			if(NullUtil.isNotEmpty(invoice.getB2b()) && NullUtil.isNotEmpty(invoice.getB2b().get(0)) && NullUtil.isNotEmpty(invoice.getB2b().get(0).getCtin())) {
				params.put("gst_no", invoice.getB2b().get(0).getCtin());
			}else {
				params.put("gst_no", "");
			}
		}else {
			params.put("gst_no", client.getGstnnumber());
		}
		params.put("date", new Date().toString());
		if(NullUtil.isNotEmpty(invoice.getBankDetails())) {
			if(NullUtil.isNotEmpty(invoice.getBankDetails().getBankname())) {
				params.put("bankname", invoice.getBankDetails().getBankname());
			}else {
				params.put("bankname", "");
			}
			if(NullUtil.isNotEmpty(invoice.getBankDetails().getAccountnumber())) {
				params.put("acctno", invoice.getBankDetails().getAccountnumber());
			}else {
				params.put("acctno", "");
			}
			if(NullUtil.isNotEmpty(invoice.getBankDetails().getBranchname())) {
				params.put("branchname", invoice.getBankDetails().getBranchname());
			}else {
				params.put("branchname", "");
			}
			if(NullUtil.isNotEmpty(invoice.getBankDetails().getIfsccode())) {
				params.put("ifsc", invoice.getBankDetails().getIfsccode());
			}else {
				params.put("ifsc", "");
			}
			if(NullUtil.isNotEmpty(invoice.getBankDetails().getAccountName())) {
				params.put("accountname", invoice.getBankDetails().getAccountName());
			}else {
				params.put("accountname", "");
			}
		} else {
			params.put("bankname", "");
			params.put("acctno", "");
			params.put("branchname", "");
			params.put("ifsc", "");
			params.put("accountname", "");
		}
		if(NullUtil.isNotEmpty(invoice.getNotes())) {
			params.put("notes", invoice.getNotes());
		}else {
			params.put("notes", "");
		}
		if(isNotEmpty(invoice.getTermDays())) {
			if(!invoice.getTermDays().equalsIgnoreCase("0")){
				String term = "Net "+invoice.getTermDays()+" days";
				if(isNotEmpty(invoice.getDueDate())) {
					term = term+" - " +new SimpleDateFormat("dd/MM/yyyy").format(invoice.getDueDate()); 
				}
				params.put("remarks", term);
			}else {
				if(NullUtil.isNotEmpty(invoice.getTerms())) {
					params.put("remarks", invoice.getTerms());
				}else {
					params.put("remarks", "");
				}
			}
			
		}else {
			if(NullUtil.isNotEmpty(invoice.getTerms())) {
				params.put("remarks", invoice.getTerms());
			}else {
				params.put("remarks", "");
			}
		}
		
		
		/*if(NullUtil.isNotEmpty(invoice.getNotroundoftotalamount()) && invoice.getNotroundoftotalamount() > 0d) {
			params.put("totalamount", invoice.getNotroundoftotalamount());
			//if(NullUtil.isNotEmpty(invoice.getOtherValue()) && invoice.getOtherValue() > 0d) {
				//params.put("totalamount", invoice.getNotroundoftotalamount()+invoice.getOtherValue());
			//}
			if(isNotEmpty(invoice.getRoundOffAmount())) {
				params.put("roundedTotal",(invoice.getNotroundoftotalamount()+invoice.getRoundOffAmount()));
			}else {
				params.put("roundedTotal",invoice.getNotroundoftotalamount());
			}
		}*/
		
		if(isNotEmpty(invoice.getRoundOffAmount()) && invoice.getRoundOffAmount() != 0d) {
			if(invoice.isTdstcsenable() && isNotEmpty(invoice.getNotroundoftotalamount())) {
				params.put("totalamount", invoice.getNotroundoftotalamount());
			}else if(isNotEmpty(invoice.getTotalamount())) {
				params.put("totalamount", invoice.getTotalamount());
			}
			if(isNotEmpty(invoice.getRoundOffAmount())) {
				params.put("roundedTotal",(invoice.getNotroundoftotalamount()+invoice.getRoundOffAmount()));
			}else {
				params.put("roundedTotal",invoice.getNotroundoftotalamount());
			}
		}else if(NullUtil.isNotEmpty(invoice.getTotalamount())) {
			params.put("totalamount", invoice.getTotalamount());
			//if(NullUtil.isNotEmpty(invoice.getOtherValue()) && invoice.getOtherValue() > 0d) {
				//params.put("totalamount", invoice.getTotalamount()+invoice.getOtherValue());
			//}
			if(isNotEmpty(invoice.getRoundOffAmount())) {
				params.put("roundedTotal",(invoice.getTotalamount()+invoice.getRoundOffAmount()));
			}else {
				params.put("roundedTotal",invoice.getTotalamount());
			}
		}else {
			params.put("totalamount", 0d);
			params.put("roundedTotal",0d);
		}
		
		
		
		if(NullUtil.isNotEmpty(invoice.getTotalCurrencyAmount())) {
			params.put("totalcuramount", Double.parseDouble(df2.format(invoice.getTotalCurrencyAmount())));
		}else {
			params.put("totalcuramount", 0d);
		}
		if(NullUtil.isNotEmpty(invoice.getTcstdsAmount())) {
			params.put("tcstdsAmount", invoice.getTcstdsAmount());
		}else {
			params.put("tcstdsAmount", 0d);
		}
		if(NullUtil.isNotEmpty(invoice.getNetAmount())) {
			params.put("netAmount", invoice.getNetAmount());
		}else {
			params.put("netAmount", 0d);
		}
		if(NullUtil.isNotEmpty(invoice.getAddcurrencyCode()) && NullUtil.isNotEmpty(invoice.getTotalCurrencyAmount())) {
			Currency c1 = Currency.getInstance(invoice.getAddcurrencyCode());
			params.put("currencyTotal", c1.getSymbol()+" "+Double.parseDouble(df2.format(invoice.getTotalCurrencyAmount())));
		}else {
			params.put("currencyTotal", "");
		}
		
		String amountcurrency = "";
		if(isNotEmpty(invoice.getAddcurrencyCode())) {
			//Currency c1 = Currency.getInstance(invoiceParent.getAddcurrencyCode());
			Currencycodes currencycode = currencycodesRepository.findByCode(invoice.getAddcurrencyCode());
			String currencySymbol = "";
			String mainunit="";
			String fractionunit = "";
			if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getSymbolcode())) {
				currencySymbol = currencycode.getSymbolcode();
			}
			if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getMainunit())) {
				mainunit = currencycode.getMainunit();
			}
			if(isNotEmpty(currencycode) && isNotEmpty(currencycode.getFractionunit())) {
				fractionunit = currencycode.getFractionunit();
			}
			
			if(isNotEmpty(invoice.getTotalCurrencyAmount())) {
				amountcurrency=MoneyConverterUtil.convertNumber(invoice.getTotalCurrencyAmount(),mainunit,fractionunit).toUpperCase();
			}
			
			if(NullUtil.isNotEmpty(invoice.getTotalCurrencyAmount())) {
				Currency c1 = Currency.getInstance(invoice.getAddcurrencyCode());
				params.put("currencyTotal", currencySymbol+" "+Double.parseDouble(df2.format(invoice.getTotalCurrencyAmount())));
			}else {
				params.put("currencyTotal", "");
			}
		}
		
		
		String amountinwords="";
		if(isNotEmpty(invoice.getRoundOffAmount()) && invoice.getRoundOffAmount() != 0d) {
			Double totamount = 0d;
			if(invoice.isTdstcsenable() && isNotEmpty(invoice.getNotroundoftotalamount())) {
				totamount = invoice.getNotroundoftotalamount();
				if(isNotEmpty(invoice.getTcstdsAmount())) {
					totamount += invoice.getTcstdsAmount();
				}
			}else if(isNotEmpty(invoice.getTotalamount())) {
				totamount = invoice.getTotalamount();
				if(isNotEmpty(invoice.getTcstdsAmount())) {
					totamount += invoice.getTcstdsAmount();
				}
			}
			amountinwords=MoneyConverterUtil.getMoneyIntoWords(totamount).toUpperCase();
		}else if(NullUtil.isNotEmpty(invoice.getTotalamount())) {
			Double totamount = invoice.getTotalamount();
			if(isNotEmpty(invoice.getTcstdsAmount())) {
				totamount += invoice.getTcstdsAmount();
			}
			amountinwords=MoneyConverterUtil.getMoneyIntoWords(totamount).toUpperCase();
		}
		if(returntype.equalsIgnoreCase(MasterGSTConstants.EWAYBILL)) {
			String invText="";String supplyType="";String SubSupplyType="";String transText="";String FromAddr="";String ToAddr="";String transMode="";String vehicleType="";
			if(NullUtil.isNotEmpty(invoice.getEwayBillNumber())) {
				try {	
					byte[] byteArr=createQRImage(invoice,returntype);
					ByteArrayInputStream bio= new ByteArrayInputStream(byteArr);
					params.put("qrcode", ImageIO.read(bio));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					params.put("qrcode",null);
				} catch (WriterException e) {
					// TODO Auto-generated catch block
					params.put("qrcode",null);
				}
			}
			if(NullUtil.isNotEmpty(invoice) && NullUtil.isNotEmpty(invoice.getDocType())) {
				if(("INV").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Tax Invoice";
				}else if(("CHL").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Delivery Challan";
				}else if(("BIL").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Bill of Supply";
				}else if(("BOE").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Bill of Entry";
				}else if(("CNT").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Credit Note";
				}else if(("OTH").equalsIgnoreCase(invoice.getDocType())) {
					invText =  "Others";
				}
			}
			if(NullUtil.isNotEmpty(invoice) && NullUtil.isNotEmpty(invoice.getSupplyType())) {
				if(("I").equalsIgnoreCase(invoice.getSupplyType())) {
					supplyType =  "Inward";
				}else if(("O").equalsIgnoreCase(invoice.getSupplyType())) {
					supplyType =  "Outward";
				}
			}
				
			String sText[] = {"Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"};
			if(NullUtil.isNotEmpty(invoice) && NullUtil.isNotEmpty(invoice.getSubSupplyType())) {
				SubSupplyType = sText[Integer.parseInt(invoice.getSubSupplyType())-1];
			}
			String tText[] = {"1. Regular","2. Bill To-Ship To","3. Bill From-Dispatch From","Combinations of 2 & 3"};
			if(NullUtil.isNotEmpty(invoice) && NullUtil.isNotEmpty(invoice.getTransactionType())) {
				String tType = invoice.getTransactionType().toString();
				//Integer intobject = new Integer((int) invoice.getTransactionType()); 
		        //int i = intobject.intValue(); 
				//transText = tText[i-1];
				if(tType.equalsIgnoreCase("1")) {
					transText = "1. Regular";
				}else if(tType.equalsIgnoreCase("2")) {
					transText = "2. Bill To-Ship To";
				}else if(tType.equalsIgnoreCase("3")) {
					transText = "3. Bill From-Dispatch From";
				}else if(tType.equalsIgnoreCase("4")) {
					transText = "Combinations of 2 & 3";
				}
			}
			
			if(isNotEmpty(invoice)) {
				if(isNotEmpty(invoice.getFromAddr1())) {
					FromAddr = FromAddr+invoice.getFromAddr1();
				}
				if(isNotEmpty(invoice.getFromAddr2())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoice.getFromAddr2();
					}else {
						FromAddr = FromAddr+invoice.getFromAddr2();
					}
				}
				if(isNotEmpty(invoice.getFromTrdName())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoice.getFromTrdName();
					}else {
						FromAddr = FromAddr+invoice.getFromTrdName();
					}
				}
				if(isNotEmpty(invoice.getFromPlace())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoice.getFromPlace();
					}else {
						FromAddr = FromAddr+","+invoice.getFromPlace();
					}
				}
				if(isNotEmpty(invoice.getFromPincode())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoice.getFromPincode();
					}else {
						FromAddr = FromAddr+invoice.getFromPincode();
					}
				}
			}
			if(NullUtil.isEmpty(FromAddr)) {
				if(isNotEmpty(client.getAddress())) {
					FromAddr = client.getAddress();
				}
			}
			if(isNotEmpty(invoice)) {
				if(isNotEmpty(invoice.getToAddr1())) {
					ToAddr = ToAddr+invoice.getToAddr1();
				}
				if(isNotEmpty(invoice.getToAddr2())) {
					if(isNotEmpty(ToAddr)) {
						ToAddr = ToAddr+","+invoice.getToAddr2();
					}else {
						ToAddr = ToAddr+invoice.getToAddr2();
					}
				}
				if(isNotEmpty(invoice.getToTrdName())) {
					if(isNotEmpty(ToAddr)) {
					ToAddr = ToAddr+","+invoice.getToTrdName();
					}else {
						ToAddr = ToAddr+invoice.getToTrdName();
					}
				}
				if(isNotEmpty(invoice.getToPlace())) {
					if(isNotEmpty(ToAddr)) {
						ToAddr = ToAddr+","+invoice.getToPlace();
					}else {
						ToAddr = ToAddr+invoice.getToPlace();
					}
				}
				if(isNotEmpty(invoice.getToPincode())) {
					if(isNotEmpty(ToAddr)) {
						ToAddr = ToAddr+","+invoice.getToPincode();
					}else {
						ToAddr = ToAddr+invoice.getToPincode();
					}
				}
			}
			if(NullUtil.isNotEmpty(invoice) && NullUtil.isNotEmpty(invoice.getVehicleType())) {
				if(("R").equalsIgnoreCase(invoice.getVehicleType())) {
					vehicleType =  "Regular";
				}else if(("O").equalsIgnoreCase(invoice.getSupplyType())) {
					vehicleType =  "Over Dimensional Cargo";
				}
				params.put("vehicleType", vehicleType);
			}
			List<EBillVehicleListDetails> vehicleList = invoice.getVehiclListDetails();
			String vText[] = {"Road","Rail","Air","Ship"};
			for(EBillVehicleListDetails evehicle : vehicleList) {
				if(NullUtil.isNotEmpty(evehicle.getTransMode())) {
					Integer intobject = new Integer(evehicle.getTransMode()); 
			        int i = intobject.intValue(); 
					transMode = vText[i-1];
					params.put("transMode", transMode);
				}
				if(NullUtil.isNotEmpty(evehicle.getVehicleType())) {
					if(("R").equalsIgnoreCase(invoice.getVehicleType())) {
						vehicleType =  "Regular";
					}else if(("O").equalsIgnoreCase(invoice.getVehicleType())) {
						vehicleType =  "Over Dimensional Cargo";
					}
					params.put("vehicleType", vehicleType);
				}else {
					params.put("vehicleType", "");
				}
				if(NullUtil.isNotEmpty(evehicle.getVehicleNo())) {
					params.put("vehicleNo", evehicle.getVehicleNo());
				}else {
					params.put("vehicleNo", "");
				}
				if(NullUtil.isNotEmpty(evehicle.getFromPlace())) {
					params.put("fromPlace", evehicle.getFromPlace());
				}else {
					params.put("fromPlace", "");
				}
				if(NullUtil.isNotEmpty(evehicle.getEnteredDate())) {
					params.put("enteredDate", evehicle.getEnteredDate());
				}else {
					params.put("enteredDate", "");
				}
				if(NullUtil.isNotEmpty(evehicle.getTransDocNo())) {
					params.put("transDocNo", evehicle.getTransDocNo());
				}else {
					params.put("transDocNo", "");
				}
				if(NullUtil.isNotEmpty(evehicle.getTransDocDate())) {
					params.put("transDocDate", evehicle.getTransDocDate());
				}else {
					params.put("transDocDate", "");
				}
				
			}
			
			if(NullUtil.isNotEmpty(invoice.getActualDist())) {
				params.put("actDistance", invoice.getActualDist().toString());
			}else if(NullUtil.isNotEmpty(invoice.getTransDistance())) {
				params.put("actDistance", invoice.getTransDistance());
			}
			
			if(NullUtil.isNotEmpty(invoice.getFromGstin())) {
				params.put("gst_no", invoice.getFromGstin());
				params.put("bpan", (invoice.getFromGstin()).substring(2, 12));
			}else if(NullUtil.isNotEmpty(client.getGstnnumber())) {
				params.put("gst_no", client.getGstnnumber());
				params.put("bpan", (client.getGstnnumber()).substring(2, 12));
			}
			
			params.put("docType", invText);
			params.put("ebillNo", invoice.getEwayBillNumber());
			if(isNotEmpty(invoice.geteBillDate())) {
				params.put("ebilldate", new SimpleDateFormat("dd/MM/yyyy").format(invoice.geteBillDate()));
			}else {
				params.put("ebilldate", "");
			}
			params.put("validdate", invoice.getValidUpto());
			
			params.put("suptype", supplyType);
			params.put("subsuptype", SubSupplyType);
			params.put("vehicleType", vehicleType);
			params.put("FromAddr", FromAddr);
			params.put("ToAddr", ToAddr);
			params.put("transVal", transText);
			params.put("ToAddr", ToAddr);
			params.put("transID", invoice.getTransporterId());
			params.put("transName", invoice.getTransporterName());
			if(NullUtil.isNotEmpty(invoice.getToGstin())) {
				params.put("toGSTIN", invoice.getToGstin());
			}else {
				params.put("toGSTIN", invoice.getB2b().get(0).getCtin());
			}
			if(NullUtil.isNotEmpty(client.getStatename())){
				//String[] state = client.getStatename().split("-");
				params.put("bpos", client.getStatename());
			}else {
				params.put("bpos", "");
			}
			params.put("totaltaxableamount", invoice.getTotaltaxableamount());
			params.put("totaltax", invoice.getTotaltax());
			params.put("totalCgstAmount", invoice.getTotalCgstAmount());
			params.put("totalSgstAmount", invoice.getTotalSgstAmount());
		}
		if(NullUtil.isNotEmpty(invoice.getReferenceNumber())) {
				params.put("refNo",invoice.getReferenceNumber());
		}
		if(NullUtil.isNotEmpty(invoice.getAddcurrencyCode())) {
			params.put("totalAmtText",invoice.getAddcurrencyCode());
		}
	params.put("amountinwords", amountinwords);
	params.put("amountcurrency", amountcurrency);
	if(NullUtil.isNotEmpty(invoice.getExchangeRate())) {
		params.put("exchangerate",invoice.getExchangeRate());
	}
	if(isNotEmpty(invoice.getNotes())) {
		params.put("notesTxt", "Notes  :");
	}else {
		params.put("notesTxt", "");
	}
	if(isNotEmpty(invoice.getTermDays())) {
		params.put("termsTxt", "Terms & Conditions  :");
	}else {
		if(isNotEmpty(invoice.getTerms())) {
			params.put("termsTxt", "Terms & Conditions  :");
		}else {
			params.put("termsTxt", "");
		}
	}
	
		return params;
	}

	
	@Async
	public void sendAttachementEmail( File file, InvoiceParent invoice, String email, List<String> ccmail, Reminders mailData, Client client, String returntype, Boolean signcheck)	{
		Map<String, List<SendMsgsSummary>> invoiceMap = clientUtils.getMailDetails(invoice, returntype);
		Map<String, String> invDetails = clientUtils.getTotals(invoice, returntype, mailData);
		Map<String, String> clientDetails = clientUtils.getClientDetails(client);
		String subject = "";
		if(isNotEmpty(mailData)) {
			if(isNotEmpty(mailData.getSubject())) {
				subject = mailData.getSubject();
			}else {
				subject = "Invoice "+invoice.getInvoiceno()+" from "+client.getBusinessname();
			}
		}
		try {
			emailService.sendAttachmentEmail(email, ccmail, VmUtil.velocityTemplate("invoicemail.vm", invoiceMap, clientDetails, invDetails, signcheck), subject, file);			
		}catch (Exception exe) {
			System.out.println(exe.getMessage());
		}
	}
	
	@Async
	public void sendPDFAttachment(String customerMail, List<String> ccmail, Reminders mailData, String id,String clientid,String invId, Boolean signcheck, String returntype,HttpServletRequest request){
		HttpSession session = null;
		try {
			Client client = clientService.findById(clientid);
			User usr = userService.findById(id);
			InvoiceParent invoiceParent = clientService.getInvoice(invId, returntype);
			String filename = invoiceParent.getInvoiceno();
			
			if(isNotEmpty(invoiceParent.getDateofinvoice())) {
				filename  += new SimpleDateFormat("dd-MM-yyyy").format(invoiceParent.getDateofinvoice());
			} 
			// Set param values.
			Map<String, Object> params = printService.getReportParams(client, invoiceParent,returntype);
			PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
			String xmlPath = "classpath:/report/invoice";
			if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
				xmlPath = "classpath:/report/invoice-igst";
			} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
				xmlPath = "classpath:/report/invoice-cgst";
			}
			CustomFields customFields = customFieldsRepository.findByClientid(client.getId().toString());
			boolean custfields = false;
			if(isNotEmpty(customFields)) {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(isNotEmpty(customFields.getSales())) {
						for(CustomData customdata : customFields.getSales()){
							if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
								custfields = true;
								break;
							}
						}
					}
				}else if(returntype.equals(MasterGSTConstants.PURCHASE_REGISTER) || returntype.equals("PurchaseRegister") || returntype.equals(MasterGSTConstants.GSTR2)) {
					if(isNotEmpty(customFields.getPurchase())) {
						for(CustomData customdata : customFields.getPurchase()){
							if(isNotEmpty(customdata.getDisplayInPrint()) && customdata.getDisplayInPrint()) {
								custfields = true;
								break;
							}
						}
					}
				}
			}
			
			
			if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
				xmlPath += "-OSL.jrxml";
			}else {
				if("GSTR1".equals(returntype) || "SalesRegister".equals(returntype)) {
					if(invoiceParent.isTdstcsenable()) {
						xmlPath += "-tcs";
					}
				}
				if(invoiceParent.getInvtype().equals(MasterGSTConstants.ADVANCES)){
					xmlPath += "-adv-disc-qty-rate";
					if(isEmpty(invoiceParent.getConsigneeaddress())){
						xmlPath += "-ship";
					}
					if("GSTR1".equals(returntype) && isNotEmpty(invoiceParent.getBankDetails())){
						if(isEmpty(invoiceParent.getBankDetails().getBankname()) && isEmpty(invoiceParent.getBankDetails().getAccountnumber())){
						xmlPath += "-bank";
						}
					}
					if(custfields) {
						xmlPath += "-cust";
					}
					if(!xmlPath.endsWith("jrxml")) {
						xmlPath += ".jrxml";
					}
				}else {
					if(!invoiceParent.getInvtype().equals(MasterGSTConstants.ISD) || !invoiceParent.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)) {
				if(isNotEmpty(pconfig)) {
					if((Boolean)params.get("isIGST") || (Boolean)params.get("isCGST")){
					if(!pconfig.isEnableDiscount()) {
						xmlPath += "-disc";
					}
					if(!pconfig.isEnableQuantity()) {
						xmlPath += "-qty";
					}
					if(!pconfig.isEnableRate()) {
						xmlPath += "-rate";
					}
					if(isEmpty(invoiceParent.getConsigneeaddress())){
						xmlPath += "-ship";
					}
					if(("GSTR1".equals(returntype) || "DELIVERYCHALLANS".equals(returntype) || "PROFORMAINVOICES".equals(returntype) || "ESTIMATES".equals(returntype)) && isNotEmpty(invoiceParent.getBankDetails())){
						if(isEmpty(invoiceParent.getBankDetails().getBankname()) && isEmpty(invoiceParent.getBankDetails().getAccountnumber())){
						xmlPath += "-bank";
						}
					}else {
						xmlPath += "-bank";
					}
					if(custfields) {
						xmlPath += "-cust";
					}
					if(!xmlPath.endsWith("jrxml")) {
						xmlPath += ".jrxml";
					}
					}else{
						if(!pconfig.isEnableDiscount()) {
							xmlPath += "-disc";
						}
						if(!pconfig.isEnableQuantity()) {
							xmlPath += "-qty";
						}
						if(!pconfig.isEnableRate()) {
							xmlPath += "-rate";
						}
						if(isEmpty(invoiceParent.getConsigneeaddress())){
							xmlPath += "-ship";
						}
						if(("GSTR1".equals(returntype)  || "DELIVERYCHALLANS".equals(returntype) || "PROFORMAINVOICES".equals(returntype) || "ESTIMATES".equals(returntype)) && isNotEmpty(invoiceParent.getBankDetails())){
							if(isEmpty(invoiceParent.getBankDetails().getBankname()) && isEmpty(invoiceParent.getBankDetails().getAccountnumber())){
							xmlPath += "-bank";
							}
						}else {
							xmlPath += "-bank";
						}
						if(custfields) {
							xmlPath += "-cust";
						}
						if(!xmlPath.endsWith("jrxml")) {
							xmlPath += ".jrxml";
						}
					}
				}else{
					if(custfields) {
						xmlPath += "-cust";
					}
					if(!xmlPath.endsWith("jrxml")) {
						xmlPath += ".jrxml";
					}
				}
				}
			}
				if(invoiceParent.getInvtype().equals(MasterGSTConstants.ISD)){
					if(custfields) {
						xmlPath = "classpath:/report/invoice-isd-cust.jrxml";
					}else{
						xmlPath = "classpath:/report/invoice-isd.jrxml";
					}
				}else if(invoiceParent.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)) {
					if(custfields) {
						xmlPath = "classpath:/report/invoice-itc-cust.jrxml";
					}else{
						xmlPath = "classpath:/report/invoice-itc.jrxml";
					}
				}
			
		}
			if(invoiceParent.getInvtype().equals(MasterGSTConstants.ITC_REVERSAL)) {
				List<Item> itcitms = invoiceParent.getItems();
				for(Item item : itcitms) {
				if(isNotEmpty(item)) {
					if(isNotEmpty(item.getItcRevtype())) {
						if(item.getItcRevtype().equals("rule2_2")) {
							item.setItcRevtype("Amount in terms of rule 37(2)");
						} else if(item.getItcRevtype().equals("rule7_1_m")) {
							item.setItcRevtype("Amount in terms of rule 42(1)(m)");
						} else if(item.getItcRevtype().equals("rule8_1_h")) {
							item.setItcRevtype("Amount in terms of rule 43(1)(h)");
						} else if(item.getItcRevtype().equals("rule7_2_a")) {
							item.setItcRevtype("Amount in terms of rule 42(2)(a)");
						} else if(item.getItcRevtype().equals("rule7_2_b")) {
							item.setItcRevtype("Amount in terms of rule 42(2)(b)");
						} else if(item.getItcRevtype().equals("revitc")) {
							item.setItcRevtype("On account of amount paid subsequent to reversal of ITC");
						}  else if(item.getItcRevtype().equals("other")) {
							item.setItcRevtype("Any other liability (Pl specify)");
						}
					}
				}
				}
			}
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getInvtype()) && "Exports".equalsIgnoreCase(invoiceParent.getInvtype())) {
				if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
					if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
						xmlPath = "classpath:/report/invoice-exports-currency-igst.jrxml";
					}
				} else{
					if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
						xmlPath = "classpath:/report/invoice-exports-currency.jrxml";
					}
				}
			}
			//System.out.println(xmlPath);
			Resource config = resourceLoader.getResource(xmlPath);
			// get report file and then load into jasperDesign
			JasperDesign jasperDesign = JRXmlLoader.load(config.getInputStream());
			// compile the jasperDesign
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getInvoiceText())) {
				params.put("title", pconfig.getInvoiceText());
			} else {
				params.put("title", "INVOICE");
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getQtyText())) {
				params.put("invqty", pconfig.getQtyText());
			} else {
				params.put("invqty", "Qty");
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getRateText())) {
				params.put("invrate", pconfig.getRateText());
			} else {
				params.put("invrate", "Rate");
			}
			params.put(JRParameter.REPORT_LOCALE, new Locale("en", "IN")); 
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
					new JRBeanCollectionDataSource(invoiceParent.getItems()));
	
			// export to pdf
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			if(filename.contains("/")) {
				filename = filename.replaceAll("/", "_");
			}
			File convFile = new File(filename);
			convFile.createNewFile();
			OutputStream os = new FileOutputStream(convFile);
			os.write(data);
			os.flush();
			os.close();
			session = request.getSession(false);
			if (isNotEmpty(session)) {
				session.setAttribute("invoicetemp.pdf", convFile);
			}
			//send email method call
			sendAttachementEmail(convFile,invoiceParent, customerMail, ccmail, mailData, client, returntype, signcheck);
		}  catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("invoicetemp.pdf"))) {
			File errFile = (File) session.getAttribute("invoicetemp.pdf");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("invoicetemp.pdf");
		}
	    
	}
	@Async
	public void sendEinvoicePDFAttachment(String customerMail,List<String> ccmail, Reminders mailData, String id,String clientid,String invId, Boolean signcheck, String returntype,HttpServletRequest request){
		HttpSession session = null;
		try {
			Client client = clientService.findById(clientid);
			User usr = userService.findById(id);
			InvoiceParent invoiceParent = clientService.getInvoice(invId, "GSTR1");
			String filename = invoiceParent.getInvoiceno()+"_";
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getB2b()) && isNotEmpty(invoiceParent.getB2b().get(0))&& isNotEmpty(invoiceParent.getB2b().get(0).getCtin())) {
				filename += invoiceParent.getB2b().get(0).getCtin()+"_";
			}
			if(isNotEmpty(invoiceParent.getDateofinvoice())) {
				filename  += new SimpleDateFormat("dd-MM-yyyy").format(invoiceParent.getDateofinvoice());
			} 
			// Set param values.
			Map<String, Object> params = printService.getReportParams(client, invoiceParent,returntype);
			PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
			String xmlPath = "classpath:/report/invoice";
			if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
				if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-igst-currency-OSL";
						}else {
							xmlPath = "classpath:/report/invoice-einv-igst-OSL";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv-igst-OSL";
					}
				} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
					xmlPath = "classpath:/report/invoice-einv-cgst-OSL";
				}else {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-currency-OSL";
						}else {
							xmlPath = "classpath:/report/invoice-einv-OSL";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv-OSL";
					}
				}
			}else if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@fhpl.net") || usr.getEmail().endsWith("@apollohealthresources.com")|| usr.getEmail().endsWith("bravikumar929@gmail.com"))){
				if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-igst-currency-FHPL";
						}else {
							xmlPath = "classpath:/report/invoice-einv-igst-FHPL";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv-igst-FHPL";
					}
				} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
					xmlPath = "classpath:/report/invoice-einv-cgst-FHPL";
				}else {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-currency-FHPL";
						}else {
							xmlPath = "classpath:/report/invoice-einv-FHPL";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv-FHPL";
					}
				}
			}else {
				if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-igst-currency";
						}else {
							xmlPath = "classpath:/report/invoice-einv-igst";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv-igst";
					}
				} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
					xmlPath = "classpath:/report/invoice-einv-cgst";
				}else {
					if(invoiceParent.getInvtype().equals(MasterGSTConstants.EXPORTS)) {
						if(isNotEmpty(invoiceParent.getAddcurrencyCode())) {
							xmlPath = "classpath:/report/invoice-einv-currency";
						}else {
							xmlPath = "classpath:/report/invoice-einv";
						}
					}else {
						xmlPath = "classpath:/report/invoice-einv";
					}
				}
			}
			
			if(invoiceParent.isTdstcsenable()) {
				xmlPath += "-tcs";
			}
			if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) || 
					isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
				xmlPath += "-ship";
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
					params.put("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
					params.put("c_ship_name", invoiceParent.getDispatcherDtls().getNm());
				}else {
					params.put("c_ship_name", "");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
					String shipaddr ="";
					shipaddr = invoiceParent.getShipmentDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getShipmentDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getShipmentDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getStcd())) {
						if(invoiceParent.getShipmentDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getShipmentDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getShipmentDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
					}
					params.put("c_ship_address", shipaddr);
					params.put("headText", "Details Of Shipment");
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
					String shipaddr ="";
					shipaddr = invoiceParent.getDispatcherDtls().getAddr1();
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getAddr2())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getAddr2();
					}
					if(isNotEmpty(invoiceParent.getDispatcherDtls().getLoc())) {
						shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getLoc();
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getStcd())) {
						if(invoiceParent.getDispatcherDtls().getStcd().contains("-")) {
							shipaddr = shipaddr+','+ invoiceParent.getDispatcherDtls().getStcd().substring(3);
						}else {
							String statename = invoiceParent.getDispatcherDtls().getStcd();
							List<StateConfig> states = configService.getStates();
							for (StateConfig state : states) {
								String name = state.getName();
								String[] nm = state.getName().split("-");
								if ((nm[1].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}else if((nm[0].replaceAll("\\s", "")).equalsIgnoreCase(statename.replaceAll("\\s", ""))) {
									statename = name;
									break;
								}
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
					}
					params.put("c_ship_address", shipaddr);
					params.put("headText", "Details Of Dispatcher");
				}else {
					params.put("c_ship_address", "");
				}
			}
			if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) && isNotEmpty(invoiceParent.getDispatcherDtls())&& isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
				xmlPath += "-disp";
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.isEnableRoundOffAmt()) && pconfig.isEnableRoundOffAmt()) {
				xmlPath += "-roff";
			}
			if(!xmlPath.endsWith("jrxml")) {
				xmlPath += ".jrxml";
			}
			System.out.println(xmlPath);
			Resource config = resourceLoader.getResource(xmlPath);
			// get report file and then load into jasperDesign
			JasperDesign jasperDesign = JRXmlLoader.load(config.getInputStream());
			// compile the jasperDesign
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getInvoiceText())) {
				params.put("title", pconfig.getInvoiceText());
			} else {
				params.put("title", "INVOICE");
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getQtyText())) {
				params.put("invqty", pconfig.getQtyText());
			} else {
				params.put("invqty", "Qty");
			}
			if(isNotEmpty(pconfig) && isNotEmpty(pconfig.getRateText())) {
				params.put("invrate", pconfig.getRateText());
			} else {
				params.put("invrate", "Rate");
			}
			if(isNotEmpty(usr) && isNotEmpty(usr.getEmail()) && (usr.getEmail().endsWith("@oceansparkle.in") || usr.getEmail().endsWith("@kvrco.in"))) {
				if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getIrnNo())) {
					params.put("irnNoText","IRN : ");
				}else {
					params.put("irnNoText","");
				}
			}else {
				if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getIrnNo())) {
					params.put("irnNoText","IRN No : ");
				}else {
					params.put("irnNoText","");
				}
			}
			params.put(JRParameter.REPORT_LOCALE, new Locale("en", "IN")); 
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,
					new JRBeanCollectionDataSource(invoiceParent.getItems()));
			// export to pdf
			byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
			if(filename.contains("/")) {
				filename = filename.replaceAll("/", "_");
			}
			File convFile = new File(filename);
			convFile.createNewFile();
			OutputStream os = new FileOutputStream(convFile);
			os.write(data);
			os.flush();
			os.close();
			session = request.getSession(false);
			if (isNotEmpty(session)) {
				session.setAttribute("invoicetemp.pdf", convFile);
			}
			//send email method call
			sendAttachementEmail(convFile,invoiceParent, customerMail, ccmail, mailData, client, returntype, signcheck);
		}  catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		if (isNotEmpty(session) && isNotEmpty(session.getAttribute("invoicetemp.pdf"))) {
			File errFile = (File) session.getAttribute("invoicetemp.pdf");
			try {
				errFile.delete();
			} catch (Exception e) {}
			session.removeAttribute("invoicetemp.pdf");
		}
	}
	
}
