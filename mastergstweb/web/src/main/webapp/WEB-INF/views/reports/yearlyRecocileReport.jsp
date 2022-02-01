<%@include file="/WEB-INF/views/includes/taglib.jsp" %>
<c:set var="varRetType" value="<%=MasterGSTConstants.PURCHASE_REGISTER%>"/>
<c:set var="varRetTypeCode" value='${varRetType.replaceAll(" ", "_")}'/>
<!DOCTYPE html>
<html lang="en" xmlns="http://www.w3.org/1999/xhtml" xmlns:fb="http://ogp.me/ns/fb#">
<head>
<title>MasterGST | Purchase Register & GTSR2A yearly Reconciliation Report</title>
<%@include file="/WEB-INF/views/includes/dashboard_script.jsp" %>
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/login/login.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-tagsinput.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/bootstrap/bootstrap-multiselect.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/common/datetimepicker.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/reports/multimonth_reports.css" media="all" />
<link rel="stylesheet" href="${contextPath}/static/mastergst/css/dashboard-ca/all-invoice-views.css" media="all" />
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-tagsinput.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/bootstrap/bootstrap-multiselect.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/jquery/jquery.form.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/client/currencyFormatter.js" type="text/javascript"></script>
<script src="${contextPath}/static/mastergst/js/datatable/buttons.flash.min.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.html5.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/buttons.print.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/dataTables.buttons.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/jszip.js"></script>
<script	src="${contextPath}/static/mastergst/js/datatable/pdfmake.js"></script>
<script src="${contextPath}/static/mastergst/js/datatable/vfs_fonts.js"></script>
<script src="${contextPath}/static/mastergst/js/common/dataTables.fixedColumns.min.js"></script>
<script src="${contextPath}/static/mastergst/js/reports/yearlyReconsileReport.js" type="text/javascript"></script>
<style>
.dropdown:hover .dropdown-content.reportSummary{display: block;}.arrow-up {width: 0; height: 14px; border-left: 9px solid transparent;border-right: 9px solid transparent;border-bottom: 12px solid white; position: absolute;top: -8px;}.dropdown {position: relative;display: inline-block;}.dropdown-content.reportSummary{display: none;margin-top: 20px;position: absolute;background-color: white;min-width: 400px; box-shadow: 0px 8px 16px 0px rgba(0,0,0,0.2);z-index: 1;color: black;padding: 12px 16px;text-decoration: none;margin-left: -13px; text-transform: capitalize;}.helpbtn.dropdown:hover .dropdown-content {display: block;}
.reportmenu:hover .dropdown-menu#reportdrop{display:block}
button#monthlydwnldxls ,#yearlydwnldxls,#customdwnldxls{margin-left: 0px;height: 30px;box-shadow:none;}
.months_na li{width:85px;text-align:center;border-right:1px solid #f0f2f3}
.months_na li:nth-child(9) , .months_na li:nth-child(10) ,.months_na li:nth-child(11) , .months_na li:nth-child(12){width:86px}
.months_na{background-color:white;width:100%;font-size:13px;list-style:none;display: inline-flex;padding-left: 0px;margin: 0px;padding-top: 6px;margin-left: 1px;}
#yearProcess{top:370px!important;}
#yearreconciletitle{text-transform: none;}
.reconciletablee #dbTable3_length{margin-left:0px!important}
</style>
<script type="text/javascript">
var mnths=['Jan','Feb','Mar','Apr','May','June','July','Aug','Sept','Oct','Nov','Dec'];
function formatInvoiceDate(date){
	var invDate = new Date(date);
	var day = invDate.getDate() + "";
	var month = (invDate.getMonth() + 1) + "";
	var year = invDate.getFullYear() + "";
	day = checkZero(day);
	month = checkZero(month);
	year = checkZero(year);
	return day + "-" + mnths[month-1] + "-" + year;
}
function checkZero(data){
	if(data.length == 1){
		data = "0" + data;
  	}
	return data;
}
</script>
</head>
<body class="body-cls">
<%@include file="/WEB-INF/views/includes/client_header.jsp" %>
<jsp:include page="/WEB-INF/views/client/otpverification.jsp"/>
  <div class="breadcrumbwrap">
  <div class="container">
		<div class="row">
			<div class="col-md-12 col-sm-12">
					<ol class="breadcrumb">
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/cdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>"/><c:choose><c:when test="${usertype eq userCA || usertype eq userTaxP || usertype eq userCenter}">Clients</c:when><c:otherwise>Business</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/ccdb/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>?type=change"><c:choose><c:when test='${fn:length(client.businessname) > 50}'>${fn:substring(client.businessname, 0, 50)}..</c:when><c:otherwise>${client.businessname}</c:otherwise></c:choose></a></li>
						<li class="breadcrumb-item"><a href="#" class="urllink" link="${contextPath}/dreports/<c:out value="${id}"/>/<c:out value="${fullname}"/>/<c:out value="${usertype}"/>/<c:out value="${client.id}"/>">Reports</a></li>
						<li class="breadcrumb-item active">Purchase Register & GTSR2A yearly Reconciliation Report</li>
					</ol>
					<div class="retresp"></div>
				</div>
			</div>
		</div>
	</div>
  <div class="db-ca-wrap yearly1">
		<div class="container">
			<div class=" "></div>
			<a href="${contextPath}/dreports/${id}/${fullname}/${usertype}/${client.id}/${month}/${year}" class="btn btn-blue-dark pull-right" role="button" style="padding: 4px 25px;">Back</a>
			<h2>Purchase Register & GTSR2A yearly Reconciliation Report</h2><p>Financial year period invoices of Purchase Register.<br>Financial year starting to till now records of GSTR2A invoices.</p>
			<div class="helpguide reporthelpguide dropdown helpicon" data-toggle="modal" data-target="#reporthelpGuideModal" style="display:flex;float:left;margin-top:0px;"> Help To Read This Report
			<div class="dropdown-content reportSummary" style="right:unset"> <span class="arrow-up"></span><span class="pl-2">Financial year period invoices of Purchase Register.</span><br><span class="pl-2">Financial year starting to till now records of GSTR2A invoices.</span></div>
			</div><span class="helpbtn" style=""><i class="fa fa-info-circle dropdown helpicon" style="margin-left: 4px;font-size:20px;color: #6b5b95;"></i></span>
			
			<div class="dropdown chooseteam mr-0" style="height: 32px">
			<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption" style="margin-right: 10px; display: inline-flex;">
				<label>Report Type:</label>
					<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; margin-left: 12px; min-width: 104px;">
						<span id="filing_option1" class="filing_option"	style="vertical-align: bottom">Yearly</span>
						<span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -7px; left: 9px;">
							<i class="fa fa-sort-desc" style="vertical-align: super;"></i> 
						</span>
					</div>
				</span>
				<div class="dropdown-menu ret-type" style="WIDTH: 108px !important; min-width: 36px; left: 19%; top: 26px"> 
					<a class="dropdown-item" href="#" value="Yearly" onClick="getval('Yearly')">Yearly</a>
					<!-- <a class="dropdown-item" href="#" value="Custom" onClick="getval('Custom')">Custom</a> -->
				</div> 
				<span style="display: inline-block;margin-bottom: 4px;" class="yearly-sp">
					<span class="dropdown-toggle yearly" data-toggle="dropdown"	id="fillingoption1"	style="margin-right: 10px; display: inline-flex;">
						<label id="ret-period" style="margin-bottom: 3px; margin-top: 2px;">Financial Year:</label>
						<div class="typ-ret" style="border: 1px solid; border-radius: 2px; padding: 2px; background-color: white; padding-right: 14px; height: 27px; align-items: top; min-width: 104px; max-width: 104px;">
							<span style="vertical-align: top; margin-left: 3px;" id="yearlyoption1" class="yearlyoption">2021 - 2022</span><span class="input-group-addon add-on pull-right" style="display: inline-block; padding: 0px !important; border: none !important; background-color: unset !important; font-size: 1.5rem; position: relative; top: -30px; left: 9px;">
								<i class="fa fa-sort-desc" style="vertical-align: super; margin-left: 6px;" id="date-drop"></i>
							</span>
						</div></Span>
					<div class="dropdown-menu ret-type1" id="financialYear1" style="WIDTH: 108px !important; min-width: 36px; left: 61%; top: 26px; border-radius: 2px">
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2021-2022')">2021 - 2022</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2020-2021')">2020 - 2021</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2019-2020')">2019 - 2020</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2018-2019')">2018 - 2019</a>
							<a class="dropdown-item" href="#" onClick="updateYearlyOption('2017-2018')">2017 - 2018</a>
					</div>
					<a href="#" class="btn btn-greendark  pull-right" role="button"	style="padding: 4px 10px;; text-transform: uppercase;" onClick="getdiv()">Generate</a>
				</span>
			</div>
			<div class=""></div>
			
			<div id="yearProcess" class="text-center"></div>
			<div style="margin-top:30px">
			<h4 class="hdrtitle" id="yearreconciletitle">
			
			<span class="summary_retperiod">
				<a href="#" data-toggle="modal" data-target="#reconcilemodal" style="color: #404144;text-decoration: underline;">Reconcile Summary</a></span> of FY <span id="retperiod"></span><span style="font-size:13px; color:#0dce2f; font-weight:bold;margin-left:3px"> <c:if test="${empty client.reconcileDate }"><span style="color:red;font-size:13px">You Haven't Reconciled Yet,</span></c:if><c:if test="${not empty client.reconcileDate }">Last Reconciled on: ${client.reconcileDate}</c:if> </span> 
				<c:if test="${reconcileCounts eq null}">
					<a href="#" onClick="reconcileYearlyGstr2a()" class="btn btn-blue reconcile_btn" style="color: #404144;;margin-left:10px;font-size: 12px;float: right; position: absolute; padding: 2px 6px 0px;">Reconcile Now</a>
				</c:if>
				<c:if test="${not empty reconcileCounts}">
				<!-- style="margin-left: 116px;" -->
					<a href="#" onClick="reconcileYearlyGstr2a()" class="btn btn-blue reconcile_btn d-none" style="color: #404144;;margin-left:10px;font-size: 12px;float: right; position: absolute; padding: 2px 6px 0px;">Reconcile Now</a>
					<span style="font-size: 14px;" class ="rccn">Reconciliation is in Process, <img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verifyReconsilation('${client.id}')">click here </a>for status</span>
				</c:if>
				<span style="font-size: 14px;" class="processverification d-none">Reconciliation is in Process, <c:out value="${reconcileCounts}"/>  <img src="${contextPath}/static/mastergst/images/master/gears.gif" width="40px" style="margin-left:6px;margin-right:6px;"/> <a href="#" onclick="verifyReconsilation('${client.id}')">click here </a>for status</span>
			
			<div class="dropdown" style="color: #404144;;margin-left:20px;font-size: 17px;float:right">Reconcile Rules<div class="dropdown-content" style="z-index:3;line-height: 2;"><span class="arrow-right"></span><div>Allowed Tax/Invoice Amount Difference for Reconcile<span class="colon" style="margin-left: 17px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.reconcileDiff}">${clientConfig.reconcileDiff}  </c:when><c:otherwise>0.00</c:otherwise></c:choose> Rupees</span></br>Allowed days for Invoice Date Difference for Reconcile<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.allowedDays}"><fmt:formatNumber type="number" pattern="###" value="${clientConfig.allowedDays}" />  </c:when><c:otherwise>0</c:otherwise></c:choose> Days</span>
			</br>Considers as Matched if Invoice Number matches anywhere in the Invoice Number<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableInvoiceMatch}"><c:if test="${clientConfig.enableInvoiceMatch eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableInvoiceMatch eq false}"><span style="color:red">No</span></c:if> </c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "/"(Backward Slash)<span class="colon" style="margin-left: 10px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreSlash}"><c:if test="${clientConfig.enableIgnoreSlash eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreSlash eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "-"(Hyphen)<span class="colon" style="margin-left: 51px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreHyphen}"><c:if test="${clientConfig.enableIgnoreHyphen eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreHyphen eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "0"(Zero/O)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreZero}"><c:if test="${clientConfig.enableIgnoreZero eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreZero eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "I"(Capital i)<span class="colon" style="margin-left: 50px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreI}"><c:if test="${clientConfig.enableIgnoreI eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreI eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			</br>Ignore "l"(Small l)<span class="colon" style="margin-left: 53px;margin-right: 3px;">:</span><span><c:choose><c:when test="${not empty clientConfig.enableIgnoreL}"><c:if test="${clientConfig.enableIgnoreL eq true}"><span style="color:green">Yes</span></c:if><c:if test="${clientConfig.enableIgnoreL eq false}"><span style="color:red">No</span></c:if></c:when><c:otherwise><span style="color:green">Yes</span></c:otherwise></c:choose></span>
			<a href="#" onClick="moreReconcileConfig()" >Click here for more configurations....</a></div></div></div> 
			</h4>			
	<%-- <jsp:include page="/WEB-INF/views/client/mismatchfilter.jsp" /> --%>
	<div class="normaltable meterialform">
		<div class="filter">
			<div class="noramltable-row">
				<div class="noramltable-row-hdr">Filter</div>
				<div class="noramltable-row-desc">
				<div class="mismatchfilter">
					<span id="divMMFilters"></span>
					<span class="btn-remove-tag" onclick="clearMMFilters()">Clear All<span data-role="remove"></span></span>
				</div>
				</div>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Search &nbsp; Filter</div>
			<div class="noramltable-row-desc">
				<select id="MMmultiselect1" class="multiselect-ui form-control" multiple="multiple">
					<option value="Mismatched">Mismatched</option>
					<option value="Matched">Matched</option>
					<option value="Round Off Matched">Round Off Matched</option>
					<option value="Manual Matched">Manual Matched</option>
					<option value="Matched In Other Months">Matched In Other Months</option>
					<option value="Probable Matched">Probable Matched</option>
					<option value="Invoice No Mismatched">Invoice No Mismatched</option>
					<option value="Invoice Value Mismatched">Invoice Value Mismatched</option>
					<option value="Invoice Date Mismatched">Invoice Date Mismatched</option>
					<option value="GST No Mismatched">GST No Mismatched</option>
					<option value="Tax Mismatched">Tax Mismatched</option>
					<option value="Not In Purchases">Not In Purchases</option>
					<option value="Not In GSTR 2A">Not In GSTR 2A</option>
				</select>
				<select id="MMmultiselect2" class="multiselect-ui form-control" multiple="multiple">
					<option value="B2B">B2B Invoices</option>
					<option value="B2BA">B2BA Invoices</option>
					<option value="Debit Note">Debit Note</option>
					<option value="Credit Note">Credit Note</option>
					<!--<option value="Credit/Debit Notes">Credit/Debit Notes</option>-->
					<option value="CDNA">CDNA</option>
					<option value="<%=MasterGSTConstants.IMP_GOODS%>"><%=MasterGSTConstants.IMP_GOODS%></option>
					<option value="<%=MasterGSTConstants.IMP_SERVICES%>"><%=MasterGSTConstants.IMP_SERVICES%></option>
					<option value="<%=MasterGSTConstants.ISD%>">ISD</option>
				</select>
				<span class="multiselectuserlist">
					<select id="MMmultiselect3" class="multiselect-ui form-control" multiple="multiple">
					</select>
				</span>
				<span class="missmultiselectuserlist">
				<select id="MMmultiselect4" class="multiselect-ui form-control" multiple="multiple">
				</select>
				</span>
				<select id="MMmultiselect5" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.branches}" var="branch">
						<option value="${branch.name}">${branch.name}</option>
					</c:forEach>
				</select>
				<select id="MMmultiselect6" class="multiselect-ui form-control" multiple="multiple">
					<c:forEach items="${client.verticals}" var="vertical">
						<option value="${vertical.name}">${vertical.name}</option>
					</c:forEach>
				</select>
			</div>
		</div>
		<div class="noramltable-row">
			<div class="noramltable-row-hdr">Filter Summary</div>
			<div class="noramltable-row-desc">
				<div class="normaltable-col hdr">Total Invoices
					<div class="normaltable-col-txt" id="idMMCount"> </div>
				</div>
				<div class="normaltable-col hdr">Total Taxable Value
					<div class="normaltable-col-txt" id="idMMTaxableVal"></div>
				</div>
				<div class="normaltable-col hdr">Total Tax Value
					<div class="normaltable-col-txt" id="idMMTaxVal"></div>
				</div>
				<div class="normaltable-col hdr">Total IGST
					<div class="normaltable-col-txt" id="idMMIGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CGST
					<div class="normaltable-col-txt" id="idMMCGST"></div>
				</div>
				<div class="normaltable-col hdr">Total SGST
					<div class="normaltable-col-txt" id="idMMSGST"></div>
				</div>
				<div class="normaltable-col hdr">Total CESS
					<div class="normaltable-col-txt" id="idMMCESS"></div>
				</div>
			</div>
		</div>
	</div>
	
	<h4 class="hdrtitle"style="position: relative;margin-top: -15px!important; z-index:2;"><!-- <a href="#" id="btnMisMatchReject" class="btn btn-red disable" onClick="updateMismatchDataNew(this,false)">Reject GSTR2A invoice(s)</a> --><a href="#" id="btnMisMatchAccept" class="btn btn-acceptgreen disable permissionGSTR2-Accept_GSTR2A" onClick="updateMismatchDataNew(this,true)">Accept GSTR2A invoice(s)</a>
	<div class="dropdown pull-right permissionExcel_Download_In_Books_And_Returns-Reconcile ml-2" style="margin-left: 10px;"><div class="split-button-menu-dropdown reportmenu"><button class="btn btn-blue b-split-right b-r-cta b-m-super-subtle" id="yearlydwnldxls" data-toggle="dropdown" style="border-left: solid 1px #435a93;border-bottom-left-radius: 0px;border-top-left-radius: 0px;" ><span class="showarrow"> <i class="fa fa-caret-down"></i></span></button><button class="btn btn-blue reportmenu" id="yearlydwnldxls" data-toggle="dropdown" aria-haspopup="true" style="box-shadow:none;text-align:left" aria-expanded="false">DOWNLOAD TO EXCEL<i class="fa fa-download" aria-hidden="true" style="margin-left: 6px;"></i></button><div class="dropdown-menu reportdrop" id="reportdrop" aria-labelledby="monthlydwnldxls" style="width: 219px!important;"><a class="dropdown-item" id="downloadExcelLink">INVOICE WISE DOWNLOAD</a><a class="dropdown-item" id="downloadExcelLinkAlldetails">All DETAILS DOWNLOAD</a></div></div></div>
	<a href="#" class="btn btn-blue sendmessage disable" data-toggle="modal" data-target="#sendMessageModal">Send Message</a> <a href="#" class="btn btn-blue reconModal mannualMatching disable" data-toggle="modal" data-target="#reconModal" onclick="mannualMatchingInv('${client.id}','yearly')">Manual Match</a></h4>
    <div class="processing-img d-none" style="color:red;font-size:20px;position:absolute;z-index:99;top:37%;left: 45%;">
    	<img src="${contextPath}/static/mastergst/images/eclipse-spinner.gif" alt="spinner-img" style="width: 150px;height: 150px;"/>
    </div>
    <div class="customtable db-ca-view tabtable2 reconciletablee">
					<div class="customtable db-ca-view" style="margin-top: 65px;">
					<span class="mt-1 select_msg" style="position:absolute;left:60%;color:#5769bb;"></span>
						<table id="reconsileDbTable" class="row-border dataTable meterialform" cellspacing="0" width="100%">
							<thead>
								<tr>
									<th class="text-center"><div class="checkbox"> <label><input type="checkbox" id="checkMismatch" onClick="updateMainMisMatchSelection(this)"/><i class="helper"></i></label></div> </th>
									<th> </th>
									<th> Type</th><th class="text-center">Suppliers</th><th>Ret.Period</th>
									<th class="text-center">Invoice Number</th><th class="text-center">Invoice Date</th><th class="text-center">GSTN No</th><th class="text-center">Invoice Value</th><th class="text-center">Taxable Amt</th><th class="text-center">Total Tax</th>
									<th class="text-center">Status</th>
									<th class="text-center">Notes</th>
								</tr>
							</thead>
							<tbody id="idMisMatchBody"></tbody>
						</table>
					</div>
					</div>
				</div>
		</div>
		</div>
		<div class="modal fade" id="reporthelpGuideModal" tabindex="-1" role="dialog" aria-labelledby="reporthelpGuideModal" aria-hidden="true">
  <div class="modal-dialog modal-md modal-right" role="document">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr"><h3>Help To Read This Report </h3></div>
        <div class=" p-2 steptext-wrap"><span class="pl-2">Financial year period invoices of Purchase Register.</span><br><span class="pl-2">Financial year starting to till now records of GSTR2A invoices.</span> </div>
      </div>
      <div class="modal-footer">

        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<div class="modal fade" id="reconcileprocessModal" tabindex="-1" role="dialog" aria-labelledby="reconcileprocessModal" aria-hidden="true">
  <div class="modal-dialog col-6 modal-center" role="document" style="max-width: unset; width: 50%;">
    <div class="modal-content">
      <div class="modal-body">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true"> <img src="${contextPath}/static/mastergst/images/master/closeicon-blue.png" alt="Close"></span> </button>
        <div class="invoice-hdr bluehdr">
          <h3>Reconciliation Status</h3>
        </div>
        <div class=" pl-4 pt-4 pr-4">
        	<div class="reconprogress">
        	<p><span id="verificationType"></span>&nbsp;Reconciliation is in Process, Initiated by :&nbsp; <span id="reconinitiatedby"></span></p>
        	<h5>Reconciliation Status</h5>
          <p><span id="gstr2areconrtntype"></span> : <span id="processInfo" style="font-weight:bold"></span> invoices out of <span id="verificationInfo"  style="font-weight:bold"></span></p>
          <p class="prinfo" style="display:none;">Purchase Register : <span id="processPrInfo" style="font-weight:bold"></span> invoices out of <span id="verificationPrInfo"  style="font-weight:bold"></span></p>
          
          </div>
          <div class="reconcompleted">
          	<p><span id="reconcomplete"></span></p>
          </div>
        </div>
      </div>
      <div class="modal-footer text-center" style="display:block">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Close</button>
      </div>
    </div>
  </div>
</div>
<jsp:include page="/WEB-INF/views/client/alliview_modal.jsp" />
	<!-- footer begin here -->
    <%@include file="/WEB-INF/views/includes/footer.jsp" %>
    <!-- footer end here -->
<script type="text/javascript">
$(function() {
	var clientname = '<c:out value="${client.businessname}"/>';
	var date = new Date();
	var cYear = date.getFullYear();
	var nYear = date.getFullYear();
	var financialyear = cYear +"-"+nYear;
	var fp = financialyear.split("-");
	var yearval = "";
	if(${month} < 4){var nxtyear = ${year};var yearval = ${year}-1+"-"+nxtyear;}else{var nxtyear = ${year}+1;var yearval = ${year}+"-"+nxtyear;}
	document.getElementById('yearlyoption1').innerHTML=yearval;
	$('#retperiod').text(yearval);
	var yr =$('.yearlyoption').text().split("-");
	loadReportsUsersByClient('${id}', '${client.id}', loadReportsUsersInDropDown);
	loadReportsInvoiceSupport('${id}', '${client.id}', parseInt(yr[0].trim()), loadReportsCustomersInDropdown);
	loadReportsInvTable('${id}', '${client.id}', parseInt(yr[0].trim()));
	initiateCallBacksForMultiSelectReports();
	initializeRemoveAppliedFiltersReports();
	loadReconcileSummary('${id}', '${client.id}', parseInt(yr[0].trim()));
	var dwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+parseInt(yr[0].trim())+"?dwnldtype=invoicewise";
	var alldetailsdwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+parseInt(yr[0].trim())+"?dwnldtype=alldetailswise";
	$('#downloadExcelLink').attr('href',dwnldExcel);
	$('#downloadExcelLinkAlldetails').attr('href',alldetailsdwnldExcel);
	var rc = "${reconcileCounts}";
	if(rc != null && rc != ""){
		executeQuery('${client.id}');
	}
});
function updateYearlyOption(value){
	document.getElementById('yearlyoption1').innerHTML=value;
}
function getdiv() {
	var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
	$('#retperiod').text(fp);
	loadReportsUsersByClient('${id}', '${client.id}', loadReportsUsersInDropDown);
	loadReportsInvoiceSupport('${id}', '${client.id}', yrs, loadReportsCustomersInDropdown);
	loadReportsInvTable('${id}', '${client.id}', yrs);
	initiateCallBacksForMultiSelectReports();
	initializeRemoveAppliedFiltersReports();
	loadReconcileSummary('${id}', '${client.id}', yrs);
	var dwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+yrs+"?dwnldtype=invoicewise";
	var alldetailsdwnldExcel = "${contextPath}/downloadReconsileInvsExcel/${id}/${client.id}/${month}/"+yrs+"?dwnldtype=alldetailswise";
	$('#downloadExcelLink').attr('href',dwnldExcel);
	$('#downloadExcelLinkAlldetails').attr('href',alldetailsdwnldExcel);
}
function reconcileYearlyGstr2a(){
	var fp = $('.yearlyoption').text();var fpsplit = fp.split(' - ');var yrs = parseInt(fpsplit[0]);var yrs1 = parseInt(fpsplit[0])+1;
	$('.reconcile_btn').addClass("btn-loader");
	$('.reconcile_btn').html("Reconciling...");
	$.ajax({
		url : '${contextPath}/verifySubscription/${id}/${client.id}',
		async: false,
		cache: false,
		contentType: 'application/json',
		success : function(response) {
			$('.reconcile_btn').addClass("d-none");
			$('.processverification').removeClass("d-none");
			if(response == 'verified') {
				var urlStr = '${contextPath}/performReconcile/${id}/${fullname}/${usertype}/${client.id}/GSTR2A/4/'+yrs1;
				$.ajax({
					url: urlStr,
					async: true,
					cache: false,
					dataType:"json",
					contentType: 'application/json',
					success : function(response) {
						window.location.href = '${contextPath}/reports/${id}/${fullname}/${usertype}/${client.id}/4/'+yrs+'?type=yearlyRecocileReport';
					}
				});
				setTimeout(function() { executeQuery('${client.id}'); }, 2000);	
			}else{
				 $('.processverification,.rccn').addClass("d-none");
		    	  $('.reconcile_btn').removeClass("btn-loader");
		    	  $('.reconcile_btn').html("Reconcile");
		    	  $('.reconcile_btn').removeClass("d-none");
				errorNotification(response);
			}
		},error : function(error){
		}
	});	
}

var timer;
function executeQuery(id) {
	var fUrl = _getContextPath()+'/reconsilationcompleted/'+id;
	  $.ajax({
	    url: fUrl,
	    success: function(data) {
	      if(data == "Completed"){
	    	  $('.processverification,.rccn').addClass("d-none");
	    	  $('.reconcile_btn').removeClass("btn-loader");
	    	  $('.reconcile_btn').html("Reconcile Now");
	    	  $('.reconcile_btn').removeClass("d-none");
	    	  $('.prinfo').css("display","none");
	    	  clearTimeout(timer);
	    	  getdiv();
	      }
	    }
	  });
	  updateCall(id);
	}

	function updateCall(id){
		timer = setTimeout(function(){executeQuery(id)}, 1000);
	}

</script>
</body>
</html>