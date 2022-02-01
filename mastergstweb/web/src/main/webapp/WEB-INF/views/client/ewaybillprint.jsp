<%@include file="/WEB-INF/views/includes/taglib.jsp"%>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml"
	xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Eway Bill Invoice</title>
<%@include file="/WEB-INF/views/includes/common_script.jsp"%>
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/dashboard/dashboards.css"	media="all" />
<link rel="stylesheet"	href="${contextPath}/static/mastergst/css/print/print.css"	media="all" />
<script src="${contextPath}/static/mastergst/js/print/jQuery.print.js"	type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/print/html2pdf.bundle.min.js"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<style>
.h1, .h2, .h3, .h4, .h5, .h6, h1, h2, h3, h4, h5, h6{font-family:'source_sans_proregular',sans-serif!important;}
/* .page-footer{position: absolute;width: 93%;    bottom: 33px;} */
/* .itemdetailsblock-wrap{margin-bottom: 50px;} */
.disp_address{border:1px solid}
.disp_address td{border:none!important}
.address_data{word-break: break-all;}
</style>
<script type="text/javascript">
$(document).ready(function() {
	var invoicetype = '<c:out value="${invoice.invtype}"/>';
	$("div.supplierwrap").hide();
	$(".printfor input[type='radio']").click(function() {
	 	 if($(this).attr('id') == 'supplier') {
             $("div.supplierwrap").show();         
       }
       else {
              $("div.supplierwrap").hide();
       }
	});
});
function downloadPDFs() {
	var element = document.getElementById('print-wrap');
	var clientname = '<c:out value="${client.businessname}"/>';	
	var dateofInvoice = $('#invoicedate').text();	
	var invoiceno = '<c:out value="${invoice.invoiceno}"/>';
	var pdffilename = clientname.concat(invoiceno,dateofInvoice);
	const options = {
	    margin: [0.25, 0.15,0.25,-0.15],
		fontSize:9,
	    image: {
	        type: 'png',
	        quality: 0.98  
	    },
	    html2canvas: {
	        dpi: 192,
			 width: 850,
			 scale: 2 ,
			 letterRendering: true
	    },
	    jsPDF: {
	        unit: 'in',
	        format: 'a4',
	        orientation: 'portrait',
			autotable: true
	    },
		filename:pdffilename
	}

	html2pdf(element, options);
}
 
</script>
</head>
<body>
<!-- body start -->
	<div class="nav-heder"><a class="navbar-brand-logo" href="http://mastergst.com"> <img src="${contextPath}/static/mastergst/images/master/logo-mastergst.png" style="width:200px" alt="Master Gst" /> </a></div>
<div class="container">
 
  <!-- Dashboard body start -->
  <div class="row">
    <!-- dashboard left block begin -->
    <div class="col-sm-12">
      <div class="print-tools mb-2">
         <a href="javascript:window.close()" class="print-link btn-print">CLOSE</a> 
		 <a href="${contextPath}/ewaybillprintpdf/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${invoice.id}" class="print-link btn-print mr-2">Generate PDF</a>
		 <a href="javascript:void()" onClick="jQuery('#print-wrap').print();" class="print-link btn-print mr-2">PRINT</a></div>
      <div class="invoice-wrap" id="print-wrap">
        <div class="invoice-wrap-in pt-0 mt-0" style="height: 1000px;max-height: max-content;">
          <form class="meterialform invoiceform" style="min-height: 725px;height: auto;">
            <div class="invoice-hdr userdetails mb-0 pb-0 pt-0">
             <div class="row mb-0">
             <table width="" class="col-md-4 pl-0" style="display:inline-block;" border="0" cellspacing="0" cellpadding="5">
              <tr>
                 <c:if test='${not empty invoice.ewayBillNumber}'><td width="30%"> <img src="${contextPath}/${invoice.ewayBillNumber}/qr_image.jpg"  alt="qrcode"></td></c:if>
               </tr>
              </table>
              <span style="font-size:24px;vertical-align: sub;">EWay Bill</span>
              <div class="col-md-6" style="display:inline-block;float:right;">
              <c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize" style="width:150px;float:right;position:relative;"></c:if>
             <c:if test="${empty client.logoid}"> <img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize col-md-12" style="width:150px;float:right;position:relative;"></c:if>
              <div class="text-right col-md-12 pr-0" style="float:right;">
	              <p class="m-1"><span style="margin-right:36px;">Document Type   :</span><span class="">${invText}</span></p>
		           <p class="m-1"><span style="margin-right:3px;">Document No  :</span><span class="">${invoice.invoiceno}</span></p>
	              <p class="m-1"><span style="margin-right:34px;">Document Date  :</span><span class=""><fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" /></span></p>
              </div>
              </div>
                 <%--  <strong class="lable-txt">Document Type<span style="margin-left:18px">:</span>${invText}</strong><br/><strong class="lable-txt">Document No<span style="margin-left:27px">:</span>${invoice.invoiceno}</strong><br/><strong class="lable-txt">Document Date<span style="margin-left:18px">:</span><fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" /></strong> --%>
             
              </div>
            </div>
            <div class="clearfix bdr-btm">&nbsp;</div>
            <!-- invoice header start -->
            <div class="invoice-hdr userdetails">
              <div class="row m-0">
			  <div class="ml-2"><h3>${client.businessname}</h3></div>
                <table width="100%" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td style="vertical-align:top;width:60%;">
                    <table width="80%" border="0" cellspacing="0" cellpadding="5" class="compdetails">
                        <tr >
                          <td><div class="lable-txt text-left" style="width:40%;text-align:left;">Eway Bill No</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class="">${invoice.ewayBillNumber}</span></td>
                        </tr> 
                           <tr >
                          <td><div class="lable-txt text-left" style="width:40%;text-align:left;">Eway Bill Date</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class=""><fmt:formatDate value="${invoice.eBillDate}" pattern="dd/MM/yyyy" /></span></td>
                        </tr> 
                        <tr >
                          <td><div class="lable-txt text-left" style="width:40%;text-align:left;">Valid Upto</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class="">${invoice.validUpto}</span></td>
                        </tr> 
                        <tr >
                          <td><div class="lable-txt text-left" style="text-align:left;">Actual Distance</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class=""><c:choose><c:when test="${not empty invoice.actualDist}"></c:when><c:otherwise>${invoice.transDistance}</c:otherwise></c:choose></span></td>
                        </tr> 
                        <tr >
                          <td><div class="lable-txt text-left" style="width:40%;text-align:left;">GSTIN</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class="">${client.gstnnumber}</span></td>
                        </tr> 
                         <tr >
                          <td><div class="lable-txt text-left" style="width:40%;text-align:left;">State Code</div></td>
                          <td>:</td>
                          <td style="text-align: left;width:55%;"><span class="">${client.statename}</span></td>
                        </tr>           
                      </table></td>
                    
                    <td width="40%" style="vertical-align:top"><table width="100%" border="0" cellspacing="0" cellpadding="5">
                          <tr>
                          <td><div class="lable-txt text-right">Supply Type</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class=""><strong>${supplyType}</strong></span></td>
                        </tr> 
                         <tr>
                          <td><div class="lable-txt text-right">SubSupply Type</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class=""><strong>${SubSupplyType}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">Transaction Type</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class=""><strong>${transType}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">Transporter ID</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class=""><strong>${invoice.transporterId}</strong></span></td>
                        </tr>
						<tr><td style="text-align: right;"><strong>Transporter Name</td><td> :</td> </strong><td style="text-align: right;">${invoice.transporterName}</td>
						</tr>
                      </table></td>
                  </tr>
                </table>
                <div class="clearfix">&nbsp;</div>
				<c:choose>
                 <c:when test="${(not empty invoice.shipmentDtls && not empty invoice.shipmentDtls.addr1) && (not empty invoice.dispatcherDtls && not empty invoice.dispatcherDtls.addr1)}">
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
									<table id="invoicetable"
										class="display row-border dataTable meterialform"
										cellspacing="0" width="100%" style="font-size: 12px;">
										<thead>
											<tr>
												<th style="width: 2%">Sr.No</th>

												<th style="width: 25%">Item / Service Description</th>
												<th style="width: 5%">HSN/SAC</th>
												<th style="width: 5%">Quantity</th>
												<th class="textright">Rate</th>
												<th style="width: 5%">Discount</th>
												<th class="textright">Taxable Value</th>
												<c:if test="${iAmt ne 0}">
													<th class="textright">IGST Amt</th>
												</c:if>
												<c:if test="${cAmt ne 0}">
													<th class="textright">CGST Amt</th>
												</c:if>
												<c:if test="${sAmt ne 0}">
													<th class="textright">SGST Amt</th>
												</c:if>
												<th class="textright" style="width: 14.4%">Total Amount</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${invoice.items}" var="item"
												varStatus="loop">
												<c:set var="aAmt" value="${item.rateperitem}" />
												<tr>
													<td style="font-size: 12px;"><div align="center">${loop.index + 1}</div></td>
													<td style="font-size: 12px;">${item.itemno}</td>
													<td style="font-size: 12px;" align="center"><c:choose>
															<c:when test="${fn:contains(item.hsn, ' :')}">${fn:substring(item.hsn, 0, fn:indexOf(item.hsn, " :"))}</c:when>
															<c:otherwise>${item.hsn}</c:otherwise>
														</c:choose></td>
													<td align="center" style="font-size: 12px;">${item.quantity}</td>
													<td class="textright" style="font-size: 12px;"><span
														class="indformat">${item.rateperitem}</span></td>
													<td class="textright" style="font-size: 12px;"><span
														class="indformat">${item.discount}</span></td>
													<td style="font-size: 12px;"><div align="right">
															<span class="indformat">${item.taxablevalue}</span>
														</div></td>
													<c:if test="${iAmt ne 0}">
														<td class="textright"><span class="indformat">${item.igstamount}</span><br>@(<fmt:formatNumber
																type="number" maxFractionDigits="3"
																value="${item.igstrate}" />%)</td>
													</c:if>
													<c:if test="${cAmt ne 0}">
														<td class="textright"><span class="indformat">${item.cgstamount}</span><br>@(<fmt:formatNumber
																type="number" maxFractionDigits="3"
																value="${item.cgstrate}" />%)</td>
													</c:if>
													<c:if test="${sAmt ne 0}">
														<td class="textright"><span class="indformat">${item.sgstamount}</span><br>@(<fmt:formatNumber
																type="number" maxFractionDigits="3"
																value="${item.sgstrate}" />%)</td>
													</c:if>
													<td class="fnt"><div align="right">
															<span class="indformat">${item.total}</span>
														</div></td>
												</tr>
											</c:forEach>
										</tbody>
										<tfoot>
											<tr>

												<th colspan="4" class="text-right whit_bg">&nbsp;</th>
												<th class="footerredbg fnt text-right" colspan="2">Total
													Amount</th>
												<th class="footerredbg fnt text-right"><span
													class="indformat">${invoice.totaltaxableamount}</span></th>
												<c:if test="${iAmt ne 0}">
													<th class="footerredbg fnt text-right"><span
														class="indformat">${iAmt}</span></th>
												</c:if>
												<c:if test="${cAmt ne 0}">
													<th class="footerredbg fnt text-right"><span
														class="indformat">${cAmt}</span></th>
												</c:if>
												<c:if test="${sAmt ne 0}">
													<th class="footerredbg fnt text-right"><span
														class="indformat">${sAmt}</span></th>
												</c:if>
												<th class="footerredbg fnt text-right"><div
														align="right">
														<span class="indformat">${invoice.totalamount}</span>
													</div></th>
											</tr>
										</tfoot>
									</table>
									<table id="invoicetable1"
										class="display dataTable meterialform" cellspacing="0"
										width="100%" style="font-size: 12px;">
										<tr>
											<td width="60%" height="19" style="font-size: 12px;"><div
													align="right">
													<strong>Taxable Amount</strong>
												</div></td>
											<td width="10%" style="font-size: 12px;"><div
													align="right">
													<span class="indformat">${invoice.totaltaxableamount}</span>
												</div></td>
										</tr>
										<tr>
											<td width="60%" style="font-size: 12px;"><div
													align="right">
													<strong>Total Tax</strong>
												</div></td>
											<td width="10%" style="font-size: 12px;"><div
													align="right">
													<span class="indformat">${invoice.totaltax}</span>
												</div></td>
										</tr>
										<tr>
											<td style="font-size: 12px;"><div align="right">
													<strong>Invoice Total</strong>
												</div></td>
											<td><div align="right"
													style="font-size: 12px !important;">
													<strong><i class="fa fa-rupee"
														style="font-size: 12px !important;"></i><span
														class="indformat">${invoice.totalamount}</span></strong>
												</div></td>
										</tr>

									</table>
									<p style="font-size: 12px; margin-top: 5px; text-align: right">${amountinwords}</p>
									<p
										style="font-size: 14px; margin-bottom: 0px; text-decoration: underline;">PART-B/Vehicle
										Details</p>
									<table id="vehtable"
										class="display row-border dataTable meterialform"
										cellspacing="0" width="100%" style="font-size: 12px;">
										<thead>
											<tr>
												<th style="width: 2%">Sr.No</th>
												<th style="width: 25%">Vehicle No</th>
												<th style="width: 25%">From Place</th>
												<th style="width: 25%">Entered Date</th>
												<th style="width: 5%">Transport Mode</th>
												<th style="width: 2%">Vehicle Type</th>
												<th style="width: 25%">Transport DocNo</th>
												<th style="width: 5%">Transport DocDate</th>
											</tr>
										</thead>
										<tbody>
											<c:forEach items="${invoice.vehiclListDetails}" var="vehicle"
												varStatus="loop">

												<tr>
													<td style="font-size: 12px;"><div align="center">${loop.index + 1}</div></td>
													<td style="font-size: 12px;">${vehicle.vehicleNo}</td>
													<td style="font-size: 12px;">${vehicle.fromPlace}</td>
													<td style="font-size: 12px;">${vehicle.enteredDate}</td>
													<td style="font-size: 12px;">${transMode}</td>
													<td style="font-size: 12px;">${vehicleType}</td>
													<td style="font-size: 12px;">${vehicle.transDocNo}</td>
													<td style="font-size: 12px;">${vehicle.transDocDate}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
								<div class="row m-0">
									<table width="100%" border="0" cellspacing="0" cellpadding="5">
										<c:forEach items="${customFieldList}" var="customFieldList"
											varStatus="loop">
											<tr>
												<c:if test="${customFieldList.displayInPrint eq true}">
													<c:if test="${empty customFieldList.customFieldName}">
														<td width="20%"><div class="lable-txt">Custom
																Field ${loop.index+1}</div></td>
													</c:if>
													<c:if test="${not empty customFieldList.customFieldName}">
														<td width="20%"><div class="lable-txt">${customFieldList.customFieldName}</div></td>
													</c:if>
													<td width="">:</td>
													<c:choose>
														<c:when test="${loop.index+1 eq '1'}">
															<td width=""><span class="f-14">${invoice.customField1}</span></td>
														</c:when>
														<c:when test="${loop.index+1 eq '2'}">
															<td width=""><span class="f-14">${invoice.customField2}</span></td>
														</c:when>
														<c:when test="${loop.index+1 eq '3'}">
															<td width=""><span class="f-14">${invoice.customField3}</span></td>
														</c:when>
														<c:otherwise>
															<td width=""><span class="f-14">${invoice.customField4}</span></td>
														</c:otherwise>
													</c:choose>
												</c:if>
											</tr>
										</c:forEach>
									</table>
									<!-- table end -->
									<!-- itemdetailsblock-wrap end -->
						</form>
					</div>
					<div class="page-footer">
						<p
							style="text-align: center; padding-top: 50px; margin-bottom: -20px;">This
							is a computer generated invoice and does not need signature.</p>
					</div>
				</div>
				<!-- inovice wrap end -->
			</div>
		</div>
	</div>
	<!-- wrap end -->
	<script>
		OSREC.CurrencyFormatter.formatAll({
			selector : '.indformat'
		});
</script>
	
<!-- body end -->
</body>


</html>