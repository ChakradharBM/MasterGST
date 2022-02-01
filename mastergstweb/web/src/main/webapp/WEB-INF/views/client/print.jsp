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
/* .page-footer{position: absolute;width: 93%;    bottom: 33px;} */
/* .itemdetailsblock-wrap{margin-bottom: 50px;} */
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
  <!-- header page begin -->
  <!-- header page end -->
  <!-- Dashboard body start -->
  <div class="row">
    <!-- dashboard left block begin -->
    <div class="col-sm-12">
      <div class="print-tools mb-2">
         <a href="javascript:window.close()" class="print-link btn-print">CLOSE</a> 
		 <a href="${contextPath}/invprintpdf/${id}/${fullname}/${usertype}/${client.id}/${returntype}/${invoice.id}" class="print-link btn-print mr-2">Generate PDF</a>
		 <a href="javascript:void()" onClick="jQuery('#print-wrap').print();" class="print-link btn-print mr-2">PRINT</a></div>
      <div class="invoice-wrap" id="print-wrap">
        <div class="invoice-wrap-in" style="height: 1000px;max-height: max-content;">
          <form class="meterialform invoiceform" style="min-height: 725px;height: auto;">
            <div class="invoice-hdr userdetails">
              <table width="100%" border="0" cellspacing="0" cellpadding="5">
                <tr>
                  <td width="50%" class="imgsize-wrap"><c:if test="${not empty client.logoid}"><img src="${contextPath}/getlogo/${client.logoid}" alt="Logo" class="imgsize"></c:if>
				  <c:if test="${empty client.logoid}"><img src="${contextPath}/static/mastergst/images/master/defaultcompany.png" alt="Logo" class="imgsize"></c:if></td>
                  <td width="50%"><h2 class="text-right">${invoiceText} </h2><br><h6 class="text-right">${reverseChargeText}</h6><br><h6 class="text-right">${sezText}</h6></td>
                </tr>
              </table>
            </div>
            <div class="clearfix bdr-btm">&nbsp;</div>
            <!-- invoice header start -->
            <div class="invoice-hdr userdetails">
              <div class="row m-0">
			  <div class="ml-2"><h3><c:choose><c:when test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}">${client.businessname}</c:when><c:otherwise>${invoice.billedtoname}</c:otherwise></c:choose></h3></div>
                <table width="100%" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td width="60%" style="vertical-align:top"><table width="100%" border="0" cellspacing="0" cellpadding="5" class="compdetails">
                        <tr>
                          <td valign="top">
						  <p class="m-1"><strong>GSTIN<span style="margin-left:47px">:</span></strong><c:choose><c:when test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}"> ${client.gstnnumber}</c:when><c:otherwise>${invoice.b2b[0].ctin}</c:otherwise></c:choose></p>
							<c:if test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}"><c:if test="${empty pconfig || pconfig.enablePan eq true}"><p class="m-1"><strong>PAN<span style="margin-left:56px">:</span> </strong>${client.pannumber}</p></c:if></c:if>
							<p class="m-1"><strong>CIN<span style="margin-left:59px">:</span> </strong>${client.cinNumber}</p>
							<c:if test='${not empty client.msmeNo}'><p class="m-1"><strong>MSME No<span style="margin-left:33px">:</span> </strong>${client.msmeNo}</p></c:if>
							 <c:if test='${not empty invoice.lutNo}'><p class="m-1"><strong>LUT No<span style="margin-left:42px">:</span> </strong>${invoice.lutNo}</p></c:if>
                            <c:if test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}"><c:if test="${empty invoice.branch}"><p class="m-1"><strong>Address<span style="margin-left:39px">:</span> </strong><c:choose><c:when test="${empty invoice.clientAddress}">${client.address}</c:when><c:otherwise>${invoice.clientAddress}</c:otherwise></c:choose></p></c:if>
                            <c:if test='${not empty invoice.branch}'>
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${invoice.branch eq branchs.name}'>
										<p class="m-1"><strong>Address<span style="margin-left:39px">:</span></strong>${branchs.address}</p>	
									</c:if>
								</c:forEach>
							</c:if>
							</c:if>
							<c:if test="${returntype eq 'Purchase Register' || returntype eq 'GSTR2' || returntype eq 'PurchaseOrder'}"><p class="m-1"><strong>Address<span style="margin-left:39px">:</span> </strong>${invoice.b2b[0].inv[0].address},${fn:substring(invoice.statename, fn:indexOf(invoice.statename, "-")+1, fn:length(invoice.statename))}</p></c:if>
						</td>
                        </tr>
                      </table></td>
                    
                    <td width="40%" style="vertical-align:top"><table width="100%" border="0" cellspacing="0" cellpadding="5">
                        <tr>
                          <td><div class="lable-txt text-right">${invoiceNumberText}</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-14"><strong>${invoice.invoiceno}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">${invoiceDateText}</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-14" id="invoicedate"><fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" /></span></td>
                        </tr>
						<tr><td style="text-align: right;"><c:if test="${empty pconfig || pconfig.enablePlaceOfSupply eq true}"><strong>Place Of Supply</td><td> :</td> </strong><td style="text-align: right;">${invoice.statename}</td></c:if>
						</tr>
						<tr><td style="text-align: right;"><c:if test="${empty pconfig || pconfig.enableState eq true}"><strong>State Code </td><td>:</td> </strong><td style="text-align: right;">${istate_code}</td></c:if></tr>
                      	<c:if test="${not empty originalInvNo}">
                      	<tr>
                          <td><div class="lable-txt text-right">Original Inv.No</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-14"><strong>${originalInvNo}</strong></span></td>
                        </tr>
                        <tr>
                          <td><div class="lable-txt text-right">Original Inv.Date</div></td>
                          <td>:</td>
                          <td style="text-align: right;"><span class="f-14"><strong>${originalInvDate}</strong></span></td>
                        </tr>
                        </c:if>
                      </table></td>
                  </tr>
                </table>
                <div class="clearfix">&nbsp;</div>
                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                  <tr>
                    <td <c:if test="${not empty invoice.consigneeaddress}">width="47%"</c:if> <c:if test="${empty invoice.consigneeaddress}">width="20%"</c:if> class="comp-addres"><h3><strong>Details Of Receiver(Billed To) </strong></h3>
					<p style="margin-top:5px;margin-bottom: 4px;"><strong style="font-size: 18px;"><c:choose><c:when test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}">${invoice.billedtoname}</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></strong></p>
					<p>GSTIN:<strong><c:choose><c:when test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}"> ${invoice.b2b[0].ctin}</c:when><c:otherwise>${client.gstnnumber}</c:otherwise></c:choose></strong></p>
                    <c:if test="${returntype eq 'Purchase Register' || returntype eq 'GSTR2' || returntype eq 'PurchaseOrder'}"><c:if test="${empty pconfig || pconfig.enablePan eq true}"><p class="m-1"><strong>PAN No <span style="margin-left:39px">:</span> </strong>${client.pannumber}</p></c:if></c:if>
                    <p style="margin-bottom: 3px;"> <c:if test="${returntype eq 'GSTR1' || returntype eq 'DELIVERYCHALLANS' || returntype eq 'PROFORMAINVOICES' || returntype eq 'ESTIMATES'}">${invoice.b2b[0].inv[0].address},${fn:substring(invoice.statename, fn:indexOf(invoice.statename, "-")+1, fn:length(invoice.statename))}</c:if>
			<c:if test="${returntype eq 'Purchase Register' || returntype eq 'GSTR2' || returntype eq 'PurchaseOrder'}"><c:if test="${empty invoice.branch}"><p class="m-1"><strong>Address<span style="margin-left:39px">:</span> </strong><c:choose><c:when test="${empty invoice.clientAddress}">${client.address}</c:when><c:otherwise>${invoice.clientAddress}</c:otherwise></c:choose></p></c:if>
                            <c:if test='${not empty invoice.branch}'>
								<c:forEach items="${client.branches}" var="branchs">
									<c:if test='${invoice.branch eq branchs.name}'>
										<p class="m-1"><strong>Address<span style="margin-left:39px">:</span></strong>${branchs.address}</p>	
									</c:if>
								</c:forEach>
							</c:if>
							</c:if>
                    </p>  
					   </td>
                    <td <c:if test="${not empty invoice.consigneeaddress}">width="5%"</c:if> <c:if test="${empty invoice.consigneeaddress}">width="20%"</c:if>>&nbsp;</td>
					<c:if test="${not empty invoice.consigneeaddress}">
                    <td width="47%"  class="comp-addres" style="vertical-align:top"><h3><strong>Details Of Consignee(Shipped To) </strong></h3>
                      <p>${invoice.consigneeaddress}</p></td>
					</c:if>
                  </tr>
                </table>
              </div>
            </div>
            <!-- end -->
            <c:if test="${not empty invoice.referenceNumber}">
			<label>Reference Number : </label><span>${invoice.referenceNumber}</span>
			</c:if>
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
                      <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <th style="width:25%">Item / Service Description</th>
                      <th style="width:5%">HSN/SAC</th>
                      </c:if>
                      <c:if test="${invoice.invtype eq 'ISD'  || invoice.invtype eq 'ITC Reversal'}">
                      <th style="width:25%">Particular Type</th>
                      </c:if>
                      <c:if test="${invoice.invtype eq 'Advances'}"><th style="width:5%;">Adv.amount</th></c:if>
                      <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD'  && invoice.invtype ne 'ITC Reversal'}">
                      <c:if test="${empty pconfig || pconfig.enableQuantity eq true}"><th style="width:5%"><c:choose><c:when test='${empty pconfig.qtyText || pconfig.qtyText eq ""}'>Qty</c:when><c:otherwise>${pconfig.qtyText}</c:otherwise></c:choose></th></c:if>
                      <c:if test="${empty pconfig || pconfig.enableRate eq true}"><th class="textright"><c:choose><c:when test='${empty pconfig.rateText || pconfig.rateText eq ""}'>Rate</c:when><c:otherwise>${pconfig.rateText}</c:otherwise></c:choose></th></c:if>
                      <c:if test="${empty pconfig || pconfig.enableDiscount eq true}"><th style="width:5%">Discount</th></c:if>
                      </c:if>
                    <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <th class="textright" >Taxable Value</th>
                      </c:if>
                      <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <c:if test="${iAmt ne 0}">
					  <th class="textright">IGST Amt</th>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <th class="textright">CGST Amt</th>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <th class="textright">SGST Amt</th>
                      </c:if>
                      </c:if>
                       <c:if test="${invoice.invtype eq 'ISD' || invoice.invtype eq 'ITC Reversal'}">
                       <th class="textright">IGST Amt</th>
                        <th class="textright">CGST Amt</th>
                        <th class="textright">SGST Amt</th>
                      <th class="textright">CESS Amt</th>
                     
                      </c:if>
                      <th class="textright" style="width:14.4%">Total Amount</th>
                    </tr>
                  </thead>
                  <tbody>
				  <c:forEach items="${invoice.items}" var="item" varStatus="loop">
				  <c:set var="aAmt" value="${item.rateperitem}" />
				    <tr>
                      <td style="font-size:12px;"><div align="center">${loop.index + 1}</div></td>
                       <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <td style="font-size:12px;">${item.itemno}</td>
                      <td style="font-size:12px;" align="center"><c:choose><c:when test="${fn:contains(item.hsn, ' :')}">${fn:substring(item.hsn, 0, fn:indexOf(item.hsn, " :"))}</c:when><c:otherwise>${item.hsn}</c:otherwise></c:choose></td>
                       </c:if>
                        <c:if test="${invoice.invtype eq 'ISD' && invoice.invtype eq 'ITC Reversal'}">
                        <td style="font-size:12px;"></td>
                        </c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><td align="center" style="font-size:12px;"><span class="indformat">${aAmt}</span></td></c:if>
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <c:if test="${empty pconfig || pconfig.enableQuantity eq true}"><td align="center" style="font-size:12px;">${item.quantity}</td></c:if>
                      <c:if test="${empty pconfig || pconfig.enableRate eq true}"><td class="textright" style="font-size:12px;"><span class="indformat">${item.rateperitem}</span></td></c:if>
                      <c:if test="${empty pconfig || pconfig.enableDiscount eq true}"><td class="textright" style="font-size:12px;"><span class="indformat">${item.discount}</span></td></c:if>
                      </c:if>
                      <c:if test="${invoice.invtype eq 'ISD'}"><td  style="font-size:12px;"><div align="right"></div>${item.isdType}</td></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal'}"><td  style="font-size:12px;"><div align="right"></div>${item.itcRevtype}</td></c:if>
                     <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><td  style="font-size:12px;"><div align="right"><span class="indformat">${item.taxablevalue}</span></div></td></c:if>
                      <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <c:if test="${iAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.igstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.igstrate}" />%)</td>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.cgstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.cgstrate}" />%)</td>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <td class="textright"><span class="indformat">${item.sgstamount}</span><br>@(<fmt:formatNumber type = "number"  maxFractionDigits="3" value = "${item.sgstrate}" />%)</td>
                      </c:if>
                      </c:if>
                       <c:if test="${invoice.invtype eq 'ISD' || invoice.invtype eq 'ITC Reversal'}">
                      <td class="textright"><span class="indformat">${item.igstamount}</span></td>
                      <td class="textright"><span class="indformat">${item.cgstamount}</span></td>
                      <td class="textright"><span class="indformat">${item.sgstamount}</span></td>
                       <td class="textright"><span class="indformat">${item.isdcessamount}</span></td>
                     
                      </c:if>
                      <td class="fnt"><div align="right"> <span class="indformat">${item.total}</span></div></td>
                    </tr>
				  </c:forEach>
                  </tbody>
                  <tfoot>
                    <tr>
					  <c:if test="${empty pconfig}">
					 	
					   <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <th colspan="4" class="text-right whit_bg">&nbsp;</th>
					  <th class="footerredbg fnt text-right" colspan="2">Total Amount</th>
					  </c:if>
					  </c:if>
					  <c:choose>
					  <c:when test="${not empty pconfig && pconfig.enableQuantity eq true && pconfig.enableDiscount eq true && pconfig.enableRate eq true}">
					  <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
					   <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
					   <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
					 <%--  <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if> --%> 
					  <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th class="footerredbg fnt text-right" colspan="2">Total Amount</th></c:if> <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th class="footerredbg fnt text-right" colspan="1">Total Amount</th></c:if>
					  </c:when>
					  <c:when test="${not empty pconfig && pconfig.enableQuantity eq false && pconfig.enableDiscount eq false && pconfig.enableRate eq false}">
					    <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="2" class="text-right whit_bg">&nbsp;</th></c:if>
					  <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
					  <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
					<%-- <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>  --%>
					  <th class="footerredbg fnt text-right">Total Amount</th>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq false && pconfig.enableQuantity eq true && pconfig.enableRate eq true)}">	
                       <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"> <th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
                        <%-- <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if> --%> 
					  <th class="footerredbg fnt text-right">Total Amount</th>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq true && pconfig.enableQuantity eq true && pconfig.enableRate eq false)}">	
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
                        <%-- <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg" >&nbsp;</th></c:if>  --%>
					  <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th class="footerredbg fnt text-right" colspan="2">Total Amount</th></c:if> <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th class="footerredbg fnt text-right" colspan="1">Total Amount</th></c:if>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq true && pconfig.enableQuantity eq false && pconfig.enableRate eq true)}">	
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
                       <%--  <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if> --%> 
					  <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th class="footerredbg fnt text-right" colspan="2">Total Amount</th></c:if> <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th class="footerredbg fnt text-right" colspan="1">Total Amount</th></c:if>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq true && pconfig.enableQuantity eq false && pconfig.enableRate eq false)}">	
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="2" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
                        <c:if test="${invoice.invtype eq 'ITC Reversal' ||invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
   					   <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th class="footerredbg fnt text-right" colspan="2">Total Amount</th></c:if> <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th class="footerredbg fnt text-right" colspan="1">Total Amount</th></c:if>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq false && pconfig.enableQuantity eq false && pconfig.enableRate eq true)}">	
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="3" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal' ||invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
                      <%-- <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="2" class="text-right whit_bg">&nbsp;</th></c:if> --%> 
					  <th class="footerredbg fnt text-right">Total Amount</th>
					  </c:when>
					  <c:when test="${not empty pconfig && (pconfig.enableDiscount eq false && pconfig.enableQuantity eq true && pconfig.enableRate eq false)}">	
                        <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th colspan="2" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'Advances'}"><th colspan="4" class="text-right whit_bg">&nbsp;</th></c:if>
                       <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if>
                     <%--  <c:if test="${invoice.invtype eq 'ISD'}"><th colspan="1" class="text-right whit_bg">&nbsp;</th></c:if> --%>
					  <c:if test="${invoice.invtype ne 'Advances' && invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}"><th class="footerredbg fnt text-right" colspan="2">Total Amount</th></c:if> <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}"><th class="footerredbg fnt text-right" colspan="1">Total Amount</th></c:if>
					  </c:when>
					  </c:choose>
                      <!--<c:if test="${empty pconfig || (pconfig.enableDiscount eq true && pconfig.enableQuantity eq false && pconfig.enableRate eq false) || (pconfig.enableDiscount eq true && pconfig.enableQuantity eq true && pconfig.enableRate eq true)}"><th>&nbsp;</th></c:if>-->
                      
                     <!--<c:if test="${empty pconfig || pconfig.enableRate eq true}"> <th>&nbsp;</th></c:if>-->
                        <c:if test="${invoice.invtype ne 'ITC Reversal' && invoice.invtype ne 'ISD'}"><th class="footerredbg fnt text-right"><span class="indformat">${invoice.totaltaxableamount}</span></th></c:if>
                     <c:if test="${invoice.invtype ne 'ISD' && invoice.invtype ne 'ITC Reversal'}">
                      <c:if test="${iAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${iAmt}</span></th>
                      </c:if>
                      <c:if test="${cAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${cAmt}</span></th>
                      </c:if>
                      <c:if test="${sAmt ne 0}">
                      <th  class="footerredbg fnt text-right"><span class="indformat">${sAmt}</span></th>
                      </c:if>
                      </c:if>
                      <c:if test="${invoice.invtype eq 'ITC Reversal' || invoice.invtype eq 'ISD'}">
                        <th  class="footerredbg fnt text-right"><span class="indformat">${iAmt}</span></th>
                        <th  class="footerredbg fnt text-right"><span class="indformat">${cAmt}</span></th>
                        <th  class="footerredbg fnt text-right"><span class="indformat">${sAmt}</span></th>
                      <th  class="footerredbg fnt text-right"><span class="indformat">${csAmt}</span></th>
                      </c:if>
                      
                      <c:choose>
                      	<c:when test="${not empty invoice.notroundoftotalamount}">
                      		<th  class="footerredbg fnt text-right"><div align="right"><span class="indformat">${invoice.notroundoftotalamount}</span></div></th>
                      	</c:when>
                      	<c:otherwise>
                      		<th  class="footerredbg fnt text-right"><div align="right"><span class="indformat">${invoice.totalamount}</span></div></th>
                      	</c:otherwise>
                      </c:choose>
                      
                    </tr>
                  </tfoot>
                </table>
                <table id="invoicetable1" class="display dataTable meterialform" cellspacing="0" width="100%" style="font-size:12px;">
                  <c:if test="${invoice.invtype ne 'ITC Reversal' && invoice.invtype ne 'ISD'}">
                  <tr>
                  
                    <td width="60%" height="19" style="font-size:12px;"><div align="right"><strong>Taxable Amount</strong></div></td>
                    <td width="10%" style="font-size:12px;"><div align="right"><span class="indformat">${invoice.totaltaxableamount}</span></div></td>
                    
                  </tr></c:if>
                  <tr>
                    <td  width="60%" style="font-size:12px;"><div align="right"><strong>Total Tax</strong></div></td>
                    <td  width="10%" style="font-size:12px;"><div align="right"><span class="indformat">${invoice.totaltax}</span></div></td>
                  </tr>
                  <tr>
                    <td style="font-size:12px;"><div align="right"><strong>Invoice Total</strong></div></td>
                    <td>
                    	<c:choose>
                    	<c:when test="${not empty invoice.roundOffAmount && invoice.roundOffAmount ne 0}">
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
                  <c:if test="${returntype eq 'SalesRegister' || returntype eq 'GSTR1'}">
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
                  </c:if>
                </table>
				<p style="font-size:12px;margin-top:5px; text-align:right">${amountinwords}</p>
              </div>
              <!-- table end -->
			  
			  <div class="row m-0">
                <table width="100%" border="0" cellspacing="0" cellpadding="5">
                  <tr>
                    <td><table width="100%" border="0" cellspacing="0" cellpadding="1">
					<c:if test="${returntype ne 'Purchase Register'}">
					  <c:if test="${not empty invoice.bankDetails && not empty invoice.bankDetails.accountnumber && not empty invoice.bankDetails.bankname}">
                        <tr>
                          <td width=""><div class="lable-txt">Bank Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.bankDetails.bankname}</span></td>
                           <td width=""><div class="lable-txt">Account Number</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.bankDetails.accountnumber}</span></td>
                        </tr>
						<tr>
                          <td width=""><div class="lable-txt">Branch Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.bankDetails.branchname}</span></td>
                          <td width=""><div class="lable-txt">IFSC Code</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.bankDetails.ifsccode}</span></td>
                        </tr>
                        <c:if test="${not empty invoice.bankDetails.accountName}">
						<tr>
                          <td width=""><div class="lable-txt">Account Name</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.bankDetails.accountName}</span></td>
                        </tr>
						</c:if>
						</c:if>
						 </c:if>
						 
						 <c:forEach items="${customFieldList}" var="customFieldList" varStatus="loop">
							<tr>
								<c:if test="${customFieldList.displayInPrint eq true}">
								<c:if test="${empty customFieldList.customFieldName}"><td width="20%"><div class="lable-txt">Custom Field ${loop.index+1}</div></td></c:if><c:if test="${not empty customFieldList.customFieldName}"><td width="20%"><div class="lable-txt">${customFieldList.customFieldName}</div></td></c:if>
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
						<c:if test="${not empty invoice.notes}">
						<tr>
                          <td width=""><div class="lable-txt">Notes</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.notes}</span></td>
                        </tr>
						</c:if>
						<c:if test="${not empty invoice.terms}">
						<tr>
                          <td width=""><div class="lable-txt">Terms & Conditions</div></td>
                          <td width="">:</td>
                          <td width=""><span class="f-14">${invoice.terms}</span></td>
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
		<c:if test="${client.enableAuthorisedSignatory eq true}">
		<div class="doc-foot">
		<span style="float:right"><strong>${signatureText}</strong></span><br>
		<div class="pull-right col-md-12 p-0"><strong style="float: right;">${client.authorisedSignatory}</strong></div>
		<div class="pull-right col-md-12 p-0"><strong style="float: right;">${client.designation}</strong></div>
		</div>
		</c:if>
		<c:if test="${empty client.enableAuthorisedSignatory || not client.enableAuthorisedSignatory}">
			<p style="text-align: center; padding-top: 50px; margin-bottom: -20px;">This is a computer generated invoice and does not need signature.</p>
		</c:if>
		
		<c:if test="${not empty pconfig && pconfig.isfooternotescheck eq true}">
		<p style="text-align: center; padding-top: 50px; margin-bottom: -20px;">${pconfig.footernotes}</p>
		</c:if>
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