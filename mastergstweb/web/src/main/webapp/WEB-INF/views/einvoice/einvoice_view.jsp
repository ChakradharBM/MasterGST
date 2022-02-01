<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST - GST Billing Software | E-Invoice</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/invoice/einvoices.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
</head>
<body class="body-cls">
  <%@include file="/WEB-INF/views/includes/client_header.jsp" %>
  <div class="breadcrumbwrap nav-bread">
	<div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item active">E-INVOICE</li>
					</ol>
					<span class="datetimetxt"><input type="text" class="form-control" id="datetimepicker" /><i class="fa fa-sort-desc"></i></span>
					<span class="f-14-b pull-right mt-1 font-weight-bold">Return Period:</span>
					<span class="dropdown chooseteam">
					  <span  <c:if test = "${returntype eq varGSTR1 || returntype eq varGSTR5 || returntype eq varGSTR6}">class="dropdown-toggle" data-toggle="dropdown"</c:if> id="fillingoption"><b>Filing Option:</b> <span id="filing_option"><c:if test='${not empty client}'><c:if test='${not empty client.filingOption}'> <c:choose><c:when test="${returntype eq varPurchase || returntype eq varGSTR2 || returntype eq 'GSTR2A'}">Monthly</c:when><c:otherwise>${client.filingOption}</c:otherwise></c:choose></c:if> <c:if test='${empty client.filingOption}'>None</c:if></c:if></span></span>
					  <div class="dropdown-menu" style="WIDTH: 108px!important;min-width: 36px;left: 52%;">
						<a class="dropdown-item" href="#" onClick="updateFilingOption('<%=MasterGSTConstants.FILING_OPTION_MONTHLY%>')"><%=MasterGSTConstants.FILING_OPTION_MONTHLY%></a>
						<a class="dropdown-item" href="#" onClick="updateFilingOption('<%=MasterGSTConstants.FILING_OPTION_QUARTERLY%>')"><%=MasterGSTConstants.FILING_OPTION_QUARTERLY%></a>
					  </div>
					</span>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
	
	<div class="db-ca-wrap db-ca-gst-wrap">
            <div class="container">
	                 <div class="row">
	                    <div class="col-md-12 col-sm-12">
								<div class="gstr-info-tabs">		
								<ul class="nav nav-tabs" role="tablist" style="max-width:85%">
								 <li class="nav-item">
								    <a class="nav-link einvoice_tab" data-toggle="tab" href="#gtab1" role="tab"><span class="serial-num">1</span>E-INVOICES </a>
								 </li>
								</ul>
								
									<div class="tab-content">
												<div class="tab-pane active" id="gtab1" role="tabpane1">
												<div class="tab-pane" id="gtab1" role="tabpane1">
														<jsp:include page="/WEB-INF/views/einvoice/einvoice_tab.jsp">
															<jsp:param name="id" value="${id}" />
															<jsp:param name="fullname" value="${fullname}" />
															<jsp:param name="usertype" value="${usertype}" />
															<jsp:param name="returntype" value="<%=MasterGSTConstants.EINVOICE%>" />
															<jsp:param name="client" value="${client}" />
															<jsp:param name="contextPath" value="${contextPath}" />
															<jsp:param name="month" value="${month}" />
															<jsp:param name="year" value="${year}" />
														</jsp:include>
													</div>
												<div id="invview_Process" class="invview_Process d-none"  style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 46%;">
													<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
												</div>
												<div class="customtable db-ca-view">
													 <table id="dbTableEINVOICE" class="row-border dataTable meterialform" cellspacing="0" width="100%">
														<thead><tr><th class="text-center"><div class="checkbox"><label><input type="checkbox" id='checkEINVOICE' onClick="updateAllEinvSelection('EINVOICE','${client.status}',this)"/><i class="helper"></i></label></div> </th><th class="text-center">IRN No</th><th class="text-center">IRN Status</th><th class="text-center" style="width:100px;max-width:100px;">Type</th><th class="text-center">Invoice No</th><th class="text-center">Invoice Date</th><th class="text-center">GSTIN</th><th class="text-center" style="width: 150px;max-width: 150px;">Customers</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th><th class="text-center">Total Amt</th><th class="text-center">Action</th></tr></thead>
														<tbody id="dbEinvoiceTable_body">
														
														</tbody>
													</table>
											</div>
												
											</div>
									</div>
								
								</div>
						</div>
				</div>
		</div>
	</div>
	
	<!-- Cancel IRN popup Start -->
	<div class="modal fade" id="cancelEinvoiceModal" tabindex="-1" role="dialog" aria-labelledby="cancelModal" data-backdrop="static"
data-keyboard="false" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" aria-label="Close" onclick="closeIRNmodal('cancelEinvoiceModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Cancel IRN </h3>
        </div>
        <form:form data-toggle="validator" id="cancelirn_form" class="meterialform">
        <div class="clearfix pl-4 pt-4 pr-4">
        <div class="form-group row">
         <label class="label-txt col-md-6 col-sm-12 mt-2 astrich">Cancel Remark</label><span class="colon mt-2" style="margin-left:-78px;">:</span>
		 <select class="form-control col-md-6 col-sm-12 ml-2 einvedit" id="ecancelremark" name="ecancelRmrk" onchange="ecancelCodeSelection(1)" data-error="Please select reason" required style="border:1px solid lightgray;">
         <option value="">-Select-</option>
          <option value="Wrong Entry">Wrong Entry</option> 
         <!--  <option value="Order Cancelled">Order Cancelled</option>
           <option value="Data Entry mistake">Data Entry mistake</option>
            <option value="Others">Others</option> -->
         </select>
         
		 <span class="control-label"></span>
		 <div class="help-block with-errors"></div>
		</div> 
		
		
         <span id="ecancel_error" class="ecancel_error" style="font-size:12px;color:red;"></span>
        </div>
      
      <div class="modal-footer">
        <button type="button" class="btn btn-blue-dark" id="btn_eCancel" style="padding:7px 12px;">Cancel IRN</button>
        <button type="button" class="btn btn-blue-dark" onclick="closeIRNmodal('cancelEinvoiceModal')"  style="padding:7px 12px;">Don't Cancel</button>
      </div>
	  <input type="hidden" class="ecancelcode" id="ecancelcode" name="ecancelcode">
      </form:form>
      </div>
    </div>
  </div>
</div>
	<!-- Cancel IRN popup End -->
	<!-- Send Email Modal Start  -->
	<div class="modal fade" id="send_emailModal" role="dialog" aria-labelledby="send_emailModal" aria-hidden="true">
		<div class="modal-dialog modal-md modal-right" role="document" style="min-width:700px">
			<div class="modal-content">
				<div class="modal-header p-0">
					<button type="button" class="close" onclick="closeSendMailmodal('send_emailModal')"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close" /></span> </button>
					<div class="bluehdr" style="width:100%">
						<h3>Send Email</h3> </div>
				</div>
				<div class="modal-body popupright bs-fancy-checks">
					<div class="p-3 row gstr-info-tabs">
						<ul class="nav nav-tabs col-md-12 pl-3" role="tablist" id="einvemail_tabs">
							<li class="nav-item"><a class="nav-link active" id="einvemail" data-toggle="tab" href="#einvemail_snd" role="tab">Email</a></li>
							<li class="nav-item"><a class="nav-link " id="einvemail_preview" data-toggle="tab" href="#einvemail_preview_mode" role="tab" onclick="email_einvPreview('email')">Preview</a></li>
						</ul>
						<div class="tab-content col-md-12 mb-3 mt-2 pl-0 pr-0">
							<div class="tab-pane active col-md-12" id="einvemail_snd" role="tabpane1">
								<div class="form-group einvsuccessEmailmsg d-none">
									<h6><i class="fa fa-check" style="font-size:32px;color:green"></i><span id="einvsuccessEmailmsg" class="text-success" style="font-weight:bold;color:green;"></span></h6></div>
								<div class="form-group col-md-11 mt-1 f-12">
									<span id="sendEmailEinvErrorMsg" style="color:red;float: right;margin-bottom: -1px;"></span>
								</div>
								<div class="col-md-11 mb-1 f-12" style="color:blue;text-align: right;">You can Enter List of mail id's by Comma Separated Values</div>
								
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="client_name" class="col-md-3">Customer Name<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="einvcustomer_name" style="display: inline-block;"> </div>
								
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="customeremailid" class="col-md-3">Email id<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="einvcustomer_emailid" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="clientemailids" class="col-md-3">CC <span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="form-control col-md-8" id="einvcustomer_emailids" style="display: inline-block;"> </div>
								<div class="form-group col-md-12 mb-1 pr-0">
									<label for="Subject" class="col-md-3">Subject<span class="coln-txt" style="float:right;">:</span></label>
									<input type="text" class="email_subject form-control col-md-8" style="display: inline-block;" id="einvemail_subject"> </div>
								<div class="form-group mt-3 col-md-12 mb-1 pr-0">
									<label for="Meassage" class="col-md-4">Message :</label>
									<textarea class="form-control einvemail_meassage col-md-11" id="einvemail_meassage" style="width:90%;height:110px;margin-left: 15px;" onkeyup=""> </textarea>
								</div>
								
								<div class="form-group mt-3 ml-3 col-md-12 mb-1 pr-0 einvemailSigncheck">
									<a href="#" class="einvsignLink" style="font-size:14px;">Add Email Signature</a>
									<div class="meterialform mr-2 mt-1" style="float:left">
										<div class="checkbox pull-right" id="einvEmailSignatureCheck" style="margin-top:-2px;">
											<label>
												<input class="einvaddEmailSignatureDetails" type="checkbox" name="isEmailSignatureDetails" onchange="signatureChange()" checked> <i class="helper"></i><strong>Include Signature In Mail</strong></label>
										</div>
									</div>
									<div id="einvemail_custdetails"  style="width:700px"> </div>
								</div>
								
							</div>
							<div class="tab-pane col-md-12 mt-0" id="einvemail_preview_mode" role="tabpane2">
								<div style="border:1px solid lightgray;border-radius:5px;">
									<div class="row  p-2">
										<div class="form-group col-md-12 col-sm-12">
											<h6>Dear <span id="einvemail_cust_name"></span>,</h6> </div>
										<div class="form-group col-md-12 col-sm-12">
											<div id="einvpreview_email"></div>
										</div>
									</div>
									<div class="p-2">
										<p class="mb-0">Thanks ,</p>
										<div id="einvpreview_custname"> </div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<input type="hidden" id="einvemail_invoiceId">
				<input type="hidden" id="einvemail_returnType">
				<div class="modal-footer">
					<button type="button" class="btn btn-blue-dark" id="seneinvEmailBtn" onclick="sendeinvMails()">Send</button>
					<button type="button" class="btn btn-blue-dark" onclick="closeSendMailmodal('send_emailModal')">Close</button>
				</div>
			</div>
		</div>
	</div>
<!-- Send Email Modal End  -->
	<script>
	var ecustEmailids=new Array(),ecustCCEmailids=new Array();
	var irnCancelArray=new Array();var esubscriptionchecks;
	var totalTaxableValue = 0, totalIGST = 0, totalCGST = 0, totalSGST = 0, totalCESS = 0,  totalITCIGST = 0, totalITCCGST = 0, totalITCSGST = 0, totalITCCESS = 0,totalTax = 0, totalITC = 0, totalValue = 0, totalInvoices = 0, totalUploaded = 0, totalPending = 0, totalFailed = 0, totalExemptedValue = 0;var NotInJournals = 'NotInJournals';
	var filingOption = '<c:out value="${client.filingOption}"/>';
	var rType='<c:out value="${returntype}"/>'; var invoiceStatus='<c:out value="${type}"/>';var otherreturnType='<c:out value="${otherreturnType}"/>';
	$(function () {
		$('.einvoice_tab,#ideinvoice').addClass('active');
		
	});
	function updateReturnPeriod(eDate) {
		var month = eDate.getMonth()+1;	var year = eDate.getFullYear();
		window.location.href = '${contextPath}/einvoice/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${returntype}"/>/<c:out value="${client.id}"/>/'+month+'/'+year;
	}
	
	</script>
	<script src="${contextPath}/static/mastergst/js/client/alliviews.js" type="text/javascript"></script>
	<jsp:include page="/WEB-INF/views/includes/footer.jsp" />
</body>
</html>