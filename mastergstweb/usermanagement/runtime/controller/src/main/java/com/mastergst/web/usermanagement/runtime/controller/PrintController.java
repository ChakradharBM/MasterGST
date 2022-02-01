package com.mastergst.web.usermanagement.runtime.controller;

import static com.mastergst.core.common.MasterGSTConstants.BEGIN;
import static com.mastergst.core.common.MasterGSTConstants.END;
import static com.mastergst.core.util.NullUtil.isNotEmpty;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.mastergst.configuration.service.ConfigService;
import com.mastergst.configuration.service.StateConfig;
import com.mastergst.core.util.MoneyConverterUtil;
import com.mastergst.core.util.NullUtil;
import com.mastergst.usermanagement.runtime.domain.Client;
import com.mastergst.usermanagement.runtime.domain.CompanyCustomers;
import com.mastergst.usermanagement.runtime.domain.CustomFields;
import com.mastergst.usermanagement.runtime.domain.EBillVehicleListDetails;
import com.mastergst.usermanagement.runtime.domain.InvoiceParent;
import com.mastergst.usermanagement.runtime.domain.PrintConfiguration;
import com.mastergst.usermanagement.runtime.repository.CompanyCustomersRepository;
import com.mastergst.usermanagement.runtime.repository.CustomFieldsRepository;
import com.mastergst.usermanagement.runtime.service.ClientService;
import com.mastergst.usermanagement.runtime.service.PrintService;
import com.mastergst.usermanagement.runtime.service.ProfileService;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Controller
public class PrintController {
	@Autowired
    private ResourceLoader resourceLoader;
	@Autowired
	private ClientService clientService;
	@Autowired
	private PrintService printService;
	@Autowired
	private ProfileService profileService;
	@Autowired
	CustomFieldsRepository customFieldsRepository;
	@Autowired
	ConfigService configService;
	@Autowired 
	private CompanyCustomersRepository companyCustomersRepository;
	@RequestMapping(value = "/ewaybillprintpdf/{id}/{name}/{usertype}/{clientid}/{returntype}/{invId}", method = RequestMethod.GET,produces = MediaType.APPLICATION_PDF_VALUE)
	public HttpEntity<byte[]>  printEwayBillInvoicePdf(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("invId") String invId, @PathVariable("returntype") String returntype, ModelMap model)
					throws Exception {
		
		try {
			Client client = clientService.findById(clientid);
			InvoiceParent invoiceParent = clientService.getInvoice(invId, returntype);
			String filename = invoiceParent.getInvoiceno();
			if(isNotEmpty(invoiceParent.getDateofinvoice())) {
				filename  += new SimpleDateFormat("dd-MM-yyyy").format(invoiceParent.getDateofinvoice());
			} 
			/*if(isNotEmpty(invoiceParent.getVehicleType())) {
				for(EBillVehicleListDetails evehicle : invoiceParent.getVehiclListDetails()) {
					if(("R").equalsIgnoreCase(invoiceParent.getVehicleType())) {
						evehicle.setVehicleType("Regular");
					}else if(("O").equalsIgnoreCase(invoiceParent.getVehicleType())) {
						evehicle.setVehicleType("Over Dimensional Cargo");
					}
				}
			} */
			// Set param values.
			Map<String, Object> params = printService.getReportParams(client, invoiceParent,returntype);
			//String xmlFile = "classpath:/report/report2.jrxml";
			String xmlPath = "classpath:/report/invoice";
			if((Boolean)params.get("isIGST") && !(Boolean)params.get("isCGST")) {
				xmlPath = "classpath:/report/invoice-waybill-igst";
			} else if(!(Boolean)params.get("isIGST") && (Boolean)params.get("isCGST")) {
				xmlPath = "classpath:/report/invoice-waybill-cgst";
			}else {
				xmlPath = "classpath:/report/invoice-waybill";
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
			if(!xmlPath.endsWith("jrxml")) {
				xmlPath += ".jrxml";
			}
			System.out.println(xmlPath);
			String custAddress = "";
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				
				if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr1())) {
					custAddress = invoiceParent.getBuyerDtls().getAddr1();
				}
				if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr2())) {
					custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getAddr2();
				}
				if(isNotEmpty(invoiceParent.getBuyerDtls().getLoc())) {
					custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getLoc();
				}
			}
			if(NullUtil.isEmpty(custAddress)) {
				CompanyCustomers customers = null;
				if(isNotEmpty(invoiceParent.getB2b()) && isNotEmpty(invoiceParent.getB2b().get(0)) && isNotEmpty(invoiceParent.getB2b().get(0).getCtin())) {
					String gstnnumber =invoiceParent.getB2b().get(0).getCtin().trim();
					customers = companyCustomersRepository.findByGstnnumberAndClientid(gstnnumber, client.getId().toString());
				}else {
					customers = companyCustomersRepository.findByNameAndClientid(invoiceParent.getBilledtoname(), client.getId().toString());
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
			}
			if(isNotEmpty(invoiceParent.getStatename())) {
				if(isNotEmpty(custAddress)) {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = custAddress+','+ invoiceParent.getStatename().substring(3);
					}else {
						custAddress = custAddress+','+ invoiceParent.getStatename();
					}
				}else {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = invoiceParent.getStatename().substring(3);
					}else {
						custAddress = invoiceParent.getStatename();
					}
				}
			}
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				if(isNotEmpty(invoiceParent.getBuyerDtls().getPin()) && invoiceParent.getBuyerDtls().getPin()>0) {
					if(isNotEmpty(custAddress)) {
						custAddress = custAddress+'-'+ invoiceParent.getBuyerDtls().getPin();
					}else {
						custAddress = invoiceParent.getBuyerDtls().getPin()+"";
					}
				}
			}
		
		params.put("custAddress", custAddress);
			Resource config = resourceLoader.getResource(xmlPath);
			// get report file and then load into jasperDesign
			JasperDesign jasperDesign = JRXmlLoader.load(config.getInputStream());
			// compile the jasperDesign
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
		        params.put("datasource1", invoiceParent.getItems());
		        
		        List<EBillVehicleListDetails> vehicleList = invoiceParent.getVehiclListDetails();
				for(EBillVehicleListDetails evehicle : vehicleList) {
					
					
					if(NullUtil.isEmpty(evehicle.getVehicleNo())) {
						evehicle.setVehicleNo("");
					}
					if(NullUtil.isEmpty(evehicle.getFromPlace())) {
						evehicle.setFromPlace("");
					}
					if(NullUtil.isEmpty(evehicle.getEnteredDate())) {
						evehicle.setEnteredDate("");
					}
					if(NullUtil.isEmpty(evehicle.getTransDocNo())) {
						evehicle.setTransDocNo("");
					}
					if(NullUtil.isEmpty(evehicle.getTransDocDate())) {
						evehicle.setTransDocDate("");
					}
					if(NullUtil.isEmpty(evehicle.getTransMode())) {
						evehicle.setTransMode("");
					}else {
						String vText[] = {"Road","Rail","Air","Ship"};
						Integer intobject = new Integer(evehicle.getTransMode()); 
				        int i = intobject.intValue(); 
				        evehicle.setTransMode(vText[i-1]);	
				     }
					if(NullUtil.isEmpty(evehicle.getVehicleType())) {
						evehicle.setVehicleType("");
					}else {
						if(("R").equalsIgnoreCase(evehicle.getVehicleType())) {
							evehicle.setVehicleType("Regular");
						}else if(("O").equalsIgnoreCase(evehicle.getVehicleType())) {
							evehicle.setVehicleType("Over Dimensional Cargo");
						}
					}
				}
				
		        params.put("datasource2", vehicleList);
		        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,params, new JREmptyDataSource());
		        byte[] data = JasperExportManager.exportReportToPdf(jasperPrint);
				HttpHeaders header = new HttpHeaders();
				header.setContentType(MediaType.APPLICATION_PDF);
				header.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename="+filename+".pdf");
				header.setContentLength(data.length);
				return new HttpEntity<byte[]>(data, header);
		      //  JasperExportManager.exportReportToPdfFile(jasperPrint, "src/main/resources/report2.pdf");
		} catch (Exception e) {
			return new ResponseEntity<>( HttpStatus.INTERNAL_SERVER_ERROR );
		}
		
	}
	@RequestMapping("{ewaybillNo}/qr_image.jpg")
	private void createQRImage(@PathVariable("ewaybillNo") String ewaybillNo,HttpServletResponse response) throws WriterException, IOException {

		String qrCodeText = "This is Your Sample E-invoice";
		String filePath = "png";
		int size = 160;
		String fileType = "png";
		//System.out.println("DONE");
	
		ServletOutputStream out = response.getOutputStream();
	
		// Create the ByteMatrix for the QR-Code that encodes the given String
		Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
		hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix byteMatrix = qrCodeWriter.encode(ewaybillNo, BarcodeFormat.QR_CODE, size, size, hintMap);
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
		ImageIO.write(image, fileType, out);				
	}
	@RequestMapping(value = "/ewaybillprint/{id}/{name}/{usertype}/{clientid}/{returntype}/{invId}", method = RequestMethod.GET)
	public String printEwayBillInvoice(@PathVariable("id") String id, @PathVariable("name") String fullname,
			@PathVariable("usertype") String usertype, @PathVariable("clientid") String clientid,
			@PathVariable("invId") String invId, @PathVariable("returntype") String returntype, ModelMap model)
					throws Exception {
		final String method = "printEwayBillInvoice::";
		model.addAttribute("id", id);
		model.addAttribute("fullname", fullname);
		model.addAttribute("usertype", usertype);
		model.addAttribute("returntype", returntype);
		Client client = clientService.findById(clientid);
		PrintConfiguration pconfig = profileService.getPrintConfig(clientid);
		model.addAttribute("pconfig", pconfig);
		model.addAttribute("client", client);
		if(isNotEmpty(client.getStatename())){
			String[] state = client.getStatename().split("-");
			model.addAttribute("state_code", state[0]);
		}
		InvoiceParent invoiceParent = clientService.getInvoice(invId, returntype);
		
		if(isNotEmpty(invoiceParent.getStatename())){
			String[] state = invoiceParent.getStatename().split("-");
			model.addAttribute("istate_code", state[0]);
		}
		
			String amountinwords="";
			if(isNotEmpty(invoiceParent.getTotalamount())) {
				amountinwords=MoneyConverterUtil.getMoneyIntoWords(invoiceParent.getTotalamount()).toUpperCase();
			}
			String invText="";String supplyType="";String SubSupplyType="";String transText="";String FromAddr="";String ToAddr="";String transMode="";String vehicleType="";
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getDocType())) {
				if(("INV").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Tax Invoice";
				}else if(("CHL").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Delivery Challan";
				}else if(("BIL").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Bill of Supply";
				}else if(("BOE").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Bill of Entry";
				}else if(("CNT").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Credit Note";
				}else if(("OTH").equalsIgnoreCase(invoiceParent.getDocType())) {
					invText =  "Others";
				}
			}
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getSupplyType())) {
				if(("I").equalsIgnoreCase(invoiceParent.getSupplyType())) {
					supplyType =  "Inward";
				}else if(("O").equalsIgnoreCase(invoiceParent.getSupplyType())) {
					supplyType =  "Outward";
				}
			}
				
			String sText[] = {"Supply","Import","Export","Job Work","For Own Use","Job work Returns","Sales Return","Others","SKD/CKD","Line Sales","Recipient Not Known","Exhibition or Fairs"};
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getSubSupplyType())) {
				SubSupplyType = sText[Integer.parseInt(invoiceParent.getSubSupplyType())-1];
			}
			String tText[] = {"1. Regular","2. Bill To-Ship To","3. Bill From-Dispatch From","Combinations of 2 & 3"};
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getTransactionType())) {
				String tType = invoiceParent.getTransactionType().toString();
				//Integer intobject = new Integer((int) invoiceParent.getTransactionType()); 
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
			if(isNotEmpty(invoiceParent)) {
				if(isNotEmpty(invoiceParent.getFromAddr1())) {
					FromAddr = FromAddr+invoiceParent.getFromAddr1();
				}
				if(isNotEmpty(invoiceParent.getFromAddr2())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoiceParent.getFromAddr2();
					}else {
						FromAddr = FromAddr+invoiceParent.getFromAddr2();
					}
				}
				if(isNotEmpty(invoiceParent.getFromTrdName())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoiceParent.getFromTrdName();
					}else {
						FromAddr = FromAddr+invoiceParent.getFromTrdName();
					}
				}
				if(isNotEmpty(invoiceParent.getFromPlace())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoiceParent.getFromPlace();
					}else {
						FromAddr = FromAddr+","+invoiceParent.getFromPlace();
					}
				}
				if(isNotEmpty(invoiceParent.getFromPincode())) {
					if(isNotEmpty(FromAddr)) {
						FromAddr = FromAddr+","+invoiceParent.getFromPincode();
					}else {
						FromAddr = FromAddr+invoiceParent.getFromPincode();
					}
				}
			}
			if(NullUtil.isEmpty(FromAddr)) {
				if(isNotEmpty(client.getAddress())) {
					FromAddr = client.getAddress();
				}
			}
			String custAddress = "";
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				
				if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr1())) {
					custAddress = invoiceParent.getBuyerDtls().getAddr1();
				}
				if(isNotEmpty(invoiceParent.getBuyerDtls().getAddr2())) {
					custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getAddr2();
				}
				if(isNotEmpty(invoiceParent.getBuyerDtls().getLoc())) {
					custAddress = custAddress+','+ invoiceParent.getBuyerDtls().getLoc();
				}
			}
			if(isNotEmpty(invoiceParent.getStatename())) {
				if(isNotEmpty(custAddress)) {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = custAddress+','+ invoiceParent.getStatename().substring(3);
					}else {
						custAddress = custAddress+','+ invoiceParent.getStatename();
					}
				}else {
					if(invoiceParent.getStatename().contains("-")) {
						custAddress = invoiceParent.getStatename().substring(3);
					}else {
						custAddress = invoiceParent.getStatename();
					}
				}
			}
			if(isNotEmpty(invoiceParent.getBuyerDtls())) {
				if(isNotEmpty(invoiceParent.getBuyerDtls().getPin()) && invoiceParent.getBuyerDtls().getPin()>0) {
					if(isNotEmpty(custAddress)) {
						custAddress = custAddress+'-'+ invoiceParent.getBuyerDtls().getPin();
					}else {
						custAddress = invoiceParent.getBuyerDtls().getPin()+"";
					}
				}
			}
			if((isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) || 
					(isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1()))) {
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getDispatcherDtls().getNm());
				}else {
					model.addAttribute("c_ship_name", "");
				}
				String shipaddr = "";
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
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
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
					}
					model.addAttribute("headText", "Details Of Shipment");
				}else if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
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
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
					}
					model.addAttribute("headText", "Details Of Dispatcher");
				}else {
					model.addAttribute("c_ship_address", "");
				}
				model.addAttribute("c_ship_address", shipaddr);
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getGstin())) {
					model.addAttribute("c_ship_gstin", invoiceParent.getShipmentDtls().getGstin());
				}else {
					model.addAttribute("c_ship_gstin", "");
				}
			}
			if(isNotEmpty(invoiceParent.getShipmentDtls()) && isNotEmpty(invoiceParent.getShipmentDtls().getAddr1()) && 
					isNotEmpty(invoiceParent.getDispatcherDtls()) && isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getTrdNm())) {
					model.addAttribute("c_ship_name", invoiceParent.getShipmentDtls().getTrdNm());
				}else {
					model.addAttribute("c_ship_name","");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getGstin())) {
					model.addAttribute("c_ship_gstin", invoiceParent.getShipmentDtls().getGstin());
				}else {
					model.addAttribute("c_ship_gstin", "");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getNm())) {
					model.addAttribute("c_disp_name", invoiceParent.getDispatcherDtls().getNm());
				}else {
					model.addAttribute("c_disp_name","");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls()) && NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getAddr1())) {
					String shipaddr = "";
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
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getShipmentDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getShipmentDtls().getPin();
					}
					model.addAttribute("c_ship_address", shipaddr);
				}else {
					model.addAttribute("c_ship_address", "");
				}
				if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls()) && NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getAddr1())) {
					String shipaddr = "";
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
							if(statename.contains("-")) {
								statename = statename.substring(3);
							}
							shipaddr = shipaddr+','+ statename;
						}
					}
					if(NullUtil.isNotEmpty(invoiceParent.getDispatcherDtls().getPin())) {
						shipaddr = shipaddr+'-'+ invoiceParent.getDispatcherDtls().getPin();
					}
					model.addAttribute("c_disp_address", shipaddr);
				}else {
					model.addAttribute("c_disp_address", "");
				}
			}
			if(isNotEmpty(invoiceParent) && isNotEmpty(invoiceParent.getVehicleType())) {
				if(("R").equalsIgnoreCase(invoiceParent.getVehicleType())) {
					vehicleType =  "Regular";
				}else if(("O").equalsIgnoreCase(invoiceParent.getVehicleType())) {
					vehicleType =  "Over Dimensional Cargo";
				}
				
			}
			List<EBillVehicleListDetails> vehicleList = invoiceParent.getVehiclListDetails();
			String vText[] = {"Road","Rail","Air","Ship"};
			for(EBillVehicleListDetails evehicle : vehicleList) {
				if(isNotEmpty(evehicle.getTransMode())) {
					Integer intobject = new Integer(evehicle.getTransMode()); 
			        int i = intobject.intValue(); 
					transMode = vText[i-1];
				}
				if(isNotEmpty(evehicle.getVehicleType())) {
					if(("R").equalsIgnoreCase(evehicle.getVehicleType())) {
						vehicleType =  "Regular";
					}else if(("O").equalsIgnoreCase(evehicle.getVehicleType())) {
						vehicleType =  "Over Dimensional Cargo";
					}
				}
				model.addAttribute("transMode", transMode);
			}
		model.addAttribute("amountinwords", amountinwords);
		model.addAttribute("vehicleType", vehicleType);
		model.addAttribute("custAddress", custAddress);
		model.addAttribute("invText", invText);
		model.addAttribute("transType", transText);
		model.addAttribute("FromAddr", FromAddr);
		model.addAttribute("ToAddr", ToAddr);
		model.addAttribute("supplyType", supplyType);
		model.addAttribute("SubSupplyType", SubSupplyType);
		model.addAttribute("invoice", invoiceParent);
		CustomFields customFields = customFieldsRepository.findByClientid(clientid);
		if(isNotEmpty(customFields)) {
			model.addAttribute("customFieldList", customFields.getEwaybill());
		}
		return "client/ewaybillprint";
	}
}
