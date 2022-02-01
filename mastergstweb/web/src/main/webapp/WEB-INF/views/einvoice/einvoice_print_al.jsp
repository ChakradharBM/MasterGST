<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Invoice</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css"	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/print/print.css"	media="all" />
<script src="${contextPath}/static/mastergst/js/print/jQuery.print.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/print/html2pdf.bundle.min.js"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
.h1, .h2, .h3, .h4, .h5, .h6, h1, h2, h3, h4, h5, h6{font-family:'source_sans_proregular',sans-serif!important;}
.disp_address{border:1px solid}
.disp_address td{border:none!important}
.address_data{word-break: break-all;}
</style>
</head>
<body>
<!-- body start -->
	<div class="nav-heder"><a class="navbar-brand-logo" href="http://mastergst.com"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" style="width:200px" alt="Master Gst" /> </a></div>
<div class="container">
  <!-- header page begin -->
  <!-- header page end -->
  <!-- Dashboard body start -->
  <div class="row">
    <!-- dashboard left block begin -->
    <div class="col-sm-12">
      <div class="print-tools mb-2">
         <a href="javascript:window.close()" class="print-link btn-print">CLOSE</a> 
		 <a href="${contextPath}/einvoiceprintpdf/${id}/${fullname}/${usertype}/${client.id}/EINVOICE/${invoice.id}" class="print-link btn-print mr-2">Generate PDF</a>
		 <a href="javascript:void()" onClick="jQuery('#print-wrap').print();" class="print-link btn-print mr-2">PRINT</a></div>
      <div class="invoice-wrap" id="print-wrap">
        <div class="invoice-wrap-in pt-0 mt-0" style="height: 1000px;max-height: max-content;">
          <form class="meterialform invoiceform" style="min-height: 725px;height: auto;">
            <div class="invoice-hdr userdetails  mb-0 pb-0 pt-0">
            <div class="row mb-0">
              <table width="" class="col-md-4 pl-0" style="display:inline-block;" border="0" cellspacing="0" cellpadding="5">
                <tr>
				<c:choose>
				<c:when test='${not empty qrcodeid}'>
                 <td width="30%"> <img src="${contextPath}/getlogo/${qrcodeid}" width="160" height="160"  alt="qrcode"></td>
                </c:when>
                <c:when test="${invoice.invtype eq 'B2C'}">
                <c:if test="${not empty invoice.bankDetails && not empty invoice.bankDetails.accountnumber && not empty invoice.bankDetails.ifsccode}">
                 	<td width="30%"> <img src="${contextPath}/b2c/${invoice.id}/${client.id}/qrImage.jpg"  alt="qrcode"></td>
                 </c:if>
                </c:when>
				<c:otherwise>
					<c:if test='${not empty invoice.irnNo}'><td width="30%"> <img src="${contextPath}/${invoice.id}/qrImage.jpg"  alt="qrcode"></td></c:if>
				</c:otherwise>
				</c:choose>
                </tr>
              </table>
              <div class="col-md-6" style="display:inline-block;float:right;">
              <h3 class="text-right col-md-12 mt-3" style="float:right;">${einvoiceheaderText}</h3>
              <h4 class="text-right col-md-12 mt-3" style="float:right;">${invText} </h4>
              </div>
              </div>
            </div>
            <c:if test='${not empty invoice.irnNo}'><p class="mb-0">IRN No  </p><strong style="font-size:16px;">${invoice.irnNo}</strong></c:if>
            <div class="clearfix bdr-btm">&nbsp;</div>
            <!-- invoice header start -->
            <div class="invoice-hdr userdetails">
              <div class="row m-0">
			 <div class="ml-2">
			<c:choose><c:when test="${(not empty invoice.billedtoname) && (not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) && (not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1)}"></c:when><c:otherwise><h3>${client.businessname}</h3></c:otherwise> </c:choose>
			</div>
                <table width="100%" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td width="60%" style="vertical-align:top"><table width="100%" border="0" cellspacing="0" cellpadding="5" class="compdetails">
                        <tr>
                          <td valign="top">
						 <c:choose><c:when test="${(not empty invoice.billedtoname) && (not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) && (not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1)}"></c:when><c:otherwise> <p class="m-1"><strong>GSTIN<span style="margin-left:64px">:</span></strong> ${client.gstnnumber}</p></c:otherwise></c:choose>
							<p class="m-1"><strong>Ack.No<span style="margin-left:60px">:</span> </strong>${ackno}</p>
							<p class="m-1"><strong>Ack.Date<span style="margin-left:52px">:</span> </strong>${ackdt}</p>
							<p class="m-1"><strong>IGST on Intra<span style="margin-left:33px">:</span> </strong>${igstonintra}</p>
							 <p class="m-1"><strong>Supply Type Code<span style="margin-left:10px">:</span> </strong>${supply}</p>
							<c:choose><c:when test="${(not empty invoice.billedtoname) && (not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) && (not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1)}"></c:when>
                            <c:otherwise>
                            <c:if test="${empty invoice.branch}"><p class="m-1"><strong>Address<span style="margin-left:56px">:</span> </strong><c:choose><c:when test="${empty invoice.clientAddress}">${client.address}</c:when><c:otherwise>${invoice.clientAddress}</c:otherwise></c:choose></p></c:if>
                            <c:if test='${not empty invoice.branch}'>
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${invoice.branch eq branchs.name}'>
										<p class="m-1"><strong>Address<span style="margin-left:56px">:</span></strong>${branchs.address}</p>	
									</c:if>
								</c:forEach>
							</c:if>
                            </c:otherwise>
                          </c:choose>
						</td>
                        </tr>
                      </table></td>
                    
                    <td width="40%" style="vertical-align:top"><table width="100%" border="0" cellspacing="0" cellpadding="5">
                     
                        <tr>
                          <td><div class="lable-txt text-right">${invoiceNumberText}</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11"><strong>${invoice.invoiceno}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">${invoiceDateText}</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11" id="invoicedate"><fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" /></span></td>
                        </tr>
                         <tr>
                          <td><div class="lable-txt text-right">Place Of Supply</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11" id="invoicedate">${invoice.statename}</span></td>
                        </tr>
                         <tr>
                          <td><div class="lable-txt text-right">State Code</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11" id="invoicedate">${istate_code}</span></td>
                        </tr>
						<c:if test="${not empty originalInvNo}">
                      	<tr>
                          <td><div class="lable-txt text-right">Original Inv.No</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11"><strong>${originalInvNo}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">Original Inv.Date</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-11"><strong>${originalInvDate}</strong></span></td>
                        </tr>
                        </c:if>
                      </table></td>
                  </tr>
                </table>
                <div class="clearfix">&nbsp;</div>
                
                <c:choose>
                 <c:when test="${(not empty invoice.billedtoname) && (not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) && (not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1)}">
	                <table width="100%" border="0" cellspacing="0" cellpadding="0" class="disp_address">
	                <tr>
	                <td width="50%"class="comp-addres"><h3><strong>Details Of Seller </strong></h3>
								<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;">${client.businessname}</strong></p>
								<p>GSTIN:<strong>${client.gstnnumber}</strong></p>
			                    <c:if test="${empty invoice.branch}"><p class=""><c:choose><c:when test="${empty invoice.clientAddress}">${client.address}</c:when><c:otherwise>${invoice.clientAddress}</c:otherwise></c:choose></p></c:if>
                            <c:if test='${not empty invoice.branch}'>
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${invoice.branch eq branchs.name}'>
										<p class="address_data">${branchs.address}</p>	
									</c:if>
								</c:forEach>
							</c:if>
					</td>
					<td width="50%" class="comp-addres" style="vertical-align:baseline;"><h3><strong>Details Of Buyer </strong></h3>
								<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;">${invoice.billedtoname}</strong></p>
								<p>GSTIN:<strong>${invoice.b2b[0].ctin}</strong></p>
								<p><strong>${theatreName}</strong></p>
			                    <p class="address_data" style="margin-bottom: 3px;">${custAddress}</p>  
					</td>
	                </tr>
	                  <tr>
						    <td width="50%"class="comp-addres" style="vertical-align:baseline;"><h3><strong>Details Of Dispatcher </strong></h3>
								<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;">${c_disp_name}</strong></p>
						  		<p class="address_data"><strong>${c_disp_address}</strong></p>
						   </td>
						 <td width="50%"class="comp-addres"><h3><strong>Details Of Shipment </strong></h3>
								<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;">${c_ship_name}</strong></p>
								<p>GSTIN:<strong>${c_ship_gstin}</strong></p>
						  		<p class="address_data"><strong>${c_ship_address}</strong></p>
						   </td>
	                  </tr>
	                </table>
                </c:when>
                <c:otherwise>
                 <table width="100%" border="0" cellspacing="0" cellpadding="0" class="">
	                  <tr>
						    <td width="50%"class="comp-addres"><h3><strong>Details Of Receiver(Billed To) </strong></h3>
								<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;">${invoice.billedtoname}</strong></p>
								<p>GSTIN:<strong>${invoice.b2b[0].ctin}</strong></p>
								<p><strong>${theatreName}</strong></p>
			                    <p class="address_data" style="margin-bottom: 3px;">${custAddress}</p>  
						   </td>
						 <td <c:if test="${(not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) || not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1}">width="5%"</c:if> <c:if test="${empty invoice.shipmentDtls || empty invoice.dispatcherDtls}">width="20%"</c:if>>&nbsp;</td>
						<c:if test="${(not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) || not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1}">
	                    <td width="47%"  class="comp-addres" style="vertical-align:top"><h3><strong>${headText}</strong></h3>
	                      <p style="font-size: 18px;">${c_ship_name}</p>
	                      <c:if test="${not empty c_ship_gstin}"><p>GSTIN:<strong>${c_ship_gstin}</strong></p></c:if>
						  <p class="address_data">${c_ship_address}</p></td>
						</c:if>
	                  </tr>
	                </table>
                </c:otherwise>
                </c:choose>
              </div>
            </div>
            <!-- end -->
            
             <table width="100%" border="0" cellspacing="0" cellpadding="0">
				<c:choose>
					<c:when test="${not empty invoice.entertaimentprintto && invoice.entertaimentprintto eq 'Producer'}">
						<tr>
							<td width="20%"><div class="lable-txt">${CustomField1}</div></td>
							<td width="">:</td>
							<td width=""><span class="f-11">${CustomField1Val}</span></td>
						</tr>
					</c:when>
					<c:otherwise>
						<c:if test="${not empty invoice.notes}">
						<tr>
                          <td colspan="6"><span class="f-11">${invoice.notes}</span></td>
                        </tr>
					</c:if>
					<c:forEach items="${customFieldList}" var="customFieldList" varStatus="loop">
							<tr>
								<c:if test="${customFieldList.displayInPrint eq true}">
								<c:if test="${empty customFieldList.customFieldName}"><td width="20%"><div class="lable-txt">Custom Field ${loop.index+1}</div></td></c:if><c:if test="${not empty customFieldList.customFieldName}"><td width="20%"><div class="lable-txt">${customFieldList.customFieldName}</div></td></c:if>
								  <td width="">:</td>
								  <c:choose>
								  <c:when test="${loop.index+1 eq '1'}">
								  <td width=""><span class="f-11">${invoice.customField1}</span></td>
								  </c:when>
								  <c:when test="${loop.index+1 eq '2'}">
								  <td width=""><span class="f-11">${invoice.customField2}</span></td>
								  </c:when>
								  <c:when test="${loop.index+1 eq '3'}">
								  <td width=""><span class="f-11">${invoice.customField3}</span></td>
								  </c:when>
								  <c:otherwise>
								  <td width=""><span class="f-11">${invoice.customField4}</span></td>
								  </c:otherwise>
								  </c:choose>
								  </c:if>
							</tr>
					</c:forEach>
					</c:otherwise>
				</c:choose>             
        			 
			</table>	
            <!-- itemdetailsblock-wrap start -->
            <div class="itemdetailsblock-wrap">
              <!-- table start -->
			  <c:set var="iAmt" value="0" />
			  <c:set var="cAmt" value="0" />
			  <c:set var="sAmt" value="0" />
			   <c:forEach items="${invoice.items}" var="item" varStatus="loop">
			  <c:set var="iAmt" value="${iAmt + item.igstamount}" />
			  <c:set var="cAmt" value="${cAmt + item.cgstamount}" />
			  <c:set var="sAmt" value="${sAmt + item.sgstamount}" />
			  <c:set var="csAmt" value="${csAmt + item.isdcessamount}" />
			  </c:forEach>
              <div class="customtable invoicetable">
                <table id="invoicetable" class="display row-border dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
                  <thead>
                    <tr>
                      <th style="width:2%">Sr.No</th>
                      <th style="width:25%">Item / Service Description</th>
                      <th style="width:5%">HSN/SAC</th>
                      <th class="textright" >Taxable Value</th>
					 <c:if test="${iAmt ne 0}">
					  <th class="textright">IGST Amt</th>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <th class="textright">CGST Amt</th>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <th class="textright">SGST Amt</th>
                      </c:if>
                      <th class="textright" style="width:14.4%">Total Amount</th>
					  <c:if test="${not empty currencyTotal}">
					  <th class="textright" style="width:14.4%">${invoice.addcurrencyCode}</th>
					  </c:if>
                    </tr>
                  </thead>
                  <tbody>
                  <c:forEach items="${invoice.items}" var="item" varStatus="loop">
                  <c:set var="aAmt" value="${item.rateperitem}" />
                    <tr>
                      <td style="font-size:12px;"><div align="center">${loop.index + 1}</div></td>
                       <td style="font-size:12px;">${item.itemno}</td>
                      <td style="font-size:12px;" align="center"><c:choose><c:when test="${fn:contains(item.hsn, ' :')}">${fn:substring(item.hsn, 0, fn:indexOf(item.hsn, " :"))}</c:when><c:otherwise>${item.hsn}</c:otherwise></c:choose></td>
                      <td  style="font-size:12px;"><div align="right"><span class="indformat">${item.taxablevalue}</span></div></td>
                      <c:if test="${iAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.igstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.igstrate}" />%)</td>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.cgstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.cgstrate}" />%)</td>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.sgstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.sgstrate}" />%)</td>
                      </c:if>
                       <td class="fnt"><div align="right"> <span class="indformat">${item.total}</span></div></td>
					   <c:if test="${not empty currencyTotal}">
					   <td class="fnt"><div align="right"><fmt:formatNumber type = "number"  maxFractionDigits="2" value = "${item.currencytotalAmount}" /></div></td>
					   </c:if>
                      </tr>
                  </c:forEach>
					
                  </tbody>
                  <tfoot>
                     <tr>
					  
					  <th colspan="1" class="text-right whit_bg">&nbsp;</th>
					  <th class="footerredbg fnt text-right" colspan="2">Total Amount</th>
                        <th class="footerredbg fnt text-right"><span class="indformat">${invoice.totaltaxableamount}</span></th>
                     <c:if test="${iAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${iAmt}</span></th>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${cAmt}</span></th>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${sAmt}</span></th>
                      </c:if>
                      <c:choose>
                      	<c:when test="${not empty invoice.notroundoftotalamount}">
                      		<th  class="footerredbg fnt text-right"><div align="right"><span class="indformat">${invoice.notroundoftotalamount}</span></div></th>
                      	</c:when>
                      	<c:otherwise>
                      		<th  class="footerredbg fnt text-right"><div align="right"><span class="indformat">${invoice.totalamount}</span></div></th>
                      	</c:otherwise>
                      </c:choose>
                       
					   <c:if test="${not empty currencyTotal}">
					   <th  class="footerredbg fnt text-right"><div align="right"><fmt:formatNumber type = "number"  maxFractionDigits="2" value = "${invoice.totalCurrencyAmount}" /></div></th>
					   </c:if>
                    </tr>
                  </tfoot>
                </table>
                <table id="invoicetable1" class="display dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
               
                  <tr>
                  
                    <td width="60%" height="19" style="font-size:12px;"><div align="right"><strong>Taxable Amount</strong></div></td>
                    <td width="10%" style="font-size:12px;"><div align="right"><span class="indformat">${invoice.totaltaxableamount}</span></div></td>
                    
                  </tr>
                   <tr>
                    <td  width="60%" style="font-size:12px;"><div align="right"><strong>Total Tax</strong></div></td>
                    <td  width="10%" style="font-size:12px;"><div align="right"><span class="indformat">${invoice.totaltax}</span></div></td>
                  </tr>
                  <tr>
                    <td style="font-size:12px;"><div align="right"><strong>Invoice Total</strong></div></td>
                    <td>
                    	<c:choose>
                    	<c:when test="${not empty pconfig && pconfig.enableRoundOffAmt eq false && not empty invoice.roundOffAmount && invoice.roundOffAmount ne 0}">
                    		<div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.totalamount}</span></strong></div>
                    	</c:when>
                      	<c:when test="${not empty invoice.notroundoftotalamount}">
                      		<div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.notroundoftotalamount}</span></strong></div>
                      	</c:when>
                      	<c:otherwise>
                      		<div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.totalamount}</span></strong></div>
                      	</c:otherwise>
                      </c:choose>
                    
                    
                    </td>
                  </tr>
				  <c:if test="${not empty currencyTotal}">
				  <tr>
                    <td style="font-size:12px;"><div align="right"><strong>Currency Total</strong></div></td>
                    <td><div align="right" style="font-size: 12px!important;">${currencyTotal}</strong></div></td>
					
                  </tr>
				  </c:if>
                    <c:if test="${invoice.tdstcsenable eq true}">
					   <tr>
					    <td style="font-size:12px;"><div align="right"><strong>TCS Amount</strong></div></td>
	                    <td><div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.tcstdsAmount}</span></strong></div></td>
	                  </tr>
	                   <tr>
	                    <td style="font-size:12px;"><div align="right"><strong>Net Receivable(Total Invoice Value + TCS Amount)</strong></div></td>
	                    <td><div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.netAmount}</span></strong></div></td>
	                  </tr>
                  </c:if>
                  <c:if test="${not empty pconfig && pconfig.enableRoundOffAmt eq true}">
                     <tr>
					    <td style="font-size:12px;"><div align="right"><strong>Round Off Amount</strong></div></td>
	                    <td><div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.roundOffAmount}</span></strong></div></td>
	                  </tr>
					  <tr>
					    <td style="font-size:12px;"><div align="right"><strong>Round Total</strong></div></td>
	                    <td><div align="right" style="font-size: 12px!important;"><strong><i class="fa fa-rupee" style="font-size: 12px!important;"></i><span class="indformat">${invoice.totalamount}</span></strong></div></td>
	                  </tr>
                  </c:if>
                </table>
				<p style="font-size:12px;margin-top:5px; text-align:right">${amountinwords}</p>
				<c:if test="${not empty currencyTotal}">
				<p style="font-size:12px;margin-top:5px; text-align:right">${amountcurrency}</p>
				</c:if>
              </div>
              <!-- table end -->
			  <div class="row m-0">
                <table width="100%" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td><table width="100%" border="0" cellspacing="0" cellpadding="1">
					
					  <c:if test="${not empty invoice.bankDetails && not empty invoice.bankDetails.accountnumber && not empty invoice.bankDetails.bankname}">
                        <tr>
                          <c:if test="${not empty invoice.bankDetails.accountName}">
						<tr>
                          <td width=""><div class="lable-txt">Account Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-11">${invoice.bankDetails.accountName}</span></td>
                        </tr>
						</c:if>
                          <td width=""><div class="lable-txt">Bank Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-11">${invoice.bankDetails.bankname}</span></td>
                           <td width=""><div class="lable-txt">Account Number</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-11">${invoice.bankDetails.accountnumber}</span></td>
                        </tr>
						
						<tr>
                          <td width=""><div class="lable-txt">Branch Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-11">${invoice.bankDetails.branchname}</span></td>
                          <td width=""><div class="lable-txt">IFSC Code</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-11">${invoice.bankDetails.ifsccode}</span></td>
                        </tr>
						
                        
						</c:if>
                      </table></td>
                  </tr>
                </table>
            </div>
            <!-- itemdetailsblock-wrap end -->
          </form>
        </div>
		<!--<c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">
		<p style="text-align: center; padding-top: 50px; margin-bottom: -20px;">This is a computer generated invoice and does not need signature.</p>
		</c:if>-->
		<div class="page-footer">
		<c:if test="${client.digitalSignOn eq true && not empty client.signid}">
		<div class="imgsize-wrap-thumb"  style="border-radius:0px;right:0px;left:90%;max-width:120px;width:80px;border:none;"><img src="${contextPath}/getlogo/${client.signid}" alt="Signature" class="imgsize-thumb" id="clntsigns" style="max-width:70%;border-radius:0px;"></div>
		</c:if>
		<strong style="">Generated By  :  ${client.gstnnumber}</strong>
		<strong style="display:block;">Printed Date    :  ${printDate}</strong>
		<c:if test="${client.enableAuthorisedSignatory eq true}">
		<div class="doc-foot">
		<span style="float:right"><strong>${signatureText}</strong></span><br>
		<div class="pull-right col-md-12 p-0"><strong style="float: right;">${client.authorisedSignatory}</strong></div>
		<div class="pull-right col-md-12 p-0"><strong style="float: right;">${client.designation}</strong></div>
		</div>
		</c:if>
		<c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">
			<p style="text-align: center; padding-top: 19px; margin-bottom: -20px;">This is a computer generated invoice and does not need signature.</p>
		</c:if>
		
		<%-- <c:if test="${not empty pconfig && pconfig.isfooternotescheck eq true}">
		<p style="text-align: center; padding-top: 18px; margin-bottom: -20px;">${pconfig.footernotes}</p>
		</c:if> --%>
      </div>
	  </div>
      <!-- inovice wrap end -->
    </div>

    <!-- dashboard left block end -->
  </div>
  <!-- Dashboard body end -->
  
</div>
<!-- wrap end -->
<script>
OSREC.CurrencyFormatter.formatAll({
			selector: '.indformat'
		});
</script>
	
<!-- body end -->
</body>


</html>