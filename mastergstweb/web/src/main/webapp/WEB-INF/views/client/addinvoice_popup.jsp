<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="varRetType" value='<%= (String)request.getParameter("returntype") %>'/>
<c:set var="varGSTR1" value="<%=MasterGSTConstants.GSTR1%>"/>
<c:set var="varGSTR1A" value="<%=MasterGSTConstants.GSTR1A%>"/>
<c:set var="varGSTR2" value="<%=MasterGSTConstants.GSTR2%>"/>
<c:set var="varGSTR2A" value="<%=MasterGSTConstants.GSTR2A%>"/>
<c:set var="varGSTR3B" value="<%=MasterGSTConstants.GSTR3B%>"/>
<c:set var="varGSTR4" value="<%=MasterGSTConstants.GSTR4%>"/>
<c:set var="varGSTR6" value="<%=MasterGSTConstants.GSTR6%>"/>
<c:set var="varGSTR5" value="<%=MasterGSTConstants.GSTR5%>"/>
<c:set var="varANX1" value="<%=MasterGSTConstants.ANX1%>"/>
<c:set var="varANX1A" value="<%=MasterGSTConstants.ANX1A%>"/>
<c:set var="varANX2" value="<%=MasterGSTConstants.ANX2%>"/>
<c:set var="varANX2A" value="<%=MasterGSTConstants.ANX2A%>"/>
<c:set var="varPurchase" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
<c:set var="statusSubmitted" value="<%=MasterGSTConstants.STATUS_SUBMITTED%>"/>
<c:set var="statusFiled" value="<%=MasterGSTConstants.STATUS_FILED%>"/>
<c:set var="statusPending" value="<%=MasterGSTConstants.PENDING%>"/>
<c:set var="varDeliverchallan" value="<%=MasterGSTConstants.DELIVERYCHALLANS%>"/>
<c:set var="varProforma" value="<%=MasterGSTConstants.PROFORMAINVOICES%>"/>
<c:set var="varEstimates" value="<%=MasterGSTConstants.ESTIMATES%>"/>
<c:set var="varPurchaseOrder" value="<%=MasterGSTConstants.PURCHASEORDER%>"/>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/invoices.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/addinvoicepopups.css" media="all" />
<script src="${contextPath}/static/mastergst/js/common/filedrag-map.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/jquery.tablednd.0.7.min.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/common/datetimepicker-inv.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/print/jQuery.print.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/jquery.number.js" type="text/javascript"></script>
<script type="text/javascript">
var clientBankDetails = new Array();var clientBankDetails1 = new Array();
</script>
<jsp:include page="/WEB-INF/views/client/addEcommerceGSTIN.jsp" />
<jsp:include page="/WEB-INF/views/client/addcustomer.jsp" />
<jsp:include page="/WEB-INF/views/client/addLedger.jsp" />
<jsp:include page="/WEB-INF/views/client/addItem.jsp" />
<jsp:include page="/WEB-INF/views/client/cancelInvoice_popup.jsp"/>
<jsp:include page="/WEB-INF/views/client/paymentRecords.jsp" />
<div class="modal fade fullscreen" id="invoiceModal" aria-labelledby="invoiceModal" aria-hidden="true">
    <div class="modal-dialog" role="document"><div class="modal-content"><div class="modal-header p-0"><div class="invoice-hdr bluehdr"><h3 id="returnTypes"></h3>&nbsp;<h3 id="invoiceType" style="font-size:18px"></h3><h6 id="sirnNumber_txt" class="mt-1" style="color:white;display:none;">IRN No : <span id="sirnNumber"></span></h6></div><button type="button" class="close" aria-label="Close" onClick="closemodal('invoiceModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button></div>
        <div class="modal-body">
		<c:if test="${varRetType ne varGSTR1 && varRetType ne varANX1 && varRetType ne varGSTR4 && varRetType ne varGSTR6 && varRetType ne varGSTR5}"><c:set var="contains" value="${contextPath}/savepinv/${varRetType}/${usertype}/${month}/${year}?type=" /></c:if>
		<c:if test="${varRetType eq varGSTR1 || varRetType eq varANX1 || varRetType eq 'GSTR1Amnd'}"><c:set var="contains" value="${contextPath}/savesinv/${varRetType}/${usertype}/${month}/${year}?type=" /></c:if>
		<c:if test="${varRetType eq varGSTR4}"><c:set var="contains" value="${contextPath}/saveGSTR4/${varRetType}/${usertype}/${month}/${year}" /></c:if>
		<c:if test="${varRetType eq varGSTR5}"><c:set var="contains" value="${contextPath}/saveGSTR5/${varRetType}/${usertype}/${month}/${year}" /></c:if>
		<c:if test="${varRetType eq varGSTR6}"><c:set var="contains" value="${contextPath}/saveGSTR6/${varRetType}/${usertype}/${month}/${year}" /></c:if>
		<c:if test="${varRetType eq varANX1}"><c:set var="contains" value="${contextPath}/saveANX1/${varRetType}/${usertype}/${month}/${year}" /></c:if>
		  <form:form method="POST" data-toggle="validator" class="sortable-form mb-2" name="salesinvoceform" id="salesinvoceform"  modelAttribute="invoice">
          <div class="customtable db-ca-view col-sm-12" id="wrapper">
          <div class="alert alert-danger popup_error" role="alert"><span id="gstStatus" class="ml-3"></span> <img src="${contextPath}/static/mastergst/images/errors/danger-alert.png" alt="alert" class="mr-2 pull-right" onclick="notifi_disp1()" /></div>
		    <div class="row notExports" style="margin-top:20px;margin-bottom:0px!important">
              <div class="col-md-8 col-sm-12">
                <div class="row">
                <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.ISD%> <%=MasterGSTConstants.ISDA%>" style="display:none"><label for="isdDocty" class="bold-txt astrich">Document Type</label><select id="isdDocty" class="form-control" name="isd[0].doclist[0].isdDocty" data-error="Please select Document Type" required="required"><option value="" selected>--Select--</option><option value="ISD">ISD</option><option value="ISDCN">ISDCN</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
                <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.CDNURA%>" style="display:none"><label for="terms" class="bold-txt astrich">Document Type</label><select class="form-control" name="cdn[0].nt[0].ntty" id="ntty" data-error="Please select Document Type" onChange="updateDocType()"><option value="" selected>--Select--</option><option value="C">Credit</option><option value="D">Debit</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div id="documentTypediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">Document Type :</h6><select class="form-control" name="docType" id="docType" data-error="Please enter valid doc type" required><option value="">-Select Type-</option><option value="INV">Tax Invoice</option><option value="CHL">Delivery Challan</option><option value="BIL">Bill of Supply</option><option value="BOE">Bill of Entry</option><option value="CNT">Credit Note</option><option value="OTH">Others</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group" id="invoicenumber"><span class="errormsg" id="invno_Msg" style="margin-top:-18px"></span><label for="inlineFormInput" class="bold-txt astrich invoiceNumberText">Invoice Number</label><input type="text" id="serialnoofinvoice" name="b2b[0].inv[0].inum" required="required" maxlength="16" pattern="^[0-9a-zA-Z/ -]+$" data-error="Please enter valid invoice number" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" aria-describedby="serialnoofinvoice" placeholder="12345" class="form-control serialnoofinvoice" onChange="checkInvoiceNumber()"/><span class="control-label"></span><div id="invNoMissing" style="width:100%;"></div><div class="help-block with-errors"></div><span class="pInvno" style="display:none;font-size: 10px; color: green;">Previous Invoice No:<span class="previousInvoiceNo" style="font-size:9px;"></span> </span></div>
                  <div class="col-md-3 col-sm-12 form-group"><div class="dropdown-menu" id="inv_cred_note_date" style="display:none;"><p style="margin:0;">Credit/Debit Note date should not be less than the invoice date</p></div><i class="fa fa-calendar cal-inv"></i><label for="inlineFormInputGroup" class="bold-txt astrich invoiceDateText datepattern">Invoice Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input type="text" id="dateofinvoice" name="dateofinvoice" style="margin-top:0px" aria-describedby="dateofinvoice" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD/MM/YYYY" pattern="^(0?[1-9]|[12][0-9]|3[01])\/(0?[1-9]|1[012])\/[1-9]\d{3}$" required="required" data-error="Please enter valid invoice date" class="form-control" onChange="checkInvoiceNumber()"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
                  <div class="col-md-3 col-sm-12 form-group placeofsupply" id="pos"><label for="inlineFormInputGroup" class="bold-txt astrich">Place Of Supply</label><input id="billedtostatecode" required="required" name="statename" placeholder="State" pattern="\d{2}[a-zA-Z-]+\s*[a-zA-z]*\s*[a-zA-z]*\s*[a-zA-z]*" class="form-control bstatecode" data-error="Please enter Place of Supply"/><span class="control-label"></span><div class="help-block with-errors"></div><div id="billedtostatecodeempty" style="display:none"><div class="ddbox"><p>Search didn't return any results.</p></div></div></div>
                  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>" style="display:none"><label for="terms" class="bold-txt astrich">Export Type</label><select class="form-control" id="exportType" name="exp[0].expTyp" data-error="Please Select Export Type" onChange="updateInvType()"><option value="" selected>--Select--</option><option value="WPAY">Export with IGST</option><option value="WOPAY">Export under Bond/LUT</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
                  <div class="col-md-3 col-sm-12 form-group mb-0" id="billDate"><i class="fa fa-calendar cal-inv"></i><div class="dropdown"><div class="dropdown-menu" id="transactiondate" style="display:none;"><p style="margin:0;">Transaction date should not be less than the invoice date</p></div></div><label for="inlineFormInputGroup" class="bold-txt astrich datepattern">Transaction Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input type="text" class="form-control billDate" id="billDate" name="billDate" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD/MM/YYYY" pattern="^(0?[1-9]|[12][0-9]|3[01])\/(0?[1-9]|1[012])\/[1-9]\d{3}$" data-error="Please Enter Bill Date"/><span class="control-label"></span><div class="help-block with-errors"></div><span style="font-size: 11px;">(Goods/Services Received Date)</span></div>
		  		  <div id="vNumb" class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.ATPAID%> "><label for="billingAddress" class="bold-txt astrich">Original Invoice No</label><input type="text" class="form-control atvoucherNumber" id="atvoucherNumber" name="advOriginalInvoiceNumber" data-error="Please enter Voucher Number" pattern="^[0-9a-zA-Z/ -]+$" maxlength="16" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))"><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 mb-3 specific_field <%=MasterGSTConstants.ATPAID%>"><div class="row">
              <!-- <label for="customerName" class="pl-3">Customer Name : </label><label for="customerName" class="bold-txt pr-3" id="atcustname"></label><input type="hidden" id="advPCustname" name="advPCustname"/> -->
              <div class="col-md-12 pl-0 pr-0"><label for="Adjusamount" class="AdjusamtLabel">Invoice Amount :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atinvamt"> </label><input type="hidden" id="advPInvamt" name="advPInvamt"/></div>
              <label for="Adjusamount" class="AdjusamtLabel atigstamt"> IGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atigstamt">  </label><input type="hidden" id="advPIgstamt" name="advPIgstamt"/>
              <label for="Adjusamount" class="AdjusamtLabel atcsgstamt"> CGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atcgstamt">  </label><input type="hidden" id="advPCgstamt" name="advPCgstamt"/>
              <label for="Adjusamount" class="AdjusamtLabel atcsgstamt"> SGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atsgstamt">  </label><input type="hidden" id="advPSgstamt" name="advPSgstamt"/>
              </div></div>
				  <div id="vNumb" class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.CDNURA%>" style="display:none"><label for="billingAddress" class="bold-txt">Original Invoice No</label><input type="text" class="form-control voucherNumber" id="voucherNumber" name="cdn[0].nt[0].ntNum" data-error="Please enter Voucher Number" pattern="^[0-9a-zA-Z/ -]+$" maxlength="16" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))"><span class="errormsg creditno"></span><span class="control-label"></span><div class="help-block with-errors"></div><span class="oInvDate" style="display:none;font-size: 11px; color: green;">Original Invoice Date : <span class="oInvDateVal" style="font-size:10px;"></span> <input type="hidden" id="oldInvDateVal"name="cdn[0].nt[0].ntDt"/></span></div>
                  <div class="col-md-3 col-sm-12 form-group invTypeDiv specific_field <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2CL%> <%=MasterGSTConstants.B2CLA%> <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.CDNURA%>" style="display:none"><label for="inlineFormInputGroup" class="bold-txt">Invoice Type</label><select class="form-control" id="invTyp" name="b2b[0].inv[0].invTyp" onChange="updateInvType()"><option value="R" selected>Regular</option><option value="DE">Deemed Exports</option><option value="SEWP">Supplies to SEZ with payment</option><option value="SEWPC">Supplies to SEZ with payment(Tax collected from customer)</option><option value="SEWOP">Supplies to SEZ without payment</option></select></div>
                 <div class="form-group bs-fancy-checks col-md-3 col-sm-12 pt-4 specific_field <%=MasterGSTConstants.ADVANCES%>">
                 	<label for="advtaxable" class="bold-txt">Taxable :</label>
                         <div class="form-check form-check-inline">
                              <input class="form-check-input adv_taxable_type" name="advTaxableType" type="checkbox" id="adv_taxable_type" value="false" onchange="advTaxableChange()"/>
                              <label style="margin-top: -7px;"><span class="ui"></span> </label> 
                         </div>
                </div> 
                  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CDNURA%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%>" id="scdnur" style="display:none"><label for="terms" class="bold-txt astrich cdnurtype">Type</label><select class="form-control" name="cdnur[0].typ" id="cdnurtyp" data-error="Please select Type" onchange="updateCDNUR()"><option value="" selected>--Select--</option><option value="EXPWP">Exports With Payment</option><option value="EXPWOP">Exports Without Payment</option><option value="B2CL">B2CL</option><option value="B2CS">B2CS</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				 <div class="col-md-3 col-sm-12 form-group" id="itcClaimedDateP" style="display:none"><i class="fa fa-calendar cal-inv"></i><label for="inlineFormInputGroup" class="bold-txt itcClaimedDateText datepattern">ITC Claimed Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input type="text" id="dateofitcClaimed" name="dateofitcClaimed" style="margin-top:0px" aria-describedby="dateofitcClaimed" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD/MM/YYYY" pattern="^(0?[1-9]|[12][0-9]|3[01])\/(0?[1-9]|1[012])\/[1-9]\d{3}$" data-error="Please enter valid invoice date" class="form-control"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
                  <div class="col-md-6 col-sm-12 form-group pr-4" id="billname"><label for="billingAddress" class="bold-txt custName custNameLabel">Customer Name</label> <span class="errormsg billname"></span><span id="billingAddress_id" style="font-size: 12px;font-variant: full-width;font-weight: 700;float: right;color:green;"></span><input type="text" class="form-control billedtoname" id="billedtoname" name="billedtoname" aria-describedby="customerName" placeholder="Customer Name" data-error="Please enter Customer Name" style="width: 100%!important;max-width: 100%;"/><span class="control-label"></span><div class="help-block with-errors"></div><div id="addbilledtoname" style="display:none"><div class="ddbox2"><p id="newcust">Please add new customer</p><input type="button" class="btn btn-sm btn-blue-dark permissionSettings-Customers-Add" id="newcustval" value="Add New Customer" data-toggle="modal" onclick="updateName('billedtoname', 'custname', 'addCustomerModal','${varRetType}')" ></div></div></div>
                  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.ITC_REVERSAL%> <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.ISD%> <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.NIL%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.ADVANCES%> <%=MasterGSTConstants.ATPAID%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2CLA%> <%=MasterGSTConstants.CDNURA%> <%=MasterGSTConstants.ATA%> <%=MasterGSTConstants.TXPA%> <%=MasterGSTConstants.ISDA%> <%=MasterGSTConstants.DELIVERYCHALLANS%> <%=MasterGSTConstants.PROFORMAINVOICES%> <%=MasterGSTConstants.ESTIMATES%> <%=MasterGSTConstants.PURCHASEORDER%>" style="display:none" ><label for="terms" class="bold-txt astrich" id="gstin_lab" style="width:100%">GSTIN<input type="button" onclick="invokegstnPublicAPI(this)" id="invokegstnPublicAPI1" class="btn btn-green btn-sm pull-right disable" style="width: 50%; margin-top: -6px; margin-left: 6px; font-size:11px!important;margin-right:29px" value="Get GSTIN Details"/> </label><span class="errormsg" id="igstnnumber_Msg" style="margin-top: -42px;margin-right: 26px;"></span><span class="errormsg clientgstno" style="margin-top: -42px;margin-right: 15px;"></span><input type="text" class="form-control" id="billedtogstin" name="b2b[0].ctin" maxlength="15" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})|([Uu][Rr][Pp])$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" maxlength="15" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" onChange="updatePan(this.value)"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
                  <div class="col-md-3 col-sm-12 form-group ewayBillNumberDiv specific_field <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2CLA%> <%=MasterGSTConstants.EXPA%> <%=MasterGSTConstants.TXPA%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.IMP_GOODS%>"><label for="inlineFormInputGroup" class="bold-txt">Eway Bill Number</label><input id="ewayBillNumber" name="ewayBillNumber" placeholder="Eway BillNumber" class="form-control ewayBillNumber" minlength="12" pattern="[0-9]+" data-error="Please enter valid ewaybill number"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group eCommerceDiv specific_field <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2CL%> <%=MasterGSTConstants.B2CLA%>" style="display:none"><label for="terms" class="bold-txt">Ecommerce GSTIN</label><input type="text" class="form-control" id="ecommercegstin" name="b2cs[0].etin" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1}$" data-error="Please enter Valid Ecommerce GSTIN.(Sample 07CQZCD1111I4C7)" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0))" placeholder="07CQZCD1111I4Z7" maxlength="15"/><span class="control-label"></span><div class="help-block with-errors"></div><div id="addeccommercegstin" style="display:none"><div class="ddbox2"><p>Please add Ecommerce GSTIN</p><input type="button" class="btn btn-sm btn-blue-dark" id="neweccomval" value="Add Ecommerce GSTIN" data-toggle="modal" onclick="addEccomGSTIN('addEcommerceGSTIN')" ></div></div><p class="mb-0" style="font-size: 10px;color: green;" id="ecomoperatorname"></p></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>" style="display:none"><label for="terms" class="bold-txt">Port Code</label><input type="text" class="form-control" id="portcode" name="exp[0].inv[0].sbpcode" data-error="Please Enter Port Code" maxlength="6"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>" style="display:none"><label for="terms" class="bold-txt">Shipping Bill Number</label><input type="text" class="form-control" id="shippingBillNumber" name="exp[0].inv[0].sbnum" data-minlength="3" maxlength="7" data-error="Please Enter valid Shipping Bill Number"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>" style="display:none"><label for="terms" class="bold-txt datepattern">Shipping Bill Date(DD-MM-YYYY)</label><input type="text" class="form-control date_field" id="shippingBillDate" name="exp[0].inv[0].sbdt" placeholder="DD-MM-YYYY" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 45) || (event.charCode == 0))" pattern="^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[1-9]\d{3}$" data-error="Please Enter Shipping Bill Date"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>"><label for="inlineFormInputGroup" class="bold-txt">Additional Currency Code</label><select class="form-control add_currencyCode" name="addcurrencyCode" id="add_currencyCode" onchange="updateInvoiceCurrencyCode()"></select><span class="control-label"></span></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.EXPA%>"><label for="inlineFormInputGroup" class="bold-txt">Exchange Rate</label><div class="row"><div class="col-md-3 col-xs-4 p-0 usd-lable bold-txt">1 INR = &nbsp;</div><div class="col-md-5 col-xs-6 p-0"><span class="exchange_span bold-txt">INR</span><input class="form-control einvedit" name="exchangeRate" id="exchange_Rate" onkeyup="updateInvoiceTypeOfSupply()"/></div></div><span class="control-label"></span><div class="help-block with-errors"></div></div><div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.IMP_GOODS%>" style="display:none"><label for="isSez" class="bold-txt astrich" id="IsSEZ">Is SEZ?</label><select id="isSez" class="form-control isSez" name="impGoods[0].isSez" data-error="Please select Is SEZ or Not" required="required"><option value="" selected>--Select--</option><option value="Y">Yes</option><option value="N">No</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.IMP_GOODS%>" id="impGoodsgstin" style="display:none"><label for="stin" class="bold-txt astrich" id="stin_id">SEZ GSTIN/Unique ID </label><input type="text" id="stin" class="form-control" name="impGoods[0].stin"  aria-describedby="billedtogstin" pattern="^[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}$" maxlength="15" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" placeholder="07CQZCD1111I4Z7" required="required"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group ewaybilladdress" style="display:none;">
                 <label for="ewaybillingAddress" class="bold-txt ewaybuyerLabel">Buyer Details</label><a href="#" class="actionicons ewybuyerAddrText che_form-control" style="font-size: 13px;line-height: 25px;">Add</a>
	       				<div  id="ewaybuyerAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
                  </div>
				  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.IMP_GOODS%>" style="display:none"><label for="impPortcode" class="bold-txt astrich">Port Code</label><input type="text" class="form-control" id="impPortcode" name="impGoods[0].portCode" data-error="Please Enter Port Code" maxlength="6" pattern="^[a-zA-Z0-9]*$" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div class="col-md-3 col-sm-12 form-group printerintra specific_field <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>" style="display:none;"><label for="inlineFormInputGroup" class="bold-txt">Supply Type</label><select class="form-control" name="printerintra" id="printerintra" data-error="Please select SupplyType" onchange="updateB2BUR()"><option value="" selected>--Select--</option><option value="Inter">Inter State</option><option value="Intra">Intra State</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				<div class="col-md-3 col-sm-12 form-group ewaybillDateDiv" style="display:none;"><i class="fa fa-calendar cal-inv"></i><label for="inlineFormInputGroup" class="bold-txt astrich">Eway Bill Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input type="text" id="ewayBillDate" name="eBillDate" aria-describedby="ewayBillDate" onkeypress="return ((event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" placeholder="DD/MM/YYYY" pattern="^(0?[1-9]|[12][0-9]|3[01])\/(0?[1-9]|1[012])\/[1-9]\d{3}$"  class="form-control" data-error="Please enter valid ewaybill date" required/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				<div id="supplyTypediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">Supply Type :</h6><select class="form-control" name="supplyType" id="supplyType" data-error="Please enter valid supply type" required><option value="">-Select Type-</option><option value="O">Outward</option><option value="I">Inward</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
				  <div id="subsupplyTypediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">SubSupply Type :</h6><select class="form-control" name="subSupplyType" id="subsupplyType" data-error="Please enter valid Sub supply type" required><option value="">-Select-</option><option value="1">Supply</option><option value="2">Import</option><option value="3">Export</option><option value="4">Job Work</option><option value="5">For Own Use</option><option value="6">Job work Returns</option><option value="7">Sales Return</option><option value="8">Others</option><option value="9">SKD/CKD</option><option value="10">Line Sales</option><option value="11">Recipient  Not Known</option><option value="12">Exhibition or Fairs</option></select><span class="control-label"></span><div class="help-block with-errors"></div> </div>                  
				   <div id="fromPincodediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">From Pincode :</h6><input type="text" class="form-control" name="fromPincode" maxlength="6" pattern="[0-9]+" id="fromPincode" data-error="Please enter valid from pincode" required><span class="control-label"></span><div class="help-block with-errors"></div></div>
                <div id="toPincodediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">To Pincode :</h6><input type="text" class="form-control" name="toPincode" id="toPincode"  maxlength="6" pattern="[0-9]+" data-error="Please enter valid from To Pincode" required><span class="control-label"></span><div class="help-block with-errors"></div> </div>
                <div id="transporterIddiv" style="display:none;" class="col-md-3 col-sm-12 pr-0"><label class="bold-txt" id="" style="width:100%">TransporterId :<input type="button" onclick="invokeTransGstnPublicAPI(this)" id="invokeTransgstnPublicAPI1" class="btn btn-green btn-sm pull-right disable" style="width: 50%; margin-top: -6px; margin-left: 6px; font-size:11px!important;margin-right:8px" value="Get Trans Details"/></label><span class="errormsg" id="transIdnumber_Msg" style="margin-top: -42px;margin-right: 26px;"></span><input type="text" class="form-control" name="transporterId" id="transporterId" placeholder="05AAACG0904A1ZL" maxlength="15" onchange="updatevehicleDetails(this.value)" pattern="^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[zZ]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$" data-error="Please enter Valid GSTIN.(Sample 07CQZCD1111I4Z7)" onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0))"></div>
				<div id="transporterNamediv" style="display:none;" class="col-md-3 col-sm-12"><h6 id="">Transporter Name :</h6><input type="text" class="form-control" name="transporterName" id="transporterName" placeholder="john"></div>
				<div id="transdistancediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">Trans Distance :</h6><input type="text" class="form-control transdistance" name="transDistance" id="transDistance" placeholder="656" data-error="Please enter valid Trans Distance" required><span class="control-label"></span><div class="help-block with-errors"></div> </div>
				<div id="transactionTypediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="astrich">Transaction Type :</h6><select class="form-control" name="transactionType" id="transactionType" data-error="Please enter valid Transaction Type" required><option value="">-Select-</option><option value="1">1. Regular</option><option value="2">2. Bill To-Ship To</option><option value="3">3. Bill From-Dispatch From</option><option value="4">Combinations of 2 & 3</option></select><span class="control-label"></span><div class="help-block with-errors"></div> </div>  
				<div id="othervaluediv" style="display:none;" class="col-md-3 col-sm-12 form-group"><h6 id="" class="">Other Values :</h6><input type="text" class="form-control otherValue" name="otherValue" id="otherValue" placeholder="656" onKeyUp="findOtherValue()"><span class="control-label"></span><div class="help-block with-errors"></div> </div>
              	 <div class="col-md-3 col-sm-12 form-group mb-0" id="lutDiv" style="display:none;"><label for="inlineFormInputGroup" class="bold-txt">LUT Number</label><input id="lutNo" name="lutNo" placeholder="123456" class="form-control invedit"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
                  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.DELIVERYCHALLANS%>" style="display:none"><label for="inlineFormInputGroup" class="bold-txt astrich">Challan Type</label><select class="form-control" id="challantype" name="dc[0].challanType" data-error="Please select Challan Type"><option value="">--Select--</option><option value="JOB_WORK">Job Work</option><option value="OTHERS">Others</option><option value="SUPPLY_OF_LIQUID_GAS">Supply of Liquid Gas</option><option value="SUPPLY_ON_APPROVAL">Supply on Approval</option></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
		 		  <div class="col-md-3 col-sm-12 form-group specific_field <%=MasterGSTConstants.PROFORMAINVOICES%> <%=MasterGSTConstants.ESTIMATES%>" style="display:none"><i class="fa fa-calendar cal-inv"></i><label for="inlineFormInputGroup" class="bold-txt astrich">Expiry Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input type="text" id="expirydateofinvoice" name="expiryDate" required="required" onkeypress="return ((event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 43) || (event.charCode == 47) || (event.charCode == 0))" data-error="Please enter expiry date"  aria-describedby="expirydateofinvoice" placeholder="DD/MM/YYYY" class="form-control expirydateofinvoice" /><span class="control-label"></span><div class="help-block with-errors"></div></div>
				 <div class="col-md-3 col-sm-12 form-group deliverydate specific_field <%=MasterGSTConstants.PURCHASEORDER%>"><label for="inlineFormInputGroup" class="bold-txt astrich">Delivery Date</label><input id="deliverydate" required="required" name="deliveryDate" placeholder="Delivery date"  class="form-control" data-error="Please enter Delivery date"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
			    </div>
              </div>
              <div class="col-md-4 col-sm-12"><div class="row">
              	<div class="col-md-12 col-sm-12"><div class="pull-right balancedue"><label class="bold-txt">Total Amount</label><h1><i class="fa fa-rupee"></i> <span id="idTotal" class="indformat indformat_roundoff">0.00</span></h1></div></div>
              	<div class="col-md-6 col-sm-12 form-group notewaybilladdress mb-2" id="billingAdressdiv"><label for="billingAddress" class="bold-txt billingAddressLabel">Billing Address</label><textarea type="text" class="form-control mapicon" id="billingAddress" placeholder="Enter a location" name="b2b[0].inv[0].address" data-error="Please enter Address"></textarea><span class="control-label"></span><div class="help-block with-errors"></div></div>
                 		<div class="col-md-6 col-sm-12 notewaybilladdress mb-2" id="shippingAdressdiv"><label for="billingAddress" class="bold-txt ShipmentAddressLabel">Shipment Address</label><textarea type="text" class="form-control mapicon" id="shippingAddress" name="consigneeaddress"></textarea></div>
	                  <div class="col-md-6 col-sm-12 form-group pl-0 ewaybilladdress" style="display:none;" id="ewaybillingAdressdiv">
		               	<label for="ewaybillingAddress" class="bold-txt ewaybillingAddressLabel">Dispatcher Address</label><a href="#" class="actionicons che_form-control" style="font-size: 13px;line-height: 25px;" onclick="ewayshowDispatchModal()">Edit</a>
		       			<div  id="ewaydispatchAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
		               
	                 </div>
	                  <div class="col-md-6 col-sm-12 ewaybilladdress" style="display:none;" id="ewayshippingAdressdiv">
		                  <label for="ewayshippingAddress" class="bold-txt ewyShipmentAddressLabel">Shipment Address</label><a href="#" class="actionicons d-none" style="font-size: 13px;line-height: 25px;" onclick="ewyshowShipmentModal()" id="ewyshimentAddrEdit">Edit</a>
		                   <div  id="ewayshipmentAddr" style="min-height:60px;font-size:12px;word-break: break-all;font-family: sans-serif;"></div>
		              </div>
		           <div class="col-md-3 col-sm-12 invoiceLevelCessDiv pr-0" style="display:none"><div class="form-check mb-2 mb-sm-0 invoiceLevelCess"><div class="meterialform"><div class="checkbox che_form-control"><label><input type="checkbox" id="invoiceLevelCess" onchange="changeInvoiceLevelCess()"><i class="helper che-box"></i> Show Cess</label></div></div></div></div>
                  <div class="col-md-4 col-sm-12 notewaybilladdress pl-0 pr-0" id="rateinclusivediv"><div class="form-check mb-2 mb-sm-0 rateinclusive"><div class="meterialform"><div class="checkbox che_form-control"><label><input type="checkbox" name="includetax"  id="includetax"><i class="helper che-box"></i> <span class="advancetax">Rate inclusive of Tax</span></label><span class="rateinctax errormsg"></span></div></div></div></div>
                  <div class="col-md-6 col-sm-12 ewaybilladdress" style="display:none;"></div>
                  <div class="col-md-4 col-sm-12 notewaybilladdress pl-0 pr-0" id="sameasBillingAddrDiv"><div class="form-check mb-2 mb-sm-0 pull-right" id="samebilladdressdiv"><div class="meterialform"><div class="checkbox che_form-control"><label><input type="checkbox" name="samebilladdress" value="1" id="samebilladdress" onChange="samebilladdresscheck()"><i class="helper che-box"></i> Same as Billing Address</label></div></div></div></div>
                  <div class="col-md-6 col-sm-12 pl-0 ewaybilladdress" style="display:none;">
	                  <div class="form-check mb-2 mb-sm-0" id="ewysamebilladdressdiv">
		                  <div class="meterialform">
			                  <div class="checkbox che_form-control">
			                	  <label><input type="checkbox" name="ewysamebilladdress" value="1" id="ewysamedispatchaddress"><i class="helper che-box"></i> Same as Buyer Address</label>
			                  </div>
		                  </div>
	                  </div>
                  </div>
              	</div></div>
			</div>
           <%-- <div class="row">
	   <div class="col-md-10 col-sm-12 mb-0 mt-2 specific_field <%=MasterGSTConstants.ATPAID%>"><div class="row">
              <!-- <label for="customerName" class="pl-3">Customer Name : </label><label for="customerName" class="bold-txt pr-3" id="atcustname"></label><input type="hidden" id="advPCustname" name="advPCustname"/> -->
              <label for="Adjusamount" class="AdjusamtLabel pl-3">Invoice Amount :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atinvamt"> </label><input type="hidden" id="advPInvamt" name="advPInvamt"/>
              <label for="Adjusamount" class=" AdjusamtLabel"> IGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atigstamt">  </label><input type="hidden" id="advPIgstamt" name="advPIgstamt"/>
              <label for="Adjusamount" class=" AdjusamtLabel"> CGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atcgstamt">  </label><input type="hidden" id="advPCgstamt" name="advPCgstamt"/>
              <label for="Adjusamount" class=" AdjusamtLabel"> SGST :</label><label for="AdjusamtVal" class="bold-txt AdjusamtVal pr-3" id="atsgstamt">  </label><input type="hidden" id="advPSgstamt" name="advPSgstamt"/>
              </div></div>
            </div>   --%>
			<div class="pull-left" style="height:20px"><span class="errormsg" id="invitemdetails"></span><span id="billgstin_name" style="color:green;font-size: 12px;"></span><span id="ewayBillError"  class="errormsg" style="color:red;font-size: 12px;"></span></div>
            <table align="center" id="sortable_table" border="0" class="row-border dataTable is-itc-no">
              <thead>
                <tr>
                	<th rowspan="2" width="3%">S.No</th>
                	<th rowspan="2" width="23%" class="noadvFlag noisd " id="ad_tax">Item/Product/Service</th>
                	<th class="item_ledger" rowspan="2" width="8%">Ledger</th>
                	<th rowspan="2" width="6%" class="astrich noadvFlag noisd" id="ad_tax1">HSN/SAC</th>
                	<th rowspan="2" width="6%" class="astrich AdvancesFlag noadvFlag noisd" id="ad_tax2">UQC</th>
                	<th rowspan="2" width="5%" class="astrich AdvancesFlag noadvFlag noisd" id="ad_tax3">
	                	<c:choose>
	                		<c:when test='${empty pconfig.qtyText || pconfig.qtyText eq ""}'>Qty</c:when>
	                		<c:otherwise>${pconfig.qtyText}</c:otherwise>
	                	</c:choose>
                	</th>
                	<th rowspan="2" class="noadvFlag noisd" width="8%" id="ad_tax4">
                		<c:choose><c:when test='${empty pconfig.rateText || pconfig.rateText eq ""}'>Rate</c:when>
                		<c:otherwise>${pconfig.rateText}</c:otherwise></c:choose>
                	</th>
                	<th rowspan="2" width="5%"  class="SnilFlag AdvancesFlag disFlag noadvFlag noisd">Discount</th>
                	<th rowspan="2" width="5%"  class="exemp_td exemptFlag">Exempted</th>
                	<th rowspan="2" class="astrich noadvFlag" width="7%" id="taxableAmount">Taxable <br>Amount</th>
                	<th class="addAdvFlag" rowspan="2" width="8%">Adv. Rept. No</th>
                	<th class="addAdvFlag" rowspan="2">Adv. Rec. Date(DD-MM-YYYY)</th>
                	<th class="addAdvFlag" rowspan="2">Place of Supply</th>
                	<th class="addAdvFlag" rowspan="2">Adv. Rec. Amount</th>
                	<th class="addAdvFlag" rowspan="2">Adv. Available fo Adjustment</th>
                	<th class="addAdvFlag" rowspan="2">Adv. to be Adjusted</th>
                	<th rowspan="2" width="5%" class="astrich SnilFlag noisd" id="tax_rate">Tax <br>Rate</th>
                	<th rowspan="2" width="5%" class="SnilFlag">Tax (GST)</th>
                	<th colspan="2" width="8%"  class="SnilFlag noisd cessFlag p-0">Cess on
                	<div class="form-group-inline cesstype">
                				<div class="form-radio mr-0">
                                    <div class="radio">
                                        <label>
                                            <input name="cessType" id="cess_taxable" type="radio" value="Taxable Value" onchange="changeCessType()" checked/>
                                            <i class="helper"></i>Taxable</label>
                                    </div>
                                </div>
                                <div class="form-radio mr-0">
                                    <div class="radio">
                                        <label>
                                            <input name="cessType" id="cess_qty" type="radio" value="Quantity" onchange="changeCessType()"/>
                                            <i class="helper"></i>Qty</label>
                                    </div>
                                </div>
                    </div>
                	</th>
                	<th rowspan="2" width="8%" class="addITCRuleFlag astrich" id="itc_rule">Nature of Supply</th>
                	<th rowspan="2" width="8%" class="addITCFlag" id="itc_elg">ITC Eligible</th>
                	<th rowspan="2" width="2%" class="addITCFlag" id="itc_per">Eligible %</th>
                	<th rowspan="2" width="4%" class="addITCFlag">ITC Amount</th>
                	<!--<th rowspan="2" width="5%" class="addAdvFlag astrich" >Adv Received</th>-->
                	<th class="addAdvFlag" rowspan="2" class="totallableltxt" width="9%">Adv. Remaining</th>
                	<th rowspan="2" class="totallableltxt" width="9%">Total</th>
                	<th rowspan="2" width="9%" class="addExportFlag" id="currency_text">Cur Total</th>
                	<th rowspan="2" width="3%" class="noisd advtext"></th></tr>
                 <tr>
                 <!-- <th class="SnilFlag">IGST</th><th class="SnilFlag">CGST</th><th class="SnilFlag">SGST</th> --><th class="SnilFlag  cessFlag noisd" id="rate_as">Rate</th><th class="SnilFlag  cessFlag noisd">Amount</th><!-- <th class="addITCFlag">IGST</th><th class="addITCFlag">CGST</th><th class="addITCFlag">SGST</th><th class="addITCFlag">CESS</th> --></tr>
              </thead>
              <tbody id="allinvoice">
                <tr id="1" class="rowshadow item_edit">
                  <td id="sno_row1" align="center">1</td>
                  <td id="" class="form-group noadvFlag product_notes">
                 <!-- <input type="hidden" class="form-control" id="ledger1" name="items[0].ledgerName"  placeholder="ledger"> --> 
                  	<div class="col-md-12 p-0">
                  		<input type='text' class='form-control input_itemDetails_txt itemDetails itemname1' id='product_text1' name='items[0].itemno' placeholder="Item/Product/Service" value=''></div><div id='remainproduct_textempty1' style='display:none'>
	                  	<div class='remainddbox1 dbbox permissionSettings-Items-Add'>
	                  		<p>Please add new item</p>
	                  		<input type='button' class='btn btn-sm btn-blue-dark permissionSettings-Items-Add' value='Add New Item' data-toggle='modal' onclick='updateNames(1)'>
	                  	</div>
                  	</div>
                  	<div class="dropdown-search1 col-md-1 p-0" data-toggle="tooltip" title=" Click on Icon 'C' to enter additional details of your Item/Product/Service">
	                  	<span class="itemnote_info_icon" onclick="showAdditionalItemFieldsPopup(1)" id="dropdownMenuitemdec"><i><b>C</b></i></span>
	                  	<div class="modal-d modal-arrow1" style="display:none;"></div>
	                  	<!-- <div  id="item_notes_filed" class="dropdown-menu search1" aria-labelledby="dropdownMenuitemdec" style="border: none;">
		                  	<div class="itemnot_hdr"><span class="itemnot_lable">ITEM NOTES</span><span class="itemno_clo_span"><i class="fa item-close-icon">&#xf00d;</i></span></div>
		                  	<textarea rows="3" cols="23" class="item_notes" id="itemnotes_text1" name="items[0].itemNotescomments" Placeholder="Item Description"></textarea>
	                  	</div> -->
                  	</div>
                  	</td>
                  	<td class="item_ledger"> <input type="text" class="form-control" id="ledger1" name="items[0].ledgerName"  placeholder="ledger">
                  		<div id="addledgername1" style="display:none">
                  			<div class="ledgerddbox1">
                  				<p id="newledger">Please add new Ledger</p>
                  				<input type="button" class="btn btn-sm btn-blue-dark" id="newledgerval" value="Add New Ledger" data-toggle="modal" onclick="addItemLedger('1')" >
                  			</div>
                  		</div>
                  	</td>
                  	<td id="" data='' class="form-group invoiceHSN noadvFlag"><input type='text' class='form-control hsnval hsn_text' id='hsn_text1' name='items[0].hsn' onchange="findHsnOrSac(1)" placeholder='HSN/SAC' value='' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 32))' required><div id='itemcodeempty' class="remainGSTR1ddbox31" style='display:none'><div class='remainhsnddbox1'><p>Search didn't return any results.</p></div></div></td>
                  	<td id="" class="form-group invoiceUqc AdvancesFlag noadvFlag"><input type='text' class='form-control uqcDetails uqcname1 uqcval itcFlag' required id='uqc_text1' placeholder='UQC' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))' name='items[0].uqc' value=''><div id='uqc_textempty' style='display:none' class="remainGSTR1ddbox41"><div class='remainddboxuqc4'><p>Search didn't return any results.</p></div></div></td><td id="" align="right" class="form-group AdvancesFlag noadvFlag "><input type="hidden" id="opening_stock1"/><input type="hidden" id="saftey_stock1"/><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control qtyval negativevalues text-right" required id="qty_text1" name="items[0].quantity"  value="" onchange="changeStockAmts(1)" onKeyUp="findTaxableValue(1)" pattern="^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="quantity" placeholder="Quantity" /></td><td id="" align="right" class="form-group noadvFlag"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control input_rate_txt rateval itcFlag negativevalues text-right" id="rate_text1" name="items[0].rateperitem" value="" onKeyUp="findTaxableValue(1)" pattern="^[0-9]+(\.[0-9]{1,10})?$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="rate" placeholder="Rate" /></td><td id="" align="right" class="form-group SnilFlag AdvancesFlag disFlag noadvFlag"><input type="hidden" class="form-control" id="disper1" name="items[0].disper"  placeholder="disPer"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)" class="form-control disval text-right" id="discount_text1" name="items[0].discount" value="" onKeyUp="findTaxableValue(1)" pattern="[0-9]+(\\.[0-9]+)?[%]?" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="discount" placeholder="25 or 25%" /></td><td id="" align="right" class="form-group exemp_td"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" class="form-control exemptFlag text-right" id="exempted_text1" name="items[0].exmepted" value="" onKeyUp="findTaxableValue(1)" pattern="^[0-9]+(\.[0-9]{1,10})?$" data-error="Please enter numeric value with a max precision of 2 decimal places" aria-describedby="Exempted" placeholder="Exempted" /></td>
				  <td id="" class="tablegreybg form-group advAmount noadvFlag itcFlag" align="right"><input type='text' class='form-control indformat text-right' id='taxableamount_text1' name='items[0].taxablevalue' onkeyup="findIsdTaxAmount(1)" readonly></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcnoval' required id='advrcno_text1' name='items[0].advReceiptNo' maxlength='16' pattern='^[0-9a-zA-Z/ -]+$' data-error='Please enter valid invoice number' onkeypress='return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode >= 48 && event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))'><div id='advrcno_textempty' style='display:none'><div class='advpaymentddbox1'><p>Search didn't return any results.</p></div></div></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcdtval' required id='advrcdt_text1' name='items[0].advReceiptDate' readonly></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcposval' required id='advrcposs_text1' name='items[0].advStateName' readonly></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcamtval text-right' required id='advrcamt_text1' name='items[0].advReceivedAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' readonly></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcavailval text-right' required id='advrcavail_text1' name='items[0].advAdjustableAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$' readonly></td><td id="" class="addAdvFlag form-group" align="right" ><input type='text' class='form-control advrcavialadjval text-right' id='advrcavailadj_text1' name='items[0].advadjustedAmount' onkeypress='return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)' onkeyup='checkadvpayemnt(1)' onblur='checkadvpayemnt(1)' pattern='^([1-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$'><div id='adjamt1' class='advadjamnt' style='display:none; color:red'>This Amount is Greater than available Amount</div></td><td id="" align="right" class="form-group invoiceTaxrate SnilFlag"><select id="taxrate_text1" class="form-control taxrate_textDisable taxrateval" name="items[0].rate" onchange="findTaxAmount(1)" required><option value="">-Select-</option><option value=0>0%</option><option value=0.1>0.1%</option><option value=0.25>0.25%</option><option value=1>1%</option><option value=1.5>1.5%</option><option value=3>3%</option><option value=5>5%</option><option value=7.5>7.5%</option><option value=12>12%</option><option value=18>18%</option><option value=28>28%</option></select></td>
				  <td id="" class="tablegreybg form-group SnilFlag" align="right"><input type='text' class='form-control dropdown text-right' id='abb1' name='items[0].totaltaxamount'  readonly><div id='tax_rate_drop' style='display:none'><div id='icon-drop'></div><i style="font-size:12px;display:none" class="fa">&#xf00d;</i><h6 style='text-align: center;' class='mb-2 tax_text'>TAX AMOUNT</h6><div class='row pl-3' style='height:25px'><p class='mr-3'>IGST <span style='margin-left:5px'>:<span></p><span><input type='text' class='form-control dropdown' id='igsttax_text1' name='items[0].igstamount' style='border:none;width: 70px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>CGST :</p><span><input type='text' class='form-control' id='cgsttax_text1' name='items[0].cgstamount' style='border:none;width:65px;padding-top: 2px;background: none;'></span></div><div class='row pl-3' style='height:25px'><p class='mr-3'>SGST :</p><span><input type='text' class='form-control' id='sgsttax_text1' name='items[0].sgstamount' style='border:none;width:78px;padding-top: 2px;background: none;'></span></div></div></td><td id="" align="right" class="form-group SnilFlag cessFlag"><input type="text" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0 || event.charCode == 37)" id="cessrate_text1" required class="form-control cessval text-right" name="items[0].cessrate" onKeyUp="findCessAmount(1)" pattern="^[0-9]+(\.[0-9]{1,2})?[%]?$" data-error="Please enter numeric value with a max precision of 2 decimal places" /></td><td id="" class="tablegreybg form-group SnilFlag cessFlag" align="right"><input type='text'  class='form-control text-right cessamtval' id='cessamount_text1' name='items[0].cessamount'  readonly></td><td id="" class="addITCRuleFlag form-group itcrule_text1" align="right"><select class="form-control itcruleval" id="itcrule_text1" name="items[0].type" required><option value="">--Select Type -- </option><option value="Nil Rated">Nil Rated</option><option value="Exempted">Exempted</option><option value="Non-GST">Non-GST</option></select></td><td id="" class="addITCFlag form-group"><select id="itctype_text1" class="form-control itcval" onchange="updateEligibity(this.value, 1)" name="items[0].elg"><option value="">- Input Type -</option><option value="cp">Capital Good</option><option value="ip">Inputs</option><option value="is">Input Service</option><option value="no">Ineligible</option><option value="pending">Pending</option></select></td><td id="" class="addITCFlag form-group"><input type="text" class="form-control itc_percent text-right" id="itcpercent_text1" name="items[0].elgpercent" value="" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" onKeyUp="findITCValue(1)" pattern="^[0-9]+(\.[0-9]{1,2})?$" data-error="Please enter numeric value with a max precision of 2 decimal places"/></td><td id="" class="tablegreybg addITCFlag form-group" align="right"><input type='text' class='form-control dropdown text-right' id='itc_tax_tot1' name='items[0].totitval'  readonly><div id='itctax_rate_drop1' style='display:none'><span id="icon-drop1"></span><h6 class='text-center mb-2'>ITC Amount</h6><div class='row pl-3'><p class='mr-3 mb-0' style='height: 25px;'>IGST :</p><span style='height: 25px;'><input type='text' class='form-control dropdown' id='igstitc_text1' name='items[0].igstavltax' style='border:none;width: 70px;'></span></div><div class='row pl-3'><p class='mr-3 mb-0' style='height: 25px;'>CGST :</p><span style='height: 25px;'><input type='text' class='form-control'  id='cgstitc_text1' name='items[0].cgstavltax' style='border:none;width:65px;'></span></div><div class='row pl-3'><p class='mr-3 mb-0' style='height: 25px;'>SGST :</p><span style='height: 25px;'><input type='text' class='form-control' id='sgstitc_text1' name='items[0].sgstavltax' style='border:none;width: 67px;'></span></div><div class='row pl-3'><p class='mr-3 mb-0' style='height: 25px;'>CESS :</p><span style='height: 25px;'><input type='text' class='form-control' id='cessitc_text1' name='items[0].cessavltax' style='border:none;width: 60px;'></span></div></div></td></td>
				  <td id="" class="form-group addAdvFlag" align="right"><input type='text' class='form-control advremaingamount text-right' id='advremaingamount_text1' name='items[0].advRemaingAmount' required readonly></td>
				  <td id="" class="tablegreybg form-group" align="right"><input type='text' class='form-control text-right' id='total_text1' name='items[0].total' readonly></td>
				  <td id="" class="tablegreybg form-group addExportFlag" align="right"><input type='text' class='form-control text-right' id='curtotal_text1' name='items[0].currencytotalAmount' readonly></td>
				  <td align="center" width="2%"><a href="javascript:void(0)" id="delete_button1" class="item_delete" onclick="delete_row(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> </td>
                  <td class="d-none"><input type='hidden' class='form-control text-right' id='itemCustomField_text11' name='items[0].itemCustomField1'>
                   <input type='hidden' class='form-control text-right' id='itemCustomField_text21' name='items[0].itemCustomField2'>
                   <input type='hidden' class='form-control text-right' id='itemCustomField_text31' name='items[0].itemCustomField3'>
                   <input type='hidden' class='form-control text-right' id='itemCustomField_text41' name='items[0].itemCustomField4'>
                    <input type='hidden' class='form-control text-right' id='itemnotes_text1' name='items[0].itemNotescomments'>
                    <input type='hidden' class='form-control text-right' id='itemId_text1' name='items[0].itemId'>
                   </td>
                </tr>
				<tr style="display:none" class="rowshadow item_edit">
                  <td align="center"></td><td align="left"><input type="text" id="new_product" class="btn"></td><td align="left"><input type="text" id="new_hsn" class="btn"></td><td align="right"><input type="text" id="new_uqc" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_qty" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_rate" class="btn AdvancesFlag"></td><td align="right"><input type="text" id="new_discount" class="btn SnilFlag AdvancesFlag disFlag"></td>
                  <td align="right"><input type="text" id="new_taxableamount" class="btn" disabled></td><td align="right"><input type="text" id="new_taxrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_igsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_cgsttax" class="btn SnilFlag"  disabled></td><td align="center"><input type="text" id="new_sgsttax" class="btn SnilFlag"  disabled></td>
                  <td align="center"><input type="text" id="new_cessrate" class="btn SnilFlag"></td><td align="center"><input type="text" id="new_cessamount" class="btn SnilFlag" disabled></td><td align="center"><input type="text" id="new_itcrule" class="btn addITCRuleFlag"></td><td align="center"><input type="text" id="new_itctype" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_itcpercent" class="btn"></td><td align="center"><input type="text" id="new_igstitc" class="btn addITCFlag" disabled></td>
                  <td align="center"><input type="text" id="new_cgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_sgstitc" class="btn addITCFlag" disabled></td><td align="center"><input type="text" id="new_cessitc" class="btn addITCFlag"></td><td align="center"><input type="text" id="new_advrcvd" class="btn addAdvFlag" disabled></td><td align="center"><input type="text" id="new_total" class="btn" disabled></td><td width="3%"></td>
                </tr>
              </tbody>
              <tfoot id="allinvoicettfoot">
                <tr>
                  <th colspan="8" class="tfootwitebg nil-foot"><span class="add pull-left" id="addrow1"><i class="add-btn">+</i> Add another row</span><span class="pull-right SnilFlag">Total Inv. Val</span></th><th class="item_ledger tfootwitebg" style="display:none"></th>
                  <th class="tfootbg indformat SnilFlag itcFlag" id="totTaxable" style="border: none;"></th><th class="tfootwitebg  row_foot"></th><th class="tfootbg indformat SnilFlag" id="totIGST"></th><!-- <th class="tfootbg indformat SnilFlag" id="totCGST"></th><th class="tfootbg indformat SnilFlag" id="totSGST"></th> --><th class="tfootwitebg row_foot cessFlag"></th><th class="tfootbg indformat SnilFlag cessFlag" id="totCESS"></th><th class="tfootwitebg addITCRuleFlag itcrule_text1"></th>
                  <th class="tfootwitebg addITCFlag"></th><th class="tfootwitebg addITCFlag"></th><th class="tfootwitebg addITCFlag indformat text-right" id="totITCIGST"></th><!-- <th class="tfootwitebg addITCFlag indformat" id="totITCCGST"></th><th class="tfootwitebg addITCFlag indformat" id="totITCSGST"></th><th class="tfootwitebg addITCFlag indformat" id="totITCCESS"></th> -->
                  <th class="tfootbg indformat addAdvFlag" id="totAdvRemaining"></th>
                  <th class="tfootbg indformat" id="totTotal"></th><th class="tfootbg addExportFlag indformat" id="totCurAmt">
                  <th class="tfootwitebg addbutton"> <span class="add add-btn" id="addrow">+</span></th>
                </tr>
              </tfoot>
            </table>
            <table align="center" id="sortable_table1" border="0" class="row-border dataTable is-itc-yes" style="display:none">
              <thead>
                <tr><th rowspan="2" width="3%">S.No</th><th rowspan="2" width="27%" class="noadvFlag particuler_type" id="particuler_type" style="display:none">Particular Type</th><th colspan="3" width="10%" class="SnilFlag tax_type">Tax (GST)</th><th rowspan="2" class="astrich noadvFlag isdcess_amount" width="3%" style="display:none;">Cess Amount</th><th rowspan="2" class="totallableltxt" width="5%">Total</th></tr>
                <tr><th class="SnilFlag">IGST</th><th class="SnilFlag">CGST</th><th class="SnilFlag">SGST</th></tr>
              </thead>
              <tbody id="allinvoice1"><tr id="1" class="item_edit1"><td id="sno" align="center">1</td><td id="particular_row1"class="form-group particuler_type"><label>Eligible - Credit distributed</label><input type="text"  id="particular_val1" name="items[0].isdType" value="Eligible - Credit distributed"   style="display:none;"/></td><td  class="form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype1 tax_type text-right" id="isdigsttax_text1" name="items[0].igstamount"  onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeyup="findIsdTaxAmount(1)"></td><td  class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype1 tax_type text-right" id="isdcgsttax_text1" name="items[0].cgstamount" onkeyup="findIsdTaxAmount(1)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td  class=" form-group SnilFlag isdtab " align="right"><input type="text" class="form-control isdtype1 tax_type text-right" id="isdsgsttax_text1" onkeyup="findIsdTaxAmount(1)" name="items[0].sgstamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" onkeyup="findIsdTaxAmount(1)" id="isdisdcess_text1" name="items[0].isdcessamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td  class="tablegreybg form-group isdtab" align="right"><input type="text" class="form-control text-right" id="isdtotal_text1" name="items[0].total" readonly="readonly"></td></tr><tr id="2" class="item_edit2"><td id="sno" align="center">2</td><td id="particular_row2" class="form-group particuler_type"><label>Eligible - Credit distributed as  </label><input type="text"  id="particular_val2" name="items[1].isdType" value="Eligible - Credit distributed as"   style="display:none;"/></td><td id="isdigsttax_row2" class="form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype2 tax_type text-right" id="isdigsttax_text2" name="items[1].igstamount"  onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$" onkeyup="findIsdTaxAmount(2)"></td><td id="isdcgsttax_row2" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype2 tax_type text-right" id="isdcgsttax_text2" name="items[1].cgstamount" onkeyup="findIsdTaxAmount(2)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdsgsttax_row2" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype2 tax_type text-right" id="isdsgsttax_text2" onkeyup="findIsdTaxAmount(2)" name="items[1].sgstamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdisdcess_row2" class="form-group noadvFlag isdcess_amount isdtab" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" onkeyup="findIsdTaxAmount(2)" id="isdisdcess_text2" name="items[1].isdcessamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdtotal_row2" class="tablegreybg form-group isdtab" align="right"><input type="text" class="form-control text-right" id="isdtotal_text2" name="items[1].total" readonly="readonly"></td></tr><tr id="3" class="item_edit3"><td id="sno" align="center">3</td><td id="particular_row3" class="form-group particuler_type"><label>Ineligible - Credit distributed   </label><input type="text"  id="particular_val3" name="items[2].isdType" value="Ineligible - Credit distributed"    style="display:none;"/></td><td id="isdigsttax_row3" class="form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype3 tax_type text-right" id="isdigsttax_text3" name="items[2].igstamount" onkeyup="findIsdTaxAmount(3)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdcgsttax_row3" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype3 tax_type text-right" id="isdcgsttax_text3" name="items[2].cgstamount" onkeyup="findIsdTaxAmount(3)"  onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdsgsttax_row3" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype3 tax_type text-right" id="isdsgsttax_text3" onkeyup="findIsdTaxAmount(3)" name="items[2].sgstamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdisdcess_row3" class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" onkeyup="findIsdTaxAmount(3)" id="isdisdcess_text3" name="items[2].isdcessamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdtotal_row3" class="tablegreybg form-group isdtab" align="right"><input type="text" class="form-control text-right" id="isdtotal_text3" name="items[2].total" readonly="readonly"></td></tr><tr id="4" class="item_edit4"><td id="sno" align="center">4</td><td id="particular_row4" class="form-group particuler_type"><label>Ineligible - Credit distributed as</label><input type="text"  id="particular_val4" name="items[3].isdType" value="Ineligible - Credit distributed as" style="display:none;"/></td><td id="isdigsttax_row4" class="form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype4 tax_type text-right" id="isdigsttax_text4" name="items[3].igstamount" onkeyup="findIsdTaxAmount(4)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdcgsttax_row4" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype4 tax_type text-right" id="isdcgsttax_text4" name="items[3].cgstamount" onkeyup="findIsdTaxAmount(4)"  onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdsgsttax_row4" class=" form-group SnilFlag isdtab" align="right"><input type="text" class="form-control isdtype4 tax_type text-right" id="isdsgsttax_text4" onkeyup="findIsdTaxAmount(4)" name="items[3].sgstamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdisdcess_row4" class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" onkeyup="findIsdTaxAmount(4)" id="isdisdcess_text4" name="items[3].isdcessamount" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="isdtotal_row4" class="tablegreybg form-group isdtab" align="right"><input type="text" class="form-control text-right" id="isdtotal_text4" name="items[3].total" readonly="readonly"></td></tr></tbody>
              <tfoot id="allinvoicettfoot1">
                <tr><th colspan="2" class="tfootwitebg nil-foot"><span class="pull-right SnilFlag">Total Inv. Val</span></th><th class="tfootbg indformat" id="isdtotIGST">0.00</th><th class="tfootbg indformat" id="isdtotCGST">0.00</th><th class="tfootbg indformat SnilFlag" id="isdtotSGST">0.00</th><th class="tfootbg indformat SnilFlag" id="isdtotisdcess" style="display:none;border:none;">0.00</th><th class="tfootbg indformat" id="isdtotTotal"></th></tr>
              </tfoot>
            </table>
          <div class="col-12 mt-2  form-group-inline p-0" style="display:block">
            <div class="sortable-form">
              <div class="row no_vehicle">
       		 <div id="reversecharge" class="col-md-2 col-sm-12 reversechargeDiv specific_field <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2CLA%> <%=MasterGSTConstants.TXPA%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.IMP_SERVICES%> <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.CDNURA%>">
                  <h6 id="inv_exports1">Reverse Charge :</h6>
                  <select class="form-control" name="revchargetype" id="revchargetype"><option value="Regular">No</option><option value="Reverse">Yes</option></select>
                </div>
                <div class="col-md-2 col-sm-12 revChargeNoDiv" id="revChargeNoDiv" style="display:none;">
	                 <h6 id="rev_no">Reverse Charge Sr. Number :</h6>
	                  <input type="text" class="form-control invedit" name="revchargeNo" id="revchargeNo"/><div id="revNoMissing"></div>
	                  <span class="revInvno" style="display:none;font-size: 10px; color: green;">Previous Reverse Charge No:<span class="previousRevChargeNo" style="font-size:9px;"></span> </span>
                </div>
                <div class="col-md-2 col-sm-12"><h6>Reference : </h6><input id="referenceNumber" name="referenceNumber" placeholder="P.O Number or Any other text" class="form-control invedit"/><span class="control-label"></span><div class="help-block with-errors"></div></div>
				<div class="col-md-2 col-sm-12"><h6>Branch :</h6><select class="form-control invedit" name="branch" id="branch"></select></div>
				<div class="col-md-2 col-sm-12"><h6>Vertical :</h6><select class="form-control invedit" name="vertical" id="vertical"></select></div>
               	<!-- <div class="col-md-2 col-sm-12 rounoff" id="roundoffdiv" style="height:63px!important"></div> -->
                <!-- <div class="col-md-2 col-sm-12 revDiv" id="revDiv" style="height:63px!important;display:none;"></div> -->
                <div class="col-md-2 pr-0 mt-2  pl-2 specific_field <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CDNURA%> <%=MasterGSTConstants.B2B%> <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.CDNUR%> <%=MasterGSTConstants.CREDIT_DEBIT_NOTES%> <%=MasterGSTConstants.CDNA%> <%=MasterGSTConstants.B2C%> <%=MasterGSTConstants.B2BA%> <%=MasterGSTConstants.B2CSA%> <%=MasterGSTConstants.B2CLA%> <%=MasterGSTConstants.CDNURA%> <%=MasterGSTConstants.EXPA%> <%=MasterGSTConstants.TXPA%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.B2BUR%> <%=MasterGSTConstants.PROFORMAINVOICES%> <%=MasterGSTConstants.ESTIMATES%> <%=MasterGSTConstants.PURCHASEORDER%>" id="diffPer">
            <div class="col-md-12 col-sm-12 pl-0 pr-0"><h6>&nbsp;</h6><div class="form-check mb-sm-0"><div class="meterialform"><div class="checkbox che_form-control ml-0" id="diffpercent"><label style="font-size: 12px;padding-left: 18px;"><input class="diffPercent" id="diffPercent" type="checkbox" name="diffPercent" value="No"><i class="helper"></i><strong> Differential Percentage(0.65)</strong></label></div></div></div></div>
                </div>
                <div class="col-md-4 col-sm-12 rounoff specific_field <%=MasterGSTConstants.ISDA%> <%=MasterGSTConstants.ITC_REVERSAL%> <%=MasterGSTConstants.DELIVERYCHALLANS%> <%=MasterGSTConstants.NIL%>" id="roundoffdiv" style="height:63px!important"></div>
                 <div class="col-md-2 col-sm-12 rounoff  advsupport_div specific_field <%=MasterGSTConstants.PROFORMAINVOICES%> <%=MasterGSTConstants.ADVANCES%> <%=MasterGSTConstants.EXPORTS%> <%=MasterGSTConstants.ATPAID%> <%=MasterGSTConstants.ESTIMATES%>  <%=MasterGSTConstants.PURCHASEORDER%>"></div>
                 <div class="col-md-2 col-sm-12 rounoff advsupport_div specific_field <%=MasterGSTConstants.ADVANCES%> <%=MasterGSTConstants.ATPAID%>"></div>
                 <div class="gstr1b2binv"></div>
                <div class="col-md-2 col-sm-12 form-group rounoff pl-0" id="roundOffTotalAmount"><h6>&nbsp;</h6>
                  <input class="form-control col-xs-2" name="roundOffAmount" id="roundOffTotalValue" style="width: 60px;float:right;margin-right:30px" readonly/>                   
				  <div class="form-check mb-2 mb-sm-0 ml-0" style="float:left"><div class="meterialform"><div class="checkbox pull-right mr-0 ml-0" id="roundOffTotal" style="margin-top:-2px;">
                        <label style="font-size: 12px;padding-left: 27px;"><input class="roundOffTotal" type="checkbox" id="roundOffTotalChckbox" name="roundOffTotal"><i class="helper"></i><strong>Roundoff Total</strong></label>
                  </div></div></div>
                </div>
              </div>
            </div>
            <div class="bdr-b"></div>
            <div class="row no_vehicle">
            <div class="col-md-8 row">
            <div class="col-md-6 col-sm-12 form-group specific_field <%=MasterGSTConstants.ITC_REVERSAL%> <%=MasterGSTConstants.ADVANCES%>" id="customer_notes_wrap_adj"><label for="inlineFormInput" class="bold-txt">Customer Notes</label><textarea class="form-control mb-2 mr-sm-2 mb-sm-0" id="bankNotesadv" style="min-height:80px!important" name="notes">Customer Notes</textarea></div>
                <div class="tcsConfigDiv"></div>
                <div class="col-md-3 col-sm-12 form-group Terms_Conditions_wrap termsDueDate mb-0 pt-0" id="Terms_Conditions_wrap">
					<label for="inlineFormInput" class="bold-txt">Terms</label>
					<span class="term-span bold-txt">Days</span>
					<input type="text" class="form-control mb-2 mb-sm-0 invedit" id="termDays" name="termDays" pattern="^[0-9]*$" placeholder="Payment Term Days" onkeyup = "paymentterm()"  onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" data-error="Please Enter Number"><span class="control-label"></span><div class="help-block with-errors"></div>
			</div>
				<div class="col-md-3 col-sm-12 Terms_Conditions_wrap has-feedback termsDueDate mb-0 pl-4  pt-0"><i class="fa fa-calendar cal-inv" style="margin-top:29px;"></i><label for="inlineFormInput" class="bold-txt">Due Date<span class="dateddlable" >(DD/MM/YYYY)</span></label><input class="form-control invedit" id="duedate_div" name="dueDate" /></div>
                <div class="col-md-3 col-sm-12 bnkdetail mb-0"><h6>&nbsp;</h6><div class="form-check mb-2 pl-3  mb-sm-0"><div class="meterialform"><div class="checkbox pull-right" id="bankCheck" style="margin-top:-2px;width:100%"><label><input class="addBankDetails" type="checkbox" name="addBankDetails"><i class="helper"></i> Add Bank Details</label></div></div></div></div>
                <div class="col-md-3 col-sm-12 form-group bnkdetail mb-0 pt-0 pl-4 "><div id="selectBankDiv" class="pl-3"><label for="inlineFormInput" class="bold-txt">Select Bank</label><select class="form-control mb-2 mr-sm-2 mb-sm-0 selectBank invedit" id="selectBank" onchange="selectBankName()"></select></div></div>
                 <div class="col-md-6 col-sm-12 form-group pt-0" id="customer_notes_wrap"><label for="inlineFormInput" class="bold-txt">Customer Notes</label><textarea class="form-control mb-2 mr-sm-2 mb-sm-0 invedit" id="bankNotes" style="min-height:80px!important;max-width: 100%;" name="notes">Customer Notes</textarea></div>
              <!-- <div class="col-md-4 col-sm-12 form-group" id="Terms_Conditions_wrap"><label for="inlineFormInput" class="bold-txt">Terms & Conditions</label><textarea class="form-control mb-2 mr-sm-2 mb-sm-0" id="bankTerms" style="min-height:80px!important" name="terms">Terms & Conditions</textarea></div> -->
			 <div class="col-md-3 col-sm-12 form-group  specific_field <%=MasterGSTConstants.ITC_REVERSAL%>"></div>
			 <div class="col-md-6 col-sm-12 form-group  specific_field <%=MasterGSTConstants.ADVANCES%>"></div>
			  <div class="col-md-6 col-sm-12 form-group backdetails_wrap pr-0"> 
			   <div class="form-inline bank-details-box sortable-form"  id="bank_details" style="display:none" >
              <div class="col-12 pr-0" style="display:block">
              <div class="row">
             <div class="col-md-6 col-sm-12 form-group"> <label for="inlineFormInput" class="bold-txt">Bank Name</label><input type="text" class="form-control mb-2 mb-sm-0 invedit" id="bankName" name="bankDetails.bankname" style="margin-top:5px;margin-bottom:5px!important" placeholder="Bank Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" data-error="Please enter Bank Name"><span class="control-label"></span><div class="help-block with-errors"></div></div>             
              <div class="col-md-6 col-sm-12 form-group bacno pl-4"><label for="inlineFormInput" class="bold-txt">Account Number</label><input type="text" class="form-control mb-2 mb-sm-0 invedit" id="bankAcctNo" name="bankDetails.accountnumber" style="margin-top:5px;margin-bottom:5px!important"  pattern="^[0-9]*$" placeholder="Account Number" onKeyPress="return ((event.charCode >= 48 && event.charCode <= 57) || event.charCode == 0 || event.which === 8)" data-error="Please Enter Account Number"><span class="control-label"></span><div class="help-block with-errors"></div></div> 
              <div class="col-md-6 col-sm-12 form-group"><label for="inlineFormInput" class="bold-txt">Account Name</label><input type="text" class="form-control mb-2 mb-sm-0 invedit" id="bankAccountName" name="bankDetails.accountName" placeholder="Account Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$"><span class="control-label"></span><div class="help-block with-errors"></div></div>               
			  <div class="col-md-6 col-sm-12 form-group pl-4"><label for="inlineFormInput" class="bold-txt">Branch Name</label><input type="text" class="form-control mb-2 mb-sm-0 invedit" id="bankBranch" name="bankDetails.branchname" placeholder="Branch Name" onkeypress="return ((event.charCode >= 65 && event.charCode <= 90) || (event.charCode >= 97 && event.charCode <= 122) || (event.charCode == 32))" pattern="[a-zA-Z ]*$" data-error="Please enter Branch Name"><span class="control-label"></span><div class="help-block with-errors"></div></div>             
			  <div class="col-md-6 col-sm-12 form-group bifscno"><label for="inlineFormInput" class="bold-txt">IFSC Code</label><input type="text" class="form-control mb-2 mb-sm-0 invedit" id="bankIFSC" name="bankDetails.ifsccode" placeholder="IFSC Code" onkeypress="return ((event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || event.charCode == 8 || (event.charCode >= 48 && event.charCode <= 57))" pattern="^[a-zA-Z0-9]*$" data-error="Please enter IFSC Code"><span class="control-label"></span><div class="help-block with-errors"></div></div>
              </div></div></div></div>
              </div>
               <div class="col-md-4 col-sm-12 pr-0">
                 <div class="col-md-12 row pr-0 m-0" >
                <div class="col-md-6 form-group bs-fancy-checks pl-2 pr-0 pl-2 " id="entdsortcs">
                			<h6>&nbsp;</h6>
                            <span class="mr-2 lable-txt" style="font-size:12px;">Enable TCS :</span>
                            <div class="form-check form-check-inline">
                                    <input class="form-check-input tcsval invedit disclass disabled" name="tdstcsenable" type="checkbox" id="tcsval" value="false"  onchange="tcscheckval()"    />
                                    <label for="tcsval" style="margin-top: -7px;"><span class="ui"></span>
                                    </label> 
                                </div>
                 </div>
                  
                 <!-- <div class="form-check col-md-6 mb-sm-0 pl-2 gstr2TcsDiv" id="notdsval">
                 <span class="lable-txt">Do you want to add : </span>
                 	 <div class="form-group-inline meterialform  row pl-3">
                                <div class="form-radio ">
                                    <div class="radio">
                                    	<label> 
                                        	<input name="tcsorTdsType" class="tcsorTdsType invedit disclass disabled"  id="tds_val" type="radio" value="tds" checked/>
                                            <i class="helper"></i>TDS</label>
                                    </div>
                                </div>
                                <div class="form-radio">
                                    <div class="radio">
                                        <label>
                                            <input name="tcsorTdsType" class="tcsorTdsType invedit" id="tcs_val" type="radio" value="tcs"/>
                                            <i class="helper"></i>TCS</label>
                                    </div>
                                </div>
                 </div>
                 </div> -->
                 <div class="col-md-6 row pr-0 pl-3">
                <div class="col-md-6 col-sm-12 form-group mb-0" id="section" ><label for="sectionlable" class="bold-txt" style="font-size:12px;">Section</label> <span class="errormsg section"></span><select class="form-control section invedit" id="tdstcssection" name="section" onchange="tdstcschange()"  style="width: 70px;max-width: unset;min-width: unset;" aria-describedby="section" placeholder="Section"  data-error="Please enter section Name" readonly disabled ></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
			 	<div class="col-md-6 col-sm-12 form-group mb-0 p-0" id="Tcs_percent"><label for="inlineFormInputGroup" class="bold-txt perc_type" style="font-size:12px;">TCS Percentage</label><input id="tcs_percent" name="tcstdspercentage" class="form-control invedit" pattern="^[0-9]+(\.[0-9]{1,10})?$"  style="width: 96px;max-width: unset;min-width: unset;" onkeyup="tdstcscal()" data-error="Please enter valid value" readonly/><span class="control-label"></span><div class="help-block with-errors"></div></div>
			 	</div>
			 	
			 	 <div class="col-md-6 form-group bs-fancy-checks pl-2 pr-0 pl-2 " id="entds">
                			<h6>&nbsp;</h6>
                            <span class="mr-2 lable-txt" style="font-size:12px;">Enable TDS :</span>
                            <div class="form-check form-check-inline">
                                    <input class="form-check-input tdsval invedit disclass" name="tdsenable" type="checkbox" id="tdsval" value="false"  onchange="tdscheckval()"    />
                                    <label for="tdsval" style="margin-top: -7px;"><span class="ui"></span>
                                    </label> 
                                </div>
                 </div>    
			 	
			 	<div class="col-md-6 row pr-0 pl-3 tdsDiv">
                <div class="col-md-6 col-sm-12 form-group mb-0" id="tds_section" ><label for="sectionlable" class="bold-txt" style="font-size:12px;">Section</label> <span class="errormsg tds_section"></span><select class="form-control section invedit" id="tdssection" name="tdsSection" onchange="tdschange()"  style="width: 70px;max-width: unset;min-width: unset;" aria-describedby="section" placeholder="Section"  data-error="Please enter section Name" readonly disabled ></select><span class="control-label"></span><div class="help-block with-errors"></div></div>
			 	<div class="col-md-6 col-sm-12 form-group mb-0 p-0" id="Tds_percent"><label for="inlineFormInputGroup" class="bold-txt" style="font-size:12px;">TDS Percentage</label><input id="tds_percent" name="tdspercentage" class="form-control invedit" pattern="^[0-9]+(\.[0-9]{1,10})?$"  style="width: 96px;max-width: unset;min-width: unset;" onkeyup="tdscal()" data-error="Please enter valid value" readonly/><span class="control-label"></span><div class="help-block with-errors"></div></div>
			 	</div>
			 	
			 	
			 	</div>
                 <div class="col-md-12 col-sm-12 form-group">
               
               <!-- <h6 class="mb-2 mt-3 customFields_row">Additional Fields</h6> -->
				<div class="row" id="customFields_row">
				</div>
			 </div>
                </div>
                </div>
				<div id="vehicletxt" style="display:none;"><p class="label-txt mt-2" style="margin-bottom: 0px;font-weight: 600;font-size: 14px;text-decoration: underline;">PART-B/Vehicle Details</p></div>
				<table align="center" id="vehicle_table" border="0" class="vehicleTable row-border dataTable mt-1" style="display:none;"><thead><tr><th rowspan="2">S.No</th><th class="astrich vehicledetails">Vehicle Number</th><th class="astrich vehicledetails">Trans Mode</th><th>Vehicle Type</th><th class="vehicle_updtrsn" style="display:none;">Remarks</th><th>Transport Document Number</th><th>Transport Document Date</th><th></th></tr></thead> <tbody id="vehicleDetailsBody"><tr id="1" class="rowshadow addrowshadow"><td id="sNo_row1" align="center">1</td><td class="form-group"><input type="text" class="form-control ewaybillvehicledetails" id="vehicleNo1" name="vehiclListDetails[0].vehicleNo" maxlength="15" minlength="0" required></td><td class="form-group"><select  class="form-control ewaybillvehicledetails" id="transMode1" name="vehiclListDetails[0].transMode" required><option value="">-Select-</option><option value="1">Road</option><option value="2">Rail</option><option value="3">Air</option><option value="4">Ship</option></select></td><td><select class="form-control" name="vehiclListDetails[0].vehicleType" id="vehicleType1"><option value="">-Select type-</option><option value="R">Regular</option><option value="O">Over Dimensional Cargo</option></select></td><td class="vehicle_updtrsn" style="display:none;"><select class="form-control reasonrem reasonRem1" id="reasonRem1" onchange="reasonCodeSelection(1)"><option value="">-Select-</option><option value="Due to Break Down">Due to Break Down</option><option value="Due to Transhipment">Due to Transhipment</option><option value="Others (Pls. Specify)">Others (Pls. Specify)</option><option value="First Time">First Time</option></select></td><td><input type="text"  class="form-control" id="transDocNo1" maxlength="15"  pattern="^[0-9a-zA-Z/ -]+$" onkeypress="return ((event.charCode >= 65 &amp;&amp; event.charCode <= 90) || (event.charCode >= 97 &amp;&amp; event.charCode <= 122) || (event.charCode >= 48 &amp;&amp; event.charCode <= 57) || (event.charCode == 0) || (event.charCode == 45) || (event.charCode == 47))" name="vehiclListDetails[0].transDocNo"></td><td><input type="text" class="form-control" id="transDocDate1" name="vehiclListDetails[0].transDocDate"></td><td align="center" width="2%"><!-- <a href="javascript:void(0)" id="delete_btn1" class="vehicle_delete disabled" onclick="delete_vehiclerow(1)"> <span class="fa fa-trash-o gstr2adeletefield"></span> </a> --> </td></tr></tbody>
				<!-- <tfoot><th colspan="5" class="text-left"><i class="add-btn ebilladdrow disabled"  onclick="addEbillrow('vehicle_table')" style="cursor: pointer;">+</i><span style="color:black">Add Another Row</span></th><th colspan="9" style="text-align: right;"><i class="add-btn ebilladdrow disabled"  onclick="addEbillrow('vehicle_table')" style="cursor: pointer;">+</i></th></tfoot> -->
				</table>
			<!-- <div class="row mt-2 notes_terms_wrap">
              
              </div> -->
              <div class="sortable-form">
              
				  <div class="row">
					  <div id="premium_div" class="col-md-2 col-sm-12" style="display:none;">
		                  <h6 id="premium_text">Premium :</h6>
		                  <input class="form-control invedit" name="premium" id="premium">
	                </div>
	                <div id="period_div" class="col-md-2 col-sm-12" style="display:none;">
		                  <h6 id="period_text">Period :</h6>
		                  <input class="form-control invedit" name="period" id="period">
	                </div>
				  </div>
            </div>
          </div>
          <div class=""></div>
        </div>
      </div>
      <div class="modal-footer spinvoiceno">
        <div class="row col-12 pl-0 pr-2 " style="display: flex;align-items: center;justify-content: center;">
            <input type="button" class="btn btn-primary mr-2"  value="Save As Draft" id="draft_btn" onclick="saveEwayBIllInvDraft('EWAYBILL')"  style="display:none;"/>
            <input type="button" class="btn btn-greendark mr-2 permissionEwaybill_Actions-Generate_Ewaybill" value="Generate & Save" id="ewayBillSave_btn"  aria-label="Close" onclick="saveEwayBIllInv('EWAYBILL')"  style="display:none;"/><input type="button" class="btn btn-greendark mr-2"  value="Update Vehicle" id="vehicleupDt" onclick="vehicleupdate('EWAYBILL')"  style="display:none;"/><button type="button" class="btn btn-greendark mr-2 btn_popup_cancel" id="cancelEwayBillInvoice" style="display:none;">Cancel EwayBill</button>
			<c:if test="${varRetType ne 'EWAYBILL'}"><button type="button" class="btn btn-primary mr-2 btn_popup_cancel" id="cancelInvoice">Cancel Invoice</button><button type="button" class="btn btn-primary mr-2 btn_popup_amnd permissionInvoices-Edit_As_Amendment-Add" style="display:none;">Edit As Amendment</button></c:if>
			<button type="button" class="btn btn-primary mr-2" id="convert_PI_Invoice" style="display:none;">Convert To Invoice</button><button type="button" class="btn btn-primary mr-2" id="convert_PO_Invoice" style="display:none;">Convert To Invoice</button>
			<c:if test="${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6}"><button type="submit" class="btn btn-primary mr-2 btn_popup_save permissionInvoices-Purchase-Edit notewaybilladdress">Save</button></c:if>
			<c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2 && varRetType ne varGSTR6}"><button type="submit" class="btn btn-primary mr-2 btn_popup_save permissionInvoices-Sales-Edit notewaybilladdress" id="save_btn">Save</button></c:if>
			 <%-- <c:if test="${varRetType ne varPurchase && varRetType ne varGSTR2 && varRetType ne varGSTR6 && varRetType ne 'EWAYBILL'}"><input type="button" class="btn btn-primary mr-2 save-mail" id="emailSend_btn" value="Save & Email"></c:if> --%><button type="button" class="btn btn-primary mr-2" onclick="closemodal('invoiceModal')">Close</button>
        </div>
		<span style="display:none" id="invnotes">${client.notes}</span><span style="display:none" id="invnotess">${client.notes}</span><span style="display:none" id="invterms">${client.terms}</span><span style="display:none" id="invtermss">${client.terms}</span><span style="display:none" id="originalinvamt"></span>
         <input type="hidden" id="paymentStatusinv" name="paymentStatus" value=""/><input type="hidden" id="InvPendingAmount" name="pendingAmount" value=""/><input type="hidden" id="InvReceivedAmount" name="receivedAmount" value=""/><input type="hidden" id="reasonCode" value=""/><input type="hidden" id=vendorName name="vendorName"/><input type="hidden" id="cdn_taxableamount" name="cdn_taxableamount"><input type="hidden" id="originalinvamt" class="originalinvamount" name="originalinvamount">
		<input type="hidden" id="irnnumber" value=""><input type="hidden" id="validUptoDate" name="validUpto" value=""><input type="hidden" id="ewaybillRejectDate" name="ewaybillRejectDate" value=""><input type="hidden" id="invLevelCess" name ="invoiceLevelCess" value=""><input type="hidden" name="srctype" id="srctype" value=""><input type="hidden" name="invoiceCustomerId" id="invoiceCustomerId" value=""><input type="hidden" name="invoiceEcomOperator" id="invoiceEcomOperator" value=""><input type="hidden" name="companyDBId" id="companyDBId" value=""><input type="hidden" name="notroundoftotalamount" id="hiddenroundOffTotalValue"/><input type="hidden" name="id" id="invoiceid" value=""><input type="hidden" name="invtype" id="idInvType" class="form-control" value=""><input type="hidden" name="dealerType" id="dealerType" value="">
		<input type="hidden" name="userid" id="userid" value="<c:out value="${id}"/>"><input type="hidden" name="fullname" id="fullname" value="<c:out value="${fullname}"/>"><input type="hidden" name="clientid" id="clientid" value="<c:out value="${client.id}"/>"><input type="hidden" id="clientAddress" name="clientAddress" value=""><input type="hidden" name="strAmendment" id="strAmendment">
		<div class="specific_field <%=MasterGSTConstants.PROFORMAINVOICES%>" style="display:none"><input type="hidden" name="convertedtoinv" id="convertedtoinv" value=""></div>
		<c:if test='${not empty otherreturnType}'><input type="hidden" name="retType" id="retType" class="sd" value="<c:out value="${otherreturnType}"/>"></c:if>
		<c:if test='${empty otherreturnType}'><input type="hidden" name="retType" id="retType" value="<c:out value="${varRetType}"/>"></c:if><input type="hidden" id="originalInvDate" value=""/>
		<input type="hidden" id="customerMail" name="customerEmail" value=""><input type="hidden" name="matchingId" id="matchingId" value="<c:out value="${invoice.matchingId}"/>"><input type="hidden" name="matchingStatus" id="matchingStatus" value="<c:out value="${invoice.matchingStatus}"/>">
		<input type="hidden" id="customer_MailId" name="customerMailIds" value=""/><input type="hidden" id="customer_CCMailId" name="customerCCMailIds" value=""/>
		<!--<div class="ewaybilladdress ewayaddrfieldss" style="display:none"></div>-->
					<c:if test="${varRetType eq 'EWAYBILL'}"><input type="hidden" name="buyerDtls.gstin" id="ewybuyerDtls_gstin"/>
					<input type="hidden" name="buyerDtls.lglNm" id="ewybuyerDtls_lglNm"/>
				   <input type="hidden" name="buyerDtls.addr1" id="ewybuyerDtls_addr1"/>
				   <input type="hidden" name="buyerDtls.addr2" id="ewybuyerDtls_addr2"/>
				   <input type="hidden" name="buyerDtls.loc" id="ewybuyerDtls_loc"/>
				    <input type="hidden" name="buyerDtls.pin" id="ewybuyerDtls_pin"/>
				   <input type="hidden" name="buyerDtls.state" id="ewybuyerDtls_state"/>
				   <input type="hidden" name="buyerDtls.pos" id="ewybuyerDtls_pos"/>
				 
				 <input type="hidden" name="dispatcherDtls.nm" id="ewydispatcherDtls_nm"/>
				   <input type="hidden" name="dispatcherDtls.addr1" id="ewydispatcherDtls_addr1"/>
				    <input type="hidden" name="dispatcherDtls.addr2" id="ewydispatcherDtls_addr2"/>
				   <input type="hidden" name="dispatcherDtls.loc" id="ewydispatcherDtls_loc"/>
				    <input type="hidden" name="dispatcherDtls.pin" id="ewydispatcherDtls_pin"/>
				   <input type="hidden" name="dispatcherDtls.stcd" id="ewydispatcherDtls_stcd"/>
				   
				   
				    <input type="hidden" name="shipmentDtls.gstin" id="ewyshipmentDtls_gstin"/>
				   <input type="hidden" name="shipmentDtls.trdNm" id="ewyshipmentDtls_trdNm"/>
				    <input type="hidden" name="shipmentDtls.lglNm" id="ewyshipmentDtls_lglNm"/>
				   <input type="hidden" name="shipmentDtls.addr1" id="ewyshipmentDtls_addr1"/>
				    <input type="hidden" name="shipmentDtls.addr2" id="ewyshipmentDtls_addr2"/>
				   <input type="hidden" name="shipmentDtls.loc" id="ewyshipmentDtls_loc"/>
				   <input type="hidden" name="shipmentDtls.pin" id="ewyshipmentDtls_pin"/>
				    <input type="hidden" name="shipmentDtls.stcd" id="ewyshipmentDtls_stcd"/>
					</c:if>
		</form:form>
      </div>
    </div>
  </div>
</div>
<script type="text/javascript">
var return_type='';var ipAddress = '';	var ewayBillStatus = '';var ewayBillNo='';var otherconfigdetails= null;var category = null, currRowIndex=null,itemrowCount=1, vehiclerowCount=1,rowCount=1, termsRowCount=1,interStateFlag=true, itcStateFlag = false;var clntStatename = '<c:out value="${client.statename}"/>';var dealertype = "";
	var invConfigUrl='${contextPath}/cp_upload/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/${month}/${year}?type=';var usertype = '<c:out value="${usertype}"/>';
	var addITCFlag = false,noadvFlag = false, addAdvFlag = false,addITCRuleFlag = false,addSnilFlag=false,SnilFlag=true,AdvanceFlag=true, CreditFlag=true; var invoiceNumber;var ewayBillNo;var clntlutNumber = '<c:out value="${client.lutNumber}"/>';
	var customFieldsData=null;var paymentTerms=null;var clientSignatureDetails= null;var emailReturnType='${varRetType}';
	var enableDiscount = '${client.allowDiscount}';var enablePurDiscount = '${client.allowPurDiscount}';var enableExempted= '${client.allowExempted}';var enableLedger= '${client.allowLedgerName}';var enablePurLedger = '${client.allowPurLedgerName}';var enableSalesCess = '${client.allowSalesCess}';var enablePurCess= '${client.allowPurCess}';
	$(document).ready(function(){
		$('.addITCFlag,.addITCRuleFlag,.addSnilFlag').hide();$('.SnilFlag,.AdvancesFlag,.noadvFlag').show();$('#selectBankDiv').show();$('#selectBank,#selectBank1').attr("disabled","disabled");
		if('${varRetType eq varPurchase || varRetType eq varGSTR2 || varRetType eq varGSTR6}' == 'true') {addITCFlag = true;addITCRuleFlag = true;$('.addITCFlag,.addITCRuleFlag').show();$('.addBankDetails').hide();$('#selectBankDiv').hide();}
		$( "#dateofinvoice" ).change(function() {
			  var invoicedate = $('#dateofinvoice').val();
			  var minimumdate = "";
			  var rtype = $('#retType').val();
				if(rtype == 'GSTR1'){
					minimumdate = "${client.cutOffDateForSales}";
				}else if(rtype == 'GSTR2'){
					minimumdate = "${client.cutOffDateForPurchases}";
				}
				if(minimumdate == ''){
					minimumdate = "30/1/1970";
				}else{
					var invres = invoicedate.split("/");
					var invmdte = invres[2]+"-"+invres[1]+"-"+invres[0];
					var minres = minimumdate.split("/");
					var minmdte = minres[2]+"-"+minres[1]+"-"+minres[0];
					var g1 = new Date(invmdte); 
					var g2 = new Date(minmdte);
					if (g1.getTime() <= g2.getTime()) {
						
						$('#dateofinvoice').val('');
						var form = $('#salesinvoceform');
						var day = g2.getDate() + "";var month = (g2.getMonth() + 1) + "";	var year = g2.getFullYear() + "";
						day = checkZero(day);month = checkZero(month);year = checkZero(year);
						var mndate = day + "/" + month + "/" + year;
						form.find("input[id='dateofinvoice']").val(mndate);
					}
				}
		  });
		var clientid = $('#clientid').val();
		$.ajax({
			url: "${contextPath}/otherconfiglist/${client.id}",
			async: false,
			cache: false,
			success : function(response) {otherconfigdetails = response;},
			error : function(e) {}
		});
		/*$.ajax({
			url: "${contextPath}/getTerms/${client.id}",
			async: false,
			cache: false,
			success : function(response) {paymentTerms = response;},
			error : function(e) {}
		});
		showTermsList("invpopup");*/
		$.ajax({
			url: "${contextPath}/getCustomFields/${client.id}",
			async: false,
			cache: false,
			success : function(response) {customFieldsData = response;},
			error : function(e) {}
		});
		var ledgeroptions = {
				url: function(phrase) {
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return "${contextPath}/ledgerlist/${client.id}?query="+ phrase + "&format=json";
				},
				getValue: "ledgerName",
				list: {
					match: {enabled: true},
				onChooseEvent: function() {
					var groupdetails = $("#ledgerName").getSelectedItemData();
				}, 
					onLoadEvent: function() {
						if($("#eac-container-ledgerName ul").children().length == 0) {$("#addlegername").show();} else {$("#addlegername").hide();}
					},
					maxNumberOfElements: 10
				},
			};
		$('#ledgerName').easyAutocomplete(ledgeroptions);
		$("#ledgerName").parent().parent().mouseleave(function() {$("#addlegername").hide();});
		var bstateoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onClickEvent: function() {
					var rettype = $('#retType').val();updateRateType('<c:out value="${client.statename}"/>',rettype);
				},
				onLoadEvent: function() {
					if($("#eac-container-billedtostatecode ul").children().length == 0) {$("#billedtostatecodeempty").show();} else {$("#billedtostatecodeempty").hide();}
				},
				maxNumberOfElements: 37
			}
		};
		var bstateoptions1 = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				return "${contextPath}/stateconfig?query="+ phrase + "&format=json";
			},
			getValue: "name",
			list: {
				onClickEvent: function() {
					var rettype = $('#retType').val();updateRateType('<c:out value="${client.statename}"/>',rettype);
				},
				onLoadEvent: function() {
					if($("#eac-container-state ul").children().length == 0) {$("#stateempty").show();} else {$("#stateempty").hide();}
				},
				maxNumberOfElements: 37
			}
		};
		var retturntype = $('#retType').val();
		$("#billedtostatecode").easyAutocomplete(bstateoptions);
		$("#state").easyAutocomplete(bstateoptions1);$("#billedtostatecode1").easyAutocomplete(bstateoptions);
		function checkZero(data){
			  if(data.length == 1){data = "0" + data;}
			  return data;
			}
			$('#abb1').hover(function() {$("#tax_rate_drop").css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"10%","margin-top":"5px"});
				  }, function() {$("#tax_rate_drop").css("display","none");}
			);
			$('.ledger-lable').hover(function() {$(".ledger-drop").css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"6px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"86%","margin-top":"-64px","font-size":"11px"});
				  }, function() {$(".ledger-drop").css("display","none");}
			);
			$('#itc_tax_tot1').hover(function() {$("#itctax_rate_drop1").css({"display":"block","background-color":"#fff","border":"1px solid #f5f5f5","padding":"10px","position":"absolute","z-index":"1","box-shadow":"0px 0px 5px 0px #e5e5e5","width":"9%"});
			  }, function() {$("#itctax_rate_drop1").css("display","none");}
			);
		var invoiceNumberoptions = {
				url: function(phrase) {
					var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#idInvType').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
					phrase = phrase.replace('(',"\\(");
					phrase = phrase.replace(')',"\\)");
					return "${contextPath}/invoiceNumbers/"+mn+"/"+yr+"/"+rettype+"/"+invtype+"/?query="+ phrase + "&clientid=${client.id}&billedToName="+$('#billedtoname').val()+"&format=json";
				},
				getValue: function(element) {
					var invDate = new Date(element.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year; var invnoAndDate = element.invoiceno+"("+invoiceDate+")";
					return invnoAndDate;
				},
				list: {
					onChooseEvent: function() {var rettype = $('#retType').val();
						var invoice = $("#voucherNumber").getSelectedItemData();$('.originalinvamount').val(invoice.totaltaxableamount);$('#voucherNumber').val('');var invDate = new Date(invoice.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year;$('#billedtoname').val(invoice.billedtoname);$('#billedtostatecode').val(invoice.statename).trigger("change");$('#billedtogstin').val(invoice.b2b[0].ctin);$('#voucherDate').val(invoiceDate);$('#voucherNumber').val(invoice.invoiceno).trigger("change");updateRateType('<c:out value="${client.statename}"/>', rettype);
						if(invoice.lutNo != '' && invoice.lutNo != null){$('#lutDiv').css("display","block");$('#lutNo').val(invoice.lutNo);}else{$('#lutDiv').css("display","none");$('#lutNo').val('');}
						if(invoice.b2b[0].ctin == ''){
							if((invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR2') || (invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR5') || (invtype == 'CDNURA' && rettype == 'GSTR2') || (invtype == 'CDNURA' && rettype == 'GSTR5')){
								$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
							}else if((invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR1') || (invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR5') || (invtype == 'CDNURA' && rettype == 'GSTR1') || (invtype == 'CDNURA' && rettype == 'GSTR5')){
								$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
							}else if(invtype == 'Credit/Debit Notes' && rettype == 'GSTR1'){
								$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
							}else if(invtype == 'Credit/Debit Notes' && rettype == 'GSTR2'){
								$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
							}
						}else{
							$('.printerintra').css("display","none");$('.cdnurtype').removeClass("astrich");$('#cdnurtyp,#cdnurinvtyp').attr("readonly","readonly").addClass("disabled").val("");;$('#cdnurtyp').removeAttr("required");$('#cdnurinvtyp').removeAttr("required");
						}
						  rowCount = 1;
			                if (rowCount < invoice.items.length) {
			                    for (var i = 1; i < invoice.items.length; i++) {
			                        if (invoice.items[i]) {
			                            add_row('${client.id}', rettype, '${usertype}');
			                        }
			                    }
			                }
						for (var i = 1; i <= invoice.items.length; i++) {
							document.getElementById("product_text" + i).value = invoice.items[i - 1].itemno ? invoice.items[i - 1].itemno : '';
							document.getElementById("hsn_text" + i).value = invoice.items[i - 1].hsn ? invoice.items[i - 1].hsn : '';
							document.getElementById("uqc_text" + i).value = invoice.items[i - 1].uqc ? invoice.items[i - 1].uqc : '';
							document.getElementById("qty_text" + i).value = invoice.items[i - 1].quantity ? invoice.items[i - 1].quantity.toFixed(3) : '0.000';
							document.getElementById("rate_text" + i).value = invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem.toFixed(3) : '0.000';
							document.getElementById("exempted_text" + i).value = invoice.items[i - 1].exempted ? invoice.items[i - 1].exempted : '0.00';
							document.getElementById("igsttax_text" + i).value = invoice.items[i - 1].igstamount ? invoice.items[i - 1].igstamount.toFixed(2) : '0.00';
	                        document.getElementById("cgsttax_text" + i).value = invoice.items[i - 1].cgstamount ? invoice.items[i - 1].cgstamount.toFixed(2) : '0.00';
	                        document.getElementById("sgsttax_text" + i).value = invoice.items[i - 1].sgstamount ? invoice.items[i - 1].sgstamount.toFixed(2) : '0.00';
	                        document.getElementById("cessamount_text" + i).value = invoice.items[i - 1].cessamount ? invoice.items[i - 1].cessamount.toFixed(2) : '0.00';
	                        document.getElementById("taxrate_text" + i).value = invoice.items[i - 1].rate;
	                        document.getElementById("cessrate_text" + i).value = invoice.items[i - 1].cessrate;
	                        document.getElementById("cessamount_text" + i).value = invoice.items[i - 1].cessamount;
	                        document.getElementById("taxableamount_text" + i).value = invoice.items[i - 1].taxablevalue.toFixed(2);
	                        document.getElementById("total_text" + i).value = invoice.items[i - 1].total;
	                       if(invoice.items[i - 1].igstamount && invoice.items[i - 1].igstamount > 0){
	                    	   document.getElementById("abb"+i).value = invoice.items[i - 1].igstamount.toFixed(2);
	                       }else if(invoice.items[i - 1].sgstamount && invoice.items[i - 1].sgstamount > 0 && invoice.items[i - 1].cgstamount && invoice.items[i - 1].cgstamount > 0){
	                    	   var ttax = 0;
	                    	   ttax += invoice.items[i - 1].cgstamount;
	                    	   ttax += invoice.items[i - 1].sgstamount;
	                    	   document.getElementById("abb"+i).value = ttax.toFixed(2);
	                       }
						}
						document.getElementById("totTotal").innerHTML = formatNumber(parseFloat(invoice.totalamount).toFixed(2));
                        document.getElementById("totTaxable").innerHTML = formatNumber(parseFloat(invoice.totaltaxableamount).toFixed(2));
                       document.getElementById("totCESS").innerHTML = formatNumber(parseFloat(invoice.totalCessAmount).toFixed(2));
                       document.getElementById("totIGST").innerHTML = formatNumber(parseFloat(invoice.totaltax).toFixed(2));
                       $('#idTotal').html(formatNumber(parseFloat(invoice.totalamount).toFixed(2)));
                       
						var cdndate = new Date().getDate() + "/" + month + "/" + year;
						var invdt = $('#dateofinvoice').val();
						var invoicedate = day + "/" + month + "/" + year;
						var res = invoicedate.split("/");
						var mdte = res[1]+"/"+res[0]+"/"+res[2];
						 $('#dateofinvoice').datetimepicker({
	                            value: invdt,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: false,
	                            minDate: new Date(mdte),
	                        });
						 $('#originalInvDate').val(res[0]+"/"+res[1]+"/"+res[2]);
						 $('.oInvDate').show();
						 $('.oInvDateVal').text(res[0]+"-"+res[1]+"-"+res[2]);
						 $('#oldInvDateVal').val(res[0]+"-"+res[1]+"-"+res[2]);
					},
					onLoadEvent: function() {
						if($("#eac-container-voucherNumber ul").children().length == 0) {$("#voucherNumberempty").show();} else {$("#voucherNumberempty").hide();}
					},
					maxNumberOfElements: 43
				}
			};
			$(".voucherNumber").easyAutocomplete(invoiceNumberoptions);
			var atinvoiceNumberoptions = {
					url: function(phrase) {
						var date = $('#dateofinvoice').val();var mnyr = date.split("/");var rettype = $('#retType').val();var invtype = $('#idInvType').val(); invtype = invtype.replace('/', '_');var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
						phrase = phrase.replace('(',"\\(");
						phrase = phrase.replace(')',"\\)");
						return "${contextPath}/invoiceNumbers/"+mn+"/"+yr+"/"+rettype+"/"+invtype+"/?query="+ phrase + "&clientid=${client.id}&format=json";
					},
					getValue: function(element) {
						var invDate = new Date(element.dateofinvoice);var day = (invDate.getDate()) + "";var month = (invDate.getMonth() + 1) + "";	var year = invDate.getFullYear() + "";day = checkZero(day);	month = checkZero(month);year = checkZero(year);var invoiceDate = day + "-" + month + "-" + year; var invnoAndDate = element.invoiceno+"("+invoiceDate+")";
						return invnoAndDate;
					},
					list: {
						onChooseEvent: function() {var rettype = $('#retType').val();
							var invoice = $("#atvoucherNumber").getSelectedItemData();$('#atinvamt').text(invoice.totaltaxableamount);$('#billedtoname').val(invoice.billedtoname);$('#advPInvamt').val(invoice.totaltaxableamount);$('#atvoucherNumber').val('');$('#atcustname').text(invoice.billedtoname);$('#advPCustname').val(invoice.billedtoname);$('#atvoucherNumber').val(invoice.invoiceno).trigger("change");
							if($('#idInvType').val() == 'Advance Adjusted Detail' || $('#idInvType').val() == 'ADVANCE ADJUSTED DETAIL'){
								$('#dateofinvoice').datetimepicker({
		                            value: new Date(),
		                            timepicker: false,
		                            format: 'd/m/Y',
		                            maxDate: false,
		                            minDate: new Date(invoice.dateofinvoice),
		                        });
							}
							var totaligst=0,totalcgst=0,totalsgst=0;
							if(invoice.items) {
								invoice.items.forEach(function(item) {
									if(item.igstamount) {totaligst += item.igstamount}if(item.cgstamount) {totalcgst += item.cgstamount}if(item.sgstamount) {totalsgst += item.sgstamount}
								})
							}
							$('#atigstamt').text(totaligst);$('#advPIgstamt').val(totaligst);$('#atcgstamt').text(totalcgst);$('#advPCgstamt').val(totalcgst);$('#atsgstamt').text(totalsgst);$('#advPSgstamt').val(totalsgst);
							if(totalcgst > 0){
								$('#atigstamt,.atigstamt').css("display","none");
								$('#atcgstamt,#atsgstamt,.atcsgstamt').css("display","block");
							}else{
								$('#atigstamt,.atigstamt').css("display","block");
								$('#atcgstamt,#atsgstamt,.atcsgstamt').css("display","none");
							}
							updateRateType('<c:out value="${client.statename}"/>', rettype);
						},
						onLoadEvent: function() {
							if($("#eac-container-atvoucherNumber ul").children().length == 0) {$("#atvoucherNumberempty").show();} else {$("#atvoucherNumberempty").hide();}
						},
						maxNumberOfElements: 43
					}
				};
				$(".atvoucherNumber").easyAutocomplete(atinvoiceNumberoptions);
		var custoptions = {
			url: function(phrase) {
				phrase = phrase.replace('(',"\\(");
				phrase = phrase.replace(')',"\\)");
				var rettype = $('#retType').val();
				if("GSTR1" == rettype){
					return "${contextPath}/srchcustomer?query="+ phrase + "&clientid=${client.id}&format=json";
				}else if("GSTR2" == rettype){
					return "${contextPath}/srchsupplier?query="+ phrase + "&clientid=${client.id}&format=json";
				}else if("GSTR1" == "<c:out value='${varRetType}'/>" || "GSTR4" == "<c:out value='${varRetType}'/>" || "GSTR5" == "<c:out value='${varRetType}'/>" || "ANX1" == "<c:out value='${varRetType}'/>"){
					return "${contextPath}/srchcustomer?query="+ phrase + "&clientid=${client.id}&format=json";
				}else if("GSTR2" == "<c:out value='${varRetType}'/>" || "GSTR6" == "<c:out value='${varRetType}'/>"){
					return "${contextPath}/srchsupplier?query="+ phrase + "&clientid=${client.id}&format=json";
				}
			},
			getValue: "customerIdAndName",
			list: {
				onChooseEvent: function() {
					$('#billingAddress_id').text('');var custData = $("#billedtoname").getSelectedItemData();$('#billedtogstin').val(custData.gstnnumber).trigger("change");$('#billedtoname').val(custData.name);$('#billingAddress').val(custData.address+","+custData.pincode);var invtype = $('#idInvType').val();var rettype = $('#retType').val();if(custData.dealerType != "" && custData.dealerType != null){dealertype = custData.dealerType;$('#dealerType').val(custData.dealerType);};
					$('#customerMail').val(custData.email);
					if("GSTR1" == rettype){
					if(custData.isCustomerTermsDetails == true || custData.isCustomerTermsDetails == "true"){$('#bankNotes').val(custData.customerterms);$('#invnotes').html(custData.customerterms);}else{var terms_cond = $("#invnotess").text();$('#bankNotes').val(terms_cond);$('#invnotes').html(terms_cond);}
					}else if("GSTR2" == rettype){
						if(custData.isSupplierTermsDetails == true || custData.isSupplierTermsDetails == "true"){$('#bankNotes').val(custData.supplierterms);$('#invnotes').html(custData.supplierterms);}else{var terms_cond = $("#invnotess").text();$('#bankNotes').val(terms_cond);$('#invnotes').html(terms_cond);}
					}
					if(custData.city != null && custData.city !="" && custData.address != ""){$('#billingAddress').val(custData.address+ " ,"+custData.city+" ,"+custData.pincode);
					}else if(custData.city =="" && custData.address != "" && custData.address != null ){	$('#billingAddress').val(custData.address+ " ,"+custData.pincode);
					}else if(custData.city != null &&  custData.city !="" && custData.address == ""){	$('#billingAddress').val(custData.city+" ,"+custData.pincode);
					}else {$('#billingAddress').val(custData.pincode);}
					if(custData.gstnnumber == ''){
						if((invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR2') || (invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR5') || (invtype == 'CDNURA' && rettype == 'GSTR2') || (invtype == 'CDNURA' && rettype == 'GSTR5')){
							$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
						}else if((invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR1') || (invtype == 'Credit/Debit Note for Unregistered Taxpayers' && rettype == 'GSTR5') || (invtype == 'CDNURA' && rettype == 'GSTR1') || (invtype == 'CDNURA' && rettype == 'GSTR5')){
							$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
						}else if(invtype == 'Credit/Debit Notes' && rettype == 'GSTR1'){
							$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
						}else if(invtype == 'Credit/Debit Notes' && rettype == 'GSTR2'){
							$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
						}
					}else{
						$('.printerintra').css("display","none");$('.cdnurtype').removeClass("astrich");$('#cdnurtyp,#cdnurinvtyp').attr("readonly","readonly").addClass("disabled");$('#cdnurtyp').removeAttr("required");$('#cdnurinvtyp').removeAttr("required");
					}
					if("GSTR2" != rettype && "PurchaseRegister" != rettype && "Purchase Register" != rettype){
						if(custData.state != ''){$('.placeofsupply').removeClass("has-error has-danger");$('.placeofsupply .with-errors').html('');$("#billedtostatecodeempty").hide();
			     		}$('#billedtostatecode').val(custData.state);
					}
					if(rettype == 'GSTR1' || rettype == 'SalesRegister'){$('#invoiceCustomerId').val(custData.customerId);$('#vendorName').val(custData.customerLedgerName);
					}else if(rettype == 'GSTR2' || rettype == 'PurchaseRegister' || rettype == 'Purchase Register'){$('#invoiceCustomerId').val(custData.supplierCustomerId);$('#companyDBId').val(custData.supplierDBId);$('#vendorName').val(custData.supplierLedgerName);}
					if((rettype == 'GSTR1' && (custData.customerId != "" && custData.customerId != null)) || (rettype == 'SalesRegister' && (custData.customerId != "" && custData.customerId != null))){$('#billingAddress_id').text('Customer Code : '+custData.customerId);
					}else if((rettype == 'GSTR2' && (custData.supplierCustomerId != "" && custData.supplierCustomerId != null)) || (rettype == 'PurchaseRegister' && (custData.supplierCustomerId != "" && custData.supplierCustomerId != null)) || (rettype == 'Purchase Register' && (custData.supplierCustomerId != "" && custData.supplierCustomerId != null))){$('#billingAddress_id').text('Supplier Code : '+custData.supplierCustomerId);}
					if("GSTR1" == rettype || "SalesRegister" == rettype || "GSTR4" == "<c:out value='${varRetType}'/>" || "GSTR5" == "<c:out value='${varRetType}'/>" || rettype == 'GSTR2' || rettype == 'PurchaseRegister' || rettype == 'Purchase Register'){
						if(invtype != 'Advance Adjusted Detail'){
							if(rettype == 'GSTR2' || rettype == 'PurchaseRegister' || rettype == 'Purchase Register'){
								if(custData.accountNumber != ''){
									$('.addBankDetails').attr("checked","true").prop('checked', true);
									if($('.addBankDetails').is(":checked")){$("#selectBankDiv").hide();$("#selectBank").removeAttr("disabled");$(".bank-details-box").show();
									}else{$(".bank-details-box").hide();$("#selectBankDiv").hide();$('#bankAcctNo').parent().removeClass('has-error');
									}$('#selectBank').val(custData.accountNumber);$('#bankName').val(custData.bankName);$('#bankAcctNo').val(custData.accountNumber);$('#bankBranch').val(custData.branchAddress);$('#bankIFSC').val(custData.ifscCode);$('#bankAccountName').val(custData.beneficiaryName);
								}else{$('.bacno').removeClass('has-error has-danger');$('.bacno .with-errors').html('');$('.bifscno').removeClass('has-error has-danger');$('.bifscno .with-errors').html('');}
							}else{
								if(custData.customerAccountNumber != ''){
									$('.addBankDetails').attr("checked","true").prop('checked', true);
									if($('.addBankDetails').is(":checked")){$("#selectBankDiv").show();$("#selectBank").removeAttr("disabled");$(".bank-details-box").show();
									}else{$(".bank-details-box").hide();$("#selectBankDiv").hide();$('#bankAcctNo').parent().removeClass('has-error');
									}$('#selectBank').val(custData.customerAccountNumber);$('#bankName').val(custData.customerBankName);$('#bankAcctNo').val(custData.customerAccountNumber);$('#bankBranch').val(custData.customerBranchName);$('#bankIFSC').val(custData.customerBankIfscCode);$('#bankAccountName').val(custData.customerAccountName);
								}else{$('.bacno').removeClass('has-error has-danger');$('.bacno .with-errors').html('');$('.bifscno').removeClass('has-error has-danger');$('.bifscno .with-errors').html('');}
							}
						}
					}
					updateRateType('<c:out value="${client.statename}"/>', rettype);
				},
				onLoadEvent: function() {
					if($("#eac-container-billedtoname ul").children().length == 0) {$("#addbilledtoname").show();} else {$("#addbilledtoname").hide();}
				},
				maxNumberOfElements: 30
			}
		};
		$("#billedtoname").easyAutocomplete(custoptions);	
		$(".billedtoname").parent().parent().mouseleave(function() {$("#addbilledtoname").hide();});
		/*$("select#exportType").change(function(){
			 var selectedExportType = $("#exportType option:selected").val();
			if(selectedExportType == 'WPAY') {interStateFlag=false;$('#tax_rate').addClass("astrich");$('#tax_rate').attr("required","required");$('#rate_as').addClass("astrich");$(".taxrate_textDisable").prop('disabled',false);
			} else if(selectedExportType == 'WOPAY'){interStateFlag=true;$('#tax_rate').removeClass("astrich");$('#rate_as').removeClass("astrich");$(".taxrate_textDisable").prop('disabled',true);
			}
		});*/
		$("select.itcreversaltype").change(function(){
			 var itcReversalType = $(".itcreversaltype option:selected").val();$('.itc_reversal').hide();$('#'+itcReversalType+'Parent').show();
		});
		$("select.isSez").change(function(){
			 var isSez = $(".isSez option:selected").val();
			if(isSez == 'Y'){$('#impGoodsgstin').css('display','block');$('#stin').attr("required","required");
			}else{$('#stin').removeAttr("required");$('#impGoodsgstin').css('display','none');
			}
		});
		$("#billedtostatecode").on('paste', function(e) {
			var pastedData = e.originalEvent.clipboardData.getData('text');
			var rettype = $('#retType').val();pupdateRateType('<c:out value="${client.statename}"/>',rettype,pastedData);
		});
	});
	function showCancelPopup(invId, retType, invNo,booksOrReturns) {
		var inyType = $('#idInvType').val();var irn = $('#irnnumber').val();
		$('#invoiceModal').css("z-index","1033");
		$('#cancelModal').modal('show');
		if(irn != ""){
			  $('#irncancel_message').text("(If you cancel the Invoice IRN Number also cancelled)");
			}else{
				$('#irncancel_message').text("");
			}
		$('#cancelPopupDetails').html(invNo);
		$('#btnCancel').attr('onclick', "cancelInvoice('"+invId+"','"+retType+"','"+booksOrReturns+"')");
	}
	function cancelInvoice(invId, retType,booksOrReturns) {
		var irn = $('#irnnumber').val();
		irnCanArray.push(invId);
		if(irn != ""){
			var cancelIRNdata=new Object();
			cancelIRNdata.CnlRsn="1";
			cancelIRNdata.CnlRem="Wrong Entry";
				$.ajax({
					url: contextPath+"/cancelIRN/"+clientId+"/"+retType+"?ids="+irnCanArray,
					type: "POST",
					data: JSON.stringify(cancelIRNdata),
					contentType: 'application/json',
					success : function(response) {
						if(response != ""){
							if(response == "Invalid Token" || response == "InActive"){
								$('#irncancel_error').html(response+'<a href="#" onclick="configEinvauthentication()" style="color:green;"> <span id="inactive_btn">Authenticate Now</span></a>');
							}else{
								$('#irncancel_error').text(response);
							}
						}else{
							window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${varRetType}"/>/'+month+'/'+year+'?type='+invoiceStatus;
						}
						
					}
				});
		}else{
			$.ajax({
				url: '${contextPath}/cancelinv/<c:out value="${id}"/>/<c:out value="${client.id}"/>/'+invId+'/'+retType+'?booksOrReturns='+booksOrReturns,
				success : function(response) {
					window.location.href = '${contextPath}/alliview/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>/<c:out value="${varRetType}"/>/'+month+'/'+year+'?type='+invoiceStatus;
				}
			});
		}
	}
	function editAsAmendment(invId, retType) {
		$('#strAmendment').val("true");$('.form-control').removeClass('disable');$('#addrow,#impSeraddrow').removeAttr('disabled').css("background-color","");$('.btn_popup_amnd').hide();$('.btn_popup_save').show();$('.gstr2adeletefield').css("display","block");var i=1;
		$.each($("#allinvoice tr"),function() { 
		var id = $(this).attr('id');
		$(this).attr("onclick","edit_row("+id+",\""+invId+"\",\""+retType+"\")");i++;
		});
	}
	function bankAndVertical(){
		var notess =  $("#invnotes").text();var terms_cond = $("#invterms").text();var branchname;var verticalname;
		$('#branch,#vertical,#snilbranch,#snilvertical').children('option').remove();
			$("#branch,#snilbranch").append($("<option></option>").attr("value","").text("-- Select Branch --")); 
			$("#vertical,#snilvertical").append($("<option></option>").attr("value","").text("-- Select Vertical --"));
			<c:choose>
				<c:when test="${not empty companyUser}">
					<c:if test='${not empty companyUser.branch}'>
						<c:forEach items = "${companyUser.branch}" var="branch">
							<c:forEach items="${client.branches}" var="branchs">
								<c:if test='${branch eq branchs.name}'>
									$("#branch,#snilbranch").append($("<option></option>").attr("value","${branch}").text("${branch}"));
									branchname = "${branch}";
								</c:if>
							</c:forEach>		
						</c:forEach>
					</c:if>
					<c:if test='${not empty companyUser.vertical}'>
						<c:forEach items = "${companyUser.vertical}" var="vertical">
							<c:forEach items="${client.verticals}" var="verticals">
								<c:if test='${vertical eq verticals.name}'>
									$("#vertical,#snilvertical").append($("<option></option>").attr("value","${vertical}").text("${vertical}"));
									verticalname = "${vertical}";
								</c:if>
							</c:forEach>	
						</c:forEach>
					</c:if>
				</c:when>
				<c:otherwise>
					<c:forEach items="${client.branches}" var="branch">
						$("#branch,#snilbranch").append($("<option></option>").attr("value","${branch.name}").text("${branch.name}"));
					</c:forEach>
					<c:forEach items="${client.verticals}" var="vertical">
						$("#vertical,#snilvertical").append($("<option></option>").attr("value","${vertical.name}").text("${vertical.name}"));
					</c:forEach>
				</c:otherwise>
			</c:choose>
			var length = $('#branch').children('option').length;var user = "<c:out value='${companyUser}'/>";
			var snilbranchlength = $('#snilbranch').children('option').length;
			if(length == 1){$('#branch').attr('disabled','disabled');$('#branch').attr('title','Please configure at least one Branch to see here');
			}else if(length == 2){if(user == '' || user == null){$('#branch').attr('readonly',false);}else{$('#branch').attr('readonly',true);$('#branch').val(branchname);$('#branch').css("pointer-events","none");}}
			else{$('#branch').removeAttr('disabled');$('#branch').removeAttr('title');if(user == '' || user == null){$('#branch').removeAttr('required');}else{$('#branch').attr('required','true');}}
			if(snilbranchlength == 1){$('#snilbranch').attr('disabled','disabled');$('#snilbranch').attr('title','Please configure at least one Branch to see here');}else{$('#snilbranch').removeAttr('disabled');$('#snilbranch').removeAttr('title');}
			var verticalLength = $('#vertical').children('option').length;var snilverticalLength = $('#snilvertical').children('option').length;
			if(verticalLength == 1){$('#vertical').attr('disabled','disabled');$('#vertical').attr('title','Please configure at least one Vertical to see here');
			}else if(verticalLength == 2){if(user == '' || user == null){$('#vertical').attr('readonly',false);}else{$('#vertical').val(verticalname);$('#vertical').attr('readonly',true);$('#vertical').css("pointer-events","none");}}
			else{$('#vertical').removeAttr('disabled');$('#vertical').removeAttr('title');if(user == '' || user == null){$('#vertical').removeAttr('required');}else{$('#vertical').attr('required','true');}}
			if(snilverticalLength == 1){$('#snilvertical').attr('disabled','disabled');$('#snilvertical').attr('title','Please configure at least one Vertical to see here');}else{$('#snilvertical').removeAttr('disabled');$('#snilvertical').removeAttr('title');}
	}
	
	function getInvData(invId, returnType, popudateData){
		if(returnType == "Unclaimed"){
			returnType = "Purchase Register";
		}
		var urlStr = _getContextPath()+'/getinv/'+invId+'/'+returnType;
		$.ajax({
			url: urlStr,
			async: true,
			cache: false,
			dataType:"json",
			contentType: 'application/json',
			success : function(response) {
				popudateData(response);
			}
		});
	}
	
	function editInvPopup(type, returnType, invId, journalsOrNotinjournals) {
	    if (journalsOrNotinjournals == 'journal' || journalsOrNotinjournals == 'ledgerreport') {
	        $('#retType').val(returnType);
	    }
		    var gstStatus = '';
		    var revChargeNo = '';
		    bankAndVertical();
		    $('#invview_Process').removeClass('d-none');
		    if(returnType == "Unclaimed"){
				returnType = "Purchase Register";
			}
		    if(returnType == 'EWAYBILL'){
		    	ewyaddr();
		    }
		    getInvData(invId, returnType, function(invoice) {
		        if (invoice.invoiceId == invId) {
		        	type = invoice.invtype;
		        	if(returnType == 'ANX1'){
		        		invoice = updateAnxInvoiceDetails(invoice);
		        	}else{
		        		invoice = updateInvoiceDetails(invoice);	
		        	}
		        	if(userMail == 'true'|| userMail == true){
			        	if(invoice.premium != null && invoice.premium != ""){
			        		$('#premium').val(invoice.premium);$('#premium_div').show();
			        	}else{$('#premium_div').hide();}
			        	if(invoice.period != null && invoice.period != ""){
			        		$('#period').val(invoice.period);$('#period_div').show();
			        	}else{$('#period_div').hide();}
		        	}
		        	if(invoice.termDays != null && invoice.termDays != ""){
		        		$('#terms_drop_div').val(invoice.termDays);
		        		$('#termDays').val(invoice.termDays);
		        		$('.adddropdown').find('span').text(invoice.termDays);
		        	}
		        	if(invoice.advTaxableType == true){
		        		$('#adv_taxable_type').attr("checked",true);
		        		$('#adv_taxable_type').val("true");
		        	}else{
		        		$('#adv_taxable_type').attr("checked",false);
		        		$('#adv_taxable_type').val("false");
		        	}
		        	if(invoice.voucherNumber != "" && invoice.voucherNumber != null){
		        		 $('.oInvDate').show();
						 $('.oInvDateVal').text(invoice.voucherDate);
						 $('#oldInvDateVal').text(invoice.voucherDate);
		        	}
		        	if(invoice.dueDate != null){
		        		$('#duedate_div').val(invoice.dueDate);
		        	}
		        	if(invoice.irnNo != null){
		        		$('#irnnumber').val(invoice.irnNo);
		        	}
		        	if(invoice.srctype != null){
						$('#srctype').val(invoice.srctype);
					}
		        	if(invoice.customerEmail != null){
		        		$('#customerMail').val(invoice.customerEmail);
		        	}
		        	if(invoice.validUpto != null){
		        		$('#validUptoDate').val(invoice.validUpto);
		        	}
		        	if(invoice.ewaybillRejectDate != null){
		        		$('#ewaybillRejectDate').val(invoice.ewaybillRejectDate);
		        	}
		            if (invoice.revchargetype == 'Regular' || invoice.revchargetype == 'N'  || invoice.revchargetype == '') {
		                $('#revchargetype').val('Regular');
		                $('#revChargeNoDiv').css("display","none");
		                $('.printerintra').css("display","none");
		            } else {
		                $('#revchargetype').val('Reverse');
		                if(returnType == 'GSTR2' || returnType == 'Purchase Register'){
			                if(invoice.revchargeNo != ""){
			                	revChargeNo = invoice.revchargeNo;
			                	$('#revChargeNoDiv').css("display","inline-block");
			                	$('#revchargeNo').val(invoice.revchargeNo);
			                }else{
			                	$('#revChargeNoDiv').css("display","none");
			                }
		                }
		                if(returnType == 'GSTR2' || returnType == 'Purchase Register'){
		                	if(type == 'B2B Unregistered' || type == 'Credit/Debit Note for Unregistered Taxpayers'){
			                	$('.printerintra').css("display","block");
			                	if(invoice.printerintra != null){
			                		$('.printerintra').val(invoice.printerintra);
			                	}else{
			                		$('.printerintra').val('');
			                	}
		                	}
		                }else{
		                	$('.printerintra').css("display","none");
		                }
		            }
		            if(invoice.buyerDtls != null){
		            var bgstin = "";
					if((invoice.buyerDtls.gstin != null) && (invoice.buyerDtls.gstin != '')){
						bgstin = invoice.buyerDtls.gstin+",";
					}
					var blgNm = "";
					if((invoice.buyerDtls.lglNm != null) && (invoice.buyerDtls.lglNm != '')){
						blgNm = invoice.buyerDtls.lglNm+",";
					}
					var bloc = "";
					if((invoice.buyerDtls.loc != null) && (invoice.buyerDtls.loc != '')){
						bloc = invoice.buyerDtls.loc+",";
					}
					var bstate = "";
					if((invoice.buyerDtls.state != null) && (invoice.buyerDtls.state != '')){
						bstate = invoice.buyerDtls.state+",";
					}
					var bpin = "";
					if((invoice.buyerDtls.pin != null) && (invoice.buyerDtls.pin != '')){
						bpin = invoice.buyerDtls.pin;
					}
					$('#ewybuyerDtls_gstin').val(invoice.buyerDtls.gstin);
					$('#ewybuyerDtls_lglNm').val(invoice.buyerDtls.lglNm);
					$('#ewybuyerDtls_addr1').val(invoice.buyerDtls.addr1);
					$('#ewybuyerDtls_addr2').val(invoice.buyerDtls.addr2);
					$('#ewybuyerDtls_loc').val(invoice.buyerDtls.loc);
					$('#ewybuyerDtls_pin').val(invoice.buyerDtls.pin);
					$('#ewybuyerDtls_state').val(invoice.buyerDtls.state);
					$('#ewybuyerDtls_pos').val(invoice.buyerDtls.pos);
					$('.ewybuyerAddrText').html('Edit');
					var addr = "";
					if((invoice.buyerDtls.addr1 != null) && (invoice.buyerDtls.addr1 != '') ){					
					addr = invoice.buyerDtls.addr1.substring(1,15)+"...";
					}
					var gstlgnm = "";
					if(bgstin != ""){
						gstlgnm = gstlgnm+""+bgstin;
					}
					if(blgNm != ""){
						gstlgnm = gstlgnm+""+blgNm;
					}
					if(gstlgnm != ""){
						addr = gstlgnm+""+addr;
					}
					if(bloc != ""){
						addr = addr+""+bloc;
					}
					if(bstate != ""){
						addr = addr+""+bstate;
					}
					if(bpin != ""){
						addr = addr+""+bpin;
					}
					$('#ewaybuyerAddr').html(addr);
		            }
					if((invoice.dispatcherDtls != null) && (invoice.dispatcherDtls != '')){	
						$('#ewydispatcherDtls_nm').val(invoice.dispatcherDtls.nm);
						$('#ewydispatcherDtls_addr1').val(invoice.dispatcherDtls.addr1);
						$('#ewydispatcherDtls_addr2').val(invoice.dispatcherDtls.addr2);
						$('#ewydispatcherDtls_loc').val(invoice.dispatcherDtls.loc);
						$('#ewydispatcherDtls_pin').val(invoice.dispatcherDtls.pin);	
						$('#ewydispatcherDtls_stcd').val(invoice.dispatcherDtls.stcd);
						var disaddr = "";
					if((invoice.dispatcherDtls.addr1 != null) && (invoice.dispatcherDtls.addr1 != '')){	
						disaddr = invoice.dispatcherDtls.addr1.substring(1,15)+"...";
					}
					var dlgNm = "";
					if((invoice.dispatcherDtls.nm != null) && (invoice.dispatcherDtls.nm != '')){	 	
						dlgNm = invoice.dispatcherDtls.nm+",";
					}
					var dloc = "";
					if((invoice.dispatcherDtls.loc != null) && (invoice.dispatcherDtls.loc != '')){
						dloc = invoice.dispatcherDtls.loc+",";
					}
					var dstate = "";
					if((invoice.dispatcherDtls.stcd != null) && (invoice.dispatcherDtls.stcd != '')){
						dstate = invoice.dispatcherDtls.stcd+",";
					}
					var dpin = "";
					if((invoice.dispatcherDtls.pin != null) && (invoice.dispatcherDtls.pin != '')){
						dpin = invoice.dispatcherDtls.pin;
					}
					var gstlgnm = "";
					if(dlgNm != ""){
						gstlgnm = gstlgnm+""+dlgNm;
					}
					if(gstlgnm != ""){
						disaddr = gstlgnm+""+disaddr;
					}
					if(dloc != ""){
						disaddr = disaddr+""+dloc;
					}
					if(dstate != ""){
						disaddr = disaddr+""+dstate;
					}
					if(dpin != ""){
						disaddr = disaddr+""+dpin;
					}
						$('#ewaydispatchAddr').html(disaddr);
					}
					if(invoice.shipmentDtls != null){
						$('#ewyshipmentDtls_gstin').val(invoice.shipmentDtls.gstin);
						$('#ewyshipmentDtls_lglNm').val(invoice.shipmentDtls.lglNm);
						$('#ewyshipmentDtls_trdNm').val(invoice.shipmentDtls.trdNm);
						$('#ewyshipmentDtls_addr1').val(invoice.shipmentDtls.addr1);
						$('#ewyshipmentDtls_addr2').val(invoice.shipmentDtls.addr2);
						$('#ewyshipmentDtls_loc').val(invoice.shipmentDtls.loc);
						$('#ewyshipmentDtls_pin').val(invoice.shipmentDtls.pin);	
						$('#ewyshipmentDtls_stcd').val(invoice.shipmentDtls.stcd);
						
							var shipaddr = "";
					if((invoice.shipmentDtls.addr1 != null) && (invoice.shipmentDtls.addr1 != '')){	
						shipaddr = invoice.shipmentDtls.addr1.substring(1,15)+"...";
					}
					var sgstin = "";
					if((invoice.shipmentDtls.gstin != null) && (invoice.shipmentDtls.gstin != '')){
						sgstin = invoice.shipmentDtls.gstin+",";
					}
					var slgNm = "";
					if((invoice.shipmentDtls.trdNm != null) && (invoice.shipmentDtls.trdNm != '')){
						slgNm = invoice.shipmentDtls.trdNm+",";
					}
					var sloc = "";
					if((invoice.shipmentDtls.loc != null) && (invoice.shipmentDtls.loc != '')){
						sloc = invoice.shipmentDtls.loc+",";
					}
					var sstate = "";
					if((invoice.shipmentDtls.stcd != null) && (invoice.shipmentDtls.stcd != '')){
						sstate = invoice.shipmentDtls.stcd+",";
					}
					var spin = "";
					if((invoice.shipmentDtls.pin != null) && (invoice.shipmentDtls.pin != '')){
						spin = invoice.shipmentDtls.pin;
					}
					var gstlgnm = "";
					if(sgstin != ""){
						gstlgnm = gstlgnm+""+sgstin;
					}
					if(slgNm != ""){
						gstlgnm = gstlgnm+""+slgNm;
					}
					if(gstlgnm != ""){
						shipaddr = gstlgnm+""+shipaddr;
					}
					if(sloc != ""){
						shipaddr = shipaddr+""+sloc;
					}
					if(sstate != ""){
						shipaddr = shipaddr+""+sstate;
					}
					if(spin != ""){
						shipaddr = shipaddr+""+spin;
					}	
						$('#ewayshipmentAddr').html(shipaddr);
					}
		            $('#exchange_Rate').val(invoice.exchangeRate);
					$('#add_currencyCode').val(invoice.addcurrencyCode);
		            $('#originalinvamt').val(parseFloat(invoice.originalinvamount));
		            $('#cdn_taxableamount').val(invoice.totaltaxableamount);
		            $('#paymentStatusinv').val(invoice.paymentStatus);
		             if(invoice.pendingAmount != null){
		            	$('#InvPendingAmount').val(parseFloat(invoice.pendingAmount).toFixed(2));	
		            }
					if(returnType == "PROFORMAINVOICES"){
						if(invoice.convertedtoinv != null){
							$('#convertedtoinv').val(invoice.convertedtoinv);	
						}
					}
		            if(invoice.receivedAmount != null){
		            	$('#InvReceivedAmount').val(parseFloat(invoice.receivedAmount).toFixed(2));
		            }
		            $('#dealerType').val(invoice.dealerType);
		            dealertype = invoice.dealerType;
		            var form = $('#salesinvoceform');
		            $('#salesinvoceform').find("input[type=text], textarea").val("");
		            $('#idInvType').val(invoice.invoicetype);
		            $('#idInvType1').val(invoice.invoicetype);
		            $('#idInvType2').val(invoice.invoicetype);
		            if (returnType == 'EWAYBILL') {
		                addAdvFlag = false;
		                ewayBillStatus = invoice.status;
		                ewayBillNo = invoice.ewayBillNumber;
		                $('#ewayBillDate').val(invoice.eBillDate);
		                $('.ebillnumber').val(invoice.ewayBillNumber);
		                $('#subSupplyType').val(invoice.subSupplyType);
		                $('.vehicleTable').css('display', 'inline-table');
		                $('#transdistancediv,#toacttostatecodediv,#fromactfromstatecodediv,#vehicletxt,#fromGstindiv,#fromTrdNamediv,#fromAddr1div,#fromAddr2div,#fromPlacediv,#fromPincodediv,#fromStateCodediv,#toGstindiv,#toTrdNamediv,#toAddr1div,#toAddr2div,#toPlacediv,#toPincodediv,#toStateCodediv,.ewaybillDateDiv,#fromPlacediv,#fromStatediv,#usetGSTINdiv,#transdiv,#transdocdiv,#transDocDatediv,#groupNodiv,#statusdiv,#districtdiv,#validDaysdiv,#validDatediv,#extendeddiv,#rejectStatusdiv,#vehicleTypediv,#transactionTypediv,#othervaluediv,#cessnonadvdiv,#vehiclediv,#tripshtNodiv,#transporterIddiv,#transporterNamediv,#supplyTypediv,#subsupplyTypediv,.ewaybilladdress').css("display", "block");
		                $('.notewaybilladdress').css("display","none");
	                    $('#transDistance').val(invoice.transDistance);
	                    $('#documentTypediv').css("display", "list-item");
		            } else {
		                $('#transdistancediv,#toacttostatecodediv,#fromactfromstatecodediv,#vehicletxt,#fromGstindiv,#fromTrdNamediv,#fromAddr1div,#fromAddr2div,#fromPlacediv,#fromPincodediv,#fromStateCodediv,#toGstindiv,#toTrdNamediv,#toAddr1div,#toAddr2div,#toPlacediv,#toPincodediv,#toStateCodediv,.vehicleTable ,.ewaybillDateDiv,#fromPlacediv,#fromStatediv,#usetGSTINdiv,#transdiv,#transdocdiv,#transDocDatediv,#groupNodiv,#documentTypediv,#statusdiv,#districtdiv,#validDaysdiv,#validDatediv,#extendeddiv,#rejectStatusdiv,#vehicleTypediv,#transactionTypediv,#othervaluediv,#cessnonadvdiv,#vehiclediv,#tripshtNodiv,#transporterIddiv,#transporterNamediv,#supplyTypediv,#subsupplyTypediv,.ewaybilladdress').css("display", "none");
		                $('.notewaybilladdress').css("display","block");
		                $('#sameasBillingAddrDiv').css("display","contents");
		                $("input:hidden[class='ewayaddrfields']").remove();
		            }
		            if (invoice.invoiceCustomerId == "" || invoice.invoiceCustomerId == null) {
		                $('#invoiceCustomerId').val('');
		            } else {
		                $('#invoiceCustomerId').val(invoice.invoiceCustomerId);
		                if (returnType == 'GSTR1') {
		                    $('#billingAddress_id').text('Customer Code:' + invoice.invoiceCustomerId);
		                } else if (returnType == 'Purchase Register' || returnType == 'GSTR2' || returnType == 'Unclaimed') {
		                    $('#billingAddress_id').text('Supplier Code:' + invoice.invoiceCustomerId);
		                }
		            }
		            if (invoice.companyDBId == "" || invoice.companyDBId == null) {
		                $('#companyDBId').val('');
		            } else {
		                $('#companyDBId').val(invoice.companyDBId);
		            }
		            gstStatus = invoice.gstStatus;
		            if (returnType != 'EWAYBILL') {
		                if (invoice.gstStatus == '<%=MasterGSTConstants.GST_STATUS_CANCEL%>') {
		                    returnType = 'GSTR2A';
		                }
		            }
		            if (invoice.roundOffAmount == "" || invoice.roundOffAmount == null) {
		                $('#roundOffTotalValue').val('');
		            } else {
		                $('#roundOffTotalChckbox').attr("checked", "true");
		                $('#roundOffTotalValue').val((invoice.roundOffAmount).toFixed(2));
		            }
		            if (invoice.notroundoftotalamount == "" || invoice.notroundoftotalamount == null) {
		                $('#hiddenroundOffTotalValue').val(invoice.totalamount);
		            } else {
		                $('#hiddenroundOffTotalValue').val(parseFloat(invoice.notroundoftotalamount).toFixed(2));
		            }
		            if (invoice.includetax == "" || invoice.includetax == null || invoice.includetax == 'No') {
		                $('#includetax').prop("checked", false);
		            } else {
		                $('#includetax').prop("checked", true);
		                $('#includetax').val('Yes');
		            }
		            if (invoice.invoiceLevelCess == 'No') {
		                $('#invoiceLevelCess').prop("checked", false);
		                $('.cessFlag').hide();
		                $('#invLevelCess').val('No');
		            } else {
		                $('#invoiceLevelCess').prop("checked", true);
		                $('#invoiceLevelCess').val('Yes');
		                $('.cessFlag').show();
		                $('#invLevelCess').val('Yes');
		            }
		            if (type == 'Credit/Debit Notes') {
		                var doctype;
		                $('.disFlag').hide();
		                if (returnType == 'GSTR1') {
		                    doctype = invoice.cdnr[0].nt[0].ntty;
		                } else if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
		                    doctype = invoice.cdn[0].nt[0].ntty;
		                }
		                if (doctype == 'C') {
		                    $('.invoiceNumberText').html('Credit Note.No');
		                    $('.invoiceDateText').html('Credit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                } else if (doctype == 'D') {
		                    $('.invoiceNumberText').html('Debit Note.No');
		                    $('.invoiceDateText').html('Debit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                } else if (doctype == 'R') {
		                    $('.invoiceNumberText').html('Refund Voucher No');
		                    $('.invoiceDateText').html('Ref.Voucher Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                }
		                $('.printerintra').css("display","none");$('.cdnurtype').removeClass("astrich");$('#cdnurtyp,#cdnurinvtyp').attr("readonly","readonly").addClass("disabled");$('#cdnurtyp').removeAttr("required");$('#cdnurinvtyp').removeAttr("required");
		                /*$('#pcdnur').css("display", "none");
		                $('#scdnur').css("display", "none");
		                $('#cdnurtyp').removeAttr("required");
		                $('#cdnurinvtyp').removeAttr("required");*/
		            }
		            if (type == 'Credit/Debit Note for Unregistered Taxpayers') {
		                var doctype = invoice.cdnur[0].ntty;
		                $('.disFlag').hide();
		                if (doctype == 'C') {
		                    $('.invoiceNumberText').html('Credit Note.No');
		                    $('.invoiceDateText').html('Credit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                } else if (doctype == 'D') {
		                    $('.invoiceNumberText').html('Debit Note.No');
		                    $('.invoiceDateText').html('Debit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                } else if (doctype == 'R') {
		                    $('.invoiceNumberText').html('Refund Voucher No');
		                    $('.invoiceDateText').html('Ref.Voucher Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                }
		                if(returnType == "GSTR1" || returnType == "SalesRegister"){
		                	$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
		                }else{
		                	$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
		                }
		            }
		            if (type == '(Nil-Rated/ Exempted / NON-GST)' || type == 'Nil Supplies') {
		                if (!SnilFlag) {
		                    document.getElementById("rate_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
		                    document.getElementById("qty_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
		                    document.getElementById("exempted_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
		                    if ($('td').hasClass('SnilFlag')) {
		                        $('.SnilFlag').children().removeAttr("required");
		                        $('#taxrate_text1').removeAttr("required");
		                    }
		                }
		            }
		            if (type == 'Advances') {
		                if ($('td').hasClass('AdvancesFlag')) {
		                    $('#uqc_text1').removeAttr("required");
		                    $('#qty_text1').removeAttr("required");
		                }
		                $('#advrcno_text1').removeAttr("required");
		                $('#advrcposs_text1').removeAttr("required");
		            }
		            if (type != 'ADVANCE ADJUSTED DETAIL' || type != 'TXPA') {
		                $('.advrcposval').attr("required", false);
		                $('.advrcnoval').attr("required", false);
		            }
		            if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type != 'TXPA') {
		                $('#uqc_text1').attr("required", false);
		                $('#qty_text1').attr("required", false);
		                $('#hsn_text1').attr("required", false);
		            }
		            if (!addITCRuleFlag) {
		                if ($('td').hasClass('addITCRuleFlag')) {
		                    $('#itcrule_text1').removeAttr("required");
		                }
		            }
		            if (!addAdvFlag) {
		                if ($('td').hasClass('addAdvFlag')) {
		                    $('.addAdvFlag').children().removeAttr("required");
		                }
		            }
		            if (!addITCFlag) {
		                if ($('td').hasClass('addITCFlag')) {
		                    $('.addITCFlag').children().removeAttr("required");
		                }
		            }
		            if (type == 'Credit/Debit Notes') {
		                $('#uqc_text1').removeAttr("required");
		                $('.disFlag').hide();
		            }
		            if (type == 'ADD EXPORT INVOICE') {
		                $('#uqc_text1').attr("required", true);
		                $('#ad_tax2').addClass('astrich');
		            }
		            if ((returnType == 'GSTR1' || returnType == 'ANX1') && (type != 'ADVANCE ADJUSTED DETAIL' && type != 'TXPA' && type != '(Receipt voucher)' && type != 'Advances')) {
		                if (type == '(Nil-Rated/ Exempted / NON-GST)') {
		                    $('.exemp_td').show();
		                    $('.nil-foot').attr('colspan', '6');
		                } else if (type == 'Credit/Debit Notes') {
		                    $('.exemp_td').show();
		                    $('.nil-foot').attr('colspan', '7');
		                } else {
		                    $('.exemp_td').show();
		                    $('.nil-foot').attr('colspan', '8');
		                }
		            } else {
		                $('.exemp_td').hide();
		            }
		            $.each(invoice, function(key, value) {
		            	if(key != "revchargetype"){
			                form.find("input[id='" + key + "']").val(value);
			                form.find("select[id='" + key + "']").val(value);
			                if (key == 'clientAddress') {
			                    $('#clientAddress').val(value);
			                }
			                if (key == 'transDistance') {
				                form.find("input[id='transDistance']").val(value);
				             }
			                if (key == 'section') {
			                    $('#tdstcssection').val(value);
			                }
			                if (key == 'tcstdspercentage') {
			                    $('#tcs_percent').val(value);
			                }
			                if (key == 'tdsSection') {
			                    $('#tdssection').val(value);
			                }
			                if (key == 'tdspercentage') {
			                    $('#tds_percent').val(value);
			                }
			                if (key == 'gstStatus') {
			                    if (value == 'SUCCESS' || value == 'Filed' || value == 'Submitted' || value == '' || value == null) {
			                        value = '';
			                        $('.popup_error').css("display", "none");
			                    }else{
			                    	$('#gstStatus').removeAttr("style");
									$('.popup_error').css("display", "block");
								}
			                    form.find("span[id='gstStatus']").html(value);
			                }
			                if (key == 'invoiceEcomGSTIN') {
			                    $('#ecomoperatorname').text(value);
			                    $('#invoiceEcomOperator').val(value);
			                }
			                if (key == 'diffPercent') {
			                    if (value == 'Yes') {
			                        $('#diffPercent').attr("checked", "true");
			                    } else {
			                        $('#diffPercent').removeAttr("checked");
			                    }
			                }
			                if (key == 'invTyp') {
			                    $('#invTyp').val(value);
			                    if (value == 'SEWOP') {
			                        $('#lutDiv').css("display", "block");
			                        $('#lutNo').val(invoice.lutNo);
			                    } else {
			                        $('#lutDiv').css("display", "none");
			                        $('#lutNo').val('');
			                    }
			                }
			                if (key == 'advOriginalInvoiceNumber') {
			                    $('#atvoucherNumber').val(invoice.advOriginalInvoiceNumber);
			                }
			                if (key == 'advPCustname') {
			                    $('#atcustname').text(invoice.advPCustname);
			                }
			                if (key == 'advPInvamt') {
			                    $('#atinvamt').text(invoice.advPInvamt);
			                }
			                if (key == 'advPIgstamt') {
			                    $('#atigstamt').text(invoice.advPIgstamt);
								if(invoice.advPIgstamt > 0){
									$('#atigstamt,.atigstamt').css("display","block");
									$('#atcgstamt,#atsgstamt,.atcsgstamt').css("display","none");
								}
			                }
			                if (key == 'advPCgstamt') {
			                    $('#atcgstamt').text(invoice.advPCgstamt);
			                }
			                if (key == 'advPSgstamt') {
			                    $('#atsgstamt').text(invoice.advPSgstamt);
								if(invoice.advPSgstamt > 0){
									$('#atigstamt,.atigstamt').css("display","none");
									$('#atcgstamt,#atsgstamt,.atcsgstamt').css("display","block");
								}
			                }
			                if (key == 'vendorName') {
			                    $('#vendorName').val(invoice.vendorName);
			                }
			                if (type != 'ISD') {
			                    if (key == 'totalamount') {
			                        form.find("span[id='idTotal']").html(formatNumber(parseFloat(value).toFixed(2)));
			                        document.getElementById("totTotal").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                    if (key == 'totaltaxableamount') {
			                        document.getElementById("totTaxable").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                    if (key == 'igstamount1') {
			                        document.getElementById("totIGST").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    } 
			                }
			                if (key == 'cessamount1') {
			                    document.getElementById("totCESS").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                }
			                if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'GSTR2' || returnType == 'GSTR6') {
			                    if (key == 'igstavltax1') {
			                        document.getElementById("totITCIGST").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                    if (key == 'cgstavltax1') {
			                        document.getElementById("totITCCGST").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                    if (key == 'sgstavltax1') {
			                        document.getElementById("totITCSGST").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                    if (key == 'cessavltax1') {
			                        document.getElementById("totITCCESS").innerHTML = formatNumber(parseFloat(value).toFixed(2));
			                    }
			                }
			                if (key == 'statename') {
			                    form.find("input[id='billedtostatecode']").val(value);
			                }
			                if (key == 'address') {
			                    if (type == "ISD" || type == 'Isd') {
			                        form.find("textarea[id='isdbillingAddress']").val(value);
			                    } else {
			                        form.find("textarea[id='billingAddress']").val(value);
			                    }
			                }
			                if (key == 'consigneeaddress') {
			                    form.find("textarea[id='shippingAddress']").val(value);
			                    if (value != '') {
			                        $('#samebilladdress').attr("checked", "true");
			                    }
			                }
			                if (key == 'subSupplyType') {
			                    if (value != "" && value != null) {
			                        form.find("select[id='subsupplyType']").val(value.trim());
			                    }
			                }
			                if (key == 'bankdetails') {
			                    if (value == 'true') {
			                        if (type != 'Advance Adjusted Detail' || type != 'TXPA') {
			                            $('.addBankDetails').attr("checked", "true");
			                            $('.addSnilBankDetails').attr("checked", "true");
			                            if ($('.addBankDetails').is(":checked")) {
			                                $("#selectBankDiv").show();
			                                $("#selectBank").removeAttr("disabled");
			                                $(".bank-details-box").show();
			                            } else {
			                                $(".bank-details-box").hide();
			                                $("#selectBankDiv").hide();
			                            }
			                            if ($('.addSnilBankDetails').is(":checked")) {
			                                $("#selectBankDiv1").show();
			                                $("#selectBank1").removeAttr("disabled");
			                                $("#snil-bank-details").show();
			                            } else {
			                                $("#snil-bank-details").hide();
			                                $("#selectBankDiv1").hide();
			                            }
			                        } else {
			                            $(".bank-details-box").hide();
			                        }
			                    } else if (value == 'false') {
			                        $('.addBankDetails').removeAttr("checked");
			                        $('.addSnilBankDetails').removeAttr("checked");
			                        if ($('.addBankDetails').is(":checked")) {
			                            $("#selectBankDiv").show();
			                            $(".bank-details-box").show();
			                        } else {
			                            $("#selectBankDiv").hide();
			                            $(".bank-details-box").hide();
			                        }
			                        if ($('.addSnilBankDetails').is(":checked")) {
			                            $("#selectBankDiv1").show();
			                            $("#snil-bank-details").show();
			                        } else {
			                            $("#selectBankDiv1").hide();
			                            $("#snil-bank-details").hide();
			                        }
			                    }
			                }
			                if (key == 'isSez') {
			                    if (value == 'Y') {
			                        $('#impGoodsgstin').css('display', 'block');
			                        $('#stin').attr("required", "required");
			                    } else if (value == 'N') {
			                        $('#impGoodsgstin').css('display', 'none');
			                        $('#stin').removeAttr("required");
			                    }
			                }
			                /*if (key == 'exportType') {
			                    if (value == 'WOPAY') {
			                        $(".taxrate_textDisable").prop('disabled', true);
			                    }
			                }*/
			                if (key == 'bankname') {
			                    form.find("input[id='bankName']").val(value);
			                }
			                if (key == 'accountnumber') {
			                    form.find("input[id='bankAcctNo']").val(value);
			                }
			                if (key == 'accountname') {
			                    form.find("input[id='bankAccountName']").val(value);
			                }
			                if (key == 'branchname') {
			                    form.find("input[id='bankBranch']").val(value);
			                }
			                if (key == 'ifsccode') {
			                    form.find("input[id='bankIFSC']").val(value);
			                }
			                if (key == 'notes') {
			                    if (type != "Advance Adjusted Detail" || type != 'TXPA') {
			                        form.find("textarea[id='bankNotes']").val(value);
			                    } else {
			                        form.find("textarea[id='bankNotesadv']").val(value);
			                    }
			                }
			                if (key == 'terms') {
			                    form.find("textarea[id='bankTerms']").val(value);
			                }
			                if (key == 'invoicetype') {
			                    type = value;
			                    form.find("input[id='idInvType']").val(value);
			                }
			                if (key == 'itcreversaltype') {
			                    $('.itc_reversal').hide();
			                    $('#' + value + 'Parent').show();
			                }
			                if (key == 'id') {
			                    $('#invoiceid').val(value).attr('name', 'id');
			                    $('#invoiceid1').val(value).attr('name', 'id');
			                    $('#invoiceid2').val(value).attr('name', 'id');
			                }
			                if (key == 'matchingId') {
			                    $('#matchingId,#matchingId1').val(value);
			                }
			                if (key == 'matchingStatus') {
			                    $('#matchingStatus,#matchingStatus1').val(value);
			                }
			                if (key == 'strAmendment') {
			                    $('#strAmendment').val(value);
			                }
			                if (key == 'dateofinvoice') {
			                    form.find("input[id='dateofinvoice']").val(value);
			                }
			                if (key == 'tdstcsenable') {
			                    if (value == true) {
			                        $('#tcsval').attr("checked", "true").prop("checked", true);
			                        tcscheckval();
			                    }
			                }
			                if (key == 'tdsenable') {
			                    if (value == true) {
			                        $('#tdsval').attr("checked", "true").prop("checked", true);
			                        tdscheckval();
			                    }
			                }
			                if (type == 'Nil Supplies') {
			                    tdstcscal();
			                }
			                if(key == 'cessType'){
			                	if(value == 'Taxable Value' || value == null){
			                		$('#cess_taxable').attr("checked","true");	
			                	}else{
			                		$('#cess_qty').attr("checked","true");	
			                	}
			                }
		            	}
		            });
		            if (returnType == 'GSTR2A') {
		                if (type == 'B2B' || type == 'B2B Unregistered' || type == 'B2C' || type == 'Exports' || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services') {
		                    SnilFlag = false;
		                    addITCFlag = true;
		                    addAdvFlag = false;
		                    addITCRuleFlag = false;
		                    noadvFlag = true;
		                } else if (type == 'Advance Adjusted Detail' || type == 'ITC Reversal' || type == 'TXPA') {
		                    addITCFlag = false;
		                    if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
		                        noadvFlag = false;
		                        addAdvFlag = true;
		                        addITCRuleFlag = false;
		                        noadvFlag = true;
		                        $('.invoiceNumberText').html('Adv.No');
		                        $('.invoiceDateText').html('Adv.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
		                    } else {
		                        $('.nil-foot').attr("colspan", "2");
		                        addAdvFlag = false;
		                        addITCRuleFlag = true;
		                        noadvFlag = true;
		                    }
		                }
		            }
	                if (returnType == 'PROFORMAINVOICES') {
	                    $('#convert_PI_Invoice').show();
	                    $('#convert_PI_Invoice').attr("onclick", "convertFromPI_To_Inv('" + invId + "','PROFORMAINVOICES')");
	                } else {
	                    $('#convert_PI_Invoice').hide();
	                    $('#convert_PI_Invoice').removeAttr("onclick");
	                }
	                if (returnType == 'PurchaseOrder') {
	                    $('#convert_PO_Invoice').show();
	                    $('#convert_PO_Invoice').attr("onclick", "convertFromPO_To_Inv('" + invId + "','PurchaseOrder')");
	                } else {
	                    $('#convert_PO_Invoice').hide();
	                    $('#convert_PO_Invoice').removeAttr("onclick");
	                }
	                addtaxrates(type);
		            if (type != 'ISD') {
		                rowCount = 1;
		                if (rowCount < invoice.items.length) {
		                    for (var i = 1; i < invoice.items.length; i++) {
		                        if (invoice.items[i]) {
		                            add_row('${client.id}', returnType, '${usertype}');
		                        }
		                    }
		                }
		                if (vehiclerowCount < invoice.vehiclListDetails.length) {
		                    for (var i = 1; i < invoice.vehiclListDetails.length; i++) {
		                        if (invoice.vehiclListDetails[i]) {
		                            addEbillrow('vehicle_table');
		                        }
		                    }
		                }
		            }
		            for (var i = 1; i <= invoice.vehiclListDetails.length; i++) {
		                $('#vehicleNo' + i).val(invoice.vehiclListDetails[i - 1].vehicleNo);
		                $('#fromPlace' + i).val(invoice.vehiclListDetails[i - 1].fromPlace);
		                $('#fromState' + i).val(invoice.vehiclListDetails[i - 1].fromState);
		                $('#tripshtNo' + i).val(invoice.vehiclListDetails[i - 1].tripshtNo);
		                $('#userGSTINTransin' + i).val(invoice.vehiclListDetails[i - 1].userGSTINTransin);
		                $('#enteredDate' + i).val(invoice.vehiclListDetails[i - 1].enteredDate);
		                $('#transMode' + i).val(invoice.vehiclListDetails[i - 1].transMode);
		                $('#vehicleType' + i).val(invoice.vehicleType);
		                $('#transDocNo' + i).val(invoice.vehiclListDetails[i - 1].transDocNo);
		                $('#transDocDate' + i).val(invoice.vehiclListDetails[i - 1].transDocDate);
		                $('#groupNo' + i).val(invoice.vehiclListDetails[i - 1].groupNo);
		            }
		            if (invoice.items.length == 0) {
		                document.getElementById("totIGST").innerHTML = formatNumber(parseFloat(0).toFixed(2)); /* document.getElementById("totCGST").innerHTML=formatNumber(parseFloat(0).toFixed(2));document.getElementById("totSGST").innerHTML=formatNumber(parseFloat(0).toFixed(2)); */
		                document.getElementById("totCESS").innerHTML = formatNumber(parseFloat(0).toFixed(2));
		                if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'GSTR2' || returnType == 'GSTR6') {
		                    document.getElementById("totITCIGST").innerHTML = formatNumber(parseFloat(0).toFixed(2)); /* document.getElementById("totITCCGST").innerHTML=formatNumber(parseFloat(0).toFixed(2));document.getElementById("totITCSGST").innerHTML=formatNumber(parseFloat(0).toFixed(2));document.getElementById("totITCCESS").innerHTML=formatNumber(parseFloat(0).toFixed(2)); */
		                }
		            }
		            if (type == 'ISD') {
		                $('.noitc').show();
		                var isdtotaligst = 0,
		                    isdtotalcgst = 0,
		                    isdtotalsgst = 0,
		                    isdtotalcess = 0,
		                    isdtotTotal = 0,
		                    isdtotTaxable = 0;
		                $('.is-itc-no').hide();
		                $('.is-itc-yes').show();
		                $('#isdtotTaxable').show();
		            } else {
		                $(".particuler_type").hide();
		                $(".isdcess_amount ").hide();
		                $('.is-itc-no').show();
		                $('.is-itc-yes').hide();
		            }
		            if (type == 'ITC Reversal') {
		                var isdtotaligst = 0,
		                    isdtotalcgst = 0,
		                    isdtotalsgst = 0,
		                    isdtotalcess = 0,
		                    isdtotTotal = 0;
		                $('.is-itc-no').hide();
		                $('#particuler_type').html('Rules');
		                $('.noitc').hide();
		                $('.is-itc-yes').show();
		                $('#allinvoice1').append('<tr id="5" class="item_edit5 isdrows"><td id="sno_row1" align="center">5</td><td id="particular_row5" class="form-group noadvFlag particuler_type isdtab" style="display:none"><label>Amount in terms of rule 42(2)(a)</label><input type="text"  id="particular_val5" name="items[4].itcRevtype" value="rule7_2_b" style="display:none;"/></td><td id="isdigsttax_row5" class="form-group SnilFlag" align="right"><input type="text" class="form-control isdtype5 text-right" id="isdigsttax_text5" name="items[4].igstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row5" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype5 text-right" id="isdcgsttax_text5" name="items[4].cgstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row5" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype5 text-right" id="isdsgsttax_text5" name="items[4].sgstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text5" name="items[4].isdcessamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row5" class="tablegreybg form-group " align="right"><input type="text" class="form-control text-right" id="isdtotal_text5" name="items[4].total" readonly="readonly"></td></tr><tr id="6" class="item_edit6 isdrows"><td id="sno_row1" align="center">6</td><td id="particular_row6" class="form-group noadvFlag particuler_type isdtab" style="display:none"><label>On account of amount paid subsequent to reversal of ITC</label><input type="text"  id="particular_val6" name="items[5].itcRevtype" value="revitc" style="display:none;"/></td><td id="igsttax_row6" class="form-group SnilFlag " align="right"><input type="text" class="form-control isdtype6 text-right" id="isdigsttax_text6" name="items[5].igstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row6" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype6 text-right" id="isdcgsttax_text6" name="items[5].cgstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row6" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype6 text-right" id="isdsgsttax_text6" name="items[5].sgstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text6" name="items[5].isdcessamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row6" class="tablegreybg form-group " align="right"><input type="text" class="form-control text-right" id="isdtotal_text6" name="items[5].total" readonly="readonly"></td></tr><tr id="7" class="item_edit7 isdrows"><td id="sno_row1" align="center">7</td><td id="particular_row7" class="form-group noadvFlag particuler_type " style="display:none"><label>Any other liability (Pl specify)</label><input type="text"  id="particular_val7" name="items[6].itcRevtype" value="other" style="display:none;"/></td><td id="igsttax_row7" class="form-group SnilFlag " align="right"><input type="text" class="form-control isdtype7 text-right" id="isdigsttax_text7" name="items[6].igstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row7" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype7 text-right" id="isdcgsttax_text7" name="items[6].cgstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row7" class=" form-group SnilFlag " align="right"><input type="text" class="form-control isdtype7 text-right" id="isdsgsttax_text7" name="items[6].sgstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text7" name="items[6].isdcessamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row7" class="tablegreybg form-group " align="right"><input type="text" class="form-control text-right" id="isdtotal_text7" name="items[6].total" readonly="readonly"></td></tr>');
		            }
		            for (var i = 1; i <= invoice.items.length; i++) {
		                if (true) {
		                    $("#itemnotes_text" + i).val(invoice.items[i - 1].itemNotescomments);
		                    var exptype = $('#exportType').val();
		                    if (exptype == 'WOPAY') {
		                        $('#lutDiv').css("display", "block");
		                        $('#lutNo').val(invoice.lutNo);
		                    } else {
		                        $('#lutDiv').css("display", "none");
		                        $('#lutNo').val('');
		                    }
		                    if (invoice.items[i - 1].igstamount == null) {
		                        invoice.items[i - 1].igstamount = '';
		                    }
		                    if (invoice.items[i - 1].cgstamount == null) {
		                        invoice.items[i - 1].cgstamount = '';
		                    }
		                    if (invoice.items[i - 1].sgstamount == null) {
		                        invoice.items[i - 1].sgstamount = '';
		                    }
		                    if (invoice.items[i - 1].cessamount == null) {
		                        invoice.items[i - 1].cessamount = '';
		                    }
		                    if (invoice.items[i - 1].advReceiptNo == null) {
		                        invoice.items[i - 1].advReceiptNo = '';
		                    }
		                    if (invoice.items[i - 1].advReceiptDate == null) {
		                        invoice.items[i - 1].advReceiptDate = '';
		                    }
		                    if (invoice.items[i - 1].advStateName == null) {
		                        invoice.items[i - 1].advStateName = '';
		                    }
		                    if (invoice.items[i - 1].advReceivedAmount == null) {
		                        invoice.items[i - 1].advReceivedAmount = '';
		                    }
		                    if (invoice.items[i - 1].advAdjustableAmount == null) {
		                        invoice.items[i - 1].advAdjustableAmount = '';
		                    }
		                    if (invoice.items[i - 1].advadjustedAmount == null) {
		                        invoice.items[i - 1].advadjustedAmount = '';
		                    }
		                    if (invoice.items[i - 1].cessrate == null) {
		                        invoice.items[i - 1].cessrate = 0;
		                    }
		                    if(invoice.cessType == null || invoice.cessType == 'Taxable Value'){
		                    	invoice.items[i - 1].cessrate = invoice.items[i - 1].cessrate+"%";
				            }
		                    
		                    if (invoice.items[i - 1].rate == null) {
		                        invoice.items[i - 1].rate = 0;
		                    }
		                    if (invoice.items[i - 1].itemno == 'null') {
		                        invoice.items[i - 1].itemno = '';
		                    }
		                    if (type == 'Nil Supplies') {
		                        if (invoice.items[i - 1].quantity == null) {
		                            invoice.items[i - 1].quantity = '';
		                        }
		                    } else {
		                        if (invoice.items[i - 1].quantity == null) {
		                            invoice.items[i - 1].quantity = 0;
		                        }
		                    }
		                    if (invoice.items[i - 1].discount == null) {
		                        invoice.items[i - 1].discount = 0;
		                        if (invoice.items[i - 1].disper == null) {
			                        invoice.items[i - 1].disper = '';
			                    }
		                    }else{
		                    	if (invoice.items[i - 1].disper == null) {
			                        invoice.items[i - 1].disper = '';
			                    }else{
			                    	if(invoice.items[i - 1].disper == "percentage"){
			                    		invoice.items[i - 1].discount = invoice.items[i - 1].discount+"%";		
			                    	}
			                    }	
		                    }
		                    if (invoice.items[i - 1].exmepted == null) {
		                        invoice.items[i - 1].exmepted = 0;
		                    }
		                    if (invoice.items[i - 1].rateperitem == null) {
		                        invoice.items[i - 1].rateperitem = 0;
		                    }
		                    if (invoice.items[i - 1].taxablevalue == null) {
		                        invoice.items[i - 1].taxablevalue = 0;
		                    }
		                    if (invoice.items[i - 1].type == null) {
		                        invoice.items[i - 1].type = '';
		                    }
				    		if (invoice.items[i - 1].ledgerName == null) {
		                        invoice.items[i - 1].ledgerName = '';
		                    }
				    		if(document.getElementById("ledger" + i)){
								document.getElementById("ledger" + i).value = invoice.items[i - 1].ledgerName;
				    		}
							if(document.getElementById("disper" + i)){
								document.getElementById("disper" + i).value = invoice.items[i - 1].disper;
							}
		                    if (invoice.items.length == 1) {
		                        document.getElementById("totIGST").innerHTML = formatNumber(parseFloat(invoice.items[i - 1].igstamount).toFixed(2)); /* document.getElementById("totCGST").innerHTML=formatNumber(parseFloat(invoice.items[i-1].cgstamount).toFixed(2));document.getElementById("totSGST").innerHTML=formatNumber(parseFloat(invoice.items[i-1].sgstamount).toFixed(2)); */
		                        document.getElementById("totCESS").innerHTML = formatNumber(parseFloat(invoice.items[i - 1].cessamount).toFixed(2));
		                        if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'GSTR2' || returnType == 'GSTR6') {
		                            if (type == 'B2B' || type == 'B2B Unregistered' || type == 'B2C' || type == 'Exports' || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services') {
		                                document.getElementById("totITCIGST").innerHTML = formatNumber(parseFloat(invoice.items[i - 1].igstavltax).toFixed(2)); /* document.getElementById("totITCCGST").innerHTML=formatNumber(parseFloat(invoice.items[i-1].cgstavltax).toFixed(2));document.getElementById("totITCSGST").innerHTML=formatNumber(parseFloat(invoice.items[i-1].sgstavltax).toFixed(2));document.getElementById("totITCCESS").innerHTML=formatNumber(parseFloat(invoice.items[i-1].cessavltax).toFixed(2)); */
		                            }
		                        }
		                    }
		                    if (returnType == 'GSTR1') {
		                        if (type != 'Advances' && type != 'Advance Adjusted Detail' && type != 'TXPA') {
		                            if (invoice.items[i - 1].exmepted == null) {
		                                invoice.items[i - 1].exmepted = '0'
		                            }
		                           // document.getElementById("exempted_text" + i).value = invoice.items[i - 1].exmepted;
		                            $("#exempted_text" + i).val(invoice.items[i - 1].exmepted);
		                        }
		                    }
		                   
		                    if (type != 'Nil Supplies' && type != 'ITC Reversal' && type != 'ISD') {
		                        $("#igsttax_text" + i).val(invoice.items[i - 1].igstamount.toFixed(2));
		                        $("#cgsttax_text" + i).val(invoice.items[i - 1].cgstamount.toFixed(2));
		                        $("#sgsttax_text" + i).val(invoice.items[i - 1].sgstamount.toFixed(2));
		                        $("#cessamount_text" + i).val(invoice.items[i - 1].cessamount.toFixed(2));
		                        $("#taxrate_text" + i).val(invoice.items[i - 1].rate);
		                        $("#cessrate_text" + i).val(invoice.items[i - 1].cessrate);
		                    	//document.getElementById("igsttax_text" + i).value = invoice.items[i - 1].igstamount.toFixed(2);
		                       // document.getElementById("cgsttax_text" + i).value = invoice.items[i - 1].cgstamount.toFixed(2);
		                        //document.getElementById("sgsttax_text" + i).value = invoice.items[i - 1].sgstamount.toFixed(2);
		                        //document.getElementById("cessamount_text" + i).value = invoice.items[i - 1].cessamount.toFixed(2);
		                       // document.getElementById("taxrate_text" + i).value = invoice.items[i - 1].rate;
		                        //document.getElementById("cessrate_text" + i).value = invoice.items[i - 1].cessrate;
		                        if (type == 'B2B' || type == 'B2B Unregistered' || type == 'B2C' || type == 'B2CL' || type == 'Exports' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services') {
		                        	$("#discount_text" + i).val(invoice.items[i - 1].discount);
		                        	$("#disper" + i).val(invoice.items[i - 1].disper);
		                        	//document.getElementById("discount_text" + i).value = invoice.items[i - 1].discount;
		                            //document.getElementById("disper" + i).value = invoice.items[i - 1].disper;
		                        }
		                        $("#itemCustomField_text1" + i).val(invoice.items[i - 1].itemCustomField1 ? invoice.items[i - 1].itemCustomField1 : ' ');
		                        $("#itemCustomField_text2" + i).val(invoice.items[i - 1].itemCustomField2 ? invoice.items[i - 1].itemCustomField2 : ' ');
		                        $("#itemCustomField_text3" + i).val(invoice.items[i - 1].itemCustomField3 ? invoice.items[i - 1].itemCustomField3 : ' ');
		                        $("#itemCustomField_text4" + i).val(invoice.items[i - 1].itemCustomField4 ? invoice.items[i - 1].itemCustomField4 : ' ');
		                        $("#itemId_text" + i).val(invoice.items[i - 1].itemId ? invoice.items[i - 1].itemId : ' ');
		                    } else {
		                        if (type != 'ITC Reversal' && type != 'ISD') {
		                            document.getElementById("itcrule_text" + i).value = invoice.items[i - 1].type;
		                        } else {}
		                    }
		                    if(type == "Exports"){
		    					$('#totCurAmt').html(invoice.totalCurrencyAmount ? formatNumber(parseFloat(invoice.totalCurrencyAmount).toFixed(2)) : '0.00');
		    			   }
		                    if (type == 'ITC Reversal' || type == 'ISD') {
		                        isdtotaligst += invoice.items[i - 1].igstamount;
		                        isdtotalcgst += invoice.items[i - 1].cgstamount;
		                        isdtotalsgst += invoice.items[i - 1].sgstamount;
		                        isdtotalcess += invoice.items[i - 1].isdcessamount;
		                        if (invoice.items[i - 1].total == '' || invoice.items[i - 1].total == null) {
		                            document.getElementById("isdtotal_text" + i).value = '0'
		                        } else {
		                            document.getElementById("isdtotal_text" + i).value = invoice.items[i - 1].total.toFixed(2);
		                        }
		                        if (invoice.items[i - 1].igstamount == '' || invoice.items[i - 1].igstamount == null) {
		                            document.getElementById("isdigsttax_text" + i).value = '0'
		                        } else {
		                            document.getElementById("isdigsttax_text" + i).value = invoice.items[i - 1].igstamount.toFixed(2);
		                        }
		                        if (invoice.items[i - 1].isdcessamount == '' || invoice.items[i - 1].isdcessamount == null) {
		                            document.getElementById("isdisdcess_text" + i).value = '0'
		                        } else {
		                            document.getElementById("isdisdcess_text" + i).value = invoice.items[i - 1].isdcessamount.toFixed(2);
		                        }
		                        if (invoice.items[i - 1].sgstamount == '' || invoice.items[i - 1].sgstamount == null) {
		                            document.getElementById("isdsgsttax_text" + i).value = '0'
		                        } else {
		                            document.getElementById("isdsgsttax_text" + i).value = invoice.items[i - 1].sgstamount.toFixed(2);
		                        }
		                        if (invoice.items[i - 1].cgstamount == '' || invoice.items[i - 1].cgstamount == null) {
		                            document.getElementById("isdcgsttax_text" + i).value = '0'
		                        } else {
		                            document.getElementById("isdcgsttax_text" + i).value = invoice.items[i - 1].cgstamount.toFixed(2);
		                        }
		                        if (invoice.items[i - 1].isdType == '' || invoice.items[i - 1].isdType == null) {
		                            document.getElementById("particular_val" + i).value = ''
		                        } else {
		                            document.getElementById("particular_val" + i).value = invoice.items[i - 1].isdType;
		                        }
		                        if (isdtotaligst && isdtotaligst != '') {
		                            document.getElementById("isdtotIGST").innerHTML = formatNumber(parseFloat(isdtotaligst).toFixed(2));
		                        }
		                        if (isdtotalcgst && isdtotalcgst != '') {
		                            document.getElementById("isdtotCGST").innerHTML = formatNumber(parseFloat(isdtotalcgst).toFixed(2));
		                        }
		                        if (isdtotalsgst && isdtotalsgst != '') {
		                            document.getElementById("isdtotSGST").innerHTML = formatNumber(parseFloat(isdtotalsgst).toFixed(2));
		                        }
		                        if (isdtotalcess && isdtotalcess != '') {
		                            document.getElementById("isdtotisdcess").innerHTML = formatNumber(parseFloat(isdtotalcess).toFixed(2));
		                        }
		                        if (invoice.totalamount == '') {
		                            document.getElementById("isdtotTotal").innerHTML = formatNumber(parseFloat(0).toFixed(2));
		                        }
		                        document.getElementById("isdtotTotal").innerHTML = formatNumber(parseFloat(invoice.totalamount).toFixed(2));
		                        if (type == 'ISD') {
		                            document.getElementById("particular_val1").value = "Eligible - Credit distributed";
		                            document.getElementById("particular_val2").value = "Eligible - Credit distributed as";
		                            document.getElementById("particular_val3").value = "Ineligible - Credit distributed";
		                            document.getElementById("particular_val4").value = "Ineligible - Credit distributed as";
		                        }
		                    }
		                    if (type != 'Advances') {
		                        if (type != 'Advance Adjusted Detail' && type != 'TXPA') {
		                            if (type != 'ITC Reversal' && type != 'ISD') {
		                            	$("#rate_text" + i).val(invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem.toFixed(3) : '0.000');
		                                //document.getElementById("rate_text" + i).value = invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem.toFixed(3) : '0.000';
		                            }
		                        }
		                    }
		                    if (invoice.items[i - 1].itemno) {
		                        document.getElementById("product_text" + i).value = invoice.items[i - 1].itemno;
		                    }
		                    if (type != 'Advance Adjusted Detail'&& type != 'TXPA') {
		                        if (type != 'ITC Reversal' && type != 'ISD') {
		                            if (invoice.items[i - 1].code == null || invoice.items[i - 1].code == '') {
		                                invoice.items[i - 1].code = '';
		                            }
		                            $("#hsn_text" + i).val(invoice.items[i - 1].hsn);
		                            //document.getElementById("hsn_text" + i).value = invoice.items[i - 1].hsn;
		                            $("#hsn_row" + i).attr("data", invoice.items[i - 1].code);
		                        }
		                    }
		                    if (type != 'Advances') {
		                        if (type != 'Advance Adjusted Detail' && type != 'TXPA') {
		                            if (type != 'ITC Reversal' && type != 'ISD') {
		                            	$("#uqc_text" + i).val(invoice.items[i - 1].uqc);
		                            	$("#qty_text" + i).val(invoice.items[i - 1].quantity);
		                                //document.getElementById("uqc_text" + i).value = invoice.items[i - 1].uqc;
		                                //document.getElementById("qty_text" + i).value = invoice.items[i - 1].quantity;
		                            }
		                        }
		                    }
		                    if (type != 'Advance Adjusted Detail' && type != 'TXPA' && type != 'ITC Reversal' && type != 'ISD') {
		                    	$("#taxableamount_text" + i).val(invoice.items[i - 1].taxablevalue.toFixed(2));
		                       // document.getElementById("taxableamount_text" + i).value = invoice.items[i - 1].taxablevalue.toFixed(2);
		                    }
		                    if (invoice.items[i - 1].total == undefined || invoice.items[i - 1].total == null) {
		                        invoice.items[i - 1].total = 0;
		                    }
		                    if (type != 'ITC Reversal' && type != 'ISD') {
		                    	$("#total_text" + i).val(invoice.items[i - 1].total);
		                        //document.getElementById("total_text" + i).value = invoice.items[i - 1].total;
		                    }
		                }
	                    if (returnType != 'EWAYBILL') {
		                if (invoice.lutNo != null && invoice.lutNo != '') {
		                    $('#lutDiv').css("display", "block");
		                    $('#lutNo').val(invoice.lutNo);
		                } else {
		                    $('#lutDiv').css("display", "none");
		                    $('#lutNo').val('');
			                }
		                }
		                if (type == 'Advances') {
		                    document.getElementById("rate_text" + i).value = invoice.items[i - 1].rateperitem ? invoice.items[i - 1].rateperitem.toFixed(3) : '0.000';
		                }
		                if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
		                    updateAdvRateType(clntStatename, invoice.items[i - 1].advStateName, returnType);
		                    document.getElementById("advrcno_text" + i).value = invoice.items[i - 1].advReceiptNo;
		                    document.getElementById("advrcdt_text" + i).value = invoice.items[i - 1].advReceiptDate;
		                    document.getElementById("advrcposs_text" + i).value = invoice.items[i - 1].advStateName;
		                    document.getElementById("advrcamt_text" + i).value = invoice.items[i - 1].advReceivedAmount ? invoice.items[i - 1].advReceivedAmount.toFixed(2) : '0.00';
		                    document.getElementById("advrcavail_text" + i).value = invoice.items[i - 1].advAdjustableAmount ? invoice.items[i - 1].advAdjustableAmount.toFixed(2) : '0.00';
		                    document.getElementById("advrcavailadj_text" + i).value = invoice.items[i - 1].advadjustedAmount ? invoice.items[i - 1].advadjustedAmount.toFixed(2) : '0.00';
		                	//document.getElementById("advremaingamount_text" + i).value = invoice.items[i - 1].advRemaingAmount.toFixed(2);
		                	
		                    if(invoice.items[i - 1].advRemaingAmount || invoice.items[i - 1].advRemaingAmount == null){
		                    	
		                    	checkadvpayemnt(i);
		                    }else{
		                    	
		                    	document.getElementById("advremaingamount_text" + i).value = invoice.items[i - 1].advRemaingAmount.toFixed(2);
		                    } 
		                }
		                if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'GSTR2' || returnType == 'GSTR6') {
		                    if (type == 'B2B' || type == 'B2B Unregistered' || type == 'B2C' || type == 'Exports' || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services') {
		                        if (invoice.items[i - 1].elgpercent == null) {
		                            invoice.items[i - 1].elgpercent = 0;
		                        }
		                        if (invoice.items[i - 1].elg == 'null') {
		                            invoice.items[i - 1].elg = '';
		                        }
		                        var billst = $('#billedtostatecode').val();
		                        var billgstn = $('#billedtogstin').val();
		                        if (billst = billgstn) {
		                            interStateFlag = true;
		                        } else if (billgstn = "") {
		                            interStateFlag = true;
		                        } else {
		                            interStateFlag = false;
		                        }
		                        if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
			                        if(type == 'B2B Unregistered' || type == 'Credit/Debit Note for Unregistered Taxpayers'){
			                        	var suptype = $('#printerintra').val();
			                    		if(suptype == 'Inter'){
			                    			interStateFlag=false;
			                    		}else{
			                    			interStateFlag=true;
			                    		}
			                        }
		                        }
		                        document.getElementById("itctype_text" + i).value = invoice.items[i - 1].elg;
		                        document.getElementById("itcpercent_text" + i).value = invoice.items[i - 1].elgpercent;
		                        document.getElementById("igstitc_text" + i).value = invoice.items[i - 1].igstavltax.toFixed(2);
		                        document.getElementById("cgstitc_text" + i).value = invoice.items[i - 1].cgstavltax.toFixed(2);
		                        document.getElementById("sgstitc_text" + i).value = invoice.items[i - 1].sgstavltax.toFixed(2);
		                        document.getElementById("cessitc_text" + i).value = invoice.items[i - 1].cessavltax.toFixed(2);
		                        if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
		                            var billstate = $('#billedtostatecode').val();
		                            var itctype = $('#itctype_text' + i).val();
		                            $('#itcpercent_text' + i).attr("readonly", false);
		                            $('#itcpercent_text' + i).removeClass("disabled");
		                            $('#itctype_text' + i).attr('readonly', false);
		                            $('#itctype_text' + i).removeClass("disabled");
		                            $('#itcpercent_text1').attr("readonly", false);
		                            $('#itcpercent_text1').removeClass("disabled");
		                            $('#itctype_text1').attr('readonly', false);
		                            $('#itctype_text1').removeClass("disabled");
		                            if (itctype != "no") {
		                                $('#itcpercent_text' + i).attr("readonly", false);
		                                $('#itcpercent_text' + i).removeClass("disabled");
		                                $('#itctype_text' + i).attr('readonly', false);
		                                $('#itctype_text' + i).removeClass("disabled");
		                            } else {
		                                $('#itcpercent_text' + i).attr("readonly", true);
		                                $('#itcpercent_text' + i).addClass("disabled");
		                                $('#itctype_text' + i).attr('readonly', false);
		                                $('#itctype_text' + i).removeClass("disabled");
		                            }
		                        }
		                    } else if (type == 'Nil Supplies') {
		                        $("#itcrule_text" + i).val(invoice.items[i - 1].type);
		                    }
		                }
		            }
		        }
		    
		        if (returnType == 'EWAYBILL') {
	                $('#samebilladdress').prop("checked", false);
	                $('#lutDiv').css("display", "none");
		   	    if (ewayBillNo == '') {
		   	    	$('.vehicle_updtrsn,#vehicleupDt,#cancelEwayBillInvoice').css("display", "none");
		   	    } else {
		   	    	if(uptVehicle){
		   	    		 $('#vehicleupDt').css("display", "block");
	                    $('.vehicle_updtrsn').css("display", "block");
		   	    	}
		   	    	if(canEbill){
		   	    	 	$('#cancelEwayBillInvoice').css("display", "block");
		   	    	 }
	                    /* $('#vehicleupDt,#cancelEwayBillInvoice').css("display", "block");
	                    $('.vehicle_updtrsn').css("display", "block"); */
		   	    }
		   	 if(ewayBillStatus == 'CNL'){
	                    $('#gstStatus').html("Eway Bill Cancelled");
	                    $("#returnTypes").html("PREVIEW EWAY BILL INVOICE");
	     		$("#savebtn,#cancelEwayBillInvoice,#vehicleupDt,#ewayBillSave_btn").hide();
	     		}
		   	    $('#cancelEwayBillInvoice').attr('onclick', 'showEwayBillCancelPopup("' + invId + '", "' + returnType + '", "' + ewayBillNo + '")');
	                $('#save_btn').css('display', 'none');
	                $('.notes_terms_wrap').css('display', 'none');
		   	    $('.no_vehicle,#customer_notes_wrap,.Terms_Conditions_wrap').css('display', 'none');
		   	} else {
		   	    $('#save_btn').css('display', 'block');
		   	    $('.no_vehicle').css('display', 'flex');
		   	    if (type != 'ADVANCE ADJUSTED DETAIL' || type != 'TXPA' || type != 'ADVANCES') {
		   	        if (type != 'ITC REVERSAL INVOICE') {
		   	            $('.Terms_Conditions_wrap').css('display', 'block');
		   	        }
		   	        $('.Terms_Conditions_wrap').css('display', 'block');
		   	        $('#customer_notes_wrap').css('display', 'block');
		   	    }
		   	}
		        var gstnumber = $('#billedtogstin').val();
		    	if (gstnumber == '') {
		    	    $('#invokegstnPublicAPI1').addClass("disable");
		    	} else {
		    	    $('#invokegstnPublicAPI1').removeClass("disable");
		    	}
		    	if (type != 'ISD') {
		    	    $('#particuler_type').css("display", "none");
		    	    $('.isdcess_amount').hide();
		    	}
		    	if (type == 'B2B Unregistered' || type == 'B2B') {
		    	    $('#product_text1').css("display", "table-cell");
		    	    $('.advtext').css("display", "table-cell");
		    	    $('.row_foot').css("display", "table-cell");
		    	    $('.addbutton').css("display", "table-cell");
		    	}
		    	invoiceNumber = $('.serialnoofinvoice').val();
		    	ewayBillNo = $('#ewayBillNumber').val();
		    	if (returnType != 'EWAYBILL') {
	        $('.btn_popup_cancel').attr('onclick', 'showCancelPopup("' + invId + '", "' + returnType + '", "' + invoiceNumber + '","<c:out value="${otherreturn_type}"/>")');
		    	}
		    	$('.btn_popup_amnd').attr('onclick', 'editAsAmendment("' + invId + '", "' + returnType + '")');
		    	$('#invview_Process').addClass('d-none');
		    	if(invoice.irnNo == null || invoice.irnNo == ""){
		    		$('#sirnNumber_txt').hide();
		    		$('#sirnNumber').html("");
	        	}else{
	        		$('#sirnNumber_txt').show();
	        		$('#sirnNumber').html(invoice.irnNo);
					$('.form-control,.che_form-control').addClass('disabled');
					$('.invedit,.buyerEmail').removeClass('disabled');
					$('#addrow').attr('disabled', true).css("background-color", "white");
					$('.gstr2adeletefield').css("display", "none");
					$('#cancelInvoice').css("display", "none");
	        	}
		    	if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'GSTR2' || returnType == 'GSTR6') {
		    		var p = document.getElementById("tdsamount");
		    		if(p){
		    			document.getElementById("tdsamount").value = invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00';
		    			
				    	$('#invvalwithtds').html(invoice.netAmount);	
		    		}
		    		
		    		var q = document.getElementById("tds_amount");
		    		if(q){
		    			document.getElementById("tds_amount").value = invoice.tdsAmount ? invoice.tdsAmount.toFixed(2) : '0.00';
		    			
				    	$('#invvalwith_tds').html(invoice.tdsNetAmount);	
		    		}
		    		$('#tdsamt').val(invoice.tdsAmount ? invoice.tdsAmount.toFixed(2) : '0.00');
		    		$('#invvalwith_tds,#invvalwithtds').html(invoice.netAmount);
		    		$('#tdsfield').html(invoice.tdsAmount ? invoice.tdsAmount.toFixed(2) : '0.00');
		    		$('#tcsfield').html(invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00');
		    	}else{
		    		var p = document.getElementById("tcsamount");
		    		if(p){
		    			document.getElementById("tcsamount").value = invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00';
				    	$('#invvalwithtcs').html(invoice.netAmount);	
		    		}
		    		$('#tcsamt').val(invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00');
		    		$('#invvalwithtcs').html(invoice.netAmount);
		    		$('#tcsfield').html(invoice.tcstdsAmount ? invoice.tcstdsAmount.toFixed(2) : '0.00');
		    	}
		    	
		    	OSREC.CurrencyFormatter.formatAll({
					selector: '.tcsindformat'
				});
		    	showInvPopup(type, returnType, false, gstStatus, journalsOrNotinjournals,revChargeNo,invoice);
		    });
		
		}
	function showInvPopup(type,returnType, addFlag, gstStatus,journalsOrNotinjournals,revChargeNo,invoice){
		googlemapsinitialize();
		abcd(type,returnType, addFlag, gstStatus,journalsOrNotinjournals,revChargeNo,invoice);
	}
	function addtaxrates(type){
		$('#taxrate_text1').find('option').remove().end();
		var taxrateValues = { "0": "- Select -", "-1": "Nil Rated","-2":"Exempted","-3":"Non-GST","0":"0%","0.1":"0.1%","0.25":"0.25%","1":"1%","1.5":"1.5%","3":"3%","5":"5%","7.5":"7.5%","12":"12%","18":"18%","28":"28%"};
		if (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers') {
			 $("#taxrate_text1").append($("<option></option>").attr("value", "").text("- Select -"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "-1").text("Nil Rated"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "-2").text("Exempted"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "-3").text("Non-GST"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0").text("0%"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0.1").text("0.1%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0.25").text("0.25%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "1").text("1%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "1.5").text("1.5%"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "3").text("3%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "5").text("5%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "7.5").text("7.5%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "12").text("12%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "18").text("18%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "28").text("28%"));
		}else{
			 $("#taxrate_text1").append($("<option></option>").attr("value", "").text("- Select -"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0").text("0%"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0.1").text("0.1%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "0.25").text("0.25%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "1").text("1%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "1.5").text("1.5%"));		
			 $("#taxrate_text1").append($("<option></option>").attr("value", "3").text("3%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "5").text("5%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "7.5").text("7.5%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "12").text("12%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "18").text("18%"));
			 $("#taxrate_text1").append($("<option></option>").attr("value", "28").text("28%"));
		}		
	}
	function abcd(type, returnType, addFlag, gstStatus, journalsOrNotinjournals,revChargeNo,invoiceCustomData) {
		if(type == 'Advances'){
			$('.backdetails_wrap').css("margin-top","-69px");
		}
		if(addFlag){
			addtaxrates(type);
			if(type == 'Advance Adjusted Detail'){
				$('#atcgstamt,#atsgstamt,.atcsgstamt,#atigstamt,.atigstamt').css("display","block");
			}
			invoiceNumber = undefined;
			if(returnType == 'EWAYBILL'){
		    	ewyaddr();
		    }
		}
		if(addFlag){
			if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
				if(enablePurCess == true || enablePurCess == "true"){
					$('#invoiceLevelCess').prop("checked",true).val("Yes");
					$('#invLevelCess').val("Yes");
			   	}else{
			    	$('#invoiceLevelCess').prop("checked",false).val("No");
					$('#invLevelCess').val("No");
			    }
			}else{
				$('.printerintra').css("display","none");
				if(enableSalesCess == true || enableSalesCess == "true"){
					$('#invoiceLevelCess').prop("checked",true).val("Yes");
					$('#invLevelCess').val("Yes");
			   	}else{
			    	$('#invoiceLevelCess').prop("checked",false).val("No");
					$('#invLevelCess').val("No");
			    }
			}
		}
		var invoiceLevel_Cess = $('#invLevelCess').val();
		$('#tdstcssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
		if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
			$('#tdssection').append($("<option></option>").attr("value","").text("-- Select Section --"));
		}
		$('#invCustField1,#invCustField2,#invCustField3,#invCustField4').css('display', 'none');
		getcustomFieldsData(customFieldsData,returnType,addFlag,invoiceCustomData);
	    if (returnType == 'ANX1') {
	        addITCFlag = false;
	        addAdvFlag = false;
	        addITCRuleFlag = false;
	        noadvFlag = false;
	    }
	    $('#ewyshimentAddrEdit').removeClass('d-none');
	    if (returnType == 'EWAYBILL') {
	        eBillinvoices();
	        $('.eCommerceDiv').css('display', 'none');
	        $('.invTypeDiv').css('display', 'none');
	        $('#billDate,#diffPer,#save_btn').css('display', 'none');
	        $('.vehicleTable').css('display', 'inline-table');
	        $('#transdistancediv,#toacttostatecodediv,#fromactfromstatecodediv,#vehicletxt,#fromGstindiv,#fromTrdNamediv,#fromAddr1div,#fromAddr2div,#fromPlacediv,#fromPincodediv,#fromStateCodediv,#toGstindiv,#toTrdNamediv,#toAddr1div,#toAddr2div,#toPlacediv,#toPincodediv,#toStateCodediv,.ewaybillDateDiv,#fromPlacediv,#fromStatediv,#usetGSTINdiv,#transdiv,#transdocdiv,#transDocDatediv,#groupNodiv,#documentTypediv,#statusdiv,#districtdiv,#validDaysdiv,#validDatediv,#extendeddiv,#rejectStatusdiv,#vehicleTypediv,#transactionTypediv,#othervaluediv,#cessnonadvdiv,#vehiclediv,#tripshtNodiv,#transporterIddiv,#transporterNamediv,#supplyTypediv,#subsupplyTypediv,.ewaybilladdress').css("display", "block");
	        $('.notewaybilladdress').css("display","none");
		if(addFlag){
			$('.vehicledetails').addClass("astrich");
			$('.ewaybillvehicledetails').attr("required","required");
		}else{
			$('.vehicledetails').removeClass("astrich");
			$('.ewaybillvehicledetails').removeAttr("required");
		}
	        $('.btn_popup_save').hide();
	    } else {
	        $('#save_btn').css('display', 'block');
	        $('.eCommerceDiv,.invTypeDiv,#transdistancediv,#toacttostatecodediv,#fromactfromstatecodediv,#vehicletxt,#fromGstindiv,#fromTrdNamediv,#fromAddr1div,#fromAddr2div,#fromPlacediv,#fromPincodediv,#fromStateCodediv,#toGstindiv,#toTrdNamediv,#toAddr1div,#toAddr2div,#toPlacediv,#toPincodediv,#toStateCodediv,.vehicleTable ,.ewaybillDateDiv,#fromPlacediv,#fromStatediv,#usetGSTINdiv,#transdiv,#transdocdiv,#transDocDatediv,#groupNodiv,#documentTypediv,#statusdiv,#districtdiv,#validDaysdiv,#validDatediv,#extendeddiv,#rejectStatusdiv,#vehicleTypediv,#transactionTypediv,#othervaluediv,#cessnonadvdiv,#vehiclediv,#tripshtNodiv,#transporterIddiv,#transporterNamediv,#supplyTypediv,#subsupplyTypediv,.ewaybilladdress').css("display", "none");
	        $("input:hidden[class='ewayaddrfields']").remove();
	        $('.notewaybilladdress').css("display","block");
	        $('#sameasBillingAddrDiv').css("display","contents");
	    	$('.vehicledetails').removeClass("astrich");
		$('.ewaybillvehicledetails').removeAttr("required");
	    }
	    var tableng = $('#allinvoice tr').length;
	    $('#ewayBillError').html("");
	    if (tableng == '2') {
	        $('.item_delete').hide();
	    } else {
	        $('.item_delete').show();
	    }
	    $('#transactiondate').css('display', 'none');
	    $('.rateinctax').html('');
	    $('#billgstin_name').html('');
	    if (type == 'Nil Supplies' || type == 'Advances') {
	        $('.rateinclusive').css('display', 'none');
	    } else {
	        $('.rateinclusive').css('display', 'block');
	    }
	    if (returnType == 'GSTR1' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES') {
	        $('#itcClaimedDateP').css('display', 'none');
	        if (returnType == 'GSTR1') {
	            $('.apply_type').text('Add TCS');
	            $('.perc_type').text('TCS Percentage');
	            var abc = "";
	            if (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers') {
	                $('.ledgerType').text('(Debit)');
	                $('.ledgerTypep').text('Debited');
	                if (addFlag) {
	                    updateDocType();
	                }
	            } else {
	            	if(type == 'Advances'){
	            		$('.ledgerType').text('(Debit)');
		                $('.ledgerTypep').text('Debited');
	            	}else{
		                $('.ledgerType').text('(Credit)');
		                $('.ledgerTypep').text('Credited');
	            	}
	            }
	            if (!addFlag) {
	                abc = $('#invTyp').val();
	            }
	            $("select#invTyp option[value='SEWP']").text("Supplies to SEZ with Payment");
	            $("select#invTyp option[value='SEWOP']").text("Supplies to SEZ without Payment");
	            $("select#invTyp option[value='R']").remove();
	            $("select#invTyp option[value='DE']").remove();
	            $("select#invTyp option[value='SEWP']").remove();
	            $("select#invTyp option[value='SEWPC']").remove();
	            $("select#invTyp option[value='SEWOP']").remove();
	            $("select#invTyp").append($("<option></option>").attr("value", "R").text("Regular"));
	            $("select#invTyp").append($("<option></option>").attr("value", "DE").text("Deemed Exports"));
	            $("select#invTyp").append($("<option></option>").attr("value", "SEWP").text("Supplies to SEZ with Payment"));
	            $("select#invTyp").append($("<option></option>").attr("value", "SEWPC").text("Supplies to SEZ with payment(Tax collected from customer)"));
	            $("select#invTyp").append($("<option></option>").attr("value", "SEWOP").text("Supplies to SEZ without Payment"));
	            $('#invTyp').val(abc);
	            $("#itcrule_text1 option[value='From Compounding Dealer']").remove();
	        } 
	    } else {
	        $("#itcrule_text1 option[value='From Compounding Dealer']").remove();
	        $("#itcrule_text1").append($("<option></option>").attr("value", "From Compounding Dealer").text("From Compounding Dealer"));
	    }
	    if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseOrder') {
	        if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
	            if (type == 'B2B' || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services' || type == 'CDNURA') {
	                $('#itcClaimedDateP').css('display', 'block');
	            }
	            if (addFlag) {
		           // $('.perc_type').text('TDS Percentage');
		            $('.apply_type').text('Add TDS');
		            $('#tds_val').prop("checked",true);
	            }
	            if (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers') {
	                $('.ledgerType').text('(Credit)');
	                $('.ledgerTypep').text('Credited');
	                if (addFlag) {
	                    updateDocType();
	                }
	            } else {
	            	if(type == 'Advances'){
	            		 $('.ledgerType').text('(Credit)');
			             $('.ledgerTypep').text('Credited');
	            	}else{
	            		 $('.ledgerType').text('(Debit)');
	 	                 $('.ledgerTypep').text('Dedited');
	            	}
	            }
	            $("select#invTyp option[value='SEWP']").text("Supplies from SEZ with Payment");
	           //$("select#invTyp option[value='SEWOP']").text("Supplies from SEZ without Payment");
	            if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
	            	$("select#invTyp option[value='DE']").remove();
	            	$("select#invTyp option[value='SEWOP']").remove();
	            	$("select#invTyp option[value='SEWPC']").remove();
	            }else{
	            	
	            }
	        } 
	      }
	        if (addFlag) {
	            $('#clientAddress').val('${client.address}');
	            if (returnType != 'ANX1' && returnType != 'ANX1A' && returnType != 'ANX2') {
	               // tcstdsoptions(returnType,"tds");
	            }
	            $('#vendorName').val('');
	            $('.roundOffTotal,.addBankDetails,.diffPercent,.tcsval').prop('checked', false);
	            $('#includetax,#samebilladdress').prop('checked', false);
	            if(type == "Advances"){
	            	$('#includetax').prop('checked', true);
	            }
	            $('#hiddenroundOffTotalValue').val(0.0);
	            $('#invoiceCustomerId,#companyDBId').val('');
	            $('#retType').val('');
	            $('#atcustname').text('');
	            $('#advPCustname').val('');
	            $('#atinvamt').text('');
	            $('#advPInvamt').val('');
	            $('#atigstamt').text('');
	            $('#advPIgstamt').val('');
	            $('#atcgstamt').text('');
	            $('#advPCgstamt').val('');
	            $('#atsgstamt').text('');
	            $('#advPSgstamt').val('');
	        }
	        AdvanceFlag = true;
	        $('.AdvancesFlag').show();
	        $('.advAmount').addClass('tablegreybg');
	        if (returnType == 'GSTR1') {
	            $('#diffPer').css("display", "block");
	            $('.nil-foot').attr('colspan', '8');
	        } else {
	            $('#diffPer').css("display", "none");
	        }
	        $('#invno_Msg').html('');
	        if (type == 'Nil Supplies') {
	            $('.itcruleval').attr("required", true);
	            $.ajax({
	                url: contextPath + '/invtypes/' + type + '/' + returnType,
	                async: true,
	                cache: false,
	                success: function(invTypes) {
	                    invtyperule = invTypes;
	                }
	            });
	        } else {
	            $('.itcruleval').attr("required", false);
	        }
	        if (type == 'ITC Reversal') {
	            addITCRuleFlag = true;
	            $('.addITCRuleFlag').show();
	        } else {
	            addITCRuleFlag = false;
	            $('.addITCRuleFlag').hide();
	        }
	        var rtype;
	        if (returnType == 'Purchase Register') {
	            rtype = 'GSTR2';
	            if (type == 'Import Goods' || type == 'Import Services') {
	                interStateFlag = false;
	            }
	        } else {
	            rtype = returnType;
	        }
	        if (type == 'Nil Supplies') {
	            addITCRuleFlag = true;
	            $('.addITCRuleFlag').show();
	            $('.AdvancesFlag').show();
	            SnilFlag = false;
	            AdvanceFlag = true;
	            $('.AdvancesFlag').show();
	            $('.SnilFlag').hide();
	            $('.disFlag').css("display", "none");
	            if (returnType == 'ANX1') {
	                $('.nil-foot').attr("colspan", "4");
	            } else {
	                $('.nil-foot').attr("colspan", "5");
	            }
	        } else if (type == 'Advances') {
	            SnilFlag = true;
	            $('.SnilFlag').show();
	            AdvanceFlag = false;
	            $('.AdvancesFlag').hide();
	            $('.nil-foot').attr("colspan", "4");
	            $('.advAmount').removeClass('tablegreybg');
	        } else if (type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'Credit/Debit Notes') {
	            if (returnType == 'GSTR1' || returnType == 'Purchase Register') {
	                SnilFlag = true;
	                addITCFlag = true;
	                $('.SnilFlag').show();
	                $('.nil-foot').attr("colspan", "6");
	            }
	            $('.disFlag').css("display", "none");
	            CreditFlag = false;
	        } else {
	            SnilFlag = true;
	            AdvanceFlag = true;
	            $('.AdvancesFlag').show();
	            $('.SnilFlag').show();
	            $('.nil-foot').attr("colspan", "8");
	        }
	        var id = '<c:out value="${client.id}"/>';
	        var month = '<c:out value="${month}"/>';
	        var year = '<c:out value="${year}"/>';
	        var monthyear;
	        var datepick = $("#datetimepicker").val();
	        if (datepick != null) {
	            monthyear = datepick.replace("-", "");
	            var ajaxurl = "${contextPath}/getclientstatus/" + id + "/" + rtype + "/" + monthyear;
	        } else {
	            monthyear = (month < 10 ? "0" + month : month + "") + year;
	            var ajaxurl = "${contextPath}/getclientstatus/" + id + "/" + rtype + "/" + monthyear;
	        }
	        var invdate = null;
	        if (addFlag) {
	            invdate = 'date+/' + month + '/' + year;
	            $.ajax({
	                url: ajaxurl,
	                contentType: 'application/json',
	                success: function(response) {
	                    if (response == 'Pending') {
	                        var invoicedate = new Date().getDate() + "/" + month + "/" + year;
							var minimumdate = '';
							if(rtype == 'GSTR1'){
								minimumdate = '${client.cutOffDateForSales}';
							}else if(rtype == 'GSTR2'){
								minimumdate = '${client.cutOffDateForPurchases}';
							}
							if(minimumdate == ''){
								minimumdate = '30/1/1970';
							}else{
								var invres = invoicedate.split("/");
								var invmdte = invres[2]+"-"+invres[1]+"-"+invres[0];
								var minres = minimumdate.split("/");
								var minmdte = minres[2]+"-"+minres[1]+"-"+minres[0];
								var g1 = new Date(invmdte); 
								var g2 = new Date(minmdte);
								if (g1.getTime() <= g2.getTime()) {
									invoicedate = minimumdate;
								}
							}
							var res = minimumdate.split("/");
							var mdte = res[1]+"/"+res[0]+"/"+res[2];
	                        $('#dateofinvoice').datetimepicker({
	                            value: invoicedate,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: '0',
	                            minDate: new Date(mdte),
	                            onSelectDate: function(date) {
	                                var selectedDate = new Date(date);
	                                var msecsInADay = 86400000;
	                                var endDate = new Date(selectedDate.getTime());
	                            }
	                        });
	                        var mn = (parseInt(month) - 1).toString();
	                        $('.billDate').datetimepicker({
	                            value: invoicedate,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: false
	                        });
	                        $('#dateofitcClaimed').datetimepicker({
	                            value: invoicedate,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: false
	                        });
							var res1 = invoicedate.split("/");
							var mdte1 = res1[1]+"/"+res1[0]+"/"+res1[2];
							$('#duedate_div').datetimepicker({
								timepicker: false,
								format: 'd/m/Y',
								maxDate: false,
								minDate:  new Date(mdte1)
							});
	                    } else {
	                        $('#dateofinvoice').datetimepicker({
	                            value: invdate,
	                            maxDate: false,
	                            format: 'd/m/Y',
	                            timepicker: false,
	                            container: '#myModalId',
	                            widgetPositioning: {
	                                vertical: 'bottom'
	                            },
	                            beforeShowDay: function(date) {
	                                var month = date.getMonth() + 1;
	                                var day = date.getDate();
	                            },
	                            minDate: new Date(year, month, '01')
	                        });
							$('#duedate_div').datetimepicker({
								value: invdate,
								timepicker: false,
								format: 'd/m/Y',
								maxDate: false,
								minDate:  new Date(year, month, '01')
							});
	                    }
	                },
	                error: function(errorres) {}
	            });
	        } else {
	            $('.pInvno').css('display', 'none');
	            invdate = $('#dateofinvoice').val();
	            var dtitcClaimed = $('#dateofitcClaimed').val();
	            $.ajax({
	                url: ajaxurl,
	                contentType: 'application/json',
	                success: function(response) {
	                    if (response == 'Pending') {
							var minimumdate = '';
							if(rtype == 'GSTR1'){
								minimumdate = '${client.cutOffDateForSales}';
							}else if(rtype == 'GSTR2'){
								minimumdate = '${client.cutOffDateForPurchases}';
							}
							if(minimumdate == ''){
								minimumdate = '30/1/1970';
							}
							var res = minimumdate.split("/");
							var mdte = res[1]+"/"+res[0]+"/"+res[2];
	                        $('#dateofinvoice').datetimepicker({
	                            value: invdate,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: false,
	                            minDate: new Date(mdte),
	                            onSelectDate: function(date) {
	                                var selectedDate = new Date(date);
	                                var msecsInADay = 86400000;
	                                var endDate = new Date(selectedDate.getTime());
	                            }
	                        });
	                        $('#dateofitcClaimed').datetimepicker({
	                            value: dtitcClaimed,
	                            timepicker: false,
	                            format: 'd/m/Y',
	                            maxDate: false
	                        });
							var res1 = invdate.split("/");
							var mdte1 = res1[1]+"/"+res1[0]+"/"+res1[2];
							$('#duedate_div').datetimepicker({
								timepicker: false,
								format: 'd/m/Y',
								maxDate: false,
								minDate: new Date(mdte1)
							});
	                    } else {
	                        $('#dateofinvoice').datetimepicker({
	                            value: invdate,
	                            maxDate: false,
	                            format: 'd/m/Y',
	                            timepicker: false,
	                            container: '#myModalId',
	                            widgetPositioning: {
	                                vertical: 'bottom'
	                            },
	                            beforeShowDay: function(date) {
	                                var month = date.getMonth() + 1;
	                                var day = date.getDate();
	                            },
	                            minDate: new Date(year, month, '01')
	                        });
							$('#duedate_div').datetimepicker({
								timepicker: false,
								format: 'd/m/Y',
								maxDate: false,
								minDate:  new Date(year, month, '01')
							});
	                    }
	                },
	                error: function(errorres) {}
	            });
	        }
	        $(".body-cls").toggleClass('no-scroll');
	        var usrType = '<c:out value="${usertype}"/>';
	        var clntStatus = '<c:out value="${client.status}"/>';
	        if (returnType == 'GSTR6' && type == 'ISD') {
	            $("#isdDocty").find('option').remove().end().append("<option value =''>-- Select --</option><option value ='ISD'>ISD</option><option value ='ISDCN'>ISDCN</option><option value ='ISDUR'>ISDUR</option><option value ='ISDCNUR'>ISDCNUR</option>");
	        }
	        if ((returnType == 'GSTR1') || (returnType == 'ANX1') || (returnType == 'GSTR4') || (returnType == 'GSTR5') || (returnType == 'GSTR6') || (returnType == 'GSTR2' && type == 'B2B') || (returnType == 'Purchase Register' && type == 'B2B') || (returnType == 'GSTR2' && type == 'Credit/Debit Notes') || (returnType == 'Purchase Register' && type == 'Credit/Debit Notes')) {
	            $('#matchingId').remove();
	            $('#matchingStatus').remove();
	        }
	        $('#sortable_table').css('display', 'inline-table');
	        $(".has-error").removeClass("has-error");
	        $(".has-danger").removeClass("has-danger");
	        $('.btn_popup_amnd').hide();
	        $('.with-errors').html('');
	        $('button[type="submit"]').show();
	        if (returnType.indexOf('Amnd') > 0) {
	            returnType = returnType.replace('Amnd', '');
	        }
	        if (returnType == 'EWAYBILL' || returnType == 'GSTR1' || returnType == 'GSTR4' || returnType == 'GSTR6' || returnType == 'GSTR5' || returnType == 'ANX1' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES' || returnType == 'PurchaseOrder') {
	            addITCFlag = false;
	            $('.addITCFlag').hide();
	            addAdvFlag = false;
	            $('.addAdvFlag').hide();
	            if (type == 'Nil Supplies') {
	                $('#taxableAmount').removeClass("astrich");
	                SnilFlag = false;
	                $('.SnilFlag').hide();
	                addITCRuleFlag = true;
	                AdvanceFlag = true;
	                $('.addITCRuleFlag').show();
	                $('.AdvancesFlag').show();
	                $('.disFlag').css("display", "none");
	            } else if (type == 'Advances') {
	                SnilFlag = true;
	                AdvanceFlag = false;
	                $('.AdvancesFlag').hide();
	                $('#taxableAmount').addClass("astrich");
	            } else if (type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'Credit/Debit Notes') {
	                $('.disFlag').css("display", "none");
	                $('.nil-foot').attr("colspan", "6");
	                CreditFlag = false;
	                SnilFlag = true;
	            } else {
	                SnilFlag = true;
	                AdvanceFlag = true;
	                $('.AdvancesFlag').show();
	                $('.SnilFlag').show();
	                addITCRuleFlag = false;
	                $('.addITCRuleFlag').hide();
	                $('#taxableAmount').removeClass("astrich");
	            }
	            if (returnType == 'GSTR6') {
	                addITCFlag = true;
	                $('.addITCFlag').show();
	            }
	            if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL'|| type == 'TXPA') {
	                $('#gstin_lab').removeClass("astrich");
	                $('.customernamediv').hide();
	                $('#billedtostatecode').removeAttr('required');
	                $('.placeofsupply').hide();
	                addAdvFlag = true;
	                noadvFlag = true;
	                $('.addAdvFlag').show();
	                $('.noadvFlag').hide();
	                $('.nil-foot').attr("colspan", "6");
	                $('.invoiceNumberText').html('Adv.adjustment.No');
	                $('.invoiceDateText').html('Adv.adjustment.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                $('#Address_wrap').addClass('col-md-2');
	                $('#Address_wrap').removeClass('col-md-4');
	                $('.rateinclusive').parent().addClass('col-md-12');
	                $('.rateinclusive').parent().removeClass('col-md-6');
	                $('.rateinclusive,.invoiceLevelCess').css('margin-top', '58px');
	            } else {
	                $('.placeofsupply').show();
	                addAdvFlag = false;
	                $('.addAdvFlag').hide();
	                noadvFlag = false;
	                $('.noadvFlag').show();
	                if(type != 'Advances'){
	                	$('#customer_notes_wrap').show();	
	                }
	                $('.Terms_Conditions_wrap').show();
	                if(returnType != 'EWAYBILL'){
		                $('#billingAdressdiv').show();
		                $('#shippingAdressdiv').show();
		                $('#samebilladdressdiv').show();
		                $('.rateinclusive,.invoiceLevelCess').css('margin-top', '0px');
	            	}
	                $('.totallableltxt').html('Total');
	                $('#gstin_lab').removeClass("astrich");
	                $('.customernamediv').show();
	                $('#Address_wrap').removeClass('col-md-2');
	                $('#Address_wrap').addClass('col-md-4');
	                $('.rateinclusive').parent().removeClass('col-md-12');
	                //$('.rateinclusive').parent().addClass('col-md-6');
	                if (type == 'Credit/Debit Notes' || type == 'Nil Supplies') {
	                    $('.disFlag').hide();
	                } else if (type == 'Advances') {
	                    AdvancesFlag = false;
	                    $('.AdvancesFlag').hide();
	                }
	            }
	            if (addFlag) {
	            	if(returnType == 'GSTR1' && (type == 'Credit/Debit Notes')){
	            		$('#bank_details').css("display", "block");
	            	}else{
	            		$('#bank_details').css("display", "none");
	            	}
	                $('.addBankDetails').removeAttr("checked");	   
	                if (returnType == 'GSTR1') {
	                    if ('${otherreturnType}' == 'SalesRegister') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/savesinv/SalesRegister/${usertype}/${month}/${year}?type=");
	                    } else {
	                        $('#salesinvoceform').attr("action", "${contextPath}/savesinv/GSTR1/${usertype}/${month}/${year}?type=");
	                    }
	                } else if (returnType == 'GSTR4') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveGSTR4/GSTR4/${usertype}/${month}/${year}");
	                } else if (returnType == 'GSTR6') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveGSTR6/GSTR6/${usertype}/${month}/${year}");
	                } else if (returnType == 'GSTR5') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveGSTR5/GSTR5/${usertype}/${month}/${year}");
	                } else if (returnType == 'ANX1') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveANX1/ANX1/${usertype}/${month}/${year}");
	                } else if (returnType == 'DELIVERYCHALLANS') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savedeliverychallan/DELIVERYCHALLANS/${usertype}/${month}/${year}");
	                } else if (returnType == 'PROFORMAINVOICES') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveproformainvoice/PROFORMAINVOICES/${usertype}/${month}/${year}");
	                } else if (returnType == 'ESTIMATES') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/saveestimatesinvoice/ESTIMATES/${usertype}/${month}/${year}");
	                } else if (returnType == 'PurchaseOrder') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savepurchaseorderinvoice/PurchaseOrder/${usertype}/${month}/${year}");
	                }
	            } else {
	                if (journalsOrNotinjournals == 'journal') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savesinv/GSTR1/${usertype}/${month}/${year}?type=journal");
	                } else if (journalsOrNotinjournals == 'ledgerreport') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savesinv/GSTR1/${usertype}/${month}/${year}?type=ledgerreport");
	                } else {
	                    if (returnType == 'DELIVERYCHALLANS') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/savedeliverychallan/DELIVERYCHALLANS/${usertype}/${month}/${year}");
	                    } else if (returnType == 'PROFORMAINVOICES') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/saveproformainvoice/PROFORMAINVOICES/${usertype}/${month}/${year}");
	                    } else if (returnType == 'ESTIMATES') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/saveestimatesinvoice/ESTIMATES/${usertype}/${month}/${year}");
	                    } else if (returnType == 'PurchaseOrder') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/savepurchaseorderinvoice/PurchaseOrder/${usertype}/${month}/${year}");
	                    } else {
	                        if ('${otherreturnType}' == 'SalesRegister') {
	                            $('#salesinvoceform').attr("action", "${contextPath}/savesinv/SalesRegister/${usertype}/${month}/${year}?type=");
	                        } else {
	                            $('#salesinvoceform').attr("action", "${contains}");
	                        }
	                    }
	                }
	            }
	            if (returnType == 'GSTR6' || returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == '<%=MasterGSTConstants.PURCHASEORDER%>') {
	                $('#returnTypes,#PImportService').html('PURCHASE INVOICE');
	                $('.custName').html('Supplier Name');
	                $('#billedtoname').attr("placeholder", "Supplier Name");
	                $('#newcust').html('Please add new Supplier');
	                $('#newcustval').attr("value", "Add New Supplier");
	                $('#newcustval').attr("onclick", "updateName('billedtoname', 'custname', 'addCustomerModal','GSTR2')");
	                $('#addcust').html('ADD SUPPLIER');
	                if(returnType == 'GSTR6'){
	                	$('.bnkdetail,.bnkdetails,#bank_details,#selectBankDiv,#selectBankDiv1,#selectCBankDiv2,#custinfotext').css("display", "none");
	                }else{
	                	$('#selectBankDiv').css("display","none");
	                	$('.bnkdetail,.bnkdetails,.addBankDetails,#bank_details,#selectBankDiv1,#selectCBankDiv2,#custinfotext').css("display", "block");
	                	
	                }
	            } else if (returnType == 'EWAYBILL') {
					$('#returnTypes').html('GENERATE EWAY BILL  INVOICE');
	                $('.invoiceNumberText').html('Invoice Number');
	                $('.invoiceDateText').html('Invoice Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                $('#bank_details').css("display", "none");
	                $(".backdetails_wrap").css("display", "none");
	            } else {
	                if (addFlag) {
	                    $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber()");
	                    $('#salesinvsave').attr("onClick", "checkInvoiceNumber()");
	                } else {
	                    $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber('edit')");
	                    $('#salesinvsave').attr("onClick", "checkInvoiceNumber('edit')");
	                }
	                $('#returnTypes,#PImportService').html('ADD SALES INVOICE ');
	                $('.custName').html('Customer Name');
	                $('#billedtoname').attr("placeholder", "Customer Name");
	                $('#newcust').html('Please add new Customer');
	                $('#newcustval').attr("value", "Add New Customer");
	                $('#newcustval').attr("onclick", "updateName('billedtoname', 'custname', 'addCustomerModal','GSTR1')");
	                $('#addcust').html('ADD CUSTOMER');
	                if((returnType == 'GSTR1' || returnType == 'SalesRegister') && (type == "Credit/Debit Notes")){
	                	$('#selectBankDiv').css("display", "none");
	                }else{
	                	$('#selectBankDiv').css("display", "block");
	                }
	                $('.bnkdetail,.bnkdetails,#selectBankDiv1,#custinfotext').css("display", "block");
	            }
	            if (addFlag) {
	            	$('#addrow,#addrow1').attr("onclick", "add_row('${client.id}',\'" + returnType + "\','${usertype}')");
	            }else{
	            	if(invoiceCustomData.irnNo == null || invoiceCustomData.irnNo == ""){
		            	$('#addrow,#addrow1').attr("onclick", "add_row('${client.id}',\'" + returnType + "\','${usertype}')");
		            }
	            }
	        } else if (returnType == 'GSTR2A' || returnType == 'GSTR2' || returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'Unclaimed' || returnType == 'GSTR6' || returnType == '<%=MasterGSTConstants.PURCHASEORDER%>') {
	            $('.custName').html('Supplier Name');
	            $('#billedtoname').attr("placeholder", "Supplier Name");
	            $('#billedtoname').attr("data-error", "Please enter Supplier Name");
	            $('#newcust').html('Please add new Supplier');
	            $('#newcustval').attr("value", "Add New Supplier");
	            $('#newcustval').attr("onclick", "updateName('billedtoname', 'custname', 'addCustomerModal','GSTR2')");
	            $('#addcust').html('ADD SUPPLIER');
	            if(returnType == 'GSTR6'){
	            	$('.bnkdetail,#bank_details,#selectBankDiv,#selectBankDiv1,.bank-details-box,.bnkdetails,#selectCBankDiv2,#custinfotext').css("display", "none");
	            }else{
	            	$('#selectBankDiv').css("display","none");
	            	$('.bnkdetail,.bnkdetails,.addBankDetails,#bank_details,#selectBankDiv1,#selectCBankDiv2,#custinfotext').css("display", "block");
	            }
	            if (returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'Unclaimed' || returnType == 'GSTR6') {
	                if (type == 'Advances' || type == 'Advance Adjusted Detail' || type == 'TXPA' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'Nil Supplies') {
	                    addITCFlag = false;
	                    $('.addITCFlag').hide();
	                } else {
	                    addITCFlag = true;
	                    $('.addITCFlag').show();
	                }
	                if (type == 'Nil Supplies') {
	                    addITCRuleFlag = true;
	                    $('.addITCRuleFlag').show();
	                    if (type == 'Nil Supplies') {
	                        SnilFlag = false;
	                        $('.SnilFlag').hide();
	                    } else if (type == 'ITC Reversal') {
	                        $('.nil-foot').attr("colspan", "7");
	                    } else {
	                        $('.nil-foot').attr("colspan", "2");
	                        SnilFlag = true;
	                        $('.SnilFlag').show();
	                    }
	                } else {
	                    addITCRuleFlag = false;
	                    $('.addITCRuleFlag').hide();
	                }
	                if (type == 'Advances') {
	                    $('.disFlag').css("display", "none");
	                } else if (type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'Credit/Debit Notes') {
	                    $('.disFlag').css("display", "none");
	                    $('.nil-foot').attr("colspan", "6");
	                    CreditFlag = false;
	                }
	                $('.form-control').removeClass('disable');
	            } else {
	                $('button[type="submit"]').hide();
	                addITCFlag = false;
	                if (type == 'Nil Supplies') {
	                    addITCRuleFlag = true;
	                    $('.addITCRuleFlag').show();
	                } else {
	                    addITCRuleFlag = false;
	                    $('.addITCRuleFlag').hide();
	                }
	                $('.addITCFlag').hide();
	                $('.form-control').addClass('disable');
	            }
	            if (returnType == 'GSTR2') {
	                if (type == 'Advances' || type == 'Advance Adjusted Detail' || type == 'TXPA' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'Nil Supplies') {
	                    addITCFlag = false;
	                    $('.addITCFlag').hide();
	                } else {
	                    addITCFlag = true;
	                    $('.addITCFlag').show();
	                }
	                if (type == 'Nil Supplies') {
	                    addITCRuleFlag = true;
	                    $('.addITCRuleFlag').show();
	                    if (type == 'Nil Supplies') {
	                        SnilFlag = false;
	                        $('.SnilFlag').hide();
	                    } else {
	                        SnilFlag = true;
	                        $('.SnilFlag').show();
	                    }
	                } else {
	                    addITCRuleFlag = false;
	                    $('.addITCRuleFlag').hide();
	                }
	            }
	            addAdvFlag = false;
	            $('.addAdvFlag').hide();
	            if (type == 'Advances') {
	                addITCFlag = false;
	                $('.addITCFlag').hide();
	                $('.disFlag').css("display", "none");
	            }
	            if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	                $('#Address_wrap').addClass('col-md-2');
	                $('#Address_wrap').removeClass('col-md-4');
	                $('#billedtostatecode').removeAttr('required');
	                $('.placeofsupply').hide();
	                $('.rateinclusive').parent().removeClass('col-md-12');
	                $('.rateinclusive').parent().removeClass('col-md-6');
	                addAdvFlag = true;
	                $('.addAdvFlag').show();
	                noadvFlag = true;
	                $('.noadvFlag').hide();
	                $('.nil-foot').attr("colspan", "6");
	                $('.invoiceNumberText').html('Adv.adjustment.No');
	                $('.invoiceDateText').html('Adv.adjustment.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                $('.customernamediv').hide();
	                $('.disFlag').css("display", "none");
	                $('.rateinclusive,.invoiceLevelCess').css('margin-top', '58px');
	            } else {
	                $('#Address_wrap').removeClass('col-md-2');
	                $('#Address_wrap').addClass('col-md-4');
	                $('.rateinclusive').parent().removeClass('col-md-12');
	                $('.rateinclusive').parent().removeClass('col-md-6');
	                $('.placeofsupply').show();
	                addAdvFlag = false;
	                $('.addAdvFlag').hide();
	                noadvFlag = false;
	                $('.noadvFlag').show();
	                if(type != 'Advances'){
	                	$('#customer_notes_wrap').show();
	                }
	                $('.Terms_Conditions_wrap').show();
	                $('#billingAdressdiv').show();
	                $('#shippingAdressdiv').show();
	                $('#samebilladdressdiv').show();
	                $('.customernamediv').show();
	                $('.rateinclusive,.invoiceLevelCess').css('margin-top', '0px');
	                $('.totallableltxt').html('Total');
	                if (type == 'Advances') {
	                    AdvancesFlag = false;
	                    $('.AdvancesFlag').hide();
	                }
	            }
	            if ((type == 'Nil Supplies') && (returnType == 'GSTR1' || returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'Unclaimed')) {
	                addSnilFlag = true;
	                $('.addSnilFlag').show();
	                SnilFlag = false;
	                $('.SnilFlag').hide();
	            } else if (type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'Credit/Debit Notes') {
	                $('.disFlag').css("display", "none");
	                $('.nil-foot').attr("colspan", "6");
	                CreditFlag = false;
	            } else {
	                addSnilFlag = false;
	                $('.addSnilFlag').hide();
	                SnilFlag = true;
	                $('.SnilFlag').show();
	                if (type == 'Advances') {
	                    addITCFlag = false;
	                    $('.addITCFlag').hide();
	                    AdvanceFlag = false;
	                    $('.disFlag').css("display", "none");
	                }
	                if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	                    $('.disFlag').css("display", "none");
	                }
	            }
	            if (addFlag) {
	                if ('${otherreturnType}' == 'PurchaseRegister') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savepinv/PurchaseRegister/${usertype}/${month}/${year}?type=");
	                    $('#purchasenilinvoceform').attr("action", "${contextPath}/savepinv/PurchaseRegister/${usertype}/${month}/${year}?type=");
	                } else {
	                	if('${returntype}' == 'ANX2'){
	                		$('#salesinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=Anx2");
	                	}else{
	                		$('#salesinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=");
	                	}
	                    
	                    $('#purchasenilinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=");
	                }
	            } else {
	                if (journalsOrNotinjournals == 'journal') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=journal");
	                } else if (journalsOrNotinjournals == 'ledgerreport') {
	                    $('#salesinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=ledgerreport");
	                } else {
	                    if ('${otherreturnType}' == 'PurchaseRegister') {
	                        $('#salesinvoceform').attr("action", "${contextPath}/savepinv/PurchaseRegister/${usertype}/${month}/${year}?type=");
	                        $('#purchasenilinvoceform').attr("action", "${contextPath}/savepinv/PurchaseRegister/${usertype}/${month}/${year}?type=");
	                    } else {
	                    	if('${returntype}' == 'ANX2'){
	                    		$('#salesinvoceform').attr("action", "${contextPath}/savepinv/Purchase Register/${usertype}/${month}/${year}?type=Anx2");
		                	}else{
		                		$('#salesinvoceform').attr("action", "${contextPath}/savepinv/Purchase Register/${usertype}/${month}/${year}?type=");
		                	}
	                        $('#purchasenilinvoceform').attr("action", "${contextPath}/savepinv/GSTR2/${usertype}/${month}/${year}?type=");
	                    }
	                }
	            }
	            if (returnType == 'Purchase Register' || returnType == 'Unclaimed' || returnType == 'GSTR6') {
	                $('#returnTypes,#pnilreturntype').html('PURCHASE INVOICE');
	                $('#addrow,#addrow1').attr("onclick", "add_row('${client.id}','GSTR2','${usertype}')");
	                $('.gstr2adeletefield').addClass("fa-trash-o");
	            } else {
	                $('#addrow,#addrow1').removeAttr("onclick");
	                $('.item_edit').removeAttr("onclick");
	                $('.item_delete').removeAttr("onclick");
	                $('.gstr2adeletefield').removeClass("fa-trash-o");
	                $('#returnTypes,#pnilreturntype').html('PREVIEW INVOICE - ');
	            }
	        }
	        $('.specific_field').hide();
	        if (addFlag) {
                if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
                	$('.addBankDetails').attr("checked","true").prop('checked', true);
                }
                if((returnType == 'GSTR1' || returnType == 'SalesRegister') && (type == "Credit/Debit Notes")){
                	$('.addBankDetails').attr("checked","true").prop('checked', true);
                }
	            $('#cancelInvoice,#cancelInvoice1,#cancelInvoice2,#cancelEwayBillInvoice,#vehicleupDt').hide();
	            $('.vehicle_updtrsn').css('display', 'none');
	            if (returnType == 'EWAYBILL') {
	                $('.ewayBillNumberDiv').css("display", "none");
	                $('.ewaybillDateDiv').css("display", "none");
					$('#ewayBillDate').removeAttr("required");
	            }
	        } else {
	            if (returnType == 'GSTR1' || returnType == 'Purchase Register' || returnType == 'Unclaimed' || returnType == 'GSTR6' || returnType == 'GSTR4' || returnType == 'GSTR5' || returnType == 'ANX1') {
	            	if (addFlag) {
	            		$('#cancelInvoice').show();
		            }else{
		            	if(invoiceCustomData.irnNo == null || invoiceCustomData.irnNo == ""){
		            		$('#cancelInvoice').show();
			            }
		            }
	            	$('#cancelInvoice1,#cancelInvoice2').show();
	            } else if (returnType == 'EWAYBILL') {
	                if (ewayBillNo != '') {
	                	if(uptVehicle){
			   	    		 $('#vehicleupDt').css("display", "block");
		                    $('.vehicle_updtrsn').css("display", "block");
			   	    	}
			   	    	if(canEbill){
			   	    	 	$('#cancelEwayBillInvoice').css("display", "block");
			   	    	 }
						$('.ewayBillNumberDiv,.ewaybillDateDiv').css("display", "block");
					$('#ewayBillDate').attr("required","required");
	                }else{
							$('.ewayBillNumberDiv,.ewaybillDateDiv').css("display", "none");
							$('#ewayBillDate').removeAttr("required");
					}
	            } else {
	                $('#cancelInvoice,#cancelInvoice1,#cancelInvoice2').hide();
	            }
	        }
	        if (addFlag) {
	            $('#invoiceModal .form-control').val('');
	            $('#invoiceModal1 .form-control').val('');
	            $('#revchargetype').val('Regular');
	            $('#invTyp').val('R');
	            $('#itctype_text1').val('no');
	            $('#itcpercent_text1').val('0').attr("readonly", true);
	            $('#splyTy').val('');
	            $('#exportType').val('');
	            $('#ntty').val('C');
	            $('#pGst').val('N');
	            $('#isSez').val('Y');
	            $('#rsn').val('Sales Return');
	            $('#cdnurtyp').val('EXPWP');
	            $('#cdnurinvtyp').val('B2BUR');
	            if (type == 'Import Services') {
	                $('#revchargetype').val('Reverse').attr("readonly",true).attr("disabled","disabled");
	                $('.rateinctax').css("margin-top", "-50px");
	            } else {
	                $('#revchargetype').val('Regular').removeAttr("readonly").removeAttr("disabled");
	                $('.rateinctax').css("margin-top", "0px");
	            }
	            var invDate = null; 
	            <c:if test = "${varRetType eq varPurchase || returntype eq varGSTR2}" >
	                invDate = '<fmt:formatDate value="${invoice.dateofinvoice}" pattern="dd/MM/yyyy" />'; 
	            </c:if>
	            if (invDate == null || invDate == '') {
	                invDate = 'date+/' + month + '/' + year;
	            }
	            $('#dateofinvoice1').datetimepicker({
	                value: invDate,
	                timepicker: false,
	                format: 'd/m/Y',
	                maxDate: '0'
	            });
	            $('#dateofinvoice2').datetimepicker({
	                value: invDate,
	                timepicker: false,
	                format: 'd/m/Y',
	                maxDate: '0'
	            });
	            $('input[type="hidden"]').each(function() {
	                var name = $(this).attr('name');
	                if (name != undefined) {
	                    if (name != "items[0].disper" && name != "items[0].itemCustomField1" && name != "items[0].itemCustomField2" && name != "items[0].itemCustomField3" && name != "items[0].itemCustomField4" && name != "items[0].itemNotescomments"  && name.indexOf("items[") >= 0 && name != "items[0].itemId"  && name.indexOf("items[") >= 0) {
	                        $(this).remove();
	                    }
	                }
	                if (name == 'id') {
	                    $('#invoiceid').attr('name', 'abcd').val('');
	                    $('#invoiceid1').attr('name', 'abcd').val('');
	                    $('#invoiceid2').attr('name', 'abcd').val('');
	                }
	                if (name == 'retType') {
	                    if (returnType == 'Purchase Register' || returnType == 'GSTR2') {
	                        $('#retType').val('GSTR2');
	                    } else {
	                        $('#retType').val(returnType);
	                    }
	                }
	                if (name == 'matchingId') {
	                    $('#matchingId').val('');
	                }
	                if (name == 'matchingStatus') {
	                    $('#matchingStatus').val('');
	                }
	            });
	        } else {
	            if (returnType == 'Purchase Register' || returnType == 'PurchaseRegister' || returnType == 'GSTR2') {
	                $('#retType').val('GSTR2');
	            } else if (returnType == 'SalesRegister' || returnType == 'GSTR1') {
	                $('#retType').val(returnType);
	            }
	        }
	        $('#boeDt').datetimepicker({
	            timepicker: false,
	            format: 'd-m-Y',
	            maxDate: 0,
	            scrollMonth: true
	        });
	        if (rowCount > 1 && addFlag) {
	            $('#sortable_table tbody').find("tr:gt(0)").remove();
	            $('#sortable_table tfoot tr').not(':last').remove();
	            rowCount = 1;
	        }
	        $('#sortable_table tbody tr').removeClass("addrowshadow");
	        if (addFlag && rowCount >= 1) {
	            currRowIndex = null;
	            $('#totTaxable, #totIGST, #totCGST, #totSGST, #totCESS, #totTotal, #idTotal,#totITCCGST, #totITCCESS, #totITCSGST, #totITCIGST, #totAdvRemaining').html('0.00');
	        }
	        $('#invoiceModal').modal('show');
	        $('.specific_field').each(function() {
	            if ($(this).attr('class').indexOf(type) >= 0) {
	                $(this).show();
	                $(this).children("input").attr("required", "required");
	                $(this).children("select").attr("required", "required");
	                $('#ewayBillNumber').removeAttr("required");
	            } else {
	                $(this).children("input").removeAttr("required");
	                $(this).children("select").removeAttr("required");
	            }
	            if(addFlag){
	            	if(returnType == 'GSTR2' || returnType == 'Purchase Register'){
			        	if(type == 'B2B' || type == 'Credit/Debit Notes'){
	            			$('.printerintra').css("display","block");
			        	}
	            	}else{
	            		$('.printerintra').css("display","none");
	            	}
	            }else{
			    	if(returnType == 'GSTR2' || returnType == 'Purchase Register'){
			        	if(type == 'B2B Unregistered' || type == 'Credit/Debit Note for Unregistered Taxpayers'){
			        		$('.printerintra').css("display","block");
						}else{
							$('.printerintra').css("display","none");	
						}
					}else{
						$('.printerintra').css("display","none");	
					}
		    	}
	            $('#printerintra').removeAttr("required");
	            if (type == "Exports") {
	                $(this).children("input").removeAttr("required");
	            }
				if (type == "PROFORMAINVOICES") {
					$('#convertedtoinv').removeAttr("required");
				}
	            if ((type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR2') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR2A') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR5') || (type == 'CDNURA' && rtype == 'GSTR2') || (type == 'CDNURA' && rtype == 'GSTR5') || (type == 'Credit/Debit Notes' && rtype == 'GSTR2') || (type == 'Credit/Debit Notes' && rtype == 'GSTR5') || (type == 'CDNUR' && rtype == 'GSTR2') || (type == 'CDNUR' && rtype == 'GSTR5')) {
	            	$('#pcdnur').css("display","block");$('#cdnurinvtyp').removeAttr("readonly");$('#scdnur').css("display","none");$('#cdnurinvtyp').attr("required","required");$('#cdnurtyp').removeAttr("required");
	            	
	            	/*$('#pcdnur').css("display", "block");
	                $('#scdnur').css("display", "none");
	                $('#cdnurinvtyp').attr("required", "required");
	                $('#cdnurtyp').removeAttr("required");*/
	            } else if ((type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR1') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR1A') || (type == 'Credit/Debit Note for Unregistered Taxpayers' && rtype == 'GSTR5') || (type == 'CDNURA' && rtype == 'GSTR1') || (type == 'CDNURA' && rtype == 'GSTR5') || (type == 'Credit/Debit Notes' && rtype == 'GSTR1') || (type == 'Credit/Debit Notes' && rtype == 'GSTR5') || (type == 'CDNUR' && rtype == 'GSTR1') || (type == 'CDNUR' && rtype == 'GSTR5')) {
	                /*$('#pcdnur').css("display", "none");
	                $('#scdnur').css("display", "block");
	                $('#cdnurtyp').attr("required", "required");
	                $('#cdnurinvtyp').removeAttr("required");*/
	            	$('#pcdnur').css("display","none");$('#scdnur').css("display","block");$('.cdnurtype').addClass("astrich");$('#cdnurtyp').removeAttr("readonly");$('#cdnurtyp').attr("required","required");$('#cdnurinvtyp').removeAttr("required");
	            }
	            if (type == 'Credit/Debit Notes') {
	                if (!addFlag) {
	                	$('#pcdnur,.printerintra').css("display","none");$('.cdnurtype').removeClass("astrich");$('#cdnurtyp,#cdnurinvtyp').attr("readonly","readonly").addClass("disabled");$('#cdnurtyp').removeAttr("required");$('#cdnurinvtyp').removeAttr("required");
	                    /*$('#pcdnur').css("display", "none");
	                    $('#scdnur').css("display", "none");
	                    $('#cdnurtyp').removeAttr("required");
	                    $('#cdnurinvtyp').removeAttr("required");*/
	                    
	                }
	            }
	            if (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'CDNA' || type == 'CDNURA') {
	                $('#voucherNumber').removeAttr("required");$('#pGst').removeAttr("required");$('#voucherDate').removeAttr("required");
	            } else {
	                $('#voucherNumber').removeAttr("required");
	            }
	        });
	        if (returnType == 'GSTR1' || returnType == 'ANX1' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES' || returnType == 'PurchaseOrder' || returnType == 'EWAYBILL') {
	            $('#billDate').css("display", "none");
	            $('.billDate').removeAttr("required");
	        } else {
	        	if(type == 'Advances' || type == 'Advance Adjusted Detail' || type == 'TXPA'){
	        		$('#billDate').css("display", "none");
		            $('.billDate').removeAttr("required");
	        	}else{
		            $('#billDate').css("display", "block");
		            $('.billDate').attr("required", "required");	        			        		
	        	}
	        }
	        if (type != null) {
	            if (addFlag) {
	                updateDefaults(type, returnType);
	                $('#gstStatus').css("display", "none");
	            }
	            
	            if(type == 'Import Services'){
	            	updaterevchargeNo(type,month,year);
	            }
	            
	            if (returnType == 'GSTR1' && type == 'Nil Supplies') {
	                $('#invoiceType').html('Nil Rated / Exempted / NON GST');
	                $('#idInvType').val(type);
	            }
	            if (returnType == 'Purchase Register' && type == 'Nil Supplies') {
	                $('#invoiceType').html('(Nil-Rated / Exempted / NON-GST)');
	                $('#idInvType').val(type);
	                $('.invoiceNumberText').html('Bill No');
	                $('.invoiceDateText').html('Bill Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                $('#returnTypes').html('Bill of Supply');
	                $('#billedtogstin').removeAttr("required");
	                $('#gstin_lab').removeClass("astrich");
	                $("#ad_tax1").removeClass("astrich");
	                $("#ad_tax2").removeClass("astrich");
	                $("#ad_tax3").removeClass("astrich");
	            } else {
	                if (type == 'B2B' || type == 'B2CL' || type == 'B2C' || type == 'Exports' || type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers' || type == 'Advances' || type == 'Advance Adjusted Detail' || type == 'TXPA' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'B2B Unregistered' || type == 'Import Goods' || type == 'Import Services' || type == 'B2BA' || type == 'B2CSA' || type == 'B2CLA' || type == 'CDNURA' || type == 'ATA' || type == 'TXPA') {
	                    $('#billedtogstin').removeAttr("required");
	                    $('#gstin_lab').removeClass("astrich");
	                    $('#ecommercegstin').removeAttr("required");
	                    $('#inv_exports').removeClass("astrich");
	                    $('#inv_exports1').removeClass("astrich");
	                    if (type == 'B2C' || type == 'Exports') {
	                        $('#gstin_lab').removeClass("astrich");
	                        $('#ad_tax1').addClass("astrich");
	                        if (type == 'B2C') {
	                            $('#ad_tax1').removeClass("astrich");
	                        }
	                    } else if (((returnType == 'GSTR5' || returnType == 'GSTR1' || returnType == 'GSTR2' || returnType == 'Purchase Register') && type == 'Credit/Debit Note for Unregistered Taxpayers') || (type == 'Advances' || type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA')) {
	                        $('#gstin_lab').removeClass("astrich");
	                    } else {
	                        $('#gstin_lab').removeClass("astrich");
	                        $('#ad_tax2').addClass("astrich");
	                        $('#ad_tax3').addClass("astrich");
	                        $('#ad_tax1').addClass("astrich");
	                    }
	                    if (returnType == 'GSTR1' && (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers')) {
	                        $('#reasonForIssue').css("display", "none");
	                        $('#rsn').removeAttr("required");
	                        $("#ad_tax2").removeClass("astrich");
	                    } else if (returnType == 'GSTR2' || returnType == 'Purchase Register' && (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers')) {
	                        $("#ad_tax2").removeClass("astrich");
	                    }
	                } else if ((returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'GSTR1' || returnType == 'GSTR4' || returnType == 'GSTR5' || returnType == 'GSTR6') && (type == '<%=MasterGSTConstants.CREDIT_DEBIT_NOTES%>' || type == '<%=MasterGSTConstants.CDNA%>')) {
	                    $('#IsSEZ').removeClass("astrich");
	                    $('#stin_id').removeClass("astrich");
	                } else if (((returnType == 'GSTR2' || returnType == 'Purchase Register') && type == 'ITC Reversal') || (returnType == 'GSTR4' && type == 'B2B Unregistered') || ((returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'GSTR6') && type == 'ISD')) {
	                    $('#gstin_lab').addClass("astrich");
	                    $('#billedtogstin').attr("required", "required");
	                    if (returnType == 'GSTR6' && type == 'ISD') {
	                        $("#ad_tax1").removeClass("astrich");
	                        $("#ad_tax2").removeClass("astrich");
	                        $("#ad_tax3").removeClass("astrich");
	                        $("#rate_as").removeClass("astrich");
	                        $("#itc_elg").removeClass("astrich");
	                        $("#itc_per").removeClass("astrich");
	                    }
	                } else if ((returnType == 'GSTR2' || returnType == 'Purchase Register') && type == 'Nil Supplies') {
	                    $('#gstin_lab').removeClass("astrich");
	                    $("#ad_tax1").removeClass("astrich");
	                    $("#ad_tax2").removeClass("astrich");
	                    $("#ad_tax3").removeClass("astrich");
	                } else if (returnType == 'GSTR5' && type == 'B2B') {
	                    $('#inv_exports').removeClass("astrich");
	                    $('#inv_exports1').removeClass("astrich");
	                    $('#billedtogstin').attr("required", "required");
	                } else {
	                    $('#billedtogstin').attr("required", "required");
	                    if (returnType == 'GSTR1' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES' || returnType == 'PurchaseOrder') {
	                        $('#billedtogstin').removeAttr("required");
	                    }
	                    if (type != 'Nil Supplies') {
	                        $('#ad_tax2').addClass("astrich");
	                        $('#ad_tax3').addClass("astrich");
	                        $('#ad_tax1').addClass("astrich");
	                    } else {
	                        $('#billedtogstin').removeAttr("required");
	                        $('#gstin_lab').removeClass("astrich");
	                    }
	                    $('#inv_exports').removeClass("astrich");
	                    $('#inv_exports1').removeClass("astrich");
	                }
	                $('#idInvType').val(type);
	                if ((type == 'B2B' || type == 'B2C' || type == 'B2BA' || type == 'B2CL' || type == 'B2CLA' || type == 'B2CSA') && returnType != 'GSTR5') {
	                    if (returnType == 'GSTR1' || returnType == 'ANX1') {
	                        type = "(B2B/B2CL/B2C)";
	                    } else {
	                        type = "";
	                    }
	                    $('#inv_exports').addClass("astrich");
	                    $('#inv_exports1').addClass("astrich");
	                }
	                if (type == 'B2B' && returnType == 'GSTR5') {
	                    type = "Business to Business(" + type + ")/Business to Consumers Large";
	                }
	                if (type == 'B2C') {
	                    type = "Business to Consumers Small(" + type + ")";
	                }
	                if (type == 'B2CL') {
	                    type = "Business to Consumers Large(" + type + ")";
	                }
	                if (type == 'B2B Unregistered') {
	                    type = "";
	                }
	                if (type == 'Advances') {
	                    $('#returnTypes').html('');
	                        $('.ledgerddbox1').addClass('adv_ledger');
	                    if (returnType == 'GSTR1') {
	                        $('#returnTypes').html('Advance Receipts');
	                        type = "(Receipt voucher)";
	                    } else if (returnType == 'GSTR2' || returnType == 'GSTR2A' || returnType == 'Purchase Register' || returnType == 'Unclaimed') {
	                        type = "Advance Payments";
	                    }
	                }
	                if (type == 'Nil Supplies') {
	                	$('.ledgerddbox1').addClass('nil_ledger');
	                    $('#returnTypes').html('Bill of Supply ');
	                    type = "(Nil-Rated/ Exempted / NON-GST)";
	                }
	                if (type == 'Import Goods') {
	                    $('#returnTypes').html('');
	                    type = "Import Goods(Bill of Entry)";
	                }
	                if (type == 'Import Services') {
	                    $('#returnTypes').html('');
	                    type = "Import Services";
	                    $('#shippingAdressdiv').css('display', 'none');
	                }
	                if (type == '(Receipt voucher)') {
	                    $('.invoiceNumberText').html('Advance Receipt No.');
	                    $('.invoiceDateText').html('Adv.Receipt Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    $('.advancetax').html('Advance Inclusive tax');
	                    $('#ad_tax4').html("Adv.amount");
	                } else if (type == "Advance Payments") {
	                    $('.invoiceNumberText').html('Advance Payment No.');
	                    $('.invoiceDateText').html('Adv.Payment Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    $('.invoiceDateText').css("width","max-content");
	                    $('.advancetax').html('Advance Inclusive tax');
	                    $('#ad_tax4').html("Adv.amount");
	                } else if (type == 'Credit/Debit Notes') {
	                    if (addFlag) {
	                        $('.invoiceNumberText').html('Credit Note.No');
	                        $('.invoiceDateText').html('Credit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    }
	                } else if (type == 'Credit/Debit Note for Unregistered Taxpayers') {
	                    $('.disFlag').hide();
	                    if (addFlag) {
	                        $('.invoiceNumberText').html('Credit Note.No');
	                        $('.invoiceDateText').html('Credit Note Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    }
	                } else if (type == "(Nil-Rated/ Exempted / NON-GST)") {
	                    $('.invoiceNumberText').html('Bill No');
	                    $('.invoiceDateText').html('Bill Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                } else if (type == 'Import Goods(Bill of Entry)') {
	                    $('#serialnoofinvoice').attr("pattern", "^[0-9]+(.[0-9]{1,10})?$");
	                    $('#serialnoofinvoice').attr("maxlength", "7");
	                    $('#serialnoofinvoice').attr("data-error", "Please Enter Numbers Only");
	                    $('.invoiceNumberText').html(' Bill of Entry Number');
	                    $('.invoiceDateText').html('Bill Of Entry Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                } else {
	                    $('.invoiceNumberText').html('Invoice Number');
	                    $('.invoiceDateText').html('Invoice Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    if('${pconfig.rateText}' == null || '${pconfig.rateText}' == ''){
	                    	$('#ad_tax4').html("Rate");	
	                    }else{
	                    	$('#ad_tax4').html('${pconfig.rateText}');	
	                    }
	                }
	                if (type == 'Credit/Debit Notes' || type == 'Credit/Debit Note for Unregistered Taxpayers') {
	                    $('#returnTypes').html('');
	                    type = "Credit/Debit Notes";
	                }
	                if (type == 'Exports') {
	                    $('#returnTypes').html('');
	                    type = "ADD EXPORT INVOICE";
	                }
	                if (type == 'Advance Adjusted Detail' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	                	 $('.ledgerddbox1').addClass('advadj_ledger');
	                    $('#returnTypes').html('');
	                    type = "ADVANCE ADJUSTED DETAIL";
	                    $('.particular_type').hide();
	                    $('.all-oth').show();
	                    $('.tfoot-taxr').show();
	                    $('.invoiceNumberText').html('Adv.adjustment.No');
	                    $('.invoiceDateText').html('Adv.adjustment.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    //$('.totallableltxt').html('Adv. Remaining');
	                    $('.totallableltxt').html('Total');
	                    $('#billingAdressdiv').css('display', 'none');
	                    $('#shippingAdressdiv').css('display', 'none');
	                    $('#samebilladdressdiv').css('display', 'none');
	                    $('.bnkdetail,#bank_details').css('display', 'none');
	                    $('#selectBankDiv').css('display', 'none');
	                    $('#customer_notes_wrap').css('display', 'none');
	                    $('.Terms_Conditions_wrap').css('display', 'none');
	                }
	                if (type == 'ISD') {
	                    $('#returnTypes').html('');
	                    type = "ISD INVOICE";
	                    $('#isdbillingAddress').attr("name", "b2b[0].inv[0].address");
	                    $('#billingAddress').removeAttr("name");
	                    $('.bnkdetail,#bank_details').css('display', 'none');
	                    $('#selectBankDiv').css('display', 'none');
	                } else {
	                    $('#billingAddress').attr("name", "b2b[0].inv[0].address");
	                    $('#isdbillingAddress').removeAttr("name");
	                }
	                if (type == 'ITC Reversal') {
	                    $('#returnTypes').html('');
	                    type = "ITC REVERSAL INVOICE";
	                    $('.bnkdetail,#bank_details').css('display', 'none');
	                    $('#selectBankDiv').css('display', 'none');
	                }
	                if (type == 'DELIVERYCHALLANS') {
	                    $('#returnTypes').html('');
	                    type = "DELIVERY CHALLAN";
	                }
	                if (type == 'ESTIMATES') {
	                    $('#returnTypes').html('');
	                    type = "ESTIMATE";
	                }
	                if (type == 'PROFORMAINVOICES') {
	                    $('#returnTypes').html('');
	                    type = "PROFORMA INVOICE";
	                }
	                if (type == 'PurchaseOrder') {
	                    $('#returnTypes').html('');
	                    type = "PURCHASE ORDER";
	                }
	                $('#invoiceType').html(type);
	            }
	        } else {
	            $('#invoiceType').html('(Business to Business)B2B/Business to Consumers Large');
	            $('#idInvType').val('');
	            if (addFlag) {
	                updateDefaults('ALL', returnType);
	                $('#gstStatus').css("display", "none");
	            }
	        }
	        if (!addFlag) {
	            var bacctno, bname;
	            bacctno = $('#bankAcctNo').val();
	            bname = $('#bankName').val();
	            updateBankDetails(type, bacctno, bname);
	            updateRateTypeedit('<c:out value="${client.statename}"/>', returnType);
	            
	            if (type == 'Import Goods(Bill of Entry)' && returnType == 'Purchase Register') {
	                if ($('#isSez').val() == 'Y') {
	                    $('#impGoodsgstin').css('display', 'block');
	                    $('#stin').attr("required", "required");
	                } else if ($('#isSez').val() == 'N') {
	                    $('#impGoodsgstin').css('display', 'none');
	                    $('#stin').removeAttr("required");
	                }
	            } else {
	                $('#impGoodsgstin').css('display', 'none');
	                $('#stin').removeAttr("required");
	            }
	            if (returnType == 'EWAYBILL') {
					 if (ewayBillNo != '') {
						   if(uptVehicle){
				   	    		$('#vehicleupDt').show();
			                    $('.vehicle_updtrsn').css("display", "block");
				   	    	}
				   	    	if(canEbill){
				   	    	 	$('#cancelEwayBillInvoice').show();
				   	    	 }
	                    /* $('#cancelEwayBillInvoice,#vehicleupDt').show();
	                    $('.vehicle_updtrsn').css('display', 'block'); */
						$('.ewayBillNumberDiv,.ewaybillDateDiv').css("display", "block");
					$('#ewayBillDate').attr("required","required");
	                }else{
							$('.ewayBillNumberDiv,.ewaybillDateDiv').css("display", "none");
							$('#ewayBillDate').removeAttr("required");
					}
	            }
	        }
	        if (addFlag) {
	            var notess = $("#invnotes").text();
				notess = notess.replaceAll("#mgst# ", "\r\n");
	            var terms_cond = $("#invterms").text();
				terms_cond = terms_cond.replaceAll("#mgst# ", "\r\n");
	            if (returnType == 'EWAYBILL') {
	                $('.ewayBillNumberDiv,.ewaybillDateDiv').css("display", "none");
					$('#ewayBillDate').removeAttr("required");
	            }
	            if (type != 'Advance Adjusted Detail' || type != 'TXPA') {
	                $('#salesinvoceform').find("textarea[id='bankNotes']").val(notess);
	            } else {
	                $('#salesinvoceform').find("textarea[id='bankNotesadv']").val(notess);
	            }
	            $('#salesinvoceform').find("textarea[id='bankTerms']").val(terms_cond);
	            bankAndVertical();
	        }
	        if (returnType == 'GSTR2A' || returnType == 'GSTR2' || gstStatus == '<%=MasterGSTConstants.STATUS_SUBMITTED%>' || gstStatus == '<%=MasterGSTConstants.STATUS_FILED%>') {
	            $('.form-control').addClass('disable');
	            $('#addrow,.addsnilrow').attr('disabled', true).css("background-color", "white");
	            $('#addrow1,.addsnilrow1').empty();
	            if (gstStatus == '<%=MasterGSTConstants.STATUS_SUBMITTED%>' || gstStatus == '<%=MasterGSTConstants.STATUS_FILED%>') {
	                $('.btn_popup_amnd').show();
	                $('.btn_popup_cancel').hide();
	                $('.btn_popup_save').hide();
	                $.each($("#allinvoice tr"), function() {
	                    $(this).removeAttr('onClick');
	                })
	                $('.gstr2adeletefield').css("display", "none");
	            }
	        } else {
	        	 if (addFlag) {
	        		 $('.gstr2adeletefield').css("display", "block");
	 	             $('.form-control,#addrow').removeClass('disable');
	 	             $('#addrow1,#impSeraddrow1').html('<i class="add-btn">+</i> Add another row');
	 	             $('#addrow,#impSeraddrow').removeAttr('disabled').css("background-color", "");
		         }else{
		           	if(invoiceCustomData.irnNo == null || invoiceCustomData.irnNo == ""){
		          		$('.gstr2adeletefield').css("display", "block");
				        $('.form-control,#addrow').removeClass('disable');
				        $('#addrow1,#impSeraddrow1').html('<i class="add-btn">+</i> Add another row');
				        $('#addrow,#impSeraddrow').removeAttr('disabled').css("background-color", "");
			        }
		         }
	        }
	        $('#igstnnumber_Msg').text('');
	        if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'GSTR2A' || returnType == 'Unclaimed') {
	            if (addFlag) {
	                $('#salesinvoceform').find("input[id='billedtostatecode']").val('<c:out value="${client.statename}"/>');
	            }
	            if (addFlag) {
	                if (returnType == 'GSTR2A' || returnType == 'GSTR2' || returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'Unclaimed' || returnType == 'GSTR6' || returnType == '<%=MasterGSTConstants.PURCHASEORDER%>') {
	                    if (type == 'B2B' || type == '' || type == 'Credit/Debit Notes') {
	                        $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber()");
	                        $('#salesinvsave').attr("onClick", "checkInvoiceNumber()");
	                    } else {
	                        $('#serialnoofinvoice,#dateofinvoice,#salesinvsave').removeAttr("onChange");
	                    }
	                } else {
	                    $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber()");
	                    $('#salesinvsave').attr("onClick", "checkInvoiceNumber()");
	                }
	            } else {
	                if (returnType == 'GSTR2A' || returnType == 'GSTR2' || returnType == '<%=MasterGSTConstants.PURCHASE_REGISTER%>' || returnType == 'Unclaimed' || returnType == 'GSTR6' || returnType == '<%=MasterGSTConstants.PURCHASEORDER%>') {
	                    if (type == 'B2B' || type == '' || type == 'Credit/Debit Notes') {
	                        $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber('edit')");
	                        $('#salesinvsave').attr("onClick", "checkInvoiceNumber('edit')");
	                    } else {
	                        $('#serialnoofinvoice,#dateofinvoice,#salesinvsave').removeAttr("onChange");
	                    }
	                } else {
	                    $('#serialnoofinvoice,#dateofinvoice').attr("onChange", "checkInvoiceNumber('edit')");
	                    $('#salesinvsave').attr("onClick", "checkInvoiceNumber('edit')");
	                }
	            }
	        } else {
	            if (addFlag) {
	                $('#salesinvoceform').find("input[id='billedtostatecode']").val('');
	            }
	            if (type == 'ADD EXPORT INVOICE') {
	                $('.bstatecode').val('97-Other Territory');
	                $('.bstatecode').attr("readonly", "readonly");
	            } else {
	                $('.bstatecode').removeAttr("readonly");
	            }
	        }
	        $('#ewayBillNumber').removeAttr("required");
	        if ((returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') && type == 'Import Services') {
	            $('#revchargetype').val('Reverse').attr("readonly",true).attr("disabled","disabled");
	            $('#billingAdressdiv').addClass('offset-md-6 mb-0');
	            //$('.rateinclusive').parent().addClass('offset-md-6');
	            $('#samebilladdressdiv').hide();
	            $('#shippingAdressdiv').hide();
	           
	        } else {
	            $('.rateinclusive').css("top", "0px");
	            $('#billingAdressdiv').removeClass('offset-md-6 mb-0');
	            $('.rateinclusive').parent().removeClass('offset-md-6');
	        }
	        if (!addITCFlag) {
	            if ($('td').hasClass('addITCFlag')) {
	                $('.addITCFlag').children().removeAttr("required");
	            }
	        }
	        if (!addITCRuleFlag) {
	            if ($('td').hasClass('addITCRuleFlag')) {
	                $('.addITCRuleFlag').children().removeAttr("required");
	            }
	        }
	        if (!addAdvFlag) {
	            if ($('td').hasClass('addAdvFlag')) {
	                $('.addAdvFlag').children().removeAttr("required");
	                $('#advrcno_text1').removeAttr("required");
	            }
	        }
	        if (type == '(Nil-Rated/ Exempted / NON-GST)' || type == 'Nil Supplies') {
	            if (!SnilFlag) {
	                document.getElementById("rate_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
	                document.getElementById("qty_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
	                if ($('td').hasClass('SnilFlag')) {
	                    $('.SnilFlag').children().removeAttr("required");
	                    $('.taxrateval').removeAttr("required");
	                }
	            }
	        }
	        if (type == '(Receipt voucher)' || type == 'Advance Payments') {
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	        }
	        if (type == 'Credit/Debit Notes') {
	            $('.disFlag').hide();
	            $('#uqc_text1').removeAttr("required");
	            $('.ledgerddbox1').addClass('crddr_ledger');
	        }
	        if (type == 'ADD EXPORT INVOICE') {
	            $('#uqc_text1').attr("required", true);
	            $('#ad_tax2').addClass('astrich');
	            $('.ledgerddbox1').addClass('exp_ledger');
	        }
	        if (type != 'ADVANCE ADJUSTED DETAIL' || type != 'TXPA') {
	            $('.advrcposval').attr("required", false);
	        }
	        if (type == 'ADVANCE ADJUSTED DETAIL' || type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	            $('#hsn_text1').attr("required", false);
	        }
	        if (type == 'ITC REVERSAL INVOICE') {
	            $('#itctype_text1').attr("required", false);
	            $('#itcpercent_text1').attr("required", false);
	            $('#cessrate_text1').attr("required", false);
	            $("#customer_notes_wrap").css('display','none');
	        	$("#customer_notes_wrap").hide();
	        }
	        if (type == 'ISD INVOICE') {
	            $('#itctype_text1').attr("required", false);
	            $('#itcpercent_text1').attr("required", false);
	            $('#taxrate_text1').attr("required", false);
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	            $('#hsn_text1').attr("required", false);
	            $('#cessrate_text1').attr("required", false);
	            $('.is-itc-no').hide();
	            $('.is-itc-yes').show();
	            $('#taxableamount_text1, #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1 , #total_text1').removeAttr('name');
	        }
	        if (type == 'ISD INVOICE') {
	            $('#isdtotTaxable').show();
	            $('.isdcess_amount').show();
	            $('.particuler_type').show();
	            $('.nil-foot').attr("colspan", "2");
	            $('#billname').show();
	            $('#Address_wrap').hide();
	            $('.customernamediv').removeClass('col-md-8');
	            $('.customernamediv').addClass('col-md-12');
	            /* $('.ISD').removeClass('col-md-3');
	            $('.ISD').addClass('col-md-2'); */
	            $('#billingAdressdiv').addClass('offset-md-6 mb-0');
	            $('#shippingAdressdiv').hide();
	            $('#rateinclusivediv').hide();
	            $('#samebilladdressdiv').hide();
	            
	            $('.Terms_Conditions_wrap').hide();
	            $('.tax_type').addClass("astrich");
	            $('#diffpercent').css("display", "none");
	            $('#taxableamount_text1 , #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1').removeAttr('readonly');
	            $('#taxableamount_text1 , #taxableamount_text2 , #taxableamount_text3 , #taxableamount_text4 , #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1, #isdcess_text1').parent().removeClass('tablegreybg');
	            $('.invoiceNumberText').html('Document Number');
	            $('.invoiceDateText').html('Document Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	            $('#pos').hide();
	            $("#addrow,#addrow1").hide();
	            $('#taxableamount_text1 , #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1').attr("pattern", "^[0-9]+(\.[0-9]{1,2})?$");
	            $('tbody#allinvoice tr td:last-child').css('display', 'none');
	            $('#particular_row1').html('<span>Eligible - Credit distributed<input type="text" id="particular_val1" name="items[0].isdType" value="Eligible - Credit distributed" style="display:none;"/></span>');
	            $('#totisdcess').css("display", "block");
	        } else {
	            $('.advtext').css("display", "table-cell");
	            $('.row_foot').css("display", "table-cell");
	            $(".particuler_type").hide();
	            $(".isdcess_amount ").hide();
	            $('.tax_type').removeClass("astrich");
	            $('#diffpercent').css("display", "block");
	            $('#totisdcess').css("display", "none");
	            $('tbody#allinvoice tr td:last-child').css('display', 'table-cell');
	            $('.particuler_type').hide();
	            $('#taxableamount_text1 , #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1').attr('readonly', 'readonly');
	            $("#addrow,#addrow1").show();
	            $('.Terms_Conditions_wrap').show();
	            $('#billname').show();
	            $('#Address_wrap').show();
	            $('.customernamediv').addClass('col-md-8');
	            $('.customernamediv').removeClass('col-md-12');
	            /* $('.ISD').addClass('col-md-3');
	            $('.ISD').removeClass('col-md-2'); */
	            if (type != 'ITC REVERSAL INVOICE') {
	                $('.isdcessamt').hide();
	                $('.addbutton').css("display", "table-cell");
	            }
	            if (type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	                $('#Address_wrap').addClass('col-md-2');
	                $('#Address_wrap').removeClass('col-md-4');
	                $('#billedtostatecode').removeAttr('required');
	                $('.placeofsupply').hide();
	                $('.rateinclusive').parent().removeClass('col-md-12');
	                $('.rateinclusive').parent().removeClass('col-md-6');
	                addAdvFlag = true;
	                $('.addAdvFlag').show();
	                noadvFlag = true;
	                $('.noadvFlag').hide();
	                $('.nil-foot').attr("colspan", "6");
	                $('.invoiceNumberText').html('Adv.adjustment.No');
	                $('.invoiceDateText').html('Adv.adjustment.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                $('.customernamediv').hide();
	                $('.disFlag').css("display", "none");
	                $('.billingAdressdiv').hide();
	                $('.Terms_Conditions_wrap').hide();
	                $('#Address_wrap').hide();
	                $('.rateinclusive,.invoiceLevelCess').css('margin-top', '58px');
	            }
	        }
	        if (type == 'ITC REVERSAL INVOICE') {
	        	$("#customer_notes_wrap").css('display','none');
	            $('#particuler_type').html('Rules');
	            $('.noitc').hide();
	            $('.is-itc-no').hide();
	            if (addFlag) {
	                $('#allinvoice1').append('<tr id="5" class="item_edit5 isdrows"><td id="sno_row1" align="center">5</td><td id="particular_row5" class="form-group noadvFlag particuler_type isdtab" style="display:none"><label>Amount in terms of rule 42(2)(a)</label><input type="text"  id="particular_val5" name="items[4].itcRevtype" value="rule7_2_b" style="display:none;"/></td><td id="isdigsttax_row5" class="form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype5" id="isdigsttax_text5" name="items[4].igstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row5" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype5" id="isdcgsttax_text5" name="items[4].cgstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row5" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype5" id="isdsgsttax_text5" name="items[4].sgstamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount itctab" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text5" name="items[4].isdcessamount" onkeyup="findIsdTaxAmount(5)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row5" class="tablegreybg form-group itctab" align="right"><input type="text" class="form-control" id="isdtotal_text5" name="items[4].total" readonly="readonly"></td></tr><tr id="6" class="rowshadow item_edit6 isdrows"><td id="sno_row1" align="center">6</td><td id="particular_row6" class="form-group noadvFlag particuler_type isdtab" style="display:none"><label>On account of amount paid subsequent to reversal of ITC</label><input type="text"  id="particular_val6" name="items[5].itcRevtype" value="revitc" style="display:none;"/></td><td id="igsttax_row6" class="form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype6 text-right" id="isdigsttax_text6" name="items[5].igstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row6" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype6 text-right" id="isdcgsttax_text6" name="items[5].cgstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row6" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype6 text-right" id="isdsgsttax_text6" name="items[5].sgstamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount itctab" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text6" name="items[5].isdcessamount" onkeyup="findIsdTaxAmount(6)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row6" class="tablegreybg form-group itctab" align="right"><input type="text" class="form-control" id="isdtotal_text6" name="items[5].total" readonly="readonly"></td></tr><tr id="7" class="rowshadow item_edit7 isdrows"><td id="sno_row1" align="center">7</td><td id="particular_row7" class="form-group noadvFlag particuler_type itctab" style="display:none"><label>Any other liability (Pl specify)</label><input type="text"  id="particular_val7" name="items[6].itcRevtype" value="other" style="display:none;"/></td><td id="igsttax_row7" class="form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype7 text-right" id="isdigsttax_text7" name="items[6].igstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="cgsttax_row7" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype7 text-right" id="isdcgsttax_text7" name="items[6].cgstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="sgsttax_row7" class=" form-group SnilFlag itctab" align="right"><input type="text" class="form-control isdtype7 text-right" id="isdsgsttax_text7" name="items[6].sgstamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="" class="form-group noadvFlag isdcess_amount itctab" align="right"><input type="text" class="form-control noadvFlag isdcess_amount text-right" id="isdisdcess_text7" name="items[6].isdcessamount" onkeyup="findIsdTaxAmount(7)" onkeypress="return (event.charCode >= 48 && event.charCode <= 57 || event.charCode == 46 || event.charCode == 0)" pattern="^[0-9]+(\.[0-9]{1,2})?$"></td><td id="total_row7" class="tablegreybg form-group itctab" align="right"><input type="text" class="form-control text-right" id="isdtotal_text7" name="items[6].total" readonly="readonly"></td></tr>');
	                $('#igsttax_text1 , #cgsttax_text1 , #sgsttax_text1 , #total_text1').removeAttr('name');
	            }
	        }
	        if (type == 'ITC REVERSAL INVOICE') {
	        	$("#customer_notes_wrap").css('display','none');
	            noisd = true;
	            $('.noisd').hide();
	            addITCFlag = false;
	            $('.addITCFlag').hide();
	            $('#taxrate_text1').attr("required", false);
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	            $('#hsn_text1').attr("required", false);
	            $('.isdcess_amount').show();
	            $('#totTaxable').css("display", "none");
	            $('.particuler_type').show();
	            $('.nil-foot').attr("colspan", "2");
	            $('.isdrows').show();
	            $('#Address_wrap').hide();
	            $('#billingAdressdiv').addClass('offset-md-6 mb-0');
	            $('#shippingAdressdiv').hide();
	            $('#rateinclusivediv').hide();
	            $('#samebilladdressdiv').hide();
	           /*  $('.customernamediv').removeClass('col-md-8');
	            $('#billname').removeClass('col-md-6');
	            $('#billname').addClass('col-md-4');
	            $('.ISD').removeClass('col-md-3');
	            $('.ISD').addClass('col-md-2'); */
	            $('.Terms_Conditions_wrap').hide();
	            $('#particular_row1').html('<span>Amount in terms of rule 37(2)<input type="text" id="particular_val1" name="items[0].itcRevtype" value="rule2_2" style="display:none;"/></span>');
	            $('#particular_row2').html('<span>Amount in terms of rule 42(1)(m)<input type="text" id="particular_val2" name="items[1].itcRevtype" value="rule7_1_m" style="display:none;"/></span>');
	            $('#particular_row3').html('<span>Amount in terms of rule 23(1)(h)<input type="text" id="particular_val3" name="items[2].itcRevtype" value="rule8_1_h" style="display:none;"/></span>');
	            $('#particular_row4').html('<span>Amount in terms of rule 42(2)(b)<input type="text" id="particular_val4" name="items[3].itcRevtype" value="rule7_2_a" style="display:none;"/></span>');
	        } else {
	            if (type != 'ISD INVOICE') {
	                $('.advtext').css("display", "table-cell");
	                $('.row_foot').css("display", "table-cell");
	                $('.addbutton').css("display", "table-cell");
	                $('#totTaxable').css("display", "block");
	                $('.tax_type').removeClass("astrich");
	                $('#diffpercent').css("display", "block");
	                $('#totisdcess').css("display", "none");
	                $('tbody#allinvoice tr td:last-child').css('display', 'table-cell');
	                $('.particuler_type').hide();
	                $('.isdcess_amount').hide();
	                $('#taxableamount_text1 , #igsttax_text1 , #cgsttax_text1 , #sgsttax_text1').attr('readonly', 'readonly');
	                $("#addrow,#addrow1").show();
	                $('.Terms_Conditions_wrap').show();
	                $('#billname').show();
	                $('#Address_wrap').show();
	                $('.customernamediv').addClass('col-md-8');
	                $('.customernamediv').removeClass('col-md-12');
	               /*  $('.ISD').addClass('col-md-3');
	                $('.ISD').removeClass('col-md-2'); */
	                if (type == 'ADVANCE ADJUSTED DETAIL' || type == 'TXPA') {
	                    $('.advtext').css("display", "table-cell");
	                    /* $('#Address_wrap').addClass('col-md-2');
	                    $('#Address_wrap').removeClass('col-md-4'); */
	                    $('#billedtostatecode').removeAttr('required');
	                    $('.placeofsupply').hide();
	                    $('.rateinclusive').parent().removeClass('col-md-12');
	                    $('.rateinclusive').parent().removeClass('col-md-6');
	                    addAdvFlag = true;
	                    $('.addAdvFlag').show();
	                    noadvFlag = true;
	                    $('.noadvFlag').hide();
	                    $('.nil-foot').attr("colspan", "6");
	                    $('.invoiceNumberText').html('Adv.adjustment.No');
	                    $('.invoiceDateText').html('Adv.adjustment.Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	                    $('.customernamediv').hide();
	                    $('.disFlag').css("display", "none");
	                    $('.billingAdressdiv').hide();
	                    $('.Terms_Conditions_wrap').hide();
	                    $('#Address_wrap').hide();
	                    $('.rateinclusive,.invoiceLevelCess').css('margin-top', '58px');
	                }
	            } else {
	                $('.noitc').show();
	                $('.isdcess_amount').show();
	                $('#taxableamount_text1, taxableamount_text2, taxableamount_text3, taxableamount_text4, taxableamount_text5, taxableamount_text6, taxableamount_text7').parent().css("display", "table-cell");
	            }
	        }
	        if ($('#isdisdcess_text1,#isdisdcess_text2,#isdisdcess_text3,#isdisdcess_text4,#isdisdcess_text4,#isdisdcess_text6,#isdisdcess_text7').val() == '') {
	            $('#isdisdcess_text1 ,#isdisdcess_text2,#isdisdcess_text3,#isdisdcess_text4,#isdisdcess_text5,#isdisdcess_text6,#isdisdcess_text7').val(0);
	        }
	        if (returnType == 'GSTR2' || returnType == 'Purchase Register') {
	            if (type == 'Credit/Debit Notes') {
	                $('.disFlag').hide();
	            } else if (type == 'Nil Supplies') {
	                $('.row_foot').css("display", "none");
	                $('.nil-foot').attr("colspan", "6");
	            } else if (type == 'B2B Unregistered' || type == 'B2B') {
	                $('.advtext').css("display", "table-cell");
	                $('.row_foot').css("display", "table-cell");
	            }
	        } else {
	            $('.advtext').css("display", "table-cell");
	            $('.row_foot').css("display", "table-cell");
	        }
	        if (returnType == 'GSTR1' && (type == '(Nil-Rated/ Exempted / NON-GST)' || type == 'Nil Supplies')) {
	            $('.row_foot').css("display", "none");
	            $('.nil-foot').attr("colspan", "7");
	            document.getElementById("exempted_text1").setAttribute("onkeyup", "findNillTaxableValue(1)");
	        }
	        if (type == 'B2B Unregistered' || type == 'B2B') {
	            $('.particuler_type').hide();
	            $('.isdcess_amount').hide();
	            $('.row_foot').css("display", "table-cell");
	        }
	        if (type == 'ISD INVOICE' || type == 'ITC REVERSAL INVOICE') {
	            $('#roundoffdiv').addClass("col-md-4");
	            $('#roundoffdiv').removeClass("col-md-2");
	            $('#allinvoice tr td').find("input:text,select").removeAttr('name');
	            $('.is-itc-no').hide();
	            $('.is-itc-yes').show();
	            $('.termsDueDate,.customer_notes_wrap').hide();
	            $('#entds,.tdsDiv').hide();
	            var tablerows = $('#allinvoice1 tr').length;
	            for (var i = 0; i < tablerows + 1; i++) {
	                $('#allinvoice1 tr').find('td #isdtaxableamount_text' + i).each(function() {
	                    $('#isdtaxableamount_text' + i).attr('name', 'items[' + (i - 1) + '].taxablevalue');
	                });
	                $('#allinvoice1 tr').find('td #isdigsttax_text' + i).each(function() {
	                    $('#isdigsttax_text' + i).attr('name', 'items[' + (i - 1) + '].igstamount');
	                });
	                $('#allinvoice1 tr').find('td #isdcgsttax_text' + i).each(function() {
	                    $('#isdcgsttax_text' + i).attr('name', 'items[' + (i - 1) + '].cgstamount');
	                });
	                $('#allinvoice1 tr').find('td #isdsgsttax_text' + i).each(function() {
	                    $('#isdsgsttax_text' + i).attr('name', 'items[' + (i - 1) + '].sgstamount');
	                });
	                $('#allinvoice1 tr').find('td #isdisdcess_text' + i).each(function() {
	                    $('#isdisdcess_text' + i).attr('name', 'items[' + (i - 1) + '].isdcessamount');
	                });
	                $('#allinvoice1 tr').find('td #isdtotal_text' + i).each(function() {
	                    $('#isdtotal_text' + i).attr('name', 'items[' + (i - 1) + '].total');
	                });
	            }
	            $("#allinvoice tr td").find("input").attr('name', "")
	        } else {
	        	 if(revChargeNo != "" && revChargeNo != "undefined"){
	        		 if(addFlag){
	        			 if(type != 'Import Services'){
	        			 	$('#roundoffdiv').addClass("col-md-2 col-sm-12");
	        			 }else{
	        				 $('#roundoffdiv').css("display","none");
	        			 }
	        		 }else{
	  	 		   			$('#roundoffdiv').removeClass("col-md-2 col-sm-12").css("display","none");
	        		 }
	  	 	   }else{
		  	 		if(type == 'Import Services'){
	      	 			$('#roundoffdiv').css("display","none");
	      	 		}
	  	 			$('#roundoffdiv').addClass("col-md-2 col-sm-12");
	  	 	   }
	            //$('#roundoffdiv').addClass("col-md-2");
	            $('#roundoffdiv').removeClass("col-md-4");
	            $('#product_text1').attr('name', 'items[0].itemno');
	            $('#hsn_text1').attr('name', 'items[0].hsn');
	            $('#uqc_text1').attr('name', 'items[0].uqc');
	            $('#qty_text1').attr('name', 'items[0].quantity');
	            $('#rate_text1').attr('name', 'items[0].rateperitem');
	            $('#discount_text1').attr('name', 'items[0].discount');
	            $('#taxableamount_text1').attr('name', 'items[0].taxablevalue');
	            $('#taxrate_text1').attr('name', 'items[0].rate');
	            $('#abb1').attr('name', 'items[0].totaltaxamount');
	            $('#cessrate_text1').attr('name', 'items[0].cessrate');
	            $('#cessamount_text1').attr('name', 'items[0].cessamount');
	            $('#itctype_text1').attr('name', 'items[0].elg');
	            $('#itcpercent_text1').attr('name', 'items[0].elgpercent');
	            $('#total_text1').attr('name', 'items[0].total');
	            $('#allinvoice1 tr td').find("input:text,select").removeAttr('name');
	            $('.is-itc-no').show();
	            $('.is-itc-yes').hide();
	        }
	        if (returnType == 'GSTR1' && (type != 'ADVANCE ADJUSTED DETAIL' && type != 'Advance Adjusted Detail' && type != 'TXPA' && type != '(Receipt voucher)')) {
	            if (type == '(Nil-Rated/ Exempted / NON-GST)') {
	                $('.exemp_td').show();
	                $('.nil-foot').attr('colspan', '7');
	            } else if (type == 'Credit/Debit Notes') {
	                $('.exemp_td').show();
	                $('.nil-foot').attr('colspan', '7');
	            } else {
	                $('.exemp_td').show();
	                $('.nil-foot').attr('colspan', '8');
	            }
	        } else {
	            $('.exemp_td').hide();
	        }
	        if (type == 'ADD EXPORT INVOICE' || type == '(Nil-Rated/ Exempted / NON-GST)' || type == 'Import Goods(Bill of Entry)' || type == 'Nil Supplies' || type == 'DELIVERY CHALLAN' || type == 'PROFORMA INVOICE' || type == 'ESTIMATE' || type == 'PurchaseOrder' || type == 'PURCHASE ORDER') {
	            $('#roundoffdiv').addClass("col-md-4");
	            $('#roundoffdiv').removeClass("col-md-2");
	        } else {
	            if (type != 'ISD INVOICE' && type != 'ITC REVERSAL INVOICE') {
	            	 if(revChargeNo != "" && revChargeNo != "undefined"){
	            		 if(addFlag){
	            			 if(type != 'Import Services'){
		        			 	$('#roundoffdiv').addClass("col-md-2 col-sm-12");
	            			 }else{
	            				 $('#roundoffdiv').css("display","none");
	            			 }
		        		 }else{
		  	 		   			$('#roundoffdiv').removeClass("col-md-2 col-sm-12").css("display","none");
		        		 }
	      	 	   }else{
	      	 		if(type == 'Import Services'){
	      	 			$('#roundoffdiv').css("display","none");
	      	 		}
	      	 		$('#roundoffdiv').addClass("col-md-2 col-sm-12");
	      	 	   }
	               // $('#roundoffdiv').addClass("col-md-2");
	                $('#roundoffdiv').removeClass("col-md-4");
	            }
	        }
	        if (type == 'ISD' || type == 'ISD INVOICE' || type == 'ITC REVERSAL INVOICE' || type == 'ITC Reversal' || type == 'DELIVERY CHALLAN' || type == 'PROFORMA INVOICE' || type == 'ESTIMATE' || type == 'PurchaseOrder' || type == 'PURCHASE ORDER') {
	            $('#tcsvaldiv,#section,#Tcs_percent,.gstr2TcsDiv,#entdsortcs').css("display", "none");
	            $('#entdsortcs').removeClass('col-md-12');$('#entdsortcs').addClass('col-md-6'); 
	        } else {
	            $('#tcsvaldiv,#section,#Tcs_percent,.gstr2TcsDiv,#entdsortcs').css("display", "block");
	          
	            // $('#entdsortcs').removeClass('col-md-6');$('#entdsortcs').addClass('col-md-12');
	        }
	        if (returnType == 'GSTR1' || returnType == 'SalesRegister') {
	            if (otherconfigdetails.enableSalesFields == true) {
	                $('.hsn_text').removeAttr("required");
	                $('.uqcDetails ').removeAttr("required");
	                $('.qtyval').removeAttr("required");
	                $('#ad_tax1').removeClass("astrich");
	                $('#ad_tax2').removeClass("astrich");
	                $('#ad_tax3').removeClass("astrich");
	            } else {
	                $('.hsn_text').attr("required", true);
	                if (type != '(Receipt voucher)' && type != 'Advance Payments') {
	                    $('.uqcDetails ').attr("required", true);
	                    $('.qtyval').attr("required", true);
	                }
	                $('#ad_tax1').addClass("astrich");
	                $('#ad_tax2').addClass("astrich");
	                $('#ad_tax3').addClass("astrich");
	            }
	        }
	        if (type != 'ISD INVOICE' && type != 'ITC REVERSAL INVOICE' && type != 'ADVANCE ADJUSTED DETAIL' && type != 'TXPA') {
	            if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
	                if (otherconfigdetails.enablePurFields == true) {
	                    $('.hsn_text').removeAttr("required");
	                    $('.uqcDetails ').removeAttr("required");
	                    $('.qtyval').removeAttr("required");
	                    $('#ad_tax1').removeClass("astrich");
	                    $('#ad_tax2').removeClass("astrich");
	                    $('#ad_tax3').removeClass("astrich");
	                } else {
	                    $('.hsn_text').attr("required", true);
	                    if (type != '(Receipt voucher)' && type != 'Advance Payments') {
	                        $('.uqcDetails ').attr("required", true);
	                        $('.qtyval').attr("required", true);
	                    }
	                    $('#ad_tax1').addClass("astrich");
	                    $('#ad_tax2').addClass("astrich");
	                    $('#ad_tax3').addClass("astrich");
	                }
	            }
	        }
	        if (returnType == 'EWAYBILL') {
	        	$('.nil-foot').attr("colspan","8");
	        	if(genEbill){
	        		 $('#ewayBillSave_btn').show();
	        	}
	            $('#draft_btn').show();
	            $('#savebtn').attr("onclick", "saveEwayBIllInv('EWAYBILL')");
	            $('#billedtogstin').attr("required", true);
	            $('#gstin_lab').addClass('astrich');
	            $('#ad_tax2,#ad_tax3,#tax_rate').removeClass('astrich');
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	            $('#taxrate_text1').attr("required", false);
	            $('#voucherDate').attr('required', false);
	            $('#invTyp').attr('required', false);
	            $('#save_btn').css('display', 'none');
	            $('.invoiceNumberText').html('Invoice Number');
	            $('.invoiceDateText').html('Invoice Date<span class="dateddlable" >(DD/MM/YYYY)</span>');
	            $('.no_vehicle,#customer_notes_wrap,.Terms_Conditions_wrap').css('display', 'none');
	            $('.eCommerceDiv').css('display', 'none');
	            $('.invTypeDiv').css('display', 'none');
	           // $('.ledgerDiv').css('display', 'none');
	        } else {
	           // $('#save_btn').css('display', 'block');
	        	if(gstStatus == "CANCELLED" || gstStatus == "Cancelled"){
					$('#save_btn').css('display', 'none');
				}else{
					$('#save_btn').css('display', 'block');
				}
	            $('.no_vehicle').css('display', 'flex');
	            if (type != 'ADVANCE ADJUSTED DETAIL' || type != 'TXPA') {
	                if (type != 'ITC REVERSAL INVOICE' && type != 'ISD INVOICE') {
	                    $('.Terms_Conditions_wrap').css('display', 'block');
	                }
	                if (type == 'ITC REVERSAL INVOICE' || type == 'Advances' || type == '(Receipt voucher)' || type == 'Advance Payments') {
	                	$('#customer_notes_wrap').css('display', 'none');
	                }else{
	                	$('#customer_notes_wrap').css('display', 'block');
	                }
	            }
	        }
	        if (returnType == 'GSTR1' || returnType == 'SalesRegister') {
            if (otherconfigdetails.enableLedgerSalesField == true || otherconfigdetails.enableLedgerSalesField == "" || otherconfigdetails.enableLedgerSalesField == undefined) {
	               // $('#ledgerName').removeAttr("required");
	            } else {
	                if (type != 'ADVANCE ADJUSTED DETAIL') {
	                   // $('#ledgerName').attr("required", true);
	                }
	            }
	            $('form[name="salesinvoceform"]').validator('update');
	        }
	        if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
	        	if(type == ""){
	        		$('.ledgerddbox1').addClass('b2b_ledger');
	        	}else if(type == 'Credit/Debit Notes'){
	        		$('.ledgerddbox1').addClass('crdr_ledger');$('.ledgerddbox1').removeClass('crddr_ledger');
	        	}else if(type == 'Import Goods(Bill of Entry)'){
	        		$('.ledgerddbox1').addClass('impg_ledger');
	        	}else if(type == 'Import Services'){
	        		$('.ledgerddbox1').addClass('imps_ledger');
	        	}else if(type == 'Nil Supplies'){
	        		$('.ledgerddbox1').addClass('nil1_ledger');
	        	}
            if (otherconfigdetails.enableLedgerPurField == true || otherconfigdetails.enableLedgerPurField == "" || otherconfigdetails.enableLedgerPurField == undefined) {
	               // $('#ledgerName').removeAttr("required");
	            } else {
	                if (type != 'ADVANCE ADJUSTED DETAIL') {
	                   // $('#ledgerName').attr("required", true);
	                }
	            }
	        }
	        if(addFlag){
		        if(otherconfigdetails.enableCessQty == true){
		        	$('#cess_qty').attr("checked", true);
		        	$('#cess_taxable').removeAttr("checked");
		        	if ($('#cessrate_text1').val() == '') {
			            document.getElementById('cessrate_text1').value = "0";
			            document.getElementById('cessamount_text1').value = "0.00";
			        }
		        }else{
		        	$('#cess_taxable').attr("checked", true);
		        	$('#cess_qty').removeAttr("checked");
		        	if ($('#cessrate_text1').val() == '') {
			            document.getElementById('cessrate_text1').value = "0%";
			            document.getElementById('cessamount_text1').value = "0.00";
			        }
		        }
	        }
	        if (returnType == 'GSTR1' || returnType == 'SalesRegister' || returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
	           
	            	if (type != 'ISD INVOICE' && type != 'ITC REVERSAL INVOICE') {
	            		$('.tcsConfigDiv').removeClass('col-md-4 col-sm-12');
	            		$('#tcsvaldiv,#section,#Tcs_percent,.gstr2TcsDiv,#entdsortcs').css("display","block");
	            		 //$('#entdsortcs').addClass('col-md-12');$('#entdsortcs').removeClass('col-md-6'); 
	            	}
	        }
		        if(returnType == 'GSTR1' || returnType == 'SalesRegister' || returnType == 'DELIVERYCHALLANS' || returnType == 'PROFORMAINVOICES' || returnType == 'ESTIMATES'){
		        	if(returnType == 'GSTR1' || returnType == 'SalesRegister'){
		 		       if(type != "(Receipt voucher)"){
		 		    	   $('.Terms,.duedate').removeClass("d-none");
		 		       } 
		 		    }
		 		       if(enableSalesCess == true || enableSalesCess == "true"){
		 		    	  if(type != "(Nil-Rated/ Exempted / NON-GST)"){
		 		    		 if(invoiceLevel_Cess == "" || invoiceLevel_Cess == 'No' || invoiceLevel_Cess == null || invoiceLevel_Cess == undefined){
			 		    			$('.cessFlag').hide();
			 		    	 		$('#cessrate_text1').removeAttr("required");
			 		    	 		$('#invoiceLevelCess').val('No');
			 		    	  }else{
		 					 	$('.cessFlag').show();
		 					 	$('#invoiceLevelCess').prop("checked",true).val("Yes");
			 		    	  }
		 				 }
		 		       }else{
		 		    	   if(invoiceLevel_Cess == "" || invoiceLevel_Cess == 'No' || invoiceLevel_Cess == null || invoiceLevel_Cess == undefined){
		 		    			$('.cessFlag').hide();
		 		    	 		$('#cessrate_text1').removeAttr("required");
		 		    	 		$('#invoiceLevelCess').val('No');
		 		    	   }
		 		       }
		        	if(enableDiscount == true || enableDiscount == "true"){
			        	if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA' && type != "(Nil-Rated/ Exempted / NON-GST)" && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers"){
			        		$('.disFlag').show();
			        	}
		        	}else{
			        	$('.disFlag').hide();
			        	if(type != "(Receipt voucher)" && type != "Advances" &&  type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA'){
			        		$('.nil-foot').attr("colspan","8");
			        	}
		            }
	        		if(enableLedger == true || enableLedger == "true"){
	        			$('.item_ledger').show();
	        			$('.nil-foot').attr("colspan","7");
	        		}else{
	        			$('.item_ledger').hide();
	        		}
	        	if(enableExempted == true || enableExempted == "true"){
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA'){
	        			$('.exemp_td').show();	
	        		}	
	        	}else{
	        		$('.exemp_td').hide();	
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA'){
	        			$('.nil-foot').attr("colspan","6");
	        		}
	        	}
	        	if((enableLedger == true || enableLedger == "true")){
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL"  && type != 'TXPA' && type != "(Nil-Rated/ Exempted / NON-GST)"){
		        		$('.nil-foot').attr("colspan","6");
		        	}else{
		        		if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "(Nil-Rated/ Exempted / NON-GST)" || type == "ADVANCE ADJUSTED DETAIL" || type == 'TXPA'){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
		        	}
	        	}
	        	if((enableDiscount == true || enableDiscount == "true")){
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA' &&  type != "(Nil-Rated/ Exempted / NON-GST)" && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers"){
		        		$('.nil-foot').attr("colspan","7");
		        	}else{
		        		if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "(Nil-Rated/ Exempted / NON-GST)" || type == "ADVANCE ADJUSTED DETAIL" || type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
		        	}
	        	}
	        	if((enableExempted == true || enableExempted == "true")){
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA'){
		        		$('.nil-foot').attr("colspan","7");
		        	}else{
		        		if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL" || type == 'TXPA'){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
		        	}
	        	}
	        	
	        	if(((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true"))){
	        		if(type != "Credit/Debit Notes" && type != "(Nil-Rated/ Exempted / NON-GST)" && type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA' && type != "Credit/Debit Note for Unregistered Taxpayers"){
	        			$('.nil-foot').attr("colspan","8");
	        		}else{
	        			if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL" || type == 'TXPA'){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
	        		}
	        	}
	        	
	        	if(((enableDiscount == true || enableDiscount == "true") && (enableLedger == true || enableLedger == "true"))){
	        		if(type != "(Nil-Rated/ Exempted / NON-GST)" && type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != 'TXPA' && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers"){
	        			$('.nil-foot').attr("colspan","7");
	        		}else{
	        			
	        			if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL" || type == 'TXPA' || type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers"  || type == "(Nil-Rated/ Exempted / NON-GST)"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
	        		}
	        	}
	        	if(((enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true"))){
	        		if(type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL"){
	        			$('.nil-foot').attr("colspan","7");
	        		}else{
	        			
	        			if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL" || type == "(Nil-Rated/ Exempted / NON-GST)"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
	        		}
	        	}
	        	if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true")){
	        		if(type != "Credit/Debit Notes" && type != "(Nil-Rated/ Exempted / NON-GST)" && type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != "Credit/Debit Note for Unregistered Taxpayers"){
	        			$('.nil-foot').attr("colspan","8");
	        		}else{
	        			if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else if(type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers" || type == "(Nil-Rated/ Exempted / NON-GST)"){
		        			$('.nil-foot').attr("colspan","7");
		        		}else{
		        			$('.nil-foot').attr("colspan","8");
		        		}
	        		}
	        	}
	        	if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true")  && (enableSalesCess == true || enableSalesCess == "true")){
	        		if(type != "Credit/Debit Notes" && type != "(Nil-Rated/ Exempted / NON-GST)" && type != "(Receipt voucher)" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != "Credit/Debit Note for Unregistered Taxpayers"){
	        			$('.nil-foot').attr("colspan","8");
	        		}else{
	        			if(type == "Advances" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else if(type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers" || type == "(Nil-Rated/ Exempted / NON-GST)"){
		        			$('.nil-foot').attr("colspan","7");
		        		}else{
		        			$('.nil-foot').attr("colspan","8");
		        		}
	        		}
	        	}
	        }else if(returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister' || returnType == 'PurchaseOrder'){
	        	if(enablePurDiscount == true || enablePurDiscount == "true"){
	        		if(type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers" && type != "Advance Payments" && type != "ADVANCE ADJUSTED DETAIL" && type != "Nil Supplies" && type != "ISD INVOICE" && type != "ITC REVERSAL INVOICE"){
	        			$('.disFlag').show();
	        		}
	        	}else{
	        		$('.disFlag').hide();
	        		if(type != "Advance Payments" && type != "ISD INVOICE" && type != "ITC REVERSAL INVOICE"){
	        			$('.nil-foot').attr("colspan","6");
	        		}
	        	}
	        	if(enablePurLedger == true || enablePurLedger == "true"){
        			$('.item_ledger').show();
        			$('.nil-foot').attr("colspan","7");
        		}else{
        			$('.item_ledger').hide();
        		}
	        	 if(enablePurCess == true || enablePurCess == "true"){
	        		 if(type != "Nil Supplies"){
	 		    	  $('.cessFlag').show();
	 		    	 $('#invoiceLevelCess').prop("checked",true);
	        		 }
	 		       }else{
	 		    	  if(invoiceLevel_Cess == "" || invoiceLevel_Cess == 'No' || invoiceLevel_Cess == null || invoiceLevel_Cess == undefined){
	 		    	  	$('.cessFlag').hide();
	 		    	  }
	 		       }
	        	if((enablePurDiscount == true || enablePurDiscount == "true")){
	        		if(type != "Advance Payments" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != "Nil Supplies" && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers" && type != "ISD INVOICE" && type != "ITC REVERSAL INVOICE"){
		        		$('.nil-foot').attr("colspan","7");
		        	}else{
		        		if(type == "Advance Payments" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "Nil Supplies" || type == "ADVANCE ADJUSTED DETAIL" || type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else if(type == "ISD INVOICE" || type == "ITC REVERSAL INVOICE"){
		        			$('.nil-foot').attr("colspan","2");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
		        	}
	        	}
	        	if((enablePurLedger == true || enablePurLedger == "true")){
	        		if(type != "Advance Payments" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != "Nil Supplies" && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers" && type != "ISD INVOICE" && type != "ITC REVERSAL INVOICE"){
		        		$('.nil-foot').attr("colspan","6");
		        	}else{
		        		if(type == "Advance Payments" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "Nil Supplies" || type == "ADVANCE ADJUSTED DETAIL" || type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else if(type == "ISD INVOICE" || type == "ITC REVERSAL INVOICE"){
		        			$('.nil-foot').attr("colspan","2");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
		        	}
	        	}
	        	
	        	if(((enablePurDiscount == true || enablePurDiscount == "true") && (enablePurLedger == true || enablePurLedger == "true"))){
	        		if(type != "Nil Supplies" && type != "Advance Payments" && type != "Advances" && type != "ADVANCE ADJUSTED DETAIL" && type != "Credit/Debit Notes" && type != "Credit/Debit Note for Unregistered Taxpayers"  && type != "ISD INVOICE" && type != "ITC REVERSAL INVOICE"){
	        			$('.nil-foot').attr("colspan","7");
	        		}else{
	        			if(type == "Advance Payments" || type == "(Receipt voucher)"){
		        			$('.nil-foot').attr("colspan","4");
		        		}else if(type == "ADVANCE ADJUSTED DETAIL" || type == "Credit/Debit Notes" || type == "Credit/Debit Note for Unregistered Taxpayers"  || type == "Nil Supplies"){
		        			$('.nil-foot').attr("colspan","6");
		        		}else if(type == "ISD INVOICE" || type == "ITC REVERSAL INVOICE"){
		        			$('.nil-foot').attr("colspan","2");
		        		}else{
		        			$('.nil-foot').attr("colspan","7");
		        		}
	        		}
	        	}
	        	
	        }
	        if (returnType != 'EWAYBILL') {
	            $('#ewayBillSave_btn,#draft_btn,#cancelEwayBillInvoice,#vehicleupDt').hide();
	            $('#savebtn').removeAttr("onclick", "saveEwayBIllInv('EWAYBILL')");
	            //$('.ledgerDiv').css('display', 'block');
	            $('#ewayBillDate').removeAttr("name", "eBillDate");
	            $('#ewayBillDate,#supplyType,#subSupplyType,#docType,#fromGstin,#fromPincode,#actFromStateCode,#fromStateCode,#toGstin,#toPincode,#actToStateCode,#toStateCode,#transDistance,#transactionType,#vehicleNo1,#transMode1,#reasonRem1,#subsupplyType').attr('required', false);
	        }
	        setTimeout(function() {
	            initializePopupAddr();
	        }, 2000);
	        if (type == 'ADVANCE ADJUSTED DETAIL') {
	            $('#uqc_text1').attr("required", false);
	            $('#qty_text1').attr("required", false);
	            $('#hsn_text1').attr("required", false);
	            $('.termsDueDate,.customer_notes_wrap').hide();
	            $('#customer_notes_wrap').addClass("pt-0");
	        }else{
	        	 $('#customer_notes_wrap').removeClass("pt-0");
	        }
	        if(type == '(Receipt voucher)' || type == 'Advance Payments'){
	        	taxableOrNontaxableChange();
	        	$('.termsDueDate,#billingAdressdiv,#shippingAdressdiv,#samebilladdressdiv').hide();
	        	$('.invoiceLevelCess').css("margin-top","58px");
	        }
	        if(returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister'){
	        	if (type != 'ISD INVOICE' && type != 'ITC REVERSAL INVOICE') {
	        		$('.gstr2TcsDiv').css("display","block");
	        		$('#entds,.tdsDiv').show();
	        		//$('#entdsortcs').addClass('col-md-12');$('#entdsortcs').removeClass('col-md-6'); 
	        	}
	        }else{
	        	$('#entds,.tdsDiv').hide();
	        	$('.gstr2TcsDiv').css("display","none");
	        	$('#entdsortcs').removeClass('col-md-12');$('#entdsortcs').addClass('col-md-6'); 
	        }
	        if(returnType == 'GSTR1' || returnType == 'SalesRegister'){
						if((enableDiscount == true || enableDiscount == "true") && (enableExempted == true || enableExempted == "true") && (enableLedger == true || enableLedger == "true")  && (enableSalesCess == true || enableSalesCess == "true")){
							$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","91%");
						}else{
							$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","93%");
						}
	        }else if (returnType == 'GSTR2' || returnType == 'Purchase Register' || returnType == 'PurchaseRegister') {
	        		$("table#sortable_table td:nth-child(2) input[type=text]").css("min-width","91%");
	        	
	        }
			var invoicetype = $('#idInvType').val();
			if(addFlag && invoicetype == 'B2B'){
				$('.negativevalues').attr("onkeypress","return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)");
					$('.negativevalues').attr("pattern","^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
			}
				if (type == 'Nil Supplies' || type == '(Nil-Rated/ Exempted / NON-GST)' || type == '(Receipt voucher)' || invoicetype == 'B2C') {
					$('.negativevalues').attr("onkeypress","return (event.charCode >= 45 && event.charCode <= 57 || event.charCode == 0)");
					$('.negativevalues').attr("pattern","^([-,0-9][0-9]*(.[0-9]+)?)|([0]{1})?(([1-9]*)?((.[0]*)?[1-9]+))$");
				}
				  $("#add_currencyCode").attr("required",false);
				  if(type == "ADD EXPORT INVOICE"){
						$('.addExportFlag').show();
					}else{
						$('.addExportFlag').hide();
					}
				  if(returnType == 'EWAYBILL'){
				  	$('.btn_popup_save').hide();
				  }
				  if(returnType != 'EWAYBILL'){
					   if (type == 'Nil Supplies' || type == '(Nil-Rated/ Exempted / NON-GST)'){
						  $('.invoiceLevelCessDiv').hide();
						  $('.disFlag').show();
						  $('.nil-foot').attr('colspan','7');
						  if (type == '(Nil-Rated/ Exempted / NON-GST)'){
							  if(enableExempted == true || enableExempted == "true"){
								  $('.nil-foot').attr('colspan','8');
							  }
						  }
					  }else{
						  if (type != 'ISD INVOICE' && type != 'ITC REVERSAL INVOICE' && returnType != 'EWAYBILL') {
						  	$('.invoiceLevelCessDiv').show(); 
						  }
					  }
				  } 
	    }
	function updateName(sourceId, destId, modalId,type) {
		$('.errormsg').css('display','block').html('');	$('#invoiceModal').css("z-index","1033");$('#'+modalId).modal('show');$('#groupstabinv').addClass('active');$('#groups1tabinv').removeClass('active');$('#sgroupstabinv').removeClass('active');
		var today = new Date();var yr = today.getFullYear();$('.openText').text(yr);
		if(type == 'GSTR1'){$("#customerBankDetails").css("display","inline-block");$("#supplierBankDetails,#supplier_additional_details,#supplier_faxno").css("display","none");$('#customerorsuppliertype').val('GSTR1');$('#ccustomerLedgerName').val($('#'+sourceId).val());$('#customer_type').removeClass('d-none');$('#supplier_type').addClass('d-none');$('#ccustomer_type').removeClass('d-none');$('#ssupplier_type').addClass('d-none');$('.ssupplier_type').removeClass('d-none');$('#customerForm').attr('action','${contextPath}/cp_createcustomer/${id}/${fullname}/${usertype}/${month}/${year}');
		$.ajax({
				url: contextPath+'/bnkdtls'+urlSuffixs,
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(bankDetails) {
					customerClientBankDetailss = new Array();
						$("#selectCustomerBanks").append($("<option></option>").attr("value","").text("-- Select Bank --"));
						for (var i=0; i<bankDetails.length; i++) {
							$("#selectCustomerBanks").append($("<option></option>").attr("value",bankDetails[i].accountnumber).text(bankDetails[i].bankname));
							customerClientBankDetailss.push(bankDetails[i]);
						}
				}
			});
		}else{
			$("#customerBankDetails").css("display","none");$("#supplierBankDetails").css("display","flex");$("#supplier_additional_details,#supplier_faxno").css("display","block");$('#customerorsuppliertype').val('GSTR2');$('#ssupplierLedgerName').val($('#'+sourceId).val());$('#customer_type').addClass('d-none');$('#supplier_type').removeClass('d-none');$('#ccustomer_type').addClass('d-none');$('#ssupplier_type').removeClass('d-none');$('#customerForm').attr('action','${contextPath}/cp_createsupplier/${id}/${fullname}/${usertype}/${month}/${year}');
		}
		$('.addCustomerBankDetailss').prop('checked',false);$("#selectCBankDiv2").css("display","none");$("#selectCBankDiv3").css("display","none");$('#selectCustomerBanks').val('');$('#customerBankNames').val('');$('#customerBankAcctNos').val('');$('#customerBankBranchs').val('');$('#customerBankIFSCs').val('');$('#customerBankAccountNames').val('');$('#'+destId).val($('#'+sourceId).val());$("#notrequireds").css('display','block');
		if($('#billedtogstin').val() != ""){
			$('#gstnnumber').val($('#billedtogstin').val()).trigger("change");
		}
		$('#custaddress').val($('#billingAddress').val());
		$.ajax({
			url: "${contextPath}/countrieslist",
			contentType: 'application/json',
			success : function(response) {for(var i = 0; i < response.length; i++) {$('#country').append("<option value="+response[i].name+">"+response[i].name+"</option>");}}
		});
	}
	function addLedger(no,type,modalId){
		$('.errormsg').css('display','block').html('');	
		$('#invoiceModal').css("z-index","1033");
		$('#'+modalId).modal('show').css("z-index","9999");
		$('#addgoslname').val('');
		$('#addlpath').html('');
		$('#ledger_rowno').val(no);
		if(type == 'ledger'){
			$('#addledgername').val($('#ledgerName').val());
		}else if(type == 'voucher'){
			$('#addledgername').val($('#voucherledger_text'+no).val());
		}else if(type == 'contra'){
			$('#addledgername').val($('#contraledger_text'+no).val());
		}
	}
	
	function addItemLedger(no){
		var sdfsd = '${client.journalEnteringDate}';
		if(sdfsd != "" && sdfsd != "undefined"){
			var date = new Date(sdfsd.replace('IST', ''));
			var day = date.getDate() + "";var month = (date.getMonth() + 1) + "";	var year = date.getFullYear() + "";
			day = checkZero(day);month = checkZero(month);year = checkZero(year);
			var cdndate = day + "/" + month + "/" + year;
			$('#addledgerOpeningBalanceMonth').html(cdndate);
		}
		$('.errormsg').css('display','block').html('');	
		$('#invoiceModal').css("z-index","1033");
		$('#addLedgerModal').modal('show').css("z-index","9999");
		$('#addgoslname').val('');
		$('#addlpath').html('');
		$('#ledger_rowno').val(no);
		$('#addledgername').val($('#ledger'+no).val());
	}
	
	function invokeInvoicePublicAPI(btn) {
		var gstnno = $("#gstnnumber").val();updateCustomerPan(gstnno);
		var userid = '${id}';
		if(gstnno != '') {
			var gstnumber = gstnno.toUpperCase();
			$(btn).addClass('btn-loader');
			$.ajax({
				url: "${contextPath}/publicsearch?gstin="+gstnumber+"&userid="+userid,
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {
					if(response.error && response.error.message) {
						if(response.error.message == 'SWEB_9035'){$('#gstnnumber_Msg').text("No Records Found");	
					  	} else{$('#gstnnumber_Msg').text(response.error.message);}
					}else if(response.status_cd == '0') {$('#igstnnumber_Msg').text(response.status_desc);}
					$(btn).removeClass('btn-loader');
					if(response.status_cd == '1') {
						if(response.data) {
							var address = "";
							if(response.data['tradeNam'] == '' || response.data['tradeNam'] == null){$('#custname').val(response.data['lgnm']);
							}else{$('#custname').val(response.data['tradeNam']);}
							Object.keys(response.data).forEach(function(key) {
							if(key == 'pradr'){
							Object.keys(response.data['pradr']['addr']).forEach(function(key){
								if(response.data['pradr']['addr'][key] != ''){
									if(key != 'pncd' && key != 'stcd'){address = address.concat(response.data['pradr']['addr'][key]+",");}
									if(key == 'pncd'){$('#pincode').val(response.data['pradr']['addr'][key]);}
									if(key == 'stcd'){$('#city').val(response.data['pradr']['addr'][key]);}
								}
							});
						}
						});
						$('#custaddress').val(address.slice(0,-1));
						}
					}
				},
				error : function(e, status, error) {$(btn).removeClass('btn-loader');}
			});
		}
	}
	function updateCustomerPan(value) {
		if(value.length == 15) {
			$('#customerpannumber').val(value.substring(2,12));	$('.pan_num .with-errors').html('');$('.pan_num').removeClass('has-error has-danger');
			$.ajax({
				url: "${contextPath}/srchstatecd?code="+value.substring(0,2),
				async: false,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(response) {if(response) {$('#state').val(response.name);}}
			});
		}
	}
</script>
<style>#billedtogstin,.uqcDetails {text-transform: uppercase;}</style>
<script type="text/javascript">
	$(function(){
		  var clientgstno = '<c:out value="${client.gstnnumber}"/>';
		  $.ajax({
				url: '${contextPath}/currencycodes',
				async: true,
				cache: false,
				dataType:"json",
				contentType: 'application/json',
				success : function(currencyCodes) {
					$("#add_currencyCode").append($("<option></option>").attr("value","").text("-- Select Currency --")); 
					for (var i=0; i<currencyCodes.length; i++) {
						$("#add_currencyCode").append($("<option></option>").attr("value",currencyCodes[i].code).text(currencyCodes[i].code));
					}
					
				}
			});
		$("#billedtogstin").keyup(function() {
			var regex = /^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Zz]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$/;			
			if($("#billedtogstin").val().trim().length>14) {
				if(!regex.test($("#billedtogstin").val())){$("#invokegstnPublicAPI1").addClass("disable");
				}else{$("#invokegstnPublicAPI1").removeClass("disable");}
			}else {$("#invokegstnPublicAPI1").addClass("disable");}
			var rettype = $('#retType').val();
			if(rettype != "EWAYBILL"){
				if(clientgstno == $("#billedtogstin").val()){$('.clientgstno').text("clientgstno should not allowed");}
				else{$('.clientgstno').text('')}
			}
		});
		
		$("#transporterId").keyup(function() {
			var regex = /^([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Zz]{1}[0-9a-zA-Z]{1})|([0-9]{4}[A-Z]{3}[0-9]{5}[UO]{1}[N][A-Z0-9]{1})|([0-9]{2}[a-zA-Z]{4}[0-9]{5}[a-zA-Z]{1}[0-9]{1}[Z]{1}[0-9]{1})|([0-9]{4}[a-zA-Z]{3}[0-9]{5}[N][R][0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{4}[a-zA-Z0-9]{1}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[D]{1}[0-9a-zA-Z]{1})|([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[C]{1}[0-9a-zA-Z]{1})|([9][9][0-9]{2}[a-zA-Z]{3}[0-9]{5}[O][S][0-9a-zA-Z]{1})$/;			
			if($("#transporterId").val().trim().length>14) {
				if(!regex.test($("#transporterId").val())){$("#invokeTransgstnPublicAPI1").addClass("disable");
				}else{$("#invokeTransgstnPublicAPI1").removeClass("disable");}
			}else {$("#invokeTransgstnPublicAPI1").addClass("disable");}
		});
		
	});
	function notifi_disp1(){$(".popup_error").css("display","none")}
	function paymentterm(){
		var val = $('#termDays').val();
		var invdt = $('#dateofinvoice').val();
		var mnyr = invdt.split("/");var dt = parseInt(mnyr[0]);var mn = parseInt(mnyr[1]);var yr = parseInt(mnyr[2]);
		var d = new Date(yr, mn-parseInt(1), dt);
		if(val == ''){
			$('#duedate_div').val("");
			$('#duedate_div').datetimepicker({
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}else{
			d.setDate(d.getDate() + parseInt(val));
			var day = d.getDate();
			var month = d.getMonth()+parseInt(1);
			var year=d.getFullYear();
			var mndate = ('0' + day).slice(-2) + '/' + ('0' + (month)).slice(-2) + '/' + year;
			$('#duedate_div').datetimepicker({
				value: mndate,
				timepicker: false,
				format: 'd/m/Y',
				maxDate: false,
				minDate: new Date(yr, mn-parseInt(1), dt)
			});
		}			
	}
	function duedatechange(){
		var invdate = $('#dateofinvoice').val();var billdate = $('#duedate_div').val();
		var rxDatePattern = /^(\d{1,2})(\/|-)(\d{1,2})(\/|-)(\d{4})$/;
		var dtFromArray = invdate.match(rxDatePattern);
		dtFromDay = dtFromArray[1];
		dtFromMonth= dtFromArray[3];	
		dtFromYear = dtFromArray[5];
		var invdate1 = dtFromMonth+"/"+dtFromDay+"/"+dtFromYear;
		var dtToArray = billdate.match(rxDatePattern);
		dtToDay = dtToArray[1];
		dtToMonth= dtToArray[3];	
		dtToYear = dtToArray[5];
		var billdate1 = dtToMonth+"/"+dtToDay+"/"+dtToYear;
		
		var Difference_In_Time = new Date(billdate1) - new Date(invdate1);
 
		// To calculate the no. of days between two dates
		var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24);
		if(Difference_In_Days >= 0){
			$('#termDays').val(Difference_In_Days);
		}else{
			$('#termDays').val('0');
			$('#duedate_div').val(dtFromDay+"/"+dtFromMonth+"/"+dtFromYear);
		}
		
	}
	function ewyaddr(){
		var content ="";
		content += '<input type="hidden" class="ewayaddrfields" name="buyerDtls.gstin" id="ewybuyerDtls_gstin"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.lglNm" id="ewybuyerDtls_lglNm"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.addr1" id="ewybuyerDtls_addr1"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.addr2" id="ewybuyerDtls_addr2"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.loc" id="ewybuyerDtls_loc"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.pin" id="ewybuyerDtls_pin"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.state" id="ewybuyerDtls_state"/><input type="hidden" class="ewayaddrfields" name="buyerDtls.pos" id="ewybuyerDtls_pos"/>';
		 
		content +='<input type="hidden" class="ewayaddrfields" name="dispatcherDtls.nm" id="ewydispatcherDtls_nm"/><input type="hidden" class="ewayaddrfields" name="dispatcherDtls.addr1" id="ewydispatcherDtls_addr1"/><input type="hidden" class="ewayaddrfields" name="dispatcherDtls.addr2" id="ewydispatcherDtls_addr2"/><input type="hidden" class="ewayaddrfields" name="dispatcherDtls.loc" id="ewydispatcherDtls_loc"/><input type="hidden" class="ewayaddrfields" name="dispatcherDtls.pin" id="ewydispatcherDtls_pin"/><input type="hidden" class="ewayaddrfields" name="dispatcherDtls.stcd" id="ewydispatcherDtls_stcd"/>';
		
		content +='<input type="hidden" class="ewayaddrfields" name="shipmentDtls.gstin" id="ewyshipmentDtls_gstin"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.trdNm" id="ewyshipmentDtls_trdNm"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.lglNm" id="ewyshipmentDtls_lglNm"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.addr1" id="ewyshipmentDtls_addr1"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.addr2" id="ewyshipmentDtls_addr2"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.loc" id="ewyshipmentDtls_loc"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.pin" id="ewyshipmentDtls_pin"/><input type="hidden" class="ewayaddrfields" name="shipmentDtls.stcd" id="ewyshipmentDtls_stcd"/>';
		$('.ewayaddrfieldss').html(content);
	}
</script>
<script type="text/javascript" src="${contextPath}/static/mastergst/js/common/jquery.datetimepicker.full.min.js"></script>
<script src="${contextPath}/static/mastergst/js/client/inv_popup_latest.js" type="text/javascript"></script>